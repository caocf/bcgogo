package com.bcgogo.config.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.merge.MergeRecordDTO;
import com.bcgogo.user.MergeType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-15
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "merge_record")
public class MergeRecord  extends LongIdentifier {

  private Long shopId;
  private Long childId;    //customerId;
  private String child;
  private Long parentId;
  private String parent;
  private MergeType mergeType;
  private Long mergeTime;
  private Long operatorId;
  private String operator;
  private String mergeSnap;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "child_id")
  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "child")
  public String getChild() {
    return child;
  }

  public void setChild(String child) {
    this.child = child;
  }

  @Column(name = "parent")
  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  @Column(name = "merge_type")
  @Enumerated(EnumType.STRING)
  public MergeType getMergeType() {
    return mergeType;
  }

  public void setMergeType(MergeType mergeType) {
    this.mergeType = mergeType;
  }

  @Column(name = "merge_time")
  public Long getMergeTime() {
    return mergeTime;
  }

  public void setMergeTime(Long mergeTime) {
    this.mergeTime = mergeTime;
  }

  @Column(name = "merge_snap")
  public String getMergeSnap() {
    return mergeSnap;
  }

  public void setMergeSnap(String mergeSnap) {
    this.mergeSnap = mergeSnap;
  }

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "operator")
  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public MergeRecord fromDTO(MergeRecordDTO mergeRecordDTO) throws ParseException {
    this.setParent(mergeRecordDTO.getParent());
    this.setParentId(NumberUtil.longValue(mergeRecordDTO.getParentId()));
    this.setMergeSnap(mergeRecordDTO.getMergeSnap());
    this.setMergeType(mergeRecordDTO.getMergeType());
    this.setChild(mergeRecordDTO.getChild());
    this.setChildId(NumberUtil.longValue(mergeRecordDTO.getChildId()));
    this.setOperator(mergeRecordDTO.getOperator());
    this.setOperatorId(mergeRecordDTO.getOperatorId());
    this.setShopId(mergeRecordDTO.getShopId());
    this.setMergeTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, mergeRecordDTO.getMergeTimeStr()));
    return this;
  }

  public MergeRecordDTO toDTO(){
    MergeRecordDTO mergeRecordDTO=new MergeRecordDTO();
     mergeRecordDTO.setParent(this.getParent());
    mergeRecordDTO.setParentId(String.valueOf(this.getParentId()));
    mergeRecordDTO.setMergeSnap(this.getMergeSnap());
    mergeRecordDTO.setMergeType(this.getMergeType());
    mergeRecordDTO.setChild(this.getChild());
    mergeRecordDTO.setChildId(String.valueOf(this.getChildId()));
    mergeRecordDTO.setOperator(this.getOperator());
     mergeRecordDTO.setOperatorId(this.getOperatorId());
    mergeRecordDTO.setShopId(this.getShopId());
    mergeRecordDTO.setMergeTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, this.getMergeTime()));
    return mergeRecordDTO;
  }
}
