/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.service.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.futu.openapi.web.bean.EstimatePrice;

/**
 * @author zhenmin
 * @version $Id: PredictPrice.java, v 0.1 2025-03-24 17:56 xuxu Exp $$
 */
public class PredictPriceService {

    /**
     * 估算X、Y周期均线交叉点
     *
     * @param closePrices
     * @param periodY
     * @param periodX
     */
    public static EstimatePrice predict(List<Double> closePrices, int periodY, int periodX) {

        //复制下一个价格
        mockNextClosePrice(closePrices);

        //重新计算MA均线
        List<Double> emaY = EMAFunc.calEMAByPeriod(closePrices, periodY);
        List<Double> emaX = EMAFunc.calEMAByPeriod(closePrices, periodX);
        Double xPrice = emaX.get(emaX.size() - 1);
        Double yPrice = emaY.get(emaY.size() - 1);

        //整体上升趋势中
        boolean up = xPrice > yPrice;

        return estimateIntersectionPoint(closePrices, periodY, periodX, up);
    }

    /**
     * 估算上升通道
     *
     * @param closePrices
     * @param periodY
     * @param periodX
     * @return
     */
    private static EstimatePrice estimateIntersectionPoint(List<Double> closePrices, int periodY, int periodX,
                                                           boolean up) {

        //重新计算MA均线
        List<Double> emaY = EMAFunc.calEMAByPeriod(closePrices, periodY);
        List<Double> emaX = EMAFunc.calEMAByPeriod(closePrices, periodX);
        Double xPrice = emaX.get(emaX.size() - 1);
        Double yPrice = emaY.get(emaY.size() - 1);

        //上升趋势结束
        boolean upEnd = up && xPrice <= yPrice;
        //下降趋势结束
        boolean downEnd = !up && xPrice >= yPrice;

        if (downEnd || upEnd) {
            return EstimatePrice.builder().estXPrice(xPrice).estPrice((xPrice + yPrice) / 2).estYPrice(yPrice)
                .estClosePrice(closePrices.get(closePrices.size() - 1)).build();
        } else {
            mockNextClosePrice(closePrices, up);
            return estimateIntersectionPoint(closePrices, periodY, periodX, up);
        }

    }

    /**
     * 模拟价格
     *
     * @param closePrices
     * @param up          上升通道
     */
    private static void mockNextClosePrice(List<Double> closePrices, boolean up) {

        int count = closePrices.size() - 1;
        if (up) {
            Double closePrice = closePrices.get(count);
            closePrice = closePrice * 0.9999;
            closePrices.remove(count);
            closePrices.add(closePrice);
        } else {
            Double closePrice = closePrices.get(count);
            closePrice = closePrice * 1.0001;
            closePrices.remove(count);
            closePrices.add(closePrice);
        }
    }

    /**
     * 新增下一个时间点
     *
     * @param closePrices
     */
    private static void mockNextClosePrice(List<Double> closePrices) {
        //模拟新增一个成交点位
        closePrices.add(closePrices.get(closePrices.size() - 1));
    }

}