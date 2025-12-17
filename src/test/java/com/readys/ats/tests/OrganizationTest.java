package com.readys.ats.tests;

import com.readys.ats.base.BaseTest;
import com.readys.ats.pages.LoginPage;
import com.readys.ats.pages.OrganizationPage;
import com.readys.ats.utils.ExtentReportManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrganizationTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(OrganizationTest.class);

    private LoginPage loginPage;
    private OrganizationPage organizationPage;

    // -------------------- SETUP & LOGIN --------------------

    @BeforeClass(alwaysRun = true)
    public void setupOrganizationTests() {
        try {
            logger.info("===== Organization Test Setup Started =====");

            loginPage = new LoginPage(getPage());
            organizationPage = new OrganizationPage(getPage());

            // 🔑 LOGIN ONLY (no extra verifyPageLoaded)
            loginPage.loginWithValidCredentials();

            // ✅ Validate landing page URL instead of login page elements
            String currentUrl = getPage().url();
            if (!currentUrl.contains("admin")) {
                throw new RuntimeException("Login failed. Current URL: " + currentUrl);
            }

            logger.info("Login successful, navigated to admin area");
            ExtentReportManager.logPass("Login successful for Organization tests");

        } catch (Exception e) {
            logger.error("Organization test setup failed", e);
            ExtentReportManager.logFail("Organization test setup failed: " + e.getMessage());
            throw new RuntimeException("Organization test setup failed", e);
        }
    }

    // -------------------- TEST 1 : VERIFY UI --------------------

    @Test(
        groups = {"organization", "ui", "smoke"},
        description = "Verify Organization page UI elements",
        priority = 1
    )
    public void verifyOrganizationPageUI() {
        try {
            organizationPage.verifyOrganizationPageLoaded();

            ExtentReportManager.logPass("Organization page UI verified successfully");

        } catch (Exception e) {
            logger.error("verifyOrganizationPageUI failed: " + e.getMessage(), e);
            ExtentReportManager.logFail("Organization page UI verification failed: " + e.getMessage());

            String screenshotPath = captureScreenshotOnFailure("verifyOrganizationPageUI");
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Organization UI Failure");
            }
            throw e;
        }
    }

    // -------------------- TEST 2 : CREATE ORGANIZATION --------------------

    @Test(
        groups = {"organization", "crud", "critical"},
        description = "Verify Create Organization functionality",
        priority = 2
    )
    public void testCreateOrganization() {
        try {
            String companyName = "AutoOrg_" + System.currentTimeMillis();
            String email = "auto_" + System.currentTimeMillis() + "@test.com";
            String phone = "9876543210";

            organizationPage.createOrganization(companyName, email, phone);
            organizationPage.searchOrganization(companyName);

            ExtentReportManager.logPass("Organization created successfully: " + companyName);

        } catch (Exception e) {
            logger.error("testCreateOrganization failed: " + e.getMessage(), e);
            ExtentReportManager.logFail("Create Organization test failed: " + e.getMessage());

            String screenshotPath = captureScreenshotOnFailure("testCreateOrganization");
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Create Organization Failure");
            }
            throw e;
        }
    }

    // -------------------- TEST 3 : EDIT ORGANIZATION --------------------

    @Test(
    	    groups = {"organization", "crud"},
    	    description = "Verify Edit Organization functionality",
    	    priority = 3
    	)
    	public void testEditOrganization() {
    	    try {
    	        String updatedName = "Edited_Org_" + System.currentTimeMillis();

    	        organizationPage.editFirstOrganization(updatedName);
    	        organizationPage.verifyOrganizationUpdated(updatedName);

    	        ExtentReportManager.logPass(
    	            "Organization edited and verified successfully: " + updatedName
    	        );

    	    } catch (Exception e) {
    	        logger.error("testEditOrganization failed: " + e.getMessage(), e);
    	        ExtentReportManager.logFail(
    	            "Edit Organization test failed: " + e.getMessage()
    	        );

    	        String screenshotPath =
    	                captureScreenshotOnFailure("testEditOrganization");
    	        if (screenshotPath != null) {
    	            ExtentReportManager.attachScreenshot(
    	                screenshotPath, "Edit Organization Failure"
    	            );
    	        }
    	        throw e;
    	    }
    	}


    // -------------------- TEST 4 : DELETE ORGANIZATION --------------------

    @Test(
        groups = {"organization", "crud"},
        description = "Verify Delete Organization functionality",
        priority = 4
    )
    public void testDeleteOrganization() {
        try {
            organizationPage.clickDeleteFirstOrganization();

            ExtentReportManager.logPass("Organization deleted successfully");

        } catch (Exception e) {
            logger.error("testDeleteOrganization failed: " + e.getMessage(), e);
            ExtentReportManager.logFail("Delete Organization test failed: " + e.getMessage());

            String screenshotPath = captureScreenshotOnFailure("testDeleteOrganization");
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Delete Organization Failure");
            }
            throw e;
        }
    }
}
