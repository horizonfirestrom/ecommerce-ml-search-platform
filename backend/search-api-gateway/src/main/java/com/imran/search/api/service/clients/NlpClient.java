package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NlpClient {

    private final WebClient webClient;
    private final ServiceEndpoints endpoints;

    @Autowired
    public NlpClient(WebClient webClient, ServiceEndpoints endpoints) {
        this.webClient = webClient;
        this.endpoints = endpoints;
    }

    public List<Float> getEmbedding(String text) {
        Map<String, String> body = Map.of("text", text);
        Map<?, ?> resp = webClient.post()
            .uri(endpoints.getNlpEmbed())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        if (resp == null || !resp.containsKey("embedding")) {
            throw new RuntimeException("Invalid response from NLP service");
        }
        @SuppressWarnings("unchecked")
        List<Number> list = (List<Number>) resp.get("embedding");
        return list.stream().map(Number::floatValue).toList();
    }
}
