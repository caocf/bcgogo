package com.bcgogo.user.service.wx;

import com.bcgogo.api.RescueDTO;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.common.CommonUtil;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.model.WXImageLib;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.Constant;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.wx.WXShopAccount;
import com.bcgogo.user.service.wx.impl.WXAccountService;
import com.bcgogo.utils.*;
import com.bcgogo.wx.*;
import com.bcgogo.wx.action.WXUserAction;
import com.bcgogo.wx.menu.*;
import com.bcgogo.wx.message.template.WXKWMsgTemplate;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import com.bcgogo.wx.qr.GetQRResult;
import com.bcgogo.wx.qr.QRActionInfo;
import com.bcgogo.wx.qr.QRType;
import com.bcgogo.wx.security.WXBizMsgCrypt;
import com.bcgogo.wx.user.WXAccountDTO;
import com.bcgogo.wx.user.WXKWTemplateDTO;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
//

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-11
 * Time: 下午3:27
 * To change this template use File | Settings | File Templates.
 */
public class WXHelper {
  public static final Logger LOG = LoggerFactory.getLogger(WXHelper.class);

  public static String decryptMsg(String publicNo, String msg, String msg_signature, String timestamp, String nonce) throws Exception {
    if (StringUtil.isEmpty(publicNo) || StringUtil.isEmpty(msg) || StringUtil.isEmpty(msg_signature)) {
      return null;
    }
    WXAccountDTO accountDTO = ServiceManager.getService(WXAccountService.class).getDecryptedWXAccountByPublicNo(publicNo);
    WXBizMsgCrypt pc = new WXBizMsgCrypt(accountDTO);
    return pc.decryptMsg(msg_signature, timestamp, nonce, msg);
//    return WXMsgCryptTools.decryptMsg(accountDTO,msg_signature,timestamp,nonce,msg);
  }


  /**
   * 验证签名
   *
   * @param signature
   * @param timestamp
   * @param nonce
   * @return
   */
  public static boolean checkSignature(String signature, String timestamp, String nonce) {
    String[] arr = new String[]{WXConstant.token, timestamp, nonce};
    // 将token、timestamp、nonce三个参数进行字典序排序
    Arrays.sort(arr);
    StringBuilder content = new StringBuilder();
    for (int i = 0; i < arr.length; i++) {
      content.append(arr[i]);
    }
    MessageDigest md = null;
    String tmpStr = null;
    try {
      md = MessageDigest.getInstance("SHA-1");
      // 将三个参数字符串拼接成一个字符串进行sha1加密
      byte[] digest = md.digest(content.toString().getBytes());
      tmpStr = byteToStr(digest);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
    return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
  }

  /**
   * 将字节数组转换为十六进制字符串
   *
   * @param byteArray
   * @return
   */
  private static String byteToStr(byte[] byteArray) {
    String strDigest = "";
    for (int i = 0; i < byteArray.length; i++) {
      strDigest += byteToHexStr(byteArray[i]);
    }
    return strDigest;
  }

  /**
   * 将字节转换为十六进制字符串
   *
   * @param mByte
   * @return
   */
  private static String byteToHexStr(byte mByte) {
    char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    char[] tempArr = new char[2];
    tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
    tempArr[1] = Digit[mByte & 0X0F];

    String s = new String(tempArr);
    return s;
  }

  public static OAuthAccessToken getMirrorOAuthAccessToken(String code) throws Exception {
    if(StringUtil.isEmpty(code)) return null;
    return getOAuthAccessToken(WXConstant.MIRROR_APP_ID, WXConstant.MIRROR_SECRET, code);
  }

  public static OAuthAccessToken getOAuthAccessToken(String publicNo, String code) throws Exception {
    WXAccountDTO accountDTO = ServiceManager.getService(WXAccountService.class).getDecryptedWXAccountByPublicNo(publicNo);
    if (accountDTO == null || StringUtil.isEmpty(accountDTO.getAppId()) || StringUtil.isEmpty(accountDTO.getSecret())) {
      LOG.error("wx:getOAuthAccessToken , account is exception and publicNo {} ", publicNo);
      return null;
    }
    return getOAuthAccessToken(accountDTO.getAppId(), accountDTO.getSecret(), code);
  }

  /**
   * 微信OAuth2.0授权登录让微信用户使用微信身份安全登录第三方应用或网站
   * 与基础支持中的access_token不同
   *
   * @param appId
   * @param secret
   * @param code
   * @return
   * @throws Exception
   */
  public static OAuthAccessToken getOAuthAccessToken(String appId, String secret, String code) throws Exception {
    String url = WXConstant.URL_OAUTH_ACCESS_TOKEN;
    url = url.replace("{APP_ID}", appId).replace("{SECRET}", secret).replace("{CODE}", code);
    HttpResponse response = HttpUtils.sendGet(url);
    String accessTokenJson = response.getContent();
    OAuthAccessToken accessToken = JsonUtil.jsonToObj(accessTokenJson, OAuthAccessToken.class);
    if (StringUtil.isNotEmpty(accessToken.getErrcode())) {
      LOG.error("wx:get oauth_access_token error,errCode is {} and errMsg is {}", accessToken.getErrcode(), accessToken.getErrmsg());
      return null;
    }
    LOG.info("get oauth_access_token success,openId is {}", accessToken.getOpenid());
    return accessToken;
  }


  /**
   * 获取 access_token
   * 并发访问时，每隔一段时间自动尝试请求一次
   *
   * @param appId
   * @param secret
   * @return
   * @throws Exception
   */
  private static String getAccessToken(String appId, String secret) throws Exception {
    int tryingCount = 0;
    String access_token = null;
    do {
      access_token = doGetAccessToken(appId, secret);
      if (StringUtil.isNotEmpty(access_token)) {
        return access_token;
      }
      Thread.sleep(WXConstant.GET_ACCESS_TRYING_EXPIRY);
      tryingCount++;
      LOG.debug("wx:the " + tryingCount + " times,try to get access_token,and appId is {}", appId);
    } while (tryingCount < 10); //尝试10次
    return null;
  }

  private static String doGetAccessToken(String appId, String secret) throws IOException {
    try {
      if (StringUtil.isEmpty(appId) || StringUtil.isEmpty(secret)) {
        LOG.error("wx:illegal param");
        return null;
      }
      String access_token = StringUtil.valueOf(MemCacheAdapter.get(WXConstant.KEY_PREFIX_ACCESS_TOKEN + appId));
      if (StringUtil.isNotEmpty(access_token)) {
        return access_token;
      }
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_GET_ACCESS_TOKEN, appId)) {
        return null;
      }
      LOG.info("wx:begin to get platform access_token");
      String url = WXConstant.URL_ACCESS_TOKEN;
      url = url.replace("{APPID}", appId).replace("{SECRET}", secret);
      HttpResponse response = HttpUtils.sendGet(url);
      String accessTokenJson = response.getContent();
      AccessToken accessToken = JsonUtil.jsonToObj(accessTokenJson, AccessToken.class);
      if (StringUtil.isNotEmpty(accessToken.getErrcode())) {
        LOG.error("wx:get access_token error,errMsg is {}", accessToken.getErrmsg());
        return null;
      }
      access_token = accessToken.getAccess_token();
      LOG.info("wx:get platform access_token success,and access_token is {}", access_token);
      MemCacheAdapter.set(WXConstant.KEY_PREFIX_ACCESS_TOKEN + appId, access_token, new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_ACCESS_TOKEN));
      return access_token;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_GET_ACCESS_TOKEN, appId);
    }
  }

  /**
   * 获取 jsApiTicket
   * 并发访问时，每隔一段时间自动尝试请求一次
   *
   * @param appId
   * @param secret
   * @return
   * @throws Exception
   */
  private static String getJsApiTicket(String appId, String secret) throws Exception {
    int tryingCount = 0;
    String ticket = null;
    do {
      ticket = doGetJsApiTicket(appId, secret);
      if (StringUtil.isNotEmpty(ticket)) {
        return ticket;
      }
      Thread.sleep(WXConstant.GET_ACCESS_TRYING_EXPIRY);
      tryingCount++;
      LOG.debug("wx:the " + tryingCount + " times,try to get jsApiTicket,and appId is {}", appId);
    } while (tryingCount < 10); //尝试10次
    return null;
  }

  private static String doGetJsApiTicket(String appId, String secret) throws Exception {
    try {
      if (StringUtil.isEmpty(appId) || StringUtil.isEmpty(secret)) {
        LOG.error("wx:illegal param");
        return null;
      }
      String accessToken = getAccessToken(appId, secret);
      if (StringUtil.isEmpty(accessToken)) return null;
      String jsApiTicket = StringUtil.valueOf(MemCacheAdapter.get(WXConstant.KEY_PREFIX_JS_API_TICKET + appId));
      if (StringUtil.isNotEmpty(jsApiTicket)) {
        return jsApiTicket;
      }
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_GET_JS_API_TICKET, appId)) {
        return null;
      }
      LOG.debug("wx:begin to get platform jsApiTicket");
      String url = WXConstant.URL_JS_API_TICKET;
      url = url.replace("{ACCESS_TOKEN}", accessToken);
      HttpResponse response = HttpUtils.sendGet(url);
      String ticketJson = response.getContent();
      JSApiTicket ticket = JsonUtil.jsonToObj(ticketJson, JSApiTicket.class);
      jsApiTicket = ticket.getTicket();
      if (!WXConstant.SUCCESS.equals(ticket.getErrcode())) {
        LOG.error("wx:get jsApiTicket error,errMsg is {}", ticket.getErrmsg());
        return null;
      }
      LOG.debug("wx:get platform jsApiTicket({}) success", jsApiTicket);
      MemCacheAdapter.set(WXConstant.KEY_PREFIX_JS_API_TICKET + appId, jsApiTicket, new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_ACCESS_TOKEN));
      return jsApiTicket;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_GET_JS_API_TICKET, appId);
    }
  }

  public static String getAccessTokenByPublicNo(String publicNo) throws Exception {
    IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
    WXAccountDTO accountDTO = accountService.getDecryptedWXAccountByPublicNo(publicNo);
    return getAccessToken(accountDTO.getAppId(), accountDTO.getSecret());
  }

  public static String getAccessTokenByShopId(Long shopId) throws Exception {
    IWXAccountService accountService = ServiceManager.getService(IWXAccountService.class);
    WXAccountDTO accountDTO = accountService.getDecryptedWXAccountByShopId(shopId);
    //判断是否店铺自己接入的的公共号
    accountDTO = accountDTO == null ? accountService.getDefaultWXAccount() : accountDTO;
    return getAccessToken(accountDTO.getAppId(), accountDTO.getSecret());
  }

  public static String getJsApiTicketByPublicNo(String publicNo) throws Exception {
    IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
    WXAccountDTO accountDTO = accountService.getDecryptedWXAccountByPublicNo(publicNo);
    return getJsApiTicket(accountDTO.getAppId(), accountDTO.getSecret());
  }


  /**
   * 创建临时二维码
   *
   * @param expireSeconds
   * @param sceneId
   * @return
   * @throws Exception
   */
  public static GetQRResult createTempQRCode(String publicNo, Long expireSeconds, String sceneId) throws Exception {
    return doCreateQRCode(publicNo, expireSeconds, QRType.QR_SCENE, sceneId);
  }

  /**
   * 创建永久二维码
   *
   * @param sceneId
   * @return
   * @throws Exception
   */
  public static GetQRResult createLimitQRCode(String publicNo, String sceneId) throws Exception {
    return doCreateQRCode(publicNo, null, QRType.QR_LIMIT_SCENE, sceneId);
  }

  private static GetQRResult doCreateQRCode(String publicNo, Long expireSeconds, QRType scene, String sceneId) throws Exception {
    if (scene == null || sceneId == null) {
      throw new Exception("illegal param");
    }
    String accessToken = WXHelper.getAccessTokenByPublicNo(publicNo);
    if (StringUtil.isEmpty(accessToken)) {
      throw new Exception("can't get access_token,method=createQRCode");
    }
    String url = WXConstant.URL_CREATE_QR_CODE;
    url = url.replace("{ACCESS_TOKEN}", accessToken);
    QRActionInfo actionInfo = new QRActionInfo();
    actionInfo.setExpire_seconds(StringUtil.valueOf(expireSeconds));
    actionInfo.setAction_name(scene);
    Map<String, String> infoMap = new HashMap<String, String>();
    infoMap.put("scene_id", sceneId);
    actionInfo.getAction_info().put("scene", infoMap);
    HttpResponse response = HttpUtils.sendPost(url, JsonUtil.objectToJson(actionInfo));
    String content = response.getContent();
    return JsonUtil.jsonToObj(content, GetQRResult.class);
  }


  /**
   * 创建菜单
   *
   * @return
   * @throws Exception
   * @throws org.dom4j.DocumentException
   */
  public static Result createMenu(String publicNo) throws Exception {
    Result result = new Result();
    String accessToken = WXHelper.getAccessTokenByPublicNo(publicNo);
    if (StringUtil.isEmpty(accessToken)) {
      return null;
    }
    String menu_delete_url = WXConstant.URL_DELETE_MENU.replace("{ACCESS_TOKEN}", accessToken);
    LOG.info("delete menu");
    HttpResponse response = HttpUtils.sendPost(menu_delete_url);
    ErrCode errCode = JsonUtil.jsonToObj(response.getContent(), ErrCode.class);
    if (!ErrCode.SUCCESS.equals(errCode.getErrcode())) {
      return result.LogErrorMsg("delete menu failed msg is " + errCode.getErrmsg());
    }
    String menu_create_url = WXConstant.URL_CREATE_MENU.replace("{ACCESS_TOKEN}", accessToken);
    String menu = getMenu(publicNo);
    LOG.info("create menu,menu={}", menu);
    response = HttpUtils.sendPost(menu_create_url, menu);
    errCode = JsonUtil.jsonToObj(response.getContent(), ErrCode.class);
    if (ErrCode.SUCCESS.equals(errCode.getErrcode())) {
      result.setMsg("create menu success!");
      return result;
    } else {
      return result.LogErrorMsg("create menu failed,msg=" + errCode.getErrmsg());
    }
  }

  /**
   * 生成短号
   *
   * @param url
   * @return
   * @throws Exception
   */
  public static String getShortUrl(String publicNo, String url) throws Exception {
    String accessToken = WXHelper.getAccessTokenByPublicNo(publicNo);
    if (StringUtil.isEmpty(accessToken)) {
      return null;
    }
    String gen_short_url = WXConstant.URL_GET_SHORT.replace("{ACCESS_TOKEN}", accessToken);
    Map<String, String> paramsMap = new HashMap<String, String>();
    paramsMap.put("action", "long2short");
    paramsMap.put("long_url", url);
    HttpResponse response = HttpUtils.sendPost(gen_short_url, JsonUtil.objectToJson(paramsMap));
    GetShortUrlResult result = JsonUtil.jsonToObj(response.getContent(), GetShortUrlResult.class);
    if (ErrCode.SUCCESS.equals(result.getErrcode())) {
      return result.getShort_url();
    } else {
      LOG.error("get short url exception,", result.getErrmsg());
      return null;
    }
  }


  /**
   * 获取微信模版-单据消费
   *
   * @param publicNo
   * @param orderTypes
   * @param vehicleNo
   * @param touser
   * @param dateTime
   * @param payAmount
   * @param location
   * @return
   * @throws Exception
   */
  public static WXKWMsgTemplate getOrderConsumerTemplate(String publicNo, OrderTypes orderTypes, String vehicleNo, String touser, String dateTime, String payAmount, String location) throws Exception {
//    WXAccountDTO accountDTO = ServiceManager.getService(WXAccountService.class).getWXAccountDTOByShopId(shopId);
//       String publicNo = accountDTO == null ? WXHelper.getDefaultPublicNo() : accountDTO.getPublicNo();
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_CONSUME_REMIND);
    if (templateDTO == null) {
      LOG.error("can't get OrderConsumerTemplate");
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    //头部
    String first = templateDTO.getFirst();
    String orderTypeStr = "";
    if (OrderTypes.REPAIR.equals(orderTypes)) {
      orderTypeStr = "施工";
    } else if (OrderTypes.WASH_BEAUTY.equals(orderTypes)) {
      orderTypeStr = "洗车";
    }
    first = first.replace("{orderType}", orderTypeStr).replace("{vehicleNo}", vehicleNo);
    msgTemplate.setFirst(first, templateDTO.getFirstColor());
    msgTemplate.setRemark(templateDTO.getRemark(), templateDTO.getRemarkColor());
    msgTemplate.setKeyword1(dateTime, templateDTO.getKeyword1Color());
    msgTemplate.setKeyword2(payAmount + "元", templateDTO.getKeyword2Color());
    msgTemplate.setKeyword3(location, templateDTO.getKeyword3Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  /**
   * 获取微信模版-会员消费
   *
   * @param publicNo
   * @param memberNo
   * @param consume
   * @param date
   * @param shopName
   * @return
   * @throws Exception
   */
  public static WXKWMsgTemplate getMemberConsumerTemplate(String publicNo, String touser, String memberNo, String consume, String date, String shopName) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_MEMBER_CONSUME);
    if (templateDTO == null) {
      LOG.error("wx:can't get member consumer template");
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    //头部
    String first = templateDTO.getFirst();
    first = first.replace("{DATE}", DateUtil.convertDateLongToDateString(DateUtil.STANDARD, System.currentTimeMillis()));
    msgTemplate.setFirst(first, templateDTO.getFirstColor());
    msgTemplate.setRemark(templateDTO.getRemark(), templateDTO.getRemarkColor());
    msgTemplate.setKeyword1(memberNo, templateDTO.getKeyword1Color());
    msgTemplate.setKeyword2(consume, templateDTO.getKeyword2Color());
    msgTemplate.setKeyword3(shopName, templateDTO.getKeyword3Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  /**
   * 车辆违章
   *
   * @param publicNo
   * @param touser
   * @param recordDTO
   * @return
   * @throws Exception
   */
  public static WXKWMsgTemplate getVRegulationTemplate(String publicNo, String touser, VehicleViolateRegulationRecordDTO recordDTO) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_VEHICLE_VIOLATE_REGULATION);
    if (templateDTO == null) {
      LOG.error("can't get VRegulationTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    String firstStr = templateDTO.getFirst().replace("{vehicleNo}", recordDTO.getVehicleNo()).replace("{money}", recordDTO.getMoney()).replace("{fen}", recordDTO.getFen());
    msgTemplate.setFirst(firstStr, templateDTO.getFirstColor());
    msgTemplate.setRemark(templateDTO.getRemark(), templateDTO.getRemarkColor());
    msgTemplate.setKeyword1(recordDTO.getDate(), templateDTO.getKeyword1Color());
    msgTemplate.setKeyword2(recordDTO.getArea(), templateDTO.getKeyword2Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  public static WXMsgTemplate getAppointRemindTemplate(String publicNo, String touser, String vehicleNo, String appoint, String date, String shopMobile) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_APPOINT_REMIND);
    if (templateDTO == null) {
      LOG.error("can't get AppointRemindTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    msgTemplate.setFirst(templateDTO.getFirst().replace("{vehicleNo}", vehicleNo), templateDTO.getFirstColor());
    msgTemplate.setRemark(templateDTO.getRemark().replace("{shopMobile}", StringUtil.valueOf(shopMobile)), templateDTO.getRemarkColor());
    msgTemplate.setKeyword1(appoint, templateDTO.getKeyword1Color());
    msgTemplate.setKeyword2(date, templateDTO.getKeyword2Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  public static WXMsgTemplate getMirrorAppointRemindTemplate(String publicNo, String touser, String vehicleNo, String customer, String date, String shopMobile) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_MIRROR_APPOINT_REMIND);
    if (templateDTO == null) {
      LOG.error("can't get getMirrorAppointRemindTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    msgTemplate.setFirst(templateDTO.getFirst().replace("{customer}", customer), templateDTO.getFirstColor());
    msgTemplate.setKeyword1(vehicleNo, templateDTO.getKeyword1Color());
    msgTemplate.setKeyword2(date, templateDTO.getKeyword2Color());
    msgTemplate.setKeyword3(shopMobile, templateDTO.getKeyword2Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

//  /**
//   * 车辆碰撞提醒
//   *
//   * @param publicNo
//   * @param touser
//   * @param vehicleNo
//   * @param location
//   * @param date
//   * @return
//   * @throws Exception
//   */
//  public static WXMsgTemplate getMirrorImpactRemindTemplate(String publicNo, String touser, String vehicleNo, String location, String date) throws Exception {
//    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_IMPACT_REMIND);
//    if (templateDTO == null) {
//      LOG.error("can't get getMirrorImpactRemindTemplate,publicNo is {}", publicNo);
//      return null;
//    }
//    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
//    msgTemplate.setFirst(templateDTO.getFirst().replace("{vehicleNo}", vehicleNo), templateDTO.getFirstColor());
//    msgTemplate.setKeyword1(date, templateDTO.getKeyword2Color());
//    msgTemplate.setKeyword2(location, templateDTO.getKeyword1Color());
//    msgTemplate.setTouser(touser);
//    msgTemplate.setTopcolor(templateDTO.getTopColor());
//    return msgTemplate;
//  }

  /**
   * 车辆异常震动提醒(碰撞)
   *
   * @param publicNo
   * @param touser
   * @param vehicleNo
   * @param remark
   * @param date
   * @return
   * @throws Exception
   */
  public static WXKWMsgTemplate getMirrorImpactRemindTemplate(String publicNo, String touser, String vehicleNo, String category, String remark, String date) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_VEHICLE_FAULT_REMIND);
    if (templateDTO == null) {
      LOG.error("can't getMirrorImpactRemindTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    String first=WXConstant.TEMPLATE_TITLE_VEHICLE_FAULT_REMIND_FIRST;
    msgTemplate.setFirst(first.replace("{VEHICLE_NO}", vehicleNo), templateDTO.getFirstColor());
    msgTemplate.setKeyword1(date, templateDTO.getKeyword2Color());

    msgTemplate.setKeyword2(category, templateDTO.getKeyword1Color());
    msgTemplate.setRemark(remark, templateDTO.getRemarkColor());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  /**
   * 车辆异常提醒
   *
   * @param publicNo
   * @param touser
   * @param vehicleNo
   * @param remark
   * @param date
   * @return
   * @throws Exception
   */
  public static WXMsgTemplate getMirrorVehicleFaultRemindTemplate(String publicNo, String touser, String vehicleNo, String category, String remark, String date) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_VEHICLE_FAULT_REMIND);
    if (templateDTO == null) {
      LOG.error("can't getMirrorVehicleFaultRemindTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    msgTemplate.setFirst(templateDTO.getFirst().replace("{vehicleNo}", vehicleNo), templateDTO.getFirstColor());
    msgTemplate.setKeyword1(date, templateDTO.getKeyword2Color());
    msgTemplate.setKeyword2(category, templateDTO.getKeyword1Color());
    msgTemplate.setRemark(remark, templateDTO.getRemarkColor());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }




  /**
   * 车辆违章
   *
   * @param publicNo
   * @param touser
   * @param recordDTO
   * @return
   * @throws Exception
   */
  public static WXKWMsgTemplate getMirrorVRegulationTemplate(String publicNo, String touser, VehicleViolateRegulationRecordDTO recordDTO) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_NEW_VEHICLE_VIOLATE_REGULATION);
    if (templateDTO == null) {
      LOG.error("can't get VRegulationTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    String firstStr = templateDTO.getFirst().replace("{vehicleNo}", recordDTO.getVehicleNo());
    msgTemplate.setFirst(firstStr, templateDTO.getFirstColor());
    Map<String, String> keyword1Map = new HashMap<String, String>();
    msgTemplate.getData().put("keynote1", keyword1Map);
    keyword1Map.put("value", recordDTO.getArea());
    keyword1Map.put("color", templateDTO.getKeyword1Color());
    Map<String, String> keyword2Map = new HashMap<String, String>();
    msgTemplate.getData().put("keynote2", keyword2Map);
    keyword2Map.put("value", recordDTO.getDate());
    keyword2Map.put("color", templateDTO.getKeyword2Color());
    Map<String, String> keyword3Map = new HashMap<String, String>();
    msgTemplate.getData().put("keynote3", keyword3Map);
    keyword3Map.put("value", recordDTO.getMoney());
    keyword3Map.put("color", templateDTO.getKeyword3Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  /**
   * 一键救援提醒模版
   *
   * @param publicNo
   * @param touser
   * @param rescueDTO
   * @return
   * @throws Exception
   */
  public static WXKWMsgTemplate getRescueTemplate(String publicNo, String touser, RescueDTO rescueDTO) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_VEHICLE_RESCUE);
    if (templateDTO == null) {
      LOG.error("can't get rescueTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    String firstStr = templateDTO.getFirst().replace("{vehicleNo}", rescueDTO.getVehicleNo());
    msgTemplate.setFirst(firstStr, templateDTO.getFirstColor());
    msgTemplate.setKeyword1(rescueDTO.getUploadTimeStr(), templateDTO.getKeyword1Color());
    msgTemplate.setKeyword2(rescueDTO.getAddr(), templateDTO.getKeyword2Color());
    msgTemplate.setKeyword3(rescueDTO.getMobile(), templateDTO.getKeyword3Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setRemark(templateDTO.getRemark(), templateDTO.getRemarkColor());
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  /**
   * 后视镜留言提醒
   *
   * @param publicNo
   * @param touser
   * @param content
   * @param fromName
   * @param time
   * @return
   * @throws Exception
   */
  public static WXKWMsgTemplate getNotifyTemplate(String publicNo, String touser, String content, String fromName, String time) throws Exception {
    WXKWTemplateDTO templateDTO = ServiceManager.getService(IWXUserService.class).getCachedWXKWTemplate(publicNo, WXConstant.TEMPLATE_TITLE_VEHICLE_NOTIFY);
    if (templateDTO == null) {
      LOG.error("can't get getNotifyTemplate,publicNo is {}", publicNo);
      return null;
    }
    WXKWMsgTemplate msgTemplate = templateDTO.toWXKWMsgTemplate();
    msgTemplate.setFirst(templateDTO.getFirst(), templateDTO.getFirstColor());
    msgTemplate.setKeyword1(content, templateDTO.getKeyword1Color());
    msgTemplate.setKeyword2(fromName, templateDTO.getKeyword2Color());
    msgTemplate.setKeyword3(time, templateDTO.getKeyword3Color());
    msgTemplate.setTouser(touser);
    msgTemplate.setRemark(templateDTO.getRemark(), templateDTO.getRemarkColor());
    msgTemplate.setTopcolor(templateDTO.getTopColor());
    return msgTemplate;
  }

  /**
   * 记录用户上次操作场景
   *
   * @param openId
   * @param action
   */
  public static void logLastUserAction(String openId, WXUserAction action) {
    String key = WXConstant.USER_LAST_ACTION_PREFIX + openId;
    MemCacheAdapter.set(key, action, new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_USER_LAST_ACTION));
  }

  public static WXUserAction getLastUserAction(String openId) {
    String key = WXConstant.USER_LAST_ACTION_PREFIX + openId;
    WXUserAction action = (WXUserAction) MemCacheAdapter.get(key);
    return action;
  }

  /**
   * 获取wx_image_lib中图片的url
   *
   * @param name
   * @return
   */
  public static String getWXImageLibShortUrl(String name) {
    if (StringUtil.isEmpty(name)) return null;
    String key = WXConstant.WX_IMAGE_LIB_PREFIX + name;
    String shortUrl = StringUtil.valueOf(MemCacheAdapter.get(key));
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (StringUtil.isEmpty(shortUrl)) {
      WXImageLib imageLib = configService.getWXImageLib(name);
      shortUrl = imageLib != null ? imageLib.getShortUrl() : null;
      if (StringUtil.isNotEmpty(shortUrl)) {
        MemCacheAdapter.set(key, shortUrl, new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_WX_IMAGE_LIB));
      }
    }
    return shortUrl;
  }

  public static boolean validateWXShopAccount(Long shopId) {
    WXShopAccount shopAccount = ServiceManager.getService(WXAccountService.class).getWXShopAccountByShopId(shopId);
    if (shopAccount == null) {
      LOG.error("wx:sendConsumeMsg failed,shopAccount isn't exist,shopId is {}", shopId);
      return false;
    }
    if (shopAccount.getBalance() <= 0D && System.currentTimeMillis() > shopAccount.getExpireDate()) {
      LOG.debug("wx:sendConsumeMsg stop,shopAccount's balance isn't enough,shopId is {}", shopId);
      return false;
    }
    return true;
  }

  /**
   * 生成跳转到单据详细的url
   *
   * @param orderId
   * @param orderTypes
   * @param vehicleNo
   * @return
   */
  public static String orderDetailUrl(Long orderId, String orderTypes, String vehicleNo) {
    String url = getEvnDomain() + WXConstant.TO_ORDER_DETAIL_URL;
    url = url.replace("{orderId}", new BigInteger(String.valueOf(orderId)).toString(36));
    url = url.replace("{orderType}", orderTypes.toString());
    url = url.replace("{vehicleNo}", vehicleNo.replaceAll("[\\u4E00-\\u9FA5]", ""));
    return url;
  }

  public static String memberDetailUrl(String openId, Long memberId) {
    String url = WXHelper.getEvnDomain() + WXConstant.TO_MEMBER_CARD_DETAIL_URL;
    url = url.replace("{MEMBER_ID}", new BigInteger(String.valueOf(memberId)).toString(36)).replace("{OPEN_ID}", openId);
    return url;
  }

  public static String mirrorVideoUrl(String openId) {
    String url = WXHelper.getEvnDomain() + WXConstant.TO_MIRROR_VIDEO_URL;
    url = url.replace("{OPEN_ID}", openId);
    return url;
  }

  public static String vehicleRegulation(Long userVehicleId) {
    String url = WXHelper.getEvnDomain() + WXConstant.TO_VEHICLE_VIOLATE_REGULATION_URL;
    url = url.replace("{USER_VEHICLE_ID}", new BigInteger(String.valueOf(userVehicleId)).toString(36));
    return url;
  }

  public static String mirrorPvMsgUrl(String type, String openId, String appUserNo) {
    String url = WXHelper.getEvnDomain() + WXConstant.TO_MIRROR_PV_MSG_URL;
    url = url.replace("{OPEN_ID}", openId).replace("{TYPE}", type).replace("{APP_USER_NO}", appUserNo);
    return url;
  }

  /**
   * 绑定车辆的url
   *
   * @param openId
   * @return
   */
  public static String vehicleBindUrl(String openId) {
    String url = WXHelper.getEvnDomain() + WXConstant.TO_VEHICLE_BIND_URL;
    url = url.replace("{OPENID}", StringUtil.valueOf(openId));
    return url;
  }

  public static String vehicleEditUrl(Long wUserVehicleId) {
    String url = WXHelper.getEvnDomain() + WXConstant.TO_VEHICLE_EDIT_URL;
    return url.replace("{U_V_ID}", new BigInteger(StringUtil.valueOf(wUserVehicleId)).toString(36));
  }

  public static String articleDetail(Long id) {
    String url = WXHelper.getEvnDomain() + WXConstant.TO_ARTICLE_DETAIL_URL;
    return url.replace("{id}", new BigInteger(StringUtil.valueOf(id)).toString(36));
  }


  /**
   * 读取xml生成菜单json字符串
   *
   * @return
   * @throws Exception
   */
  public static String getMenu(String publicNo) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String cPath = configService.getConfig("wx_cfg_path", ShopConstant.BC_SHOP_ID);
    if (StringUtil.isEmpty(cPath)) return null;
    IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
    WXAccountDTO accountDTO = accountService.getWXAccountDTOByPublicNo(publicNo);
    File file = new File(cPath + "wx_menu_" + publicNo + ".xml");
    if (!file.exists()) {
      file = new File(cPath + "wx_menu.xml");
    }
    SAXReader reader = new SAXReader();
    Document doc = reader.read(file);
    Element root = doc.getRootElement();
    List elements = root.elements();
    if (elements.size() == 0) {
      throw new Exception("wx:menu xml content is empty!");
    }
    //assemble menu
    String appId = accountDTO.getAppId();
    List<Button> buttons = new ArrayList<Button>();
    Iterator it = elements.iterator();
    while (it.hasNext()) {
      Element elem = (Element) it.next();
      if (elem.elements().size() > 0) {
        //xml中<sub_button>
        Element subRoot = (Element) elem.elements().get(0);
        //设置二级子菜单
        List<Element> subElements = subRoot.elements();
        List<Button> subButtons = new ArrayList<Button>();
        for (int i = 0; i < subElements.size(); i++) {
          Element subElem = subElements.get(i);
          subButtons.add(createButton(subElem, publicNo, appId));
        }
        //complexButton 对应xml中<sub_button>
        ComplexButton complexButton = new ComplexButton();
        complexButton.setName(elem.attributeValue("name"));
        complexButton.setSub_button(subButtons.toArray(new Button[subButtons.size()]));
        buttons.add(complexButton);
      } else {
        buttons.add(createButton(elem, publicNo, appId));
      }
    }

    Menu menu = new Menu();
    menu.setButton(buttons.toArray(new Button[buttons.size()]));
    return JsonUtil.objectCHToJson(menu);
  }

  private static Button createButton(Element elem, String publicNo, String appId) throws Exception {
    String type = elem.attributeValue("type");
    if (WXConstant.MENU_TYPE_CLICK.equals(type)) {
      ClickButton button = new ClickButton();
      button.setName(elem.attributeValue("name"));
      button.setKey(elem.attributeValue("key"));
      return button;
    } else if (WXConstant.MENU_TYPE_VIEW.equals(type)) {
      ViewButton button = new ViewButton();
      button.setName(elem.attributeValue("name"));
      String text = elem.getTextTrim();
      if(CommonUtil.isDevMode()){
      //利宜行
//      appId = "wx1cedd36d6b94a448";
//      publicNo = "gh_90e5bd7565e1";
//      domain:http://wx.bcgogo.com:8080

        appId = "wx0696c11ba901270c";
        publicNo = "gh_ed9b9cdc1a98";
      text = text.replace("{APP_ID}", appId).replace("{PUBLIC_NO}", publicNo).replace("{EVN_DOMAIN}", "http://reg.bcgogo.com");
      }else {
        text = text.replace("{APP_ID}", appId).replace("{PUBLIC_NO}", publicNo).replace("{EVN_DOMAIN}", getEvnDomain());
      }

      button.setUrl(text);
      return button;
    }
    throw new Exception("wx:menu xml has grammar error!");
  }

//
//  private void parseMenu(Document doc){
//    Element root = doc.getRootElement();
//      List elements = root.elements();
//      if (elements.size() == 0) {
//        throw new Exception("wx:menu xml content is empty!");
//      }
//      List<Button> buttons=new ArrayList<Button>();
//      Iterator it = elements.iterator();
//      while (it.hasNext()){
//        Element elem = (Element) it.next();
//        //菜单类型
//        String type=elem.attributeValue("type");
//        if(WXConstant.MENU_TYPE_CLICK.equals(type)){
//          ClickButton button=new ClickButton();
//          button.setName(elem.attributeValue("name"));
//          button.setKey(elem.attributeValue("key"));
//          buttons.add(button);
//        }else if(WXConstant.MENU_TYPE_VIEW.equals(type)){
//          ViewButton button=new ViewButton();
//          button.setName(elem.attributeValue("name"));
//          String text=elem.getTextTrim();
//          text=text.replace("{APP_ID}",accountDTO.getAppId()).replace("{PUBLIC_NO}",accountDTO.getPublicNo()).replace("{EVN_DOMAIN}",getEvnDomain());
//          button.setUrl(text);
//          buttons.add(button);
//        }else {
//          List<Element> complexElements=elem.elements();
//          if(CollectionUtil.isEmpty(complexElements))  throw new Exception("menu xml content error!");
//          Element subElement=complexElements.get(0);
//          List<Element> subElements=subElement.elements();
//          ComplexButton complexButton=new ComplexButton();
//          complexButton.setName(elem.attributeValue("name"));
//          //设置子菜单
//          List<Button> subButtons=new ArrayList<Button>();
//          for(int i=0;i<subElements.size();i++){
//            Element subElem=subElements.get(i);
//            String subType=subElem.attributeValue("type");
//            if(WXConstant.MENU_TYPE_CLICK.equals(subType)){
//              ClickButton button=new ClickButton();
//              button.setName(subElem.attributeValue("name"));
//              button.setKey(subElem.attributeValue("key"));
//              subButtons.add(button);
//            }else if(WXConstant.MENU_TYPE_VIEW.equals(subType)){
//              ViewButton button=new ViewButton();
//              button.setName(subElem.attributeValue("name"));
//              String text=subElem.getTextTrim();
//              text=text.replace("{APP_ID}",accountDTO.getAppId()).replace("{PUBLIC_NO}",accountDTO.getPublicNo()).replace("{EVN_DOMAIN}",getEvnDomain());
//              button.setUrl(text);
//              subButtons.add(button);
//            }
//          }
//          complexButton.setSub_button(subButtons.toArray(new Button[subButtons.size()]));
//          buttons.add(complexButton);
//        }
//      }
//  }


  /**
   * 获取密钥
   *
   * @return
   */
  public static byte[] getSecretKey() {
    byte[] sKeyByte = (byte[]) MemCacheAdapter.get(WXConstant.WX_ACCOUNT_SECRET_KEY);
    if (sKeyByte == null || sKeyByte.length == 0) {
      DataInputStream dis = null;
      try {
        IConfigService configService = ServiceManager.getService(IConfigService.class);
        String s_k_path = configService.getConfig("wx_cfg_path", ShopConstant.BC_SHOP_ID);
        if (CommonUtil.isDevMode()) {
          s_k_path = "d:\\tomcat\\key\\wx\\";
        }
        File file = new File(s_k_path + "s_k_file");
        FileInputStream fis = new FileInputStream(file);
        dis = new DataInputStream(fis);
        sKeyByte = new byte[(int) file.length()];
        dis.readFully(sKeyByte);
        MemCacheAdapter.set(WXConstant.WX_ACCOUNT_SECRET_KEY, sKeyByte);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      } finally {
        if (dis == null) return null;
        try {
          dis.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }
    }
    return sKeyByte;
  }

  public static String getEvnDomain() {
    String evn_mode = ServiceManager.getService(IConfigService.class).getConfig("evn_mode", ShopConstant.BC_SHOP_ID);
    if (Constant.EVN_MODE_OFFICIAL.equals(evn_mode)) {
      return WXConstant.OFFICIAL_DOMAIN;
    } else if (Constant.EVN_MODE_DEVELOP.equals(evn_mode)) {
      return WXConstant.DEVELOP_DOMAIN;
    } else {
      return WXConstant.TEST_DOMAIN;
    }
  }

  public static String getMirrorPublicNo() {
    String evn_mode = ServiceManager.getService(IConfigService.class).getConfig("evn_mode", ShopConstant.BC_SHOP_ID);
    if (Constant.EVN_MODE_DEVELOP.equals(evn_mode)) {
      return WXConstant.YI_FA_PUBLIC_ID;
    } else {
      return WXConstant.LI_YI_XING_PUBLIC_ID;
    }
  }

  public static String getDefaultPublicNo() throws Exception {
    WXAccountDTO accountDTO = ServiceManager.getService(WXAccountService.class).getDefaultWXAccount();
    return accountDTO.getPublicNo();
  }


  public static Result clearMemCache(String key, String publicNo) {
    Result result = clearMemKeyAccessToken(publicNo);
    return result;
  }

  public static Result clearMemKeyAccessToken(String publicNo) {
    LOG.info("wx:clear MemKey AccessToken begin");
    try {
      Result result = new Result();
      StringBuilder sb = new StringBuilder();
      WXAccountDTO accountDTO = ServiceManager.getService(WXAccountService.class).getWXAccountDTOByPublicNo(publicNo);
      String key_access_token = WXConstant.KEY_PREFIX_ACCESS_TOKEN + accountDTO.getAppId();
      MemCacheAdapter.delete(key_access_token);
      sb.append("clear key_access_token success\n");
      result.setMsg(sb.toString());
      LOG.info("wx:clear MemKey AccessToken success");
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("wx:clearMemKeyAccessToken failed", false);
    }
  }

  /**
   * 处理前台输入的欢迎词到数据库标准字符
   *
   * @param htmlStr
   * @return
   */
  public static String handleWelcomeWordFromHtml(String htmlStr) {
    if (StringUtils.isBlank(htmlStr)) return null;
    String regEx_img = "<img.*?src=.*?>"; //图片
    Matcher img_matcher = Pattern.compile(regEx_img).matcher(htmlStr);
    while (img_matcher.find()) {
      String img = img_matcher.group();
      Matcher code_matcher = Pattern.compile("code=\".*?\"").matcher(img);
      if (code_matcher.find()) {
        String code = code_matcher.group().replace("code=", "").replace("\"", "");
        code = "{" + code + "}";
        htmlStr = htmlStr.replaceAll(img, code);
      }
    }
    htmlStr = htmlStr.replaceAll("\\s{4,}", "");
    return htmlStr;
  }

  /**
   * 处理数据库的欢迎词到前台显示
   *
   * @param welcomeWord
   * @return
   */
  public static String handleWelcomeWordToHtml(String welcomeWord) {
    if (StringUtils.isBlank(welcomeWord)) return null;
    String regEx_emotion = "\\{.*?\\}"; //数据库中表情结构 如{/微笑}
    Matcher emotion_matcher = Pattern.compile(regEx_emotion).matcher(welcomeWord);
    while (emotion_matcher.find()) {
      String org_emotion = emotion_matcher.group();
      String emotion = org_emotion.replace("{", "").replace("}", "");
      emotion = "<img src=\"./images/emotion" + emotion + ".gif\" code=\"" + emotion + "\">";
      welcomeWord = welcomeWord.replace(org_emotion, emotion);
    }
    return welcomeWord;
  }


  /**
   * 处理成微信发送的标准字符
   *
   * @param welcomeWord
   * @return
   */
  public static String toStandardWelcomeWord(String welcomeWord) {
    if (StringUtils.isBlank(welcomeWord)) return null;
    welcomeWord = welcomeWord.replace("{", "");
    welcomeWord = welcomeWord.replace("}", "");
    welcomeWord = welcomeWord.replace("<br>", "\n");
    welcomeWord = welcomeWord.replaceAll("&nbsp;", " ");
    return welcomeWord;
  }


   public static String tmpTest() {
    String evn_mode = ServiceManager.getService(IConfigService.class).getConfig("evn_mode", ShopConstant.BC_SHOP_ID);
     return null;
  }


}