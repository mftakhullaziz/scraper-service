package com.project.scraperService.service;

import com.project.scraperService.payload.ScraperResponse;

import java.io.IOException;

public interface ScraperService {
    ScraperResponse fetchScraperDataService(String url) throws IOException;
}
