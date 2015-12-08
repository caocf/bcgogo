package com.bcgogo.txn.model.message;

import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.enums.MessageValidTimePeriod;
import com.bcgogo.remind.dto.message.MessageDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-9
 * Time: 上午9:47
 * 站内消息
 */
@Deprecated
@Entity
@Table(name = "message")
public class Message extends AbstractMessage {
  private MessageType type;    //消息类型
  private MessageValidTimePeriod validTimePeriod;
  private Long validDateFrom;   //有效时间开始
  private Long validDateTo;   //有效时间结束
  private String sender;       //消息发送人
  private Long editorId;
  private String editor;
  private Long editDate;
  private String productIds;
  private Long shopId;

  public MessageDTO toDTO() {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setContent(this.getContent());
    messageDTO.setEditDate(this.getEditDate());
    messageDTO.setEditor(this.getEditor());
    messageDTO.setEditorId(this.getEditorId());
    messageDTO.setId(this.getId());
    messageDTO.setSender(this.getSender());
    messageDTO.setShopId(this.getShopId());
    messageDTO.setType(this.getType());
    messageDTO.setValidDateFrom(this.getValidDateFrom());
    messageDTO.setValidDateTo(this.getValidDateTo());
    messageDTO.setValidTimePeriod(this.getValidTimePeriod());
    messageDTO.setProductIds(this.getProductIds());
    messageDTO.setStatus(this.getStatus());
    return messageDTO;
  }

  public Message formDTO(MessageDTO messageDTO) {
    this.setShopId(messageDTO.getShopId());
    this.setContent(messageDTO.getContent());
    this.setEditDate(messageDTO.getEditDate());
    this.setEditor(messageDTO.getEditor());
    this.setEditorId(messageDTO.getEditorId());
    this.setId(messageDTO.getId());
    this.setSender(messageDTO.getSender());
    this.setType(messageDTO.getType());
    this.setValidDateFrom(messageDTO.getValidDateFrom());
    this.setValidDateTo(messageDTO.getValidDateTo());
    this.setValidTimePeriod(messageDTO.getValidTimePeriod());
    this.setProductIds(messageDTO.getProductIds());
    this.setStatus(messageDTO.getStatus());
    return this;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "sender")
  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
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

  @Column(name = "editor_id")
  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  @Column(name = "editor")
  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "valid_time_period")
  @Enumerated(EnumType.STRING)
  public MessageValidTimePeriod getValidTimePeriod() {
    return validTimePeriod;
  }

  public void setValidTimePeriod(MessageValidTimePeriod validTimePeriod) {
    this.validTimePeriod = validTimePeriod;
  }

  @Column(name = "product_ids")
  public String getProductIds() {
    return productIds;
  }

  public void setProductIds(String productIds) {
    this.productIds = productIds;
  }


}
