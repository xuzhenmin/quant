package com.futu.openapi.scraper.model;

import lombok.Data;
import lombok.Builder;

/**
 * 趋势关键点
 */
@Data
@Builder
public class TrendPoint {
    private String time;
    private double price;
    private double changePercent;
    private int daysFromStart;
    private TrendPointType type;
} 