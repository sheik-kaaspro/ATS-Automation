package com.readys.ats.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Page;
import com.readys.ats.utils.ConfigReader;
import com.readys.ats.utils.ExtentReportManager;
import com.readys.ats.utils.PlaywrightUtils;

public abstract class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected Page page;
    protected ConfigReader config;

    public BasePage(Page page) {
        this.page = page;
        this.config = ConfigReader.getInstance();
    }

    // Basic element interaction methods
    protected void click(String selector) {
        try {
            logger.info("Clicking element: " + selector);
            PlaywrightUtils.clickElement(selector);
        } catch (Exception e) {
            String error = "Failed to click element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void doubleClick(String selector) {
        try {
            logger.info("Double clicking element: " + selector);
            PlaywrightUtils.doubleClickElement(selector);
            ExtentReportManager.logInfo("Double clicked element: " + selector);
        } catch (Exception e) {
            String error = "Failed to double click element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void rightClick(String selector) {
        try {
            logger.info("Right clicking element: " + selector);
            PlaywrightUtils.rightClickElement(selector);
            ExtentReportManager.logInfo("Right clicked element: " + selector);
        } catch (Exception e) {
            String error = "Failed to right click element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void type(String selector, String text) {
        try {
            logger.info("Typing text in element: " + selector);
            PlaywrightUtils.typeText(selector, text);
            ExtentReportManager.logInfo("Typed text in element: " + selector);
        } catch (Exception e) {
            String error = "Failed to type text in element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void fill(String selector, String text) {
        try {
            logger.info("Filling text in element: " + selector);
            PlaywrightUtils.fillText(selector, text);
        } catch (Exception e) {
            String error = "Failed to fill text in element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected String getText(String selector) {
        try {
            logger.debug("Getting text from element: " + selector);
            String text = PlaywrightUtils.getText(selector);
            return text;
        } catch (Exception e) {
            String error = "Failed to get text from element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected String getAttribute(String selector, String attributeName) {
        try {
            logger.debug("Getting attribute from element: " + selector + " - Attribute: " + attributeName);
            String value = PlaywrightUtils.getAttribute(selector, attributeName);
            return value;
        } catch (Exception e) {
            String error = "Failed to get attribute from element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void selectFromDropdown(String selector, String optionText) {
        try {
            logger.info("Selecting option from dropdown: " + selector + " - Option: " + optionText);
            PlaywrightUtils.selectFromDropdown(selector, optionText);
            ExtentReportManager.logInfo("Selected option from dropdown: " + selector + " - Option: " + optionText);
        } catch (Exception e) {
            String error = "Failed to select option from dropdown: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void checkCheckbox(String selector) {
        try {
            logger.info("Checking checkbox: " + selector);
            PlaywrightUtils.checkCheckbox(selector);
            ExtentReportManager.logInfo("Checked checkbox: " + selector);
        } catch (Exception e) {
            String error = "Failed to check checkbox: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void uncheckCheckbox(String selector) {
        try {
            logger.info("Unchecking checkbox: " + selector);
            PlaywrightUtils.uncheckCheckbox(selector);
            ExtentReportManager.logInfo("Unchecked checkbox: " + selector);
        } catch (Exception e) {
            String error = "Failed to uncheck checkbox: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected boolean isCheckboxChecked(String selector) {
        try {
            logger.debug("Checking if checkbox is checked: " + selector);
            boolean checked = PlaywrightUtils.isCheckboxChecked(selector);
            ExtentReportManager.logInfo("Checkbox " + selector + " checked status: " + checked);
            return checked;
        } catch (Exception e) {
            String error = "Failed to check if checkbox is checked: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            return false;
        }
    }

    protected void uploadFile(String selector, String filePath) {
        try {
            logger.info("Uploading file: " + filePath + " to element: " + selector);
            PlaywrightUtils.uploadFile(selector, filePath);
            ExtentReportManager.logInfo("Uploaded file: " + filePath + " to element: " + selector);
        } catch (Exception e) {
            String error = "Failed to upload file: " + filePath + " to element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    // Element state checking methods
    protected boolean isElementVisible(String selector) {
        try {
            boolean visible = PlaywrightUtils.isElementVisible(selector);
            logger.debug("Element visibility for " + selector + ": " + visible);
            return visible;
        } catch (Exception e) {
            logger.warn("Failed to check element visibility: " + selector + ". Error: " + e.getMessage());
            return false;
        }
    }

    protected boolean isElementEnabled(String selector) {
        try {
            boolean enabled = PlaywrightUtils.isElementEnabled(selector);
            logger.debug("Element enabled status for " + selector + ": " + enabled);
            return enabled;
        } catch (Exception e) {
            logger.warn("Failed to check element enabled status: " + selector + ". Error: " + e.getMessage());
            return false;
        }
    }

    protected int getElementCount(String selector) {
        try {
            int count = PlaywrightUtils.getElementCount(selector);
            logger.debug("Element count for " + selector + ": " + count);
            return count;
        } catch (Exception e) {
            logger.warn("Failed to get element count: " + selector + ". Error: " + e.getMessage());
            return 0;
        }
    }

    // Wait methods
    protected void waitForElement(String selector) {
        try {
            logger.debug("Waiting for element: " + selector);
            PlaywrightUtils.waitForElement(selector);
        } catch (Exception e) {
            String error = "Element not found or not visible: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void waitForElement(String selector, long timeoutMs) {
        try {
            logger.debug("Waiting for element: " + selector + " with timeout: " + timeoutMs + "ms");
            PlaywrightUtils.waitForElement(selector, timeoutMs);
        } catch (Exception e) {
            String error = "Element not found or not visible: " + selector + " within " + timeoutMs + "ms. Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void waitForElementToBeHidden(String selector) {
        try {
            logger.debug("Waiting for element to be hidden: " + selector);
            PlaywrightUtils.waitForElementToBeHidden(selector);
        } catch (Exception e) {
            String error = "Element is still visible: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    // Scroll and hover methods
    protected void scrollToElement(String selector) {
        try {
            logger.debug("Scrolling to element: " + selector);
            PlaywrightUtils.scrollToElement(selector);
            ExtentReportManager.logInfo("Scrolled to element: " + selector);
        } catch (Exception e) {
            String error = "Failed to scroll to element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void hoverOverElement(String selector) {
        try {
            logger.info("Hovering over element: " + selector);
            PlaywrightUtils.hoverOverElement(selector);
            ExtentReportManager.logInfo("Hovered over element: " + selector);
        } catch (Exception e) {
            String error = "Failed to hover over element: " + selector + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    // Keyboard methods
    protected void pressKey(String key) {
        try {
            logger.info("Pressing key: " + key);
            PlaywrightUtils.pressKey(key);
            ExtentReportManager.logInfo("Pressed key: " + key);
        } catch (Exception e) {
            String error = "Failed to press key: " + key + ". Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void pressEnter() {
        pressKey("Enter");
    }

    protected void pressTab() {
        pressKey("Tab");
    }

    protected void pressEscape() {
        pressKey("Escape");
    }

    // Page navigation methods
    protected void waitForPageLoad() {
        try {
            logger.debug("Waiting for page to load");
            PlaywrightUtils.waitForPageLoad();
            ExtentReportManager.logInfo("Page loaded successfully");
        } catch (Exception e) {
            String error = "Page did not load within timeout. Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void waitForNetworkIdle() {
        try {
            logger.debug("Waiting for network to be idle");
            PlaywrightUtils.waitForNetworkIdle();
            ExtentReportManager.logInfo("Network is idle");
        } catch (Exception e) {
            String error = "Network did not become idle within timeout. Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected String getCurrentUrl() {
        try {
            String url = PlaywrightUtils.getCurrentUrl();
            logger.debug("Current URL: " + url);
            return url;
        } catch (Exception e) {
            String error = "Failed to get current URL. Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected String getPageTitle() {
        try {
            String title = PlaywrightUtils.getPageTitle();
            logger.debug("Page title: " + title);
            return title;
        } catch (Exception e) {
            String error = "Failed to get page title. Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void navigateBack() {
        try {
            logger.info("Navigating back");
            PlaywrightUtils.navigateBack();
            ExtentReportManager.logInfo("Navigated back");
        } catch (Exception e) {
            String error = "Failed to navigate back. Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    protected void refreshPage() {
        try {
            logger.info("Refreshing page");
            PlaywrightUtils.refreshPage();
            ExtentReportManager.logInfo("Page refreshed");
        } catch (Exception e) {
            String error = "Failed to refresh page. Error: " + e.getMessage();
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new RuntimeException(error, e);
        }
    }

    // Screenshot methods
    protected String captureScreenshot(String fileName) {
        try {
            String screenshotPath = PlaywrightUtils.captureScreenshot(fileName);
            if (screenshotPath != null) {
                logger.info("Screenshot captured: " + screenshotPath);
                ExtentReportManager.logInfo("Screenshot captured: " + fileName);
                return screenshotPath;
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    protected String captureElementScreenshot(String selector, String fileName) {
        try {
            String screenshotPath = PlaywrightUtils.captureElementScreenshot(selector, fileName);
            if (screenshotPath != null) {
                logger.info("Element screenshot captured: " + screenshotPath);
                ExtentReportManager.logInfo("Element screenshot captured: " + fileName);
                return screenshotPath;
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to capture element screenshot: " + e.getMessage());
            return null;
        }
    }

    // Assertion methods
    protected void verifyElementVisible(String selector) {
        if (!isElementVisible(selector)) {
            String error = "Element is not visible: " + selector;
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new AssertionError(error);
        }
        logger.info("Element is visible: " + selector);
    }

    protected void verifyElementNotVisible(String selector) {
        if (isElementVisible(selector)) {
            String error = "Element is visible but should not be: " + selector;
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new AssertionError(error);
        }
        logger.info("Element is not visible as expected: " + selector);
        ExtentReportManager.logPass("Element is not visible as expected: " + selector);
    }

    protected void verifyElementEnabled(String selector) {
        if (!isElementEnabled(selector)) {
            String error = "Element is not enabled: " + selector;
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new AssertionError(error);
        }
        logger.info("Element is enabled: " + selector);
    }

    protected void verifyElementDisabled(String selector) {
        if (isElementEnabled(selector)) {
            String error = "Element is enabled but should be disabled: " + selector;
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new AssertionError(error);
        }
        logger.info("Element is disabled as expected: " + selector);
        ExtentReportManager.logPass("Element is disabled as expected: " + selector);
    }

    protected void verifyText(String selector, String expectedText) {
        String actualText = getText(selector);
        if (!actualText.equals(expectedText)) {
            String error = "Text mismatch for element: " + selector + ". Expected: '" + expectedText + "', Actual: '" + actualText + "'";
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new AssertionError(error);
        }
        logger.info("Text verified for element: " + selector + " - Text: " + expectedText);
        ExtentReportManager.logPass("Text verified for element: " + selector + " - Text: " + expectedText);
    }

    protected void verifyTextContains(String selector, String expectedText) {
        String actualText = getText(selector);
        if (!actualText.contains(expectedText)) {
            String error = "Text does not contain expected text for element: " + selector + ". Expected to contain: '" + expectedText + "', Actual: '" + actualText + "'";
            logger.error(error);
            ExtentReportManager.logFail(error);
            throw new AssertionError(error);
        }
        logger.info("Text contains expected text for element: " + selector + " - Contains: " + expectedText);
        ExtentReportManager.logPass("Text contains expected text for element: " + selector + " - Contains: " + expectedText);
    }

    // Utility methods
    protected void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Slept for " + seconds + " seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted");
        }
    }

    protected void logInfo(String message) {
        logger.info(message);
    }

    protected void logPass(String message) {
        logger.info("PASS: " + message);
        ExtentReportManager.logPass(message);
    }

    protected void logFail(String message) {
        logger.error("FAIL: " + message);
        ExtentReportManager.logFail(message);
    }

    // Abstract methods to be implemented by specific page classes
    public abstract void verifyPageLoaded();
}