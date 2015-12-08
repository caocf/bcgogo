package com.bcgogo.txn.model;

import com.bcgogo.enums.payment.LoanTransfersStatus;
import com.bcgogo.enums.LoanType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.LoanTransfersDTO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-22
 * Time: 下午4:06
 * 货款转账
 */
@Entity
@Table(name = "loan_transfers")
public class LoanTransfers extends LongIdentifier {
  private Long shopId;
  private Long transfersTime;//转账时间
  private Long payTime;//付款时间
  private String transfersNumber; //转账账号
  private Double amount;            //转账金额
  private LoanType type;   //货款分类
  private Long shopVersionId; //店面版本
  private String hardwareProduct; //硬件产品
  private LoanTransfersStatus status; //转账转态
  private String memo;               //备注

  public LoanTransfers() {

  }

  public LoanTransfers(LoanTransfersDTO dto) {
    this.setShopId(dto.getShopId());
    this.setTransfersNumber(dto.getTransfersNumber());
    this.setTransfersTime(dto.getTransfersTime());
    this.setPayTime(dto.getPayTime());
    this.setAmount(dto.getAmount());
    this.setType(dto.getType());
    this.setShopVersionId(dto.getShopVersionId());
    this.setStatus(dto.getStatus());
    this.setMemo(dto.getMemo());
    this.setId(dto.getId());
    if (!ArrayUtils.isEmpty(dto.getProducts())) {
      this.setHardwareProduct(StringUtils.join(dto.getProducts(), ","));
    }
  }

  public LoanTransfers fromDTO(LoanTransfersDTO dto) {
    this.setShopId(dto.getShopId());
    this.setTransfersNumber(dto.getTransfersNumber());
    this.setTransfersTime(dto.getTransfersTime());
    this.setPayTime(dto.getPayTime());
    this.setAmount(dto.getAmount());
    this.setType(dto.getType());
    this.setShopVersionId(dto.getShopVersionId());
    this.setStatus(dto.getStatus());
    this.setMemo(dto.getMemo());
    this.setId(dto.getId());
    if (!ArrayUtils.isEmpty(dto.getProducts())) {
      this.setHardwareProduct(StringUtils.join(dto.getProducts(), ","));
    }
    return this;
  }

  public LoanTransfersDTO toDTO() {
    LoanTransfersDTO dto = new LoanTransfersDTO();
    dto.setShopId(this.getShopId());
    dto.setTransfersNumber(this.getTransfersNumber());
    dto.setTransfersTime(this.getTransfersTime());
    dto.setPayTime(this.getPayTime());
    dto.setType(this.getType());
    dto.setAmount(this.getAmount());
    dto.setShopVersionId(this.getShopVersionId());
    dto.setStatus(this.getStatus());
    dto.setMemo(this.getMemo());
    dto.setId(this.getId());
    if (StringUtils.isNotBlank(this.getHardwareProduct())) {
      dto.setProducts(this.getHardwareProduct().split(","));
    }
    return dto;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "transfers_time")
  public Long getTransfersTime() {
    return transfersTime;
  }

  public void setTransfersTime(Long transfersTime) {
    this.transfersTime = transfersTime;
  }

  @Column(name = "transfers_number")
  public String getTransfersNumber() {
    return transfersNumber;
  }

  public void setTransfersNumber(String transfersNumber) {
    this.transfersNumber = transfersNumber;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public LoanType getType() {
    return type;
  }

  public void setType(LoanType type) {
    this.type = type;
  }

  @Column(name = "shop_version_id")
  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  @Column(name = "hardware_product")
  public String getHardwareProduct() {
    return hardwareProduct;
  }

  public void setHardwareProduct(String hardwareProduct) {
    this.hardwareProduct = hardwareProduct;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public LoanTransfersStatus getStatus() {
    return status;
  }

  public void setStatus(LoanTransfersStatus status) {
    this.status = status;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
  @Column(name = "pay_time")
  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }
}
