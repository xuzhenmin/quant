/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhenmin
 * @version $Id: CapitalDistribution.java, v 0.1 2022-04-21 15:03 xuxu Exp $$
 */
@Getter
@Setter
@Builder
public class CapitalDistribution {
    private double capitalInBig;
    private double capitalInMid;
    private double capitalInSmall;
    private double capitalOutBig;
    private double capitalOutMid;
    private double capitalOutSmall;
    private String updateTime;
    private double updateTimestamp;
}