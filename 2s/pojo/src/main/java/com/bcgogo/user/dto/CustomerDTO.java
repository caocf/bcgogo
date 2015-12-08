package com.bcgogo.user.dto;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.base.BaseDTO;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.utils.*;
import com.sun.jndi.toolkit.url.Uri;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: XJ
 * Date: 10/14/11
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerDTO extends BaseDTO implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerDTO.class);

  private Long shopId;
  private Long creationDate;
  private String code;
  private Long customerShopId;
  private String customerShopIdStr;
  private String name;
  private String company;
  private String identifierNo;
  private Integer gender;
  private Long birthday;
  private String birthdayString;
  private String mobile;
  private String landLine;
  private String landLineSecond;
  private String landLineThird;
  private String landLineForAll;
  private String fax;
  private String email;
  private String zip;
  private String address;
  private String qq;
  private String qqArray;
  private String bank;
  private String account;
  private String taxNo;
  private Long billingAddress;
  private Long invoiceCategory;
  private String invoiceCategoryStr;
  private Long settlementType;
  private String settlementTypeStr;
  private String invoiceTitle;
  private Long dept;
  private Long agent;
  private String memo;
  private String contact;//zhouxiaochen 2011-12-21
  private Long contactId; // 主联系人Id
  private String contactIdStr;
  // 联系人列表
  private ContactDTO[] contacts;
  private List<ContactDTO> contactDTOList;
  private String shortName;
  private String bankAccountName;
  private String area;
  private String areaStr;
  private String customerKind;
  private String customerKindStr;
  private  String firstLetters;         //zhangchuanlong
  private boolean isMobileOnly = false;
  private String memberNumber;
  private Long vehicleId;
  private MemberDTO memberDTO;
  private CustomerRecordDTO customerRecordDTO;
  private List<VehicleDTO> vehicleDTOList;
  private List<CarDTO> carDTOList;
  private List<CustomerVehicleResponse>  customerVehicleResponses=new ArrayList<CustomerVehicleResponse>();
  private CustomerStatus status = CustomerStatus.ENABLED;
  private Long userId;
  private Double memberDiscount;
  private Long invitationCodeSendDate; //邀请码发送时间
  private Integer invitationCodeSendTimes; //邀请码发送次数
  private RelationTypes relationType;     //客户关联类型
  private Long province;
  private Long city;
  private Long region;
  private String identity;
  private String identityStr;
  private Long supplierId;
  private String supplierIdStr;

  private Boolean isPermanentDualRole;
  private String areaInfo;

  private Long lastExpenseTime;
  private double totalAmount;
  private double totalConsume;     //总消费
  private double totalReturnDebt;  //店铺欠客户钱
  private double totalReceivable;
  private double totalReturnAmount;
  private Long consumeTimes;  //客户累计消费次数
  private Long memberConsumeTimes;//客户会员卡累计消费次数
  private Double memberConsumeTotal;//客户会员卡累计消费金额
  private List<String> mobileList=new ArrayList<String>();

  private Double deposit; //预收款
  private Long parentId;
  private String parentIdStr;
  private boolean isCancel; // add by zhuj 是否为取消关联
  private boolean isOnlineShop = false;//是否在线店铺包括既是客户又是供应商
  private ImageCenterDTO imageCenterDTO;

  private String serviceCategoryRelationIdStr;
  private String serviceCategoryRelationContent;
  private String vehicleModelContent;
  private String vehicleModelIdStr;
  private List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList;
  private String shopVehicleBrandModelDTOListJson;
  private VehicleSelectBrandModel selectBrandModel;//选择车型时 是否是全部车型

  private boolean isObd;
  private int beStored;
  private boolean appUser; //是否是app用户
  private String shortMemo;//memo的简短形式
  private boolean hasMainContact = false;//供应商是否有主联系人

  public void setServiceCategoryRelationInfo(List<ServiceCategoryRelationDTO> serviceCategoryRelationDTOList){
    if(CollectionUtils.isNotEmpty(serviceCategoryRelationDTOList)){
      serviceCategoryRelationContent="";serviceCategoryRelationIdStr="";
      for(ServiceCategoryRelationDTO serviceCategoryRelationDTO : serviceCategoryRelationDTOList){
        serviceCategoryRelationIdStr+=serviceCategoryRelationDTO.getServiceCategoryId()+",";
        serviceCategoryRelationContent+=serviceCategoryRelationDTO.getServiceCategoryName()+",";
      }
      if(serviceCategoryRelationIdStr.length()>1) serviceCategoryRelationIdStr = serviceCategoryRelationIdStr.substring(0,serviceCategoryRelationIdStr.length()-1);
      if(serviceCategoryRelationContent.length()>1) serviceCategoryRelationContent = serviceCategoryRelationContent.substring(0,serviceCategoryRelationContent.length()-1);
    }
  }
  public void setVehicleBrandModelRelationInfo(List<VehicleBrandModelRelationDTO> vehicleBrandModelRelationDTOList) {
    if(VehicleSelectBrandModel.ALL_MODEL.equals(this.getSelectBrandModel())){
      vehicleModelContent="全部车型";
      return;
    }
    if(CollectionUtils.isNotEmpty(vehicleBrandModelRelationDTOList)){
      List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();
      vehicleModelContent="";vehicleModelIdStr="";
      for(VehicleBrandModelRelationDTO vehicleBrandModelRelationDTO : vehicleBrandModelRelationDTOList){
        vehicleModelIdStr+=vehicleBrandModelRelationDTO.getModelId()+",";
        vehicleModelContent+=vehicleBrandModelRelationDTO.getModelName()+",";
        shopVehicleBrandModelDTOList.add(new ShopVehicleBrandModelDTO(vehicleBrandModelRelationDTO));
      }
      this.setShopVehicleBrandModelDTOList(shopVehicleBrandModelDTOList);
      if(vehicleModelIdStr.length()>1) vehicleModelIdStr = vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1);
      if(vehicleModelContent.length()>1) vehicleModelContent = vehicleModelContent.substring(0,vehicleModelContent.length()-1);
    }
  }

  public List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelDTOList() {
    return shopVehicleBrandModelDTOList;
  }

  public String getShopVehicleBrandModelDTOListJson() {
    return shopVehicleBrandModelDTOListJson;
  }

  public void setShopVehicleBrandModelDTOListJson(String shopVehicleBrandModelDTOListJson) {
    this.shopVehicleBrandModelDTOListJson = shopVehicleBrandModelDTOListJson;
  }

  public void setShopVehicleBrandModelDTOList(List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList) {
    this.shopVehicleBrandModelDTOList = shopVehicleBrandModelDTOList;
    if(CollectionUtils.isNotEmpty(shopVehicleBrandModelDTOList)){
      try {
        setShopVehicleBrandModelDTOListJson(URLEncoder.encode(JsonUtil.listToJson(shopVehicleBrandModelDTOList), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
      }
    }
  }

  public String getVehicleModelContent() {
    return vehicleModelContent;
  }

  public void setVehicleModelContent(String vehicleModelContent) {
    this.vehicleModelContent = vehicleModelContent;
  }

  public String getVehicleModelIdStr() {
    return vehicleModelIdStr;
  }

  public void setVehicleModelIdStr(String vehicleModelIdStr) {
    this.vehicleModelIdStr = vehicleModelIdStr;
  }

  public VehicleSelectBrandModel getSelectBrandModel() {
    return selectBrandModel;
  }

  public void setSelectBrandModel(VehicleSelectBrandModel selectBrandModel) {
    this.selectBrandModel = selectBrandModel;
  }

  public String getServiceCategoryRelationIdStr() {
    return serviceCategoryRelationIdStr;
  }

  public void setServiceCategoryRelationIdStr(String serviceCategoryRelationIdStr) {
    this.serviceCategoryRelationIdStr = serviceCategoryRelationIdStr;
  }

  public String getServiceCategoryRelationContent() {
    return serviceCategoryRelationContent;
  }

  public void setServiceCategoryRelationContent(String serviceCategoryRelationContent) {
    this.serviceCategoryRelationContent = serviceCategoryRelationContent;
  }

  public String getIdentityStr() {
    return identityStr;
  }

  public void setIdentityStr(String identityStr) {
    this.identityStr = identityStr;
  }

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public double getTotalReturnAmount() {
    return totalReturnAmount;
  }

  public void setTotalReturnAmount(double totalReturnAmount) {
    this.totalReturnAmount = totalReturnAmount;
  }

  public boolean isCancel() {
    return isCancel;
  }

  public void setCancel(boolean cancel) {
    isCancel = cancel;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getParentIdStr() {
    return parentIdStr;
  }

  public void setParentIdStr(String parentIdStr) {
    this.parentIdStr = parentIdStr;
  }

  private boolean fromManagePage = false;   //用于判断是否来自客户详情页面，删除联系人逻辑需要

  //客户的经营范围相关
  private String businessScopeStr;
  private List<Long> thirdCategoryIds = new ArrayList<Long>();//客户的经营范围三类ids
  private String thirdCategoryIdStr;//客户的经营范围三级分类
  private String thirdCategoryNodeListJson;

  public void setBusinessScopeInfo(List<BusinessScopeDTO> businessScopeDTOList,Node node){
    if (CollectionUtils.isNotEmpty(businessScopeDTOList)) {
      StringBuffer businessScope = new StringBuffer();
      List<Node> nodeList = new ArrayList<Node>();
      Map<Long,List<Node>> thirdNodeMap = new HashMap<Long, List<Node>>();
      List<Node> thirdNodeList = null;
      for (BusinessScopeDTO businessScopeDTO : businessScopeDTOList) {
        businessScope.append(businessScopeDTO.getProductCategoryId()).append(",");
        Node n = node.findNodeInTree(businessScopeDTO.getProductCategoryId());
        nodeList.add(n);
        thirdNodeList = thirdNodeMap.get(n.getParentId());
        if(thirdNodeList==null){
          thirdNodeList = new ArrayList<Node>();
        }
        thirdNodeList.add(n);
        thirdNodeMap.put(n.getParentId(),thirdNodeList);
      }
      this.setThirdCategoryIdStr(businessScope.toString());


      if(CollectionUtils.isNotEmpty(nodeList)){
        Set<Node> pNodes = new HashSet<Node>();
        Iterator<Node> iterator = nodeList.iterator();
        while (iterator.hasNext()){
          Node n = iterator.next();
          Node pn = node.findNodeInTree(n.getParentId());
          if(pn.getChildren().size()==thirdNodeMap.get(n.getParentId()).size()){
            iterator.remove();
            pNodes.add(pn);
          }
        }
        nodeList.addAll(pNodes);
        try {
          setThirdCategoryNodeListJson(URLEncoder.encode(JsonUtil.listToJson(nodeList), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
      }
    }
  }

  public String getThirdCategoryNodeListJson() {
    return thirdCategoryNodeListJson;
  }

  public void setThirdCategoryNodeListJson(String thirdCategoryNodeListJson) {
    this.thirdCategoryNodeListJson = thirdCategoryNodeListJson;
  }

  public List<Long> getThirdCategoryIds() {
    return thirdCategoryIds;
  }

  public void setThirdCategoryIds(List<Long> thirdCategoryIds) {
    this.thirdCategoryIds = thirdCategoryIds;
  }

  public List<ContactDTO> getContactDTOList() {
    return contactDTOList;
  }

  public void setContactDTOList(List<ContactDTO> contactDTOList) {
    this.contactDTOList = contactDTOList;
  }

  public String getBusinessScopeStr() {
    return businessScopeStr;
  }

  public void setBusinessScopeStr(String businessScopeStr) {
    this.businessScopeStr = businessScopeStr;
  }

  public Long getLastExpenseTime() {
    return lastExpenseTime;
  }

  public void setLastExpenseTime(Long lastExpenseTime) {
    this.lastExpenseTime = lastExpenseTime;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
        this.setIdentityStr(StringUtils.isNotBlank(identity)?"供应商/客户":"");
    }

    public Long getRegion() {
        return region;
    }

    public void setRegion(Long region) {
        this.region = region;
    }

    public Long getCity() {

        return city;
    }

    public void setCity(Long city) {
        this.city = city;
    }

    public Long getProvince() {

        return province;
    }

    public void setProvince(Long province) {
        this.province = province;
    }

    public Long getInvitationCodeSendDate() {
    return invitationCodeSendDate;
  }

  public void setInvitationCodeSendDate(Long invitationCodeSendDate) {
    this.invitationCodeSendDate = invitationCodeSendDate;
  }

  public Integer getInvitationCodeSendTimes() {
    return invitationCodeSendTimes;
  }

  public void setInvitationCodeSendTimes(Integer invitationCodeSendTimes) {
    this.invitationCodeSendTimes = invitationCodeSendTimes;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public List<VehicleDTO> getVehicleDTOList() {
    return vehicleDTOList;
  }

  public void setVehicleDTOList(List<VehicleDTO> vehicleDTOList) {
    this.vehicleDTOList = vehicleDTOList;
  }

  public CustomerRecordDTO getCustomerRecordDTO() {
    return customerRecordDTO;
  }

  public void setCustomerRecordDTO(CustomerRecordDTO customerRecordDTO) {
    this.customerRecordDTO = customerRecordDTO;
    if (customerRecordDTO != null) {
      this.setTotalAmount(NumberUtil.doubleVal(customerRecordDTO.getTotalAmount()));        //累计消费
      this.setTotalReceivable(NumberUtil.doubleVal(customerRecordDTO.getTotalReceivable()));   //累计欠款
      this.setTotalReturnAmount(NumberUtil.doubleVal(customerRecordDTO.getTotalReturnAmount()));
      this.setTotalReturnDebt(NumberUtil.doubleVal(customerRecordDTO.getTotalPayable()));
      this.setLastExpenseTime(customerRecordDTO.getLastDate());
      this.setConsumeTimes(NumberUtil.longValue(customerRecordDTO.getConsumeTimes()));
      this.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()));
      this.setMemberConsumeTotal(NumberUtil.doubleVal(customerRecordDTO.getMemberConsumeTotal()));

    }
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public CustomerDTO() {
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getMemberNumber() {
    return memberNumber;
  }

  public void setMemberNumber(String memberNumber) {
    this.memberNumber = memberNumber;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
    this.setCustomerShopIdStr(StringUtil.valueOf(customerShopId));
  }

  public String getCustomerShopIdStr() {
    return customerShopIdStr;
  }

  public void setCustomerShopIdStr(String customerShopIdStr) {
    this.customerShopIdStr = customerShopIdStr;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    if (name != null && name.length() > 0) {
        this.firstLetters = PinyinUtil.converterToFirstSpell(name);
    }
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getIdentifierNo() {
    return identifierNo;
  }

  public void setIdentifierNo(String identifierNo) {
    this.identifierNo = identifierNo;
  }

  public Integer getGender() {
    return gender;
  }

  public void setGender(Integer gender) {
    this.gender = gender;
  }

  public Long getBirthday() {
    return birthday;
  }

  public void setBirthday(Long birthday) {
    this.birthday = birthday;
  }

  public String getBirthdayString() {
    return birthdayString;
  }

  public void setBirthdayString(String birthdayString) {
    this.birthdayString = birthdayString;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  public String getLandLineSecond() {
    return landLineSecond;
  }

  public void setLandLineSecond(String landLineSecond) {
    this.landLineSecond = landLineSecond;
  }

  public String getLandLineThird() {
    return landLineThird;
  }

  public void setLandLineThird(String landLineThired) {
    this.landLineThird = landLineThired;
  }

  public String getLandLineForAll() {
    return landLineForAll;
  }

  public void setLandLineForAll(String landLineForAll) {
    this.landLineForAll = landLineForAll;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getTaxNo() {
    return taxNo;
  }

  public void setTaxNo(String taxNo) {
    this.taxNo = taxNo;
  }

  public Long getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(Long billingAddress) {
    this.billingAddress = billingAddress;
  }

  public Long getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(Long invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
  }

  public Long getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(Long settlementType) {
    this.settlementType = settlementType;
  }

  public String getInvoiceTitle() {
    return invoiceTitle;
  }

  public void setInvoiceTitle(String invoiceTitle) {
    this.invoiceTitle = invoiceTitle;
  }

  public Long getDept() {
    return dept;
  }

  public void setDept(Long dept) {
    this.dept = dept;
  }

  public Long getAgent() {
    return agent;
  }

  public void setAgent(Long agent) {
    this.agent = agent;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
    this.setShortMemo(StringUtil.getShortStr(memo,50));
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;

  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getBankAccountName() {
    return bankAccountName;
  }

  public void setBankAccountName(String bankAccountName) {
    this.bankAccountName = bankAccountName;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getCustomerKind() {
    return customerKind;
  }

  public void setCustomerKind(String customerKind) {
    this.customerKind = customerKind;
  }

  public String getFirstLetters() {
    return firstLetters;
  }

  public void setFirstLetters(String firstLetters) {
    this.firstLetters = firstLetters;
  }

  public boolean isMobileOnly() {
    return isMobileOnly;
  }

  public void setMobileOnly(boolean mobileOnly) {
    isMobileOnly = mobileOnly;
  }

  public MemberDTO getMemberDTO() {
      return memberDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
      this.memberDTO = memberDTO;
  }

  public CustomerStatus getStatus() {
    return status;
  }

  public void setStatus(CustomerStatus status) {
    this.status = status;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<CustomerVehicleResponse> getCustomerVehicleResponses() {
    return customerVehicleResponses;
  }

  public void setCustomerVehicleResponses(List<CustomerVehicleResponse> customerVehicleResponses) {
    this.customerVehicleResponses = customerVehicleResponses;
  }

  public Double getMemberDiscount() {
    return memberDiscount;
  }

  public void setMemberDiscount(Double memberDiscount) {
    this.memberDiscount = memberDiscount;
  }

  public String getInvoiceCategoryStr() {
    return invoiceCategoryStr;
  }

  public void setInvoiceCategoryStr(String invoiceCategoryStr) {
    this.invoiceCategoryStr = invoiceCategoryStr;
  }

  public String getSettlementTypeStr() {
    return settlementTypeStr;
  }

  public void setSettlementTypeStr(String settlementTypeStr) {
    this.settlementTypeStr = settlementTypeStr;
  }

  public String getAreaStr() {
    return areaStr;
  }

  public void setAreaStr(String areaStr) {
    this.areaStr = areaStr;
  }

  public String getCustomerKindStr() {
    return customerKindStr;
  }

  public void setCustomerKindStr(String customerKindStr) {
    this.customerKindStr = customerKindStr;
  }

  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  public String getThirdCategoryIdStr() {
    return thirdCategoryIdStr;
  }

  public void setThirdCategoryIdStr(String thirdCategoryIdStr) {
    this.thirdCategoryIdStr = thirdCategoryIdStr;
  }

  public boolean getIsOnlineShop() {
    return isOnlineShop;
  }

  public void setOnlineShop(boolean onlineShop) {
    isOnlineShop = onlineShop;
  }

  /**
   * @param customerRecordDTO
   * @param updateRelation 是否更新客户与店铺关联关系
   * @param updateSupplierRelation 是否更新"同时是供应商"关系
   * @throws ParseException
   */
  public void fromCustomerRecordDTO(CustomerRecordDTO customerRecordDTO, boolean updateRelation, boolean updateSupplierRelation) throws ParseException {
    this.setId(customerRecordDTO.getCustomerId());
    this.setShopId(customerRecordDTO.getShopId());
    this.setName(customerRecordDTO.getName());
    if (customerRecordDTO.getName() != null && customerRecordDTO.getName().length() > 0) {
      this.setFirstLetters(PinyinUtil.converterToFirstSpell(customerRecordDTO.getName()));
    }
    this.setShortName(customerRecordDTO.getShortName());
    this.setAddress(customerRecordDTO.getAddress());
    this.setContact(customerRecordDTO.getContact());
    this.setMobile(customerRecordDTO.getMobile());

    this.setLandLine(customerRecordDTO.getPhone());
    this.setLandLineSecond(customerRecordDTO.getPhoneSecond());
    this.setLandLineThird(customerRecordDTO.getPhoneThird());

    this.setFax(customerRecordDTO.getFax());
    this.setMemberNumber(customerRecordDTO.getMemberNumber());
    this.setMemo(customerRecordDTO.getMemo());
    this.setArea(customerRecordDTO.getArea());
    this.setEmail(customerRecordDTO.getEmail());
    this.setBirthday(customerRecordDTO.getBirthday());
    if (!StringUtil.isEmpty(customerRecordDTO.getBirthdayString())) {
      this.setBirthday(DateUtil.convertDateStringToDateLong(TxnConstant.FORMAT_STANDARD_MONTH_DATE, customerRecordDTO.getBirthdayString()));
    }
    this.setQq(customerRecordDTO.getQq());
    this.setBank(customerRecordDTO.getBank());
    this.setBankAccountName(customerRecordDTO.getBankAccountName());
    this.setAccount(customerRecordDTO.getAccount());
    if(!StringUtil.isEmpty(customerRecordDTO.getInvoiceCategory())){
      this.setInvoiceCategory(Long.valueOf(customerRecordDTO.getInvoiceCategory()));
    }
    if(!StringUtil.isEmpty(customerRecordDTO.getSettlementType())){
      this.setSettlementType(Long.valueOf(customerRecordDTO.getSettlementType()));
    }
    this.setCustomerKind(customerRecordDTO.getCustomerKind());
    this.setProvince(customerRecordDTO.getProvince());
    this.setCity(customerRecordDTO.getCity());
    this.setRegion(customerRecordDTO.getRegion());

    if (updateRelation) {
      this.setRelationType(customerRecordDTO.getRelationType());
      this.setCustomerShopId(customerRecordDTO.getCustomerShopId());
    }
    if (updateSupplierRelation) {
      this.setIdentity(customerRecordDTO.getIdentity());
      this.setSupplierId(customerRecordDTO.getSupplierId());
    }
    if(customerRecordDTO.getIdentity() != null) {
        this.setIdentity(customerRecordDTO.getIdentity());
    }

    // add by zhuj 有多联系人 填充多联系人
    ContactDTO[] contactDTOs = customerRecordDTO.getContacts();
    if (!ArrayUtils.isEmpty(contactDTOs)) {
      boolean isHasMainContact = false;
      for (int i = 0; i < contactDTOs.length; i++) {
        contactDTOs[i].setCustomerId(customerRecordDTO.getCustomerId());
        if (contactDTOs[i] != null && contactDTOs[i].isValidContact()
            && ContactConstant.IS_MAIN_CONTACT.equals(contactDTOs[i].getIsMainContact())) {
          isHasMainContact = true;
        }
      }

      this.contacts = new ContactDTO[3];
      for (int i = 0; i < contactDTOs.length; i++) {
        if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) {
          if(!isHasMainContact){
            isHasMainContact = true;
            contactDTOs[i].setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          }
          contacts[i] = contactDTOs[i];
        }
      }
    }else{ // 没有多联系人 将页面中的联系人当做主联系人新增（就是不支持多联系人的情况下） 前提是这个是合法的联系人
      this.contacts = new ContactDTO[3];
      ContactDTO contactDTO = new ContactDTO();
      contactDTO.setShopId(customerRecordDTO.getShopId());
      contactDTO.setName(customerRecordDTO.getContact());
      contactDTO.setEmail(customerRecordDTO.getEmail());
      contactDTO.setMobile(customerRecordDTO.getMobile());
      contactDTO.setQq(customerRecordDTO.getQq());
      contactDTO.setDisabled(ContactConstant.ENABLED);
      contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
      contactDTO.setLevel(ContactConstant.LEVEL_0);
      contactDTO.setShopId(customerRecordDTO.getShopId());
      contactDTO.setCustomerId(this.getId());
      if (contactDTO.isValidContact()){ // 这个判断必须有...
        contacts[0] = contactDTO;
      }
    }

  }
  public void fillingContacts (){
    ContactDTO[] tempContacts = this.contacts;

    boolean hasMainContact = false;

    this.contacts = new ContactDTO[3];
    if(!ArrayUtils.isEmpty(tempContacts)){
      for (int i = 0; i < tempContacts.length; i++) {
        if (tempContacts[i] != null && tempContacts[i].isValidContact()) {
          contacts[i] = tempContacts[i];
        }

        if (tempContacts[i] != null && NumberUtil.intValue(tempContacts[i].getIsMainContact()) == 1) {
          hasMainContact = true;
        }
      }
    }

    if (!hasMainContact) {
      if (contacts[0] == null) {
        contacts[0] = new ContactDTO();
      }
      contacts[0].setIsMainContact(1);
    }


  }
  /**
   * 比对Customer与CustomerDTO是否一致. 比对字段：
   * name, company, contact, mobile, landline, address
   * @param customerDTO
   * @return
   */
  public boolean compareHistory(CustomerDTO customerDTO) {
    if(getStatus() == CustomerStatus.DISABLED){
      return false;
    }
    if(!StringUtil.compareSame(customerDTO.getName(), getName())){
      return false;
    }
    if(!StringUtil.compareSame(customerDTO.getContact(), getContact())){
      return false;
    }
    if(!StringUtil.compareSame(customerDTO.getMobile(), getMobile())){
      return false;
    }
    if(!StringUtil.compareSame(customerDTO.getLandLine(), getLandLine())){
      return false;
    }
    if(!StringUtil.compareSame(customerDTO.getAddress(), getAddress())){
      return false;
    }
    return true;
  }

  public void fromInsuranceOrderDTO(InsuranceOrderDTO insuranceOrderDTO) {
    if(insuranceOrderDTO == null){
      return;
    }
    this.setId(insuranceOrderDTO.getCustomerId());
    this.setShopId(insuranceOrderDTO.getShopId());
    this.setName(insuranceOrderDTO.getCustomer());
    this.setMobile(insuranceOrderDTO.getMobile());
  }

  public double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(double totalReceivable) {
    this.totalReceivable = totalReceivable;
  }

  public double getTotalConsume() {
    return totalConsume;
  }

  public void setTotalConsume(double totalConsume) {
    this.totalConsume = totalConsume;
  }

  public double getTotalReturnDebt() {
    return totalReturnDebt;
  }

  public void setTotalReturnDebt(double totalReturnDebt) {
    this.totalReturnDebt = totalReturnDebt;
  }

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  public Boolean getPermanentDualRole() {
    return isPermanentDualRole;
  }

  public void setPermanentDualRole(Boolean permanentDualRole) {
    isPermanentDualRole = permanentDualRole;
  }

  public String getSupplierIdStr() {
    return supplierId==null?null:supplierId.toString();
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }

  public void fromCustomerShopDTO(ShopDTO customerShopDTO) {
    try{
    if (customerShopDTO != null) {
      this.setCustomerShopId(customerShopDTO.getId());
      this.setName(customerShopDTO.getName());
      this.setAddress(customerShopDTO.getAddress());
      this.setProvince(customerShopDTO.getProvince());
      this.setCity(customerShopDTO.getCity());
      this.setRegion(customerShopDTO.getRegion());
      this.setMobile(customerShopDTO.getStoreManagerMobile());
      this.setContact(customerShopDTO.getStoreManager());
      this.setLandLine(customerShopDTO.getLandline());
      this.setFax(customerShopDTO.getFax());
      this.setEmail(customerShopDTO.getEmail());
      this.setQq(customerShopDTO.getQq());
      this.setBank(customerShopDTO.getBank());
      this.setAccount(customerShopDTO.getAccount());
      this.setBankAccountName(customerShopDTO.getAccount_name());
      this.setSelectBrandModel(customerShopDTO.getShopSelectBrandModel());
      if (!ArrayUtils.isEmpty(customerShopDTO.getContacts())) {
        List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
        int i = 1;
        for (ContactDTO dto : customerShopDTO.getContacts()) {
          if(dto!=null){
            ContactDTO customerContactDTO = dto.clone();
            customerContactDTO.setShopId(getShopId());
            customerContactDTO.setId(null);    //shop_contact克隆过来的带有config库中id
            customerContactDTO.setIdStr(null);
            if(NumberUtil.isEqual(customerContactDTO.getIsShopOwner(), ContactConstant.IS_SHOP_OWNER)){
              customerContactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
              customerContactDTO.setLevel(ContactConstant.LEVEL_0);
            }else{
              customerContactDTO.setIsMainContact(ContactConstant.NOT_MAIN_CONTACT);
              customerContactDTO.setLevel(i++);
            }
            contactDTOs.add(customerContactDTO);
            mobileList.add(dto.getMobile());
          }
        }
        this.setContacts(contactDTOs.toArray(new ContactDTO[contactDTOs.size()]));
      }
    }
    }catch(CloneNotSupportedException e){
      LOG.error(e.getMessage(), e);
    }
  }

  public List<String> getContactMobiles() {
    if (!ArrayUtils.isEmpty(this.getContacts())) {
      for (ContactDTO dto : this.getContacts()) {
        if (dto != null){ // add by zhuj 导致报500 转换为json的时候
          mobileList.add(dto.getMobile());
        }
      }
    }
    return mobileList;
  }

  public void setPartShopDTOInfo(ShopDTO customerShopDTO,Boolean isRelated) {
    if(customerShopDTO != null){
      if(isRelated){
        this.setName(customerShopDTO.getName());
        this.setCustomerShopId(customerShopDTO.getId());
        this.setMobile(customerShopDTO.getStoreManagerMobile());
        this.setContact(customerShopDTO.getStoreManager());
        this.setLandLine(customerShopDTO.getLandline());
        this.setFax(customerShopDTO.getFax());
        this.setEmail(customerShopDTO.getEmail());
        this.setBusinessScopeStr(customerShopDTO.getBusinessScopeStr());
        this.setQqArray(customerShopDTO.getQqArray());
      }else{
        this.setName(customerShopDTO.getName());
        this.setCustomerShopId(customerShopDTO.getId());
        if(StringUtils.isNotBlank(customerShopDTO.getStoreManagerMobile()) && customerShopDTO.getStoreManagerMobile().length()>3){
          this.setMobile(customerShopDTO.getStoreManagerMobile().substring(0,3)+"********");
        }

        if(StringUtils.isNotBlank(customerShopDTO.getStoreManager())){
          this.setContact(customerShopDTO.getStoreManager().substring(0,1)+"**");
        }

        this.setLandLine("********");
        this.setFax("********");
        if(StringUtils.isNotBlank(customerShopDTO.getEmail()) && customerShopDTO.getEmail().indexOf("@")>-1){
          this.setEmail("******@"+customerShopDTO.getEmail().split("@")[1]);
        }else{
          this.setEmail("******");
        }
        this.setBusinessScopeStr(customerShopDTO.getBusinessScopeStr());
      }

    }
  }

  public void setAreaInfo(String areaInfo) {
    this.areaInfo = areaInfo;
  }

  public String getAreaInfo() {
    return areaInfo;
  }


  public ContactDTO[] getContacts() {
    return contacts;
  }

  public void setContacts(ContactDTO[] contacts) {
    this.contacts = contacts;

    if (ArrayUtil.isEmpty(contacts)) {
      setHasMainContact(false);
    } else {
      for (ContactDTO contactDTO : contacts) {
        if (contactDTO != null && NumberUtil.intValue(contactDTO.getIsMainContact()) == 1) {
          setHasMainContact(true);
        }
      }
    }
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    this.setContactIdStr(StringUtil.valueOf(contactId));
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  public boolean isFromManagePage() {
    return fromManagePage;
  }

  public void setFromManagePage(boolean fromManagePage) {
    this.fromManagePage = fromManagePage;
  }

  public void fromSupplierDTO(SupplierDTO supplierDTO) {
    this.setName(supplierDTO.getName());
    this.setProvince(supplierDTO.getProvince());
    this.setCity(supplierDTO.getCity());
    this.setRegion(supplierDTO.getRegion());
    this.setAddress(supplierDTO.getAddress());
    this.setShortName(supplierDTO.getAbbr());
    this.setContact(supplierDTO.getContact());
    this.setMobile(supplierDTO.getMobile());
    this.setEmail(supplierDTO.getEmail());
    this.setQq(supplierDTO.getQq());
    this.setLandLine(supplierDTO.getLandLine());
    this.setLandLineSecond(supplierDTO.getLandLineSecond());
    this.setLandLineThird(supplierDTO.getLandLineThird());
    this.compositeLandline();
    this.setFax(supplierDTO.getFax());
    this.setBank(supplierDTO.getBank());
    this.setAccount(supplierDTO.getAccount());
    this.setBankAccountName(supplierDTO.getAccountName());
    this.setSettlementType(supplierDTO.getSettlementTypeId());
    this.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
    this.setInvoiceCategoryStr(supplierDTO.getInvoiceCategory());
    this.setSettlementTypeStr(supplierDTO.getSettlementType());
    this.setMemo(supplierDTO.getMemo());
    this.setContacts(supplierDTO.getContacts()); // add by zhuj
    this.setSelectBrandModel(supplierDTO.getSelectBrandModel());
    this.setVehicleModelIdStr(supplierDTO.getVehicleModelIdStr());
  }

  public void fromWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) {
    if(washBeautyOrderDTO == null){
      return;
    }
    this.setShopId(washBeautyOrderDTO.getShopId());
    this.setName(washBeautyOrderDTO.getCustomer());
    this.setContact(washBeautyOrderDTO.getContact());
    this.setMobile(washBeautyOrderDTO.getMobile());
    this.setLandLine(washBeautyOrderDTO.getLandLine());
    this.setAddress(washBeautyOrderDTO.getAddress());

    if (StringUtils.isNotBlank(washBeautyOrderDTO.getCustomer())) {
      setFirstLetters(PinyinUtil.converterToFirstSpell(washBeautyOrderDTO.getCustomer()));
    }
    boolean isUpdateContact = false;
    if(washBeautyOrderDTO.getContactId() != null){
      if(!ArrayUtils.isEmpty(this.getContacts())){
        for(ContactDTO contactDTO :getContacts()){
          if(contactDTO!=null && contactDTO.isValidContact() && NumberUtil.isEqual(washBeautyOrderDTO.getContactId(), contactDTO.getId())){
            isUpdateContact = true;
            contactDTO.setMobile(washBeautyOrderDTO.getMobile());
          }
        }
      }
    }
    if(!isUpdateContact){
      if (hasValidContact()) {
        for (ContactDTO contactDTO : getContacts()) {
          if (contactDTO != null && contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
            contactDTO.setMobile(washBeautyOrderDTO.getMobile());
          }
        }
      } else {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName(washBeautyOrderDTO.getContact());
        contactDTO.setMobile(washBeautyOrderDTO.getMobile());
        contactDTO.setLevel(0);
        contactDTO.setDisabled(1);
        contactDTO.setIsMainContact(1);
        contactDTO.setShopId(washBeautyOrderDTO.getShopId());
        setContacts(new ContactDTO[]{contactDTO});
      }
    }
  }

  public boolean hasValidContact() {
    ContactDTO[] contactDTOs = this.getContacts();
    if (!ArrayUtils.isEmpty(contactDTOs)) {
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidMainContact(){
    if (StringUtils.isBlank(this.getContact()) && StringUtils.isBlank(this.getMobile()) && StringUtils.isBlank(this.getEmail()) && StringUtils.isBlank(this.getQq())) {
      return false;
    }
    return true;
  }


  public void fromRepairOrderDTO(RepairOrderDTO repairOrderDTO) {
    if(repairOrderDTO != null){
      setMobile(repairOrderDTO.getMobile());
      setLandLine(repairOrderDTO.getLandLine());
      setName(repairOrderDTO.getCustomerName());
      setShopId(repairOrderDTO.getShopId());
      if (StringUtils.isNotBlank(repairOrderDTO.getCustomerName())) {
        setFirstLetters(PinyinUtil.converterToFirstSpell(repairOrderDTO.getCustomerName()));
      }
      boolean isUpdateContact = false;
      if(StringUtil.isNotEmpty(repairOrderDTO.getContact())){
        if(!ArrayUtils.isEmpty(this.getContacts())){
          for(ContactDTO contactDTO :getContacts()){
            if(contactDTO!=null && contactDTO.isValidContact() && NumberUtil.isEqual(repairOrderDTO.getContactId(), contactDTO.getId())){
              isUpdateContact = true;
              contactDTO.setMobile(repairOrderDTO.getMobile());
              contactDTO.setQq(repairOrderDTO.getQq());
              contactDTO.setEmail(repairOrderDTO.getEmail());
            }
          }
        }
      }
      if(!isUpdateContact){
        if (!ArrayUtil.isEmpty(this.getContacts())) {
          for (ContactDTO contactDTO : getContacts()) {
            if (contactDTO != null && contactDTO.getIsMainContact()!=null && contactDTO.getIsMainContact()== 1) {
              contactDTO.setMobile(repairOrderDTO.getMobile());
            }
          }
        } else {
          ContactDTO contactDTO = new ContactDTO();
          contactDTO.setName(repairOrderDTO.getContact());
          contactDTO.setMobile(repairOrderDTO.getMobile());
          contactDTO.setLevel(0);
          contactDTO.setDisabled(1);
          contactDTO.setIsMainContact(1);
          contactDTO.setShopId(repairOrderDTO.getShopId());
          setContacts(new ContactDTO[]{contactDTO});
        }
      }
    }
  }

  public void fromAppointOrderDTO(AppointOrderDTO appointOrderDTO) {
    if(appointOrderDTO != null){
      setMobile(appointOrderDTO.getCustomerMobile());
      setLandLine(appointOrderDTO.getCustomerLandLine());
      setName(appointOrderDTO.getCustomer());
      setShopId(appointOrderDTO.getShopId());
      if (StringUtils.isNotBlank(appointOrderDTO.getCustomer())) {
        setFirstLetters(PinyinUtil.converterToFirstSpell(appointOrderDTO.getCustomer()));
      }
      boolean isUpdateContact = false;
      if(appointOrderDTO.getCustomerMobile() != null){
        if(!ArrayUtils.isEmpty(this.getContacts())){
          for(ContactDTO contactDTO :getContacts()){
            if(contactDTO!=null && contactDTO.isValidContact()){
              isUpdateContact = true;
              contactDTO.setMobile(appointOrderDTO.getCustomerMobile());
              break;
            }
          }
        }
      }
      if(!isUpdateContact){
        if (!ArrayUtils.isEmpty(this.getContacts())) {
          for (ContactDTO contactDTO : getContacts()) {
            if (contactDTO != null && contactDTO.getIsMainContact()!=null && contactDTO.getIsMainContact()== 1) {
              contactDTO.setMobile(appointOrderDTO.getCustomerMobile());
            }
          }
        } else {
          ContactDTO contactDTO = new ContactDTO();
          contactDTO.setMobile(appointOrderDTO.getCustomerMobile());
          contactDTO.setLevel(0);
          contactDTO.setDisabled(1);
          contactDTO.setIsMainContact(1);
          contactDTO.setShopId(appointOrderDTO.getShopId());
          setContacts(new ContactDTO[]{contactDTO});
        }
      }
    }
  }

  public void addFromAppUserDTO(AppUserDTO appUserDTO) {
    if (appUserDTO != null) {
      setMobile(appUserDTO.getMobile());
      setName(appUserDTO.getName());
      if (StringUtils.isNotBlank(appUserDTO.getName())) {
        setFirstLetters(PinyinUtil.converterToFirstSpell(appUserDTO.getName()));
      }
      if (StringUtils.isNotEmpty(appUserDTO.getMobile())) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setMobile(appUserDTO.getMobile());
        contactDTO.setLevel(0);
        contactDTO.setDisabled(1);
        contactDTO.setIsMainContact(1);
        contactDTO.setShopId(getShopId());
        setContacts(new ContactDTO[]{contactDTO});
      }
    }
  }

  public List<String> getMobileList() {
    return mobileList;
  }

  public void setMobileList(List<String> mobileList) {
    this.mobileList = mobileList;
  }

  public String getQqArray() {
    StringBuffer qqStr = new StringBuffer();
    if (!ArrayUtils.isEmpty(contacts)) {
      for (int i = 0; i < contacts.length; i++) {
        if (contacts[i] != null && StringUtil.isNotEmpty(contacts[i].getQq())) {
          qqStr.append(contacts[i].getQq()).append(",");
        }
      }
      if (qqStr.length() > 0) {
        setQqArray(qqStr.toString());
      }
    }
    return qqArray;
  }

  public void setQqArray(String qqArray) {
    this.qqArray = qqArray;
  }

  //销售退货单跟新现有的customerDTO 信息组装
  public void updateFromSaleReturnDTO(SalesReturnDTO salesReturnDTO) {
    if (salesReturnDTO != null) {
      setMobile(salesReturnDTO.getMobile());
      setName(salesReturnDTO.getCustomer());
      setShopId(salesReturnDTO.getShopId());
      setContact(salesReturnDTO.getContact());
//      setLandLine(salesReturnDTO.getLandline());
      if (getRelationType() == null) {
        setRelationType(RelationTypes.UNRELATED);
      }
      //单据上传入联系人信息
      if (StringUtils.isNotEmpty(salesReturnDTO.getContact()) || StringUtils.isNotEmpty(salesReturnDTO.getMobile())) {
        if (ArrayUtils.isEmpty(getContacts())) {
          ContactDTO contactDTO = new ContactDTO();
          contactDTO.setDisabled(ContactConstant.ENABLED);
          contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          contactDTO.setCustomerId(salesReturnDTO.getCustomerId());
          contactDTO.setLevel(ContactConstant.LEVEL_0);
          contactDTO.setShopId(salesReturnDTO.getShopId());
          contactDTO.setMobile(salesReturnDTO.getMobile());
          contactDTO.setName(salesReturnDTO.getContact());
          ContactDTO[] contactDTOs = new ContactDTO[1];
          contactDTOs[0] = contactDTO;
        } else {
          boolean isUpdate = false;
          for (ContactDTO contactDTO : getContacts()) {
            if (contactDTO != null) {
              if (NumberUtil.isEqual(salesReturnDTO.getContactId(), contactDTO.getId())) {
                isUpdate = true;
                contactDTO.setName(salesReturnDTO.getContact());
                if (StringUtil.isNotEmpty(salesReturnDTO.getMobile())) {
                  contactDTO.setMobile(salesReturnDTO.getMobile());
                }
              }
            }
          }
          if (!isUpdate) {
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setDisabled(ContactConstant.ENABLED);
            contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
            contactDTO.setCustomerId(salesReturnDTO.getCustomerId());
            contactDTO.setSupplierId(getSupplierId());
            contactDTO.setLevel(ContactConstant.LEVEL_0);
            contactDTO.setShopId(salesReturnDTO.getShopId());
            contactDTO.setMobile(salesReturnDTO.getMobile());
            contactDTO.setName(salesReturnDTO.getContact());
            getContacts()[0] = contactDTO;
          }
        }
      }
    }
  }

  public void updateFromSaleOrderDTO(SalesOrderDTO salesOrderDTO) {
    if (salesOrderDTO != null) {
      setMobile(salesOrderDTO.getMobile());
      setName(salesOrderDTO.getCustomer());
      setShopId(salesOrderDTO.getShopId());
      setContact(salesOrderDTO.getContact());
      if (StringUtils.isNotEmpty(salesOrderDTO.getLandline())) {
        setLandLine(salesOrderDTO.getLandline());
      }
      if (getRelationType() == null) {
        setRelationType(RelationTypes.UNRELATED);
      }
      //单据上传入联系人信息
      if (StringUtils.isNotEmpty(salesOrderDTO.getContact()) || StringUtils.isNotEmpty(salesOrderDTO.getMobile())) {
        ContactDTO contactDTO = new ContactDTO();
        if (salesOrderDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
          contactDTO.setDisabled(ContactConstant.ENABLED);
          contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          contactDTO.setCustomerId(salesOrderDTO.getCustomerId());
          contactDTO.setSupplierId(getSupplierId());
          contactDTO.setLevel(ContactConstant.LEVEL_0);
          contactDTO.setShopId(salesOrderDTO.getShopId());
          contactDTO.setMobile(salesOrderDTO.getMobile());
          contactDTO.setName(salesOrderDTO.getContact());
        } else {
          boolean isUpdate = false;
          for (ContactDTO temDTO : getContacts()) {
            if (temDTO == null) {
              continue;
            }
            if (NumberUtil.isEqual(salesOrderDTO.getContactId(), temDTO.getId())) {
              isUpdate = true;
              contactDTO = temDTO;
              contactDTO.setMobile(salesOrderDTO.getMobile());
              contactDTO.setName(salesOrderDTO.getContact());
            }
          }
          if (!isUpdate) {
            contactDTO.setDisabled(ContactConstant.ENABLED);
            contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
            contactDTO.setCustomerId(salesOrderDTO.getCustomerId());
            contactDTO.setSupplierId(getSupplierId());
            contactDTO.setLevel(ContactConstant.LEVEL_0);
            contactDTO.setShopId(salesOrderDTO.getShopId());
            contactDTO.setMobile(salesOrderDTO.getMobile());
            contactDTO.setName(salesOrderDTO.getContact());
          }
        }
        ContactDTO[] contactDTOs = new ContactDTO[1];
        contactDTOs[0] = contactDTO;
        setContacts(contactDTOs);
      }
    }
  }

  public void createFromSaleOrderDTO(SalesOrderDTO salesOrderDTO) {
    if (salesOrderDTO != null) {
      setMobile(salesOrderDTO.getMobile());
      setName(salesOrderDTO.getCustomer());
      setShopId(salesOrderDTO.getShopId());
      setContact(salesOrderDTO.getContact());
      setLandLine(salesOrderDTO.getLandline());
      if (getRelationType() == null) {
        setRelationType(RelationTypes.UNRELATED);
      }
      //单据上传入联系人信息
      if (StringUtils.isNotEmpty(salesOrderDTO.getContact()) || StringUtils.isNotEmpty(salesOrderDTO.getMobile())) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setDisabled(ContactConstant.ENABLED);
        contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
        contactDTO.setCustomerId(salesOrderDTO.getCustomerId());
        contactDTO.setLevel(ContactConstant.LEVEL_0);
        contactDTO.setShopId(salesOrderDTO.getShopId());
        contactDTO.setMobile(salesOrderDTO.getMobile());
        contactDTO.setName(salesOrderDTO.getContact());
        ContactDTO[] contactDTOs = new ContactDTO[1];
        contactDTOs[0] = contactDTO;
        setContacts(contactDTOs);
      }
    }
  }

  public String getMainContactMobile() {
    if(ArrayUtil.isNotEmpty(this.getContacts())){
      for (ContactDTO dto : getContacts()) {
         if(dto != null && StringUtil.isNotEmpty(dto.getMobile())){
           return dto.getMobile();
         }
      }
    }
    return "";
  }

  public void updateMobile(String mobile) {
    if(!ArrayUtils.isEmpty(getContacts()) && hasValidContact()){
      for(ContactDTO contactDTO : getContacts()){
        if(contactDTO!=null && contactDTO.getIsMainContact()!=null && contactDTO.getIsMainContact() == 1){
          contactDTO.setMobile(mobile);
          break;
        }
      }
    }else{
      ContactDTO contactDTO = new ContactDTO(null, getName(), mobile, getEmail(), getQq(), getId(), null, getShopId(), 0, 1, 1, null);
      ContactDTO[] contactDTOs = new ContactDTO[]{contactDTO};
      setContacts(contactDTOs);
    }
  }

  public void createFromSaleReturnDTO(SalesReturnDTO salesReturnDTO) {
    if (salesReturnDTO != null) {
       setMobile(salesReturnDTO.getMobile());
       setName(salesReturnDTO.getCustomer());
       setShopId(salesReturnDTO.getShopId());
       setContact(salesReturnDTO.getContact());
       setLandLine(salesReturnDTO.getLandline());
       if (getRelationType() == null) {
         setRelationType(RelationTypes.UNRELATED);
       }
       //单据上传入联系人信息
       if (StringUtils.isNotEmpty(salesReturnDTO.getContact()) || StringUtils.isNotEmpty(salesReturnDTO.getMobile())) {
         ContactDTO contactDTO = new ContactDTO();
         contactDTO.setDisabled(ContactConstant.ENABLED);
         contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
         contactDTO.setCustomerId(salesReturnDTO.getCustomerId());
         contactDTO.setLevel(ContactConstant.LEVEL_0);
         contactDTO.setShopId(salesReturnDTO.getShopId());
         contactDTO.setMobile(salesReturnDTO.getMobile());
         contactDTO.setName(salesReturnDTO.getContact());
         ContactDTO[] contactDTOs = new ContactDTO[1];
         contactDTOs[0] = contactDTO;
         setContacts(contactDTOs);
       }
     }
  }

  public CustomerDTO fromCustomerOrSupplierDTO(CustomerOrSupplierDTO csDTO) {
    if (csDTO == null) return null;
    this.setShopId(csDTO.getShopId());
    this.setName(csDTO.getName());
    this.setShortName(csDTO.getAbbr());
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
    if (csDTO.getCustomerOrSupplierId() == null) {
      this.setRelationType(RelationTypes.UNRELATED);
    }
    if (StringUtils.isNotEmpty(csDTO.getContact()) || StringUtils.isNotEmpty(csDTO.getMobile())) {
      ContactDTO contactDTO = new ContactDTO();
      if (csDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
        contactDTO.setDisabled(ContactConstant.ENABLED);
        contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
        contactDTO.setCustomerId(csDTO.getCustomerOrSupplierId());
        contactDTO.setSupplierId(getSupplierId());
        contactDTO.setLevel(ContactConstant.LEVEL_0);
        contactDTO.setShopId(csDTO.getShopId());
        contactDTO.setMobile(csDTO.getMobile());
        contactDTO.setName(csDTO.getContact());
        contactDTO.setEmail(csDTO.getEmail());
        contactDTO.setQq(csDTO.getQq());
      } else {
        boolean isUpdate = false;
        if(!ArrayUtils.isEmpty(getContacts())){
          for (ContactDTO temDTO : getContacts()) {
            if (temDTO == null) {
              continue;
            }
            if (NumberUtil.isEqual(csDTO.getContactId(), temDTO.getId())) {
              isUpdate = true;
              contactDTO = temDTO;
              contactDTO.setMobile(csDTO.getMobile());
              contactDTO.setName(csDTO.getContact());
            }
          }
        }

        if (!isUpdate) {
          contactDTO.setDisabled(ContactConstant.ENABLED);
          contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          contactDTO.setCustomerId(csDTO.getCustomerOrSupplierId());
          contactDTO.setSupplierId(getSupplierId());
          contactDTO.setLevel(ContactConstant.LEVEL_0);
          contactDTO.setShopId(csDTO.getShopId());
          contactDTO.setMobile(csDTO.getMobile());
          contactDTO.setName(csDTO.getContact());
          contactDTO.setEmail(csDTO.getEmail());
          contactDTO.setQq(csDTO.getQq());
        }
      }
      ContactDTO[] contactDTOs = new ContactDTO[1];
      contactDTOs[0] = contactDTO;
      setContacts(contactDTOs);
    }
    return this;
  }

  public void setContactListUsingArray(){
    if(ArrayUtils.isEmpty(getContacts())){
      return;
    }
    contactDTOList = new ArrayList<ContactDTO>();
    for(ContactDTO contactDTO : getContacts()){
      if(contactDTO == null){
        continue;
      }
      contactDTOList.add(contactDTO);
    }
  }

  public void setAreaByAreaNo(Map<Long, AreaDTO> areaMap) {
    StringBuffer sb = new StringBuffer();
    if (areaMap != null && !areaMap.isEmpty()) {
      if (this.getProvince() != null) {
        AreaDTO provinceArea = areaMap.get(this.getProvince());
        if (provinceArea != null) {
          sb.append(provinceArea.getName());
        }
      }
      if (this.getCity() != null) {
        AreaDTO cityArea = areaMap.get(this.getCity());
        if (cityArea != null) {
          sb.append(cityArea.getName());
        }
      }
      if (this.getRegion() != null) {
        AreaDTO regionArea = areaMap.get(this.getRegion());
        if (regionArea != null) {
          sb.append(regionArea.getName());
        }
      }
    }
    this.setAreaStr(sb.toString()); // 页面里面使用areaStr这个字段
  }

  public Set<Long> buildAreaNoSet() {
    Set<Long> areaNos = new HashSet<Long>();
    areaNos.add(this.getProvince());
    areaNos.add(this.getCity());
    areaNos.add(this.getRegion());
    return areaNos;
  }

  //校验是否至少有1个联系人是新增的
  public boolean isAddContacts() {
    boolean isAdd = false;
    if(!ArrayUtils.isEmpty(getContacts())){
       for(ContactDTO contactDTO :getContacts()){
         if(contactDTO != null && contactDTO.getId() == null && contactDTO.isValidContact()){
           isAdd = true;
           break;
         }
       }
    }
    return isAdd;
  }

  public void fillUnEditInfo(CustomerDTO dbCustomerDTO){
    this.setCustomerShopId(dbCustomerDTO.getCustomerShopId());
    this.setDept(dbCustomerDTO.getDept());
    this.setMemberNumber(dbCustomerDTO.getMemberNumber());
    this.setInvitationCodeSendTimes(dbCustomerDTO.getInvitationCodeSendTimes());
    this.setInvitationCodeSendDate(dbCustomerDTO.getInvitationCodeSendDate());
    this.setPermanentDualRole(dbCustomerDTO.getPermanentDualRole());
  }

  public boolean getIsObd() {
    return isObd;
  }

  public void setIsObd(boolean isObd) {
    this.isObd = isObd;
  }

  public int getBeStored() {
    return beStored;
  }

  public void setBeStored(int beStored) {
    this.beStored = beStored;
  }

  public List<CarDTO> getCarDTOList() {
    return carDTOList;
  }

  public void setCarDTOList(List<CarDTO> carDTOList) {
    this.carDTOList = carDTOList;
  }

  public AppUserDTO toAppUserDTO(List<ContactDTO> contactDTOs, List<VehicleDTO> vehicleDTOs) {
    AppUserDTO appUserDTO = new AppUserDTO();
    appUserDTO.setCustomerId(getId());
    appUserDTO.setCustomerShopId(getShopId());
    if (CollectionUtil.isNotEmpty(contactDTOs)) {
      ContactDTO contactDTO = contactDTOs.get(0);
      appUserDTO.setName(contactDTO.getName());
      appUserDTO.setMobile(contactDTO.getMobile());
      appUserDTO.getCustomerContactIds().add(contactDTO.getId());
    }
    if (CollectionUtil.isNotEmpty(vehicleDTOs)) {
      List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
      for (VehicleDTO dto : vehicleDTOs) {
        appVehicleDTOList.add(dto.toAppVehicleDTO());
      }
      appUserDTO.setAppVehicleDTOs(appVehicleDTOList);
    }
    return appUserDTO;
  }

  public boolean isAppUser() {
    return appUser;
  }

  public void setAppUser(boolean appUser) {
    this.appUser = appUser;
  }

  public Long getMemberConsumeTimes() {
    return memberConsumeTimes;
  }

  public void setMemberConsumeTimes(Long memberConsumeTimes) {
    this.memberConsumeTimes = memberConsumeTimes;
  }

  public Double getMemberConsumeTotal() {
    return memberConsumeTotal;
  }

  public void setMemberConsumeTotal(Double memberConsumeTotal) {
    this.memberConsumeTotal = memberConsumeTotal;
  }

  public Long getConsumeTimes() {
    return consumeTimes;
  }

  public void setConsumeTimes(Long consumeTimes) {
    this.consumeTimes = consumeTimes;
  }

  public String getShortMemo() {
    return shortMemo;
  }

  public void setShortMemo(String shortMemo) {
    this.shortMemo = shortMemo;
  }

  public void compositeLandline(){
    StringBuffer landLineForAll=new StringBuffer("");
    if(StringUtils.isNotBlank(this.getLandLine())){
      landLineForAll.append(this.getLandLine());
    }
    if(StringUtils.isNotBlank(this.getLandLineSecond())){
      if(StringUtils.isBlank(this.getLandLine())){
        landLineForAll.append(this.getLandLineSecond());
      }else{
        landLineForAll.append("/").append(this.getLandLineSecond());
      }
    }
    if(StringUtils.isNotBlank(this.getLandLineThird())){
      if(StringUtils.isNotBlank(this.getLandLineSecond())||StringUtils.isNotBlank(this.getLandLine())){
        landLineForAll.append("/").append(this.getLandLineThird());
      }else{
        landLineForAll.append(this.getLandLineThird());
      }

    }
    this.setLandLineForAll(landLineForAll.toString());
  }

  public boolean isHasMainContact() {
    return hasMainContact;
  }

  public void setHasMainContact(boolean hasMainContact) {
    this.hasMainContact = hasMainContact;
  }
}
