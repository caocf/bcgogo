package com.bcgogo.user.service.obd;

import com.bcgogo.api.*;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.enums.app.OBDSimOperationType;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.common.Result;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.VehicleDTO;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XinyuQiu on 14-6-24.
 */
public interface IObdManagerService {

  List<ObdSimBindDTO> searchObdSimBindDTO(ObdSimSearchCondition condition);

  int countObdSimBindDTO(ObdSimSearchCondition condition);

  List<ObdSimBindDTO> getObdSimBindDTOByShop(ObdSimSearchCondition condition) throws ParseException;

  ObdSimBindDTO getObdSimBindByShopExact(String imei,String mobile) throws BcgogoException;

  int countObdSimBindByShop(ObdSimSearchCondition condition) throws ParseException;

  ImportResult importOBDInventoryFromExcel(ImportContext importContext) throws Exception;

  void  updateOBD(ObdDTO obdDTO);

  ImportResult initImportOBDInventoryFromExcel(ImportContext importContext) throws Exception;

  Map<String,ObdDTO> getImeiObdDTOMap(Set<String> imeiSet,ObdType obdType);

  Map<String,ObdSimDTO> getMobileObdSimDTOMap(Set<String> mobileSet);

  Map<String,ObdSimDTO> getSimNoObdSimDTOMap(Set<String> mobileNoSet);

  void batchCreateObdAndSim(List<ObdSimBindDTO> obdSimBindDTOs);

  Result doStorageOBD (Long shopId,String shopName,ObdSimBindDTO[] obdSimBindDTOs) throws ParseException;

  Result gsmOBDBind(OBDBindDTO bindDTO) throws ParseException,BcgogoException;

  Result gsmOBDBind(VehicleDTO vehicleDTO,String imei,String mobile,Long userId,String shopName,String userName) throws ParseException,BcgogoException;

  void initCreateObdAndSim(List<ObdSimBindDTO> obdSimBindDTOs);

  Result updateSingleObdSim(ObdSimBindDTO obdSimBindDTO) throws Exception;

  List<OBDSimOperationLogDTO> getObdSimOperationLogDTOs(OBDSimOperationLogDTOSearchCondition condition);

  int countObdSimOperationLogDTOs(OBDSimOperationLogDTOSearchCondition condition);

  Result updateMultiObdSim(MultiObdSimUpdateDTO multiObdSimUpdateDTO) throws Exception;

  Result splitObdSimBind(ObdSimBindDTO obdSimBindDTO);

  int countObdImeiSuggestion(ObdImeiSuggestion suggestion);

  List<ObdSimBindDTO> getObdImeiSuggestion(ObdImeiSuggestion suggestion);

  int countObdVersionSuggestion(ObdVersionSuggestion suggestion);

  List<ObdSimBindDTO> getObdVersionSuggestion(ObdVersionSuggestion suggestion);

  Result combineObdSim(ObdSimBindDTO obdSimBindDTO) throws Exception;

  int countObdSimMobileSuggestion(SimMobileSuggestion suggestion);

  List<ObdSimBindDTO> getObdSimMobileSuggestion(SimMobileSuggestion suggestion);

  Result deleteObdSim(ObdSimBindDTO obdSimBindDTO) throws Exception;

  int countObdOutStorageShopNameSuggestion(ShopNameSuggestion suggestion);

  List<ShopDTO> getObdOutStorageShopNameSuggestion(ShopNameSuggestion suggestion);

  int countAgentNameSuggestion(AgentNameSuggestion suggestion);

  List<UserDTO> getAgentNameSuggestion(AgentNameSuggestion suggestion);

  int countStaffNameSuggestion(AgentNameSuggestion suggestion);

  List<UserDTO> getStaffNameSuggestion(AgentNameSuggestion suggestion);

  Result obdSimOutStorage(ObdSimOutStorageDTO outStorageDTO);

  Result obdSimSell(ObdSimOutStorageDTO outStorageDTO);

  void  createOBDSimOperationLog(ObdSimBindDTO obdSimBindDTO,OBDSimOperationType operationType);

  ObdDTO getObdDTOById(Long obdId);

  ObdSimDTO getObdSimDTOById(Long simId);

  Result obdSimReturn(ObdSimReturnDTO obdSimReturnDTO);

  ObdDTO getObdByImei(String imei);

  ObdDTO getObdByImeiAndMobile(String imei,String mobile);

}
