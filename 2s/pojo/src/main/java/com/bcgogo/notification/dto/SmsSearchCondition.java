package com.bcgogo.notification.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.user.dto.ContactDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-26
 * Time: 上午9:37
 * To change this template use File | Settings | File Templates.
 */
public class SmsSearchCondition {
  private Long id;
  private Long shopId;
  private String keyWord;
  private String contactGroupIds;
  private String contactIds;
  private List<ContactGroupDTO> contactGroupDTOs;
  private List<ContactDTO> contactDTOs;
  private String content;
  private Long sendTime;
  private String sendTimeStr;
  private SmsType smsType;
  private Long startTime;
  private Long endTime;
  private int startPageNo;
  private int pageSize;
  private Pager pager;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getKeyWord() {
    return keyWord;
  }

  public void setKeyWord(String keyWord) {
    this.keyWord = keyWord;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  public String getSendTimeStr() {
    return sendTimeStr;
  }

  public void setSendTimeStr(String sendTimeStr) {
    this.sendTimeStr = sendTimeStr;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public SmsType getSmsType() {
    return smsType;
  }

  public void setSmsType(SmsType smsType) {
    this.smsType = smsType;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }
}
