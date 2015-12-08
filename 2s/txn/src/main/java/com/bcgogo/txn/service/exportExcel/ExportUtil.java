package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PaymentTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-9-6
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ExportUtil {
  private static final Log LOG = LogFactory.getLog(ExportUtil.class);

  public static final int PAGE_SIZE = 25;       //页面显示条数
  public static final int DEFAULT_PAGE_NO = 1;  //默认查询页码 1
  public static final int STRING_SIZE = 2;//默认查询到的list 大小为2
  public static final int STRING_SIZE2 = 3;//默认查询到的list 大小为3
  public static final int DEFAULT_SIZE = 0;//默认大小为0
  public static final String QUERY_TYPE_DAY = "day"; //营业统计按天查询
  public static final String QUERY_TYPE_MONTH = "month";//营业统计按月查询
  public static final String QUERY_TYPE_YEAR = "year"; //营业统计按年查询
  public static final String INCOME = "income"; //流水统计：收入
  public static final String EXPENDITURE = "expenditure"; //流水统计：支出
  public static final int QUERY_SIZE = 4;//默认查询到的list 大小为2


  /**
   * 根据排序类型获得对item_index表的排序sql语句
   *
   * @param arrayType
   * @return
   */
  public String getItemArrayType(String arrayType) {
    arrayType = arrayType.replaceAll("total", "order_total_amount");
    return arrayType;
  }

  /**
   * 根据查询时间 和查询类型 day month year获得开始时间或者结束时间
   *
   * @param queryDate
   * @param type
   * @param getType
   * @return
   */
  public Long getTimeByQueryDateAndType(Long queryDate, String type, String getType) {
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTimeInMillis(queryDate);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    calendar.set(year, month - 1, day, 0, 0, 0);
    long startTime = calendar.getTimeInMillis();
    long endTime = calendar.getTimeInMillis();

    if (type.equals(QUERY_TYPE_DAY)) {
      calendar.add(Calendar.DATE, 1);
      endTime = calendar.getTimeInMillis();
    } else if (type.equals(QUERY_TYPE_MONTH)) {
      calendar.set(year, month - 1, 1, 0, 0, 0);
      startTime = calendar.getTimeInMillis();
      calendar.add(Calendar.MONTH, 1);
      endTime = calendar.getTimeInMillis();
    } else if (type.equals(QUERY_TYPE_YEAR)) {
      calendar.set(year, 0, 1, 0, 0, 0);
      startTime = calendar.getTimeInMillis();
      calendar.add(Calendar.YEAR, 1);
      endTime = calendar.getTimeInMillis();
    }

    if (getType.equals("startTime")) {
      return startTime;
    }
    return endTime;
  }
  /**
   * 根据前台传递过来的排序类型 转换成需要的sql排序语句
   *
   * @param arrayType
   * @return
   */
  public String getArrayTypeByType(String arrayType) {

    if (StringUtil.isEmpty(arrayType)) {
      arrayType = "timeDesc";
    }
    if (arrayType.equals("timeDesc")) {
      arrayType = " order by created desc";
    } else if (arrayType.equals("timeAsc")) {
      arrayType = " order by created asc";
    } else if (arrayType.equals("moneyDesc")) {
      arrayType = " order by total desc";
    } else if (arrayType.equals("moneyAsc")) {
      arrayType = " order by total asc";
    } else {
      arrayType = " order by created desc";
    }
    return arrayType;
  }

  /**
   * 拿到查询结果的单据条数
   *
   * @param stringList
   * @param index
   * @return
   */
  public int getIntValueByIndex(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != STRING_SIZE) {
      return DEFAULT_SIZE;
    }
    return Integer.valueOf(stringList.get(index)).intValue();
  }

  /**
   * 拿到查询结果的单据条数
   *
   * @param stringList
   * @param index
   * @return
   */
  public int getIntValueByIndex2(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != STRING_SIZE2) {
      return DEFAULT_SIZE;
    }
    return Integer.valueOf(stringList.get(index)).intValue();
  }

  /**
   * 拿到查询结果的单据总和  （用于体现会员折扣统计的地方）
   *
   * @param stringList
   * @param index
   * @return
   */
  public double getDoubleValueByIndex2(List<String> stringList, int index) {
   if (stringList == null || stringList.size() != STRING_SIZE2) {
      return DEFAULT_SIZE;
    }
    return Double.valueOf(stringList.get(index)).doubleValue();
  }

  /**
   * 拿到查询结果的单据总和
   *
   * @param stringList
   * @param index
   * @return
   */
  public double getDoubleValueByIndex(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != STRING_SIZE) {
      return DEFAULT_SIZE;
    }
    return Double.valueOf(stringList.get(index)).doubleValue();
  }

  /**
   * 根据前台传递的查询时间得到Long型时间
   *
   * @param dateStr
   * @param request
   * @return
   */
  public Long getTimeLongValue(String dateStr, HttpServletRequest request) {
    Long queryDate = null;
    try {
      queryDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, dateStr);
    } catch (ParseException e) {
      LOG.error("/statUtil.do");
      LOG.error("营业统计:查询日期出错");
      LOG.error(e.getMessage(), e);
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      queryDate = System.currentTimeMillis();
    }
    if(queryDate == null) {
      LOG.error("/statUtil.do");
      LOG.error("营业统计:查询日期解析错误,系统默认为当前系统时间");
      LOG.error("日期:" + dateStr);
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      queryDate = System.currentTimeMillis();
    }

    return queryDate;
  }




   /**
   * 根据memberCardOrderDTOList拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
   * @param memberCardOrderDTOList
   * @return
   * @throws Exception
   */
  public List<String> getStringList(List<MemberCardOrderDTO> memberCardOrderDTOList,List<ItemIndexDTO> itemIndexDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<String> stringList = new ArrayList<String>();

    Double pageTotal = 0.0; //本页小计单据总额
    Double costTotal = 0.0;//单据成本总和
    Double settleTotal = 0.0; //单据实收总和
    Double debtTotal = 0.0;   //单据欠款总和
    Double discountTotal = 0.0; //单据折扣总和
    Double profitTotal = 0.0;  //单据毛利总和

    if(CollectionUtils.isNotEmpty(itemIndexDTOList)) {
      for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
        pageTotal += itemIndexDTO.getOrderTotalAmount();
        if (itemIndexDTO.getOrderType() == OrderTypes.WASH_BEAUTY) {
          itemIndexDTO.setVehicleYear(itemIndexDTO.getOrderId().toString());
          List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(itemIndexDTO.getShopId(), itemIndexDTO.getOrderId());
          if (CollectionUtils.isEmpty(orderIndexDTOList)) {
            LOG.error("/businessStat.do");
            LOG.error("method=getStringList");
            LOG.error("order_id:" + itemIndexDTO.getOrderId() + "orderIndex记录为空");
            LOG.error("shopId:" + itemIndexDTO.getShopId());
          } else {
            itemIndexDTO.setItemName(orderIndexDTOList.get(0).getOrderContent());
          }

          ReceivableDTO receivableDTO = txnService.getReceivableByShopIdOrderId(itemIndexDTO.getShopId(),itemIndexDTO.getOrderId());
          if (receivableDTO == null) {
            LOG.error("/businessStat.do");
            LOG.error("method=getStringList");
            LOG.error("营业统计:获取洗车单实收记录出错");
            LOG.error("itemIndexDTO:" + itemIndexDTO.toString());
            LOG.error("shopId:" + itemIndexDTO.getShopId());
            continue;
          }
          debtTotal += receivableDTO.getDebt();
          settleTotal += receivableDTO.getSettledAmount();
          discountTotal += (receivableDTO.getTotal() - receivableDTO.getDebt() - receivableDTO.getSettledAmount());
          profitTotal += receivableDTO.getDebt() + receivableDTO.getSettledAmount();
        }else{
          profitTotal += itemIndexDTO.getOrderTotalAmount();
          settleTotal += NumberUtil.doubleVal(itemIndexDTO.getOrderTotalAmount());
        }
      }
    }


    if(CollectionUtils.isNotEmpty(memberCardOrderDTOList)) {
      for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
        pageTotal += memberCardOrderDTO.getTotal();

        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdOrderId(memberCardOrderDTO.getShopId(), memberCardOrderDTO.getId());
        if (receivableDTO == null) {
          LOG.error("/businessStat.do");
          LOG.error("method=getMemberOrder");
          LOG.error("营业统计:获得会员详细列表，获取实收记录出错");
          LOG.error("member_card_order:" + memberCardOrderDTO.toString());
          LOG.error("shopId:" + memberCardOrderDTO.getShopId());
          memberCardOrderDTO.setSettledAmount(0d);
        } else {
          memberCardOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
          debtTotal += receivableDTO.getDebt();
          settleTotal += receivableDTO.getSettledAmount();
          discountTotal += (receivableDTO.getTotal() - receivableDTO.getDebt() - receivableDTO.getSettledAmount());
          profitTotal += receivableDTO.getDebt() + receivableDTO.getSettledAmount();
        }
      }
    }

    stringList.add(String.valueOf(NumberUtil.toReserve(costTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(settleTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(debtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(discountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(profitTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));

    return stringList;
  }


  /**
   * 根据itemindex拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
   * @param
   * @return
   * @throws Exception
   */
  public List<String> getWashBeautyOrderStringList(List<ItemIndexDTO> itemIndexDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<String> stringList = new ArrayList<String>();

    Double pageTotal = 0.0; //本页小计单据总额
    Double costTotal = 0.0;//单据成本总和
    Double settleTotal = 0.0; //单据实收总和
    Double debtTotal = 0.0;   //单据欠款总和
    Double discountTotal = 0.0; //单据折扣总和
    Double profitTotal = 0.0;  //单据毛利总和

    if(CollectionUtils.isNotEmpty(itemIndexDTOList)) {
      for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
        if(null != itemIndexDTO.getAfterMemberDiscountOrderTotal())
        {
          pageTotal += itemIndexDTO.getAfterMemberDiscountOrderTotal();
        }
        else
        {
          pageTotal += itemIndexDTO.getOrderTotalAmount();
        }

        if (itemIndexDTO.getOrderType() == OrderTypes.WASH_BEAUTY) {
          itemIndexDTO.setVehicleYear(itemIndexDTO.getOrderId().toString());
          List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(itemIndexDTO.getShopId(), itemIndexDTO.getOrderId());
          if (CollectionUtils.isEmpty(orderIndexDTOList)) {
            LOG.error("/businessStat.do");
            LOG.error("method=getStringList");
            LOG.error("order_id:" + itemIndexDTO.getOrderId() + "orderIndex记录为空");
            LOG.error("shopId:" + itemIndexDTO.getShopId());
          } else {
            itemIndexDTO.setItemName(orderIndexDTOList.get(0).getOrderContent());
          }

          ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(itemIndexDTO.getShopId(), OrderTypes.MEMBER_BUY_CARD, itemIndexDTO.getOrderId());
          if (receivableDTO == null) {
            LOG.error("/businessStat.do");
            LOG.error("method=getStringList");
            LOG.error("营业统计:获取洗车单实收记录出错");
            LOG.error("itemIndexDTO:" + itemIndexDTO.toString());
            LOG.error("shopId:" + itemIndexDTO.getShopId());
            continue;
          }
          if(null!= receivableDTO && null == receivableDTO.getAfterMemberDiscountTotal())
          {
            receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getTotal());
          }
          debtTotal += receivableDTO.getDebt();
          settleTotal += receivableDTO.getSettledAmount();
          discountTotal += (receivableDTO.getAfterMemberDiscountTotal() - receivableDTO.getDebt() - receivableDTO.getSettledAmount());
          profitTotal += receivableDTO.getDebt() + receivableDTO.getSettledAmount();
        }else{
          profitTotal += itemIndexDTO.getOrderTotalAmount();
          settleTotal += NumberUtil.doubleVal(itemIndexDTO.getOrderTotalAmount());
        }
      }
    }

    stringList.add(String.valueOf(NumberUtil.toReserve(costTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(settleTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(debtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(discountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(profitTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));

    return stringList;
  }


  /**
   * 根据返回结果集合，总计本页所需数据
   * @param receptionRecordDTOList
   * @param runningStatDTOList
   * @param payableHistoryRecordDTOList
   * @return
   * @throws Exception
   */
  public List<String> getRunningStringList(List<ReceptionRecordDTO> receptionRecordDTOList,List<RunningStatDTO> runningStatDTOList,
                                           List<PayableHistoryRecordDTO> payableHistoryRecordDTOList,String statType) throws Exception {

    IUserService userService = ServiceManager.getService(IUserService.class);

    List<String> stringList = new ArrayList<String>();

    double pageTotal = 0.0; //本页单据总额
    double pagePayTotal = 0.0;//本页实收或者实付总额
    double pageCashTotal = 0.0; //本页现金总额
    double pageChequeTotal = 0.0; //本页支票总额
    double pageUnionPayTotal = 0.0;//本页银联总额
    double pageMemberPayTotal = 0.0; //本页会员总额
    double pageDepositPayTotal = 0.0; //本页定金总和
    double pageDebtTotal = 0.0; //本页欠款总额
    double pageDiscountTotal = 0.0; //本页折扣总和
    double pageStrikeAmountTotal = 0.0;//本页冲账总和
    if(CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
        pageTotal += NumberUtil.doubleVal(receptionRecordDTO.getOrderTotal());
        pageCashTotal += NumberUtil.doubleVal(receptionRecordDTO.getCash());
        pageChequeTotal += NumberUtil.doubleVal(receptionRecordDTO.getCheque());
        pageUnionPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getBankCard());
        pageMemberPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay());
        pageDebtTotal += NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt());
        pagePayTotal += NumberUtil.doubleVal(receptionRecordDTO.getAmount());
        pageDiscountTotal += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());
        pageStrikeAmountTotal += NumberUtil.doubleVal(receptionRecordDTO.getStrike());
        pageDepositPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getDeposit());
      }
    }else if(CollectionUtils.isNotEmpty(runningStatDTOList)) {
      for (RunningStatDTO runningStatDTO : runningStatDTOList) {
        if (INCOME.equals(statType)) {
          pageTotal += runningStatDTO.getIncomeSum();
          pageCashTotal += NumberUtil.doubleVal(runningStatDTO.getCashIncome());
          pageChequeTotal += NumberUtil.doubleVal(runningStatDTO.getChequeIncome());
          pageUnionPayTotal += NumberUtil.doubleVal(runningStatDTO.getUnionPayIncome());
          pageDepositPayTotal += NumberUtil.doubleVal(runningStatDTO.getCustomerDepositExpenditure());
          pageMemberPayTotal += NumberUtil.doubleVal(runningStatDTO.getMemberPayIncome());
          pageDebtTotal += NumberUtil.doubleVal(runningStatDTO.getDebtNewIncome());
          pageDiscountTotal += NumberUtil.doubleVal(runningStatDTO.getDebtWithdrawalIncome());
          pageStrikeAmountTotal += NumberUtil.doubleVal(runningStatDTO.getStrikeAmountIncome());
        } else {
          pageTotal += runningStatDTO.getExpenditureSum();
          pageCashTotal += NumberUtil.doubleVal(runningStatDTO.getCashExpenditure());
          pageChequeTotal += NumberUtil.doubleVal(runningStatDTO.getChequeExpenditure());
          pageUnionPayTotal += NumberUtil.doubleVal(runningStatDTO.getUnionPayExpenditure());
          pageDepositPayTotal += NumberUtil.doubleVal(runningStatDTO.getDepositPayExpenditure());
          pageDebtTotal += NumberUtil.doubleVal(runningStatDTO.getDebtNewExpenditure());
          pageDiscountTotal += NumberUtil.doubleVal(runningStatDTO.getDebtWithdrawalExpenditure());
          pageStrikeAmountTotal += NumberUtil.doubleVal(runningStatDTO.getStrikeAmountExpenditure());
        }
      }
    }else if(CollectionUtils.isNotEmpty(payableHistoryRecordDTOList)) {
      for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
        List<OrderIndexDTO> orderIndexDTOs = null;
        if(payableHistoryRecordDTO.getPurchaseInventoryId() != null) {
          orderIndexDTOs = ServiceManager.getService(ISearchService.class).getOrderIndexDTOByOrderId(payableHistoryRecordDTO.getShopId(), payableHistoryRecordDTO.getPurchaseInventoryId());
        }
        if(CollectionUtils.isNotEmpty(orderIndexDTOs)){
          payableHistoryRecordDTO.setCustomerName(CollectionUtil.getFirst(orderIndexDTOs).getCustomerOrSupplierName());
        }else if (payableHistoryRecordDTO.getSupplierId() != null) {
          SupplierDTO supplierDTO = userService.getSupplierById(payableHistoryRecordDTO.getSupplierId());
          payableHistoryRecordDTO.setCustomerName(supplierDTO.getName());
        }
        if (payableHistoryRecordDTO.getPaymentType() != null) {
          payableHistoryRecordDTO.setOrderType(payableHistoryRecordDTO.getPaymentType().getName());
        }

        pageTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getAmount());
        pageCashTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getCash());
        pageChequeTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount());
        pageUnionPayTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount());
        pageDepositPayTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount());
        pageDiscountTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getDeduction());
        pageDebtTotal += NumberUtil.toReserve(NumberUtil.doubleVal(payableHistoryRecordDTO.getCreditAmount()),NumberUtil.MONEY_PRECISION);
        pagePayTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getActuallyPaid());
        if(PaymentTypes.INVENTORY_RETURN == payableHistoryRecordDTO.getPaymentType())
        {
          Double addPageStrikeAmountTotal= payableHistoryRecordDTO.getStrikeAmount();

          if(null == addPageStrikeAmountTotal)
          {
            addPageStrikeAmountTotal = 0D;
          }
          if(addPageStrikeAmountTotal<0)
          {
            addPageStrikeAmountTotal = -addPageStrikeAmountTotal;
          }

          pageStrikeAmountTotal +=  addPageStrikeAmountTotal;
        }

      }
    }

    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pagePayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageCashTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageUnionPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageChequeTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageMemberPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDepositPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDiscountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDebtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageStrikeAmountTotal,NumberUtil.MONEY_PRECISION)));
    return stringList;
  }


   /**
   * 根据返回结果集合，总计本页所需数据
   * @param receptionRecordDTOList
   * @param
   * @param
   * @return
   * @throws Exception
   */
  public List<String> getRunningIncomeStringList(List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {

    IUserService userService = ServiceManager.getService(IUserService.class);

    List<String> stringList = new ArrayList<String>();

    double pageTotal = 0.0; //本页单据总额
    double pagePayTotal = 0.0;//本页实收或者实付总额
    double pageCashTotal = 0.0; //本页现金总额
    double pageChequeTotal = 0.0; //本页支票总额
    double pageUnionPayTotal = 0.0;//本页银联总额
    double pageMemberPayTotal = 0.0; //本页会员总额
    double pageDepositPayTotal = 0.0; //本页定金总和
    double pageDebtTotal = 0.0; //本页欠款总额
    double pageDiscountTotal = 0.0; //本页折扣总和
    double pageStrikeAmountTotal = 0.0;//本页冲账总和
    if(CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
        if(null == receptionRecordDTO.getAfterMemberDiscountTotal())
        {
          pageTotal += NumberUtil.doubleVal(receptionRecordDTO.getOrderTotal());
        }
        else
        {
          pageTotal += NumberUtil.doubleVal(receptionRecordDTO.getAfterMemberDiscountTotal());
        }
        pageCashTotal += NumberUtil.doubleVal(receptionRecordDTO.getCash());
        pageChequeTotal += NumberUtil.doubleVal(receptionRecordDTO.getCheque());
        pageUnionPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getBankCard());
        pageMemberPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay());
        pageDepositPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getDeposit()); // add by zhuj 
        pageDebtTotal += NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt());
        pagePayTotal += NumberUtil.doubleVal(receptionRecordDTO.getAmount());
        pageDiscountTotal += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());
        pageStrikeAmountTotal += NumberUtil.doubleVal(receptionRecordDTO.getStrike());
      }
    }

    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pagePayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageCashTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageUnionPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageChequeTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageMemberPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDepositPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDiscountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDebtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageStrikeAmountTotal,NumberUtil.MONEY_PRECISION)));
    return stringList;
  }

   /**
   * 拿到查询结果的单据条数
   *
   * @param stringList
   * @param index
   * @return
   */
  public int getIntValueForSale(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != QUERY_SIZE) {
      return DEFAULT_SIZE;
    }
    return Integer.valueOf(stringList.get(index)).intValue();
  }


}
