package com.project.scraperService.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScraperResponse {

    private Map<String, List<ScraperData>> response;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScraperData {
        private String title;
        private String location;
        private String description;
        private String qualification;
        private String jobType;
        private String postedBy;
    }
}
