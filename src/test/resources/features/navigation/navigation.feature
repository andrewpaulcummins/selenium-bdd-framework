# =============================================================================
# FEATURE: Header Navigation Functionality
# =============================================================================
# Tests covering the SauceDemo shared header component, which appears on
# all post-login pages and provides access to the burger menu, cart, and
# application logo.
#
# TAGGING CONVENTION:
#   @TC[ID]       - unique test case identifier, one per scenario
#   @regression   - included in the full regression suite
#   @sanity       - included in the quick smoke/sanity suite
#   @WIP          - work in progress, excluded from regression runs
#   @navigation   - functional tag grouping all navigation-related tests
#
# RUNNING SPECIFIC TESTS:
#   All navigation tests: set tags = "@navigation" in RunCukesTest.java
#   Single test         : set tags = "@TC031"      in RunCukesTest.java
#   Sanity suite only   : set tags = "@sanity"     in RunCukesTest.java
# =============================================================================

@navigation
Feature: Header navigation functionality

  As a logged-in user
  I want to navigate between pages using the header
  So that I can access all areas of the application

  Background:
    Given a "standard" user is on the "login" page
    When the user logs in

  # ---------------------------------------------------------------------------
  # LOGOUT SCENARIOS
  # ---------------------------------------------------------------------------

  @TC031 @regression @sanity
  Scenario: User can log out via the burger menu
    When the user logs out
    Then the user should be on the "login" page

  # ---------------------------------------------------------------------------
  # BURGER MENU SCENARIOS
  # ---------------------------------------------------------------------------

  @TC032 @regression
  Scenario: User can open and close the burger menu
    When the user opens the burger menu
    Then the burger menu should be open
    When the user closes the burger menu
    Then the burger menu should be closed

  @TC033 @regression
  Scenario: All Items link in the burger menu navigates to the inventory page
    When the user adds "Sauce Labs Backpack" to the cart
    And the user clicks the cart icon
    And the user opens the burger menu
    And the user clicks all items in the burger menu
    Then the user should be on the "inventory" page

  # ---------------------------------------------------------------------------
  # CART ICON SCENARIOS
  # ---------------------------------------------------------------------------

  @TC034 @regression
  Scenario: Cart icon in the header navigates to the cart page
    When the user clicks the cart icon
    Then the cart page should be displayed

  # ---------------------------------------------------------------------------
  # HEADER STATE SCENARIOS
  # ---------------------------------------------------------------------------

  @TC035 @regression
  Scenario: Application logo is displayed in the header on the inventory page
    Then the header logo should be displayed