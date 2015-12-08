package com.bcgogo.txn.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.StoreHouseDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "storehouse")
public class StoreHouse extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private String name;
  private String address;
  private String memo;
  private DeletedType deleted = DeletedType.FALSE;//默认都为false

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  @Column(name = "address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public StoreHouseDTO toDTO(){
    StoreHouseDTO storeHouseDTO = new StoreHouseDTO();
    storeHouseDTO.setAddress(this.getAddress());
    storeHouseDTO.setDeleted(this.getDeleted());
    storeHouseDTO.setMemo(this.getMemo());
    storeHouseDTO.setId(this.getId());
    storeHouseDTO.setName(this.getName());
    storeHouseDTO.setShopId(this.getShopId());
    storeHouseDTO.setUserId(this.getUserId());
    return  storeHouseDTO;

  }
}
