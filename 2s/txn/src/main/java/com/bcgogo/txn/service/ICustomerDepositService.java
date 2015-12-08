package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.DepositType;
import com.bcgogo.enums.InOutFlag;
import com.bcgogo.enums.SortObj;
import com.bcgogo.txn.dto.CustomerDepositDTO;
import com.bcgogo.txn.dto.DepositOrderDTO;
import com.bcgogo.txn.model.TxnWriter;
import org.hibernate.annotations.Sort;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-10
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public interface ICustomerDepositService {

  public void saveCustomerDeposit(CustomerDepositDTO customerDepositDTO);

  public void updateCustomerDeposit(CustomerDepositDTO customerDepositDTO);

  public CustomerDepositDTO queryCustomerDepositByShopIdAndCustomerId(Long shopId, Long customerId);

  /**
   * 充值
   *
   * @param customerDepositDTO
   * @return
   */
  public Result customerDeposit(CustomerDepositDTO customerDepositDTO);

  /**
   * 取用(这里不是支付的概念，比如作废 退预收款)
   *
   * @param customerDepositDTO
   * @param depositOrderDTO
   * @return
   */
  public boolean customerDepositUse(CustomerDepositDTO customerDepositDTO, DepositOrderDTO depositOrderDTO,TxnWriter writer);

  /**
   * 通过shopId,customerId 获取预付金 收支记录
   *
   * @param shopId
   * @param customerId
   * @return
   */
  public List<DepositOrderDTO> queryDepositOrdersByShopIdCustomerId(Long shopId, Long customerId, Long intOut, SortObj sortObj,Pager pager);

  /**
   * 通过shopId,customerId 获取预付金订单数量
   *
   * @param shopId
   * @param customerId
   * @param inOut
   * @return
   */
  public int countDepositOrderByShopIdAndCustomerId(Long shopId, Long customerId, Long inOut);

  /**
   * 通过shopId customerId 关联订单号 查询预收款使用记录
   * @param shopId
   * @param customerId
   * @param relatedOrderId
   * @return
   */
  public DepositOrderDTO queryDepositOrderByShopIdCustomerIdAndRelatedOrderId(Long shopId, Long customerId, Long relatedOrderId);

  public DepositOrderDTO getById(Long id);

}
