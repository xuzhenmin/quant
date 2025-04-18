/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhenmin
 * @version $Id: TripleWave.java, v 0.1 2022-04-20 15:36 xuxu Exp $$
 */
@Getter
@Setter
public class TripleWave {

    private String beginLow_time_1;
    private Double lowPrice_1;
    private String lowPrice_time_1;

    private String beginLow_time_2;
    private Double lowPrice_2;
    private String lowPrice_time_2;

    private String beginLow_time_3;
    private Double lowPrice_3;
    private String lowPrice_time_3;

    private String beginHigh_time_1;
    private Double highPrice_1;
    private String highPrice_time_1;

    private String beginHigh_time_2;
    private Double highPrice_2;
    private String highPrice_time_2;

    private String beginHigh_time_3;
    private Double highPrice_3;
    private String highPrice_time_3;

}