package com.bcgogo.wx.controller;

import com.bcgogo.PageErrorMsg;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.api.response.AppGsmVehicleResponse;
import com.bcgogo.api.response.OneKeyRescueResponse;
import com.bcgogo.common.CommonUtil;
import com.bcgogo.common.Result;
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
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.OAuthAccessToken;
import com.bcgogo.wx.user.AppWXUserDTO;
import com.bcgogo.wx.user.OneKeyRescueDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/mirror")
public class WXMirrorController {
  private static final Logger LOG = LoggerFactory.getLogger(WXMirrorController.class);

  private static  String DOMAIN_BCGOGO = "http://reg.bcgogo.com";

  static {
    if(CommonUtil.isDevMode()){
      DOMAIN_BCGOGO="http://wx.bcgogo.cn:8141";
    }
  }


  /**
   * 行车轨迹
   *
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/dri", method = RequestMethod.GET)
  public String toDriveLog(ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:行车轨迹 publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/2DriveLog/").append(openId).append("/NULL/NULL/NULL");
      LOG.info("redirect url:{}", sb.toString());
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }


  /**
   * 车辆定位
   *
   * @param request
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/vlo", method = RequestMethod.GET)
  public String toVehicleLocation(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    LOG.info("wx:2VLocation publicNo is {},code is {}", state, code);
    try {
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      if (authAccessToken == null) {
        modelMap.put("result", new PageErrorMsg("微信返回code 和 state 为空,", "请刷新后再试"));
        return Constant.PAGE_ERROR;
      }
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/2VLocation/").append(openId).append("/NULL");
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }


  /**
   * 违章查询
   *
   * @param request
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/vio", method = RequestMethod.GET)
  public String toVehicleViolate(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:违章查询 publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/violate/").append(openId).append("/NULL");
      LOG.info("redirect url:{}", sb.toString());
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 故障查询
   *
   * @param code
   * @param state
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/fac", method = RequestMethod.GET)
  public String getFaultCode(ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:getFaultCode publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      if (authAccessToken == null) {
        modelMap.put("result", new PageErrorMsg("微信返回code 和 state 为空,", "请刷新后再试"));
        return Constant.PAGE_ERROR;
      }
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/faultCode/NULL/").append(openId).append("/UNTREATED");
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return Constant.PAGE_ERROR;
    }
  }


  /**
   * 车况检查
   *
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/gvd", method = RequestMethod.GET)
  public String toGsmVehicleData(ModelMap modelMap, String code, String state) {
    LOG.info("wx:gvData publicNo is {},code is {}", state, code);
    try {
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      if (authAccessToken == null) {
        modelMap.put("result", new PageErrorMsg("微信返回code 和 state 为空,", "请刷新后再试"));
        return Constant.PAGE_ERROR;
      }
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/gvData/").append(openId).append("/NULL");
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 在线预约
   *
   * @param request
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/apt", method = RequestMethod.GET)
  public String toMirrorAppoint(HttpServletRequest request, ModelMap modelMap, String code, String state) throws Exception {
    LOG.info("wx:toMirrorAppoint publicNo is {},code is {}", state, code);
    try {
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      modelMap.put("openId", authAccessToken.getOpenid());
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/2Appoint/").append(openId).append("/NULL");
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }


  /**
   * 碰撞视频
   *
   * @param request
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/vdo", method = RequestMethod.GET)
  public String toImpactVideo(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:toImpactVideo publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      modelMap.put("openId", authAccessToken.getOpenid());
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/2Video/").append(openId);
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 救援电话
   *
   * @param request
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/mle", method = RequestMethod.GET)
  public String toAccidentMobile(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:toAccidentMobile publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      modelMap.put("openId", authAccessToken.getOpenid());
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/aMobile/").append(openId);
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 我的消息
   *
   * @param request
   * @param modelMap
   * @param code
   * @param state
   * @return
   */
  @RequestMapping(value = "/meg", method = RequestMethod.GET)
  public String toMessage(HttpServletRequest request, ModelMap modelMap, String code, String state) {
    try {
      LOG.info("wx:toAccidentMobile publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      modelMap.put("openId", authAccessToken.getOpenid());
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/myMsg/").append(openId);
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 我的车辆
   *
   * @param code
   * @param state
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/vel", method = RequestMethod.GET)
  public String toVehicleList( ModelMap modelMap,String code, String state) throws Exception {
    try {
      LOG.info("wx:toAccidentMobile publicNo is {},code is {}", state, code);
      OAuthAccessToken authAccessToken = WXHelper.getMirrorOAuthAccessToken(code);
      modelMap.put("openId", authAccessToken.getOpenid());
      String openId = authAccessToken.getOpenid();
      StringBuilder sb = new StringBuilder();
      sb.append("redirect:")
        .append(DOMAIN_BCGOGO)
        .append("/web/mirror/vehicleList/").append(openId);
      return sb.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }


}
