package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.notification.velocity.AppointVelocityContext;
import com.bcgogo.notification.velocity.ShopAdvertVelocityContext;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 14-04-11
 * Time: 下午6:00
 * To change this template use File | Settings | File Templates.
 */

public class AdvertDTO implements Serializable {

  private Long id;
  private String idStr;
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

  private String statusStr;
  private String editDateStr;
  private String beginDateStr;
  private String endDateStr;

  private String dateFormat = DateUtil.YEAR_MONTH_DATE;

  private YesNo containImage = YesNo.NO;//宣传描述里是否包含图片

  private String imageUrl;

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public ShopAdvertVelocityContext toShopAdvertVelocityContext() {
    ShopAdvertVelocityContext context = new ShopAdvertVelocityContext();
    context.setAdvertDateStr(this.getEditDateStr());
    return context;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if (id != null) {
      this.idStr = id.toString();
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;

    if (editDate != null) {
      this.setEditDateStr(DateUtil.convertDateLongToDateString(dateFormat, editDate));
    }
  }

  public Long getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Long publishDate) {
    this.publishDate = publishDate;
  }

  public Long getRepealDate() {
    return repealDate;
  }

  public void setRepealDate(Long repealDate) {
    this.repealDate = repealDate;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(Long beginDate) {
    this.beginDate = beginDate;

    if (beginDate != null) {
      this.setBeginDateStr(DateUtil.convertDateLongToDateString(dateFormat, beginDate));
    }
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;

    if (endDate != null) {
      this.setEndDateStr(DateUtil.convertDateLongToDateString(dateFormat, endDate));
    }
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public AdvertStatus getStatus() {
    return status;
  }

  public void setStatus(AdvertStatus status) {
    this.status = status;
    if (status != null) {
      this.setStatusStr(status.getName());
    }
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public String getBeginDateStr() {
    return beginDateStr;
  }

  public void setBeginDateStr(String beginDateStr) {
    this.beginDateStr = beginDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public YesNo getContainImage() {
    return containImage;
  }

  public void setContainImage(YesNo containImage) {
    this.containImage = containImage;
  }
}
