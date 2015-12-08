package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.BusinessAccountConstant;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-28
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BusinessStatService implements IBusinessStatService {
  public static final Logger LOG = LoggerFactory.getLogger(BusinessStatService.class);
  /**
   * 营业统计
   * @param statDTO 营业统计信息
   * @param isRepeal 是否作废
   */
  public void businessStat(BusinessStatDTO statDTO, boolean isRepeal,Long vestDate) {
    try {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
      //作废 扣除当天的营业额
      if (isRepeal) {
        if (!DateUtil.isCurrentTime(vestDate)) {
          statDTO.setStatYear(DateUtil.getYearByVestDate(vestDate));
          statDTO.setStatMonth(DateUtil.getMonthByVestDate(vestDate));
          statDTO.setStatDay(DateUtil.getDayByVestDate(vestDate));
          statDTO.setStatTime(vestDate);

          statDTO.setSales(0 - statDTO.getSales() );
          statDTO.setService(0 - statDTO.getService());
          statDTO.setWash(0 - statDTO.getWash());
          statDTO.setStatSum(0 - statDTO.getStatSum());
          statDTO.setProductCost(0 - statDTO.getProductCost());
          statDTO.setMemberIncome(0 - statDTO.getMemberIncome());
          statDTO.setOtherIncome(0 - statDTO.getOtherIncome());
          statDTO.setOrderOtherIncomeCost(0 - statDTO.getOrderOtherIncomeCost());
          this.saveBusinessStatChangeFromDTO(statDTO);
          return;
        }
      }

    List<BusinessStatDTO> todayList = txnService.getBusinessStatByYearMonthDay(statDTO.getShopId(),statDTO.getStatYear(),statDTO.getStatMonth(),statDTO.getStatDay());
    if(CollectionUtils.isNotEmpty(todayList)) {
      BusinessStatDTO todayDTO = todayList.get(0);
      todayDTO = this.calculateBusinessStat(todayDTO, statDTO, isRepeal);
      txnService.updateBusinessStat(todayDTO);
      return;
    }


    List<BusinessStatDTO> businessStatDTOList = txnService.getLatestBusinessStat(statDTO.getShopId(), null, 1);
    if (CollectionUtils.isEmpty(businessStatDTOList)) {
      if (isRepeal) {
        statDTO.setSales(0.0 - statDTO.getSales());
        statDTO.setService(0.0 - statDTO.getService());
        statDTO.setWash(0.0 - statDTO.getWash());
        statDTO.setStatSum(0.0 - statDTO.getSales() - statDTO.getService() - statDTO.getWash());
        statDTO.setProductCost(0.0 - statDTO.getProductCost());
        statDTO.setOrderOtherIncomeCost(0 - statDTO.getOrderOtherIncomeCost());
        statDTO.setMemberIncome(0);
        statDTO.setOtherIncome(0);
        statDTO.setStatTime(System.currentTimeMillis());
      }
      txnService.saveBusinessStat(statDTO);
      return;
    }
    BusinessStatDTO businessStatDTO = businessStatDTOList.get(0);
    if (businessStatDTO != null && businessStatDTO.getStatDay() != null
        && businessStatDTO.getStatMonth() != null && businessStatDTO.getStatYear() != null) {
      if (businessStatDTO.getStatYear().intValue() == statDTO.getStatYear().intValue() && businessStatDTO.getStatMonth().intValue() == statDTO.getStatMonth().intValue() && businessStatDTO.getStatDay().intValue() == statDTO.getStatDay().intValue()) {

        businessStatDTO = this.calculateBusinessStat(businessStatDTO, statDTO, isRepeal);
        txnService.updateBusinessStat(businessStatDTO);
        return;
      }
      //自动补全营业数据,即补全查询到的日期 和单据当天之间的营业数据
      int endMonth = statDTO.getStatMonth().intValue();
      int endDay = statDTO.getStatDay().intValue();
      int statYear = statDTO.getStatYear().intValue();

      int startMonth = businessStatDTO.getStatMonth().intValue();
      int startDay = businessStatDTO.getStatDay().intValue();
      int startYear = businessStatDTO.getStatYear().intValue();

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
            BusinessStatDTO newBusinessStatDTO = new BusinessStatDTO();
            newBusinessStatDTO.setShopId(statDTO.getShopId());
            newBusinessStatDTO.setStatYear(statDTO.getStatYear());
            newBusinessStatDTO.setStatMonth((long) monthIndex);
            newBusinessStatDTO.setStatDay((long) dayIndex);
            newBusinessStatDTO.setSales(businessStatDTO.getSales());
            newBusinessStatDTO.setService(businessStatDTO.getService());
            newBusinessStatDTO.setWash(businessStatDTO.getWash());
            newBusinessStatDTO.setStatSum(businessStatDTO.getStatSum());
            newBusinessStatDTO.setProductCost(businessStatDTO.getProductCost());
            newBusinessStatDTO.setMemberIncome(businessStatDTO.getMemberIncome());
            newBusinessStatDTO.setOtherIncome(businessStatDTO.getOtherIncome());
            newBusinessStatDTO.setRentExpenditure(businessStatDTO.getRentExpenditure());
            newBusinessStatDTO.setUtilitiesExpenditure(businessStatDTO.getUtilitiesExpenditure());
            newBusinessStatDTO.setSalaryExpenditure(businessStatDTO.getSalaryExpenditure());
            newBusinessStatDTO.setOtherExpenditure(businessStatDTO.getOtherExpenditure());
            newBusinessStatDTO.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost());
            //自动补全数据 statTime 设为当天的时间
            calendar.clear();
            calendar.set(statDTO.getStatYear().intValue(), monthIndex - 1, dayIndex + 1, 0, 0, 0);
            newBusinessStatDTO.setStatTime(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
            txnService.saveBusinessStat(newBusinessStatDTO);
          }
        }
      }else {
        for (int start = startYear; start <= statYear; start++) {
          if (start == startYear) {
            for (int monthIndex = startMonth; monthIndex <= 12; monthIndex++) {
              Calendar calendar = Calendar.getInstance();
              calendar.set(statYear, monthIndex - 1, 1, 0, 0, 0);
              int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

              int firstDayOfMonth = 1;
              if (monthIndex == startMonth) {
                firstDayOfMonth = startDay + 1;
              }

              for (int dayIndex = firstDayOfMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {
                BusinessStatDTO newBusinessStatDTO = new BusinessStatDTO();
                newBusinessStatDTO.setShopId(statDTO.getShopId());
                newBusinessStatDTO.setStatYear((long) start);
                newBusinessStatDTO.setStatMonth((long) monthIndex);
                newBusinessStatDTO.setStatDay((long) dayIndex);
                newBusinessStatDTO.setSales(businessStatDTO.getSales());
                newBusinessStatDTO.setService(businessStatDTO.getService());
                newBusinessStatDTO.setWash(businessStatDTO.getWash());
                newBusinessStatDTO.setStatSum(businessStatDTO.getStatSum());
                newBusinessStatDTO.setProductCost(businessStatDTO.getProductCost());
                newBusinessStatDTO.setMemberIncome(businessStatDTO.getMemberIncome());
                newBusinessStatDTO.setOtherIncome(businessStatDTO.getOtherIncome());
                newBusinessStatDTO.setRentExpenditure(businessStatDTO.getRentExpenditure());
                newBusinessStatDTO.setUtilitiesExpenditure(businessStatDTO.getUtilitiesExpenditure());
                newBusinessStatDTO.setSalaryExpenditure(businessStatDTO.getSalaryExpenditure());
                newBusinessStatDTO.setOtherExpenditure(businessStatDTO.getOtherExpenditure());
                newBusinessStatDTO.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost());
                //自动补全数据 statTime 设为当天的时间
                calendar.clear();
                calendar.set(start, monthIndex - 1, dayIndex + 1, 0, 0, 0);
                newBusinessStatDTO.setStatTime(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
                txnService.saveBusinessStat(newBusinessStatDTO);
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
                BusinessStatDTO newBusinessStatDTO = new BusinessStatDTO();
                newBusinessStatDTO.setShopId(statDTO.getShopId());
                newBusinessStatDTO.setStatYear((long) start);
                newBusinessStatDTO.setStatMonth((long) monthIndex);
                newBusinessStatDTO.setStatDay((long) dayIndex);
                newBusinessStatDTO.setSales(businessStatDTO.getSales());
                newBusinessStatDTO.setService(businessStatDTO.getService());
                newBusinessStatDTO.setWash(businessStatDTO.getWash());
                newBusinessStatDTO.setStatSum(businessStatDTO.getStatSum());
                newBusinessStatDTO.setProductCost(businessStatDTO.getProductCost());
                newBusinessStatDTO.setMemberIncome(businessStatDTO.getMemberIncome());
                newBusinessStatDTO.setOtherIncome(businessStatDTO.getOtherIncome());
                newBusinessStatDTO.setRentExpenditure(businessStatDTO.getRentExpenditure());
                newBusinessStatDTO.setUtilitiesExpenditure(businessStatDTO.getUtilitiesExpenditure());
                newBusinessStatDTO.setSalaryExpenditure(businessStatDTO.getSalaryExpenditure());
                newBusinessStatDTO.setOtherExpenditure(businessStatDTO.getOtherExpenditure());
                newBusinessStatDTO.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost());
                //自动补全数据 statTime 设为当天的时间
                calendar.clear();
                calendar.set(start, monthIndex - 1, dayIndex + 1, 0, 0, 0);
                newBusinessStatDTO.setStatTime(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
                txnService.saveBusinessStat(newBusinessStatDTO);
              }
            }
          } else if (start != startYear && start != statYear) {
            for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
              Calendar calendar = Calendar.getInstance();
              calendar.set(statYear, monthIndex - 1, 1, 0, 0, 0);
              int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

              int firstDayOfMonth = 1;
              for (int dayIndex = firstDayOfMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {
                BusinessStatDTO newBusinessStatDTO = new BusinessStatDTO();
                newBusinessStatDTO.setShopId(statDTO.getShopId());
                newBusinessStatDTO.setStatYear((long) start);
                newBusinessStatDTO.setStatMonth((long) monthIndex);
                newBusinessStatDTO.setStatDay((long) dayIndex);
                newBusinessStatDTO.setSales(businessStatDTO.getSales());
                newBusinessStatDTO.setService(businessStatDTO.getService());
                newBusinessStatDTO.setWash(businessStatDTO.getWash());
                newBusinessStatDTO.setStatSum(businessStatDTO.getStatSum());
                newBusinessStatDTO.setProductCost(businessStatDTO.getProductCost());
                newBusinessStatDTO.setMemberIncome(businessStatDTO.getMemberIncome());
                newBusinessStatDTO.setOtherIncome(businessStatDTO.getOtherIncome());
                newBusinessStatDTO.setRentExpenditure(businessStatDTO.getRentExpenditure());
                newBusinessStatDTO.setUtilitiesExpenditure(businessStatDTO.getUtilitiesExpenditure());
                newBusinessStatDTO.setSalaryExpenditure(businessStatDTO.getSalaryExpenditure());
                newBusinessStatDTO.setOtherExpenditure(businessStatDTO.getOtherExpenditure());
                newBusinessStatDTO.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost());
                //自动补全数据 statTime 设为当天的时间
                calendar.clear();
                calendar.set(start, monthIndex - 1, dayIndex + 1, 0, 0, 0);
                newBusinessStatDTO.setStatTime(calendar.getTimeInMillis() - 10000); //设置统计时间统一为每天23:59:50
                txnService.saveBusinessStat(newBusinessStatDTO);
              }
            }
          }
        }
      }
      BusinessStatDTO newBusinessStatDTO = this.calculateBusinessStat(businessStatDTO, statDTO, isRepeal);
      newBusinessStatDTO.setShopId(statDTO.getShopId());
      newBusinessStatDTO.setStatYear(statDTO.getStatYear());
      newBusinessStatDTO.setStatMonth(statDTO.getStatMonth());
      newBusinessStatDTO.setStatDay(statDTO.getStatDay());
      newBusinessStatDTO.setStatTime(statDTO.getStatTime());
      txnService.saveBusinessStat(newBusinessStatDTO);
    }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
  }
  }


  public BusinessStatDTO calculateBusinessStat(BusinessStatDTO newBusinessStatDTO, BusinessStatDTO oldBusinessStatDTO, boolean isRepeal) {
    if (isRepeal) {
      newBusinessStatDTO.setSales(newBusinessStatDTO.getSales() - oldBusinessStatDTO.getSales());
      newBusinessStatDTO.setService(newBusinessStatDTO.getService() - oldBusinessStatDTO.getService());
      newBusinessStatDTO.setWash(newBusinessStatDTO.getWash() - oldBusinessStatDTO.getWash());
      newBusinessStatDTO.setStatSum(newBusinessStatDTO.getStatSum() - oldBusinessStatDTO.getSales() - oldBusinessStatDTO.getService() - oldBusinessStatDTO.getWash());
      newBusinessStatDTO.setProductCost(newBusinessStatDTO.getProductCost() - oldBusinessStatDTO.getProductCost());
      newBusinessStatDTO.setOrderOtherIncomeCost(newBusinessStatDTO.getOrderOtherIncomeCost() - oldBusinessStatDTO.getOrderOtherIncomeCost());
      newBusinessStatDTO.setMemberIncome(newBusinessStatDTO.getMemberIncome() - oldBusinessStatDTO.getMemberIncome());
      newBusinessStatDTO.setOtherIncome(newBusinessStatDTO.getOtherIncome() - oldBusinessStatDTO.getOtherIncome());
      newBusinessStatDTO.setStatTime(System.currentTimeMillis());
    } else {
      newBusinessStatDTO.setSales(newBusinessStatDTO.getSales() + oldBusinessStatDTO.getSales());
      newBusinessStatDTO.setService(newBusinessStatDTO.getService() + oldBusinessStatDTO.getService());
      newBusinessStatDTO.setWash(newBusinessStatDTO.getWash() + oldBusinessStatDTO.getWash());
      newBusinessStatDTO.setMemberIncome(newBusinessStatDTO.getMemberIncome() + oldBusinessStatDTO.getMemberIncome());
      newBusinessStatDTO.setStatSum(newBusinessStatDTO.getStatSum() + oldBusinessStatDTO.getWash() + oldBusinessStatDTO.getSales() + oldBusinessStatDTO.getService() + oldBusinessStatDTO.getMemberIncome());
      newBusinessStatDTO.setProductCost(newBusinessStatDTO.getProductCost() + oldBusinessStatDTO.getProductCost());
      newBusinessStatDTO.setOrderOtherIncomeCost(newBusinessStatDTO.getOrderOtherIncomeCost() + oldBusinessStatDTO.getOrderOtherIncomeCost());
      //营业外记账相关
      newBusinessStatDTO.setOtherIncome(newBusinessStatDTO.getOtherIncome() + oldBusinessStatDTO.getOtherIncome());
      newBusinessStatDTO.setRentExpenditure(newBusinessStatDTO.getRentExpenditure() + oldBusinessStatDTO.getRentExpenditure());
      newBusinessStatDTO.setSalaryExpenditure(newBusinessStatDTO.getSalaryExpenditure() + oldBusinessStatDTO.getSalaryExpenditure());
      newBusinessStatDTO.setUtilitiesExpenditure(newBusinessStatDTO.getUtilitiesExpenditure() + oldBusinessStatDTO.getUtilitiesExpenditure());
      newBusinessStatDTO.setOtherExpenditure(newBusinessStatDTO.getOtherExpenditure() + oldBusinessStatDTO.getOtherExpenditure());

      newBusinessStatDTO.setStatTime(System.currentTimeMillis());
    }
    return newBusinessStatDTO;
  }

  /**
   * 营业外记账更新营业统计数据
   * @param oldBusinessAccountDTO 更新之前的营业外记账信息
   * @param newBusinessAccountDTO 更新之后的营业外记账信息
   * @param businessAccountEnum 营业外记账类型:新增 修改 删除
   */
  @Override
  public void statFromBusinessAccountDTO(BusinessAccountDTO oldBusinessAccountDTO, BusinessAccountDTO newBusinessAccountDTO,BusinessAccountEnum businessAccountEnum) {
    if (newBusinessAccountDTO == null || businessAccountEnum == null) {
      return;
    }

    //新增记账 更新流水统计数据和营业统计数据
    if (BusinessAccountEnum.STATUS_SAVE == businessAccountEnum) {
      saveStatFromBAccountDTO(newBusinessAccountDTO);
      return;
    }

    //删除营业外记账
    if (BusinessAccountEnum.STATUS_DELETE == businessAccountEnum) {
      deleteStatFromBAccountDTO(newBusinessAccountDTO);
    }

    //更新营业外记账
    if (BusinessAccountEnum.STATUS_UPDATE == businessAccountEnum) {

      deleteStatFromBAccountDTO(oldBusinessAccountDTO);
      saveStatFromBAccountDTO(newBusinessAccountDTO);
    }

  }

  /**
   * 根据营业外记账信息删除营业统计数据和流水统计数据
   * @param businessAccountDTO
   */
  public void deleteStatFromBAccountDTO(BusinessAccountDTO businessAccountDTO) {

    if (businessAccountDTO.getMoneyCategory() == null) {
      LOG.error("营业外记账数据错误" + businessAccountDTO.toString());
      return;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);

    Long editDate = businessAccountDTO.getEditDate();
    if (editDate == null) {
      return;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTimeInMillis(editDate);

    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    //流水统计修改
    RunningStatDTO runningStatChangeDTO = runningStatService.getRunningStatChangeDTOByShopIdYearMonthDay(businessAccountDTO.getShopId(), (long) year, (long) month, (long) day);

    if (runningStatChangeDTO == null) {

      runningStatChangeDTO = new RunningStatDTO();
      runningStatChangeDTO.setShopId(businessAccountDTO.getShopId());
      runningStatChangeDTO.setStatYear((long) year);
      runningStatChangeDTO.setStatMonth((long) month);
      runningStatChangeDTO.setStatDay((long) day);
      runningStatChangeDTO.setStatDate(editDate);
      if (BusinessAccountConstant.BUSINESS_INCOME.equals(businessAccountDTO.getMoneyCategory().name())) {
        runningStatChangeDTO.setCashIncome(0 - NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayIncome(0 - NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeIncome(0 - NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setIncomeSum(0 - (NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck())));
        runningStatChangeDTO.setRunningSum(runningStatChangeDTO.getIncomeSum());
      } else {
        runningStatChangeDTO.setCashExpenditure(0 - NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayExpenditure(0 - NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeExpenditure(0 - NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setExpenditureSum(0 - (NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck())));
        runningStatChangeDTO.setRunningSum(0 - (NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck())));
      }
      runningStatService.saveRunningStatChangeDTO(runningStatChangeDTO);
    } else {
      if (BusinessAccountConstant.BUSINESS_INCOME.equals(businessAccountDTO.getMoneyCategory().name())) {
        runningStatChangeDTO.setCashIncome(runningStatChangeDTO.getCashIncome() - NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayIncome(runningStatChangeDTO.getUnionPayIncome() - NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeIncome(runningStatChangeDTO.getChequeIncome() - NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setIncomeSum(runningStatChangeDTO.getIncomeSum() - (NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck())));
        runningStatChangeDTO.setRunningSum(runningStatChangeDTO.getRunningSum() - (NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck())));
      } else {
        runningStatChangeDTO.setCashExpenditure(runningStatChangeDTO.getCashExpenditure() - NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayExpenditure(runningStatChangeDTO.getUnionPayExpenditure() - NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeExpenditure(runningStatChangeDTO.getChequeExpenditure() - NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setExpenditureSum(runningStatChangeDTO.getExpenditureSum() - (NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck())));
        runningStatChangeDTO.setRunningSum(runningStatChangeDTO.getRunningSum() + (NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck())));
      }
      runningStatService.saveRunningStatChangeDTO(runningStatChangeDTO);
    }

    //删除流水统计记录
    deleteRecordFromAccountDTO(businessAccountDTO);
  }

  /**
   * 根据营业外记账信息保存流水和营业信息
   * @param businessAccountDTO
   */
  public void saveStatFromBAccountDTO(BusinessAccountDTO businessAccountDTO) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);

    if (businessAccountDTO.getMoneyCategory() == null) {
      LOG.error("营业外记账数据错误" + businessAccountDTO.toString());
      return;
    }
    String editDateStr = businessAccountDTO.getEditDateStr();
    if (StringUtil.isEmpty(editDateStr)) {
      return;
    }

    Long editDate = null;
    try {
      editDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, editDateStr);
      editDate ++; //设置统计时间统一为每天00:00:00
      if(DateUtil.isCurrentTime(editDate)){
        editDate = System.currentTimeMillis();
      }
    } catch (Exception e) {
      LOG.error(e.getMessage());
      editDate = System.currentTimeMillis();
    }

    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTimeInMillis(editDate);

    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    //流水统计修改
    RunningStatDTO runningStatChangeDTO = runningStatService.getRunningStatChangeDTOByShopIdYearMonthDay(businessAccountDTO.getShopId(), (long) year, (long) month, (long) day);

    if (runningStatChangeDTO == null) {

      runningStatChangeDTO = new RunningStatDTO();
      runningStatChangeDTO.setShopId(businessAccountDTO.getShopId());
      runningStatChangeDTO.setStatYear((long) year);
      runningStatChangeDTO.setStatMonth((long) month);
      runningStatChangeDTO.setStatDay((long) day);
      runningStatChangeDTO.setStatDate(editDate);
      if (BusinessAccountConstant.BUSINESS_INCOME.equals(businessAccountDTO.getMoneyCategory().name())) {
        runningStatChangeDTO.setCashIncome(NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayIncome(NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeIncome(NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setIncomeSum(NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setRunningSum(runningStatChangeDTO.getIncomeSum());
      } else {
        runningStatChangeDTO.setCashExpenditure(NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayExpenditure(NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeExpenditure(NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setExpenditureSum(NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setRunningSum(0 - runningStatChangeDTO.getExpenditureSum());
      }
      runningStatService.saveRunningStatChangeDTO(runningStatChangeDTO);
    } else {
      if (BusinessAccountConstant.BUSINESS_INCOME.equals(businessAccountDTO.getMoneyCategory().name())) {
        runningStatChangeDTO.setCashIncome(runningStatChangeDTO.getCashIncome() + NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayIncome(runningStatChangeDTO.getUnionPayIncome() + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeIncome(runningStatChangeDTO.getChequeIncome() + NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setIncomeSum(runningStatChangeDTO.getIncomeSum() + NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setRunningSum(runningStatChangeDTO.getRunningSum() + NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck()));
      } else {
        runningStatChangeDTO.setCashExpenditure(runningStatChangeDTO.getCashExpenditure() + NumberUtil.doubleVal(businessAccountDTO.getCash()));
        runningStatChangeDTO.setUnionPayExpenditure(runningStatChangeDTO.getUnionPayExpenditure() + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
        runningStatChangeDTO.setChequeExpenditure(runningStatChangeDTO.getChequeExpenditure() + NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setExpenditureSum(runningStatChangeDTO.getExpenditureSum() + NumberUtil.doubleVal(businessAccountDTO.getCash()) + NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) + NumberUtil.doubleVal(businessAccountDTO.getCheck()));
        runningStatChangeDTO.setRunningSum(runningStatChangeDTO.getIncomeSum() - runningStatChangeDTO.getExpenditureSum());
      }
      runningStatService.saveRunningStatChangeDTO(runningStatChangeDTO);
    }
    saveOrUpdateRecordFromAccountDTO(businessAccountDTO, editDate);
  }

  /**
   * 根据营业外记账信息保存流水统计记录
   *
   * @param businessAccountDTO
   * @param receptionDate
   */
  public void saveOrUpdateRecordFromAccountDTO(BusinessAccountDTO businessAccountDTO, long receptionDate) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    if (businessAccountDTO.getMoneyCategory() == null) {
      LOG.error("营业外记账数据错误" + businessAccountDTO.toString());
      return;
    }
    if (BusinessAccountConstant.BUSINESS_INCOME.equals(businessAccountDTO.getMoneyCategory().name())) {

      ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
      if (businessAccountDTO.getId() != null) {
        List<ReceptionRecordDTO> receptionRecordDTOList = txnService.getReceptionRecordByOrderId(businessAccountDTO.getShopId(), businessAccountDTO.getId(),OrderTypes.BUSINESS_ACCOUNT);
        if (CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
          receptionRecordDTO = receptionRecordDTOList.get(0);
        }
      }
      receptionRecordDTO.setAmount(businessAccountDTO.getTotal());
      receptionRecordDTO.setCash(NumberUtil.doubleVal(businessAccountDTO.getCash()));
      receptionRecordDTO.setCheque(NumberUtil.doubleVal(businessAccountDTO.getCheck()));
      receptionRecordDTO.setBankCard(NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
      receptionRecordDTO.setMemberBalancePay(0D);
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setOriginDebt(0D);
      receptionRecordDTO.setRemainDebt(0D);
      receptionRecordDTO.setDiscount(0D);
      receptionRecordDTO.setOrderId(businessAccountDTO.getId());
      receptionRecordDTO.setReceptionDate(receptionDate);
      receptionRecordDTO.setShopId(businessAccountDTO.getShopId());
      receptionRecordDTO.setOrderTotal(businessAccountDTO.getTotal());
      receptionRecordDTO.setAfterMemberDiscountTotal(businessAccountDTO.getTotal());
      receptionRecordDTO.setOrderTypeEnum(OrderTypes.BUSINESS_ACCOUNT);
      receptionRecordDTO.setDayType(DayType.OTHER_DAY);
      receptionRecordDTO.setPayee(businessAccountDTO.getUserName());
      receptionRecordDTO.setPayeeId(businessAccountDTO.getUserId());
      if(StringUtil.isNotEmpty(businessAccountDTO.getContent())){
        receptionRecordDTO.setMemo(businessAccountDTO.getAccountCategory() +":" + businessAccountDTO.getContent());
      }else{
        receptionRecordDTO.setMemo(businessAccountDTO.getAccountCategory());
      }
      txnService.saveOrUpdateReceptionRecord(receptionRecordDTO);

    } else {

      PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();

      if (businessAccountDTO.getId() != null) {
        List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = supplierPayableService.getPayableHistoryRecord(businessAccountDTO.getShopId(), null, businessAccountDTO.getId(),PaymentTypes.BUSINESS_ACCOUNT);
        if (CollectionUtils.isNotEmpty(payableHistoryRecordDTOList)) {
          payableHistoryRecordDTO = payableHistoryRecordDTOList.get(0);
        }
      }

      payableHistoryRecordDTO.setShopId(businessAccountDTO.getShopId());
      payableHistoryRecordDTO.setPurchaseInventoryId(businessAccountDTO.getId());
      payableHistoryRecordDTO.setDeduction(0D);
      payableHistoryRecordDTO.setCreditAmount(0D);
      payableHistoryRecordDTO.setCash(NumberUtil.doubleVal(businessAccountDTO.getCash()));
      payableHistoryRecordDTO.setBankCardAmount(NumberUtil.doubleVal(businessAccountDTO.getUnionpay()));
      payableHistoryRecordDTO.setCheckAmount(NumberUtil.doubleVal(businessAccountDTO.getCheck()));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.doubleVal(businessAccountDTO.getTotal()));
      payableHistoryRecordDTO.setDepositAmount(0D);

      if(StringUtil.isNotEmpty(businessAccountDTO.getContent())){
        payableHistoryRecordDTO.setMaterialName(businessAccountDTO.getAccountCategory() + ":" + businessAccountDTO.getContent());
      }else{
        payableHistoryRecordDTO.setMaterialName(businessAccountDTO.getAccountCategory());
      }
      payableHistoryRecordDTO.setAmount(businessAccountDTO.getTotal());
      payableHistoryRecordDTO.setPaymentType(PaymentTypes.BUSINESS_ACCOUNT);
      payableHistoryRecordDTO.setPaidTime(receptionDate);
      payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
      payableHistoryRecordDTO.setPayer(businessAccountDTO.getUserName());
      payableHistoryRecordDTO.setPayerId(businessAccountDTO.getUserId());

      supplierPayableService.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);
    }
  }


  /**
   * 根据营业外记账信息删除流水统计记录
   *
   * @param businessAccountDTO
   */
  public void deleteRecordFromAccountDTO(BusinessAccountDTO businessAccountDTO) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    if (businessAccountDTO.getMoneyCategory() == null) {
      LOG.error("营业外记账数据错误" + businessAccountDTO.toString());
      return;
    }

    if (BusinessAccountConstant.BUSINESS_INCOME.equals(businessAccountDTO.getMoneyCategory().name())) {

      if (businessAccountDTO.getId() != null) {
        List<ReceptionRecordDTO> receptionRecordDTOList = txnService.getReceptionRecordByOrderId(businessAccountDTO.getShopId(), businessAccountDTO.getId(),OrderTypes.BUSINESS_ACCOUNT);
        if (CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
          this.deleteReceptionRecordList(receptionRecordDTOList);
        }
      }
    } else {
      if (businessAccountDTO.getId() != null) {
        List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = supplierPayableService.getPayableHistoryRecord(businessAccountDTO.getShopId(), null, businessAccountDTO.getId(),PaymentTypes.BUSINESS_ACCOUNT);
        if (CollectionUtils.isNotEmpty(payableHistoryRecordDTOList)) {
          this.deletePayHistoryRecord(payableHistoryRecordDTOList);
        }
      }
    }
  }

  /**
   * 删除流水统计收入记录
   * @param receptionRecordDTOList
   */
  public void deleteReceptionRecordList(List<ReceptionRecordDTO> receptionRecordDTOList) {
    if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
        if (receptionRecordDTO.getId() == null) {
          continue;
        }
        writer.delete(ReceptionRecord.class, receptionRecordDTO.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 删除流水统计支出记录
   * @param payableHistoryRecordDTOList
   */
  public void deletePayHistoryRecord(List<PayableHistoryRecordDTO> payableHistoryRecordDTOList) {
    if (CollectionUtils.isEmpty(payableHistoryRecordDTOList)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
        if (payableHistoryRecordDTO.getId() == null) {
          continue;
        }
        writer.delete(PayableHistoryRecord.class, payableHistoryRecordDTO.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  /**
   *根据统计信息保存到营业统计
   * @param statDTO
   */
  public void saveBusinessStatChangeFromDTO(BusinessStatDTO statDTO) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      if (statDTO == null) {
        throw new Exception("统计信息为空");
      }
      BusinessStatDTO businessStatDTO = txnService.getBusinessStatChangeOfDay(statDTO.getShopId(), statDTO.getStatYear(), statDTO.getStatMonth(), statDTO.getStatDay());
      if (businessStatDTO == null) {
        businessStatDTO = new BusinessStatDTO();
        businessStatDTO.setShopId(statDTO.getShopId());
        businessStatDTO.setStatYear(statDTO.getStatYear());
        businessStatDTO.setStatMonth(statDTO.getStatMonth());
        businessStatDTO.setStatDay(statDTO.getStatDay());
        businessStatDTO.setSales(statDTO.getSales());
        businessStatDTO.setService(statDTO.getService());
        businessStatDTO.setWash(statDTO.getWash());
        businessStatDTO.setMemberIncome(statDTO.getMemberIncome());
        businessStatDTO.setStatSum(statDTO.getWash() + statDTO.getService() + statDTO.getSales());
        businessStatDTO.setProductCost(statDTO.getProductCost());
        businessStatDTO.setOrderOtherIncomeCost(statDTO.getOrderOtherIncomeCost());
      } else {
        businessStatDTO.setSales(statDTO.getSales() + businessStatDTO.getSales());
        businessStatDTO.setService(statDTO.getService() + businessStatDTO.getService());
        businessStatDTO.setWash(statDTO.getWash() + businessStatDTO.getWash());
        businessStatDTO.setMemberIncome(statDTO.getMemberIncome() + businessStatDTO.getMemberIncome());
        businessStatDTO.setStatSum(statDTO.getWash() + statDTO.getService() + statDTO.getSales() + businessStatDTO.getStatSum());
        businessStatDTO.setProductCost(statDTO.getProductCost() + businessStatDTO.getProductCost());
        businessStatDTO.setOrderOtherIncomeCost(statDTO.getOrderOtherIncomeCost() + businessStatDTO.getOrderOtherIncomeCost());
      }
      businessStatDTO.setStatTime(statDTO.getStatTime());
      txnService.saveBusinessStatChange(businessStatDTO);
    } catch (Exception e) {
      LOG.error("BusinessStatService.java method=saveBusinessStatChangeFromDTO");
      LOG.error("营业统计保存到businessStatChange出错，statDTO:" + statDTO.toString());
      LOG.error(e.getMessage(), e);
    }
  }


  /**
   * 欠款结算 如果某个单据有折扣 更新该单据结算日的营业额 去掉折扣
   * @param discountAmount
   * @param receivable
   * @param txnWriter
   */
  public void updateBusinessStatFromDebt(double discountAmount,Receivable receivable ,TxnWriter txnWriter) {

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    if (discountAmount <= 0 || receivable == null || receivable.getOrderId() == null || receivable.getOrderTypeEnum() == null) {
      return;
    }

    OrderTypes orderTypes = receivable.getOrderTypeEnum();
    BusinessStatDTO statDTO = null;

    //洗车美容单
    if (orderTypes == OrderTypes.WASH_BEAUTY) {
      WashBeautyOrder washBeautyOrder = txnWriter.getById(WashBeautyOrder.class, receivable.getOrderId());
      if (washBeautyOrder != null) {
        Long vestDate = washBeautyOrder.getVestDate();
        statDTO = new BusinessStatDTO();
        statDTO.setShopId(receivable.getShopId());
        statDTO.setStatTime(vestDate);
        statDTO.setStatYear(DateUtil.getYearByVestDate(vestDate));
        statDTO.setStatMonth(DateUtil.getMonthByVestDate(vestDate));
        statDTO.setStatDay(DateUtil.getDayByVestDate(vestDate));
        statDTO.setWash(0 - discountAmount);
        statDTO.setStatSum(0 - discountAmount);
      }
    } else if (orderTypes == OrderTypes.REPAIR) {
      RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, receivable.getOrderId());
      if (repairOrder != null) {
        Long vestDate = repairOrder.getVestDate();
        statDTO = new BusinessStatDTO();
        statDTO.setShopId(receivable.getShopId());
        statDTO.setStatTime(vestDate);
        statDTO.setStatYear(DateUtil.getYearByVestDate(vestDate));
        statDTO.setStatMonth(DateUtil.getMonthByVestDate(vestDate));
        statDTO.setStatDay(DateUtil.getDayByVestDate(vestDate));
        statDTO.setService(0 - discountAmount);
        statDTO.setStatSum(0 - discountAmount);
      }
    } else if (orderTypes == OrderTypes.SALE) {
      SalesOrder salesOrder = txnWriter.getById(SalesOrder.class, receivable.getOrderId());
      if (salesOrder != null) {
        Long vestDate = salesOrder.getVestDate();
        statDTO = new BusinessStatDTO();
        statDTO.setShopId(receivable.getShopId());
        statDTO.setStatTime(vestDate);
        statDTO.setStatYear(DateUtil.getYearByVestDate(vestDate));
        statDTO.setStatMonth(DateUtil.getMonthByVestDate(vestDate));
        statDTO.setStatDay(DateUtil.getDayByVestDate(vestDate));
        statDTO.setSales(0 - discountAmount);
        statDTO.setStatSum(0 - discountAmount);
      }
    } else {
      return;
    }

    if (statDTO == null) {
      LOG.error("BusinessStatService.java method=updateBusinessStatFromDebt");
      LOG.error("欠款结算更新营业额失败,参数:" + "discount:" + discountAmount + ",orderType:" + orderTypes + ",receivable:" + receivable.toDTO().toString());
      return;
    }

    BusinessStatDTO businessStatDTO = txnService.getBusinessStatChangeOfDay(statDTO.getShopId(), statDTO.getStatYear(), statDTO.getStatMonth(), statDTO.getStatDay());
    if (businessStatDTO == null) {
      businessStatDTO = new BusinessStatDTO();
      businessStatDTO.setShopId(statDTO.getShopId());
      businessStatDTO.setStatYear(statDTO.getStatYear());
      businessStatDTO.setStatMonth(statDTO.getStatMonth());
      businessStatDTO.setStatDay(statDTO.getStatDay());
      businessStatDTO.setSales(statDTO.getSales());
      businessStatDTO.setService(statDTO.getService());
      businessStatDTO.setWash(statDTO.getWash());
      businessStatDTO.setMemberIncome(statDTO.getMemberIncome());
      businessStatDTO.setStatSum(statDTO.getWash() + statDTO.getService() + statDTO.getSales());
      businessStatDTO.setProductCost(statDTO.getProductCost());
      businessStatDTO.setStatTime(statDTO.getStatTime());
      businessStatDTO.setOrderOtherIncomeCost(statDTO.getOrderOtherIncomeCost());
      txnWriter.save(new BusinessStatChange(businessStatDTO));
      return;
    } else {
      businessStatDTO.setSales(statDTO.getSales() + businessStatDTO.getSales());
      businessStatDTO.setService(statDTO.getService() + businessStatDTO.getService());
      businessStatDTO.setWash(statDTO.getWash() + businessStatDTO.getWash());
      businessStatDTO.setMemberIncome(statDTO.getMemberIncome() + businessStatDTO.getMemberIncome());
      businessStatDTO.setStatSum(statDTO.getWash() + statDTO.getService() + statDTO.getSales() + businessStatDTO.getStatSum());
      businessStatDTO.setProductCost(statDTO.getProductCost() + businessStatDTO.getProductCost());
      businessStatDTO.setOrderOtherIncomeCost(statDTO.getOrderOtherIncomeCost() + businessStatDTO.getOrderOtherIncomeCost());
      businessStatDTO.setStatTime(statDTO.getStatTime());

      BusinessStatChange businessStatChange = txnWriter.getById(BusinessStatChange.class, businessStatDTO.getId());
      businessStatChange = businessStatChange.fromDTO(businessStatDTO);
      txnWriter.update(businessStatChange);
      return;
    }
  }

  /**
   * 会员统计页面 获得购卡续卡列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @param arrayType
   * @param pager
   * @param memberStatResultDTO
   * @return
   */
  public MemberStatResultDTO getMemberCardOrderDTOList(long shopId,long startTime,long endTime,String arrayType,Pager pager,MemberStatResultDTO memberStatResultDTO,OrderSearchConditionDTO orderSearchConditionDTO) {

    List<MemberStatResultDTO> memberStatResultDTOList = null;
    List<MemberCardOrderDTO> memberCardOrderDTOList = null;

    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);

      Long orderId = null;
      ReceivableDTO receivableDTO = null;
      MemberDTO memberDTO = null;


      memberCardOrderDTOList = txnService.getMemberOrderListByPagerTimeArrayType(shopId, startTime, endTime, pager, arrayType,orderSearchConditionDTO);
      if (CollectionUtils.isEmpty(memberCardOrderDTOList)) {
        return memberStatResultDTO;
      }
      memberStatResultDTOList = new ArrayList<MemberStatResultDTO>();
      for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
        MemberStatResultDTO statResultDTO = new MemberStatResultDTO();
        statResultDTO.setVestDateStr(DateUtil.dateLongToStr(memberCardOrderDTO.getVestDate(), DateUtil.DATE_STRING_FORMAT_CN));
        statResultDTO.setCustomerName(memberCardOrderDTO.getCustomerName());
        orderId = memberCardOrderDTO.getId();
        CustomerDTO customerDTO = userService.getCustomerById(memberCardOrderDTO.getCustomerId());
        if (customerDTO != null) {
          statResultDTO.setCustomerStatus(customerDTO.getStatus());
        }

        List<MemberCardOrderItemDTO> memberCardOrderItemDTOList = txnService.getMemberCardOrderItemDTOByOrderId(shopId, orderId);
        if (CollectionUtils.isNotEmpty(memberCardOrderItemDTOList)) {
          MemberCardOrderItemDTO memberCardOrderItemDTO = memberCardOrderItemDTOList.get(0);
          statResultDTO.setMemberBalance(memberCardOrderItemDTO.getWorth());
        }

        memberDTO = membersService.getMemberByCustomerId(shopId, memberCardOrderDTO.getCustomerId());
        if (memberDTO == null) {
          statResultDTO.setMemberNo("");
        } else {
          statResultDTO.setMemberNo(memberDTO.getMemberNo());
        }

        List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOList = txnService.getMemberCardOrderServiceDTOByOrderId(shopId, orderId);

        if (CollectionUtils.isNotEmpty(memberCardOrderServiceDTOList)) {
          StringBuffer memberServiceChange = new StringBuffer();
          for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderServiceDTOList) {
            ServiceDTO serviceDTO = txnService.getServiceById(memberCardOrderServiceDTO.getServiceId());
            if (serviceDTO == null) {
              continue;
            }

            //删除服务
            if(ServiceLimitTypes.DELETE == memberCardOrderServiceDTO.getBalanceTimesLimitType()){
              if(ServiceLimitTypes.UNLIMITED == memberCardOrderServiceDTO.getOldTimesLimitType()){
                memberServiceChange.append("删除").append(serviceDTO.getName()).append("无限次,");
              }else{
                memberServiceChange.append("删除").append(serviceDTO.getName()).append(NumberUtil.intValue(memberCardOrderServiceDTO.getOldTimes())).append("次,");
              }
            }else if (ServiceLimitTypes.UNLIMITED == memberCardOrderServiceDTO.getBalanceTimesLimitType()) {//无限次
              memberServiceChange.append(serviceDTO.getName()).append("无限次,");
            } else {
              if (NumberUtil.intValue(memberCardOrderServiceDTO.getIncreasedTimes()) >= 0) {
                memberServiceChange.append(serviceDTO.getName()).append("增加").append(memberCardOrderServiceDTO.getIncreasedTimes()).append("次,");
              } else if (NumberUtil.intValue(memberCardOrderServiceDTO.getIncreasedTimes()) < 0) {
                memberServiceChange.append(serviceDTO.getName()).append("减少").append(0 - memberCardOrderServiceDTO.getIncreasedTimes()).append("次,");
              }
            }
            if(StringUtil.isEmpty(memberCardOrderServiceDTO.getVehicles())){
               memberServiceChange.append("限制车牌:不限,");
            }else{
               memberServiceChange.append("限制车牌:").append(memberCardOrderServiceDTO.getVehicles()).append(",");
            }
            if (NumberUtil.longValue(memberCardOrderServiceDTO.getDeadline()) == -1) {
              memberServiceChange.append("失效日期:无限期;");
            } else {
              memberServiceChange.append("失效日期:" + DateUtil.dateLongToStr(memberCardOrderServiceDTO.getDeadline(), DateUtil.DATE_STRING_FORMAT_CN)).append(";");
            }
            memberServiceChange.append("&#13;");

          }
          statResultDTO.setMemberServiceChange(memberServiceChange.toString());
        }
        receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, orderId);
        if (receivableDTO != null) {
          statResultDTO.setSettledAmount(receivableDTO.getSettledAmount());
          statResultDTO.setTotal(memberCardOrderDTO.getTotal());
          statResultDTO.setDebt(receivableDTO.getDebt());
          statResultDTO.setDiscount(receivableDTO.getDiscount());
          memberStatResultDTO.setPageTotal(memberStatResultDTO.getPageTotal() + memberCardOrderDTO.getTotal());
          memberStatResultDTO.setPageDebt(memberStatResultDTO.getPageDebt() + receivableDTO.getDebt());
          memberStatResultDTO.setPageDiscount(memberStatResultDTO.getPageDiscount() + receivableDTO.getDiscount());
          memberStatResultDTO.setPageTotalSettledAmount(memberStatResultDTO.getPageTotalSettledAmount() + receivableDTO.getSettledAmount());
        }
        memberStatResultDTOList.add(statResultDTO);
      }
      memberStatResultDTO.setOrders(memberStatResultDTOList);
    } catch (Exception e) {
      LOG.error("businessStatService.getMemberCardOrderDTOList ");
      LOG.error("shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return memberStatResultDTO;
  }



  /**
    * 会员统计页面 获得购卡续卡列表
    * @param shopId
    * @param startTime
    * @param endTime
    * @param arrayType
    * @param pager
    * @param memberStatResultDTO
    * @return
    */
   public MemberStatResultDTO getMemberCardReturnDTOList(long shopId,long startTime,long endTime,String arrayType,Pager pager,MemberStatResultDTO memberStatResultDTO,OrderSearchConditionDTO orderSearchConditionDTO) {

     List<MemberStatResultDTO> memberStatResultDTOList = null;
     List<MemberCardReturnDTO> memberCardOrderDTOList = null;

     try {
       ITxnService txnService = ServiceManager.getService(ITxnService.class);
       IUserService userService = ServiceManager.getService(IUserService.class);

       memberCardOrderDTOList = txnService.getMemberReturnListByPagerTimeArrayType(shopId, startTime, endTime, pager, arrayType, orderSearchConditionDTO);
       if (CollectionUtils.isEmpty(memberCardOrderDTOList)) {
         return memberStatResultDTO;
       }
       memberStatResultDTOList = new ArrayList<MemberStatResultDTO>();
       for (MemberCardReturnDTO memberCardOrderDTO : memberCardOrderDTOList) {
         MemberStatResultDTO statResultDTO = new MemberStatResultDTO();
         statResultDTO.setVestDateStr(DateUtil.dateLongToStr(memberCardOrderDTO.getVestDate(), DateUtil.DATE_STRING_FORMAT_CN));
         statResultDTO.setCustomerName(memberCardOrderDTO.getCustomer());
         CustomerDTO customerDTO = userService.getCustomerById(memberCardOrderDTO.getCustomerId());
         if (customerDTO != null) {
           statResultDTO.setCustomerStatus(customerDTO.getStatus());
         }
         statResultDTO.setMemberNo(memberCardOrderDTO.getMemberNo());
         statResultDTO.setMemberType(memberCardOrderDTO.getMemberCardName());
         OrderIndexDTO orderIndexDTO = ServiceManager.getService(IOrderIndexService.class).getOrderIndexByOrderId(shopId,memberCardOrderDTO.getId());
         if(orderIndexDTO !=null){
           statResultDTO.setMemberServiceChange(orderIndexDTO.getOrderContent());
         }
         statResultDTO.setMemberBalance(memberCardOrderDTO.getMemberBalance());
         statResultDTO.setSettledAmount(memberCardOrderDTO.getTotal());

         memberStatResultDTO.setPageTotal(memberStatResultDTO.getPageTotal() + memberCardOrderDTO.getTotal());
         memberStatResultDTO.setTotal(memberStatResultDTO.getTotal() + memberCardOrderDTO.getTotal());
         memberStatResultDTOList.add(statResultDTO);
       }
       memberStatResultDTO.setOrders(memberStatResultDTOList);
     } catch (Exception e) {
       LOG.error("businessStatService.getMemberCardOrderDTOList ");
       LOG.error("shopId:" + shopId);
       LOG.error(e.getMessage(), e);
     }
     return memberStatResultDTO;
   }


  /**
   * function：查询每一年的营业额 默认从2010年开始
   *
   * @param shopId   店铺id
   * @return resultList
   */
  public List<BusinessStatDTO> getYearBusinessStatList(long shopId) {

    List<BusinessStatDTO> resultList = new ArrayList<BusinessStatDTO>();

    int currentYear = DateUtil.getCurrentYear();
    for (int index = 2010; index < 2020; index++) {
      BusinessStatDTO businessStatDTO = new BusinessStatDTO();
      businessStatDTO.setStatYear((long) index);
      if (index >= 2012 && index <= currentYear) {
        businessStatDTO = this.getYearBusinessStatByYear(shopId, (long) index);
      } else {
        businessStatDTO.setStatSum(0D);
      }
      resultList.add(businessStatDTO);
    }
    return resultList;
  }


  public BusinessStatDTO getYearBusinessStatByYear(Long shopId,Long year) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    BusinessStatDTO businessStatDTO = new BusinessStatDTO();
    businessStatDTO.setShopId(shopId);
    businessStatDTO.setStatYear(year.longValue());
    businessStatDTO.setStatMonth((long) -1);
    businessStatDTO.setStatDay((long) -1);

    List<BusinessStatDTO> thisYearStatList = txnService.getLatestBusinessStat(shopId, year, 1);
    if (CollectionUtils.isNotEmpty(thisYearStatList)) {
      businessStatDTO = thisYearStatList.get(0);
      List<BusinessStatDTO> lastYearList = txnService.getLatestBusinessStat(shopId, year - 1, 1);
      if (CollectionUtils.isNotEmpty(lastYearList)) {
        businessStatDTO = this.calculateBusinessStat(businessStatDTO, lastYearList.get(0), true);
      }
    }
    BusinessStatDTO businessStatChangeDTO = txnService.sumBusinessStatChangeForYear(shopId, year);
    if (businessStatChangeDTO != null) {
      businessStatDTO = this.calculateBusinessStat(businessStatDTO, businessStatChangeDTO, false);
    }
    return businessStatDTO;
  }

  @Autowired
  private TxnDaoManager txnDaoManager;

}
