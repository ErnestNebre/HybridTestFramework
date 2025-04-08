# Hybrid Test Automation Framework

This project implements a hybrid test automation framework for web applications using Playwright with Java, supporting multiple web applications including Todo List website and Sauce Demo website.

## Technology Choices

- **Java + Maven**: Chosen over Node.js due to:
  - Better familiarity with Maven project structure
  - Strong typing capabilities
  - Superior TestNG integration
  - Simplified dependency management
  
- **TestNG Data Providers**: Used instead of JSON-based data sources for:
  - Better organization of test data
  - Native Java integration without parsing
  - Built-in parameterization capabilities
  - Familiar syntax and structure
  
- **Playwright**: Selected for robust cross-browser support and modern web application testing capabilities

- **ExtentReports**: Comprehensive reporting solution

## Setup Instructions

### Prerequisites
- Java JDK 11 or higher
- Maven 3.6+
- IntelliJ IDEA (recommended)

### Installation
1. Clone the repository:
   ```bash
   git clone [repository-url]

### Install dependencies:  
   
    mvn clean install

### Configure browsers for Playwright:

    mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"

## Running Tests

### Run all tests
    mvn clean test

### Run specific test class
    mvn test -Dtest=Scenario1_CreateTodo

### Run tests with specific tag
    mvn test -Dgroups=smoke

## Framework Architecture

### Hybrid Framework Features
- Multi-application support: Tests multiple web applications using the same core components
- Data-driven: Uses TestNG DataProviders for parameterized testing
- Page Object Model: Separates UI elements from test logic
- Comprehensive reporting: Includes screenshots, logs, and detailed test steps
- Reusable components: Common utilities for both applications
- Cross-browser compatibility: Supports Chrome, Firefox, and WebKit

## Package Organization

    src/
    ├── test/
    │   ├── scenarios/         # Test cases organized by application
    │   │   ├── todo/          # Todo application test scenarios
    │   │   └── saucedemo/     # Sauce Demo test scenarios
    │   ├── pageobjects/       # Page Object Models
    │   │   ├── todo/          # Todo application page objects
    │   │   └── saucedemo/     # Sauce Demo page objects
    │   ├── testdata/          # Test data providers
    │   └── utility/           # Common utilities and base classes
    └── test-output/       # Contains all screenshots after test runs 
    

# Framework Components
## Page Object Pattern
- Encapsulates UI elements and their interactions
- Element locators defined as constants or methods
- Provides action methods for test scenarios
- Example: TodoItemPageObjects.java, LoginPageObjects.java
## Data-Driven Testing
- Uses TestNG DataProviders instead of JSON for test data management
- Chosen for better Java integration and familiarity
- Example: ToDoTestData.java, UserCredentialsData.java
## Base Test Class
- Commons.java provides core functionality:
  - Browser initialization and cleanup
  - Screenshot capture
  - Logging methods
  - Wait utilities
  - JavaScript execution helpers
## Reporting
  - ExtentReports for HTML reports
  - Screenshots captured at key verification points
  - Detailed logs for each test step
  - Status tracking (pass/fail/skip)
  ## Key Test Scenarios
  ### Todo List CRUD Operations
  - Create new todo items with dynamic timestamps
  - Verify persistence after page refresh
  - Delete todo items with validation
  - Handle multiple test data sets
  ### Sauce Demo E-commerce
  - User authentication with different user types
  - Product catalog browsing and filtering
  - Handle multiple test data sets

  ## Assumptions
  1. Test applications are accessible at their respective URLs
  2. Applications have stable UIs with consistent element locators
  3. Test environment has necessary network access
  4. Tests are run sequentially rather than in parallel
  5. Report directory structure is maintained between test runs
  ## Test Reports
  Reports are available in the test-output directory, including:
  - HTML reports with test execution details
  - Screenshots for verification steps
  - Failure evidence for debugging
