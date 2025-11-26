package com.imran.search.api.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO to map the JSON returned by query-understanding-service /process.
 * The Python service returns fields:
 *   - cleaned_query
 *   - tokens
 * so we use @JsonProperty to bind cleaned_query -> cleanedQuery.
 */
public class QueryUnderstandingResponse {

    // Map JSON field "cleaned_query" to this Java property
    @JsonProperty("cleaned_query")
    private String cleanedQuery;

    // Field name "tokens" matches exactly, no annotation needed
    private List<String> tokens;

    // ----- Getters & setters -----

    public String getCleanedQuery() {
        return cleanedQuery;
    }

    public void setCleanedQuery(String cleanedQuery) {
        this.cleanedQuery = cleanedQuery;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }
}
