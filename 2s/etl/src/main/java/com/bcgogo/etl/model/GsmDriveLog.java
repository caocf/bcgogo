package com.bcgogo.etl.model;

/**
 * Created by Luffy.Liu on 2015/8/7.
 */
public class GsmDriveLog {
    private String uuid;//对应与每段轨迹的
    private String appUserNo;//用户账户名
    private GsmVehicleData accOn;//对应于点火信号
    private GsmVehicleData accOff;//熄火信号
    private GsmVehicleData lastData;//最新一条记录
    private Long lastTime;//最新记录更新时间

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }



    public String getAppUserNo() {
        return appUserNo;
    }

    public void setAppUserNo(String appUserNo) {
        this.appUserNo = appUserNo;
    }
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uid) {
        this.uuid = uid;
    }

    public GsmVehicleData getAccOn() {
        return accOn;
    }

    public void setAccOn(GsmVehicleData accOn) {
        this.accOn = accOn;
    }

    public GsmVehicleData getAccOff() {
        return accOff;
    }

    public void setAccOff(GsmVehicleData accOff) {
        this.accOff = accOff;
    }

    public GsmVehicleData getLastData() {
        return lastData;
    }

    public void setLastData(GsmVehicleData lastData) {
        this.lastData = lastData;
    }


}
