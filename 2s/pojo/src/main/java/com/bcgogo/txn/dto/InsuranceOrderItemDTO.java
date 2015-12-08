package com.bcgogo.txn.dto;

import com.bcgogo.utils.NumberUtil;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
public class InsuranceOrderItemDTO extends BcgogoOrderItemDto {
  private Long insuranceOrderId;
  private Double price;
  private Double total;

  public void fromRepairOrderItemDTO(RepairOrderItemDTO itemDTO) {
    if(itemDTO == null){
      return;
    }
    setProductId(itemDTO.getProductId());
    setCommodityCode(itemDTO.getCommodityCode());
    setProductName(itemDTO.getProductName());
    setBrand(itemDTO.getBrand());
    setModel(itemDTO.getModel());
    setSpec(itemDTO.getSpec());
    setVehicleBrand(itemDTO.getVehicleBrand());
    setVehicleModel(itemDTO.getVehicleModel());
    setPrice(NumberUtil.round(itemDTO.getPrice(),NumberUtil.MONEY_PRECISION));
    setAmount(NumberUtil.round(itemDTO.getAmount(),1));
    setUnit(itemDTO.getUnit());
    setTotal(NumberUtil.round(NumberUtil.doubleVal(getAmount()) * NumberUtil.doubleVal(getPrice()),NumberUtil.MONEY_PRECISION));
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getInsuranceOrderId() {
    return insuranceOrderId;
  }

  public void setInsuranceOrderId(Long insuranceOrderId) {
    this.insuranceOrderId = insuranceOrderId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }


}
