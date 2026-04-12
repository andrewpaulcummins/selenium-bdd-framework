package com.andrewcummins.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Cucumber test runner for the Selenium BDD framework.
 *
 * <p>This is the single entry point for executing tests in this framework.
 * Tests are run by right-clicking this file in IntelliJ and selecting
 * "Run RunCukesTest" — no terminal commands are required.</p>
 *
 * <h2>How to run specific test suites</h2>
 * <p>Modify the {@code tags} attribute in {@code @CucumberOptions} to control
 * which tests are executed:</p>
 * <ul>
 *   <li>{@code "@regression"} — full regression suite</li>
 *   <li>{@code "@sanity"} — quick sanity/smoke suite</li>
 *   <li>{@code "@login"} — all login-related tests</li>
 *   <li>{@code "@TC001"} — a single specific test by ID</li>
 *   <li>{@code "@regression and @login"} — intersection of two tags</li>
 *   <li>{@code "@regression or @sanity"} — union of two tags</li>
 *   <li>{@code "not @WIP"} — everything except work-in-progress tests</li>
 * </ul>
 *
 * <h2>Glue paths</h2>
 * <p>The {@code glue} attribute tells Cucumber where to find step definitions
 * and hooks. All packages containing step definitions or hooks must be listed
 * here. If a step definition is not found, Cucumber will report the step
 * as undefined rather than throwing an error.</p>
 *
 * <h2>Parallel execution</h2>
 * <p>The {@link #scenarios()} method is annotated with
 * {@code @DataProvider(parallel = true)} to enable parallel scenario execution.
 * Set {@code parallel = false} to run scenarios sequentially, which is useful
 * when debugging failures.</p>
 *
 * <h2>Reports</h2>
 * <p>After running tests, generate the Allure report by running the Maven
 * goal {@code allure:serve} from the Maven panel in IntelliJ. This generates
 * the report and opens it in your default browser automatically.</p>
 */
@CucumberOptions(
        // Path to the feature files directory.
        // All .feature files under this path are discovered recursively.
        features = "src/test/resources/features",

        // Glue paths — packages where Cucumber looks for step definitions and hooks.
        // Add any new step definition packages here as the framework grows.
        glue = {
                "com.andrewcummins.framework.stepdefs",
                "com.andrewcummins.framework.hooks"
        },

        // Tags control which scenarios are executed.
        // Modify this value to run different suites:
        //   @regression  — full regression suite
        //   @sanity      — quick smoke suite
        //   @login       — login functional suite
        //   @inventory   — inventory functional suite
        //   @cart        — cart functional suite
        //   @checkout    — checkout functional suite
        //   @navigation  — navigation functional suite
        //   @TC001       — single test by ID
        //   not @WIP     — exclude work-in-progress tests
        tags = "@TC032",

        // Plugins configure reporting output.
        // - pretty        : readable console output with colours
        // - html          : built-in Cucumber HTML report
        // - json          : machine-readable output for CI integrations
        // - allure        : captures results for Allure report generation
        // - rerun         : writes failed scenario locations to a file
        //                   so they can be re-run without a full suite run
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "rerun:target/rerun/failed_scenarios.txt"
        },

        // Monochrome removes ANSI colour codes from console output.
        // Set to false for coloured output in IntelliJ's run console.
        monochrome = true,

        // Publish generates a shareable Cucumber report link after each run.
        // Set to false to disable the prompt in the console output.
        publish = false
)
public class RunCukesTest extends AbstractTestNGCucumberTests {

    /**
     * Provides scenario data to TestNG for execution.
     *
     * <p>Overriding this method with {@code @DataProvider(parallel = true)}
     * enables TestNG to run Cucumber scenarios in parallel. Each scenario
     * is treated as an independent data set, allowing multiple browser
     * instances to execute simultaneously.</p>
     *
     * <p>To disable parallel execution for debugging purposes, change
     * {@code parallel = true} to {@code parallel = false}.</p>
     *
     * @return a two-dimensional array of scenario objects for TestNG execution
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}