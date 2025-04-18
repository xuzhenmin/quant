/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.bean;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhenmin
 * @version $Id: Quotation.java, v 0.1 2025-04-01 20:28 xuxu Exp $$
 */
@Getter
@Setter
@Builder
@ToString
public class Quotation {

    private List<PriceLine> priceLines;

    private Double price52high;

    private Double price52low;
}