package com.imran.search.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public ServiceEndpoints serviceEndpoints(
        @Value("${services.query-understanding}") String queryUrl,
        @Value("${services.nlp}") String nlpUrl,
        @Value("${services.vector-search}") String vecUrl,
        @Value("${services.ltr}") String ltrUrl,
        @Value("${services.ab-assign}") String abAssignUrl,
        @Value("${services.ab-log}") String abLogUrl
    ) {
        ServiceEndpoints e = new ServiceEndpoints();
        e.setQueryUnderstanding(queryUrl);
        e.setNlpEmbed(nlpUrl);
        e.setVectorSearch(vecUrl);
        e.setLtr(ltrUrl);
        e.setAbAssign(abAssignUrl);
        e.setAbLog(abLogUrl);
        return e;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
