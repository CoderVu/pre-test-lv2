package org.example.pages;

import org.example.constants.TestConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.JavascriptExecutor;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.Comparator;

public class DataTablesPage extends BasePage {
    private Logger logger = Logger.getLogger(DataTablesPage.class.getName());

    // Locators for navigation
    @FindBy(linkText = "Sortable Data Tables")
    private WebElement sortableDataTablesLink;

    // Locators for first table
    @FindBy(id = "table1")
    private WebElement firstTable;

    @FindBy(css = "#table1 th.header")
    private List<WebElement> tableHeaders;

    @FindBy(css = "#table1 tbody tr")
    private List<WebElement> tableRows;

    @FindBy(css = "#table1 tbody tr td")
    private List<WebElement> tableCells;

    public DataTablesPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToMainPage() {
        driver.get(TestConstants.BASE_URL);
    }

    public void navigateToSortableDataTables() {
        navigateToMainPage();
        wait.until(ExpectedConditions.elementToBeClickable(sortableDataTablesLink));
        click(sortableDataTablesLink);
        wait.until(ExpectedConditions.visibilityOf(firstTable));
    }

    private void highlightElement(WebElement element, String color) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.backgroundColor = '" + color + "'", element);
            Thread.sleep(500); // Wait for highlight to be visible
        } catch (Exception e) {
            logger.warning("Failed to highlight element: " + e.getMessage());
        }
    }

    private void highlightColumn(int columnIndex, String color) {
        List<WebElement> cells = driver.findElements(By.cssSelector("#table1 tbody tr td:nth-child(" + (columnIndex + 1) + ")"));
        for (WebElement cell : cells) {
            highlightElement(cell, color);
        }
    }

    public void clickTableHeader(int index) {
        try {
            // Wait for the header to be clickable
            WebElement header = wait.until(ExpectedConditions.elementToBeClickable(tableHeaders.get(index)));
            
            // Highlight the header being tested
            highlightElement(header, "#FFA500"); // Orange color for header
            
            // Get initial data
            List<String> initialData = getColumnData(index);
            
            // Click the header using JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", header);
            
            // Wait for data to change
            int maxAttempts = 10;
            int attempt = 0;
            boolean dataChanged = false;
            
            while (!dataChanged && attempt < maxAttempts) {
                try {
                    Thread.sleep(1000); // Wait 1 second between attempts
                    List<String> newData = getColumnData(index);
                    if (!newData.equals(initialData)) {
                        dataChanged = true;
                        // Verify sorting
                        if (isColumnSortedAscending(newData)) {
                            logger.info(TestConstants.COLUMN_NAMES[index] + " is sorted ascending: " + newData);
                            highlightColumn(index, "#90EE90"); // Light green for ascending sort
                        } else if (isColumnSortedDescending(newData)) {
                            logger.info(TestConstants.COLUMN_NAMES[index] + " is sorted descending: " + newData);
                            highlightColumn(index, "#87CEEB"); // Sky blue for descending sort
                        } else {
                            // Highlight in red if sorting is incorrect
                            highlightColumn(index, "#FFB6C1"); // Light red for incorrect sorting
                            throw new RuntimeException(TestConstants.COLUMN_NAMES[index] + " is not properly sorted");
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    // Ignore stale element exceptions and continue
                }
                attempt++;
            }
            
            if (!dataChanged) {
                // Highlight in red if data didn't change
                highlightColumn(index, "#FFB6C1"); // Light red for no change
                throw new RuntimeException(TestConstants.COLUMN_NAMES[index] + " data did not change after clicking header");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to sort " + TestConstants.COLUMN_NAMES[index] + ": " + e.getMessage(), e);
        }
    }

    public List<String> getColumnData(int columnIndex) {
        List<String> columnData = new ArrayList<>();
        try {
            for (WebElement row : tableRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() > columnIndex) {
                    // Skip the Action column (index 5) as it contains links
                    if (columnIndex == 5) {
                        List<WebElement> links = cells.get(columnIndex).findElements(By.tagName("a"));
                        StringBuilder actions = new StringBuilder();
                        for (WebElement link : links) {
                            if (actions.length() > 0) {
                                actions.append(" ");
                            }
                            actions.append(link.getText());
                        }
                        columnData.add(actions.toString());
                    } else {
                        columnData.add(cells.get(columnIndex).getText().trim());
                    }
                }
            }
        } catch (StaleElementReferenceException e) {
            // If we get a stale element, refresh the table rows and try again
            wait.until(ExpectedConditions.visibilityOfAllElements(tableRows));
            return getColumnData(columnIndex);
        }
        return columnData;
    }

    public boolean verifyFirstRowData(String lastName, String firstName, String email, String due, String website) {
        List<WebElement> firstRowCells = tableRows.get(0).findElements(By.tagName("td"));
        return firstRowCells.get(0).getText().trim().equals(lastName) &&
               firstRowCells.get(1).getText().trim().equals(firstName) &&
               firstRowCells.get(2).getText().trim().equals(email) &&
               firstRowCells.get(3).getText().trim().equals(due) &&
               firstRowCells.get(4).getText().trim().equals(website);
    }

    private double parseCurrency(String value) {
        try {
            return NumberFormat.getCurrencyInstance(Locale.US).parse(value.trim()).doubleValue();
        } catch (ParseException e) {
            return 0.0;
        }
    }

    public boolean isColumnSortedAscending(List<String> columnData) {
        if (columnData.isEmpty()) return true;
        
        String firstElement = columnData.get(0);
        Comparator<String> comparator;
        
        if (firstElement.startsWith("$")) {
            comparator = (a, b) -> {
                double aValue = Double.parseDouble(a.replace("$", "").trim());
                double bValue = Double.parseDouble(b.replace("$", "").trim());
                return Double.compare(aValue, bValue);
            };
        } else if (firstElement.startsWith("http")) {
            comparator = (a, b) -> {
                String aDomain = a.replace("http://www.", "").replace(".com", "");
                String bDomain = b.replace("http://www.", "").replace(".com", "");
                return aDomain.compareTo(bDomain);
            };
        } else if (firstElement.contains("@")) {
            comparator = (a, b) -> {
                String aUsername = a.split("@")[0];
                String bUsername = b.split("@")[0];
                return aUsername.compareTo(bUsername);
            };
        } else {
            comparator = String::compareTo;
        }
        
        for (int i = 0; i < columnData.size() - 1; i++) {
            if (comparator.compare(columnData.get(i), columnData.get(i + 1)) > 0) {
                logger.warning("Sorting failed at index " + i + ": " + columnData.get(i) + " > " + columnData.get(i + 1));
                return false;
            }
        }
        return true;
    }

    public boolean isColumnSortedDescending(List<String> columnData) {
        if (columnData.isEmpty()) return true;
        
        String firstElement = columnData.get(0);
        Comparator<String> comparator;
        
        if (firstElement.startsWith("$")) {
            comparator = (a, b) -> {
                double aValue = Double.parseDouble(a.replace("$", "").trim());
                double bValue = Double.parseDouble(b.replace("$", "").trim());
                return Double.compare(bValue, aValue); // Reverse order
            };
        } else if (firstElement.startsWith("http")) {
            comparator = (a, b) -> {
                String aDomain = a.replace("http://www.", "").replace(".com", "");
                String bDomain = b.replace("http://www.", "").replace(".com", "");
                return bDomain.compareTo(aDomain); // Reverse order
            };
        } else if (firstElement.contains("@")) {
            comparator = (a, b) -> {
                String aUsername = a.split("@")[0];
                String bUsername = b.split("@")[0];
                return bUsername.compareTo(aUsername); // Reverse order
            };
        } else {
            comparator = (a, b) -> b.compareTo(a); // Reverse order
        }
        
        for (int i = 0; i < columnData.size() - 1; i++) {
            if (comparator.compare(columnData.get(i), columnData.get(i + 1)) > 0) {
                logger.warning("Sorting failed at index " + i + ": " + columnData.get(i) + " < " + columnData.get(i + 1));
                return false;
            }
        }
        return true;
    }

    public int getColumnCount() {
        return tableHeaders.size();
    }

    public void printColumnData(List<String> columnData) {
        logger.warning("Column data:");
        for (int i = 0; i < columnData.size(); i++) {
            logger.warning(i + ": " + columnData.get(i));
        }
    }

    public boolean isHeaderSortedAscending(int columnIndex) {
        WebElement header = tableHeaders.get(columnIndex);
        return header.getAttribute("class").contains("headerSortUp");
    }

    public boolean isHeaderSortedDescending(int columnIndex) {
        WebElement header = tableHeaders.get(columnIndex);
        return header.getAttribute("class").contains("headerSortDown");
    }
} 