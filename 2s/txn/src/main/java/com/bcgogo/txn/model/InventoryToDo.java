package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-24
 * Time: 下午4:11
 * To change this template use File | Settings | File Templates.
 */

//待入库
@Entity
@Table(name="inventory_todo")
public class InventoryToDo  extends LongIdentifier{

    private Long shopId;
    private String remindType;
    private String estimateTime;
    private String productNo; //商品编号
    private String supplier;
    private String productCategory;//品名
    private String productBrand;
    private String specification;
    private String model;  //型号
    private String vehicleBrand; //车辆品牌
    private String vehicleModel;//车型
    private Long year; //年代
    private String engine; //排量
    private double price; //单价
    private Long productNumber;  //入库量
    private double totalMoney;  //总价
    private Long inventoryNumber;  //库存量

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

    @Column(name="product_no")
    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    @Column(name="supplier")
    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    @Column(name="product_category")
    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    @Column(name="product_brand")
    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    @Column(name="specification")
    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    @Column(name="model")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Column(name="vehicle_brand")
    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    @Column(name="vehicle_model")
    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    @Column(name="year")
    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    @Column(name="engine")
    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    @Column(name="price")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Column(name="product_number")
    public Long getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(Long productNumber) {
        this.productNumber = productNumber;
    }

    @Column(name="total_money")
    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Column(name="inventory_number")
    public Long getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(Long inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

}
