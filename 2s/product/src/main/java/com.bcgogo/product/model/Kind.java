package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.KindDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "kind")
public class Kind extends LongIdentifier {
  private Long categoryId;
  private String name;
  private String nameEn;
  private Long state;
  private String memo;
  private Long shopId;
  private String status;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }


  public Kind() {
  }

  @Column(name = "category_id")
  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  @Column(name = "name", length = 200)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "name_en", length = 200)
  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "memo", length = 2000)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }


  @Column(name = "status", length = 50)
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  public KindDTO toKindDTO(){
    KindDTO kindDTO = new KindDTO();
    kindDTO.setCategoryId(this.getCategoryId());
    kindDTO.setId(this.getId());
    kindDTO.setMemo(this.getMemo());
    kindDTO.setName(this.getName());
    kindDTO.setNameEn(this.getNameEn());
    kindDTO.setShopId(this.getShopId());
    kindDTO.setState(this.getState());
    return kindDTO;
  }

}
