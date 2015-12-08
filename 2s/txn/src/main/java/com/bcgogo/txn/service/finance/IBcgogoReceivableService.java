package com.bcgogo.txn.service.finance;

import com.bcgogo.common.Result;
import com.bcgogo.enums.txn.finance.BuyChannels;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.txn.dto.finance.*;

import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 下午5:29
 */
public interface IBcgogoReceivableService {
  /**
   * 查询
   *
   * @param condition BcgogoReceivableSearchCondition
   * @throws BcgogoException
   */
  Result searchBcgogoReceivableResult(BcgogoReceivableSearchCondition condition, boolean isPage) throws BcgogoException;

  /**
   * 获得分期详细信息
   *
   * @param instalmentPlanIds Long...
   */
  Map<Long, InstalmentPlanDTO> getInstalmentPlanDetailMap(Long... instalmentPlanIds);

  /**
   * 软件新增费用
   *
   * @param shopId long
   * @throws BcgogoException
   */
  void createSoftwareReceivable(Long shopId,Long userId,String userName,BuyChannels buyChannels) throws Exception;

  /**
   * 硬件支付
   *
   * @param dto BcgogoReceivableDTO
   */
  void hardwareReceivable(BcgogoReceivableDTO dto) throws BcgogoException;


  /**
   * 软件支付
   *
   * @param dto BcgogoReceivableDTO
   */
  void softwareReceivable(BcgogoReceivableDTO dto) throws BcgogoException;

  /**
   * 分期付款
   *
   * @param dto BcgogoReceivableDTO
   * @throws BcgogoException
   */
  void instalmentReceivable(BcgogoReceivableDTO dto) throws BcgogoException;

  /**
   * 审核
   *
   * @param auditUserId Long
   * @param auditUserName String
   * @param bcgogoReceivableOrderRecordRelationId
   *                    Long
   */
  void auditReceivable(Long auditUserId,String auditUserName, Long bcgogoReceivableOrderRecordRelationId) throws Exception;

  Result getInstalmentPlanAlgorithms();

  Result getInstalmentPlanDetails(Long instalmentPlanId);


  InstalmentPlanAlgorithmDTO getInstalmentPlanAlgorithmsById(Long instalmentPlanAlgorithmId);

  /**
   * 增加软件已支付记录
   *
   * @param receivableDTO UnconstrainedSoftwareReceivableDTO
   */
  Result addSoftwareReceived(UnconstrainedSoftwareReceivableDTO receivableDTO) throws BcgogoException;

  /**
   * 增加软件待支付记录
   *
   * @param receivableDTO UnconstrainedSoftwareReceivableDTO
   */
  Result createSoftwareReceivable(UnconstrainedSoftwareReceivableDTO receivableDTO) throws BcgogoException;

  Result validateBargainContext(long shopId);

  /**
   * 议价后修改 为支付过的待支付记录
   *
   * @param shopId long
   * @param price  double
   */
  Result updateSoftwareReceivable(long shopId, double price) throws BcgogoException;

  Result searchBcgogoReceivableOrderResult(BcgogoReceivableSearchCondition condition) throws BcgogoException;

  List<BcgogoReceivableOrderDTO> searchBcgogoReceivableOrderDTO(Long shopVersionId,BcgogoReceivableSearchCondition condition) throws BcgogoException;
  int countBcgogoReceivableOrderDTO(BcgogoReceivableSearchCondition condition) throws BcgogoException;

  Result statBcgogoReceivableOrderByStatusResult(BcgogoReceivableSearchCondition condition) throws BcgogoException;
  Result statBcgogoReceivableOrderRecordByStatusResult(BcgogoReceivableSearchCondition condition) throws BcgogoException;

  void initBcgogoReceivableOrder();

  BcgogoReceivableOrderDTO shipBcgogoReceivableOrder(String userName,Long userId,Long bcgogoReceivableOrderId) throws Exception;

  BcgogoReceivableOrderDTO cancelBcgogoReceivableOrder(Long shopId,Long bcgogoReceivableOrderId, Long cancelUserId,String cancelUserName,String cancelReason) throws Exception;

  BcgogoReceivableOrderItemDTO getBcgogoReceivableOrderItemDTO(Long orderItemId);
  BcgogoReceivableOrderDTO getSimpleBcgogoReceivableOrderDTO(Long orderId);

  void updateBcgogoReceivableOrderItemDTO(BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO);

  Map<String,Integer> statBcgogoReceivableOrderByStatus(BcgogoReceivableSearchCondition condition) throws BcgogoException;

  void saveBcgogoReceivableOrderDTO(Long bcgogoUserId, BcgogoReceivableOrderDTO dto) throws Exception;

  /**
   * 给客户调用  使用的时候 shopId 不能为空  请注意
   * @param shopId
   * @param orderId
   * @return
   */
  BcgogoReceivableOrderDTO getBcgogoReceivableOrderDetail(Long shopId,Long orderId) throws Exception;

  BcgogoReceivableOrderRecordRelationDTO getBcgogoReceivableOrderRecordRelationDTOById(Long shopId,Long id);

  Boolean verifyAndGetBcgogoReceivableOrderDTO(BcgogoReceivableDTO dto,Long shopId,BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO);

  List<BcgogoReceivableDTO> getBcgogoReceivableDTOByRelationId(Long... relationId);

}
