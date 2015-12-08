package com.bcgogo.txn.model;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-18
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
public class InventoryProductInfo {

    private Long created;
    private String name;
    private String brand;
    private String spec;
    private String model;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleYear;
    private String vehicleEngine;
    private Float price;
    private Float amount;
    private Float total;
    private String memo;

    public Long getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getSpec() {
        return spec;
    }

    public String getModel() {
        return model;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public String getVehicleYear() {
        return vehicleYear;
    }

    public String getVehicleEngine() {
        return vehicleEngine;
    }

    public Float getPrice() {
        return price;
    }

    public Float getAmount() {
        return amount;
    }

    public Float getTotal() {
        return total;
    }

    public String getMemo() {
        return memo;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public void setVehicleYear(String vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public void setVehicleEngine(String vehicleEngine) {
        this.vehicleEngine = vehicleEngine;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
