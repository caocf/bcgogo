package com.bcgogo.txn.service;

import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.RemindEvent;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-9
 * Time: 下午6:26
 * To change this template use File | Settings | File Templates.
 */
public class CustomerServiceRemindEventStrategy implements RemindEventStrategy{
  private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceRemindEventStrategy.class);
  @Override
  public List<RemindEventDTO> queryRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime, Integer pageNo, Integer pageSize) {
    TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    List<RemindEvent> remindEventList = writer.queryCustomerRemindEvent(shopId, isOverdue, hasRemind, flashTime,pageNo, pageSize);
    Set<Long> customerIds = new HashSet<Long>();
    if(!CollectionUtil.isEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
        customerIds.add(remindEvent.getCustomerId());
      }
    }
    Map<Long, CustomerDTO> customerMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(shopId, customerIds);
    for(RemindEventDTO remindEventDTO : remindEventDTOList){
      if(customerMap.get(remindEventDTO.getCustomerId())!=null){
        remindEventDTO.setCustomerName(customerMap.get(remindEventDTO.getCustomerId()).getName() );
        remindEventDTO.setMobile(customerMap.get(remindEventDTO.getCustomerId()).getMobile());
      }
    }
    return remindEventDTOList;
  }

  @Override
  public int countRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
    int count = writer.countCustomerRemindEvent(shopId, isOverdue, hasRemind, flashTime);
    return count;
  }

  @Override
  public int RFCountRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long deadline) {
    return 0;
  }
}
