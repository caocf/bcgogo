package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.BcgogoOrderDto;

/**
 * 用作一些特殊单据reindex使用（不计入营业统计和流水统计的单据）
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-7
 * Time: 上午10:53
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoOrderReindexEvent extends OrderSavedEvent {
  private BcgogoOrderDto bcgogoOrderDto;
  private OrderTypes orderType;

  public BcgogoOrderReindexEvent(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType) {
    super(bcgogoOrderDto);
    this.bcgogoOrderDto = bcgogoOrderDto;
    this.orderType = orderType;
  }

  public BcgogoOrderDto getBcgogoOrderDto() {
    return bcgogoOrderDto;
  }

  public void setBcgogoOrderDto(BcgogoOrderDto bcgogoOrderDto) {
    this.bcgogoOrderDto = bcgogoOrderDto;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }
}
