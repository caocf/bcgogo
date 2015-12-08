package com.bcgogo.wx;

import com.bcgogo.PageErrorMsg;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.api.response.AppGsmVehicleResponse;
import com.bcgogo.api.response.OneKeyRescueResponse;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.Constant;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.product.service.app.IAppDictionaryService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IAppVehicleService;
import com.bcgogo.user.dto.InsuranceCompanyDTO;
import com.bcgogo.user.service.IImpactService;
import com.bcgogo.user.service.IRescueService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.vEvaluate.Car360EvaluateService;
import com.bcgogo.user.service.vEvaluate.IVehicleEvaluateService;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.wx.user.AppWXUserDTO;
import com.bcgogo.wx.user.OneKeyRescueDTO;
import com.bcgogo.wx.user.ShopWXUserDTO;
import com.bcgogo.wx.user.WXUserVehicleDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 需要跳转授权的页面
 * Author: ndong
 * Date: 14-10-29
 * Time: 下午6:15
 */
@Controller
@RequestMapping("/oAuth")
public class WXOAuthController {
  private static final Logger LOG = LoggerFactory.getLogger(WXOAuthController.class);
  @Autowired
  private IWXUserService wxUserService;
  @Autowired
  private IConfigService configService;

  //车辆绑定
  private static final String PAGE_VEHICLE_BIND = "/wx/v_bind";
  //车辆评估
  private static final String PAGE_VEHICLE_EVALUATE = "/wx/v_evaluate";
  //在线预约(之前的)
  private static final String PAGE_V_APPOINT = "/wx/wx_appoint";
  //车险计算器
  private static final String PAGE_V_INSURANCE = "/wx/v_insurance";

  /**
   * 车辆绑定
   *
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/2Bind", method = RequestMethod.GET)
  public String toVehicleBind(ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:2Bind publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getOAuthAccessToken(state, code);
      if (authAccessToken == null) {
        LOG.error("wx:2Bind oauth accessToken failed");
        modelMap.put("errorMsg", "网络异常，请刷新页面后继续操作。");
        return PAGE_VEHICLE_BIND;
      }
      LOG.info("wx:2Bind oauth success ,openId is {}", authAccessToken.getOpenid());
      modelMap.put("openId", authAccessToken.getOpenid());
      modelMap.put("p_type", "BIND");
      return PAGE_VEHICLE_BIND;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PAGE_VEHICLE_BIND;
    }
  }

  /**
   * 车辆评估
   *
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/2Evaluate", method = RequestMethod.GET)
  public String toVehicleEvaluate(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    LOG.info("wx:2Evaluate publicNo is {},code is {}", state, code);
    try {
      OAuthAccessToken authAccessToken = WXHelper.getOAuthAccessToken(state, code);
      List<WXUserVehicleDTO> userVehicleDTOs = wxUserService.getWXUserVehicleByOpenId(authAccessToken.getOpenid());
      modelMap.put("openId", authAccessToken.getOpenid());
      if (CollectionUtil.isNotEmpty(userVehicleDTOs)) {
        modelMap.put("vehicleNo", CollectionUtil.getFirst(userVehicleDTOs).getVehicleNo());
//      modelMap.put("userVehicleDTOs",userVehicleDTOs);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    try {
      IVehicleEvaluateService evaluateService = ServiceManager.getService(Car360EvaluateService.class);
      modelMap.put("provAreaDTOs", evaluateService.getAreaDTOByNo(null));
      modelMap.put("brandDTOs", evaluateService.getVehicleBrandDTOs());
      String UserAgent = request.getHeader("User-Agent");
      modelMap.put("iPhone", UserAgentUtil.iPhone(UserAgent));
      return PAGE_VEHICLE_EVALUATE;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return "error";
    }

  }

  /**
   * 在线预约(之前的)
   *
   * @param request
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/2Appoint", method = RequestMethod.GET)
  public String toAppoint(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:2Appoint publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getOAuthAccessToken(state, code);
      if (authAccessToken == null) {
        LOG.error("wx:2Appoint oauth accessToken failed");
        modelMap.put("errorMsg", "网络异常，请刷新页面后继续操作。");
        return PAGE_V_APPOINT;
      }
      String openId = authAccessToken.getOpenid();
      modelMap.put("openId", openId);
      List<ShopWXUserDTO> shopWXUserDTOs = wxUserService.getShopWXUserByOpenId(openId);
      if (CollectionUtil.isNotEmpty(shopWXUserDTOs)) {
        List<Long> shopIds = new ArrayList<Long>();
        for (ShopWXUserDTO shopWXUserDTO : shopWXUserDTOs) {
          shopIds.add(shopWXUserDTO.getShopId());
        }
        List<ShopDTO> shopDTOs = configService.getShopByIds(shopIds.toArray(new Long[shopIds.size()]));
        modelMap.put("shopDTOs", shopDTOs);
        modelMap.put("shopDTO", CollectionUtil.getFirst(shopDTOs));
      }
      modelMap.put("vehicleDTOs", wxUserService.getWXUserVehicleByOpenId(openId));
      String UserAgent = request.getHeader("User-Agent");
//      modelMap.put("iPhone", UserAgentUtil.iPhone(UserAgent));
      modelMap.put("userDTO", wxUserService.getWXUserDTOByOpenId(openId));
      modelMap.put("date", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.YEAR_MONTH_DATE_2));
      modelMap.put("time", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_FORMAT_TIME));
      return PAGE_V_APPOINT;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PAGE_VEHICLE_BIND;
    }
  }

  /**
   * 车辆评估
   *
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/2Insurance", method = RequestMethod.GET)
  public String toVehicleInsurance(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    LOG.info("wx:2Evaluate publicNo is {},code is {}", state, code);
    try {
      OAuthAccessToken authAccessToken = WXHelper.getOAuthAccessToken(state, code);
      modelMap.put("openId", authAccessToken.getOpenid());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return PAGE_V_INSURANCE;
  }


}
