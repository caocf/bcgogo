package com.bcgogo.search.dto;

import com.bcgogo.api.AppShopDTO;
import com.bcgogo.api.AppShopSuggestion;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.util.Collection;

/**
 * User: ZhangJuntao
 * Date: 13-8-6
 * Time: 下午6:04
 */
public class ShopSolrDTO {
  private Long id;
  private String idStr;
  private Double locationLat;
  private Double locationLon;
  private String locationGeohash;
  private String name;
  private String province;     //省
  private Long provinceNo;     //省
  private String city;          //市
  private Long cityNo;          //市
  private String region;        //区域
  private Long regionNo;        //区域
  private String address;
  private Integer cityCode;//店铺百度地图编码
  private Integer provinceCode;//店铺百度地图编码
  private Double totalScore; //评分
  private String[] serviceScopeIds;
  private String[] serviceScopes;
  private Double distance;
  private ShopKind shopKind;
  private String shopType;

  public ShopSolrDTO() {
  }


  public void from(CommentStatDTO dto) {
    if (dto != null && dto.getCommentTotalScore() != null && dto.getRecordAmount() != null) {
      if(dto.getRecordAmount()==0){
        setTotalScore(0D);
      } else{
        setTotalScore(dto.getCommentTotalScore() / dto.getRecordAmount());
      }
    } else {
      setTotalScore(0D);
    }
//    setTotalScore(new Random().nextInt(50) / 10.0);
  }

  public ShopSolrDTO(SolrDocument document) {
    setId(Long.valueOf((String) document.getFieldValue("id")));
    setName((String) document.getFirstValue("name"));
    setAddress((String) document.getFirstValue("address"));
    setProvince((String) document.getFirstValue("province"));
    setProvinceNo((Long) document.getFirstValue("province_no"));
    setCity((String) document.getFirstValue("city"));
    setCityNo((Long) document.getFirstValue("city_no"));
    setRegion((String) document.getFirstValue("region"));
    setRegionNo((Long) document.getFirstValue("region_no"));
    setLocationLat((Double) document.getFirstValue("shop_location_lat_lon_0_coordinate"));
    setLocationLon((Double) document.getFirstValue("shop_location_lat_lon_1_coordinate"));
    setDistance((Double) document.getFirstValue("distance"));
    setTotalScore((Double) document.getFieldValue("total_score"));
    setCityCode((Integer) document.getFirstValue("city_code"));
    setProvinceCode((Integer) document.getFirstValue("province_code"));
    setShopType((String) document.getFirstValue("shop_type"));
    Collection collection = document.getFieldValues("service_scope_ids");
    setServiceScopeIds(collection == null ? null : (String[]) collection.toArray(new String[collection.size()]));
  }

  public SolrInputDocument toSolrInputDocument() {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", getId());
    doc.addField("name", getName());
    PingyinInfo pingyinInfo = PinyinUtil.getPingyinInfo(getName());
    if (pingyinInfo != null) {
      doc.addField("name_fl", pingyinInfo.firstLetters);
      doc.addField("name_py", pingyinInfo.pingyin);
    }
    doc.addField("address", getAddress());
    doc.addField("shop_type", getShopType());
    doc.addField("province", getProvince());
    doc.addField("province_no", getProvinceNo());
    doc.addField("province_code", getProvinceCode());
    doc.addField("city", getCity());
    doc.addField("city_no", getCityNo());
    doc.addField("city_code", getCityCode());
    doc.addField("region", getRegion());
    doc.addField("region_no", getRegionNo());
    doc.addField("shop_kind", getShopKind());
    if (getLocationLat() != null && getLocationLon() != null) {
      doc.addField("shop_location_lat_lon", getLocationLat() + "," + getLocationLon());
    }
    if (getTotalScore() != null) {
      doc.addField("total_score", getTotalScore());
    }
    if (ArrayUtil.isNotEmpty(getServiceScopeIds())) {
      for (String str : getServiceScopeIds()) {
        doc.addField("service_scope_ids", str);
      }
    }
    return doc;
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

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public Long getProvinceNo() {
    return provinceNo;
  }

  public void setProvinceNo(Long provinceNo) {
    this.provinceNo = provinceNo;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Long getCityNo() {
    return cityNo;
  }

  public void setCityNo(Long cityNo) {
    this.cityNo = cityNo;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public Long getRegionNo() {
    return regionNo;
  }

  public void setRegionNo(Long regionNo) {
    this.regionNo = regionNo;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if (id != null) {
      this.setIdStr(String.valueOf(id));
    }
    this.id = id;
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

  public String getLocationGeohash() {
    return locationGeohash;
  }

  public void setLocationGeohash(String locationGeohash) {
    this.locationGeohash = locationGeohash;
  }

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  public AppShopDTO toAppShopDTO() {
    AppShopDTO dto = new AppShopDTO();
    dto.setId(getId());
    dto.setName(getName());
    dto.setCoordinate(getLocationLon() + "," + getLocationLat());
    dto.setCityCode(getCityCode());
    dto.setAddress(getAddress());
    dto.setDistance(getDistance());
    dto.setTotalScore(getTotalScore());
    if (ArrayUtil.isNotEmpty(getServiceScopes())) {
      String services = "";
      int i = 0;
      for (String str : getServiceScopes()) {
        if (i++ != 0) {
          services += ",";
        }
        services += str;
      }
      dto.setServiceScope(services);
    }
    return dto;
  }

  public AppShopSuggestion toAppShopSuggestion() {
    AppShopSuggestion dto = new AppShopSuggestion();
    dto.setId(getId());
    dto.setName(getName());
    return dto;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public Integer getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(Integer provinceCode) {
    this.provinceCode = provinceCode;
  }

  public String[] getServiceScopeIds() {
    return serviceScopeIds;
  }

  public void setServiceScopeIds(String[] serviceScopeIds) {
    this.serviceScopeIds = serviceScopeIds;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public String[] getServiceScopes() {
    return serviceScopes;
  }

  public void setServiceScopes(String[] serviceScopes) {
    this.serviceScopes = serviceScopes;
  }

  public String getShopType() {
    return shopType;
  }

  public void setShopType(String shopType) {
    this.shopType = shopType;
  }
}
