package com.bhca.customer;

import com.bhca.customer.db.Customer;
import com.bhca.customer.repository.CustomerRepository;
import com.bhca.customer.service.AccountApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openapitools.client.model.AccountData;
import org.openapitools.client.model.AccountTransactionInfo;
import org.openapitools.client.model.CreateCustomerAccountRequest;
import org.openapitools.client.model.CustomerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountApiClient apiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private static class Api {

        private static final String CREATE = "/api/v1/customer";
        private static final String CUSTOMER_DATA = "/api/v1/customer/{customerId}";
    }

    @Test
    public void testZeroCreditAccount() throws Exception {
        Customer customer = new Customer();
        customer.setName("test_name");
        customer.setSurname("test_surname");
        customer.setBalance(BigDecimal.ZERO);
        customerRepository.save(customer);
        UUID customerId = customerRepository.findTopByOrderByCreatedAtDesc().getId();

        this.mockMvc.perform(post(Api.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateCustomerAccountRequest().customerId(customerId).initialCredit(BigDecimal.ZERO)))
        ).andExpect(status().isOk());

        when(apiClient.accountData(customerId, null, null))
                .thenReturn(Collections.singletonList(new AccountData().balance(BigDecimal.ZERO).number("")
                        .transactions(Collections.emptyList())));
        String response = this.mockMvc.perform(get(Api.CUSTOMER_DATA, customerId)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        CustomerData result = objectMapper.readValue(response, CustomerData.class);

        assertEquals(result.getAccounts().size(), 1);
        assertNotNull(result.getName());
        assertNotNull(result.getSurname());
        assertEquals(result.getBalance().compareTo(BigDecimal.ZERO), 0);
        assertEquals(result.getBalance().compareTo(result.getAccounts().stream().reduce(BigDecimal.ZERO, (a, b) -> a.add(b.getBalance()), BigDecimal::add)), 0);
    }

    @Test
    public void testPositiveCreditAccount() throws Exception {
        Customer customer = new Customer();
        customer.setName("test_name");
        customer.setSurname("test_surname");
        customer.setBalance(BigDecimal.ZERO);
        customerRepository.save(customer);
        UUID customerId = customerRepository.findTopByOrderByCreatedAtDesc().getId();
        BigDecimal credit = BigDecimal.TEN;
        this.mockMvc.perform(post(Api.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateCustomerAccountRequest().customerId(customerId).initialCredit(credit)))
        ).andExpect(status().isOk());

        when(apiClient.accountData(customerId, null, null))
                .thenReturn(Collections.singletonList(new AccountData().balance(credit).number("")
                        .transactions(Collections.singletonList(
                                new AccountTransactionInfo().amount(credit).createdAt(OffsetDateTime.now())))));
        String response = this.mockMvc.perform(get(Api.CUSTOMER_DATA, customerId)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        CustomerData result = objectMapper.readValue(response, CustomerData.class);

        assertEquals(result.getAccounts().size(), 1);
        assertNotNull(result.getName());
        assertNotNull(result.getSurname());
        assertEquals(result.getBalance().compareTo(credit), 0);
        assertEquals(result.getBalance().compareTo(result.getAccounts().stream().reduce(BigDecimal.ZERO, (a, b) -> a.add(b.getBalance()), BigDecimal::add)), 0);
    }

    @Test
    public void testNegativeCredit() throws Exception {
        this.mockMvc.perform(post(Api.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateCustomerAccountRequest().customerId(UUID.randomUUID()).initialCredit(new BigDecimal(-10))))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testUnknownAccountData() throws Exception {
        this.mockMvc.perform(get(Api.CUSTOMER_DATA, UUID.randomUUID())).andExpect(status().isBadRequest());
    }
}
