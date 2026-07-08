package ru.example.otc;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OtcSearchChromeTest {

    private static final Pattern PRICE_PATTERN = Pattern.compile(
            "(?iu)(\\d[\\d\\s.,]*)\\s*(?:₽|руб\\.?)"
    );

    @BeforeAll
    static void setUp() {
        Configuration.baseUrl = "https://otc.ru";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 5_000;
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }

    @Test
    @DisplayName("Поиск принтеров в Краснодаре")
    void shouldFindProductsAndSaveToFile() throws IOException {
        open("/search/catalog/");
        $("body").shouldBe(visible);

        selectCity("Краснодар");
        searchProducts("Принтер");

        List<Product> products = collectProducts();

        assertFalse(products.isEmpty(), "Товары с ценой не найдены");

        writeProductsToFile(products);
        assertTrue(
                Files.exists(Path.of("results/products.txt")),
                "Файл с результатами не создан"
        );
    }

    private static void selectCity(String city) {
        $$("[class*='SeoRegionSelector']")
                .filterBy(visible)
                .shouldHave(sizeGreaterThan(0))
                .first()
                .click();

        SelenideElement dialog = $("[role='dialog']")
                .shouldBe(visible);

        dialog.$("input[placeholder='Найти город']")
                .shouldBe(visible)
                .shouldBe(enabled)
                .setValue(city);

        dialog.$x(".//label[contains(normalize-space(), '" + city + "')]")
                .shouldBe(visible)
                .click();

        dialog.$x(".//button[normalize-space()='Применить']")
                .shouldBe(visible)
                .shouldBe(enabled)
                .click();
    }

    private static void searchProducts(String query) {
        $("input[placeholder='Название товара']")
                .shouldBe(visible)
                .shouldBe(enabled)
                .setValue(query);

        $$x("//button[normalize-space()='Найти']")
                .filterBy(visible)
                .shouldHave(sizeGreaterThan(0))
                .first()
                .click();
    }

    private static List<Product> collectProducts() {
        List<Product> products = new ArrayList<>();
        ElementsCollection cards = $$x(
                "//*[@itemtype='http://schema.org/Product']"
        ).filterBy(visible);

        for (SelenideElement card : cards) {
            String name = card.$("[itemprop='name']").text().trim();
            BigDecimal price = parsePrice(card.text());

            if (!name.isBlank() && price != null) {
                products.add(new Product(name, price));
            }
        }

        return products;
    }

    private static BigDecimal parsePrice(String text) {
        Matcher matcher = PRICE_PATTERN.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        String price = matcher.group(1)
                .replace('\u00A0', ' ')
                .replace(" ", "")
                .replace(",", ".");

        return new BigDecimal(price);
    }

    private static void writeProductsToFile(
            List<Product> products
    ) throws IOException {
        Path file = Path.of("results/products.txt");
        Files.createDirectories(file.toAbsolutePath().getParent());

        List<String> lines = products.stream()
                .map(product ->
                        product.name() + ", " + product.price().toPlainString()
                )
                .toList();

        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    private record Product(String name, BigDecimal price) {
    }
}