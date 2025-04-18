/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util;

import java.util.List;

import com.futu.openapi.pb.QotCommon.KLine;
import com.futu.openapi.trade.graph.CreatLineChart;
import com.futu.openapi.trade.graph.Serie;
import com.google.common.collect.Lists;
import org.jfree.chart.ChartPanel;

/**
 * @author zhenmin
 * @version $Id: GraphUtil.java, v 0.1 2024-04-15 10:31 xuxu Exp $$
 */
public class GraphUtil {

    /**
     * @param title
     * @param code
     * @param xTitle
     * @param yTitle
     * @param X
     * @param Y
     */
    public static void drawLine(String title, String code, String xTitle, String yTitle, List<String> X,
                                List<Double> Y) {

        List<Serie> serieList = Lists.newArrayList();
        Serie serie = new Serie(code, Y.toArray());
        serieList.add(serie);
        new CreatLineChart().createChartSwing(title, xTitle, yTitle, X, serieList);

    }

    /**
     * TODO  可以通过调用这个方法, 提供对应格式的参数即可生成图片,并存在指定位置
     * 生成一个这先出并保存为png格式,
     *
     * @param title     图片标题
     * @param xtitle    x轴标题
     * @param ytitle    y轴标题
     * @param filepath  文件路径+文件名
     * @param categorie 横坐标类型
     * @param series    数据内容
     * @param width     图片宽度
     * @param height    图片高度
     */
    public static void createNewLineChartForPng(String title, String code, String xtitle, String ytitle,
                                                String filepath,
                                                List<String> categorie, List<Double> doubleList, int width,
                                                int height) {

        try {
            List<Serie> serieList = Lists.newArrayList();
            Serie serie = new Serie(code, doubleList.toArray());
            serieList.add(serie);

            ChartPanel chartPanel = new CreatLineChart().createChart(title, xtitle, ytitle, categorie, serieList);
            //将图片保存为png格式
            CreatLineChart.saveAsFile(chartPanel.getChart(), filepath, width, height);
        } catch (Exception e) {e.printStackTrace();}
    }

}