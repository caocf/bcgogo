package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.BrandDTO;
import com.bcgogo.product.dto.NormalBrandDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-1-5
 * Time: 下午3:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "normal_brand")
public class NormalBrand extends LongIdentifier {
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

  public NormalBrandDTO toDTO()
  {
    NormalBrandDTO normalBrandDTO = new NormalBrandDTO();
    normalBrandDTO.setFirstLetter(this.getFirstLetter());
    normalBrandDTO.setId(this.getId());
    normalBrandDTO.setMemo(this.getMemo());
    normalBrandDTO.setName(this.getName());
    normalBrandDTO.setNameEn(this.getNameEn());
    normalBrandDTO.setShopId(this.getShopId());
    normalBrandDTO.setState(this.getState());

    return normalBrandDTO;
  }

  public NormalBrand(){};

  public NormalBrand(BrandDTO brandDTO)
  {
    if(null == brandDTO)
    {
      return;
    }

    this.setFirstLetter(brandDTO.getFirstLetter());
    this.setMemo(brandDTO.getMemo());
    this.setName(brandDTO.getName());
    this.setNameEn(brandDTO.getNameEn());
//    this.setState();
  }
}
