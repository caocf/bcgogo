package com.bcgogo.txn.dto;

import com.bcgogo.enums.DeletedType;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class StoreHouseDTO {
  private Long id;
  private Long userId;
  private String idStr;
  private Long shopId;
  private String name;
  private String address;
  private String memo;
  private DeletedType deleted;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null){
      this.setIdStr(id.toString());
    }else{
     this.setIdStr("");
    }
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
