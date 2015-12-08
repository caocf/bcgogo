package com.bcgogo.wx;

import com.bcgogo.utils.NumberUtil;
import com.bcgogo.wx.message.resp.BaseMsg;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-27
 * Time: 上午11:35
 */
public class WXRequestParam implements Serializable {
  private String publicNo;
  private String openId;
  private Long createTime;
  private String content;
  private WXEvent wxEvent;
  private String eventKey;
  private MsgType msgType;
  private String msgId;
  private String status;
  //mass msg status report
  private int totalCount;
  private int filterCount;
  private int sentCount;
  private int errorCount;




  public void setReqContent(Map<String, String> requestMap) {
    if (requestMap == null) return;
    setOpenId(requestMap.get("FromUserName"));
    setPublicNo(requestMap.get("ToUserName"));
    setCreateTime(NumberUtil.longValue(requestMap.get("CreateTime")));
    setContent(requestMap.get("Content"));
    setWxEvent(requestMap.get("Event"));
    setEventKey(requestMap.get("EventKey"));
    setMsgType(requestMap.get("MsgType"));
    setMsgId(requestMap.get("MsgID"));
    setStatus(requestMap.get("Status"));
    if (WXEvent.MASS_SEND_JOB_FINISH.equals(getWxEvent())) {
      setTotalCount(NumberUtil.intValue(requestMap.get("TotalCount")));
      setFilterCount(NumberUtil.intValue(requestMap.get("FilterCount")));
      setSentCount(NumberUtil.intValue(requestMap.get("SentCount")));
      setErrorCount(NumberUtil.intValue(requestMap.get("ErrorCount")));
    }
  }

  public BaseMsg toBaseMsg() {
    BaseMsg baseMsg = new BaseMsg();
    baseMsg.setFromUserName(getPublicNo());
    baseMsg.setToUserName(getOpenId());
    baseMsg.setCreateTime(getCreateTime());
    baseMsg.setMsgType(MsgType.transfer_customer_service);
    return baseMsg;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getMsgId() {
    return msgId;
  }

  public void setMsgId(String msgId) {
    this.msgId = msgId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public WXEvent getWxEvent() {
    return wxEvent;
  }

  public void setWxEvent(String wxEventStr) {
    this.wxEvent = WXEvent.getWXEvent(wxEventStr);
  }

  public String getEventKey() {
    return eventKey;
  }

  public void setEventKey(String eventKey) {
    this.eventKey = eventKey;
  }

  public MsgType getMsgType() {
    return msgType;
  }

  public void setMsgType(String msgTypeStr) {
    this.msgType = MsgType.getType(msgTypeStr);
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


}
