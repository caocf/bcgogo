package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserDTO;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-27
 * Time: 17:57
 */
public class ApiMirrorLoginResponse extends ApiResponse {
  private AppUserDTO appUserDTO;//用户信息
  private Long synTimestamp;

  public ApiMirrorLoginResponse() {
    super();
  }

  public ApiMirrorLoginResponse(ApiResponse response) {
    super(response);
  }

  public Long getSynTimestamp() {
    return synTimestamp;
  }

  public void setSynTimestamp(Long synTimestamp) {
    this.synTimestamp = synTimestamp;
  }

  public AppUserDTO getAppUserDTO() {
    return appUserDTO;
  }

  public void setAppUserDTO(AppUserDTO appUserDTO) {
    this.appUserDTO = appUserDTO;
  }

}
