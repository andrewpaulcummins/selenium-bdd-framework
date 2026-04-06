package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.pages.InventoryPage;
import com.andrewcummins.framework.poms.pages.ProductDetailPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Cucumber step definitions for inventory-related feature file steps.
 *
 * <p>This class contains all step definitions that relate to the inventory
 * (product listing) page of the SauceDemo application, including product
 * sorting, adding and removing items from the cart, and navigating to
 * product detail pages.</p>
 *
 * <h2>PicoContainer injection</h2>
 * <p>{@code ScenarioContext} is injected via the constructor by PicoContainer,
 * providing the same shared driver instance used by all other step definition
 * classes in the current scenario.</p>
 */
public class InventorySteps {

    /**
     * The shared {@code ScenarioContext} injected by PicoContainer.
     */
    private final ScenarioContext context;

    /**
     * Constructs a new {@code InventorySteps} with the injected {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} injected by PicoContainer
     */
    public InventorySteps(ScenarioContext context) {
        this.context = context;
    }

    // =========================================================================
    // WHEN STEPS
    // =========================================================================

    /**
     * Selects the given sort option from the product sort dropdown.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user sorts products by "Name (Z to A)"
     *   When the user sorts products by "Price (low to high)"
     * </pre>
     *
     * @param sortOption the visible text of the sort option to select
     */
    @When("the user sorts products by {string}")
    public void theUserSortsProductsBy(String sortOption) {
        new InventoryPage(context).sortProductsBy(sortOption);
    }

    /**
     * Adds the product with the given name to the shopping cart from the inventory page.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user adds "Sauce Labs Backpack" to the cart
     * </pre>
     *
     * @param productName the exact visible name of the product to add
     */
    @When("the user adds {string} to the cart")
    public void theUserAddsToTheCart(String productName) {
        new InventoryPage(context).addProductToCart(productName);
    }

    /**
     * Removes the product with the given name from the cart via its inventory page button.
     *
     * <p>This step removes a product from the cart using the "Remove" button on the
     * inventory page. For removing from the cart page itself, use the CartSteps step.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user removes "Sauce Labs Backpack" from the cart
     * </pre>
     *
     * @param productName the exact visible name of the product to remove
     */
    @When("the user removes {string} from the cart")
    public void theUserRemovesFromTheCart(String productName) {
        new InventoryPage(context).removeProductFromCart(productName);
    }

    /**
     * Clicks the product name link to navigate to the product detail page.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user opens the product details for "Sauce Labs Backpack"
     * </pre>
     *
     * @param productName the exact visible name of the product to open
     */
    @When("the user opens the product details for {string}")
    public void theUserOpensTheProductDetailsFor(String productName) {
        new InventoryPage(context).openProductDetails(productName);
        context.setCurrentPageName("product-detail");
    }

    // =========================================================================
    // THEN STEPS
    // =========================================================================

    /**
     * Asserts that the inventory page displays the expected number of products.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the inventory page displays 6 products
     * </pre>
     *
     * @param expectedCount the expected number of products on the page
     */
    @Then("the inventory page displays {int} products")
    public void theInventoryPageDisplaysProducts(int expectedCount) {
        InventoryPage inventoryPage = new InventoryPage(context);
        int actualCount = inventoryPage.getProductCount();
        Assert.assertEquals(
                actualCount,
                expectedCount,
                "Expected " + expectedCount + " products but found " + actualCount + "."
        );
    }

    /**
     * Asserts that the products are sorted alphabetically from A to Z.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the products should be sorted by name in ascending order
     * </pre>
     */
    @Then("the products should be sorted by name in ascending order")
    public void theProductsShouldBeSortedByNameAscending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<String> actual = inventoryPage.getAllProductNames();
        List<String> expected = new ArrayList<>(actual);
        expected.sort(String::compareTo);
        Assert.assertEquals(actual, expected, "Products are not sorted by name A to Z.");
    }

    /**
     * Asserts that the products are sorted alphabetically from Z to A.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the products should be sorted by name in descending order
     * </pre>
     */
    @Then("the products should be sorted by name in descending order")
    public void theProductsShouldBeSortedByNameDescending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<String> actual = inventoryPage.getAllProductNames();
        List<String> expected = new ArrayList<>(actual);
        expected.sort((a, b) -> b.compareTo(a));
        Assert.assertEquals(actual, expected, "Products are not sorted by name Z to A.");
    }

    /**
     * Asserts that the products are sorted by price from lowest to highest.
     *
     * <p>Prices are parsed from their string representation (e.g. {@code "$9.99"})
     * to doubles for numeric comparison.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the products should be sorted by price in ascending order
     * </pre>
     */
    @Then("the products should be sorted by price in ascending order")
    public void theProductsShouldBeSortedByPriceAscending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<Double> actual = parsePrices(inventoryPage.getAllProductPrices());
        List<Double> expected = new ArrayList<>(actual);
        expected.sort(Double::compareTo);
        Assert.assertEquals(actual, expected, "Products are not sorted by price low to high.");
    }

    /**
     * Asserts that the products are sorted by price from highest to lowest.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the products should be sorted by price in descending order
     * </pre>
     */
    @Then("the products should be sorted by price in descending order")
    public void theProductsShouldBeSortedByPriceDescending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<Double> actual = parsePrices(inventoryPage.getAllProductPrices());
        List<Double> expected = new ArrayList<>(actual);
        expected.sort((a, b) -> b.compareTo(a));
        Assert.assertEquals(actual, expected, "Products are not sorted by price high to low.");
    }

    /**
     * Asserts that the cart badge displays the expected item count.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the cart badge should show 1
     *   Then the cart badge should show 2
     * </pre>
     *
     * @param expectedCount the expected number shown on the cart badge
     */
    @Then("the cart badge should show {int}")
    public void theCartBadgeShouldShow(int expectedCount) {
        InventoryPage inventoryPage = new InventoryPage(context);
        Assert.assertTrue(
                inventoryPage.isCartBadgeDisplayed(),
                "Expected cart badge to be displayed but it was not."
        );
        Assert.assertEquals(
                inventoryPage.getCartBadgeCount(),
                String.valueOf(expectedCount),
                "Expected cart badge to show " + expectedCount +
                        " but it showed " + inventoryPage.getCartBadgeCount() + "."
        );
    }

    /**
     * Asserts that the cart badge is not displayed, indicating an empty cart.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the cart badge should not be displayed
     * </pre>
     */
    @Then("the cart badge should not be displayed")
    public void theCartBadgeShouldNotBeDisplayed() {
        InventoryPage inventoryPage = new InventoryPage(context);
        inventoryPage.waitForCartBadgeToDisappear();
        Assert.assertFalse(
                inventoryPage.isCartBadgeDisplayed(),
                "Expected cart badge to not be displayed but it was visible."
        );
    }

    /**
     * Asserts that the product detail page is displayed for the given product name.
     *
     * <p>Verifies both that the product name element is visible and that it
     * matches the expected product name exactly.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the product detail page should be displayed for "Sauce Labs Backpack"
     * </pre>
     *
     * @param productName the expected product name on the detail page
     */
    @Then("the product detail page should be displayed for {string}")
    public void theProductDetailPageShouldBeDisplayedFor(String productName) {
        ProductDetailPage productDetailPage = new ProductDetailPage(context);
        Assert.assertTrue(
                productDetailPage.isProductNameDisplayed(),
                "Expected product detail page to be displayed but the product name was not found."
        );
        Assert.assertEquals(
                productDetailPage.getProductNameText(),
                productName,
                "Expected product name '" + productName + "' but found '" +
                        productDetailPage.getProductNameText() + "'."
        );
    }

    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================

    /**
     * Parses a list of price strings (e.g. "$9.99") into a list of doubles.
     *
     * @param priceStrings the list of price strings to parse
     * @return a list of price values as doubles
     */
    private List<Double> parsePrices(List<String> priceStrings) {
        List<Double> prices = new ArrayList<>();
        for (String price : priceStrings) {
            prices.add(Double.parseDouble(price.replace("$", "")));
        }
        return prices;
    }
}