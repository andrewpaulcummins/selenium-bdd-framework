package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the SauceDemo order confirmation page.
 *
 * <p>Encapsulates all element locators and user interactions available
 * on the checkout complete page ({@code /checkout-complete.html}). This is
 * the final page in the checkout flow, displayed after a user successfully
 * places an order.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Verifying the confirmation header and message are displayed</li>
 *   <li>Navigating back to the inventory via the "Back Home" button</li>
 * </ul>
 */
public class CheckoutCompletePage extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The page title element displaying "Checkout: Complete!".
     */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /**
     * The confirmation header displaying "Thank you for your order!".
     */
    @FindBy(css = ".complete-header")
    private WebElement confirmationHeader;

    /**
     * The confirmation body text with order dispatch information.
     */
    @FindBy(css = ".complete-text")
    private WebElement confirmationText;

    /**
     * The "Back Home" button that returns the user to the inventory page.
     */
    @FindBy(css = "[data-test='back-to-products']")
    private WebElement backHomeButton;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code CheckoutCompletePage} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public CheckoutCompletePage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Clicks the "Back Home" button, returning the user to the inventory page.
     */
    public void clickBackHome() {
        click(backHomeButton);
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the visible title text of the confirmation page.
     *
     * <p>The expected value is {@code "Checkout: Complete!"}.</p>
     *
     * @return the page title text as a string
     */
    public String getPageTitleText() {
        return getText(pageTitle);
    }

    /**
     * Returns the confirmation header text.
     *
     * <p>The expected value is {@code "Thank you for your order!"}.</p>
     *
     * @return the confirmation header text as a string
     */
    public String getConfirmationHeaderText() {
        return getText(confirmationHeader);
    }

    /**
     * Returns the confirmation body text shown below the header.
     *
     * @return the confirmation body text as a string
     */
    public String getConfirmationText() {
        return getText(confirmationText);
    }

    /**
     * Returns whether the confirmation page title is displayed.
     *
     * <p>Used as a quick check to confirm the order was placed successfully.</p>
     *
     * @return {@code true} if the page title is displayed, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isDisplayed(pageTitle);
    }

    /**
     * Returns whether the confirmation header is displayed.
     *
     * @return {@code true} if the confirmation header is displayed, {@code false} otherwise
     */
    public boolean isConfirmationHeaderDisplayed() {
        return isDisplayed(confirmationHeader);
    }
}