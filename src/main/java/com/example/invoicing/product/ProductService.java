package com.example.invoicing.product;

import com.example.invoicing.common.exception.ResourceNotFoundException;
import com.example.invoicing.product.dto.ProductRequest;
import com.example.invoicing.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository repository;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getOrThrow(id));
    }

    public ProductResponse create(ProductRequest req) {
        Product product = Product.builder()
                .sku(req.sku())
                .name(req.name())
                .description(req.description())
                .unitPrice(req.unitPrice())
                .stockQuantity(req.stockQuantity())
                .build();
        return ProductResponse.from(repository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest req) {
        Product product = getOrThrow(id);
        product.setSku(req.sku());
        product.setName(req.name());
        product.setDescription(req.description());
        product.setUnitPrice(req.unitPrice());
        product.setStockQuantity(req.stockQuantity());
        return ProductResponse.from(repository.save(product));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        repository.deleteById(id);
    }

    public Product getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
