/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.msg;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * String textMsg = "{\"appId\":64469,\"msgType\":2,\"msgTitle\":\"这是测试消息\",\"msgContent\":\"{  \\\"msgType\\\":"
 * + " \\\"markdown\\\",  \\\"title\\\": \\\"乔布斯\\\",  \\\"text\\\": \\\"#### 乔布斯 20 年前想打造的苹果咖啡厅 \\nApple "
 * + "Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划 ![screenshot](https://img.alicdn"
 * + ".com/tfs/TB14VDlpCzqK1RjSZPcXXbTepXa-962-446.png)\\\",  \\\"singleTitle\\\": \\\"点击查看更多天气\\\",  "
 * + "\\\"singleURL\\\": \\\"http://www.weather.com.cn\\\"}\",\"dingChatbotId\":\"\","
 * + "\"receivers\":\"https://oapi.dingtalk"
 * + ".com/robot/send?access_token=ec8f9b3879ca933d6fd1b0d59a5784ca8a8fa454afe3ee36428b408dd51f89e4\"}";
 *
 * @author zhenmin
 * @version $Id:DingMsg.java,v0.1 2024-05-08 14:41xuxu Exp $$
 */

@Builder
@Setter
@Getter
public class DingMsg {

    private long appId;

    private int msgType;

    private String msgTitle;

    private String msgContent;

    private String receivers;

    private String dingChatbotId;

}