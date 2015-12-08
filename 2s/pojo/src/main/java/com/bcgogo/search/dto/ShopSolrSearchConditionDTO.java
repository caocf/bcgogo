package com.bcgogo.search.dto;

import com.bcgogo.enums.DataKind;
import com.bcgogo.enums.app.SortType;
import com.bcgogo.enums.shop.ShopType;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 */
public class ShopSolrSearchConditionDTO {
  private enum Strategy {
    SERVICE_SCOPE_WASH
  }

  private Strategy strategy = null;
  private int start;
  private int limit = 10;
  private Integer pageNo;
  private Integer pageSize;
  private ShopType shopType;
  private String[] shopTypes;
  private String name;
  private String keyword;
  private String sort;//排序规则
  private Double locationLat;
  private Double locationLon;
  private Double locationDistance;
  private Integer areaId;
  private Integer cityCode;
  private String appUserNo;
  private DataKind dataKind;
  private String serviceScopeIds;
  //3种特殊店铺
  private List<Long> recommendShopIds; //推荐店铺ids
  private Long lastExpenseShopId; //最后消费店铺id
  private List<Long> memberCardShopIds;//会员店铺
  private Set<Long> specialShopIds = new HashSet<Long>(); //特殊店铺
  private Set<Long> excludeShopIds = new HashSet<Long>(); //查询排除店铺
  private boolean isMore = false;

  public void setServiceScopeWashStrategy() {
    strategy = Strategy.SERVICE_SCOPE_WASH;
  }

  public boolean isServiceScopeWashStrategy() {
    return strategy == Strategy.SERVICE_SCOPE_WASH;
  }

  public Set<Long> getSpecialAndExcludeShopIds() {
    Set<Long> shopIds = new HashSet<Long>();
    shopIds.addAll(specialShopIds);
    shopIds.addAll(excludeShopIds);
    return shopIds;
  }

  public void addExcludeShopIds(Set<Long> ids) {
    excludeShopIds.addAll(ids);
  }

  public void setSpecialShopIds() {
    if (getLastExpenseShopId() != null) {
      specialShopIds.add(getLastExpenseShopId());
    }
    if (CollectionUtil.isNotEmpty(getRecommendShopIds())) {
      specialShopIds.addAll(new HashSet<Long>(getRecommendShopIds()));
    }
    if (CollectionUtil.isNotEmpty(getMemberCardShopIds()) && isWashCarServiceShopQuery()) {
      specialShopIds.addAll(new HashSet<Long>(getMemberCardShopIds()));
    }
  }

  public void sortType2Sort(SortType sortType) {
    if (sortType == null || sortType == SortType.DISTANCE) {
      setSort("geodist() asc,total_score desc");
    } else {
      setSort("total_score desc,geodist() asc");
    }
  }

  public boolean isRecommendShopQuery() {
    return StringUtil.isEmpty(keyword) && !isMore && (CollectionUtil.isNotEmpty(getRecommendShopIds()));
  }


  public boolean isWashCarServiceShopQuery() {
    return StringUtil.isEmpty(keyword) && !isMore && (CollectionUtil.isNotEmpty(getMemberCardShopIds())) && isServiceScopeWashStrategy();
  }

  public boolean isLastExpenseShopQuery() {
    return StringUtil.isEmpty(keyword) && !isMore && (getLastExpenseShopId() != null);
  }

  public boolean isNotSpecialQuery() {
    return !isSpecialQuery();
  }

  public boolean isSpecialQuery() {
    return (isLastExpenseShopQuery() || isWashCarServiceShopQuery()) || isRecommendShopQuery();
  }

  public Set<Long> getSpecialShopIds() {
    return specialShopIds;
  }

  public void setSpecialShopIds(Set<Long> specialShopIds) {
    this.specialShopIds = specialShopIds;
  }

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
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

  public Integer getAreaId() {
    return areaId;
  }

  public void setAreaId(Integer areaId) {
    this.areaId = areaId;
  }

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }


  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getServiceScopeIds() {
    return serviceScopeIds;
  }

  public void setServiceScopeIds(String serviceScopeIds) {
    this.serviceScopeIds = serviceScopeIds;
  }

  public Long getLastExpenseShopId() {
    return lastExpenseShopId;
  }

  public void setLastExpenseShopId(Long lastExpenseShopId) {
    this.lastExpenseShopId = lastExpenseShopId;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public ShopType getShopType() {
    return shopType;
  }

  public void setShopType(ShopType shopType) {
    this.shopType = shopType;
  }

  public List<Long> getMemberCardShopIds() {
    return memberCardShopIds;
  }

  public void setMemberCardShopIds(List<Long> memberCardShopIds) {
    this.memberCardShopIds = memberCardShopIds;
  }


  public Set<Long> getExcludeShopIds() {
    return excludeShopIds;
  }

  public void setExcludeShopIds(Set<Long> excludeShopIds) {
    this.excludeShopIds = excludeShopIds;
  }

  public DataKind getDataKind() {
    return dataKind;
  }

  public void setDataKind(DataKind dataKind) {
    this.dataKind = dataKind;
  }

  public String[] getShopTypes() {
    return shopTypes;
  }

  public void setShopTypes(String[] shopTypes) {
    this.shopTypes = shopTypes;
  }

  public boolean getIsMore() {
    return isMore;
  }

  public void setIsMore(boolean isMore) {
    this.isMore = isMore;
  }

  public List<Long> getRecommendShopIds() {
    return recommendShopIds;
  }

  public void setRecommendShopIds(List<Long> recommendShopIds) {
    this.recommendShopIds = recommendShopIds;
  }
}
