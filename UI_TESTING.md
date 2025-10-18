# UI Testing with Selenium WebDriver

This document describes the UI testing setup for the Callable APIs project, designed to catch JavaScript errors and UI regressions in the OIDC authentication flow.

## Overview

The UI testing framework uses Selenium WebDriver to automate browser interactions and detect JavaScript console errors. This helps identify UI regressions that might not be caught by unit tests.

## Test Structure

### Test Classes

- **`BaseUITest`**: Base class providing common WebDriver setup and utilities
- **`OIDCFlowUITest`**: Comprehensive tests for the OIDC authentication flow
- **`JavaScriptErrorDetectionTest`**: Tests specifically designed to detect JavaScript errors

### Test Categories

1. **Page Load Tests**: Verify pages load without JavaScript errors
2. **Functionality Tests**: Test JavaScript functions like copy API key and API testing
3. **Error Detection Tests**: Specifically look for JavaScript console errors
4. **Navigation Tests**: Test page navigation and link functionality

## Running UI Tests

### Prerequisites

- Docker must be running
- The application must be built successfully

### Manual Execution

1. **Using the provided script** (recommended):
   ```bash
   ./run_ui_tests.sh
   ```

2. **Manual steps**:
   ```bash
   # Build the project
   ./gradlew build -x uiTest
   
   # Start the application
   ./gradlew tomcatRun &
   
   # Wait for application to start (about 15-30 seconds)
   sleep 30
   
   # Run UI tests
   ./gradlew uiTest
   
   # Stop the application
   pkill -f tomcat
   ```

### Docker-based Execution

The UI tests run in a Docker container to ensure consistency:

```bash
# Build the validator Docker image
docker build -f Dockerfile.validator -t callableapis-validator .

# Run UI tests in Docker
docker run --rm \
  -v "$(pwd)":/workspace \
  -w /workspace \
  callableapis-validator \
  bash -c "./gradlew uiTest"
```

## Test Configuration

### Browser Support

- **Chrome** (default): Headless mode enabled
- **Firefox**: Available but not default
- **Edge**: Available but not default

### Environment Variables

- `TEST_BASE_URL`: Base URL for the application (default: http://localhost:8080)
- `TEST_BROWSER`: Browser to use (default: chrome)
- `TEST_HEADLESS`: Run in headless mode (default: true)

## JavaScript Error Detection

The tests automatically detect and report JavaScript console errors. This includes:

- **Syntax Errors**: JavaScript syntax issues
- **Runtime Errors**: Errors during execution
- **Reference Errors**: Undefined variables or functions
- **Type Errors**: Incorrect type usage

### Error Reporting

When JavaScript errors are detected, the tests will:
1. Print error details to the console
2. Fail the test with a descriptive error message
3. Include error information in test reports

## Test Scenarios

### OIDC Flow Tests

1. **Home Page Load**: Verify home page loads without errors
2. **Authenticated Page Load**: Test authenticated page with mock data
3. **API Key Copy**: Test clipboard functionality
4. **API Testing**: Test JavaScript API testing functionality
5. **Navigation**: Test page navigation and links
6. **Error Handling**: Test graceful handling of missing parameters

### JavaScript Error Detection Tests

1. **Page Load Errors**: Check for errors during page load
2. **Function Execution Errors**: Test JavaScript function execution
3. **API Availability**: Verify required JavaScript APIs are available
4. **Error Handling**: Test error handling in JavaScript functions

## Troubleshooting

### Common Issues

1. **Chrome not found**: Ensure Chrome is installed in the Docker container
2. **Application not starting**: Check if port 8080 is available
3. **Tests timing out**: Increase wait times or check application startup time
4. **JavaScript errors**: Check browser console for specific error details

### Debug Mode

To run tests in debug mode (non-headless):

```bash
# Set environment variable
export TEST_HEADLESS=false

# Run tests
./gradlew uiTest
```

### Verbose Output

To see detailed test output:

```bash
./gradlew uiTest --info
```

## Integration with CI/CD

The UI tests are integrated into the validation pipeline:

- **Local Development**: Use `./run_ui_tests.sh`
- **CI Pipeline**: Tests are available but require manual execution
- **Validation Script**: UI tests are skipped by default in `run_checks.sh`

## Best Practices

1. **Always run UI tests** before deploying changes that affect the UI
2. **Check test reports** for JavaScript errors after UI changes
3. **Update tests** when adding new UI functionality
4. **Use descriptive test names** to make failures easy to understand
5. **Keep tests focused** on specific functionality

## Adding New UI Tests

1. **Extend BaseUITest**: Use the base class for common functionality
2. **Follow naming conventions**: Use descriptive test method names
3. **Include error detection**: Always check for JavaScript errors
4. **Test edge cases**: Include tests for malformed data and error conditions
5. **Document test purpose**: Use `@DisplayName` annotations

## Example Test

```java
@Test
@DisplayName("Test specific UI functionality")
public void testSpecificFunctionality() {
    driver.get(baseUrl + "/specific-page");
    waitForJavaScriptReady();
    
    // Test functionality
    WebElement element = driver.findElement(By.id("test-element"));
    element.click();
    
    // Verify result
    assertTrue(element.isDisplayed());
    
    // JavaScript errors will be caught automatically in tearDown
}
```

## Reports

Test reports are generated in:
- `build/test-results/uiTest/`: Test execution results
- `build/reports/tests/uiTest/`: HTML test reports

## Support

For issues with UI tests:
1. Check the test reports for specific error details
2. Verify the application is running correctly
3. Check browser console for JavaScript errors
4. Review test logs for timing or configuration issues
