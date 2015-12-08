package com.bcgogo.api;

import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ImageVersion;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午2:47
 */
public class LoginDTO extends AppMobileInfo {
  private static final Logger LOG = LoggerFactory.getLogger(LoginDTO.class);
  private String userNo;    //用户账号                         *
  private String password;   //用户密码                        *
  private ImageVersion imageVersionEnum; //                   *
  private String sessionId;
  private String deviceToken;//ios 用户的deviceToken
  private String umDeviceToken;//安卓 用户的友盟deviceToken
  private String imei;
  private AppUserType appUserType;//用户类型

  public LoginDTO(AppPlatform platform, String appVersion, String platformVersion, String imageVersion) {
    this.platform = platform;
    this.appVersion = appVersion;
    this.platformVersion = platformVersion;
    this.imageVersion = imageVersion;
  }

  public LoginDTO() {
    super();
  }

  public String computeMD5() {
    setPassword(EncryptionUtil.computeMD5Improved(getPassword()));
    return getPassword();
  }

  public String validate() {
    if (StringUtil.isEmpty(userNo)) {
      return ValidateMsg.APP_USER_NO_EMPTY.getValue();
    }
    if (StringUtil.isEmpty(password)) {
      return ValidateMsg.PASSWORD_EMPTY.getValue();
    } else if (password.length() > 20) {
      return ValidateMsg.PASSWORD_TOO_LONG.getValue();
    }
    if (StringUtil.isEmpty(appVersion)) {
      return ValidateMsg.APP_VERSION_EMPTY.getValue();
    }
    if (this.getAppUserType() != AppUserType.BCGOGO_SHOP_OWNER) {
      this.setImageVersionEnum(ImageVersion.getImageVersion(imageVersion));
      if (imageVersionEnum == null) {
        LOG.warn("手机端未传分辨率服默认分辨率:[{}]", this.toString());
        this.setImageVersionEnum(ImageVersion.IV_320X480);
      }
    }
    return "";
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }


  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public ImageVersion getImageVersionEnum() {
    return imageVersionEnum;
  }

  public void setImageVersionEnum(ImageVersion imageVersionEnum) {
    this.imageVersionEnum = imageVersionEnum;
  }

  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  public String getUmDeviceToken() {
    return umDeviceToken;
  }

  public void setUmDeviceToken(String umDeviceToken) {
    this.umDeviceToken = umDeviceToken;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }

  @Override
  public String toString() {
    return "LoginDTO{" +
        "userNo='" + userNo + '\'' +
        ", password='" + password + '\'' +
        ", platform=" + platform +
        ", appVersion='" + appVersion + '\'' +
        ", platformVersion='" + platformVersion + '\'' +
        ", mobileModel='" + mobileModel + '\'' +
        ", imageVersion='" + imageVersion + '\'' +
        ", sessionId='" + sessionId + '\'' +
        '}';
  }

}
