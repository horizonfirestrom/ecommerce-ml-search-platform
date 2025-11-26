package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AbTestingClient {

    private final WebClient webClient;
    private final ServiceEndpoints endpoints;

    @Autowired
    public AbTestingClient(WebClient webClient, ServiceEndpoints endpoints) {
        this.webClient = webClient;
        this.endpoints = endpoints;
    }

    public String assignVariant(String experimentKey, String userId) {
        Map<String, String> body = Map.of(
            "userId", userId,
            "experimentKey", experimentKey
        );
        Map<?, ?> resp = webClient.post()
            .uri(endpoints.getAbAssign())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        if (resp == null || !resp.containsKey("variant")) {
            throw new RuntimeException("Invalid response from AB-Testing assign endpoint");
        }
        return (String) resp.get("variant");
    }

    public void logEvent(String userId, String experimentKey, String variant, String eventType, Map<String, Object> metadata) {
        Map<String, Object> body = Map.of(
            "userId", userId,
            "experimentKey", experimentKey,
            "variant", variant,
            "eventType", eventType,
            "metadata", metadata
        );
        webClient.post()
            .uri(endpoints.getAbLog())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }
}
