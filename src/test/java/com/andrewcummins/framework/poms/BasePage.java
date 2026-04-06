package com.andrewcummins.framework.poms;

import com.andrewcummins.framework.context.ScenarioContext;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base class for all Page Object Models and Widget POMs in the framework.
 *
 * <p>Every page and widget class extends this base to inherit a consistent,
 * reusable set of interaction and wait methods. This eliminates duplication
 * across page objects and ensures all WebDriver interactions are handled
 * uniformly throughout the framework.</p>
 *
 * <h2>What this class provides</h2>
 * <ul>
 *   <li>Explicit wait methods using {@code WebDriverWait} and {@code ExpectedConditions}</li>
 *   <li>Safe interaction methods (click, type, getText) with built-in waits</li>
 *   <li>JavaScript execution utilities for scenarios where native WebDriver
 *       interactions are insufficient</li>
 *   <li>Dropdown interaction via Selenium's {@code Select} class</li>
 *   <li>Scroll utilities for elements outside the visible viewport</li>
 *   <li>Page state checks (title, URL, element presence)</li>
 * </ul>
 *
 * <h2>Design decisions</h2>
 * <ul>
 *   <li>All interaction methods include explicit waits — no raw {@code findElement}
 *       calls that could fail on slow page loads</li>
 *   <li>The {@code WebDriver} and {@code WebDriverWait} are sourced from
 *       {@code ScenarioContext}, ensuring no static state</li>
 *   <li>{@code PageFactory.initElements} is called in the constructor to
 *       initialise {@code @FindBy} annotated fields in subclasses</li>
 *   <li>All public methods include JavaDoc as per the framework convention</li>
 * </ul>
 */
public abstract class BasePage {

    /**
     * The {@code ScenarioContext} instance providing access to the WebDriver,
     * configuration, and shared scenario state.
     */
    protected final ScenarioContext context;

    /**
     * The {@code WebDriver} instance for this scenario, sourced from {@code ScenarioContext}.
     */
    protected final WebDriver driver;

    /**
     * The {@code WebDriverWait} instance used for all explicit wait operations.
     * Timeout duration is sourced from {@code config.properties} via {@code ConfigReader}.
     */
    protected final WebDriverWait wait;

    /**
     * Constructs a new {@code BasePage} and initialises the WebDriver, wait,
     * and PageFactory elements for the subclass.
     *
     * <p>{@code PageFactory.initElements} processes the {@code @FindBy} annotations
     * in the subclass and creates lazy-loading proxies for each element. The element
     * is not actually located in the DOM until the first interaction — this is
     * intentional and prevents {@code StaleElementReferenceException} on elements
     * that may not be present at the time the page object is instantiated.</p>
     *
     * @param context the {@code ScenarioContext} for the current scenario,
     *                providing access to the driver and configuration
     */
    public BasePage(ScenarioContext context) {
        this.context = context;
        this.driver = context.getDriver();
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(context.getConfigReader().getExplicitWait())
        );
        PageFactory.initElements(driver, this);
    }

    // =========================================================================
    // WAIT METHODS
    // =========================================================================

    /**
     * Waits until the given element is visible in the DOM and on screen.
     *
     * <p>Uses {@code ExpectedConditions.visibilityOf} which waits until the element
     * is both present in the DOM and has non-zero dimensions. An element can be
     * present in the DOM but hidden — this method ensures it is actually visible.</p>
     *
     * @param element the {@code WebElement} to wait for
     * @return the visible {@code WebElement} once the condition is met
     * @throws TimeoutException if the element is not visible within the configured wait duration
     */
    public WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until an element located by the given locator is visible.
     *
     * <p>Use this overload when the element is not yet in the DOM at all —
     * for example, a modal that appears after a button click. Unlike
     * {@link #waitForVisibility(WebElement)}, this method locates the element
     * fresh from the DOM rather than using a pre-existing reference.</p>
     *
     * @param locator the {@code By} locator strategy to find the element
     * @return the visible {@code WebElement} once found
     * @throws TimeoutException if no visible element is found within the wait duration
     */
    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the given element is clickable.
     *
     * <p>{@code ExpectedConditions.elementToBeClickable} waits until the element
     * is both visible and enabled. This is the correct condition to use before
     * clicking — an element can be visible but disabled (e.g. a submit button
     * before a form is complete), and clicking it would either fail or have no effect.</p>
     *
     * @param element the {@code WebElement} to wait for
     * @return the clickable {@code WebElement} once the condition is met
     * @throws TimeoutException if the element is not clickable within the wait duration
     */
    public WebElement waitForClickability(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until the page title contains the given text.
     *
     * <p>Useful for verifying navigation to a new page when the URL alone
     * is not a reliable indicator — for example, SPAs that change the title
     * before updating the URL.</p>
     *
     * @param titleFragment the text expected to appear in the page title
     * @throws TimeoutException if the title does not contain the expected text
     *                          within the wait duration
     */
    public void waitForTitleToContain(String titleFragment) {
        wait.until(ExpectedConditions.titleContains(titleFragment));
    }

    /**
     * Waits until the current URL contains the given text.
     *
     * <p>Used to confirm navigation has completed to the expected page.
     * More reliable than checking the title for URL-based navigation verification.</p>
     *
     * @param urlFragment the text expected to appear in the current URL
     * @throws TimeoutException if the URL does not contain the expected text
     *                          within the wait duration
     */
    public void waitForUrlToContain(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Waits until the given element is no longer visible or is removed from the DOM.
     *
     * <p>Useful for waiting on loading spinners, overlays, or modals to disappear
     * before proceeding with the next interaction.</p>
     *
     * @param element the {@code WebElement} expected to become invisible
     * @throws TimeoutException if the element is still visible after the wait duration
     */
    public void waitForInvisibility(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Clicks the given element after waiting for it to be clickable.
     *
     * <p>Always waits for clickability before clicking rather than clicking
     * immediately. This prevents {@code ElementNotInteractableException} on
     * elements that are present in the DOM but not yet ready for interaction.</p>
     *
     * @param element the {@code WebElement} to click
     * @throws TimeoutException if the element is not clickable within the wait duration
     */
    public void click(WebElement element) {
        waitForClickability(element).click();
    }

    /**
     * Clears the given input field and types the specified text into it.
     *
     * <p>The field is cleared before typing to prevent appending to any
     * existing value. The element is waited on for visibility before interaction.</p>
     *
     * @param element the input {@code WebElement} to type into
     * @param text    the text to enter into the field
     * @throws TimeoutException if the element is not visible within the wait duration
     */
    public void type(WebElement element, String text) {
        waitForVisibility(element).clear();
        element.sendKeys(text);
    }

    /**
     * Retrieves the visible text content of the given element.
     *
     * <p>Waits for the element to be visible before retrieving its text,
     * ensuring the element has fully rendered before the value is read.</p>
     *
     * @param element the {@code WebElement} to retrieve text from
     * @return the visible text content of the element as a string
     * @throws TimeoutException if the element is not visible within the wait duration
     */
    public String getText(WebElement element) {
        return waitForVisibility(element).getText();
    }

    /**
     * Retrieves the value of the specified attribute from the given element.
     *
     * <p>Useful for reading input field values ({@code value} attribute),
     * checking element state ({@code class}, {@code aria-disabled}), or
     * retrieving data attributes ({@code data-*}).</p>
     *
     * @param element       the {@code WebElement} to read the attribute from
     * @param attributeName the name of the attribute to retrieve
     * @return the attribute value as a string, or {@code null} if not present
     * @throws TimeoutException if the element is not visible within the wait duration
     */
    public String getAttribute(WebElement element, String attributeName) {
        waitForVisibility(element);
        return element.getAttribute(attributeName);
    }

    /**
     * Selects an option from a {@code <select>} dropdown by its visible text.
     *
     * <p>Uses Selenium's {@code Select} class which provides robust dropdown
     * interaction. The element is waited on for visibility before selection.</p>
     *
     * @param element     the {@code <select>} {@code WebElement}
     * @param visibleText the visible text of the option to select
     * @throws TimeoutException if the element is not visible within the wait duration
     */
    public void selectByVisibleText(WebElement element, String visibleText) {
        waitForVisibility(element);
        new Select(element).selectByVisibleText(visibleText);
    }

    /**
     * Selects an option from a {@code <select>} dropdown by its value attribute.
     *
     * @param element the {@code <select>} {@code WebElement}
     * @param value   the value attribute of the option to select
     * @throws TimeoutException if the element is not visible within the wait duration
     */
    public void selectByValue(WebElement element, String value) {
        waitForVisibility(element);
        new Select(element).selectByValue(value);
    }

    /**
     * Submits a form by sending the RETURN key to the given element.
     *
     * <p>An alternative to clicking a submit button — useful when the element
     * is an input field and pressing Enter triggers form submission.</p>
     *
     * @param element the {@code WebElement} to send the RETURN key to
     * @throws TimeoutException if the element is not visible within the wait duration
     */
    public void pressEnter(WebElement element) {
        waitForVisibility(element).sendKeys(Keys.RETURN);
    }

    // =========================================================================
    // JAVASCRIPT UTILITIES
    // =========================================================================

    /**
     * Clicks an element using JavaScript execution.
     *
     * <p>Use this as a fallback when the native WebDriver {@link #click(WebElement)}
     * method fails due to an element being obscured by an overlay, or when
     * a click is intercepted by another element. JavaScript bypasses the
     * normal WebDriver click mechanism and triggers the click event directly.</p>
     *
     * <p>Note: Prefer {@link #click(WebElement)} for normal interactions.
     * JavaScript click should only be used when native click fails, as it
     * bypasses certain browser behaviours that your tests should ideally cover.</p>
     *
     * @param element the {@code WebElement} to click via JavaScript
     */
    public void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Scrolls the given element into the visible viewport using JavaScript.
     *
     * <p>Useful for elements below the fold that WebDriver cannot interact with
     * until they are scrolled into view. Uses {@code scrollIntoView} with
     * smooth scrolling behaviour for a more realistic user simulation.</p>
     *
     * @param element the {@code WebElement} to scroll into view
     */
    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                element
        );
    }

    /**
     * Scrolls to the top of the current page using JavaScript.
     */
    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scrolls to the bottom of the current page using JavaScript.
     */
    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);"
        );
    }

    // =========================================================================
    // PAGE STATE METHODS
    // =========================================================================

    /**
     * Returns the current page title as reported by the browser.
     *
     * @return the current page title string
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Returns the current page URL.
     *
     * @return the current URL string
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Checks whether an element is currently displayed on the page.
     *
     * <p>Unlike the wait methods, this does not wait for the element to become
     * visible — it checks the current state immediately. Use this for conditional
     * logic in step definitions where an element may or may not be present.</p>
     *
     * @param element the {@code WebElement} to check
     * @return {@code true} if the element is displayed, {@code false} otherwise
     */
    public boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Checks whether at least one element matching the given locator exists in the DOM.
     *
     * <p>Uses {@code findElements} rather than {@code findElement} to avoid throwing
     * {@code NoSuchElementException} when no match is found. Returns {@code true}
     * if at least one matching element is found, {@code false} otherwise.</p>
     *
     * @param locator the {@code By} locator strategy to search for
     * @return {@code true} if at least one matching element exists, {@code false} otherwise
     */
    public boolean isElementPresent(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty();
    }

    /**
     * Navigates the browser to the given URL.
     *
     * <p>Used by the page navigator to route to specific pages based on the
     * page name passed in from the feature file step.</p>
     *
     * @param url the full URL to navigate to
     */
    public void navigateTo(String url) {
        driver.get(url);
    }

    /**
     * Refreshes the current page.
     */
    public void refreshPage() {
        driver.navigate().refresh();
    }

    /**
     * Navigates back to the previous page in the browser history.
     */
    public void navigateBack() {
        driver.navigate().back();
    }
}