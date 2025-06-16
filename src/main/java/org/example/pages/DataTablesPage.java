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
import java.util.Collections;

public class DataTablesPage extends BasePage {

    // Locators
    @FindBy(linkText = "Sortable Data Tables")
    private WebElement sortableDataTablesLink;

    @FindBy(id = "table1")
    private WebElement firstTable;

    @FindBy(css = "#table1 th.header")
    private List<WebElement> tableHeaders;

    @FindBy(css = "#table1 tbody tr")
    private List<WebElement> tableRows;

    @FindBy(css = "#table1 tbody tr:first-child td")
    private List<WebElement> firstRowCells;

    public WebElement getSortableDataTablesLink() {
        return sortableDataTablesLink;
    }

    public WebElement getFirstTable() {
        return firstTable;
    }

    public DataTablesPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToMainPage() {
        driver.get(TestConstants.BASE_URL);
    }

    public void navigateToSortableDataTables() {
        navigateToMainPage();
        waitForElementClickable(sortableDataTablesLink);
        click(sortableDataTablesLink);
        waitForElementVisible(firstTable);
    }

    public void clickTableHeader(int index) {
        try {
            WebElement header = wait.until(ExpectedConditions.elementToBeClickable(tableHeaders.get(index)));
            clickByJavaScript(header);
            waitForElementVisible(firstTable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to click table header at index " + index + ": " + e.getMessage(), e);
        }
    }

    public List<String> getColumnData(int columnIndex) {
        List<String> columnData = new ArrayList<>();
        try {
            waitForElementVisible(firstTable);
            for (WebElement row : tableRows) {
                List<WebElement> cells = row.findElements(By.cssSelector("td"));
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
                    }
                }
            }
        } catch (StaleElementReferenceException e) {
            waitForSeconds(1);
            return getColumnData(columnIndex);
        }
        return columnData;
    }

    public List<String> getFirstRowData() {
        List<String> firstRowData = new ArrayList<>();
        try {
            waitForElementVisible(firstTable);
            if (firstRowCells.isEmpty()) {
                return firstRowData;
            }
            
            for (int i = 0; i < Math.min(firstRowCells.size(), 5); i++) {
                String cellText = firstRowCells.get(i).getText().trim();
                firstRowData.add(cellText);
            }
        } catch (StaleElementReferenceException e) {
            waitForSeconds(1);
            return getFirstRowData();
        }
        return firstRowData;
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
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < tableHeaders.size(); i++) {
            String headerText = tableHeaders.get(i).getText().trim();
            if (headerText.equals(columnName)) {
                return i;
            }
        }
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
            waitForSeconds(1);
            
            if (!isColumnAscending(columnName)) {
                clickColumnHeader(columnName);
                waitForSeconds(1);
                
                if (!isColumnAscending(columnName)) {
                    throw new RuntimeException("Column '" + columnName + "' is not in ascending order after multiple clicks");
                }
            }
        }
    }

    public void verifyColumnsDescending(String... columnNames) {
        for (String columnName : columnNames) {
            clickColumnHeader(columnName);
            waitForSeconds(1);
            
            if (isColumnAscending(columnName)) {
                clickColumnHeader(columnName);
                waitForSeconds(1);
            }
            
            if (!isColumnDescending(columnName)) {
                clickColumnHeader(columnName);
                waitForSeconds(1);
                
                if (!isColumnDescending(columnName)) {
                    throw new RuntimeException("Column '" + columnName + "' is not in descending order after multiple clicks");
                }
            }
        }
    }
} 