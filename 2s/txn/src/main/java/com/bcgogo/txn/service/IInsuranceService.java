package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.txn.dto.InsuranceOrderDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午9:26
 */

public interface IInsuranceService {

  InsuranceOrderDTO createInsuranceOrderDTOByRepairOrderId(Long shopId,Long shopVersionId, Long repairOrderId)throws Exception;

  Long getInsuranceOrderIdByRepairOrderId(Long shopId, Long repairOrderId)throws Exception;

  InsuranceOrderDTO getInsuranceOrderByRepairOrderId(Long shopId, Long repairOrderId)throws Exception;

  InsuranceOrderDTO getInsuranceOrderByRepairDraftOrderId(Long shopId, Long repairDraftOrderId)throws Exception;

  void updateInsuranceOrderById(Long repairOrderId, Long id, String receiptNo);

  void RFupdateInsuranceOrderById(Long repairOrderId, Long repairDraftOrderId, Long id, String receiptNo);

  Result validateSaveInsurance(InsuranceOrderDTO insuranceOrderDTO, String validateScene)throws Exception;

  Result validateInsurancePolicyNo(Long shopId, Long insuranceOrderId, String policyNo) throws Exception;

  Result validateInsuranceReportNo(Long shopId, Long insuranceOrderId, String reportNo) throws Exception;

  void saveOrUpdateInsuranceOrder(InsuranceOrderDTO insuranceOrderDTO)throws Exception;

  void saveOrUpdateCustomerVehicle(InsuranceOrderDTO insuranceOrderDTO)throws Exception;

  InsuranceOrderDTO getInsuranceOrderDTOById(Long insuranceOrderId, Long shopId);

  List<InsuranceOrderDTO> getInsuranceOrderDTOs(InsuranceOrderDTO searchCondition);

  int countInsuranceOrderDTOs(InsuranceOrderDTO searchCondition);

  Double sumInsuranceOrderClaims(InsuranceOrderDTO searchCondition);

  Integer sumInsuranceOrderDTOs(Long shopId);

  Result validateInsuranceRepairOrderId(Long shopId, Long insuranceOrderId, Long repairOrderId) throws Exception;

  Result validateInsuranceRepairDraftOrderId(Long shopId, Long insuranceOrderId, Long repairOrderId) throws Exception;

  RepairOrderDTO createRepairOrderDTO(InsuranceOrderDTO insuranceOrderDTO) throws Exception;

  void updateInsuranceByRepairOrderDTO(RepairOrderDTO repairOrderDTO) throws Exception;
}
