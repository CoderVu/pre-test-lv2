package org.example.pages;

import org.example.constants.TestConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.StaleElementReferenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Collections;

public class DataTablesPage extends BasePage {
    private Logger logger = Logger.getLogger(DataTablesPage.class.getName());

    // Locators
    @FindBy(linkText = "Sortable Data Tables")
    private WebElement sortableDataTablesLink;

    @FindBy(id = "table1")
    private WebElement firstTable;

    @FindBy(css = "#table1 th.header")
    private List<WebElement> tableHeaders;

    @FindBy(css = "#table1 tbody tr")
    private List<WebElement> tableRows;

    @FindBy(css = "#table1 tbody tr td")
    private List<WebElement> tableCells;

    // Getters for WebElements
    public WebElement getSortableDataTablesLink() {
        return sortableDataTablesLink;
    }

    public WebElement getFirstTable() {
        return firstTable;
    }


    public boolean isElementDisplayed(WebElement element) {
        return super.isElementDisplayed(element);
    }

    public void waitForElementVisible(WebElement element) {
        super.waitForElementVisible(element);
    }

    public void waitForElementClickable(WebElement element) {
        super.waitForElementClickable(element);
    }

    public void click(WebElement element) {
        super.click(element);
    }

    public void sendKeys(WebElement element, String text) {
        super.sendKeys(element, text);
    }

    public String getText(WebElement element) {
        return super.getText(element);
    }

    // Constructor
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

    public void clickTableHeader(int index) {
        try {
            WebElement header = wait.until(ExpectedConditions.elementToBeClickable(tableHeaders.get(index)));
            logger.info("=== Column: " + TestConstants.COLUMN_NAMES[index] + " ===");
            
            clickByJavaScript(header);
            wait.until(ExpectedConditions.visibilityOf(firstTable));
            
            List<String> columnData = getColumnData(index);
            logger.info("Column data after clicking: " + columnData);
            
            boolean isAscending = isColumnSortedAscending(columnData);
            boolean isDescending = isColumnSortedDescending(columnData);
            
            if (isAscending) {
                logger.info(TestConstants.COLUMN_NAMES[index] + " is sorted ascending: " + columnData);
            } else if (isDescending) {
                logger.info(TestConstants.COLUMN_NAMES[index] + " is sorted descending: " + columnData);
            } else {
                logger.warning("Sorting verification failed:");
                logger.warning("Data: " + columnData);
                throw new RuntimeException(TestConstants.COLUMN_NAMES[index] + " is not properly sorted");
            }
            
        } catch (Exception e) {
            logger.severe("Failed to sort " + TestConstants.COLUMN_NAMES[index] + ": " + e.getMessage());
            throw new RuntimeException("Failed to sort " + TestConstants.COLUMN_NAMES[index] + ": " + e.getMessage(), e);
        }
    }

    public List<String> getColumnData(int columnIndex) {
        List<String> columnData = new ArrayList<>();
        try {
            wait.until(ExpectedConditions.visibilityOf(firstTable));
            for (WebElement row : tableRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() > columnIndex) {
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
                        String cellText = cells.get(columnIndex).getText().trim();
                        columnData.add(cellText);
                        logger.info("Column " + TestConstants.COLUMN_NAMES[columnIndex] + " - Cell value: " + cellText);
                    }
                }
            }
        } catch (StaleElementReferenceException e) {
            logger.warning("Stale element in getColumnData, retrying...");
            waitForSeconds(1);
            return getColumnData(columnIndex);
        }
        return columnData;
    }

    public boolean verifyFirstRowData(String lastName, String firstName, String email, String due, String website) {
        List<WebElement> firstRowCells = tableCells.subList(0, 5);
        return firstRowCells.get(0).getText().trim().equals(lastName) &&
               firstRowCells.get(1).getText().trim().equals(firstName) &&
               firstRowCells.get(2).getText().trim().equals(email) &&
               firstRowCells.get(3).getText().trim().equals(due) &&
               firstRowCells.get(4).getText().trim().equals(website);
    }

    public boolean isColumnSortedAscending(List<String> columnData) {
        if (columnData.isEmpty()) return true;
        
        String firstElement = columnData.get(0);
        List<String> sortedData = new ArrayList<>(columnData);
        
        if (firstElement.startsWith("$")) {
            sortedData.sort((a, b) -> {
                double aValue = Double.parseDouble(a.replace("$", "").trim());
                double bValue = Double.parseDouble(b.replace("$", "").trim());
                return Double.compare(aValue, bValue);
            });
        } else if (firstElement.startsWith("http")) {
            sortedData.sort((a, b) -> {
                String aDomain = a.replace("http://www.", "").replace(".com", "");
                String bDomain = b.replace("http://www.", "").replace(".com", "");
                return aDomain.compareTo(bDomain);
            });
        } else if (firstElement.contains("@")) {
            sortedData.sort((a, b) -> {
                String aUsername = a.split("@")[0];
                String bUsername = b.split("@")[0];
                return aUsername.compareTo(bUsername);
            });
        } else {
            Collections.sort(sortedData);
        }
        
        return columnData.equals(sortedData);
    }

    public boolean isColumnSortedDescending(List<String> columnData) {
        if (columnData.isEmpty()) return true;
        
        String firstElement = columnData.get(0);
        List<String> sortedData = new ArrayList<>(columnData);
        
        if (firstElement.startsWith("$")) {
            sortedData.sort((a, b) -> {
                double aValue = Double.parseDouble(a.replace("$", "").trim());
                double bValue = Double.parseDouble(b.replace("$", "").trim());
                return Double.compare(bValue, aValue);
            });
        } else if (firstElement.startsWith("http")) {
            sortedData.sort((a, b) -> {
                String aDomain = a.replace("http://www.", "").replace(".com", "");
                String bDomain = b.replace("http://www.", "").replace(".com", "");
                return bDomain.compareTo(aDomain);
            });
        } else if (firstElement.contains("@")) {
            sortedData.sort((a, b) -> {
                String aUsername = a.split("@")[0];
                String bUsername = b.split("@")[0];
                return bUsername.compareTo(aUsername);
            });
        } else {
            sortedData.sort(Collections.reverseOrder());
        }
        
        return columnData.equals(sortedData);
    }

    public String getColumnSortState(int columnIndex) {
        List<String> columnData = getColumnData(columnIndex);
        if (isColumnSortedAscending(columnData)) {
            return "ascending";
        } else if (isColumnSortedDescending(columnData)) {
            return "descending";
        }
        return "unsorted";
    }

    public boolean isColumnInAscendingOrder(int columnIndex) {
        return getColumnSortState(columnIndex).equals("ascending");
    }

    public boolean isColumnInDescendingOrder(int columnIndex) {
        return getColumnSortState(columnIndex).equals("descending");
    }

    public void clickColumnHeader(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        if (columnIndex == -1) {
            throw new RuntimeException("Column '" + columnName + "' not found");
        }
        clickTableHeader(columnIndex);
        logger.info("Clicked on column: " + columnName);
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < tableHeaders.size(); i++) {
            String headerText = tableHeaders.get(i).getText().trim();
            logger.info("Checking header: " + headerText + " against: " + columnName);
            if (headerText.equals(columnName)) {
                return i;
            }
        }
        logger.warning("Column '" + columnName + "' not found in table headers");
        return -1;
    }

    public boolean isColumnAscending(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        if (columnIndex == -1) {
            throw new RuntimeException("Column '" + columnName + "' not found");
        }
        return isColumnInAscendingOrder(columnIndex);
    }

    public boolean isColumnDescending(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        if (columnIndex == -1) {
            throw new RuntimeException("Column '" + columnName + "' not found");
        }
        return isColumnInDescendingOrder(columnIndex);
    }

    public void verifyColumnsAscending(String... columnNames) {
        for (String columnName : columnNames) {
            clickColumnHeader(columnName);
            if (!isColumnAscending(columnName)) {
                throw new RuntimeException("Column '" + columnName + "' is not in ascending order");
            }
            logger.info(columnName + " is in ascending order");
        }
    }

    public void verifyColumnsDescending(String... columnNames) {
        for (String columnName : columnNames) {
            clickColumnHeader(columnName);
            if (!isColumnDescending(columnName)) {
                throw new RuntimeException("Column '" + columnName + "' is not in descending order");
            }
            logger.info(columnName + " is in descending order");
        }
    }

} 