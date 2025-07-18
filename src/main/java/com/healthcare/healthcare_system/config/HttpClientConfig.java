package com.healthcare.healthcare_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * The type Http client config.
 */
@Configuration
public class HttpClientConfig {

    /**
     * Http client http client.
     *
     * @return the http client
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }
}