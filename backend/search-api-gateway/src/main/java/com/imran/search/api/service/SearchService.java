package com.imran.search.api.service;

import com.imran.search.api.dto.SearchRequest;
import com.imran.search.api.dto.SearchResponse;
import com.imran.search.api.service.clients.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private final QueryUnderstandingClient queryClient;
    private final NlpClient nlpClient;
    private final VectorSearchClient vectorClient;
    private final LtrClient ltrClient;
    private final AbTestingClient abTestingClient;

    @Autowired
    public SearchService(
            QueryUnderstandingClient queryClient,
            NlpClient nlpClient,
            VectorSearchClient vectorClient,
            LtrClient ltrClient,
            AbTestingClient abTestingClient
    ) {
        this.queryClient = queryClient;
        this.nlpClient = nlpClient;
        this.vectorClient = vectorClient;
        this.ltrClient = ltrClient;
        this.abTestingClient = abTestingClient;
    }

    public SearchResponse performSearch(SearchRequest req) {
        // 1. Clean the query
        String cleaned = queryClient.cleanQuery(req.getQuery());

        // 2. Get embedding for the cleaned query
        List<Float> embedding = nlpClient.getEmbedding(cleaned);

        // 3. Perform vector search with embedding and topK
        List<String> initialIds = vectorClient.search(embedding, req.getTopK());

        // 4. Build features for the initial product IDs
        Map<String, List<Float>> features = ((Object) ltrClient).buildFeatures(initialIds);

        // 5. Rerank results
        List<String> reranked = ltrClient.rerank(cleaned, initialIds, features);

        // 6. Optional AB-Testing: only if userId provided
        String variant = null;
        String userId = req.getUserId();
        if (userId != null && !userId.isBlank()) {
            variant = abTestingClient.assignVariant("exp_search_ranking", userId);
            abTestingClient.logEvent(userId, "exp_search_ranking", variant, "search",
                    Map.of("query", req.getQuery()));
        }

        // 7. Prepare and return response
        SearchResponse resp = new SearchResponse();
        resp.setOriginalQuery(req.getQuery());
        resp.setCleanedQuery(cleaned);
        resp.setVariant(variant);
        resp.setProductIds(reranked);

        return resp;
    }
}
