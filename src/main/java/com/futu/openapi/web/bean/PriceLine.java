/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhenmin
 * @version $Id: PriceLine.java, v 0.1 2025-03-24 19:30 xuxu Exp $$
 */
@Getter
@Setter
@Builder
@ToString
public class PriceLine {

    //日期
    private String date;

    //保守价格
    private Double consPrice;

    //激进价格
    private Double radicalPrice;

    //斜率干扰价格
    private Double slopePrice;

    //预测下一个价格
    private Double nextPrice;

    //x均线价格
    private Double x;

    //y均线价格
    private Double y;

    //收盘价
    private Double closePrice;

    //最低价
    private Double lowPrice;

    //x、y均线交叉时价格
    private Double endPrice;

}