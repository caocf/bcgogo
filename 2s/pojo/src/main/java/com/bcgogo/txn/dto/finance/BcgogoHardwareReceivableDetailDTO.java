package com.bcgogo.txn.dto.finance;

import com.bcgogo.utils.StringUtil;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 下午4:07
 */
public class BcgogoHardwareReceivableDetailDTO {
  private Long shopId;
  private Long payeeId;              //收款人       只有线下才会有
  private Long paymentTime;         //收款时间
  private String createdTimeStr;
  private Long operatorId;          //操作人
  private Double readerPrice;
  private Double scanningGunPrice;
  private Double membershipCardPrice;
  private Integer readerNum;
  private Integer scanningGunNum;
  private Integer membershipCardNum;
  private Double actualTotalPrice = 0.0; //实际价格
  private Double discountTotalPrice;     //优惠价
  private String content;

  public void assemblyContent() {
    StringBuilder content = new StringBuilder();
    boolean start = true;
    if (readerPrice != null && readerNum != null) {
      content.append("读卡器").append(readerNum).append("台");
      start = false;
    }
    if (scanningGunPrice != null && scanningGunNum != null) {
      if (!start) {
        content.append("、");
        start = false;
      }
      content.append("扫描枪").append(scanningGunNum).append("台");
    }
    if (membershipCardPrice != null && membershipCardNum != null) {
      if (!start) {
        content.append("、");
      }
      content.append("会员卡").append(membershipCardNum * 1000).append("张");
    }
    content.append("【总额").append("￥").append(actualTotalPrice).append("】");
    this.setContent(content.toString());
  }

  public Double getReaderPrice() {
    return readerPrice;
  }

  public void setReaderPrice(Double readerPrice) {
    this.readerPrice = readerPrice;
  }

  public Double getScanningGunPrice() {
    return scanningGunPrice;
  }

  public void setScanningGunPrice(Double scanningGunPrice) {
    this.scanningGunPrice = scanningGunPrice;
  }

  public Double getMembershipCardPrice() {
    return membershipCardPrice;
  }

  public void setMembershipCardPrice(Double membershipCardPrice) {
    this.membershipCardPrice = membershipCardPrice;
  }

  public Integer getReaderNum() {
    return readerNum;
  }

  public void setReaderNum(Integer readerNum) {
    this.readerNum = readerNum;
  }

  public Integer getScanningGunNum() {
    return scanningGunNum;
  }

  public void setScanningGunNum(Integer scanningGunNum) {
    this.scanningGunNum = scanningGunNum;
  }

  public Integer getMembershipCardNum() {
    return membershipCardNum;
  }

  public void setMembershipCardNum(Integer membershipCardNum) {
    this.membershipCardNum = membershipCardNum;
  }

  public Double getDiscountTotalPrice() {
    return discountTotalPrice;
  }

  public void setDiscountTotalPrice(Double discountTotalPrice) {
    this.discountTotalPrice = discountTotalPrice;
  }

  public Double getActualTotalPrice() {
    return actualTotalPrice;
  }

  public void setActualTotalPrice(Double actualTotalPrice) {
    this.actualTotalPrice = actualTotalPrice;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public String getCreatedTimeStr() {
    return createdTimeStr;
  }

  public void setCreatedTimeStr(String createdTimeStr) {
    this.createdTimeStr = createdTimeStr;
  }
}
