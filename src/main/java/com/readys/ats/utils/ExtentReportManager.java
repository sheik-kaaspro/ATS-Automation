package com.readys.ats.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {
    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static String reportPath;

    public static void initializeReport() {
        try {
            ConfigReader config = ConfigReader.getInstance();
            logger.info("Starting ExtentReports initialization...");
            
         // Ensure report path ends with slash
            String basePath = config.getProperty("extent.report.path");
            if (!basePath.endsWith("/") && !basePath.endsWith("\\")) {
                basePath = basePath + File.separator;
            }

            // Always generate a single fixed report file (no timestamp)
            String reportName = config.getProperty("extent.report.name") + ".html";
            reportPath = basePath + reportName;

            // Delete old report if exists
            File oldReport = new File(reportPath);
            if (oldReport.exists()) {
                oldReport.delete();
                logger.info("Old Extent report deleted: " + reportPath);
            }

            // Ensure directory exists
            File reportDir = new File(basePath);
            if (!reportDir.exists()) {
                boolean created = reportDir.mkdirs();
                logger.info("Created report directory: " + reportDir.getAbsolutePath() + " - Success: " + created);
            }


            // Initialize Spark Reporter
            sparkReporter = new ExtentSparkReporter(reportPath);
            logger.info("ExtentSparkReporter created");

            configureSparkReporter(config);
            logger.info("SparkReporter configured");

            // Initialize ExtentReports
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            logger.info("ExtentReports instance created and reporter attached");

            setSystemInfo(config);
            logger.info("System info set");

            logger.info("ExtentReports initialized successfully. Report path: " + reportPath);

        } catch (Exception e) {
            logger.error("Failed to initialize ExtentReports: " + e.getMessage(), e);
            // Don't throw exception - just log the error and continue
            logger.warn("ExtentReports initialization failed, but tests will continue without reporting");
        }
    }

    private static void configureSparkReporter(ConfigReader config) {
        try {
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle(config.getProperty("extent.report.title"));
            sparkReporter.config().setReportName(config.getProperty("extent.report.name"));
            sparkReporter.config().setTimelineEnabled(true);
            sparkReporter.config().setEncoding("utf-8");

            // Custom CSS for better appearance
            String css = """
                .test-item { margin-bottom: 10px; }
                .test-content { padding: 10px; border: 1px solid #ddd; border-radius: 5px; }
                .screenshot-container { text-align: center; margin: 10px 0; }
                .screenshot-container img { max-width: 100%; height: auto; border: 1px solid #ccc; }
                """;
            sparkReporter.config().setCss(css);

        } catch (Exception e) {
            logger.warn("Failed to configure Spark Reporter: " + e.getMessage());
        }
    }

    private static void setSystemInfo(ConfigReader config) {
        try {
            extent.setSystemInfo("Application", config.getProperty("app.name"));
            extent.setSystemInfo("Environment", config.getProperty("app.environment"));
            extent.setSystemInfo("Browser", config.getProperty("browser"));
            extent.setSystemInfo("URL", config.getProperty("app.url"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            extent.setSystemInfo("Timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        } catch (Exception e) {
            logger.warn("Failed to set system info: " + e.getMessage());
        }
    }

    public static ExtentTest createTest(String testName) {
        return createTest(testName, "");
    }

    public static ExtentTest createTest(String testName, String description) {
        try {
            if (extent == null) {
                logger.warn("ExtentReports is null, cannot create test: " + testName);
                return null;
            }
            ExtentTest test = extent.createTest(testName, description);
            extentTest.set(test);
            logger.info("Created test: " + testName);
            return test;
        } catch (Exception e) {
            logger.error("Failed to create test: " + testName + ". Error: " + e.getMessage());
            return null; // Return null instead of throwing exception
        }
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void removeTest() {
        extentTest.remove();
    }

    public static void logInfo(String message) {
        try {
            ExtentTest test = getTest();
            if (test != null) {
                test.log(Status.INFO, message);
                logger.info(message);
            }
        } catch (Exception e) {
            logger.error("Failed to log info message: " + e.getMessage());
        }
    }

    public static void logPass(String message) {
        try {
            ExtentTest test = getTest();
            if (test != null) {
                test.log(Status.PASS, message);
                logger.info("PASS: " + message);
            }
        } catch (Exception e) {
            logger.error("Failed to log pass message: " + e.getMessage());
        }
    }

    public static void logFail(String message) {
        try {
            ExtentTest test = getTest();
            if (test != null) {
                test.log(Status.FAIL, message);
                logger.error("FAIL: " + message);
            }
        } catch (Exception e) {
            logger.error("Failed to log fail message: " + e.getMessage());
        }
    }

    public static void logSkip(String message) {
        try {
            ExtentTest test = getTest();
            if (test != null) {
                test.log(Status.SKIP, message);
                logger.warn("SKIP: " + message);
            }
        } catch (Exception e) {
            logger.error("Failed to log skip message: " + e.getMessage());
        }
    }

    public static void logWarning(String message) {
        try {
            ExtentTest test = getTest();
            if (test != null) {
                test.log(Status.WARNING, message);
                logger.warn("WARNING: " + message);
            }
        } catch (Exception e) {
            logger.error("Failed to log warning message: " + e.getMessage());
        }
    }

    public static void attachScreenshot(String screenshotPath) {
        attachScreenshot(screenshotPath, "Screenshot");
    }

    public static void attachScreenshot(String screenshotPath, String description) {
        try {
            ExtentTest test = getTest();
            if (test != null && screenshotPath != null && !screenshotPath.isEmpty()) {
                File screenshotFile = new File(screenshotPath);
                if (screenshotFile.exists()) {
                    test.log(Status.INFO, description,
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                    logger.info("Screenshot attached: " + screenshotPath);
                } else {
                    logger.warn("Screenshot file not found: " + screenshotPath);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot: " + e.getMessage());
        }
    }

    public static void logFailWithScreenshot(String message, String screenshotPath) {
        try {
            ExtentTest test = getTest();
            if (test != null) {
                if (screenshotPath != null && !screenshotPath.isEmpty()) {
                    File screenshotFile = new File(screenshotPath);
                    if (screenshotFile.exists()) {
                        test.log(Status.FAIL, message,
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                    } else {
                        test.log(Status.FAIL, message + " (Screenshot not available)");
                    }
                } else {
                    test.log(Status.FAIL, message);
                }
                logger.error("FAIL: " + message);
            }
        } catch (Exception e) {
            logger.error("Failed to log fail message with screenshot: " + e.getMessage());
        }
    }

    public static void flushReport() {
        try {
            if (extent != null) {
                extent.flush();
                logger.info("ExtentReports flushed successfully");

                // Auto-open report if configured
                ConfigReader config = ConfigReader.getInstance();
                if ("true".equalsIgnoreCase(config.getProperty("extent.report.auto.open"))) {
                    openReportInBrowser();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to flush ExtentReports: " + e.getMessage());
        }
    }

    public static void openReportInBrowser() {
        try {
            if (reportPath != null) {
                File reportFile = new File(reportPath);
                if (reportFile.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(reportFile.toURI());
                            logger.info("ExtentReport opened in default browser: " + reportPath);
                        } else {
                            logger.warn("Browser action not supported on this system");
                        }
                    } else {
                        logger.warn("Desktop not supported on this system");
                    }
                } else {
                    logger.warn("Report file not found: " + reportPath);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to open report in browser: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error opening report: " + e.getMessage());
        }
    }

    public static String getReportPath() {
        return reportPath;
    }

    public static void addTestCategory(String category) {
        try {
            ExtentTest test = getTest();
            if (test != null && category != null && !category.isEmpty()) {
                test.assignCategory(category);
                logger.info("Test category assigned: " + category);
            }
        } catch (Exception e) {
            logger.error("Failed to add test category: " + e.getMessage());
        }
    }

    public static void addTestAuthor(String author) {
        try {
            ExtentTest test = getTest();
            if (test != null && author != null && !author.isEmpty()) {
                test.assignAuthor(author);
                logger.info("Test author assigned: " + author);
            }
        } catch (Exception e) {
            logger.error("Failed to add test author: " + e.getMessage());
        }
    }
}