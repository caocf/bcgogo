package com.bcgogo.txn.dto;

import com.bcgogo.product.dto.ProductDTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-6-6
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class InventoryLimitDTO {
  private Long shopId;
  private ProductDTO[] productDTOs;
  private Integer previousLowerLimitAmount;
  private Integer afterLowerLimitAmount;
  private Integer previousUpperLimitAmount;
  private Integer afterUpperLimitAmount;
  private Integer currentLowerLimitAmount;
  private Integer currentUpperLimitAmount;
  private Integer lowerLimitChangeAmount;
  private Integer upperLimitChangeAmount;

  public ProductDTO[] getProductDTOs() {
    return productDTOs;
  }

  public void setProductDTOs(ProductDTO[] productDTOs) {
    this.productDTOs = productDTOs;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Integer getPreviousLowerLimitAmount() {
    return previousLowerLimitAmount;
  }

  public void setPreviousLowerLimitAmount(Integer previousLowerLimitAmount) {
    this.previousLowerLimitAmount = previousLowerLimitAmount;
  }

  public Integer getAfterLowerLimitAmount() {
    return afterLowerLimitAmount;
  }

  public void setAfterLowerLimitAmount(Integer afterLowerLimitAmount) {
    this.afterLowerLimitAmount = afterLowerLimitAmount;
  }

  public Integer getPreviousUpperLimitAmount() {
    return previousUpperLimitAmount;
  }

  public void setPreviousUpperLimitAmount(Integer previousUpperLimitAmount) {
    this.previousUpperLimitAmount = previousUpperLimitAmount;
  }

  public Integer getAfterUpperLimitAmount() {
    return afterUpperLimitAmount;
  }

  public void setAfterUpperLimitAmount(Integer afterUpperLimitAmount) {
    this.afterUpperLimitAmount = afterUpperLimitAmount;
  }

  public Integer getCurrentLowerLimitAmount() {
    return currentLowerLimitAmount;
  }

  public void setCurrentLowerLimitAmount(Integer currentLowerLimitAmount) {
    this.currentLowerLimitAmount = currentLowerLimitAmount;
  }

  public Integer getCurrentUpperLimitAmount() {
    return currentUpperLimitAmount;
  }

  public void setCurrentUpperLimitAmount(Integer currentUpperLimitAmount) {
    this.currentUpperLimitAmount = currentUpperLimitAmount;
  }

  public Integer getLowerLimitChangeAmount() {
    return lowerLimitChangeAmount;
  }

  public void setLowerLimitChangeAmount(Integer lowerLimitChangeAmount) {
    this.lowerLimitChangeAmount = lowerLimitChangeAmount;
  }

  public Integer getUpperLimitChangeAmount() {
    return upperLimitChangeAmount;
  }

  public void setUpperLimitChangeAmount(Integer upperLimitChangeAmount) {
    this.upperLimitChangeAmount = upperLimitChangeAmount;
  }
}
