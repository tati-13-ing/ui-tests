package ru.example.otc.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

public final class TestConfig {

    private static final String CONFIG_FILE =
            "test.properties";

    private static final Properties PROPERTIES =
            new Properties();

    static {
        loadProperties();
    }

    private TestConfig() {
    }

    public static String baseUrl() {
        return getRequiredProperty("base.url");
    }

    public static String homePath() {
        return getRequiredProperty("endpoint.home");
    }

    public static String catalogPath() {
        return getRequiredProperty("endpoint.catalog");
    }

    public static String browser() {
        return getProperty(
                "browser",
                "chrome"
        );
    }

    public static String remoteUrl() {
        return getRequiredProperty(
                "remote.url"
        );
    }

    public static boolean headless() {
        return Boolean.parseBoolean(
                getProperty(
                        "headless",
                        "false"
                )
        );
    }

    public static String browserSize() {
        return getProperty(
                "browser.size",
                "1920x1080"
        );
    }

    public static String pageLoadStrategy() {
        return getProperty(
                "page.load.strategy",
                "eager"
        );
    }

    public static long uiTimeoutMs() {
        return getLongProperty(
                "timeout.ui.ms"
        );
    }

    public static long pageLoadTimeoutMs() {
        return getLongProperty(
                "timeout.page.load.ms"
        );
    }

    public static Duration shortTimeout() {
        return Duration.ofMillis(
                getLongProperty("timeout.short.ms")
        );
    }

    public static Duration pageReadyTimeout() {
        return Duration.ofMillis(
                getLongProperty("timeout.page.ready.ms")
        );
    }

    public static String searchQuery() {
        return getRequiredProperty(
                "test.search.query"
        );
    }

    public static String searchCity() {
        return getRequiredProperty(
                "test.search.city"
        );
    }

    public static String initialCity() {
        return getRequiredProperty(
                "test.initial.city"
        );
    }

    public static String outputFile() {
        return getRequiredProperty(
                "output.file"
        );
    }

    private static void loadProperties() {
        InputStream inputStream = TestConfig.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE);

        if (inputStream == null) {
            throw new IllegalStateException(
                    "Файл src/test/resources/"
                            + CONFIG_FILE
                            + " не найден"
            );
        }

        try (Reader reader = new InputStreamReader(
                inputStream,
                StandardCharsets.UTF_8
        )) {
            PROPERTIES.load(reader);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Не удалось прочитать файл "
                            + CONFIG_FILE,
                    exception
            );
        }
    }

    private static String getRequiredProperty(
            String propertyName
    ) {
        String value = getProperty(
                propertyName,
                null
        );

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "В "
                            + CONFIG_FILE
                            + " не задан параметр: "
                            + propertyName
            );
        }

        return value;
    }

    private static String getProperty(
            String propertyName,
            String defaultValue
    ) {
        String systemProperty =
                System.getProperty(propertyName);

        if (systemProperty != null
                && !systemProperty.isBlank()) {

            return systemProperty.trim();
        }

        String fileProperty =
                PROPERTIES.getProperty(propertyName);

        if (fileProperty != null
                && !fileProperty.isBlank()) {

            return fileProperty.trim();
        }

        return defaultValue;
    }

    private static long getLongProperty(
            String propertyName
    ) {
        String value =
                getRequiredProperty(propertyName);

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Параметр "
                            + propertyName
                            + " должен быть числом, "
                            + "но получено: "
                            + value,
                    exception
            );
        }
    }
}