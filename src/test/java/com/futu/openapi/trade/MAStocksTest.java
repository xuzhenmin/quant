/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade;

import com.futu.openapi.trade.run.MAStocks;
import com.futu.openapi.trade.run.util.PropUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: MAStocksTest.java, v 0.1 2025-02-13 20:30 xuxu Exp $$
 */
public class MAStocksTest {

    @Before
    public void init() {
        PropUtil.readProp();
    }

    @Test
    public void testRun() throws Exception {
        MAStocks.run();

    }

}