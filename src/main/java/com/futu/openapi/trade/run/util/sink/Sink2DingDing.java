/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.sink;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.run.util.oss.OssClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Ding ID:ding3lvipljv3otq1usv
 *
 * @author zhenmin
 * @version $Id: Sink2DingDing.java, v 0.1 2025-04-23 14:55 xuxu Exp $$
 */
public class Sink2DingDing {

    private static final String DINGTALK_WEBHOOK_URL
        = "https://oapi.dingtalk"
        + ".com/robot/send?access_token=49ea8f4b59bb5715cc0f9619ba58120fbafc7847c732716ef17c24cb3d00d503";

    //private static final String DINGTALK_WEBHOOK_URL = "https://oapi.dingtalk.com/robot/send?access_token=%s";
    private static final String APP_KEY = "ding3lvipljv3otq1usv";
    private static final String APP_SECRET = "SF2mD6rGJXG3tnA5_wf8d4PoA7crH0ZvRSLAxEVsM2xHPcF4wihHwq5DxKYoCBc4";
    private static final String GET_TOKEN_URL = "https://oapi.dingtalk.com/gettoken?appkey=%s&appsecret=%s";
    private static final String SEND_GROUP_MESSAGE_URL
        = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=%s";

    private static String accessToken;
    private static long tokenExpireTime;

    /**
     * 获取accessToken
     *
     * @return accessToken
     */
    public static String getAccessToken() {
        // 如果token未过期，直接返回
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        try {
            HttpClient httpClient = HttpClients.createDefault();
            String url = String.format(GET_TOKEN_URL, APP_KEY, APP_SECRET);
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);

            if (responseJson.getInteger("errcode") == 0) {
                accessToken = responseJson.getString("access_token");
                // 设置token过期时间，提前5分钟过期
                tokenExpireTime = System.currentTimeMillis() + (responseJson.getLong("expires_in") - 300) * 1000;
                return accessToken;
            } else {
                throw new RuntimeException("Failed to get access token: " + responseJson.getString("errmsg"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get access token", e);
        }
    }

    /**
     * 发送文本消息到钉钉群
     *
     * @param content 消息内容
     * @return 是否发送成功
     */
    public static boolean sendTextMessage(String content) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(DINGTALK_WEBHOOK_URL);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");

            // 构建消息体
            JSONObject message = new JSONObject();
            message.put("msgtype", "text");

            JSONObject text = new JSONObject();
            text.put("content", content);
            message.put("text", text);

            // 设置请求体
            StringEntity entity = new StringEntity(message.toJSONString(), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 发送请求
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            // 解析响应
            JSONObject responseJson = JSON.parseObject(responseBody);
            System.out.println(responseJson);
            return responseJson.getInteger("errcode") == 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送Markdown格式消息到钉钉群
     *
     * @param title   消息标题
     * @param content Markdown格式的消息内容
     * @return 是否发送成功
     */
    public static boolean sendMarkdownMessage(String title) {
        try {
            String content = buildMsgContent();

            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(DINGTALK_WEBHOOK_URL);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");

            // 构建消息体
            JSONObject message = new JSONObject();
            message.put("msgtype", "markdown");

            JSONObject markdown = new JSONObject();
            markdown.put("title", title);
            markdown.put("text", content);
            message.put("markdown", markdown);

            // 设置请求体
            StringEntity entity = new StringEntity(message.toJSONString(), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 发送请求
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            // 解析响应
            JSONObject responseJson = JSON.parseObject(responseBody);
            return responseJson.getInteger("errcode") == 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static String buildMsgContent() {

        StringBuffer buffer = new StringBuffer("<font color=\"#ff6633\">今日股市分析报告.</font>");
        buffer.append("日期:").append(sdf.format(new Date())).append("<br>");

        buffer.append("今日重点关注股票:");
        String downloadUrl = OssClient.newInstance().generateUrl(Constants.getAnalysisStatisticFileName());
        buffer.append("[详细列表](").append(downloadUrl).append(")").append("<br>");
        buffer.append("近期重点关注股票趋势:<br>");
        buffer.append("![screenshot](").append(
            OssClient.newInstance().generateUrl(Constants.getMarketPeakTrendFileName())).append(
            ")");
        buffer.append("近期周一涨跌趋势:<br>");

        buffer.append("![screenshot](").append(OssClient.newInstance().generateUrl(Constants.getMarketTrendFileName()))
            .append(")");
        return buffer.toString();
    }
}
