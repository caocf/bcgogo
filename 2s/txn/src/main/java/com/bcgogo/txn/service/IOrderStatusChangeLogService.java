package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.OrderStatusChangeLogDTO;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
public interface IOrderStatusChangeLogService {
  public void saveOrderStatusChangeLog(OrderStatusChangeLogDTO orderStatusChangeLogDTO ) throws Exception;
}
