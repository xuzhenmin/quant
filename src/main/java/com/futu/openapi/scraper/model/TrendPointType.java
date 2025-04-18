package com.futu.openapi.scraper.model;

/**
 * 趋势点类型枚举
 */
public enum TrendPointType {
    START,           // 上升通道开始点
    PEAK,           // 上升通道拐点
    END,            // 上升通道结束点
    SECOND_START,   // 二次上升通道开始点
    PRESSURE        // 压力位
} 