package com.bcgogo.web;

import com.bcgogo.common.Pager;
import com.bcgogo.common.StringUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.PayableHistoryRecord;
import com.bcgogo.txn.model.RepealOrder;
import com.bcgogo.txn.service.IRunningStatService;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-8-31
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/statInit.do")
public class StatInitController {

  private static final Logger LOG = LoggerFactory.getLogger(StatInitController.class);

  public static final int PAGE_SIZE = 2000;//营业数据初始化 每次查询的最大条数
  public static final int BEGIN_DAY_EVERY_MONTH = 1;//默认每个月的第一天
  public static final int BUSINESS_STAT_TIME = 10000;//营业数据初始化 默认每天的最后10秒钟 10000为毫秒数
  public static final int FIRST_PAGE_NO = 1;//营业数据初始化 分页查询 默认的第一页
  public static final int COMPARE_RESULT = 1;//单据作废时间和统计时间比较结果 默认为1 即作废时间比统计时间大
  public static final int defaultYear = 2012;


  /**
   * function：通过输入开始日期 统计 开始日期 到当天的流水数据
   * 如果不输入日期 默认统计2012年 到 当天的数据 包含当天
   * 如果输入日期 统计输入日期到 2013年 之前的数据
   * 后台通过输入url自动对店面进行统计 暂时只统计2012年数据
   *
   * @param model
   * @param request
   * @return
   */
  @RequestMapping(params = "method=initStat")
  public String initStat(ModelMap model, HttpServletRequest request) {

    try {
      //1.判断是否是jackchen用户 如果不是 返回到登陆页面
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId != 0) {
        return "/";
      }

      //2.获得统计的开始日期
      Date beginDate = getInitDateFromRequest(request);

      Date endDate = new Date();

      //3.获得店铺List
      List<Shop> shopList = getShopList(request);

      LOG.info("系统开始初始化流水数据，开始时间为:" + DateUtil.dateLongToStr(System.currentTimeMillis()));

      this.initStatByDate(beginDate, endDate, shopList);
    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=initStat");
      LOG.error(e.getMessage(), e);
      LOG.error("流水数据初始化失败");
      return "/";
    }
    LOG.info("系统结束所有店铺初始化营业数据，结束时间为:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
    //返回到营业统计页面 代表初始化成功
    return "stat/businessStatistics";
  }


  public String initStatByDate(Date beginDate, Date endDate, List<Shop> shopList) {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    //.流水统计数据的特殊性  流水数据都是累加值 先设置统计的开始时间
    Calendar calendar = getFirstDayOfYear();
    long startTime = calendar.getTimeInMillis();//开始时间默认为2012.3.1
    calendar.clear();
    calendar.setTime(beginDate);
    int startMonth = calendar.get(Calendar.MONTH) + 1; //数据初始化的开始日期 月份
    int startDay = calendar.get(Calendar.DAY_OF_MONTH);//数据初始化的开始日期 日
    int startYear = calendar.get(Calendar.YEAR);//数据初始化的开始日期 年

    //如果输入的日期不是2012年 默认只统计2012年数据
    if (startYear != defaultYear) {
      LOG.error("/statInit.do");
      LOG.error("method=initRunningStat");
      LOG.error("流水数据初始化数据出错 日期年份只能输入2012," + startYear);
      return "/";
    }

    //设置统计的结束日期 营业数据都是累加值 结束日期只能为当前时间
    Calendar currentCalendar = Calendar.getInstance();
    currentCalendar.clear();
    currentCalendar.setTime(endDate);
    int currentMonth = currentCalendar.get(Calendar.MONTH) + 1; //当前月份
    int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH); //当前日


    //每个月统计的第一天
    int dayStartEveryMonth = BEGIN_DAY_EVERY_MONTH;
    if (CollectionUtils.isEmpty(shopList)) {
      LOG.error("/statInit.do");
      LOG.error("method=initRunningStat");
      LOG.error("店铺为空");
      return "/";
    }
    for (Shop shop : shopList) {
      StringBuffer stringBuffer = new StringBuffer();
      long shopId = shop.getId();
      //遍历 开始日期  到 当前时间 统计每日的营业数据
      LOG.info("系统开始初始化shop_id:" + shopId + "的流水数据");
      try {

        this.initPurchaseInventory(shopId);
        this.initPurchaseReturn(shopId);

        List<RunningStatDTO> runningStatDTOList = new ArrayList<RunningStatDTO>();
        for (int monthIndex = startMonth; monthIndex <= currentMonth; monthIndex++) {

          calendar.set(startYear, monthIndex - 1, 1, 0, 0, 0);

          //每个月统计的最后一天
          int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

          //如果统计到当前月份 统计的最后一天为当前日
          if (monthIndex == currentMonth) {
            lastDayOfCurrentMonth = currentDay;
          }

          //如果统计月份为开始月份 统计开始天数 为输入的开始日期
          if (monthIndex == startMonth) {
            dayStartEveryMonth = startDay;
          }

          for (int dayIndex = dayStartEveryMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {
            calendar.set(startYear, monthIndex - 1, dayIndex, 0, 0, 0);
            startTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DATE, 1);
            long endTime = calendar.getTimeInMillis();

            if(dayIndex == 23 && monthIndex == 10){
              System.out.println("");
          }
            RunningStatDTO returnDTO = this.initRunningStatByShopIdAndTime(shopId, startTime, endTime);
            returnDTO.setIncomeSum(returnDTO.getCashIncome() + returnDTO.getChequeIncome() + returnDTO.getUnionPayIncome());
            returnDTO.setExpenditureSum(returnDTO.getCashExpenditure() + returnDTO.getChequeExpenditure() + returnDTO.getUnionPayExpenditure());
            returnDTO.setRunningSum(returnDTO.getIncomeSum() - returnDTO.getExpenditureSum());
            runningStatDTOList.add(returnDTO);
        }
        }
        runningStatService.saveRunningStatDTOList(runningStatDTOList);
      } catch (Exception e) {
        LOG.error("/init.do");
        LOG.error("method=countBusinessStat");
        LOG.error(e.getMessage(), e);
        LOG.error(" 营业统计数据初始化出错 shop_id为" + shopId);
        LOG.error("系统继续初始化下一个店铺");
        continue;
      }

      LOG.info("系统结束初始化shop_id:" + shopId + "的流水数据，继续初始化下一个店铺");
      LOG.info("该店铺的错误信息:" + stringBuffer.toString());
    }


    //付定金初始化


    LOG.info("系统结束所有店铺初始化营业数据，结束时间为:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
    //返回到营业统计页面 代表初始化成功
    return "stat/businessStatistics";
  }

  public void  initPurchaseInventory(Long shopId) throws ParseException {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = runningStatService.getPayableHistoryRecordByPaymentType(null, shopId);
    if(CollectionUtils.isEmpty(payableHistoryRecordDTOList)){
      return;
    }

    for(PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
      Long purchaseInventoryId = payableHistoryRecordDTO.getPurchaseInventoryId();

      PurchaseInventoryDTO purchaseInventoryDTO = txnService.getPurchaseInventoryById(purchaseInventoryId, payableHistoryRecordDTO.getShopId());
      if (purchaseInventoryDTO == null) {
        continue;
      }
      OrderStatus orderStatus = purchaseInventoryDTO.getStatus();

      //入库单
      if (orderStatus != OrderStatus.PURCHASE_INVENTORY_REPEAL) {
        payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY_DEBT);
      }
    }
    runningStatService.saveOrUpdatePayRecordList(payableHistoryRecordDTOList);
  }

  public void initPurchaseReturn(Long shopId){
    try {
      IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      ISearchService searchService = ServiceManager.getService(ISearchService.class);
      List<PurchaseReturnDTO> purchaseReturnDTOList = runningStatService.getPurchaseReturn(shopId);
      if (CollectionUtils.isEmpty(purchaseReturnDTOList)) {
        return;
      }
      List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = new ArrayList<PayableHistoryRecordDTO>();

      for (PurchaseReturnDTO purchaseReturnDTO : purchaseReturnDTOList) {
        if (purchaseReturnDTO == null || purchaseReturnDTO.getId() == null) {
          continue;
        }

//        List<PayableHistoryRecordDTO> dtoList = supplierPayableService.getPayableHistoryRecord(purchaseReturnDTO.getShopId(),purchaseReturnDTO.getSupplierId(),purchaseReturnDTO.getId(),null);
//        if(CollectionUtils.isNotEmpty(dtoList)){
//          continue;
//        }

        PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord();

        List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());

        if (CollectionUtils.isNotEmpty(orderIndexDTOList)) {
          payableHistoryRecord.setMaterialName(orderIndexDTOList.get(0).getOrderContent());

        }
        payableHistoryRecord.setShopId(purchaseReturnDTO.getShopId());
        payableHistoryRecord.setDeduction(0D);
        payableHistoryRecord.setCreditAmount(0D);
        payableHistoryRecord.setPayTime(purchaseReturnDTO.getCreationDate());
        payableHistoryRecord.setBankCardAmount(0D);
        payableHistoryRecord.setCheckAmount(0D);
        payableHistoryRecord.setDayType(DayType.OTHER_DAY);
        payableHistoryRecord.setPurchaseInventoryId(purchaseReturnDTO.getId());

        SupplierReturnPayableDTO supplierReturnPayableDTO = supplierPayableService.getSupplierReturnPayableByPurchaseReturnId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());
        if (supplierReturnPayableDTO == null) {
          //退入现金
          payableHistoryRecord.setCash(0 - purchaseReturnDTO.getTotal());
          payableHistoryRecord.setDepositAmount(0d);
          payableHistoryRecord.setStrikeAmount(0d);
          payableHistoryRecord.setActuallyPaid(0 - purchaseReturnDTO.getTotal());
          payableHistoryRecord.setPaymentType(PaymentTypes.INVENTORY_RETURN);
        } else {
          payableHistoryRecord.setCash(0-(null==supplierReturnPayableDTO.getCash()?0d:supplierReturnPayableDTO.getCash()));
          payableHistoryRecord.setDepositAmount(0 - (null==supplierReturnPayableDTO.getDeposit()?0d:supplierReturnPayableDTO.getDeposit()));
          payableHistoryRecord.setStrikeAmount(0-(null==supplierReturnPayableDTO.getStrikeAmount()?0d:supplierReturnPayableDTO.getStrikeAmount()));
          payableHistoryRecord.setActuallyPaid(0-(null==supplierReturnPayableDTO.getTotal()?0d:supplierReturnPayableDTO.getTotal()));
          payableHistoryRecord.setPaymentType(PaymentTypes.INVENTORY_RETURN);

        }
        payableHistoryRecord.setAmount(0 - purchaseReturnDTO.getTotal());
        payableHistoryRecord.setSupplierId(purchaseReturnDTO.getSupplierId());
        payableHistoryRecord.setPurchaseReturnId(purchaseReturnDTO.getId());
        payableHistoryRecordDTOList.add(payableHistoryRecord.toDTO());
      }

      runningStatService.deletePayHistoryRecord(shopId);
      runningStatService.saveOrUpdatePayRecordList(payableHistoryRecordDTOList);
    }catch (Exception e){
      LOG.error("初始化入库退货单失败");
      LOG.error(e.getMessage(),e);
    }

  }


  public RunningStatDTO initRunningStatByShopIdAndTime(long shopId,long startTime, long endTime) {
    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(shopId);
    runningStatDTO.setStatYear(DateUtil.getYearByVestDate(endTime - BUSINESS_STAT_TIME));
    runningStatDTO.setStatMonth(DateUtil.getMonthByVestDate(endTime - BUSINESS_STAT_TIME));
    runningStatDTO.setStatDay(DateUtil.getDayByVestDate(endTime - BUSINESS_STAT_TIME));
    runningStatDTO.setStatDate(endTime - BUSINESS_STAT_TIME);
    try {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);

      List<ReceptionRecordDTO> receptionRecordDTOList = supplierPayableService.getReceptionRecordByReceptionDate(shopId, startTime, endTime);
      if (CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
        for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {

          runningStatDTO.setCashIncome(runningStatDTO.getCashIncome() + NumberUtil.doubleVal(receptionRecordDTO.getCash()));
          runningStatDTO.setChequeIncome(runningStatDTO.getChequeIncome() + NumberUtil.doubleVal(receptionRecordDTO.getCheque()));
          runningStatDTO.setUnionPayIncome(runningStatDTO.getUnionPayIncome() + NumberUtil.doubleVal(receptionRecordDTO.getBankCard()));
          runningStatDTO.setMemberPayIncome(runningStatDTO.getMemberPayIncome() + NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay()));

          if (OrderTypes.DEBT == receptionRecordDTO.getOrderTypeEnum()) {
            runningStatDTO.setDebtWithdrawalIncome(runningStatDTO.getDebtWithdrawalIncome() + NumberUtil.doubleVal(receptionRecordDTO.getCash())
                + NumberUtil.doubleVal(receptionRecordDTO.getCheque()) + NumberUtil.doubleVal(receptionRecordDTO.getBankCard()) + NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay()));
            runningStatDTO.setCustomerDebtDiscount(runningStatDTO.getCustomerDebtDiscount() + NumberUtil.doubleVal(receptionRecordDTO.getDiscount()));
          }else {

            if (OrderStatus.REPAIR_REPEAL == receptionRecordDTO.getOrderStatusEnum()) {
              RunningStatDTO returnDTO = this.updateOrderRepealReception(receptionRecordDTO.getShopId(), receptionRecordDTO.getOrderId());
              runningStatDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() - returnDTO.getDebtNewIncome());
              runningStatDTO.setCustomerDebtDiscount(runningStatDTO.getCustomerDebtDiscount() - returnDTO.getCustomerDebtDiscount());
              runningStatDTO.setDebtWithdrawalIncome(runningStatDTO.getDebtWithdrawalIncome() - returnDTO.getDebtWithdrawalIncome());

            } else if (OrderStatus.SALE_REPEAL == receptionRecordDTO.getOrderStatusEnum()) {
              RunningStatDTO returnDTO = this.updateOrderRepealReception(receptionRecordDTO.getShopId(), receptionRecordDTO.getOrderId());
              runningStatDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() - returnDTO.getDebtNewIncome());
              runningStatDTO.setCustomerDebtDiscount(runningStatDTO.getCustomerDebtDiscount() - returnDTO.getCustomerDebtDiscount());
              runningStatDTO.setDebtWithdrawalIncome(runningStatDTO.getDebtWithdrawalIncome() - returnDTO.getDebtWithdrawalIncome());
            } else {
              runningStatDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() + receptionRecordDTO.getRemainDebt());
            }
          }
        }
      }

      List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = supplierPayableService.getPayHistoryRecordByPayTime(shopId, startTime, endTime);
      if (CollectionUtils.isEmpty(payableHistoryRecordDTOList)) {
        return runningStatDTO;
      }

      for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
        runningStatDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() + NumberUtil.doubleVal(payableHistoryRecordDTO.getCash()));
        runningStatDTO.setChequeExpenditure(runningStatDTO.getChequeExpenditure() + NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount()));
        runningStatDTO.setUnionPayExpenditure(runningStatDTO.getUnionPayExpenditure() + NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount()));
        runningStatDTO.setDepositPayExpenditure(runningStatDTO.getDepositPayExpenditure() + NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount()));

        if (payableHistoryRecordDTO.getPaymentType() == PaymentTypes.INVENTORY) {
          runningStatDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() + NumberUtil.doubleVal(payableHistoryRecordDTO.getCreditAmount()));
        }
        if (payableHistoryRecordDTO.getPaymentType() == PaymentTypes.INVENTORY_DEBT) {
          runningStatDTO.setDebtWithdrawalExpenditure(runningStatDTO.getDebtWithdrawalExpenditure() + NumberUtil.doubleVal(payableHistoryRecordDTO.getCash())
              + NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount()) + NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount()) + NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount()));
          runningStatDTO.setSupplierDebtDiscount(runningStatDTO.getSupplierDebtDiscount() + NumberUtil.doubleVal(payableHistoryRecordDTO.getDeduction()));
        }
        if (payableHistoryRecordDTO.getPaymentType() == PaymentTypes.INVENTORY_REPEAL) {

          List<PayableHistoryRecord> payableHistoryRecordList = supplierPayableService.getPayHistoryRecordListByIds(shopId, payableHistoryRecordDTO.getSupplierId(), payableHistoryRecordDTO.getPurchaseInventoryId());

          double debtNewExpenditure = 0.0;
          double debtWithdrawalExpenditure = 0.0;
          double supplierDebtDiscount = 0.0;
          if (CollectionUtils.isNotEmpty(payableHistoryRecordList)) {

            for (int index = 0; index < payableHistoryRecordList.size(); index++) {
              //拿到单据结算时欠款
              PayableHistoryRecord payableHistoryRecord = payableHistoryRecordList.get(index);

              if (PaymentTypes.INVENTORY == payableHistoryRecord.getPaymentType()) {
                debtNewExpenditure = NumberUtil.doubleVal(payableHistoryRecord.getCreditAmount());
              }
              //欠款结算单
              if (PaymentTypes.INVENTORY_DEBT == payableHistoryRecord.getPaymentType()) {
                debtWithdrawalExpenditure += NumberUtil.doubleVal(payableHistoryRecord.getActuallyPaid());
                supplierDebtDiscount += NumberUtil.doubleVal(payableHistoryRecord.getDeduction());
              }
            }
          }
          runningStatDTO.setDebtWithdrawalExpenditure(runningStatDTO.getDebtWithdrawalExpenditure() - debtWithdrawalExpenditure);
          runningStatDTO.setSupplierDebtDiscount(runningStatDTO.getSupplierDebtDiscount() - supplierDebtDiscount);
          runningStatDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() - debtNewExpenditure);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return runningStatDTO;
  }


  public String initOldWashOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);
    StringBuffer errorMessage = new StringBuffer();

    //营业额:洗车 先获取条数 然后分页获取列表
    int totalNum = itemIndexService.countWashItemIndexByShopId(shopId, startTime, endTime);
    if (totalNum <= 0) {
      return errorMessage.toString();
    }

    Pager pager = new Pager(totalNum, FIRST_PAGE_NO, PAGE_SIZE);

    for (int index = 1; index <= pager.getTotalPage(); index++) {

      pager = new Pager(totalNum, index, PAGE_SIZE);

      List<ItemIndexDTO> itemIndexDTOs = itemIndexService.getWashItemIndexListByPager(shopId, startTime, endTime, pager);
      if (CollectionUtils.isEmpty(itemIndexDTOs)) {
        continue;
      }
      for (ItemIndexDTO itemIndexDTO : itemIndexDTOs) {
        if (itemIndexDTO == null || itemIndexDTO.getOrderId() == null) {
          continue;
        }

        if (OrderTypes.WASH_MEMBER == itemIndexDTO.getOrderType()) {
          continue;
        }

        long orderId = itemIndexDTO.getOrderId();
        WashOrderDTO washOrderDTO = txnService.getWashOrder(orderId);
        if (washOrderDTO == null) {
          errorMessage.append("shopId:" + shopId + ",orderId:" + orderId + "在washOrder表中无记录");
        } else {
          if (OrderTypes.WASH_MEMBER == washOrderDTO.getOrderType()) {
            continue;
          }
        }

        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null, orderId);
        if (receivableDTO == null) {
          errorMessage.append("shopId:" + shopId + ",orderId:" + orderId + "在receivable表中无记录");
          continue;
        }
        errorMessage.append(this.saveReceptionRecord(receivableDTO, false, OrderTypes.WASH, OrderStatus.WASH_SETTLED, receptionRecordDTOList, runningStatDTO));
      }
    }
    return errorMessage.toString();
  }


  public String initSalesOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    StringBuffer errorMessage = new StringBuffer();

    //营业额:销售
    int totalNum = txnService.countSalesOrderByVestDate(shopId, startTime, endTime);
    if (totalNum <= 0) {
      return errorMessage.toString();
    }

    Pager pager = new Pager(totalNum, FIRST_PAGE_NO, PAGE_SIZE);
    for (int index = 1; index <= pager.getTotalPage(); index++) {

      pager = new Pager(totalNum, index, PAGE_SIZE);

      List<SalesOrderDTO> salesOrderDTOList = txnService.getSalesOrderListByPager(shopId, startTime, endTime, pager);

      if (CollectionUtils.isEmpty(salesOrderDTOList)) {
        continue;
      }

      for (SalesOrderDTO salesOrderDTO : salesOrderDTOList) {
        if (salesOrderDTO == null || salesOrderDTO.getStatus() == null) {
          continue;
        }


        long orderId = salesOrderDTO.getId();
        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null, orderId);
        if (receivableDTO == null) {
          errorMessage.append("shopId:" + shopId + ",orderId:" + orderId + "在receivable表中无记录");
        } else {
          errorMessage.append(this.saveReceptionRecord(receivableDTO, false, OrderTypes.SALE, OrderStatus.SALE_DONE, receptionRecordDTOList, runningStatDTO));
        }
      }
    }

    return errorMessage.toString();
  }


  public String initRepairOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    StringBuffer errorMessage = new StringBuffer();

    int totalNum = txnService.countRepairOrderByVestDate(shopId, startTime, endTime);
    if (totalNum <= 0) {
      return errorMessage.toString();
    }

    Pager pager = new Pager(totalNum, FIRST_PAGE_NO, PAGE_SIZE);
    //分次读取数据
    for (int index = FIRST_PAGE_NO; index <= pager.getTotalPage(); index++) {

      pager = new Pager(totalNum, index, PAGE_SIZE);

      List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderListByPager(shopId, startTime, endTime, pager);

      if (CollectionUtils.isEmpty(repairOrderDTOList)) {
        continue;
      }

      for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
        if (repairOrderDTO == null || repairOrderDTO.getStatus() == null) {
          continue;
        }

        OrderStatus orderStatus = repairOrderDTO.getStatus();
        if (orderStatus == OrderStatus.REPAIR_DISPATCH || orderStatus == OrderStatus.REPAIR_DONE) {
          continue;
        }

        if (NumberUtil.longValue(repairOrderDTO.getVestDate()) <= 0) {
          continue;
        }

        //实收和欠款从receivable表拿
        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(repairOrderDTO.getShopId(),
            OrderTypes.REPAIR, repairOrderDTO.getId());
        if (receivableDTO != null) {

          errorMessage.append(this.saveReceptionRecord(receivableDTO, false, OrderTypes.REPAIR, OrderStatus.REPAIR_SETTLED, receptionRecordDTOList, runningStatDTO));

        } else {
          errorMessage.append("shopId:" + shopId + ",orderId:" + repairOrderDTO.getId() + "在receivable表中无记录");
        }
      }
    }

    return errorMessage.toString();
  }


  public String saveReceptionRecord(ReceivableDTO receivableDTO, boolean isRepeal, OrderTypes orderTypes, OrderStatus orderStatus,
                                    List<ReceptionRecordDTO> receptionRecordDTOList, RunningStatDTO runningStatDTO) {
    StringBuffer errorMessage = new StringBuffer();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ReceptionRecordDTO[] receptionRecordDTOs = receivableDTO.getRecordDTOs();
    ReceptionRecordDTO receptionRecordDTO = null;
    if (!isRepeal) {
      receptionRecordDTO = new ReceptionRecordDTO();
      if (!ArrayUtils.isEmpty(receptionRecordDTOs)) {
        receptionRecordDTO = receptionRecordDTOs[0];
      }
      receptionRecordDTO.setReceivableId(receivableDTO.getId());
      receptionRecordDTO.setAmount(receivableDTO.getSettledAmount());
      receptionRecordDTO.setReceiveTime(0L);
      receptionRecordDTO.setMemberBalancePay(NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()));
      receptionRecordDTO.setAccumulatePointsPay(0d);
      receptionRecordDTO.setAccumulatePoints(0);
      receptionRecordDTO.setCash(receivableDTO.getCash());
      receptionRecordDTO.setBankCard(NumberUtil.doubleVal(receivableDTO.getBankCard()));
      receptionRecordDTO.setCheque(NumberUtil.doubleVal(receivableDTO.getCheque()));
      receptionRecordDTO.setMemberId(NumberUtil.longValue(receivableDTO.getMemberId()) > 0 ? receivableDTO.getMemberId() : null);
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setOriginDebt(0d);
      receptionRecordDTO.setDiscount(receivableDTO.getDiscount());
      receptionRecordDTO.setRemainDebt(receivableDTO.getDebt());
      receptionRecordDTO.setShopId(receivableDTO.getShopId());
      receptionRecordDTO.setOrderId(receivableDTO.getOrderId());
      receptionRecordDTO.setReceptionDate(receivableDTO.getCreationDate());
      receptionRecordDTO.setOrderTotal(receivableDTO.getTotal());
      receptionRecordDTO.setOrderTypeEnum(orderTypes);
      receptionRecordDTO.setOrderStatusEnum(orderStatus);
      receptionRecordDTOList.add(receptionRecordDTO);

      runningStatDTO.setCashIncome(runningStatDTO.getCashIncome() + receivableDTO.getCash());
      runningStatDTO.setUnionPayIncome(runningStatDTO.getUnionPayIncome() + receivableDTO.getBankCard());
      runningStatDTO.setChequeIncome(runningStatDTO.getChequeIncome() + receivableDTO.getCheque());
      runningStatDTO.setDebtNewIncome(receivableDTO.getDebt() + runningStatDTO.getDebtNewIncome());
      runningStatDTO.setMemberPayIncome(NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()) + runningStatDTO.getMemberPayIncome());
      runningStatDTO.setIncomeSum(runningStatDTO.getCashIncome() + runningStatDTO.getUnionPayIncome() + runningStatDTO.getChequeIncome());
      return errorMessage.toString();
    }

    receptionRecordDTO = new ReceptionRecordDTO();
    receptionRecordDTO.setReceivableId(receivableDTO.getId());
    receptionRecordDTO.setAmount(0 - NumberUtil.doubleVal(receivableDTO.getSettledAmount()));
    receptionRecordDTO.setReceiveTime(0L);
    receptionRecordDTO.setMemberBalancePay(0 - NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()));
    receptionRecordDTO.setAccumulatePointsPay(0d);
    receptionRecordDTO.setAccumulatePoints(0);
    receptionRecordDTO.setChequeNo("");
    receptionRecordDTO.setCash(0 - NumberUtil.doubleVal(receivableDTO.getCash()));
    receptionRecordDTO.setBankCard(0 - NumberUtil.doubleVal(receivableDTO.getBankCard()));
    receptionRecordDTO.setCheque(0 - NumberUtil.doubleVal(receivableDTO.getCheque()));
    receptionRecordDTO.setMemberId(NumberUtil.longValue(receivableDTO.getMemberId()) > 0 ? receivableDTO.getMemberId() : null);
    receptionRecordDTO.setOriginDebt(0d);
    receptionRecordDTO.setRecordNum(1);
    receptionRecordDTO.setDiscount(0 - NumberUtil.doubleVal(receivableDTO.getDiscount()));
    receptionRecordDTO.setRemainDebt(0 - NumberUtil.doubleVal(receivableDTO.getDebt()));
    receptionRecordDTO.setShopId(receivableDTO.getShopId());
    receptionRecordDTO.setOrderId(receivableDTO.getOrderId());

    List<RepealOrderDTO> repealOrderDTOList = txnService.getRepealOrderByShopIdAndOrderId(receivableDTO.getShopId(), receivableDTO.getOrderId());
    if (CollectionUtils.isEmpty(repealOrderDTOList)) {
      errorMessage.append("shopId:" + receivableDTO.getShopId() + ",orderId:" + receivableDTO.getOrderId() + "在repealOrder表中无记录");
      return errorMessage.toString();
    }
    receptionRecordDTO.setReceptionDate(repealOrderDTOList.get(0).getRepealDate());
    receptionRecordDTO.setOrderTotal(0 - receivableDTO.getTotal());
    receptionRecordDTO.setOrderTypeEnum(orderTypes);
    receptionRecordDTO.setOrderStatusEnum(orderStatus);

    receptionRecordDTOList.add(receptionRecordDTO);

    runningStatDTO.setCashIncome(runningStatDTO.getCashIncome() - NumberUtil.doubleVal(receivableDTO.getCash()));
    runningStatDTO.setChequeIncome(runningStatDTO.getChequeIncome() - NumberUtil.doubleVal(receivableDTO.getCheque()));
    runningStatDTO.setUnionPayIncome(runningStatDTO.getUnionPayIncome() - NumberUtil.doubleVal(receivableDTO.getBankCard()));
    runningStatDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() - NumberUtil.doubleVal(receivableDTO.getDebt()));
    runningStatDTO.setMemberPayIncome(runningStatDTO.getMemberPayIncome() - NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()));

    return errorMessage.toString();
  }


  public String initWashBeautyOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    StringBuffer errorMessage = new StringBuffer();

    List<WashBeautyOrderDTO> washBeautyOrderDTOList = txnService.countWashBeautyAgentAchievements(shopId, startTime, endTime);//维修美容单记录

    if (CollectionUtils.isEmpty(washBeautyOrderDTOList)) {
      return errorMessage.toString();
    }
    for (WashBeautyOrderDTO washBeautyOrderDTO : washBeautyOrderDTOList) {
      if (washBeautyOrderDTO == null || washBeautyOrderDTO.getId() == null) {
        continue;
      }
      long orderId = washBeautyOrderDTO.getId();
      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null, orderId);
      if (receivableDTO == null) {
        errorMessage.append("shopId:" + shopId + ",orderId:" + orderId + "在receivable表中无记录");
        continue;
      } else {
        errorMessage.append(this.saveReceptionRecord(receivableDTO, false, OrderTypes.WASH_BEAUTY, OrderStatus.WASH_SETTLED, receptionRecordDTOList, runningStatDTO));
      }
    }
    return errorMessage.toString();
  }


  public String initMemberCardOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    StringBuffer errorMessage = new StringBuffer();

    List<MemberCardOrderDTO> memberCardOrderDTOList = txnService.countMemberAgentAchievements(shopId, startTime, endTime);

    if (CollectionUtils.isEmpty(memberCardOrderDTOList)) {
      return errorMessage.toString();
    }
    for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
      if (memberCardOrderDTO == null || memberCardOrderDTO.getId() == null) {
        continue;
      }

      long orderId = memberCardOrderDTO.getId();
      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null, orderId);
      if (receivableDTO == null) {
        errorMessage.append("shopId:" + shopId + ",orderId:" + orderId + "在receivable表中无记录");
        continue;
      } else {
        errorMessage.append(this.saveReceptionRecord(receivableDTO, false, OrderTypes.MEMBER_BUY_CARD, OrderStatus.MEMBERCARD_ORDER_STATUS, receptionRecordDTOList, runningStatDTO));
      }
    }
    return errorMessage.toString();
  }


  public Calendar getFirstDayOfYear() {
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(defaultYear, 2, 1, 0, 0, 0);
    return calendar;
  }

  public Date getInitDateFromRequest(HttpServletRequest request) {
    Date date = null;
    String dateString = request.getParameter("date");
    if (!StringUtil.isEmpty(dateString)) {
      date = DateUtil.getDateFromString(DateUtil.DATE_STRING_FORMAT_DAY, dateString);
      if (date == null) {
        LOG.error("/statInit.do");
        LOG.error("method=initRunningStat");
        LOG.error("流水数据初始化数据出错 日期输入不正确" + request.getParameter("date"));
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        date = new Date(getFirstDayOfYear().getTimeInMillis());
      }
    } else {
      date = new Date(getFirstDayOfYear().getTimeInMillis());
    }
    return date;
  }


  public List<Shop> getShopList(HttpServletRequest request) {

    //判断url中是否输入了shop_id,如果没有输入 默认初始化所有店铺

    List<Shop> shopList = new ArrayList<Shop>();

    if (StringUtil.isEmpty(request.getParameter("shopId"))) {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      shopList = configService.getShop();
    } else {
      long shopId = Long.valueOf(request.getParameter("shopId"));
      ShopDTO shopDTO = new ShopDTO();
      shopDTO.setId(shopId);
      Shop shop = new Shop(shopDTO);
      shopList.add(shop);
    }
    return shopList;
  }


  /**
   * 根据shop_id 开始时间start_time  结束时间end_time统计营业数据
   *
   * @param shopId
   * @param year      年
   * @param month     月
   * @param day       日
   * @param startTime 开始时间start_time
   * @param endTime   结束时间end_time
   * @throws Exception
   */
  public String countStat(long shopId, long year, long month, long day, long startTime,
                          long endTime, List<RunningStatDTO> runningStatDTOList, List<ReceptionRecordDTO> receptionRecordDTOList, List<PayableHistoryRecordDTO> payableHistoryRecordDTOList) throws Exception {


    StringBuffer errorMessage = new StringBuffer();

    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(shopId);
    runningStatDTO.setStatYear(year);
    runningStatDTO.setStatMonth(month);
    runningStatDTO.setStatDay(day);

    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set((int) year, (int) month - 1, (int) day);
    runningStatDTO.setStatDate(calendar.getTimeInMillis() - BUSINESS_STAT_TIME);

    errorMessage.append(this.initOldWashOrderReception(shopId, startTime, endTime, runningStatDTO, receptionRecordDTOList));
    errorMessage.append(this.initSalesOrderReception(shopId, startTime, endTime, runningStatDTO, receptionRecordDTOList));
    errorMessage.append(this.initRepairOrderReception(shopId, startTime, endTime, runningStatDTO, receptionRecordDTOList));
    errorMessage.append(this.initWashBeautyOrderReception(shopId, startTime, endTime, runningStatDTO, receptionRecordDTOList));
    errorMessage.append(this.initMemberCardOrderReception(shopId, startTime, endTime, runningStatDTO, receptionRecordDTOList));
    errorMessage.append(this.initPurchaseReturnReception(shopId, startTime, endTime, runningStatDTO, payableHistoryRecordDTOList));
    errorMessage.append(this.initDeposit(shopId, startTime, endTime, runningStatDTO, payableHistoryRecordDTOList));
    errorMessage.append(this.initInventoryOrderReception(shopId, startTime, endTime, runningStatDTO, payableHistoryRecordDTOList));
    errorMessage.append(this.initRepealOrderReception(shopId, startTime, endTime, runningStatDTO, receptionRecordDTOList));
    errorMessage.append(this.initInventoryDebtOrderReception(shopId, startTime, endTime, runningStatDTO, receptionRecordDTOList));

    runningStatDTO.setIncomeSum(runningStatDTO.getCashIncome() + runningStatDTO.getChequeIncome() + runningStatDTO.getUnionPayIncome());
    runningStatDTO.setRunningSum(runningStatDTO.getIncomeSum() - runningStatDTO.getExpenditureSum());

    runningStatDTOList.add(runningStatDTO);
    return errorMessage.toString();
  }


  public String initDeposit(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<PayableHistoryRecordDTO> payableHistoryRecordDTOList) {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    List<DepositDTO> depositDTOList = runningStatService.getDepositDTOListBySHopId(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(depositDTOList)) {
      return "";
    }
    for (DepositDTO depositDTO : depositDTOList) {
      runningStatDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() + NumberUtil.doubleVal(depositDTO.getCash()));
      runningStatDTO.setUnionPayExpenditure(runningStatDTO.getUnionPayExpenditure() + NumberUtil.doubleVal(depositDTO.getBankCardAmount()));
      runningStatDTO.setChequeExpenditure(runningStatDTO.getChequeExpenditure() + NumberUtil.doubleVal(depositDTO.getCheckAmount()));
      runningStatDTO.setExpenditureSum(NumberUtil.doubleVal(depositDTO.getCash()) + NumberUtil.doubleVal(depositDTO.getBankCardAmount()) + NumberUtil.doubleVal(depositDTO.getCheckAmount()));

      PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord();
      payableHistoryRecord.setShopId(depositDTO.getShopId());
      payableHistoryRecord.setDeduction(0.0);
      payableHistoryRecord.setCreditAmount(0.0);
      payableHistoryRecord.setCash(depositDTO.getCash());
      payableHistoryRecord.setBankCardAmount(depositDTO.getBankCardAmount());
      payableHistoryRecord.setCheckNo(depositDTO.getCheckNo());
      payableHistoryRecord.setCheckAmount(depositDTO.getCheckAmount());
      payableHistoryRecord.setDepositAmount(0.0);
      payableHistoryRecord.setActuallyPaid(NumberUtil.toReserve(depositDTO.getCash() + depositDTO.getBankCardAmount() + depositDTO.getCheckAmount(), NumberUtil.MONEY_PRECISION));
      payableHistoryRecord.setAmount(NumberUtil.toReserve(depositDTO.getCash() + depositDTO.getBankCardAmount() + depositDTO.getCheckAmount(), NumberUtil.MONEY_PRECISION));
      payableHistoryRecord.setSupplierId(depositDTO.getSupplierId());
      payableHistoryRecord.setPaymentType(PaymentTypes.SUPPLIER_DEPOSIT);
      if (depositDTO.getPayTime() <= endTime && depositDTO.getPayTime() >= startTime) {
        payableHistoryRecord.setPayTime(depositDTO.getPayTime());
      } else {
        payableHistoryRecord.setPayTime(endTime - BUSINESS_STAT_TIME);
      }
      payableHistoryRecord.setPaidAmount(NumberUtil.toReserve(depositDTO.getCash() + depositDTO.getBankCardAmount() + depositDTO.getCheckAmount(), NumberUtil.MONEY_PRECISION));
      payableHistoryRecord.setMaterialName(PaymentTypes.SUPPLIER_DEPOSIT.getName());

      payableHistoryRecordDTOList.add(payableHistoryRecord.toDTO());
    }
    return "";
  }

  public String initPurchaseReturnReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<PayableHistoryRecordDTO> payableHistoryRecordDTOList) throws Exception {
    StringBuffer errorMessage = new StringBuffer();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    double cashExpenditure = 0.0; //现金收入总计
    double depositExpenditure = 0.0;//定金收入总计

    //营业额:销售
    int totalNum = txnService.countPurchaseReturnOrderByCreated(shopId, startTime, endTime);
    if (totalNum <= 0) {
      return errorMessage.toString();
    }

    Pager pager = new Pager(totalNum, FIRST_PAGE_NO, PAGE_SIZE);
    for (int index = 1; index <= pager.getTotalPage(); index++) {

      pager = new Pager(totalNum, index, PAGE_SIZE);

      List<PurchaseReturnDTO> purchaseReturnList = txnService.getPurchaseReturnOrderListByPager(shopId, startTime, endTime, pager);

      if (CollectionUtils.isEmpty(purchaseReturnList)) {
        continue;
      }

      for (PurchaseReturnDTO purchaseReturnDTO : purchaseReturnList) {
        if (purchaseReturnDTO == null || purchaseReturnDTO.getId() == null) {
          continue;
        }


        PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord();
        payableHistoryRecord.setShopId(purchaseReturnDTO.getShopId());
        payableHistoryRecord.setDeduction(0D);
        payableHistoryRecord.setCreditAmount(0D);
        payableHistoryRecord.setPayTime(purchaseReturnDTO.getCreationDate());
        payableHistoryRecord.setBankCardAmount(0D);
        payableHistoryRecord.setCheckAmount(0D);

        SupplierReturnPayableDTO supplierReturnPayableDTO = supplierPayableService.getSupplierReturnPayableByPurchaseReturnId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());
        if (supplierReturnPayableDTO == null || NumberUtil.doubleVal(supplierReturnPayableDTO.getCash()) > 0) {
          cashExpenditure += purchaseReturnDTO.getTotal();

          //退入现金
          payableHistoryRecord.setCash(0 - purchaseReturnDTO.getTotal());
          payableHistoryRecord.setDepositAmount(0D);
          payableHistoryRecord.setActuallyPaid(0 - purchaseReturnDTO.getTotal());
          payableHistoryRecord.setPaymentType(PaymentTypes.INVENTORY_RETURN_CASH);

        } else {
          depositExpenditure += purchaseReturnDTO.getTotal();

          //退入定金
          payableHistoryRecord.setDepositAmount(0 - purchaseReturnDTO.getTotal());
          payableHistoryRecord.setCash(0D);
          payableHistoryRecord.setActuallyPaid(0D);
          payableHistoryRecord.setPaymentType(PaymentTypes.INVENTORY_RETURN_DEPOSIT);

        }

        payableHistoryRecord.setAmount(0 - purchaseReturnDTO.getTotal());
        payableHistoryRecord.setSupplierId(purchaseReturnDTO.getSupplierId());

        payableHistoryRecordDTOList.add(payableHistoryRecord.toDTO());
      }
    }


    runningStatDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() - cashExpenditure);
    runningStatDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() - depositExpenditure);

    return errorMessage.toString();
  }


  public String initInventoryDebtOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    StringBuffer stringBuffer = new StringBuffer();

    double cashExpenditure = 0.0; //现金支出总计
    double chequeExpenditure = 0.0; //支票支出总和
    double unionPayExpenditure = 0.0; //银联支出总和
    double depositPayExpenditure = 0.0;//入库单定金支付
    double debtWithdrawalExpenditure = 0.0;//欠款回笼总和
    double debtNewExpenditure = 0.0; //供应商新增欠款

    int size = txnService.countPayHistoryRecordByPayTime(shopId, startTime, endTime);
    if (size <= 0) {
      return stringBuffer.toString();
    }
    Pager pager = new Pager(size, FIRST_PAGE_NO, size);

    List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = txnService.getPayHistoryRecordByPayTime(shopId, startTime, endTime, pager);
    if (CollectionUtils.isEmpty(payableHistoryRecordDTOList)) {
      return stringBuffer.toString();
    }

    for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
      if (PaymentTypes.INVENTORY_DEBT == payableHistoryRecordDTO.getPaymentType()) {
        cashExpenditure += payableHistoryRecordDTO.getCash();
        unionPayExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount());
        chequeExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount());
        depositPayExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount());
        debtWithdrawalExpenditure += (NumberUtil.doubleVal(payableHistoryRecordDTO.getCash()) + NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount())
            + NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount()) + NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount()));
      } else if (PaymentTypes.INVENTORY == payableHistoryRecordDTO.getPaymentType()) {
        cashExpenditure += payableHistoryRecordDTO.getCash();
        unionPayExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount());
        chequeExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount());
        depositPayExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount());
        debtNewExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getCreditAmount());
      }
    }

    runningStatDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() + cashExpenditure);
    runningStatDTO.setUnionPayExpenditure(runningStatDTO.getUnionPayExpenditure() + unionPayExpenditure);
    runningStatDTO.setChequeExpenditure(runningStatDTO.getChequeExpenditure() + chequeExpenditure);
    runningStatDTO.setDepositPayExpenditure(runningStatDTO.getDepositPayExpenditure() + depositPayExpenditure);
    runningStatDTO.setDebtWithdrawalExpenditure(runningStatDTO.getDebtWithdrawalExpenditure() + debtWithdrawalExpenditure);
    runningStatDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() + debtNewExpenditure);
    runningStatDTO.setExpenditureSum(runningStatDTO.getCashExpenditure() + runningStatDTO.getUnionPayExpenditure() + runningStatDTO.getChequeExpenditure());

    return stringBuffer.toString();
  }

  public String initRepealOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    StringBuffer errorMessage = new StringBuffer();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    double debtNew = 0.0;//新增欠款
    double cashIncome = 0.0; //现金收入总计
    double chequeIncome = 0.0;    //支票收入总和
    double unionPayIncome = 0.0; //银联收入总和
    double memberPayIncome = 0.0;//会员支付总和

    int totalNum = txnService.countRepealOrderByRepealDate(shopId, startTime, endTime);
    if (totalNum <= 0) {
      return errorMessage.toString();
    }

    Pager pager = new Pager(totalNum, FIRST_PAGE_NO, PAGE_SIZE);
    for (int index = 1; index <= pager.getTotalPage(); index++) {

      pager = new Pager(totalNum, index, PAGE_SIZE);

      List<RepealOrder> repealOrderList = txnService.getRepealOrderListByRepealDate(shopId, startTime, endTime, pager);

      if (CollectionUtils.isEmpty(repealOrderList)) {
        continue;
      }

      for (RepealOrder repealOrder : repealOrderList) {

        OrderTypes orderTypes = repealOrder.getOrderTypeEnum();
        if (OrderTypes.REPAIR == orderTypes || OrderTypes.SALE == orderTypes) {

          if (repealOrder == null || repealOrder.getOrderId() == null || NumberUtil.longValue(repealOrder.getVestDate()) <= 0) {
            continue;
          }

          long orderId = repealOrder.getOrderId();

          ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, null, orderId);
          if (receivableDTO == null) {
            errorMessage.append("shopId:" + shopId + ",orderId:" + orderId + "在receivable表中无记录");
          } else {
            debtNew += (receivableDTO.getDebt() < 0 == true ? (0 - receivableDTO.getDebt()) : receivableDTO.getDebt());
            cashIncome += NumberUtil.doubleVal(receivableDTO.getCash());
            unionPayIncome += NumberUtil.doubleVal(receivableDTO.getBankCard());
            chequeIncome += NumberUtil.doubleVal(receivableDTO.getCheque());
            memberPayIncome += NumberUtil.doubleVal(receivableDTO.getMemberBalancePay());

            if (OrderStatus.SALE_REPEAL == repealOrder.getStatusEnum() || OrderStatus.REPAIR_REPEAL == repealOrder.getStatusEnum()) {
              errorMessage.append(this.saveReceptionRecord(receivableDTO, true, repealOrder.getOrderTypeEnum(), repealOrder.getStatusEnum(), receptionRecordDTOList, runningStatDTO));
            } else {
              errorMessage.append(this.saveReceptionRecord(receivableDTO, false, repealOrder.getOrderTypeEnum(), repealOrder.getStatusEnum(), receptionRecordDTOList, runningStatDTO));
            }
          }
          continue;
        }

//        if (OrderTypes.INVENTORY == orderTypes) {
//          PurchaseInventoryDTO purchaseInventoryDTO = txnService.getPurchaseInventoryById(repealOrder.getOrderId(), repealOrder.getShopId());
//          PayableDTO payableDTO = supplierPayableService.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
//          if (payableDTO != null) {
//            //入库单 流水统计
//            cashExpenditure = NumberUtil.doubleVal(payableDTO.getCash());
//            unionPayExpenditure = NumberUtil.doubleVal(payableDTO.getBankCard());
//            chequeExpenditure = NumberUtil.doubleVal(payableDTO.getCheque());
//            debtNewExpenditure = NumberUtil.doubleVal(payableDTO.getCreditAmount());
//            depositPayExpenditure = NumberUtil.doubleVal(payableDTO.getDeposit());
//          } else {
//            errorMessage.append("shopId:" + shopId + ",orderId:" + purchaseInventoryDTO.getId() + "在payable表中无记录");
//            cashExpenditure += purchaseInventoryDTO.getTotal();
//          }
//
//          List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = supplierPayableService.getPayableHistoryRecord(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierId(), purchaseInventoryDTO.getId());
//          for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
//            if (PaymentTypes.INVENTORY_DEBT == payableHistoryRecordDTO.getPaymentType()) {
//              debtWithdrawalExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getActuallyPaid());
//            }
//          }
//        }
      }
    }

    return errorMessage.toString();
  }


  public String initInventoryOrderReception(long shopId, long startTime, long endTime, RunningStatDTO runningStatDTO, List<PayableHistoryRecordDTO> recordDTOList) throws Exception {
    StringBuffer errorMessage = new StringBuffer();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    double cashExpenditure = 0.0; //现金支出总计
    double chequeExpenditure = 0.0; //支票支出总和
    double unionPayExpenditure = 0.0; //银联支出总和
    double debtNewExpenditure = 0.0; //供应商新增欠款
    double depositPayExpenditure = 0.0;//入库单定金支付
    double debtWithdrawalExpenditure = 0.0;//欠款回笼总和
    //营业额:销售
    int totalNum = txnService.countPurchaseInventoryOrderByCreated(shopId, startTime, endTime);
    if (totalNum <= 0) {
      return errorMessage.toString();
    }

    Pager pager = new Pager(totalNum, FIRST_PAGE_NO, PAGE_SIZE);
    for (int index = 1; index <= pager.getTotalPage(); index++) {

      pager = new Pager(totalNum, index, PAGE_SIZE);

      List<PurchaseInventoryDTO> purchaseInventoryDTOList = txnService.getInventoryOrderListByPager(shopId, startTime, endTime, pager);

      if (CollectionUtils.isEmpty(purchaseInventoryDTOList)) {
        continue;
      }

      for (PurchaseInventoryDTO purchaseInventoryDTO : purchaseInventoryDTOList) {
        if (purchaseInventoryDTO == null || purchaseInventoryDTO.getId() == null) {
          continue;
        }

        if (purchaseInventoryDTO.getStatus() == OrderStatus.PURCHASE_INVENTORY_REPEAL) {
          continue;
        }

        String materialName = "";
        Long repealDate = null;

        PayableDTO payableDTO = supplierPayableService.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
        if (payableDTO == null) {
          PayableDTO newPayableDTO = new PayableDTO();
          newPayableDTO.setAmount(purchaseInventoryDTO.getTotal());
          newPayableDTO.setShopId(purchaseInventoryDTO.getShopId());
          newPayableDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
          newPayableDTO.setPaidAmount(purchaseInventoryDTO.getTotal());
          newPayableDTO.setPurchaseInventoryId(purchaseInventoryDTO.getId());
          newPayableDTO.setPayTime(purchaseInventoryDTO.getCreationDate());
          newPayableDTO.setCash(purchaseInventoryDTO.getTotal());
          newPayableDTO.setBankCard(0D);
          newPayableDTO.setCheque(0D);
          newPayableDTO.setDeposit(0D);
          if (purchaseInventoryDTO.getItemDTOs() != null) {
            for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
              if (purchaseInventoryItemDTO == null) {
                LOG.error("入库单内没有商品！");
                continue;
              }
              materialName = materialName + purchaseInventoryItemDTO.getProductName() + ";";
            }
          }
          if (purchaseInventoryDTO.getStatus() == OrderStatus.PURCHASE_INVENTORY_REPEAL) {
            newPayableDTO.setStatus(PayStatus.REPEAL);
          } else {
            newPayableDTO.setStatus(PayStatus.USE);
          }

          if (!StringUtil.isEmpty(materialName) && materialName.length() > 500) {
            newPayableDTO.setMaterialName(materialName.substring(0, 450) + "...");
          } else {
            newPayableDTO.setMaterialName(materialName);
          }
          newPayableDTO.setPayTime(purchaseInventoryDTO.getCreationDate());
          supplierPayableService.savePayable(newPayableDTO);
          payableDTO = supplierPayableService.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
        }

        if (payableDTO == null) {
          LOG.error("shop_id:" + purchaseInventoryDTO.getShopId() + "purchaseInventoryId:" + purchaseInventoryDTO.getId() + "初始化实付记录失败");
          continue;
        }

        double cash = 0.0;  //现金
        double bankCard = 0.0; //银行卡
        double cheque = 0.0; //支票
        double deposit = 0.0; //定金
        double deduction = 0.0;//折扣
        double creditAmount = 0.0;//欠款


        List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = supplierPayableService.getPayableHistoryRecord(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierId(), purchaseInventoryDTO.getId(),null);
        //没有记录
        if (CollectionUtils.isEmpty(payableHistoryRecordDTOList)) {

          PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
          payableHistoryRecordDTO.setShopId(purchaseInventoryDTO.getShopId());
          payableHistoryRecordDTO.setDeduction(0D);
          payableHistoryRecordDTO.setCreditAmount(0D);
          payableHistoryRecordDTO.setCash(purchaseInventoryDTO.getTotal());
          payableHistoryRecordDTO.setBankCardAmount(0D);
          payableHistoryRecordDTO.setCheckAmount(0D);
          payableHistoryRecordDTO.setDepositAmount(0D);
          payableHistoryRecordDTO.setActuallyPaid(purchaseInventoryDTO.getTotal());
          payableHistoryRecordDTO.setAmount(purchaseInventoryDTO.getTotal());
          payableHistoryRecordDTO.setPurchaseInventoryId(purchaseInventoryDTO.getId());
          payableHistoryRecordDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
          payableHistoryRecordDTO.setPayableId(payableDTO.getId());
          payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY);
          payableHistoryRecordDTO.setPaidAmount(purchaseInventoryDTO.getTotal());
          payableHistoryRecordDTO.setPaidTime(purchaseInventoryDTO.getCreationDate());
          payableHistoryRecordDTO.setStatus(PayStatus.USE);

          if (!StringUtil.isEmpty(payableDTO.getMaterialName()) && payableDTO.getMaterialName().length() > 500) {
            payableHistoryRecordDTO.setMaterialName(payableDTO.getMaterialName().substring(0, 450) + "...");
          } else {
            payableHistoryRecordDTO.setMaterialName(payableDTO.getMaterialName());
          }
          recordDTOList.add(payableHistoryRecordDTO);
          cashExpenditure = purchaseInventoryDTO.getTotal();

          payableDTO.setCash(payableDTO.getAmount() - payableDTO.getCreditAmount() - payableDTO.getDeduction());
          payableDTO.setPaidAmount(payableDTO.getCash());
          payableDTO.setCheque(0D);
          payableDTO.setBankCard(0D);
          payableDTO.setDeposit(0D);
          supplierPayableService.updatePayable(payableDTO);

        } else {
          for (int i = 0; i < payableHistoryRecordDTOList.size(); i++) {
            PayableHistoryRecordDTO payableHistoryRecordDTO = payableHistoryRecordDTOList.get(i);

            cash += payableHistoryRecordDTO.getCash();
            bankCard += payableHistoryRecordDTO.getBankCardAmount();
            cheque += payableHistoryRecordDTO.getCheckAmount();
            deposit += payableHistoryRecordDTO.getDepositAmount();
            deduction += payableHistoryRecordDTO.getDeduction();

            if (NumberUtil.longValue(payableHistoryRecordDTO.getPaidTime()) <= endTime && i == 0) {
              cashExpenditure += payableHistoryRecordDTO.getCash();
              unionPayExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount());
              chequeExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount());
              depositPayExpenditure += NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount());
            }

            if (i == 0) {
              payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY);
              payableHistoryRecordDTO.setStatus(PayStatus.USE);
              debtNewExpenditure = NumberUtil.doubleVal(payableHistoryRecordDTO.getCreditAmount());
            } else {

              if (NumberUtil.longValue(payableHistoryRecordDTO.getPaidTime()) <= endTime) {
                payableHistoryRecordDTO.setPaymentType(PaymentTypes.INVENTORY_DEBT);
                debtWithdrawalExpenditure += (NumberUtil.doubleVal(payableHistoryRecordDTO.getCash()) + NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount())
                    + NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount()) + NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount()));
              }
            }
            recordDTOList.add(payableHistoryRecordDTO);
          }
          payableDTO.setCash(cash);
          payableDTO.setCheque(cheque);
          payableDTO.setBankCard(bankCard);
          payableDTO.setPaidAmount(payableDTO.getAmount() - payableDTO.getCreditAmount() - payableDTO.getDeduction());
          payableDTO.setDeposit(deposit);
          supplierPayableService.updatePayable(payableDTO);
        }
      }
    }

    runningStatService.saveOrUpdatePayRecordList(recordDTOList);
    recordDTOList.clear();
    recordDTOList = new ArrayList<PayableHistoryRecordDTO>();
    return errorMessage.toString();

  }


  public String initReceivableByShopId(Long shopId) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    StringBuffer errorMessage = new StringBuffer();

    int pageSize = PAGE_SIZE;//每次查询的最大条数

    LOG.info("店面shop_id:" + shopId + ",后台开始初始化Receivable");

    //分页查询 先获得该店面下实收的条数
    int receivableTotalNumber = (int) txnService.countReceivableDTOByShopId(shopId);
    if (receivableTotalNumber <= 0) {
      return "";
    }

    Pager pager = new Pager(receivableTotalNumber, FIRST_PAGE_NO, PAGE_SIZE);
    //分次读取数据
    for (int index = 1; index <= pager.getTotalPage(); index++) {

      pager = new Pager(receivableTotalNumber, index, PAGE_SIZE);
      List<ReceivableDTO> receivableDTOList = txnService.getReceivableDTOList(shopId, index - 1, pageSize);
      if (CollectionUtils.isNotEmpty(receivableDTOList)) {
        //更新实收表
        try {
          errorMessage.append(runningStatService.initReceivable(receivableDTOList));
        } catch (Exception e) {
          LOG.error("初始化Receivable，shop_id :" + shopId + "失败,系统继续初始化下一个");
          errorMessage.append("初始化Receivable，shop_id :" + shopId + "失败,系统继续初始化下一个");
          LOG.error(e.getMessage(), e);
          continue;
        }
      }
    }
    LOG.info("店面shop_id:" + shopId + ",后台结束初始化Receivable");
    return errorMessage.toString();
  }

  /**
   * 施工单或销售单作废 更新收入记录
   * @param shopId
   * @param orderId
   * @return ：单据的欠款回笼金额 和这个单据产生的新增欠款
   * @throws Exception
   */
  public RunningStatDTO updateOrderRepealReception(long shopId ,long orderId) throws Exception {

    RunningStatDTO runningStatDTO = new RunningStatDTO();

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    double debtWithdrawalIncome = 0;//单据欠款回笼金额
    double debtNewIncome = 0;//这个单据产生的新增欠款
    //如果单据类型或者单据内容为空 返回
    double customerDebtDiscount = 0;//欠款结算时的折扣

    //获得该单据的收入记录
    List<ReceptionRecordDTO> receptionRecordDTOList = txnService.getReceptionRecordByOrderId(shopId, orderId, null);
    if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
      return runningStatDTO;
  }

    for (int index = 0; index < receptionRecordDTOList.size(); index++) {

      ReceptionRecordDTO receptionRecordDTO = receptionRecordDTOList.get(index);

      if (receptionRecordDTO.getRecordNum() != null && receptionRecordDTO.getRecordNum().intValue() == 0) {
        debtNewIncome = NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt());
      }
      if (OrderTypes.DEBT == receptionRecordDTO.getOrderTypeEnum()) {
        debtWithdrawalIncome += receptionRecordDTO.getAmount();
        customerDebtDiscount += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());

      }
    }

    runningStatDTO.setDebtNewIncome(debtNewIncome);
    runningStatDTO.setDebtWithdrawalIncome(debtWithdrawalIncome);
    runningStatDTO.setCustomerDebtDiscount(customerDebtDiscount);
    return runningStatDTO;
  }



  }
