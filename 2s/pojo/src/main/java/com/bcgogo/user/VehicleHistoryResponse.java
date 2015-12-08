package com.bcgogo.user;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-16
 * Time: 上午9:46
 * To change this template use File | Settings | File Templates.
 */
public class VehicleHistoryResponse {

    private Long customerId;
    private Long vehicleId;
    private Long orderType;       //  1--维修单   2--销售单
    private Long orderId;
    private String statusStr;

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    private Long comsuDate;  //消费日期
    private String comsuDateStr;
    private String licenceNo;
    private String content;
    private String repair;      //施工
    private String cailiao;
    private double totalMoney;
    private Long endDate;    //出厂
    private String endDateStr;
    private Long status;
    private double arrears;
    private Long repayDate;
    private String repayDateStr;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getOrderType() {
        return orderType;
    }

    public void setOrderType(Long orderType) {
        this.orderType = orderType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getComsuDate() {
        return comsuDate;
    }

    public void setComsuDate(Long comsuDate) {
        this.comsuDate = comsuDate;
    }

    public String getComsuDateStr() {
        if(this.getComsuDate()==null){
            return comsuDateStr;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date(this.getComsuDate());
        return sdf.format(d);
    }

    public void setComsuDateStr(String comsuDateStr) {
        this.comsuDateStr = comsuDateStr;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }

    public String getCailiao() {
        return cailiao;
    }

    public void setCailiao(String cailiao) {
        this.cailiao = cailiao;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getEndDateStr() {
        if(this.getEndDate()==null){
            return endDateStr;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date(this.getEndDate());
        return sdf.format(d);
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public double getArrears() {
        return arrears;
    }

    public void setArrears(double arrears) {
        this.arrears = arrears;
    }

    public Long getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(Long repayDate) {
        this.repayDate = repayDate;
    }

    public String getRepayDateStr() {
        if(this.getRepayDate()==null){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(this.getRepayDate());
        return sdf.format(d);
    }

    public void setRepayDateStr(String repayDateStr) {
        this.repayDateStr = repayDateStr;
    }
}
