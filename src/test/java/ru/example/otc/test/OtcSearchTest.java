package ru.example.otc.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.example.otc.config.BrowserConfig;
import ru.example.otc.config.TestConfig;
import ru.example.otc.model.Product;
import ru.example.otc.service.ProductFileService;
import ru.example.otc.steps.OtcCatalogSteps;

import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.closeWebDriver;

class OtcSearchTest {

    private final OtcCatalogSteps catalogSteps =
            new OtcCatalogSteps();

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

        List<Product> products = catalogSteps
                .openCatalog()
                .selectCity(
                        TestConfig.searchCity(),
                        TestConfig.initialCity()
                )
                .searchFor(
                        TestConfig.searchQuery()
                )
                .collectProductsFromFirstTwoPages();

        productFileService.writeProductsToFile(products);

        productFileService.checkOutputFile(products);
    }
}