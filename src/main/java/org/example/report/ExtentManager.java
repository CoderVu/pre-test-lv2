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
    private static final String REPORT_PATH = "src/test/resources/extent-reports/";
    private static final String SCREENSHOT_PATH = "src/test/resources/";
    private static String currentReportPath;

    static {
        try {
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

            // Configure ExtentSparkReporter with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            currentReportPath = REPORT_PATH + "extent-report_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(currentReportPath);
            sparkReporter.config().setDocumentTitle("Test Execution Report");
            sparkReporter.config().setReportName("Data Tables Test Report");
            sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);
            
            // Add reporter to ExtentReports
            extentReports.attachReporter(sparkReporter);
            
            // Add system info
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExtentTest createTest(String name, String description) {
        return extentReports.createTest(name, description);
    }

    public static void flush() {
        extentReports.flush();
    }

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            // Create timestamp for unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = screenshotName + "_" + timestamp + ".png";
            
            // Save screenshot in the same directory as the report
            Path reportDir = Paths.get(currentReportPath).getParent();
            Path targetPath = reportDir.resolve(fileName);
            // Copy screenshot to target location
            Files.copy(source.toPath(), targetPath);
            
            // Return relative path for report
            String relativePath = fileName;
            return relativePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
} 