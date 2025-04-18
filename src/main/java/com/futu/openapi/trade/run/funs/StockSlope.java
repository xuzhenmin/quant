/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run.funs;

import java.util.List;

import com.futu.openapi.trade.run.funs.meta.SlopeData;
import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * @author zhenmin
 * @version $Id: StockSlope.java, v 0.1 2025-02-20 15:21 xuxu Exp $$
 */
public class StockSlope {

    // 计算斜率（基于线性回归）
    public static List<SlopeData> calculateSlope(List<Double> closePrices, int period, int window) {

        List<SlopeData> dataList = combineSlopData(closePrices, period);

        for (int i = 0; i < dataList.size(); i++) {
            if (i < window - 1) {
                setSlope(dataList.get(i), null);
                continue;
            }

            SimpleRegression regression = new SimpleRegression();
            for (int j = 0; j < window; j++) {
                int index = i - window + 1 + j;
                Double value = dataList.get(index).getEmaX();
                regression.addData(j, value);
            }

            if (regression == null) {
                setSlope(dataList.get(i), null);
            } else {
                setSlope(dataList.get(i), regression.getSlope());
            }
        }
        return dataList;
    }

    private static List<SlopeData> combineSlopData(List<Double> closePrices, int period) {

        List<SlopeData> slopeData = Lists.newArrayList();

        List<Double> emaX = EMA.calculateEMA(closePrices, period);

        for (int i = 0; i < closePrices.size(); i++) {
            SlopeData data = new SlopeData(closePrices.get(i));
            data.setEmaX(emaX.get(i));
            slopeData.add(data);
        }
        return slopeData;
    }

    private static void setSlope(SlopeData data, Double slope) {
        data.setEmaXSlope(slope);
    }

}