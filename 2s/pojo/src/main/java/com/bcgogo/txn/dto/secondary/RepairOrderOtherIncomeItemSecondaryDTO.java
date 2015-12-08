package com.bcgogo.txn.dto.secondary;

import com.bcgogo.enums.OtherIncomeCalculateWay;
import com.bcgogo.txn.dto.RepairOrderOtherIncomeItemDTO;
import org.apache.commons.lang.StringUtils;

public class RepairOrderOtherIncomeItemSecondaryDTO {

  public RepairOrderOtherIncomeItemSecondaryDTO() {
  }

  private Long id;
  private String idStr;
  private Long shopId;
  private Long repairOrderSecondaryId;
  private String name;                                      //费用名称
  private Double price;                                     //金额
  private String memo;                                      //备注
  private OtherIncomeCalculateWay otherIncomeCalculateWay;  //计算方式
  private Double otherIncome;                               //材料管理费

  public void fromRepairOrderOtherIncomeItemDTO(RepairOrderOtherIncomeItemDTO repairOrderOtherIncomeItemDTO) {
    setShopId(repairOrderOtherIncomeItemDTO.getShopId());
    setName(repairOrderOtherIncomeItemDTO.getName());
    setMemo(repairOrderOtherIncomeItemDTO.getMemo());
    setPrice(repairOrderOtherIncomeItemDTO.getPrice());
    setOtherIncomeCalculateWay(repairOrderOtherIncomeItemDTO.getOtherIncomeCalculateWay());
    setOtherIncome(repairOrderOtherIncomeItemDTO.getOtherIncomeRate());
  }

  public boolean isValidator() {
    return StringUtils.isNotEmpty(name) && price != null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = id == null ? "" : id.toString();
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public OtherIncomeCalculateWay getOtherIncomeCalculateWay() {
    return otherIncomeCalculateWay;
  }

  public void setOtherIncomeCalculateWay(OtherIncomeCalculateWay otherIncomeCalculateWay) {
    this.otherIncomeCalculateWay = otherIncomeCalculateWay;
  }

  public Double getOtherIncome() {
    return otherIncome;
  }

  public void setOtherIncome(Double otherIncome) {
    this.otherIncome = otherIncome;
  }
}
