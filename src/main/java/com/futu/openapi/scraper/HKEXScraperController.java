package com.futu.openapi.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hkex")
public class HKEXScraperController {
    
    private static final Logger logger = LoggerFactory.getLogger(HKEXScraperController.class);
    
    @Autowired
    private HKEXScraperService scraperService;
    
    @GetMapping("/short-selling")
    public ResponseEntity<?> getShortSellingData() {
        try {
            logger.info("Received request for short selling data");
            Map<String, String> data = scraperService.scrapeShortSellingData("");
            logger.info("Successfully retrieved {} records", data.size());
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            logger.error("Failed to fetch short selling data: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to connect to HKEX website: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching short selling data: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch short selling data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
} 