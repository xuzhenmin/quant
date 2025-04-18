/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhenmin
 * @version $Id: ThreeFinger.java, v 0.1 2024-04-03 17:18 xuxu Exp $$
 */
@Getter
@Setter
public class ThreeFinger {

    private Double lowPrice_1;
    private String lowPrice_time_1;

    private Double lowPrice_2;
    private String lowPrice_time_2;

    private Double lowPrice_3;
    private String lowPrice_time_3;

    private Double highPrice_1;
    private String highPrice_time_1;

    private Double highPrice_2;
    private String highPrice_time_2;

    private Double highPrice_3;
    private String highPrice_time_3;

}