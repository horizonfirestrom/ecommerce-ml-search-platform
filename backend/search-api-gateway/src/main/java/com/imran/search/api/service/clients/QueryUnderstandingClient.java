package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import com.imran.search.api.dto.QueryUnderstandingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client that calls the Python query-understanding-service (/process).
 */
@Service
public class QueryUnderstandingClient {

    private final WebClient webClient;
    private final ServiceEndpoints endpoints;

    public QueryUnderstandingClient(WebClient.Builder builder,
                                    ServiceEndpoints endpoints) {
        this.webClient = builder.build();
        this.endpoints = endpoints;
    }

    /**
     * Send the raw query to the Python service and return the cleaned query.
     *
     * @param query original user query, e.g. "   red shoes !!!"
     * @return cleaned query from Python, e.g. "red shoes"
     */
    public String cleanQuery(String query) {
        QueryUnderstandingResponse response = webClient.post()
                .uri(endpoints.getQueryUnderstanding()) // e.g. http://query-understanding-service:7003/process
                .bodyValue(java.util.Map.of("query", query))
                .retrieve()
                .bodyToMono(QueryUnderstandingResponse.class)
                .block();

        if (response == null || response.getCleanedQuery() == null) {
            throw new RuntimeException("Invalid response from query-understanding service: " + response);
        }

        return response.getCleanedQuery();
    }
}
