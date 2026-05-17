package com.example.invoicing.invoice;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    @EntityGraph(attributePaths = {"customer", "lineItems", "lineItems.product"})
    Optional<Invoice> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"customer"})
    List<Invoice> findAllByCustomerId(Long customerId);
}
