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
import ru.example.otc.config.MinioClientConfig;
import ru.example.otc.service.ProductS3StorageService;

import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OtcSearchTest {

    private final OtcCatalogSteps catalogSteps =
            new OtcCatalogSteps();

    private final ProductFileService productFileService =
            new ProductFileService(
                    TestConfig.outputFile()
            );
    private final ProductS3StorageService
            productS3StorageService =
            new ProductS3StorageService(
                    MinioClientConfig.createClient(),
                    TestConfig.s3Bucket(),
                    TestConfig.s3ObjectName()
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
            "Поиск принтеров в Краснодаре, " +
                    "сохранение файла и загрузка в S3"
    )
    void shouldFindProductsAndSaveToFile()
            throws Exception {

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

        productFileService.writeProductsToFile(
                products
        );

        assertOutputFileContains(
                products
        );

        productS3StorageService.uploadFile(
                Path.of(
                        TestConfig.outputFile()
                )
        );

        assertS3ObjectContains(
                products
        );
    }

    private void assertOutputFileContains(
            List<Product> products
    ) throws IOException {
        assertTrue(
                productFileService.outputFileExists(),
                "Текстовый файл не создан"
        );

        List<String> actualLines =
                productFileService.readLines();

        assertFalse(
                actualLines.isEmpty(),
                "Файл с товарами пустой"
        );

        List<String> expectedLines = products
                .stream()
                .map(product ->
                        product.name()
                                + ", "
                                + product.price()
                                .toPlainString()
                )
                .toList();

        assertEquals(
                expectedLines,
                actualLines,
                "Содержимое файла не соответствует найденным товарам"
        );
    }
    private void assertS3ObjectContains(
            List<Product> products
    ) throws Exception {
        assertTrue(
                productS3StorageService.objectExists(),
                "Файл не загружен в MinIO"
        );

        List<String> actualLines =
                productS3StorageService.readLines();

        assertFalse(
                actualLines.isEmpty(),
                "Объект в MinIO пустой"
        );

        List<String> expectedLines = products
                .stream()
                .map(product ->
                        product.name()
                                + ", "
                                + product.price()
                                .toPlainString()
                )
                .toList();

        assertEquals(
                expectedLines,
                actualLines,
                "Содержимое объекта в MinIO " +
                        "не соответствует найденным товарам"
        );
    }
}