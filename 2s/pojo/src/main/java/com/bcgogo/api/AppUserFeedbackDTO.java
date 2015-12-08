package com.bcgogo.api;

import com.bcgogo.BooleanEnum;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * 用户反馈信息封装
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 上午10:34
 * To change this template use File | Settings | File Templates.
 */
public class AppUserFeedbackDTO implements Serializable {
  private String userNo;    //用户账号
  private String content;
  private String mobile;
  private Long feedBackTime;//反馈时间
  private BooleanEnum isHandle;//反馈是否处理
  private AppMobileInfo mobileInfo;


  public String validate() {
    if (StringUtil.isEmpty(userNo)) {
      return "用户账号不能为空";
    }
    if (StringUtil.isEmpty(content)) {
      return "请输入反馈建议";
    } else if (content.length() > 200) {
      return "意见最多200个字";
    }
    if (StringUtil.isNotEmpty(mobile) && mobile.length() > 20) {
      return "联系方式最多20个字";
    }
    if (userNo.equals(AppUserDTO.APP_GUEST) && mobileInfo != null) {
      content += "【" + mobileInfo.toString() + "】";
    }
    return "";
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }


  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Long getFeedBackTime() {
    return feedBackTime;
  }

  public void setFeedBackTime(Long feedBackTime) {
    this.feedBackTime = feedBackTime;
  }


  public BooleanEnum getHandle() {
    return isHandle;
  }

  public void setHandle(BooleanEnum handle) {
    isHandle = handle;
  }

  @Override
  public String toString() {
    return "AppUserFeedbackDTO{" +
        "userNo='" + userNo + '\'' +
        ", content='" + content + '\'' +
        ", mobile='" + mobile + '\'' +
        ", feedBackTime=" + feedBackTime +
        '}';
  }

  public AppMobileInfo getMobileInfo() {
    return mobileInfo;
  }

  public void setMobileInfo(AppMobileInfo mobileInfo) {
    this.mobileInfo = mobileInfo;
  }
}
