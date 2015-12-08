package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.BusinessAccountEnum;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.txn.dto.BusinessStatDTO;
import com.bcgogo.txn.dto.MemberStatResultDTO;
import com.bcgogo.txn.dto.PayableHistoryRecordDTO;
import com.bcgogo.txn.dto.ReceptionRecordDTO;
import com.bcgogo.txn.model.Receivable;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-9-28
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public interface IBusinessStatService {

   /**
   * 营业统计:根据是否作废 和结算时间计算营业统计
   * 单据作废:扣除单据结算日的营业额
   * @param businessStatDTO
   * @param isRepeal
   * @param vestDate
   */
  public void businessStat(BusinessStatDTO businessStatDTO,boolean isRepeal,Long vestDate);

  /**
   * 两个营业统计数据进行计算
   * @param newBusinessStatDTO
   * @param oldBusinessStatDTO
   * @param isRepeal
   * @return
   */
  public BusinessStatDTO calculateBusinessStat(BusinessStatDTO newBusinessStatDTO, BusinessStatDTO oldBusinessStatDTO, boolean isRepeal);

  /**
   * 营业外记账更新营业统计数据
   * @param oldBusinessAccountDTO 更新之前的营业外记账信息
   * @param newBusinessAccountDTO 更新之后的营业外记账信息
   * @param businessAccountEnum 营业外记账类型:新增 修改 删除
   */
  public void statFromBusinessAccountDTO(BusinessAccountDTO oldBusinessAccountDTO, BusinessAccountDTO newBusinessAccountDTO,BusinessAccountEnum businessAccountEnum);


  /**
   * 根据营业外记账信息保存流水统计记录
   * @param businessAccountDTO
   * @param receptionDate
   */
  public void saveOrUpdateRecordFromAccountDTO(BusinessAccountDTO businessAccountDTO,long receptionDate);

  /**
   * 根据营业外记账信息删除流水统计记录
   *
   * @param businessAccountDTO
   */
  public void deleteRecordFromAccountDTO(BusinessAccountDTO businessAccountDTO);

  /**
   * 删除流水统计收入记录
   * @param receptionRecordDTOList
   */
  public void deleteReceptionRecordList(List<ReceptionRecordDTO> receptionRecordDTOList);


  /**
   * 删除流水统计支出记录
   * @param payableHistoryRecordDTOList
   */
  public void deletePayHistoryRecord(List<PayableHistoryRecordDTO> payableHistoryRecordDTOList);

  /**
   * 根据营业外记账信息删除营业统计数据和流水统计数据
   * @param businessAccountDTO
   */
  public void deleteStatFromBAccountDTO(BusinessAccountDTO businessAccountDTO);

  /**
   * 根据营业外记账信息保存流水和营业信息
   * @param businessAccountDTO
   */
  public void saveStatFromBAccountDTO(BusinessAccountDTO businessAccountDTO);


  /**
   * 根据统计信息保存到营业统计
   * @param businessStatDTO
   */
  public void saveBusinessStatChangeFromDTO(BusinessStatDTO businessStatDTO);

  /**
   * 欠款结算 如果某个单据有折扣 更新该单据结算日的营业额 去掉折扣
   * @param discountAmount
   * @param receivable
   * @param txnWriter
   */
  public void updateBusinessStatFromDebt(double discountAmount,Receivable receivable ,TxnWriter txnWriter);

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
  public MemberStatResultDTO getMemberCardOrderDTOList(long shopId,long startTime,long endTime,String arrayType,Pager pager,MemberStatResultDTO memberStatResultDTO,OrderSearchConditionDTO orderSearchConditionDTO);


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
  public MemberStatResultDTO getMemberCardReturnDTOList(long shopId,long startTime,long endTime,String arrayType,Pager pager,MemberStatResultDTO memberStatResultDTO,OrderSearchConditionDTO orderSearchConditionDTO);

    /**
   * 获得该店铺下2010年至2019年每年的营业数据
   * @param shopId
   * @return
   */
  public List<BusinessStatDTO> getYearBusinessStatList(long shopId);

}
