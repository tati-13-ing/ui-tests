package ru.example.otc.service;

import ru.example.otc.model.Product;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

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

    public boolean outputFileExists() {
        return Files.exists(outputFilePath);
    }

    public List<String> readLines()
            throws IOException {
        return Files.readAllLines(
                outputFilePath,
                StandardCharsets.UTF_8
        );
    }
}