package com.bcgogo.config.dto;

import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.utils.CollectionUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-19
 * Time: 上午9:39
 * To change this template use File | Settings | File Templates.
 */
public class ApplyShopSearchCondition implements Serializable {
  private String name;
  private Long provinceNo;     //省
  private Long cityNo;          //市
  private Long regionNo;        //区域
  private String province;     //省
  private String city;          //市
  private String region;        //区域
  private Long shopId;
  private Long shopAreaId;//自己店的areaId

  private String brandName;  //车辆品牌
  private String modelName; //车型

  private String keyword;

  //flow 用于排序
  private Long sortProvinceNo;
  private Long sortCityNo;
  private Long sortRegionNo;

  private String shopIdStr;
  private String searchArea;
  private String address;
  private String businessScope;
  private InviteStatus inviteStatus;
  private String[] thirdCategoryIdStr;
  private VehicleSelectBrandModel shopSelectBrandModel;
   private String shopVehicleBrandModelStr;

  //供应商评价
  private Double totalAverageScore;       //总平均分
  private Double qualityAverageScore;     //质量平均分
  private Double performanceAverageScore;//性价比平均分
  private Double speedAverageScore;       //发货速度平均分
  private Double attitudeAverageScore;    //服务态度平均分
  private Long commentRecordCount;          //评分参数人数
  private String remindCustomerMobiles;    //需要提醒关联的客户的手机号，以逗号分隔
  private Set<Long> shopIds;       //查询时包含的shopId
  private Set<Long> excludeShopIds;//查询时排除的shopId

  public String getRemindCustomerMobiles() {
    return remindCustomerMobiles;
  }

  public void setRemindCustomerMobiles(String remindCustomerMobiles) {
    this.remindCustomerMobiles = remindCustomerMobiles;
  }

  public Set<Long> getShopIds() {
    return shopIds;
  }

  public void setShopIds(Set<Long> shopIds) {
    this.shopIds = shopIds;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getProvinceNo() {
    return provinceNo;
  }

  public void setProvinceNo(Long provinceNo) {
    this.provinceNo = provinceNo;
  }

  public Long getCityNo() {
    return cityNo;
  }

  public void setCityNo(Long cityNo) {
    this.cityNo = cityNo;
  }

  public Long getRegionNo() {
    return regionNo;
  }

  public void setRegionNo(Long regionNo) {
    this.regionNo = regionNo;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
    if (shopId != null) {
      this.setShopIdStr(shopId.toString());
    } else {
      this.setShopIdStr("");
    }
  }

  public String getSearchArea() {
    return searchArea;
  }

  public void setSearchArea(String searchArea) {
    this.searchArea = searchArea;
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  public InviteStatus getInviteStatus() {
    return inviteStatus;
  }

  public void setInviteStatus(InviteStatus inviteStatus) {
    this.inviteStatus = inviteStatus;
  }

  public Long getShopAreaId() {
    return shopAreaId;
  }

  public void setShopAreaId(Long shopAreaId) {
    this.shopAreaId = shopAreaId;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public Long getSortProvinceNo() {
    return sortProvinceNo;
  }

  public void setSortProvinceNo(Long sortProvinceNo) {
    this.sortProvinceNo = sortProvinceNo;
  }

  public Long getSortCityNo() {
    return sortCityNo;
  }

  public void setSortCityNo(Long sortCityNo) {
    this.sortCityNo = sortCityNo;
  }

  public Long getSortRegionNo() {
    return sortRegionNo;
  }

  public void setSortRegionNo(Long sortRegionNo) {
    this.sortRegionNo = sortRegionNo;
  }


  public Double getTotalAverageScore() {
    return totalAverageScore;
  }

  public void setTotalAverageScore(Double totalAverageScore) {
    this.totalAverageScore = totalAverageScore;
  }

  public Double getQualityAverageScore() {
    return qualityAverageScore;
  }

  public void setQualityAverageScore(Double qualityAverageScore) {
    this.qualityAverageScore = qualityAverageScore;
  }

  public Double getPerformanceAverageScore() {
    return performanceAverageScore;
  }

  public void setPerformanceAverageScore(Double performanceAverageScore) {
    this.performanceAverageScore = performanceAverageScore;
  }

  public Double getSpeedAverageScore() {
    return speedAverageScore;
  }

  public void setSpeedAverageScore(Double speedAverageScore) {
    this.speedAverageScore = speedAverageScore;
  }

  public Double getAttitudeAverageScore() {
    return attitudeAverageScore;
  }

  public void setAttitudeAverageScore(Double attitudeAverageScore) {
    this.attitudeAverageScore = attitudeAverageScore;
  }

  public Long getCommentRecordCount() {
    return commentRecordCount;
  }

  public void setCommentRecordCount(Long commentRecordCount) {
    this.commentRecordCount = commentRecordCount;
  }

  public VehicleSelectBrandModel getShopSelectBrandModel() {
    return shopSelectBrandModel;
  }

  public void setShopSelectBrandModel(VehicleSelectBrandModel shopSelectBrandModel) {
    this.shopSelectBrandModel = shopSelectBrandModel;
  }

  public String getShopVehicleBrandModelStr() {
    return shopVehicleBrandModelStr;
  }

  public void setShopVehicleBrandModelStr(String shopVehicleBrandModelStr) {
    this.shopVehicleBrandModelStr = shopVehicleBrandModelStr;
  }

  //用于排序 空则补全
  public void setShopAreaInfo(ShopDTO shopDTO) {
    if(shopDTO == null){
      return;
    }
    if (shopDTO.getProvince() != null) {
      this.setSortProvinceNo(shopDTO.getProvince());
    }
    if (shopDTO.getCity() != null) {
      this.setSortCityNo(shopDTO.getCity());
    }
    if (shopDTO.getRegion() != null) {
      this.setSortRegionNo(shopDTO.getRegion());
    }
  }

  //用于显示供应商评分score panel
  public void fromSupplierCommentStat(CommentStatDTO commentStatDTO) {
    if (commentStatDTO == null) {
      return;
    }
    this.setTotalAverageScore(commentStatDTO.getTotalScore());
    this.setCommentRecordCount(commentStatDTO.getRecordAmount());
    this.setQualityAverageScore(commentStatDTO.getQualityTotalScore());
    this.setPerformanceAverageScore(commentStatDTO.getPerformanceTotalScore());
    this.setSpeedAverageScore(commentStatDTO.getSpeedTotalScore());
    this.setAttitudeAverageScore(commentStatDTO.getAttitudeTotalScore());
  }


  public String[] getThirdCategoryIdStr() {
    return thirdCategoryIdStr;
  }

  public void setThirdCategoryIdStr(String[] thirdCategoryIdStr) {
    this.thirdCategoryIdStr = thirdCategoryIdStr;
  }

  public Set<Long> getExcludeShopIds() {
    return excludeShopIds;
  }

  public void setExcludeShopIds(Set<Long> excludeShopIds) {
    this.excludeShopIds = excludeShopIds;
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
}
