package com.bcgogo.config;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-5-26
 * Time: 下午1:23
 */
public class CustomizerConfigResult implements Serializable {
  private String name;
  private String value;
  private boolean checked = true;
  private Integer sort = 1;
  //  private String resourceName;
  private boolean necessary = false;
  private List<CustomizerConfigInfo> configInfoList = new ArrayList<CustomizerConfigInfo>();


  public CustomizerConfigResult() {
    super();
  }

  public CustomizerConfigResult(String name, String value, Integer sort) {
    this.setName(name);
    this.setSort(sort);
    this.setValue(value);
  }

  public void sortConfigInfoList() {
    if (CollectionUtils.isNotEmpty(this.getConfigInfoList())) {
      Collections.sort(this.configInfoList, new Comparator<CustomizerConfigInfo>() {
        @Override
        public int compare(CustomizerConfigInfo o1, CustomizerConfigInfo o2) {
          if (o1.getSort() == null) {
            return -1;
          }
          if (o2.getSort() == null) {
            return 1;
          }
          try {
            return o1.getSort().compareTo(o2.getSort());
          } catch (Exception e) {
            return -1;
          }
        }
      });
    }
  }

  public void isAllConfigInfoListUnchecked() {
    boolean isAllUnchecked = true;
    if (CollectionUtils.isNotEmpty(this.getConfigInfoList())) {
      for (CustomizerConfigInfo info : this.getConfigInfoList()) {
        if (info.getChecked() != null && info.getChecked()) {
          isAllUnchecked = false;
        }
      }
    }
    this.setChecked(isAllUnchecked);
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

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

//  public String getResourceName() {
//    return resourceName;
//  }
//
//  public void setResourceName(String resourceName) {
//    this.resourceName = resourceName;
//  }

  public boolean isNecessary() {
    return necessary;
  }

  public void setNecessary(boolean necessary) {
    this.necessary = necessary;
  }

  public List<CustomizerConfigInfo> getConfigInfoList() {
    return configInfoList;
  }

  public void setConfigInfoList(List<CustomizerConfigInfo> configInfoList) {
    this.configInfoList = configInfoList;
  }
}
