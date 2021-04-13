package com.bhca.customer.controller;

import com.bhca.customer.service.CustomerAccountService;
import lombok.AllArgsConstructor;
import org.openapitools.client.model.CreateCustomerAccountRequest;
import org.openapitools.client.model.CustomerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/api/v1/customer")
@RestController
public class CustomerController {

    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerAccountService accountTransactionService;

    @PostMapping
    public void create(@RequestBody CreateCustomerAccountRequest request) {
        logger.debug("New customer account creation is called for customer {}", request.getCustomerId());
        accountTransactionService.createAccountWithTransaction(request.getCustomerId(), request.getInitialCredit());
        logger.debug("New customer account created");
    }

    @GetMapping("/{customerId}")
    public CustomerData customerAccounts(@PathVariable UUID customerId, @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size) {
        logger.debug("Retrieving accounts for customer {}", customerId);
        return accountTransactionService.accountsWithTransactions(customerId, page, size);
    }
}
