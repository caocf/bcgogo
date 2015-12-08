package com.bcgogo.product.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-1-5
 * Time: 下午3:53
 * To change this template use File | Settings | File Templates.
 */
public class NormalModelDTO {
  private Long normalBrandId;
  private Long mfrId;
  private String name;
  private String nameEn;
  private String firstLetter;
  private Long state;
  private String memo;
  private Long shopId;
  private Long id;

  public NormalModelDTO() {
  }

  public NormalModelDTO(Long id,String name,Long normalBrandId) {
    this.normalBrandId = normalBrandId;
    this.name = name;
    this.id = id;
  }

  public Long getNormalBrandId() {
    return normalBrandId;
  }

  public void setNormalBrandId(Long normalBrandId) {
    this.normalBrandId = normalBrandId;
  }

  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
