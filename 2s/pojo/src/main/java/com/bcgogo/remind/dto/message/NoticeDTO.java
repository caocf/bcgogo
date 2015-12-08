package com.bcgogo.remind.dto.message;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.enums.txn.message.NoticeType;
import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午3:27
 */
public class NoticeDTO extends AbstractMessageDTO {
  private Long shopRelationInviteId;
  private String shopRelationInviteIdStr;
  private Long noticeReceiverId;
  private String noticeReceiverIdStr;
  private ReceiverStatus noticeReceiverStatus;
  private String receiverIds;       //本店的供应商或客户  多客户供应商 合并供应商使用
  private Long senderShopId;
  private String senderShopIdStr;
  private Long receiverShopId;
  private String receiverShopIdStr;
  private NoticeType noticeType;
  private Long requestTime;
  private Long userId;
  private String userIdStr;
  private Long supplierId;
  private Long customerId;

  private Long originShopId;

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getReceiverShopId() {
    return receiverShopId;
  }

  public void setReceiverShopId(Long receiverShopId) {
    if (receiverShopId != null) this.setReceiverShopIdStr(receiverShopId.toString());
    this.receiverShopId = receiverShopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    if (userId != null) this.setUserIdStr(userId.toString());
    this.userId = userId;
  }

  public Long getShopRelationInviteId() {
    return shopRelationInviteId;
  }

  public void setShopRelationInviteId(Long shopRelationInviteId) {
    if (shopRelationInviteId != null) this.setShopRelationInviteIdStr(shopRelationInviteId.toString());
    this.shopRelationInviteId = shopRelationInviteId;
  }

  public String getReceiverIds() {
    return receiverIds;
  }

  public void setReceiverIds(String receiverIds) {
    this.receiverIds = receiverIds;
  }

  public Long getSenderShopId() {
    return senderShopId;
  }

  public void setSenderShopId(Long senderShopId) {
    if (senderShopId != null) this.setSenderShopIdStr(senderShopId.toString());
    this.senderShopId = senderShopId;
  }

  public NoticeType getNoticeType() {
    return noticeType;
  }

  public void setNoticeType(NoticeType noticeType) {
    this.noticeType = noticeType;
  }

  public Long getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(Long requestTime) {
    this.requestTime = requestTime;
  }

  public String getSenderShopIdStr() {
    return senderShopIdStr;
  }

  public void setSenderShopIdStr(String senderShopIdStr) {
    this.senderShopIdStr = senderShopIdStr;
  }

  public void setSameCustomers(CustomerDTO customerDTO, List<CustomerDTO> similarCustomers) {
    StringBuffer sb = new StringBuffer();
    if(customerDTO != null) {
      sb.append(customerDTO.getId().toString());
    }
    if(CollectionUtils.isNotEmpty(similarCustomers)){
      for(CustomerDTO temp : similarCustomers){
        if(temp.getId() == null){
          continue;
        }
        if(StringUtils.isBlank(sb.toString())){
          sb.append(temp.getId().toString());
        }else {
          sb.append(",").append(temp.getId().toString());
        }
      }
    }
    this.setReceiverIds(sb.toString());
    this.setCustomerId(customerDTO.getId());
  }

  public void setSameSuppliers(SupplierDTO supplierDTO, List<SupplierDTO> similarSuppliers) {
    StringBuffer sb = new StringBuffer();
    if(supplierDTO != null) {
      sb.append(supplierDTO.getId().toString());
    }
    if(CollectionUtils.isNotEmpty(similarSuppliers)){
      for(SupplierDTO temp : similarSuppliers){
        if(temp.getId() == null){
          continue;
        }
        if(StringUtils.isBlank(sb.toString())){
          sb.append(temp.getId().toString());
        }else {
          sb.append(",").append(temp.getId().toString());
        }
      }
    }
    this.setReceiverIds(sb.toString());
    this.setSupplierId(supplierDTO.getId());
  }
  public String getReceiverShopIdStr() {
    return receiverShopIdStr;
  }

  public void setReceiverShopIdStr(String receiverShopIdStr) {
    this.receiverShopIdStr = receiverShopIdStr;
  }

  public String getUserIdStr() {
    return userIdStr;
  }

  public void setUserIdStr(String userIdStr) {
    this.userIdStr = userIdStr;
  }

  public String getShopRelationInviteIdStr() {
    return shopRelationInviteIdStr;
  }

  public void setShopRelationInviteIdStr(String shopRelationInviteIdStr) {
    this.shopRelationInviteIdStr = shopRelationInviteIdStr;
  }

  public Long getNoticeReceiverId() {
    return noticeReceiverId;
  }

  public void setNoticeReceiverId(Long noticeReceiverId) {
    if (noticeReceiverId != null) this.setNoticeReceiverIdStr(noticeReceiverId.toString());
    this.noticeReceiverId = noticeReceiverId;
  }

  public String getNoticeReceiverIdStr() {
    return noticeReceiverIdStr;
  }

  public void setNoticeReceiverIdStr(String noticeReceiverIdStr) {
    this.noticeReceiverIdStr = noticeReceiverIdStr;
  }

  public ReceiverStatus getNoticeReceiverStatus() {
    return noticeReceiverStatus;
  }

  public void setNoticeReceiverStatus(ReceiverStatus noticeReceiverStatus) {
    this.noticeReceiverStatus = noticeReceiverStatus;
  }

  public Long getOriginShopId() {
    return originShopId;
  }

  public void setOriginShopId(Long originShopId) {
    this.originShopId = originShopId;
  }
}
