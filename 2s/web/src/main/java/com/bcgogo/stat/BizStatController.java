package com.bcgogo.stat;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.AssistantStatDTO;
import com.bcgogo.stat.dto.BizStatDTO;
import com.bcgogo.stat.model.BizStatType;
import com.bcgogo.stat.service.IBizStatService;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.*;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

@Controller
@RequestMapping("/bizstat.do")
public class BizStatController {
  private static final Log LOG = LogFactory.getLog(BizStatController.class);
  public static final int pageSize = 10;                                                //页面显示条数

  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
  Calendar calendar = Calendar.getInstance();
  DecimalFormat decimalFormat = new DecimalFormat("###,###,###");


  //营业统计
  @RequestMapping(params = "method=bizstat")
  public String BizStat(ModelMap model, HttpServletRequest request) {
    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

    if (request.getSession() == null || request.getSession().getAttribute("shopId") == null) return "/";
    long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());

    Date date;
    try {
      date = simpleDateFormat.parse(request.getParameter("date"));
    } catch (Exception e) {
      LOG.debug("/bizstat.do");
      LOG.debug("method=bizstat");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      date = new Date();
    }
    calendar.setTime(date);
    calendar.setMinimalDaysInFirstWeek(1);
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    long year = calendar.get(Calendar.YEAR);
    long month = calendar.get(Calendar.MONTH) + 1;
    long day = calendar.get(Calendar.DAY_OF_MONTH);
    long week = calendar.get(Calendar.WEEK_OF_YEAR);

    request.setAttribute("statYear", year);
    request.setAttribute("statMonth", month);
    request.setAttribute("statDay", day);
    request.setAttribute("statWeek", week);

    List<BizStatDTO> bizStatDTOListOfYear = bizStatService.getShopOneYearStatByYear(shopId, year);
    request.setAttribute("carRepairStatOfCurrentYear", this.getValue(bizStatDTOListOfYear, BizStatType.CARREPAIR.toString()));
    request.setAttribute("salesStatOfCurrentYear", this.getValue(bizStatDTOListOfYear, BizStatType.SALES.toString()));
    request.setAttribute("carWashingStatOfCurrentYear", this.getValue(bizStatDTOListOfYear, BizStatType.CARWASHING.toString()));
    request.setAttribute("purchasingStatOfCurrentYear", this.getValue(bizStatDTOListOfYear, BizStatType.PURCHASING.toString()));
    request.setAttribute("incomeStatOfCurrentYear", this.getValue(bizStatDTOListOfYear, BizStatType.INCOME.toString()));
    request.setAttribute("grossProfitStatOfCurrentYear", this.getValue(bizStatDTOListOfYear, BizStatType.GROSSPROFIT.toString()));

    List<BizStatDTO> bizStatDTOListOfMonth = bizStatService.getShopOneMonthStatByYearAndMonth(shopId, year, month);
    request.setAttribute("carRepairStatOfCurrentMonth", this.getValue(bizStatDTOListOfMonth, BizStatType.CARREPAIR.toString()));
    request.setAttribute("salesStatOfCurrentMonth", this.getValue(bizStatDTOListOfMonth, BizStatType.SALES.toString()));
    request.setAttribute("carWashingStatOfCurrentMonth", this.getValue(bizStatDTOListOfMonth, BizStatType.CARWASHING.toString()));
    request.setAttribute("purchasingStatOfCurrentMonth", this.getValue(bizStatDTOListOfMonth, BizStatType.PURCHASING.toString()));
    request.setAttribute("incomeStatOfCurrentMonth", this.getValue(bizStatDTOListOfMonth, BizStatType.INCOME.toString()));
    request.setAttribute("grossProfitStatOfCurrentMonth", this.getValue(bizStatDTOListOfMonth, BizStatType.GROSSPROFIT.toString()));

    List<BizStatDTO> bizStatDTOListOfDay = bizStatService.getShopOneDayStatByYearAndMonthAndDay(shopId, year, month, day);
    request.setAttribute("carRepairStatOfCurrentDay", this.getValue(bizStatDTOListOfDay, BizStatType.CARREPAIR.toString()));
    request.setAttribute("salesStatOfCurrentDay", this.getValue(bizStatDTOListOfDay, BizStatType.SALES.toString()));
    request.setAttribute("carWashingStatOfCurrentDay", this.getValue(bizStatDTOListOfDay, BizStatType.CARWASHING.toString()));
    request.setAttribute("purchasingStatOfCurrentDay", this.getValue(bizStatDTOListOfDay, BizStatType.PURCHASING.toString()));
    request.setAttribute("incomeStatOfCurrentDay", this.getValue(bizStatDTOListOfDay, BizStatType.INCOME.toString()));
    request.setAttribute("grossProfitStatOfCurrentDay", this.getValue(bizStatDTOListOfDay, BizStatType.GROSSPROFIT.toString()));

    List<BizStatDTO> bizStatDTOListOfWeek = bizStatService.getShopOneWeekStatByYearAndWeek(shopId, year, week);
    request.setAttribute("carRepairStatOfCurrentWeek", this.getValue(bizStatDTOListOfWeek, BizStatType.CARREPAIR.toString()));
    request.setAttribute("salesStatOfCurrentWeek", this.getValue(bizStatDTOListOfWeek, BizStatType.SALES.toString()));
    request.setAttribute("carWashingStatOfCurrentWeek", this.getValue(bizStatDTOListOfWeek, BizStatType.CARWASHING.toString()));
    request.setAttribute("purchasingStatOfCurrentWeek", this.getValue(bizStatDTOListOfWeek, BizStatType.PURCHASING.toString()));
    request.setAttribute("incomeStatOfCurrentWeek", this.getValue(bizStatDTOListOfWeek, BizStatType.INCOME.toString()));
    request.setAttribute("grossProfitStatOfCurrentWeek", this.getValue(bizStatDTOListOfWeek, BizStatType.GROSSPROFIT.toString()));

    try {
      double totalAmount = ServiceManager.getService(IBizStatService.class).getInventoryTotalAmountByShopId(shopId);
      request.setAttribute("totalAmount", totalAmount);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    return "/stat/bizstat";
  }


  //营业统计
  @RequestMapping(params = "method=agentAchievements")
  public String agentAchievements(ModelMap model, HttpServletRequest request) throws BcgogoException {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) {
        return "/";
      }
      Long shopVersionId = WebUtil.getShopVersionId(request);
      if (BcgogoShopLogicResourceUtils.isVehicleConstruction(shopVersionId)) {

        IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
        int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.SERVICE);
        model.addAttribute("totalShopAchievementConfig", totalShopAchievementConfig);

        RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
        int totalNum = txnService.countServiceByCategory(shopId, null, null, CategoryType.BUSINESS_CLASSIFICATION);
        model.addAttribute("totalNum", totalNum);
        return "/stat/assistantStat/allServiceConfig";
      }
      String userGroupName = request.getParameter("userGroupName");
      String userGroupId = request.getParameter("userGroupId");
      model.addAttribute("configUserGroupName", userGroupName);
      model.addAttribute("configUserGroupId", userGroupId);

      IUserService userService = ServiceManager.getService(IUserService.class);
      int totalNum = userService.countSalesManByShopIdAndStatus(shopId, null);
      int totalNumDelete = userService.countSalesManByShopIdAndStatus(shopId, SalesManStatus.DELETED);
      model.addAttribute("totalNum", totalNum - totalNumDelete);

      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.ASSISTANT_DEPARTMENT);
      model.addAttribute("totalShopAchievementConfig", totalShopAchievementConfig);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return "/stat/assistantStat/allSalesManConfig";
  }

  private String getValue(List<BizStatDTO> bizStatDTOList, String bizStatType) {
    for (BizStatDTO bizStatDTO : bizStatDTOList) {
      if (bizStatType.equals(bizStatDTO.getStatType())) {
        return decimalFormat.format(bizStatDTO.getStatSum());
      }
    }
    return "0";
  }

  //页面查询统计数据，显示饼图
  @RequestMapping(params = "method=getAssistant")
  public void getAssistant(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DataSourceRequest dsRequest = null;
    try {
      dsRequest = new DataSourceRequest(request);

      if (request.getSession() == null || request.getSession().getAttribute("shopId") == null) {
        ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.ACCESS_DENIED, "请登录！");
        if (dsRequest == null) {
          dsRequest = DataSourceRequest.getDefaultDataSourceRequest(request);
        }
        DataSourceHelper.setServletErrorResponse(status, dsRequest, response);
        return;
      }
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      DataTable data = null;
      if (request.getParameter("startMonth") != null) {
        String startMonth = request.getParameter("startMonth");
        String endMonth = startMonth;
        int year = 2012;
        if (request.getParameter("endMonth") != null) {
          endMonth = request.getParameter("endMonth");
        } else {
          endMonth = startMonth;
        }
        if (request.getParameter("year") != null) {
          year = Integer.valueOf(request.getParameter("year"));
        }

        request.setAttribute("queryYear", year);
        request.setAttribute("startMonth", Integer.valueOf(startMonth));
        request.setAttribute("endMonth", Integer.valueOf(endMonth));
        data = getAssistantEachMonth(shopId, year, Integer.valueOf(startMonth), Integer.valueOf(endMonth));
      } else {
        data = null;
      }

      // Apply the query to the data table.
      DataTable newData = DataSourceHelper.applyQuery(dsRequest.getQuery(), data, dsRequest.getUserLocale());

      // Set the response.
      DataSourceHelper.setServletResponse(newData, dsRequest, response);
    } catch (RuntimeException rte) {
      LOG.error("/bizstat.do");
      LOG.error("method=getAssistant");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("A runtime exception has occured", rte);
      ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.INTERNAL_ERROR, rte.getMessage());
      if (dsRequest == null) {
        dsRequest = DataSourceRequest.getDefaultDataSourceRequest(request);
      }
      DataSourceHelper.setServletErrorResponse(status, dsRequest, response);
    } catch (DataSourceException e) {
      LOG.error("/bizstat.do");
      LOG.error("method=getAssistant");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(),e);
      if (dsRequest != null) {
        DataSourceHelper.setServletErrorResponse(e, dsRequest, response);
      } else {
        DataSourceHelper.setServletErrorResponse(e, request, response);
      }
    }

  }

  //ajax 查询统计数据
  @RequestMapping(params = "method=queryStatData")
  public void queryStatData(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Date date;
    try {
      date = simpleDateFormat.parse(request.getParameter("date"));
    } catch (Exception e) {
      LOG.debug("/bizstat.do");
      LOG.debug("method=queryStatData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      date = new Date();
    }
    calendar.setTime(date);
    calendar.setMinimalDaysInFirstWeek(1);
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;

    //
    DataSourceRequest dsRequest = null;

    try {
      dsRequest = new DataSourceRequest(request);

      if (request.getSession() == null || request.getSession().getAttribute("shopId") == null) {
        ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.ACCESS_DENIED, "请登录！");
        if (dsRequest == null) {
          dsRequest = DataSourceRequest.getDefaultDataSourceRequest(request);
        }
        DataSourceHelper.setServletErrorResponse(status, dsRequest, response);
        return;
      }
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());

      String type = request.getParameter("type");
      DataTable data;

      if ("month".equals(type)) {
        data = generateEachMonthInAYearDataTable(shopId, year);
      } else if ("week".equals(type)) {
        data = generateEachWeekInAQuarterDataTable(shopId, year, (month - 1) / 3 + 1);
      } else if ("day".equals(type)) {
        data = generateEachDayInAMonthDataTable(shopId, year, month);
      } else {
        ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.NOT_SUPPORTED, "不支持！");
        if (dsRequest == null) {
          dsRequest = DataSourceRequest.getDefaultDataSourceRequest(request);
        }
        DataSourceHelper.setServletErrorResponse(status, dsRequest, response);
        return;
      }


      // Apply the query to the data table.
      DataTable newData = DataSourceHelper.applyQuery(dsRequest.getQuery(), data, dsRequest.getUserLocale());

      // Set the response.
      DataSourceHelper.setServletResponse(newData, dsRequest, response);
    } catch (RuntimeException rte) {
      LOG.debug("/bizstat.do");
      LOG.debug("method=queryStatData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("A runtime exception has occured", rte);
      ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.INTERNAL_ERROR, rte.getMessage());
      if (dsRequest == null) {
        dsRequest = DataSourceRequest.getDefaultDataSourceRequest(request);
      }
      DataSourceHelper.setServletErrorResponse(status, dsRequest, response);
    } catch (DataSourceException e) {
      LOG.debug("/bizstat.do");
      LOG.debug("method=queryStatData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      if (dsRequest != null) {
        DataSourceHelper.setServletErrorResponse(e, dsRequest, response);
      } else {
        DataSourceHelper.setServletErrorResponse(e, request, response);
      }
    }
  }

  public DataTable getAssistantEachMonth(long shopId, int year, int startMonth, int endMonth) {
    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

    List<AssistantStatDTO> assistantStatDTOs = bizStatService.getAssistantMonth(shopId, year, startMonth, endMonth, 0, 10000);
    List<AssistantStatDTO> assistantStatDTOList = new ArrayList<AssistantStatDTO>();
    if (assistantStatDTOs != null && assistantStatDTOs.size() > 0) {
      if (startMonth != endMonth) {
        AssistantStatDTO assistantStatDTOSum = new AssistantStatDTO();
        Map<String, AssistantStatDTO> map = new HashMap<String, AssistantStatDTO>();
        for (AssistantStatDTO assistantStatDTO : assistantStatDTOs) {
          if (map.containsKey(assistantStatDTO.getAssistant())) {
            AssistantStatDTO assistantStatDTOTmp = map.get(assistantStatDTO.getAssistant());
            map.remove(assistantStatDTO.getAssistant());

            double salesSum = assistantStatDTOTmp.getSales() + assistantStatDTO.getSales();
            assistantStatDTOTmp.setSales(salesSum);

            double serviceSum = assistantStatDTOTmp.getService() + assistantStatDTO.getService();
            assistantStatDTOTmp.setService(serviceSum);

            double washSum = assistantStatDTOTmp.getWash() + assistantStatDTO.getWash();
            assistantStatDTOTmp.setWash(washSum);

            double total = salesSum + serviceSum + washSum;
            assistantStatDTOTmp.setStatSum(total);

            map.put(assistantStatDTO.getAssistant(), assistantStatDTOTmp);
          } else {
            map.put(assistantStatDTO.getAssistant(), assistantStatDTO);
          }
        }

        Set<String> key = map.keySet();
        for (Iterator iterator = key.iterator(); iterator.hasNext(); ) {
          String s = (String) iterator.next();
          assistantStatDTOList.add(map.get(s));
        }
        Collections.sort(assistantStatDTOList);
      } else {
        assistantStatDTOList = assistantStatDTOs;
      }
    }





    DataTable data = new DataTable();
    ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
    cd.add(new ColumnDescription("month", ValueType.TEXT, "月份"));
    cd.add(new ColumnDescription("income", ValueType.NUMBER, "营收"));
    data.addColumns(cd);
    int size = 0;
    AssistantStatDTO assistantStatDTO = null;
    AssistantStatDTO assistantStatOther = null;
    List<AssistantStatDTO> assistantStatList = new ArrayList<AssistantStatDTO>();

    if (assistantStatDTOList.size() > 10) {
      size = 10;
      boolean isContain = false;
      for(int j = 0;j< size;j++){
        assistantStatDTO = assistantStatDTOList.get(j);
        assistantStatList.add(assistantStatDTO);
      }
      if(isContain==false){
        AssistantStatDTO assistantStatDTO1 = new AssistantStatDTO();
        assistantStatDTO1.setAssistant("其他");
        for(int k = size;k < assistantStatDTOList.size();k++){
          assistantStatDTO = assistantStatDTOList.get(k);
          assistantStatDTO1.setSales(assistantStatDTO1.getSales() + assistantStatDTO.getSales());
          assistantStatDTO1.setWash(assistantStatDTO1.getWash() + assistantStatDTO.getWash());
          assistantStatDTO1.setService(assistantStatDTO1.getService() + assistantStatDTO.getService());
          assistantStatDTO1.setStatSum(assistantStatDTO1.getStatSum() + assistantStatDTO.getStatSum());
        }
        assistantStatList.add(assistantStatDTO1);
      }
    } else {
      //size = assistantStatDTOList.size();
      assistantStatList =assistantStatDTOList;
    }
    for (int i = 0; i < assistantStatList.size(); i++) {
      assistantStatDTO = assistantStatList.get(i);
      try {
        data.addRowFromValues(assistantStatDTO.getAssistant(), assistantStatDTO.getStatSum());
      } catch (Exception e) {
        LOG.info("/bizstat.do");
        LOG.info("menthod:DataTable getAssistantEachMonth");
        LOG.info("shopId:" + shopId + ",year:" + year);
      }

    }
    return data;
  }


  private DataTable generateEachMonthInAYearDataTable(long shopId, int year) {
    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

    List<BizStatDTO> bizStatDTOList = bizStatService.getShopEachMonthInAYearStatByTypeAndYear(shopId, BizStatType.INCOME.toString(), year);

    // Create a data table,
    DataTable data = new DataTable();
    ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
    cd.add(new ColumnDescription("month", ValueType.TEXT, "月份"));
    cd.add(new ColumnDescription("income", ValueType.NUMBER, "营收"));

    data.addColumns(cd);

    // Fill the data table.
    try {
      for (BizStatDTO bizStatDTO : bizStatDTOList) {
        data.addRowFromValues(String.valueOf(bizStatDTO.getStatMonth()), bizStatDTO.getStatSum());
      }
    } catch (TypeMismatchException e) {
      LOG.debug("/bizstat.do");
      LOG.debug("shopId:" + shopId + ",year:" + year);
    }
    return data;

  }

  private DataTable generateEachWeekInAQuarterDataTable(long shopId, int year, int quarter) {
    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

    List<BizStatDTO> bizStatDTOList = bizStatService.getShopEachWeekInAYearStatByTypeAndYear(shopId, BizStatType.INCOME.toString(), year);

    // Create a data table,
    DataTable data = new DataTable();
    ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
    cd.add(new ColumnDescription("week", ValueType.TEXT, "周数"));
    cd.add(new ColumnDescription("income", ValueType.NUMBER, "营收"));

    data.addColumns(cd);

    //--该描述为注释掉截止周计算的描述：下面开始计算该季度所经过的当年的周数
    calendar.clear();

    calendar.set(year, (quarter - 1) * 3, 1);//该季度第一天
    int startWeek = calendar.get(Calendar.WEEK_OF_YEAR);//获取起始周数
    int weekday = calendar.get(Calendar.DAY_OF_WEEK);//判断该季度第一天是星期几
    if (weekday != Calendar.SUNDAY) {//如果不是星期天
      calendar.add(Calendar.DATE, 2 - weekday);//退回相应天数到当周星期一
    } else {
      calendar.add(Calendar.DATE, -6);//如果是星期天退回6天到当周星期一
    }
    long startTime = calendar.getTimeInMillis();//该季度第一天所在周的星期一为起始日期

    calendar.set(year, (quarter - 1) * 3, 1);//重新回到该季度第一天
    calendar.add(Calendar.MONTH, 3);//下季度第一天
    long endTime = calendar.getTimeInMillis();//下季度第一天的值，用于判断


    /*--之前用于计算截止周数的
    int endWeek = startWeek;//初始化截止周数
    do {
      calendar.add(Calendar.DATE, 7);//加7天表示下周
      endWeek++;//截止周数加1
    }
    while (calendar.getTimeInMillis() < endTime);//如果增加1后的周数的星期一还小于下季度第一天继续增加截止周数
    endWeek--;//倒回去1周即表示该季度的最后一周了
    */

    // Fill the data table.
    try {
      for (BizStatDTO bizStatDTO : bizStatDTOList) {
        int week = Integer.parseInt(String.valueOf(bizStatDTO.getStatWeek()));
        if (week >= startWeek) {
          calendar.setTimeInMillis(startTime);
          calendar.add(Calendar.DATE, (week - startWeek) * 7);
          if (calendar.getTimeInMillis() >= endTime) break;
          data.addRowFromValues(String.valueOf(calendar.get(Calendar.MONTH) + 1) + "." + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), bizStatDTO.getStatSum());

        }
      }
    } catch (TypeMismatchException e) {
      LOG.debug("/bizstat.do");
      LOG.debug("shopId:" + shopId + ",year:" + year + ",quarter:" + quarter);
    }
    return data;

  }

  private DataTable generateEachDayInAMonthDataTable(long shopId, int year, int month) {
    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

    List<BizStatDTO> bizStatDTOList = bizStatService.getShopEachDayInAMonthStatByTypeAndYearAndMonth(shopId, BizStatType.INCOME.toString(), year, month);

    // Create a data table,
    DataTable data = new DataTable();
    ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
    cd.add(new ColumnDescription("day", ValueType.TEXT, "日期"));
    cd.add(new ColumnDescription("income", ValueType.NUMBER, "营收"));

    data.addColumns(cd);

    // Fill the data table.
    try {
      for (BizStatDTO bizStatDTO : bizStatDTOList) {
        data.addRowFromValues(String.valueOf(bizStatDTO.getStatDay()), bizStatDTO.getStatSum());
      }
    } catch (TypeMismatchException e) {
      LOG.debug("/bizstat.do");
      LOG.debug("shopId:" + shopId + ",year:" + year + ",month:" + month);
    }
    return data;

  }

  @RequestMapping(params = "method=customerResponse")
  public void customerResponse(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Integer startPageNo, Integer maxRows, Integer year, Integer startMonth, Integer endMonth) throws BcgogoException {

    IUserService userService = ServiceManager.getService(IUserService.class);
    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    List<AssistantStatDTO> assistantStatDTOs = bizStatService.getAssistantMonth(shopId, year, startMonth, endMonth, 0, 1000);
    List<AssistantStatDTO> assistantStatDTOList = new ArrayList<AssistantStatDTO>();
    if (assistantStatDTOs != null && assistantStatDTOs.size() > 0) {
      //如果是历史查询，开始月份不等于结束月份，则对查询结果assistantStatDTOs进行归并，把店员一样的统计在一起
      if (startMonth != endMonth) {
        AssistantStatDTO assistantStatDTOSum = new AssistantStatDTO();
        Map<String, AssistantStatDTO> map = new HashMap<String, AssistantStatDTO>();
        for (AssistantStatDTO assistantStatDTO : assistantStatDTOs) {
          if (map.containsKey(assistantStatDTO.getAssistant())) {
            AssistantStatDTO assistantStatDTOTmp = map.get(assistantStatDTO.getAssistant());
            map.remove(assistantStatDTO.getAssistant());

            double salesSum = assistantStatDTOTmp.getSales() + assistantStatDTO.getSales();
            BigDecimal bigDecimal = new BigDecimal(salesSum);
            salesSum = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            assistantStatDTOTmp.setSales(salesSum);

            double serviceSum = assistantStatDTOTmp.getService() + assistantStatDTO.getService();
            bigDecimal = new BigDecimal(serviceSum);
            serviceSum = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            assistantStatDTOTmp.setService(serviceSum);

            double washSum = assistantStatDTOTmp.getWash() + assistantStatDTO.getWash();
            bigDecimal = new BigDecimal(washSum);
            washSum = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            assistantStatDTOTmp.setWash(washSum);

            double total = salesSum + serviceSum + washSum;
            bigDecimal = new BigDecimal(total);
            total = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            assistantStatDTOTmp.setStatSum(total);

            map.put(assistantStatDTO.getAssistant(), assistantStatDTOTmp);
          } else {
            map.put(assistantStatDTO.getAssistant(), assistantStatDTO);
          }
        }

        Set<String> key = map.keySet();
        for (Iterator iterator = key.iterator(); iterator.hasNext(); ) {
          String s = (String) iterator.next();
          assistantStatDTOList.add(map.get(s));
        }
        Collections.sort(assistantStatDTOList);
      } else {
        assistantStatDTOList = assistantStatDTOs;
      }
    }
    int assistantStatSize = assistantStatDTOList.size();    //店员总数
    Pager pager = new Pager(assistantStatSize, startPageNo, pageSize);
    List<AssistantStatDTO> dtoList = new ArrayList<AssistantStatDTO>();
    int size = pager.getRowStart() + pageSize;
    if (size > assistantStatDTOList.size()) {
      size = assistantStatDTOList.size();
    }
    for (int index = pager.getRowStart(); index < size; index++) {
      dtoList.add(assistantStatDTOList.get(index));
    }

    String jsonStr = "";
    if (assistantStatDTOList.size() <= 0) {
      return;
    }
    request.setAttribute("assistantSize", assistantStatDTOList.size());
    jsonStr = JsonUtil.listToJson(dtoList);
    modelMap.addAttribute("jsonStr", jsonStr);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);

    if(pager.getCurrentPage()==1){
      jsonStr = jsonStr + ",{\"isTheFirstPage1\":\"true\"}";
    }else{
      jsonStr = jsonStr + ",{\"isTheFirstPage1\":\"false\"}";
    }

    if (!pager.hasNextPage()) {
      jsonStr = jsonStr + ",{\"isTheLastPage1\":\"true\"}]";
    } else {
      jsonStr = jsonStr + ",{\"isTheLastPage1\":\"false\"}]";
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.info("/unitlink.do");
      LOG.info("method=customerResponse");
      LOG.info("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.info("startMonth:" + startMonth + ",endMonth:" + endMonth);
      LOG.info(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getAssistantDetail")
  public String getAssistantDetail(HttpServletRequest request) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String assistantName = String.valueOf(request.getParameter("assistantName"));
    int year = 2012;
    int startMonth = 1;
    int endMonth = 1;
    double carRepair = 0;
    double sales = 0;
    double washing = 0;
    double total = 0;
    double memberIncome = 0;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    if (null == assistantName || "".equals(assistantName)) {
      return "/";
    }
    if (request.getParameter("year") != null) {
      year = Integer.valueOf(request.getParameter("year"));
    } else {
      return "/";
    }
    if (request.getParameter("startMonth") != null) {
      startMonth = Integer.valueOf(request.getParameter("startMonth"));
    } else {
      return "/";
    }
    if (request.getParameter("endMonth") != null) {
      endMonth = Integer.valueOf(request.getParameter("endMonth"));
    } else {
      return "/";
    }

    if (request.getParameter("memberIncome") != null) {
      memberIncome = Double.valueOf(request.getParameter("memberIncome"));
    }

    if (request.getParameter("carRepair") != null) {
      carRepair = Double.valueOf(request.getParameter("carRepair"));
    } else {
      carRepair = 0;
    }
    if (request.getParameter("sales") != null) {
      sales = Double.valueOf(request.getParameter("sales"));
    } else {
      sales = 0;
    }
    if (request.getParameter("washing") != null) {
      washing = Double.valueOf(request.getParameter("washing"));
    } else {
      washing = 0;
    }
    if (request.getParameter("total") != null) {
      total = Double.valueOf(request.getParameter("total"));
    } else {
      total = carRepair + sales + washing + memberIncome;
    }


    request.setAttribute("queryYear", year);
    request.setAttribute("startMonth", startMonth);
    request.setAttribute("endMonth", endMonth);

    Calendar calendar = Calendar.getInstance();
    calendar.set(year, startMonth - 1, 1, 0, 0, 0);
    long startTime = calendar.getTimeInMillis();
    calendar.set(year, endMonth, 1, 0, 0, 0);
    long endTime = calendar.getTimeInMillis();

    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    List<OrderIndexDTO> orderIndexDTOs = null;
    int totalNumber = 0;

    Pager pager = new Pager(NumberUtil.intValue(request.getParameter("pageNo"), 1));
    try {
      totalNumber = orderIndexService.getOrderIndexSizeByServiceWorker(shopId, assistantName, startTime, endTime);
      request.setAttribute("totalNumber", totalNumber);
      request.setAttribute("pageNo", pager.getCurrentPage());
      request.setAttribute("pageCount", 0);
      request.setAttribute("assistantName", assistantName);
      pager = new Pager(totalNumber, pager.getCurrentPage(), pageSize);
      orderIndexDTOs = orderIndexService.getOrderIndexByServiceWork(shopId, assistantName, startTime, endTime, (pager.getCurrentPage() - 1) * pager.getPageSize(), pager.getPageSize());
    } catch (Exception e) {
      LOG.error("/bizstat.do");
      LOG.error("method=getAssistantDetail");
      LOG.error(e.getMessage(), e);
    }

    //拿到该单据的最新数据
    if (orderIndexDTOs != null && orderIndexDTOs.size() > 0) {
      for (OrderIndexDTO orderIndexDTO : orderIndexDTOs) {
        if (orderIndexDTO == null || orderIndexDTO.getShopId() == null || orderIndexDTO.getOrderId() == null) {
          continue;
        }

        ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(orderIndexDTO.getShopId(),orderIndexDTO.getOrderId());
        if(receivableDTO == null){
          orderIndexDTO.setArrears(0D);
          continue;
        }
        orderIndexDTO.setArrears(receivableDTO.getDebt());
        orderIndexDTO.setPaymentTime(receivableDTO.getRemindTime());
      }
    }

    request.setAttribute("memberIncome", memberIncome);
    request.setAttribute("carRepair", carRepair);
    request.setAttribute("sales", sales);
    request.setAttribute("washing", washing);
    request.setAttribute("total", total);
    request.setAttribute("assistantName", assistantName);
    if (totalNumber > 0) {
      request.setAttribute("orderIndexDTOList", orderIndexDTOs);
      request.setAttribute("orderIndexListSize", String.valueOf(totalNumber));
      request.setAttribute("pageCount", pager.getTotalPage());
    } else {
      request.setAttribute("orderIndexListSize", String.valueOf(0));
    }
    return "/stat/assistantDetail";

  }

  @RequestMapping(params = "method=getHistoryAchievements")
  public String getHistoryAchievements(HttpServletRequest request) {
    String startDateStr = request.getParameter("startDate");
    String endDateStr = request.getParameter("endDate");
    try {
      long startDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, startDateStr);
      long endDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, endDateStr);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return "stat/assistantDetail";
  }

}
