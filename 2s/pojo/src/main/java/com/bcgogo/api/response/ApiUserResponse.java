package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机端用户获取个人资料返回封装
 * User: lw
 * Date: 13-8-19
 * Time: 下午2:57
 */
public class ApiUserResponse extends ApiResponse {
  private AppUserDTO userInfo;

  public ApiUserResponse() {
    super();
  }

  public ApiUserResponse(ApiResponse response) {
    super(response);
  }

  public AppUserDTO getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(AppUserDTO userInfo) {
    this.userInfo = userInfo;
  }

  @Override
  public String toString() {
    return "ApiLoginResponse{" +
        "userInfo=" + userInfo == null ? "" : userInfo.toString() +
        '}';
  }
}
