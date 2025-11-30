package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Client that calls the Python-based vector-search-service /search endpoint.
 *
 * Expected request body (JSON):
 * {
 *   "embedding": [0.12, 0.34, ...],
 *   "top_k": 10
 * }
 *
 * Expected response body (JSON):
 * [
 *   { "product_id": "P1", "score": 0.98 },
 *   { "product_id": "P2", "score": 0.94 }
 * ]
 *
 * On the Java side we deserialize it as List<Map<String, Object>>
 * and then extract the product IDs into List<String>.
 */
@Component
public class VectorSearchClient {

    // Logger for debug / troubleshooting
    private static final Logger log = LoggerFactory.getLogger(VectorSearchClient.class);

    // Spring WebClient used to call other services over HTTP
    private final WebClient webClient;

    // Holds the URLs for downstream services (configured elsewhere)
    private final ServiceEndpoints endpoints;

    /**
     * Constructor used by Spring to inject dependencies.
     *
     * @param webClient  a configured WebClient bean
     * @param endpoints  configuration bean that provides vector-search-service URL
     */
    @Autowired
    public VectorSearchClient(WebClient webClient, ServiceEndpoints endpoints) {
        this.webClient = webClient;
        this.endpoints = endpoints;
    }

    /**
     * Call vector-search-service /search endpoint with the given embedding and topK.
     *
     * @param embedding the query embedding (must match Python model dimension, e.g. 384 floats)
     * @param topK      number of top results to retrieve
     * @return list of product IDs returned by vector-search-service (can be empty, but never null)
     */
    public List<String> search(List<Float> embedding, int topK) {
        // Guard clause: if embedding is null/empty, we avoid calling the service
        if (embedding == null || embedding.isEmpty()) {
            log.warn("VectorSearchClient.search called with null/empty embedding, returning empty list");
            return Collections.emptyList();
        }

        // Build the JSON request body for Python service:
        // { "embedding": [...], "top_k": topK }
        Map<String, Object> requestBody = Map.of(
                "embedding", embedding,
                "top_k", topK
        );

        log.debug("Calling vector-search-service at {} with topK={} and embedding dimension={}",
                endpoints.getVectorSearch(), topK, embedding.size());

        // We expect a TOP-LEVEL JSON ARRAY of objects, so we use ParameterizedTypeReference<List<Map<String,Object>>>
        List<Map<String, Object>> response = webClient.post()
                .uri(endpoints.getVectorSearch())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block(); // block() is fine here as this is a sync boundary inside the gateway

        // If response is null or empty, just return an empty list (not an error in most cases)
        if (response == null || response.isEmpty()) {
            log.warn("Vector search returned null/empty response for topK={} and embedding dimension={}",
                    topK, embedding.size());
            return Collections.emptyList();
        }

        // Map each JSON object to its product ID.
        // We support both "product_id" (snake_case) and "productId" (camelCase) just in case.
        List<String> productIds = response.stream()
                .map(item -> {
                    // Try "product_id" (snake_case)
                    Object pid = item.get("product_id");

                    // Fallback to "productId" (camelCase)
                    if (pid == null) {
                        pid = item.get("productId");
                    }

                    // Fallback to "id" (what your Python service is actually sending)
                    if (pid == null) {
                        pid = item.get("id");
                    }

                    if (pid == null) {
                        log.warn("Missing product_id/productId/id in vector search response item: {}", item);
                        return null;
                    }

                    return pid.toString();
                })
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());


        log.debug("Vector search returned {} product IDs: {}", productIds.size(), productIds);
        return productIds;
    }
}
