package com.project.scraperService.controllers;

import com.project.scraperService.payload.ScraperResponse;
import com.project.scraperService.service.ScraperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Api(tags = "Scraper Crawling Handler")
public class ScraperController {

    private final ScraperService scraperService;

    @ApiOperation(value = "Scraper Synchronous")
    @PostMapping("fetch/synchronous/scraper")
    public ResponseEntity<Map<String, List<ScraperResponse.ScraperData>>> fetchScraperDataSync(
        @RequestParam(value = "address_url") String url) throws IOException
    {
        ScraperResponse response = scraperService.fetchScraperDataSynchronousService(url);
        return ResponseEntity.ok(response.getResponse());
    }

    @ApiOperation(value = "Scraper Asynchronous")
    @PostMapping("fetch/asynchronous/scraper")
    public ResponseEntity<Map<String, List<ScraperResponse.ScraperData>>> fetchScraperDataAsync(
        @RequestParam(value = "address_url") String url) throws ExecutionException, InterruptedException
    {
        ScraperResponse response = scraperService.fetchScraperDataAsynchronousService(url).get();
        return ResponseEntity.ok(response.getResponse());
    }

}
