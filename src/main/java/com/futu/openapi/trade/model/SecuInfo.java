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
 * @version $Id: SecuInfo.java, v 0.1 2022-04-20 15:39 xuxu Exp $$
 */

@Setter
@Getter
@Builder
public class SecuInfo {

    public SecuInfo(String code, int market, int secuType, String name) {
        this.code = code;
        this.market = market;
        this.secuType = secuType;
        this.name = name;
    }

    private String code;

    private int market;

    private int secuType;

    private String name;

}