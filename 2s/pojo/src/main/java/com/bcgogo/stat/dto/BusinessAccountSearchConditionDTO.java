package com.bcgogo.stat.dto;

import com.bcgogo.enums.BusinessAccountEnum;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;

/**
 * 营业外记账查询条件
 * Created by IntelliJ IDEA.
 * User: Li jinlong
 * Date: 12-9-19
 * Time: 下午7:09
 * To change this template use File | Settings | File Templates.
 */
public class BusinessAccountSearchConditionDTO {

  //营业记账起始日期
  private String editDateStartStr;

  // 营业记账结束日期
  private String editDateEndStr ;

  //记账类别（固定5项：房租、工资提成、水电杂项、其他、营业外收入）
  private String accountCategory = "";

  //凭证号
  private String  docNo = "";

   //部门
  private String dept = "";

    //人员
  private String person = "";

  private String moneyCategoryStr = "";
  private MoneyCategory moneyCategory;

  private BusinessAccountEnum accountEnum;

  private String businessCategory;
  private Long businessCategoryId;

  private Long startTime;
  private Long endTime;

  private Integer rowStart;
  private Integer maxRows;

  public String getBusinessCategory() {
    return businessCategory;
  }

  public void setBusinessCategory(String businessCategory) {
    this.businessCategory = businessCategory;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public String getEditDateStartStr() {
    return editDateStartStr;
  }

  public void setEditDateStartStr(String editDateStartStr) {
    this.editDateStartStr = editDateStartStr;
  }

  public String getEditDateEndStr() {
    return editDateEndStr;
  }

  public void setEditDateEndStr(String editDateEndStr) {
    this.editDateEndStr = editDateEndStr;
  }

  public String getAccountCategory() {
    return accountCategory;
  }

  public void setAccountCategory(String accountCategory) {
    this.accountCategory = accountCategory;
  }

  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
  }

  public String getPerson() {
    return person;
  }

  public void setPerson(String person) {
    this.person = person;
  }

  public String getMoneyCategoryStr() {
    return moneyCategoryStr;
  }

  public void setMoneyCategoryStr(String moneyCategoryStr) {
    this.moneyCategoryStr = moneyCategoryStr;
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

  public MoneyCategory getMoneyCategory() {
    return moneyCategory;
  }

  public void setMoneyCategory(MoneyCategory moneyCategory) {
    this.moneyCategory = moneyCategory;
  }

  public BusinessAccountEnum getAccountEnum() {
    return accountEnum;
  }

  public void setAccountEnum(BusinessAccountEnum accountEnum) {
    this.accountEnum = accountEnum;
  }

  public Integer getRowStart() {
    return rowStart;
  }

  public void setRowStart(Integer rowStart) {
    this.rowStart = rowStart;
  }

  public Integer getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(Integer maxRows) {
    this.maxRows = maxRows;
  }
}
