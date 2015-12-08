package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.Sex;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.user.Status;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 */
public class StaffSearchCondition {
  private int start;
  private int limit;
  private int startPageNo;
  private String departmentName;//部门
  private String userGroupName;//职位
  private Long userGroupId;//职位
  private String name;     //姓名
  private Long shopId;
  private Sex sex;
  private SalesManStatus status;
  private Status userStatus;
  private String sortStr;

  private Set<Long> salesManIdSet = new HashSet<Long>();

  public String getSortStr() {
    return sortStr;
  }

  public void setSortStr(String sortStr) {
    this.sortStr = sortStr;
  }

  public Status getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(Status userStatus) {
    this.userStatus = userStatus;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getUserGroupName() {
    return userGroupName;
  }

  public void setUserGroupName(String userGroupName) {
    this.userGroupName = userGroupName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }

  public SalesManStatus getStatus() {
    return status;
  }

  public void setStatus(SalesManStatus status) {
    this.status = status;
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

  public Set<Long> getSalesManIdSet() {
    return salesManIdSet;
  }

  public void setSalesManIdSet(Set<Long> salesManIdSet) {
    this.salesManIdSet = salesManIdSet;
  }
}
