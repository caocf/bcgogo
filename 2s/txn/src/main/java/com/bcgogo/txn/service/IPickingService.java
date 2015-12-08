package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.RepairPickingItem;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-17
 * Time: 下午1:46
 * To change this template use File | Settings | File Templates.
 */
public interface IPickingService {

  List<RepairPickingDTO> getRepairPickingDTODetails(RepairPickingDTO repairPickingDTO) throws Exception;

  List<RepairPickingDTO> getRepairPickingDTODetails(Long shopId,Long ... ids) throws Exception;

  RepairPickingDTO getRepairPickDTODById(Long shopId,Long ids) throws Exception;

  int countRepairPickDTOs(RepairPickingDTO repairPickingDTO);

  Result verifyOutStorage(RepairPickingDTO repairPickingDTO);

  /**
   * @param repairPickingDTO  前台组装的
   * @param dbRepairPickingDTO
   * @return
   */
  Result verifyRepairPicking(RepairPickingDTO repairPickingDTO, RepairPickingDTO dbRepairPickingDTO) throws Exception;

  //出库，退料逻辑
  void handleRepairPicking(RepairPickingDTO repairPickingDTO) throws Exception;

  Map<Long,RepairPickingItem> getPendingRepairPickingItemMap(Long id);

  void createInnerPickingDTO(InnerPickingDTO innerPickingDTO, Set<Long> productIds) throws Exception;

  void createInnerReturnDTO(InnerReturnDTO innerReturnDTO, Set<Long> productIds) throws Exception;

  void saveInnerPicking(InnerPickingDTO innerPickingDTO) throws Exception;

  void saveInnerReturn(InnerReturnDTO innerReturnDTO) throws Exception;

  Result verifySaveInnerPicking(InnerPickingDTO innerPickingDTO) throws Exception;

  Result verifySaveInnerReturn(InnerReturnDTO innerReturnDTO) throws Exception;

  InnerPickingDTO getInnerPickingById(Long shopId, Long innerPickingId) throws Exception;

  InnerReturnDTO getInnerReturnById(Long shopId, Long innerReturnId) throws Exception;

  List<InnerPickingDTO> getInnerPickingDTOs(InnerPickingDTO innerPickingDTO);

  List<InnerReturnDTO> getInnerReturnDTOs(InnerReturnDTO innerReturnDTO);

  int countInnerPickingDTOs(InnerPickingDTO innerPickingDTO);

  int countInnerReturnDTOs(InnerReturnDTO innerReturnDTO);

  int sumInnerPickingDTOs(Long shopId);

  int sumInnerReturnDTOs(Long shopId);

  Map<Long,List<InnerPickingItemDTO>> getInnerPickingItemDTOs(Long ... ids);

  Map<Long, List<InnerReturnItemDTO>> getInnerReturnItemDTOs(Long... ids);

  void updateRepairPickingStatus(RepairPickingDTO repairPickingDTO)throws Exception;

  public boolean checkRepairPickingUsedInProcessingOrder(Long shopId) throws Exception;

  public boolean checkProcessingRepairOrderUseMaterialByShopId(Long shopId) throws Exception;

  RepairPickingDTO getRepairPickingDTOSimpleByRepairId(Long shopId, Long repairOrderId);

  Map<Long,RepairPickingDTO> getSimpleRepairPickingDTOsByRepairOrderIds(Long shopId, Long... repairOrderId);

  Result validatorLackProductTodo(Long shopId, Long orderId) throws Exception;

  //在开关打开的前提下，判断是否要创建新的维修领料
  boolean isNeedToCreateNewRepairPicking(RepairOrderDTO repairOrderDTO);

  //在开关打开的前提下，判断是否要更新维修领料
  boolean isNeedToUpdateRepairPicking(RepairOrderDTO repairOrderDTO);
}
