package com.sell.mcp.service;

import com.sell.mcp.model.LeadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KylasApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(KylasApiService.class);
    
    private final WebClient webClient;
    private final String kylasApiUrl;
    private final String kylasApiKey;
    
    public KylasApiService(WebClient.Builder webClientBuilder,
                          @Value("${kylas.api.url:https://api.kylas.io/v1/leads}") String kylasApiUrl,
                          @Value("${kylas.api.key:}") String kylasApiKey) {
        this.kylasApiUrl = kylasApiUrl;
        this.kylasApiKey = kylasApiKey;
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + kylasApiKey)
                .build();
    }
    
    public Mono<String> createLead(LeadRequest leadRequest) {
        logger.info("Creating lead for: {} {}", leadRequest.getFirstName(), leadRequest.getLastName());
        
        return webClient.post()
                .uri(kylasApiUrl)
                .bodyValue(leadRequest)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> logger.info("Lead created successfully: {}", response))
                .doOnError(error -> logger.error("Failed to create lead: {}", error.getMessage()))
                .onErrorResume(error -> Mono.just("Failed to create lead: " + error.getMessage()));
    }
}
