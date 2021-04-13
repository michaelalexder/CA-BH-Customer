package com.bhca.customer.service;

import com.bhca.customer.db.Customer;
import com.bhca.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository repository;

    @Transactional
    public Customer getCustomer(UUID customerId) {
        return repository.findById(customerId).orElse(null);
    }
}
