package com.bcgogo.notification.velocity;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderItemDTO;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.utils.DateUtil;

/**
 * User: xzhu
 * Date: 13-9-11
 * Time: 下午4:29
 */
public class PushMessageVelocityContext {
  private ShopDTO shopDTO;

  private String cancelMsg;//取消关联原因
  private String refuseMsg;//拒绝关联原因

  private ProductDTO productDTO;

  private PromotionsDTO promotionsDTO;

  private PreBuyOrderItemDTO preBuyOrderItemDTO;

  private QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO;

  private Integer pushCount;//推送的店铺数

  private String customerName;
  private String supplierName;

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public QuotedPreBuyOrderItemDTO getQuotedPreBuyOrderItemDTO() {
    return quotedPreBuyOrderItemDTO;
  }

  public void setQuotedPreBuyOrderItemDTO(QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO) {
    this.quotedPreBuyOrderItemDTO = quotedPreBuyOrderItemDTO;
  }

  public PromotionsDTO getPromotionsDTO() {
    return promotionsDTO;
  }

  public void setPromotionsDTO(PromotionsDTO promotionsDTO) {
    this.promotionsDTO = promotionsDTO;
  }

  public PreBuyOrderItemDTO getPreBuyOrderItemDTO() {
    return preBuyOrderItemDTO;
  }

  public void setPreBuyOrderItemDTO(PreBuyOrderItemDTO preBuyOrderItemDTO) {
    this.preBuyOrderItemDTO = preBuyOrderItemDTO;
  }

  public Integer getPushCount() {
    return pushCount;
  }

  public void setPushCount(Integer pushCount) {
    this.pushCount = pushCount;
  }


  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public ShopDTO getShopDTO() {
    return shopDTO;
  }

  public void setShopDTO(ShopDTO shopDTO) {
    this.shopDTO = shopDTO;
  }

  public String getCancelMsg() {
    return cancelMsg;
  }

  public void setCancelMsg(String cancelMsg) {
    this.cancelMsg = cancelMsg;
  }

  public String getRefuseMsg() {
    return refuseMsg;
  }

  public void setRefuseMsg(String refuseMsg) {
    this.refuseMsg = refuseMsg;
  }
}
