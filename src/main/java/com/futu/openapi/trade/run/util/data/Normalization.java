/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.data;

import java.util.Arrays;

/**
 * @author zhenmin
 * @version $Id: Normalization.java, v 0.1 2025-02-21 10:08 xuxu Exp $$
 */
public class Normalization {

    /**
     * 将数组归一化到 [0, 1] 范围
     *
     * @param data 原始数组
     * @return 归一化后的数组（若输入无效或全相同值返回全0数组）
     */
    public static Double[] normalize(Double[] data) {
        if (data == null || data.length == 0) {
            return new Double[0];
        }

        // 1. 找到最大值和最小值
        Double min = Double.MAX_VALUE;
        Double max = -Double.MAX_VALUE;
        for (Double value : data) {
            if (value < min) { min = value; }
            if (value > max) { max = value; }
        }

        // 2. 处理所有值相同的情况
        if (min == max) {
            Double[] result = new Double[data.length];
            Arrays.fill(result, 0.0); // 统一设为0或根据需求调整
            return result;
        }

        // 3. 计算归一化值
        Double range = max - min;
        Double[] normalized = new Double[data.length];
        for (int i = 0; i < data.length; i++) {
            normalized[i] = (data[i] - min) / range;
        }

        return normalized;
    }
}