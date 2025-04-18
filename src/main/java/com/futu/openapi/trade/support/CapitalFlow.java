/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.support;

import java.util.List;

import com.futu.openapi.pb.Common;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotCommon.SecurityStaticBasic;
import com.futu.openapi.pb.QotCommon.SecurityStaticInfo;
import com.futu.openapi.pb.QotGetCapitalDistribution;
import com.futu.openapi.pb.QotGetCapitalDistribution.S2C;
import com.futu.openapi.pb.QotGetCapitalFlow;
import com.futu.openapi.pb.QotGetCapitalFlow.CapitalFlowItem;
import com.futu.openapi.pb.QotGetUserSecurity;
import com.futu.openapi.trade.base.BaseDaemon;
import com.futu.openapi.trade.model.CapitalDistribution;
import com.google.common.collect.Lists;

/**
 * @author zhenmin
 * @version $Id: CapitalFlow.java, v 0.1 2022-04-21 14:49 xuxu Exp $$
 */
public class CapitalFlow extends BaseDaemon {

    private static CapitalFlow capitalFlow;

    public static synchronized CapitalFlow newInstance() {
        if (capitalFlow == null) {
            capitalFlow = new CapitalFlow();
            capitalFlow.createCon();
        }
        return capitalFlow;
    }

    private CapitalFlow() {}

    public List<CapitalFlowItem> queryCapitalFlow(String code, int market) throws Exception {

        List<CapitalFlowItem> capitalFlowItems = Lists.newArrayList();
        QotGetCapitalFlow.C2S c2s = QotGetCapitalFlow.C2S.newBuilder()
            .setSecurity(Security.newBuilder().setCode(code).setMarket(market).build())
            .build();

        QotGetCapitalFlow.Response rsp = getCapitalFlow(c2s);

        if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            System.err.printf("queryCapitalFlow fail: %s\n", rsp == null ? null : rsp.getRetMsg());
            return capitalFlowItems;
        }
        for (CapitalFlowItem capitalFlowItem : rsp.getS2C().getFlowItemListList()) {
            capitalFlowItems.add(capitalFlowItem);
        }
        return capitalFlowItems;
    }

    public CapitalDistribution queryCapitalDistribution(String code, int market) throws Exception {

        QotGetCapitalDistribution.C2S c2s = QotGetCapitalDistribution.C2S.newBuilder()
            .setSecurity(Security.newBuilder().setCode(code).setMarket(market).build())
            .build();

        QotGetCapitalDistribution.Response rsp = getCapitalDistribution(c2s);
        CapitalDistribution.CapitalDistributionBuilder builder = CapitalDistribution.builder();

        if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            System.err.printf("queryCapitalDistribution fail: %s\n", rsp == null ? null : rsp.getRetMsg());
            return builder.build();
        }
        S2C s2C = rsp.getS2C();
        builder.capitalInBig(s2C.getCapitalInBig()).capitalOutBig(s2C.getCapitalOutBig()).capitalOutMid(
            s2C.getCapitalOutMid()).capitalInMid(s2C.getCapitalInMid()).capitalOutSmall(s2C.getCapitalOutSmall())
            .capitalInSmall(s2C.getCapitalInSmall()).updateTimestamp(s2C.getUpdateTimestamp());

        return builder.build();
    }

}