package com.example.financeservice.config;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignClientConfig {
    
    private static final Logger log = LoggerFactory.getLogger(FeignClientConfig.class);
    
    @Value("${project-service.auth.token:}")
    private String authToken;
    
    @Bean
    @Primary
    public RequestInterceptor authRequestInterceptor() {
        return requestTemplate -> {
            // Si vous utilisez l'authentification, décommentez la propriété dans application.properties
            // et remplacez la valeur par un token valide
            if (authToken != null && !authToken.isEmpty() && !authToken.equals("EXPIRED_TOKEN")) {
                log.debug("Adding Authorization header to request");
                requestTemplate.header("Authorization", "Bearer " + authToken);
            } else {
                log.debug("No valid authentication token found, proceeding without authentication");
            }
        };
    }
}
