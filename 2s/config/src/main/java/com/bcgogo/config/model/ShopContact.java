package com.bcgogo.config.model;

import com.bcgogo.enums.user.ContactType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.ContactDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ������ϵ��
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 13-6-16
 * Time: ����10:34
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "shop_contact")
public class ShopContact extends LongIdentifier{

  private String name; // 联系人姓名
  private String mobile; // 联系人手机号
  private String email;  // 邮箱
  private String qq; //QQ
  private Long shopId;

  private Integer level; // {0,1,2} 分别代表 第一、第二、第三联系人 数字越小 级别越高
  private Integer disabled; // 0 -失效 1-可用
  private Integer isMainContact; // 0 -非主联系人 1-主联系人
  private Integer isShopOwner; // 0 -非店主 1-店主  保持与shop 中 owner同步

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

  @Column(name = "email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "qq")
  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "level")
  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  @Column(name = "disabled")
  public Integer getDisabled() {
    return disabled;
  }

  public void setDisabled(Integer disabled) {
    this.disabled = disabled;
  }

  @Column(name = "is_main_contact")
  public Integer getMainContact() {
    return isMainContact;
  }

  public void setMainContact(Integer mainContact) {
    isMainContact = mainContact;
  }

  @Column(name = "is_shop_owner")
  public Integer getShopOwner() {
    return isShopOwner;
  }

  public void setShopOwner(Integer shopOwner) {
    isShopOwner = shopOwner;
  }

  public ContactDTO toDTO() {
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setDisabled(this.getDisabled());
    contactDTO.setEmail(this.getEmail());
    contactDTO.setId(this.getId());
    contactDTO.setLevel(this.getLevel());
    contactDTO.setIsMainContact(this.getMainContact());
    contactDTO.setMobile(this.getMobile());
    contactDTO.setName(this.getName());
    contactDTO.setQq(this.getQq());
    contactDTO.setShopId(this.getShopId());
    contactDTO.setIsShopOwner(this.getShopOwner());
    contactDTO.setContactType(ContactType.SHOP);
    return contactDTO;
  }

  public ShopContact fromDTO(ContactDTO contactDTO) {
    if (contactDTO != null) {
      this.setId(this.getId());
      this.setName(contactDTO.getName());
      this.setDisabled(contactDTO.getDisabled());
      this.setEmail(contactDTO.getEmail());
      this.setLevel(contactDTO.getLevel());
      this.setMainContact(contactDTO.getIsMainContact());
      this.setMobile(contactDTO.getMobile());
      this.setQq(contactDTO.getQq());
      this.setShopId(contactDTO.getShopId());
      this.setShopOwner(contactDTO.getIsShopOwner());
    }
    return this;
  }

}
