/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.base;

import com.google.protobuf.GeneratedMessageV3;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhenmin
 * @version $Id: ConReqInfo.java, v 0.1 2022-03-07 10:46 上午 xuxu Exp $$
 */
@Getter
@Setter
public class ConReqInfo {

    private int protoID;
    private Object syncEvent;
    private GeneratedMessageV3 rsp;

    ConReqInfo(int protoID, Object syncEvent) {
        this.protoID = protoID;
        this.syncEvent = syncEvent;
    }
}