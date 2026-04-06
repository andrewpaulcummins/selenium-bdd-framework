package com.andrewcummins.framework.navigation;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.pages.InventoryPage;
import com.andrewcummins.framework.poms.pages.LoginPage;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles dynamic page routing based on page name strings from Cucumber feature files.
 *
 * <p>This class maps human-readable page name strings (e.g. {@code "login"},
 * {@code "inventory"}) to their corresponding URLs and Page POM instances.
 * It enables a single Gherkin step to navigate to any page in the application
 * without requiring a separate step definition per page.</p>
 *
 * <h2>How the dynamic step pattern works</h2>
 * <p>The feature file step:</p>
 * <pre>
 *   Given a "standard" user is on the "login" page
 * </pre>
 * <p>Results in the following call chain:</p>
 * <ol>
 *   <li>Cucumber captures {@code "login"} as a string parameter</li>
 *   <li>The step definition calls {@link #navigateTo(String)}</li>
 *   <li>{@code PageNavigator} looks up the URL for {@code "login"}</li>
 *   <li>The browser navigates to that URL</li>
 *   <li>The corresponding {@code LoginPage} POM instance is returned</li>
 *   <li>The step definition stores it in {@code ScenarioContext} for later steps</li>
 * </ol>
 *
 * <h2>Adding new pages</h2>
 * <p>To add a new page to the framework:</p>
 * <ol>
 *   <li>Create the Page POM class in {@code poms/pages/}</li>
 *   <li>Add an entry to {@link #buildUrlMap()} with the page name and URL path</li>
 *   <li>Add a corresponding case to {@link #getPageInstance(String)}</li>
 * </ol>
 * <p>No changes are needed in step definitions or feature files.</p>
 *
 * <h2>Fail fast</h2>
 * <p>If a page name is passed that has no registered URL or POM, a descriptive
 * exception is thrown immediately listing all valid page names. This surfaces
 * feature file typos at the navigation step rather than causing a cryptic
 * failure later in the scenario.</p>
 */
public class PageNavigator {

    /**
     * The {@code ScenarioContext} providing access to the driver,
     * configuration, and shared state.
     */
    private final ScenarioContext context;

    /**
     * Maps lowercase page name strings to their full URLs.
     * Built once at construction time and reused for all navigation calls.
     */
    private final Map<String, String> urlMap;

    /**
     * Constructs a new {@code PageNavigator} and initialises the URL map.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public PageNavigator(ScenarioContext context) {
        this.context = context;
        this.urlMap = buildUrlMap();
    }

    /**
     * Navigates the browser to the page corresponding to the given page name
     * and returns the matching Page POM instance.
     *
     * <p>The page name is normalised to lowercase and trimmed before lookup,
     * making feature file steps case-insensitive for page names.</p>
     *
     * <p>After navigation, the current page name is updated in
     * {@code ScenarioContext} so subsequent steps can reference it.</p>
     *
     * @param pageName the page name from the feature file step
     *                 (e.g. "login", "inventory", "cart")
     * @return the {@code BasePage} subclass instance for the navigated page
     * @throws RuntimeException if the page name is not registered in the URL map
     */
    public BasePage navigateTo(String pageName) {
        String normalisedName = normalise(pageName);
        String url = resolveUrl(normalisedName);

        context.getDriver().get(url);
        context.setCurrentPageName(normalisedName);

        return getPageInstance(normalisedName);
    }

    /**
     * Returns the Page POM instance for the current page without navigating.
     *
     * <p>Used when a step needs to interact with the current page but
     * navigation has already occurred in a previous step. Retrieves the
     * current page name from {@code ScenarioContext}.</p>
     *
     * @return the {@code BasePage} subclass instance for the current page
     * @throws RuntimeException if no current page name has been set in context,
     *                          or if the page name is not registered
     */
    public BasePage getCurrentPage() {
        String currentPageName = context.getCurrentPageName();

        if (currentPageName == null || currentPageName.trim().isEmpty()) {
            throw new RuntimeException(
                    "[PageNavigator] No current page has been set in ScenarioContext. " +
                            "Ensure a navigation step runs before any step that calls getCurrentPage()."
            );
        }

        return getPageInstance(currentPageName);
    }

    /**
     * Resolves the full URL for the given normalised page name.
     *
     * <p>Fails fast with a descriptive error listing all registered page names
     * if the requested page is not found. This makes feature file typos
     * immediately obvious rather than causing a WebDriver navigation failure.</p>
     *
     * @param normalisedName the lowercase, trimmed page name to resolve
     * @return the full URL string for the requested page
     * @throws RuntimeException if the page name is not found in the URL map
     */
    private String resolveUrl(String normalisedName) {
        String url = urlMap.get(normalisedName);

        if (url == null) {
            throw new RuntimeException(
                    "[PageNavigator] Page '" + normalisedName + "' is not registered. " +
                            "Registered pages are: " + urlMap.keySet() + ". " +
                            "Add the page to buildUrlMap() and getPageInstance() in PageNavigator, " +
                            "and create the corresponding Page POM class."
            );
        }

        return url;
    }

    /**
     * Returns the correct Page POM instance for the given normalised page name.
     *
     * <p>Each case instantiates the appropriate Page POM subclass with the
     * current {@code ScenarioContext}. When a new page is added to the framework,
     * a new case must be added here alongside the URL entry in
     * {@link #buildUrlMap()}.</p>
     *
     * @param normalisedName the lowercase, trimmed page name
     * @return the corresponding {@code BasePage} subclass instance
     * @throws RuntimeException if no Page POM is registered for the given name
     */
    private BasePage getPageInstance(String normalisedName) {
        switch (normalisedName) {
            case "login":
                return new LoginPage(context);
            case "inventory":
                return new InventoryPage(context);
            default:
                throw new RuntimeException(
                        "[PageNavigator] No Page POM registered for page '" +
                                normalisedName + "'. " +
                                "Add a case to getPageInstance() in PageNavigator and " +
                                "create the corresponding Page POM class in poms/pages/."
                );
        }
    }

    /**
     * Builds and returns the map of page name strings to full URLs.
     *
     * <p>The base URL is sourced from {@code config.properties} via
     * {@code ConfigReader}, ensuring the framework can be pointed at different
     * environments (dev, staging, production) without modifying this class.</p>
     *
     * <p>Page name keys must be lowercase. The {@link #normalise(String)} method
     * ensures all lookups use lowercase keys regardless of how the page name
     * appears in the feature file.</p>
     *
     * @return a populated {@code Map} of page name strings to URLs
     */
    private Map<String, String> buildUrlMap() {
        String baseUrl = context.getConfigReader().getUiBaseUrl();

        Map<String, String> map = new HashMap<>();

        // SauceDemo pages
        map.put("login",     baseUrl + "/");
        map.put("inventory", baseUrl + "/inventory.html");
        map.put("cart",      baseUrl + "/cart.html");
        map.put("checkout",  baseUrl + "/checkout-step-one.html");
        map.put("overview",  baseUrl + "/checkout-step-two.html");
        map.put("complete",  baseUrl + "/checkout-complete.html");

        return map;
    }

    /**
     * Normalises a page name string to lowercase and trims surrounding whitespace.
     *
     * <p>Applied to all page name inputs before URL resolution and POM instantiation,
     * making page name matching case-insensitive and whitespace-tolerant.</p>
     *
     * @param pageName the raw page name string from the feature file
     * @return the normalised lowercase, trimmed page name
     */
    private String normalise(String pageName) {
        return pageName.toLowerCase().trim();
    }
}