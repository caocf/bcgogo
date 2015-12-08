package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "brand")
@XmlAccessorType(XmlAccessType.NONE)
public class BrandRequest {
  @XmlElement(name = "name")
  private String name;
  @XmlElement(name = "nameEn")
  private String nameEn;
  @XmlElement(name = "firstLetter")
  private String firstLetter;
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
}
