package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppConfig;
import com.bcgogo.api.ObdInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午2:57
 */
public class ApiLoginResponse extends ApiResponse {
  private List<ObdInfo> obdList = new ArrayList<ObdInfo>();
  private AppConfig appConfig;
  private Map<String,String> appUserConfig;
  private Map<String,String> privilegeMap = new HashMap<String, String>();

  public ApiLoginResponse() {
    super();
  }

  public ApiLoginResponse(ApiResponse response) {
    super(response);
  }

  public List<ObdInfo> getObdList() {
    return obdList;
  }

  public void setObdList(List<ObdInfo> obdList) {
    this.obdList = obdList;
  }

  public AppConfig getAppConfig() {
    return appConfig;
  }

  public void setAppConfig(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  public Map<String, String> getAppUserConfig() {
    return appUserConfig;
  }

  public void setAppUserConfig(Map<String, String> appUserConfig) {
    this.appUserConfig = appUserConfig;
  }

  public Map<String, String> getPrivilegeMap() {
    return privilegeMap;
  }

  public void setPrivilegeMap(Map<String, String> privilegeMap) {
    this.privilegeMap = privilegeMap;
  }

  @Override
  public String toString() {
    return "ApiLoginResponse{" +
        "obdList=" + obdList +
        ", appConfig=" + appConfig +
        ", appUserConfig=" + appUserConfig +
        ", privilegeMap=" + privilegeMap +
        '}';
  }
}
