package com.bcgogo.product.dto;

import com.bcgogo.product.TemplateRequest;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:52
 * To change this template use File | Settings | File Templates.
 */
public class TemplateDTO implements Serializable {
  private String name;
  private Integer type;
  private String template;
  private Long kindId;
  private BigDecimal ver;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;

  public TemplateDTO() {
  }

  public TemplateDTO(TemplateRequest request) {
    setName(request.getName());
    setType(request.getType());
    setTemplate(request.getTemplate());
    setKindId(request.getKindId());
    setVer(request.getVer());
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  public BigDecimal getVer() {
    return ver;
  }

  public void setVer(BigDecimal ver) {
    this.ver = ver;
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
