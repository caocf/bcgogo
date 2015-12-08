package com.bcgogo.product.dto;

import com.bcgogo.product.EngineRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class EngineDTO implements Serializable {
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Long yearId;
  private String engine;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  private String brand;
  private String model;
  private String year;

  public EngineDTO() {
  }

  public EngineDTO(EngineRequest request) {
    setBrandId(request.getBrandId());
    setMfrId(request.getMfrId());
    setModelId(request.getModelId());
    setYearId(request.getYearId());
    setEngine(request.getEngine());
    setState(request.getState());
    setMemo(request.getMemo());
    setShopId(request.getShopId());
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

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
