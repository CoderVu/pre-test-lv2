package org.example.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.example.config.WebDriverConfig;
import org.example.report.ExtentManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TestListener implements ITestListener {
    private static final Logger LOGGER = Logger.getLogger(TestListener.class.getName());
    private ExtentTest test;

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info("Starting test: " + result.getName());
        test = ExtentManager.createTest(
            result.getMethod().getDescription(),
            "Test execution started"
        );
        test.log(Status.INFO, "Test started: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info("Test passed: " + result.getName());
        test.log(Status.PASS, "Test completed successfully");
        ExtentManager.flush();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.severe("Test failed: " + result.getName());
        Throwable throwable = result.getThrowable();
        String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
        
        test.log(Status.FAIL, "Test failed with error: " + errorMessage);
        
        // Log detailed stack trace
        if (throwable != null && throwable.getStackTrace().length > 0) {
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
            }
            test.log(Status.INFO, "Stack trace:\n" + stackTrace.toString());
        }
        
        // Capture and attach screenshot
        captureAndAttachScreenshot(result.getName());
        
        ExtentManager.flush();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warning("Test skipped: " + result.getName());
        test.log(Status.SKIP, "Test was skipped: " + result.getSkipCausedBy());
        ExtentManager.flush();
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("Test suite execution completed");
        ExtentManager.flush();
    }

    private void captureAndAttachScreenshot(String testName) {
        try {
            LOGGER.info("Capturing screenshot for failed test: " + testName);
            String screenshotPath = ExtentManager.captureScreenshot(
                WebDriverConfig.getDriver(), 
                "failure_" + testName
            );
            
            if (screenshotPath != null) {
                LOGGER.info("Screenshot captured successfully: " + screenshotPath);
                test.addScreenCaptureFromPath(screenshotPath);
            } else {
                LOGGER.warning("Screenshot capture failed - path is null");
                test.log(Status.WARNING, "Failed to capture screenshot - path is null");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error capturing screenshot", e);
            test.log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
        }
    }
} 