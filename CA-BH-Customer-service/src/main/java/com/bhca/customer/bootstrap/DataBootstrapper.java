package com.bhca.customer.bootstrap;

import com.bhca.customer.db.Customer;
import com.bhca.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@AllArgsConstructor
@Component
public class DataBootstrapper implements CommandLineRunner {

    private final CustomerRepository repository;

    @Override
    public void run(String... args) throws Exception {
        createCustomer("Philip", "Fry");
        createCustomer("Bender", "Rodríguez");
        createCustomer("John", "Zoidberg");
        createCustomer("Amy", "Wong");
    }

    private void createCustomer(String name, String surname) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setSurname(surname);
        customer.setBalance(BigDecimal.ZERO);
        repository.save(customer);
    }
}
