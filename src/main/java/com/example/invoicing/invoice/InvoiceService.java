package com.example.invoicing.invoice;

import com.example.invoicing.common.exception.ResourceNotFoundException;
import com.example.invoicing.customer.Customer;
import com.example.invoicing.customer.CustomerService;
import com.example.invoicing.invoice.dto.InvoiceLineItemRequest;
import com.example.invoicing.invoice.dto.InvoiceRequest;
import com.example.invoicing.invoice.dto.InvoiceResponse;
import com.example.invoicing.product.Product;
import com.example.invoicing.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAll().stream().map(InvoiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findById(Long id) {
        Invoice invoice = invoiceRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
        return InvoiceResponse.from(invoice);
    }

    public InvoiceResponse create(InvoiceRequest req) {
        Customer customer = customerService.getOrThrow(req.customerId());

        Invoice invoice = Invoice.builder()
                .invoiceNumber(req.invoiceNumber())
                .invoiceDate(req.invoiceDate())
                .customer(customer)
                .status(req.status() != null ? req.status() : InvoiceStatus.DRAFT)
                .notes(req.notes())
                .taxAmount(req.taxAmount())
                .subTotal(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();

        attachLineItems(invoice, req.lineItems());
        recalculateTotals(invoice);

        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    public InvoiceResponse update(Long id, InvoiceRequest req) {
        Invoice invoice = invoiceRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));

        Customer customer = customerService.getOrThrow(req.customerId());

        invoice.setInvoiceNumber(req.invoiceNumber());
        invoice.setInvoiceDate(req.invoiceDate());
        invoice.setCustomer(customer);
        invoice.setStatus(req.status() != null ? req.status() : invoice.getStatus());
        invoice.setNotes(req.notes());
        invoice.setTaxAmount(req.taxAmount());

        invoice.clearLineItems();
        attachLineItems(invoice, req.lineItems());
        recalculateTotals(invoice);

        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    public void delete(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice", id);
        }
        invoiceRepository.deleteById(id);
    }

    private void attachLineItems(Invoice invoice, List<InvoiceLineItemRequest> items) {
        for (InvoiceLineItemRequest itemReq : items) {
            Product product = productService.getOrThrow(itemReq.productId());
            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));

            InvoiceLineItem item = InvoiceLineItem.builder()
                    .product(product)
                    .description(itemReq.description())
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build();
            invoice.addLineItem(item);
        }
    }

    private void recalculateTotals(Invoice invoice) {
        BigDecimal subTotal = invoice.getLineItems().stream()
                .map(InvoiceLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setSubTotal(subTotal);
        invoice.setTotalAmount(subTotal.add(invoice.getTaxAmount()));
    }
}
