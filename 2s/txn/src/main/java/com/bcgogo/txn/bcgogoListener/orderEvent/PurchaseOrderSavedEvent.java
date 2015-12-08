package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.PurchaseOrderDTO;

/**
 * Created by IntelliJ IDEA.
 * User: LiuWei
 * Date: 12-4-12
 * Time: 下午1:23
 * To change this template use File | Settings | File Templates.
 */
  public class PurchaseOrderSavedEvent extends OrderSavedEvent {
    private PurchaseOrderDTO purchaseOrderDTO;
    public PurchaseOrderDTO getPurchaseOrderDTO() {
      return purchaseOrderDTO;
    }

    public void setPurchaseOrderDTO(PurchaseOrderDTO purchaseOrderDTO) {
      this.purchaseOrderDTO = purchaseOrderDTO;
    }

    public PurchaseOrderSavedEvent(PurchaseOrderDTO purchaseOrderDTO){
      super(purchaseOrderDTO);
      this.purchaseOrderDTO = purchaseOrderDTO;

    }

  }
