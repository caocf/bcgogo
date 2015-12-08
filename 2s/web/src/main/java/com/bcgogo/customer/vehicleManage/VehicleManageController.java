package com.bcgogo.customer.vehicleManage;

import com.bcgogo.api.*;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.etl.GsmPointDTO;
import com.bcgogo.etl.GsmVehicleInfoDTO;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.etl.service.IGsmPointService;
import com.bcgogo.etl.service.IGsmVehicleService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.JoinSearchConditionDTO;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.txn.service.app.IAppVehicleService;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.Coordinate;
import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.user.dto.AppointServiceDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IDriveLogService;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: lw
 * Date: 14-5-4
 * Time: 下午3:42
 */
@Controller
@RequestMapping("/vehicleManage.do")
public class VehicleManageController {
  private IUserService userService;
  private IVehicleService vehicleService;
  private IGsmVehicleService gsmVehicleService;
  public static final Logger LOG = LoggerFactory.getLogger(VehicleManageController.class);   //Log4j 日志


  public IGsmVehicleService getGsmVehicleService() {
    return gsmVehicleService == null ? ServiceManager.getService(IGsmVehicleService.class) : gsmVehicleService;
  }

  public IUserService getUserService() {
    return userService == null ? ServiceManager.getService(IUserService.class) : userService;
  }

  public IVehicleService getVehicleService() {
    return vehicleService == null ? ServiceManager.getService(IVehicleService.class) : vehicleService;
  }

  @RequestMapping(params = "method=toVehicleDetail")
  public String toVehicleDetail(HttpServletRequest request, HttpServletResponse response, ModelMap model, String customerIdStr, String vehicleIdStr, String edit, String fromPage) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    VehicleDTO vehicleDTO = null;
    if (shopId == null || StringUtil.isEmpty(customerIdStr) || StringUtil.isEmpty(vehicleIdStr) ||
      !NumberUtil.isLongNumber(customerIdStr) || !NumberUtil.isLongNumber(vehicleIdStr)) {
      return "/";
    }
    CustomerDTO customerDTO = getUserService().getCustomerDTOByCustomerId(Long.valueOf(customerIdStr), shopId);
    if (customerDTO == null) {
      throw new Exception("customer not found!");
    }
    CustomerVehicleDTO customerVehicleDTO = getUserService().getCustomerVehicleDTOByVehicleIdAndCustomerId(Long.valueOf(vehicleIdStr), Long.valueOf(customerIdStr));
    vehicleDTO = getVehicleService().findVehicleById(Long.valueOf(vehicleIdStr));
    if (customerVehicleDTO == null || vehicleDTO == null) {
      throw new Exception("vehicle not found!");
    }
    model.addAttribute("edit", edit);
    model.addAttribute("today", DateUtil.getTodayStr(DateUtil.YEAR_MONTH_DATE));

    model.addAttribute("fromPage", fromPage);
    model.addAttribute("customerDTO", customerDTO);
    List<AppointServiceDTO> appointServiceDTOs = getUserService().getAppointServiceByCustomerVehicle(shopId, Long.valueOf(vehicleIdStr), Long.valueOf(customerIdStr));

    try {
      ObdDTO obdDTO = ServiceManager.getService(IObdManagerService.class).getObdByImei(vehicleDTO.getGsmObdImei());
      GsmVehicleInfoDTO gsmVehicleInfoDTO = null;
      if (ObdType.MIRROR.equals(obdDTO.getObdType())) {
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        ObdUserVehicleDTO obdUserVehicleDTO = CollectionUtil.getFirst(appUserService.getOBDUserVehicleByObdIds(obdDTO.getId()));
        IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
        GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(obdUserVehicleDTO.getAppUserNo());
        if (gsmVehicleDataDTO != null) {
          if(StringUtil.isNotEmpty(gsmVehicleDataDTO.getCurMil())){
            vehicleDTO.setObdMileage(Double.valueOf(gsmVehicleDataDTO.getCurMil()));
          }
          gsmVehicleInfoDTO = gsmVehicleDataDTO.toGsmVehicleInfoDTO();
          AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(obdUserVehicleDTO.getAppUserNo()));
          gsmVehicleInfoDTO.setCacafe(StringUtil.valueOf(appVehicleDTO.getAvgOilWear()));
          double maxr = NumberUtil.doubleVal(gsmVehicleDataDTO.getRpm());
          double maxs = NumberUtil.doubleVal(gsmVehicleDataDTO.getVss());
          gsmVehicleInfoDTO.setMaxr(StringUtil.valueOf(maxr));
          gsmVehicleInfoDTO.setMaxs(StringUtil.valueOf(maxs));
        }
      } else {
        gsmVehicleInfoDTO = getGsmVehicleService().getGsmVehicleInfoByEmi(vehicleDTO.getGsmObdImei(), 1);
        if(gsmVehicleInfoDTO!=null){
          gsmVehicleInfoDTO.setCurMil(StringUtil.valueOf(vehicleDTO.getObdMileage()));
        }
      }
      model.addAttribute("customerVehicleResponse", new CustomerVehicleResponse(customerVehicleDTO, vehicleDTO, appointServiceDTOs));
      model.addAttribute("gsmVehicleInfoDTO", gsmVehicleInfoDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
    IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
    String faultCodes = shopFaultInfoService.getUnhandledFaultCodes(shopId, vehicleDTO.getLicenceNo());
    model.addAttribute("faultCodes", faultCodes);
    model.addAttribute("faultCodesShort", StringUtil.shortStr(faultCodes, 12, "..."));
    model.addAttribute("totalFaultNum", shopFaultInfoService.countShopFaultInfoByVehicleNo(shopId, vehicleDTO.getLicenceNo()));
    return "/customer/vehicle/vehicleDetail";
  }

  @RequestMapping(params = "method=toVehiclePosition")
  public String toVehiclePosition(HttpServletRequest request, HttpServletResponse response, ModelMap model, String customerIdStr, String vehicleIdStr) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }

    model.addAttribute("vehicleSearchConditionDTO", new VehicleSearchConditionDTO());

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);

    model.addAttribute("coordinateLat", shopDTO.getCoordinateLat());
    model.addAttribute("coordinateLon", shopDTO.getCoordinateLon());
    String city = AreaCacheManager.getAreaDTOByNo(shopDTO.getCity()).getName();
    model.addAttribute("city", city);

    try {
      if (NumberUtil.isLongNumber(customerIdStr) && NumberUtil.isLongNumber(vehicleIdStr)) {

        Customer customer = getUserService().getCustomerByCustomerId(Long.valueOf(customerIdStr), shopId);
        VehicleDTO vehicleDTO = getVehicleService().findVehicleById(Long.valueOf(vehicleIdStr));
        model.addAttribute("vehicleDTO", vehicleDTO);
        model.addAttribute("vehicleIdStr", vehicleIdStr);


        if (customer != null && vehicleDTO != null) {
          model.addAttribute("customerName", customer.getName());
          model.addAttribute("vehicleNo", vehicleDTO.getLicenceNo());

        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "/customer/vehicle/vehicleCurrentPosition";
  }

  @RequestMapping(params = "method=toVehicleDriveLog")
  public String toVehicleDriveLog(HttpServletRequest request, HttpServletResponse response, ModelMap model, String customerIdStr, String vehicleIdStr) throws Exception {

    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);

    model.addAttribute("coordinateLat", shopDTO.getCoordinateLat());
    model.addAttribute("coordinateLon", shopDTO.getCoordinateLon());
    String city = AreaCacheManager.getAreaDTOByNo(shopDTO.getCity()).getName();
    model.addAttribute("city", city);

    try {
      if (NumberUtil.isLongNumber(customerIdStr) && NumberUtil.isLongNumber(vehicleIdStr)) {

        Customer customer = getUserService().getCustomerByCustomerId(Long.valueOf(customerIdStr), shopId);
        VehicleDTO vehicleDTO = getVehicleService().findVehicleById(Long.valueOf(vehicleIdStr));
        model.addAttribute("vehicleDTO", vehicleDTO);
        model.addAttribute("vehicleIdStr", vehicleIdStr);

        if (customer != null && vehicleDTO != null) {
          model.addAttribute("customerName", customer.getName());
          model.addAttribute("vehicleNo", vehicleDTO.getLicenceNo());
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    model.addAttribute("vehicleSearchConditionDTO", new VehicleSearchConditionDTO());
    return "/customer/vehicle/vehicleDriveLog";
  }

  @RequestMapping(params = "method=updateVehicleInfo")
  @ResponseBody
  public Map updateVehicleInfo(HttpServletRequest request, HttpServletResponse response, CustomerVehicleResponse customerVehicleResponse) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {

      IProductService productService = ServiceManager.getService(IProductService.class);
      IAppVehicleService appVehicleService = ServiceManager.getService(IAppVehicleService.class);

      if (StringUtil.isNotEmpty(customerVehicleResponse.getCarDateStr())) {
        customerVehicleResponse.setCarDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, customerVehicleResponse.getCarDateStr()));
      }

      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      Long customerId = customerVehicleResponse.getCustomerId();
      CarDTO[] carArray = new CarDTO[1];
      carArray[0] = new CarDTO(customerVehicleResponse);

      List<VehicleDTO> vehicleDTOList = productService.saveOrUpdateVehicleInfo(shopId, userId, customerId, carArray);
      if (ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))) {
        appVehicleService.syncAppVehicle(vehicleDTOList);
      }
      VehicleDTO vehicleDTO = CollectionUtil.getFirst(vehicleDTOList);

      String vehicleIdStr = String.valueOf(vehicleDTO.getId());

      CustomerVehicleDTO customerVehicleDTO = getUserService().getCustomerVehicleDTOByVehicleIdAndCustomerId(Long.valueOf(vehicleIdStr), customerId);
      List<CustomerVehicleDTO> customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
      customerVehicleDTO.setMaintainMileagePeriod(customerVehicleResponse.getMaintainMileagePeriod());

      if (customerVehicleDTO.getLastMaintainMileage() != null && customerVehicleResponse.getMaintainMileagePeriod() != null) {
        customerVehicleDTO.setMaintainMileage((long) (customerVehicleDTO.getLastMaintainMileage() + customerVehicleResponse.getMaintainMileagePeriod()));
        customerVehicleResponse.setMaintainMileage(customerVehicleDTO.getMaintainMileage());
      }

      if (vehicleDTO.getObdMileage() != null && customerVehicleDTO.getMaintainMileage() != null) {
        customerVehicleDTO.setNextMaintainMileageAccess(customerVehicleDTO.getMaintainMileage() - vehicleDTO.getObdMileage());
        customerVehicleResponse.setNextMaintainMileageAccess(customerVehicleDTO.getNextMaintainMileageAccess());
      } else {
        customerVehicleDTO.setNextMaintainMileageAccess(null);
        customerVehicleResponse.setNextMaintainMileageAccess(null);
      }

      customerVehicleDTOs.add(customerVehicleDTO);
      getUserService().saveOrUpdateCustomerVehicle(customerVehicleDTOs);

      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
      returnMap.put("vehicleId", vehicleIdStr);
      returnMap.put("customerVehicleResponse", new CustomerVehicleResponse(customerVehicleDTO, vehicleDTOList.get(0), null));
      return returnMap;

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return returnMap;
  }

  @RequestMapping(params = "method=vehicleMaintainRegister")
  @ResponseBody
  public Result vehicleMaintainRegister(HttpServletRequest request, HttpServletResponse response, CustomerVehicleResponse customerVehicleResponse) {
    Result result = null;
    try {
      result = getVehicleService().maintainRegister(customerVehicleResponse);

      if (result.isSuccess()) {
        Long shopId = WebUtil.getShopId(request);
        Long vehicleId = customerVehicleResponse.getVehicleId();
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerVehicleResponse.getCustomerId());
        ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicleId);
      }
      return result;
    } catch (Exception e) {
      result = new Result(false);
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=queryVehiclePosition")
  @ResponseBody
  public Result queryVehiclePosition(HttpServletRequest request, HttpServletResponse response, VehicleSearchConditionDTO vehicleSearchConditionDTO) {
    Result result = new Result(false);

    Long shopId = WebUtil.getShopId(request);

    try {
      if (shopId == null) {
        throw new Exception("shopId is null!");
      }
      IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
      IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);

      vehicleSearchConditionDTO.setShopId(shopId);
      vehicleSearchConditionDTO.setSearchStrategies(new VehicleSearchConditionDTO.SearchStrategy[]{VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_OBD});
      vehicleSearchConditionDTO.setStatsFields(new String[]{VehicleSearchConditionDTO.StatsFields.OBD_ID.getName(), VehicleSearchConditionDTO.StatsFields.IS_MOBILE_VEHICLE.getName(), VehicleSearchConditionDTO.StatsFields.VEHICLE_TOTAL_CONSUME_AMOUNT.getName()});
      if (StringUtils.isNotBlank(vehicleSearchConditionDTO.getCustomerInfo())) {
        JoinSearchConditionDTO joinSearchConditionDTO = new JoinSearchConditionDTO();
        joinSearchConditionDTO.setShopId(shopId);
        joinSearchConditionDTO.setFromColumn("id");
        joinSearchConditionDTO.setToColumn("customer_id");
        joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue());
        joinSearchConditionDTO.setCustomerOrSupplier(new String[]{"customer"});
        joinSearchConditionDTO.setCustomerOrSupplierInfo(vehicleSearchConditionDTO.getCustomerInfo());
        vehicleSearchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
      }

      Map<String, String> imeiMap = ServiceManager.getService(ISearchVehicleService.class).queryVehicleNoForVehiclePosition(vehicleSearchConditionDTO);
      if (MapUtils.isEmpty(imeiMap)) {
        result.setMsg("车辆信息输入不正确,查询不到有OBD的车辆");
        result.setSuccess(false);
        return result;
      } else if (imeiMap.keySet().size() > 20) {
        result.setSuccess(false);
        result.setMsg("请输入更精确的查询条件!");
        return result;
      }


      List<GsmPointDTO> gsmPointDTOs = gsmPointService.getLastGsmPointByImei(imeiMap.keySet());

      if (CollectionUtil.isEmpty(gsmPointDTOs)) {
        result.setSuccess(false);
        result.setMsg("暂无可定位的车辆");
        return result;
      }

      Coordinate[] coordinates = new Coordinate[gsmPointDTOs.size()];
      for (int i = 0; i < gsmPointDTOs.size(); i++) {
        GsmPointDTO gsmPointDTO = gsmPointDTOs.get(i);

        Coordinate coordinate = new Coordinate();
        coordinate.setLat(gsmPointDTO.getGpsLat());
        coordinate.setLng(gsmPointDTO.getGpsLon());
        coordinates[i] = coordinate;
      }
      List<Coordinate> coordinateList = geocodingService.coordinateGspToBaiDu(coordinates);

      if (CollectionUtil.isEmpty(coordinateList)) {
        result.setSuccess(false);
        result.setMsg("暂无可定位的车辆");
        return result;
      }
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < gsmPointDTOs.size(); i++) {
        GsmPointDTO gsmPointDTO = gsmPointDTOs.get(i);
        Coordinate coordinate = null;
        if (i < coordinateList.size()) {
          coordinate = coordinateList.get(i);
        }
        if (coordinate == null) {
          gsmPointDTO.setBaiDuLat(gsmPointDTO.getGpsLat());
          gsmPointDTO.setBaiDuLon(gsmPointDTO.getGpsLon());
        } else {
          gsmPointDTO.setBaiDuLat(coordinate.getY());
          gsmPointDTO.setBaiDuLon(coordinate.getX());

        }

        gsmPointDTO.setVehicleNo(imeiMap.get(gsmPointDTO.getEmi()));

        AddressComponent addressComponent = geocodingService.gpsToAddress(gsmPointDTO.getGpsLat(), gsmPointDTO.getGpsLon());
        if (addressComponent == null) {
          continue;
        }
        gsmPointDTO.setAddress(addressComponent.getStreetInfo());
        sb.append(gsmPointDTO.getBaiDuLat()).append("__").append(gsmPointDTO.getBaiDuLon()).append("__");
        sb.append(gsmPointDTO.getUploadServerTimeStr()).append("__").append(URLEncoder.encode(gsmPointDTO.getAddress(), "UTF-8")).append("__");
        sb.append(URLEncoder.encode(gsmPointDTO.getVehicleNo(), "UTF-8"));

        if (i != gsmPointDTOs.size() - 1) {
          sb.append(",,,");
        }

      }
      result.setData(sb.toString());
      result.setSuccess(true);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    result.setMsg("数据异常，请刷新页面");
    result.setSuccess(false);
    return result;
  }

  @RequestMapping(params = "method=queryVehicleDriveLog")
  @ResponseBody
  public Result queryVehicleDriveLog(HttpServletRequest request, HttpServletResponse response, VehicleSearchConditionDTO vehicleSearchConditionDTO) {
    Result result = new Result(false);
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return result;
    }

    try {
      vehicleSearchConditionDTO.setShopId(shopId);

      List<VehicleDTO> vehicleDTOList = getVehicleService().getVehicleByCondition(vehicleSearchConditionDTO);
      if (CollectionUtil.isEmpty(vehicleDTOList)) {
        result.setMsg("车辆信息输入不正确,查询不到有OBD的车辆");
        return result;
      } else if (vehicleDTOList.size() > 1) {
        result.setMsg("车辆信息输入不精确,请输入精准的车辆信息");
        return result;
      }
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);

      String imei = CollectionUtil.getFirst(vehicleDTOList).getGsmObdImei();
      if (StringUtil.isEmpty(imei)) {
        result.setMsg("车辆信息输入不正确,查询不到有OBD的车辆");
        return result;
      }
      int count = driveLogService.countDriveLogDTOsByImeiTime(imei, vehicleSearchConditionDTO.getLastDriveTimeStart(), vehicleSearchConditionDTO.getLastDriveTimeEnd());
      Pager pager = new Pager(count, vehicleSearchConditionDTO.getStartPageNo(), vehicleSearchConditionDTO.getMaxRows());

      List<DriveLogDTO> driveLogDTOList = null;
      if (pager.getPageRows() > 0) {
        driveLogDTOList = driveLogService.getDriveLogDTOsByImeiTime(imei, vehicleSearchConditionDTO.getLastDriveTimeStart(), vehicleSearchConditionDTO.getLastDriveTimeEnd(), pager);

        Set<Long> driveLogIdSet = new HashSet<Long>();
        if (CollectionUtil.isNotEmpty(driveLogDTOList)) {
          for (DriveLogDTO driveLogDTO : driveLogDTOList) {
            driveLogIdSet.add(driveLogDTO.getId());
            AddressComponent addressComponent = geocodingService.gpsToAddress(driveLogDTO.getStartLat(), driveLogDTO.getStartLon());
            if (addressComponent == null) {
              continue;
            }
            driveLogDTO.setStartPlace(addressComponent.getStreetInfo());
            driveLogDTO.setDetailStartPlace(addressComponent.getStreetNumberInfo());
            addressComponent = geocodingService.gpsToAddress(driveLogDTO.getEndLat(), driveLogDTO.getEndLon());
            if (addressComponent == null) {
              continue;
            }
            driveLogDTO.calculateAverageSpeed();
            driveLogDTO.setEndPlace(addressComponent.getStreetInfo());
            driveLogDTO.setDetailEndPlace(addressComponent.getStreetNumberInfo());
          }
        }
      }
      List list = new ArrayList();

      list.add(driveLogDTOList);
      list.add(pager);
      result.setData(list);
      result.setSuccess(true);
      return result;


    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    result.setMsg("数据异常，请刷新页面");
    return result;

  }

}
