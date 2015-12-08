package com.bcgogo.user.merge;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.user.MergeType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-15
 * Time: 下午2:02
 * To change this template use File | Settings | File Templates.
 */
public class MergeRecordDTO {
  private Long shopId;
  private String childId;    //customerId;
  private String child;
  private String parentId;
  private String parent;
  private MergeType mergeType;
  private String mergeTimeStr;
  private Long operatorId;
  private String operator;
  private String mergeSnap;
  private String customerOrSupplierName;//搜索条件
  private Long startTime;
  private Long endTime;
  private String startTimeStr;
  private String endTimeStr;
  private ObjectStatus parentStatus;
  private String startPageNo;
  private Pager pager;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getChild() {
    return child;
  }

  public void setChild(String child) {
    this.child = child;
  }

  public String getChildId() {
    return childId;
  }

  public void setChildId(String childId) {
    this.childId = childId;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public MergeType getMergeType() {
    return mergeType;
  }

  public void setMergeType(MergeType mergeType) {
    this.mergeType = mergeType;
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

  public String getMergeTimeStr() {
    return mergeTimeStr;
  }

  public void setMergeTimeStr(String mergeTimeStr) {
    this.mergeTimeStr = mergeTimeStr;
  }

  public String getMergeSnap() {
    return mergeSnap;
  }

  public void setMergeSnap(String mergeSnap) {
    this.mergeSnap = mergeSnap;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public ObjectStatus getParentStatus() {
    return parentStatus;
  }

  public void setParentStatus(ObjectStatus parentStatus) {
    this.parentStatus = parentStatus;
  }

  public String getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(String startPageNo) {
    this.startPageNo = startPageNo;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public void convertRequestParams() throws ParseException {
    if(StringUtil.isNotEmpty(this.getCustomerOrSupplierName())){
      this.setCustomerOrSupplierName(this.getCustomerOrSupplierName().trim());
    }
    if(StringUtil.isNotEmpty(this.getOperator())){
      this.setOperator(this.getOperator().trim());
    }
    if(StringUtil.isNotEmpty(this.getStartTimeStr())){
      this.setStartTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT,this.getStartTimeStr()));
    }
    if(StringUtil.isNotEmpty(this.getEndTimeStr())){
      this.setEndTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT,this.getEndTimeStr()));
    }
  }

}
