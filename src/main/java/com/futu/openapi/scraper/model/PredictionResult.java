package com.futu.openapi.scraper.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class PredictionResult {
    private String date;
    private double price;
    private String explanation;
    private boolean isHistorical;
    private double ema5;
    private double ema10;
    private double changePercent;  // 涨跌幅

    public void setDate(String date) {
        // 使用DateTimeFormatter将日期格式化为YYYYMMDD
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
        this.date = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
} 