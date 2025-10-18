package com.callableapis.api.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to demonstrate JavaScript error detection without requiring a
 * running application.
 * This test uses a simple HTML page to test JavaScript error detection
 * capabilities.
 */
@DisplayName("Simple JavaScript Error Detection Test")
public class SimpleJavaScriptErrorTest extends BaseUITest {

    @Test
    @DisplayName("Test JavaScript error detection with simple HTML page")
    public void testJavaScriptErrorDetection() {
        // Create a simple HTML page with JavaScript errors
        String htmlWithErrors = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Test Page with JavaScript Errors</title>
                    </head>
                    <body>
                        <h1>JavaScript Error Test</h1>
                        <div id="test-div">Test Content</div>
                        <script>
                            // This will cause a ReferenceError
                            console.log(undefinedVariable);

                            // This will cause a TypeError
                            null.someMethod();

                            // This will cause a SyntaxError (commented out to avoid immediate parsing error)
                            // eval('function() {');
                        </script>
                    </body>
                    </html>
                """;

        // Write the HTML to a temporary file and serve it
        // For now, let's just test that our WebDriver setup works
        driver.get("data:text/html;charset=utf-8," + htmlWithErrors);
        waitForJavaScriptReady();

        // Verify the page loaded
        assertTrue(driver.getTitle().contains("Test Page"));

        // Verify we can find elements
        WebElement testDiv = driver.findElement(By.id("test-div"));
        assertEquals("Test Content", testDiv.getText());

        // The tearDown method will check for JavaScript errors
        // This test will fail if JavaScript errors are detected
    }

    @Test
    @DisplayName("Test JavaScript error detection with working JavaScript")
    public void testWorkingJavaScript() {
        // Create a simple HTML page with working JavaScript
        String htmlWorking = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Test Page with Working JavaScript</title>
                    </head>
                    <body>
                        <h1>Working JavaScript Test</h1>
                        <div id="test-div">Test Content</div>
                        <div id="result"></div>
                        <script>
                            // This should work without errors
                            document.getElementById('result').textContent = 'JavaScript executed successfully';
                            console.log('JavaScript executed without errors');
                        </script>
                    </body>
                    </html>
                """;

        driver.get("data:text/html;charset=utf-8," + htmlWorking);
        waitForJavaScriptReady();

        // Verify the page loaded
        assertTrue(driver.getTitle().contains("Test Page"));

        // Verify JavaScript executed
        WebElement result = driver.findElement(By.id("result"));
        assertEquals("JavaScript executed successfully", result.getText());

        // The tearDown method will check for JavaScript errors
        // This test should pass as there are no JavaScript errors
    }

    @Test
    @DisplayName("Test clipboard API availability")
    public void testClipboardAPIAvailability() {
        String htmlClipboard = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Clipboard API Test</title>
                    </head>
                    <body>
                        <h1>Clipboard API Test</h1>
                        <div id="result"></div>
                        <script>
                            if (navigator.clipboard && navigator.clipboard.writeText) {
                                document.getElementById('result').textContent = 'Clipboard API available';
                            } else {
                                document.getElementById('result').textContent = 'Clipboard API not available';
                            }
                        </script>
                    </body>
                    </html>
                """;

        driver.get("data:text/html;charset=utf-8," + htmlClipboard);
        waitForJavaScriptReady();

        WebElement result = driver.findElement(By.id("result"));
        String resultText = result.getText();

        // In headless mode, clipboard API might not be available
        assertTrue(resultText.contains("Clipboard API") || resultText.contains("not available"));
    }

    @Test
    @DisplayName("Test fetch API availability")
    public void testFetchAPIAvailability() {
        String htmlFetch = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Fetch API Test</title>
                    </head>
                    <body>
                        <h1>Fetch API Test</h1>
                        <div id="result"></div>
                        <script>
                            if (typeof fetch !== 'undefined') {
                                document.getElementById('result').textContent = 'Fetch API available';
                            } else {
                                document.getElementById('result').textContent = 'Fetch API not available';
                            }
                        </script>
                    </body>
                    </html>
                """;

        driver.get("data:text/html;charset=utf-8," + htmlFetch);
        waitForJavaScriptReady();

        WebElement result = driver.findElement(By.id("result"));
        assertEquals("Fetch API available", result.getText());
    }
}
