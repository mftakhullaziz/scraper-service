package com.project.scraperService;

import com.project.scraperService.payload.ScraperResponse;
import com.project.scraperService.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebMvc
@EnableSwagger2
public class ScraperServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScraperServiceApplication.class, args);
	}

	// This class for handler if u want running using commandLine
	// Example: java -jar your-application.jar synchronous https://example.com
	@Component
	public static class ScraperCommandLineRunner implements CommandLineRunner {

		private final ScraperService scraperService;

		public ScraperCommandLineRunner(ScraperService scraperService) {
			this.scraperService = scraperService;
		}

		@Override
		public void run(String... args) throws Exception {
			if (args.length < 2) {
				System.out.println("Please provide the service type (synchronous/asynchronous) and the URL.");
				return;
			}

			String serviceType = args[0];
			String url = args[1];

			if ("synchronous".equalsIgnoreCase(serviceType)) {
				System.out.println("Synchronous scraping data from URL: " + url);
				ScraperResponse synchronousResponse = scraperService.fetchScraperDataSynchronousService(url);
				System.out.println("Synchronous Scraper Response: " + synchronousResponse);
			} else if ("asynchronous".equalsIgnoreCase(serviceType)) {
				System.out.println("Asynchronous scraping data from URL: " + url);
				scraperService.fetchScraperDataAsynchronousService(url).thenAcceptAsync(asynchronousResponse -> {
					System.out.println("Asynchronous Scraper Response: " + asynchronousResponse);
				});
			} else {
				System.out.println("Invalid service type. Please provide 'synchronous' or 'asynchronous'.");
			}
		}
	}
}
