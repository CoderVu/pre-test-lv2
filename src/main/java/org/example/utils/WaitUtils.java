package org.example.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.example.constants.TestConstants;

import java.time.Duration;

public class WaitUtils {
    private static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(TestConstants.DEFAULT_TIMEOUT));
    }

    public static void waitForElementVisible(WebDriver driver, By locator) {
        getWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void waitForElementVisible(WebDriver driver, WebElement element) {
        getWait(driver).until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForElementClickable(WebDriver driver, By locator) {
        getWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitForElementClickable(WebDriver driver, WebElement element) {
        getWait(driver).until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void waitForElementInvisible(WebDriver driver, By locator) {
        getWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static void waitForPageLoad(WebDriver driver) {
        getWait(driver).until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }
} 