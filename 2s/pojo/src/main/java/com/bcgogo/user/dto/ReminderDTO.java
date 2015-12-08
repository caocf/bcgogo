package com.bcgogo.user.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.common.Pager;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-3
 * Time: 上午9:05
 * To change this template use File | Settings | File Templates.
 */
public class ReminderDTO extends BaseDTO {
   private Long shopId;
  private String title;
  private String content;
  private String createDate;
  private String releaseDate;
  private Long releaseManId;
  private String releaseMan;
  private String status;
  private Pager pager;
  private Long startDate;
  private Long endDate;
  private int start;
  private int limit;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getReleaseManId() {
    return releaseManId;
  }

  public void setReleaseManId(Long releaseManId) {
    this.releaseManId = releaseManId;
  }

  public String getReleaseMan() {
    return releaseMan;
  }

  public void setReleaseMan(String releaseMan) {
    this.releaseMan = releaseMan;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
