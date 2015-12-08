package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.AllocateRecordDTO;
import com.bcgogo.txn.dto.AllocateRecordSearchConditionDTO;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-8
 * Time: 上午5:15
 * To change this template use File | Settings | File Templates.
 */
public interface IAllocateRecordService {

  public List<AllocateRecordDTO> searchAllocateRecords(AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) throws Exception;

  public int countAllocateRecords(AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) throws Exception;

  /**
   * 调拨单的总 库存不用修改
   * @param shopId
   * @param allocateRecordDTO
   * @throws Exception
   */
  public void saveOrUpdateAllocateRecord(Long shopId,AllocateRecordDTO allocateRecordDTO) throws Exception;

  public void saveOrUpdateAllocateRecord(TxnWriter writer, Long shopId,AllocateRecordDTO allocateRecordDTO) throws Exception;

  public AllocateRecordDTO getAllocateRecordDTOById(Long shopId, Long id) throws Exception;

}
