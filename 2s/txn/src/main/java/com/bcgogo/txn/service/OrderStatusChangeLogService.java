package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.OrderStatusChangeLogDTO;
import com.bcgogo.txn.model.OrderStatusChangeLog;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午5:20
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OrderStatusChangeLogService implements IOrderStatusChangeLogService {
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void saveOrderStatusChangeLog(OrderStatusChangeLogDTO orderStatusChangeLogDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    writer.save(new OrderStatusChangeLog(orderStatusChangeLogDTO));
  }
}
