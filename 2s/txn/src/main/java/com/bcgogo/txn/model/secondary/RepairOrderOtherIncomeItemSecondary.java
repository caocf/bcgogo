package com.bcgogo.txn.model.secondary;

import com.bcgogo.enums.OtherIncomeCalculateWay;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.secondary.RepairOrderOtherIncomeItemSecondaryDTO;

import javax.persistence.*;

@Entity
@Table(name = "repair_order_other_income_item_secondary")
public class RepairOrderOtherIncomeItemSecondary extends LongIdentifier {
  private Long shopId;
  private Long repairOrderSecondaryId;
  private String name;                                      //费用名称
  private Double price;                                     //金额
  private String memo;                                      //备注
  private OtherIncomeCalculateWay otherIncomeCalculateWay;  //计算方式
  private Double otherIncome;                               //材料管理费

  public RepairOrderOtherIncomeItemSecondary() {
  }

  public RepairOrderOtherIncomeItemSecondaryDTO toDTO() {
    RepairOrderOtherIncomeItemSecondaryDTO repairOrderOtherIncomeItemSecondaryDTO = new RepairOrderOtherIncomeItemSecondaryDTO();
    repairOrderOtherIncomeItemSecondaryDTO.setId(getId());
    repairOrderOtherIncomeItemSecondaryDTO.setShopId(shopId);
    repairOrderOtherIncomeItemSecondaryDTO.setRepairOrderSecondaryId(repairOrderSecondaryId);
    repairOrderOtherIncomeItemSecondaryDTO.setName(name);
    repairOrderOtherIncomeItemSecondaryDTO.setPrice(price);
    repairOrderOtherIncomeItemSecondaryDTO.setMemo(memo);
    repairOrderOtherIncomeItemSecondaryDTO.setOtherIncomeCalculateWay(otherIncomeCalculateWay);
    repairOrderOtherIncomeItemSecondaryDTO.setOtherIncome(otherIncome);
    return repairOrderOtherIncomeItemSecondaryDTO;
  }

  public void fromDTO(RepairOrderOtherIncomeItemSecondaryDTO repairOrderOtherIncomeItemSecondaryDTO) {
    setName(repairOrderOtherIncomeItemSecondaryDTO.getName());
    setPrice(repairOrderOtherIncomeItemSecondaryDTO.getPrice());
    setMemo(repairOrderOtherIncomeItemSecondaryDTO.getMemo());
    setOtherIncomeCalculateWay(repairOrderOtherIncomeItemSecondaryDTO.getOtherIncomeCalculateWay());
    setOtherIncome(repairOrderOtherIncomeItemSecondaryDTO.getOtherIncome());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "repair_order_secondary_id")
  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "other_income_calculate_way")
  @Enumerated(EnumType.STRING)
  public OtherIncomeCalculateWay getOtherIncomeCalculateWay() {
    return otherIncomeCalculateWay;
  }

  public void setOtherIncomeCalculateWay(OtherIncomeCalculateWay otherIncomeCalculateWay) {
    this.otherIncomeCalculateWay = otherIncomeCalculateWay;
  }

  @Column(name = "other_income")
  public Double getOtherIncome() {
    return otherIncome;
  }

  public void setOtherIncome(Double otherIncome) {
    this.otherIncome = otherIncome;
  }
}
