/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.strategy;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.DateUtil;
import com.futu.openapi.trade.run.util.GraphUtil;
import com.futu.openapi.trade.run.util.IOUtil;
import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.run.util.oss.OssClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 牛市：周一涨 高概率
 * 熊市：周一跌 高概率，周三涨
 *
 * @author zhenmin
 * @version $Id: BearBullJudge.java, v 0.1 2024-04-12 14:49 xuxu Exp $$
 */
public class BearBullJudge {

    private static final String BULL_JUDGE_FILE = Constants.BULL_JUDGE_FILE;

    //市场趋势图
    private static final String MARKET_TREND_FILE = Constants.MARKET_TREND_FILE;

    //文件名称
    private static final String MARKET_TREND_FILE_NAME = Constants.MARKET_TREND_FILE_NAME;

    public static boolean judge(Map<CodeInfo, List<KLine>> kLineMap) {

        Map<CodeInfo, List<Double>> changeRates = Maps.newConcurrentMap();

        List<String> times = Lists.newArrayList();
        kLineMap.forEach((codeInfo, kLines) -> {
            List<Double> doubles = Lists.newArrayList();
            boolean needAdd = times.size() < 1;
            kLines.forEach(kLine -> {
                if (DateUtil.whatsDay(kLine.getTime()) == 2) {
                    doubles.add(Double.parseDouble(new DecimalFormat("0.00").format(kLine.getChangeRate())));
                    try {
                        if (needAdd) {
                            times.add(new SimpleDateFormat("MMdd")
                                .format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(kLine.getTime())));
                        }
                    } catch (Exception e) {e.printStackTrace();}
                }
            });
            changeRates.put(codeInfo, doubles);
        });

        final List<Double> riseRateList = Lists.newArrayList();

        final List<String> stringList = Lists.newArrayList();
        changeRates.forEach((codeInfo, doubles) -> {

            stringList.add(
                codeInfo.getCode() + "," + codeInfo.getName() + JSON.toJSONString(doubles) + ",count:" + doubles.size()
                    + ",riseRate:" + riseRate(doubles, 0d));
            riseRateList.add(riseRate(doubles, 0d));
        });
        IOUtil.write(BULL_JUDGE_FILE, stringList);
        IOUtil.write(BULL_JUDGE_FILE, Lists.newArrayList("整体胜率:" + riseRate(riseRateList, 0.5d)), true);

        //监控绘图
        riseMonitor(times, changeRates);

        return riseRate(riseRateList, 0.5d) > Double.parseDouble(PropUtil.getProp("bearBullRate"));
    }

    private static void riseMonitor(List<String> times, Map<CodeInfo, List<Double>> changeRates) {

        List<Double> riseRateList = Lists.newArrayList();

        Map<Integer, List<Double>> doubleMap = Maps.newConcurrentMap();

        changeRates.forEach((codeInfo, doubles) -> {

            for (int i = 0; i < doubles.size(); i++) {

                List<Double> doubleList = doubleMap.get(i);
                if (doubleList == null) {
                    doubleList = Lists.newArrayList();
                } else {doubleList.add(doubles.get(i));}
                doubleMap.put(i, doubleList);
            }

        });

        doubleMap.forEach((integer, doubles) -> {
            riseRateList.add(riseRate(doubles, 0d));
        });

        //绘图
        //GraphUtil.drawLine("Bull&Bear", "MonDayRiseRate", "time", "rate", times,
        //    riseRateList);

        GraphUtil.createNewLineChartForPng("Market Trend", "MondayRiseRate", "time", "rate", MARKET_TREND_FILE, times,
            riseRateList, 1024, 768);

        //upload oss
        OssClient.newInstance().loadData(MARKET_TREND_FILE, MARKET_TREND_FILE_NAME);

    }

    private static Double riseRate(List<Double> list, Double stand) {

        if (list.size() < 1) {
            return 0d;
        }
        final AtomicInteger riseCount = new AtomicInteger();
        list.forEach(aDouble -> {
            if (aDouble > stand) {
                riseCount.getAndIncrement();
            }
        });

        return Double.parseDouble(new DecimalFormat("0.00").format((double)riseCount.get() / list.size()));

    }

}