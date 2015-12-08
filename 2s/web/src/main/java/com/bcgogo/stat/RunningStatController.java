package com.bcgogo.stat;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Sort;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PayableHistoryRecordDTO;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.ReceptionRecordDTO;
import com.bcgogo.txn.dto.RunningStatDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.IRunningStatService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.FileUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * 流水统计相关controller
 * Date: 12-8-29
 * Time: 下午9:48
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/runningStat.do")
public class RunningStatController {
  private static final Log LOG = LogFactory.getLog(BizStatController.class);
  public static final int pageSize = 10;     //每页显示的条数
  public static final int defaultSize = 1;  //每次查询默认查询的条数
  public static final String QUERY_TYPE_DAY = "day"; //流水统计按天查询
  public static final String QUERY_TYPE_MONTH = "month";//流水统计按月查询
  public static final String QUERY_TYPE_YEAR = "year"; //流水统计按年查询
//  public static final int defaultYear = 2012;//默认年份为2012
  public static final Sort timeSortDesc = new Sort(" stat_date ", "desc");//按照统计日期降序排列
  public static final Sort daySortAsc = new Sort(" stat_day ", "asc");//按照统计日期升序排列
  public static final Sort timeSortAsc = new Sort(" stat_date ", "asc");//按照统计日期升序排列
  /**
   * @param model   model
   * @param request request
   * @return 用于向页面跳转 什么事情都不做
   */
  @RequestMapping(params = "method=getRunningStat")
  public String getRunningStat(ModelMap model, HttpServletRequest request) {
    model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
    return "stat/runningStat";
  }


  /**
   * 根据前台传递的参数和查询类型 获得数据用于柱状图
   *
   * @param request
   * @param response
   * @param modelMap
   * @param type
   * @param year
   * @param month
   * @param day
   */
  @RequestMapping(params = "method=getRunningStatByType")
  public void getRunningStatByType(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
                                   String type, Integer year, Integer month, Integer day) {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);

    StringBuilder jsonStr = new StringBuilder();
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) {
        return;
      }

      if (StringUtil.isEmpty(type)) {
        throw new Exception("系统获得的查询类型不正确!");
      }
      if (year == null || month == null || day == null) {
        throw new Exception("查询时间不正确!");
      }

      List<RunningStatDTO> returnDTOList = null;

      if (QUERY_TYPE_DAY.equals(type)) {
        returnDTOList = this.getDayRunningStatList(shopId, year, month);
        Map<Long, RunningStatDTO> map = runningStatService.getDayRunningStatChangeMap(shopId, (long) year, (long) month);
        if (MapUtils.isNotEmpty(map)) {
          if (CollectionUtils.isEmpty(returnDTOList)) {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(year, month - 1, 1, 0, 0, 0);
            returnDTOList = new ArrayList<RunningStatDTO>();
            int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
            for (int index = 1; index <= lastDayOfCurrentMonth; index++) {
              RunningStatDTO runningStatDTO = new RunningStatDTO();
              runningStatDTO.setStatYear((long) year);
              runningStatDTO.setStatMonth((long) month);
              runningStatDTO.setStatDay((long) index);
              returnDTOList.add(runningStatDTO);
            }
          }
          for (RunningStatDTO statDTO : returnDTOList) {
            RunningStatDTO dto = map.get(statDTO.getStatDay());
            if (dto != null) {
              dto.setIncomeSum(dto.getCashIncome() + dto.getUnionPayIncome() + dto.getChequeIncome());
              dto.setExpenditureSum(dto.getCashExpenditure() + dto.getChequeExpenditure() + dto.getUnionPayExpenditure());
              dto.setRunningSum(dto.getIncomeSum() - dto.getExpenditureSum());
              statDTO = runningStatService.addRunningStatDate(statDTO, dto);
            }
          }
        }
      } else if (QUERY_TYPE_MONTH.equals(type)) {
        returnDTOList = this.getMonthRunningStatList(shopId, year);

         //merge month
        Map<Long, RunningStatDTO> monthMap = runningStatService.getMonthRunningStatChangeMap(shopId, year);
        if (MapUtils.isNotEmpty(monthMap)) {

          if (CollectionUtils.isEmpty(returnDTOList)) {
            returnDTOList = new ArrayList<RunningStatDTO>();
            for (int index = 1; index <= 12; index++) {
              RunningStatDTO businessStatDTO = new RunningStatDTO();
              businessStatDTO.setStatYear((long) year);
              businessStatDTO.setStatMonth((long) index);
              returnDTOList.add(businessStatDTO);
            }
          }

          for (RunningStatDTO statDTO : returnDTOList) {
            RunningStatDTO dto = monthMap.get(statDTO.getStatMonth());
            if (dto != null) {
              dto.setIncomeSum(dto.getCashIncome() + dto.getUnionPayIncome() + dto.getChequeIncome());
              dto.setExpenditureSum(dto.getCashExpenditure() + dto.getChequeExpenditure() + dto.getUnionPayExpenditure());
              dto.setRunningSum(dto.getIncomeSum() - dto.getExpenditureSum());
              statDTO = runningStatService.addRunningStatDate(statDTO, dto);
            }
          }
        }
      } else if (QUERY_TYPE_YEAR.equals(type)) {
        returnDTOList = runningStatService.getYearRunningStat(shopId);
      }
      jsonStr.append(JsonUtil.listToJsonNoQuote(returnDTOList));
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getRunningStatByType");
      LOG.error("系统流水统计出现错误，获得柱状图数据异常");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      LOG.error(e.getMessage(), e);
    }

    try {

      if (StringUtil.isEmpty(jsonStr.toString())) {
        jsonStr.append(JsonUtil.EMPTY_JSON_STRING);
      }

      PrintWriter writer = response.getWriter();
      writer.write(jsonStr.toString());
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getRunningStatDate");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      LOG.error(e.getMessage(), e);
      LOG.error("系统流水统计出现错误，获得柱状图数据异常");
    }
  }


  /**
   * 获得每月的流水数据
   *
   * @param shopId 店铺id
   * @param year   查询年份
   * @return 查询获得的list
   */
  public List<RunningStatDTO> getMonthRunningStatList(long shopId, int year) {

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    //返回值
    List<RunningStatDTO> returnDTOList = new ArrayList<RunningStatDTO>();

    //获取日期
    Calendar currentCalendar = Calendar.getInstance();
    int currentYear = currentCalendar.get(Calendar.YEAR);
    //获取上一年的最新数据
    List<RunningStatDTO> lastYearList = txnService.getRunningStatByYearMonthDay(shopId, year - 1, null, null, defaultSize, timeSortDesc);
    if (CollectionUtils.isEmpty(lastYearList)) {
      returnDTOList.add(new RunningStatDTO());
    } else {
      returnDTOList.add(lastYearList.get(0));
    }

    //这一年的最后一个月的最新数据
    List<RunningStatDTO> thisYearLastList = txnService.getRunningStatByYearMonthDay(shopId, year, null, null, defaultSize, timeSortDesc);

    //这一年的开始数据
    List<RunningStatDTO> thisYearEarliestList = txnService.getRunningStatByYearMonthDay(shopId, year, null, null, defaultSize, timeSortAsc);

    if (CollectionUtils.isEmpty(thisYearLastList) || CollectionUtils.isEmpty(thisYearEarliestList)) {
      return null;
    }

    RunningStatDTO lastRunningStatDTO = thisYearLastList.get(0);
    RunningStatDTO earlyRunningStatDTO = thisYearEarliestList.get(0);

    int beginMonth = earlyRunningStatDTO.getStatMonth().intValue();
    int endMonth = lastRunningStatDTO.getStatMonth().intValue();
    int lastDay = lastRunningStatDTO.getStatDay().intValue();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" and ( ");
    for (int i = beginMonth; i <= endMonth; i++) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(year, i - 1, 1, 0, 0, 0);
      int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
      if (year == currentYear && i == endMonth) {
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
      RunningStatDTO runningStatDTO = new RunningStatDTO();
      runningStatDTO.setStatMonth((long) index);
      runningStatDTO.setStatYear((long)year);
      returnDTOList.add(runningStatDTO);
    }
    List<RunningStatDTO> monthList = new ArrayList<RunningStatDTO>();
    if (!StringUtil.isEmpty(stringBuilder.toString())) {
      monthList = txnService.getRunningStatMonth(shopId, year, stringBuilder.toString());
    }
    if (CollectionUtils.isEmpty(monthList)) {
      return null;
    }
    returnDTOList.addAll(monthList);

    int size = returnDTOList.size() - 1;
    for (int j = size; j >= 1; j--) {
      RunningStatDTO runningStatDTO = returnDTOList.get(j);
      runningStatDTO.setRunningStatDateStr(year + "年" + runningStatDTO.getStatMonth() + "月");
      RunningStatDTO lastMonthDTO = returnDTOList.get(j - 1);
      runningStatDTO = runningStatService.minusRunningStatDate(runningStatDTO,lastMonthDTO);
    }

    for (int index = endMonth + 1; index <= 12; index++) {
      RunningStatDTO runningStatDTO = new RunningStatDTO();
      runningStatDTO.setRunningStatDateStr(year + "年" + index + "月");
      runningStatDTO.setStatMonth((long) index);
      runningStatDTO.setStatYear((long)year);
      returnDTOList.add(runningStatDTO);
    }

    returnDTOList.remove(0);
    return returnDTOList;
  }


  /**
   * 根据年份和月份获得该月份每日的流水数据
   *
   * @param shopId 店铺id
   * @param year   年份
   * @param month  月份
   * @return
   */
  public List<RunningStatDTO> getDayRunningStatList(long shopId, int year, int month) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    List<RunningStatDTO> returnDTOList = new ArrayList<RunningStatDTO>();
    List<RunningStatDTO> lastMonthList = new ArrayList<RunningStatDTO>();
    Calendar calendar = Calendar.getInstance();

    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONDAY) + 1;
    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

    calendar.set(year, month - 1, 1, 0, 0, 0);
    int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

     List<RunningStatDTO> runningStatDTOList = null;
    if(month != 1){
      runningStatDTOList = txnService.getRunningStatByYearMonthDay(shopId, year, month - 1, null, defaultSize, timeSortDesc);
    }else{
      runningStatDTOList = txnService.getRunningStatByYearMonthDay(shopId, year-1, 12, null, defaultSize, timeSortDesc);
    }
    if (CollectionUtils.isEmpty(runningStatDTOList)) {
      returnDTOList.add(new RunningStatDTO());
    } else {
      returnDTOList.add(runningStatDTOList.get(0));
    }
    lastMonthList.add(returnDTOList.get(0));


    List<RunningStatDTO> dayList = null;

    if (month == currentMonth && year == currentYear) {
      dayList = txnService.getRunningStatByYearMonthDay(shopId, year, month, null, currentDay, daySortAsc);
    } else {
      dayList = txnService.getRunningStatByYearMonthDay(shopId, year, month, null, lastDayOfCurrentMonth, daySortAsc);
    }

    if (CollectionUtils.isEmpty(dayList)) {
      return null;
    }

    lastMonthList.addAll(dayList);

    RunningStatDTO runningStatDTO = dayList.get(0);//这个月最早的一天
    if (runningStatDTO.getStatDay() != null) {
      int startDay = runningStatDTO.getStatDay().intValue();
      for (int i = 1; i < startDay; i++) {
        RunningStatDTO statDTO = new RunningStatDTO();
        statDTO.setStatYear((long) year);
        statDTO.setStatMonth((long) month);
        statDTO.setStatDay((long) i);
        statDTO.setRunningStatDateStr(statDTO.getStatYear() + "年" + statDTO.getStatMonth() + "月" + statDTO.getStatDay() + "日");
        returnDTOList.add(statDTO);
      }
    }


    int size = lastMonthList.size() - 1;
    for (int j = size; j >= 1; j--) {
      runningStatDTO = lastMonthList.get(j);
      runningStatDTO.setRunningStatDateStr(runningStatDTO.getStatYear() + "年" + runningStatDTO.getStatMonth() + "月" + runningStatDTO.getStatDay() + "日");
      RunningStatDTO lastMonthDTO = lastMonthList.get(j - 1);
      runningStatDTO = runningStatService.minusRunningStatDate(runningStatDTO,lastMonthDTO);
    }

    lastMonthList.remove(0);
    returnDTOList.addAll(lastMonthList);


    runningStatDTO = returnDTOList.get(returnDTOList.size() - 1);
    int lastDay = runningStatDTO.getStatDay().intValue();
    for (int k = lastDay + 1; k <= lastDayOfCurrentMonth; k++) {
      RunningStatDTO runningStatDTOTmp = new RunningStatDTO();
      runningStatDTOTmp.setStatYear((long) year);
      runningStatDTOTmp.setStatMonth((long) month);
      runningStatDTOTmp.setStatDay((long) k);
      runningStatDTOTmp.setRunningStatDateStr(runningStatDTOTmp.getStatYear() + "年" + runningStatDTOTmp.getStatMonth() + "月" + runningStatDTOTmp.getStatDay() + "日");
      returnDTOList.add(runningStatDTOTmp);
    }

    returnDTOList.remove(0);
    return returnDTOList;
  }


  /**
   * 获得流水统计左边图标需要的数据
   *
   * @param request
   * @param response
   * @param modelMap
   * @param year
   * @param month
   * @param day
   */
  @RequestMapping(params = "method=getRunningStatDate")
  public void getRunningStatDate(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Integer year, Integer month, Integer day) {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);

    String jsonStr = "";
    List<RunningStatDTO> returnResultList = new ArrayList<RunningStatDTO>();
    Long shopId = null;

    try {
      shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null || year == null || month == null || day == null) {
        LOG.error("/runningStat.do");
        LOG.error("method = getRunningStatDate");
        LOG.error("流水获得流水数据参数为空");
        return;
      }

      RunningStatDTO dayRunningStatDTO = runningStatService.getDayRunningStat(shopId, year, month, day);
      double currentTotalDebt = Math.abs(NumberUtil.toReserve(runningStatService.getTotalDebtByShopId(shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE),NumberUtil.MONEY_PRECISION));

      dayRunningStatDTO.setCustomerTotalReceivable(currentTotalDebt);

      double customerTotalPayable = Math.abs(NumberUtil.toReserve(runningStatService.getTotalDebtByShopId(shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE),NumberUtil.MONEY_PRECISION));
      dayRunningStatDTO.setCustomerTotalPayable(customerTotalPayable);


      double supplierTotalPayable = Math.abs(NumberUtil.toReserve(runningStatService.getSupplierTotalDebtByShopId(shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE),NumberUtil.MONEY_PRECISION));

      dayRunningStatDTO.setSupplierTotalPayable(supplierTotalPayable);

      double supplierTotalReceivable = Math.abs(NumberUtil.toReserve(runningStatService.getSupplierTotalDebtByShopId(shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE),NumberUtil.MONEY_PRECISION));
      dayRunningStatDTO.setSupplierTotalReceivable(supplierTotalReceivable);

      returnResultList.add(dayRunningStatDTO);

      //每个月的数据
      returnResultList.add(this.getMonthRunningStat(shopId, year, month, day));

      returnResultList.add(this.getYearRunningStat(shopId, year, month, day));

      //流水更改进行合并
      mergeRunningStatAndRunningStatChange(returnResultList,shopId,year,month,day);

    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method = getRunningStatDate");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      LOG.error(e.getMessage(), e);
      returnResultList.clear();
      RunningStatDTO runningStatDTO = new RunningStatDTO();
      returnResultList.add(runningStatDTO);
      returnResultList.add(runningStatDTO);
      returnResultList.add(runningStatDTO);
    }



    jsonStr = JsonUtil.listToJsonNoQuote(returnResultList);
    modelMap.addAttribute("jsonStr", jsonStr);
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getRunningStatDate");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("year:" + year + ",month:" + month + ",day" + day);
      LOG.error(e.getMessage(), e);
    }
  }




  //merge BusinessStat 与RunningStatChange
  private void mergeRunningStatAndRunningStatChange(List<RunningStatDTO> runningStatDTOList, Long shopId, int year, int month, int day) throws Exception{
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    if (runningStatDTOList == null || runningStatDTOList.size() != 3) {
      throw new Exception("mergeBusinessStatList For Year Month And Day illegal");
    }
    //天
    RunningStatDTO runningStatOfToday = runningStatDTOList.get(0);
    RunningStatDTO businessStatChangeDTO = runningStatService.getRunningStatChangeDTOByShopIdYearMonthDay(shopId, (long) year, (long) month, (long) day);
    if (businessStatChangeDTO != null) {
      runningStatOfToday = runningStatService.addRunningStatDate(runningStatOfToday, businessStatChangeDTO);

    }
    //月
    RunningStatDTO runningStatOfMonth = runningStatDTOList.get(1);
    List<RunningStatDTO> runningStatChangeList = runningStatService.getRunningStatChangeByYearMonth(shopId, year, month);
    if (CollectionUtils.isNotEmpty(runningStatChangeList)) {
      for (RunningStatDTO statDTO : runningStatChangeList) {
        runningStatOfMonth = runningStatService.addRunningStatDate(runningStatOfMonth, statDTO);
      }
    }
    //年
    RunningStatDTO runningStatOfYear = runningStatDTOList.get(2);
    businessStatChangeDTO = runningStatService.sumRunningStatChangeForYearMonth(shopId, (long) year,null);
    if (businessStatChangeDTO != null) {
      businessStatChangeDTO.setIncomeSum(businessStatChangeDTO.getCashIncome() + businessStatChangeDTO.getUnionPayIncome() + businessStatChangeDTO.getChequeIncome());
      businessStatChangeDTO.setExpenditureSum(businessStatChangeDTO.getCashExpenditure() + businessStatChangeDTO.getUnionPayExpenditure() + businessStatChangeDTO.getChequeExpenditure());
      businessStatChangeDTO.setRunningSum(businessStatChangeDTO.getIncomeSum() - businessStatChangeDTO.getExpenditureSum());
      runningStatOfYear = runningStatService.addRunningStatDate(runningStatOfYear, businessStatChangeDTO);
    }
  }


  /**
   * 获得某一个月的流水统计数据
   *
   * @param shopId
   * @param year
   * @param month
   * @param day
   * @return
   */
  public RunningStatDTO getMonthRunningStat(long shopId, Integer year, Integer month, Integer day) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RunningStatDTO returnRunningStatDTO = new RunningStatDTO();
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    List<RunningStatDTO> lastMonthRunningStatDTO = null;
    if (month != 1) {
      lastMonthRunningStatDTO = txnService.getRunningStatByYearMonthDay(shopId, year, month - 1, null, defaultSize, timeSortDesc);
      if (CollectionUtils.isEmpty(lastMonthRunningStatDTO)) {
        lastMonthRunningStatDTO = new ArrayList<RunningStatDTO>();
        lastMonthRunningStatDTO.add(returnRunningStatDTO);
      }
    } else {
      lastMonthRunningStatDTO = txnService.getRunningStatByYearMonthDay(shopId, year-1, 12, null, defaultSize, timeSortDesc);
      if (CollectionUtils.isEmpty(lastMonthRunningStatDTO)) {
        lastMonthRunningStatDTO = new ArrayList<RunningStatDTO>();
        lastMonthRunningStatDTO.add(returnRunningStatDTO);
      }
    }

    List<RunningStatDTO> thisMonthDTO = txnService.getRunningStatByYearMonthDay(shopId, year, month, null, defaultSize, timeSortDesc);
    if (CollectionUtils.isEmpty(thisMonthDTO)) {
      return returnRunningStatDTO;
    }

    returnRunningStatDTO = runningStatService.minusRunningStatDate(thisMonthDTO.get(0), lastMonthRunningStatDTO.get(0));
    return returnRunningStatDTO;
  }

  /**
   * 获得某一年的统计数据
   *
   * @param shopId
   * @param year
   * @param month
   * @param day
   * @return
   */
  public RunningStatDTO getYearRunningStat(long shopId, Integer year, Integer month, Integer day) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    RunningStatDTO returnRunningStatDTO = new RunningStatDTO();
    List<RunningStatDTO> yearRunningStatDTO = txnService.getRunningStatByYearMonthDay(shopId, year, null, null, defaultSize, timeSortDesc);
    if (CollectionUtils.isEmpty(yearRunningStatDTO)) {
      return returnRunningStatDTO;
    } else {
      List<RunningStatDTO> lastYearRunningStatDTO = txnService.getRunningStatByYearMonthDay(shopId, year - 1, null, null, defaultSize, timeSortDesc);
      if (CollectionUtils.isEmpty(lastYearRunningStatDTO)) {
        return yearRunningStatDTO.get(0);
      } else {
        returnRunningStatDTO = runningStatService.minusRunningStatDate(yearRunningStatDTO.get(0), lastYearRunningStatDTO.get(0));
        return returnRunningStatDTO;
      }
    }
  }


  /**
   * 每日收入记录
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param type
   * @param dateStr
   * @param arrayType
   */
  @RequestMapping(params = "method=getIncomeDetailByDay")
  public void getIncomeDetailByDay(HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String type, String dateStr, String arrayType) {

    String jsonStr = "";

    try {
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }
      ITxnService txnService = ServiceManager.getService(ITxnService.class);

      type = QUERY_TYPE_DAY;

      Long queryDate = statUtil.getTimeLongValue(dateStr, request);
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间
      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime);
      int size = 0;
      double total = 0;

      List<String> stringList = new ArrayList<String>();

      List<ReceptionRecordDTO> resultList = new ArrayList<ReceptionRecordDTO>();

      try {
        stringList = txnService.countReceptionRecordByReceptionDate(shopId, startTime, endTime);
      } catch (Exception e) {
        LOG.error("/runningStat.do");
        LOG.error("method=getIncomeDetailByDay");
        LOG.error(e.getMessage(), e);
        LOG.error("流水统计 获取每天收入列表出错 ");
      }

      size = statUtil.getIntValueByIndex(stringList, 0); //获得当前查询日期内的单据条数
      total = statUtil.getDoubleValueByIndex(stringList, 1); //获得当前查询日期内的单据总和

      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      pager.setStartDateStr(startTimeStr);
      pager.setEndDateStr(endTimeStr);
      if (size > 0) {
        resultList = statUtil.getReceptionRecordDTOList(shopId, startTime, endTime, arrayType, pager, type);
      }

      //拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
      List<String> strings = statUtil.getRunningIncomeStringList(resultList);
      strings.add(String.valueOf(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION)));

      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(resultList, strings);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error(e.getMessage(), e);
      LOG.error("流水统计 获取每天收入列表出错 ");
    }

    if (StringUtil.isEmpty(jsonStr)) {
      jsonStr = JsonUtil.EMPTY_JSON_STRING;
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error("printWriter");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }


  /**
   * 每月收入记录
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param type
   * @param dateStr
   * @param arrayType
   */
  @RequestMapping(params = "method=getIncomeDetailByMonth")
  public void getIncomeDetailByMonth(HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String type, String dateStr, String arrayType) {

    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    String jsonStr = "";

    try {
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }

      Long queryDate = statUtil.getTimeLongValue(dateStr, request);

      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_CN2,queryDate);

      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.setTimeInMillis(queryDate);
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH) + 1;

      double total = 0;

      List<RunningStatDTO> resultList = this.getDayRunningStatList(shopId, year, month);
      Map<Long, RunningStatDTO> map = runningStatService.getDayRunningStatChangeMap(shopId, (long) year, (long) month);
      if (MapUtils.isNotEmpty(map)) {
        if (CollectionUtils.isEmpty(resultList)) {
          calendar = Calendar.getInstance();
          calendar.clear();
          calendar.set(year, month - 1, 1, 0, 0, 0);
          resultList = new ArrayList<RunningStatDTO>();
          int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
          for (int index = 1; index <= lastDayOfCurrentMonth; index++) {
            RunningStatDTO runningStatDTO = new RunningStatDTO();
            runningStatDTO.setStatYear((long) year);
            runningStatDTO.setStatMonth((long) month);
            runningStatDTO.setStatDay((long) index);
            runningStatDTO.setRunningStatDateStr(runningStatDTO.getStatYear() + "年" + runningStatDTO.getStatMonth() + "月" + runningStatDTO.getStatDay() + "日");
            resultList.add(runningStatDTO);
          }
        }
        for (RunningStatDTO statDTO : resultList) {
          RunningStatDTO dto = map.get(statDTO.getStatDay());
          if (dto != null) {
            dto.setIncomeSum(dto.getCashIncome() + dto.getUnionPayIncome() + dto.getChequeIncome());
            dto.setExpenditureSum(dto.getCashExpenditure() + dto.getChequeExpenditure() + dto.getUnionPayExpenditure());
            dto.setRunningSum(dto.getIncomeSum() - dto.getExpenditureSum());
            statDTO = runningStatService.addRunningStatDate(statDTO, dto);
          }
        }
      }

      int size = CollectionUtils.isEmpty(resultList) == true ? 0 : resultList.size();
      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      pager.setStartDateStr(startTimeStr);
      List<RunningStatDTO> runningStatDTOList = null;
      if (CollectionUtils.isNotEmpty(resultList)) {
        size = resultList.size();
        runningStatDTOList = new ArrayList<RunningStatDTO>();
        for(int index = 0;index < size;index ++){
          if(index < pager.getRowEnd() && index >= pager.getRowStart()){
            runningStatDTOList.add(resultList.get(index));
          }
        }
      }

      //拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
      List<String> strings = statUtil.getRunningStringList(null, runningStatDTOList,null,statUtil.INCOME);
      strings.add(String.valueOf(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION)));

      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(runningStatDTOList, strings);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error(e.getMessage(), e);
      LOG.error("流水统计 获取每天收入列表出错 ");
    }

    if (StringUtil.isEmpty(jsonStr)) {
      jsonStr = JsonUtil.EMPTY_JSON_STRING;
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error("printWriter");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }


  /**
   * 每年收入记录
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param type
   * @param dateStr
   * @param arrayType
   */
  @RequestMapping(params = "method=getIncomeDetailByYear")
  public void getIncomeDetailByYear(HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String type, String dateStr, String arrayType) {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    String jsonStr = "";

    try {
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }

      Long queryDate = statUtil.getTimeLongValue(dateStr, request);
      String startDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_CN3,queryDate);
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.setTimeInMillis(queryDate);
      int year = calendar.get(Calendar.YEAR);

      double total = 0;

      List<RunningStatDTO> resultList = this.getMonthRunningStatList(shopId, year);


      //merge month
      Map<Long, RunningStatDTO> monthMap = runningStatService.getMonthRunningStatChangeMap(shopId, year);
      if (MapUtils.isNotEmpty(monthMap)) {

        if (CollectionUtils.isEmpty(resultList)) {
          resultList = new ArrayList<RunningStatDTO>();
          for (int index = 1; index <= 12; index++) {
            RunningStatDTO businessStatDTO = new RunningStatDTO();
            businessStatDTO.setStatYear((long) year);
            businessStatDTO.setStatMonth((long) index);
            resultList.add(businessStatDTO);
          }
        }

        for (RunningStatDTO statDTO : resultList) {
          RunningStatDTO dto = monthMap.get(statDTO.getStatMonth());
          if (dto != null) {
            dto.setIncomeSum(dto.getCashIncome() + dto.getUnionPayIncome() + dto.getChequeIncome());
            dto.setExpenditureSum(dto.getCashExpenditure() + dto.getChequeExpenditure() + dto.getUnionPayExpenditure());
            dto.setRunningSum(dto.getIncomeSum() - dto.getExpenditureSum());
            statDTO = runningStatService.addRunningStatDate(statDTO, dto);
          }
        }
      }

      int size = CollectionUtils.isEmpty(resultList) == true ? 0 : resultList.size();
      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      pager.setStartDateStr(startDateStr);
      //拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
      List<String> strings = statUtil.getRunningStringList(null, resultList, null,statUtil.INCOME);
      strings.add(String.valueOf(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION)));

      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(resultList, strings);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByYear");
      LOG.error(e.getMessage(), e);
      LOG.error("流水统计 获取每年收入列表出错 ");
    }

    if (StringUtil.isEmpty(jsonStr)) {
      jsonStr = JsonUtil.EMPTY_JSON_STRING;
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error("printWriter");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }


  /**
   * 每日支出记录
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param type
   * @param dateStr
   * @param arrayType
   */
  @RequestMapping(params = "method=getExpenditureDetailByDay")
  public void getExpenditureDetailByDay(HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String type, String dateStr, String arrayType) {

    String jsonStr = "";

    try {
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId < 0) {
        return;
      }
      ITxnService txnService = ServiceManager.getService(ITxnService.class);

      type = QUERY_TYPE_DAY;

      Long queryDate = statUtil.getTimeLongValue(dateStr, request);
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间

      String startDateStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endDateStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime);

      int size = txnService.countPayHistoryRecordByPayTime(shopId, startTime, endTime);

      List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = new ArrayList<PayableHistoryRecordDTO>();

      Pager pager = statUtil.getPager(size, startPageNo, maxRows);

      pager.setStartDateStr(startDateStr);
      pager.setEndDateStr(endDateStr);

      if (size > 0) {
        payableHistoryRecordDTOList = txnService.getPayHistoryRecordByPayTime(shopId, startTime, endTime, pager);
      }

      //拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
      List<String> strings = statUtil.getRunningStringList(null, null, payableHistoryRecordDTOList,null);

      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(payableHistoryRecordDTOList, strings);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error(e.getMessage(), e);
      LOG.error("流水统计 获取每天收入列表出错 ");
    }

    if (StringUtil.isEmpty(jsonStr)) {
      jsonStr = JsonUtil.EMPTY_JSON_STRING;
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error("printWriter");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }


  /**
   * 根据前台传递的日期 获得该日期下每一天的的流水统计 支出记录数据
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param type
   * @param dateStr
   * @param arrayType
   */
  @RequestMapping(params = "method=getExpenditureDetailByMonth")
  public void getExpenditureDetailByMonth(HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String type, String dateStr, String arrayType) {

    String jsonStr = "";
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    try {
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      Long queryDate = statUtil.getTimeLongValue(dateStr, request);

      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.setTimeInMillis(queryDate);
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH) + 1;

      List<RunningStatDTO> resultList = this.getDayRunningStatList(shopId, year, month);

      Map<Long, RunningStatDTO> map = runningStatService.getDayRunningStatChangeMap(shopId, (long) year, (long) month);
      if (MapUtils.isNotEmpty(map)) {
        if (CollectionUtils.isEmpty(resultList)) {
          calendar = Calendar.getInstance();
          calendar.clear();
          calendar.set(year, month - 1, 1, 0, 0, 0);
          resultList = new ArrayList<RunningStatDTO>();
          int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
          for (int index = 1; index <= lastDayOfCurrentMonth; index++) {
            RunningStatDTO runningStatDTO = new RunningStatDTO();
            runningStatDTO.setStatYear((long) year);
            runningStatDTO.setStatMonth((long) month);
            runningStatDTO.setStatDay((long) index);
            runningStatDTO.setRunningStatDateStr(runningStatDTO.getStatYear() + "年" + runningStatDTO.getStatMonth() + "月" + runningStatDTO.getStatDay() + "日");
            resultList.add(runningStatDTO);
          }
        }
        for (RunningStatDTO statDTO : resultList) {
          RunningStatDTO dto = map.get(statDTO.getStatDay());
          if (dto != null) {
            dto.setIncomeSum(dto.getCashIncome() + dto.getUnionPayIncome() + dto.getChequeIncome());
            dto.setExpenditureSum(dto.getCashExpenditure() + dto.getChequeExpenditure() + dto.getUnionPayExpenditure());
            dto.setRunningSum(dto.getIncomeSum() - dto.getExpenditureSum());
            statDTO = runningStatService.addRunningStatDate(statDTO, dto);
          }
        }
      }
      int size = CollectionUtils.isEmpty(resultList) == true ? 0 : resultList.size();

      Pager pager = statUtil.getPager(size, startPageNo, maxRows);
      pager.setStartDateStr(String.valueOf(year)+"年"+String.valueOf(month)+"月");

      List<RunningStatDTO> runningStatDTOList = null;
      if (CollectionUtils.isNotEmpty(resultList)) {
        size = resultList.size();
        runningStatDTOList = new ArrayList<RunningStatDTO>();
        for (int index = 0; index < size; index++) {
          if (index < pager.getRowEnd() && index >= pager.getRowStart()) {
            runningStatDTOList.add(resultList.get(index));
          }
        }
      }

      //拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
      List<String> strings = statUtil.getRunningStringList(null, runningStatDTOList, null, statUtil.EXPENDITURE);
      strings.add(String.valueOf(NumberUtil.toReserve(0.0,NumberUtil.MONEY_PRECISION)));

      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(runningStatDTOList, strings);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error(e.getMessage(), e);
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("流水统计 获取每天的支出列表出错 日期" + dateStr);
    }

    if (StringUtil.isEmpty(jsonStr)) {
      jsonStr = JsonUtil.EMPTY_JSON_STRING;
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error("printWriter");
      LOG.error(e.getMessage(), e);
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("流水统计 获取每天的支出列表出错 日期" + dateStr);
    }

  }


  /**
   * 每年支出记录
   * @param request
   * @param response
   * @param startPageNo
   * @param maxRows
   * @param type
   * @param dateStr
   * @param arrayType
   */
  @RequestMapping(params = "method=getExpenditureDetailByYear")
  public void getExpenditureDetailByYear(HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String type, String dateStr, String arrayType) {
    String jsonStr = "";
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    try {
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      Long queryDate = statUtil.getTimeLongValue(dateStr, request);

      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.setTimeInMillis(queryDate);
      int year = calendar.get(Calendar.YEAR);
      double total = 0;

      List<RunningStatDTO> resultList = this.getMonthRunningStatList(shopId, year);

       //merge month
      Map<Long, RunningStatDTO> monthMap = runningStatService.getMonthRunningStatChangeMap(shopId, year);
      if (MapUtils.isNotEmpty(monthMap)) {

        if (CollectionUtils.isEmpty(resultList)) {
          resultList = new ArrayList<RunningStatDTO>();
          for (int index = 1; index <= 12; index++) {
            RunningStatDTO businessStatDTO = new RunningStatDTO();
            businessStatDTO.setStatYear((long) year);
            businessStatDTO.setStatMonth((long) index);
            resultList.add(businessStatDTO);
          }
        }

        for (RunningStatDTO statDTO : resultList) {
          RunningStatDTO dto = monthMap.get(statDTO.getStatMonth());
          if (dto != null) {
            dto.setIncomeSum(dto.getCashIncome() + dto.getUnionPayIncome() + dto.getChequeIncome());
            dto.setExpenditureSum(dto.getCashExpenditure() + dto.getChequeExpenditure() + dto.getUnionPayExpenditure());
            dto.setRunningSum(dto.getIncomeSum() - dto.getExpenditureSum());
            statDTO = runningStatService.addRunningStatDate(statDTO, dto);
          }
        }
      }

      int size = CollectionUtils.isEmpty(resultList) == true ? 0 : resultList.size();

      Pager pager = statUtil.getPager(size, startPageNo, maxRows);

      pager.setStartDateStr(String.valueOf(year)+"年");

      //拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
      List<String> strings = statUtil.getRunningStringList(null, resultList, null, statUtil.EXPENDITURE);
      strings.add(String.valueOf(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION)));

      //单据总和 和相关总和数据 进行转化
      jsonStr = statUtil.getJsonStr(resultList, strings);
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());

    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByYear");
      LOG.error(e.getMessage(), e);
      LOG.error("流水统计 获取每年收入列表出错 ");
    }

    if (StringUtil.isEmpty(jsonStr)) {
      jsonStr = JsonUtil.EMPTY_JSON_STRING;
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getIncomeDetailByDay");
      LOG.error("printWriter");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=getDataToPrint")
  public void getDataToPrint(HttpServletRequest request, HttpServletResponse response) {
    String debtSteam = request.getParameter("debtSteam");
    String deposit = request.getParameter("deposit");
    String dataList = request.getParameter("dataList");
    String bankTotal = request.getParameter("bank");
    String checkTotal = request.getParameter("check");
    String memberAmount = request.getParameter("memberAmount");
    String settleTotal = request.getParameter("settleTotal");
    String debtTotal = request.getParameter("debt");
    String discountTotal = request.getParameter("discount");
    String pageTotal = request.getParameter("pageTotal");
    String total = request.getParameter("total");
    String startDateStr = request.getParameter("startDateStr");
    String endDateStr = request.getParameter("endDateStr");
    String runningType = request.getParameter("runningType");
    String cashTotal = request.getParameter("cash");
    String couponTotal = request.getParameter("coupon");
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);

    List<ReceptionRecordDTO> receptionRecordDTOList = null;
    List<RunningStatDTO> runningStatDTOList = null;
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = null;

    Long shopId = WebUtil.getShopId(request);

    Long queryDate = null;

    try {
      boolean displayMember = true;
      if(!privilegeService.verifierShopVersionResource(WebUtil.getShopVersionId(request), ResourceType.logic, LogicResource.WEB_VERSION_MEMBER_STORED_VALUE)){
        displayMember = false;
      }
      Gson gson = new Gson();
      PrintTemplateDTO printTemplateDTO = null;
      String myTemplateName = "";
      if ("dayIncome".equals(runningType)) {
        receptionRecordDTOList = gson.fromJson(dataList, new TypeToken<List<ReceptionRecordDTO>>() {
        }.getType());
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RUNNING_DAY_INCOME);
        myTemplateName = "runningDayIncomeDetail" + String.valueOf(WebUtil.getShopId(request));
        //测试本地模板
//        File templateFile = new File("G:/RUNNING_DAY_INCOME.html");
//        byte[] templateHtml = FileUtil.readFileToByteArray(templateFile);
//        printTemplateDTO.setTemplateHtml(templateHtml);
      }
      if ("monthIncome".equals(runningType)) {
        try {
          queryDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_CN2, startDateStr);
        } catch (ParseException e) {
          queryDate = System.currentTimeMillis();
        }
        if (queryDate == null) {
          queryDate = System.currentTimeMillis();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(queryDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        //获取整月的
        runningStatDTOList = this.getDayRunningStatList(shopId, year, month);

        if (CollectionUtils.isEmpty(runningStatDTOList)) {
          return;
        }

        List<String> strings = statUtil.getRunningStringList(null, runningStatDTOList, null, statUtil.INCOME);

        total = strings.get(1);
        cashTotal = strings.get(3);
        bankTotal = strings.get(4);
        checkTotal = strings.get(5);
        if(displayMember){
          memberAmount = strings.get(6);
        }else{
          memberAmount = strings.get(7);
        }
        debtTotal = strings.get(9);
        debtSteam = strings.get(8);
        couponTotal = strings.get(0); //add by litao

        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RUNNING_MONTH_INCOME);
        myTemplateName = "runningMonthIncomeDetail" + String.valueOf(WebUtil.getShopId(request));
        //测试本地模板
//        File templateFile = new File("G:/RUNNING_MONTH_INCOME.html");
//        byte[] templateHtml = FileUtil.readFileToByteArray(templateFile);
//        printTemplateDTO.setTemplateHtml(templateHtml);
      }
      if ("yearIncome".equals(runningType)) {
        runningStatDTOList = gson.fromJson(dataList, new TypeToken<List<RunningStatDTO>>() {
        }.getType());
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RUNNING_YEAR_INCOME);
        myTemplateName = "runningYearIncomeDetail" + String.valueOf(WebUtil.getShopId(request));
        //测试本地模板
//        File templateFile = new File("G:/RUNNING_YEAR_INCOME.html");
//        byte[] templateHtml = FileUtil.readFileToByteArray(templateFile);
//        printTemplateDTO.setTemplateHtml(templateHtml);
      }
      if ("dayExpend".equals(runningType)) {
        payableHistoryRecordDTOList = gson.fromJson(dataList, new TypeToken<List<PayableHistoryRecordDTO>>() {
        }.getType());
        if (CollectionUtils.isNotEmpty(payableHistoryRecordDTOList)) {
          for (PayableHistoryRecordDTO recordDTO : payableHistoryRecordDTOList) {
            if (null == recordDTO.getStrikeAmount()) {
              recordDTO.setStrikeAmount(0D);
            }

            recordDTO.setPaidTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, DateUtil
                .convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getPaidTimeStr())));
          }
        }
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RUNNING_DAY_EXPEND);
        myTemplateName = "runningDayExpendDetail" + String.valueOf(WebUtil.getShopId(request));
      }
      if ("monthExpend".equals(runningType)) {
        try {
          queryDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_CN2, startDateStr);
        } catch (ParseException e) {
          queryDate = System.currentTimeMillis();
        }
        if (queryDate == null) {
          queryDate = System.currentTimeMillis();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(queryDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        runningStatDTOList = this.getDayRunningStatList(shopId, year, month);
        if (CollectionUtils.isEmpty(runningStatDTOList)) {
          return;
        }
        List<String> strings = statUtil.getRunningStringList(null, runningStatDTOList, null, statUtil.EXPENDITURE);
        total = strings.get(1);
        cashTotal = strings.get(3);
        bankTotal = strings.get(4);
        checkTotal = strings.get(5);
        memberAmount = strings.get(6);
        debtTotal = strings.get(9);
        debtSteam = strings.get(8);
        deposit = strings.get(7);
        couponTotal = strings.get(0); //add by litao
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RUNNING_MONTH_EXPEND);
        myTemplateName = "runningMonthExpendDetail" + String.valueOf(WebUtil.getShopId(request));
      }
      if ("yearExpend".equals(runningType)) {
        runningStatDTOList = gson.fromJson(dataList, new TypeToken<List<RunningStatDTO>>() {
        }.getType());
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.RUNNING_YEAR_EXPEND);
        myTemplateName = "runningYearExpendDetail" + String.valueOf(WebUtil.getShopId(request));
      }
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

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

        String myTemplate = str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("startDateStr", startDateStr);
        context.put("endDateStr", endDateStr);
        context.put("debtSteam", debtSteam);
        context.put("deposit", deposit);
        context.put("bankTotal", bankTotal);
        context.put("checkTotal", checkTotal);
        context.put("memberAmount", memberAmount);
        context.put("debtTotal", debtTotal);
        context.put("discountTotal", discountTotal);
        context.put("pageTotal", pageTotal);
        context.put("settleTotal", settleTotal);
        context.put("total", total);
        context.put("receptionRecordDTOList", receptionRecordDTOList);
        context.put("runningStatDTOList", runningStatDTOList);
        context.put("payableHistoryRecordDTOList", payableHistoryRecordDTOList);
        context.put("cashTotal", cashTotal);
        context.put("displayMember", displayMember);
        context.put("couponTotal", couponTotal);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }
      out.close();
    } catch (Exception e) {
      LOG.error("method=getDataToPrint");
      LOG.error(e.getMessage(), e);
    }
  }

  @Autowired
  private StatUtil statUtil;
}
