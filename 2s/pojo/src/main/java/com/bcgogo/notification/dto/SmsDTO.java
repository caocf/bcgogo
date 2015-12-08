package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-24
 * Time: 下午2:37
 * To change this template use File | Settings | File Templates.
 */
public class SmsDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long userId;
  private Long smsDraftId;
  private String smsDraftIdStr;
  private String userName;
  private String contactGroupIds;
  private String contactIds;
  private List<ContactGroupDTO> contactGroupDTOs;
  private List<ContactDTO> contactDTOs;
  private List<CustomerDTO> customerDTOs;
  private List<SupplierDTO> supplierDTOs;
  private String content;
  private Long editDate;
  private Long sendTime;
  private String sendTimeStr;
  private SmsType smsType;
  private SmsSendScene smsSendScene;
  private Boolean appFlag;
  private Boolean smsFlag;
  private Boolean templateFlag;
  private int rowStart;
  private int smsTotalNum;
  private SmsIndexDTO smsIndexDTO;
  private Integer countSmsSent;
  private Integer countAppSent;
  private List<String> appUserNos;

  public SmsDTO(){}

  public SmsDTO(Long shopId,Long userId){
    this.shopId=shopId;
    this.userId=userId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.setIdStr(StringUtil.valueOf(id));
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getSmsDraftId() {
    return smsDraftId;
  }

  public void setSmsDraftId(Long smsDraftId) {
    this.smsDraftId = smsDraftId;
    this.smsDraftIdStr=StringUtil.valueOf(smsDraftId);
  }

  public String getSmsDraftIdStr() {
    return smsDraftIdStr;
  }

  public void setSmsDraftIdStr(String smsDraftIdStr) {
    this.smsDraftIdStr = smsDraftIdStr;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getContactGroupIds() {
    return contactGroupIds;
  }

  public void setContactGroupIds(String contactGroupIds) {
    this.contactGroupIds = contactGroupIds;
  }

  public String getContactIds() {
    return contactIds;
  }

  public void setContactIds(String contactIds) {
    this.contactIds = contactIds;
  }

  public List<ContactGroupDTO> getContactGroupDTOs() {
    return contactGroupDTOs;
  }

  public void setContactGroupDTOs(List<ContactGroupDTO> contactGroupDTOs) {
    this.contactGroupDTOs = contactGroupDTOs;
  }

  public List<ContactDTO> getContactDTOs() {
    return contactDTOs;
  }

  public void setContactDTOs(List<ContactDTO> contactDTOs) {
    this.contactDTOs = contactDTOs;
  }

  public List<CustomerDTO> getCustomerDTOs() {
    return customerDTOs;
  }

  public void setCustomerDTOs(List<CustomerDTO> customerDTOs) {
    this.customerDTOs = customerDTOs;
  }

  public List<SupplierDTO> getSupplierDTOs() {
    return supplierDTOs;
  }

  public void setSupplierDTOs(List<SupplierDTO> supplierDTOs) {
    this.supplierDTOs = supplierDTOs;
  }

  public String getSendTimeStr() {
    return sendTimeStr;
  }

  public void setSendTimeStr(String sendTimeStr) {
    this.sendTimeStr = sendTimeStr;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
    this.sendTimeStr= DateUtil.convertDateLongToDateString(DateUtil.STANDARD,sendTime);
  }

  public SmsType getSmsType() {
    return smsType;
  }

  public void setSmsType(SmsType smsType) {
    this.smsType = smsType;
  }

  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }

  public Boolean getAppFlag() {
    return appFlag;
  }

  public void setAppFlag(Boolean appFlag) {
    this.appFlag = appFlag;
  }

  public Boolean getSmsFlag() {
    return smsFlag;
  }

  public void setSmsFlag(Boolean smsFlag) {
    this.smsFlag = smsFlag;
  }

  public Boolean getTemplateFlag() {
    return templateFlag;
  }

  public void setTemplateFlag(Boolean templateFlag) {
    this.templateFlag = templateFlag;
  }

  public int getRowStart() {
    return rowStart;
  }

  public void setRowStart(int rowStart) {
    this.rowStart = rowStart;
  }

  public int getSmsTotalNum() {
    return smsTotalNum;
  }

  public void setSmsTotalNum(int smsTotalNum) {
    this.smsTotalNum = smsTotalNum;
  }

  public SmsIndexDTO getSmsIndexDTO() {
    return smsIndexDTO;
  }

  public void setSmsIndexDTO(SmsIndexDTO smsIndexDTO) {
    this.smsIndexDTO = smsIndexDTO;
  }

  public Integer getCountSmsSent() {
    return countSmsSent;
  }

  public void setCountSmsSent(Integer countSmsSent) {
    this.countSmsSent = countSmsSent;
  }

  public Integer getCountAppSent() {
    return countAppSent;
  }

  public void setCountAppSent(Integer countAppSent) {
    this.countAppSent = countAppSent;
  }

  public List<String> getAppUserNos() {
    return appUserNos;
  }

  public void setAppUserNos(List<String> appUserNos) {
    this.appUserNos = appUserNos;
  }
}
