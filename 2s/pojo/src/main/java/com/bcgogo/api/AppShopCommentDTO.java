package com.bcgogo.api;

import com.bcgogo.utils.DateUtil;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-19
 * Time: 下午8:09
 */
public class AppShopCommentDTO {
  private String commentatorName;
  private Long commentTime;   //评价时间
  private String commentTimeStr;//评价时间
  private String commentContent;  //评价内容
  private Double commentScore;//评分

  public String getCommentatorName() {
    return commentatorName;
  }

  public void setCommentatorName(String commentatorName) {
    this.commentatorName = commentatorName;
  }

  public Long getCommentTime() {
    return commentTime;
  }

  public void setCommentTime(Long commentTime) {
    this.commentTime = commentTime;
    if(commentTime != null){
      setCommentTimeStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD,commentTime));
    }else {
      setCommentTimeStr("");
    }
  }

  public String getCommentTimeStr() {
    return commentTimeStr;
  }

  public void setCommentTimeStr(String commentTimeStr) {
    this.commentTimeStr = commentTimeStr;
  }

  public String getCommentContent() {
    return commentContent;
  }

  public void setCommentContent(String commentContent) {
    this.commentContent = commentContent;
  }

  public Double getCommentScore() {
    return commentScore;
  }

  public void setCommentScore(Double commentScore) {
    this.commentScore = commentScore;
  }

}
