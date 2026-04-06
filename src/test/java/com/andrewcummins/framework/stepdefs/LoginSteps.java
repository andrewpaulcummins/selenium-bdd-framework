package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.factory.UserFactory;
import com.andrewcummins.framework.models.User;
import com.andrewcummins.framework.navigation.PageNavigator;
import com.andrewcummins.framework.poms.pages.InventoryPage;
import com.andrewcummins.framework.poms.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/**
 * Cucumber step definitions for login-related feature file steps.
 *
 * <p>This class contains all step definitions that relate to the login
 * functionality of the SauceDemo application. It acts as the glue layer
 * between the Gherkin feature files and the framework's page objects,
 * factories, and navigation components.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Mapping Gherkin steps to Java methods via Cucumber annotations</li>
 *   <li>Orchestrating calls to {@code UserFactory}, {@code PageNavigator},
 *       and page POMs</li>
 *   <li>Performing assertions using TestNG's {@code Assert} class</li>
 * </ul>
 *
 * <h2>PicoContainer injection</h2>
 * <p>{@code ScenarioContext} is injected via the constructor by PicoContainer.
 * This is the same instance shared with {@code Hooks} and any other step
 * definition classes in the current scenario, providing access to the
 * WebDriver, current user, and all shared state without static variables.</p>
 *
 * <h2>What this class does NOT do</h2>
 * <ul>
 *   <li>Interact with WebDriver directly</li>
 *   <li>Contain element locators</li>
 *   <li>Contain business logic beyond orchestration</li>
 * </ul>
 */
public class LoginSteps {

    /**
     * The shared {@code ScenarioContext} injected by PicoContainer.
     */
    private final ScenarioContext context;

    /**
     * The {@code PageNavigator} used to route to pages by name string.
     */
    private final PageNavigator pageNavigator;

    /**
     * The {@code UserFactory} used to build {@code User} objects by type string.
     */
    private final UserFactory userFactory;

    /**
     * Constructs a new {@code LoginSteps} with the injected {@code ScenarioContext}.
     *
     * <p>PicoContainer automatically injects the shared {@code ScenarioContext}
     * instance here. {@code PageNavigator} and {@code UserFactory} are
     * initialised using the context so they share the same driver and
     * configuration for the duration of the scenario.</p>
     *
     * @param context the {@code ScenarioContext} injected by PicoContainer
     */
    public LoginSteps(ScenarioContext context) {
        this.context = context;
        this.pageNavigator = new PageNavigator(context);
        this.userFactory = new UserFactory(context.getJsonDataReader());
    }

    // =========================================================================
    // GIVEN STEPS
    // =========================================================================

    /**
     * Navigates to the specified page as the specified user type.
     *
     * <p>This is the core dynamic navigation step that handles all user types
     * and all pages from a single step definition. The user type and page name
     * are captured as parameters from the feature file and used to:</p>
     * <ol>
     *   <li>Load and decrypt the correct user from test data via {@code UserFactory}</li>
     *   <li>Store the user in {@code ScenarioContext} for use in later steps</li>
     *   <li>Navigate to the correct URL via {@code PageNavigator}</li>
     *   <li>Store the current page name in {@code ScenarioContext}</li>
     * </ol>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Given a "standard" user is on the "login" page
     *   Given a "locked" user is on the "login" page
     * </pre>
     *
     * @param userType the user type string from the feature file (e.g. "standard")
     * @param pageName the page name string from the feature file (e.g. "login")
     */
    @Given("a {string} user is on the {string} page")
    public void aUserIsOnThePage(String userType, String pageName) {
        User user = userFactory.getUser(userType);
        context.setCurrentUser(user);
        pageNavigator.navigateTo(pageName);
    }

    // =========================================================================
    // WHEN STEPS
    // =========================================================================

    /**
     * Performs a complete login using the current user's credentials.
     *
     * <p>Retrieves the current user from {@code ScenarioContext} and uses
     * their decrypted username and password to log in via {@code LoginPage}.
     * The user must have been set by a preceding Given step.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user logs in
     * </pre>
     */
    @When("the user logs in")
    public void theUserLogsIn() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        User currentUser = context.getCurrentUser();
        loginPage.login(currentUser.getUsername(), currentUser.getPassword());
    }

    /**
     * Enters only the username of the current user without submitting the form.
     *
     * <p>Used in scenarios that test partial form submission or validate
     * field-level behaviour independently.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user enters their username
     * </pre>
     */
    @When("the user enters their username")
    public void theUserEntersTheirUsername() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        loginPage.enterUsername(context.getCurrentUser().getUsername());
    }

    /**
     * Enters only the password of the current user without submitting the form.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user enters their password
     * </pre>
     */
    @When("the user enters their password")
    public void theUserEntersTheirPassword() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        loginPage.enterPassword(context.getCurrentUser().getPassword());
    }

    /**
     * Clicks the login button without entering any credentials.
     *
     * <p>Used in scenarios that verify the error state when the login
     * button is clicked with empty fields.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user clicks the login button
     * </pre>
     */
    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        loginPage.clickLoginButton();
    }

    // =========================================================================
    // THEN STEPS
    // =========================================================================

    /**
     * Asserts that the user has been navigated to the specified page after login.
     *
     * <p>Verifies navigation by checking the current URL contains the
     * expected page path. This is more reliable than checking the page
     * title, which may load slightly after the URL changes.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the user should be on the "inventory" page
     * </pre>
     *
     * @param pageName the expected page name (e.g. "inventory", "login")
     */
    @Then("the user should be on the {string} page")
    public void theUserShouldBeOnThePage(String pageName) {
        String currentUrl = context.getDriver().getCurrentUrl();
        String expectedUrlFragment = pageName.toLowerCase().equals("login")
                ? context.getConfigReader().getUiBaseUrl()
                : pageName.toLowerCase();

        Assert.assertTrue(
                currentUrl.contains(expectedUrlFragment),
                "Expected to be on the '" + pageName + "' page but current URL was: " + currentUrl
        );

        context.setCurrentPageName(pageName.toLowerCase());
    }

    /**
     * Asserts that an error message is displayed on the login page.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then an error message should be displayed
     * </pre>
     */
    @Then("an error message should be displayed")
    public void anErrorMessageShouldBeDisplayed() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "Expected an error message to be displayed but none was found."
        );
    }

    /**
     * Asserts that the error message displayed matches the expected text.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the error message should contain "Epic sadface: Sorry, this user has been locked out."
     * </pre>
     *
     * @param expectedMessage the expected error message text
     */
    @Then("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedMessage) {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        String actualMessage = loginPage.getErrorMessageText();
        Assert.assertTrue(
                actualMessage.contains(expectedMessage),
                "Expected error message to contain '" + expectedMessage +
                        "' but actual message was: '" + actualMessage + "'"
        );
    }

    /**
     * Asserts that the inventory page title is displayed after a successful login.
     *
     * <p>Confirms that the user has fully landed on the inventory page
     * and the page content has rendered correctly.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the inventory page should be displayed
     * </pre>
     */
    @Then("the inventory page should be displayed")
    public void theInventoryPageShouldBeDisplayed() {
        InventoryPage inventoryPage = new InventoryPage(context);
        Assert.assertTrue(
                inventoryPage.isPageTitleDisplayed(),
                "Expected the inventory page to be displayed but the page title was not found."
        );
        Assert.assertEquals(
                inventoryPage.getPageTitleText(),
                "Products",
                "Expected page title to be 'Products' but was: " +
                        inventoryPage.getPageTitleText()
        );
    }

    /**
     * Asserts that no error message is currently displayed on the login page.
     *
     * <p>Used to verify a clean login page state before attempting login,
     * or after an error message has been dismissed.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then no error message should be displayed
     * </pre>
     */
    @Then("no error message should be displayed")
    public void noErrorMessageShouldBeDisplayed() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        Assert.assertFalse(
                loginPage.isErrorMessageDisplayed(),
                "Expected no error message to be displayed but one was found."
        );
    }
}