/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web;

import java.util.List;

import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.web.bean.PriceLine;
import com.futu.openapi.web.service.MAStockService;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: MAStockServiceTest.java, v 0.1 2025-03-25 10:28 xuxu Exp $$
 */
public class MAStockServiceTest {

    @Test
    public void testRun() throws Exception {
        MAStockService stockService = new MAStockService();
        List<PriceLine> priceLines = stockService.run("300059", QotMarket.QotMarket_CNSZ_Security.getNumber());

        for (PriceLine line : priceLines) {
            System.out.println(line);
        }
    }

    @Test
    public void testQueryKlines() throws Exception {

        MAStockService stockService = new MAStockService();
        List<KLine> kLineList = stockService.queryKlines("00700", QotCommon.QotMarket.QotMarket_HK_Security.getNumber(),
            -2, 0, 0);

        System.out.println(kLineList);
    }

}