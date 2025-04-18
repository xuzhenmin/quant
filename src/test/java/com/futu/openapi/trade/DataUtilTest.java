/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade;

import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.trade.run.util.PropUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: DataUtilTest.java, v 0.1 2024-05-08 16:00 xuxu Exp $$
 */
public class DataUtilTest {

    @Before
    public void init() {
        PropUtil.readProp();
    }

    @Test
    public void testLoadPeak() throws Exception {

        DataUtil.loadPeak(Constants.SELECT_STOCK_FILE);

    }

}