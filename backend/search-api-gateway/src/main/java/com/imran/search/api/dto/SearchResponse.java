package com.imran.search.api.dto;

import java.util.List;

public class SearchResponse {
    private String originalQuery;
    private String cleanedQuery;
    private String variant;
    private List<String> productIds;

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }

    public String getCleanedQuery() {
        return cleanedQuery;
    }

    public void setCleanedQuery(String cleanedQuery) {
        this.cleanedQuery = cleanedQuery;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
}
