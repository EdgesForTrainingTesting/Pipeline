package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Utility class for Allure reporting integration
 */
public class AllureUtil {

    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final String LOGS_DIR = "target/logs";

    private AllureUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Attach a screenshot to the Allure report
     */
    public static void attachScreenshot(WebDriver driver, String name) {
        try {
            if (driver == null) {
                System.err.println("WebDriver is null, cannot take screenshot");
                return;
            }

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
            System.out.println("✅ Attached screenshot to Allure: " + name);
        } catch (Exception e) {
            System.err.println("❌ Failed to attach screenshot: " + e.getMessage());
        }
    }

    /**
     * Save and attach screenshot to Allure report
     */
    public static void saveAndAttachScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshot directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.png", testName.replaceAll("[^a-zA-Z0-9]", "_"), timestamp);
            File screenshotFile = new File(screenshotDir, fileName);

            // Take screenshot
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(screenshotFile.toPath(), screenshot);

            // Attach to Allure
            Allure.addAttachment(testName, "image/png", new ByteArrayInputStream(screenshot), ".png");

            System.out.println("✅ Screenshot saved and attached: " + screenshotFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Failed to save screenshot: " + e.getMessage());
        }
    }

    /**
     * Attach text content to Allure report
     */
    public static void attachText(String name, String content) {
        try {
            Allure.addAttachment(name, "text/plain", content, ".txt");
            System.out.println("✅ Attached text to Allure: " + name);
        } catch (Exception e) {
            System.err.println("❌ Failed to attach text: " + e.getMessage());
        }
    }

    /**
     * Attach log file to Allure report
     */
    public static void attachLogFile() {
        try {
            File logFile = getLatestLogFile();
            if (logFile == null || !logFile.exists()) {
                System.err.println("⚠️  Log file does not exist");
                return;
            }

            String logContent = Files.readString(logFile.toPath());
            Allure.addAttachment("Test Logs", "text/plain", logContent, ".log");
            System.out.println("✅ Attached log file to Allure: " + logFile.getName());
        } catch (Exception e) {
            System.err.println("❌ Failed to attach log file: " + e.getMessage());
        }
    }

    /**
     * Attach screenshot from file path
     */
    public static void attachScreenshotFromPath(String screenshotName, String screenshotPath) {
        try {
            File screenshot = new File(screenshotPath);
            if (!screenshot.exists()) {
                System.err.println("⚠️  Screenshot file does not exist: " + screenshotPath);
                return;
            }

            try (FileInputStream fis = new FileInputStream(screenshot)) {
                Allure.addAttachment(screenshotName, "image/png", fis, ".png");
                System.out.println("✅ Attached screenshot from path: " + screenshotPath);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to attach screenshot from path: " + e.getMessage());
        }
    }

    /**
     * Attach HTML content to Allure report
     */
    public static void attachHtml(String name, String htmlContent) {
        try {
            Allure.addAttachment(name, "text/html", htmlContent, ".html");
            System.out.println("✅ Attached HTML to Allure: " + name);
        } catch (Exception e) {
            System.err.println("❌ Failed to attach HTML: " + e.getMessage());
        }
    }

    /**
     * Attach JSON content to Allure report
     */
    public static void attachJson(String name, String jsonContent) {
        try {
            Allure.addAttachment(name, "application/json", jsonContent, ".json");
            System.out.println("✅ Attached JSON to Allure: " + name);
        } catch (Exception e) {
            System.err.println("❌ Failed to attach JSON: " + e.getMessage());
        }
    }

    /**
     * Add a step to Allure report
     */
    public static void step(String stepName) {
        Allure.step(stepName);
        System.out.println("📍 Step: " + stepName);
    }

    /**
     * Get the latest log file from logs directory
     */
    private static File getLatestLogFile() {
        try {
            File logsDir = new File(LOGS_DIR);
            if (!logsDir.exists() || !logsDir.isDirectory()) {
                return null;
            }

            File[] logFiles = logsDir.listFiles((dir, name) -> name.endsWith(".log"));
            if (logFiles == null || logFiles.length == 0) {
                return null;
            }

            return Arrays.stream(logFiles)
                    .max(Comparator.comparingLong(File::lastModified))
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("❌ Error finding latest log file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attach all screenshots from the screenshots directory
     */
    public static void attachAllScreenshots() {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists() || !screenshotDir.isDirectory()) {
                System.out.println("⚠️  Screenshot directory does not exist");
                return;
            }

            File[] screenshots = screenshotDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));

            if (screenshots == null || screenshots.length == 0) {
                System.out.println("⚠️  No screenshots found");
                return;
            }

            for (File screenshot : screenshots) {
                try (FileInputStream fis = new FileInputStream(screenshot)) {
                    Allure.addAttachment(screenshot.getName(), "image/png", fis, ".png");
                }
            }

            System.out.println("✅ Attached " + screenshots.length + " screenshots to Allure");
        } catch (Exception e) {
            System.err.println("❌ Failed to attach screenshots: " + e.getMessage());
        }
    }

    /**
     * Create screenshot directory if it doesn't exist
     */
    public static void ensureScreenshotDirectoryExists() {
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("✅ Created screenshot directory: " + SCREENSHOT_DIR);
        }
    }
}