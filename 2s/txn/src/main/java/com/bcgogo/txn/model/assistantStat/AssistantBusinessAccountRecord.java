package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.AssistantBusinessAccountRecordDTO;
import com.bcgogo.txn.dto.assistantStat.AssistantProductRecordDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;

/**
 * 会员业绩统计-员工商品销售记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "assistant_business_account_record")
public class AssistantBusinessAccountRecord extends LongIdentifier {

  public AssistantBusinessAccountRecordDTO toDTO() {
    AssistantBusinessAccountRecordDTO recordDTO = new AssistantBusinessAccountRecordDTO();

    recordDTO.setShopId(this.getShopId());
    recordDTO.setVestDate(this.getVestDate());
    if (getVestDate() != null) {
      recordDTO.setVestDateStr(DateUtil.dateLongToStr(getVestDate(), DateUtil.DATE_STRING_FORMAT_CN));
    }
    recordDTO.setAccountCategory(this.getAccountCategory());
    recordDTO.setAssistantId(this.getAssistantId());
    recordDTO.setAssistantName(this.getAssistantName());
    recordDTO.setDepartmentId(this.getDepartmentId());
    recordDTO.setDepartmentName(this.getDepartmentName());
    recordDTO.setContent(this.getContent());
    recordDTO.setTotal(this.getTotal());
    recordDTO.setShopId(this.getId());
    recordDTO.setBusinessCategory(getBusinessCategory());
    recordDTO.setBusinessCategoryId(getBusinessCategoryId());
    recordDTO.setBusinessAccountId(getBusinessAccountId());
    recordDTO.setDocNo(getDocNo());
    return recordDTO;
  }

  public AssistantBusinessAccountRecord fromDTO(AssistantBusinessAccountRecordDTO recordDTO) {

    this.setShopId(recordDTO.getShopId());
    this.setVestDate(recordDTO.getVestDate());
    this.setAccountCategory(recordDTO.getAccountCategory());
    this.setAssistantId(recordDTO.getAssistantId());
    this.setAssistantName(recordDTO.getAssistantName());
    this.setDepartmentId(recordDTO.getDepartmentId());
    this.setDepartmentName(recordDTO.getDepartmentName());
    this.setContent(recordDTO.getContent());
    this.setTotal(recordDTO.getTotal());
    this.setBusinessCategory(recordDTO.getBusinessCategory());
    this.setBusinessAccountId(recordDTO.getBusinessAccountId());
    this.setBusinessCategoryId(recordDTO.getBusinessCategoryId());
    this.setDocNo(recordDTO.getDocNo());
    return this;
  }

  private Long shopId;
  private Long vestDate;

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
  private Long statTime;//统计时间

  @Column(name = "assistant_id")
  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  @Column(name = "assistant_name")
  public String getAssistantName() {
    return assistantName;
  }

  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
  }

  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Column(name = "department_name")
  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "account_category")
  public String getAccountCategory() {
    return accountCategory;
  }

  public void setAccountCategory(String accountCategory) {
    this.accountCategory = accountCategory;
  }

  @Column(name = "business_category")
  public String getBusinessCategory() {
    return businessCategory;
  }

  public void setBusinessCategory(String businessCategory) {
    this.businessCategory = businessCategory;
  }

  @Column(name = "business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name = "business_account_id")
  public Long getBusinessAccountId() {
    return businessAccountId;
  }

  public void setBusinessAccountId(Long businessAccountId) {
    this.businessAccountId = businessAccountId;
  }

  @Column(name = "doc_no")
  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }
}
