package com.bcgogo.api;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.util.Collection;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午5:23
 */
public class AppShopDTO {
  private Long id;
  private String name;
  private String serviceScope; //经营范围
  private List<ShopServiceCategoryDTO> productCategoryList;
  private String smallImageUrl;
  private String bigImageUrl;
  private String coordinate;
  private Double distance;
  private Double totalScore;
  private MemberInfoDTO memberInfo;
  private String mobile;
  private String landLine;
  private String address;
  private Integer cityCode;
  private String accidentMobile;//事故专员手机

  public AppShopDTO() {
  }

  public AppShopDTO(ShopDTO dto) {
    if (dto != null) {
      setId(dto.getId());
      setName(dto.getName());
      setAddress(dto.getAddress());
      setCoordinate(dto.getCoordinateLon() + "," + dto.getCoordinateLat());
      setCityCode(dto.getCityCode());
      setMobile(dto.getMainContactMobile());
      setLandLine(dto.getLandline());
      setAccidentMobile(dto.getAccidentMobile());
    }
  }

  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  public void from(CommentStatDTO dto) {
    if (dto != null && NumberUtil.doubleVal(dto.getCommentTotalScore()) > 0) {
      this.setTotalScore(NumberUtil.toReserve(dto.getCommentTotalScore() / (dto.getCommentFiveAmount() + dto.getCommentFourAmount() + dto.getCommentThreeAmount()
          + dto.getCommentTwoAmount() + dto.getCommentOneAmount())));
    }
  }

  public List<ShopServiceCategoryDTO> getProductCategoryList() {
    return productCategoryList;
  }

  public void setProductCategoryList(List<ShopServiceCategoryDTO> productCategoryList) {
    this.productCategoryList = productCategoryList;
  }

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  public Long getId() {
    return id;
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

  public String getServiceScope() {
    return serviceScope;
  }

  public void setServiceScope(String serviceScope) {
    this.serviceScope = serviceScope;
  }

  public String getSmallImageUrl() {
    return smallImageUrl;
  }

  public void setSmallImageUrl(String smallImageUrl) {
    this.smallImageUrl = smallImageUrl;
  }

  public String getBigImageUrl() {
    return bigImageUrl;
  }

  public void setBigImageUrl(String bigImageUrl) {
    this.bigImageUrl = bigImageUrl;
  }

  public String getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(String coordinate) {
    this.coordinate = coordinate;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = NumberUtil.round(distance, 2);
  }

  public Double getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Double totalScore) {
    if (totalScore != null) {
      this.totalScore = NumberUtil.round(totalScore, 1);
    } else {
      this.totalScore = null;
    }
  }

  public MemberInfoDTO getMemberInfo() {
    return memberInfo;
  }

  public void setMemberInfo(MemberInfoDTO memberInfo) {
    this.memberInfo = memberInfo;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAccidentMobile() {
    return accidentMobile;
  }

  public void setAccidentMobile(String accidentMobile) {
    this.accidentMobile = accidentMobile;
  }
}
