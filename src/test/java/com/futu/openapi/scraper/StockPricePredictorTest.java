/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.scraper;

import java.util.List;

import com.futu.openapi.scraper.model.PredictionResult;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: StockPricePredictorTest.java, v 0.1 2025-04-14 16:13 xuxu Exp $$
 */
public class StockPricePredictorTest {


    @Test
    public void testPredictStockPrice() throws Exception{

        StockPricePredictor stockPricePredictor = new StockPricePredictor();

        List<PredictionResult> doubleMap =  stockPricePredictor.predictStockPrice("00700.HK");

        System.out.println(doubleMap);
    }


}

