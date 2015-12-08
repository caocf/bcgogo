package com.bcgogo.txn.dto;

import com.bcgogo.api.AppOrderItemDTO;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.utils.NumberUtil;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-27
 * Time: 下午5:08
 */
public class AppointOrderMaterialDTO extends BcgogoOrderItemDto {
  private Long shopId;
  private Long appointOrderId;
  private Double price;
  private Double total;
  private ObjectStatus status;

  public AppOrderItemDTO toAppOrderItemDTO() {
    AppOrderItemDTO appOrderItemDTO = new AppOrderItemDTO();
    appOrderItemDTO.setContent(this.getProductName() + "*" + NumberUtil.doubleVal(this.getAmount()));
    appOrderItemDTO.setType(AppOrderItemDTO.itemTypeProduct);
    appOrderItemDTO.setAmount(this.getTotal());
    return  appOrderItemDTO;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getAmount() {
     return amount;
   }

   public void setAmount(Double amount) {
     this.amount = NumberUtil.round(amount, 2);
   }

  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }


}
