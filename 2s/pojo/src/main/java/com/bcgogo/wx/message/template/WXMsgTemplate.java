package com.bcgogo.wx.message.template;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 模版消息
 * User: ndong
 * Date: 14-8-28
 * Time: 下午4:16
 * To change this template use File | Settings | File Templates.
 */
public class WXMsgTemplate implements Serializable{

  private static final long serialVersionUID = 5927785113933642559L;

  private String touser;
  private String template_id;
  private String url;
  private String topcolor;
  private String remark;
  private String remarkColor;
  Map<String,Map<String,String>> data=new HashMap<String,Map<String,String>>();

  public String getTouser() {
    return touser;
  }

  public void setTouser(String touser) {
    this.touser = touser;
  }

  public String getTemplate_id() {
    return template_id;
  }

  public void setTemplate_id(String template_id) {
    this.template_id = template_id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTopcolor() {
    return topcolor;
  }

  public void setTopcolor(String topcolor) {
    this.topcolor = topcolor;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getRemarkColor() {
    return remarkColor;
  }

  public void setRemarkColor(String remarkColor) {
    this.remarkColor = remarkColor;
  }

  public Map<String, Map<String, String>> getData() {
    return data;
  }

  public void setData(Map<String, Map<String, String>> data) {
    this.data = data;
  }
}
