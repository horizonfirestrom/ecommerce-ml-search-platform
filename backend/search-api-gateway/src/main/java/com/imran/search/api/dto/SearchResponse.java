package com.imran.search.api.dto;

import java.util.List;

public class SearchResponse {

    private String query;
    private List<SearchResult> results;

    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }

    public List<SearchResult> getResults() {
        return results;
    }
    public void setResults(List<SearchResult> results) {
        this.results = results;
    }
}

