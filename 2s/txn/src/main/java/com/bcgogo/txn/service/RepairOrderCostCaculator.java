package com.bcgogo.txn.service;

import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Inventory;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.UnitUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-5-29
 * Time: 下午1:19
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RepairOrderCostCaculator {

  public void calculate(RepairOrderDTO repairOrderDTO) {
    if (repairOrderDTO == null) {
      return;
    }
    repairOrderDTO.setTotalCostPrice(0d);
    RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
    //如果没有维修条目，将维修单总价设为o
    if (repairOrderItemDTOs == null || repairOrderItemDTOs.length == 0) {
      repairOrderDTO.setTotalCostPrice(0d);
      return;
    }
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    ProductLocalInfoDTO productLocalInfoDTO = null;
    //单位换算后采购单价
    Double purchasePriceWithUnit = 0d;

    Set<Long> productIds = new HashSet<Long>();
    Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>();
    Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
    Inventory inventory = null;

    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      if (repairOrderItemDTO == null || repairOrderItemDTO.getProductId() == null) {
        continue;
      }
      productIds.add(repairOrderItemDTO.getProductId());
    }

    inventoryMap = inventoryService.getInventoryMap(repairOrderDTO.getShopId(), productIds);
    productLocalInfoDTOMap = productService.getProductLocalInfoMap(repairOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));

    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOs) {
      if (repairOrderItemDTO == null) {
        continue;
      }
      repairOrderItemDTO.setCostPrice(0d);
      repairOrderItemDTO.setTotalCostPrice(0d);

      inventory = inventoryMap.get(repairOrderItemDTO.getProductId());
      if (inventory == null) {
        continue;
      }

      productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductId());
      if (productLocalInfoDTO == null) {
        continue;
      }
      if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productLocalInfoDTO)) {
        //单位换算后采购单价
        purchasePriceWithUnit = NumberUtil.toReserve(NumberUtil.doubleVal(inventory.getInventoryAveragePrice()) * productLocalInfoDTO.getRate(), 2);
      } else {
        purchasePriceWithUnit = NumberUtil.toReserve(inventory.getInventoryAveragePrice(), 2);
      }
      if (purchasePriceWithUnit != null) {
        //设置单价
        repairOrderItemDTO.setCostPrice(purchasePriceWithUnit);
        //item的金额
        repairOrderItemDTO.setTotalCostPrice(NumberUtil.toReserve(purchasePriceWithUnit.doubleValue() * repairOrderItemDTO.getAmount(), 2));
        //将维修单条目（item）金额累加到维修单(order)总金额中
        repairOrderDTO.setTotalCostPrice(NumberUtil.toReserve(repairOrderDTO.getTotalCostPrice().doubleValue() + repairOrderItemDTO.getTotalCostPrice().doubleValue(), 2));
      }
    }

    repairOrderDTO.setOtherTotalCostPrice(0D);
    if(CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
        if (NumberUtil.doubleVal(orderOtherIncomeItemDTO.getOtherIncomeCostPrice()) <= 0) {
          continue;
        }
        repairOrderDTO.setTotalCostPrice(NumberUtil.toReserve(repairOrderDTO.getTotalCostPrice().doubleValue() + NumberUtil.doubleVal(orderOtherIncomeItemDTO.getOtherIncomeCostPrice()), 2));
        repairOrderDTO.setOtherTotalCostPrice(NumberUtil.toReserve(repairOrderDTO.getOtherTotalCostPrice().doubleValue() + NumberUtil.doubleVal(orderOtherIncomeItemDTO.getOtherIncomeCostPrice()), 2));
      }
    }

  }

  /**
   * 销售单计算商品成本
   *
   * @param salesOrderDTO
   */
  public void calculate(SalesOrderDTO salesOrderDTO, Map<Long, Inventory> inventoryMap, Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap) {
    if (salesOrderDTO == null) {
      return;
    }
    salesOrderDTO.setTotalCostPrice(0d);

    SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
    if (ArrayUtils.isEmpty(salesOrderItemDTOs)) {
      return;
    }

    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);

    Set<Long> productIds = new HashSet<Long>();
    Inventory inventory = null;

    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
      if (salesOrderItemDTO == null || salesOrderItemDTO.getProductId() == null) {
        continue;
      }
      productIds.add(salesOrderItemDTO.getProductId());
    }
    if (MapUtils.isEmpty(inventoryMap)) {
      inventoryMap = inventoryService.getInventoryMap(salesOrderDTO.getShopId(), productIds);
    }
    if (MapUtils.isEmpty(productLocalInfoDTOMap)) {
      productLocalInfoDTOMap = productService.getProductLocalInfoMap(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
    }
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
      if (salesOrderItemDTO == null) {
        continue;
      }
      salesOrderItemDTO.setPrice(salesOrderItemDTO.getPrice());
      salesOrderItemDTO.setCostPrice(0d);
      salesOrderItemDTO.setTotalCostPrice(0d);
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItemDTO.getProductId());
      if (productLocalInfoDTO == null) {
        continue;
      }
      inventory = inventoryMap.get(salesOrderItemDTO.getProductId());
      if (inventory == null) {
        continue;
      }
      Double purchasePriceWithUnit = 0d;
      if (UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productLocalInfoDTO)) {
        //单位换算后采购单价
        purchasePriceWithUnit = NumberUtil.toReserve(NumberUtil.doubleVal(inventory.getInventoryAveragePrice()) * productLocalInfoDTO.getRate(), 2);
      } else {
        purchasePriceWithUnit = NumberUtil.toReserve(inventory.getInventoryAveragePrice(), 2);
      }
      if (purchasePriceWithUnit != null) {
        //设置单价
        salesOrderItemDTO.setCostPrice(purchasePriceWithUnit);
        //item的金额
        salesOrderItemDTO.setTotalCostPrice(NumberUtil.toReserve(purchasePriceWithUnit.doubleValue() * salesOrderItemDTO.getAmount(), 2));
        //将维修单条目（item）金额累加到维修单(order)总金额中
        salesOrderDTO.setTotalCostPrice(NumberUtil.toReserve(salesOrderDTO.getTotalCostPrice() + salesOrderItemDTO.getTotalCostPrice().doubleValue(), 2));
      }
    }
    salesOrderDTO.setOtherTotalCostPrice(0D);
    if(CollectionUtils.isNotEmpty(salesOrderDTO.getOtherIncomeItemDTOList())) {
      for (SalesOrderOtherIncomeItemDTO salesOrderOtherIncomeItemDTO : salesOrderDTO.getOtherIncomeItemDTOList()) {
        if (NumberUtil.doubleVal(salesOrderOtherIncomeItemDTO.getOtherIncomeCostPrice()) <= 0) {
          continue;
        }
        salesOrderDTO.setTotalCostPrice(NumberUtil.toReserve(salesOrderDTO.getTotalCostPrice().doubleValue() + NumberUtil.doubleVal(salesOrderOtherIncomeItemDTO.getOtherIncomeCostPrice()), 2));
        salesOrderDTO.setOtherTotalCostPrice(NumberUtil.toReserve(salesOrderDTO.getOtherTotalCostPrice().doubleValue() + NumberUtil.doubleVal(salesOrderOtherIncomeItemDTO.getOtherIncomeCostPrice()), 2));
      }
    }
  }
}
