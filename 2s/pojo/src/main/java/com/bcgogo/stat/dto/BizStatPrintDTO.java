package com.bcgogo.stat.dto;

import com.bcgogo.txn.dto.BusinessStatDTO;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-6
 * Time: 下午2:43
 * To change this template use File | Settings | File Templates.
 */
public class BizStatPrintDTO {
  private double sales; //销售金额，包含施工单中的销售金额
  private double wash;  //洗车金额
  private double service; //服务金额：即 施工单中的施工费
  private double statSum; // 销售 + 洗车 + 服务
  private double productCost; // 商品成本: 销售单和施工单所卖出的产品的成本
  private double rent;
  private double labor;
  private double other;
  private double totalExpend;
  private double grossProfit;
  private String grossProfitPercent;
  private double memberIncome;
  private double otherFee;
  private double otherIncome;//营业外收入
  private double orderOtherIncomeCost;//施工单或者销售单 其他费用成本统计

  public String getGrossProfitPercent() {
    return grossProfitPercent;
  }

  public double getGrossProfit() {
    return grossProfit;
  }

  public double getRent() {
    return rent;
  }

  public double getLabor() {
    return labor;
  }

  public double getOther() {
    return other;
  }

  public double getTotalExpend() {
    return totalExpend;
  }

  public double getSales() {
    return sales;
  }

  public double getWash() {
    return wash;
  }

  public double getStatSum() {
    return statSum;
  }

  public double getService() {
    return service;
  }

  public double getProductCost() {
    return productCost;
  }

  public void setSales(double sales) {
    this.sales = sales;
  }

  public void setWash(double wash) {
    this.wash = wash;
  }

  public void setService(double service) {
    this.service = service;
  }

  public void setStatSum(double statSum) {
    this.statSum = statSum;
  }

  public void setProductCost(double productCost) {
    this.productCost = productCost;
  }

  public void setRent(double rent) {
    this.rent = rent;
  }

  public void setLabor(double labor) {
    this.labor = labor;
  }

  public void setOther(double other) {
    this.other = other;
  }

  public void setTotalExpend(double totalExpend) {
    this.totalExpend = totalExpend;
  }

  public void setGrossProfit(double grossProfit) {
    this.grossProfit = grossProfit;
  }

  public void setGrossProfitPercent(String grossProfitPercent) {
    this.grossProfitPercent = grossProfitPercent;
  }

  public double getMemberIncome() {
    return memberIncome;
  }

  public void setMemberIncome(double memberIncome) {
    this.memberIncome = memberIncome;
  }

  public double getOtherFee() {
    return otherFee;
  }

  public void setOtherFee(double otherFee) {
    this.otherFee = otherFee;
  }

  public double getOtherIncome() {
    return otherIncome;
  }

  public void setOtherIncome(double otherIncome) {
    this.otherIncome = otherIncome;
  }

  public void getInfoFromBusinessStatDTO(BusinessStatDTO businessStatDTO)
  {
    this.setProductCost(businessStatDTO.getProductCost());
    this.setStatSum(businessStatDTO.getStatSum());
    this.setSales(businessStatDTO.getSales());
    this.setService(businessStatDTO.getService());
    this.setWash(businessStatDTO.getWash());
    this.setMemberIncome(businessStatDTO.getMemberIncome());
    this.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost());
  }
  public void getInfoFromBusinessStatDTO(BusinessStatDTO businessStatDTO,BusinessStatDTO businessStatDTO2)
  {
    this.setProductCost(businessStatDTO2.getProductCost()-businessStatDTO.getProductCost());
    this.setStatSum(businessStatDTO2.getStatSum()-businessStatDTO.getStatSum());
    this.setSales(businessStatDTO2.getSales()-businessStatDTO.getSales());
    this.setService(businessStatDTO2.getService()-businessStatDTO.getService());
    this.setWash(businessStatDTO2.getWash()-businessStatDTO.getWash());
    this.setMemberIncome(businessStatDTO2.getMemberIncome()-businessStatDTO.getMemberIncome());
    this.setOrderOtherIncomeCost(businessStatDTO2.getOrderOtherIncomeCost() - businessStatDTO.getOrderOtherIncomeCost());
  }
  public void toFix(int num)
  {
    this.setGrossProfit(NumberUtil.round(this.getGrossProfit(),num));
    this.setLabor(NumberUtil.round(this.getLabor(),num));
    this.setTotalExpend(NumberUtil.round(this.getTotalExpend(),num));
    this.setOtherFee(NumberUtil.round(this.getOtherFee(),num));
    this.setOther(NumberUtil.round(this.getOther(),num));
    this.setProductCost(NumberUtil.round(this.getProductCost(),num));
    this.setOrderOtherIncomeCost(NumberUtil.round(this.getOrderOtherIncomeCost(),num));
    this.setRent(NumberUtil.round(this.getRent(),num));
    this.setSales(NumberUtil.round(this.getSales(),num));
    this.setService(NumberUtil.round(this.getService(),num));
    this.setStatSum(NumberUtil.round(this.getStatSum(),num));
    this.setWash(NumberUtil.round(this.getWash(),num));
    this.setMemberIncome(NumberUtil.round(this.getMemberIncome(),num));
    this.setOtherIncome(NumberUtil.round(this.getOtherIncome(),num));
  }

  public double getTotalExpendInfo(int num)
  {
    double totalExpend = 0;
    totalExpend = this.rent+this.labor+this.other+this.otherFee;
    totalExpend = NumberUtil.round(totalExpend,num);
    return totalExpend;
  }

  public double getOrderOtherIncomeCost() {
    return orderOtherIncomeCost;
  }

  public void setOrderOtherIncomeCost(double orderOtherIncomeCost) {
    this.orderOtherIncomeCost = orderOtherIncomeCost;
  }
}
