package com.readys.ats.pages;

import com.microsoft.playwright.Page;
import com.readys.ats.utils.PlaywrightUtils;

public class OrganizationPage {

    private Page page;

    // -------------------- LOCATORS (CLASS / ATTRIBUTE BASED ONLY) --------------------

    // Page heading
    private static final String PAGE_HEADER =
            "h1.text-4xl.font-bold";

    private static final String PAGE_SUBTITLE =
            "p.mt-2.text-gray-600";

    // Buttons
    private static final String BTN_SETTINGS =
            "button.border-2.rounded-xl";

    private static final String BTN_CREATE_ORG =
            "button.bg-gradient-to-r.from-blue-500.to-purple-600";

    // Search
    private static final String SEARCH_BOX =
            "input[placeholder*='Search']";

    // Create Organization modal fields
    private static final String INPUT_COMPANY_NAME =
            "input[placeholder='e.g., Acme Corporation']";

    private static final String INPUT_EMAIL =
            "input[type='email']";

    private static final String INPUT_PHONE =
            "input[type='tel']";

    private static final String BTN_SAVE =
            "button.bg-gradient-to-r.from-blue-500.to-purple-600";

    // Table
    private static final String TABLE_ROWS =
            "tbody tr";

    // First row actions
    private static final String FIRST_EDIT_BTN =
            "tbody tr:first-child button[title='Edit Organization']";

    private static final String FIRST_DELETE_BTN =
            "tbody tr:first-child button[title='Delete Organization']";
    
    private static final String EDIT_SAVE_BUTTON =
            "button[type='submit']";
 


    // -------------------- CONSTRUCTOR --------------------

    public OrganizationPage(Page page) {
        this.page = page;
        PlaywrightUtils.setPage(page);
    }

    // -------------------- PAGE VALIDATION --------------------

    public void verifyOrganizationPageLoaded() {
        PlaywrightUtils.waitForElement(PAGE_HEADER);
        PlaywrightUtils.waitForElement(PAGE_SUBTITLE);
    }

    // -------------------- CREATE ORGANIZATION --------------------

    public void createOrganization(String companyName, String email, String phone) {

        PlaywrightUtils.waitForElement(BTN_CREATE_ORG);
        PlaywrightUtils.clickElement(BTN_CREATE_ORG);

        PlaywrightUtils.sleep(1000);

        PlaywrightUtils.waitForElement(INPUT_COMPANY_NAME);
        PlaywrightUtils.fillText(INPUT_COMPANY_NAME, companyName);

        PlaywrightUtils.fillText(INPUT_EMAIL, email);
        PlaywrightUtils.fillText(INPUT_PHONE, phone);

        PlaywrightUtils.clickElement(BTN_SAVE);

        PlaywrightUtils.waitForNetworkIdle();
        PlaywrightUtils.sleep(2000);
    }

    // -------------------- SEARCH ORGANIZATION --------------------

    public void searchOrganization(String companyName) {
        PlaywrightUtils.waitForElement(SEARCH_BOX);
        PlaywrightUtils.fillText(SEARCH_BOX, companyName);
        PlaywrightUtils.sleep(1500);
    }

    // -------------------- EDIT ORGANIZATION --------------------

    public void clickEditFirstOrganization() {
        PlaywrightUtils.waitForElement(FIRST_EDIT_BTN);
        PlaywrightUtils.clickElement(FIRST_EDIT_BTN);
        PlaywrightUtils.sleep(1000);
    }

    public void editFirstOrganization(String updatedCompanyName) {
        try {
            PlaywrightUtils.clickElement(FIRST_EDIT_BTN);

            // Wait popup input
            PlaywrightUtils.waitForElement(INPUT_COMPANY_NAME);

            // Clear & update name
            PlaywrightUtils.fillText(INPUT_COMPANY_NAME, updatedCompanyName);

            // Save
            PlaywrightUtils.clickElement(EDIT_SAVE_BUTTON);

            // Wait for UI update
            PlaywrightUtils.waitForNetworkIdle();

        } catch (Exception e) {
            throw new RuntimeException("Failed to edit organization", e);
        }
    }

    public void verifyOrganizationUpdated(String expectedName) {
        try {
            PlaywrightUtils.waitForElement(INPUT_COMPANY_NAME);

            String actualName =
                    PlaywrightUtils.getText(INPUT_COMPANY_NAME);

            if (!actualName.equals(expectedName)) {
                throw new AssertionError(
                    "Organization name not updated. Expected: "
                    + expectedName + " but Found: " + actualName
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Organization update validation failed", e);
        }
    }

    
    
    // -------------------- DELETE ORGANIZATION --------------------

    public void clickDeleteFirstOrganization() {
        PlaywrightUtils.waitForElement(FIRST_DELETE_BTN);
        PlaywrightUtils.clickElement(FIRST_DELETE_BTN);
        PlaywrightUtils.sleep(1000);

        // If confirmation popup exists, Enter key will confirm
        PlaywrightUtils.pressKey("Enter");
        PlaywrightUtils.waitForNetworkIdle();
    }

    // -------------------- TABLE UTIL --------------------

    public int getOrganizationCount() {
        return PlaywrightUtils.getElementCount(TABLE_ROWS);
    }
}
