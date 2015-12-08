package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderItemDTO;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-6-18
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
public interface IWashBeautyService {

  void initWashBeautyService(TxnWriter writer,WashBeautyOrderDTO washBeautyOrderDTO)throws Exception;

  public void setServiceWorks(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception;

  public WashBeautyOrderDTO saveWashBeautyOrder(Long shopId, Long userId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception;

  public void accountMemberWithWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO)throws Exception;

  WashBeautyOrderDTO accountMemberWithWashBeauty_camera(WashBeautyOrderDTO washBeautyOrderDTO,String[] serviceIds)throws Exception;

  Map<Long,List<WashBeautyOrderItemDTO>> getWashBeautyOrderItemDTOMap(Set<Long> washBeautyOrderIds);

  public void updateConsumingRecordFromRepairOrder(WashBeautyOrderDTO washBeautyOrderDTO);
}
