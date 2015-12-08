package com.bcgogo.product.standardVehicleBrandModel;

/**
 * 标准车辆品牌
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:09
 * To change this template use File | Settings | File Templates.
 */
public class StandardVehicleBrandDTO {
  private String name;
  private Long id;
  private String firstLetter;//车辆品牌首字母（不一定是首个汉字的首字母）
  private Integer frequency;

  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
