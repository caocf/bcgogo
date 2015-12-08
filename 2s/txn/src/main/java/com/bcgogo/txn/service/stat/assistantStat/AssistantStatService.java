package com.bcgogo.txn.service.stat.assistantStat;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.assistantStat.*;
import com.bcgogo.enums.user.MemberCardType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.model.Product;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.assistantStat.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.model.assistantStat.*;
import com.bcgogo.txn.service.IProductHistoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.model.MemberCard;
import com.bcgogo.user.model.SalesMan;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 员工业绩统计专用service（新）
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AssistantStatService implements IAssistantStatService {

  @Autowired
  private TxnDaoManager txnDaoManager;
  private static final Logger LOG = LoggerFactory.getLogger(AssistantStatService.class);

  private static final int DEFAULT_START_YEAR = 2012;//默认统计月份从2012年开始
  private static final int MONTH = 12;
  private static final int PERCENT = 100;


  /**
   * 根据店铺id进行员工业绩统计
   *
   * @param statShopId
   */
  public void assistantStat(Long statShopId) {
    try {

      IConfigService configService = ServiceManager.getService(IConfigService.class);

      List<Long> shopIdList = null;

      if (statShopId == null) {
        shopIdList = configService.getShopId();
      } else {
        shopIdList = new ArrayList<Long>();
        shopIdList.add(statShopId);
      }

      Map<Long, Long> washBeautyShopIdMap = this.getWashBeautyShopIdList(statShopId);
      Map<Long, Long> memberCardOrderShopIdMap = this.getMemberCardOrderShopIdList(statShopId);
      Map<Long, Long> memberCardReturnShopIdMap = this.getMemberCardReturnShopIdList(statShopId);
      Map<Long, Long> salesOrderShopIdMap = this.getSalesOrderShopIdList(statShopId);
      Map<Long, Long> salesReturnShopIdMap = this.getSalesReturnShopIdList(statShopId);

      if (CollectionUtils.isEmpty(shopIdList)) {
        return;
      }

      Calendar calendar = Calendar.getInstance();
      int currentYear = calendar.get(Calendar.YEAR);

      long startTime;
      long endTime;

      for (Long shopId : shopIdList) {
        try{

        Long statTime = DateUtil.get6clock(System.currentTimeMillis());
        LOG.warn("店铺shop_id:" + shopId + "开始统计员工业绩");

        ShopDTO shopDTO = configService.getShopById(shopId);


        this.deleteAssistantAchievementStat(shopId);

        Map<Long, SalesManDTO> salesManDTOMap = this.getSalesManMap(shopId);

        if(MapUtils.isEmpty(salesManDTOMap)){
          continue;
        }

        Map<Long, DepartmentDTO> departmentDTOMap = this.getDepartmentMap(shopId);

        for (int year = DEFAULT_START_YEAR; year <= currentYear; year++) {

          int index = (currentYear == year ? DateUtil.getCurrentMonth() : MONTH);

          for (int month = 1; month <= index; month++) {
            calendar.set(year, month - 1, 1, 0, 0, 0);
            startTime = calendar.getTimeInMillis();
            calendar.add(Calendar.MONTH, 1);
            endTime = calendar.getTimeInMillis();

            //这个统计结果不区分服务 即所有的服务都记录到一起  提成计算到一起 key 为salesManId
            Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap = new HashMap<Long, AssistantAchievementStatDTO>();

            //这个统计结果key为salesManId value为Map key为serviceId value为统计值
            Map<Long,Map<Long,AssistantAchievementStatDTO>> washBeautyAchievementStatDTOMap = new HashMap<Long, Map<Long,AssistantAchievementStatDTO>>();

            if (washBeautyShopIdMap.containsKey(shopId)) {
              calculateWashBeautyAssistant(shopId, startTime, endTime, year, month, salesManDTOMap, departmentDTOMap,
                  assistantAchievementStatDTOMap,washBeautyAchievementStatDTOMap,statTime);
            }

            if (!ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) {
              if (memberCardOrderShopIdMap.containsKey(shopId)) {
                calculateMemberAssistant(shopId, startTime, endTime, year, month, salesManDTOMap, departmentDTOMap, assistantAchievementStatDTOMap,statTime);
              }
              if (memberCardReturnShopIdMap.containsKey(shopId)) {
                calculateMemberReturnAssistant(shopId, startTime, endTime, year, month, salesManDTOMap, departmentDTOMap, assistantAchievementStatDTOMap,statTime);
              }
              calculateRepairServiceAssistant(shopId, startTime, endTime, year, month, salesManDTOMap, departmentDTOMap, assistantAchievementStatDTOMap,washBeautyAchievementStatDTOMap,statTime);
              calculateRepairItemAssistant(shopId, startTime, endTime, year, month, salesManDTOMap, departmentDTOMap, assistantAchievementStatDTOMap,statTime);
            }
            if (salesOrderShopIdMap.containsKey(shopId)) {
              calculateSaleAssistant(shopId, startTime, endTime, year, month,salesManDTOMap, departmentDTOMap, assistantAchievementStatDTOMap,statTime);
            }
            if (salesReturnShopIdMap.containsKey(shopId)) {
              calculateSaleReturnAssistant(shopId, startTime, endTime, year, month, salesManDTOMap, departmentDTOMap, assistantAchievementStatDTOMap,statTime);
            }



            if (CollectionUtils.isNotEmpty(assistantAchievementStatDTOMap.values())) {
              this.saveAchievementStat(shopId, year, month, assistantAchievementStatDTOMap);
            }

            if (MapUtils.isNotEmpty(washBeautyAchievementStatDTOMap)) {
              this.saveAssistantAchievementStat(shopId,year,month,washBeautyAchievementStatDTOMap);
            }

          }
        }

        this.calculateUnConfigDepartment(shopId);
        this.calculateUnConfigProduct(shopId);
        this.calculateUnConfigService(shopId);


        if (!ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) {
          this.deleteAssistantMemberRecord(shopId, statTime);
          this.deleteAssistantServiceRecord(shopId, statTime);
        }
        this.deleteAssistantBusinessAccountRecord(shopId, statTime);
        this.deleteAssistantProductRecord(shopId, statTime);

        LOG.warn("店铺shop_id:" + shopId + "结束统计员工业绩");

      }catch (Exception e){
          LOG.error("店铺shop_id:" + shopId + "执行营业统计报错"+e.getMessage(), e);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  public void calculateUnConfigService(Long shopId) {
    List<ShopAchievementConfig> configList = new ArrayList<ShopAchievementConfig>();
    Set<Long> deleteConfigList = new HashSet<Long>();
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      List<ServiceDTO> serviceDTOs = txnService.getAllServiceDTOByShopId(shopId);
      Long vestDate = System.currentTimeMillis();

      if (CollectionUtil.isEmpty(serviceDTOs)) {
        return;
      }

      Map<Long, ServiceAchievementHistory> serviceMap = new HashMap<Long, ServiceAchievementHistory>();
      List<ServiceAchievementHistory> serviceAchievementHistoryList = this.getServiceAchievementHistory(shopId, null);
      if (CollectionUtil.isNotEmpty(serviceAchievementHistoryList)) {
        for (ServiceAchievementHistory history : serviceAchievementHistoryList) {
          serviceMap.put(history.getServiceId(), history);
        }
      }
      List<Long> serviceIdList = new ArrayList<Long>();
      for (ServiceDTO serviceDTO : serviceDTOs) {
        serviceIdList.add(serviceDTO.getId());
      }

      Map<Long, Long> unConfigMap = this.getARecordIdFromSAConfig(shopId, AssistantRecordType.SERVICE, serviceIdList);


      for (ServiceDTO serviceDTO : serviceDTOs) {
        ServiceAchievementHistory achievementHistory = serviceMap.get(serviceDTO.getId());

        boolean unConfig = unConfigMap.containsKey(serviceDTO.getId());

        if (serviceDTO.getStatus() == ServiceStatus.DISABLED || achievementHistory != null) {
          if (unConfig) {
            deleteConfigList.add(unConfigMap.get(serviceDTO.getId()));
          }
          continue;
        }

        if ((!unConfig) && achievementHistory == null) {
          ShopAchievementConfig shopAchievementConfig = new ShopAchievementConfig(shopId, vestDate, AssistantRecordType.SERVICE, serviceDTO.getId());
          configList.add(shopAchievementConfig);
        }
      }
      this.saveOrUpdateShopAchievementConfig(configList);
      this.deleteShopAchievementConfig(deleteConfigList);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  public void calculateUnConfigDepartment(Long shopId) {
    List<ShopAchievementConfig> configList = new ArrayList<ShopAchievementConfig>();
    Set<Long> deleteConfigList = new HashSet<Long>();

    try {
      IUserService userService = ServiceManager.getService(IUserService.class);
      Long vestDate = System.currentTimeMillis();

      List<SalesManDTO> salesManDTOList = userService.getSalesManByDepartmentId(shopId, null);
      if (CollectionUtil.isEmpty(salesManDTOList)) {
        return;
      }

      List<AssistantAchievementHistory> historyList = this.geAssistantAchievementHistory(shopId, null);

      Map<Long, Long> assistantMap = new HashMap<Long, Long>();
      if (CollectionUtils.isNotEmpty(historyList)) {
        for (AssistantAchievementHistory history : historyList) {
          assistantMap.put(history.getAssistantId(), history.getAssistantId());
        }
      }

      List<Long> salesManIdSet = new ArrayList<Long>();
      for (SalesManDTO salesManDTO : salesManDTOList) {
        salesManIdSet.add(salesManDTO.getId());
      }

      Map<Long, Long> unConfigMap = this.getARecordIdFromSAConfig(shopId, AssistantRecordType.ASSISTANT_DEPARTMENT, salesManIdSet);

      for (SalesManDTO salesManDTO : salesManDTOList) {


        boolean config = assistantMap.containsKey(salesManDTO.getId());
        boolean unConfig = unConfigMap.containsKey(salesManDTO.getId());

        if (salesManDTO.getStatus() == SalesManStatus.DELETED || salesManDTO.getDepartmentId() != null || config) {
          if(unConfig){
            deleteConfigList.add(unConfigMap.get(salesManDTO.getId()));
          }
          continue;
        }

        if((!unConfig)&&(!config)){
          ShopAchievementConfig achievementConfig = new ShopAchievementConfig(shopId, vestDate, AssistantRecordType.SERVICE, salesManDTO.getId());
          configList.add(achievementConfig);
        }
      }

      this.saveOrUpdateShopAchievementConfig(configList);
      this.deleteShopAchievementConfig(deleteConfigList);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }


  public Map<Long,Long> getARecordIdFromAHistory(Long shopId, AssistantRecordType recordType,List<Long> recordIdList) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Long> recordIds = writer.getARecordIdFromAHistory(shopId, recordType, recordIdList);

    Map<Long, Long> recordMap = new HashMap<Long, Long>();
    if (CollectionUtil.isEmpty(recordIds)) {
      return recordMap;
    }
    for (Long id : recordIds) {
      recordMap.put(id, id);
    }
    recordIds.clear();
    recordIds = null;
    return recordMap;
  }

  public Map<Long, Long> getARecordIdFromSAConfig(Long shopId, AssistantRecordType recordType, List<Long> recordIdList) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long, Long> recordMap = writer.getARecordIdFromSAConfig(shopId, recordType, recordIdList);
    return recordMap;
  }





  public void calculateUnConfigProduct(Long shopId) {
    try {
      IProductService productService = ServiceManager.getService(IProductService.class);
      Long vestDate = System.currentTimeMillis();

      int start = 0;
      while (true) {

        List<ShopAchievementConfig> achievementConfigList = new ArrayList<ShopAchievementConfig>();
        Set<Long> deleteList = new HashSet<Long>();


        List<Long> productLocalInfoIdList = productService.getProductLocalInfoIdList(shopId, start, 500);
        if (CollectionUtils.isEmpty(productLocalInfoIdList)) break;
        int size = productLocalInfoIdList.size();
        start += size;

        Map<Long, Long> configMap = this.getARecordIdFromAHistory(shopId, AssistantRecordType.PRODUCT, productLocalInfoIdList);
        Map<Long, Long> unConfigMap = this.getARecordIdFromSAConfig(shopId, AssistantRecordType.PRODUCT, productLocalInfoIdList);


        List<Object[]> productDataList = productService.getProductDataByProductLocalInfoId(shopId, productLocalInfoIdList.toArray(new Long[size]));
        if (CollectionUtil.isNotEmpty(productDataList)) {
          for (Object[] obj : productDataList) {
            if (ArrayUtil.isEmpty(obj) || obj.length != 2) {
              continue;
            }
            Product product = (Product) obj[0];
            ProductLocalInfo productLocalInfo = (ProductLocalInfo) obj[1];

            if (product == null || productLocalInfo == null) {
              continue;
            }
            Long productLocalInfoId = productLocalInfo.getId();
            boolean config = configMap.containsKey(productLocalInfoId);
            boolean unConfig = unConfigMap.containsKey(productLocalInfoId);

            if (product.getStatus() == ProductStatus.DISABLED || config) {
              if (unConfig) {
                deleteList.add(unConfigMap.get(productLocalInfoId));
              }
              continue;
            }

            if ((!config) && (!unConfig)) {
              ShopAchievementConfig shopAchievementConfig = new ShopAchievementConfig(shopId, vestDate, AssistantRecordType.PRODUCT, productLocalInfo.getId());
              achievementConfigList.add(shopAchievementConfig);
            }
          }
        }

        this.deleteShopAchievementConfig(deleteList);
        this.saveOrUpdateShopAchievementConfig(achievementConfigList);
      }


    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public void saveAssistantAchievementStat(Long shopId, int year, int month, Map<Long,Map<Long,AssistantAchievementStatDTO>> washBeautyAchievementStatDTOMap) {
    //key departmentId value:Map key:serviceId value serviceId对应的统计
    Map<Long, Map<Long, AssistantAchievementStatDTO>> departmentAchievementStatDTOMap = new HashMap<Long, Map<Long, AssistantAchievementStatDTO>>();

    for (Map<Long, AssistantAchievementStatDTO> map : washBeautyAchievementStatDTOMap.values()) {
      if (MapUtils.isEmpty(map)) {
        continue;
      }
      for (AssistantAchievementStatDTO statDTO : map.values()) {
        if (statDTO == null || statDTO.getDepartmentId() == null) {
          continue;
        }

        Map<Long, AssistantAchievementStatDTO> statDTOMap = departmentAchievementStatDTOMap.get(statDTO.getDepartmentId());
        if (statDTOMap == null) {
          statDTOMap = new HashMap<Long, AssistantAchievementStatDTO>();
        }

        AssistantAchievementStatDTO departmentAchievementStat = statDTOMap.get(statDTO.getServiceId());

        if (departmentAchievementStat == null) {
          departmentAchievementStat = new AssistantAchievementStatDTO();
          departmentAchievementStat.setShopId(shopId);
          departmentAchievementStat.setStatYear(year);
          departmentAchievementStat.setStatMonth(month);
          departmentAchievementStat.setAchievementStatType(AchievementStatType.DEPARTMENT);
          departmentAchievementStat.setDepartmentId(statDTO.getDepartmentId());
          departmentAchievementStat.setStatTime(statDTO.getStatTime());
          departmentAchievementStat.setDepartmentName(statDTO.getDepartmentName());
          departmentAchievementStat.setServiceId(statDTO.getServiceId());
          departmentAchievementStat.setServiceName(statDTO.getServiceName());
          departmentAchievementStat.setAchievementOrderType(statDTO.getAchievementOrderType());
        }
        departmentAchievementStat = departmentAchievementStat.add(statDTO);
        statDTOMap.put(statDTO.getServiceId(), departmentAchievementStat);
        departmentAchievementStatDTOMap.put(statDTO.getDepartmentId(), statDTOMap);
      }
    }

    for (Map<Long, AssistantAchievementStatDTO> map : washBeautyAchievementStatDTOMap.values()) {
      if (MapUtils.isEmpty(map)) {
        continue;
      }
      this.saveOrUpdateAssistantAchievementStat(map.values());
    }

    for (Map<Long, AssistantAchievementStatDTO> map : departmentAchievementStatDTOMap.values()) {
      if (MapUtils.isEmpty(map)) {
        continue;
      }
      this.saveOrUpdateAssistantAchievementStat(map.values());

    }
  }


  public void saveAchievementStat(Long shopId, int year, int month, Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap) {
    Map<Long, AssistantAchievementStatDTO> departmentAchievementStatDTOMap = new HashMap<Long, AssistantAchievementStatDTO>();

    for (AssistantAchievementStatDTO assistantAchievementStatDTO : assistantAchievementStatDTOMap.values()) {
      if (assistantAchievementStatDTO == null || assistantAchievementStatDTO.getDepartmentId() == null) {
        continue;
      }
      AssistantAchievementStatDTO departmentAchievementStat = null;

      departmentAchievementStat = departmentAchievementStatDTOMap.get(assistantAchievementStatDTO.getDepartmentId());
      if (departmentAchievementStat == null) {
        departmentAchievementStat = new AssistantAchievementStatDTO();
        departmentAchievementStat.setShopId(shopId);
        departmentAchievementStat.setStatYear(year);
        departmentAchievementStat.setStatMonth(month);
        departmentAchievementStat.setAchievementStatType(AchievementStatType.DEPARTMENT);
        departmentAchievementStat.setDepartmentId(assistantAchievementStatDTO.getDepartmentId());
        departmentAchievementStat.setStatTime(assistantAchievementStatDTO.getStatTime());
        departmentAchievementStat.setDepartmentName(assistantAchievementStatDTO.getDepartmentName());

      } else {
        departmentAchievementStat = departmentAchievementStatDTOMap.get(assistantAchievementStatDTO.getDepartmentId());
      }
      departmentAchievementStat = departmentAchievementStat.add(assistantAchievementStatDTO);
      departmentAchievementStatDTOMap.put(assistantAchievementStatDTO.getDepartmentId(), departmentAchievementStat);
    }

    this.saveOrUpdateAssistantAchievementStat(assistantAchievementStatDTOMap.values());
    this.saveOrUpdateAssistantAchievementStat(departmentAchievementStatDTOMap.values());

  }


  public void saveOrUpdateAssistantAchievementStat(Collection<AssistantAchievementStatDTO> assistantAchievementStatDTOList) {
    if (CollectionUtils.isEmpty(assistantAchievementStatDTOList)) {
      return;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (AssistantAchievementStatDTO assistantAchievementStatDTO : assistantAchievementStatDTOList) {
        AssistantAchievementStat assistantAchievementStat = null;

        if (assistantAchievementStatDTO.getId() != null) {
          assistantAchievementStat = writer.getById(AssistantAchievementStat.class, assistantAchievementStatDTO.getId());
        } else {
          assistantAchievementStat = new AssistantAchievementStat();
        }
        assistantAchievementStat.fromDTO(assistantAchievementStatDTO);
        writer.saveOrUpdate(assistantAchievementStat);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  public void deleteShopAchievementConfig(Long shopId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      txnWriter.deleteShopAchievementConfig(shopId);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  public void deleteAssistantAchievementStat(Long shopId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      txnWriter.deleteAssistantAchievementStat(shopId);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }

  }


  public void deleteAssistantServiceRecord(Long shopId,Long statTime) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      txnWriter.deleteAssistantServiceRecord(shopId,statTime);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  public void deleteAssistantBusinessAccountRecord(Long shopId, Long statTime) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      txnWriter.deleteAssistantBusinessAccountRecord(shopId, statTime);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  public void deleteAssistantProductRecord(Long shopId, Long statTime) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      txnWriter.deleteAssistantProductRecord(shopId,statTime);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  public void deleteAssistantMemberRecord(Long shopId, Long statTime) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      txnWriter.deleteAssistantMemberRecord(shopId,statTime);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  /**
   * 根据开始时间 结束时间 统计每个员工的购卡续卡业绩
   *
   * @param shopId
   * @param startTime
   * @param endTime
   */
  public void calculateMemberAssistant(Long shopId, long startTime, long endTime, int year, int month, Map<Long, SalesManDTO> salesManDTOMap,
                                       Map<Long, DepartmentDTO> departmentDTOMap, Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap,
                                       Long statTime) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);


      List<ShopAchievementConfig> shopAchievementConfigList = new ArrayList<ShopAchievementConfig>();
      List<AssistantMemberRecordDTO> assistantMemberRecordDTOList = new ArrayList<AssistantMemberRecordDTO>();


      List<MemberCardOrderDTO> memberCardOrderDTOList = txnService.countMemberAgentAchievements(shopId, startTime, endTime);
      if (CollectionUtils.isEmpty(memberCardOrderDTOList)) {
        return;
      }

      List<MemberAchievementHistory> memberAchievementHistoryList = this.getMemberAchievementHistory(shopId, null,null);

      for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
        if (memberCardOrderDTO == null) {
          continue;
        }
        List<MemberCardOrderItemDTO> memberCardOrderItemDTOList = txnService.getMemberCardOrderItemDTOByOrderId(memberCardOrderDTO.getShopId(), memberCardOrderDTO.getId());
        if (CollectionUtils.isEmpty(memberCardOrderItemDTOList)) {
          return;
        }
        MemberCardOrderItemDTO memberCardOrderItemDTO = memberCardOrderItemDTOList.get(0);
        if (memberCardOrderItemDTO.getSalesId() == null) {
          continue;
        }

        SalesManDTO salesManDTO = salesManDTOMap.get(memberCardOrderItemDTO.getSalesId());
        if (salesManDTO == null) {
          continue;
        }

        MemberAchievementHistoryDTO memberAchievementHistoryDTO = this.getMemberAchievementHistoryDTO(memberCardOrderDTO.getMemberOrderType(), memberCardOrderDTO.getVestDate(), memberAchievementHistoryList);
        if(memberAchievementHistoryDTO == null){
          memberAchievementHistoryDTO = new MemberAchievementHistoryDTO();
        }
        AssistantMemberRecordDTO assistantMemberRecordDTO = new AssistantMemberRecordDTO(memberCardOrderDTO, memberCardOrderItemDTO);

        double achievement = 0;
        if (memberAchievementHistoryDTO.getAchievementMemberType() == AchievementMemberType.CARD_AMOUNT) {
          achievement = memberAchievementHistoryDTO.getAchievementAmount();
          assistantMemberRecordDTO.setAchievementCalculateWay("提成配置:"+AchievementMemberType.CARD_AMOUNT.getName() + ",每张卡提成" + memberAchievementHistoryDTO.getAchievementAmount() + "元");
        } else if (memberAchievementHistoryDTO.getAchievementMemberType() == AchievementMemberType.CARD_TOTAL) {
          achievement = memberAchievementHistoryDTO.getAchievementAmount() * memberCardOrderItemDTO.getAmount() / PERCENT;
          assistantMemberRecordDTO.setAchievementCalculateWay("提成配置:" + AchievementMemberType.CARD_TOTAL.getName() + ",销售额" + memberCardOrderItemDTO.getAmount() + "元,提成比率" + memberAchievementHistoryDTO.getAchievementAmount() + "%" + ",提成" + achievement + "元");
        }


        if (memberCardOrderItemDTO.getCardId() != null) {

          MemberCard memberCard = membersService.getMemberCardById(shopId, memberCardOrderItemDTO.getCardId());
          if (memberCard != null) {
            assistantMemberRecordDTO.setMemberCardName(memberCard.getName());
            assistantMemberRecordDTO.setMemberCardTotal(NumberUtil.doubleVal(memberCard.getPrice()));
            if (TxnConstant.VALUE_CARD.equals(memberCard.getType())) {
              assistantMemberRecordDTO.setMemberCardType(MemberCardType.STORED_CARD);
            } else if (TxnConstant.TIME_CARD.equals(memberCard.getType())) {
              assistantMemberRecordDTO.setMemberCardType(MemberCardType.TIMES_CARD);
            }
          }
        }
        MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, memberCardOrderDTO.getCustomerId());
        if (memberDTO != null) {
          assistantMemberRecordDTO.setMemberNo(memberDTO.getMemberNo());
        }

        ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, memberCardOrderDTO.getId());
        if (receivableDTO != null) {
          assistantMemberRecordDTO.setTotal(NumberUtil.doubleVal(receivableDTO.getSettledAmount()));
        }

        assistantMemberRecordDTO.setAssistantId(memberCardOrderItemDTO.getSalesId());
        assistantMemberRecordDTO.setAchievement(achievement);
        assistantMemberRecordDTO.setMemberAchievementHistoryId(memberAchievementHistoryDTO == null ? null : memberAchievementHistoryDTO.getId());

        assistantMemberRecordDTO.setAssistantName(salesManDTO.getName());


        Long departmentId = null;

        AssistantAchievementHistoryDTO assistantAchievementHistoryDTO = this.geAssistantAchievementHistoryByVestDate(shopId, salesManDTO.getId(), memberCardOrderDTO.getVestDate());
        if (assistantAchievementHistoryDTO != null) {
          departmentId = assistantAchievementHistoryDTO.getDepartmentId();
        }

        if (departmentId == null) {
          departmentId = salesManDTO.getDepartmentId();
        }

        if (departmentId != null) {
          DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
          if (departmentDTO == null) {
            LOG.error("calculateMemberAssistant.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + salesManDTO.getId() + ",departmentId:" + salesManDTO.getDepartmentId());
          } else {
            assistantMemberRecordDTO.setDepartmentId(departmentDTO.getId());
            assistantMemberRecordDTO.setDepartmentName(departmentDTO.getName());
          }
        }

        AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesManDTO.getId());
        if (assistantAchievementStatDTO == null) {
          assistantAchievementStatDTO = new AssistantAchievementStatDTO();
          assistantAchievementStatDTO.setShopId(shopId);
          assistantAchievementStatDTO.setStatYear(year);
          assistantAchievementStatDTO.setStatMonth(month);
          assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
          assistantAchievementStatDTO.setAssistantId(salesManDTO.getId());
          assistantAchievementStatDTO.setDepartmentId(departmentId);
          assistantAchievementStatDTO.setStatTime(endTime - 10000);
          assistantAchievementStatDTO.setAssistantName(salesManDTO.getName());
          assistantAchievementStatDTO.setDepartmentName(assistantMemberRecordDTO.getDepartmentName());
        }


        if (memberCardOrderDTO.getMemberOrderType() == MemberOrderType.NEW) {
          if (assistantAchievementHistoryDTO != null) {
            double memberNewAchievement = NumberUtil.doubleVal(assistantAchievementHistoryDTO.getMemberNewAchievement());

            if (assistantAchievementHistoryDTO.getMemberNewType() == AchievementMemberType.CARD_AMOUNT) {
              assistantAchievementStatDTO.setMemberAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberAchievementByAssistant()) + memberNewAchievement);
              assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + memberNewAchievement);
              assistantMemberRecordDTO.setAchievementByAssistant(memberNewAchievement);
              assistantMemberRecordDTO.setAchievementByAssistantCalculateWay("购卡员工提成配置:"+AchievementMemberType.CARD_AMOUNT.getName() + ",每张卡提成" + memberNewAchievement + "元");
            }
            if (assistantAchievementHistoryDTO.getMemberNewType() == AchievementMemberType.CARD_TOTAL) {

              double memberAchievementByAssistant = NumberUtil.toReserve(memberCardOrderItemDTO.getAmount() * memberNewAchievement / PERCENT,NumberUtil.MONEY_PRECISION);
              assistantAchievementStatDTO.setMemberAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberAchievementByAssistant()) + memberAchievementByAssistant);
              assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + memberAchievementByAssistant);

              assistantMemberRecordDTO.setAchievementByAssistant(memberAchievementByAssistant);
              assistantMemberRecordDTO.setAchievementByAssistantCalculateWay("购卡员工提成配置:"+AchievementMemberType.CARD_TOTAL.getName() + ",购卡销售额" + memberCardOrderItemDTO.getAmount() + "元,提成比率" + assistantAchievementHistoryDTO.getMemberNewAchievement() + "%" + ",提成" + memberAchievementByAssistant + "元");
            }
          }

          assistantAchievementStatDTO.setMember(NumberUtil.doubleVal(assistantAchievementStatDTO.getMember()) + memberCardOrderDTO.getTotal());
          assistantAchievementStatDTO.setMemberTimes(NumberUtil.longValue(assistantAchievementStatDTO.getMemberTimes()) + 1);
          assistantAchievementStatDTO.setMemberAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberAchievement()) + achievement);

        } else if (memberCardOrderDTO.getMemberOrderType() == MemberOrderType.RENEW) {
          if (assistantAchievementHistoryDTO != null) {
            double memberReNewAchievement = NumberUtil.doubleVal(assistantAchievementHistoryDTO.getMemberReNewAchievement());
            if (assistantAchievementHistoryDTO.getMemberRenewType() == AchievementMemberType.CARD_AMOUNT) {
              assistantAchievementStatDTO.setMemberRenewAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberRenewAchievementByAssistant()) + memberReNewAchievement);
              assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + memberReNewAchievement);
              assistantMemberRecordDTO.setAchievementByAssistant(memberReNewAchievement);
              assistantMemberRecordDTO.setAchievementByAssistantCalculateWay("续卡员工提成配置:" + AchievementMemberType.CARD_AMOUNT.getName() + ",每张卡提成" + memberReNewAchievement + "元");

            }
            if (assistantAchievementHistoryDTO.getMemberRenewType() == AchievementMemberType.CARD_TOTAL) {

              double memberAchievementByAssistant = NumberUtil.toReserve(memberCardOrderItemDTO.getAmount() * memberReNewAchievement / PERCENT,NumberUtil.MONEY_PRECISION);

              assistantAchievementStatDTO.setMemberRenewAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberRenewAchievementByAssistant()) + memberAchievementByAssistant);
              assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + memberAchievementByAssistant);
              assistantMemberRecordDTO.setAchievementByAssistant(memberAchievementByAssistant);
              assistantMemberRecordDTO.setAchievementByAssistantCalculateWay("续卡员工提成配置:" + AchievementMemberType.CARD_TOTAL.getName() + ",续卡销售额" + memberCardOrderItemDTO.getAmount() + "元,提成比率" + memberReNewAchievement + "%" + ",提成" + memberAchievementByAssistant + "元");
            }
          }

          assistantAchievementStatDTO.setMemberRenew(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberRenew()) + memberCardOrderDTO.getTotal());
          assistantAchievementStatDTO.setMemberRenewTimes(NumberUtil.longValue(assistantAchievementStatDTO.getMemberRenewTimes()) + 1);
          assistantAchievementStatDTO.setMemberRenewAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberRenewAchievement()) + achievement);

        }
        assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) + memberCardOrderDTO.getTotal());
        assistantAchievementStatDTO.setAchievementSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()) + achievement);
        assistantAchievementStatDTOMap.put(salesManDTO.getId(), assistantAchievementStatDTO);
        assistantMemberRecordDTOList.add(assistantMemberRecordDTO);
      }

      this.saveOrUpdateAssistantMember(shopId, assistantMemberRecordDTOList,statTime);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  public Map<Long, SalesManDTO> getSalesManMap(Long shopId) {
    IUserService userService = ServiceManager.getService(IUserService.class);

    List<SalesManDTO> salesManDTOList = userService.getSalesManDTOListByShopId(shopId, null, null);

    Map<Long, SalesManDTO> salesManDTOMap = new HashMap<Long, SalesManDTO>();
    if (CollectionUtils.isEmpty(salesManDTOList)) {
      return salesManDTOMap;
    }
    for (SalesManDTO salesManDTO : salesManDTOList) {
      salesManDTOMap.put(salesManDTO.getId(), salesManDTO);
    }
    return salesManDTOMap;
  }

  public Map<Long, DepartmentDTO> getDepartmentMap(Long shopId) {
    IUserService userService = ServiceManager.getService(IUserService.class);

    List<DepartmentDTO> departmentDTOList = userService.getAllDepartmentsByShopId(shopId);

    Map<Long, DepartmentDTO> departmentDTOMap = new HashMap<Long, DepartmentDTO>();
    if (CollectionUtils.isEmpty(departmentDTOList)) {
      return departmentDTOMap;
    }
    for (DepartmentDTO departmentDTO : departmentDTOList) {
      departmentDTOMap.put(departmentDTO.getId(), departmentDTO);
    }
    return departmentDTOMap;
  }


  public Map<Long, Long> getWashBeautyShopIdList(Long statShopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    Set<Long> shopIdSet = null;
    if (statShopId != null) {
      shopIdSet = new HashSet<Long>();
      shopIdSet.add(statShopId);
    }

    List<Long> shopIdList = writer.getWashBeautyShopIdList(shopIdSet);

    Map<Long, Long> washBeautyShopIdMap = new HashMap<Long, Long>();

    if (CollectionUtil.isEmpty(shopIdList)) {
      return washBeautyShopIdMap;
    }
    for (Long shopId : shopIdList) {
      washBeautyShopIdMap.put(shopId, shopId);
    }
    return washBeautyShopIdMap;
  }
  public Map<Long, Long> getMemberCardOrderShopIdList(Long statShopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    Set<Long> shopIdSet = null;
    if (statShopId != null) {
      shopIdSet = new HashSet<Long>();
      shopIdSet.add(statShopId);
    }

    List<Long> shopIdList = writer.getMemberCardOrderShopIdList(shopIdSet);

    Map<Long, Long> memberCardOrderShopId = new HashMap<Long, Long>();

    if (CollectionUtil.isEmpty(shopIdList)) {
      return memberCardOrderShopId;
    }
    for (Long shopId : shopIdList) {
      memberCardOrderShopId.put(shopId, shopId);
    }
    return memberCardOrderShopId;
  }

  public Map<Long, Long> getMemberCardReturnShopIdList(Long statShopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    Set<Long> shopIdSet = null;
    if (statShopId != null) {
      shopIdSet = new HashSet<Long>();
      shopIdSet.add(statShopId);
    }

    List<Long> shopIdList = writer.getMemberCardReturnShopIdList(shopIdSet);

    Map<Long, Long> memberCardReturnShopId = new HashMap<Long, Long>();

    if (CollectionUtil.isEmpty(shopIdList)) {
      return memberCardReturnShopId;
    }
    for (Long shopId : shopIdList) {
      memberCardReturnShopId.put(shopId, shopId);
    }
    return memberCardReturnShopId;
  }

  public Map<Long, Long> getSalesOrderShopIdList(Long statShopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    Set<Long> shopIdSet = null;
    if (statShopId != null) {
      shopIdSet = new HashSet<Long>();
      shopIdSet.add(statShopId);
    }

    List<Long> shopIdList = writer.getSalesOrderShopIdList(shopIdSet);

    Map<Long, Long> salesShopId = new HashMap<Long, Long>();

    if (CollectionUtil.isEmpty(shopIdList)) {
      return salesShopId;
    }
    for (Long shopId : shopIdList) {
      salesShopId.put(shopId, shopId);
    }
    return salesShopId;
  }

  public Map<Long, Long> getSalesReturnShopIdList(Long statShopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    Set<Long> shopIdSet = null;
    if (statShopId != null) {
      shopIdSet = new HashSet<Long>();
      shopIdSet.add(statShopId);
    }

    List<Long> shopIdList = writer.getSalesReturnShopIdList(shopIdSet);

    Map<Long, Long> salesReturnShopId = new HashMap<Long, Long>();

    if (CollectionUtil.isEmpty(shopIdList)) {
      return salesReturnShopId;
    }
    for (Long shopId : shopIdList) {
      salesReturnShopId.put(shopId, shopId);
    }
    return salesReturnShopId;
  }

  /**
   * 根据开始时间、结束时间、shop_id统计员工的洗车美容业绩 并放入map和asssistantList中
   *
   * @param shopId
   * @param startTime
   * @param endTime
   */
  public void calculateWashBeautyAssistant(Long shopId, long startTime, long endTime, int year, int month,
                                           Map<Long, SalesManDTO> salesManDTOMap, Map<Long, DepartmentDTO> departmentDTOMap,
                                           Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap,
                                           Map<Long,Map<Long,AssistantAchievementStatDTO>> washBeautyAchievementStatDTOMap,Long statTime) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);

      List<WashBeautyOrderDTO> washBeautyOrderDTOList = txnService.countWashBeautyAgentAchievements(shopId, startTime, endTime);//维修美容单记录
      if (CollectionUtils.isEmpty(washBeautyOrderDTOList)) {
        return;
      }

      List<AssistantServiceRecordDTO> assistantServiceRecordDTOList = new ArrayList<AssistantServiceRecordDTO>();

      List<Long> orderIds = new ArrayList<Long>();
      for (WashBeautyOrderDTO washBeautyOrderDTO : washBeautyOrderDTOList) {
        if (washBeautyOrderDTO.getStatus() == OrderStatus.WASH_REPEAL) {
          continue;
        }
        orderIds.add(washBeautyOrderDTO.getId());
      }
      if (CollectionUtils.isEmpty(orderIds)) {
        return;
      }

      Map<Long, Map<Long, WashBeautyOrderItemDTO>> itemMap = txnService.getWashBeautyOrderItemByShopIdAndArrayOrderId(shopId, orderIds.toArray(new Long[orderIds.size()]));
      if (MapUtils.isEmpty(itemMap)) {
        return;
      }

      Set<Long> serviceIdSet = new HashSet<Long>();
      for(Map map : itemMap.values()){
        for(Object object :map.values()){
          WashBeautyOrderItemDTO itemDTO = (WashBeautyOrderItemDTO)object;
          if(itemDTO.getServiceId() == null){
            continue;
          }
          serviceIdSet.add(itemDTO.getServiceId());
        }
      }
      Map<Long,ServiceDTO> serviceDTOMap = txnService.getServiceByServiceIdSet(shopId,serviceIdSet);


      double achievement = 0; //每个洗车美容服务项目的统计金额
      for (WashBeautyOrderDTO washBeautyOrderDTO : washBeautyOrderDTOList) {
        if (washBeautyOrderDTO.getStatus() == OrderStatus.WASH_REPEAL) {
          continue;
        }

        Map<Long, WashBeautyOrderItemDTO> washBeautyOrderItemDTOMap = itemMap.get(washBeautyOrderDTO.getId());
        if (MapUtils.isEmpty(washBeautyOrderItemDTOMap)) {
          continue;
        }
        for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderItemDTOMap.values()) {
          if (washBeautyOrderItemDTO == null || StringUtils.isEmpty(washBeautyOrderItemDTO.getSalesManIds())) {
            continue;
          }
          ServiceDTO serviceDTO = serviceDTOMap.get(washBeautyOrderItemDTO.getServiceId());
          if (serviceDTO == null) {
            LOG.error("service is null : " + washBeautyOrderItemDTO.getServiceId());
            continue;
          }
          ServiceAchievementHistoryDTO serviceAchievementHistoryDTO = this.getServiceAchievementHistoryDTO(shopId, washBeautyOrderItemDTO.getServiceId(), washBeautyOrderDTO.getVestDate());
          if (serviceAchievementHistoryDTO == null) {
            serviceAchievementHistoryDTO = new ServiceAchievementHistoryDTO();
            serviceAchievementHistoryDTO.setAchievementAmount(0D);
            serviceAchievementHistoryDTO.setStandardHours(0D);
            serviceAchievementHistoryDTO.setStandardUnitPrice(0D);
          }
          String[] array = washBeautyOrderItemDTO.getSalesManIds().split(",");
          if (ArrayUtil.isEmpty(array)) {
            continue;
          }


          double totalAchievement = 0;
          if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.AMOUNT) {
            totalAchievement = NumberUtil.doubleVal(serviceAchievementHistoryDTO.getAchievementAmount());
          } else if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.RATIO) {
            totalAchievement = NumberUtil.doubleVal(serviceAchievementHistoryDTO.getAchievementAmount()) * NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice()) / PERCENT;
          }

          achievement = NumberUtil.toReserve(totalAchievement / array.length, NumberUtil.PRECISION);
          for (String salesManIdStr : array) {
            AssistantServiceRecordDTO assistantServiceRecordDTO = new AssistantServiceRecordDTO(washBeautyOrderDTO, washBeautyOrderItemDTO);
            assistantServiceRecordDTO.setServiceAchievementHistoryId(serviceAchievementHistoryDTO.getId());
            assistantServiceRecordDTO.setServiceName(serviceDTO.getName());
            assistantServiceRecordDTO.setAssistantId(Long.valueOf(salesManIdStr));
            assistantServiceRecordDTO.setAchievement(achievement);
            assistantServiceRecordDTO.setActualService(NumberUtil.toReserve(NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice()) / array.length, NumberUtil.MONEY_PRECISION));

            if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.AMOUNT) {
              StringBuffer stringBuffer = new StringBuffer();
              stringBuffer.append("该服务计算方式:").append(serviceAchievementHistoryDTO.getAchievementType().getName()).append(",每次").append( serviceAchievementHistoryDTO.getAchievementAmount() +"元");
              stringBuffer.append(",该项目总提成").append(serviceAchievementHistoryDTO.getAchievementAmount()).append(",共有").append(array.length).append("人施工").append(",每人平均获得提成").append(achievement).append("元");
              assistantServiceRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
            } else if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.RATIO) {
              assistantServiceRecordDTO.setAchievementCalculateWay("计算方式:" + serviceAchievementHistoryDTO.getAchievementType().getName() + ",比率是" + serviceAchievementHistoryDTO.getAchievementAmount() + "%");
              StringBuffer stringBuffer = new StringBuffer();
              stringBuffer.append("该服务计算方式:").append(serviceAchievementHistoryDTO.getAchievementType().getName()).append(",比率是").append(serviceAchievementHistoryDTO.getAchievementAmount() + "%");
              stringBuffer.append(",该项目总提成").append(NumberUtil.doubleVal(serviceAchievementHistoryDTO.getAchievementAmount()) * NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice()) / PERCENT).append(",共有").append(array.length).append("人施工").append(",每人平均获得提成").append(achievement);
              assistantServiceRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
            }


            SalesManDTO salesManDTO = salesManDTOMap.get(Long.valueOf(salesManIdStr));
            if (salesManDTO == null) {
              continue;
            }
            assistantServiceRecordDTO.setAssistantName(salesManDTO.getName());

            Long departmentId = null;

            AssistantAchievementHistoryDTO  assistantAchievementHistoryDTO = this.geAssistantAchievementHistoryByVestDate(shopId,salesManDTO.getId(),washBeautyOrderDTO.getVestDate());
            if(assistantAchievementHistoryDTO != null) {
              departmentId = assistantAchievementHistoryDTO.getDepartmentId();
            }

            if(departmentId == null){
              departmentId = salesManDTO.getDepartmentId();
            }

            if (departmentId != null) {
              DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
              if (departmentDTO == null) {
                LOG.error("calculateWashBeautyAssistant.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + salesManIdStr + ",departmentId:" + departmentId);
              } else {
                assistantServiceRecordDTO.setDepartmentId(departmentDTO.getId());
                assistantServiceRecordDTO.setDepartmentName(departmentDTO.getName());
              }
            }

            AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesManDTO.getId());
            if (assistantAchievementStatDTO == null) {
              assistantAchievementStatDTO = new AssistantAchievementStatDTO();
              assistantAchievementStatDTO.setShopId(shopId);
              assistantAchievementStatDTO.setStatYear(year);
              assistantAchievementStatDTO.setStatMonth(month);
              assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
              assistantAchievementStatDTO.setAssistantId(salesManDTO.getId());
              assistantAchievementStatDTO.setDepartmentId(departmentId);
              assistantAchievementStatDTO.setStatTime(endTime - 10000);
              assistantAchievementStatDTO.setAssistantName(salesManDTO.getName());
              assistantAchievementStatDTO.setDepartmentName(assistantServiceRecordDTO.getDepartmentName());
            }

            if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getWashBeautyAchievement() != null) {
              assistantAchievementStatDTO.setWashAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getWashAchievementByAssistant()) + assistantAchievementHistoryDTO.getWashBeautyAchievement());
              assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + assistantAchievementHistoryDTO.getWashBeautyAchievement());
              assistantServiceRecordDTO.setAchievementByAssistant(assistantAchievementHistoryDTO.getWashBeautyAchievement());

              StringBuffer stringBuffer = new StringBuffer();
              stringBuffer.append("该员工配置的提成是洗车每次:").append(assistantAchievementHistoryDTO.getWashBeautyAchievement()).append("元,").append("获得提成").append(assistantAchievementHistoryDTO.getWashBeautyAchievement()).append("元");
              assistantServiceRecordDTO.setAchievementByAssistantCalculateWay(stringBuffer.toString());
            }

            assistantAchievementStatDTO.setWash(NumberUtil.doubleVal(assistantAchievementStatDTO.getWash()) + NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice()) / array.length);
            assistantAchievementStatDTO.setWashTimes(NumberUtil.longValue(assistantAchievementStatDTO.getWashTimes()) + 1);
            assistantAchievementStatDTO.setWashAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getWashAchievement()) + achievement);

            assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) + NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice()) / array.length);
            assistantAchievementStatDTO.setAchievementSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()) + achievement);
            assistantAchievementStatDTOMap.put(salesManDTO.getId(), assistantAchievementStatDTO);

            Map<Long,AssistantAchievementStatDTO> salesManAchievementMap = washBeautyAchievementStatDTOMap.get(salesManDTO.getId());

            if(salesManAchievementMap == null){
              salesManAchievementMap =  new HashMap<Long, AssistantAchievementStatDTO>();
            }
            AssistantAchievementStatDTO statDTO = salesManAchievementMap.get(serviceDTO.getId());


            if (statDTO == null  ) {
              statDTO = new AssistantAchievementStatDTO();
              statDTO.setShopId(shopId);
              statDTO.setStatYear(year);
              statDTO.setStatMonth(month);
              statDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
              statDTO.setAssistantId(salesManDTO.getId());
              statDTO.setDepartmentId(departmentId);
              statDTO.setStatTime(endTime - 10000);
              statDTO.setAssistantName(salesManDTO.getName());
              statDTO.setDepartmentName(assistantServiceRecordDTO.getDepartmentName());
              statDTO.setAchievementOrderType(AchievementOrderType.WASH_BEAUTY);
              statDTO.setServiceId(serviceDTO.getId());
              if (StringUtil.isEmpty(washBeautyOrderItemDTO.getServiceName())) {
                statDTO.setServiceName(serviceDTO.getName());
              } else {
                statDTO.setServiceName(washBeautyOrderItemDTO.getServiceName());
              }
            }
            if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getWashBeautyAchievement() != null) {
              statDTO.setWashAchievementByAssistant(NumberUtil.doubleVal(statDTO.getWashAchievementByAssistant()) + assistantAchievementHistoryDTO.getWashBeautyAchievement());
              statDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(statDTO.getAchievementSumByAssistant()) + assistantAchievementHistoryDTO.getWashBeautyAchievement());
            }
            statDTO.setWash(NumberUtil.doubleVal(statDTO.getWash()) + washBeautyOrderItemDTO.getPrice());
            statDTO.setWashTimes(NumberUtil.longValue(statDTO.getWashTimes()) + 1);
            statDTO.setWashAchievement(NumberUtil.doubleVal(statDTO.getWashAchievement()) + achievement);

            statDTO.setStatSum(NumberUtil.doubleVal(statDTO.getStatSum()) + washBeautyOrderItemDTO.getPrice());
            statDTO.setAchievementSum(NumberUtil.doubleVal(statDTO.getAchievementSum()) + achievement);
            salesManAchievementMap.put(serviceDTO.getId(), statDTO);
            washBeautyAchievementStatDTOMap.put(salesManDTO.getId(), salesManAchievementMap);

            assistantServiceRecordDTOList.add(assistantServiceRecordDTO);
          }
        }
      }
      this.saveOrUpdateAssistantService(shopId, assistantServiceRecordDTOList,statTime);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }

  /**
   * 根据开始时间、结束时间、shop_id统计员工的洗车美容业绩 并放入map和asssistantList中
   *
   * @param shopId
   * @param startTime
   * @param endTime
   */
  public void calculateRepairServiceAssistant(Long shopId, long startTime, long endTime, int year, int month,
                                              Map<Long, SalesManDTO> salesManDTOMap, Map<Long, DepartmentDTO> departmentDTOMap,
                                              Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap,
                                              Map<Long,Map<Long,AssistantAchievementStatDTO>> washBeautyAchievementStatDTOMap,
                                              Long statTime) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);


      List<RepairOrder> repairOrderList = txnService.countAgentAchievements(shopId, startTime, endTime);//维修美容单记录
      if (CollectionUtils.isEmpty(repairOrderList)) {
        return;
      }

      List<AssistantServiceRecordDTO> assistantServiceRecordDTOList = new ArrayList<AssistantServiceRecordDTO>();

      List<Long> orderIdList = new ArrayList<Long>();

      Map<Long, RepairOrder> repairOrderMap = new HashMap<Long, RepairOrder>();

      for (RepairOrder repairOrder : repairOrderList) {
        if(repairOrder.getStatusEnum() != OrderStatus.REPAIR_SETTLED){
          continue;
        }

        orderIdList.add(repairOrder.getId());
        repairOrderMap.put(repairOrder.getId(), repairOrder);
      }

      Map<Long, List<RepairOrderServiceDTO>> repairOrderServiceDTOListMap = txnService.getRepairOrderServiceDTOByShopIdAndArrayOrderId(shopId, orderIdList.toArray(new Long[orderIdList.size()]));
      if (MapUtils.isEmpty(repairOrderServiceDTOListMap) || CollectionUtils.isEmpty(repairOrderServiceDTOListMap.values())) {
        return;
      }


      for (RepairOrder repairOrder : repairOrderList) {

        RepairOrderDTO repairOrderDTO = repairOrder.toDTO();

        double achievement = 0; //每个洗车美容服务项目的统计金额
        if (repairOrder.getStatusEnum() != OrderStatus.REPAIR_SETTLED) {
          continue;
        }

        List<RepairOrderServiceDTO> repairOrderServiceDTOList = repairOrderServiceDTOListMap.get(repairOrder.getId());
        if (CollectionUtils.isEmpty(repairOrderServiceDTOList)) {
          continue;
        }
        for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOList) {
          if (repairOrderServiceDTO == null || StringUtils.isEmpty(repairOrderServiceDTO.getWorkerIds())) {
            continue;
          }
          ServiceDTO serviceDTO = txnService.getServiceById(repairOrderServiceDTO.getServiceId());
          if (serviceDTO == null) {
            LOG.error("service is null : " + repairOrderServiceDTO.getServiceId());
            continue;
          }

          ServiceAchievementHistoryDTO serviceAchievementHistoryDTO = this.getServiceAchievementHistoryDTO(shopId, repairOrderServiceDTO.getServiceId(), repairOrder.getVestDate());
          if (serviceAchievementHistoryDTO == null) {
            serviceAchievementHistoryDTO = new ServiceAchievementHistoryDTO();
            serviceAchievementHistoryDTO.setAchievementAmount(0D);
            serviceAchievementHistoryDTO.setStandardHours(0D);
            serviceAchievementHistoryDTO.setStandardUnitPrice(0D);
          }

          double totalAchievement = 0;
          if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.AMOUNT) {
            totalAchievement = NumberUtil.doubleVal(serviceAchievementHistoryDTO.getAchievementAmount());
          } else if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.RATIO) {
            totalAchievement = NumberUtil.doubleVal(serviceAchievementHistoryDTO.getAchievementAmount()) * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / PERCENT;
          }


          String[] array = repairOrderServiceDTO.getWorkerIds().split(",");
          if (ArrayUtil.isEmpty(array)) {
            continue;
          }
          achievement = NumberUtil.toReserve(NumberUtil.doubleVal(totalAchievement) / array.length, NumberUtil.PRECISION);
          double standardHoursTotal = 0;
          if (repairOrderServiceDTO.getStandardHours() != null) {
            standardHoursTotal = repairOrderServiceDTO.getStandardHours();
          } else {
            standardHoursTotal = NumberUtil.doubleVal(serviceAchievementHistoryDTO.getStandardHours());
          }

          double standardHours = NumberUtil.toReserve(standardHoursTotal / array.length, NumberUtil.PRECISION);
          double actualHours = NumberUtil.toReserve(NumberUtil.doubleVal(repairOrderServiceDTO.getActualHours()) / array.length, NumberUtil.PRECISION);
          double actualService = NumberUtil.toReserve(NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / array.length, NumberUtil.PRECISION);
          for (String salesManIdStr : array) {
            AssistantServiceRecordDTO assistantServiceRecordDTO = new AssistantServiceRecordDTO(repairOrderDTO, repairOrderServiceDTO);

            assistantServiceRecordDTO.setServiceAchievementHistoryId(serviceAchievementHistoryDTO.getId());
            assistantServiceRecordDTO.setServiceId(repairOrderServiceDTO.getServiceId());
            assistantServiceRecordDTO.setServiceName(StringUtils.isNotEmpty(repairOrderServiceDTO.getService()) ? repairOrderServiceDTO.getService() : serviceDTO.getName());
            assistantServiceRecordDTO.setAssistantId(Long.valueOf(salesManIdStr));
            assistantServiceRecordDTO.setAchievement(achievement);
            assistantServiceRecordDTO.setStandardHours(standardHours);
            assistantServiceRecordDTO.setStandardService(NumberUtil.doubleVal(serviceAchievementHistoryDTO.getStandardUnitPrice()));
            assistantServiceRecordDTO.setActualHours(actualHours);
            assistantServiceRecordDTO.setActualService(actualService);


            if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.AMOUNT) {
              StringBuffer stringBuffer = new StringBuffer();
              stringBuffer.append("该服务计算方式:").append(serviceAchievementHistoryDTO.getAchievementType().getName()).append(",每次").append(serviceAchievementHistoryDTO.getAchievementAmount() + "元");
              stringBuffer.append(",该项目总提成").append(serviceAchievementHistoryDTO.getAchievementAmount()).append(",共有").append(array.length).append("人施工").append(",每人平均获得提成").append(achievement).append("元");
              assistantServiceRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
            } else if (serviceAchievementHistoryDTO.getAchievementType() == AchievementType.RATIO) {
              StringBuffer stringBuffer = new StringBuffer();
              stringBuffer.append("该服务计算方式:").append(serviceAchievementHistoryDTO.getAchievementType().getName()).append(",比率是").append(serviceAchievementHistoryDTO.getAchievementAmount() + "%");
              stringBuffer.append(",该项目总提成").append(NumberUtil.doubleVal(serviceAchievementHistoryDTO.getAchievementAmount()) * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / PERCENT).append(",共有").append(array.length).append("人施工").append(",每人平均获得提成").append(achievement).append("元");
              assistantServiceRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
            }

            SalesManDTO salesManDTO = salesManDTOMap.get(Long.valueOf(salesManIdStr));
            if (salesManDTO == null) {
              continue;
            }
            assistantServiceRecordDTO.setAssistantName(salesManDTO.getName());

            Long departmentId = null;

            AssistantAchievementHistoryDTO assistantAchievementHistoryDTO = this.geAssistantAchievementHistoryByVestDate(shopId, salesManDTO.getId(), repairOrderDTO.getVestDate());
            if (assistantAchievementHistoryDTO != null) {
              departmentId = assistantAchievementHistoryDTO.getDepartmentId();
            }

            if (departmentId == null) {
              departmentId = salesManDTO.getDepartmentId();
            }

            if (departmentId != null) {
              DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
              if (departmentDTO == null) {
                LOG.error("calculateWashBeautyAssistant.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + salesManIdStr + ",departmentId:" + departmentId);
              } else {
                assistantServiceRecordDTO.setDepartmentId(departmentDTO.getId());
                assistantServiceRecordDTO.setDepartmentName(departmentDTO.getName());
              }
            }


            AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesManDTO.getId());
            if (assistantAchievementStatDTO == null) {
              assistantAchievementStatDTO = new AssistantAchievementStatDTO();
              assistantAchievementStatDTO.setShopId(shopId);
              assistantAchievementStatDTO.setStatYear(year);
              assistantAchievementStatDTO.setStatMonth(month);
              assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
              assistantAchievementStatDTO.setAssistantId(salesManDTO.getId());
              assistantAchievementStatDTO.setDepartmentId(departmentId);
              assistantAchievementStatDTO.setStatTime(endTime - 10000);
              assistantAchievementStatDTO.setAssistantName(salesManDTO.getName());
              assistantAchievementStatDTO.setDepartmentName(assistantServiceRecordDTO.getDepartmentName());
            }

            if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getServiceAchievement() != null) {
              assistantAchievementStatDTO.setServiceAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getServiceAchievementByAssistant()) + assistantAchievementHistoryDTO.getServiceAchievement() * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / (PERCENT * array.length));
              assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + assistantAchievementHistoryDTO.getServiceAchievement() * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / (PERCENT * array.length));

              assistantServiceRecordDTO.setAchievementByAssistant(assistantAchievementHistoryDTO.getServiceAchievement() * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / (PERCENT * array.length));

              StringBuffer stringBuffer = new StringBuffer();
              stringBuffer.append("该员工的施工提成比率:").append(assistantAchievementHistoryDTO.getServiceAchievement());
              stringBuffer.append("%,该项目总金额").append(repairOrderServiceDTO.getTotal()).append(",获得提成").append(assistantAchievementHistoryDTO.getServiceAchievement() * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / PERCENT).append("元");
              assistantServiceRecordDTO.setAchievementByAssistantCalculateWay(stringBuffer.toString());
            }
            assistantAchievementStatDTO.setStandardHours(NumberUtil.doubleVal(assistantAchievementStatDTO.getStandardHours()) + assistantServiceRecordDTO.getStandardHours());
            assistantAchievementStatDTO.setStandardService(NumberUtil.doubleVal(assistantAchievementStatDTO.getStandardService()) + assistantServiceRecordDTO.getStandardService() * assistantServiceRecordDTO.getStandardHours());
            assistantAchievementStatDTO.setActualHours(NumberUtil.doubleVal(assistantAchievementStatDTO.getActualHours()) + assistantServiceRecordDTO.getActualHours());
            assistantAchievementStatDTO.setActualService(NumberUtil.doubleVal(assistantAchievementStatDTO.getActualService()) + assistantServiceRecordDTO.getActualService());
            assistantAchievementStatDTO.setServiceAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getServiceAchievement()) + achievement);

            assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) + assistantServiceRecordDTO.getActualService());
            assistantAchievementStatDTO.setAchievementSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()) + achievement);
            assistantAchievementStatDTOMap.put(salesManDTO.getId(), assistantAchievementStatDTO);


            Map<Long, AssistantAchievementStatDTO> salesManAchievementMap = washBeautyAchievementStatDTOMap.get(salesManDTO.getId());

            if (salesManAchievementMap == null) {
              salesManAchievementMap = new HashMap<Long, AssistantAchievementStatDTO>();
            }
            AssistantAchievementStatDTO statDTO = salesManAchievementMap.get(serviceDTO.getId());


            if (statDTO == null) {
              statDTO = new AssistantAchievementStatDTO();
              statDTO.setShopId(shopId);
              statDTO.setStatYear(year);
              statDTO.setStatMonth(month);
              statDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
              statDTO.setAssistantId(salesManDTO.getId());
              statDTO.setDepartmentId(departmentId);
              statDTO.setStatTime(endTime - 10000);
              statDTO.setAssistantName(salesManDTO.getName());
              statDTO.setDepartmentName(assistantServiceRecordDTO.getDepartmentName());
              statDTO.setAchievementOrderType(AchievementOrderType.REPAIR_SERVICE);
              statDTO.setServiceId(serviceDTO.getId());
              if (StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
                statDTO.setServiceName(serviceDTO.getName());
              } else {
                statDTO.setServiceName(repairOrderServiceDTO.getService());
              }
            }
            if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getServiceAchievement() != null) {
              statDTO.setServiceAchievementByAssistant(NumberUtil.doubleVal(statDTO.getServiceAchievementByAssistant()) + assistantAchievementHistoryDTO.getServiceAchievement() * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / (PERCENT * array.length));
              statDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(statDTO.getAchievementSumByAssistant()) + assistantAchievementHistoryDTO.getServiceAchievement() * NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / (PERCENT * array.length));
            }

            statDTO.setStandardHours(NumberUtil.doubleVal(statDTO.getStandardHours()) + assistantServiceRecordDTO.getStandardHours());
            statDTO.setStandardService(NumberUtil.doubleVal(statDTO.getStandardService()) + assistantServiceRecordDTO.getStandardService() * assistantServiceRecordDTO.getStandardHours());
            statDTO.setActualHours(NumberUtil.doubleVal(statDTO.getActualHours()) + assistantServiceRecordDTO.getActualHours());
            statDTO.setActualService(NumberUtil.doubleVal(statDTO.getActualService()) + assistantServiceRecordDTO.getActualService());
            statDTO.setServiceAchievement(NumberUtil.doubleVal(statDTO.getServiceAchievement()) + achievement);

            statDTO.setStatSum(NumberUtil.doubleVal(statDTO.getStatSum()) + NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) / array.length);
            statDTO.setAchievementSum(NumberUtil.doubleVal(statDTO.getAchievementSum()) + achievement);
            salesManAchievementMap.put(serviceDTO.getId(), statDTO);
            washBeautyAchievementStatDTOMap.put(salesManDTO.getId(), salesManAchievementMap);
            assistantServiceRecordDTOList.add(assistantServiceRecordDTO);
          }
        }
      }
      this.saveOrUpdateAssistantService(shopId, assistantServiceRecordDTOList,statTime);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }


  /**
   * 根据开始时间、结束时间、shop_id统计员工的洗车美容业绩 并放入map和asssistantList中
   *
   * @param shopId
   * @param startTime
   * @param endTime
   */
  public void calculateRepairItemAssistant(Long shopId, long startTime, long endTime, int year, int month, Map<Long, SalesManDTO> salesManDTOMap, Map<Long, DepartmentDTO> departmentDTOMap,
                                           Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap,Long statTime) {


    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);

    List<RepairOrder> repairOrderList = txnService.countAgentAchievements(shopId, startTime, endTime);//维修美容单记录
    if (org.apache.commons.collections.CollectionUtils.isEmpty(repairOrderList)) {
      return;
    }

    List<AssistantProductRecordDTO> assistantProductRecordDTOList = new ArrayList<AssistantProductRecordDTO>();

    List<Long> orderIdList = new ArrayList<Long>();

    Map<Long, RepairOrder> repairOrderMap = new HashMap<Long, RepairOrder>();

    for (RepairOrder repairOrder : repairOrderList) {
      if (StringUtil.isEmpty(repairOrder.getProductSaler()) && StringUtil.isEmpty(repairOrder.getProductSalerIds())) {
        continue;
      }
      orderIdList.add(repairOrder.getId());
      repairOrderMap.put(repairOrder.getId(), repairOrder);
    }
    if (org.apache.commons.collections.CollectionUtils.isEmpty(orderIdList)) {
      return;
    }


    List<RepairOrderItemDTO> repairOrderItemDTOList = txnService.getRepairOrderItemDTOsByShopIdAndArrayOrderId(shopId, orderIdList.toArray(new Long[orderIdList.size()]));
    if (CollectionUtils.isEmpty(repairOrderItemDTOList)) {
      return;
    }
    Map<Long, List<RepairOrderItemDTO>> repairOrderItemDTOListMap = new HashMap<Long, List<RepairOrderItemDTO>>();

    List<RepairOrderItemDTO> repairOrderItemDTOs = null;

    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOList) {

      if (repairOrderItemDTOListMap.get(repairOrderItemDTO.getRepairOrderId()) == null) {
        repairOrderItemDTOs = new ArrayList<RepairOrderItemDTO>();
        repairOrderItemDTOs.add(repairOrderItemDTO);
        repairOrderItemDTOListMap.put(repairOrderItemDTO.getRepairOrderId(), repairOrderItemDTOs);
      } else {
        repairOrderItemDTOListMap.get(repairOrderItemDTO.getRepairOrderId()).add(repairOrderItemDTO);
      }
    }


    for (RepairOrder repairOrder : repairOrderList) {
      if (StringUtil.isEmpty(repairOrder.getProductSaler()) && StringUtil.isEmpty(repairOrder.getProductSalerIds())) {
        continue;
      }

      if (StringUtil.isNotEmpty(repairOrder.getProductSaler()) && StringUtil.isEmpty(repairOrder.getProductSalerIds())) {
        String[] array = repairOrder.getProductSaler().replace("，", ",").split(",");

        if (ArrayUtil.isEmpty(array)) {
          continue;
        }
        StringBuffer salesManIdStr = new StringBuffer();
        for (String salesManIdName : array) {

          IUserService userService = ServiceManager.getService(IUserService.class);
          SalesMan salesMan = userService.getSalesManByName(shopId, salesManIdName);
          if (salesMan == null) {
            continue;
          }
          salesManIdStr.append(salesMan.getId()).append(",");
        }
        if (StringUtil.isNotEmpty(salesManIdStr.toString())) {
          repairOrder.setProductSalerIds(salesManIdStr.substring(0, salesManIdStr.length() - 1));
          TxnWriter writer = txnDaoManager.getWriter();
          Object status = writer.begin();
          try {

            writer.update(repairOrder);
            writer.commit(status);
          } finally {
            writer.rollback(status);
          }
        }
      }

      if(StringUtil.isEmpty(repairOrder.getProductSalerIds())){
        continue;
      }

      RepairOrderDTO repairOrderDTO = repairOrder.toDTO();

      double achievement = 0;
      double salesProfitAchievement = 0;
      double totalAchievement = 0;
      if (repairOrder.getStatusEnum() == OrderStatus.REPAIR_REPEAL) {
        continue;
      }

      List<RepairOrderItemDTO> itemDTOList = repairOrderItemDTOListMap.get(repairOrder.getId());
      if (CollectionUtils.isEmpty(itemDTOList)) {
        continue;
      }
      repairOrderDTO.setItemDTOs(itemDTOList.toArray(new RepairOrderItemDTO[itemDTOList.size()]));

      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(shopId, repairOrderDTO.getProductIdList().toArray(new Long[repairOrderDTO.getProductIdList().size()]));

      for (RepairOrderItemDTO repairOrderItemDTO : itemDTOList) {

        ProductAchievementHistoryDTO productAchievementHistoryDTO = this.getProductAchievementHistoryDTO(shopId, repairOrderItemDTO.getProductId(), repairOrder.getVestDate());
        if (productAchievementHistoryDTO == null) {
          productAchievementHistoryDTO = new ProductAchievementHistoryDTO();
        }
        String[] array = repairOrderDTO.getProductSalerIds().replace("，", ",").split(",");
        ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(repairOrderItemDTO.getProductHistoryId(), shopId);

        for (String salesManIdName : array) {

          double salesProfit = NumberUtil.toReserve(repairOrderItemDTO.getTotal() - NumberUtil.doubleVal(repairOrderItemDTO.getTotalCostPrice()), NumberUtil.MONEY_PRECISION);
          //只有按数量计算提成的时候才使用单位换算
          AssistantProductRecordDTO assistantProductRecordDTO = new AssistantProductRecordDTO(repairOrderDTO, repairOrderItemDTO, OrderTypes.REPAIR);
          assistantProductRecordDTO.setTotal(NumberUtil.toReserve(assistantProductRecordDTO.getTotal() / array.length, NumberUtil.MONEY_PRECISION));
          if (productAchievementHistoryDTO.getAchievementType() == AchievementType.RATIO) {
            totalAchievement = productAchievementHistoryDTO.getAchievementAmount() * repairOrderItemDTO.getTotal() / PERCENT;

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("该商品销售提成计算方式:").append(productAchievementHistoryDTO.getAchievementType().getName()).append(",比率是").append(productAchievementHistoryDTO.getAchievementAmount() + "%");
            stringBuffer.append(",获得提成").append(totalAchievement).append("元").append(",共有" + array.length + "人销售").append(",每人平均获得提成").append(NumberUtil.toReserve(NumberUtil.doubleVal(totalAchievement) / array.length, NumberUtil.PRECISION)).append("元");
            assistantProductRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
          } else if (productAchievementHistoryDTO.getAchievementType() == AchievementType.AMOUNT) {
            totalAchievement = productAchievementHistoryDTO.getAchievementAmount() * repairOrderItemDTO.getAmount();

            ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductId());
            if (productLocalInfoDTO != null && UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productLocalInfoDTO)) {
              totalAchievement = NumberUtil.toReserve(totalAchievement * productLocalInfoDTO.getRate(), NumberUtil.PRECISION);
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("该商品销售提成计算方式:").append(productAchievementHistoryDTO.getAchievementType().getName()).append(",每").append(productLocalInfoDTO.getSellUnit()).append("是").append(productAchievementHistoryDTO.getAchievementAmount());
            stringBuffer.append(",总提成").append(totalAchievement).append("元")
                .append(",共有" + array.length + "人销售").append(",每人平均获得提成").append(NumberUtil.toReserve(NumberUtil.doubleVal(totalAchievement) / array.length, NumberUtil.PRECISION)).append("元");
            assistantProductRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
          }

          if (productAchievementHistoryDTO.getSalesProfitAchievementType() == AchievementType.RATIO) {
            salesProfitAchievement = productAchievementHistoryDTO.getSalesProfitAchievementAmount() * (salesProfit) / PERCENT;


            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("该商品利润提成计算方式:").append(productAchievementHistoryDTO.getSalesProfitAchievementType().getName()).append(",比率是").append(productAchievementHistoryDTO.getSalesProfitAchievementAmount() + "%");
            stringBuffer.append(",利润").append(salesProfit).append("元").append("总提成").append(salesProfitAchievement).append("元")
                .append(",共有" + array.length + "人销售").append(",每人平均获得提成").append(NumberUtil.toReserve(NumberUtil.doubleVal(salesProfitAchievement) / array.length, NumberUtil.PRECISION)).append("元");

            assistantProductRecordDTO.setProfitCalculateWay(stringBuffer.toString());

          } else if (productAchievementHistoryDTO.getSalesProfitAchievementType() == AchievementType.AMOUNT) {
            salesProfitAchievement = productAchievementHistoryDTO.getSalesProfitAchievementAmount() * repairOrderItemDTO.getAmount();

            ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductId());
            if (productLocalInfoDTO != null && UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productLocalInfoDTO)) {
              salesProfitAchievement = NumberUtil.toReserve(salesProfitAchievement * productLocalInfoDTO.getRate(), NumberUtil.PRECISION);
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("该商品利润提成计算方式:").append(productAchievementHistoryDTO.getSalesProfitAchievementType().getName()).append(",每").append(productLocalInfoDTO.getSellUnit()).append("是").append(productAchievementHistoryDTO.getSalesProfitAchievementAmount());
            stringBuffer.append(",总提成").append(salesProfitAchievement).append("元").append(",共有" + array.length + "人销售").append(",每人平均获得提成").append(NumberUtil.toReserve(NumberUtil.doubleVal(salesProfitAchievement) / array.length, NumberUtil.PRECISION)).append("元");
            assistantProductRecordDTO.setProfitCalculateWay(stringBuffer.toString());
          }


          achievement = NumberUtil.toReserve(NumberUtil.doubleVal(totalAchievement) / array.length, NumberUtil.PRECISION);
          salesProfitAchievement = NumberUtil.toReserve(NumberUtil.doubleVal(salesProfitAchievement) / array.length, NumberUtil.PRECISION);


          if (!NumberUtil.isLongNumber(salesManIdName)) {
            continue;
          }

          IUserService userService = ServiceManager.getService(IUserService.class);
          SalesManDTO salesMan = userService.getSalesManDTOById(Long.valueOf(salesManIdName));
          if (salesMan == null) {
            continue;
          }


          if (productHistoryDTO != null) {
            assistantProductRecordDTO.setProductName(productHistoryDTO.getName());

          }
          assistantProductRecordDTO.setProductAchievementHistoryId(productAchievementHistoryDTO.getId());
          assistantProductRecordDTO.setAssistantId(salesMan.getId());
          assistantProductRecordDTO.setAchievement(achievement);

          assistantProductRecordDTO.setAssistantName(salesMan.getName());

          Long departmentId = null;

          AssistantAchievementHistoryDTO assistantAchievementHistoryDTO = this.geAssistantAchievementHistoryByVestDate(shopId, salesMan.getId(), repairOrderDTO.getVestDate());
          if (assistantAchievementHistoryDTO != null) {
            departmentId = assistantAchievementHistoryDTO.getDepartmentId();
          }

          if (departmentId == null) {
            departmentId = salesMan.getDepartmentId();
          }

          if (departmentId != null) {
            DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
            if (departmentDTO == null) {
              LOG.error("calculateWashBeautyAssistant.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + salesMan.getId() + ",departmentId:" + departmentId);
            } else {
              assistantProductRecordDTO.setDepartmentId(departmentDTO.getId());
              assistantProductRecordDTO.setDepartmentName(departmentDTO.getName());
            }
          }


          AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesMan.getId());
          if (assistantAchievementStatDTO == null) {
            assistantAchievementStatDTO = new AssistantAchievementStatDTO();
            assistantAchievementStatDTO.setShopId(shopId);
            assistantAchievementStatDTO.setStatYear(year);
            assistantAchievementStatDTO.setStatMonth(month);
            assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
            assistantAchievementStatDTO.setAssistantId(salesMan.getId());
            assistantAchievementStatDTO.setDepartmentId(departmentId);
            assistantAchievementStatDTO.setStatTime(endTime - 10000);
            assistantAchievementStatDTO.setAssistantName(salesMan.getName());
            assistantAchievementStatDTO.setDepartmentName(assistantProductRecordDTO.getDepartmentName());
          }

          if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getSalesAchievement() != null) {
            assistantAchievementStatDTO.setSalesAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesAchievementByAssistant()) + assistantAchievementHistoryDTO.getSalesAchievement() * repairOrderItemDTO.getTotal() / (PERCENT * array.length));
            assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + assistantAchievementHistoryDTO.getSalesAchievement() * repairOrderItemDTO.getTotal() / (PERCENT * array.length));
            assistantProductRecordDTO.setAchievementByAssistant(assistantAchievementHistoryDTO.getSalesAchievement() * repairOrderItemDTO.getTotal() / (PERCENT * array.length));

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("该员工的销售提成比率:").append(assistantAchievementHistoryDTO.getSalesAchievement());
            stringBuffer.append("%,销售金额").append(repairOrderItemDTO.getTotal()).append(",获得提成").append(assistantAchievementHistoryDTO.getSalesAchievement() * NumberUtil.doubleVal(repairOrderItemDTO.getTotal()) / PERCENT).append("元");
            assistantProductRecordDTO.setAchievementByAssistantCalculateWay(stringBuffer.toString());
          }
          if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getSalesProfitAchievement() != null) {
            double profitAchievementByAssistant = NumberUtil.toReserve(assistantAchievementHistoryDTO.getSalesProfitAchievement() * salesProfit / (PERCENT * array.length), NumberUtil.MONEY_PRECISION);
            assistantAchievementStatDTO.setSalesProfitAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant()) + profitAchievementByAssistant);
            assistantProductRecordDTO.setProfitAchievementByAssistant(profitAchievementByAssistant);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("该员工的利润提成比率:").append(assistantAchievementHistoryDTO.getSalesProfitAchievement());
            stringBuffer.append("%,利润").append(salesProfit).append(",获得提成").append(profitAchievementByAssistant).append("元");
            assistantProductRecordDTO.setProfitByAssistantCalculateWay(stringBuffer.toString());
          }

          assistantProductRecordDTO.setProfitAchievement(salesProfitAchievement);
          assistantProductRecordDTO.setProfit(salesProfit / array.length);
          assistantAchievementStatDTO.setSale(NumberUtil.doubleVal(assistantAchievementStatDTO.getSale()) + repairOrderItemDTO.getTotal() / array.length);
          assistantAchievementStatDTO.setSaleAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getSaleAchievement()) + achievement);
          assistantAchievementStatDTO.setSalesProfit(assistantAchievementStatDTO.getSalesProfit() + salesProfit / array.length);
          assistantAchievementStatDTO.setSalesProfitAchievement(assistantAchievementStatDTO.getSalesProfitAchievement() + salesProfitAchievement);

          assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) + repairOrderItemDTO.getTotal() / array.length);
          assistantAchievementStatDTO.setAchievementSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()) + achievement);
          assistantAchievementStatDTOMap.put(salesMan.getId(), assistantAchievementStatDTO);

          assistantProductRecordDTOList.add(assistantProductRecordDTO);
        }
      }
    }

    this.saveOrUpdateAssistantRepairProduct(shopId, assistantProductRecordDTOList,statTime);

  }


  /**
   * 根据开始时间 结束时间 统计每个员工的退卡业绩
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param year
   * @param month
   * @param salesManDTOMap
   * @param departmentDTOMap
   * @param assistantAchievementStatDTOMap
   */
  public void calculateMemberReturnAssistant(Long shopId, long startTime, long endTime, int year, int month, Map<Long, SalesManDTO> salesManDTOMap,
                                             Map<Long, DepartmentDTO> departmentDTOMap, Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap,
                                             Long statTime) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<MemberCardReturnDTO> memberCardReturnDTOList = txnService.getMemberReturnListByReturnDate(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(memberCardReturnDTOList)) {
      return;
    }

//    List<MemberAchievementHistory> memberAchievementHistoryList = this.getMemberAchievementHistory(shopId, null);
    List<AssistantMemberRecordDTO> assistantMemberRecordDTOList = new ArrayList<AssistantMemberRecordDTO>();

    for (MemberCardReturnDTO memberCardReturnDTO : memberCardReturnDTOList) {
      if (memberCardReturnDTO == null) {
        continue;
      }
      List<MemberCardReturnItemDTO> memberCardReturnItemDTOList = txnService.getMemberCardReturnItemDTOByOrderId(memberCardReturnDTO.getShopId(), memberCardReturnDTO.getId());
      if (CollectionUtils.isEmpty(memberCardReturnItemDTOList)) {
        continue;
      }
      MemberCardReturnItemDTO memberCardReturnItemDTO = memberCardReturnItemDTOList.get(0);
      if (memberCardReturnItemDTO.getSalesId() == null) {
        continue;
      }


      SalesManDTO salesManDTO = salesManDTOMap.get(memberCardReturnItemDTO.getSalesId());
      if (salesManDTO == null) {
        LOG.error("calculateMemberReturnAssistant.salesMan is null :" + "shopId:" + shopId + ",salesManId:" + memberCardReturnItemDTO.getSalesId());
        return;
      }

//      MemberAchievementHistoryDTO memberAchievementHistoryDTO = this.getMemberAchievementHistoryDTO(null, memberCardReturnDTO.getReturnDate(), memberAchievementHistoryList);

      double achievement = 0;
//      if (memberAchievementHistoryDTO.getSalesTotalAchievementType() == AchievementType.RATIO) {
//        achievement = memberAchievementHistoryDTO.getSalesTotalAchievementAmount() * memberCardReturnDTO.getTotal() /PERCENT;
//      } else {
//        achievement = memberAchievementHistoryDTO.getSalesTotalAchievementAmount();
//      }

      AssistantMemberRecordDTO assistantMemberRecordDTO = new AssistantMemberRecordDTO(memberCardReturnDTO, memberCardReturnItemDTO);
      assistantMemberRecordDTO.setAssistantId(memberCardReturnItemDTO.getSalesId());
      assistantMemberRecordDTO.setAchievement(achievement);
      assistantMemberRecordDTO.setAchievementByAssistantCalculateWay("会员卡退卡不计算提成");
      assistantMemberRecordDTO.setAchievementCalculateWay("会员卡退卡不计算提成");
      assistantMemberRecordDTO.setAchievementByAssistant(achievement);

      if (memberCardReturnDTO.getLastMemberCardOrderId() != null) {
        List<MemberCardOrderItemDTO> memberCardOrderItemDTOList = txnService.getMemberCardOrderItemDTOByOrderId(memberCardReturnDTO.getShopId(), memberCardReturnDTO.getLastMemberCardOrderId());

        MemberCardOrderItemDTO memberCardOrderItemDTO = CollectionUtil.getFirst(memberCardOrderItemDTOList);
        if (memberCardOrderItemDTO.getCardId() != null) {

          IMembersService membersService = ServiceManager.getService(IMembersService.class);
          MemberCard memberCard = membersService.getMemberCardById(shopId, memberCardOrderItemDTO.getCardId());
          if (memberCard != null) {
            assistantMemberRecordDTO.setMemberCardName(memberCard.getName());
            assistantMemberRecordDTO.setMemberCardTotal(NumberUtil.doubleVal(memberCard.getPrice()));
            if (TxnConstant.VALUE_CARD.equals(memberCard.getType())) {
              assistantMemberRecordDTO.setMemberCardType(MemberCardType.STORED_CARD);
            } else if (TxnConstant.TIME_CARD.equals(memberCard.getType())) {
              assistantMemberRecordDTO.setMemberCardType(MemberCardType.TIMES_CARD);
            }
          }
        }
      }


      assistantMemberRecordDTO.setAssistantName(salesManDTO.getName());

      Long departmentId = null;

      AssistantAchievementHistoryDTO  assistantAchievementHistoryDTO = this.geAssistantAchievementHistoryByVestDate(shopId,salesManDTO.getId(),memberCardReturnDTO.getVestDate());
      if(assistantAchievementHistoryDTO != null) {
        departmentId = assistantAchievementHistoryDTO.getDepartmentId();
      }

      if(departmentId == null){
        departmentId = salesManDTO.getDepartmentId();
      }

      if (departmentId != null) {
        DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
        if (departmentDTO == null) {
          LOG.error("calculateMemberReturnAssistant.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + salesManDTO.getId() + ",departmentId:" + departmentId);
        } else {
          assistantMemberRecordDTO.setDepartmentId(departmentDTO.getId());
          assistantMemberRecordDTO.setDepartmentName(departmentDTO.getName());
        }
      }

      AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesManDTO.getId());
      if (assistantAchievementStatDTO == null) {
        assistantAchievementStatDTO = new AssistantAchievementStatDTO();
        assistantAchievementStatDTO.setShopId(shopId);
        assistantAchievementStatDTO.setStatYear(year);
        assistantAchievementStatDTO.setStatMonth(month);
        assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
        assistantAchievementStatDTO.setAssistantId(salesManDTO.getId());
        assistantAchievementStatDTO.setDepartmentId(departmentId);
        assistantAchievementStatDTO.setStatTime(endTime - 10000);
        assistantAchievementStatDTO.setAssistantName(salesManDTO.getName());
        assistantAchievementStatDTO.setDepartmentName(assistantMemberRecordDTO.getDepartmentName());
      }

      assistantAchievementStatDTO.setMember(NumberUtil.doubleVal(assistantAchievementStatDTO.getMember()) - memberCardReturnDTO.getTotal());
      assistantAchievementStatDTO.setMemberTimes(NumberUtil.longValue(assistantAchievementStatDTO.getMemberTimes()) - 1);
      assistantAchievementStatDTO.setMemberAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberAchievement()) - achievement);

      assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) - memberCardReturnDTO.getTotal());
      assistantAchievementStatDTO.setAchievementSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()) - achievement);
      assistantAchievementStatDTOMap.put(salesManDTO.getId(), assistantAchievementStatDTO);

      assistantMemberRecordDTOList.add(assistantMemberRecordDTO);
    }
    this.saveOrUpdateAssistantMember(shopId, assistantMemberRecordDTOList,statTime);
  }


  /**
   * 根据开始时间、结束时间、shop_id统计员工的洗车美容业绩 并放入map和asssistantList中
   *
   * @param shopId
   * @param startTime
   * @param endTime
   */
  public void calculateSaleReturnAssistant(Long shopId, long startTime, long endTime, int year, int month, Map<Long, SalesManDTO> salesManDTOMap, Map<Long, DepartmentDTO> departmentDTOMap,
                                           Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap,Long statTime) throws Exception {

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesReturn> salesReturnList = writer.countSalesReturnAchievements(shopId, startTime, endTime);//销售记录
    if (CollectionUtils.isEmpty(salesReturnList)) {
      return;
    }

    List<AssistantProductRecordDTO> assistantProductRecordDTOList = new ArrayList<AssistantProductRecordDTO>();

    List<Long> orderIdList = new ArrayList<Long>();


    for (SalesReturn salesReturn : salesReturnList) {
      if (salesReturn.getSalesReturnerId() == null && StringUtil.isEmpty(salesReturn.getSalesReturner())) {
        continue;
      }

      orderIdList.add(salesReturn.getId());
    }

    if (CollectionUtils.isEmpty(orderIdList)) {
      return;
    }
    List<SalesReturnItemDTO> salesReturnItemDTOList = txnService.getSalesReturnItemDTOs(shopId, orderIdList.toArray(new Long[orderIdList.size()]));


    if (CollectionUtils.isEmpty(salesReturnItemDTOList)) {
      return;
    }
    Map<Long, List<SalesReturnItemDTO>> salesReturnItemMap = new HashMap<Long, List<SalesReturnItemDTO>>();

    List<SalesReturnItemDTO> orderItemDTOs = null;

    for (SalesReturnItemDTO salesReturnItemDTO : salesReturnItemDTOList) {

      if (salesReturnItemMap.get(salesReturnItemDTO.getSalesReturnId()) == null) {
        orderItemDTOs = new ArrayList<SalesReturnItemDTO>();
        orderItemDTOs.add(salesReturnItemDTO);
        salesReturnItemMap.put(salesReturnItemDTO.getSalesReturnId(), orderItemDTOs);
      } else {
        salesReturnItemMap.get(salesReturnItemDTO.getSalesReturnId()).add(salesReturnItemDTO);
      }
    }


    for (SalesReturn salesReturn : salesReturnList) {
      if (salesReturn.getSalesReturnerId() == null && StringUtil.isEmpty(salesReturn.getSalesReturner())) {
        continue;
      }

      SalesManDTO salesMan = null;
      if (NumberUtil.longValue(salesReturn.getSalesReturnerId()) > 0) {
        salesMan = salesManDTOMap.get(Long.valueOf(salesReturn.getSalesReturnerId()));
      }
      if (salesMan == null) {
        SalesMan man = userService.getSalesManByName(shopId, salesReturn.getSalesReturner());
        if (man == null) {
          continue;
        } else {
          salesMan = man.toDTO();
          salesReturn.setSalesReturnerId(salesMan.getId());
        }
      }

      SalesReturnDTO salesReturnDTO = salesReturn.toDTO();

      double totalAchievement = 0;
      double salesProfitAchievement = 0;

      List<SalesReturnItemDTO> itemDTOList = salesReturnItemMap.get(salesReturn.getId());
      if (CollectionUtils.isEmpty(itemDTOList)) {
        continue;
      }
      for (SalesReturnItemDTO salesReturnItemDTO : itemDTOList) {
        if (salesReturnItemDTO == null) {
          continue;
        }
        double salesProfit = NumberUtil.toReserve(NumberUtil.doubleVal(salesReturnItemDTO.getTotal()) - NumberUtil.doubleVal(salesReturnItemDTO.getTotalCostPrice()), NumberUtil.MONEY_PRECISION);

        ProductAchievementHistoryDTO productAchievementHistoryDTO = this.getProductAchievementHistoryDTO(shopId, salesReturnItemDTO.getProductId(), salesReturnDTO.getVestDate());
        if (productAchievementHistoryDTO == null) {
          productAchievementHistoryDTO = new ProductAchievementHistoryDTO();
        }

        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesReturnItemDTO.getProductId(), shopId);
        AssistantProductRecordDTO assistantProductRecordDTO = new AssistantProductRecordDTO(salesReturnDTO, salesReturnItemDTO, OrderTypes.SALE_RETURN);

        //只有按数量统计的时候才使用商品的单位
        if (productAchievementHistoryDTO.getAchievementType() == AchievementType.RATIO) {
          totalAchievement = productAchievementHistoryDTO.getAchievementAmount() * salesReturnItemDTO.getTotal() / PERCENT;
          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品退货提成计算方式:").append(productAchievementHistoryDTO.getAchievementType().getName()).append(",比率是").append(productAchievementHistoryDTO.getAchievementAmount() + "%");
          stringBuffer.append(",减掉提成").append(totalAchievement).append("元");
          assistantProductRecordDTO.setAchievementCalculateWay(stringBuffer.toString());

        } else if (productAchievementHistoryDTO.getAchievementType() == AchievementType.AMOUNT) {
          totalAchievement = productAchievementHistoryDTO.getAchievementAmount() * salesReturnItemDTO.getAmount();

          if (productLocalInfoDTO != null && UnitUtil.isStorageUnit(salesReturnItemDTO.getUnit(), productLocalInfoDTO)) {
            totalAchievement = NumberUtil.toReserve(totalAchievement * productLocalInfoDTO.getRate());
          }

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品退货提成计算方式:").append(productAchievementHistoryDTO.getAchievementType().getName()).append(",每")
              .append(productLocalInfoDTO!=null? productLocalInfoDTO.getSellUnit():"").append("是").append(productAchievementHistoryDTO.getAchievementAmount());
          if(productLocalInfoDTO == null){
            LOG.error("productLocalInfo id :{} is null 看到这条报错需要跟进做数据订正", salesReturnItemDTO.getProductId());
          }
          stringBuffer.append(",减掉提成").append(totalAchievement).append("元");
          assistantProductRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
        }

        if (productAchievementHistoryDTO.getSalesProfitAchievementType() == AchievementType.RATIO) {
          salesProfitAchievement = productAchievementHistoryDTO.getSalesProfitAchievementAmount() * (salesProfit) / PERCENT;

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品退货利润提成计算方式:").append(productAchievementHistoryDTO.getSalesProfitAchievementType().getName()).append(",比率是").append(productAchievementHistoryDTO.getSalesProfitAchievementAmount() + "%");
          stringBuffer.append(",利润").append(salesProfit).append("元");
          stringBuffer.append(",减掉提成").append(salesProfitAchievement).append("元");
          assistantProductRecordDTO.setProfitCalculateWay(stringBuffer.toString());


        } else if (productAchievementHistoryDTO.getSalesProfitAchievementType() == AchievementType.AMOUNT) {
          salesProfitAchievement = productAchievementHistoryDTO.getSalesProfitAchievementAmount() * salesReturnItemDTO.getAmount();

          if (productLocalInfoDTO != null && UnitUtil.isStorageUnit(salesReturnItemDTO.getUnit(), productLocalInfoDTO)) {
            salesProfitAchievement = NumberUtil.toReserve(salesProfitAchievement * productLocalInfoDTO.getRate(), NumberUtil.PRECISION);
          }
          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品退货利润提成计算方式:").append(productAchievementHistoryDTO.getSalesProfitAchievementType().getName()).append(",每").append(productLocalInfoDTO.getSellUnit()).append("是").append(productAchievementHistoryDTO.getSalesProfitAchievementAmount());
          stringBuffer.append(",减掉提成").append(salesProfitAchievement).append("元");
          assistantProductRecordDTO.setProfitCalculateWay(stringBuffer.toString());
        }


        ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(salesReturnItemDTO.getProductHistoryId(), shopId);
        if (productHistoryDTO != null) {
          assistantProductRecordDTO.setProductName(productHistoryDTO.getName());

        }
        assistantProductRecordDTO.setProductAchievementHistoryId(productAchievementHistoryDTO.getId());
        assistantProductRecordDTO.setAssistantId(salesMan.getId());
        assistantProductRecordDTO.setAchievement(-totalAchievement);
        assistantProductRecordDTO.setProfitAchievement(-salesProfitAchievement);
        assistantProductRecordDTO.setProfit(-NumberUtil.toReserve(salesReturnItemDTO.getTotal() - NumberUtil.doubleVal(salesReturnItemDTO.getTotalCostPrice()),NumberUtil.MONEY_PRECISION));

        assistantProductRecordDTO.setAssistantName(salesMan.getName());

        Long departmentId = null;

        AssistantAchievementHistoryDTO assistantAchievementHistoryDTO = this.geAssistantAchievementHistoryByVestDate(shopId, salesMan.getId(), salesReturnDTO.getVestDate());
        if (assistantAchievementHistoryDTO != null) {
          departmentId = assistantAchievementHistoryDTO.getDepartmentId();
        }

        if (departmentId == null) {
          departmentId = salesMan.getDepartmentId();
        }

        if (departmentId != null) {
          DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
          if (departmentDTO == null) {
            LOG.error("calculateWashBeautyAssistant.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + salesMan.getId() + ",departmentId:" + departmentId);
          } else {
            assistantProductRecordDTO.setDepartmentId(departmentDTO.getId());
            assistantProductRecordDTO.setDepartmentName(departmentDTO.getName());
          }
        }


        AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesMan.getId());
        if (assistantAchievementStatDTO == null) {
          assistantAchievementStatDTO = new AssistantAchievementStatDTO();
          assistantAchievementStatDTO.setShopId(shopId);
          assistantAchievementStatDTO.setStatYear(year);
          assistantAchievementStatDTO.setStatMonth(month);
          assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
          assistantAchievementStatDTO.setAssistantId(salesMan.getId());
          assistantAchievementStatDTO.setDepartmentId(departmentId);
          assistantAchievementStatDTO.setStatTime(endTime - 10000);
          assistantAchievementStatDTO.setAssistantName(salesMan.getName());
          assistantAchievementStatDTO.setDepartmentName(assistantProductRecordDTO.getDepartmentName());
        }

        if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getSalesAchievement() != null) {
          double salesAchievementByAssistant =  assistantAchievementHistoryDTO.getSalesAchievement() * salesReturnItemDTO.getTotal() / PERCENT;
          assistantAchievementStatDTO.setSalesAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesAchievementByAssistant()) - salesAchievementByAssistant);
          assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) - salesAchievementByAssistant);
          assistantProductRecordDTO.setAchievementByAssistant(-salesAchievementByAssistant);

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该员工的退货提成比率:").append(assistantAchievementHistoryDTO.getSalesAchievement());
          stringBuffer.append("%,退货金额").append(salesReturnItemDTO.getTotal()).append(",减掉提成").append(assistantAchievementHistoryDTO.getSalesAchievement() * NumberUtil.doubleVal(salesReturnItemDTO.getTotal()) / PERCENT).append("元");
          assistantProductRecordDTO.setAchievementByAssistantCalculateWay(stringBuffer.toString());
        }
        if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getSalesProfitAchievement() != null) {
          double profitAchievementByAssistant = NumberUtil.toReserve(assistantAchievementHistoryDTO.getSalesProfitAchievement() * salesProfit / PERCENT, NumberUtil.MONEY_PRECISION);

          assistantAchievementStatDTO.setSalesProfitAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant()) - profitAchievementByAssistant);
          assistantProductRecordDTO.setProfitAchievementByAssistant(-profitAchievementByAssistant);

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该员工的退货利润提成比率:").append(assistantAchievementHistoryDTO.getSalesProfitAchievement());
          stringBuffer.append("%,利润").append(salesProfit).append(",减掉提成").append(profitAchievementByAssistant).append("元");
          assistantProductRecordDTO.setProfitByAssistantCalculateWay(stringBuffer.toString());

        }

        assistantAchievementStatDTO.setSale(NumberUtil.doubleVal(assistantAchievementStatDTO.getSale()) - salesReturnItemDTO.getTotal());
        assistantAchievementStatDTO.setSaleAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getSaleAchievement()) - totalAchievement);
        assistantAchievementStatDTO.setSalesProfit(assistantAchievementStatDTO.getSalesProfit() - salesProfit);
        assistantAchievementStatDTO.setSalesProfitAchievement(assistantAchievementStatDTO.getSalesProfitAchievement() - salesProfitAchievement);
        assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) - salesReturnItemDTO.getTotal());
        assistantAchievementStatDTO.setAchievementSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()) - totalAchievement);
        assistantAchievementStatDTOMap.put(salesMan.getId(), assistantAchievementStatDTO);

        assistantProductRecordDTOList.add(assistantProductRecordDTO);
      }
    }


    this.updateSalesReturnerId(salesReturnList);
    this.saveOrUpdateAssistantProduct(shopId, assistantProductRecordDTOList,statTime);

  }


  public void updateSalesReturnerId(List<SalesReturn> salesReturnList) {
    if (CollectionUtils.isEmpty(salesReturnList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (SalesReturn salesReturn : salesReturnList) {

        writer.update(salesReturn);
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据开始时间、结束时间、shop_id统计员工的洗车美容业绩 并放入map和asssistantList中
   *
   * @param shopId
   * @param startTime
   * @param endTime
   */
  public void calculateSaleAssistant(Long shopId, long startTime, long endTime, int year, int month,Map<Long,SalesManDTO> salesManMap, Map<Long, DepartmentDTO> departmentDTOMap,
                                     Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap,Long statTime) throws Exception {


    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<SalesOrder> salesOrderList = txnService.countSalesAgentAchievements(shopId, startTime, endTime);//销售记录
    if (CollectionUtils.isEmpty(salesOrderList)) {
      return;
    }

    IUserService userService = ServiceManager.getService(IUserService.class);

    List<AssistantProductRecordDTO> assistantProductRecordDTOList = new ArrayList<AssistantProductRecordDTO>();

    List<Long> orderIdList = new ArrayList<Long>();


    for (SalesOrder salesOrder : salesOrderList) {
      if (StringUtil.isEmpty(salesOrder.getGoodsSaler())) {
        continue;
      }

      if (salesOrder.getGoodsSalerId() == null) {
        SalesMan salesMan = userService.getSalesManByName(shopId, salesOrder.getGoodsSaler());

        if (salesMan == null) {
          continue;
        }
        salesOrder.setGoodsSalerId(salesMan.getId());

        TxnWriter writer = txnDaoManager.getWriter();
        Object status = writer.begin();
        try {

          writer.update(salesOrder);
          writer.commit(status);
        } finally {
          writer.rollback(status);
        }

      }
      orderIdList.add(salesOrder.getId());
    }

    if (CollectionUtils.isEmpty(orderIdList)) {
      return;
    }

    List<SalesOrderItemDTO> salesOrderItemDTOs = txnService.getSalesOrderItemDTOs(shopId, orderIdList.toArray(new Long[orderIdList.size()]));

    if (CollectionUtils.isEmpty(salesOrderItemDTOs)) {
      return;
    }
    Map<Long, List<SalesOrderItemDTO>> salesOrderItemMap = new HashMap<Long, List<SalesOrderItemDTO>>();

    List<SalesOrderItemDTO> orderItemDTOs = null;

    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {

      if (salesOrderItemMap.get(salesOrderItemDTO.getSalesOrderId()) == null) {
        orderItemDTOs = new ArrayList<SalesOrderItemDTO>();
        orderItemDTOs.add(salesOrderItemDTO);
        salesOrderItemMap.put(salesOrderItemDTO.getSalesOrderId(), orderItemDTOs);
      } else {
        salesOrderItemMap.get(salesOrderItemDTO.getSalesOrderId()).add(salesOrderItemDTO);
      }
    }


    for (SalesOrder salesOrder : salesOrderList) {

      if (salesOrder.getGoodsSalerId() == null) {
        continue;
      }

      SalesManDTO salesMan = salesManMap.get(salesOrder.getGoodsSalerId());
      if (salesMan == null) {
        continue;
      }

      SalesOrderDTO salesOrderDTO = salesOrder.toDTO();

      if (salesOrderDTO.getStatus() == OrderStatus.SALE_REPEAL) {
        continue;
      }

      List<SalesOrderItemDTO> itemDTOList = salesOrderItemMap.get(salesOrderDTO.getId());
      if (CollectionUtils.isEmpty(itemDTOList)) {
        continue;
      }
      for (SalesOrderItemDTO salesOrderItemDTO : itemDTOList) {
        if (salesOrderItemDTO == null || StringUtils.isEmpty(salesOrderDTO.getGoodsSaler())) {
          continue;
        }
        double totalAchievement = 0;
        double salesProfitAchievement = 0;

        ProductAchievementHistoryDTO productAchievementHistoryDTO = this.getProductAchievementHistoryDTO(shopId, salesOrderItemDTO.getProductId(), salesOrderDTO.getVestDate());

        if (productAchievementHistoryDTO == null) {
          productAchievementHistoryDTO = new ProductAchievementHistoryDTO();
        }
        AssistantProductRecordDTO assistantProductRecordDTO = new AssistantProductRecordDTO(salesOrderDTO, salesOrderItemDTO, OrderTypes.SALE);

        double salesProfit = NumberUtil.toReserve(NumberUtil.doubleVal(salesOrderItemDTO.getTotal()) - NumberUtil.doubleVal(salesOrderItemDTO.getTotalCostPrice()), NumberUtil.MONEY_PRECISION);

        //只有按数量统计的时候才使用商品的单位
        if (productAchievementHistoryDTO.getAchievementType() == AchievementType.RATIO) {
          totalAchievement = productAchievementHistoryDTO.getAchievementAmount() * salesOrderItemDTO.getTotal() / PERCENT;
          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品销售提成计算方式:").append(productAchievementHistoryDTO.getAchievementType().getName()).append(",比率是").append(productAchievementHistoryDTO.getAchievementAmount() + "%");
          stringBuffer.append(",获得提成").append(totalAchievement).append("元");
          assistantProductRecordDTO.setAchievementCalculateWay(stringBuffer.toString());

        } else if (productAchievementHistoryDTO.getAchievementType() == AchievementType.AMOUNT) {
          totalAchievement = productAchievementHistoryDTO.getAchievementAmount() * salesOrderItemDTO.getAmount();
          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesOrderItemDTO.getProductId(), shopId);

          if (productLocalInfoDTO != null && UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productLocalInfoDTO)) {
            totalAchievement = NumberUtil.toReserve(totalAchievement * productLocalInfoDTO.getRate());
          }

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品销售提成计算方式:").append(productAchievementHistoryDTO.getAchievementType().getName()).append(",每").append(productLocalInfoDTO.getSellUnit()).append("是").append(productAchievementHistoryDTO.getAchievementAmount());
          stringBuffer.append(",获得提成").append(totalAchievement).append("元");
          assistantProductRecordDTO.setAchievementCalculateWay(stringBuffer.toString());
        }

        if (productAchievementHistoryDTO.getSalesProfitAchievementType() == AchievementType.RATIO) {
          salesProfitAchievement = productAchievementHistoryDTO.getSalesProfitAchievementAmount() * (salesProfit) / PERCENT;

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品利润提成计算方式:").append(productAchievementHistoryDTO.getSalesProfitAchievementType().getName()).append(",比率是").append(productAchievementHistoryDTO.getSalesProfitAchievementAmount() + "%");
          stringBuffer.append(",利润").append(salesProfit).append("元");
          stringBuffer.append(",获得提成").append(salesProfitAchievement).append("元");
          assistantProductRecordDTO.setProfitCalculateWay(stringBuffer.toString());

        } else if (productAchievementHistoryDTO.getSalesProfitAchievementType() == AchievementType.AMOUNT) {
          salesProfitAchievement = productAchievementHistoryDTO.getSalesProfitAchievementAmount() * salesOrderItemDTO.getAmount();

          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesOrderItemDTO.getProductId(), shopId);
          if (productLocalInfoDTO != null && UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productLocalInfoDTO)) {
            salesProfitAchievement = NumberUtil.toReserve(salesProfitAchievement * productLocalInfoDTO.getRate(), NumberUtil.PRECISION);
          }

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该商品利润提成计算方式:").append(productAchievementHistoryDTO.getSalesProfitAchievementType().getName()).append(",每").append(productLocalInfoDTO.getSellUnit()).append("是").append(productAchievementHistoryDTO.getSalesProfitAchievementAmount());
          stringBuffer.append(",获得提成").append(salesProfitAchievement).append("元");
          assistantProductRecordDTO.setProfitCalculateWay(stringBuffer.toString());
        }
        ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(salesOrderItemDTO.getProductHistoryId(), shopId);

        if (productHistoryDTO != null) {
          assistantProductRecordDTO.setProductName(productHistoryDTO.getName());

        }
        assistantProductRecordDTO.setProductAchievementHistoryId(productAchievementHistoryDTO.getId());
        assistantProductRecordDTO.setAssistantId(salesMan.getId());
        assistantProductRecordDTO.setAchievement(totalAchievement);

        assistantProductRecordDTO.setAssistantName(salesMan.getName());

        Long departmentId = null;


        AssistantAchievementHistoryDTO assistantAchievementHistoryDTO = this.geAssistantAchievementHistoryByVestDate(shopId, salesMan.getId(), salesOrderDTO.getVestDate());

        if (assistantAchievementHistoryDTO != null) {
          departmentId = assistantAchievementHistoryDTO.getDepartmentId();
        }

        if (departmentId == null) {
          departmentId = salesMan.getDepartmentId();
        }

        if (departmentId != null) {
          DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
          if (departmentDTO == null) {
            LOG.error("calculateWashBeautyAssistant.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + salesMan.getId() + ",departmentId:" + departmentId);
          } else {
            assistantProductRecordDTO.setDepartmentId(departmentDTO.getId());
            assistantProductRecordDTO.setDepartmentName(departmentDTO.getName());
          }
        }


        AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesMan.getId());
        if (assistantAchievementStatDTO == null) {
          assistantAchievementStatDTO = new AssistantAchievementStatDTO();
          assistantAchievementStatDTO.setShopId(shopId);
          assistantAchievementStatDTO.setStatYear(year);
          assistantAchievementStatDTO.setStatMonth(month);
          assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
          assistantAchievementStatDTO.setAssistantId(salesMan.getId());
          assistantAchievementStatDTO.setDepartmentId(departmentId);
          assistantAchievementStatDTO.setStatTime(endTime - 10000);
          assistantAchievementStatDTO.setAssistantName(salesMan.getName());
          assistantAchievementStatDTO.setDepartmentName(assistantProductRecordDTO.getDepartmentName());
        }

        if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getSalesAchievement() != null) {
          assistantAchievementStatDTO.setSalesAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesAchievementByAssistant()) + assistantAchievementHistoryDTO.getSalesAchievement() * salesOrderItemDTO.getTotal() / PERCENT);
          assistantAchievementStatDTO.setAchievementSumByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()) + assistantAchievementHistoryDTO.getSalesAchievement() * salesOrderItemDTO.getTotal() / PERCENT);
          assistantProductRecordDTO.setAchievementByAssistant(assistantAchievementHistoryDTO.getSalesAchievement() * salesOrderItemDTO.getTotal() / PERCENT);

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该员工的销售提成比率:").append(assistantAchievementHistoryDTO.getSalesAchievement());
          stringBuffer.append("%,销售金额").append(salesOrderItemDTO.getTotal()).append(",获得提成").append(assistantAchievementHistoryDTO.getSalesAchievement() * NumberUtil.doubleVal(salesOrderItemDTO.getTotal()) / PERCENT).append("元");
          assistantProductRecordDTO.setAchievementByAssistantCalculateWay(stringBuffer.toString());
        }
        if (assistantAchievementHistoryDTO != null && assistantAchievementHistoryDTO.getSalesProfitAchievement() != null) {

          double profitAchievementByAssistant = NumberUtil.toReserve(assistantAchievementHistoryDTO.getSalesProfitAchievement() * salesProfit / PERCENT, NumberUtil.MONEY_PRECISION);
          assistantAchievementStatDTO.setSalesProfitAchievementByAssistant(NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant()) + profitAchievementByAssistant);
          assistantProductRecordDTO.setProfitAchievementByAssistant(profitAchievementByAssistant);

          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("该员工的利润提成比率:").append(assistantAchievementHistoryDTO.getSalesProfitAchievement());
          stringBuffer.append("%,利润").append(salesProfit).append(",获得提成").append(profitAchievementByAssistant).append("元");
          assistantProductRecordDTO.setProfitByAssistantCalculateWay(stringBuffer.toString());
        }
        assistantProductRecordDTO.setProfitAchievement(salesProfitAchievement);
        assistantProductRecordDTO.setProfit(salesProfit);

        assistantAchievementStatDTO.setSale(NumberUtil.doubleVal(assistantAchievementStatDTO.getSale()) + salesOrderItemDTO.getTotal());
        assistantAchievementStatDTO.setSaleAchievement(NumberUtil.doubleVal(assistantAchievementStatDTO.getSaleAchievement()) + totalAchievement);
        assistantAchievementStatDTO.setSalesProfit(assistantAchievementStatDTO.getSalesProfit() + salesProfit);
        assistantAchievementStatDTO.setSalesProfitAchievement(assistantAchievementStatDTO.getSalesProfitAchievement() + salesProfitAchievement);

        assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) + salesOrderItemDTO.getTotal());
        assistantAchievementStatDTO.setAchievementSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()) + totalAchievement);
        assistantAchievementStatDTOMap.put(salesMan.getId(), assistantAchievementStatDTO);

        assistantProductRecordDTOList.add(assistantProductRecordDTO);
      }
    }
    this.saveOrUpdateAssistantProduct(shopId, assistantProductRecordDTOList,statTime);


  }

  public void saveOrUpdateShopAchievementConfig(List<ShopAchievementConfig> shopAchievementConfigList) {
    if (CollectionUtils.isEmpty(shopAchievementConfigList)) {
      return;
    }

    for (ShopAchievementConfig shopAchievementConfig : shopAchievementConfigList) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        writer.save(shopAchievementConfig);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }


  public void deleteShopAchievementConfig(Set<Long> shopAchievementConfigList) {
    if (CollectionUtils.isEmpty(shopAchievementConfigList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Long id : shopAchievementConfigList) {
        writer.delete(ShopAchievementConfig.class, id);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  public void saveOrUpdateAssistantProduct(Long shopId,List<AssistantProductRecordDTO> assistantProductRecordDTOs,Long statTime) {

    if (CollectionUtils.isEmpty(assistantProductRecordDTOs)) {
      return;
    }
//    Long statTime = DateUtil.get6clock(System.currentTimeMillis());

    Set<Long> itemIdSet = new HashSet<Long>();
    for (AssistantProductRecordDTO recordDTO : assistantProductRecordDTOs) {
      itemIdSet.add(recordDTO.getItemId());
    }
    if (CollectionUtil.isEmpty(itemIdSet)) {
      return;
    }

    List list = txnDaoManager.getWriter().getAssistantAchievementRecord(shopId, itemIdSet, AssistantRecordType.PRODUCT);
    Map<Long, AssistantProductRecord> recordMap = new HashMap<Long, AssistantProductRecord>();

    if (CollectionUtil.isNotEmpty(list)) {
      for (Object object : list) {
        AssistantProductRecord record = (AssistantProductRecord) object;
        recordMap.put(record.getItemId(), record);
      }
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (AssistantProductRecordDTO productRecord : assistantProductRecordDTOs) {
        AssistantProductRecord record = recordMap.get(productRecord.getItemId());
        if (record == null) {
          record = new AssistantProductRecord();
          record = record.fromDTO(productRecord);
          record.setStatTime(statTime);
          writer.save(record);
        } else {
          record = record.fromDTO(productRecord);
          record.setStatTime(statTime);
          writer.update(record);
        }
      }
      writer.commit(status);
    } finally {
      writer.commit(status);
    }
  }

  public void saveOrUpdateAssistantRepairProduct(Long shopId,List<AssistantProductRecordDTO> assistantProductRecordDTOs,Long statTime) {

    if (CollectionUtils.isEmpty(assistantProductRecordDTOs)) {
      return;
    }
//    Long statTime = DateUtil.get6clock(System.currentTimeMillis());

    Set<Long> itemIdSet = new HashSet<Long>();
    for (AssistantProductRecordDTO recordDTO : assistantProductRecordDTOs) {
      itemIdSet.add(recordDTO.getItemId());
    }
    if (CollectionUtil.isEmpty(itemIdSet)) {
      return;
    }

    List list = txnDaoManager.getWriter().getAssistantAchievementRecord(shopId, itemIdSet, AssistantRecordType.PRODUCT);
    Map<Long, List<AssistantProductRecord>> recordMap = new HashMap<Long, List<AssistantProductRecord>>();

    if (CollectionUtil.isNotEmpty(list)) {
      for (Object object : list) {
        AssistantProductRecord record = (AssistantProductRecord) object;
        List<AssistantProductRecord> recordList = recordMap.get(record.getItemId());

        if (CollectionUtil.isEmpty(recordList)) {
          recordList = new ArrayList<AssistantProductRecord>();
        }
        recordList.add(record);
        recordMap.put(record.getItemId(), recordList);
      }
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (AssistantProductRecordDTO productRecordDTO : assistantProductRecordDTOs) {
        List<AssistantProductRecord> recordList = recordMap.get(productRecordDTO.getItemId());
        if (CollectionUtil.isEmpty(recordList)) {
          AssistantProductRecord record = new AssistantProductRecord();
          record = record.fromDTO(productRecordDTO);
          record.setStatTime(statTime);
          writer.save(record);
          continue;
        }
        boolean update = false;
        for (AssistantProductRecord assistantProductRecord : recordList) {
          if (NumberUtil.longValue(assistantProductRecord.getAssistantId()) == NumberUtil.longValue(productRecordDTO.getAssistantId())) {
            assistantProductRecord = assistantProductRecord.fromDTO(productRecordDTO);
            assistantProductRecord.setStatTime(statTime);
            writer.update(assistantProductRecord);
            update = true;
          }
        }

        if (!update) {
          AssistantProductRecord record = new AssistantProductRecord();
          record = record.fromDTO(productRecordDTO);
          record.setStatTime(statTime);
          writer.save(record);
        }
      }
      writer.commit(status);
    } finally {
      writer.commit(status);
    }
  }


  public void saveOrUpdateAssistantMember(Long shopId,List<AssistantMemberRecordDTO> assistantMemberRecordDTOs,Long statTime) {


//    Long statTime = DateUtil.get6clock(System.currentTimeMillis());

    if (CollectionUtils.isEmpty(assistantMemberRecordDTOs)) {
      return;
    }

    Set<Long> orderIdSet = new HashSet<Long>();
    for (AssistantMemberRecordDTO recordDTO : assistantMemberRecordDTOs) {
      orderIdSet.add(recordDTO.getOrderId());
    }
    if (CollectionUtil.isEmpty(orderIdSet)) {
      return;
    }

    List list = txnDaoManager.getWriter().getAssistantAchievementRecord(shopId, orderIdSet, AssistantRecordType.MEMBER_NEW);
    Map<Long, AssistantMemberRecord> recordMap = new HashMap<Long, AssistantMemberRecord>();

    if (CollectionUtil.isNotEmpty(list)) {
      for (Object object : list) {
        AssistantMemberRecord record = (AssistantMemberRecord) object;
        recordMap.put(record.getOrderId(), record);
      }
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (AssistantMemberRecordDTO memberRecordDTO : assistantMemberRecordDTOs) {
        AssistantMemberRecord record = recordMap.get(memberRecordDTO.getOrderId());
        if (record == null) {
          record = new AssistantMemberRecord();
          record = record.fromDTO(memberRecordDTO);
          record.setStatTime(statTime);
          writer.save(record);
        } else {
          record = record.fromDTO(memberRecordDTO);
          record.setStatTime(statTime);
          writer.update(record);
        }
      }
      writer.commit(status);
    } finally {
      writer.commit(status);
    }
  }

  public void saveOrUpdateAssistantService(Long shopId,List<AssistantServiceRecordDTO> assistantServiceRecordDTOs,Long statTime) {

    if (CollectionUtils.isEmpty(assistantServiceRecordDTOs)) {
      return;
    }

//    Long statTime = DateUtil.get6clock(System.currentTimeMillis());

    Set<Long> itemIdSet = new HashSet<Long>();
    for (AssistantServiceRecordDTO recordDTO : assistantServiceRecordDTOs) {
      itemIdSet.add(recordDTO.getItemId());
    }
    if (CollectionUtil.isEmpty(itemIdSet)) {
      return;
    }

    List list = txnDaoManager.getWriter().getAssistantAchievementRecord(shopId, itemIdSet, AssistantRecordType.SERVICE);
    Map<Long, List<AssistantServiceRecord>> recordMap = new HashMap<Long, List<AssistantServiceRecord>>();

    if (CollectionUtil.isNotEmpty(list)) {
      for (Object object : list) {
        AssistantServiceRecord record = (AssistantServiceRecord) object;

        List<AssistantServiceRecord> recordList = recordMap.get(record.getItemId());
        if (CollectionUtil.isEmpty(recordList)) {
          recordList = new ArrayList<AssistantServiceRecord>();
        }
        recordList.add(record);
        recordMap.put(record.getItemId(), recordList);
      }
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (AssistantServiceRecordDTO serviceRecordDTO : assistantServiceRecordDTOs) {
        List<AssistantServiceRecord> serviceRecordList = recordMap.get(serviceRecordDTO.getItemId());

        if (CollectionUtil.isEmpty(serviceRecordList)) {
          AssistantServiceRecord record = new AssistantServiceRecord();
          record = record.fromDTO(serviceRecordDTO);
          record.setStatTime(statTime);
          writer.save(record);
          continue;
        }

        boolean update = false;
        for (AssistantServiceRecord serviceRecord : serviceRecordList) {
          if (NumberUtil.longValue(serviceRecord.getAssistantId()) == NumberUtil.longValue(serviceRecordDTO.getAssistantId())) {
            serviceRecord = serviceRecord.fromDTO(serviceRecordDTO);
            serviceRecord.setStatTime(statTime);
            writer.update(serviceRecord);
            update = true;
          }
        }

        if (!update) {
          AssistantServiceRecord record = new AssistantServiceRecord();
          record = record.fromDTO(serviceRecordDTO);
          record.setStatTime(statTime);
          writer.save(record);
        }
      }
      writer.commit(status);
    } finally {
      writer.commit(status);
    }
  }

  public void saveOrUpdateAssistantBusinessAccount(Long shopId,List<AssistantBusinessAccountRecordDTO> businessAccountRecordDTOs) {

    if (CollectionUtils.isEmpty(businessAccountRecordDTOs)) {
      return;
    }

    Long statTime = DateUtil.get6clock(System.currentTimeMillis());

    Set<Long> orderIdSet = new HashSet<Long>();
    for (AssistantBusinessAccountRecordDTO recordDTO : businessAccountRecordDTOs) {
      orderIdSet.add(recordDTO.getBusinessAccountId());
    }
    if (CollectionUtil.isEmpty(orderIdSet)) {
      return;
    }

    List list = txnDaoManager.getWriter().getAssistantAchievementRecord(shopId, orderIdSet, AssistantRecordType.BUSINESS_ACCOUNT);
    Map<Long, AssistantBusinessAccountRecord> recordMap = new HashMap<Long, AssistantBusinessAccountRecord>();

    if (CollectionUtil.isNotEmpty(list)) {
      for (Object object : list) {
        AssistantBusinessAccountRecord record = (AssistantBusinessAccountRecord) object;
        recordMap.put(record.getBusinessAccountId(), record);
      }
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (AssistantBusinessAccountRecordDTO recordDTO : businessAccountRecordDTOs) {
        AssistantBusinessAccountRecord record = recordMap.get(recordDTO.getBusinessAccountId());
        if (record == null) {
          record = new AssistantBusinessAccountRecord();
          record = record.fromDTO(recordDTO);
          record.setStatTime(statTime);
          writer.save(record);
        } else {
          record = record.fromDTO(recordDTO);
          record.setStatTime(statTime);
          writer.update(record);
        }
      }
      writer.commit(status);
    } finally {
      writer.commit(status);
    }
  }

//  public void saveOrUpdateAssistantRecord(List assistantRecordDTOList) {
//    if (CollectionUtils.isEmpty(assistantRecordDTOList)) {
//      return;
//    }
//
//    Long statTime = DateUtil.get6clock(System.currentTimeMillis());
//
//    TxnWriter writer = txnDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      for (Object object : assistantRecordDTOList) {
//        if (object instanceof AssistantServiceRecordDTO) {
//          AssistantServiceRecordDTO assistantServiceRecordDTO = (AssistantServiceRecordDTO) object;
//          AssistantServiceRecord assistantServiceRecord = null;
//          List list = writer.getAssistantRecord(assistantServiceRecordDTO.getShopId(), assistantServiceRecordDTO.getOrderId(), assistantServiceRecordDTO.getAssistantId(), assistantServiceRecordDTO.getItemId(), AssistantRecordType.SERVICE);
//          if (CollectionUtil.isNotEmpty(list)) {
//            if (list.size() > 1) {
//              LOG.error("shopId:" + assistantServiceRecordDTO.getShopId() + "orderId:" + assistantServiceRecordDTO.getOrderId() + "assistantId:" + assistantServiceRecordDTO.getAssistantId() + "has more  service record");
//            }
//            assistantServiceRecord = (AssistantServiceRecord) CollectionUtil.getFirst(list);
//            assistantServiceRecord = assistantServiceRecord.fromDTO(assistantServiceRecordDTO);
//            assistantServiceRecord.setStatTime(statTime);
//            writer.update(assistantServiceRecord);
//          } else {
//            assistantServiceRecord = new AssistantServiceRecord();
//            assistantServiceRecord = assistantServiceRecord.fromDTO(assistantServiceRecordDTO);
//            assistantServiceRecord.setStatTime(statTime);
//            writer.save(assistantServiceRecord);
//          }
//
//        } else if (object instanceof AssistantProductRecordDTO) {
//          AssistantProductRecordDTO assistantProductRecordDTO = (AssistantProductRecordDTO) object;
//          AssistantProductRecord assistantProductRecord = null;
//          List list = writer.getAssistantRecord(assistantProductRecordDTO.getShopId(), assistantProductRecordDTO.getOrderId(), assistantProductRecordDTO.getAssistantId(), assistantProductRecordDTO.getItemId(), AssistantRecordType.PRODUCT);
//          if (CollectionUtil.isNotEmpty(list)) {
//            if (list.size() > 1) {
//              LOG.error("shopId:" + assistantProductRecordDTO.getShopId() + "orderId:" + assistantProductRecordDTO.getOrderId() + "assistantId:" + assistantProductRecordDTO.getAssistantId() + "has more  productRecord");
//            }
//            assistantProductRecord = (AssistantProductRecord) CollectionUtil.getFirst(list);
//            assistantProductRecord = assistantProductRecord.fromDTO(assistantProductRecordDTO);
//            assistantProductRecord.setStatTime(statTime);
//            writer.update(assistantProductRecord);
//          } else {
//            assistantProductRecord = new AssistantProductRecord();
//            assistantProductRecord = assistantProductRecord.fromDTO(assistantProductRecordDTO);
//            assistantProductRecord.setStatTime(statTime);
//            writer.save(assistantProductRecord);
//          }
//        } else if (object instanceof AssistantMemberRecordDTO) {
//          AssistantMemberRecordDTO assistantMemberRecordDTO = (AssistantMemberRecordDTO) object;
//          AssistantMemberRecord assistantMemberRecord = null;
//          List list = writer.getAssistantRecord(assistantMemberRecordDTO.getShopId(), assistantMemberRecordDTO.getOrderId(), assistantMemberRecordDTO.getAssistantId(), null, AssistantRecordType.MEMBER_NEW);
//          if (CollectionUtil.isNotEmpty(list)) {
//            if (list.size() > 1) {
//              LOG.error("shopId:" + assistantMemberRecordDTO.getShopId() + "orderId:" + assistantMemberRecordDTO.getOrderId() + "assistantId:" + assistantMemberRecordDTO.getAssistantId() + "has more member record");
//            }
//            assistantMemberRecord = (AssistantMemberRecord) CollectionUtil.getFirst(list);
//            assistantMemberRecord = assistantMemberRecord.fromDTO(assistantMemberRecordDTO);
//            assistantMemberRecord.setStatTime(statTime);
//            writer.update(assistantMemberRecord);
//          } else {
//            assistantMemberRecord = new AssistantMemberRecord();
//            assistantMemberRecord = assistantMemberRecord.fromDTO(assistantMemberRecordDTO);
//            assistantMemberRecord.setStatTime(statTime);
//            writer.save(assistantMemberRecord);
//          }
//        } else if (object instanceof AssistantBusinessAccountRecordDTO) {
//          AssistantBusinessAccountRecordDTO recordDTO = (AssistantBusinessAccountRecordDTO) object;
//          AssistantBusinessAccountRecord record = null;
//          List list = writer.getAssistantRecord(recordDTO.getShopId(), recordDTO.getBusinessAccountId(), recordDTO.getAssistantId(), null, AssistantRecordType.BUSINESS_ACCOUNT);
//          if (CollectionUtil.isNotEmpty(list)) {
//            if (list.size() > 1) {
//              LOG.error("shopId:" + recordDTO.getShopId() + "orderId:" + recordDTO.getBusinessAccountId() + "assistantId:" + recordDTO.getAssistantId() + "has more business account record");
//            }
//            record = (AssistantBusinessAccountRecord) CollectionUtil.getFirst(list);
//            record = record.fromDTO(recordDTO);
//            record.setStatTime(statTime);
//            writer.update(record);
//          } else {
//            record = new AssistantBusinessAccountRecord();
//            record = record.fromDTO(recordDTO);
//            record.setStatTime(statTime);
//            writer.save(record);
//          }
//        }
//      }
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//  }


  public List<AssistantAchievementHistory> geAssistantAchievementHistory(Long shopId, Long assistantId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<AssistantAchievementHistory> assistantAchievementHistoryList = txnWriter.geAssistantAchievementHistory(shopId, assistantId);
    return assistantAchievementHistoryList;
  }


  public List<ServiceAchievementHistory> getServiceAchievementHistory(Long shopId, Long serviceId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ServiceAchievementHistory> serviceAchievementHistoryList = txnWriter.getServiceAchievementHistory(shopId, serviceId);
    return serviceAchievementHistoryList;
  }

  public List<ServiceAchievementHistory> getLastedServiceAchievementHistory(Long shopId, Long serviceId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ServiceAchievementHistory> serviceAchievementHistoryList = txnWriter.getLastedServiceAchievementHistory(shopId, serviceId);
    return serviceAchievementHistoryList;
  }

  public List<ProductAchievementHistory> getProductAchievementHistory(Long shopId, Long productId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ProductAchievementHistory> productAchievementHistoryList = txnWriter.getProductAchievementHistory(shopId, productId);
    return productAchievementHistoryList;
  }

  public List<MemberAchievementHistory> getMemberAchievementHistory(Long shopId, Long vestDate,MemberOrderType memberOrderType) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<MemberAchievementHistory> memberAchievementHistoryList = txnWriter.getMemberAchievementHistory(shopId, vestDate,memberOrderType);
    return memberAchievementHistoryList;
  }


  public ShopAchievementConfig getShopAchievementConfig(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ShopAchievementConfig> shopAchievementConfigList = txnWriter.getShopAchievementConfig(shopId, achievementRecordId, assistantRecordType, null);
    if (CollectionUtil.isEmpty(shopAchievementConfigList)) {
      return null;
    }
    if (shopAchievementConfigList.size() > 1) {
      LOG.error("AssistantStatService.getShopAchievementConfig,shopId:" + shopId +
          ",achievementRecordId:" + achievementRecordId + ",assistantRecordType:" + assistantRecordType);
    }
    return CollectionUtil.getFirst(shopAchievementConfigList);

  }

  public int countShopAchievementConfig(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.countShopAchievementConfig(shopId, achievementRecordId, assistantRecordType);
  }

  public List<Long> countAssistantStatByCondition(AssistantStatSearchDTO assistantStatSearchDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.countAssistantStatByCondition(assistantStatSearchDTO);
  }


  public List<AssistantAchievementStatDTO> getAssistantStatByIds(AssistantStatSearchDTO assistantStatSearchDTO, Set<Long> ids) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<AssistantAchievementStat> assistantAchievementStatList = txnWriter.getAssistantStatByIds(assistantStatSearchDTO, ids);

    if (CollectionUtils.isEmpty(assistantAchievementStatList)) {
      return null;
    }
    Map<Long, AssistantAchievementStatDTO> achievementStatDTOMap = new HashMap<Long, AssistantAchievementStatDTO>();

    List<AssistantAchievementStatDTO> assistantAchievementStatDTOList = new ArrayList<AssistantAchievementStatDTO>();

    Long key = null;
    for (AssistantAchievementStat assistantAchievementStat : assistantAchievementStatList) {
      AssistantAchievementStatDTO assistantAchievementStatDTO = null;
      if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
        key = assistantAchievementStat.getAssistantId();
        assistantAchievementStatDTO = achievementStatDTOMap.get(key);
      } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
        key = assistantAchievementStat.getDepartmentId();
        assistantAchievementStatDTO = achievementStatDTOMap.get(key);
      }
      if (assistantAchievementStatDTO == null) {
        achievementStatDTOMap.put(key, assistantAchievementStat.toDTO());
      } else {
        assistantAchievementStatDTO = assistantAchievementStatDTO.add(assistantAchievementStat.toDTO());
      }

    }

    for (AssistantAchievementStatDTO statDTO : achievementStatDTOMap.values()) {
      assistantAchievementStatDTOList.add(statDTO);
    }
    return assistantAchievementStatDTOList;
  }

  public List<ShopAchievementConfigDTO> getShopAchievementConfigByPager(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType, Pager pager) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ShopAchievementConfig> shopAchievementConfigList = txnWriter.getShopAchievementConfig(shopId, achievementRecordId, assistantRecordType, pager);

    List<ShopAchievementConfigDTO> shopAchievementConfigDTOList = new ArrayList<ShopAchievementConfigDTO>();
    if (CollectionUtils.isEmpty(shopAchievementConfigList)) {
      return shopAchievementConfigDTOList;

    }
    for (ShopAchievementConfig shopAchievementConfig : shopAchievementConfigList) {
      shopAchievementConfigDTOList.add(shopAchievementConfig.toDTO());
    }
    return shopAchievementConfigDTOList;
  }

  public ServiceAchievementHistoryDTO getServiceAchievementHistoryDTO(Long shopId, Long serviceId, Long vestDate) {
    List<ServiceAchievementHistory> serviceAchievementHistoryList = this.getServiceAchievementHistory(shopId, serviceId);
    ServiceAchievementHistoryDTO serviceAchievementHistoryDTO = null;

    if (CollectionUtils.isEmpty(serviceAchievementHistoryList)) {
      ServiceAchievementHistory serviceAchievementHistory = new ServiceAchievementHistory();
      TxnWriter txnWriter = txnDaoManager.getWriter();
      Service service = txnWriter.getById(Service.class, serviceId);
      if (service != null && service.getAchievementType() != null) {
        Object status = txnWriter.begin();
        try {
          serviceAchievementHistory.setShopId(shopId);
          serviceAchievementHistory.setServiceId(serviceId);
          serviceAchievementHistory.setServiceName(service.getName());
          serviceAchievementHistory.setAchievementType(service.getAchievementType());
          serviceAchievementHistory.setAchievementAmount(service.getAchievementAmount());
          serviceAchievementHistory.setStandardHours(service.getStandardHours());
          serviceAchievementHistory.setStandardUnitPrice(service.getStandardUnitPrice());
          serviceAchievementHistory.setChangeTime(System.currentTimeMillis());
          serviceAchievementHistory.setChangeUserId(null);
          txnWriter.save(serviceAchievementHistory);
          txnWriter.commit(status);
        } finally {
          txnWriter.rollback(status);
        }
      }
      return serviceAchievementHistory.toDTO();
    }
    if (serviceAchievementHistoryList.size() == 1) {
      serviceAchievementHistoryDTO = serviceAchievementHistoryList.get(0).toDTO();
      return serviceAchievementHistoryDTO;
    }


    for (ServiceAchievementHistory serviceAchievementHistory : serviceAchievementHistoryList) {

      if (NumberUtil.longValue(serviceAchievementHistory.getChangeTime()) <= vestDate) {
        serviceAchievementHistoryDTO = serviceAchievementHistory.toDTO();
      }
    }

    if (serviceAchievementHistoryDTO == null) {
      serviceAchievementHistoryDTO = CollectionUtil.getFirst(serviceAchievementHistoryList).toDTO();
    }

    return serviceAchievementHistoryDTO;
  }



  public AssistantAchievementHistoryDTO geAssistantAchievementHistoryByVestDate(Long shopId, Long assistantId, Long vestDate) {
    List<AssistantAchievementHistory> assistantAchievementHistoryList = this.geAssistantAchievementHistory(shopId, assistantId);
    AssistantAchievementHistoryDTO assistantAchievementHistoryDTO = null;

    if (CollectionUtils.isEmpty(assistantAchievementHistoryList)) {
      return assistantAchievementHistoryDTO;
    }
    if (assistantAchievementHistoryList.size() == 1) {
      assistantAchievementHistoryDTO = assistantAchievementHistoryList.get(0).toDTO();
      return assistantAchievementHistoryDTO;
    }


    for (AssistantAchievementHistory assistantAchievementHistory : assistantAchievementHistoryList) {

      if (NumberUtil.longValue(assistantAchievementHistory.getDepartmentChangeTime()) <= vestDate) {
        assistantAchievementHistoryDTO = assistantAchievementHistory.toDTO();
      }
    }

    if (assistantAchievementHistoryDTO == null) {
      assistantAchievementHistoryDTO = CollectionUtil.getFirst(assistantAchievementHistoryList).toDTO();
    }

    return assistantAchievementHistoryDTO;
  }

  public ProductAchievementHistoryDTO getProductAchievementHistoryDTO(Long shopId, Long productId, Long vestDate) {
    List<ProductAchievementHistory> productAchievementHistoryList = this.getProductAchievementHistory(shopId, productId);

    ProductAchievementHistoryDTO productAchievementHistoryDTO = null;

    if (CollectionUtils.isEmpty(productAchievementHistoryList)) {
      return productAchievementHistoryDTO;
    }
    if (productAchievementHistoryList.size() == 1) {
      productAchievementHistoryDTO = productAchievementHistoryList.get(0).toDTO();
      return productAchievementHistoryDTO;
    }


    for (ProductAchievementHistory productAchievementHistory : productAchievementHistoryList) {

      if (NumberUtil.longValue(productAchievementHistory.getChangeTime()) <= vestDate) {
        productAchievementHistoryDTO = productAchievementHistory.toDTO();
      }
    }

    if (productAchievementHistoryDTO == null) {
      productAchievementHistoryDTO = CollectionUtil.getFirst(productAchievementHistoryList).toDTO();
    }

    return productAchievementHistoryDTO;
  }

  public MemberAchievementHistoryDTO getMemberAchievementHistoryDTO(MemberOrderType memberOrderType, Long vestDate, List<MemberAchievementHistory> memberAchievementHistoryList) {

    MemberAchievementHistoryDTO memberAchievementHistoryDTO = null;

    if (CollectionUtils.isEmpty(memberAchievementHistoryList)) {
      return memberAchievementHistoryDTO;
    }
    if (memberAchievementHistoryList.size() == 1) {
      memberAchievementHistoryDTO = memberAchievementHistoryList.get(0).toDTO();

      if (memberOrderType != null && memberOrderType != memberAchievementHistoryDTO.getMemberOrderType()) {
        return null;
      }

      return memberAchievementHistoryDTO;
    }


    for (MemberAchievementHistory memberAchievementHistory : memberAchievementHistoryList) {

      if (memberOrderType != null && memberOrderType != memberAchievementHistory.getMemberOrderType()) {
        continue;
      }

      if (NumberUtil.longValue(memberAchievementHistory.getChangeTime()) <= vestDate) {
        memberAchievementHistoryDTO = memberAchievementHistory.toDTO();
        return memberAchievementHistoryDTO;
      }
    }

    if (memberAchievementHistoryDTO == null) {
      for (MemberAchievementHistory history : memberAchievementHistoryList) {
        if (memberOrderType == history.getMemberOrderType()) {
          return history.toDTO();
        }
      }
    }

    return memberAchievementHistoryDTO;
  }

  public void saveOrUpdateProductAchievement(Long shopId, AchievementType achievementType, Double achievementAmount, Long userId, Set<Long> productIdSet) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (CollectionUtils.isEmpty(productIdSet) || achievementType == null) {
      return;
    }

    Object status = txnWriter.begin();
    try {
      for (Long productId : productIdSet) {
        Inventory inventory = txnWriter.getById(Inventory.class, productId);

        if (NumberUtil.isEqual(achievementAmount, inventory.getSalesTotalAchievementAmount()) && achievementType == inventory.getSalesTotalAchievementType()) {
          continue;
        }
        inventory.setSalesTotalAchievementType(achievementType);
        inventory.setSalesTotalAchievementAmount(achievementAmount);

        txnWriter.update(inventory);

        ProductAchievementHistory productAchievementHistory = new ProductAchievementHistory();
        productAchievementHistory.setShopId(shopId);
        productAchievementHistory.setProductId(productId);
        productAchievementHistory.setSalesTotalAchievementType(achievementType);
        productAchievementHistory.setSalesTotalAchievementAmount(achievementAmount);
        productAchievementHistory.setChangeTime(System.currentTimeMillis());
        productAchievementHistory.setChangeUserId(userId);
        productAchievementHistory.setSalesProfitAchievementAmount(inventory.getSalesProfitAchievementAmount());
        productAchievementHistory.setSalesProfitAchievementType(inventory.getSalesProfitAchievementType());
        txnWriter.save(productAchievementHistory);

        deleteShopAchievementConfig(shopId, AssistantRecordType.PRODUCT, productId, txnWriter);
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }


  public void saveOrUpdateServiceAchievementHistory(Long shopId, AchievementType achievementType, Double achievementAmount, Long userId, Set<Long> serviceIdSet,Double standardHours,Double standardUnitPrice) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (CollectionUtils.isEmpty(serviceIdSet) || achievementType == null) {
      return;
    }


    Object status = txnWriter.begin();
    try {
      for (Long serviceId : serviceIdSet) {
        Service service = txnWriter.getById(Service.class, serviceId);

        service.setAchievementType(achievementType);
        service.setAchievementAmount(achievementAmount);
        if (standardHours != null) {
          service.setStandardHours(standardHours);
        }
        if (standardUnitPrice != null) {
          service.setStandardUnitPrice(standardUnitPrice);
        }


        txnWriter.update(service);

        ServiceAchievementHistory serviceAchievementHistory = new ServiceAchievementHistory();
        serviceAchievementHistory.setShopId(shopId);
        serviceAchievementHistory.setServiceId(serviceId);
        serviceAchievementHistory.setServiceName(service.getName());
        serviceAchievementHistory.setAchievementType(achievementType);
        serviceAchievementHistory.setAchievementAmount(achievementAmount);
        serviceAchievementHistory.setStandardHours(standardHours == null ? service.getStandardHours() : standardHours);
        serviceAchievementHistory.setStandardUnitPrice(standardUnitPrice == null ? service.getStandardUnitPrice() : standardUnitPrice);
        serviceAchievementHistory.setChangeTime(System.currentTimeMillis());
        serviceAchievementHistory.setChangeUserId(userId);

        txnWriter.save(serviceAchievementHistory);

        deleteShopAchievementConfig(shopId, AssistantRecordType.SERVICE, serviceId, txnWriter);

      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }


  public void deleteShopAchievementConfig(Long shopId, AssistantRecordType assistantRecordType, Long assistantRecordId, TxnWriter txnWriter) {
    if (assistantRecordType == null || assistantRecordId == null) {
      return;
    }
    List<ShopAchievementConfig> shopAchievementConfigList = txnWriter.getShopAchievementConfig(shopId, assistantRecordId, assistantRecordType, null);
    if (!CollectionUtils.isEmpty(shopAchievementConfigList)) {
      for (ShopAchievementConfig shopAchievementConfig : shopAchievementConfigList) {
        txnWriter.delete(ShopAchievementConfig.class, shopAchievementConfig.getId());
      }
    }
  }


  /**
   * 更新员工部门 保存部门记录
   *
   * @param shopId
   * @param salesManDTOList
   */
  public void updateSalesManDepartment(Long shopId, List<SalesManDTO> salesManDTOList, Long userId) throws Exception {

    if (CollectionUtil.isEmpty(salesManDTOList)) {
      return;
    }

    IUserService userService = ServiceManager.getService(IUserService.class);

    List<SalesManDTO> manDTOList = new ArrayList<SalesManDTO>();

    for (SalesManDTO salesManDTO : salesManDTOList) {
      SalesManDTO manDTO = userService.getSalesManDTOById(salesManDTO.getId());

      if (manDTO == null) {
        return;
      }

      AssistantAchievementHistoryDTO historyDTO = this.getLastedAssistantAchievementHistory(shopId, salesManDTO.getId(), System.currentTimeMillis());
      if (historyDTO != null && salesManDTO != null) {
        manDTO = manDTO.fromDTO(historyDTO);
      }


      if (salesManDTO.getDepartmentId() != null) {
        manDTO.setDepartmentName(salesManDTO.getDepartmentName());
        manDTO.setDepartmentId(salesManDTO.getDepartmentId());
      }
      userService.saveOrUpdateSalesMan(manDTO);
      manDTOList.add(manDTO);
    }

    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();

    try {
      for (SalesManDTO salesManDTO : manDTOList) {
        if (salesManDTO.getDepartmentId() == null) {
          continue;
        }

        AssistantAchievementHistory assistantAchievementHistory = new AssistantAchievementHistory();
        assistantAchievementHistory.setShopId(shopId);
        assistantAchievementHistory.setAssistantId(salesManDTO.getId());
        assistantAchievementHistory.setAssistantName(salesManDTO.getName());
        assistantAchievementHistory.setDepartmentId(salesManDTO.getDepartmentId());
        assistantAchievementHistory.setDepartmentName(salesManDTO.getDepartmentName());
        assistantAchievementHistory.setChangeUserId(userId);
        assistantAchievementHistory.setDepartmentChangeTime(System.currentTimeMillis());

        assistantAchievementHistory.setWashBeautyAchievement(salesManDTO.getWashBeautyAchievement());
        assistantAchievementHistory.setServiceAchievement(salesManDTO.getServiceAchievement());
        assistantAchievementHistory.setSalesAchievement(salesManDTO.getSalesAchievement());
        assistantAchievementHistory.setSalesProfitAchievement(salesManDTO.getSalesProfitAchievement());

        assistantAchievementHistory.setMemberNewType(salesManDTO.getMemberNewType());
        assistantAchievementHistory.setMemberNewAchievement(salesManDTO.getMemberNewAchievement());
        assistantAchievementHistory.setMemberRenewType(salesManDTO.getMemberRenewType());
        assistantAchievementHistory.setMemberReNewAchievement(salesManDTO.getMemberReNewAchievement());
        assistantAchievementHistory.setAchievementChangeTime(System.currentTimeMillis());


        txnWriter.save(assistantAchievementHistory);
        deleteShopAchievementConfig(shopId, AssistantRecordType.ASSISTANT_DEPARTMENT, salesManDTO.getId(), txnWriter);
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }

  }


  public int countAssistantRecordByCondition(AssistantStatSearchDTO assistantStatSearchDTO, Set<OrderTypes> orderTypes) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.countAssistantRecordByCondition(assistantStatSearchDTO, orderTypes);
  }


  public List getAssistantRecordByPager(AssistantStatSearchDTO assistantStatSearchDTO, Set<OrderTypes> orderTypes, Pager pager) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List list = txnWriter.getAssistantRecordByPager(assistantStatSearchDTO, orderTypes, pager);
    return list;
  }

  public void saveMemberAchievementHistory(MemberAchievementHistoryDTO memberAchievementHistoryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();

    try {
      MemberAchievementHistory memberAchievementHistory = new MemberAchievementHistory();
      memberAchievementHistory.fromDTO(memberAchievementHistoryDTO);
      txnWriter.save(memberAchievementHistory);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  public AssistantAchievementHistoryDTO getLastedAssistantAchievementHistory(Long shopId, Long assistantId,Long changeTime) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    AssistantAchievementHistory assistantAchievementHistory = txnWriter.getLastedAssistantAchievementHistory(shopId, assistantId,changeTime);
    return assistantAchievementHistory == null ? null : assistantAchievementHistory.toDTO();
  }


  /**
   * 更新员工业绩配置 保存员工业绩配置记录
   *
   * @param shopId
   * @param salesManDTOList
   */
  public void updateSalesManAchievement(Long shopId, List<SalesManDTO> salesManDTOList, Long userId) throws Exception {

    if (CollectionUtil.isEmpty(salesManDTOList)) {
      return;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();

    try {
      for (SalesManDTO salesManDTO : salesManDTOList) {

        AssistantAchievementHistory assistantAchievementHistory = new AssistantAchievementHistory();
        assistantAchievementHistory.setShopId(shopId);
        assistantAchievementHistory.setAssistantId(salesManDTO.getId());
        assistantAchievementHistory.setAssistantName(salesManDTO.getName());
        assistantAchievementHistory.setDepartmentId(salesManDTO.getDepartmentId());
        assistantAchievementHistory.setDepartmentName(salesManDTO.getDepartmentName());
        assistantAchievementHistory.setChangeUserId(userId);
        assistantAchievementHistory.setDepartmentChangeTime(System.currentTimeMillis());

        assistantAchievementHistory.setWashBeautyAchievement(salesManDTO.getWashBeautyAchievement());
        assistantAchievementHistory.setServiceAchievement(salesManDTO.getServiceAchievement());
        assistantAchievementHistory.setSalesAchievement(salesManDTO.getSalesAchievement());
        assistantAchievementHistory.setSalesProfitAchievement(salesManDTO.getSalesProfitAchievement());

        assistantAchievementHistory.setMemberNewType(salesManDTO.getMemberNewType());
        assistantAchievementHistory.setMemberNewAchievement(salesManDTO.getMemberNewAchievement());
        assistantAchievementHistory.setMemberRenewType(salesManDTO.getMemberRenewType());
        assistantAchievementHistory.setMemberReNewAchievement(salesManDTO.getMemberReNewAchievement());
        assistantAchievementHistory.setAchievementChangeTime(System.currentTimeMillis());

        txnWriter.save(assistantAchievementHistory);
        deleteShopAchievementConfig(shopId, AssistantRecordType.ASSISTANT_DEPARTMENT, salesManDTO.getId(), txnWriter);
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  /**
   * 更新员工的销售利润提成配置
   * @param shopId
   * @param salesProfitAchievementType
   * @param salesProfitAchievementAmount
   * @param userId
   * @param productIdSet
   */
  public void saveProductSalesProfitAchievement(Long shopId, AchievementType salesProfitAchievementType, Double salesProfitAchievementAmount, Long userId, Set<Long> productIdSet) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (CollectionUtils.isEmpty(productIdSet) || salesProfitAchievementType == null) {
      return;
    }

    Object status = txnWriter.begin();
    try {
      for (Long productId : productIdSet) {
        Inventory inventory = txnWriter.getById(Inventory.class, productId);

        if (NumberUtil.isEqual(salesProfitAchievementAmount, inventory.getSalesTotalAchievementAmount()) && salesProfitAchievementType == inventory.getSalesTotalAchievementType()) {
          continue;
        }
        inventory.setSalesProfitAchievementType(salesProfitAchievementType);
        inventory.setSalesProfitAchievementAmount(salesProfitAchievementAmount);

        txnWriter.update(inventory);

        ProductAchievementHistory productAchievementHistory = new ProductAchievementHistory();
        productAchievementHistory.setShopId(shopId);
        productAchievementHistory.setProductId(productId);
        productAchievementHistory.setSalesTotalAchievementType(inventory.getSalesTotalAchievementType());
        productAchievementHistory.setSalesTotalAchievementAmount(inventory.getSalesTotalAchievementAmount());
        productAchievementHistory.setChangeTime(System.currentTimeMillis());
        productAchievementHistory.setChangeUserId(userId);
        productAchievementHistory.setSalesProfitAchievementAmount(salesProfitAchievementAmount);
        productAchievementHistory.setSalesProfitAchievementType(salesProfitAchievementType);
        txnWriter.save(productAchievementHistory);

        deleteShopAchievementConfig(shopId, AssistantRecordType.PRODUCT, productId, txnWriter);
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }


  public List<ServiceDTO> getShopAllStatServiceByShopId(Long shopId) {
    List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();

    TxnWriter writer = txnDaoManager.getWriter();

    List<AssistantAchievementStat> achievementStats = writer.getShopAllStatServiceByShopId(shopId);
    if (CollectionUtil.isEmpty(achievementStats)) {
      return serviceDTOs;
    }

    for (AssistantAchievementStat assistantAchievementStat : achievementStats) {
      if (assistantAchievementStat.getServiceId() == null || StringUtil.isEmpty(assistantAchievementStat.getServiceName())) {
        continue;
      }
      ServiceDTO serviceDTO = new ServiceDTO();
      serviceDTO.setId(assistantAchievementStat.getServiceId());
      serviceDTO.setName(assistantAchievementStat.getServiceName());
      serviceDTOs.add(serviceDTO);
    }

    return serviceDTOs;

  }

  public List<AssistantAchievementStat> getAssistantAchievementStat(Long shopId,int statYear,int statMonth,AchievementStatType statType,Long assistantOrDepartmentId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getAssistantAchievementStat(shopId, statYear, statMonth, statType, assistantOrDepartmentId);
  }

  public void getAssistantOrDepartmentName(AssistantStatSearchDTO searchDTO) {
    try {
      if (searchDTO == null) {
        return;
      }

      if (NumberUtil.isLongNumber(searchDTO.getAssistantOrDepartmentIdStr())) {
        IUserService userService = ServiceManager.getService(IUserService.class);
        if (AchievementStatType.DEPARTMENT.name().equals(searchDTO.getAchievementStatTypeStr())) {
          DepartmentDTO departmentDTO = userService.getDepartmentById(Long.valueOf(searchDTO.getAssistantOrDepartmentIdStr()));
          if (departmentDTO != null) {
            searchDTO.setAssistantOrDepartmentName(departmentDTO.getName());
          }
        } else if (AchievementStatType.ASSISTANT.name().equals(searchDTO.getAchievementStatTypeStr())) {
          SalesManDTO salesManDTO = userService.getSalesManDTOById(Long.valueOf(searchDTO.getAssistantOrDepartmentIdStr()));
          if (salesManDTO != null) {
            searchDTO.setAssistantOrDepartmentName(salesManDTO.getName());
          }
        }
      } else {
        searchDTO.setAssistantOrDepartmentName("");
      }

      if (NumberUtil.isLongNumber(searchDTO.getServiceIdStr())) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        ServiceDTO serviceDTO = txnService.getServiceById(Long.valueOf(searchDTO.getServiceIdStr()));
        if (serviceDTO != null) {
          searchDTO.setServiceName(serviceDTO.getName());
        }
      } else {
        searchDTO.setServiceName("");
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }


}
