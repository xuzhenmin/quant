/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade;

import com.futu.openapi.trade.run.WatchGoodStocks;
import com.futu.openapi.trade.run.analysis.CyclicalAnalysis;
import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.run.util.sink.Sink2Ding;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: TradeRunTest.java, v 0.1 2024-05-22 20:56 xuxu Exp $$
 */
public class TradeRunTest {

    @Before
    public void init() {
        PropUtil.readProp();
    }

    @Test
    public void testRun() throws Exception {
        WatchGoodStocks.run();
        CyclicalAnalysis.analysisPeak();
        Sink2Ding.sendMsg();

    }

}