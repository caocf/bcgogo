package com.bcgogo.stat.service;

import com.bcgogo.stat.dto.AssistantStatDTO;
import com.bcgogo.stat.dto.BizStatDTO;

import java.util.List;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

public interface IBizStatService {

  public BizStatDTO saveBizStat(BizStatDTO bizStatDTO);

  public BizStatDTO getBizStatById(long bizStatId);

  public List<BizStatDTO> getShopEachYearStatByType(long shopId, String statType);

  public double getShopOneYearStatByTypeAndYear(long shopId, String statType, long statYear);

  public List<BizStatDTO> getShopOneYearStatByYear(long shopId, long statYear);

  public List<BizStatDTO> getShopEachMonthInAYearStatByTypeAndYear(long shopId, String statType, long statYear);

  public double getShopOneMonthStatByTypeAndYearAndMonth(long shopId, String statType, long statYear, long statMonth);

  public List<BizStatDTO> getShopOneMonthStatByYearAndMonth(long shopId, long statYear, long statMonth);

  public List<BizStatDTO> getShopEachDayInAMonthStatByTypeAndYearAndMonth(long shopId, String statType, long statYear, long statMonth);

  public double getShopOneDayStatByTypeAndYearAndMonthAndDay(long shopId, String statType, long statYear, long statMonth, long statDay);

  public List<BizStatDTO> getShopOneDayStatByYearAndMonthAndDay(long shopId, long statYear, long statMonth, long statDay);

  public List<BizStatDTO> getShopEachWeekInAYearStatByTypeAndYear(long shopId, String statType, long statYear);

  public double getShopOneWeekStatByTypeAndYearAndWeek(long shopId, String statType, long statYear, long statWeek);

  public List<BizStatDTO> getShopOneWeekStatByYearAndWeek(long shopId, long statYear, long statWeek);

  public double getInventoryTotalAmountByShopId(long shopId) throws Exception;

  public List<AssistantStatDTO> getAssistantMonth(long shopId, long statYear, long startMonth,long endMonth,int rowStart, int pageSize);

  public List<AssistantStatDTO> saveAssistantStat(List<AssistantStatDTO> assistantStatList);

  public long countAssistantByMonth(long shopId,int statYear,int statMonth);

  public void countAssistant(long shopId, int statYear, Integer statMonth, Integer statDay, Integer week, long startTime, long endTime);

  public void deleteAllAssistantStat(long shopId, int statYear, Integer statMonth, Integer statDay, Integer week);


}
