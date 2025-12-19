package com.readys.ats.pages;

import java.util.Random;
import java.util.UUID;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class OrganizationPage {

    private final Page page;

    public OrganizationPage(Page page) {
        this.page = page;
    }

    // ================== LOCATORS ==================

    private final String createOrgBtn =
            "//button[text()='+ Create Organization']";

    private final String nameInput =
            "//input[@placeholder='e.g., Acme Corporation']";

    private final String emailInput =
            "input[type='email']";

    private final String phoneInput =
            "input[type='tel']";

    private final String createButton =
            "//button[text()='Create']";
    
    private final String updateButton =
            "//button[text()='Update']";

    private final String deleteConfirmButton =
            "//button[text()='Delete']";

    private final String modalOverlay =
            "div.fixed.inset-0";

    private final String tableRows =
            "table tbody tr";
    
    // Generic action buttons in table rows
    private final String editBtnInRow =
            "button.text-blue-600";
    
    private final String deleteBtnInRow =
            "button.text-red-600";

    // ================== COMMON ==================

    private void closeAnyOpenModal() {
        try {
            // Check if overlay exists
            if (page.locator(modalOverlay).count() > 0) {
                System.out.println("Modal overlay detected, attempting to close...");
                
                // Try pressing Escape key to close modal
                page.keyboard().press("Escape");
                page.waitForTimeout(1000);
                
                // Wait for overlay to disappear
                page.locator(modalOverlay).first().waitFor(
                    new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(5000)
                );
                
                System.out.println("Modal closed successfully");
            }
        } catch (Exception e) {
            System.out.println("No modal to close or already closed");
        }
        
        // Extra safety wait
        page.waitForTimeout(1000);
    }
    
    private void waitForModalToOpen() {
        System.out.println("Waiting for modal to open...");
        page.locator(modalOverlay).first().waitFor(
            new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000)
        );
        page.waitForTimeout(1000); // Let animation complete
        System.out.println("Modal opened");
    }
    
    private void waitForModalToClose() {
        System.out.println("Waiting for modal to close...");
        try {
            page.locator(modalOverlay).first().waitFor(
                new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.HIDDEN)
                    .setTimeout(10000)
            );
            System.out.println("Modal closed");
        } catch (Exception e) {
            System.out.println("Modal may already be closed");
        }
        
        // Wait for DOM to stabilize
        page.waitForTimeout(2000);
    }
    
    //Generate Random Email
    public static String generateRandomEmail() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "testuser_" + uuid + "@mailinator.com";
    }
    
    // Generate random company name
    public static String generateRandomCompanyName() {
        String[] prefixes = {"Tech", "Info", "Data", "Cloud", "Smart", "Next"};
        String[] suffixes = {"Solutions", "Systems", "Labs", "Corp", "Technologies", "Services"};

        Random random = new Random();
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        int number = random.nextInt(900) + 100;

        return prefix + " " + suffix + " " + number;
    }
    
    //Generate Random phone number
    public static String generateIndianPhoneNumber() {
        Random random = new Random();
        int firstDigit = random.nextInt(4) + 6; // 6–9
        StringBuilder phone = new StringBuilder();
        phone.append(firstDigit);

        for (int i = 0; i < 9; i++) {
            phone.append(random.nextInt(10));
        }
        return phone.toString();
    }

    private Locator firstRow() {
        page.locator(tableRows).first().waitFor(
            new Locator.WaitForOptions().setTimeout(15000)
        );
        return page.locator(tableRows).first();
    }

    // ================== VERIFY ==================

    public void verifyOrganizationPageLoaded() {
        page.locator(createOrgBtn).first().waitFor(
            new Locator.WaitForOptions().setTimeout(15000)
        );
    }

    // ================== CREATE ==================

    public void createOrganization(String name, String email, String phone) {
        System.out.println("Starting create organization: " + name);
        
        // Ensure no modal is open
        closeAnyOpenModal();
        
        // Click create button
        System.out.println("Clicking create button...");
        page.locator(createOrgBtn).click();
        
        // Wait for modal to open
        waitForModalToOpen();
        
        // Wait for name input to be ready
        page.locator(nameInput).waitFor(
            new Locator.WaitForOptions().setTimeout(10000)
        );
        page.waitForTimeout(500);

        System.out.println("Filling form...");
        // Fill form
        page.locator(nameInput).fill(name);
        page.locator(emailInput).fill(email);
        page.locator(phoneInput).fill(phone);
        
        page.waitForTimeout(500);

        System.out.println("Clicking create button in modal...");
        // Click create button in modal
        page.locator(createButton).click();
        
        // Wait for modal to close
        waitForModalToClose();
        
        System.out.println("Create organization completed");
    }

    public void searchOrganization(String name) {
        System.out.println("Searching for organization: " + name);
        
        // Wait for table to be present
        page.locator(tableRows).first().waitFor(
            new Locator.WaitForOptions().setTimeout(15000)
        );

        // Wait for the table to refresh
        page.waitForTimeout(2000);

        // Search for the organization name in any table cell
        Locator orgCell = page.locator("table tbody tr td.text-sm.font-medium.text-gray-900").filter(
            new Locator.FilterOptions().setHasText(name)
        );
        
        try {
            orgCell.first().waitFor(
                new Locator.WaitForOptions().setTimeout(10000)
            );
            System.out.println("Organization found in table");
        } catch (Exception e) {
            throw new RuntimeException("Organization '" + name + "' not found in table after waiting");
        }
    }

    // ================== EDIT ==================

    public void editFirstOrganization(String updatedName) {
        System.out.println("Starting edit operation for: " + updatedName);
        
        // 1. Ensure clean state
        closeAnyOpenModal();
        
        // 2. Wait for table to populate
        page.locator(tableRows).first().waitFor(
            new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE)
        );
        page.waitForTimeout(1000); 

        // 3. Robustly find the Edit button
        Locator row = page.locator(tableRows).first();
        Locator editButton = row.locator("td").last().locator(editBtnInRow).first();
        
        // Ensure button is ready and scroll it into view
        editButton.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        editButton.scrollIntoViewIfNeeded();
        
        System.out.println("Clicking edit button...");
        // USE FORCE CLICK: Solves issues with overlays or click interception
        editButton.click(new Locator.ClickOptions().setForce(true));
        
        // 4. Wait for modal
        waitForModalToOpen();
        
        // 5. Robust Input Strategy
        // In 'Edit' mode, placeholders might disappear if value exists.
        // We look for the Name input inside the active modal specifically.
        // Try the specific placeholder first, fall back to generic input if needed.
        Locator nameField;
        
        try {
            // Check if specific placeholder exists (might fail if pre-filled)
            nameField = page.locator(nameInput).first();
            if(!nameField.isVisible()) {
                throw new Exception("Specific placeholder locator not visible");
            }
        } catch (Exception e) {
            System.out.println("Placeholder locator not found (likely due to pre-filled value). Using generic modal input.");
            // Fallback: Get the first text input inside the modal
            nameField = page.locator(modalOverlay).locator("input[type='text']").first();
        }
        
        nameField.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        page.waitForTimeout(500);
        
        System.out.println("Clearing and entering new name: " + updatedName);
        
        // 6. Robust Clear and Fill
        nameField.click();
        // Standard .fill("") sometimes fails to trigger framework validation events
        // Using keyboard actions ensures the field is truly cleared
        page.keyboard().press("Control+A"); // Select All
        page.keyboard().press("Backspace"); // Delete
        page.waitForTimeout(200);
        nameField.fill(updatedName);
        
        page.waitForTimeout(500);
        
        System.out.println("Clicking update button...");
        page.locator(updateButton).click();
        
        waitForModalToClose();
        System.out.println("Edit operation completed");
    }

    public void verifyOrganizationUpdated(String updatedName) {
        System.out.println("Verifying organization updated to: " + updatedName);
        page.waitForTimeout(2000);
        Locator updatedCell = page.locator("table tbody tr td.text-sm.font-medium.text-gray-900").filter(
            new Locator.FilterOptions().setHasText(updatedName)
        );
        try {
            updatedCell.first().waitFor(new Locator.WaitForOptions().setTimeout(15000));
            System.out.println("Verification successful: Updated organization found");
        } catch (Exception e) {
            System.out.println("Verification failed: Updated organization not found");
            throw new RuntimeException("Updated organization '" + updatedName + "' not found in table");
        }
    }

    // ================== DELETE ==================

    public void clickDeleteFirstOrganization() {
        System.out.println("Starting delete operation...");
        
        // Ensure no modal is open
        closeAnyOpenModal();
        
        // Wait for table to be ready
        page.locator(tableRows).first().waitFor(
            new Locator.WaitForOptions().setTimeout(15000)
        );
        page.waitForTimeout(2000);

        // Get first row
        Locator row = firstRow();
        
        // Get organization name before deletion
        String orgName = row.locator("td").first().textContent().trim();
        System.out.println("Attempting to delete: " + orgName);

        // Find delete button in actions cell
        Locator actionsCell = row.locator("td").last();
        Locator deleteButton = actionsCell.locator(deleteBtnInRow).last();
        
        // Ensure button is visible
        deleteButton.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        
        System.out.println("Clicking delete button...");

        // Click delete button
        deleteButton.click();

        // Wait for confirmation modal to appear
        System.out.println("Waiting for delete confirmation modal...");
        
        waitForModalToOpen();
        
        // Wait for confirm button in modal
        page.locator(deleteConfirmButton).waitFor(
            new Locator.WaitForOptions().setTimeout(10000)
        );
        
        page.waitForTimeout(1000);

        System.out.println("Clicking confirm delete button...");
        
        // Click confirm button
        page.locator(deleteConfirmButton).click();

        // Wait for modal to close
        waitForModalToClose();
        
        System.out.println("Delete operation completed");
    }
}