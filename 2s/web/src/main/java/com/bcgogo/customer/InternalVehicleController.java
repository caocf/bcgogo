package com.bcgogo.customer;

import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ShopInternalVehicleDriveStatDTO;
import com.bcgogo.user.dto.ShopInternalVehicleRequestDTO;
import com.bcgogo.user.service.intenernalVehicle.IInternalVehicleService;
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
 * Created by XinyuQiu on 14-12-15.
 */
@Controller
@RequestMapping("/internalVehicle.do")
public class InternalVehicleController {
  public static final Logger LOG = LoggerFactory.getLogger(InternalVehicleController.class);

  @RequestMapping(params = "method=internalVehicleListPage")
  public String vehicleListPage(HttpServletRequest request, HttpServletResponse response) {
    return "/customer/vehicle/internalVehicleList";
  }

  @RequestMapping(params = "method=internalVehicleListContentPage")
  public String internalVehicleListContentPage(HttpServletRequest request, ModelMap model, HttpServletResponse response) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));

    model.addAttribute("coordinateLat",shopDTO.getCoordinateLat());
    model.addAttribute("coordinateLon",shopDTO.getCoordinateLon());
    String city  = AreaCacheManager.getAreaDTOByNo(shopDTO.getCity()).getName();
    model.addAttribute("city",city);
    return "/customer/vehicle/internalVehicleListContent";
  }

  @ResponseBody
  @RequestMapping(params = "method=getQueryInternalVehicleNo")
  public Object getQueryInternalVehicleNo(HttpServletRequest request, HttpServletResponse response, String q) {
    IInternalVehicleService internalVehicleService = ServiceManager.getService(IInternalVehicleService.class);
    try {
      return internalVehicleService.getQueryInternalVehicleNo(WebUtil.getShopId(request), q);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  @ResponseBody
  @RequestMapping(params = "method=getInternalVehicleList")
  public Object getInternalVehicleList(HttpServletRequest request, HttpServletResponse response,
                                       ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    Map<String, Object> result = new HashMap<String, Object>();
    IInternalVehicleService internalVehicleService = ServiceManager.getService(IInternalVehicleService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      shopInternalVehicleRequestDTO.setShopId(shopId);
      internalVehicleService.generateSearchInfo(shopInternalVehicleRequestDTO);
      int total = internalVehicleService.countShopDriveLogStat(shopInternalVehicleRequestDTO);
      result.put("total", total);
      List<ShopInternalVehicleDriveStatDTO> driveStatDTOList = new ArrayList<ShopInternalVehicleDriveStatDTO>();
      if (total > 0) {
        driveStatDTOList = internalVehicleService.getShopDriveLogStat(shopInternalVehicleRequestDTO);
      }
      result.put("rows", driveStatDTOList);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @ResponseBody
  @RequestMapping(params = "method=getInternalVehicleDriveList")
  public Object getInternalVehicleDriveList(HttpServletRequest request, HttpServletResponse response,
                                       ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    Map<String, Object> result = new HashMap<String, Object>();
    IInternalVehicleService internalVehicleService = ServiceManager.getService(IInternalVehicleService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      shopInternalVehicleRequestDTO.setShopId(shopId);
      internalVehicleService.generateSearchInfo(shopInternalVehicleRequestDTO);
      int total = internalVehicleService.countShopDriveLog(shopInternalVehicleRequestDTO);
      result.put("total", total);
      List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
      if (total > 0) {
        driveLogDTOs = internalVehicleService.getShopDriveLogDTOs(shopInternalVehicleRequestDTO);
      }
      result.put("rows", driveLogDTOs);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


}
