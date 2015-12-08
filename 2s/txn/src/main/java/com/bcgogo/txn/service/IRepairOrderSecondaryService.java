package com.bcgogo.txn.service;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.txn.dto.secondary.RepairOrderSecondaryCondition;
import com.bcgogo.txn.dto.secondary.RepairOrderSecondaryDTO;
import com.bcgogo.txn.dto.secondary.RepairOrderSecondaryResponse;

public interface IRepairOrderSecondaryService {
  public RepairOrderSecondaryDTO saveRepairOrderSecondary(RepairOrderSecondaryDTO repairOrderSecondaryDTO);

  public RepairOrderSecondaryDTO findRepairOrderSecondaryById(Long shopId, Long repairOrderSecondaryId) throws Exception;

  public int updateRepairOrderSecondaryOrderStatus(Long shopId, Long repairOrderSecondaryId, OrderStatus orderStatus);

  public RepairOrderSecondaryDTO findRepairOrderSecondaryByRepairOrderId(Long shopId, Long repairOrderId);

  public RepairOrderSecondaryResponse queryRepairOrderSecondary(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition);

  public int queryRepairOrderSecondarySize(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition);

  public RepairOrderSecondaryResponse statisticsRepairOrderSecondary(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition);
}
