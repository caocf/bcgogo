package com.bcgogo.config.model;

import com.bcgogo.api.AppShopDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.shop.*;
import com.bcgogo.enums.txn.finance.BuyChannels;
import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: XiaoJian
 * Date: 10/10/11
 */
@Entity
@Table(name = "shop")
public class Shop extends LongIdentifier {
  Logger LOG = LoggerFactory.getLogger(Shop.class);

  private String name;
  private String legalRep;
  private String no;
  private Long areaId;
  @Deprecated  //by ZhangJuntao use detailAddress install
  private String address;
  private String detailAddress;
  private String zip;
  private String contact;
  private String landline;
  private String mobile;
  private String fax;
  private String email;
  private String qq;
  private String bank;
  private String account;
  private Long categoryId;
  private String account_name;
  private String reviewer;
  private Long reviewDate;
  private String memo;
  private String owner;
  private String operator;
  private Double softPrice;
  private Double bargainPrice;
  private String operationMode;
  private String businessHours;
  private Long established;
  private String qualification;
  private String personnel;
  private String area;
  private String businessScope;
  private String relatedBusiness;
  private String feature;
  private String storeManager;
  private String storeManagerMobile;

  private String shortname;
  private String licencePlate;
  private String photo;
  private Long shopVersionId;
  private Long registrationDate;//注册时间
  private Long submitApplicationDate;//提交申请时间
  private String usedSoftware;   //使用其他软件
  private NetworkType networkType;
  private YesNo hasComputer;//是否有电脑
  private Long wholesalerShopId;//指向该店的批发商shopId（仅限一对多关系）
  private Long province;     //省
  private Long city;          //市
  private Long region;        //区域
  private Integer relativeCustomerAmount;//关联客户数量
  private ShopLevel shopLevel;
  private Long trialStartTime;  //试用开始时间
  private Long trialEndTime;  //试用结束时间
  private Long usingEndTime;  //使用结束时间
  private PaymentStatus paymentStatus;
  private BargainStatus bargainStatus;
  private String url;
  private Long shopPhotoId;
  private Long businessLicenseId;
  private ShopKind shopKind;
  private ShopStatus shopStatus;
  private ShopState shopState;
  private RegisterType registerType;
  private LocateStatus locateStatus = LocateStatus.IN_ACTIVE; //坐标状态
  private Long clueInputTime;
  private ShopRecommendedType shopRecommendedType;
  private BuyChannels buyChannels;
  private Integer shopRecommendedGrade;

  private String namePy;
  private String nameFl;
  private Long followId;      //跟进人
  private String followName;  //跟进人
  private String agent;       //销售人
  private String agentId;    //
  private String agentMobile;
  private VehicleSelectBrandModel shopSelectBrandModel; //注册时选择车型时 是否是全部车型

  private String coordinateLat;//店铺纬度信息
  private String coordinateLon;//店铺经度信息

  public static final String VALID_Shop_NAME_REGEX = ".*";
  private ChargeType chargeType;    //收费类型，一次性或者按年收费
  private String accidentMobile;//事故专员手机

  private Double adPricePerMonth;//每个月广告费
  private Long adStartDate;//广告开始投放时间
  private Long adEndDate;//广告投放结束时间
  private ProductAdType productAdType;//广告类型


  public Shop() {
    super();
  }

  public AppShopDTO toAppShopDTO() {
    AppShopDTO dto = new AppShopDTO();
    dto.setId(getId());
    dto.setName(getName());
    if (getCoordinateLon() != null && getCoordinateLat() != null)
      dto.setCoordinate(getCoordinateLon() + "," + getCoordinateLat());
    dto.setMobile(getMobile());
    dto.setAddress(getAddress());
    dto.setAccidentMobile(getAccidentMobile());
    return dto;
  }

  public Shop(ShopDTO shopDTO) {
    this.setId(shopDTO.getId());
    this.setBuyChannels(shopDTO.getBuyChannels());
    this.setName(shopDTO.getName());
    this.setLegalRep(shopDTO.getLegalRep());
    this.setNo(shopDTO.getNo());
    this.setAreaId(shopDTO.getAreaId());
    this.setAddress(shopDTO.getAddress());
    this.setDetailAddress(shopDTO.getDetailAddress());
    this.setZip(shopDTO.getZip());
    this.setContact(shopDTO.getContact());
    this.setLandline(shopDTO.getLandline());
    this.setMobile(shopDTO.getMobile());
    this.setFax(shopDTO.getFax());
    this.setEmail(shopDTO.getEmail());
    this.setQq(shopDTO.getQq());
    this.setBank(shopDTO.getBank());
    this.setAccount(shopDTO.getAccount());
    this.setCategoryId(shopDTO.getCategoryId());
    this.setAccount_name(shopDTO.getAccount_name());
    this.setReviewer(shopDTO.getReviewer());
    this.setReviewDate(shopDTO.getReviewDate());
    this.setShopState(shopDTO.getShopState());
    this.setShopStatus(shopDTO.getShopStatus());
    this.setMemo(shopDTO.getMemo());
    this.setOwner(shopDTO.getOwner());
    this.setOperator(shopDTO.getOperator());
    this.setSoftPrice(shopDTO.getSoftPrice());
    this.setBargainPrice(shopDTO.getBargainPrice());
    this.setOperationMode(shopDTO.getOperationMode());
    this.setBusinessHours(shopDTO.getBusinessHours());
    this.setEstablished(shopDTO.getEstablished());
    this.setQualification(shopDTO.getQualification());
    this.setPersonnel(shopDTO.getPersonnel());
    this.setArea(shopDTO.getArea());
    this.setBusinessScope(shopDTO.getBusinessScope());
    this.setRelatedBusiness(shopDTO.getRelatedBusiness());
    this.setFeature(shopDTO.getFeature());
    this.setStoreManager(shopDTO.getStoreManager());
    this.setStoreManagerMobile(shopDTO.getStoreManagerMobile());
    this.setShortname(shopDTO.getShortname());
    this.setLicencePlate(shopDTO.getLicencePlate());
    this.setPhoto(shopDTO.getPhoto());
    this.setShopVersionId(shopDTO.getShopVersionId());
    this.setWholesalerShopId(shopDTO.getWholesalerShopId());
    this.setNetworkType(shopDTO.getNetworkType());
    this.setUsedSoftware(shopDTO.getUsedSoftware());
    this.setHasComputer(shopDTO.getHasComputer());
    this.setRegistrationDate(shopDTO.getRegistrationDate());
    this.setSubmitApplicationDate(shopDTO.getSubmitApplicationDate());
    this.setProvince(shopDTO.getProvince());
    this.setCity(shopDTO.getCity());
    this.setRegion(shopDTO.getRegion());
    this.setShopKind(shopDTO.getShopKind());
    this.setTrialEndTime(shopDTO.getTrialEndTime());
    this.setTrialStartTime(shopDTO.getTrialStartTime());
    this.setUrl(shopDTO.getUrl());
    this.setRegisterType(shopDTO.getRegisterType());
    this.setShopLevel(shopDTO.getShopLevel());
    this.setUsingEndTime(shopDTO.getUsingEndTime());
    this.setPaymentStatus(shopDTO.getPaymentStatus());
    this.setBargainStatus(shopDTO.getBargainStatus());
    this.setLocateStatus(shopDTO.getLocateStatus());
    this.setDetailAddress(shopDTO.getDetailAddress());
    if (shopDTO.getSelectAllBrandModel() != null && shopDTO.getSelectAllBrandModel()) {
      this.setShopSelectBrandModel(VehicleSelectBrandModel.ALL_MODEL);
    } else {
      this.setShopSelectBrandModel(VehicleSelectBrandModel.PART_MODEL);
    }


    try {
      this.setClueInputTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, shopDTO.getClueInputDate()));
    } catch (ParseException e) {
      LOG.error("日期解析错误", e);
    }
    this.setAgent(shopDTO.getAgent());
    this.setAgentId(shopDTO.getAgentId());
    this.setAgentMobile(shopDTO.getAgentMobile());
    this.setFollowId(shopDTO.getFollowId());
    this.setFollowName(shopDTO.getFollowName());
    this.setCoordinateLat(shopDTO.getCoordinateLat());
    this.setCoordinateLon(shopDTO.getCoordinateLon());
    this.setChargeType(shopDTO.getChargeType());
    this.setAccidentMobile(shopDTO.getAccidentMobile());
    this.setAdPricePerMonth(shopDTO.getAdPricePerMonth());

    this.setAdStartDate(shopDTO.getAdStartDate());
    this.setAdEndDate(shopDTO.getAdEndDate());
    this.setAdPricePerMonth(shopDTO.getAdPricePerMonth());
    this.setProductAdType(shopDTO.getProductAdType());

//    this.setCityCode(shopDTO.getCityCode());
  }

  public Shop fromDTO(ShopDTO shopDTO) {
    this.setId(shopDTO.getId());
    this.setName(shopDTO.getName());
    if (StringUtils.isNotBlank(shopDTO.getName())) {
      this.setNameFl(PinyinUtil.converterToFirstSpell(shopDTO.getName()));
      this.setNamePy(PinyinUtil.converterToPingyin(shopDTO.getName()));
    }
    this.setBuyChannels(shopDTO.getBuyChannels());
    this.setLegalRep(shopDTO.getLegalRep());
    this.setNo(shopDTO.getNo());
    this.setAreaId(shopDTO.getAreaId());
    this.setAddress(shopDTO.getAddress());
    this.setDetailAddress(shopDTO.getDetailAddress());
    this.setZip(shopDTO.getZip());
    this.setContact(shopDTO.getContact());
    this.setLandline(shopDTO.getLandline());
    this.setMobile(shopDTO.getMobile());
    this.setFax(shopDTO.getFax());
    this.setEmail(shopDTO.getEmail());
    this.setQq(shopDTO.getQq());
    this.setBank(shopDTO.getBank());
    this.setAccount(shopDTO.getAccount());
    this.setCategoryId(shopDTO.getCategoryId());
    this.setAccount_name(shopDTO.getAccount_name());
    this.setReviewer(shopDTO.getReviewer());
    this.setReviewDate(shopDTO.getReviewDate());
    this.setShopStatus(shopDTO.getShopStatus());
    this.setShopState(shopDTO.getShopState());
    this.setMemo(shopDTO.getMemo());
    this.setOwner(shopDTO.getOwner());
    this.setOperator(shopDTO.getOperator());
    this.setSoftPrice(shopDTO.getSoftPrice());
    this.setBargainPrice(shopDTO.getBargainPrice());
    this.setOperationMode(shopDTO.getOperationMode());
    this.setBusinessHours(shopDTO.getBusinessHours());
    this.setEstablished(shopDTO.getEstablished());
    this.setQualification(shopDTO.getQualification());
    this.setPersonnel(shopDTO.getPersonnel());
    this.setArea(shopDTO.getArea());
    this.setBusinessScope(shopDTO.getBusinessScope());
    this.setRelatedBusiness(shopDTO.getRelatedBusiness());
    this.setFeature(shopDTO.getFeature());
    this.setStoreManager(shopDTO.getStoreManager());
    this.setStoreManagerMobile(shopDTO.getStoreManagerMobile());
    this.setShortname(shopDTO.getShortname());
    this.setLicencePlate(shopDTO.getLicencePlate());
    this.setPhoto(shopDTO.getPhoto());
    this.setShopVersionId(shopDTO.getShopVersionId());
    this.setWholesalerShopId(shopDTO.getWholesalerShopId());
    this.setNetworkType(shopDTO.getNetworkType());
    this.setUsedSoftware(shopDTO.getUsedSoftware());
    this.setHasComputer(shopDTO.getHasComputer());
    this.setRegistrationDate(shopDTO.getRegistrationDate());
    this.setSubmitApplicationDate(shopDTO.getSubmitApplicationDate());
    this.setProvince(shopDTO.getProvince());
    this.setCity(shopDTO.getCity());
    this.setRegion(shopDTO.getRegion());
    this.setShopKind(shopDTO.getShopKind());
    this.setTrialStartTime(shopDTO.getTrialStartTime());
    this.setTrialEndTime(shopDTO.getTrialEndTime());
    this.setUrl(shopDTO.getUrl());
    this.setRegisterType(shopDTO.getRegisterType());
    this.setShopLevel(shopDTO.getShopLevel());
    this.setUsingEndTime(shopDTO.getUsingEndTime());
    this.setPaymentStatus(shopDTO.getPaymentStatus());
    this.setBargainStatus(shopDTO.getBargainStatus());
    this.setLocateStatus(shopDTO.getLocateStatus());
    this.setAgent(shopDTO.getAgent());
    this.setAgentId(shopDTO.getAgentId());
    this.setAgentMobile(shopDTO.getAgentMobile());
    this.setFollowId(shopDTO.getFollowId());
    this.setFollowName(shopDTO.getFollowName());

    if (shopDTO.getSelectAllBrandModel() != null && shopDTO.getSelectAllBrandModel()) {
      this.setShopSelectBrandModel(VehicleSelectBrandModel.ALL_MODEL);
    } else {
      this.setShopSelectBrandModel(VehicleSelectBrandModel.PART_MODEL);
    }
    try {
      this.setClueInputTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, shopDTO.getClueInputDate()));
    } catch (ParseException e) {
      LOG.error("日期解析错误", e);
    }

    this.setCoordinateLat(shopDTO.getCoordinateLat());
    this.setCoordinateLon(shopDTO.getCoordinateLon());
    this.setChargeType(shopDTO.getChargeType());
    this.setAccidentMobile(shopDTO.getAccidentMobile());
    this.setAdStartDate(shopDTO.getAdStartDate());
    this.setAdEndDate(shopDTO.getAdEndDate());
    this.setAdPricePerMonth(shopDTO.getAdPricePerMonth());
    this.setProductAdType(shopDTO.getProductAdType());
//    this.setCityCode(shopDTO.getCityCode());
      this.setAdPricePerMonth(shopDTO.getAdPricePerMonth());
    this.setAdStartDate(shopDTO.getAdStartDate());
    this.setAdEndDate(shopDTO.getAdEndDate());
    this.setProductAdType(shopDTO.getProductAdType());
    return this;
  }

  public ShopDTO toDTO() {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setId(this.getId());
    shopDTO.setIdStr(this.getId() == null ? null : this.getId().toString());
    shopDTO.setCreationDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getCreationDate()));
    shopDTO.setName(this.getName());
    shopDTO.setLegalRep(this.getLegalRep());
    shopDTO.setNo(this.getNo());
    shopDTO.setAreaId(this.getAreaId());
    shopDTO.setAddress(this.getAddress());
    shopDTO.setDetailAddress(this.getDetailAddress());
    shopDTO.setZip(this.getZip());
    shopDTO.setContact(this.getContact());
    shopDTO.setLandline(this.getLandline());
    shopDTO.setMobile(this.getMobile());
    shopDTO.setFax(this.getFax());
    shopDTO.setEmail(this.getEmail());
    shopDTO.setQq(this.getQq());
    shopDTO.setBank(this.getBank());
    shopDTO.setAccount(this.getAccount());
    shopDTO.setCategoryId(this.getCategoryId());
    shopDTO.setAccount_name(this.getAccount_name());
    shopDTO.setReviewer(this.getReviewer());
    shopDTO.setReviewDate(this.getReviewDate());
    shopDTO.setShopState(this.getShopState());
    shopDTO.setShopStatus(this.getShopStatus());
    shopDTO.setMemo(this.getMemo());
    shopDTO.setOwner(this.getOwner());
    shopDTO.setOperator(this.getOperator());
    shopDTO.setSoftPrice(this.getSoftPrice());
    shopDTO.setBargainPrice(this.getBargainPrice());
    shopDTO.setOperationMode(this.getOperationMode());
    shopDTO.setBusinessHours(this.getBusinessHours());
    shopDTO.setEstablished(this.getEstablished());
    shopDTO.setQualification(this.getQualification());
    shopDTO.setPersonnel(this.getPersonnel());
    shopDTO.setArea(this.getArea());
    shopDTO.setBusinessScope(this.getBusinessScope());
    shopDTO.setRelatedBusiness(this.getRelatedBusiness());
    shopDTO.setFeature(this.getFeature());
    shopDTO.setStoreManager(this.getStoreManager());
    shopDTO.setStoreManagerMobile(this.getStoreManagerMobile());
    shopDTO.setShortname(this.getShortname());
    shopDTO.setLicencePlate(this.getLicencePlate());
    shopDTO.setPhoto(this.getPhoto());
    shopDTO.setShopVersionId(this.getShopVersionId());
    shopDTO.setWholesalerShopId(this.getWholesalerShopId());
    shopDTO.setNetworkType(this.getNetworkType());
    shopDTO.setUsedSoftware(this.getUsedSoftware());
    shopDTO.setHasComputer(this.getHasComputer());
    shopDTO.setRegistrationDate(this.getRegistrationDate());
    shopDTO.setRegistrationDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getRegistrationDate()));
    shopDTO.setSubmitApplicationDate(this.getSubmitApplicationDate());
    shopDTO.setProvince(this.getProvince());
    shopDTO.setBuyChannels(this.getBuyChannels());
    shopDTO.setCity(this.getCity());
    shopDTO.setRegion(this.getRegion());
    shopDTO.setShopKind(this.getShopKind());
    shopDTO.setRelativeCustomerAmount(this.getRelativeCustomerAmount());
    shopDTO.setShopLevel(this.getShopLevel());
    shopDTO.setTrialStartTime(this.getTrialStartTime());
    shopDTO.setTrialEndTime(this.getTrialEndTime());
    shopDTO.setUrl(this.getUrl());
    shopDTO.setRegisterType(this.getRegisterType());
    shopDTO.setShopPhotoId(this.getShopPhotoId());
    shopDTO.setBusinessLicenseId(this.getBusinessLicenseId());
    shopDTO.setUsingEndTime(this.getUsingEndTime());
    shopDTO.setPaymentStatus(this.getPaymentStatus());
    shopDTO.setBargainStatus(this.getBargainStatus());
    shopDTO.setClueInputDate(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getClueInputTime()));
    shopDTO.fromBusinessScopes();
    shopDTO.resetBusinessScope();

    shopDTO.setShopSelectBrandModel(this.getShopSelectBrandModel());
    shopDTO.setAgent(this.getAgent());
    shopDTO.setAgentId(this.getAgentId());
    shopDTO.setAgentMobile(this.getAgentMobile());
    shopDTO.setFollowId(this.getFollowId());
    shopDTO.setFollowName(this.getFollowName());


    shopDTO.setCoordinateLat(this.getCoordinateLat());
    shopDTO.setCoordinateLon(this.getCoordinateLon());
    shopDTO.setLocateStatus(this.getLocateStatus());
    shopDTO.setChargeType(this.getChargeType());
    shopDTO.setAccidentMobile(this.getAccidentMobile());
    shopDTO.setAdStartDate(this.getAdStartDate());
    if(this.getAdStartDate() != null){
      shopDTO.setAdStartDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getAdStartDate()));
    }
    shopDTO.setAdEndDate(this.getAdEndDate());
    if(this.getAdEndDate() != null){
      shopDTO.setAdEndDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getAdEndDate()));
    }
    shopDTO.setAdPricePerMonth(this.getAdPricePerMonth());
    shopDTO.setProductAdType(this.getProductAdType());
//    shopDTO.setCityCode(this.getCityCode());
    shopDTO.setAdPricePerMonth(this.getAdPricePerMonth());
    shopDTO.setAdStartDate(this.getAdStartDate());
    shopDTO.setAdEndDate(this.getAdEndDate());
    shopDTO.setProductAdType(this.getProductAdType());
    return shopDTO;
  }

  @Column(name = "detail_address")
  public String getDetailAddress() {
    return detailAddress;
  }

  public void setDetailAddress(String detailAddress) {
    this.detailAddress = detailAddress;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "buy_channels")
  public BuyChannels getBuyChannels() {
    return buyChannels;
  }

  public void setBuyChannels(BuyChannels buyChannels) {
    this.buyChannels = buyChannels;
  }

  @Column(name = "name_py")
  public String getNamePy() {
    return namePy;
  }

  public void setNamePy(String namePy) {
    this.namePy = namePy;
  }

  @Column(name = "name_fl")
  public String getNameFl() {
    return nameFl;
  }

  public void setNameFl(String nameFl) {
    this.nameFl = nameFl;
  }

  @Column(name = "submit_application_date")
  public Long getSubmitApplicationDate() {
    return submitApplicationDate;
  }

  public void setSubmitApplicationDate(Long submitApplicationDate) {
    this.submitApplicationDate = submitApplicationDate;
  }

  @Column(name = "name", length = 100)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "legal_rep", length = 20)
  public String getLegalRep() {
    return legalRep;
  }

  public void setLegalRep(String legalRep) {
    this.legalRep = legalRep;
  }

  @Column(name = "no", length = 20)
  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  @Column(name = "area_id")
  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  @Column(name = "address", length = 100)
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Column(name = "zip", length = 20)
  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  @Column(name = "contact", length = 20)
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name = "landline", length = 20)
  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  @Column(name = "mobile", length = 20)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
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

  @Column(name = "category_id")
  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  @Column(name = "account_name", length = 20)
  public String getAccount_name() {
    return account_name;
  }

  public void setAccount_name(String account_name) {
    this.account_name = account_name;
  }

  @Column(name = "reviewer", length = 20)
  public String getReviewer() {
    return reviewer;
  }

  public void setReviewer(String reviewer) {
    this.reviewer = reviewer;
  }

  @Column(name = "review_date")
  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "owner", length = 20)
  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  @Column(name = "operator", length = 20)
  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  @Column(name = "soft_price")
  public Double getSoftPrice() {
    return softPrice;
  }

  public void setSoftPrice(Double softPrice) {
    this.softPrice = softPrice;
  }

  @Column(name = "operation_mode", length = 20)
  public String getOperationMode() {
    return operationMode;
  }

  public void setOperationMode(String operationMode) {
    this.operationMode = operationMode;
  }

  @Column(name = "business_hours", length = 20)
  public String getBusinessHours() {
    return businessHours;
  }

  public void setBusinessHours(String businessHours) {
    this.businessHours = businessHours;
  }

  @Column(name = "established")
  public Long getEstablished() {
    return established;
  }

  public void setEstablished(Long established) {
    this.established = established;
  }

  @Column(name = "qualification", length = 20)
  public String getQualification() {
    return qualification;
  }

  public void setQualification(String qualification) {
    this.qualification = qualification;
  }

  @Column(name = "personnel", length = 20)
  public String getPersonnel() {
    return personnel;
  }

  public void setPersonnel(String personnel) {
    this.personnel = personnel;
  }

  @Column(name = "area", length = 20)
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  @Column(name = "business_scope", length = 500)
  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  @Column(name = "related_business", length = 200)
  public String getRelatedBusiness() {
    return relatedBusiness;
  }

  public void setRelatedBusiness(String relatedBusiness) {
    this.relatedBusiness = relatedBusiness;
  }

  @Column(name = "feature", length = 200)
  public String getFeature() {
    return feature;
  }

  public void setFeature(String feature) {
    this.feature = feature;
  }

  @Column(name = "store_manager", length = 20)
  public String getStoreManager() {
    return storeManager;
  }

  public void setStoreManager(String storeManager) {
    this.storeManager = storeManager;
  }

  @Column(name = "store_manager_mobile", length = 20)
  public String getStoreManagerMobile() {
    return storeManagerMobile;
  }

  public void setStoreManagerMobile(String storeManagerMobile) {
    this.storeManagerMobile = storeManagerMobile;
  }

  @Column(name = "short_name", length = 20)
  public String getShortname() {
    return shortname;
  }

  public void setShortname(String shortname) {
    this.shortname = shortname;
  }

  @Column(name = "licence_plate", length = 2)
  public String getLicencePlate() {
    return licencePlate;
  }

  public void setLicencePlate(String licencePlate) {
    this.licencePlate = licencePlate;
  }

  @Column(name = "photo", length = 500)
  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  @Column(name = "wholesaler_shop_id")
  public Long getWholesalerShopId() {
    return wholesalerShopId;
  }

  public void setWholesalerShopId(Long wholesalerShopId) {
    this.wholesalerShopId = wholesalerShopId;
  }

  @Column(name = "shop_version_id")
  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  @Column(name = "registration_date")
  public Long getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(Long registrationDate) {
    this.registrationDate = registrationDate;
  }

  @Column(name = "used_software")
  public String getUsedSoftware() {
    return usedSoftware;
  }

  public void setUsedSoftware(String usedSoftware) {
    this.usedSoftware = usedSoftware;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "network_type")
  public NetworkType getNetworkType() {
    return networkType;
  }

  public void setNetworkType(NetworkType networkType) {
    this.networkType = networkType;
  }

  @Column(name = "has_computer")
  @Enumerated(EnumType.STRING)
  public YesNo getHasComputer() {
    return hasComputer;
  }

  public void setHasComputer(YesNo hasComputer) {
    this.hasComputer = hasComputer;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_kind")
  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_status")
  public ShopStatus getShopStatus() {
    return shopStatus;
  }

  public void setShopStatus(ShopStatus shopStatus) {
    this.shopStatus = shopStatus;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_state")
  public ShopState getShopState() {
    return shopState;
  }

  public void setShopState(ShopState shopState) {
    this.shopState = shopState;
  }

  @Column(name = "relative_customer_amount")
  public Integer getRelativeCustomerAmount() {
    return relativeCustomerAmount;
  }

  public void setRelativeCustomerAmount(Integer relativeCustomerAmount) {
    this.relativeCustomerAmount = relativeCustomerAmount;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_level")
  public ShopLevel getShopLevel() {
    return shopLevel;
  }

  public void setShopLevel(ShopLevel shopLevel) {
    this.shopLevel = shopLevel;
  }

  @Column(name = "trial_end_time")
  public Long getTrialEndTime() {
    return trialEndTime;
  }

  public void setTrialEndTime(Long trialEndTime) {
    this.trialEndTime = trialEndTime;
  }


  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "shop_photo_id")
  public Long getShopPhotoId() {
    return shopPhotoId;
  }

  public void setShopPhotoId(Long shopPhotoId) {
    this.shopPhotoId = shopPhotoId;
  }

  @Column(name = "business_license_id")
  public Long getBusinessLicenseId() {
    return businessLicenseId;
  }

  public void setBusinessLicenseId(Long businessLicenseId) {
    this.businessLicenseId = businessLicenseId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "register_type")
  public RegisterType getRegisterType() {
    return registerType;
  }

  public void setRegisterType(RegisterType registerType) {
    this.registerType = registerType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "locate_status")
  public LocateStatus getLocateStatus() {
    return locateStatus;
  }

  public void setLocateStatus(LocateStatus locateStatus) {
    this.locateStatus = locateStatus;
  }

  @Column(name = "trial_start_time")
  public Long getTrialStartTime() {
    return trialStartTime;
  }

  public void setTrialStartTime(Long trialStartTime) {
    this.trialStartTime = trialStartTime;
  }

  @Column(name = "using_end_time")
  public Long getUsingEndTime() {
    return usingEndTime;
  }

  public void setUsingEndTime(Long usingEndTime) {
    this.usingEndTime = usingEndTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status")
  public PaymentStatus getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(PaymentStatus paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "bargain_status")
  public BargainStatus getBargainStatus() {
    return bargainStatus;
  }

  public void setBargainStatus(BargainStatus bargainStatus) {
    this.bargainStatus = bargainStatus;
  }

  @Column(name = "bargain_price")
  public Double getBargainPrice() {
    return bargainPrice;
  }

  public void setBargainPrice(Double bargainPrice) {
    this.bargainPrice = bargainPrice;
  }

  @Column(name = "clue_input_time")
  public Long getClueInputTime() {
    return clueInputTime;
  }

  public void setClueInputTime(Long clueInputTime) {
    this.clueInputTime = clueInputTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_recommended_type")
  public ShopRecommendedType getShopRecommendedType() {
    return shopRecommendedType;
  }

  public void setShopRecommendedType(ShopRecommendedType shopRecommendedType) {
    this.shopRecommendedType = shopRecommendedType;
  }

  @Column(name = "shop_recommended_grade")
  public Integer getShopRecommendedGrade() {
    return shopRecommendedGrade;
  }

  public void setShopRecommendedGrade(Integer shopRecommendedGrade) {
    this.shopRecommendedGrade = shopRecommendedGrade;
  }

  @Column(name = "follow_id")
  public Long getFollowId() {
    return followId;
  }

  public void setFollowId(Long followId) {
    this.followId = followId;
  }

  @Column(name = "follow_name")
  public String getFollowName() {
    return followName;
  }

  public void setFollowName(String followName) {
    this.followName = followName;
  }

  @Column(name = "agent")
  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  @Column(name = "agent_id")
  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  @Column(name = "agent_mobile")
  public String getAgentMobile() {
    return agentMobile;
  }

  public void setAgentMobile(String agentMobile) {
    this.agentMobile = agentMobile;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_select_brand_model")
  public VehicleSelectBrandModel getShopSelectBrandModel() {
    return shopSelectBrandModel;
  }

  public void setShopSelectBrandModel(VehicleSelectBrandModel shopSelectBrandModel) {
    this.shopSelectBrandModel = shopSelectBrandModel;
  }

  @Column(name = "coordinate_lat")
  public String getCoordinateLat() {
    return coordinateLat;
  }

  public void setCoordinateLat(String coordinateLat) {
    this.coordinateLat = coordinateLat;
  }

  @Column(name = "coordinate_lon")
  public String getCoordinateLon() {
    return coordinateLon;
  }

  public void setCoordinateLon(String coordinateLon) {
    this.coordinateLon = coordinateLon;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "charge_type")
  public ChargeType getChargeType() {
    return chargeType;
  }
  public void setAreaId(ShopDTO dto) {
    if (dto != null) {
      if (dto.getRegion() != null)
        setAreaId(dto.getRegion());
      else if (dto.getCity() != null)
        setAreaId(dto.getCity());
      else if (dto.getProvince() != null)
        setAreaId(dto.getProvince());
    }
  }

  public void setChargeType(ChargeType chargeType) {
    this.chargeType = chargeType;
  }
  //  @Column(name = "city_code")
//  public Integer getCityCode() {
//    return cityCode;
//  }
//
//  public void setCityCode(Integer cityCode) {
//    this.cityCode = cityCode;
//  }


  @Column(name = "accident_mobile")
  public String getAccidentMobile() {
    return accidentMobile;
  }

  public void setAccidentMobile(String accidentMobile) {
    this.accidentMobile = accidentMobile;
  }

  @Column(name = "ad_price_per_month")
  public Double getAdPricePerMonth() {
    return adPricePerMonth;
  }

  public void setAdPricePerMonth(Double adPricePerMonth) {
    this.adPricePerMonth = adPricePerMonth;
  }

  @Column(name = "ad_start_date")
  public Long getAdStartDate() {
    return adStartDate;
  }

  public void setAdStartDate(Long adStartDate) {
    this.adStartDate = adStartDate;
  }

  @Column(name = "ad_end_date")
  public Long getAdEndDate() {
    return adEndDate;
  }

  public void setAdEndDate(Long adEndDate) {
    this.adEndDate = adEndDate;
  }

  @Column(name = "product_ad_type")
  @Enumerated(EnumType.STRING)
  public ProductAdType getProductAdType() {
    return productAdType;
  }

  public void setProductAdType(ProductAdType productAdType) {
    this.productAdType = productAdType;
  }
}