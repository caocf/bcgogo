package com.bcgogo.api;

import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.ImageVersion;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: ZhangJuntao
 * Date: 13-12-24
 * Time: 下午1:42
 */
public class AppGuestLoginInfo extends AppMobileInfo {
  private static final Logger LOG = LoggerFactory.getLogger(LoginDTO.class);

  private ImageVersion imageVersionEnum; //                   *

  public String validate() {
    if (StringUtil.isEmpty(appVersion)) {
      return ValidateMsg.APP_VERSION_EMPTY.getValue();
    }
    this.setImageVersionEnum(ImageVersion.getImageVersion(imageVersion));
    if (imageVersionEnum == null) {
      LOG.error("手机端未传分辨率服默认分辨率:[{}]", this.toString());
      this.setImageVersionEnum(ImageVersion.IV_320X480);
    }
    return "";
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public ImageVersion getImageVersionEnum() {
    return imageVersionEnum;
  }

  public void setImageVersionEnum(ImageVersion imageVersionEnum) {
    this.imageVersionEnum = imageVersionEnum;
  }
}
