package com.readys.ats.base;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;

import com.microsoft.playwright.*;
import com.readys.ats.utils.ConfigReader;
import com.readys.ats.utils.ExtentReportManager;
import com.readys.ats.utils.PlaywrightUtils;

public class BaseTest {

    private static final Logger logger =
            LogManager.getLogger(BaseTest.class);

    protected static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;

    protected static ConfigReader config;

    // ===================== SUITE =====================

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

        logger.info("===== TEST SUITE SETUP STARTED =====");

        config = ConfigReader.getInstance();
        ExtentReportManager.initializeReport();

        createDirectories();
        initializePlaywright();

        logger.info("===== TEST SUITE SETUP COMPLETED =====");
    }

    // ===================== CLASS =====================

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        logger.info("Setting up Test Class: " + getClass().getSimpleName());

        Browser.NewContextOptions options =
                new Browser.NewContextOptions();

        // 🔥 IMPORTANT → maximize browser
        options.setViewportSize(null);

        if (config.isVideoRecording()) {
            options.setRecordVideoDir(Paths.get(config.getVideoPath()))
                   .setRecordVideoSize(
                       config.getVideoWidth(),
                       config.getVideoHeight()
                   );
        }

        context = browser.newContext(options);
        page = context.newPage();

        PlaywrightUtils.setPage(page);

        page.setDefaultTimeout(config.getPageTimeout());
        page.setDefaultNavigationTimeout(
                config.getNavigationTimeout());

        logger.info("Browser context & page created");
    }

    // ===================== METHOD =====================

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {

        String testName = method.getName();

        ExtentReportManager.createTest(
                testName,
                "Automated Test Execution");

        logger.info("Starting Test: " + testName);
    }

    @AfterMethod(alwaysRun = true)
    public void captureResultScreenshot(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                String testName = result.getName();
                String screenshotPath = captureScreenshotOnFailure(testName);
                if (screenshotPath != null) {
                    ExtentReportManager.attachScreenshot(
                            screenshotPath,
                            "Failure Screenshot - " + testName
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot on test failure", e);
        }
    }

    // ===================== CLASS CLEANUP =====================

    @AfterClass(alwaysRun = true)
    public void afterClass() {

        logger.info("Tearing down Test Class: "
                + getClass().getSimpleName());

        try {
            if (page != null) {
                page.close();
            }
            if (context != null) {
                context.close();
            }
        } catch (Exception e) {
            logger.error("Class cleanup failed", e);
        }
    }

    // ===================== SUITE CLEANUP =====================

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {

        logger.info("===== TEST SUITE TEARDOWN STARTED =====");

        ExtentReportManager.flushReport();

        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }

        logger.info("===== TEST SUITE TEARDOWN COMPLETED =====");
    }

    // ===================== HELPERS =====================

    private void initializePlaywright() {

        playwright = Playwright.create();

        BrowserType.LaunchOptions options =
                new BrowserType.LaunchOptions()
                        .setHeadless(config.isHeadless())
                        .setSlowMo(config.getBrowserSlowmo());

        if (!config.isHeadless()) {
            options.setArgs(java.util.Arrays.asList(
                    "--start-maximized",
                    "--disable-web-security",
                    "--disable-features=VizDisplayCompositor"
            ));
        }

        String browserName = config.getBrowser().toLowerCase();

        switch (browserName) {
            case "firefox":
                browser = playwright.firefox().launch(options);
                break;
            case "webkit":
                browser = playwright.webkit().launch(options);
                break;
            case "edge":
                options.setChannel("msedge");
                browser = playwright.chromium().launch(options);
                break;
            case "chrome":
                options.setChannel("chrome");
                browser = playwright.chromium().launch(options);
                break;
            default:
                browser = playwright.chromium().launch(options);
        }

        logger.info("Browser launched: " + browserName);
    }

    private void createDirectories() {

        String[] paths = {
            config.getScreenshotPath(),
            config.getVideoPath(),
            config.getDownloadPath(),
            config.getExtentReportPath(),
            "test-output/logs"
        };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    protected String captureScreenshotOnFailure(String testName) {

        try {
            String timestamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(new Date());

            return PlaywrightUtils.captureScreenshot(
                    testName + "_" + timestamp);

        } catch (Exception e) {
            logger.error("Screenshot failed", e);
            return null;
        }
    }

    // ===================== GETTERS =====================

    protected Page getPage() {
        return page;
    }

    protected BrowserContext getContext() {
        return context;
    }
}
