package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "synonym")
@XmlAccessorType(XmlAccessType.NONE)
public class SynonymRequest {
  @XmlElement(name = "type")
  private Integer type;
  @XmlElement(name = "targetId")
  private Long targetId;
  @XmlElement(name = "word")
  private String word;
  @XmlElement(name = "shopId")
  private Long shopId;

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
}
