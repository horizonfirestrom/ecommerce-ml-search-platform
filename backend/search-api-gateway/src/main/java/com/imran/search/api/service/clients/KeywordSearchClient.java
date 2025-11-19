package com.imran.search.api.service.clients;

import com.imran.search.api.dto.KeywordSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class KeywordSearchClient {

    private final WebClient.Builder builder;

    @Value("${services.keyword-search}")
    private String searchUrl;

    public KeywordSearchClient(WebClient.Builder builder) {
        this.builder = builder;
    }

    public List<KeywordSearchResponse> search(String query, int topK) {
        return builder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(searchUrl)
                        .queryParam("q", query)
                        .queryParam("topK", topK)
                        .build())
                .retrieve()
                .bodyToFlux(KeywordSearchResponse.class)
                .collectList()
                .block();
    }
}
