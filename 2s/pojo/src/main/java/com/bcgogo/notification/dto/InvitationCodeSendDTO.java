package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.user.dto.ContactDTO;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-18
 * Time: 下午5:57
 */
public class InvitationCodeSendDTO {
  private Long shopId;
  private String mobile;
  private String code, transformedCode;
  private SenderType sender;
  private InvitationCodeType invitationCodeType;
  private Long sendTime;
  private boolean checkingDuplicated = true;
  private int invitationCodeSendTimes = -1;  //发送次数检查
  private Integer pageSize = 1000;
  private Long createTime;                      //(根据创建时间筛选）
  private String smsInvitationCodeTemplateStrategy = "CUSTOMER";//CUSTOMER SUPPLIER
  private List<String> eliminateMobileList = new ArrayList<String>();//  排除 mobile
  private List<Long> eliminateShopIdList = new ArrayList<Long>();
  private SmsSendScene smsSendScene;
  private List<ContactDTO> contactDTOs;

  private boolean needCode = true;

  public boolean isCustomerSmsInvitationCodeTemplate() {
    return smsInvitationCodeTemplateStrategy.equals("CUSTOMER");
  }

  public boolean isSupplierSmsInvitationCodeTemplate() {
    return !isCustomerSmsInvitationCodeTemplate();
  }

  public void setCustomerSmsInvitationCodeTemplate() {
    smsInvitationCodeTemplateStrategy = "CUSTOMER";
  }

  public void setSupplierSmsInvitationCodeTemplate() {
    smsInvitationCodeTemplateStrategy = "SUPPLIER";
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  public boolean isCheckingDuplicated() {
    return checkingDuplicated;
  }

  public void setCheckingDuplicated(boolean checkingDuplicated) {
    this.checkingDuplicated = checkingDuplicated;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getTransformedCode() {
    return transformedCode;
  }

  public void setTransformedCode(String transformedCode) {
    this.transformedCode = transformedCode;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    if (StringUtils.isNotEmpty(code)) {
//      StringBuilder builder = new StringBuilder();
//      builder.append(code.substring(0, 1)).append(code.substring(1, 3).toUpperCase()).append(code.substring(3, 5)).append(code.substring(5, 6).toUpperCase());
//      setTransformedCode(builder.toString());
      setTransformedCode("邀请码:" + code.toUpperCase() +";");
    }
    this.code = code;
  }

  public SenderType getSender() {
    return sender;
  }

  public void setSender(SenderType sender) {
    this.sender = sender;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public InvitationCodeType getInvitationCodeType() {
    return invitationCodeType;
  }

  public void setInvitationCodeType(InvitationCodeType invitationCodeType) {
    this.invitationCodeType = invitationCodeType;
  }

  public int getInvitationCodeSendTimes() {
    return invitationCodeSendTimes;
  }

  public void setInvitationCodeSendTimes(int invitationCodeSendTimes) {
    this.invitationCodeSendTimes = invitationCodeSendTimes;
  }

  public String getSmsInvitationCodeTemplateStrategy() {
    return smsInvitationCodeTemplateStrategy;
  }

  public void setSmsInvitationCodeTemplateStrategy(String smsInvitationCodeTemplateStrategy) {
    this.smsInvitationCodeTemplateStrategy = smsInvitationCodeTemplateStrategy;
  }

  public List<String> getEliminateMobileList() {
    return eliminateMobileList;
  }

  public void setEliminateMobileList(List<String> eliminateMobileList) {
    this.eliminateMobileList = eliminateMobileList;
  }

  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }

  public List<ContactDTO> getContactDTOs() {
    return contactDTOs;
  }

  public void setContactDTOs(List<ContactDTO> contactDTOs) {
    this.contactDTOs = contactDTOs;
  }

  public List<Long> getEliminateShopIdList() {
    return eliminateShopIdList;
  }

  public void setEliminateShopIdList(List<Long> eliminateShopIdList) {
    this.eliminateShopIdList = eliminateShopIdList;
  }

  public boolean isNeedCode() {
    return needCode;
  }

  public void setNeedCode(boolean needCode) {
    this.needCode = needCode;
  }

  @Override
  public String toString() {
    return "InvitationCodeSendDTO{" +
        "shopId=" + shopId +
        ", mobile='" + mobile + '\'' +
        ", code='" + code + '\'' +
        ", transformedCode='" + transformedCode + '\'' +
        ", sender=" + sender +
        ", invitationCodeType=" + invitationCodeType +
        ", sendTime=" + sendTime +
        ", checkingDuplicated=" + checkingDuplicated +
        ", invitationCodeSendTimes=" + invitationCodeSendTimes +
        ", pageSize=" + pageSize +
        ", createTime=" + createTime +
        ", smsInvitationCodeTemplateStrategy='" + smsInvitationCodeTemplateStrategy + '\'' +
        ", eliminateMobileList=" + eliminateMobileList +
        ", smsSendScene=" + smsSendScene +
        ", eliminateShopIdList=" + eliminateShopIdList +
        ", needCode" + needCode +
        '}';
  }

}
