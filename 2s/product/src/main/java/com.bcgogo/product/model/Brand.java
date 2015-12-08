package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.BrandDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午5:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "brand")
public class Brand extends LongIdentifier {
  private String name;
  private String nameEn;
  private String firstLetter;
  private Long state;
  private String memo;
  private Long shopId;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  @Column(name = "first_letter", length = 200)
  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
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

  public BrandDTO toDTO()
  {
    BrandDTO brandDTO = new BrandDTO();

    brandDTO.setId(this.getId());
    brandDTO.setName(this.getName());
    brandDTO.setNameEn(this.getNameEn());
    brandDTO.setFirstLetter(this.getFirstLetter());
    brandDTO.setState(this.getState());
    brandDTO.setMemo(this.getMemo());
    brandDTO.setShopId(this.getShopId());

    return brandDTO;
  }
}
