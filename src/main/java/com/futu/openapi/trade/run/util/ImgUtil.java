///**
// * Alipay.com Inc.
// * Copyright (c) 2004-2024 All Rights Reserved.
// */
//package com.futu.openapi.trade.run.util;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.alibaba.fastjson.JSON;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.FileEntity;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//
///**
// * @author zhenmin
// * @version $Id: ImgUtil.java, v 0.1 2024-04-26 17:29 xuxu Exp $$
// */
//public class ImgUtil {
//
//    private static final String TOKEN = "99|Cn4vt1YLfHmgOxJbq2nj3K18KlHU1thE6MTtNtur";
//
//    public static void loadImg() {
//
//        //http://mhimg.cn/page/api-docs.html
//        HttpClient httpClient = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost(
//            "http://mhimg.cn/api/v1/upload");
//        httpPost.addHeader("Authorization", TOKEN);
//        httpPost.addHeader("Accept", "application/json");
//        httpPost.addHeader("Content-Type", "multipart/form-data; charset=utf-8");
//
//        // 准备数据：参数列表
//        List<NameValuePair> urlParameters = new ArrayList<>();
//        urlParameters.add(new BasicNameValuePair("token", TOKEN));
//        urlParameters.add(new BasicNameValuePair("permission", "1"));
//        urlParameters.add(new BasicNameValuePair("expired_at", "2030-01-01"));
//
//        FileEntity fileEntity = new FileEntity(new File("./data/20240426/market-trend-20240426.png"),
//            ContentType.DEFAULT_BINARY);
//
//        HttpResponse response;
//        try {
//            urlParameters.add(new BasicNameValuePair("file", EntityUtils.toString(fileEntity, Charset.defaultCharset())));
//
//            // 对POST数据进行包装
//            //httpPost.setEntity(fileEntity);
//            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
//
//
//
//            response = httpClient.execute(httpPost);
//            System.out.println(JSON.toJSONString(response.getStatusLine()));
//            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                String result = EntityUtils.toString(response.getEntity(), "utf-8");
//                System.out.println(result);
//            }
//        } catch (ClientProtocolException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//}