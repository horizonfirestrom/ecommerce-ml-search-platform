package com.imran.search.api.dto;

public class SearchResult {
    private String id;
    private String title;
    private String description;
    private double score;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String t) {
        this.title = t;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String d) {
        this.description = d;
    }

    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }
}
