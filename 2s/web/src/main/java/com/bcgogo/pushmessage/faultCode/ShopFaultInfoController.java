package com.bcgogo.pushmessage.faultCode;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.model.MessageTemplate;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.ShopFaultInfoListResult;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 上午11:47
 */
@Controller
@RequestMapping("/shopFaultInfo.do")
public class ShopFaultInfoController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopFaultInfoController.class);
  private static final String SHOW_SEARCH_PAGE = "/remind/pushMessage/faultCode/shopFaultInfoList";

  @RequestMapping(params = "method=showShopFaultInfoList")
  public String showEnquiryOrderList(ModelMap model, HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    try {
      model.addAttribute("shopFaultInfoId", request.getParameter("shopFaultInfoId"));
      model.addAttribute("faultAlertTypes", FaultAlertType.values());
      model.addAttribute("vehicleNo",request.getParameter("vehicleNo"));
      model.addAttribute("scene",request.getParameter("scene"));
      String scene = request.getParameter("scene");
      if("ALL".equals(scene)){
        model.addAttribute("isUntreated","YES");
        model.addAttribute("isSendMessage","YES");
        model.addAttribute("isCreateAppointOrder","YES");
        model.addAttribute("isDeleted","YES");
      }else {
        model.addAttribute("isUntreated","YES");
//        model.addAttribute("isSendMessage","NO");
//        model.addAttribute("isCreateAppointOrder","NO");
//        model.addAttribute("isDeleted","NO");
      }
    } catch (Exception e) {
      LOG.error("shopFaultInfo.do?method=showShopFaultInfoList,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_SEARCH_PAGE;
  }

  @RequestMapping(params = "method=searchShopFaultInfoList")
  @ResponseBody
  public Object searchShopFaultInfoList(HttpServletRequest request, HttpServletResponse response,
                                        FaultInfoSearchConditionDTO searchCondition) {
    Long shopId = WebUtil.getShopId(request);
    searchCondition.setShopId(shopId);
    IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
    try {
      searchCondition.setShopId(WebUtil.getShopId(request));
      return shopFaultInfoService.searchShopFaultInfoList(searchCondition);
    } catch (Exception e) {
      LOG.error("shopFaultInfo.do?method=searchShopFaultInfoList,shopId:{}" + e.getMessage(), shopId, e);
      Pager pager;
      try {
        pager = new Pager(0, 1, searchCondition.getMaxRows());
      } catch (PageException pe) {
        LOG.error(pe.getMessage(), pe);
        pager = new Pager();
      }
      return new ShopFaultInfoListResult(pager);
    }
  }

  @RequestMapping(params = "method=getFaultInfoCodeSMSTemplate")
  @ResponseBody
  public Object getFaultInfoCodeSMSTemplate(HttpServletRequest request, String code, String time, FaultAlertType faultAlertType,String faultAlertTypeValue) {
    Map<String, Object> map = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
      map = shopFaultInfoService.getSopFaultInfoMsgContent(shopId,code,time,faultAlertType,faultAlertTypeValue,request.getParameter("licenceNo"));
    } catch (Exception e) {
      LOG.error("shopFaultInfo.do?method=getMsgTemplateByType,{},{}", e.getMessage(), e);
    }
    return map;
  }

  @RequestMapping(params = "method=sendSMSAndAppNotice")
  @ResponseBody
  public Object sendSMSAndAppNotice(HttpServletRequest request, String content,
                                    String appUserNo, Long customerId, String mobile,
                                    String shopName, Boolean sendSms, Boolean sendApp, Long id) {
    Result result = new Result();
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      ISendSmsService sendSmsService = ServiceManager.getService(ISendSmsService.class);
      result = sendSmsService.sendSms(shopId, userId, content, sendApp, sendSms, true,appUserNo,mobile );
      if(result!= null && result.isSuccess()){
        ServiceManager.getService(IShopFaultInfoService.class).updateShopFaultInfo2SendMessage(id);
      }
    } catch (Exception e) {
      LOG.info("shopFaultInfo.do?method=sendSMSAndAppNotice");
      LOG.error(e.getMessage(), e);
      result.setMsg(false,"网络异常！");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteFaultInfoCode")
  @ResponseBody
  public Object deleteFaultInfoCode(HttpServletRequest request, Long[] id) {
    Result result = new Result();
    try {
      IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
      shopFaultInfoService.deleteShopFaultInfo(id);
    } catch (Exception e) {
      LOG.error("shopFaultInfo.do?method=deleteFaultInfoCode,{},{}", e.getMessage(), e);
      result.setSuccess(false);
    }
    return result;
  }

  @RequestMapping(params = "method=getShopFaultInfoVehicleNoSuggestion")
  @ResponseBody
  public Object getShopFaultInfoVehicleNoSuggestion(HttpServletRequest request, String uuid, String keyword) {
    Long shopId = WebUtil.getShopId(request);
    List<String> list = ServiceManager.getService(IShopFaultInfoService.class).getShopFaultInfoVehicleNoSuggestion(shopId, keyword);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    for (String str : list) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("label", str);
      result.add(map);
    }
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("uuid", uuid);
    map.put("data", result);
    return map;
  }

  @RequestMapping(params = "method=getShopFaultInfoMobileSuggestion")
  @ResponseBody
  public Object getShopFaultInfoMobileSuggestion(HttpServletRequest request, String uuid, String keyword) {
    Long shopId = WebUtil.getShopId(request);
    List<String> list = ServiceManager.getService(IShopFaultInfoService.class).getShopFaultInfoMobileSuggestion(shopId, keyword);
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    for (String str : list) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("label", str);
      result.add(map);
    }
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("uuid", uuid);
    map.put("data", result);
    return map;
  }




}
