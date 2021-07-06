Feature: Calculator Validation for multiple Test Data
  @Calculator
  Scenario Outline: Validate Arithmrtic Operation for Calculator
    Given Getting inputs from User for Calculator APP
    When Do "<aith_ops>" Arithmetic Calculation for all the given inputs
    Then Resultant value should be displayed
    Examples:
      | aith_ops |
      | Addition |
      | Subtraction |
      | Multiplication |
      | Division |
      | mods |


