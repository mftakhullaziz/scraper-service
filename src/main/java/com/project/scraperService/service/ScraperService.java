package com.project.scraperService.service;

import com.project.scraperService.payload.ScraperResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface ScraperService {
    ScraperResponse fetchScraperDataSynchronousService(String url) throws IOException;
    CompletableFuture<ScraperResponse> fetchScraperDataAsynchronousService(String url);
}
