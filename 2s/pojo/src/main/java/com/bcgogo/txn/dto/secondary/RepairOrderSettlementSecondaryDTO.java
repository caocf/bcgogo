package com.bcgogo.txn.dto.secondary;

import com.bcgogo.utils.DateUtil;

public class RepairOrderSettlementSecondaryDTO {
  private Long repairOrderSecondaryId;
  private Long shopId;
  private Long date;
  private String dateStr;
  private String name;
  private Double balance;
  private Double income;
  private Double debt;
  private Double discount;

  public void fromRepairOrderSecondaryDTO(RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    setRepairOrderSecondaryId(repairOrderSecondaryDTO.getId());
    setShopId(repairOrderSecondaryDTO.getShopId());
    setName(repairOrderSecondaryDTO.getSalesName());
    setIncome(repairOrderSecondaryDTO.getSettledAmount());
    setDebt(repairOrderSecondaryDTO.getAccountDebtAmount());
    setDiscount(repairOrderSecondaryDTO.getAccountDiscount());
  }

  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    if (date != null) {
      dateStr = DateUtil.convertDateLongToString(date, "yyyy-MM-dd HH:mm");
    }
    this.date = date;
  }

  public String getDateStr() {
    return dateStr;
  }

  public void setDateStr(String dateStr) {
    this.dateStr = dateStr;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public Double getIncome() {
    return income;
  }

  public void setIncome(Double income) {
    this.income = income;
  }

  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }
}
