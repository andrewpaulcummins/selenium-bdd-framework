# Selenium BDD Framework

> An enterprise-grade test automation framework built with **Selenium 4**, **Cucumber 7**, **TestNG**, and **PicoContainer** : demonstrating production-ready BDD architecture, encrypted test data, dynamic page routing, and full CI/CD integration.

> **Live Test Report:** https://andrewpaulcummins.github.io/selenium-bdd-framework/

[![CI Pipeline](https://github.com/andrewpaulcummins/selenium-bdd-framework/actions/workflows/ci.yml/badge.svg)](https://github.com/andrewpaulcummins/selenium-bdd-framework/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Selenium](https://img.shields.io/badge/Selenium-4.18.1-green)
![Cucumber](https://img.shields.io/badge/Cucumber-7.15.0-brightgreen)
![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [Prerequisites](#3-prerequisites)
4. [Getting Started](#4-getting-started)
5. [Project Architecture](#5-project-architecture)
6. [Folder Structure](#6-folder-structure)
7. [Configuration](#7-configuration)
8. [Test Data & Encryption](#8-test-data--encryption)
9. [Writing Tests](#9-writing-tests)
10. [Running Tests](#10-running-tests)
11. [Tagging Strategy](#11-tagging-strategy)
12. [Reporting](#12-reporting)
13. [JavaDocs](#13-javadocs)
14. [CI/CD](#14-cicd)
15. [Debugging](#15-debugging)
16. [Contributing & Team Usage](#16-contributing--team-usage)
17. [Roadmap](#17-roadmap)

---

## 1. Project Overview

This framework was built to demonstrate senior SDET capability : not just the ability to write tests, but the ability to design and build a **maintainable, scalable, team-ready automation architecture** from scratch.

### What makes this framework different

Most automation frameworks are a collection of test scripts. This framework is a **layered architecture** where every design decision has a reason:

- **BDD with Cucumber** : test scenarios written in plain English (Gherkin) that any team member can read, understand, and contribute to : not just engineers
- **Dynamic step pattern** : a single Gherkin step handles every user type and every page, eliminating step explosion
- **No static state** : PicoContainer dependency injection ensures complete test isolation and parallel execution safety
- **Encrypted test data** : credentials stored in AES-encrypted JSON files; the decryption key never touches source control
- **Fail fast** : every layer throws descriptive, actionable errors the moment something is wrong, not halfway through a test run
- **Widget POMs** : reusable UI components extracted from page objects to prevent duplication across pages

### Applications under test

| Application | Type | URL |
|---|---|---|
| SauceDemo | UI / E2E | https://www.saucedemo.com |
| ReqRes | REST API | https://reqres.in/api |
| JSONPlaceholder | REST API | https://jsonplaceholder.typicode.com |

---

## 2. Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 (LTS) | Primary language |
| Selenium WebDriver | 4.18.1 | Browser automation |
| WebDriverManager | 5.7.0 | Automatic driver binary management |
| Cucumber | 7.15.0 | BDD framework : Gherkin feature files |
| TestNG | 7.9.0 | Test runner and execution engine |
| PicoContainer | 2.15 | Dependency injection between step classes |
| REST Assured | 5.4.0 | API test automation |
| Jackson | 2.16.1 | JSON parsing and test data deserialisation |
| Allure | 2.25.0 | Rich interactive test reporting |
| Gatling | 3.10.3 | Performance and load testing |
| Maven | 3.9.x | Build tool and dependency management |
| GitHub Actions | : | Cloud CI/CD pipeline |
| Jenkins | : | Enterprise CI/CD pipeline |

---

## 3. Prerequisites

The following must be installed on your machine before setting up the framework.

### Required

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17 (LTS) | [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=17) |
| Apache Maven | 3.9.x | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Google Chrome | Latest | [google.com/chrome](https://www.google.com/chrome) |
| Git | Latest | [git-scm.com](https://git-scm.com) |
| IntelliJ IDEA | Latest | [jetbrains.com/idea](https://www.jetbrains.com/idea) |

### Verifying your installation

Open a terminal and run the following commands. All should return version numbers without errors:

```bash
java -version      # Should show openjdk 17.x.x
mvn --version      # Should show Apache Maven 3.9.x with Java 17
git --version      # Should show git version x.x.x
```

### Environment variable

The framework uses AES encryption to protect test credentials. A secret key must be set as an environment variable on your machine:

```bash
# Windows (PowerShell : run as user level)
[System.Environment]::SetEnvironmentVariable("TEST_DATA_SECRET_KEY", "YOUR_KEY_HERE", "User")
# macOS / Linux
export TEST_DATA_SECRET_KEY="YOUR_KEY_HERE"
```

> **Important:** The key must be exactly **16 characters** long. Contact the framework maintainer to obtain the correct key for your team.

### Recommended IntelliJ Plugins

Install these via **File → Settings → Plugins → Marketplace**:

| Plugin | Purpose |
|---|---|
| Cucumber for Java | Gherkin syntax highlighting, step navigation |
| Gherkin | Feature file support |
| Maven Helper | Dependency tree visualisation |
| Allure Plugin | View Allure reports inside IntelliJ |
| SonarLint | Real-time code quality analysis |
| Rainbow Brackets | Improved code readability |
| EnvFile | Load `.env` files into run configurations |

---

## 4. Getting Started

### Clone the repository

```bash
git clone https://github.com/andrewpaulcummins/selenium-bdd-framework.git
cd selenium-bdd-framework
```

### Open in IntelliJ

1. Open IntelliJ IDEA
2. Select **File → Open** and navigate to the cloned project folder
3. Select the `pom.xml` file and click **Open as Project**
4. IntelliJ will detect Maven and prompt you to load the project : click **Load Maven Project**
5. Wait for Maven to download all dependencies (first time only : this may take a few minutes)

### Configure the run environment

1. Open **Run → Edit Configurations**
2. Select the **RunCukesTest** configuration (or create one by right-clicking `RunCukesTest.java` → **Run**)
3. In the **Environment variables** field add:
   ```
   TEST_DATA_SECRET_KEY=YourKey1234567!
   ```
4. Click **OK**

### Verify the setup

Right-click `RunCukesTest.java` and select **Run 'RunCukesTest'**. You should see Chrome launch, tests execute against SauceDemo, and a green result summary in the IntelliJ console.

---

## 5. Project Architecture

The framework is built on a strict layered architecture. Each layer has a single responsibility and only communicates with the layer directly below it.

```
┌─────────────────────────────────────────────────────┐
│                   FEATURE FILES                     │
│         Plain English Gherkin scenarios             │
│      Written by anyone : devs, QA, BAs, PMs        │
└─────────────────────┬───────────────────────────────┘
                      │ Cucumber maps steps
┌─────────────────────▼───────────────────────────────┐
│                STEP DEFINITIONS                     │
│    Glue layer between Gherkin and Java code         │
│    Orchestrates factories, navigator, POMs          │
│              Assertions live here                   │
└──────┬──────────────┬──────────────┬────────────────┘
       │              │              │
┌──────▼──────┐ ┌─────▼──────┐ ┌────▼───────────────┐
│ UserFactory │ │PageNavigator│ │   ScenarioContext   │
│             │ │             │ │  (PicoContainer DI) │
└──────┬──────┘ └─────┬──────┘ └────────────────────┘
       │              │
┌──────▼──────────────▼───────────────────────────────┐
│                   PAGE POMs                         │
│     One class per page : interactions only          │
│          No assertions, no navigation               │
└─────────────────────┬───────────────────────────────┘
                      │ Delegates to
┌─────────────────────▼───────────────────────────────┐
│                 WIDGET POMs                         │
│   Reusable UI components shared across pages        │
│        e.g. Header, Footer, Modal, Dropdown         │
└─────────────────────┬───────────────────────────────┘
                      │ Inherits from
┌─────────────────────▼───────────────────────────────┐
│                   BASE PAGE                         │
│   All WebDriver interactions and explicit waits     │
│   click(), type(), getText(), waitForVisibility()   │
└─────────────────────────────────────────────────────┘
```

### Design principles

**No static state** : PicoContainer creates one `ScenarioContext` per scenario and injects it into every step definition class. The WebDriver instance lives in `ScenarioContext`, never in a static field. This makes parallel execution safe.

**Fail fast** : every class throws a descriptive `RuntimeException` at the earliest possible point if something is wrong. Missing environment variables, unknown page names, unrecognised user types : all surface immediately with clear messages.

**Dynamic steps** : a single Gherkin step covers every user type and every page:
```gherkin
Given a "standard" user is on the "login" page
Given a "locked" user is on the "inventory" page
```

**Assertions at step definition level only** : page objects interact, step definitions assert. This separation means page objects are reusable across multiple step classes without coupling.

**JavaDocs on all public POM methods** : every public method on every Page POM and Widget POM is documented, making the framework self-explanatory for any team member.

---

## 6. Folder Structure

```
selenium-bdd-framework/
├── .github/
│   └── workflows/
│       └── ci.yml                    # GitHub Actions CI pipeline
├── src/
│   └── test/
│       ├── java/
│       │   └── com/andrewcummins/framework/
│       │       ├── context/
│       │       │   └── ScenarioContext.java       # Shared state via PicoContainer
│       │       ├── factory/
│       │       │   ├── DriverFactory.java         # WebDriver creation
│       │       │   └── UserFactory.java           # User object creation
│       │       ├── hooks/
│       │       │   └── Hooks.java                 # @Before/@After lifecycle
│       │       ├── models/
│       │       │   └── User.java                  # Test user model
│       │       ├── navigation/
│       │       │   └── PageNavigator.java         # Dynamic page routing
│       │       ├── poms/
│       │       │   ├── BasePage.java              # Base class for all POMs
│       │       │   ├── pages/
│       │       │   │   ├── LoginPage.java         # Login page interactions
│       │       │   │   └── InventoryPage.java     # Inventory page interactions
│       │       │   └── widgets/
│       │       │       └── HeaderWidget.java      # Shared header component
│       │       ├── runners/
│       │       │   └── RunCukesTest.java          # Cucumber test runner
│       │       ├── stepdefs/
│       │       │   └── LoginSteps.java            # Login step definitions
│       │       └── utils/
│       │           ├── ConfigReader.java          # config.properties access
│       │           ├── EncryptionUtil.java        # AES encrypt/decrypt
│       │           ├── EncryptionRunner.java      # One-time encryption utility
│       │           └── JsonDataReader.java        # Test data loading
│       └── resources/
│           ├── config.properties                  # Runtime configuration
│           ├── features/
│           │   └── login/
│           │       └── login.feature              # Login BDD scenarios
│           └── testdata/
│               └── users.json                     # Encrypted user credentials
├── .gitignore
├── Jenkinsfile                                    # Jenkins CI pipeline
└── pom.xml                                        # Maven dependencies & plugins
```

---

## 7. Configuration

All runtime configuration lives in `src/test/resources/config.properties`. No Java code needs to be modified to change browser, environment, or timeout settings.

### Key configuration options

```properties
# Browser : chrome | firefox | edge
browser=chrome

# Headless mode : false for local debugging, true for CI
headless=false

# Environment : points all URLs at the correct environment
environment=prod

# Application URLs
ui.base.url=https://www.saucedemo.com
api.base.url=https://reqres.in/api

# Timeouts (seconds)
implicit.wait=0       # Always 0 : explicit waits used throughout
explicit.wait=10      # Primary wait mechanism
page.load.timeout=30

# Test data
test.data.path=testdata/users.json

# Screenshots on failure
screenshot.on.failure=true
```

### Switching browsers

Change the `browser` property to run against a different browser : no code changes required:

```properties
browser=firefox
```

### Running in headed mode (visible browser)

Set `headless=false` in `config.properties` for local debugging with a visible browser window.

---

## 8. Test Data & Encryption

### Why encryption?

Test credentials must never be stored in plain text in source control. Anyone with access to the repository would be able to read them. This framework uses **AES-128 encryption** (Advanced Encryption Standard) to encrypt all sensitive values before storing them in `users.json`. The decryption key is stored only as an environment variable : never in any file.

### How it works

```
Plain text password
        ↓
EncryptionUtil.encrypt()  ←  TEST_DATA_SECRET_KEY (env var)
        ↓
Encrypted Base64 string → stored in users.json
        ↓
At runtime: EncryptionUtil.decrypt()  ←  TEST_DATA_SECRET_KEY
        ↓
Plain text password → used in test
```

### Test user types

| User Type | Username | Behaviour |
|---|---|---|
| `standard` | standard_user | Normal access : full functionality |
| `locked` | locked_out_user | Login blocked : error message shown |
| `problem` | problem_user | UI renders with visual bugs |
| `performance_glitch` | performance_glitch_user | Artificial login delay |

### Adding a new user type

1. Add the plain text entry to a local-only copy of `users.json`
2. Run `EncryptionRunner.java` (right-click → Run) to generate encrypted values
3. Add the encrypted values to `users.json` in the repository
4. Add a mapping entry in `PageNavigator.buildUrlMap()` if a new page is also needed

### Generating encrypted values

Right-click `EncryptionRunner.java` in IntelliJ and select **Run**. The console will output encrypted values for each plain text entry. Copy them into `users.json`.

> **Never commit plain text credentials.** Always encrypt first, then store only the encrypted output.

---

## 9. Writing Tests

### Creating a new feature file

1. Navigate to `src/test/resources/features/`
2. Create a new folder for your feature area (e.g. `checkout/`)
3. Create a new `.feature` file (e.g. `checkout.feature`)
4. Follow the tagging convention : see [Tagging Strategy](#11-tagging-strategy)

### Feature file structure

```gherkin
@checkout
Feature: Checkout functionality

  As a logged-in user
  I want to complete the checkout process
  So that I can purchase items from my cart

  @TC010 @sanity @regression
  Scenario: User can complete checkout successfully
    Given a "standard" user is on the "login" page
    When the user logs in
    Then the user should be on the "inventory" page
```

### The dynamic step pattern

A single step definition handles all user types and all pages:

```gherkin
Given a "standard" user is on the "login" page      # standard user, login page
Given a "locked" user is on the "login" page        # locked user, login page
Given a "standard" user is on the "inventory" page  # standard user, inventory page
```

The user type string maps to a user in `users.json`. The page name string maps to a URL in `PageNavigator`. Adding a new combination requires no new step definitions.

### Adding a new page

1. Create a new Page POM class in `src/test/java/.../poms/pages/`
2. Extend `BasePage` and declare `@FindBy` element locators
3. Add public interaction methods with JavaDoc
4. Register the page in `PageNavigator`:
    - Add URL entry to `buildUrlMap()`
    - Add POM instantiation to `getPageInstance()`

```java
// In buildUrlMap()
map.put("cart", baseUrl + "/cart.html");

// In getPageInstance()
case "cart":
    return new CartPage(context);
```

### Adding a new widget

1. Create a new Widget POM class in `src/test/java/.../poms/widgets/`
2. Extend `BasePage` and declare `@FindBy` locators
3. Add public methods with JavaDoc
4. Expose the widget from any page that contains it:

```java
public HeaderWidget getHeader() {
    return new HeaderWidget(context);
}
```

### Adding new step definitions

1. Create a new class in `src/test/java/.../stepdefs/`
2. Declare `ScenarioContext` as a constructor parameter : PicoContainer handles injection
3. Add the new package to the `glue` path in `RunCukesTest.java`

```java
public class CheckoutSteps {
    private final ScenarioContext context;
    private final PageNavigator pageNavigator;

    public CheckoutSteps(ScenarioContext context) {
        this.context = context;
        this.pageNavigator = new PageNavigator(context);
    }
}
```

---

## 10. Running Tests

### Running from IntelliJ (recommended)

1. Open `src/test/java/com/andrewcummins/framework/runners/RunCukesTest.java`
2. Right-click anywhere in the file
3. Select **Run 'RunCukesTest'**

This is the only way tests should be run : no terminal commands required.

### Changing which tests run

Modify the `tags` attribute in `RunCukesTest.java`:

```java
@CucumberOptions(
    tags = "@regression",        // Run full regression suite
    // tags = "@sanity",         // Run sanity suite only
    // tags = "@TC001",          // Run single test by ID
    // tags = "@login",          // Run all login tests
    // tags = "not @WIP",        // Exclude work in progress
)
```

### Running via Maven (for CI or command line)

```bash
# Run with default tags from RunCukesTest.java
mvn test

# Override tags at runtime
mvn test -Dcucumber.filter.tags="@sanity"

# Override browser
mvn test -Dbrowser=firefox

# Override headless mode
mvn test -Dheadless=true
```

---

## 11. Tagging Strategy

Every scenario has three types of tags applied at different levels:

### Tag types

| Tag Type | Examples | Purpose |
|---|---|---|
| **Feature tag** | `@login`, `@checkout`, `@cart` | Groups all scenarios for a functional area |
| **Suite tag** | `@regression`, `@sanity`, `@WIP` | Controls which CI pipeline runs the test |
| **Test ID** | `@TC001`, `@TC002` | Unique permanent identifier per scenario |

### Suite tag definitions

| Tag | Purpose | When to use |
|---|---|---|
| `@regression` | Full test suite | Nightly builds, pre-release |
| `@sanity` | Core happy path only | Every PR, quick smoke check |
| `@WIP` | Work in progress | Scenarios under development : excluded from CI |

### Tag combinations

```gherkin
@login @regression
Feature: Login functionality

  @TC001 @sanity
  Scenario: Standard user can log in successfully
```

This scenario has: `@login` (from feature), `@regression` (from feature), `@TC001` (scenario), `@sanity` (scenario).

It will run when tags are: `@regression`, `@sanity`, `@login`, `@TC001`, `@regression or @sanity`, `@login and @sanity`.

### Tag ID convention

Test IDs are sequential and never reused. If a scenario is deleted, its ID is retired : not reassigned to a new scenario. This ensures traceability between test management tools and the codebase.

---

## 12. Reporting

### Allure Report (recommended)

Allure produces a rich interactive HTML report with step-by-step execution details, screenshots on failure, and historical trend data.

**For interviewers (quick access):** open the latest hosted report here:
`https://andrewpaulcummins.github.io/selenium-bdd-framework/`

**View the hosted report on GitHub Pages (no local setup):**

`https://andrewpaulcummins.github.io/selenium-bdd-framework/`

Each successful CI run on `main` publishes the latest Allure HTML report to this URL.

**Local generation (optional for debugging):**

In IntelliJ open the Maven panel (**View → Tool Windows → Maven**), click **Execute Maven Goal** and run:

```
allure:serve
```

This generates the report and opens it in your default browser automatically.

**Generate report files only (without opening):**

```
allure:report
```

Report is saved to `target/allure-report/index.html`.

**What the Allure report shows:**
- Pass/fail status per scenario
- Each Gherkin step with pass/fail status
- Screenshots automatically attached on failure
- Test execution duration
- Historical trend across runs (when using CI)
- Breakdown by feature, tag, and suite

### Cucumber HTML Report

A simpler built-in HTML report is generated automatically after every test run:

```
target/cucumber-reports/cucumber.html
```

Open this file directly in any browser.

### Console output

The `pretty` plugin in `RunCukesTest.java` produces readable console output during test execution showing each step as it runs.

---

## 13. JavaDocs

All public methods on Page POMs and Widget POMs are documented with JavaDoc comments explaining what the method does, its parameters, return values, and any exceptions thrown.

### Generating JavaDocs

In the Maven panel run:

```
javadoc:javadoc
```

Output is saved to `target/site/apidocs/`.

### Viewing JavaDocs

Open `target/site/apidocs/index.html` in any browser. You will see:
- Package overview
- All public classes
- All public methods with full documentation
- Parameter and return type details

### JavaDoc convention

| Element | JavaDoc required? |
|---|---|
| Public methods on Page POMs | ✅ Yes |
| Public methods on Widget POMs | ✅ Yes |
| Private methods | ❌ No |
| Step definition methods | ❌ No (Gherkin is self-documenting) |
| Utility class methods | ✅ Yes (public only) |

---

## 14. CI/CD

### GitHub Actions

The pipeline is defined in `.github/workflows/ci.yml` and triggers automatically on every push to `main` or `develop`, on every pull request targeting `main`, and on a daily schedule targeting ~06:00 UK local time (06:00 UTC in GMT months, 05:00 UTC in BST months).

**Pipeline stages:**
1. Checkout repository
2. Set up Java 17 (Eclipse Temurin)
3. Cache Maven dependencies
4. Install Chrome and ChromeDriver (matched versions)
5. Compile framework
6. Run test suite (headless Chrome)
7. Upload Allure results as artifact
8. Upload Cucumber reports as artifact
9. Upload failure screenshots (on failure only)
10. Output pipeline summary

**Viewing results:**
1. Go to the repository on GitHub
2. Click the **Actions** tab
3. Click the latest pipeline run on `main`
4. Open the GitHub Pages Allure site:
   `https://andrewpaulcummins.github.io/selenium-bdd-framework/`

**One-time GitHub Pages setup (repository admin):**
1. Go to **Settings → Pages**
2. Under **Build and deployment**, set **Source** to **Deploy from a branch**
3. Set branch to **`gh-pages`** and folder to **`/ (root)`**

**Running manually with custom tags:**
1. Go to **Actions → CI Pipeline → Run workflow**
2. Enter a tag expression (e.g. `@sanity`)
3. Select a browser
4. Click **Run workflow**

**Adding the secret to GitHub:**
1. Go to **Settings → Secrets and variables → Actions**
2. Click **New repository secret**
3. Name: `TEST_DATA_SECRET_KEY`
4. Value: the 16-character encryption key

### Jenkins

The pipeline is defined in `Jenkinsfile` at the project root.

**Prerequisites in Jenkins:**
- JDK 17 configured as a tool named `JDK17`
- Maven 3.9.x configured as a tool named `Maven3`
- Allure configured as a tool named `Allure`
- Secret text credential named `TEST_DATA_SECRET_KEY`

**Setting up the pipeline:**
1. In Jenkins click **New Item**
2. Select **Pipeline**
3. Under **Pipeline** select **Pipeline script from SCM**
4. Set SCM to **Git** and enter the repository URL
5. Set **Script Path** to `Jenkinsfile`
6. Click **Save**

**Running with custom tags:**
Click **Build with Parameters** and enter a tag expression in the `CUCUMBER_TAGS` field.

---

## 15. Debugging

### Running a single test by ID

In `RunCukesTest.java` change the tags to the specific test ID:

```java
tags = "@TC001"
```

Right-click and run. Only that scenario executes.

### Running in headed mode (visible browser)

In `config.properties` set:

```properties
headless=false
```

The browser window will be visible during test execution, making it easy to see exactly what is happening.

### Using the IntelliJ debugger with Cucumber

1. Set a breakpoint in any step definition method by clicking in the left gutter
2. Right-click `RunCukesTest.java` and select **Debug 'RunCukesTest'**
3. Execution will pause at your breakpoint
4. Use **Step Over** (F8) to advance one line at a time
5. Inspect variables in the **Variables** panel

### Common errors and fixes

| Error | Cause | Fix |
|---|---|---|
| `TEST_DATA_SECRET_KEY is not set` | Environment variable missing | Add to IntelliJ run configuration |
| `config.properties not found` | File missing from resources | Ensure file exists at `src/test/resources/config.properties` |
| `Page 'x' is not registered` | Page not in PageNavigator | Add entry to `buildUrlMap()` and `getPageInstance()` |
| `User type 'x' not found` | User not in users.json | Add encrypted entry to users.json |
| `SessionNotCreatedException` | Browser/driver version mismatch | Run `mvn dependency:resolve` to refresh WebDriverManager |
| `StaleElementReferenceException` | DOM updated after element located | Use `waitForVisibility()` before interaction |

### Checking which tests are being picked up

Add `dryRun = true` to `@CucumberOptions` temporarily:

```java
@CucumberOptions(
    dryRun = true,
    tags = "@regression"
)
```

This prints all matched scenarios without executing them : useful for verifying tag expressions.

---

## 16. Contributing & Team Usage

### For non-technical team members (BAs, PMs, manual testers)

You can write and contribute test scenarios without any Java knowledge. All you need to edit is the `.feature` files in `src/test/resources/features/`.

**Writing a new scenario:**

1. Open the appropriate `.feature` file for the area you're testing
2. Add a new `Scenario:` block following the existing pattern
3. Use the existing steps : they are listed in the step definition files
4. Apply the correct tags : see [Tagging Strategy](#11-tagging-strategy)
5. Commit and push : the CI pipeline will pick it up automatically

**Available steps (current):**

```gherkin
Given a "{userType}" user is on the "{pageName}" page
When the user logs in
When the user enters their username
When the user enters their password
When the user clicks the login button
Then the user should be on the "{pageName}" page
Then an error message should be displayed
Then the error message should contain "{text}"
Then the inventory page should be displayed
Then no error message should be displayed
```

### For developers

**Branch convention:**
- `main` : stable, deployable code only
- `develop` : integration branch for features
- `feature/TC-XXX-description` : individual feature branches

**Adding a new page to the framework:**
1. Create the Page POM in `poms/pages/`
2. Register in `PageNavigator`
3. Write step definitions in `stepdefs/`
4. Write feature file scenarios in `features/`
5. All public POM methods must have JavaDoc

**Pull request checklist:**
- [ ] All existing tests still pass locally
- [ ] New scenarios have correct tag format (feature tag + suite tag + TC ID)
- [ ] New public POM methods have JavaDoc
- [ ] No static variables introduced
- [ ] No hardcoded credentials
- [ ] `config.properties` updated if new properties added

---

## 17. Roadmap

### In progress

- [ ] API testing module with REST Assured against ReqRes
- [ ] Performance testing module with Gatling load simulations
- [ ] Additional page coverage : Cart, Checkout, Order Complete

### Planned

- [ ] **Playwright for Java framework** : parallel project demonstrating the same architecture with Playwright instead of Selenium
- [ ] Database validation utilities
- [ ] Email notification on pipeline failure
- [ ] Test retry mechanism for flaky tests
- [ ] Cross-browser matrix (Chrome + Firefox + Edge) in CI

### Completed

- [x] Core BDD framework with Selenium + Cucumber + TestNG
- [x] PicoContainer dependency injection : zero static state
- [x] AES-encrypted test data
- [x] Dynamic user and page routing from feature file strings
- [x] Page Object Model with Widget POM pattern
- [x] Allure reporting with failure screenshots
- [x] GitHub Actions CI pipeline
- [x] Jenkinsfile CI pipeline
- [x] Full JavaDoc coverage on public POM methods

---

## Author

**Andrew Cummins** : SDET / Automation Engineer

[![GitHub](https://img.shields.io/badge/GitHub-andrewpaulcummins-black?logo=github)](https://github.com/andrewpaulcummins)

---

> *This framework was designed and built from scratch as a portfolio demonstration of enterprise-grade test automation architecture. Every design decision : from PicoContainer DI to AES encryption to the dynamic step pattern : reflects real-world production framework experience.*