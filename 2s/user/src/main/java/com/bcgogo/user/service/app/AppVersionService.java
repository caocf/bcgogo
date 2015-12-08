package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.response.ApiUpgradeTestingResponse;
import com.bcgogo.config.dto.AppUpdateAnnounceDTO;
import com.bcgogo.config.service.App.IAppUpdateService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 手机端接口处理service
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-22
 * Time: 上午9:51
 */
@Component
public class AppVersionService implements IAppVersionService {

  @Autowired
  private UserDaoManager userDaoManager;
  private static final Logger LOG = LoggerFactory.getLogger(AppVersionService.class);

  @Override
  public ApiResponse needAppUpdating(AppPlatform platform, String appVersion, String platformVersion, String mobileModel,String userNo) {
    if (platform == null || platform == AppPlatform.NULL) {
      return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_FAIL, ValidateMsg.APP_PLATFORM_EMPTY);
    } else if (StringUtil.isEmptyAppGetParameter(appVersion)) {
      return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_FAIL, ValidateMsg.APP_VERSION_EMPTY);
    }else if(StringUtil.isEmptyAppGetParameter(userNo)){
      return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_FAIL, ValidateMsg.APP_USER_NOT_EXIST);
    }

    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(userNo,null);
    AppUserType appUserType = appUserDTO.getAppUserType();
    if (appUserType == null) {
      return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_FAIL, ValidateMsg.APP_USER_NOT_EXIST);
    }

    IAppUpdateService appUpdateService = ServiceManager.getService(IAppUpdateService.class);
    ApiUpgradeTestingResponse apiUpgradeTestingResponse = new ApiUpgradeTestingResponse(MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_SUCCESS));
    apiUpgradeTestingResponse.setNormalAction();
    switch (platform) {
      case ANDROID:
        String androidAppVersion = (appUserType == AppUserType.BLUE_TOOTH ? ConfigUtils.getAndroidAppVersion() : ConfigUtils.getGsmAndroidAppVersion());
        if (StringUtil.compareAppVersion(appVersion, androidAppVersion)) {
          apiUpgradeTestingResponse.setUrl(appUserType == AppUserType.BLUE_TOOTH ? ConfigUtils.getAndroidAppUpgradeURL() : ConfigUtils.getGsmAndroidAppUpgradeURL());
          AppUpdateAnnounceDTO appUpdateAnnounceDTO = appUpdateService.getAppUpdateAnnounceDTO(platform, androidAppVersion, appUserType);
          if (appUpdateAnnounceDTO != null) {
            apiUpgradeTestingResponse.setAppUpdateAnnounceDTO(appUpdateAnnounceDTO);
            apiUpgradeTestingResponse.setMessage(appUpdateAnnounceDTO.getDescription());
          } else {
            apiUpgradeTestingResponse.setAlertAction();
            apiUpgradeTestingResponse.setMessage(ValidateMsg.VERSION_UPGRADE.getValue());
          }
        }
        break;
      case IOS:
        String iosAppVersion = (appUserType == AppUserType.BLUE_TOOTH ? ConfigUtils.getIOSAppVersion() : ConfigUtils.getGsmIOSAppVersion());
        if (StringUtil.compareAppVersion(appVersion, iosAppVersion)) {
          apiUpgradeTestingResponse.setAlertAction();
          apiUpgradeTestingResponse.setUrl(appUserType == AppUserType.BLUE_TOOTH ? ConfigUtils.getISOAppUpgradeURL() : ConfigUtils.getGsmISOAppUpgradeURL());
          AppUpdateAnnounceDTO appUpdateAnnounceDTO = appUpdateService.getAppUpdateAnnounceDTO(platform, iosAppVersion, appUserType);
          if(appUpdateAnnounceDTO != null){
            apiUpgradeTestingResponse.setAppUpdateAnnounceDTO(appUpdateAnnounceDTO);
            apiUpgradeTestingResponse.setMessage(appUpdateAnnounceDTO.getDescription());
          }else{
            apiUpgradeTestingResponse.setAlertAction();
            apiUpgradeTestingResponse.setMessage(ValidateMsg.VERSION_UPGRADE.getValue());
          }

        }
        break;
    }
    return apiUpgradeTestingResponse;
  }


  @Override
  public ApiResponse bcgogoAppNeedAppUpdating(AppPlatform platform, String appVersion) {
    if (platform == null || platform == AppPlatform.NULL) {
      return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_FAIL, ValidateMsg.APP_PLATFORM_EMPTY);
    } else if (StringUtil.isEmptyAppGetParameter(appVersion)) {
      return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_FAIL, ValidateMsg.APP_VERSION_EMPTY);
    }


    IAppUpdateService appUpdateService = ServiceManager.getService(IAppUpdateService.class);
    ApiUpgradeTestingResponse apiUpgradeTestingResponse = new ApiUpgradeTestingResponse(MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_SUCCESS));
    apiUpgradeTestingResponse.setNormalAction();
    AppUserType appUserType = AppUserType.BCGOGO_SHOP_OWNER;
    switch (platform) {
      case OBD:
        String obdVersion = ConfigUtils.getBcgogoOBDVersion();
        if (appVersion.compareTo(obdVersion)>0) {
          apiUpgradeTestingResponse.setUrl(ConfigUtils.getBcgogoOBDUpgradeURL());
          appUserType = AppUserType.OBD;
          AppUpdateAnnounceDTO appUpdateAnnounceDTO = appUpdateService.getAppUpdateAnnounceDTO(platform, obdVersion, appUserType);
          if (appUpdateAnnounceDTO != null) {
            apiUpgradeTestingResponse.setAppUpdateAnnounceDTO(appUpdateAnnounceDTO);
            apiUpgradeTestingResponse.setMessage(appUpdateAnnounceDTO.getDescription());
          } else {
            apiUpgradeTestingResponse.setAlertAction();
            apiUpgradeTestingResponse.setMessage(ValidateMsg.OBD_VERSION_UPGRADE.getValue());
          }
        }
        break;
      case WINCE:
        String winCEAppVersion = ConfigUtils.getBcgogoWinCEAppVersion();
        if (StringUtil.compareAppVersion(appVersion, winCEAppVersion)) {
          apiUpgradeTestingResponse.setUrl(ConfigUtils.getBcgogoWinCEUpgradeURL());
          appUserType = AppUserType.MIRROR;
          AppUpdateAnnounceDTO appUpdateAnnounceDTO = appUpdateService.getAppUpdateAnnounceDTO(platform, winCEAppVersion, appUserType);
          if (appUpdateAnnounceDTO != null) {
            apiUpgradeTestingResponse.setAppUpdateAnnounceDTO(appUpdateAnnounceDTO);
            apiUpgradeTestingResponse.setMessage(appUpdateAnnounceDTO.getDescription());
          } else {
            apiUpgradeTestingResponse.setUrl(null);
            apiUpgradeTestingResponse.setAlertAction();
            apiUpgradeTestingResponse.setMessage(ValidateMsg.MIRROR_VERSION_UPGRADE.getValue());
          }
        }
        break;
      case ANDROID:
        String androidAppVersion = ConfigUtils.getBcgogoAndroidAppVersion();
        if (StringUtil.compareAppVersion(appVersion, androidAppVersion)) {
          apiUpgradeTestingResponse.setUrl(ConfigUtils.getBcgogoAndroidAppUpgradeURL());
          AppUpdateAnnounceDTO appUpdateAnnounceDTO = appUpdateService.getAppUpdateAnnounceDTO(platform, androidAppVersion, appUserType);
          if (appUpdateAnnounceDTO != null) {
            apiUpgradeTestingResponse.setAppUpdateAnnounceDTO(appUpdateAnnounceDTO);
            apiUpgradeTestingResponse.setMessage(appUpdateAnnounceDTO.getDescription());
          } else {
            apiUpgradeTestingResponse.setAlertAction();
            apiUpgradeTestingResponse.setMessage(ValidateMsg.VERSION_UPGRADE.getValue());
          }
        }
        break;
      case IOS:
        String iosAppVersion = ConfigUtils.getBcgogoIOSAppVersion();
        if (StringUtil.compareAppVersion(appVersion, iosAppVersion)) {
          apiUpgradeTestingResponse.setAlertAction();
          apiUpgradeTestingResponse.setUrl(ConfigUtils.getBcgogoISOAppUpgradeURL());
          AppUpdateAnnounceDTO appUpdateAnnounceDTO = appUpdateService.getAppUpdateAnnounceDTO(platform, iosAppVersion, appUserType);
          if (appUpdateAnnounceDTO != null) {
            apiUpgradeTestingResponse.setAppUpdateAnnounceDTO(appUpdateAnnounceDTO);
            apiUpgradeTestingResponse.setMessage(appUpdateAnnounceDTO.getDescription());
          } else {
            apiUpgradeTestingResponse.setAlertAction();
            apiUpgradeTestingResponse.setMessage(ValidateMsg.VERSION_UPGRADE.getValue());
          }

        }
        break;

    }
    return apiUpgradeTestingResponse;
  }

}
