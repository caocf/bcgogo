package com.bcgogo.txn.dto.supplierComment;


import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;

/**
 * 评价统计dto
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-13
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class CommentStatDTO implements Serializable {

  public final static String ZERO_PERCENT = "0%";
  public final static String PERCENT = "%";
  public final static double HUNDRED = 100D;
  public final static double ZER0_SPAN = 190D;

  private Long id;
  private Long shopId;       //店铺id

  private Long orderAmount; //供应商在线交易笔数
  private Long recordAmount; //有评价的交易笔数
  private Double totalScore; //有评价的交易平分总和
  private double totalScoreSpan;//用来控制前台星星css的高度
  private Long statTime;//最后一次统计时间
  private int totalScoreWidth;//前台分数控件css的宽度

  private Double qualityTotalScore; //商品质量平分总和
  private Long qualityFiveAmount;  //货品质量5分笔数
  private Long qualityFourAmount;  //货品质量4分笔数
  private Long qualityThreeAmount; //货品质量3分笔数
  private Long qualityTwoAmount;  //货品质量2分笔数
  private Long qualityOneAmount;  //货品质量1分笔数
  private double qualityScoreSpan;//用来控制前台星星css的高度

  private String qualityFiveAmountPer = ZERO_PERCENT;  //货品质量5分笔数百分比
  private String qualityFourAmountPer = ZERO_PERCENT;  //货品质量4分笔数 百分比
  private String qualityThreeAmountPer = ZERO_PERCENT; //货品质量3分笔数 百分比
  private String qualityTwoAmountPer = ZERO_PERCENT;  //货品质量2分笔数 百分比
  private String qualityOneAmountPer = ZERO_PERCENT;  //货品质量1分笔数 百分比

  private Double performanceTotalScore;
  private Long performanceFiveAmount;
  private Long performanceFourAmount;
  private Long performanceThreeAmount;
  private Long performanceTwoAmount;
  private Long performanceOneAmount;
  private double performanceScoreSpan;

  private String performanceFiveAmountPer = ZERO_PERCENT;
  private String performanceFourAmountPer = ZERO_PERCENT;
  private String performanceThreeAmountPer = ZERO_PERCENT;
  private String performanceTwoAmountPer = ZERO_PERCENT;
  private String performanceOneAmountPer = ZERO_PERCENT;


  private Double speedTotalScore;
  private Long speedFiveAmount;
  private Long speedFourAmount;
  private Long speedThreeAmount;
  private Long speedTwoAmount;
  private Long speedOneAmount;
  private double speedScoreSpan;

  private String speedFiveAmountPer = ZERO_PERCENT;
  private String speedFourAmountPer = ZERO_PERCENT;
  private String speedThreeAmountPer = ZERO_PERCENT;
  private String speedTwoAmountPer = ZERO_PERCENT;
  private String speedOneAmountPer = ZERO_PERCENT;

  private Double attitudeTotalScore;
  private Long attitudeFiveAmount;
  private Long attitudeFourAmount;
  private Long attitudeThreeAmount;
  private Long attitudeTwoAmount;
  private Long attitudeOneAmount;
  private double attitudeScoreSpan;

  private String attitudeFiveAmountPer = ZERO_PERCENT;
  private String attitudeFourAmountPer = ZERO_PERCENT;
  private String attitudeThreeAmountPer = ZERO_PERCENT;
  private String attitudeTwoAmountPer = ZERO_PERCENT;
  private String attitudeOneAmountPer = ZERO_PERCENT;


  private Double commentTotalScore;  //手机端评价就一项 存在这里
  private Long commentFiveAmount;  //手机端评价五分数量
  private Long commentFourAmount; //手机端评价4分数量
  private Long commentThreeAmount; //手机端评价3分数量
  private Long commentTwoAmount; //手机端评价2分数量
  private Long commentOneAmount; //手机端评价1分数量
  private Double averageScore;   //手机端综合评分

  //直接从数据库取出以下数据
  private Long badCommentAmount;//手机端评价差评个数
  private Long mediumCommentAmount;//手机端评价中评个数
  private Long goodCommentAmount;//手机端评价差评个数


  public CommentStatDTO() {
    orderAmount = 0L; //供应商在线交易笔数
    recordAmount = 0L; //有评价的交易笔数
    totalScore = 0D; //有评价的交易平分总和
    statTime = System.currentTimeMillis();//最后一次统计时间

    qualityTotalScore = 0D; //商品质量平分总和
    qualityFiveAmount = 0L;  //货品质量5分笔数
    qualityFourAmount = 0L;  //货品质量4分笔数
    qualityThreeAmount = 0L; //货品质量3分笔数
    qualityTwoAmount = 0L;  //货品质量2分笔数
    qualityOneAmount = 0L;  //货品质量1分笔数

    performanceTotalScore = 0D;
    performanceFiveAmount = 0L;
    performanceFourAmount = 0L;
    performanceThreeAmount = 0L;
    performanceTwoAmount = 0L;
    performanceOneAmount = 0L;

    speedTotalScore = 0D;
    speedFiveAmount = 0L;
    speedFourAmount = 0L;
    speedThreeAmount = 0L;
    speedTwoAmount = 0L;
    speedOneAmount = 0L;

    attitudeTotalScore = 0D;
    attitudeFiveAmount = 0L;
    attitudeFourAmount = 0L;
    attitudeThreeAmount = 0L;
    attitudeTwoAmount = 0L;
    attitudeOneAmount = 0L;

    commentTotalScore = 0D;  //手机端评价就一项 存在这里
    commentFiveAmount = 0L;  //手机端评价五分数量
    commentFourAmount = 0L; //手机端评价4分数量
    commentThreeAmount = 0L; //手机端评价3分数量
    commentTwoAmount = 0L; //手机端评价2分数量
    commentOneAmount = 0L; //手机端评价1分数量
    averageScore = 0D;      //手机端综合评分

    badCommentAmount=0L;
    mediumCommentAmount=0L;
    goodCommentAmount=0L;

  }


  public void calculateFromSupplierRecordDTO(SupplierCommentRecordDTO supplierCommentRecordDTO) {
    double qualityScoreFromRecord;  //质量分数
    double performanceScoreFromRecord; //性价比分数
    double speedScoreFromRecord; //发货速度分数
    double attitudeScoreFromRecord;   //服务态度分数
    double totalScoreFromRecord;//总分

    this.setRecordAmount(this.getRecordAmount() + 1);

    qualityScoreFromRecord = NumberUtil.doubleVal(supplierCommentRecordDTO.getQualityScore());
    performanceScoreFromRecord = NumberUtil.doubleVal(supplierCommentRecordDTO.getPerformanceScore());
    speedScoreFromRecord = NumberUtil.doubleVal(supplierCommentRecordDTO.getSpeedScore());
    attitudeScoreFromRecord = NumberUtil.doubleVal(supplierCommentRecordDTO.getAttitudeScore());

    totalScoreFromRecord = qualityScoreFromRecord + performanceScoreFromRecord + speedScoreFromRecord + attitudeScoreFromRecord;

    this.setTotalScore(getTotalScore() + totalScoreFromRecord);
    this.setQualityTotalScore(this.getQualityTotalScore() + qualityScoreFromRecord);
    this.setPerformanceTotalScore(this.getPerformanceTotalScore() + performanceScoreFromRecord);
    this.setSpeedTotalScore(this.getSpeedTotalScore() + speedScoreFromRecord);
    this.setAttitudeTotalScore(this.getAttitudeTotalScore() + attitudeScoreFromRecord);

    if (qualityScoreFromRecord == 5) {
      this.setQualityFiveAmount(this.getQualityFiveAmount() + 1);
    } else if (qualityScoreFromRecord == 4) {
      this.setQualityFourAmount(this.getQualityFourAmount() + 1);
    } else if (qualityScoreFromRecord == 3) {
      this.setQualityThreeAmount(this.getQualityThreeAmount() + 1);
    } else if (qualityScoreFromRecord == 2) {
      this.setQualityTwoAmount(this.getQualityTwoAmount() + 1);
    } else {
      this.setQualityOneAmount(this.getQualityOneAmount() + 1);
    }

    if (performanceScoreFromRecord == 5) {
      this.setPerformanceFiveAmount(this.getPerformanceFiveAmount() + 1);
    } else if (performanceScoreFromRecord == 4) {
      this.setPerformanceFourAmount(this.getPerformanceFourAmount() + 1);
    } else if (performanceScoreFromRecord == 3) {
      this.setPerformanceThreeAmount(this.getPerformanceThreeAmount() + 1);
    } else if (performanceScoreFromRecord == 2) {
      this.setPerformanceTwoAmount(this.getPerformanceTwoAmount() + 1);
    } else {
      this.setPerformanceOneAmount(this.getPerformanceOneAmount() + 1);
    }


    if (speedScoreFromRecord == 5) {
      this.setSpeedFiveAmount(this.getSpeedFiveAmount() + 1);
    } else if (speedScoreFromRecord == 4) {
      this.setSpeedFourAmount(this.getSpeedFourAmount() + 1);
    } else if (speedScoreFromRecord == 3) {
      this.setSpeedThreeAmount(this.getSpeedThreeAmount() + 1);
    } else if (speedScoreFromRecord == 2) {
      this.setSpeedTwoAmount(this.getSpeedTwoAmount() + 1);
    } else {
      this.setSpeedOneAmount(this.getSpeedOneAmount() + 1);
    }


    if (attitudeScoreFromRecord == 5) {
      this.setAttitudeFiveAmount(this.getAttitudeFiveAmount() + 1);
    } else if (attitudeScoreFromRecord == 4) {
      this.setAttitudeFourAmount(this.getAttitudeFourAmount() + 1);
    } else if (attitudeScoreFromRecord == 3) {
      this.setAttitudeThreeAmount(this.getAttitudeThreeAmount() + 1);
    } else if (attitudeScoreFromRecord == 2) {
      this.setAttitudeTwoAmount(this.getAttitudeTwoAmount() + 1);
    } else {
      this.setAttitudeOneAmount(this.getAttitudeOneAmount() + 1);
    }
  }


  public void calculateFromAppUserRecordDTO(AppUserCommentRecordDTO appUserCommentRecordDTO) {

    double commentScore = appUserCommentRecordDTO.getCommentScore();//手机端评价分数
    this.setCommentTotalScore(NumberUtil.doubleVal(this.getCommentTotalScore()) + commentScore);
    this.setRecordAmount(NumberUtil.longValue(this.getRecordAmount()) + 1);

    if (commentScore == 5) {
      this.setCommentFiveAmount(NumberUtil.longValue(this.getCommentFiveAmount()) + 1);
    } else if (commentScore == 4) {
      this.setCommentFourAmount(NumberUtil.longValue(this.getCommentFourAmount()) + 1);
    } else if (commentScore == 3) {
      this.setCommentThreeAmount(NumberUtil.longValue(this.getCommentThreeAmount()) + 1);
    } else if (commentScore == 2) {
      this.setCommentTwoAmount(NumberUtil.longValue(this.getCommentTwoAmount()) + 1);
    } else {
      this.setCommentOneAmount(NumberUtil.longValue(this.getCommentOneAmount()) + 1);
    }
  }

  public void calculate() {
    if (getRecordAmount() == 0L) {
      this.setTotalScore(0D);
      this.setTotalScoreSpan(ZER0_SPAN);
      this.setQualityScoreSpan(ZER0_SPAN);
      this.setPerformanceScoreSpan(ZER0_SPAN);
      this.setAttitudeScoreSpan(ZER0_SPAN);
      this.setSpeedScoreSpan(ZER0_SPAN);

    } else {
      double recordAmount = getRecordAmount().doubleValue();

      if(NumberUtil.longValue(this.getCommentFiveAmount()) + NumberUtil.longValue(this.getCommentFourAmount()) + NumberUtil.longValue(this.getCommentThreeAmount())
          + NumberUtil.longValue(this.getCommentTwoAmount()) + NumberUtil.longValue(this.getCommentOneAmount()) <= 0){
        this.setTotalScore(NumberUtil.toReserve((getTotalScore() + NumberUtil.doubleVal(getCommentTotalScore())) / (getRecordAmount() * 4)));
      }else{
        this.setTotalScore(NumberUtil.round((this.getTotalScore() + NumberUtil.doubleVal(this.getCommentTotalScore())) / this.getRecordAmount(), 1));
      }

      int totalScoreIntValue = getTotalScore().intValue();
      if (getTotalScore() - totalScoreIntValue > 0) {
        this.setTotalScoreSpan(((5 - totalScoreIntValue - CommentConstant.SCORE_UNIT) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      } else {
        this.setTotalScoreSpan(((5 - totalScoreIntValue) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      }

      this.setQualityTotalScore(NumberUtil.toReserve(getQualityTotalScore() / getRecordAmount()));
      int qualityScoreIntValue = getQualityTotalScore().intValue();
      if (getQualityTotalScore() - qualityScoreIntValue > 0) {
        this.setQualityScoreSpan(((5 - qualityScoreIntValue - CommentConstant.SCORE_UNIT) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      } else {
        this.setQualityScoreSpan(((5 - qualityScoreIntValue) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      }

      this.setQualityFiveAmountPer(String.valueOf(NumberUtil.toReserve(getQualityFiveAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setQualityFourAmountPer(String.valueOf(NumberUtil.toReserve(getQualityFourAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setQualityThreeAmountPer(String.valueOf(NumberUtil.toReserve(getQualityThreeAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setQualityTwoAmountPer(String.valueOf(NumberUtil.toReserve(getQualityTwoAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setQualityOneAmountPer(String.valueOf(NumberUtil.toReserve(getQualityOneAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);

      this.setPerformanceTotalScore(NumberUtil.toReserve(getPerformanceTotalScore() / getRecordAmount()));

      int performanceScoreIntValue = getPerformanceTotalScore().intValue();
      if (getPerformanceTotalScore() - performanceScoreIntValue > 0) {
        this.setPerformanceScoreSpan(((5 - performanceScoreIntValue - CommentConstant.SCORE_UNIT) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      } else {
        this.setPerformanceScoreSpan(((5 - performanceScoreIntValue) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      }

      this.setPerformanceFiveAmountPer(String.valueOf(NumberUtil.toReserve(getPerformanceFiveAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setPerformanceFourAmountPer(String.valueOf(NumberUtil.toReserve(getPerformanceFourAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setPerformanceThreeAmountPer(String.valueOf(NumberUtil.toReserve(getPerformanceThreeAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setPerformanceTwoAmountPer(String.valueOf(NumberUtil.toReserve(getPerformanceTwoAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setPerformanceOneAmountPer(String.valueOf(NumberUtil.toReserve(getPerformanceOneAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);

      this.setSpeedTotalScore(NumberUtil.toReserve(getSpeedTotalScore() / getRecordAmount()));

      int speedScoreIntValue = getSpeedTotalScore().intValue();
      if (getSpeedTotalScore() - speedScoreIntValue > 0) {
        this.setSpeedScoreSpan(((5 - speedScoreIntValue - CommentConstant.SCORE_UNIT) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      } else {
        this.setSpeedScoreSpan(((5 - speedScoreIntValue) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      }

      this.setSpeedFiveAmountPer(String.valueOf(NumberUtil.toReserve(getSpeedFiveAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setSpeedFourAmountPer(String.valueOf(NumberUtil.toReserve(getSpeedFourAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setSpeedThreeAmountPer(String.valueOf(NumberUtil.toReserve(getSpeedThreeAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setSpeedTwoAmountPer(String.valueOf(NumberUtil.toReserve(getSpeedTwoAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setSpeedOneAmountPer(String.valueOf(NumberUtil.toReserve(getSpeedOneAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);

      this.setAttitudeTotalScore(NumberUtil.toReserve(getAttitudeTotalScore() / getRecordAmount()));

      int attitudeScoreIntValue = getAttitudeTotalScore().intValue();
      if (getAttitudeTotalScore() - attitudeScoreIntValue > 0) {
        this.setAttitudeScoreSpan(((5 - attitudeScoreIntValue - CommentConstant.SCORE_UNIT) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      } else {
        this.setAttitudeScoreSpan(((5 - attitudeScoreIntValue) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
      }

      this.setAttitudeFiveAmountPer(String.valueOf(NumberUtil.toReserve(getAttitudeFiveAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setAttitudeFourAmountPer(String.valueOf(NumberUtil.toReserve(getAttitudeFourAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setAttitudeThreeAmountPer(String.valueOf(NumberUtil.toReserve(getAttitudeThreeAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setAttitudeTwoAmountPer(String.valueOf(NumberUtil.toReserve(getAttitudeTwoAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
      this.setAttitudeOneAmountPer(String.valueOf(NumberUtil.toReserve(getAttitudeOneAmount() * HUNDRED / recordAmount, NumberUtil.MONEY_PRECISION)) + PERCENT);
    }
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getOrderAmount() {
    return orderAmount;
  }

  public void setOrderAmount(Long orderAmount) {
    this.orderAmount = orderAmount;
  }

  public Long getRecordAmount() {
    return recordAmount;
  }

  public void setRecordAmount(Long recordAmount) {
    this.recordAmount = recordAmount;
  }

  public Double getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Double totalScore) {
    this.totalScore = totalScore;

    if (totalScore > totalScore.intValue() + 0.5) {
      this.totalScoreWidth = (totalScore.intValue() + 1) * 2;
    }else if (totalScore > totalScore.intValue()) {
      this.totalScoreWidth = (totalScore.intValue()) * 2 + 1;
    } else {
      this.totalScoreWidth = (totalScore.intValue()) * 2;
    }
  }

  public Double getQualityTotalScore() {
    return qualityTotalScore;
  }

  public void setQualityTotalScore(Double qualityTotalScore) {
    this.qualityTotalScore = qualityTotalScore;
  }

  public Long getQualityFiveAmount() {
    return qualityFiveAmount;
  }

  public void setQualityFiveAmount(Long qualityFiveAmount) {
    this.qualityFiveAmount = qualityFiveAmount;
  }

  public Long getQualityFourAmount() {
    return qualityFourAmount;
  }

  public void setQualityFourAmount(Long qualityFourAmount) {
    this.qualityFourAmount = qualityFourAmount;
  }

  public Long getQualityThreeAmount() {
    return qualityThreeAmount;
  }

  public void setQualityThreeAmount(Long qualityThreeAmount) {
    this.qualityThreeAmount = qualityThreeAmount;
  }

  public Long getQualityTwoAmount() {
    return qualityTwoAmount;
  }

  public void setQualityTwoAmount(Long qualityTwoAmount) {
    this.qualityTwoAmount = qualityTwoAmount;
  }

  public Long getQualityOneAmount() {
    return qualityOneAmount;
  }

  public void setQualityOneAmount(Long qualityOneAmount) {
    this.qualityOneAmount = qualityOneAmount;
  }

  public Double getPerformanceTotalScore() {
    return performanceTotalScore;
  }

  public void setPerformanceTotalScore(Double performanceTotalScore) {
    this.performanceTotalScore = performanceTotalScore;
  }

  public Long getPerformanceFiveAmount() {
    return performanceFiveAmount;
  }

  public void setPerformanceFiveAmount(Long performanceFiveAmount) {
    this.performanceFiveAmount = performanceFiveAmount;
  }

  public Long getPerformanceFourAmount() {
    return performanceFourAmount;
  }

  public void setPerformanceFourAmount(Long performanceFourAmount) {
    this.performanceFourAmount = performanceFourAmount;
  }

  public Long getPerformanceThreeAmount() {
    return performanceThreeAmount;
  }

  public void setPerformanceThreeAmount(Long performanceThreeAmount) {
    this.performanceThreeAmount = performanceThreeAmount;
  }

  public Long getPerformanceTwoAmount() {
    return performanceTwoAmount;
  }

  public void setPerformanceTwoAmount(Long performanceTwoAmount) {
    this.performanceTwoAmount = performanceTwoAmount;
  }

  public Long getPerformanceOneAmount() {
    return performanceOneAmount;
  }

  public void setPerformanceOneAmount(Long performanceOneAmount) {
    this.performanceOneAmount = performanceOneAmount;
  }

  public Double getSpeedTotalScore() {
    return speedTotalScore;
  }

  public void setSpeedTotalScore(Double speedTotalScore) {
    this.speedTotalScore = speedTotalScore;
  }

  public Long getSpeedFiveAmount() {
    return speedFiveAmount;
  }

  public void setSpeedFiveAmount(Long speedFiveAmount) {
    this.speedFiveAmount = speedFiveAmount;
  }

  public Long getSpeedFourAmount() {
    return speedFourAmount;
  }

  public void setSpeedFourAmount(Long speedFourAmount) {
    this.speedFourAmount = speedFourAmount;
  }

  public Long getSpeedThreeAmount() {
    return speedThreeAmount;
  }

  public void setSpeedThreeAmount(Long speedThreeAmount) {
    this.speedThreeAmount = speedThreeAmount;
  }

  public Long getSpeedTwoAmount() {
    return speedTwoAmount;
  }

  public void setSpeedTwoAmount(Long speedTwoAmount) {
    this.speedTwoAmount = speedTwoAmount;
  }

  public Long getSpeedOneAmount() {
    return speedOneAmount;
  }

  public void setSpeedOneAmount(Long speedOneAmount) {
    this.speedOneAmount = speedOneAmount;
  }

  public Double getAttitudeTotalScore() {
    return attitudeTotalScore;
  }

  public void setAttitudeTotalScore(Double attitudeTotalScore) {
    this.attitudeTotalScore = attitudeTotalScore;
  }

  public Long getAttitudeFiveAmount() {
    return attitudeFiveAmount;
  }

  public void setAttitudeFiveAmount(Long attitudeFiveAmount) {
    this.attitudeFiveAmount = attitudeFiveAmount;
  }

  public Long getAttitudeFourAmount() {
    return attitudeFourAmount;
  }

  public void setAttitudeFourAmount(Long attitudeFourAmount) {
    this.attitudeFourAmount = attitudeFourAmount;
  }

  public Long getAttitudeThreeAmount() {
    return attitudeThreeAmount;
  }

  public void setAttitudeThreeAmount(Long attitudeThreeAmount) {
    this.attitudeThreeAmount = attitudeThreeAmount;
  }

  public Long getAttitudeTwoAmount() {
    return attitudeTwoAmount;
  }

  public void setAttitudeTwoAmount(Long attitudeTwoAmount) {
    this.attitudeTwoAmount = attitudeTwoAmount;
  }

  public Long getAttitudeOneAmount() {
    return attitudeOneAmount;
  }

  public void setAttitudeOneAmount(Long attitudeOneAmount) {
    this.attitudeOneAmount = attitudeOneAmount;
  }

  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  public String getQualityFiveAmountPer() {
    return qualityFiveAmountPer;
  }

  public void setQualityFiveAmountPer(String qualityFiveAmountPer) {
    this.qualityFiveAmountPer = qualityFiveAmountPer;
  }

  public String getQualityFourAmountPer() {
    return qualityFourAmountPer;
  }

  public void setQualityFourAmountPer(String qualityFourAmountPer) {
    this.qualityFourAmountPer = qualityFourAmountPer;
  }

  public String getQualityThreeAmountPer() {
    return qualityThreeAmountPer;
  }

  public void setQualityThreeAmountPer(String qualityThreeAmountPer) {
    this.qualityThreeAmountPer = qualityThreeAmountPer;
  }

  public String getQualityTwoAmountPer() {
    return qualityTwoAmountPer;
  }

  public void setQualityTwoAmountPer(String qualityTwoAmountPer) {
    this.qualityTwoAmountPer = qualityTwoAmountPer;
  }

  public String getQualityOneAmountPer() {
    return qualityOneAmountPer;
  }

  public void setQualityOneAmountPer(String qualityOneAmountPer) {
    this.qualityOneAmountPer = qualityOneAmountPer;
  }

  public String getPerformanceFiveAmountPer() {
    return performanceFiveAmountPer;
  }

  public void setPerformanceFiveAmountPer(String performanceFiveAmountPer) {
    this.performanceFiveAmountPer = performanceFiveAmountPer;
  }

  public String getPerformanceFourAmountPer() {
    return performanceFourAmountPer;
  }

  public void setPerformanceFourAmountPer(String performanceFourAmountPer) {
    this.performanceFourAmountPer = performanceFourAmountPer;
  }

  public String getPerformanceThreeAmountPer() {
    return performanceThreeAmountPer;
  }

  public void setPerformanceThreeAmountPer(String performanceThreeAmountPer) {
    this.performanceThreeAmountPer = performanceThreeAmountPer;
  }

  public String getPerformanceTwoAmountPer() {
    return performanceTwoAmountPer;
  }

  public void setPerformanceTwoAmountPer(String performanceTwoAmountPer) {
    this.performanceTwoAmountPer = performanceTwoAmountPer;
  }

  public String getPerformanceOneAmountPer() {
    return performanceOneAmountPer;
  }

  public void setPerformanceOneAmountPer(String performanceOneAmountPer) {
    this.performanceOneAmountPer = performanceOneAmountPer;
  }

  public String getSpeedFiveAmountPer() {
    return speedFiveAmountPer;
  }

  public void setSpeedFiveAmountPer(String speedFiveAmountPer) {
    this.speedFiveAmountPer = speedFiveAmountPer;
  }

  public String getSpeedFourAmountPer() {
    return speedFourAmountPer;
  }

  public void setSpeedFourAmountPer(String speedFourAmountPer) {
    this.speedFourAmountPer = speedFourAmountPer;
  }

  public String getSpeedThreeAmountPer() {
    return speedThreeAmountPer;
  }

  public void setSpeedThreeAmountPer(String speedThreeAmountPer) {
    this.speedThreeAmountPer = speedThreeAmountPer;
  }

  public String getSpeedTwoAmountPer() {
    return speedTwoAmountPer;
  }

  public void setSpeedTwoAmountPer(String speedTwoAmountPer) {
    this.speedTwoAmountPer = speedTwoAmountPer;
  }

  public String getSpeedOneAmountPer() {
    return speedOneAmountPer;
  }

  public void setSpeedOneAmountPer(String speedOneAmountPer) {
    this.speedOneAmountPer = speedOneAmountPer;
  }

  public String getAttitudeFiveAmountPer() {
    return attitudeFiveAmountPer;
  }

  public void setAttitudeFiveAmountPer(String attitudeFiveAmountPer) {
    this.attitudeFiveAmountPer = attitudeFiveAmountPer;
  }

  public String getAttitudeFourAmountPer() {
    return attitudeFourAmountPer;
  }

  public void setAttitudeFourAmountPer(String attitudeFourAmountPer) {
    this.attitudeFourAmountPer = attitudeFourAmountPer;
  }

  public String getAttitudeThreeAmountPer() {
    return attitudeThreeAmountPer;
  }

  public void setAttitudeThreeAmountPer(String attitudeThreeAmountPer) {
    this.attitudeThreeAmountPer = attitudeThreeAmountPer;
  }

  public String getAttitudeTwoAmountPer() {
    return attitudeTwoAmountPer;
  }

  public void setAttitudeTwoAmountPer(String attitudeTwoAmountPer) {
    this.attitudeTwoAmountPer = attitudeTwoAmountPer;
  }

  public String getAttitudeOneAmountPer() {
    return attitudeOneAmountPer;
  }

  public void setAttitudeOneAmountPer(String attitudeOneAmountPer) {
    this.attitudeOneAmountPer = attitudeOneAmountPer;
  }

  public double getQualityScoreSpan() {
    return qualityScoreSpan;
  }

  public void setQualityScoreSpan(double qualityScoreSpan) {
    this.qualityScoreSpan = qualityScoreSpan;
  }

  public double getPerformanceScoreSpan() {
    return performanceScoreSpan;
  }

  public void setPerformanceScoreSpan(double performanceScoreSpan) {
    this.performanceScoreSpan = performanceScoreSpan;
  }

  public double getSpeedScoreSpan() {
    return speedScoreSpan;
  }

  public void setSpeedScoreSpan(double speedScoreSpan) {
    this.speedScoreSpan = speedScoreSpan;
  }

  public double getAttitudeScoreSpan() {
    return attitudeScoreSpan;
  }

  public void setAttitudeScoreSpan(double attitudeScoreSpan) {
    this.attitudeScoreSpan = attitudeScoreSpan;
  }

  public double getTotalScoreSpan() {
    return totalScoreSpan;
  }

  public void setTotalScoreSpan(double totalScoreSpan) {
    this.totalScoreSpan = totalScoreSpan;
  }

  public Double getCommentTotalScore() {
    return commentTotalScore;
  }

  public void setCommentTotalScore(Double commentTotalScore) {
    this.commentTotalScore = commentTotalScore;
  }

  public Long getCommentFiveAmount() {
    return commentFiveAmount;
  }

  public void setCommentFiveAmount(Long commentFiveAmount) {
    this.commentFiveAmount = commentFiveAmount;
  }

  public Long getCommentFourAmount() {
    return commentFourAmount;
  }

  public void setCommentFourAmount(Long commentFourAmount) {
    this.commentFourAmount = commentFourAmount;
  }

  public Long getCommentThreeAmount() {
    return commentThreeAmount;
  }

  public void setCommentThreeAmount(Long commentThreeAmount) {
    this.commentThreeAmount = commentThreeAmount;
  }

  public Long getCommentTwoAmount() {
    return commentTwoAmount;
  }

  public void setCommentTwoAmount(Long commentTwoAmount) {
    this.commentTwoAmount = commentTwoAmount;
  }

  public Long getCommentOneAmount() {
    return commentOneAmount;
  }

  public void setCommentOneAmount(Long commentOneAmount) {
    this.commentOneAmount = commentOneAmount;
  }

  public Double getAverageScore() {
    return averageScore;
  }

  public void setAverageScore(Double averageScore) {
    this.averageScore = averageScore;
    if (averageScore > averageScore.intValue() + 0.5) {
      this.totalScoreWidth = (averageScore.intValue() + 1) * 2;
    }else if (averageScore > averageScore.intValue()) {
      this.totalScoreWidth = (averageScore.intValue()) * 2 + 1;
    } else {
      this.totalScoreWidth = (averageScore.intValue()) * 2;
    }
  }

  public Long getBadCommentAmount() {
    return badCommentAmount;
  }

  public void setBadCommentAmount(Long badCommentAmount) {
    this.badCommentAmount = badCommentAmount;
  }

  public Long getMediumCommentAmount() {
    return mediumCommentAmount;
  }

  public void setMediumCommentAmount(Long mediumCommentAmount) {
    this.mediumCommentAmount = mediumCommentAmount;
  }

  public Long getGoodCommentAmount() {
    return goodCommentAmount;
  }

  public void setGoodCommentAmount(Long goodCommentAmount) {
    this.goodCommentAmount = goodCommentAmount;
  }

  public int getTotalScoreWidth() {
    return totalScoreWidth;
  }

  public void setTotalScoreWidth(int totalScoreWidth) {
    this.totalScoreWidth = totalScoreWidth;
  }
}
