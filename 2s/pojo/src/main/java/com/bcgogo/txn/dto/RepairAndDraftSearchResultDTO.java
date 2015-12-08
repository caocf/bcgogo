package com.bcgogo.txn.dto;

import com.bcgogo.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-4
 * Time: 下午4:25
 * To change this template use File | Settings | File Templates.
 */
public class RepairAndDraftSearchResultDTO {
  private String idStr;
  private String receiptNo;
  private String saveTimeStr;
  private String customerName;
  private String vechicle;
  private String material;
  private String serviceContent;
  private String orderType;
  private String materialStr;
  private String serviceContentStr;

  public String getServiceContentStr() {
    return serviceContentStr;
  }

  public void setServiceContentStr(String serviceContentStr) {
    this.serviceContentStr = serviceContentStr;
  }

  public String getMaterialStr() {

    return materialStr;
  }

  public void setMaterialStr(String materialStr) {
    this.materialStr = materialStr;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getServiceContent() {

    return serviceContent;
  }

  public void setServiceContent(String serviceContent) {
    this.serviceContent = serviceContent;
    if (!StringUtil.isEmpty(this.getServiceContent()) && this.getServiceContent().length() > 15) {
      this.setServiceContentStr(this.getServiceContent().substring(0, 15) + "...");
    } else {
      this.setServiceContentStr(this.getServiceContent());
    }
  }

  public String getMaterial() {

    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
    if (!StringUtil.isEmpty(this.getMaterial()) && this.getMaterial().length() > 15) {
      this.setMaterialStr(this.getMaterial().substring(0, 15) + "...");
    } else {
      this.setMaterialStr(this.getMaterial());
    }
  }

  public String getVechicle() {

    return vechicle;
  }

  public void setVechicle(String vechicle) {
    this.vechicle = vechicle;
  }

  public String getCustomerName() {

    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getSaveTimeStr() {
    return saveTimeStr;
  }

  public void setSaveTimeStr(String saveTimeStr) {
    this.saveTimeStr = saveTimeStr;
  }

  public String getReceiptNo() {

    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getIdStr() {

    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }
}
