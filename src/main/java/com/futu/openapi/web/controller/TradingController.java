/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.controller;

import com.futu.openapi.web.bean.Quotation;
import com.futu.openapi.web.trading.MetaphysicalTrading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhenmin
 * @version $Id: TradingController.java, v 0.1 2025-03-26 19:33 xuxu Exp $$
 */
@Controller
public class TradingController {

    private final static Logger LOGGER = LoggerFactory
        .getLogger(TradingController.class);

    @Autowired
    private MetaphysicalTrading metaphysicalTrading;

    @RequestMapping("/home")
    public String home() {
        return "home"; // 对应 /WEB-INF/views/home.jsp
    }

    @RequestMapping("/trading")
    public String trading() {

        return "trading.html"; // 对应 /WEB-INF/views/home.jsp
    }

    @GetMapping("/ajax/queryLines")
    public @ResponseBody
    Object queryLines(@RequestParam String symbol) {

        Quotation quotation = metaphysicalTrading.maBacktest(symbol);

        LOGGER.info("TradingController quotation:{}", quotation);

        return quotation;
    }

}