package org.devlogtwo.devlog.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.search.dto.response.SearchResponse;
import org.devlogtwo.devlog.domain.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<GlobalApiResponse<SearchResponse>> searchAll(@RequestParam("q") String query) {

        SearchResponse response = searchService.searchAll(query);
        return ResponseHelper.success(SuccessCode.SEARCH_SUCCESS, response);
    }
}
