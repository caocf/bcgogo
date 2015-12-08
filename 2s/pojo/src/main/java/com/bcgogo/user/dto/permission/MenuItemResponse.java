package com.bcgogo.user.dto.permission;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-4-12
 * Time: 下午4:38
 */
public class MenuItemResponse {
  private String label;
  private String href;
  private String uid;
  private List<MenuItemResponse> item = new ArrayList<MenuItemResponse>();

  public MenuItemResponse() {
    super();
  }

  public MenuItemResponse(String label, String href, String uid) {
    this.setLabel(label);
    this.setHref(href);
    this.setUid(uid);
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public List<MenuItemResponse> getItem() {
    return item;
  }

  public void setItem(List<MenuItemResponse> item) {
    this.item = item;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
}
