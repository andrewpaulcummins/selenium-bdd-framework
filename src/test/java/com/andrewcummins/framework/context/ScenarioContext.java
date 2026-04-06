package com.andrewcummins.framework.context;

import com.andrewcummins.framework.models.User;
import com.andrewcummins.framework.utils.ConfigReader;
import com.andrewcummins.framework.utils.JsonDataReader;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * Central shared state container for a single Cucumber scenario execution.
 *
 * <p>This class holds all state that needs to be shared between step definition
 * classes during a scenario — most importantly the {@code WebDriver} instance,
 * the current {@code User}, and any data passed between steps.</p>
 *
 * <h2>How it works with PicoContainer</h2>
 * <p>PicoContainer is a lightweight dependency injection container that Cucumber
 * uses to share objects between step definition classes. When Cucumber detects
 * that multiple step definition classes declare {@code ScenarioContext} as a
 * constructor parameter, PicoContainer:</p>
 * <ol>
 *   <li>Creates exactly one instance of {@code ScenarioContext} at the start
 *       of each scenario</li>
 *   <li>Injects that same instance into every step definition class that
 *       declares it as a constructor parameter</li>
 *   <li>Discards the instance at the end of the scenario</li>
 * </ol>
 * <p>This means every step in a scenario shares the same driver, user, and
 * data — but each scenario starts completely fresh with no leftover state
 * from previous scenarios.</p>
 *
 * <h2>Why not static fields?</h2>
 * <p>Static fields appear to work for simple sequential test runs but fail
 * immediately under parallel execution, where multiple scenarios run
 * simultaneously and would overwrite each other's static state. This
 * approach provides complete scenario isolation regardless of execution mode.</p>
 *
 * <h2>Lifecycle</h2>
 * <p>PicoContainer instantiates this class at the start of each scenario.
 * The {@code WebDriver} is initialised by {@code DriverFactory} in the
 * Cucumber {@code @Before} hook and set here via {@link #setDriver(WebDriver)}.
 * It is quit and nulled in the {@code @After} hook.</p>
 */
public class ScenarioContext {

    /**
     * The WebDriver instance for this scenario.
     *
     * <p>Initialised in the {@code @Before} hook via {@code DriverFactory}
     * and set here via {@link #setDriver(WebDriver)}. All page object
     * interactions use this driver instance.</p>
     */
    private WebDriver driver;

    /**
     * The current user for this scenario.
     *
     * <p>Set when a step like {@code Given a "standard" user is on the "login" page}
     * is executed. Loaded and decrypted from {@code users.json} via
     * {@code UserFactory}.</p>
     */
    private User currentUser;

    /**
     * The name of the current page the framework believes the user is on.
     *
     * <p>Updated by navigation steps. Used for contextual logging and
     * to verify correct page routing in assertions.</p>
     */
    private String currentPageName;

    /**
     * The {@code ConfigReader} instance for this scenario.
     *
     * <p>Provides access to all configuration properties from
     * {@code config.properties}. Instantiated once here and reused
     * across all step definitions via this context.</p>
     */
    private final ConfigReader configReader;

    /**
     * The {@code JsonDataReader} instance for this scenario.
     *
     * <p>Provides access to decrypted test data from {@code users.json}.
     * Instantiated once here using the test data path from {@code ConfigReader}.</p>
     */
    private final JsonDataReader jsonDataReader;

    /**
     * A general-purpose data store for passing arbitrary values between steps.
     *
     * <p>Allows steps to store and retrieve named values without needing
     * dedicated fields for every possible piece of scenario data. For example,
     * a step that creates a resource can store its ID here for a later step
     * to retrieve and use in an assertion.</p>
     *
     * <p>Keys should be descriptive strings. Values are stored as {@code Object}
     * and cast to the expected type on retrieval via {@link #getData(String, Class)}.</p>
     */
    private final Map<String, Object> scenarioData;

    /**
     * Constructs a new {@code ScenarioContext} for a single scenario execution.
     *
     * <p>PicoContainer calls this constructor automatically at the start of
     * each scenario. {@code ConfigReader} and {@code JsonDataReader} are
     * initialised here so they are available to all step definitions
     * immediately without any additional setup.</p>
     */
    public ScenarioContext() {
        this.configReader = new ConfigReader();
        this.jsonDataReader = new JsonDataReader(configReader.getTestDataPath());
        this.scenarioData = new HashMap<>();
    }

    /**
     * Returns the {@code WebDriver} instance for this scenario.
     *
     * <p>Fails fast if the driver has not been initialised, which would indicate
     * that the {@code @Before} hook did not run correctly or that a step is
     * attempting to use the driver before it has been set up.</p>
     *
     * @return the active {@code WebDriver} instance
     * @throws RuntimeException if the driver has not been initialised
     */
    public WebDriver getDriver() {
        if (driver == null) {
            throw new RuntimeException(
                    "[ScenarioContext] WebDriver has not been initialised. " +
                            "Ensure the @Before hook in Hooks.java is running correctly " +
                            "and that DriverFactory is successfully creating a driver instance."
            );
        }
        return driver;
    }

    /**
     * Sets the {@code WebDriver} instance for this scenario.
     *
     * <p>Called by the {@code @Before} hook after {@code DriverFactory}
     * has created and configured the driver.</p>
     *
     * @param driver the initialised {@code WebDriver} instance to set
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Returns the current user for this scenario.
     *
     * <p>Fails fast if no user has been set, which would indicate a feature
     * file step is attempting to act as a user before a user type has been
     * specified in a preceding Given step.</p>
     *
     * @return the current {@code User} object
     * @throws RuntimeException if no user has been set for this scenario
     */
    public User getCurrentUser() {
        if (currentUser == null) {
            throw new RuntimeException(
                    "[ScenarioContext] No user has been set for this scenario. " +
                            "Ensure a step like 'Given a \"standard\" user is on the \"login\" page' " +
                            "runs before any step that requires a user."
            );
        }
        return currentUser;
    }

    /**
     * Sets the current user for this scenario.
     *
     * <p>Called by the navigation step definition after {@code UserFactory}
     * has loaded and decrypted the appropriate user from test data.</p>
     *
     * @param currentUser the {@code User} object to set as the current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Returns the name of the current page.
     *
     * @return the current page name string (e.g. "login", "inventory")
     */
    public String getCurrentPageName() {
        return currentPageName;
    }

    /**
     * Sets the name of the current page.
     *
     * <p>Updated by navigation steps after a page transition occurs.</p>
     *
     * @param currentPageName the page name to set (e.g. "login", "inventory")
     */
    public void setCurrentPageName(String currentPageName) {
        this.currentPageName = currentPageName;
    }

    /**
     * Returns the {@code ConfigReader} instance for this scenario.
     *
     * <p>Step definitions and page objects can retrieve configuration values
     * through this method rather than instantiating their own {@code ConfigReader}.</p>
     *
     * @return the {@code ConfigReader} instance
     */
    public ConfigReader getConfigReader() {
        return configReader;
    }

    /**
     * Returns the {@code JsonDataReader} instance for this scenario.
     *
     * <p>Used by {@code UserFactory} to load and decrypt user data
     * from the test data JSON file.</p>
     *
     * @return the {@code JsonDataReader} instance
     */
    public JsonDataReader getJsonDataReader() {
        return jsonDataReader;
    }

    /**
     * Stores an arbitrary value in the scenario data map under the given key.
     *
     * <p>Use this to pass data between steps that cannot be communicated
     * through page objects or the current user — for example, an ID returned
     * by an API call that a later step needs to reference.</p>
     *
     * @param key   a descriptive string key for the value (e.g. "createdUserId")
     * @param value the value to store
     */
    public void setData(String key, Object value) {
        scenarioData.put(key, value);
    }

    /**
     * Retrieves a value from the scenario data map and casts it to the expected type.
     *
     * <p>Fails fast with a descriptive error if the key does not exist,
     * preventing confusing {@code NullPointerException}s in step definitions.</p>
     *
     * @param <T>  the expected return type
     * @param key  the key under which the value was stored
     * @param type the {@code Class} to cast the value to
     * @return the stored value cast to type {@code T}
     * @throws RuntimeException if no value exists for the given key
     * @throws ClassCastException if the stored value cannot be cast to the expected type
     */
    public <T> T getData(String key, Class<T> type) {
        Object value = scenarioData.get(key);

        if (value == null) {
            throw new RuntimeException(
                    "[ScenarioContext] No data found for key '" + key + "'. " +
                            "Ensure a preceding step has stored this value using setData(). " +
                            "Available keys: " + scenarioData.keySet()
            );
        }

        return type.cast(value);
    }

    /**
     * Returns whether a value exists in the scenario data map for the given key.
     *
     * <p>Use this to check for optional data before attempting retrieval,
     * avoiding the {@code RuntimeException} that {@link #getData(String, Class)}
     * throws for missing keys.</p>
     *
     * @param key the key to check
     * @return {@code true} if a value exists for the key, {@code false} otherwise
     */
    public boolean hasData(String key) {
        return scenarioData.containsKey(key);
    }
}