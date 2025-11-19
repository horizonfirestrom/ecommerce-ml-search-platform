package com.imran.search.api.service.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AbTestingClient {

    private final WebClient.Builder builder;

    @Value("${services.ab-testing}")
    private String abTestingUrl;

    public AbTestingClient(WebClient.Builder builder) {
        this.builder = builder;
    }

    public String assignBucket() {
        return builder.build()
                .get()
                .uri(abTestingUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
