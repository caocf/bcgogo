package com.bcgogo.wx.article;

/**
 *  发送客服消息素材
 * User: ndong
 * Date: 14-9-23
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public class CustomArticle {
  private String title;
  private String description;
  private String picurl;
  private String url;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPicurl() {
    return picurl;
  }

  public void setPicurl(String picurl) {
    this.picurl = picurl;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
