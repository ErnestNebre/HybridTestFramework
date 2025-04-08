package test.pageobjects.saucedemo;

import com.microsoft.playwright.Page;

public class LoginPageObjects {
    private final Page page;

    // Page Objects Definitions
    // Element Css selector
    private final String URL = "https://www.saucedemo.com/";
    private final String LOGIN_BUTTON = "#login-button";
    private final String ERROR_MESSAGE = "h3[data-test='error']";
    // Alternative using name attribute:
    private final String USERNAME_INPUT = "[name='user-name']";
    // XPath Locator/Selector
    private final String PASSWORD_INPUT = "//input[@id='password']";

    // Constructor
    public LoginPageObjects(Page page) {
        this.page = page;
    }

    // Getter methods for selectors
    public String getUsernameInputSelector() {
        return USERNAME_INPUT;
    }

    public String getPasswordInputSelector() {
        return PASSWORD_INPUT;
    }

    public String getLoginButtonSelector() {
        return LOGIN_BUTTON;
    }

    public String getErrorMessageSelector() {
        return ERROR_MESSAGE;
    }

    // Page actions
    public void navigateToLoginPage() {
        page.navigate(URL);
    }

    // Page actions: Enter username
    public void enterUsername(String username) {
        page.fill(USERNAME_INPUT, username);
    }

    // Page actions: Enter password
    public void enterPassword(String password) {
        page.fill(PASSWORD_INPUT, password);
    }

    // Page actions: Click login button
    public void clickLoginButton() {
        page.click(LOGIN_BUTTON);
    }

    public String getErrorMessage() {
        return page.textContent(ERROR_MESSAGE);
    }

    public boolean isErrorMessageDisplayed() {
        return page.isVisible(ERROR_MESSAGE);
    }

    // Composite actions
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isLoginSuccessful() {
        return page.url().contains("inventory.html");
    }
}
