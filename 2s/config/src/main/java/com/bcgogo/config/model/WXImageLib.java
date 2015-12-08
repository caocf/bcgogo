package com.bcgogo.config.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-11
 * Time: 下午12:31
 * To change this template use File | Settings | File Templates.
 */
  @Entity
@Table(name = "wx_image_lib")
public class WXImageLib extends LongIdentifier{
  private String name;
  //原始url
  private String url;
  //转化后的短url
  private String shortUrl;
  private DeletedType deleted=DeletedType.FALSE;

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "short_url")
  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
