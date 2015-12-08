package com.bcgogo.pushmessage.mileage;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.api.MileageDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.user.MileageType;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.mileage.MileageInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.mileage.ShopMileageInfoListResult;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.IRescueService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.CollectionUtil;
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
@RequestMapping("/shopMileageInfo.do")
public class ShopMileageInfoController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopMileageInfoController.class);
  private static final String SHOW_SEARCH_PAGE = "/remind/pushMessage/mileage/shopMileageInfoList";

  @RequestMapping(params = "method=showShopMileageInfoList")
  public String showShopMileageInfoList(ModelMap model, HttpServletRequest request) {
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
      LOG.error("shopMileageInfo.do?method=showShopMileageInfoList,shopId:{}" + e.getMessage(), shopId, e);
    }
    return SHOW_SEARCH_PAGE;
  }

  @RequestMapping(params = "method=searchShopMileageInfoList")
  @ResponseBody
  public Object searchShopMileageInfoList(HttpServletRequest request, HttpServletResponse response,  MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO
                                        ) {
    int start = mileageInfoSearchConditionDTO.getStartPageNo() == 0 ? 1 : mileageInfoSearchConditionDTO.getStartPageNo();
    int pageSize = 10;
    ShopMileageInfoListResult result = new ShopMileageInfoListResult();
    Long shopId = WebUtil.getShopId(request);
//    Long shopId = 10000010066806580L;  //测试
    IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
    List<MileageDTO> mileageDTOs = new ArrayList<MileageDTO>();
    try {
      int size = iRescueService.countGetMileageDTOs(shopId, mileageInfoSearchConditionDTO);
      if(size>0){
        mileageDTOs = iRescueService.getMileageDTOsByShopId(shopId, mileageInfoSearchConditionDTO);
        if(CollectionUtil.isNotEmpty(mileageDTOs)){
          for(MileageDTO mileageDTO:mileageDTOs){
            //里程
            GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(mileageDTO.getAppUserNo());
            if (gsmVehicleDataDTO != null) {
              if(StringUtil.isNotEmpty(gsmVehicleDataDTO.getCurMil())){
                mileageDTO.setCurrentMileage(gsmVehicleDataDTO.getCurMil().toString());
              }
            }
            //
            AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(mileageDTO.getAppUserNo(), shopId));
            if(appUserCustomerDTO!=null){
              CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(appUserCustomerDTO.getCustomerId(), shopId);
              if(customerDTO!=null){
                mileageDTO.setCustomerName(customerDTO.getName());
                mileageDTO.setCustomerMobile(customerDTO.getMobile());
                mileageDTO.setCustomerId(customerDTO.getId().toString());
              }
            }
          }
        }
      }
      Pager pager = new Pager(size, mileageInfoSearchConditionDTO.getStartPageNo(), pageSize);
      result.setPager(pager);
      result.setTodayShopMileageInfoList(mileageDTOs);
      result.setTodayTotalRows(size);
    }catch (Exception e){
      LOG.error("查询里程出错！");
      LOG.error(e.getMessage(),e);
    }
    return result;
  }



}
