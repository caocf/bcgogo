package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-24
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class ContactGroupDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private String name;
  private ContactGroupType contactGroupType;
  private List<ContactDTO> contactDTOs;
  private int totalNum;
  private boolean appCustomerFlag=false;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.setIdStr(StringUtil.valueOf(id));
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

  public ContactGroupType getContactGroupType() {
    return contactGroupType;
  }

  public void setContactGroupType(ContactGroupType contactGroupType) {
    this.contactGroupType = contactGroupType;
  }

  public List<ContactDTO> getContactDTOs() {
    return contactDTOs;
  }

  public void setContactDTOs(List<ContactDTO> contactDTOs) {
    this.contactDTOs = contactDTOs;
  }

  public int getTotalNum() {
    return totalNum;
  }

  public void setTotalNum(int totalNum) {
    this.totalNum = totalNum;
  }

  public boolean isAppCustomerFlag() {
    return appCustomerFlag;
  }

  public void setAppCustomerFlag(boolean appCustomerFlag) {
    this.appCustomerFlag = appCustomerFlag;
  }
}
