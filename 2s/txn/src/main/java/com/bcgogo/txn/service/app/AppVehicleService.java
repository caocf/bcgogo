package com.bcgogo.txn.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.response.*;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.util.AppConstant;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.app.AppUserCustomerMatchType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.product.service.IStandardBrandModelService;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.RemindEvent;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.CustomerServiceJob;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.model.app.AppVehicle;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.HttpUtils;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-23
 * Time: 下午1:47
 */
@Component
public class AppVehicleService implements IAppVehicleService {
  private static final Logger LOG = LoggerFactory.getLogger(AppVehicleService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public ApiResponse getBrandModelByKeywordsV1(String keywords, String type, Long brandId) {
    ApiResponse apiResponse;
    if (StringUtil.isNotEmpty(keywords)) keywords = keywords.toUpperCase();
    if (!"brand".equals(type) && !"model".equals(type)) {
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_FAIL, "车辆品牌或者车型类型不合法");
    }/* else if ("model".equals(type) && brandId == null) {
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_FAIL, "车辆品牌类型不合法");
    } */ else {
      IStandardBrandModelService standardBrandModelService = ServiceManager.getService(IStandardBrandModelService.class);
      apiResponse = MessageCode.toApiResponse(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_SUCCESS);
      VehicleResponseV1 response = new VehicleResponseV1(apiResponse);
      if ("brand".equals(type)) {
        List<StandardVehicleBrandDTO> standardVehicleBrandDTOList = standardBrandModelService.getStandardVehicleBrandSuggestionByName(keywords, null);
        BrandModelDTO brandModelDTO;
        for (StandardVehicleBrandDTO dto : standardVehicleBrandDTOList) {
          brandModelDTO = new BrandModelDTO();
          brandModelDTO.setBrandId(dto.getId());
          brandModelDTO.setBrandName(dto.getName());
          response.getResult().add(brandModelDTO);
        }
      } else {
        List<StandardVehicleModelDTO> standardVehicleBrandDTOList = standardBrandModelService.getStandardVehicleModelSuggestionByName(brandId, keywords);
        Set<Long> ids = new HashSet<Long>();
        for (StandardVehicleModelDTO dto : standardVehicleBrandDTOList) {
          ids.add(dto.getStandardVehicleBrandId());
        }
        Map<Long, StandardVehicleBrandDTO> map = standardBrandModelService.getStandardVehicleBrandMapByIds(ids);
        BrandModelDTO brandModelDTO;
        for (StandardVehicleModelDTO dto : standardVehicleBrandDTOList) {
          brandModelDTO = new BrandModelDTO();
          brandModelDTO.setModelId(dto.getId());
          brandModelDTO.setModelName(dto.getName());
          brandModelDTO.from(map.get(dto.getStandardVehicleBrandId()));
          response.getResult().add(brandModelDTO);
        }
      }
      return response;
    }
  }

  @Override
  public ApiResponse getBrandModelByKeywordsV2(String keywords, String type, Long brandId) {
    VehicleResponse response = new VehicleResponse(MessageCode.toApiResponse(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_SUCCESS));
    if (StringUtil.isNotEmpty(keywords)) keywords = keywords.toUpperCase();
    List<BrandModelDTO> brandModelResult = new ArrayList<BrandModelDTO>();
    if (StringUtil.isEmpty(type)) {
      addVehicleModel(keywords, null, brandModelResult);
      getVehicleBrand(keywords, brandModelResult);
      assembleResult(response, brandModelResult, true);
    } else {
      if ("brand".equals(type)) {
        getVehicleBrand(keywords, brandModelResult);
        assembleResult(response, brandModelResult, false);
      } else {
        addVehicleModel(keywords, brandId, brandModelResult);
        assembleResult(response, brandModelResult, true);
      }
    }

    return response;
  }

  @Override
  public VehicleInfoSuggestionDTO getVehicleInfoSuggestion(String vehicleNo, String mobile) {
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    IStandardBrandModelService standardBrandModelService = ServiceManager.getService(IStandardBrandModelService.class);
    List<VehicleDTO> vehicleDTOList = vehicleService.getVehiclesByCustomerMobile(mobile, vehicleNo);
    List<VehicleInfoSuggestionDTO> suggestionDTOList = new ArrayList<VehicleInfoSuggestionDTO>();
    Set<String> brandName = new HashSet<String>();
    Set<Long> brandIds = new HashSet<Long>();
    Set<String> modelName = new HashSet<String>();
    for (VehicleDTO dto : vehicleDTOList) {
      if (StringUtil.isNotEmpty(dto.getBrand()))
        brandName.add(dto.getBrand());
      if (StringUtil.isNotEmpty(dto.getModel()))
        modelName.add(dto.getModel());
    }
    Map<String, StandardVehicleModelDTO> modelDTOMap = standardBrandModelService.getNameStandardVehicleModelMapByNames(modelName);
    Map<String, StandardVehicleBrandDTO> brandDTOMap = standardBrandModelService.getNameStandardVehicleBrandMapByNames(brandName);
    for (StandardVehicleModelDTO dto : modelDTOMap.values()) {
      brandIds.add(dto.getStandardVehicleBrandId());
    }
    brandDTOMap.putAll(standardBrandModelService.getNameStandardVehicleBrandMapByIds(brandIds));
    StandardVehicleBrandDTO sb;
    StandardVehicleModelDTO sm;
    VehicleInfoSuggestionDTO suggestionDTO;
    for (VehicleDTO dto : vehicleDTOList) {
      sb = brandDTOMap.get(dto.getBrand());
      sm = modelDTOMap.get(dto.getModel());
      if (sb == null || sm == null || !sm.getStandardVehicleBrandId().equals(sb.getId())) continue;
      suggestionDTO = new VehicleInfoSuggestionDTO();
      suggestionDTO.setShopId(dto.getShopId());
      suggestionDTO.setVehicleNo(dto.getLicenceNo());
      suggestionDTO.setBrandModel(new BrandModelDTO(sb, sm));
      suggestionDTOList.add(suggestionDTO);
    }
    return CollectionUtil.getFirst(suggestionDTOList);
  }

  public void assembleResult(VehicleResponse response, List<BrandModelDTO> brandModelResult, boolean hasModel) {
    Map<Long, BrandDTO> brandIdBrandMap = new HashMap<Long, BrandDTO>();
    for (BrandModelDTO dto : brandModelResult) {
      brandIdBrandMap.put(dto.getBrandId(), new BrandDTO(dto.getBrandId(), dto.getBrandName()));
    }
    if (hasModel) {
      BrandDTO brandDTO;
      for (BrandModelDTO dto : brandModelResult) {
        if (dto.getModelId() == null) continue;
        brandDTO = brandIdBrandMap.get(dto.getBrandId());
        if (brandDTO != null)
          brandDTO.getModels().add(new ModelDTO(dto.getModelId(), dto.getModelName()));
      }
    }
    response.setResult(new ArrayList<BrandDTO>(brandIdBrandMap.values()));
  }

  private void addVehicleModel(String keywords, Long brandId, List<BrandModelDTO> brandModelResult) {
    IStandardBrandModelService standardBrandModelService = ServiceManager.getService(IStandardBrandModelService.class);
    List<StandardVehicleModelDTO> standardVehicleModelDTOList = standardBrandModelService.getStandardVehicleModelSuggestionByName(brandId, keywords);
    Set<Long> ids = new HashSet<Long>();
    for (StandardVehicleModelDTO dto : standardVehicleModelDTOList) {
      ids.add(dto.getStandardVehicleBrandId());
    }
    Map<Long, StandardVehicleBrandDTO> map = standardBrandModelService.getStandardVehicleBrandMapByIds(ids);
    BrandModelDTO brandModelDTO;
    for (StandardVehicleModelDTO dto : standardVehicleModelDTOList) {
      brandModelDTO = new BrandModelDTO();
      brandModelDTO.setModelId(dto.getId());
      brandModelDTO.setModelName(dto.getName());
      brandModelDTO.from(map.get(dto.getStandardVehicleBrandId()));
      brandModelResult.add(brandModelDTO);
    }
  }

  private void getVehicleBrand(String keywords, List<BrandModelDTO> brandModelResult) {
    IStandardBrandModelService standardBrandModelService = ServiceManager.getService(IStandardBrandModelService.class);
    List<StandardVehicleBrandDTO> standardVehicleBrandDTOList = standardBrandModelService.getStandardVehicleBrandSuggestionByName(keywords, null);
    BrandModelDTO brandModelDTO;
    for (StandardVehicleBrandDTO dto : standardVehicleBrandDTOList) {
      brandModelDTO = new BrandModelDTO();
      brandModelDTO.setBrandId(dto.getId());
      brandModelDTO.setBrandName(dto.getName());
      brandModelResult.add(brandModelDTO);
    }
  }

  @Override
  public void syncAppVehicle(List<VehicleDTO> vehicleDTOList) {
    if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
      IStandardBrandModelService standardBrandModelService = ServiceManager.getService(IStandardBrandModelService.class);
      Map<Long, VehicleDTO> vehicleDTOMap = new HashMap<Long, VehicleDTO>();
      for (VehicleDTO vehicleDTO : vehicleDTOList) {
        if (vehicleDTO != null && vehicleDTO.getId() != null) {
          vehicleDTOMap.put(vehicleDTO.getId(), vehicleDTO);
        }
      }
      if (CollectionUtils.isNotEmpty(vehicleDTOMap.keySet())) {
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        Set<AppUserCustomerMatchType> matchTypes = new HashSet<AppUserCustomerMatchType>();
        matchTypes.add(AppUserCustomerMatchType.IMEI_MATCH);
        List<AppUserCustomerDTO> appUserCustomerDTOs = appUserService.getAppUserCustomerDTOsByShopVehicleIds(vehicleDTOMap.keySet(), matchTypes);
        if (CollectionUtils.isEmpty(appUserCustomerDTOs)) {
          return;
        }
        UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
        UserWriter writer = userDaoManager.getWriter();
        Object status = writer.begin();
        try {
          for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
            if (appUserCustomerDTO != null && appUserCustomerDTO.getAppVehicleId() != null) {
              VehicleDTO vehicleDTO = vehicleDTOMap.get(appUserCustomerDTO.getShopVehicleId());
              if (vehicleDTO != null) {
                AppVehicle appVehicle = writer.getById(AppVehicle.class, appUserCustomerDTO.getAppVehicleId());
                if (appVehicle != null) {
                  boolean isNeedToUpdate = false;
                  if (StringUtils.isNotBlank(vehicleDTO.getLicenceNo()) && !vehicleDTO.getLicenceNo().equals(appVehicle.getVehicleNo())) {
                    appVehicle.setVehicleNo(vehicleDTO.getLicenceNo());
                    isNeedToUpdate = true;
                  }
                  if (StringUtils.isNotBlank(vehicleDTO.getEngineNo()) && !vehicleDTO.getEngineNo().equals(appVehicle.getEngineNo())) {
                    appVehicle.setEngineNo(vehicleDTO.getEngineNo());
                    isNeedToUpdate = true;
                  }
                  if (StringUtils.isNotBlank(vehicleDTO.getChassisNumber()) && !vehicleDTO.getChassisNumber().equals(appVehicle.getVehicleVin())) {
                    appVehicle.setVehicleVin(vehicleDTO.getChassisNumber());
                    isNeedToUpdate = true;
                  }
                  boolean isVehicleBrandChange = false;
                  if (StringUtils.isNotBlank(vehicleDTO.getBrand()) && !vehicleDTO.getBrand().equals(appVehicle.getVehicleBrand())) {
                    appVehicle.setVehicleBrand(vehicleDTO.getBrand());
                    StandardVehicleBrandDTO standardVehicleBrandDTO = standardBrandModelService.getStandardVehicleBrandDTOByName(vehicleDTO.getBrand());
                    if (standardVehicleBrandDTO != null) {
                      appVehicle.setVehicleBrandId(standardVehicleBrandDTO.getId());
                    }
                    isNeedToUpdate = true;
                    isVehicleBrandChange = true;
                  }
                  if (isVehicleBrandChange || StringUtils.isNotBlank(vehicleDTO.getModel())
                    && !vehicleDTO.getModel().equals(appVehicle.getVehicleModel())) {
                    appVehicle.setVehicleModel(vehicleDTO.getModel());
                    StandardVehicleModelDTO standardVehicleModelDTO = null;
                    if (appVehicle.getVehicleBrandId() != null) {
                      standardVehicleModelDTO = CollectionUtil.getFirst(standardBrandModelService.getStandardVehicleModelByName(
                        appVehicle.getVehicleBrandId(), vehicleDTO.getModel()));
                    }
                    if (standardVehicleModelDTO != null) {
                      appVehicle.setVehicleModelId(standardVehicleModelDTO.getId());
                    }

                    isNeedToUpdate = true;
                  }
                  if (isNeedToUpdate) {
                    writer.update(appVehicle);
                  }
                }
              }
            }
          }
          writer.commit(status);
        } finally {
          writer.rollback(status);
        }
      }
    }
  }

  @Override
  public ApiResponse addOrUpdateGsmAppVehicle(AppVehicleDTO appVehicleDTO) {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    UserWriter userWriter = userDaoManager.getWriter();

    appVehicleDTO.filter();
    AppGsmVehicleResponse appGsmVehicleResponse = new AppGsmVehicleResponse(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_SUCCESS));

    List<AppVehicle> appVehicleList = userWriter.getAppVehicleByAppUserNo(appVehicleDTO.getUserNo());
    boolean isNeedToUpdateShopVehicle = false;
    if (CollectionUtils.isNotEmpty(appVehicleList)) {
      AppVehicle appVehicle = CollectionUtil.getFirst(appVehicleList);
      Object status = userWriter.begin();
      try {

        if (StringUtils.isNotBlank(appVehicleDTO.getVehicleNo()) && !appVehicleDTO.getVehicleNo().equals(appVehicle.getVehicleNo())) {
          isNeedToUpdateShopVehicle = true;
        }
        appVehicle.updateAppVehicleFromGsmApp(appVehicleDTO);
        appUserVehicleObdService.createBaseVehicle(appVehicleDTO, userWriter);
        userWriter.update(appVehicle);
        appVehicleDTO.setVehicleId(appVehicle.getId());
        userWriter.commit(status);
      } finally {
        userWriter.rollback(status);
      }
    } else {
      appGsmVehicleResponse = new AppGsmVehicleResponse(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_FAIL));
    }
    if (isNeedToUpdateShopVehicle) {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserCustomerDTO appUserCustomerDTO = appUserService.getAppUserCustomerDTOByAppUserNoAndAppVehicleId(appVehicleDTO.getUserNo(), appVehicleDTO.getVehicleId(), AppUserCustomerMatchType.IMEI_MATCH);
      if (appUserCustomerDTO != null && appUserCustomerDTO.getShopId() != null && appUserCustomerDTO.getShopVehicleId() != null) {
        Vehicle vehicle = userWriter.getVehicleById(appUserCustomerDTO.getShopId(), appUserCustomerDTO.getShopVehicleId());
        if (vehicle != null && vehicle.getShopId() != null && vehicle.getId() != null) {
          List<Vehicle> toCheckVehicles = userWriter.getVehicleByLicenceNo(appUserCustomerDTO.getShopId(), appVehicleDTO.getVehicleNo());
          if (CollectionUtils.isEmpty(toCheckVehicles)) {
            Object status = userWriter.begin();
            try {
              vehicle.updateLicenceNo(appVehicleDTO.getVehicleNo());
              userWriter.update(vehicle);
              userWriter.commit(status);
            } finally {
              userWriter.rollback(status);
            }
            //更新remind_event 上的vehicleNo
            List<CustomerServiceJob> customerServiceJobs = userWriter.getCustomerServiceJobByCustomerIdAndVehicleId(
              appUserCustomerDTO.getShopId(), appUserCustomerDTO.getCustomerId(), appUserCustomerDTO.getShopVehicleId());
            if (CollectionUtils.isNotEmpty(customerServiceJobs)) {
              for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
                TxnWriter txnWriter = txnDaoManager.getWriter();
                RemindEvent remindEvent = CollectionUtil.getFirst(txnWriter.getRemindEventByOldRemindEventId(
                  RemindEventType.CUSTOMER_SERVICE, customerServiceJob.getShopId(), customerServiceJob.getId()));
                if (remindEvent != null) {
                  Object txnStatus = txnWriter.begin();
                  try {
                    remindEvent.setLicenceNo(appVehicleDTO.getVehicleNo());
                    txnWriter.saveOrUpdate(remindEvent);
                    txnWriter.commit(txnStatus);
                  } finally {
                    txnWriter.rollback(txnStatus);
                  }
                }
              }
            }


            try {
              IVehicleSolrWriterService vehicleSolrWriterService = ServiceManager.getService(IVehicleSolrWriterService.class);
              vehicleSolrWriterService.createVehicleSolrIndex(vehicle.getShopId(), vehicle.getId());

              IUserService userService = ServiceManager.getService(IUserService.class);
              CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(userService.getCustomerVehicleByVehicleId(vehicle.getId()));
              if (customerVehicleDTO != null && customerVehicleDTO.getCustomerId() != null) {
                ICustomerOrSupplierSolrWriteService customerOrSupplierSolrWriteService = ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
                customerOrSupplierSolrWriteService.reindexCustomerByCustomerId(customerVehicleDTO.getCustomerId());
              }

            } catch (Exception e) {
              LOG.error(e.getMessage(), e);
            }

          }
        }
      }
    }

    ApiResponse apiResponse = appUserVehicleObdService.updateOilPriceByAppVehicleDTO(appVehicleDTO);
    if (MessageCode.UPDATE_APP_USER_CONFIG_SUCCESS.getCode() != apiResponse.getMsgCode()) {
      return new AppGsmVehicleResponse(MessageCode.toApiResponse(MessageCode.SAVE_OIL_PRICE_FAIL, "请填写油价"));
    }
    return appGsmVehicleResponse;
  }


  //微信违章调用
  @Override
  public Result getVRegulationRecordDTO(String appUserNo) throws Exception {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
    String juheCityCode = "";
    List<VehicleViolateRegulationRecordDTO> recordDTOList = new ArrayList<VehicleViolateRegulationRecordDTO>();
    List<VehicleViolateRegulationRecordDTO> recordDTOs = new ArrayList<VehicleViolateRegulationRecordDTO>();
    Result result = null;
    if (appVehicleDTO == null) {
      return new Result("违章车辆不存在或您还未绑定后视镜", false);
    }
    if (appVehicleDTO.getEngineNo() == null) {
      return new Result("发动机号为空，请确认已经填写成功", false);
    }
    if (appVehicleDTO.getVehicleVin() == null) {
      return new Result("车架号为空，请确认已经填写成功", false);
    }
    String url = AppConstant.URL_APIS_GET_ILLEGAL_CITY;
    url = url.replace("{appUserNo}", appUserNo);
    HttpResponse response = HttpUtils.sendGet(url);
    String illegalCityDTOJson = response.getContent();
    IllegalCityDTO illegalCityDTO = JsonUtil.jsonToObj(illegalCityDTOJson, IllegalCityDTO.class);
    if (illegalCityDTO != null) { //先查定时钟生成的cityCode,存在的情况执行以下程序
      if (illegalCityDTO.getJuheCityCode().indexOf(",") == -1) {//只有一个城市
        result = queryUnHandledVehicleViolateRegulation(illegalCityDTO.getJuheCityCode(), appVehicleDTO);
        recordDTOs = (List<VehicleViolateRegulationRecordDTO>) result.getData();
        if (CollectionUtil.isNotEmpty(recordDTOs)) {
          return new Result(true, recordDTOs);
        } else {
          return new Result("无违章记录！", false);
        }

      } else { //存在多个城市，遍历
        String juheCityCodes[] = illegalCityDTO.getJuheCityCode().split(",");
        for (int i = 0; i < juheCityCodes.length; i++) {
          result = queryUnHandledVehicleViolateRegulation(juheCityCodes[i], appVehicleDTO);
          recordDTOs = (List<VehicleViolateRegulationRecordDTO>) result.getData();
          if (CollectionUtils.isNotEmpty(recordDTOs)) {
            for (VehicleViolateRegulationRecordDTO vehicleViolateRegulationRecordDTO : recordDTOs) {
              recordDTOList.add(vehicleViolateRegulationRecordDTO);
            }
          }
        }
        if (CollectionUtil.isNotEmpty(recordDTOList)) {
          return new Result(true, recordDTOList);
        } else {
          return new Result("无违章记录！", false);
        }

      }
    }
    return new Result("查询失败！", false);
  }

  public Result queryUnHandledVehicleViolateRegulation(String juheCityCode, AppVehicleDTO appVehicleDTO) {
    IJuheService juheService = ServiceManager.getService(IJuheService.class);
    Result result = juheService.queryUnHandledVehicleViolateRegulation(juheCityCode, appVehicleDTO.getVehicleNo(),
      "02", appVehicleDTO.getEngineNo(), appVehicleDTO.getVehicleVin(), appVehicleDTO.getRegistNo());
    return result;
  }

  //后视镜调用
  @Override
  public ApiVehicleViolateRegulationResponse getVRegulationRecordDTO_Mirror(String appUserNo) throws Exception {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
    String juheCityCode = "";
    List<VehicleViolateRegulationRecordDTO> recordDTOList = new ArrayList<VehicleViolateRegulationRecordDTO>();
    List<VehicleViolateRegulationRecordDTO> recordDTOs = new ArrayList<VehicleViolateRegulationRecordDTO>();
    ApiVehicleViolateRegulationResponse result = null;
    String url = AppConstant.URL_APIS_GET_ILLEGAL_CITY;
    url = url.replace("{appUserNo}", appUserNo);
    HttpResponse response = HttpUtils.sendGet(url);
    String illegalCityDTOJson = response.getContent();
    IllegalCityDTO illegalCityDTO = JsonUtil.jsonToObj(illegalCityDTOJson, IllegalCityDTO.class);
    if (illegalCityDTO != null) { //先查定时钟生成的cityCode,存在的情况执行以下程序
      if (illegalCityDTO.getJuheCityCode().indexOf(",") == -1) {//只有一个城市
        result = queryUnHandledVehicleViolateRegulation_(illegalCityDTO.getJuheCityCode(), appVehicleDTO);
        return result;
      } else { //存在多个城市，遍历
        String juheCityCodes[] = illegalCityDTO.getJuheCityCode().split(",");
        for (int i = 0; i < juheCityCodes.length; i++) {
          result = queryUnHandledVehicleViolateRegulation_(juheCityCodes[i], appVehicleDTO);
          if (CollectionUtils.isNotEmpty(recordDTOs)) {
            for (VehicleViolateRegulationRecordDTO vehicleViolateRegulationRecordDTO : recordDTOs) {
              recordDTOList.add(vehicleViolateRegulationRecordDTO);
            }
          }
        }
        return result;
      }
    }
    return result;
  }

  public ApiVehicleViolateRegulationResponse queryUnHandledVehicleViolateRegulation_(String juheCityCode, AppVehicleDTO appVehicleDTO) {
    IJuheService juheService = ServiceManager.getService(IJuheService.class);
    ApiVehicleViolateRegulationResponse result = juheService.queryVehicleViolateRegulation(juheCityCode, appVehicleDTO.getVehicleNo(),
      "02", appVehicleDTO.getEngineNo(), appVehicleDTO.getVehicleVin(), appVehicleDTO.getRegistNo());
    return result;
  }


}
