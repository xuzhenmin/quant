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
 * @version $Id: EstimatePrice.java, v 0.1 2025-04-03 17:22 xuxu Exp $$
 */
@Getter
@Setter
@Builder
@ToString
public class EstimatePrice {

    //估算的收盘价
    private Double estClosePrice;

    //估算的MA(X)的价格
    private Double estXPrice;

    //估算的MA(Y)的价格
    private Double estYPrice;

    //估算的交点价格
    private Double estPrice;

}