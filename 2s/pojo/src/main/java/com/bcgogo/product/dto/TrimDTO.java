package com.bcgogo.product.dto;

import com.bcgogo.product.TrimRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class TrimDTO implements Serializable {
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Long yearId;
  private String name;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  public TrimDTO() {
  }

  public TrimDTO(TrimRequest request) {
    setBrandId(request.getBrandId());
    setMfrId(request.getMfrId());
    setModelId(request.getModelId());
    setYearId(request.getYearId());
    setName(request.getName());
    setState(request.getState());
    setMemo(request.getMemo());
    setShopId(request.getShopId());
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
