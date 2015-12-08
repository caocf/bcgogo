package com.bcgogo.txn.service;

import com.bcgogo.common.Sort;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PaymentTypes;
import com.bcgogo.enums.ReceivableStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-8-30
 * Time: 上午9:40
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RunningStatService implements IRunningStatService {
  public static final Logger LOG = LoggerFactory.getLogger(RunningStatService.class);

  /**
   * 根据年月日获得流水记录
   *
   * @param shopId
   * @param year
   * @param month
   * @param day
   * @return
   */
  public RunningStatDTO getRunningStatDTOByShopIdYearMonthDay(Long shopId, Long year, long month, long day) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<RunningStat> runningStatList = txnWriter.getRunningStatDTOByShopIdYearMonthDay(shopId, year, month, day);
    if (!CollectionUtils.isEmpty(runningStatList) && runningStatList.size() > 1) {
      LOG.warn("流水记录有一条以上");
      LOG.warn("参数:" + "shopId:" + shopId + "year:" + year + "month:" + month + "day:" + day);
    }
    return CollectionUtils.isEmpty(runningStatList) ? null : runningStatList.get(0).toDTO();
  }

  /**
   * 保存流水记录
   *
   * @param runningStatDTO
   * @return
   */
  public RunningStatDTO saveRunningStatDTO(RunningStatDTO runningStatDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      if (runningStatDTO == null) {
        return null;
      }

      RunningStat runningStat = null;

      if (runningStatDTO.getShopId() != null && runningStatDTO.getStatYear() != null && runningStatDTO.getStatMonth() != null && runningStatDTO.getStatDay() != null) {
        RunningStatDTO todayRunningStatDTO = this.getRunningStatDTOByShopIdYearMonthDay(runningStatDTO.getShopId(),
            runningStatDTO.getStatYear(), runningStatDTO.getStatMonth(), runningStatDTO.getStatDay());
        if (todayRunningStatDTO != null) {
          runningStat = txnWriter.getById(RunningStat.class, todayRunningStatDTO.getId());
          runningStat = runningStat.fromDTO(runningStatDTO, false);
          txnWriter.saveOrUpdate(runningStat);
          runningStatDTO = runningStat.toDTO();
        } else {
          runningStat = new RunningStat();
          runningStat = runningStat.fromDTO(runningStatDTO, false);
          txnWriter.save(runningStat);
          runningStatDTO = runningStat.toDTO();
        }
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    return runningStatDTO;

  }

  /**
   * 获得这一年的最后一条数据
   *
   * @param shopId
   * @param year
   * @return
   */
  public RunningStatDTO getLastRunningStatDTOByShopId(Long shopId, Long year) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RunningStat runningStat = txnWriter.getLastRunningStatDTOByShopId(shopId, year);
    return runningStat == null ? null : runningStat.toDTO();
  }

  /**
   * 流水统计
   *
   * @param statDTO
   * @param isRepeal 是否作废
   */
  public void runningStat(RunningStatDTO statDTO, boolean isRepeal) {

    double cashIncome = statDTO.getCashIncome(); //现金收入总和
    double chequeIncome = statDTO.getChequeIncome();    //支票收入总和
    double unionPayIncome = statDTO.getUnionPayIncome(); //银联收入总和

    double memberPayIncome = statDTO.getMemberPayIncome();  //会员支付总和

    double debtNewIncome = statDTO.getDebtNewIncome(); //客户新增欠款
    double debtWithdrawalIncome = statDTO.getDebtWithdrawalIncome(); //客户欠款回笼

    double cashExpenditure = statDTO.getCashExpenditure(); //现金支出总和
    double chequeExpenditure = statDTO.getChequeExpenditure(); //支票支出总和
    double unionPayExpenditure = statDTO.getUnionPayExpenditure(); //银联支出总和

    double debtNewExpenditure = statDTO.getDebtNewExpenditure(); //供应商新增欠款
    double debtWithdrawalExpenditure = statDTO.getDebtWithdrawalExpenditure(); //供应商欠款回笼总和

    double depositPayExpenditure = statDTO.getDepositPayExpenditure(); //供应商订金总和
    double customerDepositPayExpenditure = statDTO.getCustomerDepositExpenditure(); // 客户预收款使用总和

    double strikeAmountExpenditure= statDTO.getStrikeAmountExpenditure(); //供应商冲账总和
    double strikeAmountIncome = statDTO.getStrikeAmountIncome();

    double customerDebtDiscount = statDTO.getCustomerDebtDiscount();//客户欠款结算折扣
    double supplierDebtDiscount = statDTO.getSupplierDebtDiscount();//供应商欠款结算折扣

    double supplierReturnDebt = statDTO.getSupplierReturnDebt(); //店铺欠供应商钱；
    double customerReturnDebt = statDTO.getCustomerReturnDebt(); //

    double couponIncome=statDTO.getCouponIncome();  //流水统计代金券支付
    double couponExpenditure=statDTO.getCouponExpenditure();  //代金券支出总和
    //获得该店铺最新一条的流水数据
    RunningStatDTO runningStatDTO = this.getLastRunningStatDTOByShopId(statDTO.getShopId(), null);
    if (runningStatDTO == null) {
      if (isRepeal) {

//        statDTO.setRunningSum(0 - (cashIncome + chequeIncome + unionPayIncome - cashExpenditure - chequeExpenditure - unionPayExpenditure));
        statDTO.setRunningSum(0 - (cashIncome + chequeIncome + unionPayIncome + couponIncome - couponExpenditure - cashExpenditure - chequeExpenditure - unionPayExpenditure));  //add by litao
        statDTO.setIncomeSum(0 - (cashIncome + chequeIncome + unionPayIncome + couponIncome));
        statDTO.setCashIncome(0 - cashIncome);
        statDTO.setChequeIncome(0 - chequeIncome);
        statDTO.setUnionPayIncome(0 - unionPayIncome);
        statDTO.setMemberPayIncome(0 - memberPayIncome);
        statDTO.setDebtNewIncome(0 - debtNewIncome);
        statDTO.setDebtWithdrawalIncome(0 - statDTO.getDebtWithdrawalIncome());

        statDTO.setExpenditureSum(0 - (cashExpenditure + chequeExpenditure + unionPayExpenditure + couponExpenditure));
        statDTO.setCashExpenditure(0 - cashExpenditure);
        statDTO.setChequeExpenditure(0 - chequeExpenditure);
        statDTO.setUnionPayExpenditure(0 - unionPayExpenditure);
        statDTO.setDebtNewExpenditure(0 - statDTO.getDebtNewExpenditure());
        statDTO.setDebtWithdrawalExpenditure(0 - statDTO.getDebtWithdrawalExpenditure());
        statDTO.setDepositPayExpenditure(0 - statDTO.getDepositPayExpenditure());
        statDTO.setCustomerDepositExpenditure(0 - statDTO.getCustomerDepositExpenditure()); // add by zhuj
        statDTO.setStrikeAmountExpenditure(0-statDTO.getStrikeAmountExpenditure());
        statDTO.setCustomerDebtDiscount(0 - customerDebtDiscount);
        statDTO.setSupplierDebtDiscount(0 - supplierDebtDiscount);
        statDTO.setStrikeAmountIncome(0 - strikeAmountIncome);

        statDTO.setCustomerReturnDebt(0 -  customerReturnDebt);
        statDTO.setSupplierReturnDebt(0 -  supplierReturnDebt);

        statDTO.setCouponIncome(0 - couponIncome);  //add by litao
        statDTO.setCouponExpenditure(0 - couponExpenditure);  //add by litao
      } else {
//        statDTO.setRunningSum(cashIncome + chequeIncome + unionPayIncome - chequeExpenditure - cashExpenditure - unionPayExpenditure);
        statDTO.setRunningSum(cashIncome + chequeIncome + unionPayIncome + couponIncome - couponExpenditure - cashExpenditure - chequeExpenditure - unionPayExpenditure);  //add by litao
        statDTO.setIncomeSum(cashIncome + unionPayIncome + chequeIncome + couponIncome);
        statDTO.setExpenditureSum(cashExpenditure + chequeExpenditure + unionPayExpenditure + couponExpenditure);
      }
      this.saveRunningStatDTO(statDTO);
      return;
    }
    if (runningStatDTO.getStatDay() != null
        && runningStatDTO.getStatMonth() != null && runningStatDTO.getStatYear() != null) {
      if (runningStatDTO.getStatYear().intValue() == statDTO.getStatYear().intValue() && runningStatDTO.getStatMonth().intValue() == statDTO.getStatMonth().intValue() && runningStatDTO.getStatDay().intValue() == statDTO.getStatDay().intValue()) {
        if (isRepeal) {
//          runningStatDTO.setRunningSum(runningStatDTO.getRunningSum() - (cashIncome + chequeIncome + unionPayIncome - cashExpenditure - chequeExpenditure - unionPayExpenditure));
          runningStatDTO.setRunningSum(runningStatDTO.getRunningSum() + (cashIncome + chequeIncome + unionPayIncome + couponIncome - couponExpenditure - cashExpenditure - chequeExpenditure - unionPayExpenditure));  //add by litao
          runningStatDTO.setIncomeSum(runningStatDTO.getIncomeSum() - (cashIncome + chequeIncome + unionPayIncome + couponIncome));

          runningStatDTO.setCashIncome(runningStatDTO.getCashIncome() - cashIncome);
          runningStatDTO.setChequeIncome(runningStatDTO.getChequeIncome() - chequeIncome);
          runningStatDTO.setUnionPayIncome(runningStatDTO.getUnionPayIncome() - unionPayIncome);
          runningStatDTO.setMemberPayIncome(runningStatDTO.getMemberPayIncome() - memberPayIncome);
          runningStatDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() - debtNewIncome);
          runningStatDTO.setDebtWithdrawalIncome(runningStatDTO.getDebtWithdrawalIncome() - debtWithdrawalIncome);

          runningStatDTO.setExpenditureSum(runningStatDTO.getExpenditureSum() - (cashExpenditure + chequeExpenditure + unionPayExpenditure + couponExpenditure));
          runningStatDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() - cashExpenditure);
          runningStatDTO.setChequeExpenditure(runningStatDTO.getChequeExpenditure() - chequeExpenditure);
          runningStatDTO.setUnionPayExpenditure(runningStatDTO.getUnionPayExpenditure() - unionPayExpenditure);
          runningStatDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() - debtNewExpenditure);
          runningStatDTO.setDebtWithdrawalExpenditure(runningStatDTO.getDebtWithdrawalExpenditure() - debtWithdrawalExpenditure);
          runningStatDTO.setDepositPayExpenditure(runningStatDTO.getDepositPayExpenditure() - depositPayExpenditure);
          // add by zhuj
          runningStatDTO.setCustomerDepositExpenditure(runningStatDTO.getCustomerDepositExpenditure() - customerDepositPayExpenditure);
          runningStatDTO.setStrikeAmountExpenditure(runningStatDTO.getStrikeAmountExpenditure() - strikeAmountExpenditure);
          runningStatDTO.setCustomerDebtDiscount(runningStatDTO.getCustomerDebtDiscount() - customerDebtDiscount);
          runningStatDTO.setSupplierDebtDiscount(runningStatDTO.getSupplierDebtDiscount() - supplierDebtDiscount);
          runningStatDTO.setStrikeAmountIncome(runningStatDTO.getStrikeAmountIncome() - strikeAmountIncome);

          runningStatDTO.setCustomerReturnDebt(runningStatDTO.getCustomerReturnDebt() - customerReturnDebt);
          runningStatDTO.setSupplierReturnDebt(runningStatDTO.getSupplierReturnDebt() - supplierReturnDebt);

          runningStatDTO.setCouponIncome(runningStatDTO.getCouponIncome() - couponIncome);  //add by litao
          runningStatDTO.setCouponExpenditure(runningStatDTO.getCouponExpenditure() - couponExpenditure); //add by litao
        } else {

//          runningStatDTO.setRunningSum(runningStatDTO.getRunningSum() + (cashIncome + chequeIncome + unionPayIncome - cashExpenditure - chequeExpenditure - unionPayExpenditure));
          runningStatDTO.setRunningSum(runningStatDTO.getRunningSum() + (cashIncome + chequeIncome + unionPayIncome + couponIncome - couponExpenditure - cashExpenditure - chequeExpenditure - unionPayExpenditure));  //add by litao
          runningStatDTO.setIncomeSum(runningStatDTO.getIncomeSum() + (cashIncome + chequeIncome + unionPayIncome + couponIncome));

          runningStatDTO.setCashIncome(runningStatDTO.getCashIncome() + cashIncome);
          runningStatDTO.setChequeIncome(runningStatDTO.getChequeIncome() + chequeIncome);
          runningStatDTO.setUnionPayIncome(runningStatDTO.getUnionPayIncome() + unionPayIncome);
          runningStatDTO.setMemberPayIncome(runningStatDTO.getMemberPayIncome() + memberPayIncome);
          runningStatDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() + debtNewIncome);
          runningStatDTO.setDebtWithdrawalIncome(runningStatDTO.getDebtWithdrawalIncome() + debtWithdrawalIncome);

          runningStatDTO.setExpenditureSum(runningStatDTO.getExpenditureSum() + (cashExpenditure + chequeExpenditure + unionPayExpenditure + couponExpenditure));
          runningStatDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() + cashExpenditure);
          runningStatDTO.setChequeExpenditure(runningStatDTO.getChequeExpenditure() + chequeExpenditure);
          runningStatDTO.setUnionPayExpenditure(runningStatDTO.getUnionPayExpenditure() + unionPayExpenditure);
          runningStatDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() + debtNewExpenditure);
          runningStatDTO.setDebtWithdrawalExpenditure(runningStatDTO.getDebtWithdrawalExpenditure() + debtWithdrawalExpenditure);
          runningStatDTO.setDepositPayExpenditure(runningStatDTO.getDepositPayExpenditure() + depositPayExpenditure);
          runningStatDTO.setCustomerDepositExpenditure(runningStatDTO.getCustomerDepositExpenditure() + customerDepositPayExpenditure); // add by zhuj
          runningStatDTO.setStrikeAmountExpenditure(runningStatDTO.getStrikeAmountExpenditure()+strikeAmountExpenditure);
          runningStatDTO.setCustomerDebtDiscount(runningStatDTO.getCustomerDebtDiscount() + customerDebtDiscount);
          runningStatDTO.setSupplierDebtDiscount(runningStatDTO.getSupplierDebtDiscount() + supplierDebtDiscount);
          runningStatDTO.setStrikeAmountIncome(runningStatDTO.getStrikeAmountIncome() + strikeAmountIncome);
          runningStatDTO.setCustomerReturnDebt(runningStatDTO.getCustomerReturnDebt() + customerReturnDebt);
          runningStatDTO.setSupplierReturnDebt(runningStatDTO.getSupplierReturnDebt() + supplierReturnDebt);

          runningStatDTO.setCouponIncome(runningStatDTO.getCouponIncome() + couponIncome);  //add by litao
          runningStatDTO.setCouponExpenditure(runningStatDTO.getCouponExpenditure() + couponExpenditure); //add by litao
        }
        this.saveRunningStatDTO(runningStatDTO);
        return;
      }

      //自动补全营业数据,即补全查询到的日期 和单据当天之间的营业数据
      int endMonth = statDTO.getStatMonth().intValue();
      int endDay = statDTO.getStatDay().intValue();
      int statYear = statDTO.getStatYear().intValue();

      int startMonth = runningStatDTO.getStatMonth().intValue();
      int startDay = runningStatDTO.getStatDay().intValue();
      int startYear = runningStatDTO.getStatYear().intValue();



       if(statYear == startYear) {
         for (int monthIndex = startMonth; monthIndex <= endMonth; monthIndex++) {
           Calendar calendar = Calendar.getInstance();
           calendar.set(statYear, monthIndex - 1, 1, 0, 0, 0);
           int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

           if (endMonth == monthIndex) {
             lastDayOfCurrentMonth = endDay - 1; //统计到当前日期的前一天
           }
           int firstDayOfMonth = 1;
           if (monthIndex == startMonth) {
             firstDayOfMonth = startDay + 1;
           }

           for (int dayIndex = firstDayOfMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {

             runningStatDTO.setStatMonth((long) monthIndex);
             runningStatDTO.setStatDay((long) dayIndex);
             runningStatDTO.setStatYear((long)statYear);
             calendar.clear();
             calendar.set(statDTO.getStatYear().intValue(), monthIndex - 1, dayIndex + 1, 0, 0, 0);
             runningStatDTO.setStatDate(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
             this.saveRunningStatDTO(runningStatDTO);
           }
         }
       }else {
         for (int start = startYear; start <= statYear; start++) {
           if (start == startYear) {
             for (int monthIndex = startMonth; monthIndex <= 12; monthIndex++) {
               Calendar calendar = Calendar.getInstance();
               calendar.set(start, monthIndex - 1, 1, 0, 0, 0);
               int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

               int firstDayOfMonth = 1;
               if (monthIndex == startMonth) {
                 firstDayOfMonth = startDay + 1;
               }

               for (int dayIndex = firstDayOfMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {
                 runningStatDTO.setStatMonth((long) monthIndex);
                 runningStatDTO.setStatDay((long) dayIndex);
                 runningStatDTO.setStatYear((long) start);
                 calendar.clear();
                 calendar.set(start, monthIndex - 1, dayIndex + 1, 0, 0, 0);
                 runningStatDTO.setStatDate(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
                 this.saveRunningStatDTO(runningStatDTO);
               }
             }
             continue;
           } else if (start == statYear) {
             for (int monthIndex = 1; monthIndex <= endMonth; monthIndex++) {
               Calendar calendar = Calendar.getInstance();
               calendar.set(statYear, monthIndex - 1, 1, 0, 0, 0);
               int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

               if (endMonth == monthIndex) {
                 lastDayOfCurrentMonth = endDay - 1; //统计到当前日期的前一天
               }

               int firstDayOfMonth = 1;

               for (int dayIndex = firstDayOfMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {
                 runningStatDTO.setStatMonth((long) monthIndex);
                 runningStatDTO.setStatDay((long) dayIndex);
                 runningStatDTO.setStatYear((long) start);
                 calendar.clear();
                 calendar.set(start, monthIndex - 1, dayIndex + 1, 0, 0, 0);
                 runningStatDTO.setStatDate(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
                 this.saveRunningStatDTO(runningStatDTO);
               }
             }
           } else if (start != startYear && start != statYear) {
             for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
               Calendar calendar = Calendar.getInstance();
               calendar.set(start, monthIndex - 1, 1, 0, 0, 0);
               int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

               int firstDayOfMonth = 1;
               for (int dayIndex = firstDayOfMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {
                 runningStatDTO.setStatMonth((long) monthIndex);
                 runningStatDTO.setStatDay((long) dayIndex);
                 runningStatDTO.setStatYear((long) start);
                 calendar.clear();
                 calendar.set(start, monthIndex - 1, dayIndex + 1, 0, 0, 0);
                 runningStatDTO.setStatDate(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
                 this.saveRunningStatDTO(runningStatDTO);
               }
             }
           }
         }
       }

      if (isRepeal) {
//        statDTO.setRunningSum(runningStatDTO.getRunningSum() - (cashIncome + chequeIncome + unionPayIncome - cashExpenditure - chequeExpenditure - unionPayExpenditure));
        statDTO.setRunningSum(runningStatDTO.getRunningSum() + (cashIncome + chequeIncome + unionPayIncome + couponIncome - couponExpenditure - cashExpenditure - chequeExpenditure - unionPayExpenditure));  //add by litao
        statDTO.setIncomeSum(runningStatDTO.getIncomeSum() - (cashIncome + chequeIncome + unionPayIncome + couponIncome));

        statDTO.setCashIncome(runningStatDTO.getCashIncome() - cashIncome);
        statDTO.setChequeIncome(runningStatDTO.getChequeIncome() - chequeIncome);
        statDTO.setUnionPayIncome(runningStatDTO.getUnionPayIncome() - unionPayIncome);
        statDTO.setMemberPayIncome(runningStatDTO.getMemberPayIncome() - memberPayIncome);
        statDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() - debtNewIncome);
        statDTO.setDebtWithdrawalIncome(runningStatDTO.getDebtWithdrawalIncome() - debtWithdrawalIncome);

        statDTO.setExpenditureSum(runningStatDTO.getExpenditureSum() - (cashExpenditure + chequeExpenditure + unionPayExpenditure + couponExpenditure));
        statDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() - cashExpenditure);
        statDTO.setChequeExpenditure(runningStatDTO.getChequeExpenditure() - chequeExpenditure);
        statDTO.setUnionPayExpenditure(runningStatDTO.getUnionPayExpenditure() - unionPayExpenditure);

        statDTO.setDebtWithdrawalExpenditure(runningStatDTO.getDebtWithdrawalExpenditure() - debtWithdrawalExpenditure);
        statDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() - debtNewExpenditure);
        statDTO.setCustomerDepositExpenditure(runningStatDTO.getCustomerDepositExpenditure() - customerDepositPayExpenditure); // add by zhuj
        statDTO.setDepositPayExpenditure(runningStatDTO.getDepositPayExpenditure() - depositPayExpenditure);
        statDTO.setStrikeAmountExpenditure(runningStatDTO.getStrikeAmountExpenditure()-strikeAmountExpenditure);
        statDTO.setCustomerDebtDiscount(runningStatDTO.getCustomerDebtDiscount()  - customerDebtDiscount);
        statDTO.setSupplierDebtDiscount(runningStatDTO.getSupplierDebtDiscount() - supplierDebtDiscount);
        statDTO.setStrikeAmountIncome(runningStatDTO.getStrikeAmountIncome() - strikeAmountIncome);

        statDTO.setCouponIncome(runningStatDTO.getCouponIncome() - couponIncome);  //add by litao
        statDTO.setCouponExpenditure(runningStatDTO.getCouponExpenditure() - couponExpenditure); //add by litao
      } else {
//        statDTO.setRunningSum(runningStatDTO.getRunningSum() + (cashIncome + chequeIncome + unionPayIncome - cashExpenditure - chequeExpenditure - unionPayExpenditure));
        statDTO.setRunningSum(runningStatDTO.getRunningSum() + (cashIncome + chequeIncome + unionPayIncome + couponIncome - couponExpenditure - cashExpenditure - chequeExpenditure - unionPayExpenditure));  //add by litao
        statDTO.setIncomeSum(runningStatDTO.getIncomeSum() + (cashIncome + chequeIncome + unionPayIncome + couponIncome));

        statDTO.setCashIncome(runningStatDTO.getCashIncome() + cashIncome);
        statDTO.setChequeIncome(runningStatDTO.getChequeIncome() + chequeIncome);
        statDTO.setUnionPayIncome(runningStatDTO.getUnionPayIncome() + unionPayIncome);
        statDTO.setMemberPayIncome(runningStatDTO.getMemberPayIncome() + memberPayIncome);
        statDTO.setDebtNewIncome(runningStatDTO.getDebtNewIncome() + debtNewIncome);
        statDTO.setDebtWithdrawalIncome(runningStatDTO.getDebtWithdrawalIncome() + debtWithdrawalIncome);

        statDTO.setExpenditureSum(runningStatDTO.getExpenditureSum() + (cashExpenditure + chequeExpenditure + unionPayExpenditure + couponExpenditure));
        statDTO.setCashExpenditure(runningStatDTO.getCashExpenditure() + cashExpenditure);
        statDTO.setChequeExpenditure(runningStatDTO.getChequeExpenditure() + chequeExpenditure);
        statDTO.setUnionPayExpenditure(runningStatDTO.getUnionPayExpenditure() + unionPayExpenditure);
        statDTO.setDebtNewExpenditure(runningStatDTO.getDebtNewExpenditure() + debtNewExpenditure);
        statDTO.setDebtWithdrawalExpenditure(runningStatDTO.getDebtWithdrawalExpenditure() + debtWithdrawalExpenditure);
        statDTO.setCustomerDepositExpenditure(runningStatDTO.getCustomerDepositExpenditure() + customerDepositPayExpenditure); // add by zhuj
        statDTO.setDepositPayExpenditure(runningStatDTO.getDepositPayExpenditure() + depositPayExpenditure);
        statDTO.setStrikeAmountExpenditure(runningStatDTO.getStrikeAmountExpenditure()+strikeAmountExpenditure);
        statDTO.setCustomerDebtDiscount(runningStatDTO.getCustomerDebtDiscount() + customerDebtDiscount);
        statDTO.setSupplierDebtDiscount(runningStatDTO.getSupplierDebtDiscount() + supplierDebtDiscount);
        statDTO.setStrikeAmountIncome(runningStatDTO.getStrikeAmountIncome() + strikeAmountIncome);
        runningStatDTO.setCustomerReturnDebt(runningStatDTO.getCustomerReturnDebt() + customerReturnDebt);
        runningStatDTO.setSupplierReturnDebt(runningStatDTO.getSupplierReturnDebt() + supplierReturnDebt);

        statDTO.setCouponIncome(runningStatDTO.getCouponIncome() + couponIncome);  //add by litao
        statDTO.setCouponExpenditure(runningStatDTO.getCouponExpenditure() + couponExpenditure); //add by litao
      }
      this.saveRunningStatDTO(statDTO);
    }
  }

  /**
   * 根据供应商应付款进行流水统计
   *
   * @param depositDTO
   * @param isRepeal
   */
  public void runningStatFromDepositDTO(DepositDTO depositDTO, boolean isRepeal) {
    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(depositDTO.getShopId());
    runningStatDTO.setStatYear((long)DateUtil.getCurrentYear());
    runningStatDTO.setStatMonth((long)DateUtil.getCurrentMonth());
    runningStatDTO.setStatDay((long)DateUtil.getCurrentDay());
    runningStatDTO.setStatDate(System.currentTimeMillis());

    runningStatDTO.setCashExpenditure(NumberUtil.doubleVal(depositDTO.getCash()));
    runningStatDTO.setUnionPayExpenditure(NumberUtil.doubleVal(depositDTO.getBankCardAmount()));
    runningStatDTO.setChequeExpenditure(NumberUtil.doubleVal(depositDTO.getCheckAmount()));
    runningStatDTO.setExpenditureSum(NumberUtil.doubleVal(depositDTO.getCash()) + NumberUtil.doubleVal(depositDTO.getBankCardAmount()) + NumberUtil.doubleVal(depositDTO.getCheckAmount()));

    this.runningStat(runningStatDTO, isRepeal);
  }

  @Override
  public void runningStatFromCustomerDepositDTO(CustomerDepositDTO depositDTO, boolean isRepeal) {
    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(depositDTO.getShopId());
    runningStatDTO.setStatYear((long) DateUtil.getCurrentYear());
    runningStatDTO.setStatMonth((long) DateUtil.getCurrentMonth());
    runningStatDTO.setStatDay((long) DateUtil.getCurrentDay());
    runningStatDTO.setStatDate(System.currentTimeMillis());

    runningStatDTO.setCashIncome(depositDTO.getCash());
    runningStatDTO.setChequeIncome(depositDTO.getCheckAmount());
    runningStatDTO.setUnionPayIncome(depositDTO.getBankCardAmount());
    runningStatDTO.setIncomeSum(depositDTO.getCash() + NumberUtil.doubleVal(depositDTO.getBankCardAmount()) + NumberUtil.doubleVal(depositDTO.getCheckAmount()));
    runningStatDTO.setCustomerDepositPayIncome(depositDTO.getActuallyPaid());

    this.runningStat(runningStatDTO, isRepeal);
  }

  /**
   * 根据付款记录进行流水统计
   *
   * @param payableHistoryDTO
   * @param isRepeal
   */
  public void runningStatFromPayableHistoryDTO(PayableHistoryDTO payableHistoryDTO, boolean isRepeal) {
    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(payableHistoryDTO.getShopId());
    runningStatDTO.setStatYear((long)DateUtil.getCurrentYear());
    runningStatDTO.setStatMonth((long)DateUtil.getCurrentMonth());
    runningStatDTO.setStatDay((long)DateUtil.getCurrentDay());
    runningStatDTO.setStatDate(System.currentTimeMillis());
    runningStatDTO.setCashExpenditure(NumberUtil.doubleVal(payableHistoryDTO.getCash()));
    runningStatDTO.setUnionPayExpenditure(NumberUtil.doubleVal(payableHistoryDTO.getBankCardAmount()));
    runningStatDTO.setChequeExpenditure(NumberUtil.doubleVal(payableHistoryDTO.getCheckAmount()));
    runningStatDTO.setDepositPayExpenditure(NumberUtil.doubleVal(payableHistoryDTO.getDepositAmount()));
    runningStatDTO.setDebtWithdrawalExpenditure(runningStatDTO.getCashExpenditure() + runningStatDTO.getUnionPayExpenditure() + runningStatDTO.getChequeExpenditure() + runningStatDTO.getDepositPayExpenditure());
    runningStatDTO.setExpenditureSum(runningStatDTO.getCashExpenditure() + runningStatDTO.getUnionPayExpenditure() + runningStatDTO.getChequeExpenditure());
    runningStatDTO.setSupplierDebtDiscount(NumberUtil.doubleVal(payableHistoryDTO.getDeduction()));
    this.runningStat(runningStatDTO, isRepeal);
  }


  public RunningStatDTO getDayRunningStat(long shopId, Integer year, Integer month, Integer day) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RunningStatDTO returnRunningStatDTO = new RunningStatDTO();
    List<RunningStatDTO> todayRunningStatDTO = txnService.getRunningStatByYearMonthDay(shopId, year, month, day, 1, new Sort(" stat_date ", "desc"));
    if (CollectionUtils.isEmpty(todayRunningStatDTO)) {
      return returnRunningStatDTO;
    }
    List<RunningStatDTO> yesterdayDTO = null;
    if (day > 1) {
      yesterdayDTO = txnService.getRunningStatByYearMonthDay(shopId, year, month, day - 1, 1, new Sort(" stat_date ", "desc"));
    } else {
      Calendar currentCalendar = Calendar.getInstance();
      currentCalendar.set(year, month - 1, day);
      currentCalendar.add(Calendar.DATE, -1);
      int lastYear = currentCalendar.get(Calendar.YEAR);
      int lastMonth = currentCalendar.get(Calendar.MONTH) + 1;
      int lastDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
      yesterdayDTO = txnService.getRunningStatByYearMonthDay(shopId, lastYear, lastMonth, lastDay, 1, new Sort(" stat_date ", "desc"));
    }

    if (CollectionUtils.isEmpty(yesterdayDTO)) {
      return todayRunningStatDTO.get(0);
    }

    returnRunningStatDTO = this.minusRunningStatDate(todayRunningStatDTO.get(0), yesterdayDTO.get(0));
    return returnRunningStatDTO;
  }


  /**
   * 两个流水统计数据进行相减
   *
   * @param newRunningStatDTO
   * @param oldRunningStatDTO
   * @return
   */
  public RunningStatDTO minusRunningStatDate(RunningStatDTO newRunningStatDTO, RunningStatDTO oldRunningStatDTO) {
    if (newRunningStatDTO == null || oldRunningStatDTO == null) {
      return null;
    }
    newRunningStatDTO.setRunningSum(newRunningStatDTO.getRunningSum() - oldRunningStatDTO.getRunningSum());

    newRunningStatDTO.setIncomeSum(newRunningStatDTO.getIncomeSum() - oldRunningStatDTO.getIncomeSum());
    newRunningStatDTO.setCashIncome(newRunningStatDTO.getCashIncome() - oldRunningStatDTO.getCashIncome());
    newRunningStatDTO.setChequeIncome(newRunningStatDTO.getChequeIncome() - oldRunningStatDTO.getChequeIncome());
    newRunningStatDTO.setUnionPayIncome(newRunningStatDTO.getUnionPayIncome() - oldRunningStatDTO.getUnionPayIncome());
    newRunningStatDTO.setMemberPayIncome(newRunningStatDTO.getMemberPayIncome() - oldRunningStatDTO.getMemberPayIncome());
    newRunningStatDTO.setDebtNewIncome(newRunningStatDTO.getDebtNewIncome() - oldRunningStatDTO.getDebtNewIncome());
    newRunningStatDTO.setDebtWithdrawalIncome(newRunningStatDTO.getDebtWithdrawalIncome() - oldRunningStatDTO.getDebtWithdrawalIncome());
    newRunningStatDTO.setCustomerDepositExpenditure(newRunningStatDTO.getCustomerDepositExpenditure() - oldRunningStatDTO.getCustomerDepositExpenditure());

    newRunningStatDTO.setCouponIncome(newRunningStatDTO.getCouponIncome() - oldRunningStatDTO.getCouponIncome()); //流水统计代金券支付
    newRunningStatDTO.setCouponExpenditure(newRunningStatDTO.getCouponExpenditure() - oldRunningStatDTO.getCouponExpenditure()); //代金券支出总和

    newRunningStatDTO.setExpenditureSum(newRunningStatDTO.getExpenditureSum() - oldRunningStatDTO.getExpenditureSum());
    newRunningStatDTO.setCashExpenditure(newRunningStatDTO.getCashExpenditure() - oldRunningStatDTO.getCashExpenditure());
    newRunningStatDTO.setChequeExpenditure(newRunningStatDTO.getChequeExpenditure() - oldRunningStatDTO.getChequeExpenditure());
    newRunningStatDTO.setUnionPayExpenditure(newRunningStatDTO.getUnionPayExpenditure() - oldRunningStatDTO.getUnionPayExpenditure());
    newRunningStatDTO.setDebtNewExpenditure(newRunningStatDTO.getDebtNewExpenditure() - oldRunningStatDTO.getDebtNewExpenditure());
    newRunningStatDTO.setDebtWithdrawalExpenditure(newRunningStatDTO.getDebtWithdrawalExpenditure() - oldRunningStatDTO.getDebtWithdrawalExpenditure());
    newRunningStatDTO.setDepositPayExpenditure(newRunningStatDTO.getDepositPayExpenditure() - oldRunningStatDTO.getDepositPayExpenditure());
    newRunningStatDTO.setStrikeAmountExpenditure(newRunningStatDTO.getStrikeAmountExpenditure() - oldRunningStatDTO.getStrikeAmountExpenditure());
    newRunningStatDTO.setCustomerDebtDiscount(newRunningStatDTO.getCustomerDebtDiscount() - oldRunningStatDTO.getCustomerDebtDiscount());
    newRunningStatDTO.setSupplierDebtDiscount(newRunningStatDTO.getSupplierDebtDiscount() - oldRunningStatDTO.getSupplierDebtDiscount());
    newRunningStatDTO.setStrikeAmountIncome(NumberUtil.doubleVal(newRunningStatDTO.getStrikeAmountIncome() - oldRunningStatDTO.getStrikeAmountIncome()));
    return newRunningStatDTO;
  }

  /**
   * 两个流水统计数据进行相加
   *
   * @param newRunningStatDTO
   * @param oldRunningStatDTO
   * @return
   */
  public RunningStatDTO addRunningStatDate(RunningStatDTO newRunningStatDTO, RunningStatDTO oldRunningStatDTO) {
    if (newRunningStatDTO == null || oldRunningStatDTO == null) {
      return null;
    }

    newRunningStatDTO.setRunningSum(newRunningStatDTO.getRunningSum() + oldRunningStatDTO.getRunningSum());

    newRunningStatDTO.setIncomeSum(newRunningStatDTO.getIncomeSum() + oldRunningStatDTO.getIncomeSum());
    newRunningStatDTO.setCashIncome(newRunningStatDTO.getCashIncome() + oldRunningStatDTO.getCashIncome());
    newRunningStatDTO.setChequeIncome(newRunningStatDTO.getChequeIncome() + oldRunningStatDTO.getChequeIncome());
    newRunningStatDTO.setUnionPayIncome(newRunningStatDTO.getUnionPayIncome() + oldRunningStatDTO.getUnionPayIncome());
    newRunningStatDTO.setMemberPayIncome(newRunningStatDTO.getMemberPayIncome() + oldRunningStatDTO.getMemberPayIncome());
    newRunningStatDTO.setDebtNewIncome(newRunningStatDTO.getDebtNewIncome() + oldRunningStatDTO.getDebtNewIncome());
    newRunningStatDTO.setDebtWithdrawalIncome(newRunningStatDTO.getDebtWithdrawalIncome() + oldRunningStatDTO.getDebtWithdrawalIncome());
    newRunningStatDTO.setDepositPayIncome(newRunningStatDTO.getDepositPayIncome() + oldRunningStatDTO.getDepositPayIncome());
    newRunningStatDTO.setCustomerDepositExpenditure(newRunningStatDTO.getCustomerDepositExpenditure() + oldRunningStatDTO.getCustomerDepositExpenditure());

    newRunningStatDTO.setCouponIncome(newRunningStatDTO.getCouponIncome()+oldRunningStatDTO.getCouponIncome()); //流水统计代金券支付
    newRunningStatDTO.setCouponExpenditure(newRunningStatDTO.getCouponExpenditure()+oldRunningStatDTO.getCouponExpenditure()); //代金券支出总和

    newRunningStatDTO.setExpenditureSum(newRunningStatDTO.getExpenditureSum() + oldRunningStatDTO.getExpenditureSum());
    newRunningStatDTO.setCashExpenditure(newRunningStatDTO.getCashExpenditure() + oldRunningStatDTO.getCashExpenditure());
    newRunningStatDTO.setChequeExpenditure(newRunningStatDTO.getChequeExpenditure() + oldRunningStatDTO.getChequeExpenditure());
    newRunningStatDTO.setUnionPayExpenditure(newRunningStatDTO.getUnionPayExpenditure() + oldRunningStatDTO.getUnionPayExpenditure());
    newRunningStatDTO.setDebtNewExpenditure(newRunningStatDTO.getDebtNewExpenditure() + oldRunningStatDTO.getDebtNewExpenditure());
    newRunningStatDTO.setDebtWithdrawalExpenditure(newRunningStatDTO.getDebtWithdrawalExpenditure() + oldRunningStatDTO.getDebtWithdrawalExpenditure());
    newRunningStatDTO.setDepositPayExpenditure(newRunningStatDTO.getDepositPayExpenditure() + oldRunningStatDTO.getDepositPayExpenditure());
    newRunningStatDTO.setStrikeAmountExpenditure(newRunningStatDTO.getStrikeAmountExpenditure()+oldRunningStatDTO.getStrikeAmountExpenditure());
    newRunningStatDTO.setCustomerDebtDiscount(newRunningStatDTO.getCustomerDebtDiscount() + oldRunningStatDTO.getCustomerDebtDiscount());
    newRunningStatDTO.setSupplierDebtDiscount(newRunningStatDTO.getSupplierDebtDiscount() + oldRunningStatDTO.getSupplierDebtDiscount());
    newRunningStatDTO.setStrikeAmountIncome(newRunningStatDTO.getStrikeAmountIncome() + oldRunningStatDTO.getStrikeAmountIncome());
    return newRunningStatDTO;
  }


  /**
   * 查询所有店铺下的定金
   *
   * @param shopId
   * @param
   * @return
   */
  public List<DepositDTO> getDepositDTOListBySHopId(long shopId,long startTime,long endTime) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<DepositDTO> depositDTOList = new ArrayList<DepositDTO>();
    List<Deposit> depositList = txnWriter.getDepositDTOListBySHopId(shopId,startTime,endTime);
    if (CollectionUtils.isEmpty(depositList)) {
      return null;
    }

    for (Deposit deposit : depositList) {
      DepositDTO depositDTO = deposit.toDTO();
      depositDTOList.add(depositDTO);
    }
    return depositDTOList;
  }

  public String initReceivable(List<ReceivableDTO> receivableDTOList) {
    StringBuffer stringBuffer = new StringBuffer();
    TxnWriter writer = txnDaoManager.getWriter();

    if (CollectionUtils.isEmpty(receivableDTOList)) {
      return stringBuffer.toString();
    }
    List<Receivable> receivableList = new ArrayList<Receivable>();

    for (ReceivableDTO receivableDTO : receivableDTOList) {
      if (receivableDTO == null || receivableDTO.getId() == null || receivableDTO.getOrderId() == null) {
        continue;
      }
      Receivable receivable = writer.getById(Receivable.class, receivableDTO.getId());

      Long orderId = receivable.getOrderId();

      boolean isSetting = false;
      int orderNumByOrderId = 0;

      WashOrder washOrder = writer.getById(WashOrder.class, orderId);
      if (washOrder != null) {
        receivable.setOrderTypeEnum(OrderTypes.WASH);
        receivable.setStatusEnum(ReceivableStatus.FINISH);
        isSetting = true;
        orderNumByOrderId++;
      }

      if (!isSetting) {
        SalesOrder salesOrder = writer.getById(SalesOrder.class, orderId);
        if (salesOrder != null) {
          isSetting = true;
          orderNumByOrderId++;

          receivable.setOrderTypeEnum(OrderTypes.SALE);
          if (OrderStatus.SALE_DONE.equals(salesOrder.getStatusEnum()) || OrderStatus.SALE_DEBT_DONE.equals(salesOrder.getStatusEnum())) {
            receivable.setStatusEnum(ReceivableStatus.FINISH);
          } else {
            receivable.setStatusEnum(ReceivableStatus.REPEAL);
          }
        }
      }
      if (!isSetting) {
        RepairOrder repairOrder = writer.getById(RepairOrder.class, orderId);
        if (repairOrder != null) {
          isSetting = true;
          orderNumByOrderId++;

          receivable.setOrderTypeEnum(OrderTypes.REPAIR);

          if (OrderStatus.REPAIR_REPEAL == repairOrder.getStatusEnum()) {
            receivable.setStatusEnum(ReceivableStatus.REPEAL);
          } else {
            receivable.setStatusEnum(ReceivableStatus.FINISH);
          }
        }
      }

      if (!isSetting) {
        MemberCardOrder memberCardOrder = writer.getById(MemberCardOrder.class, orderId);
        if (memberCardOrder != null) {
          isSetting = true;
          orderNumByOrderId++;
          receivable.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
          receivable.setStatusEnum(ReceivableStatus.FINISH);
        }
      }

      if (!isSetting) {
        WashBeautyOrder washBeautyOrder = writer.getById(WashBeautyOrder.class, orderId);
        if (washBeautyOrder != null) {
          isSetting = true;
          orderNumByOrderId++;
          receivable.setOrderTypeEnum(OrderTypes.WASH_BEAUTY);
          receivable.setStatusEnum(ReceivableStatus.FINISH);
        }
      }

      if (orderNumByOrderId > 1 || orderNumByOrderId == 0) {
        LOG.error("TxnService.java");
        LOG.error("method=updateReceivable");
        LOG.error("shopId:" + receivable.getShopId() + ",OrderId:" + orderId + "orderId在order表中有" + String.valueOf(orderNumByOrderId) + "条记录");
        stringBuffer.append("shopId:" + receivable.getShopId() + ",OrderId:" + orderId + "orderId在order表中有" + String.valueOf(orderNumByOrderId) + "条记录");
      }
      receivable.setDiscount(receivable.getDiscount() == null ? 0 : receivable.getDiscount());
      receivable.setTotal(receivable.getTotal() == null ? 0 : receivable.getTotal());


      if (NumberUtil.toReserve(NumberUtil.doubleVal(receivable.getCash()) + NumberUtil.doubleVal(receivable.getBankCard()) +
          NumberUtil.doubleVal(receivable.getCheque()) + NumberUtil.doubleVal(receivable.getMemberBalancePay()), NumberUtil.MONEY_PRECISION) <= 0) {
        receivable.setCash(receivable.getSettledAmount());
      }

//      if (receivable.getCash() == null && receivable.getBankCard() == null && receivable.getCheque() == null && receivable.getMemberBalancePay() == null) {
//        receivable.setCash(receivable.getSettledAmount());
//      }
      receivable.setAccumulatePointsPay(0d);
      double total = receivable.getSettledAmount() + receivable.getDebt() + receivable.getDiscount();
      receivable.setTotal(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION));
      receivableList.add(receivable);
    }

    Object status = writer.begin();
    try {
      for (Receivable receivable : receivableList) {
        writer.saveOrUpdate(receivable);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return stringBuffer.toString();
  }

  public void saveRunningStatDTOList(List<RunningStatDTO> runningStatDTOList) {
    if (CollectionUtils.isEmpty(runningStatDTOList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (int index = 0; index < runningStatDTOList.size(); index++) {
        RunningStatDTO runningStatDTO = runningStatDTOList.get(index);
        if (index != 0) {
          RunningStatDTO yesterdayDTO = runningStatDTOList.get(index - 1);
          runningStatDTO = this.addRunningStatDate(runningStatDTO, yesterdayDTO);
        }
        RunningStat runningStat = null;

        if (runningStatDTO.getShopId() != null && runningStatDTO.getStatYear() != null && runningStatDTO.getStatMonth() != null && runningStatDTO.getStatDay() != null) {
          RunningStatDTO todayRunningStatDTO = this.getRunningStatDTOByShopIdYearMonthDay(runningStatDTO.getShopId(),
              runningStatDTO.getStatYear(), runningStatDTO.getStatMonth(), runningStatDTO.getStatDay());
          if (todayRunningStatDTO != null) {
            runningStat = writer.getById(RunningStat.class, todayRunningStatDTO.getId());
            runningStatDTO.setStrikeAmountExpenditure(runningStat.getStrikeAmountExpenditure());
            runningStat = runningStat.fromDTO(runningStatDTO, false);
            writer.saveOrUpdate(runningStat);
          } else {
            runningStat = new RunningStat();
            runningStat = runningStat.fromDTO(runningStatDTO, false);
            writer.save(runningStat);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 流水数据初始化:保存收入记录
   * @param payableHistoryRecordDTOList
   */
  public void saveOrUpdatePayRecordList(List<PayableHistoryRecordDTO> payableHistoryRecordDTOList) throws ParseException {
    if (CollectionUtils.isEmpty(payableHistoryRecordDTOList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
        if (payableHistoryRecordDTO.getId() != null) {
          PayableHistoryRecord payableHistoryRecord = writer.getById(PayableHistoryRecord.class, payableHistoryRecordDTO.getId());
          payableHistoryRecord = payableHistoryRecord.fromDTO(payableHistoryRecordDTO, false);
          writer.update(payableHistoryRecord);
        } else {
          PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord(payableHistoryRecordDTO);
          writer.save(payableHistoryRecord);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  /**
   * 批量保存收款记录
   * @param receptionRecordDTOList
   */
  public void saveOrUpdateReceptionRecordList(List<ReceptionRecordDTO> receptionRecordDTOList) {
    if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {

        if (receptionRecordDTO.getId() != null) {
          ReceptionRecord receptionRecord = writer.getById(ReceptionRecord.class, receptionRecordDTO.getId());
          receptionRecord.fromDTO(receptionRecordDTO);
          writer.saveOrUpdate(receptionRecord);
        } else {
          ReceptionRecord receptionRecord = new ReceptionRecord();
          receptionRecord.fromDTO(receptionRecordDTO);
          writer.save(receptionRecord);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据年月日获得流水修改记录
   *
   * @param shopId
   * @param year
   * @param month
   * @param day
   * @return
   */
  public RunningStatDTO getRunningStatChangeDTOByShopIdYearMonthDay(Long shopId, Long year, long month, long day) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<RunningStatChange> runningStatChangeList = txnWriter.getRunningStatChangeDTOByShopIdYearMonthDay(shopId, year, month, day);
    if (!CollectionUtils.isEmpty(runningStatChangeList) && runningStatChangeList.size() > 1) {
      LOG.error("/RunningService");
      LOG.error("/method=getRunningStatChangeDTOByShopIdYearMonthDay");
      LOG.error("流水记录有一条以上");
      LOG.error("参数:" + "shopId:" + shopId + "year:" + year + "month:" + month + "day:" + day);
    }
    return CollectionUtils.isEmpty(runningStatChangeList) ? null : runningStatChangeList.get(0).toDTO();
  }


  /**
   * 保存流水更改记录
   *
   * @param runningStatDTO
   * @return
   */
  public RunningStatDTO saveRunningStatChangeDTO(RunningStatDTO runningStatDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      if (runningStatDTO == null) {
        return null;
      }

      RunningStatChange runningStatChange = null;
      if (runningStatDTO.getId() != null) {
        runningStatChange = txnWriter.getById(RunningStatChange.class, runningStatDTO.getId());
      } else {
        runningStatChange = new RunningStatChange();
      }
      runningStatChange = runningStatChange.fromDTO(runningStatDTO, true);
      txnWriter.saveOrUpdate(runningStatChange);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    return runningStatDTO;

  }

  /**
   * 统计该年月下流水变更记录总和
   *
   * @param shopId
   * @param year
   * @param month
   * @return
   */
  @Override
  public List<RunningStatDTO> getRunningStatChangeByYearMonth(Long shopId, long year, long month) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RunningStatChange> runningStatChangeList = writer.getRunningStatChangeByYearMonth(shopId, year, month);
    if (CollectionUtils.isEmpty(runningStatChangeList)) {
      return null;
    }
    List<RunningStatDTO> runningStatDTOList = new ArrayList<RunningStatDTO>();
    for (RunningStatChange runningStatChange : runningStatChangeList) {
      runningStatDTOList.add(runningStatChange.toDTO());
    }
    return runningStatDTOList;
  }



  @Override
  public RunningStatDTO sumRunningStatChangeForYearMonth(Long shopId, Long year,Long month) {
    RunningStatDTO resultDTO = new RunningStatDTO();
    TxnWriter writer = txnDaoManager.getWriter();
    List<RunningStatChange> runningStatDTOs = writer.sumRunningStatChangeForYearMonth(shopId, year, month);

    if (CollectionUtils.isEmpty(runningStatDTOs)) {
      return resultDTO;
    }

    for (RunningStatChange runningStatChange : runningStatDTOs) {
      resultDTO = this.addRunningStatDate(resultDTO, runningStatChange.toDTO());
    }

    return resultDTO;
  }


  @Override
  public Map<Long, RunningStatDTO> getDayRunningStatChangeMap(Long shopId, Long year, Long month) {
    Map<Long, RunningStatDTO> map = new HashMap<Long, RunningStatDTO>();

    TxnWriter writer = txnDaoManager.getWriter();

    List<RunningStatChange> runningStatChangeList = writer.getRunningStatChangeByYearMonth(shopId, year, month);

    if (CollectionUtils.isEmpty(runningStatChangeList)) {
      return null;
    }

    for (RunningStatChange runningStatChange : runningStatChangeList) {
      map.put(runningStatChange.getStatDay(), runningStatChange.toDTO());
    }
    return map;
  }

  @Override
  public Map<Long,RunningStatDTO> getMonthRunningStatChangeMap(Long shopId, long year) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getMonthRunningStatChangeMap(shopId, year);
  }

  /**
   * 根据流水统计信息进行流水统计
   * @param runningStatDTO
   */
  public void saveRunningStatChangeFromDTO(RunningStatDTO runningStatDTO){

    try {
      if(runningStatDTO == null){
        throw new Exception("统计信息为空");
      }

      RunningStatDTO dayRunningStatDTO = this.getRunningStatChangeDTOByShopIdYearMonthDay(runningStatDTO.getShopId(),
          runningStatDTO.getStatYear(), runningStatDTO.getStatMonth(), runningStatDTO.getStatDay());
      if(dayRunningStatDTO == null){
        dayRunningStatDTO = new RunningStatDTO();
        dayRunningStatDTO.setShopId(runningStatDTO.getShopId());
        dayRunningStatDTO.setStatYear(runningStatDTO.getStatYear());
        dayRunningStatDTO.setStatMonth(runningStatDTO.getStatMonth());
        dayRunningStatDTO.setStatDay(runningStatDTO.getStatDay());
        dayRunningStatDTO.setStatDate(runningStatDTO.getStatDate());
      }

      dayRunningStatDTO = this.addRunningStatDate(dayRunningStatDTO,runningStatDTO);
      this.saveRunningStatChangeDTO(dayRunningStatDTO);
    } catch (Exception e) {
      LOG.error("RunningStatService.java method=saveRunningStatChangeFromDTO");
      LOG.error("营业统计保存到runningStatChange出错，statDTO:" + runningStatDTO.toString());
    }
  }


  /**
   * 根据付款类型查询付款记录
   * @param paymentTypes
   * @return
   */
  public List<PayableHistoryRecordDTO>  getPayableHistoryRecordByPaymentType(PaymentTypes paymentTypes,Long shopId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<PayableHistoryRecord> payableHistoryRecordList = txnWriter.getPayableHistoryRecordByPaymentType(paymentTypes,shopId);
    if (CollectionUtils.isEmpty(payableHistoryRecordList)) {
      return null;
    }
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = new ArrayList<PayableHistoryRecordDTO>();
    for (PayableHistoryRecord payableHistoryRecord : payableHistoryRecordList) {
      payableHistoryRecordDTOList.add(payableHistoryRecord.toDTO());
    }
    return payableHistoryRecordDTOList;

  }


  /**
   * 获取所有入库退货单
   * @return
   */
  public List<PurchaseReturnDTO> getPurchaseReturn(Long shopId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<PurchaseReturn> purchaseReturnList = txnWriter.getPurchaseReturn(shopId);
    if (CollectionUtils.isEmpty(purchaseReturnList)) {
      return null;
    }
    List<PurchaseReturnDTO> purchaseReturnDTOList = new ArrayList<PurchaseReturnDTO>();
    for (PurchaseReturn purchaseReturn : purchaseReturnList) {
      purchaseReturnDTOList.add(purchaseReturn.toDTO());
    }
    return purchaseReturnDTOList;
  }

  public void deletePayHistoryRecord(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deletePayHistoryRecord(shopId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public Double getTotalDebtByShopId(Long shopId,OrderDebtType type){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getTotalDebtByShopId(shopId,type);
  }


  public List<RunningStatDTO> getYearRunningStat(Long shopId) {
    List<RunningStatDTO> resultList = new ArrayList<RunningStatDTO>();

    int currentYear = DateUtil.getCurrentYear();
    for (int index = 2010; index < 2020; index++) {
      RunningStatDTO runningStatDTO = new RunningStatDTO();
      runningStatDTO.setStatYear((long) index);
      if (index >= 2012 && index <= currentYear) {
        runningStatDTO = this.getYearRunningStatByYear(shopId, (long) index);
      } else {
        runningStatDTO.setRunningSum(0D);
      }
      resultList.add(runningStatDTO);
    }
    return resultList;
  }


  public RunningStatDTO getYearRunningStatByYear(Long shopId,Long year) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(shopId);
    runningStatDTO.setStatYear(year.longValue());
    runningStatDTO.setStatMonth((long) -1);
    runningStatDTO.setStatDay((long) -1);


    List<RunningStatDTO> thisYearStatList = txnService.getRunningStatByYearMonthDay(shopId, year.intValue(), null, null, 1, new Sort(" stat_date ", "desc"));
    if (CollectionUtils.isNotEmpty(thisYearStatList)) {
      runningStatDTO = thisYearStatList.get(0);

      List<RunningStatDTO> lastYearList = txnService.getRunningStatByYearMonthDay(shopId, year.intValue() - 1, null, null, 1, new Sort(" stat_date ", "desc"));
      if (CollectionUtils.isNotEmpty(lastYearList)) {
        runningStatDTO = this.minusRunningStatDate(runningStatDTO, lastYearList.get(0));
      }
    }

    RunningStatDTO runningStatChangeDTO = this.sumRunningStatChangeForYearMonth(shopId, year, null);
    if (runningStatChangeDTO != null) {
      runningStatChangeDTO.setIncomeSum(runningStatChangeDTO.getCashIncome() + runningStatChangeDTO.getUnionPayIncome() + runningStatChangeDTO.getChequeIncome() + runningStatChangeDTO.getCouponIncome());
      runningStatChangeDTO.setExpenditureSum(runningStatChangeDTO.getCashExpenditure() + runningStatChangeDTO.getUnionPayExpenditure() + runningStatChangeDTO.getChequeExpenditure() + runningStatChangeDTO.getCouponExpenditure());
      runningStatChangeDTO.setRunningSum(runningStatChangeDTO.getIncomeSum() - runningStatChangeDTO.getExpenditureSum());
      runningStatDTO = this.addRunningStatDate(runningStatDTO, runningStatChangeDTO);
    }
    return runningStatDTO;
  }


  public Double getSupplierTotalDebtByShopId(Long shopId,OrderDebtType type) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSupplierTotalDebtByShopId(shopId, type);
  }

  @Autowired
  private TxnDaoManager txnDaoManager;
}
