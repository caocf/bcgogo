package com.bcgogo.product.dto;

import com.bcgogo.product.ModelRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午12:05
 * To change this template use File | Settings | File Templates.
 */
public class ModelDTO implements Serializable {
  private Long brandId;
  private Long mfrId;
  private String name;
  private String nameEn;
  private String firstLetter;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  private String virtualFields;  //

  public ModelDTO() {
  }

  public ModelDTO(ModelRequest request) {
    setBrandId(request.getBrandId());
    setMfrId(request.getMfrId());
    setName(request.getName());
    setNameEn(request.getNameEn());
    setFirstLetter(request.getFirstLetter());
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
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

  public String getVirtualFields() {
    return this.virtualFields;
  }

  public void setVirtualFields(String virtualFields) {
    this.virtualFields = virtualFields;
  }
}
