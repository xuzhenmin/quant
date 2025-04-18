/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade;

import com.futu.openapi.trade.run.analysis.CyclicalAnalysis;
import com.futu.openapi.trade.run.util.PropUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: CyclicalAnalysisTest.java, v 0.1 2024-05-08 16:53 xuxu Exp $$
 */
public class CyclicalAnalysisTest {

    @Before
    public void init() {
        PropUtil.readProp();
    }

    @Test
    public void testAnalysisPeak() throws Exception {

        CyclicalAnalysis.analysisPeak();
    }

}