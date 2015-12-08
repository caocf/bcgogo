package com.bcgogo.user.service.app;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.model.UserWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 手机端用户、店铺客户匹配service
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-9-9
 * Time: 下午1:51
 * To change this template use File | Settings | File Templates.
 */
public interface IAppUserCustomerMatchService {
  Map<Long, List<AppUserDTO>> getAppUserMapByCustomerIds(Set<Long> customerIds);

  /**
   * 找到某个customer关联的appUser
   * @param shopId
   * @param customerId
   * @return
   */
  List<AppUserDTO> getCustomerRelatedAppUserDTOs(Long shopId,Long customerId);

  Map<String, Set<Long>> getAppUserNoShopIdsMapByAppUserCustomer(Set<String> appUserNos);

  Set<Long> filterMatchVehicleCustomerIds(Set<String> vehicleNos, Set<Long> allCustomerIds, Long shopId);


}