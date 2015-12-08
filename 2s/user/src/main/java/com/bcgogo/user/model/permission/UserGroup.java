package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.UserGroupDTO;

import javax.persistence.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:52
 */

@Entity
@Table(name = "user_group")
public class UserGroup extends LongIdentifier {
  private String name;
  private Status status;
  private String memo;
  private String value;
  private String variety;  //所有，自定义，系统默认  （SYSTEM_DEFAULT，CUSTOM） for shop
  private String userGroupNo;

  public UserGroup() {
  }

  public UserGroup(UserGroupDTO userGroupDTO) {
    this.setId(userGroupDTO.getId());
    this.setName(userGroupDTO.getName());
    this.setMemo(userGroupDTO.getMemo());
    this.setValue(userGroupDTO.getValue());
    this.setStatus(userGroupDTO.getStatus());
    this.setVariety(userGroupDTO.getVariety());
    this.setUserGroupNo(userGroupDTO.getUserGroupNo());
  }

  public UserGroup fromDTO(UserGroupDTO userGroupDTO) {
    this.setId(userGroupDTO.getId());
    this.setName(userGroupDTO.getName());
    this.setStatus(userGroupDTO.getStatus());
    this.setValue(userGroupDTO.getValue());
    this.setMemo(userGroupDTO.getMemo());
    this.setVariety(userGroupDTO.getVariety());
    this.setUserGroupNo(userGroupDTO.getUserGroupNo());
    return this;
  }

  public UserGroupDTO toDTO() {
    UserGroupDTO userGroupDTO = new UserGroupDTO();
    userGroupDTO.setId(this.getId());
    userGroupDTO.setName(this.getName());
    userGroupDTO.setStatus(this.getStatus());
    userGroupDTO.setValue(this.getValue());
    userGroupDTO.setMemo(this.getMemo());
    userGroupDTO.setVariety(this.getVariety());
    userGroupDTO.setUserGroupNo(this.getUserGroupNo());
    return userGroupDTO;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "variety")
  public String getVariety() {
    return variety;
  }

  public void setVariety(String variety) {
    this.variety = variety;
  }

  @Column(name = "user_group_no")
  public String getUserGroupNo() {
    return userGroupNo;
  }

  public void setUserGroupNo(String userGroupNo) {
    this.userGroupNo = userGroupNo;
  }
}
