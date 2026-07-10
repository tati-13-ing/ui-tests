package ru.example.otc.page;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class CityDialog {

    private static final Duration SHORT_TIMEOUT =
            Duration.ofSeconds(5);

    private final SelenideElement dialog;

    public CityDialog(SelenideElement dialog) {
        this.dialog = dialog;
    }

    public void selectOnlyCity(String city) {
        String expectedCity = normalizeCityLabel(city);

        clearSearch();
        clickCityCheckbox(expectedCity);

        findCityLabel(expectedCity)
                .shouldBe(visible);

        clearSearch();
        clickCityCheckbox("Москва");

        dialog.shouldHave(text("Выбрано: 1"));
    }

    public void apply() {
        dialog
                .$x(".//button[normalize-space()='Применить']")
                .shouldBe(visible)
                .shouldBe(enabled)
                .click();

        dialog.should(disappear);
    }

    private void clickCityCheckbox(String city) {
        SelenideElement cityLabel = findCityLabel(city);

        if (!cityLabel.exists() || !cityLabel.is(visible)) {
            search(city);
            cityLabel = findCityLabel(city);
        }

        cityLabel.shouldBe(visible);
        clickElement(cityLabel);
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