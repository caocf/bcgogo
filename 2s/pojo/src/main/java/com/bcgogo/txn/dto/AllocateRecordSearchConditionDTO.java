package com.bcgogo.txn.dto;

import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: xzhu
 * Date: 12-5-11
 * Time: 上午9:15
 * 调拨记录搜索条件DTO
 */
public class AllocateRecordSearchConditionDTO {
  private static final Logger LOG = LoggerFactory.getLogger(AllocateRecordSearchConditionDTO.class);
  private static final Long ONE_DAY = 86400000L;

  private String uuid;
  //
  private Long shopId;
  private String receiptNo;
  private Long outStorehouseId;
  private Long inStorehouseId;
  private String editor;
  private String startDateStr;
  private String endDateStr;
  private Long startDate;
  private Long endDate;

  private GenerateType generateType;

  private String sortStatus;                    //排序规则
  private int maxRows = 15;//默认15
  private int startPageNo = 1;


  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public GenerateType getGenerateType() {
    return generateType;
  }

  public void setGenerateType(GenerateType generateType) {
    this.generateType = generateType;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    if(StringUtils.isNotBlank(receiptNo)){
      this.receiptNo = receiptNo.toUpperCase();
    }else{
      this.receiptNo = receiptNo;
    }

  }

  public Long getOutStorehouseId() {
    return outStorehouseId;
  }

  public void setOutStorehouseId(Long outStorehouseId) {
    this.outStorehouseId = outStorehouseId;
  }

  public Long getInStorehouseId() {
    return inStorehouseId;
  }

  public void setInStorehouseId(Long inStorehouseId) {
    this.inStorehouseId = inStorehouseId;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    if (StringUtils.isBlank(startDateStr)) return;
    this.startDate = DateUtil.parseInquiryCenterDate(startDateStr);
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    if (StringUtils.isBlank(endDateStr)) return;
    this.endDate = DateUtil.parseInquiryCenterDate(endDateStr);
    this.endDateStr = endDateStr;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public void verificationQueryTime() throws Exception {
    if (this.startDate != null && this.endDate != null) {
      if (this.startDate > endDate) {
        Long temp = endDate;
        endDate = startDate;
        startDate = temp;
      }
      endDate += ONE_DAY - 1;
      LOG.debug("query allocateRecord time:" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", startDate) + "--" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", endDate));
    }else if(this.endDate != null) {
      endDate += ONE_DAY - 1;
      LOG.debug("query allocateRecord time:" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", startDate) + "--" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", endDate));
    }
  }

  public enum GenerateType {
    SYSTEM("系统生成"),USER("自主生成");

    String value;

    public String getValue() {
      return value;
    }

    private GenerateType(String value) {
      this.value = value;
    }
  }
}
