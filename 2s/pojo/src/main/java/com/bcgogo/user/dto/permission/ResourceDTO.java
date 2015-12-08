package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.ResourceType;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-14
 * Time: 上午9:21
 */
public class ResourceDTO implements Serializable {
  public final static String NAME_DUPLICATED = "resource name is duplicated.";
  public final static String VALUE_DUPLICATED = "resource value is duplicated.";
  private Long resourceId;
  private String name;
  private String value;
  private String status;
  private ResourceType type;
  private Long syncTime;
  private String memo;
  private SystemType systemType;

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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public ResourceType getType() {
    return type;
  }

  public void setType(ResourceType type) {
    this.type = type;
  }

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  public Long getResourceId() {
    return resourceId;
  }

  public void setResourceId(Long resourceId) {
    this.resourceId = resourceId;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public SystemType getSystemType() {
    return systemType;
  }

  public void setSystemType(SystemType systemType) {
    this.systemType = systemType;
  }

  @Override
  public String toString() {
    return "ResourceDTO{" +
        "resourceId=" + resourceId +
        ", name='" + name + '\'' +
        ", value='" + value + '\'' +
        ", status='" + status + '\'' +
        ", type=" + type +
        ", syncTime=" + syncTime +
        ", memo='" + memo + '\'' +
        ", systemType=" + systemType +
        '}';
  }
}
