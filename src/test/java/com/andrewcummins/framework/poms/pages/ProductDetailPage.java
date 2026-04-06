package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the SauceDemo product detail page.
 *
 * <p>Encapsulates all element locators and user interactions available
 * on the product detail page ({@code /inventory-item.html?id=X}). Users
 * reach this page by clicking a product name or image on the inventory page.</p>
 *
 * <h2>Navigation note</h2>
 * <p>This page is navigated to via a product link click on the inventory page
 * rather than a direct URL, as the URL requires a product ID query parameter
 * that varies per product. Use {@link InventoryPage#openProductDetails(String)}
 * to navigate here.</p>
 *
 * <h2>Widget usage</h2>
 * <p>Header interactions are delegated to {@link HeaderWidget} via
 * {@link #getHeader()} rather than duplicating header element locators here.</p>
 */
public class ProductDetailPage extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The product name heading on the detail page.
     */
    @FindBy(css = ".inventory_details_name")
    private WebElement productName;

    /**
     * The product description text on the detail page.
     */
    @FindBy(css = ".inventory_details_desc")
    private WebElement productDescription;

    /**
     * The product price on the detail page.
     */
    @FindBy(css = ".inventory_details_price")
    private WebElement productPrice;

    /**
     * The "Add to cart" button on the detail page.
     */
    @FindBy(css = ".btn_primary")
    private WebElement addToCartButton;

    /**
     * The "Back to products" link that returns the user to the inventory page.
     */
    @FindBy(css = "[data-test='back-to-products']")
    private WebElement backButton;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code ProductDetailPage} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public ProductDetailPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Clicks the "Add to cart" button on the product detail page.
     */
    public void addToCart() {
        click(addToCartButton);
    }

    /**
     * Clicks the "Back to products" link, returning to the inventory page.
     */
    public void goBack() {
        click(backButton);
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
     * Returns the product name text displayed on the detail page.
     *
     * @return the product name as a string
     */
    public String getProductNameText() {
        return getText(productName);
    }

    /**
     * Returns the product description text displayed on the detail page.
     *
     * @return the product description as a string
     */
    public String getProductDescriptionText() {
        return getText(productDescription);
    }

    /**
     * Returns the product price displayed on the detail page.
     *
     * <p>The returned string includes the currency symbol (e.g. {@code "$29.99"}).</p>
     *
     * @return the product price as a string
     */
    public String getProductPriceText() {
        return getText(productPrice);
    }

    /**
     * Returns whether the product name is currently displayed on the page.
     *
     * <p>Used as a quick check to confirm the product detail page has loaded
     * successfully after clicking a product link.</p>
     *
     * @return {@code true} if the product name is displayed, {@code false} otherwise
     */
    public boolean isProductNameDisplayed() {
        return isDisplayed(productName);
    }
}