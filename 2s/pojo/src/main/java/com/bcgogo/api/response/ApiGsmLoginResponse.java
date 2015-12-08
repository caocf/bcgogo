package com.bcgogo.api.response;

import com.bcgogo.api.*;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: lw
 * Date: 14-3-12
 * Time: 下午2:57
 */
public class ApiGsmLoginResponse extends ApiResponse {
  private AppConfig appConfig;
  private Map<String,String> appUserConfig;

  private ShopDTO shopDTO; //店铺信息

  private AppUserDTO appUserDTO;//用户信息

  private AppVehicleDTO appVehicleDTO;

  private AppShopDTO appShopDTO;

  public ApiGsmLoginResponse() {
    super();
  }

  public ApiGsmLoginResponse(ApiResponse response) {
    super(response);
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


  public ShopDTO getShopDTO() {
    return shopDTO;
  }

  public void setShopDTO(ShopDTO shopDTO) {
    this.shopDTO = shopDTO;
  }

  public AppUserDTO getAppUserDTO() {
    return appUserDTO;
  }

  public void setAppUserDTO(AppUserDTO appUserDTO) {
    this.appUserDTO = appUserDTO;
  }

  public AppVehicleDTO getAppVehicleDTO() {
    return appVehicleDTO;
  }

  public void setAppVehicleDTO(AppVehicleDTO appVehicleDTO) {
    this.appVehicleDTO = appVehicleDTO;
  }

  public AppShopDTO getAppShopDTO() {
    return appShopDTO;
  }

  public void setAppShopDTO(AppShopDTO appShopDTO) {
    this.appShopDTO = appShopDTO;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ApiGsmLoginResponse{");
    sb.append(", appConfig=").append(JsonUtil.objectToJson(appConfig));
    sb.append(", appUserConfig=").append(JsonUtil.objectToJson(appUserConfig));
    sb.append(", shopDTO=").append(JsonUtil.objectToJson(shopDTO));
    sb.append(", appUserDTO=").append(JsonUtil.objectToJson(appUserDTO));
    sb.append(", appVehicleDTO=").append(JsonUtil.objectToJson(appVehicleDTO));
    sb.append('}');
    return sb.toString();
  }
}
