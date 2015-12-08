package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.PurchaseInventoryDTO;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-12
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventorySavedEvent extends OrderSavedEvent{
  public PurchaseInventoryDTO getPurchaseInventoryDTO() {
    return purchaseInventoryDTO;
  }

  public void setPurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO) {
    this.purchaseInventoryDTO = purchaseInventoryDTO;
  }

  private PurchaseInventoryDTO purchaseInventoryDTO;

  public PurchaseInventorySavedEvent(PurchaseInventoryDTO purchaseInventoryDTO){
     super(purchaseInventoryDTO);
     this.purchaseInventoryDTO = purchaseInventoryDTO;
  }
}
