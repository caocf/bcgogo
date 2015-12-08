package com.bcgogo.remind;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-21
 * Time: 下午2:15
 * To change this template use File | Settings | File Templates.
 */
public class UserRemindResponse {

    private String remindType;    //提醒
    private String estimateTime; //预计时间
    private String licenceNo;
    private String name;
    private String mobile;
    private String lastBill;
    private String comeTime;
    private double totalMoney;
    private String remindWay;

    public String getRemindType() {
        return remindType;
    }

    public void setRemindType(String remindType) {
        this.remindType = remindType;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastBill() {
        return lastBill;
    }

    public void setLastBill(String lastBill) {
        this.lastBill = lastBill;
    }

    public String getComeTime() {
        return comeTime;
    }

    public void setComeTime(String comeTime) {
        this.comeTime = comeTime;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getRemindWay() {
        return remindWay;
    }

    public void setRemindWay(String remindWay) {
        this.remindWay = remindWay;
    }
}
