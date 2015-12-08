package com.bcgogo.config;

import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 * 仅限于CRM
 */
public class ShopSearchCondition {
  private boolean hasPager = true;
  private int start;
  private int limit = 25;
  private String[] shopStatuses;//状态
  private String[] shopStates;
  private String[] bargainStatuses;
  private String[] locateStatuses;
  private String[] paymentStatus;
  private String[] shopVersionName;
  private Long[] shopVersionIds;
  private ShopSearchScene scene;
  private Long[] areaId;
  private Set<Long> userIds;//数据权限
  private String name;
  private String agent;       //销售人
  private String followName;       //跟进人
  private Long reviewDateStart;//审核时间
  private Long reviewDateEnd;//审核时间
  private String owner;
  private String province;
  private String city;
  private String region;
  private String[] registerType;
  private Long registrationDateStart;//注册时间
  private Long registrationDateEnd;//注册时间
  private Long submitApplicationDateStart;//提交申请时间
  private Long submitApplicationDateEnd;//提交申请时间
  private String sortFiled;
  private String sort;//排序规则
  private Double locationLat;
  private Double locationLon;
  private Double locationDistance;
  private Integer cityCode;
  private String appUserNo;

  private List<Long> shopIds = new ArrayList<Long>();

  public Long[] getShopVersionIds() {
    return shopVersionIds;
  }

  public void setShopVersionIds(Long[] shopVersionIds) {
    this.shopVersionIds = shopVersionIds;
  }

  public Long getReviewDateStart() {
    return reviewDateStart;
  }

  public void setReviewDateStart(Long reviewDateStart) {
    this.reviewDateStart = reviewDateStart;
  }

  public Long getReviewDateEnd() {
    return reviewDateEnd;
  }

  public void setReviewDateEnd(Long reviewDateEnd) {
    this.reviewDateEnd = reviewDateEnd;
  }

  public String[] getLocateStatuses() {
    return locateStatuses;
  }

  public void setLocateStatuses(String[] locateStatuses) {
    this.locateStatuses = locateStatuses;
  }

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  public String getSortFiled() {
    return sortFiled;
  }

  public void setSortFiled(String sortFiled) {
    this.sortFiled = sortFiled;
  }

  public String[] getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(String[] paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public Long getRegistrationDateStart() {
    return registrationDateStart;
  }

  public void setRegistrationDateStart(Long registrationDateStart) {
    this.registrationDateStart = registrationDateStart;
  }

  public Long getRegistrationDateEnd() {
    return registrationDateEnd;
  }

  public void setRegistrationDateEnd(Long registrationDateEnd) {
    this.registrationDateEnd = registrationDateEnd;
  }

  public Long getSubmitApplicationDateStart() {
    return submitApplicationDateStart;
  }

  public void setSubmitApplicationDateStart(Long submitApplicationDateStart) {
    this.submitApplicationDateStart = submitApplicationDateStart;
  }

  public Long getSubmitApplicationDateEnd() {
    return submitApplicationDateEnd;
  }

  public void setSubmitApplicationDateEnd(Long submitApplicationDateEnd) {
    this.submitApplicationDateEnd = submitApplicationDateEnd;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
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

  public boolean isHasPager() {
    return hasPager;
  }

  public void setHasPager(boolean hasPager) {
    this.hasPager = hasPager;
  }


  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public String[] getShopStatuses() {
    return shopStatuses;
  }

  public void setShopStatuses(String[] shopStatuses) {
    this.shopStatuses = shopStatuses;
  }

  public Long[] getAreaId() {
    return areaId;
  }

  public void setAreaId(Long[] areaId) {
    this.areaId = areaId;
  }

  public Set<Long> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<Long> userIds) {
    this.userIds = userIds;
  }

  public ShopSearchScene getScene() {
    return scene;
  }

  public void setScene(ShopSearchScene scene) {
    this.scene = scene;
  }

  public String[] getShopVersionName() {
    return shopVersionName;
  }

  public void setShopVersionName(String[] shopVersionName) {
    this.shopVersionName = shopVersionName;
  }

  public String[] getShopStates() {
    return shopStates;
  }

  public void setShopStates(String[] shopStates) {
    this.shopStates = shopStates;
  }

  public String[] getRegisterType() {
    return registerType;
  }

  public void setRegisterType(String[] registerType) {
    this.registerType = registerType;
  }

  public String[] getBargainStatuses() {
    return bargainStatuses;
  }

  public void setBargainStatuses(String[] bargainStatuses) {
    this.bargainStatuses = bargainStatuses;
  }

  public List<Long> getShopIds() {
    return shopIds;
  }

  public void setShopIds(List<Long> shopIds) {
    this.shopIds = shopIds;
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  public String getFollowName() {
    return followName;
  }

  public void setFollowName(String followName) {
    this.followName = followName;
  }

  public Double getLocationLat() {
    return locationLat;
  }

  public void setLocationLat(Double locationLat) {
    this.locationLat = locationLat;
  }

  public Double getLocationLon() {
    return locationLon;
  }

  public void setLocationLon(Double locationLon) {
    this.locationLon = locationLon;
  }

  public Double getLocationDistance() {
    return locationDistance;
  }

  public void setLocationDistance(Double locationDistance) {
    this.locationDistance = locationDistance;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }
  public BargainStatus[] getBargainStatusesEnum() {
    if(!ArrayUtils.isEmpty(bargainStatuses)){
      List<BargainStatus> bargainStatusList = new ArrayList<BargainStatus>();
      for(String str:bargainStatuses)
        bargainStatusList.add(BargainStatus.valueOf(str));
      return bargainStatusList.toArray(new BargainStatus[bargainStatusList.size()]);
    }
    return null;
  }
}
