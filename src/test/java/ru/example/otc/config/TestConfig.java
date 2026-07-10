package ru.example.otc.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class TestConfig {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private TestConfig() {
    }

    public static String baseUrl() {
        return getRequiredProperty("base.url");
    }

    public static String catalogPath() {
        return getRequiredProperty("catalog.path");
    }

    public static String browser() {
        return PROPERTIES.getProperty(
                "browser",
                "chrome"
        ).trim();
    }

    public static boolean headless() {
        String headlessValue = System.getProperty(
                "headless",
                PROPERTIES.getProperty("headless", "false")
        );

        return Boolean.parseBoolean(headlessValue);
    }

    public static String browserSize() {
        return PROPERTIES.getProperty(
                "browser.size",
                "1920x1080"
        ).trim();
    }

    public static String searchQuery() {
        return getRequiredProperty("search.query");
    }

    public static String searchCity() {
        return getRequiredProperty("search.city");
    }

    public static String outputFile() {
        return getRequiredProperty("output.file");
    }

    private static void loadProperties() {
        InputStream inputStream = TestConfig.class
                .getClassLoader()
                .getResourceAsStream("test.properties");

        if (inputStream == null) {
            throw new IllegalStateException(
                    "Файл src/test/resources/test.properties не найден"
            );
        }

        try (Reader reader = new InputStreamReader(
                inputStream,
                StandardCharsets.UTF_8
        )) {
            PROPERTIES.load(reader);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Не удалось прочитать файл test.properties",
                    exception
            );
        }
    }

    private static String getRequiredProperty(String propertyName) {
        String value = PROPERTIES.getProperty(propertyName);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "В test.properties не задан параметр: "
                            + propertyName
            );
        }

        return value.trim();
    }
}