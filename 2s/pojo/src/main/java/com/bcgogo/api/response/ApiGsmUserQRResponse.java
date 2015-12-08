package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.wx.user.AppWXUserDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-28
 * Time: 16:32
 */
public class ApiGsmUserQRResponse extends ApiResponse {
  private String url;
  private Long expireTime;
  private String publicNo;
  private List<AppWXUserDTO> appWXUserDTOs;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public List<AppWXUserDTO> getAppWXUserDTOs() {
    return appWXUserDTOs;
  }

  public void setAppWXUserDTOs(List<AppWXUserDTO> appWXUserDTOs) {
    this.appWXUserDTOs = appWXUserDTOs;
  }
}
