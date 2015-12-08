package com.bcgogo.txn.dto.secondary;

import java.util.List;

public class RepairOrderSecondaryResponse {
  private List<RepairOrderSecondaryDTO> repairOrderSecondaryDTOList;
  private String count;
  private String total;
  private String income;
  private String debt;
  private String discount;

  public List<RepairOrderSecondaryDTO> getRepairOrderSecondaryDTOList() {
    return repairOrderSecondaryDTOList;
  }

  public void setRepairOrderSecondaryDTOList(List<RepairOrderSecondaryDTO> repairOrderSecondaryDTOList) {
    this.repairOrderSecondaryDTOList = repairOrderSecondaryDTOList;
  }

  public String getCount() {
    return count;
  }

  public void setCount(String count) {
    this.count = count;
  }

  public String getTotal() {
    return total;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public String getIncome() {
    return income;
  }

  public void setIncome(String income) {
    this.income = income;
  }

  public String getDebt() {
    return debt;
  }

  public void setDebt(String debt) {
    this.debt = debt;
  }

  public String getDiscount() {
    return discount;
  }

  public void setDiscount(String discount) {
    this.discount = discount;
  }
}
