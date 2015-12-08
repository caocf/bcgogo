package com.bcgogo.client;


import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午1:32
 */
public class ClientAssortedMessage {
  private RecommendScene recommendScene;              //业务信息场景分类
  private String title;                       //标题
  private String relatedTitle;                //关联到shop系统连接标题
  private String relatedUrl;                  //关联到shop系统连接地址
  private List<AssortedMessageItem> items = new ArrayList<AssortedMessageItem>();    //消息项列表
  private Long nextRequestTime;               //下次请求时间，使用unix长整型时间格式
  private String feedbackUrl;                 //用户点击反馈连接
  private Integer msgNumber=0;                      //该消息数量

  public void feedbackUrlGenerator(String basePath, Long shopId, String userNo, String apiVersion, RecommendScene recommendScene, FeedbackType feedbackType) {
    String url = basePath;
    url += ClientConstant.FEEDBACK_URL;
    if (shopId != null) {
      url += "&shopId=" + shopId;
    }
    if (StringUtils.isNotBlank(userNo)) {
      url += "&userNo=" + userNo;
    }
    if (StringUtils.isNotBlank(apiVersion)) {
      url += "&apiVersion=" + apiVersion;
    }
    if (recommendScene != null) {
      url += "&recommendScene=" + recommendScene;
    }
    if (feedbackType != null) {
      url += "&feedbackType=" + feedbackType;
    }
    feedbackUrl = url;
  }

  public void addMsgNumber(Integer number) {
    msgNumber += number;
  }

  public RecommendScene getRecommendScene() {
    return recommendScene;
  }

  public void setRecommendScene(RecommendScene recommendScene) {
    this.recommendScene = recommendScene;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getRelatedTitle() {
    return relatedTitle;
  }

  public void setRelatedTitle(String relatedTitle) {
    this.relatedTitle = relatedTitle;
  }

  public String getRelatedUrl() {
    return relatedUrl;
  }

  public void setRelatedUrl(String relatedUrl) {
    this.relatedUrl = relatedUrl;
  }

  public List<AssortedMessageItem> getItems() {
    return items;
  }

  public void setItems(List<AssortedMessageItem> items) {
    this.items = items;
  }

  public Long getNextRequestTime() {
    return nextRequestTime;
  }

  public void setNextRequestTime(Long nextRequestTime) {
    this.nextRequestTime = nextRequestTime;
  }

  public String getFeedbackUrl() {
    return feedbackUrl;
  }

  public void setFeedbackUrl(String feedbackUrl) {
    this.feedbackUrl = feedbackUrl;
  }

  public Integer getMsgNumber() {
    return msgNumber;
  }

  public void setMsgNumber(Integer msgNumber) {
    this.msgNumber = msgNumber;
  }
}
