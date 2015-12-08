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
public class AssortedMessageItem {
  private String content;                     //消息内容
  private String url;                         //消息连接地址
  private List<String> feedbackUrls = new ArrayList<String>();          //消息反馈连接地址，包含三个地址，依次为：用户点击后反馈连接；用户关闭/取消后反馈连接；用户未操作自动消失反馈连接

  public void addFeedbackUrl(String basePath, Long shopId, String userNo, String apiVersion, Long messageId, RecommendScene recommendScene, FeedbackType feedbackType) {
    String url = basePath;
    url += ClientConstant.FEEDBACK_URL ;
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

  public List<String> getFeedbackUrls() {
    return feedbackUrls;
  }

  public void setFeedbackUrls(List<String> feedbackUrls) {
    this.feedbackUrls = feedbackUrls;
  }
}
