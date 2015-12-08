package com.bcgogo.txn.dto;

import com.bcgogo.BooleanEnum;
import com.bcgogo.enums.OtherIncomeCalculateWay;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-13
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderTemplateOtherIncomeItemDTO {

  private Long id;
  private Long shopId;
  private Long repairOrderTemplateId;
  private String name;
  private String memo;
  private Double price;
  private String idStr;

  private BooleanEnum calculateCostPrice;//是否计算成本
  private Double otherIncomeCostPrice; //成本金额

  private OtherIncomeCalculateWay otherIncomeCalculateWay;//计算方式
  private Double otherIncomeRate;//施工单其他费用 材料管理费 计算比率

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(null != this.id)
    {
      this.idStr = this.id.toString();
    }
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getRepairOrderTemplateId() {
    return repairOrderTemplateId;
  }

  public void setRepairOrderTemplateId(Long repairOrderTemplateId) {
    this.repairOrderTemplateId = repairOrderTemplateId;
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

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public BooleanEnum getCalculateCostPrice() {
    return calculateCostPrice;
  }

  public void setCalculateCostPrice(BooleanEnum calculateCostPrice) {
    this.calculateCostPrice = calculateCostPrice;
  }

  public Double getOtherIncomeCostPrice() {
    return otherIncomeCostPrice;
  }

  public void setOtherIncomeCostPrice(Double otherIncomeCostPrice) {
    this.otherIncomeCostPrice = otherIncomeCostPrice;
  }

  public OtherIncomeCalculateWay getOtherIncomeCalculateWay() {
    return otherIncomeCalculateWay;
  }

  public void setOtherIncomeCalculateWay(OtherIncomeCalculateWay otherIncomeCalculateWay) {
    this.otherIncomeCalculateWay = otherIncomeCalculateWay;
  }

  public Double getOtherIncomeRate() {
    return otherIncomeRate;
  }

  public void setOtherIncomeRate(Double otherIncomeRate) {
    this.otherIncomeRate = otherIncomeRate;
  }

  public static Map<Long,RepairOrderTemplateOtherIncomeItemDTO> listToMap(List<RepairOrderTemplateOtherIncomeItemDTO> itemDTOList)
  {
    Map<Long,RepairOrderTemplateOtherIncomeItemDTO> itemDTOMap = new HashMap<Long, RepairOrderTemplateOtherIncomeItemDTO>();

    if(CollectionUtils.isNotEmpty(itemDTOList))
    {
      for(RepairOrderTemplateOtherIncomeItemDTO itemDTO : itemDTOList)
      {
        if(StringUtils.isBlank(itemDTO.getName()))
        {
          continue;
        }

        itemDTOMap.put(itemDTO.getId(),itemDTO);
      }
    }

    return itemDTOMap;
  }
}
