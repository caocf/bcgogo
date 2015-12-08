package com.bcgogo.user.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.AccidentSpecialistDTO;

import javax.persistence.*;

/**
 * 事故专员
 * Author: ndong
 * Date: 2015-5-7
 * Time: 13:00
 */
@Entity
@Table(name = "accident_specialist")
public class AccidentSpecialist extends LongIdentifier {
  private String name;
  private String mobile;
  private Long shopId;
  private String openId;
  private DeletedType deleted = DeletedType.FALSE;


  public void fromDTO(AccidentSpecialistDTO specialistDTO) {
    if (specialistDTO == null) return;
    this.setId(specialistDTO.getId());
    this.setName(specialistDTO.getName());
    this.setMobile(specialistDTO.getMobile());
    this.setShopId(specialistDTO.getShopId());
    this.setOpenId(specialistDTO.getOpenId());
    this.setDeleted(specialistDTO.getDeleted());
  }

  public AccidentSpecialistDTO toDTO() {
    AccidentSpecialistDTO specialistDTO = new AccidentSpecialistDTO();
    specialistDTO.setId(this.getId());
    specialistDTO.setName(this.getName());
    specialistDTO.setMobile(this.getMobile());
    specialistDTO.setShopId(this.getShopId());
    specialistDTO.setOpenId(this.getOpenId());
    specialistDTO.setDeleted(this.getDeleted());
    return specialistDTO;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
