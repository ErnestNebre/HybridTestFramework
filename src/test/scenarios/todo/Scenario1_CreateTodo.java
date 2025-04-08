package test.scenarios.todo;

import org.testng.Assert;
import org.testng.annotations.Test;
import test.testdata.ToDoTestData;
import test.pageobjects.todo.TodoItemPageObjects;
import test.utility.Commons;

import java.io.IOException;

public class Scenario1_CreateTodo extends Commons {
    private TodoItemPageObjects todoPage;

    @Test(dataProvider = "todoInputs", dataProviderClass = ToDoTestData.class)
    public void createAndDeleteTodo(String todoText) throws IOException {
        // Initialize page object
        todoPage = new TodoItemPageObjects(page);

        // Start test case
        startTestCase("Create and Delete Todo Test with: " + todoText);

        // Step 1: Launch URL
        logInfo("Step 1: Navigating to Todo application");
        todoPage.navigateToTodoApp();

        // Wait for page to load
        waitForElementVisible(todoPage.getNewTodoInputSelector());

        // Get todoText with timestamp (calling the javascript method
        String timestampedTodoText = addTimestampToText(todoText);

        // Step 2: Type in the text field
        logInfo("Step 2: Entering todo text: " + todoText);
        todoPage.enterTodoText(timestampedTodoText);

        // Step 3: Click Add button
        logInfo("Step 3: Clicking Add button");
        todoPage.clickAddButton();

        //refresh the page
        todoPage.refreshPage();

        // Step 4: Verify todo is created
        logInfo("Step 4: Verifying todo item was created: " + timestampedTodoText);
        waitForElementVisible(todoPage.getTodoItemByTextSelector(timestampedTodoText));

        if (todoPage.isTodoCreated(timestampedTodoText)) {
            logPass("Todo item was created successfully: " + timestampedTodoText);
        } else {
            logFail("Failed to create todo item: " + timestampedTodoText);
            Assert.fail("Todo item was not created: " + timestampedTodoText);
            return;
        }

        // Capture screenshot after todo creation
        scrollAndCaptureScreenshot("todo_created_" + todoText.replaceAll("\\s+", "_"));

        // Step 5: Click delete button
        logInfo("Step 5: Deleting todo item: " + todoText);
        todoPage.deleteTodo(timestampedTodoText);

        // Step 6: Verify todo is deleted
        logInfo("Step 6: Verifying todo item was deleted: " + timestampedTodoText);

        try {
            page.waitForTimeout(1000); // Wait for UI update

            boolean itemStillExists = todoPage.isTodoCreated(timestampedTodoText);
            if (itemStillExists) {
                logFail("Todo item '" + todoText + "' still exists after deletion");
                Assert.fail("Todo item was not deleted: " + todoText);
            } else {
                logPass("Todo item '" + todoText + "' was successfully deleted");
            }

            // Final screenshot
            scrollAndCaptureScreenshot("todo_deleted_" + todoText.replaceAll("\\s+", "_"));
        } catch (Exception e) {
            logFail("Error checking if todo item was deleted: " + e.getMessage());
            Assert.fail("Error checking if todo item was deleted: " + e.getMessage());
        }
    }
}