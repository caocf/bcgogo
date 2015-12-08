package com.bcgogo.remind.dto.message;

import com.bcgogo.enums.MessageDayRange;
import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageTopCategory;
import com.bcgogo.utils.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-1-26
 * Time: 下午3:36
 */
public class SearchMessageCondition {
  private Long shopId;
  private Long shopVersionId;
  private Long userId;
  private Long userGroupId;
  private String keyWord;
  private int maxRows = 10;
  private int startPageNo = 1;
  private String sortStatus;
  private String sender;
  private String receiver;
  private List<Long> senderShopId=new ArrayList<Long>();
  private PushMessageReceiverStatus receiverStatus;
  private PushMessageCategory category;
  private PushMessageTopCategory topCategory;
  private MessageDayRange dayRange;
  private String operateType;
  private Long relatedObjectId;

  public Long getRelatedObjectId() {
    return relatedObjectId;
  }

  public void setRelatedObjectId(Long relatedObjectId) {
    this.relatedObjectId = relatedObjectId;
  }

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  public MessageDayRange getDayRange() {
    return dayRange;
  }

  public SearchMessageCondition() {
  }

  public String getKeyWord() {
    return keyWord;
  }

  public void setKeyWord(String keyWord) {
    this.keyWord = keyWord;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }


  public String getOperateType() {
    return operateType;
  }

  public void setOperateType(String operateType) {
    this.operateType = operateType;
  }


  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public List<Long> getSenderShopId() {
    return senderShopId;
  }

  public void setSenderShopId(List<Long> senderShopId) {
    this.senderShopId = senderShopId;
  }

  public PushMessageTopCategory getTopCategory() {
    return topCategory;
  }

  public void setTopCategory(PushMessageTopCategory topCategory) {
    this.topCategory = topCategory;
  }

  public PushMessageCategory getCategory() {
    return category;
  }

  public void setCategory(PushMessageCategory category) {
    this.category = category;
  }

  public PushMessageReceiverStatus getReceiverStatus() {
    return receiverStatus;
  }

  public void setReceiverStatus(PushMessageReceiverStatus receiverStatus) {
    this.receiverStatus = receiverStatus;
  }

  public void setDayRange(MessageDayRange dayRange) {
    this.dayRange = dayRange;
  }
}
