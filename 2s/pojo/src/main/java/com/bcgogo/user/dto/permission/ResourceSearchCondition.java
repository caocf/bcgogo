package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.user.dto.UserDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 * 仅限于CRM
 */
public class ResourceSearchCondition {
  private int start;
  private int limit;
  private String name;      //资源名
  private String value;     //资源value
  private String memo;
  private String type;      //类型
  private String roleName;  //角色名
  private SystemType systemType;


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

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public SystemType getSystemType() {
    return systemType;
  }

  public void setSystemType(SystemType systemType) {
    this.systemType = systemType;
  }
}
