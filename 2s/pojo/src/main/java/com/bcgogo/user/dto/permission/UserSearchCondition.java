package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.user.DepartmentResponsibility;
import com.bcgogo.enums.user.Status;
import com.bcgogo.user.dto.UserDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 * 仅限于CRM
 */
public class UserSearchCondition {
  private boolean hasPager=true;
  private int start;
  private int limit;
  private String username;  //用户名
  private String userNo;  //
  private String departmentName;//部门
  private String occupationName;//职位
  private String roleName;  //分配角色名
  private String name;     //姓名
  private Status status;
  private Long shopId;
  private Long departmentId;
  private DepartmentResponsibility departmentResponsibility;  //职责

  public boolean isHasPager() {
    return hasPager;
  }

  public void setHasPager(boolean hasPager) {
    this.hasPager = hasPager;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getOccupationName() {
    return occupationName;
  }

  public void setOccupationName(String occupationName) {
    this.occupationName = occupationName;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public DepartmentResponsibility getDepartmentResponsibility() {
    return departmentResponsibility;
  }

  public void setDepartmentResponsibility(DepartmentResponsibility departmentResponsibility) {
    this.departmentResponsibility = departmentResponsibility;
  }
}
