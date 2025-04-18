/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade;

import java.io.File;

import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.run.util.oss.OssClient;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: OssClientTest.java, v 0.1 2024-05-07 15:55 xuxu Exp $$
 */
public class OssClientTest {

    @Before
    public void init() {
        PropUtil.readProp();
    }

    @Test
    public void testOssClient() throws Exception {

        OssClient.newInstance();
    }

    @Test
    public void testLoadData() throws Exception {

        OssClient.newInstance().loadData("data/20240423/market-trend-20240423.png", "market-trend-20240423.png");
    }

    @Test
    public void testImgUtil() throws Exception {

        OssClient.newInstance().generateUrl("market-trend-20240423.png");
    }

}