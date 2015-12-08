package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InsuranceOrderDTO;
import com.bcgogo.txn.dto.InsuranceOrderServiceDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午8:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "insurance_order_service")
public class InsuranceOrderService extends LongIdentifier {
  private Long insuranceOrderId;
  private Long serviceId;
  private String service;
  private Double total;
  private Long shopId;

  public InsuranceOrderServiceDTO toDTO(){
    InsuranceOrderServiceDTO insuranceOrderServiceDTO = new InsuranceOrderServiceDTO();
    insuranceOrderServiceDTO.setId(getId());
    insuranceOrderServiceDTO.setInsuranceOrderId(getInsuranceOrderId());
    insuranceOrderServiceDTO.setService(getService());
    insuranceOrderServiceDTO.setServiceId(getServiceId());
    insuranceOrderServiceDTO.setTotal(getTotal());
    insuranceOrderServiceDTO.setShopId(getShopId());

    return insuranceOrderServiceDTO;
  }

  public void fromDTO(InsuranceOrderServiceDTO insuranceOrderServiceDTO,InsuranceOrderDTO insuranceOrderDTO){
    if (insuranceOrderServiceDTO != null) {
      this.setId(getId());
      this.setInsuranceOrderId(insuranceOrderServiceDTO.getInsuranceOrderId());
      this.setService(insuranceOrderServiceDTO.getService());
      this.setServiceId(insuranceOrderServiceDTO.getServiceId());
      this.setTotal(insuranceOrderServiceDTO.getTotal());
    }
    if (insuranceOrderDTO != null) {
      this.setInsuranceOrderId(insuranceOrderDTO.getId());
      this.setShopId(insuranceOrderDTO.getShopId());
    }
  }

  @Column(name = "insurance_order_id")
  public Long getInsuranceOrderId() {
    return insuranceOrderId;
  }

  public void setInsuranceOrderId(Long insuranceOrderId) {
    this.insuranceOrderId = insuranceOrderId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service")
  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
