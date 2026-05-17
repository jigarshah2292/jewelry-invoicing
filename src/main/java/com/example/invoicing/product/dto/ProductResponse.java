package com.example.invoicing.product.dto;

import com.example.invoicing.product.Product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal unitPrice,
        Integer stockQuantity,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(), p.getSku(), p.getName(), p.getDescription(),
                p.getUnitPrice(), p.getStockQuantity(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
