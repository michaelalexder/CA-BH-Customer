package com.bhca.customer.service;

import com.bhca.common.mdc.MdcParams;
import com.bhca.customer.configuration.account.AccountServiceConfigurationProperties;
import org.openapitools.client.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class AccountApiClient {

    private final Logger logger = LoggerFactory.getLogger(AccountApiClient.class);

    private final RestTemplate restTemplate;

    private final AccountServiceConfigurationProperties props;

    public AccountApiClient(RestTemplateBuilder builder, AccountServiceConfigurationProperties props) {
        this.restTemplate = builder.build();
        this.props = props;
    }

    /**
     * Api uri's
     */
    private static class Api {

        private static final String CREATE_ACCOUNT = "/api/v1/account";
        private static final String ACCOUNTS_LIST = "/api/v1/account/%s}";
    }

    private <T> HttpEntity<T> defaultHeaders(T body, Consumer<HttpHeaders> headersConsumer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(MdcParams.REQUEST_ID, MDC.get(MdcParams.REQUEST_ID));
        if (headersConsumer != null) {
            headersConsumer.accept(headers);
        }
        return new HttpEntity<>(body, headers);
    }

    public void createAccount(UUID customerId, BigDecimal amount) {
        String url = props.host + Api.CREATE_ACCOUNT;
        withLogging(() ->
                restTemplate.postForEntity(url, defaultHeaders(new CreateAccountRequest().customerId(customerId)
                        .initialCredit(amount), null), Void.class), url);
    }

    @SuppressWarnings("unchecked")
    public List<AccountData> accountData(UUID customerId, Integer page, Integer size) {
        String url = props.host + String.format(Api.ACCOUNTS_LIST, customerId);
        return (List<AccountData>) withLogging(() ->
                restTemplate.exchange(
                        UriComponentsBuilder.fromHttpUrl(url)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .toUriString(), HttpMethod.GET, defaultHeaders("", null), List.class), url);
    }

    private <T> T withLogging(Supplier<T> func, String url) {
        try {
            return func.get();
        } catch (Throwable e) {
            logger.error(MessageFormat.format("Failed to call URL {0}", url), e);
            throw e;
        }
    }
}
