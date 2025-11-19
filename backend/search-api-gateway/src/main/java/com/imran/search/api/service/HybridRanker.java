package com.imran.search.api.service;

import com.imran.search.api.dto.KeywordSearchResponse;
import com.imran.search.api.dto.SearchResult;
import com.imran.search.api.dto.VectorSearchResponse;

import java.util.*;

public class HybridRanker {

    public static List<SearchResult> combine(
            List<VectorSearchResponse> vectorResults,
            List<KeywordSearchResponse> keywordResults) {

        Map<String, SearchResult> combined = new HashMap<>();

        // Add vector results (semantic)
        for (VectorSearchResponse vr : vectorResults) {
            SearchResult sr = new SearchResult();
            sr.setId(vr.getId());
            sr.setScore(vr.getScore() * 0.6);  // weight for semantic score
            combined.put(sr.getId(), sr);
        }

        // Add BM25 keyword results
        for (KeywordSearchResponse kr : keywordResults) {
            SearchResult sr = combined.getOrDefault(kr.getId(), new SearchResult());
            sr.setId(kr.getId());
            sr.setTitle(kr.getTitle());
            sr.setDescription(kr.getDescription());
            sr.setScore(sr.getScore() + kr.getScore() * 0.4); // weight for keyword score
            combined.put(sr.getId(), sr);
        }

        // Sort by final hybrid score
        List<SearchResult> resultList = new ArrayList<>(combined.values());
        resultList.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return resultList;
    }
}
