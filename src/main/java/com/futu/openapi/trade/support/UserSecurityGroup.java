/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.support;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.futu.openapi.pb.Common;
import com.futu.openapi.pb.QotCommon.SecurityStaticBasic;
import com.futu.openapi.pb.QotCommon.SecurityStaticInfo;
import com.futu.openapi.pb.QotGetUserSecurity;
import com.futu.openapi.pb.QotGetUserSecurityGroup;
import com.futu.openapi.pb.QotModifyUserSecurity;
import com.futu.openapi.pb.QotModifyUserSecurity.ModifyUserSecurityOp;
import com.futu.openapi.trade.base.BaseDaemon;
import com.futu.openapi.trade.base.ConStatusEnum;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author zhenmin
 * @version $Id: UserSecurityGroup.java, v 0.1 2022-04-02 10:38 xuxu Exp $$
 */
public class UserSecurityGroup extends BaseDaemon {

    private static final Logger LOGGER = LogManager.getLogger(UserSecurityGroup.class);

    private static UserSecurityGroup userSecurityGroup;

    public static synchronized UserSecurityGroup newInstance() {
        if (userSecurityGroup == null) {
            userSecurityGroup = new UserSecurityGroup();
            userSecurityGroup.createCon();
        }
        checkCon();
        return userSecurityGroup;
    }

    private UserSecurityGroup() {}

    private static void checkCon() {
        try {
            //尝试重新启动链接
            if (userSecurityGroup.qotConnStatus != ConStatusEnum.READY) {
                LOGGER.error("queryUserSecurity checkCon err reconnection !");
                //重新创建链接
                userSecurityGroup.createCon();
                LOGGER.error("queryUserSecurity checkCon err reconnection end! status:{}",
                    userSecurityGroup.qotConnStatus);
            }
        } catch (Exception e) {
            LOGGER.error("check con ex", e);
        }

    }

    /**
     * @return
     * @throws Exception
     */
    public List<String> queryUserSecurityGroup() throws Exception {

        List<String> groupNames = Lists.newArrayList();
        QotGetUserSecurityGroup.C2S c2s = QotGetUserSecurityGroup.C2S.newBuilder()
            .setGroupType(QotGetUserSecurityGroup.GroupType.GroupType_All_VALUE)
            .build();

        QotGetUserSecurityGroup.Response rsp = getUserSecurityGroup(c2s);
        if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            System.err.printf("queryUserSecurityGroup fail: %s\n", rsp == null ? null : rsp.getRetMsg());
            //尝试重新启动链接
            if (rsp == null && userSecurityGroup.qotConnStatus != ConStatusEnum.READY) {
                LOGGER.error("queryUserSecurity err reconnection !");
                //重新创建链接
                userSecurityGroup.createCon();
                LOGGER.error("queryUserSecurity err reconnection end! status:{}", userSecurityGroup.qotConnStatus);
            }
            return groupNames;
        }
        for (QotGetUserSecurityGroup.GroupData groupData : rsp.getS2C().getGroupListList()) {
            groupNames.add(groupData.getGroupName());
        }
        return groupNames;
    }

    public void modifyUserSecurity(String groupName) throws Exception {
        QotModifyUserSecurity.C2S c2s = QotModifyUserSecurity.C2S.newBuilder()
            .setGroupName(groupName).setOp(ModifyUserSecurityOp.ModifyUserSecurityOp_Add_VALUE)
            .build();

        QotModifyUserSecurity.Response rsp = getModifyUserSecurity(c2s);

        System.out.println(JSON.toJSONString(rsp));
    }

    public List<SecurityStaticBasic> queryUserSecurity(String groupName) throws Exception {

        List<SecurityStaticBasic> securities = Lists.newArrayList();
        QotGetUserSecurity.C2S c2s = QotGetUserSecurity.C2S.newBuilder()
            .setGroupName(groupName)
            .build();

        QotGetUserSecurity.Response rsp = getUserSecurity(c2s);

        if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            System.err.printf("queryUserSecurity fail: %s\n", rsp == null ? null : rsp.getRetMsg());
            //尝试重新启动链接
            if (rsp == null && userSecurityGroup.qotConnStatus != ConStatusEnum.READY) {
                LOGGER.error("queryUserSecurity err reconnection !");
                //重新创建链接
                userSecurityGroup.createCon();
                LOGGER.error("queryUserSecurity err reconnection end! status:{}", userSecurityGroup.qotConnStatus);
            }
            return securities;
        }
        for (SecurityStaticInfo staticInfo : rsp.getS2C().getStaticInfoListList()) {
            securities.add(staticInfo.getBasic());
        }
        return securities;
    }

}
