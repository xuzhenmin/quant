/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.sink;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;

import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.run.util.msg.DingMsg;
import com.futu.openapi.trade.run.util.msg.MsgContent;
import com.futu.openapi.trade.run.util.msg.MsgUtil;
import com.futu.openapi.trade.run.util.oss.OssClient;

/**
 * @author zhenmin
 * @version $Id: Sink2Ding.java, v 0.1 2024-05-10 19:45 xuxu Exp $$
 */
public class Sink2Ding {

    //统计
    private static final String ANALYSIS_STATISTIC_FILE_NAME = Constants.ANALYSIS_STATISTIC_FILE_NAME;

    //趋势股票每日统计
    private static final String MARKET_PEAK_TREND_FILE_NAME = Constants.MARKET_PEAK_TREND_FILE_NAME;

    //周一市场抽样涨跌趋势
    public static final String MARKET_TREND_FILE_NAME = Constants.MARKET_TREND_FILE_NAME;

    public static void sendMsg() {

        MsgUtil.sendMsg(buildMsg());
    }

    private static DingMsg buildMsg() {

        DingMsg dingMsg = DingMsg.builder().appId(64469).msgType(2).msgTitle("Good stocks test").msgContent(
            JSON.toJSONString(MsgContent.builder().title(
                "Stock Analysis").msgType("markdown").text(buildMsgContent()).build())).receivers(
            "https://oapi.dingtalk"
                + ".com/robot/send?access_token=ec8f9b3879ca933d6fd1b0d59a5784ca8a8fa454afe3ee36428b408dd51f89e4")
            .build();

        return dingMsg;
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static String buildMsgContent() {

        StringBuffer buffer = new StringBuffer("<font color=\"#ff6633\">今日股市分析报告.</font>");
        buffer.append("日期:").append(sdf.format(new Date())).append("<br>");

        buffer.append("今日重点关注股票:");
        String downloadUrl = OssClient.newInstance().generateUrl(ANALYSIS_STATISTIC_FILE_NAME);
        buffer.append("[详细列表](").append(downloadUrl).append(")").append("<br>");
        buffer.append("近期重点关注股票趋势:<br>");
        buffer.append("![screenshot](").append(OssClient.newInstance().generateUrl(MARKET_PEAK_TREND_FILE_NAME)).append(
            ")");
        buffer.append("近期周一涨跌趋势:<br>");

        buffer.append("![screenshot](").append(OssClient.newInstance().generateUrl(MARKET_TREND_FILE_NAME)).append(")");
        return buffer.toString();
    }

}