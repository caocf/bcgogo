package com.bcgogo.stat.model;

import com.bcgogo.enums.BusinessAccountEnum;
import com.bcgogo.enums.stat.businessAccountStat.BusinessCategoryStatType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class SQL {

  //**营业统计BizStat开始**

  /**
   * 根据店面Id、统计类型、年月日、周数删除营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @param statMonth
   * @param statDay
   * @param statWeek
   * @return
   */
  public static Query deleteBizStatByShopAndTypeAndYearAndMonthAndDayAndWeek(Session session, long shopId, String statType, long statYear, Long statMonth, Long statDay, Long statWeek) {
    StringBuilder sb = new StringBuilder("delete from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear");
    sb.append(" and bs.statMonth" + (statMonth == null ? " is null" : " = " + statMonth.toString()));
    sb.append(" and bs.statDay" + (statDay == null ? " is null" : " = " + statDay.toString()));
    sb.append(" and bs.statWeek" + (statWeek == null ? " is null" : " = " + statWeek.toString()));
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setParameter("statYear", statYear);
  }


  public static Query deleteAssistantStat(Session session, long shopId, long statYear, Long statMonth, Long statDay, Long statWeek, String assistant) {
    StringBuilder sb = new StringBuilder("delete from AssistantStat as bs where bs.shopId = :shopId and bs.statYear = :statYear");
    sb.append(" and bs.statMonth" + (statMonth == null ? " is null" : " = " + statMonth.toString()));
    sb.append(" and bs.statDay" + (statDay == null ? " is null" : " = " + statDay.toString()));
    sb.append(" and bs.statWeek" + (statWeek == null ? " is null" : " = " + statWeek.toString()));
    if (assistant == null || assistant.equals("")) {
      sb.append(" and bs.assistant is null ");
    } else {
      sb.append(" and bs.assistant = '" + assistant.toString() + "'");
    }
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameter("statYear", statYear);
  }

  /**
   * 根据店面Id、统计类型获取每年营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @return
   */
  public static Query getShopEachYearStatByType(Session session, long shopId, String statType) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear is not null and bs.statMonth is null and bs.statDay is null and bs.statWeek is null order by bs.statYear")
        .setLong("shopId", shopId)
        .setString("statType", statType);
  }

  /**
   * 根据店面Id、统计类型、年份获取当年营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @return
   */
  public static Query getShopOneYearStatByTypeAndYear(Session session, long shopId, String statType, long statYear) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear and bs.statMonth is null and bs.statDay is null and bs.statWeek is null")
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setLong("statYear", statYear);
  }

  /**
   * 根据店面Id、年份获取当年营业统计数据
   *
   * @param session
   * @param shopId
   * @param statYear
   * @return
   */
  public static Query getShopOneYearStatByYear(Session session, long shopId, long statYear) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statYear = :statYear and bs.statMonth is null and bs.statDay is null and bs.statWeek is null")
        .setLong("shopId", shopId)
        .setLong("statYear", statYear);
  }

  /**
   * 根据店面Id、统计类型、年份获取当年每月营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @return
   */
  public static Query getShopEachMonthInAYearStatByTypeAndYear(Session session, long shopId, String statType, long statYear) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear and bs.statMonth is not null and bs.statDay is null and bs.statWeek is null order by bs.statMonth")
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setLong("statYear", statYear);
  }

  /**
   * 根据店面Id、统计类型、年份、月份获取当年当月营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @param statMonth
   * @return
   */
  public static Query getShopOneMonthStatByTypeAndYearAndMonth(Session session, long shopId, String statType, long statYear, long statMonth) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear and bs.statMonth = :statMonth and bs.statDay is null and bs.statWeek is null")
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setLong("statYear", statYear)
        .setLong("statMonth", statMonth);
  }

  /**
   * 根据店面Id、年份、月份获取当年当月营业统计数据
   *
   * @param session
   * @param shopId
   * @param statYear
   * @param statMonth
   * @return
   */
  public static Query getShopOneMonthStatByYearAndMonth(Session session, long shopId, long statYear, long statMonth) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statYear = :statYear and bs.statMonth = :statMonth and bs.statDay is null and bs.statWeek is null")
        .setLong("shopId", shopId)
        .setLong("statYear", statYear)
        .setLong("statMonth", statMonth);
  }

  /**
   * 根据店面Id、统计类型、年份、月份获取当年当月每日营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @param statMonth
   * @return
   */
  public static Query getShopEachDayInAMonthStatByTypeAndYearAndMonth(Session session, long shopId, String statType, long statYear, long statMonth) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear and bs.statMonth = :statMonth and bs.statDay is not null and bs.statWeek is null order by bs.statDay")
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setLong("statYear", statYear)
        .setLong("statMonth", statMonth);
  }

  /**
   * 根据店面Id、统计类型、年份、月份、日期获取当年当月当日营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @param statMonth
   * @param statDay
   * @return
   */
  public static Query getShopOneDayStatByTypeAndYearAndMonthAndDay(Session session, long shopId, String statType, long statYear, long statMonth, long statDay) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear and bs.statMonth = :statMonth and bs.statDay = :statDay and bs.statWeek is null")
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setLong("statYear", statYear)
        .setLong("statMonth", statMonth)
        .setLong("statDay", statDay);
  }

  /**
   * 根据店面Id、年份、月份、日期获取当年当月当日营业统计数据
   *
   * @param session
   * @param shopId
   * @param statYear
   * @param statMonth
   * @param statDay
   * @return
   */
  public static Query getShopOneDayStatByYearAndMonthAndDay(Session session, long shopId, long statYear, long statMonth, long statDay) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statYear = :statYear and bs.statMonth = :statMonth and bs.statDay = :statDay and bs.statWeek is null")
        .setLong("shopId", shopId)
        .setLong("statYear", statYear)
        .setLong("statMonth", statMonth)
        .setLong("statDay", statDay);
  }

  /**
   * 根据店面Id、统计类型、年份获取当年各周营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @return
   */
  public static Query getShopEachWeekInAYearStatByTypeAndYear(Session session, long shopId, String statType, long statYear) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear and bs.statMonth is null and bs.statDay is null and bs.statWeek is not null order by bs.statWeek")
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setLong("statYear", statYear);
  }

  /**
   * 根据店面Id、统计类型、年份、周数获取当年当周营业统计数据
   *
   * @param session
   * @param shopId
   * @param statType
   * @param statYear
   * @param statWeek
   * @return
   */
  public static Query getShopOneWeekStatByTypeAndYearAndWeek(Session session, long shopId, String statType, long statYear, long statWeek) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statType = :statType and bs.statYear = :statYear and bs.statMonth is null and bs.statDay is null and bs.statWeek = :statWeek")
        .setLong("shopId", shopId)
        .setString("statType", statType)
        .setLong("statYear", statYear)
        .setLong("statWeek", statWeek);
  }

  /**
   * 根据店面Id、年份、周数获取当年当周营业统计数据
   *
   * @param session
   * @param shopId
   * @param statYear
   * @param statWeek
   * @return
   */
  public static Query getShopOneWeekStatByYearAndWeek(Session session, long shopId, long statYear, long statWeek) {
    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statYear = :statYear and bs.statMonth is null and bs.statDay is null and bs.statWeek = :statWeek")
        .setLong("shopId", shopId)
        .setLong("statYear", statYear)
        .setLong("statWeek", statWeek);
  }

  //**营业统计BizStat结束**

  //======================================================================================================================

  //**客户统计CustomerStat开始**

  /**
   * 根据店面Id、统计类型删除客户统计数据
   *
   * @param session
   * @param shopId
   * @param customerType
   * @return
   */
  public static Query deleteCustomerStatByShopAndType(Session session, long shopId, String customerType) {
    return session.createQuery("delete from CustomerStat as cs where cs.shopId = :shopId and cs.customerType = :customerType ")
        .setLong("shopId", shopId)
        .setString("customerType", customerType);
  }

  /**
   * 根据店面Id获取客户统计数据
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query getShopCustomerStat(Session session, long shopId) {
    return session.createQuery("select cs from CustomerStat as cs where cs.shopId = :shopId")
        .setLong("shopId", shopId);
  }

  /**
   * 根据店面Id、统计类型获取客户统计数据
   *
   * @param session
   * @param shopId
   * @param customerType
   * @return
   */
  public static Query getShopCustomerStatByType(Session session, long shopId, String customerType) {
    return session.createQuery("select cs from CustomerStat as cs where cs.shopId = :shopId and cs.customerType = :customerType")
        .setLong("shopId", shopId)
        .setString("customerType", customerType);
  }
  //**客户统计CustomerStat结束**

  /**
   * 根据日期查询服务车辆数
   *
   * @param session
   * @param shopId
   * @param serviceTime
   * @return
   * @author zhangchuanlong
   */
  public static Query getServiceVehicleCountByTime(Session session, long shopId, long serviceTime) {
    return session.createQuery("select svc from ServiceVehicleCount as svc where svc.shopId=:shopId and svc.serviceTime=:serviceTime")
        .setLong("shopId", shopId)
        .setLong("serviceTime", serviceTime);
  }

  public static Query getAssistantMonth(Session session, long shopId, long statYear, long startMonth, long endMonth, int pageNo, int pageSize) {
    if (startMonth == endMonth) {
      return session.createQuery("select bs from AssistantStat as bs where bs.shopId = :shopId and bs.statYear = :statYear " +
          "and bs.statWeek is null and bs.statDay is null and bs.statMonth = :statMonth and bs.assistant is not null order by bs.statSum desc ")
          .setLong("shopId", shopId)
          .setLong("statYear", statYear)
          .setLong("statMonth", startMonth)
          .setFirstResult(pageNo)
          .setMaxResults(pageSize);
    } else {
      return session.createQuery("select bs from AssistantStat as bs where bs.shopId = :shopId and bs.statYear = :statYear " +
          "and bs.statWeek is null and bs.statDay is null and bs.statMonth >= :startMonth and bs.statMonth <= :endMonth and bs.assistant is not null order by bs.statSum desc ")
          .setLong("shopId", shopId)
          .setLong("statYear", statYear)
          .setLong("startMonth", startMonth).setLong("endMonth", endMonth).setFirstResult(pageNo)
          .setMaxResults(pageSize);
    }

  }

  public static Query countAssistantByMonth(Session session, long shopId, long statYear, long statMonth) {
    return session.createQuery("select count(*) from AssistantStat as c where c.shopId = :shopId and c.statYear = :statYear " +
        "and c.statMonth = :statMonth and c.statDay is null and c.statWeek is null ")
        .setLong("shopId", shopId).setLong("statYear", statYear).setLong("statMonth", statMonth);
  }

  public static Query deleteAllAssistantStat(Session session, long shopId, int statYear, Integer statMonth, Integer statDay, Integer statWeek) {
    StringBuilder sb = new StringBuilder("delete from AssistantStat as bs where bs.shopId = :shopId and bs.statYear = :statYear");
    sb.append(" and bs.statMonth" + (statMonth == null ? " is null" : " = " + statMonth.toString()));
    sb.append(" and bs.statDay" + (statDay == null ? " is null" : " = " + statDay.toString()));
    sb.append(" and bs.statWeek" + (statWeek == null ? " is null" : " = " + statWeek.toString()));
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameter("statYear", (long) statYear);
  }

  public static Query getBusinessAccount(Session session, Long shopId, BusinessAccountSearchConditionDTO searchConditionDTO) {
    StringBuilder sb = new StringBuilder("select ba from BusinessAccount as ba where ba.shopId = :shopId");
    if (searchConditionDTO.getStartTime() != null) {
      sb.append(" and ba.editDate >= :editDateStart");
    }
    if (searchConditionDTO.getEndTime() != null) {
      sb.append(" and ba.editDate <= :editDateEnd");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getAccountCategory())) {
      sb.append(" and ba.accountCategory =:accountCategory");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDocNo())) {
      sb.append(" and ba.docNo like:docNo");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDept())) {
      sb.append(" and ba.dept like:dept");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getPerson())) {
      sb.append(" and ba.person like:person");
    }
    if (searchConditionDTO.getAccountEnum() != null) {
      sb.append(" and ba.status =:status");
    }
    if (searchConditionDTO.getMoneyCategory() != null) {
      sb.append(" and ba.moneyCategory =:moneyCategory");
    }
    if (searchConditionDTO.getBusinessCategoryId() != null) {
      sb.append(" and ba.businessCategoryId =:businessCategoryId");
    } else if (StringUtils.isNotEmpty(searchConditionDTO.getBusinessCategory())) {
      sb.append(" and ba.businessCategory like:businessCategory");
    }


    sb.append(" order by ba.editDate desc,last_update desc");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if (searchConditionDTO.getStartTime() != null) {
      query.setLong("editDateStart", searchConditionDTO.getStartTime());
    }
    if (searchConditionDTO.getEndTime() != null) {
      query.setLong("editDateEnd", searchConditionDTO.getEndTime());
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getAccountCategory())) {
      query.setString("accountCategory", searchConditionDTO.getAccountCategory());
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDocNo())) {
      query.setString("docNo", "%"+searchConditionDTO.getDocNo()+"%");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDept())) {
      query.setString("dept", "%"+searchConditionDTO.getDept()+"%");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getPerson())) {
      query.setString("person", "%"+searchConditionDTO.getPerson()+"%");
    }
    if (searchConditionDTO.getAccountEnum() != null) {
      query.setString("status", searchConditionDTO.getAccountEnum().getName());
    }
    if (searchConditionDTO.getMoneyCategory() != null) {
      query.setParameter("moneyCategory", searchConditionDTO.getMoneyCategory());
    }

    if (searchConditionDTO.getBusinessCategoryId() != null) {
      query.setLong("businessCategoryId", searchConditionDTO.getBusinessCategoryId());
    } else if (StringUtils.isNotEmpty(searchConditionDTO.getBusinessCategory())) {
      query.setString("businessCategory", "%" + searchConditionDTO.getBusinessCategory() + "%");
    }
    if (searchConditionDTO.getRowStart() != null) {
      query.setFirstResult(searchConditionDTO.getRowStart()).setMaxResults(searchConditionDTO.getMaxRows());
    }

    return query;
  }

  /**
   * 创建统计记账数量查询
   * @param session
   * @param shopId
   * @return
   */
  public static Query countBusinessAccounts(Session session, Long shopId,BusinessAccountSearchConditionDTO searchConditionDTO) {
    StringBuilder sb = new StringBuilder("select count(*),sum(ba.total) from BusinessAccount as ba where ba.shopId = :shopId");
    if (searchConditionDTO.getStartTime() != null) {
      sb.append(" and ba.editDate >=:editDateStart");
    }
    if (searchConditionDTO.getEndTime() != null) {
      sb.append(" and ba.editDate <=:editDateEnd");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getAccountCategory())) {
      sb.append(" and ba.accountCategory =:accountCategory");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDocNo())) {
      sb.append(" and ba.docNo like:docNo");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDept())) {
      sb.append(" and ba.dept like:dept");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getPerson())) {
      sb.append(" and ba.person like:person");
    }
    if (searchConditionDTO.getAccountEnum() != null) {
      sb.append(" and ba.status =:status");
    }
    if (searchConditionDTO.getMoneyCategory() != null) {
      sb.append(" and ba.moneyCategory =:moneyCategory");
    }

    if (searchConditionDTO.getBusinessCategoryId() != null) {
      sb.append(" and ba.businessCategoryId =:businessCategoryId");
    } else if (StringUtils.isNotEmpty(searchConditionDTO.getBusinessCategory())) {
      sb.append(" and ba.businessCategory like:businessCategory");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if (searchConditionDTO.getStartTime() != null) {
      query.setLong("editDateStart", searchConditionDTO.getStartTime());
    }
    if (searchConditionDTO.getEndTime() != null) {
      query.setLong("editDateEnd", searchConditionDTO.getEndTime());
    }

    if (StringUtils.isNotBlank(searchConditionDTO.getAccountCategory())) {
      query.setString("accountCategory", searchConditionDTO.getAccountCategory());
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDocNo())) {
      query.setString("docNo", "%" + searchConditionDTO.getDocNo() + "%");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDept())) {
      query.setString("dept", "%" + searchConditionDTO.getDept() + "%");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getPerson())) {
      query.setString("person", "%" + searchConditionDTO.getPerson() + "%");
    }
    if (searchConditionDTO.getAccountEnum() != null) {
      query.setString("status", searchConditionDTO.getAccountEnum().getName());
    }

    if (searchConditionDTO.getBusinessCategoryId() != null) {
      query.setLong("businessCategoryId", searchConditionDTO.getBusinessCategoryId());
    } else if (StringUtils.isNotEmpty(searchConditionDTO.getBusinessCategory())) {
      query.setString("businessCategory", "%" + searchConditionDTO.getBusinessCategory() + "%");
    }
    if (searchConditionDTO.getMoneyCategory() != null) {
      query.setParameter("moneyCategory", searchConditionDTO.getMoneyCategory());
    }
    return query;
  }

  /**
   *  统计记账金额
   * @param session
   * @param shopId
   * @return
   */
  public static Query countBusinessAccountExpenses(Session session, Long shopId, BusinessAccountSearchConditionDTO searchConditionDTO) {
    StringBuilder sb = new StringBuilder("select sum(ba.total) from BusinessAccount as ba where ba.shopId =:shopId");
    if (searchConditionDTO.getStartTime() != null) {
      sb.append(" and ba.editDate >=:editDateStart");
    }
    if (searchConditionDTO.getEndTime() != null) {
      sb.append(" and ba.editDate <=:editDateEnd");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getAccountCategory())) {
      sb.append(" and ba.accountCategory =:accountCategory");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDocNo())) {
      sb.append(" and ba.docNo like:docNo");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDept())) {
      sb.append(" and ba.dept like:dept");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getPerson())) {
      sb.append(" and ba.person like:person");
    }
    if (searchConditionDTO.getAccountEnum() != null) {
      sb.append(" and ba.status =:status");
    }

    if (searchConditionDTO.getMoneyCategory() != null) {
      sb.append(" and ba.moneyCategory =:moneyCategory");
    }

    if (searchConditionDTO.getBusinessCategoryId() != null) {
      sb.append(" and ba.businessCategoryId =:businessCategoryId");
    } else if (StringUtils.isNotEmpty(searchConditionDTO.getBusinessCategory())) {
      sb.append(" and ba.businessCategory like:businessCategory");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if (searchConditionDTO.getStartTime() != null) {
      query.setLong("editDateStart", searchConditionDTO.getStartTime());
    }
    if (searchConditionDTO.getEndTime() != null) {
      query.setLong("editDateEnd", searchConditionDTO.getEndTime());
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getAccountCategory())) {
      query.setString("accountCategory", searchConditionDTO.getAccountCategory());
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDocNo())) {
      query.setString("docNo", "%" + searchConditionDTO.getDocNo() + "%");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getDept())) {
      query.setString("dept", "%" + searchConditionDTO.getDept() + "%");
    }
    if (StringUtils.isNotBlank(searchConditionDTO.getPerson())) {
      query.setString("person", "%" + searchConditionDTO.getPerson() + "%");
    }
    if (searchConditionDTO.getAccountEnum() != null) {
      query.setString("status", searchConditionDTO.getAccountEnum().getName());
    }
    if (searchConditionDTO.getMoneyCategory() != null) {
      query.setParameter("moneyCategory", searchConditionDTO.getMoneyCategory());
    }

    if (searchConditionDTO.getBusinessCategoryId() != null) {
      query.setLong("businessCategoryId", searchConditionDTO.getBusinessCategoryId());
    } else if (StringUtils.isNotEmpty(searchConditionDTO.getBusinessCategory())) {
      query.setString("businessCategory", "%" + searchConditionDTO.getBusinessCategory() + "%");
    }

    return query;
  }

  public static Query getBusinessCategory(Session session, Long shopId, String itemType) {
    StringBuffer sb = new StringBuffer(" from BusinessCategory bc where 1=1 ");
    sb.append(" and  bc.shopId =:shopId ");
    if (StringUtils.isNotBlank(itemType)) {
      sb.append(" and bc.itemType =:itemType");
    }
    sb.append(" order by useTime desc ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (StringUtils.isNotBlank(itemType)) {
      query.setString("itemType", itemType);
    }
    return query;

  }


  public static Query getBusinessCategoryLikeItemName(Session session, Long shopId, String itemName) {
    StringBuffer sb = new StringBuffer(" from BusinessCategory bc where 1=1 ");
    sb.append(" and  bc.shopId =:shopId  ");


    if (StringUtils.isNotEmpty(itemName)) {
      sb.append(" and bc.itemName like:itemName ");
    }
    sb.append(" order by useTime desc ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (StringUtils.isNotEmpty(itemName)) {
      query.setString("itemName", "%" + itemName + "%");
    }
    return query;
  }

  public static Query getBusinessCategoryByItemName(Session session, Long shopId, String itemName) {
    StringBuffer sb = new StringBuffer(" from BusinessCategory bc where 1=1 ");
    sb.append(" and  bc.shopId =:shopId ");
    if (StringUtils.isNotEmpty(itemName)) {
      sb.append(" and bc.itemName =:itemName ");
    }
    sb.append(" order by useTime desc ");
    Query query = session.createQuery(sb.toString());

    query.setLong("shopId", shopId);
    if (StringUtils.isNotEmpty(itemName)) {
      query.setString("itemName", itemName);
    }
    return query;
  }

  public static Query getBusinessCategoryStat(Session session, Long shopId,Long businessCategoryId,Long statYear,Long statMonth,Long statDay,BusinessCategoryStatType statType,MoneyCategory moneyCategory) {
    StringBuffer sb = new StringBuffer(" from BusinessCategoryStat bc where 1=1 ");

    sb.append(" and bc.shopId =:shopId and bc.businessCategoryId =:businessCategoryId ");
    sb.append(" and bc.statType =:statType and bc.moneyCategory =:moneyCategory ");

    if (statType == BusinessCategoryStatType.DAY) {
      sb.append(" and bc.statDay =:statDay and statMonth=:statMonth and statYear=:statYear ");
    } else if (statType == BusinessCategoryStatType.MONTH) {
      sb.append(" and statMonth=:statMonth and statYear=:statYear ");
    } else if (statType == BusinessCategoryStatType.YEAR) {
      sb.append(" and statYear=:statYear ");
    }

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setLong("businessCategoryId", businessCategoryId);
    query.setParameter("statType", statType);
    query.setParameter("moneyCategory", moneyCategory);

    if (statType == BusinessCategoryStatType.DAY) {
      query.setLong("statDay", statDay);
      query.setLong("statMonth", statMonth);
      query.setLong("statYear", statYear);
    } else if (statType == BusinessCategoryStatType.MONTH) {
      query.setLong("statMonth", statMonth);
      query.setLong("statYear", statYear);
    } else if (statType == BusinessCategoryStatType.YEAR) {
      query.setLong("statYear", statYear);
    }

    return query;
  }

  public static Query getBusinessCategoryStatForBusinessStat(Session session, Long shopId,Long statYear,Long statMonth,Long statDay) {
    StringBuffer sb = new StringBuffer(" from BusinessCategoryStat bc where 1=1 ");

    sb.append(" and bc.shopId =:shopId  ");
    sb.append(" and statYear=:statYear and total > 0 ");

    sb.append(" and (  " +
        "    ( statMonth =:statMonth and statType = 'MONTH' ) " +
        " or ( statMonth =:statMonth and statType='DAY' and statDay =:statDay ) " +
        " or (statType ='YEAR' ) " +
        ")  ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);

    query.setLong("statDay", statDay);
    query.setLong("statMonth", statMonth);
    query.setLong("statYear", statYear);

    return query;
  }


  public static Query getBusinessAccountForAssistantStat(Session session, Long shopId, Long editDateStart, Long editDateEnd) {
    StringBuilder sb = new StringBuilder("select ba from BusinessAccount as ba where ba.shopId = :shopId");
    if (editDateStart != null) {
      sb.append(" and ba.editDate >= :editDateStart");
    }
    if (editDateEnd != null) {
      sb.append(" and ba.editDate <= :editDateEnd");
    }
    sb.append(" and ba.status =:status");
    sb.append(" and ba.moneyCategory =:moneyCategory and ba.salesManId is not null ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if (editDateStart != null) {
      query.setLong("editDateStart", editDateStart);
    }
    if (editDateEnd != null) {
      query.setLong("editDateEnd", editDateEnd);
    }
    query.setParameter("moneyCategory", MoneyCategory.income);

    query.setParameter("status", BusinessAccountEnum.STATUS_SAVE.getName());

    return query;
  }


}
