package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:18
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "product-spec")
@XmlAccessorType(XmlAccessType.NONE)
public class ProductSpecRequest {
  @XmlElement(name = "productId")
  private Long productId;
  @XmlElement(name = "featureId")
  private Long featureId;
  @XmlElement(name = "feature")
  private String feature;
  @XmlElement(name = "dataType")
  private Integer dataType;
  @XmlElement(name = "str")
  private String str;
  @XmlElement(name = "integer")
  private Integer integer;
  @XmlElement(name = "dt")
  private Timestamp dt;
  @XmlElement(name = "lstr")
  private String lstr;
  @XmlElement(name = "dec")
  private BigDecimal dec;
  @XmlElement(name = "text")
  private String text;
  @XmlElement(name = "uid")
  private String uid;
  @XmlElement(name = "xml")
  private String xml;
  @XmlElement(name = "file")
  private Blob file;
  @XmlElement(name = "shopId")
  private Long shopId;

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
}
