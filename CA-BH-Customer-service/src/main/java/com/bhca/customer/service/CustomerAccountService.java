package com.bhca.customer.service;

import com.bhca.customer.configuration.cache.CachesConfig;
import com.bhca.customer.db.Customer;
import lombok.AllArgsConstructor;
import org.openapitools.client.model.AccountData;
import org.openapitools.client.model.CustomerAccountData;
import org.openapitools.client.model.CustomerData;
import org.openapitools.client.model.TransactionInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Combines customer logic with account
 */
@AllArgsConstructor
@Service
public class CustomerAccountService {

    private final CustomerService customerService;
    private final AccountApiClient accountApiClient;

    @CacheEvict(value = CachesConfig.CUSTOMER_CACHE, key = "#customerId", beforeInvocation = true)
    @Transactional
    public void createAccountWithTransaction(UUID customerId, BigDecimal initialCredit) {
        if (initialCredit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial credit must be greater or equals to zero");
        }
        Customer customer = customerService.getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("No customer with id " + customerId + " exists");
        }
        accountApiClient.createAccount(customerId, initialCredit);
        customer.setBalance(customer.getBalance().add(initialCredit));
    }

    @Cacheable(value = CachesConfig.CUSTOMER_CACHE, key = "#customerId")
    @Transactional(readOnly = true)
    public CustomerData accountsWithTransactions(UUID customerId, Integer page, Integer size) {
        Customer customer = customerService.getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("No customer with id " + customerId + " exists");
        }
        return new CustomerData().name(customer.getName()).surname(customer.getSurname()).balance(customer.getBalance())
                .accounts(accountApiClient.accountData(customerId, page, size).stream().map(accountData ->
                        new CustomerAccountData().number(accountData.getNumber()).balance(accountData.getBalance())
                                .transactions(accountData.getTransactions().stream().map(transactionInfo ->
                                        new TransactionInfo().amount(transactionInfo.getAmount()).createdAt(transactionInfo.getCreatedAt())
                                ).collect(Collectors.toList()))
                ).collect(Collectors.toList()));
    }
}
