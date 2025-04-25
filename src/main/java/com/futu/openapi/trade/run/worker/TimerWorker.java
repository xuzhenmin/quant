/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.worker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.scraper.HKEXScraperController;
import com.futu.openapi.trade.run.WatchGoodStocks;
import com.futu.openapi.trade.run.analysis.CyclicalAnalysis;
import com.futu.openapi.trade.run.util.DateUtil;
import com.futu.openapi.trade.run.util.PropUtil;
import com.futu.openapi.trade.run.util.sink.Sink2DingDing;
import com.futu.openapi.trade.support.SecurityKline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhenmin
 * @version $Id: TimerWorker.java, v 0.1 2024-05-14 16:44 xuxu Exp $$
 */
public class TimerWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerWorker.class);

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    static {
        PropUtil.readProp();
    }

    public static void scheduleTask() {

        LOGGER.info("定时任务注册成功！！！！");

        Runnable task = () -> {
            // 检查当天是否是工作日（周一到周五）
            Calendar now = Calendar.getInstance();
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {

                try {
                    String code = "000338";
                    //int market = QotMarket.QotMarket_CNSZ_Security_VALUE;
                    SecurityKline securityKline = SecurityKline.newInstance();
                    securityKline.createCon();
                    List<KLine> kLines = securityKline.queryKline(Security.newBuilder().setCode(code)
                        .setMarket(QotMarket.QotMarket_CNSZ_Security_VALUE).build(), 30, 1000);
                    if (DateUtil.isSameDay(kLines.get(0).getTime(), sdf.format(new Date()))) {
                        LOGGER.info("今天是交易日！！！！");

                        //任务执行
                        WatchGoodStocks.run();
                        CyclicalAnalysis.analysisPeak();
                        //Sink2Ding.sendMsg();

			Sink2DingDing.sendMarkdownMessage("invest day!");
                    }

                } catch (Exception e) {e.printStackTrace();}

            } else {
                LOGGER.info("今天是周末，不执行任务。");
            }
        };

        // 计算从现在开始到下一个工作日15:30的延迟时间
        long delay = computeDelayUntilNextTaskTime(16, 30);
        long period = TimeUnit.DAYS.toMillis(1);  // 每天重复

        scheduler.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @param targetHour
     * @param targetMinute
     * @return
     */
    private static long computeDelayUntilNextTaskTime(int targetHour, int targetMinute) {
        Calendar nextTime = Calendar.getInstance();
        nextTime.set(Calendar.HOUR_OF_DAY, targetHour);
        nextTime.set(Calendar.MINUTE, targetMinute);
        nextTime.set(Calendar.SECOND, 0);
        nextTime.set(Calendar.MILLISECOND, 0);

        if (nextTime.before(Calendar.getInstance())) {
            nextTime.add(Calendar.DATE, 1);  // 如果时间已过，设置为明天的这个时间
        }

        // 确保设置的是下一个工作日
        while (nextTime.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            nextTime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            nextTime.add(Calendar.DATE, 1);
        }

        long delay = nextTime.getTimeInMillis() - System.currentTimeMillis();
        return delay;
    }

}
