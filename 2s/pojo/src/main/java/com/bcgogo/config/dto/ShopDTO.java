package com.bcgogo.config.dto;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.shop.*;
import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.txn.finance.BuyChannels;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.search.dto.ShopSolrDTO;
import com.bcgogo.user.dto.AccidentSpecialistDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;

public class ShopDTO implements Serializable {
  private Long id;
  private String idStr;
  private String name;
  private String legalRep;
  private String no;
  private Long areaId;
  private String areaName;
  private Long province;     //省
  private Long city;          //市
  private Long region;        //区域
  @Deprecated  //by ZhangJuntao use detailAddress install
  private String address;
  private String detailAddress;
  private String zip;
  private String contact;
  private String contactMobile;
  private ContactDTO[] contacts; // add by zhuj 联系人列表
    private AccidentSpecialistDTO[] specialistDTOs;
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
  private Long shopVersionId;
  private String shopVersionName;
  private String owner;
  private String operator;
  private Double softPrice;
  private Double bargainPrice;
  private String businessHours;
  private Long established;
  private String establishedStr;
  private String qualification;
  private String personnel;
  private String area; //面积

  private CommentStatDTO commentStatDTO;

  private String operationMode;     //经营方式
  private String operationModeBrand;//专卖店时，出现品牌的输入框
  private String[] operationModes;
  private String otherOperationMode;
  private String operationModeStr;//经营方式

  private String businessScope; //经营范围
  private String businessScopeStr;
  private String otherBusinessScope;
  private String majorProduct;
  private String[] businessScopes;

  private String relatedBusiness;
  private String otherRelatedBusiness;
  private String[] relatedBusinesses;

  private String feature;
  private String[] features;
  private String otherVehicleBrand;  //特色-车型
  private String otherFeature;  //特色-其他

  @Deprecated
  private byte[] attachment;
  private String storeManager;
  private String storeManagerMobile;
  private String shortname;
  private String licencePlate;
  private String photo;
  private String creationDateStr;
  private Long wholesalerShopId;
  private Long registrationDate;//注册时间
  private Long submitApplicationDate;//提交审核时间
  private Long editDate; //录入时间
  private String clueInputDate;

  private String usedSoftware;   //使用其他软件
  private NetworkType networkType;
  private LocateStatus locateStatus; //坐标状态
  private YesNo hasComputer;//是否有电脑
//  private Long shopRelationInviteOriginShopId;
//  private String shopRelationInviteOriginShopName;
  private Long managerId;   //店面管理员id
  private String managerUserNo;       // 管理员账号
  private ShopOperateScene scene;//操作shop场景
  private Integer relativeCustomerAmount;//关联客户数量
  private ShopLevel shopLevel;
  private Long trialStartTime;  //试用开始时间
  private Long trialEndTime;  //试用结束时间
  private Long usingEndTime;  //使用结束时间
  private PaymentStatus paymentStatus;
  private ShopKind shopKind;
  private ShopStatus shopStatus;
  private String shopStatusValue;
  private ShopState shopState;
  private String url;//店铺网址
  private String businessLicenseName;  //营业执照文件名
  private byte[] businessLicense;
  private Long businessLicenseId;
  private byte[] shopPhoto;
  private Long shopPhotoId;
  private RegisterType registerType;
  private BuyChannels buyChannels;
  private String invitationCode;//邀请码
  private InvitationCodeDTO invitationCodeDTO;
  private String registrationDateStr;
  private BargainStatus bargainStatus;
  private List<String> contactMobiles;
  private Double customScore;

  //注册时填写的商品
  private ProductDTO[] productDTOs;
  private String thirdCategoryIdStr;//注册时填写的经营范围id字符串
  private Set<Long> productCategoryIds;
  private Set<Long> serviceCategoryIds; //二级服务范围
  private Set<Long> agentProductIds;
  private Long followId;//跟进人
  private String followName;//跟进人
  private String agent;   //销售人
  private String agentId;
  private Long agentDBId;
  private String agentMobile;
  private ShopVehicleBrandModelDTO[] shopVehicleBrandModelDTOs;
  private String shopVehicleBrandModelStr;
  private Boolean selectAllBrandModel;//注册时选择车型时 是否是全部车型
  private VehicleSelectBrandModel shopSelectBrandModel; //注册时选择车型时 是否是全部车型
  private ImageCenterDTO imageCenterDTO;
  private Set<Long> vehicleModelIds;     //选择部分车型时后台admin传过来的modelIds

  private boolean licensed =false;
  /**
   * 收藏该店铺的供应商数量
   */
  private int beStored;  // add by zhuj 通过在supplier中supplierShopId统计获得
  private int totalProductCount; // 持有的产品总数

  private String qqArray;
  private ChargeType chargeType;    //收费类型，一次性或者按年收费
  private String accidentMobile;//事故专员手机


  private Double adPricePerMonth;//每个月广告费
  private Long adStartDate;//广告开始投放时间
  private String adStartDateStr;//广告开始投放时间
  private Long adEndDate;//广告投放结束时间
  private String adEndDateStr;//广告投放结束时间
  private ProductAdType productAdType;//广告类型
  private Set<Long> shopAdAreaIds;     //选择区域后传过来的Id
  private Set<Long> recommendIds;     //选择分类后传过来的Id


  public String getDetailAddress() {
    return detailAddress;
  }

  public void setDetailAddress(String detailAddress) {
    this.detailAddress = detailAddress;
  }

  public BuyChannels getBuyChannels() {
    return buyChannels;
  }

  public void setBuyChannels(BuyChannels buyChannels) {
    this.buyChannels = buyChannels;
  }

  public ChargeType getChargeType() {
    return chargeType;
  }

  public void setChargeType(ChargeType chargeType) {
    this.chargeType = chargeType;
  }

  public boolean isLicensed() {
    return licensed;
  }

  public void setLicensed(boolean licensed) {
    this.licensed = licensed;
  }

  public int getTotalProductCount() {
    return totalProductCount;
  }

  public void setTotalProductCount(int totalProductCount) {
    this.totalProductCount = totalProductCount;
  }

  public int getBeStored() {
    return beStored;
  }

  public void setBeStored(int beStored) {
    this.beStored = beStored;
  }


  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  private String coordinateLat;//店铺纬度信息
  private String coordinateLon;//店铺经度信息
  private Integer cityCode;//店铺百度地图编码
  private List<Node> serviceCategory;    //所有的二级预约服务分类
  private String serviceCategoryIdStr;   //本店面的服务IDs
  private String serviceCategoryStr;     //服务所拼接的字符窜
  public ShopSolrDTO toShopSolrDTO(){
    ShopSolrDTO dto = new ShopSolrDTO();
    dto.setId(this.getId());
    dto.setAddress(this.getAddress());
    dto.setCityNo(this.getCity());
    dto.setProvinceNo(this.getProvince());
    dto.setRegionNo(this.getRegion());
    dto.setName(this.getName());
    dto.setShopKind(getShopKind());
    if(StringUtil.isNotEmpty(getCoordinateLat()))dto.setLocationLat(Double.valueOf(getCoordinateLat()));
    if(StringUtil.isNotEmpty(getCoordinateLon()))dto.setLocationLon(Double.valueOf(getCoordinateLon()));
    return dto;
  }

  public String shopStateValidate() {
    if (getShopState() == ShopState.DELETED) {
      return "店铺不存在";
    }
    if (getShopState() == ShopState.OVERDUE) {
      return "店铺已过期";
    }
    if (getShopState() == ShopState.IN_ACTIVE) {
      return "店铺已禁用";
    }
    if (getShopState() == ShopState.ARREARS) {
      return "店铺已欠费";
    }
    return "";
  }

  //经营范围
  @Deprecated
  public void toBusinessScopes() {
    StringBuilder bus = new StringBuilder();
    if (!ArrayUtils.isEmpty(this.getBusinessScopes())) {
      for (String s : this.getBusinessScopes()) {
        bus.append(s).append(",");
      }
    }
    if (StringUtils.isNotBlank(this.getOtherBusinessScope())) {
      bus.append("<other:").append(this.getOtherBusinessScope()).append(">,");
    }
    if (StringUtils.isNotBlank(this.getMajorProduct())) {
      bus.append("<majorProduct:").append(this.getMajorProduct()).append(">,");
    }
    this.setBusinessScope(StringUtil.subString(bus.toString()));
  }

  public String fromBusinessScopes() {
    StringBuilder builder = new StringBuilder();
    List<String> bus = new ArrayList<String>();
    String[] busArray;
    if (StringUtils.isNotBlank(getBusinessScope())) {
      busArray = getBusinessScope().split(",");
      for (String str : busArray) {
        if (str.contains("<")) {
          String[] s = str.split(":");
          String special = s[1].replace(">", "");
          if (s[0].replace("<", "").equals("other")) {
            builder.append("其他：").append(special).append(",");
            this.setOtherBusinessScope(special);
          } else {
            builder.append("主要产品：").append(special).append(",");
            this.setMajorProduct(special);
          }
        } else {
          builder.append(str).append(",");
          bus.add(str);
        }
      }
      this.setBusinessScopes(bus.toArray(new String[bus.size()]));
    }
    return StringUtil.subString(builder.toString());
  }

  //店面特色
  public void toFeatures() {
    StringBuilder bus = new StringBuilder();
    if (!ArrayUtils.isEmpty(this.getFeatures())) {
      for (String s : this.getFeatures()) {
        bus.append(s).append(",");
      }
    }
    if (StringUtils.isNotBlank(this.getOtherFeature())) {
      bus.append("<other:").append(this.getOtherFeature()).append(">,");
    }
    if (StringUtils.isNotBlank(this.getOtherVehicleBrand())) {
      bus.append("<vehicleBrand:").append(this.getOtherVehicleBrand()).append(">,");
    }
    this.setFeature(StringUtil.subString(bus.toString()));
  }

  public String fromFeatures() {
    List<String> bus = new ArrayList<String>();
    StringBuilder builder = new StringBuilder();
    String[] busArray;
    if (StringUtils.isNotBlank(this.getFeature())) {
      busArray = this.getFeature().split(",");
      for (String str : busArray) {
        if (str.contains("<")) {
          String[] s = str.split(":");
          String special = s[1].replace(">", "");
          if (s[0].replace("<", "").equals("other")) {
            builder.append("其他：").append(str).append(",");
            this.setOtherFeature(special);
          } else {
            builder.append("品牌：").append(str).append(",");
            this.setOtherVehicleBrand(special);
          }
        } else {
          builder.append(str).append(",");
          bus.add(str);
        }
      }
      this.setFeatures(bus.toArray(new String[bus.size()]));
    }
    return StringUtil.subString(builder.toString());
  }

  //相关业务
  public void toRelatedBusinesses() {
    StringBuilder bus = new StringBuilder();
    if (!ArrayUtils.isEmpty(this.getRelatedBusinesses())) {
      for (String s : this.getRelatedBusinesses()) {
        bus.append(s).append(",");
      }
    }
    if (StringUtils.isNotBlank(this.getOtherRelatedBusiness())) {
      bus.append("<other:").append(this.getOtherRelatedBusiness()).append(">,");
    }
    this.setRelatedBusiness(StringUtil.subString(bus.toString()));
  }

  public String fromRelatedBusinesses() {
    List<String> bus = new ArrayList<String>();
    StringBuilder builder = new StringBuilder();
    String[] busArray;
    if (StringUtils.isNotBlank(this.getRelatedBusiness())) {
      busArray = this.getRelatedBusiness().split(",");
      for (String str : busArray) {
        if (str.contains("<")) {
          String[] s = str.split(":");
          String special = s[1].replace(">", "");
          if (s[0].replace("<", "").equals("other")) {
            builder.append("其他：").append(str).append(",");
            this.setOtherRelatedBusiness(special);
          }
        } else {
          builder.append(str).append(",");
          bus.add(str);
        }
      }
      this.setRelatedBusinesses(bus.toArray(new String[bus.size()]));
    }
    return StringUtil.subString(builder.toString());
  }

  //经营方式
  public void toOperationModes() {
    StringBuilder bus = new StringBuilder();
    if (!ArrayUtils.isEmpty(this.getOperationModes())) {
      for (String s : this.getOperationModes()) {
        bus.append(s).append(",");
      }
    }
    if (StringUtils.isNotBlank(this.getOtherOperationMode())) {
      bus.append("<other:").append(this.getOtherOperationMode()).append(">,");
    }
    this.setOperationMode(StringUtil.subString(bus.toString()));
  }

  public String fromOperationModes() {
    List<String> bus = new ArrayList<String>();
    StringBuilder builder = new StringBuilder();
    String[] busArray;
    if (StringUtils.isNotBlank(this.getOperationMode())) {
      busArray = this.getOperationMode().split(",");
      for (String str : busArray) {
        if (str.contains("<")) {
          String[] s = str.split(":");
          String special = s[1].replace(">", "");
          if (s[0].replace("<", "").equals("other")) {
            this.setOtherOperationMode(special);
            builder.append("其他：").append(str).append(",");
          }
        } else {
          builder.append(str).append(",");
          bus.add(str);
        }
      }
      this.setOperationModes(bus.toArray(new String[bus.size()]));
    }
    return StringUtil.subString(builder.toString());
  }

  public ApplyShopSearchCondition toApplyShopDTO() {
    ApplyShopSearchCondition shopDTO = new ApplyShopSearchCondition();
    shopDTO.setShopId(this.getId());
    shopDTO.setName(this.getName());
    this.resetBusinessScope();
    shopDTO.setBusinessScope(this.fromBusinessScopes());
    shopDTO.setAddress(this.getAreaName());
    shopDTO.setShopSelectBrandModel(this.getShopSelectBrandModel());
    return shopDTO;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }

  public Long getTrialStartTime() {
    return trialStartTime;
  }

  public void setTrialStartTime(Long trialStartTime) {
    this.trialStartTime = trialStartTime;
  }

  public Long getTrialEndTime() {
    return trialEndTime;
  }

  public void setTrialEndTime(Long trialEndTime) {
    this.trialEndTime = trialEndTime;
  }

  public Long getManagerId() {
    return managerId;
  }

  public void setManagerId(Long managerId) {
    this.managerId = managerId;
  }

  public String getManagerUserNo() {
    return managerUserNo;
  }

  public void setManagerUserNo(String managerUserNo) {
    this.managerUserNo = managerUserNo;
  }

  public ShopOperateScene getScene() {
    return scene;
  }

  public void setScene(ShopOperateScene scene) {
    this.scene = scene;
  }

  public String[] getOperationModes() {
    return operationModes;
  }

  public void setOperationModes(String[] operationModes) {
    this.operationModes = operationModes;
  }

  public String getOtherOperationMode() {
    return otherOperationMode;
  }

  public void setOtherOperationMode(String otherOperationMode) {
    this.otherOperationMode = otherOperationMode;
  }

  public String getOtherRelatedBusiness() {
    return otherRelatedBusiness;
  }

  public void setOtherRelatedBusiness(String otherRelatedBusiness) {
    this.otherRelatedBusiness = otherRelatedBusiness;
  }

  public String[] getRelatedBusinesses() {
    return relatedBusinesses;
  }

  public void setRelatedBusinesses(String[] relatedBusinesses) {
    this.relatedBusinesses = relatedBusinesses;
  }

  public String[] getFeatures() {
    return features;
  }

  public void setFeatures(String[] features) {
    this.features = features;
  }

  public String getOtherVehicleBrand() {
    return otherVehicleBrand;
  }

  public void setOtherVehicleBrand(String otherVehicleBrand) {
    this.otherVehicleBrand = otherVehicleBrand;
  }

  public String getOtherFeature() {
    return otherFeature;
  }

  public void setOtherFeature(String otherFeature) {
    this.otherFeature = otherFeature;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

  public String getOtherBusinessScope() {
    return otherBusinessScope;
  }

  public void setOtherBusinessScope(String otherBusinessScope) {
    this.otherBusinessScope = otherBusinessScope;
  }

  public String getMajorProduct() {
    return majorProduct;
  }

  public void setMajorProduct(String majorProduct) {
    this.majorProduct = majorProduct;
  }

  public String[] getBusinessScopes() {
    return businessScopes;
  }

  public void setBusinessScopes(String[] businessScopes) {
    this.businessScopes = businessScopes;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public String getShortname() {
    return shortname;
  }

  public void setShortname(String shortname) {
    this.shortname = shortname;
  }

  public String getLicencePlate() {
    return licencePlate;
  }

  public void setLicencePlate(String licencePlate) {
    this.licencePlate = licencePlate;
  }

  public ShopDTO() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLegalRep() {
    return legalRep;
  }

  public void setLegalRep(String legalRep) {
    this.legalRep = legalRep;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
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

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getAccount_name() {
    return account_name;
  }

  public void setAccount_name(String account_name) {
    this.account_name = account_name;
  }

  public String getReviewer() {
    return reviewer;
  }

  public void setReviewer(String reviewer) {
    this.reviewer = reviewer;
  }

  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public Double getSoftPrice() {
    return softPrice;
  }

  public void setSoftPrice(Double softPrice) {
    this.softPrice = softPrice;
  }

  public String getOperationMode() {
    return operationMode;
  }

  public void setOperationMode(String operationMode) {
    this.operationMode = operationMode;
  }

  public CommentStatDTO getCommentStatDTO() {
    return commentStatDTO;
  }

  public void setCommentStatDTO(CommentStatDTO commentStatDTO) {
    this.commentStatDTO = commentStatDTO;
  }

  public String getBusinessHours() {
    return businessHours;
  }

  public void setBusinessHours(String businessHours) {
    this.businessHours = businessHours;
  }

  public Long getEstablished() {
    return established;
  }

  public void setEstablished(Long established) {
    this.established = established;
    if (this.established != null && established != 0) {
      establishedStr = DateUtil.dateLongToStr(this.established);
    } else {
      establishedStr = "";
    }


  }

  public String getQualification() {
    return qualification;
  }

  public void setQualification(String qualification) {
    this.qualification = qualification;
  }

  public String getPersonnel() {
    return personnel;
  }

  public void setPersonnel(String personnel) {
    this.personnel = personnel;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  public String getRelatedBusiness() {
    return relatedBusiness;
  }

  public void setRelatedBusiness(String relatedBusiness) {
    this.relatedBusiness = relatedBusiness;
  }

  public String getFeature() {
    return feature;
  }

  public void setFeature(String feature) {
    this.feature = feature;
  }

  public byte[] getAttachment() {
    return attachment;
  }

  public void setAttachment(byte[] attachment) {
    this.attachment = attachment;
  }

  public String getStoreManager() {
    return storeManager;
  }

  public void setStoreManager(String storeManager) {
    this.storeManager = storeManager;
  }

  public String getStoreManagerMobile() {
    return storeManagerMobile;
  }

  public void setStoreManagerMobile(String storeManagerMobile) {
    this.storeManagerMobile = storeManagerMobile;
  }

  public String getEstablishedStr() {
    return establishedStr;
  }

  public void setEstablishedStr(String establishedStr) {
    this.establishedStr = establishedStr;
  }

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  public String getCreationDateStr() {
    return creationDateStr;
  }

  public void setCreationDateStr(String creationDateStr) {
    this.creationDateStr = creationDateStr;
  }

  public Long getWholesalerShopId() {
    return wholesalerShopId;
  }

  public void setWholesalerShopId(Long wholesalerShopId) {
    this.wholesalerShopId = wholesalerShopId;
  }

  public Long getSubmitApplicationDate() {
    return submitApplicationDate;
  }

  public void setSubmitApplicationDate(Long submitApplicationDate) {
    this.submitApplicationDate = submitApplicationDate;
  }

  public String getShopVersionName() {
    return shopVersionName;
  }

  public void setShopVersionName(String shopVersionName) {
    this.shopVersionName = shopVersionName;
  }

  public Long getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(Long registrationDate) {
    this.registrationDate = registrationDate;
  }

  public String getUsedSoftware() {
    return usedSoftware;
  }

  public void setUsedSoftware(String usedSoftware) {
    this.usedSoftware = usedSoftware;
  }

  public NetworkType getNetworkType() {
    return networkType;
  }

  public void setNetworkType(NetworkType networkType) {
    this.networkType = networkType;
  }

  public YesNo getHasComputer() {
    return hasComputer;
  }

  public void setHasComputer(YesNo hasComputer) {
    this.hasComputer = hasComputer;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public Integer getRelativeCustomerAmount() {
    return relativeCustomerAmount;
  }

  public void setRelativeCustomerAmount(Integer relativeCustomerAmount) {
    this.relativeCustomerAmount = relativeCustomerAmount;
  }

  public ShopLevel getShopLevel() {
    return shopLevel;
  }

  public void setShopLevel(ShopLevel shopLevel) {
    this.shopLevel = shopLevel;
  }

  public ShopStatus getShopStatus() {
    return shopStatus;
  }

  public void setShopStatus(ShopStatus shopStatus) {
    if (shopStatus != null) this.setShopStatusValue(shopStatus.getValue());
    this.shopStatus = shopStatus;
  }

  public String getShopStatusValue() {
    return shopStatusValue;
  }

  public void setShopStatusValue(String shopStatusValue) {
    this.shopStatusValue = shopStatusValue;
  }

  public ShopState getShopState() {
    return shopState;
  }

  public void setShopState(ShopState shopState) {
    this.shopState = shopState;
  }

  public String getUrl() {
    return url;
  }


  public void setUrl(String url) {
    this.url = url;
  }

  public byte[] getBusinessLicense() {
    return businessLicense;
  }

  public void setBusinessLicense(byte[] businessLicense) {
    this.businessLicense = businessLicense;
  }

  public Long getBusinessLicenseId() {
    return businessLicenseId;
  }

  public void setBusinessLicenseId(Long businessLicenseId) {
    this.businessLicenseId = businessLicenseId;
  }

  public byte[] getShopPhoto() {
    return shopPhoto;
  }

  public void setShopPhoto(byte[] shopPhoto) {
    this.shopPhoto = shopPhoto;
  }

  public Long getShopPhotoId() {
    return shopPhotoId;
  }

  public void setShopPhotoId(Long shopPhotoId) {
    this.shopPhotoId = shopPhotoId;
  }

  public RegisterType getRegisterType() {
    return registerType;
  }

  public void setRegisterType(RegisterType registerType) {
    this.registerType = registerType;
  }

  public String getOperationModeBrand() {
    return operationModeBrand;
  }

  public void setOperationModeBrand(String operationModeBrand) {
    this.operationModeBrand = operationModeBrand;
  }

  public String getInvitationCode() {
    return invitationCode;
  }

  public void setInvitationCode(String invitationCode) {
    this.invitationCode = invitationCode;
  }

  public String getBusinessLicenseName() {
    return businessLicenseName;
  }

  public void setBusinessLicenseName(String businessLicenseName) {
    this.businessLicenseName = businessLicenseName;
  }

  public InvitationCodeDTO getInvitationCodeDTO() {
    return invitationCodeDTO;
  }

  public void setInvitationCodeDTO(InvitationCodeDTO invitationCodeDTO) {
    this.invitationCodeDTO = invitationCodeDTO;
  }

  public String getRegistrationDateStr() {
    return registrationDateStr;
  }

  public void setRegistrationDateStr(String registrationDateStr) {
    this.registrationDateStr = registrationDateStr;
  }

  public String getOperationModeStr() {
    resetOperationMode();
    return operationModeStr;
  }

  public void setOperationModeStr(String operationModeStr) {
    this.operationModeStr = operationModeStr;
  }

  public String getBusinessScopeStr() {
    return businessScopeStr;
  }

  public void setBusinessScopeStr(String businessScopeStr) {
    this.businessScopeStr = businessScopeStr;
  }

  public Long getUsingEndTime() {
    return usingEndTime;
  }

  public void setUsingEndTime(Long usingEndTime) {
    this.usingEndTime = usingEndTime;
  }

  public PaymentStatus getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(PaymentStatus paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public BargainStatus getBargainStatus() {
    return bargainStatus;
  }

  public void setBargainStatus(BargainStatus bargainStatus) {
    this.bargainStatus = bargainStatus;
  }

  public Double getBargainPrice() {
    return bargainPrice;
  }

  public void setBargainPrice(Double bargainPrice) {
    this.bargainPrice = bargainPrice;
  }

  public String getClueInputDate() {
    return clueInputDate;
  }

  public void setClueInputDate(String clueInputDate) {
    this.clueInputDate = clueInputDate;
  }

  public ContactDTO[] getContacts() {
    return contacts;
  }

  public void setContacts(ContactDTO[] contacts) {
    this.contacts = contacts;
    StringBuffer qqArray = new StringBuffer();
    if (ArrayUtil.isNotEmpty(contacts)) {
      this.setContactMobiles(new ArrayList<String>());
      for (ContactDTO contactDTO : contacts) {
        if (contactDTO != null&&StringUtil.isNotEmpty(contactDTO.getMobile())) {
          this.getContactMobiles().add(contactDTO.getMobile());
        }
        if (contactDTO != null && StringUtils.isNotBlank(contactDTO.getQq())) {
          qqArray.append(contactDTO.getQq().trim()).append(",");
        }
      }
    }

    if(qqArray.length()>0){
      setQqArray(qqArray.toString());
    }
  }

  /**
   * 保存商品前准备数据
   */
  public void prepareForSaveProduct() {
    if (ArrayUtil.isEmpty(productDTOs)) {
      return;
    }
    for (ProductDTO productDTO : productDTOs) {
      if (productDTO != null) {
        productDTO.setStorageUnit(productDTO.getStorageUnit() == null ? null : productDTO.getStorageUnit().trim());
        productDTO.setSellUnit(productDTO.getSellUnit() == null ? null : productDTO.getSellUnit().trim());
        if (StringUtils.isNotBlank(productDTO.getStorageUnit()) && StringUtils.isEmpty(productDTO.getSellUnit())) {
          productDTO.setSellUnit(productDTO.getStorageUnit());
        } else if (StringUtils.isNotBlank(productDTO.getSellUnit()) && StringUtils.isEmpty(productDTO.getStorageUnit())) {
          productDTO.setStorageUnit(productDTO.getSellUnit());
        }
      }
    }
  }

  public AccidentSpecialistDTO[] getSpecialistDTOs() {
    return specialistDTOs;
  }

  public void setSpecialistDTOs(AccidentSpecialistDTO[] specialistDTOs) {
    this.specialistDTOs = specialistDTOs;
  }

  public ProductDTO[] getProductDTOs() {
    return productDTOs;
  }

  public void setProductDTOs(ProductDTO[] productDTOs) {
    this.productDTOs = productDTOs;
  }

  public String getThirdCategoryIdStr() {
    return thirdCategoryIdStr;
  }

  public void setThirdCategoryIdStr(String thirdCategoryIdStr) {
    this.thirdCategoryIdStr = thirdCategoryIdStr;
  }

  public Set<Long> getServiceCategoryIds() {
    return serviceCategoryIds;
  }

  public void setServiceCategoryIds(Set<Long> serviceCategoryIds) {
    this.serviceCategoryIds = serviceCategoryIds;
  }

  public Set<Long> getAgentProductIds() {
    return agentProductIds;
  }

  public void setAgentProductIds(Set<Long> agentProductIds) {
    this.agentProductIds = agentProductIds;
  }

  //  public Long getShopRelationInviteOriginShopId() {
//    return shopRelationInviteOriginShopId;
//  }
//
//  public void setShopRelationInviteOriginShopId(Long shopRelationInviteOriginShopId) {
//    this.shopRelationInviteOriginShopId = shopRelationInviteOriginShopId;
//  }
//
//  public String getShopRelationInviteOriginShopName() {
//    return shopRelationInviteOriginShopName;
//  }
//
//  public void setShopRelationInviteOriginShopName(String shopRelationInviteOriginShopName) {
//    this.shopRelationInviteOriginShopName = shopRelationInviteOriginShopName;
//  }


  public ShopVehicleBrandModelDTO[] getShopVehicleBrandModelDTOs() {
    return shopVehicleBrandModelDTOs;
  }

  public void setShopVehicleBrandModelDTOs(ShopVehicleBrandModelDTO[] shopVehicleBrandModelDTOs) {
    this.shopVehicleBrandModelDTOs = shopVehicleBrandModelDTOs;
  }

  public Set<Long> getVehicleModelIds() {
    return vehicleModelIds;
  }

  public void setVehicleModelIds(Set<Long> vehicleModelIds) {
    this.vehicleModelIds = vehicleModelIds;
  }

  public String getShopVehicleBrandModelStr() {
    return shopVehicleBrandModelStr;
  }

  public void setShopVehicleBrandModelStr(String shopVehicleBrandModelStr) {
    this.shopVehicleBrandModelStr = shopVehicleBrandModelStr;
  }

  public Boolean getSelectAllBrandModel() {
    return selectAllBrandModel;
  }

  public void setSelectAllBrandModel(Boolean selectAllBrandModel) {
    this.selectAllBrandModel = selectAllBrandModel;
  }

  public VehicleSelectBrandModel getShopSelectBrandModel() {
    return shopSelectBrandModel;
  }

  public void setShopSelectBrandModel(VehicleSelectBrandModel shopSelectBrandModel) {
    this.shopSelectBrandModel = shopSelectBrandModel;
  }

  public void setAreaNameByAreaNo(Map<Long, AreaDTO> areaMap) {
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
    this.setAreaName(sb.toString());
  }


  public void resetBusinessScope() {
    StringBuffer stringBuffer = new StringBuffer();
    if (!ArrayUtils.isEmpty(getBusinessScopes())) {
      for (String str : getBusinessScopes()) {
        stringBuffer.append(str).append(",");
      }
    }
    if (StringUtil.isNotEmpty(getOtherBusinessScope())) {
      stringBuffer.append("其他:").append(getOtherBusinessScope()).append(",");
    }
    if (StringUtil.isNotEmpty(getMajorProduct())) {
      stringBuffer.append("主要产品:").append(getMajorProduct()).append(",");
    }
    if (stringBuffer.length() > 1) {
      setBusinessScopeStr(StringUtil.getShortStringByNum(stringBuffer.toString(), 0, stringBuffer.length() - 1));
    }

  }

  public void resetOperationMode() {
    StringBuffer stringBuffer = new StringBuffer();
    if (!ArrayUtils.isEmpty(getOperationModes())) {
      for (String str : getOperationModes()) {
        stringBuffer.append(str).append(",");
      }
    }
    if (StringUtil.isNotEmpty(getOtherOperationMode())) {
      stringBuffer.append(getOtherOperationMode()).append(",");
    }
    if (stringBuffer.length() > 1) {
      setOperationModeStr(StringUtil.getShortStringByNum(stringBuffer.toString(), 0, stringBuffer.length() - 1));
    }

  }

  public void generateShopVehicleBrandModelStr(List<ShopVehicleBrandModelDTO> bmDTOs){
    if(getShopSelectBrandModel() == VehicleSelectBrandModel.ALL_MODEL){
      setShopVehicleBrandModelStr("全部车型");
      return;
    }
    if(CollectionUtil.isEmpty(bmDTOs)){
      return;
    }
    StringBuilder sb=new StringBuilder();
    for(ShopVehicleBrandModelDTO bmDTO:bmDTOs){
      sb.append(bmDTO.getBrandName()).append(bmDTO.getModelName()).append(",");
    }
    String bmStr=sb.toString();
    if(bmStr.length()>0){
      setShopVehicleBrandModelStr(bmStr.substring(0,bmStr.length()-1));
      return;
    }
    setShopVehicleBrandModelStr("");
  }

  public List<String> getContactMobiles() {
    return contactMobiles;
  }

  public void setContactMobiles(List<String> contactMobiles) {
    this.contactMobiles = contactMobiles;
  }

  public void generatePartShopInfo(){
    this.setName(this.getName());
    if(StringUtils.isNotBlank(this.getStoreManagerMobile()) && this.getStoreManagerMobile().length()>3){
      this.setStoreManagerMobile(this.getStoreManagerMobile().substring(0,3)+"********");
    }

    if(StringUtils.isNotBlank(this.getStoreManager())){
      this.setStoreManager(this.getStoreManager().substring(0,1)+"**");
    }

    this.setLandline("********");
    this.setFax("********");
    this.setQq("********");
    if(StringUtils.isNotBlank(this.getEmail()) && this.getEmail().indexOf("@")>-1){
      this.setEmail("******@"+this.getEmail().split("@")[1]);
    }else{
      this.setEmail("******");
    }
    if(!ArrayUtils.isEmpty(getContacts())){
      for(ContactDTO contact : getContacts()){
        if(contact!=null && StringUtils.isNotBlank(contact.getMobile())){
          contact.setMobile(contact.getMobile().substring(0, 3) + "********");
        }
      }
    }
    this.setAddress("");
  }

  public boolean isValidMainContact(){
    if (StringUtils.isBlank(this.getContact()) && StringUtils.isBlank(this.getMobile()) && StringUtils.isBlank(this.getEmail()) && StringUtils.isBlank(this.getQq())) {
      return false;
    }
    return true;
  }
  public void filterHasMobileContact(){
    if(!ArrayUtils.isEmpty(this.getContacts())){
      List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
      for(ContactDTO contactDTO : this.getContacts()){
        if(contactDTO == null) continue;
        if (StringUtils.isNotBlank(contactDTO.getMobile())) {
          contactDTOList.add(contactDTO);
        }
      }
      if(CollectionUtils.isNotEmpty(contactDTOList)){
        this.setContacts(contactDTOList.toArray(new ContactDTO[contactDTOList.size()]));
      }else{
        this.setContacts(null);
      }
    }
  }
  public Set<Long> getProductCategoryIds() {
    return productCategoryIds;
  }

  public void setProductCategoryIds(Set<Long> productCategoryIds) {
    this.productCategoryIds = productCategoryIds;
  }

  public Long getFollowId() {
    return followId;
  }

  public void setFollowId(Long followId) {
    this.followId = followId;
  }

  public String getFollowName() {
    return followName;
  }

  public void setFollowName(String followName) {
    this.followName = followName;
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public String getAgentMobile() {
    return agentMobile;
  }

  public void setAgentMobile(String agentMobile) {
    this.agentMobile = agentMobile;
  }

  public Long getAgentDBId() {
    return agentDBId;
  }

  public void setAgentDBId(Long agentDBId) {
    this.agentDBId = agentDBId;
  }

  public String getContactMobile() {
    return contactMobile;
  }

  public void setContactMobile(String contactMobile) {
    this.contactMobile = contactMobile;
  }

  public String getQqArray(){
    return qqArray;
  }

  public void setQqArray(String qqArray) {
    this.qqArray = qqArray;
  }

  public String getCoordinateLat() {
    return coordinateLat;
  }

  public void setCoordinateLat(String coordinateLat) {
    this.coordinateLat = coordinateLat;
  }

  public String getCoordinateLon() {
    return coordinateLon;
  }

  public void setCoordinateLon(String coordinateLon) {
    this.coordinateLon = coordinateLon;
  }

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  public List<Node> getServiceCategory() {
    return serviceCategory;
  }

  public void setServiceCategory(List<Node> serviceCategory) {
    this.serviceCategory = serviceCategory;
  }

  public String getServiceCategoryIdStr() {
    return serviceCategoryIdStr;
  }

  public void setServiceCategoryIdStr(String serviceCategoryIdStr) {
    this.serviceCategoryIdStr = serviceCategoryIdStr;
  }

  public String getServiceCategoryStr() {
    return serviceCategoryStr;
  }

  public void setServiceCategoryStr(String serviceCategoryStr) {
    this.serviceCategoryStr = serviceCategoryStr;
  }

  public String getAccidentMobile() {
    return accidentMobile;
  }

  public void setAccidentMobile(String accidentMobile) {
    this.accidentMobile = accidentMobile;
  }

  @Override
  public String toString() {
    return JsonUtil.objectToJson(this);
//    return "ShopDTO{" +
//        "id=" + id +
//        ", name='" + name + '\'' +
//        ", legalRep='" + legalRep + '\'' +
//        ", no='" + no + '\'' +
//        ", areaId=" + areaId +
//        ", areaName='" + areaName + '\'' +
//        ", province=" + province +
//        ", city=" + city +
//        ", region=" + region +
//        ", address='" + address + '\'' +
//        ", zip='" + zip + '\'' +
//        ", contact='" + contact + '\'' +
//        ", contacts=" + (contacts == null ? null : Arrays.asList(contacts)) +
//        ", landline='" + landline + '\'' +
//        ", mobile='" + mobile + '\'' +
//        ", fax='" + fax + '\'' +
//        ", email='" + email + '\'' +
//        ", qq='" + qq + '\'' +
//        ", bank='" + bank + '\'' +
//        ", account='" + account + '\'' +
//        ", categoryId=" + categoryId +
//        ", account_name='" + account_name + '\'' +
//        ", reviewer='" + reviewer + '\'' +
//        ", reviewDate=" + reviewDate +
//        ", agent='" + agent + '\'' +
//        ", memo='" + memo + '\'' +
//        ", shopVersionId=" + shopVersionId +
//        ", shopVersionName='" + shopVersionName + '\'' +
//        ", owner='" + owner + '\'' +
//        ", operator='" + operator + '\'' +
//        ", agentId=" + agentId +
//        ", agentMobile='" + agentMobile + '\'' +
//        ", softPrice=" + softPrice +
//        ", bargainPrice=" + bargainPrice +
//        ", businessHours='" + businessHours + '\'' +
//        ", established=" + established +
//        ", establishedStr='" + establishedStr + '\'' +
//        ", qualification='" + qualification + '\'' +
//        ", personnel='" + personnel + '\'' +
//        ", area='" + area + '\'' +
//        ", operationMode='" + operationMode + '\'' +
//        ", operationModeBrand='" + operationModeBrand + '\'' +
//        ", operationModes=" + (operationModes == null ? null : Arrays.asList(operationModes)) +
//        ", otherOperationMode='" + otherOperationMode + '\'' +
//        ", operationModeStr='" + operationModeStr + '\'' +
//        ", businessScope='" + businessScope + '\'' +
//        ", businessScopeStr='" + businessScopeStr + '\'' +
//        ", otherBusinessScope='" + otherBusinessScope + '\'' +
//        ", majorProduct='" + majorProduct + '\'' +
//        ", businessScopes=" + (businessScopes == null ? null : Arrays.asList(businessScopes)) +
//        ", relatedBusiness='" + relatedBusiness + '\'' +
//        ", otherRelatedBusiness='" + otherRelatedBusiness + '\'' +
//        ", relatedBusinesses=" + (relatedBusinesses == null ? null : Arrays.asList(relatedBusinesses)) +
//        ", feature='" + feature + '\'' +
//        ", features=" + (features == null ? null : Arrays.asList(features)) +
//        ", otherVehicleBrand='" + otherVehicleBrand + '\'' +
//        ", otherFeature='" + otherFeature + '\'' +
//        ", attachment=" + attachment +
//        ", storeManager='" + storeManager + '\'' +
//        ", storeManagerMobile='" + storeManagerMobile + '\'' +
//        ", shortname='" + shortname + '\'' +
//        ", licencePlate='" + licencePlate + '\'' +
//        ", photo='" + photo + '\'' +
//        ", creationDateStr='" + creationDateStr + '\'' +
//        ", wholesalerShopId=" + wholesalerShopId +
//        ", registrationDate=" + registrationDate +
//        ", submitApplicationDate=" + submitApplicationDate +
//        ", editDate=" + editDate +
//        ", clueInputDate='" + clueInputDate + '\'' +
//        ", usedSoftware='" + usedSoftware + '\'' +
//        ", networkType=" + networkType +
//        ", hasComputer=" + hasComputer +
//        ", saleManId=" + saleManId +
//        ", saleManMapId=" + saleManMapId +
//        ", saleManName='" + saleManName + '\'' +
//        ", managerId=" + managerId +
//        ", managerUserNo='" + managerUserNo + '\'' +
//        ", scene=" + scene +
//        ", relativeCustomerAmount=" + relativeCustomerAmount +
//        ", shopLevel=" + shopLevel +
//        ", trialStartTime=" + trialStartTime +
//        ", trialEndTime=" + trialEndTime +
//        ", usingEndTime=" + usingEndTime +
//        ", paymentStatus=" + paymentStatus +
//        ", shopKind=" + shopKind +
//        ", shopStatus=" + shopStatus +
//        ", shopStatusValue='" + shopStatusValue + '\'' +
//        ", shopState=" + shopState +
//        ", url='" + url + '\'' +
//        ", businessLicenseName='" + businessLicenseName + '\'' +
//        ", businessLicense=" + businessLicense +
//        ", businessLicenseId=" + businessLicenseId +
//        ", shopPhoto=" + shopPhoto +
//        ", shopPhotoId=" + shopPhotoId +
//        ", registerType=" + registerType +
//        ", invitationCode='" + invitationCode + '\'' +
//        ", invitationCodeDTO=" + invitationCodeDTO +
//        ", registrationDateStr='" + registrationDateStr + '\'' +
//        ", bargainStatus=" + bargainStatus +
//        ", customScore=" + customScore +
//        ", selectAllBrandModel=" + selectAllBrandModel == null?null:selectAllBrandModel.toString() +
//        ", shopSelectBrandModel=" + shopSelectBrandModel == null ? null : shopSelectBrandModel.toString() +
//        ", productDTOs=" + (productDTOs == null ? null : Arrays.asList(productDTOs)) +
//        ", shopVehicleBrandModelDTOs=" + (shopVehicleBrandModelDTOs == null ? null : Arrays.asList(shopVehicleBrandModelDTOs)) +
//        '}';
  }


  public String getMainContactMobile() {
    if (ArrayUtil.isNotEmpty(getContacts())) {
      for (ContactDTO dto : getContacts()) {
        if (dto != null && dto.getIsShopOwner() == 1 && dto.getIsMainContact() == 1 && dto.getDisabled() == 1 && StringUtil.isNotEmpty(dto.getMobile())) {
          return dto.getMobile();
        }
      }
    }
    return getMobile();
  }

  public LocateStatus getLocateStatus() {
    return locateStatus;
  }

  public void setLocateStatus(LocateStatus locateStatus) {
    this.locateStatus = locateStatus;
  }

  public Double getAdPricePerMonth() {
    return adPricePerMonth;
  }

  public void setAdPricePerMonth(Double adPricePerMonth) {
    this.adPricePerMonth = adPricePerMonth;
  }

  public Long getAdStartDate() {
    return adStartDate;
  }

  public void setAdStartDate(Long adStartDate) {
    this.adStartDate = adStartDate;
  }

  public Long getAdEndDate() {
    return adEndDate;
  }

  public void setAdEndDate(Long adEndDate) {
    this.adEndDate = adEndDate;
  }

  public ProductAdType getProductAdType() {
    return productAdType;
  }

  public void setProductAdType(ProductAdType productAdType) {
    this.productAdType = productAdType;
  }

  public String getAdStartDateStr() {
    return adStartDateStr;
  }

  public void setAdStartDateStr(String adStartDateStr) {
    this.adStartDateStr = adStartDateStr;
  }

  public String getAdEndDateStr() {
    return adEndDateStr;
  }

  public void setAdEndDateStr(String adEndDateStr) {
    this.adEndDateStr = adEndDateStr;
  }

  public Set<Long> getShopAdAreaIds() {
    return shopAdAreaIds;
  }

  public void setShopAdAreaIds(Set<Long> shopAdAreaIds) {
    this.shopAdAreaIds = shopAdAreaIds;
  }

  public Set<Long> getRecommendIds() {
    return recommendIds;
  }

  public void setRecommendIds(Set<Long> recommendIds) {
    this.recommendIds = recommendIds;
  }
}
