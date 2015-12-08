package com.bcgogo.txn.dto;

import java.util.List;

/**
 * 充值完成后返回结果页面的信息封装类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-6
 * Time: 下午2:42
 * To change this template use File | Settings | File Templates.
 */
public class SmsRechargeCompleteDTO {

    private String userName;
    private String shopName;
    private double smsBalance;
    private double rechargeTotal;
    private int rechargeHistoryTotal;
    private List<SmsRechargeDTO> smsRechargeDTOList;

    public String getUserName() {
        return userName;
    }

    public String getShopName() {
        return shopName;
    }

    public double getSmsBalance() {
        return smsBalance;
    }

    public double getRechargeTotal() {
        return rechargeTotal;
    }

    public int getRechargeHistoryTotal() {
        return rechargeHistoryTotal;
    }

    public List<SmsRechargeDTO> getSmsRechargeDTOList() {
        return smsRechargeDTOList;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setSmsBalance(double smsBalance) {
        this.smsBalance = smsBalance;
    }

    public void setRechargeTotal(double rechargeTotal) {
        this.rechargeTotal = rechargeTotal;
    }

    public void setRechargeHistoryTotal(int rechargeHistoryTotal) {
        this.rechargeHistoryTotal = rechargeHistoryTotal;
    }

    public void setSmsRechargeDTOList(List<SmsRechargeDTO> smsRechargeDTOList) {
        this.smsRechargeDTOList = smsRechargeDTOList;
    }
}
