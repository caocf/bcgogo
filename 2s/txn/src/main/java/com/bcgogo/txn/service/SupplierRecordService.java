package com.bcgogo.txn.service;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.SupplierRecord;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-9-12
 * Time: 下午1:51
 */
@Component
public class SupplierRecordService implements ISupplierRecordService {
  private static final Logger LOG = LoggerFactory.getLogger(SupplierRecordService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public SupplierRecordDTO saveSupplierRecord(SupplierRecordDTO supplierRecordDTO) {
    if (supplierRecordDTO == null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      SupplierRecord supplierRecord = new SupplierRecord();
      supplierRecord.fromDTO(supplierRecordDTO);
      writer.save(supplierRecord);
      writer.commit(status);
      supplierRecordDTO = supplierRecord.toDTO();
    } finally {
      writer.rollback(status);
    }
    return supplierRecordDTO;
  }

  @Override
  public SupplierRecordDTO saveOrUpdateSupplierRecord(SupplierRecordDTO supplierRecordDTO) {
    if (supplierRecordDTO == null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      SupplierRecord supplierRecord = writer.getSupplierRecord(supplierRecordDTO.getShopId(), supplierRecordDTO.getSupplierId());
      if (supplierRecord == null) {
        supplierRecord = new SupplierRecord();
        supplierRecord.fromDTO(supplierRecordDTO);
      } else {
        supplierRecord.setCreditAmount(NumberUtil.toReserve(supplierRecordDTO.getCreditAmount(),NumberUtil.MONEY_PRECISION));
        supplierRecord.setDebt(NumberUtil.toReserve(supplierRecordDTO.getDebt(),NumberUtil.MONEY_PRECISION));
      }
      writer.saveOrUpdate(supplierRecord);
      writer.commit(status);
      supplierRecordDTO = supplierRecord.toDTO();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
    return supplierRecordDTO;
  }

  @Override
  public SupplierRecord getSupplierRecordBySupplierId(Long shopId, Long supplierId) {
    if (shopId == null || supplierId == null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSupplierRecord(shopId, supplierId);
  }

  public SupplierRecordDTO getSupplierRecordDTOBySupplierId(Long shopId, Long supplierId) {
    if (shopId == null || supplierId == null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    SupplierRecord supplierRecord=writer.getSupplierRecord(shopId, supplierId);
    if(supplierRecord==null) return null;
    return supplierRecord.toDTO();
  }

  @Override
  public Map<Long, SupplierRecordDTO> getSupplierRecordDTOMapBySupplierId(Long shopId, List<Long> supplierIds) throws Exception{
    Map<Long, SupplierRecordDTO> map = new HashMap<Long, SupplierRecordDTO>();
    if(CollectionUtils.isEmpty(supplierIds)) return map;
    TxnWriter writer = txnDaoManager.getWriter();
    List<SupplierRecordDTO> supplierRecordDTOList = writer.getSupplierRecordForReindex(shopId, supplierIds);

    if(CollectionUtils.isEmpty(supplierRecordDTOList)) return map;
    for(SupplierRecordDTO supplierRecordDTO : supplierRecordDTOList){
      map.put(supplierRecordDTO.getSupplierId(), supplierRecordDTO);
    }
    return map;
  }
  @Override
  public SupplierRecordDTO updateSupplierRecordCreditAmount(Long shopId, SupplierDTO supplierDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    SupplierRecord supplierRecord = writer.getSupplierRecord(shopId, supplierDTO.getId());
    if(supplierRecord == null){
      createSupplierRecordUsingSupplierDTO(supplierDTO);
      supplierRecord = writer.getSupplierRecord(shopId, supplierDTO.getId());
    }
    List<Double> doubleList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
    if(CollectionUtil.isNotEmpty(doubleList))
      supplierRecord.setCreditAmount(NumberUtil.toReserve(doubleList.get(0),2));

    Object status = writer.begin();
    try{
      writer.saveOrUpdate(supplierRecord);
      writer.commit(status);
    } catch (Exception e){
      LOG.error(e.getMessage(), e);
    } finally{
      writer.rollback(status);
    }
    return supplierRecord.toDTO();
  }

    @Override
    public SupplierRecordDTO updateSupplierRecordDebt(Long shopId, SupplierDTO supplierDTO) {
        TxnWriter writer = txnDaoManager.getWriter();
        SupplierRecord supplierRecord = writer.getSupplierRecord(shopId, supplierDTO.getId());
        if(supplierRecord == null){
            createSupplierRecordUsingSupplierDTO(supplierDTO);
            supplierRecord = writer.getSupplierRecord(shopId, supplierDTO.getId());
        }
        List<Double> doubleList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        if(CollectionUtil.isNotEmpty(doubleList))
            supplierRecord.setDebt(Math.abs(NumberUtil.toReserve(doubleList.get(0),2)));

        Object status = writer.begin();
        try{
            writer.saveOrUpdate(supplierRecord);
            writer.commit(status);
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
        } finally{
            writer.rollback(status);
        }
        return supplierRecord.toDTO();
    }

  @Override
  public SupplierRecordDTO createSupplierRecordUsingSupplierDTO(SupplierDTO supplierDTO) {
    SupplierRecordDTO supplierRecordDTO = new SupplierRecordDTO();
    supplierRecordDTO.setShopId(supplierDTO.getShopId());
    supplierRecordDTO.setSupplierId(supplierDTO.getId());
    supplierRecordDTO.setCreditAmount(0d);
    return saveOrUpdateSupplierRecord(supplierRecordDTO);
  }
}
