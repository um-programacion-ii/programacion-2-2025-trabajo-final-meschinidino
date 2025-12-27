package com.eventos.sistemaeventos.infrastructure.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import com.eventos.sistemaeventos.infrastructure.security.ServiceJwtProvider;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ServiceJwtProvider serviceJwtProvider) {
        return builder
                .requestFactory(this::clientHttpRequestFactory)
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(serviceJwtProvider.createToken());
                    return execution.execute(request, body);
                })
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return factory;
    }
}
