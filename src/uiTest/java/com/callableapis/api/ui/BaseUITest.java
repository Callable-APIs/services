package com.callableapis.api.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

/**
 * Base class for UI tests providing common WebDriver setup and utilities.
 * This class handles browser initialization, logging, and cleanup.
 */
public abstract class BaseUITest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor jsExecutor;
    protected String baseUrl;

    @BeforeEach
    public void setUp() {
        // Get base URL from system property or default to localhost
        baseUrl = System.getProperty("test.base.url", "http://localhost:8080");

        // Get browser type from system property or default to chrome
        String browser = System.getProperty("test.browser", "chrome");

        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new EdgeDriver(edgeOptions);
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless"); // Run headless by default
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--enable-logging");
                chromeOptions.addArguments("--log-level=0");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        jsExecutor = (JavascriptExecutor) driver;
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            // Capture and report JavaScript console errors
            captureJavaScriptErrors();
            driver.quit();
        }
    }

    /**
     * Captures and reports JavaScript console errors.
     * This helps identify UI regressions and JavaScript issues.
     */
    protected void captureJavaScriptErrors() {
        try {
            // Use JavaScript to check for console errors
            Object result = jsExecutor.executeScript("""
                        if (window.console && window.console.error) {
                            return window.console.error.toString();
                        }
                        return 'No console available';
                    """);

            // Check if there are any JavaScript errors by looking at the page
            Object hasErrors = jsExecutor.executeScript("""
                        return window.console && window.console.error &&
                               (window.console.error.toString().includes('Error') ||
                                window.console.error.toString().includes('TypeError') ||
                                window.console.error.toString().includes('ReferenceError'));
                    """);

            if (Boolean.TRUE.equals(hasErrors)) {
                System.err.println("Potential JavaScript errors detected in console");
            }

        } catch (Exception e) {
            System.err.println("Could not capture JavaScript logs: " + e.getMessage());
        }
    }

    /**
     * Executes JavaScript and returns the result.
     * Useful for testing JavaScript functionality.
     */
    protected Object executeJavaScript(String script) {
        return jsExecutor.executeScript(script);
    }

    /**
     * Waits for JavaScript to be ready and checks for console errors.
     */
    protected void waitForJavaScriptReady() {
        // Wait for document ready state
        wait.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));

        // Small delay to allow any async JavaScript to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Checks if the clipboard API is available in the current browser.
     */
    protected boolean isClipboardAPIAvailable() {
        try {
            Boolean result = (Boolean) jsExecutor.executeScript(
                    "return typeof navigator !== 'undefined' && " +
                            "typeof navigator.clipboard !== 'undefined' && " +
                            "typeof navigator.clipboard.writeText === 'function'");
            return result != null && result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the fetch API is available in the current browser.
     */
    protected boolean isFetchAPIAvailable() {
        try {
            Boolean result = (Boolean) jsExecutor.executeScript(
                    "return typeof fetch !== 'undefined'");
            return result != null && result;
        } catch (Exception e) {
            return false;
        }
    }
}
