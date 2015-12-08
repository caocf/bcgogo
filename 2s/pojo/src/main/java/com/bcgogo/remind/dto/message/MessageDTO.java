package com.bcgogo.remind.dto.message;

import com.bcgogo.enums.MessageValidTimePeriod;
import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.remind.dto.MessageReceiverDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-9
 * Time: 上午10:30
 */
public class MessageDTO extends AbstractMessageDTO {
  private MessageType type;    //消息类型
  private String typeName;
  private MessageValidTimePeriod validTimePeriod;
  private Long validDateFrom;    //有效时间开始
  private String validDateFromStr;
  private String validDateToStr;  //有效时间结束
  private Long validDateTo;
  private String sender;     //消息发送人
  private Long editorId;
  private String editorIdStr;
  private Long receiverId;
  private String receiverIdStr;
  private String editor;
  private Long editDate;
  private String editDateStr;
  private String messageReceivers;  //消息接收人ID 字符串
  private List<MessageReceiverDTO> messageReceiverDTOList;
  private Long  promotionsId;
  private String promotionsIdStr;
  private String productIds;
  private boolean smsFlag = false;     //是否发送手机短信
  private Long shopId;
  private Long messageReceiverId;      //针对接收消息
  private String messageReceiverIdStr;      //针对接收消息
  private Long messageUserReceiverId; //针对接收消息
  private String messageUserReceiverIdStr; //针对接收消息
  private ReceiverStatus receiverStatus;
  private List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>(); //针对发送消息

  private String sendTimeStr;

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
    if (type != null) {
      this.typeName = type.getName();
    }
  }

  public Long getValidDateFrom() {
    return validDateFrom;
  }

  public void setValidDateFrom(Long validDateFrom) {
    this.validDateFromStr = DateUtil.convertDateLongToString(validDateFrom, DateUtil.DATE_STRING_FORMAT_DAY);
    this.validDateFrom = validDateFrom;
  }

  public Long getValidDateTo() {
    return validDateTo;
  }

  public void setValidDateTo(Long validDateTo) {
    this.validDateToStr = DateUtil.convertDateLongToString(validDateTo, DateUtil.DATE_STRING_FORMAT_DAY);
    this.validDateTo = validDateTo;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    if (editorId != null) this.setEditorIdStr(editorId.toString());
    this.editorId = editorId;
  }

  public String getEditorIdStr() {
    return editorIdStr;
  }

  public void setEditorIdStr(String editorIdStr) {
    this.editorIdStr = editorIdStr;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public String getReceiverIdStr() {
    return receiverIdStr;
  }

  public void setReceiverIdStr(String receiverIdStr) {
    this.receiverIdStr = receiverIdStr;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDateStr = DateUtil.convertDateLongToString(editDate, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN);
    this.editDate = editDate;
  }

  public String getMessageReceivers() {
    return messageReceivers;
  }

  public void setMessageReceivers(String messageReceivers) {
    this.messageReceivers = messageReceivers;
  }

  public boolean isSmsFlag() {
    return smsFlag;
  }

  public void setSmsFlag(boolean smsFlag) {
    this.smsFlag = smsFlag;
  }

  public List<MessageReceiverDTO> getMessageReceiverDTOList() {
    return messageReceiverDTOList;
  }

  public void setMessageReceiverDTOList(List<MessageReceiverDTO> messageReceiverDTOList) {
    this.messageReceiverDTOList = messageReceiverDTOList;
  }

  public MessageValidTimePeriod getValidTimePeriod() {
    return validTimePeriod;
  }

  public void setValidTimePeriod(MessageValidTimePeriod validTimePeriod) {
    this.validTimePeriod = validTimePeriod;
  }

  public String getValidDateFromStr() {
    return validDateFromStr;
  }

  public void setValidDateFromStr(String validDateFromStr) {
    this.validDateFromStr = validDateFromStr;
  }

  public String getValidDateToStr() {
    return validDateToStr;
  }

  public void setValidDateToStr(String validDateToStr) {
    this.validDateToStr = validDateToStr;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public String getProductIds() {
    return productIds;
  }

  public void setProductIds(String productIds) {
    this.productIds = productIds;
  }

  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
    this.setPromotionsIdStr(StringUtil.valueOf(promotionsId));
  }

  public String getPromotionsIdStr() {
    return promotionsIdStr;
  }

  public void setPromotionsIdStr(String promotionsIdStr) {
    this.promotionsIdStr = promotionsIdStr;
  }

  public Long getMessageReceiverId() {
    return messageReceiverId;
  }

  public void setMessageReceiverId(Long messageReceiverId) {
    if(messageReceiverId!=null)this.setMessageReceiverIdStr(messageReceiverId.toString());
    this.messageReceiverId = messageReceiverId;
  }

  public Long getMessageUserReceiverId() {
    return messageUserReceiverId;
  }

  public void setMessageUserReceiverId(Long messageUserReceiverId) {
    if(messageUserReceiverId!=null)this.setMessageUserReceiverIdStr(messageUserReceiverId.toString());
    this.messageUserReceiverId = messageUserReceiverId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public List<CustomerDTO> getCustomerDTOList() {
    return customerDTOList;
  }

  public void setCustomerDTOList(List<CustomerDTO> customerDTOList) {
    this.customerDTOList = customerDTOList;
  }

  public String getSendTimeStr() {
    return sendTimeStr;
  }

  public void setSendTimeStr(String sendTimeStr) {
    this.sendTimeStr = sendTimeStr;
  }

  public String getMessageReceiverIdStr() {
    return messageReceiverIdStr;
  }

  public void setMessageReceiverIdStr(String messageReceiverIdStr) {
    this.messageReceiverIdStr = messageReceiverIdStr;
  }

  public String getMessageUserReceiverIdStr() {
    return messageUserReceiverIdStr;
  }

  public void setMessageUserReceiverIdStr(String messageUserReceiverIdStr) {
    this.messageUserReceiverIdStr = messageUserReceiverIdStr;
  }

  public ReceiverStatus getReceiverStatus() {
    return receiverStatus;
  }

  public void setReceiverStatus(ReceiverStatus receiverStatus) {
    this.receiverStatus = receiverStatus;
  }
}
