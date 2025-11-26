package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class LtrClient {

    private final WebClient webClient;
    private final ServiceEndpoints endpoints;

    @Autowired
    public LtrClient(WebClient webClient, ServiceEndpoints endpoints) {
        this.webClient = webClient;
        this.endpoints = endpoints;
    }

    public Map<String, List<Float>> buildFeatures(List<String> productIds) {
        // TODO: implement your actual feature extraction logic
        // For now, return dummy or placeholder features
        return Map.of(
            "price", List.of(0.0f),  // replace with actual
            "popularity", List.of(0.0f),
            "brand_score", List.of(0.0f)
        );
    }

    public List<String> rerank(String query, List<String> productIds, Map<String, List<Float>> features) {
        Map<String, Object> body = Map.of(
            "query", query,
            "productIds", productIds,
            "features", features
        );
        Map<?, ?> resp = webClient.post()
            .uri(endpoints.getLtr())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        if (resp == null || !resp.containsKey("rerankedProductIds")) {
            throw new RuntimeException("Invalid response from LTR service");
        }
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) resp.get("rerankedProductIds");
        return result;
    }
}
