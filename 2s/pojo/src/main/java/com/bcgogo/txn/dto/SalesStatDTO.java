package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-1-11
 * Time: 下午5:44
 * To change this template use File | Settings | File Templates.
 */
public class SalesStatDTO implements Serializable,Comparable<SalesStatDTO> {
  private String id;
  private Long shopId;
  private Long productId;
  private Integer statYear;
  private Integer statMonth;
  private Integer statDay;
  private int times;    //次数
  private double amount;   //数量
  private double total;     //总额
  private Long statTime;//每次统计的时间

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  public Integer getStatDay() {
    return statDay;
  }

  public void setStatDay(Integer statDay) {
    this.statDay = statDay;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

    //实现Comparable的方法
  public int compareTo(SalesStatDTO salesStatDTO){
      double d1 = this.getAmount();
      double d2 = salesStatDTO.getAmount();
      if(d1>d2){
        return -1;
      }else if(d1==d2){
        return 0;
      }else if(d1<d2){
        return 1;
      }
      return 0;
  }
}
