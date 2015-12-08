package com.bcgogo.payment.dto;

import com.bcgogo.common.Pager;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-8
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class RechargeSearchDTO {

  private Long smsRechargeState;

  private Pager pager;

  private long timePeriod;//时间间隔

  public Long getSmsRechargeState() {
    return smsRechargeState;
  }

  public void setSmsRechargeState(Long smsRechargeState) {
    this.smsRechargeState = smsRechargeState;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public long getTimePeriod() {
    return timePeriod;
  }

  public void setTimePeriod(long timePeriod) {
    this.timePeriod = timePeriod;
  }
}
