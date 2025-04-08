package test.pageobjects.todo;

import com.microsoft.playwright.Page;

public class TodoItemPageObjects {
    private final Page page;

    // Simple locators
    private static final String TODO_APP_URL = "https://todo.uiineed.com/";
    private static final String NEW_TODO_INPUT = "//input[@placeholder='Add a to-do item...']";
    private static final String ADD_BUTTON = "//button[text()='Add']";
    private static final String TODO_LIST = "//ul[contains(@class, 'todo-list')]";

    public TodoItemPageObjects(Page page) {
        this.page = page;
    }

    // Dynamic selectors
    public String getTodoItemByTextSelector(String todoText) {
        return "//div[normalize-space(text())='" + todoText + "']";
    }

    public String getDeleteButtonForTodoSelector(String todoText) {
        return "//div[normalize-space(text())='" + todoText + "']/following-sibling::div[contains(@class, 'todo-btn btn-delete')]";
    }

    // Actions

    public void refreshPage() {
        page.reload();
    }

    public void navigateToTodoApp() {
        page.navigate(TODO_APP_URL);
    }

    public void enterTodoText(String todoText) {
        page.fill(NEW_TODO_INPUT, todoText);
    }

    public void clickAddButton() {
        page.click(ADD_BUTTON);
    }

    public void deleteTodo(String todoText) {
        page.click(getDeleteButtonForTodoSelector(todoText));
    }

    // Verifications
    public boolean isTodoCreated(String todoText) {
        return page.locator(getTodoItemByTextSelector(todoText)).count() > 0;
    }

    // Getters for selectors
    public String getNewTodoInputSelector() {
        return NEW_TODO_INPUT;
    }




}
