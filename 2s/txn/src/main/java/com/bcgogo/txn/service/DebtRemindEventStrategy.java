package com.bcgogo.txn.service;

import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.model.RemindEvent;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.CollectionUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-9
 * Time: 下午6:08
 * To change this template use File | Settings | File Templates.
 */
public class DebtRemindEventStrategy implements RemindEventStrategy{

  @Override
  public List<RemindEventDTO> queryRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime, Integer pageNo, Integer pageSize)  {
    TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    List<Object[]> objectsList = writer.queryDebtRemindEvent(shopId, isOverdue, hasRemind, flashTime, pageNo, pageSize);
    if(!CollectionUtil.isEmpty(objectsList)){
      for(int i=0;i<objectsList.size();i++){
        RemindEventDTO remindEventDTO = new RemindEventDTO();
        if(objectsList.get(i)[0] != null) {
          remindEventDTO.setCustomerId(((BigInteger)objectsList.get(i)[0]).longValue());
        }
        remindEventDTO.setRemindStatus(objectsList.get(i)[1].toString());
        remindEventDTO.setRemindTime(objectsList.get(i)[2]==null?null:((BigInteger)objectsList.get(i)[2]).longValue());
        //此处欠款为该客户的欠款总额，直接累计好传给前台显示
        remindEventDTO.setDebt((Double)objectsList.get(i)[3]);
        if(objectsList.get(i)[4] != null) {
          remindEventDTO.setOldRemindEventId(((BigInteger)objectsList.get(i)[4]).longValue());
        }
        remindEventDTO.setId(((BigInteger)objectsList.get(i)[5]).longValue());
        if(objectsList.get(i)[6] != null) {
          remindEventDTO.setSupplierId(((BigInteger)objectsList.get(i)[6]).longValue());
        }
        remindEventDTOList.add(remindEventDTO);
      }
    }
    return remindEventDTOList;
  }

  @Override
  public int countRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
    int count = writer.countDebtRemindEvent(shopId, isOverdue, hasRemind, flashTime);
    return count;
  }

  @Override
  public int RFCountRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
    List<Long> customerIds = writer.RFCountDebtRemindEvent(shopId, isOverdue, hasRemind, flashTime);
    if(customerIds == null) {
      return 0;
    }
    int count = customerIds.size();
    for(Long customerId : customerIds) {
      CustomerRecordDTO customerRecordDTO = ServiceManager.getService(ICustomerService.class).getCustomerRecordDTOByCustomerId(shopId, customerId);
      CustomerDTO customerDTO = ServiceManager.getService(ICustomerService.class).getCustomerById(customerId);
      double supplierReceivable = 0d;
      if(customerDTO.getSupplierId()!=null){
        SupplierRecordDTO supplierRecordDTO = ServiceManager.getService(ISupplierRecordService.class).getSupplierRecordDTOBySupplierId(shopId, customerDTO.getSupplierId());
        if(supplierRecordDTO!=null){
          supplierReceivable = supplierRecordDTO.getDebt();
        }
      }
      double customerReceivable = customerRecordDTO==null?0:customerRecordDTO.getTotalReceivable();
      Double totalArrears = customerReceivable + supplierReceivable;
      if(totalArrears == 0) {
         count--;
      }
    }
    return count;
  }
}
