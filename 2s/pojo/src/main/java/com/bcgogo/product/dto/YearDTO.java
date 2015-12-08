package com.bcgogo.product.dto;

import com.bcgogo.product.YearRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public class YearDTO implements Serializable {
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Integer year;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  public YearDTO() {
  }

  public YearDTO(YearRequest request) {
    setBrandId(request.getBrandId());
    setMfrId(request.getMfrId());
    setModelId(request.getModelId());
    setYear(request.getYear());
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

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
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
