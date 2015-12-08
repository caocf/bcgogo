package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.InvitationCodeStatus;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.OperatorType;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午8:10
 */
public class InvitationCodeDTO {
  private Long id;
  private InvitationCodeType type;
  private Long inviterId;
  private OperatorType inviterType;  //邀请人类型
  private Long inviteeId;
  private OperatorType inviteeType;  //被邀请人类型
  private InvitationCodeStatus status;
  private String code;
  private Long expirationTime;
  private Long inviteTime;
  private Long usageTime;
  private String mobile;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getInviterId() {
    return inviterId;
  }

  public void setInviterId(Long inviterId) {
    this.inviterId = inviterId;
  }

  public OperatorType getInviterType() {
    return inviterType;
  }

  public void setInviterType(OperatorType inviterType) {
    this.inviterType = inviterType;
  }

  public Long getInviteeId() {
    return inviteeId;
  }

  public void setInviteeId(Long inviteeId) {
    this.inviteeId = inviteeId;
  }

  public OperatorType getInviteeType() {
    return inviteeType;
  }

  public void setInviteeType(OperatorType inviteeType) {
    this.inviteeType = inviteeType;
  }

  public InvitationCodeStatus getStatus() {
    return status;
  }

  public void setStatus(InvitationCodeStatus status) {
    this.status = status;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(Long expirationTime) {
    this.expirationTime = expirationTime;
  }

  public Long getInviteTime() {
    return inviteTime;
  }

  public void setInviteTime(Long inviteTime) {
    this.inviteTime = inviteTime;
  }

  public Long getUsageTime() {
    return usageTime;
  }

  public void setUsageTime(Long usageTime) {
    this.usageTime = usageTime;
  }

  public InvitationCodeType getType() {
    return type;
  }

  public void setType(InvitationCodeType type) {
    this.type = type;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
