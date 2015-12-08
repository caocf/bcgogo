package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "kind")
@XmlAccessorType(XmlAccessType.NONE)
public class KindRequest {
  @XmlElement(name = "categoryId")
  private Long categoryId;
  @XmlElement(name = "name")
  private String name;
  @XmlElement(name = "nameEn")
  private String nameEn;
  @XmlElement(name = "state")
  private Long state;
  @XmlElement(name = "memo")
  private String memo;
  @XmlElement(name = "id")
  private Long id;
  @XmlElement(name = "shopId")
  private Long shopId;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
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
