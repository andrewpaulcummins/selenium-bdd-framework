package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.pages.CheckoutCompletePage;
import com.andrewcummins.framework.poms.pages.CheckoutOverviewPage;
import com.andrewcummins.framework.poms.pages.CheckoutPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/**
 * Cucumber step definitions for checkout-related feature file steps.
 *
 * <p>This class contains all step definitions that relate to the multi-step
 * checkout flow of the SauceDemo application: the information form (step one),
 * the order overview (step two), and the order confirmation page.</p>
 *
 * <h2>PicoContainer injection</h2>
 * <p>{@code ScenarioContext} is injected via the constructor by PicoContainer,
 * providing the same shared driver instance used by all other step definition
 * classes in the current scenario.</p>
 */
public class CheckoutSteps {

    /**
     * The shared {@code ScenarioContext} injected by PicoContainer.
     */
    private final ScenarioContext context;

    /**
     * Constructs a new {@code CheckoutSteps} with the injected {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} injected by PicoContainer
     */
    public CheckoutSteps(ScenarioContext context) {
        this.context = context;
    }

    // =========================================================================
    // WHEN STEPS
    // =========================================================================

    /**
     * Fills all three checkout information fields with the given values.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user enters first name "John", last name "Doe" and postal code "12345"
     * </pre>
     *
     * @param firstName  the first name to enter
     * @param lastName   the last name to enter
     * @param postalCode the postal code to enter
     */
    @When("the user enters first name {string}, last name {string} and postal code {string}")
    public void theUserEntersCheckoutDetails(String firstName, String lastName, String postalCode) {
        new CheckoutPage(context).fillForm(firstName, lastName, postalCode);
    }

    /**
     * Enters only the first name on the checkout information form.
     *
     * <p>Used in scenarios that test form validation when the last name
     * or postal code field is left empty.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user enters first name "John"
     * </pre>
     *
     * @param firstName the first name to enter
     */
    @When("the user enters first name {string}")
    public void theUserEntersFirstName(String firstName) {
        new CheckoutPage(context).enterFirstName(firstName);
    }

    /**
     * Enters the first name and last name on the checkout information form,
     * leaving the postal code field empty.
     *
     * <p>Used in scenarios that test postal code field validation.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user enters first name "John" and last name "Doe"
     * </pre>
     *
     * @param firstName the first name to enter
     * @param lastName  the last name to enter
     */
    @When("the user enters first name {string} and last name {string}")
    public void theUserEntersFirstNameAndLastName(String firstName, String lastName) {
        CheckoutPage checkoutPage = new CheckoutPage(context);
        checkoutPage.enterFirstName(firstName);
        checkoutPage.enterLastName(lastName);
    }

    /**
     * Clicks the "Continue" button on the checkout information form.
     *
     * <p>If all required fields are filled, this advances to the checkout
     * overview page. If any required field is missing, a validation error
     * is displayed instead.</p>
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user continues to the checkout overview
     * </pre>
     */
    @When("the user continues to the checkout overview")
    public void theUserContinuesToTheCheckoutOverview() {
        new CheckoutPage(context).clickContinue();
    }

    /**
     * Clicks the "Finish" button on the checkout overview page to place the order.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user finishes the order
     * </pre>
     */
    @When("the user finishes the order")
    public void theUserFinishesTheOrder() {
        new CheckoutOverviewPage(context).clickFinish();
    }

    /**
     * Clicks the "Cancel" button on the checkout information form, returning to the cart.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   When the user cancels checkout
     * </pre>
     */
    @When("the user cancels checkout")
    public void theUserCancelsCheckout() {
        new CheckoutPage(context).clickCancel();
        context.setCurrentPageName("cart");
    }

    // =========================================================================
    // THEN STEPS
    // =========================================================================

    /**
     * Asserts that the checkout information page shows a validation error
     * containing the given text.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the checkout page should display an error containing "First Name is required"
     * </pre>
     *
     * @param expectedError the text expected to appear in the error message
     */
    @Then("the checkout page should display an error containing {string}")
    public void theCheckoutPageShouldDisplayAnErrorContaining(String expectedError) {
        CheckoutPage checkoutPage = new CheckoutPage(context);
        Assert.assertTrue(
                checkoutPage.isErrorMessageDisplayed(),
                "Expected an error message on the checkout page but none was displayed."
        );
        Assert.assertTrue(
                checkoutPage.getErrorMessageText().contains(expectedError),
                "Expected error to contain '" + expectedError + "' but was: '" +
                        checkoutPage.getErrorMessageText() + "'."
        );
    }

    /**
     * Asserts that the checkout overview page is currently displayed.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the checkout overview page should be displayed
     * </pre>
     */
    @Then("the checkout overview page should be displayed")
    public void theCheckoutOverviewPageShouldBeDisplayed() {
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(context);
        Assert.assertTrue(
                overviewPage.isPageTitleDisplayed(),
                "Expected checkout overview page to be displayed but the title was not found."
        );
        Assert.assertEquals(
                overviewPage.getPageTitleText(),
                "Checkout: Overview",
                "Expected page title 'Checkout: Overview' but was: '" +
                        overviewPage.getPageTitleText() + "'."
        );
    }

    /**
     * Asserts that the order summary on the checkout overview page contains
     * a product with the given name.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the order summary should contain "Sauce Labs Backpack"
     * </pre>
     *
     * @param productName the exact product name expected in the order summary
     */
    @Then("the order summary should contain {string}")
    public void theOrderSummaryShouldContain(String productName) {
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(context);
        Assert.assertTrue(
                overviewPage.containsItem(productName),
                "Expected order summary to contain '" + productName + "' but it was not found. " +
                        "Items in summary: " + overviewPage.getItemNames()
        );
    }

    /**
     * Asserts that the order confirmation page is displayed after a successful checkout.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the order confirmation page should be displayed
     * </pre>
     */
    @Then("the order confirmation page should be displayed")
    public void theOrderConfirmationPageShouldBeDisplayed() {
        CheckoutCompletePage completePage = new CheckoutCompletePage(context);
        Assert.assertTrue(
                completePage.isPageTitleDisplayed(),
                "Expected the order confirmation page to be displayed but the title was not found."
        );
        Assert.assertEquals(
                completePage.getPageTitleText(),
                "Checkout: Complete!",
                "Expected page title 'Checkout: Complete!' but was: '" +
                        completePage.getPageTitleText() + "'."
        );
    }

    /**
     * Asserts that the order confirmation header contains the given text.
     *
     * <p>Example feature file usage:</p>
     * <pre>
     *   Then the order confirmation should contain "Thank you for your order!"
     * </pre>
     *
     * @param expectedText the text expected to appear in the confirmation header
     */
    @Then("the order confirmation should contain {string}")
    public void theOrderConfirmationShouldContain(String expectedText) {
        CheckoutCompletePage completePage = new CheckoutCompletePage(context);
        String actualHeader = completePage.getConfirmationHeaderText();
        Assert.assertTrue(
                actualHeader.contains(expectedText),
                "Expected confirmation to contain '" + expectedText + "' but was: '" +
                        actualHeader + "'."
        );
    }
}