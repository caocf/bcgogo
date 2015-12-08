package com.bcgogo.stat.model;

import com.bcgogo.enums.stat.businessAccountStat.BusinessCategoryStatType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class StatWriter extends GenericWriterDao {

  public StatWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  //BizStat -----------------
  public void deleteBizStatByShopAndTypeAndYearAndMonthAndDayAndWeek(long shopId, String statType, long statYear, Long statMonth, Long statDay, Long statWeek) {
    Session session = this.getSession();

    try {
      Query q = SQL.deleteBizStatByShopAndTypeAndYearAndMonthAndDayAndWeek(session, shopId, statType, statYear, statMonth, statDay, statWeek);

      q.executeUpdate();
    } finally {
      release(session);
    }

  }

  public void deleteAssistantStat(long shopId, String assistant, long statYear, Long statMonth, Long statDay, Long statWeek) {
    Session session = this.getSession();

    try {
      Query q = SQL.deleteAssistantStat(session, shopId, statYear, statMonth, statDay, statWeek, assistant);

      q.executeUpdate();
    } finally {
      release(session);
    }

  }


  public List<BizStat> getShopEachYearStatByType(long shopId, String statType) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopEachYearStatByType(session, shopId, statType);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  public double getShopOneYearStatByTypeAndYear(long shopId, String statType, long statYear) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneYearStatByTypeAndYear(session, shopId, statType, statYear);

      List<BizStat> bizStatList = (List<BizStat>) q.list();

      if (bizStatList.size() > 0) {
        return bizStatList.get(0).getStatSum();
      }
      else return 0.0d;
    } finally {
      release(session);
    }

  }

  public List<BizStat> getShopOneYearStatByYear(long shopId, long statYear) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneYearStatByYear(session, shopId, statYear);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  public List<BizStat> getShopEachMonthInAYearStatByTypeAndYear(long shopId, String statType, long statYear) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopEachMonthInAYearStatByTypeAndYear(session, shopId, statType, statYear);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  public double getShopOneMonthStatByTypeAndYearAndMonth(long shopId, String statType, long statYear, long statMonth) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneMonthStatByTypeAndYearAndMonth(session, shopId, statType, statYear, statMonth);

      List<BizStat> bizStatList = (List<BizStat>) q.list();

      if (bizStatList.size() > 0) {
        return bizStatList.get(0).getStatSum();
      }
      else return 0.0d;
    } finally {
      release(session);
    }

  }

  public List<BizStat> getShopOneMonthStatByYearAndMonth(long shopId, long statYear, long statMonth) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneMonthStatByYearAndMonth(session, shopId, statYear, statMonth);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  public List<BizStat> getShopEachDayInAMonthStatByTypeAndYearAndMonth(long shopId, String statType, long statYear, long statMonth) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopEachDayInAMonthStatByTypeAndYearAndMonth(session, shopId, statType, statYear, statMonth);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  public double getShopOneDayStatByTypeAndYearAndMonthAndDay(long shopId, String statType, long statYear, long statMonth, long statDay) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneDayStatByTypeAndYearAndMonthAndDay(session, shopId, statType, statYear, statMonth, statDay);

      List<BizStat> bizStatList = (List<BizStat>) q.list();

      if (bizStatList.size() > 0) {
        return bizStatList.get(0).getStatSum();
      }
      else return 0.0d;
    } finally {
      release(session);
    }

  }

  public List<BizStat> getShopOneDayStatByYearAndMonthAndDay(long shopId, long statYear, long statMonth, long statDay) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneDayStatByYearAndMonthAndDay(session, shopId, statYear, statMonth, statDay);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  public List<BizStat> getShopEachWeekInAYearStatByTypeAndYear(long shopId, String statType, long statYear) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopEachWeekInAYearStatByTypeAndYear(session, shopId, statType, statYear);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  public double getShopOneWeekStatByTypeAndYearAndWeek(long shopId, String statType, long statYear, long statWeek) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneWeekStatByTypeAndYearAndWeek(session, shopId, statType, statYear, statWeek);

      List<BizStat> bizStatList = (List<BizStat>) q.list();

      if (bizStatList.size() > 0) {
        return bizStatList.get(0).getStatSum();
      }
      else return 0.0d;
    } finally {
      release(session);
    }

  }

  public List<BizStat> getShopOneWeekStatByYearAndWeek(long shopId, long statYear, long statWeek) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopOneWeekStatByYearAndWeek(session, shopId, statYear, statWeek);

      return (List<BizStat>) q.list();
    } finally {
      release(session);
    }

  }

  //BizStat end -----------


  //CustomerStat -------------
  public void deleteCustomerStatByShopAndType(long shopId, String customerType) {
    Session session = this.getSession();

    try {
      Query q = SQL.deleteCustomerStatByShopAndType(session, shopId, customerType);

      q.executeUpdate();
    } finally {
      release(session);
    }

  }

  public List<CustomerStat> getShopCustomerStat(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerStat(session, shopId);

      return (List<CustomerStat>) q.list();
    } finally {
      release(session);
    }

  }

  public List<CustomerStat> getShopCustomerStatByType(long shopId, String customerType) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerStatByType(session, shopId, customerType);

      return (List<CustomerStat>) q.list();
    } finally {
      release(session);
    }

  }

  //CustomerStat end ------------

    /**
   * 根据日期查询服务车辆数
   * @author zhangchuanlong
   * @param shopId
   * @param serviceTime
   * @return
   */
   public List  getServiceVehicleCountByTime(long shopId,long serviceTime)
   {
      Session session = this.getSession();

    try {
      Query q = SQL.getServiceVehicleCountByTime(session, shopId, serviceTime);
             List cout=q.list();
        if(cout.size()==0||cout==null)
        {
            return null;
        }
        else
        {
            return cout;
        }
    } finally {
      release(session);
    }
   }
  public List<AssistantStat> getAssistantMonth(long shopId, long statYear, long startMonth,long endMonth,int pageNo,int pageSize) {
    Session session = this.getSession();

    try {
      Query q = SQL.getAssistantMonth(session, shopId, statYear, startMonth, endMonth,pageNo,pageSize);

      return (List<AssistantStat>) q.list();
    } finally {
      release(session);
    }

  }

  public long countAssistantByMonth(long shopId,int statYear,int statMonth) {
    Session session = getSession();
    try {
      Query q = SQL.countAssistantByMonth(session, shopId,statYear,statMonth);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public void deleteAllAssistantStat(long shopId, int statYear, Integer statMonth, Integer statDay, Integer week) {
    Session session = this.getSession();

    try {
      Query q = SQL.deleteAllAssistantStat(session, shopId, statYear, statMonth, statDay,week);
      q.executeUpdate();
    } finally {
      release(session);
    }

  }

  public List<BusinessAccount>  getBusinessAccount(Long shopId ,BusinessAccountSearchConditionDTO searchConditionDTO)
  {
      Session session = this.getSession();

    try {
      Query q = SQL.getBusinessAccount( session, shopId , searchConditionDTO) ;

      return (List<BusinessAccount>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据查询条件统计营业记账数量
   * @param shopId
   * @param searchConditionDTO
   * @return
   */
  public List<String> countBusinessAccounts(Long shopId,BusinessAccountSearchConditionDTO searchConditionDTO) {
    Session session = this.getSession();
    List<String> strings = new ArrayList<String>();
    try {
      Query q = SQL.countBusinessAccounts(session, shopId, searchConditionDTO);

      List list = q.list();

      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null) {
          strings.add(array[0].toString());
          strings.add(array[1].toString());
        }
      }
      if (CollectionUtils.isEmpty(strings)) {
        strings.add("0");
        strings.add("0");
      }

      return strings;
    } finally {
      release(session);
    }
  }

  /**
   * 根据查询条件取得统计金额
   * @param shopId
   * @return
   */
  public Double  countBusinessAccountsExpenses(Long shopId ,BusinessAccountSearchConditionDTO searchConditionDTO)
  {
    Session session = this.getSession();
    try {
      Query q = SQL.countBusinessAccountExpenses(session, shopId, searchConditionDTO) ;
      if (q.uniqueResult() == null) {
        return 0d;
      }
      return ((Double) q.uniqueResult()).doubleValue();
    } finally {
      release(session);
    }
  }

  /**
   * BusinessCategory 小型记账类别表 查询
   * @param shopId
   * @param itemType
   * @return
   */
  public List<BusinessCategory> getBusinessCategoryByItemType(Long shopId, String itemType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBusinessCategory(session, shopId, itemType);
      List<BusinessCategory> list = q.list();
      return list;
    } finally {
      release(session);
    }

  }

  /**
   * 根据店面ID和营业分类名称查询记账类别表（Business_category）
   * @param shopId
   * @param itemName
   * @return
   */
  public List<BusinessCategory> getBusinessCategoryByItemName(Long shopId, String itemName) {
    Session session = this.getSession();
    try {
      BusinessCategory businessCategory = null;
      Query q = SQL.getBusinessCategoryByItemName(session, shopId, itemName);
      List<BusinessCategory> list = q.list();
      return list;
    } finally {
      release(session);
    }

  }


  public List<BusinessCategoryStat> getBusinessCategoryStat(Long shopId,Long businessCategoryId,Long statYear,Long statMonth,Long statDay,BusinessCategoryStatType statType,MoneyCategory moneyCategory) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBusinessCategoryStat(session, shopId, businessCategoryId,statYear,statMonth,statDay,statType,moneyCategory);
      return q.list();
    } finally {
      release(session);
    }

  }

  public List<BusinessCategoryStat> getBusinessCategoryStatForBusinessStat(Long shopId,Long statYear,Long statMonth,Long statDay) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBusinessCategoryStatForBusinessStat(session, shopId, statYear, statMonth, statDay);
      return q.list();
    } finally {
      release(session);
    }

  }

  /**
   * BusinessCategory 小型记账类别表 查询
   *
   * @param shopId
   * @param itemType
   * @return
   */
  public List<BusinessCategory> getBusinessCategoryLikeItemName(Long shopId, String itemType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBusinessCategoryLikeItemName(session, shopId, itemType);
      List<BusinessCategory> list = q.list();
      return list;
    } finally {
      release(session);
    }

  }

  public List<BusinessAccount> getBusinessAccountForAssistantStat(Long shopId, Long editDateStart, Long editDateEnd) {
    Session session = this.getSession();

    try {
      Query q = SQL.getBusinessAccountForAssistantStat(session, shopId, editDateStart, editDateEnd);

      return (List<BusinessAccount>) q.list();
    } finally {
      release(session);
    }
  }

}
