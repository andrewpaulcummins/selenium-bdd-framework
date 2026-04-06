package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the SauceDemo login page.
 *
 * <p>Encapsulates all element locators and user interactions available
 * on the login page ({@code /}). All methods interact with the page
 * via the inherited {@code BasePage} utility methods — no raw WebDriver
 * calls are made directly in this class.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Declaring all element locators via {@code @FindBy} annotations</li>
 *   <li>Providing public methods for every user interaction on this page</li>
 *   <li>Returning values needed for assertions in step definitions</li>
 * </ul>
 *
 * <h2>What this class does NOT do</h2>
 * <ul>
 *   <li>Make assertions — those belong in step definitions</li>
 *   <li>Navigate to other pages — that belongs in {@code PageNavigator}</li>
 *   <li>Access test data — that belongs in {@code UserFactory}</li>
 * </ul>
 */
public class LoginPage extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The username input field.
     */
    @FindBy(id = "user-name")
    private WebElement usernameField;

    /**
     * The password input field.
     */
    @FindBy(id = "password")
    private WebElement passwordField;

    /**
     * The login submit button.
     */
    @FindBy(id = "login-button")
    private WebElement loginButton;

    /**
     * The error message container displayed when login fails.
     */
    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    /**
     * The error message dismiss button (the X icon on the error container).
     */
    @FindBy(css = ".error-button")
    private WebElement errorDismissButton;

    /**
     * The SauceDemo logo on the login page.
     */
    @FindBy(css = ".login_logo")
    private WebElement loginLogo;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code LoginPage} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public LoginPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Enters the given username into the username input field.
     *
     * <p>Clears any existing value before typing to prevent appending
     * to pre-filled content.</p>
     *
     * @param username the username string to enter
     */
    public void enterUsername(String username) {
        type(usernameField, username);
    }

    /**
     * Enters the given password into the password input field.
     *
     * <p>Clears any existing value before typing to prevent appending
     * to pre-filled content.</p>
     *
     * @param password the password string to enter
     */
    public void enterPassword(String password) {
        type(passwordField, password);
    }

    /**
     * Clicks the login submit button.
     *
     * <p>Waits for the button to be clickable before clicking.
     * After clicking, the browser will either navigate to the inventory
     * page on success or display an error message on failure.</p>
     */
    public void clickLoginButton() {
        click(loginButton);
    }

    /**
     * Performs a complete login by entering credentials and clicking the login button.
     *
     * <p>This is a convenience method that combines {@link #enterUsername(String)},
     * {@link #enterPassword(String)}, and {@link #clickLoginButton()} into a
     * single call for scenarios where the individual steps do not need to be
     * asserted independently.</p>
     *
     * @param username the username to enter
     * @param password the password to enter
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Dismisses the error message by clicking the X button on the error container.
     *
     * <p>Only call this method after verifying an error message is displayed.
     * Calling it when no error is present will result in a {@code TimeoutException}.</p>
     */
    public void dismissErrorMessage() {
        click(errorDismissButton);
    }

    /**
     * Clears the username input field.
     *
     * <p>Useful in scenarios that verify behaviour when the username
     * field is intentionally left empty.</p>
     */
    public void clearUsername() {
        type(usernameField, "");
    }

    /**
     * Clears the password input field.
     *
     * <p>Useful in scenarios that verify behaviour when the password
     * field is intentionally left empty.</p>
     */
    public void clearPassword() {
        type(passwordField, "");
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the current text of the error message container.
     *
     * <p>Used in step definitions to assert the correct error message
     * is displayed after a failed login attempt.</p>
     *
     * @return the error message text as a string
     */
    public String getErrorMessageText() {
        return getText(errorMessage);
    }

    /**
     * Returns whether the error message container is currently displayed.
     *
     * <p>Used in step definitions to assert that an error is or is not
     * visible after a login attempt.</p>
     *
     * @return {@code true} if the error message is displayed, {@code false} otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Returns whether the login page logo is visible.
     *
     * <p>Used to verify the login page has loaded correctly before
     * interacting with any login form elements.</p>
     *
     * @return {@code true} if the login logo is visible, {@code false} otherwise
     */
    public boolean isLoginLogoDisplayed() {
        return isDisplayed(loginLogo);
    }

    /**
     * Returns the current value of the username input field.
     *
     * <p>Reads the {@code value} attribute of the input element rather
     * than its text content, as input fields expose their content via
     * the {@code value} attribute in the DOM.</p>
     *
     * @return the current value of the username field as a string
     */
    public String getUsernameFieldValue() {
        return getAttribute(usernameField, "value");
    }

    /**
     * Returns whether the login button is displayed on the page.
     *
     * @return {@code true} if the login button is displayed, {@code false} otherwise
     */
    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }
}