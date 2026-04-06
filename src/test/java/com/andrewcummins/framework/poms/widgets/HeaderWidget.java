package com.andrewcummins.framework.poms.widgets;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Widget POM for the SauceDemo shared header component.
 *
 * <p>The header is a reusable UI component that appears on every post-login
 * page in the SauceDemo application, including the inventory, cart, checkout,
 * and order complete pages. Encapsulating it here ensures that header
 * interactions are defined once and reused across all pages that contain it,
 * rather than being duplicated in each individual page POM.</p>
 *
 * <h2>How to use this widget</h2>
 * <p>Any page POM that contains the header should expose it via a
 * {@code getHeader()} method that returns a new instance of this widget:</p>
 * <pre>
 *   public HeaderWidget getHeader() {
 *       return new HeaderWidget(context);
 *   }
 * </pre>
 * <p>Step definitions then access header functionality through the page:</p>
 * <pre>
 *   inventoryPage.getHeader().clickCartIcon();
 *   inventoryPage.getHeader().getCartItemCount();
 * </pre>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Burger menu interactions (open, close)</li>
 *   <li>Navigation menu item interactions</li>
 *   <li>Cart icon interactions and badge count retrieval</li>
 *   <li>Page title and logo visibility checks</li>
 * </ul>
 */
public class HeaderWidget extends BasePage {

    // =========================================================================
    // ELEMENT LOCATORS
    // =========================================================================

    /**
     * The burger menu button (hamburger icon) in the top left of the header.
     */
    @FindBy(id = "react-burger-menu-btn")
    private WebElement burgerMenuButton;

    /**
     * The close button for the burger menu sidebar.
     */
    @FindBy(id = "react-burger-cross-btn")
    private WebElement burgerMenuCloseButton;

    /**
     * The shopping cart icon link in the top right of the header.
     */
    @FindBy(css = ".shopping_cart_link")
    private WebElement cartIcon;

    /**
     * The cart item count badge displayed on the cart icon.
     * Only present in the DOM when at least one item is in the cart.
     */
    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    /**
     * The application logo displayed in the header.
     */
    @FindBy(css = ".app_logo")
    private WebElement appLogo;

    /**
     * The "All Items" link in the burger menu sidebar.
     */
    @FindBy(id = "inventory_sidebar_link")
    private WebElement allItemsLink;

    /**
     * The "About" link in the burger menu sidebar.
     */
    @FindBy(id = "about_sidebar_link")
    private WebElement aboutLink;

    /**
     * The "Logout" link in the burger menu sidebar.
     */
    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    /**
     * The "Reset App State" link in the burger menu sidebar.
     */
    @FindBy(id = "reset_sidebar_link")
    private WebElement resetAppStateLink;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs a new {@code HeaderWidget} and initialises all {@code @FindBy}
     * elements via the {@code BasePage} constructor.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public HeaderWidget(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // BURGER MENU INTERACTIONS
    // =========================================================================

    /**
     * Opens the burger menu sidebar by clicking the hamburger icon.
     *
     * <p>Waits for the menu button to be clickable before clicking.
     * After clicking, use {@link #isMenuOpen()} to verify the menu
     * has opened before interacting with menu items.</p>
     */
    public void openBurgerMenu() {
        click(burgerMenuButton);
    }

    /**
     * Closes the burger menu sidebar by clicking the X close button.
     *
     * <p>Only call this when the burger menu is already open. Calling
     * it when the menu is closed will result in a {@code TimeoutException}
     * as the close button is not visible when the menu is closed.</p>
     */
    public void closeBurgerMenu() {
        click(burgerMenuCloseButton);
    }

    /**
     * Clicks the "All Items" link in the burger menu sidebar.
     *
     * <p>Navigates back to the inventory page from any other page.
     * The burger menu must be open before calling this method.</p>
     */
    public void clickAllItems() {
        click(allItemsLink);
    }

    /**
     * Clicks the "Logout" link in the burger menu sidebar, ending the session.
     *
     * <p>After clicking, the browser is redirected to the login page.
     * The burger menu must be open before calling this method.</p>
     */
    public void clickLogout() {
        click(logoutLink);
    }

    /**
     * Clicks the "Reset App State" link in the burger menu sidebar.
     *
     * <p>Resets the SauceDemo application state — clears the cart and
     * resets any modified product states. Useful in test teardown or
     * in scenarios that require a clean application state.
     * The burger menu must be open before calling this method.</p>
     */
    public void clickResetAppState() {
        click(resetAppStateLink);
    }

    /**
     * Logs the user out by opening the burger menu and clicking logout.
     *
     * <p>Convenience method that combines {@link #openBurgerMenu()} and
     * {@link #clickLogout()} for scenarios where logout is a precondition
     * or teardown step rather than the action under test.</p>
     */
    public void logout() {
        openBurgerMenu();
        clickLogout();
    }

    // =========================================================================
    // CART INTERACTIONS
    // =========================================================================

    /**
     * Clicks the shopping cart icon, navigating to the cart page.
     */
    public void clickCartIcon() {
        click(cartIcon);
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the number of items currently displayed on the cart badge.
     *
     * <p>The cart badge is only present in the DOM when at least one item
     * is in the cart. Always call {@link #isCartBadgeDisplayed()} before
     * this method to avoid a {@code TimeoutException} when the cart is empty.</p>
     *
     * @return the number of items in the cart as an integer
     */
    public int getCartItemCount() {
        return Integer.parseInt(getText(cartBadge));
    }

    /**
     * Returns whether the cart badge is currently displayed on the cart icon.
     *
     * <p>The badge is only visible when the cart contains at least one item.
     * Use this to verify items have been successfully added to or removed
     * from the cart.</p>
     *
     * @return {@code true} if the cart badge is displayed, {@code false} otherwise
     */
    public boolean isCartBadgeDisplayed() {
        return isDisplayed(cartBadge);
    }

    /**
     * Returns whether the application logo is displayed in the header.
     *
     * <p>Used as a lightweight check to confirm the header has rendered
     * correctly on the current page.</p>
     *
     * @return {@code true} if the app logo is displayed, {@code false} otherwise
     */
    public boolean isAppLogoDisplayed() {
        return isDisplayed(appLogo);
    }

    /**
     * Returns whether the burger menu sidebar is currently open.
     *
     * <p>Checks for the presence of the close button, which is only
     * visible when the sidebar is open. Used to verify menu state
     * before interacting with menu items.</p>
     *
     * @return {@code true} if the burger menu is open, {@code false} otherwise
     */
    public boolean isMenuOpen() {
        return isDisplayed(burgerMenuCloseButton);
    }

    /**
     * Returns whether the burger menu button is displayed in the header.
     *
     * @return {@code true} if the burger menu button is displayed, {@code false} otherwise
     */
    public boolean isBurgerMenuButtonDisplayed() {
        return isDisplayed(burgerMenuButton);
    }
}