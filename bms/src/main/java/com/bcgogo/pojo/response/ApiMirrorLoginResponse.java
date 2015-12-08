package com.bcgogo.pojo.response;

import com.bcgogo.pojo.AppUserDTO;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-19
 * Time: 下午5:28
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
