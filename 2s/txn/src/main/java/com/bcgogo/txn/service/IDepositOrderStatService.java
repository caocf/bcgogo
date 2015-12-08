package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.stat.dto.DepositStatConditionDTO;
import com.bcgogo.txn.dto.DepositOrderDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-21
 * Time: 下午9:20
 * 查询预收款、预付款的统计服务（对于客户、供应商单独的查询在 ICustomerDepositService,ISupplierPayableService）
 */
public interface IDepositOrderStatService {

  /**
   * 查询预付款、预收款订单
   *
   * @param depositStatConditionDTO
   * @return
   */
  public List<DepositOrderDTO> queryDepositOrdersByStatCondition(DepositStatConditionDTO depositStatConditionDTO,Pager pager);

  /**
   * 统购
   * @param depositStatConditionDTO
   * @return
   */
  public int countDepositOrdersByStatCondition(DepositStatConditionDTO depositStatConditionDTO);


}
