/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run.funs;

import java.util.ArrayList;
import java.util.List;

import com.futu.openapi.trade.run.util.data.DataUtil;

/**
 * @author zhenmin
 * @version $Id: EMA.java, v 0.1 2025-02-13 20:59 xuxu Exp $$
 */
public class EMA {

    /**
     * 计算股票的指数移动平均线（EMA）
     *
     * @param prices 股票价格列表，按时间顺序排列
     * @param period EMA计算周期
     * @return 与输入列表长度相同的EMA值列表，前period-1个位置为Double.NaN表示无效值
     */
    public static List<Double> calculateEMA(List<Double> prices, int period) {
        List<Double> emaValues = new ArrayList<>();
        int dataSize = prices.size();

        // 处理数据不足的情况
        if (dataSize < period) {
            for (int i = 0; i < dataSize; i++) {
                emaValues.add(Double.NaN);
            }
            return emaValues;
        }

        // 填充前period-1个位置为NaN
        for (int i = 0; i < period - 1; i++) {
            emaValues.add(Double.NaN);
        }

        // 计算初始SMA（简单移动平均）
        double sma = 0.0;
        for (int i = 0; i < period; i++) {
            sma += prices.get(i);
        }
        sma /= period;
        emaValues.add(sma);

        // 计算乘数和后续EMA
        double multiplier = 2.0 / (period + 1.0);
        for (int i = period; i < dataSize; i++) {
            double currentPrice = prices.get(i);
            double prevEMA = emaValues.get(i - 1);
            double ema = (currentPrice * multiplier) + (prevEMA * (1 - multiplier));
            emaValues.add(Double.parseDouble(DataUtil.numFormat("#.000", ema)));
        }

        return emaValues;
    }

}