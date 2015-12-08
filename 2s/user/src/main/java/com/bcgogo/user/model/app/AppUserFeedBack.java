package com.bcgogo.user.model.app;

import com.bcgogo.BooleanEnum;
import com.bcgogo.api.AppUserFeedbackDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "app_user_feed_back")
public class AppUserFeedBack extends LongIdentifier {
  private String appUserNo;//用户账号
  private String content;//反馈内容
  private Long feedBackTime;//反馈时间
  private BooleanEnum isHandle;//反馈是否处理
  private String mobile;


  public AppUserFeedBack() {
    super();
  }

  public AppUserFeedBack(AppUserFeedbackDTO appUserFeedbackDTO) {
    if (appUserFeedbackDTO != null) {
      this.setAppUserNo(appUserFeedbackDTO.getUserNo());
      this.setContent(appUserFeedbackDTO.getContent());
      this.setFeedBackTime(appUserFeedbackDTO.getFeedBackTime());
      this.setMobile(appUserFeedbackDTO.getMobile());
    }
  }


  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "feed_back_time")
  public Long getFeedBackTime() {
    return feedBackTime;
  }

  public void setFeedBackTime(Long feedBackTime) {
    this.feedBackTime = feedBackTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "is_handle")
  public BooleanEnum getHandle() {
    return isHandle;
  }

  public void setHandle(BooleanEnum handle) {
    isHandle = handle;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
