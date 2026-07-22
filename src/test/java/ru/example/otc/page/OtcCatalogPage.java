package ru.example.otc.page;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.example.otc.config.TestConfig;
import ru.example.otc.model.Product;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URI;
import java.util.Arrays;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtcCatalogPage {

    private static final Logger log =
            LoggerFactory.getLogger(
                    OtcCatalogPage.class
            );

    private static final Duration SHORT_TIMEOUT =
            TestConfig.shortTimeout();

    private static final Duration PAGE_READY_TIMEOUT =
            TestConfig.pageReadyTimeout();

    private static final Pattern PRICE_PATTERN =
            Pattern.compile(
                    "(?iu)(\\d[\\d\\s.,]*)\\s*(?:₽|руб\\.?)"
            );

    public void openCatalogPage() {
        log.info(
                "Открываем страницу каталога OTC"
        );

        open(TestConfig.homePath());
        $("body").shouldBe(visible);

        log.info(
                "Главная страница OTC открыта, " +
                        "переходим в каталог"
        );

        open(TestConfig.catalogPath());

        $("body").shouldBe(visible);

        waitPageLoadingFinished();

        findSearchInput()
                .shouldBe(visible)
                .shouldBe(enabled);

        findSearchButton()
                .shouldBe(visible)
                .shouldBe(enabled);

        webdriver().shouldHave(
                urlContaining(
                        TestConfig.catalogPath()
                )
        );

        log.info(
                "Страница каталога OTC успешно открыта"
        );
    }

    public void selectCity(
            String city,
            String previouslySelectedCity
    ) {
        log.info(
                "Начинаем выбор города. " +
                        "Требуемый город: {}, " +
                        "предыдущий город: {}",
                city,
                previouslySelectedCity
        );

        String expectedCity = normalizeText(city);

        SelenideElement citySelector =
                findCitySelector();

        String currentCity =
                normalizeText(citySelector.text());

        log.info(
                "Текущий город на странице: {}",
                currentCity
        );

        if (currentCity.contains(expectedCity)) {
            log.info(
                    "Город {} уже выбран, " +
                            "повторный выбор не требуется",
                    city
            );

            return;
        }

        SelenideElement cityDialogElement =
                openCityDialog(citySelector);

        CityDialog cityDialog =
                new CityDialog(cityDialogElement);

        cityDialog.selectOnlyCity(
                city,
                previouslySelectedCity
        );

        cityDialog.apply();

        findCitySelector()
                .shouldHave(text(expectedCity));

        log.info(
                "Город {} успешно выбран",
                city
        );
    }

    public void search(String query) {
        log.info(
                "Начинаем поиск по запросу: {}",
                query
        );

        enterSearchQuery(query);
        submitSearch();

        log.info(
                "Поиск по запросу {} выполнен",
                query
        );
    }

    public List<Product> collectProductsFromCurrentPage() {
        log.info(
                "Начинаем сбор товаров " +
                        "с текущей страницы"
        );

        List<Product> products =
                new ArrayList<>();

        ElementsCollection productCards =
                getProductCards();

        log.info(
                "На странице обнаружено карточек: {}",
                productCards.size()
        );

        for (int index = 0;
             index < productCards.size();
             index++) {

            SelenideElement productCard =
                    productCards.get(index);

            String name =
                    productName(productCard);

            BigDecimal price =
                    productPrice(productCard);

            if (name.isBlank() || price == null) {
                log.info(
                        "Карточка с индексом {} пропущена: " +
                                "не найдено название или цена",
                        index
                );

                continue;
            }

            Product product = new Product(
                    name,
                    price.stripTrailingZeros()
            );

            if (!products.contains(product)) {
                products.add(product);

                log.info(
                        "Добавлен товар: {}, цена: {}",
                        product.name(),
                        product.price()
                );
            }
        }

        log.info(
                "С текущей страницы собрано товаров: {}",
                products.size()
        );

        return products;
    }


    public void openSecondPage() {
        log.info(
                "Переходим на вторую страницу результатов"
        );

        SelenideElement secondPageLink =
                $$("a[href*='page=2']")
                        .filterBy(visible)
                        .shouldHave(sizeGreaterThan(0))
                        .first();

        clickElement(
                secondPageLink.scrollIntoView(true)
        );

        webdriver().shouldHave(
                urlContaining("page=2")
        );

        String currentUrl =
                WebDriverRunner.url();

        log.info(
                "URL после перехода: {}",
                currentUrl
        );

        assertEquals(
                "2",
                getQueryParameterValue(
                        currentUrl,
                        "page"
                ),
                "После перехода должна быть открыта " +
                        "вторая страница. Текущий URL: " +
                        currentUrl
        );

        log.info(
                "Вторая страница результатов открыта"
        );
    }

    private String getQueryParameterValue(
            String url,
            String parameterName
    ) {
        log.info(
                "Получаем параметр {} из URL",
                parameterName
        );

        String query =
                URI.create(url).getRawQuery();

        if (query == null || query.isBlank()) {
            log.info(
                    "В URL отсутствуют query-параметры"
            );

            return null;
        }

        String parameterPrefix =
                parameterName + "=";

        String value = Arrays.stream(
                        query.split("&")
                )
                .filter(parameter ->
                        parameter.startsWith(
                                parameterPrefix
                        )
                )
                .map(parameter ->
                        parameter.substring(
                                parameterPrefix.length()
                        )
                )
                .findFirst()
                .orElse(null);

        log.info(
                "Значение параметра {}: {}",
                parameterName,
                value
        );

        return value;
    }

    private void enterSearchQuery(String query) {
        findSearchInput()
                .shouldBe(visible)
                .shouldBe(enabled)
                .setValue(query)
                .pressEscape();
    }

    private void submitSearch() {
        findSearchButton()
                .shouldBe(visible)
                .shouldBe(enabled)
                .click();

        $("body")
                .shouldHave(text("Найдено товаров"))
                .shouldHave(
                        text(TestConfig.searchCity())
                );

        getProductLinks()
                .shouldHave(sizeGreaterThan(0));
    }

    private SelenideElement findSearchInput() {
        SelenideElement inputByPlaceholder =
                $("input[placeholder='Название товара']");

        if (inputByPlaceholder.exists()) {
            return inputByPlaceholder;
        }

        return findSearchButton()
                .$x(
                        "preceding::input[" +
                                "not(@type='checkbox')" +
                                "][1]"
                );
    }

    private SelenideElement findSearchButton() {
        return $$x(
                "//button[normalize-space()='Найти']"
        )
                .filterBy(visible)
                .shouldHave(sizeGreaterThan(0))
                .first();
    }

    private SelenideElement findCitySelector() {
        ElementsCollection cityFields = $$(
                "[class*='SeoRegionSelector']" +
                        "[class*='selector']"
        ).filterBy(visible);

        if (!cityFields.isEmpty()) {
            return cityFields
                    .first()
                    .shouldBe(visible);
        }

        SelenideElement searchButton =
                findSearchButton();

        SelenideElement regionTarget =
                searchButton.$x(
                        "preceding-sibling::*[" +
                                "@aria-haspopup='dialog' and " +
                                ".//*[" +
                                "contains(" +
                                "@class," +
                                "'SeoRegionSelector'" +
                                ")" +
                                "]" +
                                "][1]"
                );

        if (regionTarget.exists()) {
            return regionTarget
                    .shouldBe(visible);
        }

        SelenideElement seoRegionSelector =
                searchButton.$x(
                        "preceding::*[" +
                                "contains(" +
                                "@class," +
                                "'SeoRegionSelector'" +
                                ")" +
                                "][1]"
                );

        if (seoRegionSelector.exists()) {
            SelenideElement dialogTarget =
                    seoRegionSelector.$x(
                            "ancestor::*[" +
                                    "@aria-haspopup='dialog'" +
                                    "][1]"
                    );

            if (dialogTarget.exists()) {
                return dialogTarget.shouldBe(visible);
            }

            return seoRegionSelector.shouldBe(visible);
        }

        return searchButton
                .$x(
                        "preceding::*[" +
                                "contains(@class,'Region')" +
                                "][1]"
                )
                .shouldBe(visible);
    }

    private SelenideElement openCityDialog(
            SelenideElement citySelector
    ) {
        AssertionError lastDialogError = null;

        SelenideElement currentCitySelector =
                citySelector;

        for (int attempt = 1;
             attempt <= 2;
             attempt++) {

            if (attempt > 1) {
                open(TestConfig.catalogPath());

                $("body").shouldBe(visible);

                waitPageLoadingFinished();

                findSearchInput()
                        .shouldBe(visible)
                        .shouldBe(enabled);

                findSearchButton()
                        .shouldBe(visible)
                        .shouldBe(enabled);

                currentCitySelector =
                        findCitySelector();
            }

            if (!clickChooseAnotherCityIfPresent()) {
                waitPageLoadingFinished();

                clickElement(currentCitySelector);

                clickChooseAnotherCityIfPresent();
            }

            try {
                return findCityDialog();
            } catch (AssertionError error) {
                lastDialogError = error;
            }
        }

        if (lastDialogError != null) {
            throw lastDialogError;
        }

        throw new AssertionError(
                "Не удалось открыть окно выбора города"
        );
    }

    private boolean clickChooseAnotherCityIfPresent() {
        Boolean clicked = executeJavaScript(
                """
                const dialogs = Array.from(
                    document.querySelectorAll('[role="dialog"]')
                );

                const confirmationDialog = dialogs.find(dialog =>
                    dialog.offsetParent !== null &&
                    dialog.textContent.includes('Ваш город')
                );

                if (!confirmationDialog) {
                    return false;
                }

                const controls = Array.from(
                    confirmationDialog.querySelectorAll(
                        'button, a, [role="button"]'
                    )
                );

                const chooseAnotherButton = controls.find(control =>
                    control.textContent.trim() === 'Выбрать другой'
                );

                if (!chooseAnotherButton) {
                    return false;
                }

                chooseAnotherButton.click();

                return true;
                """
        );

        return Boolean.TRUE.equals(clicked);
    }

    private SelenideElement findCityDialog() {
        SelenideElement cityDialog =
                $$("[role='dialog']")
                        .filterBy(visible)
                        .shouldHave(
                                sizeGreaterThan(0),
                                SHORT_TIMEOUT
                        )
                        .first();

        waitCityDialogLoadingFinished(cityDialog);

        try {
            cityDialog
                    .$x(
                            ".//*[" +
                                    "normalize-space()=" +
                                    "'Уточните ваш город'" +
                                    "]"
                    )
                    .shouldBe(
                            visible,
                            SHORT_TIMEOUT
                    );
        } catch (AssertionError error) {
            throw new AssertionError(
                    "Открылась модалка выбора города, " +
                            "но ее содержимое не загрузилось. " +
                            "Текст модалки: [" +
                            shortText(cityDialog.text()) +
                            "]. HTML: [" +
                            shortText(
                                    cityDialog.getAttribute(
                                            "innerHTML"
                                    )
                            ) +
                            "]",
                    error
            );
        }

        cityDialog
                .$("input[placeholder='Найти город']")
                .shouldBe(
                        visible,
                        SHORT_TIMEOUT
                )
                .shouldBe(enabled);

        cityDialog
                .$x(
                        ".//*[" +
                                "normalize-space()=" +
                                "'Популярные города'" +
                                "]"
                )
                .shouldBe(
                        visible,
                        SHORT_TIMEOUT
                );

        cityDialog
                .$x(
                        ".//label[" +
                                "normalize-space()='г. Москва' " +
                                "or normalize-space()='Москва'" +
                                "]"
                )
                .shouldBe(
                        visible,
                        SHORT_TIMEOUT
                );

        return cityDialog;
    }

    private void waitCityDialogLoadingFinished(
            SelenideElement cityDialog
    ) {
        try {
            cityDialog
                    .$("[class*='LoadingOverlay']")
                    .should(
                            disappear,
                            SHORT_TIMEOUT
                    );
        } catch (AssertionError error) {
            throw new AssertionError(
                    "Модалка выбора города открылась, " +
                            "но лоадер не исчез за " +
                            SHORT_TIMEOUT.toSeconds() +
                            " секунд. Текст модалки: [" +
                            shortText(cityDialog.text()) +
                            "]. HTML: [" +
                            shortText(
                                    cityDialog.getAttribute(
                                            "innerHTML"
                                    )
                            ) +
                            "]",
                    error
            );
        }
    }

    private void waitPageLoadingFinished() {
        $("body").shouldBe(
                visible,
                PAGE_READY_TIMEOUT
        );

        executeJavaScript(
                """
                return new Promise(resolve => {
                    if (document.readyState !== 'loading') {
                        resolve(true);
                        return;
                    }

                    document.addEventListener(
                        'DOMContentLoaded',
                        () => resolve(true),
                        { once: true }
                    );
                });
                """
        );

        $$("[class*='LoadingOverlay']")
                .filterBy(visible)
                .shouldHave(
                        size(0),
                        PAGE_READY_TIMEOUT
                );
    }

    private ElementsCollection getProductCards() {
        ElementsCollection productCards =
                $$x(
                        "//*[" +
                                "@itemtype=" +
                                "'http://schema.org/Product'" +
                                "]"
                ).filterBy(visible);

        if (!productCards.isEmpty()) {
            return productCards;
        }

        return $$("a[href*='/product/']")
                .filterBy(visible)
                .shouldHave(sizeGreaterThan(0));
    }

    private String productName(
            SelenideElement productCard
    ) {
        SelenideElement namedElement =
                productCard.$("[itemprop='name']");

        if (namedElement.exists()
                && namedElement.is(visible)) {
            return normalizeText(
                    namedElement.text()
            );
        }

        ElementsCollection productLinks =
                productCard
                        .$$("a[href*='/product/']")
                        .filterBy(visible);

        for (int index = 0;
             index < productLinks.size();
             index++) {

            String productName = normalizeText(
                    productLinks
                            .get(index)
                            .text()
            );

            if (!productName.isBlank()) {
                return productName;
            }
        }

        return "";
    }

    private BigDecimal productPrice(
            SelenideElement productCard
    ) {
        String normalizedCardText =
                normalizeText(productCard.text());

        for (String line :
                normalizedCardText.split("\\R|  ")) {

            BigDecimal price =
                    parsePrice(line);

            if (price != null) {
                return price;
            }
        }

        return parsePrice(productCard.text());
    }

    private ElementsCollection getProductLinks() {
        return $$("a[href*='/product/']")
                .filterBy(visible)
                .shouldHave(sizeGreaterThan(0));
    }

    private BigDecimal parsePrice(String text) {
        Matcher matcher = PRICE_PATTERN.matcher(
                normalizeText(text)
        );

        if (!matcher.find()) {
            return null;
        }

        String normalizedPrice =
                matcher.group(1)
                        .replace('\u00A0', ' ')
                        .replace(" ", "")
                        .replace(",", ".");

        try {
            return new BigDecimal(
                    normalizedPrice
            );
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void clickElement(
            SelenideElement element
    ) {
        log.info(
                "Выполняем нажатие на элемент"
        );

        try {
            element
                    .shouldBe(visible)
                    .click();

            log.info(
                    "Элемент нажат обычным способом"
            );
        } catch (RuntimeException clickError) {
            log.info(
                    "Обычное нажатие не сработало, " +
                            "используем JavaScript click"
            );

            executeJavaScript(
                    "arguments[0].click();",
                    element
            );

            log.info(
                    "Элемент нажат через JavaScript"
            );
        }
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

    private String shortText(String text) {
        String normalizedText =
                normalizeText(text);

        if (normalizedText.length() <= 500) {
            return normalizedText;
        }

        return normalizedText.substring(
                0,
                500
        ) + "...";
    }
}