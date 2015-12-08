package com.bcgogo.txn.dto;

import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

/**
 * PurchaseOrder, SalesOrderDTO中的promotionsJsonInfo parse为此对象
 * User: Jimuchen
 * Date: 13-7-22
 * Time: 下午8:04
 */
public class PromotionsInfoDTO {
  private String BARGAIN;
  private String MJS;
  private String MLJ;
  private String SPECIAL_CUSTOMER;
  private String FREE_SHIPPING;

  public String getBARGAIN() {
    if(NumberUtil.isNumber(this.BARGAIN)){
      return StringUtil.valueOf(NumberUtil.round(this.BARGAIN));
    }
    return BARGAIN;
  }

  public void setBARGAIN(String BARGAIN) {
    this.BARGAIN = BARGAIN;
  }

  public String getMJS() {
    return MJS;
  }

  public void setMJS(String MJS) {
    this.MJS = MJS;
  }

  public String getMLJ() {
    if(NumberUtil.isNumber(this.MLJ)){
      return StringUtil.valueOf(NumberUtil.round(this.MLJ));
    }
    return MLJ;
  }

  public void setMLJ(String MLJ) {
    this.MLJ = MLJ;
  }

  public String getSPECIAL_CUSTOMER() {
    return SPECIAL_CUSTOMER;
  }

  public void setSPECIAL_CUSTOMER(String SPECIAL_CUSTOMER) {
    this.SPECIAL_CUSTOMER = SPECIAL_CUSTOMER;
  }

  public String getFREE_SHIPPING() {
    return FREE_SHIPPING;
  }

  public void setFREE_SHIPPING(String FREE_SHIPPING) {
    this.FREE_SHIPPING = FREE_SHIPPING;
  }

  public double getPromotionsTotal(){
    double total = 0d;
    if(NumberUtil.isNumber(getMLJ())){
      total += Double.parseDouble(getMLJ());
    }
    if(NumberUtil.isNumber(getBARGAIN())){
      total += Double.parseDouble(getBARGAIN());
    }
    return NumberUtil.round(total);
  }
}
