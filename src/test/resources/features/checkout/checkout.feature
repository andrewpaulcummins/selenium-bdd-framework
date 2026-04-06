# =============================================================================
# FEATURE: Checkout Functionality
# =============================================================================
# Tests covering the SauceDemo multi-step checkout flow:
#   Step 1: Checkout information form (/checkout-step-one.html)
#   Step 2: Order overview          (/checkout-step-two.html)
#   Complete: Order confirmation    (/checkout-complete.html)
#
# TAGGING CONVENTION:
#   @TC[ID]       - unique test case identifier, one per scenario
#   @regression   - included in the full regression suite
#   @sanity       - included in the quick smoke/sanity suite
#   @WIP          - work in progress, excluded from regression runs
#   @checkout     - functional tag grouping all checkout-related tests
#
# RUNNING SPECIFIC TESTS:
#   All checkout tests: set tags = "@checkout" in RunCukesTest.java
#   Single test       : set tags = "@TC025"    in RunCukesTest.java
#   Sanity suite only : set tags = "@sanity"   in RunCukesTest.java
# =============================================================================

@checkout
Feature: Checkout functionality

  As a logged-in user
  I want to complete the checkout process
  So that I can place an order for the products in my cart

  Background:
    Given a "standard" user is on the "login" page
    When the user logs in
    And the user adds "Sauce Labs Backpack" to the cart
    And the user clicks the cart icon
    And the user proceeds to checkout

  # ---------------------------------------------------------------------------
  # HAPPY PATH SCENARIOS
  # ---------------------------------------------------------------------------

  @TC025 @regression @sanity
  Scenario: User can complete a full checkout and receive an order confirmation
    When the user enters first name "John", last name "Doe" and postal code "12345"
    And the user continues to the checkout overview
    And the user finishes the order
    Then the order confirmation page should be displayed
    And the order confirmation should contain "Thank you for your order!"

  @TC030 @regression
  Scenario: Checkout overview displays the correct order summary
    When the user enters first name "John", last name "Doe" and postal code "12345"
    And the user continues to the checkout overview
    Then the checkout overview page should be displayed
    And the order summary should contain "Sauce Labs Backpack"

  # ---------------------------------------------------------------------------
  # FIELD VALIDATION SCENARIOS
  # ---------------------------------------------------------------------------

  @TC026 @regression
  Scenario: Checkout form requires a first name
    When the user continues to the checkout overview
    Then the checkout page should display an error containing "First Name is required"

  @TC027 @regression
  Scenario: Checkout form requires a last name
    When the user enters first name "John"
    And the user continues to the checkout overview
    Then the checkout page should display an error containing "Last Name is required"

  @TC028 @regression
  Scenario: Checkout form requires a postal code
    When the user enters first name "John" and last name "Doe"
    And the user continues to the checkout overview
    Then the checkout page should display an error containing "Postal Code is required"

  # ---------------------------------------------------------------------------
  # NAVIGATION SCENARIOS
  # ---------------------------------------------------------------------------

  @TC029 @regression
  Scenario: User can cancel checkout and return to the cart
    When the user cancels checkout
    Then the cart page should be displayed