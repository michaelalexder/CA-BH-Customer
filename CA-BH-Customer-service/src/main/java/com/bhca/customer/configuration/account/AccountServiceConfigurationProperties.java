package com.bhca.customer.configuration.account;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties("service.account")
public class AccountServiceConfigurationProperties {
    public final String host;
}