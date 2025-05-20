package org.example.tests;

import org.example.config.WebDriverConfig;
import org.example.constants.TestConstants;
import org.example.model.User;
import org.example.pages.DataTablesPage;
import org.example.testdata.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

public class TestCase {
    private DataTablesPage dataTablesPage;
    private User expectedUser;
    private WebDriver driver;
    private TestData testData;

    @BeforeClass
    public void setup() {
        driver = WebDriverConfig.getDriver();
        dataTablesPage = new DataTablesPage(driver);
        expectedUser = TestData.EXPECTED_USER;
    }

    @Test(
        description = "TC01: Verify data table functionality - First row data validation and column sorting"
    )
    public void verifyDataTableFirstRowAndColumnSorting() {
        // Step 1: Navigate to main page
        dataTablesPage.navigateToMainPage();
        Assert.assertTrue(dataTablesPage.isElementDisplayed(dataTablesPage.getSortableDataTablesLink()), 
            "Failed to navigate to main page");

        // Step 2: Navigate to Sortable Data Tables
        dataTablesPage.navigateToSortableDataTables();
        Assert.assertTrue(dataTablesPage.isElementDisplayed(dataTablesPage.getFirstTable()), 
            "Failed to navigate to sortable data tables page");

        // Step 3: Verify first row data
        boolean isFirstRowCorrect = dataTablesPage.verifyFirstRowData(
            expectedUser.getLastName(),
            expectedUser.getFirstName(),
            expectedUser.getEmail(),
            expectedUser.getDue(),
            expectedUser.getWebsite()
        );
        Assert.assertTrue(isFirstRowCorrect, 
            String.format("First row data is incorrect.\nExpected: %s\nActual: %s", 
                expectedUser, dataTablesPage.getColumnData(0)));

        // Step 4: Test sorting functionality
        dataTablesPage.verifyColumnsAscending(
            "Last Name", "First Name", "Email", "Due", "Web Site"
        );
        dataTablesPage.verifyColumnsDescending(
            "Last Name", "First Name", "Email", "Due", "Web Site"
        );
    }

    @AfterClass
    public void tearDown() {
        WebDriverConfig.quitDriver();
    }
} 