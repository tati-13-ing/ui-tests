package ru.example.otc.config;

import com.codeborne.selenide.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public final class BrowserConfig {

    private static final Logger log =
            LoggerFactory.getLogger(
                    BrowserConfig.class
            );

    private BrowserConfig() {
    }

    public static void configure() {
        log.info(
                "Начинаем настройку Selenide"
        );

        disableCdpWarning();

        Configuration.baseUrl =
                TestConfig.baseUrl();
        Configuration.browser =
                RemoteChromeDriverProvider
                        .class
                        .getName();

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

        Configuration.screenshots = true;
        Configuration.savePageSource = true;

        log.info(
                "Selenide настроен. " +
                        "Base URL: {}, Grid: {}, " +
                        "размер браузера: {}",
                TestConfig.baseUrl(),
                TestConfig.remoteUrl(),
                TestConfig.browserSize()
        );
    }

    private static void disableCdpWarning() {
        log.info(
                "Отключаем предупреждение Selenium CDP"
        );

        java.util.logging.Logger cdpLogger =
                java.util.logging.Logger.getLogger(
                        "org.openqa.selenium.devtools." +
                                "CdpVersionFinder"
                );

        cdpLogger.setLevel(Level.OFF);
        cdpLogger.setUseParentHandlers(false);

        log.info(
                "Предупреждение Selenium CDP отключено"
        );
    }
}