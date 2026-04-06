package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object Model for the SauceDemo inventory page.
 *
 * <p>Encapsulates all element locators and user interactions available
 * on the inventory page ({@code /inventory.html}). This is the main
 * product listing page that users land on after a successful login.</p>
 *
 * <h2>Dynamic element handling</h2>
 * <p>Product elements on this page are rendered as a dynamic list.
 * Rather than declaring individual locators per product, this POM
 * uses {@code List<WebElement>} with {@code @FindBy} to capture
 * all product elements at once, then locates specific items by
 * matching against their text content dynamically.</p>
 *
 * <h2>Widget usage</h2>
 * <p>The header (containing the cart icon, menu button, and page title)
 * is shared across multiple pages. These interactions are delegated to
 * {@link HeaderWidget} rather than being duplicated here.</p>
 */
public class InventoryPage extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The page title element displaying "Products".
     */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /**
     * The product sort dropdown allowing sorting by name or price.
     */
    @FindBy(css = "[data-test='product-sort-container']")
    private WebElement sortDropdown;

    /**
     * The complete list of product name elements on the page.
     * Used for dynamic product lookup by name.
     */
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    /**
     * The complete list of product price elements on the page.
     * Ordered to match {@code productNames} for price lookups by index.
     */
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;

    /**
     * The complete list of "Add to cart" and "Remove" buttons across all products.
     * Used for dynamic add/remove interactions by product name.
     */
    @FindBy(css = ".btn_inventory")
    private List<WebElement> inventoryButtons;

    /**
     * The complete list of product item containers.
     * Each container wraps one product's name, description, price, and button.
     */
    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    /**
     * The shopping cart badge showing the number of items in the cart.
     * Only present in the DOM when at least one item has been added.
     */
    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code InventoryPage} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public InventoryPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Adds the product with the given name to the shopping cart.
     *
     * <p>Locates the product by matching its name against the list of all
     * product name elements, then clicks the corresponding "Add to cart"
     * button at the same index. Fails fast if the product name is not found.</p>
     *
     * @param productName the exact visible name of the product to add to the cart
     * @throws RuntimeException if no product with the given name is found on the page
     */
    public void addProductToCart(String productName) {
        int index = getProductIndexByName(productName);
        click(inventoryButtons.get(index));
    }

    /**
     * Removes the product with the given name from the shopping cart.
     *
     * <p>Locates the product by name and clicks its "Remove" button.
     * This method assumes the product has already been added to the cart —
     * if the button still reads "Add to cart", clicking it will add rather
     * than remove the item.</p>
     *
     * @param productName the exact visible name of the product to remove
     * @throws RuntimeException if no product with the given name is found on the page
     */
    public void removeProductFromCart(String productName) {
        int index = getProductIndexByName(productName);
        click(inventoryButtons.get(index));
    }

    /**
     * Sorts the product list using the sort dropdown.
     *
     * <p>Selects the sort option whose visible text matches the given value.
     * SauceDemo sort options are:</p>
     * <ul>
     *   <li>{@code "Name (A to Z)"}</li>
     *   <li>{@code "Name (Z to A)"}</li>
     *   <li>{@code "Price (low to high)"}</li>
     *   <li>{@code "Price (high to low)"}</li>
     * </ul>
     *
     * @param sortOption the visible text of the sort option to select
     */
    public void sortProductsBy(String sortOption) {
        selectByVisibleText(sortDropdown, sortOption);
    }

    /**
     * Clicks on the product name link to navigate to the product detail page.
     *
     * @param productName the exact visible name of the product to open
     * @throws RuntimeException if no product with the given name is found on the page
     */
    public void openProductDetails(String productName) {
        int index = getProductIndexByName(productName);
        click(productNames.get(index));
    }

    /**
     * Returns a {@link HeaderWidget} instance for interacting with the
     * shared header component on this page.
     *
     * <p>The header contains the cart icon, hamburger menu, and page title.
     * These elements are shared across multiple pages, so all header
     * interactions are delegated to the widget rather than duplicated here.</p>
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
     * Returns the visible title text of the inventory page.
     *
     * <p>The expected value is {@code "Products"}. Used in step definitions
     * to assert the user has successfully navigated to the inventory page.</p>
     *
     * @return the page title text as a string
     */
    public String getPageTitleText() {
        return getText(pageTitle);
    }

    /**
     * Returns the price of the product with the given name.
     *
     * <p>Locates the product by name and retrieves the price text at the
     * corresponding index. The returned string includes the currency symbol
     * (e.g. {@code "$29.99"}).</p>
     *
     * @param productName the exact visible name of the product
     * @return the price string including currency symbol (e.g. "$29.99")
     * @throws RuntimeException if no product with the given name is found
     */
    public String getProductPrice(String productName) {
        int index = getProductIndexByName(productName);
        return getText(productPrices.get(index));
    }

    /**
     * Returns a list of all product names currently displayed on the page.
     *
     * <p>Used in step definitions to assert sort order or verify the
     * complete product list is present.</p>
     *
     * @return a {@code List} of product name strings in their current display order
     */
    public List<String> getAllProductNames() {
        return productNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all product prices currently displayed on the page.
     *
     * <p>Prices are returned as strings including the currency symbol.
     * Used in step definitions to assert sort order by price.</p>
     *
     * @return a {@code List} of price strings in their current display order
     */
    public List<String> getAllProductPrices() {
        return productPrices.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of products currently displayed on the page.
     *
     * @return the total count of product items on the page
     */
    public int getProductCount() {
        return inventoryItems.size();
    }

    /**
     * Returns the current value shown on the shopping cart badge.
     *
     * <p>The badge only appears when at least one item is in the cart.
     * Use {@link #isCartBadgeDisplayed()} to check for its presence
     * before calling this method.</p>
     *
     * @return the cart item count as a string (e.g. "1", "3")
     */
    public String getCartBadgeCount() {
        return getText(cartBadge);
    }

    /**
     * Returns whether the shopping cart badge is currently displayed.
     *
     * <p>The badge is absent from the DOM when the cart is empty and
     * present when at least one item has been added.</p>
     *
     * @return {@code true} if the cart badge is visible, {@code false} if the cart is empty
     */
    public boolean isCartBadgeDisplayed() {
        return isDisplayed(cartBadge);
    }

    /**
     * Returns whether a product with the given name is present on the page.
     *
     * @param productName the product name to search for
     * @return {@code true} if the product is found, {@code false} otherwise
     */
    public boolean isProductDisplayed(String productName) {
        return productNames.stream()
                .anyMatch(element -> element.getText().equals(productName));
    }

    /**
     * Returns the currently selected sort option text from the sort dropdown.
     *
     * @return the visible text of the currently selected sort option
     */
    public String getSelectedSortOption() {
        return getAttribute(sortDropdown, "value");
    }

    /**
     * Returns whether the inventory page title is displayed.
     *
     * <p>Used as a quick check to confirm the inventory page has loaded
     * successfully after login.</p>
     *
     * @return {@code true} if the page title is displayed, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isDisplayed(pageTitle);
    }

    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================

    /**
     * Finds the index of a product in the product list by its exact name.
     *
     * <p>This index is used to locate the corresponding price or button
     * element at the same position in their respective lists. Fails fast
     * with a descriptive error if the product is not found, listing all
     * available products to make debugging straightforward.</p>
     *
     * @param productName the exact visible name of the product to find
     * @return the zero-based index of the product in the product list
     * @throws RuntimeException if no product with the given name exists on the page
     */
    private int getProductIndexByName(String productName) {
        for (int i = 0; i < productNames.size(); i++) {
            if (productNames.get(i).getText().equals(productName)) {
                return i;
            }
        }

        List<String> available = productNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        throw new RuntimeException(
                "[InventoryPage] Product '" + productName + "' was not found on the page. " +
                        "Available products are: " + available + ". " +
                        "Check the product name in your feature file for typos."
        );
    }
}