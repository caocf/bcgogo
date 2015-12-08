package com.bcgogo.config.service.App;

import com.bcgogo.config.dto.AppUpdateAnnounceDTO;
import com.bcgogo.config.model.AppUpdateAnnounce;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigReader;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-20
 * Time: 下午2:40
 */
@Component
public class AppUpdateService implements IAppUpdateService {

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public AppUpdateAnnounceDTO getAppUpdateAnnounceDTO(AppPlatform platform, String appVersion,AppUserType appUserType) {
    if(platform != null && StringUtils.isNotEmpty(appVersion)){
      ConfigWriter writer = configDaoManager.getWriter();
      AppUpdateAnnounce appUpdateAnnounce = writer.getAppUpdateAnnounce(platform,appVersion,appUserType);
      if(appUpdateAnnounce != null){
        return  appUpdateAnnounce.toDTO();
      }

    }
    return null;
  }
}
