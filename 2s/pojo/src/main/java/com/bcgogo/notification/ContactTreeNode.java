package com.bcgogo.notification;

import com.bcgogo.common.Node;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-1-11
 * Time: 下午5:58
 * To change this template use File | Settings | File Templates.
 */
public class ContactTreeNode extends Node{
  private boolean groupNodeFlag=false;
  private Long contactId;
  private String specialIdStr;
  private Long contactGroupId;
  private String contactGroupIdStr;
  private String contactName;
  private String groupName;
  private String mobile;
  private ContactGroupType sGroupType; //前台选择联系人所属组
  private boolean appCustomerFlag=false;
  private Integer nodeNum;

  public ContactTreeNode(){}

  public ContactTreeNode(Long id, Long pId, String name, boolean parent, boolean open){
   super(id,pId,name,parent,open);
  }

  public boolean isGroupNodeFlag() {
    return groupNodeFlag;
  }

  public void setGroupNodeFlag(boolean groupNodeFlag) {
    this.groupNodeFlag = groupNodeFlag;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  public String getSpecialIdStr() {
    return specialIdStr;
  }

  public void setSpecialIdStr(String specialIdStr) {
    this.specialIdStr = specialIdStr;
  }

  public Long getContactGroupId() {
    return contactGroupId;
  }

  public void setContactGroupId(Long contactGroupId) {
    this.contactGroupId = contactGroupId;
    this.contactGroupIdStr= StringUtil.valueOf(contactGroupId);
  }

  public String getContactGroupIdStr() {
    return contactGroupIdStr;
  }

  public void setContactGroupIdStr(String contactGroupIdStr) {
    this.contactGroupIdStr = contactGroupIdStr;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public ContactGroupType getsGroupType() {
    return sGroupType;
  }

  public void setsGroupType(ContactGroupType sGroupType) {
    this.sGroupType = sGroupType;
  }

  public boolean isAppCustomerFlag() {
    return appCustomerFlag;
  }

  public void setAppCustomerFlag(boolean appCustomerFlag) {
    this.appCustomerFlag = appCustomerFlag;
  }

  public Integer getNodeNum() {
    return nodeNum;
  }

  public void setNodeNum(Integer nodeNum) {
    this.nodeNum = nodeNum;
  }
}
