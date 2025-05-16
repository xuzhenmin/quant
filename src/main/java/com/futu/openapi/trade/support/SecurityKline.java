/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.futu.openapi.pb.Common;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.futu.openapi.trade.base.BaseDaemon;
import com.futu.openapi.trade.base.ConStatusEnum;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author zhenmin
 * @version $Id: SecurityKline.java, v 0.1 2022-03-18 4:49 下午 xuxu Exp $$
 */
public class SecurityKline extends BaseDaemon {

    private static final Logger LOGGER = LogManager.getLogger(SecurityKline.class);

    private static SecurityKline securityKline;

    public static synchronized SecurityKline newInstance() {
        if (securityKline == null) {
            securityKline = new SecurityKline();
            securityKline.createCon();
        }
        return securityKline;
    }

    private SecurityKline() {}

    /**
     * query kline
     *
     * @param sec
     * @return
     * @throws Exception
     */
    public List<KLine> queryKline(QotCommon.Security sec, int days, int limit) throws Exception {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusDays(days);
        QotRequestHistoryKL.Response rsp = requestHistoryKLSync(sec, QotCommon.KLType.KLType_Day,
            QotCommon.RehabType.RehabType_Forward,
            startDate.format(dateFormatter),
            now.format(dateFormatter),
            limit,
            null,
            new byte[] {},
            false);

        List<KLine> kLines = Lists.newArrayList();
        if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            LOGGER.error("queryKline err: code:{},market:{},rsp={} ,msg:{}", sec.getCode(), sec.getMarket(), rsp,
                rsp == null ? null : rsp.getRetMsg());
            //尝试重新启动链接
            if (rsp == null && securityKline.qotConnStatus != ConStatusEnum.READY) {
                //重新创建链接
                securityKline.createCon();
            }
        } else {
            kLines = rsp.getS2C().getKlListList();
        }
        return kLines;
    }

    /**
     * query kline
     *
     * @param sec
     * @return
     * @throws Exception
     */
    public List<KLine> queryKline(QotCommon.Security sec, int offset, int days, int limit) throws Exception {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        now = now.plusDays(offset);
        LocalDate startDate = now.minusDays(days);
        QotRequestHistoryKL.Response rsp = requestHistoryKLSync(sec, QotCommon.KLType.KLType_Day,
            QotCommon.RehabType.RehabType_Forward,
            startDate.format(dateFormatter),
            now.format(dateFormatter),
            limit,
            null,
            new byte[] {},
            false);

        List<KLine> kLines = Lists.newArrayList();
        if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            LOGGER.error("queryKline err: code:{},market:{},rsp={} ", sec.getCode(), sec.getMarket(), rsp);
        } else {
            kLines = rsp.getS2C().getKlListList();
        }
        return kLines;
    }

}
