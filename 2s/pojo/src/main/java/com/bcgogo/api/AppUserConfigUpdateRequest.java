package com.bcgogo.api;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-12
 * Time: 上午9:39
 */
public class AppUserConfigUpdateRequest {
  private String appUserNo;
  private AppUserConfigDTO[] appUserConfigDTOs;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public AppUserConfigDTO[] getAppUserConfigDTOs() {
    return appUserConfigDTOs;
  }

  public void setAppUserConfigDTOs(AppUserConfigDTO[] appUserConfigDTOs) {
    this.appUserConfigDTOs = appUserConfigDTOs;
  }
}
