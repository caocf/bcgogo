package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.api.request.FaultCodeListRequest;
import com.bcgogo.api.request.MultiFaultRequest;
import com.bcgogo.api.request.VehicleRequest;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.api.response.AppGsmVehicleResponse;
import com.bcgogo.api.response.AppVehicleFaultInfoListResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.service.ILicensePlateService;
import com.bcgogo.product.service.app.IAppDictionaryService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IAppVehicleService;
import com.bcgogo.txn.service.app.IHandleAppUserShopCustomerMatchService;
import com.bcgogo.txn.service.app.IHandleAppVehicleFaultCodeService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.model.app.AppUserConfig;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.app.IAppVehicleFaultCodeService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 手机端车辆相关controller
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 上午11:07
 */
@Controller
public class AppVehicleController {
  private static final Logger LOG = LoggerFactory.getLogger(AppVehicleController.class);

  /**
   * 保存车辆信息
   * id：后台数据主键
   * vehicleId：车辆唯一标识号
   * vehicleNo：车牌号                        *
   * vehicleModel：车型                        *
   * vehicleModelId：车型ID
   * vehicleBrand：车辆品牌                    *
   * vehicleBrandId：车辆品牌ID
   * obdSN：当前车辆所安装的obd的唯一标识号
   * userNo：用户账号
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/vehicleInfo", method = RequestMethod.PUT)
  public ApiResponse saveAppVehicle(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody AppVehicleDTO appVehicleDTO) {
    Long appUserId = null;
    String appUserNo = null;
    try {
      appUserId = SessionUtil.getAppUserId(request, response);
      appUserNo = SessionUtil.getAppUserNo(request, response);
      appVehicleDTO.setAppUserId(appUserId);
      appVehicleDTO.setUserNo(appUserNo);
      return saveAppVehicleInternal(appVehicleDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return (MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_EXCEPTION));
    }
  }

  private ApiResponse saveAppVehicleInternal(AppVehicleDTO appVehicleDTO) {
    try {
      if (appVehicleDTO.getAppUserId() == null || StringUtil.isEmpty(appVehicleDTO.getUserNo())) {
        throw new BcgogoException("appUserId is null or userNo is null");
      }
      appVehicleDTO.setStatus(Status.active);
      appVehicleDTO.filter();
      String appUserNo = appVehicleDTO.getUserNo();
      Pair<ApiResponse, Boolean> responsePair = ServiceManager.getService(IAppUserVehicleObdService.class).addOrUpdateAppVehicle(appVehicleDTO);
      ApiResponse apiResponse = responsePair.getKey();
      if (apiResponse != null && apiResponse.getMsgCode() > 0 && responsePair.getValue()) {
        //给店铺创建客户
        if (appVehicleDTO.getBindingShopId() != null) {
          ServiceManager.getService(ICustomerService.class)
            .createOrMatchingCustomerByAppUserNo(appUserNo, appVehicleDTO.getBindingShopId());
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class)
            .reindexCustomersByAppUserNos(appUserNo);
        }
        IHandleAppUserShopCustomerMatchService handleAppUserShopCustomerMatchService = ServiceManager.getService(IHandleAppUserShopCustomerMatchService.class);
        handleAppUserShopCustomerMatchService.handleAppUserCustomerMatch(appVehicleDTO.getAppUserId());
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/vehicle/list", method = RequestMethod.PUT)
  public List<ApiResponse> saveAppVehicleV1(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody VehicleRequest vehicleRequest) {
    List<ApiResponse> apiResponses = new ArrayList<ApiResponse>();
    Long appUserId;
    String appUserNo;
    try {
      appUserId = SessionUtil.getAppUserId(request, response);
      appUserNo = SessionUtil.getAppUserNo(request, response);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      apiResponses.add(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_EXCEPTION));
      return apiResponses;
    }
    for (AppVehicleDTO appVehicleDTO : vehicleRequest.getVehicles()) {
      appVehicleDTO.setStatus(Status.active);
      try {
        appVehicleDTO.setAppUserId(appUserId);
        appVehicleDTO.setUserNo(appUserNo);
      } catch (Exception e) {
        e.printStackTrace();
      }
      apiResponses.add(saveAppVehicleInternal(appVehicleDTO));
    }
    return apiResponses;
  }

  @ResponseBody
  @RequestMapping(value = "/vehicle/list/guest", method = RequestMethod.PUT)
  public ApiResultResponse<Map<String, String>> addVehiclesForGuest(HttpServletRequest request, HttpServletResponse response,
                                                                    @RequestBody VehicleRequest vehicleRequest) {
    ApiResultResponse<Map<String, String>> resultResponse = new ApiResultResponse<Map<String, String>>();
    Long appUserId;
    String appUserNo;
    try {
      appUserId = SessionUtil.getAppUserId(request, response);
      appUserNo = SessionUtil.getAppUserNo(request, response);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      resultResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_EXCEPTION));
      return resultResponse;
    }
    for (AppVehicleDTO appVehicleDTO : vehicleRequest.getVehicles()) {
      appVehicleDTO.setStatus(Status.active);
      try {
        appVehicleDTO.setAppUserId(appUserId);
        appVehicleDTO.setUserNo(appUserNo);
        appVehicleDTO.filter();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    Pair<Boolean, Map<String, String>> result = ServiceManager.getService(IAppUserVehicleObdService.class).addVehiclesForGuest(vehicleRequest.getVehicles());
    if (result.getKey()) {
      resultResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_SUCCESS));
    } else {
      resultResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_FAIL));
      resultResponse.setResult(result.getValue());
      resultResponse.setMessage("保存车辆信息失败");
    }
    return resultResponse;
  }

  /**
   * 获取车辆列表
   * userNo：用户账号  *
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/list/userNo/{userNo}", method = RequestMethod.GET)
  public ApiResponse obtainVehicleList(@PathVariable("userNo") String userNo, HttpServletRequest request, HttpServletResponse response) {
    try {
      return ServiceManager.getService(IAppUserVehicleObdService.class)
        .getAppVehicleResponseByAppUserNo(SessionUtil.getAppUserNo(request, response));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/vehicle/updateDefault", method = RequestMethod.POST)
  public ApiResponse updateVehicleDefault(Long vehicleId, HttpServletRequest request, HttpServletResponse response) {
    try {
      if (vehicleId == null) {
        return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_SET_DEFAULT_FAIL, ValidateMsg.APP_VEHICLE_ID_EMPTY);
      }
      ServiceManager.getService(IAppUserVehicleObdService.class)
        .updateDefaultAppVehicle(vehicleId, SessionUtil.getAppUserNo(request, response), true);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_SET_DEFAULT_SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_SET_DEFAULT_EXCEPTION);
    }
  }


  /**
   * 获取一辆车信息
   * vehicleId不能为空
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/singleVehicle/vehicleId/{vehicleId}", method = RequestMethod.GET)
  public ApiResponse singleVehicle(@PathVariable("vehicleId") Long vehicleId, HttpServletRequest request, HttpServletResponse response) {
    try {
      return ServiceManager.getService(IAppUserVehicleObdService.class)
        .getAppVehicleDetail(vehicleId, SessionUtil.getAppUserNo(request, response));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_EXCEPTION);
    }
  }


  @Deprecated
  @ResponseBody
  @RequestMapping(value = "/vehicle/singleVehicle/vehicleVin/{vehicleVin}/userNo/{userNo}", method = RequestMethod.GET)
  public ApiResponse getSingleVehicle(HttpServletRequest request, HttpServletResponse response, @PathVariable("vehicleVin") String vehicleVin, @PathVariable("userNo") String userNo) throws Exception {
    try {
      userNo = SessionUtil.getAppUserNo(request, response);
      return ServiceManager.getService(IAppUserVehicleObdService.class)
        .getAppVehicleDetail((StringUtil.isEmptyAppGetParameter(userNo) ? "" : userNo), StringUtil.isEmptyAppGetParameter(vehicleVin) ? "" : vehicleVin);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_EXCEPTION);
    }
  }


  /**
   * 删除车辆
   * id和vehicleId不能同时为空
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/singleVehicle/vehicleId/{vehicleId}", method = RequestMethod.DELETE)
  public ApiResponse deleteVehicle(HttpServletRequest request, HttpServletResponse response, @PathVariable("vehicleId") Long vehicleId) throws Exception {
    try {
      ApiResponse apiResponse = ServiceManager.getService(IAppUserVehicleObdService.class)
        .deleteVehicle(vehicleId, SessionUtil.getAppUserId(request, response));
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class)
        .reindexCustomersByAppUserNos(SessionUtil.getAppUserNo(request, response));
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.DELETE_SINGLE_APP_VEHICLE_EXCEPTION);
    }
  }

  /**
   * 发送车辆故障信息
   * faultCode：故障码    *
   * userNo：用户账号
   * vehicleId：车辆唯一标识号    *
   * obdSN：obd唯一标识号        *
   * reportTime：故障时间（Long型unixTime）  *
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/fault", method = RequestMethod.POST)
  public ApiResponse vehicleFault(HttpServletRequest request, HttpServletResponse response, VehicleFaultDTO faultDTO) throws Exception {
    IHandleAppVehicleFaultCodeService handleAppVehicleFaultCodeService = ServiceManager.getService(IHandleAppVehicleFaultCodeService.class);
    Long vehicleId = faultDTO.getVehicleId();
    String appUserNo = SessionUtil.getAppUserNo(request, response);
    String faultCode = faultDTO.getFaultCode();
    StringBuilder lockKey = new StringBuilder();
    try {
      if (vehicleId != null) {
        lockKey = new StringBuilder(vehicleId.toString()).append(StringUtil.valueOf(faultCode));
        if (BcgogoConcurrentController.lock(ConcurrentScene.APP_SAVE_VEHICLE_FAULT_INFO, lockKey.toString())) {
          faultDTO.setUserNo(appUserNo);
          ApiResponse apiResponse = handleAppVehicleFaultCodeService.handleVehicleFaultInfo(faultDTO);
          apiResponse.setDebug(faultDTO.toString());
          return apiResponse;
        } else {
          return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_FAIL, ValidateMsg.APP_SENT_VEHICLE_FAULT_TO_OFTEN.getValue());
        }
      } else {
        return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_FAIL, ValidateMsg.VEHICLE_EMPTY.getValue());
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_EXCEPTION);
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.APP_SAVE_VEHICLE_FAULT_INFO, lockKey.toString());
    }
  }

  @ResponseBody
  @RequestMapping(value = "/vehicle/multiFault", method = RequestMethod.POST)
  public ApiResponse multiFault(HttpServletRequest request, HttpServletResponse response, MultiFaultRequest multiFaultRequest) throws Exception {
    IHandleAppVehicleFaultCodeService handleAppVehicleFaultCodeService = ServiceManager.getService(IHandleAppVehicleFaultCodeService.class);
    String appUserNo = SessionUtil.getAppUserNo(request, response);
    multiFaultRequest.setAppUserNo(appUserNo);
    try {
      return handleAppVehicleFaultCodeService.handleMultiVehicleFaultInfo(multiFaultRequest);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_EXCEPTION);
    }
  }

  /**
   * 根据关键字获取车辆品牌和车型
   * keywords：车型或者车辆品牌关键字          *
   * type：车辆品牌或者车型  （brand|model）   *
   * brandId：车辆品牌ID，当type值为"model"时生效
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/brandModel/{keywords}/{type}/{brandId}/v2", method = RequestMethod.GET)
  public ApiResponse getBrandModelByKeywordsV2(@PathVariable("keywords") String keywords,
                                               @PathVariable("type") String type,
                                               @PathVariable("brandId") String brandId) throws Exception {
    try {
      ApiResponse apiResponse = ServiceManager.getService(IAppVehicleService.class)
        .getBrandModelByKeywordsV2(StringUtil.isEmptyAppGetParameter(keywords) ? "" : keywords, (StringUtil.isEmptyAppGetParameter(type) ? null : type), StringUtil.isEmptyAppGetParameter(brandId) ? null : Long.valueOf(brandId));
      apiResponse.setDebug("keywords:" + keywords + ",type:" + type + ",brandId:" + brandId);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_EXCEPTION);
    }
  }

  @Deprecated
  @ResponseBody
  @RequestMapping(value = "/vehicle/brandModel/keywords/{keywords}/type/{type}/brandId/{brandId}", method = RequestMethod.GET)
  public ApiResponse getBrandModelByKeywords(@PathVariable("keywords") String keywords,
                                             @PathVariable("type") String type,
                                             @PathVariable("brandId") String brandId) throws Exception {
    try {
      ApiResponse apiResponse = ServiceManager.getService(IAppVehicleService.class)
        .getBrandModelByKeywordsV1(StringUtil.isEmptyAppGetParameter(keywords) ? "" : keywords, type, StringUtil.isEmptyAppGetParameter(brandId) ? null : Long.valueOf(brandId));
      apiResponse.setDebug("your params:" + "[keywords:" + keywords + ",type:" + type + ",brandId:" + brandId + "]");
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_EXCEPTION);
    }
  }

  /**
   * 故障字典信息更新
   * vehicleModelId：车型ID ( 如果车型Id为空则取通用字典,为空的时候前台传入的值为NULL)
   * dicVersion：字典版本   (如果版本号为空则取最新版，为空的时候前台传入的值为NULL）
   * status：请求处理状态   String(success|fail开发时可根据实际需要扩展)
   * msgCode：请求的错误码      int
   * message：请求的错误码描述信息  String
   * <p/>
   * dictionaryId：字典ID  Long
   * dictionaryVersion：字典版本  String
   * isCommon：是否通用  String  true 通用
   * faultCodeList：List<FaultCodeInfo>   字典错误码列表
   * FaultCodeInfo:
   * faultCode：故障码    String
   * description：故障描述    String
   * 每次请求的时候会会把当前最新的字典版本发送给客户端
   * 当对方传过来的字典版本号和系统字典版本号一致的时候，返回 fail，当前字典版本已经是最新版了
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/faultDic/dicVersion/{dicVersion}/vehicleModelId/{vehicleModelId}",
    method = RequestMethod.GET)
  public ApiResponse updateFaultDictionary(@PathVariable("vehicleModelId") String vehicleModelId,
                                           @PathVariable("dicVersion") String dicVersion) throws Exception {
    try {
      Long longVehicleModelId = null;
      String dicVersionStr = null;
      if (StringUtil.isNotEmptyAppGetParameter(vehicleModelId) && StringUtils.isNumeric(vehicleModelId)) {
        longVehicleModelId = NumberUtils.createLong(vehicleModelId);
      }
      if (StringUtil.isNotEmptyAppGetParameter(dicVersion)) {
        dicVersionStr = dicVersion;
      }

      ApiResponse apiResponse = ServiceManager.getService(IAppDictionaryService.class)
        .updateFaultDictionary(longVehicleModelId, dicVersionStr);
      apiResponse.setDebug("vehicleModelId:" + vehicleModelId + ",dicVersion:" + dicVersion);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_EXCEPTION);
    }
  }


  /**
   * 修改保养信息
   *
   * @param maintainDTO AppVehicleMaintainDTO
   * @return ApiResponse
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/maintain", method = RequestMethod.PUT)
  public ApiResponse vehicleMaintain(@RequestBody AppVehicleMaintainDTO maintainDTO) throws Exception {
    try {
      ApiResponse apiResponse;
      String validateResult = maintainDTO.validate();
      if (maintainDTO.isSuccess(validateResult)) {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_MAINTAIN_UPDATE_SUCCESS));
        apiResponse.setDebug(maintainDTO.toString());
        IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
        Result result = appUserVehicleObdService.updateVehicleMaintain(maintainDTO);
        if (result.isSuccess()) {
          return apiResponse;
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.VEHICLE_MAINTAIN_UPDATE_FAIL, result.getMsg());
          apiResponse.setDebug(maintainDTO.toString());
          return apiResponse;
        }
      } else {
        apiResponse = MessageCode.toApiResponse(MessageCode.VEHICLE_MAINTAIN_UPDATE_FAIL, validateResult);
        apiResponse.setDebug(maintainDTO.toString());
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VEHICLE_MAINTAIN_UPDATE_EXCEPTION);
    }
  }

  /**
   * 手机端发送车况信息
   *
   * @param appVehicleDTO AppVehicleDTO
   * @return ApiResponse
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/condition", method = RequestMethod.PUT)
  public ApiResponse vehicleCondition(@RequestBody AppVehicleDTO appVehicleDTO) throws Exception {
    try {
      ApiResponse apiResponse;
      String result = appVehicleDTO.saveVehicleConditionValidate();
      if (appVehicleDTO.isSuccess(result)) {

        IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
        Result saveResult = appUserVehicleObdService.saveOrUpdateVehicleCondition(appVehicleDTO);
        if (saveResult.isSuccess()) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_CONDITION_SAVE_SUCCESS));
          apiResponse.setDebug(appVehicleDTO.toString());
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.VEHICLE_CONDITION_SAVE_FAIL, saveResult.getMsg());
          apiResponse.setDebug(appVehicleDTO.toString());
        }
        return apiResponse;
      } else {
        apiResponse = MessageCode.toApiResponse(MessageCode.VEHICLE_CONDITION_SAVE_FAIL, result);
        apiResponse.setDebug(appVehicleDTO.toString());
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VEHICLE_CONDITION_SAVE_EXCEPTION);
    }
  }


  @ResponseBody
  @RequestMapping(value = "/vehicle/info/suggestion/{mobile}/{vehicleNo}", method = RequestMethod.GET)
  public ApiResponse vehicleInfoSuggestion(
    @PathVariable("vehicleNo") String vehicleNo,
    @PathVariable("mobile") String mobile) throws Exception {
    try {
      String _vehicleNo = StringUtil.isEmptyAppGetParameter(vehicleNo) ? null : vehicleNo;
      String _mobile = StringUtil.isEmptyAppGetParameter(mobile) ? null : mobile;
      if (StringUtil.isEmpty(_vehicleNo) && StringUtil.isEmpty(_mobile)) {
        return MessageCode.toApiResponse(MessageCode.VEHICLE_INFO_SUGGESTION_FAIL, " mobile and vehicleNo is empty.");
      }
      VehicleInfoSuggestionDTO vehicleInfoSuggestion = ServiceManager.getService(IAppVehicleService.class)
        .getVehicleInfoSuggestion(_vehicleNo, _mobile);
      ApiResultResponse apiResultResponse = new ApiResultResponse<VehicleInfoSuggestionDTO>
        (MessageCode.toApiResponse(MessageCode.VEHICLE_INFO_SUGGESTION_SUCCESS), vehicleInfoSuggestion);
      apiResultResponse.setDebug("mobile:" + mobile + ",vehicleNo:" + vehicleNo);
      return apiResultResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VEHICLE_INFO_SUGGESTION_EXCEPTION);
    }
  }


  /**
   * 操作车辆故障信息
   * appVehicleFaultInfoDTOs[0].status：新状态的status *
   * appVehicleFaultInfoDTOs[0].appVehicleId：故障对应的车型 *
   * appVehicleFaultInfoDTOs[0].errorCode：故障码 *
   * appVehicleFaultInfoDTOs[0].lastStatus：需要操作的故障类型 *
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/faultCode", method = RequestMethod.POST)
  public ApiResponse handleFaultCode(HttpServletRequest request, HttpServletResponse response,
                                     AppVehicleFaultInfoOperateDTO appVehicleFaultInfoOperateDTO) throws Exception {
    IAppVehicleFaultCodeService appVehicleFaultCodeService = ServiceManager.getService(IAppVehicleFaultCodeService.class);
    String appUserNo = SessionUtil.getAppUserNo(request, response);
    try {
      return appVehicleFaultCodeService.handleFaultCode(appUserNo, appVehicleFaultInfoOperateDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_OPERATE_EXCEPTION);
    }
  }

  /**
   * 获取车辆故障信息列表
   * pageNo 当前页数
   * pageSize 分页大小
   * status ： ErrorCodeTreatStatus 枚举值，逗号分隔
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/faultCodeList", method = RequestMethod.POST)
  public ApiResponse faultCodeList(HttpServletRequest request, HttpServletResponse response, FaultCodeListRequest faultCodeListRequest) {
    IAppVehicleFaultCodeService appVehicleFaultCodeService = ServiceManager.getService(IAppVehicleFaultCodeService.class);
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      faultCodeListRequest.setAppUserNo(appUserNo);
      ApiResponse apiResponse = appVehicleFaultCodeService.getFaultInfoList(faultCodeListRequest);
      if (apiResponse instanceof AppVehicleFaultInfoListResponse) {
        IAppDictionaryService appDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
        appDictionaryService.addFaultCodeCategoryBackgroundInfo(((AppVehicleFaultInfoListResponse) apiResponse).getResult());
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_LIST_EXCEPTION);
    }
  }


  /**
   * 获取一辆车信息
   * vehicleId不能为空
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/gsmUserGetAppVehicle", method = RequestMethod.GET)
  public ApiResponse gsmUserGetAppVehicle(HttpServletRequest request, HttpServletResponse response) {
    try {
      AppGsmVehicleResponse apiResponse = ServiceManager.getService(IAppUserVehicleObdService.class)
        .gsmUserGetAppVehicle(SessionUtil.getAppUserNo(request, response));

      if (apiResponse != null && apiResponse.getVehicleInfo() != null) {
        this.setAppVehicleJuheCityCode(apiResponse.getVehicleInfo());
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_EXCEPTION);
    }
  }

  /**
   * @param request
   * @param response
   * @param appVehicleDTO
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/saveGsmVehicle", method = RequestMethod.PUT)
  public ApiResponse saveGsmAppVehicle(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody AppVehicleDTO appVehicleDTO) {
    Long appUserId = null;
    String appUserNo = null;
    try {
      IAppVehicleService appVehicleService = ServiceManager.getService(IAppVehicleService.class);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      appUserId = SessionUtil.getAppUserId(request, response);
      appUserNo = SessionUtil.getAppUserNo(request, response);
      appVehicleDTO.setAppUserId(appUserId);
      appVehicleDTO.setUserNo(appUserNo);
      if (StringUtil.isEmpty(appVehicleDTO.getJuheCityCode())) {
        this.setAppVehicleJuheCityCode(appVehicleDTO);
      }
      Double oilPrice = null;
      AppUserConfig appUserConfig = appUserService.getAppUserConfigByName(appUserNo);
      if (appUserConfig == null || NumberUtil.doubleVal(appUserConfig.getValue()) <= 0) {
        oilPrice = GSMConstant.DEF_OIL_PRICE;
      } else {
        oilPrice = NumberUtil.doubleVal(appUserConfig.getValue());
      }
      appVehicleDTO.setOilPrice(oilPrice);
      ApiResponse apiResponse = appVehicleService.addOrUpdateGsmAppVehicle(appVehicleDTO);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return (MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_EXCEPTION));
    }
  }

  public void setAppVehicleJuheCityCode(AppVehicleDTO appVehicleDTO) {
    if (StringUtil.isEmpty(appVehicleDTO.getVehicleNo()) || StringUtil.isNotEmpty(appVehicleDTO.getJuheCityCode())) {
      return;
    }
    ILicensePlateService licensePlateService = ServiceManager.getService(ILicensePlateService.class);
    AreaDTO areaDTO = licensePlateService.getAreaDTOByLicenseNo(appVehicleDTO.getVehicleNo());
    if (areaDTO != null && StringUtil.isNotEmpty(areaDTO.getJuheCityCode())) {
      IJuheService juheService = ServiceManager.getService(IJuheService.class);

      List<JuheViolateRegulationCitySearchConditionDTO> conditionDTOs = juheService.getJuheViolateRegulationCitySearchCondition(areaDTO.getJuheCityCode(), JuheStatus.ACTIVE);
      if (CollectionUtil.isNotEmpty(conditionDTOs)) {
        JuheViolateRegulationCitySearchConditionDTO conditionDTO = CollectionUtil.getFirst(conditionDTOs);
        appVehicleDTO.setJuheCityName(conditionDTO.getCityName());
        appVehicleDTO.setJuheCityCode(conditionDTO.getCityCode());
      }
    }
  }

}
