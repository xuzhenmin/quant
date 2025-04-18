/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.msg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.*;

/**
 * @author zhenmin
 * @version $Id: MsgUtil.java, v 0.1 2024-04-23 17:45 xuxu Exp $$
 */
public class MsgUtil {

    public static String getToken() {

        String token = "";

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(
            "https://robot.alibaba-inc.com/robot/api/getToken.json");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

        // 准备数据：参数列表
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("apiName", "dingRobotMessageSend.json"));
        urlParameters.add(new BasicNameValuePair("identity", "{\"type\":1,\"appKey\":\"931993af-a326-4242-847b"
            + "-f323304bdd84\"}"));

        HttpResponse response;
        try {
            // 对POST数据进行包装
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(result);
                token = JSON.parseObject(result).getJSONObject("fields").getString("token");
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return token;

    }

    public static void sendMsg(DingMsg dingMsg) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(
            "https://robot.alibaba-inc.com/robot/api/open/dingRobotMessageSend.json?"
                + "token=" + getToken());
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");

        String textMsg = JSON.toJSONString(dingMsg);

        StringEntity se = new StringEntity(textMsg, "utf-8");
        httpPost.setEntity(se);

        HttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            System.out.println(JSON.toJSONString(response.getStatusLine()));
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(result);
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}