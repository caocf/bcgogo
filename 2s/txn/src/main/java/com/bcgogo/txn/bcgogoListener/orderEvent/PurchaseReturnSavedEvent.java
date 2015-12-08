package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.dto.PurchaseReturnDTO;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-8-17
 * Time: 上午11:29
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseReturnSavedEvent  extends OrderSavedEvent{

	private PurchaseReturnDTO purchaseReturnDTO;

	public PurchaseReturnSavedEvent(BcgogoOrderDto bcgogoOrderDto) {
		super(bcgogoOrderDto);

	}

	public PurchaseReturnSavedEvent(PurchaseReturnDTO purchaseReturnDTO) {
		super(purchaseReturnDTO);
		this.purchaseReturnDTO =  purchaseReturnDTO;
	}

	  public PurchaseReturnDTO getPurchaseReturnDTO() {
    return purchaseReturnDTO;
  }

  public void setPurchaseReturnDTO (PurchaseReturnDTO purchaseReturnDTO) {
    this.purchaseReturnDTO = purchaseReturnDTO;
  }

}
