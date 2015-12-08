package com.bcgogo.txn.service;

import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.RemindEvent;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-9
 * Time: 下午6:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RepairRemindEventStrategy implements RemindEventStrategy{

  @Override
  public List<RemindEventDTO> queryRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime, Integer pageNo, Integer pageSize) {
    TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    List<RemindEvent> remindEventList = writer.queryRepairRemindEvent(shopId, flashTime, pageNo, pageSize);
    if(!CollectionUtil.isEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
      }
    }
    return remindEventDTOList;
  }

  @Override
  public int countRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
    int count = writer.countRepairRemindEvent(shopId,null);
    return count;
  }

  @Override
  public int RFCountRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long deadline) {
    return 0;
  }
}
