package com.bcgogo.common;

import com.bcgogo.txn.dto.PurchaseOrderDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-8
 * Time: 上午9:59
 */
public class BcgogoOrderFormBean implements Serializable {
  private PurchaseOrderDTO[] purchaseOrderDTOs;

  public PurchaseOrderDTO[] getPurchaseOrderDTOs() {
    return purchaseOrderDTOs;
  }

  public void setPurchaseOrderDTOs(PurchaseOrderDTO[] purchaseOrderDTOs) {
    this.purchaseOrderDTOs = purchaseOrderDTOs;
  }
}
