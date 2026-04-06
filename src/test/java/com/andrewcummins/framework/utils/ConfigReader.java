package com.andrewcummins.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides centralised, type-safe access to the framework's configuration properties.
 *
 * <p>This class loads {@code config.properties} from the classpath at instantiation
 * and exposes each configuration value through a dedicated, clearly named method.
 * All other classes in the framework retrieve configuration values exclusively
 * through this class — no direct property file access elsewhere.</p>
 *
 * <h2>Design decisions</h2>
 * <ul>
 *   <li><b>Classpath loading</b> — the file is loaded via ClassLoader rather than
 *       a hardcoded file path, ensuring consistent behaviour across all environments
 *       including CI/CD pipelines</li>
 *   <li><b>Fail fast</b> — if a required property is missing or the file cannot
 *       be found, a descriptive {@code RuntimeException} is thrown immediately
 *       rather than returning null and causing errors elsewhere</li>
 *   <li><b>Type conversion</b> — numeric properties (timeouts) are converted to
 *       {@code int} here so callers receive the correct type without parsing</li>
 *   <li><b>Single responsibility</b> — this class only reads configuration.
 *       It does not make decisions based on it.</li>
 * </ul>
 */
public class ConfigReader {

    /**
     * The name of the configuration file loaded from the classpath.
     */
    private static final String CONFIG_FILE = "config.properties";

    /**
     * The loaded properties object containing all configuration key-value pairs.
     */
    private final Properties properties;

    /**
     * Constructs a new {@code ConfigReader} and immediately loads {@code config.properties}.
     *
     * <p>The file is loaded from the classpath root, which corresponds to
     * {@code src/test/resources/config.properties} in the Maven project structure.</p>
     *
     * @throws RuntimeException if the configuration file cannot be found on the
     *                          classpath or cannot be read
     */
    public ConfigReader() {
        this.properties = new Properties();
        loadProperties();
    }

    /**
     * Returns the browser to use for WebDriver initialisation.
     *
     * <p>Maps directly to the {@code browser} property in {@code config.properties}.
     * The value is returned in lowercase to ensure consistent matching in
     * {@code DriverFactory} regardless of how it is written in the config file.</p>
     *
     * <p>Supported values: {@code chrome}, {@code firefox}, {@code edge}</p>
     *
     * @return the browser name in lowercase (e.g. "chrome", "firefox", "edge")
     */
    public String getBrowser() {
        return getRequiredProperty("browser").toLowerCase().trim();
    }

    /**
     * Returns whether the browser should run in headless mode.
     *
     * <p>Headless mode runs the browser without a visible UI window.
     * This is required for CI/CD pipelines where no display is available,
     * and is also faster for local runs where visual verification is not needed.</p>
     *
     * @return {@code true} if headless mode is enabled, {@code false} otherwise
     */
    public boolean isHeadless() {
        return Boolean.parseBoolean(getRequiredProperty("headless"));
    }

    /**
     * Returns the base URL for the UI application under test.
     *
     * <p>For this framework, this points to SauceDemo. The URL is used by
     * the {@code PageNavigator} to construct full page URLs dynamically.</p>
     *
     * @return the UI base URL string (e.g. "https://www.saucedemo.com")
     */
    public String getUiBaseUrl() {
        return getRequiredProperty("ui.base.url");
    }

    /**
     * Returns the base URL for the primary REST API under test.
     *
     * <p>For this framework, this points to ReqRes. Used as the base URI
     * in REST Assured API test configurations.</p>
     *
     * @return the API base URL string (e.g. "https://reqres.in/api")
     */
    public String getApiBaseUrl() {
        return getRequiredProperty("api.base.url");
    }

    /**
     * Returns the base URL for the secondary REST API under test.
     *
     * <p>For this framework, this points to JSONPlaceholder, used for
     * additional API test coverage.</p>
     *
     * @return the secondary API base URL string (e.g. "https://jsonplaceholder.typicode.com")
     */
    public String getApiSecondaryUrl() {
        return getRequiredProperty("api.secondary.url");
    }

    /**
     * Returns the implicit wait timeout duration in seconds.
     *
     * <p>This value is intentionally set to 0 in the default configuration
     * because implicit and explicit waits interact unpredictably when used
     * together. All waiting in this framework is handled by explicit waits
     * in the page object methods.</p>
     *
     * @return the implicit wait duration in seconds
     */
    public int getImplicitWait() {
        return getIntProperty("implicit.wait");
    }

    /**
     * Returns the explicit wait timeout duration in seconds.
     *
     * <p>This is the primary wait mechanism used throughout the framework.
     * Page object methods use this value when constructing
     * {@code WebDriverWait} instances for {@code ExpectedConditions}.</p>
     *
     * @return the explicit wait duration in seconds
     */
    public int getExplicitWait() {
        return getIntProperty("explicit.wait");
    }

    /**
     * Returns the page load timeout duration in seconds.
     *
     * <p>WebDriver will wait up to this duration for a full page load
     * to complete before throwing a {@code TimeoutException}.</p>
     *
     * @return the page load timeout duration in seconds
     */
    public int getPageLoadTimeout() {
        return getIntProperty("page.load.timeout");
    }

    /**
     * Returns the script execution timeout duration in seconds.
     *
     * <p>Used when executing JavaScript asynchronously via
     * {@code JavascriptExecutor}. WebDriver will wait up to this duration
     * for the script to complete.</p>
     *
     * @return the script timeout duration in seconds
     */
    public int getScriptTimeout() {
        return getIntProperty("script.timeout");
    }

    /**
     * Returns the classpath-relative path to the encrypted test data JSON file.
     *
     * <p>This path is passed to {@code JsonDataReader} during initialisation.
     * It is relative to {@code src/test/resources}, so a value of
     * {@code testdata/users.json} resolves to
     * {@code src/test/resources/testdata/users.json}.</p>
     *
     * @return the test data file path string (e.g. "testdata/users.json")
     */
    public String getTestDataPath() {
        return getRequiredProperty("test.data.path");
    }

    /**
     * Returns whether screenshots should be captured automatically on test failure.
     *
     * <p>When {@code true}, the framework's Cucumber hooks will capture a
     * screenshot at the point of failure and attach it to the Allure report,
     * making remote debugging significantly easier.</p>
     *
     * @return {@code true} if failure screenshots are enabled, {@code false} otherwise
     */
    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getRequiredProperty("screenshot.on.failure"));
    }

    /**
     * Returns the directory path where failure screenshots are saved.
     *
     * <p>This path is relative to the project root. The directory is created
     * automatically by the screenshot utility if it does not already exist.</p>
     *
     * @return the screenshot output directory path (e.g. "target/screenshots")
     */
    public String getScreenshotPath() {
        return getRequiredProperty("screenshot.path");
    }

    /**
     * Returns the Allure results output directory path.
     *
     * <p>This should match the {@code allure.results.directory} system property
     * configured in the Surefire plugin in {@code pom.xml}. Allure writes its
     * raw result files here during test execution, which are then used to
     * generate the final HTML report.</p>
     *
     * @return the Allure results directory path (e.g. "target/allure-results")
     */
    public String getAllureResultsDir() {
        return getRequiredProperty("allure.results.dir");
    }

    /**
     * Returns the configured log level for framework output.
     *
     * <p>Supported values: {@code ERROR}, {@code WARN}, {@code INFO}, {@code DEBUG}.
     * This value is used to configure the logging framework at startup.</p>
     *
     * @return the log level string (e.g. "INFO", "DEBUG")
     */
    public String getLogLevel() {
        return getRequiredProperty("log.level");
    }

    /**
     * Loads {@code config.properties} from the classpath into the {@code properties} object.
     *
     * <p>Uses the thread's context ClassLoader to locate the file, which ensures
     * correct resolution in both standard JVM execution and Maven Surefire's
     * isolated classloading environment.</p>
     *
     * @throws RuntimeException if the file is not found on the classpath or
     *                          cannot be read due to an I/O error
     */
    private void loadProperties() {
        try {
            InputStream inputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(CONFIG_FILE);

            if (inputStream == null) {
                throw new RuntimeException(
                        "[ConfigReader] Configuration file '" + CONFIG_FILE + "' was not " +
                                "found on the classpath. Ensure it exists at " +
                                "src/test/resources/config.properties and that Maven has " +
                                "included it in the test classpath."
                );
            }

            properties.load(inputStream);

        } catch (RuntimeException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(
                    "[ConfigReader] Failed to read configuration file '" + CONFIG_FILE +
                            "'. Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Retrieves a required property value by key, failing fast if it is absent.
     *
     * <p>A property is considered missing if it does not exist in the file
     * or if its value is empty after trimming whitespace. This prevents
     * silent failures caused by misconfigured or incomplete property files.</p>
     *
     * @param key the property key to look up
     * @return the trimmed property value string
     * @throws RuntimeException if the property is missing or empty
     */
    private String getRequiredProperty(String key) {
        String value = properties.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(
                    "[ConfigReader] Required property '" + key + "' is missing or empty " +
                            "in '" + CONFIG_FILE + "'. Please add this property and its value " +
                            "to the configuration file before running the framework."
            );
        }

        return value.trim();
    }

    /**
     * Retrieves a required property value and converts it to an {@code int}.
     *
     * <p>Fails fast with a descriptive error if the property is missing,
     * empty, or cannot be parsed as an integer — for example if someone
     * accidentally puts a non-numeric value in a timeout field.</p>
     *
     * @param key the property key to look up
     * @return the property value as an {@code int}
     * @throws RuntimeException if the property is missing, empty, or not a valid integer
     */
    private int getIntProperty(String key) {
        String value = getRequiredProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "[ConfigReader] Property '" + key + "' has value '" + value +
                            "' which cannot be parsed as an integer. " +
                            "Please ensure this property contains a numeric value in '" +
                            CONFIG_FILE + "'."
            );
        }
    }
}