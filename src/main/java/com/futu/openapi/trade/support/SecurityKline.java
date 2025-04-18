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
import com.google.common.collect.Lists;

/**
 * @author zhenmin
 * @version $Id: SecurityKline.java, v 0.1 2022-03-18 4:49 下午 xuxu Exp $$
 */
public class SecurityKline extends BaseDaemon {

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
            System.err.printf("queryKline err: retType=%d msg=%s\n", rsp == null ? null : rsp.getRetType(),
                rsp == null ? null : rsp.getRetMsg());
            System.out.println("queryKline error : " + sec.getCode() + " , " + sec.getMarket());
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
    public List<KLine> queryKline(QotCommon.Security sec, int offset,int days, int limit) throws Exception {

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
            System.err.printf("queryKline err: retType=%d msg=%s\n", rsp == null ? null : rsp.getRetType(),
                rsp == null ? null : rsp.getRetMsg());
            System.out.println("queryKline error : " + sec.getCode() + " , " + sec.getMarket());
        } else {
            kLines = rsp.getS2C().getKlListList();
        }
        return kLines;
    }

}