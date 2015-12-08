package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.ServiceHistoryDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.model.ServiceHistory;
import com.bcgogo.txn.model.TxnWriter;

import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-13
 * Time: 下午3:41
 */
public interface IServiceHistoryService {

  void setServiceHistory(RepairOrderDTO repairOrderDTO);

  void setServiceHistory(WashBeautyOrderDTO washBeautyOrderDTO);

  ServiceHistory getOrSaveServiceHistoryByServiceId(Long serviceId, Long shopId);

  Map<Long,ServiceHistoryDTO> batchGetOrSaveServiceHistoryByServiceIds(TxnWriter writer, Long shopId,Set<Long> serviceIds);

  Map<Long,ServiceHistoryDTO> batchGetOrSaveServiceHistoryByServiceIds(Long shopId,Set<Long> serviceIds);

  ServiceHistory getServiceHistoryByIdAndVersion(Long serviceId, Long shopId, Long version);

  ServiceHistoryDTO getServiceHistoryById(Long serviceHistoryId, Long shopId);

  Map<Long,ServiceHistoryDTO> getServiceHistoryByServiceHistoryIdSet(Long shopId, Set<Long> serviceHistoryIds);

  boolean compareServiceSameWithHistory(Map<Long,Long> serviceInfoIdAndHistoryIdMap, Long shopId);

  boolean compareServiceSameWithHistory(Long serviceId, Long serviceHistoryId, Long shopId);
}
