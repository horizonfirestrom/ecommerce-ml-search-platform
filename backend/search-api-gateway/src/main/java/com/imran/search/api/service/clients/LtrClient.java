package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Client for calling ltr-service /rerank endpoint.
 */
@Service
public class LtrClient {

    private static final Logger log = LoggerFactory.getLogger(LtrClient.class);

    // WebClient used to call downstream LTR microservice
    private final WebClient webClient;

    // Bean that holds service URLs (e.g. http://ltr-service:7005/rerank)
    private final ServiceEndpoints endpoints;

    public LtrClient(WebClient.Builder builder, ServiceEndpoints endpoints) {
        // We do not set baseUrl here, endpoints provides full URL.
        this.webClient = builder.build();
        this.endpoints = endpoints;
    }

    /**
     * Call ltr-service /rerank.
     *
     * Request JSON:
     * {
     *   "query": "red shoes",
     *   "productIds": ["product_12", "product_61", ...],
     *   "features": {
     *     "bm25": [1.2, 0.8, ...],
     *     "vector_score": [0.9, 0.7, ...]
     *   }
     * }
     *
     * Response JSON from Python:
     * {
     *   "rerankedProductIds": ["product_61", "product_12", ...]
     * }
     *
     * If the LTR service fails or returns unexpected payload,
     * we fall back to the original productIds order.
     */
    public List<String> rerank(String query,
                               List<String> productIds,
                               Map<String, List<Float>> features) {

        // If there is nothing to rerank, short-circuit.
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Build request body to send to Python LTR service.
        Map<String, Object> body = Map.of(
                "query", query,
                "productIds", productIds,
                "features", features != null ? features : Collections.emptyMap()
        );

        try {
            // Use Map-based response to avoid tight coupling.
            Map<String, Object> resp = webClient.post()
                    .uri(endpoints.getLtr()) // <-- ensure this method returns "http://ltr-service:7005/rerank"
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (resp == null || !resp.containsKey("rerankedProductIds")) {
                log.warn("LTR: response missing 'rerankedProductIds', falling back to original order. resp={}", resp);
                return productIds;
            }

            @SuppressWarnings("unchecked")
            List<String> reranked = (List<String>) resp.get("rerankedProductIds");

            if (reranked == null || reranked.isEmpty()) {
                log.warn("LTR: 'rerankedProductIds' empty or null, falling back to original order.");
                return productIds;
            }

            log.info("LTR: rerank successful. First result: {}", reranked.get(0));
            return reranked;

        } catch (WebClientResponseException ex) {
            // HTTP error from the LTR service (e.g. 500, 404, etc.)
            log.warn("LTR: HTTP error {} from LTR service, falling back to original order. msg={}",
                    ex.getStatusCode(), ex.getMessage());
            return productIds;

        } catch (Exception ex) {
            // Any other client-side error (timeout, decoding, etc.)
            log.warn("LTR: error calling LTR service, falling back to original order.", ex);
            return productIds;
        }
    }
}
