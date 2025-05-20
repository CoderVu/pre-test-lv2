package org.example.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.example.config.WebDriverConfig;
import org.example.report.ExtentManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private ExtentTest test;

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test started: " + result.getName());
        test = ExtentManager.createTest(
            result.getMethod().getDescription(),
            "Test execution started"
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getName());
        test.log(Status.PASS, "Test passed successfully");
        ExtentManager.flush();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getName());
        Throwable throwable = result.getThrowable();
        String errorMessage = throwable.getMessage();
        
        test.log(Status.FAIL, "Test failed: " + errorMessage);
        
        // Log stack trace
        if (throwable.getStackTrace().length > 0) {
            test.log(Status.INFO, "Stack trace: " + throwable.getStackTrace()[0]);
        }
        
        // Capture screenshot
        try {
            System.out.println("Attempting to capture screenshot...");
            String screenshotPath = ExtentManager.captureScreenshot(
                WebDriverConfig.getDriver(), 
                "test_failure_" + result.getName()
            );
            if (screenshotPath != null) {
                System.out.println("Screenshot captured at: " + screenshotPath);
                test.addScreenCaptureFromPath(screenshotPath);
            } else {
                System.out.println("Failed to capture screenshot - path is null");
            }
        } catch (Exception e) {
            System.out.println("Error capturing screenshot: " + e.getMessage());
            e.printStackTrace();
            test.log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
        }
        
        // Flush report after failure
        System.out.println("Flushing report...");
        ExtentManager.flush();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getName());
        test.log(Status.SKIP, "Test was skipped");
        ExtentManager.flush();
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Test suite finished");
        // Final flush to ensure all reports are written
        ExtentManager.flush();
    }
} 