package com.bhca.customer.repository;

import com.bhca.customer.db.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Customer> findById(UUID customerId);

    Customer findTopByOrderByCreatedAtDesc();
}
