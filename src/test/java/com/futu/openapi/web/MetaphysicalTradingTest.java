/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web;

import com.futu.openapi.web.bean.Quotation;
import com.futu.openapi.web.trading.MetaphysicalTrading;
import com.futu.openapi.web.trading.impl.MetaphysicalTradingImpl;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: MetaphysicalTradingTest.java, v 0.1 2025-04-08 17:47 xuxu Exp $$
 */
public class MetaphysicalTradingTest {

    @Test
    public void testRun() throws Exception {
        MetaphysicalTrading metaphysicalTrading = new MetaphysicalTradingImpl();
        Quotation quotation = metaphysicalTrading.maBacktest("300059.SZ");

        System.out.println(quotation);
    }

}