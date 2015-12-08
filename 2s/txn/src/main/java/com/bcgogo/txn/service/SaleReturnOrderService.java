package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: QiuXinYu
 * Date: 12-7-16
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SaleReturnOrderService implements ISaleReturnOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(ISaleReturnOrderService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private RFITxnService rfiTxnService;
  private IProductOutStorageService productOutStorageService;
  private IProductInStorageService productInStorageService;

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }
 public IProductInStorageService getProductInStorageService() {
    return productInStorageService == null ? ServiceManager.getService(IProductInStorageService.class) : productInStorageService;
  }

  public RFITxnService getRfiTxnService() {
    return rfiTxnService == null ? ServiceManager.getService(RFITxnService.class) : rfiTxnService;
  }

  @Override
  public void createSalesReturnDTOByPurchaseReturnOrderDTO(PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    if (purchaseReturnDTO.getShopId() == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    if (purchaseReturnDTO.getSupplierShopId() == null)
      throw new BcgogoException(BcgogoExceptionType.WholeSalerShopIdNotFound);
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = userService.getCustomerByCustomerShopIdAndShopId(purchaseReturnDTO.getSupplierShopId(), purchaseReturnDTO.getShopId());
    if (customerDTO == null) throw new BcgogoException(BcgogoExceptionType.CustomerNotFound);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      SalesReturnDTO salesReturnDTO = new SalesReturnDTO();
      salesReturnDTO.setShopId(purchaseReturnDTO.getSupplierShopId());
      salesReturnDTO.setCustomerId(customerDTO.getId());
      salesReturnDTO.setCustomer(customerDTO.getName());
      salesReturnDTO.setEditDate(System.currentTimeMillis());
      salesReturnDTO.setVestDate(System.currentTimeMillis());
      salesReturnDTO.setTotal(purchaseReturnDTO.getTotal());
      salesReturnDTO.setStatus(OrderStatus.PENDING);
      salesReturnDTO.setPurchaseReturnOrderId(purchaseReturnDTO.getId());
      salesReturnDTO.setPurchaseReturnOrderMemo(purchaseReturnDTO.getMemo());
      salesReturnDTO.setReceiptNo(txnService.getReceiptNo(salesReturnDTO.getShopId(), OrderTypes.SALE_RETURN, null));

      SalesReturn salesReturn = new SalesReturn().fromDTO(salesReturnDTO);
      writer.save(salesReturn);
      salesReturnDTO.setId(salesReturn.getId());

      if (!ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())) {
        Set<Long> customerProductIdSet = new HashSet<Long>();
        for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
          customerProductIdSet.add(purchaseReturnItemDTO.getProductId());
        }
        List<SalesReturnItemDTO> itemDTOs = new ArrayList<SalesReturnItemDTO>();
        Map<Long, ProductMappingDTO> productMappingDTOMap = productService.getCustomerProductMappingDTOMap(purchaseReturnDTO.getShopId(), salesReturnDTO.getShopId(), customerProductIdSet.toArray(new Long[customerProductIdSet.size()]));
        ProductMappingDTO productMappingDTO = null;
        SalesReturnItemDTO salesReturnItemDTO = null;
        SalesReturnItem salesReturnItem = null;
        for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
          salesReturnItemDTO = new SalesReturnItemDTO();
          productMappingDTO = productMappingDTOMap.get(purchaseReturnItemDTO.getProductId());
          if (productMappingDTO != null && productMappingDTO.getSupplierProductDTO() != null) {

            salesReturnItemDTO.setProductDTOWithOutUnit(productMappingDTO.getSupplierProductDTO());
            salesReturnItemDTO.setAmount(purchaseReturnItemDTO.getAmount());
            salesReturnItemDTO.setPrice(purchaseReturnItemDTO.getPrice());
            salesReturnItemDTO.setTotal(purchaseReturnItemDTO.getTotal());
            salesReturnItemDTO.setSalesReturnId(salesReturnDTO.getId());
            salesReturnItemDTO.setUnit(purchaseReturnItemDTO.getUnit());
            salesReturnItem = generateSalesReturnItem(salesReturnItemDTO);
            writer.save(salesReturnItem);
            purchaseReturnItemDTO.setId(salesReturnItem.getId());
            itemDTOs.add(salesReturnItemDTO);
          }
        }
        salesReturnDTO.setItemDTOs(itemDTOs.toArray(new SalesReturnItemDTO[itemDTOs.size()]));
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public SalesReturnDTO getSalesReturnDTOById(Long shopId, Long id) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesReturn> salesReturnList = writer.getSalesReturnDTOById(shopId, id);
    if (CollectionUtils.isNotEmpty(salesReturnList)) {
      SalesReturnDTO salesReturnDTO = salesReturnList.get(0).toDTO();
      List<SalesReturnItem> items = writer.getSalesReturnItemsBySalesReturnId(id);
      SalesReturnItemDTO[] itemDTOs = new SalesReturnItemDTO[items.size()];

      for (int i = 0; i < items.size(); i++) {
        SalesReturnItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      salesReturnDTO.setItemDTOs(itemDTOs);
      salesReturnDTO.setVestDateStr(DateUtil.dateLongToStr(salesReturnDTO.getVestDate(), DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
      return salesReturnDTO;
    }
    return null;
  }

  @Override
  public SalesReturnDTO getSalesReturnDTOByPurchaseReturnOrderId(Long purchaseReturnOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesReturn> salesReturnList = writer.getSalesReturnDTOByPurchaseReturnOrderId(purchaseReturnOrderId);

    if (CollectionUtils.isNotEmpty(salesReturnList)) {
      SalesReturnDTO salesReturnDTO = salesReturnList.get(0).toDTO();

      List<SalesReturnItem> items = writer.getSalesReturnItemsBySalesReturnId(salesReturnDTO.getId());
      SalesReturnItemDTO[] itemDTOs = new SalesReturnItemDTO[items.size()];

      for (int i = 0; i < items.size(); i++) {
        SalesReturnItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      salesReturnDTO.setItemDTOs(itemDTOs);
      return salesReturnDTO;
    }
    return null;
  }

  @Override
  public SalesReturnDTO getSimpleSalesReturnDTOByPurchaseReturnOrderId(Long shopId, Long purchaseReturnOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
     List<SalesReturn> salesReturnList = writer.getSalesReturnByPurchaseReturnOrderIdAndShopId(shopId,purchaseReturnOrderId);

     if (CollectionUtils.isNotEmpty(salesReturnList)) {
       SalesReturnDTO salesReturnDTO = salesReturnList.get(0).toDTO();
       return salesReturnDTO;
     }
     return null;
  }

  @Override
  public SalesReturnDTO acceptSalesReturnDTO(SalesReturnDTO salesReturnDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);

      SalesReturn salesReturn = writer.getById(SalesReturn.class, salesReturnDTO.getId());
      OrderStatus preOrderStatus = salesReturn.getStatus();
      salesReturn.fromDTO(salesReturnDTO);
      salesReturn.setStatus(OrderStatus.WAITING_STORAGE);
      writer.saveOrUpdate(salesReturn);
      salesReturnDTO.setStatus(salesReturn.getStatus());
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(salesReturnDTO.getShopId(), salesReturnDTO.getUserId(), salesReturnDTO.getId(), ObjectTypes.SALE_RETURN_ORDER, OperationTypes.ACCEPT));
      orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesReturnDTO.getShopId(), salesReturnDTO.getUserId(), salesReturnDTO.getStatus(), preOrderStatus, salesReturnDTO.getId(), OrderTypes.SALE_RETURN));

      PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class, salesReturn.getPurchaseReturnOrderId());
      preOrderStatus = purchaseReturn.getStatus();
      purchaseReturn.setStatus(OrderStatus.SELLER_ACCEPTED);
      writer.saveOrUpdate(purchaseReturn);
      salesReturnDTO.setPurchaseReturnDTO(purchaseReturn.toDTO());
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(purchaseReturn.getShopId(), salesReturnDTO.getUserId(), purchaseReturn.getId(), ObjectTypes.PURCHASE_RETURN_ORDER, OperationTypes.ACCEPT));
      orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseReturn.getShopId(), salesReturnDTO.getUserId(), purchaseReturn.getStatus(), preOrderStatus, purchaseReturn.getId(), OrderTypes.RETURN));

      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
    return salesReturnDTO;
  }

  @Override
  public SalesReturnDTO refuseSalesReturnDTO(SalesReturnDTO salesReturnDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);

      SalesReturn salesReturn = writer.getById(SalesReturn.class, salesReturnDTO.getId());
      OrderStatus preOrderStatus = salesReturn.getStatus();
      salesReturn.fromDTO(salesReturnDTO);
      salesReturn.setStatus(OrderStatus.REFUSED);
      writer.saveOrUpdate(salesReturn);
      salesReturnDTO.setStatus(salesReturn.getStatus());

      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(salesReturnDTO.getShopId(), salesReturnDTO.getUserId(), salesReturnDTO.getId(), ObjectTypes.SALE_RETURN_ORDER, OperationTypes.REFUSE));
      orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesReturnDTO.getShopId(), salesReturnDTO.getUserId(), salesReturnDTO.getStatus(), preOrderStatus, salesReturnDTO.getId(), OrderTypes.SALE_RETURN));

      PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class, salesReturn.getPurchaseReturnOrderId());
      preOrderStatus = purchaseReturn.getStatus();
      purchaseReturn.setStatus(OrderStatus.SELLER_REFUSED);
      purchaseReturn.setRefuseReason(salesReturn.getRefuseReason());
      writer.saveOrUpdate(purchaseReturn);
      PurchaseReturnDTO purchaseReturnDTO=purchaseReturn.toDTO();
      purchaseReturnDTO.setShopVersionId(salesReturnDTO.getShopVersionId());
      salesReturnDTO.setPurchaseReturnDTO(purchaseReturnDTO);
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(purchaseReturn.getShopId(), salesReturnDTO.getUserId(), purchaseReturn.getId(), ObjectTypes.PURCHASE_RETURN_ORDER, OperationTypes.REFUSE));
      orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseReturn.getShopId(), salesReturnDTO.getUserId(), purchaseReturn.getStatus(), preOrderStatus, purchaseReturn.getId(), OrderTypes.RETURN));


      //处理库存信息 店铺库存  入库退货单
      IProductService productService = ServiceManager.getService(IProductService.class);
      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      ShopDTO shopDTO = configService.getShopById(purchaseReturn.getShopId());
      List<PurchaseReturnItem> items = writer.getPurchaseReturnItemsByReturnId(purchaseReturn.getId());
      InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
      List<PurchaseReturnItemDTO> purchaseReturnItemDTOs=new ArrayList<PurchaseReturnItemDTO>();
      List<Long> productIdList = new ArrayList<Long>();
      for (PurchaseReturnItem purchaseReturnItem : items) {
        if (purchaseReturnItem.getProductId() == null) {
          continue;
        }
        productIdList.add(purchaseReturnItem.getProductId());
        //本地商品库
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(purchaseReturnItem.getProductId(), purchaseReturn.getShopId());
        //标准商品库
        ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), purchaseReturn.getShopId());
        //库存
        Inventory inventory = writer.getById(Inventory.class, purchaseReturnItem.getProductId());      // 参数 purchaseReturnItemDTO.getProductId()是本地库productId

        inventoryService.caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
        double purchaseReturnItemAmount = purchaseReturnItem.getAmount();
        if (UnitUtil.isStorageUnit(purchaseReturnItem.getUnit(), productDTO)) {
          purchaseReturnItemAmount = purchaseReturnItemAmount * productDTO.getRate();
        }
        //被拒绝后  现库存量=原库存量+退货量
        inventory.setAmount(inventory.getAmount() + purchaseReturnItemAmount);
        if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopDTO.getShopVersionId())){
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,new StoreHouseInventoryDTO(purchaseReturn.getStorehouseId(),purchaseReturnItem.getProductId(),null,purchaseReturnItemAmount));
        }
        //更新库存
        writer.update(inventory);
        inventoryService.caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
        purchaseReturnItemDTOs.add(purchaseReturnItem.toDTO());
      }
      //更新店铺库存告警信息
      inventoryService.updateMemocacheLimitByInventoryLimitDTO(purchaseReturn.getShopId(), inventoryLimitDTO);
      //商品打通 供应商拒绝给客户入库
      purchaseReturnDTO.setItemDTOs(purchaseReturnItemDTOs.toArray(new PurchaseReturnItemDTO[purchaseReturnItemDTOs.size()]));
     getProductInStorageService().productThroughByOrder(purchaseReturnDTO,OrderTypes.RETURN,OrderStatus.SELLER_REFUSED,writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return salesReturnDTO;
  }

  private SalesReturnItem generateSalesReturnItem(SalesReturnItemDTO salesReturnItemDTO) throws Exception {
    SalesReturnItem salesReturnItem = new SalesReturnItem();
    salesReturnItem.setAmount(salesReturnItemDTO.getAmount());
    salesReturnItem.setMemo(salesReturnItemDTO.getMemo());
    salesReturnItem.setPrice(salesReturnItemDTO.getPrice());
    salesReturnItem.setProductId(salesReturnItemDTO.getProductId());
    salesReturnItem.setSalesReturnId(salesReturnItemDTO.getSalesReturnId());
    salesReturnItem.setTotal(salesReturnItemDTO.getTotal());
    salesReturnItem.setUnit(salesReturnItemDTO.getUnit());
    return salesReturnItem;
  }

  /**
   * 填充退货明细中的具体信息
   *
   * @param salesReturnDTO
   * @return
   * @throws Exception
   */
  @Override
  public SalesReturnDTO fillSalesReturnItemDTOsDetailInfo(SalesReturnDTO salesReturnDTO) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    if (salesReturnDTO != null) {
      Boolean isNeedPurchaseReturnOrderItemInfo = false;
      boolean isNeedWholesalerProductInfo = false;
      Map<Long, PurchaseReturnItemDTO> purchaseReturnItemDTOMap = new HashMap<Long, PurchaseReturnItemDTO>();
      Long customerShopId = null;
      if (!OrderStatus.SETTLED.equals(salesReturnDTO.getStatus()) && salesReturnDTO.getPurchaseReturnOrderId() != null) {
        isNeedPurchaseReturnOrderItemInfo = true;
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        IPurchaseReturnService purchaseReturnService = ServiceManager.getService(IPurchaseReturnService.class);
        PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(salesReturnDTO.getPurchaseReturnOrderId());
        purchaseReturnDTO = purchaseReturnService.fillPurchaseReturnItemDTOsDetailInfo(purchaseReturnDTO);
        if (!ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())) {
          for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
            purchaseReturnItemDTOMap.put(purchaseReturnItemDTO.getId(), purchaseReturnItemDTO);
          }
        }
        customerShopId = purchaseReturnDTO.getShopId();
      }
      if (OrderStatus.WAITING_STORAGE == salesReturnDTO.getStatus() && salesReturnDTO.getPurchaseReturnOrderId() != null) {
        isNeedWholesalerProductInfo = true;
      }
      Long shopId = salesReturnDTO.getShopId();
      SalesReturnItemDTO[] salesReturnItemDTOs = salesReturnDTO.getItemDTOs();
      double totalReturnAmount = 0d;
      if (salesReturnItemDTOs != null && shopId != null) {
        IProductService productService = ServiceManager.getService(IProductService.class);
        TxnWriter writer = txnDaoManager.getWriter();
        for (SalesReturnItemDTO salesReturnItemDTO : salesReturnItemDTOs) {
          Long productLocalInfoId = salesReturnItemDTO.getProductId();
          ProductDTO productDTO = productService.getProductByProductLocalInfoId(productLocalInfoId, shopId);

          ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(salesReturnItemDTO.getProductHistoryId(), shopId);
          if (productHistoryDTO != null) {
            salesReturnItemDTO.setProductHistoryDTO(productHistoryDTO);
            if (OrderUtil.salesOrderInProgress.contains(salesReturnDTO.getStatus())) {
              salesReturnItemDTO.setProductUnitRateInfo(productDTO);
            }
          } else {
            salesReturnItemDTO.setProductDTOWithOutUnit(productDTO);
          }
          Inventory inventory = writer.getById(Inventory.class, productLocalInfoId);
          if (inventory != null) {
            salesReturnItemDTO.setInventoryAmount(inventory.getAmount());
          }
          totalReturnAmount += salesReturnItemDTO.getAmount();
          if (isNeedPurchaseReturnOrderItemInfo) {
            salesReturnItemDTO.setPurchaseReturnItemDTO(purchaseReturnItemDTOMap.get(salesReturnItemDTO.getCustomerOrderItemId()));
          }
          if (isNeedWholesalerProductInfo) {
            salesReturnItemDTO.setStorageAmount(salesReturnItemDTO.getPurchaseReturnItemDTO().getAmount());
            PurchaseReturnItem purchaseReturnItem = txnWriter.getById(PurchaseReturnItem.class, salesReturnItemDTO.getCustomerOrderItemId());
            if (purchaseReturnItem != null) {
              // 店面退货时的产品
              ProductDTO shopProductDTO = productService.getProductByProductLocalInfoId(purchaseReturnItem.getProductId(), customerShopId);
              //找到供应商对应的商品
              List<ProductDTO> productDTOs = productService.getProductDTOsBy7P(shopId, shopProductDTO);
              if (CollectionUtils.isNotEmpty(productDTOs)) {
                ProductDTO supplierProductDTO = CollectionUtil.getFirst(productDTOs);
                ProductLocalInfoDTO supplierProductLocalInfoDTO = productService.getProductLocalInfoByProductId(supplierProductDTO.getId(), shopId);
                salesReturnItemDTO.setWholesalerUnit(supplierProductLocalInfoDTO.getSellUnit());
                salesReturnItemDTO.setStorageUnit(supplierProductLocalInfoDTO.getStorageUnit());
                salesReturnItemDTO.setSellUnit(supplierProductLocalInfoDTO.getSellUnit());
                salesReturnItemDTO.setRate(supplierProductLocalInfoDTO.getRate());
                salesReturnItemDTO.setCustomerProductId(purchaseReturnItem.getProductId());
                salesReturnItemDTO.setProductId(supplierProductLocalInfoDTO.getId());
              }
            }
          }
        }
      }
      salesReturnDTO.setTotalReturnAmount(NumberUtil.round(totalReturnAmount, NumberUtil.MONEY_PRECISION));

      //相关单据
      if (salesReturnDTO.getOriginOrderId() != null && salesReturnDTO.getOriginOrderType() != null) {
        if (OrderTypes.SALE == salesReturnDTO.getOriginOrderType()) {
          SalesOrder salesOrder = txnWriter.getById(SalesOrder.class, salesReturnDTO.getOriginOrderId());
          if (salesOrder != null) {
            salesReturnDTO.setOriginReceiptNo(salesOrder.getReceiptNo());
          }
        } else if (OrderTypes.REPAIR == salesReturnDTO.getOriginOrderType()) {
          RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, salesReturnDTO.getOriginOrderId());
          if (repairOrder != null) {
            salesReturnDTO.setOriginReceiptNo(repairOrder.getReceiptNo());
          }
        }
      }

    }
    return salesReturnDTO;

  }

  @Override
  public Result settleSalesReturn(SalesReturnDTO salesReturnDTO, Long userId, Map<Long, Long> supplierNewProductIdOldProductIdMap) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    SalesReturn salesReturn = writer.getById(SalesReturn.class, salesReturnDTO.getId());
    if (salesReturn == null) {
      LOG.error("结算销售退货单时出错: 此单据不存在。ID:{}", salesReturnDTO.getId());
      return new Result("结算时出错，此单据不存在。", false);
    }

    Object status = writer.begin();
    try {
      OrderStatus preStatus = salesReturnDTO.getStatus();
      if (preStatus != OrderStatus.WAITING_STORAGE) {
        writer.rollback(status);
        return new Result("单据状态不符，无法结算。当前状态：" + preStatus.getSellerName(), false);
      }

      double totalCostPrice = 0d;
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      if (!ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())) {
        for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {
          SalesReturnItem salesReturnItem = writer.getById(SalesReturnItem.class, salesReturnItemDTO.getId());
          if (salesReturnItem == null) {
            LOG.error("SalesReturnItem找不到。SaleReturnOrderService.settleSalesReturn, item ID:{}", salesReturnItemDTO.getId());
            continue;
          }
          //更新结算时更新的入库数量与单位, 成本价
          salesReturnItem.setAmount(salesReturnItemDTO.getAmount());
          salesReturnItem.setPrice(salesReturnItemDTO.getPrice());
          salesReturnItem.setUnit(salesReturnItemDTO.getUnit());
          salesReturnItem.setProductId(salesReturnItemDTO.getProductId());

          Inventory inventory = saveOrUpdateInventoryForSalesReturn(salesReturnDTO, salesReturnItemDTO, supplierNewProductIdOldProductIdMap);

          ProductDTO productDTO = productService.getProductByProductLocalInfoId(inventory.getId(), inventory.getShopId());
          productDTO.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, salesReturnDTO.getVestDateStr()));
          inventorySearchIndexList.add(txnService.createInventorySearchIndex(inventory, productDTO.getId()));

          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesReturnItem.getProductId(), salesReturn.getShopId());
          double costPrice = 0d;
          if (UnitUtil.isStorageUnit(salesReturnItem.getUnit(), productLocalInfoDTO)) {
            costPrice = NumberUtil.doubleVal(inventory.getInventoryAveragePrice()) * NumberUtil.longValue(productLocalInfoDTO.getRate());
          } else {
            costPrice = NumberUtil.doubleVal(inventory.getInventoryAveragePrice());
          }
          salesReturnItem.setCostPrice(NumberUtil.round(costPrice, NumberUtil.MONEY_PRECISION));
          salesReturnItem.setTotalCostPrice(NumberUtil.round(salesReturnItem.getAmount() * salesReturnItem.getCostPrice(), NumberUtil.MONEY_PRECISION));
          totalCostPrice += salesReturnItem.getTotalCostPrice();
          writer.update(salesReturnItem);
        }
      }

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesReturnDTO.getShopVersionId())){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(salesReturnDTO.getShopId(), salesReturnDTO.getStorehouseId());
        salesReturnDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
        salesReturn.setStorehouseId(salesReturnDTO.getStorehouseId());
        salesReturn.setStorehouseName(salesReturnDTO.getStorehouseName());
      }

      salesReturn.setSalesReturner(salesReturnDTO.getSalesReturner());
      salesReturn.setSalesReturnerId(salesReturnDTO.getSalesReturnerId());
      salesReturn.setStatus(OrderStatus.SETTLED);
      salesReturn.setTotalCostPrice(totalCostPrice);
      salesReturn.setVestDate(salesReturnDTO.getVestDate());
      writer.saveOrUpdate(salesReturn);
      salesReturnDTO.setStatus(salesReturn.getStatus());
      salesReturnDTO.setId(salesReturn.getId());
      salesReturnDTO.setTotalCostPrice(totalCostPrice);

      //receivable, reception_record 相关
      postProcessingForSavingSaleReturnOrder(writer, salesReturnDTO);

      OrderStatusChangeLogDTO orderStatusChangeLogDTO = new OrderStatusChangeLogDTO(salesReturn.getShopId(), userId, OrderStatus.SETTLED, preStatus, salesReturn.getId(), OrderTypes.SALE_RETURN);
      orderStatusChangeLogService.saveOrderStatusChangeLog(orderStatusChangeLogDTO);

      writer.commit(status);
      ServiceManager.getService(IInventoryService.class).addOrUpdateInventorySearchIndexWithList(salesReturnDTO.getShopId(), inventorySearchIndexList);

      OperationLogDTO operationLogDTO = new OperationLogDTO(salesReturn.getShopId(), userId, salesReturn.getId(), ObjectTypes.SALE_RETURN_ORDER, OperationTypes.SETTLE);
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(operationLogDTO);
      return new Result("结算成功！", true);
    } catch (Exception e) {
      writer.rollback(status);
      LOG.error("结算销售退货单时出错. salesReturnDTO:{}", salesReturnDTO);
      LOG.error(e.getMessage(), e);
      return new Result("结算失败。", false);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<Long, Long> handleProductForSalesReturnOrder(SalesReturnDTO salesReturnDTO) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    if (salesReturnDTO == null || ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())) {
      return new HashMap<Long, Long>();
    }
    // 找不到6属性相同商品时，将老的productId与新建的productId记录下来，待Inventory设置成老product的入库价
    Map<Long, Long> supplierNewProductIdOldProductIdMap = new HashMap<Long, Long>();
    // 店面商品localInfoId, 供应商商品localInfoId.
    Map<Long, Long> customerSupplierProductMapping = new HashMap<Long, Long>();
    PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(salesReturnDTO.getPurchaseReturnOrderId());
    for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {
      SalesReturnItem salesReturnItem = txnWriter.getById(SalesReturnItem.class, salesReturnItemDTO.getId());
      if (salesReturnItem.getCustomerOrderItemId() != null) {   // 批发商接受的销售退货单
        //得到对应店面的入库退货单
        PurchaseReturnItem purchaseReturnItem = txnWriter.getById(PurchaseReturnItem.class, salesReturnItem.getCustomerOrderItemId());
        if (purchaseReturnItem == null) {
          LOG.error("SalesReturnItem对应的PurchaseReturnItem找不到, SalesReturnItem ID:{}", salesReturnItem.getCustomerOrderItemId());
          continue;
        }

        // 入库退货单的商品
        ProductDTO shopProductDTO = productService.getProductByProductLocalInfoId(purchaseReturnItem.getProductId(), purchaseReturnDTO.getShopId());
        // 6字段在供应商商品中查询出相应商品
        List<ProductDTO> productDTOs = productService.getProductDTOsBy7P(salesReturnDTO.getShopId(), shopProductDTO);
        // 销售退货单生成时的商品
        ProductDTO productDTO = productService.getProductByProductLocalInfoId(salesReturnItemDTO.getProductId(), salesReturnDTO.getShopId());
        if (CollectionUtils.isEmpty(productDTOs)) {
          //如果无6字段匹配的商品，新建
          ProductDTO newProductDTO = new ProductDTO(shopProductDTO.getName(), shopProductDTO.getBrand(), shopProductDTO.getSpec(), shopProductDTO.getModel(), shopProductDTO.getProductVehicleBrand(), shopProductDTO.getProductVehicleModel(), shopProductDTO.getProductVehicleYear(), shopProductDTO.getProductVehicleEngine());
          newProductDTO.setShopId(salesReturnDTO.getShopId());
          productService.saveNewProduct(newProductDTO);
          supplierNewProductIdOldProductIdMap.put(newProductDTO.getProductLocalInfoId(), salesReturnItemDTO.getProductId());
          salesReturnItemDTO.setProductDTOWithOutUnit(newProductDTO);
          // product mapping 映射关系
          customerSupplierProductMapping.put(purchaseReturnItem.getProductId(), newProductDTO.getProductLocalInfoId());
        } else {
          //如果有6字段匹配的商品，是否与销售退货单中的productId相符，如果不相符，update
          boolean productIdRight = false;
          for (ProductDTO existProductDTO : productDTOs) {
            if (existProductDTO.getId().equals(productDTO.getId())) {
              productIdRight = true;
              existProductDTO.setProductLocalInfoId(salesReturnItemDTO.getProductId());
              salesReturnItemDTO.setProductDTOWithOutUnit(existProductDTO);
            }
          }
          if (!productIdRight) {
            ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(productDTOs.get(0).getId(), salesReturnDTO.getShopId());
            ProductDTO rightProductDTO = productService.getProductByProductLocalInfoId(productLocalInfoDTO.getId(), salesReturnDTO.getShopId());
            salesReturnItemDTO.setProductDTOWithOutUnit(rightProductDTO);

            customerSupplierProductMapping.put(purchaseReturnItem.getProductId(), productLocalInfoDTO.getId());
          }
        }
      }
    }
    productService.updateProductMappingForSalesReturn(purchaseReturnDTO.getShopId(), salesReturnDTO.getShopId(), customerSupplierProductMapping);
    return supplierNewProductIdOldProductIdMap;
  }


  private Inventory saveOrUpdateInventoryForSalesReturn(SalesReturnDTO salesReturnDTO, SalesReturnItemDTO salesReturnItemDTO, Map<Long, Long> supplierNewProductIdOldProductIdMap) throws Exception {
    if(salesReturnItemDTO.getProductId() == null || salesReturnDTO.getShopId() == null){
      LOG.error("SaleReturnOrderService.saveOrUpdateInventoryForSalesReturn shopId :{},productId:{}",salesReturnDTO.getShopId(), salesReturnItemDTO.getProductId());
      return null;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Inventory inventory = txnWriter.getInventoryByIdAndshopId(salesReturnItemDTO.getProductId(), salesReturnDTO.getShopId());
    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesReturnItemDTO.getProductId(), salesReturnDTO.getShopId());
    Double returnAmount = 0d;
    if (UnitUtil.isStorageUnit(salesReturnItemDTO.getUnit(), productLocalInfoDTO)) {
      returnAmount = salesReturnItemDTO.getAmount() * productLocalInfoDTO.getRate();
    } else {
      returnAmount = salesReturnItemDTO.getAmount();
    }
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesReturnDTO.getShopVersionId())){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(salesReturnDTO.getStorehouseId(), salesReturnItemDTO.getProductId(), null);
      storeHouseInventoryDTO.setChangeAmount(returnAmount);
      storeHouseService.saveOrUpdateStoreHouseInventoryDTO(txnWriter, storeHouseInventoryDTO);
    }

    if (inventory == null) {
      inventory = new Inventory();
      inventory.setId(salesReturnItemDTO.getProductId());
      inventory.setShopId(salesReturnDTO.getShopId());
      inventory.setAmount(returnAmount);
      inventory.setUnit(salesReturnItemDTO.getUnit());

      if (supplierNewProductIdOldProductIdMap != null) {
        // 如果Mapping被更改为新建的商品，则新建商品的最新入库价等信息仍使用老映射关系中商品的对应属性
        Long oldProductLocalInfoId = supplierNewProductIdOldProductIdMap.get(salesReturnItemDTO.getProductId());
        if (oldProductLocalInfoId != null) {
          Inventory oldProductInventory = txnWriter.getInventoryByIdAndshopId(oldProductLocalInfoId, salesReturnDTO.getShopId());
          if (oldProductInventory != null) {
            inventory.setLatestInventoryPrice(oldProductInventory.getLatestInventoryPrice());
            inventory.setInventoryAveragePrice(oldProductInventory.getInventoryAveragePrice());
            inventory.setSalesPrice(oldProductInventory.getSalesPrice());
            inventory.setLastStorageTime(oldProductInventory.getLastStorageTime());
          }
        }
      }
      txnWriter.save(inventory);
    } else {
      if (StringUtils.isEmpty(inventory.getUnit())) {
        inventory.setUnit(salesReturnItemDTO.getUnit());
      }
      inventory.setAmount(inventory.getAmount() + returnAmount);
      txnWriter.update(inventory);
    }
    return inventory;
  }

  private void postProcessingForSavingSaleReturnOrder(TxnWriter writer, SalesReturnDTO salesReturnDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    Long shopId = salesReturnDTO.getShopId();
    ReceivableDTO receivableDTO = new ReceivableDTO();
    receivableDTO.setOrderType(OrderTypes.SALE_RETURN);
    receivableDTO.setStatus(ReceivableStatus.FINISH);

    receivableDTO.setOrderId(salesReturnDTO.getId());
    receivableDTO.setShopId(shopId);
    receivableDTO.setSettledAmount(-salesReturnDTO.getSettledAmount());
    receivableDTO.setTotal(-salesReturnDTO.getTotal());

    receivableDTO.setCash(-NumberUtil.doubleVal(salesReturnDTO.getCashAmount()));  //现金
    receivableDTO.setBankCard(-NumberUtil.doubleVal(salesReturnDTO.getBankAmount()));    //银行卡
    receivableDTO.setCheque(-NumberUtil.doubleVal(salesReturnDTO.getBankCheckAmount()));    //支票
    receivableDTO.setDeposit(-NumberUtil.doubleVal(salesReturnDTO.getCustomerDeposit()));  // 预收款 add by zhuj
    receivableDTO.setStrike(-NumberUtil.doubleVal(salesReturnDTO.getStrikeAmount()));   //冲帐
    receivableDTO.setDiscount(-NumberUtil.doubleVal(salesReturnDTO.getDiscountAmount()));   //优惠
    receivableDTO.setDebt(-NumberUtil.doubleVal(salesReturnDTO.getAccountDebtAmount()));   //欠款
    receivableDTO.setLastPayee(salesReturnDTO.getUserName());
    receivableDTO.setLastPayeeId(salesReturnDTO.getUserId());
    receivableDTO.setCustomerId(salesReturnDTO.getCustomerId());
    receivableDTO.setVestDate(salesReturnDTO.getVestDate());
    receivableDTO.setReceiptNo(salesReturnDTO.getReceiptNo());
    receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getTotal());
    receivableDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_PAYABLE);

    ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
    ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
    writer.save(receivableHistory);
    receivableHistoryDTO.setId(receivableHistory.getId());

    if (salesReturnDTO.getSettledAmount() >= 0) {
      ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
      receptionRecordDTO.setReceivableHistoryId(receivableHistory.getId());
      receptionRecordDTO.setDayType(DayType.OTHER_DAY);
      receptionRecordDTO.setAmount(-salesReturnDTO.getSettledAmount());
      receptionRecordDTO.setReceivableId(receivableDTO.getId());
      receptionRecordDTO.setOrderTotal(-salesReturnDTO.getTotal());
      receptionRecordDTO.setChequeNo(salesReturnDTO.getBankCheckNo());

      receptionRecordDTO.setCash(-NumberUtil.doubleVal(salesReturnDTO.getCashAmount()));
      receptionRecordDTO.setBankCard(-NumberUtil.doubleVal(salesReturnDTO.getBankAmount()));
      receptionRecordDTO.setCheque(-NumberUtil.doubleVal(salesReturnDTO.getBankCheckAmount()));
      receptionRecordDTO.setDeposit(-NumberUtil.doubleVal(salesReturnDTO.getCustomerDeposit())); // add by zhuj
      receptionRecordDTO.setStrike(-NumberUtil.doubleVal(salesReturnDTO.getStrikeAmount()));
      receptionRecordDTO.setMemberId(receivableDTO.getMemberId());
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setOriginDebt(0d);
      receptionRecordDTO.setDiscount(-NumberUtil.doubleVal(salesReturnDTO.getDiscountAmount()));
      receptionRecordDTO.setOriginDebt(-NumberUtil.doubleVal(salesReturnDTO.getAccountDebtAmount()));
      receptionRecordDTO.setRemainDebt(-NumberUtil.doubleVal(salesReturnDTO.getAccountDebtAmount()));
      receptionRecordDTO.setShopId(salesReturnDTO.getShopId());
      receptionRecordDTO.setOrderId(salesReturnDTO.getId());
      receptionRecordDTO.setReceptionDate(System.currentTimeMillis());
      receptionRecordDTO.setOrderTypeEnum(OrderTypes.SALE_RETURN);
      receptionRecordDTO.setOrderStatusEnum(OrderStatus.SETTLED);
      receptionRecordDTO.setPayee(salesReturnDTO.getUserName());
      receptionRecordDTO.setPayeeId(salesReturnDTO.getUserId());
      ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
      receptionRecordDTOs[0] = receptionRecordDTO;
      receivableDTO.setRecordDTOs(receptionRecordDTOs);
    }
    txnService.createOrUpdateReceivable(writer, receivableDTO);

    //add by zhuj 更新depositOrder
    if (salesReturnDTO.getCustomerDeposit() > 0.001) {
      CustomerDepositDTO customerDepositDTO = new CustomerDepositDTO();
      customerDepositDTO.setOperator(salesReturnDTO.getUserName());
      customerDepositDTO.setShopId(salesReturnDTO.getShopId());
      customerDepositDTO.setActuallyPaid(salesReturnDTO.getCustomerDeposit());
      customerDepositDTO.setCustomerId(salesReturnDTO.getCustomerId());
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      //基础字段在service方法中有填充
      depositOrderDTO.setDepositType(DepositType.SALES_BACK.getScene());
      depositOrderDTO.setInOut(InOutFlag.IN_FLAG.getCode());
      depositOrderDTO.setRelatedOrderId(salesReturnDTO.getId());
      depositOrderDTO.setRelatedOrderNo(salesReturnDTO.getReceiptNo());
      ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
      customerDepositService.customerDepositUse(customerDepositDTO, depositOrderDTO,writer);
    }

  }

  public Result validateSalesReturnBeforeSettle(SalesReturnDTO salesReturnDTO) throws Exception {
    if (salesReturnDTO == null) {
      return new Result("数据出错，请重试。", false);

    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    //客户为空不能退货
    if (StringUtil.isEmpty(salesReturnDTO.getCustomer())) {
      return new Result("客户姓名为空，不能退货。", false);
    }
    //判断来源单据的状态
    if (salesReturnDTO.getOriginOrderId() != null && salesReturnDTO.getOriginOrderType() != null) {
      if (salesReturnDTO.getOriginOrderType() == OrderTypes.SALE) {
        SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(salesReturnDTO.getOriginOrderId());
        if (salesOrderDTO == null || !(salesOrderDTO.getStatus() == OrderStatus.SALE_DEBT_DONE || salesOrderDTO.getStatus() == OrderStatus.SALE_DONE)) {
          return new Result("相关销售单单据状态不正确。", false);
        }
      } else if (salesReturnDTO.getOriginOrderType() == OrderTypes.REPAIR) {
        RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(salesReturnDTO.getOriginOrderId());
        if (repairOrderDTO == null || repairOrderDTO.getStatus() != OrderStatus.REPAIR_SETTLED) {
          return new Result("相关施工单状态不正确。", false);
        }
      } else {
        return new Result("相关单据类型不正确。", false);
      }
    }

    if (ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())) {
      return new Result("退货商品为空。", false);
    }

    List<SalesReturnItemDTO> salesReturnItemDTOList = new ArrayList<SalesReturnItemDTO>();
    for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {
      if (salesReturnItemDTO == null) {
        return new Result("退货商品有空行。", false);
      }
      if (StringUtil.isEmpty(salesReturnItemDTO.getProductName())) {
        continue;
      }
      if (NumberUtil.longValue(salesReturnItemDTO.getProductId()) > 0) {
        salesReturnItemDTOList.add(salesReturnItemDTO);
        continue;
      }
      ProductDTO productDTO = new ProductDTO().fromSalesReturnItemDTO(salesReturnItemDTO);
      IProductService productService = ServiceManager.getService(IProductService.class);
      List<ProductDTO> productDTOList = productService.getProductDTOsBy7P(salesReturnDTO.getShopId(), productDTO);

      if (CollectionUtils.isNotEmpty(productDTOList)) {
        ProductDTO productLocalInfo = productService.getProductById(productDTOList.get(0).getId(), salesReturnDTO.getShopId());
        salesReturnItemDTO.setProductId(productLocalInfo.getProductLocalInfoId());
        salesReturnItemDTOList.add(salesReturnItemDTO);
        continue;
      } else {
        return new Result("店铺没有商品:" + salesReturnItemDTO.getProductName() + "，不能退货", false);
      }
    }
    Result result = getRfiTxnService().getDeletedProductValidatorResult(salesReturnDTO);
    if (result != null && !result.isSuccess()) {
      return result;
    }
    salesReturnDTO.setItemDTOs(salesReturnItemDTOList.toArray(new SalesReturnItemDTO[salesReturnItemDTOList.size()]));
    return new Result("可以结算。", true);
  }

  @Override
  public Result settleSalesReturnForNormal(SalesReturnDTO salesReturnDTO) throws Exception {

    if (salesReturnDTO == null || ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())) {
      return new Result("结算失败！", false);
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(salesReturnDTO.getCustomerId(), salesReturnDTO.getShopId());
    if (customerDTO == null) {
      throw new BcgogoException(BcgogoExceptionType.CustomerNotFound);
    }

    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);

    Result result;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();

    try {
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesReturnDTO.getShopVersionId())){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(salesReturnDTO.getShopId(), salesReturnDTO.getStorehouseId());
        salesReturnDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
      }

      salesReturnDTO.setCustomerDTO(customerDTO);
      MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(salesReturnDTO.getShopId(), customerDTO.getId());
      if (memberDTO != null) {
        salesReturnDTO.setMemberNo(memberDTO.getMemberNo());
      }
      salesReturnDTO.setEditDate(System.currentTimeMillis());
      salesReturnDTO.setStatus(OrderStatus.SETTLED);

      double totalCostPrice = 0.0;

      List<SalesReturnItemDTO> itemDTOs = new ArrayList<SalesReturnItemDTO>();
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();

      for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {

        double costPrice = 0d;
        Inventory inventory = saveOrUpdateInventoryForSalesReturn(salesReturnDTO, salesReturnItemDTO, null);

        ProductDTO productDTO = productService.getProductByProductLocalInfoId(inventory.getId(), inventory.getShopId());
        productDTO.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, salesReturnDTO.getVestDateStr()));
        inventorySearchIndexList.add(txnService.createInventorySearchIndex(inventory, productDTO.getId()));

        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesReturnItemDTO.getProductId(), salesReturnDTO.getShopId());
        if (UnitUtil.isStorageUnit(salesReturnItemDTO.getUnit(), productLocalInfoDTO)) {
          costPrice = NumberUtil.doubleVal(inventory.getInventoryAveragePrice()) * NumberUtil.longValue(productLocalInfoDTO.getRate());
        } else {
          costPrice = NumberUtil.doubleVal(inventory.getInventoryAveragePrice());
        }

        if (salesReturnDTO.getOriginOrderId() == null) {
          salesReturnItemDTO.setCostPrice(NumberUtil.round(costPrice, NumberUtil.MONEY_PRECISION));
          salesReturnItemDTO.setTotalCostPrice(NumberUtil.round(salesReturnItemDTO.getAmount() * salesReturnItemDTO.getCostPrice(), NumberUtil.MONEY_PRECISION));
        } else {
          salesReturnItemDTO.setTotalCostPrice(NumberUtil.round(salesReturnItemDTO.getAmount() * salesReturnItemDTO.getCostPrice(), NumberUtil.MONEY_PRECISION));
        }
        totalCostPrice += salesReturnItemDTO.getTotalCostPrice();
      }
      if (StringUtils.isBlank(salesReturnDTO.getReceiptNo())) {
            salesReturnDTO.setReceiptNo(txnService.getReceiptNo(salesReturnDTO.getShopId(), OrderTypes.SALE_RETURN, null));
      }
      salesReturnDTO.setTotalCostPrice(totalCostPrice);
      SalesReturn salesReturn = new SalesReturn().fromDTO(salesReturnDTO);
      writer.save(salesReturn);
      salesReturnDTO.setId(salesReturn.getId());
      salesReturnDTO.setStatus(salesReturn.getStatus());
      salesReturnDTO.setId(salesReturn.getId());
      salesReturnDTO.setTotalCostPrice(totalCostPrice);

      for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {
        if (StringUtil.isEmpty(salesReturnItemDTO.getProductName()) || salesReturnItemDTO.getProductId() == null) {
          continue;
        }
        SalesReturnItem salesReturnItem = generateSalesReturnItem(salesReturnItemDTO);
        salesReturnItem.setCostPrice(salesReturnItemDTO.getCostPrice());
        salesReturnItem.setTotalCostPrice(salesReturnItemDTO.getTotalCostPrice());
        salesReturnItem.setSalesReturnId(salesReturnDTO.getId());
        salesReturnItem.setBusinessCategoryId(salesReturnItemDTO.getBusinessCategoryId());
        salesReturnItem.setBusinessCategoryName(salesReturnItemDTO.getBusinessCategoryName());
        writer.save(salesReturnItem);
        salesReturnItemDTO.setId(salesReturnItem.getId());
        itemDTOs.add(salesReturnItemDTO);
      }

      //receivable, reception_record 相关
      postProcessingForSavingSaleReturnOrder(writer, salesReturnDTO);

      salesReturnDTO.setItemDTOs(itemDTOs.toArray(new SalesReturnItemDTO[itemDTOs.size()]));


      //销售退货单库存打通
      ServiceManager.getService(IProductInStorageService.class).productThroughByOrder(salesReturnDTO, OrderTypes.SALE_RETURN, salesReturnDTO.getStatus());


      OrderStatusChangeLogDTO orderStatusChangeLogDTO = new OrderStatusChangeLogDTO(salesReturn.getShopId(), salesReturnDTO.getUserId(), OrderStatus.SETTLED, null, salesReturn.getId(), OrderTypes.SALE_RETURN);
      orderStatusChangeLogService.saveOrderStatusChangeLog(orderStatusChangeLogDTO);

      writer.commit(status);


      ServiceManager.getService(IInventoryService.class).addOrUpdateInventorySearchIndexWithList(salesReturnDTO.getShopId(), inventorySearchIndexList);

      OperationLogDTO operationLogDTO = new OperationLogDTO(salesReturnDTO.getShopId(), salesReturnDTO.getUserId(), salesReturn.getId(), ObjectTypes.SALE_RETURN_ORDER, OperationTypes.CREATE);
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(operationLogDTO);
    } finally {
      writer.rollback(status);
    }


    result = new Result(salesReturnDTO.getId().toString(), true);
    return result;
  }

  public SalesReturnDTO createSalesReturnByOrderId(ModelMap model, SalesReturnDTO salesReturnDTO) throws Exception {
    if (salesReturnDTO.getOriginOrderId() == null || salesReturnDTO.getOriginOrderType() == null) {
      return salesReturnDTO;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    Long customerId = null;

    if (salesReturnDTO.getOriginOrderType() == OrderTypes.SALE) {
      SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(salesReturnDTO.getOriginOrderId(), salesReturnDTO.getShopId());
      if (salesOrderDTO == null) {
        return salesReturnDTO;
      }
      customerId = salesOrderDTO.getCustomerId();
      salesReturnDTO.setCustomerId(salesOrderDTO.getCustomerId());
      salesReturnDTO.setContactId(salesOrderDTO.getContactId());
      salesReturnDTO = this.getCustomerInfoByCustomerInfo(model, salesReturnDTO, customerId.toString(), null);
      salesReturnDTO.setTotal(salesOrderDTO.getTotal());
      salesReturnDTO.setTotalCostPrice(NumberUtil.toReserve(NumberUtil.doubleVal(salesOrderDTO.getTotalCostPrice()) - NumberUtil.doubleVal(salesOrderDTO.getOtherTotalCostPrice()),NumberUtil.PRECISION));
      salesReturnDTO.setOriginReceiptNo(salesOrderDTO.getReceiptNo());
      salesReturnDTO.setStorehouseId(salesOrderDTO.getStorehouseId());
      salesReturnDTO.setStorehouseName(salesOrderDTO.getStorehouseName());
      if (ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
        return salesReturnDTO;
      }

      List<SalesReturnItemDTO> salesReturnItemDTOList = new ArrayList<SalesReturnItemDTO>();

      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        //得到产品信息
        SalesReturnItemDTO salesReturnItemDTO = this.getSalesReturnItemByProductId(salesReturnDTO.getShopId(), salesOrderItemDTO.getProductId());

        salesReturnItemDTO.setPrice(salesOrderItemDTO.getPrice());
        salesReturnItemDTO.setAmount(salesOrderItemDTO.getAmount());
        salesReturnItemDTO.setTotal(salesOrderItemDTO.getTotal());
        salesReturnItemDTO.setOriginSaleTotal(salesOrderItemDTO.getTotal());
        salesReturnItemDTO.setOriginSalesPrice(salesOrderItemDTO.getPrice());
        salesReturnItemDTO.setOriginSaleAmount(salesOrderItemDTO.getAmount());
        salesReturnItemDTO.setSellUnit(salesReturnItemDTO.getSellUnit());
        salesReturnItemDTO.setUnit(salesOrderItemDTO.getUnit());
        salesReturnItemDTO.setCostPrice(NumberUtil.doubleVal(salesOrderItemDTO.getCostPrice()));
        salesReturnItemDTO.setTotalCostPrice(NumberUtil.doubleVal(salesOrderItemDTO.getTotalCostPrice()));
        if (StringUtil.isEmpty(salesOrderItemDTO.getUnit())) {
          salesReturnItemDTO.setOriginSaleAmountStr(String.valueOf(salesOrderItemDTO.getAmount()));
        } else {
          salesReturnItemDTO.setOriginSaleAmountStr(salesReturnItemDTO.getOriginSaleAmount() + salesReturnItemDTO.getUnit());
        }
        salesReturnItemDTOList.add(salesReturnItemDTO);
      }
      salesReturnDTO.setItemDTOs(salesReturnItemDTOList.toArray(new SalesReturnItemDTO[salesReturnItemDTOList.size()]));
      return salesReturnDTO;
    } else if (salesReturnDTO.getOriginOrderType() == OrderTypes.REPAIR) {
      RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(salesReturnDTO.getOriginOrderId());
      if (repairOrderDTO == null) {
        return salesReturnDTO;
      }
      customerId = repairOrderDTO.getCustomerId();
      salesReturnDTO.setCustomerId(repairOrderDTO.getCustomerId());
      salesReturnDTO.setOriginReceiptNo(repairOrderDTO.getReceiptNo());
      salesReturnDTO.setStorehouseId(repairOrderDTO.getStorehouseId());
      salesReturnDTO.setStorehouseName(repairOrderDTO.getStorehouseName());

      salesReturnDTO = this.getCustomerInfoByCustomerInfo(model, salesReturnDTO, customerId.toString(), null);
      if (ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
        return salesReturnDTO;
      }

      double total = 0.0;
      double totalCostPrice = 0.0;
      List<SalesReturnItemDTO> salesReturnItemDTOList = new ArrayList<SalesReturnItemDTO>();
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        //得到产品信息
        SalesReturnItemDTO salesReturnItemDTO = this.getSalesReturnItemByProductId(salesReturnDTO.getShopId(), repairOrderItemDTO.getProductId());

        salesReturnItemDTO.setPrice(repairOrderItemDTO.getPrice());
        salesReturnItemDTO.setAmount(repairOrderItemDTO.getAmount());
        salesReturnItemDTO.setTotal(repairOrderItemDTO.getTotal());
        salesReturnItemDTO.setOriginSaleTotal(repairOrderItemDTO.getTotal());

        salesReturnItemDTO.setOriginSalesPrice(repairOrderItemDTO.getPrice());
        salesReturnItemDTO.setOriginSaleAmount(repairOrderItemDTO.getAmount());
        salesReturnItemDTO.setSellUnit(salesReturnItemDTO.getSellUnit());
        salesReturnItemDTO.setUnit(repairOrderItemDTO.getUnit());
        salesReturnItemDTO.setCostPrice(NumberUtil.doubleVal(repairOrderItemDTO.getCostPrice()));
        salesReturnItemDTO.setTotalCostPrice(NumberUtil.doubleVal(repairOrderItemDTO.getTotalCostPrice()));
        if (StringUtil.isEmpty(repairOrderItemDTO.getUnit())) {
          salesReturnItemDTO.setOriginSaleAmountStr(String.valueOf(repairOrderItemDTO.getAmount()));
        } else {
          salesReturnItemDTO.setOriginSaleAmountStr(salesReturnItemDTO.getOriginSaleAmount() + salesReturnItemDTO.getUnit());
        }
        salesReturnItemDTOList.add(salesReturnItemDTO);
        total += repairOrderItemDTO.getTotal();
        totalCostPrice += repairOrderItemDTO.getTotalCostPrice();
      }
      salesReturnDTO.setTotal(total);
      salesReturnDTO.setTotalCostPrice(totalCostPrice);
      salesReturnDTO.setItemDTOs(salesReturnItemDTOList.toArray(new SalesReturnItemDTO[salesReturnItemDTOList.size()]));
      return salesReturnDTO;
    }
    return salesReturnDTO;
  }


  public SalesReturnDTO getCustomerInfoByProductIds(SalesReturnDTO salesReturnDTO, String productIdStr) throws Exception {
    if (salesReturnDTO == null || StringUtil.isEmpty(productIdStr)) {
      return salesReturnDTO;
    }

    String[] productIds = productIdStr.split(",");
    StringBuilder stringBuilder = new StringBuilder();
    if (ArrayUtil.isEmpty(productIds)) {
      return salesReturnDTO;
    }
    for (int arrayLength = 0; arrayLength < productIds.length; arrayLength++) {
      if (NumberUtil.isNumber(productIds[arrayLength])) {
        stringBuilder.append(productIds[arrayLength]).append(",");
      }
    }
    productIds = stringBuilder.toString().split(",");

    if (ArrayUtil.isEmpty(productIds)) {
      return salesReturnDTO;
    }
    SalesReturnItemDTO[] itemDTOs = new SalesReturnItemDTO[productIds.length];
    salesReturnDTO.setItemDTOs(itemDTOs);
    double total = 0.0;
    for (int i = 0; i < productIds.length; i++) {
      if (!com.bcgogo.utils.NumberUtil.isNumber(productIds[i])) {
        continue;
      }
      itemDTOs[i] = this.getSalesReturnItemByProductId(salesReturnDTO.getShopId(), new Long(productIds[i]));
      total += itemDTOs[i].getTotal();
    }
    salesReturnDTO.setTotal(total);
    return salesReturnDTO;
  }

  public SalesReturnDTO getCustomerInfoByCustomerInfo(ModelMap model, SalesReturnDTO salesReturnDTO, String customerIdStr, String customerName) throws Exception {

    if (salesReturnDTO == null) {
      return salesReturnDTO;
    }
    if (StringUtil.isEmpty(customerIdStr) && StringUtil.isEmpty(customerName)) {
      return salesReturnDTO;
    }

    if (StringUtil.isEmpty(customerIdStr) || "null".equals(customerIdStr)) {
      IUserService userService = ServiceManager.getService(IUserService.class);
      if (StringUtil.isNotEmpty(customerName)) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName(customerName);
        customerDTO.setShopId(salesReturnDTO.getShopId());
        customerDTO = userService.createCustomer(customerDTO);
        customerIdStr = customerDTO.getId().toString();
      }
    }

    if (StringUtil.isEmpty(customerIdStr) || !NumberUtil.isNumber(customerIdStr)) {
      return salesReturnDTO;
    }

    IUserService userService = ServiceManager.getService(IUserService.class);

    //设置客户信息
    List<CustomerDTO> customerDTOList = userService.getShopCustomerById(salesReturnDTO.getShopId(), Long.valueOf(customerIdStr));
    if (CollectionUtils.isNotEmpty(customerDTOList)) {
      salesReturnDTO.setCustomerDTO(customerDTOList.get(0));
    }

    List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(Long.valueOf(customerIdStr));
    if (null != customerRecordDTOs && customerRecordDTOs.size() > 0) {
      model.addAttribute("customerRecordDTO", customerRecordDTOs.get(0));
    }
      //客户的应收应付
      ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, salesReturnDTO.getShopId(), Long.valueOf(customerIdStr));

      return salesReturnDTO;
  }


  public SalesReturnItemDTO getSalesReturnItemByProductId(Long shopId, Long productId) throws Exception {

    SalesReturnItemDTO salesReturnItemDTO = new SalesReturnItemDTO();
    if (shopId == null || productId == null) {
      return salesReturnItemDTO;
    }

    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);

    ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);

    if (productDTO != null && productDTO.getId() != null) {

      salesReturnItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
      salesReturnItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
      salesReturnItemDTO.setVehicleYear(productDTO.getProductVehicleYear());
      salesReturnItemDTO.setVehicleEngine(productDTO.getProductVehicleEngine());
      salesReturnItemDTO.setBrand(productDTO.getBrand());
      salesReturnItemDTO.setModel(productDTO.getModel());
      salesReturnItemDTO.setSpec(productDTO.getSpec());

      salesReturnItemDTO.setProductName(productDTO.getName());
      salesReturnItemDTO.setProductId(productId);
      salesReturnItemDTO.setStorageBin(productDTO.getStorageBin());
      salesReturnItemDTO.setCommodityCode(productDTO.getCommodityCode());

      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(productId, shopId);

      if (null != productLocalInfoDTO && null != productLocalInfoDTO.getBusinessCategoryId()) {
        Category category = rfiTxnService.getCategoryById(shopId, productLocalInfoDTO.getBusinessCategoryId());

        if (null != category) {
          salesReturnItemDTO.setBusinessCategoryId(category.getId());
          salesReturnItemDTO.setBusinessCategoryName(category.getCategoryName());
        }
      }

      salesReturnItemDTO.setUnitAndRate(productLocalInfoDTO);

      InventoryDTO inventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productId);

      if (inventoryDTO == null) {
        LOG.error("productId为{}的inventory记录不存在。shopID:{}", productId, shopId);
      } else {
        salesReturnItemDTO.setPrice(NumberUtil.doubleVal(inventoryDTO.getSalesPrice()));
        salesReturnItemDTO.setAmount(RfTxnConstant.ORDER_DEFAULT_AMOUNT);
        salesReturnItemDTO.setTotal(salesReturnItemDTO.getPrice() * salesReturnItemDTO.getAmount());
        salesReturnItemDTO.setOriginSalesPrice(inventoryDTO.getSalesPrice());
        if (UnitUtil.isStorageUnit(salesReturnItemDTO.getUnit(), productDTO)) {
          salesReturnItemDTO.setInventoryAmount(inventoryDTO.getAmount() / salesReturnItemDTO.getRate());
          salesReturnItemDTO.setPurchasePrice(productDTO.getPurchasePrice() == null ?
              0d : productDTO.getPurchasePrice() * productDTO.getRate());
          salesReturnItemDTO.setInventoryAmountApprox(salesReturnItemDTO.getInventoryAmount());

        } else {
          salesReturnItemDTO.setInventoryAmount(inventoryDTO.getAmount());
          salesReturnItemDTO.setPurchasePrice(productDTO.getPurchasePrice() == null ? 0 : productDTO.getPurchasePrice());
          salesReturnItemDTO.setInventoryAmountApprox(salesReturnItemDTO.getInventoryAmount());
        }
      }
    }
    return salesReturnItemDTO;
  }

  /**
   * 销售退货 或者 入库退货统计
   *
   * @param orderSearchConditionDTO
   * @param startPageNo
   * @param maxRows
   * @return
   */
  public List getReturnStatByCondition(OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows) {
    try {
      ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      if (orderSearchConditionDTO == null) {
        return null;
      }

      List<Object> result = new ArrayList<Object>();
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.validateBeforeQuery();

      if (StringUtils.isNotEmpty(orderSearchConditionDTO.getCustomerName())) {
        ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
        List<Customer> customerList = customerService.getAllCustomerByNameAndMobile(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getCustomerName(), orderSearchConditionDTO.getMobile());
        if (CollectionUtils.isEmpty(customerList)) {
          return null;
        }

        Set<String> customerIdSet = new HashSet<String>();
        for (Customer customer : customerList) {
          customerIdSet.add(customer.getId().toString());
        }
        orderSearchConditionDTO.setCustomerOrSupplierIds(customerIdSet.toArray(new String[customerIdSet.size()]));
        orderSearchConditionDTO.setCustomerOrSupplierName(null);
        orderSearchConditionDTO.setCustomerName(null);
      }else if(StringUtils.isNotEmpty(orderSearchConditionDTO.getSupplierName())) {
        ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
        List<Supplier> supplierList = customerService.getAllSupplierByNameAndMobile(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getSupplierName(), orderSearchConditionDTO.getMobile());
        if (CollectionUtils.isEmpty(supplierList)) {
          return null;
        }

        Set<String> customerIdSet = new HashSet<String>();
        for (Supplier supplier : supplierList) {
          customerIdSet.add(supplier.getId().toString());
        }
        orderSearchConditionDTO.setCustomerOrSupplierIds(customerIdSet.toArray(new String[customerIdSet.size()]));
        orderSearchConditionDTO.setCustomerOrSupplierName(null);
      }

      orderSearchConditionDTO.setOrderStatusRepeal(StatConstant.NOT_CONTAIN_REPEAL);
      orderSearchConditionDTO.setSort("created_time desc");
      orderSearchConditionDTO.setRowStart((startPageNo - 1) * maxRows);
      orderSearchConditionDTO.setPageRows(maxRows);

      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);
      if (CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrders())) {
        Set<Long> customerIds = new HashSet<Long>();
        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
          customerIds.add(order.getCustomerOrSupplierId());

          if (orderSearchConditionDTO.getStatType().equals(StatConstant.INVENTORY_RETURN_STATISTICS)) {
            continue;
          }

          SalesReturnDTO salesReturnDTO = this.getSalesReturnDTOById(orderSearchConditionDTO.getShopId(), order.getOrderId());
          if (salesReturnDTO != null && salesReturnDTO.getOriginOrderId() != null) {
            order.setOriginOrderIdStr(salesReturnDTO.getOriginOrderId().toString());
            order.setOriginOrderType(salesReturnDTO.getOriginOrderType().toString());
            if (salesReturnDTO.getOriginOrderType() == OrderTypes.SALE) {
              SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(salesReturnDTO.getOriginOrderId());
              order.setOriginReceiptNo(salesOrderDTO == null ? "" : salesOrderDTO.getReceiptNo());

            } else if (salesReturnDTO.getOriginOrderType() == OrderTypes.REPAIR) {
              RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(salesReturnDTO.getShopId(), salesReturnDTO.getOriginOrderId());
              order.setOriginReceiptNo(repairOrderDTO == null ? "" : repairOrderDTO.getReceiptNo());
            }
          }
        }

        if (orderSearchConditionDTO.getStatType().equals(StatConstant.INVENTORY_RETURN_STATISTICS)) {
          Map<Long, SupplierDTO> supplierDTOMap = ServiceManager.getService(ISupplierService.class).getSupplierByIdSet(orderSearchConditionDTO.getShopId(), customerIds);
          if (MapUtils.isNotEmpty(supplierDTOMap)) {
            SupplierDTO supplierDTO = null;
            for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
              supplierDTO = supplierDTOMap.get(order.getCustomerOrSupplierId());
              if (supplierDTO != null) {
                order.setCustomerStatus(supplierDTO.getStatus());
              }
            }
          }
        } else if (orderSearchConditionDTO.getStatType().equals(StatConstant.SALES_RETURN_STATISTICS)) {
          Map<Long, CustomerDTO> customerDTOMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(
              orderSearchConditionDTO.getShopId(), customerIds);
          if (MapUtils.isNotEmpty(customerDTOMap)) {
            CustomerDTO customerDTO = null;
            for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
              customerDTO = customerDTOMap.get(order.getCustomerOrSupplierId());
              if (customerDTO != null) {
                order.setCustomerStatus(customerDTO.getStatus());
              }
            }
          }
        }
      }
      Pager pager = new Pager(Integer.valueOf(String.valueOf(orderSearchResultListDTO.getNumFound())), startPageNo, maxRows);
      result.add(orderSearchResultListDTO);
      if (StringUtils.isBlank(orderSearchConditionDTO.getStartTimeStr())) {
        pager.setStartDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, orderSearchConditionDTO.getStartTime()));
      } else {
        pager.setStartDateStr(orderSearchConditionDTO.getStartTimeStr());
      }
      if (StringUtils.isBlank(orderSearchConditionDTO.getEndTimeStr())) {
        pager.setEndDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, orderSearchConditionDTO.getEndTime() - 1L));
      } else {
        pager.setEndDateStr(orderSearchConditionDTO.getEndTimeStr());
      }
      result.add(pager);
      return result;

    } catch (Exception e) {
      LOG.error("saleReturnOrderService.getSalesReturnList:" + orderSearchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public void repealOrderInTxn(SalesReturnDTO salesReturnDTO, Long toStorehouseId) {
    Long shopId = salesReturnDTO.getShopId();
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      //库存扣除(inventorySarchIndex)
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesReturnDTO.getShopVersionId())){
        storehouseInventoryChangeForRepeal(salesReturnDTO, writer);
      }else{
        inventoryChangeForRepeal(salesReturnDTO, writer);
      }

      //receivable相关处理, debt处理。  reception_record在线程中处理RFTxnService.updateOrderRepealReception
      ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, salesReturnDTO.getId());
      salesReturnDTO.setReceivableDTO(receivableDTO);
      receivableDTO.setStatus(ReceivableStatus.REPEAL);
      txnService.createOrUpdateReceivable(writer, receivableDTO);

      //预收款处理
      //add by zhuj 更新depositOrder
      if (salesReturnDTO.getCustomerDeposit() > 0.001) {
        CustomerDepositDTO customerDepositDTO = new CustomerDepositDTO();
        customerDepositDTO.setOperator(salesReturnDTO.getUserName());
        customerDepositDTO.setShopId(salesReturnDTO.getShopId());
        customerDepositDTO.setActuallyPaid(salesReturnDTO.getCustomerDeposit());
        customerDepositDTO.setCustomerId(salesReturnDTO.getCustomerId());
        DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
        //基础字段在service方法中有填充
        depositOrderDTO.setDepositType(DepositType.SALES_BACK_REPEAL.getScene());
        depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
        depositOrderDTO.setRelatedOrderId(salesReturnDTO.getId());
        depositOrderDTO.setRelatedOrderNo(salesReturnDTO.getReceiptNo());
        ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
        customerDepositService.customerDepositUse(customerDepositDTO, depositOrderDTO,writer);
      }

      //单据状态修改为repeal
      SalesReturn salesReturn = writer.getById(SalesReturn.class, salesReturnDTO.getId());
      salesReturn.setStatus(OrderStatus.REPEAL);
      writer.update(salesReturn);
      salesReturnDTO.setStatus(OrderStatus.REPEAL);

      //changelog
      OrderStatusChangeLogDTO orderStatusChangeLogDTO = new OrderStatusChangeLogDTO(salesReturnDTO.getShopId(), salesReturnDTO.getUserId(), OrderStatus.REPEAL, null, salesReturnDTO.getId(), OrderTypes.SALE_RETURN);
      ServiceManager.getService(IOrderStatusChangeLogService.class).saveOrderStatusChangeLog(orderStatusChangeLogDTO);

      writer.commit(status);
      if (StringUtils.isNotBlank(salesReturnDTO.getDraftOrderIdStr())) {
        ServiceManager.getService(DraftOrderService.class).deleteDraftOrder(salesReturnDTO.getShopId(), Long.valueOf(salesReturnDTO.getDraftOrderIdStr()));
      }
    }catch(Exception e){
      LOG.error("SalesReturnOrderService.repealOrderInTxn error", e);
    }finally{
      writer.rollback(status);
    }
  }

  private void storehouseInventoryChangeForRepeal(SalesReturnDTO salesReturnDTO, TxnWriter writer) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    //update inventory amount   && update repair_remind_event Status
    List<Long> productIdList = salesReturnDTO.getProductIdList();
    Set<Long> productIdSet = new HashSet<Long>();
    productIdSet.addAll(productIdList);
    Long shopId = salesReturnDTO.getShopId();
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    storeHouseInventoryDTOMap.putAll(storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, salesReturnDTO.getStorehouseId(), productIdList.toArray(new Long[]{})));
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
    Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productIdSet);
    for (SalesReturnItemDTO itemDTO : salesReturnDTO.getItemDTOs()) {
      StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTO.getProductId());
      if (storeHouseInventoryDTO == null) {
        storeHouseInventoryDTO = new StoreHouseInventoryDTO(salesReturnDTO.getStorehouseId(), itemDTO.getProductId(), 0d);
      }
      ProductDTO productDTO = productDTOMap.get(itemDTO.getProductId());
      InventoryDTO inventoryDTO = inventoryDTOMap.get(itemDTO.getProductId());
      if(productDTO == null || inventoryDTO == null){
        continue;
      }
      inventoryService.caculateBeforeLimit(inventoryDTO, salesReturnDTO.getInventoryLimitDTO());
      double inventoryAmount = storeHouseInventoryDTO.getAmount();
      double itemAmount = itemDTO.getAmount() == null ? 0d : itemDTO.getAmount();
      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
        itemAmount = itemAmount * productDTO.getRate();
      }
      if (itemAmount > inventoryAmount) {
        throw new BcgogoException(BcgogoExceptionType.NotEnoughInventory);
      } else {
        storeHouseInventoryDTO.setAmount(inventoryAmount - itemAmount);
        inventoryDTO.setAmount(inventoryDTO.getAmount() - itemAmount);
      }
      inventoryService.caculateAfterLimit(inventoryDTO, salesReturnDTO.getInventoryLimitDTO());
      storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
      writer.updateInventoryAmount(shopId, inventoryDTO.getId(), inventoryDTO.getAmount());
    }
  }

  private void inventoryChangeForRepeal(SalesReturnDTO salesReturnDTO, TxnWriter writer) throws Exception{
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = salesReturnDTO.getShopId();
    List<Long> productIdList = salesReturnDTO.getProductIdList();
    Set<Long> productIdSet = new HashSet<Long>();
    productIdSet.addAll(productIdList);
//    Map<Long, InventorySearchIndex> inventorySearchIndexMap = searchService.getInventorySearchIndexMapByProductIds(shopId, productIdList.toArray(new Long[]{}));
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
    Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productIdSet);
    for (SalesReturnItemDTO itemDTO : salesReturnDTO.getItemDTOs()) {
      ProductDTO productDTO = productDTOMap.get(itemDTO.getProductId());
      InventoryDTO inventoryDTO = inventoryDTOMap.get(itemDTO.getProductId());
      if(productDTO == null || inventoryDTO == null){
        continue;
      }
      inventoryService.caculateBeforeLimit(inventoryDTO, salesReturnDTO.getInventoryLimitDTO());
      double inventoryAmount = inventoryDTO.getAmount() == null ? 0d : inventoryDTO.getAmount();
      double itemAmount = itemDTO.getAmount() == null ? 0d : itemDTO.getAmount();
      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
        itemAmount = itemAmount * productLocalInfoDTO.getRate();
      }
      if (itemAmount > inventoryAmount) {
        throw new BcgogoException(BcgogoExceptionType.NotEnoughInventory);
      } else {
        inventoryDTO.setAmount(inventoryAmount - itemAmount);
      }
      inventoryService.caculateAfterLimit(inventoryDTO, salesReturnDTO.getInventoryLimitDTO());
      writer.updateInventoryAmount(shopId, inventoryDTO.getId(), inventoryDTO.getAmount());
    }
  }

  @Override
  public Result validateEnoughInventoryForRepeal(SalesReturnDTO salesReturnDTO) {
    if(ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())){
      return new Result(true);
    }
    Set<Long> productIds = new HashSet<Long>();
    for(SalesReturnItemDTO salesReturnItemDTO:salesReturnDTO.getItemDTOs()){
      productIds.add(salesReturnItemDTO.getProductId());
    }
    Long shopId = salesReturnDTO.getShopId();
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productIds);
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIds);
    try{
      List<ProductDTO> notEnoughProducts = new ArrayList<ProductDTO>();
      for(SalesReturnItemDTO itemDTO:salesReturnDTO.getItemDTOs()){
        InventoryDTO inventoryDTO = inventoryDTOMap.get(itemDTO.getProductId());
        ProductDTO productDTO = productDTOMap.get(itemDTO.getProductId());
        if(inventoryDTO == null || productDTO == null){
          ProductHistoryDTO productHistoryDTO = productHistoryService.getProductHistoryById(itemDTO.getProductHistoryId(), shopId);
          String msg = "商品:" + productHistoryDTO.getName() + "不存在！";
          return new Result(msg, false);
        }
        double salesReturnItemAmount = itemDTO.getAmount();
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
          salesReturnItemAmount = salesReturnItemAmount * productDTO.getRate();
        }
        if(inventoryDTO.getAmount() < salesReturnItemAmount){
          notEnoughProducts.add(productDTO);
        }
      }
      if(CollectionUtils.isEmpty(notEnoughProducts)){
        return new Result(true);
      }else{
        return new Result("库存不足", false, Result.Operation.NOT_ENOUGH_INVENTORY.getValue(), notEnoughProducts);
      }

    }catch(Exception e){
      LOG.error("salesReturnOrderService.validateEnoughInventoryForRepeal", e);
      return new Result("验证库存时出错！", false);
    }
  }

  @Override
  public void updateCustomerInfoByRepeal(SalesReturnDTO salesReturnDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(salesReturnDTO.getCustomerId());
    if(CollectionUtils.isEmpty(customerRecordDTOs)){
      return;
    }
    CustomerRecordDTO customerRecordDTO = customerRecordDTOs.get(0);
    customerRecordDTO.setTotalReturnAmount(customerRecordDTO.getTotalReturnAmount()-salesReturnDTO.getSettledAmount()-salesReturnDTO.getAccountDebtAmount());
    Double totalReceivable = supplierPayableService.getSumReceivableByCustomerId(salesReturnDTO.getCustomerId(),salesReturnDTO.getShopId(), OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
    Double totalPayable = supplierPayableService.getSumReceivableByCustomerId(salesReturnDTO.getCustomerId(),salesReturnDTO.getShopId(), OrderDebtType.CUSTOMER_DEBT_PAYABLE);
    customerRecordDTO.setTotalPayable(NumberUtil.toReserve(totalPayable,NumberUtil.MONEY_PRECISION));
    customerRecordDTO.setTotalReceivable(NumberUtil.toReserve(totalReceivable,NumberUtil.MONEY_PRECISION));
    try{
      userService.updateCustomerRecord(customerRecordDTO);
    }catch(Exception e){
      LOG.error("SalesreturnOrderService.updateCustomerInfoByRepeal error", e);
    }
  }

  @Override
  public Result validateStorehouseInventoryForRepeal(SalesReturnDTO salesReturnDTO) throws Exception {
    Long shopId = salesReturnDTO.getShopId();
    Long storehouseId = salesReturnDTO.getStorehouseId();
    SalesReturnItemDTO[] itemDTOs = salesReturnDTO.getItemDTOs();
    if(ArrayUtils.isEmpty(itemDTOs)){
      return new Result(true);
    }
    if(storehouseId == null){
      throw new IllegalArgumentException("storehouseId不存在.");
    }
    Long[] productLocalInfoIds = new Long[itemDTOs.length];
    for (int i = 0; i < itemDTOs.length; i++) {
      productLocalInfoIds[i] = itemDTOs[i].getProductId();
    }
    Set<Long> productIdSet = new HashSet<Long>();
    productIdSet.addAll(Arrays.asList(productLocalInfoIds));

    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    storeHouseInventoryDTOMap.putAll(storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, salesReturnDTO.getStorehouseId(), productLocalInfoIds));
    Map<Long, ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
    StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, storehouseId);
    StoreHouseInventoryDTO storeHouseInventoryDTO = null;
    List<ProductDTO> notEnoughProducts = new ArrayList<ProductDTO>();
    for (SalesReturnItemDTO itemDTO : salesReturnDTO.getItemDTOs()) {
      storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTO.getProductId());
      if (storeHouseInventoryDTO == null) {
        storeHouseInventoryDTO = new StoreHouseInventoryDTO(storehouseId, itemDTO.getProductId(), 0d);
      }
      ProductDTO productDTO = productDTOMap.get(itemDTO.getProductId());
      if (productDTO != null) {
        double inventoryAmount = storeHouseInventoryDTO.getAmount();
        double itemAmount = itemDTO.getAmount() == null ? 0d : itemDTO.getAmount();
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {   //作废大单位单据
          itemAmount = itemAmount*productDTO.getRate();
        }
        if (itemAmount > inventoryAmount) {
          notEnoughProducts.add(productDTO);
        }
      }
    }
    if(CollectionUtils.isEmpty(notEnoughProducts)){
      return new Result(true);
    }else{
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("storehouse", storeHouseDTO);
      data.put("products", notEnoughProducts);
      return new Result("库存不足", false, Result.Operation.NOT_ENOUGH_INVENTORY.getValue(), data);
    }
  }

  @Override
  public List<SalesReturnDTO> getUnsettledSalesReturnDTOsByCustomerId(Long shopId, Long customerId) {
    List<SalesReturnDTO> salesReturnDTOs = new ArrayList<SalesReturnDTO>();
    if (shopId == null || customerId == null) {
      return salesReturnDTOs;
    }
    List<SalesReturn> salesReturns = txnDaoManager.getWriter().getUnsettledSalesReturnByCustomerId(shopId, customerId);
    for (SalesReturn salesReturn : salesReturns) {
      salesReturnDTOs.add(salesReturn.toDTO());
    }
    return salesReturnDTOs;
  }
  @Override
  public SalesReturnItemDTO getSalesReturnOrderItemDTOById(Long id) {
    if (id == null || id == 0L) {
      throw new RuntimeException("getSalesReturnOrderItemDTOByIdAndShopId,id is null or 0L.");
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    SalesReturnItem salesReturnItem = txnWriter.getById(SalesReturnItem.class,id);
    if (salesReturnItem == null) {
      return null;
    }
    return salesReturnItem.toDTO();
  }


}