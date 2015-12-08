package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IAppVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: ZhangJuntao
 * Date: 13-8-31
 * Time: 下午2:27
 */
@Controller
public class AppVersionController {
    private static final Logger LOG = LoggerFactory.getLogger(AppVersionController.class);

    /**
     * 升级检测
     * platform：用户手机系统平台类型 *
     * appVersion：APP版本号   *
     * platformVersion：用户手机系统平台版本
     * mobileModel：用户手机型号
     * userNo:用户账号
     */
    @ResponseBody
    @RequestMapping(value = "/newVersion/platform/{platform}/appVersion/{appVersion}/platformVersion/{platformVersion}/mobileModel/{mobileModel}", method = RequestMethod.GET)
    public Object testUpgrade(@PathVariable("platform") AppPlatform platform,
                              @PathVariable("appVersion") String appVersion,
                              @PathVariable("platformVersion") String platformVersion,
                              @PathVariable("mobileModel") String mobileModel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String appUserNo = null;
            try {
                appUserNo = SessionUtil.getAppUserNo(request, response);
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
                appUserNo = null;
            }
            ApiResponse apiResponse = ServiceManager.getService(IAppVersionService.class)
                    .needAppUpdating(platform, appVersion, platformVersion, mobileModel, appUserNo);

            apiResponse.setDebug(platform + "," + appVersion + "," + platformVersion + "," + mobileModel + "," + appUserNo == null ? "" : appUserNo);

            return apiResponse;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_EXCEPTION);
        }
    }


    /**
     * 升级检测
     * platform：用户手机系统平台类型 *
     * appVersion：APP版本号   *
     * platformVersion：用户手机系统平台版本
     * mobileModel：用户手机型号
     * userNo:用户账号
     */
    @ResponseBody
    @RequestMapping(value = "/bcgogoNewVersion/platform/{platform}/appVersion/{appVersion}/platformVersion/{platformVersion}/mobileModel/{mobileModel}", method = RequestMethod.GET)
    public Object bcgogoNewVersion(@PathVariable("platform") AppPlatform platform,
                                   @PathVariable("appVersion") String appVersion,
                                   @PathVariable("platformVersion") String platformVersion,
                                   @PathVariable("mobileModel") String mobileModel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {


            ApiResponse apiResponse = ServiceManager.getService(IAppVersionService.class)
                    .bcgogoAppNeedAppUpdating(platform, appVersion);
            apiResponse.setDebug(platform + "," + appVersion + "," + platformVersion + "," + mobileModel);
            //temp add by luffy for wince app 1.3.6&1.3.7 upgrade error
            if (platform == AppPlatform.WINCE  && apiResponse.getStatus().equalsIgnoreCase("SUCCESS") && (appVersion.equalsIgnoreCase("1.3.6") || appVersion.equalsIgnoreCase("1.3.7")))
            {
                apiResponse.setStatus("FAIL");
            }
            //end add
            return apiResponse;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return MessageCode.toApiResponse(MessageCode.UPGRADE_TESTING_EXCEPTION);
        }
    }

}
