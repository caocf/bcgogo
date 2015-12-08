package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:01
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "feature-filter")
@XmlAccessorType(XmlAccessType.NONE)
public class FeatureFilterRequest {
  @XmlElement(name = "featureId")
  private Long featureId;
  @XmlElement(name = "name")
  private String name;
  @XmlElement(name = "nameEn")
  private String nameEn;
  @XmlElement(name = "filter")
  private String filter;
  @XmlElement(name = "filterSql")
  private String filterSql;
  @XmlElement(name = "state")
  private Long state;
  @XmlElement(name = "memo")
  private String memo;
  @XmlElement(name = "shopId")
  private Long shopId;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getFeatureId() {
    return featureId;
  }

  public void setFeatureId(Long featureId) {
    this.featureId = featureId;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getFilterSql() {
    return filterSql;
  }

  public void setFilterSql(String filterSql) {
    this.filterSql = filterSql;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

}
