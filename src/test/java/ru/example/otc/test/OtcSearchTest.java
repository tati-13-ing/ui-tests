package ru.example.otc.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.example.otc.config.BrowserConfig;
import ru.example.otc.config.TestConfig;
import ru.example.otc.model.Product;
import ru.example.otc.page.OtcCatalogPage;
import ru.example.otc.service.ProductFileService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OtcSearchTest {

    private final OtcCatalogPage catalogPage =
            new OtcCatalogPage();

    private final ProductFileService productFileService =
            new ProductFileService(
                    TestConfig.outputFile()
            );

    @BeforeAll
    static void setUp() {
        BrowserConfig.configure();
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }

    @Test
    @DisplayName(
            "Поиск принтеров в Краснодаре " +
                    "и сохранение результатов"
    )
    void shouldFindProductsAndSaveToFile()
            throws IOException {

        List<Product> products =
                new ArrayList<>();

        catalogPage.openCatalogPage();

        catalogPage.selectCity(
                TestConfig.searchCity(),
                TestConfig.initialCity()
        );

        catalogPage.search(
                TestConfig.searchQuery()
        );

        List<Product> firstPageProducts =
                catalogPage.collectProductsFromCurrentPage();

        assertFalse(
                firstPageProducts.isEmpty(),
                "На первой странице " +
                        "не найдено товаров с ценой"
        );

        products.addAll(
                firstPageProducts
        );

        String firstProductLink =
                catalogPage.firstProductLink();

        catalogPage.openSecondPage(
                firstProductLink
        );

        List<Product> secondPageProducts =
                catalogPage.collectProductsFromCurrentPage();

        assertFalse(
                secondPageProducts.isEmpty(),
                "На второй странице " +
                        "не найдено товаров с ценой"
        );

        products.addAll(
                secondPageProducts
        );

        productFileService.writeProductsToFile(products);

        productFileService.checkOutputFile(products);
    }
}