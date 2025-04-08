package test.utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;

public class Commons {
    public static ExtentReports extent;
    public static ExtentSparkReporter rpt;
    public static ExtentTest logger;
    public Test testclass;

    // Playwright objects
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    // Default timeout values
    protected final int DEFAULT_TIMEOUT = 10000; // 10 seconds
    protected final int POLLING_INTERVAL = 500; // 0.5 second

    @BeforeSuite
    public void StartTest() throws IOException {
        rpt = new ExtentSparkReporter("./test-output/TestReport.html");
        extent = new ExtentReports();
        extent.attachReporter(rpt);
    }

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
        page = browser.newPage();
    }

    @BeforeMethod
    public void beforeMethod(Method method) throws IOException {
        testclass = method.getAnnotation(Test.class);
    }

    @AfterMethod
    public void getResult(ITestResult result) throws IOException {
        ExceptionCategory(result);
        extent.flush();
    }

    @AfterClass
    public void tearDown() {
        if (page != null) {
            page.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * Handle test result and log it in ExtentReports
     * @param result Test result object
     * @throws IOException If an error occurs during file operations
     */
    public void ExceptionCategory(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.fail("Test Failed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.skip("Test Skipped");
            logger.skip(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.pass("Test Passed");
        }

        extent.flush();
    }
    /**
     * Log test step with info status
     * @param stepDescription Description of the test step
     */
    public void logInfo(String stepDescription) {
        if (logger != null) {
            logger.info(stepDescription);
        }
    }
    /**
     * Log test step with pass status and capture screenshot
     * @param stepDescription Description of the test step
     */
    public void logPass(String stepDescription) {
        if (logger != null) {
            logger.pass(stepDescription);
            try {
                String screenshotPath = scrollAndCaptureScreenshot("PASS_" + stepDescription.replaceAll("[^a-zA-Z0-9]", "_"));
                logger.addScreenCaptureFromPath(screenshotPath);
            } catch (IOException e) {
                logger.info("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    /**
     * Log test step with fail status and capture screenshot
     * @param stepDescription Description of the test step
     */
    public void logFail(String stepDescription) {
        if (logger != null) {
            logger.fail(stepDescription);
            try {
                String screenshotPath = scrollAndCaptureScreenshot("FAIL_" + stepDescription.replaceAll("[^a-zA-Z0-9]", "_"));
                logger.addScreenCaptureFromPath(screenshotPath);
            } catch (IOException e) {
                logger.info("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }
    /**
     * Initialize test in ExtentReports
     * @param testName Name of the test
     * @param description Optional description of the test
     */
    public void startTestCase(String testName, String... description) {
        String desc = description.length > 0 ? description[0] : "";
        logger = extent.createTest(testName, desc);
    }
    /**
     * Capture screenshot and save it in the specified directory
     * @param fileName Name of the screenshot file
     * @return Path to the saved screenshot
     * @throws IOException If an error occurs during file operations
     */
    public String scrollAndCaptureScreenshot(String fileName) throws IOException {
        // Create screenshots directory if it doesn't exist
        File screenshotDir = new File(System.getProperty("user.dir") + "/test-output/screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        // Scroll to bottom of page using Playwright
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");

        // Capture screenshot using Playwright
        String fileNamePath = "screenshots/" + fileName + ".png";
        String absolutePath = System.getProperty("user.dir") + "/test-output/" + fileNamePath;

        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(absolutePath))
                .setFullPage(true));

        // Return just the filename portion for use in the report
        return fileNamePath;
    }
    /**
     * Wait for an element to be visible
     * @param selector CSS selector of the element
     * @param timeout Optional timeout in milliseconds
     */
    protected void waitForElementVisible(String selector, int... timeout) {
        int timeoutMs = timeout.length > 0 ? timeout[0] : DEFAULT_TIMEOUT;
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }
    /**
     * Wait for an element to be enabled
     * @param selector CSS selector of the element
     * @param timeout Optional timeout in milliseconds
     */
    protected void waitForElementEnabled(String selector, int... timeout) {
        int timeoutMs = timeout.length > 0 ? timeout[0] : DEFAULT_TIMEOUT;
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.ATTACHED)
                .setTimeout(timeoutMs));

        // Additionally check if element is enabled
        page.waitForFunction("selector => !document.querySelector(selector).disabled",
                selector, new Page.WaitForFunctionOptions().setTimeout(timeoutMs));
    }
    /**
     * Wait for the URL to contain specific text
     * @param urlText Text to wait for in the URL
     * @param timeout Optional timeout in milliseconds
     */
    protected void waitForUrlContains(String urlText, int... timeout) {
        int timeoutMs = timeout.length > 0 ? timeout[0] : DEFAULT_TIMEOUT;

        // Instead of using regex pattern which may fail, use a polling approach
        page.waitForFunction(
                "expected => window.location.href.includes(expected)",
                urlText,
                new Page.WaitForFunctionOptions().setTimeout(timeoutMs)
        );
    }
    /**
     * Checks if a dialog box is present and clicks OK if it is.
     * Returns true if a dialog was handled, false otherwise.
     *
     * @param timeoutMs Maximum time to wait for dialog in milliseconds
     * @return true if dialog was detected and handled, false otherwise
     */
    public boolean checkAndHandleDialog(int timeoutMs) {
        logInfo("Checking for dialog box presence");
        final boolean[] dialogDetected = {false};

        // Set up dialog handler
        page.onDialog(dialog -> {
            String message = dialog.message();
            logInfo("Dialog detected with message: " + message);
            dialogDetected[0] = true;

            try {
                dialog.accept();
                logInfo("Clicked OK on dialog box");
            } catch (Exception e) {
                logInfo("Failed to accept dialog: " + e.getMessage());
            }
        });

        // Wait a bit to see if dialog appears
        try {
            // Use a polling approach to check if dialog was detected
            long endTime = System.currentTimeMillis() + timeoutMs;
            while (System.currentTimeMillis() < endTime && !dialogDetected[0]) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return dialogDetected[0];
    }
    /**
     * Adds a timestamp to todo text using JavaScript
     * @param todoText The original todo text
     * @return The todo text with timestamp appended
     */
    public String addTimestampToText(String todoText) {
        // Use JavaScript to generate a timestamp and append it to the text
        String timestampedText = (String) page.evaluate("(text) => {" +
                "const now = new Date();" +
                "const timestamp = now.toLocaleString();" +
                "return `${text} - ${timestamp}`;" +
                "}", todoText);
        return timestampedText;
    }
}
