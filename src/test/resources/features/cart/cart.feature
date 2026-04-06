# =============================================================================
# FEATURE: Shopping Cart Functionality
# =============================================================================
# Tests covering the SauceDemo shopping cart page.
#
# TAGGING CONVENTION:
#   @TC[ID]       - unique test case identifier, one per scenario
#   @regression   - included in the full regression suite
#   @sanity       - included in the quick smoke/sanity suite
#   @WIP          - work in progress, excluded from regression runs
#   @cart         - functional tag grouping all cart-related tests
#
# RUNNING SPECIFIC TESTS:
#   All cart tests   : set tags = "@cart"   in RunCukesTest.java
#   Single test      : set tags = "@TC020"  in RunCukesTest.java
#   Sanity suite only: set tags = "@sanity" in RunCukesTest.java
# =============================================================================

@cart
Feature: Shopping cart functionality

  As a logged-in user
  I want to manage items in my shopping cart
  So that I can review my selection before placing an order

  Background:
    Given a "standard" user is on the "login" page
    When the user logs in

  # ---------------------------------------------------------------------------
  # CART CONTENT SCENARIOS
  # ---------------------------------------------------------------------------

  @TC020 @regression @sanity
  Scenario: Cart page displays items that have been added from inventory
    When the user adds "Sauce Labs Backpack" to the cart
    And the user clicks the cart icon
    Then the cart page should be displayed
    And the cart should contain "Sauce Labs Backpack"

  @TC021 @regression
  Scenario: Cart page is empty when no items have been added
    When the user clicks the cart icon
    Then the cart page should be displayed
    And the cart should be empty

  # ---------------------------------------------------------------------------
  # CART MANAGEMENT SCENARIOS
  # ---------------------------------------------------------------------------

  @TC022 @regression
  Scenario: User can remove an item from the cart page
    When the user adds "Sauce Labs Backpack" to the cart
    And the user clicks the cart icon
    And the user removes "Sauce Labs Backpack" from the cart page
    Then the cart should be empty

  # ---------------------------------------------------------------------------
  # NAVIGATION SCENARIOS
  # ---------------------------------------------------------------------------

  @TC023 @regression
  Scenario: Continue shopping returns the user to the inventory page
    When the user clicks the cart icon
    And the user clicks continue shopping
    Then the user should be on the "inventory" page

  @TC024 @regression
  Scenario: User can proceed to checkout from the cart
    When the user adds "Sauce Labs Backpack" to the cart
    And the user clicks the cart icon
    And the user proceeds to checkout
    Then the user should be on the "checkout" page