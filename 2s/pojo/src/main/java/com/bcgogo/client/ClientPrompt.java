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
 * Time: 下午1:33
 */
public class ClientPrompt {
  private RecommendScene recommendScene;              //推荐类型，反馈用户行为时使用
  private Long recommendId;                 //推荐记录ID，反馈用户行为时使用
  private String title;                       //标题
  private String content;                     //内容
  private String url;                         //关联到shop系统连接地址
  private String feedbackUrl;                 //消息点击反馈连接地址
  private Long nextRequestTime;               //下次请求时间，使用unix长整型时间格式
  private List<String> feedbackUrls = new ArrayList<String>();          //消息反馈连接地址，包含三个地址，依次为：用户点击后反馈连接；用户关闭/取消后反馈连接；用户未操作自动消失反馈连接

  public void addFeedbackUrl(String basePath, Long shopId, String userNo, String apiVersion, Long messageId, RecommendScene recommendScene, FeedbackType feedbackType) {
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
    if (messageId != null) {
      url += "&recommendId=" + messageId;
    }
    if (feedbackType != null) {
      url += "&feedbackType=" + feedbackType;
    }
    feedbackUrls.add(url);

  }

  public void setFeedbackUrl(String basePath, Long shopId, String userNo, String apiVersion, Long messageId, RecommendScene recommendScene, FeedbackType feedbackType) {
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
    if (messageId != null) {
      url += "&recommendId=" + messageId;
    }
    if (feedbackType != null) {
      url += "&feedbackType=" + feedbackType;
    }
    feedbackUrl = (url);
  }

  public RecommendScene getRecommendScene() {
    return recommendScene;
  }

  public void setRecommendScene(RecommendScene recommendScene) {
    this.recommendScene = recommendScene;
  }

  public Long getRecommendId() {
    return recommendId;
  }

  public void setRecommendId(Long recommendId) {
    this.recommendId = recommendId;
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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFeedbackUrl() {
    return feedbackUrl;
  }

  public void setFeedbackUrl(String feedbackUrl) {
    this.feedbackUrl = feedbackUrl;
  }

  public Long getNextRequestTime() {
    return nextRequestTime;
  }

  public void setNextRequestTime(Long nextRequestTime) {
    this.nextRequestTime = nextRequestTime;
  }

  public List<String> getFeedbackUrls() {
    return feedbackUrls;
  }

  public void setFeedbackUrls(List<String> feedbackUrls) {
    this.feedbackUrls = feedbackUrls;
  }
}
