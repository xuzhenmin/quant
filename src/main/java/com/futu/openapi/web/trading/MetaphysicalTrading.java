/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.trading;

import com.futu.openapi.web.bean.Quotation;

/**
 * 1、回测
 * 2、预测
 * 3、找股
 *
 * @author zhenmin
 * @version $Id: MetaphysicalTrading.java, v 0.1 2025-03-21 17:38 xuxu Exp $$
 */
public interface MetaphysicalTrading {

    /**
     * 回测近N天的预测数据
     *
     * @return
     */
    Quotation maBacktest(String symbol);

}