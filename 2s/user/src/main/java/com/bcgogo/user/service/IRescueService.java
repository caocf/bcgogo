package com.bcgogo.user.service;

import com.bcgogo.api.MileageDTO;
import com.bcgogo.api.RescueDTO;
import com.bcgogo.api.response.InsuranceCompanyResponse;
import com.bcgogo.api.response.OneKeyRescueResponse;
import com.bcgogo.txn.dto.pushMessage.mileage.MileageInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.sos.SosInfoSearchConditionDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: zhangjie
 * Date: 2015-4-23
 * Time: 17:50
 */
public interface IRescueService {

  OneKeyRescueResponse findOneKeyRescueDetails(String appUserNo);

  InsuranceCompanyResponse findInsuranceCompanyResponseDetails();

  void saveOrUpdateRescue(RescueDTO rescueDTO);

  List<RescueDTO> getRescueDTOsByShopId(Long shopId, int start, int limit);

  List<RescueDTO> getRescueDTOsByShopId(Long shopId, SosInfoSearchConditionDTO sosInfoSearchConditionDTO);

  int countGetRescueDTOs(Long shopId,SosInfoSearchConditionDTO sosInfoSearchConditionDTO);

  List<MileageDTO> getMileageDTOsByShopId(Long shopId, MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO);

  int countGetMileageDTOs(Long shopId,MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO);

  void deleteShopSosInfo(Long... ids);

  void detailShopSosInfo(Long... ids);

  void updateShopMileageInfo(String appUserNo,long shopId);

}
