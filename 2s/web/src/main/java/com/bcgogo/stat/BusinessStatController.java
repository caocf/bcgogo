package com.bcgogo.stat;

import com.bcgogo.common.Pager;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.BusinessAccountEnum;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.stat.businessAccountStat.BusinessCategoryStatType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BizStatPrintDTO;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import com.bcgogo.stat.dto.BusinessCategoryStatDTO;
import com.bcgogo.stat.model.BusinessCategoryStat;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.IBusinessStatService;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.model.MemberCard;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * 营业统计专用controller
 * Created by IntelliJ IDEA.
 * User: LiuWei
 * Date: 12-5-7
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/businessStat.do")
public class BusinessStatController {
  private static final Log LOG = LogFactory.getLog(BusinessStatController.class);

  public static final int PAGE_SIZE = 25;       //页面显示条数
  public static final int DEFAULT_PAGE_NO = 1;  //默认查询页码 1
  public static final int STRING_SIZE  = 2;//默认查询到的list 大小为2
  public static final int DEFAULT_SIZE  = 0;//默认大小为0
  public static final String QUERY_TYPE_DAY = "day"; //营业统计按天查询
  public static final String QUERY_TYPE_MONTH = "month";//营业统计按月查询
  public static final String QUERY_TYPE_YEAR = "year"; //营业统计按年查询


  /**
   * @param model   model
   * @param request request
   * @return 用于向页面跳转 什么事情都不做
   */
  @RequestMapping(params = "method=getBusinessStat")
  public String BizStat(ModelMap model, HttpServletRequest request) {
    model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
    return "stat/businessStatistics";
  }

  /**
   * 营业统计页面：用户显示当天、当月、当年数据
   *
   * @param request  ：
   * @param response
   * @param modelMap
   * @param year     所要统计的年份
   * @param month    所要统计的月份
   * @param day      所要统计的日期
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=customerResponse")
  public void customerResponse(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Integer year, Integer month, Integer day) throws BcgogoException {

    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      return;
    }

    if(year == null || month == null || day == null) {
      LOG.error("/businessStat.do method=customerResponse");
      LOG.error("营业统计:查询日期为空，默认为当前系统时间");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      year = DateUtil.getCurrentYear();
      month = DateUtil.getCurrentMonth();
      day = DateUtil.getCurrentDay();
    }


    String jsonStr = "";
    try {
      //返回结果的list
      List<BusinessStatDTO> resultList = new ArrayList<BusinessStatDTO>();

      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
      //今天的数据
      List<BusinessStatDTO> businessStatDTOList = txnService.getBusinessStatByYearMonthDay(shopId, year, month, day);

      //昨天的数据
      List<BusinessStatDTO> yesDayBusinessStatDTOList = null;

      //上个月的数据
      List<BusinessStatDTO> lastMonthList = null;

      //上个月的最后一天
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(year, month - 1, day);
      calendar.add(Calendar.MONTH, -1);
      int latYear = calendar.get(Calendar.YEAR);
      int lastMonth = calendar.get(Calendar.MONTH) + 1;
      int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

      lastMonthList = txnService.getBusinessStatByYearMonthDay(shopId, latYear, lastMonth, lastDayOfCurrentMonth);
      if (CollectionUtils.isEmpty(lastMonthList)) {
        //上个月最后一天没有数据，拿到上个月的最新数据
        lastMonthList = txnService.getLatestBusinessStatMonth(shopId, year, month - 1, 1);
        if (CollectionUtils.isEmpty(lastMonthList)) {
          lastMonthList = new ArrayList<BusinessStatDTO>();
          //说明上个月以前没有数据 虚拟一条数据 保存 作为上个月的数据
          BusinessStatDTO businessStatDTO = new BusinessStatDTO();
          businessStatDTO.setShopId(shopId);
          businessStatDTO.setStatYear(year.longValue());
          businessStatDTO.setStatMonth((long)lastMonth);
          businessStatDTO.setStatDay((long) lastDayOfCurrentMonth);
          businessStatDTO.setStatTime(System.currentTimeMillis());
          lastMonthList.add(businessStatDTO);
        }
      }
      if (day != 1) {
        yesDayBusinessStatDTOList = txnService.getBusinessStatByYearMonthDay(shopId, year, month, day - 1);
      } else {
        yesDayBusinessStatDTOList = lastMonthList;
      }

      List<BusinessStatDTO> thisYearStatList = txnService.getLatestBusinessStat(shopId, (long)year, 1);
      List<BusinessStatDTO> lastYearList = txnService.getLatestBusinessStat(shopId, (long) (year - 1), 1);

      //今天没有统计数据 代表今天没有做单
      if (CollectionUtils.isEmpty(businessStatDTOList)) {
        //添加一个今天的空数据
        BusinessStatDTO businessStatDTO = new BusinessStatDTO();
        businessStatDTO.setShopId(shopId);
        businessStatDTO.setStatYear(year.longValue());
        businessStatDTO.setStatMonth(month.longValue());
        businessStatDTO.setStatDay((long) day);
        businessStatDTO.setStatTime(System.currentTimeMillis());
        resultList.add(businessStatDTO);
      } else {
        BusinessStatDTO todayDto = businessStatDTOList.get(0);
        //查询日期有数据
        //拿到查询日期昨天的数据，判断昨天是否有数据 昨天没有数据 拿到除了查询日期之外的最新数据
        if (CollectionUtils.isEmpty(yesDayBusinessStatDTOList)) {
          //默认昨天的数据为 0
          resultList.add(todayDto);//把今天的数据存进去作为今天的营业额
        } else {
          BusinessStatDTO businessStatDTO = yesDayBusinessStatDTOList.get(0);
          BusinessStatDTO todayBusinessStatDTO = new BusinessStatDTO();
          todayBusinessStatDTO.setShopId(shopId);
          todayBusinessStatDTO.setStatYear(year.longValue());
          todayBusinessStatDTO.setStatMonth(month.longValue());
          todayBusinessStatDTO.setStatDay(day.longValue());

          todayBusinessStatDTO = businessStatService.calculateBusinessStat(todayDto,businessStatDTO,true);

          resultList.add(todayBusinessStatDTO);
        }
      }

      //这个月的数据
      if (lastMonthList != null && lastMonthList.size() == 1) {
        //上个月有数据
        BusinessStatDTO lastMonthStatDTO = lastMonthList.get(0);
        List<BusinessStatDTO> thisMonthList = txnService.getLatestBusinessStatMonth(shopId, year, month, 1);
        if (thisMonthList != null && thisMonthList.size() == 1) {
          BusinessStatDTO thisMonthStatDTO = thisMonthList.get(0);
          BusinessStatDTO thisMonthDTO = new BusinessStatDTO();
          thisMonthDTO.setShopId(shopId);
          thisMonthDTO.setStatYear(year.longValue());
          thisMonthDTO.setStatMonth(month.longValue());
          thisMonthDTO.setStatDay((long) -1);

          thisMonthDTO = businessStatService.calculateBusinessStat(thisMonthStatDTO,lastMonthStatDTO,true);

          resultList.add(thisMonthDTO);

        } else {
          //这个月没有数据，
          BusinessStatDTO businessStatDTO = new BusinessStatDTO();
          businessStatDTO.setShopId(shopId);
          businessStatDTO.setStatYear(year.longValue());
          businessStatDTO.setStatMonth(month.longValue());
          businessStatDTO.setStatDay((long) -1);
          resultList.add(businessStatDTO);
        }
      }
      if (CollectionUtils.isEmpty(thisYearStatList)) {
        //这个年没有数据
        BusinessStatDTO businessStatDTO = new BusinessStatDTO();
        businessStatDTO.setShopId(shopId);
        businessStatDTO.setStatYear(year.longValue());
        businessStatDTO.setStatMonth((long) -1);
        businessStatDTO.setStatDay((long) -1);
        resultList.add(businessStatDTO);
      } else {

        if (CollectionUtils.isEmpty(lastYearList)) {
          resultList.add(thisYearStatList.get(0));
        } else {
          BusinessStatDTO thisYearDTO = thisYearStatList.get(0);
          BusinessStatDTO lastYearDTO = lastYearList.get(0);
          thisYearDTO = businessStatService.calculateBusinessStat(thisYearDTO,lastYearDTO,true);
          resultList.add(thisYearDTO);
        }
      }

      mergeBusinessStatAndBusinessStatChange(resultList, shopId, year, month, day);

//      //计算营业外支出总和
//      for(BusinessStatDTO businessStatDTO : resultList) {
//        businessStatDTO.setOtherExpenditureTotal(businessStatDTO.getRentExpenditure() + businessStatDTO.getSalaryExpenditure()
//            + businessStatDTO.getUtilitiesExpenditure() + businessStatDTO.getOtherExpenditure());
//      }

      jsonStr = JsonUtil.listToJsonNoQuote(resultList);

    } catch (Exception ex) {
      LOG.error("/businessStat.do method=customerResponse");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      LOG.error(ex.getMessage(), ex);
    }

    //从后台没有去到数据。虚拟数据 0
    if (StringUtil.isEmpty(jsonStr)) {
      BusinessStatDTO errorBusinessStatDTO = new BusinessStatDTO();
      List<BusinessStatDTO> errorList = new ArrayList<BusinessStatDTO>();
      errorList.add(errorBusinessStatDTO);
      errorList.add(errorBusinessStatDTO);
      errorList.add(errorBusinessStatDTO);
      jsonStr = JsonUtil.listToJsonNoQuote(errorList);
    }

    modelMap.addAttribute("jsonStr", jsonStr);
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/businessStat.do method=customerResponse");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      LOG.error(e.getMessage(), e);
    }
  }

  //merge BusinessStat 与BusinessStatChange的 statSum
  private void mergeBusinessStatAndBusinessStatChange(List<BusinessStatDTO> businessStatDTOs, Long shopId, int year, int month, int day) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);

    IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);

    if (businessStatDTOs == null || businessStatDTOs.size() != 3) {
      throw new Exception("mergeBusinessStatList For Year Month And Day illegal");
    }
    //天
    BusinessStatDTO businessStatOfToday = businessStatDTOs.get(0);
    BusinessStatDTO businessStatChangeDTO = txnService.getBusinessStatChangeOfDay(shopId, year, month, day);
    if (businessStatChangeDTO != null) {
      businessStatOfToday = businessStatService.calculateBusinessStat(businessStatOfToday,businessStatChangeDTO,false);

    }
    businessStatOfToday.setOtherIncome(0d);
    businessStatOfToday.setOtherExpenditureTotal(0d);
    //月
    BusinessStatDTO businessStatOfMonth = businessStatDTOs.get(1);
    businessStatChangeDTO = txnService.sumBusinessStatChangeForMonth(shopId, year, month);
    if (businessStatChangeDTO != null) {
      businessStatOfMonth = businessStatService.calculateBusinessStat(businessStatOfMonth,businessStatChangeDTO,false);
    }
    businessStatOfMonth.setOtherIncome(0d);
    businessStatOfMonth.setOtherExpenditureTotal(0d);
    //年
    BusinessStatDTO businessStatOfYear = businessStatDTOs.get(2);
    businessStatChangeDTO = txnService.sumBusinessStatChangeForYear(shopId, year);
    if (businessStatChangeDTO != null) {
      businessStatOfYear = businessStatService.calculateBusinessStat(businessStatOfYear,businessStatChangeDTO,false);
    }
    businessStatOfYear.setOtherIncome(0d);
    businessStatOfYear.setOtherExpenditureTotal(0d);

    List<BusinessCategoryStatDTO> businessCategoryStatDTOList = businessAccountService.getBusinessCategoryStatForBusinessStat(shopId,(long)year,(long)month,(long)day);
    if(CollectionUtils.isEmpty(businessCategoryStatDTOList)){
      return;
    }

    Map<MoneyCategory,Map<Long,BusinessCategoryStatDTO>> moneyCategoryListMap = new HashMap<MoneyCategory, Map<Long,BusinessCategoryStatDTO>>();

    for(BusinessCategoryStatDTO businessCategoryStatDTO :businessCategoryStatDTOList) {
      double total = NumberUtil.doubleVal(businessCategoryStatDTO.getTotal());
      MoneyCategory moneyCategory = businessCategoryStatDTO.getMoneyCategory();
      BusinessCategoryStatType statType = businessCategoryStatDTO.getStatType();
      Long businessCategoryId = businessCategoryStatDTO.getBusinessCategoryId();

      if (total <= 0 || moneyCategory == null || statType == null || businessCategoryId == null) {
        continue;
      }
      Map<Long, BusinessCategoryStatDTO> map = moneyCategoryListMap.get(moneyCategory);

      if (map == null) {
        map = new HashMap<Long, BusinessCategoryStatDTO>();
      }
      BusinessCategoryStatDTO statDTO = map.get(businessCategoryId);
      if (statDTO == null) {
        statDTO = new BusinessCategoryStatDTO();
        statDTO.setBusinessCategory(businessCategoryStatDTO.getBusinessCategory());
        statDTO.setBusinessCategoryId(businessCategoryId);
      }
      if (statType == BusinessCategoryStatType.DAY) {
        statDTO.setDayTotal(NumberUtil.toReserve(NumberUtil.doubleVal(statDTO.getDayTotal()) + total, NumberUtil.MONEY_PRECISION));

        if (moneyCategory == MoneyCategory.income) {
          businessStatOfToday.setOtherIncome(NumberUtil.toReserve(NumberUtil.doubleVal(businessStatOfToday.getOtherIncome()) + total, NumberUtil.MONEY_PRECISION));
        } else {
          businessStatOfToday.setOtherExpenditureTotal(NumberUtil.toReserve(NumberUtil.doubleVal(businessStatOfToday.getOtherExpenditureTotal()) + total, NumberUtil.MONEY_PRECISION));
        }

      } else if (statType == BusinessCategoryStatType.MONTH) {
        statDTO.setMonthTotal(NumberUtil.toReserve(NumberUtil.doubleVal(statDTO.getMonthTotal()) + total, NumberUtil.MONEY_PRECISION));

        if (moneyCategory == MoneyCategory.income) {
          businessStatOfMonth.setOtherIncome(NumberUtil.toReserve(NumberUtil.doubleVal(businessStatOfMonth.getOtherIncome()) + total, NumberUtil.MONEY_PRECISION));
        } else {
          businessStatOfMonth.setOtherExpenditureTotal(NumberUtil.toReserve(NumberUtil.doubleVal(businessStatOfMonth.getOtherExpenditureTotal()) + total, NumberUtil.MONEY_PRECISION));
        }
      } else if (statType == BusinessCategoryStatType.YEAR) {
        statDTO.setYearTotal(NumberUtil.toReserve(NumberUtil.doubleVal(statDTO.getYearTotal()) + total, NumberUtil.MONEY_PRECISION));
        if (moneyCategory == MoneyCategory.income) {
          businessStatOfYear.setOtherIncome(NumberUtil.toReserve(NumberUtil.doubleVal(businessStatOfYear.getOtherIncome()) + total, NumberUtil.MONEY_PRECISION));
        } else {
          businessStatOfYear.setOtherExpenditureTotal(NumberUtil.toReserve(NumberUtil.doubleVal(businessStatOfYear.getOtherExpenditureTotal()) + total, NumberUtil.MONEY_PRECISION));
        }
      }
      map.put(businessCategoryStatDTO.getBusinessCategoryId(), statDTO);

//      for (int i = 0; i < 5; i++) {
//        map.put(businessCategoryStatDTO.getBusinessCategoryId() +i, statDTO);
//      }
      moneyCategoryListMap.put(businessCategoryStatDTO.getMoneyCategory(), map);
    }
    if(MapUtils.isNotEmpty(moneyCategoryListMap)) {
      if (MapUtils.isNotEmpty(moneyCategoryListMap.get(MoneyCategory.expenses))) {
        businessStatOfToday.setExpenditureList(moneyCategoryListMap.get(MoneyCategory.expenses).values());
      }
      if (MapUtils.isNotEmpty(moneyCategoryListMap.get(MoneyCategory.income))) {
        businessStatOfToday.setIncomeList(moneyCategoryListMap.get(MoneyCategory.income).values());


      }
    }

  }

  /**
   *
   * @param request
   * @param response
   * @param modelMap
   * @param type 查找类型
   * @param year  年份
   * @param month 月份
   * @param day   日期
   * @throws BcgogoException
   */
  @RequestMapping(params = "method=getDataTable")
  public void getDataTable(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, String type, Integer year, Integer month, Integer day) throws BcgogoException {
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) {
        return;
      }
      StringBuilder jsonStr = new StringBuilder();

      if (StringUtil.isEmpty(type)) {
        LOG.error("/businessStat.do method=getDataTable");
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error("year:" + year + ",month:" + month + ",day" + day + ",type:" + type);
        type = QUERY_TYPE_DAY;
      }
      if (year == null || month == null || day == null) {
        LOG.error("/businessStat.do method=getDataTable");
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error("year:" + year + ",month:" + month + ",day" + day + ",type:" + type);
        year = DateUtil.getCurrentYear();
        month = DateUtil.getCurrentMonth();
        day = DateUtil.getCurrentDay();
      }

      if (QUERY_TYPE_DAY.equals(type)) {
        List<BusinessStatDTO> dayList = this.getDayBusinessStatList(shopId, year, month);
        //merge day
        Map<Long, BusinessStatDTO> dayMap = txnService.getDayBusinessStatChangeMap(shopId, year, month);
        if (MapUtils.isNotEmpty(dayMap)) {
          if(CollectionUtils.isEmpty(dayList)) {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(year, month - 1, 1, 0, 0, 0);
            dayList = new ArrayList<BusinessStatDTO>();
            int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
            for (int index = 1; index <= lastDayOfCurrentMonth; index++) {
              BusinessStatDTO businessStatDTO = new BusinessStatDTO();
              businessStatDTO.setStatYear((long) year);
              businessStatDTO.setStatMonth((long) month);
              businessStatDTO.setStatDay((long) index);
              dayList.add(businessStatDTO);
            }
          }
          if (CollectionUtils.isNotEmpty(dayList)) {
            for (BusinessStatDTO bs : dayList) {
              BusinessStatDTO businessStatDTO = dayMap.get(bs.getStatDay());
              if (businessStatDTO != null) {
                bs.setStatSum(bs.getStatSum() + businessStatDTO.getStatSum());
                dayMap.remove(bs.getStatDay());
              }
            }
          }
          if (MapUtils.isNotEmpty(dayMap)) {
            dayList = new ArrayList<BusinessStatDTO>();
            for (Map.Entry<Long, BusinessStatDTO> set : dayMap.entrySet()) {
              dayList.add(set.getValue());
            }
          }
        }
        if (dayList == null || dayList.size() <= 0) {
          String str = "[]";
          jsonStr.append(str);
        }else{
          String str = JsonUtil.listToJsonNoQuote(dayList);
          jsonStr.append(str);
        }
      } else if (QUERY_TYPE_MONTH.equals(type)) {
        List<BusinessStatDTO> monthList = this.getMonthBusinessStatList(shopId, year);
        //merge month
        Map<Long, BusinessStatDTO> monthMap = txnService.getMonthBusinessStatChangeMap(shopId, year);
        if (MapUtils.isNotEmpty(monthMap)) {

          if(CollectionUtils.isEmpty(monthList)) {
            monthList = new ArrayList<BusinessStatDTO>();
            for (int index = 1; index <= 12; index++) {
              BusinessStatDTO businessStatDTO = new BusinessStatDTO();
              businessStatDTO.setStatYear((long) year);
              businessStatDTO.setStatMonth((long) index);
              monthList.add(businessStatDTO);
            }
          }

          if (CollectionUtils.isNotEmpty(monthList)) {
            for (BusinessStatDTO bs : monthList) {
              BusinessStatDTO businessStatDTO = monthMap.get(bs.getStatMonth());
              if (businessStatDTO != null) {
                bs.setStatSum(bs.getStatSum() + businessStatDTO.getStatSum());
                monthMap.remove(bs.getStatMonth());
              }
            }
          }
          if (MapUtils.isNotEmpty(monthMap)) {
            monthList = new ArrayList<BusinessStatDTO>();
            for (Map.Entry<Long, BusinessStatDTO> set : monthMap.entrySet()) {
              monthList.add(set.getValue());
            }
          }
        }
        if (CollectionUtils.isEmpty(monthList)) {
          String str = "[]";
          jsonStr.append(str);
        }else {
          String str = JsonUtil.listToJsonNoQuote(monthList);
          jsonStr.append(str);
        }

      } else if (QUERY_TYPE_YEAR.equals(type)) {
        List<BusinessStatDTO> yearList = businessStatService.getYearBusinessStatList(shopId);
        if (CollectionUtils.isEmpty(yearList)) {
          String str = "[]";
          jsonStr.append(str);
        } else {
          String str = JsonUtil.listToJsonNoQuote(yearList);
          jsonStr.append(str);
        }
      }

      PrintWriter writer = response.getWriter();
      writer.write(jsonStr.toString());
      writer.close();

    } catch (Exception e) {
      LOG.error("/businessStat.do method=getDataTable " + "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      LOG.error(e.getMessage(), e);
    }
  }


  /**
   *
   * @param shopId 店铺id
   * @param year   查询年份
   * @return      查询获得的list
   */
  public List<BusinessStatDTO> getMonthBusinessStatList(long shopId, int year) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();

    //获取日期
    Calendar currentCalendar = Calendar.getInstance();
    int currentYear = currentCalendar.get(Calendar.YEAR);

    //获取上一年的最新数据
    List<BusinessStatDTO> lastYearList = txnService.getLatestBusinessStat(shopId, (long)(year - 1), 1);
    if (CollectionUtils.isEmpty(lastYearList)) {
      BusinessStatDTO businessStatDTO = new BusinessStatDTO();
      businessStatDTO.setShopId(shopId);
      businessStatDTO.setStatSum(0d);
      businessStatDTOList.add(businessStatDTO);
    } else {
      businessStatDTOList.add(lastYearList.get(0));
    }

    //这一年的最后一个月的最新数据
    List<BusinessStatDTO> businessStatDTOs = txnService.getLatestBusinessStat(shopId, (long)year, 1);
    //这一年的第一个月的最新数据
    List<BusinessStatDTO> businessStatDTOs1 = txnService.getEarliestBusinessStat(shopId, year, 1);
    if (businessStatDTOs.size() == 0 || businessStatDTOs1.size() == 0) {
      //说明这个年的的数据没有
      return null;
    } else {
      BusinessStatDTO lastBusinessStatDTO = businessStatDTOs.get(0);
      BusinessStatDTO earlyBusinessStatDTO = businessStatDTOs1.get(0);

      int beginMonth = earlyBusinessStatDTO.getStatMonth().intValue();
      int endMonth = lastBusinessStatDTO.getStatMonth().intValue();
      int lastDay = lastBusinessStatDTO.getStatDay().intValue();
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(" and ( ");
      for (int i = beginMonth; i <= endMonth; i++) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, i - 1, 1, 0, 0, 0);
        int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
        if (i == endMonth) {
          lastDayOfCurrentMonth = lastDay;
        }

        stringBuilder.append(" ( r.statMonth = ").append(i).append(" and r.statDay = ")
            .append(lastDayOfCurrentMonth).append(" ) ");
        if (i != endMonth) {
          stringBuilder.append(" or ");
        }

      }
      stringBuilder.append(" ) ");
      for (int index = 1; index < beginMonth; index++) {
        BusinessStatDTO businessStatDTO = new BusinessStatDTO();
        businessStatDTO.setStatSum(0);
        businessStatDTO.setStatMonth((long) index);
        businessStatDTOList.add(businessStatDTO);
      }
      List<BusinessStatDTO> monthList = new ArrayList<BusinessStatDTO>();
      if (!StringUtil.isEmpty(stringBuilder.toString())) {
        monthList = txnService.getBusinessStatMonth(shopId, year, stringBuilder.toString());
      }
      if (monthList.size() <= 0) {
        return null;
      } else {
        businessStatDTOList.addAll(monthList);
        BusinessStatDTO lastDTO = businessStatDTOList.get(businessStatDTOList.size() - 1);
        for (int index = endMonth + 1; index <= 12; index++) {
          BusinessStatDTO businessStatDTO = new BusinessStatDTO();
          businessStatDTO.setStatSum(lastDTO.getStatSum());
          businessStatDTO.setStatMonth((long) index);
          businessStatDTOList.add(businessStatDTO);
        }
        int size = businessStatDTOList.size() - 1;
        for (int j = size; j >= 1; j--) {
          BusinessStatDTO businessStatDTO = businessStatDTOList.get(j);
          BusinessStatDTO lastMonthDTO = businessStatDTOList.get(j - 1);
          businessStatDTO.setStatSum(businessStatDTO.getStatSum() - lastMonthDTO.getStatSum());
        }
      }
    }

    businessStatDTOList.remove(0);
    return businessStatDTOList;
  }


  /**
   *
   * @param shopId  店铺id
   * @param year    年份
   * @param month   月份
   * @return
   */
  public List<BusinessStatDTO> getDayBusinessStatList(long shopId, int year, int month) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
    Calendar calendar = Calendar.getInstance();
    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONDAY) + 1;
    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    calendar.set(year, month - 1, 1, 0, 0, 0);
    int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
    List<BusinessStatDTO> lastMonthList = null;

    if (month != 1) {
      //上个月最后一天没有数据，拿到上个月的最新数据
      lastMonthList = txnService.getLatestBusinessStatMonth(shopId, year, month - 1, 1);
    }else {
      //上个月最后一天没有数据，拿到上个月的最新数据
      lastMonthList = txnService.getLatestBusinessStatMonth(shopId, year-1, 12, 1);
    }
    if (lastMonthList == null || lastMonthList.size() <= 0) {
      //说明上个月以前没有数据 虚拟一条数据 保存 作为上个月的数据
      lastMonthList = new ArrayList<BusinessStatDTO>();
      BusinessStatDTO businessStatDTO = new BusinessStatDTO();
      businessStatDTO.setShopId(shopId);
      businessStatDTO.setStatYear((long) year);
      businessStatDTO.setStatMonth((long) month);
      businessStatDTO.setStatDay(1L);
      businessStatDTO.setSales(0);
      businessStatDTO.setService(0);
      businessStatDTO.setWash(0);
      businessStatDTO.setStatSum(0);
      businessStatDTO.setProductCost(0);
      businessStatDTO.setStatTime(System.currentTimeMillis());
      lastMonthList.add(businessStatDTO);
      businessStatDTOList.add(businessStatDTO);
    } else {
      businessStatDTOList.add(lastMonthList.get(0));
    }

    List<BusinessStatDTO> dayList = new ArrayList<BusinessStatDTO>();
    if (month == currentMonth && year == currentYear) {
      dayList = txnService.getBusinessStatMonthEveryDay(shopId, year, month, currentDay);

      if (dayList.size() <= 0) {
        return null;
      } else {

        double statSum = lastMonthList.get(0).getStatSum();
        BusinessStatDTO businessStatDTO2 = dayList.get(0);//这个月最早的一天
        if(businessStatDTO2.getStatDay() != null){
          int startDay = businessStatDTO2.getStatDay().intValue();
          for(int i = 1; i < startDay;i++){
            BusinessStatDTO statDTO = new BusinessStatDTO();
            statDTO.setStatDay((long) i);
            statDTO.setStatSum(statSum);
            businessStatDTOList.add(statDTO);
          }
        }

        businessStatDTOList.addAll(dayList);

        BusinessStatDTO businessStatDTO1 = businessStatDTOList.get(businessStatDTOList.size() - 1);
        int lastDay = businessStatDTO1.getStatDay().intValue();
        for (int k = lastDay + 1; k <= lastDayOfCurrentMonth; k++) {
          BusinessStatDTO businessStatDTOTmp = new BusinessStatDTO();
          businessStatDTOTmp.setStatDay((long) k);
          businessStatDTOTmp.setStatSum(businessStatDTO1.getStatSum());
          businessStatDTOList.add(businessStatDTOTmp);
        }
      }
    } else {
      dayList = txnService.getBusinessStatMonthEveryDay(shopId, year, month, lastDayOfCurrentMonth);
      if (dayList.size() <= 0) {
        //这个月没有数据
        return null;
      } else {

        double statSum = lastMonthList.get(0).getStatSum();
        BusinessStatDTO businessStatDTO2 = dayList.get(0);//这个月最早的一天
        if(businessStatDTO2.getStatDay() != null){
          int startDay = businessStatDTO2.getStatDay().intValue();
          for(int i = 1; i < startDay;i++){
            BusinessStatDTO statDTO = new BusinessStatDTO();
            statDTO.setStatDay((long) i);
            statDTO.setStatSum(statSum);
            businessStatDTOList.add(statDTO);
          }
        }

        businessStatDTOList.addAll(dayList);

        BusinessStatDTO businessStatDTO1 = businessStatDTOList.get(businessStatDTOList.size() - 1);
        int lastDay = businessStatDTO1.getStatDay().intValue();

        for (int k = lastDay + 1; k <= lastDayOfCurrentMonth; k++) {
          BusinessStatDTO businessStatDTOTmp = new BusinessStatDTO();
          businessStatDTOTmp.setStatDay((long) k);
          businessStatDTOTmp.setStatSum(businessStatDTO1.getStatSum());
          businessStatDTOList.add(businessStatDTOTmp);
        }
      }
    }

    int size = businessStatDTOList.size() - 1;
    for (int j = size; j >= 1; j--) {
      BusinessStatDTO businessStatDTO = businessStatDTOList.get(j);
      BusinessStatDTO lastMonthDTO = businessStatDTOList.get(j - 1);
      businessStatDTO.setStatSum(businessStatDTO.getStatSum() - lastMonthDTO.getStatSum());
    }

    businessStatDTOList.remove(0);
    return businessStatDTOList;
  }




  /**
   * 从前台获得房租、水电、人工等数据，保存到数据库中
   *
   * @param request  request
   * @param response response
   */
  @RequestMapping(params = "method=saveExpendDetail")
  public void saveExpendDetail(HttpServletRequest request, HttpServletResponse response) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
    if (shopId < 0) {
      return;
    }

    ExpendDetailDTO expendDetailDTO = new ExpendDetailDTO();
    expendDetailDTO.setShopId(shopId);

    if (!StringUtil.isEmpty(request.getParameter("expendDetailId"))) {
      expendDetailDTO.setId(Long.valueOf(request.getParameter("expendDetailId")));
    } else {
      expendDetailDTO.setId(null);
    }

    if (!StringUtil.isEmpty(request.getParameter("dayHid"))) {
      expendDetailDTO.setDay(Long.valueOf(request.getParameter("dayHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("monthHid"))) {
      expendDetailDTO.setMonth(Long.valueOf(request.getParameter("monthHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("yearHid"))) {
      expendDetailDTO.setYear(Long.valueOf(request.getParameter("yearHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("dayTotalHid"))) {
      expendDetailDTO.setTotalDay(Double.valueOf(request.getParameter("dayTotalHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("monthTotalHid"))) {
      expendDetailDTO.setTotalMonth(Double.valueOf(request.getParameter("monthTotalHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("yearTotalHid"))) {
      expendDetailDTO.setTotalYear(Double.valueOf(request.getParameter("yearTotalHid")));
    }

    if (!StringUtil.isEmpty(request.getParameter("dayRentHid"))) {
      expendDetailDTO.setRentDay(Double.valueOf(request.getParameter("dayRentHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("monthRentHid"))) {
      expendDetailDTO.setRentMonth(Double.valueOf(request.getParameter("monthRentHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("yearRentHid"))) {
      expendDetailDTO.setRentYear(Double.valueOf(request.getParameter("yearRentHid")));
    }

    if (!StringUtil.isEmpty(request.getParameter("dayLaborHid"))) {
      expendDetailDTO.setLaborDay(Double.valueOf(request.getParameter("dayLaborHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("monthLaborHid"))) {
      expendDetailDTO.setLaborMonth(Double.valueOf(request.getParameter("monthLaborHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("yearLaborHid"))) {
      expendDetailDTO.setLaborYear(Double.valueOf(request.getParameter("yearLaborHid")));
    }

    if (!StringUtil.isEmpty(request.getParameter("dayOtherHid"))) {
      expendDetailDTO.setOtherDay(Double.valueOf(request.getParameter("dayOtherHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("monthOtherHid"))) {
      expendDetailDTO.setOtherMonth(Double.valueOf(request.getParameter("monthOtherHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("yearOtherHid"))) {
      expendDetailDTO.setOtherYear(Double.valueOf(request.getParameter("yearOtherHid")));
    }

    if (!StringUtil.isEmpty(request.getParameter("dayStatOtherId"))) {
      expendDetailDTO.setOtherFeeDay(Double.valueOf(request.getParameter("dayStatOtherId")));
    }
    if (!StringUtil.isEmpty(request.getParameter("monthStatOtherId"))) {
      expendDetailDTO.setOtherFeeMonth(Double.valueOf(request.getParameter("monthStatOtherId")));
    }
    if (!StringUtil.isEmpty(request.getParameter("yearStatOtherId"))) {
      expendDetailDTO.setOtherFeeYear(Double.valueOf(request.getParameter("yearStatOtherId")));
    }

    if (!StringUtil.isEmpty(request.getParameter("dayOtherExpendHid"))) {
      expendDetailDTO.setOtherExpendDay(Double.valueOf(request.getParameter("dayOtherExpendHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("monthOtherExpendHid"))) {
      expendDetailDTO.setOtherExpendMonth(Double.valueOf(request.getParameter("monthOtherExpendHid")));
    }
    if (!StringUtil.isEmpty(request.getParameter("yearOtherExpendHid"))) {
      expendDetailDTO.setOtherExpendYear(Double.valueOf(request.getParameter("yearOtherExpendHid")));
    }

    if (expendDetailDTO != null) {
      txnService.saveExpendDetail(expendDetailDTO);
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write("保存成功");
      writer.close();
    } catch (IOException e) {
      LOG.info("店铺房租保存失败");
    }
  }

  /**
   * 营业统计页面 用于获取洗车列表
   * @param request
   * @param response
   * @param startPageNo 开始页数
   * @param maxRows     每页显示条数 初定为25
   * @param type day:按日查询 month 按月查询 year 按年查询
   * @param dateStr  查询日期
   */
  @RequestMapping(params = "method=getWashOrderDetail")
  public void getWashOrderDetail(HttpServletRequest request,HttpServletResponse response,Integer startPageNo,Integer maxRows,String type,String dateStr,String arrayType) {
    try {
      ISearchService searchService = ServiceManager.getService(ISearchService.class);

      //校验数据
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }
      Long queryDate = statUtil.getTimeLongValue(dateStr, request); //查询开始日期
      type = (StringUtil.isEmpty(type) == true ? QUERY_TYPE_DAY : type);
      arrayType = statUtil.getArrayTypeByType(arrayType); //排序类型:按照金额 或者时间排序
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间
      arrayType = statUtil.getItemArrayType(arrayType); //获得对item_type表的排序sql语句
      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L);
      //总条数和总金额 使用 count 和 sum进行查询
      List<String> washStringList = searchService.getWashItemTotal(shopId, startTime, endTime);
      int size = statUtil.getIntValueByIndex2(washStringList, 0); //获得当前查询日期内的单据条数
      Double total = statUtil.getDoubleValueByIndex2(washStringList, 1); //获得当前查询日期内的单据总和

      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      pager.setStartDateStr(startTimeStr);
      pager.setEndDateStr(endTimeStr);

      List<ItemIndexDTO> itemIndexDTOList = null;

      String jsonStr = ""; //前台返回json字符串
      if (size > 0) {
        itemIndexDTOList = searchService.getWashOrderItemIndexList(shopId, startTime, endTime, startPageNo.intValue(), maxRows.intValue(), arrayType);
      }

      List<String> stringList = statUtil.getWashBeautyOrderStringList(itemIndexDTOList);
      stringList.add(String.valueOf(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION)));
      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(itemIndexDTOList, stringList);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

      try {
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
      } catch (Exception e) {
        LOG.error("/businessStat.do");
        LOG.error("method=getWashOrderDetail");
        LOG.error("PrintWriter出错");
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
    } catch (Exception ex) {
      LOG.error("/businessStat.do");
      LOG.error("method=getWashOrderDetail");
      LOG.error("营业统计 获取洗车单列表出错");
      LOG.error(ex.getMessage(), ex);
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      return;
    }
  }



  /**
   * 营业统计页面 用于获取销售单列表
   * @param request
   * @param response
   * @param startPageNo 开始页数
   * @param maxRows     每页显示条数 初定为25
   * @param type day:按日查询 month 按月查询 year 按年查询
   * @param dateStr  查询日期
   */
  @RequestMapping(params = "method=getSalesOrderDetail")
  public void getSalesOrderDetail(HttpServletRequest request,HttpServletResponse response,Integer startPageNo,Integer maxRows,String type,String dateStr,String arrayType) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      ISearchService searchService = ServiceManager.getService(ISearchService.class);

      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }

      Long queryDate = statUtil.getTimeLongValue(dateStr, request); //查询开始日期
      type = (StringUtil.isEmpty(type) == true ? QUERY_TYPE_DAY : type);
      arrayType = statUtil.getArrayTypeByType(arrayType); //排序类型:按照金额 或者时间排序
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间

      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L);
      double total = 0.0; //单据总计
      double pageTotal = 0.0; //本页小计单据总额
      double memberDiscountTotal = 0.0; //折后总额
      //总条数和总金额 使用 count 和 sum进行查询

      int size = 0;

      List<String>  stringList = new ArrayList<String>();
      try {
        stringList = txnService.getSalesOrderCountAndSum(shopId, startTime, endTime);
      } catch (Exception e) {
        LOG.error(e.getMessage(),e);
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      }

      size = CollectionUtils.isEmpty(stringList)? 0 :Integer.valueOf(stringList.get(0)); //获得当前查询日期内的单据条数
      total = (Double.valueOf(CollectionUtils.isEmpty(stringList)? 0D :Double.valueOf(stringList.get(1)))); //获得当前查询日期内的单据总和
      size += CollectionUtils.isEmpty(stringList)? 0 :Integer.valueOf(stringList.get(3));
      total -=  CollectionUtils.isEmpty(stringList)? 0D :Double.valueOf(stringList.get(4));
      String str = "";
      double saleCostTotal = 0.0;//销售单成本
      double saleSettleTotal = 0.0; //销售单实收
      double saleDebtTotal = 0.0;   //销售单欠款
      double saleDiscountTotal = 0.0; //销售单优惠
      double saleProfitTotal = 0.0;  //销售单毛利
      double saleAfterMemberDiscountTotal = 0.0;//销售折后总额
      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      pager.setStartDateStr(startTimeStr);
      pager.setEndDateStr(endTimeStr);
      List<OrderSearchResultDTO> orderSearchResultDTOList = null;
      if(size > 0) {
        try {
          //使用归属时间进行查询vest_date
          orderSearchResultDTOList = txnService.getSalesOrderDTOList(shopId, startTime, endTime, startPageNo.intValue(), maxRows.intValue(), arrayType);
        } catch (Exception ex) {
          LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
          LOG.error(ex.getMessage(),ex);
          return;
        }

        arrayType = statUtil.getItemArrayType(arrayType);

        if(CollectionUtils.isNotEmpty(orderSearchResultDTOList)){
          for(OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOList) {



            List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(shopId, orderSearchResultDTO.getOrderId());
            List<ItemIndexDTO> itemIndexDTOList = searchService.getSalesOrderItemIndexList(shopId, " ( " + orderSearchResultDTO.getOrderIdStr() + " ) ", arrayType);

            if (CollectionUtils.isNotEmpty(itemIndexDTOList)) {
              StringBuffer productNames = new StringBuffer();

              for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {

                if(StringUtils.isNotBlank(itemIndexDTO.getItemName()) && itemIndexDTO.getItemType().equals(ItemTypes.MATERIAL))
                {
                  productNames.append(itemIndexDTO.getItemName()+(StringUtils.isNotBlank(itemIndexDTO.getItemName())?",":""));
                }
              }
              orderSearchResultDTO.setProductNames(productNames.toString());
            }

            if (CollectionUtils.isNotEmpty(orderIndexDTOList)) {
              OrderIndexDTO orderIndexDTO = orderIndexDTOList.get(0);
              orderSearchResultDTO.setOrderContent(orderIndexDTO.getOrderContent());

            } else {
              if (CollectionUtils.isNotEmpty(itemIndexDTOList)) {
                StringBuffer orderContent = new StringBuffer();

                for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {

                  if (itemIndexDTO.getItemCount() != null && itemIndexDTO.getItemPrice() != null) {
                    orderContent.append(itemIndexDTO.getItemName()).append("(").
                        append(itemIndexDTO.getItemPrice()).append("*").append(itemIndexDTO.getItemCount());
                    if (!StringUtil.isEmpty(itemIndexDTO.getUnit())) {
                      orderContent.append(itemIndexDTO.getUnit());
                    }
                    orderContent.append("),");
                  }
                }
                orderSearchResultDTO.setOrderContent(orderContent.toString());
              }
            }

            ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, orderSearchResultDTO.getOrderId());
            if (OrderTypes.SALE.getName().equals(orderSearchResultDTO.getOrderTypeValue())) {

              if (receivableDTO != null) {
                orderSearchResultDTO.setSettled(NumberUtil.toReserve(receivableDTO.getSettledAmount(), NumberUtil.MONEY_PRECISION));
                orderSearchResultDTO.setDebt(NumberUtil.toReserve(receivableDTO.getDebt(), NumberUtil.MONEY_PRECISION));
                orderSearchResultDTO.setDiscount(NumberUtil.toReserve(receivableDTO.getTotal() - receivableDTO.getSettledAmount() - receivableDTO.getDebt(), NumberUtil.MONEY_PRECISION));
                orderSearchResultDTO.setGrossProfit(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt() -NumberUtil.doubleVal(orderSearchResultDTO.getTotalCostPrice()));
                orderSearchResultDTO.setGrossProfit(NumberUtil.toReserve(orderSearchResultDTO.getGrossProfit(),NumberUtil.MONEY_PRECISION));

                if (NumberUtil.toReserve(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt(),NumberUtil.MONEY_PRECISION) == 0.0) {
                  orderSearchResultDTO.setGrossProfitRate(0D);
                } else {
                  orderSearchResultDTO.setGrossProfitRate(NumberUtil.toReserve(orderSearchResultDTO.getGrossProfit() * 100 / (orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt()), 1));
                }
                orderSearchResultDTO.setAfterMemberDiscountTotal(null!=receivableDTO.getAfterMemberDiscountTotal()?receivableDTO.getAfterMemberDiscountTotal():receivableDTO.getTotal());
              }
              else
              {
                orderSearchResultDTO.setAfterMemberDiscountTotal(orderSearchResultDTO.getAmount());
              }

              orderSearchResultDTO.setProductTotal(NumberUtil.toReserve(orderSearchResultDTO.getAmount() - orderSearchResultDTO.getOtherIncomeTotal(),NumberUtil.MONEY_PRECISION));
              orderSearchResultDTO.setProductTotalCostPrice(NumberUtil.toReserve(orderSearchResultDTO.getTotalCostPrice() - orderSearchResultDTO.getOtherTotalCostPrice(),NumberUtil.MONEY_PRECISION));


              saleCostTotal += orderSearchResultDTO.getTotalCostPrice();
              saleSettleTotal += orderSearchResultDTO.getSettled();
              saleDebtTotal += orderSearchResultDTO.getDebt();
              saleDiscountTotal += orderSearchResultDTO.getDiscount();
              saleProfitTotal += orderSearchResultDTO.getGrossProfit();
              pageTotal += orderSearchResultDTO.getAmount();
              saleAfterMemberDiscountTotal += orderSearchResultDTO.getAfterMemberDiscountTotal();
            } else {
              orderSearchResultDTO.setTotalCostPrice(0- orderSearchResultDTO.getTotalCostPrice());
              orderSearchResultDTO.setAmount(0- orderSearchResultDTO.getAmount());

              orderSearchResultDTO.setProductTotal(orderSearchResultDTO.getAmount());
              orderSearchResultDTO.setProductTotalCostPrice(orderSearchResultDTO.getTotalCostPrice());


              if (receivableDTO != null) {
                orderSearchResultDTO.setSettled(receivableDTO.getSettledAmount());
                orderSearchResultDTO.setDebt(receivableDTO.getDebt());
                orderSearchResultDTO.setDiscount(NumberUtil.toReserve(receivableDTO.getTotal() - receivableDTO.getSettledAmount() - receivableDTO.getDebt(), NumberUtil.MONEY_PRECISION));
                orderSearchResultDTO.setGrossProfit(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt() - orderSearchResultDTO.getTotalCostPrice());

                if (NumberUtil.toReserve(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt(),NumberUtil.MONEY_PRECISION) == 0.0) {
                  orderSearchResultDTO.setGrossProfitRate(0D);
                } else {
                  orderSearchResultDTO.setGrossProfitRate(NumberUtil.toReserve(orderSearchResultDTO.getGrossProfit() * 100 / (0 - orderSearchResultDTO.getSettled() - orderSearchResultDTO.getDebt()), 1));
                }
              }
              saleCostTotal += orderSearchResultDTO.getTotalCostPrice();
              saleSettleTotal += orderSearchResultDTO.getSettled();
              saleDebtTotal += orderSearchResultDTO.getDebt();
              saleDiscountTotal += orderSearchResultDTO.getDiscount();
              saleProfitTotal += orderSearchResultDTO.getGrossProfit();
              pageTotal += orderSearchResultDTO.getAmount();
              saleAfterMemberDiscountTotal += orderSearchResultDTO.getAmount();
            }
          }
        }
      }

      if (CollectionUtils.isEmpty(orderSearchResultDTOList)) {
        str = "[";
        str = str + "{\"saleCostTotal\":" + String.valueOf(NumberUtil.toReserve(saleCostTotal,NumberUtil.MONEY_PRECISION)) + "}";
      } else {
        str = JsonUtil.listToJsonNoQuote(orderSearchResultDTOList);
        str = str.substring(0, str.length() - 1);
        str = str + ",{\"saleCostTotal\":" + String.valueOf(NumberUtil.toReserve(saleCostTotal,NumberUtil.MONEY_PRECISION)) + "}";
      }

      str = str + ",{\"saleSettleTotal\":" + String.valueOf(NumberUtil.toReserve(saleSettleTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"saleDebtTotal\":" + String.valueOf(NumberUtil.toReserve(saleDebtTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"saleDiscountTotal\":" + String.valueOf(NumberUtil.toReserve(saleDiscountTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"saleProfitTotal\":" + String.valueOf(NumberUtil.toReserve(saleProfitTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"total\":" + String.valueOf(NumberUtil.toReserve(total,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"pageTotal\":" + String.valueOf(NumberUtil.toReserve(pageTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"saleAfterMemberDiscountTotal\":" + String.valueOf(NumberUtil.toReserve(saleAfterMemberDiscountTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + "," + pager.toJson().substring(1, pager.toJson().length());

      try {
        PrintWriter writer = response.getWriter();
        writer.write(str);
        writer.close();
      } catch (Exception e) {
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
    } catch (Exception ex) {
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(ex.getMessage(),ex);
    }
  }



  /**
   * 营业统计页面 用于获取维修单列表
   * @param request
   * @param response
   * @param startPageNo 开始页数
   * @param maxRows     每页显示条数 初定为25
   * @param type day:按日查询 month 按月查询 year 按年查询
   * @param dateStr  查询日期
   */
  @RequestMapping(params = "method=getRepairOrderDetail")
  public void getRepairOrderDetail(HttpServletRequest request,HttpServletResponse response,Integer startPageNo,Integer maxRows,String type,String dateStr,String arrayType) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      ISearchService searchService = ServiceManager.getService(ISearchService.class);

      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }

      Long queryDate = statUtil.getTimeLongValue(dateStr, request); //查询开始日期
      type = (StringUtil.isEmpty(type) == true ? QUERY_TYPE_DAY : type);
      arrayType = statUtil.getArrayTypeByType(arrayType); //排序类型:按照金额 或者时间排序
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间
      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L);
      Pager pager = null;
      String str = "";
      int size = 0;
      double total = 0.0;
      double pageTotal = 0.0;
      double repairCostTotal = 0.0;
      double repairSettleTotal = 0.0;
      double repairDebtTotal = 0.0;
      double repairDiscountTotal = 0.0;
      double repairProfitTotal = 0.0;

      List<String>  stringList = new ArrayList<String>();

      List<RepairOrderDTO> resultList = new ArrayList<RepairOrderDTO>();

      try {
        stringList = txnService.getRepairOrderDTOListByVestDate(shopId, startTime, endTime, OrderStatus.REPAIR_SETTLED);
      } catch (Exception e) {
        LOG.error("/init.do");
        LOG.error("method=getRepairOrderDetail");
        LOG.error(e.getMessage(),e);
        LOG.error(" 营业统计数据初始化 获取施工单列表出错 ");
      }

      size = statUtil.getIntValueByIndex2(stringList, 0); //获得当前查询日期内的单据条数
      total = statUtil.getDoubleValueByIndex2(stringList, 1); //获得当前查询日期内的单据总和
      pager = statUtil.getPager(size, startPageNo, maxRows);

      pager.setStartDateStr(startTimeStr);
      pager.setEndDateStr(endTimeStr);
      if(size > 0) {
        //查询时间使用单据时间vest_date
        List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderDTOList(shopId, startTime, endTime, startPageNo, maxRows,arrayType,OrderStatus.REPAIR_SETTLED);

        StringBuilder idString = new StringBuilder();
        String strTmp = "";
        idString.append(" ( ");

        if (CollectionUtils.isNotEmpty(repairOrderDTOList)){

          Map<Long, RepairOrderDTO> repairOrderDTOMap = new HashMap<Long, RepairOrderDTO>();

          for (RepairOrderDTO roDto : repairOrderDTOList) {
            if (roDto == null && roDto.getId() == null) {
              LOG.error("/businessStat.do");
              LOG.error("method=getRepairOrderDetail");
              LOG.error("营业统计:获得施工单详细列表,单据为空" + roDto.toString());
              LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
              continue;
            }
            idString.append(roDto.getId().toString()).append(" ,");
            repairOrderDTOMap.put(roDto.getId(), roDto);
          }
          strTmp = idString.substring(0, idString.length() - 1);
          strTmp = strTmp + " ) ";

          arrayType = statUtil.getItemArrayType(arrayType);//获得对item_index表的排序sql语句

          List<ItemIndexDTO> itemIndexDTOList = searchService.getRepairOrderItemIndexList(shopId,strTmp, arrayType);

          Map<Long, List<ItemIndexDTO>> itemIndexMap = new HashMap<Long, List<ItemIndexDTO>>();
          for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
            if (itemIndexDTO == null) {
              continue;
            }
            if (repairOrderDTOMap.containsKey(itemIndexDTO.getOrderId())) {
              if (itemIndexMap.containsKey(itemIndexDTO.getOrderId())) {
                List<ItemIndexDTO> itemIndexDTOs = itemIndexMap.get(itemIndexDTO.getOrderId());
                itemIndexDTOs.add(itemIndexDTO);
                itemIndexMap.remove(itemIndexDTO.getOrderId());
                itemIndexMap.put(itemIndexDTO.getOrderId(), itemIndexDTOs);
              } else {
                List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
                itemIndexDTOs.add(itemIndexDTO);
                itemIndexMap.put(itemIndexDTO.getOrderId(), itemIndexDTOs);
              }
            }
          }

          for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
            Long orderId = repairOrderDTO.getId();
            List<ItemIndexDTO> itemIndexDTOs = itemIndexMap.get(orderId);
            double serviceTotal = 0.0; //施工单施工总费用
            double salesTotal = 0.0;  //施工单销售总费用
            double serviceTotalCost = 0.0; //施工单中的施工 总工时成本
            double salesTotalCost = 0.0;//施工单中的销售总成本
            double orderProfit = 0.0; //施工单毛利
            double repairAfterMemberDiscountTotal = 0.0; //施工单打折后总额
//            StringBuilder serviceContent = new StringBuilder();
//            StringBuilder salesContent = new StringBuilder();
            StringBuffer productNames = new StringBuffer();
            repairOrderDTO.setBrand(repairOrderDTO.getId().toString());

            if (CollectionUtils.isNotEmpty(itemIndexDTOs)) {
              for (int i = 0; i < itemIndexDTOs.size(); i++) {
                ItemIndexDTO itemIndexDTO = itemIndexDTOs.get(i);

                //施工item
                if (itemIndexDTO.getItemType() == ItemTypes.SERVICE) {
//                  serviceContent.append(itemIndexDTO.getItemName()).append("(").append(itemIndexDTO.getItemPrice() == null ? 0 : itemIndexDTO.getItemPrice()).append("元),");
                  serviceTotal += (itemIndexDTO.getItemPrice() == null ? 0 : itemIndexDTO.getItemPrice());
                  if (itemIndexDTO.getTotalCostPrice() != null) {
                    serviceTotalCost += itemIndexDTO.getTotalCostPrice();
                  }
                } else if (itemIndexDTO.getItemType() == ItemTypes.MATERIAL) {

                  productNames.append(itemIndexDTO.getItemName() + (StringUtils.isNotBlank(itemIndexDTO.getItemName())?",":""));
                  //销售item
                  if (itemIndexDTO.getItemCount() != null && itemIndexDTO.getItemPrice() != null) {
//                    salesContent.append(itemIndexDTO.getItemName()).append("(").
//                        append(itemIndexDTO.getItemPrice()).append("*").append(itemIndexDTO.getItemCount());
//                    if (!StringUtil.isEmpty(itemIndexDTO.getUnit())) {
//                      salesContent.append(itemIndexDTO.getUnit());
//                    }
//                    salesContent.append("),");
                    salesTotal += (itemIndexDTO.getItemPrice() * itemIndexDTO.getItemCount());
                  } else {
                    LOG.error("/businessStat.do");
                    LOG.error("method=getRepairOrderDetail");
                    LOG.error("营业统计:获得施工单详细列表 itemIndexDTO itemCount itemPrice为空,orderId:" + itemIndexDTO.getOrderId());
                    LOG.error("itemIndexDTO toString:" + itemIndexDTO.toString());
                    LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
                  }

                  if (itemIndexDTO.getTotalCostPrice() != null) {
                    salesTotalCost += itemIndexDTO.getTotalCostPrice();
                  }
                }
              }
            } else {
              LOG.error("/businessStat.do");
              LOG.error("method=getRepairOrderDetail");
              LOG.error("营业统计:获得施工单详细列表 该单据下没有item_index");
              LOG.error("itemIndexDTO toString:" + repairOrderDTO.toString());
              LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
            }

//            if (serviceContent.length() <= 1) {
//              repairOrderDTO.setServiceContent(serviceContent.toString());
//              repairOrderDTO.setServiceContentStr(serviceContent.toString());
//            } else if (serviceContent.length() < 5) {
//              repairOrderDTO.setServiceContent(serviceContent.toString().substring(0, serviceContent.length() - 1));
//              repairOrderDTO.setServiceContentStr(serviceContent.toString().substring(0, serviceContent.length() - 1));
//            } else {
//              repairOrderDTO.setServiceContentStr(serviceContent.toString().substring(0, serviceContent.length() - 1));
//              repairOrderDTO.setServiceContent(serviceContent.toString().substring(0, 5) + "...");
//            }

//            if (salesContent.length() <= 1) {
//              repairOrderDTO.setSalesContent(salesContent.toString());
//              repairOrderDTO.setSalesContentStr(salesContent.toString());
//            } else if (salesContent.length() < 5) {
//              repairOrderDTO.setSalesContent(salesContent.toString().substring(0, salesContent.length() - 1));
//              repairOrderDTO.setSalesContentStr(salesContent.toString().substring(0, salesContent.length() - 1));
//            } else {
//              repairOrderDTO.setSalesContentStr(salesContent.toString().substring(0, salesContent.length() - 1));
//              repairOrderDTO.setSalesContent(salesContent.toString().substring(0, 5) + "...");
//            }

            repairOrderDTO.setProductNames(productNames.toString());
            repairOrderDTO.setServiceTotal(serviceTotal);
            repairOrderDTO.setServiceTotalCost(serviceTotalCost);
            repairOrderDTO.setSalesTotal(salesTotal);
            repairOrderDTO.setSalesTotalCost(salesTotalCost);
            repairOrderDTO.setOrderTotalCost(repairOrderDTO.getTotalCostPrice());

            //实收和欠款从receivable表拿
            ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(repairOrderDTO.getShopId(), OrderTypes.REPAIR, repairOrderDTO.getId());
            if (receivableDTO != null) {
              repairOrderDTO.setDebt(receivableDTO.getDebt());
              repairOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
              repairOrderDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
              if(null != receivableDTO.getAfterMemberDiscountTotal())
              {
                receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
              }
              else
              {
                receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getTotal());
              }
            } else {
              repairOrderDTO.setDebt(0.0);
              repairOrderDTO.setSettledAmount(repairOrderDTO.getTotal());
              repairOrderDTO.setAfterMemberDiscountTotal(repairOrderDTO.getTotal());
            }

            orderProfit = repairOrderDTO.getDebt() + repairOrderDTO.getSettledAmount() - repairOrderDTO.getOrderTotalCost();
            repairOrderDTO.setOrderProfit(orderProfit);
            repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal() - repairOrderDTO.getDebt() - repairOrderDTO.getSettledAmount());
            double orderProfitTmp = (orderProfit * 100) / (repairOrderDTO.getDebt() + repairOrderDTO.getSettledAmount());
            BigDecimal bigDecimal = null;
            try {
              bigDecimal = new BigDecimal(orderProfitTmp);
            } catch (Exception e) {
              bigDecimal = new BigDecimal(0.0);
            }

              pageTotal += repairOrderDTO.getTotal();
              repairCostTotal += repairOrderDTO.getOrderTotalCost();
              repairSettleTotal += repairOrderDTO.getSettledAmount();
              repairDebtTotal += repairOrderDTO.getDebt();
              repairDiscountTotal += repairOrderDTO.getOrderDiscount();
              repairProfitTotal += orderProfit;

            orderProfitTmp = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            repairOrderDTO.setOrderProfitPercent(String.valueOf(orderProfitTmp) + "%");
            resultList.add(repairOrderDTO);
          }
        }
      }

      if (CollectionUtils.isEmpty(resultList)) {
        str = "[";
        str = str + "{\"repairCostTotal\":" + String.valueOf(NumberUtil.toReserve(repairCostTotal,NumberUtil.MONEY_PRECISION)) + "}";
      }else{
        str = JsonUtil.listToJsonNoQuote(resultList);
        str = str.substring(0, str.length() - 1);
        str = str + ",{\"repairCostTotal\":" + String.valueOf(NumberUtil.toReserve(repairCostTotal,NumberUtil.MONEY_PRECISION)) + "}";
      }

      str = str + ",{\"repairSettleTotal\":" + String.valueOf(NumberUtil.toReserve(repairSettleTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"repairDebtTotal\":" + String.valueOf(NumberUtil.toReserve(repairDebtTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"repairDiscountTotal\":" + String.valueOf(NumberUtil.toReserve(repairDiscountTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"repairProfitTotal\":" + String.valueOf(NumberUtil.toReserve(repairProfitTotal,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"total\":" + String.valueOf(NumberUtil.toReserve(total,NumberUtil.MONEY_PRECISION)) + "}";
      str = str + ",{\"pageTotal\":" + String.valueOf(NumberUtil.toReserve(pageTotal,NumberUtil.MONEY_PRECISION)) + "}";

      str = str + "," + pager.toJson().substring(1, pager.toJson().length());

      try {
        PrintWriter writer = response.getWriter();
        writer.write(str);
        writer.close();
      } catch (Exception e) {
        LOG.error("/businessStat.do");
        LOG.error("method=getRepairOrderDetail");
        LOG.error("PrintWriter出错");
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
    } catch (Exception e) {
      LOG.error("/businessStat.do");
      LOG.error("method=getRepairOrderDetail");
      LOG.error("营业统计 获取施工单列表出错");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }



  @RequestMapping(params = "method=createQueryDate")
  public String createQueryDate(HttpServletRequest request) throws Exception
  {
    Long queryDate = System.currentTimeMillis();
    String queryDateStr = DateUtil.dateLongToStr(queryDate, DateUtil.YEAR_MONTH_DATE);
    request.setAttribute("queryDateStr",queryDateStr);

    return "stat/queryDate";
  }

  public void getBizStatPrintDTOToPrint(HttpServletRequest request,String startDateStr,String endDateStr) throws Exception
  {
    Long getPrintDate =System.currentTimeMillis();

    String getPrintDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT,getPrintDate);
    request.setAttribute("getPrintDateStr",getPrintDateStr);
    request.setAttribute("startDateStr",startDateStr);
    request.setAttribute("endDateStr",endDateStr);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
    List<BusinessStatDTO> businessStatDTOList= null;
    List<BusinessStatDTO> businessStatDTOList1= null;
    List<BusinessStatDTO> businessStatDTOList2= null;
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    ShopDTO shopDTO = null;
    Long year = Long.valueOf(startDateStr.substring(0,4));
    Long startDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,startDateStr);
    Long endDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,endDateStr);
    //开始日期的前一天
    String beforeStartDateStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startDate-1L);
    BizStatPrintDTO bizStatPrintDTO = new BizStatPrintDTO();
    try{
      shopDTO = configService.getShopById(shopId);
      businessStatDTOList = txnService.getLatestBusinessStat(shopId,year,1);
      Long startTime = null;
      if(CollectionUtils.isNotEmpty(businessStatDTOList))
      {
        String startTimeStr = businessStatDTOList.get(0).getStatYear()+"-"+businessStatDTOList.get(0).getStatMonth()+
            "-"+businessStatDTOList.get(0).getStatDay();
        startTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,startTimeStr);
        if(startTime < startDate)
        {
          bizStatPrintDTO.setSales(0);
          bizStatPrintDTO.setService(0);
          bizStatPrintDTO.setWash(0);
          bizStatPrintDTO.setMemberIncome(0);
          bizStatPrintDTO.setProductCost(0);
          bizStatPrintDTO.setStatSum(0);
          bizStatPrintDTO.setOrderOtherIncomeCost(0);
        }
        else if(startTime>=startDate && startTime<=endDate)
        {
          //最后一条 记录的时间在开始时间和结束时间之间，把最后一条记录的时间赋给结束时间
          businessStatDTOList1 = txnService.getBusinessStatByYearMonthDay(shopId, Long.valueOf(beforeStartDateStr.substring(0, 4)),
              Long.valueOf(beforeStartDateStr.substring(5, 7)), Long.valueOf(beforeStartDateStr.substring(8, 10)));
          if(null == businessStatDTOList1 || 0== businessStatDTOList1.size())
          {
            bizStatPrintDTO.getInfoFromBusinessStatDTO(businessStatDTOList.get(0));
          }
          else
          {
            bizStatPrintDTO.getInfoFromBusinessStatDTO(businessStatDTOList1.get(0),businessStatDTOList.get(0));
          }
        }
        else
        {
          businessStatDTOList1 = txnService.getBusinessStatByYearMonthDay(shopId, Long.valueOf(beforeStartDateStr.substring(0, 4)),
              Long.valueOf(beforeStartDateStr.substring(5, 7)), Long.valueOf(beforeStartDateStr.substring(8, 10)));
          businessStatDTOList2 = txnService.getBusinessStatByYearMonthDay(shopId, Long.valueOf(endDateStr.substring(0, 4)),
              Long.valueOf(endDateStr.substring(5, 7)), Long.valueOf(endDateStr.substring(8, 10)));
          if(null == businessStatDTOList2 || 0==businessStatDTOList2.size())
          {
            bizStatPrintDTO.setSales(0);
            bizStatPrintDTO.setService(0);
            bizStatPrintDTO.setWash(0);
            bizStatPrintDTO.setProductCost(0);
            bizStatPrintDTO.setStatSum(0);
            bizStatPrintDTO.setMemberIncome(0);
            bizStatPrintDTO.setOrderOtherIncomeCost(0);
          }
          else
          {
            if(null == businessStatDTOList1 || 0 == businessStatDTOList1.size())
            {
              bizStatPrintDTO.getInfoFromBusinessStatDTO(businessStatDTOList2.get(0));
            }
            else
            {
              bizStatPrintDTO.getInfoFromBusinessStatDTO(businessStatDTOList1.get(0), businessStatDTOList2.get(0));
            }
          }
        }

      }
      else
      {
        bizStatPrintDTO.setSales(0);
        bizStatPrintDTO.setService(0);
        bizStatPrintDTO.setWash(0);
        bizStatPrintDTO.setProductCost(0);
        bizStatPrintDTO.setStatSum(0);
        bizStatPrintDTO.setMemberIncome(0);
        bizStatPrintDTO.setOrderOtherIncomeCost(0);
      }

      BizStatPrintDTO newBizStatPrintDTO = txnService.getBusinessChangeInfoToPrint(shopId,startDate,endDate);

      if(null == newBizStatPrintDTO)
      {
        bizStatPrintDTO.setRent(0);
        bizStatPrintDTO.setLabor(0);
        bizStatPrintDTO.setOther(0);
        bizStatPrintDTO.setOtherFee(0);
        bizStatPrintDTO.setOtherIncome(0);
        bizStatPrintDTO.setOrderOtherIncomeCost(0);
      }
      else
      {
        bizStatPrintDTO.setProductCost(bizStatPrintDTO.getProductCost()+newBizStatPrintDTO.getProductCost());
        bizStatPrintDTO.setOrderOtherIncomeCost(bizStatPrintDTO.getOrderOtherIncomeCost()+newBizStatPrintDTO.getOrderOtherIncomeCost());
        bizStatPrintDTO.setSales(bizStatPrintDTO.getSales()+newBizStatPrintDTO.getSales());
        bizStatPrintDTO.setWash(bizStatPrintDTO.getWash()+newBizStatPrintDTO.getWash());
        bizStatPrintDTO.setService(bizStatPrintDTO.getService()+newBizStatPrintDTO.getService());
        bizStatPrintDTO.setStatSum(bizStatPrintDTO.getStatSum()+newBizStatPrintDTO.getStatSum());
        bizStatPrintDTO.setRent(newBizStatPrintDTO.getRent());
        bizStatPrintDTO.setLabor(newBizStatPrintDTO.getLabor());
        bizStatPrintDTO.setOther(newBizStatPrintDTO.getOther());
        bizStatPrintDTO.setOtherFee(newBizStatPrintDTO.getOtherFee());
        bizStatPrintDTO.setOtherIncome(newBizStatPrintDTO.getOtherIncome());
      }
      if (startDate != null && endDate != null) {
        BusinessAccountSearchConditionDTO searchConditionDTO = new BusinessAccountSearchConditionDTO();
        searchConditionDTO.setAccountEnum(BusinessAccountEnum.STATUS_SAVE);
        searchConditionDTO.setStartTime(startDate);
        searchConditionDTO.setEndTime(endDate);

        searchConditionDTO.setMoneyCategory(MoneyCategory.income);
        Double incomeSum = businessAccountService.getSumBySearchCondition(shopId, searchConditionDTO);

        searchConditionDTO.setMoneyCategory(MoneyCategory.expenses);
        Double expensesSum = businessAccountService.getSumBySearchCondition(shopId, searchConditionDTO);

        bizStatPrintDTO.setOtherIncome(incomeSum);
        bizStatPrintDTO.setTotalExpend(expensesSum);
      }

      bizStatPrintDTO.setStatSum(bizStatPrintDTO.getStatSum()+bizStatPrintDTO.getOtherIncome());


      bizStatPrintDTO.setGrossProfit(com.bcgogo.utils.NumberUtil.round(bizStatPrintDTO.getStatSum()-bizStatPrintDTO.getProductCost() - bizStatPrintDTO.getOrderOtherIncomeCost()-bizStatPrintDTO.getTotalExpend(),NumberUtil.MONEY_PRECISION));
      double totalCost = bizStatPrintDTO.getProductCost()+bizStatPrintDTO.getTotalExpend();
      if(0 != bizStatPrintDTO.getStatSum())
      {
        NumberFormat nfPercent = NumberFormat.getPercentInstance();

        double grossProfitPercent = bizStatPrintDTO.getGrossProfit()/bizStatPrintDTO.getStatSum();
        grossProfitPercent = com.bcgogo.utils.NumberUtil.round(grossProfitPercent*100, 1);
        bizStatPrintDTO.setGrossProfitPercent(String.valueOf(grossProfitPercent)+"%");
//              bizStatPrintDTO.setGrossProfitPercent(nfPercent.format(grossProfitPercent));
      }
      else
      {
        bizStatPrintDTO.setGrossProfitPercent(" ");
      }
      bizStatPrintDTO.toFix(1);
      request.setAttribute("shopName",shopDTO.getName());
      request.setAttribute("bizStatPrintDTO",bizStatPrintDTO);
    }catch (Exception e){
      LOG.info("/businessStat.do");
      LOG.info("method=getBusinessStatToPrint");
      LOG.info("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(),e);
    }

//      return "stat/print/revenueCountPrint";
  }

  @RequestMapping(params = "method=getBusinessStatToPrint")
  public void getBusinessStatToPrint(HttpServletRequest request,HttpServletResponse response,String startDateStr,String endDateStr) throws  Exception
  {
    this.getBizStatPrintDTOToPrint(request,startDateStr,endDateStr);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try
    {
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BIZSTAT);
      BizStatPrintDTO bizStatPrintDTO= (BizStatPrintDTO)request.getAttribute("bizStatPrintDTO");
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO)
      {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "businessStatPrint"+String.valueOf(WebUtil.getShopId(request));

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("bizStatPrintDTO", bizStatPrintDTO);
        context.put("shopName",shopDTO.getName());
        context.put("startDateStr",startDateStr);
        context.put("endDateStr",endDateStr);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else
      {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    }
    catch(Exception e)
    {
      LOG.debug("/businessStat.do");
      LOG.debug("id:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(),e);
    }
  }



  /**
   * 营业统计页面 用于获取销售单列表
   * @param request
   * @param response
   * @param startPageNo 开始页数
   * @param maxRows     每页显示条数 初定为25
   * @param type day:按日查询 month 按月查询 year 按年查询
   * @param dateStr  查询日期
   */
  @RequestMapping(params = "method=getMemberOrder")
  public void getMemberOrder(HttpServletRequest request,HttpServletResponse response,Integer startPageNo,Integer maxRows,String type,String dateStr,String arrayType) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);

      //校验数据
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }
      Long queryDate = statUtil.getTimeLongValue(dateStr, request); //查询开始日期
      type = (StringUtil.isEmpty(type) == true ? "day" : type);
      arrayType = statUtil.getArrayTypeByType(arrayType); //排序类型:按照金额 或者时间排序
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间

      //总条数和总金额 使用 count 和 sum进行查询
      List<String> stringList = new ArrayList<String>();
      try {
        stringList = txnService.getMemberOrderCountAndSum(shopId, startTime, endTime,null);
      } catch (Exception e) {
        LOG.error("/init.do");
        LOG.error("method=getMemberOrder");
        LOG.error(e.getMessage(), e);
        LOG.error(" 营业统计数据初始化 获取会员列表出错 ");
      }

      int size = statUtil.getIntValueByIndex(stringList, 0); //获得当前查询日期内的单据条数
      Double total = statUtil.getDoubleValueByIndex(stringList, 1); //获得当前查询日期内的单据总和

      String jsonStr = ""; //返回到前台的json字符串
      List<MemberCardOrderDTO> resultList = new ArrayList<MemberCardOrderDTO>();//返回的购卡续卡单据类型
      //分页
      Pager pager = statUtil.getPager(size, startPageNo, maxRows);

      //根据开始时间、结束时间、排序类型 分页组建 获得单据列表
      if (size > 0) {
        resultList = getMemberCardOrderDTOList(shopId, startTime, endTime, arrayType, pager);
      }

      //拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
      List<String> strings = statUtil.getStringList(resultList, null);
      strings.add(String.valueOf(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION)));

      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(resultList, strings);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

      try {
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
      } catch (Exception e) {
        LOG.error("/businessStat.do");
        LOG.error("method=getMemberOrder");
        LOG.error("PrintWriter出错");
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error(e.getMessage(), e);
      }
    } catch (Exception ex) {
      LOG.error("/businessStat.do");
      LOG.error("method=getMemberOrder");
      LOG.error("获取会员列表出错");
      LOG.error(ex.getMessage(),ex);
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(ex.getMessage(), ex);
    }
  }



  /**
   * 根据shop_id、开始时间 结束时间 排序类型 分页 查询购卡续卡单据列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @param arrayType
   * @param pager
   * @return
   * @throws Exception
   */
  public List<MemberCardOrderDTO> getMemberCardOrderDTOList(long shopId,long startTime,long endTime,String arrayType,Pager pager) {

    List<MemberCardOrderDTO> memberCardOrderDTOList = null;
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);

      Long orderId = null;
      String orderIdStr = "";
      List<MemberCardOrderItemDTO> memberCardOrderItemDTOList = null;
      MemberCardOrderItemDTO memberCardOrderItemDTO = null;
      ReceivableDTO receivableDTO = null;
      MemberDTO memberDTO = null;

      try {
        //使用归属时间进行查询vest_date
        memberCardOrderDTOList = txnService.getMemberOrderListByPagerTimeArrayType(shopId, startTime, endTime, pager, arrayType,null);
      } catch (Exception ex) {
        LOG.error("/businessStat.do");
        LOG.error("method=getMemberOrder");
        LOG.error("营业统计:获得会员详细列表，查询出现异常");
        LOG.error("shopId:" + shopId);
        LOG.error(ex.getMessage(), ex);
        return null;
      }

      if (CollectionUtils.isNotEmpty(memberCardOrderDTOList)) {
        for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
          orderId = memberCardOrderDTO.getId();
          orderIdStr = orderId.toString();
          memberCardOrderDTO.setOrderNo(orderIdStr.substring(orderIdStr.length() - 8, orderIdStr.length()));
          // pageTotal += memberCardOrderDTO.getTotal();

          CustomerDTO customerDTO = userService.getCustomerById(memberCardOrderDTO.getCustomerId());
          if (customerDTO == null) {
            LOG.error("/businessStat.do");
            LOG.error("method=getMemberOrder");
            LOG.error("营业统计:获得会员详细列表，customerId不正确");
            LOG.error("member_card_order:" + memberCardOrderDTO.toString());
            LOG.error("shopId:" + shopId);
            memberCardOrderDTO.setCustomerName("**客户**");
          } else {
            memberCardOrderDTO.setCustomerName(customerDTO.getName());
          }

          memberCardOrderItemDTOList = txnService.getMemberCardOrderItemDTOByOrderId(shopId, orderId);
          if (CollectionUtils.isEmpty(memberCardOrderItemDTOList)) {
            LOG.error("/businessStat.do");
            LOG.error("method=getMemberOrder");
            LOG.error("营业统计:获得会员详细列表，memberCardOrderItme列表为空");
            LOG.error("member_card_order:" + memberCardOrderDTO.toString());
            LOG.error("shopId:" + shopId);
          }else {
            memberCardOrderItemDTO = memberCardOrderItemDTOList.get(0);
          }

          if(memberCardOrderItemDTO != null) {

            memberCardOrderDTO.setMemberAmount(memberCardOrderItemDTO.getWorth());
            if (memberCardOrderItemDTO.getSalesId() != null) {
              SalesManDTO salesManDTO = userService.getSalesManDTOById(memberCardOrderItemDTO.getSalesId());
              memberCardOrderDTO.setSalesMan(salesManDTO == null ? "" : salesManDTO.getName());
            } else {
              memberCardOrderDTO.setSalesMan("");
            }

            if (memberCardOrderItemDTO.getCardId() != null) {
              MemberCard memberCard = membersService.getMemberCardById(shopId, memberCardOrderItemDTO.getCardId());
              if (memberCard == null) {
                LOG.error("/businessStat.do");
                LOG.error("method=getMemberOrder");
                LOG.error("营业统计:获得会员详细列表，获取卡失败");
                LOG.error("member_card_order:" + memberCardOrderDTO.toString());
                LOG.error("shopId:" + shopId);
                memberCardOrderDTO.setMemberCardName("");
                memberCardOrderDTO.setMemberCardType("");
              } else {
                memberCardOrderDTO.setMemberCardName(memberCard.getName());
                memberCardOrderDTO.setMemberCardType(memberCard.getType() == "valueCard" ? "储值卡" : "计次卡");
              }
            }
          }

          memberDTO = membersService.getMemberByCustomerId(shopId, memberCardOrderDTO.getCustomerId());
          if (memberDTO == null) {
            LOG.error("/businessStat.do");
            LOG.error("method=getMemberOrder");
            LOG.error("营业统计:获得会员详细列表，获取会员信息出错");
            LOG.error("member_card_order:" + memberCardOrderDTO.toString());
            LOG.error("shopId:" + shopId);
            memberCardOrderDTO.setMemberNo("");
          } else {
            memberCardOrderDTO.setMemberNo(memberDTO.getMemberNo());
          }
        }
      }
    } catch (Exception e) {
      LOG.error("/businessStat.do");
      LOG.error("method=getMemberOrder");
      LOG.error("营业统计:获得会员详细列表出错");
      LOG.error("shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return memberCardOrderDTOList;
  }

  @RequestMapping(params = "method=getBusinessStatDetailToPrint")
  public void getBusinessStatDetailToPrint(HttpServletRequest request,HttpServletResponse response)
  {
    String dataList = request.getParameter("dataList");
    String costTotal = request.getParameter("costTotal");
    String settleTotal = request.getParameter("settleTotal");
    String debtTotal = request.getParameter("debtTotal");
    String discountTotal = request.getParameter("discountTotal");
    String profitTotal = request.getParameter("profitTotal");
    String pagerTotal = request.getParameter("pagerTotal");
    String total = request.getParameter("total");
    String startDateStr = request.getParameter("startDateStr");
    String endDateStr = request.getParameter("endDateStr");
    String orderType = request.getParameter("orderType");

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);

    List<OrderSearchResultDTO> orderSearchResultDTOList = null;
    List<RepairOrderDTO> repairOrderDTOList = null;
    List<ItemIndexDTO> itemIndexDTOList = null;

    try
    {
      Gson gson = new Gson();
      PrintTemplateDTO printTemplateDTO = null;
      String myTemplateName = "";
      if("sale".equals(orderType))
      {
        orderSearchResultDTOList =  gson.fromJson(dataList,new TypeToken<List<OrderSearchResultDTO>>(){}.getType());

        if(CollectionUtils.isNotEmpty(orderSearchResultDTOList))
        {
          for(OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOList)
          {
            orderSearchResultDTO.setGrossProfit(NumberUtil.round(orderSearchResultDTO.getGrossProfit(),NumberUtil.MONEY_PRECISION));
            if("销售退货单".equals(orderSearchResultDTO.getOrderTypeValue()))
            {
              orderSearchResultDTO.setAfterMemberDiscountTotal(orderSearchResultDTO.getAmount());
            }
            orderSearchResultDTO.setAfterMemberDiscountTotal(NumberUtil.round(orderSearchResultDTO.getAfterMemberDiscountTotal(),NumberUtil.MONEY_PRECISION));
          }
        }

        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BIZSTAT_SALES_DETAIL);
        myTemplateName = "businessSalesDetail"+ String.valueOf(WebUtil.getShopId(request));
      }
      if("wash".equals(orderType))
      {
        itemIndexDTOList =  gson.fromJson(dataList,new TypeToken<List<ItemIndexDTO>>(){}.getType());

        for(ItemIndexDTO indexDTO : itemIndexDTOList)
        {
          indexDTO.setItemCount(null==indexDTO.getItemCount()?1D:indexDTO.getItemCount());
        }

        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BIZSTAT_WASH_DETAIL);
        myTemplateName = "businessWashDetail"+ String.valueOf(WebUtil.getShopId(request));
      }
      if("repair".equals(orderType))
      {
        repairOrderDTOList =  gson.fromJson(dataList,new TypeToken<List<RepairOrderDTO>>(){}.getType());

        if(CollectionUtils.isNotEmpty(repairOrderDTOList))
        {
          for(RepairOrderDTO repairOrderDTO : repairOrderDTOList)
          {
            repairOrderDTO.setAfterMemberDiscountTotal(NumberUtil.round(repairOrderDTO.getAfterMemberDiscountTotal(),NumberUtil.MONEY_PRECISION));
          }
        }

        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BIZSTAT_REPAIR_DETAIL);
        myTemplateName = "businessRepairDetail"+ String.valueOf(WebUtil.getShopId(request));
      }
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO)
      {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("startDateStr",startDateStr);
        context.put("endDateStr",endDateStr);
        context.put("costTotal",NumberUtil.round(Double.valueOf(costTotal),NumberUtil.MONEY_PRECISION));
        context.put("settleTotal",NumberUtil.round(Double.valueOf(settleTotal),NumberUtil.MONEY_PRECISION));
        context.put("debtTotal",NumberUtil.round(Double.valueOf(debtTotal),NumberUtil.MONEY_PRECISION));
        context.put("discountTotal",NumberUtil.round(Double.valueOf(discountTotal),NumberUtil.MONEY_PRECISION));
        context.put("profitTotal",NumberUtil.round(Double.valueOf(profitTotal),NumberUtil.MONEY_PRECISION));
        context.put("pagerTotal",NumberUtil.round(Double.valueOf(pagerTotal),NumberUtil.MONEY_PRECISION));
        context.put("total",NumberUtil.round(Double.valueOf(total),NumberUtil.MONEY_PRECISION));
        context.put("orderSearchResultDTOList",orderSearchResultDTOList);
        context.put("repairOrderDTOList",repairOrderDTOList);
        context.put("itemIndexDTOList",itemIndexDTOList);

        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else
      {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    }
    catch(Exception e)
    {
      LOG.error("method=getBusinessStatDetailToPrint");
      LOG.error(e.getMessage(),e);
    }
  }

  @Autowired
  private StatUtil statUtil;

}
