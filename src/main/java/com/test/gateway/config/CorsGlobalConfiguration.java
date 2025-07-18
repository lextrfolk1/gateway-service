package com.test.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public GlobalFilter corsFilter() {
        return ((exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();
            if (path.contains("websocket-endpoint")) {
                exchange.getResponse().getHeaders().remove("Access-Control-Allow-Origin");
                exchange.getResponse().getHeaders().remove("Access-Control-Allow-Credentials");
            }

            return chain.filter(exchange);
        });
    }
}

