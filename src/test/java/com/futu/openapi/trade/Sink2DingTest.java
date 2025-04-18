/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade;

import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.run.util.sink.Sink2Ding;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: Sink2DingTest.java, v 0.1 2024-05-10 20:03 xuxu Exp $$
 */
public class Sink2DingTest {

    @Before
    public void init() {
        PropUtil.readProp();
    }

    @Test
    public void testSendMsg() throws Exception {

        Sink2Ding.sendMsg();

    }
}