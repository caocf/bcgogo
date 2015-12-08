package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.RepairOrderDTO;

/**
 * Created by IntelliJ IDEA.
 * User: LiuWei
 * Date: 12-4-11
 * Time: 下午5:05
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderSavedEvent extends OrderSavedEvent {
  private RepairOrderDTO repairOrderDTO;

  public RepairOrderDTO getRepairOrderDTO() {
    return repairOrderDTO;
  }
  public void setRepairOrderDTO(RepairOrderDTO repairOrderDTO) {
    this.repairOrderDTO = repairOrderDTO;
  }
  public RepairOrderSavedEvent(RepairOrderDTO repairOrderDTO){
    super(repairOrderDTO);
    this.repairOrderDTO = repairOrderDTO;
  }



}
