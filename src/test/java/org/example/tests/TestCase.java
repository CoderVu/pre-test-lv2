package org.example.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.example.config.WebDriverConfig;
import org.example.model.User;
import org.example.pages.DataTablesPage;
import org.example.report.ExtentManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class TestCase {
    private WebDriverConfig driverConfig;
    private WebDriver driver; // Class-level variable
    private DataTablesPage dataTablesPage;
    private ExtentTest test;
    private User expectedUser;
    private final String[] columnNames = {"LastName", "FirstName", "Email", "Due", "Website", "Action"};

    @BeforeClass
    public void setup() {
        driverConfig = new WebDriverConfig();
        driver = driverConfig.getDriver();
        driver.manage().window().maximize();
        dataTablesPage = new DataTablesPage(driver);
        expectedUser = new User("Smith", "John", "jsmith@gmail.com", "$50.00", "http://www.jsmith.com");
    }

    @Test(description = "TC01 - Test first table data and sorting functionality")
    public void testFirstTableDataAndSorting() throws InterruptedException {
        test = ExtentManager.createTest("First Table Data and Sorting Test", 
            "Verify first table data and sorting functionality for all columns");
        
        try {
            // Step 1: Navigate to Sortable Data Tables page
            test.log(Status.INFO, "Step 1: Navigate to Sortable Data Tables page");
            dataTablesPage.navigateToSortableDataTables();
            test.log(Status.PASS, "Successfully navigated to Sortable Data Tables page");

            // Step 2: Verify first row data
            test.log(Status.INFO, "Step 2: Verify first row data matches expected values");
            test.log(Status.INFO, "Expected user data: " + expectedUser);
            boolean isFirstRowCorrect = dataTablesPage.verifyFirstRowData(
                expectedUser.getLastName(),
                expectedUser.getFirstName(),
                expectedUser.getEmail(),
                expectedUser.getDue(),
                expectedUser.getWebsite()
            );
            Assert.assertTrue(isFirstRowCorrect, "First row data is incorrect");
            test.log(Status.PASS, "First row data verification passed");

            // Step 3: Test sorting for each column
            test.log(Status.INFO, "Step 3: Test sorting functionality for all columns");
            int columnCount = dataTablesPage.getColumnCount();
            
            for (int i = 0; i < columnCount; i++) {
                // Skip sorting test for the Action column
                if (i == 5) continue;

                String columnName = columnNames[i];
                test.log(Status.INFO, "Testing " + columnName + " column sorting");

                // Get initial data before sorting
                List<String> initialData = dataTablesPage.getColumnData(i);
                test.log(Status.INFO, "Initial data for " + columnName + ": " + initialData);

                // Test ascending sort
                test.log(Status.INFO, "Testing ascending sort for " + columnName);
                dataTablesPage.clickTableHeader(i);
                Thread.sleep(1000); // Wait for sorting to complete
                
                // Verify data sorting
                List<String> ascendingData = dataTablesPage.getColumnData(i);
                test.log(Status.INFO, "Data after ascending sort: " + ascendingData);
                try {
                    Assert.assertTrue(dataTablesPage.isColumnSortedAscending(ascendingData),
                        columnName + " is not sorted in ascending order");
                    test.log(Status.PASS, columnName + " ascending sort verification passed");
                } catch (AssertionError e) {
                    test.log(Status.FAIL, e.getMessage());
                    test.addScreenCaptureFromPath(ExtentManager.captureScreenshot(driver, 
                        String.format("%s_ascending_sort_fail", columnName)));
                    throw e;
                }

                // Verify data changed after ascending sort
                try {
                    Assert.assertNotEquals(ascendingData, initialData, 
                        columnName + " data did not change after ascending sort");
                } catch (AssertionError e) {
                    test.log(Status.FAIL, e.getMessage());
                    test.addScreenCaptureFromPath(ExtentManager.captureScreenshot(driver, 
                        String.format("%s_ascending_no_change", columnName)));
                    throw e;
                }

                // Test descending sort
                test.log(Status.INFO, "Testing descending sort for " + columnName);
                dataTablesPage.clickTableHeader(i);
                Thread.sleep(1000); // Wait for sorting to complete
                
                // Verify data sorting
                List<String> descendingData = dataTablesPage.getColumnData(i);
                test.log(Status.INFO, "Data after descending sort: " + descendingData);
                try {
                    Assert.assertTrue(dataTablesPage.isColumnSortedDescending(descendingData),
                        columnName + " is not sorted in descending order");
                    test.log(Status.PASS, columnName + " descending sort verification passed");
                } catch (AssertionError e) {
                    test.log(Status.FAIL, e.getMessage());
                    test.addScreenCaptureFromPath(ExtentManager.captureScreenshot(driver, 
                        String.format("%s_descending_sort_fail", columnName)));
                    throw e;
                }

                // Verify data changed after descending sort
                try {
                    Assert.assertNotEquals(descendingData, ascendingData, 
                        columnName + " data did not change after descending sort");
                } catch (AssertionError e) {
                    test.log(Status.FAIL, e.getMessage());
                    test.addScreenCaptureFromPath(ExtentManager.captureScreenshot(driver, 
                        String.format("%s_descending_no_change", columnName)));
                    throw e;
                }
            }

            test.log(Status.PASS, "All sorting tests completed successfully");
            
        } catch (AssertionError e) {
            test.log(Status.FAIL, "Test failed: " + e.getMessage());
            test.addScreenCaptureFromPath(ExtentManager.captureScreenshot(driver, "test_failure"));
            throw e;
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed with exception: " + e.getMessage());
            test.addScreenCaptureFromPath(ExtentManager.captureScreenshot(driver, "test_exception"));
            throw e;
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        ExtentManager.flush();
    }
} 