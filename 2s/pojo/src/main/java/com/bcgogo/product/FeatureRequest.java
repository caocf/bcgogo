package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午10:58
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "feature")
@XmlAccessorType(XmlAccessType.NONE)
public class FeatureRequest {
  @XmlElement(name = "kindId")
  private Long kindId;
  @XmlElement(name = "name")
  private String name;
  @XmlElement(name = "nameEn")
  private String nameEn;
  @XmlElement(name = "dataType")
  private Long dataType;
  @XmlElement(name = "editMode")
  private String editMode;
  @XmlElement(name = "listMode")
  private Integer listMode;
  @XmlElement(name = "viewMode")
  private Integer viewMode;
  @XmlElement(name = "iconMode")
  private Integer iconMode;
  @XmlElement(name = "state")
  private Long state;
  @XmlElement(name = "memo")
  private String memo;
  @XmlElement(name = "shopId")
  private Long shopId;

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

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
