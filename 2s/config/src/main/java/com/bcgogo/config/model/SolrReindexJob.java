package com.bcgogo.config.model;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-19
 * Time: 上午10:50
 */

@Entity
@Table(name = "solr_reindex_job")
public class SolrReindexJob extends LongIdentifier {
  private Long shopId;
  private String reindexType;
  private Long batchId;
  private ExeStatus exeStatus;
  private OrderTypes orderType;
  private Date createTime;
  private Date startTime;
  private Date finishTime;
  private String executor;

  public SolrReindexJob() {
  }

  public SolrReindexJob(Long shopId, String reindexType, Long batchId, ExeStatus exeStatus, Date createTime, OrderTypes orderType) {
    this.shopId = shopId;
    this.reindexType = reindexType;
    this.batchId = batchId;
    this.exeStatus = exeStatus;
    this.createTime = createTime;
    this.orderType = orderType;
  }

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="reindex_type")
  public String getReindexType() {
    return reindexType;
  }

  public void setReindexType(String reindexType) {
    this.reindexType = reindexType;
  }

  @Column(name="batch_id")
  public Long getBatchId() {
    return batchId;
  }

  public void setBatchId(Long batchId) {
    this.batchId = batchId;
  }

  @Column(name="exe_status")
  @Enumerated(EnumType.STRING)
  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  @Column(name="order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name = "create_time")
  @Temporal(TemporalType.TIMESTAMP)
  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  @Column(name = "start_time")
  @Temporal(TemporalType.TIMESTAMP)
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @Column(name = "finish_time")
  @Temporal(TemporalType.TIMESTAMP)
  public Date getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Date finishTime) {
    this.finishTime = finishTime;
  }

  @Column(name="executor")
  public String getExecutor() {
    return executor;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }
}
