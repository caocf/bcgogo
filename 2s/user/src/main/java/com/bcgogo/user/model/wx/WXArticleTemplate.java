package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.wx.WXArticleTemplateDTO;
import com.bcgogo.wx.message.WXArticleType;

import javax.persistence.*;

/**
 * 微信素材
 * User: ndong
 * Date: 14-9-22
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_article_template")
public class WXArticleTemplate extends LongIdentifier{
  private Long shopId= ShopConstant.BC_SHOP_ID;
  private String title;
  private String description;
  private String url;
  private String picUrl;
  private WXArticleType articleType;
//  private WXArticleCategory category;
  private DeletedType deleted=DeletedType.FALSE;
  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "pic_url")
  public String getPicUrl() {
    return picUrl;
  }

  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

  @Column(name = "article_type")
  @Enumerated(EnumType.STRING)
  public WXArticleType getArticleType() {
    return articleType;
  }

  public void setArticleType(WXArticleType articleType) {
    this.articleType = articleType;
  }

//  @Column(name = "category")
//  @Enumerated(EnumType.STRING)
//  public WXArticleCategory getCategory() {
//    return category;
//  }
//
//  public void setCategory(WXArticleCategory category) {
//    this.category = category;
//  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public WXArticleTemplateDTO toDTO() {
    WXArticleTemplateDTO wxArticleDTO = new WXArticleTemplateDTO();
    wxArticleDTO.setDescription(this.description);
    wxArticleDTO.setTitle(this.title);
    wxArticleDTO.setPicUrl(this.picUrl);
    wxArticleDTO.setUrl(this.url);
    wxArticleDTO.setId(this.getId());
    return wxArticleDTO;
  }
}
