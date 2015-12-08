package com.bcgogo.txn.dto.finance;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-26
 * Time: 下午3:13
 */
public class HardwareSoftwareAccountDTO {
  private static final Logger LOG = LoggerFactory.getLogger(HardwareSoftwareAccountDTO.class);
  private Long id;
  private Long shopId;
  private String shopName;
  private Double hardwareReceivedAmount = 0.0d;
  private Double hardwareReceivableAmount = 0.0d;
  private Double hardwareTotalAmount = 0.0d;

  private Double softwareReceivedAmount = 0.0d;
  private Double softwareReceivableAmount = 0.0d;
  private Double softwareTotalAmount = 0.0d;

  private Double totalReceivedAmount = 0.0d;
  private Double totalReceivableAmount = 0.0d;
  private Double totalAmount = 0.0d;

  private List<HardwareSoftwareAccountOrderDTO> orders = new ArrayList<HardwareSoftwareAccountOrderDTO>();

  public void createHardwarePayable(Double payable) {
    this.setHardwareReceivableAmount(this.getHardwareReceivableAmount() + payable);
    this.setHardwareTotalAmount(this.getHardwareTotalAmount() + payable);

    this.setTotalReceivableAmount(this.getTotalReceivableAmount() + payable);
    this.setTotalAmount(this.getTotalAmount() + payable);
  }


  public void createSoftwarePayable(Double payable) {
    this.setSoftwareReceivableAmount(this.getSoftwareReceivableAmount() + payable);
    this.setSoftwareTotalAmount(this.getSoftwareTotalAmount() + payable);

    this.setTotalAmount(this.getTotalAmount() + payable);
    this.setTotalReceivableAmount(this.getTotalReceivableAmount() + payable);
  }

  public void createSoftwareReceived(Double received) throws BcgogoException {
    this.setSoftwareReceivedAmount(NumberUtil.round(this.getSoftwareReceivedAmount() + received, 0));
    this.setSoftwareReceivableAmount(NumberUtil.round(this.getSoftwareReceivableAmount() - received, 0));

    this.setTotalReceivableAmount(NumberUtil.round(this.getTotalReceivableAmount() - received, 0));
    this.setTotalReceivedAmount(NumberUtil.round(this.getTotalReceivedAmount() + received, 0));
    if (this.getSoftwareReceivableAmount() < 0)
      LOG.error("HardwareSoftwareAccount SoftwareReceivableAmount is < 0");
  }

  public void createHardwareReceived(Double received) throws BcgogoException {
    this.setHardwareReceivableAmount(NumberUtil.round(this.getHardwareReceivableAmount() - received, 0));
    this.setHardwareReceivedAmount(this.getHardwareReceivedAmount() + received);

    this.setTotalReceivedAmount(this.getTotalReceivedAmount() + received);
    this.setTotalReceivableAmount(NumberUtil.round(this.getTotalReceivableAmount() - received, 0));

    if (this.getHardwareReceivableAmount() < 0)
      LOG.error("HardwareSoftwareAccount HardwareReceivableAmount is < 0");
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getHardwareReceivedAmount() {
    return hardwareReceivedAmount;
  }

  public void setHardwareReceivedAmount(Double hardwareReceivedAmount) {
    this.hardwareReceivedAmount = hardwareReceivedAmount;
  }

  public Double getHardwareReceivableAmount() {
    return hardwareReceivableAmount;
  }

  public void setHardwareReceivableAmount(Double hardwareReceivableAmount) {
    this.hardwareReceivableAmount = hardwareReceivableAmount;
  }

  public Double getHardwareTotalAmount() {
    return hardwareTotalAmount;
  }

  public void setHardwareTotalAmount(Double hardwareTotalAmount) {
    this.hardwareTotalAmount = hardwareTotalAmount;
  }

  public Double getSoftwareReceivedAmount() {
    return softwareReceivedAmount;
  }

  public void setSoftwareReceivedAmount(Double softwareReceivedAmount) {
    this.softwareReceivedAmount = softwareReceivedAmount;
  }

  public Double getSoftwareReceivableAmount() {
    return softwareReceivableAmount;
  }

  public void setSoftwareReceivableAmount(Double softwareReceivableAmount) {
    this.softwareReceivableAmount = softwareReceivableAmount;
  }

  public Double getSoftwareTotalAmount() {
    return softwareTotalAmount;
  }

  public void setSoftwareTotalAmount(Double softwareTotalAmount) {
    this.softwareTotalAmount = softwareTotalAmount;
  }

  public Double getTotalReceivedAmount() {
    return totalReceivedAmount;
  }

  public void setTotalReceivedAmount(Double totalReceivedAmount) {
    this.totalReceivedAmount = totalReceivedAmount;
  }

  public Double getTotalReceivableAmount() {
    return totalReceivableAmount;
  }

  public void setTotalReceivableAmount(Double totalReceivableAmount) {
    this.totalReceivableAmount = totalReceivableAmount;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public List<HardwareSoftwareAccountOrderDTO> getOrders() {
    return orders;
  }

  public void setOrders(List<HardwareSoftwareAccountOrderDTO> orders) {
    this.orders = orders;
  }
}
