package com.futu.openapi.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HKEXScraperService {

    private static final Logger logger = LoggerFactory.getLogger(HKEXScraperService.class);
    private static final String HKEX_URL = "https://www.hkex.com.hk/chi/stat/smstat/ssturnover/ncms/mshtmain_c.htm";
    private static final String DATA_DIR = "data";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");

    public Map<String, String> scrapeShortSellingData(String index) throws IOException {
        Map<String, String> result = new HashMap<>();
        String today = DATE_FORMAT.format(new Date());
        String yearMonth = YEAR_MONTH_FORMAT.format(new Date());
        
        // Create path with year/month subdirectory
        Path dataDir = Paths.get(DATA_DIR, yearMonth);
        Path dataFile = dataDir.resolve(today + ".txt");
        
        try {
            // Check if data file exists for today
            if (Files.exists(dataFile)) {
                logger.info("Reading data from cache file: {}", dataFile);
                return readFromCache(dataFile, index);
            }
            
            // If no cache exists, fetch from website
            logger.info("No cache found, fetching data from HKEX website: {}", HKEX_URL);
            
            // Add request headers to simulate a real browser
            Document doc = Jsoup.connect(HKEX_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Cache-Control", "max-age=0")
                    .timeout(30000)
                    .get();
            
            logger.info("Successfully connected to HKEX website");
            
            // Get the entire text content
            String content = doc.text();
            logger.info("Page content: {}", content);
            
            // Create data directory if it doesn't exist
            Files.createDirectories(dataDir);
            
            // Write content to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile.toFile()))) {
                writer.write(content);
                logger.info("Data written to cache file: {}", dataFile);
            }
            
            // Process the content
            if (index == null || index.trim().isEmpty()) {
                // If no index provided, return the total volume
                Pattern totalPattern = Pattern.compile("賣空交易成交股數[　\\s]+[：:][　\\s]+([\\d,]+)");
                Matcher totalMatcher = totalPattern.matcher(content);
                if (totalMatcher.find()) {
                    String volume = totalMatcher.group(1).replaceAll(",", ""); // Remove commas
                    logger.info("Found total short selling volume: {}", volume);
                    result.put("volume", volume);
                } else {
                    logger.error("No total short selling volume found in the content");
                    throw new RuntimeException("No total short selling volume found in the content");
                }
            } else {
                // Parse all stock data into a Map
                Map<String, String> stockData = new HashMap<>();
                // Pattern to match: "1  長和　　　　　　       2,115,000     83,774,425"
                Pattern stockPattern = Pattern.compile("(\\d+)[　\\s]+[^\\d]+[　\\s]+([\\d,]+)[　\\s]+[\\d,]+");
                Matcher stockMatcher = stockPattern.matcher(content);
                
                while (stockMatcher.find()) {
                    String stockIndex = stockMatcher.group(1);
                    String volume = stockMatcher.group(2).replaceAll(",", ""); // Remove commas
                    stockData.put(stockIndex, volume);
                    logger.info("Found stock data - Index: {}, Volume: {}", stockIndex, volume);
                }
                
                // Return the specific stock's volume if index exists
                if (stockData.containsKey(index)) {
                    result.put("volume", stockData.get(index));
                } else {
                    logger.error("No short selling volume found for index: {}", index);
                    throw new RuntimeException("No short selling volume found for index: " + index);
                }
            }
            
        } catch (IOException e) {
            logger.error("Failed to connect to HKEX website: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while scraping data: {}", e.getMessage());
            throw new RuntimeException("Failed to scrape data: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    private Map<String, String> readFromCache(Path dataFile, String index) throws IOException {
        Map<String, String> result = new HashMap<>();
        String content = new String(Files.readAllBytes(dataFile));
        
        if (index == null || index.trim().isEmpty()) {
            // If no index provided, return the total volume
            Pattern totalPattern = Pattern.compile("賣空交易成交股數[　\\s]+[：:][　\\s]+([\\d,]+)");
            Matcher totalMatcher = totalPattern.matcher(content);
            if (totalMatcher.find()) {
                String volume = totalMatcher.group(1).replaceAll(",", ""); // Remove commas
                logger.info("Found total short selling volume from cache: {}", volume);
                result.put("volume", volume);
            } else {
                logger.error("No total short selling volume found in cache");
                throw new RuntimeException("No total short selling volume found in cache");
            }
        } else {
            // Parse all stock data into a Map
            Map<String, String> stockData = new HashMap<>();
            // Pattern to match: "1  長和　　　　　　       2,115,000     83,774,425"
            Pattern stockPattern = Pattern.compile("(\\d+)[　\\s]+[^\\d]+[　\\s]+([\\d,]+)[　\\s]+[\\d,]+");
            Matcher stockMatcher = stockPattern.matcher(content);
            
            while (stockMatcher.find()) {
                String stockIndex = stockMatcher.group(1);
                String volume = stockMatcher.group(2).replaceAll(",", ""); // Remove commas
                stockData.put(stockIndex, volume);
                logger.info("Found stock data from cache - Index: {}, Volume: {}", stockIndex, volume);
            }
            
            // Return the specific stock's volume if index exists
            if (stockData.containsKey(index)) {
                result.put("volume", stockData.get(index));
            } else {
                logger.error("No short selling volume found for index in cache: {}", index);
                throw new RuntimeException("No short selling volume found for index in cache: " + index);
            }
        }
        
        return result;
    }
}