package com.bcgogo.txn.model.app;

import com.bcgogo.api.EnquiryShopResponseDTO;
import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-23
 * Time: 上午10:07
 */
@Entity
@Table(name = "enquiry_shop_response")
public class EnquiryShopResponse extends LongIdentifier {

  private Long enquiryId;
  private Long shopId;
  private String shopName;
  private String responseMsg;
  private Long responseTime;
  private EnquiryShopResponseStatus status; //shop处理的状态

  @Column(name = "enquiry_id")
  public Long getEnquiryId() {
    return enquiryId;
  }

  public void setEnquiryId(Long enquiryId) {
    this.enquiryId = enquiryId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "shop_name")
  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  @Column(name = "response_msg")
  public String getResponseMsg() {
    return responseMsg;
  }

  public void setResponseMsg(String responseMsg) {
    this.responseMsg = responseMsg;
  }

  @Column(name = "response_time")
  public Long getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(Long responseTime) {
    this.responseTime = responseTime;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public EnquiryShopResponseStatus getStatus() {
    return status;
  }

  public void setStatus(EnquiryShopResponseStatus status) {
    this.status = status;
  }



  public EnquiryShopResponseDTO toDTO(){
    EnquiryShopResponseDTO enquiryShopResponseDTO = new EnquiryShopResponseDTO();
    enquiryShopResponseDTO.setId(getId());
    enquiryShopResponseDTO.setEnquiryId(getEnquiryId());
    enquiryShopResponseDTO.setResponseMsg(getResponseMsg());
    enquiryShopResponseDTO.setResponseTime(getResponseTime());
    enquiryShopResponseDTO.setShopId(getShopId());
    enquiryShopResponseDTO.setShopName(getShopName());
    enquiryShopResponseDTO.setStatus(getStatus());
    return enquiryShopResponseDTO;
  }

  public void fromDTO(EnquiryShopResponseDTO enquiryShopResponseDTO){
    if(enquiryShopResponseDTO != null){
      this.setId(enquiryShopResponseDTO.getId());
      this.setEnquiryId(enquiryShopResponseDTO.getEnquiryId());
      this.setResponseMsg(enquiryShopResponseDTO.getResponseMsg());
      this.setResponseTime(enquiryShopResponseDTO.getResponseTime());
      this.setShopId(enquiryShopResponseDTO.getShopId());
      this.setShopName(enquiryShopResponseDTO.getShopName());
      this.setStatus(enquiryShopResponseDTO.getStatus());
    }
  }
}
