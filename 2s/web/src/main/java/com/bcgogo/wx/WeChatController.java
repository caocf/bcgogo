package com.bcgogo.wx;

import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.api.response.ApiVehicleViolateRegulationResponse;
import com.bcgogo.camera.CameraConfigDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.cache.ShopConfigCacheManager;
import com.bcgogo.config.dto.ShopConfigDTO;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.service.IShopConfigService;
import com.bcgogo.config.service.ShopConfigService;
import com.bcgogo.config.service.camera.ICameraService;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.model.WXMsg;
import com.bcgogo.notification.model.WXMsgReceiver;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.notification.service.WXService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.model.wx.WXShopAccount;
import com.bcgogo.user.model.wx.WXUser;
import com.bcgogo.user.service.app.IDriveLogService;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.utils.*;
import com.bcgogo.wx.message.WXMCategory;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.user.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ndong
 * Date: 14-9-15
 * Time: 下午5:59
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/weChat.do")
public class WeChatController {
  private static final Logger LOG = LoggerFactory.getLogger(WeChatController.class);
  @Autowired
  private IWXUserService wxUserService;
  @Autowired
  private IWXService wxService;
  @Autowired
  private IWXAccountService accountService;
  @Autowired
  private IWXArticleService wxArticleService;
  @Autowired
  private IConfigService configService;

  public IWXService getWxService() {
    return wxService;
  }

  public void setWxService(IWXService wxService) {
    this.wxService = wxService;
  }

  public IWXUserService getWXUserService() {
    return wxUserService;
  }

  public void setWXUserService(IWXUserService wxUserService) {
    this.wxUserService = wxUserService;
  }

  public IWXAccountService getAccountService() {
    return accountService;
  }

  public void setAccountService(IWXAccountService accountService) {
    this.accountService = accountService;
  }

  public IWXArticleService getWxArticleService() {
    return wxArticleService;
  }

  public void setWxArticleService(IWXArticleService wxArticleService) {
    this.wxArticleService = wxArticleService;
  }

  public IConfigService getConfigService() {
    return configService;
  }

  public void setConfigService(IConfigService configService) {
    this.configService = configService;
  }

  //车辆绑定
  private static final String PAGE_VEHICLE_BIND = "/wx/v_bind";
  //素材详细
  private static final String PAGE_ARTICLE_DETAIL = "/wx/articleDetail";
  //车辆违章记录
  private static final String PAGE_VEHICLE_REGULATION = "/wx/v_regulation";
  //打印微信二维码
  private static final String PAGE_WX_QR_CODE = "/wx/p_wx_qr_code";

  @RequestMapping(params = "method=printWXQRCode")
  public String printWXQRCode(ModelMap modelMap, HttpServletRequest request) {
    try {
      Long shopId = WebUtil.getShopId(request);
      WXAccountDTO accountDTO = accountService.getWXAccountDTOByShopId(shopId);
      //店铺自己没有接入,使用缺省的 统购车业
      if (accountDTO == null) {
        accountDTO = accountService.getDefaultWXAccount();
      }
      String publicNo = accountDTO.getPublicNo();
      WXQRCodeDTO qrCodeDTO = wxUserService.getWXQRCodeDTOByShopId(publicNo, shopId, QRScene.SHOP_USER);
      if (qrCodeDTO == null) {
        qrCodeDTO = wxUserService.getUnAssignedWXQRCode(publicNo, shopId);
        if (qrCodeDTO == null) {
          LOG.error("wx:assign failed,WXQRCodeDTO is null");
          return PAGE_WX_QR_CODE;
        }
        qrCodeDTO.setShopId(shopId);
        wxUserService.saveOrUpdateWXQRCodeDTOs(qrCodeDTO);
      }
      String qr_code_show_url = WXConstant.URL_SHOW_QR_CODE;
      qr_code_show_url = qr_code_show_url.replace("{TICKET}", qrCodeDTO.getTicket());
      LOG.info("wx:print wx qr_code,shopId is {},qr_code_show_url is {}", shopId, qr_code_show_url);
      modelMap.put("qr_code_show_url", qr_code_show_url);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return PAGE_WX_QR_CODE;
  }

  /**
   * 违章详细页面
   *
   * @param modelMap
   * @param _i
   * @return
   */
  @RequestMapping(params = "method=vReg")
  public Object toVehicleRegulation(ModelMap modelMap, String _i) {
    LOG.info("wx:vReg begin");
    try {
      if (StringUtil.isEmpty(_i)) return PAGE_VEHICLE_REGULATION;
      Long userVehicleId = NumberUtil.longValue(new BigInteger(_i, 36).toString(10));
      WXUserVehicleDTO userVehicleDTO = wxUserService.getWXUserVehicleDTOById(userVehicleId);
      String juheCityCode = AreaCacheManager.getJuheCodeByCityCode(userVehicleDTO.getCity());
      if (StringUtil.isEmpty(juheCityCode) || StringUtil.isEmpty(userVehicleDTO.getVin()) || StringUtil.isEmpty(userVehicleDTO.getVehicleNo())) {
        return PAGE_VEHICLE_REGULATION;
      }
      IJuheService juheService = ServiceManager.getService(IJuheService.class);
      Double total_money = 0d;
      int total_fen = 0;
      ApiVehicleViolateRegulationResponse response = juheService.queryVehicleViolateRegulation(juheCityCode, userVehicleDTO.getVehicleNo(), "02", userVehicleDTO.getEngineNo(), userVehicleDTO.getVin(), null);
      if ("SUCCESS".equals(response.getStatus())) {
        List<VehicleViolateRegulationRecordDTO> recordDTOs = response.getQueryResponse().getResult().getLists();
        modelMap.put("recordDTOs", recordDTOs);
        for (VehicleViolateRegulationRecordDTO recordDTO : recordDTOs) {
          int fen = NumberUtil.intValue(recordDTO.getFen());
          total_fen += fen;
          recordDTO.setFen(StringUtil.valueOf(fen));
          total_money = NumberUtil.addition(total_money, recordDTO.getMoney());
        }
      } else if ("FAIL".equals(response.getStatus())) {

      }
      LOG.info("wx:vReg,vehicle is {},tMony is {}", userVehicleDTO.getVehicleNo(), total_money);
      modelMap.put("vehicle", userVehicleDTO.getVehicleNo());
      modelMap.put("tMoney", total_money);
      modelMap.put("tFen", total_fen);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    return PAGE_VEHICLE_REGULATION;
  }

  /**
   * 素材详细
   *
   * @param modelMap
   * @param _i
   * @return
   */
  @RequestMapping(params = "method=aDetail")
  public Object toArticleDetail(HttpServletRequest request, ModelMap modelMap, String _i) {
    try {
      Long id = NumberUtil.longValue(new BigInteger(_i, 36).toString(10));
      WXMsgDTO msgDTO = wxService.getWXMsgDTOById(id);
      if (msgDTO == null) {
        return PAGE_ARTICLE_DETAIL;
      }
      modelMap.put("article", msgDTO);
      modelMap.put("shopName", configService.getShopById(msgDTO.getFromShopId()).getName());
      WXAccountDTO accountDTO = accountService.getWXAccountDTOByShopId(WebUtil.getShopId(request));
      if (accountDTO == null) {
        accountDTO = accountService.getDefaultWXAccount();
      }
      String publicName = accountDTO.getName();
      modelMap.put("publicName", publicName);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return PAGE_ARTICLE_DETAIL;
  }

  @RequestMapping(params = "method=2Bind")
  public Object toVehicleBind(ModelMap modelMap, String _i) {
    modelMap.put("openId", _i);
    modelMap.put("p_type", "BIND");
    modelMap.put("evnDomain", WXHelper.getEvnDomain());
    return PAGE_VEHICLE_BIND;
  }

  @RequestMapping(params = "method=2DriveLog")
  public Object getDriveLog(String openId, Long startTime,Long endTime) {
    return new Result(false, "后视镜用户不存在。");
  }


  /**
   * 查看并编辑车辆
   *
   * @param modelMap
   * @param _i       userVehicleId
   * @return
   */
  @RequestMapping(params = "method=vEdit")
  public Object toVehicleEdit(ModelMap modelMap, String _i) {
    if (StringUtil.isEmpty(_i)) return PAGE_VEHICLE_BIND;
    try {
      Long uVehicleId = NumberUtil.longValue(new BigInteger(_i, 36).toString(10));
      WXUserVehicleDTO userVehicleDTO = wxUserService.getWXUserVehicleDTOById(uVehicleId);
      modelMap.put("userVehicleDTO", userVehicleDTO);
      modelMap.put("openId", userVehicleDTO != null ? userVehicleDTO.getOpenId() : null);
      modelMap.put("p_type", "EDIT");
      modelMap.put("uVehicleId", StringUtil.valueOf(uVehicleId));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return PAGE_VEHICLE_BIND;
  }


  private Result validateBindVehicle(WXUserVehicleDTO userVehicleDTO) throws Exception {
    String openId = userVehicleDTO.getOpenId();
    Result result = new Result();
    if (StringUtil.isEmpty(openId)) return result.LogErrorMsg("账户异常。");
    WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
    if (userDTO == null) {
      LOG.error("wx:bindVehicle exception,wxuser isn't exist,openId is {}", openId);
      return result.LogErrorMsg("您的账户信息异常，无法绑定。请联系一发客服 0512-66733331");
    }
    List<WXUserVehicleDTO> userVehicles = wxUserService.getWXUserVehicle(openId, null);
    if (CollectionUtil.isNotEmpty(userVehicles) && (userVehicles.size() + 1) > WXConstant.USER_BIND_VEHICLE_MAX_SIZE) {
      return result.LogErrorMsg("您绑定的车辆数超过系统限制，无法绑定。");
    }
    String vehicleNo = StringUtil.toTrim(userVehicleDTO.getVehicleNo()).toUpperCase(); //修改后或将要绑定的车牌号
    userVehicleDTO.setVehicleNo(vehicleNo);
    if (!RegexUtils.isVehicleNo(vehicleNo)) {
      return result.LogErrorMsg("抱歉，您输入的车牌号“" + vehicleNo + "”不符合车牌号格式，请检查后重新输入。");
    }
    return result;
  }

  /**
   * 绑定车辆
   *
   * @param userVehicleDTO
   * @return
   */
  @RequestMapping(params = "method=sBind")
  @ResponseBody
  public Result bindVehicle(HttpServletRequest request, WXUserVehicleDTO userVehicleDTO) {
    try {
      Result result = validateBindVehicle(userVehicleDTO);
      if (result == null || !result.isSuccess()) return result;
      String openId = userVehicleDTO.getOpenId();
      String vehicleNo = StringUtil.toTrim(userVehicleDTO.getVehicleNo()).toUpperCase();
      //绑定时校验
      WXUserVehicleDTO uVehicleDTO = CollectionUtil.getFirst(wxUserService.getWXUserVehicle(openId, vehicleNo));
      if (uVehicleDTO != null) {
        return result.LogErrorMsg("车牌号" + vehicleNo + "已经绑定。");
      }
      if (result == null || !result.isSuccess()) return result;
      userVehicleDTO.setDeleted(DeletedType.FALSE);
      wxUserService.saveOrUpdateWXUserVehicle(userVehicleDTO);
      String publicNo = wxUserService.getWXUserDTOByOpenId(openId).getPublicNo();
      String content = "车牌号" + userVehicleDTO.getVehicleNo() + "绑定成功。";
      ServiceManager.getService(IWXMsgSender.class).sendCustomTextMsg(publicNo, userVehicleDTO.getOpenId(), content);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("绑定异常", false);
    }
  }


  /**
   * 保存编辑车辆信息
   *
   * @param userVehicleDTO
   * @return
   */
  @RequestMapping(params = "method=edit")
  @ResponseBody
  public Object editVehicle(HttpServletRequest request, WXUserVehicleDTO userVehicleDTO) {
    StopWatchUtil sw = new StopWatchUtil("wx:编辑车辆", "all");
    Result result = new Result();
    try {
      String openId = userVehicleDTO.getOpenId();
      String vehicleNo = StringUtil.toTrim(userVehicleDTO.getVehicleNo()).toUpperCase();
      result = validateBindVehicle(userVehicleDTO);
      WXUserVehicleDTO dbVehicleDTO = wxUserService.getWXUserVehicleDTOById(userVehicleDTO.getId());
      if (dbVehicleDTO == null) {
        return result.LogErrorMsg("您编辑的车辆，车牌号" + vehicleNo + "不存在或已经删除。");
      }
      WXUserVehicleDTO userVehicleDTOTemp = CollectionUtil.getFirst(wxUserService.getWXUserVehicle(openId, vehicleNo));
      if (userVehicleDTOTemp != null && !dbVehicleDTO.getId().equals(userVehicleDTOTemp.getId())) {
        return result.LogErrorMsg("车牌号" + vehicleNo + "已经绑定，请在菜单中查看。");
      }
      if (result == null || !result.isSuccess()) return result;
      userVehicleDTO.setDeleted(DeletedType.FALSE);
      result = wxUserService.saveOrUpdateWXUserVehicle(userVehicleDTO);
      String content = "车牌号" + userVehicleDTO.getVehicleNo() + "编辑成功";
      WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
      ServiceManager.getService(IWXMsgSender.class).sendCustomTextMsg(userDTO.getPublicNo(), userVehicleDTO.getOpenId(), content);
      sw.stopAndPrintLog();
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result.LogErrorMsg("绑定异常");
    }
  }

  @RequestMapping(params = "method=unBind")
  @ResponseBody
  public Object unBind(String openId, String vehicleNo) throws Exception {
    Result result = new Result();
    if (StringUtil.isEmpty(openId)) return result.LogErrorMsg("账户异常。");
    result = wxUserService.doUnBindVehicle(openId, vehicleNo);
    if (result.isSuccess()) {
      String publicNo = wxUserService.getWXUserDTOByOpenId(openId).getPublicNo();
      String content = "车牌号" + vehicleNo + ",解绑成功";
      ServiceManager.getService(IWXMsgSender.class).sendCustomTextMsg(publicNo, openId, content);
    }
    return result;
  }


  //校验测试图文消息
  private Result validateSendCustomNewMsg(WXArticleDTO wxArticleDTO) throws Exception {
    if (StringUtils.isBlank(wxArticleDTO.getPicUrl()) && wxArticleDTO.getImgFile() == null
      && ImageUtils.isImg(wxArticleDTO.getImgFile())) {
      return new Result("您要发送的微信消息未包含图片，请选择图片！", false);
    }
    if (StringUtil.isEmpty(wxArticleDTO.getWxReceiverGroupType())) {
      return new Result("请选择发送方式。", false);
    }
    if (StringUtils.isBlank(wxArticleDTO.getTitle())) {
      return new Result("请填写微信消息的标题。", false);
    }
    if (StringUtils.isBlank(wxArticleDTO.getDescription())) {
      return new Result("请填写微信消息的正文。", false);
    }
    if ("ALL_FANS".equals(wxArticleDTO.getWxReceiverGroupType())) {
      List<ShopWXUserDTO> shopWXUserDTOs = wxUserService.getShopWXUserByShopId(wxArticleDTO.getFromShopId());
      if (CollectionUtil.isEmpty(shopWXUserDTOs)) {
        return new Result("您的店铺暂无粉丝，无法发送。", false);
      }
      int count = wxService.countShopMonthWXMassMsg(wxArticleDTO.getFromShopId());
      if (count > 0) {
//        return new Result("本月您已群发"+count+"条信息，超过系统上限！",false);
      }
    }
    return new Result();
  }


  /**
   * 点击发送进入审核流程。演示通道直接发送
   */
  @RequestMapping(params = "method=saveArticle")
  @ResponseBody
  public Object saveArticle(HttpServletRequest request, WXArticleDTO articleDTO) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    articleDTO.setFromShopId(shopId);
    articleDTO.setUserId(WebUtil.getUserId(request));
    articleDTO.setUserName(WebUtil.getUserName(request));
    articleDTO.setImgFile(((MultipartRequest) request).getFile("imgFileTemp"));
    Result result = validateSendCustomNewMsg(articleDTO);
    if (result == null || !result.isSuccess()) {
      return result;
    }
    if ("OFFICIAL".equals(articleDTO.getWxReceiverGroupType())) {
      if (!WXHelper.validateWXShopAccount(shopId)) return new Result("您的微信帐号余额不足或套餐已过期，请即时充值", false);
      wxUserService.saveArticle(articleDTO);
      wxUserService.uploadShopArticleImg(articleDTO);
      return new Result("提交审核成功，请耐心等待,或及时联系一发客服，电话：0512-66733331", true, null);
    } else if ("TEST".equals(articleDTO.getWxReceiverGroupType())) {
      return wxUserService.saveAndSendCustomNewsMsg(articleDTO);
    } else {
      return new Result("请选择发送方式后再发送。", false, null);
    }
  }

  @RequestMapping(params = "method=getWXUserDTOByOpenId")
  @ResponseBody
  public Object getWXUserDTOByOpenId(String openId) throws Exception {
    Result result = new Result();
    if (StringUtil.isEmpty(openId)) return result.LogErrorMsg("账户异常。");
    WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
    result.setData(userDTO);
    return result;
  }

  @RequestMapping(params = "method=remarkWXUser")
  @ResponseBody
  public Object remarkWXUser(String openId, String remark) throws Exception {
    Result result = new Result();
    if (StringUtil.isEmpty(remark)) return result.LogErrorMsg("请输入备注名。");
    WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
    if (userDTO == null) return result.LogErrorMsg("账户异常。");
    userDTO.setRemark(remark);
    wxUserService.saveOrUpdateWXUser(userDTO);
    return result;
  }


  /**
   * 跳转到发送页面
   */
  @RequestMapping(params = "method=toSendMessagePage")
  public String toSendMessagePage(HttpServletRequest request, String articleId) {
    return "/wx/shopSendMsg";
  }

  /**
   * 跳转到微信账单
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=toWXShopBill")
  public String toWXShopBill(HttpServletRequest request, ModelMap modelMap) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    WXShopAccountDTO shopAccount = accountService.getWXShopAccountDTOByShopId(shopId);
    modelMap.put("shopAccount", shopAccount);
    modelMap.put("billStat", wxService.getWXShopBillStat(shopId));
    return "/wx/wxShopBill";
  }

  /**
   * 获取微信账单
   *
   * @param request
   * @param startPageNo
   * @param maxRows
   * @return
   */
  @RequestMapping(params = "method=getWXShopBill")
  @ResponseBody
  public Object getWXShopBill(HttpServletRequest request, int startPageNo, int maxRows) throws PageException {
    IWXService wxService = ServiceManager.getService(WXService.class);
    Long shopId = WebUtil.getShopId(request);
    int count = wxService.countWXShopBill(shopId);
    Pager pager = new Pager(count, startPageNo, maxRows);
    PagingListResult<WXUserDTO> pagingListResult = new PagingListResult<WXUserDTO>();
    pagingListResult.setPager(pager);
    List<WXShopBillDTO> billDTOs = wxService.getWXShopBill(shopId, pager);
    pagingListResult.setData(billDTOs);
    return pagingListResult;
  }

  @RequestMapping(params = "method=getWXShopAccount")
  @ResponseBody
  public Object getWXShopAccount(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    return accountService.getWXShopAccountDTOByShopId(shopId);
  }


  /**
   * 获取店铺微信粉丝列表
   */
  @RequestMapping(params = "method=getShopWXUsers")
  @ResponseBody
  public Object getShopWXUsers(HttpServletRequest request, WXUserSearchCondition searchCondition) throws PageException {
    Long shopId = WebUtil.getShopId(request);
    searchCondition.setShopId(shopId);
    int count = wxUserService.countMyFans(searchCondition);
    Pager pager = new Pager(count, NumberUtil.intValue(searchCondition.getCurrentPage()), searchCondition.getPageSize());
    PagingListResult<WXUserDTO> pagingListResult = new PagingListResult<WXUserDTO>();
    pagingListResult.setPager(pager);
    List<WXUserDTO> wxUserDTOs = wxUserService.getMyFans(searchCondition, pager);
    pagingListResult.setData(wxUserDTOs);
    return pagingListResult;
  }

  /**
   * 获取店铺消息模版
   */
  @RequestMapping(params = "method=getWXMsgTemplate")
  @ResponseBody
  public Object getWXMsgTemplate(HttpServletRequest request, WXArticleTemplateDTO wxArticleTemplateDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      IWXArticleService wxArticleService = ServiceManager.getService(IWXArticleService.class);
      int count = wxArticleService.getCountWXArticleJob(wxArticleTemplateDTO);
      int currentPage = NumberUtil.intValue(request.getParameter("startPageNo"), 1);
      int pageSize = NumberUtil.intValue(request.getParameter("pageSize"), 10);
      Pager pager = new Pager(count, currentPage, pageSize);
      ArrayList<WXArticleTemplateDTO> wxArticleTemplateDTOs = new ArrayList<WXArticleTemplateDTO>();
//      pagingListResult.setPager(pager);
      if (count > 0) {
        wxArticleTemplateDTOs = (ArrayList<WXArticleTemplateDTO>) wxArticleService.getWXArticleJobs(wxArticleTemplateDTO, pager);
      }
      PagingListResult<WXArticleTemplateDTO> pagingListResult = new PagingListResult<WXArticleTemplateDTO>(wxArticleTemplateDTOs, true, pager);
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("pager", pager);
      result.put("results", wxArticleTemplateDTOs);
      return pagingListResult;
    } catch (Exception e) {
      LOG.error("shopId:{}" + e.getMessage(), shopId, e);
      return null;
    }
  }


//------------------------------------微信发送记录开始--------------------------------------------------------------------------------

  /**
   * 跳转到微信发送记录列表界面
   */
  @RequestMapping(params = "method=toWxSent")
  public String toWxSent() {
    return "/wx/wxSendRecord";
  }

  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * 微信素材列表页面初始化
   */
  @RequestMapping(params = "method=inintSendRecord")
  public void listAdult(ModelMap model, HttpServletResponse response, HttpServletRequest request, Integer startPageNo, String title, String description) throws PageException {
    WXMsgDTO wxMsgDTO = new WXMsgDTO();
    /*标题*/
    wxMsgDTO.setTitle(title);
//    /*正文*/
    wxMsgDTO.setDescription(description);
    Long shopId = WebUtil.getShopId(request);
    wxMsgDTO.setFromShopId(shopId);
    /*总数*/
    int total = wxService.getCountAdultJob(wxMsgDTO, "allWXMsg");
    /*分页*/
    Pager pager = new Pager(total, NumberUtil.intValue(String.valueOf(startPageNo), 1));
    /*失败短信分页查询list*/
    List<WXMsgDTO> wxMsgDTOList = wxService.getAdultJobs(wxMsgDTO, pager, "allWXMsg");
    for (WXMsgDTO w : wxMsgDTOList) {
      //发送状态
      if (null != w.getStatus()) {
        w.setStatusName(w.getStatus().getName());
      } else {
        w.setStatusName("");
      }
      //发送时间
      if (null != w.getSendTime() && 0 != w.getSendTime()) {
        String dateString = formatter.format(w.getSendTime());
        w.setStime(dateString);
      } else {
        w.setStime("");
      }
      //送达人数
      if (null != w.getId() && !"".equals(w.getId())) {
        int count = wxService.getCountWXMsgReceiverById(w.getId());
        w.setReceiverCount(String.valueOf(count));
      }
      //收信人
      if (WXMCategory.MASS.equals(w.getCategory())) {
        w.setReceivers("全部粉丝");
      } else {
        StringBuffer sb = new StringBuffer();
        List<WXMsgReceiver> wxMsgReceivers = wxService.getWXMsgReceiverByMsgLocalId(w.getId());
        for (WXMsgReceiver wr : wxMsgReceivers) {
          WXUser wxUser = wxArticleService.getWeUserByOpenId(wr.getOpenId());
          sb.append(StringUtil.valueOf(wxUser.getNickName())).append(";");
        }
        w.setReceivers(sb.toString());
      }

    }
    String jsonStr = "";
    /*JSON*/
    jsonStr = JsonUtil.listToJson(wxMsgDTOList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=initAudit");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getShopWXMsgRecord")
  @ResponseBody
  public Object getShopWXMsgRecord(HttpServletRequest request, String startPageNo) throws PageException {
    Long shopId = WebUtil.getShopId(request);
    PagingListResult<WXMsgDTO> result = new PagingListResult<WXMsgDTO>();
    Pager pager = new Pager(wxService.countShopWXMsgRecord(shopId), NumberUtil.intValue(startPageNo, 1));
    List<WXMsgDTO> msgDTOs = wxService.getShopWXMsgRecord(shopId, pager);
    result.setResults(msgDTOs);
    result.setPager(pager);
    return result;
  }

  /**
   * 查看发送的微信信息
   */
  @RequestMapping(params = "method=toFindWxSend")
  public String toFindAdult(HttpServletRequest request, Long id, ModelMap model) {
    WXMsgDTO wxMsgDTO = wxService.getWXMsgDTOById(id);
    model.addAttribute("wxMsgDTO", wxMsgDTO);
    model.addAttribute("userName", WebUtil.getUserName(request));
    //收信人
    StringBuffer sb = new StringBuffer();
    List<WXMsgReceiver> wxMsgReceivers = wxService.getWXMsgReceiverByMsgLocalId(wxMsgDTO.getId());
    for (WXMsgReceiver wr : wxMsgReceivers) {
      WXUser wxUser = wxArticleService.getWeUserByOpenId(wr.getOpenId());
      sb.append(StringUtil.valueOf(wxUser.getNickName())).append(";");
    }
    model.addAttribute("receiver", sb.toString());
    return "/wx/findWxSend";
  }

  /**
   * 删除发送的微信信息（逻辑删除）
   *
   * @param id
   * @return
   * @throws IOException
   */
  @RequestMapping(params = "method=deleteWXMsg")
  @ResponseBody
  public Result deleteWXMsg(Long id) throws IOException {
    WXMsgDTO msgDTO = wxService.getWXMsgDTOById(id);
    Result result = new Result();
    if (msgDTO == null) return result.LogErrorMsg("消息不存在或已经删除。");
    msgDTO.setDeleted(DeletedType.TRUE);
    wxService.saveOrUpdateWXMsg(msgDTO);
    return result;
  }


  //-------------------------------------微信粉丝列表开始-------------------------------------------------------------------------------
  @RequestMapping(params = "method=toWXFans")
  public String toFan() {
    return "/wx/wxFanList";
  }

  @RequestMapping(params = "method=initFanList")
  @ResponseBody
  public Object getShopWxUserVehicles(HttpServletRequest request, WXUserSearchCondition condition) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    WXAccountDTO accountDTO = accountService.getWXAccountDTOByShopId(shopId);
    if (accountDTO == null) {
      accountDTO = accountService.getDefaultWXAccount();
    }
    condition.setShopId(WebUtil.getShopId(request));
    condition.setPublicNo(accountDTO.getPublicNo());
    if (StringUtil.isNotEmpty(condition.getKeyWord())) {
      condition.setNickName(condition.getKeyWord());
      condition.setRemark(condition.getKeyWord());
    }
    int count = wxUserService.countMyFans(condition);
    Pager pager = new Pager(count, NumberUtil.intValue(condition.getStartPageNo(), 1), condition.getPageSize());
    List<WXUserDTO> wxUserDTOs = wxUserService.getWxUserDTOsBySearchCondition(condition, pager);
    wxUserService.generateVehicleInfo(shopId, wxUserDTOs);
    PagingListResult<WXUserDTO> pagingListResult = new PagingListResult<WXUserDTO>(wxUserDTOs, true, pager);
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("pager", pager);
    result.put("results", pagingListResult);
    return result;

  }

  /**
   * 查看Vehicle
   */
  @RequestMapping(params = "method=toFindVehicle")
  public String toFindVehicle(String vehicleId, ModelMap model) {
    try {
      Vehicle vehicle = wxArticleService.getVehicleByVehicleId(vehicleId);
      model.addAttribute("vehicle", vehicle);
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=toFindVehicle");
      LOG.error(e.getMessage(), e);
    }
    return "/wx/vehicleDetail";
  }

  /**
   * 查看Customer
   */
  @RequestMapping(params = "method=toFindCustomer")
  public String toFindCustomer(String customerId, ModelMap model) {
    try {
      Customer customer = wxArticleService.getCustomerByCustomerId(customerId);
      model.addAttribute("customer", customer);
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=toFindCustomer");
      LOG.error(e.getMessage(), e);
    }
    return "/wx/customerDetail";
  }

  /**
   * 跳转到发送微信界面
   */
  @RequestMapping(params = "method=toWxSendMessage")
  public String toWxSendMessage(Long[] userIds, ModelMap model) {
    if (ArrayUtil.isEmpty(userIds)) return "/wx/shopSendMsg";
    List<WXUserDTO> userDTOs = new ArrayList<WXUserDTO>();
    for (Long userId : userIds) {
      WXUserDTO userDTO = wxUserService.getWXUserDTOById(userId);
      userDTOs.add(userDTO);
    }
    model.addAttribute("wxUsers", JsonUtil.listToJson(userDTOs));
    return "/wx/shopSendMsg";
  }

  @RequestMapping(params = "method=toWxShopConfig")
  public String toWxShopConfig(HttpServletRequest request, ModelMap modelMap) {
    Long shopId = WebUtil.getShopId(request);
    String shopName = ServiceManager.getService(IConfigService.class).getShopById(shopId).getName();
    modelMap.put("shopName", shopName);
    ShopConfig configDTO = ShopConfigCacheManager.getConfig(shopId, ShopConfigScene.WX_WELCOME_WORD);
    String content = configDTO != null ? configDTO.getValue() : null;
    if (StringUtil.isEmpty(content)) {
      content = WXConstant.HTML_WELCOME_WORD_DEFAULT.replace("{NAME}", shopName);
    }
    modelMap.put("welcomeWord", WXHelper.handleWelcomeWordToHtml(content));
    return "/wx/wxShopConfig";
  }


  @RequestMapping(params = "method=saveWelcomeWord")
  @ResponseBody
  public Result saveWelcomeWord(HttpServletRequest request, String content) {
    if (StringUtil.isEmpty(content)) {
      return new Result("内容不应为空。", false);
    }
    content = WXHelper.handleWelcomeWordFromHtml(content);
    Result result = new Result();
    IShopConfigService configService = ServiceManager.getService(ShopConfigService.class);
    Long shopId = WebUtil.getShopId(request);
    ShopConfigDTO configDTO = configService.getShopConfigDTOByShopIdAndScene(shopId, ShopConfigScene.WX_WELCOME_WORD);
    if (configDTO == null) {
      configDTO = new ShopConfigDTO();
      configDTO.setShopId(shopId);
      configDTO.setScene(ShopConfigScene.WX_WELCOME_WORD);
    }
    configDTO.setValue(content);
    configService.saveOrUpdateShopConfig(configDTO);
    return result;

  }

  @RequestMapping(params = "method=getPrinterSerialNo")
  @ResponseBody
  public String getPrinterSerialNo(String cameraSerialNo) {
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    CameraConfigDTO cameraConfigDTO = cameraService.getCameraConfigBySerialNo(cameraSerialNo);
    return cameraConfigDTO != null ? cameraConfigDTO.getPrinter_serial_no() : null;
  }


}
