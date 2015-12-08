package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "category_item_relation")
public class CategoryItemRelation extends LongIdentifier{
  private Long categoryId;
  private Long serviceId;

  @Column(name = "category_id")
  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public CategoryItemRelation(Long categoryId, Long serviceId) {
    this.categoryId = categoryId;
    this.serviceId = serviceId;
  }
  public CategoryItemRelation() {
  }
}
