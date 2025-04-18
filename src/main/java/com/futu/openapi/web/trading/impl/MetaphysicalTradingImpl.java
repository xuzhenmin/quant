/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.trading.impl;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.pb.QotGetSecuritySnapshot.Snapshot;
import com.futu.openapi.web.bean.PriceLine;
import com.futu.openapi.web.bean.Quotation;
import com.futu.openapi.web.service.MAStockService;
import com.futu.openapi.web.service.QuotationService;
import com.futu.openapi.web.trading.MetaphysicalTrading;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhenmin
 * @version $Id: MetaphysicalTrading.java, v 0.1 2025-03-21 17:39 xuxu Exp $$
 */
@Component
public class MetaphysicalTradingImpl implements MetaphysicalTrading {

    @Autowired
    private MAStockService stockService;

    @Autowired
    private QuotationService quotationService;

    private final ConcurrentMap<String, Quotation> cacheMap = Maps.newConcurrentMap();

    @Override
    public Quotation maBacktest(String symbol) {

        if (cacheMap.get(symbol.toUpperCase(Locale.ROOT)) != null) {
            return cacheMap.get(symbol.toUpperCase(Locale.ROOT));
        }

        Integer market = -1;
        String[] sts = symbol.split("\\.");
        if (sts.length > 2) {return Quotation.builder().build();}
        String code = sts[0];
        System.out.println("code :" + code +", market:" + sts[1]);
        if ("SH".equalsIgnoreCase(sts[1])) {
            market = QotMarket.QotMarket_CNSH_Security_VALUE;
        } else if ("SZ".equalsIgnoreCase(sts[1])) {
            market = QotMarket.QotMarket_CNSZ_Security_VALUE;
        } else if ("HK".equalsIgnoreCase(sts[1])) {
            market = QotMarket.QotMarket_HK_Security_VALUE;
        }
        List<PriceLine> priceLineList = stockService.run(code, market);

        List<Snapshot> snapshotList = quotationService.querySnapshot(code, market);

        Quotation quotation = Quotation.builder().priceLines(priceLineList).price52high(snapshotList.get(0).getBasic()
            .getHighest52WeeksPrice()).price52low(snapshotList.get(0).getBasic().getLowest52WeeksPrice()).build();
        cacheMap.put(symbol.toUpperCase(Locale.ROOT), quotation);
        return quotation;
    }
}