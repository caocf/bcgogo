package com.bcgogo.remind;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-18
 * Time: 下午4:49
 * To change this template use File | Settings | File Templates.
 */
public class InventoryRemindResponse {
    private String remindType;
    private Calendar estimateTime;
    private String estimateTimeStr;
    private String productNo;
    private String supplier;
    private String productName;
    private String brand;  //品牌
    private String specification; //规格
    private String model; //型号
    private String vehicleBrand; //车辆品牌
    private String vehicleModel;  //车辆
    private int vehicleYear;
    private String vehicleEngine;
    private double price;
    private int number;
    private double totalPrice;
    private Double inventoryNum;     //库存数量
    private Long supplierId;        //供应商Id   zcl
    private Long purchaseOrderId;    //采购单ID
    private String unit;
    private String purchaseOrderIdStr;
    private String receiptNo;

    public String getPurchaseOrderIdStr() {
      return purchaseOrderIdStr;
    }

    public void setPurchaseOrderIdStr(String purchaseOrderIdStr) {
      this.purchaseOrderIdStr = purchaseOrderIdStr;
    }

    public Long getPurchaseOrderId() {
      return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
      this.purchaseOrderId = purchaseOrderId;
      this.purchaseOrderIdStr = String.valueOf(purchaseOrderId);
    }

    public Long getSupplierId() {
      return supplierId;
    }

    public void setSupplierId(Long supplierId) {
      this.supplierId = supplierId;
    }
    public String getEstimateTimeStr() {
        return estimateTimeStr;
    }

    public void setEstimateTimeStr(String estimateTimeStr) {
        this.estimateTimeStr = estimateTimeStr;
    }

    public String getRemindType() {
        return remindType;
    }

    public void setRemindType(String remindType) {
        this.remindType = remindType;
    }

    public Double getInventoryNum() {
        return inventoryNum;
    }

    public void setInventoryNum(Double inventoryNum) {
        this.inventoryNum = inventoryNum;
    }

    public Calendar getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(Calendar estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(int vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getVehicleEngine() {
        return vehicleEngine;
    }

    public void setVehicleEngine(String vehicleEngine) {
        this.vehicleEngine = vehicleEngine;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUnit() {
      return unit;
    }

    public void setUnit(String unit) {
      this.unit = unit;
    }

    public String getReceiptNo() {
      return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
      this.receiptNo = receiptNo;
    }
}
