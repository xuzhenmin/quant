/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade;

import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.trade.service.CapitalService;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: CapitalTest.java, v 0.1 2024-04-17 14:13 xuxu Exp $$
 */
public class CapitalTest {

    @Test
    public void queryCapitalFlow() throws Exception {

        CapitalService capitalService = new CapitalService();
        capitalService.queryCapitalFlow("002124", QotMarket.QotMarket_CNSZ_Security_VALUE);
        capitalService.queryCapitalDistribution("002124", QotMarket.QotMarket_CNSZ_Security_VALUE);

        capitalService.queryCapitalFlow("02333", QotMarket.QotMarket_HK_Security_VALUE);
        capitalService.queryCapitalDistribution("02333", QotMarket.QotMarket_HK_Security_VALUE);


    }

}