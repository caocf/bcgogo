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
@Table(name = "invitation_code")
public class InvitationCode extends AbstractInvitationCode {
  public InvitationCodeDTO toDto() {
    InvitationCodeDTO dto = new InvitationCodeDTO();
    dto.setType(this.getType());
    dto.setCode(this.getCode());
    dto.setInviterType(this.getInviterType());
    dto.setInviteeId(this.getInviteeId());
    dto.setInviteeType(this.getInviteeType());
    dto.setInviterId(this.getInviterId());
    dto.setExpirationTime(this.getExpirationTime());
    dto.setInviteTime(this.getInviteTime());
    dto.setStatus(this.getStatus());
    dto.setId(this.getId());
    dto.setUsageTime(this.getUsageTime());
    return dto;
  }

  public InvitationCode fromDto(InvitationCodeDTO dto) {
    this.setType(dto.getType());
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
    return this;
  }
}
