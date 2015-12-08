package com.bcgogo.notification.model;

import com.bcgogo.enums.notification.InvitationCodeStatus;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.InvitationCodeDTO;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午10:41
 */
@MappedSuperclass
public class AbstractInvitationCode extends LongIdentifier {
  private InvitationCodeType type;
  private Long inviterId;                //邀请者
  private OperatorType inviterType;
  private Long inviteeId;              //被邀请者
  private OperatorType inviteeType;
  private InvitationCodeStatus status;
  private String code;
  private Long inviteTime;              //邀请时间
  private Long expirationTime;          //失效时间 null代表永久有效
  private Long usageTime;               //使用时间

  @Column(name = "inviter_id")
  public Long getInviterId() {
    return inviterId;
  }

  public void setInviterId(Long inviterId) {
    this.inviterId = inviterId;
  }

  @Column(name = "inviter_type")
  @Enumerated(EnumType.STRING)
  public OperatorType getInviterType() {
    return inviterType;
  }

  public void setInviterType(OperatorType inviterType) {
    this.inviterType = inviterType;
  }

  @Column(name = "invitee_id")
  public Long getInviteeId() {
    return inviteeId;
  }

  public void setInviteeId(Long inviteeId) {
    this.inviteeId = inviteeId;
  }

  @Column(name = "invitee_type")
  @Enumerated(EnumType.STRING)
  public OperatorType getInviteeType() {
    return inviteeType;
  }

  public void setInviteeType(OperatorType inviteeType) {
    this.inviteeType = inviteeType;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public InvitationCodeStatus getStatus() {
    return status;
  }

  public void setStatus(InvitationCodeStatus status) {
    this.status = status;
  }

  @Column(name = "code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name = "expiration_time")
  public Long getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(Long expirationTime) {
    this.expirationTime = expirationTime;
  }

  @Column(name = "invite_time")
  public Long getInviteTime() {
    return inviteTime;
  }

  public void setInviteTime(Long inviteTime) {
    this.inviteTime = inviteTime;
  }

  @Column(name = "usage_time")
  public Long getUsageTime() {
    return usageTime;
  }

  public void setUsageTime(Long usageTime) {
    this.usageTime = usageTime;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public InvitationCodeType getType() {
    return type;
  }

  public void setType(InvitationCodeType type) {
    this.type = type;
  }
}

