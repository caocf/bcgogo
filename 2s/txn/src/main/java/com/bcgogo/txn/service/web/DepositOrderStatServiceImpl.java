package com.bcgogo.txn.service.web;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.InOutFlag;
import com.bcgogo.enums.SortObj;
import com.bcgogo.stat.dto.DepositStatConditionDTO;
import com.bcgogo.txn.dto.DepositOrderDTO;
import com.bcgogo.txn.model.DepositOrder;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.IDepositOrderStatService;
import com.bcgogo.user.service.IContactService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-21
 * Time: 下午9:26
 */
@Component
public class DepositOrderStatServiceImpl implements IDepositOrderStatService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public List<DepositOrderDTO> queryDepositOrdersByStatCondition(DepositStatConditionDTO depositStatConditionDTO, Pager pager) {
 if (depositStatConditionDTO.getShopId()== null) {
      return new ArrayList<DepositOrderDTO>();
    }
    TxnWriter tw = this.txnDaoManager.getWriter();
    List<DepositOrderDTO> depositOrderDTOs;

    try {
      SortObj sortObj = new SortObj();
      sortObj.setSortName(depositStatConditionDTO.getSortName());
      sortObj.setSortFlag(depositStatConditionDTO.getSortFlag());
      List<DepositOrder> depositOrders = new ArrayList<DepositOrder>();
      if ((depositStatConditionDTO.getCustomerId() != null || depositStatConditionDTO.getSupplierId() != null) || (CollectionUtils.isEmpty(depositStatConditionDTO.getCustomerIds())) && CollectionUtils.isEmpty(depositStatConditionDTO.getSupplierIds())) {
        depositOrders = tw.queryDepositOrderByConditions(depositStatConditionDTO.getShopId(), depositStatConditionDTO.getCustomerId(), depositStatConditionDTO.getSupplierId(), buildInOutList(depositStatConditionDTO.getInOut()), sortObj, depositStatConditionDTO.getStartTime(), depositStatConditionDTO.getEndTime(), depositStatConditionDTO.getType(), pager);
      } else {
        if (depositStatConditionDTO.getCustomerIds() != null || depositStatConditionDTO.getSupplierIds() != null) { // 如果有id列表优先通过id列表查询
          if (StringUtils.equals("customer", depositStatConditionDTO.getType())) {
            depositOrders = tw.queryDepositOrderByIdsAndType(depositStatConditionDTO.getShopId(), depositStatConditionDTO.getCustomerIds(), buildInOutList(depositStatConditionDTO.getInOut()), sortObj, depositStatConditionDTO.getStartTime(), depositStatConditionDTO.getEndTime(), depositStatConditionDTO.getType(), pager);
          } else {
            depositOrders = tw.queryDepositOrderByIdsAndType(depositStatConditionDTO.getShopId(), depositStatConditionDTO.getSupplierIds(), buildInOutList(depositStatConditionDTO.getInOut()), sortObj, depositStatConditionDTO.getStartTime(), depositStatConditionDTO.getEndTime(), depositStatConditionDTO.getType(), pager);
          }
        }
      }
      if (CollectionUtils.isEmpty(depositOrders))
        return new ArrayList<DepositOrderDTO>();
      depositOrderDTOs = new ArrayList<DepositOrderDTO>(depositOrders.size() + 1);
      for (DepositOrder depositOrder : depositOrders) {
        DepositOrderDTO depositOrderDTO = depositOrder.toDTO();
        depositOrderDTO.buildDepositType(); // 页面友好显示
        depositOrderDTOs.add(depositOrderDTO);
      }
      return depositOrderDTOs;
    } catch (Exception e) {
      logger.error("queryDepositOrdersByStatConditionError,shopId is {},stack is{}", new Object[]{depositStatConditionDTO.getShopId(), e});
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
  public int countDepositOrdersByStatCondition(DepositStatConditionDTO depositStatConditionDTO) {
    int result = 0;
    if (depositStatConditionDTO.getShopId() == null) {
      return 0;
    }
    try {
      TxnWriter tw = this.txnDaoManager.getWriter();
      if ((depositStatConditionDTO.getCustomerId() != null || depositStatConditionDTO.getSupplierId() != null) || (CollectionUtils.isEmpty(depositStatConditionDTO.getCustomerIds())) && CollectionUtils.isEmpty(depositStatConditionDTO.getSupplierIds())) {
        result = tw.countDepositOrderByConditions(depositStatConditionDTO.getShopId(), depositStatConditionDTO.getCustomerId(), depositStatConditionDTO.getSupplierId(), buildInOutList(depositStatConditionDTO.getInOut()), depositStatConditionDTO.getStartTime(), depositStatConditionDTO.getEndTime(), depositStatConditionDTO.getType());
      } else {
        if (!CollectionUtils.isEmpty(depositStatConditionDTO.getCustomerIds()) || !CollectionUtils.isEmpty(depositStatConditionDTO.getSupplierIds())) {
          if (StringUtils.equals(depositStatConditionDTO.getType(), "customer")) {
            result = tw.countDepositOrdersByShopIdAndIdsAndType(depositStatConditionDTO.getShopId(), depositStatConditionDTO.getCustomerIds(), depositStatConditionDTO.getType());
          } else {
            result = tw.countDepositOrdersByShopIdAndIdsAndType(depositStatConditionDTO.getShopId(), depositStatConditionDTO.getSupplierIds(), depositStatConditionDTO.getType());
          }
        }
      }
      return result;
    } catch (Exception e) {
      logger.error("queryDepositOrdersByShopIdCustomerIdError", e);
      return 0;
    }
  }

}
