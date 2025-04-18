/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.trade.run;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotCommon.SecurityStaticBasic;
import com.futu.openapi.pb.QotCommon.SecurityType;
import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.run.funs.EMA;
import com.futu.openapi.trade.run.funs.StockSlope;
import com.futu.openapi.trade.run.funs.meta.SlopeData;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.trade.run.util.data.Normalization;
import com.futu.openapi.trade.support.UserSecurityGroup;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author zhenmin
 * @version $Id: MAStocks.java, v 0.1 2025-02-13 20:26 xuxu Exp $$
 */
public class MAStocks {

    static {
        PropUtil.readProp();
    }

    private static final String SELECT_STOCK_FILE = Constants.SELECT_STOCK_FILE;

    public static void main(String[] args) {

        run();

    }

    public static void run() {
        System.out.println("Watch start //");

        CodeInfo[] codeInfos = queryCodeInfo("量化");
        System.out.println(Arrays.toString(codeInfos));

        Map<CodeInfo, List<KLine>> lists = DataUtil.loadKLineData(codeInfos);

        int slopPeriod = 10;
        int spilt = 20;//斜率截取
        int window = 5;
        int periodX = 5;
        int periodY = 10;
        lists.forEach((codeInfo, kLines) -> {

            System.out.println("symbol:" + codeInfo.getCode() + ", name:" + codeInfo.getName());
            //if (!codeInfo.getCode().equalsIgnoreCase("300859")) {
            //    return;
            //}
            analysis(kLines, periodX, periodY, slopPeriod, window);

            //List<SlopeData> dataList = StockSlope.calculateSlope(clipKline(kLines), slopPeriod, window);
            //System.out.println(dataList.get(dataList.size() - 1));
            //dataList = dataList.subList(dataList.size() - spilt, dataList.size());
            //System.out.println(normalization(dataList));
            System.out.println();
        });

    }

    private static List<Double> normalization(List<SlopeData> dataList) {

        List<Double> slopeList = Lists.newArrayList();
        dataList.forEach(slopeData -> {
            slopeList.add(slopeData.getEmaXSlope());
        });
        //System.out.println("slopeList :" + slopeList);
        final List<Double> result = Lists.newArrayList();

        Lists.newArrayList(Normalization.normalize(slopeList.toArray(new Double[] {}))).forEach(aDouble -> {
            result.add(Double.parseDouble(DataUtil.numFormat("#0.0000", aDouble)));
        });
        return result;
    }

    private static List<Double> clipKline(List<KLine> kLines) {

        final List<Double> closePrices = Lists.newArrayList();
        kLines.forEach(kLine -> {
            closePrices.add(Double.parseDouble(DataUtil.numFormat("#.0000", kLine.getClosePrice())));
        });
        return closePrices;

    }

    private static void analysis(List<KLine> kLines, int periodX, int periodY, int slopPeriod, int window) {
        final List<Double> closePrices = clipKline(kLines);

        List<Double> emaY = calEMAByPeriod(closePrices, periodY);
        System.out.println(emaY);
        List<Double> emaX = calEMAByPeriod(closePrices, periodX);
        System.out.println(emaX);

        //
        Double emaPrice = calEMA(closePrices, periodX, periodY);

        //计算期望价格
        calIdealPrice(emaPrice, emaX, emaY, closePrices, slopPeriod, window);

        //斜率加权期望价格

    }

    private static void calIdealPrice(Double emaPrice, List<Double> emaX, List<Double> emaY, List<Double> closePrices,
                                      int slopPeriod, int window) {

        Double endPrice = closePrices.get(closePrices.size() - 1);  //363
        Double closePrice = closePrices.get(closePrices.size() - 2);  // 494

        Double xPrice = emaX.get(emaX.size() - 1); //463
        Double yPrice = emaY.get(emaY.size() - 1); //445

        //System.out.println("closePrices:" + closePrices);

        //System.out.println(
        //    "emaPrice:" + emaPrice + ",endPrice:" + DataUtil.numFormat("#.0000", endPrice) + ",closePrice:" +
        //        closePrice
        //        + ",xPrice:" + xPrice
        //        + ",yPrice:" + yPrice);
        Double consPrice = conservativeAlgorithm(emaPrice, endPrice, closePrice, xPrice, yPrice);
        Double radicalPrice = radicalAlgorithm(emaPrice, endPrice, closePrice, xPrice, yPrice);

        Double slopePrice = slopeIdealPrice(emaPrice, endPrice, closePrice, xPrice, yPrice, closePrices, slopPeriod,
            window);
        System.out.println("consPrice:" + DataUtil.numFormat("#.0000", consPrice) + ", radicalPrice:" + DataUtil
            .numFormat("#.0000", radicalPrice) + ", slopePrice:" + DataUtil.numFormat("#.0000", slopePrice));
    }

    /**
     * 加斜率
     *
     * @param closePrices
     * @param slopPeriod  20   斜率依赖数据区间
     * @param window      5
     */
    private static Double slopeIdealPrice(Double emaPrice, Double endPrice, Double closePrice, Double xPrice,
                                          Double yPrice, List<Double> closePrices, int slopPeriod, int window) {
        if (closePrice.equals(emaPrice)) {
            return closePrice;
        }

        List<SlopeData> dataList = StockSlope.calculateSlope(closePrices, slopPeriod, window);

        //System.out.println(dataList.get(dataList.size() - 1));
        dataList = dataList.subList(dataList.size() - slopPeriod, dataList.size());

        List<Double> normSlopes = normalization(dataList);

        Double normSlope = normSlopes.get(normSlopes.size() - 1);

        return emaPrice + normSlope * ((xPrice - yPrice) / (closePrice - emaPrice)) * (closePrice - endPrice);

    }

    //保守
    private static Double conservativeAlgorithm(Double emaPrice, Double endPrice, Double closePrice, Double xPrice,
                                                Double yPrice) {
        if (closePrice.equals(emaPrice)) {
            return closePrice;
        }
        return emaPrice + ((xPrice - yPrice) / (closePrice - emaPrice)) * (closePrice - endPrice);
    }

    //激进
    private static Double radicalAlgorithm(Double emaPrice, Double endPrice, Double closePrice, Double xPrice,
                                           Double yPrice) {
        if (closePrice.equals(endPrice)) {
            return closePrice;
        }
        return emaPrice + (1 - (xPrice - yPrice) / (closePrice - endPrice)) * (closePrice - emaPrice);

    }

    private static List<Double> calEMAByPeriod(List<Double> closePrices, int period) {
        List<Double> emaX = EMA.calculateEMA(closePrices, period);
        return emaX.subList(emaX.size() - period, emaX.size());
    }

    private static void genEMAEstimate(List<Double> closePrices, boolean plus) {

        if (plus) {
            Double closePrice = closePrices.get(closePrices.size() - 1) + 0.1;
            closePrices.remove(closePrices.size() - 1);
            closePrices.add(closePrice);
        } else {
            Double closePrice = closePrices.get(closePrices.size() - 1) - 0.1;
            closePrices.remove(closePrices.size() - 1);
            closePrices.add(closePrice);
        }
    }

    private static Double calEMA(List<Double> closePrices, int periodX, int periodY) {

        //模拟新增一个成交点位
        closePrices.add(closePrices.get(closePrices.size() - 1));

        List<Double> emaY = calEMAByPeriod(closePrices, periodY);
        List<Double> emaX = calEMAByPeriod(closePrices, periodX);

        Double crossover = touchEMACrossover(emaX, emaY);
        if (crossover == 0.0) {
            while (true) {
                genEMAEstimate(closePrices, false);
                emaY = calEMAByPeriod(closePrices, periodY);
                emaX = calEMAByPeriod(closePrices, periodX);
                crossover = touchEMACrossover(emaX, emaY);
                if (crossover != 0.0) {
                    //System.out.print("emaX:" + emaX + " , emaY: " + emaY);
                    //System.out.println(" ,closePrice :" + closePrices.get(closePrices.size() - 1));
                    break;
                }
            }
        } else {
            //System.out.print("emaX:" + emaX + " , emaY: " + emaY);
            //System.out.println(" ,closePrice :" + closePrices.get(closePrices.size() - 1));
        }
        return emaX.get(emaX.size() - 1);
    }

    private static Double touchEMACrossover(List<Double> emaX, List<Double> emaY) {

        Double x = emaX.get(emaX.size() - 1);
        Double y = emaY.get(emaY.size() - 1);
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception E) {E.printStackTrace();}
        if (x <= y) {
            return emaX.get(emaX.size() - 1);
        }
        return 0.0;
    }

    private static CodeInfo[] queryCodeInfo(String group) {

        //System.out.println("groups :" + groups);
        Set<CodeInfo> symbolPool = Sets.newConcurrentHashSet();
        List<SecurityStaticBasic> securities = queryUserSecurity(group);
        for (SecurityStaticBasic staticBasic : securities) {
            Security security = staticBasic.getSecurity();
            if (!securityFilter(security.getCode(), security.getMarket(), staticBasic.getSecType())) {
                continue;
            }
            symbolPool.add(CodeInfo.builder().code(security.getCode()).market(security.getMarket())
                .name(staticBasic.getName()).build());
        }
        return symbolPool.toArray(new CodeInfo[] {});

    }

    private static List<SecurityStaticBasic> queryUserSecurity(String groupName) {
        List<SecurityStaticBasic> securities = Lists.newArrayList();
        try {
            UserSecurityGroup userSecurityGroup = UserSecurityGroup.newInstance();

            securities = userSecurityGroup.queryUserSecurity(groupName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return securities;
    }

    private static final List<Integer> MARKET_BLACK = Lists.newArrayList(QotMarket.QotMarket_JP_Security_VALUE,
        QotMarket.QotMarket_US_Security_VALUE, QotMarket.QotMarket_SG_Security_VALUE,
        QotMarket.QotMarket_HK_Security_VALUE);

    private static final List<Integer> SEC_BLACK = Lists.newArrayList(SecurityType.SecurityType_Bond_VALUE,
        SecurityType.SecurityType_Plate_VALUE, SecurityType.SecurityType_Trust_VALUE,
        SecurityType.SecurityType_Unknown_VALUE, SecurityType.SecurityType_Bwrt_VALUE,
        SecurityType.SecurityType_Drvt_VALUE, SecurityType.SecurityType_PlateSet_VALUE);

    private static boolean securityFilter(String code, int market, int secType) {
        //if (MARKET_BLACK.contains(market) || SEC_BLACK.contains(secType)) {
        //    return false;
        //}
        return true;
    }

}