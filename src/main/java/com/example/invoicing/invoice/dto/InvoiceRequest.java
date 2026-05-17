package com.example.invoicing.invoice.dto;

import com.example.invoicing.invoice.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
        @NotBlank @Size(max = 50) String invoiceNumber,
        @NotNull LocalDate invoiceDate,
        @NotNull Long customerId,
        InvoiceStatus status,
        @Size(max = 1000) String notes,
        @NotNull @DecimalMin(value = "0.00", inclusive = true) BigDecimal taxAmount,
        @NotEmpty @Valid List<InvoiceLineItemRequest> lineItems
) {}
