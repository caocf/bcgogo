package com.bcgogo.txn.dto.finance;

/**
 * User: ZhangJuntao
 * Date: 13-3-22
 * Time: 下午2:15
 */
public class InstalmentPlanAlgorithmDTO {
  private Long id;
  private String name;
  private Integer periods;                //期数
  private Double periodsMonthRate = 1.0;  //一期对应几个月
  private String terminallyRatio;         //每期比例 如 0.1,0.2
  private String memo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPeriods() {
    return periods;
  }

  public void setPeriods(Integer periods) {
    this.periods = periods;
  }

  public Double getPeriodsMonthRate() {
    return periodsMonthRate;
  }

  public void setPeriodsMonthRate(Double periodsMonthRate) {
    this.periodsMonthRate = periodsMonthRate;
  }

  public String getTerminallyRatio() {
    return terminallyRatio;
  }

  public void setTerminallyRatio(String terminallyRatio) {
    this.terminallyRatio = terminallyRatio;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
