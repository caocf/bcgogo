package com.bcgogo.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-5-25
 * Time: 16:10
 */
public class ApiMirrorArea {
  private String name;
  private String juheCityCode;
  private List<ApiMirrorArea> children = new ArrayList<ApiMirrorArea>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }

  public List<ApiMirrorArea> getChildren() {
    return children;
  }

  public void setChildren(List<ApiMirrorArea> children) {
    this.children = children;
  }
}
