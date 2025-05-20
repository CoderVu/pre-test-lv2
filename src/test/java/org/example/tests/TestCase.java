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

public class TestCase {
    private DataTablesPage dataTablesPage;
    private ExtentTest test;
    private User expectedUser;
    private WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = WebDriverConfig.getDriver();
        dataTablesPage = new DataTablesPage(driver);
        expectedUser = new User("Smith", "John", "jsmith@gmail.com", "$50.00", "http://www.jsmith.com");
    }

    @Test(description = "Test Sortable Data Tables functionality")
    public void testSortableDataTables() {
        test = ExtentManager.createTest("Sortable Data Tables Test", 
            "Verify data table sorting functionality");

        test.log(Status.INFO, "Step 1: Navigate to https://the-internet.herokuapp.com/");
        dataTablesPage.navigateToMainPage();
        test.log(Status.PASS, "Successfully navigated to the URL");

        test.log(Status.INFO, "Step 2: Click on the 'Sortable Data Tables' link");
        dataTablesPage.navigateToSortableDataTables();
        test.log(Status.PASS, "Successfully navigated to Sortable Data Tables page");

        test.log(Status.INFO, "Step 3: Verify first row data matches expected values");
        test.log(Status.INFO, "Expected data: " + expectedUser);
        boolean isFirstRowCorrect = dataTablesPage.verifyFirstRowData(
            expectedUser.getLastName(),
            expectedUser.getFirstName(),
            expectedUser.getEmail(),
            expectedUser.getDue(),
            expectedUser.getWebsite()
        );
        Assert.assertTrue(isFirstRowCorrect, "First row data is incorrect. Expected: " + expectedUser + 
            ", Actual: " + dataTablesPage.getColumnData(0));
        test.log(Status.PASS, "First row data verification passed");

        test.log(Status.INFO, "Step 4: Test sorting functionality for all columns");
        dataTablesPage.testAllColumnsSorting();
        test.log(Status.PASS, "All sorting tests completed successfully");
    }

    @AfterClass
    public void tearDown() {
        WebDriverConfig.quitDriver();
    }
} 