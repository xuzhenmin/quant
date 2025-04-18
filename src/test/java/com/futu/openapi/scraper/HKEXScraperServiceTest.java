/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.scraper;

import java.util.Map;

import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: HKEXScraperServiceTest.java, v 0.1 2025-04-10 17:08 xuxu Exp $$
 */
public class HKEXScraperServiceTest {

    @Test
    public void testScrapeShortSellingData() throws Exception {

        HKEXScraperService scraperService = new HKEXScraperService();
        Map<String, String> stringMap =  scraperService.scrapeShortSellingData("1876");

        System.out.println(stringMap);
    }

}