package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: JYW
 * Date: 12-1-11
 * Time: 下午3:41
 * To change this template use File | Settings | File Templates.
 */
public class ItemDTO implements Serializable {
     //po.suppllier as suppllier ,p.name,p.brand,p.spec,p.model,pli.price,poi.amount,poi.total
    private Long id;
    private Long supplierId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    private String suppllier;
    private String name;
    private String brand;
    private String spec;
    private String model;
    private Double price;
    private Double amount;
    private Double total;

    public String getSuppllier() {
        return suppllier;
    }

    public void setSuppllier(String suppllier) {
        this.suppllier = suppllier;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
