package com.bcgogo.txn.dto.StatementAccount;

import com.bcgogo.txn.dto.StatementAccountOrderDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 对账单查询结果封装类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午3:33
 * To change this template use File | Settings | File Templates.
 */
public class StatementAccountResultDTO {
  //收入单据列表
  private List<StatementAccountOrderDTO> receivableList = new ArrayList<StatementAccountOrderDTO>();

  //支出单据列表
  private List<StatementAccountOrderDTO> payList = new ArrayList<StatementAccountOrderDTO>();

  //应收合计
  private double totalReceivable;

  //应付合计
  private double totalPayable;

  //对账结算 应收-应付
  private double totalDebt;

  private String lastStateAccount;//上期对账余额

  private String customer;//客户名字

  //开始时间
  private String startDateStr;

  //结束时间
  private String endDateStr;

  //对账开始时间
  private Long startDate;

  //对账结束时间
  private Long endDate;

  //返回结果的条数 控制前台div的宽度
  private int resultSize;

  private OrderDebtType orderDebtType;//单据欠款类型

  public String getLastStateAccount() {
    return lastStateAccount;
  }

  public void setLastStateAccount(String lastStateAccount) {
    this.lastStateAccount = lastStateAccount;
  }

  public List<StatementAccountOrderDTO> getReceivableList() {
    return receivableList;
  }

  public void setReceivableList(List<StatementAccountOrderDTO> receivableList) {
    this.receivableList = receivableList;
  }

  public List<StatementAccountOrderDTO> getPayList() {
    return payList;
  }

  public void setPayList(List<StatementAccountOrderDTO> payList) {
    this.payList = payList;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(double totalReceivable) {
    this.totalReceivable = totalReceivable;
  }

  public double getTotalPayable() {
    return totalPayable;
  }

  public void setTotalPayable(double totalPayable) {
    this.totalPayable = totalPayable;
  }

  public double getTotalDebt() {
    return totalDebt;
  }

  public void setTotalDebt(double totalDebt) {
    this.totalDebt = totalDebt;
  }

  public int getResultSize() {
    return resultSize;
  }

  public void setResultSize(int resultSize) {
    this.resultSize = resultSize;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public OrderDebtType getOrderDebtType() {
    return orderDebtType;
  }

  public void setOrderDebtType(OrderDebtType orderDebtType) {
    this.orderDebtType = orderDebtType;
  }
}
