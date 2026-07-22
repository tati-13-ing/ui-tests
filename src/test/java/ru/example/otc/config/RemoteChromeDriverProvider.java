package ru.example.otc.config;

import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class RemoteChromeDriverProvider
        implements WebDriverProvider {

    private static final Logger log =
            LoggerFactory.getLogger(
                    RemoteChromeDriverProvider.class
            );

    @Override
    public WebDriver createDriver(
            Capabilities capabilities
    ) {
        String remoteUrl =
                TestConfig.remoteUrl();

        log.info(
                "Создаём удалённый браузер. " +
                        "Адрес Selenium Grid: {}",
                remoteUrl
        );

        ChromeOptions chromeOptions =
                createChromeOptions(capabilities);

        try {
            URL gridUrl =
                    URI.create(remoteUrl).toURL();

            RemoteWebDriver driver =
                    new RemoteWebDriver(
                            gridUrl,
                            chromeOptions
                    );

            log.info(
                    "RemoteWebDriver успешно создан. " +
                            "Session ID: {}",
                    driver.getSessionId()
            );

            return driver;
        } catch (MalformedURLException
                 | IllegalArgumentException exception) {

            log.error(
                    "Не удалось создать RemoteWebDriver. " +
                            "Некорректный адрес Grid: {}",
                    remoteUrl,
                    exception
            );

            throw new IllegalStateException(
                    "Некорректный адрес Selenium Grid: "
                            + remoteUrl,
                    exception
            );
        }
    }

    private ChromeOptions createChromeOptions(
            Capabilities capabilities
    ) {
        log.info(
                "Формируем настройки удалённого Chrome"
        );

        ChromeOptions chromeOptions =
                new ChromeOptions();

        chromeOptions.merge(capabilities);

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

        log.info(
                "Настройки удалённого Chrome сформированы"
        );

        return chromeOptions;
    }
}