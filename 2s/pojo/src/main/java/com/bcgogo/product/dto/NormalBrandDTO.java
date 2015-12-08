package com.bcgogo.product.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-1-5
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
public class NormalBrandDTO {
  private String name;
  private String nameEn;
  private String firstLetter;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  public NormalBrandDTO() {
  }

  public NormalBrandDTO(Long id, String name) {
    this.id = id;
    this.name = name;
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

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

}
