package com.example.invoicing.invoice.dto;

import com.example.invoicing.invoice.InvoiceLineItem;

import java.math.BigDecimal;

public record InvoiceLineItemResponse(
        Long id,
        Long productId,
        String productSku,
        String productName,
        String description,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
    public static InvoiceLineItemResponse from(InvoiceLineItem item) {
        return new InvoiceLineItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getSku(),
                item.getProduct().getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}
