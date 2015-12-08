package com.bcgogo.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.WXEvent;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-20
 * Time: 下午7:12
 */
public class WXSendStatusReportDTO {
  private Long id;
  private String msgId;
  private String publicNo;
  private String fromUserName;
  private Long createTime;
  private MsgType msgType;
  private WXEvent event;
  private String status;
  private int totalCount;
  private int filterCount;
  private int sentCount;
  private int errorCount;
  private DeletedType deleted=DeletedType.FALSE;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMsgId() {
    return msgId;
  }

  public void setMsgId(String msgId) {
    this.msgId = msgId;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public String getFromUserName() {
    return fromUserName;
  }

  public void setFromUserName(String fromUserName) {
    this.fromUserName = fromUserName;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public MsgType getMsgType() {
    return msgType;
  }

  public void setMsgType(MsgType msgType) {
    this.msgType = msgType;
  }

  public WXEvent getEvent() {
    return event;
  }

  public void setEvent(WXEvent event) {
    this.event = event;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }

  public int getFilterCount() {
    return filterCount;
  }

  public void setFilterCount(int filterCount) {
    this.filterCount = filterCount;
  }

  public int getSentCount() {
    return sentCount;
  }

  public void setSentCount(int sentCount) {
    this.sentCount = sentCount;
  }

  public int getErrorCount() {
    return errorCount;
  }

  public void setErrorCount(int errorCount) {
    this.errorCount = errorCount;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
