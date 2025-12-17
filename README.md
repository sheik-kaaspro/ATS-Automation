# Readys ATS Automation Framework

A comprehensive Playwright Java automation framework for testing the Readys ATS (Applicant Tracking System) application.

## 🚀 Features

- **Playwright Java**: Modern, fast, and reliable browser automation
- **TestNG**: Flexible test framework with powerful annotations and parallel execution
- **ExtentReports**: Beautiful HTML reports with automatic browser opening
- **Page Object Model**: Maintainable and scalable test architecture
- **Configuration Driven**: All test data and settings from config.properties
- **Comprehensive Logging**: Log4j2 integration with multiple appenders
- **Screenshot Capture**: Automatic screenshots on failures and configurable success captures
- **Cross-browser Support**: Chromium, Firefox, WebKit, Chrome, and Edge
- **CI/CD Ready**: Maven integration for build pipelines

## 📁 Project Structure

```
ats-automation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/readys/ats/
│   │   │       ├── pages/          # Page Object Model classes
│   │   │       │   ├── BasePage.java
│   │   │       │   └── LoginPage.java
│   │   │       └── utils/          # Utility classes
│   │   │           ├── ConfigReader.java
│   │   │           ├── ExtentReportManager.java
│   │   │           └── PlaywrightUtils.java
│   │   └── resources/
│   │       ├── config.properties   # Configuration file
│   │       └── log4j2.xml         # Logging configuration
│   └── test/
│       ├── java/
│       │   └── com/readys/ats/
│       │       ├── base/           # Base test classes
│       │       │   ├── BaseTest.java
│       │       │   └── TestListener.java
│       │       └── tests/          # Test classes
│       │           └── LoginTests.java
│       └── resources/
│           └── testdata/           # Test data files
├── test-output/                    # Generated test artifacts
│   ├── extent-reports/            # HTML reports
│   ├── screenshots/               # Test screenshots
│   ├── videos/                    # Test recordings
│   ├── logs/                      # Application logs
│   └── downloads/                 # Downloaded files
├── testng.xml                     # TestNG suite configuration
├── pom.xml                        # Maven dependencies
└── README.md                      # This file
```

## 🛠️ Prerequisites

- **Java 21+**: Required for running the framework
- **Maven 3.6+**: For dependency management and build
- **IDE**: Eclipse, IntelliJ IDEA, or VS Code
- **Git**: For version control

## 📥 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ats-automation
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Install Playwright Browsers

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

### 4. Configure Test Data

Update `src/main/resources/config.properties` with your test credentials:

```properties
# Test Credentials - IMPORTANT: Update these with your actual test data
test.email=your.test.email@example.com
test.password=YourTestPassword123

# Invalid credentials for negative testing
invalid.email=invalid@test.com
invalid.password=WrongPassword
```

## ⚙️ Configuration

The framework is highly configurable through `src/main/resources/config.properties`:

### Application Settings
```properties
app.url=https://readys.io/login
app.name=Readys ATS
app.environment=QA
```

### Browser Configuration
```properties
browser=chromium                # Options: chromium, firefox, webkit, chrome, edge
headless=false                  # Set to true for headless execution
browser.width=1920
browser.height=1080
```

### Test Configuration
```properties
screenshot.on.failure=true     # Capture screenshots on test failures
screenshot.on.success=false    # Capture screenshots on test success
video.recording=true           # Record videos during test execution
extent.report.auto.open=true   # Automatically open HTML report after execution
```

### Timeout Settings
```properties
page.timeout=30000             # Page load timeout (ms)
element.timeout=10000          # Element wait timeout (ms)
navigation.timeout=30000       # Navigation timeout (ms)
```

## 🚀 Running Tests

### 1. Run All Tests
```bash
mvn test
```

### 2. Run Specific Test Suite
```bash
mvn test -Dtest=LoginTests
```

### 3. Run Tests by Group
```bash
mvn test -Dgroups=smoke
mvn test -Dgroups=login
mvn test -Dgroups=critical
```

### 4. Run with Different Browser
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=chrome
```

### 5. Run in Headless Mode
```bash
mvn test -Dheadless=true
```

### 6. Run with TestNG XML
```bash
mvn test -DsuiteXmlFile=testng.xml
```

## 📊 Test Reports

### ExtentReports
- **Location**: `test-output/extent-reports/`
- **Auto-open**: Configured to automatically open in default browser
- **Features**:
  - Interactive HTML reports
  - Screenshots attached to failed tests
  - Test execution timeline
  - System information
  - Detailed test logs

### Console Logs
- Real-time test execution logs in console
- Different log levels: DEBUG, INFO, WARN, ERROR

### Log Files
- **Application logs**: `test-output/logs/automation.log`
- **Error logs**: `test-output/logs/errors.log`
- **Test execution logs**: `test-output/logs/test-execution.log`

## 🧪 Test Cases

### Current Test Coverage

#### Login Tests (`LoginTests.java`)
1. **testValidLogin**: Verify successful login with valid credentials
2. **testInvalidLogin**: Verify login failure with invalid credentials
3. **testEmptyEmailLogin**: Verify validation for empty email field
4. **testEmptyPasswordLogin**: Verify validation for empty password field
5. **testEmptyFieldsLogin**: Verify validation for both empty fields
6. **testLoginPageElementsDisplay**: Verify all login page elements are displayed

### Test Groups
- **smoke**: Critical tests that must pass
- **login**: All login-related tests
- **critical**: High-priority tests
- **negative**: Negative testing scenarios
- **validation**: Input validation tests
- **ui**: User interface tests

## 🔧 Framework Components

### Page Object Model
- **BasePage**: Common functionality for all pages
- **LoginPage**: Login page specific methods and locators
- Extensible design for adding new pages

### Utilities
- **ConfigReader**: Singleton pattern for configuration management
- **ExtentReportManager**: Centralized reporting with automatic screenshot attachment
- **PlaywrightUtils**: Common Playwright operations and helper methods

### Base Classes
- **BaseTest**: Playwright setup, browser management, and common test methods
- **TestListener**: TestNG listener for automatic reporting and screenshot capture

## 🎯 Best Practices Implemented

### 1. Configuration Management
- All test data from external configuration
- No hardcoded values in test code
- Environment-specific configurations

### 2. Error Handling
- Comprehensive try-catch blocks
- Detailed error messages
- Automatic screenshot capture on failures

### 3. Reporting
- Detailed test execution reports
- Screenshots embedded in reports
- Test execution timeline and statistics

### 4. Code Organization
- Clear separation of concerns
- Reusable components
- Maintainable test structure

### 5. Logging
- Structured logging with different levels
- File and console output
- Test execution tracking

## 📝 Adding New Tests

### 1. Create a New Page Class

```java
public class NewPage extends BasePage {
    // Page locators
    private static final String ELEMENT_SELECTOR = "selector";

    public NewPage(Page page) {
        super(page);
    }

    @Override
    public void verifyPageLoaded() {
        // Page verification logic
    }

    // Page-specific methods
}
```

### 2. Create Test Class

```java
public class NewTests extends BaseTest {
    private NewPage newPage;

    @BeforeMethod
    public void setUp() {
        newPage = new NewPage(getPage());
    }

    @Test(groups = {"smoke"})
    public void testNewFeature() {
        // Test implementation
    }
}
```

### 3. Update TestNG Configuration

Add your test class to `testng.xml`:

```xml
<test name="NewFeatureTests">
    <classes>
        <class name="com.readys.ats.tests.NewTests"/>
    </classes>
</test>
```

## 🔍 Troubleshooting

### Common Issues

#### 1. Browser Installation Issues
```bash
# Reinstall browsers
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --force"
```

#### 2. Test Data Configuration
- Ensure `config.properties` contains valid test credentials
- Check URL accessibility
- Verify environment-specific settings

#### 3. Element Not Found
- Check if selectors need updating
- Verify page load timing
- Adjust timeout settings in configuration

#### 4. Report Generation Issues
- Ensure `test-output` directory has write permissions
- Check if default browser is set for auto-opening reports
- Verify ExtentReports dependencies are present

### Debug Mode
Enable debug logging by updating `log4j2.xml`:
```xml
<Logger name="com.readys.ats" level="DEBUG" additivity="false">
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Make your changes following the existing patterns
4. Add tests for new functionality
5. Update documentation as needed
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For questions, issues, or contributions:
- Create an issue in the repository
- Contact the automation team
- Refer to the Playwright documentation: https://playwright.dev/java/

## 🎉 Framework Benefits

### For Developers
- **Quick Setup**: Get started with minimal configuration
- **Readable Tests**: Clear, maintainable test code
- **Rich Debugging**: Comprehensive logging and reporting

### For QA Teams
- **Visual Reports**: Beautiful HTML reports with screenshots
- **Easy Maintenance**: Page Object Model for easy updates
- **Parallel Execution**: Fast test execution with TestNG

### For CI/CD
- **Maven Integration**: Easy build pipeline integration
- **Headless Support**: Perfect for CI environments
- **Detailed Artifacts**: Screenshots, videos, and logs for analysis

---

**Happy Testing! 🚀**

*This framework provides a solid foundation for automating the Readys ATS application with modern tools and best practices.*