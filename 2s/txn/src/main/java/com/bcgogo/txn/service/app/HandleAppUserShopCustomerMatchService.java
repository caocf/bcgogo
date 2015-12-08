package com.bcgogo.txn.service.app;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.YesNo;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-5
 * Time: 下午1:40
 */
@Component
public class HandleAppUserShopCustomerMatchService implements IHandleAppUserShopCustomerMatchService {
  private static final Logger LOG = LoggerFactory.getLogger(HandleAppUserShopCustomerMatchService.class);

  private static final int matchSize = 100;

  //从appUserId =0 开始扫描下去，第二次从第一次最后一个开始扫描,一次扫描matchSize个匹配数据 。
  // 指定assignAppUserId 的话，只扫描这个帐号
  @Override
  public void handleAppUserCustomerMatch(Long assignAppUserId) {
    handelVehicleNoMatch(assignAppUserId);
    handelCustomerMobileMatch(assignAppUserId);
    handelVehicleMobileMatch(assignAppUserId);
  }

  //根据车牌号去匹配
  private void handelVehicleNoMatch(Long assignAppUserId) {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter writer = userDaoManager.getWriter();
    Long startAppUserId = 0L;
    int loopTimes = 0;
    do {
      loopTimes++;
      if (loopTimes > 10000) {
        LOG.error("当前匹配App车牌号已经执行了10000次,可能已经死循环了，跳出循环。等待下一个定时钟。");
      }
      List<AppUserCustomerDTO> appUserCustomerDTOs = writer.getMatchAppUserCustomerDTOByVehicleNo(startAppUserId, assignAppUserId, matchSize,ConfigUtils.getBlueBoothMatchShopVersion());
      if (CollectionUtils.isEmpty(appUserCustomerDTOs)) {
        break;
      }
      Set<Long> allCustomerIds = new HashSet<Long>();
      Map<Long, Set<Long>> allShopVehicleIdsMap = new HashMap<Long, Set<Long>>();
      for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
        if (appUserCustomerDTO != null && validateAppUserCustomerDTO(appUserCustomerDTO)) {
          AppUserDTO appUserDTO = appUserCustomerDTO.getAppUserDTO();
          if (appUserDTO != null && appUserDTO.getId() != null && appUserDTO.getId() > startAppUserId) {
            startAppUserId = appUserDTO.getId();
          }
          if (saveAppUserCustomer(appUserCustomerDTO)) {
            allCustomerIds.add(appUserCustomerDTO.getCustomerId());
            allShopVehicleIdsMap = addShopVehicleId(allShopVehicleIdsMap, appUserCustomerDTO.getShopId(), appUserCustomerDTO.getShopVehicleId());
          }
        }
      }
      reindexCustomerOrVehicle(allCustomerIds, allShopVehicleIdsMap);
    } while (true);
  }

  //根据手机去匹配
  private void handelCustomerMobileMatch(Long assignAppUserId) {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter writer = userDaoManager.getWriter();
    Long startAppUserId = 0L;
    int loopTimes = 0;
    do {
      loopTimes++;
      if (loopTimes > 10000) {
        LOG.error("当前匹配客户手机号已经执行了10000次,可能已经死循环了，跳出循环。等待下一个定时钟。");
        break;
      }
      List<AppUserCustomerDTO> appUserCustomerDTOs = writer.getMatchAppUserCustomerDTOByMobile(startAppUserId, assignAppUserId,
          matchSize, ConfigUtils.getBlueBoothMatchShopVersion());
      if (CollectionUtils.isEmpty(appUserCustomerDTOs)) {
        break;
      }
      Set<Long> allCustomerIds = new HashSet<Long>();
      for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
        if (appUserCustomerDTO != null && validateAppUserCustomerDTO(appUserCustomerDTO)) {
          AppUserDTO appUserDTO = appUserCustomerDTO.getAppUserDTO();
          if (appUserDTO != null && appUserDTO.getId() != null && appUserDTO.getId() > startAppUserId) {
            startAppUserId = appUserDTO.getId();
          }
          if (saveAppUserCustomer(appUserCustomerDTO)) {
            allCustomerIds.add(appUserCustomerDTO.getCustomerId());
          }
        }
      }
      reindexCustomerOrVehicle(allCustomerIds, null);
    } while (true);
  }

  //根据车主手机去匹配
  private void handelVehicleMobileMatch(Long assignAppUserId) {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter writer = userDaoManager.getWriter();

    Long startAppUserId = 0L;
    int loopTimes = 0;
    do {
      loopTimes++;
      if (loopTimes > 10000) {
        LOG.error("当前匹配车主手机号已经执行了10000次,可能已经死循环了，跳出循环。等待下一个定时钟。");
        break;
      }
      List<AppUserCustomerDTO> appUserCustomerDTOs = writer.getMatchAppUserCustomerDTOByVehicleMobile(startAppUserId, assignAppUserId,
          matchSize,ConfigUtils.getBlueBoothMatchShopVersion());
      if (CollectionUtils.isEmpty(appUserCustomerDTOs)) {
        break;
      }
      Set<Long> allCustomerIds = new HashSet<Long>();
      Map<Long, Set<Long>> allShopVehicleIdsMap = new HashMap<Long, Set<Long>>();
      for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
        if (appUserCustomerDTO != null && validateAppUserCustomerDTO(appUserCustomerDTO)) {
          AppUserDTO appUserDTO = appUserCustomerDTO.getAppUserDTO();
          if (appUserDTO != null && appUserDTO.getId() != null && appUserDTO.getId() > startAppUserId) {
            startAppUserId = appUserDTO.getId();
          }
          if (saveAppUserCustomer(appUserCustomerDTO)) {
            allCustomerIds.add(appUserCustomerDTO.getCustomerId());
            allShopVehicleIdsMap = addShopVehicleId(allShopVehicleIdsMap, appUserCustomerDTO.getShopId(), appUserCustomerDTO.getShopVehicleId());
          }
        }
      }
      reindexCustomerOrVehicle(allCustomerIds, allShopVehicleIdsMap);
    } while (true);
  }

  public boolean saveAppUserCustomer(AppUserCustomerDTO appUserCustomerDTO) {
    boolean isSaved = false;
    if (appUserCustomerDTO != null && validateAppUserCustomerDTO(appUserCustomerDTO)) {
      UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomerAndVehicle(appUserCustomerDTO.getAppUserNo(),
            appUserCustomerDTO.getCustomerId(), appUserCustomerDTO.getShopId(), appUserCustomerDTO.getAppVehicleId(),
            appUserCustomerDTO.getShopVehicleId(), appUserCustomerDTO.getMatchType());
        if (CollectionUtils.isEmpty(appUserCustomers)) {
          AppUserCustomer appUserCustomer = new AppUserCustomer();
          appUserCustomer.fromDTO(appUserCustomerDTO);
          writer.save(appUserCustomer);
          appUserCustomerDTO.setId(appUserCustomer.getId());
          isSaved = true;
        } else {
          isSaved = false;
          for (AppUserCustomer appUserCustomer : appUserCustomers) {
            boolean isNeedToUpdate = false;
            if (YesNo.YES.equals(appUserCustomerDTO.getIsVehicleNoMatch())
                && !appUserCustomerDTO.getIsVehicleNoMatch().equals(appUserCustomer.getIsVehicleNoMatch())) {
              isNeedToUpdate = true;
              appUserCustomer.setIsVehicleNoMatch(appUserCustomerDTO.getIsVehicleNoMatch());
            }
            if (YesNo.YES.equals(appUserCustomerDTO.getIsMobileMatch())
                && !appUserCustomerDTO.getIsMobileMatch().equals(appUserCustomer.getIsMobileMatch())) {
              isNeedToUpdate = true;
              appUserCustomer.setIsMobileMatch(appUserCustomerDTO.getIsMobileMatch());
            }
            if (isNeedToUpdate) {
              writer.update(appUserCustomer);
            }
          }
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    return isSaved;
  }

  public boolean validateAppUserCustomerDTO(AppUserCustomerDTO appUserCustomerDTO) {
    boolean isValidateSuccess = false;
    if (appUserCustomerDTO != null) {
      if (StringUtils.isNotBlank(appUserCustomerDTO.getAppUserNo())
          && appUserCustomerDTO.getShopId() != null
          && appUserCustomerDTO.getCustomerId() != null) {
        isValidateSuccess = true;
      }
    }
    return isValidateSuccess;
  }

  private Map<Long, Set<Long>> addShopVehicleId(Map<Long, Set<Long>> allShopVehicleIdsMap, Long shopId, Long shopVehicleId) {
    if(allShopVehicleIdsMap == null){
      allShopVehicleIdsMap = new HashMap<Long, Set<Long>>();
    }
    Set<Long> vehicleIds = allShopVehicleIdsMap.get(shopId);
    if(vehicleIds == null){
      vehicleIds = new HashSet<Long>();
    }
    vehicleIds.add(shopVehicleId);
    return allShopVehicleIdsMap;
  }

  private void reindexCustomerOrVehicle(Set<Long> allCustomerIds, Map<Long, Set<Long>> allShopVehicleIdsMap) {
    //客户做索引
    if (CollectionUtils.isNotEmpty(allCustomerIds)) {
      ICustomerOrSupplierSolrWriteService supplierSolrWriteService = ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
      for (Long customerId : allCustomerIds) {
        supplierSolrWriteService.reindexCustomerByCustomerId(customerId);
      }
    }
    //车辆做索引
    if (MapUtils.isNotEmpty(allShopVehicleIdsMap)) {
      IVehicleSolrWriterService vehicleSolrWriterService = ServiceManager.getService(IVehicleSolrWriterService.class);
      Iterator iterator = allShopVehicleIdsMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry) iterator.next();
        Set<Long> vehicleIds = (HashSet<Long>) entry.getValue();
        Long shopId = (Long) entry.getKey();
        if (CollectionUtils.isNotEmpty(vehicleIds)) {
          try {
            vehicleSolrWriterService.createVehicleSolrIndex(shopId, vehicleIds.toArray(new Long[vehicleIds.size()]));
          } catch (Exception e) {
            LOG.error("appUser customer vehicle 匹配时，vehicle索引出错");
          }
        }
      }
    }
  }
}
