package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * Time: 上午11:02
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "price_adjustment")
public class PriceAdjustment extends LongIdentifier {
  public PriceAdjustment() {
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "date")
  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  @Column(name = "no", length = 20)
  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "price")
  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "editor_id")
  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  @Column(name = "editor", length = 20)
  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "reviewer_id")
  public Long getReviewerId() {
    return reviewerId;
  }

  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  @Transient
  public String getReviewer() {
    return reviewer;
  }

  private void setReviewer(String reviewer) {
    this.reviewer = reviewer;
  }

  @Column(name = "review_date")
  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  @Column(name = "invalidator_id")
  public Long getInvalidatorId() {
    return invalidatorId;
  }

  public void setInvalidatorId(Long invalidatorId) {
    this.invalidatorId = invalidatorId;
  }

  @Transient
  public String getInvalidator() {
    return invalidator;
  }

  private void setInvalidator(String invalidator) {
    this.invalidator = invalidator;
  }

  @Column(name = "invalidate_date", length = 20)
  public String getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(String invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  private Long shopId;
  private Long date;
  private String no;
  private Long productId;
  private double price;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long reviewerId;
  private String reviewer;       //瞬态字段
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;   //瞬态字段
  private String invalidateDate;

  /**
   * 根据XXId找到相关信息并设置到XX字段.
   */
  @Override
  public void onLoad(Session s, Serializable id) {
//    if(reviewerId!=null){
//      setReviewer(ServiceManager.getService(IUserService.class).getNameByUserId(reviewerId));
//    }
  }
}