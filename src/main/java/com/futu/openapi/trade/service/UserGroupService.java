/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.futu.openapi.pb.QotCommon.QotMarket;
import com.futu.openapi.pb.QotCommon.SecurityStaticBasic;
import com.futu.openapi.pb.QotCommon.SecurityType;
import com.futu.openapi.trade.model.SecuInfo;
import com.futu.openapi.trade.support.UserSecurityGroup;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author zhenmin
 * @version $Id: UserGroupService.java, v 0.1 2022-04-20 15:33 xuxu Exp $$
 */
public class UserGroupService {

    private static final String SECUINFOS = "[{\"code\":\"600967\",\"market\":21,\"name\":\"内蒙一机\",\"secuType\":3},"
        + "{\"code\":\"ZTO\",\"market\":11,\"name\":\"中通快递\",\"secuType\":3},{\"code\":\"03328\",\"market\":1,"
        + "\"name\":\"交通银行\",\"secuType\":3},{\"code\":\"UXIN\",\"market\":11,\"name\":\"优信\",\"secuType\":3},"
        + "{\"code\":\"BK0968\",\"market\":21,\"name\":\"焦炭Ⅱ\",\"secuType\":7},{\"code\":\"601318\",\"market\":21,"
        + "\"name\":\"中国平安\",\"secuType\":3},{\"code\":\"601857\",\"market\":21,\"name\":\"中国石油\",\"secuType\":3},"
        + "{\"code\":\"PTR\",\"market\":11,\"name\":\"中石油\",\"secuType\":3},{\"code\":\"600188\",\"market\":21,"
        + "\"name\":\"兖矿能源\",\"secuType\":3},{\"code\":\"603259\",\"market\":21,\"name\":\"药明康德\",\"secuType\":3},"
        + "{\"code\":\"515870\",\"market\":21,\"name\":\"先进制造ETF\",\"secuType\":4},{\"code\":\"159919\","
        + "\"market\":22,\"name\":\"沪深300ETF\",\"secuType\":4},{\"code\":\"01211\",\"market\":1,\"name\":\"比亚迪股份\","
        + "\"secuType\":3},{\"code\":\"TXN\",\"market\":11,\"name\":\"德州仪器\",\"secuType\":3},{\"code\":\"002507\","
        + "\"market\":22,\"name\":\"涪陵榨菜\",\"secuType\":3},{\"code\":\"601238\",\"market\":21,\"name\":\"广汽集团\","
        + "\"secuType\":3},{\"code\":\"002371\",\"market\":22,\"name\":\"北方华创\",\"secuType\":3},{\"code\":\"GLL\","
        + "\"market\":11,\"name\":\"ProShares两倍做空黄金ETF\",\"secuType\":4},{\"code\":\"600887\",\"market\":21,"
        + "\"name\":\"伊利股份\",\"secuType\":3},{\"code\":\"600600\",\"market\":21,\"name\":\"青岛啤酒\",\"secuType\":3},"
        + "{\"code\":\"00520\",\"market\":1,\"name\":\"呷哺呷哺\",\"secuType\":3},{\"code\":\"000063\",\"market\":22,"
        + "\"name\":\"中兴通讯\",\"secuType\":3},{\"code\":\"000636\",\"market\":22,\"name\":\"风华高科\",\"secuType\":3},"
        + "{\"code\":\"RIOT\",\"market\":11,\"name\":\"Riot Blockchain\",\"secuType\":3},{\"code\":\"000166\","
        + "\"market\":22,\"name\":\"申万宏源\",\"secuType\":3},{\"code\":\"000980\",\"market\":22,\"name\":\"*ST众泰\","
        + "\"secuType\":3},{\"code\":\"300682\",\"market\":22,\"name\":\"朗新科技\",\"secuType\":3},{\"code\":\"159931\","
        + "\"market\":22,\"name\":\"金融ETF\",\"secuType\":4},{\"code\":\"603626\",\"market\":21,\"name\":\"科森科技\","
        + "\"secuType\":3},{\"code\":\"UPRO\",\"market\":11,\"name\":\"ProShares三倍做多标普500ETF\",\"secuType\":4},"
        + "{\"code\":\"00939\",\"market\":1,\"name\":\"建设银行\",\"secuType\":3},{\"code\":\"600612\",\"market\":21,"
        + "\"name\":\"老凤祥\",\"secuType\":3},{\"code\":\"300750\",\"market\":22,\"name\":\"宁德时代\",\"secuType\":3},"
        + "{\"code\":\"601789\",\"market\":21,\"name\":\"宁波建工\",\"secuType\":3},{\"code\":\"00422\",\"market\":1,"
        + "\"name\":\"越南制造加工出口\",\"secuType\":3},{\"code\":\"603156\",\"market\":21,\"name\":\"养元饮品\","
        + "\"secuType\":3},{\"code\":\"NTES\",\"market\":11,\"name\":\"网易\",\"secuType\":3},{\"code\":\"600118\","
        + "\"market\":21,\"name\":\"中国卫星\",\"secuType\":3},{\"code\":\"510410\",\"market\":21,\"name\":\"资源ETF\","
        + "\"secuType\":4},{\"code\":\"HOOD\",\"market\":11,\"name\":\"Robinhood\",\"secuType\":3},"
        + "{\"code\":\"600085\",\"market\":21,\"name\":\"同仁堂\",\"secuType\":3},{\"code\":\"ATVI\",\"market\":11,"
        + "\"name\":\"动视暴雪\",\"secuType\":3},{\"code\":\"300188\",\"market\":22,\"name\":\"美亚柏科\",\"secuType\":3},"
        + "{\"code\":\"159875\",\"market\":22,\"name\":\"新能源ETF\",\"secuType\":4},{\"code\":\"000016\",\"market\":21,"
        + "\"name\":\"上证50\",\"secuType\":6},{\"code\":\"SBUX\",\"market\":11,\"name\":\"星巴克\",\"secuType\":3},"
        + "{\"code\":\"000488\",\"market\":22,\"name\":\"晨鸣纸业\",\"secuType\":3},{\"code\":\"JNUG\",\"market\":11,"
        + "\"name\":\"每日两倍做多小型金矿指数-Direxion\",\"secuType\":4},{\"code\":\"002589\",\"market\":22,\"name\":\"瑞康医药\","
        + "\"secuType\":3},{\"code\":\"688169\",\"market\":21,\"name\":\"石头科技\",\"secuType\":3},{\"code\":\"601628\","
        + "\"market\":21,\"name\":\"中国人寿\",\"secuType\":3},{\"code\":\"GSK\",\"market\":11,\"name\":\"葛兰素史克\","
        + "\"secuType\":3},{\"code\":\"603214\",\"market\":21,\"name\":\"爱婴室\",\"secuType\":3},{\"code\":\"CHAD\","
        + "\"market\":11,\"name\":\"沪深300做空(Direxion)\",\"secuType\":4},{\"code\":\"SOHU\",\"market\":11,"
        + "\"name\":\"搜狐\",\"secuType\":3},{\"code\":\"08083\",\"market\":1,\"name\":\"中国有赞\",\"secuType\":3},"
        + "{\"code\":\"LI\",\"market\":11,\"name\":\"理想汽车\",\"secuType\":3},{\"code\":\"512660\",\"market\":21,"
        + "\"name\":\"军工ETF\",\"secuType\":4},{\"code\":\"QTT\",\"market\":11,\"name\":\"趣头条\",\"secuType\":3},"
        + "{\"code\":\"600702\",\"market\":21,\"name\":\"舍得酒业\",\"secuType\":3},{\"code\":\"601319\",\"market\":21,"
        + "\"name\":\"中国人保\",\"secuType\":3},{\"code\":\"00981\",\"market\":1,\"name\":\"中芯国际\",\"secuType\":3},"
        + "{\"code\":\"300166\",\"market\":22,\"name\":\"东方国信\",\"secuType\":3},{\"code\":\"SQQQ\",\"market\":11,"
        + "\"name\":\"ProShares三倍做空纳斯达克指数ETF\",\"secuType\":4},{\"code\":\"601766\",\"market\":21,\"name\":\"中国中车\","
        + "\"secuType\":3},{\"code\":\"DDL\",\"market\":11,\"name\":\"叮咚买菜\",\"secuType\":3},{\"code\":\"000785\","
        + "\"market\":22,\"name\":\"居然之家\",\"secuType\":3},{\"code\":\"002714\",\"market\":22,\"name\":\"牧原股份\","
        + "\"secuType\":3},{\"code\":\"000338\",\"market\":22,\"name\":\"潍柴动力\",\"secuType\":3},{\"code\":\"601398\","
        + "\"market\":21,\"name\":\"工商银行\",\"secuType\":3},{\"code\":\"CEA\",\"market\":11,\"name\":\"东方航空\","
        + "\"secuType\":3},{\"code\":\"160611\",\"market\":22,\"name\":\"鹏华优质治理LOF\",\"secuType\":4},"
        + "{\"code\":\"300177\",\"market\":22,\"name\":\"中海达\",\"secuType\":3},{\"code\":\"06909\",\"market\":1,"
        + "\"name\":\"百得利控股\",\"secuType\":3},{\"code\":\"00883\",\"market\":1,\"name\":\"中国海洋石油\",\"secuType\":3},"
        + "{\"code\":\"BILI\",\"market\":11,\"name\":\"哔哩哔哩\",\"secuType\":3},{\"code\":\"000820\",\"market\":21,"
        + "\"name\":\"煤炭指数\",\"secuType\":6},{\"code\":\"600028\",\"market\":21,\"name\":\"中国石化\",\"secuType\":3},"
        + "{\"code\":\"600750\",\"market\":21,\"name\":\"江中药业\",\"secuType\":3},{\"code\":\"HTHT\",\"market\":11,"
        + "\"name\":\"华住\",\"secuType\":3},{\"code\":\".DJI\",\"market\":11,\"name\":\"道琼斯指数\",\"secuType\":6},"
        + "{\"code\":\"510650\",\"market\":21,\"name\":\"金融地产ETF\",\"secuType\":4},{\"code\":\"501018\","
        + "\"market\":21,\"name\":\"南方原油LOF\",\"secuType\":4},{\"code\":\"IQ\",\"market\":11,\"name\":\"爱奇艺\","
        + "\"secuType\":3},{\"code\":\"601328\",\"market\":21,\"name\":\"交通银行\",\"secuType\":3},{\"code\":\"002830\","
        + "\"market\":22,\"name\":\"名雕股份\",\"secuType\":3},{\"code\":\"000798\",\"market\":22,\"name\":\"中水渔业\","
        + "\"secuType\":3},{\"code\":\"AMZN\",\"market\":11,\"name\":\"亚马逊\",\"secuType\":3},{\"code\":\"BK2541\","
        + "\"market\":11,\"name\":\"SPAC上市公司\",\"secuType\":7},{\"code\":\"000829\",\"market\":22,\"name\":\"天音控股\","
        + "\"secuType\":3},{\"code\":\"159944\",\"market\":22,\"name\":\"材料ETF\",\"secuType\":4},{\"code\":\"00772\","
        + "\"market\":1,\"name\":\"阅文集团\",\"secuType\":3},{\"code\":\"NVDA\",\"market\":11,\"name\":\"英伟达\","
        + "\"secuType\":3},{\"code\":\"09626\",\"market\":1,\"name\":\"哔哩哔哩-SW\",\"secuType\":3},"
        + "{\"code\":\"159910\",\"market\":22,\"name\":\"基本面120ETF\",\"secuType\":4},{\"code\":\"06186\","
        + "\"market\":1,\"name\":\"中国飞鹤\",\"secuType\":3},{\"code\":\"000625\",\"market\":22,\"name\":\"长安汽车\","
        + "\"secuType\":3},{\"code\":\"00708\",\"market\":1,\"name\":\"恒大汽车\",\"secuType\":3},{\"code\":\"600609\","
        + "\"market\":21,\"name\":\"金杯汽车\",\"secuType\":3},{\"code\":\"600418\",\"market\":21,\"name\":\"江淮汽车\","
        + "\"secuType\":3},{\"code\":\"BK0360\",\"market\":21,\"name\":\"杭州湾大湾区\",\"secuType\":7},{\"code\":\"ZNH\","
        + "\"market\":11,\"name\":\"南方航空\",\"secuType\":3},{\"code\":\"603879\",\"market\":21,\"name\":\"永悦科技\","
        + "\"secuType\":3},{\"code\":\"600660\",\"market\":21,\"name\":\"福耀玻璃\",\"secuType\":3},{\"code\":\"02319\","
        + "\"market\":1,\"name\":\"蒙牛乳业\",\"secuType\":3},{\"code\":\"00305\",\"market\":1,\"name\":\"五菱汽车\","
        + "\"secuType\":3},{\"code\":\"588050\",\"market\":21,\"name\":\"科创ETF\",\"secuType\":4},"
        + "{\"code\":\"600050\",\"market\":21,\"name\":\"中国联通\",\"secuType\":3},{\"code\":\"000555\",\"market\":22,"
        + "\"name\":\"神州信息\",\"secuType\":3},{\"code\":\"300165\",\"market\":22,\"name\":\"天瑞仪器\",\"secuType\":3},"
        + "{\"code\":\"000830\",\"market\":22,\"name\":\"鲁西化工\",\"secuType\":3},{\"code\":\"600109\",\"market\":21,"
        + "\"name\":\"国金证券\",\"secuType\":3},{\"code\":\"601066\",\"market\":21,\"name\":\"中信建投\",\"secuType\":3},"
        + "{\"code\":\"06030\",\"market\":1,\"name\":\"中信证券\",\"secuType\":3},{\"code\":\"000725\",\"market\":22,"
        + "\"name\":\"京东方A\",\"secuType\":3},{\"code\":\"600313\",\"market\":21,\"name\":\"农发种业\",\"secuType\":3},"
        + "{\"code\":\"WB\",\"market\":11,\"name\":\"微博\",\"secuType\":3},{\"code\":\"300496\",\"market\":22,"
        + "\"name\":\"中科创达\",\"secuType\":3},{\"code\":\"002174\",\"market\":22,\"name\":\"游族网络\",\"secuType\":3},"
        + "{\"code\":\"02020\",\"market\":1,\"name\":\"安踏体育\",\"secuType\":3},{\"code\":\"160610\",\"market\":22,"
        + "\"name\":\"鹏华动力LOF\",\"secuType\":4},{\"code\":\"EWV\",\"market\":11,\"name\":\"ProShares两倍做空MSCI日本ETF\","
        + "\"secuType\":4},{\"code\":\"601688\",\"market\":21,\"name\":\"华泰证券\",\"secuType\":3},{\"code\":\"01114\","
        + "\"market\":1,\"name\":\"华晨中国\",\"secuType\":3},{\"code\":\"002118\",\"market\":22,\"name\":\"紫鑫药业\","
        + "\"secuType\":3},{\"code\":\"300269\",\"market\":22,\"name\":\"ST联建\",\"secuType\":3},{\"code\":\"09858\","
        + "\"market\":1,\"name\":\"优然牧业\",\"secuType\":3},{\"code\":\"SH\",\"market\":11,"
        + "\"name\":\"Proshares做空标普500\",\"secuType\":4},{\"code\":\"YMmain\",\"market\":11,\"name\":\"道琼斯指数主连(2206)"
        + "\",\"secuType\":10},{\"code\":\"LMT\",\"market\":11,\"name\":\"洛克希德马丁\",\"secuType\":3},"
        + "{\"code\":\"KWEB\",\"market\":11,\"name\":\"KraneShares中国海外互联网ETF\",\"secuType\":4},{\"code\":\"DRV\","
        + "\"market\":11,\"name\":\"Direxion每日房地产指数三倍做空ETF\",\"secuType\":4},{\"code\":\"300015\",\"market\":22,"
        + "\"name\":\"爱尔眼科\",\"secuType\":3},{\"code\":\"06185\",\"market\":1,\"name\":\"康希诺生物-B\",\"secuType\":3},"
        + "{\"code\":\"09922\",\"market\":1,\"name\":\"九毛九\",\"secuType\":3},{\"code\":\"159922\",\"market\":22,"
        + "\"name\":\"中证500ETF嘉实\",\"secuType\":4},{\"code\":\"000979\",\"market\":21,\"name\":\"大宗商品\","
        + "\"secuType\":6},{\"code\":\"EWK\",\"market\":11,\"name\":\"比利时ETF-iShares MSCI\",\"secuType\":4},"
        + "{\"code\":\"159911\",\"market\":22,\"name\":\"民营ETF\",\"secuType\":4},{\"code\":\"600690\",\"market\":21,"
        + "\"name\":\"海尔智家\",\"secuType\":3},{\"code\":\"300266\",\"market\":22,\"name\":\"兴源环境\",\"secuType\":3},"
        + "{\"code\":\"300048\",\"market\":22,\"name\":\"合康新能\",\"secuType\":3},{\"code\":\"06862\",\"market\":1,"
        + "\"name\":\"海底捞\",\"secuType\":3},{\"code\":\"600598\",\"market\":21,\"name\":\"北大荒\",\"secuType\":3},"
        + "{\"code\":\"02618\",\"market\":1,\"name\":\"京东物流\",\"secuType\":3},{\"code\":\"689009\",\"market\":21,"
        + "\"name\":\"九号公司-WD\",\"secuType\":3},{\"code\":\"NIO\",\"market\":11,\"name\":\"蔚来\",\"secuType\":3},"
        + "{\"code\":\"002310\",\"market\":22,\"name\":\"东方园林\",\"secuType\":3},{\"code\":\"600958\",\"market\":21,"
        + "\"name\":\"东方证券\",\"secuType\":3},{\"code\":\"WUBA\",\"market\":11,\"name\":\"58同城\",\"secuType\":3},"
        + "{\"code\":\"07552\",\"market\":1,\"name\":\"南方两倍做空恒生科技\",\"secuType\":4},{\"code\":\"LIT\",\"market\":11,"
        + "\"name\":\"锂电池ETF-Global X\",\"secuType\":4},{\"code\":\"GOOG\",\"market\":11,\"name\":\"谷歌-C\","
        + "\"secuType\":3},{\"code\":\"000300\",\"market\":21,\"name\":\"沪深300\",\"secuType\":6},{\"code\":\"00175\","
        + "\"market\":1,\"name\":\"吉利汽车\",\"secuType\":3},{\"code\":\"EWY\",\"market\":11,\"name\":\"韩国ETF-iShares "
        + "MSCI\",\"secuType\":4},{\"code\":\"VNM\",\"market\":11,\"name\":\"越南ETF-VanEck\",\"secuType\":4},"
        + "{\"code\":\"002969\",\"market\":22,\"name\":\"嘉美包装\",\"secuType\":3},{\"code\":\"159945\",\"market\":22,"
        + "\"name\":\"能源ETF基金\",\"secuType\":4},{\"code\":\"300059\",\"market\":22,\"name\":\"东方财富\",\"secuType\":3},"
        + "{\"code\":\"NDAQ\",\"market\":11,\"name\":\"纳斯达克\",\"secuType\":3},{\"code\":\"600019\",\"market\":21,"
        + "\"name\":\"宝钢股份\",\"secuType\":3},{\"code\":\"601939\",\"market\":21,\"name\":\"建设银行\",\"secuType\":3},"
        + "{\"code\":\"002190\",\"market\":22,\"name\":\"成飞集成\",\"secuType\":3},{\"code\":\"510270\",\"market\":21,"
        + "\"name\":\"国企ETF\",\"secuType\":4},{\"code\":\"01060\",\"market\":1,\"name\":\"阿里影业\",\"secuType\":3},"
        + "{\"code\":\"300104\",\"market\":22,\"name\":\"乐视退\",\"secuType\":3},{\"code\":\"600006\",\"market\":21,"
        + "\"name\":\"东风汽车\",\"secuType\":3},{\"code\":\"601865\",\"market\":21,\"name\":\"福莱特\",\"secuType\":3},"
        + "{\"code\":\"RENN\",\"market\":11,\"name\":\"人人网\",\"secuType\":3},{\"code\":\"300014\",\"market\":22,"
        + "\"name\":\"亿纬锂能\",\"secuType\":3},{\"code\":\"601888\",\"market\":21,\"name\":\"中国中免\",\"secuType\":3},"
        + "{\"code\":\"SPGI\",\"market\":11,\"name\":\"标普全球\",\"secuType\":3},{\"code\":\"01797\",\"market\":1,"
        + "\"name\":\"新东方在线\",\"secuType\":3},{\"code\":\"06993\",\"market\":1,\"name\":\"蓝月亮集团\",\"secuType\":3},"
        + "{\"code\":\"002762\",\"market\":22,\"name\":\"金发拉比\",\"secuType\":3},{\"code\":\"03319\",\"market\":1,"
        + "\"name\":\"雅生活服务\",\"secuType\":3},{\"code\":\"399975\",\"market\":22,\"name\":\"证券公司\",\"secuType\":6},"
        + "{\"code\":\"03690\",\"market\":1,\"name\":\"美团-W\",\"secuType\":3},{\"code\":\"VIPS\",\"market\":11,"
        + "\"name\":\"唯品会\",\"secuType\":3},{\"code\":\"02333\",\"market\":1,\"name\":\"长城汽车\",\"secuType\":3},"
        + "{\"code\":\"AMD\",\"market\":11,\"name\":\"美国超微公司\",\"secuType\":3},{\"code\":\"510200\",\"market\":21,"
        + "\"name\":\"上证券商ETF\",\"secuType\":4},{\"code\":\"600031\",\"market\":21,\"name\":\"三一重工\",\"secuType\":3},"
        + "{\"code\":\"002145\",\"market\":22,\"name\":\"中核钛白\",\"secuType\":3},{\"code\":\"512400\",\"market\":21,"
        + "\"name\":\"有色金属ETF\",\"secuType\":4},{\"code\":\"PLTR\",\"market\":11,\"name\":\"Palantir Technologies\","
        + "\"secuType\":3},{\"code\":\"TCOM\",\"market\":11,\"name\":\"携程网\",\"secuType\":3},{\"code\":\"JT\","
        + "\"market\":11,\"name\":\"简普科技\",\"secuType\":3},{\"code\":\"UGL\",\"market\":11,"
        + "\"name\":\"ProShares两倍做多黄金ETF\",\"secuType\":4},{\"code\":\"601658\",\"market\":21,\"name\":\"邮储银行\","
        + "\"secuType\":3},{\"code\":\"300012\",\"market\":22,\"name\":\"华测检测\",\"secuType\":3},{\"code\":\"09618\","
        + "\"market\":1,\"name\":\"京东集团-SW\",\"secuType\":3},{\"code\":\"601899\",\"market\":21,\"name\":\"紫金矿业\","
        + "\"secuType\":3},{\"code\":\"BEKE\",\"market\":11,\"name\":\"贝壳\",\"secuType\":3},{\"code\":\"LFC\","
        + "\"market\":11,\"name\":\"中国人寿\",\"secuType\":3},{\"code\":\"003038\",\"market\":22,\"name\":\"鑫铂股份\","
        + "\"secuType\":3},{\"code\":\"601111\",\"market\":21,\"name\":\"中国国航\",\"secuType\":3},{\"code\":\"159887\","
        + "\"market\":22,\"name\":\"银行ETF\",\"secuType\":4},{\"code\":\"000156\",\"market\":22,\"name\":\"华数传媒\","
        + "\"secuType\":3},{\"code\":\"UBER\",\"market\":11,\"name\":\"优步\",\"secuType\":3},{\"code\":\"600518\","
        + "\"market\":21,\"name\":\"*ST康美\",\"secuType\":3},{\"code\":\"002899\",\"market\":22,\"name\":\"英派斯\","
        + "\"secuType\":3},{\"code\":\"002352\",\"market\":22,\"name\":\"顺丰控股\",\"secuType\":3},{\"code\":\"ADDYY\","
        + "\"market\":11,\"name\":\"阿迪达斯(ADR)\",\"secuType\":3},{\"code\":\"300651\",\"market\":22,\"name\":\"金陵体育\","
        + "\"secuType\":3},{\"code\":\"300267\",\"market\":22,\"name\":\"尔康制药\",\"secuType\":3},{\"code\":\"603596\","
        + "\"market\":21,\"name\":\"伯特利\",\"secuType\":3},{\"code\":\"600507\",\"market\":21,\"name\":\"方大特钢\","
        + "\"secuType\":3},{\"code\":\"600576\",\"market\":21,\"name\":\"祥源文化\",\"secuType\":3},{\"code\":\"002320\","
        + "\"market\":22,\"name\":\"海峡股份\",\"secuType\":3},{\"code\":\"BK2123\",\"market\":11,\"name\":\"高瓴资本持仓\","
        + "\"secuType\":7},{\"code\":\"600905\",\"market\":21,\"name\":\"三峡能源\",\"secuType\":3},{\"code\":\"00670\","
        + "\"market\":1,\"name\":\"中国东方航空股份\",\"secuType\":3},{\"code\":\"603833\",\"market\":21,\"name\":\"欧派家居\","
        + "\"secuType\":3},{\"code\":\"SBRCY\",\"market\":11,\"name\":\"俄罗斯联邦储蓄银行(ADR)\",\"secuType\":3},"
        + "{\"code\":\"06699\",\"market\":1,\"name\":\"时代天使\",\"secuType\":3},{\"code\":\"03888\",\"market\":1,"
        + "\"name\":\"金山软件\",\"secuType\":3},{\"code\":\"06808\",\"market\":1,\"name\":\"高鑫零售\",\"secuType\":3},"
        + "{\"code\":\"601018\",\"market\":21,\"name\":\"宁波港\",\"secuType\":3},{\"code\":\"603843\",\"market\":21,"
        + "\"name\":\"正平股份\",\"secuType\":3},{\"code\":\"SFUN\",\"market\":11,\"name\":\"房天下\",\"secuType\":3},"
        + "{\"code\":\"CSCO\",\"market\":11,\"name\":\"思科\",\"secuType\":3},{\"code\":\"LUKOY\",\"market\":11,"
        + "\"name\":\"卢克石油公司\",\"secuType\":3},{\"code\":\"002887\",\"market\":22,\"name\":\"绿茵生态\",\"secuType\":3},"
        + "{\"code\":\"PEJ\",\"market\":11,\"name\":\"Powershares动态休闲娱乐投资组\",\"secuType\":4},{\"code\":\"002027\","
        + "\"market\":22,\"name\":\"分众传媒\",\"secuType\":3},{\"code\":\"601377\",\"market\":21,\"name\":\"兴业证券\","
        + "\"secuType\":3},{\"code\":\"600763\",\"market\":21,\"name\":\"通策医疗\",\"secuType\":3},{\"code\":\"600332\","
        + "\"market\":21,\"name\":\"白云山\",\"secuType\":3},{\"code\":\"TME\",\"market\":11,\"name\":\"腾讯音乐\","
        + "\"secuType\":3},{\"code\":\"600030\",\"market\":21,\"name\":\"中信证券\",\"secuType\":3},{\"code\":\"512200\","
        + "\"market\":21,\"name\":\"房地产ETF\",\"secuType\":4},{\"code\":\"600029\",\"market\":21,\"name\":\"南方航空\","
        + "\"secuType\":3},{\"code\":\"603899\",\"market\":21,\"name\":\"晨光股份\",\"secuType\":3},{\"code\":\"399001\","
        + "\"market\":22,\"name\":\"深证成指\",\"secuType\":6},{\"code\":\".IXIC\",\"market\":11,\"name\":\"纳斯达克综合指数\","
        + "\"secuType\":6},{\"code\":\"600587\",\"market\":21,\"name\":\"新华医疗\",\"secuType\":3},{\"code\":\"00241\","
        + "\"market\":1,\"name\":\"阿里健康\",\"secuType\":3},{\"code\":\"TEDU\",\"market\":11,\"name\":\"达内科技\","
        + "\"secuType\":3},{\"code\":\"CNmain\",\"market\":31,\"name\":\"A50指数主连(2204)\",\"secuType\":10},"
        + "{\"code\":\"600018\",\"market\":21,\"name\":\"上港集团\",\"secuType\":3},{\"code\":\"688368\",\"market\":21,"
        + "\"name\":\"晶丰明源\",\"secuType\":3},{\"code\":\"300162\",\"market\":22,\"name\":\"雷曼光电\",\"secuType\":3},"
        + "{\"code\":\"688032\",\"market\":21,\"name\":\"禾迈股份\",\"secuType\":3},{\"code\":\"06060\",\"market\":1,"
        + "\"name\":\"众安在线\",\"secuType\":3},{\"code\":\"GREK\",\"market\":11,\"name\":\"Global X MSCI希腊ETF\","
        + "\"secuType\":4},{\"code\":\"XLK\",\"market\":11,\"name\":\"高科技指数ETF-SPDR\",\"secuType\":4},"
        + "{\"code\":\"300756\",\"market\":22,\"name\":\"金马游乐\",\"secuType\":3},{\"code\":\"000877\",\"market\":22,"
        + "\"name\":\"天山股份\",\"secuType\":3},{\"code\":\"BA\",\"market\":11,\"name\":\"波音\",\"secuType\":3},"
        + "{\"code\":\"BITA\",\"market\":11,\"name\":\"易车\",\"secuType\":3},{\"code\":\"FF\",\"market\":11,"
        + "\"name\":\"FutureFuel\",\"secuType\":3},{\"code\":\"02331\",\"market\":1,\"name\":\"李宁\",\"secuType\":3},"
        + "{\"code\":\"600519\",\"market\":21,\"name\":\"贵州茅台\",\"secuType\":3},{\"code\":\"603320\",\"market\":21,"
        + "\"name\":\"迪贝电气\",\"secuType\":3},{\"code\":\"159865\",\"market\":22,\"name\":\"养殖ETF\",\"secuType\":4},"
        + "{\"code\":\"603993\",\"market\":21,\"name\":\"洛阳钼业\",\"secuType\":3},{\"code\":\"FUTU\",\"market\":11,"
        + "\"name\":\"富途控股\",\"secuType\":3},{\"code\":\"MOMO\",\"market\":11,\"name\":\"挚文集团\",\"secuType\":3},"
        + "{\"code\":\"515790\",\"market\":21,\"name\":\"光伏ETF\",\"secuType\":4},{\"code\":\"BTCmain\",\"market\":11,"
        + "\"name\":\"比特币参考汇率主连(2204)\",\"secuType\":10},{\"code\":\"FFIE\",\"market\":11,\"name\":\"Faraday Future "
        + "Intelligent Electric Inc.\",\"secuType\":3},{\"code\":\"PG\",\"market\":11,\"name\":\"宝洁\","
        + "\"secuType\":3},{\"code\":\"03087\",\"market\":1,\"name\":\"XTR富时越南\",\"secuType\":4},"
        + "{\"code\":\"601989\",\"market\":21,\"name\":\"中国重工\",\"secuType\":3},{\"code\":\"RUSL\",\"market\":11,"
        + "\"name\":\"俄罗斯2X做多-Direxion\",\"secuType\":4},{\"code\":\"512690\",\"market\":21,\"name\":\"酒ETF\","
        + "\"secuType\":4},{\"code\":\"02238\",\"market\":1,\"name\":\"广汽集团\",\"secuType\":3},{\"code\":\"RIVN\","
        + "\"market\":11,\"name\":\"Rivian Automotive\",\"secuType\":3},{\"code\":\"510050\",\"market\":21,"
        + "\"name\":\"上证50ETF\",\"secuType\":4},{\"code\":\"CLF\",\"market\":11,\"name\":\"克里夫天然资源\",\"secuType\":3},"
        + "{\"code\":\"000538\",\"market\":22,\"name\":\"云南白药\",\"secuType\":3},{\"code\":\"603392\",\"market\":21,"
        + "\"name\":\"万泰生物\",\"secuType\":3},{\"code\":\"03606\",\"market\":1,\"name\":\"福耀玻璃\",\"secuType\":3},"
        + "{\"code\":\"HON\",\"market\":11,\"name\":\"霍尼韦尔\",\"secuType\":3},{\"code\":\"MDB\",\"market\":11,"
        + "\"name\":\"MongoDB\",\"secuType\":3},{\"code\":\"YRD\",\"market\":11,\"name\":\"宜人金科\",\"secuType\":3},"
        + "{\"code\":\"002594\",\"market\":22,\"name\":\"比亚迪\",\"secuType\":3},{\"code\":\"301073\",\"market\":22,"
        + "\"name\":\"君亭酒店\",\"secuType\":3},{\"code\":\"159857\",\"market\":22,\"name\":\"光伏ETF\",\"secuType\":4},"
        + "{\"code\":\"300137\",\"market\":22,\"name\":\"先河环保\",\"secuType\":3},{\"code\":\"603517\",\"market\":21,"
        + "\"name\":\"绝味食品\",\"secuType\":3},{\"code\":\"000688\",\"market\":21,\"name\":\"科创50\",\"secuType\":6},"
        + "{\"code\":\"BK0023\",\"market\":21,\"name\":\"航空运输(停用)\",\"secuType\":7},{\"code\":\"TWTR\",\"market\":11,"
        + "\"name\":\"Twitter\",\"secuType\":3},{\"code\":\"600009\",\"market\":21,\"name\":\"上海机场\",\"secuType\":3},"
        + "{\"code\":\"600926\",\"market\":21,\"name\":\"杭州银行\",\"secuType\":3},{\"code\":\"00460\",\"market\":1,"
        + "\"name\":\"四环医药\",\"secuType\":3},{\"code\":\"002948\",\"market\":22,\"name\":\"青岛银行\",\"secuType\":3},"
        + "{\"code\":\"02359\",\"market\":1,\"name\":\"药明康德\",\"secuType\":3},{\"code\":\"603333\",\"market\":21,"
        + "\"name\":\"尚纬股份\",\"secuType\":3},{\"code\":\"399005\",\"market\":22,\"name\":\"中小100\",\"secuType\":6},"
        + "{\"code\":\"02171\",\"market\":1,\"name\":\"科济药业-B\",\"secuType\":3},{\"code\":\"300033\",\"market\":22,"
        + "\"name\":\"同花顺\",\"secuType\":3},{\"code\":\"601633\",\"market\":21,\"name\":\"长城汽车\",\"secuType\":3},"
        + "{\"code\":\"00700\",\"market\":1,\"name\":\"腾讯控股\",\"secuType\":3},{\"code\":\"000333\",\"market\":22,"
        + "\"name\":\"美的集团\",\"secuType\":3},{\"code\":\"516800\",\"market\":21,\"name\":\"智能制造ETF\",\"secuType\":4},"
        + "{\"code\":\"688608\",\"market\":21,\"name\":\"恒玄科技\",\"secuType\":3},{\"code\":\"TOUR\",\"market\":11,"
        + "\"name\":\"途牛\",\"secuType\":3},{\"code\":\"002742\",\"market\":22,\"name\":\"三圣股份\",\"secuType\":3},"
        + "{\"code\":\"DNK\",\"market\":11,\"name\":\"蛋壳公寓\",\"secuType\":3},{\"code\":\"000607\",\"market\":22,"
        + "\"name\":\"华媒控股\",\"secuType\":3},{\"code\":\"000768\",\"market\":22,\"name\":\"中航西飞\",\"secuType\":3},"
        + "{\"code\":\"603598\",\"market\":21,\"name\":\"引力传媒\",\"secuType\":3},{\"code\":\"399050\",\"market\":22,"
        + "\"name\":\"创新引擎\",\"secuType\":6},{\"code\":\"601336\",\"market\":21,\"name\":\"新华保险\",\"secuType\":3},"
        + "{\"code\":\"GCmain\",\"market\":11,\"name\":\"黄金主连(2206)\",\"secuType\":10},{\"code\":\"516950\","
        + "\"market\":21,\"name\":\"基建ETF\",\"secuType\":4},{\"code\":\"601919\",\"market\":21,\"name\":\"中远海控\","
        + "\"secuType\":3},{\"code\":\"603013\",\"market\":21,\"name\":\"亚普股份\",\"secuType\":3},{\"code\":\"600744\","
        + "\"market\":21,\"name\":\"华银电力\",\"secuType\":3},{\"code\":\"FDX\",\"market\":11,\"name\":\"联邦快递\","
        + "\"secuType\":3},{\"code\":\"300124\",\"market\":22,\"name\":\"汇川技术\",\"secuType\":3},{\"code\":\"600903\","
        + "\"market\":21,\"name\":\"贵州燃气\",\"secuType\":3},{\"code\":\"LKNCY\",\"market\":11,\"name\":\"瑞幸咖啡\","
        + "\"secuType\":3},{\"code\":\"000927\",\"market\":22,\"name\":\"中国铁物\",\"secuType\":3},{\"code\":\"ZTCOY\","
        + "\"market\":11,\"name\":\"中兴通讯(ADR)\",\"secuType\":3},{\"code\":\"159926\",\"market\":22,"
        + "\"name\":\"国债ETF\",\"secuType\":4},{\"code\":\"U\",\"market\":11,\"name\":\"Unity Software\","
        + "\"secuType\":3},{\"code\":\"002570\",\"market\":22,\"name\":\"贝因美\",\"secuType\":3},{\"code\":\"000505\","
        + "\"market\":22,\"name\":\"京粮控股\",\"secuType\":3},{\"code\":\"300115\",\"market\":22,\"name\":\"长盈精密\","
        + "\"secuType\":3},{\"code\":\"300677\",\"market\":22,\"name\":\"英科医疗\",\"secuType\":3},{\"code\":\"00388\","
        + "\"market\":1,\"name\":\"香港交易所\",\"secuType\":3},{\"code\":\"300792\",\"market\":22,\"name\":\"壹网壹创\","
        + "\"secuType\":3},{\"code\":\"AMC\",\"market\":11,\"name\":\"AMC院线\",\"secuType\":3},{\"code\":\"150205\","
        + "\"market\":22,\"name\":\"国防A\",\"secuType\":4},{\"code\":\"600604\",\"market\":21,\"name\":\"市北高新\","
        + "\"secuType\":3},{\"code\":\"300318\",\"market\":22,\"name\":\"博晖创新\",\"secuType\":3},{\"code\":\"TRI\","
        + "\"market\":11,\"name\":\"汤森路透\",\"secuType\":3},{\"code\":\"588000\",\"market\":21,\"name\":\"科创50ETF\","
        + "\"secuType\":4},{\"code\":\"01658\",\"market\":1,\"name\":\"邮储银行\",\"secuType\":3},{\"code\":\"01929\","
        + "\"market\":1,\"name\":\"周大福\",\"secuType\":3},{\"code\":\"YY\",\"market\":11,\"name\":\"欢聚\","
        + "\"secuType\":3},{\"code\":\"002340\",\"market\":22,\"name\":\"格林美\",\"secuType\":3},{\"code\":\"KO\","
        + "\"market\":11,\"name\":\"可口可乐\",\"secuType\":3},{\"code\":\"000858\",\"market\":22,\"name\":\"五粮液\","
        + "\"secuType\":3},{\"code\":\"ATHM\",\"market\":11,\"name\":\"汽车之家\",\"secuType\":3},{\"code\":\"PYPL\","
        + "\"market\":11,\"name\":\"PayPal\",\"secuType\":3},{\"code\":\"159867\",\"market\":22,\"name\":\"畜牧ETF\","
        + "\"secuType\":4},{\"code\":\"600276\",\"market\":21,\"name\":\"恒瑞医药\",\"secuType\":3},{\"code\":\"01055\","
        + "\"market\":1,\"name\":\"中国南方航空股份\",\"secuType\":3},{\"code\":\"XNET\",\"market\":11,\"name\":\"迅雷\","
        + "\"secuType\":3},{\"code\":\"INDY\",\"market\":11,\"name\":\"iShares安硕印度50 ETF\",\"secuType\":4},"
        + "{\"code\":\"300781\",\"market\":22,\"name\":\"因赛集团\",\"secuType\":3},{\"code\":\"RIO\",\"market\":11,"
        + "\"name\":\"力拓\",\"secuType\":3},{\"code\":\"600776\",\"market\":21,\"name\":\"东方通信\",\"secuType\":3},"
        + "{\"code\":\"MMM\",\"market\":11,\"name\":\"3M\",\"secuType\":3},{\"code\":\"300159\",\"market\":22,"
        + "\"name\":\"新研股份\",\"secuType\":3},{\"code\":\"300136\",\"market\":22,\"name\":\"信维通信\",\"secuType\":3},"
        + "{\"code\":\"600516\",\"market\":21,\"name\":\"方大炭素\",\"secuType\":3},{\"code\":\"MF\",\"market\":11,"
        + "\"name\":\"每日优鲜\",\"secuType\":3},{\"code\":\"600115\",\"market\":21,\"name\":\"中国东航\",\"secuType\":3},"
        + "{\"code\":\"300742\",\"market\":22,\"name\":\"越博动力\",\"secuType\":3},{\"code\":\"AAPL\",\"market\":11,"
        + "\"name\":\"苹果\",\"secuType\":3},{\"code\":\"EDU\",\"market\":11,\"name\":\"新东方\",\"secuType\":3},"
        + "{\"code\":\"601108\",\"market\":21,\"name\":\"财通证券\",\"secuType\":3},{\"code\":\"RYB\",\"market\":11,"
        + "\"name\":\"红黄蓝教育\",\"secuType\":3},{\"code\":\"600160\",\"market\":21,\"name\":\"巨化股份\",\"secuType\":3},"
        + "{\"code\":\"00710\",\"market\":1,\"name\":\"京东方精电\",\"secuType\":3},{\"code\":\"900902\",\"market\":21,"
        + "\"name\":\"市北B股\",\"secuType\":3},{\"code\":\"600500\",\"market\":21,\"name\":\"中化国际\",\"secuType\":3},"
        + "{\"code\":\"688012\",\"market\":21,\"name\":\"中微公司\",\"secuType\":3},{\"code\":\"159995\",\"market\":22,"
        + "\"name\":\"芯片ETF\",\"secuType\":4},{\"code\":\"QCOM\",\"market\":11,\"name\":\"高通\",\"secuType\":3},"
        + "{\"code\":\"YINN\",\"market\":11,\"name\":\"Direxion每日三倍做多FTSE中国ETF\",\"secuType\":4},"
        + "{\"code\":\"002124\",\"market\":22,\"name\":\"天邦股份\",\"secuType\":3},{\"code\":\"600137\",\"market\":21,"
        + "\"name\":\"浪莎股份\",\"secuType\":3},{\"code\":\"688139\",\"market\":21,\"name\":\"海尔生物\",\"secuType\":3},"
        + "{\"code\":\"NFLX\",\"market\":11,\"name\":\"奈飞\",\"secuType\":3},{\"code\":\"NKE\",\"market\":11,"
        + "\"name\":\"耐克\",\"secuType\":3},{\"code\":\"000048\",\"market\":21,\"name\":\"责任指数\",\"secuType\":6},"
        + "{\"code\":\"600066\",\"market\":21,\"name\":\"宇通客车\",\"secuType\":3},{\"code\":\"002741\",\"market\":22,"
        + "\"name\":\"光华科技\",\"secuType\":3},{\"code\":\"000014\",\"market\":22,\"name\":\"沙河股份\",\"secuType\":3},"
        + "{\"code\":\"002304\",\"market\":22,\"name\":\"洋河股份\",\"secuType\":3},{\"code\":\"000931\",\"market\":22,"
        + "\"name\":\"中关村\",\"secuType\":3},{\"code\":\"MS\",\"market\":11,\"name\":\"摩根士丹利\",\"secuType\":3},"
        + "{\"code\":\"159825\",\"market\":22,\"name\":\"农业ETF\",\"secuType\":4},{\"code\":\"YANG\",\"market\":11,"
        + "\"name\":\"Direxion每日富时中国三倍做空ETF\",\"secuType\":4},{\"code\":\"09888\",\"market\":1,\"name\":\"百度集团-SW\","
        + "\"secuType\":3},{\"code\":\"603348\",\"market\":21,\"name\":\"文灿股份\",\"secuType\":3},{\"code\":\"BK1996\","
        + "\"market\":1,\"name\":\"猪肉概念\",\"secuType\":7},{\"code\":\"01810\",\"market\":1,\"name\":\"小米集团-W\","
        + "\"secuType\":3},{\"code\":\"06158\",\"market\":1,\"name\":\"正荣地产\",\"secuType\":3},{\"code\":\"ORCL\","
        + "\"market\":11,\"name\":\"甲骨文\",\"secuType\":3},{\"code\":\"688526\",\"market\":21,\"name\":\"科前生物\","
        + "\"secuType\":3},{\"code\":\"600036\",\"market\":21,\"name\":\"招商银行\",\"secuType\":3},{\"code\":\"513050\","
        + "\"market\":21,\"name\":\"中概互联网ETF\",\"secuType\":4},{\"code\":\"601012\",\"market\":21,\"name\":\"隆基股份\","
        + "\"secuType\":3},{\"code\":\"XPEV\",\"market\":11,\"name\":\"小鹏汽车\",\"secuType\":3},{\"code\":\"GLD\","
        + "\"market\":11,\"name\":\"SPDR黄金ETF\",\"secuType\":4},{\"code\":\"600581\",\"market\":21,\"name\":\"八一钢铁\","
        + "\"secuType\":3},{\"code\":\"600793\",\"market\":21,\"name\":\"宜宾纸业\",\"secuType\":3},{\"code\":\"BK0925\","
        + "\"market\":21,\"name\":\"商用车\",\"secuType\":7},{\"code\":\"09899\",\"market\":1,\"name\":\"云音乐\","
        + "\"secuType\":3},{\"code\":\"000988\",\"market\":22,\"name\":\"华工科技\",\"secuType\":3},{\"code\":\"000673\","
        + "\"market\":22,\"name\":\"*ST当代\",\"secuType\":3},{\"code\":\"BRK.B\",\"market\":11,\"name\":\"伯克希尔-B\","
        + "\"secuType\":3},{\"code\":\"BK0060\",\"market\":21,\"name\":\"酒店(停用)\",\"secuType\":7},"
        + "{\"code\":\"512010\",\"market\":21,\"name\":\"医药ETF\",\"secuType\":4},{\"code\":\"000776\",\"market\":22,"
        + "\"name\":\"广发证券\",\"secuType\":3},{\"code\":\"BK0943\",\"market\":21,\"name\":\"旅游零售Ⅱ\",\"secuType\":7},"
        + "{\"code\":\"600305\",\"market\":21,\"name\":\"恒顺醋业\",\"secuType\":3},{\"code\":\"159928\",\"market\":22,"
        + "\"name\":\"消费ETF\",\"secuType\":4},{\"code\":\"600999\",\"market\":21,\"name\":\"招商证券\",\"secuType\":3},"
        + "{\"code\":\"601288\",\"market\":21,\"name\":\"农业银行\",\"secuType\":3},{\"code\":\"600837\",\"market\":21,"
        + "\"name\":\"海通证券\",\"secuType\":3},{\"code\":\"300364\",\"market\":22,\"name\":\"中文在线\",\"secuType\":3},"
        + "{\"code\":\"QD\",\"market\":11,\"name\":\"趣店\",\"secuType\":3},{\"code\":\"200725\",\"market\":22,"
        + "\"name\":\"京东方B\",\"secuType\":3},{\"code\":\"002511\",\"market\":22,\"name\":\"中顺洁柔\",\"secuType\":3},"
        + "{\"code\":\"600138\",\"market\":21,\"name\":\"中青旅\",\"secuType\":3},{\"code\":\"002368\",\"market\":22,"
        + "\"name\":\"太极股份\",\"secuType\":3},{\"code\":\"BABA\",\"market\":11,\"name\":\"阿里巴巴\",\"secuType\":3},"
        + "{\"code\":\"002264\",\"market\":22,\"name\":\"新华都\",\"secuType\":3},{\"code\":\"002241\",\"market\":22,"
        + "\"name\":\"歌尔股份\",\"secuType\":3},{\"code\":\"DIDI\",\"market\":11,\"name\":\"滴滴\",\"secuType\":3},"
        + "{\"code\":\"600048\",\"market\":21,\"name\":\"保利发展\",\"secuType\":3},{\"code\":\"X\",\"market\":11,"
        + "\"name\":\"美国钢铁\",\"secuType\":3},{\"code\":\"000735\",\"market\":22,\"name\":\"罗牛山\",\"secuType\":3},"
        + "{\"code\":\"605099\",\"market\":21,\"name\":\"共创草坪\",\"secuType\":3},{\"code\":\"TSLA\",\"market\":11,"
        + "\"name\":\"特斯拉\",\"secuType\":3},{\"code\":\"300135\",\"market\":22,\"name\":\"宝利国际\",\"secuType\":3},"
        + "{\"code\":\"BIDU\",\"market\":11,\"name\":\"百度\",\"secuType\":3},{\"code\":\"JD\",\"market\":11,"
        + "\"name\":\"京东\",\"secuType\":3},{\"code\":\"600150\",\"market\":21,\"name\":\"中国船舶\",\"secuType\":3},"
        + "{\"code\":\"FB\",\"market\":11,\"name\":\"Meta Platforms\",\"secuType\":3},{\"code\":\"159001\","
        + "\"market\":22,\"name\":\"货币ETF\",\"secuType\":4},{\"code\":\"688016\",\"market\":21,\"name\":\"心脉医疗\","
        + "\"secuType\":3},{\"code\":\"BRK.A\",\"market\":11,\"name\":\"伯克希尔-A\",\"secuType\":3},"
        + "{\"code\":\"000002\",\"market\":22,\"name\":\"万科A\",\"secuType\":3},{\"code\":\"09633\",\"market\":1,"
        + "\"name\":\"农夫山泉\",\"secuType\":3},{\"code\":\"BK1304\",\"market\":1,\"name\":\"回港中概股\",\"secuType\":7},"
        + "{\"code\":\"601138\",\"market\":21,\"name\":\"工业富联\",\"secuType\":3},{\"code\":\"JPM\",\"market\":11,"
        + "\"name\":\"摩根大通\",\"secuType\":3},{\"code\":\"510500\",\"market\":21,\"name\":\"中证500ETF\","
        + "\"secuType\":4},{\"code\":\"02318\",\"market\":1,\"name\":\"中国平安\",\"secuType\":3},{\"code\":\"688005\","
        + "\"market\":21,\"name\":\"容百科技\",\"secuType\":3},{\"code\":\"06618\",\"market\":1,\"name\":\"京东健康\","
        + "\"secuType\":3},{\"code\":\"603501\",\"market\":21,\"name\":\"韦尔股份\",\"secuType\":3},{\"code\":\"600499\","
        + "\"market\":21,\"name\":\"科达制造\",\"secuType\":3},{\"code\":\"02779\",\"market\":1,\"name\":\"中国新华教育\","
        + "\"secuType\":3},{\"code\":\"601211\",\"market\":21,\"name\":\"国泰君安\",\"secuType\":3},{\"code\":\"600023\","
        + "\"market\":21,\"name\":\"浙能电力\",\"secuType\":3},{\"code\":\"601933\",\"market\":21,\"name\":\"永辉超市\","
        + "\"secuType\":3},{\"code\":\"600745\",\"market\":21,\"name\":\"闻泰科技\",\"secuType\":3},{\"code\":\"159930\","
        + "\"market\":22,\"name\":\"能源ETF\",\"secuType\":4},{\"code\":\"RUSS\",\"market\":11,\"name\":\"三倍做空俄罗斯ETF\","
        + "\"secuType\":4},{\"code\":\"603816\",\"market\":21,\"name\":\"顾家家居\",\"secuType\":3},{\"code\":\"002606\","
        + "\"market\":22,\"name\":\"大连电瓷\",\"secuType\":3},{\"code\":\"512170\",\"market\":21,\"name\":\"医疗ETF\","
        + "\"secuType\":4},{\"code\":\"BK2455\",\"market\":11,\"name\":\"印度概念\",\"secuType\":7},{\"code\":\"000001\","
        + "\"market\":21,\"name\":\"上证指数\",\"secuType\":6},{\"code\":\"000001\",\"market\":22,\"name\":\"平安银行\","
        + "\"secuType\":3},{\"code\":\"600547\",\"market\":21,\"name\":\"山东黄金\",\"secuType\":3},{\"code\":\"BK0434\","
        + "\"market\":21,\"name\":\"玻璃概念\",\"secuType\":7},{\"code\":\"PDD\",\"market\":11,\"name\":\"拼多多\","
        + "\"secuType\":3},{\"code\":\"CLmain\",\"market\":11,\"name\":\"WTI原油主连(2205)\",\"secuType\":10},"
        + "{\"code\":\"800000\",\"market\":1,\"name\":\"恒生指数\",\"secuType\":6},{\"code\":\"601116\",\"market\":21,"
        + "\"name\":\"三江购物\",\"secuType\":3},{\"code\":\"603128\",\"market\":21,\"name\":\"华贸物流\",\"secuType\":3},"
        + "{\"code\":\"002617\",\"market\":22,\"name\":\"露笑科技\",\"secuType\":3},{\"code\":\"002208\",\"market\":22,"
        + "\"name\":\"合肥城建\",\"secuType\":3},{\"code\":\"605299\",\"market\":21,\"name\":\"舒华体育\",\"secuType\":3},"
        + "{\"code\":\"GTLB\",\"market\":11,\"name\":\"Gitlab\",\"secuType\":3},{\"code\":\"GOTU\",\"market\":11,"
        + "\"name\":\"高途\",\"secuType\":3},{\"code\":\"600685\",\"market\":21,\"name\":\"中船防务\",\"secuType\":3},"
        + "{\"code\":\"09988\",\"market\":1,\"name\":\"阿里巴巴-SW\",\"secuType\":3},{\"code\":\"00763\",\"market\":1,"
        + "\"name\":\"中兴通讯\",\"secuType\":3},{\"code\":\"02150\",\"market\":1,\"name\":\"奈雪的茶\",\"secuType\":3},"
        + "{\"code\":\"XLF\",\"market\":11,\"name\":\"SPDR金融行业ETF\",\"secuType\":4},{\"code\":\"01398\",\"market\":1,"
        + "\"name\":\"工商银行\",\"secuType\":3},{\"code\":\"688235\",\"market\":21,\"name\":\"百济神州-U\",\"secuType\":3},"
        + "{\"code\":\"600686\",\"market\":21,\"name\":\"金龙汽车\",\"secuType\":3},{\"code\":\"002194\",\"market\":22,"
        + "\"name\":\"武汉凡谷\",\"secuType\":3},{\"code\":\"GME\",\"market\":11,\"name\":\"游戏驿站\",\"secuType\":3},"
        + "{\"code\":\"002302\",\"market\":22,\"name\":\"西部建设\",\"secuType\":3},{\"code\":\"MAR\",\"market\":11,"
        + "\"name\":\"万豪酒店\",\"secuType\":3},{\"code\":\"01024\",\"market\":1,\"name\":\"快手-W\",\"secuType\":3},"
        + "{\"code\":\"000651\",\"market\":22,\"name\":\"格力电器\",\"secuType\":3},{\"code\":\"300123\",\"market\":22,"
        + "\"name\":\"亚光科技\",\"secuType\":3},{\"code\":\"601360\",\"market\":21,\"name\":\"三六零\",\"secuType\":3},"
        + "{\"code\":\"159871\",\"market\":22,\"name\":\"有色金属ETF\",\"secuType\":4},{\"code\":\"510170\","
        + "\"market\":21,\"name\":\"大宗商品ETF\",\"secuType\":4},{\"code\":\"002078\",\"market\":22,\"name\":\"太阳纸业\","
        + "\"secuType\":3},{\"code\":\"01179\",\"market\":1,\"name\":\"华住集团-S\",\"secuType\":3},{\"code\":\"399006\","
        + "\"market\":22,\"name\":\"创业板指\",\"secuType\":6},{\"code\":\"603195\",\"market\":21,\"name\":\"公牛集团\","
        + "\"secuType\":3},{\"code\":\"601988\",\"market\":21,\"name\":\"中国银行\",\"secuType\":3},{\"code\":\"603277\","
        + "\"market\":21,\"name\":\"银都股份\",\"secuType\":3},{\"code\":\"600372\",\"market\":21,\"name\":\"中航电子\","
        + "\"secuType\":3},{\"code\":\"NQmain\",\"market\":11,\"name\":\"纳斯达克100指数主连(2206)\",\"secuType\":10},"
        + "{\"code\":\"603288\",\"market\":21,\"name\":\"海天味业\",\"secuType\":3},{\"code\":\"002512\",\"market\":22,"
        + "\"name\":\"达华智能\",\"secuType\":3},{\"code\":\"ESmain\",\"market\":11,\"name\":\"标普500指数主连(2206)\","
        + "\"secuType\":10}]";

    public void queryUserSecurityByGroup() throws Exception {

        List<String> groups = queryUserGroup();
        System.out.println("groups :" + groups);

        Map<String, SecuInfo> infoMap = Maps.newConcurrentMap();
        List<SecurityStaticBasic> securities = queryUserSecurity("全部");
        for (SecurityStaticBasic staticBasic : securities) {
            infoMap.putIfAbsent(staticBasic.getSecurity().getCode() + "." + staticBasic.getSecurity().getMarket(),
                SecuInfo.builder().code(staticBasic.getSecurity().getCode()).market(
                    staticBasic.getSecurity().getMarket()).secuType(staticBasic.getSecType())
                    .name(staticBasic.getName()).build());
        }

        System.out.println(JSON.toJSONString(infoMap.values()));
    }

    public List<SecuInfo> parseSecuInfo() {

        List<SecuInfo> infos = JSON.parseObject(SECUINFOS, new TypeReference<List<SecuInfo>>() {});
        for (SecuInfo secuinfo : infos) {
            System.out.println(secuinfo.getCode() + " " + secuinfo.getName());
        }
        return infos;
    }

    public static List<String> queryUserGroup() {
        List<String> groups = Lists.newArrayList();
        try {
            UserSecurityGroup userSecurityGroup = UserSecurityGroup.newInstance();
            groups = userSecurityGroup.queryUserSecurityGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    public static List<SecurityStaticBasic> queryUserSecurity(String groupName) {
        List<SecurityStaticBasic> securities = Lists.newArrayList();
        try {
            UserSecurityGroup userSecurityGroup = UserSecurityGroup.newInstance();

            securities = userSecurityGroup.queryUserSecurity(groupName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return securities;
    }

    private static final List<Integer> MARKET_BLACK = Lists.newArrayList(QotMarket.QotMarket_JP_Security_VALUE,
        QotMarket.QotMarket_US_Security_VALUE, QotMarket.QotMarket_SG_Security_VALUE,
        QotMarket.QotMarket_HK_Security_VALUE);

    private static final List<Integer> SEC_BLACK = Lists.newArrayList(SecurityType.SecurityType_Bond_VALUE,
        SecurityType.SecurityType_Plate_VALUE, SecurityType.SecurityType_Trust_VALUE,
        SecurityType.SecurityType_Unknown_VALUE, SecurityType.SecurityType_Bwrt_VALUE,
        SecurityType.SecurityType_Drvt_VALUE, SecurityType.SecurityType_PlateSet_VALUE);

    private static boolean securityFilter(String code, int market, int secType) {
        if (MARKET_BLACK.contains(market) || SEC_BLACK.contains(secType)) {
            return false;
        }
        return true;
    }

    private static final List<String> GROUP_BLACK = Lists.newArrayList("加拿大", "澳洲", "港股期权", "期权", "美股期权", "债券", "全部",
        "期货");

    private static boolean groupFilter(String group) {
        if (GROUP_BLACK.contains(group)) {
            return false;
        }
        return true;
    }

}