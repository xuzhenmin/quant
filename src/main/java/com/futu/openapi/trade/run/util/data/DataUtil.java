/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotGetCapitalFlow.CapitalFlowItem;
import com.futu.openapi.trade.base.Constants;
import com.futu.openapi.trade.run.analysis.Analyze;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.IOUtil;
import com.futu.openapi.trade.run.util.Peak;
import com.futu.openapi.trade.support.SecurityKline;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

/**
 * @author zhenmin
 * @version $Id: DataUtil.java, v 0.1 2024-04-09 11:27 xuxu Exp $$
 */
public class DataUtil {

    private static final Logger LOGGER = LogManager.getLogger(DataUtil.class);

    private static final String PREFIX_SPE = "@";
    private static final String PREFIX_START = "$";

    //private static final Map<String, KlineData> kLineMap = Maps.newConcurrentMap();

    private static String CUTOFF = "";

    /**
     * 读取K线数据
     *
     * @param codeInfos
     * @return
     */
    public static Map<CodeInfo, List<KLine>> loadKLineData(CodeInfo... codeInfos) {

        LOGGER.info("loadKLineData codes:{}", codeInfos);

        Map<String, KlineData> kLineMap = initKline(codeInfos);

        //已有数据
        Map<CodeInfo, List<KLine>> readyMap = Maps.newConcurrentMap();

        Map<String, KlineData> unReadyKLineMap = Maps.newConcurrentMap();

        Set<CodeInfo> unReadyCodes = Sets.newConcurrentHashSet();

        for (CodeInfo codeInfo : codeInfos) {
            if (kLineMap.get(codeInfo.getCode()) == null || CollectionUtils.isEmpty(
                kLineMap.get(codeInfo.getCode()).getData())) {
                unReadyCodes.add(codeInfo);
                unReadyKLineMap = modifyFile(unReadyCodes.toArray(new CodeInfo[] {}));
            }
        }
        kLineMap.putAll(unReadyKLineMap);
        for (CodeInfo codeInfo : codeInfos) {
            if (kLineMap.get(codeInfo.getCode()) != null) {
                readyMap.put(codeInfo, kLineMap.get(codeInfo.getCode()).getData());
            }
        }
        //System.out.println("unReadyCodes:" + JSON.toJSONString(unReadyCodes));
        return readyMap;

    }

    /**
     * 读取K线数据
     *
     * @param codeInfos
     * @return
     */
    public static Map<CodeInfo, List<KLine>> loadKLineDirect(CodeInfo... codeInfos) {

        LOGGER.info("loadKLineDirect codes:{}", codeInfos);

        Map<String, KlineData> kLineMap = queryKLine(codeInfos);

        //已有数据
        Map<CodeInfo, List<KLine>> readyMap = Maps.newConcurrentMap();

        for (CodeInfo codeInfo : codeInfos) {
            if (kLineMap.get(codeInfo.getCode()) != null) {
                readyMap.put(codeInfo, kLineMap.get(codeInfo.getCode()).getData());
            }
        }
        return readyMap;
    }

    /**
     * 直查并写文件
     *
     * @param codeInfos
     * @return
     */
    private static Map<String, KlineData> queryKLine(CodeInfo[] codeInfos) {
        Map<String, KlineData> kLineMap = Maps.newConcurrentMap();

        Map<CodeInfo, List<KLine>> listMap = Maps.newConcurrentMap();
        for (int i = 0; i < codeInfos.length; i++) {
            try {
                List<KLine> kLineList = queryKLines(codeInfos[i].getCode(), codeInfos[i].getMarket());
                //组装数据
                listMap.put(codeInfos[i], kLineList);
                //写入
                DataUtil.writeKline(kLineList, codeInfos[i]);
                //System.out.print("," + i);
                if (i > 0 && i % 30 == 0) {
                    System.out.println();
                }
                Thread.sleep(600);
            } catch (Exception e) {
                LOGGER.error("queryKLine error:{}", codeInfos, e);
            }
        }
        listMap.forEach((codeInfo, kLines) -> kLineMap.put(codeInfo.getCode(), KlineData.builder().codeInfo(codeInfo)
            .data(kLines).build()));
        return kLineMap;
    }

    private static Map<String, KlineData> initKline(CodeInfo[] codeInfos) {

        Map<String, KlineData> kLineMap = Maps.newConcurrentMap();

        LOGGER.info("date:{},CUTOFF:{}", new SimpleDateFormat("yyyyMMdd").format(new Date()), CUTOFF);

        if (!new SimpleDateFormat("yyyyMMdd").format(new Date()).equalsIgnoreCase(CUTOFF) || kLineMap.isEmpty()) {

            Map<CodeInfo, List<KLine>> listMap = DataUtil.loadKline();
            if (listMap.size() < 1) {
                for (int i = 0; i < codeInfos.length; i++) {
                    try {
                        List<KLine> kLineList = queryKLines(codeInfos[i].getCode(), codeInfos[i].getMarket());
                        //组装数据
                        listMap.put(codeInfos[i], kLineList);
                        //写入
                        DataUtil.writeKline(kLineList, codeInfos[i]);
                        //System.out.print("," + i);
                        if (i > 0 && i % 30 == 0) {
                            System.out.println();
                        }
                        Thread.sleep(500);
                    } catch (Exception e) { e.printStackTrace();}
                }
            }
            listMap.forEach((codeInfo, kLines) -> {
                kLineMap.put(codeInfo.getCode(), KlineData.builder().codeInfo(codeInfo).data(kLines).build());
            });
            CUTOFF = new SimpleDateFormat("yyyyMMdd").format(new Date());
        }
        return kLineMap;
    }

    private static Map<String, KlineData> modifyFile(CodeInfo[] unreadyCodes) {
        Map<String, KlineData> kLineMap = Maps.newConcurrentMap();
        Map<CodeInfo, List<KLine>> listMap = DataUtil.loadKline();
        for (int i = 0; i < unreadyCodes.length; i++) {
            try {
                List<KLine> kLineList = queryKLines(unreadyCodes[i].getCode(), unreadyCodes[i].getMarket());
                //组装数据
                listMap.put(unreadyCodes[i], kLineList);
                //写入
                DataUtil.writeKline(kLineList, unreadyCodes[i]);
                //System.out.print("," + i);
                if (i > 0 && i % 30 == 0) {
                    System.out.println();
                }
                Thread.sleep(500);
            } catch (Exception e) { e.printStackTrace();}
        }
        listMap.forEach((codeInfo, kLines) -> {
            kLineMap.put(codeInfo.getCode(), KlineData.builder().codeInfo(codeInfo).data(kLines).build());
        });
        return kLineMap;
    }

    public static String numFormat(String pattern, Double num) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(num);
    }

    public static Double doubleFormat(String pattern, Double num) {
        DecimalFormat df = new DecimalFormat(pattern);
        return Double.parseDouble(df.format(num));
    }

    /**
     * @param code
     * @param market
     * @return
     */
    public static List<KLine> queryKLines(String code, int market) {
        List<KLine> kLines = Lists.newArrayList();
        try {
            SecurityKline securityKline = SecurityKline.newInstance();
            kLines = securityKline.queryKline(Security.newBuilder().setCode(code)
                .setMarket(market).build(), 150, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kLines;
    }

    public static Map<CodeInfo, List<KLine>> loadKline() {

        Map<CodeInfo, List<KLine>> listMap = Maps.newConcurrentMap();
        List<String> dataList = IOUtil.read(Constants.getKlineFile());

        List<KLine> kLineList = Lists.newArrayList();

        String infoStr = "";
        for (int i = 0; i < dataList.size(); i++) {
            if (StringUtils.isBlank(dataList.get(i))) {
                continue;
            }
            if (dataList.get(i).startsWith(PREFIX_START)) {
                infoStr = dataList.get(i).replace(PREFIX_START, "");
            } else if (PREFIX_SPE.equalsIgnoreCase(dataList.get(i))) {
                listMap.put(JSON.parseObject(infoStr, CodeInfo.class), kLineList);
                kLineList = Lists.newArrayList();
            } else {
                KLine.Builder kline = KLine.newBuilder();
                try {
                    JsonFormat.Parser parser = JsonFormat.parser();
                    parser.ignoringUnknownFields().merge(dataList.get(i), kline);
                    kLineList.add(kline.build());
                } catch (Exception e) {e.printStackTrace();}
            }

        }

        return listMap;
    }

    public static Map<CodeInfo, List<Peak<String, String, String, Double>>> loadPeak(String filePath) {

        Map<CodeInfo, List<Peak<String, String, String, Double>>> peakMap = Maps.newConcurrentMap();

        List<String> dataList = IOUtil.read(filePath);

        dataList.forEach(s -> {

            try {
                if (StringUtils.isBlank(s)) {
                    return;
                }
                String[] st = s.split(",");
                if (st.length < 3) {
                    return;
                }
                String symbol = st[0].split(":")[1];
                String name = st[1].split(":")[1];
                List<Peak<String, String, String, Double>> peakList = Lists.newArrayList();

                Peak peak;
                int index = 0;
                for (int i = 3; i < st.length; i = i + 3) {
                    peak = new Peak<String, String, String, Double>().put("" + index, st[i].split(":")[1],
                        st[i + 1].substring("highTime:".length()),
                        Double.parseDouble(st[i + 2].split(":")[1]));
                    peakList.add(peak);
                    index++;
                }
                peakMap.put(CodeInfo.builder().code(symbol).name(name).build(), peakList);

            } catch (Exception e) {e.printStackTrace();}
        });

        return peakMap;
    }

    public static void writeKline(List<KLine> kLineList, CodeInfo codeInfo) {
        List<String> strings = Lists.newArrayList();
        kLineList.forEach(kLine -> {
            try {
                strings.add(JsonFormat.printer().print(kLine));
            } catch (Exception e) {e.printStackTrace();}
        });
        IOUtil.write(Constants.getKlineFile(), Lists.newArrayList(PREFIX_START + JSON.toJSONString(codeInfo)));
        IOUtil.write(Constants.getKlineFile(), strings);
        IOUtil.write(Constants.getKlineFile(), Lists.newArrayList(PREFIX_SPE));

    }

    public static void writeAnalysis(Map<CodeInfo, List<Analyze>> listMap) {

        List<String> strings = Lists.newArrayList();

        listMap.forEach((codeInfo, analyzes) -> {
            strings.add("code:" + codeInfo.getCode() + ",name:" + codeInfo.getName() + ",analyzes:" + JSON
                .toJSONString(analyzes));
        });
        IOUtil.write(Constants.getAnalysisFile(), strings, false);

    }

    public static void writeAnalysisStatistic(List<String> results) {

        IOUtil.write(Constants.getAnalysisStatisticFile(), results, false, false);

    }

    public static void writeCapital(List<CapitalFlowItem> capitalFlowItems, CodeInfo codeInfo) {
        List<String> strings = Lists.newArrayList();
        capitalFlowItems.forEach(flowItem -> {
            try {
                strings.add(JsonFormat.printer().print(flowItem));
            } catch (Exception e) {e.printStackTrace();}
        });
        IOUtil.write(Constants.getCapitalFile(), Lists.newArrayList(PREFIX_START + JSON.toJSONString(codeInfo)));
        IOUtil.write(Constants.getCapitalFile(), strings);
        IOUtil.write(Constants.getCapitalFile(), Lists.newArrayList(PREFIX_SPE));

    }

}


