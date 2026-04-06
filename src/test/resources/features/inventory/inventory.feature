# =============================================================================
# FEATURE: Inventory Page Functionality
# =============================================================================
# Tests covering the SauceDemo inventory (product listing) page.
#
# TAGGING CONVENTION:
#   @TC[ID]       - unique test case identifier, one per scenario
#   @regression   - included in the full regression suite
#   @sanity       - included in the quick smoke/sanity suite
#   @WIP          - work in progress, excluded from regression runs
#   @inventory    - functional tag grouping all inventory-related tests
#
# RUNNING SPECIFIC TESTS:
#   All inventory tests : set tags = "@inventory" in RunCukesTest.java
#   Single test         : set tags = "@TC011"     in RunCukesTest.java
#   Sanity suite only   : set tags = "@sanity"    in RunCukesTest.java
# =============================================================================

@inventory
Feature: Inventory page functionality

  As a logged-in user
  I want to browse and interact with the product inventory
  So that I can find products and manage my shopping cart

  Background:
    Given a "standard" user is on the "login" page
    When the user logs in

  # ---------------------------------------------------------------------------
  # PRODUCT LISTING SCENARIOS
  # ---------------------------------------------------------------------------

  @TC011 @regression @sanity
  Scenario: Inventory page displays all six products
    Then the inventory page displays 6 products

  @TC012 @regression
  Scenario: Products are sorted alphabetically A to Z by default
    Then the products should be sorted by name in ascending order

  @TC013 @regression
  Scenario: User can sort products by name Z to A
    When the user sorts products by "Name (Z to A)"
    Then the products should be sorted by name in descending order

  @TC014 @regression
  Scenario: User can sort products by price low to high
    When the user sorts products by "Price (low to high)"
    Then the products should be sorted by price in ascending order

  @TC015 @regression
  Scenario: User can sort products by price high to low
    When the user sorts products by "Price (high to low)"
    Then the products should be sorted by price in descending order

  # ---------------------------------------------------------------------------
  # CART INTERACTION SCENARIOS
  # ---------------------------------------------------------------------------

  @TC016 @regression @sanity
  Scenario: Adding a product to the cart updates the cart badge
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge should show 1

  @TC017 @regression
  Scenario: Adding multiple products shows the correct cart badge count
    When the user adds "Sauce Labs Backpack" to the cart
    And the user adds "Sauce Labs Bike Light" to the cart
    Then the cart badge should show 2

  @TC018 @regression
  Scenario: Removing a product from the inventory page clears the cart badge
    When the user adds "Sauce Labs Backpack" to the cart
    And the user removes "Sauce Labs Backpack" from the cart
    Then the cart badge should not be displayed

  # ---------------------------------------------------------------------------
  # PRODUCT DETAIL SCENARIOS
  # ---------------------------------------------------------------------------

  @TC019 @regression
  Scenario: User can navigate to a product detail page
    When the user opens the product details for "Sauce Labs Backpack"
    Then the product detail page should be displayed for "Sauce Labs Backpack"