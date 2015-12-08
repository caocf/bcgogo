package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.event.BcgogoEvent;
import com.bcgogo.txn.dto.BcgogoOrderDto;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午4:53
 * To change this template use File | Settings | File Templates.
 */
public class OrderSavedEvent extends BcgogoEvent {
  private boolean orderFlag = false, mainFlag = false;

  public OrderSavedEvent(BcgogoOrderDto bcgogoOrderDto) {
    super(bcgogoOrderDto);
  }

  public boolean isOrderFlag() {
    return orderFlag;
  }

  public void setOrderFlag(boolean orderFlag) {
    this.orderFlag = orderFlag;
  }

  public boolean isMainFlag() {
    return mainFlag;
  }

  public void setMainFlag(boolean mainFlag) {
    this.mainFlag = mainFlag;
  }

  public boolean mockFlag() {
    return !(orderFlag && mainFlag);
  }

}
