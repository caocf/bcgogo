package com.bcgogo.product.dto;

import com.bcgogo.product.FeatureModeRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午12:02
 * To change this template use File | Settings | File Templates.
 */
public class FeatureModeDTO implements Serializable {
  private String name;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  public FeatureModeDTO() {
  }

  public FeatureModeDTO(FeatureModeRequest request) {
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
