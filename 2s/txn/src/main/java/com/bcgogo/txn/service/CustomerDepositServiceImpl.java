package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.*;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CustomerDepositDTO;
import com.bcgogo.txn.dto.DepositOrderDTO;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.txn.dto.ReceptionRecordDTO;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户预约金相关服务
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-10
 * Time: 下午5:49
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CustomerDepositServiceImpl implements ICustomerDepositService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private TxnDaoManager txnDaoManager;

  private TxnWriter getTxnWriter() {
    return this.txnDaoManager.getWriter();
  }

  @Override
  public void saveCustomerDeposit(CustomerDepositDTO customerDepositDTO) {
    if (customerDepositDTO == null) {
      return;
    }
    TxnWriter tw = this.getTxnWriter();
    Object status = tw.begin();
    try {
      CustomerDeposit customerDeposit = new CustomerDeposit(customerDepositDTO);
      tw.save(customerDeposit);
      tw.commit(status);
    } catch (Exception e) {
      logger.error("saveCustomerDepositError,the CustomerDepositDTO is {" + customerDepositDTO.toString() + "}", e);
      return;
    } finally {
      tw.rollback(status);
    }
  }

  @Override
  public void updateCustomerDeposit(CustomerDepositDTO customerDepositDTO) {
    if (customerDepositDTO == null) {
      return;
    }
    TxnWriter tw = this.getTxnWriter();
    Object status = tw.begin();
    try {
      CustomerDeposit customerDeposit = new CustomerDeposit(customerDepositDTO);
      tw.update(customerDeposit);
      tw.commit(status);
    } catch (Exception e) {
      logger.error("updateCustomerDepositError,the CustomerDepositDTO is {" + customerDepositDTO.toString() + "}", e);
      tw.rollback(status);
      return;
    }
  }

  @Override
  public CustomerDepositDTO queryCustomerDepositByShopIdAndCustomerId(Long shopId, Long customerId) {
    try {
      if (shopId == null) {
        throw new Exception("shopId should not be null");
      }
      if (customerId == null) {
        throw new Exception("customerId should not be null");
      }
      TxnWriter tw = this.getTxnWriter();
      CustomerDeposit customerDeposit = tw.queryCustomerDepositByShopIdAndCustomerId(shopId, customerId);
      if (customerDeposit != null) {
        return customerDeposit.toDTO();
      }
      return null;
    } catch (Exception e) {
      logger.error("queryCustomerDepositByShopIdAndCustomerIdError", e);
      return new CustomerDepositDTO();
    }
  }

  @Override
  public Result customerDeposit(CustomerDepositDTO customerDepositDTO) {
    Result result = new Result();
    result.setSuccess(false);

    if (customerDepositDTO == null) {
      return result;
    }
    TxnWriter tw = this.getTxnWriter();
    Object status = tw.begin();
    try {
      //记录充值订单
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      BeanUtils.copyProperties(customerDepositDTO, depositOrderDTO, new String[]{"id", "payTime"});
      depositOrderDTO.setInOut(InOutFlag.IN_FLAG.getCode());
      depositOrderDTO.setDepositType(DepositType.DEPOSIT.getScene()); //预付金充值 场景
      depositOrderDTO.setOperator(customerDepositDTO.getOperator());
      depositOrderDTO.setMemo(customerDepositDTO.getMemo());
      CustomerDeposit currentCustomerDeposit = tw.queryCustomerDepositByShopIdAndCustomerId(customerDepositDTO.getShopId(), customerDepositDTO.getCustomerId());
      if (currentCustomerDeposit == null) {
        tw.save(new CustomerDeposit(customerDepositDTO));
      } else {
        CustomerDepositDTO curCustomerDepositDTO = currentCustomerDeposit.toDTO();
        currentCustomerDeposit.FromDTO(customerDepositAddUp(customerDepositDTO, curCustomerDepositDTO, InOutFlag.IN_FLAG)); //金额累加保存
        tw.update(currentCustomerDeposit);
      }
      DepositOrder depositOrder = new DepositOrder(depositOrderDTO);
      tw.save(depositOrder); // 预收款使用记录增量式
      result.setData(currentCustomerDeposit.getActuallyPaid());

      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      ReceptionRecord receptionRecord = new ReceptionRecord();
      receptionRecord.setCash(customerDepositDTO.getCash());
      receptionRecord.setBankCard(customerDepositDTO.getBankCardAmount());
      receptionRecord.setCheque(customerDepositDTO.getCheckAmount());
      receptionRecord.setChequeNo(customerDepositDTO.getCheckNo());
      receptionRecord.setShopId(customerDepositDTO.getShopId());
      receptionRecord.setOrderTypeEnum(OrderTypes.DEPOSIT);
      receptionRecord.setOrderId(depositOrder.getId());
      receptionRecord.setReceptionDate(System.currentTimeMillis());
      receptionRecord.setDayType(DayType.OTHER_DAY);
      CustomerDTO customerDTO = customerService.getCustomerById(customerDepositDTO.getCustomerId());
      if (customerDTO != null) {
        receptionRecord.setPayeeId(customerDTO.getId());
        receptionRecord.setPayee(customerDTO.getName());
      }
      receptionRecord.setOrderTotal(customerDepositDTO.getActuallyPaid());
      tw.save(receptionRecord);

      tw.commit(status);
    } catch (Exception e) {
      logger.error("CustomerDepositError,the CustomerDepositDTO is {" + customerDepositDTO.toString() + "}", e);
      return result;
    } finally {
      tw.rollback(status);
    }
    result.setSuccess(true);
    return result;
  }


  @Override
  public boolean customerDepositUse(CustomerDepositDTO customerDepositDTO, DepositOrderDTO depositOrderDTO, TxnWriter writer) {
    if (customerDepositDTO == null) {
      return false;
    }
    if (customerDepositDTO.getActuallyPaid() == null) {
      return false;
    }
    TxnWriter tw;
    Object status = null;
    if (writer != null) {
      tw = writer;
    } else {
      tw = this.getTxnWriter();
      status = tw.begin();
    }
    try {
      BeanUtils.copyProperties(customerDepositDTO, depositOrderDTO, new String[]{"id", "payTime"});
      if (org.apache.commons.lang.StringUtils.isNotBlank(customerDepositDTO.getOperator()) && org.apache.commons.lang.StringUtils.isBlank(depositOrderDTO.getOperator())) {
        depositOrderDTO.setOperator(customerDepositDTO.getOperator());
      }
      CustomerDeposit currentCustomerDeposit = tw.queryCustomerDepositByShopIdAndCustomerId(customerDepositDTO.getShopId(), customerDepositDTO.getCustomerId());
      CustomerDepositDTO curCustomerDepositDTO;
      if (currentCustomerDeposit == null) {
        //无预收款账户 新增
        curCustomerDepositDTO = new CustomerDepositDTO();
        curCustomerDepositDTO.setCash(0.00);
        curCustomerDepositDTO.setBankCardAmount(0.00);
        curCustomerDepositDTO.setCheckAmount(0.00);
        curCustomerDepositDTO.setActuallyPaid(0.00);
        curCustomerDepositDTO.setShopId(customerDepositDTO.getShopId());
        curCustomerDepositDTO.setCustomerId(customerDepositDTO.getCustomerId());
        curCustomerDepositDTO.setOperator(customerDepositDTO.getOperator());
        currentCustomerDeposit = new CustomerDeposit();
      } else {
        curCustomerDepositDTO = currentCustomerDeposit.toDTO();
      }
      // 使用场景
      DepositType depositType = DepositType.getDepositTypeBySceneAndInOutFlag(depositOrderDTO.getDepositType(), InOutFlag.getInOutFlagEnumByCode(depositOrderDTO.getInOut()));
      depositStategy(customerDepositDTO, curCustomerDepositDTO, depositOrderDTO, depositType); // 预收款结算策略
      currentCustomerDeposit.FromDTO(customerDepositAddUp(customerDepositDTO, curCustomerDepositDTO, InOutFlag.getInOutFlagEnumByCode(depositOrderDTO.getInOut()))); //金额累加保存
      tw.saveOrUpdate(currentCustomerDeposit);

      tw.save(new DepositOrder(depositOrderDTO)); // 预售款取用记录增量式
      if (status != null) {
        tw.commit(status);
      }
    } catch (Exception e) {
      logger.error("CustomerDepositError,the CustomerDepositDTO is {" + customerDepositDTO.toString() + "}", e);
      return false;
    } finally {
      if (status != null) {
        tw.rollback(status);
      }
    }
    return true;
  }

  /**
   * 使用的时候目前depositOrderDTO 前端只传actually_paid
   * 默认扣款顺序为cash>bankAmount>cheque
   *
   * @param customerDepositDTO
   * @param currentCustomerDepositDTO
   * @param depositOrderDTO
   * @param depositType
   */
  private void depositStategy(CustomerDepositDTO customerDepositDTO, CustomerDepositDTO currentCustomerDepositDTO, DepositOrderDTO depositOrderDTO, DepositType depositType) {
    Double actually_paid = customerDepositDTO.getActuallyPaid();
    Double currentCash = currentCustomerDepositDTO.getCash();
    Double currentBankAmount = currentCustomerDepositDTO.getBankCardAmount();
    Double currentCheque = currentCustomerDepositDTO.getCheckAmount();
    if (compareTwoDouble(actually_paid, 0.00) > 0) {
      switch (depositType) {
        case SALES:
          if (compareTwoDouble(actually_paid, currentCash) == 0 || compareTwoDouble(actually_paid, currentCash) == -1) {
            customerDepositDTO.setCash(actually_paid);
            customerDepositDTO.setBankCardAmount(0.00);
            customerDepositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(actually_paid);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount) == -1) {
            customerDepositDTO.setCash(currentCash);
            customerDepositDTO.setBankCardAmount((actually_paid - currentCash));
            customerDepositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount((actually_paid - currentCash));
            depositOrderDTO.setCheckAmount(0.00);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == -1) {
            customerDepositDTO.setCash(currentCash);
            customerDepositDTO.setBankCardAmount(currentBankAmount);
            customerDepositDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount(currentBankAmount);
            depositOrderDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
          } else {
            String errorMsg = "预收款使用金额大于余额，异常。预收款使用金额为:" + actually_paid + ",当前余额为:" + currentCash + currentBankAmount + currentCheque;
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
          }
          break;
        case SALES_REPEAL:
          customerDepositDTO.setCash(depositOrderDTO.getCash());
          customerDepositDTO.setCheckAmount(depositOrderDTO.getCheckAmount());
          customerDepositDTO.setBankCardAmount(depositOrderDTO.getBankCardAmount());
          customerDepositDTO.setCheckNo(depositOrderDTO.getCheckNo());
          depositOrderDTO.setCash(depositOrderDTO.getCash());
          depositOrderDTO.setCheckAmount(depositOrderDTO.getCheckAmount());
          depositOrderDTO.setBankCardAmount(depositOrderDTO.getBankCardAmount());
          depositOrderDTO.setCheckNo(depositOrderDTO.getCheckNo());
          break;
        case SALES_BACK:
          customerDepositDTO.setCash(actually_paid); // 退货默认还现金
          customerDepositDTO.setBankCardAmount(0.00);
          customerDepositDTO.setCheckAmount(0.00);
          depositOrderDTO.setCash(actually_paid);
          break;
        case SALES_BACK_REPEAL:
          customerDepositDTO.setCash(actually_paid);
          customerDepositDTO.setBankCardAmount(0.00);
          customerDepositDTO.setCheckAmount(0.00);
          depositOrderDTO.setCash(actually_paid);
          break;
        case COMPARE:
          if (compareTwoDouble(actually_paid, currentCash) == 0 || compareTwoDouble(actually_paid, currentCash) == -1) {
            customerDepositDTO.setCash(actually_paid);
            customerDepositDTO.setBankCardAmount(0.00);
            customerDepositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(actually_paid);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount) == -1) {
            customerDepositDTO.setCash(currentCash);
            customerDepositDTO.setBankCardAmount((actually_paid - currentCash));
            customerDepositDTO.setCheckAmount(0.00);
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount((actually_paid - currentCash));
            depositOrderDTO.setCheckAmount(0.00);
          } else if (compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == 0 || compareTwoDouble(actually_paid, currentCash + currentBankAmount + currentCheque) == -1) {
            customerDepositDTO.setCash(currentCash);
            customerDepositDTO.setBankCardAmount(currentBankAmount);
            customerDepositDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
            depositOrderDTO.setCash(currentCash);
            depositOrderDTO.setBankCardAmount(currentBankAmount);
            depositOrderDTO.setCheckAmount((actually_paid - currentCash - currentBankAmount));
          } else {
            String errorMsg = "预收款使用金额大于余额，异常。预收款使用金额为:" + actually_paid + ",当前余额为:" + currentCash + currentBankAmount + currentCheque;
            logger.error(errorMsg);
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
  public List<DepositOrderDTO> queryDepositOrdersByShopIdCustomerId(Long shopId, Long customerId, Long inOut, SortObj sortObj, Pager pager) {
    if (shopId == null || customerId == null) {
      return new ArrayList<DepositOrderDTO>();
    }
    TxnWriter tw = this.getTxnWriter();
    List<DepositOrderDTO> depositOrderDTOs = null;

    try {
      List<DepositOrder> depositOrders = tw.queryDepositOrderByShopIdAndCustomerIdOrSupplierId(shopId, customerId, null, buildInOutList(inOut), sortObj, pager);
      if (CollectionUtils.isEmpty(depositOrders))
        return new ArrayList<DepositOrderDTO>();
      depositOrderDTOs = new ArrayList<DepositOrderDTO>(depositOrders.size() + 1);
      for (DepositOrder depositOrder : depositOrders) {
        DepositOrderDTO depositOrderDTO = depositOrder.toDTO();
        depositOrderDTO.buildDepositType(); // 页面友好显示
        depositOrderDTO.setRelatedOrderIdStr(String.valueOf(depositOrderDTO.getRelatedOrderId())); // 关联ID显示的问题
        depositOrderDTOs.add(depositOrderDTO);
      }
      return depositOrderDTOs;
    } catch (Exception e) {
      logger.error("queryDepositOrdersByShopIdCustomerIdError,shopId is {},customerId is{},stack is{}", new Object[]{shopId, customerId, e});
      return new ArrayList<DepositOrderDTO>();
    }
  }

  @Override
  public int countDepositOrderByShopIdAndCustomerId(Long shopId, Long customerId, Long inOut) {
    if (shopId == null || customerId == null) {
      return 0;
    }
    try {

      TxnWriter tw = this.getTxnWriter();
      return tw.countDepositOrderByShopIdAndCustomerIdOrSupplierId(shopId, customerId, null, buildInOutList(inOut));
    } catch (Exception e) {
      logger.error("queryDepositOrdersByShopIdCustomerIdError", e);
      return 0;
    }

  }

  @Override
  public DepositOrderDTO queryDepositOrderByShopIdCustomerIdAndRelatedOrderId(Long shopId, Long customerId, Long relatedOrderId) {
    if (shopId == null || customerId == null || relatedOrderId == null) {
      return null;
    }
    try {
      TxnWriter tw = this.getTxnWriter();
      return tw.queryDepositOrderByShopIdAndCustomerIdOrSupplierIdAndRelatedOrderId(shopId, customerId, null, relatedOrderId).toDTO();
    } catch (Exception e) {
      logger.error("queryDepositOrderByShopIdCustomerIdAndRelatedOrderIdError", e);
      return null;
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


  /**
   * 将当前充值金额累加持久化
   *
   * @param toBeAdd
   * @param currentDeposit
   * @return
   */
  private CustomerDepositDTO customerDepositAddUp(CustomerDepositDTO toBeAdd, CustomerDepositDTO currentDeposit, InOutFlag inOutFlag) {
    if (toBeAdd == null) {
      logger.error("customerDeposit should not be null.");
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
    currentDeposit.setMemo(toBeAdd.getMemo());
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
    current = current + toBeAdd;
    return NumberUtil.round(current, NumberUtil.MONEY_PRECISION);
  }

  @Override
  public DepositOrderDTO getById(Long id) {
    TxnWriter tw = this.getTxnWriter();
    DepositOrder depositOrder = tw.getById(DepositOrder.class, id);
    if (depositOrder != null) {
      return depositOrder.toDTO();
    }
    return null;
  }
}
