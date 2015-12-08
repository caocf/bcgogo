package com.bcgogo.user.merge;


import com.bcgogo.txn.dto.DebtDTO;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.user.dto.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-15
 * Time: 上午4:00
 * To change this template use File | Settings | File Templates.
 */


public class MergeCustomerSnap extends MergeSnap<MergeCustomerSnap> {

  private CustomerDTO child;
  private CustomerRecordDTO childRecord;
  private DebtDTO childDebt;
  private ReceivableDTO childReceivable;
  private MemberDTO childMember;
  private List<CustomerVehicleDTO> childVehicles;

  private CustomerDTO parent;
  private DebtDTO parentDebt;
  private ReceivableDTO parentReceivable;
  private CustomerRecordDTO parentRecord;
  private MemberDTO parentMember;
  private List<CustomerVehicleDTO> parentVehicles;

  public CustomerDTO getChild() {
    return child;
  }

  public void setChild(CustomerDTO child) {
    this.child = child;
  }

  public CustomerRecordDTO getChildRecord() {
    return childRecord;
  }

  public void setChildRecord(CustomerRecordDTO childRecord) {
    this.childRecord = childRecord;
  }

  public DebtDTO getChildDebt() {
    return childDebt;
  }

  public void setChildDebt(DebtDTO childDebt) {
    this.childDebt = childDebt;
  }
  public CustomerDTO getParent() {
    return parent;
  }

  public void setParent(CustomerDTO parent) {
    this.parent = parent;
  }

  public DebtDTO getParentDebt() {
    return parentDebt;
  }

  public void setParentDebt(DebtDTO parentDebt) {
    this.parentDebt = parentDebt;
  }

  public CustomerRecordDTO getParentRecord() {
    return parentRecord;
  }

  public void setParentRecord(CustomerRecordDTO parentRecord) {
    this.parentRecord = parentRecord;
  }

  public List<CustomerVehicleDTO> getChildVehicles() {
    return childVehicles;
  }

  public void setChildVehicles(List<CustomerVehicleDTO> childVehicles) {
    this.childVehicles = childVehicles;
  }

  public List<CustomerVehicleDTO> getParentVehicles() {
    return parentVehicles;
  }

  public void setParentVehicles(List<CustomerVehicleDTO> parentVehicles) {
    this.parentVehicles = parentVehicles;
  }

  public MemberDTO getChildMember() {
    return childMember;
  }

  public void setChildMember(MemberDTO childMember) {
    this.childMember = childMember;
  }

  public MemberDTO getParentMember() {
    return parentMember;
  }

  public void setParentMember(MemberDTO parentMember) {
    this.parentMember = parentMember;
  }

  public ReceivableDTO getChildReceivable() {
    return childReceivable;
  }

  public void setChildReceivable(ReceivableDTO childReceivable) {
    this.childReceivable = childReceivable;
  }

  public ReceivableDTO getParentReceivable() {
    return parentReceivable;
  }

  public void setParentReceivable(ReceivableDTO parentReceivable) {
    this.parentReceivable = parentReceivable;
  }
}