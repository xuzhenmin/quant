/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.analysis;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.DateUtil;
import com.futu.openapi.trade.run.util.GraphUtil;
import com.futu.openapi.trade.run.util.Peak;
import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.trade.run.util.oss.OssClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author zhenmin
 * @version $Id: CyclicalAnalysis.java, v 0.1 2024-05-08 16:44 xuxu Exp $$
 */
public class CyclicalAnalysis {


    public static Map<CodeInfo, List<Analyze>> analysisPeak() {

        List<LocalDate> recently = DateUtil.recently(Integer.parseInt(PropUtil.getProp("RECENTLY_DAY")));

        Map<String, List<CodeInfo>> peakMap = Maps.newTreeMap((o1, o2) -> o2.compareTo(o1));
        recently.forEach(localDate -> {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String date = localDate.format(formatter);
            Map<CodeInfo, List<Peak<String, String, String, Double>>> listMap = DataUtil.loadPeak(
                Constants.buildPeakFile(date));

            //默认当天
            peakMap.put(date,Lists.newArrayList());

            listMap.forEach((codeInfo, peaks) -> {
                peaks.forEach(pks -> {
                    if (DateUtil.isSameDay(date, pks.getEnd())) {
                        if (peakMap.get(date) == null) {
                            List<CodeInfo> codeInfos = Lists.newArrayList();
                            codeInfos.add(codeInfo);
                            peakMap.put(date, codeInfos);
                        } else {
                            peakMap.get(date).add(codeInfo);
                        }
                    }
                });
            });
        });

        Map<CodeInfo, List<Analyze>> infoListMap = recentlyPerformance(peakMap);

        DataUtil.writeAnalysis(infoListMap);

        //组装数据
        List<String> analysisStatisticResult = assembly(peakMap, infoListMap);
        DataUtil.writeAnalysisStatistic(analysisStatisticResult);

        //upload oss
        OssClient.newInstance().loadData(Constants.getAnalysisStatisticFile(), Constants.getAnalysisStatisticFileName());

        return infoListMap;
    }

    /**
     * @param peakMap     date symbol
     * @param infoListMap symbol date changePercent
     */
    public static List<String> assembly(Map<String, List<CodeInfo>> peakMap, Map<CodeInfo, List<Analyze>> infoListMap) {

        List<String> assemblyResult = Lists.newArrayList();

        List<Double> peakCount = Lists.newArrayList();

        peakMap.forEach((s, codeInfos) -> {

            peakCount.add(Double.parseDouble("" + codeInfos.size()));

            StringBuffer msgContent = new StringBuffer("");

            msgContent.append("日期:").append(s).append(",共:").append(codeInfos.size()).append("条。其列表如下:\n");

            codeInfos.forEach(codeInfo -> {
                msgContent.append(codeInfo.getName()).append("(").append(codeInfo.getCode()).append(")")
                    .append(",近期出现次数:").append(infoListMap.get(codeInfo).size()).append("次。");
                for (Analyze analyze : infoListMap.get(codeInfo)) {
                    msgContent.append("时间:").append(analyze.getDate()).append(", 距今涨跌幅:").append(
                        Double.parseDouble(DataUtil.numFormat("#.00", analyze.getRecentlyChangePercent() * 100)))
                        .append("%; ");
                }
                msgContent.append("\n");

            });
            assemblyResult.add(msgContent.toString());
        });

        List<String> dates = Lists.newArrayList();
        for (String date : peakMap.keySet().toArray(new String[] {})) {
            try {
                dates.add(new SimpleDateFormat("MMdd")
                    .format(new SimpleDateFormat("yyyyMMdd").parse(date)));
            } catch (Exception e) {e.printStackTrace();}
        }
        Collections.reverse(dates);
        Collections.reverse(peakCount);
        GraphUtil.createNewLineChartForPng("Market Peak Trend", "Peak Trend", "date", "count", Constants.getMarketPeakTrendFile(),
            dates,
            peakCount, 1024, 768);


        //upload oss
        OssClient.newInstance().loadData(Constants.getMarketPeakTrendFile(), Constants.getMarketPeakTrendFileName());

        return assemblyResult;

    }

    public static Map<CodeInfo, List<Analyze>> recentlyPerformance(Map<String, List<CodeInfo>> peakMap) {

        Map<CodeInfo, List<Analyze>> listMap = Maps.newConcurrentMap();

        peakMap.forEach((s, codeInfos) -> {

            codeInfos.forEach(codeInfo -> {
                if (listMap.get(codeInfo) == null) {
                    List<Analyze> analyzes = Lists.newArrayList();
                    Analyze analyze = Analyze.builder().date(s).build();
                    analyzes.add(analyze);
                    listMap.put(codeInfo, analyzes);

                } else {
                    listMap.get(codeInfo).add(Analyze.builder().date(s).build());
                }
            });
        });

        listMap.forEach((codeInfo, analyzes) -> {

            Map<CodeInfo, List<KLine>> klineListMap = DataUtil.loadKLineDirect(codeInfo);

            analyzes.forEach(analyze -> {

                Double percent = calChangePercent(analyze.getDate(), klineListMap.get(codeInfo));
                analyze.setRecentlyChangePercent(percent);
            });

        });

        return listMap;
    }

    private static Double calChangePercent(String date, List<KLine> kLineList) {
        Double newPrice = null;
        KlinePrice klinePrice = new KlinePrice();
        if (kLineList != null && kLineList.size() > 0) {
            newPrice = kLineList.get(kLineList.size() - 1).getClosePrice();
            kLineList.forEach(kLine -> {
                if (DateUtil.isSameDay(kLine.getTime(), date)) {
                    klinePrice.setPrice(kLine.getClosePrice());
                }
            });
        }

        if (newPrice != null && klinePrice.getPrice() != null) {
            return Double.parseDouble(
                DataUtil.numFormat("#.0000", (newPrice - klinePrice.getPrice()) / klinePrice.getPrice()));
        }

        //default
        return 99.87;
    }

}

class KlinePrice {

    private Double price;

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
