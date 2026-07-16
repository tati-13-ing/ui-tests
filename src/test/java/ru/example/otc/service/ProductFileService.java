package ru.example.otc.service;

import ru.example.otc.model.Product;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductFileService {

    private final Path outputFilePath;

    public ProductFileService(String outputFile) {
        this.outputFilePath =
                Path.of(outputFile);
    }

    public void writeProductsToFile(
            List<Product> products
    ) throws IOException {
        Path parentDirectory =
                outputFilePath
                        .toAbsolutePath()
                        .getParent();

        if (parentDirectory != null) {
            Files.createDirectories(
                    parentDirectory
            );
        }

        List<String> lines = products
                .stream()
                .map(product ->
                        product.name()
                                + ", "
                                + product.price()
                                .toPlainString()
                )
                .toList();

        Files.write(
                outputFilePath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public void checkOutputFile(
            List<Product> products
    ) throws IOException {
        assertTrue(
                Files.exists(outputFilePath),
                "Текстовый файл не создан"
        );

        List<String> actualLines =
                Files.readAllLines(
                        outputFilePath,
                        StandardCharsets.UTF_8
                );

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
}