/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

import com.futu.openapi.trade.graph.CreatLineChart;
import com.futu.openapi.trade.graph.Serie;
import com.google.common.collect.Lists;
import org.jfree.chart.ChartPanel;

/**
 * @author zhenmin
 * @version $Id: IOUtil.java, v 0.1 2024-04-08 20:51 xuxu Exp $$
 */
public class IOUtil {

    public static void write(String file, List<String> data, boolean append) {
        write(file, data, append, true);
    }

    public static void write(String file, List<String> data, boolean append, boolean hasBr) {

        /* 写入Txt文件 */
        File writeName = new File(file); // 相对路径，如果没有则要建立一个新的output。txt文件
        BufferedWriter out = null;
        try {
            if (!writeName.exists()) {
                if (!writeName.getParentFile().exists()) {
                    writeName.getParentFile().mkdirs();
                }
                writeName.createNewFile(); // 创建新文件
            }
            out = new BufferedWriter(new FileWriter(writeName, append));
            for (String dt : data) {
                if (hasBr) {
                    dt = dt.replaceAll("\n", "");
                }
                out.write(dt + "\r\n"); // \r\n即为换行
            }
            out.flush(); // 把缓存区内容压入文件
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close(); // 最后记得关闭文件
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void write(String file, List<String> data) {

        write(file, data, true);
    }

    public static List<String> read(String file) {

        File fileName = new File(file);

        List<String> dataList = Lists.newArrayList();
        try {
            if (!fileName.exists()) {
                return dataList;
            }
            InputStreamReader reader = new InputStreamReader(
                new FileInputStream(fileName)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            line = br.readLine();
            if (line != null) {
                dataList.add(line);
            }
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                dataList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataList;
    }

}