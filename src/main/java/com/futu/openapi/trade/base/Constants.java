/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.futu.openapi.trade.run.util.PropUtil;

/**
 * @author zhenmin
 * @version $Id: Constants.java, v 0.1 2024-05-08 14:32 xuxu Exp $$
 */
public class Constants {

    public static final String BULL_JUDGE_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + "/bull-judge-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".txt";

    //市场趋势图
    public static final String MARKET_TREND_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + "/market-trend-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".png";

    public static final String MARKET_PEAK_TREND_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + "/market-peak-trend-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".png";

    public static final String MARKET_PEAK_TREND_FILE_NAME = "market-peak-trend-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".png";

    //文件名称
    public static final String MARKET_TREND_FILE_NAME = "market-trend-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".png";

    public static final String KLINE_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/kline-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".txt";

    public static final String CAPITAL_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + "/capital-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".txt";

    public static final String ANALYSIS_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + "/analysis-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".txt";

    public static final String ANALYSIS_STATISTIC_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + "/analysis-statistic-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".txt";

    public static final String ANALYSIS_STATISTIC_FILE_NAME = "analysis-statistic-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".txt";

    public static final String SELECT_STOCK_FILE = "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + "/peak-" + PropUtil.getProp("step") + "-"
        + new SimpleDateFormat("yyyyMMdd").format(new Date())
        + ".txt";

    public static String buildPeakFile(String date) {
        return "./data/" + date
            + "/peak-" + PropUtil.getProp("step") + "-"
            + date
            + ".txt";
    }

}