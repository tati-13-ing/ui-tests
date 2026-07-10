package ru.example.otc.model;

import java.math.BigDecimal;

public record Product(
        String name,
        BigDecimal price
) {
}