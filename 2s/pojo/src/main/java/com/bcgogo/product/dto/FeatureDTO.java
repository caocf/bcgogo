package com.bcgogo.product.dto;

import com.bcgogo.product.FeatureRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:49
 * To change this template use File | Settings | File Templates.
 */
public class FeatureDTO implements Serializable {
  private Long kindId;
  private String name;
  private String nameEn;
  private Long dataType;
  private String editMode;
  private Integer listMode;
  private Integer viewMode;
  private Integer iconMode;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  public FeatureDTO() {
  }

  public FeatureDTO(FeatureRequest request) {
    setKindId(request.getKindId());
    setName(request.getName());
    setNameEn(request.getNameEn());
    setDataType(request.getDataType());
    setEditMode(request.getEditMode());
    setListMode(request.getListMode());
    setViewMode(request.getViewMode());
    setIconMode(request.getIconMode());
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

  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
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

  public Long getDataType() {
    return dataType;
  }

  public void setDataType(Long dataType) {
    this.dataType = dataType;
  }

  public String getEditMode() {
    return editMode;
  }

  public void setEditMode(String editMode) {
    this.editMode = editMode;
  }

  public Integer getListMode() {
    return listMode;
  }

  public void setListMode(Integer listMode) {
    this.listMode = listMode;
  }

  public Integer getViewMode() {
    return viewMode;
  }

  public void setViewMode(Integer viewMode) {
    this.viewMode = viewMode;
  }

  public Integer getIconMode() {
    return iconMode;
  }

  public void setIconMode(Integer iconMode) {
    this.iconMode = iconMode;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
