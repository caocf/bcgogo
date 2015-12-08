package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午4:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "template")
public class Template extends LongIdentifier {
  private String name;
  private Integer type;
  private String template;
  private Long kindId;
  private BigDecimal ver;
  private Long state;
  private String memo;
  private Long shopId;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "name", length = 50)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "type")
  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  @Column(name = "template")
  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  @Column(name = "kind_id")
  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  @Column(name = "ver")
  public BigDecimal getVer() {
    return ver;
  }

  public void setVer(BigDecimal ver) {
    this.ver = ver;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "demo", length = 2000)

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
