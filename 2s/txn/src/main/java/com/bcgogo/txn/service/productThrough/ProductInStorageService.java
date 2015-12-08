package com.bcgogo.txn.service.productThrough;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 库存增加专用service
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-2
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductInStorageService implements IProductInStorageService {

  private static final Logger LOG = LoggerFactory.getLogger(IProductInStorageService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;
  private IProductThroughService productThroughService;
  private IProductOutStorageService productOutStorageService;
  private IProductService productService;

  public IProductThroughService getProductThroughService() {
    return productThroughService == null ? ServiceManager.getService(IProductThroughService.class) : productThroughService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }

  public IProductService getProductService() {
    return productService == null ? ServiceManager.getService(IProductService.class) : productService;
  }


  /**
   * 库存增加的单据 商品-库存打通逻辑
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   */
  public void productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      productThroughByOrder(bcgogoOrderDto, orderType, orderStatus,txnWriter);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }


  /**
   * 库存增加的单据 商品-库存打通逻辑
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   */
  public void productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus, TxnWriter txnWriter) {
    try {
      LOG.debug("单据入库开始打通:" + ReflectionToStringBuilder.toString(bcgogoOrderDto, ToStringStyle.SHORT_PREFIX_STYLE));
      StopWatchUtil sw = new StopWatchUtil("productInStorage:productThrough", "getRecords");
      if (bcgogoOrderDto == null || orderType == null|| ArrayUtil.isEmpty(bcgogoOrderDto.getItemDTOs())) {
        LOG.error("ProductInStorageService.productThroughByOrder,OrderTypes:" + orderType + ",orderStatus:" + orderStatus);
        return;
      }

      List<InStorageRecordDTO> inStorageRecordDTOList = getInStorageRecordByOrder(bcgogoOrderDto, orderType, orderStatus);
      sw.stopAndStart("setPrice");
      if(!bcgogoOrderDto.isSelectSupplier()){
        setPriceByInStorageRecordList(inStorageRecordDTOList);
      }
      sw.stopAndStart("save");
      this.saveOrUpdateInStorageRecordDTO(inStorageRecordDTOList, txnWriter);
      sw.stopAndStart("updateSupplierInventory");
      if(orderType != OrderTypes.INVENTORY) {
        Map<Long, List<SupplierInventoryDTO>> map = getSupplierInventoryList(inStorageRecordDTOList);

        List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
        for (List<SupplierInventoryDTO> list : map.values()) {
          supplierInventoryDTOList.addAll(list);
        }
        getProductThroughService().saveOrUpdateSupplierInventory(txnWriter, supplierInventoryDTOList);
      }
      sw.stopAndPrintLog();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("ProductInStorageService.productThroughByOrder,bcgogoOrderDto:" + JsonUtil.objectToJson(bcgogoOrderDto));
      LOG.error("ProductInStorageService.productThroughByOrder,OrderTypes:" + orderType + ",orderStatus:" + orderType);
    }
  }


  /**
   * 库存增加单据 保存商品入库记录 入库单 销售退货单 销售单、施工单作废 盘点单 内部退料单 维修退料单 仓库调拨单 借调单归还
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param orderStatus
   * @return
   */
  public List<InStorageRecordDTO> getInStorageRecordByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus) {
    List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();

    for (BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()) {
      if (orderType == OrderTypes.INVENTORY) {
        inStorageRecordDTOList.addAll(getInStorageRecordByInventory(bcgogoOrderDto, bcgogoOrderItemDto, orderStatus));
      } else if (orderType == OrderTypes.SALE || orderType == OrderTypes.REPAIR || orderType == OrderTypes.RETURN) {
        inStorageRecordDTOList.addAll(getInStorageRecordByRepeal(bcgogoOrderDto, bcgogoOrderItemDto, orderStatus, orderType));
      } else if (orderType == OrderTypes.INNER_RETURN || orderType == OrderTypes.ALLOCATE_RECORD || orderType == OrderTypes.RETURN_ORDER
          || orderType == OrderTypes.INVENTORY_CHECK || orderType == OrderTypes.REPAIR_PICKING || orderType == OrderTypes.SALE_RETURN) {
        inStorageRecordDTOList.addAll(getInStorageRecord(bcgogoOrderDto, bcgogoOrderItemDto, orderStatus, orderType));
      }
    }
    return inStorageRecordDTOList;
  }

  /**
   * 保存库存增加记录
   *
   * @param inStorageRecordDTOList
   */
  public void saveOrUpdateInStorageRecordDTO(List<InStorageRecordDTO> inStorageRecordDTOList,TxnWriter txnWriter) {
    if (CollectionUtil.isEmpty(inStorageRecordDTOList)) {
      return;
    }
    for (InStorageRecordDTO inStorageRecordDTO : inStorageRecordDTOList) {
      if (inStorageRecordDTO.getId() == null) {
        InStorageRecord inStorageRecord = new InStorageRecord();
        inStorageRecord.fromDTO(inStorageRecordDTO);
        txnWriter.save(inStorageRecord);
      } else {
        InStorageRecord inStorageRecord = txnWriter.getById(InStorageRecord.class, inStorageRecordDTO.getId());
        inStorageRecord.fromDTO(inStorageRecordDTO);
        txnWriter.update(inStorageRecord);
      }

    }
  }

  /**
   * 根据单据内容 item内容 和单据类型 保存库存增加记录 入库单
   *
   * @param bcgogoOrderDto
   * @param bcgogoOrderItemDto
   * @return
   */
  public List<InStorageRecordDTO> getInStorageRecordByInventory(BcgogoOrderDto bcgogoOrderDto, BcgogoOrderItemDto bcgogoOrderItemDto, OrderStatus orderStatus) {
    List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();

    PurchaseInventoryDTO purchaseInventoryDTO = (PurchaseInventoryDTO) bcgogoOrderDto;
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = (PurchaseInventoryItemDTO)bcgogoOrderItemDto;

    InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();

    inStorageRecordDTO.setShopId(purchaseInventoryDTO.getShopId());
    inStorageRecordDTO.setInStorageOrderId(purchaseInventoryDTO.getId());
    inStorageRecordDTO.setInStorageOrderType(OrderTypes.INVENTORY);
    inStorageRecordDTO.setInStorageOrderStatus(orderStatus);


    inStorageRecordDTO.setInStorageItemId(purchaseInventoryItemDTO.getId());
    inStorageRecordDTO.setProductId(purchaseInventoryItemDTO.getProductId());
    inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(purchaseInventoryItemDTO.getAmount()));
    inStorageRecordDTO.setInStorageUnit(purchaseInventoryItemDTO.getUnit());
    inStorageRecordDTO.setRemainAmount(NumberUtil.doubleVal(purchaseInventoryItemDTO.getAmount()));
    inStorageRecordDTO.setPrice(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPurchasePrice()));

    inStorageRecordDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
    inStorageRecordDTO.setSupplierName(purchaseInventoryDTO.getSupplier());

    inStorageRecordDTO.setStorehouseId(purchaseInventoryDTO.getStorehouseId());
    inStorageRecordDTO.setStorehouseName(purchaseInventoryDTO.getStorehouseName());
    inStorageRecordDTO.setSupplierType(OutStorageSupplierType.NATIVE_SUPPLIER);
    inStorageRecordDTO.setSupplierRelatedAmount(NumberUtil.doubleVal(purchaseInventoryItemDTO.getAmount()));

    inStorageRecordDTOList.add(inStorageRecordDTO);

    return inStorageRecordDTOList;
  }


  /**
   * 根据单据内容 item内容 和单据类型 保存库存增加记录 销售单或者施工单作废 销售退货单
   *
   * @param bcgogoOrderDto
   * @param bcgogoOrderItemDto
   * @return
   */
  public List<InStorageRecordDTO> getInStorageRecordByRepeal(BcgogoOrderDto bcgogoOrderDto, BcgogoOrderItemDto bcgogoOrderItemDto, OrderStatus orderStatus, OrderTypes orderType) {
    StopWatchUtil sw = new StopWatchUtil("getInStorageRecordByRepeal", "start");
    List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();

    SalesOrderItemDTO salesOrderItemDTO = null;
    RepairOrderItemDTO repairOrderItemDTO = null;
    Set<Long> productIdSet = new HashSet<Long>();
    productIdSet.add(bcgogoOrderItemDto.getProductId());

    if (orderType == OrderTypes.SALE) {
      salesOrderItemDTO = (SalesOrderItemDTO) bcgogoOrderItemDto;
      bcgogoOrderItemDto.setItemCostPrice(salesOrderItemDTO.getCostPrice());
      bcgogoOrderItemDto.setItemTotalCostPrice(salesOrderItemDTO.getTotalCostPrice());
    } else if (orderType == OrderTypes.REPAIR) {
      repairOrderItemDTO = (RepairOrderItemDTO) bcgogoOrderItemDto;
      bcgogoOrderItemDto.setItemCostPrice(repairOrderItemDTO.getCostPrice());
      bcgogoOrderItemDto.setItemTotalCostPrice(repairOrderItemDTO.getTotalCostPrice());
    }else if (orderType == OrderTypes.RETURN) {
      PurchaseReturnItemDTO  purchaseReturnItemDTO= (PurchaseReturnItemDTO) bcgogoOrderItemDto;
      bcgogoOrderItemDto.setItemCostPrice(purchaseReturnItemDTO.getPrice());
      bcgogoOrderItemDto.setItemTotalCostPrice(purchaseReturnItemDTO.getTotal());
    }else {
      return inStorageRecordDTOList;
    }

    List<SupplierInventory> undefinedSupplierInventory = new ArrayList<SupplierInventory>();
    List<SupplierInventory> importSupplierInventory  = new ArrayList<SupplierInventory>();
    Map<Long,List<SupplierInventory>> normalSupplierInventoryMap = new HashMap<Long,List<SupplierInventory>>();

    sw.stopAndStart("getSupplierInventories");
    List<SupplierInventory> supplierInventories = txnDaoManager.getWriter().getSupplierInventoryByStorehouseIdAndProductIds(
        bcgogoOrderDto.getShopId(), bcgogoOrderDto.getStorehouseId(), bcgogoOrderItemDto.getProductId());


    if(CollectionUtils.isNotEmpty(supplierInventories)) {
      for (SupplierInventory supplierInventory : supplierInventories) {
        if (supplierInventory.getSupplierType() == OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER) {
          importSupplierInventory.add(supplierInventory);
        } else if (supplierInventory.getSupplierType() == OutStorageSupplierType.UNDEFINED_SUPPLIER) {
          undefinedSupplierInventory.add(supplierInventory);
        } else if (supplierInventory.getSupplierId() != null) {

          List<SupplierInventory> list = normalSupplierInventoryMap.get(supplierInventory.getSupplierId());
          if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<SupplierInventory>();
          }
          list.add(supplierInventory);
          normalSupplierInventoryMap.put(supplierInventory.getSupplierId(), list);
        } else {
          undefinedSupplierInventory.add(supplierInventory);
        }
      }
    }
    sw.stopAndStart("getProductMap");
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(bcgogoOrderDto.getShopId(), productIdSet.toArray(new Long[productIdSet.size()]));


    sw.stopAndStart("getOutStorageRelation");
    List<OutStorageRelationDTO> outStorageRelationDTOList = getProductThroughService().getOutStorageRelation(bcgogoOrderDto.getShopId(), bcgogoOrderDto.getId(), orderType,
        bcgogoOrderItemDto.getId(), bcgogoOrderItemDto.getProductId());

    sw.stopAndStart("process-1");
    if (CollectionUtils.isEmpty(outStorageRelationDTOList)) {

      InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();
      inStorageRecordDTO.setPrice(bcgogoOrderItemDto.getItemCostPrice());
      inStorageRecordDTO.setShopId(bcgogoOrderDto.getShopId());
      inStorageRecordDTO.setInStorageOrderId(bcgogoOrderDto.getId());
      inStorageRecordDTO.setInStorageOrderType(orderType);
      inStorageRecordDTO.setInStorageOrderStatus(orderStatus);
      inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
      inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
      inStorageRecordDTO.setInStorageUnit(bcgogoOrderItemDto.getUnit());
      inStorageRecordDTO.setStorehouseId(bcgogoOrderDto.getStorehouseId());
      inStorageRecordDTO.setStorehouseName(bcgogoOrderDto.getStorehouseName());
      inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(bcgogoOrderItemDto.getAmount()));
      inStorageRecordDTO.setRemainAmount(inStorageRecordDTO.getInStorageItemAmount());
      inStorageRecordDTO.setSupplierId(null);
      inStorageRecordDTO.setSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
      inStorageRecordDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      inStorageRecordDTO.setSupplierRelatedAmount(inStorageRecordDTO.getInStorageItemAmount());


      List<OutStorageRelationDTO> relationList = new ArrayList<OutStorageRelationDTO>();
      OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
      outStorageRelationDTO.setUseRelatedAmount(bcgogoOrderItemDto.getAmount());
      outStorageRelationDTO.setSupplierRelatedAmount(bcgogoOrderItemDto.getAmount());
      outStorageRelationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      SupplierInventory supplierInventory = CollectionUtil.getFirst(undefinedSupplierInventory);
      if (supplierInventory != null) {
        if (UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()))) {
          outStorageRelationDTO.setAverageStoragePrice(NumberUtil.doubleVal(supplierInventory.getAverageStoragePrice()) * productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()).getRate());
        } else {
          outStorageRelationDTO.setAverageStoragePrice(NumberUtil.doubleVal(supplierInventory.getAverageStoragePrice()));
        }
      }
      relationList.add(outStorageRelationDTO);
      bcgogoOrderItemDto.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[relationList.size()]));

      inStorageRecordDTOList.add(inStorageRecordDTO);
      sw.stopAndPrintLog();
      return inStorageRecordDTOList;
    }


    Map<Long, List<OutStorageRelationDTO>> map = new HashMap<Long, List<OutStorageRelationDTO>>();

    List<OutStorageRelationDTO> undefinedSupplierType = new ArrayList<OutStorageRelationDTO>();
    List<OutStorageRelationDTO> importSupplierType = new ArrayList<OutStorageRelationDTO>();

    for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOList) {

      if (outStorageRelationDTO.getDisabled() == YesNo.YES) {
        continue;
      }

      if (outStorageRelationDTO.getRelatedSupplierId() != null && outStorageRelationDTO.getSupplierType() == null) {
        outStorageRelationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      }

      if (outStorageRelationDTO.getSupplierType() == OutStorageSupplierType.UNDEFINED_SUPPLIER) {
        undefinedSupplierType.add(outStorageRelationDTO);
      } else if (outStorageRelationDTO.getSupplierType() == OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER) {
        importSupplierType.add(outStorageRelationDTO);
      } else {
        List<OutStorageRelationDTO> supplierList = null;
        if (map.containsKey(outStorageRelationDTO.getRelatedSupplierId())) {
          supplierList = map.get(outStorageRelationDTO.getRelatedSupplierId());
        } else {
          supplierList = new ArrayList<OutStorageRelationDTO>();
        }
        supplierList.add(outStorageRelationDTO);
        map.put(outStorageRelationDTO.getRelatedSupplierId(), supplierList);
      }

      InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();

      inStorageRecordDTO.setShopId(bcgogoOrderDto.getShopId());
      inStorageRecordDTO.setInStorageOrderId(bcgogoOrderDto.getId());

      inStorageRecordDTO.setInStorageOrderType(orderType);
      inStorageRecordDTO.setInStorageOrderStatus(orderStatus);
      inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
      inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
      inStorageRecordDTO.setPrice(bcgogoOrderItemDto.getItemCostPrice());
      inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()));
      inStorageRecordDTO.setInStorageUnit(outStorageRelationDTO.getRelatedUnit());
      inStorageRecordDTO.setRemainAmount(inStorageRecordDTO.getInStorageItemAmount());
      inStorageRecordDTO.setSupplierId(outStorageRelationDTO.getRelatedSupplierId());
      inStorageRecordDTO.setSupplierName(outStorageRelationDTO.getRelatedSupplierName());
      inStorageRecordDTO.setStorehouseId(bcgogoOrderDto.getStorehouseId());
      inStorageRecordDTO.setStorehouseName(bcgogoOrderDto.getStorehouseName());
      inStorageRecordDTO.setSupplierType(outStorageRelationDTO.getSupplierType());
      inStorageRecordDTO.setSupplierRelatedAmount(outStorageRelationDTO.getSupplierRelatedAmount());
      inStorageRecordDTOList.add(inStorageRecordDTO);
    }

    sw.stopAndStart("process-2");
    List<OutStorageRelationDTO> relationDTOList = new ArrayList<OutStorageRelationDTO>();
    if (CollectionUtils.isNotEmpty(undefinedSupplierType)) {
      OutStorageRelationDTO relationDTO = CollectionUtil.getFirst(undefinedSupplierType);
      relationDTO.setUseRelatedAmount(relationDTO.getSupplierRelatedAmount());
      relationDTO.setId(null);
      //todo 价格

      SupplierInventory supplierInventory = CollectionUtil.getFirst(undefinedSupplierInventory);
      if (supplierInventory != null) {
        if (UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()))) {
          relationDTO.setAverageStoragePrice(supplierInventory.getAverageStoragePrice() * productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()).getRate());
        } else {
          relationDTO.setAverageStoragePrice(supplierInventory.getAverageStoragePrice());
        }
      }
      relationDTOList.add(relationDTO);
    }
    if (CollectionUtils.isNotEmpty(importSupplierType)) {
      OutStorageRelationDTO relationDTO = CollectionUtil.getFirst(importSupplierType);
      relationDTO.setUseRelatedAmount(relationDTO.getSupplierRelatedAmount());
      relationDTO.setId(null);

      SupplierInventory supplierInventory = CollectionUtil.getFirst(importSupplierInventory);
      if (supplierInventory != null) {
        if (UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()))) {
          relationDTO.setAverageStoragePrice(supplierInventory.getAverageStoragePrice() * productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()).getRate());
        } else {
          relationDTO.setAverageStoragePrice(supplierInventory.getAverageStoragePrice());
        }
      }

      relationDTOList.add(relationDTO);
    }

    sw.stopAndStart("process-3");
    if (MapUtils.isNotEmpty(map)) {
      for (Long supplierId : map.keySet()) {
        List<OutStorageRelationDTO> supplierList = map.get(supplierId);
        OutStorageRelationDTO relationDTO = CollectionUtil.getFirst(supplierList);
        relationDTO.setUseRelatedAmount(relationDTO.getSupplierRelatedAmount());
        relationDTO.setId(null);
        relationDTOList.add(relationDTO);

        SupplierInventory supplierInventory = CollectionUtil.getFirst(normalSupplierInventoryMap.get(supplierId));
        if (supplierInventory != null) {
          if (UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()))) {
            relationDTO.setAverageStoragePrice(supplierInventory.getAverageStoragePrice() * productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId()).getRate());
          } else {
            relationDTO.setAverageStoragePrice(supplierInventory.getAverageStoragePrice());
          }
        }

      }
    }
    bcgogoOrderItemDto.setOutStorageRelationDTOs(relationDTOList.toArray(new OutStorageRelationDTO[relationDTOList.size()]));

    sw.stopAndPrintLog();
    return inStorageRecordDTOList;
  }

  /**
   * 根据单据内容 item内容 和单据类型 保存库存增加记录 内部退料单，借调单，维修退料，仓库调拨单
   *
   * @param bcgogoOrderDto
   * @param bcgogoOrderItemDto
   * @return
   */
  public List<InStorageRecordDTO> getInStorageRecord(BcgogoOrderDto bcgogoOrderDto, BcgogoOrderItemDto bcgogoOrderItemDto, OrderStatus orderStatus, OrderTypes orderType) {
    List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();

    double costPrice = 0D;

    if (orderType == OrderTypes.SALE_RETURN) {
      costPrice = NumberUtil.doubleVal(((SalesReturnItemDTO) bcgogoOrderItemDto).getCostPrice());
    } else if (orderType == OrderTypes.INNER_RETURN) {
      costPrice = NumberUtil.doubleVal(((InnerReturnItemDTO) bcgogoOrderItemDto).getPrice());
    } else if (orderType == OrderTypes.ALLOCATE_RECORD) {
      costPrice = NumberUtil.doubleVal(((AllocateRecordItemDTO) bcgogoOrderItemDto).getCostPrice());
    } else if (orderType == OrderTypes.RETURN_ORDER) {
      costPrice = NumberUtil.doubleVal(((ReturnOrderItemDTO) bcgogoOrderItemDto).getPrice());
    }else if(OrderTypes.REPAIR_PICKING.equals(orderType)){
//      costPrice = NumberUtil.doubleVal(((RepairPickingItemDTO) bcgogoOrderItemDto).getCostPrice());
    }

    boolean selectSupplier = bcgogoOrderDto.isSelectSupplier();
    if (!selectSupplier) {

      InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();
      inStorageRecordDTO.setPrice(costPrice);
      inStorageRecordDTO.setShopId(bcgogoOrderDto.getShopId());
      inStorageRecordDTO.setInStorageOrderId(bcgogoOrderDto.getId());
      inStorageRecordDTO.setInStorageOrderType(orderType);
      inStorageRecordDTO.setInStorageOrderStatus(orderStatus);
      inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
      inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
      inStorageRecordDTO.setInStorageUnit(bcgogoOrderItemDto.getUnit());
      inStorageRecordDTO.setStorehouseId(bcgogoOrderDto.getStorehouseId());
      inStorageRecordDTO.setStorehouseName(bcgogoOrderDto.getStorehouseName());

      if (orderType == OrderTypes.RETURN_ORDER) {
        inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(((ReturnOrderItemDTO) bcgogoOrderItemDto).getReturnAmount()));
      } else {
        inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(bcgogoOrderItemDto.getAmount()));
      }

      inStorageRecordDTO.setRemainAmount(inStorageRecordDTO.getInStorageItemAmount());
      inStorageRecordDTO.setSupplierId(null);
      inStorageRecordDTO.setSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
      inStorageRecordDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      inStorageRecordDTO.setSupplierRelatedAmount(inStorageRecordDTO.getInStorageItemAmount());
      inStorageRecordDTOList.add(inStorageRecordDTO);

      return inStorageRecordDTOList;
    }

    for (OutStorageRelationDTO outStorageRelationDTO : bcgogoOrderItemDto.getOutStorageRelationDTOs()) {

      if (NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()) <= 0) {
        continue;
      }

      InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();
      inStorageRecordDTO.setPrice(costPrice);

      inStorageRecordDTO.setShopId(bcgogoOrderDto.getShopId());
      inStorageRecordDTO.setInStorageOrderId(bcgogoOrderDto.getId());
      inStorageRecordDTO.setInStorageOrderType(orderType);
      inStorageRecordDTO.setInStorageOrderStatus(orderStatus);

      inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
      inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
      inStorageRecordDTO.setInStorageUnit(bcgogoOrderItemDto.getUnit());
      inStorageRecordDTO.setStorehouseId(bcgogoOrderDto.getStorehouseId());
      inStorageRecordDTO.setStorehouseName(bcgogoOrderDto.getStorehouseName());
      inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()));
      inStorageRecordDTO.setSupplierRelatedAmount(NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()));

      Set<Long> productIdSet = new HashSet<Long>();
      productIdSet.add(inStorageRecordDTO.getProductId());
      inStorageRecordDTO.setSupplierId(outStorageRelationDTO.getRelatedSupplierId() == null ?
          NumberUtil.longValue(outStorageRelationDTO.getRelatedSupplierIdStr()) : outStorageRelationDTO.getRelatedSupplierId());


      if(orderType == OrderTypes.ALLOCATE_RECORD) {
        inStorageRecordDTO.setAverageStoragePrice(outStorageRelationDTO.getAverageStoragePrice());
      }

      Map<Long, SupplierInventory> map = getProductThroughService().getSupplierInventoryMap(inStorageRecordDTO.getShopId(), inStorageRecordDTO.getSupplierId(),
          inStorageRecordDTO.getStorehouseId(), productIdSet);
      SupplierInventory supplierInventory = map.get(inStorageRecordDTO.getProductId());

      if (supplierInventory != null) {
        if (orderType != OrderTypes.SALE_RETURN
            && orderType != OrderTypes.INNER_RETURN
            && orderType != OrderTypes.ALLOCATE_RECORD
            && orderType != OrderTypes.RETURN_ORDER) {
          inStorageRecordDTO.setPrice(NumberUtil.doubleVal(supplierInventory.getAverageStoragePrice()));
        }
        inStorageRecordDTO.setSupplierType(supplierInventory.getSupplierType());
      } else {
        LOG.info("getInStorageRecord.supplierInventory is null: shopId:" + inStorageRecordDTO.getShopId() + ",productId:"
            + inStorageRecordDTO.getProductId() + ",storeHouseId:" + inStorageRecordDTO.getStorehouseId() + ",supplierId:" + inStorageRecordDTO.getSupplierId());
        inStorageRecordDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      }
      if (orderType.equals(OrderTypes.ALLOCATE_RECORD)) {
        inStorageRecordDTO.setPrice(outStorageRelationDTO.getRelatedSupplierAveragePrice());
      }
      inStorageRecordDTO.setRemainAmount(inStorageRecordDTO.getInStorageItemAmount());
      inStorageRecordDTO.setSupplierName(outStorageRelationDTO.getRelatedSupplierName());
      inStorageRecordDTO.setSupplierId(outStorageRelationDTO.getRelatedSupplierId());
      inStorageRecordDTO.setSupplierType(outStorageRelationDTO.getSupplierType());
      inStorageRecordDTOList.add(inStorageRecordDTO);
    }
    return inStorageRecordDTOList;
  }

  /**
   * 保存supplier_inventory 根据库存增加记录
   *
   * @param inStorageRecordDTOList
   * @return
   */
  public Map<Long, List<SupplierInventoryDTO>> getSupplierInventoryList(List<InStorageRecordDTO> inStorageRecordDTOList) {

    Map<Long, List<SupplierInventoryDTO>> map = new HashMap<Long, List<SupplierInventoryDTO>>();
    if (CollectionUtil.isEmpty(inStorageRecordDTOList)) {
      return map;
    }
    List<SupplierInventoryDTO> supplierInventoryDTOList = null;

    for (InStorageRecordDTO inStorageRecordDTO : inStorageRecordDTOList) {

      SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
      supplierInventoryDTO.setShopId(inStorageRecordDTO.getShopId());
      supplierInventoryDTO.setProductId(inStorageRecordDTO.getProductId());
      supplierInventoryDTO.setStorehouseId(inStorageRecordDTO.getStorehouseId());
      supplierInventoryDTO.setChangeAmount(inStorageRecordDTO.getInStorageItemAmount());
      supplierInventoryDTO.setUnit(inStorageRecordDTO.getInStorageUnit());

      if(OrderTypes.INVENTORY == inStorageRecordDTO.getInStorageOrderType()){
        supplierInventoryDTO.setTotalInStorageChangeAmount(inStorageRecordDTO.getInStorageItemAmount());
      }else{
        supplierInventoryDTO.setTotalInStorageChangeAmount(0D);
      }

      if(OrderTypes.ALLOCATE_RECORD.equals(inStorageRecordDTO.getInStorageOrderType())){
       //用于更新平均价
        supplierInventoryDTO.setLastStoragePrice(inStorageRecordDTO.getAverageStoragePrice());
        supplierInventoryDTO.setLastStorageAmount(inStorageRecordDTO.getInStorageItemAmount());
      }


      supplierInventoryDTO.setSupplierType(inStorageRecordDTO.getSupplierType());
      supplierInventoryDTO.setSupplierId(inStorageRecordDTO.getSupplierId());
      supplierInventoryDTO.setSupplierName(inStorageRecordDTO.getSupplierName());

      if (map.containsKey(inStorageRecordDTO.getProductId())) {
        supplierInventoryDTOList = map.get(inStorageRecordDTO.getProductId());
      } else {
        supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
      }
      supplierInventoryDTOList.add(supplierInventoryDTO);

      map.put(inStorageRecordDTO.getProductId(), supplierInventoryDTOList);
    }
    return map;

  }


  /**
   * 库存盘点单打通逻辑
   * @param inventoryCheckDTO
   */
  public void productThroughByInventoryCheck(InventoryCheckDTO inventoryCheckDTO,TxnWriter txnWriter) {
    try {

      if (inventoryCheckDTO == null || ArrayUtils.isEmpty(inventoryCheckDTO.getItemDTOs())) {
        LOG.error("ProductInStorageService.productThroughByInventoryCheck:inventoryCheckDTO null:" + JsonUtil.objectToJson(inventoryCheckDTO));
        return;
      }
      List<InventoryCheckItemDTO> storageAddList = new ArrayList<InventoryCheckItemDTO>();//库存增加的盘点
      List<InventoryCheckItemDTO> storageReduceList = new ArrayList<InventoryCheckItemDTO>();//库存减少的盘点

      InventoryCheckItemDTO[] inventoryCheckItemDTOs = inventoryCheckDTO.getItemDTOs();

      boolean selectSupplier = inventoryCheckDTO.isSelectSupplier();

      for (InventoryCheckItemDTO inventoryCheckItemDTO : inventoryCheckDTO.getItemDTOs()) {

        if (!selectSupplier) {
          //盘盈
          if (inventoryCheckItemDTO.getActualInventoryAmount() > inventoryCheckItemDTO.getInventoryAmount()) {
            inventoryCheckItemDTO.setAmount(inventoryCheckItemDTO.getActualInventoryAmount() - inventoryCheckItemDTO.getInventoryAmount());
            storageAddList.add(inventoryCheckItemDTO);
          } else {//盘亏
            inventoryCheckItemDTO.setAmount(inventoryCheckItemDTO.getInventoryAmount() - inventoryCheckItemDTO.getActualInventoryAmount());
            storageReduceList.add(inventoryCheckItemDTO);
          }
        } else {

          OutStorageRelationDTO[] outStorageRelationDTOs = inventoryCheckItemDTO.getOutStorageRelationDTOs();
          if (ArrayUtils.isEmpty(outStorageRelationDTOs)) {
            LOG.error("ProductInStorageService.productThroughByInventoryCheck:outStorageRelationDTOs empty");
            continue;
          }

          for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOs) {

            if (NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()) == NumberUtil.doubleVal(outStorageRelationDTO.getRelatedSupplierInventory())) {
              continue;
            }
            InventoryCheckItem inventoryCheckItem = new InventoryCheckItem();
            inventoryCheckItem.fromDTO(inventoryCheckItemDTO);
            InventoryCheckItemDTO checkItemDTO = inventoryCheckItem.toDTO();
            checkItemDTO.setId(inventoryCheckItemDTO.getId());

            //盘多的
            if (NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()) - NumberUtil.doubleVal(outStorageRelationDTO.getRelatedSupplierInventory()) > 0) {
              storageAddList.add(checkItemDTO);
            } else {
              storageReduceList.add(checkItemDTO);
            }

            checkItemDTO.setAmount(Math.abs(NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()) - NumberUtil.doubleVal(outStorageRelationDTO.getRelatedSupplierInventory())));
            checkItemDTO.setAmount(NumberUtil.toReserve(checkItemDTO.getAmount(), NumberUtil.PRECISION));
            OutStorageRelationDTO[] storageRelationDTOs = new OutStorageRelationDTO[1];
            storageRelationDTOs[0] = outStorageRelationDTO;
            outStorageRelationDTO.setUseRelatedAmount(checkItemDTO.getAmount());
            checkItemDTO.setOutStorageRelationDTOs(storageRelationDTOs);

          }
        }
      }

      if (CollectionUtil.isNotEmpty(storageAddList)) {
        inventoryCheckDTO.setItemDTOs(storageAddList.toArray(new InventoryCheckItemDTO[storageAddList.size()]));
        this.productThroughByOrder(inventoryCheckDTO, OrderTypes.INVENTORY_CHECK, null, txnWriter);
      }
      if (CollectionUtil.isNotEmpty(storageReduceList)) {
        inventoryCheckDTO.setItemDTOs(storageReduceList.toArray(new InventoryCheckItemDTO[storageReduceList.size()]));
        getProductOutStorageService().productThroughByOrder(inventoryCheckDTO, OrderTypes.INVENTORY_CHECK, null, txnWriter,null);
      }
      inventoryCheckDTO.setItemDTOs(inventoryCheckItemDTOs);
      return;

    } catch (Exception e) {
      LOG.error("ProductInStorageService.productThroughByInventoryCheck" + ",inventoryCheckDTO:" + inventoryCheckDTO.toString());
      LOG.error(e.getMessage(), e);
    }
  }

   /**
   * 对商品库存增加的单据进行排序
   * @param inStorageRecordDTOList
   */
  public List<InStorageRecordDTO> sortInStorageRecordList(List<InStorageRecordDTO> inStorageRecordDTOList) {
    List<InStorageRecordDTO> returnList = new ArrayList<InStorageRecordDTO>();

    if (CollectionUtil.isEmpty(inStorageRecordDTOList)) {
      return returnList;
    }

    List<InStorageRecordDTO> purchaseInventoryList = new ArrayList<InStorageRecordDTO>();     //入库单
    List<InStorageRecordDTO> repealList = new ArrayList<InStorageRecordDTO>();  //销售单或者施工单作废
    List<InStorageRecordDTO> salesReturnList = new ArrayList<InStorageRecordDTO>();   //销售退货单
    List<InStorageRecordDTO> innerReturnList = new ArrayList<InStorageRecordDTO>();  //内部退料单
    List<InStorageRecordDTO> repairPickingReturn = new ArrayList<InStorageRecordDTO>();  //维修退料单
    List<InStorageRecordDTO> inventoryCheckList = new ArrayList<InStorageRecordDTO>(); //库存盘点单
    List<InStorageRecordDTO> allocateRecordList = new ArrayList<InStorageRecordDTO>();   //仓库调拨单
    List<InStorageRecordDTO> borrowReturnOrderList = new ArrayList<InStorageRecordDTO>(); //借调单归还
    List<InStorageRecordDTO> importList = new ArrayList<InStorageRecordDTO>(); //导入的单据
    List<InStorageRecordDTO> purchaseReturnList = new ArrayList<InStorageRecordDTO>(); //入库退货单作废

    List<InStorageRecordDTO> otherList = new ArrayList<InStorageRecordDTO>(); //未知单据

    for (InStorageRecordDTO inStorageRecordDTO : inStorageRecordDTOList) {
      OrderTypes orderType = inStorageRecordDTO.getInStorageOrderType();
      if (orderType == null) {
        importList.add(inStorageRecordDTO);
      } else {
        switch (orderType) {
          case REPAIR:
            repealList.add(inStorageRecordDTO);
            break;
          case SALE:
            repealList.add(inStorageRecordDTO);
            break;
          case INVENTORY:
            purchaseInventoryList.add(inStorageRecordDTO);
            break;
          case SALE_RETURN:
            salesReturnList.add(inStorageRecordDTO);
            break;
          case INVENTORY_CHECK:
            inventoryCheckList.add(inStorageRecordDTO);
            break;
          case ALLOCATE_RECORD:
            allocateRecordList.add(inStorageRecordDTO);
            break;
          case REPAIR_PICKING:
            repairPickingReturn.add(inStorageRecordDTO);
            break;
          case INNER_RETURN:
            innerReturnList.add(inStorageRecordDTO);
            break;
          case RETURN_ORDER:
            borrowReturnOrderList.add(inStorageRecordDTO);
            break;
          case RETURN:
            purchaseReturnList.add(inStorageRecordDTO);
            break;
          default:
            otherList.add(inStorageRecordDTO);
            LOG.error("sortInStorageRecord orderType error:" + orderType.getName());
        }
      }
    }
    if (CollectionUtil.isNotEmpty(purchaseReturnList)) {
      returnList.addAll(purchaseReturnList);
    }

    if (CollectionUtil.isNotEmpty(otherList)) {
      returnList.addAll(otherList);
    }
    if (CollectionUtil.isNotEmpty(importList)) {
      returnList.addAll(importList);
    }

    if (CollectionUtil.isNotEmpty(repealList)) {
      returnList.addAll(repealList);
    }
    if (CollectionUtil.isNotEmpty(salesReturnList)) {
      returnList.addAll(salesReturnList);
    }
    if (CollectionUtil.isNotEmpty(borrowReturnOrderList)) {
      returnList.addAll(borrowReturnOrderList);
    }
    if (CollectionUtil.isNotEmpty(allocateRecordList)) {
      returnList.addAll(allocateRecordList);
    }
    if (CollectionUtil.isNotEmpty(repairPickingReturn)) {
      returnList.addAll(repairPickingReturn);
    }
    if (CollectionUtil.isNotEmpty(innerReturnList)) {
      returnList.addAll(innerReturnList);
    }
    if (CollectionUtil.isNotEmpty(inventoryCheckList)) {
      returnList.addAll(inventoryCheckList);
    }
    if (CollectionUtil.isNotEmpty(purchaseInventoryList)) {
      returnList.addAll(purchaseInventoryList);
    }
    return returnList;
  }

  /**
   * 仓库调拨单处理流程
   * @param allocateRecordDTO
   * @param txnWriter
   * 1.更新每个item的SupplierInventory
   * 2.保存原仓库的出库记录
   * 3.保存目的仓库的入库记录
   */
  public void productThroughByAllocateRecord(AllocateRecordDTO allocateRecordDTO,TxnWriter txnWriter) {
    if (allocateRecordDTO == null || ArrayUtil.isEmpty(allocateRecordDTO.getItemDTOs())) {
      return;
    }
    allocateRecordDTO.setStorehouseId(allocateRecordDTO.getOutStorehouseId());
    allocateRecordDTO.setStorehouseName(allocateRecordDTO.getOutStorehouseName());

    getProductOutStorageService().productThroughByOrder(allocateRecordDTO,OrderTypes.ALLOCATE_RECORD,null,txnWriter,null);

    allocateRecordDTO.setStorehouseId(allocateRecordDTO.getInStorehouseId());
    allocateRecordDTO.setStorehouseName(allocateRecordDTO.getInStorehouseName());
    this.productThroughByOrder(allocateRecordDTO,OrderTypes.ALLOCATE_RECORD,null,txnWriter);
  }

  /**
   * 非选择供应商的版本 把每个商品库存增加记录的价格 设置成平均价
   *
   * @param inStorageRecordDTOList
   */
  public void setPriceByInStorageRecordList(List<InStorageRecordDTO> inStorageRecordDTOList) {
    if (CollectionUtils.isEmpty(inStorageRecordDTOList)) {
      return;
    }

    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Long shopId = null;
    Set<Long> productIdSet = new HashSet<Long>();


    for (InStorageRecordDTO inStorageRecordDTO : inStorageRecordDTOList) {
      productIdSet.add(inStorageRecordDTO.getProductId());
      shopId = inStorageRecordDTO.getShopId();
    }
    if (shopId == null) {
      return;
    }

    Map<Long, Inventory> inventoryMap = inventoryService.getInventoryMap(shopId, productIdSet);

    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(shopId, productIdSet.toArray(new Long[productIdSet.size()]));

    for (InStorageRecordDTO inStorageRecordDTO : inStorageRecordDTOList) {

      Inventory inventory = inventoryMap.get(inStorageRecordDTO.getProductId());
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(inStorageRecordDTO.getProductId());
      Double purchasePriceWithUnit = 0D;
      if (productLocalInfoDTO != null && inventory != null) {
        if (UnitUtil.isStorageUnit(inStorageRecordDTO.getInStorageUnit(), productLocalInfoDTO)) {
          //单位换算后采购单价
          purchasePriceWithUnit = NumberUtil.toReserve(NumberUtil.doubleVal(inventory.getInventoryAveragePrice()) * productLocalInfoDTO.getRate(), NumberUtil.PRECISION);
        } else {
          purchasePriceWithUnit = NumberUtil.toReserve(inventory.getInventoryAveragePrice(), NumberUtil.PRECISION);
        }
      }
      inStorageRecordDTO.setPrice(purchasePriceWithUnit);
    }
  }

    /**
   * 对供应商库存进行排序
   * @param supplierInventoryList
   */
  public List<SupplierInventory> sortSupplierInventoryList(List<SupplierInventory> supplierInventoryList) {
    if (CollectionUtils.isEmpty(supplierInventoryList)) {
      return supplierInventoryList;
    }

    List<SupplierInventory> returnList = new ArrayList<SupplierInventory>();


    List<SupplierInventory> supplierIdNullList = new ArrayList<SupplierInventory>();

    List<SupplierInventory> undefinedList = new ArrayList<SupplierInventory>();

    List<SupplierInventory> importList = new ArrayList<SupplierInventory>();

    List<SupplierInventory> normalList = new ArrayList<SupplierInventory>();

    for (SupplierInventory supplierInventory : supplierInventoryList) {
      if (supplierInventory.getSupplierId() == null) {
        supplierIdNullList.add(supplierInventory);
      } else if (supplierInventory.getSupplierType() == OutStorageSupplierType.UNDEFINED_SUPPLIER) {
        undefinedList.add(supplierInventory);
      } else if (supplierInventory.getSupplierType() == OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER) {
        importList.add(supplierInventory);
      } else {
        normalList.add(supplierInventory);
      }
    }

    if (CollectionUtils.isNotEmpty(supplierIdNullList)) {
      returnList.addAll(supplierIdNullList);
    }
    if (CollectionUtils.isNotEmpty(undefinedList)) {
      returnList.addAll(undefinedList);
    }
    if (CollectionUtils.isNotEmpty(importList)) {
      returnList.addAll(importList);
    }
    if (CollectionUtils.isNotEmpty(normalList)) {
      returnList.addAll(normalList);
    }
    return returnList;
  }

  @Override
  public void handelRepairPickingInStorageService(RepairPickingDTO repairPickingDTO, List<RepairPickingItemDTO> repairPickingItemDTOs, TxnWriter writer) {
    if(repairPickingDTO == null || CollectionUtils.isEmpty(repairPickingItemDTOs)){
      return;
    }

    //1.找到相同产品之前的出库记录
    Long repairPickingId = repairPickingDTO.getId();
    Long shopId = repairPickingDTO.getShopId();
    Set<Long> productIds = new HashSet<Long>();
    for (RepairPickingItemDTO repairPickingItemDTO : repairPickingItemDTOs) {
      if (repairPickingItemDTO.getProductId() != null) {
        productIds.add(repairPickingItemDTO.getProductId());
      }
    }
    List<OutStorageRelation> outStorageRelations = writer.getOutStorageRelationByOrderAndProductIds(shopId,repairPickingId,productIds);
    List<InStorageRecordDTO> inStorageRecordDTOs = new ArrayList<InStorageRecordDTO>();
    Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(shopId,productIds.toArray(new Long[productIds.size()]));


    //2.根据出库记录的供应商信息，价格信息,RepairPickingItemDTO信息生成相应的入库记录

    //3.将这些 入库记录归类供应商库存变化记录

    //4，更新供应商库存



  }

}
