/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.service;

import java.util.List;

import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotGetSecuritySnapshot.Snapshot;
import com.futu.openapi.trade.support.SecuritySnapshot;
import com.futu.openapi.web.controller.TradingController;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhenmin
 * @version $Id: QuotationService.java, v 0.1 2025-04-01 20:18 xuxu Exp $$
 */
@Component
public class QuotationService {

    private final SecuritySnapshot securitySnapshot = SecuritySnapshot.newInstance();

    private final static Logger LOGGER = LoggerFactory
        .getLogger(QuotationService.class);

    /**
     * @param code
     * @param market
     * @return
     */
    public List<Snapshot> querySnapshot(String code, Integer market) {

        List<Snapshot> snapshotList = Lists.newArrayList();
        List<Security> securities = Lists.newArrayList();

        try {
            Security security = Security.newBuilder().setCode(code).setMarket(market).build();
            securities.add(security);
            snapshotList = securitySnapshot.querySnapshot(securities);
        } catch (Exception e) {
            LOGGER.error("QuotationService querySnapshot error. code:{} ,market:{}", code, market, e);
        }
        return snapshotList;
    }

}