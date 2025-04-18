/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.msg;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhenmin
 * @version $Id: MsgContent.java, v 0.1 2024-05-08 14:52 xuxu Exp $$
 */
@Builder
@Setter
@Getter
public class MsgContent {

    private String msgType;

    private String title;

    private String text;

    private String singleTitle;

    private String singleURL;

}