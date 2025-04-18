/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run.funs;

import java.util.List;

/**
 * @author zhenmin
 * @version $Id: SMA.java, v 0.1 2025-02-20 15:25 xuxu Exp $$
 */
public class SMA {

    // 计算SMA（简单移动平均）
    public static Double calculateSMA(List<Double> prices, int period) {

        double sma = 0.0d;
        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                break;
            }
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) {
                sum += prices.get(j);
            }
            sma = sum / period;
        }
        return sma;
    }
}