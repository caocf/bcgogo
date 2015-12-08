package com.bcgogo.txn.service;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-13
 * Time: 下午3:41
 */
@Component
public class ServiceHistoryService implements IServiceHistoryService{
  @Autowired
  private TxnDaoManager txnDaoManager;


  public void setServiceHistory(RepairOrderDTO repairOrderDTO){
    RepairOrderServiceDTO[] serviceDTOs=repairOrderDTO.getServiceDTOs();
    Set<Long> serviceHistoryIds = new HashSet<Long>();
    for (RepairOrderServiceDTO serviceDTO : serviceDTOs) {
      serviceHistoryIds.add(serviceDTO.getServiceHistoryId());
    }
    //默认的2个service的shopId 为1  所以不能传shopId
    Map<Long, ServiceHistoryDTO> serviceHistoryMap = getServiceHistoryByServiceHistoryIdSet(null,serviceHistoryIds);
    for (RepairOrderServiceDTO serviceDTO : serviceDTOs) {
      ServiceHistoryDTO serviceHistory = serviceHistoryMap.get(serviceDTO.getServiceHistoryId());
      if (serviceHistory != null) {
        serviceDTO.setService(serviceHistory.getName());
      }
    }
  }

  public void setServiceHistory(WashBeautyOrderDTO washBeautyOrderDTO){
    WashBeautyOrderItemDTO[] itemDTOs=washBeautyOrderDTO.getWashBeautyOrderItemDTOs();
    Set<Long> serviceHistoryIds = new HashSet<Long>();
    for (WashBeautyOrderItemDTO itemDTO : itemDTOs) {
      serviceHistoryIds.add(itemDTO.getServiceHistoryId());
    }
    Map<Long, ServiceHistoryDTO> serviceHistoryMap = getServiceHistoryByServiceHistoryIdSet(null,serviceHistoryIds);
    for (WashBeautyOrderItemDTO itemDTO:itemDTOs) {
      ServiceHistoryDTO serviceHistory = serviceHistoryMap.get(itemDTO.getServiceHistoryId());
      if (serviceHistory== null) {
        continue;
      }
      itemDTO.setServiceHistoryDTO(serviceHistory);
    }
  }

  /**
   * 传入Service的Id， 返回相应的serviceHistory.  不存在则新建，存在则返回。
   * @param serviceId
   * @param shopId
   * @return
   */
  @Override
  public ServiceHistory getOrSaveServiceHistoryByServiceId(Long serviceId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Service service = writer.getServiceById(shopId, serviceId);
    if(service == null){
      return null;
    }

    ServiceDTO serviceDTO = service.toDTO();
    ServiceHistory serviceHistoryExist = getServiceHistoryByIdAndVersion(service.getId(), service.getShopId(), serviceDTO.getVersion());
    if(serviceHistoryExist != null){
      return serviceHistoryExist;
    }else{
      ServiceHistory serviceHistory = new ServiceHistory();
      serviceHistory.setServiceDTO(serviceDTO);
      writer.save(serviceHistory);
      return serviceHistory;
    }
  }

  /**
   * 批量保存获取serviceHistory
   * @param shopId
   * @param serviceIds
   * @return     key是serviceId
   */
  @Override
  public Map<Long, ServiceHistoryDTO> batchGetOrSaveServiceHistoryByServiceIds(TxnWriter writer, Long shopId, Set<Long> serviceIds) {
    if (shopId == null || CollectionUtils.isEmpty(serviceIds)) {
      return new HashMap<Long, ServiceHistoryDTO>();
    }
    Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = new HashMap<Long, ServiceHistoryDTO>();
    Map<Long, ServiceDTO> serviceDTOMap = writer.getServiceByServiceIdSet(shopId, serviceIds);
    List<ServiceHistory> serviceHistoryList = writer.getLastServiceHistories(shopId, serviceIds);

    if (CollectionUtils.isNotEmpty(serviceHistoryList)) {
      for (ServiceHistory serviceHistory : serviceHistoryList) {
        if(serviceHistory != null && serviceHistory.getId() != null) {
          serviceHistoryDTOMap.put(serviceHistory.getServiceId(), serviceHistory.toDTO());
        }
      }
    }
    for (Long serviceId : serviceIds) {
      if (serviceHistoryDTOMap.get(serviceId) == null) {
        ServiceDTO serviceDTO = serviceDTOMap.get(serviceId);
        if (serviceDTO != null) {
          ServiceHistory serviceHistory = new ServiceHistory();
          serviceHistory.setServiceDTO(serviceDTO);
          writer.save(serviceHistory);
          serviceHistoryDTOMap.put(serviceId, serviceHistory.toDTO());
        }
      }
    }

    return serviceHistoryDTOMap;
  }

  /**
   * 批量保存获取serviceHistory
   * @param shopId
   * @param serviceIds
   * @return     key是serviceId
   */
  @Override
  public Map<Long, ServiceHistoryDTO> batchGetOrSaveServiceHistoryByServiceIds(Long shopId, Set<Long> serviceIds) {
    if (shopId == null || CollectionUtils.isEmpty(serviceIds)) {
      return new HashMap<Long, ServiceHistoryDTO>();
    }
    Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = new HashMap<Long, ServiceHistoryDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      serviceHistoryDTOMap = batchGetOrSaveServiceHistoryByServiceIds(writer,shopId,serviceIds);
    } finally {
      writer.rollback(status);
    }
    return serviceHistoryDTOMap;
  }





  @Override
  public ServiceHistory getServiceHistoryByIdAndVersion(Long serviceId, Long shopId, Long version) {
    TxnWriter writer = txnDaoManager.getWriter();
    ServiceHistory serviceHistory = writer.getServiceHistoryByIdAndVersion(serviceId, shopId, version);
    return serviceHistory;
  }

  @Override
  public ServiceHistoryDTO getServiceHistoryById(Long id, Long shopId) {
    if(id == null || shopId  ==null){
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    ServiceHistory serviceHistory = writer.getServiceHistoryById(id, shopId);
    return serviceHistory==null? null : serviceHistory.toDTO();
  }

  @Override
  public Map<Long, ServiceHistoryDTO> getServiceHistoryByServiceHistoryIdSet(Long shopId, Set<Long> serviceHistoryIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    if(CollectionUtils.isEmpty(serviceHistoryIds)) return new HashMap<Long, ServiceHistoryDTO>();
    List<ServiceHistory> serviceHistoryList = writer.getServiceHistoryByServiceHistoryIdSet(shopId, serviceHistoryIds);
    if (CollectionUtils.isEmpty(serviceHistoryList)) return new HashMap<Long, ServiceHistoryDTO>();
    Map<Long, ServiceHistoryDTO> map = new HashMap<Long, ServiceHistoryDTO>();
    for (ServiceHistory serviceHistory : serviceHistoryList) {
      map.put(serviceHistory.getId(), serviceHistory.toDTO());
    }
    return map;
  }

  @Override
  public boolean compareServiceSameWithHistory(Map<Long, Long> serviceInfoIdAndHistoryIdMap, Long shopId) {
    if(serviceInfoIdAndHistoryIdMap==null || serviceInfoIdAndHistoryIdMap.isEmpty()){
      return true;
    }
    for(Map.Entry<Long, Long> entry : serviceInfoIdAndHistoryIdMap.entrySet()){
      Long serviceId = entry.getKey();
      Long serviceHistoryId = entry.getValue();
      if(serviceId == null || serviceHistoryId == null){
        continue;
      }
      if(!compareServiceSameWithHistory(serviceId, serviceHistoryId, shopId)){
        return false;
      }
    }
    return true;
  }

  public boolean compareServiceSameWithHistory(Long serviceId, Long serviceHistoryId, Long shopId) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Service service = txnService.getServiceById(shopId, serviceId);
    ServiceHistoryDTO serviceHistoryDTO = getServiceHistoryById(serviceHistoryId, shopId);
    if(service == null || serviceHistoryDTO == null){
      return false;
    }
    ServiceDTO serviceDTO = service.toDTO();
    return serviceHistoryDTO.compareSame(serviceDTO);
  }
}
