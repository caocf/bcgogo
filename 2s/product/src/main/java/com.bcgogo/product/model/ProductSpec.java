package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午5:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_spec")
public class ProductSpec extends LongIdentifier {
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
  private Long shopId;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "feature_id")
  public Long getFeatureId() {
    return featureId;
  }

  public void setFeatureId(Long featureId) {
    this.featureId = featureId;
  }

  @Column(name = "feature", length = 200)
  public String getFeature() {
    return feature;
  }

  public void setFeature(String feature) {
    this.feature = feature;
  }

  @Column(name = "datatype")
  public Integer getDataType() {
    return dataType;
  }

  public void setDataType(Integer dataType) {
    this.dataType = dataType;
  }

  @Column(name = "str", length = 200)
  public String getStr() {
    return str;
  }

  public void setStr(String str) {
    this.str = str;
  }

  @Column(name = "ingeter")
  public Integer getInteger() {
    return integer;
  }

  public void setInteger(Integer integer) {
    this.integer = integer;
  }

  @Column(name = "dt")
  public Timestamp getDt() {
    return dt;
  }

  public void setDt(Timestamp dt) {
    this.dt = dt;
  }

  @Column(name = "lstr", length = 2000)
  public String getLstr() {
    return lstr;
  }

  public void setLstr(String lstr) {
    this.lstr = lstr;
  }

  @Column(name = "dec")
  public BigDecimal getDec() {
    return dec;
  }

  public void setDec(BigDecimal dec) {
    this.dec = dec;
  }

  @Column(name = "text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Column(name = "uid", length = 36)
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  @Column(name = "xml")
  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

  @Column(name = "file")
  public Blob getFile() {
    return file;
  }

  public void setFile(Blob file) {
    this.file = file;
  }
}
