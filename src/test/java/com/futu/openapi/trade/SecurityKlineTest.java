/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade;

import java.util.List;

import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.trade.support.SecurityKline;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: SecurityKlineTest.java, v 0.1 2025-03-21 17:22 xuxu Exp $$
 */
public class SecurityKlineTest {

    @Test
    public void testQueryKline() throws Exception {

        //List<KLine> kLineList = SecurityKline.newInstance().queryKline(Security.newBuilder().setMarket(
        //    QotCommon.QotMarket.QotMarket_HK_Security_VALUE)
        //    .setCode("00700").build(), 120, 1000);
        //
        //System.out.println(kLineList);
        List<KLine> kLineList = SecurityKline.newInstance().queryKline(Security.newBuilder().setMarket(
            QotCommon.QotMarket.QotMarket_HK_Security_VALUE)
            .setCode("00700").build(), -1, 120, 1000);

        System.out.println(kLineList);

    }

}