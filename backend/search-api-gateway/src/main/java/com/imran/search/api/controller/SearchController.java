package com.imran.search.api.controller;

import com.imran.search.api.dto.SearchRequest;
import com.imran.search.api.dto.SearchResponse;
import com.imran.search.api.service.SearchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    // 🔥 Add constructor for dependency injection
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public SearchResponse search(@RequestBody SearchRequest request) {
        return searchService.search(request);
    }
    }
