package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ModelDTO;
import com.bcgogo.product.dto.NormalModelDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-1-5
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "normal_model")
public class NormalModel extends LongIdentifier {
  private Long normalBrandId;
  private Long mfrId;
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

  @Column(name="normal_brand_id")
  public Long getNormalBrandId() {
    return normalBrandId;
  }

  public void setNormalBrandId(Long normalBrandId) {
    this.normalBrandId = normalBrandId;
  }

  @Column(name = "mfr_id")
  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
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

  public NormalModelDTO toDTO()
  {
    NormalModelDTO normalModelDTO = new NormalModelDTO();

    normalModelDTO.setFirstLetter(this.getFirstLetter());
    normalModelDTO.setId(this.getId());
    normalModelDTO.setMemo(this.getMemo());
    normalModelDTO.setMfrId(this.getMfrId());
    normalModelDTO.setName(this.getName());
    normalModelDTO.setNameEn(this.getNameEn());
    normalModelDTO.setNormalBrandId(this.getNormalBrandId());
    normalModelDTO.setShopId(this.getShopId());
    normalModelDTO.setState(this.getState());

    return normalModelDTO;
  }

  public NormalModel(){};

  public NormalModel(ModelDTO modelDTO)
  {
    if(null == modelDTO)
    {
      return;
    }

    this.setFirstLetter(modelDTO.getFirstLetter());
    this.setMemo(modelDTO.getMemo());
    this.setName(modelDTO.getName());
    this.setNameEn(modelDTO.getNameEn());
  }
}
