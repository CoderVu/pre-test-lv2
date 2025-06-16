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
        test = ExtentManager.createTest(
            result.getMethod().getDescription(),
            "Test execution started"
        );
        test.log(Status.INFO, "Test started: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "Test completed successfully");
        ExtentManager.flush();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable throwable = result.getThrowable();
        String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
        
        test.log(Status.FAIL, "Test failed with error: " + errorMessage);
        
        if (throwable != null && throwable.getStackTrace().length > 0) {
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
            }
            test.log(Status.INFO, "Stack trace:\n" + stackTrace.toString());
        }
        
        captureAndAttachScreenshot(result.getName());
        
        ExtentManager.flush();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "Test was skipped: " + result.getSkipCausedBy());
        ExtentManager.flush();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    private void captureAndAttachScreenshot(String testName) {
        try {
            String screenshotPath = ExtentManager.captureScreenshot(
                WebDriverConfig.getDriver(), 
                "failure_" + testName
            );
            
            if (screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath);
            } else {
                test.log(Status.WARNING, "Failed to capture screenshot - path is null");
            }
        } catch (Exception e) {
            test.log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
        }
    }
} 