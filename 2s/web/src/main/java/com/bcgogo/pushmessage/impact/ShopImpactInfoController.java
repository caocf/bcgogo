package com.bcgogo.pushmessage.impact;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.ImpactDetailDTO;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.etl.ImpactVideoExpDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.impact.ImpactInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.impact.ShopImpactInfoListResult;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IImpactService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 上午11:47
 */
@Controller
@RequestMapping("/shopImpactInfo.do")
public class ShopImpactInfoController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopImpactInfoController.class);
  private static final String SHOW_SEARCH_PAGE = "/remind/pushMessage/impact/shopImpactInfoList";

  @RequestMapping(params = "method=showShopImpactInfoList")
  public String showShopImpactInfoList(ModelMap model, HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    try {
      model.addAttribute("shopFaultInfoId", request.getParameter("shopFaultInfoId"));
      model.addAttribute("faultAlertTypes", FaultAlertType.values());
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
      LOG.error("shopFaultInfo.do?method=showShopFaultInfoList,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_SEARCH_PAGE;
  }

  @RequestMapping(params = "method=searchShopImpactInfoList")
  @ResponseBody
  public Object searchShopImpactInfoList(HttpServletRequest request, HttpServletResponse response,  ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO
                                        ) {
    Long shopId = WebUtil.getShopId(request);
    int start = impactInfoSearchConditionDTO.getStartPageNo() == 0 ? 1 :  impactInfoSearchConditionDTO.getStartPageNo();
    int pageSize = 10;
    IAppUserService iAppUserService = ServiceManager.getService(IAppUserService.class);
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    AddressComponent addressComponent = null;
    IImpactService iImpactService = ServiceManager.getService(IImpactService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IGeocodingService iGeocodingService = ServiceManager.getService(IGeocodingService.class);
    ShopImpactInfoListResult result = new ShopImpactInfoListResult();
    List<ImpactVideoExpDTO> impactVideoExpDTOs = new ArrayList<ImpactVideoExpDTO>();
    Set<Long> customerIds = new HashSet<Long>();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    CustomerDTO customerDTO = null;
    AppUserCustomerDTO appUserCustomerDTO =null;
    int size = 0;
    AppVehicleDTO appVehicleDTO = null;

    try{
      size = iImpactService.countGetImpactVideoExpDTOs_page(shopId.toString(),impactInfoSearchConditionDTO);
      if(size>0){
        impactVideoExpDTOs= iImpactService.getImpactVideoExpDTOByAppUserNo_page(shopId.toString(), impactInfoSearchConditionDTO);
        if (CollectionUtil.isNotEmpty(impactVideoExpDTOs)) {
          for (ImpactVideoExpDTO impactVideoExpDTO : impactVideoExpDTOs) {
            impactVideoExpDTO.setImpactIdStr(impactVideoExpDTO.getImpactId().toString());
            impactVideoExpDTO.setImpactVideoIdStr(impactVideoExpDTO.getImpactVideoId().toString());
            appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(impactVideoExpDTO.getAppUserNo()));
            if(appVehicleDTO!=null){
              impactVideoExpDTO.setVehicleNo(appVehicleDTO.getVehicleNo());//碰撞车牌号
              impactVideoExpDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
              impactVideoExpDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
            }
            addressComponent = iGeocodingService.gpsToAddress(impactVideoExpDTO.getLatitude(), impactVideoExpDTO.getLongitude());
            if (addressComponent != null) {
              impactVideoExpDTO.setAddress(addressComponent.getDistrict() + addressComponent.getStreet()); //碰撞地址
            }
            impactVideoExpDTO.setUploadTimeDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, impactVideoExpDTO.getUploadTime()));
            impactVideoExpDTO.setUploadTimeStr(impactVideoExpDTO.getUploadTime().toString());
            impactVideoExpDTO.setUrl(impactService.getImpactVideoUrl(impactVideoExpDTO.getImpactVideoId()));
            appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(impactVideoExpDTO.getAppUserNo(), shopId));
            if(appUserCustomerDTO!=null){
              customerDTO = customerService.getCustomerById(appUserCustomerDTO.getCustomerId());
              if(customerDTO!=null){
                impactVideoExpDTO.setCustomerId(customerDTO.getId().toString());
                impactVideoExpDTO.setCustomerName(customerDTO.getName());
                impactVideoExpDTO.setCustomerMobile(customerDTO.getMobile());
              }
             }
          }
        }
      }
      Pager pager = new Pager(size, impactInfoSearchConditionDTO.getStartPageNo(), pageSize);
      result.setPager(pager);
      result.setTodayShopImpactInfoList(impactVideoExpDTOs);
      result.setTodayTotalRows(size);
    }catch (Exception e){
      LOG.error("查询碰撞视频类出错！");
      LOG.error(e.getMessage(),e);
    }
    return result;
  }


  @RequestMapping(params = "method=deleteImpactInfoCode")
  @ResponseBody
  public Object deleteImpactInfoCode(HttpServletRequest request, Long[] id) {
    Result result = new Result();
    try {
      IImpactService iImpactService = ServiceManager.getService(IImpactService.class);
      iImpactService.deleteShopImpactInfo(id);
    } catch (Exception e) {
      LOG.error("shopImpactInfo.do?method=deleteFaultInfoCode,{},{}", e.getMessage(), e);
      result.setSuccess(false);
    }
    return result;
  }

  @RequestMapping(params = "method=getImpactDetail")
  @ResponseBody
  public Object getImpactDetail(HttpServletRequest request, String impactId,String uploadTime) {
    Long shopId = WebUtil.getShopId(request);
    AddressComponent addressComponent = null;
    IGeocodingService iGeocodingService = ServiceManager.getService(IGeocodingService.class);
    IImpactService iImpactService = ServiceManager.getService(IImpactService.class);
    ImpactDetailDTO impactDetailDTO = null;
    try {
//      impactDetailDTO =CollectionUtil.getFirst(iImpactService.getImpact_detail(shopId, impactId, Long.valueOf(uploadTime)));
      impactDetailDTO = iImpactService.getImpact_detailByIdAndTime(shopId, impactId, Long.valueOf(uploadTime));
      if(impactDetailDTO!=null){
        addressComponent = iGeocodingService.gpsToAddress(impactDetailDTO.getLat(), impactDetailDTO.getLon());
        if (addressComponent != null) {
          impactDetailDTO.setAddress(addressComponent.getDistrict() + addressComponent.getStreet() + addressComponent.getStreetNumber()); //碰撞地址
        }
        impactDetailDTO.setUploadTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, Long.valueOf(uploadTime)));
      }
    } catch (Exception e) {
      LOG.error("shopImpactInfo.do?method=getImpactDetail,{},{}", e.getMessage(), e);
    }
    return impactDetailDTO;
  }

}
