package com.bcgogo.user.model.app;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.RegistrationDTO;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.enums.DataKind;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.text.ParseException;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "app_user")
public class AppUser extends LongIdentifier {
  private static final Logger LOG = LoggerFactory.getLogger(AppUser.class);
  private String appUserNo;//用户账号
  private String password;//密码
  private String mobile;//手机号码
  private String email;//邮箱
  private String name;//用户名
  private Long registrationShopId;//注册店铺Id
  private Status status = Status.active;// 登陆状态
  private Long sendSMSDate;
  private Integer sendSMSTimes = 0;
  private Long lastExpenseShopId;//最后消费店铺id
  private DataKind dataKind = DataKind.OFFICIAL;
  private Double oilPrice;//油价
  private String oilKind;//油品

  private AppUserType appUserType; //用户类型
  private String deviceToken;//ios 用户的deviceToken
  private String umDeviceToken;//安卓 用户的友盟deviceToken

  public AppUser() {
    super();
  }

  public AppUser(RegistrationDTO dto) {
    this.setAppUserNo(dto.getUserNo());
    this.setPassword(dto.getPassword());
    this.setName(dto.getName());
    this.setMobile(dto.getMobile());
    this.setRegistrationShopId(dto.getShopId());
    this.setAppUserType(AppUserType.BLUE_TOOTH);
  }

  public AppUser(GSMRegisterDTO dto) {
    this.setAppUserNo(dto.getUserNo());
    this.setPassword(dto.getPassword());
    this.setName(dto.getMobile());
    this.setMobile(dto.getMobile());
    LOG.info("GSMRegisterDTO appUserType:{}", dto.getAppUserType());
    if (AppUserType.POBD.equals(dto.getAppUserType())) {
      this.setAppUserType(AppUserType.POBD);
    } else if (AppUserType.SGSM.equals(dto.getAppUserType())) {
      this.setAppUserType(AppUserType.SGSM);
    } else {
      this.setAppUserType(AppUserType.GSM);
    }
    if (dto.getLoginInfo() != null) {
      this.setDeviceToken(dto.getLoginInfo().getDeviceToken());
      this.setUmDeviceToken(dto.getLoginInfo().getUmDeviceToken());
    }
  }


  public AppUser fromDTO(AppUserDTO dto) {
    this.setId(dto.getId());
    this.setAppUserNo(dto.getUserNo());
    this.setPassword(dto.getPassword());
    this.setName(dto.getName());
    this.setMobile(dto.getMobile());
    this.setEmail(dto.getEmail());
    this.setRegistrationShopId(dto.getRegistrationShopId());
    this.setLastExpenseShopId(dto.getLastExpenseShopId());
    this.setDataKind(dto.getDataKind());
    this.setAppUserType(dto.getAppUserType());
    this.setDeviceToken(dto.getDeviceToken());
    this.setUmDeviceToken(dto.getUmDeviceToken());

    return this;
  }

  public AppUserDTO toDTO() {
    AppUserDTO dto = new AppUserDTO();
    dto.setUserNo(getAppUserNo());
    dto.setPassword(getPassword());
    dto.setName(getName());
    dto.setMobile(getMobile());
    dto.setEmail(getEmail());
    dto.setRegistrationShopId(getRegistrationShopId());
    dto.setLastExpenseShopId(getLastExpenseShopId());
    dto.setId(getId());
    dto.setDataKind(getDataKind());
    dto.setAppUserType(getAppUserType());
    dto.setDeviceToken(getDeviceToken());
    dto.setUmDeviceToken(getUmDeviceToken());

    return dto;
  }

  public boolean isSendSMSLimited(int limit) {
    if (sendSMSDate == null) return false;
    if (sendSMSTimes < limit) return false;
    try {
      if (DateUtil.getTheDayTime() > sendSMSDate) {
        return false;
      }
    } catch (ParseException e) {
      LOG.error(e.getMessage(), e);
      return false;
    }
    return true;
  }

  public void addSendSMSLimited() {
    if (sendSMSDate == null) {
      sendSMSDate = System.currentTimeMillis();
      if (sendSMSTimes == null) {
        sendSMSTimes = 0;
      }
      sendSMSTimes++;
      return;
    }
    try {
      if (DateUtil.getTheDayTime() > sendSMSDate) {
        sendSMSDate = System.currentTimeMillis();
        sendSMSTimes = 0;
      }
      sendSMSTimes++;
    } catch (ParseException e) {
      LOG.error(e.getMessage(), e);
    }
  }


  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "registration_shop_id")
  public Long getRegistrationShopId() {
    return registrationShopId;
  }

  public void setRegistrationShopId(Long registrationShopId) {
    this.registrationShopId = registrationShopId;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "send_sms_date")
  public Long getSendSMSDate() {
    return sendSMSDate;
  }

  public void setSendSMSDate(Long sendSMSDate) {
    this.sendSMSDate = sendSMSDate;
  }

  @Column(name = "send_sms_times")
  public Integer getSendSMSTimes() {
    return sendSMSTimes;
  }

  public void setSendSMSTimes(Integer sendSMSTimes) {
    this.sendSMSTimes = sendSMSTimes;
  }

  @Column(name = "last_expense_shop_id")
  public Long getLastExpenseShopId() {
    return lastExpenseShopId;
  }

  public void setLastExpenseShopId(Long lastExpenseShopId) {
    this.lastExpenseShopId = lastExpenseShopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "data_kind")
  public DataKind getDataKind() {
    return dataKind;
  }

  public void setDataKind(DataKind dataKind) {
    this.dataKind = dataKind;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type")
  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }

  @Column(name = "device_token")
  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  @Column(name = "um_device_token")
  public String getUmDeviceToken() {
    return umDeviceToken;
  }

  public void setUmDeviceToken(String umDeviceToken) {
    this.umDeviceToken = umDeviceToken;
  }
}
