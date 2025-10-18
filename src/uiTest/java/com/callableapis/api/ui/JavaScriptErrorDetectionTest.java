package com.callableapis.api.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests specifically designed to detect JavaScript errors and regressions.
 * These tests will fail if JavaScript console errors are detected.
 */
@DisplayName("JavaScript Error Detection Tests")
public class JavaScriptErrorDetectionTest extends BaseUITest {

    @Test
    @DisplayName("No JavaScript errors on home page load")
    public void testNoJavaScriptErrorsOnHomePageLoad() {
        driver.get(baseUrl);
        waitForJavaScriptReady();

        // Verify page loaded successfully
        assertTrue(driver.getTitle().contains("Callable APIs"));

        // The tearDown method will check for JavaScript errors
        // This test will fail if any JavaScript errors are detected
    }

    @Test
    @DisplayName("No JavaScript errors on authenticated page load")
    public void testNoJavaScriptErrorsOnAuthenticatedPageLoad() {
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key-12345";

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Verify page loaded successfully
        assertTrue(driver.getTitle().contains("Callable APIs"));

        // The tearDown method will check for JavaScript errors
    }

    @Test
    @DisplayName("No JavaScript errors when clicking copy button")
    public void testNoJavaScriptErrorsWhenClickingCopyButton() {
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key-12345";

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Find and click copy button
        WebElement copyButton = driver.findElement(By.className("copy-button"));
        copyButton.click();

        // Wait for any async operations to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // The tearDown method will check for JavaScript errors
    }

    @Test
    @DisplayName("No JavaScript errors when clicking test API buttons")
    public void testNoJavaScriptErrorsWhenClickingTestApiButtons() {
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key-12345";

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Find and click all test buttons
        var testButtons = driver.findElements(By.className("test-button"));
        for (WebElement button : testButtons) {
            button.click();

            // Wait for any async operations to complete
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // The tearDown method will check for JavaScript errors
    }

    @Test
    @DisplayName("No JavaScript errors when executing JavaScript functions")
    public void testNoJavaScriptErrorsWhenExecutingJavaScriptFunctions() {
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key-12345";

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Test JavaScript functions directly
        try {
            // Test copyApiKey function
            executeJavaScript("if (typeof copyApiKey === 'function') { copyApiKey(); }");

            // Test testApi function
            executeJavaScript("if (typeof testApi === 'function') { testApi('/api/v1/calendar/date'); }");

            // Wait for any async operations to complete
            Thread.sleep(2000);

        } catch (Exception e) {
            // If JavaScript execution fails, that's also a problem we want to catch
            fail("JavaScript execution failed: " + e.getMessage());
        }

        // The tearDown method will check for JavaScript errors
    }

    @Test
    @DisplayName("JavaScript APIs are available and functional")
    public void testJavaScriptAPIsAreAvailableAndFunctional() {
        driver.get(baseUrl);
        waitForJavaScriptReady();

        // Test that basic JavaScript APIs are available
        assertTrue(isFetchAPIAvailable(), "Fetch API should be available");

        // Test that we can execute basic JavaScript
        Object result = executeJavaScript("return typeof document !== 'undefined'");
        assertTrue((Boolean) result, "Document object should be available");

        // Test that we can access DOM elements
        result = executeJavaScript("return document.querySelector('h1') !== null");
        assertTrue((Boolean) result, "Should be able to query DOM elements");
    }

    @Test
    @DisplayName("No JavaScript errors with malformed URLs")
    public void testNoJavaScriptErrorsWithMalformedUrls() {
        // Test with various malformed URLs that might cause JavaScript errors
        String[] testUrls = {
                baseUrl + "/api/authenticated?userIdentity=&apiKey=",
                baseUrl + "/api/authenticated?userIdentity=testuser",
                baseUrl + "/api/authenticated?apiKey=test-key",
                baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key&invalidParam=value"
        };

        for (String testUrl : testUrls) {
            driver.get(testUrl);
            waitForJavaScriptReady();

            // Wait for any async operations to complete
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // The tearDown method will check for JavaScript errors
    }

    @Test
    @DisplayName("No JavaScript errors when navigating between pages")
    public void testNoJavaScriptErrorsWhenNavigatingBetweenPages() {
        // Start at home page
        driver.get(baseUrl);
        waitForJavaScriptReady();

        // Navigate to authenticated page
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key";
        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Navigate back to home page
        driver.get(baseUrl);
        waitForJavaScriptReady();

        // The tearDown method will check for JavaScript errors
    }

    @Test
    @DisplayName("JavaScript error handling works correctly")
    public void testJavaScriptErrorHandlingWorksCorrectly() {
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key-12345";

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Test error handling in JavaScript functions
        try {
            // Test copyApiKey with invalid element (should handle gracefully)
            executeJavaScript("""
                        try {
                            var invalidElement = document.getElementById('invalid-api-key');
                            if (invalidElement) {
                                invalidElement.textContent = 'test';
                            }
                        } catch (e) {
                            console.log('Expected error handled:', e.message);
                        }
                    """);

            // Test testApi with invalid endpoint (should handle gracefully)
            executeJavaScript("""
                        try {
                            if (typeof testApi === 'function') {
                                testApi('/invalid-endpoint');
                            }
                        } catch (e) {
                            console.log('Expected error handled:', e.message);
                        }
                    """);

            // Wait for any async operations to complete
            Thread.sleep(2000);

        } catch (Exception e) {
            // If JavaScript execution fails, that's also a problem we want to catch
            fail("JavaScript execution failed: " + e.getMessage());
        }

        // The tearDown method will check for JavaScript errors
    }
}
