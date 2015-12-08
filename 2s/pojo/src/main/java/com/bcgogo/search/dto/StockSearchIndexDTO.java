package com.bcgogo.search.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-1-6
 * Time: 下午9:05
 * To change this template use File | Settings | File Templates.
 */
public class StockSearchIndexDTO implements Serializable {
  private Long shopId;         //店面ID
  private Long productId;
  private Long editDate; // 最新入库时间
  private String editDateStr;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String brand;
  private String model;
  private String year;
  private String engine;
  private Integer amount;//库存量

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }
}
