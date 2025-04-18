/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.support;

import java.util.List;

import com.futu.openapi.pb.Common;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotGetSecuritySnapshot;
import com.futu.openapi.pb.QotGetSecuritySnapshot.Snapshot;
import com.futu.openapi.trade.base.BaseDaemon;
import com.google.common.collect.Lists;

/**
 * @author zhenmin
 * @version $Id: SecuritySnapshot.java, v 0.1 2022-03-07 11:14 上午 xuxu Exp $$
 */
public class SecuritySnapshot extends BaseDaemon {

    private static SecuritySnapshot securitySnapshot;

    public static synchronized SecuritySnapshot newInstance() {
        if (securitySnapshot == null) {
            securitySnapshot = new SecuritySnapshot();
            securitySnapshot.createCon();
        }
        return securitySnapshot;
    }

    private SecuritySnapshot() {}

    /**
     * query snapshot (batch)
     *
     * @param securities
     * @return
     * @throws Exception
     */
    public List<Snapshot> querySnapshot(List<QotCommon.Security> securities) throws Exception {

        List<Snapshot> snapshotList = Lists.newArrayList();
        QotGetSecuritySnapshot.Response rsp = getSecuritySnapshotSync(securities);
        if (rsp == null || rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            System.err.printf("querySnapshot err: retType=%d msg=%s\n", rsp == null ? null : rsp.getRetType(),
                rsp == null ? null : rsp.getRetMsg());
        } else {
            snapshotList = rsp.getS2C().getSnapshotListList();
        }
        return snapshotList;
    }

}