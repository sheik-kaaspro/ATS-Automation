package com.readys.ats.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;

public class PlaywrightUtils {
    private static final Logger logger = LogManager.getLogger(PlaywrightUtils.class);
    private static ThreadLocal<Page> page = new ThreadLocal<>();
    private static ConfigReader config = ConfigReader.getInstance();

    public static void setPage(Page currentPage) {
        page.set(currentPage);
    }

    public static Page getPage() {
        return page.get();
    }

    public static void removePage() {
        page.remove();
    }

    public static String captureScreenshot(String fileName) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                logger.warn("Page is null, cannot capture screenshot");
                return null;
            }

            // Ensure screenshot directory exists
            String screenshotDir = config.getScreenshotPath();
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate screenshot path
            String format = config.getScreenshotFormat();
            String screenshotPath = screenshotDir + fileName + "." + format;

            // Capture screenshot
            Path path = Paths.get(screenshotPath);
            currentPage.screenshot(new Page.ScreenshotOptions().setPath(path).setFullPage(true));

            logger.info("Screenshot captured: " + screenshotPath);
            return screenshotPath;

        } catch (Exception e) {
            logger.error("Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        }
    }

    public static String captureElementScreenshot(String selector, String fileName) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                logger.warn("Page is null, cannot capture element screenshot");
                return null;
            }

            Locator element = currentPage.locator(selector);
            if (!isElementVisible(selector)) {
                logger.warn("Element not visible, cannot capture screenshot: " + selector);
                return null;
            }

            // Ensure screenshot directory exists
            String screenshotDir = config.getScreenshotPath();
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate screenshot path
            String format = config.getScreenshotFormat();
            String screenshotPath = screenshotDir + fileName + "_element." + format;

            // Capture element screenshot
            Path path = Paths.get(screenshotPath);
            element.screenshot(new Locator.ScreenshotOptions().setPath(path));

            logger.info("Element screenshot captured: " + screenshotPath);
            return screenshotPath;

        } catch (Exception e) {
            logger.error("Failed to capture element screenshot: " + e.getMessage(), e);
            return null;
        }
    }

    public static void waitForElement(String selector) {
        waitForElement(selector, config.getElementTimeout());
    }

    public static void waitForElement(String selector, long timeoutMs) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setTimeout(timeoutMs)
                    .setState(WaitForSelectorState.VISIBLE));

            logger.debug("Element found and visible: " + selector);

        } catch (Exception e) {
            logger.error("Failed to wait for element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Element not found or not visible: " + selector, e);
        }
    }

    public static void waitForElementToBeHidden(String selector) {
        waitForElementToBeHidden(selector, config.getElementTimeout());
    }

    public static void waitForElementToBeHidden(String selector, long timeoutMs) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setTimeout(timeoutMs)
                    .setState(WaitForSelectorState.HIDDEN));

            logger.debug("Element is hidden: " + selector);

        } catch (Exception e) {
            logger.error("Failed to wait for element to be hidden: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Element is still visible: " + selector, e);
        }
    }

    public static boolean isElementVisible(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                return false;
            }

            return currentPage.locator(selector).isVisible();

        } catch (Exception e) {
            logger.debug("Element not visible: " + selector);
            return false;
        }
    }

    public static boolean isElementEnabled(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                return false;
            }

            return currentPage.locator(selector).isEnabled();

        } catch (Exception e) {
            logger.debug("Element not enabled: " + selector);
            return false;
        }
    }

    public static void clickElement(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).click();
            logger.info("Clicked element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to click element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to click element: " + selector, e);
        }
    }

    public static void doubleClickElement(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).dblclick();
            logger.info("Double clicked element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to double click element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to double click element: " + selector, e);
        }
    }

    public static void rightClickElement(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
            logger.info("Right clicked element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to right click element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to right click element: " + selector, e);
        }
    }

    public static void typeText(String selector, String text) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).clear();
            currentPage.locator(selector).type(text);
            logger.info("Typed text in element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to type text in element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to type text in element: " + selector, e);
        }
    }

    public static void fillText(String selector, String text) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).fill(text);
            logger.info("Filled text in element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to fill text in element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to fill text in element: " + selector, e);
        }
    }

    public static String getText(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            String text = currentPage.locator(selector).textContent();
            logger.debug("Retrieved text from element: " + selector + " - Text: " + text);
            return text != null ? text.trim() : "";

        } catch (Exception e) {
            logger.error("Failed to get text from element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to get text from element: " + selector, e);
        }
    }

    public static String getAttribute(String selector, String attributeName) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            String attributeValue = currentPage.locator(selector).getAttribute(attributeName);
            logger.debug("Retrieved attribute from element: " + selector + " - Attribute: " + attributeName + " - Value: " + attributeValue);
            return attributeValue != null ? attributeValue.trim() : "";

        } catch (Exception e) {
            logger.error("Failed to get attribute from element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to get attribute from element: " + selector, e);
        }
    }

    public static void selectFromDropdown(String selector, String optionText) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).selectOption(optionText);
            logger.info("Selected option from dropdown: " + selector + " - Option: " + optionText);

        } catch (Exception e) {
            logger.error("Failed to select option from dropdown: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to select option from dropdown: " + selector, e);
        }
    }

    public static void checkCheckbox(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).check();
            logger.info("Checked checkbox: " + selector);

        } catch (Exception e) {
            logger.error("Failed to check checkbox: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to check checkbox: " + selector, e);
        }
    }

    public static void uncheckCheckbox(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).uncheck();
            logger.info("Unchecked checkbox: " + selector);

        } catch (Exception e) {
            logger.error("Failed to uncheck checkbox: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to uncheck checkbox: " + selector, e);
        }
    }

    public static boolean isCheckboxChecked(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            return currentPage.locator(selector).isChecked();

        } catch (Exception e) {
            logger.error("Failed to check if checkbox is checked: " + selector + ". Error: " + e.getMessage());
            return false;
        }
    }

    public static void uploadFile(String selector, String filePath) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + filePath);
            }

            waitForElement(selector);
            currentPage.locator(selector).setInputFiles(Paths.get(filePath));
            logger.info("Uploaded file: " + filePath + " to element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to upload file: " + filePath + " to element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to upload file to element: " + selector, e);
        }
    }

    public static void scrollToElement(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.locator(selector).scrollIntoViewIfNeeded();
            logger.info("Scrolled to element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to scroll to element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to scroll to element: " + selector, e);
        }
    }

    public static void hoverOverElement(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            waitForElement(selector);
            currentPage.locator(selector).hover();
            logger.info("Hovered over element: " + selector);

        } catch (Exception e) {
            logger.error("Failed to hover over element: " + selector + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to hover over element: " + selector, e);
        }
    }

    public static int getElementCount(String selector) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            int count = currentPage.locator(selector).count();
            logger.debug("Element count for selector: " + selector + " - Count: " + count);
            return count;

        } catch (Exception e) {
            logger.error("Failed to get element count: " + selector + ". Error: " + e.getMessage());
            return 0;
        }
    }

    public static void pressKey(String key) {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.keyboard().press(key);
            logger.info("Pressed key: " + key);

        } catch (Exception e) {
            logger.error("Failed to press key: " + key + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to press key: " + key, e);
        }
    }

    public static void waitForPageLoad() {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.waitForLoadState(LoadState.LOAD);
            logger.info("Page loaded successfully");

        } catch (Exception e) {
            logger.error("Failed to wait for page load. Error: " + e.getMessage());
            throw new RuntimeException("Page did not load within timeout", e);
        }
    }

    public static void waitForNetworkIdle() {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.waitForLoadState(LoadState.NETWORKIDLE);
            logger.info("Network is idle");

        } catch (Exception e) {
            logger.error("Failed to wait for network idle. Error: " + e.getMessage());
            throw new RuntimeException("Network did not become idle within timeout", e);
        }
    }

    public static String getCurrentUrl() {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            String url = currentPage.url();
            logger.debug("Current URL: " + url);
            return url;

        } catch (Exception e) {
            logger.error("Failed to get current URL. Error: " + e.getMessage());
            throw new RuntimeException("Failed to get current URL", e);
        }
    }

    public static String getPageTitle() {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            String title = currentPage.title();
            logger.debug("Page title: " + title);
            return title;

        } catch (Exception e) {
            logger.error("Failed to get page title. Error: " + e.getMessage());
            throw new RuntimeException("Failed to get page title", e);
        }
    }

    public static void navigateBack() {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.goBack();
            logger.info("Navigated back");

        } catch (Exception e) {
            logger.error("Failed to navigate back. Error: " + e.getMessage());
            throw new RuntimeException("Failed to navigate back", e);
        }
    }

    public static void refreshPage() {
        try {
            Page currentPage = getPage();
            if (currentPage == null) {
                throw new RuntimeException("Page is null");
            }

            currentPage.reload();
            logger.info("Page refreshed");

        } catch (Exception e) {
            logger.error("Failed to refresh page. Error: " + e.getMessage());
            throw new RuntimeException("Failed to refresh page", e);
        }
    }

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            logger.debug("Slept for " + milliseconds + " milliseconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted");
        }
    }
    
   
    
}