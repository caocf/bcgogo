package com.bcgogo.txn.model.message;

import com.bcgogo.enums.txn.message.NoticeType;
import com.bcgogo.remind.dto.message.NoticeDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午3:27
 */
@Deprecated
@Entity
@Table(name = "notice")
public class Notice extends AbstractMessage {
  private Long shopRelationInviteId;
  private String receiverIds;     //接收者的供应商或客户
  private Long senderShopId;
  private Long receiverShopId;
  private NoticeType noticeType;
  private Long requestTime;

  public Notice fromDTO(NoticeDTO dto) {
    if (dto == null) return this;
    this.setId(dto.getId());
    this.setContent(dto.getContent());
    this.setReceiverIds(dto.getReceiverIds());
    this.setShopRelationInviteId(dto.getShopRelationInviteId());
    this.setSenderShopId(dto.getSenderShopId());
    this.setNoticeType(dto.getNoticeType());
    this.setRequestTime(dto.getRequestTime());
    this.setStatus(dto.getStatus());
    this.setReceiverShopId(dto.getReceiverShopId());
    return this;
  }

  public NoticeDTO toDTO() {
    NoticeDTO dto = new NoticeDTO();
    dto.setId(this.getId());
    dto.setContent(this.getContent());
    dto.setReceiverIds(this.getReceiverIds());
    dto.setShopRelationInviteId(this.getShopRelationInviteId());
    dto.setSenderShopId(this.getSenderShopId());
    dto.setNoticeType(this.getNoticeType());
    dto.setRequestTime(this.getRequestTime());
    dto.setStatus(this.getStatus());
    return dto;
  }


  @Column(name = "receiver_ids")
  public String getReceiverIds() {
    return receiverIds;
  }

  public void setReceiverIds(String receiverIds) {
    this.receiverIds = receiverIds;
  }

  @Column(name = "sender_shop_id")
  public Long getSenderShopId() {
    return senderShopId;
  }

  public void setSenderShopId(Long senderShopId) {
    this.senderShopId = senderShopId;
  }

  @Column(name = "shop_relation_invite_id")
  public Long getShopRelationInviteId() {
    return shopRelationInviteId;
  }

  public void setShopRelationInviteId(Long shopRelationInviteId) {
    this.shopRelationInviteId = shopRelationInviteId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "notice_type")
  public NoticeType getNoticeType() {
    return noticeType;
  }

  public void setNoticeType(NoticeType noticeType) {
    this.noticeType = noticeType;
  }

  @Column(name = "request_time")
  public Long getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(Long requestTime) {
    this.requestTime = requestTime;
  }

  @Column(name = "receiver_shop_id")
  public Long getReceiverShopId() {
    return receiverShopId;
  }

  public void setReceiverShopId(Long receiverShopId) {
    this.receiverShopId = receiverShopId;
  }
}
