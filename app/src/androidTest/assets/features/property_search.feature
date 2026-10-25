Feature: Property search
  As a home buyer
  I want to search listings by city, keyword, price and bedrooms
  So that I can find a home that fits my needs

  Background:
    Given the listing service is available
    And the app is open on the search screen

  @results
  Scenario: Search by city shows the results list
    When I enter "Reston" in the city field
    And I tap Search
    Then I see the results list
    And I see "5 properties found" as the result count
    And I see the listing "1820 Wiehle Avenue"
    And the filters are collapsed into the summary bar

  @bedrooms
  Scenario Outline: Filter by minimum bedrooms
    When I enter "Reston" in the city field
    And I enter "<bedrooms>" in the min bedrooms field
    And I tap Search
    Then I see "<count>" as the result count

    Examples:
      | bedrooms | count               |
      | 3        | 4 properties found  |
      | 4        | 1 property found    |

  @no-results
  Scenario: Search with no matches shows the no results message
    When I enter "Nowhere" in the city field
    And I tap Search
    Then I see the no results message

  @validation
  Scenario: Minimum price above maximum price is rejected
    When I enter "600000" in the min price field
    And I enter "400000" in the max price field
    And I tap Search
    Then I see the price error "Minimum price cannot be greater than maximum price."
    And the search filters are open

  @edit-filters
  Scenario: Reopen the filters from the summary bar
    Given I have searched for "Reston"
    When I tap the filter summary bar
    Then the search filters are open
    And the city field shows "Reston"

  @clear
  Scenario: Clear resets the search
    Given I have searched for "Reston"
    When I tap the filter summary bar
    And I tap Clear
    Then I see the start screen
    And the city field is empty

  @network-error
  Scenario: Network error, then retry succeeds
    Given the listing service is unavailable
    When I enter "Reston" in the city field
    And I tap Search
    Then I see the network error message
    When the listing service is available
    And I tap Try again
    Then I see the results list
