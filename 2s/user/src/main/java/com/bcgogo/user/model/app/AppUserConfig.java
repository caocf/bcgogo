package com.bcgogo.user.model.app;

import com.bcgogo.api.AppUserConfigDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-22
 * Time: 上午10:50
 */
@Entity
@Table(name = "app_user_config")
public class AppUserConfig extends LongIdentifier {
  private String appUserNo;
  private String name;
  private String value;
  private Long syncTime;
  private String description;

  public void fromDTO(AppUserConfigDTO appUserConfigDTO) {
    setAppUserNo(appUserConfigDTO.getAppUserNo());
    setName(appUserConfigDTO.getName());
    setValue(appUserConfigDTO.getValue());
    setSyncTime(appUserConfigDTO.getSyncTime());
    setDescription(appUserConfigDTO.getDescription());
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
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

  @Column(name = "sync_time")
  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


}
