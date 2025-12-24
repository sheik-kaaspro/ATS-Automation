package com.readys.ats.tests;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.readys.ats.base.BaseTest;
import com.readys.ats.pages.LoginPage;
import com.readys.ats.pages.SystemSettingsPage;
import com.readys.ats.utils.ExtentReportManager;
import com.readys.ats.utils.PlaywrightUtils;

public class SystemSettingsTest  extends BaseTest{
	
	private static final Logger logger = LogManager.getLogger(SystemSettingsTest.class);

    private LoginPage loginPage;
    private SystemSettingsPage settingsPage;

    // Helper to generate random name
    private String generateRandomName() {
        return "Admin User " + UUID.randomUUID().toString().substring(0, 5);
    }
    
    private String generateRandomTemplateName() { return "Template_" + UUID.randomUUID().toString().substring(0, 6); }
    
    @BeforeClass(alwaysRun = true)
    public void loginAndNavigate() {
        try {
            logger.info("===== SystemSettingsTest : SETUP =====");

            // 1. Navigate to URL
            String appUrl = config.getAppUrl(); 
            // Assuming settings are accessible from the main dashboard/organizations page
            // If the URL is different, adjust here.
            getPage().navigate(appUrl);

            loginPage = new LoginPage(getPage());
            settingsPage = new SystemSettingsPage(getPage());

            // 2. Login if needed
            if (getPage().url().contains("login")) {
                loginPage.loginWithValidCredentials();
                // Wait for dashboard to load
                getPage().waitForURL(url -> !url.contains("login"));
            }
            
            getPage().waitForLoadState();
            ExtentReportManager.logPass("Login successful and navigated to Dashboard");

        } catch (Exception e) {
            logger.error("Login failed in SystemSettingsTest", e);
            ExtentReportManager.logFail("Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }
    
    @Test(priority = 1, groups = {"settings", "ui"})
    public void testUpdateProfileFullName() {
        String newName = generateRandomName();
        
        try {
            logger.info("Starting Test: Update Profile Full Name");
            
            // 1. Open Settings Modal
            settingsPage.clickSettingsButton();
            settingsPage.waitForSettingsModal();
            ExtentReportManager.logInfo("Settings modal opened");

            // 2. Ensure we are on 'My Profile' tab
            settingsPage.navigateToMyProfileTab();

            // 3. Update Name
          int statusCode = settingsPage.updateFullName(newName);
       // 4. Verify Backend Status (200 OK)
          if (statusCode == 200 || statusCode == 204) {
              ExtentReportManager.logPass("Backend Update Successful. Status Code: " + statusCode);
          } else {
              ExtentReportManager.logFail("Backend Update Failed. Status Code: " + statusCode);
              Assert.fail("API error during profile update: " + statusCode);
          }
          
            ExtentReportManager.logInfo("Entered new name: " + newName);

            // 4. Save
           settingsPage.clickSaveChanges();

            // 5. Verify Success Message
            boolean isSuccess = settingsPage.isSuccessMessageDisplayed();
            
            if (isSuccess) {
                ExtentReportManager.logPass("Success message displayed successfully");
            } else {
                ExtentReportManager.logFail("Success message was NOT displayed");
                // Capture screenshot for debugging
                ExtentReportManager.attachScreenshot(
                    captureScreenshotOnFailure("SystemSettings_Toast_Missing"), 
                    "Missing Success Toast"
                );
                Assert.fail("Success message toast not found");
            }

            // Optional: Verify the value persists (re-open modal or check if modal stays open)
            // If the modal closes automatically, you might need to re-open it to verify the value.
            // If it stays open:
            String actualValue = settingsPage.getFullNameValue();
            Assert.assertEquals(actualValue, newName, "The input field value did not match the updated name!");
            ExtentReportManager.logPass("Verified input field value persists: " + actualValue);

        } catch (Exception e) {
            logger.error("Test failed: testUpdateProfileFullName", e);
            ExtentReportManager.logFail("Test failed: " + e.getMessage());
            
            String screenshot = captureScreenshotOnFailure("testUpdateProfileFullName");
            if (screenshot != null) {
                ExtentReportManager.attachScreenshot(screenshot, "Failure Screenshot");
            }
            throw e;
        }
    }
    
    
    @Test(priority = 2, groups = {"settings", "functional"})
    public void testUpdateNotificationEmail() {
        String newEmail = PlaywrightUtils.generateRandomEmail();
        
        try {
            
            // 2. Navigate to Tab
            settingsPage.navigateToNotificationEmailTab();
            ExtentReportManager.logInfo("Navigated to Notification Email tab");

            // 3. Update Email
            settingsPage.updateNotificationEmail(newEmail);
            ExtentReportManager.logInfo("Entered new email: " + newEmail);

            // 4. Save
            settingsPage.clickSaveChanges();

            // 5. Verify Success
            boolean isSuccess = settingsPage.isSuccessMessageDisplayed();
            
            if (isSuccess) {
                ExtentReportManager.logPass("Notification email updated successfully to: " + newEmail);
            } else {
            	// captureScreenshotOnFailure returns a path, attachScreenshot handles the rest
                String path = captureScreenshotOnFailure("Email_Update_Failure");
                ExtentReportManager.attachScreenshot(path, "Missing Toast Failure");
                Assert.fail("Success message not displayed for Email update");
            
            }
            
         
        } catch (Exception e) {
        	logger.error("Test failed: testUpdateNotificationEmail", e);
            ExtentReportManager.logFail("Email Update Test failed: " + e.getMessage());
            
            String screenshot = captureScreenshotOnFailure("testUpdateNotificationEmail_Exception");
            if (screenshot != null) {
                ExtentReportManager.attachScreenshot(screenshot, "Critical Failure Screenshot");
            }
            throw e;
        }
    }
    
    @Test(priority = 3, groups = {"settings", "crud"})
    public void testEmailTemplatesCRUD() {
        String tName = generateRandomTemplateName();
        String tSubject = "Welcome to " + tName;
        String tBody = "<html><body><h1>Hello World</h1></body></html>";
        String tSubjectUpdated = tSubject + " - Updated";
        
        try {
        	
        	logger.info("Starting Test: Email Templates CRUD");

            // 1. Open Settings & Navigate to Tab
            if (!getPage().locator("h2.text-2xl.font-bold.text-gray-900").isVisible()) {
                settingsPage.clickSettingsButton();
                settingsPage.waitForSettingsModal();
            }
            settingsPage.navigateToEmailTemplatesTab();
            ExtentReportManager.logInfo("Navigated to Email Templates tab");
            
         // 2. Create Template
            settingsPage.clickNewTemplateButton();
            settingsPage.fillTemplateForm( tName, tSubject, tBody);
            settingsPage.clickCreateTemplate();
            
         // Verify Creation (Toast & List)
            Assert.assertTrue(settingsPage.isSuccessMessageDisplayed(), "Creation Success toast not displayed");
            Assert.assertTrue(settingsPage.isTemplateListed(tName), "Created template not found in list");
            ExtentReportManager.logPass("Template created and verified: " + tName);
			
		} catch (Exception e) {
			
		}
        
    }
       

}
