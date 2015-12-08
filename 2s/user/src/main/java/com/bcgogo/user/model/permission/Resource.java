package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.ResourceDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:49
 * 将系统中所有最小粒度的操作（读、写）权限项定义为权限资源，当资源被赋给角色时，角色就拥有了系统操作的某项权限；资源分为若干种：请求url、页面元素、页面等
 */
@Entity
@Table(name = "resource")
public class Resource extends LongIdentifier implements Cacheable {
  private String name;
  private String value;
  private String status;
  private ResourceType type;
  private Long syncTime;
  private String memo;
  private SystemType systemType;


  public ResourceDTO toDTO() {
    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setResourceId(this.getId());
    resourceDTO.setName(this.getName());
    resourceDTO.setStatus(this.getStatus());
    resourceDTO.setSyncTime(this.getSyncTime());
    resourceDTO.setType(this.getType());
    resourceDTO.setValue(this.getValue());
    resourceDTO.setMemo(this.getMemo());
    resourceDTO.setSystemType(this.getSystemType());
    return resourceDTO;
  }

  public void fromDTO(ResourceDTO resourceDTO) {
    this.setId(resourceDTO.getResourceId());
    this.setName(resourceDTO.getName());
    this.setStatus(resourceDTO.getStatus());
    this.setSyncTime(resourceDTO.getSyncTime());
    this.setType(resourceDTO.getType());
    this.setValue(resourceDTO.getValue());
    this.setSystemType(resourceDTO.getSystemType());
    this.setMemo(resourceDTO.getMemo());
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  public ResourceType getType() {
    return type;
  }

  public void setType(ResourceType type) {
    this.type = type;
  }

  @Column(name = "system_type")
  @Enumerated(EnumType.STRING)
  public SystemType getSystemType() {
    return systemType;
  }

  public void setSystemType(SystemType systemType) {
    this.systemType = systemType;
  }

  @Transient
  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  @Override
  public String assembleKey() {
    return MemcachePrefix.resource.getValue() +  String.valueOf(getId());
  }


}
