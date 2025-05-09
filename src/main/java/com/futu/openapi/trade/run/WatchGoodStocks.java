/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotCommon.SecurityStaticBasic;
import com.futu.openapi.pb.QotCommon.SecurityType;
import com.futu.openapi.pb.QotGetCapitalFlow.CapitalFlowItem;
import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.model.CapitalDistribution;
import com.futu.openapi.trade.run.strategy.BearBullJudge;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.trade.run.util.FindHigher;
import com.futu.openapi.trade.run.util.IOUtil;
import com.futu.openapi.trade.run.util.Peak;
import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.service.CapitalService;
import com.futu.openapi.trade.support.UserSecurityGroup;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author zhenmin
 * @version $Id: WatchGoodStocks.java, v 0.1 2024-04-03 16:41 xuxu Exp $$
 */
public class WatchGoodStocks {

    static {
        PropUtil.readProp();
    }

    public static void run() {
        System.out.println("Watch start //");

        CodeInfo[] codeInfos = queryCodeInfo();
        System.out.println("分组代码数量：" + codeInfos.length);

        Map<CodeInfo, List<KLine>> lists = DataUtil.loadKLineDirect(codeInfos);

        //Map<CodeInfo, List<CapitalFlowItem>> capitalFlowMap = loadCapitalData(codeInfos);

        //判断牛熊
        boolean isBull = BearBullJudge.judge(lists);

        System.out.println("Is Bull:" + isBull);

        //
        Map<CodeInfo, List<Peak<String, String, String, Double>>> peakMap = Maps.newConcurrentMap();

        lists.forEach((s, kLines) -> {
            List<Peak<String, String, String, Double>> peakPairs = multiPeak(kLines);
            peakMap.put(s, peakPairs);
        });

        List<String> sts = Lists.newArrayList();
        peakMap.forEach((codeInfo, peaks) -> {
            final StringBuffer st = new StringBuffer("symbol:");
            st.append(codeInfo.getCode()).append(",name:").append(codeInfo.getName()).append(",Good:").append(
                FindHigher.checkPeak(peaks, Integer.parseInt(PropUtil.getProp("peak")) - 1));
            peaks.forEach(sp -> st.append(",beginTime:").append(sp.getBegin()).append(",highTime:").append(sp.getEnd())
                .append(",price:").append(sp.getPrice()));
            sts.add(st.toString());
        });
        IOUtil.write(Constants.getSelectStockFile(), sts, false);
    }

    private static final List<String> GROUP_WHITELIST = Lists.newArrayList("量化", "沪深", "港股", "韭菜", "埋伏", "生肖", "提前埋伏",
        "猥琐发育别浪");

    private static final List<String> GROUP_BLACKLIST = Lists.newArrayList("加拿大", "澳洲", "港股期权", "期权", "美股期权", "债券",
        "全部",
        "期货");

    public static List<Peak<String, String, String, Double>> multiPeak(List<KLine> kLines) {

        List<Double> highPrices = Lists.newArrayList();
        List<Double> lowPrices = Lists.newArrayList();
        List<Double> closePrices = Lists.newArrayList();
        List<String> times = Lists.newArrayList();

        for (KLine kline : kLines) {
            highPrices.add(kline.getHighPrice());
            lowPrices.add(kline.getLowPrice());
            closePrices.add(kline.getClosePrice());
            times.add(kline.getTime());
        }

        //findLowStage(lowPrices, times, 10, multiPeak);
        return FindHigher.findHighStage(highPrices, times, Integer.parseInt(PropUtil.getProp("step")),
            Integer.parseInt(PropUtil.getProp("peak")));

    }

    public static List<SecurityStaticBasic> queryUserSecurity(String groupName) {
        List<SecurityStaticBasic> securities = Lists.newArrayList();
        try {
            UserSecurityGroup userSecurityGroup = UserSecurityGroup.newInstance();

            securities = userSecurityGroup.queryUserSecurity(groupName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return securities;
    }

    public static List<String> queryUserGroup() {
        List<String> groups = Lists.newArrayList();
        try {
            UserSecurityGroup userSecurityGroup = UserSecurityGroup.newInstance();
            groups = userSecurityGroup.queryUserSecurityGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    private static final List<Integer> MARKET_BLACK = Lists.newArrayList(QotMarket.QotMarket_JP_Security_VALUE,
        QotMarket.QotMarket_US_Security_VALUE, QotMarket.QotMarket_SG_Security_VALUE);

    private static final List<Integer> SEC_BLACK = Lists.newArrayList(SecurityType.SecurityType_Bond_VALUE,
        SecurityType.SecurityType_Plate_VALUE, SecurityType.SecurityType_Trust_VALUE,
        SecurityType.SecurityType_Unknown_VALUE, SecurityType.SecurityType_Bwrt_VALUE,
        SecurityType.SecurityType_Drvt_VALUE, SecurityType.SecurityType_PlateSet_VALUE);

    private static boolean securityFilter(String code, int market, int secType) {
        if (MARKET_BLACK.contains(market) || SEC_BLACK.contains(secType)) {
            return false;
        }
        return true;
    }

    private static Map<CodeInfo, List<CapitalFlowItem>> loadCapitalData(CodeInfo[] codeInfos) {
        CapitalService capitalService = new CapitalService();
        //capitalService.queryCapitalFlow("002124", QotMarket.QotMarket_CNSZ_Security_VALUE);
        //capitalService.queryCapitalDistribution("002124", QotMarket.QotMarket_CNSZ_Security_VALUE);

        Map<CodeInfo, List<CapitalFlowItem>> capitalMap = Maps.newConcurrentMap();

        Map<CodeInfo, CapitalDistribution> distributionMap = Maps.newConcurrentMap();

        for (int i = 0; i < codeInfos.length; i++) {
            try {
                List<CapitalFlowItem> capitalFlowItems = capitalService.queryCapitalFlow(codeInfos[i].getCode(),
                    codeInfos[i].getMarket());
                capitalMap.put(codeInfos[i], capitalFlowItems);

                DataUtil.writeCapital(capitalFlowItems, codeInfos[i]);

                CapitalDistribution capitalDistribution = capitalService.queryCapitalDistribution(
                    codeInfos[i].getCode(),
                    codeInfos[i].getMarket());
                distributionMap.put(codeInfos[i], capitalDistribution);

            } catch (Exception e) {e.printStackTrace();}

        }

        return capitalMap;

    }

    private static CodeInfo[] queryCodeInfo() {

        List<String> groups = queryUserGroup();
        //System.out.println("groups :" + groups);
        Set<CodeInfo> symbolPool = Sets.newConcurrentHashSet();
        for (String group : groups) {
            if (GROUP_WHITELIST.contains(group)) {
                List<SecurityStaticBasic> securities = queryUserSecurity(group);
                if (securities == null || securities.size() < 1) {
                    continue;
                }
                for (SecurityStaticBasic staticBasic : securities) {
                    Security security = staticBasic.getSecurity();
                    if (!securityFilter(security.getCode(), security.getMarket(), staticBasic.getSecType())) {
                        continue;
                    }
                    symbolPool.add(CodeInfo.builder().code(security.getCode()).market(security.getMarket())
                        .name(staticBasic.getName()).build());
                }
            }
        }
        return symbolPool.toArray(new CodeInfo[] {});

    }

}
