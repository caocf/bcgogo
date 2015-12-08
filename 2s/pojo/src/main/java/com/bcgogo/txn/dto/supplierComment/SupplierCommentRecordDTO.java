package com.bcgogo.txn.dto.supplierComment;

import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * 供应商点评记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-13
 * Time: 下午5:05
 * To change this template use File | Settings | File Templates.
 */
public class SupplierCommentRecordDTO extends CommentRecordDTO {
  private Long supplierShopId;  //供应商店铺id
  private Long supplierId;    //供应商id
  private String supplier;   //供应商姓名

  private Long customerShopId;  //客户店铺id
  private Long customerId;    //客户id
  private String customer;  //客户姓名

  private Long purchaseOrderId;    //采购单id
  private Long purchaseInventoryId;   //入库单id
  private Long salesOrderId;//供应商销售单id

  private String purchaseInventoryIdStr;
  private String purchaseOrderIdStr;

  private String supplierCommentRecordIdStr; //id

  private Double qualityScore;  //质量分数
  private Double performanceScore; //性价比分数
  private Double speedScore; //发货速度分数
  private Double attitudeScore;   //服务态度分数


  private double qualityScoreSpan;//用来控制前台星星css的高度
  private double performanceScoreSpan;
  private double speedScoreSpan;
  private double attitudeScoreSpan;

  private String receiptNo;//单据号


  public String getSupplierCommentRecordIdStr() {
    return supplierCommentRecordIdStr;
  }

  public void setSupplierCommentRecordIdStr(String supplierCommentRecordIdStr) {
    this.supplierCommentRecordIdStr = supplierCommentRecordIdStr;
  }

  public String getPurchaseInventoryIdStr() {
    return purchaseInventoryIdStr;
  }

  public void setPurchaseInventoryIdStr(String purchaseInventoryIdStr) {
    this.purchaseInventoryIdStr = purchaseInventoryIdStr;
  }

  public String getPurchaseOrderIdStr() {
    return purchaseOrderIdStr;
  }

  public void setPurchaseOrderIdStr(String purchaseOrderIdStr) {
    this.purchaseOrderIdStr = purchaseOrderIdStr;
  }

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  public Long getSalesOrderId() {
    return salesOrderId;
  }

  public void setSalesOrderId(Long salesOrderId) {
    this.salesOrderId = salesOrderId;
  }

  public void setSupplierCommentInfo(PurchaseOrderDTO purchaseOrderDTO,PurchaseInventoryDTO purchaseInventoryDTO,SalesOrderDTO salesOrderDTO) {
    this.setSupplierShopId(salesOrderDTO.getShopId());

    this.setSupplier(purchaseInventoryDTO.getSupplier());
    this.setSupplierId(purchaseInventoryDTO.getSupplierId());

    this.setCustomerShopId(purchaseInventoryDTO.getShopId());
    this.setCustomer(salesOrderDTO.getCustomer());
    this.setCustomerId(salesOrderDTO.getCustomerId());

    this.setPurchaseOrderId(purchaseOrderDTO.getId());
    this.setPurchaseInventoryId(purchaseInventoryDTO.getId());
    this.setSalesOrderId(salesOrderDTO.getId());

    this.setCommentTime(System.currentTimeMillis());
    this.setCommentStatus(CommentStatus.UN_STAT);

  }

  public double getQualityScoreSpan() {
    return qualityScoreSpan;
  }

  public void setQualityScoreSpan(double qualityScoreSpan) {
    this.qualityScoreSpan = qualityScoreSpan;
  }

  public double getPerformanceScoreSpan() {
    return performanceScoreSpan;
  }

  public void setPerformanceScoreSpan(double performanceScoreSpan) {
    this.performanceScoreSpan = performanceScoreSpan;
  }

  public double getAttitudeScoreSpan() {
    return attitudeScoreSpan;
  }

  public void setAttitudeScoreSpan(double attitudeScoreSpan) {
    this.attitudeScoreSpan = attitudeScoreSpan;
  }

  public double getSpeedScoreSpan() {
    return speedScoreSpan;
  }

  public void setSpeedScoreSpan(double speedScoreSpan) {
    this.speedScoreSpan = speedScoreSpan;
  }

  public Double getQualityScore() {
    return qualityScore;
  }

  public void setQualityScore(Double qualityScore) {
    this.qualityScore = qualityScore;
  }

  public Double getPerformanceScore() {
    return performanceScore;
  }

  public void setPerformanceScore(Double performanceScore) {
    this.performanceScore = performanceScore;
  }

  public Double getSpeedScore() {
    return speedScore;
  }

  public void setSpeedScore(Double speedScore) {
    this.speedScore = speedScore;
  }

  public Double getAttitudeScore() {
    return attitudeScore;
  }

  public void setAttitudeScore(Double attitudeScore) {
    this.attitudeScore = attitudeScore;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Override
  public String toString() {
    return "SupplierCommentRecordDTO{" +
        "supplierShopId=" + supplierShopId +
        ", supplierId=" + supplierId +
        ", supplier='" + supplier + '\'' +
        ", customerShopId=" + customerShopId +
        ", customerId=" + customerId +
        ", customer='" + customer + '\'' +
        ", purchaseOrderId=" + purchaseOrderId +
        ", purchaseInventoryId=" + purchaseInventoryId +
        ", salesOrderId=" + salesOrderId +
        ", purchaseInventoryIdStr='" + purchaseInventoryIdStr + '\'' +
        ", purchaseOrderIdStr='" + purchaseOrderIdStr + '\'' +
        ", supplierCommentRecordIdStr='" + supplierCommentRecordIdStr + '\'' +
        ", qualityScore=" + qualityScore +
        ", performanceScore=" + performanceScore +
        ", speedScore=" + speedScore +
        ", attitudeScore=" + attitudeScore +
        ", qualityScoreSpan=" + qualityScoreSpan +
        ", performanceScoreSpan=" + performanceScoreSpan +
        ", speedScoreSpan=" + speedScoreSpan +
        ", attitudeScoreSpan=" + attitudeScoreSpan +
        '}';
  }
}
