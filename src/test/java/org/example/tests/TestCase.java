package org.example.tests;

import org.example.config.WebDriverConfig;
import org.example.model.User;
import org.example.pages.DataTablesPage;
import org.example.testdata.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class TestCase {
    private DataTablesPage dataTablesPage;
    private User expectedUser;
    private WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = WebDriverConfig.getDriver();
        dataTablesPage = new DataTablesPage(driver);
        expectedUser = TestData.EXPECTED_USER;
    }

    @Test(description = "TC01: Verify first row data validation and column sorting")
    public void verifyDataTableFirstRowAndColumnSorting() {
        dataTablesPage.navigateToMainPage();
        Assert.assertTrue(dataTablesPage.isElementDisplayed(dataTablesPage.getSortableDataTablesLink()), 
            "Failed to navigate to main page");

        dataTablesPage.navigateToSortableDataTables();
        Assert.assertTrue(dataTablesPage.isElementDisplayed(dataTablesPage.getFirstTable()), 
            "Failed to navigate to sortable data tables page");

        List<String> actualFirstRowData = dataTablesPage.getFirstRowData();
        List<String> expectedFirstRowData = List.of(
            expectedUser.getLastName(),
            expectedUser.getFirstName(),
            expectedUser.getEmail(),
            expectedUser.getDue(),
            expectedUser.getWebsite()
        );
        
        Assert.assertEquals(actualFirstRowData, expectedFirstRowData, 
            "First row data is incorrect");

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