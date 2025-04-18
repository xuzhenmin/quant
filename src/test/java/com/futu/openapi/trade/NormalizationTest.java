/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade;

import java.util.Arrays;

import com.futu.openapi.trade.run.util.data.Normalization;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: NormalizationTest.java, v 0.1 2025-02-21 10:09 xuxu Exp $$
 */
public class NormalizationTest {

    @Test
    public void testNormalize() throws Exception {
        // 测试数据
        Double[] test1 = {1.0, 2.0, 3.0, 4.0, 5.0}; // 常规数据
        Double[] test2 = {-5.0, 0.0, 10.0};          // 含负数
        Double[] test3 = {100.0, 100.0, 100.0};     // 全相同值
        Double[] test4 = {};                         // 空数组

        // 执行归一化
        System.out.println("Test1: " + Arrays.toString(Normalization.normalize(test1)));
        System.out.println("Test2: " + Arrays.toString(Normalization.normalize(test2)));
        System.out.println("Test3: " + Arrays.toString(Normalization.normalize(test3)));
        System.out.println("Test4: " + Arrays.toString(Normalization.normalize(test4)));
    }
}