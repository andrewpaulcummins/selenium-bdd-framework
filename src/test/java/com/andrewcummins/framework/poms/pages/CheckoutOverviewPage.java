package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object Model for the SauceDemo checkout overview page (step two).
 *
 * <p>Encapsulates all element locators and user interactions available
 * on the second checkout step ({@code /checkout-step-two.html}), where the
 * user reviews their order summary before finalising the purchase.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Displaying the ordered items with their prices</li>
 *   <li>Showing payment and shipping information summaries</li>
 *   <li>Displaying the price breakdown (subtotal, tax, total)</li>
 *   <li>Finishing the order via the Finish button</li>
 *   <li>Cancelling the order and returning to the inventory page</li>
 * </ul>
 */
public class CheckoutOverviewPage extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The page title element displaying "Checkout: Overview".
     */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /**
     * The complete list of product name elements in the order summary.
     */
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> itemNames;

    /**
     * The subtotal label showing the item total before tax.
     */
    @FindBy(css = ".summary_subtotal_label")
    private WebElement subtotalLabel;

    /**
     * The tax label showing the calculated tax amount.
     */
    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;

    /**
     * The total label showing the final amount including tax.
     */
    @FindBy(css = ".summary_total_label")
    private WebElement totalLabel;

    /**
     * The "Finish" button that completes the order and navigates to the confirmation page.
     */
    @FindBy(css = "[data-test='finish']")
    private WebElement finishButton;

    /**
     * The "Cancel" button that cancels the order and returns to the inventory page.
     */
    @FindBy(css = "[data-test='cancel']")
    private WebElement cancelButton;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code CheckoutOverviewPage} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public CheckoutOverviewPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Clicks the "Finish" button to complete the order.
     *
     * <p>After clicking, the browser navigates to the order confirmation page.</p>
     */
    public void clickFinish() {
        click(finishButton);
    }

    /**
     * Clicks the "Cancel" button to abandon the order and return to the inventory page.
     */
    public void clickCancel() {
        click(cancelButton);
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the visible title text of the checkout overview page.
     *
     * <p>The expected value is {@code "Checkout: Overview"}.</p>
     *
     * @return the page title text as a string
     */
    public String getPageTitleText() {
        return getText(pageTitle);
    }

    /**
     * Returns whether the checkout overview page title is displayed.
     *
     * @return {@code true} if the page title is displayed, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isDisplayed(pageTitle);
    }

    /**
     * Returns a list of all product names in the order summary.
     *
     * @return a {@code List} of product name strings in their current display order
     */
    public List<String> getItemNames() {
        return itemNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns whether a product with the given name appears in the order summary.
     *
     * @param productName the exact product name to search for
     * @return {@code true} if the product is in the order summary, {@code false} otherwise
     */
    public boolean containsItem(String productName) {
        return itemNames.stream()
                .anyMatch(e -> e.getText().equals(productName));
    }

    /**
     * Returns the total price text including tax.
     *
     * <p>The returned string includes the label prefix (e.g. {@code "Total: $32.39"}).</p>
     *
     * @return the total label text as a string
     */
    public String getTotalText() {
        return getText(totalLabel);
    }

    /**
     * Returns the subtotal text before tax.
     *
     * @return the subtotal label text as a string
     */
    public String getSubtotalText() {
        return getText(subtotalLabel);
    }
}