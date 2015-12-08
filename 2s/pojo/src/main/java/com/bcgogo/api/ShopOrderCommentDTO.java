package com.bcgogo.api;

import com.bcgogo.txn.dto.supplierComment.CommentConstant;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * 手机端用户评价单据信息封装
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
public class ShopOrderCommentDTO implements Serializable {
  private Long orderId;//单据ID
//  private Integer qualityScore;//质量评分
//  private Integer attitudeScore;//态度评分
//  private Integer speedScore;//速度评分
//  private Integer performanceScore;//性价比评分
  private String commentContent;//评论
  private String userNo;

  private Integer commentScore;
  private Long customerId;//每个单据对应的客户id
  private String receiptNo;//单据号

  private static final int LOWEST_MARK = 0;
  private static final int FULL_MARK = 5;

  public String validate() {
    if (orderId == null) {
      return "单据不能为空";
    }
    if (NumberUtil.intValue(commentScore) <= LOWEST_MARK || NumberUtil.intValue(commentScore) > FULL_MARK) {
      return "评分不正确";
    }
//    if (NumberUtil.intValue(attitudeScore) <= LOWEST_MARK || NumberUtil.intValue(qualityScore) > FULL_MARK) {
//      return "态度评分不正确";
//    }
//    if (NumberUtil.intValue(speedScore) <= LOWEST_MARK || NumberUtil.intValue(qualityScore) > FULL_MARK) {
//      return "速度评分不正确";
//    }
//    if (NumberUtil.intValue(performanceScore) <= LOWEST_MARK || NumberUtil.intValue(qualityScore) > FULL_MARK) {
//      return "性价比评分不正确";
//    }

    if(StringUtil.isEmpty(userNo)){
      return "用户账号不能为空";
    }

    if(StringUtil.isNotEmpty(getCommentContent()) && getCommentContent().length() > CommentConstant.APP_USER_COMMENT_LENGTH){
      return "评价内容过长,请重新输入";
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

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Integer getCommentScore() {
    return commentScore;
  }

  public void setCommentScore(Integer commentScore) {
    this.commentScore = commentScore;
  }

  public String getCommentContent() {
    return commentContent;
  }

  public void setCommentContent(String commentContent) {
    this.commentContent = commentContent;
  }


  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Override
  public String toString() {
    return "ShopOrderCommentDTO{" +
        "orderId=" + orderId +
        ", commentContent='" + commentContent + '\'' +
        ", userNo='" + userNo + '\'' +
        ", commentScore=" + commentScore +
        ", customerId=" + customerId +
        ", receiptNo='" + receiptNo + '\'' +
        '}';
  }
}
