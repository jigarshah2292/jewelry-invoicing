package com.example.invoicing.invoice.dto;

import com.example.invoicing.invoice.Invoice;
import com.example.invoicing.invoice.InvoiceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        LocalDate invoiceDate,
        Long customerId,
        String customerName,
        InvoiceStatus status,
        String notes,
        BigDecimal subTotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        List<InvoiceLineItemResponse> lineItems,
        Instant createdAt,
        Instant updatedAt
) {
    public static InvoiceResponse from(Invoice inv) {
        return new InvoiceResponse(
                inv.getId(),
                inv.getInvoiceNumber(),
                inv.getInvoiceDate(),
                inv.getCustomer().getId(),
                inv.getCustomer().getName(),
                inv.getStatus(),
                inv.getNotes(),
                inv.getSubTotal(),
                inv.getTaxAmount(),
                inv.getTotalAmount(),
                inv.getLineItems().stream().map(InvoiceLineItemResponse::from).toList(),
                inv.getCreatedAt(),
                inv.getUpdatedAt()
        );
    }
}
