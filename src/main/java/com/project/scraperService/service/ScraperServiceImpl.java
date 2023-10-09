package com.project.scraperService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.scraperService.payload.ScraperResponse;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScraperServiceImpl implements ScraperService {

    @Override
    public ScraperResponse fetchScraperDataService(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        // Parse the HTML content
        Document document = Jsoup.parse(doc.html());
        // Select the script tag with the specified ID
        Element scriptTag = document.selectFirst("#initials");
        // Extract the JSON content from the script tag
        assert scriptTag != null;
        String jsonContent = scriptTag.data();

        JSONObject jsonObject = new JSONObject(jsonContent);
        Object value = jsonObject.get("smartRecruiterResult");
        System.out.println(value);

        return null;
    }

}
