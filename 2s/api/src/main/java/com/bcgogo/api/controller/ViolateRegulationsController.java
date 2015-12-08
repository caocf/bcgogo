package com.bcgogo.api.controller;

import com.bcgogo.api.ApiArea;
import com.bcgogo.api.ApiMirrorArea;
import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.api.response.ApiVehicleViolateRegulationResponse;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.service.IAreaService;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.service.IViolateRegulationService;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.product.service.ILicensePlateService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IAppVehicleService;
import com.bcgogo.user.model.app.AppVehicle;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-10-22
 * Time: 下午5:44
 * 车辆违章管理
 */
@Controller
@RequestMapping("/violateRegulations/*")
public class ViolateRegulationsController {
  private static final Logger LOG = LoggerFactory.getLogger(ViolateRegulationsController.class);

  @ResponseBody
  @RequestMapping(value = "/juhe/condition/{vehicleNo}/{baiduCityCodes}/{juheCityCodes}", method = RequestMethod.GET)
  public ApiResponse list(HttpServletRequest request, HttpServletResponse response,
                          @PathVariable("vehicleNo") String vehicleNo,
                          @PathVariable("baiduCityCodes") String baiduCityCodes,
                          @PathVariable("juheCityCodes") String juheCityCodes) throws Exception {
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      baiduCityCodes = (StringUtil.isEmptyAppGetParameter(baiduCityCodes) ? "" : baiduCityCodes);
      juheCityCodes = (StringUtil.isEmptyAppGetParameter(juheCityCodes) ? "" : juheCityCodes);
      vehicleNo = (StringUtil.isEmptyAppGetParameter(vehicleNo) ? "" : vehicleNo);
      Integer[] integers = null;
      if (StringUtil.isNotEmpty(baiduCityCodes))
        integers = StringUtil.parseIntegerArray(baiduCityCodes.split(","));
      String[] strings = null;
      if (StringUtil.isNotEmpty(juheCityCodes))
        strings = juheCityCodes.split(",");
      IViolateRegulationService violateRegulationService = ServiceManager.getService(IViolateRegulationService.class);
      Map<String, AppVehicleDTO> map = ServiceManager.getService(IAppUserVehicleObdService.class).getAppVehicleMapByAppUserNo(appUserNo);
      List<String> vehicleNoList = new ArrayList<String>();
      for (AppVehicleDTO appVehicleDTO : map.values()) {
        vehicleNoList.add(appVehicleDTO.getVehicleNo());
      }
      Map<String, AreaDTO> areaDTOMap = ServiceManager.getService(ILicensePlateService.class)
        .getAreaMapByLicenseNo(vehicleNoList.toArray(new String[vehicleNoList.size()]));
      String[] juhe = getJuheCityCodes(integers, strings, map, areaDTOMap);
      ApiResponse apiResponse = violateRegulationService.getJuheViolateRegulationSerachCondition(appUserNo, juhe, map, areaDTOMap);
      apiResponse.setDebug("vehicleNo:" + vehicleNo + ",baiduCityCodes:" + baiduCityCodes + ",juheCityCodes:" + juheCityCodes);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_EXCEPTION);
    }
  }


  private String[] getJuheCityCodes(Integer[] baiduCityCodes, String[] juheCityCodes, Map<String, AppVehicleDTO> map, Map<String, AreaDTO> areaDTOMap) {
    Set<String> juheCodes = new HashSet<String>();
    for (AreaDTO areaDTO : areaDTOMap.values()) {
      juheCodes.add(areaDTO.getJuheCityCode());
    }
    if (ArrayUtil.isNotEmpty(juheCityCodes)) {
      juheCodes.addAll(Arrays.asList(juheCityCodes));
    }
    if (ArrayUtil.isNotEmpty(baiduCityCodes)) {
      juheCodes.addAll(ServiceManager.getService(IAreaService.class).getJuheCityCodeByBaiduCityCode(baiduCityCodes));
    }
    juheCodes.removeAll(Collections.singleton(null));
    return juheCodes.toArray(new String[juheCodes.size()]);
  }


  @ResponseBody
  @RequestMapping(value = "/juhe/area/list", method = RequestMethod.GET)
  public ApiResponse obtainJuheArea() throws Exception {
    try {
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_SUCCESS);
      ApiResultResponse<List<ApiArea>> response = new ApiResultResponse<List<ApiArea>>(apiResponse);
      response.setResult(ServiceManager.getService(IJuheService.class).obtainJuheSupportAreaAndViolateRegulations());
      return response;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/mirror/area/list", method = RequestMethod.GET)
  public ApiResponse obtainMirrorJuheArea() throws Exception {
    try {
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_SUCCESS);
      ApiResultResponse<List<ApiMirrorArea>> response = new ApiResultResponse<List<ApiMirrorArea>>(apiResponse);
      List<ApiArea> apiAreas = ServiceManager.getService(IJuheService.class).obtainJuheSupportAreaAndViolateRegulations();
      List<ApiMirrorArea> mirrorAreas = new ArrayList<ApiMirrorArea>();
      if (CollectionUtil.isNotEmpty(apiAreas)) {
        for (ApiArea apiArea : apiAreas) {
          mirrorAreas.add(apiArea.toApiMirrorArea());
        }
      }
      response.setResult(mirrorAreas);
      return response;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_EXCEPTION);
    }
  }


  /**
   * @param request
   * @param response
   * @param city     聚合城市编码
   * @param hphm     号牌号码 完整7位
   * @param hpzl     号牌种类编号 (参考号牌种类接口)
   * @param engineno 发动机号
   * @param classno  车架号
   * @param registno 车辆登记证书号
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "queryVehicleViolateRegulation/city/{city}/hphm/{hphm}/hpzl/{hpzl}/engineno/{engineno}/classno/{classno}/registno/{registno}", method = RequestMethod.GET)
  public ApiResponse queryVehicleViolateRegulation(HttpServletRequest request, HttpServletResponse response,
                                                   @PathVariable("city") String city,
                                                   @PathVariable("hphm") String hphm,
                                                   @PathVariable("hpzl") String hpzl,
                                                   @PathVariable("engineno") String engineno,
                                                   @PathVariable("classno") String classno,
                                                   @PathVariable("registno") String registno) throws Exception {
    try {
      LOG.info("queryVehicleViolateRegulation start");
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      AppVehicle appVehicle = CollectionUtil.getFirst(ServiceManager.getService(IAppUserService.class).getAppVehicleByAppUserNo(appUserNo));
      String vehicleNo = appVehicle != null ? appVehicle.getVehicleNo() : hphm;   //TODO app上传车牌号乱码 临时解决
      LOG.info("vehicleNo :{}", vehicleNo);
      IJuheService juheService = ServiceManager.getService(IJuheService.class);
      ApiVehicleViolateRegulationResponse regulationResponse = juheService.queryVehicleViolateRegulation(city, vehicleNo, hpzl, engineno, classno, registno);
      LOG.info(regulationResponse.getMessage());
      return regulationResponse;

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "mirror", method = RequestMethod.GET)
  public ApiResponse queryMirrorVehicleViolateRegulation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);//SessionUtil.getAppUserNo(request, response);    "6b13480c82b9c118bfe44de1fe141439"
      IAppVehicleService vehicleService = ServiceManager.getService(IAppVehicleService.class);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppVehicleDTO appVehicle = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
      if (StringUtil.isEmpty(appVehicle.getVehicleVin())) {
        return MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_VIN_IS_EMPTY);
      }
      if (StringUtil.isEmpty(appVehicle.getEngineNo())) {
        return MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_ENGINE_NO_IS_EMPTY);
      }
      ApiResponse result = vehicleService.getVRegulationRecordDTO_Mirror(appUserNo);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


}
