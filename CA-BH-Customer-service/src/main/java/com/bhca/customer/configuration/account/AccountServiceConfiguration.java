package com.bhca.customer.configuration.account;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AccountServiceConfigurationProperties.class)
public class AccountServiceConfiguration {
}
