package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.AllocateRecordDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "advert")
public class Advert extends LongIdentifier {
  private Long shopId;
  private Long editDate;
  private Long publishDate;
  private Long repealDate;
  private String title;
  private Long beginDate;
  private Long endDate;
  private String description;
  private Long userId;
  private String user;
  private AdvertStatus status;

  private YesNo containImage;//宣传描述里是否包含图片

  public Advert(){

  }

  public Advert(AdvertDTO advertDTO) {
    this.setShopId(advertDTO.getShopId());
    this.setEditDate(advertDTO.getEditDate());
    this.setPublishDate(advertDTO.getPublishDate());
    this.setRepealDate(advertDTO.getRepealDate());
    this.setTitle(advertDTO.getTitle());
    this.setBeginDate(advertDTO.getBeginDate());
    this.setEndDate(advertDTO.getEndDate());
    this.setDescription(advertDTO.getDescription());
    this.setUserId(advertDTO.getUserId());
    this.setUser(advertDTO.getUser());
    this.setStatus(advertDTO.getStatus());

    this.setContainImage(advertDTO.getContainImage());
  }

  public Advert fromDTO(AdvertDTO advertDTO) {
    this.setShopId(advertDTO.getShopId());
    this.setEditDate(advertDTO.getEditDate());
    this.setPublishDate(advertDTO.getPublishDate());
    this.setRepealDate(advertDTO.getRepealDate());
    this.setTitle(advertDTO.getTitle());
    this.setBeginDate(advertDTO.getBeginDate());
    this.setEndDate(advertDTO.getEndDate());
    this.setDescription(advertDTO.getDescription());
    this.setUserId(advertDTO.getUserId());
    this.setUser(advertDTO.getUser());
    this.setStatus(advertDTO.getStatus());
    this.setContainImage(advertDTO.getContainImage());
    return this;
  }


  public AdvertDTO toDTO() {
    AdvertDTO advertDTO = new AdvertDTO();
    advertDTO.setId(getId());
    advertDTO.setShopId(this.getShopId());
    advertDTO.setEditDate(this.getEditDate());
    advertDTO.setPublishDate(this.getPublishDate());
    advertDTO.setRepealDate(this.getRepealDate());
    advertDTO.setTitle(this.getTitle());
    advertDTO.setBeginDate(this.getBeginDate());
    advertDTO.setEndDate(this.getEndDate());
    advertDTO.setDescription(this.getDescription());
    advertDTO.setUserId(this.getUserId());
    advertDTO.setUser(this.getUser());
    advertDTO.setStatus(this.getStatus());
    advertDTO.setContainImage(this.getContainImage());
    return advertDTO;
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "publish_date")
  public Long getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Long publishDate) {
    this.publishDate = publishDate;
  }

  @Column(name = "repeal_date")
  public Long getRepealDate() {
    return repealDate;
  }

  public void setRepealDate(Long repealDate) {
    this.repealDate = repealDate;
  }

  @Column(name = "title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "begin_date")
  public Long getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(Long beginDate) {
    this.beginDate = beginDate;
  }

  @Column(name = "end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "user")
  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public AdvertStatus getStatus() {
    return status;
  }

  public void setStatus(AdvertStatus status) {
    this.status = status;
  }

  @Column(name = "contain_image")
  @Enumerated(EnumType.STRING)
  public YesNo getContainImage() {
    return containImage;
  }

  public void setContainImage(YesNo containImage) {
    this.containImage = containImage;
  }
}
