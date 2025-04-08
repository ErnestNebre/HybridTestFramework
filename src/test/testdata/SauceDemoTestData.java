package test.testdata;

import org.testng.annotations.DataProvider;

public class SauceDemoTestData {

    @DataProvider(name = "loginCredentials")
    public Object[][] getLoginCredentials() {
        return new Object[][] {
            // username, password, expectedResult, expectedMessage
            {"standard_user", "secret_sauce", null},
            //test should fail
            {"standard_user", "wrong_password", null},
            //performance glitch user
            {"performance_glitch_user", "secret_sauce", null},
            //problem user
            {"problem_user", "secret_sauce", null},
        };
    }
    @DataProvider(name = "loginNegativeCredsentials")
    public Object[][] getloginNegativeCredsentials() {
        return new Object[][] {
                {"locked_out_user", "secret_sauce", "Epic sadface: Sorry, this user has been locked out."},
                {"standard_user", "wrong_password", "Epic sadface: Username and password do not match any user in this service"},
                {"wrong_user", "secret_sauce", "Epic sadface: Username and password do not match any user in this service"}
        };
    }
}
