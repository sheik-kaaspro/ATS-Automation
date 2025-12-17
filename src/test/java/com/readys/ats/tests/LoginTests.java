package com.readys.ats.tests;

import com.aventstack.extentreports.util.Assert;
import com.readys.ats.base.BaseTest;
import com.readys.ats.pages.LoginPage;
import com.readys.ats.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
    private static final Logger logger = LogManager.getLogger(LoginTests.class);
    private LoginPage loginPage;
    
    

    @BeforeMethod(alwaysRun = true)
    public void setUpLoginTests() {
        try {
            loginPage = new LoginPage(getPage());
            logger.info("LoginPage initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize LoginPage: " + e.getMessage(), e);
            ExtentReportManager.logFail("Failed to initialize LoginPage: " + e.getMessage());
            throw new RuntimeException("LoginPage initialization failed", e);
        }
    }

    @Test(
        groups = {"smoke", "login", "critical"},
        description = "Verify user can login with valid credentials from config.properties",
        priority = 1
    )
    public void testValidLogin() {
        try {
            loginPage.verifyPageLoaded();
            loginPage.verifyLoginFormDisplayed();

            String testEmail = config.getTestEmail();
            String testPassword = config.getTestPassword();

            if (testEmail.isEmpty() || testPassword.isEmpty()) {
                throw new RuntimeException("Test credentials not configured in config.properties");
            }

            loginPage.loginWithValidCredentials();
            loginPage.verifyLoginSuccessful();

            ExtentReportManager.logPass("Valid login test completed successfully");

        } catch (Exception e) {
            logger.error("Test testValidLogin failed: " + e.getMessage(), e);
            ExtentReportManager.logFail("Valid login test failed: " + e.getMessage());

            String screenshotPath = captureScreenshotOnFailure("testValidLogin");
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Login Failure Screenshot");
            }
            throw e;
        }
    }

    @Test(
        groups = {"smoke", "login", "negative"},
        description = "Verify user cannot login with invalid credentials",
        priority = 2
    )
    public void testInvalidLogin() {
        try {
            loginPage.verifyPageLoaded();
            loginPage.verifyLoginFormDisplayed();

            loginPage.loginWithInvalidCredentials();
            loginPage.verifyLoginFailed();

            ExtentReportManager.logPass("Invalid login test completed successfully");

        } catch (Exception e) {
            logger.error("Test testInvalidLogin failed: " + e.getMessage(), e);
            ExtentReportManager.logFail("Invalid login test failed: " + e.getMessage());

            String screenshotPath = captureScreenshotOnFailure("testInvalidLogin");
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Invalid Login Failure Screenshot");
            }
            throw e;
        }
    }

    @Test(
        groups = {"login", "validation", "negative"},
        description = "Verify validation when trying to login with empty fields",
        priority = 3
    )
    public void testEmptyFieldsLogin() {
        try {
            loginPage.verifyPageLoaded();
            loginPage.clearAllFields();
            // Button should be disabled → TRUE
            org.testng.Assert.assertTrue(
                loginPage.isLoginButtonDisabled(),
                "Login button should be disabled when email and password are empty"
            );
            ExtentReportManager.logPass("Empty fields login test completed successfully");

        } catch (Exception e) {
            logger.error("Test testEmptyFieldsLogin failed: " + e.getMessage(), e);
            ExtentReportManager.logFail("Empty fields login test failed: " + e.getMessage());

            String screenshotPath = captureScreenshotOnFailure("testEmptyFieldsLogin");
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Empty Fields Login Failure Screenshot");
            }
            throw e;
        }
    }
    
    @Test(groups = {"login", "validation", "negative"}, description = "Verify Forgot Password Page navigation", priority = 4)
    public void testFogotPasswordNavigation() {
    	
    	try {
    		
    		loginPage.verifyPageLoaded();
        	loginPage.clickForgotPasswordLink();
        	
        	String currentUrl = page.get().url().toLowerCase();
        	
        	org.testng.Assert.assertTrue(currentUrl.contains("forgot") || currentUrl.contains("reset"), "Forgot Password page did not open. Current URL: " + currentUrl);
        	
        	ExtentReportManager.logPass("Forgot password navigation test completed successfully");
        	
        	
			
		} catch (Exception e) {
			
			logger.error("forgot password navigation failed:"+e.getMessage(), e);
			ExtentReportManager.logFail("Forgot Password Test Failed :"+ e.getMessage());
			
			 String screenshotPath = captureScreenshotOnFailure("testForgotPasswordNavigation");
	            if (screenshotPath != null) {
	                ExtentReportManager.attachScreenshot(screenshotPath, "Forgot Password Failure Screenshot");
	            }
	            throw e;
			
		}
    }
    
   
    
    
    
    
    
    
    
}