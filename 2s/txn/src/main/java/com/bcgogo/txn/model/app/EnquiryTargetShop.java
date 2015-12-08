package com.bcgogo.txn.model.app;

import com.bcgogo.api.EnquiryTargetShopDTO;
import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.enums.app.EnquiryTargetShopStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-23
 * Time: 上午9:44
 */
@Entity
@Table(name = "enquiry_target_shop")
public class EnquiryTargetShop extends LongIdentifier {
  private Long enquiryId;
  private Long targetShopId;
  private String targetShopName;
  private int sequence;//发送店铺的顺序，前台展示用
  private EnquiryTargetShopStatus status;   //appUser处理的状态
  private String receiptNo;//发送给店家后生成的单据号
  private Long sendTime;//发送时间
  private Long lastResponseTime;//最后报价时间
  private EnquiryShopResponseStatus shopResponseStatus; //shop处理的状态


  @Column(name = "enquiry_id")
  public Long getEnquiryId() {
    return enquiryId;
  }

  public void setEnquiryId(Long enquiryId) {
    this.enquiryId = enquiryId;
  }

  @Column(name = "target_shop_id")
  public Long getTargetShopId() {
    return targetShopId;
  }

  public void setTargetShopId(Long targetShopId) {
    this.targetShopId = targetShopId;
  }

  @Column(name = "target_shop_name")
  public String getTargetShopName() {
    return targetShopName;
  }

  public void setTargetShopName(String targetShopName) {
    this.targetShopName = targetShopName;
  }

  @Column(name = "sequence")
  public int getSequence() {
    return sequence;
  }

  public void setSequence(int sequence) {
    this.sequence = sequence;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public EnquiryTargetShopStatus getStatus() {
    return status;
  }

  public void setStatus(EnquiryTargetShopStatus status) {
    this.status = status;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "send_time")
  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  @Column(name = "last_response_time")
  public Long getLastResponseTime() {
    return lastResponseTime;
  }

  public void setLastResponseTime(Long lastResponseTime) {
    this.lastResponseTime = lastResponseTime;
  }

  @Column(name = "shop_response_status")
  @Enumerated(EnumType.STRING)
  public EnquiryShopResponseStatus getShopResponseStatus() {
    return shopResponseStatus;
  }

  public void setShopResponseStatus(EnquiryShopResponseStatus shopResponseStatus) {
    this.shopResponseStatus = shopResponseStatus;
  }

  public EnquiryTargetShopDTO toDTO(){
    EnquiryTargetShopDTO enquiryTargetShopDTO = new EnquiryTargetShopDTO();
    enquiryTargetShopDTO.setId(getId());
    enquiryTargetShopDTO.setEnquiryId(getEnquiryId());
    enquiryTargetShopDTO.setTargetShopName(getTargetShopName());
    enquiryTargetShopDTO.setSequence(getSequence());
    enquiryTargetShopDTO.setStatus(getStatus());
    enquiryTargetShopDTO.setReceiptNo(getReceiptNo());
    enquiryTargetShopDTO.setSendTime(getSendTime());
    enquiryTargetShopDTO.setTargetShopId(getTargetShopId());
    enquiryTargetShopDTO.setShopResponseStatus(getShopResponseStatus());
    enquiryTargetShopDTO.setLastResponseTime(getLastResponseTime());
    return enquiryTargetShopDTO;
  }

  public void saveFromDTO(EnquiryTargetShopDTO enquiryTargetShopDTO){
    if(enquiryTargetShopDTO != null){
      this.setId(enquiryTargetShopDTO.getId());
      this.setEnquiryId(enquiryTargetShopDTO.getEnquiryId());
      this.setTargetShopName(enquiryTargetShopDTO.getTargetShopName());
      this.setSequence(enquiryTargetShopDTO.getSequence());
      this.setStatus(enquiryTargetShopDTO.getStatus());
      this.setReceiptNo(enquiryTargetShopDTO.getReceiptNo());
      this.setSendTime(enquiryTargetShopDTO.getSendTime());
      this.setTargetShopId(enquiryTargetShopDTO.getTargetShopId());
      this.setShopResponseStatus(enquiryTargetShopDTO.getShopResponseStatus());
      this.setLastResponseTime(enquiryTargetShopDTO.getLastResponseTime());
    }
  }

  //不包含更新 id,targetShopId
  public void updateFromDTO(EnquiryTargetShopDTO enquiryTargetShopDTO){
    if(enquiryTargetShopDTO != null){
      this.setEnquiryId(enquiryTargetShopDTO.getEnquiryId());
      this.setTargetShopName(enquiryTargetShopDTO.getTargetShopName());
      this.setSequence(enquiryTargetShopDTO.getSequence());
      this.setStatus(enquiryTargetShopDTO.getStatus());
      this.setReceiptNo(enquiryTargetShopDTO.getReceiptNo());
      this.setSendTime(enquiryTargetShopDTO.getSendTime());
      this.setShopResponseStatus(enquiryTargetShopDTO.getShopResponseStatus());
      this.setLastResponseTime(enquiryTargetShopDTO.getLastResponseTime());
    }
  }
}
