/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.data;

import java.util.List;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.trade.run.util.CodeInfo;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhenmin
 * @version $Id: KlineData.java, v 0.1 2024-05-09 14:08 xuxu Exp $$
 */
@Data
@Builder
public class KlineData {

    private CodeInfo codeInfo;

    private List<KLine> data;
}