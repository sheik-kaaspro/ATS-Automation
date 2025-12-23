package com.readys.ats.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.WaitForSelectorState;

public class SystemSettingsPage {
	
	private final Page page;
	
	public SystemSettingsPage(Page page) {
		this.page = page;
	}
	
	// ================== LOCATORS ==================

    // The 'Settings' button on the main Organizations dashboard
    // Using a broad text match or locating relative to Create button if needed
    private final String settingsButton = 
            "//button[contains(., 'Settings')]";

    // Modal Header to verify it's open
    private final String modalHeader = 
            "//h2[text()='System Settings']";

    // Tabs
    private final String myProfileTab = 
            "//button[text()='My Profile']";

    // Input Fields
    // Strategy: Find label "Full Name", go to parent, find input. 
    // This is robust against placeholder changes.
    private final String fullNameInput = 
            "//label[contains(text(), 'Full Name')]/..//input";

    private final String saveChangesBtn = 
            "//button[contains(text(), 'Save Changes')]";

    // Success Message (Toast/Alert)
    // Adjust this text based on the actual message shown in your app
    private final String successMessageToast = 
            "//div[contains(@class,'mb-4 p-3 bg-green-50')]"; 
            // Alternative generic locator: "div[role='alert']"
    
    private final String notificatioEmailTab= "//button[contains(text(), 'Notification Email')]";
    
    private final String notificationEmailField = "//input[@placeholder='admin@example.com']";
    
    private final String deleteConfirmationBtn = "//button[text()='Delete']";
    
    private final String emailTemplateTab = "//button[contains(text(), 'Email Templates')]";
    
    private final String newTemplateBtn = "//button[text()='+ New Template']";
    
    private final String createTemplateBtn = "button[type='submit']";
    
    private final String updateTemplateBtn = "//button[text()='Update Template']";
    
    private final String templateTypeField = "input[value='Custom Template']";
    
    private final String templateNameField = "input[placeholder='e.g., Welcome Email']";
    
    private final String  subjectField= "input[placeholder='e.g., Welcome to {{company_name}}!']";
    
    private final String  htmlBodyTextArea= "textarea[placeholder='<html><body>Hello {{user_name}},...</body></html>']";

    
    
    
	
    
 // ================== ACTIONS ==================

    public void clickSettingsButton() {
        System.out.println("Clicking 'Settings' button...");
        
        Locator btn = page.locator(settingsButton).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        
        // Use force click to avoid potential overlay interception
        btn.click(new Locator.ClickOptions().setForce(true));
    }
    
    public void waitForSettingsModal() {
        System.out.println("Waiting for System Settings modal...");
        page.locator(modalHeader).first().waitFor(
            new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000)
        );
    }
    
    private void clearAndFillInput(String locator, String value) {
        Locator field = page.locator(locator).first();
        field.click();
        // Clear existing value robustly
        page.keyboard().press("Control+A");
        page.keyboard().press("Backspace");
        page.waitForTimeout(200); 
        field.fill(value);
    }
    
    public void navigateToMyProfileTab() {
        Locator tab = page.locator(myProfileTab);
        if (tab.isVisible()) {
            // Only click if not already active/visible (optional logic, usually safe to click)
            tab.click();
            System.out.println("Switched to 'My Profile' tab");
        }
    }
    
    public int updateFullName(String newName) {
        System.out.println("Updating Full Name to: " + newName);
        
        Locator nameField = page.locator(fullNameInput).first();
        nameField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        
        // Click to focus
        nameField.click();
        
        // Robust Clear: Ctrl+A -> Backspace
        // This ensures the field is truly empty before typing, 
        // unlike .fill("") which sometimes fails on pre-filled React/Angular inputs.
        page.keyboard().press("Control+A");
        page.keyboard().press("Backspace");
        page.waitForTimeout(200); 

        // Type new name
        nameField.fill(newName);
        
        System.out.println("Clicking 'Save Changes' and waiting for API response...");

        // Strategy: Wait for the system-settings API call
        Response response = page.waitForResponse(
            res -> res.url().contains("/api/auth/profile") 
                && (res.request().method().equalsIgnoreCase("PATCH") 
                    || res.request().method().equalsIgnoreCase("PUT")),
            new Page.WaitForResponseOptions().setTimeout(15000),
            () -> {
                page.locator(saveChangesBtn).click();
            }
        );

        System.out.println("Profile Update API Status: " + response.status());
        return response.status();
    
    }
    
    public void clickSaveChanges() {
        System.out.println("Clicking 'Save Changes'...");
        	    	page.locator(saveChangesBtn).click();
        
        
    }
    
 // ================== VERIFICATION ==================

    public boolean isSuccessMessageDisplayed() {
        System.out.println("Waiting for success message...");
        try {
            // Wait for the toast to appear
            Locator toast = page.locator(successMessageToast).first();
            toast.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
            
            System.out.println("Success message verified: " + toast.textContent());
            return true;
        } catch (Exception e) {
            System.out.println("Success message not found within timeout.");
            return false;
        }
    }
    
 // Optional: Verify the input field actually retained the new value
    public String getFullNameValue() {
        return page.locator(fullNameInput).inputValue();
    }
    
 // ================== NOTIFICATION EMAIL TAB ACTIONS (NEW) ==================

    public void navigateToNotificationEmailTab() {
        System.out.println("Navigating to 'Notification Email' tab...");
        Locator tab = page.locator(notificatioEmailTab);
        tab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        tab.click();
        
        // Wait for the specific input of this tab to appear to ensure tab switch is complete
        page.locator(notificationEmailField).waitFor(
            new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000)
        );
    }

    public void updateNotificationEmail(String newEmail) {
        System.out.println("Updating Notification Email to: " + newEmail);
        clearAndFillInput(notificationEmailField, newEmail);
    }
    
    public String getNotificationEmailValue() {
        return page.locator(notificationEmailField).inputValue();
    }
    
 // ================== EMAIL TEMPLATES ACTIONS ==================

    public void navigateToEmailTemplatesTab() {
        System.out.println("Navigating to 'Email Templates' tab...");
        page.locator(emailTemplateTab).click();
        // Wait for the "New Template" button to be visible to confirm tab load
        page.locator(newTemplateBtn).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    public void clickNewTemplateButton() {
        System.out.println("Clicking '+ New Template'...");
        page.locator(newTemplateBtn).click();
    }
    
    public void fillTemplateForm( String name, String subject, String body) {
        System.out.println("Filling template form...");
        // Handle "Template Type" - It might be a dropdown or text. Treating as text based on standard fill usage.
        // If it is a searchable dropdown, fill() usually works too.
       // clearAndFillInput(templateTypeField, type);
        clearAndFillInput(templateNameField, name);
        clearAndFillInput(subjectField, subject);
        
        // Body might be a simple textarea or code editor. 
        Locator bodyField = page.locator(htmlBodyTextArea);
        bodyField.fill(body);
    }
	
    public void clickCreateTemplate() {
        System.out.println("Clicking 'Create Template'...");
        page.locator(createTemplateBtn).click();
    }
    
    public void clickUpdateTemplate() {
        System.out.println("Clicking 'Update Template'...");
        page.locator(updateTemplateBtn).click();
    }
    
 // Check if template exists in the list
    public boolean isTemplateListed(String templateName) {
        System.out.println("Verifying template in list: " + templateName);
        // Locate a row/card that contains the template name text
        Locator templateItem = page.locator("//div[contains(@class,'border')]//h3" + templateName + "')]");
        try {
            templateItem.first().waitFor(new Locator.WaitForOptions().setTimeout(5000));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
