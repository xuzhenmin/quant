/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run.funs.meta;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhenmin
 * @version $Id: SlopeData.java, v 0.1 2025-02-20 15:23 xuxu Exp $$
 */
@Getter
@Setter
@ToString
public class SlopeData {

    Double close;
    Double emaX;
    Double smaX;
    Double emaXSlope;
    Double smaXSlope;

    public SlopeData(Double close) {
        this.close = close;
    }

}