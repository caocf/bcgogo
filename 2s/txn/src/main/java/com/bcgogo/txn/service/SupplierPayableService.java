package com.bcgogo.txn.service;


import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.RecOrPayIndexDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-16
 * Time: 下午5:49
 * <p/>
 * 供应商应付款Service
 */
@Service
public class SupplierPayableService implements ISupplierPayableService {
  private static final Logger LOG = LoggerFactory.getLogger(SupplierPayableService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  /**
   * 保存定金    ，每个供应商只有一个定金记录
   *
   * @param depositDTO
   * @author zhangchuanlong
   */
  @Override
  public DepositDTO saveOrUpdateDeposit(DepositDTO depositDTO) {

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    Deposit deposit = null;
    try {
      //查找定金
      deposit = writer.getDepositBySupplierId(depositDTO.getShopId(), depositDTO.getSupplierId());
      if (deposit == null) {
        deposit = new Deposit(depositDTO);
        writer.save(deposit);
      } else {
        //定金累加
        deposit.setCheckNo(depositDTO.getCheckNo());
        deposit.setActuallyPaid(NumberUtil.numberValue(deposit.getActuallyPaid(), 0d) + NumberUtil.numberValue(depositDTO.getActuallyPaid(), 0d));
        deposit.setBankCardAmount(NumberUtil.numberValue(deposit.getBankCardAmount(), 0d) + NumberUtil.numberValue(depositDTO.getBankCardAmount(), 0d));
        deposit.setCash(NumberUtil.numberValue(deposit.getCash(), 0d) + NumberUtil.numberValue(depositDTO.getCash(), 0d));
        deposit.setCheckAmount(NumberUtil.numberValue(deposit.getCheckAmount(), 0d) + NumberUtil.numberValue(depositDTO.getCheckAmount(), 0d));
        deposit.setMemo(depositDTO.getMemo());
        writer.update(deposit);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return deposit.toDTO();
  }

  @Override
  public Result supplierDepositAdd(DepositDTO depositDTO) {
    Result result = new Result();
    result.setSuccess(false);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      DepositDTO resultDeposit = this.saveOrUpdateDeposit(depositDTO);
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      BeanUtils.copyProperties(depositDTO, depositOrderDTO, new String[]{"id", "payTime"});
      depositOrderDTO.setInOut(InOutFlag.IN_FLAG.getCode());
      depositOrderDTO.setDepositType(DepositType.DEPOSIT.getScene()); //预付金充值 场景
      depositOrderDTO.setOperator(depositDTO.getOperator());
      depositOrderDTO.setMemo(depositDTO.getMemo());
      writer.save(new DepositOrder(depositOrderDTO)); // 预售款取用记录增量式
      writer.commit(status);
      result.setSuccess(true);
      result.setData(resultDeposit.getActuallyPaid());
    } catch (Exception e) {
      LOG.error("supplierDepositAddError", e);
      return result;
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public boolean supplierDepositUse(DepositDTO depositDTO, DepositOrderDTO depositOrderDTO, TxnWriter writer) {
     if (depositDTO == null) {
      return false;
    }
    if (depositDTO.getActuallyPaid() == null) {
      return false;
    }
    TxnWriter tw;
    Object status = null;
    if (writer != null) {
      tw = writer;
    } else {
      tw = this.txnDaoManager.getWriter();
      status = tw.begin();
    }
    try {
      BeanUtils.copyProperties(depositDTO, depositOrderDTO, new String[]{"id", "payTime"});
      if (StringUtils.isNotBlank(depositDTO.getOperator()) && StringUtils.isBlank(depositOrderDTO.getOperator())) {
      depositOrderDTO.setOperator(depositDTO.getOperator());
      }
      Deposit currentDeposit = tw.getDepositBySupplierId(depositDTO.getShopId(), depositDTO.getSupplierId());
      DepositDTO curDepositDTO;
      if (currentDeposit == null) {
        //无预收款账户 新增
        curDepositDTO = new DepositDTO();
        curDepositDTO.setActuallyPaid(0.00);
        curDepositDTO.setCash(0.00);
        curDepositDTO.setBankCardAmount(0.00);
        curDepositDTO.setCheckAmount(0.00);
        curDepositDTO.setShopId(depositDTO.getShopId());
        curDepositDTO.setSupplierId(depositDTO.getSupplierId());
        curDepositDTO.setOperator(depositDTO.getOperator());
        currentDeposit = new Deposit();
      } else {
        curDepositDTO = currentDeposit.toDTO();
      }
        DepositType depositType = DepositType.getDepositTypeBySceneAndInOutFlag(depositOrderDTO.getDepositType(), InOutFlag.getInOutFlagEnumByCode(depositOrderDTO.getInOut()));
      // 根据使用场景（入库、入库退货、对账 等等） 设置预付款和预付款记录里面的字段值
      depositStategy(depositDTO, curDepositDTO, depositOrderDTO, depositType);
      // 金额累加（减）
        currentDeposit.FromDTO(supplierDepositAddUp(depositDTO, curDepositDTO, InOutFlag.getInOutFlagEnumByCode(depositOrderDTO.getInOut()))); //金额累加保存
      // 新增或者更新 预付款
      tw.saveOrUpdate(currentDeposit);
      // 新增预售款取用记录（增量式）
      tw.save(new DepositOrder(depositOrderDTO));

      if(status != null){
      tw.commit(status);
      }
    } catch (Exception e) {
      LOG.error("depositDTOUseError,the depositDTO is {" + depositDTO.toString() + "}", e);
      return false;
    } finally {
      if(status != null){
      tw.rollback(status);
    }
    }
    return true;
  }

  @Override
  public DepositOrderDTO queryDepositOrderByShopIdAndSupplierIdAndRelatedOrderId(Long shopId, Long supplierId, Long relatedOrderId) {
    TxnWriter tw = this.txnDaoManager.getWriter();
    return tw.queryDepositOrderByShopIdAndCustomerIdOrSupplierIdAndRelatedOrderId(shopId, null, supplierId, relatedOrderId).toDTO();
  }

  /**
   * 将当前充值金额累加持久化
   *
   * @param toBeAdd
   * @param currentDeposit
   * @return
   */
  private DepositDTO supplierDepositAddUp(DepositDTO toBeAdd, DepositDTO currentDeposit, InOutFlag inOutFlag) {
    if (toBeAdd == null) {
      LOG.error("customerDeposit should not be null.");
    }
    switch (inOutFlag) {
      case IN_FLAG:
        currentDeposit.setCash(moneyAddUp(toBeAdd.getCash(), currentDeposit.getCash()));
        currentDeposit.setBankCardAmount(moneyAddUp(toBeAdd.getBankCardAmount(), currentDeposit.getBankCardAmount()));
        currentDeposit.setCheckAmount(moneyAddUp(toBeAdd.getCheckAmount(), currentDeposit.getCheckAmount()));
        currentDeposit.setActuallyPaid(moneyAddUp(toBeAdd.getCash() + toBeAdd.getBankCardAmount() + toBeAdd.getCheckAmount(), currentDeposit.getActuallyPaid()));
        currentDeposit.setCheckNo(toBeAdd.getCheckNo());// 看支票号的历史 找充值订单 deposit_order 只存最近一次充值的支票号
        break;
      case OUT_FLAG:
        currentDeposit.setCash(moneyAddUp(-NumberUtil.numberValue(toBeAdd.getCash(), 0.00), NumberUtil.numberValue(currentDeposit.getCash(), 0.00)));
        currentDeposit.setBankCardAmount(moneyAddUp(-NumberUtil.numberValue(toBeAdd.getBankCardAmount(), 0.00), NumberUtil.numberValue(currentDeposit.getBankCardAmount(), 0.00)));
        currentDeposit.setCheckAmount(moneyAddUp(-NumberUtil.numberValue(toBeAdd.getCheckAmount(), 0.00), NumberUtil.numberValue(currentDeposit.getCheckAmount(), 0.00)));
        currentDeposit.setActuallyPaid(moneyAddUp(-NumberUtil.numberValue(toBeAdd.getCash(), 0.00) + (-NumberUtil.numberValue(toBeAdd.getBankCardAmount(), 0.00)) + (-NumberUtil.numberValue(toBeAdd.getCheckAmount(), 0.00)), currentDeposit.getActuallyPaid()));
        currentDeposit.setCheckNo(toBeAdd.getCheckNo());// 看支票号的历史 找充值订单 deposit_order 只存最近一次充值的支票号
        break;
      default:
    }
    // 金额分类累加

    return currentDeposit;
  }

   /**
   * 金额相加 返回current(金额累加操作 可以为负数) 精度为2位小数 四舍五入
   *
   * @param toBeAdd
   * @param current
   * @return
   */
  private Double moneyAddUp(Double toBeAdd, Double current) {
    current = NumberUtil.numberValue(current, 0.00) + NumberUtil.numberValue(toBeAdd, 0.00);
    return NumberUtil.round(current, NumberUtil.MONEY_PRECISION);
  }

  /**
   * 使用的时候目前depositOrderDTO 前端只传actually_paid
   * 默认扣款顺序为cash>bankAmount>cheque
   *
   * @param depositDTO
   * @param currentDepositDTO
   * @param depositOrderDTO
   * @param depositType
   */
  private void depositStategy(DepositDTO depositDTO, DepositDTO currentDepositDTO, DepositOrderDTO depositOrderDTO, DepositType depositType) {
    Double actually_paid = depositDTO.getActuallyPaid();
    Double currentCash = currentDepositDTO.getCash();
    Double currentBankAmount = currentDepositDTO.getBankCardAmount();
    Double currentCheque = currentDepositDTO.getCheckAmount();
    if (compareTwoDouble(actually_paid, 0.00) > 0) {
      switch (depositType) {
        case INVENTORY:
          if (compareTwoDouble(actually_paid, currentCash) == 0 || compareTwoDouble(actually_paid, currentCash) == -1) {
            depositDTO.setCash(actually_paid);
            depositDTO.setBankCardAmount(0.00);
            depositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(actually_paid);
            depositOrderDTO.setBankCardAmount(0.00);
            depositOrderDTO.setCheckAmount(0.00);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount) == -1) {
            depositDTO.setCash(currentCash);
            depositDTO.setBankCardAmount((actually_paid - currentCash));
            depositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount((actually_paid - currentCash));
            depositOrderDTO.setCheckAmount(0.00);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == -1) {
            depositDTO.setCash(currentCash);
            depositDTO.setBankCardAmount(currentBankAmount);
            depositDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount(currentBankAmount);
            depositOrderDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
          } else {
            String errorMsg = "预付款使用金额大于余额，异常。预付款使用金额为:" + actually_paid + ",当前余额为:" + (currentCash + currentBankAmount + currentCheque);
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
          }
          break;
        case INVENTORY_REPEAL:
          depositDTO.setCash(depositOrderDTO.getCash());
          depositDTO.setCheckAmount(depositOrderDTO.getCheckAmount());
          depositDTO.setBankCardAmount(depositOrderDTO.getBankCardAmount());
          depositDTO.setCheckNo(depositOrderDTO.getCheckNo());
          depositOrderDTO.setCash(depositOrderDTO.getCash());
          depositOrderDTO.setCheckAmount(depositOrderDTO.getCheckAmount());
          depositOrderDTO.setBankCardAmount(depositOrderDTO.getBankCardAmount());
          depositOrderDTO.setCheckNo(depositOrderDTO.getCheckNo());
          break;
        case INVENTORY_BACK:
          depositDTO.setCash(actually_paid); // 退货默认还现金
          depositDTO.setBankCardAmount(0.00);
          depositDTO.setCheckAmount(0.00);
          depositOrderDTO.setCash(actually_paid);
          break;
        case INVENTORY_BACK_REPEAL:
          depositDTO.setCash(actually_paid);
          depositDTO.setBankCardAmount(0d);
          depositDTO.setCheckAmount(0d);
          depositOrderDTO.setCash(actually_paid);
          break;
        case COMPARE:
           if (compareTwoDouble(actually_paid, currentCash) == 0 || compareTwoDouble(actually_paid, currentCash) == -1) {
            depositDTO.setCash(actually_paid);
            depositDTO.setBankCardAmount(0.00);
            depositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(actually_paid);
            depositOrderDTO.setBankCardAmount(0.00);
            depositOrderDTO.setCheckAmount(0.00);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount) == -1) {
            depositDTO.setCash(currentCash);
            depositDTO.setBankCardAmount((actually_paid - currentCash));
            depositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount((actually_paid - currentCash));
            depositOrderDTO.setCheckAmount(0.00);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == -1) {
            depositDTO.setCash(currentCash);
            depositDTO.setBankCardAmount(currentBankAmount);
            depositDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount(currentBankAmount);
            depositOrderDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
          } else {
            String errorMsg = "预付款使用金额大于余额，异常。预付款使用金额为:" + actually_paid + ",当前余额为:" + currentCash + currentBankAmount + currentCheque;
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
          }
          break;
        default:
      }

    }
  }

  private int compareTwoDouble(Double d1, Double d2) {
    BigDecimal bd1 = new BigDecimal(d1);
    BigDecimal bd2 = new BigDecimal(d2);
    return bd1.compareTo(bd2);
  }

  @Override
  public List<DepositOrderDTO> queryDepositOrdersByShopIdSupplierId(Long shopId, Long supplierId, Long inOut, SortObj sortObj, Pager pager) {
    if (shopId == null || supplierId == null) {
      return new ArrayList<DepositOrderDTO>();
    }
    TxnWriter tw = this.txnDaoManager.getWriter();
    List<DepositOrderDTO> depositOrderDTOs = null;

    try {
      List<DepositOrder> depositOrders = tw.queryDepositOrderByShopIdAndCustomerIdOrSupplierId(shopId, null, supplierId, buildInOutList(inOut), sortObj, pager);
      if (CollectionUtils.isEmpty(depositOrders))
        return new ArrayList<DepositOrderDTO>();
      depositOrderDTOs = new ArrayList<DepositOrderDTO>(depositOrders.size() + 1);
      for (DepositOrder depositOrder : depositOrders) {
        DepositOrderDTO depositOrderDTO = depositOrder.toDTO();
        depositOrderDTO.buildDepositType(); // 页面友好显示
        depositOrderDTO.setRelatedOrderIdStr(String.valueOf(depositOrder.getRelatedOrderId()));
        depositOrderDTOs.add(depositOrderDTO);
      }
      return depositOrderDTOs;
    } catch (Exception e) {
      LOG.error("queryDepositOrdersByShopIdCustomerIdError,shopId is {},customerId is{},stack is{}", new Object[]{shopId, supplierId, e});
      return new ArrayList<DepositOrderDTO>();
    }
  }

   private List<Long> buildInOutList(Long inOut) {
    List<Long> queryInOut = new ArrayList<Long>();
    if (inOut == 0L) {
      queryInOut.add(InOutFlag.IN_FLAG.getCode());
      queryInOut.add(InOutFlag.OUT_FLAG.getCode());
    } else {
      queryInOut.add(inOut);
    }
    return queryInOut;
  }

  @Override
  public int countDepositOrdersByShopIdSupplierId(Long shopId, Long supplierId, Long inOut) {
    if (shopId == null || supplierId == null) {
      return 0;
    }
    try {
      TxnWriter tw = this.txnDaoManager.getWriter();
      return tw.countDepositOrderByShopIdAndCustomerIdOrSupplierId(shopId, null, supplierId, buildInOutList(inOut));
    } catch (Exception e) {
      LOG.error("queryDepositOrdersByShopIdCustomerIdError", e);
      return 0;
    }
  }

  /**
   * 获得供应商定金
   *
   * @param shopId
   * @param supplierId
   * @return
   */
  @Override
  public DepositDTO getDepositBySupplierId(Long shopId, Long supplierId) {
    TxnWriter writer = txnDaoManager.getWriter();

    //查找定金
    Deposit deposit = writer.getDepositBySupplierId(shopId, supplierId);
    if (deposit == null) return null;
    return deposit.toDTO();
  }

  /**
   * 获得供应商应付款总额
   *
   * @param shopId
   * @return
   */
  @Override
  public double getTotalPayableByShopId(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getTotalPayableByShopId(shopId);
  }

  /**
   * 查询供应商应付款
   *
   * @param shopId
   * @param supplierId
   * @param fromTime
   * @param toTime
   * @param orderByType
   * @param orderName
   * @param pager
   * @return
   * @throws ParseException
   */
  @Override
  public List<PayableDTO> searchPayable(Long shopId, Long supplierId, String fromTime, String toTime, String orderByType, String orderName, Pager pager) throws ParseException {
    Long fromTimeLong = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, fromTime);
    Long toTimeLong = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, toTime);
    TxnWriter writer = txnDaoManager.getWriter();
    Sort sort = null;
    if (StringUtils.isNotEmpty(orderName) && StringUtils.isNotEmpty(orderByType)) {
      sort = new Sort(orderName, orderByType);
    }
    List<Payable> payables = writer.searchPayable(shopId, supplierId, fromTimeLong, toTimeLong, sort, pager);
    List<PayableDTO> payableDTOs = new ArrayList<PayableDTO>();
    for (Payable p : payables) {
      payableDTOs.add(p.toDTO());
    }
    return payableDTOs;
  }


  /**
   * 保存或者更新付款历史
   *
   * @param payableHistoryDTO
   */
  @Override
  public PayableHistoryDTO saveOrUpdatePayableHistory(PayableHistoryDTO payableHistoryDTO) {
    PayableHistoryDTO p = null;
    if (payableHistoryDTO.getId() == null) {
      p = this.savePayableHistory(payableHistoryDTO);
    } else {
      p = this.updatePayableHistory(payableHistoryDTO);
    }
    return p;
  }

  /**
   * 保存付款历史
   *
   * @param payableHistoryDTO
   */
  @Override
  public PayableHistoryDTO savePayableHistory(PayableHistoryDTO payableHistoryDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    PayableHistory payableHistory = new PayableHistory(payableHistoryDTO);
    try {
      writer.save(payableHistory);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return payableHistory.toDTO();
  }

  /**
   * 更新付款历史
   *
   * @param payableHistoryDTO
   */
  @Override
  public PayableHistoryDTO updatePayableHistory(PayableHistoryDTO payableHistoryDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    PayableHistory p = writer.findById(PayableHistory.class, payableHistoryDTO.getId());
    try {
      p.setShopId(payableHistoryDTO.getShopId());
      /* 扣款*/
      p.setDeduction(NumberUtil.numberValue(payableHistoryDTO.getDeduction(), 0D));
      /*欠款挂账*/
      p.setCreditAmount(NumberUtil.numberValue(payableHistoryDTO.getCreditAmount(), 0D));
      /*现金*/
      p.setCash(NumberUtil.numberValue(payableHistoryDTO.getCash(), 0D));
      /*银行卡*/
      p.setBankCardAmount(NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0D));
      /*支票*/
      p.setCheckAmount(NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0D));
      /*支票号码*/
      p.setCheckNo(payableHistoryDTO.getCheckNo());
      /*定金*/
      p.setDepositAmount(NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      /*实付*/
      p.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d));
      /*供应商ID*/
      p.setSupplierId(payableHistoryDTO.getSupplierId());
      p.setPayer(payableHistoryDTO.getPayer());
      p.setPayerId(payableHistoryDTO.getPayerId());
      writer.update(p);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return p.toDTO();
  }

  /**
   * 根据shopId获得总定金额
   *
   * @param shopId
   * @return
   * @author zhangchuanlong
   */
  @Override
  public float getTotaDepositByShopId(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getTotaDepositByShopId(shopId);
  }

  /**
   * 查询付款表 总数
   *
   * @param shopId
   * @param supplierId
   * @return
   */
  @Override
  public int searchPayable(Long shopId, Long supplierId, String fromTime, String toTime) throws ParseException {
    int totalCount = 0;
    Long fromTimeLong = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, fromTime);
    Long toTimeLong = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, toTime);
    TxnWriter writer = txnDaoManager.getWriter();
    totalCount = writer.countSearchPayable(shopId, supplierId, fromTimeLong, toTimeLong);
    return totalCount;
  }

  /**
   * 客户管理-->供应商列表显示应付款和定金
   *
   * @param listSupplierDTO
   * @return
   * @author zhangchuanlong
   */
  @Override
  public List<SupplierDTO> formListSupplierDTOByPayableAndDeposit(List<SupplierDTO> listSupplierDTO, Long shopId) {
    //应付款添加到listSupplierDTO
    this.formListSupplierDTOByPayable(listSupplierDTO, shopId);
    //定金添加到listSupplierDTO
    this.formListSupplierDTOByDeposit(listSupplierDTO, shopId);
    return listSupplierDTO;
  }

  /**
   * 应付款添加到listSupplierDTO
   *
   * @param listSupplierDTO
   * @param shopId
   * @return
   */
  @Override
  public void formListSupplierDTOByDeposit(List<SupplierDTO> listSupplierDTO, Long shopId) {
    if (CollectionUtils.isEmpty(listSupplierDTO)) {
      return;
    }
    for (SupplierDTO s : listSupplierDTO) {
      Double creditAmount = this.getSumDepositBySupplierId(Long.parseLong(s.getIdStr()), shopId);
      s.setDeposit(creditAmount);
    }
  }

  /**
   * 应付款添加到listSupplierDTO
   *
   * @param supplierId
   * @param shopId
   * @return
   */
  @Override
  public Double getSumDepositBySupplierId(Long supplierId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSumDepositBySupplierId(supplierId, shopId);
  }

  /**
   * 应付款添加到listSupplierDTO
   *
   * @param listSupplierDTO
   * @param shopId
   * @return
   */
  @Override
  public void formListSupplierDTOByPayable(List<SupplierDTO> listSupplierDTO, Long shopId) {
    if (CollectionUtils.isEmpty(listSupplierDTO)) {
      return;
    }
    for (SupplierDTO s : listSupplierDTO) {
      Double creditAmount = this.getSumPayableBySupplierId(s.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);
      s.setCreditAmount(creditAmount);
    }
  }

  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param supplierId
   * @param shopId
   * @param debtType :
   * @return    list.get(0):总欠款： SUPPLIER_DEBT_RECEIVABLE：应收；SUPPLIER_DEBT_PAYABLE：应付
   * list.get(1): 不可单独使用， debtType:SUPPLIER_DEBT_RECEIVABLE list（0）+ list(1):累计消费金额
   * list.get(1): 不可单独使用， debtType:SUPPLIER_DEBT_PAYABLE list（0）+ list(1):者累计退货金额
   */
  @Override
  public List<Double> getSumPayableBySupplierId(Long supplierId, Long shopId, OrderDebtType debtType) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Double> doubleList = writer.getSumPayableBySupplierId(supplierId, shopId, debtType);
    List<Double> statementOrderList = writer.getStatementOrderSumPayable(supplierId, shopId, debtType);

    List<Double> returnList = new ArrayList<Double>();
    returnList.add(doubleList.get(0));
    returnList.add(NumberUtil.toReserve(doubleList.get(1) - statementOrderList.get(0) - statementOrderList.get(1)));
    return returnList;

  }

  @Override
  public Double getSumReceivableByCustomerId(Long customerId, Long shopId, OrderDebtType debtType) {
      TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSumReceivableByCustomerId(customerId, shopId, debtType);
  }

  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param supplierIdList
   * @param shopId
   * @return
   */
  @Override
  public Map<Long, List<Double>> getSumPayableMapBySupplierIdList(List<Long> supplierIdList, Long shopId, OrderDebtType debtType) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object> objectList = writer.getSumPayableBySupplierIdList(supplierIdList, shopId, debtType);

    Map<Long, Double> map = new HashMap<Long, Double>();
    List<Object> list = writer.getStatementOrderSumPayable(supplierIdList, shopId, debtType);
    if (CollectionUtils.isNotEmpty(list)) {
      Object[] objects = null;
      for (Object object : list) {
        objects = (Object[]) object;
        if (!ArrayUtils.isEmpty(objects) && objects[0] != null) {
          double total = NumberUtil.doubleVal((Double) objects[1]) + NumberUtil.doubleVal((Double) objects[2]);
          map.put(Long.parseLong(objects[0].toString()), total);
        }
      }
    }


    Map<Long, List<Double>> payableMap = new HashMap<Long, List<Double>>();
    if (CollectionUtils.isNotEmpty(objectList)) {
      Object[] arrayObj = null;
      List<Double> doubleList = null;
      for (Object object : objectList) {
        arrayObj = (Object[]) object;
        if (!ArrayUtils.isEmpty(arrayObj) && arrayObj[0] != null) {
          doubleList = new ArrayList<Double>();
          doubleList.add(NumberUtil.doubleVal((Double) arrayObj[1]));
          if (map.containsKey(Long.valueOf(arrayObj[0].toString()))) {
            doubleList.add(NumberUtil.doubleVal((Double) arrayObj[2]) - map.get(Long.valueOf(arrayObj[0].toString())));
          } else {
            doubleList.add(NumberUtil.doubleVal((Double) arrayObj[2]));
          }
          payableMap.put(Long.parseLong(arrayObj[0].toString()), doubleList);
        }
      }
    }
    return payableMap;
  }

  /**
   * 付款给供应商
   *
   * @param payableDTOList    应付款ID
   * @param payableHistoryDTO 付款历史
   */
  @Override
  public List<PayableDTO> payedToSupplier(List<PayableDTO> payableDTOList, PayableHistoryDTO payableHistoryDTO, PaymentTypes paymentTypes) {
    if (payableHistoryDTO == null) {
      return null;
    }

    if (PaymentTypes.INVENTORY_DEBT == paymentTypes) {
      IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);

      runningStatService.runningStatFromPayableHistoryDTO(payableHistoryDTO, false);
    }
    List<Long> payableIds = new ArrayList<Long>();
    for (PayableDTO payableDTO : payableDTOList) {
      payableIds.add(Long.valueOf(payableDTO.getIdStr()));
    }
    //根据选定的IDs获得应付款记录列表
    List<PayableDTO> payableDTOs = this.getPayable(payableIds);
    //开始付款
    for (PayableDTO payableDTO : payableDTOs) {

      if (NumberUtil.doubleVal(payableHistoryDTO.getCash()) > 0) {
        //现金付款
        paidByCash(payableHistoryDTO, payableDTO, paymentTypes);
        if (payableHistoryDTO.getDeductionAndActuallyPaid() == 0) break;//实付与扣款之和为0停止付款
        if (payableDTO.getCreditAmount() == 0) continue;//挂帐为0，跳到下一张单据
      }


      if (NumberUtil.doubleVal(payableHistoryDTO.getBankCardAmount()) > 0) {
        //银行卡付款
        paidByBankCard(payableHistoryDTO, payableDTO, paymentTypes);
        if (payableHistoryDTO.getDeductionAndActuallyPaid() == 0) break;//实付与扣款之和为0停止付款
        if (payableDTO.getCreditAmount() == 0) continue;//挂帐为0，跳到下一张单据
      }


      if (NumberUtil.doubleVal(payableHistoryDTO.getCheckAmount()) > 0) {
        //支票付款
        paidByCheck(payableHistoryDTO, payableDTO, paymentTypes);
        if (payableHistoryDTO.getDeductionAndActuallyPaid() == 0) break;//实付与扣款之和为0停止付款
        if (payableDTO.getCreditAmount() == 0) continue;//挂帐为0，跳到下一张单据
      }


      if (NumberUtil.doubleVal(payableHistoryDTO.getDepositAmount()) > 0) {
        //使用定金付款
        paidByDeposit(payableHistoryDTO, payableDTO, paymentTypes);
        if (payableHistoryDTO.getDeductionAndActuallyPaid() == 0) break;//实付与扣款之和为0停止付款
        if (payableDTO.getCreditAmount() == 0) continue;//挂帐为0，跳到下一张单据
      }


      if (NumberUtil.doubleVal(payableHistoryDTO.getDeduction()) > 0) {
        //扣款付款
        paidBydeduction(payableHistoryDTO, payableDTO, paymentTypes);
        if (payableHistoryDTO.getDeductionAndActuallyPaid() == 0) break;//实付与扣款之和为0停止付款
        if (payableDTO.getCreditAmount() == 0) continue;//挂帐为0，跳到下一张单据
      }

      if (payableHistoryDTO.getDeductionAndActuallyPaid() == 0) {
        paidByCreditAmount(payableHistoryDTO, payableDTO, paymentTypes);
      }
    }
    return payableDTOs;
  }

  /**
   * 使用现金付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  @Override
  public void paidByCash(PayableHistoryDTO payableHistoryDTO, PayableDTO p, PaymentTypes paymentTypes) {
    PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(p.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
    if (payableHistoryRecordDTO == null) {
      payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setShopId(p.getShopId());
      payableHistoryRecordDTO.setSupplierId(p.getSupplierId());
      payableHistoryRecordDTO.setPayableId(p.getId());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
      payableHistoryRecordDTO.setMaterialName(p.getMaterialName());
      payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(p.getAmount(), 0d));
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d));
      payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
      payableHistoryRecordDTO.setActuallyPaid(0d);
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
    }
    payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setPaymentType(paymentTypes);
    payableHistoryRecordDTO.setPayer(payableHistoryDTO.getPayer());
    payableHistoryRecordDTO.setPayerId(payableHistoryDTO.getPayerId());
    //如果现金大于付款挂账，将此单据结算完，并进行下一张单据的结算
    if (payableHistoryDTO.getCash() >= p.getCreditAmount()) {
      //现金减少p.getCreditAmount()
      payableHistoryDTO.setCash(payableHistoryDTO.getCash() - p.getCreditAmount());
      payableHistoryRecordDTO.setCash(p.getCreditAmount());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setCash(NumberUtil.numberValue(p.getCash(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setCreditAmount(0d);
      payableHistoryRecordDTO.setCreditAmount(p.getCreditAmount());
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

    } else {     //如果现金小于于付款挂账，将现金付完，并用下一支付方式

      payableHistoryRecordDTO.setCash(payableHistoryDTO.getCash());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCash(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCash(), 0d));
      p.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getCash(), 0d));
      p.setCash(NumberUtil.numberValue(p.getCash(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCash(), 0d));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCash(), 0d));
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

      payableHistoryDTO.setCash(0d);
    }
    p.setLastPayer(payableHistoryDTO.getPayer());
    p.setLastPayerId(payableHistoryDTO.getPayerId());
    this.updatePayable(p);
  }

  /**
   * 银行卡付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  @Override
  public void paidByBankCard(PayableHistoryDTO payableHistoryDTO, PayableDTO p, PaymentTypes paymentTypes) {
    PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(p.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
    if (payableHistoryRecordDTO == null) {
      payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setShopId(payableHistoryDTO.getShopId());
      payableHistoryRecordDTO.setSupplierId(payableHistoryDTO.getSupplierId());
      payableHistoryRecordDTO.setPayableId(p.getId());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
      payableHistoryRecordDTO.setMaterialName(p.getMaterialName());
      payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(p.getAmount(), 0d));
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d));
      payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
      payableHistoryRecordDTO.setActuallyPaid(0d);
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
    }
    payableHistoryRecordDTO.setPayer(payableHistoryDTO.getPayer());
    payableHistoryRecordDTO.setPayerId(payableHistoryDTO.getPayerId());
    payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setPaymentType(paymentTypes);
    //如果银行卡大于付款挂账，将此单据结算完，并进行下一张单据的结算
    if (payableHistoryDTO.getBankCardAmount() >= p.getCreditAmount()) {
      //实付减少p.getCreditAmount()
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d) - NumberUtil.numberValue(p.getCreditAmount(), 0d)); //
      //银行卡减少p.getCreditAmount()
      payableHistoryDTO.setBankCardAmount(NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0d) - NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setBankCardAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0D) + NumberUtil.numberValue(p.getCreditAmount(), 0D));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0D) + NumberUtil.numberValue(p.getCreditAmount(), 0D));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0D) + NumberUtil.numberValue(p.getCreditAmount(), 0D));
      p.setBankCard(NumberUtil.numberValue(p.getBankCard(), 0D) + NumberUtil.numberValue(p.getCreditAmount(), 0D));
      p.setCreditAmount(0d);

      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0D));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);


    } else {     //如果银行卡小于于付款挂账，将现金付完，并用下一支付方式
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0d));
      payableHistoryRecordDTO.setBankCardAmount(NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0d));
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0D));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0D) + NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0D));
      p.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0d));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0d));
      p.setBankCard(NumberUtil.numberValue(p.getBankCard(), 0D) + NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0D));
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

      payableHistoryDTO.setBankCardAmount(0d);
    }
    p.setLastPayer(payableHistoryDTO.getPayer());
    p.setLastPayerId(payableHistoryDTO.getPayerId());
    this.updatePayable(p);
  }

  /**
   * 使用支票付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  @Override
  public void paidByCheck(PayableHistoryDTO payableHistoryDTO, PayableDTO p, PaymentTypes paymentTypes) {
    PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(p.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
    if (payableHistoryRecordDTO == null) {
      payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setShopId(payableHistoryRecordDTO.getShopId());
      payableHistoryRecordDTO.setSupplierId(payableHistoryRecordDTO.getSupplierId());
      payableHistoryRecordDTO.setPayableId(p.getId());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
      payableHistoryRecordDTO.setMaterialName(p.getMaterialName());
      payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(p.getAmount(), 0d));
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d));
       payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
      payableHistoryRecordDTO.setActuallyPaid(0d);
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
    }
    payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setPaymentType(paymentTypes);
    payableHistoryRecordDTO.setPayer(payableHistoryDTO.getPayer());
    payableHistoryRecordDTO.setPayerId(payableHistoryDTO.getPayerId());

    //如果现金大于付款挂账，将此单据结算完，并进行下一张单据的结算
    if (payableHistoryDTO.getCheckAmount() >= p.getCreditAmount()) {
      //实付减少p.getCreditAmount()
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d) - NumberUtil.numberValue(p.getCreditAmount(), 0d)); //
      //支票减少p.getCreditAmount()
      payableHistoryDTO.setCheckAmount(NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d) - NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setCheckAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setCheque(NumberUtil.numberValue(p.getCheque(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setCreditAmount(0d);
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);


    } else {     //如果支票小于于付款挂账，将现金付完，并用下一支付方式
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d));
      payableHistoryRecordDTO.setCheckAmount(NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d));
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d));
      p.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d));
      p.setCheque(NumberUtil.numberValue(p.getCheque(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0d));
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

      payableHistoryDTO.setCheckAmount(0d);
    }
    this.updatePayable(p);
  }

  /**
   * 使用定金付款    ，更新供应商定金
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  @Override
  public void paidByDeposit(PayableHistoryDTO payableHistoryDTO, PayableDTO p, PaymentTypes paymentTypes) {
//付款历史记录
    PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(p.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
    if (payableHistoryRecordDTO == null) {
      payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setShopId(payableHistoryDTO.getShopId());
      payableHistoryRecordDTO.setSupplierId(payableHistoryDTO.getSupplierId());
      payableHistoryRecordDTO.setPayableId(p.getId());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
      payableHistoryRecordDTO.setMaterialName(p.getMaterialName());
      payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(p.getAmount(), 0d));
       payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(0d);
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
    }
    payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setPaymentType(paymentTypes);
    payableHistoryRecordDTO.setPayer(payableHistoryDTO.getPayer());
    payableHistoryRecordDTO.setPayerId(payableHistoryDTO.getPayerId());

    //如果定金大于付款挂账，将此单据结算完，并进行下一张单据的结算
    if (payableHistoryDTO.getDepositAmount() >= p.getCreditAmount()) {
      //modified by zhuj
      //this.paidByDepositFromDeposit(p.getShopId(), p.getSupplierId(), p.getCreditAmount());              //定金表更新
      DepositDTO depositDTO = new DepositDTO();
      depositDTO.setShopId(p.getShopId());
      depositDTO.setSupplierId(p.getSupplierId());
      depositDTO.setActuallyPaid(p.getCreditAmount());
      depositDTO.setOperator(p.getLastPayer());
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      depositOrderDTO.setDepositType(DepositType.INVENTORY.getScene());
      depositOrderDTO.setActuallyPaid(NumberUtil.doubleVal(p.getCreditAmount()));
      depositOrderDTO.setOperator(p.getLastPayer());
      depositOrderDTO.setSupplierId(p.getSupplierId());
      depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
      depositOrderDTO.setShopId(p.getShopId());
      depositOrderDTO.setRelatedOrderId(p.getPurchaseInventoryId());
      depositOrderDTO.setRelatedOrderNo(p.getReceiptNo());
      boolean depositUseResult = this.supplierDepositUse(depositDTO, depositOrderDTO,null);
      if (!depositUseResult) {
        return;
      }

      //实付减少p.getCreditAmount()
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d) - NumberUtil.numberValue(p.getCreditAmount(), 0d)); //
      //定金减少p.getCreditAmount()
      payableHistoryDTO.setDepositAmount(NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d) - NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setDepositAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setDeposit(NumberUtil.numberValue(p.getDeposit(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setCreditAmount(0d);
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

    } else {     //如果定金小于于付款挂账，将现金付完，并用下一支付方式
      //modify by zhuj
      //this.paidByDepositFromDeposit(p.getShopId(), p.getSupplierId(), payableHistoryDTO.getDepositAmount());           //定金表更新
      DepositDTO depositDTO = new DepositDTO();
      depositDTO.setShopId(p.getShopId());
      depositDTO.setSupplierId(p.getSupplierId());
      depositDTO.setActuallyPaid(payableHistoryDTO.getDepositAmount());
      depositDTO.setOperator(p.getLastPayer());
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      depositOrderDTO.setDepositType(DepositType.INVENTORY.getScene());
      depositOrderDTO.setActuallyPaid(NumberUtil.doubleVal(payableHistoryDTO.getDepositAmount()));
      depositOrderDTO.setOperator(p.getLastPayer());
      depositOrderDTO.setSupplierId(p.getSupplierId());
      depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
      depositOrderDTO.setShopId(p.getShopId());
      depositOrderDTO.setRelatedOrderId(p.getPurchaseInventoryId());
      depositOrderDTO.setRelatedOrderNo(p.getReceiptNo());
      boolean depositUseResult = this.supplierDepositUse(depositDTO, depositOrderDTO ,null);
      if (!depositUseResult) {
        return;
      }
      // modify end
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      payableHistoryRecordDTO.setDepositAmount(NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      p.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      p.setDeposit(NumberUtil.numberValue(p.getDeposit(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0d));
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));

      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

      payableHistoryDTO.setDepositAmount(0d);
    }
    p.setLastPayer(payableHistoryDTO.getPayer());
    p.setLastPayerId(payableHistoryDTO.getPayerId());
    this.updatePayable(p);
  }

  /**
   * 分别用定金的现金，银行卡，支票进行付款
   *
   * @param shopId
   * @param supplierId
   * @param creditAmount
   */
  @Override
  public void paidByDepositFromDeposit(Long shopId, Long supplierId, Double creditAmount) {
    DepositDTO depositDTO = this.getDepositBySupplierId(shopId, supplierId);
    if (depositDTO == null) return;
    //用现金
    if (depositDTO.getCash() != 0) {
      if (depositDTO.getCash() < creditAmount) {
        creditAmount = creditAmount - depositDTO.getCash();
        depositDTO.setCash(0d);
      } else {
        depositDTO.setCash(depositDTO.getCash() - creditAmount);
        creditAmount = 0d;
      }
    }
    //如果现金不足 ，用银行卡付款
    if (creditAmount != 0) {
      if (depositDTO.getBankCardAmount() != null) {
        if (depositDTO.getBankCardAmount() < creditAmount) {
          creditAmount = creditAmount - depositDTO.getBankCardAmount();
          depositDTO.setBankCardAmount(0d);
        } else {
          depositDTO.setBankCardAmount(depositDTO.getBankCardAmount() - creditAmount);
          creditAmount = 0d;
        }
      }
    }
    //如果银行卡不足，用支票支付
    if (creditAmount != 0) {
      if (depositDTO.getCheckAmount() != null) {
        if (depositDTO.getCheckAmount() < creditAmount) {
          creditAmount = creditAmount - depositDTO.getCheckAmount();
          depositDTO.setCheckAmount(0d);
        } else {
          depositDTO.setCheckAmount(depositDTO.getCheckAmount() - creditAmount);
          creditAmount = 0d;
        }
      }
    }
    depositDTO.setActuallyPaid(depositDTO.getCash() + depositDTO.getBankCardAmount() + depositDTO.getCheckAmount());
    this.updateDeposit(depositDTO);
  }

  /**
   * 更新用户定金
   *
   * @param depositDTO
   */
  @Override
  public void updateDeposit(DepositDTO depositDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    //查找定金
    Deposit deposit = writer.getDepositBySupplierId(depositDTO.getShopId(), depositDTO.getSupplierId());
    try {
      //定金累加
      deposit.setCheckNo(depositDTO.getCheckNo());
      deposit.setActuallyPaid(depositDTO.getActuallyPaid());
      deposit.setBankCardAmount(depositDTO.getBankCardAmount());
      deposit.setCash(depositDTO.getCash());
      deposit.setCheckAmount(depositDTO.getCheckAmount());
      writer.update(deposit);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 使用扣款付款
   *
   * @param payableHistoryDTO
   * @param p
   * @return
   */
  @Override
  public void paidBydeduction(PayableHistoryDTO payableHistoryDTO, PayableDTO p, PaymentTypes paymentTypes) {
    PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(p.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
    if (payableHistoryRecordDTO == null) {
      payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setShopId(payableHistoryDTO.getShopId());
      payableHistoryRecordDTO.setSupplierId(payableHistoryDTO.getSupplierId());
      payableHistoryRecordDTO.setPayableId(p.getId());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
      payableHistoryRecordDTO.setMaterialName(p.getMaterialName());
      payableHistoryRecordDTO.setAmount(p.getAmount() == null ? 0 : p.getAmount());
       payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
      payableHistoryRecordDTO.setPaidAmount(p.getPaidAmount() == null ? 0 : p.getPaidAmount());
      payableHistoryRecordDTO.setActuallyPaid(0d);
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
    }
    payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setPaymentType(paymentTypes);
    payableHistoryRecordDTO.setPayer(payableHistoryDTO.getPayer());
    payableHistoryRecordDTO.setPayerId(payableHistoryDTO.getPayerId());

    //如果扣款大于付款挂账，将此单据结算完，并进行下一张单据的结算
    if (payableHistoryDTO.getDeduction() >= p.getCreditAmount()) {
      //实付减少p.getCreditAmount()
      payableHistoryDTO.setActuallyPaid(payableHistoryDTO.getActuallyPaid() - p.getCreditAmount()); //
      //扣款减少p.getCreditAmount()
      payableHistoryDTO.setDeduction(payableHistoryDTO.getDeduction() - p.getCreditAmount());
      payableHistoryRecordDTO.setDeduction(p.getCreditAmount());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
//      payableHistoryRecordDTO.setActuallyPaid(payableHistoryRecordDTO.getActuallyPaid() + p.getCreditAmount());
      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setDeduction(NumberUtil.numberValue(p.getDeduction(), 0d) + NumberUtil.numberValue(p.getCreditAmount(), 0d));
      p.setCreditAmount(0d);
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

    } else {     //如果扣款小于于付款挂账，将现金付完，并用下一支付方式
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getDeduction(), 0d));
      payableHistoryRecordDTO.setDeduction(NumberUtil.numberValue(payableHistoryDTO.getDeduction(), 0d));
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getDeduction(), 0d));
//      payableHistoryRecordDTO.setActuallyPaid(payableHistoryRecordDTO.getActuallyPaid() + payableHistoryDTO.getDeduction());
      p.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getDeduction(), 0d));
      p.setDeduction(NumberUtil.numberValue(p.getDeduction(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getDeduction(), 0d));
//      p.setPaidAmount(NumberUtil.numberValue(p.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getDeduction(), 0d));
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(p.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

      payableHistoryDTO.setDeduction(0d);
    }
    p.setLastPayer(payableHistoryDTO.getPayer());
    p.setLastPayerId(payableHistoryDTO.getPayerId());
    this.updatePayable(p);
  }

  /**
   * 当全部挂账时的付款
   */
  public void paidByCreditAmount(PayableHistoryDTO payableHistoryDTO, PayableDTO p, PaymentTypes paymentTypes) {
    PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(p.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
    if (payableHistoryRecordDTO == null) {
      payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setShopId(payableHistoryDTO.getShopId());
      payableHistoryRecordDTO.setSupplierId(payableHistoryDTO.getSupplierId());
      payableHistoryRecordDTO.setPayableId(p.getId());
      payableHistoryRecordDTO.setPurchaseInventoryId(p.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
      payableHistoryRecordDTO.setMaterialName(p.getMaterialName());
      payableHistoryRecordDTO.setAmount(p.getAmount() == null ? 0 : p.getAmount());
      payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
      payableHistoryRecordDTO.setPaidAmount(p.getPaidAmount() == null ? 0 : p.getPaidAmount());
      payableHistoryRecordDTO.setActuallyPaid(0d);
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
    }
    payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setPaymentType(paymentTypes);
    payableHistoryRecordDTO.setPayer(payableHistoryDTO.getPayer());
    payableHistoryRecordDTO.setPayerId(payableHistoryDTO.getPayerId());
    payableHistoryRecordDTO.setCash(0d);
    payableHistoryRecordDTO.setBankCardAmount(0d);
    payableHistoryRecordDTO.setDeduction(0d);
    payableHistoryRecordDTO.setCreditAmount(payableHistoryDTO.getCreditAmount());
    payableHistoryRecordDTO.setCheckAmount(0d);
    payableHistoryRecordDTO.setDepositAmount(0d);
    this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);
  }

  /**
   * 更新应付款表, 同时更新supplier_record表
   *
   * @param p
   */
  @Override
  public void updatePayable(PayableDTO p) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Payable payable = writer.findById(Payable.class, p.getId());
      payable = payable.fromDTO(p, false);
      writer.update(payable);
      writer.commit(status);

      Object newStatus = writer.begin();
      updateSupplierRecordTotalPayable(payable, writer);    //保证事务一致性
      writer.commit(newStatus);
    } finally {
      writer.rollback(status);
    }

  }

  /**
   * 根据payable信息将总应付款更新至SupplierRecord记录
   *
   * @param payable
   * @param writer
   */
  private void updateSupplierRecordTotalPayable(Payable payable, TxnWriter writer) {
    SupplierRecord supplierRecord = writer.getSupplierRecord(payable.getShopId(), payable.getSupplierId());
    if (supplierRecord == null) {
      supplierRecord = new SupplierRecord();
      supplierRecord.setShopId(payable.getShopId());
      supplierRecord.setSupplierId(payable.getSupplierId());
    }
    Double totalPayable = getSumPayableBySupplierId(supplierRecord.getSupplierId(), supplierRecord.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);
    supplierRecord.setCreditAmount(totalPayable);
    writer.saveOrUpdate(supplierRecord);
  }

  /**
   * 保存或者更新付款历史记录
   *
   * @param payableHistoryRecordDTO
   */
  @Override
  public PayableHistoryRecordDTO saveOrUpdatePayHistoryRecord(PayableHistoryRecordDTO payableHistoryRecordDTO){

    if (payableHistoryRecordDTO.getPaidTime() == null) {
      payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    }

    if (payableHistoryRecordDTO.getId() == null) {
      payableHistoryRecordDTO = this.savePayHistoryRecord(payableHistoryRecordDTO);
    } else {
      payableHistoryRecordDTO = this.updtePayHistoryRecord(payableHistoryRecordDTO);
    }
    return payableHistoryRecordDTO;
  }

  /**
   * 更新付款历史记录
   *
   * @param payableHistoryRecordDTO
   * @author zhangchuanlong
   */
  @Override
  public PayableHistoryRecordDTO updtePayHistoryRecord(PayableHistoryRecordDTO payableHistoryRecordDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    PayableHistoryRecord payableHistoryRecord = null;
    try {
      payableHistoryRecord = writer.findById(PayableHistoryRecord.class, payableHistoryRecordDTO.getId());
      /*店面ID*/
      payableHistoryRecord.setShopId(payableHistoryRecordDTO.getShopId());
      /*扣款*/
      payableHistoryRecord.setDeduction(payableHistoryRecordDTO.getDeduction());
      /*欠款挂账*/
      payableHistoryRecord.setCreditAmount(payableHistoryRecordDTO.getCreditAmount());
      /* 现金*/
      payableHistoryRecord.setCash(payableHistoryRecordDTO.getCash());
      /*  银行卡*/
      payableHistoryRecord.setBankCardAmount(payableHistoryRecordDTO.getBankCardAmount());
      /*支票*/
      payableHistoryRecord.setCheckAmount(payableHistoryRecordDTO.getCheckAmount());
      /*支票号码*/
      payableHistoryRecord.setCheckNo(payableHistoryRecordDTO.getCheckNo());
      /*用定金*/
      payableHistoryRecord.setDepositAmount(payableHistoryRecordDTO.getDepositAmount());
      /*  实付*/
      payableHistoryRecord.setActuallyPaid(payableHistoryRecordDTO.getActuallyPaid());
      /*采购入库单ID*/
      payableHistoryRecord.setPurchaseInventoryId(payableHistoryRecordDTO.getPurchaseInventoryId());
      /*结算历史ID*/
      payableHistoryRecord.setPayableHistoryId(payableHistoryRecordDTO.getPayableHistoryId());
      /*供应商ID*/
      payableHistoryRecord.setSupplierId(payableHistoryRecordDTO.getSupplierId());
      /*应付款ID*/
      payableHistoryRecord.setPayableId(payableHistoryRecordDTO.getPayableId());
      /*材料品名*/
      payableHistoryRecord.setMaterialName(payableHistoryRecordDTO.getMaterialName());
      /*已付金额*/
      payableHistoryRecord.setPaidAmount(payableHistoryRecordDTO.getPaidAmount());
      /*金额*/
      payableHistoryRecord.setAmount(payableHistoryRecordDTO.getAmount());
      /*状态*/
      payableHistoryRecord.setStatus(payableHistoryRecordDTO.getStatus());
      payableHistoryRecord.setPaymentType(payableHistoryRecordDTO.getPaymentType());
      payableHistoryRecord.setPayTime(payableHistoryRecordDTO.getPaidTime());
      writer.update(payableHistoryRecord);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return payableHistoryRecord.toDTO();
  }

  /**
   * 保存付款历史记录
   *
   * @param payableHistoryRecordDTO
   * @author zhangchuanlong
   */
  @Override
  public PayableHistoryRecordDTO savePayHistoryRecord(PayableHistoryRecordDTO payableHistoryRecordDTO){
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    PayableHistoryRecord payableHistoryRecord = null;
    try {
      payableHistoryRecord = new PayableHistoryRecord(payableHistoryRecordDTO);
      writer.save(payableHistoryRecord);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return payableHistoryRecord.toDTO();
  }

  /**
   * 根据入库单ID，付款历史获得付款历史记录
   *
   * @param purchaseInventoryId
   * @param payableHistoryDTOId
   * @param shopId
   * @return
   */
  @Override
  public PayableHistoryRecordDTO getPayHistoryRecord(Long purchaseInventoryId, Long payableHistoryDTOId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    PayableHistoryRecord p = writer.getPayHistoryRecord(purchaseInventoryId, payableHistoryDTOId, shopId);
    if (p == null) return null;
    return p.toDTO();
  }

  /**
   * 根据订单类型和订单ID查询该单据历史结算记录
   *
   * @param shopId
   * @param orderTypeEnum
   * @param orderId
   * @return
   */
  @Override
  public List<PayableHistoryRecordDTO> getSettledRecord(Long shopId, OrderTypes orderTypeEnum, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = new ArrayList<PayableHistoryRecordDTO>();

    if (orderTypeEnum == null || (!OrderTypes.RETURN.equals(orderTypeEnum) && !OrderTypes.INVENTORY.equals(orderTypeEnum))) {
       return null;
    }
    List<PayableHistoryRecord> payableHistoryRecords = writer.getSettledRecord(shopId, orderTypeEnum, orderId);
    if (!com.bcgogo.utils.CollectionUtil.isNotEmpty(payableHistoryRecords)) {
       return null;
    }
    for (PayableHistoryRecord payableHistoryRecord : payableHistoryRecords) {
      PayableHistoryRecordDTO payableHistoryRecordDTO = payableHistoryRecord.toDTO();
      if (PaymentTypes.STATEMENT_ACCOUNT.equals(payableHistoryRecordDTO.getPaymentType())) {
         payableHistoryRecordDTO.setStatementAccountFlag(true);
      }
      payableHistoryRecordDTOs.add(payableHistoryRecordDTO);
    }
    return payableHistoryRecordDTOs;
  }

  /**
   * 根据多个ID获得应付款记录
   *
   * @param payableIds
   * @return
   */
  @Override
  public List<PayableDTO> getPayable(List<Long> payableIds) {
    List<PayableDTO> payableDTOs = new ArrayList<PayableDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    if (CollectionUtils.isEmpty(payableIds)) {
      return null;
    }
    for (Long id : payableIds) {
      Payable p = writer.findById(Payable.class, id);
      payableDTOs.add(p.toDTO());
    }
    return payableDTOs;
  }

  /**
   * 根据shopId,supplierId获得供应商总付款数
   *
   * @param shopId
   * @param supplierId
   * @return
   */
  @Override
  public int getTotalCountOfPayable(Long shopId, Long supplierId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getTotalCountOfPayable(supplierId, shopId);
  }

  /**
   * 付款历史记录 总数
   *
   * @param shopId
   * @param supplierId
   * @param startTimeStr
   * @param endTimeStr
   * @return
   */
  @Override
  public int getTotalCountOfPayableHistoryRecord(Long shopId, String supplierId, String startTimeStr, String endTimeStr) throws ParseException {
    TxnWriter writer = txnDaoManager.getWriter();
    if (StringUtils.isEmpty(supplierId)) return 0;
    Long supplierIdLong = Long.parseLong(supplierId);
    Long startTime = null;
    Long endTime = null;
    if (!StringUtil.isEmpty(startTimeStr)) {
      //开始时间按该天0点算，左闭右开
      startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startTimeStr);
    }
    if (!StringUtil.isEmpty(endTimeStr)) {
      //结束时间按第二天的0点算，左闭右开
      endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endTimeStr);
      endTime = endTime + 24 * 3600 * 1000 - 1;
    }
    return writer.getTotalCountOfPayableHistoryRecord(shopId, supplierIdLong, startTime, endTime);
  }

  /**
   * 分页查询付款历史记录并排序
   *
   * @param shopId
   * @param supplierId  供应商ID
   * @param startTimeStr   开始时间
   * @param endTimeStr     结束时间
   * @param orderByName 排序字段
   * @param orderByType 排序方式
   * @param pager       分页
   * @return
   */
  @Override
  public List<PayableHistoryRecordDTO> getPayableHistoryRecord(Long shopId, String supplierId, String startTimeStr, String endTimeStr, String orderByName, String orderByType, Pager pager) throws ParseException {
    Long supplierIdLong = Long.parseLong(supplierId);
    Long startTime = null;
    Long endTime = null;
    if (!StringUtil.isEmpty(startTimeStr)) {
      //开始时间按该天0点算，左闭右开
      startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startTimeStr);
    }
    if (!StringUtil.isEmpty(endTimeStr)) {
      //结束时间按第二天的0点算，左闭右开
      endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endTimeStr);
      endTime = endTime + 24 * 3600 * 1000 - 1;
    }
    if (StringUtils.isEmpty(supplierId)) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    Sort sort = null;
    if (StringUtils.isNotEmpty(orderByName) && StringUtils.isNotEmpty(orderByType)) {
      sort = new Sort(orderByName, orderByType);
    }
    List<PayableHistoryRecord> payableHistoryRecords = writer.getPayableHistoryRecord(shopId, supplierIdLong, startTime, endTime, sort, pager);
    if (payableHistoryRecords == null) return null;
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = new ArrayList<PayableHistoryRecordDTO>();
    PayableHistoryRecordDTO payableHistoryRecordDTO = null;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPurchaseReturnService purchaseReturnService = ServiceManager.getService(IPurchaseReturnService.class);
    for (PayableHistoryRecord p : payableHistoryRecords) {
      payableHistoryRecordDTO = p.toDTO();
      if ((p.getPaymentType() == PaymentTypes.INVENTORY || p.getPaymentType() == PaymentTypes.INVENTORY_DEBT
          || p.getPaymentType() == PaymentTypes.INVENTORY_REPEAL) && p.getPurchaseInventoryId() != null) {
        PurchaseInventoryDTO purchaseInventoryDTO = txnService.getPurchaseInventoryById(p.getPurchaseInventoryId(), shopId);
        if (purchaseInventoryDTO != null) {
          payableHistoryRecordDTO.setOrderType(OrderTypes.INVENTORY.toString());
          payableHistoryRecordDTO.setReceiptNo(purchaseInventoryDTO.getReceiptNo());
    }
      } else if ((p.getPaymentType() == PaymentTypes.INVENTORY_RETURN || p.getPaymentType() == PaymentTypes.INVENTORY_RETURN_CASH
          || p.getPaymentType() == PaymentTypes.INVENTORY_RETURN_DEPOSIT || p.getPaymentType() == PaymentTypes.INVENTORY_RETURN_REPEAL)
          && p.getPurchaseInventoryId() != null) {
        PurchaseReturnDTO purchaseReturnDTO = purchaseReturnService.getPurchaseReturnById(p.getPurchaseInventoryId(), shopId);
        if (purchaseReturnDTO != null) {
          payableHistoryRecordDTO.setOrderType(OrderTypes.RETURN.toString());
          payableHistoryRecordDTO.setReceiptNo(purchaseReturnDTO.getReceiptNo());
        }
      }
      payableHistoryRecordDTOs.add(payableHistoryRecordDTO);
    }
    return payableHistoryRecordDTOs;

  }

  @Override
  public List<PayableHistoryRecordDTO> getPayableHistoryRecord(Long shopId, Long supplierId, Long inventoryId, PaymentTypes paymentTypes) {
    if (shopId == null || inventoryId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<PayableHistoryRecord> payableHistoryRecords = writer.getPayableHistoryRecord(shopId, supplierId, inventoryId, paymentTypes);
    if (CollectionUtils.isEmpty(payableHistoryRecords)) return null;
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = new ArrayList<PayableHistoryRecordDTO>();
    for (PayableHistoryRecord p : payableHistoryRecords) {
      payableHistoryRecordDTOs.add(p.toDTO());
    }
    return payableHistoryRecordDTOs;
  }

  /**
   * 退货还款
   *
   * @param purchaseReturnDTO
   */
  @Override
  public void returnPayable(PurchaseReturnDTO purchaseReturnDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    if (purchaseReturnDTO == null) return;
    StringBuffer materialName = new StringBuffer();
    PurchaseReturnItemDTO purchaseReturnItemDTO[] = purchaseReturnDTO.getItemDTOs();
    for (PurchaseReturnItemDTO p : purchaseReturnItemDTO) {
      materialName.append(p.getProductName()).append("；");
    }

    PayableHistoryDTO payableHistoryDTO = purchaseReturnDTO.toPayableHistoryDTO();
    payableHistoryDTO = this.savePayableHistory(payableHistoryDTO);

    SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
//    if(supplierDTO.getCustomerId() != null)  {
//        CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId()).get(0);
//        customerRecordDTO.setTotalReceivable(customerRecordDTO.getTotalReceivable() - payableHistoryDTO.getCreditAmount());
//        try {
//            ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
//
//        }catch(Exception e) {
//            LOG.info(e.getMessage(),e);
//        }
//
//    }
//    if (PayMethod.PURCHASE_RETURN_DEPOSIT.getValue().equals(purchaseReturnDTO.getReturnPayableType())) {
      //退入定金, 默认退入定金中的cash
    if (null != purchaseReturnDTO.getDepositAmount() && purchaseReturnDTO.getDepositAmount() > 0) {
      /*DepositDTO depositDTO = new DepositDTO();
      depositDTO.setShopId(purchaseReturnDTO.getShopId());
      depositDTO.setSupplierId(purchaseReturnDTO.getSupplierId());
      depositDTO.setCash(purchaseReturnDTO.getDepositAmount());
      depositDTO.setActuallyPaid(purchaseReturnDTO.getDepositAmount());
      this.saveOrUpdateDeposit(depositDTO);*/

      // add by zhuj
      DepositDTO depositDTO = new DepositDTO();
      depositDTO.setShopId(purchaseReturnDTO.getShopId());
      depositDTO.setSupplierId(purchaseReturnDTO.getSupplierId());
      depositDTO.setActuallyPaid(purchaseReturnDTO.getDepositAmount());
      depositDTO.setOperator(purchaseReturnDTO.getUserName());
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      depositOrderDTO.setInOut(InOutFlag.IN_FLAG.getCode());
      depositOrderDTO.setDepositType(DepositType.INVENTORY_BACK.getScene());
      depositOrderDTO.setRelatedOrderId(purchaseReturnDTO.getId());
      depositOrderDTO.setRelatedOrderNo(purchaseReturnDTO.getReceiptNo());
      this.supplierDepositUse(depositDTO, depositOrderDTO,null);
    }

    //保存退货款入库退货单记录
    this.savePayableAndRecordFromPurchaseReturnDTO(purchaseReturnDTO, materialName.toString(), payableHistoryDTO);

    if (purchaseReturnDTO.getAccountDebtAmount() > 0) {
      this.updateSupplierDebt(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplierId(), purchaseReturnDTO.getAccountDebtAmount());
      //更新remind_event的deleted_type
      writer.updateDebtRemindDeletedType(purchaseReturnDTO.getShopId(),purchaseReturnDTO.getSupplierId(),"supplier",DeletedType.FALSE);
      //保存欠款提醒事件
      Object status = writer.begin();
      try {
        ServiceManager.getService(ITxnService.class).saveRemindEvent(supplierDTO,purchaseReturnDTO,payableHistoryDTO.getPayerId(),writer);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }

  }

  public void returnPayableRepeal(PurchaseReturnDTO purchaseReturnDTO){
    PayableDTO payableDTO = ServiceManager.getService(ITxnService.class).getPayableDTOByOrderId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());
    if (payableDTO == null) return;
    purchaseReturnDTO.setPayableDTO(payableDTO);
    /*应付款作废*/
    payableDTO.setStatus(PayStatus.REPEAL);
    this.updatePayable(payableDTO);
    /*付款记录作废*/
    this.repealPayableHistoryRecord(payableDTO);
    //处理预付款相关逻辑
    if(Math.abs(payableDTO.getDeposit())>0){
      DepositDTO depositDTO = getDepositBySupplierId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplierId());
      DepositOrderDTO depositOrderDTO = queryDepositOrderByShopIdAndSupplierIdAndRelatedOrderId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplierId(), purchaseReturnDTO.getId());
      if (InOutFlag.getInOutFlagEnumByCode(depositOrderDTO.getInOut()) == InOutFlag.IN_FLAG) {
        depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
      }
      if (DepositType.getDepositTypeBySceneAndInOutFlag(depositOrderDTO.getDepositType(), InOutFlag.IN_FLAG) != null) {
        depositOrderDTO.setDepositType(DepositType.INVENTORY_BACK_REPEAL.getScene());
      }
      depositDTO.setOperator(purchaseReturnDTO.getUserName());
      depositDTO.setCash(depositOrderDTO.getCash());
      depositDTO.setBankCardAmount(depositOrderDTO.getBankCardAmount());
      depositDTO.setCheckAmount(depositOrderDTO.getCheckAmount());
      depositDTO.setActuallyPaid(depositOrderDTO.getActuallyPaid());
      supplierDepositUse(depositDTO, depositOrderDTO, null);
    }
  }


  public Map<Long, SupplierReturnPayableDTO> getSupplierReturnPayableByPurchaseReturnId(Long shopId, Long... purchaseReturnId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long, SupplierReturnPayableDTO> supplierReturnPayableDTOMap = new HashMap<Long, SupplierReturnPayableDTO>();
    if (purchaseReturnId != null && purchaseReturnId.length > 0) {
      List<SupplierReturnPayable> supplierReturnPayableList = writer.getSupplierReturnPayableByPurchaseReturnId(shopId, purchaseReturnId);
      if (CollectionUtils.isNotEmpty(supplierReturnPayableList)) {
        for (SupplierReturnPayable supplierReturnPayable : supplierReturnPayableList) {
          supplierReturnPayableDTOMap.put(supplierReturnPayable.getPurchaseReturnId(), supplierReturnPayable.toSupplierReturnPayableDTO());
        }
      }
    }
    return supplierReturnPayableDTOMap;
  }

  /**
   * 保存应付款
   *
   * @param payableDTO
   * @return
   */
  @Override
  public PayableDTO savePayable(PayableDTO payableDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    Payable payable = new Payable(payableDTO);
    try {
      writer.save(payable);
      writer.commit(status);
      payableDTO.setId(payable.getId());
    } finally {
      writer.rollback(status);
    }
    return payable.toDTO();
  }

  /**
   * 付款给供应商
   *
   * @param payableDTO
   * @param purchaseInventoryDTO
   * @author zhangchuanlong
   */
  @Override
  public void payedToSupplier(PayableDTO payableDTO, PurchaseInventoryDTO purchaseInventoryDTO, PaymentTypes paymentTypes) {
    PayableHistoryDTO payableHistoryDTO = new PayableHistoryDTO();
    payableHistoryDTO.setPayTime(purchaseInventoryDTO.getVestDate());
    try {

//    if (PayMethod.PURCHASE_RETURN_SURPAY.getValue().equals(purchaseInventoryDTO.getPaidtype())) {         //如果是付款详细页面进行付款
      /* 扣款*/
      payableHistoryDTO.setDeduction(NumberUtil.numberValue(purchaseInventoryDTO.getDeduction(), 0D));
      /*欠款挂账*/
      payableHistoryDTO.setCreditAmount(NumberUtil.numberValue(purchaseInventoryDTO.getCreditAmount(), 0D));
      /*现金*/
      payableHistoryDTO.setCash(NumberUtil.numberValue(purchaseInventoryDTO.getCash(), 0D));
      /*实付*/
      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(purchaseInventoryDTO.getActuallyPaid(), 0D));
//    } else {    //如果是入库单页面进行付款
//      /* 入库单页面扣款*/
//      payableHistoryDTO.setDeduction(NumberUtil.numberValue(purchaseInventoryDTO.getStroageSupplierDeduction(), 0d));
//      /*入库单页面欠款挂账*/
//      payableHistoryDTO.setCreditAmount(NumberUtil.numberValue(purchaseInventoryDTO.getStroageCreditAmount(), 0D));
//      /*入库单页面现金*/
//      payableHistoryDTO.setCash(NumberUtil.numberValue(purchaseInventoryDTO.getStroageActuallyPaid(), 0D));
//      /*实付*/
//      payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(purchaseInventoryDTO.getStroageActuallyPaid(), 0D));
//    }
      /*银行卡*/
      payableHistoryDTO.setBankCardAmount(NumberUtil.numberValue(purchaseInventoryDTO.getBankCardAmount(), 0D));
      /*支票*/
      payableHistoryDTO.setCheckAmount(NumberUtil.numberValue(purchaseInventoryDTO.getCheckAmount(), 0d));
      /*支票号码*/
      payableHistoryDTO.setCheckNo(purchaseInventoryDTO.getCheckNo());
      /*定金*/
      payableHistoryDTO.setDepositAmount(NumberUtil.numberValue(purchaseInventoryDTO.getDepositAmount(), 0D));
      /*店面ID*/
      payableHistoryDTO.setShopId(payableDTO.getShopId());
      /*供应商ID*/
      payableHistoryDTO.setSupplierId(payableDTO.getSupplierId());

      payableHistoryDTO.setStrikeAmount(payableDTO.getStrikeAmount());
      payableHistoryDTO.setPayer(purchaseInventoryDTO.getUserName());
      payableHistoryDTO.setPayerId(purchaseInventoryDTO.getUserId());
      //保存付款历史
      payableHistoryDTO = this.savePayableHistory(payableHistoryDTO);

      PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(payableDTO.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
      if (payableHistoryRecordDTO == null) {
        payableHistoryRecordDTO = new PayableHistoryRecordDTO();
        payableHistoryRecordDTO.setShopId(payableDTO.getShopId());
        payableHistoryRecordDTO.setSupplierId(payableDTO.getSupplierId());
        payableHistoryRecordDTO.setPayableId(payableDTO.getId());
        payableHistoryRecordDTO.setPurchaseInventoryId(purchaseInventoryDTO.getId());
        payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
        payableHistoryRecordDTO.setMaterialName(payableDTO.getMaterialName());
        payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(payableDTO.getAmount(), 0d));
        payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableDTO.getPaidAmount(), 0d));
        payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
        payableHistoryRecordDTO.setStatus(PayStatus.USE);
      }
      payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
      payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
      if (StringUtil.isNotEmpty(purchaseInventoryDTO.getAccountDateStr()) && DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, purchaseInventoryDTO.getAccountDateStr()) > 0) {
        payableHistoryRecordDTO.setPaidTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, purchaseInventoryDTO.getAccountDateStr()));
      }

      payableHistoryRecordDTO.setDeduction(payableHistoryDTO.getDeduction());
      payableHistoryRecordDTO.setCreditAmount(payableHistoryDTO.getCreditAmount());
      payableHistoryRecordDTO.setPaymentType(paymentTypes);
      payableHistoryRecordDTO.setCash(payableHistoryDTO.getCash());
      payableHistoryRecordDTO.setBankCardAmount(payableHistoryDTO.getBankCardAmount());
      payableHistoryRecordDTO.setCheckAmount(payableHistoryDTO.getCheckAmount());
      payableHistoryRecordDTO.setDepositAmount(payableHistoryDTO.getDepositAmount());
      payableHistoryRecordDTO.setActuallyPaid(payableHistoryDTO.getCash() + payableHistoryDTO.getBankCardAmount() + payableHistoryDTO.getCheckAmount() + payableHistoryDTO.getDepositAmount());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d));
      payableHistoryRecordDTO.setPaymentType(paymentTypes);
      payableHistoryRecordDTO.setPayer(purchaseInventoryDTO.getUserName());
      payableHistoryRecordDTO.setPayerId(purchaseInventoryDTO.getUserId());
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

      //更新供应商订金
      //this.paidByDepositFromDeposit(payableDTO.getShopId(),payableDTO.getSupplierId(),NumberUtil.doubleVal(purchaseInventoryDTO.getDepositAmount()));
      if (NumberUtil.doubleVal(purchaseInventoryDTO.getDepositAmount()) > 0.001) {
      DepositDTO depositDTO = new DepositDTO();
      depositDTO.setShopId(payableDTO.getShopId());
      depositDTO.setSupplierId(payableDTO.getSupplierId());
        depositDTO.setActuallyPaid(payableDTO.getDeposit());
        depositDTO.setOperator(purchaseInventoryDTO.getUserName());
      //add by zhuj 记录预付款订单
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      depositOrderDTO.setDepositType(DepositType.INVENTORY.getScene());
      depositOrderDTO.setActuallyPaid(NumberUtil.doubleVal(purchaseInventoryDTO.getDepositAmount()));
      depositOrderDTO.setOperator(purchaseInventoryDTO.getUserName());
      depositOrderDTO.setSupplierId(payableDTO.getSupplierId());
      depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
      depositOrderDTO.setShopId(payableDTO.getShopId());
      depositOrderDTO.setRelatedOrderId(purchaseInventoryDTO.getId());
      depositOrderDTO.setRelatedOrderNo(purchaseInventoryDTO.getReceiptNo());
        this.supplierDepositUse(depositDTO, depositOrderDTO,null);
      }

    } catch (Exception e) {
      LOG.error("payToSupplierError", e);
      return;
    }
  }

  /**
   * 根据入库单作废相应的应付款表作废
   *
   * @param purchaseInventoryDTO
   */
  @Override
  public void repealPayable(PurchaseInventoryDTO purchaseInventoryDTO) {
    PayableDTO payableDTO = this.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
    if (payableDTO == null) return;
    purchaseInventoryDTO.setPayableDTO(payableDTO);
    /*应付款作废*/
    payableDTO.setStatus(PayStatus.REPEAL);
    this.updatePayable(payableDTO);
    /*付款记录作废*/
    this.repealPayableHistoryRecord(payableDTO);
  }

  /**
   * 作废单统计专用
   * 付款历史记录(record)作废和流水统计
   *
   * @param payableDTO
   */
  @Override
  public void repealPayableHistoryRecord(PayableDTO payableDTO) {
    try {
      TxnWriter writer = txnDaoManager.getWriter();

      List<PayableHistoryRecord> payableHistoryRecordList = writer.getPayHistoryRecordListByIds(payableDTO.getShopId(), payableDTO.getSupplierId(), payableDTO.getPurchaseInventoryId());
      if (CollectionUtils.isEmpty(payableHistoryRecordList)) {
        return;
      }

      //判断作废时间和 流水日期是否是同一天
      boolean isCurrentDay = true;


      double debtNewExpenditure = 0.0; //这个单据产生的新增欠款
      double deduction = 0.0;//折扣
      double creditAmount = 0.0;   //欠款
      double cash = 0.0;//现金
      double bankCardAmount = 0.0;//银联
      double checkAmount = 0.0; //支票
      double depositAmount = 0.0;  //定金
      double actuallyPaid = 0.0;//实付金额
      double debtWithdrawalExpenditure = 0.0;//供应商欠款回笼总和
      double amount = 0;
      double supplierDebtDiscount = 0.0;//供应商欠款折扣
      double strikeAmount = 0.0;//退货冲账

      PayableHistoryRecord newPayableHistoryRecord = new PayableHistoryRecord();
      for (int index = 0; index < payableHistoryRecordList.size(); index++) {
        PayableHistoryRecord payableHistoryRecord = payableHistoryRecordList.get(index);
        PaymentTypes repealPaymentType = null;
        if (payableHistoryRecord.getPaymentType() == PaymentTypes.INVENTORY || payableHistoryRecord.getPaymentType() == PaymentTypes.INVENTORY_DEBT) {
          repealPaymentType = PaymentTypes.INVENTORY_REPEAL;
        } else if (payableHistoryRecord.getPaymentType() == PaymentTypes.INVENTORY_RETURN || payableHistoryRecord.getPaymentType() == PaymentTypes.INVENTORY_RETURN_CASH
            || payableHistoryRecord.getPaymentType() == PaymentTypes.INVENTORY_RETURN_DEPOSIT) {
          repealPaymentType = PaymentTypes.INVENTORY_RETURN_REPEAL;
        }
        payableHistoryRecord.setStatus(PayStatus.REPEAL);

        if (!DateUtil.isSameDay(System.currentTimeMillis(), NumberUtil.longValue(payableHistoryRecord.getPayTime()))) {
          isCurrentDay = false;
        }

//        //拿到单据结算时欠款
//        if (index == payableHistoryRecordList.size() - 1) {
//          debtNewExpenditure = NumberUtil.doubleVal(payableHistoryRecord.getCreditAmount());
//        }
        if (index == 0) {
          amount = payableHistoryRecord.getAmount();
          /*店面ID*/
          newPayableHistoryRecord.setShopId(payableHistoryRecord.getShopId());
          /*材料品名*/
          newPayableHistoryRecord.setMaterialName(payableHistoryRecord.getMaterialName());
          /*采购入库单ID*/
          newPayableHistoryRecord.setPurchaseInventoryId(payableHistoryRecord.getPurchaseInventoryId());
          /*供应商ID*/
          newPayableHistoryRecord.setSupplierId(payableHistoryRecord.getSupplierId());
          /*应付款ID*/
          newPayableHistoryRecord.setPayableId(payableHistoryRecord.getPayableId());
          newPayableHistoryRecord.setAmount(0 - NumberUtil.numberValue(payableHistoryRecord.getAmount(), 0D));
          newPayableHistoryRecord.setPaymentType(repealPaymentType);
          newPayableHistoryRecord.setStatus(PayStatus.REPEAL);
          newPayableHistoryRecord.setPaidAmount(0 - payableHistoryRecord.getPaidAmount());
          newPayableHistoryRecord.setPayTime(System.currentTimeMillis());
          newPayableHistoryRecord.setPayer(payableHistoryRecord.getPayer());
          newPayableHistoryRecord.setPayerId(payableHistoryRecord.getPayerId());
        }
        deduction += NumberUtil.doubleVal(payableHistoryRecord.getDeduction());
        cash += NumberUtil.doubleVal(payableHistoryRecord.getCash());
        bankCardAmount += NumberUtil.doubleVal(payableHistoryRecord.getBankCardAmount());
        checkAmount += NumberUtil.doubleVal(payableHistoryRecord.getCheckAmount());
        depositAmount += NumberUtil.doubleVal(payableHistoryRecord.getDepositAmount());
        actuallyPaid += NumberUtil.doubleVal(payableHistoryRecord.getActuallyPaid());

//        //欠款结算单
//        if (PaymentTypes.INVENTORY_DEBT == payableHistoryRecord.getPaymentType()) {
//          debtWithdrawalExpenditure += NumberUtil.doubleVal(payableHistoryRecord.getActuallyPaid());
//          supplierDebtDiscount += NumberUtil.doubleVal(payableHistoryRecord.getDeduction());
//          strikeAmount += NumberUtil.doubleVal(payableHistoryRecord.getStrikeAmount());
//        }
      }

      creditAmount = amount - cash - bankCardAmount - checkAmount - depositAmount - deduction - strikeAmount;
      /*扣款*/
      newPayableHistoryRecord.setDeduction(0 - deduction);
      /*欠款挂账*/
      newPayableHistoryRecord.setCreditAmount(0 - creditAmount);
      /* 现金*/
      newPayableHistoryRecord.setCash(0 - cash);
      /*  银行卡*/
      newPayableHistoryRecord.setBankCardAmount(0 - bankCardAmount);
      /*支票*/
      newPayableHistoryRecord.setCheckAmount(0 - checkAmount);
      /*用定金*/
      newPayableHistoryRecord.setDepositAmount(0 - depositAmount);
      /*  实付*/
      newPayableHistoryRecord.setActuallyPaid(0 - actuallyPaid);

      /**冲账*/
      newPayableHistoryRecord.setStrikeAmount(0 - strikeAmount);
      payableHistoryRecordList.add(newPayableHistoryRecord);

      Object status = writer.begin();
      try {
        for (PayableHistoryRecord payableHistoryRecord : payableHistoryRecordList) {
          if (isCurrentDay) {
            payableHistoryRecord.setDayType(DayType.TODAY);
          } else {
            payableHistoryRecord.setDayType(DayType.OTHER_DAY);
          }
          writer.saveOrUpdate(payableHistoryRecord);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    } catch (Exception e) {
      LOG.error("入库作废单流水统计失败");
      LOG.error("purchaseInventoryId:" + payableDTO.getPurchaseInventoryId() + ",payable:" + payableDTO.toString());
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 根据shopiD,供应商ID，入库单ID 查找对应的付款历史记录
   *
   * @param shopId
   * @param supplierId
   * @param purchaseInventoryId
   * @return
   */
  private PayableHistoryRecordDTO getPayHistoryRecordForRepeal(Long shopId, Long supplierId, Long purchaseInventoryId) {
    TxnWriter writer = txnDaoManager.getWriter();
    PayableHistoryRecord payableHistoryRecord = writer.getPayHistoryRecordForRepeal(shopId, supplierId, purchaseInventoryId);
    if (payableHistoryRecord == null) return null;
    return payableHistoryRecord.toDTO();
  }

  /**
   * 更新付款历史记录
   *
   * @param payableHistoryRecordDTO
   */
  @Override
  public void updatePayableHistoryRecordDTO(PayableHistoryRecordDTO payableHistoryRecordDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      PayableHistoryRecord payableHistoryRecord = writer.findById(PayableHistoryRecord.class, payableHistoryRecordDTO.getId());
      /*店面ID*/
      payableHistoryRecord.setShopId(payableHistoryRecordDTO.getShopId());
      /*扣款*/
      payableHistoryRecord.setDeduction(NumberUtil.numberValue(payableHistoryRecordDTO.getDeduction(), 0D));
      /*欠款挂账*/
      payableHistoryRecord.setCreditAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getCreditAmount(), 0D));
      /* 现金*/
      payableHistoryRecord.setCash(NumberUtil.numberValue(payableHistoryRecordDTO.getCash(), 0D));
      /*  银行卡*/
      payableHistoryRecord.setBankCardAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getBankCardAmount(), 0D));
      /*支票*/
      payableHistoryRecord.setCheckAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getCheckAmount(), 0D));
      /*支票号码*/
      payableHistoryRecord.setCheckNo(payableHistoryRecordDTO.getCheckNo());
      /*用定金*/
      payableHistoryRecord.setDepositAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getDepositAmount(), 0D));
      /*  实付*/
      payableHistoryRecord.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0D));
      /*材料品名*/
      payableHistoryRecord.setMaterialName(payableHistoryRecordDTO.getMaterialName());
      /*金额*/
      payableHistoryRecord.setAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getAmount(), 0D));
      /*已付金额*/
      payableHistoryRecord.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0D));
      /*采购入库单ID*/
      payableHistoryRecord.setPayableHistoryId(payableHistoryRecordDTO.getPayableHistoryId());
      /*结算历史ID*/
      payableHistoryRecord.setPayableHistoryId(payableHistoryRecordDTO.getPayableHistoryId());
      /*供应商ID*/
      payableHistoryRecord.setSupplierId(payableHistoryRecordDTO.getSupplierId());
      /*应付款ID*/
      payableHistoryRecord.setPayableId(payableHistoryRecordDTO.getPayableId());
      /*状态*/
      payableHistoryRecord.setStatus(payableHistoryRecordDTO.getStatus());
      writer.update(payableHistoryRecord);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * @param shopId               店面ID
   * @param purchaserInventoryId 入库单ID
   * @param supplierId           供应商ID
   * @return
   */
  @Override
  public PayableDTO getInventoryPayable(Long shopId, Long purchaserInventoryId, Long supplierId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Payable payable = writer.getInventoryPayable(shopId, purchaserInventoryId, supplierId);
    if (payable == null) return null;
    return payable.toDTO();
  }



  /**
   * 据作废 结算时如果有定金 归还定金
   *
   * @param purchaseInventoryDTO
   */
  @Override
  public void returnPayable(PurchaseInventoryDTO purchaseInventoryDTO) {

    if (purchaseInventoryDTO == null) return;
    //获得付款单
    PayableDTO payableDTO = this.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
    if (payableDTO == null) {
      LOG.error("SupplierPayableService.returnPayable 无实付记录" + purchaseInventoryDTO.toString());
      return;
    }

    //如果付款时 没有使用定金支付 返回
    if (NumberUtil.doubleVal(payableDTO.getDeposit()) <= 0) {
      return;
    }
      DepositDTO depositDTO = new DepositDTO();
      depositDTO.setShopId(purchaseInventoryDTO.getShopId());
      depositDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
    double returned = NumberUtil.numberValue(payableDTO.getDeposit(), 0d);
    depositDTO.setCash(returned);
      depositDTO.setActuallyPaid(returned);
      this.saveOrUpdateDeposit(depositDTO);
    }

  /**
   * 供应商付定金 保存收款记录
   *
   * @param depositDTO
   */
  @Override
  public void savePayableHistoryRecordFromDepositDTO(DepositDTO depositDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
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
    payableHistoryRecord.setDayType(DayType.OTHER_DAY);
    if (depositDTO.getPayTime() != null) {
      payableHistoryRecord.setPayTime(depositDTO.getPayTime());
    } else {
      payableHistoryRecord.setPayTime(System.currentTimeMillis());
    }
    payableHistoryRecord.setPaidAmount(NumberUtil.toReserve(depositDTO.getCash() + depositDTO.getBankCardAmount() + depositDTO.getCheckAmount(), NumberUtil.MONEY_PRECISION));
    payableHistoryRecord.setMaterialName(PaymentTypes.SUPPLIER_DEPOSIT.getName());

    Object status = txnWriter.begin();
    try {
      txnWriter.save(payableHistoryRecord);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  /**
   * 入库退货单
   *
   * @param purchaseReturnDTO
   */
  @Override
  public void savePayableAndRecordFromPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO, String materialName, PayableHistoryDTO payableHistoryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Payable payable = new Payable();
    payable.fromPurchaseReturnDTO(purchaseReturnDTO);
    payable.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord();
    payableHistoryRecord.setShopId(purchaseReturnDTO.getShopId());

    payableHistoryRecord.setPayTime(System.currentTimeMillis());
    payableHistoryRecord.setCheckNo(purchaseReturnDTO.getBankCheckNo());
    payableHistoryRecord.setMaterialName(materialName);
    payableHistoryRecord.setPurchaseInventoryId(purchaseReturnDTO.getId());
    payableHistoryRecord.setPurchaseReturnId(purchaseReturnDTO.getId());
    payableHistoryRecord.setPayer(purchaseReturnDTO.getUserName());
    payableHistoryRecord.setPayerId(purchaseReturnDTO.getUserId());
    payableHistoryRecord.setStatus(PayStatus.USE);

    if (null == purchaseReturnDTO.getCash()) {
      purchaseReturnDTO.setCash(0D);
    }

    if (null == purchaseReturnDTO.getDepositAmount()) {
      purchaseReturnDTO.setDepositAmount(0D);
    }

    if (null == purchaseReturnDTO.getStrikeAmount()) {
      purchaseReturnDTO.setStrikeAmount(0D);
    }
    purchaseReturnDTO.setBankAmount(NumberUtil.numberValue(purchaseReturnDTO.getBankAmount(), 0D));
    purchaseReturnDTO.setBankCheckAmount(NumberUtil.numberValue(purchaseReturnDTO.getBankCheckAmount(), 0D));
    purchaseReturnDTO.setSettledAmount(NumberUtil.numberValue(purchaseReturnDTO.getSettledAmount(), 0D));
    purchaseReturnDTO.setAccountDebtAmount(NumberUtil.numberValue(purchaseReturnDTO.getAccountDebtAmount(), 0D));
    purchaseReturnDTO.setAccountDiscount(NumberUtil.numberValue(purchaseReturnDTO.getAccountDiscount(), 0D));
//    if (PayMethod.PURCHASE_RETURN_DEPOSIT.getValue().equals(purchaseReturnDTO.getReturnPayableType())) {
    //退入定金
    payableHistoryRecord.setDepositAmount(0 - purchaseReturnDTO.getDepositAmount());
//      payableHistoryRecord.setCash(0D);
//      payableHistoryRecord.setActuallyPaid(0D);
    payableHistoryRecord.setPaymentType(PaymentTypes.INVENTORY_RETURN);

//    } else {
    //退入现金
    payableHistoryRecord.setCash(0 - purchaseReturnDTO.getCash());
//      payableHistoryRecord.setDepositAmount(0D);
    payableHistoryRecord.setActuallyPaid(0 - purchaseReturnDTO.getSettledAmount());
//      payableHistoryRecord.setPaymentTypes(PaymentTypes.INVENTORY_RETURN_CASH);
    payableHistoryRecord.setDeduction(0 - purchaseReturnDTO.getAccountDiscount());
    payableHistoryRecord.setCreditAmount(0 - purchaseReturnDTO.getAccountDebtAmount());
    payableHistoryRecord.setStrikeAmount(0 - purchaseReturnDTO.getStrikeAmount());
    payableHistoryRecord.setBankCardAmount(0 - purchaseReturnDTO.getBankAmount());
    payableHistoryRecord.setCheckAmount(0 - purchaseReturnDTO.getBankCheckAmount());
//    }

    payableHistoryRecord.setDayType(DayType.OTHER_DAY);
    payableHistoryRecord.setAmount(0 - purchaseReturnDTO.getTotal());
    payableHistoryRecord.setSupplierId(purchaseReturnDTO.getSupplierId());
    payableHistoryRecord.setPaidAmount(payableHistoryRecord.getActuallyPaid());
    payableHistoryRecord.setPayableHistoryId(payableHistoryDTO.getId());
    Object status = txnWriter.begin();
    try {
      txnWriter.save(payable);
      txnWriter.save(payableHistoryRecord);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  public SupplierReturnPayableDTO getSupplierReturnPayableByPurchaseReturnId(Long shopId, Long purchaseReturnId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SupplierReturnPayable> supplierReturnPayableList = writer.getSupplierReturnPayableByPurchaseReturnId(shopId, purchaseReturnId);
    if (CollectionUtils.isEmpty(supplierReturnPayableList)) {
      return null;
    }
    return supplierReturnPayableList.get(0).toSupplierReturnPayableDTO();
  }


  public  List<PayableDTO> getPayables(RecOrPayIndexDTO recOrPayIndexDTO)  {
    PayableDTO payableDTO = null;
    SupplierDTO supplierDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<PayableDTO> payableDTOList = new ArrayList<PayableDTO>();
    List<Payable> payableList = writer.getPayables(recOrPayIndexDTO);
    for (Payable payable : payableList) {
      payableDTO = payable.toDTO();
      supplierDTO = userService.getSupplierById(payable.getSupplierId());
      if (supplierDTO != null) {
//        SupplierRecordDTO supplierRecordDTO = getSupplierRecordDTOBySupplierId(supplierDTO.getShopId(),supplierDTO.getId());
        Double sumCreditAmount = getSumPayableBySupplierId(supplierDTO.getId(), supplierDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);

        payableDTO.setTotalCreditAmount(sumCreditAmount);

        payableDTO.setSupplierName(supplierDTO.getName());
      }

      payableDTOList.add(payableDTO);
    }
    return payableDTOList;
  }


  public List<PayableDTO> searchPayable(Long shopId, Long supplierId) throws ParseException {
    TxnWriter writer = txnDaoManager.getWriter();

    List<Payable> payables = writer.searchPayable(shopId, supplierId);

    if (CollectionUtils.isEmpty(payables)) {
      return null;
    }
    List<PayableDTO> payableDTOs = new ArrayList<PayableDTO>();
    for (Payable p : payables) {
      payableDTOs.add(p.toDTO());
    }
    return payableDTOs;
  }

  public void paidByStrikeAmount(PayableHistoryDTO payableHistoryDTO, PayableDTO payableDTO, PaymentTypes paymentTypes) {
    PayableHistoryRecordDTO payableHistoryRecordDTO = this.getPayHistoryRecord(payableDTO.getPurchaseInventoryId(), payableHistoryDTO.getId(), payableHistoryDTO.getShopId());
    if (payableHistoryRecordDTO == null) {
      payableHistoryRecordDTO = new PayableHistoryRecordDTO();
      payableHistoryRecordDTO.setShopId(payableDTO.getShopId());
      payableHistoryRecordDTO.setSupplierId(payableDTO.getSupplierId());
      payableHistoryRecordDTO.setPayableId(payableDTO.getId());
      payableHistoryRecordDTO.setPurchaseInventoryId(payableDTO.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
      payableHistoryRecordDTO.setMaterialName(payableDTO.getMaterialName());
      payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(payableDTO.getAmount(), 0d));
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableDTO.getPaidAmount(), 0d));
      payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
      payableHistoryRecordDTO.setActuallyPaid(0d);
      payableHistoryRecordDTO.setStatus(PayStatus.USE);
      payableHistoryRecordDTO.setPurchaseReturnId(payableHistoryDTO.getPurchaseReturnId());

    }
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setPaymentType(paymentTypes);
    payableHistoryRecordDTO.setDayType(DayType.OTHER_DAY);
    //如果冲账大于付款挂账，将此单据结算完，并进行下一张单据的结算
    if (payableHistoryDTO.getStrikeAmount() >= payableDTO.getCreditAmount()) {
      //实付减少p.getCreditAmount()
      payableHistoryDTO.setActuallyPaid(payableHistoryDTO.getActuallyPaid() - payableDTO.getCreditAmount()); //
      //冲账减少p.getCreditAmount()
      payableHistoryDTO.setStrikeAmount(payableHistoryDTO.getStrikeAmount() - payableDTO.getCreditAmount());
      payableHistoryRecordDTO.setStrikeAmount(payableDTO.getCreditAmount());
      payableHistoryRecordDTO.setPurchaseInventoryId(payableDTO.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d));
      payableDTO.setPaidAmount(NumberUtil.numberValue(payableDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d));
      payableDTO.setStrikeAmount(NumberUtil.numberValue(payableDTO.getStrikeAmount(), 0d) + NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d));
      payableDTO.setCreditAmount(0d);
      payableHistoryRecordDTO.setCreditAmount(payableDTO.getCreditAmount());
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

    } else {     //如果冲账小于于付款挂账，将冲账付完，并用下一支付方式
      payableHistoryDTO.setActuallyPaid(payableHistoryDTO.getActuallyPaid() - payableHistoryDTO.getStrikeAmount());
      payableHistoryRecordDTO.setStrikeAmount(payableHistoryDTO.getStrikeAmount());
      payableHistoryRecordDTO.setPurchaseInventoryId(payableDTO.getPurchaseInventoryId());
      payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getStrikeAmount(), 0d));
      payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getStrikeAmount(), 0d));
      payableDTO.setCreditAmount(NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d) - NumberUtil.numberValue(payableHistoryDTO.getStrikeAmount(), 0d));
      payableDTO.setStrikeAmount(NumberUtil.numberValue(payableDTO.getStrikeAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getStrikeAmount(), 0d));
      payableDTO.setPaidAmount(NumberUtil.numberValue(payableDTO.getPaidAmount(), 0d) + NumberUtil.numberValue(payableHistoryDTO.getStrikeAmount(), 0d));
      payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d));
      //添加或更新付款历史记录
      this.saveOrUpdatePayHistoryRecord(payableHistoryRecordDTO);

      payableHistoryDTO.setStrikeAmount(0d);
    }
    this.updatePayable(payableDTO);
  }


  /**
   * 供应商结算结算前进行校验
   *
   * @param payableDTOList
   * @param payableHistoryDTO
   * @return
   */
  public String checkSupplierAccount(List<PayableDTO> payableDTOList, PayableHistoryDTO payableHistoryDTO) {
    if (CollectionUtils.isEmpty(payableDTOList) || payableHistoryDTO == null) {
      return ValidatorConstant.ORDER_IS_NULL_MSG;
    }

    double totalDebt = 0;
    double totalPayable = NumberUtil.doubleVal(payableHistoryDTO.getCash()) +  NumberUtil.doubleVal(payableHistoryDTO.getCreditAmount())
        + NumberUtil.doubleVal(payableHistoryDTO.getDeduction()) + NumberUtil.doubleVal(payableHistoryDTO.getBankCardAmount())
        + NumberUtil.doubleVal(payableHistoryDTO.getCheckAmount()) + NumberUtil.doubleVal(payableHistoryDTO.getDepositAmount());
    for (PayableDTO payableDTO : payableDTOList) {
      PayableDTO newPayableDTO = this.getPayableDTOById(Long.valueOf(payableDTO.getIdStr()));
      if (newPayableDTO == null) {
        continue;
      }
      if (newPayableDTO.getStatus() == PayStatus.USE) {
        totalDebt += NumberUtil.doubleVal(newPayableDTO.getCreditAmount());
      }
    }

    if (totalDebt - totalPayable < 0) {
      return ValidatorConstant.SUPPLIER_ACCOUNT_FAIL;
    }

    return "";
  }


  public PayableDTO getPayableDTOById(Long payableId) {
    TxnWriter writer = txnDaoManager.getWriter();
    if (payableId == null) {
      return null;
    }
    Payable payable = writer.getById(Payable.class, payableId);
    if (payable == null) {
      return null;
    }
    return payable.toDTO();
  }


  @Override
  public List<PayableHistoryRecordDTO> getPayHistoryRecordByPayTime(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<PayableHistoryRecord> payableHistoryRecordList = writer.getPayHistoryRecordByPayTime(shopId, startTime, endTime);
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
   * 查找收入记录
   *
   * @param shopId
   * @param supplierId
   * @param purchaseInventoryId
   * @return
   */
  public List<PayableHistoryRecord> getPayHistoryRecordListByIds(Long shopId, Long supplierId, Long purchaseInventoryId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPayHistoryRecordListByIds(shopId, supplierId, purchaseInventoryId);
  }


  @Override
  public List<ReceptionRecordDTO> getReceptionRecordByReceptionDate(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<ReceptionRecord> receptionRecordList = writer.getReceptionRecordByReceptionDate(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(receptionRecordList)) {
      return null;
    }
    List<ReceptionRecordDTO> receptionRecordDTOList = new ArrayList<ReceptionRecordDTO>();
    for (ReceptionRecord receptionRecord : receptionRecordList) {
      receptionRecordDTOList.add(receptionRecord.toDTO());
    }
    return receptionRecordDTOList;
  }

  @Override
  public SupplierRecordDTO getSupplierRecordDTOBySupplierId(Long shopId, Long supplierId) {
    TxnWriter writer = txnDaoManager.getWriter();

    SupplierRecord supplierRecord = writer.getSupplierRecordDTOBySupplierId(shopId, supplierId);

    if (null == supplierRecord) {
      return null;
    }

    return supplierRecord.toDTO();
  }

  @Override
  public void updateSupplierDebt(Long shopId, Long supplierId, double debt) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      SupplierRecord supplierRecord = writer.getSupplierRecord(shopId, supplierId);
      if (null != supplierRecord) {
        Double oldDebt = null == supplierRecord.getDebt() ? 0D : supplierRecord.getDebt();

        supplierRecord.setDebt(oldDebt + debt);

        writer.update(supplierRecord);
      } else {
        LOG.error("shopId=" + shopId.toString() + " supplierId=" + supplierId.toString() + " 在supplierRecord中没记录");
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public int countSupplierReturnPayable() {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countSupplierReturnPayable();
  }

  @Override
  public List<SupplierReturnPayable> getSupplierReturnPayable(int size) {
    return txnDaoManager.getWriter().getSupplierReturnPayable(size);
  }

  public void fillSupplierTradeInfo(SupplierDTO supplierDTO) {
    Long shopId = supplierDTO.getShopId();
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<Double> returnList = getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
    List<Double> inventoryList = getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
    supplierDTO.setTotalTradeAmount(NumberUtil.round(inventoryList.get(0) + inventoryList.get(1), 2));
    Double totalReturnAmount = 0 - (returnList.get(0) + returnList.get(1));
    supplierDTO.setTotalReturnAmount(NumberUtil.round(totalReturnAmount, 2));
    supplierDTO.setTotalPayable(CollectionUtil.getFirst(inventoryList));
    supplierDTO.setTotalReceivable(0 - CollectionUtil.getFirst(returnList));
    supplierDTO.setDeposit(getSumDepositBySupplierId(supplierDTO.getId(), shopId));
    try {
      supplierDTO.setCountSupplierReturn(searchService.countReturn(supplierDTO.getShopId(), supplierDTO.getId(), OrderTypes.RETURN, OrderStatus.SETTLED));
    } catch (Exception e) {
      LOG.error("SupplierPayableService.fillSupplierSettleInfo出错. supplierDTO:{}", supplierDTO);
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public PayableDTO getPayableDTOByOrderId(Long shopId, Long orderId) {
    if (null == orderId) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Payable payable = writer.getPayableDTOByOrderId(shopId, orderId, true);
    if (null == payable) {
      return null;
    }

    return payable.toDTO();
  }

  @Override
  public List<Double> getPayableConsumeTimesBySupplierId(Long supplierId, Long shopId, OrderDebtType debtType) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Double> doubleList = writer.getSumPayableBySupplierId(supplierId, shopId, debtType);
    List<Double> statementOrderList = writer.getStatementOrderSumPayable(supplierId, shopId, debtType);

    List<Double> returnList = new ArrayList<Double>();
    returnList.add(doubleList.get(0));
    returnList.add(NumberUtil.toReserve(doubleList.get(1) - statementOrderList.get(0) - statementOrderList.get(1)));
    returnList.add(doubleList.get(2) - statementOrderList.get(2));
    return returnList;

  }


  /**
   * 作废单统计专用
   * 付款历史记录(record)作废和流水统计
   *
   * @param purchaseInventoryDTO
   */
  @Override
  public RunningStatDTO repealPayableHistoryRecordForInventory(PurchaseInventoryDTO purchaseInventoryDTO) {
    RunningStatDTO runningStatDTO = new RunningStatDTO();

    try {
      TxnWriter writer = txnDaoManager.getWriter();

      List<PayableHistoryRecord> payableHistoryRecordList = writer.getPayHistoryRecordListByIds(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierId(), purchaseInventoryDTO.getId());
      if (CollectionUtils.isEmpty(payableHistoryRecordList)) {
        return runningStatDTO;
      }
      double debtNewExpenditure = 0.0; //这个单据产生的新增欠款
      double debtWithdrawalExpenditure = 0.0;//供应商欠款回笼总和
      double supplierDebtDiscount = 0.0;//供应商欠款折扣

      for (int index = 0; index < payableHistoryRecordList.size(); index++) {
        PayableHistoryRecord payableHistoryRecord = payableHistoryRecordList.get(index);

        if (payableHistoryRecord.getPaymentType() == PaymentTypes.INVENTORY_REPEAL) {
          continue;
        }
        if (payableHistoryRecord.getPaymentType() == PaymentTypes.INVENTORY) {
          debtNewExpenditure = NumberUtil.doubleVal(payableHistoryRecord.getCreditAmount());
        }

        //欠款结算单
        if (PaymentTypes.INVENTORY_DEBT == payableHistoryRecord.getPaymentType()) {
          debtWithdrawalExpenditure += NumberUtil.doubleVal(payableHistoryRecord.getActuallyPaid());
          supplierDebtDiscount += NumberUtil.doubleVal(payableHistoryRecord.getDeduction());
        }
      }

      runningStatDTO.setDebtNewExpenditure(debtNewExpenditure);
      runningStatDTO.setDebtWithdrawalExpenditure(debtWithdrawalExpenditure);
      runningStatDTO.setSupplierDebtDiscount(supplierDebtDiscount);

      return runningStatDTO;

    } catch (Exception e) {
      LOG.error("入库作废单计算失败");
      LOG.error("purchaseInventoryId:" + purchaseInventoryDTO.getId() + ",purchaseInventoryDTO:" + purchaseInventoryDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return runningStatDTO;
  }

  public DepositOrderDTO getTotalDepositOrderByPurchaseInventoryInfo(Long shopId, Long supplierId, Long relatedOrderId) {
    TxnWriter tw = this.txnDaoManager.getWriter();
    List<DepositOrder> depositOrderList = tw.getTotalDepositOrderByPurchaseInventoryInfo(shopId, null, supplierId, relatedOrderId);

    DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
    if (CollectionUtil.isEmpty(depositOrderList)) {
      return depositOrderDTO;
    }

    for (int i = 0; i < depositOrderList.size(); i++) {
      DepositOrder orderDTO = (DepositOrder) depositOrderList.get(i);
      if (i == 0) {
        depositOrderDTO.setInOut(orderDTO.getInOut());
        depositOrderDTO.setOperator(orderDTO.getOperator());
        depositOrderDTO.setShopId(orderDTO.getShopId());
        depositOrderDTO.setSupplierId(orderDTO.getSupplierId());
        depositOrderDTO.setRelatedOrderNo(orderDTO.getRelatedOrderNo());
        depositOrderDTO.setRelatedOrderId(orderDTO.getRelatedOrderId());
        depositOrderDTO.setDepositType(orderDTO.getDepositType());
      }

      depositOrderDTO.setCash(moneyAddUp(depositOrderDTO.getCash(), orderDTO.getCash()));
      depositOrderDTO.setBankCardAmount(moneyAddUp(depositOrderDTO.getBankCardAmount(), orderDTO.getBankCardAmount()));
      depositOrderDTO.setCheckAmount(moneyAddUp(depositOrderDTO.getCheckAmount(), orderDTO.getCheckAmount()));
      depositOrderDTO.setActuallyPaid(moneyAddUp(depositOrderDTO.getActuallyPaid(), orderDTO.getActuallyPaid()));
    }


    return depositOrderDTO;
  }


}
