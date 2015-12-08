package com.bcgogo.notification.model;

import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午2:00
 * To change this template use File | Settings | File Templates.
 */

@MappedSuperclass
public class Reminder extends LongIdentifier{

  private Long shopId;
  private Long userId;
  private String title;
  private byte[]  content;
  private Long releaseDate;
  private Long releaseManId;
  private String releaseMan;
  private Long createDate;

  private ObjectStatus status;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  @Column(name = "title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "content")
  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  @Column(name = "create_date")
  public Long getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Long createDate) {
    this.createDate = createDate;
  }

  @Column(name = "release_date")
  public Long getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(Long releaseDate) {
    this.releaseDate = releaseDate;
  }

  @Column(name = "release_man_id")
  public Long getReleaseManId() {
    return releaseManId;
  }

  public void setReleaseManId(Long releaseManId) {
    this.releaseManId = releaseManId;
  }

  @Column(name = "release_man")
  public String getReleaseMan() {
    return releaseMan;
  }

  public void setReleaseMan(String releaseMan) {
    this.releaseMan = releaseMan;
  }

}
