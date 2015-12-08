package com.bcgogo.notification.model;

import com.bcgogo.enums.notification.InvitationCodeStatus;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.InvitationCodeDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午4:11
 */
@Entity
@Table(name = "invitation_code_recycle")
public class InvitationCodeRecycle extends AbstractInvitationCode {
  public void fromInvitationCodeDTO(InvitationCodeDTO dto) {
    this.setCode(dto.getCode());
    this.setInviterType(dto.getInviterType());
    this.setInviteeId(dto.getInviteeId());
    this.setInviteeType(dto.getInviteeType());
    this.setInviterId(dto.getInviterId());
    this.setExpirationTime(dto.getExpirationTime());
    this.setInviteTime(dto.getInviteTime());
    this.setStatus(dto.getStatus());
    this.setId(dto.getId());
    this.setUsageTime(dto.getUsageTime());
  }

  public InvitationCodeRecycle fromInvitationCode(InvitationCode code) {
    this.setCode(code.getCode());
    this.setInviterType(code.getInviterType());
    this.setInviteeId(code.getInviteeId());
    this.setInviteeType(code.getInviteeType());
    this.setInviterId(code.getInviterId());
    this.setExpirationTime(code.getExpirationTime());
    this.setInviteTime(code.getInviteTime());
    this.setStatus(code.getStatus());
    this.setUsageTime(code.getUsageTime());
    return this;
  }
}
