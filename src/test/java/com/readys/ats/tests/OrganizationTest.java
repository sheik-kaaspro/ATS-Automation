package com.readys.ats.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.readys.ats.base.BaseTest;
import com.readys.ats.pages.LoginPage;
import com.readys.ats.pages.OrganizationPage;
import com.readys.ats.utils.ExtentReportManager;

public class OrganizationTest extends BaseTest {

    private static final Logger logger =
            LogManager.getLogger(OrganizationTest.class);

    private LoginPage loginPage;
    private OrganizationPage organizationPage;
    private String createdOrgName;
    
    public void verifyOrganizationPresentInList(String organizationName) {
        try {
            // Wait for table to load
            page.waitForSelector("table tbody tr", 
                new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(15000));
            
            // Try multiple strategies
            String normalizedXpath = "//table//tr//td[normalize-space()='" + organizationName + "']";
            String containsXpath = "//table//tr//td[contains(text(),'" + organizationName + "')]";
            
            Locator row = null;
            try {
                row = page.locator(normalizedXpath);
                row.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            } catch (Exception e1) {
                try {
                    row = page.locator(containsXpath);
                    row.waitFor(new Locator.WaitForOptions().setTimeout(10000));
                } catch (Exception e2) {
                    throw new RuntimeException(
                        "Organization not found in list: " + organizationName
                    );
                }
            }

            if (!row.isVisible()) {
                throw new RuntimeException(
                    "Organization not visible in list: " + organizationName
                );
            }
        } catch (Exception e) {
            logger.error("Failed to verify organization in list: " + organizationName, e);
            throw e;
        }
    }

    // =====================================================
    // LOGIN ONCE – SAME TAB
    // =====================================================
    @BeforeClass(alwaysRun = true)
    public void loginOnce() {
        try {
            logger.info("===== OrganizationTest : LOGIN ONCE START =====");

            // Navigate to organizations page
            String orgUrl = config.getAppUrl();
            if (!orgUrl.endsWith("/admin/organizations")) {
                orgUrl = orgUrl.replace("/login", "/admin/organizations");
            }
            
            getPage().navigate(orgUrl);

            loginPage = new LoginPage(getPage());
            organizationPage = new OrganizationPage(getPage());

            // Check if already logged in
            if (getPage().url().contains("login")) {
                // Perform login
                loginPage.loginWithValidCredentials();

                // Navigate to organizations page after login
                getPage().navigate(orgUrl);
            }

            // Wait for organizations page to load
            getPage().waitForURL(
                url -> url.contains("admin/organizations"),
                new com.microsoft.playwright.Page.WaitForURLOptions()
                        .setTimeout(20000)
            );
            
            // Wait for page content
            getPage().waitForLoadState();

            ExtentReportManager.logPass(
                "Login successful and navigated to Organization page"
            );

        } catch (Exception e) {
            logger.error("Login failed in OrganizationTest", e);
            ExtentReportManager.logFail(
                "Login failed in OrganizationTest: " + e.getMessage()
            );

            String screenshot =
                captureScreenshotOnFailure("Organization_Login_Failure");
            if (screenshot != null) {
                ExtentReportManager.attachScreenshot(
                    screenshot, "Login Failure"
                );
            }

            throw new RuntimeException("Login failed", e);
        }
    }

    // =====================================================
    // TEST 1 – VERIFY PAGE UI
    // =====================================================
    @Test(priority = 1, groups = {"organization", "ui"}, enabled = true)
    public void verifyOrganizationPageUI() {
        try {
            organizationPage.verifyOrganizationPageLoaded();

            ExtentReportManager.logPass(
                "Organization page UI verified successfully"
            );

        } catch (Exception e) {
            ExtentReportManager.logFail(
                "Organization UI verification failed: " + e.getMessage()
            );

            String screenshot =
                captureScreenshotOnFailure("verifyOrganizationPageUI");
            if (screenshot != null) {
                ExtentReportManager.attachScreenshot(
                    screenshot, "UI Failure"
                );
            }

            throw e;
        }
    }

    // =====================================================
    // TEST 2 – CREATE ORGANIZATION
    // =====================================================
    @Test(priority = 2, groups = {"organization", "crud"}, enabled = true)
    public void testCreateOrganization() {
        try {
            String email = organizationPage.generateRandomEmail();
            String orgName = organizationPage.generateRandomCompanyName();
            String phone = organizationPage.generateIndianPhoneNumber();
            
            createdOrgName = orgName;
            
            logger.info("Creating organization: " + orgName);

           int statusCode = organizationPage.createOrganization(orgName, email, phone);
           
        // Verify Backend Response (200 OK or 201 Created)
           if (statusCode == 200 || statusCode == 201) {
               ExtentReportManager.logPass("Backend API Verification Successful. Status Code: " + statusCode);
           } else {
               ExtentReportManager.logFail("Backend API Verification Failed. Status Code: " + statusCode);
               // Fail the test if status is not success
               Assert.assertEquals(statusCode, 200, "API did not return success code!"); 
           }
            
            // Verify it appears in the list
            organizationPage.searchOrganization(orgName);
            verifyOrganizationPresentInList(orgName);

            ExtentReportManager.logPass(
                "Organization created successfully: " + orgName
            );

        } catch (Exception e) {
            ExtentReportManager.logFail(
                "Create Organization failed: " + e.getMessage()
            );

            String screenshot =
                captureScreenshotOnFailure("testCreateOrganization");
            if (screenshot != null) {
                ExtentReportManager.attachScreenshot(
                    screenshot, "Create Failure"
                );
            }

            throw e;
        }
    }
    
    

    // =====================================================
    // TEST 3 – EDIT ORGANIZATION
    // =====================================================
    @Test(priority = 3, groups = {"organization", "crud"}, enabled = true)
    public void testEditOrganization() {
        try {
        	
             
            String updatedName= organizationPage.generateRandomCompanyName();
            
            logger.info("Editing organization to: " + updatedName);

           int statusCode = organizationPage.editFirstOrganization(updatedName);
            organizationPage.verifyOrganizationUpdated(updatedName);
            
         // 2. Verify Backend Status (Usually 200 for Update)
            if (statusCode == 200) {
                ExtentReportManager.logPass("Backend Edit Verification Successful. Status Code: " + statusCode);
            } else {
                ExtentReportManager.logFail("Backend Edit Verification Failed. Status Code: " + statusCode);
                Assert.assertEquals(statusCode, 200, "API did not return 200 OK for edit!");
            }

            ExtentReportManager.logPass(
                "Organization edited successfully: " + updatedName
            );

        } catch (Exception e) {
            ExtentReportManager.logFail(
                "Edit Organization failed: " + e.getMessage()
            );

            String screenshot =
                captureScreenshotOnFailure("testEditOrganization");
            if (screenshot != null) {
                ExtentReportManager.attachScreenshot(
                    screenshot, "Edit Failure"
                );
            }

            throw e;
        }
    }

    // =====================================================
    // TEST 5 – DELETE ORGANIZATION
    // =====================================================
    @Test(priority = 4, groups = {"organization", "crud"}, enabled = true)
    public void testDeleteOrganization() {
        try {
            logger.info("Deleting first organization");
            
           int statusCode = organizationPage.clickDeleteFirstOrganization();
           
        // Delete usually returns 200 (OK) or 204 (No Content)
           if (statusCode == 200 || statusCode == 204) {
               ExtentReportManager.logPass(
                   "Backend Delete Verification Successful. Status Code: " + statusCode
               );
           } else {
               ExtentReportManager.logFail(
                   "Backend Delete Verification Failed. Status Code: " + statusCode
               );
               Assert.fail("API did not return success code for delete! Got: " + statusCode);
           }

            ExtentReportManager.logPass(
                "Organization deleted successfully"
            );

        } catch (Exception e) {
            ExtentReportManager.logFail(
                "Delete Organization failed: " + e.getMessage()
            );

            String screenshot =
                captureScreenshotOnFailure("testDeleteOrganization");
            if (screenshot != null) {
                ExtentReportManager.attachScreenshot(
                    screenshot, "Delete Failure"
                );
            }

            throw e;
        }
    }
}