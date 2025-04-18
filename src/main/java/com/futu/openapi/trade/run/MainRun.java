/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run;

import com.futu.openapi.trade.run.worker.TimerWorker;

/**
 * @author zhenmin
 * @version $Id: MainRun.java, v 0.1 2024-05-14 16:53 xuxu Exp $$
 */
public class MainRun {

    public static void main(String[] args) {

        System.out.println("start ....");
        TimerWorker.scheduleTask();

        System.out.println("end ~");

    }
}