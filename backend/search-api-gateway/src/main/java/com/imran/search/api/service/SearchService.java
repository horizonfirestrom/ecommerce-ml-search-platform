package com.imran.search.api.service;

import com.imran.search.api.dto.KeywordSearchResponse;
import com.imran.search.api.dto.SearchRequest;
import com.imran.search.api.dto.SearchResponse;
import com.imran.search.api.dto.SearchResult;
import com.imran.search.api.dto.VectorSearchResponse;
import com.imran.search.api.service.clients.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final NlpClient nlpClient;
    private final VectorSearchClient vectorClient;
    private final KeywordSearchClient keywordClient;
    private final LtrClient ltrClient;
    private final AbTestingClient abTestingClient;

    // 🔥 MANUAL CONSTRUCTOR (Spring will autowire dependencies here)
    public SearchService(
            NlpClient nlpClient,
            VectorSearchClient vectorClient,
            KeywordSearchClient keywordClient,
            LtrClient ltrClient,
            AbTestingClient abTestingClient
    ) {
        this.nlpClient = nlpClient;
        this.vectorClient = vectorClient;
        this.keywordClient = keywordClient;
        this.ltrClient = ltrClient;
        this.abTestingClient = abTestingClient;
    }

    public SearchResponse search(SearchRequest request) {

        String query = request.getQuery();

        // 1. Embed query
        double[] queryEmbedding = nlpClient.embed(query);

        // 2. Vector search
        List<VectorSearchResponse> vectorResults =
                vectorClient.search(queryEmbedding, request.getTopK());

        // 3. Keyword search (BM25)
        List<KeywordSearchResponse> keywordResults =
                keywordClient.search(query, request.getTopK());

        // 4. Hybrid Fusion
        List<SearchResult> combined =
                HybridRanker.combine(vectorResults, keywordResults);

        // 5. A/B Test bucket assignment
        String bucket = abTestingClient.assignBucket();

        if ("ltr".equals(bucket)) {
            combined = ltrClient.reRank(query, combined);
        }

        // 6. Prepare response
        SearchResponse response = new SearchResponse();
        response.setQuery(query);
        response.setResults(combined);

        return response;
    }
}
