package org.example.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentManager {
    private static final ExtentReports extentReports = new ExtentReports();
    private static final String REPORT_PATH = "test-output/extent-reports/";
    private static final String SCREENSHOT_PATH = "test-output/screenshots/";

    static {
        try {
            System.out.println("Initializing ExtentManager...");
            
            // Create directories if they don't exist
            Path reportDir = Paths.get(REPORT_PATH);
            Path screenshotDir = Paths.get(SCREENSHOT_PATH);
            
            if (!Files.exists(reportDir)) {
                System.out.println("Creating report directory: " + reportDir);
                Files.createDirectories(reportDir);
            }
            
            if (!Files.exists(screenshotDir)) {
                System.out.println("Creating screenshot directory: " + screenshotDir);
                Files.createDirectories(screenshotDir);
            }

            // Configure ExtentSparkReporter
            String reportFile = REPORT_PATH + "report.html";
            System.out.println("Creating report file: " + reportFile);
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile);
            sparkReporter.config().setDocumentTitle("Test Execution Report");
            sparkReporter.config().setReportName("Data Tables Test Report");
            sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);
            
            // Add reporter to ExtentReports
            extentReports.attachReporter(sparkReporter);
            
            // Add system info
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            
            System.out.println("ExtentManager initialized successfully");
        } catch (IOException e) {
            System.err.println("Error initializing ExtentManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ExtentTest createTest(String name, String description) {
        System.out.println("Creating test: " + name);
        return extentReports.createTest(name, description);
    }

    public static void flush() {
        System.out.println("Flushing report...");
        extentReports.flush();
    }

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            System.out.println("Capturing screenshot: " + screenshotName);
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            // Create timestamp for unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = screenshotName + "_" + timestamp + ".png";
            Path targetPath = Paths.get(SCREENSHOT_PATH, fileName);
            
            System.out.println("Saving screenshot to: " + targetPath);
            // Copy screenshot to target location
            Files.copy(source.toPath(), targetPath);
            
            // Return relative path for report
            String relativePath = SCREENSHOT_PATH + fileName;
            System.out.println("Screenshot saved successfully at: " + relativePath);
            return relativePath;
        } catch (IOException e) {
            System.err.println("Error capturing screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 