package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PaymentTypes;
import com.bcgogo.enums.SortObj;
import com.bcgogo.search.dto.RecOrPayIndexDTO;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.PayableHistoryRecord;
import com.bcgogo.txn.model.SupplierReturnPayable;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.dto.SupplierDTO;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-16
 * Time: 下午5:48
 * <p/>
 * 供应商应付款Service
 */
public interface ISupplierPayableService {
  
  /**
   * 添加或更新预付款
   *
   *
   * @param depositDTO@author zhangchuanlong
   */
  public DepositDTO saveOrUpdateDeposit(DepositDTO depositDTO);

  /**
   * 新增预付款（预付款） 包含depoistOrder表的操作
   * @param depositDTO
   * @return
   */
  public Result supplierDepositAdd(DepositDTO depositDTO);

  /**
   * 取用预付款（包含结算、结算退款、退货入款、退款退款）
   * @param depositDTO 本次使用预付款DTO 具体使用可以参考各个结算场景的设置 在private depositStategy 方法里面
   * @param depositOrderDTO 本次使用预付款的记录
   * @return
   */
  public boolean supplierDepositUse(DepositDTO depositDTO,DepositOrderDTO depositOrderDTO,TxnWriter writer);

 /**
   * 通过shopId,customerId 获取预付款 收支记录
   *
   * @param shopId
   * @param supplierId
   * @return
   */
  public List<DepositOrderDTO> queryDepositOrdersByShopIdSupplierId(Long shopId, Long supplierId, Long intOut, SortObj sortObj, Pager pager);


  /**
   * 通过shopId,供应商id，出入标识 获取预付金总数
   * @param shopId
   * @param supplierId
   * @param inOut
   * @return
   */
  public int countDepositOrdersByShopIdSupplierId(Long shopId, Long supplierId, Long inOut);

  /**
   * 通过shopId SupplierId relatedOrderId 查询预付款订单
   * @param shopId
   * @param supplierId
   * @param relatedOrderId
   * @return
   */
  public DepositOrderDTO queryDepositOrderByShopIdAndSupplierIdAndRelatedOrderId(Long shopId, Long supplierId, Long relatedOrderId);

  /**
   * 获得应付款总额
   *
   * @return 总额
   * @author zhangchuanlong
   */
  public double getTotalPayableByShopId(Long shopId);


  /**
   * 分页查询应付款表
   *
   * @param supplierId
   * @param fromTime
   * @param toTime
   * @param pager
   * @return
   */
  public List<PayableDTO> searchPayable(Long shopId, Long supplierId, String fromTime, String toTime, String orderByType, String orderName, Pager pager) throws ParseException;

  /**
   * 付款给供应商
   *
   * @param payableDTOList
   * @param payableHistoryDTO
   */
  public List<PayableDTO> payedToSupplier(List<PayableDTO> payableDTOList, PayableHistoryDTO payableHistoryDTO,PaymentTypes paymentTypes);

  /**
   * 保存或者更新付款历史
   *
   * @param payableHistoryDTO
   */
  public PayableHistoryDTO saveOrUpdatePayableHistory(PayableHistoryDTO payableHistoryDTO);


  /**
   * 根据shopId总定金额
   *
   * @return
   */
  public float getTotaDepositByShopId(Long shopId);


  /**
   * 查询应付款总数
   *
   * @param supplierId
   * @param fromTime
   * @param toTime
   * @return
   */
  public int searchPayable(Long shopId, Long supplierId, String fromTime, String toTime) throws ParseException;

  /**
   * 客户管理--->供应商资料
   * 供应商列表显示应付款和定金
   *
   * @param listSupplierDTO
   * @return
   * @author zhangchuanlong
   */
  public List<SupplierDTO> formListSupplierDTOByPayableAndDeposit(List<SupplierDTO> listSupplierDTO, Long shopId);

  /**
   * 保存付款历史
   *
   * @param payableHistoryDTO
   * @return
   * @author zhangchuanlong
   */
  public PayableHistoryDTO savePayableHistory(PayableHistoryDTO payableHistoryDTO);

  /**
   * 更新付款历史
   *
   * @param payableHistoryDTO
   * @return
   */
  public PayableHistoryDTO updatePayableHistory(PayableHistoryDTO payableHistoryDTO);

  /**
   * 应付款添加到listSupplierDTO
   *
   * @param listSupplierDTO
   * @param shopId
   * @return
   */
  public void formListSupplierDTOByDeposit(List<SupplierDTO> listSupplierDTO, Long shopId);

  /**
   * 应付款添加到listSupplierDTO
   *
   * @param supplierId
   * @param shopId
   * @return
   */
  public Double getSumDepositBySupplierId(Long supplierId, Long shopId);

  /**
   * 应付款添加到listSupplierDTO
   *
   * @param listSupplierDTO
   * @param shopId
   * @return
   */
  public void formListSupplierDTOByPayable(List<SupplierDTO> listSupplierDTO, Long shopId);

  /**
   * 根据供应商ID获取每个供应商的总付款
   * @param supplierId
   * @param shopId
   * @param debtType :
   * @return    list.get(0):总欠款： SUPPLIER_DEBT_RECEIVABLE：应收；SUPPLIER_DEBT_PAYABLE：应付
   * list.get(1): 不可单独使用， debtType:SUPPLIER_DEBT_RECEIVABLE list（0）+ list(1):累计消费金额
   * list.get(1): 不可单独使用， debtType:SUPPLIER_DEBT_PAYABLE list（0）+ list(1):者累计退货金额
   */
  public List<Double>  getSumPayableBySupplierId(Long supplierId, Long shopId,OrderDebtType debtType);

    /**
     * 通过客户ID获取总应收
     * @param customerId
     * @param shopId
     * @param debtType
     * @return
     */
  public Double  getSumReceivableByCustomerId(Long customerId, Long shopId,OrderDebtType debtType);
  /**
   * 使用现金付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  public void paidByCash(PayableHistoryDTO payableHistoryDTO, PayableDTO p,PaymentTypes paymentTypes);

  /**
   * 银行卡付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  public void paidByBankCard(PayableHistoryDTO payableHistoryDTO, PayableDTO p,PaymentTypes paymentTypes);

  /**
   * 使用支票付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  public void paidByCheck(PayableHistoryDTO payableHistoryDTO, PayableDTO p,PaymentTypes paymentTypes);

  /**
   * 使用定金付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  public void paidByDeposit(PayableHistoryDTO payableHistoryDTO, PayableDTO p,PaymentTypes paymentTypes);

  /**
   * 使用扣款付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  public void paidBydeduction(PayableHistoryDTO payableHistoryDTO, PayableDTO p,PaymentTypes paymentTypes);

  /**
   * 更新应付款表
   *
   * @param p
   */
  public void updatePayable(PayableDTO p);

  /**
   * 保存或者更新付款历史记录
   *
   * @param payableHistoryRecordDTO
   */
  public PayableHistoryRecordDTO saveOrUpdatePayHistoryRecord(PayableHistoryRecordDTO payableHistoryRecordDTO);

  /**
   * 更新付款历史记录
   *
   *
   * @param payableHistoryRecordDTO
   * @author zhangchuanlong
   */
  public PayableHistoryRecordDTO updtePayHistoryRecord(PayableHistoryRecordDTO payableHistoryRecordDTO);

  /**
   * 保存付款历史记录
   *
   *
   * @param payableHistoryRecordDTO
   * @author zhangchuanlong
   */
  public PayableHistoryRecordDTO savePayHistoryRecord(PayableHistoryRecordDTO payableHistoryRecordDTO);

  /**
   * 根据订单类型和订单ID查询该单据历史结算记录
   * @param shopId
   * @param orderTypeEnum
   * @param orderId
   * @return
   */
  public List<PayableHistoryRecordDTO> getSettledRecord(Long shopId, OrderTypes orderTypeEnum, Long orderId);

  /**
   * 根据入库单ID，付款历史获得付款历史记录
   *
   * @param purchaseInventoryId
   * @param payableHistoryDTOId
   * @param shopId
   * @return
   */
  public PayableHistoryRecordDTO getPayHistoryRecord(Long purchaseInventoryId, Long payableHistoryDTOId, Long shopId);

  /**
   * 根据多个ID获得应付款记录
   *
   * @param payableIds
   * @return
   */
  public List<PayableDTO> getPayable(List<Long> payableIds);

  /**
   * 根据supplierId获取【应付款】记录总数
   *
   * @param shopId
   * @param supplierId
   * @return
   */
  public int getTotalCountOfPayable(Long shopId, Long supplierId);

  /**
   * 【付款历史记录】总数
   *
   * @param shopId
   * @param supplierId
   * @param startTime
   * @param endTime
   * @return
   * @throws ParseException
   */
  public int getTotalCountOfPayableHistoryRecord(Long shopId, String supplierId, String startTime, String endTime) throws ParseException;

  /**
   * 分页查询付款历史记录并排序
   *
   * @param shopId
   * @param supplierId
   * @param startTime
   * @param endTime
   * @param orderByName
   * @param orderByType
   * @param pager
   * @return
   * @throws ParseException
   */
  public List<PayableHistoryRecordDTO> getPayableHistoryRecord(Long shopId, String supplierId, String startTime, String endTime, String orderByName, String orderByType, Pager pager) throws ParseException;

  /**
   * 根据shopId, supplierId, 入库单Id得到相应PayableHistoryRecordDTO
   */
  public List<PayableHistoryRecordDTO> getPayableHistoryRecord(Long shopId, Long supplierId, Long inventoryId,PaymentTypes paymentTypes);

  /**
   * 退货给供应商
   *
   * @param purchaseReturnDTO
   */
  public void returnPayable(PurchaseReturnDTO purchaseReturnDTO);

  /**
   * 获得供应商定金
   *
   * @param shopId
   * @param supplierId
   * @return
   */
  public DepositDTO getDepositBySupplierId(Long shopId, Long supplierId);

  /**
   * 分别用定金的现金，银行卡，支票进行付款
   *
   * @param shopId
   * @param supplierId
   * @param creditAmount
   */
  public void paidByDepositFromDeposit(Long shopId, Long supplierId, Double creditAmount);

  /**
   * 更新用户定金
   *
   * @param depositDTO
   */
  public void updateDeposit(DepositDTO depositDTO);

  /**
   * 入库退货单作废, 结算相关逻辑.
   * @param purchaseReturnDTO
   */
  void returnPayableRepeal(PurchaseReturnDTO purchaseReturnDTO);

  /**
   * 查询退货款
   * @param shopId
   * @param purchaseReturnId
   */
  public Map<Long, SupplierReturnPayableDTO> getSupplierReturnPayableByPurchaseReturnId(Long shopId,Long... purchaseReturnId);
  /**
   * 保存应付款
   *
   * @param payableDTO
   * @return
   */
  public PayableDTO savePayable(PayableDTO payableDTO);

  /**
   * 付款给供应商
   *
   * @param payableDTO
   * @param purchaseInventoryDTO
   * @author zhangchuanlong
   */
  public void payedToSupplier(PayableDTO payableDTO, PurchaseInventoryDTO purchaseInventoryDTO,PaymentTypes paymentTypes);

  /**
   * 根据入库单作废相应的应付款表作废
   *
   * @param purchaseInventoryDTO
   */
  public void repealPayable(PurchaseInventoryDTO purchaseInventoryDTO);

  /**
   * 作废单流水统计专用统计专用
   * 付款历史记录(record)作废
   *
   * @param payableDTO
   */
  public void repealPayableHistoryRecord(PayableDTO payableDTO);

  /**
   * 更新付款历史记录
   *
   * @param payableHistoryRecordDTO
   */
  public void updatePayableHistoryRecordDTO(PayableHistoryRecordDTO payableHistoryRecordDTO);

  /**
   * @param shopId               店面ID
   * @param purchaserInventoryId 入库单ID
   * @param supplierId           供应商ID
   * @return
   */
  public PayableDTO getInventoryPayable(Long shopId, Long purchaserInventoryId, Long supplierId);

  /**
   * 入库单作废后还款
   *
   * @param purchaseInventoryDTO
   */
  public void returnPayable(PurchaseInventoryDTO purchaseInventoryDTO);

  /**
   * 根据供应商应付款保存收款记录
   * @param depositDTO
   */
  public void savePayableHistoryRecordFromDepositDTO(DepositDTO depositDTO);

  /**
   * 根据入库退货单保存流水统计记录
   * @param purchaseReturnDTO
   */
  public void savePayableAndRecordFromPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO, String materialName, PayableHistoryDTO payableHistoryDTO);

  /**
   * 查询退货款
   * @param shopId
   * @param purchaseReturnId
   */
  public SupplierReturnPayableDTO getSupplierReturnPayableByPurchaseReturnId(Long shopId,Long purchaseReturnId);


  public  List<PayableDTO> getPayables(RecOrPayIndexDTO recOrPayIndexDTO) ;



  /**
   * 查询此供应商下面所有记录
   *
   * @param supplierId
   * @return
   */
  public List<PayableDTO> searchPayable(Long shopId, Long supplierId) throws ParseException;

  public void paidByStrikeAmount(PayableHistoryDTO payableHistoryDTO, PayableDTO payableDTO, PaymentTypes paymentTypes);

  /**
   * 供应商结算结算前进行校验
   *
   * @param payableDTOList
   * @param payableHistoryDTO
   * @return
   */
  public String checkSupplierAccount(List<PayableDTO> payableDTOList, PayableHistoryDTO payableHistoryDTO);

  public PayableDTO getPayableDTOById(Long payableId);



  /**根据开始时间和结束时间查询收入记录
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   * @throws Exception
   */
  public List<PayableHistoryRecordDTO> getPayHistoryRecordByPayTime(long shopId,long startTime,long endTime);

  /**
   * 查找收入记录
   * @param shopId
   * @param supplierId
   * @param purchaseInventoryId
   * @return
   */
  public List<PayableHistoryRecord> getPayHistoryRecordListByIds(Long shopId, Long supplierId,Long purchaseInventoryId);

  /**
   * 根据开始时间 结束时间 查询收入记录
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   * @throws Exception
   */
  public List<ReceptionRecordDTO> getReceptionRecordByReceptionDate(long shopId,long startTime,long endTime);

  public SupplierRecordDTO getSupplierRecordDTOBySupplierId(Long shopId,Long supplierId);

  public void updateSupplierDebt(Long shopId,Long supplierId,double debt);

  //初始化用
  public int countSupplierReturnPayable();

  public List<SupplierReturnPayable> getSupplierReturnPayable(int size);

  Map<Long,List<Double>> getSumPayableMapBySupplierIdList(List<Long> supplierIdList, Long shopId, OrderDebtType debtType);

  public void fillSupplierTradeInfo(SupplierDTO supplierDTO);

  /**
   * 不排除REPEAL状态的Payable.
   * @param shopId
   * @param id
   * @return
   */
  PayableDTO getPayableDTOByOrderId(Long shopId, Long id);


  public List<Double> getPayableConsumeTimesBySupplierId(Long supplierId, Long shopId, OrderDebtType debtType);

  /**
   * 作废单流水统计专用统计专用 用作入库单作废
   * 付款历史记录(record)作废
   *
   * @param purchaseInventoryDTO
   */
  public RunningStatDTO  repealPayableHistoryRecordForInventory(PurchaseInventoryDTO purchaseInventoryDTO);


  /**
   * 通过shopId SupplierId relatedOrderId 查询预付款订单  入库单
   * @param shopId
   * @param supplierId
   * @param relatedOrderId
   * @return
   */
  public DepositOrderDTO getTotalDepositOrderByPurchaseInventoryInfo(Long shopId, Long supplierId, Long relatedOrderId);
}
