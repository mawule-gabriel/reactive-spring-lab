package com.mawule.employee_management_system.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void exposesConfiguredOriginsMethodsAndHeadersForApiPaths() {
        CorsConfigurationSource source = new CorsConfig(List.of("http://localhost:5173")).corsConfigurationSource();

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/departments"));
        CorsConfiguration configuration = source.getCorsConfiguration(exchange);

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins()).containsExactly("http://localhost:5173");
        assertThat(configuration.getAllowedMethods()).containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(configuration.getAllowedHeaders()).containsExactlyInAnyOrder("Content-Type", "Authorization");
    }

    @Test
    void supportsMultipleConfiguredOrigins() {
        CorsConfigurationSource source =
                new CorsConfig(List.of("http://localhost:5173", "https://app.example.com")).corsConfigurationSource();

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/employees"));
        CorsConfiguration configuration = source.getCorsConfiguration(exchange);

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins())
                .containsExactlyInAnyOrder("http://localhost:5173", "https://app.example.com");
    }
}
