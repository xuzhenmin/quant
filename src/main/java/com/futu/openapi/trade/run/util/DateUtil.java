/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

/**
 * @author zhenmin
 * @version $Id: DateUtil.java, v 0.1 2024-04-12 14:52 xuxu Exp $$
 */
public class DateUtil {

    public static List<LocalDate> recently(int x) {

        // 获得今天的日期
        LocalDate today = LocalDate.now();

        List<LocalDate> localDates = Lists.newArrayList();
        localDates.add(today);
        // 输出今天以及接下来29天的日期
        for (int i = 0; i < x; i++) {
            // 将日期向前推一天
            today = today.minusDays(1);
            localDates.add(today);
        }

        return localDates;
    }

    public static boolean isSameDay(String today, String... days) {

        boolean sameDay = false;
        // 定义日期时间的格式
        LocalDate localDate1 = parseDateString(today);
        for (String day : days) {
            LocalDate localDate2 = parseDateString(day);
            // 比较两个 LocalDate 对象
            if (localDate1.equals(localDate2)) {
                sameDay = true;
                break;
            }
        }
        return sameDay;
    }

    // 尝试解析日期的多种格式
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withLocale(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyyMMdd HH:mm").withLocale(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyyMMdd").withLocale(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").withLocale(Locale.ENGLISH)
    );

    private static LocalDate parseDateString(String dateString) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // 忽略解析错误，尝试下一个格式
            }
        }
        // 所有格式尝试结束后还未成功解析，则抛出异常或返回 null
        return null;
    }

    public static int whatsDay(String date) {

        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date theDate = sdf.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(theDate);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                return Calendar.MONDAY;
            }
            //日期加一天
            cal.add(Calendar.DATE, 1);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                return Calendar.TUESDAY;
            }
            cal.add(Calendar.DATE, 1);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                return Calendar.WEDNESDAY;
            }
            cal.add(Calendar.DATE, 1);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                return Calendar.THURSDAY;
            }
            cal.add(Calendar.DATE, 1);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                return Calendar.FRIDAY;
            }
            cal.add(Calendar.DATE, 1);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                return Calendar.SATURDAY;
            }
            cal.add(Calendar.DATE, 1);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return Calendar.SUNDAY;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }

}