package com.imran.search.api.controller;

import com.imran.search.api.dto.SearchRequest;
import com.imran.search.api.dto.SearchResponse;
import com.imran.search.api.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
        @RequestParam("query") String query,
        @RequestParam(value = "topK", defaultValue = "10") int topK,
        @RequestParam(value = "userId", required = false) String userId
    ) {
        SearchRequest req = new SearchRequest();
        req.setQuery(query);
        req.setTopK(topK);
        req.setUserId(userId);

        SearchResponse resp = searchService.performSearch(req);
        return ResponseEntity.ok(resp);
    }
}
