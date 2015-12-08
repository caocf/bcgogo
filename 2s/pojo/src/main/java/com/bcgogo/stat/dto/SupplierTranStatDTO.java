package com.bcgogo.stat.dto;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-29
 * Time: 下午5:32
 */
public class SupplierTranStatDTO {
  private String supplierName;
  private double amount;
  private int times;
  private double total;

  public String getTotalStr(){
    return String.format("%.1f", total);
  }

  public String getAmountStr(){
    return String.format("%.1f", amount);
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }
}
