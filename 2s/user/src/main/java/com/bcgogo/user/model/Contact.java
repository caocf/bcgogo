package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SalesReturnDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-6-4
 * Time: 下午1:16
 * 联系人 客户、供应商、店面 最多3个
 */
@Entity
@Table(name = "contact")
public class Contact extends LongIdentifier {

  private String name; // 联系人姓名
  private String mobile; // 联系人手机号
  private String email;  // 邮箱
  private String qq; //QQ
  private Long customerId;
  private Long supplierId;
  private Long shopId;

  private Integer level; // {0,1,2} 分别代表 第一、第二、第三联系人 数字越小 级别越高
  private Integer disabled; // 0 -失效 1-可用
  private Integer isMainContact; // 0 -非主联系人 1-主联系人
  private Integer isShopOwner; // 0 -非店主 1-店主

  public Contact(){}
  public Contact(ContactDTO contactDTO){
    if(contactDTO==null) return;
    this.shopId=contactDTO.getShopId();
    this.mobile=contactDTO.getMobile();
    this.name=contactDTO.getName();
    this.customerId=contactDTO.getCustomerId();
    this.supplierId=contactDTO.getSupplierId();
    this.isMainContact=contactDTO.getMainContact();
    this.disabled=contactDTO.getDisabled();
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

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
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
  public Integer getIsMainContact() {
    return isMainContact;
  }

  public void setIsMainContact(Integer isMainContact) {
    this.isMainContact = isMainContact;
  }

  @Column(name = "is_shop_owner")
  public Integer getShopOwner() {
    return isShopOwner;
  }

  public ContactDTO toDTO() {
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setCustomerId(this.getCustomerId());
    contactDTO.setDisabled(NumberUtil.intValue(this.getDisabled()));
    contactDTO.setEmail(this.getEmail());
    contactDTO.setId(this.getId());
    contactDTO.setIdStr(this.getId() != null ? String.valueOf(getId()) : "");
    contactDTO.setLevel(NumberUtil.intValue(this.getLevel()));
    contactDTO.setIsMainContact(NumberUtil.intValue(this.getIsMainContact()));
    contactDTO.setMobile(this.getMobile());
    contactDTO.setName(this.getName());
    contactDTO.setQq(this.getQq());
    contactDTO.setShopId(this.getShopId());
    contactDTO.setIsShopOwner(NumberUtil.intValue(this.getShopOwner()));
    contactDTO.setSupplierId(this.getSupplierId());
    contactDTO.setCustomerId(this.getCustomerId());
    return contactDTO;
  }

  public void setShopOwner(Integer shopOwner) {
    isShopOwner = shopOwner;
  }

  public Contact fromDTO(ContactDTO contactDTO) {
    if (contactDTO != null) {
      if (StringUtils.isNotBlank(contactDTO.getIdStr())) {
        this.setId(Long.parseLong(contactDTO.getIdStr()));
      } else {
        this.setId(this.getId());
      }
      this.setName(contactDTO.getName());
      this.setEmail(contactDTO.getEmail());
      this.setMobile(contactDTO.getMobile());
      this.setQq(contactDTO.getQq());
      if (contactDTO.getCustomerId() != null && contactDTO.getCustomerId() != 0) {
        this.setCustomerId(contactDTO.getCustomerId());
      }
      if (contactDTO.getDisabled() != null) {
        this.setDisabled(contactDTO.getDisabled());
      }
      if (contactDTO.getLevel() != null) {
        this.setLevel(contactDTO.getLevel());
      }
      if (contactDTO.getIsMainContact() != null) {
        this.setIsMainContact(contactDTO.getIsMainContact());
      }
      if (contactDTO.getShopId() != null) {
        this.setShopId(contactDTO.getShopId());
      }
      if (contactDTO.getIsShopOwner() != null) {
        this.setShopOwner(contactDTO.getIsShopOwner());
      }
      if (contactDTO.getSupplierId() != null && contactDTO.getSupplierId() != 0) {
        this.setSupplierId(contactDTO.getSupplierId());
      }
    }
    return this;
  }


  public void fromSaleReturnDTO(SalesReturnDTO salesReturnDTO) {
    if (salesReturnDTO != null) {
      this.setDisabled(1);
      this.setIsMainContact(1);
      this.setCustomerId(salesReturnDTO.getCustomerId());
      this.setLevel(0);
      this.setShopId(salesReturnDTO.getShopId());
      this.setMobile(salesReturnDTO.getMobile());
      this.setName(salesReturnDTO.getCustomer());
    }
  }
}
