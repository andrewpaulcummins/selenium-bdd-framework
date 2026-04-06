package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.pages.CartPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/**
 * Cucumber step definitions for shopping cart feature file steps.
 *
 * <p>This class contains all step definitions that relate to the cart page
 * of the SauceDemo application, including viewing cart contents, removing
 * items, continuing shopping, and proceeding to checkout.</p>
 *
 * <h2>PicoContainer injection</h2>
 * <p>{@code ScenarioContext} is injected via the constructor by PicoContainer,
 * providing the same shared driver instance used by all other step definition
 * classes in the current scenario.</p>
 */
public class CartSteps {

    /**
     * The shared {@code ScenarioContext} injected by PicoContainer.
     */
    private final ScenarioContext context;

    /**
     * Constructs a new {@code CartSteps} with the injected {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} injected by PicoContainer
     */
    public CartSteps(ScenarioContext context) {
        this.context = context;
    }

    // =========================================================================
    // WHEN STEPS
    // =========================================================================

    /**
     * Removes the given product from the cart via the "Remove" button on the cart page.
     *
     * <p>This step removes a product from the cart page itself. For removing via
     * the inventory page button, use the InventorySteps step instead.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user removes "Sauce Labs Backpack" from the cart page
     * </pre>
     *
     * @param productName the exact visible name of the product to remove
     */
    @When("the user removes {string} from the cart page")
    public void theUserRemovesFromTheCartPage(String productName) {
        new CartPage(context).removeItem(productName);
    }

    /**
     * Clicks the "Continue Shopping" button, returning to the inventory page.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user clicks continue shopping
     * </pre>
     */
    @When("the user clicks continue shopping")
    public void theUserClicksContinueShopping() {
        CartPage cartPage = new CartPage(context);
        cartPage.continueShopping();
        cartPage.waitForUrlToContain("inventory");
        context.setCurrentPageName("inventory");
    }

    /**
     * Clicks the "Checkout" button, proceeding to the checkout information form.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user proceeds to checkout
     * </pre>
     */
    @When("the user proceeds to checkout")
    public void theUserProceedsToCheckout() {
        new CartPage(context).proceedToCheckout();
        context.setCurrentPageName("checkout");
    }

    // =========================================================================
    // THEN STEPS
    // =========================================================================

    /**
     * Asserts that the cart page is currently displayed by checking the page title.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the cart page should be displayed
     * </pre>
     */
    @Then("the cart page should be displayed")
    public void theCartPageShouldBeDisplayed() {
        CartPage cartPage = new CartPage(context);
        Assert.assertTrue(
                cartPage.isPageTitleDisplayed(),
                "Expected the cart page to be displayed but the page title was not found."
        );
        Assert.assertEquals(
                cartPage.getPageTitleText(),
                "Your Cart",
                "Expected cart page title to be 'Your Cart' but was: '" +
                        cartPage.getPageTitleText() + "'."
        );
    }

    /**
     * Asserts that the cart contains a product with the given name.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the cart should contain "Sauce Labs Backpack"
     * </pre>
     *
     * @param productName the exact product name expected in the cart
     */
    @Then("the cart should contain {string}")
    public void theCartShouldContain(String productName) {
        CartPage cartPage = new CartPage(context);
        Assert.assertTrue(
                cartPage.containsItem(productName),
                "Expected cart to contain '" + productName + "' but it was not found. " +
                        "Items in cart: " + cartPage.getCartItemNames()
        );
    }

    /**
     * Asserts that the cart contains no items.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the cart should be empty
     * </pre>
     */
    @Then("the cart should be empty")
    public void theCartShouldBeEmpty() {
        CartPage cartPage = new CartPage(context);
        Assert.assertTrue(
                cartPage.isCartEmpty(),
                "Expected the cart to be empty but it contained " +
                        cartPage.getCartItemCount() + " item(s)."
        );
    }
}