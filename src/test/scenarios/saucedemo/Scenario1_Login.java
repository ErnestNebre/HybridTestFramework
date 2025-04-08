package test.scenarios.saucedemo;

import org.testng.Assert;
import org.testng.annotations.*;
import test.pageobjects.saucedemo.LoginPageObjects;
import test.testdata.SauceDemoTestData;
import test.utility.Commons;
import java.io.IOException;

public class Scenario1_Login extends Commons {

    private LoginPageObjects loginPage;

    @BeforeClass
    @Override
    public void setUp() {
        super.setUp();  // Initialize playwright, browser and page
        loginPage = new LoginPageObjects(page);
    }

    // This positive test case is for the login functionality of the SauceDemo application
    @Test(dataProvider = "loginCredentials", dataProviderClass = SauceDemoTestData.class)
    public void testLogin(String username, String password, String expectedErrorMessage) throws IOException {
        // Initialize test case in reports
        startTestCase("Positive Login Test with user: " + username);
        String screenshotPath = null;

        try {
            // Navigate to login page
            logInfo("Navigating to login page");
            loginPage.navigateToLoginPage();

            // Wait for page to be fully loaded with login form
            logInfo("Waiting for login form elements");
            waitForElementVisible(loginPage.getUsernameInputSelector());
            waitForElementVisible(loginPage.getPasswordInputSelector());
            waitForElementEnabled(loginPage.getLoginButtonSelector());

            // Perform login Test Steps
            //Step1
            logInfo("Step 1: Entering username: " + username);
            loginPage.enterUsername(username);

            //Step2
            logInfo("Step 2: Entering password: " + password);
            loginPage.enterPassword(password);

            //Step3
            logInfo("Step 3: Clicking login button");
            loginPage.clickLoginButton();

            //Step4
            // Check for dialog and handle it if present (wait up to 3 seconds)
            if (checkAndHandleDialog(3000)) {
                logInfo("Dialog was present and handled");
            } else {
                logInfo("No dialog detected, continuing with test");
            }

            // Validate the result with appropriate waits
            logInfo("Waiting for redirect to inventory page");
            try {
                waitForUrlContains("inventory.html");
                boolean isSuccess = loginPage.isLoginSuccessful();
                Assert.assertTrue(isSuccess, "Login should be successful for user: " + username);
                logPass("Login successful for user: " + username);
            } catch (Exception e) {
                logFail("Login failed - inventory page not loaded");
                Assert.fail("Expected successful login to inventory page for user: " + username);
            }

        } catch (Exception e) {
            logFail("Login failed but was expected to succeed");
            Assert.fail("Login should succeed but failed: " + e.getMessage());
        }
    }

    // Negative test case for login functionality
    @Test(dataProvider = "loginNegativeCredsentials", dataProviderClass = SauceDemoTestData.class)
    public void testLoginNegative(String username, String password, String expectedErrorMessage) throws IOException {
        // Initialize test case in reports
        startTestCase("Negative Login Test with user: " + username);
        String screenshotPath = null;

        try {
            // Navigate to login page
            logInfo("Navigating to login page");
            loginPage.navigateToLoginPage();

            // Wait for page to be fully loaded with login form
            logInfo("Waiting for login form elements");
            waitForElementVisible(loginPage.getUsernameInputSelector());
            waitForElementVisible(loginPage.getPasswordInputSelector());
            waitForElementEnabled(loginPage.getLoginButtonSelector());

            // Perform login Test Steps
            //Step1
            logInfo("Step 1: Entering username: " + username);
            loginPage.enterUsername(username);

            //Step2
            logInfo("Step 2: Entering password: " + password);
            loginPage.enterPassword(password);

            //Step3
            logInfo("Step 3: Clicking login button");
            loginPage.clickLoginButton();

            // Validate the result with appropriate waits

                // Wait for error message to be displayed
                logInfo("Waiting for error message");
                waitForElementVisible(loginPage.getErrorMessageSelector());

                boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();
                String actualError = loginPage.getErrorMessage();

                Assert.assertTrue(isErrorDisplayed, "Error message should be displayed for user: " + username);
                Assert.assertEquals(actualError, expectedErrorMessage, "Error message not matching for user: " + username);

                if (actualError.equals(expectedErrorMessage)) {
                    logPass("Validation successful: Error message displayed correctly: " + actualError);
                } else {
                    logFail("Validation failed: Error message incorrect or not displayed");
                }

        } catch (Exception e) {
            logFail("Negative scenario Failed");
            Assert.fail("Login should not succeed in user: " + e.getMessage());
            throw e;
        }
    }

    @AfterMethod
    public void resetForNextTest() {
        // If we're on the inventory page, navigate back to login page for next test
        if (loginPage != null && loginPage.isLoginSuccessful()) {
            logInfo("Resetting test: Navigating back to login page");
            loginPage.navigateToLoginPage();
            // Wait for login page to load
            waitForElementVisible(loginPage.getUsernameInputSelector());
        }
    }
}
