package com.readys.ats.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;

import com.readys.ats.base.BaseTest;
import com.readys.ats.pages.LoginPage;
import com.readys.ats.utils.ExtentReportManager;

public class LoginTests extends BaseTest {

    private static final Logger logger =
            LogManager.getLogger(LoginTests.class);

    private LoginPage loginPage;

    // ================= CLASS SETUP =================

    @BeforeClass(alwaysRun = true)
    public void setUpLoginClass() {
        try {
            loginPage = new LoginPage(getPage());
            logger.info("LoginPage initialized once for LoginTests");
        } catch (Exception e) {
            throw new RuntimeException("LoginPage init failed", e);
        }
    }

    // ================= RESET BEFORE EACH TEST =================

    @BeforeMethod(alwaysRun = true)
    public void navigateToLoginPage() {
        try {
            getPage().navigate(config.getAppUrl());
            logger.info("Navigated to Login page before test");
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to login page", e);
        }
    }

    // ================= TESTS =================

    @Test(
        groups = {"smoke", "login", "critical"},
        description = "Verify user can login with valid credentials",
        priority = 1
    )
    public void testValidLogin() {
        try {
            loginPage.verifyPageLoaded();
            loginPage.verifyLoginFormDisplayed();

            loginPage.loginWithValidCredentials();
            loginPage.verifyLoginSuccessful();

            ExtentReportManager.logPass("Valid login successful");

        } catch (Exception e) {
            logger.error("Valid login failed", e);
            ExtentReportManager.logFail("Valid login failed: " + e.getMessage());
            attachFailureScreenshot("testValidLogin");
            throw e;
        }
    }

    @Test(
        groups = {"login", "negative"},
        description = "Verify user cannot login with invalid credentials",
        priority = 2
    )
    public void testInvalidLogin() {
        try {
            loginPage.verifyPageLoaded();
            loginPage.verifyLoginFormDisplayed();

            loginPage.loginWithInvalidCredentials();
            loginPage.verifyLoginFailed();

            ExtentReportManager.logPass("Invalid login validation successful");

        } catch (Exception e) {
            logger.error("Invalid login test failed", e);
            ExtentReportManager.logFail("Invalid login failed: " + e.getMessage());
            attachFailureScreenshot("testInvalidLogin");
            throw e;
        }
    }

    @Test(
        groups = {"login", "validation"},
        description = "Verify validation when login fields are empty",
        priority = 3
    )
    public void testEmptyFieldsLogin() {
        try {
            loginPage.verifyPageLoaded();
            loginPage.clearAllFields();

            org.testng.Assert.assertTrue(
                loginPage.isLoginButtonDisabled(),
                "Login button should be disabled"
            );

            ExtentReportManager.logPass("Empty field validation successful");

        } catch (Exception e) {
            logger.error("Empty fields validation failed", e);
            ExtentReportManager.logFail("Empty fields login failed");
            attachFailureScreenshot("testEmptyFieldsLogin");
            throw e;
        }
    }

    @Test(
        groups = {"login", "navigation"},
        description = "Verify Forgot Password navigation",
        priority = 4
    )
    public void testForgotPasswordNavigation() {
        try {
            loginPage.verifyPageLoaded();
            loginPage.clickForgotPasswordLink();

            String currentUrl = getPage().url().toLowerCase();
            org.testng.Assert.assertTrue(
                currentUrl.contains("forgot") || currentUrl.contains("reset"),
                "Forgot password page not opened"
            );

            ExtentReportManager.logPass("Forgot password navigation successful");

        } catch (Exception e) {
            logger.error("Forgot password navigation failed", e);
            ExtentReportManager.logFail("Forgot password navigation failed");
            attachFailureScreenshot("testForgotPasswordNavigation");
            throw e;
        }
    }

    // ================= HELPER =================

    private void attachFailureScreenshot(String testName) {
        String path = captureScreenshotOnFailure(testName);
        if (path != null) {
            ExtentReportManager.attachScreenshot(path, testName + " Failure");
        }
    }
}
