package com.sky.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create a CloseableHttpClient bean for autowiring into HttpServiceImpl.
 */
@Configuration
public class HttpClientConfig {

    @Bean
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    public CloseableHttpClient closeableHttpClient() {
        return HttpClients.createDefault();
    }
}

