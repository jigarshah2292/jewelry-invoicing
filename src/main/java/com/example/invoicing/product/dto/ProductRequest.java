package com.example.invoicing.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 64) String sku,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 1000) String description,
        @NotNull @DecimalMin(value = "0.00", inclusive = true) BigDecimal unitPrice,
        @NotNull @Min(0) Integer stockQuantity
) {}
