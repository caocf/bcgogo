package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.*;
import com.bcgogo.utils.NumberUtil;

/**
 * 员工营业外记账记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午1:28
 * To change this template use File | Settings | File Templates.
 */
public class AssistantBusinessAccountRecordDTO {

  private Long id;
  private Long shopId;
  private Long vestDate;
  private String vestDateStr;

  private Long assistantId;
  private String assistantName;

  private Long departmentId;
  private String departmentName;

  private String content;
  private Double total;
  private String accountCategory;

  private String businessCategory; //营业分类
  private Long businessCategoryId; //营业分类id
  private Long businessAccountId;//营业外记账记录 id
  private String docNo;//凭证号


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  public String getAssistantName() {
    return assistantName;
  }

  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = NumberUtil.toReserve(total, NumberUtil.PRECISION);
  }

  public String getAccountCategory() {
    return accountCategory;
  }

  public void setAccountCategory(String accountCategory) {
    this.accountCategory = accountCategory;
  }

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

  public Long getBusinessAccountId() {
    return businessAccountId;
  }

  public void setBusinessAccountId(Long businessAccountId) {
    this.businessAccountId = businessAccountId;
  }

  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }
}
