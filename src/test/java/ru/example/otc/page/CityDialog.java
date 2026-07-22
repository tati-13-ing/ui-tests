package ru.example.otc.page;

import com.codeborne.selenide.SelenideElement;
import ru.example.otc.config.TestConfig;
import java.time.Duration;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CityDialog {

    private static final Logger log =
            LoggerFactory.getLogger(
                    CityDialog.class
            );

    private static final Duration SHORT_TIMEOUT =
            TestConfig.shortTimeout();

    private final SelenideElement dialog;

    public CityDialog(
            SelenideElement dialog
    ) {
        log.info(
                "Создаём Page Object диалога выбора города"
        );

        this.dialog = dialog;
    }

    public void selectOnlyCity(
            String city,
            String previouslySelectedCity
    ) {
        log.info(
                "Выбираем только город {}. " +
                        "Ранее выбранный город: {}",
                city,
                previouslySelectedCity
        );

        String expectedCity =
                normalizeCityLabel(city);

        String previousCity =
                normalizeCityLabel(
                        previouslySelectedCity
                );

        clearSearch();

        clickCityCheckbox(expectedCity);

        findCityLabel(expectedCity)
                .shouldBe(visible);

        log.info(
                "Город {} отмечен",
                expectedCity
        );

        clearSearch();

        clickCityCheckbox(previousCity);

        log.info(
                "Предыдущий город {} снят",
                previousCity
        );

        dialog.shouldHave(
                text("Выбрано: 1")
        );

        log.info(
                "В диалоге выбран только город {}",
                expectedCity
        );
    }

    public void apply() {
        log.info(
                "Применяем выбранный город"
        );

        dialog
                .$x(
                        ".//button[" +
                                "normalize-space()='Применить'" +
                                "]"
                )
                .shouldBe(visible)
                .shouldBe(enabled)
                .click();

        dialog.should(disappear);

        log.info(
                "Выбор города применён, " +
                        "диалог закрыт"
        );
    }

    private void clickCityCheckbox(
            String city
    ) {
        log.info(
                "Нажимаем checkbox города {}",
                city
        );

        SelenideElement cityLabel =
                findCityLabel(city);

        if (!cityLabel.exists()
                || !cityLabel.is(visible)) {

            log.info(
                    "Город {} не отображается, " +
                            "используем поиск",
                    city
            );

            search(city);

            cityLabel =
                    findCityLabel(city);
        }

        cityLabel.shouldBe(visible);

        clickElement(cityLabel);

        log.info(
                "Checkbox города {} нажат",
                city
        );
    }

    private void search(String city) {
        searchInput().setValue(city);
    }

    private void clearSearch() {
        searchInput().setValue("");
    }

    private SelenideElement searchInput() {
        return dialog
                .$("input[placeholder='Найти город']")
                .shouldBe(visible, SHORT_TIMEOUT)
                .shouldBe(enabled);
    }

    private SelenideElement findCityLabel(String city) {
        String cityText = normalizeCityLabel(city);

        String cityLiteral =
                xpathLiteral(cityText);

        String prefixedCityLiteral =
                xpathLiteral("г. " + cityText);

        SelenideElement exactCityLabel = dialog.$x(
                ".//label[" +
                        "normalize-space()="
                        + prefixedCityLiteral +
                        " or normalize-space()="
                        + cityLiteral +
                        "]"
        );

        if (exactCityLabel.exists()) {
            return exactCityLabel;
        }

        return dialog.$x(
                ".//label[" +
                        "contains(normalize-space(), "
                        + cityLiteral +
                        ") and " +
                        "not(contains(normalize-space(), 'край'))" +
                        "]"
        );
    }

    private void clickElement(SelenideElement element) {
        try {
            element
                    .shouldBe(visible)
                    .click();
        } catch (RuntimeException clickError) {
            com.codeborne.selenide.Selenide.executeJavaScript(
                    "arguments[0].click();",
                    element
            );
        }
    }

    private String normalizeCityLabel(String text) {
        return normalizeText(text)
                .replaceFirst("(?iu)^г\\.\\s*", "");
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String xpathLiteral(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }

        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }

        String[] parts = text.split("'");

        StringBuilder literal =
                new StringBuilder("concat(");

        for (int index = 0; index < parts.length; index++) {
            if (index > 0) {
                literal.append(", \"'\", ");
            }

            literal
                    .append("'")
                    .append(parts[index])
                    .append("'");
        }

        return literal
                .append(")")
                .toString();
    }
}