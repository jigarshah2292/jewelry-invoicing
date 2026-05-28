package com.example.invoicing.invoice;

import com.example.invoicing.common.exception.BusinessRuleException;
import com.example.invoicing.common.exception.ResourceNotFoundException;
import com.example.invoicing.customer.Customer;
import com.example.invoicing.customer.CustomerService;
import com.example.invoicing.invoice.dto.InvoiceLineItemRequest;
import com.example.invoicing.invoice.dto.InvoiceRequest;
import com.example.invoicing.invoice.dto.InvoiceResponse;
import com.example.invoicing.product.Product;
import com.example.invoicing.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<InvoiceResponse> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(InvoiceResponse::from);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findById(Long id) {
        return InvoiceResponse.from(getWithDetailsOrThrow(id));
    }

    public InvoiceResponse create(InvoiceRequest req) {
        if (invoiceRepository.existsByInvoiceNumber(req.invoiceNumber())) {
            throw new BusinessRuleException("Invoice number '%s' already exists".formatted(req.invoiceNumber()));
        }

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

        syncLineItems(invoice, req.lineItems());
        recalculateTotals(invoice);

        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    public InvoiceResponse update(Long id, InvoiceRequest req) {
        Invoice invoice = getWithDetailsOrThrow(id);
        requireDraft(invoice, "modified");

        if (!invoice.getInvoiceNumber().equals(req.invoiceNumber())
                && invoiceRepository.existsByInvoiceNumberAndIdNot(req.invoiceNumber(), id)) {
            throw new BusinessRuleException("Invoice number '%s' already exists".formatted(req.invoiceNumber()));
        }

        Customer customer = customerService.getOrThrow(req.customerId());

        invoice.setInvoiceNumber(req.invoiceNumber());
        invoice.setInvoiceDate(req.invoiceDate());
        invoice.setCustomer(customer);
        invoice.setStatus(req.status() != null ? req.status() : invoice.getStatus());
        invoice.setNotes(req.notes());
        invoice.setTaxAmount(req.taxAmount());

        syncLineItems(invoice, req.lineItems());
        recalculateTotals(invoice);

        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
        requireDraft(invoice, "deleted");
        invoiceRepository.delete(invoice);
    }

    private Invoice getWithDetailsOrThrow(Long id) {
        return invoiceRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
    }

    private void requireDraft(Invoice invoice, String action) {
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BusinessRuleException(
                    "Only DRAFT invoices can be %s; invoice %d is %s"
                            .formatted(action, invoice.getId(), invoice.getStatus()));
        }
    }

    /**
     * Reconciles the invoice's line items with the requested set in place: existing rows are
     * reused positionally, surplus rows are removed (orphan removal), and only genuinely new
     * rows are inserted. This preserves line-item identities and avoids a full DELETE+INSERT
     * cycle when an invoice is edited.
     */
    private void syncLineItems(Invoice invoice, List<InvoiceLineItemRequest> items) {
        List<InvoiceLineItem> existing = invoice.getLineItems();

        while (existing.size() > items.size()) {
            existing.remove(existing.size() - 1);
        }

        for (int i = 0; i < items.size(); i++) {
            InvoiceLineItemRequest itemReq = items.get(i);
            Product product = productService.getOrThrow(itemReq.productId());
            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));

            if (i < existing.size()) {
                InvoiceLineItem item = existing.get(i);
                item.setProduct(product);
                item.setDescription(itemReq.description());
                item.setQuantity(itemReq.quantity());
                item.setUnitPrice(unitPrice);
                item.setLineTotal(lineTotal);
            } else {
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
    }

    private void recalculateTotals(Invoice invoice) {
        BigDecimal subTotal = invoice.getLineItems().stream()
                .map(InvoiceLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setSubTotal(subTotal);
        invoice.setTotalAmount(subTotal.add(invoice.getTaxAmount()));
    }
}
