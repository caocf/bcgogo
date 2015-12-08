package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "payment_record")
public class PaymentRecord extends LongIdentifier {
  public PaymentRecord()
  {
  }

  @Column(name = "payable_id")
  public Long getPayableId() {
    return payableId;
  }

  public void setPayableId(Long payableId) {
    this.payableId = payableId;
  }

 @Column(name = "payable_no", length = 20)
  public String getPayableNo() {
    return payableNo;
  }

  public void setPayableNo(String payableNo) {
    this.payableNo = payableNo;
  }

  @Column(name = "amount")
   public double getAmount() {
     return amount;
   }

   public void setAmount(double amount) {
     this.amount = amount;
   }

  @Column(name = "payer_id")
  public Long getPayerId() {
    return payerId;
  }

  public void setPayerId(Long payerId) {
    this.payerId = payerId;
  }

 @Transient
  public String getPayer() {
    return payer;
  }

  private void setPayer(String payer) {
    this.payer = payer;
  }

  @Column(name = "pay_time")
   public Long getPayTime() {
     return payTime;
   }

   public void setPayTime(Long payTime) {
     this.payTime = payTime;
   }

  private Long payableId;
  private String payableNo;
  private double amount;
  private Long payerId;
  private String payer;        //瞬态字段
  private Long payTime;


  /**
   * 根据XXId找到相关信息并设置到XX字段.
   */
  @Override
  public void onLoad(Session s, Serializable id) {
//    if(payerId!=null){
//      setLastPayer(ServiceManager.getService(IUserService.class).getNameByUserId(payerId));
//    }
  }
}
