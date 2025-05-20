package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    protected void waitForElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForElementClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void click(WebElement element) {
        waitForElementClickable(element);
        element.click();
    }

    protected void sendKeys(WebElement element, String text) {
        waitForElementVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        waitForElementVisible(element);
        return element.getText();
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            waitForElementVisible(element);
            return element.isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected void clickByJavaScript(WebElement element) {
        waitForElementClickable(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected boolean waitForDataChange(List<String> initialData, List<String> newData, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(driver -> {
                try {
                    return !newData.equals(initialData);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Wait was interrupted", e);
        }
    }
} 