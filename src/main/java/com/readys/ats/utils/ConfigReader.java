package com.readys.ats.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private static final Object lock = new Object();
    private Properties properties;
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";

    private ConfigReader() {
        loadProperties();
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            // Try loading from file system first
            try (InputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
                properties.load(inputStream);
                logger.info("Configuration loaded successfully from: " + CONFIG_FILE_PATH);
            } catch (IOException e) {
                // Fallback to classpath resource
                try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                    if (inputStream != null) {
                        properties.load(inputStream);
                        logger.info("Configuration loaded successfully from classpath");
                    } else {
                        throw new RuntimeException("config.properties file not found in classpath");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration properties: " + e.getMessage(), e);
            throw new RuntimeException("Unable to load configuration properties", e);
        }
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            logger.warn("Property not found or empty: " + key);
            return "";
        }
        return value.trim();
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value.isEmpty() ? defaultValue : value;
    }

    public int getIntProperty(String key) {
        try {
            String value = getProperty(key);
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("Invalid integer format for property: " + key + ". Error: " + e.getMessage());
            return 0;
        }
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value.isEmpty() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer format for property: " + key + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value.isEmpty() ? defaultValue : Boolean.parseBoolean(value);
    }

    public long getLongProperty(String key) {
        try {
            String value = getProperty(key);
            return value.isEmpty() ? 0L : Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.error("Invalid long format for property: " + key + ". Error: " + e.getMessage());
            return 0L;
        }
    }

    public long getLongProperty(String key, long defaultValue) {
        try {
            String value = getProperty(key);
            return value.isEmpty() ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid long format for property: " + key + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    // Application Configuration Methods
    public String getAppUrl() {
        return getProperty("app.url");
    }

    public String getAppName() {
        return getProperty("app.name", "Readys ATS");
    }

    public String getAppEnvironment() {
        return getProperty("app.environment", "QA");
    }

    // Browser Configuration Methods
    public String getBrowser() {
        return getProperty("browser", "chromium");
    }

    public boolean isHeadless() {
        return getBooleanProperty("headless", false);
    }

    public int getBrowserWidth() {
        return getIntProperty("browser.width", 1920);
    }

    public int getBrowserHeight() {
        return getIntProperty("browser.height", 1080);
    }

    public int getBrowserSlowmo() {
        return getIntProperty("browser.slowmo", 0);
    }

    // Timeout Configuration Methods
    public long getPageTimeout() {
        return getLongProperty("page.timeout", 30000);
    }

    public long getElementTimeout() {
        return getLongProperty("element.timeout", 10000);
    }

    public long getNavigationTimeout() {
        return getLongProperty("navigation.timeout", 30000);
    }

    public long getDefaultTimeout() {
        return getLongProperty("default.timeout", 15000);
    }

    // Test Data Methods
    public String getTestEmail() {
        return getProperty("test.email");
    }

    public String getTestPassword() {
        return getProperty("test.password");
    }

    public String getInvalidEmail() {
        return getProperty("invalid.email");
    }

    public String getInvalidPassword() {
        return getProperty("invalid.password");
    }

    // Screenshot Configuration Methods
    public boolean isScreenshotOnFailure() {
        return getBooleanProperty("screenshot.on.failure", true);
    }

    public boolean isScreenshotOnSuccess() {
        return getBooleanProperty("screenshot.on.success", false);
    }

    public String getScreenshotPath() {
        return getProperty("screenshot.path", "test-output/screenshots/");
    }

    public String getScreenshotFormat() {
        return getProperty("screenshot.format", "png");
    }

    // Video Configuration Methods
    public boolean isVideoRecording() {
        return getBooleanProperty("video.recording", false);
    }

    public String getVideoPath() {
        return getProperty("video.path", "test-output/videos/");
    }

    public int getVideoWidth() {
        return getIntProperty("video.size.width", 1920);
    }

    public int getVideoHeight() {
        return getIntProperty("video.size.height", 1080);
    }

    // Extent Report Configuration Methods
    public String getExtentReportPath() {
        return getProperty("extent.report.path", "test-output/extent-reports/");
    }

    public String getExtentReportName() {
        return getProperty("extent.report.name", "Test-Report");
    }

    public String getExtentReportTitle() {
        return getProperty("extent.report.title", "Automation Test Report");
    }

    public boolean isExtentReportAutoOpen() {
        return getBooleanProperty("extent.report.auto.open", true);
    }

    // Wait Configuration Methods
    public int getImplicitWait() {
        return getIntProperty("implicit.wait", 10);
    }

    public int getExplicitWait() {
        return getIntProperty("explicit.wait", 15);
    }

    public int getFluentWait() {
        return getIntProperty("fluent.wait", 20);
    }

    public int getFluentPolling() {
        return getIntProperty("fluent.polling", 2);
    }

    // Test Configuration Methods
    public int getRetryFailedTests() {
        return getIntProperty("retry.failed.tests", 1);
    }

    public boolean isTestParallelMode() {
        return getBooleanProperty("test.parallel.mode", false);
    }

    public int getTestThreadCount() {
        return getIntProperty("test.thread.count", 1);
    }

    // Database Configuration Methods (if needed)
    public String getDbUrl() {
        return getProperty("db.url");
    }

    public String getDbUsername() {
        return getProperty("db.username");
    }

    public String getDbPassword() {
        return getProperty("db.password");
    }

    // API Configuration Methods (if needed)
    public String getApiBaseUrl() {
        return getProperty("api.base.url");
    }

    public long getApiTimeout() {
        return getLongProperty("api.timeout", 30000);
    }

    // File Configuration Methods
    public String getDownloadPath() {
        return getProperty("download.path", "test-output/downloads/");
    }

    public String getUploadFilesPath() {
        return getProperty("upload.files.path", "src/test/resources/testdata/files/");
    }

    // Additional Test Data Methods
    public String getCompanyName() {
        return getProperty("company.name", "Test Company");
    }

    public String getUserFirstname() {
        return getProperty("user.firstname", "Test");
    }

    public String getUserLastname() {
        return getProperty("user.lastname", "User");
    }

    public String getUserPhone() {
        return getProperty("user.phone", "+1234567890");
    }

    public String getUserDepartment() {
        return getProperty("user.department", "Engineering");
    }

    public String getUserRole() {
        return getProperty("user.role", "Administrator");
    }

    // Environment Specific URLs
    public String getStagingUrl() {
        return getProperty("staging.url");
    }

    public String getProductionUrl() {
        return getProperty("production.url");
    }

    public String getDevUrl() {
        return getProperty("dev.url");
    }

    // Utility Methods
    public void reloadProperties() {
        loadProperties();
        logger.info("Configuration properties reloaded");
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key) && !getProperty(key).isEmpty();
    }

    public void logAllProperties() {
        logger.info("Current Configuration Properties:");
        properties.entrySet().stream()
                .sorted((entry1, entry2) -> entry1.getKey().toString().compareTo(entry2.getKey().toString()))
                .forEach(entry -> {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    // Mask sensitive properties
                    if (key.toLowerCase().contains("password") || key.toLowerCase().contains("secret")) {
                        value = "*****";
                    }
                    logger.info(key + " = " + value);
                });
    }
}