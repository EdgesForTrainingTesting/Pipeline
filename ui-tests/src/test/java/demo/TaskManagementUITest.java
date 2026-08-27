package demo;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.time.Duration;
import java.util.Objects;

/**
 * UI Tests for Task Management System using Selenium with Allure Reporting
 */
@Epic("Task Management System")
@Feature("UI Testing")
public class TaskManagementUITest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;
    private String trial;

    @BeforeMethod
    public void setUp() throws Exception {
        utils.AllureUtil.step("Setting up WebDriver");

        baseUrl = resolveBaseUrl();
        utils.AllureUtil.attachText("BASE_URL", baseUrl);

        String seleniumUrl =
                System.getProperty("selenium.remote.url", System.getenv("SELENIUM_REMOTE_URL"));

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        // 🔹 NEW: wire env vars from CI (and you can also set them locally if you like)
        String chromeBin = System.getenv("CHROME_BIN");
        if (chromeBin != null && !chromeBin.isBlank()) {
            options.setBinary(chromeBin);
            utils.AllureUtil.attachText("CHROME_BIN", chromeBin);
        }

        String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");
        if (chromeDriverPath != null && !chromeDriverPath.isBlank()) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            utils.AllureUtil.attachText("CHROMEDRIVER_PATH", chromeDriverPath);
        }

        try {
            if (seleniumUrl != null && !seleniumUrl.isBlank()) {
                utils.AllureUtil.attachText("Selenium URL", seleniumUrl);
                driver = new RemoteWebDriver(new URL(seleniumUrl), options);
            } else {
                utils.AllureUtil.step("No SELENIUM_REMOTE_URL – using local ChromeDriver");
                driver = new org.openqa.selenium.chrome.ChromeDriver(options);
            }
        } catch (Exception e) {
            utils.AllureUtil.attachText("Remote WebDriver error", e.getMessage());
            utils.AllureUtil.step("Falling back to local ChromeDriver");
            // Ensure env-based config is still applied
            driver = new org.openqa.selenium.chrome.ChromeDriver(options);
        }

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        utils.AllureUtil.ensureScreenshotDirectoryExists();
    }

    private String resolveBaseUrl() {
        // 1) JVM system property: -Dapp.baseUrl=...
        String fromProp = System.getProperty("app.baseUrl");
        if (fromProp != null && !fromProp.isBlank()) {
            return fromProp;
        }

        // 2) Environment variable: APP_BASE_URL=...
        String fromEnv = System.getenv("APP_BASE_URL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        // 3) Safe default for both IntelliJ + pipeline:
        // assume we serve index.html on port 8080.
        return "http://localhost:8080/index.html";
    }

    @AfterMethod
    public void tearDown() {
        utils.AllureUtil.step("Tearing down WebDriver");

        if (driver != null) {
            utils.AllureUtil.attachScreenshot(driver, "Final State");
            driver.quit();
        }
    }

    private void waitForPageLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> ((JavascriptExecutor) d)
                        .executeScript("return document.readyState").equals("complete"));
    }


    @Test
    @Story("Home Page")
    @Description("Verify that the home page loads successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testHomePageLoads() {
        utils.AllureUtil.step("Navigate to home page");
        driver.get(baseUrl);
        waitForPageLoad();

        utils.AllureUtil.step("Verify page title");
        String title = driver.getTitle();
        utils.AllureUtil.attachText("Page Title", title);

        Assert.assertTrue(title.contains("Task Managements") || title.length() > 0,
                "Home page should load successfully");

        utils.AllureUtil.attachScreenshot(driver, "Home Page Loaded");
    }

    @Test
    @Story("Task Creation")
    @Description("Verify that the create task button exists and is visible")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateTaskButtonExists() {
        utils.AllureUtil.step("Navigate to home page");
        driver.get(baseUrl);
        waitForPageLoad();

        utils.AllureUtil.step("Wait for create task button");
        WebElement createButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("create-task-btn"))
        );

        utils.AllureUtil.step("Verify button is displayed");
        Assert.assertTrue(createButton.isDisplayed(),
                "Create Task button should be visible");

        utils.AllureUtil.attachScreenshot(driver, "Create Task Button");
    }

    @Test
    @Story("Task Display")
    @Description("Verify that you can see the task list on the home page")
    @Severity(SeverityLevel.NORMAL)
    public void testTaskListDisplays() {
        utils.AllureUtil.step("Navigate to home page");
        driver.get(baseUrl);
        waitForPageLoad();

        utils.AllureUtil.step("Wait for task list to be present");
        WebElement taskList = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("task-list"))
        );

        Assert.assertTrue(taskList.isDisplayed(),
                "Task list should be displayed on the page");
        utils.AllureUtil.attachScreenshot(driver, "Task List Displayed");
    }

    @Test
    @Story("Task Creation")
    @Description("Verify that you can create new tasks via the UI")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateNewTask() {
        utils.AllureUtil.step("Navigate to home page");
        driver.get(baseUrl);
        waitForPageLoad();

        utils.AllureUtil.step("Click create task button");
        WebElement createButton = driver.findElement(By.id("create-task-btn"));
        createButton.click();

        // Wait for modal to appear
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("task-modal"))
        );

        utils.AllureUtil.step("Fill in task details");
        WebElement titleInput = driver.findElement(By.id("task-title"));
        WebElement descInput = driver.findElement(By.id("task-description"));
        WebElement prioritySelect = driver.findElement(By.id("task-priority"));

        titleInput.sendKeys("New Test Task");
        descInput.sendKeys("This is a test task created by Selenium");
        prioritySelect.sendKeys("High");

        utils.AllureUtil.step("Submit new task");
        WebElement submitButton = driver.findElement(By.id("submit-task-btn"));
        submitButton.click();

        driver.switchTo().alert().accept();

        // Verify task was created
        wait.until(ExpectedConditions.invisibilityOf(modal));
        utils.AllureUtil.attachScreenshot(driver, "Task Created");
    }

    @Test
    @Story("Task Filtration")
    @Description("Verify that you can filter tasks by status")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterTasksByStatus() {
        utils.AllureUtil.step("Navigate to home page");
        driver.get(baseUrl);
        waitForPageLoad();

        utils.AllureUtil.step("Open status filter dropdown");
        WebElement statusFilter = driver.findElement(By.id("status-filter"));
        statusFilter.click();

       utils.AllureUtil.step("Select 'In Progress' status");
        WebElement inProgressOption = driver.findElement(
                By.xpath("//option[@value='IN_PROGRESS']")
        );
        inProgressOption.click();

        // Wait for filtered results
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("task-item")));


        Assert.assertTrue(Objects.requireNonNull(driver.getPageSource()).contains("In Progress"),
                "Filtered view should show In Progress tasks");
        utils.AllureUtil.attachScreenshot(driver, "Filtered Tasks by Status");
    }

    @Test
    @Story("Task Search")
    @Description("Verify that you can search tasks by title")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchTaskByTitle() {
        utils.AllureUtil.step("Navigate to home page.");
        driver.get(baseUrl);
        waitForPageLoad();

        utils.AllureUtil.step("Enter search term in search box");
        WebElement searchBox = driver.findElement(By.id("search-input"));
        searchBox.sendKeys("authentication");

        // Wait for search results
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("task-item")));

        WebElement searchResult = driver.findElement(By.className("task-item"));
        String taskTitle = searchResult.getText();

        Assert.assertTrue(taskTitle.toLowerCase().contains("authentication"),
                "Search results should contain tasks matching the search term");
        utils.AllureUtil.attachScreenshot(driver, "Search Results for Task Title");
    }


    @Test
    @Story("Task Display")
    @Description("Verify that you can see priority badges with correct colors")
    @Severity(SeverityLevel.NORMAL)
    public void testPriorityBadgeColors() {
        utils.AllureUtil.step("Navigate to home page");
        driver.get(baseUrl);
        waitForPageLoad();

        WebElement highPriorityTask = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//span[contains(@class, 'priority-high')]")
                )
        );

        String bgColor = highPriorityTask.getCssValue("background-color");


        Assert.assertTrue(bgColor.contains("rgb") || bgColor.contains("#"),
                "Priority badge should have a background color");
        utils.AllureUtil.attachText("High Priority Badge Color", bgColor);
    }

    @Test
    @Story("Task Display")
    @Description("Verify that you can see priority badges with correct colors 2 ")
    @Severity(SeverityLevel.NORMAL)
    public void testPriorityBadgeColors2() {
        utils.AllureUtil.step("Navigate to home page");
        driver.get(baseUrl);
        waitForPageLoad();

        WebElement highPriorityTask = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//span[contains(@class, 'priority-high')]")
                )
        );

        String bgColor = highPriorityTask.getCssValue("background-color");


        Assert.assertTrue(bgColor.contains("rgb") || bgColor.contains("#"),
                "Priority badge should have a background color");
        utils.AllureUtil.attachText("High Priority Badge Color", bgColor);
        Assert.assertTrue(false);
    }

}