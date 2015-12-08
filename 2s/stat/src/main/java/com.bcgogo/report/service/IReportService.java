package com.bcgogo.report.service;

import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.WashOrder;

import java.util.List;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

public interface IReportService {

  public double countShopCarRepairIncome(long shopId, long startTime, long endTime);

  public double countShopCarWashingIncome(long shopId, long startTime, long endTime);

  public double countShopSalesIncome(long shopId, long startTime, long endTime);

  public double countShopPurchasingCost(long shopId, long startTime, long endTime);

  public double countShopDiscount(long shopId, long startTime, long endTime);

  public List<RepairOrder> countAgentAchievements(long shopId, long startTime, long endTime);

  public double countItemAgentAchievements(long shopId, long startTime, long endTime);

  public double countServiceAgentAchievements(long shopId, long startTime, long endTime);

  public List<WashOrder> countWashAgentAchievements(long shopId, long startTime, long endTime);
}
