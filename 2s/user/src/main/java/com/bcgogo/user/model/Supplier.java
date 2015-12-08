package com.bcgogo.user.model;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.user.dto.RelatedShopUpdateLogDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-8
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "supplier")
public class Supplier extends LongIdentifier {
  private Long shopId;
  private String code;
  private Long supplierShopId;
  private String name;
  private String abbr;
  private Long category;
  private Long areaId;
  private String contact;
  private String mobile;
  private String landLine;

  //新增的多座机号
  private String landLineSecond;
  private String landLineThird;

  private String fax;
  private String email;
  private String zip;
  private String address;
  private String qq;
  private String legalRep;
  private String bank;
  private String accountName;
  private String account;
  private String taxNo;
  private String billingAddress;
  private Long invoiceCategoryId;
  private Long settlementTypeId;
  private String invoiceTitle;
  private String businessScope;
  private String memo;
  private  String firstLetters;           //zhangchuanlong

  private Double totalInventoryAmount;
  private Long lastOrderId;
  private String lastOrderProducts;
  private Long lastOrderTime;
  private String lastOrderType;
  private OrderTypes lastOrderTypeEnum;
  private Long lastInventoryTime;    //最后入库时间
  private String score;//评价分数 用于退货统计评价
  private CustomerStatus status;
  private Long parentId;
  private Long invitationCodeSendDate; //邀请码发送时间
  private Integer invitationCodeSendTimes; //邀请码发送次数
  private RelationTypes relationType;     //客户关联类型
  private Long province;
  private Long city;
  private Long region;
  private String identity;
  private Long customerId;
  private Boolean isPermanentDualRole;
  private Integer cancelRecommendAssociatedCount=0;
  private VehicleSelectBrandModel selectBrandModel; //选择车型时 是否是全部车型
    public Supplier() {
  }

  public Supplier fromBasicInfoDTO(SupplierDTO supplierDTO) {
//    this.setId(supplierDTO.getId());
    this.setSupplierShopId(supplierDTO.getSupplierShopId());
    this.setLastOrderId(supplierDTO.getLastOrderId());
    this.setLastOrderProducts(supplierDTO.getLastOrderProducts());
    this.setLastOrderTime(supplierDTO.getLastOrderTime());
    this.setLastOrderTypeEnum(supplierDTO.getLastOrderType());
    this.setTotalInventoryAmount(supplierDTO.getTotalInventoryAmount());
    this.setLastInventoryTime(supplierDTO.getLastInventoryTime());
    this.setScore(supplierDTO.getScore());
    this.setInvitationCodeSendTimes(supplierDTO.getInvitationCodeSendTimes());
    this.setInvitationCodeSendDate(supplierDTO.getInvitationCodeSendDate());
    this.setPermanentDualRole(supplierDTO.getPermanentDualRole());
    return this;
  }

  public Supplier fromDTO(SupplierDTO supplierDTO) {
//    this.setId(supplierDTO.getId());
    this.setShopId(supplierDTO.getShopId());
    this.setCode(supplierDTO.getCode());
    this.setSupplierShopId(supplierDTO.getSupplierShopId());
    this.setName(supplierDTO.getName());
    this.setAbbr(supplierDTO.getAbbr());
    this.setCategory(supplierDTO.getCategory());
    this.setAreaId(supplierDTO.getAreaId());
    this.setContact(supplierDTO.getContact());
    this.setMobile(supplierDTO.getMobile());
    //多座机号
    this.setLandLine(supplierDTO.getLandLine());
    this.setLandLineSecond(supplierDTO.getLandLineSecond());
    this.setLandLineThird(supplierDTO.getLandLineThird());

    this.setFax(supplierDTO.getFax());
    this.setEmail(supplierDTO.getEmail());
    this.setZip(supplierDTO.getZip());
    this.setAddress(supplierDTO.getAddress());
    this.setQq(supplierDTO.getQq());
    this.setLegalRep(supplierDTO.getLegalRep());
    this.setBank(supplierDTO.getBank());
    this.setAccountName(supplierDTO.getAccountName());
    this.setAccount(supplierDTO.getAccount());
    this.setTaxNo(supplierDTO.getTaxNo());
    this.setBillingAddress(supplierDTO.getBillingAddress());
    this.setInvoiceCategoryId(supplierDTO.getInvoiceCategoryId());
    this.setSettlementTypeId(supplierDTO.getSettlementTypeId());
    this.setInvoiceTitle(supplierDTO.getInvoiceTitle());
    this.setMemo(supplierDTO.getMemo());
    this.setFirstLetters(supplierDTO.getFirstLetters());
    this.setLastOrderId(supplierDTO.getLastOrderId());
    this.setLastOrderProducts(supplierDTO.getLastOrderProducts());
    this.setLastOrderTime(supplierDTO.getLastOrderTime());
    this.setLastOrderTypeEnum(supplierDTO.getLastOrderType());
    this.setTotalInventoryAmount(supplierDTO.getTotalInventoryAmount());
    this.setLastInventoryTime(supplierDTO.getLastInventoryTime());
    this.setScore(supplierDTO.getScore());
    this.setInvitationCodeSendTimes(supplierDTO.getInvitationCodeSendTimes());
    this.setInvitationCodeSendDate(supplierDTO.getInvitationCodeSendDate());
    this.setRelationType(supplierDTO.getRelationType());
    this.setProvince(supplierDTO.getProvince());
    this.setCity(supplierDTO.getCity());
    this.setRegion(supplierDTO.getRegion());
    this.setIdentity(supplierDTO.getIdentity());
    this.setCustomerId(supplierDTO.getCustomerId());
    this.setPermanentDualRole(supplierDTO.getPermanentDualRole());
    this.setStatus(supplierDTO.getStatus());
    this.setSelectBrandModel(supplierDTO.getSelectBrandModel());
    return this;
  }

  public SupplierDTO toDTO() {
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.setId(this.getId());
    supplierDTO.setSupplierShopId(this.getSupplierShopId());
    if(this.getSupplierShopId() != null){
      supplierDTO.setOnlineShop(true);
    }
    supplierDTO.setIdString(String.valueOf(this.getId()));
    supplierDTO.setShopId(this.getShopId());
    supplierDTO.setCode(this.getCode());
    supplierDTO.setSupplierShopId(this.getSupplierShopId());
    supplierDTO.setName(this.getName());
    supplierDTO.setAbbr(this.getAbbr());
    supplierDTO.setCategory(this.getCategory());
    supplierDTO.setAreaId(this.getAreaId());
    supplierDTO.setContact(this.getContact());
    supplierDTO.setMobile(this.getMobile());
    //多座机号
    supplierDTO.setLandLine(this.getLandLine());
    supplierDTO.setLandLineSecond(this.getLandLineSecond());
    supplierDTO.setLandLineThird(this.getLandLineThird());
    supplierDTO.compositeLandline();
    supplierDTO.setFax(this.getFax());
    supplierDTO.setEmail(this.getEmail());
    supplierDTO.setZip(this.getZip());
    supplierDTO.setAddress(this.getAddress());
    supplierDTO.setQq(this.getQq());
    supplierDTO.setLegalRep(this.getLegalRep());
    supplierDTO.setBank(this.getBank());
    supplierDTO.setAccountName(this.getAccountName());
    supplierDTO.setAccount(this.getAccount());
    supplierDTO.setTaxNo(this.getTaxNo());
    supplierDTO.setBillingAddress(this.getBillingAddress());
    supplierDTO.setInvoiceCategoryId(this.getInvoiceCategoryId());
    supplierDTO.setSettlementTypeId(this.getSettlementTypeId());
    supplierDTO.setInvoiceTitle(this.getInvoiceTitle());
    supplierDTO.setBusinessScope(this.getBusinessScope());
    supplierDTO.setMemo(this.getMemo());
    supplierDTO.setFirstLetters(this.getFirstLetters());
    supplierDTO.setLastOrderId(this.lastOrderId);
    supplierDTO.setLastOrderProducts(this.lastOrderProducts);
    supplierDTO.setLastOrderTime(this.lastOrderTime);
    supplierDTO.setCreationDate(this.getCreationDate());
    supplierDTO.setLastInventoryTime(this.getLastInventoryTime());
    supplierDTO.setLastOrderType(getLastOrderTypeEnum());
    supplierDTO.setTotalInventoryAmount(this.totalInventoryAmount);
    supplierDTO.setScore(this.getScore()==null? "0" :this.getScore());
    supplierDTO.setStatus(this.getStatus());
    supplierDTO.setInvitationCodeSendTimes(this.getInvitationCodeSendTimes());
    supplierDTO.setInvitationCodeSendDate(this.getInvitationCodeSendDate());
    supplierDTO.setRelationType(this.getRelationType());
    if(this.getRelationType()!=null)
    supplierDTO.setRelationTypeStr(this.getRelationType().getName());
    supplierDTO.setProvince(this.getProvince());
    supplierDTO.setCity(this.getCity());
    supplierDTO.setRegion(this.getRegion());
    supplierDTO.setIdentity(getIdentity());
    supplierDTO.setCustomerId(getCustomerId());
    supplierDTO.setPermanentDualRole(getPermanentDualRole());
    supplierDTO.setSelectBrandModel(getSelectBrandModel());
    return supplierDTO;
  }


  //用于比较申请关联的时候是否需要新增供应商
  public boolean isSame(SupplierDTO supplierDTO) {
    if (supplierDTO != null) {
      if (!StringUtil.isEqual(supplierDTO.getName(), this.getName())) {
        return false;
      }
      if (!StringUtil.isEqual(supplierDTO.getMobile(), this.getMobile())) {
        return false;
      }
      if (!StringUtil.isEqual(supplierDTO.getLandLine(), this.getLandLine())) {
        return false;
      }
      if (!StringUtil.isEqual(supplierDTO.getAddress(), this.getAddress())) {
        return false;
      }
      if (!StringUtil.isEqual(supplierDTO.getContact(), this.getContact())) {
        return false;
      }
      if (!StringUtil.isEqual(supplierDTO.getQq(), this.getQq())) {
        return false;
      }
      if (!StringUtil.isEqual(supplierDTO.getEmail(), this.getEmail())) {
        return false;
      }
      return true;
    }
    return false;
  }

  public  Supplier fromCustomerOrSupplierDTO(CustomerOrSupplierDTO csDTO){
    if(csDTO==null) return null;
    this.setShopId(csDTO.getShopId());
    this.setName(csDTO.getName());
    this.setContact(csDTO.getContact());
    this.setAbbr(csDTO.getAbbr());
    this.setMobile(csDTO.getMobile());
    this.setLandLine(csDTO.getLandline());
    this.setFax(csDTO.getFax());
    this.setQq(csDTO.getQq());
    this.setAreaId(csDTO.getAreaId());
    this.setBank(csDTO.getBank());
    this.setCategory(csDTO.getCategory());
    this.setEmail(csDTO.getEmail());
    this.setAccount(csDTO.getAccount());
    this.setAccountName(csDTO.getAccountName());
    this.setAddress(csDTO.getAddress());
    this.setBusinessScope(csDTO.getBusinessScope());
    this.setSettlementTypeId(csDTO.getSettlementType());
    this.setInvoiceCategoryId(csDTO.getInvoiceCategory());
    this.setCategory(csDTO.getCategory());
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

  @Column(name = "code", length = 20)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name = "supplier_shop_id")
  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  @Column(name = "name", length = 20)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "abbr", length = 10)
  public String getAbbr() {
    return abbr;
  }

  public void setAbbr(String abbr) {
    this.abbr = abbr;
  }

  @Column(name = "category")
  public Long getCategory() {
    return category;
  }

  public void setCategory(Long category) {
    this.category = category;
  }

  @Column(name = "area_id")
  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  @Column(name = "mobile", length = 20)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "contact", length = 20)
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name = "landline", length = 20)
  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }
  @Column(name="landline_third",length = 20)
  public String getLandLineThird() {
    return landLineThird;
  }

  public void setLandLineThird(String landLineThird) {
    this.landLineThird = landLineThird;
  }

  @Column(name="landline_second",length = 20)
  public String getLandLineSecond() {
    return landLineSecond;
  }

  public void setLandLineSecond(String landLineSecond) {
    this.landLineSecond = landLineSecond;
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

  @Column(name = "legal_rep", length = 20)
  public String getLegalRep() {
    return legalRep;
  }

  public void setLegalRep(String legalRep) {
    this.legalRep = legalRep;
  }

  @Column(name = "bank", length = 20)
  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  @Column(name = "account_name", length = 20)
  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
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

  @Column(name = "billing_address", length = 50)
  public String getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(String billingAddress) {
    this.billingAddress = billingAddress;
  }

  @Column(name = "invoice_category_id")
  public Long getInvoiceCategoryId() {
    return invoiceCategoryId;
  }

  public void setInvoiceCategoryId(Long invoiceCategoryId) {
    this.invoiceCategoryId = invoiceCategoryId;
  }

  @Column(name = "settlement_type_id")
  public Long getSettlementTypeId() {
    return settlementTypeId;
  }

  public void setSettlementTypeId(Long settlementTypeId) {
    this.settlementTypeId = settlementTypeId;
  }

  @Column(name = "invoice_title", length = 50)
  public String getInvoiceTitle() {
    return invoiceTitle;
  }

  public void setInvoiceTitle(String invoiceTitle) {
    this.invoiceTitle = invoiceTitle;
  }

  @Column(name = "business_scope", length = 500)
  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name="first_letters",length = 100)
  public String getFirstLetters() {
    return firstLetters;
  }

  public void setFirstLetters(String firstLetters) {
    this.firstLetters = firstLetters;
  }

  @Column(name="total_inventory_amount")
  public Double getTotalInventoryAmount() {
    return totalInventoryAmount;
  }

  @Column(name="last_order_id")
  public Long getLastOrderId() {
    return lastOrderId;
  }

  public void setTotalInventoryAmount(Double totalInventoryAmount) {
    this.totalInventoryAmount = totalInventoryAmount;
  }

  public void setLastOrderId(Long lastOrderId) {
    this.lastOrderId = lastOrderId;
  }

  @Column(name="last_order_products")
  public String getLastOrderProducts() {
    return lastOrderProducts;
  }

  @Column(name="last_order_time")
  public Long getLastOrderTime() {
    return lastOrderTime;
  }

  public void setLastOrderProducts(String lastOrderProducts) {
    this.lastOrderProducts = lastOrderProducts;
  }

  public void setLastOrderTime(Long lastOrderTime) {
    this.lastOrderTime = lastOrderTime;
  }

  @Column(name="last_order_type")
  public String getLastOrderType() {
//    return lastOrderType;
    return lastOrderTypeEnum==null?"":lastOrderTypeEnum.getName();
  }

  public void setLastOrderType(String lastOrderType) {
    this.lastOrderType = lastOrderType;
  }

  @Column(name="last_order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getLastOrderTypeEnum() {
    return lastOrderTypeEnum;
  }

  public void setLastOrderTypeEnum(OrderTypes lastOrderTypeEnum) {
    this.lastOrderTypeEnum = lastOrderTypeEnum;
  }

  @Column(name="last_inventory_time")
  public Long getLastInventoryTime() {
    return lastInventoryTime;
  }

  public void setLastInventoryTime(Long lastInventoryTime) {
    this.lastInventoryTime = lastInventoryTime;
  }

  @Column(name="score")
  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public CustomerStatus getStatus() {
    return status;
  }

  public void setStatus(CustomerStatus status) {
    this.status = status;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="relation_type")
  public RelationTypes getRelationType() {
    return relationType;
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
  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

    @Column(name="province")

    public Long getProvince() {
        return province;
    }

    public void setProvince(Long province) {
        this.province = province;
    }

    @Column(name="city")

    public Long getCity() {
        return city;
    }

    public void setCity(Long city) {
        this.city = city;
    }

    @Column(name="region")

    public Long getRegion() {
        return region;
    }

    public void setRegion(Long region) {
        this.region = region;
    }
    @Column(name="identity", length=20)
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Column(name="customer_id")

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

  public void setCancelRecommendAssociatedCount(Integer cancelRecommendAssociatedCount) {
    this.cancelRecommendAssociatedCount = cancelRecommendAssociatedCount;
  }

  public void addCancelRecommendAssociatedCount() {
    if(NumberUtil.intValue(cancelRecommendAssociatedCount)<100){
      cancelRecommendAssociatedCount = NumberUtil.intValue(cancelRecommendAssociatedCount)+1;
    }
  }

  public void fromRelatedShop(ShopDTO shopDTO, List<RelatedShopUpdateLogDTO> relatedShopUpdateLogDTOs) throws Exception{
    if (shopDTO == null) {
      return;
    }
    if (relatedShopUpdateLogDTOs == null) {
      relatedShopUpdateLogDTOs = new ArrayList<RelatedShopUpdateLogDTO>();
    }
    //name, abbr,address,province,city, region
    SupplierDTO supplierDTO = this.toDTO();
    if (!StringUtil.compareSame(getName(), shopDTO.getName())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "name", getName(), shopDTO.getName()));
      setName(shopDTO.getName());
    }
    if (!StringUtil.compareSame(getAbbr(), shopDTO.getShortname())) {
          relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "abbr", getAbbr(), shopDTO.getShortname()));
          setAbbr(shopDTO.getShortname());
        }

    if (!StringUtil.compareSame(getAddress(), shopDTO.getAddress())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "address", getAddress(), shopDTO.getAddress()));
      setAddress(shopDTO.getAddress());
    }

//    if (!StringUtil.compareSame(getLandLine(), shopDTO.getLandline())) {
//      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "landLine", getLandLine(), shopDTO.getLandline()));
//      setLandLine(shopDTO.getLandline());
//    }
    if (!StringUtil.compareSame(getProvince(), shopDTO.getProvince())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "province",
          StringUtil.valueOf(getProvince()), StringUtil.valueOf(shopDTO.getProvince())));
      setProvince(shopDTO.getProvince());
    }

    if (!StringUtil.compareSame(getCity(), shopDTO.getCity())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "city",
          StringUtil.valueOf(getCity()), StringUtil.valueOf(shopDTO.getCity())));
      setCity(shopDTO.getCity());
    }

    if (!StringUtil.compareSame(getRegion(), shopDTO.getRegion())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "region",
          StringUtil.valueOf(getRegion()), StringUtil.valueOf(shopDTO.getRegion())));
      setRegion(shopDTO.getRegion());
    }
    if (getSelectBrandModel() != shopDTO.getShopSelectBrandModel()) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(supplierDTO, "selectBrandModel", getSelectBrandModel() == null ? null : getSelectBrandModel().toString(), shopDTO.getShopSelectBrandModel() == null ? null : shopDTO.getShopSelectBrandModel().toString()));
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
