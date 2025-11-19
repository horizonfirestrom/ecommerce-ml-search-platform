package com.imran.search.api.dto;

import java.util.List;

public class LtrResponse {

    private List<SearchResult> reranked;

    public List<SearchResult> getReranked() {
        return reranked;
    }

    public void setReranked(List<SearchResult> reranked) {
        this.reranked = reranked;
    }
}

