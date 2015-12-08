package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-2-7
 * Time: 下午3:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "agent_targt")
public class AgentTargt extends LongIdentifier {
  private Long agentId;
  private int month;
  private String year;
  private double monthTarget;
  private double seasonTarget;
  private double yearTarget;
  @Column(name = "season_target")
  public double getSeasonTarget() {
    return seasonTarget;
  }

  public void setSeasonTarget(double seasonTarget) {
    this.seasonTarget = seasonTarget;
  }
  @Column(name = "year_target")
  public double getYearTarget() {
    return yearTarget;
  }

  public void setYearTarget(double yearTarget) {
    this.yearTarget = yearTarget;
  }

  @Column(name = "month_target")
  public double getMonthTarget() {
    return monthTarget;
  }

  public void setMonthTarget(double monthTarget) {
    this.monthTarget = monthTarget;
  }

  @Column(name = "month")
  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  @Column(name = "year")
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  @Column(name = "agent_id")
  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }


}
