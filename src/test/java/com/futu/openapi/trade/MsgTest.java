/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade;

import com.alibaba.fastjson.JSON;

import com.futu.openapi.trade.run.util.msg.DingMsg;
import com.futu.openapi.trade.run.util.msg.MsgContent;
import com.futu.openapi.trade.run.util.msg.MsgUtil;
import org.junit.Test;

/**
 * @author zhenmin
 * @version $Id: MsgTest.java, v 0.1 2024-04-23 17:55 xuxu Exp $$
 */
public class MsgTest {

    @Test
    public void testSendMsg() throws Exception {

        DingMsg dingMsg = DingMsg.builder().appId(64469).msgType(2).msgTitle("Good stocks test").msgContent(
            JSON.toJSONString(MsgContent.builder().title(
                "Good Stock List").msgType("markdown").text("#### 乔布斯 20年前想打造的苹果咖啡厅 \\nApple"
                + "Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划 ![screenshot](https://img.alicdn"
                + ".com/tfs/TB14VDlpCzqK1RjSZPcXXbTepXa-962-446.png)").build())).receivers(
            "https://oapi.dingtalk"
                + ".com/robot/send?access_token=ec8f9b3879ca933d6fd1b0d59a5784ca8a8fa454afe3ee36428b408dd51f89e4")
            .build();
        MsgUtil.sendMsg(dingMsg);
    }

    @Test
    public void testGetToken() throws Exception {

        MsgUtil.getToken();
    }
}