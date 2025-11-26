package com.imran.search.api.config;

public class ServiceEndpoints {
    private String queryUnderstanding;
    private String nlpEmbed;
    private String vectorSearch;
    private String ltr;
    private String abAssign;
    private String abLog;

    public ServiceEndpoints() { }

    public String getQueryUnderstanding() { return queryUnderstanding; }
    public void setQueryUnderstanding(String queryUnderstanding) { this.queryUnderstanding = queryUnderstanding; }

    public String getNlpEmbed() { return nlpEmbed; }
    public void setNlpEmbed(String nlpEmbed) { this.nlpEmbed = nlpEmbed; }

    public String getVectorSearch() { return vectorSearch; }
    public void setVectorSearch(String vectorSearch) { this.vectorSearch = vectorSearch; }

    public String getLtr() { return ltr; }
    public void setLtr(String ltr) { this.ltr = ltr; }

    public String getAbAssign() { return abAssign; }
    public void setAbAssign(String abAssign) { this.abAssign = abAssign; }

    public String getAbLog() { return abLog; }
    public void setAbLog(String abLog) { this.abLog = abLog; }
}
