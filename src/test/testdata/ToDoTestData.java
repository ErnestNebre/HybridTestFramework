package test.testdata;

import org.testng.annotations.DataProvider;

public class ToDoTestData {

    @DataProvider(name = "todoInputs")
    public Object[][] provideTodoInputs() {
        return new Object[][]{
                {"This is a test Input 1"},
                {"This is a test input 2 with a very very very long entry"}
        };
    }
}