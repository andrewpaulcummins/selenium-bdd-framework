package com.andrewcummins.framework.factory;

import com.andrewcummins.framework.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class responsible for creating and configuring {@code WebDriver} instances.
 *
 * <p>This class implements the Factory design pattern for WebDriver creation.
 * All browser-specific configuration — options, arguments, headless mode,
 * and timeout settings — is centralised here. No other class in the framework
 * creates {@code WebDriver} instances directly.</p>
 *
 * <h2>Supported browsers</h2>
 * <ul>
 *   <li>{@code chrome} — Google Chrome via ChromeDriver</li>
 *   <li>{@code firefox} — Mozilla Firefox via GeckoDriver</li>
 *   <li>{@code edge} — Microsoft Edge via EdgeDriver</li>
 * </ul>
 *
 * <h2>WebDriverManager</h2>
 * <p>Browser driver binary management is handled automatically by WebDriverManager.
 * It detects the installed browser version and downloads the matching driver
 * binary without any manual configuration. This eliminates the most common
 * source of environment setup failures in Selenium frameworks.</p>
 *
 * <h2>Lifecycle</h2>
 * <p>A new {@code WebDriver} instance is created for each scenario via the
 * {@code @Before} hook in {@code Hooks.java}, and quit in the {@code @After}
 * hook. This ensures complete browser isolation between scenarios.</p>
 */
public class DriverFactory {

    /**
     * The {@code ConfigReader} instance used to retrieve browser configuration.
     */
    private final ConfigReader configReader;

    /**
     * Constructs a new {@code DriverFactory} with the given {@code ConfigReader}.
     *
     * @param configReader the configuration reader providing browser settings
     */
    public DriverFactory(ConfigReader configReader) {
        this.configReader = configReader;
    }

    /**
     * Creates and returns a fully configured {@code WebDriver} instance.
     *
     * <p>The browser type is determined by the {@code browser} property in
     * {@code config.properties}. WebDriverManager automatically handles driver
     * binary setup for the detected browser version. Timeouts are applied
     * immediately after creation as specified in the configuration.</p>
     *
     * <p>Fails fast with a descriptive error if the configured browser is not
     * supported, rather than returning null or throwing a cryptic exception.</p>
     *
     * @return a fully initialised and configured {@code WebDriver} instance
     * @throws RuntimeException if the configured browser is not supported
     */
    public WebDriver createDriver() {
        String browser = configReader.getBrowser();
        boolean headless = configReader.isHeadless();

        WebDriver driver;

        switch (browser) {
            case "chrome":
                driver = createChromeDriver(headless);
                break;
            case "firefox":
                driver = createFirefoxDriver(headless);
                break;
            case "edge":
                driver = createEdgeDriver(headless);
                break;
            default:
                throw new RuntimeException(
                        "[DriverFactory] Unsupported browser: '" + browser + "'. " +
                                "Supported values are: 'chrome', 'firefox', 'edge'. " +
                                "Check the 'browser' property in config.properties."
                );
        }

        configureTimeouts(driver);
        return driver;
    }

    /**
     * Creates a configured {@code ChromeDriver} instance.
     *
     * <p>WebDriverManager detects the installed Chrome version and sets up
     * the matching ChromeDriver binary automatically. Common Chrome arguments
     * are applied to improve stability in both local and CI/CD environments.</p>
     *
     * @param headless {@code true} to run Chrome without a visible UI window
     * @return a configured {@code ChromeDriver} instance
     */
    private ChromeDriver createChromeDriver(boolean headless) {
        // In CI environments, use the system ChromeDriver if available
        // WebDriverManager will fall back to downloading if not found
        WebDriverManager.chromedriver().clearDriverCache().setup();

        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);              // Disable save-password bubble
        prefs.put("profile.password_manager_enabled", false);        // Disable password manager
        prefs.put("profile.password_manager_leak_detection", false); // Disable breach warning

        options.setExperimentalOption("prefs", prefs);


        if (headless) {
            options.addArguments("--headless=new");
        }

        // Required for containerised Linux environments
        // (GitHub Actions, Docker, Jenkins agents)
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-setuid-sandbox");
        options.addArguments("--ignore-certificate-errors");

        options.setExperimentalOption("excludeSwitches",
                new String[]{"enable-automation"});
        options.addArguments("--disable-blink-features=AutomationControlled");

        return new ChromeDriver(options);
    }

    /**
     * Creates a configured {@code FirefoxDriver} instance.
     *
     * <p>WebDriverManager detects the installed Firefox version and sets up
     * the matching GeckoDriver binary automatically.</p>
     *
     * @param headless {@code true} to run Firefox without a visible UI window
     * @return a configured {@code FirefoxDriver} instance
     */
    private FirefoxDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        return new FirefoxDriver(options);
    }

    /**
     * Creates a configured {@code EdgeDriver} instance.
     *
     * <p>WebDriverManager detects the installed Edge version and sets up
     * the matching EdgeDriver binary automatically.</p>
     *
     * @param headless {@code true} to run Edge without a visible UI window
     * @return a configured {@code EdgeDriver} instance
     */
    private EdgeDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();

        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        return new EdgeDriver(options);
    }

    /**
     * Applies timeout configuration to the given {@code WebDriver} instance.
     *
     * <p>All timeout values are read from {@code config.properties} via
     * {@code ConfigReader}. Timeouts are applied using {@code Duration} objects
     * rather than the deprecated {@code TimeUnit} API introduced in older
     * versions of Selenium.</p>
     *
     * <p>Timeout types applied:</p>
     * <ul>
     *   <li><b>Implicit wait</b> — set to 0 to avoid conflicts with explicit waits</li>
     *   <li><b>Page load timeout</b> — maximum time to wait for a page to fully load</li>
     *   <li><b>Script timeout</b> — maximum time to wait for async JavaScript execution</li>
     * </ul>
     *
     * @param driver the {@code WebDriver} instance to configure timeouts on
     */
    private void configureTimeouts(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(configReader.getImplicitWait())
        );
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(configReader.getPageLoadTimeout())
        );
        driver.manage().timeouts().scriptTimeout(
                Duration.ofSeconds(configReader.getScriptTimeout())
        );
    }
}