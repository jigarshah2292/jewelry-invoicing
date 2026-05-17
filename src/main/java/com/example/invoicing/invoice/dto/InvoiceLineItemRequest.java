package com.example.invoicing.invoice.dto;

import jakarta.validation.constraints.*;

public record InvoiceLineItemRequest(
        @NotNull Long productId,
        @Size(max = 500) String description,
        @NotNull @Min(1) Integer quantity
) {}
