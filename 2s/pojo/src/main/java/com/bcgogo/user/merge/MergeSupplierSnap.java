package com.bcgogo.user.merge;

import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.PayableDTO;
import com.bcgogo.user.dto.SupplierDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-15
 * Time: 上午5:34
 * To change this template use File | Settings | File Templates.
 */
public class MergeSupplierSnap extends MergeSnap<MergeSupplierSnap> {
  private SupplierDTO parent;
  private SupplierRecordDTO parentRecord;
  private PayableDTO parentPayable;

  private SupplierDTO child;
  private SupplierRecordDTO childRecord;
  private PayableDTO childPayable;

  public SupplierDTO getParent() {
    return parent;
  }

  public void setParent(SupplierDTO parent) {
    this.parent = parent;
  }

  public SupplierRecordDTO getParentRecord() {
    return parentRecord;
  }

  public void setParentRecord(SupplierRecordDTO parentRecord) {
    this.parentRecord = parentRecord;
  }

  public PayableDTO getParentPayable() {
    return parentPayable;
  }

  public void setParentPayable(PayableDTO parentPayable) {
    this.parentPayable = parentPayable;
  }

  public SupplierDTO getChild() {
    return child;
  }

  public void setChild(SupplierDTO child) {
    this.child = child;
  }

  public SupplierRecordDTO getChildRecord() {
    return childRecord;
  }

  public void setChildRecord(SupplierRecordDTO childRecord) {
    this.childRecord = childRecord;
  }

  public PayableDTO getChildPayable() {
    return childPayable;
  }

  public void setChildPayable(PayableDTO childPayable) {
    this.childPayable = childPayable;
  }
}
