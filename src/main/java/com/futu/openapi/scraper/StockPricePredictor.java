package com.futu.openapi.scraper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.pb.QotGetCapitalFlow.CapitalFlowItem;
import com.futu.openapi.trade.model.CapitalDistribution;
import com.futu.openapi.trade.service.CapitalService;
import com.futu.openapi.scraper.model.PredictionResult;
import com.futu.openapi.trade.run.util.CodeInfo;
import com.futu.openapi.trade.run.util.data.DataUtil;
import com.futu.openapi.trade.run.funs.EMA;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 股票价格预测器
 * 基于技术指标、资金流向和成交量分析预测股票未来价格走势
 */
@Data
@Builder
class StockPrediction {
    private String stockCode;
    private List<PredictionResult> results;
}

@Service
public class StockPricePredictor {
    private static final Logger logger = LoggerFactory.getLogger(StockPricePredictor.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 文件相关常量
    private static final String DATA_DIR = "data";
    private static final String PREDICTION_FILE_PREFIX = "prediction.txt";

    // 预测相关常量
    private static final int PREDICTION_DAYS = 5;
    private static final int HISTORY_DAYS = 30;

    // 技术指标相关常量
    private static final int MA_PERIOD = 20;
    private static final int EMA5_PERIOD = 5;
    private static final int EMA10_PERIOD = 10;
    private static final int EMA12_PERIOD = 12;
    private static final int EMA26_PERIOD = 26;
    private static final int MACD_SIGNAL_PERIOD = 9;
    private static final int RSI_PERIOD = 14;
    private static final double RSI_OVERBOUGHT = 70;
    private static final double RSI_OVERSOLD = 30;
    private static final double RSI_UPPER_WARNING = 60;
    private static final double RSI_LOWER_WARNING = 40;
    private static final double BOLLINGER_STD_DEV = 2.0;

    // 市场指标相关常量
    private static final double HIGH_TURNOVER_RATE = 5.0;
    private static final double LOW_TURNOVER_RATE = 1.0;
    private static final double HIGH_AMPLITUDE = 5.0;
    private static final double LOW_AMPLITUDE = 1.0;

    // 资金流向相关常量
    private static final double STRONG_CAPITAL_FLOW = 0.03;
    private static final double MEDIUM_CAPITAL_FLOW = 0.02;
    private static final double WEAK_CAPITAL_FLOW = 0.01;

    // 时间周期相关常量
    private static final int SHORT_TERM_PERIOD = 60;
    private static final int MEDIUM_TERM_PERIOD = 120;
    private static final int LONG_TERM_PERIOD = 240;

    // 历史数据缓存
    private List<Double> closePrices;
    private List<Double> highPrices;
    private List<Double> lowPrices;
    private List<Double> volumes;
    private List<Double> turnoverRates;
    private List<Double> amplitudes;
    private List<String> dates;
    private List<CapitalFlowItem> capitalFlowItems;
    private CapitalDistribution capitalDistribution;

    /**
     * 成交量分析结果
     */
    private static class VolumeAnalysis {
        double avgVolume;
        double maxVolume;
        double volumeStdDev;
        boolean isShortTermHigh;
        boolean isShortTermLow;
        boolean isMediumTermHigh;
        boolean isMediumTermLow;
        boolean isLongTermHigh;
        boolean isLongTermLow;
    }

    @Autowired
    private CapitalService capitalService;

    /**
     * 预测股票价格
     *
     * @param stockCode 股票代码
     * @return 预测结果列表
     */
    public List<PredictionResult> predictStockPrice(String stockCode) {
        try {
            // 检查缓存
            List<PredictionResult> cachedResults = loadCachedPrediction(stockCode);
            if (cachedResults != null) {
                logger.info("Using cached prediction data for stock: {}", stockCode);
                return cachedResults;
            }

            // 获取市场信息
            Integer market = getMarketFromStockCode(stockCode);
            if (market == null) {
                logger.error("Invalid stock code format: {}", stockCode);
                return new ArrayList<>();
            }

            // 准备股票代码信息
            CodeInfo codeInfo = createCodeInfo(stockCode, market);

            // 获取K线数据
            List<KLine> kLines = getKLineData(codeInfo);
            if (kLines == null || kLines.size() < MA_PERIOD) {
                logger.error("Insufficient data for prediction");
                return new ArrayList<>();
            }

            // 提取并处理历史数据
            extractHistoricalData(kLines);

            // 获取资金流向数据
            fetchCapitalFlowData(stockCode, market);

            // 创建预测结果
            List<PredictionResult> results = new ArrayList<>();

            // 添加历史数据
            addHistoricalResults(results);

            // 预测未来价格
            predictFuturePrices(results);

            // 保存预测结果
            savePredictionResults(stockCode, results);

            return results;

        } catch (Exception e) {
            logger.error("Error predicting stock price: {}", e.getMessage());
            throw new RuntimeException("Failed to predict stock price: " + e.getMessage(), e);
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
     * 提取历史数据
     */
    private void extractHistoricalData(List<KLine> kLines) {
        closePrices = new ArrayList<>();
        highPrices = new ArrayList<>();
        lowPrices = new ArrayList<>();
        volumes = new ArrayList<>();
        turnoverRates = new ArrayList<>();
        amplitudes = new ArrayList<>();
        dates = new ArrayList<>();

        for (KLine kLine : kLines) {
            closePrices.add(kLine.getClosePrice());
            highPrices.add(kLine.getHighPrice());
            lowPrices.add(kLine.getLowPrice());
            volumes.add((double)kLine.getVolume());
            turnoverRates.add(calculateTurnoverRate(kLine));
            amplitudes.add(calculateAmplitude(kLine));
            dates.add(kLine.getTime());
        }
    }

    /**
     * 计算换手率
     */
    private double calculateTurnoverRate(KLine kLine) {
        return (kLine.getVolume() * kLine.getClosePrice()) / (kLine.getTurnover() * 100);
    }

    /**
     * 计算振幅
     */
    private double calculateAmplitude(KLine kLine) {
        return (kLine.getHighPrice() - kLine.getLowPrice()) / kLine.getClosePrice() * 100;
    }

    /**
     * 获取资金流向数据
     */
    private void fetchCapitalFlowData(String stockCode, Integer market) {
        try {
            String[] sts = stockCode.split("\\.");
            capitalFlowItems = capitalService.queryCapitalFlow(sts[0], market);
            capitalDistribution = capitalService.queryCapitalDistribution(sts[0], market);
        } catch (Exception e) {
            logger.error("Failed to get capital flow data: {}", e.getMessage());
        }
    }

    /**
     * 添加历史数据结果
     */
    private void addHistoricalResults(List<PredictionResult> results) {
        int startIndex = Math.max(0, closePrices.size() - HISTORY_DAYS);
        List<Double> ema5Values = EMA.calculateEMA(closePrices, EMA5_PERIOD);
        List<Double> ema10Values = EMA.calculateEMA(closePrices, EMA10_PERIOD);

        for (int i = startIndex; i < closePrices.size(); i++) {
            double ema5 = ema5Values.get(i);
            double ema10 = ema10Values.get(i);

            if (!Double.isNaN(ema5) && !Double.isNaN(ema10)) {
                double changePercent = calculateChangePercent(i, startIndex);
                results.add(createPredictionResult(
                    dates.get(i),
                    closePrices.get(i),
                    "历史数据",
                    true,
                    ema5,
                    ema10,
                    changePercent
                ));
            }
        }
    }

    /**
     * 计算涨跌幅
     */
    private double calculateChangePercent(int currentIndex, int startIndex) {
        if (currentIndex <= startIndex) {
            return 0.0;
        }
        return (closePrices.get(currentIndex) - closePrices.get(currentIndex - 1)) /
            closePrices.get(currentIndex - 1) * 100;
    }

    /**
     * 创建预测结果对象
     */
    private PredictionResult createPredictionResult(String date, double price, String explanation,
                                                    boolean isHistorical, double ema5, double ema10,
                                                    double changePercent) {
        return PredictionResult.builder()
            .date(date)
            .price(price)
            .explanation(explanation)
            .isHistorical(isHistorical)
            .ema5(Double.parseDouble(String.format("%.2f", ema5)))
            .ema10(Double.parseDouble(String.format("%.2f", ema10)))
            .changePercent(changePercent)
            .build();
    }

    /**
     * 预测未来价格
     */
    private void predictFuturePrices(List<PredictionResult> results) {
        double lastPrice = closePrices.get(closePrices.size() - 1);
        double trendStrength = calculateTrendStrength(closePrices);
        LocalDate currentDate = LocalDate.now();

        // 创建预测数据列表
        List<Double> predictedClosePrices = new ArrayList<>(closePrices);
        List<Double> predictedHighPrices = new ArrayList<>(highPrices);
        List<Double> predictedLowPrices = new ArrayList<>(lowPrices);
        List<Double> predictedVolumes = new ArrayList<>(volumes);
        List<Double> predictedTurnoverRates = new ArrayList<>(turnoverRates);
        List<Double> predictedAmplitudes = new ArrayList<>(amplitudes);

        for (int i = 1; i <= PREDICTION_DAYS; i++) {
            // 计算技术指标
            TechnicalIndicators indicators = calculateTechnicalIndicators(predictedClosePrices);

            // 预测价格
            double predictedPrice = predictNextPrice(
                lastPrice,
                indicators.currentMA,
                indicators.currentRSI,
                indicators.currentMACD,
                indicators.currentUpperBand,
                indicators.currentLowerBand,
                trendStrength,
                predictedVolumes.get(predictedVolumes.size() - 1),
                predictedTurnoverRates.get(predictedTurnoverRates.size() - 1),
                predictedAmplitudes.get(predictedAmplitudes.size() - 1),
                indicators.prevMACD
            );

            // 更新预测数据
            updatePredictedData(predictedClosePrices, predictedHighPrices, predictedLowPrices,
                predictedVolumes, predictedTurnoverRates, predictedAmplitudes,
                predictedPrice, trendStrength);

            // 生成预测说明
            String explanation = generatePredictionExplanation(
                lastPrice,
                predictedPrice,
                indicators.currentRSI,
                indicators.currentMACD,
                indicators.currentUpperBand,
                indicators.currentLowerBand,
                trendStrength,
                predictedVolumes.get(predictedVolumes.size() - 1),
                predictedTurnoverRates.get(predictedTurnoverRates.size() - 1),
                predictedAmplitudes.get(predictedAmplitudes.size() - 1),
                indicators.prevMACD
            );

            // 添加预测结果
            results.add(createPredictionResult(
                currentDate.plusDays(i).format(DateTimeFormatter.ISO_DATE),
                predictedPrice,
                explanation,
                false,
                indicators.currentMA,
                indicators.currentMA,
                (predictedPrice - lastPrice) / lastPrice * 100
            ));

            lastPrice = predictedPrice;
        }
    }

    /**
     * 技术指标类
     */
    private static class TechnicalIndicators {
        double currentMA;
        double currentRSI;
        double currentMACD;
        double prevMACD;
        double currentUpperBand;
        double currentLowerBand;
    }

    /**
     * 计算技术指标
     */
    private TechnicalIndicators calculateTechnicalIndicators(List<Double> prices) {
        TechnicalIndicators indicators = new TechnicalIndicators();

        double[] maValues = calculateMA(prices);
        double[] rsiValues = calculateRSI(prices);
        double[] macdValues = calculateMACD(prices);
        double[] bollingerBands = calculateBollingerBands(prices);

        indicators.currentMA = maValues[maValues.length - 1];
        indicators.currentRSI = rsiValues[rsiValues.length - 1];
        indicators.currentMACD = macdValues[macdValues.length - 1];
        indicators.prevMACD = macdValues.length > 1 ? macdValues[macdValues.length - 2] : 0;
        indicators.currentUpperBand = bollingerBands[0];
        indicators.currentLowerBand = bollingerBands[1];

        return indicators;
    }

    /**
     * 更新预测数据
     */
    private void updatePredictedData(List<Double> predictedClosePrices, List<Double> predictedHighPrices,
                                     List<Double> predictedLowPrices, List<Double> predictedVolumes,
                                     List<Double> predictedTurnoverRates, List<Double> predictedAmplitudes,
                                     double predictedPrice, double trendStrength) {
        predictedClosePrices.add(predictedPrice);

        double currentAmplitude = predictedAmplitudes.get(predictedAmplitudes.size() - 1);
        double predictedHigh = predictedPrice * (1 + currentAmplitude / 200);
        double predictedLow = predictedPrice * (1 - currentAmplitude / 200);
        predictedHighPrices.add(predictedHigh);
        predictedLowPrices.add(predictedLow);

        double currentVolume = predictedVolumes.get(predictedVolumes.size() - 1);
        double predictedVolume = currentVolume * (1 + trendStrength * 0.1);
        predictedVolumes.add(predictedVolume);

        double currentTurnoverRate = predictedTurnoverRates.get(predictedTurnoverRates.size() - 1);
        double predictedTurnoverRate = currentTurnoverRate * (1 + trendStrength * 0.05);
        predictedTurnoverRates.add(predictedTurnoverRate);

        double amplitudeChange = trendStrength * 0.1;
        double predictedAmplitude = currentAmplitude * (1 + amplitudeChange);
        predictedAmplitude = Math.max(LOW_AMPLITUDE, Math.min(HIGH_AMPLITUDE, predictedAmplitude));
        predictedAmplitudes.add(predictedAmplitude);
    }

    /**
     * 计算移动平均线
     */
    private double[] calculateMA(List<Double> prices) {
        double[] ma = new double[prices.size() - MA_PERIOD + 1];
        for (int i = MA_PERIOD - 1; i < prices.size(); i++) {
            double sum = 0;
            for (int j = 0; j < MA_PERIOD; j++) {
                sum += prices.get(i - j);
            }
            ma[i - MA_PERIOD + 1] = sum / MA_PERIOD;
        }
        return ma;
    }

    /**
     * 计算相对强弱指标
     */
    private double[] calculateRSI(List<Double> prices) {
        double[] rsi = new double[prices.size() - RSI_PERIOD];
        double[] gains = new double[prices.size() - 1];
        double[] losses = new double[prices.size() - 1];

        // 计算价格变化
        for (int i = 1; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            gains[i - 1] = Math.max(0, change);
            losses[i - 1] = Math.max(0, -change);
        }

        // 计算RSI
        for (int i = RSI_PERIOD; i < prices.size(); i++) {
            double avgGain = 0;
            double avgLoss = 0;
            for (int j = 0; j < RSI_PERIOD; j++) {
                avgGain += gains[i - j - 1];
                avgLoss += losses[i - j - 1];
            }
            avgGain /= RSI_PERIOD;
            avgLoss /= RSI_PERIOD;

            // 处理avgLoss为0的情况
            if (avgLoss == 0) {
                rsi[i - RSI_PERIOD] = 100;
            } else {
                double rs = avgGain / avgLoss;
                rsi[i - RSI_PERIOD] = 100 - (100 / (1 + rs));
            }
        }

        return rsi;
    }

    /**
     * 计算MACD指标
     */
    private double[] calculateMACD(List<Double> prices) {
        double[] ema12 = calculateEMA(prices, 12);
        double[] ema26 = calculateEMA(prices, 26);
        double[] macd = new double[ema26.length];
        double[] signal = new double[ema26.length]; // 信号线
        double[] histogram = new double[ema26.length]; // MACD柱状图

        // 计算MACD线
        for (int i = 0; i < ema26.length; i++) {
            macd[i] = ema12[i] - ema26[i];
        }

        // 计算信号线（9日EMA）
        List<Double> macdList = new ArrayList<>();
        for (double value : macd) {
            macdList.add(value);
        }
        double[] signalEMA = calculateEMA(macdList, 9);
        System.arraycopy(signalEMA, 0, signal, 0, signalEMA.length);

        // 计算MACD柱状图
        for (int i = 0; i < macd.length; i++) {
            histogram[i] = macd[i] - signal[i];
        }

        return macd;
    }

    /**
     * 计算指数移动平均线
     */
    private double[] calculateEMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            return new double[0];
        }

        double[] ema = new double[prices.size()];
        double multiplier = 2.0 / (period + 1);

        // 从第period个点开始计算EMA
        for (int i = period - 1; i < prices.size(); i++) {
            // 计算当前点的SMA作为第一个EMA值
            double sum = 0;
            for (int j = 0; j < period; j++) {
                sum += prices.get(i - j);
            }
            double sma = sum / period;

            // 计算当前点的EMA
            ema[i] = sma;
            for (int j = 1; j < period; j++) {
                ema[i] = (prices.get(i - j) - ema[i]) * multiplier + ema[i];
            }
        }

        // 对于前period-1个点，使用第一个有效的EMA值
        for (int i = 0; i < period - 1; i++) {
            ema[i] = ema[period - 1];
        }

        return ema;
    }

    /**
     * 计算布林带
     */
    private double[] calculateBollingerBands(List<Double> prices) {
        if (prices.size() < MA_PERIOD) {
            return new double[] {0, 0};
        }

        double[] ma = calculateMA(prices);
        double[] upperBand = new double[ma.length];
        double[] lowerBand = new double[ma.length];

        for (int i = 0; i < ma.length; i++) {
            double sum = 0;
            for (int j = 0; j < MA_PERIOD; j++) {
                sum += Math.pow(prices.get(i + j) - ma[i], 2);
            }
            double stdDev = Math.sqrt(sum / MA_PERIOD);
            upperBand[i] = ma[i] + BOLLINGER_STD_DEV * stdDev;
            lowerBand[i] = ma[i] - BOLLINGER_STD_DEV * stdDev;
        }

        return new double[] {upperBand[upperBand.length - 1], lowerBand[lowerBand.length - 1]};
    }

    /**
     * 计算趋势强度
     */
    private double calculateTrendStrength(List<Double> prices) {
        if (prices.size() < 2) {
            return 0;
        }

        // 计算价格变化率
        List<Double> priceChanges = new ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            double change = (prices.get(i) - prices.get(i - 1)) / prices.get(i - 1) * 100;
            priceChanges.add(change);
        }

        // 计算趋势强度（使用加权平均）
        double sum = 0;
        double weightSum = 0;
        for (int i = 0; i < priceChanges.size(); i++) {
            double weight = 1.0 / (i + 1); // 越近的数据权重越大
            sum += priceChanges.get(i) * weight;
            weightSum += weight;
        }

        return sum / weightSum;
    }

    /**
     * 预测下一个价格
     */
    private double predictNextPrice(double lastPrice, double ma, double rsi, double macd,
                                    double upperBand, double lowerBand, double trendStrength,
                                    double volume, double turnoverRate, double amplitude,
                                    double prevMacd) {
        // 基于多个技术指标计算预测价格
        double priceChange = 0;

        // 分析历史资金流向趋势
        if (capitalFlowItems != null && !capitalFlowItems.isEmpty() && capitalDistribution != null) {
            // 计算最近3天的资金流向趋势
            double recentNetFlow = 0;
            int lookbackDays = Math.min(3, capitalFlowItems.size());
            for (int i = 0; i < lookbackDays; i++) {
                CapitalFlowItem flowItem = capitalFlowItems.get(capitalFlowItems.size() - 1 - i);
                // 使用资金分布数据计算流向强度
                double netFlow =
                    (capitalDistribution.getCapitalInBig() + capitalDistribution.getCapitalInMid() + capitalDistribution
                        .getCapitalInSmall()) -
                        (capitalDistribution.getCapitalOutBig() + capitalDistribution.getCapitalOutMid()
                            + capitalDistribution.getCapitalOutSmall());
                recentNetFlow += netFlow;
            }
            recentNetFlow /= lookbackDays; // 计算平均净流入

            // 计算资金流向强度
            double flowStrength = recentNetFlow / (lastPrice * volume * 10000); // 转换为万元单位

            // 分析资金流向趋势的持续性
            if (flowStrength > 0.1) {
                priceChange += STRONG_CAPITAL_FLOW * 1.2; // 强资金流入且趋势持续
            } else if (flowStrength > 0.05) {
                priceChange += MEDIUM_CAPITAL_FLOW * 1.1; // 中等资金流入且趋势持续
            } else if (flowStrength > 0.01) {
                priceChange += WEAK_CAPITAL_FLOW; // 弱资金流入
            } else if (flowStrength < -0.1) {
                priceChange -= STRONG_CAPITAL_FLOW * 1.2; // 强资金流出且趋势持续
            } else if (flowStrength < -0.05) {
                priceChange -= MEDIUM_CAPITAL_FLOW * 1.1; // 中等资金流出且趋势持续
            } else if (flowStrength < -0.01) {
                priceChange -= WEAK_CAPITAL_FLOW; // 弱资金流出
            }
        }

        // 分析历史成交量变化
        if (volumes != null && !volumes.isEmpty()) {
            // 计算最近5天的平均成交量
            double recentAvgVolume = 0;
            int lookbackDays = Math.min(5, volumes.size());
            for (int i = 0; i < lookbackDays; i++) {
                recentAvgVolume += volumes.get(volumes.size() - 1 - i);
            }
            recentAvgVolume /= lookbackDays;

            // 计算成交量变化率
            double volumeChangeRate = (volume - recentAvgVolume) / recentAvgVolume;

            // 成交量显著增加且资金流入
            if (volumeChangeRate > 0.5 && priceChange > 0) {
                priceChange *= 1.2; // 放大上涨预期
            }
            // 成交量显著减少且资金流出
            else if (volumeChangeRate < -0.3 && priceChange < 0) {
                priceChange *= 1.2; // 放大下跌预期
            }
        }

        // 分析历史关键点位
        double pressureLevel = findNearestPressureLevel(lastPrice);
        double supportLevel = findNearestSupportLevel(lastPrice);

        // 计算与压力位和支撑位的距离
        double pressureDistance = (pressureLevel - lastPrice) / lastPrice * 100;
        double supportDistance = (lastPrice - supportLevel) / lastPrice * 100;

        // 根据与关键点位的距离调整价格变化
        if (pressureDistance < 5) {
            priceChange *= 0.8; // 接近压力位，降低上涨预期
        } else if (supportDistance < 5) {
            priceChange *= 1.2; // 接近支撑位，提高上涨预期
        }

        // 趋势强度影响
        if (Math.abs(trendStrength) > 1) {
            priceChange += trendStrength * 0.1;
        } else {
            priceChange += trendStrength * 0.05;
        }

        // 成交量影响
        double volumeFactor = Math.log10(volume) / 8;
        priceChange += volumeFactor * 0.01;

        // 换手率影响
        if (turnoverRate > HIGH_TURNOVER_RATE) {
            priceChange += 0.01;
        } else if (turnoverRate < LOW_TURNOVER_RATE) {
            priceChange -= 0.01;
        }

        // 振幅影响
        if (amplitude > HIGH_AMPLITUDE) {
            priceChange += 0.01;
        } else if (amplitude < LOW_AMPLITUDE) {
            priceChange -= 0.01;
        }

        // 分析历史价格附近的成交量
        VolumeAnalysis volumeAnalysis = getVolumeAnalysisAroundPrice(lastPrice);

        // 根据不同的时间周期调整价格变化
        if (volumeAnalysis.isShortTermHigh) {
            double volumeRatio = volume / volumeAnalysis.avgVolume;
            if (volumeRatio > 1.5) {
                if (priceChange > 0) {
                    priceChange *= 1.2; // 短期高点突破
                } else {
                    priceChange *= 0.8; // 短期高点压力
                }
            } else if (volumeRatio < 0.7) {
                priceChange *= 0.9; // 短期高点成交量不足
            }
        }

        if (volumeAnalysis.isMediumTermHigh) {
            double volumeRatio = volume / volumeAnalysis.avgVolume;
            if (volumeRatio > 1.5) {
                if (priceChange > 0) {
                    priceChange *= 1.3; // 中期高点突破
                } else {
                    priceChange *= 0.7; // 中期高点压力
                }
            } else if (volumeRatio < 0.7) {
                priceChange *= 0.8; // 中期高点成交量不足
            }
        }

        if (volumeAnalysis.isLongTermHigh) {
            double volumeRatio = volume / volumeAnalysis.avgVolume;
            if (volumeRatio > 1.5) {
                if (priceChange > 0) {
                    priceChange *= 1.4; // 长期高点突破
                } else {
                    priceChange *= 0.6; // 长期高点压力
                }
            } else if (volumeRatio < 0.7) {
                priceChange *= 0.7; // 长期高点成交量不足
            }
        }

        return lastPrice * (1 + priceChange);
    }

    /**
     * 查找最近的压力位
     */
    private double findNearestPressureLevel(double currentPrice) {
        double nearestPressure = Double.MAX_VALUE;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < closePrices.size() - 1; i++) {
            double price = closePrices.get(i);
            if (price > currentPrice) { // 只考虑高于当前价格的点
                double distance = price - currentPrice;
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPressure = price;
                }
            }
        }

        return nearestPressure;
    }

    /**
     * 查找最近的支撑位
     */
    private double findNearestSupportLevel(double currentPrice) {
        double nearestSupport = Double.MAX_VALUE;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < closePrices.size() - 1; i++) {
            double price = closePrices.get(i);
            if (price < currentPrice) { // 只考虑低于当前价格的点
                double distance = currentPrice - price;
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestSupport = price;
                }
            }
        }

        return nearestSupport;
    }

    /**
     * 获取特定价格的历史成交量
     */
    private double getVolumeAtPrice(double price) {
        double totalVolume = 0;
        int count = 0;

        for (int i = 0; i < closePrices.size(); i++) {
            if (Math.abs(closePrices.get(i) - price) < price * 0.01) { // 价格在1%范围内
                totalVolume += volumes.get(i);
                count++;
            }
        }

        return count > 0 ? totalVolume / count : 0;
    }

    /**
     * 获取历史价格附近的成交量分析
     */
    private VolumeAnalysis getVolumeAnalysisAroundPrice(double currentPrice) {
        VolumeAnalysis analysis = new VolumeAnalysis();
        double priceThreshold = currentPrice * 0.02; // 2%的价格波动范围

        // 获取不同时间段的起始索引
        int shortTermStart = Math.max(0, closePrices.size() - SHORT_TERM_PERIOD);
        int mediumTermStart = Math.max(0, closePrices.size() - MEDIUM_TERM_PERIOD);
        int longTermStart = Math.max(0, closePrices.size() - LONG_TERM_PERIOD);

        // 查找价格相近的历史数据点
        List<Integer> similarPriceIndices = new ArrayList<>();
        for (int i = 0; i < closePrices.size(); i++) {
            if (Math.abs(closePrices.get(i) - currentPrice) <= priceThreshold) {
                similarPriceIndices.add(i);
            }
        }

        if (!similarPriceIndices.isEmpty()) {
            // 计算平均成交量
            double totalVolume = 0;
            for (int index : similarPriceIndices) {
                totalVolume += volumes.get(index);
            }
            analysis.avgVolume = totalVolume / similarPriceIndices.size();

            // 计算最大成交量
            analysis.maxVolume = similarPriceIndices.stream()
                .mapToDouble(i -> volumes.get(i))
                .max()
                .orElse(0);

            // 计算成交量标准差
            double sumSquaredDiff = 0;
            for (int index : similarPriceIndices) {
                double diff = volumes.get(index) - analysis.avgVolume;
                sumSquaredDiff += diff * diff;
            }
            analysis.volumeStdDev = Math.sqrt(sumSquaredDiff / similarPriceIndices.size());

            // 判断是否是短期高点/低点
            analysis.isShortTermHigh = similarPriceIndices.stream()
                .filter(i -> i >= shortTermStart)
                .allMatch(i -> closePrices.get(i) >= currentPrice);
            analysis.isShortTermLow = similarPriceIndices.stream()
                .filter(i -> i >= shortTermStart)
                .allMatch(i -> closePrices.get(i) <= currentPrice);

            // 判断是否是中期高点/低点
            analysis.isMediumTermHigh = similarPriceIndices.stream()
                .filter(i -> i >= mediumTermStart)
                .allMatch(i -> closePrices.get(i) >= currentPrice);
            analysis.isMediumTermLow = similarPriceIndices.stream()
                .filter(i -> i >= mediumTermStart)
                .allMatch(i -> closePrices.get(i) <= currentPrice);

            // 判断是否是长期高点/低点
            analysis.isLongTermHigh = similarPriceIndices.stream()
                .filter(i -> i >= longTermStart)
                .allMatch(i -> closePrices.get(i) >= currentPrice);
            analysis.isLongTermLow = similarPriceIndices.stream()
                .filter(i -> i >= longTermStart)
                .allMatch(i -> closePrices.get(i) <= currentPrice);
        }

        return analysis;
    }

    /**
     * 生成预测说明
     */
    private String generatePredictionExplanation(double lastPrice, double predictedPrice,
                                                 double rsi, double macd,
                                                 double upperBand, double lowerBand,
                                                 double trendStrength,
                                                 double volume, double turnoverRate, double amplitude,
                                                 double prevMacd) {
        StringBuilder explanation = new StringBuilder();
        double changePercent = (predictedPrice - lastPrice) / lastPrice * 100;
        explanation.append("预测价格: ").append(String.format("%.2f", predictedPrice))
            .append(" (涨跌幅: ").append(String.format("%.2f", changePercent)).append("%)\n");
        explanation.append("预测依据:\n");

        // 分析资金流向趋势
        if (capitalFlowItems != null && !capitalFlowItems.isEmpty() && capitalDistribution != null) {
            double recentNetFlow = 0;
            int lookbackDays = Math.min(3, capitalFlowItems.size());
            for (int i = 0; i < lookbackDays; i++) {
                CapitalFlowItem flowItem = capitalFlowItems.get(capitalFlowItems.size() - 1 - i);
                double netFlow =
                    (capitalDistribution.getCapitalInBig() + capitalDistribution.getCapitalInMid() + capitalDistribution
                        .getCapitalInSmall()) -
                        (capitalDistribution.getCapitalOutBig() + capitalDistribution.getCapitalOutMid()
                            + capitalDistribution.getCapitalOutSmall());
                recentNetFlow += netFlow;
            }
            recentNetFlow /= lookbackDays;

            double flowStrength = recentNetFlow / (lastPrice * volume * 10000);
            if (flowStrength > 0.1) {
                explanation.append("- 资金流向: 强资金流入，趋势持续\n");
            } else if (flowStrength > 0.05) {
                explanation.append("- 资金流向: 中等资金流入，趋势持续\n");
            } else if (flowStrength > 0.01) {
                explanation.append("- 资金流向: 弱资金流入\n");
            } else if (flowStrength < -0.1) {
                explanation.append("- 资金流向: 强资金流出，趋势持续\n");
            } else if (flowStrength < -0.05) {
                explanation.append("- 资金流向: 中等资金流出，趋势持续\n");
            } else if (flowStrength < -0.01) {
                explanation.append("- 资金流向: 弱资金流出\n");
            } else {
                explanation.append("- 资金流向: 资金流向平稳\n");
            }
        }

        // 分析成交量
        if (volumes != null && !volumes.isEmpty()) {
            double recentAvgVolume = 0;
            int lookbackDays = Math.min(5, volumes.size());
            for (int i = 0; i < lookbackDays; i++) {
                recentAvgVolume += volumes.get(volumes.size() - 1 - i);
            }
            recentAvgVolume /= lookbackDays;

            double volumeChangeRate = (volume - recentAvgVolume) / recentAvgVolume;
            if (volumeChangeRate > 0.5) {
                explanation.append("- 成交量: 显著增加\n");
            } else if (volumeChangeRate > 0.2) {
                explanation.append("- 成交量: 温和增加\n");
            } else if (volumeChangeRate < -0.3) {
                explanation.append("- 成交量: 显著减少\n");
            } else if (volumeChangeRate < -0.1) {
                explanation.append("- 成交量: 温和减少\n");
            } else {
                explanation.append("- 成交量: 保持稳定\n");
            }
        }

        // 分析技术指标
        if (rsi > RSI_OVERBOUGHT) {
            explanation.append("- RSI: 超买区域，需警惕回调风险\n");
        } else if (rsi > RSI_UPPER_WARNING) {
            explanation.append("- RSI: 接近超买区域，需注意风险\n");
        } else if (rsi < RSI_OVERSOLD) {
            explanation.append("- RSI: 超卖区域，可能存在反弹机会\n");
        } else if (rsi < RSI_LOWER_WARNING) {
            explanation.append("- RSI: 接近超卖区域，可能存在机会\n");
        } else {
            explanation.append("- RSI: 处于正常区间\n");
        }

        if (macd > 0 && prevMacd < 0) {
            explanation.append("- MACD: 金叉形成，看涨信号\n");
        } else if (macd < 0 && prevMacd > 0) {
            explanation.append("- MACD: 死叉形成，看跌信号\n");
        } else if (macd > 0) {
            explanation.append("- MACD: 多头趋势\n");
        } else {
            explanation.append("- MACD: 空头趋势\n");
        }

        if (predictedPrice > upperBand) {
            explanation.append("- 布林带: 突破上轨，需注意回调风险\n");
        } else if (predictedPrice < lowerBand) {
            explanation.append("- 布林带: 突破下轨，可能存在反弹机会\n");
        } else {
            explanation.append("- 布林带: 价格在通道内运行\n");
        }

        // 分析趋势强度
        if (Math.abs(trendStrength) > 1) {
            explanation.append("- 趋势强度: ").append(trendStrength > 0 ? "强" : "弱").append("\n");
        } else {
            explanation.append("- 趋势强度: ").append(trendStrength > 0 ? "中等" : "中等偏弱").append("\n");
        }

        // 分析换手率
        if (turnoverRate > HIGH_TURNOVER_RATE) {
            explanation.append("- 换手率: 较高，市场活跃\n");
        } else if (turnoverRate < LOW_TURNOVER_RATE) {
            explanation.append("- 换手率: 较低，市场相对冷清\n");
        } else {
            explanation.append("- 换手率: 处于正常水平\n");
        }

        // 分析振幅
        if (amplitude > HIGH_AMPLITUDE) {
            explanation.append("- 振幅: 较大，波动剧烈\n");
        } else if (amplitude < LOW_AMPLITUDE) {
            explanation.append("- 振幅: 较小，波动平缓\n");
        } else {
            explanation.append("- 振幅: 处于正常水平\n");
        }

        return explanation.toString();
    }

    /**
     * 加载缓存的预测结果
     */
    private List<PredictionResult> loadCachedPrediction(String stockCode) {
        try {
            LocalDate today = LocalDate.now();
            String year = String.format("%04d", today.getYear());
            String month = String.format("%02d", today.getMonthValue());
            String day = String.format("%02d", today.getDayOfMonth());
            Path filePath = getPredictionFilePath(year, month, day);

            // 1. 创建字节流（FileInputStream）
            FileInputStream fis = new FileInputStream(filePath.toFile());

            // 2. 用 InputStreamReader 指定 UTF-8 编码
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);

            if (Files.exists(filePath)) {
                try (BufferedReader reader = new BufferedReader(isr)) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    StockPrediction[] predictions = gson.fromJson(jsonContent.toString(), StockPrediction[].class);
                    for (StockPrediction prediction : predictions) {
                        if (prediction.getStockCode().equals(stockCode)) {
                            return prediction.getResults();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error loading cached prediction: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 保存预测结果
     */
    private void savePredictionResults(String stockCode, List<PredictionResult> results) {
        try {
            LocalDate today = LocalDate.now();
            String year = String.format("%04d", today.getYear());
            String month = String.format("%02d", today.getMonthValue());
            String day = String.format("%02d", today.getDayOfMonth());
            Path filePath = getPredictionFilePath(year, month, day);
            
            // 确保目录存在
            Files.createDirectories(filePath.getParent());
            
            // 读取现有文件内容
            List<StockPrediction> existingPredictions = new ArrayList<>();
            if (Files.exists(filePath)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    StockPrediction[] predictions = gson.fromJson(jsonContent.toString(), StockPrediction[].class);
                    existingPredictions = new ArrayList<>(Arrays.asList(predictions));
                }
            }
            
            // 更新或添加新的预测结果
            boolean found = false;
            for (StockPrediction prediction : existingPredictions) {
                if (prediction.getStockCode().equals(stockCode)) {
                    prediction.setResults(results);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                existingPredictions.add(StockPrediction.builder()
                    .stockCode(stockCode)
                    .results(results)
                    .build());
            }
            
            // 保存更新后的预测结果
            // 1. 创建文件输出流
            FileOutputStream fos = new FileOutputStream(filePath.toFile());
            try (OutputStreamWriter writer =  new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                String json = gson.toJson(existingPredictions);
                writer.write(json);
            }
            
            logger.info("Prediction results saved to: {}", filePath);
        } catch (Exception e) {
            logger.error("Error saving prediction results: {}", e.getMessage());
        }
    }

    /**
     * 获取预测文件路径
     */
    private Path getPredictionFilePath(String year, String month, String day) {
        return Paths.get(DATA_DIR, year, month, day + "-" + PREDICTION_FILE_PREFIX);
    }
} 
