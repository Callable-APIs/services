package com.callableapis.api.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI tests for the OIDC authentication flow.
 * These tests verify that the user interface works correctly and catches
 * JavaScript regressions.
 */
@DisplayName("OIDC Flow UI Tests")
public class OIDCFlowUITest extends BaseUITest {

    @Test
    @DisplayName("Home page loads without JavaScript errors")
    public void testHomePageLoadsWithoutErrors() {
        driver.get(baseUrl);
        waitForJavaScriptReady();

        // Verify page title
        assertEquals("Callable APIs", driver.getTitle());

        // Verify main elements are present
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Callable APIs"));
        assertTrue(driver.findElement(By.className("status-unauthenticated")).isDisplayed());

        // Verify GitHub login link is present
        WebElement loginLink = driver.findElement(By.linkText("Connect with GitHub"));
        assertTrue(loginLink.isDisplayed());
        assertTrue(loginLink.getAttribute("href").contains("github.com"));
    }

    @Test
    @DisplayName("Home page JavaScript functionality works")
    public void testHomePageJavaScriptFunctionality() {
        driver.get(baseUrl);
        waitForJavaScriptReady();

        // Test that required JavaScript APIs are available
        assertTrue(isFetchAPIAvailable(), "Fetch API should be available for API testing");

        // Verify page structure is correct
        WebElement container = driver.findElement(By.className("container"));
        assertTrue(container.isDisplayed());

        // Verify API cards are present
        List<WebElement> apiCards = driver.findElements(By.className("api-card"));
        assertTrue(apiCards.size() >= 4, "Should have at least 4 API cards");

        // Verify version information is present
        WebElement versionInfo = driver.findElement(By.className("version-info"));
        assertTrue(versionInfo.isDisplayed());
    }

    @Test
    @DisplayName("Authenticated page loads without JavaScript errors")
    public void testAuthenticatedPageLoadsWithoutErrors() {
        // Navigate to authenticated page with mock data
        String mockUserIdentity = "testuser";
        String mockApiKey = "test-api-key-12345";
        String mockGitCommitHash = "abc123";
        String mockShortCommitHash = "abc123";
        String mockBuildTime = "2024-01-01T00:00:00Z";

        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=" + mockUserIdentity +
                "&apiKey=" + mockApiKey +
                "&gitCommitHash=" + mockGitCommitHash +
                "&shortCommitHash=" + mockShortCommitHash +
                "&buildTime=" + mockBuildTime;

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Verify page title
        assertEquals("Callable APIs - Authenticated", driver.getTitle());

        // Verify main elements are present
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Callable APIs"));
        assertTrue(driver.findElement(By.className("status-authenticated")).isDisplayed());

        // Verify user identity is displayed
        WebElement userInfo = driver.findElement(By.className("user-info"));
        assertTrue(userInfo.getText().contains(mockUserIdentity));
    }

    @Test
    @DisplayName("API key copy functionality works")
    public void testApiKeyCopyFunctionality() {
        String mockApiKey = "test-api-key-12345";
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=" + mockApiKey;

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Find API key element
        WebElement apiKeyElement = driver.findElement(By.id("api-key"));
        assertEquals(mockApiKey, apiKeyElement.getText());

        // Find copy button
        WebElement copyButton = driver.findElement(By.className("copy-button"));
        assertTrue(copyButton.isDisplayed());
        assertEquals("Copy", copyButton.getText());

        // Test clipboard API availability
        boolean clipboardAvailable = isClipboardAPIAvailable();
        if (clipboardAvailable) {
            // Click copy button
            copyButton.click();

            // Wait for button text to change
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.textToBePresentInElement(copyButton, "Copied!"));

            // Verify button text changed
            assertEquals("Copied!", copyButton.getText());

            // Wait for button to revert
            shortWait.until(ExpectedConditions.textToBePresentInElement(copyButton, "Copy"));
            assertEquals("Copy", copyButton.getText());
        } else {
            // Clipboard API not available - skipping copy functionality test
            assertTrue(true, "Clipboard API not available - test skipped");
        }
    }

    @Test
    @DisplayName("API testing functionality works")
    public void testApiTestingFunctionality() {
        String mockApiKey = "test-api-key-12345";
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=" + mockApiKey;

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Find test buttons
        List<WebElement> testButtons = driver.findElements(By.className("test-button"));
        assertTrue(testButtons.size() >= 3, "Should have at least 3 test buttons");

        // Find test result div
        WebElement testResultDiv = driver.findElement(By.id("test-result"));
        assertFalse(testResultDiv.isDisplayed(), "Test result div should be hidden initially");

        // Test fetch API availability
        boolean fetchAvailable = isFetchAPIAvailable();
        if (fetchAvailable) {
            // Click first test button
            WebElement firstTestButton = testButtons.get(0);
            firstTestButton.click();

            // Wait for test result to appear
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOf(testResultDiv));

            // Verify test result is displayed
            assertTrue(testResultDiv.isDisplayed());
            assertTrue(testResultDiv.getText().contains("Testing...") ||
                    testResultDiv.getText().contains("Status:") ||
                    testResultDiv.getText().contains("Error:"));
        } else {
            // Fetch API not available - skipping API testing functionality test
            assertTrue(true, "Fetch API not available - test skipped");
        }
    }

    @Test
    @DisplayName("All API cards are present and functional")
    public void testApiCardsPresentAndFunctional() {
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key";

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Verify API grid is present
        WebElement apiGrid = driver.findElement(By.className("api-grid"));
        assertTrue(apiGrid.isDisplayed());

        // Verify all expected API cards are present
        String[] expectedCards = {
                "Calendar API", "User Management", "Planetary API",
                "Random API", "Inspiration API", "Health Check"
        };

        for (String cardTitle : expectedCards) {
            WebElement card = driver.findElement(By.xpath("//h3[text()='" + cardTitle + "']"));
            assertTrue(card.isDisplayed(), "API card '" + cardTitle + "' should be present");
        }

        // Verify each card has endpoints
        List<WebElement> apiCards = driver.findElements(By.className("api-card"));
        for (WebElement card : apiCards) {
            List<WebElement> endpoints = card.findElements(By.className("endpoint"));
            assertTrue(endpoints.size() > 0, "Each API card should have at least one endpoint");
        }
    }

    @Test
    @DisplayName("Version information is displayed correctly")
    public void testVersionInformationDisplay() {
        String mockGitCommitHash = "abc123def456";
        String mockShortCommitHash = "abc123";
        String mockBuildTime = "2024-01-01T00:00:00Z";
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key" +
                "&gitCommitHash=" + mockGitCommitHash +
                "&shortCommitHash=" + mockShortCommitHash +
                "&buildTime=" + mockBuildTime;

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Verify version info section is present
        WebElement versionInfo = driver.findElement(By.className("version-info"));
        assertTrue(versionInfo.isDisplayed());

        // Verify version information content
        String versionText = versionInfo.getText();
        assertTrue(versionText.contains("Deployed:"));
        assertTrue(versionText.contains(mockShortCommitHash));
        assertTrue(versionText.contains("Built:"));
        assertTrue(versionText.contains(mockBuildTime));
    }

    @Test
    @DisplayName("Navigation links work correctly")
    public void testNavigationLinks() {
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key";

        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Test "Back to Home" link
        WebElement backToHomeLink = driver.findElement(By.linkText("Back to Home"));
        assertTrue(backToHomeLink.isDisplayed());
        assertEquals("/",
                backToHomeLink.getAttribute("href").substring(backToHomeLink.getAttribute("href").lastIndexOf("/")));

        // Test "Rotate API Key" link
        WebElement rotateKeyLink = driver.findElement(By.linkText("Rotate API Key"));
        assertTrue(rotateKeyLink.isDisplayed());
        assertTrue(rotateKeyLink.getAttribute("href").contains("/api/user/key/rotate"));
        assertTrue(rotateKeyLink.getAttribute("onclick").contains("confirm"));
    }

    @Test
    @DisplayName("Page handles missing parameters gracefully")
    public void testMissingParametersHandling() {
        // Test with minimal parameters
        String minimalUrl = baseUrl + "/api/authenticated?userIdentity=testuser";

        driver.get(minimalUrl);
        waitForJavaScriptReady();

        // Page should still load without JavaScript errors
        assertTrue(driver.getTitle().contains("Callable APIs"));

        // API key section should handle missing API key gracefully
        WebElement apiKeySection = driver.findElement(By.className("api-key-section"));
        assertTrue(apiKeySection.isDisplayed());
    }

    @Test
    @DisplayName("JavaScript console has no errors")
    public void testNoJavaScriptConsoleErrors() {
        driver.get(baseUrl);
        waitForJavaScriptReady();

        // Navigate to authenticated page
        String authenticatedUrl = baseUrl + "/api/authenticated?userIdentity=testuser&apiKey=test-key";
        driver.get(authenticatedUrl);
        waitForJavaScriptReady();

        // Test some JavaScript functionality
        WebElement copyButton = driver.findElement(By.className("copy-button"));
        copyButton.click();

        // Wait a bit for any async operations
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // The tearDown method will check for JavaScript errors
        // This test passes if no errors are thrown during tearDown
    }
}
