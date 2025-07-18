package com.test.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Global CORS configuration filter for removing CORS headers
 * from WebSocket upgrade responses.
 */
@Configuration
public class CorsGlobalConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsGlobalConfiguration.class);


    /**
     * Regex patterns to match WebSocket endpoint paths.
     * Modify this list to support additional WebSocket routes.
     */
    private static final List<Pattern> WEBSOCKET_PATH_PATTERNS = List.of(
            Pattern.compile("^/websocket-endpoint.*$"),
            Pattern.compile("^/ws/.*$")
    );

    /**
     * Global filter that removes CORS headers for WebSocket paths.
     *
     * @return the global filter bean
     */
    @Bean
    public GlobalFilter corsFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (isWebSocketPath(path)) {
                removeCorsHeaders(exchange);
                LOGGER.debug("Removed CORS headers for WebSocket path: {}", path);
            }

            return chain.filter(exchange);
        };
    }

    /**
     * Checks if the given path matches any known WebSocket regex pattern.
     *
     * @param path request URI path
     * @return true if it matches a WebSocket pattern
     */
    private boolean isWebSocketPath(String path) {
        return WEBSOCKET_PATH_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(path).matches());
    }

    /**
     * Removes CORS-related headers from the response.
     *
     * @param exchange the current server web exchange
     */
    private void removeCorsHeaders(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
    }
}
