/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.base;

import java.util.HashMap;

import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Qot;
import com.futu.openapi.FTAPI_Conn_Trd;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Qot;
import com.futu.openapi.FTSPI_Trd;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhenmin
 * @version $Id: DaemonCon.java, v 0.1 2022-03-07 10:43 上午 xuxu Exp $$
 */
@Getter
@Setter
public class DaemonCon implements FTSPI_Conn, FTSPI_Qot, FTSPI_Trd {

    protected final Object qotLock = new Object();
    protected final Object trdLock = new Object();
    protected FTAPI_Conn_Qot qot = new FTAPI_Conn_Qot();
    protected FTAPI_Conn_Trd trd = new FTAPI_Conn_Trd();
    protected ConStatusEnum qotConnStatus = ConStatusEnum.DISCONNECT;
    protected ConStatusEnum trdConnStatus = ConStatusEnum.DISCONNECT;
    protected HashMap<Integer, ConReqInfo> qotConReqInfoMap = new HashMap<>();
    protected HashMap<Integer, ConReqInfo> trdConReqInfoMap = new HashMap<>();

    @Override
    public void onInitConnect(FTAPI_Conn client, long errCode, String desc) {
        if (client instanceof FTAPI_Conn_Qot) {
            synchronized (qotLock) {
                if (errCode == 0) {
                    qotConnStatus = ConStatusEnum.READY;
                }
                qotLock.notifyAll();
                return;
            }
        }

        if (client instanceof FTAPI_Conn_Trd) {
            synchronized (trdLock) {
                if (errCode == 0) {
                    trdConnStatus = ConStatusEnum.READY;
                }
                trdLock.notifyAll();
                return;
            }
        }
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        if (client instanceof FTAPI_Conn_Qot) {
            synchronized (qotLock) {
                qotConnStatus = ConStatusEnum.DISCONNECT;
                return;
            }
        }

        if (client instanceof FTAPI_Conn_Trd) {
            synchronized (trdLock) {
                trdConnStatus = ConStatusEnum.DISCONNECT;
            }
        }
    }

    protected boolean initConnectQotSync(String ip, short port) throws InterruptedException {
        qot.setConnSpi(this);
        qot.setQotSpi(this);
        synchronized (qotLock) {
            boolean ret = qot.initConnect(ip, port, false);
            if (!ret) { return false; }
            qotLock.wait();
            return qotConnStatus == ConStatusEnum.READY;
        }
    }

}
