package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VectorSearchClient {

    private final WebClient webClient;
    private final ServiceEndpoints endpoints;

    @Autowired
    public VectorSearchClient(WebClient webClient, ServiceEndpoints endpoints) {
        this.webClient = webClient;
        this.endpoints = endpoints;
    }

    public List<String> search(List<Float> embedding, int topK) {
        Map<String, Object> body = Map.of(
            "embedding", embedding,
            "top_k", topK
        );
        Map<?, ?> resp = webClient.post()
            .uri(endpoints.getVectorSearch())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        if (resp == null || !resp.containsKey("productIds")) {
            throw new RuntimeException("Invalid response from vector search service");
        }
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) resp.get("productIds");
        return result;
    }
}
