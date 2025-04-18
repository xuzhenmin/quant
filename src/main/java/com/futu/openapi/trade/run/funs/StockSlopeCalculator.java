/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run.funs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * @author zhenmin
 * @version $Id: StockSlopeCalculator.java, v 0.1 2025-02-18 10:14 xuxu Exp $$
 */
public class StockSlopeCalculator {

    // 股票数据结构
    static class StockData {
        double close;
        Double ema12;
        Double ema26;
        Double sma50;
        Double ema12Slope;
        Double ema26Slope;
        Double sma50Slope;

        public StockData(double close) {
            this.close = close;
        }
    }

    public static void main(String[] args) {
        // 1. 模拟股票收盘价数据（可替换为实际数据源）
        List<StockData> stockDataList = new ArrayList<>();
        double[] closes = {150.0, 152.5, 151.8, 153.2, 155.0, 156.3, 154.7, 157.2, 158.5, 159.1};
        for (double close : closes) {
            stockDataList.add(new StockData(close));
        }

        // 2. 计算EMA和SMA
        calculateEMA(stockDataList, 12);  // EMA12
        calculateEMA(stockDataList, 26);  // EMA26
        calculateSMA(stockDataList, 50);  // SMA50（因数据量不足，实际会部分为空）

        // 3. 计算斜率（窗口设为5）
        int window = 5;
        calculateSlope(stockDataList, "ema12", window);
        calculateSlope(stockDataList, "ema26", window);
        calculateSlope(stockDataList, "sma50", window);

        // 4. 输出结果
        StockData latest = stockDataList.get(stockDataList.size() - 1);
        System.out.println("最新斜率：");
        System.out.printf("EMA12斜率: %.2f\n", latest.ema12Slope);
        System.out.printf("EMA26斜率: %.2f\n", latest.ema26Slope);
        System.out.printf("SMA50斜率: %.2f\n", latest.sma50Slope);
    }

    // 计算EMA（指数移动平均）
    public static void calculateEMA(List<StockData> dataList, int span) {
        double alpha = 2.0 / (span + 1);
        double ema = dataList.get(0).close; // 初始EMA为第一个收盘价

        for (int i = 0; i < dataList.size(); i++) {
            if (i == 0) {
                ema = dataList.get(i).close;
            } else {
                ema = dataList.get(i).close * alpha + ema * (1 - alpha);
            }

            if (span == 12) {
                dataList.get(i).ema12 = ema;
            } else if (span == 26) {
                dataList.get(i).ema26 = ema;
            }
        }
    }

    // 计算SMA（简单移动平均）
    public static void calculateSMA(List<StockData> dataList, int period) {
        for (int i = 0; i < dataList.size(); i++) {
            if (i < period - 1) {
                dataList.get(i).sma50 = null; // 数据不足时置空
                continue;
            }

            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) {
                sum += dataList.get(j).close;
            }
            dataList.get(i).sma50 = sum / period;
        }
    }

    // 计算斜率（基于线性回归）
    public static void calculateSlope(List<StockData> dataList, String field, int window) {
        for (int i = 0; i < dataList.size(); i++) {
            if (i < window - 1) {
                setSlope(dataList.get(i), field, null);
                continue;
            }

            SimpleRegression regression = new SimpleRegression();
            for (int j = 0; j < window; j++) {
                int index = i - window + 1 + j;
                Double value = getFieldValue(dataList.get(index), field);
                if (value == null) {
                    regression = null;
                    break;
                }
                regression.addData(j, value);
            }

            if (regression == null) {
                setSlope(dataList.get(i), field, null);
            } else {
                setSlope(dataList.get(i), field, regression.getSlope());
            }
        }
    }

    // 工具方法：根据字段名获取数值
    private static Double getFieldValue(StockData data, String field) {
        switch (field) {
            case "ema12": return data.ema12;
            case "ema26": return data.ema26;
            case "sma50": return data.sma50;
            default: throw new IllegalArgumentException("无效字段");
        }
    }

    // 工具方法：根据字段名设置斜率
    private static void setSlope(StockData data, String field, Double slope) {
        switch (field) {
            case "ema12": data.ema12Slope = slope; break;
            case "ema26": data.ema26Slope = slope; break;
            case "sma50": data.sma50Slope = slope; break;
            default: throw new IllegalArgumentException("无效字段");
        }
    }
}