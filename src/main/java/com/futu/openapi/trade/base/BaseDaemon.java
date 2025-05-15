/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.futu.openapi.trade.base;

import java.util.List;

import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.ProtoID;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotCommon.Security;
import com.futu.openapi.pb.QotGetCapitalDistribution;
import com.futu.openapi.pb.QotGetCapitalFlow;
import com.futu.openapi.pb.QotGetOrderBook;
import com.futu.openapi.pb.QotGetSecuritySnapshot;
import com.futu.openapi.pb.QotGetStaticInfo;
import com.futu.openapi.pb.QotGetUserSecurity;
import com.futu.openapi.pb.QotGetUserSecurityGroup;
import com.futu.openapi.pb.QotModifyUserSecurity;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.futu.openapi.pb.QotSub;
import com.futu.openapi.pb.TrdCommon;
import com.futu.openapi.pb.TrdGetAccList;
import com.futu.openapi.pb.TrdGetFunds;
import com.futu.openapi.pb.TrdGetHistoryOrderFillList;
import com.futu.openapi.pb.TrdGetHistoryOrderList;
import com.futu.openapi.pb.TrdGetOrderFillList;
import com.futu.openapi.pb.TrdGetOrderList;
import com.futu.openapi.pb.TrdGetPositionList;
import com.futu.openapi.pb.TrdPlaceOrder;
import com.futu.openapi.pb.TrdSubAccPush;
import com.futu.openapi.pb.TrdUnlockTrade;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author zhenmin
 * @version $Id: BaseDaemon.java, v 0.1 2022-03-07 10:44 上午 xuxu Exp $$
 */

@Getter
@Setter
public class BaseDaemon extends DaemonCon {

    private static final Logger LOGGER = LogManager.getLogger(BaseDaemon.class);

    public void createCon() {
        try {
            //建立链接
            boolean ret = initConnectQotSync("127.0.0.1", (short)11111);
            //System.out.println(JSON.toJSONString(ret));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean initConnectTrdSync(String ip, short port) throws InterruptedException {
        trd.setConnSpi(this);
        trd.setTrdSpi(this);
        synchronized (trdLock) {
            boolean ret = trd.initConnect(ip, port, false);
            if (!ret) { return false; }
            trdLock.wait();
            return trdConnStatus == ConStatusEnum.READY;
        }
    }

    /**
     * 查询快照
     *
     * @param secList
     * @return
     * @throws InterruptedException
     */
    protected QotGetSecuritySnapshot.Response getSecuritySnapshotSync(List<Security> secList)
        throws InterruptedException {
        ConReqInfo ConReqInfo = null;
        Object syncEvent = new Object();

        synchronized (syncEvent) {
            synchronized (qotLock) {
                if (qotConnStatus != ConStatusEnum.READY) { return null; }
                QotGetSecuritySnapshot.C2S c2s = QotGetSecuritySnapshot.C2S.newBuilder()
                    .addAllSecurityList(secList)
                    .build();
                QotGetSecuritySnapshot.Request req = QotGetSecuritySnapshot.Request.newBuilder().setC2S(c2s).build();
                int sn = qot.getSecuritySnapshot(req);
                if (sn == 0) { return null; }
                ConReqInfo = new ConReqInfo(ProtoID.QOT_GETSECURITYSNAPSHOT, syncEvent);
                qotConReqInfoMap.put(sn, ConReqInfo);
            }
            syncEvent.wait();
            return (QotGetSecuritySnapshot.Response)ConReqInfo.getRsp();
        }
    }

    /**
     * 查询历史k线
     *
     * @param sec
     * @param klType
     * @param rehabType
     * @param beginTime
     * @param endTime
     * @param count
     * @param klFields
     * @param nextReqKey
     * @param extendedTime
     * @return
     * @throws InterruptedException
     */
    protected QotRequestHistoryKL.Response requestHistoryKLSync(QotCommon.Security sec,
                                                                QotCommon.KLType klType,
                                                                QotCommon.RehabType rehabType,
                                                                String beginTime,
                                                                String endTime,
                                                                Integer count,
                                                                Long klFields,
                                                                byte[] nextReqKey,
                                                                boolean extendedTime) throws InterruptedException {
        ConReqInfo reqInfo = null;
        Object syncEvent = new Object();

        synchronized (syncEvent) {
            synchronized (qotLock) {
                if (qotConnStatus != ConStatusEnum.READY) {
                    LOGGER.warn("requestHistoryKLSync failed qotConnStatus:{}", qotConnStatus);
                    return null;
                }
                QotRequestHistoryKL.C2S.Builder c2s = QotRequestHistoryKL.C2S.newBuilder()
                    .setSecurity(sec)
                    .setKlType(klType.getNumber())
                    .setRehabType(rehabType.getNumber())
                    .setBeginTime(beginTime)
                    .setEndTime(endTime)
                    .setExtendedTime(extendedTime);
                if (count != null) {
                    c2s.setMaxAckKLNum(count);
                }
                if (klFields != null) {
                    c2s.setNeedKLFieldsFlag(klFields);
                }
                if (nextReqKey.length > 0) {
                    c2s.setNextReqKey(ByteString.copyFrom(nextReqKey));
                }
                QotRequestHistoryKL.Request req = QotRequestHistoryKL.Request.newBuilder().setC2S(c2s).build();
                int sn = qot.requestHistoryKL(req);
                if (sn == 0) {
                    LOGGER.warn("requestHistoryKLSync failed sn:{}", sn);
                    return null;
                }
                reqInfo = new ConReqInfo(ProtoID.QOT_REQUESTHISTORYKL, syncEvent);
                qotConReqInfoMap.put(sn, reqInfo);
            }
            syncEvent.wait();
            return (QotRequestHistoryKL.Response)reqInfo.getRsp();
        }

    }

    /**
     * 查询码表
     *
     * @param c2s
     * @return
     * @throws InterruptedException
     */
    protected QotGetStaticInfo.Response getStaticInfoSync(QotGetStaticInfo.C2S c2s) throws InterruptedException {
        ConReqInfo ConReqInfo = null;
        Object syncEvent = new Object();

        synchronized (syncEvent) {
            synchronized (qotLock) {
                if (qotConnStatus != ConStatusEnum.READY) { return null; }
                QotGetStaticInfo.Request req = QotGetStaticInfo.Request.newBuilder().setC2S(c2s).build();
                int sn = qot.getStaticInfo(req);
                if (sn == 0) { return null; }
                ConReqInfo = new ConReqInfo(ProtoID.QOT_GETSTATICINFO, syncEvent);
                qotConReqInfoMap.put(sn, ConReqInfo);
            }
            syncEvent.wait();
            return (QotGetStaticInfo.Response)ConReqInfo.getRsp();
        }
    }

    protected QotGetUserSecurityGroup.Response getUserSecurityGroup(QotGetUserSecurityGroup.C2S c2s)
        throws InterruptedException {
        ConReqInfo ConReqInfo = null;
        Object syncEvent = new Object();
        synchronized (syncEvent) {
            synchronized (qotLock) {
                QotGetUserSecurityGroup.Request req = QotGetUserSecurityGroup.Request.newBuilder().setC2S(c2s).build();
                int sn = qot.getUserSecurityGroup(req);
                if (sn == 0) { return null; }
                ConReqInfo = new ConReqInfo(ProtoID.QOT_GETUSERSECURITYGROUP, syncEvent);
                qotConReqInfoMap.put(sn, ConReqInfo);
            }
            syncEvent.wait();
            return (QotGetUserSecurityGroup.Response)ConReqInfo.getRsp();
        }
    }

    /**
     * 修改自选分组下的股票列表
     *
     * @param c2s
     * @return
     * @throws InterruptedException
     */
    protected QotModifyUserSecurity.Response getModifyUserSecurity(QotModifyUserSecurity.C2S c2s)
        throws InterruptedException {
        ConReqInfo ConReqInfo = null;
        Object syncEvent = new Object();
        synchronized (syncEvent) {
            synchronized (qotLock) {
                QotModifyUserSecurity.Request req = QotModifyUserSecurity.Request.newBuilder().setC2S(c2s).build();
                int sn = qot.modifyUserSecurity(req);
                if (sn == 0) { return null; }
                ConReqInfo = new ConReqInfo(ProtoID.QOT_MODIFYUSERSECURITY, syncEvent);
                qotConReqInfoMap.put(sn, ConReqInfo);
            }
            syncEvent.wait();
            return (QotModifyUserSecurity.Response)ConReqInfo.getRsp();
        }
    }

    protected QotGetUserSecurity.Response getUserSecurity(QotGetUserSecurity.C2S c2s)
        throws InterruptedException {
        ConReqInfo ConReqInfo = null;
        Object syncEvent = new Object();
        synchronized (syncEvent) {
            synchronized (qotLock) {
                QotGetUserSecurity.Request req = QotGetUserSecurity.Request.newBuilder().setC2S(c2s).build();
                int sn = qot.getUserSecurity(req);
                if (sn == 0) { return null; }
                ConReqInfo = new ConReqInfo(ProtoID.QOT_GETUSERSECURITY, syncEvent);
                qotConReqInfoMap.put(sn, ConReqInfo);
            }
            syncEvent.wait();
            return (QotGetUserSecurity.Response)ConReqInfo.getRsp();
        }
    }

    /**
     * 资金流向
     *
     * @param c2s
     * @return
     * @throws InterruptedException
     */
    protected QotGetCapitalFlow.Response getCapitalFlow(QotGetCapitalFlow.C2S c2s)
        throws InterruptedException {
        ConReqInfo ConReqInfo = null;
        Object syncEvent = new Object();
        synchronized (syncEvent) {
            synchronized (qotLock) {
                QotGetCapitalFlow.Request req = QotGetCapitalFlow.Request.newBuilder().setC2S(c2s).build();
                int sn = qot.getCapitalFlow(req);
                if (sn == 0) { return null; }
                ConReqInfo = new ConReqInfo(ProtoID.QOT_GETCAPITALFLOW, syncEvent);
                qotConReqInfoMap.put(sn, ConReqInfo);
            }
            syncEvent.wait();
            return (QotGetCapitalFlow.Response)ConReqInfo.getRsp();
        }
    }

    /**
     * 获取资金分布
     *
     * @param c2s
     * @return
     * @throws InterruptedException
     */
    protected QotGetCapitalDistribution.Response getCapitalDistribution(QotGetCapitalDistribution.C2S c2s)
        throws InterruptedException {
        ConReqInfo ConReqInfo = null;
        Object syncEvent = new Object();
        synchronized (syncEvent) {
            synchronized (qotLock) {
                QotGetCapitalDistribution.Request req = QotGetCapitalDistribution.Request.newBuilder().setC2S(c2s)
                    .build();
                int sn = qot.getCapitalDistribution(req);
                if (sn == 0) { return null; }
                ConReqInfo = new ConReqInfo(ProtoID.QOT_GETCAPITALDISTRIBUTION, syncEvent);
                qotConReqInfoMap.put(sn, ConReqInfo);
            }
            syncEvent.wait();
            return (QotGetCapitalDistribution.Response)ConReqInfo.getRsp();
        }
    }

    @Override
    public void onReply_GetCapitalFlow(FTAPI_Conn client, int nSerialNo, QotGetCapitalFlow.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_GETCAPITALFLOW, rsp);
    }

    @Override
    public void onReply_GetCapitalDistribution(FTAPI_Conn client, int nSerialNo,
                                               QotGetCapitalDistribution.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_GETCAPITALDISTRIBUTION, rsp);
    }

    @Override
    public void onReply_GetUserSecurity(FTAPI_Conn client, int nSerialNo, QotGetUserSecurity.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_GETUSERSECURITY, rsp);
    }

    @Override
    public void onReply_GetUserSecurityGroup(FTAPI_Conn client, int nSerialNo, QotGetUserSecurityGroup.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_GETUSERSECURITYGROUP, rsp);
    }

    @Override
    public void onReply_ModifyUserSecurity(FTAPI_Conn client, int nSerialNo, QotModifyUserSecurity.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_MODIFYUSERSECURITY, rsp);
    }

    @Override
    public void onReply_GetSecuritySnapshot(FTAPI_Conn client, int nSerialNo, QotGetSecuritySnapshot.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_GETSECURITYSNAPSHOT, rsp);
    }

    @Override
    public void onReply_GetStaticInfo(FTAPI_Conn client, int nSerialNo, QotGetStaticInfo.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_GETSTATICINFO, rsp);
    }

    @Override
    public void onReply_Sub(FTAPI_Conn client, int nSerialNo, QotSub.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_SUB, rsp);
    }

    @Override
    public void onReply_GetOrderBook(FTAPI_Conn client, int nSerialNo, QotGetOrderBook.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_GETORDERBOOK, rsp);
    }

    @Override
    public void onReply_RequestHistoryKL(FTAPI_Conn client, int nSerialNo, QotRequestHistoryKL.Response rsp) {
        handleQotOnReply(nSerialNo, ProtoID.QOT_REQUESTHISTORYKL, rsp);
    }

    @Override
    public void onReply_PlaceOrder(FTAPI_Conn client, int nSerialNo, TrdPlaceOrder.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_PLACEORDER, rsp);
    }

    @Override
    public void onReply_UnlockTrade(FTAPI_Conn client, int nSerialNo, TrdUnlockTrade.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_UNLOCKTRADE, rsp);
    }

    @Override
    public void onReply_SubAccPush(FTAPI_Conn client, int nSerialNo, TrdSubAccPush.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_SUBACCPUSH, rsp);
    }

    @Override
    public void onReply_GetAccList(FTAPI_Conn client, int nSerialNo, TrdGetAccList.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_GETACCLIST, rsp);
    }

    @Override
    public void onReply_GetFunds(FTAPI_Conn client, int nSerialNo, TrdGetFunds.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_GETFUNDS, rsp);
    }

    @Override
    public void onReply_GetOrderList(FTAPI_Conn client, int nSerialNo, TrdGetOrderList.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_GETORDERLIST, rsp);
    }

    @Override
    public void onReply_GetOrderFillList(FTAPI_Conn client, int nSerialNo, TrdGetOrderFillList.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_GETORDERFILLLIST, rsp);
    }

    @Override
    public void onReply_GetHistoryOrderList(FTAPI_Conn client, int nSerialNo, TrdGetHistoryOrderList.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_GETHISTORYORDERLIST, rsp);
    }

    @Override
    public void onReply_GetHistoryOrderFillList(FTAPI_Conn client, int nSerialNo,
                                                TrdGetHistoryOrderFillList.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_GETHISTORYORDERFILLLIST, rsp);
    }

    @Override
    public void onReply_GetPositionList(FTAPI_Conn client, int nSerialNo, TrdGetPositionList.Response rsp) {
        handleTrdOnReply(nSerialNo, ProtoID.TRD_GETPOSITIONLIST, rsp);
    }

    ConReqInfo getQotConReqInfo(int serialNo, int protoID) {
        synchronized (qotLock) {
            ConReqInfo info = qotConReqInfoMap.getOrDefault(serialNo, null);
            if (info != null && info.getProtoID() == protoID) {
                qotConReqInfoMap.remove(serialNo);
                return info;
            }
        }
        return null;
    }

    ConReqInfo getTrdConReqInfo(int serialNo, int protoID) {
        synchronized (trdLock) {
            ConReqInfo info = trdConReqInfoMap.getOrDefault(serialNo, null);
            if (info != null && info.getProtoID() == protoID) {
                trdConReqInfoMap.remove(serialNo);
                return info;
            }
        }
        return null;
    }

    void handleQotOnReply(int serialNo, int protoID, GeneratedMessageV3 rsp) {
        ConReqInfo ConReqInfo = getQotConReqInfo(serialNo, protoID);
        if (ConReqInfo != null) {
            synchronized (ConReqInfo.getSyncEvent()) {
                ConReqInfo.setRsp(rsp);
                ConReqInfo.getSyncEvent().notifyAll();
            }
        }
    }

    void handleTrdOnReply(int serialNo, int protoID, GeneratedMessageV3 rsp) {
        ConReqInfo ConReqInfo = getTrdConReqInfo(serialNo, protoID);
        if (ConReqInfo != null) {
            synchronized (ConReqInfo.getSyncEvent()) {
                ConReqInfo.setRsp(rsp);
                ConReqInfo.getSyncEvent().notifyAll();
            }
        }
    }

    static QotCommon.Security makeSec(QotCommon.QotMarket market, String code) {
        QotCommon.Security sec = QotCommon.Security.newBuilder().setCode(code)
            .setMarket(market.getNumber())
            .build();
        return sec;
    }

    static TrdCommon.TrdHeader makeTrdHeader(TrdCommon.TrdEnv trdEnv,
                                             long accID,
                                             TrdCommon.TrdMarket trdMarket) {
        TrdCommon.TrdHeader header = TrdCommon.TrdHeader.newBuilder()
            .setTrdEnv(trdEnv.getNumber())
            .setAccID(accID)
            .setTrdMarket(trdMarket.getNumber())
            .build();
        return header;
    }

}


