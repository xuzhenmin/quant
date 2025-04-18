/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotCommon.SecurityStaticBasic;
import com.futu.openapi.pb.QotCommon.SecurityType;
import com.futu.openapi.pb.QotGetSecuritySnapshot.Snapshot;
import com.futu.openapi.trade.graph.CreatLineChart;
import com.futu.openapi.trade.graph.Serie;
import com.futu.openapi.trade.model.TripleWave;
import com.futu.openapi.trade.run.util.FindHigher;
import com.futu.openapi.trade.service.CapitalService;
import com.futu.openapi.trade.service.UserGroupService;
import com.futu.openapi.trade.support.SecurityKline;
import com.futu.openapi.trade.support.SecuritySnapshot;
import com.futu.openapi.trade.support.SecurityStockCode;
import com.futu.openapi.trade.support.UserSecurityGroup;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.protobuf.util.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhenmin
 * @version $Id: TradeRun.java, v 0.1 2022-03-07 10:57 上午 xuxu Exp $$
 */
public class TradeRun {

    private final static SimpleDateFormat DATE_FORMAT_D = new SimpleDateFormat("yyyy-MM-dd");

    private final static SimpleDateFormat DATE_FORMAT_S = new SimpleDateFormat("yyyy-MM-dd mm:HH:ss");

    public static void main(String[] args) {

        System.out.println("run start //");

        try {
            //----------------------------------------------------
            //SecurityStockCode securityStockCode = new SecurityStockCode();
            //securityStockCode.createCon();
            //List<Security> stockCodes = securityStockCode.queryStockCodes(QotMarket.QotMarket_CNSH_Security);
            //
            //for (Security security : stockCodes) {
            //    System.out.println(JsonFormat.printer().print(security));
            //}

            //----------------------------------------------------
            //SecuritySnapshot securitySnapshot = new SecuritySnapshot();
            //securitySnapshot.createCon();
            //List<Security> subCodes = stockCodes.subList(0, 100);
            //List<Snapshot> snapshotList = securitySnapshot.querySnapshot(subCodes);
            //for (Snapshot snapshot : snapshotList) {
            //    System.out.println(JsonFormat.printer().print(snapshot));
            //}
            //----------------------------------------------------

            String code = "000014";
            int market = QotMarket.QotMarket_CNSZ_Security_VALUE;
            //String code = "000002";

            //List<Serie> serieList = Lists.newArrayList();
            //
            //SecurityKline securityKline = new SecurityKline();
            //securityKline.createCon();
            //List<KLine> kLines = securityKline.queryKline(Security.newBuilder().setCode(code)
            //    .setMarket(QotMarket.QotMarket_CNSZ_Security_VALUE).build(), 30, 1000);
            //List<Double> closePrices = Lists.newArrayList();
            //List<String> categories = Lists.newArrayList();
            //for (KLine kline : kLines) {
            //    closePrices.add(kline.getClosePrice());
            //    //DATE_FORMAT_S.parse(kline.getTime())
            //    categories.add(kline.getTime());
            //    System.out.println(JsonFormat.printer().print(kline));
            //}
            //serieList.add(new Serie(code, closePrices.toArray()));

            //new CreatLineChart().createChartSwing("kline", "date", "closePrice", categories, serieList);

            //
            //threeWave(queryKLines(code));

            //
            userGroupWaves();
            //queryWave(code, market);
            //queryWave("603993", QotMarket.QotMarket_CNSH_Security_VALUE);

            //UserGroupService userGroupService = new UserGroupService();
            //userGroupService.queryUserSecurityByGroup();
            //userGroupService.parseSecuInfo();

            //CapitalService capitalService = new CapitalService();
            //capitalService.queryCapitalFlow("002124", QotMarket.QotMarket_CNSZ_Security_VALUE);
            //capitalService.queryCapitalDistribution("002124", QotMarket.QotMarket_CNSZ_Security_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("run end //");

    }

    private static void queryWave(String code, int market) throws Exception {

        List<KLine> kLines = queryKLines(code, market);
        TripleWave threeWave = threeWave(kLines);
        System.out.println(
            code + ", " + checkWave(threeWave));
        System.out.println(threeWave.getBeginHigh_time_1() + ", " + threeWave.getHighPrice_time_1() + ", " + threeWave.getHighPrice_1());
        System.out.println(threeWave.getBeginHigh_time_2() + ", " + threeWave.getHighPrice_time_2() + ", " + threeWave.getHighPrice_2());
        System.out.println(threeWave.getBeginHigh_time_3() + ", " + threeWave.getHighPrice_time_3() + ", " + threeWave.getHighPrice_3());

    }

    /**
     * 用户自选
     *
     * @throws Exception
     */
    private static void userGroupWaves() throws Exception {
        List<String> groups = queryUserGroup();
        System.out.println("groups :" + groups);

        Map<String, TripleWave> waveMap = Maps.newConcurrentMap();

        Set<String> symbolPool = Sets.newConcurrentHashSet();

        for (String group : groups) {
            if (!groupFilter(group)) {
                continue;
            }
            List<SecurityStaticBasic> securities = queryUserSecurity(group);
            if (securities == null || securities.size() < 1) {
                continue;
            }
            for (SecurityStaticBasic staticBasic : securities) {
                Security security = staticBasic.getSecurity();
                if (!securityFilter(security.getCode(), security.getMarket(), staticBasic.getSecType())) {
                    continue;
                }
                if (symbolPool.contains(security.getCode())) {
                    continue;
                } else {
                    symbolPool.add(security.getCode());
                }
                List<KLine> kLines = queryKLines(security.getCode(), security.getMarket());
                TripleWave threeWave = threeWave(kLines);
                if (checkWave(threeWave)) {
                    waveMap.put(security.getCode(), threeWave);
                    System.out.println(
                        staticBasic.getName() + ", " + security.getCode() + ", " + checkWave(threeWave)
                            + " , " + JSON
                            .toJSONString(threeWave));
                } else {
                    System.out.print("ω");
                }
                Thread.sleep(500);
            }
        }
        waveMap.forEach((s, threeWave) -> System.out.println(
            s + ", " + checkWave(threeWave)
                + " , " + JSON
                .toJSONString(threeWave)));
    }

    private static final List<Integer> MARKET_BLACK = Lists.newArrayList(QotMarket.QotMarket_JP_Security_VALUE,
        QotMarket.QotMarket_US_Security_VALUE, QotMarket.QotMarket_SG_Security_VALUE,
        QotMarket.QotMarket_HK_Security_VALUE);

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

    private static final List<String> GROUP_BLACK = Lists.newArrayList("加拿大", "澳洲", "港股期权", "期权", "美股期权", "债券", "全部",
        "期货");

    private static boolean groupFilter(String group) {
        if (GROUP_BLACK.contains(group)) {
            return false;
        }
        return true;
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

    /**
     * @param code
     */
    public static List<KLine> queryKLines(String code, int market) {
        List<KLine> kLines = Lists.newArrayList();
        try {
            SecurityKline securityKline = SecurityKline.newInstance();
            kLines = securityKline.queryKline(Security.newBuilder().setCode(code)
                .setMarket(market).build(), 120, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kLines;
    }

    /**
     * @param kLines
     */
    public static TripleWave threeWave(List<KLine> kLines) {

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

        TripleWave threeWave = new TripleWave();

        findHighStage(highPrices, times, 10, threeWave);
        findLowStage(lowPrices, times, 10, threeWave);
        return threeWave;

    }

    private void compareLowPrice(List<KLine> kLines, int index, TripleWave threeWave) {

    }

    private PricePool splitPrice(List<KLine> kLines, int index) {

        PricePool pricePool = new PricePool();

        return pricePool;
    }

    private static void findHighStage(List<Double> highList, List<String> times, int step, TripleWave threeWave) {
        if (highList == null || highList.size() < 1) {

            return;
        }

        Double h_temp = highList.get(0);
        threeWave.setHighPrice_1(h_temp);
        //连续递增序列
        int temp_index = 0;
        //锚点
        int temp_anchor = 0;

        int high_index = 0;

        int wave_n = 1;
        for (int i = 0; i < highList.size(); i++) {
            if (h_temp < highList.get(i)) {
                h_temp = highList.get(i);

                temp_anchor = temp_index;
                high_index = i;
            } else if ((temp_index - temp_anchor) > step) {  //非连续上涨，判断 上次高位锚点与本次差值，大于步长，则结束连续上涨判断，小于步长，则继续等待高点出现
                //设置最高点
                System.out.println(
                    "high_index:" + high_index + ",h_temp:" + h_temp + ",i:" + i + ",times:" + times.get(i));
                highWave(threeWave, wave_n, h_temp, times.get(high_index));
                //重置最高点，开始下一后期判断
                h_temp = highList.get(i);
                //当前h_temp 即是周期内最高点
                wave_n++;
                temp_anchor = 0;
                temp_index = 0;

            }
            temp_index++;
        }

    }

    private static void findLowStage(List<Double> lowList, List<String> times, int step, TripleWave threeWave) {
        if (lowList == null || lowList.size() < 1) {
            return;
        }

        Double l_temp = lowList.get(0);
        threeWave.setLowPrice_1(l_temp);
        //连续递增序列
        int temp_index = 0;
        //锚点
        int temp_anchor = 0;

        int low_index = 0;

        int wave_n = 1;
        for (int i = 0; i < lowList.size(); i++) {
            if (l_temp > lowList.get(i)) {
                l_temp = lowList.get(i);

                temp_anchor = temp_index;
                low_index = i;
            } else {
                //非连续下跌，判断 上次低位锚点与本次差值，大于步长，则结束连续下涨判断，小于步长，则继续等待低点出现
                if ((temp_index - temp_anchor) > step) {
                    //设置最低点
                    lowWave(threeWave, wave_n, l_temp, times.get(low_index));
                    //重置最低点，开始下一后期判断
                    l_temp = lowList.get(i);
                    //当前h_temp 即是周期内最低点
                    wave_n++;
                    temp_anchor = 0;
                    temp_index = 0;
                }

            }
            temp_index++;
        }

    }

    private static void lowWave(TripleWave threeWave, int wave_n, Double l_temp, String time) {
        if (wave_n == 1) {
            threeWave.setLowPrice_1(l_temp);
            threeWave.setLowPrice_time_1(time);
        } else if (wave_n == 2) {
            threeWave.setLowPrice_2(l_temp);
            threeWave.setLowPrice_time_2(time);
        } else if (wave_n == 3) {
            threeWave.setLowPrice_3(l_temp);
            threeWave.setLowPrice_time_3(time);
        }
    }

    private static void highWave(TripleWave threeWave, int wave_n, Double h_temp, String time) {
        if (wave_n == 1) {
            threeWave.setHighPrice_1(h_temp);
            threeWave.setHighPrice_time_1(time);
        } else if (wave_n == 2) {
            threeWave.setHighPrice_2(h_temp);
            threeWave.setHighPrice_time_2(time);
        } else if (wave_n == 3) {
            threeWave.setHighPrice_3(h_temp);
            threeWave.setHighPrice_time_3(time);
        }
    }

    /**
     * 判断 h1 < h2 < h3
     *
     * @param threeWave
     * @return
     */
    private static boolean checkWave(TripleWave threeWave) {

        boolean h_c = false;
        boolean l_c = false;

        if (threeWave.getHighPrice_1() != null && threeWave.getHighPrice_2() != null
            && threeWave.getHighPrice_3() != null) {
            if (threeWave.getHighPrice_1() < threeWave.getHighPrice_2()) {
                h_c = true;
            }
        }
        if (threeWave.getLowPrice_1() != null && threeWave.getLowPrice_2() != null
            && threeWave.getLowPrice_3() != null) {
            if (threeWave.getLowPrice_1() < threeWave.getLowPrice_2() && threeWave.getLowPrice_2() < threeWave
                .getLowPrice_3()) {
                l_c = true;
            }
        }

        return h_c && l_c;
    }

    @Getter
    @Setter
    class PricePool {

        private List<Double> leftPool;

        private List<Double> rightPool;

    }

}