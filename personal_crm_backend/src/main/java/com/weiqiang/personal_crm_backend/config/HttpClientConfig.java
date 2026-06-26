package com.weiqiang.personal_crm_backend.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;

import java.time.Duration;

/**
 * 全局 HTTP 客户端超时配置类（适用于 Spring Boot 3.4+ RestClient）
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> {
            ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                    .withConnectTimeout(Duration.ofSeconds(5))
                    .withReadTimeout(Duration.ofSeconds(15));
            restClientBuilder.requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings));
        };
    }
}
