package com.bcgogo.txn.dto.supplierComment;


import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.ShopOrderCommentDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.supplierComment.CommentRecordType;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;

import java.io.Serializable;

/**
 * 手机端点评记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-13
 * Time: 下午5:05
 * To change this template use File | Settings | File Templates.
 */
public class AppUserCommentRecordDTO extends CommentRecordDTO {

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
  private String orderIdStr;
  private OrderTypes orderType; //评价者和被评价者交易单据类型
  private String receiptNo;    //评价者和被评价者交易单据号
  private CommentRecordType commentRecordType; //评价类型

  private String vechicle;//客户对应的车牌号
  private String customerName;//单据对应的客户姓名
  private Long customerId;//单据对应的客户id
  private String mobile;//单据对应的客户手机号

  public AppUserCommentRecordDTO() {

  }

  public ShopOrderCommentDTO toShopOrderCommentDTO() {
    ShopOrderCommentDTO shopOrderCommentDTO = new ShopOrderCommentDTO();
    shopOrderCommentDTO.setCommentScore(getCommentScore().intValue());
    shopOrderCommentDTO.setCommentContent(getCommentContent());

    return shopOrderCommentDTO;
  }

  public AppUserCommentRecordDTO(ShopOrderCommentDTO shopOrderCommentDTO, ShopDTO shopDTO, AppUserDTO appUserDTO, Long orderId, OrderTypes orderType) {
    this.setCommentTargetShopId(shopDTO.getId());
    this.setCommentTargetId(shopDTO.getId());
    this.setCommentTarget(shopDTO.getName());
    this.setCommentTargetType(OperatorType.SHOP);

    this.setCommentatorShopId(null);
    this.setCommentatorId(appUserDTO.getId());
    this.setCommentator(appUserDTO.getName());
    this.setCommentatorType(OperatorType.APP_USER);

    this.setOrderType(orderType);
    this.setOrderId(orderId);

    this.setCommentRecordType(CommentRecordType.APP_TO_SHOP);
    this.setCommentTime(System.currentTimeMillis());
    this.setCommentStatus(CommentStatus.UN_STAT);

    this.setCommentContent(shopOrderCommentDTO.getCommentContent());
    this.setCommentScore(shopOrderCommentDTO.getCommentScore().doubleValue());
    this.setReceiptNo(shopOrderCommentDTO.getReceiptNo());
    this.setCustomerId(shopOrderCommentDTO.getCustomerId());
  }

  public Long getCommentTargetShopId() {
    return commentTargetShopId;
  }

  public void setCommentTargetShopId(Long commentTargetShopId) {
    this.commentTargetShopId = commentTargetShopId;
  }

  public Long getCommentTargetId() {
    return commentTargetId;
  }

  public void setCommentTargetId(Long commentTargetId) {
    this.commentTargetId = commentTargetId;
  }

  public String getCommentTarget() {
    return commentTarget;
  }

  public void setCommentTarget(String commentTarget) {
    this.commentTarget = commentTarget;
  }

  public OperatorType getCommentTargetType() {
    return commentTargetType;
  }

  public void setCommentTargetType(OperatorType commentTargetType) {
    this.commentTargetType = commentTargetType;
  }

  public Long getCommentatorShopId() {
    return commentatorShopId;
  }

  public void setCommentatorShopId(Long commentatorShopId) {
    this.commentatorShopId = commentatorShopId;
  }

  public Long getCommentatorId() {
    return commentatorId;
  }

  public void setCommentatorId(Long commentatorId) {
    this.commentatorId = commentatorId;
  }

  public String getCommentator() {
    return commentator;
  }

  public void setCommentator(String commentator) {
    this.commentator = commentator;
  }

  public OperatorType getCommentatorType() {
    return commentatorType;
  }

  public void setCommentatorType(OperatorType commentatorType) {
    this.commentatorType = commentatorType;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
    if(orderId != null) {
      setOrderIdStr(String.valueOf(orderId));
    }
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public CommentRecordType getCommentRecordType() {
    return commentRecordType;
  }

  public void setCommentRecordType(CommentRecordType commentRecordType) {
    this.commentRecordType = commentRecordType;
  }



  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public String getVechicle() {
    return vechicle;
  }

  public void setVechicle(String vechicle) {
    this.vechicle = vechicle;
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

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
