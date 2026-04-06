package com.andrewcummins.framework.hooks;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.factory.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber lifecycle hooks for the framework.
 *
 * <p>This class defines {@code @Before} and {@code @After} hooks that Cucumber
 * executes automatically before and after every scenario. It is responsible for:</p>
 * <ul>
 *   <li>Creating and injecting a configured {@code WebDriver} instance into
 *       {@code ScenarioContext} before each scenario</li>
 *   <li>Capturing a screenshot and attaching it to the Allure/Cucumber report
 *       when a scenario fails</li>
 *   <li>Quitting the {@code WebDriver} and releasing browser resources after
 *       every scenario, regardless of pass or fail</li>
 * </ul>
 *
 * <h2>PicoContainer dependency injection</h2>
 * <p>{@code ScenarioContext} is injected into this class by PicoContainer via
 * the constructor. The same {@code ScenarioContext} instance is shared with
 * all step definition classes in the same scenario, enabling the driver
 * initialised here to be accessed in every step.</p>
 *
 * <h2>Hook ordering</h2>
 * <p>When multiple classes define {@code @Before} or {@code @After} hooks,
 * Cucumber executes them in an order determined by the {@code order} attribute.
 * Lower order values run first for {@code @Before} hooks, and last for
 * {@code @After} hooks. The default order is 10000 if not specified.</p>
 */
public class Hooks {

    /**
     * The shared {@code ScenarioContext} injected by PicoContainer.
     * The same instance is shared across all step definition classes
     * for the duration of the current scenario.
     */
    private final ScenarioContext context;

    /**
     * Constructs a new {@code Hooks} instance with the injected {@code ScenarioContext}.
     *
     * <p>PicoContainer calls this constructor automatically at the start of
     * each scenario and injects the shared {@code ScenarioContext} instance.
     * The same instance is also injected into all step definition classes
     * that declare it as a constructor parameter.</p>
     *
     * @param context the {@code ScenarioContext} for the current scenario,
     *                injected by PicoContainer
     */
    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    /**
     * Sets up the WebDriver before each scenario.
     *
     * <p>Creates a new {@code WebDriver} instance via {@code DriverFactory}
     * using the browser and configuration settings from {@code config.properties},
     * then injects it into {@code ScenarioContext} so it is available to all
     * step definitions and page objects throughout the scenario.</p>
     *
     * <p>The {@code @Before} hook runs after PicoContainer has created and
     * injected all shared objects, so {@code ScenarioContext} is fully
     * initialised and ready to receive the driver.</p>
     *
     * @param scenario the current Cucumber {@code Scenario} object, provided
     *                 automatically by Cucumber — used for logging the scenario name
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) {
        System.out.println("[Hooks] Starting scenario: " + scenario.getName());
        System.out.println("[Hooks] Tags: " + scenario.getSourceTagNames());

        DriverFactory driverFactory = new DriverFactory(context.getConfigReader());
        WebDriver driver = driverFactory.createDriver();
        context.setDriver(driver);

        System.out.println("[Hooks] WebDriver initialised: " +
                context.getConfigReader().getBrowser().toUpperCase());
    }

    /**
     * Tears down the WebDriver after each scenario.
     *
     * <p>This method runs after every scenario regardless of whether it
     * passed, failed, or was skipped. It:</p>
     * <ol>
     *   <li>Captures a screenshot if the scenario failed and attaches it
     *       to the Cucumber/Allure report for debugging purposes</li>
     *   <li>Quits the {@code WebDriver} instance, closing all browser
     *       windows and releasing all associated resources</li>
     * </ol>
     *
     * <p>Driver teardown is performed in a null-safe way — if the driver
     * was never successfully initialised (e.g. due to a {@code @Before}
     * hook failure), the quit call is safely skipped to prevent a
     * secondary {@code NullPointerException} masking the original error.</p>
     *
     * @param scenario the current Cucumber {@code Scenario} object, used
     *                 to check pass/fail status and attach screenshots
     */
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        System.out.println("[Hooks] Scenario '" + scenario.getName() +
                "' finished with status: " + scenario.getStatus());

        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }

        quitDriver();
    }

    /**
     * Captures a screenshot after each individual step that fails.
     *
     * <p>This hook fires after every step. When a step fails, it captures
     * a screenshot at the exact point of failure and embeds it directly
     * into the Cucumber/Allure report. This provides more granular failure
     * context than the scenario-level screenshot captured in {@link #tearDown(Scenario)}.</p>
     *
     * <p>Note: {@code @AfterStep} receives the scenario status after the step
     * completes. Only failed steps trigger the screenshot capture.</p>
     *
     * @param scenario the current Cucumber {@code Scenario} object
     */
    @AfterStep
    public void captureStepScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }
    }

    /**
     * Captures a screenshot of the current browser state and attaches it
     * to the Cucumber scenario report.
     *
     * <p>Screenshots are captured as PNG byte arrays using Selenium's
     * {@code TakesScreenshot} interface and attached to the scenario via
     * {@code scenario.attach()}. This embeds the screenshot directly into
     * the Cucumber HTML report and Allure report, making it immediately
     * visible without navigating to a separate file.</p>
     *
     * <p>If screenshot capture fails for any reason (e.g. the browser
     * has already crashed), the error is logged but not rethrown — the
     * original scenario failure is more important than a secondary
     * screenshot failure.</p>
     *
     * @param scenario the Cucumber {@code Scenario} to attach the screenshot to
     */
    private void captureScreenshot(Scenario scenario) {
        try {
            WebDriver driver = context.getDriver();
            byte[] screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot on failure");
            System.out.println("[Hooks] Screenshot captured and attached to report.");
        } catch (Exception e) {
            System.err.println("[Hooks] Failed to capture screenshot: " + e.getMessage());
        }
    }

    /**
     * Quits the {@code WebDriver} instance in a null-safe manner.
     *
     * <p>Checks whether a driver instance exists in {@code ScenarioContext}
     * before attempting to quit. This prevents a {@code NullPointerException}
     * in scenarios where the driver was never successfully created — for
     * example if {@code DriverFactory} threw an exception during {@code @Before}.</p>
     *
     * <p>After quitting, any attempt to interact with the driver will throw
     * a {@code NoSuchSessionException}. The driver reference remains in
     * {@code ScenarioContext} but PicoContainer discards the entire context
     * object at the end of the scenario.</p>
     */
    private void quitDriver() {
        try {
            WebDriver driver = context.getDriver();
            if (driver != null) {
                driver.quit();
                System.out.println("[Hooks] WebDriver quit successfully.");
            }
        } catch (Exception e) {
            System.err.println("[Hooks] Failed to quit WebDriver: " + e.getMessage());
        }
    }
}