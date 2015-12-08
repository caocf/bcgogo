package com.bcgogo.txn.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-30
 * Time: 下午5:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "preferential_policy")
public class PreferentialPolicy extends LongIdentifier implements Comparable{
  private Double rechargeAmount;
  private Double presentAmount;
  private DeletedType deletedType;

  public PreferentialPolicy() {

  }

  @Column(name = "present_amount")
  public Double getPresentAmount() {
    return presentAmount;
  }

  public void setPresentAmount(Double presentAmount) {
    this.presentAmount = presentAmount;
  }

  @Column(name = "recharge_amount")
  public Double getRechargeAmount() {
    return rechargeAmount;
  }

  public void setRechargeAmount(Double rechargeAmount) {
    this.rechargeAmount = rechargeAmount;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "deleted_type")

  public DeletedType getDeletedType() {
    return deletedType;
  }

  public void setDeletedType(DeletedType deletedType) {
    this.deletedType = deletedType;
  }

  @Override
  public int compareTo(Object o) {
    return this.rechargeAmount.compareTo(((PreferentialPolicy)o).getRechargeAmount());
  }
}
