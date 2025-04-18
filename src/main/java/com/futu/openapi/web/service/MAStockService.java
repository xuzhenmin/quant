/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2025 All Rights Reserved.
 */
package com.futu.openapi.web.service;

import java.util.List;
import java.util.Map;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.web.bean.EstimatePrice;
import com.futu.openapi.web.bean.PriceLine;
import com.futu.openapi.web.service.util.EMAFunc;
import com.futu.openapi.web.service.util.IdealPriceService;
import com.futu.openapi.web.service.util.PredictPriceService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhenmin
 * @version $Id: MAStockService.java, v 0.1 2025-03-21 17:42 xuxu Exp $$
 */
@Component
public class MAStockService {

    private final static Logger LOGGER = LoggerFactory
        .getLogger(MAStockService.class);

    public static final String PATTERN_PRICE = "#.0000";

    public static final int SLOP_PERIOD = 10;

    public static final int WINDOW = 5;

    public static final int X_PERIOD = 5;
    public static final int Y_PERIOD = 10;

    public static final int LINE_POINT = 30;

    public static final int KLINE_DAYS = 30;

    public static final int KLINE_LIMIT = 50;

    /**
     * @param code
     * @param market
     * @return
     */
    public List<PriceLine> run(String code, int market) {

        List<PriceLine> priceLines = Lists.newArrayList();

        String date = "";
        try {
            for (int i = 0; i < LINE_POINT; i++) {

                List<KLine> kLines = queryKlines(code, market, 0 - i, KLINE_DAYS, KLINE_LIMIT);
                if (date.equalsIgnoreCase(kLines.get(kLines.size() - 1).getTime())) {
                    continue;
                } else {
                    date = kLines.get(kLines.size() - 1).getTime();
                }
                PriceLine priceLine = analysis(kLines, X_PERIOD, Y_PERIOD, SLOP_PERIOD, WINDOW);
                LOGGER.info("MAStockService priceLine:{}", priceLine);
                priceLines.add(priceLine);
            }
        } catch (Exception e) {
            LOGGER.error("MAStockService run error, code:{}", code, e);
        }
        return priceLines;
    }

    private PriceLine analysis(List<KLine> kLines, int periodX, int periodY, int slopPeriod, int window) {

        String date = kLines.get(kLines.size() - 1).getTime().substring(0, "2025-03-18".length());
        //格式化价格
        final List<Double> closePrices = standardizedClosePrice(kLines, PATTERN_PRICE);

        final List<Double> lowPrices = standardizedLowPrice(kLines, PATTERN_PRICE);

        //10日均线
        List<Double> emaY = EMAFunc.calEMAByPeriod(closePrices, periodY);
        //5日均线
        List<Double> emaX = EMAFunc.calEMAByPeriod(closePrices, periodX);

        //预测均线交叉价格及收盘价
        EstimatePrice estimatePrice = PredictPriceService.predict(closePrices, periodY, periodX);

        //System.out.println("closePrice: " + closePrices.size() +", " + closePrices);
        //System.out.println("lowPrice: " + lowPrices.size() +", " + lowPrices);
        //计算期望价格
        PriceLine priceLine = IdealPriceService.calIdealPrice(estimatePrice.getEstPrice(), emaX, emaY, closePrices,
            lowPrices, slopPeriod,
            window);
        priceLine.setDate(date);
        return priceLine;
    }

    /**
     * 格式化价格
     *
     * @param kLines
     * @param pattern
     * @return
     */
    private List<Double> standardizedClosePrice(List<KLine> kLines, String pattern) {

        final List<Double> closePrices = Lists.newArrayList();
        kLines.forEach(kLine -> {
            closePrices.add(Double.parseDouble(DataUtil.numFormat(pattern, kLine.getClosePrice())));
        });
        return closePrices;
    }

    /**
     * 格式化价格
     *
     * @param kLines
     * @param pattern
     * @return
     */
    private List<Double> standardizedLowPrice(List<KLine> kLines, String pattern) {

        final List<Double> lowPrices = Lists.newArrayList();
        kLines.forEach(kLine -> {
            lowPrices.add(Double.parseDouble(DataUtil.numFormat(pattern, kLine.getLowPrice())));
        });
        return lowPrices;
    }

    /**
     * 按照指定偏移天数查询历史K线
     *
     * @param code
     * @param market
     * @param offset
     * @param days
     * @param limit
     * @return
     * @throws Exception
     */
    public List<KLine> queryKlines(String code, int market, int offset, int days, int limit) throws Exception {

        CodeInfo codeInfo = CodeInfo.builder().code(code).market(market).build();

        Map<CodeInfo, List<KLine>> lists = DataUtil.loadKLineData(codeInfo);
        List<KLine> kLineList = lists.get(codeInfo);
        //TODO 可以按天数再次截取
        return kLineList.subList(0, kLineList.size() - Math.abs(offset));
    }

}