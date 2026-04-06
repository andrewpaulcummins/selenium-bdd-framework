package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the SauceDemo checkout information page (step one).
 *
 * <p>Encapsulates all element locators and user interactions available
 * on the first checkout step ({@code /checkout-step-one.html}), where the
 * user enters their personal information before reviewing their order.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Entering first name, last name, and postal code</li>
 *   <li>Submitting the form via the Continue button</li>
 *   <li>Cancelling checkout and returning to the cart</li>
 *   <li>Retrieving validation error messages when required fields are missing</li>
 * </ul>
 */
public class CheckoutPage extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The page title element displaying "Checkout: Your Information".
     */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /**
     * The first name input field.
     */
    @FindBy(css = "[data-test='firstName']")
    private WebElement firstNameField;

    /**
     * The last name input field.
     */
    @FindBy(css = "[data-test='lastName']")
    private WebElement lastNameField;

    /**
     * The postal code input field.
     */
    @FindBy(css = "[data-test='postalCode']")
    private WebElement postalCodeField;

    /**
     * The "Continue" button that submits the form and advances to the overview page.
     */
    @FindBy(css = "[data-test='continue']")
    private WebElement continueButton;

    /**
     * The "Cancel" button that returns the user to the cart page.
     */
    @FindBy(css = "[data-test='cancel']")
    private WebElement cancelButton;

    /**
     * The error message container displayed when required fields are missing.
     */
    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code CheckoutPage} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public CheckoutPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Enters the given first name into the first name input field.
     *
     * @param firstName the first name string to enter
     */
    public void enterFirstName(String firstName) {
        type(firstNameField, firstName);
    }

    /**
     * Enters the given last name into the last name input field.
     *
     * @param lastName the last name string to enter
     */
    public void enterLastName(String lastName) {
        type(lastNameField, lastName);
    }

    /**
     * Enters the given postal code into the postal code input field.
     *
     * @param postalCode the postal code string to enter
     */
    public void enterPostalCode(String postalCode) {
        type(postalCodeField, postalCode);
    }

    /**
     * Fills all three checkout information fields in a single call.
     *
     * <p>Convenience method that combines {@link #enterFirstName(String)},
     * {@link #enterLastName(String)}, and {@link #enterPostalCode(String)}
     * for scenarios that do not need to assert individual field behaviour.</p>
     *
     * @param firstName  the first name to enter
     * @param lastName   the last name to enter
     * @param postalCode the postal code to enter
     */
    public void fillForm(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
    }

    /**
     * Clicks the "Continue" button to submit the checkout form.
     *
     * <p>If all required fields are populated, this navigates to the checkout
     * overview page. If any required field is missing, an error message is
     * displayed instead.</p>
     */
    public void clickContinue() {
        click(continueButton);
    }

    /**
     * Clicks the "Cancel" button, returning the user to the cart page.
     */
    public void clickCancel() {
        click(cancelButton);
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the visible title text of the checkout information page.
     *
     * <p>The expected value is {@code "Checkout: Your Information"}.</p>
     *
     * @return the page title text as a string
     */
    public String getPageTitleText() {
        return getText(pageTitle);
    }

    /**
     * Returns whether the checkout page title is displayed.
     *
     * @return {@code true} if the page title is displayed, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isDisplayed(pageTitle);
    }

    /**
     * Returns the current text of the validation error message.
     *
     * <p>Used in step definitions to assert the correct field validation
     * error is shown when a required field is missing.</p>
     *
     * @return the error message text as a string
     */
    public String getErrorMessageText() {
        return getText(errorMessage);
    }

    /**
     * Returns whether a validation error message is currently displayed.
     *
     * @return {@code true} if an error message is displayed, {@code false} otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }
}