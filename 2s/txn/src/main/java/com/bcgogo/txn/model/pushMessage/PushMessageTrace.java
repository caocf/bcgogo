package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.MessageValidTimePeriod;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.pushMessage.PushMessageLevel;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 13-7-1
 * Time: 上午9:47
 * 推送消息 回收
 */
@Entity
@Table(name = "push_message_trace")
public class PushMessageTrace extends LongIdentifier {
  private String title;
  private Long shopId;
  private Long creatorId;
  private OperatorType creatorType;
  private String content;
  private String promptContent;
  private Long endDate;
  private Long createTime;
  private PushMessageType type;
  private String redirectUrl;
  private Integer level;
  private String params;  //url 中参数放不下可使用此字段
  ////MessageReceiver 移植来
  private MessageValidTimePeriod validTimePeriod;
  private Long validDateFrom;   //有效时间开始
  private Long validDateTo;   //有效时间结束
  private String creator;       //消息发送人
  private DeletedType deleted;
  private String contentText;//纯文本  没有 html 供搜索

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


  public PushMessageTrace(PushMessageDTO pushMessageDTO) {
    this.setShopId(pushMessageDTO.getShopId());
    this.setEndDate(pushMessageDTO.getEndDate());
    this.setCreateTime(pushMessageDTO.getCreateTime());
    this.setContent(pushMessageDTO.getContent());
    this.setRedirectUrl(pushMessageDTO.getRedirectUrl());
    this.setTitle(pushMessageDTO.getTitle());
    this.setType(pushMessageDTO.getType());
    this.setParams(pushMessageDTO.getParams());
    this.setLevel(pushMessageDTO.getLevel().getValue());
    this.setId(pushMessageDTO.getId());
    this.setCreatorId(pushMessageDTO.getCreatorId());
    this.setCreatorType(pushMessageDTO.getCreatorType());
    this.setValidDateFrom(pushMessageDTO.getValidDateFrom());
    this.setValidDateTo(pushMessageDTO.getValidDateTo());
    this.setValidTimePeriod(pushMessageDTO.getValidTimePeriod());
    this.setDeleted(pushMessageDTO.getDeleted());
    this.setCreator(pushMessageDTO.getCreator());
    this.setContentText(pushMessageDTO.getContentText());
    this.setPromptContent(pushMessageDTO.getPromptContent());

  }

  @Column(name = "prompt_content")
  public String getPromptContent() {
    return promptContent;
  }

  public void setPromptContent(String promptContent) {
    this.promptContent = promptContent;
  }
}