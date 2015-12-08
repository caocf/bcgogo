package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.*;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午9:27
 * To change this template use File | Settings | File Templates.
 */
@Component
public class InsuranceService extends AbstractService implements IInsuranceService {
  private static final Logger LOG = LoggerFactory.getLogger(InsuranceService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public InsuranceOrderDTO createInsuranceOrderDTOByRepairOrderId(Long shopId, Long shopVersionId, Long repairOrderId) throws Exception {
    InsuranceOrderDTO insuranceOrderDTO = new InsuranceOrderDTO();
    if (shopId == null || repairOrderId == null) {
      return insuranceOrderDTO;
    }
    RepairOrderDTO repairOrderDTO = getRfiTxnService().getRepairOrderDTODetailById(repairOrderId, shopId);
    repairOrderDTO.setShopVersionId(shopVersionId);
    getRepairService().getProductInfo(repairOrderDTO);
    insuranceOrderDTO.fromRepairOrderDTO(repairOrderDTO);
    if (repairOrderDTO.getVechicleId() != null) {
      VehicleDTO vehicleDTO = getUserService().getVehicleById(repairOrderDTO.getVechicleId());
      insuranceOrderDTO.setVehicleDTO(vehicleDTO);
    }
    if (repairOrderDTO.getCustomerId() != null) {
      CustomerDTO customerDTO = getUserService().getCustomerById(repairOrderDTO.getCustomerId());
      insuranceOrderDTO.setCustomerDTO(customerDTO);
    }
    return insuranceOrderDTO;
  }

  @Override
  public Long getInsuranceOrderIdByRepairOrderId(Long shopId, Long repairOrderId) throws Exception {
    return txnDaoManager.getWriter().getInsuranceOrderIdByRepairOrderId(shopId, repairOrderId);
  }

  @Override
  public InsuranceOrderDTO getInsuranceOrderByRepairOrderId(Long shopId, Long repairOrderId) throws Exception {
    InsuranceOrder insuranceOrder = CollectionUtil.getFirst(txnDaoManager.getWriter().getInsuranceOrderByRepairOrderId(shopId, repairOrderId)) ;
    if(insuranceOrder!=null) return insuranceOrder.toDTO();
    return null;
  }

  @Override
  public InsuranceOrderDTO getInsuranceOrderByRepairDraftOrderId(Long shopId, Long repairDraftOrderId) throws Exception {
    InsuranceOrder insuranceOrder = CollectionUtil.getFirst(txnDaoManager.getWriter().getInsuranceOrderByRepairDraftOrderId(shopId, repairDraftOrderId)) ;
    if(insuranceOrder!=null) return insuranceOrder.toDTO();
    return null;
  }

  @Override
  public void updateInsuranceOrderById(Long repairOrderId, Long id, String receiptNo) {
    if (repairOrderId == null || id == null) return;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      InsuranceOrder insuranceOrder = writer.getById(InsuranceOrder.class, id);
      if (insuranceOrder != null && insuranceOrder.getRepairOrderId() == null) {
        insuranceOrder.setRepairOrderId(repairOrderId);
        insuranceOrder.setRepairOrderReceiptNo(receiptNo);
        writer.update(insuranceOrder);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void RFupdateInsuranceOrderById(Long repairOrderId, Long repairDraftOrderId, Long id, String receiptNo) {
    if ((repairOrderId == null && repairDraftOrderId == null) || id == null) return;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      InsuranceOrder insuranceOrder = writer.getById(InsuranceOrder.class, id);
      if (insuranceOrder != null && (insuranceOrder.getRepairOrderId() == null || insuranceOrder.getRepairDraftOrderId() == null)) {
        insuranceOrder.setRepairOrderId(repairOrderId);
        insuranceOrder.setRepairDraftOrderId(repairDraftOrderId);
        insuranceOrder.setRepairOrderReceiptNo(receiptNo);
        writer.update(insuranceOrder);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result validateSaveInsurance(InsuranceOrderDTO insuranceOrderDTO, String validateScene) throws Exception {
    String[] scenes = new String[0];
    if (StringUtils.isNotBlank(validateScene)) {
      scenes = validateScene.split(",");
    }
    if (ArrayUtil.isEmpty(scenes)) {
      return new Result();
    }
    Result result = null;
    for (String scene : scenes) {
      if (InsuranceValidateScene.CHECK_POLICY_NO.toString().equals(scene.trim().toUpperCase())) {
        result = validateInsurancePolicyNo(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getId(), insuranceOrderDTO.getPolicyNo());
        if (result != null && !result.isSuccess()) {
          return result;
        }
      } else if (InsuranceValidateScene.CHECK_REPORT_NO.toString().equals(scene.trim().toUpperCase())) {
        result = validateInsuranceReportNo(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getId(), insuranceOrderDTO.getReportNo());
        if (result != null && !result.isSuccess()) {
          return result;
        }
      } else if (InsuranceValidateScene.CHECK_REPAIR_ORDER_ID.toString().equals(scene.trim().toUpperCase())) {
        result = validateInsuranceRepairOrderId(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getId(), insuranceOrderDTO.getRepairOrderId());
        if (result != null && !result.isSuccess()) {
          return result;
        }
      } else if(InsuranceValidateScene.CHECK_REPAIR_DRAFT_ORDER_ID.toString().equals(scene.trim().toUpperCase())) {
        result = validateInsuranceRepairDraftOrderId(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getId(), insuranceOrderDTO.getRepairDraftOrderId());
        if (result != null && !result.isSuccess()) {
          return result;
        }
      }
    }
    return result;
  }

  @Override
  public Result validateInsurancePolicyNo(Long shopId, Long insuranceOrderId, String policyNo) throws Exception {
    if (shopId == null || StringUtils.isBlank(policyNo)) {
      return new Result("保险单号不能为空！", false);
    }
    List<InsuranceOrder> insuranceOrders = txnDaoManager.getWriter().getInsuranceOrderByPolicyNo(shopId, policyNo);
    if (insuranceOrders.size() == 0) {
      return new Result();
    } else if (insuranceOrderId == null) {
      return new Result("保险单号【" + policyNo + "】已存在，请重新输入！", false);
    } else {
      for (InsuranceOrder insuranceOrder : insuranceOrders) {
        if (insuranceOrderId.equals(insuranceOrder.getId())) {
          return new Result();
        }
      }
      return new Result("保险单号【" + policyNo + "】已存在，请重新输入！", false);
    }
  }

  @Override
  public Result validateInsuranceReportNo(Long shopId, Long insuranceOrderId, String reportNo) throws Exception {
    if (StringUtils.isBlank(reportNo)) {
      return new Result();
    }
    List<InsuranceOrder> insuranceOrders = txnDaoManager.getWriter().getInsuranceOrderByReportNo(shopId, reportNo);
    if (insuranceOrders.size() == 0) {
      return new Result();
    } else if (insuranceOrderId == null) {
      return new Result("报案编号【" + reportNo + "】已存在，请重新输入！", false);
    } else {
      for (InsuranceOrder insuranceOrder : insuranceOrders) {
        if (insuranceOrderId.equals(insuranceOrder.getId())) {
          return new Result();
        }
      }
      return new Result("报案编号【" + reportNo + "】已存在，请重新输入！", false);
    }
  }

  @Override
  public Result validateInsuranceRepairOrderId(Long shopId, Long insuranceOrderId, Long repairOrderId) throws Exception {
    Result result = new Result();
    if (insuranceOrderId != null && repairOrderId == null) {
      InsuranceOrder insuranceOrder = txnDaoManager.getWriter().getInsuranceOrderById(shopId, insuranceOrderId);
      if (insuranceOrder != null && insuranceOrder.getRepairOrderId() != null) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(insuranceOrder.getRepairOrderReceiptNo())) {
          sb.append("【" + insuranceOrder.getRepairOrderReceiptNo() + "】");
        }
        return new Result("当前保险单已存在关联的施工单" + sb.toString() + "，无法重复生成！", false);
      }
    } else if (insuranceOrderId == null && repairOrderId != null) {
      List<InsuranceOrder> insuranceOrders = txnDaoManager.getWriter().getInsuranceOrderByRepairOrderId(shopId, repairOrderId);
      if (CollectionUtils.isNotEmpty(insuranceOrders)) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(insuranceOrders.get(0).getPolicyNo())) {
          sb.append("【" + insuranceOrders.get(0).getPolicyNo() + "】");
        }
        return new Result("当前施工单已存在关联的保险单" + sb.toString() + "，无法重复生成！", false);
      }
    }
    return result;
  }

  @Override
  public Result validateInsuranceRepairDraftOrderId(Long shopId, Long insuranceOrderId, Long repairDraftOrderId) throws Exception {
    Result result = new Result();
    if (insuranceOrderId != null && repairDraftOrderId == null) {
      InsuranceOrder insuranceOrder = txnDaoManager.getWriter().getInsuranceOrderById(shopId, insuranceOrderId);
      if (insuranceOrder != null && insuranceOrder.getRepairDraftOrderId() != null) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(insuranceOrder.getRepairOrderReceiptNo())) {
          sb.append("【" + insuranceOrder.getRepairOrderReceiptNo() + "】");
        }
        return new Result("当前保险单已存在关联的施工草稿单" + sb.toString() + "，无法重复生成！", false);
      }
    } else if (insuranceOrderId == null && repairDraftOrderId != null) {
      List<InsuranceOrder> insuranceOrders = txnDaoManager.getWriter().getInsuranceOrderByRepairDraftOrderId(shopId, repairDraftOrderId);
      if (CollectionUtils.isNotEmpty(insuranceOrders)) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(insuranceOrders.get(0).getPolicyNo())) {
          sb.append("【" + insuranceOrders.get(0).getPolicyNo() + "】");
        }
        return new Result("当前施工草稿单已存在关联的保险单" + sb.toString() + "，无法重复生成！", false);
      }
    }
    return result;
  }

  @Override
  public void saveOrUpdateInsuranceOrder(InsuranceOrderDTO insuranceOrderDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (insuranceOrderDTO.getRepairOrderId() != null) {
        RepairOrder repairOrder = writer.getRepairOrderById(insuranceOrderDTO.getRepairOrderId(), insuranceOrderDTO.getShopId());
        if (repairOrder != null) {
          insuranceOrderDTO.setRepairOrderReceiptNo(repairOrder.getReceiptNo());
        } else {
          insuranceOrderDTO.setRepairOrderReceiptNo(null);
          insuranceOrderDTO.setRepairOrderId(null);
        }
      }
      InsuranceOrder insuranceOrder = null;
      if (insuranceOrderDTO.getId() != null) {
        insuranceOrder = writer.getInsuranceOrderById(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getId());
      }
      if (insuranceOrder != null) {
        insuranceOrder.fromDTO(insuranceOrderDTO);
        writer.update(insuranceOrder);
      } else {
            insuranceOrder = new InsuranceOrder();
            insuranceOrder.fromDTO(insuranceOrderDTO);
            writer.save(insuranceOrder);
            insuranceOrderDTO.setId(insuranceOrder.getId());
      }

      List<InsuranceOrderService> insuranceOrderServices = writer.getInsuranceOrderServiceByOrderId(
          insuranceOrderDTO.getShopId(), insuranceOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(insuranceOrderServices)) {
        for (InsuranceOrderService insuranceOrderService : insuranceOrderServices) {
          writer.delete(insuranceOrderService);
        }
      }

      if (CollectionUtils.isNotEmpty(insuranceOrderDTO.getServiceDTOs())) {
        List<InsuranceOrderServiceDTO> newInsuranceOrderServiceDTOs = new ArrayList<InsuranceOrderServiceDTO>();
        for (InsuranceOrderServiceDTO insuranceOrderServiceDTO : insuranceOrderDTO.getServiceDTOs()) {
          if (StringUtils.isBlank(insuranceOrderServiceDTO.getService())) {
            continue;
          }
          InsuranceOrderService insuranceOrderService = new InsuranceOrderService();
          insuranceOrderServiceDTO.setId(null);
          insuranceOrderService.fromDTO(insuranceOrderServiceDTO, insuranceOrderDTO);
          writer.save(insuranceOrderService);
          insuranceOrderServiceDTO.setId(insuranceOrderService.getId());
          newInsuranceOrderServiceDTOs.add(insuranceOrderServiceDTO);
        }
        insuranceOrderDTO.setServiceDTOs(newInsuranceOrderServiceDTOs);
      }

      List<InsuranceOrderItem> insuranceOrderItems = writer.getInsuranceOrderItemByOrderId(
          insuranceOrderDTO.getShopId(), insuranceOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(insuranceOrderItems)) {
        for (InsuranceOrderItem insuranceOrderItem : insuranceOrderItems) {
          writer.delete(insuranceOrderItem);
        }
      }

      if (!ArrayUtil.isEmpty(insuranceOrderDTO.getItemDTOs())) {
        List<InsuranceOrderItemDTO> newInsuranceOrderItemDTOs = new ArrayList<InsuranceOrderItemDTO>();
        for (InsuranceOrderItemDTO insuranceOrderItemDTO : insuranceOrderDTO.getItemDTOs()) {
          if (StringUtils.isBlank(insuranceOrderItemDTO.getProductName())) {
            continue;
          }
          InsuranceOrderItem insuranceOrderItem = new InsuranceOrderItem();
          insuranceOrderItemDTO.setId(null);
          insuranceOrderItemDTO.setInsuranceOrderId(insuranceOrderDTO.getId());
          insuranceOrderItem.fromDTO(insuranceOrderItemDTO, insuranceOrderDTO);
          writer.save(insuranceOrderItem);
          insuranceOrderItemDTO.setId(insuranceOrderItem.getId());
          newInsuranceOrderItemDTOs.add(insuranceOrderItemDTO);
        }
        insuranceOrderDTO.setItemDTOs(newInsuranceOrderItemDTOs.toArray(new InsuranceOrderItemDTO[newInsuranceOrderItemDTOs.size()]));
      }
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
    //保存操作记录
    if(insuranceOrderDTO.getRepairOrderId() != null) {
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
          new OperationLogDTO(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getEditorId(), insuranceOrderDTO.getRepairOrderId(), ObjectTypes.REPAIR_ORDER, OperationTypes.INSURANCE_ORDER));
    }
  }

  @Override
  public void saveOrUpdateCustomerVehicle(InsuranceOrderDTO insuranceOrderDTO) throws Exception {
    Long[] vehicleIds = getBaseProductService().saveVehicle(null, null, null, null,
        insuranceOrderDTO.getBrand(), insuranceOrderDTO.getModel(), null, null);
    insuranceOrderDTO.setBrandId(vehicleIds[0]);
    insuranceOrderDTO.setModelId(vehicleIds[1]);

    getCustomerService().saveOrUpdateCustomerVehicle(insuranceOrderDTO);
  }

  @Override
  public InsuranceOrderDTO getInsuranceOrderDTOById(Long insuranceOrderId, Long shopId) {
    if (shopId == null || insuranceOrderId == null) {
      return null;
    }
    InsuranceOrderDTO insuranceOrderDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    InsuranceOrder insuranceOrder = writer.getInsuranceOrderById(shopId, insuranceOrderId);
    if (insuranceOrder != null) {
      insuranceOrderDTO = insuranceOrder.toDTO();
    } else {
      return null;
    }
    List<InsuranceOrderService> insuranceOrderServices = writer.getInsuranceOrderServiceByOrderId(shopId, insuranceOrderId);
    if (CollectionUtils.isNotEmpty(insuranceOrderServices)) {
      List<InsuranceOrderServiceDTO> insuranceOrderServiceDTOs = new ArrayList<InsuranceOrderServiceDTO>();
      for (InsuranceOrderService insuranceOrderService : insuranceOrderServices) {
        insuranceOrderServiceDTOs.add(insuranceOrderService.toDTO());
      }
      insuranceOrderDTO.setServiceDTOs(insuranceOrderServiceDTOs);
    }

    List<InsuranceOrderItem> insuranceOrderItems = writer.getInsuranceOrderItemByOrderId(shopId, insuranceOrderId);
    if (CollectionUtils.isNotEmpty(insuranceOrderItems)) {
      List<InsuranceOrderItemDTO> insuranceOrderItemDTOs = new ArrayList<InsuranceOrderItemDTO>();
      for (InsuranceOrderItem insuranceOrderItem : insuranceOrderItems) {
        insuranceOrderItemDTOs.add(insuranceOrderItem.toDTO());
      }
      insuranceOrderDTO.setItemDTOs(insuranceOrderItemDTOs.toArray(new InsuranceOrderItemDTO[insuranceOrderItemDTOs.size()]));
    }

    return insuranceOrderDTO;
  }

  @Override
  public Integer sumInsuranceOrderDTOs(Long shopId) {
    return txnDaoManager.getWriter().sumInsuranceOrderDTOs(shopId);
  }

  @Override
  public int countInsuranceOrderDTOs(InsuranceOrderDTO searchCondition) {
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return 0;
    }
    searchCondition.initSearchTime();
    return txnDaoManager.getWriter().countInsuranceOrderDTOs(searchCondition);
  }

  @Override
  public Double sumInsuranceOrderClaims(InsuranceOrderDTO searchCondition) {
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return 0d;
    }
    searchCondition.initSearchTime();
    return txnDaoManager.getWriter().sumInsuranceOrderClaims(searchCondition);
  }

  @Override
  public List<InsuranceOrderDTO> getInsuranceOrderDTOs(InsuranceOrderDTO searchCondition) {
    List<InsuranceOrderDTO> insuranceOrderDTOs = new ArrayList<InsuranceOrderDTO>();
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return insuranceOrderDTOs;
    }
    searchCondition.initSearchTime();
    TxnWriter writer = txnDaoManager.getWriter();
    List<InsuranceOrder> insuranceOrders = writer.getInsuranceOrderDTOs(searchCondition);

    if (CollectionUtils.isNotEmpty(insuranceOrders)) {
      for (InsuranceOrder insuranceOrder : insuranceOrders) {
        InsuranceOrderDTO insuranceOrderDTO = insuranceOrder.toDTO();
        insuranceOrderDTOs.add(insuranceOrderDTO);
      }
    }
    return insuranceOrderDTOs;
  }

  @Override
  public RepairOrderDTO createRepairOrderDTO(InsuranceOrderDTO insuranceOrderDTO) throws Exception {

    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    repairOrderDTO.setInsuranceOrderId(insuranceOrderDTO.getId());
    ShopDTO shopDTO = getConfigService().getShopById(insuranceOrderDTO.getShopId());
    repairOrderDTO.setShopDTO(shopDTO);
    long curTime = System.currentTimeMillis();
    repairOrderDTO.setStartDate(curTime);
    repairOrderDTO.setEndDate(curTime);
    repairOrderDTO.setEditDate(curTime);
    repairOrderDTO.setServiceType(OrderTypes.REPAIR);//主要内容默认保养/维修/美容
    if (insuranceOrderDTO.getCustomerId() != null) {
      CustomerDTO customerDTO = getUserService().getCustomerById(insuranceOrderDTO.getCustomerId());
      if (customerDTO != null) {
        repairOrderDTO.setCustomerDTO(customerDTO);
      }
      MemberDTO memberDTO = getMembersService().getMemberByCustomerId(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getCustomerId());
      if (memberDTO != null) {
        repairOrderDTO.setMemberDTO(memberDTO);
        repairOrderDTO.setMemberStatus(getMembersService().getMemberStatusByMemberDTO(memberDTO).getStatus());
      }
    }

    if (insuranceOrderDTO.getVehicleId() != null) {
      VehicleDTO vehicleDTO = getUserService().getVehicleById(insuranceOrderDTO.getVehicleId());
      repairOrderDTO.setVehicleDTO(vehicleDTO);
    }

    if (insuranceOrderDTO.getCustomerId() != null && insuranceOrderDTO.getVehicleId() != null) {
      CustomerVehicleDTO customerVehicleDTO = getUserService().getCustomerVehicleDTOByVehicleIdAndCustomerId(
          insuranceOrderDTO.getVehicleId(), insuranceOrderDTO.getCustomerId());
      repairOrderDTO.setCustomerVehicleDTO(customerVehicleDTO);
      List<AppointServiceDTO> appointServiceDTOs = getUserService().getAppointServiceByCustomerVehicle(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getVehicleId(), insuranceOrderDTO.getVehicleId());
      if (CollectionUtils.isNotEmpty(appointServiceDTOs)) {
        repairOrderDTO.setAppointServiceDTOs(appointServiceDTOs.toArray(new AppointServiceDTO[appointServiceDTOs.size()]));
      }
    }

    double total = 0d;
    if (CollectionUtils.isNotEmpty(insuranceOrderDTO.getServiceDTOs())) {
      List<RepairOrderServiceDTO> repairOrderServiceDTOs = new ArrayList<RepairOrderServiceDTO>();
      for (InsuranceOrderServiceDTO insuranceOrderServiceDTO : insuranceOrderDTO.getServiceDTOs()) {
        if (StringUtils.isNotBlank(insuranceOrderServiceDTO.getService())) {
          RepairOrderServiceDTO repairOrderServiceDTO = new RepairOrderServiceDTO();
          repairOrderServiceDTO.fromInsuranceOrderServiceDTO(insuranceOrderServiceDTO);
          Service service = getRfiTxnService().getRFServiceByServiceNameAndShopId(insuranceOrderDTO.getShopId(), insuranceOrderServiceDTO.getService());
          if (service != null) {
            repairOrderServiceDTO.setServiceId(service.getId());
            CategoryDTO categoryDTO = getRfiTxnService().getCateGoryByServiceId(insuranceOrderDTO.getShopId(), repairOrderServiceDTO.getServiceId());
            if (categoryDTO != null) {
              repairOrderServiceDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
              repairOrderServiceDTO.setBusinessCategoryId(categoryDTO.getId());
            }

            if (service.getStandardHours() != null) {
              repairOrderServiceDTO.setStandardHours(service.getStandardHours());
              repairOrderServiceDTO.setActualHours(service.getStandardHours());
            }
            if (service.getStandardUnitPrice() != null) {
              repairOrderServiceDTO.setStandardUnitPrice(service.getStandardUnitPrice());
            }
          }
          total += NumberUtil.doubleVal(repairOrderServiceDTO.getTotal());
          repairOrderServiceDTOs.add(repairOrderServiceDTO);
        }
        if (CollectionUtils.isNotEmpty(repairOrderServiceDTOs)) {
          repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs.toArray(new RepairOrderServiceDTO[repairOrderServiceDTOs.size()]));
        }
      }
    }

    if (!ArrayUtils.isEmpty(insuranceOrderDTO.getItemDTOs())) {
      List<RepairOrderItemDTO> repairOrderItemDTOs = new ArrayList<RepairOrderItemDTO>();
      for (InsuranceOrderItemDTO insuranceOrderItemDTO : insuranceOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(insuranceOrderItemDTO.getProductName())) {
          continue;
        }
        RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
        ProductDTO searchProductDTO = new ProductDTO();
        searchProductDTO.fromInsuranceOrderItemDTO(insuranceOrderItemDTO);
        List<ProductDTO> productDTOs = getProductService().getProductDTOsBy7P(insuranceOrderDTO.getShopId(), searchProductDTO);
        if (CollectionUtils.isNotEmpty(productDTOs)) {
          ProductDTO productDTO = getProductService().getProductById(productDTOs.get(0).getId(), insuranceOrderDTO.getShopId());
          repairOrderItemDTO.setProductDTOWithOutUnit(productDTO);
          InventoryDTO inventoryDTO = getInventoryService().getInventoryDTOByProductId(repairOrderItemDTO.getProductId());
          repairOrderItemDTO.setInventoryDTO(inventoryDTO);
          Category category = getRfiTxnService().getCategoryById(insuranceOrderDTO.getShopId(), productDTO.getBusinessCategoryId());
          if (category != null) {
            repairOrderItemDTO.setBusinessCategoryName(category.getCategoryName());
            repairOrderItemDTO.setBusinessCategoryId(category.getId());
          }
        } else {
          repairOrderItemDTO.setProductDTOWithOutUnit(searchProductDTO);
        }
        repairOrderItemDTO.fromInsuranceItemDTO(insuranceOrderItemDTO);

        total += NumberUtil.doubleVal(repairOrderItemDTO.getTotal());
        repairOrderItemDTOs.add(repairOrderItemDTO);
      }
      if (CollectionUtils.isNotEmpty(repairOrderItemDTOs)) {
        repairOrderDTO.setItemDTOs(repairOrderItemDTOs.toArray(new RepairOrderItemDTO[repairOrderItemDTOs.size()]));
      }
    }
    repairOrderDTO.setTotal(NumberUtil.round(total, 2));
    repairOrderDTO.setInsuranceOrderDTO(insuranceOrderDTO);
    return repairOrderDTO;
  }

  @Override
  public void updateInsuranceByRepairOrderDTO(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO == null || repairOrderDTO.getInsuranceOrderId() == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      InsuranceOrder insuranceOrder = writer.getInsuranceOrderById(repairOrderDTO.getShopId(), repairOrderDTO.getInsuranceOrderId());
      if (insuranceOrder != null && insuranceOrder.getRepairOrderId() == null) {
        insuranceOrder.setRepairOrderId(repairOrderDTO.getId());
        insuranceOrder.setRepairOrderReceiptNo(repairOrderDTO.getReceiptNo());
        writer.update(insuranceOrder);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }
}
