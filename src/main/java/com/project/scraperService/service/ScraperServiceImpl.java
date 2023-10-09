package com.project.scraperService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.scraperService.payload.ScraperResponse;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Log4j2
@Component
public class ScraperServiceImpl implements ScraperService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScraperServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ScraperResponse fetchScraperDataSynchronousService(String url) throws IOException {
        long startTime = System.currentTimeMillis();

        log.info("Crawling Data From URI: {}", url);

        // Fetch data from web pages using JSOUP
        Object value = constructFetchDataFromWebPage(url);

        // Define response
        Map<String, List<ScraperResponse.ScraperData>> responseMap = new HashMap<>();

        if (value instanceof JSONObject) {
            JSONObject smartRecruiterResult = (JSONObject) value;

            // Loop through all keys in the smartRecruiterResult object
            for (Iterator<String> it = smartRecruiterResult.keys(); it.hasNext(); ) {
                String key = it.next();

                Object fieldValue = smartRecruiterResult.get(key);
                log.info("Field Values By Keys: {} - {}", key, fieldValue);

                if (fieldValue instanceof JSONObject) {
                    JSONObject fieldValues = (JSONObject) fieldValue;
                    JSONArray contentArray = fieldValues.getJSONArray("content");

                    for (int i = 0; i < contentArray.length(); i++) {
                        JSONObject posting = contentArray.getJSONObject(i);
                        String departmentLabel = posting.getJSONObject("department").getString("label");

                        String qualificationUrl = posting.getString("ref");
                        log.info("URI Detail Jobs From Field Ref: {}", qualificationUrl);

                        String fetchURIDetailJobs = restTemplate.getForObject(qualificationUrl, String.class);
                        log.info("Data Response From URI Detail Jobs : {}", fetchURIDetailJobs);

                        JsonNode jsonNode = objectMapper.readTree(fetchURIDetailJobs);

                        // Now you can access data using keys
                        String description = jsonNode.get("jobAd").get("sections").get("jobDescription").get("text").asText();
                        String qualification = jsonNode.get("jobAd").get("sections").get("qualifications").get("text").asText();

                        ScraperResponse.ScraperData scraperData = extractScraperData(posting, description, qualification);

                        // If the department label is not present in the map, create a new list for it
                        responseMap.computeIfAbsent(departmentLabel, k -> new ArrayList<>()).add(scraperData);
                    }
                }
            }
        }

        generateToJsonFile(responseMap, "Cermati Job List - Synchronous");

        long endTime = System.currentTimeMillis();
        log.info("Data Crawling Completed in {} milliseconds", endTime - startTime);

        return new ScraperResponse(responseMap);
    }

    @Async
    @Override
    public CompletableFuture<ScraperResponse> fetchScraperDataAsynchronousService(String url) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("Crawling Data From URI: {}", url);

            // Fetch data from web pages using JSOUP
            Object value = constructFetchDataFromWebPage(url);

            // Define response
            Map<String, List<ScraperResponse.ScraperData>> responseMap = new HashMap<>();

            if (value instanceof JSONObject) {
                JSONObject smartRecruiterResult = (JSONObject) value;

                // Loop through all keys in the smartRecruiterResult object
                for (Iterator<String> it = smartRecruiterResult.keys(); it.hasNext(); ) {
                    String key = it.next();

                    Object fieldValue = smartRecruiterResult.get(key);
                    log.info("Field Values By Keys: {} - {}", key, fieldValue);

                    if (fieldValue instanceof JSONObject) {
                        JSONObject fieldValues = (JSONObject) fieldValue;
                        JSONArray contentArray = fieldValues.getJSONArray("content");

                        List<CompletableFuture<ScraperResponse.ScraperData>> futures = new ArrayList<>();

                        for (int i = 0; i < contentArray.length(); i++) {
                            JSONObject posting = contentArray.getJSONObject(i);
                            String departmentLabel = posting.getJSONObject("department").getString("label");

                            String qualificationUrl = posting.getString("ref");
                            log.info("URI Detail Jobs From Field Ref: {}", qualificationUrl);

                            CompletableFuture<ScraperResponse.ScraperData> future = fetchDetailDataAsync(qualificationUrl, posting);
                            futures.add(future);

                            // Populate responseMap using departmentLabel as keys
                            responseMap.computeIfAbsent(departmentLabel, k -> new ArrayList<>()).add(future.join());
                        }
                        // Wait for all asynchronous calls to complete
                        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                        allOf.get(); // Wait for all tasks to complete before continuing
                    }
                }
            }

            generateToJsonFile(responseMap, "Cermati Job List - Asynchronous");

            long endTime = System.currentTimeMillis();
            log.info("Data Crawling Completed in {} milliseconds", endTime - startTime);

            return CompletableFuture.completedFuture(new ScraperResponse(responseMap));

        } catch (Exception e) {
            log.error("Error occurred during data scraping: {}", e.getMessage());
            CompletableFuture<ScraperResponse> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // Fetch data from web page parse to HTML using JSOUP
    private Object constructFetchDataFromWebPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Document document = Jsoup.parse(doc.html());
        Element scriptTag = document.selectFirst("#initials");

        assert scriptTag != null;
        String jsonContent = scriptTag.data();
        JSONObject jsonObject = new JSONObject(jsonContent);

        return jsonObject.get("smartRecruiterResult");
    }

    private ScraperResponse.ScraperData extractScraperData(JSONObject posting, String description, String qualification) {
        return getScraperData(posting, description, qualification);
    }

    private CompletableFuture<ScraperResponse.ScraperData> fetchDetailDataAsync(String qualificationUrl, JSONObject posting) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String fetchURIDetailJobs = restTemplate.getForObject(qualificationUrl, String.class);
                log.info("Data Response From URI Detail Jobs : {}", fetchURIDetailJobs);

                JsonNode jsonNode = objectMapper.readTree(fetchURIDetailJobs);

                String description = jsonNode.get("jobAd").get("sections").get("jobDescription").get("text").asText();
                String qualification = jsonNode.get("jobAd").get("sections").get("qualifications").get("text").asText();

                return getScraperData(posting, description, qualification);
            } catch (Exception e) {
                log.error("Error occurred during detail data fetching: {}", e.getMessage());
                throw new CompletionException(e);
            }
        });
    }

    private ScraperResponse.ScraperData getScraperData(JSONObject posting, String description, String qualification) {
        String title = posting.getString("name");
        String location = posting.getJSONObject("location").getString("city");
        String jobType = posting.getJSONObject("typeOfEmployment").getString("label");
        String postedBy = posting.getJSONObject("company").getString("name");

        return new ScraperResponse.ScraperData(title, location, description, qualification, jobType, postedBy);
    }

    private void generateToJsonFile(Map<String, List<ScraperResponse.ScraperData>> scraperData, String name) throws IOException {
        String currentWorkingDirectory = System.getProperty("user.dir");
        String directoryPath = currentWorkingDirectory + "/result";
        File directory = new File(directoryPath);
        validateDirectory(directory);
        generateRawDataToJsonFile(scraperData, name, directory);
    }

    private void generateRawDataToJsonFile(Map<String, List<ScraperResponse.ScraperData>> scraperData, String name, File directoryPath) throws IOException {
        String outputPath = directoryPath + "/" + name + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(outputPath), scraperData);
        log.info("ResponseMap exported to JSON file: {}", outputPath);
    }

    private void validateDirectory(File directory) {
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                log.info("Directory created: {}", directory.getAbsolutePath());
            } else {
                log.error("Failed to create directory: {}", directory.getAbsolutePath());
            }
        }
    }

}
