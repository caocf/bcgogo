package com.bcgogo.product.dto;

import com.bcgogo.product.FeatureFilterRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:57
 * To change this template use File | Settings | File Templates.
 */
public class FeatureFilterDTO implements Serializable {
  private Long featureId;
  private String name;
  private String nameEn;
  private String filter;
  private String filterSql;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  public FeatureFilterDTO() {
  }

  public FeatureFilterDTO(FeatureFilterRequest request) {
    setFeatureId(request.getFeatureId());
    setNameEn(request.getNameEn());
    setName(request.getName());
    setFilter(request.getFilter());
    setFilterSql(request.getFilterSql());
    setState(request.getState());
    setMemo(request.getMemo());
    setShopId(request.getShopId());
  }

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

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
