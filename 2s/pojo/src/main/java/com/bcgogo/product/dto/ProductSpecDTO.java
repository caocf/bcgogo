package com.bcgogo.product.dto;

import com.bcgogo.product.ProductSpecRequest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:43
 * To change this template use File | Settings | File Templates.
 */
public class ProductSpecDTO implements Serializable {
  private Long productId;
  private Long featureId;
  private String feature;
  private Integer dataType;
  private String str;
  private Integer integer;
  private Timestamp dt;
  private String lstr;
  private BigDecimal dec;
  private String text;
  private String uid;
  private String xml;
  private Blob file;
  private Long id;
  private Long shopId;

  public ProductSpecDTO() {
  }

  public ProductSpecDTO(ProductSpecRequest request) {
    setProductId(request.getProductId());
    setFeatureId(request.getFeatureId());
    setFeature(request.getFeature());
    setDataType(request.getDataType());
    setStr(request.getStr());
    setInteger(request.getInteger());
    setDt(request.getDt());
    setLstr(request.getStr());
    setDec(request.getDec());
    setText(request.getText());
    setUid(request.getUid());
    setXml(request.getXml());
    setFile(request.getFile());
    setShopId(request.getShopId());
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getFeatureId() {
    return featureId;
  }

  public void setFeatureId(Long featureId) {
    this.featureId = featureId;
  }

  public String getFeature() {
    return feature;
  }

  public void setFeature(String feature) {
    this.feature = feature;
  }

  public Integer getDataType() {
    return dataType;
  }

  public void setDataType(Integer dataType) {
    this.dataType = dataType;
  }

  public String getStr() {
    return str;
  }

  public void setStr(String str) {
    this.str = str;
  }

  public Integer getInteger() {
    return integer;
  }

  public void setInteger(Integer integer) {
    this.integer = integer;
  }

  public Timestamp getDt() {
    return dt;
  }

  public void setDt(Timestamp dt) {
    this.dt = dt;
  }

  public String getLstr() {
    return lstr;
  }

  public void setLstr(String lstr) {
    this.lstr = lstr;
  }

  public BigDecimal getDec() {
    return dec;
  }

  public void setDec(BigDecimal dec) {
    this.dec = dec;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

  public Blob getFile() {
    return file;
  }

  public void setFile(Blob file) {
    this.file = file;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
