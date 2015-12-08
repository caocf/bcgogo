package com.bcgogo.txn.service;

import com.bcgogo.enums.PaymentTypes;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-8-24
 * Time: 下午3:47
 * To change this template use File | Settings | File Templates.
 */
public interface IRunningStatService {
  /**
   * 根据年月日获得流水记录
   * @param shopId
   * @param year
   * @param month
   * @param day
   * @return
   */
  public RunningStatDTO getRunningStatDTOByShopIdYearMonthDay(Long shopId,Long year,long month,long day);

  /**
   * 保存流水记录
   * @param runningStatDTO
   * @return
   */
  public RunningStatDTO saveRunningStatDTO(RunningStatDTO runningStatDTO);

  /**
   * 获得这一年的最后一条数据
   * @param shopId
   * @param statYear
   * @return
   */
  public RunningStatDTO getLastRunningStatDTOByShopId(Long shopId,Long statYear);

  /**
   * 流水统计
   * @param statDTO
   * @param isRepeal 是否作废
   */
  public void runningStat(RunningStatDTO statDTO,boolean isRepeal);

  /**
   * 根据供应商应付款进行流水统计
   * @param depositDTO
   * @param isRepeal
   */
  public void runningStatFromDepositDTO(DepositDTO depositDTO,boolean isRepeal);


  /**
   * 根据客户应预付款进行流水统计
   * @param depositDTO
   * @param isRepeal
   */
  public void runningStatFromCustomerDepositDTO(CustomerDepositDTO depositDTO,boolean isRepeal);

  /**
   * 根据付款记录进行流水统计
   * @param payableHistoryDTO
   * @param isRepeal
   */
  public void runningStatFromPayableHistoryDTO(PayableHistoryDTO payableHistoryDTO,boolean isRepeal);


  /**
   * 获取某一天的流水统计数据
   *
   * @param shopId
   * @param year
   * @param month
   * @param day
   * @return
   */
  public RunningStatDTO getDayRunningStat(long shopId, Integer year, Integer month, Integer day);


  /**
   * 两个流水统计数据进行计算
   *
   * @param newRunningStatDTO
   * @param oldRunningStatDTO
   * @return
   */
  public RunningStatDTO minusRunningStatDate(RunningStatDTO newRunningStatDTO, RunningStatDTO oldRunningStatDTO);

  /**
   * 两个流水统计数据进行计算
   *
   * @param newRunningStatDTO
   * @param oldRunningStatDTO
   * @return
   */
  public RunningStatDTO addRunningStatDate(RunningStatDTO newRunningStatDTO, RunningStatDTO oldRunningStatDTO);


  /**
   * 查询所有店铺下的定金
   *
   * @param shopId
   * @param
   * @return
   */
  public List<DepositDTO> getDepositDTOListBySHopId(long shopId,long startTime,long endTime);

  /**
   * 流水统计初始化receivable order_type_enum order_type_status
   * @param receivableDTOList
   * @return
   */
  public String initReceivable(List<ReceivableDTO> receivableDTOList);

  /**
   * 批量保存流水统计数据
   * @param runningStatDTOList
   */
  public void saveRunningStatDTOList(List<RunningStatDTO> runningStatDTOList);

   /**
   * 批量保存或者更新付款历史记录
   *
   * @param payableHistoryRecordDTOList
   */
  public void saveOrUpdatePayRecordList(List<PayableHistoryRecordDTO> payableHistoryRecordDTOList) throws ParseException;

  /**
   * 批量保存收款记录
   * @param receptionRecordDTOList
   */
  public void saveOrUpdateReceptionRecordList(List<ReceptionRecordDTO> receptionRecordDTOList);


  /**
   * 根据年月日获得流水修改记录
   * @param shopId
   * @param year
   * @param month
   * @param day
   * @return
   */
  public RunningStatDTO getRunningStatChangeDTOByShopIdYearMonthDay(Long shopId,Long year,long month,long day);

  /**
   * 保存流水记录
   *
   * @param runningStatDTO
   * @return
   */
  public RunningStatDTO saveRunningStatChangeDTO(RunningStatDTO runningStatDTO);


  /**
   * 统计该年月下流水变更记录
   * @param shopId
   * @param year
   * @param month
   * @return
   */
  List<RunningStatDTO> getRunningStatChangeByYearMonth(Long shopId, long year, long month);


  /**
   *统计该年月下流水变更记录总和
   * @param shopId
   * @param year
   * @param month
   * @return
   */
  public RunningStatDTO sumRunningStatChangeForYearMonth(Long shopId, Long year,Long month);


  /**
   *统计该年月下流水变更记录
   * @param shopId
   * @param year
   * @param month
   * @return
   */
  public Map<Long, RunningStatDTO> getDayRunningStatChangeMap(Long shopId, Long year, Long month);

  /**
   * 统计该年月下流水变更记录
   * @param shopId
   * @param year
   * @return
   */
  public Map<Long,RunningStatDTO> getMonthRunningStatChangeMap(Long shopId, long year);


  /**
   * 根据统计信息保存到running_stat_change表
   * @param runningStatDTO
   */
  public void saveRunningStatChangeFromDTO(RunningStatDTO runningStatDTO);

  /**
   * 根据付款类型查询付款记录
   * @param paymentTypes
   * @return
   */
  public List<PayableHistoryRecordDTO>  getPayableHistoryRecordByPaymentType(PaymentTypes paymentTypes,Long shopId);

  /**
   * 获取所有入库退货单
   * @return
   */
  public List<PurchaseReturnDTO> getPurchaseReturn(Long shopId);


  public void deletePayHistoryRecord(Long shopId);

  /**
   * 获取店铺下所有客户的总欠款
   * @param shopId
   * @return
   */
  public Double getTotalDebtByShopId(Long shopId,OrderDebtType type);

  public List<RunningStatDTO> getYearRunningStat(Long shopId);


   /**
   * 获取店铺下所有供应商的总欠款
   * @param shopId
   * @return
   */
  public Double getSupplierTotalDebtByShopId(Long shopId,OrderDebtType type);
}
