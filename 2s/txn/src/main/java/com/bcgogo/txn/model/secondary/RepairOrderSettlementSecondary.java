package com.bcgogo.txn.model.secondary;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.secondary.RepairOrderSettlementSecondaryDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "repair_order_settlement_secondary")
public class RepairOrderSettlementSecondary extends LongIdentifier {
  private Long repairOrderSecondaryId;
  private Long shopId;
  private Long date;
  private String name;
  private Double balance;
  private Double income;
  private Double debt;
  private Double discount;

  public RepairOrderSettlementSecondary() {
  }

  public void fromDTO(RepairOrderSettlementSecondaryDTO repairOrderSettlementSecondaryDTO) {
    setRepairOrderSecondaryId(repairOrderSettlementSecondaryDTO.getRepairOrderSecondaryId());
    setShopId(repairOrderSettlementSecondaryDTO.getShopId());
    setDate(repairOrderSettlementSecondaryDTO.getDate());
    setName(repairOrderSettlementSecondaryDTO.getName());
    setBalance(repairOrderSettlementSecondaryDTO.getBalance());
    setIncome(repairOrderSettlementSecondaryDTO.getIncome());
    setDebt(repairOrderSettlementSecondaryDTO.getDebt());
    setDiscount(repairOrderSettlementSecondaryDTO.getDiscount());
  }

  public RepairOrderSettlementSecondaryDTO toDTO() {
    RepairOrderSettlementSecondaryDTO repairOrderSettlementSecondaryDTO = new RepairOrderSettlementSecondaryDTO();
    repairOrderSettlementSecondaryDTO.setRepairOrderSecondaryId(repairOrderSecondaryId);
    repairOrderSettlementSecondaryDTO.setShopId(shopId);
    repairOrderSettlementSecondaryDTO.setDate(date);
    repairOrderSettlementSecondaryDTO.setName(name);
    repairOrderSettlementSecondaryDTO.setBalance(balance);
    repairOrderSettlementSecondaryDTO.setIncome(income);
    repairOrderSettlementSecondaryDTO.setDebt(debt);
    repairOrderSettlementSecondaryDTO.setDiscount(discount);
    return repairOrderSettlementSecondaryDTO;
  }


  @Column(name = "repair_order_secondary_id")
  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "date")
  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "balance")
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  @Column(name = "income")
  public Double getIncome() {
    return income;
  }

  public void setIncome(Double income) {
    this.income = income;
  }

  @Column(name = "debt")
  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  @Column(name = "discount")
  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }
}
