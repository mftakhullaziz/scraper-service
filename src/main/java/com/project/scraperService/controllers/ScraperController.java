package com.project.scraperService.controllers;

import com.project.scraperService.payload.ScraperResponse;
import com.project.scraperService.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ScraperController {

    private final ScraperService scraperService;

    @PostMapping("fetch/scraper")
    public ResponseEntity<ScraperResponse> fetchScraperDataFromWebApp(@RequestParam(value = "url") String url) throws IOException {
        ScraperResponse response = scraperService.fetchScraperDataService(url);
        return ResponseEntity.ok(new ScraperResponse());
    }

}
