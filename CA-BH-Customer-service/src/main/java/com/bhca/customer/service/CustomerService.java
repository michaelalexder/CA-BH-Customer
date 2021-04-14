package com.bhca.customer.service;

import com.bhca.customer.db.Customer;
import com.bhca.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.openapitools.client.model.CustomerInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository repository;

    @Transactional
    public Customer getCustomer(UUID customerId) {
        return repository.findById(customerId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CustomerInfo> all() {
        return repository.findAll().stream().map(customer ->
                new CustomerInfo().id(customer.getId()).name(customer.getName()).surname(customer.getSurname()))
                .collect(Collectors.toList());
    }
}
