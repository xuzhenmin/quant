/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.service.util;

import java.util.List;

import com.futu.openapi.trade.run.funs.EMA;

/**
 * @author zhenmin
 * @version $Id: EMAFunc.java, v 0.1 2025-03-24 17:57 xuxu Exp $$
 */
public class EMAFunc {

    /**
     * 基于周期计算平均线
     *
     * @param closePrices
     * @param period
     * @return
     */
    public static List<Double> calEMAByPeriod(List<Double> closePrices, int period) {
        List<Double> emaX = EMA.calculateEMA(closePrices, period);
        return emaX.subList(emaX.size() - period, emaX.size());
    }
}