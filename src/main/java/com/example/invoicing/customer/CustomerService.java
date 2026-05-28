package com.example.invoicing.customer;

import com.example.invoicing.common.exception.ResourceNotFoundException;
import com.example.invoicing.customer.dto.CustomerRequest;
import com.example.invoicing.customer.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository repository;

    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(CustomerResponse::from);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getOrThrow(id));
    }

    public CustomerResponse create(CustomerRequest req) {
        Customer customer = Customer.builder()
                .name(req.name())
                .email(req.email())
                .phone(req.phone())
                .address(req.address())
                .build();
        return CustomerResponse.from(repository.save(customer));
    }

    public CustomerResponse update(Long id, CustomerRequest req) {
        Customer customer = getOrThrow(id);
        customer.setName(req.name());
        customer.setEmail(req.email());
        customer.setPhone(req.phone());
        customer.setAddress(req.address());
        return CustomerResponse.from(repository.save(customer));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", id);
        }
        repository.deleteById(id);
    }

    public Customer getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }
}
