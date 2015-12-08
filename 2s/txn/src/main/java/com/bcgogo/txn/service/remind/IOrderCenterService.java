package com.bcgogo.txn.service.remind;

import com.bcgogo.txn.dto.remind.OrderCenterDTO;

/**
 * User: ZhangJuntao
 * Date: 13-6-18
 * Time: 下午1:51
 */
public interface IOrderCenterService {
  OrderCenterDTO getOrderCenterSaleAndSaleReturnNewStatistics(Long shopId);


  OrderCenterDTO getOrderCenterPurchaseSellerStatistics(Long shopId);

  OrderCenterDTO getOrderCenterStatistics(Long shopId);

}
