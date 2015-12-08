package com.bcgogo.stat.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.CustomerStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-11
 * Time: 下午6:18
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "customer_stat")
public class CustomerStat extends LongIdentifier {
  private Long shopId;
  private String customerType;
  private Long amount;

  public CustomerStat() {
  }

  public CustomerStat(CustomerStatDTO customerStatDTO) {
    this.setId(customerStatDTO.getId());
    this.setShopId(customerStatDTO.getShopId());
    this.setCustomerType(customerStatDTO.getCustomerType());
    this.setAmount(customerStatDTO.getAmount());
  }

  public CustomerStat fromDTO(CustomerStatDTO customerStatDTO) {
    this.setId(customerStatDTO.getId());
    this.setShopId(customerStatDTO.getShopId());
    this.setCustomerType(customerStatDTO.getCustomerType());
    this.setAmount(customerStatDTO.getAmount());
    return this;
  }

  public CustomerStatDTO toDTO() {
    CustomerStatDTO customerStatDTO = new CustomerStatDTO();
    customerStatDTO.setId(this.getId());
    customerStatDTO.setShopId(this.getShopId());
    customerStatDTO.setCustomerType(this.getCustomerType());
    customerStatDTO.setAmount(this.getAmount());
    return customerStatDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_type", length = 20)
  public String getCustomerType() {
    return customerType;
  }

  public void setCustomerType(String customerType) {
    this.customerType = customerType;
  }

  @Column(name = "amount")
  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }
}
