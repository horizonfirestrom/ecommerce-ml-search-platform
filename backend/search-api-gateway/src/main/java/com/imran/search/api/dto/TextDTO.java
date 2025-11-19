package com.imran.search.api.dto;

public class TextDTO {
    private String text;

    public TextDTO() {}
    public TextDTO(String text) { this.text = text; }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
