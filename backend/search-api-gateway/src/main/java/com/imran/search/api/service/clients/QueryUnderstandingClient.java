package com.imran.search.api.service.clients;

import com.imran.search.api.config.ServiceEndpoints;
import com.imran.search.api.dto.QueryUnderstandingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Client that calls the Python query-understanding-service (/process).
 */
@Service
public class QueryUnderstandingClient {

    private final WebClient webClient;
    private final ServiceEndpoints endpoints;

    public QueryUnderstandingClient(WebClient.Builder builder,
                                    ServiceEndpoints endpoints) {
        // We let ServiceEndpoints contain the full URL, so baseUrl can be empty.
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
        // Request body: { "query": "<user query>" }
        Map<String, String> requestBody = Map.of("query", query);

        QueryUnderstandingResponse resp = webClient
                .post()
                .uri(endpoints.getQueryUnderstanding())   // e.g. http://query-understanding-service:7003/process
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(QueryUnderstandingResponse.class)
                .block();

        if (resp == null || resp.getCleanedQuery() == null || resp.getCleanedQuery().isBlank()) {
            // This is exactly where your "Invalid response..." exception came from
            throw new RuntimeException("Invalid response from query-understanding service");
        }

        return resp.getCleanedQuery();
    }
}
