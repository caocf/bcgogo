package com.bcgogo.txn.model.supplierComment;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 供应商点评统计表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-13
 * Time: 下午4:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "comment_stat")
public class CommentStat extends LongIdentifier {
  private Long shopId;       //店铺id

  private Long orderAmount; //供应商在线交易笔数
  private Long recordAmount; //有评价的交易笔数
  private Double totalScore; //有评价的交易平分总和
  private Long statTime;//最后一次统计时间

  private Double qualityTotalScore; //商品质量平分总和
  private Long qualityFiveAmount;  //货品质量5分笔数
  private Long qualityFourAmount;  //货品质量4分笔数
  private Long qualityThreeAmount; //货品质量3分笔数
  private Long qualityTwoAmount;  //货品质量2分笔数
  private Long qualityOneAmount;  //货品质量1分笔数

  private Double performanceTotalScore;
  private Long performanceFiveAmount;
  private Long performanceFourAmount;
  private Long performanceThreeAmount;
  private Long performanceTwoAmount;
  private Long performanceOneAmount;

  private Double speedTotalScore;
  private Long speedFiveAmount;
  private Long speedFourAmount;
  private Long speedThreeAmount;
  private Long speedTwoAmount;
  private Long speedOneAmount;

  private Double attitudeTotalScore;
  private Long attitudeFiveAmount;
  private Long attitudeFourAmount;
  private Long attitudeThreeAmount;
  private Long attitudeTwoAmount;
  private Long attitudeOneAmount;

  private Double commentTotalScore;  //手机端评价就一项 存在这里
  private Long commentFiveAmount;  //手机端评价五分数量
  private Long commentFourAmount; //手机端评价4分数量
  private Long commentThreeAmount; //手机端评价3分数量
  private Long commentTwoAmount; //手机端评价2分数量
  private Long commentOneAmount; //手机端评价1分数量



  public CommentStatDTO toDTO() {
    CommentStatDTO supplierCommentDTO = new CommentStatDTO();
    supplierCommentDTO.setId(getId());
    supplierCommentDTO.setShopId(getShopId());
    supplierCommentDTO.setStatTime(getStatTime());

    supplierCommentDTO.setOrderAmount(getOrderAmount());
    supplierCommentDTO.setRecordAmount(getRecordAmount());
    supplierCommentDTO.setTotalScore(getTotalScore());

    supplierCommentDTO.setQualityTotalScore(getQualityTotalScore());
    supplierCommentDTO.setQualityFiveAmount(getQualityFiveAmount());
    supplierCommentDTO.setQualityFourAmount(getQualityFourAmount());
    supplierCommentDTO.setQualityThreeAmount(getQualityThreeAmount());
    supplierCommentDTO.setQualityTwoAmount(getQualityTwoAmount());
    supplierCommentDTO.setQualityOneAmount(getQualityOneAmount());

    supplierCommentDTO.setPerformanceTotalScore(getPerformanceTotalScore());
    supplierCommentDTO.setPerformanceFiveAmount(getPerformanceFiveAmount());
    supplierCommentDTO.setPerformanceFourAmount(getPerformanceFourAmount());
    supplierCommentDTO.setPerformanceThreeAmount(getPerformanceThreeAmount());
    supplierCommentDTO.setPerformanceTwoAmount(getPerformanceTwoAmount());
    supplierCommentDTO.setPerformanceOneAmount(getPerformanceOneAmount());

    supplierCommentDTO.setSpeedTotalScore(getSpeedTotalScore());
    supplierCommentDTO.setSpeedFiveAmount(getSpeedFiveAmount());
    supplierCommentDTO.setSpeedFourAmount(getSpeedFourAmount());
    supplierCommentDTO.setSpeedThreeAmount(getSpeedThreeAmount());
    supplierCommentDTO.setSpeedTwoAmount(getSpeedTwoAmount());
    supplierCommentDTO.setSpeedOneAmount(getSpeedOneAmount());


    supplierCommentDTO.setAttitudeTotalScore(getAttitudeTotalScore());
    supplierCommentDTO.setAttitudeFiveAmount(getAttitudeFiveAmount());
    supplierCommentDTO.setAttitudeFourAmount(getAttitudeFourAmount());
    supplierCommentDTO.setAttitudeThreeAmount(getAttitudeThreeAmount());
    supplierCommentDTO.setAttitudeTwoAmount(getAttitudeTwoAmount());
    supplierCommentDTO.setAttitudeOneAmount(getAttitudeOneAmount());

    supplierCommentDTO.setCommentTotalScore(NumberUtil.doubleVal(this.getCommentTotalScore()));
    supplierCommentDTO.setCommentFiveAmount(NumberUtil.longValue(this.getCommentFiveAmount()));
    supplierCommentDTO.setCommentFourAmount(NumberUtil.longValue(this.getCommentFourAmount()));
    supplierCommentDTO.setCommentThreeAmount(NumberUtil.longValue(this.getCommentThreeAmount()));
    supplierCommentDTO.setCommentTwoAmount(NumberUtil.longValue(this.getCommentTwoAmount()));
    supplierCommentDTO.setCommentOneAmount(NumberUtil.longValue(this.getCommentOneAmount()));

    return supplierCommentDTO;
  }

  public CommentStat fromDTO(CommentStatDTO supplierCommentDTO) {
    this.setId(supplierCommentDTO.getId());
    this.setShopId(supplierCommentDTO.getShopId());
    this.setStatTime(supplierCommentDTO.getStatTime());

    this.setOrderAmount(supplierCommentDTO.getOrderAmount());
    this.setRecordAmount(supplierCommentDTO.getRecordAmount());
    this.setTotalScore(supplierCommentDTO.getTotalScore());

    this.setQualityTotalScore(supplierCommentDTO.getQualityTotalScore());
    this.setQualityFiveAmount(supplierCommentDTO.getQualityFiveAmount());
    this.setQualityFourAmount(supplierCommentDTO.getQualityFourAmount());
    this.setQualityThreeAmount(supplierCommentDTO.getQualityThreeAmount());
    this.setQualityTwoAmount(supplierCommentDTO.getQualityTwoAmount());
    this.setQualityOneAmount(supplierCommentDTO.getQualityOneAmount());

    this.setPerformanceTotalScore(supplierCommentDTO.getPerformanceTotalScore());
    this.setPerformanceFiveAmount(supplierCommentDTO.getPerformanceFiveAmount());
    this.setPerformanceFourAmount(supplierCommentDTO.getPerformanceFourAmount());
    this.setPerformanceThreeAmount(supplierCommentDTO.getPerformanceThreeAmount());
    this.setPerformanceTwoAmount(supplierCommentDTO.getPerformanceTwoAmount());
    this.setPerformanceOneAmount(supplierCommentDTO.getPerformanceOneAmount());

    this.setSpeedTotalScore(supplierCommentDTO.getSpeedTotalScore());
    this.setSpeedFiveAmount(supplierCommentDTO.getSpeedFiveAmount());
    this.setSpeedFourAmount(supplierCommentDTO.getSpeedFourAmount());
    this.setSpeedThreeAmount(supplierCommentDTO.getSpeedThreeAmount());
    this.setSpeedTwoAmount(supplierCommentDTO.getSpeedTwoAmount());
    this.setSpeedOneAmount(supplierCommentDTO.getSpeedOneAmount());

    this.setAttitudeTotalScore(supplierCommentDTO.getAttitudeTotalScore());
    this.setAttitudeFiveAmount(supplierCommentDTO.getAttitudeFiveAmount());
    this.setAttitudeFourAmount(supplierCommentDTO.getAttitudeFourAmount());
    this.setAttitudeThreeAmount(supplierCommentDTO.getAttitudeThreeAmount());
    this.setAttitudeTwoAmount(supplierCommentDTO.getAttitudeTwoAmount());
    this.setAttitudeOneAmount(supplierCommentDTO.getAttitudeOneAmount());

    this.setCommentTotalScore(supplierCommentDTO.getCommentTotalScore());
    this.setCommentFiveAmount(supplierCommentDTO.getCommentFiveAmount());
    this.setCommentFourAmount(supplierCommentDTO.getCommentFourAmount());
    this.setCommentThreeAmount(supplierCommentDTO.getCommentThreeAmount());
    this.setCommentTwoAmount(supplierCommentDTO.getCommentTwoAmount());
    this.setCommentOneAmount(supplierCommentDTO.getCommentOneAmount());

    return this;
  }


  public CommentStat() {

  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "order_amount")
  public Long getOrderAmount() {
    return orderAmount;
  }

  public void setOrderAmount(Long orderAmount) {
    this.orderAmount = orderAmount;
  }

  @Column(name = "record_amount")
  public Long getRecordAmount() {
    return recordAmount;
  }

  public void setRecordAmount(Long recordAmount) {
    this.recordAmount = recordAmount;
  }

  @Column(name = "total_score")
  public Double getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Double totalScore) {
    this.totalScore = totalScore;
  }

  @Column(name = "quality_total_score")
  public Double getQualityTotalScore() {
    return qualityTotalScore;
  }

  public void setQualityTotalScore(Double qualityTotalScore) {
    this.qualityTotalScore = qualityTotalScore;
  }

  @Column(name = "quality_five_amount")
  public Long getQualityFiveAmount() {
    return qualityFiveAmount;
  }

  public void setQualityFiveAmount(Long qualityFiveAmount) {
    this.qualityFiveAmount = qualityFiveAmount;
  }

  @Column(name = "quality_four_amount")
  public Long getQualityFourAmount() {
    return qualityFourAmount;
  }

  public void setQualityFourAmount(Long qualityFourAmount) {
    this.qualityFourAmount = qualityFourAmount;
  }

  @Column(name = "quality_three_amount")
  public Long getQualityThreeAmount() {
    return qualityThreeAmount;
  }

  public void setQualityThreeAmount(Long qualityThreeAmount) {
    this.qualityThreeAmount = qualityThreeAmount;
  }

  @Column(name = "quality_two_amount")
  public Long getQualityTwoAmount() {
    return qualityTwoAmount;
  }

  public void setQualityTwoAmount(Long qualityTwoAmount) {
    this.qualityTwoAmount = qualityTwoAmount;
  }

  @Column(name = "quality_one_amount")
  public Long getQualityOneAmount() {
    return qualityOneAmount;
  }

  public void setQualityOneAmount(Long qualityOneAmount) {
    this.qualityOneAmount = qualityOneAmount;
  }

  @Column(name = "performance_total_score")
  public Double getPerformanceTotalScore() {
    return performanceTotalScore;
  }

  public void setPerformanceTotalScore(Double performanceTotalScore) {
    this.performanceTotalScore = performanceTotalScore;
  }

  @Column(name = "performance_five_amount")
  public Long getPerformanceFiveAmount() {
    return performanceFiveAmount;
  }

  public void setPerformanceFiveAmount(Long performanceFiveAmount) {
    this.performanceFiveAmount = performanceFiveAmount;
  }

  @Column(name = "performance_four_amount")
  public Long getPerformanceFourAmount() {
    return performanceFourAmount;
  }

  public void setPerformanceFourAmount(Long performanceFourAmount) {
    this.performanceFourAmount = performanceFourAmount;
  }

  @Column(name = "performance_three_amount")
  public Long getPerformanceThreeAmount() {
    return performanceThreeAmount;
  }

  public void setPerformanceThreeAmount(Long performanceThreeAmount) {
    this.performanceThreeAmount = performanceThreeAmount;
  }

  @Column(name = "performance_two_amount")
  public Long getPerformanceTwoAmount() {
    return performanceTwoAmount;
  }

  public void setPerformanceTwoAmount(Long performanceTwoAmount) {
    this.performanceTwoAmount = performanceTwoAmount;
  }

  @Column(name = "performance_one_amount")
  public Long getPerformanceOneAmount() {
    return performanceOneAmount;
  }

  public void setPerformanceOneAmount(Long performanceOneAmount) {
    this.performanceOneAmount = performanceOneAmount;
  }

  @Column(name = "speed_total_score")
  public Double getSpeedTotalScore() {
    return speedTotalScore;
  }

  public void setSpeedTotalScore(Double speedTotalScore) {
    this.speedTotalScore = speedTotalScore;
  }

  @Column(name = "speed_five_amount")
  public Long getSpeedFiveAmount() {
    return speedFiveAmount;
  }

  public void setSpeedFiveAmount(Long speedFiveAmount) {
    this.speedFiveAmount = speedFiveAmount;
  }

  @Column(name = "speed_four_amount")
  public Long getSpeedFourAmount() {
    return speedFourAmount;
  }

  public void setSpeedFourAmount(Long speedFourAmount) {
    this.speedFourAmount = speedFourAmount;
  }

  @Column(name = "speed_three_amount")
  public Long getSpeedThreeAmount() {
    return speedThreeAmount;
  }

  public void setSpeedThreeAmount(Long speedThreeAmount) {
    this.speedThreeAmount = speedThreeAmount;
  }

  @Column(name = "speed_two_amount")
  public Long getSpeedTwoAmount() {
    return speedTwoAmount;
  }

  public void setSpeedTwoAmount(Long speedTwoAmount) {
    this.speedTwoAmount = speedTwoAmount;
  }

  @Column(name = "speed_one_amount")
  public Long getSpeedOneAmount() {
    return speedOneAmount;
  }

  public void setSpeedOneAmount(Long speedOneAmount) {
    this.speedOneAmount = speedOneAmount;
  }

  @Column(name = "attitude_total_score")
  public Double getAttitudeTotalScore() {
    return attitudeTotalScore;
  }

  public void setAttitudeTotalScore(Double attitudeTotalScore) {
    this.attitudeTotalScore = attitudeTotalScore;
  }

  @Column(name = "attitude_five_amount")
  public Long getAttitudeFiveAmount() {
    return attitudeFiveAmount;
  }

  public void setAttitudeFiveAmount(Long attitudeFiveAmount) {
    this.attitudeFiveAmount = attitudeFiveAmount;
  }

  @Column(name = "attitude_four_amount")
  public Long getAttitudeFourAmount() {
    return attitudeFourAmount;
  }

  public void setAttitudeFourAmount(Long attitudeFourAmount) {
    this.attitudeFourAmount = attitudeFourAmount;
  }

  @Column(name = "attitude_three_amount")
  public Long getAttitudeThreeAmount() {
    return attitudeThreeAmount;
  }

  public void setAttitudeThreeAmount(Long attitudeThreeAmount) {
    this.attitudeThreeAmount = attitudeThreeAmount;
  }

  @Column(name = "attitude_two_amount")
  public Long getAttitudeTwoAmount() {
    return attitudeTwoAmount;
  }

  public void setAttitudeTwoAmount(Long attitudeTwoAmount) {
    this.attitudeTwoAmount = attitudeTwoAmount;
  }

  @Column(name = "attitude_one_amount")
  public Long getAttitudeOneAmount() {
    return attitudeOneAmount;
  }

  public void setAttitudeOneAmount(Long attitudeOneAmount) {
    this.attitudeOneAmount = attitudeOneAmount;
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  @Column(name = "comment_total_score")
  public Double getCommentTotalScore() {
    return commentTotalScore;
  }

  public void setCommentTotalScore(Double commentTotalScore) {
    this.commentTotalScore = commentTotalScore;
  }

  @Column(name = "comment_five_amount")
  public Long getCommentFiveAmount() {
    return commentFiveAmount;
  }

  public void setCommentFiveAmount(Long commentFiveAmount) {
    this.commentFiveAmount = commentFiveAmount;
  }

  @Column(name = "comment_four_amount")
  public Long getCommentFourAmount() {
    return commentFourAmount;
  }

  public void setCommentFourAmount(Long commentFourAmount) {
    this.commentFourAmount = commentFourAmount;
  }

  @Column(name = "comment_three_amount")
  public Long getCommentThreeAmount() {
    return commentThreeAmount;
  }

  public void setCommentThreeAmount(Long commentThreeAmount) {
    this.commentThreeAmount = commentThreeAmount;
  }

  @Column(name = "comment_two_amount")
  public Long getCommentTwoAmount() {
    return commentTwoAmount;
  }

  public void setCommentTwoAmount(Long commentTwoAmount) {
    this.commentTwoAmount = commentTwoAmount;
  }

  @Column(name = "comment_one_amount")
  public Long getCommentOneAmount() {
    return commentOneAmount;
  }

  public void setCommentOneAmount(Long commentOneAmount) {
    this.commentOneAmount = commentOneAmount;
  }
}
