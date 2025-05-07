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

    public static String getBullJudgeFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + "/bull-judge-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".txt";
    }

    //市场趋势图
    public static String getMarketTrendFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + "/market-trend-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".png";
    }


    public static String getMarketPeakTrendFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + "/market-peak-trend-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".png";
    }

    public static String getMarketPeakTrendFileName() {

        return "market-peak-trend-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".png";
    }

    //文件名称

    public static String getMarketTrendFileName() {

        return "market-trend-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".png";
    }

    public static String getKlineFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/kline-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".txt";
    }

    public static String getCapitalFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + "/capital-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".txt";
    }

    public static String getAnalysisFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + "/analysis-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".txt";
    }

    public static String getAnalysisStatisticFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + "/analysis-statistic-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".txt";
    }

    public static String getAnalysisStatisticFileName() {

        return "analysis-statistic-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".txt";
    }

    public static String getSelectStockFile() {

        return "./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + "/peak-" + PropUtil.getProp("step") + "-"
            + new SimpleDateFormat("yyyyMMdd").format(new Date())
            + ".txt";
    }

    public static String buildPeakFile(String date) {
        return "./data/" + date
            + "/peak-" + PropUtil.getProp("step") + "-"
            + date
            + ".txt";
    }

}
