package com.bcgogo.notification.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.WXEvent;
import com.bcgogo.wx.WXSendStatusReportDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-20
 * Time: 下午6:28
 */
@Entity
@Table(name = "wx_send_status_report")
public class WXSendStatusReport extends LongIdentifier{
  //群发的消息ID
  private String msgId;
  //公众号的微信号
  private String publicNo;
  //公众号群发助手的微信号，为mphelper
  private String fromUserName;
  //创建时间的时间戳
  private Long createTime;
  private MsgType msgType;
  private WXEvent event;
  private String status;
  //group_id下粉丝数；或者openid_list中的粉丝数
  private int totalCount;
  /**
   * 过滤（过滤是指特定地区、性别的过滤、用户设置拒收的过滤，用户接收已超4条的过滤）后，准备发送的粉丝数，
   * 原则上，FilterCount = SentCount + ErrorCount
   */
  private int filterCount;
  //发送成功的粉丝数
  private int sentCount;
  //发送失败的粉丝数
  private int errorCount;
  private DeletedType deleted=DeletedType.FALSE;

  public WXSendStatusReportDTO toDTO(){
    WXSendStatusReportDTO reportDTO=new WXSendStatusReportDTO();
    reportDTO.setId(getId());
    reportDTO.setCreateTime(getCreateTime());
    reportDTO.setStatus(getStatus());
    reportDTO.setEvent(getEvent());
    reportDTO.setMsgType(getMsgType());
    reportDTO.setPublicNo(getPublicNo());
    reportDTO.setFromUserName(getFromUserName());
    reportDTO.setSentCount(getSentCount());
    reportDTO.setErrorCount(getErrorCount());
    reportDTO.setFilterCount(getFilterCount());
    reportDTO.setTotalCount(getTotalCount());
    reportDTO.setDeleted(getDeleted());
    return reportDTO;
  }

  public void fromDTO(WXSendStatusReportDTO reportDTO){
    this.setId(reportDTO.getId());
    this.setMsgId(reportDTO.getMsgId());
    this.setCreateTime(reportDTO.getCreateTime());
    this.setStatus(reportDTO.getStatus());
    this.setEvent(reportDTO.getEvent());
    this.setMsgType(reportDTO.getMsgType());
    this.setPublicNo(reportDTO.getPublicNo());
    this.setFromUserName(reportDTO.getFromUserName());
    this.setSentCount(reportDTO.getSentCount());
    this.setErrorCount(reportDTO.getErrorCount());
    this.setFilterCount(reportDTO.getFilterCount());
    this.setTotalCount(reportDTO.getTotalCount());
    this.setDeleted(reportDTO.getDeleted());
  }


  @Column(name = "msg_id")
  public String getMsgId() {
    return msgId;
  }

  public void setMsgId(String msgId) {
    this.msgId = msgId;
  }

  @Column(name = "public_no")
  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }



  @Column(name = "from_user_name")
  public String getFromUserName() {
    return fromUserName;
  }

  public void setFromUserName(String fromUserName) {
    this.fromUserName = fromUserName;
  }

  @Column(name = "create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name = "msg_type")
  @Enumerated(EnumType.STRING)
  public MsgType getMsgType() {
    return msgType;
  }

  public void setMsgType(MsgType msgType) {
    this.msgType = msgType;
  }

  @Column(name = "event")
  @Enumerated(EnumType.STRING)
  public WXEvent getEvent() {
    return event;
  }

  public void setEvent(WXEvent event) {
    this.event = event;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    status = status;
  }

  @Column(name = "total_count")
  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }




  @Column(name = "filter_count")
  public int getFilterCount() {
    return filterCount;
  }

  public void setFilterCount(int filterCount) {
    this.filterCount = filterCount;
  }

  @Column(name = "sent_count")
  public int getSentCount() {
    return sentCount;
  }

  public void setSentCount(int sentCount) {
    this.sentCount = sentCount;
  }

  @Column(name = "error_count")
  public int getErrorCount() {
    return errorCount;
  }

  public void setErrorCount(int errorCount) {
    this.errorCount = errorCount;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
