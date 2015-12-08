package com.bcgogo.txn.model.finance;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.InstalmentPlanAlgorithmDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午9:01
 * 分期付款算法
 */
@Entity
@Table(name = "instalment_plan_algorithm")
public class InstalmentPlanAlgorithm extends LongIdentifier {
  private String name;
  private Integer periods;                //期数
  private Double periodsMonthRate = 1.0;  //一期对应几个月
  private String terminallyRatio;         //每期比例 如 0.1,0.2
  private String memo;

  public InstalmentPlanAlgorithmDTO toDTO() {
    InstalmentPlanAlgorithmDTO dto = new InstalmentPlanAlgorithmDTO();
    dto.setId(this.getId());
    dto.setName(this.getName());
    dto.setPeriods(this.getPeriods());
    dto.setPeriodsMonthRate(this.getPeriodsMonthRate());
    dto.setTerminallyRatio(this.getTerminallyRatio());
    dto.setMemo(this.getMemo());
    return dto;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "periods")
  public Integer getPeriods() {
    return periods;
  }

  public void setPeriods(Integer periods) {
    this.periods = periods;
  }

  @Column(name = "periods_month_rate")
  public Double getPeriodsMonthRate() {
    return periodsMonthRate;
  }

  public void setPeriodsMonthRate(Double periodsMonthRate) {
    this.periodsMonthRate = periodsMonthRate;
  }

  @Column(name = "terminally_ratio")
  public String getTerminallyRatio() {
    return terminallyRatio;
  }

  public void setTerminallyRatio(String terminallyRatio) {
    this.terminallyRatio = terminallyRatio;
  }
}
