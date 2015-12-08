package com.bcgogo.remind.dto.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-28
 * Time: 上午11:08
 */
public class MessageNumberResult {
  private Map<String,String> request = new HashMap<String, String>();
  private Map<String,String> notification = new HashMap<String, String>();
  private Map<String,String> news = new HashMap<String, String>();
  private Integer noticeTotalNumber;

  public Map<String, String> getRequest() {
    return request;
  }

  public void setRequest(Map<String, String> request) {
    this.request = request;
  }

  public Map<String, String> getNotification() {
    return notification;
  }

  public void setNotification(Map<String, String> notification) {
    this.notification = notification;
  }

  public Map<String, String> getNews() {
    return news;
  }

  public void setNews(Map<String, String> news) {
    this.news = news;
  }

  public Integer getNoticeTotalNumber() {
    return noticeTotalNumber;
  }

  public void setNoticeTotalNumber(Integer noticeTotalNumber) {
    this.noticeTotalNumber = noticeTotalNumber;
  }
}
