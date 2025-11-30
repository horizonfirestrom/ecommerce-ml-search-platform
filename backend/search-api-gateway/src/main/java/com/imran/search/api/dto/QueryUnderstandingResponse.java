package com.imran.search.api.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO to map the JSON returned by query-understanding-service /process.
 *
 * Expected Python JSON:
 * {
 *   "cleaned_query": "red shoes",
 *   "tokens": ["red", "shoes"]
 * }
 */
public class QueryUnderstandingResponse {

    // Maps JSON field "cleaned_query" -> Java field cleanedQuery
    @JsonProperty("cleaned_query")
    private String cleanedQuery;

    // JSON field "tokens" matches this name directly
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

    @Override
    public String toString() {
        return "QueryUnderstandingResponse{" +
                "cleanedQuery='" + cleanedQuery + '\'' +
                ", tokens=" + tokens +
                '}';
    }
}
