package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-30
 * Time: 上午10:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="user_todo")
public class UserToDo extends LongIdentifier{

    private Long shopId;
    private String remindType;
    private String estimateTime;
    private String licenceNo;
    private String name;
    private String mobile;
    private String billContent;
    private String comeTime;
    private double totalMoney;
    private String remindWay;

    @Column(name="shop_id")
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Column(name="remind_type")
    public String getRemindType() {
        return remindType;
    }

    public void setRemindType(String remindType) {
        this.remindType = remindType;
    }

    @Column(name="estimate_time")
    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    @Column(name="licence_no")
    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name="bill_content")
    public String getBillContent() {
        return billContent;
    }

    public void setBillContent(String billContent) {
        this.billContent = billContent;
    }

    @Column(name="come_time")
    public String getComeTime() {
        return comeTime;
    }

    public void setComeTime(String comeTime) {
        this.comeTime = comeTime;
    }

    @Column(name="total_money")
    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Column(name="remind_way")
    public String getRemindWay() {
        return remindWay;
    }

    public void setRemindWay(String remindWay) {
        this.remindWay = remindWay;
    }

}
