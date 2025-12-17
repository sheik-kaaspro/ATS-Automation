package com.readys.ats.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import com.readys.ats.utils.ConfigReader;
import com.readys.ats.utils.ExtentReportManager;
import com.readys.ats.utils.PlaywrightUtils;

public class TestListener implements ITestListener, ISuiteListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static int skippedTests = 0;

    @Override
    public void onStart(ISuite suite) {
        try {
            logger.info("=================================================================");
            logger.info("Starting Test Suite: " + suite.getName());
            logger.info("=================================================================");

            // Initialize ExtentReports
            ExtentReportManager.initializeReport();


            // Reset counters
            totalTests = 0;
            passedTests = 0;
            failedTests = 0;
            skippedTests = 0;

        } catch (Exception e) {
            logger.error("Error in onStart method: " + e.getMessage(), e);
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        try {
            logger.info("=================================================================");
            logger.info("Test Suite Finished: " + suite.getName());
            logger.info("Total Tests: " + totalTests);
            logger.info("Passed: " + passedTests);
            logger.info("Failed: " + failedTests);
            logger.info("Skipped: " + skippedTests);
            logger.info("=================================================================");


            // Flush and open report
            ExtentReportManager.flushReport();

            // Small delay to ensure report is fully written
            Thread.sleep(1000);

        } catch (Exception e) {
            logger.error("Error in onFinish method: " + e.getMessage(), e);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getName();

            logger.info("Starting test: " + testName + " in " + className);

            // Create test in ExtentReports
            String testDescription = getTestDescription(result);
            ExtentReportManager.createTest(testName, testDescription);

            // Add test metadata
            ExtentReportManager.addTestCategory(getTestCategory(result));
            ExtentReportManager.addTestAuthor("Automation Team");

            totalTests++;

        } catch (Exception e) {
            logger.error("Error in onTestStart method: " + e.getMessage(), e);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            long duration = result.getEndMillis() - result.getStartMillis();

            logger.info("Test PASSED: " + testName + " (Duration: " + duration + "ms)");

            ExtentReportManager.logPass("Test completed successfully");

            // Capture screenshot on success if configured
            ConfigReader config = ConfigReader.getInstance();
            if ("true".equalsIgnoreCase(config.getProperty("screenshot.on.success"))) {
                captureScreenshot(testName, "SUCCESS");
            }

            passedTests++;

        } catch (Exception e) {
            logger.error("Error in onTestSuccess method: " + e.getMessage(), e);
        } finally {
            ExtentReportManager.removeTest();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
            long duration = result.getEndMillis() - result.getStartMillis();

            logger.error("Test FAILED: " + testName + " - " + errorMessage + " (Duration: " + duration + "ms)");

            // Capture screenshot on failure
            String screenshotPath = captureScreenshot(testName, "FAILURE");

            // Log failure with screenshot
            if (screenshotPath != null) {
                ExtentReportManager.logFailWithScreenshot("Test failed: " + errorMessage, screenshotPath);
            } else {
                ExtentReportManager.logFail("Test failed: " + errorMessage);
            }

            // Log stack trace if available
            if (result.getThrowable() != null) {
                ExtentReportManager.logFail("Stack Trace: " + getStackTrace(result.getThrowable()));
            }

            failedTests++;

        } catch (Exception e) {
            logger.error("Error in onTestFailure method: " + e.getMessage(), e);
        } finally {
            ExtentReportManager.removeTest();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            String skipReason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Test skipped";

            logger.warn("Test SKIPPED: " + testName + " - " + skipReason);

            ExtentReportManager.createTest(testName, "Test was skipped");
            ExtentReportManager.addTestCategory(getTestCategory(result));
            ExtentReportManager.logSkip("Test skipped: " + skipReason);

            skippedTests++;

        } catch (Exception e) {
            logger.error("Error in onTestSkipped method: " + e.getMessage(), e);
        } finally {
            ExtentReportManager.removeTest();
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            logger.warn("Test failed but within success percentage: " + testName);

            ExtentReportManager.logWarning("Test failed but within acceptable success percentage");

        } catch (Exception e) {
            logger.error("Error in onTestFailedButWithinSuccessPercentage method: " + e.getMessage(), e);
        }
    }

    private String captureScreenshot(String testName, String status) {
        try {
            ConfigReader config = ConfigReader.getInstance();
            boolean shouldCapture = "true".equalsIgnoreCase(config.getProperty("screenshot.on.failure")) && "FAILURE".equals(status)
                                 || "true".equalsIgnoreCase(config.getProperty("screenshot.on.success")) && "SUCCESS".equals(status);

            if (shouldCapture) {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = testName + "_" + status + "_" + timestamp;

                String screenshotPath = PlaywrightUtils.captureScreenshot(fileName);
                if (screenshotPath != null) {
                    logger.info("Screenshot captured: " + screenshotPath);
                    return screenshotPath;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test: " + testName + ". Error: " + e.getMessage());
        }
        return null;
    }

    private String getTestDescription(ITestResult result) {
        try {
            // Get description from Test annotation if available
            Test testAnnotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class);
            if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
                return testAnnotation.description();
            }

            // Default description
            return "Automated test for " + result.getMethod().getMethodName();

        } catch (Exception e) {
            logger.warn("Failed to get test description: " + e.getMessage());
            return "Automated test";
        }
    }

    private String getTestCategory(ITestResult result) {
        try {
            // Get groups from Test annotation
            Test testAnnotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class);
            if (testAnnotation != null && testAnnotation.groups().length > 0) {
                return String.join(", ", testAnnotation.groups());
            }

            // Default category based on class name
            String className = result.getTestClass().getName();
            if (className.contains("Login")) {
                return "Login Tests";
            } else if (className.contains("Smoke")) {
                return "Smoke Tests";
            } else if (className.contains("Regression")) {
                return "Regression Tests";
            }

            return "Functional Tests";

        } catch (Exception e) {
            logger.warn("Failed to get test category: " + e.getMessage());
            return "General";
        }
    }

    private String getStackTrace(Throwable throwable) {
        try {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e) {
            logger.warn("Failed to get stack trace: " + e.getMessage());
            return throwable.toString();
        }
    }
}