package com.bcgogo.user.merge;

import com.bcgogo.base.BaseResult;
import com.bcgogo.user.MergeType;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-10
 * Time: 上午7:30
 * To change this template use File | Settings | File Templates.
 */
public class MergeResult<T,V> extends BaseResult {
  private String customerOrSupplierIdStr;
  private T customerOrSupplierDTO;
  private Map<Long,V> mergeSnapMap=new HashMap<Long,V>();
  private List<MergeChangeLogDTO> mergeChangeLogs=new ArrayList<MergeChangeLogDTO>();
  private MergeType mergeType;
  //针对客户合并
  private Long childMemberId;
  private Long mergedMemberId;  //最终合并确定的memberid
  private Locale locale;
  private List<Long> childIds;




  public List<MergeChangeLogDTO> getMergeChangeLogs() {
    return mergeChangeLogs;
  }

  public void setMergeChangeLogs(List<MergeChangeLogDTO> mergeChangeLogs) {
    this.mergeChangeLogs = mergeChangeLogs;
  }

  public String getCustomerOrSupplierIdStr() {
    return customerOrSupplierIdStr;
  }

  public void setCustomerOrSupplierIdStr(String customerOrSupplierIdStr) {
    this.customerOrSupplierIdStr = customerOrSupplierIdStr;
  }

  public T getCustomerOrSupplierDTO() {
    return customerOrSupplierDTO;
  }

  public void setCustomerOrSupplierDTO(T customerOrSupplierDTO) {
    this.customerOrSupplierDTO = customerOrSupplierDTO;
  }

  public MergeType getMergeType() {
    return mergeType;
  }

  public void setMergeType(MergeType mergeType) {
    this.mergeType = mergeType;
  }

  public Long getChildMemberId() {
    return childMemberId;
  }

  public void setChildMemberId(Long childMemberId) {
    this.childMemberId = childMemberId;
  }

  public Long getMergedMemberId() {
    return mergedMemberId;
  }

  public void setMergedMemberId(Long mergedMemberId) {
    this.mergedMemberId = mergedMemberId;
  }

  public Map<Long, V> getMergeSnapMap() {
    return mergeSnapMap;
  }

  public void setMergeSnapMap(Map<Long, V> mergeSnapMap) {
    this.mergeSnapMap = mergeSnapMap;
  }

  public List<Long> getChildIds() {
    return childIds;
  }

  public void setChildIds(List<Long> childIds) {
    this.childIds = childIds;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}
