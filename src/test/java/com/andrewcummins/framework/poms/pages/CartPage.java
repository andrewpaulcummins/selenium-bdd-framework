package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object Model for the SauceDemo shopping cart page.
 *
 * <p>Encapsulates all element locators and user interactions available
 * on the cart page ({@code /cart.html}). Users reach this page by clicking
 * the cart icon in the header from any post-login page.</p>
 *
 * <h2>Dynamic element handling</h2>
 * <p>Cart items are rendered as a dynamic list. Item removal is handled by
 * matching the product name against the list of cart item names and clicking
 * the corresponding remove button at the same index.</p>
 *
 * <h2>Widget usage</h2>
 * <p>Header interactions are delegated to {@link HeaderWidget} via
 * {@link #getHeader()} rather than duplicating header element locators here.</p>
 */
public class CartPage extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The page title element displaying "Your Cart".
     */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /**
     * The complete list of product name elements currently in the cart.
     */
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> cartItemNames;

    /**
     * The complete list of product price elements in the cart.
     */
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> cartItemPrices;

    /**
     * The complete list of "Remove" buttons, one per cart item.
     * Ordered to match {@code cartItemNames} for removal by index.
     */
    @FindBy(css = ".btn_secondary")
    private List<WebElement> removeButtons;

    /**
     * The complete list of cart item containers.
     * Used to determine whether the cart is empty.
     */
    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    /**
     * The "Continue Shopping" button that returns the user to the inventory page.
     */
    @FindBy(css = "[data-test='continue-shopping']")
    private WebElement continueShoppingButton;

    /**
     * The "Checkout" button that proceeds to the checkout information form.
     */
    @FindBy(css = "[data-test='checkout']")
    private WebElement checkoutButton;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code CartPage} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public CartPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Removes the product with the given name from the cart.
     *
     * <p>Locates the product by matching its name against the list of cart
     * item names and clicks the corresponding "Remove" button at the same index.
     * Fails fast if the product name is not found in the cart.</p>
     *
     * @param productName the exact visible name of the product to remove
     * @throws RuntimeException if no item with the given name is found in the cart
     */
    public void removeItem(String productName) {
        int index = getItemIndexByName(productName);
        click(removeButtons.get(index));
    }

    /**
     * Clicks the "Continue Shopping" button, returning to the inventory page.
     */
    public void continueShopping() {
        continueShoppingButton.click();
    }

    /**
     * Clicks the "Checkout" button, proceeding to the checkout information form.
     */
    public void proceedToCheckout() {
        checkoutButton.click();
    }

    /**
     * Returns a {@link HeaderWidget} instance for interacting with the
     * shared header component on this page.
     *
     * @return a new {@code HeaderWidget} instance bound to the current driver
     */
    public HeaderWidget getHeader() {
        return new HeaderWidget(context);
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the visible title text of the cart page.
     *
     * <p>The expected value is {@code "Your Cart"}.</p>
     *
     * @return the page title text as a string
     */
    public String getPageTitleText() {
        return getText(pageTitle);
    }

    /**
     * Returns whether the cart page title is displayed.
     *
     * @return {@code true} if the page title is displayed, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isDisplayed(pageTitle);
    }

    /**
     * Returns a list of all product names currently in the cart.
     *
     * @return a {@code List} of product name strings in their current display order
     */
    public List<String> getCartItemNames() {
        return cartItemNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns whether a product with the given name is present in the cart.
     *
     * @param productName the exact product name to search for
     * @return {@code true} if the product is in the cart, {@code false} otherwise
     */
    public boolean containsItem(String productName) {
        return cartItemNames.stream()
                .anyMatch(e -> e.getText().equals(productName));
    }

    /**
     * Returns the number of items currently in the cart.
     *
     * @return the total count of cart items
     */
    public int getCartItemCount() {
        return cartItems.size();
    }

    /**
     * Returns whether the cart is currently empty.
     *
     * @return {@code true} if no items are in the cart, {@code false} otherwise
     */
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================

    /**
     * Finds the index of a cart item by its exact product name.
     *
     * <p>Used to locate the corresponding remove button at the same index.
     * Fails fast with a descriptive error listing all cart items if the
     * product name is not found.</p>
     *
     * @param productName the exact visible name of the product to find
     * @return the zero-based index of the item in the cart list
     * @throws RuntimeException if no item with the given name is in the cart
     */
    private int getItemIndexByName(String productName) {
        for (int i = 0; i < cartItemNames.size(); i++) {
            if (cartItemNames.get(i).getText().equals(productName)) {
                return i;
            }
        }
        List<String> available = cartItemNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        throw new RuntimeException(
                "[CartPage] Item '" + productName + "' was not found in the cart. " +
                        "Items in cart: " + available + ". " +
                        "Check the product name in your feature file for typos."
        );
    }
}