package com.bcgogo.user;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-21
 * Time: 上午10:43
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventoryResponse {

    private Long date;
    private String name;   //品名
    private String brand;  //品牌
    private String spec;   //规格
    private String model;  //型号
    private String vehicleBrand;     //车辆品牌
    private String vehicleModel;     //车辆车型
    private String year;    //年代
    private String vin;   //排量
    private double price;  //单价
    private int amount;    //采购量
    private double total;   //金额
    private String memo;    //备注

    private Long supplierId;
    private Long purchaseInventoryId;

    public Long getPurchaseInventoryId() {
        return purchaseInventoryId;
    }

    public void setPurchaseInventoryId(Long purchaseInventoryId) {
        this.purchaseInventoryId = purchaseInventoryId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    private String dateStr;

    public String getDateStr() {
        Long time = this.getDate();
        if(time==null){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date(time);
        return sdf.format(d);
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
