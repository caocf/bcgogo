package com.bcgogo.txn.service.statementAccount;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.txn.dto.StatementAccountOrderDTO;

import java.util.List;

/**
 * 对账单专用service
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public interface IStatementAccountService {

  /**
   * 根据查询条件查询对账单记录条数
   *
   * @param orderSearchConditionDTO
   * @return
   */
  public int countStatementAccountOrderList(OrderSearchConditionDTO orderSearchConditionDTO);

  /**
   * 根据查询条件查询对账单记录
   * @param orderSearchConditionDTO
   * @param pager
   * @return
   */
  public List getStatementAccountOrderList(OrderSearchConditionDTO orderSearchConditionDTO, Pager pager);

  /**
   * 查询某个店铺下本期客户或者供应商对账id
   *
   * @param orderSearchConditionDTO
   * @return
   */
  public List getCurrentStatementAccountOrder(OrderSearchConditionDTO orderSearchConditionDTO);

  /**
   * 查询某个客户或者供应商最后一次对账单
   *
   * @param shopId
   * @param customerOrSupplierId
   * @param orderTypes
   * @return
   */
  public StatementAccountOrderDTO getLastStatementAccountOrder(Long shopId, Long customerOrSupplierId, OrderTypes orderTypes);

  /**
   * 对账单结算前进行校验
   * @param statementAccountOrderDTO
   * @return
   */
  public Result validateStatementAccountBeforeSettle(StatementAccountOrderDTO statementAccountOrderDTO);

  /**
   * 对账单结算
   * @param statementAccountOrderDTO
   * @return
   */
  public Result settleStatementAccountOrder(StatementAccountOrderDTO statementAccountOrderDTO) throws Exception;

  /**
   * 查询结算人
   * @param shopId
   * @param customerOrSupplierId
   * @param salesMan
   * @param orderType
   * @return
   */
  public List getOperatorByCustomerOrSupplierId(Long shopId,Long customerOrSupplierId,String salesMan);


  /**
   * 根据对账单id查询对账单详细信息
   * @param statementAccountOrderId
   * @return
   */
  public StatementAccountOrderDTO getStatementOrderInfo(Long statementAccountOrderId) throws Exception;

  /**
   * 根据id获取对账单
   * @param orderId
   * @return
   */
  public StatementAccountOrderDTO getStatementAccountOrderById(Long orderId);
}
