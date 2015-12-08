package com.bcgogo.txn.model.supplierComment;

import com.bcgogo.api.AppShopCommentDTO;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.supplierComment.CommentRecordType;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.supplierComment.AppUserCommentRecordDTO;
import com.bcgogo.txn.dto.supplierComment.CommentConstant;
import com.bcgogo.txn.dto.supplierComment.SupplierCommentRecordDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * bcgogo点评记录实体类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-13
 * Time: 下午4:52
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "comment_record")
public class CommentRecord extends LongIdentifier {
  //被评价者
  private Long commentTargetShopId;  //供应商店铺id supplierShopId 被评价的店铺id
  private Long commentTargetId;    //供应商id supplierId 被评价的对象id
  private String commentTarget;   //供应商姓名 supplier  被评价的对象
  private OperatorType commentTargetType;  //被评价的类型：店铺或者供应商

  //评价者
  private Long commentatorShopId;  //客户店铺id customerShopId 评价者shop_id
  private Long commentatorId;    //客户id customerId   评价者id
  private String commentator;   //客户姓名 customer  评价者名称
  private OperatorType commentatorType;  //客户  评价者类型

  //单据
  private Long orderId;    //采购单id  评价者和被评价者交易单据id
  private OrderTypes orderType; //评价者和被评价者交易单据类型
  private Long purchaseInventoryId;   //入库单id
  private Long salesOrderId;//供应商店铺销售单id

  private Long commentTime;   //评价时间
  private String commentContent;  //评价内容

  private Double qualityScore;  //质量分数
  private Double performanceScore; //性价比分数
  private Double speedScore; //发货速度分数
  private Double attitudeScore;   //服务态度分数
  private CommentStatus commentStatus;  //评论记录状态

  private CommentRecordType commentRecordType; //评价类型

  private Double commentScore;//手机端用户评价单据 只有一项 存放在这里

  private Long customerId;//每个单据对应的客户id
  private String receiptNo;//单据号

  public SupplierCommentRecordDTO toSupplierCommentRecordDTO() {
    SupplierCommentRecordDTO supplierCommentRecordDTO = new SupplierCommentRecordDTO();

    supplierCommentRecordDTO.setId(getId());

    supplierCommentRecordDTO.setSupplierShopId(getCommentTargetShopId());
    supplierCommentRecordDTO.setSupplierId(getCommentTargetId());
    supplierCommentRecordDTO.setSupplier(getCommentTarget());

    supplierCommentRecordDTO.setCustomerShopId(getCommentatorShopId());
    supplierCommentRecordDTO.setCustomerId(getCommentatorId());
    supplierCommentRecordDTO.setCustomer(getCommentator());

    supplierCommentRecordDTO.setPurchaseOrderId(getOrderId());
    supplierCommentRecordDTO.setPurchaseInventoryId(getPurchaseInventoryId());
    supplierCommentRecordDTO.setSalesOrderId(getSalesOrderId());
    supplierCommentRecordDTO.setCommentTime(getCommentTime());
    supplierCommentRecordDTO.setCommentContent(getCommentContent());
    supplierCommentRecordDTO.setQualityScore(getQualityScore());
    supplierCommentRecordDTO.setPerformanceScore(getPerformanceScore());
    supplierCommentRecordDTO.setSpeedScore(getSpeedScore());
    supplierCommentRecordDTO.setAttitudeScore(getAttitudeScore());
    supplierCommentRecordDTO.setCommentStatus(getCommentStatus());
    supplierCommentRecordDTO.setFirstCommentContent(getCommentContent());

    if (getCommentTime() != null) {
      supplierCommentRecordDTO.setCommentTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, getCommentTime()));
    }
    supplierCommentRecordDTO.setFirstCommentContent(supplierCommentRecordDTO.getFirstCommentContent());

    String commentContent = getCommentContent();
    String addContentFormat = CommentConstant.COMMENT_SPACE;

    if (StringUtil.isNotEmpty(commentContent) && commentContent.contains(addContentFormat)) {
      supplierCommentRecordDTO.setAddCommentContent(commentContent.substring(commentContent.indexOf(addContentFormat) + addContentFormat.length(), commentContent.length()));
      supplierCommentRecordDTO.setFirstCommentContent(commentContent.substring(0,commentContent.indexOf(addContentFormat)));
    }

    supplierCommentRecordDTO.setQualityScoreSpan(((5 - NumberUtil.doubleVal(supplierCommentRecordDTO.getQualityScore()) ) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
    supplierCommentRecordDTO.setPerformanceScoreSpan(((5 - NumberUtil.doubleVal(supplierCommentRecordDTO.getPerformanceScore())) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
    supplierCommentRecordDTO.setSpeedScoreSpan(((5 - NumberUtil.doubleVal(supplierCommentRecordDTO.getSpeedScore())) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
    supplierCommentRecordDTO.setAttitudeScoreSpan(((5 - NumberUtil.doubleVal(supplierCommentRecordDTO.getAttitudeScore())) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);


    return supplierCommentRecordDTO;
  }


  public AppUserCommentRecordDTO toAppUserCommentRecordDTO() {
    AppUserCommentRecordDTO appUserCommentRecordDTO = new AppUserCommentRecordDTO();

    appUserCommentRecordDTO.setId(getId());

    appUserCommentRecordDTO.setCommentTargetShopId(this.getCommentTargetShopId());
    appUserCommentRecordDTO.setCommentTargetId(this.getCommentTargetId());
    appUserCommentRecordDTO.setCommentTarget(this.getCommentTarget());

    appUserCommentRecordDTO.setCommentatorShopId(this.getCommentatorShopId());
    appUserCommentRecordDTO.setCommentatorId(this.getCommentatorId());
    appUserCommentRecordDTO.setCommentator(this.getCommentator());

    appUserCommentRecordDTO.setOrderId(getOrderId());
    appUserCommentRecordDTO.setOrderType(getOrderType());
    appUserCommentRecordDTO.setReceiptNo(getReceiptNo());
    appUserCommentRecordDTO.setCommentTime(getCommentTime());
    appUserCommentRecordDTO.setCommentContent(getCommentContent());
    appUserCommentRecordDTO.setCommentScore(getCommentScore());
    appUserCommentRecordDTO.setCommentStatus(getCommentStatus());
    appUserCommentRecordDTO.setFirstCommentContent(getCommentContent());

    appUserCommentRecordDTO.setCustomerId(getCustomerId());


    if (getCommentTime() != null) {
      appUserCommentRecordDTO.setCommentTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, getCommentTime()));
    }

    appUserCommentRecordDTO.setCommentContent(getCommentContent());

    return appUserCommentRecordDTO;
  }

  public CommentRecord fromSupplierCommentRecordDTO(SupplierCommentRecordDTO supplierCommentRecordDTO) {

    this.setCommentTargetShopId(supplierCommentRecordDTO.getSupplierShopId());
    this.setCommentTargetId(supplierCommentRecordDTO.getSupplierId());
    this.setCommentTarget(supplierCommentRecordDTO.getSupplier());

    this.setCommentatorShopId(supplierCommentRecordDTO.getCustomerShopId());
    this.setCommentatorId(supplierCommentRecordDTO.getCustomerId());
    this.setCommentator(supplierCommentRecordDTO.getCustomer());

    this.setOrderId(supplierCommentRecordDTO.getPurchaseOrderId());
    this.setPurchaseInventoryId(supplierCommentRecordDTO.getPurchaseInventoryId());
    this.setSalesOrderId(supplierCommentRecordDTO.getSalesOrderId());

    this.setCommentTime(supplierCommentRecordDTO.getCommentTime());
    this.setCommentContent(supplierCommentRecordDTO.getCommentContent());
    this.setQualityScore(supplierCommentRecordDTO.getQualityScore());
    this.setPerformanceScore(supplierCommentRecordDTO.getPerformanceScore());
    this.setSpeedScore(supplierCommentRecordDTO.getSpeedScore());
    this.setAttitudeScore(supplierCommentRecordDTO.getAttitudeScore());
    this.setCommentStatus(supplierCommentRecordDTO.getCommentStatus());

    //四个枚举值
    this.setCommentTargetType(OperatorType.SUPPLIER);
    this.setCommentatorType(OperatorType.CUSTOMER);
    this.setOrderType(OrderTypes.PURCHASE);
    this.setCommentRecordType(CommentRecordType.CUSTOMER_TO_SUPPLIER);
    this.setReceiptNo(supplierCommentRecordDTO.getReceiptNo());
    return this;
  }

  public CommentRecord fromAppUserCommentRecordDTO(AppUserCommentRecordDTO appUserCommentRecordDTO) {

     this.setCommentTargetShopId(appUserCommentRecordDTO.getCommentTargetShopId());
     this.setCommentTargetId(appUserCommentRecordDTO.getCommentTargetId());
     this.setCommentTarget(appUserCommentRecordDTO.getCommentTarget());

     this.setCommentatorShopId(appUserCommentRecordDTO.getCommentatorShopId());
     this.setCommentatorId(appUserCommentRecordDTO.getCommentatorId());
     this.setCommentator(appUserCommentRecordDTO.getCommentator());

     this.setOrderId(appUserCommentRecordDTO.getOrderId());
     this.setPurchaseInventoryId(null);
     this.setSalesOrderId(null);

     this.setCommentTime(appUserCommentRecordDTO.getCommentTime());
     this.setCommentContent(appUserCommentRecordDTO.getCommentContent());
     this.setCommentScore(appUserCommentRecordDTO.getCommentScore());
     this.setCommentStatus(appUserCommentRecordDTO.getCommentStatus());

     //四个枚举值
     this.setCommentTargetType(appUserCommentRecordDTO.getCommentTargetType());
     this.setCommentatorType(appUserCommentRecordDTO.getCommentatorType());
     this.setOrderType(appUserCommentRecordDTO.getOrderType());
     this.setCommentRecordType(appUserCommentRecordDTO.getCommentRecordType());

     this.setCustomerId(appUserCommentRecordDTO.getCustomerId());
     this.setReceiptNo(appUserCommentRecordDTO.getReceiptNo());
     return this;
   }

  public CommentRecord() {

  }

  public AppShopCommentDTO toAppShopCommentDTO() {
    AppShopCommentDTO appShopCommentDTO = new AppShopCommentDTO();
    appShopCommentDTO.setCommentScore(getCommentScore());
    appShopCommentDTO.setCommentContent(getCommentContent());
    appShopCommentDTO.setCommentatorName(getCommentator());
    appShopCommentDTO.setCommentTime(getCommentTime());
    return appShopCommentDTO;
  }

  @Column(name = "comment_target_shop_id")
  public Long getCommentTargetShopId() {
    return commentTargetShopId;
  }

  public void setCommentTargetShopId(Long commentTargetShopId) {
    this.commentTargetShopId = commentTargetShopId;
  }


  @Column(name = "purchase_inventory_id")
  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  @Column(name = "comment_time")
  public Long getCommentTime() {
    return commentTime;
  }

  public void setCommentTime(Long commentTime) {
    this.commentTime = commentTime;
  }

  @Column(name = "comment_content")
  public String getCommentContent() {
    return commentContent;
  }

  public void setCommentContent(String commentContent) {
    this.commentContent = commentContent;
  }

  @Column(name = "quality_score")
  public Double getQualityScore() {
    return qualityScore;
  }

  public void setQualityScore(Double qualityScore) {
    this.qualityScore = qualityScore;
  }

  @Column(name = "performance_score")
  public Double getPerformanceScore() {
    return performanceScore;
  }

  public void setPerformanceScore(Double performanceScore) {
    this.performanceScore = performanceScore;
  }

  @Column(name = "speed_score")
  public Double getSpeedScore() {
    return speedScore;
  }

  public void setSpeedScore(Double speedScore) {
    this.speedScore = speedScore;
  }

  @Column(name = "attitude_score")
  public Double getAttitudeScore() {
    return attitudeScore;
  }

  public void setAttitudeScore(Double attitudeScore) {
    this.attitudeScore = attitudeScore;
  }

  @Column(name = "comment_status")
  @Enumerated(EnumType.STRING)
  public CommentStatus getCommentStatus() {
    return commentStatus;
  }

  public void setCommentStatus(CommentStatus commentStatus) {
    this.commentStatus = commentStatus;
  }

  @Column(name = "sales_order_id")
  public Long getSalesOrderId() {
    return salesOrderId;
  }

  public void setSalesOrderId(Long salesOrderId) {
    this.salesOrderId = salesOrderId;
  }

  @Column(name = "comment_target_id")
  public Long getCommentTargetId() {
    return commentTargetId;
  }

  public void setCommentTargetId(Long commentTargetId) {
    this.commentTargetId = commentTargetId;
  }

  @Column(name = "comment_target")
  public String getCommentTarget() {
    return commentTarget;
  }

  public void setCommentTarget(String commentTarget) {
    this.commentTarget = commentTarget;
  }

  @Column(name = "comment_target_type")
  @Enumerated(EnumType.STRING)
  public OperatorType getCommentTargetType() {
    return commentTargetType;
  }

  public void setCommentTargetType(OperatorType commentTargetType) {
    this.commentTargetType = commentTargetType;
  }

  @Column(name = "commentator_shop_id")
  public Long getCommentatorShopId() {
    return commentatorShopId;
  }

  public void setCommentatorShopId(Long commentatorShopId) {
    this.commentatorShopId = commentatorShopId;
  }

  @Column(name = "commentator_id")
  public Long getCommentatorId() {
    return commentatorId;
  }

  public void setCommentatorId(Long commentatorId) {
    this.commentatorId = commentatorId;
  }

  @Column(name = "commentator")
  public String getCommentator() {
    return commentator;
  }

  public void setCommentator(String commentator) {
    this.commentator = commentator;
  }

  @Column(name = "commentator_type")
  @Enumerated(EnumType.STRING)
  public OperatorType getCommentatorType() {
    return commentatorType;
  }

  public void setCommentatorType(OperatorType commentatorType) {
    this.commentatorType = commentatorType;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name = "comment_record_type")
  @Enumerated(EnumType.STRING)
  public CommentRecordType getCommentRecordType() {
    return commentRecordType;
  }

  public void setCommentRecordType(CommentRecordType commentRecordType) {
    this.commentRecordType = commentRecordType;
  }

  @Column(name = "comment_score")
  public Double getCommentScore() {
    return commentScore;
  }

  public void setCommentScore(Double commentScore) {
    this.commentScore = commentScore;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }


}
