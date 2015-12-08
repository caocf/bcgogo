package com.bcgogo.config;

import java.io.Serializable;

/**
 * User: ZhangJuntao
 * Date: 13-5-24
 * Time: 上午11:53
 */
public class CustomizerConfigInfo implements Serializable {
  private String name;
  private String value;
  private Boolean checked = true;
  private Integer sort = 1;
  private String groupName;
  private String resourceName;
  private Integer weight = 1;       //权重
  private Boolean necessary = false;

  public CustomizerConfigInfo(){
    super();
  }

  public CustomizerConfigInfo(String name, String value, Integer sort, String resourceName) {
    this.setResourceName(resourceName);
    this.setName(name);
    this.setSort(sort);
    this.setValue(value);
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

  public Boolean getChecked() {
    return checked;
  }

  public void setChecked(Boolean checked) {
    this.checked = checked;
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public Integer getWeight() {
    return weight;
  }

  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  public Boolean getNecessary() {
    return necessary;
  }

  public void setNecessary(Boolean necessary) {
    this.necessary = necessary;
  }
}
