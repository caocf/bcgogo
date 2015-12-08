package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.SalesOrderDTO;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午4:54
 * To change this template use File | Settings | File Templates.
 * Comment:
 */
public class SaleOrderSavedEvent extends OrderSavedEvent{
  public SalesOrderDTO getSalesOrderDTO() {
    return salesOrderDTO;
  }

  public void setSalesOrderDTO(SalesOrderDTO salesOrderDTO) {
    this.salesOrderDTO = salesOrderDTO;
  }

  private SalesOrderDTO salesOrderDTO;

  public SaleOrderSavedEvent(SalesOrderDTO salesOrderDTO) {
		super(salesOrderDTO);
    this.salesOrderDTO =salesOrderDTO;
	}
}
