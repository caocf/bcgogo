package com.bcgogo.remind.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-30
 * Time: 上午10:40
 * To change this template use File | Settings | File Templates.
 */
public class UserToDoDTO implements Serializable {

    private Long id;
    private Long shopId;
    private String remindType;
    private Long remindCode;
    private String estimateTime;
    private String licenceNo;
    private String name;
    private String mobile;
    private String billContent;
    private String comeTime;
    private double totalMoney;
    private String remindWay;

    public Long getRemindCode() {
        return remindCode;
    }

    public void setRemindCode(Long remindCode) {
        this.remindCode = remindCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

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

    public String getBillContent() {
        return billContent;
    }

    public void setBillContent(String billContent) {
        this.billContent = billContent;
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
