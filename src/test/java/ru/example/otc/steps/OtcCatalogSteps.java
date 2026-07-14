package ru.example.otc.steps;

import ru.example.otc.model.Product;
import ru.example.otc.page.OtcCatalogPage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class OtcCatalogSteps {

    private final OtcCatalogPage catalogPage;

    public OtcCatalogSteps() {
        this(new OtcCatalogPage());
    }

    public OtcCatalogSteps(OtcCatalogPage catalogPage) {
        this.catalogPage = catalogPage;
    }

    public OtcCatalogSteps openCatalog() {
        catalogPage.openCatalogPage();

        return this;
    }

    public OtcCatalogSteps selectCity(
            String city,
            String previouslySelectedCity
    ) {
        catalogPage.selectCity(
                city,
                previouslySelectedCity
        );

        return this;
    }

    public OtcCatalogSteps searchFor(String query) {
        catalogPage.search(query);

        return this;
    }

    public List<Product> collectProductsFromFirstTwoPages() {
        List<Product> products =
                new ArrayList<>();

        List<Product> firstPageProducts =
                catalogPage.collectProductsFromCurrentPage();

        assertFalse(
                firstPageProducts.isEmpty(),
                "На первой странице " +
                        "не найдено товаров с ценой"
        );

        products.addAll(firstPageProducts);

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

        products.addAll(secondPageProducts);

        return products;
    }
}