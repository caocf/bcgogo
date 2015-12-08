package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.dto.SalesReturnDTO;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
public class SalesReturnSavedEvent extends OrderSavedEvent{

	private SalesReturnDTO salesReturnDTO;

	public SalesReturnSavedEvent(BcgogoOrderDto bcgogoOrderDto) {
		super(bcgogoOrderDto);

	}

	public SalesReturnSavedEvent(SalesReturnDTO salesReturnDTO) {
		super(salesReturnDTO);
		this.salesReturnDTO =  salesReturnDTO;
	}

	  public SalesReturnDTO getSalesReturnDTO() {
    return salesReturnDTO;
  }

  public void setSalesReturnDTO (SalesReturnDTO salesReturnDTO) {
    this.salesReturnDTO = salesReturnDTO;
  }

}
