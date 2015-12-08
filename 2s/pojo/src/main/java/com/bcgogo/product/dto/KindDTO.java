package com.bcgogo.product.dto;

import com.bcgogo.product.KindRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午2:31
 * To change this template use File | Settings | File Templates.
 */
public class KindDTO implements Serializable {
  private Long categoryId;
  private String name;
  private String nameEn;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;


  public KindDTO() {
  }

  public KindDTO(KindRequest request) {
    setCategoryId(request.getCategoryId());
    setName(request.getName());
    setNameEn(request.getNameEn());
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
