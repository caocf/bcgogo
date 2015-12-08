package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.BcgogoHardwareReceivableDetailDTO;
import com.bcgogo.txn.dto.finance.HardwareSoftwareAccountDTO;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:58
 * 店面硬件软件总账单
 */
@Entity
@Table(name = "hardware_software_account")
public class HardwareSoftwareAccount extends LongIdentifier {
  private Long shopId;
  private Double hardwareReceivedAmount;
  private Double hardwareReceivableAmount;
  private Double hardwareTotalAmount;

  private Double softwareReceivedAmount;
  private Double softwareReceivableAmount;
  private Double softwareTotalAmount;

  private Double totalReceivedAmount;
  private Double totalReceivableAmount;
  private Double totalAmount;


  public HardwareSoftwareAccount() {
    super();
  }

  public HardwareSoftwareAccount(long shopId) {
    this.setShopId(shopId);

    this.setHardwareReceivableAmount(0.0D);
    this.setHardwareReceivedAmount(0.0D);
    this.setHardwareTotalAmount(0.0D);

    this.setSoftwareReceivableAmount(0.0D);
    this.setSoftwareReceivedAmount(0.0D);
    this.setSoftwareTotalAmount(0.0D);

    this.setTotalAmount(0.0D);
    this.setTotalReceivableAmount(0.0D);
    this.setTotalReceivedAmount(0.0D);
  }

  public HardwareSoftwareAccountDTO toDTO() {
    HardwareSoftwareAccountDTO dto = new HardwareSoftwareAccountDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());

    dto.setHardwareReceivableAmount(this.getHardwareReceivableAmount());
    dto.setHardwareReceivedAmount(this.getHardwareReceivedAmount());
    dto.setHardwareTotalAmount(this.getHardwareTotalAmount());

    dto.setSoftwareReceivableAmount(this.getSoftwareReceivableAmount());
    dto.setSoftwareReceivedAmount(this.getSoftwareReceivedAmount());
    dto.setSoftwareTotalAmount(this.getSoftwareTotalAmount());

    dto.setTotalAmount(this.getTotalAmount());
    dto.setTotalReceivableAmount(this.getTotalReceivableAmount());
    dto.setTotalReceivedAmount(this.getTotalReceivedAmount());

    return dto;
  }

  public void fromDTO(HardwareSoftwareAccountDTO dto) {
    this.setHardwareReceivableAmount(dto.getHardwareReceivableAmount());
    this.setHardwareReceivedAmount(dto.getHardwareReceivedAmount());
    this.setHardwareTotalAmount(dto.getHardwareTotalAmount());

    this.setSoftwareReceivableAmount(dto.getSoftwareReceivableAmount());
    this.setSoftwareReceivedAmount(dto.getSoftwareReceivedAmount());
    this.setSoftwareTotalAmount(dto.getSoftwareTotalAmount());

    this.setTotalAmount(dto.getTotalAmount());
    this.setTotalReceivableAmount(dto.getTotalReceivableAmount());
    this.setTotalReceivedAmount(dto.getTotalReceivedAmount());
  }


  public void createSoftwarePayable(Double payable) {
    this.setSoftwareReceivableAmount(this.getSoftwareReceivableAmount() + payable);
    this.setSoftwareTotalAmount(this.getSoftwareTotalAmount() + payable);

    this.setTotalAmount(this.getTotalAmount() + payable);
    this.setTotalReceivableAmount(this.getTotalReceivableAmount() + payable);
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "hardware_received_amount")
  public Double getHardwareReceivedAmount() {
    return hardwareReceivedAmount;
  }

  public void setHardwareReceivedAmount(Double hardwareReceivedAmount) {
    this.hardwareReceivedAmount = hardwareReceivedAmount;
  }

  @Column(name = "hardware_receivable_amount")
  public Double getHardwareReceivableAmount() {
    return hardwareReceivableAmount;
  }

  public void setHardwareReceivableAmount(Double hardwareReceivableAmount) {
    this.hardwareReceivableAmount = hardwareReceivableAmount;
  }

  @Column(name = "hardware_total_amount")
  public Double getHardwareTotalAmount() {
    return hardwareTotalAmount;
  }

  public void setHardwareTotalAmount(Double hardwareTotalAmount) {
    this.hardwareTotalAmount = hardwareTotalAmount;
  }

  @Column(name = "software_received_amount")
  public Double getSoftwareReceivedAmount() {
    return softwareReceivedAmount;
  }

  public void setSoftwareReceivedAmount(Double softwareReceivedAmount) {
    this.softwareReceivedAmount = softwareReceivedAmount;
  }

  @Column(name = "software_receivable_amount")
  public Double getSoftwareReceivableAmount() {
    return softwareReceivableAmount;
  }

  public void setSoftwareReceivableAmount(Double softwareReceivableAmount) {
    this.softwareReceivableAmount = softwareReceivableAmount;
  }

  @Column(name = "software_total_amount")
  public Double getSoftwareTotalAmount() {
    return softwareTotalAmount;
  }

  public void setSoftwareTotalAmount(Double softwareTotalAmount) {
    this.softwareTotalAmount = softwareTotalAmount;
  }

  @Column(name = "total_received_amount")
  public Double getTotalReceivedAmount() {
    return totalReceivedAmount;
  }

  public void setTotalReceivedAmount(Double totalReceivedAmount) {
    this.totalReceivedAmount = totalReceivedAmount;
  }

  @Column(name = "total_receivable_amount")
  public Double getTotalReceivableAmount() {
    return totalReceivableAmount;
  }

  public void setTotalReceivableAmount(Double totalReceivableAmount) {
    this.totalReceivableAmount = totalReceivableAmount;
  }

  @Column(name = "total_amount")
  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }


}
