package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.dto.WashOrderDTO;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午5:10
 * To change this template use File | Settings | File Templates.
 */
public class WashOrderSavedEvent extends OrderSavedEvent{
  public WashOrderDTO getWashOrderDTO() {
    return washOrderDTO;
  }

  public void setWashOrderDTO(WashOrderDTO washOrderDTO) {
    this.washOrderDTO = washOrderDTO;
  }

  private WashOrderDTO washOrderDTO;
  private WashBeautyOrderDTO washBeautyOrderDTO;

  public WashBeautyOrderDTO getWashBeautyOrderDTO() {
    return washBeautyOrderDTO;
  }

  public void setWashBeautyOrderDTO(WashBeautyOrderDTO washBeautyOrderDTO) {
    this.washBeautyOrderDTO = washBeautyOrderDTO;
  }

  public WashOrderSavedEvent(WashBeautyOrderDTO washBeautyOrderDTO){
    super(washBeautyOrderDTO);
    this.washBeautyOrderDTO =washBeautyOrderDTO;
  }
  public WashOrderSavedEvent(WashOrderDTO washOrderDTO) {
		super(washOrderDTO);
    this.washOrderDTO =washOrderDTO;
	}
}
