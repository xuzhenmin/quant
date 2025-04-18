/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

/**
 * @author zhenmin
 * @version $Id: PropUtil.java, v 0.1 2024-04-09 20:24 xuxu Exp $$
 */
public class PropUtil {

    private static final Map<String, String> configs = Maps.newConcurrentMap();

    public static void readProp() {
        try {
            if (configs.size() > 0) {
                return;
            }

            Properties properties = new Properties();
            // 使用ClassLoader加载properties配置文件生成对应的输入流
            InputStream in = new BufferedInputStream(new FileInputStream(new File("config/config.properties")));
            // 使用properties对象加载输入流
            properties.load(in);
            //获取key对应的value值
            configs.put("userId", properties.getProperty("userId"));
            configs.put("trdAcc", properties.getProperty("trdAcc"));
            configs.put("unlockTradePwdMd5", properties.getProperty("unlockTradePwdMd5"));
            configs.put("opendIP", properties.getProperty("opendIP"));
            configs.put("opendPort", properties.getProperty("opendPort"));
            configs.put("step", properties.getProperty("step"));
            configs.put("peak", properties.getProperty("peak"));
            configs.put("bearBullWeek", properties.getProperty("bearBullWeek"));
            configs.put("bearBullRate", properties.getProperty("bearBullRate"));

            configs.put("OSS_ACCESS_KEY", properties.getProperty("OSS_ACCESS_KEY"));
            configs.put("OSS_ACCESS_KEY_SECRET", properties.getProperty("OSS_ACCESS_KEY_SECRET"));

            configs.put("RECENTLY_DAY", properties.getProperty("RECENTLY_DAY"));

        } catch (Exception e) {e.printStackTrace();}
    }

    public static String getProp(String key) {

        return configs.get(key);
    }

}