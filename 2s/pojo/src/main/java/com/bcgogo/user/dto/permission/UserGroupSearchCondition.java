package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 */
public class UserGroupSearchCondition {
  private int start; //for ext
  private int limit; //for ext
  private String name;
  private String memo;
  private Status status;
  private String variety;  //所有，自定义，系统默认
  private String variety2;  //所有，自定义，系统默认
  private String userGroupNo; //编号
  private Long shopId;
  private Long shopVersionId;

  public String getVariety2() {
    return variety2;
  }

  public void setVariety2(String variety2) {
    this.variety2 = variety2;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getUserGroupNo() {
    return userGroupNo;
  }

  public void setUserGroupNo(String userGroupNo) {
    this.userGroupNo = userGroupNo;
  }

  public String getVariety() {
    return variety;
  }

  public void setVariety(String variety) {
    this.variety = variety;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }
}
