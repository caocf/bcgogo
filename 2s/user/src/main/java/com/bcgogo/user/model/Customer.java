package com.bcgogo.user.model;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InsuranceOrderDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.user.dto.RelatedShopUpdateLogDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-8
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "customer")
public class Customer extends LongIdentifier {
  private Long shopId;
  private String code;
  private Long customerShopId;
  private String name;
  private String company;
  private String identifierNo;
  private Integer gender;
  private Long birthday;
  private String mobile;
  private String landLine;
  private String landLineSecond;
  private String landLineThird;
  private String fax;
  private String email;
  private String zip;
  private String address;
  private String qq;
  private String bank;
  private String account;
  private String taxNo;
  private Long billingAddress;
  private Long invoiceCategory;
  private Long settlementType;
  private String invoiceTitle;
  private Long dept;
  private Long agent;
  private String memo;
  private String contact;//zhouxiaochen 2011-12-21
  private String shortName;
  private String bankAccountName;
  private String area;
  private String customerKind;
  private String firstLetters;   //zhangchuanlong
  private String memberNumber;
  private Long parentId;      //  保存合并后的parent customer id
  private CustomerStatus status;
  private Long invitationCodeSendDate; //邀请码发送时间
  private Integer invitationCodeSendTimes; //邀请码发送次数
  private RelationTypes relationType;     //客户关联类型
  private Long province;
  private Long city;
  private Long region;
  private String identity;
  private Long supplierId;
  private Boolean isPermanentDualRole;
  private Integer cancelRecommendAssociatedCount = 0;

  private VehicleSelectBrandModel selectBrandModel; //选择车型时 是否是全部车型

  private String businessScope;

    public Customer() {
  }

  //保险单更新客户信息
  public boolean updateFromInsurance(InsuranceOrderDTO insuranceOrderDTO) {
    boolean isUpdate = false;
    if(insuranceOrderDTO == null){
      return isUpdate;
    }
    if(getMobile()!=null && !getMobile().equals(insuranceOrderDTO.getMobile())){
      setMobile(insuranceOrderDTO.getMobile());
      isUpdate = true;
    }
    return isUpdate;
  }
  //保险单新建客户信息
  public void fromInsuranceOrderDTO(InsuranceOrderDTO insuranceOrderDTO){
    if (insuranceOrderDTO == null) {
      return;
    }
    this.setMobile(insuranceOrderDTO.getMobile());
    this.setName(insuranceOrderDTO.getCustomer());
    if (insuranceOrderDTO.getCustomer() != null && insuranceOrderDTO.getCustomer().length() > 0) {
      this.setFirstLetters(PinyinUtil.converterToFirstSpell(insuranceOrderDTO.getCustomer()));
    }
    this.setShopId(insuranceOrderDTO.getShopId());
  }
  public Customer(CustomerDTO customerDTO) {
    this.setId(customerDTO.getId());
    this.setShopId(customerDTO.getShopId());
    this.setCode(customerDTO.getCode());
    this.setCustomerShopId(customerDTO.getCustomerShopId());
    this.setName(customerDTO.getName());
    this.setCompany(customerDTO.getCompany());
    this.setIdentifierNo(customerDTO.getIdentifierNo());
    this.setGender(customerDTO.getGender());
    this.setBirthday(customerDTO.getBirthday());
    this.setMobile(customerDTO.getMobile());

    this.setLandLine(customerDTO.getLandLine());
    this.setLandLineSecond(customerDTO.getLandLineSecond());
    this.setLandLineThird(customerDTO.getLandLineThird());

    this.setFax(customerDTO.getFax());
    this.setEmail(customerDTO.getEmail());
    this.setZip(customerDTO.getZip());
    this.setAddress(customerDTO.getAddress());
    this.setQq(customerDTO.getQq());
    this.setBank(customerDTO.getBank());
    this.setAccount(customerDTO.getAccount());
    this.setTaxNo(customerDTO.getTaxNo());
    this.setBillingAddress(customerDTO.getBillingAddress());
    this.setInvoiceCategory(customerDTO.getInvoiceCategory());
    this.setSettlementType(customerDTO.getSettlementType());
    this.setInvoiceTitle(customerDTO.getInvoiceTitle());
    this.setDept(customerDTO.getDept());
    this.setAgent(customerDTO.getAgent());
    this.setMemo(customerDTO.getMemo());
    this.setContact(customerDTO.getContact());
    this.setShortName(customerDTO.getShortName());
    this.setArea(customerDTO.getArea());
    this.setBankAccountName(customerDTO.getBankAccountName());
    this.setCustomerKind(customerDTO.getCustomerKind());
    this.setFirstLetters(customerDTO.getFirstLetters());
    this.setMemberNumber(customerDTO.getMemberNumber());
    this.setStatus(customerDTO.getStatus());
    this.setInvitationCodeSendTimes(customerDTO.getInvitationCodeSendTimes());
    this.setInvitationCodeSendDate(customerDTO.getInvitationCodeSendDate());
    this.setRelationType(customerDTO.getRelationType());
    this.setProvince(customerDTO.getProvince());
    this.setCity(customerDTO.getCity());
    this.setRegion(customerDTO.getRegion());
    this.setIdentity(customerDTO.getIdentity());
    this.setSupplierId(customerDTO.getSupplierId());
    this.setPermanentDualRole(customerDTO.getPermanentDualRole());
    this.setParentId(customerDTO.getParentId());
    this.setBusinessScope(customerDTO.getBusinessScopeStr());
  }

  public Customer fromDTO(CustomerDTO customerDTO) {
    this.setId(customerDTO.getId());
    this.setShopId(customerDTO.getShopId());
    this.setCode(customerDTO.getCode());
    this.setCustomerShopId(customerDTO.getCustomerShopId());
    this.setName(customerDTO.getName());
    this.setCompany(customerDTO.getCompany());
    this.setIdentifierNo(customerDTO.getIdentifierNo());
    this.setGender(customerDTO.getGender());
    this.setBirthday(customerDTO.getBirthday());
    this.setMobile(customerDTO.getMobile());
    this.setLandLine(customerDTO.getLandLine());
    this.setLandLineSecond(customerDTO.getLandLineSecond());
    this.setLandLineThird(customerDTO.getLandLineThird());
    this.setFax(customerDTO.getFax());
    this.setEmail(customerDTO.getEmail());
    this.setZip(customerDTO.getZip());
    this.setAddress(customerDTO.getAddress());
    this.setQq(customerDTO.getQq());
    this.setBank(customerDTO.getBank());
    this.setAccount(customerDTO.getAccount());
    this.setTaxNo(customerDTO.getTaxNo());
    this.setBillingAddress(customerDTO.getBillingAddress());
    this.setInvoiceCategory(customerDTO.getInvoiceCategory());
    this.setSettlementType(customerDTO.getSettlementType());
    this.setInvoiceTitle(customerDTO.getInvoiceTitle());
    this.setDept(customerDTO.getDept());
    this.setAgent(customerDTO.getAgent());
    this.setMemo(customerDTO.getMemo());
    this.setContact(customerDTO.getContact());
    this.setShortName(customerDTO.getShortName());
    this.setArea(customerDTO.getArea());
    this.setBankAccountName(customerDTO.getBankAccountName());
    this.setCustomerKind(customerDTO.getCustomerKind());
    this.setFirstLetters(customerDTO.getFirstLetters());
    this.setMemberNumber(customerDTO.getMemberNumber());
    this.setStatus(customerDTO.getStatus());
    this.setInvitationCodeSendTimes(customerDTO.getInvitationCodeSendTimes());
    this.setInvitationCodeSendDate(customerDTO.getInvitationCodeSendDate());
    this.setRelationType(customerDTO.getRelationType());
    this.setProvince(customerDTO.getProvince());
    this.setCity(customerDTO.getCity());
    this.setRegion(customerDTO.getRegion());
    this.setIdentity(customerDTO.getIdentity());
    this.setSupplierId(customerDTO.getSupplierId());
    this.setPermanentDualRole(customerDTO.getPermanentDualRole());
    this.setParentId(customerDTO.getParentId());
    this.setBusinessScope(customerDTO.getBusinessScopeStr());
    this.setSelectBrandModel(customerDTO.getSelectBrandModel());
    return this;
  }

  public CustomerDTO toDTO() {
    CustomerDTO customerDTO = new CustomerDTO();

    customerDTO.setId(this.getId());
    customerDTO.setShopId(this.getShopId());
    customerDTO.setCode(this.getCode());
    customerDTO.setCustomerShopId(this.getCustomerShopId());
    if(this.getCustomerShopId() != null){
      customerDTO.setOnlineShop(true);
    }
    customerDTO.setName(this.getName());
    customerDTO.setCompany(this.getCompany());
    customerDTO.setIdentifierNo(this.getIdentifierNo());
    customerDTO.setGender(this.getGender());
    customerDTO.setBirthday(this.getBirthday());
    customerDTO.setBirthdayString(DateUtil.convertDateLongToDateString(DateUtil.MONTH_DATE,this.getBirthday()));
    customerDTO.setMobile(this.getMobile());
    customerDTO.setLandLine(this.getLandLine());
    customerDTO.setLandLineSecond(this.getLandLineSecond());
    customerDTO.setLandLineThird(this.getLandLineThird());
    customerDTO.compositeLandline();
    customerDTO.setFax(this.getFax());
    customerDTO.setEmail(this.getEmail());
    customerDTO.setZip(this.getZip());
    customerDTO.setAddress(this.getAddress());
    customerDTO.setQq(this.getQq());
    customerDTO.setBank(this.getBank());
    customerDTO.setAccount(this.getAccount());
    customerDTO.setTaxNo(this.getTaxNo());
    customerDTO.setBillingAddress(this.getBillingAddress());
    customerDTO.setInvoiceCategory(this.getInvoiceCategory());
    customerDTO.setSettlementType(this.getSettlementType());
    customerDTO.setInvoiceTitle(this.getInvoiceTitle());
    customerDTO.setDept(this.getDept());
    customerDTO.setAgent(this.getAgent());
    customerDTO.setMemo(this.getMemo());
    customerDTO.setContact(this.getContact());
    customerDTO.setBankAccountName(this.getBankAccountName());
    customerDTO.setArea(this.getArea());
    customerDTO.setCustomerKind(this.getCustomerKind());
    customerDTO.setShortName(this.getShortName());
    customerDTO.setFirstLetters(this.getFirstLetters());
    customerDTO.setMemberNumber(this.getMemberNumber());
    customerDTO.setCreationDate(this.getCreationDate());
    customerDTO.setStatus(this.getStatus());
    customerDTO.setInvitationCodeSendTimes(this.getInvitationCodeSendTimes());
    customerDTO.setInvitationCodeSendDate(this.getInvitationCodeSendDate());
    customerDTO.setRelationType(getRelationType());
    customerDTO.setProvince(getProvince());
    customerDTO.setCity(getCity());
    customerDTO.setRegion(getRegion());
    customerDTO.setIdentity(getIdentity());
    customerDTO.setSupplierId(getSupplierId());
    customerDTO.setPermanentDualRole(getPermanentDualRole());
    customerDTO.setParentId(getParentId());
    customerDTO.setParentIdStr(getParentId() != null ? String.valueOf(getParentId()) : "");
    customerDTO.setBusinessScopeStr(getBusinessScope());
    customerDTO.setSelectBrandModel(getSelectBrandModel());
    return customerDTO;
  }


    //用于比较申请关联的时候是否需要新增供应商
  public boolean isSame(CustomerDTO searchCondition) {
    if (searchCondition != null) {
      if (!StringUtil.isEqual(searchCondition.getName(), this.getName())) {
        return false;
      }
      if (!StringUtil.isEqual(searchCondition.getMobile(), this.getMobile())) {
        return false;
      }

      if (!StringUtil.isEqual(searchCondition.getLandLine(), this.getLandLine())) {
        return false;
      }
      if (!StringUtil.isEqual(searchCondition.getLandLineSecond(), this.getLandLineSecond())) {
        return false;
      }
      if (!StringUtil.isEqual(searchCondition.getLandLineThird(), this.getLandLineThird())) {
        return false;
      }

      if (!StringUtil.isEqual(searchCondition.getAddress(), this.getAddress())) {
        return false;
      }
      if (!StringUtil.isEqual(searchCondition.getContact(), this.getContact())) {
        return false;
      }
      if (!StringUtil.isEqual(searchCondition.getQq(), this.getQq())) {
        return false;
      }
      if (!StringUtil.isEqual(searchCondition.getEmail(), this.getEmail())) {
        return false;
      }
      return true;
    }
    return false;
  }

  public  Customer fromCustomerOrSupplierDTO(CustomerOrSupplierDTO csDTO){
     if(csDTO==null) return null;
    this.setShopId(csDTO.getShopId());
    this.setName(csDTO.getName());
    this.setAddress(csDTO.getAbbr());
    this.setAddress(csDTO.getAddress());
    this.setAccount(csDTO.getAccount());
    this.setAgent(csDTO.getAgent());
    this.setArea(csDTO.getArea());
    this.setBank(csDTO.getBank());
    this.setBankAccountName(csDTO.getAccountName());
    this.setAccount(csDTO.getAccount());
//    this.setBirthday(csDTO.getBirthDay());
    this.setCompany(csDTO.getCompany());
    this.setContact(csDTO.getContact());
    this.setEmail(csDTO.getEmail());
    this.setFax(csDTO.getFax());
    this.setQq(csDTO.getQq());
    this.setLandLine(csDTO.getLandline());
    this.setMobile(csDTO.getMobile());
    this.setSettlementType(csDTO.getSettlementType());
    this.setInvoiceCategory(csDTO.getInvoiceCategory());
    this.setCustomerKind(String.valueOf(csDTO.getCategory()));
    if(csDTO.getCustomerOrSupplierId()==null){
      this.setRelationType(RelationTypes.UNRELATED);
    }
    return this;
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "member_number", length = 20)
  public String getMemberNumber() {
    return memberNumber;
  }

  public void setMemberNumber(String memberNumber) {
    this.memberNumber = memberNumber;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "code", length = 20)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name = "customer_shop_id")
  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  @Column(name = "name", length = 100)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "company", length = 20)
  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  @Column(name = "identifier_no", length = 50)
  public String getIdentifierNo() {
    return identifierNo;
  }

  public void setIdentifierNo(String identifierNo) {
    this.identifierNo = identifierNo;
  }

  @Column(name = "gender")
  public Integer getGender() {
    return gender;
  }

  public void setGender(Integer gender) {
    this.gender = gender;
  }

  @Column(name = "birthday")
  public Long getBirthday() {
    return birthday;
  }

  public void setBirthday(Long birthday) {
    this.birthday = birthday;
  }

  @Column(name = "mobile", length = 20)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "landline", length = 20)
  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  @Column(name = "landline_second", length = 20)
  public String getLandLineSecond() {
    return landLineSecond;
  }

  public void setLandLineSecond(String landLineSecond) {
    this.landLineSecond = landLineSecond;
  }

  @Column(name = "landline_third", length = 20)
  public String getLandLineThird() {
    return landLineThird;
  }

  public void setLandLineThird(String landLineThird) {
    this.landLineThird = landLineThird;
  }

  @Column(name = "fax", length = 20)
  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  @Column(name = "email", length = 50)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "zip", length = 10)
  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  @Column(name = "address", length = 100)
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Column(name = "qq", length = 20)
  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  @Column(name = "bank", length = 20)
  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  @Column(name = "account", length = 20)
  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  @Column(name = "tax_no", length = 50)
  public String getTaxNo() {
    return taxNo;
  }

  public void setTaxNo(String taxNo) {
    this.taxNo = taxNo;
  }

  @Column(name = "billing_address")
  public Long getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(Long billingAddress) {
    this.billingAddress = billingAddress;
  }

  @Column(name = "invoice_category")
  public Long getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(Long invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
  }

  @Column(name = "settlement_type")
  public Long getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(Long settlementType) {
    this.settlementType = settlementType;
  }

  @Column(name = "invoice_title", length = 20)
  public String getInvoiceTitle() {
    return invoiceTitle;
  }

  public void setInvoiceTitle(String invoiceTitle) {
    this.invoiceTitle = invoiceTitle;
  }

  @Column(name = "dept")
  public Long getDept() {
    return dept;
  }

  public void setDept(Long dept) {
    this.dept = dept;
  }

  @Column(name = "agent")
  public Long getAgent() {
    return agent;
  }

  public void setAgent(Long agent) {
    this.agent = agent;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "contact", length = 200)
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name = "short_name", length = 20)
  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @Column(name = "bank_account_name", length = 20)
  public String getBankAccountName() {
    return bankAccountName;
  }

  public void setBankAccountName(String bankAccountName) {
    this.bankAccountName = bankAccountName;
  }

  @Column(name = "area", length = 20)
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  @Column(name = "customer_kind", length = 20)
  public String getCustomerKind() {
    return customerKind;
  }

  public void setCustomerKind(String customerKind) {
    this.customerKind = customerKind;
  }

  @Column(name = "first_letters", length = 100)
  public String getFirstLetters() {
    return firstLetters;
  }

  public void setFirstLetters(String firstLetters) {
    this.firstLetters = firstLetters;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public CustomerStatus getStatus() {
    return status;
  }

  public void setStatus(CustomerStatus status) {
    this.status = status;
  }

  @Column(name="invitation_code_send_date")
  public Long getInvitationCodeSendDate() {
    return invitationCodeSendDate;
  }

  public void setInvitationCodeSendDate(Long invitationCodeSendDate) {
    this.invitationCodeSendDate = invitationCodeSendDate;
  }

  @Column(name="invitation_code_send_times")
  public Integer getInvitationCodeSendTimes() {
    return invitationCodeSendTimes;
  }

  public void setInvitationCodeSendTimes(Integer invitationCodeSendTimes) {
    this.invitationCodeSendTimes = invitationCodeSendTimes;
  }


  @Enumerated(EnumType.STRING)
  @Column(name="relation_type")
  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  @Column(name = "province")

    public Long getProvince() {
        return province;
    }

    public void setProvince(Long province) {
        this.province = province;
    }

    @Column(name = "city")

    public Long getCity() {
        return city;
    }

    public void setCity(Long city) {
        this.city = city;
    }

   @Column(name = "region")

    public Long getRegion() {
        return region;
    }

    public void setRegion(Long region) {
        this.region = region;
    }

    @Column(name = "identity", length = 20)
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Column(name = "supplier_id")

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

  @Column(name="permanent_dual_role")
  public Boolean getPermanentDualRole() {
    return isPermanentDualRole;
  }

  public void setPermanentDualRole(Boolean permanentDualRole) {
    isPermanentDualRole = permanentDualRole;
  }

  @Column(name="cancel_recommend_associated_count")
  public Integer getCancelRecommendAssociatedCount() {
    return cancelRecommendAssociatedCount;
  }

  @Column(name="business_scope")
  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  public void setCancelRecommendAssociatedCount(Integer cancelRecommendAssociatedCount) {
    this.cancelRecommendAssociatedCount = cancelRecommendAssociatedCount;
  }
  public void addCancelRecommendAssociatedCount() {
    if(NumberUtil.intValue(cancelRecommendAssociatedCount)<100){
      cancelRecommendAssociatedCount = NumberUtil.intValue(cancelRecommendAssociatedCount)+1;
    }
  }

  public void fromRelatedShop(ShopDTO shopDTO, List<RelatedShopUpdateLogDTO> relatedShopUpdateLogDTOs)throws Exception{
    if (shopDTO == null) {
      return;
    }
    if (relatedShopUpdateLogDTOs == null) {
      relatedShopUpdateLogDTOs = new ArrayList<RelatedShopUpdateLogDTO>();
    }
    //name, shortName,address,landLine,province,city, region
    CustomerDTO customerDTO = this.toDTO();
    if (!StringUtil.compareSame(getName(), shopDTO.getName())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "name", getName(), shopDTO.getName()));
      setName(shopDTO.getName());
    }
    if (!StringUtil.compareSame(getShortName(), shopDTO.getShortname())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "short_name", getShortName(), shopDTO.getShortname()));
      setShortName(shopDTO.getShortname());
    }
    if (!StringUtil.compareSame(getAddress(), shopDTO.getAddress())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "address", getAddress(), shopDTO.getAddress()));
      setAddress(shopDTO.getAddress());
    }
//    if (!StringUtil.compareSame(getLandLine(), shopDTO.getLandline())) {
//      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "landLine", getLandLine(), shopDTO.getLandline()));
//      setLandLine(shopDTO.getLandline());
//    }
    if (!StringUtil.compareSame(getProvince(), shopDTO.getProvince())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "province",
          StringUtil.valueOf(getProvince()), StringUtil.valueOf(shopDTO.getProvince())));
      setProvince(shopDTO.getProvince());
    }

    if (!StringUtil.compareSame(getCity(), shopDTO.getCity())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "city",
          StringUtil.valueOf(getCity()), StringUtil.valueOf(shopDTO.getCity())));
      setCity(shopDTO.getCity());
    }

    if (!StringUtil.compareSame(getRegion(), shopDTO.getRegion())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "region",
          StringUtil.valueOf(getRegion()), StringUtil.valueOf(shopDTO.getRegion())));
      setRegion(shopDTO.getRegion());
    }
    if (getSelectBrandModel() != shopDTO.getShopSelectBrandModel()) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerDTO, "selectBrandModel", getSelectBrandModel()==null?null:getSelectBrandModel().toString(), shopDTO.getShopSelectBrandModel()==null?null:shopDTO.getShopSelectBrandModel().toString()));
      setSelectBrandModel(shopDTO.getShopSelectBrandModel());
    }
  }

  @Enumerated(EnumType.STRING)
  @Column(name="select_brand_model")
  public VehicleSelectBrandModel getSelectBrandModel() {
    return selectBrandModel;
  }

  public void setSelectBrandModel(VehicleSelectBrandModel selectBrandModel) {
    this.selectBrandModel = selectBrandModel;
  }
}

