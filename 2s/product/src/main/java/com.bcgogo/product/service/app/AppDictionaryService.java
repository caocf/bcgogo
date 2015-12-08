package com.bcgogo.product.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.response.UpdateFaultDictionaryResponse;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductWriter;
import com.bcgogo.product.model.app.Dictionary;
import com.bcgogo.product.model.app.DictionaryFaultInfo;
import com.bcgogo.product.model.app.VehicleDictionary;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-2
 * Time: 上午10:22
 */
@Component
public class AppDictionaryService implements  IAppDictionaryService {

  @Autowired
  private ProductDaoManager productDaoManager;

  private static final Logger LOG = LoggerFactory.getLogger(AppDictionaryService.class);

  public ApiResponse updateFaultDictionary(Long vehicleModelId, String dicVersion) {
    List<DictionaryFaultInfoDTO> dictionaryInfoList = new ArrayList<DictionaryFaultInfoDTO>();
    DictionaryDTO dictionaryDTO = null;
    if (vehicleModelId == null) {
      dictionaryDTO = getCommonDictionaryDTO();
      if (dictionaryDTO != null && dictionaryDTO.getId() != null) {
        dictionaryInfoList = getDictionaryFaultInfoDTOsByDictionaryId(dictionaryDTO.getId());
      }
    } else {
      dictionaryDTO = getDictionaryDTOByVehicleModelId(vehicleModelId);
      if (dictionaryDTO != null && dictionaryDTO.getId() != null) {
        dictionaryInfoList = getDictionaryFaultInfoDTOsByDictionaryId(dictionaryDTO.getId());
      }
    }
    UpdateFaultDictionaryResponse response = null;
    if (dictionaryDTO != null && StringUtils.isNotBlank(dictionaryDTO.getDictionaryVersion()) && dictionaryDTO.getDictionaryVersion().equals(dicVersion)){
      return new UpdateFaultDictionaryResponse(MessageCode.toApiResponse(MessageCode.UPDATE_FAULT_DICTIONARY_FAIL, "当前字典版本已经是最新版了"));
    }
    if (CollectionUtils.isNotEmpty(dictionaryInfoList)) {
      response = new UpdateFaultDictionaryResponse(MessageCode.toApiResponse(MessageCode.UPDATE_FAULT_DICTIONARY_SUCCESS));
      response.setDictionaryInfoList(dictionaryInfoList);
      response.setDictionaryDTO(dictionaryDTO);
    } else  {
      response = new UpdateFaultDictionaryResponse(MessageCode.toApiResponse(MessageCode.UPDATE_FAULT_DICTIONARY_FAIL, "字典版本为空"));
    }
    return response;
   }

  @Override
  public DictionaryDTO getDictionaryDTOById(Long id) {
    if(id == null){
      return null;
    }
    Dictionary dictionary = productDaoManager.getWriter().getById(Dictionary.class,id);
    if(dictionary != null){
      return dictionary.toDTO();
    }
    return null;
  }

  @Override
  public List<DictionaryFaultInfoDTO> getDictionaryFaultInfoDTOsByDictionaryId(Long dictionaryId) {
    ProductWriter writer = productDaoManager.getWriter();
    List<DictionaryFaultInfoDTO> dictionaryFaultInfoDTOList = new ArrayList<DictionaryFaultInfoDTO>();
    List<DictionaryFaultInfo> infoList = writer.getDictionaryFaultInfo(dictionaryId);
    if (CollectionUtils.isEmpty(infoList)) {
      return dictionaryFaultInfoDTOList;
    }
    for (DictionaryFaultInfo info : infoList) {
      dictionaryFaultInfoDTOList.add(info.toDTO());
    }
    return dictionaryFaultInfoDTOList;
  }

  @Override
  public DictionaryDTO getCommonDictionaryDTO() {
    Dictionary dictionary = productDaoManager.getWriter().getCommonDictionary();
    if (dictionary != null) {
      return dictionary.toDTO();
    } else {
      return null;
    }
  }

  @Override
  public DictionaryDTO getDictionaryDTOByVehicleModelId(Long vehicleModelId) {
    if (vehicleModelId == null) {
      return null;
    }
    VehicleDictionary vehicleDictionary = productDaoManager.getWriter().getVehicleDictionaryByVehicleModelId(vehicleModelId);

    if (vehicleDictionary != null && vehicleDictionary.getDictionaryId() != null) {
      return getDictionaryDTOById(vehicleDictionary.getDictionaryId());
    }else {
      return null;
    }
  }

  @Override
  public Map<String, List<DictionaryFaultInfoDTO>> getDictionaryFaultInfoMapByBrandIdAndCodes(Long brandId, Set<String> codes) {
    Map<String, List<DictionaryFaultInfoDTO>> dictionaryFaultInfoMap = new HashMap<String, List<DictionaryFaultInfoDTO>>();
    if(CollectionUtils.isEmpty(codes)){
      return dictionaryFaultInfoMap;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<DictionaryFaultInfo> dictionaryFaultInfoList = new ArrayList<DictionaryFaultInfo>();
    dictionaryFaultInfoList.addAll(writer.getCommonDictionaryFaultInfo(codes));
    dictionaryFaultInfoList.addAll(writer.getDictionaryFaultInfoByBrandIdAndCodes(brandId,codes));
    if(CollectionUtils.isNotEmpty(dictionaryFaultInfoList)){
      for(DictionaryFaultInfo dictionaryFaultInfo :dictionaryFaultInfoList){
        if(dictionaryFaultInfo != null && StringUtils.isNotEmpty(dictionaryFaultInfo.getFaultCode())){
          List<DictionaryFaultInfoDTO> dictionaryFaultInfoDTOs = dictionaryFaultInfoMap.get(dictionaryFaultInfo.getFaultCode());
          if(dictionaryFaultInfoDTOs == null){
            dictionaryFaultInfoDTOs = new ArrayList<DictionaryFaultInfoDTO>();
          }
          dictionaryFaultInfoDTOs.add(dictionaryFaultInfo.toDTO());
          dictionaryFaultInfoMap.put(dictionaryFaultInfo.getFaultCode(),dictionaryFaultInfoDTOs);
        }
      }
    }
    return dictionaryFaultInfoMap;
  }

  @Override
  public void addFaultCodeCategoryBackgroundInfo(List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOs) {
    if (CollectionUtils.isNotEmpty(appVehicleFaultInfoDTOs)) {
      Map<Long, Set<String>> vehicleIdFaultCodeMap = new HashMap<Long, Set<String>>();
      Map<Long, Map<String, List<DictionaryFaultInfoDTO>>> vehicleFaultInfoDTOsMap = new HashMap<Long, Map<String, List<DictionaryFaultInfoDTO>>>();
      for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : appVehicleFaultInfoDTOs) {
        if (appVehicleFaultInfoDTO != null
            && appVehicleFaultInfoDTO.getAppVehicleId() != null
            && StringUtils.isNotBlank(appVehicleFaultInfoDTO.getErrorCode())) {
          Set<String> faultCodes = vehicleIdFaultCodeMap.get(appVehicleFaultInfoDTO.getAppVehicleId());
          if (faultCodes == null) {
            faultCodes = new HashSet<String>();
          }
          faultCodes.add(appVehicleFaultInfoDTO.getErrorCode());
          vehicleIdFaultCodeMap.put(appVehicleFaultInfoDTO.getAppVehicleId(), faultCodes);
        }
      }
      if (MapUtils.isNotEmpty(vehicleIdFaultCodeMap)) {
        IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
        for (Long vehicleId : vehicleIdFaultCodeMap.keySet()) {
          Set<String> faultCodes = vehicleIdFaultCodeMap.get(vehicleId);
          AppVehicleDTO appVehicleDTO = appUserVehicleObdService.getAppVehicleById(vehicleId);
          if (appVehicleDTO != null) {
            Map<String, List<DictionaryFaultInfoDTO>> faultInfoMap = getDictionaryFaultInfoMapByBrandIdAndCodes(
                appVehicleDTO.getVehicleBrandId(), faultCodes);
            vehicleFaultInfoDTOsMap.put(vehicleId, faultInfoMap);
          }
        }
      }
      if (MapUtils.isNotEmpty(vehicleFaultInfoDTOsMap)) {
        for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : appVehicleFaultInfoDTOs) {
          if (appVehicleFaultInfoDTO != null
              && appVehicleFaultInfoDTO.getAppVehicleId() != null
              && StringUtils.isNotBlank(appVehicleFaultInfoDTO.getErrorCode())) {
            Map<String, List<DictionaryFaultInfoDTO>> faultCodeDictionaryFaultInfoDTOsMap = vehicleFaultInfoDTOsMap.get(appVehicleFaultInfoDTO.getAppVehicleId());
            if (faultCodeDictionaryFaultInfoDTOsMap != null) {
              appVehicleFaultInfoDTO.setCategoryBackgroundInfo(faultCodeDictionaryFaultInfoDTOsMap.get(appVehicleFaultInfoDTO.getErrorCode()));
            }
          }
        }
      }
    }
  }

  @Override
  public List<DictionaryFaultInfoDTO> getDictionaryFaultInfoDTOsByBrandIdAndCode(Long brandId, String code) {
    Set<String> codeSet = new HashSet<String>();
    if (StringUtils.isNotEmpty(code)) {
      codeSet.add(code);
      Map<String, List<DictionaryFaultInfoDTO>> dicMap = getDictionaryFaultInfoMapByBrandIdAndCodes(brandId, codeSet);
      if (MapUtils.isNotEmpty(dicMap) && CollectionUtils.isNotEmpty(dicMap.get(code))) {
        return dicMap.get(code);
      }
    }
    return new ArrayList<DictionaryFaultInfoDTO>();
  }

  @Override
  public DictionaryFaultInfoDTO getDictionaryFaultInfoDTOByFaultCode(String faultCode) {
    ProductWriter writer = productDaoManager.getWriter();
    DictionaryFaultInfo dictionaryFaultInfo = writer.getDictionaryFaultInfoDTOByFaultCode(faultCode);
    return dictionaryFaultInfo != null ? dictionaryFaultInfo.toDTO() : null;
  }
}
