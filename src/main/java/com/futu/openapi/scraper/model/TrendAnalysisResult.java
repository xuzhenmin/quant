package com.futu.openapi.scraper.model;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class TrendAnalysisResult {
    private boolean isUpTrend;
    private String explanation;
    private List<TrendPoint> trendPoints;
    private double currentPrice;
    private double ema5;
    private double ema10;
} 