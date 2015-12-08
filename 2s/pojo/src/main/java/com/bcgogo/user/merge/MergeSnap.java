package com.bcgogo.user.merge;

import com.bcgogo.utils.JsonUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-15
 * Time: 上午1:20
 * To change this template use File | Settings | File Templates.
 */
public class MergeSnap<T> implements Cloneable{

  private Long shopId;
  private Long parentId;
  private String parentName;
  private Long childId;
  private String childName;
  private Long operatorId;
  private String operator;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

  public String getChildName() {
    return childName;
  }

  public void setChildName(String childName) {
    this.childName = childName;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public T clone() throws CloneNotSupportedException{
    return (T)super.clone();
  }

  public MergeRecordDTO toMergeRecord(){
    MergeRecordDTO mergeRecord=new MergeRecordDTO();
    mergeRecord.setShopId(this.getShopId());
    mergeRecord.setParentId(String.valueOf(this.getParentId()));
    mergeRecord.setParent(this.getParentName());
    mergeRecord.setChildId(String.valueOf(this.getChildId()));
    mergeRecord.setChild(this.getChildName());
    mergeRecord.setOperatorId(this.getOperatorId());
    mergeRecord.setOperator(this.getOperator());
    mergeRecord.setMergeSnap(JsonUtil.objectToJson(this));
    clear();
    return mergeRecord;
  }

  private void clear(){
    this.setShopId(null);
    this.setChildId(null);
    this.setParentId(null);
    this.setParentName(null);
    this.setChildName(null);
    this.setOperatorId(null);
    this.setOperator(null);
  }

}

