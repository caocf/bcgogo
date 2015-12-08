package com.bcgogo.txn.service;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.secondary.*;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.secondary.*;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.Vehicle;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepairOrderSecondaryService implements IRepairOrderSecondaryService {
  @Autowired
  private TxnDaoManager txnDaoManager;

  public RepairOrderSecondaryDTO findRepairOrderSecondaryById(Long shopId, Long repairOrderSecondaryId) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RepairOrderSecondaryDTO repairOrderSecondaryDTO = null;
    RepairOrderSecondary repairOrderSecondary = txnWriter.findRepairOrderSecondaryById(shopId, repairOrderSecondaryId);
    if (repairOrderSecondary != null) {
      repairOrderSecondaryDTO = repairOrderSecondary.toDTO();
      List<RepairOrderServiceSecondary> repairOrderServiceSecondaryList = txnWriter.findRepairOrderServiceSecondaryById(shopId, repairOrderSecondaryId);
      if (CollectionUtils.isNotEmpty(repairOrderServiceSecondaryList)) {
        RepairOrderServiceSecondaryDTO[] repairOrderServiceSecondaryDTOs = new RepairOrderServiceSecondaryDTO[repairOrderServiceSecondaryList.size()];
        int i = 0;
        for (RepairOrderServiceSecondary repairOrderServiceSecondary : repairOrderServiceSecondaryList) {
          repairOrderServiceSecondaryDTOs[i++] = repairOrderServiceSecondary.toDTO();
        }
        repairOrderSecondaryDTO.setServiceDTOs(repairOrderServiceSecondaryDTOs);
      }
      List<RepairOrderItemSecondary> repairOrderItemSecondaryList = txnWriter.findRepairOrderItemSecondaryById(shopId, repairOrderSecondaryId);
      if (CollectionUtils.isNotEmpty(repairOrderItemSecondaryList)) {
        IProductService productService = ServiceManager.getService(IProductService.class);
        RepairOrderItemSecondaryDTO[] repairOrderItemSecondaryDTOs = new RepairOrderItemSecondaryDTO[repairOrderItemSecondaryList.size()];
        int i = 0;
        for (RepairOrderItemSecondary repairOrderItemSecondary : repairOrderItemSecondaryList) {
          repairOrderItemSecondaryDTOs[i] = repairOrderItemSecondary.toDTO();
          ProductDTO productDTO = productService.getProductByProductLocalInfoId(repairOrderItemSecondary.getProductId(), shopId);
          if (productDTO != null) {
            repairOrderItemSecondaryDTOs[i].setStorageUnit(productDTO.getStorageUnit());
            repairOrderItemSecondaryDTOs[i].setSellUnit(productDTO.getSellUnit());
            repairOrderItemSecondaryDTOs[i].setRate(productDTO.getRate());
          }
          i++;
        }
        repairOrderSecondaryDTO.setItemDTOs(repairOrderItemSecondaryDTOs);
      }
      List<RepairOrderOtherIncomeItemSecondary> repairOrderOtherIncomeItemSecondaryList = txnWriter.findRepairOrderOtherIncomeItemSecondaryById(shopId, repairOrderSecondaryId);
      if (CollectionUtils.isNotEmpty(repairOrderOtherIncomeItemSecondaryList)) {
        RepairOrderOtherIncomeItemSecondaryDTO[] repairOrderOtherIncomeItemSecondaryDTOs = new RepairOrderOtherIncomeItemSecondaryDTO[repairOrderOtherIncomeItemSecondaryList.size()];
        int i = 0;
        for (RepairOrderOtherIncomeItemSecondary repairOrderOtherIncomeItemSecondary : repairOrderOtherIncomeItemSecondaryList) {
          repairOrderOtherIncomeItemSecondaryDTOs[i++] = repairOrderOtherIncomeItemSecondary.toDTO();
        }
        repairOrderSecondaryDTO.setOtherIncomeItemDTOs(repairOrderOtherIncomeItemSecondaryDTOs);
      }
      List<RepairOrderSettlementSecondary> repairOrderSettlementSecondaries = txnWriter.findRepairOrderSettlementSecondaryByRepairOrderId(shopId, repairOrderSecondaryId);
      if (CollectionUtils.isNotEmpty(repairOrderSettlementSecondaries)) {
        RepairOrderSettlementSecondaryDTO[] repairOrderSettlementSecondaryDTOs = new RepairOrderSettlementSecondaryDTO[repairOrderSettlementSecondaries.size()];
        int i = 0;
        for (RepairOrderSettlementSecondary repairOrderSettlementSecondary : repairOrderSettlementSecondaries) {
          repairOrderSettlementSecondaryDTOs[i++] = repairOrderSettlementSecondary.toDTO();
        }
        repairOrderSecondaryDTO.setRepairOrderSettlementSecondaryDTOs(repairOrderSettlementSecondaryDTOs);
      }
    }
    return repairOrderSecondaryDTO;
  }

  public RepairOrderSecondaryDTO saveRepairOrderSecondary(RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      if (repairOrderSecondaryDTO.getId() == null) {
        RepairOrderSecondaryDTO rosd = findRepairOrderSecondaryByRepairOrderId(repairOrderSecondaryDTO.getShopId(), repairOrderSecondaryDTO.getRepairOrderId());
        Assert.isNull(rosd, "施工单结算附表已结算，不能重复结算！");
        RepairOrderSecondary repairOrderSecondary = new RepairOrderSecondary();
        repairOrderSecondary.fromDTO(repairOrderSecondaryDTO);
        txnWriter.save(repairOrderSecondary);
        repairOrderSecondaryDTO.setId(repairOrderSecondary.getId());
      } else {
        RepairOrderSecondary repairOrderSecondary = txnWriter.findById(RepairOrderSecondary.class, repairOrderSecondaryDTO.getId());
        repairOrderSecondary.fromDTO(repairOrderSecondaryDTO);
        txnWriter.update(repairOrderSecondary);
      }
      saveRepairOrderSettlementSecondary(repairOrderSecondaryDTO);
      Long[] deleteServiceDTOs = repairOrderSecondaryDTO.getDeleteServiceDTOs();
      Long[] deleteItemDTOs = repairOrderSecondaryDTO.getDeleteItemDTOs();
      Long[] deleteOtherIncomeItemDTOs = repairOrderSecondaryDTO.getDeleteOtherIncomeItemDTOs();
      deleteRepairOrderServiceSecondary(deleteServiceDTOs);
      deleteRepairOrderItemSecondary(deleteItemDTOs);
      deleteRepairOrderOtherIncomeItemSecondary(deleteOtherIncomeItemDTOs);
      saveRepairOrderServiceSecondary(repairOrderSecondaryDTO);
      saveRepairOrderItemSecondary(repairOrderSecondaryDTO);
      saveRepairOrderOtherIncomeItemSecondary(repairOrderSecondaryDTO);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    return repairOrderSecondaryDTO;
  }

  public void saveRepairOrderSettlementSecondary(RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (repairOrderSecondaryDTO.getAgain()) {
      deleteRepairOrderSettlementSecondary(repairOrderSecondaryDTO.getId());
    }
    RepairOrderSettlementSecondaryDTO repairOrderSettlementSecondaryDTO = new RepairOrderSettlementSecondaryDTO();
    repairOrderSettlementSecondaryDTO.fromRepairOrderSecondaryDTO(repairOrderSecondaryDTO);
    List<RepairOrderSettlementSecondary> repairOrderSettlementSecondaries = txnWriter.findRepairOrderSettlementSecondaryByRepairOrderId(repairOrderSecondaryDTO.getShopId(), repairOrderSecondaryDTO.getId());
    if (CollectionUtils.isNotEmpty(repairOrderSettlementSecondaries)) {
      RepairOrderSettlementSecondary repairOrderSettlementSecondary = repairOrderSettlementSecondaries.get(repairOrderSettlementSecondaries.size() - 1);
      repairOrderSettlementSecondaryDTO.setBalance(repairOrderSettlementSecondary.getDebt());
      repairOrderSettlementSecondaryDTO.setDate(new java.util.Date().getTime());
    } else {
      repairOrderSettlementSecondaryDTO.setDate(repairOrderSecondaryDTO.getEndDate());
    }
    RepairOrderSettlementSecondary repairOrderSettlementSecondary = new RepairOrderSettlementSecondary();
    repairOrderSettlementSecondary.fromDTO(repairOrderSettlementSecondaryDTO);
    txnWriter.save(repairOrderSettlementSecondary);
  }

  public int deleteRepairOrderSettlementSecondary(Long repairOrderSecondaryId) {
    return txnDaoManager.getWriter().deleteRepairOrderSettlementSecondary(repairOrderSecondaryId);
  }

  public void saveRepairOrderServiceSecondary(RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RepairOrderServiceSecondaryDTO[] serviceDTOs = repairOrderSecondaryDTO.getServiceDTOs();
    if (serviceDTOs != null && serviceDTOs.length > 0) {
      for (int i = 0; i < serviceDTOs.length; i++) {
        if (serviceDTOs[i].getId() == null) {
          if (serviceDTOs[i].isValidator()) {
            RepairOrderServiceSecondary repairOrderServiceSecondary = new RepairOrderServiceSecondary();
            repairOrderServiceSecondary.setRepairOrderSecondaryId(repairOrderSecondaryDTO.getId());
            repairOrderServiceSecondary.setShopId(repairOrderSecondaryDTO.getShopId());
            repairOrderServiceSecondary.fromDTO(serviceDTOs[i]);
            txnWriter.save(repairOrderServiceSecondary);
          }
        } else {
          RepairOrderServiceSecondary repairOrderServiceSecondary = txnWriter.findById(RepairOrderServiceSecondary.class, serviceDTOs[i].getId());
          repairOrderServiceSecondary.fromDTO(serviceDTOs[i]);
          txnWriter.update(repairOrderServiceSecondary);
        }
      }
    }
  }

  public void saveRepairOrderItemSecondary(RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RepairOrderItemSecondaryDTO[] itemDTOs = repairOrderSecondaryDTO.getItemDTOs();
    if (itemDTOs != null && itemDTOs.length > 0) {
      for (int i = 0; i < itemDTOs.length; i++) {
        if (itemDTOs[i].getId() == null) {
          if (itemDTOs[i].isValidator()) {
            RepairOrderItemSecondary repairOrderItemSecondary = new RepairOrderItemSecondary();
            repairOrderItemSecondary.setRepairOrderSecondaryId(repairOrderSecondaryDTO.getId());
            repairOrderItemSecondary.setShopId(repairOrderSecondaryDTO.getShopId());
            repairOrderItemSecondary.fromDTO(itemDTOs[i]);
            txnWriter.save(repairOrderItemSecondary);
          }
        } else {
          RepairOrderItemSecondary repairOrderItemSecondary = txnWriter.findById(RepairOrderItemSecondary.class, itemDTOs[i].getId());
          repairOrderItemSecondary.fromDTO(itemDTOs[i]);
          txnWriter.update(repairOrderItemSecondary);
        }
      }
    }
  }

  public void saveRepairOrderOtherIncomeItemSecondary(RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RepairOrderOtherIncomeItemSecondaryDTO[] otherIncomeItemDTOs = repairOrderSecondaryDTO.getOtherIncomeItemDTOs();
    if (otherIncomeItemDTOs != null && otherIncomeItemDTOs.length > 0) {
      for (int i = 0; i < otherIncomeItemDTOs.length; i++) {
        if (otherIncomeItemDTOs[i].getId() == null) {
          if (otherIncomeItemDTOs[i].isValidator()) {
            RepairOrderOtherIncomeItemSecondary repairOrderOtherIncomeItemSecondary = new RepairOrderOtherIncomeItemSecondary();
            repairOrderOtherIncomeItemSecondary.setRepairOrderSecondaryId(repairOrderSecondaryDTO.getId());
            repairOrderOtherIncomeItemSecondary.setShopId(repairOrderSecondaryDTO.getShopId());
            repairOrderOtherIncomeItemSecondary.fromDTO(otherIncomeItemDTOs[i]);
            txnWriter.save(repairOrderOtherIncomeItemSecondary);
          }
        } else {
          RepairOrderOtherIncomeItemSecondary repairOrderOtherIncomeItemSecondary = txnWriter.findById(RepairOrderOtherIncomeItemSecondary.class, otherIncomeItemDTOs[i].getId());
          repairOrderOtherIncomeItemSecondary.fromDTO(otherIncomeItemDTOs[i]);
          txnWriter.update(repairOrderOtherIncomeItemSecondary);
        }
      }
    }
  }

  public void deleteRepairOrderServiceSecondary(Long[] deleteServiceDTOs) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (deleteServiceDTOs != null) {
      for (int i = 0; i < deleteServiceDTOs.length; i++) {
        txnWriter.delete(RepairOrderServiceSecondary.class, deleteServiceDTOs[i]);
      }
    }
  }

  public void deleteRepairOrderItemSecondary(Long[] deleteItemDTOs) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (deleteItemDTOs != null) {
      for (int i = 0; i < deleteItemDTOs.length; i++) {
        txnWriter.delete(RepairOrderItemSecondary.class, deleteItemDTOs[i]);
      }
    }
  }

  public void deleteRepairOrderOtherIncomeItemSecondary(Long[] deleteOtherIncomeItemDTOs) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (deleteOtherIncomeItemDTOs != null) {
      for (int i = 0; i < deleteOtherIncomeItemDTOs.length; i++) {
        txnWriter.delete(RepairOrderOtherIncomeItemSecondary.class, deleteOtherIncomeItemDTOs[i]);
      }
    }
  }

  public int updateRepairOrderSecondaryOrderStatus(Long shopId, Long repairOrderSecondaryId, OrderStatus orderStatus) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object object = txnWriter.begin();
    int result = 0;
    try {
      result = txnWriter.updateRepairOrderSecondaryOrderStatus(shopId, repairOrderSecondaryId, orderStatus);
      txnWriter.commit(object);
    } finally {
      txnWriter.rollback(object);
    }
    return result;
  }

  public RepairOrderSecondaryDTO findRepairOrderSecondaryByRepairOrderId(Long shopId, Long repairOrderId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RepairOrderSecondaryDTO repairOrderSecondaryDTO = null;
    RepairOrderSecondary repairOrderSecondary = txnWriter.findRepairOrderSecondaryByRepairOrderId(shopId, repairOrderId);
    if (repairOrderSecondary != null) {
      repairOrderSecondaryDTO = repairOrderSecondary.toDTO();
    }
    return repairOrderSecondaryDTO;
  }

  public RepairOrderSecondaryResponse statisticsRepairOrderSecondary(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    RepairOrderSecondaryResponse repairOrderSecondaryResponse = new RepairOrderSecondaryResponse();
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object[] temp = txnWriter.statisticsRepairOrderSecondary(shopId, repairOrderSecondaryCondition);
    DecimalFormat df = new DecimalFormat("#.##");
    repairOrderSecondaryResponse.setCount(temp[0] == null ? "0" : temp[0].toString());
    repairOrderSecondaryResponse.setTotal(temp[1] == null ? "0" : df.format(temp[1]));
    repairOrderSecondaryResponse.setDebt(temp[2] == null ? "0" : df.format(temp[2]));
    temp = txnWriter.statisticsRepairOrderSettlementSecondary(shopId, repairOrderSecondaryCondition);
    repairOrderSecondaryResponse.setIncome(temp[0] == null ? "0" : df.format(temp[0]));
    repairOrderSecondaryResponse.setDiscount(temp[1] == null ? "0" : df.format(temp[1]));
    return repairOrderSecondaryResponse;
  }

  public RepairOrderSecondaryResponse queryRepairOrderSecondary(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RepairOrderSecondaryResponse repairOrderSecondaryResponse = statisticsRepairOrderSecondary(shopId, repairOrderSecondaryCondition);
    List<RepairOrderSecondary> repairOrderSecondaryList = txnWriter.queryRepairOrderSecondary(shopId, repairOrderSecondaryCondition);
    if (CollectionUtils.isNotEmpty(repairOrderSecondaryList)) {
      List<RepairOrderSecondaryDTO> repairOrderSecondaryDTOList = new ArrayList<RepairOrderSecondaryDTO>();
      Long[] ids = new Long[repairOrderSecondaryList.size()];
      int i = 0;
      for (RepairOrderSecondary repairOrderSecondary : repairOrderSecondaryList) {
        repairOrderSecondaryDTOList.add(repairOrderSecondary.toDTO());
        ids[i++] = repairOrderSecondary.getId();
      }
      List<RepairOrderSettlementSecondary> repairOrderSettlementSecondarieList = txnWriter.findRepairOrderSettlementSecondaryByRepairOrderIds(shopId, ids);
      if (CollectionUtils.isNotEmpty(repairOrderSettlementSecondarieList)) {
        Map<Long, List<RepairOrderSettlementSecondaryDTO>> map = new HashMap<Long, List<RepairOrderSettlementSecondaryDTO>>();
        for (RepairOrderSettlementSecondary repairOrderSettlementSecondary : repairOrderSettlementSecondarieList) {
          List<RepairOrderSettlementSecondaryDTO> repairOrderSettlementSecondaryDTOList = map.get(repairOrderSettlementSecondary.getRepairOrderSecondaryId());
          if (repairOrderSettlementSecondaryDTOList == null) {
            repairOrderSettlementSecondaryDTOList = new ArrayList<RepairOrderSettlementSecondaryDTO>();
            map.put(repairOrderSettlementSecondary.getRepairOrderSecondaryId(), repairOrderSettlementSecondaryDTOList);
          }
          repairOrderSettlementSecondaryDTOList.add(repairOrderSettlementSecondary.toDTO());
        }
        for (RepairOrderSecondaryDTO repairOrderSecondaryDTO : repairOrderSecondaryDTOList) {
          List<RepairOrderSettlementSecondaryDTO> repairOrderSettlementSecondaryDTOList = map.get(repairOrderSecondaryDTO.getId());
          if (CollectionUtils.isNotEmpty(repairOrderSettlementSecondaryDTOList)) {
            double income = 0.0, discount = 0.0;
            for (RepairOrderSettlementSecondaryDTO repairOrderSettlementSecondaryDTO : repairOrderSettlementSecondaryDTOList) {
              income += income + (repairOrderSettlementSecondaryDTO.getIncome() == null ? 0.0 : repairOrderSettlementSecondaryDTO.getIncome());
              discount += discount + (repairOrderSettlementSecondaryDTO.getDiscount() == null ? 0.0 : repairOrderSettlementSecondaryDTO.getDiscount());
            }
            repairOrderSecondaryDTO.setSettledAmount(income);
            repairOrderSecondaryDTO.setAccountDiscount(discount);
          }
        }
      }
      repairOrderSecondaryResponse.setRepairOrderSecondaryDTOList(repairOrderSecondaryDTOList);
    }
    return repairOrderSecondaryResponse;
  }

  public int queryRepairOrderSecondarySize(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.queryRepairOrderSecondarySize(shopId, repairOrderSecondaryCondition);
  }

  public TxnDaoManager getTxnDaoManager() {
    return txnDaoManager;
  }

  public void setTxnDaoManager(TxnDaoManager txnDaoManager) {
    this.txnDaoManager = txnDaoManager;
  }
}
