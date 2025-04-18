/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.service;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.futu.openapi.pb.QotGetCapitalFlow.CapitalFlowItem;
import com.futu.openapi.trade.model.CapitalDistribution;
import com.futu.openapi.trade.support.CapitalFlow;
import com.google.protobuf.util.JsonFormat;
import org.springframework.stereotype.Component;

/**
 * @author zhenmin
 * @version $Id: CapitalService.java, v 0.1 2022-04-21 15:10 xuxu Exp $$
 */
@Component
public class CapitalService {

    /**
     * 资金流
     *
     * @param code
     * @param market
     * @return
     * @throws Exception
     */
    public List<CapitalFlowItem> queryCapitalFlow(String code, int market) throws Exception {

        CapitalFlow capitalFlow = CapitalFlow.newInstance();

        List<CapitalFlowItem> capitalFlowItems = capitalFlow.queryCapitalFlow(code, market);

        for (CapitalFlowItem capitalFlowItem : capitalFlowItems) {
            System.out.println(JsonFormat.printer().print(capitalFlowItem));
        }
        return capitalFlowItems;

    }

    /**
     * 资金分布
     *
     * @param code
     * @param market
     * @return
     * @throws Exception
     */
    public CapitalDistribution queryCapitalDistribution(String code, int market) throws Exception {

        CapitalFlow capitalFlow = CapitalFlow.newInstance();

        CapitalDistribution capitalDistribution = capitalFlow.queryCapitalDistribution(code, market);

        System.out.println(JSON.toJSONString(capitalDistribution));

        return capitalDistribution;
    }

}