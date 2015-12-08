package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: XJ
 * Date: 9/28/11
 * Time: 5:08 PM
 */
public class UserGroupDTO implements Serializable {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long shopVersionId;
  private String name;
  private String memo;
  private String value;
  private String label; //下拉建议使用
  private String statusValue;
  private Status status;
  private String variety; //所有，自定义，系统默认  （SYSTEM_DEFAULT，CUSTOM） for shop
  private String userGroupNo;

  public UserGroupDTO() {
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    if (id != null) {
      this.setIdStr(id.toString());
    }
    this.id = id;
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
    this.setLabel(name);
    this.name = name;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    if (status != null) {
      this.setStatusValue(status.getValue());
    }
    this.status = status;
  }

  public String getStatusValue() {
    return statusValue;
  }

  public void setStatusValue(String statusValue) {
    this.statusValue = statusValue;
  }

  public String getVariety() {
    return variety;
  }

  public void setVariety(String variety) {
    this.variety = variety;
  }

  public String getUserGroupNo() {
    return userGroupNo;
  }

  public void setUserGroupNo(String userGroupNo) {
    this.userGroupNo = userGroupNo;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }
}
