package com.futu.openapi.scraper;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.scraper.model.TrendAnalysisResult;
import com.futu.openapi.scraper.model.TrendPoint;
import com.futu.openapi.scraper.model.TrendPointType;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.trade.run.funs.EMA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StockTrendAnalyzer {
    private static final int EMA5_PERIOD = 5;
    private static final int EMA10_PERIOD = 10;
    private static final int MIN_TREND_DAYS = 3;

    /**
     * 分析股票趋势
     *
     * @param symbol 股票代码
     * @return 趋势分析结果
     */
    public TrendAnalysisResult analyzeTrend(String symbol) {
        try {
            // 获取市场信息
            Integer market = getMarketFromStockCode(symbol);
            if (market == null) {
                log.error("Invalid stock code format: {}", symbol);
                return null;
            }

            // 准备股票代码信息
            CodeInfo codeInfo = createCodeInfo(symbol, market);

            // 获取K线数据
            List<KLine> kLines = getKLineData(codeInfo);
            if (kLines == null || kLines.isEmpty()) {
                log.error("No K-line data available for stock: {}", symbol);
                return null;
            }

            // 计算EMA均线
            List<Double> ema5 = calculateEMA(kLines, EMA5_PERIOD);
            List<Double> ema10 = calculateEMA(kLines, EMA10_PERIOD);

            // 分析趋势
            List<TrendPoint> trendPoints = analyzeTrendPoints(kLines, ema5, ema10);
            
            // 判断当前是否处于上升趋势
            boolean isUpTrend = isCurrentlyUpTrend(trendPoints, ema5.get(0), ema10.get(0));
            
            // 生成分析说明
            String explanation = generateExplanation(trendPoints, kLines.get(0).getClosePrice());

            return TrendAnalysisResult.builder()
                    .isUpTrend(isUpTrend)
                    .explanation(explanation)
                    .trendPoints(trendPoints)
                    .currentPrice(kLines.get(0).getClosePrice())
                    .ema5(ema5.get(0))
                    .ema10(ema10.get(0))
                    .build();

        } catch (Exception e) {
            log.error("Error analyzing stock trend: {}", e.getMessage());
            throw new RuntimeException("Failed to analyze stock trend: " + e.getMessage(), e);
        }
    }

    /**
     * 从股票代码获取市场信息
     */
    private Integer getMarketFromStockCode(String stockCode) {
        String[] sts = stockCode.split("\\.");
        if (sts.length != 2) {
            return null;
        }

        switch (sts[1].toUpperCase()) {
            case "SH":
                return QotMarket.QotMarket_CNSH_Security_VALUE;
            case "SZ":
                return QotMarket.QotMarket_CNSZ_Security_VALUE;
            case "HK":
                return QotMarket.QotMarket_HK_Security_VALUE;
            default:
                return null;
        }
    }

    /**
     * 创建股票代码信息对象
     */
    private CodeInfo createCodeInfo(String stockCode, Integer market) {
        String[] sts = stockCode.split("\\.");
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setCode(sts[0]);
        codeInfo.setMarket(market);
        return codeInfo;
    }

    /**
     * 获取K线数据
     */
    private List<KLine> getKLineData(CodeInfo codeInfo) {
        Map<CodeInfo, List<KLine>> kLineMap = DataUtil.loadKLineData(new CodeInfo[] {codeInfo});
        return kLineMap != null ? kLineMap.get(codeInfo) : null;
    }

    /**
     * 计算EMA均线
     */
    private List<Double> calculateEMA(List<KLine> kLines, int period) {
        List<Double> prices = new ArrayList<>();
        for (KLine kLine : kLines) {
            prices.add(kLine.getClosePrice());
        }
        return EMA.calculateEMA(prices, period);
    }

    /**
     * 分析趋势关键点
     * 注意：K线数据是按时间倒序排列的，最新的数据在索引0位置
     */
    private List<TrendPoint> analyzeTrendPoints(List<KLine> kLines, List<Double> ema5, List<Double> ema10) {
        List<TrendPoint> trendPoints = new ArrayList<>();
        
        // 从最新的数据开始向前遍历
        for (int i = 0; i < kLines.size() - 1; i++) {
            String currentTime = kLines.get(i).getTime();
            double currentPrice = kLines.get(i).getClosePrice();
            double currentEma5 = ema5.get(i);
            double currentEma10 = ema10.get(i);
            
            // 前一天的数据
            double prevEma5 = ema5.get(i + 1);
            double prevEma10 = ema10.get(i + 1);
            
            // 判断是否开始上升趋势（均线从低点到高点）
            if (isUpTrendStarting(currentEma5, prevEma5, currentEma10, prevEma10)) {
                // 检查是否已经存在上升通道开始点
                boolean hasStartPoint = trendPoints.stream()
                        .anyMatch(p -> p.getType() == TrendPointType.START);
                
                if (!hasStartPoint) {
                    // 添加上升通道开始点
                    trendPoints.add(TrendPoint.builder()
                            .time(currentTime)
                            .price(currentPrice)
                            .type(TrendPointType.START)
                            .build());
                    continue;
                }
            }
            
            // 判断是否是拐点（均线开始下降）
            if (isPeakPoint(currentEma5, prevEma5, currentEma10, prevEma10)) {
                // 查找最近的上升通道开始点
                TrendPoint startPoint = findLastPointByType(trendPoints, TrendPointType.START);
                
                if (startPoint != null) {
                    // 添加上升通道拐点
                    double changePercent = calculateChangePercent(currentPrice, startPoint.getPrice());
                    trendPoints.add(TrendPoint.builder()
                            .time(currentTime)
                            .price(currentPrice)
                            .changePercent(changePercent)
                            .daysFromStart(calculateDaysBetween(currentTime, startPoint.getTime()))
                            .type(TrendPointType.PEAK)
                            .build());
                    continue;
                }
            }
            
            // 判断上升通道是否结束（均线下降低于开始上升的价格）
            TrendPoint startPoint = findLastPointByType(trendPoints, TrendPointType.START);
            if (startPoint != null && currentEma5 < startPoint.getPrice() && currentEma10 < startPoint.getPrice()) {
                // 检查是否已经存在上升通道结束点
                boolean hasEndPoint = trendPoints.stream()
                        .anyMatch(p -> p.getType() == TrendPointType.END);
                
                if (!hasEndPoint) {
                    // 添加上升通道结束点
                    trendPoints.add(TrendPoint.builder()
                            .time(currentTime)
                            .price(currentPrice)
                            .type(TrendPointType.END)
                            .build());
                    continue;
                }
            }
            
            // 判断二次上升通道开启（均线下降的低点未低于上次拐点，并开始上升）
            TrendPoint endPoint = findLastPointByType(trendPoints, TrendPointType.END);
            if (endPoint != null && isUpTrendStarting(currentEma5, prevEma5, currentEma10, prevEma10) && 
                currentPrice > endPoint.getPrice()) {
                // 检查是否已经存在二次上升通道开始点
                boolean hasSecondStartPoint = trendPoints.stream()
                        .anyMatch(p -> p.getType() == TrendPointType.SECOND_START);
                
                if (!hasSecondStartPoint) {
                    // 添加二次上升通道开始点
                    trendPoints.add(TrendPoint.builder()
                            .time(currentTime)
                            .price(currentPrice)
                            .type(TrendPointType.SECOND_START)
                            .build());
                }
            }
            
            // 添加压力位
            TrendPoint peakPoint = findLastPointByType(trendPoints, TrendPointType.PEAK);
            if (startPoint != null && peakPoint != null) {
                addPressurePoints(trendPoints, currentTime, currentPrice, startPoint, peakPoint);
            }
        }
        
        // 按时间顺序排序（从旧到新）
        Collections.reverse(trendPoints);
        return trendPoints;
    }
    
    /**
     * 查找指定类型的最后一个趋势点
     */
    private TrendPoint findLastPointByType(List<TrendPoint> trendPoints, TrendPointType type) {
        for (TrendPoint point : trendPoints) {
            if (point.getType() == type) {
                return point;
            }
        }
        return null;
    }

    /**
     * 判断是否开始上升趋势
     */
    private boolean isUpTrendStarting(double currentEma5, double prevEma5, double currentEma10, double prevEma10) {
        return currentEma5 > prevEma5 && currentEma10 > prevEma10;
    }

    /**
     * 判断是否是拐点
     */
    private boolean isPeakPoint(double currentEma5, double prevEma5, double currentEma10, double prevEma10) {
        return currentEma5 < prevEma5 && currentEma10 < prevEma10;
    }

    /**
     * 计算涨跌幅
     */
    private double calculateChangePercent(double currentPrice, double startPrice) {
        return (currentPrice - startPrice) / startPrice * 100;
    }

    /**
     * 计算两个时间点之间的天数
     */
    private int calculateDaysBetween(String time1, String time2) {
        // 简单实现，假设时间格式为 "YYYY-MM-DD"
        return Math.abs(Integer.parseInt(time1.substring(0, 10).replace("-", "")) - 
                       Integer.parseInt(time2.substring(0, 10).replace("-", "")));
    }

    /**
     * 添加压力位
     */
    private void addPressurePoints(List<TrendPoint> trendPoints, String currentTime, double currentPrice,
                                 TrendPoint startPoint, TrendPoint peakPoint) {
        // 添加起始点作为压力位
        if (Math.abs(currentPrice - startPoint.getPrice()) / startPoint.getPrice() < 0.02) {
            trendPoints.add(TrendPoint.builder()
                    .time(currentTime)
                    .price(startPoint.getPrice())
                    .type(TrendPointType.PRESSURE)
                    .build());
        }

        // 添加峰值点作为压力位
        if (Math.abs(currentPrice - peakPoint.getPrice()) / peakPoint.getPrice() < 0.02) {
            trendPoints.add(TrendPoint.builder()
                    .time(currentTime)
                    .price(peakPoint.getPrice())
                    .type(TrendPointType.PRESSURE)
                    .build());
        }
    }

    /**
     * 判断当前是否处于上升趋势
     */
    private boolean isCurrentlyUpTrend(List<TrendPoint> trendPoints, double currentEma5, double currentEma10) {
        if (trendPoints.isEmpty()) {
            return false;
        }

        // 获取最近的趋势点
        TrendPoint lastPoint = trendPoints.get(trendPoints.size() - 1);
        
        // 如果最近的点是上升通道开始点或二次上升通道开始点，且均线呈上升趋势
        if ((lastPoint.getType() == TrendPointType.START || lastPoint.getType() == TrendPointType.SECOND_START) &&
            currentEma5 > lastPoint.getPrice() && currentEma10 > lastPoint.getPrice()) {
            return true;
        }

        return false;
    }

    /**
     * 生成趋势分析说明
     */
    private String generateExplanation(List<TrendPoint> trendPoints, double currentPrice) {
        if (trendPoints.isEmpty()) {
            return "暂无足够的趋势数据进行分析";
        }

        StringBuilder explanation = new StringBuilder();
        explanation.append("当前价格: ").append(String.format("%.2f", currentPrice)).append("\n");

        for (TrendPoint point : trendPoints) {
            switch (point.getType()) {
                case START:
                    explanation.append("上升通道开始 - 时间: ").append(point.getTime())
                            .append(", 价格: ").append(String.format("%.2f", point.getPrice())).append("\n");
                    break;
                case PEAK:
                    explanation.append("上升通道拐点 - 时间: ").append(point.getTime())
                            .append(", 价格: ").append(String.format("%.2f", point.getPrice()))
                            .append(", 涨跌幅: ").append(String.format("%.2f%%", point.getChangePercent()))
                            .append(", 上涨天数: ").append(point.getDaysFromStart()).append("\n");
                    break;
                case END:
                    explanation.append("上升通道结束 - 时间: ").append(point.getTime())
                            .append(", 价格: ").append(String.format("%.2f", point.getPrice())).append("\n");
                    break;
                case SECOND_START:
                    explanation.append("二次上升通道开启 - 时间: ").append(point.getTime())
                            .append(", 价格: ").append(String.format("%.2f", point.getPrice())).append("\n");
                    break;
                case PRESSURE:
                    explanation.append("压力位 - 时间: ").append(point.getTime())
                            .append(", 价格: ").append(String.format("%.2f", point.getPrice())).append("\n");
                    break;
            }
        }

        return explanation.toString();
    }
} 