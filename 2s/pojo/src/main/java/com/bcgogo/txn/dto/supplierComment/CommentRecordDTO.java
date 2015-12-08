package com.bcgogo.txn.dto.supplierComment;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * bcgogo点评记录
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-23
 * Time: 下午3:53
 * To change this template use File | Settings | File Templates.
 */
public class CommentRecordDTO implements Serializable {
  private Long id;

  private Long commentTime;   //评价时间

  private String commentContent;  //评价内容

  private CommentStatus commentStatus;  //评论记录状态

  private String addCommentContent; //追加评价内容
  private String firstCommentContent;//第一次评论内容
  private String commentTimeStr;//评论时间

  //增加的搜索条件对应的需要的属性
  private String commentTimeStartStr;//单据开始时间
  private Long commentTimeStart;
  private String commentTimeEndStr; //单据结束时间
  private Long commentTimeEnd;
  private Double commentScore; //用户评价分数
  private String commentStr;//用户评价分数
  private String commentScoreStr;//用户评价分数
  private List<Double> commentScores;//把用户评价类型转换成list储存(有多个时)

  private String addGoodCommentScoreStr;//用户评价分数
  private List<Double> addGoodCommentScores;//把用户评价类型转换成list储存(有多个时)
  private String addMediumCommentScoreStr;//用户评价分数
  private List<Double> addMediumCommentScores;//把用户评价类型转换成list储存(有多个时)
  private String addBadCommentScoreStr;//用户评价分数
  private List<Double> addBadCommentScores;//把用户评价类型转换成list储存(有多个时)

  private String orderTypeStr; //交易单据类型
  private List<OrderTypes> orderTypes;//把交易单据类型转换成list存储（有多个时）
  private String customerName;//客户
  private Long customerId;//客户id
  private List<Long> customerIds;//通过客户名查到的客户id可能有多个
  private String receiptNo;//单据号
  private List<Long> orderIds;//依据单据号查询出来的orderId集合



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCommentTime() {
    return commentTime;
  }

  public void setCommentTime(Long commentTime) {
    this.commentTime = commentTime;
  }

  public String getCommentContent() {
    return commentContent;
  }

  public void setCommentContent(String commentContent) {
    this.commentContent = commentContent;
  }

  public CommentStatus getCommentStatus() {
    return commentStatus;
  }

  public void setCommentStatus(CommentStatus commentStatus) {
    this.commentStatus = commentStatus;
  }

  public String getAddCommentContent() {
    return addCommentContent;
  }

  public void setAddCommentContent(String addCommentContent) {
    this.addCommentContent = addCommentContent;
  }

  public String getFirstCommentContent() {
    return firstCommentContent;
  }

  public void setFirstCommentContent(String firstCommentContent) {
    this.firstCommentContent = firstCommentContent;
  }

  public String getCommentTimeStr() {
    return commentTimeStr;
  }

  public void setCommentTimeStr(String commentTimeStr) {
    this.commentTimeStr = commentTimeStr;
  }

  public String getCommentTimeStartStr() {
    return commentTimeStartStr;
  }

  public void setCommentTimeStartStr(String commentTimeStartStr) {
    this.commentTimeStartStr = commentTimeStartStr;
  }

  public Long getCommentTimeStart() {
    return commentTimeStart;
  }

  public void setCommentTimeStart(Long commentTimeStart) {
    this.commentTimeStart = commentTimeStart;
  }

  public String getCommentTimeEndStr() {
    return commentTimeEndStr;
  }

  public void setCommentTimeEndStr(String commentTimeEndStr) {
    this.commentTimeEndStr = commentTimeEndStr;
  }

  public Long getCommentTimeEnd() {
    return commentTimeEnd;
  }

  public void setCommentTimeEnd(Long commentTimeEnd) {
    this.commentTimeEnd = commentTimeEnd;
  }

  public Double getCommentScore() {
    return commentScore;
  }

  public void setCommentScore(Double commentScore) {
    this.commentScore = commentScore;
    setCommentStr(commentScore);
  }

  public String getCommentStr() {
    return commentStr;
  }

  public void setCommentStr(Double commentScore) {
     if(commentScore==null) return;
    if(commentScore<=2){
      this.commentStr="差评";
    }else if(commentScore==3){
      this.commentStr="中评";
    }else{
      this.commentStr="好评";
    }
  }



  public String getCommentScoreStr() {
    return commentScoreStr;
  }

  public void setCommentScoreStr(String commentScoreStr) {
    this.commentScoreStr = commentScoreStr;
  }

  public List<Double> getCommentScores() {
    return commentScores;
  }

  public void setCommentScores(List<Double> commentScores) {
    this.commentScores = commentScores;
  }

  public String getAddGoodCommentScoreStr() {
    return addGoodCommentScoreStr;
  }

  public void setAddGoodCommentScoreStr(String addGoodCommentScoreStr) {
    this.addGoodCommentScoreStr = addGoodCommentScoreStr;
  }

  public List<Double> getAddGoodCommentScores() {
    return addGoodCommentScores;
  }

  public void setAddGoodCommentScores(List<Double> addGoodCommentScores) {
    this.addGoodCommentScores = addGoodCommentScores;
  }

  public String getAddMediumCommentScoreStr() {
    return addMediumCommentScoreStr;
  }

  public void setAddMediumCommentScoreStr(String addMediumCommentScoreStr) {
    this.addMediumCommentScoreStr = addMediumCommentScoreStr;
  }

  public List<Double> getAddMediumCommentScores() {
    return addMediumCommentScores;
  }

  public void setAddMediumCommentScores(List<Double> addMediumCommentScores) {
    this.addMediumCommentScores = addMediumCommentScores;
  }

  public String getAddBadCommentScoreStr() {
    return addBadCommentScoreStr;
  }

  public void setAddBadCommentScoreStr(String addBadCommentScoreStr) {
    this.addBadCommentScoreStr = addBadCommentScoreStr;
  }

  public List<Double> getAddBadCommentScores() {
    return addBadCommentScores;
  }

  public void setAddBadCommentScores(List<Double> addBadCommentScores) {
    this.addBadCommentScores = addBadCommentScores;
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  public List<OrderTypes> getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(List<OrderTypes> orderTypes) {
    this.orderTypes = orderTypes;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public List<Long> getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(List<Long> customerIds) {
    this.customerIds = customerIds;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public List<Long> getOrderIds() {
    return orderIds;
  }

  public void setOrderIds(List<Long> orderIds) {
    this.orderIds = orderIds;
  }

  public void initSearchTime() {
    Long startTime, endTime;
    try {
      startTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getCommentTimeStartStr());
    } catch (Exception e) {
      startTime = null;
      this.setCommentTimeStartStr(null);
    }
    try {
      endTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getCommentTimeEndStr());
    } catch (Exception e) {
      endTime = null;
      this.setCommentTimeEndStr(null);
    }
    if (startTime != null && endTime != null) {
      if (startTime > endTime) {
        Long temp = endTime;
        endTime = startTime;
        startTime = temp;
      }
    }
    if (endTime != null) {
      endTime = DateUtil.getInnerDayTime(endTime, 1);
    }
    this.setCommentTimeStart(startTime);
    this.setCommentTimeEnd(endTime);
  }

  public void StringsToDoubles(List<String> ls){
    List<Double> ld=new ArrayList<Double>();
    for(int i=0;i<ls.size();i++){
      ld.add(Double.parseDouble(ls.get(i)));
    }
      this.setCommentScores(ld);
  }

  public void StringsToOrderTypes(List<String> ls){
    List<OrderTypes> lo=new ArrayList<OrderTypes>();
    for(int i=0;i<ls.size();i++){
      if(ls.get(i).trim().equals("REPAIR")){
        lo.add(OrderTypes.REPAIR);
      }
      if(ls.get(i).trim().equals("WASH_BEAUTY")){
        lo.add(OrderTypes.WASH_BEAUTY);
      }
    }
    this.setOrderTypes(lo);
  }
}
