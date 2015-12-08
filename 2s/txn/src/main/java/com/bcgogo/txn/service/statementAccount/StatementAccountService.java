package com.bcgogo.txn.service.statementAccount;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.StatementAccount.StatementAccountConstant;
import com.bcgogo.txn.dto.StatementAccount.StatementAccountResultDTO;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 对账单专用service实现类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
@Component
public class StatementAccountService implements IStatementAccountService {
  private static final Logger LOG = LoggerFactory.getLogger(StatementAccountService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  /**
   * 根据查询条件查询对账单记录
   *
   * @param orderSearchConditionDTO
   * @param pager
   * @return
   */
  @Override
  public List getStatementAccountOrderList(OrderSearchConditionDTO orderSearchConditionDTO, Pager pager) {
    List<StatementAccountOrder> statementAccountOrderList = new ArrayList<StatementAccountOrder>();
    TxnWriter writer = txnDaoManager.getWriter();
    statementAccountOrderList = writer.getStatementAccountOrderList(orderSearchConditionDTO, pager);
    if (CollectionUtils.isEmpty(statementAccountOrderList)) {
      return null;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long[] orderIds = new Long[statementAccountOrderList.size()];
    for (int i = 0; i < statementAccountOrderList.size(); i++) {
      orderIds[i] = statementAccountOrderList.get(i).getId();
    }
    List<StatementAccountOrderDTO> statementAccountOrderDTOList = new ArrayList<StatementAccountOrderDTO>();
    Map<Long, ReceivableDTO> receivableDTOMap = txnService.getReceivableDTOByShopIdAndArrayOrderId(orderSearchConditionDTO.getShopId(), orderIds);
    Map<Long, PayableDTO> payableDTOMap = ServiceManager.getService(IInventoryService.class).getPayableDTOByPurchaseInventoryId(orderSearchConditionDTO.getShopId(), orderIds);
    if (receivableDTOMap == null) {
      receivableDTOMap = new HashMap<Long, ReceivableDTO>();
    }
    if (payableDTOMap == null) {
      payableDTOMap = new HashMap<Long, PayableDTO>();
    }
    for (StatementAccountOrder statementAccountOrder : statementAccountOrderList) {
      StatementAccountOrderDTO statementAccountOrderDTO = statementAccountOrder.toDTO();
      statementAccountOrderDTOList.add(statementAccountOrderDTO);
      ReceivableDTO receivableDTO = receivableDTOMap.get(statementAccountOrder.getId());
      if (receivableDTO != null) {
        statementAccountOrderDTO.setOrderDebtType(receivableDTO.getOrderDebtType());
        if (receivableDTO.getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE) {
          statementAccountOrderDTO.setOrderTotalStr(StatementAccountConstant.RECEIVABLE + statementAccountOrderDTO.getTotal());
          statementAccountOrderDTO.setSettledAmountStr(StatementAccountConstant.RECEIVABLE + receivableDTO.getSettledAmount());
          statementAccountOrderDTO.setDiscount(receivableDTO.getDiscount());
          statementAccountOrderDTO.setDebt(receivableDTO.getDebt());
        } else if (receivableDTO.getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_PAYABLE) {
          statementAccountOrderDTO.setOrderTotalStr(StatementAccountConstant.PAY + statementAccountOrderDTO.getTotal());
          statementAccountOrderDTO.setSettledAmountStr(StatementAccountConstant.PAY + (0 - receivableDTO.getSettledAmount()));
          statementAccountOrderDTO.setDiscount(0 - receivableDTO.getDiscount());
          statementAccountOrderDTO.setDebt(0 - receivableDTO.getDebt());
        }
      }
      PayableDTO payableDTO = payableDTOMap.get(statementAccountOrder.getId());
      if (payableDTO != null) {
        statementAccountOrderDTO.setOrderDebtType(payableDTO.getOrderDebtType());
        if (payableDTO.getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE) {
          statementAccountOrderDTO.setOrderTotalStr(StatementAccountConstant.RECEIVABLE + statementAccountOrderDTO.getTotal());
          statementAccountOrderDTO.setSettledAmountStr(StatementAccountConstant.RECEIVABLE + (0 - NumberUtil.doubleVal(payableDTO.getPaidAmount())));
          statementAccountOrderDTO.setDiscount(0 - NumberUtil.doubleVal(payableDTO.getDeduction()));
          statementAccountOrderDTO.setDebt(0 - NumberUtil.doubleVal(payableDTO.getCreditAmount()));
        } else if (payableDTO.getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
          statementAccountOrderDTO.setOrderTotalStr(StatementAccountConstant.PAY + statementAccountOrderDTO.getTotal());
          statementAccountOrderDTO.setSettledAmountStr(StatementAccountConstant.PAY + NumberUtil.doubleVal(payableDTO.getPaidAmount()));
          statementAccountOrderDTO.setDiscount(payableDTO.getDeduction());
          statementAccountOrderDTO.setDebt(payableDTO.getCreditAmount());
        }
      }
    }
    return statementAccountOrderDTOList;
  }

  /**
   * 根据查询条件查询对账单记录条数
   *
   * @param orderSearchConditionDTO
   * @return
   */
  public int countStatementAccountOrderList(OrderSearchConditionDTO orderSearchConditionDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countStatementAccountOrderList(orderSearchConditionDTO);
  }

  /**
   * 查询某个店铺下本期客户或者供应商对账id
   *
   * @param orderSearchConditionDTO
   * @return
   */
  @Override
  public List getCurrentStatementAccountOrder(OrderSearchConditionDTO orderSearchConditionDTO) {
    if (orderSearchConditionDTO == null || orderSearchConditionDTO.getShopId() == null || orderSearchConditionDTO.getCustomerOrSupplierId() == null || ArrayUtils.isEmpty(orderSearchConditionDTO.getOrderType())) {
      return null;
    }
    OrderTypes orderTypes = null;
    List list = null;

    if (OrderTypes.valueOf(orderSearchConditionDTO.getOrderType()[0]).equals(OrderTypes.CUSTOMER_STATEMENT_ACCOUNT)) {
      orderTypes = OrderTypes.CUSTOMER_STATEMENT_ACCOUNT;
      list = getCustomerCurrentStatementAccountOrder(orderSearchConditionDTO, orderTypes);
    } else if (OrderTypes.valueOf(orderSearchConditionDTO.getOrderType()[0]).equals(OrderTypes.SUPPLIER_STATEMENT_ACCOUNT)) {
      orderTypes = OrderTypes.SUPPLIER_STATEMENT_ACCOUNT;
      list = getSupplierCurrentStatementAccountOrder(orderSearchConditionDTO, orderTypes);
    } else {
      return null;
    }

    if (CollectionUtils.isNotEmpty(list)) {
      StatementAccountResultDTO statementAccountResultDTO = (StatementAccountResultDTO) list.get(0);
      int receivableSize = statementAccountResultDTO.getReceivableList().size();
      int payableSize = statementAccountResultDTO.getPayList().size();
      statementAccountResultDTO.setResultSize(receivableSize > payableSize ? receivableSize : payableSize);
      statementAccountResultDTO.setResultSize(1 + statementAccountResultDTO.getResultSize());

      statementAccountResultDTO.setTotalDebt(NumberUtil.toReserve(statementAccountResultDTO.getTotalDebt(), NumberUtil.MONEY_PRECISION));
      statementAccountResultDTO.setTotalPayable(NumberUtil.toReserve(statementAccountResultDTO.getTotalPayable(), NumberUtil.MONEY_PRECISION));
      statementAccountResultDTO.setTotalReceivable(NumberUtil.toReserve(statementAccountResultDTO.getTotalReceivable(), NumberUtil.MONEY_PRECISION));

      if (CollectionUtils.isNotEmpty(statementAccountResultDTO.getReceivableList())) {
        Collections.sort(statementAccountResultDTO.getReceivableList());
      }
      if (CollectionUtils.isNotEmpty(statementAccountResultDTO.getPayList())) {
        Collections.sort(statementAccountResultDTO.getPayList());

      }
    }

    return list;

  }

  /**
   * 查询某个客户或者供应商最后一次对账单
   *
   * @param shopId
   * @param customerOrSupplierId
   * @param orderType
   * @return
   */
  @Override
  public StatementAccountOrderDTO getLastStatementAccountOrder(Long shopId, Long customerOrSupplierId, OrderTypes orderType) {
    if (shopId == null || customerOrSupplierId == null || orderType == null) {
      return null;
    }

    if (orderType != OrderTypes.CUSTOMER_STATEMENT_ACCOUNT && orderType != OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getLastStatementAccountOrder(shopId, customerOrSupplierId, orderType);
  }

  /**
   * 根据开始时间、结束时间 店铺id、客户id查询实收记录
   *
   * @param shopId
   * @param customerId
   * @param startTime
   * @param endTime
   * @return
   */
  public List<ReceivableDTO> getReceivableListByCustomerId(Long shopId, Long customerId, Long startTime, Long endTime) {

    TxnWriter writer = txnDaoManager.getWriter();
    List<Receivable> receivableList = writer.getReceivableListByCustomerId(shopId, customerId, startTime, endTime);
    List<ReceivableDTO> receivableDTOList = new ArrayList<ReceivableDTO>();

    if (CollectionUtils.isEmpty(receivableList)) {
      return receivableDTOList;
    }
    for (Receivable receivable : receivableList) {
      receivableDTOList.add(receivable.toDTO());
    }
    return receivableDTOList;
  }

  /**
   * 根据开始时间、结束时间 店铺id、供应商id查询实付记录
   *
   * @param shopId
   * @param supplierId
   * @param startTime
   * @param endTime
   * @return
   */
  public List<PayableDTO> getPayableListBySupplierId(Long shopId, Long supplierId, Long startTime, Long endTime) {

    TxnWriter writer = txnDaoManager.getWriter();
    List<Payable> payableList = writer.getPayableListBySupplierId(shopId, supplierId, startTime, endTime);
    List<PayableDTO> payableDTOList = new ArrayList<PayableDTO>();
    if (CollectionUtils.isEmpty(payableList)) {
      return payableDTOList;
    }
    for (Payable payable : payableList) {
      payableDTOList.add(payable.toDTO());
    }
    return payableDTOList;
  }


  /**
   * 根据查询条件获得该客户的对账记录
   *
   * @param orderSearchConditionDTO
   * @param orderType
   * @return
   */
  public List getCustomerCurrentStatementAccountOrder(OrderSearchConditionDTO orderSearchConditionDTO, OrderTypes orderType) {
    List<Object> resultList = new ArrayList<Object>();//返回结果

    List<ReceivableDTO> receivableDTOList = this.getReceivableListByCustomerId(orderSearchConditionDTO.getShopId(), Long.valueOf(orderSearchConditionDTO.getCustomerOrSupplierId()), orderSearchConditionDTO.getStartTime(), orderSearchConditionDTO.getEndTime());
    CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(Long.valueOf(orderSearchConditionDTO.getCustomerOrSupplierId()));
    List<PayableDTO> payableDTOList = new ArrayList<PayableDTO>();
    StatementAccountResultDTO statementAccountResultDTO2 = null;  //作为供应商时候的单据
    StatementAccountResultDTO statementAccountResultDTO = null; //作为客户时候的单据

    if (customerDTO.getSupplierId() != null) {
      payableDTOList = this.getPayableListBySupplierId(orderSearchConditionDTO.getShopId(), customerDTO.getSupplierId(), orderSearchConditionDTO.getStartTime(), orderSearchConditionDTO.getEndTime());
    }
    if (CollectionUtils.isEmpty(receivableDTOList) && CollectionUtils.isEmpty(payableDTOList)) {
      return null;
    }

    //当收入和支出的单据数量都为1的时候
    if (receivableDTOList.size() <= 1 && payableDTOList.size() <= 1) {

      ReceivableDTO receivableDTO = CollectionUtil.getFirst(receivableDTOList);
      PayableDTO payableDTO = CollectionUtil.getFirst(payableDTOList);

      //如果是只有一个单据 而且是对账单 而且欠款为0 返回为空 没有可对账的单据
      if ((receivableDTO == null || (receivableDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT && receivableDTO.getDebt() == 0)) &&
          (payableDTO == null || (payableDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT && NumberUtil.doubleVal(payableDTO.getCreditAmount()) == 0))) {
        return null;
      }
    }
    statementAccountResultDTO = this.getStatementAccountResultDTO(receivableDTOList, orderType);
    statementAccountResultDTO2 = this.getStatementAccountResultDTO(payableDTOList, OrderTypes.SUPPLIER_STATEMENT_ACCOUNT);

    if (statementAccountResultDTO == null && statementAccountResultDTO2 == null) {
      return null;
    }

    if (statementAccountResultDTO != null) {
      if (statementAccountResultDTO2 != null) {
        if (statementAccountResultDTO.getTotalDebt() + statementAccountResultDTO2.getTotalDebt() >= 0) {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
        } else {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_PAYABLE);
        }
        statementAccountResultDTO.setStartDate(receivableDTOList.get(0).getVestDate());
        statementAccountResultDTO.setEndDate(receivableDTOList.get(receivableDTOList.size() - 1).getVestDate());
        statementAccountResultDTO.getPayList().addAll(statementAccountResultDTO2.getPayList());
        statementAccountResultDTO.getReceivableList().addAll(statementAccountResultDTO2.getReceivableList());
        statementAccountResultDTO.setTotalDebt(statementAccountResultDTO.getTotalDebt() + statementAccountResultDTO2.getTotalDebt());
        statementAccountResultDTO.setTotalPayable(statementAccountResultDTO.getTotalPayable() + statementAccountResultDTO2.getTotalPayable());
        statementAccountResultDTO.setTotalReceivable(statementAccountResultDTO.getTotalReceivable() + statementAccountResultDTO2.getTotalReceivable());
      } else {
        if (statementAccountResultDTO.getTotalDebt() >= 0) {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
        } else {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_PAYABLE);
        }
        statementAccountResultDTO.setStartDate(receivableDTOList.get(0).getVestDate());
        statementAccountResultDTO.setEndDate(receivableDTOList.get(receivableDTOList.size() - 1).getVestDate());
      }

    } else {
      if (statementAccountResultDTO2.getTotalDebt() >= 0) {
        statementAccountResultDTO2.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
      } else {
        statementAccountResultDTO2.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_PAYABLE);
      }
      statementAccountResultDTO2.setStartDate(payableDTOList.get(0).getPayTime());
      statementAccountResultDTO2.setEndDate(payableDTOList.get(payableDTOList.size() - 1).getPayTime());
    }
    List<StatementAccountOrderDTO> resultReceivable = new ArrayList<StatementAccountOrderDTO>();
    if (statementAccountResultDTO != null) {
      resultReceivable.addAll(statementAccountResultDTO.getReceivableList());
      resultReceivable.addAll(statementAccountResultDTO.getPayList());
    }
    if (statementAccountResultDTO2 != null) {
      resultReceivable.addAll(statementAccountResultDTO2.getReceivableList());
      resultReceivable.addAll(statementAccountResultDTO2.getPayList());
    }
    if (statementAccountResultDTO != null) {
      resultList.add(statementAccountResultDTO);
    } else {
      resultList.add(statementAccountResultDTO2);
    }

    resultList.add(JsonUtil.listToJsonNoQuote(resultReceivable));

    return resultList;
  }


  /**
   * 根据查询条件查询供应商欠款单据列表
   *
   * @param orderSearchConditionDTO
   * @param orderType:orderType.SUPPLIER_STATEMENT_ACCOUNT
   *
   * @return
   */
  public List getSupplierCurrentStatementAccountOrder(OrderSearchConditionDTO orderSearchConditionDTO, OrderTypes orderType) {
    List<Object> resultList = new ArrayList<Object>();//返回结果
    List<ReceivableDTO> receivableDTOList = new ArrayList<ReceivableDTO>();
    StatementAccountResultDTO statementAccountResultDTO2 = null;
    StatementAccountResultDTO statementAccountResultDTO = null;
    List<PayableDTO> payableDTOList = this.getPayableListBySupplierId(orderSearchConditionDTO.getShopId(), Long.valueOf(orderSearchConditionDTO.getCustomerOrSupplierId()), orderSearchConditionDTO.getStartTime(), orderSearchConditionDTO.getEndTime());
    SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(Long.valueOf(orderSearchConditionDTO.getCustomerOrSupplierId()));
    if (supplierDTO.getCustomerId() != null) {
      receivableDTOList = this.getReceivableListByCustomerId(orderSearchConditionDTO.getShopId(), supplierDTO.getCustomerId(), orderSearchConditionDTO.getStartTime(), orderSearchConditionDTO.getEndTime());
    }
    if (CollectionUtils.isEmpty(payableDTOList) && CollectionUtils.isEmpty(receivableDTOList)) {
      return null;
    }

    //当收入和支出的单据数量都为1的时候
    if (receivableDTOList.size() <= 1 && payableDTOList.size() <= 1) {

      ReceivableDTO receivableDTO = CollectionUtil.getFirst(receivableDTOList);
      PayableDTO payableDTO = CollectionUtil.getFirst(payableDTOList);

      //如果是只有一个单据 而且是对账单 而且欠款为0 返回为空 没有可对账的单据
      if ((receivableDTO == null || (receivableDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT && receivableDTO.getDebt() == 0)) &&
          (payableDTO == null || (payableDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT && NumberUtil.doubleVal(payableDTO.getCreditAmount()) == 0))) {
        return null;
      }
    }
    statementAccountResultDTO = this.getStatementAccountResultDTO(payableDTOList, orderType);
    statementAccountResultDTO2 = this.getStatementAccountResultDTO(receivableDTOList, OrderTypes.CUSTOMER_STATEMENT_ACCOUNT);

    if (statementAccountResultDTO == null && statementAccountResultDTO2 == null) {
      return null;
    }
    if (statementAccountResultDTO != null) {
      if (statementAccountResultDTO2 != null) {
        statementAccountResultDTO.setStartDate(payableDTOList.get(0).getPayTime());
        statementAccountResultDTO.setEndDate(payableDTOList.get(payableDTOList.size() - 1).getPayTime());
        if (statementAccountResultDTO.getTotalDebt() + statementAccountResultDTO2.getTotalDebt() >= 0) {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        } else {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_PAYABLE);
        }
        statementAccountResultDTO.getPayList().addAll(statementAccountResultDTO2.getPayList());
        statementAccountResultDTO.getReceivableList().addAll(statementAccountResultDTO2.getReceivableList());
        statementAccountResultDTO.setTotalDebt(statementAccountResultDTO.getTotalDebt() + statementAccountResultDTO2.getTotalDebt());
        statementAccountResultDTO.setTotalPayable(statementAccountResultDTO.getTotalPayable() + statementAccountResultDTO2.getTotalPayable());
        statementAccountResultDTO.setTotalReceivable(statementAccountResultDTO.getTotalReceivable() + statementAccountResultDTO2.getTotalReceivable());
      } else {
        statementAccountResultDTO.setStartDate(payableDTOList.get(0).getPayTime());
        statementAccountResultDTO.setEndDate(payableDTOList.get(payableDTOList.size() - 1).getPayTime());
        if (statementAccountResultDTO.getTotalDebt() >= 0) {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        } else {
          statementAccountResultDTO.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_PAYABLE);
        }
      }
    } else {
      if (statementAccountResultDTO2.getTotalDebt() >= 0) {
        statementAccountResultDTO2.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
      } else {
        statementAccountResultDTO2.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_PAYABLE);
      }
      statementAccountResultDTO2.setStartDate(receivableDTOList.get(0).getVestDate());
      statementAccountResultDTO2.setEndDate(receivableDTOList.get(receivableDTOList.size() - 1).getVestDate());
    }

    List<StatementAccountOrderDTO> resultReceivable = new ArrayList<StatementAccountOrderDTO>();
    if (statementAccountResultDTO != null) {
      resultReceivable.addAll(statementAccountResultDTO.getReceivableList());
      resultReceivable.addAll(statementAccountResultDTO.getPayList());
    }
    if (statementAccountResultDTO2 != null) {
      resultReceivable.addAll(statementAccountResultDTO2.getReceivableList());
      resultReceivable.addAll(statementAccountResultDTO2.getPayList());
    }
    if (statementAccountResultDTO != null) {
      resultList.add(statementAccountResultDTO);
    } else {
      resultList.add(statementAccountResultDTO2);
    }
    resultList.add(JsonUtil.listToJsonNoQuote(resultReceivable));
    return resultList;
  }


  /**
   * 根据单据类型和结果List返回查询结果
   *
   * @param objectList
   * @param orderTypes
   * @return
   */
  public StatementAccountResultDTO getStatementAccountResultDTO(List objectList, OrderTypes orderTypes) {
    if (CollectionUtils.isEmpty(objectList) || orderTypes == null) {
      return null;
    }
    StatementAccountResultDTO statementAccountResultDTO = new StatementAccountResultDTO();//返回结果封装类
    //店铺收入单据列表
    List<StatementAccountOrderDTO> receivableList = new ArrayList<StatementAccountOrderDTO>();
    //店铺支出单据列表
    List<StatementAccountOrderDTO> payList = new ArrayList<StatementAccountOrderDTO>();


    if (orderTypes == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
      for (Object object : objectList) {
        ReceivableDTO receivableDTO = (ReceivableDTO) object;

        StatementAccountOrderDTO resultDTO = receivableDTO.toStatementAccountOrderDTO(null);
        if (resultDTO == null || resultDTO.getOrderDebtType() == null) {
          continue;
        }

        resultDTO.setCustomerOrSupplierId(receivableDTO.getCustomerId());
        if (receivableDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
          resultDTO.setOrderTypeStr(StatementAccountConstant.LAST_STATEMENT_ACCOUNT);
        }
        if (OrderDebtType.CUSTOMER_DEBT_RECEIVABLE == resultDTO.getOrderDebtType()) {
          receivableList.add(resultDTO);
          statementAccountResultDTO.setTotalReceivable(statementAccountResultDTO.getTotalReceivable() + resultDTO.getDebt());
        } else if (OrderDebtType.CUSTOMER_DEBT_PAYABLE == resultDTO.getOrderDebtType()) {
          payList.add(resultDTO);
          statementAccountResultDTO.setTotalPayable(statementAccountResultDTO.getTotalPayable() + resultDTO.getDebt());
        }
      }
    } else if (orderTypes == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      for (Object object : objectList) {
        PayableDTO payableDTO = (PayableDTO) object;

        StatementAccountOrderDTO resultDTO = payableDTO.toStatementAccountOrderDTO(null);
        if (resultDTO == null || resultDTO.getOrderDebtType() == null) {
          continue;
        }
        resultDTO.setCustomerOrSupplierId(payableDTO.getSupplierId());
        if (payableDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
          resultDTO.setOrderTypeStr(StatementAccountConstant.LAST_STATEMENT_ACCOUNT);
        }
        if (OrderDebtType.SUPPLIER_DEBT_RECEIVABLE == resultDTO.getOrderDebtType()) {
          receivableList.add(resultDTO);
          statementAccountResultDTO.setTotalReceivable(statementAccountResultDTO.getTotalReceivable() + resultDTO.getDebt());
        } else if (OrderDebtType.SUPPLIER_DEBT_PAYABLE == resultDTO.getOrderDebtType()) {
          payList.add(resultDTO);
          statementAccountResultDTO.setTotalPayable(statementAccountResultDTO.getTotalPayable() + resultDTO.getDebt());
        }
      }
    }

    statementAccountResultDTO.setReceivableList(receivableList);
    statementAccountResultDTO.setPayList(payList);

    statementAccountResultDTO.setTotalDebt(statementAccountResultDTO.getTotalReceivable() - statementAccountResultDTO.getTotalPayable());
    return statementAccountResultDTO;
  }


  /**
   * 对账结算前进行校验
   *
   * @param statementAccountOrderDTO
   * @return
   */
  public Result validateStatementAccountBeforeSettle(StatementAccountOrderDTO statementAccountOrderDTO) {
    Result result = new Result();
    result.setSuccess(false);
    try {
      if (statementAccountOrderDTO == null) {
        throw new Exception("statementAccountOrderDTO is null");
      }
      String accountInfo = statementAccountOrderDTO.toString();
      if (StringUtil.isEmpty(statementAccountOrderDTO.getJsonStr())) {
        throw new Exception("statementAccountOrderDTO,jsonStr is null:" + accountInfo);
      }
      OrderDebtType orderDebtType = statementAccountOrderDTO.getOrderDebtType();
      if (orderDebtType == null) {
        throw new Exception("statementAccountOrderDTO.orderDebtType is null" + accountInfo);
      }

      OrderTypes orderType = statementAccountOrderDTO.getOrderType();
      if (orderType == null) {
        throw new Exception("statementAccountOrderDTO.orderType is null" + accountInfo);
      }

      if (orderType != OrderTypes.CUSTOMER_STATEMENT_ACCOUNT && orderType != OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
        throw new Exception("statementAccountOrderDTO.orderType is error" + orderType.toString());
      }

      if (Math.abs(statementAccountOrderDTO.getTotal() - statementAccountOrderDTO.getSettledAmount() -
          statementAccountOrderDTO.getDebt() - statementAccountOrderDTO.getDiscount()) > 0.0001) {
        result.setMsg(StatementAccountConstant.TOTAL_ERROR_MESSAGE);
        return result;
      }

      double totalSettledAmount  = NumberUtil.doubleVal(statementAccountOrderDTO.getCashAmount()) +
          NumberUtil.doubleVal(statementAccountOrderDTO.getBankAmount()) +
          NumberUtil.doubleVal(statementAccountOrderDTO.getBankCheckAmount()) +
          NumberUtil.doubleVal(statementAccountOrderDTO.getMemberAmount())+
          NumberUtil.doubleVal(statementAccountOrderDTO.getDepositAmount());

      if (Math.abs(statementAccountOrderDTO.getSettledAmount() - totalSettledAmount) > 0.001) {
        result.setMsg(StatementAccountConstant.SETTLE_ERROR_MESSAGE);
        return result;
      }
      if(StringUtil.isEmpty(statementAccountOrderDTO.getReceptNoListStr())){
         result.setMsg(StatementAccountConstant.ORDER_SELECTED_EMPTY);
        return result;
      }

      Gson gson = new Gson();
      List<StatementAccountOrderDTO> statementAccountOrderDTOList = gson.fromJson(statementAccountOrderDTO.getJsonStr(),
          new TypeToken<List<StatementAccountOrderDTO>>(){}.getType());
      if (CollectionUtils.isEmpty(statementAccountOrderDTOList)) {
        result.setMsg(StatementAccountConstant.SETTLE_ERROR_MESSAGE);
        return result;
      }

      Map<Long,StatementAccountOrderDTO> orderMap = new HashMap<Long,StatementAccountOrderDTO>();
      for(StatementAccountOrderDTO accountOrderDTO : statementAccountOrderDTOList){
        if(accountOrderDTO.getOrderId() != null){
          orderMap.put(accountOrderDTO.getOrderId(),accountOrderDTO);
        }
      }

      List<StatementAccountOrderDTO> selectedAccountOrderDTO = new ArrayList<StatementAccountOrderDTO>();
        String[] receiptNoListStrs = statementAccountOrderDTO.getReceptNoListStr().split(",");
        for (String st : receiptNoListStrs) {
          if (!NumberUtil.isNumber(st)) {
            continue;
          }
          if (orderMap.containsKey(Long.valueOf(st))) {
            selectedAccountOrderDTO.add(orderMap.get(Long.valueOf(st)));
          }
        }
      statementAccountOrderDTO.setOrderDTOList(selectedAccountOrderDTO);

      if(CollectionUtils.isEmpty(selectedAccountOrderDTO)) {
        result.setMsg(StatementAccountConstant.ORDER_SELECTED_EMPTY);
        return result;
      }

      Set<Long> customerIdSet = new HashSet<Long>();
      Set<Long> supplierIdSet = new HashSet<Long>();

      Long[] orderIds = new Long[selectedAccountOrderDTO.size()];
      for (int index = 0; index < selectedAccountOrderDTO.size(); index++) {
        StatementAccountOrderDTO orderDTO = selectedAccountOrderDTO.get(index);
        orderIds[index] = orderDTO.getOrderId();

        if (orderDTO.getCustomerOrSupplierId() == null) {
          result.setMsg(StatementAccountConstant.SETTLE_ERROR_MESSAGE);
          return result;
        }
        if (orderDTO.getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE || orderDTO.getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_PAYABLE) {
          customerIdSet.add(orderDTO.getCustomerOrSupplierId());
        } else if (orderDTO.getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE || orderDTO.getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
          supplierIdSet.add(orderDTO.getCustomerOrSupplierId());
        } else {
          result.setMsg(StatementAccountConstant.SETTLE_ERROR_MESSAGE);
          return result;
        }
      }


      if (CollectionUtils.isEmpty(customerIdSet) && CollectionUtils.isEmpty(supplierIdSet)) {
        result.setMsg(StatementAccountConstant.SETTLE_ERROR_MESSAGE);
        return result;
      } else if (customerIdSet.size() > 1) {
        result.setMsg(StatementAccountConstant.CUSTOMER_MORE_ONE);
        return result;
      } else if (supplierIdSet.size() > 1) {
        result.setMsg(StatementAccountConstant.SUPPLIER_MORE_ONE);
        return result;
      }


      if (ArrayUtils.isEmpty(orderIds)) {
        result.setMsg(StatementAccountConstant.ORDER_SELECTED_EMPTY);
        return result;
      }

      List<String> stringList = null;
      List<String> customerDebtList = null;
      List<String> supplierDebtList = null;
      Double totalDebt = null;
      int resultDebtOrderSize  = 0;

      //既是供应商又是客户的校验
      if(StringUtil.isNotEmpty(statementAccountOrderDTO.getIdentity()) && StatementAccountConstant.IDENTITY.equals(statementAccountOrderDTO.getIdentity())) {
        customerDebtList = this.getTotalDebtByOrderIds(statementAccountOrderDTO.getShopId(), OrderTypes.CUSTOMER_STATEMENT_ACCOUNT, orderIds);
        supplierDebtList = this.getTotalDebtByOrderIds(statementAccountOrderDTO.getShopId(), OrderTypes.SUPPLIER_STATEMENT_ACCOUNT, orderIds);
        totalDebt = NumberUtil.doubleVal(Double.valueOf(customerDebtList.get(0))) - NumberUtil.doubleVal(Double.valueOf(supplierDebtList.get(0)));
        resultDebtOrderSize = Integer.valueOf(customerDebtList.get(1)) + Integer.valueOf(supplierDebtList.get(1));

        IUserService userService = ServiceManager.getService(IUserService.class);

        //客户结算，校验结算单据中是否有关联供应商的单据
        if (orderType == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT && Integer.valueOf(supplierDebtList.get(1)) > 0) {
          CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(statementAccountOrderDTO.getCustomerOrSupplierId(), statementAccountOrderDTO.getShopId());
          if (customerDTO == null || customerDTO.getSupplierId() == null) {
            result.setMsg(StatementAccountConstant.CUSTOMER_ERROR);
            return result;
          }else if (!supplierIdSet.contains(customerDTO.getSupplierId())) {
            result.setMsg(StatementAccountConstant.CUSTOMER_RELATED_SUPPLIER_ERROR);
            return result;
          }
        } else if (orderType == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT && Integer.valueOf(customerDebtList.get(1)) > 0) {
          //供应商结算，校验是否有关联客户的单据
          SupplierDTO supplierDTO = userService.getSupplierById(statementAccountOrderDTO.getCustomerOrSupplierId());
          if (supplierDTO == null || supplierDTO.getCustomerId() == null) {
            result.setMsg(StatementAccountConstant.SUPPLIER_ERROR);
            return result;
          }else if (!customerIdSet.contains(supplierDTO.getCustomerId())) {
            result.setMsg(StatementAccountConstant.SUPPLIER_RELATED_CUSTOMER_ERROR);
            return result;
          }
        }
      } else {
        stringList = this.getTotalDebtByOrderIds(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getOrderType(), orderIds);
        totalDebt = NumberUtil.doubleVal(Double.valueOf(stringList.get(0)));
        resultDebtOrderSize = Integer.valueOf(stringList.get(1));
      }

      if(resultDebtOrderSize < orderIds.length) {
        result.setMsg(StatementAccountConstant.TOTAL_DEBT_ERROR_MESSAGE);
        return result;
      }

      if(totalDebt < 0) {
        totalDebt = 0 - totalDebt;
      }
      if (Math.abs(totalDebt - statementAccountOrderDTO.getTotal()) > 1) {
        result.setMsg(StatementAccountConstant.TOTAL_DEBT_ERROR_MESSAGE);
        return result;
      }


//      if(Integer.valueOf(stringList.get(1)).intValue() != statementAccountOrderDTOList2.size()) {
//        result.setMsg(StatementAccountConstant.TOTAL_DEBT_ERROR_MESSAGE);
//        return result;
//      }



      if (NumberUtil.doubleVal(statementAccountOrderDTO.getMemberAmount()) > 0) {
        IMembersService membersService = ServiceManager.getService(IMembersService.class);
        IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
        //单据包含会员相关信息 校验卡号和密码
        String resultStr = memberCheckerService.checkMemberAndPassword(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getAccountMemberNo(), statementAccountOrderDTO.getAccountMemberPassword());
        if (!StringUtil.isEmpty(resultStr)) {
          result.setMsg(resultStr);
          return result;
        }
        Member member = membersService.getMemberByShopIdAndMemberNo(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getAccountMemberNo());
        if (member == null || member.getStatus() == MemberStatus.DISABLED) {
          result.setMsg("会员卡信息输入不正确");
          return result;
        } else if (NumberUtil.doubleVal(member.getBalance()) < statementAccountOrderDTO.getMemberAmount()) {
          result.setMsg(MemberConstant.MEMBER_BALANCE_NOT_ENOUGH);
          return result;
        }
        member.setBalance(member.getBalance() - statementAccountOrderDTO.getMemberAmount());
        statementAccountOrderDTO.setMemberDTO(member.toDTO());
      }

      //如果有定金支付 判断定金支付
      if (NumberUtil.doubleVal(statementAccountOrderDTO.getDepositAmount()) > 0) {
        // modified by zhuj
        if (orderType == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
          ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
          Double sumPayable = supplierPayableService.getSumDepositBySupplierId(statementAccountOrderDTO.getCustomerOrSupplierId(), statementAccountOrderDTO.getShopId());
          sumPayable = NumberUtil.toReserve(sumPayable, NumberUtil.MONEY_PRECISION);
          if (statementAccountOrderDTO.getDepositAmount() > sumPayable) {
            result.setMsg(MemberConstant.DEPOSIT_NOT_ENOUGH);
            return result;
          }
        }else if(orderType == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT){
          ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
          CustomerDepositDTO depositDTO = customerDepositService.queryCustomerDepositByShopIdAndCustomerId(statementAccountOrderDTO.getShopId(),statementAccountOrderDTO.getCustomerOrSupplierId());
          Double sumPayable = depositDTO.getActuallyPaid();
          sumPayable = NumberUtil.toReserve(sumPayable, NumberUtil.MONEY_PRECISION);
          if (statementAccountOrderDTO.getDepositAmount() > sumPayable) {
            result.setMsg(MemberConstant.CUSTOMER_DEPOSIT_NOT_ENOUGH);
            return result;
          }
        }
      }

      result.setSuccess(true);
      return result;

    } catch (Exception e) {
      LOG.error("statementAccountService.validateStatementAccountBeforeSettle");
      LOG.error(e.getMessage(), e);
      result.setMsg(StatementAccountConstant.STATEMENT_ACCOUNT_ORDER_ERROR);
    }
    return result;
  }

  /**
   * 根据前款单据的ids或者总欠款
   *
   * @param shopId
   * @param orderType
   * @param orderIds
   * @return
   */
  public List<String> getTotalDebtByOrderIds(Long shopId, OrderTypes orderType, Long[] orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();

    if (orderType == null) {
      LOG.error("StatementAccountService.getTotalDebtByOrderIds orderType is null: shopId:" + shopId + ",orderIds:" + orderIds.toString());
      List<String> stringList = new ArrayList<String>();
      stringList.add("0");
      stringList.add("0");
      return stringList;
    }

    return writer.getTotalDebtByOrderIds(shopId, orderType, orderIds);
  }

  /**
   * 对账单结算
   * //1.更新单据statementAccountOrderId
   * //2.更新单据receivable信息
   * //3.更新单据debt信息
   * //4.更新客户信息 customer_solr
   * //5.更新order solr
   * //6.更新流水信息 更新营业信息
   *
   * @param statementAccountOrderDTO
   * @return
   */
  public Result settleStatementAccountOrder(StatementAccountOrderDTO statementAccountOrderDTO) throws Exception {
    Result result = new Result();
    result.setSuccess(false);

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //保存单据
      statementAccountOrderDTO.setVestDate(System.currentTimeMillis());
      statementAccountOrderDTO.setOrderStatus(OrderStatus.SETTLED);
      statementAccountOrderDTO.setStartDate(statementAccountOrderDTO.getStartDateStr() == null ? null : Long.valueOf(statementAccountOrderDTO.getStartDateStr()));
      statementAccountOrderDTO.setEndDate(statementAccountOrderDTO.getEndDateStr() == null ? null : Long.valueOf(statementAccountOrderDTO.getEndDateStr()));
      StatementAccountOrder statementAccountOrder = new StatementAccountOrder(statementAccountOrderDTO);
      writer.save(statementAccountOrder);
      statementAccountOrderDTO.setId(statementAccountOrder.getId());

      if (OrderTypes.CUSTOMER_STATEMENT_ACCOUNT == statementAccountOrderDTO.getOrderType()) {
        ReceivableDTO receivableDTO = statementAccountOrderDTO.toReceivableDTO();
        Receivable receivable = new Receivable();
        receivable = receivable.fromDTO(receivableDTO);
        writer.save(receivable);
        receivableDTO.setId(receivable.getId());
        ReceivableHistoryDTO receivableHistoryDTO  = receivableDTO.toReceivableHistoryDTO();
        ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
        writer.save(receivableHistory);
        receivableHistoryDTO.setId(receivableHistory.getId());

        ReceptionRecordDTO receptionRecordDTO = statementAccountOrderDTO.toReceptionRecordDTO(receivableDTO);
        receptionRecordDTO.setReceivableHistoryId(receivableHistory.getId());
        ReceptionRecord receptionRecord = new ReceptionRecord();
        receptionRecord = receptionRecord.fromDTO(receptionRecordDTO);
        writer.save(receptionRecord);

        if (receivable.getDeposit() > 0.001) {
          // add by zhuj 预收款使用
          ICustomerDepositService customerDepositService=ServiceManager.getService(ICustomerDepositService.class);

          CustomerDepositDTO customerDepositDTO = new CustomerDepositDTO();
          customerDepositDTO.setOperator(statementAccountOrderDTO.getSalesMan());
          customerDepositDTO.setShopId(statementAccountOrderDTO.getShopId());
          customerDepositDTO.setActuallyPaid(receivableDTO.getDeposit());
          customerDepositDTO.setCustomerId(receivableDTO.getCustomerId());
          DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
          depositOrderDTO.setDepositType(DepositType.COMPARE.getScene());
          depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
          depositOrderDTO.setRelatedOrderId(statementAccountOrderDTO.getId());
          depositOrderDTO.setRelatedOrderNo(statementAccountOrderDTO.getReceiptNo());

          customerDepositService.customerDepositUse(customerDepositDTO, depositOrderDTO, writer);
          // add end
        }

        //add by WLF 新增欠款提醒，更新缓存
        if(statementAccountOrderDTO.getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE){
          ServiceManager.getService(ITxnService.class).saveRemindEvent(writer, statementAccountOrderDTO);
          //by qxy move to thread with slow query
//          ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT,statementAccountOrderDTO.getShopId());
        }

        if (statementAccountOrderDTO.getDebt() > 0  && statementAccountOrderDTO.getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE) {
          DebtDTO debtDTO = statementAccountOrderDTO.toDebtDTO(receivableDTO);
          Debt debt = new Debt();
          debt = debt.fromDTO(debtDTO, false);
          writer.save(debt);
        }
        this.settleOrderByStatementAccountOrder(writer, statementAccountOrderDTO,receivableHistoryDTO);
      } else if (OrderTypes.SUPPLIER_STATEMENT_ACCOUNT == statementAccountOrderDTO.getOrderType()) {
        PayableDTO payableDTO = statementAccountOrderDTO.toPayableDTO();
        payableDTO.setStatementAccount(0D);
        Payable payable = new Payable(payableDTO);
        writer.save(payable);
        payableDTO.setId(payable.getId());
        PayableHistoryDTO payableHistoryDTO = statementAccountOrderDTO.toPayableHistoryDTO();
        PayableHistory payableHistory = new PayableHistory(payableHistoryDTO);
        writer.save(payableHistory);
        payableHistoryDTO.setId(payableHistory.getId());

        PayableHistoryRecordDTO payableHistoryRecordDTO = statementAccountOrderDTO.toPayableHistoryRecordDTO(payableDTO, payableHistoryDTO);
        PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord(payableHistoryRecordDTO);
        writer.save(payableHistoryRecord);

        // add by zhuj 预付款使用
        if (payable.getDeposit() > 0.001) {
          ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
          DepositDTO depositDTO = new DepositDTO();
          depositDTO.setOperator(statementAccountOrderDTO.getSalesMan());
          depositDTO.setShopId(statementAccountOrderDTO.getShopId());
          depositDTO.setActuallyPaid(payableDTO.getDeposit());
          depositDTO.setSupplierId(payableDTO.getSupplierId());
          DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
          depositOrderDTO.setDepositType(DepositType.COMPARE.getScene());
          depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
          depositOrderDTO.setRelatedOrderId(statementAccountOrderDTO.getId());
          depositOrderDTO.setRelatedOrderNo(statementAccountOrderDTO.getReceiptNo());
          supplierPayableService.supplierDepositUse(depositDTO,depositOrderDTO,writer);
        }
        // add end
          if(statementAccountOrderDTO.getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE){
              ServiceManager.getService(ITxnService.class).saveRemindEvent(writer, statementAccountOrderDTO);
          }
        this.settleOrderByStatementAccountOrder(writer, statementAccountOrderDTO, payableDTO, payableHistoryDTO);
      }
      writer.commit(status);

      status = writer.begin();
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      for (StatementAccountOrderDTO orderDTO : statementAccountOrderDTO.getOrderDTOList()) {
        Long orderId = orderDTO.getOrderId();
        OrderTypes orderTypes = orderDTO.getOrderType();
        txnService.cancelRemindEventByOrderTypeAndOrderId(writer, RemindEventType.DEBT, orderTypes, orderId);
      }
            //by qxy move to thread with slow query
//      txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, statementAccountOrderDTO.getShopId());
      writer.commit(status);
      result.setSuccess(true);
    } catch (Exception e) {
      result.setMsg(StatementAccountConstant.STATEMENT_ACCOUNT_ORDER_ERROR);
      LOG.error("statementAccountService.settleStatementAccountOrder" + statementAccountOrderDTO.toString());
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  public void settleOrderByStatementAccountOrder(TxnWriter writer, StatementAccountOrderDTO statementAccountOrderDTO, PayableDTO payableDTO, PayableHistoryDTO payableHistoryDTO) throws Exception {

    List<StatementAccountOrderDTO> statementAccountOrderDTOList = statementAccountOrderDTO.getOrderDTOList();

    for (StatementAccountOrderDTO orderDTO : statementAccountOrderDTOList) {
      Long orderId = orderDTO.getOrderId();
      OrderTypes orderTypes = orderDTO.getOrderType();

      this.setStatementAccountOrderIdByOrderId(writer, orderId, orderTypes, statementAccountOrderDTO.getId());

      Long payableId = orderDTO.getPayableId();
      Long receivableId = orderDTO.getReceivableId();
      if(payableId != null) {
          Payable payable = writer.getById(Payable.class, payableId);
          double totalDebt = NumberUtil.doubleVal(payable.getCreditAmount());

          payable.setStatementAccountOrderId(statementAccountOrderDTO.getId());
          payable.setPaidAmount(NumberUtil.doubleVal(payable.getPaidAmount()) + totalDebt);
          payable.setStatementAccount(totalDebt);
          payable.setCreditAmount(0D);
          writer.update(payable);
          PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
          payableHistoryRecordDTO.setPayer(statementAccountOrderDTO.getSalesMan());
          payableHistoryRecordDTO.setPayerId(statementAccountOrderDTO.getSalesManId());
          payableHistoryRecordDTO.setShopId(payable.getShopId());
          payableHistoryRecordDTO.setSupplierId(payable.getSupplierId());
          payableHistoryRecordDTO.setPayableId(payable.getId());
          payableHistoryRecordDTO.setPurchaseInventoryId(payable.getPurchaseInventoryId());
          payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
          payableHistoryRecordDTO.setMaterialName(payable.getMaterialName());
          payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(payable.getAmount(), 0d));
          payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payable.getPaidAmount(), 0d));
          payableHistoryRecordDTO.setCheckNo(payableHistoryDTO.getCheckNo());
          payableHistoryRecordDTO.setStatus(PayStatus.USE);
          payableHistoryRecordDTO.setDayType(DayType.STATEMENT_ACCOUNT);
          payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
          payableHistoryRecordDTO.setPaymentType(PaymentTypes.STATEMENT_ACCOUNT);
          payableHistoryRecordDTO.setPayer(payableHistoryDTO.getPayer());
          payableHistoryRecordDTO.setPayerId(payableHistoryDTO.getPayerId());
          payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + totalDebt);
          payableHistoryRecordDTO.setStatementAmount(totalDebt);

          PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord(payableHistoryRecordDTO);
          writer.save(payableHistoryRecord);
      }
      if(receivableId != null) {
          Receivable receivable = writer.getById(Receivable.class, receivableId);
          double orderDebt = receivable.getDebt();
          receivable.setStatementAccountOrderId(statementAccountOrderDTO.getId());
          receivable.setSettledAmount(receivable.getSettledAmount() + receivable.getDebt());
          receivable.setStatementAmount(receivable.getDebt());
          receivable.setDebt(0D);
          receivable.setRemindTime(null);
          writer.update(receivable);
          if (orderDebt != 0) {
              DebtDTO debtDTO = ServiceManager.getService(ITxnService.class).getDebtByShopIdOrderId(statementAccountOrderDTO.getShopId(), orderId);
              if (debtDTO != null) {
                  Debt debt = writer.getById(Debt.class, Long.valueOf(debtDTO.getId()));
                  debt.setSettledAmount(debt.getTotalAmount());
                  debt.setDebt(0D);
                  debt.setPayTime(statementAccountOrderDTO.getVestDate());
                  debt.setRemindTime(statementAccountOrderDTO.getPaymentTime());
                  debt.setStatus(TxnConstant.DebtStatus.DEBT_STATUS_SETTLE);
                  debt.setStatusEnum(DebtStatus.SETTLED);
              }
          }
          List<ReceptionRecordDTO> receptionRecordDTOList = null;
              //获得该单据的收入记录
              receptionRecordDTOList = ServiceManager.getService(ITxnService.class).getReceptionRecordByOrderId(statementAccountOrderDTO.getShopId(), orderId, null);
              if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
                  throw new Exception("statementAccountService.settleOrderByStatementAccountOrder receptionRecordList is empty");
              }

              int recordNum = receptionRecordDTOList.get(0).getRecordNum();
              recordNum++;

              ReceptionRecord receptionRecord = new ReceptionRecord();
              receptionRecord.setReceivableId(receivableId);
              receptionRecord.setPayee(statementAccountOrderDTO.getSalesMan());
              receptionRecord.setPayeeId(statementAccountOrderDTO.getSalesManId());
              receptionRecord.setAmount(orderDebt);
              receptionRecord.setCash(0D);
              receptionRecord.setBankCard(0D);
              receptionRecord.setCheque(0D);
              receptionRecord.setMemberBalancePay(0D);
              receptionRecord.setRecordNum(recordNum);
              receptionRecord.setOriginDebt(orderDebt);
              receptionRecord.setRemainDebt(0D);
              receptionRecord.setShopId(statementAccountOrderDTO.getShopId());
              receptionRecord.setOrderId(orderId);
              receptionRecord.setReceptionDate(statementAccountOrderDTO.getVestDate());
              receptionRecord.setOrderTotal(receivable.getTotal());
              receptionRecord.setOrderTypeEnum(OrderTypes.CUSTOMER_STATEMENT_DEBT);
              receptionRecord.setOrderStatusEnum(OrderStatus.STATEMENT_ACCOUNTED);//单据的状态更改为已对账
              receptionRecord.setDayType(DayType.STATEMENT_ACCOUNT);
              receptionRecord.setStatementAmount(orderDebt);
              receptionRecord.setAfterMemberDiscountTotal(receivable.getAfterMemberDiscountTotal());
              writer.save(receptionRecord);

      }
    }
  }


  public void settleOrderByStatementAccountOrder(TxnWriter writer, StatementAccountOrderDTO statementAccountOrderDTO,ReceivableHistoryDTO receivableHistoryDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<StatementAccountOrderDTO> statementAccountOrderDTOList = statementAccountOrderDTO.getOrderDTOList();
    
    for (StatementAccountOrderDTO orderDTO : statementAccountOrderDTOList) {
      Long orderId = orderDTO.getOrderId();
      OrderTypes orderTypes = orderDTO.getOrderType();

      this.setStatementAccountOrderIdByOrderId(writer, orderId, orderTypes, statementAccountOrderDTO.getId());

      Long receivableId = orderDTO.getReceivableId();
      Long payableId = orderDTO.getPayableId();
      double orderDebt = 0;
      Receivable receivable = null;
      Payable payable = null;
      if(receivableId != null) {
          receivable = writer.getById(Receivable.class, receivableId);
          orderDebt = receivable.getDebt();
          receivable.setStatementAccountOrderId(statementAccountOrderDTO.getId());
          receivable.setSettledAmount(receivable.getSettledAmount() + receivable.getDebt());
          receivable.setStatementAmount(receivable.getDebt());
          receivable.setDebt(0D);
          receivable.setRemindTime(null);
          writer.update(receivable);
      }
      if(payableId != null) {
          payable = writer.getById(Payable.class, payableId);
          orderDebt = payable.getCreditAmount();
          payable.setStatementAccountOrderId(statementAccountOrderDTO.getId());
          payable.setPaidAmount(payable.getPaidAmount() +  payable.getCreditAmount());
          payable.setStatementAccount( payable.getCreditAmount());
          payable.setCreditAmount(0D);
          writer.update(payable);
          PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
          payableHistoryRecordDTO.setPayer(statementAccountOrderDTO.getSalesMan());
          payableHistoryRecordDTO.setPayerId(statementAccountOrderDTO.getSalesManId());
          payableHistoryRecordDTO.setShopId(payable.getShopId());
          payableHistoryRecordDTO.setSupplierId(payable.getSupplierId());
          payableHistoryRecordDTO.setPayableId(payable.getId());
          payableHistoryRecordDTO.setPurchaseInventoryId(payable.getPurchaseInventoryId());
          payableHistoryRecordDTO.setMaterialName(payable.getMaterialName());
          payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(payable.getAmount(), 0d));
          payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(payable.getPaidAmount(), 0d));
          payableHistoryRecordDTO.setStatus(PayStatus.USE);
          payableHistoryRecordDTO.setDayType(DayType.STATEMENT_ACCOUNT);
          payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
          payableHistoryRecordDTO.setPaymentType(PaymentTypes.STATEMENT_ACCOUNT);
          payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d) + orderDebt);
          payableHistoryRecordDTO.setStatementAmount(orderDebt);

          PayableHistoryRecord payableHistoryRecord = new PayableHistoryRecord(payableHistoryRecordDTO);
          writer.save(payableHistoryRecord);
      }

      if (orderDebt != 0) {
        DebtDTO debtDTO = txnService.getDebtByShopIdOrderId(statementAccountOrderDTO.getShopId(), orderId);
        if (debtDTO != null) {
          Debt debt = writer.getById(Debt.class, Long.valueOf(debtDTO.getId()));
          debt.setSettledAmount(debt.getTotalAmount());
          debt.setDebt(0D);
          debt.setPayTime(statementAccountOrderDTO.getVestDate());
          debt.setRemindTime(statementAccountOrderDTO.getPaymentTime());
          debt.setStatus(TxnConstant.DebtStatus.DEBT_STATUS_SETTLE);
          debt.setStatusEnum(DebtStatus.SETTLED);
        }
      }
        List<ReceptionRecordDTO> receptionRecordDTOList = null;


      if(receivableId != null) {
          //获得该单据的收入记录
          receptionRecordDTOList = txnService.getReceptionRecordByOrderId(statementAccountOrderDTO.getShopId(), orderId, null);
          if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
              throw new Exception("statementAccountService.settleOrderByStatementAccountOrder receptionRecordList is empty");
          }

          int recordNum = receptionRecordDTOList.get(0).getRecordNum();
          recordNum++;

          ReceptionRecord receptionRecord = new ReceptionRecord();
          receptionRecord.setReceivableId(receivableId);
          receptionRecord.setPayee(statementAccountOrderDTO.getSalesMan());
          receptionRecord.setPayeeId(statementAccountOrderDTO.getSalesManId());
          receptionRecord.setAmount(orderDebt);
          receptionRecord.setCash(0D);
          receptionRecord.setBankCard(0D);
          receptionRecord.setCheque(0D);
          receptionRecord.setMemberBalancePay(0D);
          receptionRecord.setRecordNum(recordNum);
          receptionRecord.setOriginDebt(orderDebt);
          receptionRecord.setRemainDebt(0D);
          receptionRecord.setShopId(statementAccountOrderDTO.getShopId());
          receptionRecord.setOrderId(orderId);
          receptionRecord.setReceptionDate(statementAccountOrderDTO.getVestDate());
          receptionRecord.setOrderTotal(receivable.getTotal());
          receptionRecord.setOrderTypeEnum(OrderTypes.CUSTOMER_STATEMENT_DEBT);
          receptionRecord.setOrderStatusEnum(OrderStatus.STATEMENT_ACCOUNTED);//单据的状态更改为已对账
          receptionRecord.setDayType(DayType.STATEMENT_ACCOUNT);
          receptionRecord.setStatementAmount(orderDebt);
          receptionRecord.setAfterMemberDiscountTotal(receivable.getAfterMemberDiscountTotal());
          receptionRecord.setReceivableHistoryId(receivableHistoryDTO.getId());
          writer.save(receptionRecord);
      }

    }
  }

  public void setStatementAccountOrderIdByOrderId(TxnWriter txnWriter, Long orderId, OrderTypes orderType, Long statementAccountOrderId) throws Exception {

    if (orderId == null || statementAccountOrderId == null || orderType == null) {
      return;
    }
    if (orderType == OrderTypes.WASH_BEAUTY) {
      WashBeautyOrder washBeautyOrder = txnWriter.getById(WashBeautyOrder.class, orderId);
      washBeautyOrder.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(washBeautyOrder);

    } else if (orderType == OrderTypes.REPAIR) {
      RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, orderId);
      repairOrder.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(repairOrder);
    } else if (orderType == OrderTypes.SALE) {
      SalesOrder salesOrder = txnWriter.getById(SalesOrder.class, orderId);
      salesOrder.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(salesOrder);
    } else if (orderType == OrderTypes.SALE_RETURN) {
      SalesReturn salesReturn = txnWriter.getById(SalesReturn.class, orderId);
      salesReturn.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(salesReturn);

    } else if (orderType == OrderTypes.MEMBER_BUY_CARD) {
      MemberCardOrder memberCardOrder = txnWriter.getById(MemberCardOrder.class, orderId);
      memberCardOrder.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(memberCardOrder);
    } else if (orderType == OrderTypes.INVENTORY) {
      PurchaseInventory purchaseInventory = txnWriter.getById(PurchaseInventory.class, orderId);
      purchaseInventory.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(purchaseInventory);
    } else if (orderType == OrderTypes.RETURN) {
      PurchaseReturn purchaseReturn = txnWriter.getById(PurchaseReturn.class, orderId);
      purchaseReturn.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(purchaseReturn);
    } else if (orderType == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT || orderType == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      StatementAccountOrder statementAccountOrder = txnWriter.getById(StatementAccountOrder.class, orderId);
      statementAccountOrder.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(statementAccountOrder);
    }else if (orderType == OrderTypes.MEMBER_RETURN_CARD) {
      MemberCardReturn memberCardReturn = txnWriter.getById(MemberCardReturn.class, orderId);
      memberCardReturn.setStatementAccountOrderId(statementAccountOrderId);
      txnWriter.update(memberCardReturn);
    }
  }

  /**
   * @param shopId
   * @param customerOrSupplierId
   * @param salesMan
   * @return
   */
  @Override
  public List getOperatorByCustomerOrSupplierId(Long shopId, Long customerOrSupplierId, String salesMan) {
    List<StatementAccountOrder> statementAccountOrderList = null;
    TxnWriter writer = txnDaoManager.getWriter();
    statementAccountOrderList = writer.getOperatorByCustomerOrSupplierId(shopId, customerOrSupplierId, salesMan);
    if (CollectionUtils.isEmpty(statementAccountOrderList)) {
      return null;
    }
    List<StatementAccountOrderDTO> statementAccountOrderDTOList = new ArrayList<StatementAccountOrderDTO>();
    for (StatementAccountOrder statementAccountOrder : statementAccountOrderList) {
      statementAccountOrderDTOList.add(statementAccountOrder.toDTO());
    }
    return statementAccountOrderDTOList;
  }

  /**
   * 根据对账单id查询对账单详细信息
   *
   * @param statementAccountOrderId
   * @return
   */
  public StatementAccountOrderDTO getStatementOrderInfo(Long statementAccountOrderId) throws Exception {
    StatementAccountOrderDTO statementAccountOrderDTO = this.getStatementAccountOrderById(statementAccountOrderId);
    if (statementAccountOrderDTO == null) {
      LOG.error("statementAccountOrder is null," + statementAccountOrderId);
      return null;
    }

    TxnWriter txnWriter = txnDaoManager.getWriter();

    List<StatementAccountOrderDTO> statementAccountOrderDTOList = new ArrayList<StatementAccountOrderDTO>();

    if (statementAccountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
      Receivable orderReceivable = txnWriter.getReceivableByShopIdAndOrderId(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getId());
      if (orderReceivable == null) {
        LOG.error("orderReceivable is null," + statementAccountOrderId);
        return null;
      }
      statementAccountOrderDTO = orderReceivable.toDTO().toStatementAccountOrderDTO(statementAccountOrderDTO);

      List<ReceptionRecord> receptionRecordList = txnWriter.getReceptionRecordByOrderId(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getId(), statementAccountOrderDTO.getOrderType());
      if (CollectionUtils.isNotEmpty(receptionRecordList)) {
        statementAccountOrderDTO.setBankCheckNo("");
        for (ReceptionRecord receptionRecord : receptionRecordList) {
          statementAccountOrderDTO.setBankCheckNo(statementAccountOrderDTO.getBankCheckNo() + " " + receptionRecord.getChequeNo());
        }
      }

    } else if (statementAccountOrderDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      Payable orderPayable = txnWriter.getPayableDTOByOrderId(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getId(), false);
      if (orderPayable == null) {
        LOG.error("orderPayable is null," + statementAccountOrderId);
        return null;
      }

      statementAccountOrderDTO = orderPayable.toDTO().toStatementAccountOrderDTO(statementAccountOrderDTO);
      List<PayableHistoryRecord> payableHistoryRecordList = txnWriter.getPayableHistoryRecord(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getCustomerOrSupplierId(), statementAccountOrderDTO.getId(), null);
      if (CollectionUtils.isNotEmpty(payableHistoryRecordList)) {
        statementAccountOrderDTO.setBankCheckNo("");
        for (PayableHistoryRecord payableHistoryRecord : payableHistoryRecordList) {
          statementAccountOrderDTO.setBankCheckNo(statementAccountOrderDTO.getBankCheckNo() + " " + payableHistoryRecord.getCheckNo() == null ? "" : payableHistoryRecord.getCheckNo());
        }
      }
    }

    List<Receivable> receivableList = txnWriter.getReceivableListByStatementOrderId(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getId());
    if (CollectionUtils.isNotEmpty(receivableList)) {
      for (Receivable receivable : receivableList) {
        ReceivableDTO receivableDTO = receivable.toDTO();
        StatementAccountOrderDTO accountOrderDTO = receivableDTO.toStatementAccountOrderDTO(null);
        accountOrderDTO.setSettledAmount(NumberUtil.toReserve(NumberUtil.doubleVal(accountOrderDTO.getSettledAmount()) - NumberUtil.doubleVal(accountOrderDTO.getStatementAmount()), NumberUtil.MONEY_PRECISION));
        if (accountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT || accountOrderDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
          accountOrderDTO.setOrderTypeStr(StatementAccountConstant.LAST_STATEMENT_ACCOUNT);
        }
        statementAccountOrderDTOList.add(accountOrderDTO);
      }
    }

    List<Payable> payableList = txnWriter.getPayableListByStatementOrderId(statementAccountOrderDTO.getShopId(), statementAccountOrderDTO.getId());
    if (CollectionUtils.isNotEmpty(payableList)) {
      for (Payable payable : payableList) {
        PayableDTO payableDTO = payable.toDTO();
        StatementAccountOrderDTO accountOrderDTO = payableDTO.toStatementAccountOrderDTO(null);
        accountOrderDTO.setSettledAmount(NumberUtil.toReserve(NumberUtil.doubleVal(accountOrderDTO.getSettledAmount()) - NumberUtil.doubleVal(accountOrderDTO.getStatementAmount()), NumberUtil.MONEY_PRECISION));
        if (accountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT || accountOrderDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
          accountOrderDTO.setOrderTypeStr(StatementAccountConstant.LAST_STATEMENT_ACCOUNT);
        }
        statementAccountOrderDTOList.add(accountOrderDTO);
      }
    }

    if(CollectionUtils.isNotEmpty(statementAccountOrderDTOList)){
      Collections.sort(statementAccountOrderDTOList);
    }
    statementAccountOrderDTO.setOrderDTOList(statementAccountOrderDTOList);


    if (NumberUtil.doubleVal(statementAccountOrderDTO.getTotalReceivable()) >= NumberUtil.doubleVal(statementAccountOrderDTO.getTotalPayable())) {
      statementAccountOrderDTO.setOrderTotalStr(StatementAccountConstant.RECEIVABLE_STR + statementAccountOrderDTO.getTotal());
      statementAccountOrderDTO.setSettledAmountStr(StatementAccountConstant.RECEIVABLE + statementAccountOrderDTO.getSettledAmount());
    } else {
      statementAccountOrderDTO.setOrderTotalStr(StatementAccountConstant.PAY_STR + statementAccountOrderDTO.getTotal());
      statementAccountOrderDTO.setSettledAmountStr(StatementAccountConstant.PAY + statementAccountOrderDTO.getSettledAmount());
    }
    return statementAccountOrderDTO;

  }

  public StatementAccountOrderDTO getStatementAccountOrderById(Long orderId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    StatementAccountOrder statementAccountOrder = txnWriter.getById(StatementAccountOrder.class, orderId);
    if (statementAccountOrder == null) {
      return null;
    }
    return statementAccountOrder.toDTO();
  }

}
