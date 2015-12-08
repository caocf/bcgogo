package com.bcgogo.pushmessage.Sos;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.RescueDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.user.MileageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.sos.ShopSosInfoListResult;
import com.bcgogo.txn.dto.pushMessage.sos.SosInfoSearchConditionDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.IRescueService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
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
@RequestMapping("/shopSosInfo.do")
public class ShopSosInfoController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopSosInfoController.class);
  private static final String SHOW_SEARCH_PAGE = "/remind/pushMessage/sos/shopSosInfoList";

  @RequestMapping(params = "method=showShopSosInfoList")
  public String showShopSosInfoList(ModelMap model, HttpServletRequest request) {
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

  @RequestMapping(params = "method=searchShopSosInfoList")
  @ResponseBody
  public Object searchShopSosInfoList(HttpServletRequest request, HttpServletResponse response,  SosInfoSearchConditionDTO sosInfoSearchConditionDTO
                                        ) {
    int start = sosInfoSearchConditionDTO.getStartPageNo() == 0 ? 1 : sosInfoSearchConditionDTO.getStartPageNo();
    int pageSize = 10;
    ShopSosInfoListResult result = new ShopSosInfoListResult();
    Long shopId = WebUtil.getShopId(request);
    IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    List<RescueDTO> rescueDTOs = new ArrayList<RescueDTO>() ;
    try {
      int size = iRescueService.countGetRescueDTOs(shopId, sosInfoSearchConditionDTO);
      if(size>0){
        rescueDTOs = iRescueService.getRescueDTOsByShopId(shopId, sosInfoSearchConditionDTO);
        if (CollectionUtil.isNotEmpty(rescueDTOs)) {
          for (RescueDTO rescueDTO : rescueDTOs) {
            rescueDTO.setUploadTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, rescueDTO.getUploadTime()));
            rescueDTO.setIdStr(rescueDTO.getId().toString());
            if(StringUtil.isNotEmpty(rescueDTO.getAddr())&&rescueDTO.getAddr().indexOf("null")!=-1){
              rescueDTO.setAddr(rescueDTO.getAddr().replace("null",""));
            }
            AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(rescueDTO.getAppUserNo(), shopId));
            if(appUserCustomerDTO!=null){
              //客户信息
              CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(appUserCustomerDTO.getCustomerId(), shopId);
              if(customerDTO!=null){
                rescueDTO.setCustomerName(customerDTO.getName());
                rescueDTO.setCustomerMobile(customerDTO.getMobile());
                rescueDTO.setCustomerId(customerDTO.getId().toString());
              }
              //车辆信息
              AppVehicleDTO appVehicleDTO =  appUserVehicleObdService.getAppVehicleById(appUserCustomerDTO.getAppVehicleId());
              if (appVehicleDTO != null) {
                rescueDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
                rescueDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
                rescueDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
                if(appVehicleDTO.getCurrentMileage()!=null){
                  rescueDTO.setCurrentMileage(appVehicleDTO.getCurrentMileage().toString());
                }
                rescueDTO.setVehicleMobile(appVehicleDTO.getMobile());
                rescueDTO.setVehicleContact(appVehicleDTO.getContact());
              }
            }
          }
        }
      }
      Pager pager = new Pager(size, sosInfoSearchConditionDTO.getStartPageNo(), pageSize);
      result.setPager(pager);
      result.setTodayShopSosInfoList(rescueDTOs);
      result.setTodayTotalRows(size);
    }catch (Exception e){
      LOG.error("查询Sos出错！");
      LOG.error(e.getMessage(),e);
    }
    return result;
  }

  @RequestMapping(params = "method=deleteSosInfo")
  @ResponseBody
  public Object deleteSosInfo(HttpServletRequest request, Long[] id) {
    Result result = new Result();
    try {
      IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
      iRescueService.deleteShopSosInfo(id);
    } catch (Exception e) {
      LOG.error("shopSosInfo.do?method=deleteSosInfo,{},{}", e.getMessage(), e);
      result.setSuccess(false);
    }
    return result;
  }


}
