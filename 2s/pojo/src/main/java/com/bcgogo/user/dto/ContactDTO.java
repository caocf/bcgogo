package com.bcgogo.user.dto;

import com.bcgogo.enums.SolrIdPrefix;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.enums.user.ContactType;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.solr.common.SolrInputDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-6-4
 * Time: 下午2:31
 * 联系人DTO
 */
public class ContactDTO implements Serializable{
  private String specialIdStr;
  private Long id;
  private String idStr;
  private String customerOrSupplierName;
  private String name; // 联系人姓名
  private String mobile; // 联系人手机号
  private String email;  // 邮箱
  private String qq; //QQ
  private Long customerId;
  private Long supplierId;
  private Long shopId;
  private Boolean vehicleContactFlag;
  private ContactGroupType sGroupType; //前台选择联系人所属组
  private boolean appCustomerFlag=false;

  //在contactConstant.java 添加定义
  private Integer level; // {0,1,2} 分别代表 第一、第二、第三联系人 数字越小 级别越高
  private Integer disabled; // 0 -失效 1-可用
  private Integer isMainContact; // 0 -非主联系人 1-主联系人
  private Integer isShopOwner;

  private SolrIdPrefix dataSourceFrom;

  private ContactType contactType;
  private List<ContactGroupType> contactGroupTypeList;
  private Boolean isApp;

  public List<ContactGroupType> getContactGroupTypeList() {
    return contactGroupTypeList;
  }

  public void setContactGroupTypeList(List<ContactGroupType> contactGroupTypeList) {
    this.contactGroupTypeList = contactGroupTypeList;
  }

  public Boolean getApp() {
    return isApp;
  }

  public void setApp(Boolean app) {
    isApp = app;
  }

  public String getSpecialIdStr() {
    return specialIdStr;
  }

  public void setSpecialIdStr(String specialIdStr) {
    this.specialIdStr = specialIdStr;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null){
      this.idStr = id.toString();
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public ContactGroupType getsGroupType() {
    return sGroupType;
  }

  public void setsGroupType(ContactGroupType sGroupType) {
    this.sGroupType = sGroupType;
  }

  public Boolean getVehicleContactFlag() {
    return vehicleContactFlag;
  }

  public void setVehicleContactFlag(Boolean vehicleContactFlag) {
    this.vehicleContactFlag = vehicleContactFlag;
  }

  public boolean isAppCustomerFlag() {
    return appCustomerFlag;
  }

  public void setAppCustomerFlag(boolean appCustomerFlag) {
    this.appCustomerFlag = appCustomerFlag;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public Integer getDisabled() {
    return disabled;
  }

  public void setDisabled(Integer disabled) {
    this.disabled = disabled;
  }

  public Integer getIsMainContact() {
    return isMainContact;
  }

  public void setIsMainContact(Integer mainContact) {
    isMainContact = mainContact;
  }

  public Integer getMainContact(){
    return isMainContact;
  }

  public void setMainContact(Integer mainContact){
    isMainContact = mainContact;
  }

  public Integer getIsShopOwner() {
    return isShopOwner;
  }

  public void setIsShopOwner(Integer shopOwner) {
    isShopOwner = shopOwner;
  }

  public SolrIdPrefix getDataSourceFrom() {
    return dataSourceFrom;
  }

  public void setDataSourceFrom(SolrIdPrefix dataSourceFrom) {
    this.dataSourceFrom = dataSourceFrom;
  }

  public ContactType getContactType() {
    return contactType;
  }

  public void setContactType(ContactType contactType) {
    this.contactType = contactType;
  }

  public Integer getShopOwner() {
    return isShopOwner;
  }

  public void setShopOwner(Integer shopOwner) {
    isShopOwner = shopOwner;
  }

  public ContactDTO() {
  }

  public ContactDTO(Long id, String name, String mobile, String email, String qq, Long customerId, Long supplierId, Long shopId, Integer level, Integer disabled, Integer mainContact, Integer shopOwner) {
    this.id = id;
    this.name = name;
    this.mobile = mobile;
    this.email = email;
    this.qq = qq;
    this.customerId = customerId;
    this.supplierId = supplierId;
    this.shopId = shopId;
    this.level = level;
    this.disabled = disabled;
    this.isMainContact = mainContact;
    this.isShopOwner = shopOwner;
  }
  public ContactDTO(Long id){
    this.id = id;
  }
  public ContactDTO(String name,String mobile){
    this.name=name;
    this.mobile=mobile;
  }

  public boolean isValidContact() {
    if (StringUtils.isBlank(this.getName()) && StringUtils.isBlank(this.getMobile()) && StringUtils.isBlank(this.getEmail()) && StringUtils.isBlank(this.getQq())) {
      return false;
    }
    return true;
  }

  @Override
  public ContactDTO clone() throws CloneNotSupportedException {
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setId(id);
    contactDTO.setName(name);
    contactDTO.setMobile(mobile);
    contactDTO.setEmail(email);
    contactDTO.setQq(qq);
    contactDTO.setCustomerId(customerId);
    contactDTO.setSupplierId(supplierId);
    contactDTO.setShopId(shopId);
    contactDTO.setLevel(level);
    contactDTO.setDisabled(disabled);
    contactDTO.setIsMainContact(isMainContact);
    contactDTO.setIsShopOwner(isShopOwner);
    return contactDTO;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }


  public SolrInputDocument toOtherContactSolrDocument(String docType,SolrIdPrefix solrIdPrefix) throws Exception {
    PingyinInfo pingyinInfo = null;
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", solrIdPrefix+"_"+this.getId());
    doc.addField("doc_type", docType);
    doc.addField("shop_id", this.getShopId());

    String name = this.getCustomerOrSupplierName();
    if (StringUtils.isBlank(name)) {
      name = this.getName();
    }
    doc.addField("name", name);
    if (StringUtils.isNotBlank(name)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(name);
      doc.addField("name_fl", pingyinInfo.firstLetters);
      doc.addField("name_py", pingyinInfo.pingyin);
      doc.addField("name_fl_sort", pingyinInfo.firstLetter);
    }

    if(StringUtils.isNotBlank(this.getName())){
      doc.addField("contact", this.getName());
      pingyinInfo = PinyinUtil.getPingyinInfo(this.getName());
      String contact_fl = pingyinInfo.firstLetters;
      String contact_py = pingyinInfo.pingyin;
      doc.addField("contact_fl", contact_fl);
      doc.addField("contact_py", contact_py);
    }
    if (StringUtils.isNotBlank(this.getMobile())) {
      doc.addField("mobile", this.getMobile());
    }
    doc.addField("contact_group_type", ContactGroupType.OTHERS);
    return doc;
  }
}
