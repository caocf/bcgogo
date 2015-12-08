package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.api.PushAppMessageDTO;
import com.bcgogo.constant.MQConstant;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.MessageValidTimePeriod;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.enums.txn.message.Status;
import com.bcgogo.enums.txn.pushMessage.PushMessageLevel;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.mq.message.MQMessageItemDTO;
import com.bcgogo.remind.dto.message.MessageDTO;
import com.bcgogo.remind.dto.message.NoticeDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-9
 * Time: 上午9:47
 * 推送消息
 */
@Entity
@Table(name = "push_message")
public class PushMessage extends LongIdentifier {
  private String title;
  private Long shopId;
  private Long creatorId;
  private OperatorType creatorType;
  private Long relatedObjectId;
  private String content;
  private String promptContent;
  private Long endDate;
  private Long createTime;
  private PushMessageType type;
  @Deprecated
  private String redirectUrl;
  private Integer level;
  private String params;  //url 中参数放不下可使用此字段

  //MessageReceiver 移植来
  private MessageValidTimePeriod validTimePeriod;
  private Long validDateFrom;   //有效时间开始
  private Long validDateTo;   //有效时间结束
  private String creator;       //消息发送人
  private DeletedType deleted;
  private String contentText;//纯文本  没有 html 供搜索


  @Column(name = "prompt_content")
  public String getPromptContent() {
    return promptContent;
  }

  public void setPromptContent(String promptContent) {
    this.promptContent = promptContent;
  }

  @Column(name = "content_text")
  public String getContentText() {
    return contentText;
  }

  public void setContentText(String contentText) {
    this.contentText = contentText;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  @Column(name = "valid_time_period")
  @Enumerated(EnumType.STRING)
  public MessageValidTimePeriod getValidTimePeriod() {
    return validTimePeriod;
  }

  public void setValidTimePeriod(MessageValidTimePeriod validTimePeriod) {
    this.validTimePeriod = validTimePeriod;
  }

  @Column(name = "valid_date_from")
  public Long getValidDateFrom() {
    return validDateFrom;
  }

  public void setValidDateFrom(Long validDateFrom) {
    this.validDateFrom = validDateFrom;
  }

  @Column(name = "valid_date_to")
  public Long getValidDateTo() {
    return validDateTo;
  }

  public void setValidDateTo(Long validDateTo) {
    this.validDateTo = validDateTo;
  }

  @Column(name = "creator")
  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  @Column(name = "title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name = "create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public PushMessageType getType() {
    return type;
  }

  public void setType(PushMessageType type) {
    this.type = type;
  }

  @Column(name = "redirect_url")
  public String getRedirectUrl() {
    return redirectUrl;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }


  @Column(name = "level")
  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  @Column(name = "params")
  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  @Column(name = "related_object_id")
  public Long getRelatedObjectId() {
    return relatedObjectId;
  }

  public void setRelatedObjectId(Long relatedObjectId) {
    this.relatedObjectId = relatedObjectId;
  }

  @Column(name = "creator_id")
  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "creator_type")
  public OperatorType getCreatorType() {
    return creatorType;
  }

  public void setCreatorType(OperatorType creatorType) {
    this.creatorType = creatorType;
  }

  public void fromDTO(PushMessageDTO pushMessageDTO) {
    this.setShopId(pushMessageDTO.getShopId());
    this.setEndDate(pushMessageDTO.getEndDate());
    this.setCreateTime(pushMessageDTO.getCreateTime());
    this.setContent(pushMessageDTO.getContent());
    this.setPromptContent(pushMessageDTO.getPromptContent());
    this.setTitle(pushMessageDTO.getTitle());
    this.setType(pushMessageDTO.getType());
    this.setParams(pushMessageDTO.getParams());
    this.setLevel(pushMessageDTO.getLevel() == null ? null : pushMessageDTO.getLevel().getValue());
    this.setRelatedObjectId(pushMessageDTO.getRelatedObjectId());
    this.setCreatorId(pushMessageDTO.getCreatorId());
    this.setCreatorType(pushMessageDTO.getCreatorType());
    this.setCreator(pushMessageDTO.getCreator());
    this.setValidDateFrom(pushMessageDTO.getValidDateFrom());
    this.setValidDateTo(pushMessageDTO.getValidDateTo());
    this.setValidTimePeriod(pushMessageDTO.getValidTimePeriod());
    this.setDeleted(pushMessageDTO.getDeleted());
    this.setContentText(pushMessageDTO.getContentText());
  }

  public PushMessageDTO toDTO() {
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setId(this.getId());
    pushMessageDTO.setShopId(this.getShopId());
    pushMessageDTO.setEndDate(this.getEndDate());
    pushMessageDTO.setCreateTime(this.getCreateTime());
    pushMessageDTO.setContent(this.getContent());
    pushMessageDTO.setPromptContent(this.getPromptContent());
    pushMessageDTO.setTitle(this.getTitle());
    pushMessageDTO.setType(this.getType());
    pushMessageDTO.setParams(this.getParams());
    pushMessageDTO.setLevel(this.getLevel() != null ? PushMessageLevel.valueOf(this.getLevel()) : null);
    pushMessageDTO.setRelatedObjectId(this.getRelatedObjectId());
    pushMessageDTO.setCreatorId(this.getCreatorId());
    pushMessageDTO.setCreatorType(this.getCreatorType());
    pushMessageDTO.setCreator(this.getCreator());
    pushMessageDTO.setValidDateFrom(this.getValidDateFrom());
    pushMessageDTO.setValidDateTo(this.getValidDateTo());
    pushMessageDTO.setValidTimePeriod(this.getValidTimePeriod());
    pushMessageDTO.setDeleted(this.getDeleted());
    pushMessageDTO.setContentText(this.getContentText());
    return pushMessageDTO;
  }


  public void fromMessageDTO(MessageDTO messageDTO) {
    this.setShopId(messageDTO.getShopId());
    this.setCreateTime(messageDTO.getEditDate());
    this.setContent(messageDTO.getContent());
    this.setType(PushMessageType.valueOf(messageDTO.getType().toString()));
    Map<String, String> paramsMap = new HashMap<String, String>();
    paramsMap.put(PushMessageParamsKeyConstant.ProductLocalInfoIds, messageDTO.getProductIds());
    this.setParams(JsonUtil.mapToJson(paramsMap));
    this.setCreatorId(messageDTO.getEditorId());
    this.setCreatorType(OperatorType.SHOP);
    this.setCreator(messageDTO.getEditor());
    this.setValidDateFrom(messageDTO.getValidDateFrom());
    this.setValidDateTo(messageDTO.getValidDateTo());
    this.setValidTimePeriod(messageDTO.getValidTimePeriod());
    if (Status.ACTIVE.equals(messageDTO.getStatus())) {
      this.setDeleted(DeletedType.FALSE);
    } else {
      this.setDeleted(DeletedType.TRUE);
    }
    this.setContentText(messageDTO.getContentText());
  }

  public MessageDTO toMessageDTO() {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setContent(this.getContent());
    messageDTO.setEditDate(this.getCreateTime());
    messageDTO.setEditor(this.getCreator());
    messageDTO.setEditorId(this.getCreatorId());
    messageDTO.setId(this.getId());
//    messageDTO.setSender(this.get());
    messageDTO.setShopId(this.getShopId());
    messageDTO.setType(MessageType.valueOf(this.getType().toString()));
    messageDTO.setValidDateFrom(this.getValidDateFrom());
    messageDTO.setValidDateTo(this.getValidDateTo());
    messageDTO.setValidTimePeriod(this.getValidTimePeriod());
    Map<String, String> paramsMap = JsonUtil.jsonToStringMap(this.getParams());
    messageDTO.setProductIds(paramsMap.get(PushMessageParamsKeyConstant.ProductLocalInfoIds));
    if (DeletedType.TRUE.equals(this.getDeleted())) {
      messageDTO.setStatus(Status.DELETED);
    } else {
      messageDTO.setStatus(Status.ACTIVE);
    }

    return messageDTO;
  }

  public void fromNoticeDTO(NoticeDTO noticeDTO) {
    if (Status.ACTIVE.equals(noticeDTO.getStatus())) {
      this.setDeleted(DeletedType.FALSE);
    } else {
      this.setDeleted(DeletedType.TRUE);
    }
    this.setContent(noticeDTO.getContent());
    this.setContentText(noticeDTO.getContentText());
    this.setType(PushMessageType.valueOf(noticeDTO.getNoticeType().toString()));
    this.setCreateTime(noticeDTO.getRequestTime());
    this.setCreatorType(OperatorType.SHOP);
    this.setShopId(noticeDTO.getSenderShopId());
    this.setRelatedObjectId(noticeDTO.getShopRelationInviteId());
    if (StringUtils.isNotBlank(noticeDTO.getReceiverIds())) {
      Map<String, String> map = new HashMap<String, String>();
      if (PushMessageType.CUSTOMER_ACCEPT_TO_SUPPLIER.equals(this.getType()) || PushMessageType.SUPPLIER_ACCEPT_TO_SUPPLIER.equals(this.getType())) {
        map.put(PushMessageParamsKeyConstant.SimilarCustomerIds, noticeDTO.getReceiverIds());//是否存在相似相同的客户 需要合并
        map.put(PushMessageParamsKeyConstant.CustomerId, noticeDTO.getCustomerId().toString());
        map.put(PushMessageParamsKeyConstant.ShopId, noticeDTO.getOriginShopId().toString());
      } else if (PushMessageType.SUPPLIER_ACCEPT_TO_CUSTOMER.equals(this.getType()) || PushMessageType.CUSTOMER_ACCEPT_TO_CUSTOMER.equals(this.getType())) {
        map.put(PushMessageParamsKeyConstant.SimilarSupplierIds, noticeDTO.getReceiverIds());//是否存在相似相同的供应商 需要合并
        map.put(PushMessageParamsKeyConstant.SupplierId, noticeDTO.getSupplierId().toString());
        map.put(PushMessageParamsKeyConstant.ShopId, noticeDTO.getOriginShopId().toString());
      }
      this.setParams(JsonUtil.mapToJson(map));
    }

  }


  public PushAppMessageDTO toPushAppMessageDTO(boolean isCommented) {

    return new PushAppMessageDTO(getId(),
      getPromptContent(), getType(), getParams(), getTitle(), isCommented, createTime);
  }

  public MQMessageItemDTO toMQMessageItemDTO() {
    MQMessageItemDTO itemDTO = new MQMessageItemDTO();
    itemDTO.setMsgId(getId());
    itemDTO.setTitle(getTitle());
    itemDTO.setContent(getPromptContent());
    itemDTO.setCreateTime(createTime);
    if(getType()!=null){
      itemDTO.setType(NumberUtil.intValue(MQConstant.pushMessageTypeMap.get(getType().toString()),0));
    }
    return itemDTO;
  }

  @Override
  public String toString() {
    return "PushMessage{" +
      "title='" + title + '\'' +
      ", shopId=" + shopId +
      ", creatorId=" + creatorId +
      ", creatorType=" + creatorType +
      ", relatedObjectId=" + relatedObjectId +
      ", content='" + content + '\'' +
      ", endDate=" + endDate +
      ", createTime=" + createTime +
      ", type=" + type +
      ", redirectUrl='" + redirectUrl + '\'' +
      ", level=" + level +
      ", params='" + params + '\'' +
      '}';
  }

}