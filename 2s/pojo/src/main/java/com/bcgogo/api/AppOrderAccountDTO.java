package com.bcgogo.api;

import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 单据结算信息
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public class AppOrderAccountDTO implements Serializable {


  private Double totalAmount;//单据总额       double
  private Double settledAmount;//实收         double
  private Double discount;//优惠              double
  private Double debt;//挂账                  double

  public AppOrderAccountDTO(){

  }

  public AppOrderAccountDTO(ReceivableDTO receivableDTO) {
    this.setTotalAmount(receivableDTO.getTotal());
    this.setSettledAmount(NumberUtil.doubleVal(receivableDTO.getSettledAmount()));
    this.setDebt(NumberUtil.doubleVal(receivableDTO.getDebt()));
    this.setDiscount(NumberUtil.doubleVal(receivableDTO.getDiscount()));
  }


  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }
}
