package com.bcgogo.report.service;

import com.bcgogo.report.model.ReportDaoManager;
import com.bcgogo.report.model.ReportWriter;
import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.WashOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

@Component
public class ReportService implements IReportService {

  @Autowired
  private ReportDaoManager reportDaoManager;

  @Override
  public double countShopCarRepairIncome(long shopId, long startTime, long endTime) {
    ReportWriter writer = reportDaoManager.getWriter();

    return writer.countShopCarRepairIncome(shopId, startTime, endTime);
  }

  @Override
  public double countShopCarWashingIncome(long shopId, long startTime, long endTime) {
    ReportWriter writer = reportDaoManager.getWriter();

    return writer.countShopCarWashingIncome(shopId, startTime, endTime);
  }

  @Override
  public double countShopSalesIncome(long shopId, long startTime, long endTime) {
    ReportWriter writer = reportDaoManager.getWriter();

    return writer.countShopSalesIncome(shopId, startTime, endTime);
  }

  @Override
  public double countShopPurchasingCost(long shopId, long startTime, long endTime) {
    ReportWriter writer = reportDaoManager.getWriter();

    return writer.countShopPurchasingCost(shopId, startTime, endTime);
  }
  public List<RepairOrder> countAgentAchievements(long shopId, long startTime, long endTime){
     ReportWriter writer = reportDaoManager.getWriter();

    return writer.countAgentAchievements(shopId, startTime, endTime);
  }

  public double countItemAgentAchievements(long repair_order_id, long startTime, long endTime){
      ReportWriter writer = reportDaoManager.getWriter();

    return writer.countItemAgentAchievements(repair_order_id, startTime, endTime);
  }
  public double countServiceAgentAchievements(long repair_order_id, long startTime, long endTime){
      ReportWriter writer = reportDaoManager.getWriter();

    return writer.countServiceAgentAchievements(repair_order_id, startTime, endTime);

  }
  public List<WashOrder> countWashAgentAchievements(long shopId, long startTime, long endTime){
    ReportWriter writer = reportDaoManager.getWriter();

    return writer.countWashAgentAchievements(shopId, startTime, endTime);
  }

  @Override
  public double countShopDiscount(long shopId, long startTime, long endTime) {
    ReportWriter writer = reportDaoManager.getWriter();

    return writer.countShopDiscount(shopId, startTime, endTime);
  }
}
