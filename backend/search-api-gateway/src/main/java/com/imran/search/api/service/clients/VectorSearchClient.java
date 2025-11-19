package com.imran.search.api.service.clients;

import com.imran.search.api.dto.VectorSearchRequest;
import com.imran.search.api.dto.VectorSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class VectorSearchClient {

    private final WebClient.Builder builder;

    @Value("${services.vector-search}")
    private String vectorSearchUrl;

    public VectorSearchClient(WebClient.Builder builder) {
        this.builder = builder;
    }

    public List<VectorSearchResponse> search(double[] embedding, int topK) {

        VectorSearchRequest req = new VectorSearchRequest();
        req.setEmbedding(embedding);
        req.setTopK(topK);

        return builder.build()
                .post()
                .uri(vectorSearchUrl)
                .bodyValue(req)
                .retrieve()
                .bodyToFlux(VectorSearchResponse.class)
                .collectList()
                .block();
    }
}
