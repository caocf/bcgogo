package com.bcgogo.product.dto;

import com.bcgogo.product.SynonymRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
public class SynonymDTO implements Serializable {
  private Integer type;
  private Long targetId;
  private String word;
  private Long id;
  private Long shopId;

  public SynonymDTO() {
  }

  public SynonymDTO(SynonymRequest request) {
    setType(request.getType());
    setTargetId(request.getTargetId());
    setWord(request.getWord());
    setShopId(request.getShopId());
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Long getTargetId() {
    return targetId;
  }

  public void setTargetId(Long targetId) {
    this.targetId = targetId;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
