package ru.example.otc.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BrowserConfig {

    private BrowserConfig() {
    }

    public static void configure() {
        disableCdpWarning();

        Configuration.baseUrl =
                TestConfig.baseUrl();

        Configuration.browser =
                TestConfig.browser();

        Configuration.headless =
                TestConfig.headless();

        Configuration.browserSize =
                TestConfig.browserSize();

        Configuration.pageLoadStrategy =
                TestConfig.pageLoadStrategy();

        Configuration.pageLoadTimeout =
                TestConfig.pageLoadTimeoutMs();

        Configuration.timeout =
                TestConfig.uiTimeoutMs();

        Configuration.remote =
                TestConfig.remoteUrl();

        Configuration.screenshots = true;
        Configuration.savePageSource = true;

        Configuration.browserCapabilities =
                createChromeOptions();
    }



    private static ChromeOptions createChromeOptions() {
        ChromeOptions chromeOptions =
                new ChromeOptions();

        chromeOptions.addArguments(
                "--disable-geolocation",
                "--disable-notifications",
                "--disable-popup-blocking",
                "--lang=ru-RU"
        );

        Map<String, Object> chromePreferences =
                new HashMap<>();

        chromePreferences.put(
                "profile.default_content_setting_values.geolocation",
                2
        );

        chromePreferences.put(
                "profile.default_content_setting_values.notifications",
                2
        );

        chromeOptions.setExperimentalOption(
                "prefs",
                chromePreferences
        );

        return chromeOptions;
    }

    private static void disableCdpWarning() {
        Logger cdpLogger = Logger.getLogger(
                "org.openqa.selenium.devtools.CdpVersionFinder"
        );

        cdpLogger.setLevel(Level.OFF);
        cdpLogger.setUseParentHandlers(false);
    }
}