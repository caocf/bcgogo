package com.bcgogo.txn.model.message;

import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午4:51
 */
@Deprecated
@Entity
@Table(name = "notice_receiver")
public class NoticeReceiver extends LongIdentifier {
  private Long receiverUserId;
  private Long noticeId;
  private ReceiverStatus status;

  public NoticeReceiver() {
  }

  public NoticeReceiver(Long receiverUserId, Long noticeId) {
    this.receiverUserId = receiverUserId;
    this.noticeId = noticeId;
  }

  @Column(name = "receiver_user_id")
  public Long getReceiverUserId() {
    return receiverUserId;
  }

  public void setReceiverUserId(Long receiverUserId) {
    this.receiverUserId = receiverUserId;
  }

  @Column(name = "notice_id")
  public Long getNoticeId() {
    return noticeId;
  }

  public void setNoticeId(Long noticeId) {
    this.noticeId = noticeId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ReceiverStatus getStatus() {
    return status;
  }

  public void setStatus(ReceiverStatus status) {
    this.status = status;
  }
}
