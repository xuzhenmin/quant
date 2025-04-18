/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.scraper;

import com.futu.openapi.scraper.model.TrendAnalysisResult;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: StockTrendAnalyzerTest.java, v 0.1 2025-04-17 10:48 xuxu Exp $$
 */
public class StockTrendAnalyzerTest {

    @Test
    public void testAnalyzeTrend() throws Exception {

        StockTrendAnalyzer trendAnalyzer = new StockTrendAnalyzer();
        TrendAnalysisResult analysisResult = trendAnalyzer.analyzeTrend("09988.HK");
        System.out.println(analysisResult);

    }

}