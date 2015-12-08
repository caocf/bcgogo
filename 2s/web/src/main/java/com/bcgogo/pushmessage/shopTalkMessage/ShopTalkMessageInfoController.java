package com.bcgogo.pushmessage.shopTalkMessage;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.user.MileageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.ShopTalkMessageDTO;
import com.bcgogo.txn.dto.pushMessage.shopTalkMessage.ShopTalkMessageInfoListResult;
import com.bcgogo.txn.dto.pushMessage.shopTalkMessage.TalkMessageInfoSearchConditionDTO;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 上午11:47
 */
@Controller
@RequestMapping("/shopTalkMessageInfo.do")
public class ShopTalkMessageInfoController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopTalkMessageInfoController.class);
  private static final String SHOW_SEARCH_PAGE = "/remind/pushMessage/shopTalkMessage/shopTalkMessageInfoList";

  @RequestMapping(params = "method=showShopTalkMessageInfoList")
  public String showShopTalkMessageInfoList(ModelMap model, HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    try {
      model.addAttribute("shopFaultInfoId", request.getParameter("shopFaultInfoId"));
      model.addAttribute("mileageTypes", MileageType.values());
      model.addAttribute("vehicleNo", request.getParameter("vehicleNo"));
      model.addAttribute("scene", request.getParameter("scene"));
      String scene = request.getParameter("scene");
      if ("ALL".equals(scene)) {
        model.addAttribute("isUntreated", "YES");
        model.addAttribute("isSendMessage", "YES");
        model.addAttribute("isCreateAppointOrder", "YES");
        model.addAttribute("isDeleted", "YES");
      } else {
        model.addAttribute("isUntreated", "YES");
      }
    } catch (Exception e) {
      LOG.error("shopSosInfo.do?method=showShopSosInfoList,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_SEARCH_PAGE;
  }

  @RequestMapping(params = "method=searchShopTalkMessageInfoList")
  @ResponseBody
  public Object searchShopTalkMessageInfoList(HttpServletRequest request, HttpServletResponse response,  TalkMessageInfoSearchConditionDTO talkMessageInfoSearchConditionDTO
                                        ) {
    int pageSize = 10;
    Long shopId = WebUtil.getShopId(request);
    ShopTalkMessageInfoListResult result = new ShopTalkMessageInfoListResult();
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    List<ShopTalkMessageDTO> talkMessageDTOs = new ArrayList<ShopTalkMessageDTO>();
    try {
      int count = pushMessageService.countShopTalkMessageList(null,talkMessageInfoSearchConditionDTO.getVehicleNo(), shopId);
      if(count>0){
        talkMessageDTOs = pushMessageService.getShopTalkMessageDTO(null,talkMessageInfoSearchConditionDTO.getVehicleNo(), shopId, talkMessageInfoSearchConditionDTO.getStartPageNo()-1, talkMessageInfoSearchConditionDTO.getMaxRows());
        if(CollectionUtil.isNotEmpty(talkMessageDTOs)){
          for(ShopTalkMessageDTO shopTalkMessageDTO:talkMessageDTOs){
            AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(shopTalkMessageDTO.getAppUserNo(), shopId));
            if(appUserCustomerDTO!=null){
              CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(appUserCustomerDTO.getCustomerId(), shopId);
              if(customerDTO!=null){
                shopTalkMessageDTO.setCustomerName(customerDTO.getName());
                shopTalkMessageDTO.setCustomerMobile(customerDTO.getMobile());
                shopTalkMessageDTO.setCustomerIdStr(customerDTO.getId().toString());
              }
              AppVehicleDTO appVehicleDTO =  appUserVehicleObdService.getAppVehicleById(appUserCustomerDTO.getAppVehicleId());
              if (appVehicleDTO != null) {
                shopTalkMessageDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
                shopTalkMessageDTO.setVehicleMobile(appVehicleDTO.getMobile());
                shopTalkMessageDTO.setVehicleContact(appVehicleDTO.getContact());
              }
            }
          }
        }
      }
      Pager pager = new Pager(count, talkMessageInfoSearchConditionDTO.getStartPageNo(), pageSize);
      result.setPager(pager);
      result.setTodayShopTalkMessageInfoList(talkMessageDTOs);
      result.setTodayTotalRows(count);
    }catch (Exception e){
      LOG.error("查询互动类（ShopTalkMessage）出错！");
      LOG.error(e.getMessage(),e);
    }
    return result;
  }

//  @RequestMapping(params = "method=deleteSosInfo")
//  @ResponseBody
//  public Object deleteSosInfo(HttpServletRequest request, Long[] id) {
//    Result result = new Result();
//    try {
//      IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
//      iRescueService.deleteShopSosInfo(id);
//    } catch (Exception e) {
//      LOG.error("shopSosInfo.do?method=deleteSosInfo,{},{}", e.getMessage(), e);
//      result.setSuccess(false);
//    }
//    return result;
//  }


}
