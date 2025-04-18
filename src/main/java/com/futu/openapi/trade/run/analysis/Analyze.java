/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.analysis;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhenmin
 * @version $Id: Analyze.java, v 0.1 2024-05-09 13:47 xuxu Exp $$
 */
@Data
@Builder
public class Analyze {

    private String date;

    private String beginDate;

    private Double recentlyChangePercent;

}