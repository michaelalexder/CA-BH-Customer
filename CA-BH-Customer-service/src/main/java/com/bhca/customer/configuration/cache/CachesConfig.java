package com.bhca.customer.configuration.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachesConfig {

    public static final String CUSTOMER_CACHE = "customer";
}