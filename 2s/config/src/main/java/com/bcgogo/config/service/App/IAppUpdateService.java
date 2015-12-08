package com.bcgogo.config.service.App;

import com.bcgogo.config.dto.AppUpdateAnnounceDTO;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-20
 * Time: 下午2:40
 */
public interface IAppUpdateService {

  AppUpdateAnnounceDTO getAppUpdateAnnounceDTO(AppPlatform platform, String androidAppVersion,AppUserType appUserType);
}
