/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.support;

import java.util.ArrayList;

import com.futu.openapi.pb.Common;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotGetStaticInfo;
import com.futu.openapi.trade.base.BaseDaemon;

/**
 * @author zhenmin
 * @version $Id: SecurityStockCode.java, v 0.1 2022-03-09 9:52 上午 xuxu Exp $$
 */
public class SecurityStockCode extends BaseDaemon {

    private static SecurityStockCode securityStockCode;

    public static synchronized SecurityStockCode newInstance() {
        if (securityStockCode == null) {
            securityStockCode = new SecurityStockCode();
            securityStockCode.createCon();
        }
        return securityStockCode;
    }

    private SecurityStockCode() {}

    /**
     * query stock code
     *
     * @param market
     * @return
     * @throws Exception
     */
    public ArrayList<Security> queryStockCodes(QotCommon.QotMarket market) throws Exception {

        int[] stockTypes = {QotCommon.SecurityType.SecurityType_Eqty_VALUE,
            QotCommon.SecurityType.SecurityType_Index_VALUE,
            QotCommon.SecurityType.SecurityType_Trust_VALUE,
            QotCommon.SecurityType.SecurityType_Warrant_VALUE,
            QotCommon.SecurityType.SecurityType_Bond_VALUE};
        ArrayList<Security> stockCodes = new ArrayList<>();
        for (int stockType : stockTypes) {
            QotGetStaticInfo.C2S c2s = QotGetStaticInfo.C2S.newBuilder()
                .setMarket(market.getNumber())
                .setSecType(stockType)
                .build();
            QotGetStaticInfo.Response rsp = getStaticInfoSync(c2s);
            if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
                System.err.printf("queryStockCodes fail: %s\n", rsp == null ? null : rsp.getRetMsg());
                return stockCodes;
            }
            for (QotCommon.SecurityStaticInfo info : rsp.getS2C().getStaticInfoListList()) {
                stockCodes.add(info.getBasic().getSecurity());
            }
        }

        return stockCodes;

    }
}