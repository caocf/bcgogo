package com.bcgogo.txn.service;

import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.model.SupplierRecord;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-9-12
 * Time: 下午1:51
 */
public interface ISupplierRecordService {

  SupplierRecordDTO saveSupplierRecord(SupplierRecordDTO supplierRecordDTO);

  SupplierRecordDTO saveOrUpdateSupplierRecord(SupplierRecordDTO supplierRecordDTO);

  SupplierRecord getSupplierRecordBySupplierId(Long shopId, Long supplierId);

  SupplierRecordDTO getSupplierRecordDTOBySupplierId(Long shopId, Long supplierId);

  SupplierRecordDTO updateSupplierRecordCreditAmount(Long shopId, SupplierDTO supplierDTO);

  SupplierRecordDTO updateSupplierRecordDebt(Long shopId, SupplierDTO supplierDTO);

  SupplierRecordDTO createSupplierRecordUsingSupplierDTO(SupplierDTO supplierDTO);

  Map<Long, SupplierRecordDTO> getSupplierRecordDTOMapBySupplierId(Long shopId, List<Long> supplierIds) throws Exception;
}
