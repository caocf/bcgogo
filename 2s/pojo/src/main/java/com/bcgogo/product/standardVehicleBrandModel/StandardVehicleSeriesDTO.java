package com.bcgogo.product.standardVehicleBrandModel;

/**
 * 车系
 * Author: ndong
 * Date: 14-11-6
 * Time: 上午11:41
 */
public class StandardVehicleSeriesDTO {
  private Long id;
  private String name;
  private String groupName;

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

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
}
