package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DictionaryDTO;
import com.bcgogo.enums.app.AppPlatform;

/**
 * 手机端接口处理service
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-22
 * Time: 上午9:51
 */
public interface IAppVersionService {

  ApiResponse needAppUpdating(AppPlatform platform, String appVersion, String platformVersion, String mobileModel,String userNo);

  ApiResponse bcgogoAppNeedAppUpdating(AppPlatform platform, String appVersion);

}
