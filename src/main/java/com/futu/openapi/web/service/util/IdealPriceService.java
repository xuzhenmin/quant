/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.service.util;

import java.util.List;

import com.futu.openapi.trade.run.funs.StockSlope;
import com.futu.openapi.trade.run.funs.meta.SlopeData;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.trade.run.util.data.Normalization;
import com.futu.openapi.web.bean.PriceLine;
import com.google.common.collect.Lists;

/**
 * @author zhenmin
 * @version $Id: IdealPrice.java, v 0.1 2025-03-24 19:24 xuxu Exp $$
 */
public class IdealPriceService {

    private static final String PATTERN_PRICE = "#.0000";

    /**
     * 计算期望价格
     *
     * @param emaPrice
     * @param emaX
     * @param emaY
     * @param closePrices
     * @param slopPeriod
     * @param window
     */
    public static PriceLine calIdealPrice(Double emaPrice, List<Double> emaX, List<Double> emaY,
                                          List<Double> closePrices, List<Double> lowPrices,
                                          int slopPeriod, int window) {

        emaPrice = DataUtil.doubleFormat(PATTERN_PRICE, emaPrice);
        Double endPrice = DataUtil.doubleFormat(PATTERN_PRICE, closePrices.get(closePrices.size() - 1));  //363
        Double closePrice = closePrices.get(closePrices.size() - 2);  // 494

        Double lowPrice = lowPrices.get(lowPrices.size() - 1);  //

        Double xPrice = emaX.get(emaX.size() - 1); //463
        Double yPrice = emaY.get(emaY.size() - 1); //445

        //System.out.println("closePrices:" + closePrices);

        //System.out.println(
        //    "emaPrice:" + emaPrice + ",endPrice:" + DataUtil.numFormat("#.0000", endPrice) + ",closePrice:" +
        //        closePrice
        //        + ",xPrice:" + xPrice
        //        + ",yPrice:" + yPrice);
        Double consPrice = DataUtil.doubleFormat(PATTERN_PRICE,
            conservativeAlgorithm(emaPrice, endPrice, closePrice, xPrice, yPrice));
        Double radicalPrice = DataUtil.doubleFormat(PATTERN_PRICE,
            radicalAlgorithm(emaPrice, endPrice, closePrice, xPrice, yPrice));
        Double slopePrice = DataUtil.doubleFormat(PATTERN_PRICE,
            slopeIdealPrice(emaPrice, endPrice, closePrice, xPrice, yPrice, closePrices, slopPeriod,
                window));

        //System.out.println("consPrice:" + DataUtil.numFormat("#.0000", consPrice) + ", radicalPrice:" + DataUtil
        //    .numFormat("#.0000", radicalPrice) + ", slopePrice:" + DataUtil.numFormat("#.0000", slopePrice));

        return PriceLine.builder().consPrice(consPrice).radicalPrice(radicalPrice).slopePrice(slopePrice).nextPrice(
            emaPrice).x(xPrice).y(yPrice).closePrice(closePrice).lowPrice(lowPrice).endPrice(endPrice).build();
    }

    /**
     * 加斜率
     *
     * @param closePrices
     * @param slopPeriod  20   斜率依赖数据区间
     * @param window      5
     */
    private static Double slopeIdealPrice(Double emaPrice, Double endPrice, Double closePrice, Double xPrice,
                                          Double yPrice, List<Double> closePrices, int slopPeriod, int window) {
        if (closePrice.equals(emaPrice)) {
            return closePrice;
        }

        List<SlopeData> dataList = StockSlope.calculateSlope(closePrices, slopPeriod, window);

        //System.out.println(dataList.get(dataList.size() - 1));
        dataList = dataList.subList(dataList.size() - slopPeriod, dataList.size());

        List<Double> normSlopes = normalization(dataList);

        Double normSlope = normSlopes.get(normSlopes.size() - 1);
        //x、y均线价格相交、向下穿
        if (closePrice - emaPrice <= 0) {
            return emaPrice + normSlope * (closePrice - endPrice);
        }
        return emaPrice + normSlope * ((xPrice - yPrice) / (closePrice - emaPrice)) * (closePrice - endPrice);

    }

    private static List<Double> normalization(List<SlopeData> dataList) {

        List<Double> slopeList = Lists.newArrayList();
        dataList.forEach(slopeData -> {
            slopeList.add(slopeData.getEmaXSlope());
        });
        //System.out.println("slopeList :" + slopeList);
        final List<Double> result = Lists.newArrayList();

        Lists.newArrayList(Normalization.normalize(slopeList.toArray(new Double[] {}))).forEach(aDouble -> {
            if (!aDouble.isNaN()) {
                result.add(Double.parseDouble(DataUtil.numFormat(PATTERN_PRICE, aDouble)));
            } else {
                result.add(aDouble);
            }
        });
        return result;
    }

    //保守
    private static Double conservativeAlgorithm(Double emaPrice, Double endPrice, Double closePrice, Double xPrice,
                                                Double yPrice) {
        if (closePrice.equals(emaPrice)) {
            return closePrice;
        }
        //x、y均线价格相交、向下穿
        if (closePrice - emaPrice <= 0) {
            return emaPrice;
        }
        return emaPrice + ((xPrice - yPrice) / (closePrice - emaPrice)) * (closePrice - endPrice);
    }

    //激进
    private static Double radicalAlgorithm(Double emaPrice, Double endPrice, Double closePrice, Double xPrice,
                                           Double yPrice) {
        if (closePrice.equals(endPrice)) {
            return closePrice;
        }
        return emaPrice + (1 - (xPrice - yPrice) / (closePrice - endPrice)) * (closePrice - emaPrice);

    }

}