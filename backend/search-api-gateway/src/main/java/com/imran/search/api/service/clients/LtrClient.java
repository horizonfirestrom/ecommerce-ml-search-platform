package com.imran.search.api.service.clients;

import com.imran.search.api.dto.LtrRequest;
import com.imran.search.api.dto.LtrResponse;
import com.imran.search.api.dto.SearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class LtrClient {

    private final WebClient.Builder builder;

    @Value("${services.ltr}")
    private String ltrUrl;

    public LtrClient(WebClient.Builder builder) {
        this.builder = builder;
    }

    public List<SearchResult> reRank(String query, List<SearchResult> candidates) {

        LtrRequest request = new LtrRequest();
        request.setQuery(query);
        request.setCandidates(candidates);

        LtrResponse response = builder.build()
                .post()
                .uri(ltrUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LtrResponse.class)
                .block();

        return response.getReranked();
    }
}
