/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author zhenmin
 * @version $Id: FindHigher.java, v 0.1 2024-04-08 14:47 xuxu Exp $$
 */
public class FindHigher {

    public static List<Peak<String, String, String, Double>> findHighStage(List<Double> highList, List<String> times,
                                                                           int step,
                                                                           int count) {
        List<Peak<String, String, String, Double>> peaks = Lists.newArrayList();

        if (highList == null || highList.size() < 1) {
            return peaks;
        }

        /*  ----- 初始化  --- */

        //N = one
        int peak_n = 1;

        for (int i = 0; i < highList.size(); i++) {
            //st1、找到第一个顶点
            int more_i = more(highList.get(i), highList, i, 2);
            //超过连续上涨步长，记录当前第一个点
            if ((more_i - i) > step) {
                //设置不超限制
                if (more_i >= highList.size()) {
                    more_i = highList.size() - 1;
                }
                highRecorder(peaks, peak_n, highList.get(more_i), times.get(more_i), times.get(i));
                i = more_i;
                peak_n++;
                if (peak_n >= count) {
                    break;
                }
            }
        }
        return peaks;
    }

    /**
     * 找到更大的则返回 i + n
     * 未找到则返回 i
     *
     * @param left
     * @param hl
     * @param i
     * @param range
     * @return
     */
    private static int more(Double left, List<Double> hl, int i, int range) {

        Double temp_right = 0d;

        boolean isMore = false;
        int more_i = i;
        for (int j = 1; j <= range; j++) {
            if (hl.size() > i + j && (temp_right = hl.get(i + j)) >= left) {
                more_i = j;
                isMore = true;
                break;
            }
        }
        if (isMore) {
            return more(temp_right, hl, i + more_i, range);
        }
        return more_i;
    }

    private static void highRecorder(List<Peak<String, String, String, Double>> peaks, int n, Double h_temp,
                                     String time,
                                     String beginTime) {

        peaks.add(new Peak<String, String, String, Double>().put("" + n, beginTime
            , time, h_temp));
    }

    /**
     * 检查是否连涨
     *
     * @param peaks
     * @param peakNum
     * @return
     */
    public static boolean checkPeak(List<Peak<String, String, String, Double>> peaks, int peakNum) {

        if (peaks.size() < peakNum) {
            return false;
        }

        Double tempPrice = 0d;
        for (Peak<String, String, String, Double> p :
            peaks) {

            if (tempPrice >= p.getPrice()) {
                return false;
            }
            tempPrice = p.getPrice();
        }
        return true;
    }

}