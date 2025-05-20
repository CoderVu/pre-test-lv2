package org.example.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import java.util.logging.Logger;
import java.util.logging.Level;

public class WebDriverConfig {
    private static final Logger LOGGER = Logger.getLogger(WebDriverConfig.class.getName());
    private static WebDriver driver;
    private static final String BROWSER = System.getProperty("browser", "chrome").toLowerCase();

    public static WebDriver getDriver() {
        if (driver == null) {
            driver = createDriver();
        }
        return driver;
    }

    private static WebDriver createDriver() {
        LOGGER.info("Initializing " + BROWSER + " browser");
        switch (BROWSER) {
            case "firefox":
                return createFirefoxDriver();
            case "edge":
                return createEdgeDriver();
            case "chrome":
            default:
                return createChromeDriver();
        }
    }

    private static WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-maximized");
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        return new EdgeDriver(options);
    }

    public static void quitDriver() {
        if (driver != null) {
            LOGGER.info("Closing " + BROWSER + " browser");
            driver.quit();
            driver = null;
        }
    }

    public static String getBrowserName() {
        return BROWSER;
    }
} 