package com.bcgogo.txn.service.pushMessage.faultCode;

import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.exception.PageException;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.ShopFaultInfoListResult;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 上午11:53
 */
public interface IShopFaultInfoService {
  int countShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition);

  ShopFaultInfoListResult searchShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition) throws PageException, ParseException;

  List<String> getShopFaultInfoVehicleNoSuggestion(Long shopId, String keyword);

  List<String> getShopFaultInfoMobileSuggestion(Long shopId, String keyword);

  void deleteShopFaultInfo(Long... id);

  void updateShopFaultInfo2SendMessage(Long id);

  void updateShopFaultInfo2CreateAppointOrder(Long id);

  List<FaultInfoToShopDTO> getShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition);

  List<FaultInfoToShopDTO> getFaultInfoToShopDTOsByIds(Long shopId, Long... ids);

  String getUnhandledFaultCodes(Long shopId, String vehicleNo);

  int countShopFaultInfoByVehicleNo(Long shopId, String licenceNo);

  FaultInfoToShopDTO getShopFaultInfo(Long id);

  Map<String, Object> getSopFaultInfoMsgContent(Long shopId,String code, String time, FaultAlertType faultAlertType,String faultAlertTypeValue,String licenceNo);

  public List<FaultInfoToShopDTO> getFaultInfoListByCondition(FaultInfoSearchConditionDTO searchCondition);

  List<FaultInfoToShopDTO> findShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition);

  int countShopFaultInfoList_(FaultInfoSearchConditionDTO searchCondition);

  List<FaultInfoToShop>  getShopFaultInfoByFaultCode(FaultInfoSearchConditionDTO searchCondition);
}
