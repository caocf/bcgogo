package com.bcgogo.product.service.app;

import com.bcgogo.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-2
 * Time: 上午9:51
 */
public interface IAppDictionaryService {

  //根据车型信息、当前版本号获取最新字典版本
  ApiResponse updateFaultDictionary(Long vehicleModelId, String dicVersion);

  //获取通用字典，不包括字典故障码的内容
  DictionaryDTO getCommonDictionaryDTO();

    //根据车型Id获取通用字典 不包括字典故障码的内容
  DictionaryDTO getDictionaryDTOByVehicleModelId(Long vehicleModelId);

  //根据字典Id取字典下的故障码信息
  List<DictionaryFaultInfoDTO> getDictionaryFaultInfoDTOsByDictionaryId(Long dictionaryId);


  DictionaryDTO getDictionaryDTOById(Long id);

  //根据车辆品牌，故障码找出故障描述，包括通用码
  Map<String,List<DictionaryFaultInfoDTO>> getDictionaryFaultInfoMapByBrandIdAndCodes(Long brandId,Set<String> codes);

  //服务器查出客户故障列表之后，组装故障分类，故障背景知识
  void addFaultCodeCategoryBackgroundInfo(List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOs);
  //根据车辆品牌，故障码找出故障描述，包括通用码
  List<DictionaryFaultInfoDTO> getDictionaryFaultInfoDTOsByBrandIdAndCode(Long brandId,String code);

  //根据故障码查故障码信息
  DictionaryFaultInfoDTO getDictionaryFaultInfoDTOByFaultCode(String faultCode);


}