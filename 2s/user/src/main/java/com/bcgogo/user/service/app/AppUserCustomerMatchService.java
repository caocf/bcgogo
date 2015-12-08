package com.bcgogo.user.service.app;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.DataKind;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.model.Contact;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppUser;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *  * 手机端用户、店铺客户匹配service

 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-9-9
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AppUserCustomerMatchService implements IAppUserCustomerMatchService {
  private static final Logger LOG = LoggerFactory.getLogger(AppUserCustomerMatchService.class);
  @Autowired
  private UserDaoManager userDaoManager;


  @Override
  public Map<Long, List<AppUserDTO>> getAppUserMapByCustomerIds(Set<Long> customerIds) {
    UserWriter writer = userDaoManager.getWriter();
    List<Object[]> customerIdAppUsers = writer.getAppUserMapByCustomerIds(customerIds);
    Map<Long, List<AppUserDTO>> map = new HashMap<Long, List<AppUserDTO>>();
    for (Object[] objects : customerIdAppUsers) {
      Long customerId = (Long)objects[0];
      if(map.get(customerId) == null){
        map.put(customerId, new ArrayList<AppUserDTO>());
      }
      map.get(customerId).add(((AppUser) objects[1]).toDTO());
    }
    return map;
  }

  @Override
  public List<AppUserDTO> getCustomerRelatedAppUserDTOs(Long shopId, Long customerId) {
    List<AppUserDTO> appUserDTOs = new ArrayList<AppUserDTO>();
    if(shopId == null || customerId == null){
      return appUserDTOs;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomer(null, customerId, shopId);
    if(CollectionUtils.isNotEmpty(appUserCustomers)){
        Set<String> appUserNos = new HashSet<String>();
      for(AppUserCustomer appUserCustomer :appUserCustomers){
        appUserNos.add(appUserCustomer.getAppUserNo());
      }
      List<AppUser> appUsers = writer.getAppUserByUserNos(appUserNos);
      if(CollectionUtils.isNotEmpty(appUsers)){
        for(AppUser appUser :appUsers){
          appUserDTOs.add(appUser.toDTO());
        }
      }
    }
    return appUserDTOs;
  }

  @Override
  public Map<String, Set<Long>> getAppUserNoShopIdsMapByAppUserCustomer(Set<String> appUserNos) {
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomerByAppUserNo(appUserNos);
    Map<String, Set<Long>> map = new HashMap<String, Set<Long>>();
    for (AppUserCustomer appUserCustomer : appUserCustomers) {
      Set<Long> set = map.get(appUserCustomer.getAppUserNo());
      if (CollectionUtil.isEmpty(set)) {
        set = new HashSet<Long>();
        map.put(appUserCustomer.getAppUserNo(), set);
      }
      set.add(appUserCustomer.getShopId());
    }
    return map;
  }

  @Override
  public Set<Long> filterMatchVehicleCustomerIds(Set<String> vehicleNos, Set<Long> allCustomerIds, Long shopId) {
    Set<Long> matchCustomerIds = new HashSet<Long>();
    if(CollectionUtils.isNotEmpty(vehicleNos) && CollectionUtils.isNotEmpty(allCustomerIds) && shopId != null){
      UserWriter writer = userDaoManager.getWriter();
      matchCustomerIds =  writer.filterMatchVehicleCustomerIds(vehicleNos,allCustomerIds,shopId);
    }
    return matchCustomerIds;
  }
}
