package com.imran.search.api.dto;

import java.util.List;

public class LtrRequest {
    private String query;
    private List<SearchResult> candidates;

    public String getQuery() { return query; }
    public void setQuery(String q) { this.query = q; }

    public List<SearchResult> getCandidates() { return candidates; }
    public void setCandidates(List<SearchResult> c) { this.candidates = c; }
}

