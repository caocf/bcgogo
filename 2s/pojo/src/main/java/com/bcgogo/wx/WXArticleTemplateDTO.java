package com.bcgogo.wx;

import com.bcgogo.wx.article.CustomArticle;
import com.bcgogo.wx.message.WXArticleType;

/**
 * 图文消息
 * User: ndong
 * Date: 14-8-13
 * Time: 上午9:32
 * To change this template use File | Settings | File Templates.
 */
public class WXArticleTemplateDTO {


  // 图文消息名称
  private String title;
  // 图文消息描述
  private String description;
  // 图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80，限制图片链接的域名需要与开发者填写的基本资料中的Url一致
  private String picUrl;
  // 点击图文消息跳转链接
  private String url;
  private WXArticleType articleType;

  private Long id;
  private String idStr;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(id.toString());
    }else {
      setIdStr("");
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

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

  public String getPicUrl() {
    return picUrl;
  }

  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public WXArticleType getArticleType() {
    return articleType;
  }

  public void setArticleType(WXArticleType articleType) {
    this.articleType = articleType;
  }
}
