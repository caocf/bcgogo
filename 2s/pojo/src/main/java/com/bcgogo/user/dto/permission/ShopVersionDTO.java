package com.bcgogo.user.dto.permission;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-14
 * Time: 上午9:21
 */
public class ShopVersionDTO  implements Serializable {
  private Long id;
  private String idStr;
  private String name;
  private String value;
  private String description;
  private Integer softPrice;

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ShopVersionDTO() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) this.idStr = id.toString();
  }

  public Integer getSoftPrice() {
    return softPrice;
  }

  @Override
  public String toString() {
    return "ShopVersionDTO{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", value='" + value + '\'' +
        ", description='" + description + '\'' +
        ", softPrice=" + softPrice +
        '}';
  }

  public void setSoftPrice(Integer softPrice) {
    this.softPrice = softPrice;
  }

}
