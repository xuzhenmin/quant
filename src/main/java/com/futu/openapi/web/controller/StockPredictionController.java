package com.futu.openapi.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.futu.openapi.scraper.StockPricePredictor;
import com.futu.openapi.scraper.model.PredictionResult;
import com.futu.openapi.trade.run.WatchGoodStocks;
import com.futu.openapi.trade.run.analysis.CyclicalAnalysis;
import com.futu.openapi.trade.run.util.sink.Sink2DingDing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StockPredictionController {

    private static final Logger LOGGER = LogManager.getLogger(StockPredictionController.class);

    @Autowired
    private StockPricePredictor stockPricePredictor;

    @GetMapping("/stock/prediction")
    public String showPredictionPage() {
        return "stock/prediction";
    }

    @GetMapping("/b/{num}")
    public String redirect(@PathVariable String num) {
        LOGGER.info("redirect num:{}:", num);
        return "stock/prediction";
    }

    @GetMapping("/blog/{num}")
    public String redirectB(@PathVariable String num) {
        LOGGER.info("redirect blog num:{}:", num);
        return "stock/prediction";
    }

    @GetMapping("/")
    public String redirect() {
        LOGGER.info("redirect !");
        return "stock/prediction";
    }

    @GetMapping("/watch")
    @ResponseBody
    public String watch() {
        LOGGER.info("watch good stocks ！！！！");

        //任务执行
        WatchGoodStocks.run();
        CyclicalAnalysis.analysisPeak();
        //Sink2Ding.sendMsg();
        Sink2DingDing.sendMarkdownMessage("invest day!");
        return "watching";
    }

    @PostMapping("/stock/prediction/data")
    @ResponseBody
    public Map<String, Object> getPredictionData(@RequestParam String stockCode) {
        List<PredictionResult> results = stockPricePredictor.predictStockPrice(stockCode);

        // 准备图表数据
        List<String> dates = results.stream()
            .map(PredictionResult::getDate)
            .collect(Collectors.toList());

        List<Double> prices = results.stream()
            .map(PredictionResult::getPrice)
            .collect(Collectors.toList());

        List<Double> ema5 = results.stream()
            .map(PredictionResult::getEma5)
            .collect(Collectors.toList());

        List<Double> ema10 = results.stream()
            .map(PredictionResult::getEma10)
            .collect(Collectors.toList());

        // 添加预测说明和历史数据标记
        List<String> explanations = results.stream()
            .map(PredictionResult::getExplanation)
            .collect(Collectors.toList());

        List<Boolean> isHistorical = results.stream()
            .map(PredictionResult::isHistorical)
            .collect(Collectors.toList());

        // 添加涨跌幅数据
        List<Double> changePercents = results.stream()
            .map(PredictionResult::getChangePercent)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("dates", dates);
        response.put("prices", prices);
        response.put("ema5", ema5);
        response.put("ema10", ema10);
        response.put("stockCode", stockCode);
        response.put("explanations", explanations);
        response.put("isHistorical", isHistorical);
        response.put("changePercents", changePercents);

        return response;
    }
} 
