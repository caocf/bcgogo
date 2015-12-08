package com.bcgogo.txn.service.productThrough;

import com.bcgogo.enums.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 库存减少的单据 商品-库存打通 专用service
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-2
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductOutStorageService implements IProductOutStorageService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductOutStorageService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;
  private IProductThroughService productThroughService;
  private IProductService productService;
  private IProductInStorageService productInStorageService;

  public IProductInStorageService getProductInStorageService() {
    return productInStorageService == null ? ServiceManager.getService(IProductInStorageService.class) : productInStorageService;
  }

  public IProductThroughService getProductThroughService() {
    return productThroughService == null ? ServiceManager.getService(IProductThroughService.class) : productThroughService;
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
      productThroughByOrder(bcgogoOrderDto, orderType, orderStatus,txnWriter,null);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  /**
   * 销售退货单作废入库打通
   * @param salesReturnDTO
   */
  public void productThroughForSalesReturnRepeal(SalesReturnDTO salesReturnDTO) {
    try {
      if (salesReturnDTO == null || ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())) {
        return;
      }
      Map<Long, List<InStorageRecordDTO>> inStorageRecordDTOMap = getProductThroughService().getInStorageRecordDTOMapByOrderIds(salesReturnDTO.getShopId(), salesReturnDTO.getId());
      if (MapUtils.isEmpty(inStorageRecordDTOMap)) {
        LOG.error("inStorageRecordDTOMap is empty:" + salesReturnDTO.getId());
        return;
      }

      for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {
        List<InStorageRecordDTO> inStorageRecordDTOList = inStorageRecordDTOMap.get(salesReturnItemDTO.getId());
        if (CollectionUtils.isEmpty(inStorageRecordDTOList)) {
          LOG.error("inStorageRecordDTOList is Empty:" + salesReturnItemDTO.getId());

          List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();
          OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
          outStorageRelationDTO.setUseRelatedAmount(salesReturnItemDTO.getAmount());
          outStorageRelationDTO.setSupplierRelatedAmount(salesReturnItemDTO.getAmount());
          outStorageRelationDTO.setRelatedSupplierId(null);
          outStorageRelationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
          outStorageRelationDTO.setRelatedSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
          outStorageRelationDTOList.add(outStorageRelationDTO);
          salesReturnItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
          continue;
        }

        List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();
        for (InStorageRecordDTO inStorageRecordDTO : inStorageRecordDTOList) {
          OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
          outStorageRelationDTO.setUseRelatedAmount(inStorageRecordDTO.getSupplierRelatedAmount());
          outStorageRelationDTO.setSupplierRelatedAmount(inStorageRecordDTO.getSupplierRelatedAmount());
          outStorageRelationDTO.setRelatedSupplierId(inStorageRecordDTO.getSupplierId());
          outStorageRelationDTO.setRelatedSupplierName(inStorageRecordDTO.getSupplierName());
          outStorageRelationDTO.setSupplierType(inStorageRecordDTO.getSupplierType());
          outStorageRelationDTOList.add(outStorageRelationDTO);
        }
        salesReturnItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
      }
      salesReturnDTO.setSelectSupplier(true);

      this.productThroughByOrder(salesReturnDTO,OrderTypes.SALE_RETURN,salesReturnDTO.getStatus());

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }


  /**
   * 库存增加的单据 商品-库存打通逻辑
   *
   * @param bcgogoOrderDto 单据状态
   * @param orderType      单据类型
   * @param orderStatus    单据状态
   *                       逻辑：
   *                       1.获得每个商品的出库记录(根据是否选择供应商区分) 如果没有选择供应商 系统默认选择
   *                       2.更新每个商品的出库记录 InstorageRelation
   *                       3.保存商品出入库关系表 OutStorageRelation
   *                       3.更新每个商品对应的supplier_inventory
   */
  public void productThroughByOrder(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, OrderStatus orderStatus, TxnWriter txnWriter,
                                    Map<Long,Inventory> inventoryMap) {
    try {

      LOG.debug("单据出库开始打通:");
      if (bcgogoOrderDto == null || orderType == null || ArrayUtil.isEmpty(bcgogoOrderDto.getItemDTOs())) {
        LOG.error("ProductOutStorageService.productThroughByOrder,OrderTypes:" + orderType + ",orderStatus:" + orderStatus);
        return;
      }

      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);

      Set<Long> productIdSet = new HashSet<Long>();
      Set<Long> supplierIdSet = new HashSet<Long>();

      Set<OutStorageSupplierType> supplierTypeSet = new HashSet<OutStorageSupplierType>();
      boolean supplierIdEmpty = false;

      boolean selectSupplier = bcgogoOrderDto.isSelectSupplier();

      if (orderType == OrderTypes.REPAIR) {
        selectSupplier = false;
      }

      for (BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()) {
        if (bcgogoOrderItemDto.getProductId() == null) {
          continue;
        }

        productIdSet.add(bcgogoOrderItemDto.getProductId());
        if (!selectSupplier) {
          supplierIdEmpty = true;
          continue;
        }
        if (!ArrayUtil.isEmpty(bcgogoOrderItemDto.getOutStorageRelationDTOs())) {
          for (OutStorageRelationDTO outStorageRelationDTO : bcgogoOrderItemDto.getOutStorageRelationDTOs()) {

            if (NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()) <= 0) {
              continue;
            }

            if (outStorageRelationDTO.getSupplierType() != null) {
              supplierTypeSet.add(outStorageRelationDTO.getSupplierType());
            }

            if (outStorageRelationDTO.getRelatedSupplierId() != null) {
              supplierIdSet.add(outStorageRelationDTO.getRelatedSupplierId());
            } else {
              supplierIdEmpty = true;
            }
          }
        }
      }

      if (CollectionUtils.isEmpty(productIdSet)) {
        LOG.error("ProductOutStorageService.productThroughByOrder,productIdSet empty," +
            "OrderTypes:" + orderType + ",orderStatus:" + orderStatus + ",bcgogoOrderDto:" + JsonUtil.objectToJson(bcgogoOrderDto));
        return;
      }

      // inventoryMap从前面业务传过来，如果没有或者缺少数据再补充
      if(MapUtils.isEmpty(inventoryMap)){
        inventoryMap = inventoryService.getInventoryMap(bcgogoOrderDto.getShopId(), productIdSet);
      }else{
        if(CollectionUtils.isNotEmpty(productIdSet)){
          Set<Long> newProductIds = new HashSet<Long>();
          for(Long productId :productIdSet){
            if(inventoryMap.get(productId) == null){
              newProductIds.add(productId);
            }
          }
          if(CollectionUtils.isNotEmpty(newProductIds)){
            Map<Long,Inventory> addInventoryMap = inventoryService.getInventoryMap(bcgogoOrderDto.getShopId(),newProductIds);
            inventoryMap.putAll(addInventoryMap);
          }
        }
      }



      Map<Long, List<InStorageRecordDTO>> productInStorageMap = getProductThroughService().getInStorageRecordMap(bcgogoOrderDto.getShopId(), bcgogoOrderDto.getStorehouseId(), productIdSet, supplierIdSet,supplierTypeSet,supplierIdEmpty);
      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(bcgogoOrderDto.getShopId(), productIdSet.toArray(new Long[productIdSet.size()]));
      List<SupplierInventory> productSupplierInventory = txnWriter.getHasRemainSupplierInventory(bcgogoOrderDto.getShopId(),null,bcgogoOrderDto.getStorehouseId(),productIdSet);

      List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();
      List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();

      for (BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()) {

        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId());
        Double costPrice = 0D;
        if (!selectSupplier || ArrayUtil.isEmpty(bcgogoOrderItemDto.getOutStorageRelationDTOs())) {
          setOutStorageRelation(bcgogoOrderItemDto, productSupplierInventory, productLocalInfoDTO);
        }

        List<InStorageRecordDTO> storageRecordDTOList = productInStorageMap.get(bcgogoOrderItemDto.getProductId());

        //其他异常没有入库记录或者老数据
        if (CollectionUtils.isEmpty(storageRecordDTOList)) {
          outStorageRelationDTOList.addAll(getOutStorageRelation(bcgogoOrderDto, orderType, bcgogoOrderItemDto));

          Inventory inventory = inventoryMap.get(bcgogoOrderItemDto.getProductId());
          if (inventory != null) {
            if (UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productLocalInfoDTO)) {
              costPrice = NumberUtil.toReserve(NumberUtil.doubleVal(inventory.getInventoryAveragePrice()) * productLocalInfoDTO.getRate(), NumberUtil.PRECISION);
            } else {
              costPrice = NumberUtil.toReserve(inventory.getInventoryAveragePrice(), NumberUtil.PRECISION);
            }
            bcgogoOrderItemDto.setItemCostPrice(NumberUtil.doubleVal(costPrice));
            bcgogoOrderItemDto.setItemTotalCostPrice(NumberUtil.toReserve(bcgogoOrderItemDto.getItemCostPrice() * bcgogoOrderItemDto.getAmount(), NumberUtil.PRECISION));
          }
          continue;
        }

        List<InStorageRecordDTO> sortResultList = getProductInStorageService().sortInStorageRecordList(storageRecordDTOList);

        List<OutStorageRelationDTO> resultOutStorageRelationList =getOutStorageRelation(bcgogoOrderDto, orderType, bcgogoOrderItemDto, sortResultList,productLocalInfoDTO);
        outStorageRelationDTOList.addAll(resultOutStorageRelationList);
        if (CollectionUtils.isNotEmpty(sortResultList)) {

          for (InStorageRecordDTO inStorageRecordDTO : sortResultList) {
            if (inStorageRecordDTO.getPrice() == null) {
              inStorageRecordDTO.setPrice(0D);
              LOG.error("ProductOutStorageService.productThroughByOrder price empty," + inStorageRecordDTO.toString());
            }
            if (NumberUtil.doubleVal(inStorageRecordDTO.getUseRelatedAmount()) <= 0) {
              continue;
            }
            inStorageRecordDTOList.add(inStorageRecordDTO);
            costPrice += inStorageRecordDTO.getUseRelatedAmount() * NumberUtil.doubleVal(inStorageRecordDTO.getPrice());
          }
        }

        if (selectSupplier) {
          bcgogoOrderItemDto.setItemCostPrice(NumberUtil.toReserve(costPrice / bcgogoOrderItemDto.getAmount(), NumberUtil.PRECISION));
          bcgogoOrderItemDto.setItemTotalCostPrice(costPrice);
        }
      }
      getProductInStorageService().saveOrUpdateInStorageRecordDTO(inStorageRecordDTOList, txnWriter);

      this.saveOutStorageRelation(outStorageRelationDTOList, txnWriter);

      Map<Long, List<SupplierInventoryDTO>> map = getSupplierInventoryList(outStorageRelationDTOList);

      List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
      for (List list : map.values()) {
        supplierInventoryDTOList.addAll(list);
      }
      getProductThroughService().saveOrUpdateSupplierInventory(txnWriter, supplierInventoryDTOList);

      //更新item成本和单据的总成本
      if (selectSupplier) {
        updateOrderAndItemCostPrice(bcgogoOrderDto, orderType, txnWriter);
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("ProductInStorageService.productThroughByOrder,bcgogoOrderDto:" + JsonUtil.objectToJson(bcgogoOrderDto));
      LOG.error("ProductInStorageService.productThroughByOrder,OrderTypes:" + orderType + ",orderStatus:" + orderType);
    }
  }


  /**
   * 商品-出入库打通之后更新单据和item的成本
   *
   * @param bcgogoOrderDto
   * @param orderType
   */
  public void updateOrderAndItemCostPrice(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, TxnWriter txnWriter) {

    if (!bcgogoOrderDto.isSelectSupplier()) {
      return;
    }

    Double orderTotalCostPrice = 0D;

    if (orderType == OrderTypes.SALE) {
      SalesOrder salesOrder = txnWriter.getById(SalesOrder.class, bcgogoOrderDto.getId());
      for (BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()) {
        SalesOrderItem salesOrderItem = txnWriter.getById(SalesOrderItem.class, bcgogoOrderItemDto.getId());
        salesOrderItem.setCostPrice(NumberUtil.toReserve(bcgogoOrderItemDto.getItemCostPrice(), NumberUtil.PRECISION));
        salesOrderItem.setTotalCostPrice(NumberUtil.toReserve(bcgogoOrderItemDto.getItemTotalCostPrice(), NumberUtil.PRECISION));
        txnWriter.update(salesOrderItem);
        orderTotalCostPrice += salesOrderItem.getTotalCostPrice();
      }
      salesOrder.setTotalCostPrice(NumberUtil.toReserve(orderTotalCostPrice + NumberUtil.doubleVal(salesOrder.getOtherTotalCostPrice()), NumberUtil.PRECISION));

      SalesOrderDTO salesOrderDTO = (SalesOrderDTO)bcgogoOrderDto;
      salesOrderDTO.setTotalCostPrice(salesOrder.getTotalCostPrice());

      txnWriter.update(salesOrder);
    }
  }


  /**
   * 商品导入 或者商品库存增加记录为空的处理逻辑
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param bcgogoOrderItemDto
   * @return
   */
  public List<OutStorageRelationDTO> getOutStorageRelation(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, BcgogoOrderItemDto bcgogoOrderItemDto) {
    List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();

    for (OutStorageRelationDTO relationDTO : bcgogoOrderItemDto.getOutStorageRelationDTOs()) {
      OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
      outStorageRelationDTO.setShopId(bcgogoOrderDto.getShopId());
      outStorageRelationDTO.setOutStorageOrderId(bcgogoOrderDto.getId());
      outStorageRelationDTO.setOutStorageOrderType(orderType);
      outStorageRelationDTO.setRelatedStoreHouseId(bcgogoOrderDto.getStorehouseId());
      outStorageRelationDTO.setDisabled(YesNo.NO);
      outStorageRelationDTO.setOutStorageItemId(bcgogoOrderItemDto.getId());
      outStorageRelationDTO.setProductId(bcgogoOrderItemDto.getProductId());
      outStorageRelationDTO.setOutStorageItemAmount(bcgogoOrderItemDto.getAmount());
      outStorageRelationDTO.setOutStorageUnit(bcgogoOrderItemDto.getUnit());
      outStorageRelationDTO.setRelationTime(System.currentTimeMillis());
      outStorageRelationDTO.setRelatedSupplierName(relationDTO.getRelatedSupplierName());
      outStorageRelationDTO.setRelatedUnit(relationDTO.getRelatedUnit());

      if (bcgogoOrderDto.isSelectSupplier()) {
        outStorageRelationDTO.setOutStorageType(OutStorageType.SELF_ASSIGN);
      } else {
        outStorageRelationDTO.setOutStorageType(OutStorageType.SYSTEM_ASSIGN);
      }

      if (relationDTO.getSupplierType() != null) {
        outStorageRelationDTO.setSupplierType(relationDTO.getSupplierType());
      } else if (OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER.getName().equals(relationDTO.getRelatedSupplierName())) {
        outStorageRelationDTO.setSupplierType(OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER);
      }else{
        outStorageRelationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      }
      outStorageRelationDTO.setUseRelatedAmount(outStorageRelationDTO.getOutStorageItemAmount());
      outStorageRelationDTO.setRelatedOrderType(null);
      outStorageRelationDTO.setRelatedOrderId(null);
      outStorageRelationDTO.setRelatedItemId(null);
      outStorageRelationDTO.setRelatedSupplierId(null);
      outStorageRelationDTO.setSupplierRelatedAmount(outStorageRelationDTO.getUseRelatedAmount());
      outStorageRelationDTOList.add(outStorageRelationDTO);
    }
    return outStorageRelationDTOList;
  }


  public List<OutStorageRelationDTO> getOutStorageRelation(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType,
                                                           BcgogoOrderItemDto bcgogoOrderItemDto, List<InStorageRecordDTO> inStorageRecordDTOList, ProductLocalInfoDTO productLocalInfoDTO) {
    List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();


    Map<Long, List<InStorageRecordDTO>> supplierInStorageRecordMap = new HashMap<Long, List<InStorageRecordDTO>>();      //key 是supplierId
    List<InStorageRecordDTO> supplierIdEmptyList = new ArrayList<InStorageRecordDTO>();
    for (InStorageRecordDTO recordDTO : inStorageRecordDTOList) {
      if (recordDTO.getSupplierId() == null) {
        supplierIdEmptyList.add(recordDTO);
        continue;
      }
      List<InStorageRecordDTO> recordDTOs = supplierInStorageRecordMap.get(recordDTO.getSupplierId());
      if (CollectionUtils.isEmpty(recordDTOs)) {
        recordDTOs = new ArrayList<InStorageRecordDTO>();
      }
      recordDTOs.add(recordDTO);
      supplierInStorageRecordMap.put(recordDTO.getSupplierId(), recordDTOs);
    }


    for (OutStorageRelationDTO relationDTO : bcgogoOrderItemDto.getOutStorageRelationDTOs()) {
      // itemAmount 表示出库单上某个供应商需要出库的数量
      Double itemAmount = NumberUtil.doubleVal(relationDTO.getUseRelatedAmount());
      if(itemAmount <= 0){
        continue;
      }

      List<InStorageRecordDTO> inStorageRecordDTOs = null;
      if (relationDTO.getRelatedSupplierId() != null) {
        inStorageRecordDTOs = supplierInStorageRecordMap.get(relationDTO.getRelatedSupplierId());
      }else{
        inStorageRecordDTOs = supplierIdEmptyList;
      }

      if (CollectionUtils.isEmpty(inStorageRecordDTOs)) {
        OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
        outStorageRelationDTO.setShopId(bcgogoOrderDto.getShopId());
        outStorageRelationDTO.setOutStorageOrderId(bcgogoOrderDto.getId());
        outStorageRelationDTO.setOutStorageOrderType(orderType);
        outStorageRelationDTO.setRelatedStoreHouseId(bcgogoOrderDto.getStorehouseId());
        outStorageRelationDTO.setDisabled(YesNo.NO);
        outStorageRelationDTO.setOutStorageItemId(bcgogoOrderItemDto.getId());
        outStorageRelationDTO.setProductId(bcgogoOrderItemDto.getProductId());
        outStorageRelationDTO.setOutStorageItemAmount(bcgogoOrderItemDto.getAmount());
        outStorageRelationDTO.setOutStorageUnit(bcgogoOrderItemDto.getUnit());
        outStorageRelationDTO.setRelationTime(System.currentTimeMillis());
        outStorageRelationDTO.setRelatedSupplierName(relationDTO.getRelatedSupplierName());

        if (bcgogoOrderDto.isSelectSupplier()) {
          outStorageRelationDTO.setOutStorageType(OutStorageType.SELF_ASSIGN);
        } else {
          outStorageRelationDTO.setOutStorageType(OutStorageType.SYSTEM_ASSIGN);
        }

        if (relationDTO.getSupplierType() != null) {
          outStorageRelationDTO.setSupplierType(relationDTO.getSupplierType());
        } else if (OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER.getName().equals(relationDTO.getRelatedSupplierName())) {
          outStorageRelationDTO.setSupplierType(OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER);
        }else{
          outStorageRelationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        }
        outStorageRelationDTO.setUseRelatedAmount(relationDTO.getUseRelatedAmount());
        outStorageRelationDTO.setSupplierRelatedAmount(relationDTO.getUseRelatedAmount());
        outStorageRelationDTOList.add(outStorageRelationDTO);
        continue;
      }

      inStorageRecordDTOs = getProductInStorageService().sortInStorageRecordList(inStorageRecordDTOs);
      for (InStorageRecordDTO inStorageRecordDTO : inStorageRecordDTOs) {
        if (itemAmount <= 0) {
          break;
        }
        Double useAmount = 0D;      //入库记录被出库占用后的剩余数量
        Double useRelatedAmount = 0D;  //换算成入库item单位的出库需要占用的数量

        OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
        outStorageRelationDTO.setSupplierRelatedAmount(relationDTO.getUseRelatedAmount());
        outStorageRelationDTO.setShopId(bcgogoOrderDto.getShopId());
        outStorageRelationDTO.setOutStorageOrderId(bcgogoOrderDto.getId());
        outStorageRelationDTO.setOutStorageOrderType(orderType);
        outStorageRelationDTO.setRelatedStoreHouseId(bcgogoOrderDto.getStorehouseId());
        outStorageRelationDTO.setDisabled(YesNo.NO);
        outStorageRelationDTO.setOutStorageItemId(bcgogoOrderItemDto.getId());
        outStorageRelationDTO.setProductId(bcgogoOrderItemDto.getProductId());
        outStorageRelationDTO.setOutStorageItemAmount(bcgogoOrderItemDto.getAmount());
        outStorageRelationDTO.setOutStorageUnit(bcgogoOrderItemDto.getUnit());
        outStorageRelationDTO.setRelationTime(System.currentTimeMillis());
        outStorageRelationDTO.setRelatedSupplierName(relationDTO.getRelatedSupplierName());

        if (bcgogoOrderDto.isSelectSupplier()) {
          outStorageRelationDTO.setOutStorageType(OutStorageType.SELF_ASSIGN);
        } else {
          outStorageRelationDTO.setOutStorageType(OutStorageType.SYSTEM_ASSIGN);
        }

        if (relationDTO.getSupplierType() != null) {
          outStorageRelationDTO.setSupplierType(relationDTO.getSupplierType());
        } else if (OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER.getName().equals(relationDTO.getRelatedSupplierName())) {
          outStorageRelationDTO.setSupplierType(OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER);
        }else{
          outStorageRelationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        }

        outStorageRelationDTO.setRelatedUnit(inStorageRecordDTO.getInStorageUnit());
        outStorageRelationDTO.setRelatedStoreHouseId(inStorageRecordDTO.getStorehouseId());
        outStorageRelationDTO.setRelatedOrderType(inStorageRecordDTO.getInStorageOrderType());
        outStorageRelationDTO.setRelatedOrderId(inStorageRecordDTO.getInStorageOrderId());
        outStorageRelationDTO.setRelatedItemId(inStorageRecordDTO.getInStorageItemId());
        outStorageRelationDTO.setRelatedSupplierId(inStorageRecordDTO.getSupplierId());

        if (!UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productLocalInfoDTO)) {
           //出库单据上是小单位，入库单据上是大单位
          if (UnitUtil.isStorageUnit(inStorageRecordDTO.getInStorageUnit(), productLocalInfoDTO)) {
            Double remainAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) * productLocalInfoDTO.getRate();  //小单位的入库记录剩余库存

            if (remainAmount >= itemAmount) {
              useRelatedAmount = itemAmount / productLocalInfoDTO.getRate();  //换算成入库item单位的出库需要占用的数量
              useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) - useRelatedAmount;
              inStorageRecordDTO.setRemainAmount(useAmount);
              itemAmount = 0D;
            } else {
              useRelatedAmount = inStorageRecordDTO.getRemainAmount();

              useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount());
              inStorageRecordDTO.setRemainAmount(0D);
              itemAmount = itemAmount - useRelatedAmount * productLocalInfoDTO.getRate();
            }
          } else {
              //出库单据上是小单位，入库单据上也是小单位
            Double remainAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount());
            if (remainAmount >= itemAmount) {
              useRelatedAmount = itemAmount;
              useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) - useRelatedAmount;
              inStorageRecordDTO.setRemainAmount(NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) - useRelatedAmount);
              itemAmount = 0D;
            } else {
              useRelatedAmount = inStorageRecordDTO.getRemainAmount();
              useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount());
              inStorageRecordDTO.setRemainAmount(0D);
              itemAmount = itemAmount - useRelatedAmount;
            }
          }
          outStorageRelationDTO.setUseRelatedAmount(useRelatedAmount);
          inStorageRecordDTO.setUseRelatedAmount(useRelatedAmount);
          outStorageRelationDTOList.add(outStorageRelationDTO);
          continue;
        }

        if (!UnitUtil.isStorageUnit(inStorageRecordDTO.getInStorageUnit(), productLocalInfoDTO)) {
          Double remainAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) / productLocalInfoDTO.getRate();

          if (remainAmount >= itemAmount) {
            useRelatedAmount = itemAmount * productLocalInfoDTO.getRate();
            useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) - useRelatedAmount;
            inStorageRecordDTO.setRemainAmount(NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) - useRelatedAmount);
            itemAmount = 0D;
          } else {
            useRelatedAmount = inStorageRecordDTO.getRemainAmount();
            useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount());

            inStorageRecordDTO.setRemainAmount(0D);
            itemAmount = itemAmount - useRelatedAmount / productLocalInfoDTO.getRate();
          }
        } else {
          Double remainAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount());
          if (remainAmount >= itemAmount) {
            useRelatedAmount = itemAmount;
            useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) - useRelatedAmount;
            inStorageRecordDTO.setRemainAmount(NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount()) - useRelatedAmount);
            itemAmount = 0D;
          } else {
            useRelatedAmount = inStorageRecordDTO.getRemainAmount();
            useAmount = NumberUtil.doubleVal(inStorageRecordDTO.getRemainAmount());
            inStorageRecordDTO.setRemainAmount(0D);
            itemAmount = itemAmount - useRelatedAmount;
          }
        }

        inStorageRecordDTO.setUseRelatedAmount(useRelatedAmount);
        outStorageRelationDTO.setUseRelatedAmount(useRelatedAmount);
        outStorageRelationDTOList.add(outStorageRelationDTO);
        continue;
      }
    }

    return outStorageRelationDTOList;
  }


  /**
   * 如果没有选择供应商 设置选择的供应商
   *
   * @param bcgogoOrderItemDto
   * @param supplierInventoryList
   * @param productLocalInfoDTO
   */
  public void setOutStorageRelation(BcgogoOrderItemDto bcgogoOrderItemDto, List<SupplierInventory> supplierInventoryList, ProductLocalInfoDTO productLocalInfoDTO) {

    List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();

    if (CollectionUtil.isEmpty(supplierInventoryList)) {
      LOG.info("ProductOutStorageService.setOutStorageRelation,supplierInventoryList empty");
      LOG.info("productId:" + bcgogoOrderItemDto.getProductId());
      OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
      outStorageRelationDTO.setUseRelatedAmount(bcgogoOrderItemDto.getAmount());
      outStorageRelationDTO.setSupplierRelatedAmount(bcgogoOrderItemDto.getAmount());
      outStorageRelationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
      outStorageRelationDTO.setRelatedSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
      outStorageRelationDTO.setRelatedUnit(bcgogoOrderItemDto.getUnit());
      outStorageRelationDTOList.add(outStorageRelationDTO);
      bcgogoOrderItemDto.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
      return;
    }

    List<SupplierInventory> resultList = getProductInStorageService().sortSupplierInventoryList(supplierInventoryList);

    Double itemAmount = NumberUtil.doubleVal(bcgogoOrderItemDto.getAmount());

    for (SupplierInventory supplierInventory : resultList) {
      if(bcgogoOrderItemDto.getProductId() != null && !bcgogoOrderItemDto.getProductId().equals(supplierInventory.getProductId())){
        continue;
      }

      if(supplierInventory.getProductId() == null){
        continue;
      }
      if(!supplierInventory.getProductId().equals(bcgogoOrderItemDto.getProductId())){
        continue;
      }

      if (itemAmount <= 0) {
        break;
      }

      if (NumberUtil.doubleVal(supplierInventory.getRemainAmount()) <= 0) {
        continue;
      }
      double remainAmount = NumberUtil.toReserve(supplierInventory.getRemainAmount(), NumberUtil.PRECISION);

      OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();
      outStorageRelationDTO.setRelatedSupplierId(supplierInventory.getSupplierId());
      outStorageRelationDTO.setSupplierType(supplierInventory.getSupplierType());
      outStorageRelationDTO.setRelatedSupplierName(supplierInventory.getSupplierName());
      outStorageRelationDTO.setRelatedStoreHouseId(supplierInventory.getStorehouseId());
      outStorageRelationDTO.setRelatedUnit(bcgogoOrderItemDto.getUnit());

      if (UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productLocalInfoDTO)) {
        if (!UnitUtil.isStorageUnit(supplierInventory.getUnit(), productLocalInfoDTO)) {
          remainAmount = NumberUtil.toReserve(remainAmount / productLocalInfoDTO.getRate(), NumberUtil.PRECISION);
        }
      }

      if (remainAmount >= itemAmount) {
        outStorageRelationDTO.setUseRelatedAmount(itemAmount);
        itemAmount = 0D;
      } else {
        outStorageRelationDTO.setUseRelatedAmount(remainAmount);
        itemAmount = itemAmount - remainAmount;
      }

      outStorageRelationDTO.setSupplierRelatedAmount(outStorageRelationDTO.getUseRelatedAmount());
      outStorageRelationDTOList.add(outStorageRelationDTO);
    }

    bcgogoOrderItemDto.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));

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
      } else if (orderType == OrderTypes.SALE || orderType == OrderTypes.REPAIR || orderType == OrderTypes.SALE_RETURN) {
        inStorageRecordDTOList.addAll(getInStorageRecordByRepeal(bcgogoOrderDto, bcgogoOrderItemDto, orderStatus, orderType));
      } else if (orderType == OrderTypes.INNER_RETURN || orderType == OrderTypes.ALLOCATE_RECORD || orderType == OrderTypes.RETURN_ORDER
          || orderType == OrderTypes.INVENTORY_CHECK || orderType == OrderTypes.REPAIR_PICKING) {
        inStorageRecordDTOList.addAll(getInStorageRecord(bcgogoOrderDto, bcgogoOrderItemDto, orderStatus, orderType));
      }
    }
    return inStorageRecordDTOList;
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

    InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();

    inStorageRecordDTO.setShopId(purchaseInventoryDTO.getShopId());
    inStorageRecordDTO.setInStorageOrderId(purchaseInventoryDTO.getId());
    inStorageRecordDTO.setInStorageOrderType(OrderTypes.INVENTORY);
    inStorageRecordDTO.setInStorageOrderStatus(orderStatus);


    inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
    inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
    inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(bcgogoOrderItemDto.getAmount()));
    inStorageRecordDTO.setInStorageUnit(bcgogoOrderItemDto.getUnit());
    inStorageRecordDTO.setRemainAmount(bcgogoOrderItemDto.getRemainAmount());

    inStorageRecordDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
    inStorageRecordDTO.setSupplierName(purchaseInventoryDTO.getSupplier());

    inStorageRecordDTO.setStorehouseId(purchaseInventoryDTO.getStorehouseId());
    inStorageRecordDTO.setStorehouseName(purchaseInventoryDTO.getStorehouseName());

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
    List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();
    List<OutStorageRelationDTO> outStorageRelationDTOList = getProductThroughService().getOutStorageRelation(bcgogoOrderDto.getShopId(), bcgogoOrderDto.getId(), orderType,
        bcgogoOrderItemDto.getId(), bcgogoOrderItemDto.getProductId());

    if (CollectionUtils.isEmpty(outStorageRelationDTOList)) {
      LOG.error("orderId outStorageRelation is null: " + bcgogoOrderDto.getId());
      return inStorageRecordDTOList;
    }

    for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOList) {
      InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();

      inStorageRecordDTO.setShopId(bcgogoOrderDto.getShopId());
      inStorageRecordDTO.setInStorageOrderId(bcgogoOrderDto.getId());

      inStorageRecordDTO.setInStorageOrderType(orderType);
      inStorageRecordDTO.setInStorageOrderStatus(orderStatus);

      inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
      inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
      inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()));
      inStorageRecordDTO.setInStorageUnit(outStorageRelationDTO.getOutStorageUnit());
      inStorageRecordDTO.setRemainAmount(inStorageRecordDTO.getInStorageItemAmount());
      inStorageRecordDTO.setSupplierId(outStorageRelationDTO.getRelatedSupplierId());
      inStorageRecordDTO.setSupplierName(outStorageRelationDTO.getRelatedSupplierName());
      inStorageRecordDTO.setStorehouseId(bcgogoOrderDto.getStorehouseId());
      inStorageRecordDTO.setStorehouseName(bcgogoOrderDto.getStorehouseName());
      inStorageRecordDTOList.add(inStorageRecordDTO);
    }

    return inStorageRecordDTOList;
  }

  /**
   * 根据单据内容 item内容 和单据类型 保存库存增加记录 内部退料单，借调单，维修退料，库存盘点，仓库调拨单
   *
   * @param bcgogoOrderDto
   * @param bcgogoOrderItemDto
   * @return
   */
  public List<InStorageRecordDTO> getInStorageRecord(BcgogoOrderDto bcgogoOrderDto, BcgogoOrderItemDto bcgogoOrderItemDto, OrderStatus orderStatus, OrderTypes orderType) {
    List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();

    InnerReturnDTO innerReturnDTO = (InnerReturnDTO) bcgogoOrderDto;

    boolean selectSupplier = bcgogoOrderDto.isSelectSupplier();
    if (!selectSupplier) {
      InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();

      inStorageRecordDTO.setShopId(innerReturnDTO.getShopId());
      inStorageRecordDTO.setInStorageOrderId(innerReturnDTO.getId());
      inStorageRecordDTO.setInStorageOrderType(orderType);
      inStorageRecordDTO.setInStorageOrderStatus(orderStatus);

      inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
      inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
      inStorageRecordDTO.setInStorageUnit(bcgogoOrderItemDto.getUnit());

      inStorageRecordDTO.setStorehouseId(innerReturnDTO.getStorehouseId());
      inStorageRecordDTO.setStorehouseName(innerReturnDTO.getStorehouseName());
      inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(bcgogoOrderItemDto.getAmount()));
      inStorageRecordDTO.setRemainAmount(bcgogoOrderItemDto.getRemainAmount());

      inStorageRecordDTO.setSupplierId(null);
      inStorageRecordDTO.setSupplierName(null);
      inStorageRecordDTOList.add(inStorageRecordDTO);

      return inStorageRecordDTOList;
    }

    for (OutStorageRelationDTO outStorageRelationDTO : bcgogoOrderItemDto.getOutStorageRelationDTOs()) {
      InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();

      inStorageRecordDTO.setShopId(innerReturnDTO.getShopId());
      inStorageRecordDTO.setInStorageOrderId(innerReturnDTO.getId());
      inStorageRecordDTO.setInStorageOrderType(orderType);
      inStorageRecordDTO.setInStorageOrderStatus(orderStatus);

      inStorageRecordDTO.setInStorageItemId(bcgogoOrderItemDto.getId());
      inStorageRecordDTO.setProductId(bcgogoOrderItemDto.getProductId());
      inStorageRecordDTO.setInStorageUnit(bcgogoOrderItemDto.getUnit());

      inStorageRecordDTO.setStorehouseId(innerReturnDTO.getStorehouseId());
      inStorageRecordDTO.setStorehouseName(innerReturnDTO.getStorehouseName());
      inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(outStorageRelationDTO.getUseRelatedAmount()));
      inStorageRecordDTO.setRemainAmount(inStorageRecordDTO.getInStorageItemAmount());
      inStorageRecordDTOList.add(inStorageRecordDTO);
    }
    return inStorageRecordDTOList;
  }

  /**
   * 保存supplier_inventory 根据库存减少关系记录
   *
   * @param outStorageRelationDTOList
   * @return
   */
  public Map<Long, List<SupplierInventoryDTO>> getSupplierInventoryList(List<OutStorageRelationDTO> outStorageRelationDTOList) {

    Map<Long, List<SupplierInventoryDTO>> map = new HashMap<Long, List<SupplierInventoryDTO>>();
    if (CollectionUtil.isEmpty(outStorageRelationDTOList)) {
      return map;
    }
    List<SupplierInventoryDTO> supplierInventoryDTOList = null;

    for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOList) {

      SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
      supplierInventoryDTO.setShopId(outStorageRelationDTO.getShopId());
      supplierInventoryDTO.setProductId(outStorageRelationDTO.getProductId());
      supplierInventoryDTO.setStorehouseId(outStorageRelationDTO.getRelatedStoreHouseId());
      supplierInventoryDTO.setChangeAmount(-outStorageRelationDTO.getUseRelatedAmount());
      supplierInventoryDTO.setUnit(outStorageRelationDTO.getRelatedUnit());

      supplierInventoryDTO.setSupplierType(outStorageRelationDTO.getSupplierType());
      supplierInventoryDTO.setSupplierId(outStorageRelationDTO.getRelatedSupplierId());
      supplierInventoryDTO.setSupplierName(outStorageRelationDTO.getRelatedSupplierName());

      if (map.containsKey(outStorageRelationDTO.getProductId())) {
        supplierInventoryDTOList = map.get(outStorageRelationDTO.getProductId());
      } else {
        supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
      }
      supplierInventoryDTOList.add(supplierInventoryDTO);

      map.put(outStorageRelationDTO.getProductId(), supplierInventoryDTOList);
    }
    return map;

  }


  /**
   * 保存库存减少单据 单据关系记录
   *
   * @param outStorageRelationDTOList
   * @param writer
   */
  public void saveOutStorageRelation(List<OutStorageRelationDTO> outStorageRelationDTOList, TxnWriter writer) {
    if (CollectionUtil.isEmpty(outStorageRelationDTOList)) {
      return;
    }
    for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOList) {
      OutStorageRelation outStorageRelation = new OutStorageRelation();
      outStorageRelation.fromDTO(outStorageRelationDTO);
      writer.save(outStorageRelation);
    }
  }

  /**
   * 入库单作废时判断该供应商的库存是否充足
   *
   * @param purchaseInventoryDTO
   * @return
   */
  public boolean validateBeforeInventoryRepeal(PurchaseInventoryDTO purchaseInventoryDTO) {
    boolean result = false;

    try {
      Set<Long> productIdSet = new HashSet<Long>();
      if(purchaseInventoryDTO != null && !ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())){
        for(PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()){
           if(purchaseInventoryItemDTO.getProductId() != null){
             productIdSet.add(purchaseInventoryItemDTO.getProductId());
           }
        }
      }
      Map<Long,SupplierInventoryDTO> supplierInventoryDTOMap = getProductThroughService().getSupplierInventoryDTOMap(
          purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierId(), purchaseInventoryDTO.getStorehouseId(), productIdSet);
      if (MapUtils.isEmpty(supplierInventoryDTOMap)) {
        return result;
      }

      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(purchaseInventoryDTO.getShopId(), productIdSet.toArray(new Long[productIdSet.size()]));
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        SupplierInventoryDTO supplierInventoryDTO = supplierInventoryDTOMap.get(purchaseInventoryItemDTO.getProductId());
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(purchaseInventoryItemDTO.getProductId());
        String sellUnit =  productLocalInfoDTO == null ? "": productLocalInfoDTO.getSellUnit();
        if (supplierInventoryDTO == null  ) {
          return result;
        }
        double remindAmount = NumberUtil.doubleVal(supplierInventoryDTO.getRemainAmount());
        double itemAmount = NumberUtil.doubleVal(purchaseInventoryItemDTO.getAmount());
        if (remindAmount < 0.0001) {
          return result;
        }
        if (UnitUtil.isStorageUnit(supplierInventoryDTO.getUnit(), productLocalInfoDTO)) {
          remindAmount = remindAmount * productLocalInfoDTO.getRate();
        }
        if(UnitUtil.isStorageUnit(purchaseInventoryItemDTO.getUnit(),productLocalInfoDTO)){
          itemAmount =  itemAmount *  productLocalInfoDTO.getRate();
        }

        if(itemAmount > remindAmount + 0.0001){
          return result;
        } else {
          remindAmount = remindAmount - itemAmount;
          supplierInventoryDTO.setRemainAmount(remindAmount);
          supplierInventoryDTO.setUnit(sellUnit);
        }
      }
    } catch (Exception e) {
      LOG.error("ProductOutStorageService.validateBeforeInventoryRepeal,purchaseInventoryDTO: " + purchaseInventoryDTO.toString());
      LOG.error(e.getMessage(), e);
      return false;
    }
    result = true;
    return result;
  }

  /**
   * 商品出入库打通 入库单作废专用处理
   *
   * @param bcgogoOrderDto
   */
  public Map<OrderTypes, HashSet<Long>> productThroughByInventoryRepeal(BcgogoOrderDto bcgogoOrderDto) {
    PurchaseInventoryDTO purchaseInventoryDTO = null;
    Map<Long, SupplierInventory> supplierInventoryMap = null;
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = null;
    TxnWriter txnWriter = null;
    List<InStorageRecord> inStorageRecordList = null;

    Map<OrderTypes, HashSet<Long>> reIndexMap = new HashMap<OrderTypes, HashSet<Long>>();

    Set<Long> supplierIdSet = null;
    try {
      purchaseInventoryDTO = (PurchaseInventoryDTO) bcgogoOrderDto;

      supplierIdSet = new HashSet<Long>();
      supplierIdSet.add(purchaseInventoryDTO.getSupplierId());

      Set<Long> orderIdSet = new HashSet<Long>();
      orderIdSet.add(purchaseInventoryDTO.getId());
      Set<Long> productIdSet = new HashSet<Long>();

      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        productIdSet.add(purchaseInventoryItemDTO.getProductId());
      }

      supplierInventoryMap = getProductThroughService().getSupplierInventoryMap(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierId(), purchaseInventoryDTO.getStorehouseId(), productIdSet);
      productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(bcgogoOrderDto.getShopId(), productIdSet.toArray(new Long[productIdSet.size()]));

      txnWriter = txnDaoManager.getWriter();

      inStorageRecordList = txnWriter.getInStorageRecordByOrderIds(purchaseInventoryDTO.getShopId(), orderIdSet.toArray(new Long[orderIdSet.size()]));
    } catch (Exception e) {
      LOG.error("ProductOutStorageService.productThroughByInventoryRepeal" + bcgogoOrderDto.toString());
      LOG.error(e.getMessage(), e);
      return reIndexMap;
    }

    Map<Long, InStorageRecord> inStorageRecordMap = new HashMap<Long, InStorageRecord>();

    Object status = txnWriter.begin();
    try {

      if (CollectionUtil.isNotEmpty(inStorageRecordList)) {
        for (InStorageRecord inStorageRecord : inStorageRecordList) {
          inStorageRecord.setInStorageOrderStatus(OrderStatus.PURCHASE_INVENTORY_REPEAL);
          inStorageRecord.setDisabled(YesNo.YES);
          inStorageRecordMap.put(inStorageRecord.getInStorageItemId(), inStorageRecord);
          txnWriter.update(inStorageRecord);
        }
      }


      List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();

      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        InStorageRecord inStorageRecord = inStorageRecordMap.get(purchaseInventoryItemDTO.getId());
        if (inStorageRecord == null) {
          continue;
        }
        if (inStorageRecord.getInStorageItemAmount() == inStorageRecord.getRemainAmount()) {
          continue;
        }

        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(purchaseInventoryItemDTO.getProductId());
        Double supplierInventoryChangeAmount = 0D;
        double currentInventoryPrice = NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice());
        double totalChangeAmount = 0;
        if (UnitUtil.isStorageUnit(purchaseInventoryItemDTO.getUnit(), productLocalInfoDTO)) {
          supplierInventoryChangeAmount = inStorageRecord.getRemainAmount() * productLocalInfoDTO.getRate();
          currentInventoryPrice = currentInventoryPrice / productLocalInfoDTO.getRate();
          totalChangeAmount = purchaseInventoryItemDTO.getAmount() * productLocalInfoDTO.getRate();
        } else {
          supplierInventoryChangeAmount = inStorageRecord.getRemainAmount();
          totalChangeAmount = purchaseInventoryItemDTO.getAmount();
        }

        SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO(purchaseInventoryDTO, purchaseInventoryItemDTO);
        supplierInventoryDTO.reduceStorageInventoryChange(productLocalInfoDTO.getSellUnit(), supplierInventoryChangeAmount, currentInventoryPrice);
        supplierInventoryDTO.setTotalInStorageChangeAmount(-totalChangeAmount);
        supplierInventoryDTO.setLastPurchaseInventoryOrderId(null);
        supplierInventoryDTO.setLastStorageTime(null);
        supplierInventoryDTOs.add(supplierInventoryDTO);
      }
      getProductThroughService().saveOrUpdateSupplierInventory(txnWriter, supplierInventoryDTOs);


      List<OutStorageRelationDTO> outStorageRelationDTOList = new ArrayList<OutStorageRelationDTO>();
      List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {

        List<OutStorageRelation> outStorageRelationList = txnWriter.getOutStorageRelationByRelated(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(),
            OrderTypes.INVENTORY, purchaseInventoryItemDTO.getId(), purchaseInventoryItemDTO.getProductId());
        if (CollectionUtils.isEmpty(outStorageRelationList)) {
          continue;
        }

        Set<Long> itemProductIdSet = new HashSet<Long>();
        itemProductIdSet.add(purchaseInventoryItemDTO.getProductId());

        for (OutStorageRelation outStorageRelation : outStorageRelationList) {
          outStorageRelation.setDisabled(YesNo.YES);
          txnWriter.update(outStorageRelation);
          OrderTypes orderType = outStorageRelation.getOutStorageOrderType();

          BcgogoOrderItemDto bcgogoOrderItemDto = null;
          BcgogoOrderDto orderDto = null;

          HashSet<Long> orderIdSet = null;
          if (reIndexMap.containsKey(orderType)) {
            orderIdSet = reIndexMap.get(orderType);
          } else {
            orderIdSet = new HashSet<Long>();
          }
          orderIdSet.add(outStorageRelation.getOutStorageOrderId());
          reIndexMap.put(orderType, orderIdSet);


          Map<Long, List<InStorageRecordDTO>> productInStorageMap = getProductThroughService().getInStorageRecordMap(bcgogoOrderDto.getShopId(),
              bcgogoOrderDto.getStorehouseId(), itemProductIdSet, supplierIdSet,null,true);

          switch (orderType) {
            case REPAIR:
              orderDto = txnWriter.getById(RepairOrder.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(RepairOrderItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            case RETURN:
              orderDto = txnWriter.getById(PurchaseReturn.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(PurchaseInventoryItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            case SALE:
              orderDto = txnWriter.getById(SalesOrder.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(SalesOrderItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            case INVENTORY_CHECK:
              orderDto = txnWriter.getById(InventoryCheck.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(InventoryCheckItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            case ALLOCATE_RECORD:
              orderDto = txnWriter.getById(AllocateRecord.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(AllocateRecordItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            case REPAIR_PICKING:
              orderDto = txnWriter.getById(RepairPicking.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(RepairPickingItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            case INNER_PICKING:
              orderDto = txnWriter.getById(InnerPicking.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(InnerPickingItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            case BORROW_ORDER:
              orderDto = txnWriter.getById(BorrowOrder.class, outStorageRelation.getOutStorageOrderId()).toDTO();
              bcgogoOrderItemDto = txnWriter.getById(BorrowOrderItem.class, outStorageRelation.getOutStorageItemId()).toDTO();
              break;
            default:
              LOG.error("orderType error:", orderType.toString());
          }
          if (orderDto == null || bcgogoOrderItemDto == null) {
            continue;
          }

          bcgogoOrderItemDto.setAmount(outStorageRelation.getUseRelatedAmount());
          bcgogoOrderItemDto.setUnit(outStorageRelation.getRelatedUnit());
          OutStorageRelationDTO[] outStorageRelationDTOs = new OutStorageRelationDTO[1];
          outStorageRelationDTOs[0] = outStorageRelation.toDTO();
          bcgogoOrderItemDto.setOutStorageRelationDTOs(outStorageRelationDTOs);

          List<InStorageRecordDTO> storageRecordDTOList = productInStorageMap.get(purchaseInventoryItemDTO.getProductId());
          outStorageRelationDTOList.addAll(getOutStorageRelation(orderDto, orderType, bcgogoOrderItemDto, storageRecordDTOList,
              productLocalInfoDTOMap.get(bcgogoOrderItemDto.getProductId())));

          if (CollectionUtils.isNotEmpty(storageRecordDTOList)) {
            inStorageRecordDTOList.addAll(storageRecordDTOList);
          }
        }
      }

      getProductInStorageService().saveOrUpdateInStorageRecordDTO(inStorageRecordDTOList, txnWriter);
      this.saveOutStorageRelation(outStorageRelationDTOList, txnWriter);

      Map<Long, List<SupplierInventoryDTO>> map = getSupplierInventoryList(outStorageRelationDTOList);

      List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
      for (List list : map.values()) {
        supplierInventoryDTOList.addAll(list);
      }
      getProductThroughService().saveOrUpdateSupplierInventory(txnWriter, supplierInventoryDTOList);

      txnWriter.commit(status);
    } catch (Exception e) {
      LOG.error("productOutStroageService.productThroughByInventoryRepeal error,orderId:" + bcgogoOrderDto.getId());
      LOG.error(e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }

    return reIndexMap;
  }

}
