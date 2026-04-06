package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/**
 * Cucumber step definitions for header navigation feature file steps.
 *
 * <p>This class contains all step definitions that relate to the shared header
 * component of the SauceDemo application, including the burger menu, cart icon,
 * logout, and app logo. The header is present on all post-login pages, so these
 * steps can be used in any feature that has a logged-in user.</p>
 *
 * <h2>Direct widget instantiation</h2>
 * <p>Because the header appears on every post-login page, this class instantiates
 * {@link HeaderWidget} directly rather than going through a specific page POM.
 * This avoids coupling navigation steps to any particular page type.</p>
 *
 * <h2>PicoContainer injection</h2>
 * <p>{@code ScenarioContext} is injected via the constructor by PicoContainer,
 * providing the same shared driver instance used by all other step definition
 * classes in the current scenario.</p>
 */
public class NavigationSteps {

    /**
     * The shared {@code ScenarioContext} injected by PicoContainer.
     */
    private final ScenarioContext context;

    /**
     * Constructs a new {@code NavigationSteps} with the injected {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} injected by PicoContainer
     */
    public NavigationSteps(ScenarioContext context) {
        this.context = context;
    }

    // =========================================================================
    // WHEN STEPS
    // =========================================================================

    /**
     * Logs the user out by opening the burger menu and clicking the logout link.
     *
     * <p>After logout, the browser is redirected to the login page.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user logs out
     * </pre>
     */
    @When("the user logs out")
    public void theUserLogsOut() {
        new HeaderWidget(context).logout();
        context.setCurrentPageName("login");
    }

    /**
     * Opens the burger menu sidebar by clicking the hamburger icon in the header.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user opens the burger menu
     * </pre>
     */
    @When("the user opens the burger menu")
    public void theUserOpensTheBurgerMenu() {
        new HeaderWidget(context).openBurgerMenu();
    }

    /**
     * Closes the burger menu sidebar by clicking the X close button.
     *
     * <p>The burger menu must already be open before calling this step.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user closes the burger menu
     * </pre>
     */
    @When("the user closes the burger menu")
    public void theUserClosesTheBurgerMenu() {
        new HeaderWidget(context).closeBurgerMenu();
    }

    /**
     * Clicks the "All Items" link in the open burger menu sidebar.
     *
     * <p>Navigates to the inventory page from any post-login page.
     * The burger menu must already be open before calling this step.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user clicks all items in the burger menu
     * </pre>
     */
    @When("the user clicks all items in the burger menu")
    public void theUserClicksAllItemsInTheBurgerMenu() {
        new HeaderWidget(context).clickAllItems();
        context.setCurrentPageName("inventory");
    }

    /**
     * Clicks the shopping cart icon in the header, navigating to the cart page.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user clicks the cart icon
     * </pre>
     */
    @When("the user clicks the cart icon")
    public void theUserClicksTheCartIcon() {
        HeaderWidget header = new HeaderWidget(context);
        header.clickCartIcon();
        header.waitForUrlToContain("cart");
        context.setCurrentPageName("cart");
    }

    // =========================================================================
    // THEN STEPS
    // =========================================================================

    /**
     * Asserts that the burger menu sidebar is currently open.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the burger menu should be open
     * </pre>
     */
    @Then("the burger menu should be open")
    public void theBurgerMenuShouldBeOpen() {
        Assert.assertTrue(
                new HeaderWidget(context).isMenuOpen(),
                "Expected the burger menu to be open but it was closed."
        );
    }

    /**
     * Asserts that the burger menu sidebar is currently closed.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the burger menu should be closed
     * </pre>
     */
    @Then("the burger menu should be closed")
    public void theBurgerMenuShouldBeClosed() {
        Assert.assertFalse(
                new HeaderWidget(context).isMenuOpen(),
                "Expected the burger menu to be closed but it was open."
        );
    }

    /**
     * Asserts that the application logo is displayed in the header.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the header logo should be displayed
     * </pre>
     */
    @Then("the header logo should be displayed")
    public void theHeaderLogoShouldBeDisplayed() {
        Assert.assertTrue(
                new HeaderWidget(context).isAppLogoDisplayed(),
                "Expected the header logo to be displayed but it was not found."
        );
    }
}