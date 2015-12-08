package com.bcgogo.notification.model;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.utils.SmsConstant;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 24/2/13
 * Time: 12:31 PM
 */
@Entity
@Table(name = "sms_sending_times")
public class SmsSendingTimes extends LongIdentifier {
  private String mobile;
  private Integer invitationCodeSendingTimes;

  public SmsSendingTimes() {
  }

  public SmsSendingTimes(String mobile, int invitationCodeSendingTimes) {
    this.setMobile(mobile);
    this.setInvitationCodeSendingTimes(invitationCodeSendingTimes);
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "invitation_code_sending_times")
  public Integer getInvitationCodeSendingTimes() {
    return invitationCodeSendingTimes;
  }

  public void setInvitationCodeSendingTimes(Integer invitationCodeSendingTimes) {
    this.invitationCodeSendingTimes = invitationCodeSendingTimes;
  }

  public void invitationCodeSendingTimesIncrementing() {
    invitationCodeSendingTimes++;
  }
}
