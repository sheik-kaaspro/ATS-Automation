package com.readys.ats.base;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.readys.ats.utils.ConfigReader;
import com.readys.ats.utils.ExtentReportManager;
import com.readys.ats.utils.PlaywrightUtils;

public class BaseTest {
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    protected static Playwright playwright;
    protected static Browser browser;
    protected static ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    protected static ThreadLocal<Page> page = new ThreadLocal<>();

    protected static ConfigReader config;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        try {
            logger.info("=================================================================");
            logger.info("Setting up Test Suite");
            logger.info("=================================================================");

            // Initialize configuration
            config = ConfigReader.getInstance();
            
            ExtentReportManager.initializeReport();

            // Create necessary directories
            createDirectories();

            // Initialize Playwright
            initializePlaywright();

            logger.info("Test Suite setup completed successfully");

        } catch (Exception e) {
            logger.error("Failed to setup Test Suite: " + e.getMessage(), e);
            throw new RuntimeException("Suite setup failed", e);
        }
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        try {
            logger.info("Setting up Test Class: " + this.getClass().getSimpleName());

        } catch (Exception e) {
            logger.error("Failed to setup Test Class: " + e.getMessage(), e);
            throw new RuntimeException("Class setup failed", e);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpTest(Method method) {
        try {
        	
        	String testName = method.getName();
        	logger.info("Setting up test: " + testName);

        	// Create Extent Report Test Entry
        	ExtentReportManager.createTest(testName, "Automated Test Execution");


            // Create browser context
            createBrowserContext();

            // Create page
            createPage();

            // Navigate to application URL
            String appUrl = config.getAppUrl();
            if (appUrl != null && !appUrl.isEmpty()) {
                page.get().navigate(appUrl);
                PlaywrightUtils.waitForPageLoad();
                logger.info("Navigated to: " + appUrl);

                // Log navigation in ExtentReport
                ExtentReportManager.logInfo("Navigated to application URL: " + appUrl);
            }

        } catch (Exception e) {
            logger.error("Failed to setup test: " + e.getMessage(), e);
            captureScreenshotOnFailure("SetupFailure");
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTest(Method method) {
        try {
            String testName = method.getName();
            logger.info("Tearing down test: " + testName);

            // Close page if exists
            if (page.get() != null) {
                page.get().close();
                PlaywrightUtils.removePage();
                page.remove();
                logger.debug("Page closed for test: " + testName);
            }

            // Close context if exists
            if (context.get() != null) {
                context.get().close();
                context.remove();
                logger.debug("Browser context closed for test: " + testName);
            }

        } catch (Exception e) {
            logger.error("Failed to teardown test: " + e.getMessage(), e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        try {
            logger.info("Tearing down Test Class: " + this.getClass().getSimpleName());

        } catch (Exception e) {
            logger.error("Failed to teardown Test Class: " + e.getMessage(), e);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        try {
            logger.info("=================================================================");
            logger.info("Tearing down Test Suite");
            logger.info("=================================================================");

            
            ExtentReportManager.flushReport();
            
            // Close browser
            if (browser != null) {
                browser.close();
                logger.info("Browser closed");
            }

            // Close playwright
            if (playwright != null) {
                playwright.close();
                logger.info("Playwright closed");
            }

            logger.info("Test Suite teardown completed");

        } catch (Exception e) {
            logger.error("Failed to teardown Test Suite: " + e.getMessage(), e);
        }
    }

    private void initializePlaywright() {
        try {
            logger.info("Initializing Playwright...");

            // Create Playwright instance
            playwright = Playwright.create();

            // Get browser type and options
            String browserName = config.getBrowser().toLowerCase();
            boolean headless = config.isHeadless();

            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setSlowMo(config.getBrowserSlowmo());

            // Add additional launch options
            if (!headless) {
                launchOptions.setArgs(java.util.Arrays.asList(
                    "--start-maximized",
                    "--disable-web-security",
                    "--disable-features=VizDisplayCompositor"
                ));
            }

            // Launch browser based on configuration
            switch (browserName) {
                case "chromium":
                    browser = playwright.chromium().launch(launchOptions);
                    break;
                case "firefox":
                    browser = playwright.firefox().launch(launchOptions);
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(launchOptions);
                    break;
                case "chrome":
                    launchOptions.setChannel("chrome");
                    browser = playwright.chromium().launch(launchOptions);
                    break;
                case "edge":
                    launchOptions.setChannel("msedge");
                    browser = playwright.chromium().launch(launchOptions);
                    break;
                default:
                    logger.warn("Unknown browser: " + browserName + ". Defaulting to Chromium");
                    browser = playwright.chromium().launch(launchOptions);
            }

            logger.info("Playwright initialized successfully with browser: " + browserName +
                       " (headless: " + headless + ")");

        } catch (Exception e) {
            logger.error("Failed to initialize Playwright: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Playwright", e);
        }
    }

    private void createBrowserContext() {
        try {
            // Create context options
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

            // Set viewport to null to use full screen (maximized window)
            if (!config.isHeadless()) {
                contextOptions.setViewportSize(null); // This allows browser to use full screen
            } else {
                contextOptions.setViewportSize(config.getBrowserWidth(), config.getBrowserHeight());
            }

            // Configure video recording if enabled
            if (config.isVideoRecording()) {
                String videoDir = config.getVideoPath();
                File videoDirFile = new File(videoDir);
                if (!videoDirFile.exists()) {
                    videoDirFile.mkdirs();
                }

                contextOptions.setRecordVideoDir(Paths.get(videoDir))
                             .setRecordVideoSize(config.getVideoWidth(), config.getVideoHeight());
            }

            // Browser context options configured
            // Note: Timeouts will be set on individual pages

            // Create browser context
            BrowserContext browserContext = browser.newContext(contextOptions);
            context.set(browserContext);

            logger.debug("Browser context created successfully");

        } catch (Exception e) {
            logger.error("Failed to create browser context: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create browser context", e);
        }
    }

    private void createPage() {
        try {
            // Create page from context
            Page newPage = context.get().newPage();
            page.set(newPage);

            // Set page in PlaywrightUtils for global access
            PlaywrightUtils.setPage(newPage);

            // Set page timeout
            newPage.setDefaultTimeout(config.getPageTimeout());
            newPage.setDefaultNavigationTimeout(config.getNavigationTimeout());

            logger.debug("Page created successfully");

        } catch (Exception e) {
            logger.error("Failed to create page: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create page", e);
        }
    }

    private void createDirectories() {
        try {
            // Create all necessary directories
            String[] directories = {
                config.getScreenshotPath(),
                config.getVideoPath(),
                config.getDownloadPath(),
                config.getExtentReportPath(),
                "test-output/logs/"
            };

            for (String dirPath : directories) {
                File directory = new File(dirPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                    logger.debug("Created directory: " + dirPath);
                }
            }

        } catch (Exception e) {
            logger.error("Failed to create directories: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create directories", e);
        }
    }

    protected String captureScreenshotOnFailure(String testName) {
        try {
            if (config.isScreenshotOnFailure()) {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = testName + "_FAILURE_" + timestamp;
                return PlaywrightUtils.captureScreenshot(fileName);
            }
        } catch (Exception e) {
            logger.error("Failed to capture failure screenshot: " + e.getMessage());
        }
        return null;
    }

    protected String captureScreenshotOnSuccess(String testName) {
        try {
            if (config.isScreenshotOnSuccess()) {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = testName + "_SUCCESS_" + timestamp;
                return PlaywrightUtils.captureScreenshot(fileName);
            }
        } catch (Exception e) {
            logger.error("Failed to capture success screenshot: " + e.getMessage());
        }
        return null;
    }

    // Utility methods for tests
    protected Page getPage() {
        return page.get();
    }

    protected BrowserContext getContext() {
        return context.get();
    }

    protected void navigateTo(String url) {
        try {
            getPage().navigate(url);
            PlaywrightUtils.waitForPageLoad();
            logger.info("Navigated to: " + url);
            ExtentReportManager.logInfo("Navigated to: " + url);
        } catch (Exception e) {
            logger.error("Failed to navigate to: " + url + ". Error: " + e.getMessage());
            ExtentReportManager.logFail("Failed to navigate to: " + url);
            throw e;
        }
    }

    protected void verifyPageTitle(String expectedTitle) {
        try {
            String actualTitle = getPage().title();
            if (actualTitle.contains(expectedTitle)) {
                logger.info("Page title verified: " + actualTitle);
                ExtentReportManager.logPass("Page title verified: " + actualTitle);
            } else {
                logger.error("Page title mismatch. Expected: " + expectedTitle + ", Actual: " + actualTitle);
                ExtentReportManager.logFail("Page title mismatch. Expected: " + expectedTitle + ", Actual: " + actualTitle);
                throw new AssertionError("Page title mismatch");
            }
        } catch (Exception e) {
            logger.error("Failed to verify page title: " + e.getMessage());
            ExtentReportManager.logFail("Failed to verify page title: " + e.getMessage());
            throw e;
        }
    }

    protected void verifyCurrentUrl(String expectedUrl) {
        try {
            String actualUrl = getPage().url();
            if (actualUrl.contains(expectedUrl)) {
                logger.info("Current URL verified: " + actualUrl);
                ExtentReportManager.logPass("Current URL verified: " + actualUrl);
            } else {
                logger.error("URL mismatch. Expected: " + expectedUrl + ", Actual: " + actualUrl);
                ExtentReportManager.logFail("URL mismatch. Expected: " + expectedUrl + ", Actual: " + actualUrl);
                throw new AssertionError("URL mismatch");
            }
        } catch (Exception e) {
            logger.error("Failed to verify current URL: " + e.getMessage());
            ExtentReportManager.logFail("Failed to verify current URL: " + e.getMessage());
            throw e;
        }
    }

    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Waited for " + seconds + " seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted");
        }
    }
}