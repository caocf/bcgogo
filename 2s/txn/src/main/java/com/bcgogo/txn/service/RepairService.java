package com.bcgogo.txn.service;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.cache.DataHolder;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.common.TwoTuple;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.constant.WashCardConstants;
import com.bcgogo.enums.*;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.dto.ProductVehicleDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.CarConstructionInvoiceSearchResultListDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.orderEvent.RepairOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.model.assistantStat.ServiceAchievementHistory;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-12
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
 */

@Component
public class RepairService implements IRepairService {
  private static final Logger LOG = LoggerFactory.getLogger(RepairService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IConsumingService consumingService;
  private IUserService userService;
  private ITxnService txnService;
  private ISearchService searchService;
  private RFITxnService rfiTxnService;
  private IProductService productService;
  private IConfigService configService;
  private IInventoryService inventoryService;
  private IServiceHistoryService serviceHistoryService;
  private IPickingService pickingService;
  private RepairOrderCostCaculator repairOrderCostCaculator;
  private IStoreHouseService storeHouseService;
  private IProductHistoryService productHistoryService;
  private ICustomerService customerService;

  public ICustomerService getCustomerService() {
    return customerService == null ? ServiceManager.getService(ICustomerService.class) : customerService;
  }

  public IUserService getUserService() {
    return userService == null ? ServiceManager.getService(IUserService.class) : userService;
  }

  public ITxnService getTxnService() {
    return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
  }

  public ISearchService getSearchService() {
    return searchService == null ? ServiceManager.getService(ISearchService.class) : searchService;
  }

  public RFITxnService getRfiTxnService() {
    return rfiTxnService == null ? ServiceManager.getService(RFITxnService.class) : rfiTxnService;
  }

  public IProductService getProductService() {
    return productService == null ? ServiceManager.getService(IProductService.class) : productService;
  }

  public IConfigService getConfigService() {
    return configService == null ? ServiceManager.getService(IConfigService.class) : configService;
  }

  public IInventoryService getInventoryService() {
    return inventoryService == null ? ServiceManager.getService(IInventoryService.class) : inventoryService;
  }

  public IServiceHistoryService getServiceHistoryService() {
    return serviceHistoryService == null ? ServiceManager.getService(IServiceHistoryService.class) : serviceHistoryService;
  }

  public IPickingService getPickingService() {
    return pickingService == null ? ServiceManager.getService(IPickingService.class) : pickingService;
  }

  public RepairOrderCostCaculator getRepairOrderCostCaculator() {
    return repairOrderCostCaculator == null ? ServiceManager.getService(RepairOrderCostCaculator.class) : repairOrderCostCaculator;
  }

  public IStoreHouseService getStoreHouseService() {
    return storeHouseService == null ? ServiceManager.getService(IStoreHouseService.class) : storeHouseService;
  }

  public IProductHistoryService getProductHistoryService() {
    return productHistoryService == null ? ServiceManager.getService(IProductHistoryService.class) : productHistoryService;
  }

  public IConsumingService getConsumingService() {
    return consumingService == null ? ServiceManager.getService(IConsumingService.class) : consumingService;
  }

  @Override
  public void handleProductForRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    //激活有删除的商品
//    getRfiTxnService().updateDeleteProductsByOrderDTO(repairOrderDTO);
    //保存或者更新商品信息（不包括txn，search）
//    getProductService().saveOrUpdateProductForRepairOrder(repairOrderDTO);
//    saveOrUpdateProductForRepairOrder(repairOrderDTO);

    //更新商品单位顺序
//    getConfigService().updateOrderUnitSort(repairOrderDTO.getShopId(), repairOrderDTO);
  }

  /**
   * @inheritDoc
   */
  @Override
  public Map<String, Service> getRepairOrderServiceByNames(Long shopId, boolean isIncludeDisabled, String... serviceNames) {
    Map<String, Service> serviceDTOMap = new HashMap<String, Service>();
    if (shopId == null || ArrayUtils.isEmpty(serviceNames)) {
      return serviceDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.getServiceByNames(shopId, isIncludeDisabled, serviceNames);
    if (CollectionUtils.isNotEmpty(services)) {
      for (Service service : services) {
        serviceDTOMap.put(service.getName(), service);
      }
    }
    return serviceDTOMap;
  }

  //派工
  @Override
  public void saveRepairOrderWithPicking(RepairOrderDTO repairOrderDTO) throws Exception {
    long current = System.currentTimeMillis();
    if (repairOrderDTO == null || !getUserService().isRepairPickingSwitchOn(repairOrderDTO.getShopId())) {
      return;
    }
    //   List<ItemIndex> itemIndexes = new ArrayList<ItemIndex>();
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    //处理营业分类
//    initBusinessCategoryRepairOrder(repairOrderDTO);
    //处理商品逻辑
//    handleProductForRepairOrder(repairOrderDTO);
    //保存施工人
//    setServiceWorksAndProductSaler(repairOrderDTO);
    //是否需要生成维修领料
    boolean isCreateRepairPicking = repairOrderDTO.isHaveItem();
    RepairPickingDTO repairPickingDTO = new RepairPickingDTO();
    if (isCreateRepairPicking) {
      repairPickingDTO.setReceiptNo(getTxnService().getReceiptNo(repairOrderDTO.getShopId(), OrderTypes.REPAIR_PICKING, null));
    }
    LOG.info("=======保存维修单--saveRepairOrderWithPicking--阶段4-1。执行时间: {} ms", System.currentTimeMillis() - current);
    current = System.currentTimeMillis();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      initRepairOrderService(writer, repairOrderDTO);
      //生成维修单
      RepairOrder repairOrder = new RepairOrder();
      repairOrder.fromDTO(repairOrderDTO);
      writer.save(repairOrder);
      repairOrderDTO.setId(repairOrder.getId());

      //保存ServiceItem
      if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
        for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
          if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
            continue;
          }
          RepairOrderService repairOrderService = new RepairOrderService();
          repairOrderService = repairOrderService.fromDTO(repairOrderServiceDTO, false);
          repairOrderService.setRepairOrderId(repairOrderDTO.getId());
          repairOrderService.setShopId(repairOrderDTO.getShopId());
          writer.save(repairOrderService);
          repairOrderServiceDTO.setId(repairOrderService.getId());
//          ItemIndex itemIndex = new ItemIndex();
//          itemIndex.setRepairOrderService(repairOrderDTO, repairOrderServiceDTO);
//          itemIndexes.add(itemIndex);
        }
      }
      //保存ProductItem
      saveOrUpdateRepairOrderItemsWithPicking(repairOrderDTO, writer, inventorySearchIndexList);

      //保存其他费用
      saveOrUpdateOtherIncomeItem(repairOrderDTO, writer);

      LOG.info("=======保存维修单--saveRepairOrderWithPicking--阶段4-2。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      if (isCreateRepairPicking) {
        //生成领料单
        Set<Long> productIds = new HashSet<Long>();
        Map<Long, ProductDTO> productDTOMap = null;
        for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
          if (repairOrderItemDTO.getProductId() != null) {
            productIds.add(repairOrderItemDTO.getProductId());
          }
        }
        productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
        repairPickingDTO.fromRepairDTO(repairOrderDTO);
        RepairPicking repairPicking = new RepairPicking();
        repairPicking.fromDTO(repairPickingDTO);
        repairPicking.setStatus(OrderStatus.PENDING);
        writer.save(repairPicking);
        repairPickingDTO.setId(repairPicking.getId());
        List<RepairPickingItemDTO> repairPickingItemDTOs = new ArrayList<RepairPickingItemDTO>();
        for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
          if (StringUtils.isBlank(repairOrderItemDTO.getProductName()) || repairOrderItemDTO.getProductId() == null) {
            continue;
          }
          ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
          RepairPickingItemDTO repairPickingItemDTO = new RepairPickingItemDTO(
            repairPickingDTO, repairOrderItemDTO, productDTO, OrderStatus.WAIT_OUT_STORAGE);
          repairPickingItemDTO.setProductDTO(productDTO);
          RepairPickingItem repairPickingItem = new RepairPickingItem();
          repairPickingItem.fromDTO(repairPickingItemDTO);
          writer.save(repairPickingItem);
          repairPickingItemDTO.setId(repairPickingItem.getId());
          repairPickingItemDTOs.add(repairPickingItemDTO);
        }
        repairPickingDTO.setTotalItemDTOs(repairPickingItemDTOs);
      }

      LOG.info("=======保存维修单--saveRepairOrderWithPicking--阶段4-3。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      writer.commit(status);
      LOG.info("=======保存维修单--saveRepairOrderWithPicking--阶段4-4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
        Set<Long> serviceIds = new HashSet<Long>();
        for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
          if (repairOrderServiceDTO.getServiceId() != null) {
            serviceIds.add(repairOrderServiceDTO.getServiceId());
          }
        }
        if (CollectionUtils.isNotEmpty(serviceIds)) {
          ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(repairOrderDTO.getShopId(), serviceIds);
        }
      }
      saveOrUpdateRepairRemindEventWithRepairPicking(repairOrderDTO);
      LOG.info("=======保存维修单--saveRepairOrderWithPicking--阶段4-5。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
//      if (StringUtils.isNotBlank(repairOrderDTO.getDraftOrderIdStr())) {
//        ServiceManager.getService(DraftOrderService.class).deleteDraftOrder(repairOrderDTO.getShopId(), Long.valueOf(repairOrderDTO.getDraftOrderIdStr()));
//      }
//      getSearchService().addOrUpdateItemIndexWithList(itemIndexes, null);
      ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(repairOrderDTO.getShopId(), inventorySearchIndexList);
      LOG.info("=======保存维修单--saveRepairOrderWithPicking--阶段4-6。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<String, CategoryDTO> initBusinessCategoryRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    Map<String, CategoryDTO> categoryDTOMap = new HashMap<String, CategoryDTO>();
    if (repairOrderDTO == null) {
      return categoryDTOMap;
    }
    Set<String> categoryNames = new HashSet<String>();
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (repairOrderServiceDTO == null || StringUtils.isEmpty(repairOrderServiceDTO.getBusinessCategoryName())) {
          continue;
        }
        categoryNames.add(repairOrderServiceDTO.getBusinessCategoryName());
      }
    }
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getBusinessCategoryName())) {
          continue;
        }
        categoryNames.add(repairOrderItemDTO.getBusinessCategoryName());
      }
    }
    if (CollectionUtils.isNotEmpty(categoryNames)) {
      categoryDTOMap = getRfiTxnService().getAndSaveCategoryDTOByNames(repairOrderDTO.getShopId(),
        CategoryType.BUSINESS_CLASSIFICATION, categoryNames.toArray(new String[categoryNames.size()]));
    }
    CategoryDTO categoryDTO = null;
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (repairOrderServiceDTO == null || StringUtils.isEmpty(repairOrderServiceDTO.getBusinessCategoryName())) {
          continue;
        }
        categoryDTO = categoryDTOMap.get(repairOrderServiceDTO.getBusinessCategoryName());
        if (categoryDTO != null) {
          repairOrderServiceDTO.setBusinessCategoryId(categoryDTO.getId());
        }
      }
    }
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getBusinessCategoryName())) {
          continue;
        }
        categoryDTO = categoryDTOMap.get(repairOrderItemDTO.getBusinessCategoryName());
        if (categoryDTO != null) {
          repairOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
        }
      }
    }
    return categoryDTOMap;
  }

  @Override
  public void updateRepairOrderWithPicking(RepairOrderDTO repairOrderDTO) throws Exception {
    long current = System.currentTimeMillis();
    if (repairOrderDTO == null || !getUserService().isRepairPickingSwitchOn(repairOrderDTO.getShopId())) {
      return;
    }
    //将预留还给领料单，再重新分配

    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();

    if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
      //   维修单item总金额和单据总金额
      getRepairOrderCostCaculator().calculate(repairOrderDTO);
    }
    RepairPickingDTO repairPickingDTO = getPickingService().getRepairPickingDTOSimpleByRepairId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
    boolean isNeedToCreateNewRepairPicking = getPickingService().isNeedToCreateNewRepairPicking(repairOrderDTO);
    boolean isNeedToUpdateRepairPicking = repairPickingDTO != null;
    if (isNeedToCreateNewRepairPicking) {
      repairPickingDTO = new RepairPickingDTO();
      repairPickingDTO.setReceiptNo(getTxnService().getReceiptNo(repairOrderDTO.getShopId(), OrderTypes.REPAIR_PICKING, null));
    }
    LOG.debug("=======保存维修单--updateRepairOrderWithPicking--阶段4-1。执行时间: {} ms", System.currentTimeMillis() - current);
    current = System.currentTimeMillis();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      initRepairOrderService(writer, repairOrderDTO);
      RepairOrder repairOrder = writer.getRepairOrderById(repairOrderDTO.getId(), repairOrderDTO.getShopId());
      repairOrder.fromDTO(repairOrderDTO);
      writer.update(repairOrder);

      //处理serviceItem
      List<RepairOrderService> lastRepairOrderServices = writer.getRepairOrderServicesByOrderId(repairOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(lastRepairOrderServices)) {
        for (RepairOrderService repairOrderService : lastRepairOrderServices) {
          writer.delete(repairOrderService);
        }
      }
      if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
        for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
          if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
            continue;
          }
          RepairOrderService repairOrderService = new RepairOrderService();
          repairOrderService = repairOrderService.fromDTO(repairOrderServiceDTO, false);
          repairOrderService.setRepairOrderId(repairOrderDTO.getId());
          repairOrderService.setShopId(repairOrderDTO.getShopId());
          writer.save(repairOrderService);
          repairOrderServiceDTO.setId(repairOrderService.getId());
        }
      }

      //保存其他费用
      saveOrUpdateOtherIncomeItem(repairOrderDTO, writer);
      //处理productItem
      //1，将之前预留值归还给领料单
      RepairPicking repairPicking = writer.getRepairPickingByRepairOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
      if (repairPicking == null && isNeedToCreateNewRepairPicking) {
        repairPickingDTO.fromRepairDTO(repairOrderDTO);
        repairPicking = new RepairPicking();
        repairPicking.fromDTO(repairPickingDTO);
        repairPicking.setStatus(OrderStatus.PENDING);
        writer.save(repairPicking);
      } else if (repairPicking != null) {
        repairPicking.setRepairOrderReceiptNo(repairOrderDTO.getReceiptNo());
        repairPicking.setProductSeller(repairOrderDTO.getProductSaler());
        repairPicking.setVestDate(repairOrderDTO.getSettleDate() == null ? System.currentTimeMillis() : repairOrderDTO.getSettleDate());
        writer.update(repairPicking);
      } else {
        repairPicking = new RepairPicking();
      }
      List<RepairOrderItem> oldRepairOrderItems = writer.getRepairOrderItemsByOrderId(repairOrderDTO.getId());
      Map<Long, RepairPickingItem> repairPickingItemMap = new HashMap<Long, RepairPickingItem>();
      if (isNeedToUpdateRepairPicking) {
        repairPickingItemMap = getPickingService().getPendingRepairPickingItemMap(repairPicking.getId());
      }

      Set<Long> productIds = new HashSet<Long>();
      Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
      if (CollectionUtils.isNotEmpty(oldRepairOrderItems)) {
        for (RepairOrderItem repairOrderItem : oldRepairOrderItems) {
          if (repairOrderItem.getProductId() != null) {
            productIds.add(repairOrderItem.getProductId());
          }
        }
      }
      if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
        for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
          if (repairOrderItemDTO.getProductId() != null) {
            productIds.add(repairOrderItemDTO.getProductId());
          }
        }
      }
      productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
      if (CollectionUtils.isNotEmpty(oldRepairOrderItems)) {
        for (RepairOrderItem repairOrderItem : oldRepairOrderItems) {
          if (repairOrderItem.getProductId() == null) {
            continue;
          }
          ProductDTO productDTO = productDTOMap.get(repairOrderItem.getProductId());
          double reservedWithSellUnit = NumberUtil.doubleVal(repairOrderItem.getReserved());
          if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productDTO)) {
            reservedWithSellUnit = reservedWithSellUnit * productDTO.getRate();
          }
          RepairPickingItem repairPickingItem = repairPickingItemMap.get(repairOrderItem.getProductId());
          if (repairPickingItem == null) {
            String unit = productDTO == null ? null : productDTO.getSellUnit();
            RepairPickingItemDTO repairPickingItemDTO = new RepairPickingItemDTO(
              repairPicking.getId(), repairOrderItem.getProductId(), reservedWithSellUnit, unit,
              OrderStatus.WAIT_RETURN_STORAGE);
            repairPickingItemDTO.setProductDTO(productDTO);
            repairPickingItem = new RepairPickingItem();
            repairPickingItem.fromDTO(repairPickingItemDTO);
          } else if (OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItem.getStatus())) {
            repairPickingItem.setAmount(reservedWithSellUnit);
            repairPickingItem.setStatus(OrderStatus.WAIT_RETURN_STORAGE);
          } else if (OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItem.getStatus())) {
            repairPickingItem.setAmount(repairPickingItem.getAmount() + reservedWithSellUnit);
          }
          repairPickingItemMap.put(repairOrderItem.getProductId(), repairPickingItem);
          writer.delete(repairOrderItem);
        }
      }
      LOG.debug("=======保存维修单--updateRepairOrderWithPicking--阶段4-2。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      //生成新的item
      List<RepairOrderItem> repairOrderItems = saveOrUpdateRepairOrderItemsWithPicking(repairOrderDTO, writer, inventorySearchIndexList);
      //重新分配出库单
      if (repairPickingItemMap != null && CollectionUtils.isNotEmpty(repairOrderItems)) {
        for (RepairOrderItem repairOrderItem : repairOrderItems) {
          RepairPickingItem repairPickingItem = repairPickingItemMap.get(repairOrderItem.getProductId());
          ProductDTO productDTO = productDTOMap.get(repairOrderItem.getProductId());
          if (repairPickingItem == null) {
            RepairPickingItemDTO repairPickingItemDTO = new RepairPickingItemDTO(repairPicking.toDTO(), repairOrderItem.toDTO(), productDTO, OrderStatus.WAIT_OUT_STORAGE);
            repairPickingItem = new RepairPickingItem();
            repairPickingItem.fromDTO(repairPickingItemDTO);
          } else if (OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItem.getStatus())) {
            double pickingItemAmount = NumberUtil.doubleVal(repairPickingItem.getAmount());
            double repairItemAmount = NumberUtil.doubleVal(repairOrderItem.getAmount());
            double pickingItemAmountWithItemUnit = pickingItemAmount;
            if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productDTO)) {
              repairItemAmount = repairItemAmount * productDTO.getRate();
              pickingItemAmountWithItemUnit = pickingItemAmountWithItemUnit / productDTO.getRate();
            }
            double toReturnAmount = pickingItemAmount - repairItemAmount;
            if (NumberUtil.isEqualOrGreater(pickingItemAmount, repairItemAmount)) {
              repairOrderItem.setReserved(repairOrderItem.getAmount());
            } else {
              repairOrderItem.setReserved(pickingItemAmountWithItemUnit);
            }
            writer.update(repairOrderItem);
            if (toReturnAmount > -0.0001) {
              repairPickingItem.setAmount(toReturnAmount);
            } else {
              repairPickingItem.setAmount(Math.abs(toReturnAmount));
              repairPickingItem.setStatus(OrderStatus.WAIT_OUT_STORAGE);
            }
          } else if (OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItem.getStatus())) {
            double repairItemAmount = NumberUtil.doubleVal(repairOrderItem.getAmount());
            if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productDTO)) {
              repairItemAmount = repairItemAmount * productDTO.getRate();
            }
            repairPickingItem.setAmount(NumberUtil.doubleVal(repairPickingItem.getAmount()) + repairItemAmount);
          }
          repairPickingItemMap.put(repairOrderItem.getProductId(), repairPickingItem);
        }
      }
      //更新RepairOrderItemDTO 预留
      if (CollectionUtils.isNotEmpty(repairOrderItems)) {
        for (RepairOrderItem repairOrderItem : repairOrderItems) {
          for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
            if (repairOrderItem.getId().equals(repairOrderItemDTO.getId())) {
              repairOrderItemDTO.setReserved(repairOrderItem.getReserved());
            }
          }
        }
      }
      //重新生成维修领料
      boolean isRepairPickingFinish = true;
      if (!repairPickingItemMap.isEmpty()) {
        for (RepairPickingItem repairPickingItem : repairPickingItemMap.values()) {
          if (repairPickingItem.getId() == null && Math.abs(NumberUtil.doubleVal(repairPickingItem.getAmount())) < 0.0001) {
            continue;
          } else if (Math.abs(NumberUtil.doubleVal(repairPickingItem.getAmount())) > 0.0001) {
            isRepairPickingFinish = false;
            writer.saveOrUpdate(repairPickingItem);
          } else {
            writer.delete(repairPickingItem);
          }
        }
      }
      //维修领料完结逻辑
      if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
        //如果是结算
        postProcessAfterUpdateRepairOrderStatus(writer, repairOrderDTO, false);
      }
      if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus()) && isRepairPickingFinish
        && repairPicking != null && repairPicking.getId() != null) {
        repairPicking.setStatus(OrderStatus.SETTLED);
        writer.update(repairPicking);
      }
      if (repairPicking != null && repairPicking.getId() != null) {
        List<RepairPickingItem> repairPickingItems = writer.getRepairPickingItemsByOrderIds(repairPicking.getId());
        if (CollectionUtils.isEmpty(repairPickingItems)) {
          writer.delete(repairPicking);
        }
      }
      LOG.debug("=======保存维修单--updateRepairOrderWithPicking--阶段4-3。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      writer.commit(status);
      LOG.debug("=======保存维修单--updateRepairOrderWithPicking--阶段4-4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();

      ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(repairOrderDTO.getShopId(), repairOrderDTO);
      saveOrUpdateRepairRemindEventWithRepairPicking(repairOrderDTO);
      LOG.debug("=======保存维修单--updateRepairOrderWithPicking--阶段4-5。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();

      ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(repairOrderDTO.getShopId(), inventorySearchIndexList);
      LOG.debug("=======保存维修单--updateRepairOrderWithPicking--阶段4-6。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result validateCopy(Long repairOrderId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    RepairOrder repairOrder = writer.getRepairOrderById(repairOrderId, shopId);
    if (repairOrder == null) {
      return new Result("无法复制", "单据不存在，无法复制！", false, Result.Operation.ALERT);
    }
    RepairOrderDTO repairOrderDTO = repairOrder.toDTO();
    CustomerDTO customerDTO = repairOrderDTO.generateCustomerDTO();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    boolean customerSame = customerService.compareCustomerSameWithHistory(customerDTO, shopId);

    VehicleDTO vehicleDTO = repairOrderDTO.generateVehicleDTO();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    boolean vehicleSame = vehicleService.compareVehicleSameWithHistory(vehicleDTO, shopId);

    List<RepairOrderItem> repairOrderItems = writer.getRepairOrderItemsByOrderId(repairOrder.getId());
    Map<Long, Long> localInfoIdAndHistoryIdMap = new HashMap<Long, Long>();
    if (CollectionUtils.isNotEmpty(repairOrderItems)) {
      for (RepairOrderItem item : repairOrderItems) {
        localInfoIdAndHistoryIdMap.put(item.getProductId(), item.getProductHistoryId());
      }
    }
    boolean productSame = productHistoryService.compareProductSameWithHistory(localInfoIdAndHistoryIdMap, shopId);

    List<RepairOrderService> serviceItems = writer.getRepairOrderServicesByOrderId(repairOrder.getId());
    Map<Long, Long> serviceIdAndHistoryIdMap = new HashMap<Long, Long>();
    if (CollectionUtils.isNotEmpty(serviceItems)) {
      for (RepairOrderService item : serviceItems) {
        serviceIdAndHistoryIdMap.put(item.getServiceId(), item.getServiceHistoryId());
      }
    }
    boolean serviceSame = serviceHistoryService.compareServiceSameWithHistory(serviceIdAndHistoryIdMap, shopId);

    if (customerSame && serviceSame && vehicleSame && productSame) {
      return new Result("通过校验", true);
    }
    StringBuffer sb = new StringBuffer("<ul>");
    if (!customerSame || !vehicleSame) {
      sb.append("<li>").append("客户/车辆信息").append("</li>");
    }
    if (!serviceSame) {
      sb.append("<li>").append("施工名称").append("</li>");
    }
    if (!productSame) {
      sb.append("<li>").append("商品信息").append("</li>");
    }
    sb.append("</ul>");
    return new Result("提示", "此单据中的以下信息已被修改，请确认是否继续复制。<br/>" + sb.toString() + "<br/>如果继续，已被修改过的信息将不会被复制。", false, Result.Operation.CONFIRM);
  }

  private void saveAchievementHistoryFromRepairService(Service service, RepairOrderDTO repairOrderDTO, TxnWriter txnWriter) {
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

    List<ServiceAchievementHistory> serviceAchievementHistoryList = assistantStatService.getLastedServiceAchievementHistory(repairOrderDTO.getShopId(), service.getId());

    ServiceAchievementHistory achievementHistory = null;
    if (CollectionUtils.isNotEmpty(serviceAchievementHistoryList)) {
      achievementHistory = serviceAchievementHistoryList.get(0);

      if (NumberUtil.isEqual(achievementHistory.getStandardHours(), NumberUtil.doubleVal(service.getStandardHours()))
        && NumberUtil.isEqual(achievementHistory.getStandardUnitPrice(), NumberUtil.doubleVal(service.getStandardUnitPrice()))
        ) {
        return;
      }
    }

    ServiceAchievementHistory serviceAchievementHistory = new ServiceAchievementHistory();
    serviceAchievementHistory.setShopId(service.getShopId());
    serviceAchievementHistory.setServiceId(service.getId());
    serviceAchievementHistory.setServiceName(service.getName());
    serviceAchievementHistory.setAchievementType(service.getAchievementType());
    serviceAchievementHistory.setAchievementAmount(service.getAchievementAmount());
    serviceAchievementHistory.setStandardHours(service.getStandardHours() == null ? (achievementHistory == null ? null : achievementHistory.getStandardHours()) : service.getStandardHours());
    serviceAchievementHistory.setStandardUnitPrice(service.getStandardUnitPrice() == null ? (achievementHistory == null ? null : achievementHistory.getStandardUnitPrice()) : service.getStandardUnitPrice());
    serviceAchievementHistory.setChangeTime(System.currentTimeMillis());
    serviceAchievementHistory.setChangeUserId(repairOrderDTO.getUserId());
    txnWriter.save(serviceAchievementHistory);

    assistantStatService.deleteShopAchievementConfig(repairOrderDTO.getShopId(), AssistantRecordType.SERVICE, service.getId(), txnWriter);
  }

  //保存service和营业分类关系
  private void initRepairOrderService(TxnWriter writer, RepairOrderDTO repairOrderDTO) {
    //保存service
    Set<String> serviceNames = new HashSet<String>();
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        serviceNames.add(repairOrderServiceDTO.getService());
      }
    }
    Map<String, Service> serviceMap = getRepairOrderServiceByNames(repairOrderDTO.getShopId(), true, serviceNames.toArray(new String[serviceNames.size()]));

    Set<Long> serviceIds = new HashSet<Long>();
    Map<Long, ServiceHistoryDTO> serviceHistoryMap = new HashMap<Long, ServiceHistoryDTO>();
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        Service service = serviceMap.get(repairOrderServiceDTO.getService());
        if (service != null) {
          if (repairOrderServiceDTO.getStandardHours() != null || repairOrderServiceDTO.getStandardUnitPrice() != null) {
            if (repairOrderServiceDTO.getStandardHours() != null) {
              service.setStandardHours(repairOrderServiceDTO.getStandardHours());
            }
            if (repairOrderServiceDTO.getStandardUnitPrice() != null) {
              service.setStandardUnitPrice(repairOrderServiceDTO.getStandardUnitPrice());
            }
            service.setPrice(NumberUtil.toReserve(NumberUtil.doubleVal(service.getStandardHours()) * NumberUtil.doubleVal(service.getStandardUnitPrice()), NumberUtil.PRECISION));
          }
          if (ServiceStatus.DISABLED.equals(service.getStatus())) {
            service.setStatus(ServiceStatus.ENABLED);
          }
          service.setUseTimes(NumberUtil.longValue(service.getUseTimes()) + 1);
          writer.update(service);
        } else {
          service = new Service(repairOrderDTO.getShopId(), repairOrderServiceDTO.getService(), repairOrderServiceDTO.getTotal(), ServiceStatus.ENABLED, repairOrderServiceDTO.getStandardHours(), repairOrderServiceDTO.getStandardUnitPrice());
          service.setPrice(NumberUtil.toReserve(NumberUtil.doubleVal(service.getStandardHours()) * NumberUtil.doubleVal(service.getStandardUnitPrice()), NumberUtil.PRECISION));
          service.setUseTimes(NumberUtil.longValue(service.getUseTimes()) + 1);
          writer.save(service);
        }
        if (repairOrderServiceDTO.getStandardHours() != null || repairOrderServiceDTO.getStandardUnitPrice() != null) {
          saveAchievementHistoryFromRepairService(service, repairOrderDTO, writer);
        }
        repairOrderServiceDTO.setServiceId(service.getId());
      }
    }
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (repairOrderServiceDTO.getServiceId() != null) {
          serviceIds.add(repairOrderServiceDTO.getServiceId());
        }
      }
    }
    //批量处理serviceHistory
    serviceHistoryMap = getServiceHistoryService().batchGetOrSaveServiceHistoryByServiceIds(writer, repairOrderDTO.getShopId(), serviceIds);
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (repairOrderServiceDTO.getServiceId() != null) {
          ServiceHistoryDTO serviceHistoryDTO = serviceHistoryMap.get(repairOrderServiceDTO.getServiceId());
          repairOrderServiceDTO.setServiceHistoryId(serviceHistoryDTO == null ? null : serviceHistoryDTO.getId());
        }
      }
    }

    //保存service和营业分类关系
    batchSaveOrUpdateCategoryItemRelation(writer, repairOrderDTO);
  }

  //保存service和营业分类关系
  private void batchSaveOrUpdateCategoryItemRelation(TxnWriter writer, RepairOrderDTO repairOrderDTO) {
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      Set<Long> serviceIds = repairOrderDTO.getServiceIds();
      Map<Long, List<CategoryItemRelation>> categoryItemRelationMap = getRfiTxnService().getCategoryItemRelationMapByServiceIds(serviceIds.toArray(new Long[serviceIds.size()]));
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (repairOrderServiceDTO.getServiceId() != null && repairOrderServiceDTO.getBusinessCategoryId() != null) {
          List<CategoryItemRelation> categoryItemRelations = categoryItemRelationMap.get(repairOrderServiceDTO.getServiceId());
          if (CollectionUtils.isNotEmpty(categoryItemRelations)) {
            for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
              if (!repairOrderServiceDTO.getBusinessCategoryId().equals(categoryItemRelation.getCategoryId())) {
                categoryItemRelation.setCategoryId(repairOrderServiceDTO.getBusinessCategoryId());
                writer.update(categoryItemRelation);
              }
            }
          } else {
            categoryItemRelations = new ArrayList<CategoryItemRelation>();
            CategoryItemRelation categoryItemRelation = new CategoryItemRelation(repairOrderServiceDTO.getBusinessCategoryId(), repairOrderServiceDTO.getServiceId());
            writer.save(categoryItemRelation);
            categoryItemRelations.add(categoryItemRelation);
          }
          categoryItemRelationMap.put(repairOrderServiceDTO.getServiceId(), categoryItemRelations);
        }
      }
    }
  }

  private List<RepairOrderItem> saveOrUpdateRepairOrderItemsWithPicking(RepairOrderDTO repairOrderDTO, TxnWriter writer,
                                                                        List<InventorySearchIndex> inventorySearchIndexList) throws Exception {
    List<RepairOrderItem> repairOrderItems = new ArrayList<RepairOrderItem>();
    Set<Long> productIds = new HashSet<Long>();
    Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>();
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
    Map<Long, ProductHistory> productHistoryMap = new HashMap<Long, ProductHistory>();

    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getProductName())) {
          continue;
        }
        productIds.add(repairOrderItemDTO.getProductId());
        repairOrderItemDTO.setRepairOrderId(repairOrderDTO.getId());
      }
      inventoryMap = getInventoryService().getInventoryMap(repairOrderDTO.getShopId(), productIds);
      productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
      productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(repairOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));

      //保存inventory ProductHistory
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getProductName())) {
          continue;
        }
        Inventory inventory = inventoryMap.get(repairOrderItemDTO.getProductId());
        ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductId());

        if (inventory == null) {
          inventory = new Inventory();
          inventory.setId(repairOrderItemDTO.getProductId());
          inventory.setShopId(repairOrderDTO.getShopId());
          inventory.setAmount(0d);
          if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
            getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer,
              new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, 0d));
          }
          if (productDTO != null) {
            inventory.setUnit(productDTO.getSellUnit());
          } else {
            inventory.setUnit(repairOrderItemDTO.getUnit());
          }
          writer.save(inventory);
          inventoryMap.put(repairOrderItemDTO.getProductId(), inventory);

          InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
          inventorySearchIndex.createInventorySearchIndex(inventory.toDTO(), productDTO);
          inventorySearchIndexList.add(inventorySearchIndex);
        }
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProductDTO(productDTO);
        productHistory.setProductLocalInfoDTO(productLocalInfoDTO);
        productHistory.setInventoryDTO(inventory.toDTO());
        productHistoryMap.put(repairOrderItemDTO.getProductId(), productHistory);
      }
      getProductHistoryService().batchSaveProductHistory(productHistoryMap, writer);

      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getProductName())) {
          continue;
        }
        ProductHistory productHistory = productHistoryMap.get(repairOrderItemDTO.getProductId());
        RepairOrderItem repairOrderItem = new RepairOrderItem();
        repairOrderItemDTO.setId(null);
        repairOrderItemDTO.setReserved(0d);
        repairOrderItemDTO.setShopId(repairOrderDTO.getShopId());
        if (productHistory != null) {
          repairOrderItemDTO.setProductHistoryId(productHistory.getId());
        }
        //只有结算的时候才保存成本价
        if (!OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
          //   维修单item成本总金额和单据总金额
          repairOrderItemDTO.setCostPrice(null);
          repairOrderItemDTO.setTotalCostPrice(null);
        }
        repairOrderItem.fromDTO(repairOrderItemDTO);
        writer.save(repairOrderItem);
        repairOrderItems.add(repairOrderItem);
        repairOrderItemDTO.setId(repairOrderItem.getId());
//        ItemIndex itemIndex = new ItemIndex();
//        itemIndex.setRepairOrderItem(repairOrderDTO, repairOrderItemDTO);
//        itemIndexes.add(itemIndex);

      }
    }
    return repairOrderItems;
  }

  private void updateRepairOrderItemsNoPicking(RepairOrderDTO repairOrderDTO, TxnWriter writer, Long oldStorehouseId, List<InventorySearchIndex> inventorySearchIndexes) throws Exception {
    List<RepairOrderItem> repairOrderItems = new ArrayList<RepairOrderItem>();
    Set<Long> productIds = new HashSet<Long>();
    //需要操作的数据块
    Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>();
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    Map<Long, ProductHistory> productHistoryMap = new HashMap<Long, ProductHistory>();
    if (inventorySearchIndexes == null) {
      inventorySearchIndexes = new ArrayList<InventorySearchIndex>();
    }
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getProductName())) {
          continue;
        }
        productIds.add(repairOrderItemDTO.getProductId());
        repairOrderItemDTO.setRepairOrderId(repairOrderDTO.getId());
        repairOrderItemDTO.setShopId(repairOrderDTO.getShopId());
      }
    }

    List<RepairOrderItem> oldRepairOrderItems = writer.getRepairOrderItemsByOrderId(repairOrderDTO.getId());
    Map<Long, RepairOrderItem> toUpdateRepairOrderItemMap = new HashMap<Long, RepairOrderItem>();
    Map<Long, RepairOrderItem> toDeleteRepairOrderItemMap = new HashMap<Long, RepairOrderItem>();
    //改单操作对以前的item分类   有仓库的版本还要检查仓库是否匹配
    if (CollectionUtils.isNotEmpty(oldRepairOrderItems)) {
      for (RepairOrderItem repairOrderItem : oldRepairOrderItems) {
        if (repairOrderItem.getProductId() != null) {
          productIds.add(repairOrderItem.getProductId());
        }
        boolean isToDelete = true;
        if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
          for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
            if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
              continue;
            }
            //itemDTO，item id相同的
            if (repairOrderItem.getId().equals(repairOrderItemDTO.getId())) {
              // 两个item的产品id相同
              if (repairOrderItem.getProductId() != null && repairOrderItem.getProductId().equals(repairOrderItemDTO.getProductId())) {
                //有仓库的版本判断仓库是否一样
                if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
                  if (repairOrderDTO.getStorehouseId() != null && repairOrderDTO.getStorehouseId().equals(oldStorehouseId)) {
                    toUpdateRepairOrderItemMap.put(repairOrderItem.getId(), repairOrderItem);
                    isToDelete = false;
                  }
                } else {
                  toUpdateRepairOrderItemMap.put(repairOrderItem.getId(), repairOrderItem);
                  isToDelete = false;
                }

              } else {
                repairOrderItemDTO.setId(null);
              }
              break;
            }

          }
        }
        if (isToDelete) {
          toDeleteRepairOrderItemMap.put(repairOrderItem.getId(), repairOrderItem);
        }
      }
    }
    //取出要操作的数据块
    inventoryMap = getInventoryService().getInventoryMap(repairOrderDTO.getShopId(), productIds);
    productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
    productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(repairOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));

    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
      storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(
        repairOrderDTO.getShopId(), repairOrderDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
    }
    //删除不存在的item
    if (MapUtils.isNotEmpty(toDeleteRepairOrderItemMap)) {
      for (RepairOrderItem repairOrderItem : toDeleteRepairOrderItemMap.values()) {
        ProductDTO productDTO = productDTOMap.get(repairOrderItem.getProductId());
        double toReturnInventoryAmount = NumberUtil.doubleVal(repairOrderItem.getReserved());
        if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productDTO)) {
          toReturnInventoryAmount = toReturnInventoryAmount * productDTO.getRate();
        }
        Inventory inventory = inventoryMap.get(repairOrderItem.getProductId());
        if (toReturnInventoryAmount > 0.0001) {
          if (inventory != null) {
            getInventoryService().caculateBeforeLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
            inventory.setAmount(inventory.getAmount() + toReturnInventoryAmount);
            writer.update(inventory);
            getInventoryService().caculateAfterLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
            if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
              StoreHouseInventoryDTO storeHouseInventoryDTO = getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer,
                new StoreHouseInventoryDTO(oldStorehouseId, repairOrderItem.getProductId(), null, toReturnInventoryAmount));
              storeHouseInventoryDTOMap.put(repairOrderItem.getProductId(), storeHouseInventoryDTO);
            }
          }
        }
        inventoryMap.put(repairOrderItem.getProductId(), inventory);
        if (inventory != null && productDTO != null) {
          InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
          inventorySearchIndex.createInventorySearchIndex(inventory.toDTO(), productDTO);
          inventorySearchIndexes.add(inventorySearchIndex);
        }
        writer.delete(repairOrderItem);
      }
    }
    //库存不存在的new出库存
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getProductName())) {
        continue;
      }
      Inventory inventory = inventoryMap.get(repairOrderItemDTO.getProductId());
      ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductId());

      if (inventory == null) {
        inventory = new Inventory();
        inventory.setId(repairOrderItemDTO.getProductId());
        inventory.setShopId(repairOrderDTO.getShopId());
        inventory.setAmount(0d);
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(
            writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, 0d));
          storeHouseInventoryDTOMap.put(repairOrderItemDTO.getProductId(), storeHouseInventoryDTO);
        }
        if (productDTO != null) {
          inventory.setUnit(productDTO.getSellUnit());
        } else {
          inventory.setUnit(repairOrderItemDTO.getUnit());
        }
        writer.save(inventory);
        inventoryMap.put(repairOrderItemDTO.getProductId(), inventory);
      }
      ProductHistory productHistory = new ProductHistory();
      productHistory.setInventoryDTO(inventory.toDTO());
      productHistory.setProductLocalInfoDTO(productLocalInfoDTO);
      productHistory.setProductDTO(productDTO);
      productHistoryMap.put(repairOrderItemDTO.getProductId(), productHistory);
    }
    getProductHistoryService().batchSaveProductHistory(productHistoryMap, writer);


    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      if (repairOrderItemDTO == null || StringUtils.isEmpty(repairOrderItemDTO.getProductName())) {
        continue;
      }
      Inventory inventory = inventoryMap.get(repairOrderItemDTO.getProductId());
      ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
      ProductHistory productHistory = productHistoryMap.get(repairOrderItemDTO.getProductId());
      if (productHistory != null) {
        repairOrderItemDTO.setProductHistoryId(productHistory.getId());
      }
      if (inventory != null) {
        getInventoryService().caculateBeforeLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
      }

      RepairOrderItem repairOrderItem = null;
      if (repairOrderItemDTO.getId() != null) {
        repairOrderItem = toUpdateRepairOrderItemMap.get(repairOrderItemDTO.getId());
      }
      double inventoryAmount = NumberUtil.doubleVal(inventory.getAmount());
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
        StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(repairOrderItemDTO.getProductId());
        inventoryAmount = storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount();
      }

      //更新已有的item
      if (repairOrderItem != null) {
        double preReservedAmount = NumberUtil.doubleVal(repairOrderItem.getReserved());
        double newItemAmount = NumberUtil.doubleVal(repairOrderItemDTO.getAmount());
        double inventoryChangeAmount = 0d;
        if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productDTO)) {
          preReservedAmount = preReservedAmount * productDTO.getRate();
        }
        if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productDTO)) {
          newItemAmount = newItemAmount * productDTO.getRate();
        }
        //新的item更新预留 和库存
        if (inventoryAmount + preReservedAmount + 0.0001 > newItemAmount) {
          inventoryChangeAmount = preReservedAmount - newItemAmount;
          repairOrderItemDTO.setReserved(repairOrderItemDTO.getAmount());
        } else {
          inventoryChangeAmount = preReservedAmount;
          repairOrderItemDTO.setReserved(0d);
        }
        repairOrderItem.fromDTO(repairOrderItemDTO);
        writer.update(repairOrderItem);
        inventory.setAmount(inventory.getAmount() + inventoryChangeAmount);
        if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus()) && NumberUtil.longValue(inventory.getLastSalesTime()) < repairOrderDTO.getVestDate()) {
          inventory.setLastSalesTime(repairOrderDTO.getVestDate());
        }
        writer.update(inventory);
        getInventoryService().caculateAfterLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
          getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer,
            new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, inventoryChangeAmount));
        }
        //处理新增的item逻辑
      } else {
        repairOrderItemDTO.setId(null);
        if (null != inventory) {
          repairOrderItem = new RepairOrderItem();
          double itemAmountWithSellUnit = NumberUtil.doubleVal(repairOrderItemDTO.getAmount());
          double inventoryMinusAmount = 0d;   //需要减掉库存的数量
          if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
            StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(repairOrderItemDTO.getProductId());
            inventoryAmount = NumberUtil.doubleVal(storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount());
          }
          if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productDTO)) {    //维修单商品单位为库存大单位
            itemAmountWithSellUnit = itemAmountWithSellUnit * productDTO.getRate();
          }

          if (itemAmountWithSellUnit < inventoryAmount + 0.0001) {
            inventoryMinusAmount = itemAmountWithSellUnit;
            inventory.setAmount(inventory.getAmount() - inventoryMinusAmount);
            repairOrderItemDTO.setReserved(NumberUtil.doubleVal(repairOrderItemDTO.getAmount()));
            if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus()) && NumberUtil.longValue(inventory.getLastSalesTime()) < NumberUtil.longValue(repairOrderDTO.getVestDate())) {
              inventory.setLastSalesTime(repairOrderDTO.getVestDate());
            }
            writer.update(inventory);
            if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
              StoreHouseInventoryDTO storeHouseInventoryDTO = ServiceManager.getService(IStoreHouseService.class).saveOrUpdateStoreHouseInventoryDTO(
                writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, inventoryMinusAmount * -1));
              storeHouseInventoryDTOMap.put(repairOrderItemDTO.getProductId(), storeHouseInventoryDTO);
            }
            getInventoryService().caculateAfterLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
          }
        }
      }
      if (inventory != null && productDTO != null) {
        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        inventorySearchIndex.createInventorySearchIndex(inventory.toDTO(), productDTO);
        inventorySearchIndexes.add(inventorySearchIndex);
      }

      //只有结算的时候才保存成本价
      if (!OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
        //   维修单item成本总金额和单据总金额
        repairOrderItemDTO.setCostPrice(null);
        repairOrderItemDTO.setTotalCostPrice(null);
      }
      repairOrderItem.fromDTO(repairOrderItemDTO);
      writer.saveOrUpdate(repairOrderItem);
      repairOrderItems.add(repairOrderItem);
      repairOrderItemDTO.setId(repairOrderItem.getId());
    }
  }

  private void saveRepairOrderItemsNoPicking(RepairOrderDTO repairOrderDTO, TxnWriter writer,
                                             List<InventorySearchIndex> inventorySearchIndexes) throws Exception {
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      Set<Long> productIds = repairOrderDTO.getProductIdSet();
      Map<Long, Inventory> inventoryMap = getInventoryService().getInventoryMap(repairOrderDTO.getShopId(), productIds);
      Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
        storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(
          repairOrderDTO.getShopId(), repairOrderDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
      }
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(repairOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      Map<Long, ProductHistory> productHistoryMap = new HashMap<Long, ProductHistory>();
      //保存之前没有的inventory
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
          continue;
        }
        ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
        Inventory inventory = inventoryMap.get(repairOrderItemDTO.getProductId());
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductId());
        if (productLocalInfoDTO != null && productLocalInfoDTO.getBusinessCategoryId() != null) {
          productLocalInfoDTO.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName());
        }
        if (inventory == null) {
          String unit = productDTO == null ? repairOrderItemDTO.getUnit() : productDTO.getSellUnit();
          inventory = getTxnService().saveInventoryAfterSaveNewProduct(repairOrderDTO.getShopId(), repairOrderItemDTO.getProductId(), 0, writer, unit);
          inventoryMap.put(repairOrderItemDTO.getProductId(), inventory);
        }
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProductDTO(productDTO);
        productHistory.setProductLocalInfoDTO(productLocalInfoDTO);
        productHistory.setInventoryDTO(inventory.toDTO());
        productHistoryMap.put(repairOrderItemDTO.getProductId(), productHistory);
      }

      getProductHistoryService().batchSaveProductHistory(productHistoryMap, writer);

      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
          continue;
        }
        repairOrderItemDTO.setShopId(repairOrderDTO.getShopId());
        repairOrderItemDTO.setRepairOrderId(repairOrderDTO.getId());
        ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
        Inventory inventory = inventoryMap.get(repairOrderItemDTO.getProductId());
        ProductHistory productHistory = productHistoryMap.get(repairOrderItemDTO.getProductId());
        RepairOrderItem repairOrderItem = new RepairOrderItem();
        repairOrderItemDTO.setReserved(0d);
        if (productHistory != null) {
          repairOrderItemDTO.setProductHistoryId(productHistory.getId());
        }
        //更新库存
        if (null != inventory) {
          double inventoryAmount = NumberUtil.doubleVal(inventory.getAmount());
          double itemAmountWithSellUnit = NumberUtil.doubleVal(repairOrderItemDTO.getAmount());
          double inventoryMinusAmount = 0d;
          if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
            StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(repairOrderItemDTO.getProductId());
            inventoryAmount = NumberUtil.doubleVal(storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount());
          }
          if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productDTO)) {    //维修单商品单位为库存大单位
            itemAmountWithSellUnit = itemAmountWithSellUnit * productDTO.getRate();
          }

          if (itemAmountWithSellUnit < inventoryAmount + 0.0001) {
            inventoryMinusAmount = itemAmountWithSellUnit;
            getInventoryService().caculateBeforeLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
            inventory.setAmount(inventory.getAmount() - inventoryMinusAmount);
            repairOrderItemDTO.setReserved(NumberUtil.doubleVal(repairOrderItemDTO.getAmount()));
            if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus()) && NumberUtil.longValue(inventory.getLastSalesTime()) < repairOrderDTO.getVestDate()) {
              inventory.setLastSalesTime(repairOrderDTO.getVestDate());
            }
            if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
              StoreHouseInventoryDTO storeHouseInventoryDTO = getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(
                writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, inventoryMinusAmount * -1));
              storeHouseInventoryDTOMap.put(repairOrderItemDTO.getProductId(), storeHouseInventoryDTO);
            }
            writer.update(inventory);
            inventoryMap.put(repairOrderItemDTO.getProductId(), inventory);
            getInventoryService().caculateAfterLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
          }
        }

        repairOrderItem.fromDTO(repairOrderItemDTO);
        //只有结算的时候才保存成本价
        if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
          repairOrderItem.setCostPrice(NumberUtil.doubleVal(repairOrderItemDTO.getCostPrice()));
          repairOrderItem.setTotalCostPrice(NumberUtil.doubleVal(repairOrderItemDTO.getTotalCostPrice()));
        } else {
          repairOrderItem.setCostPrice(0d);
          repairOrderItem.setCostPrice(0d);
        }
        writer.saveOrUpdate(repairOrderItem);
        repairOrderItemDTO.setId(repairOrderItem.getId());
        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        inventorySearchIndex.createInventorySearchIndex(inventory.toDTO(), productDTO);
        inventorySearchIndexes.add(inventorySearchIndex);
      }
    }
  }


  @Override
  public void saveOrUpdateRepairRemindEventWithRepairPicking(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO == null || repairOrderDTO.getShopId() == null || repairOrderDTO.getId() == null || repairOrderDTO.getStatus() == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByRepairOrderId(repairOrderDTO.getShopId(),
        repairOrderDTO.getId());
      List<RemindEvent> remindEvents = writer.getRemindEventByOrderId(RemindEventType.REPAIR, repairOrderDTO.getShopId(), repairOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(repairRemindEvents)) {
        for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
          writer.delete(repairRemindEvent);
        }
      }
      if (CollectionUtils.isNotEmpty(remindEvents)) {
        for (RemindEvent remindEvent : remindEvents) {
          if (!PlansRemindStatus.canceled.name().equals(remindEvent.getRemindStatus())) {
            remindEvent.setRemindStatus(PlansRemindStatus.canceled.name());
            writer.update(remindEvent);
          }
        }
      }
      //派单改单
      if (OrderStatus.REPAIR_DISPATCH.equals(repairOrderDTO.getStatus())
        || OrderStatus.REPAIR_DISPATCH.equals(repairOrderDTO.getStatus())) {
        if (!repairOrderDTO.isHaveItem()) {
          RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
          repairRemindEvent.createPendingRemindEvent(repairOrderDTO);
          repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);
          writer.save(repairRemindEvent);
          //保存到提醒总表
          getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
        } else {
          for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
            if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
              continue;
            }
            RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
            repairRemindEvent.createWaitOutStorageRemindEvent(repairOrderDTO, repairOrderItemDTO);
            if (NumberUtil.isEqualOrGreater(repairOrderItemDTO.getReserved(), repairOrderItemDTO.getAmount())) {
              repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.OUT_STORAGE);
            } else {
              repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.WAIT_OUT_STORAGE);
            }
            writer.save(repairRemindEvent);
            //保存到提醒总表
            getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
          }
        }
      } else if (OrderStatus.REPAIR_DONE.equals(repairOrderDTO.getStatus())) {             //完工
        RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
        repairRemindEvent.createPendingRemindEvent(repairOrderDTO);
        repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);
        writer.save(repairRemindEvent);
        //保存到提醒总表
        getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
      }
      //提醒数量，更新到缓存
      getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据施工单生成提醒事件
   *
   * @param repairOrderDTO
   * @throws Exception
   */
  @Override
  public void saveOrUpdateRepairRemindEventNoRepairPicking(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO == null || repairOrderDTO.getShopId() == null || repairOrderDTO.getId() == null || repairOrderDTO.getStatus() == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByRepairOrderId(repairOrderDTO.getShopId(),
        repairOrderDTO.getId());
      List<RemindEvent> remindEvents = writer.getRemindEventByOrderId(RemindEventType.REPAIR, repairOrderDTO.getShopId(), repairOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(repairRemindEvents)) {
        for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
          writer.delete(repairRemindEvent);
        }
      }
      if (CollectionUtils.isNotEmpty(remindEvents)) {
        for (RemindEvent remindEvent : remindEvents) {
          if (!PlansRemindStatus.canceled.name().equals(remindEvent.getRemindStatus())) {
            remindEvent.setRemindStatus(PlansRemindStatus.canceled.name());
            writer.update(remindEvent);
          }
        }
      }
      //派单改单
      if (OrderStatus.REPAIR_DISPATCH.equals(repairOrderDTO.getStatus())
        || OrderStatus.REPAIR_DISPATCH.equals(repairOrderDTO.getStatus())) {
        boolean isHaveLackItem = false;
        if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
          for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
            if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
              continue;
            }
            if (NumberUtil.isGreater(repairOrderItemDTO.getAmount(), repairOrderItemDTO.getReserved())) {
              RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
              repairRemindEvent.createWaitOutStorageRemindEvent(repairOrderDTO, repairOrderItemDTO);
              repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.LACK);
              writer.save(repairRemindEvent);
              //保存到提醒总表
              getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
              isHaveLackItem = true;
            }
          }
        }

        if (!isHaveLackItem) {
          RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
          repairRemindEvent.createPendingRemindEvent(repairOrderDTO);
          repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);
          writer.save(repairRemindEvent);
          //保存到提醒总表
          getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
        }
      } else if (OrderStatus.REPAIR_DONE.equals(repairOrderDTO.getStatus())) {             //完工
        RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
        repairRemindEvent.createPendingRemindEvent(repairOrderDTO);
        repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);
        writer.save(repairRemindEvent);
        //保存到提醒总表
        getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void repealRepairOrder(RepairOrderDTO repairOrderDTO, Long toStorehouseId) throws Exception {
    StopWatchUtil sw = new StopWatchUtil("RepairService.repealRepairOrder", "inventory");
    Long shopId = repairOrderDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<InventorySearchIndex> newInventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      if (getUserService().isRepairPickingSwitchOn(shopId)) {
        updateInventoryByRepealRepairOrderWithRepairPicking(repairOrderDTO, writer);
      } else {
        newInventorySearchIndexList = updateInventoryAndInventorySearchIndexByRepealedRepairOrderDTO(toStorehouseId, repairOrderDTO, writer);
      }

      sw.stopAndStart("receivable");
      getRfiTxnService().updateReceivable(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR, ReceivableStatus.REPEAL, writer);
      getRfiTxnService().updateDebtByRepealOrder(shopId, repairOrderDTO.getId(), repairOrderDTO.getCustomerId(), DebtStatus.REPEAL, writer);

      sw.stopAndStart("remind");
      writer.deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(shopId, repairOrderDTO.getId());
      //add by WLF 删除提醒总表中的提醒
      writer.cancelRemindEventByOrderId(RemindEventType.REPAIR, repairOrderDTO.getId());
      sw.stopAndStart("updateStatus");
      writer.updateRepairOrderStatus(shopId, repairOrderDTO.getId(), OrderStatus.REPAIR_REPEAL);

      repairOrderDTO.setStatus(OrderStatus.REPAIR_REPEAL);
      writer.commit(status);

      sw.stopAndStart("inventorySearchIndex");
      ServiceManager.getService(IInventoryService.class).addOrUpdateInventorySearchIndexWithList(repairOrderDTO.getShopId(), newInventorySearchIndexList);

      //add by WLF 更新缓存中维修美容提醒的数量
      getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, shopId);
    } catch (Exception e) {
      LOG.error("repairService.repealRepairOrder error", e);
      throw e;
    } finally {
      writer.rollback(status);
    }

    sw.stopAndStart("log, delete draft order");
    getRfiTxnService().saveRepealOrderByOrderIdAndOrderType(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR);
    //ad by WLF 保存施工单的作废日志
    ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
      new OperationLogDTO(shopId, repairOrderDTO.getEditorId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.INVALID));

    if (StringUtils.isNotBlank(repairOrderDTO.getDraftOrderIdStr())) {
      ServiceManager.getService(DraftOrderService.class).deleteDraftOrder(repairOrderDTO.getShopId(), Long.valueOf(repairOrderDTO.getDraftOrderIdStr()));
    }

    getUserService().rollBackMemberInfo(shopId, repairOrderDTO.getAccountMemberNo(), repairOrderDTO.getMemberAmount());
    sw.stopAndPrintLog();
  }

  @Override
  public RepairOrderDTO createRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    long current = System.currentTimeMillis();
    boolean enough = true;
    TxnWriter writer = txnDaoManager.getWriter();
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    Object status = writer.begin();
    try {
      if (repairOrderDTO == null) {
        return null;
      }
      addInventoryForSpecialShopVersion(repairOrderDTO, repairOrderDTO.getShopId(), repairOrderDTO.getShopVersionId(), writer);  //为小型店补充库存
      RepairOrderItemDTO itemDTOs[] = repairOrderDTO.getItemDTOs();
      RepairOrder repairOrder = new RepairOrder();
      repairOrder.fromDTO(repairOrderDTO);
      //结算的时候才设置成本价
      if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
        //   维修单item总金额和单据总金额
        getRepairOrderCostCaculator().calculate(repairOrderDTO);
        //   维修单总金额
        repairOrder.setTotalCostPrice(repairOrderDTO.getTotalCostPrice());
        repairOrder.setOtherTotalCostPrice(repairOrderDTO.getOtherTotalCostPrice());
      }

      //保存施工人
      setServiceWorksAndProductSaler(repairOrderDTO);
      repairOrder.setServiceWorker(repairOrderDTO.getServiceWorker());
      repairOrder.setProductSaler(repairOrderDTO.getProductSaler());
      LOG.info("=======保存维修单--create--阶段4-1。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      //保存维修单
      writer.save(repairOrder);
      repairOrderDTO.setId(repairOrder.getId());
      List<ItemIndex> itemIndexList = new ArrayList<ItemIndex>();
      //service, serviceHistory
      String serviceStr = createRepairOrderService(repairOrderDTO, repairOrder, writer, itemIndexList);

      LOG.info("=======保存维修单--create--阶段4-2。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      if (!ArrayUtils.isEmpty(itemDTOs)) {
        for (RepairOrderItemDTO repairOrderItemDTO : itemDTOs) {
          boolean isNP = repairOrderItemDTO.getProductId() == null;

          repairOrderItemDTO.setShopId(repairOrderDTO.getShopId());
          //过滤没用的空行
          Long parentProductId = null;
          Long productLocalInfoId = null;
          ProductLocalInfoDTO productLocalInfoDTO = new ProductLocalInfoDTO();
          if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
            continue;
          }
          //如果材料不存在
          if (null == repairOrderItemDTO.getProductId()) {
            //增加商品
            ProductDTO productDTO = new ProductDTO();
            boolean isNewProduct = addNewProduct(repairOrderDTO, repairOrderItemDTO, productDTO);
            productLocalInfoId = productDTO.getProductLocalInfoId();
            parentProductId = productDTO.getId();
            if (null != productLocalInfoId && isNewProduct) {
              getTxnService().saveInventoryAfterSaveNewProduct(productDTO.getShopId(), productLocalInfoId, 0, writer, productDTO.getSellUnit());
              if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
                storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), productLocalInfoId, 0d));
              }
            }
            if (null != productLocalInfoId && !isNewProduct) {
              getProductService().updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), productLocalInfoId, repairOrderItemDTO.getCommodityCode());
            }
            repairOrderItemDTO.setProductId(productLocalInfoId);
            productLocalInfoDTO = getProductService().getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
          } else {
            productLocalInfoId = repairOrderItemDTO.getProductId();
            addVehicleToProduct(repairOrderDTO, repairOrderItemDTO);        //多款车型的商品做了一次维修单之后新增一款关联车型
            productLocalInfoDTO = getProductService().getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
            if (productLocalInfoDTO != null) {
              parentProductId = productLocalInfoDTO.getProductId();
              getProductService().updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), productLocalInfoId, repairOrderItemDTO.getCommodityCode());
              getProductService().updateProductLocalInfoCategory(repairOrderDTO.getShopId(), productLocalInfoDTO.getId(), repairOrderItemDTO.getBusinessCategoryId());
            } else {
              LOG.error("cannot find ProductLocalInfo product id =" + repairOrderItemDTO.getProductId());
            }
            if (repairOrderItemDTO.getProductType() != null && repairOrderItemDTO.getProductType() == SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE) {
              ProductVehicleDTO productVehicleDTO = new ProductVehicleDTO();
              productVehicleDTO.setShopId(repairOrderDTO.getShopId());
              productVehicleDTO.setBrandId(repairOrderDTO.getBrandId());
              productVehicleDTO.setModelId(repairOrderDTO.getModelId());
              productVehicleDTO.setYearId(repairOrderDTO.getYearId());
              productVehicleDTO.setEngineId(repairOrderDTO.getEngineId());
              productVehicleDTO.setProductId(parentProductId);
              getProductService().createProductVehicle(productVehicleDTO);
            }
          }

          RepairOrderItem repairOrderItem = new RepairOrderItem();
          repairOrderItem.setReserved(0d);
          //save inventory   todo check unit
          Inventory inventory = writer.getInventoryByIdAndshopId(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
          if (null != inventory) {
            Double inventoryAmount = inventory.getAmount();
            if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
              StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId());
              inventoryAmount = storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount();
            }
            if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productLocalInfoDTO)) {    //维修单商品单位为库存大单位
              //当材料需求量>库存量的时候，产生缺料提醒，不要更新库存量；当需求量<=库存量的时候，更新库存
              if (repairOrderItemDTO.getAmount() <= inventoryAmount / productLocalInfoDTO.getRate() + 0.001) {
                //update inventory
                iInventoryService.caculateBeforeLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
                inventory.setAmount(inventory.getAmount() - repairOrderItemDTO.getAmount() * productLocalInfoDTO.getRate());
                repairOrderItem.setReserved(repairOrderItemDTO.getAmount());
                repairOrderItemDTO.setReserved(repairOrderItemDTO.getAmount());
                if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_SETTLED && NumberUtil.longValue(inventory.getLastSalesTime()) < repairOrderDTO.getVestDate()) {
                  inventory.setLastSalesTime(repairOrderDTO.getVestDate());
                }
                if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
                  storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, (repairOrderItemDTO.getAmount() * productLocalInfoDTO.getRate()) * -1));
                }
                writer.update(inventory);
                iInventoryService.caculateAfterLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
              } else {
                enough = false;
                createRepairRemindEvent(repairOrderDTO, repairOrder.getId(), writer, repairOrderItemDTO, serviceStr, RepairRemindEventTypes.LACK, inventory);
              }
            } else {                                                                                          //维修单商品单位为销售小单位
              //当材料需求量>库存量的时候，产生缺料提醒，不要更新库存量；当需求量<=库存量的时候，更新库存
              if (repairOrderItemDTO.getAmount() <= inventoryAmount + 0.001) {
                //update inventory
                iInventoryService.caculateBeforeLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
                inventory.setAmount(inventory.getAmount() - repairOrderItemDTO.getAmount());
                repairOrderItem.setReserved(repairOrderItemDTO.getAmount());
                repairOrderItemDTO.setReserved(repairOrderItemDTO.getAmount());
                if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_SETTLED && NumberUtil.longValue(inventory.getLastSalesTime()) < repairOrderDTO.getVestDate()) {
                  inventory.setLastSalesTime(repairOrderDTO.getVestDate());
                }
                if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
                  storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, repairOrderItemDTO.getAmount() * -1));
                }
                writer.update(inventory);
                iInventoryService.caculateAfterLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
              } else {
                enough = false;
                createRepairRemindEvent(repairOrderDTO, repairOrder.getId(), writer, repairOrderItemDTO, serviceStr, RepairRemindEventTypes.LACK, inventory);
              }
            }
          } else {
            LOG.error("cannot find inventory {}" + repairOrderItemDTO.getProductId());
          }
          repairOrderItem.setAmount(repairOrderItemDTO.getAmount());
          repairOrderItem.setMemo(repairOrderItemDTO.getMemo());
          repairOrderItem.setPrice(repairOrderItemDTO.getPrice());
          repairOrderItem.setTotal(repairOrderItemDTO.getTotal());
          repairOrderItem.setProductId(repairOrderItemDTO.getProductId());
          repairOrderItem.setRepairOrderId(repairOrder.getId());
          repairOrderItem.setShopId(repairOrderDTO.getShopId());
          //只有结算的时候才保存成本价
          if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
            repairOrderItem.setCostPrice(NumberUtil.doubleVal(repairOrderItemDTO.getCostPrice()));
            repairOrderItem.setTotalCostPrice(NumberUtil.doubleVal(repairOrderItemDTO.getTotalCostPrice()));
//                itemDTO.setTotalCostPrice(item.getTotalCostPrice());
//                itemDTO.setCostPrice(item.getCostPrice());
          }
          repairOrderItem.setBusinessCategoryId(repairOrderItemDTO.getBusinessCategoryId());
          repairOrderItem.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName());
          repairOrderItem.setUnit(repairOrderItemDTO.getUnit());     //add unit
          writer.save(repairOrderItem);
          repairOrderItemDTO.setId(repairOrderItem.getId());
          repairOrderItemDTO.setRepairOrderId(repairOrder.getId());
          addMaterialItemIndex(repairOrderDTO, repairOrderItemDTO, itemIndexList, isNP);
          if (inventory == null) {
            continue;
          }
          InventorySearchIndex inventorySearchIndex = getTxnService().createInventorySearchIndex(inventory, parentProductId);
          inventorySearchIndexList.add(inventorySearchIndex);
        }
      }

      //此处已过滤空行
      if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
        for (RepairOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
          if (null == orderOtherIncomeItemDTO.getId()) {
            RepairOrderOtherIncomeItem repairOrderOtherIncomeItem = new RepairOrderOtherIncomeItem(orderOtherIncomeItemDTO);
            repairOrderOtherIncomeItem.setShopId(repairOrderDTO.getShopId());
            repairOrderOtherIncomeItem.setOrderId(repairOrderDTO.getId());
            writer.save(repairOrderOtherIncomeItem);
            orderOtherIncomeItemDTO.setId(repairOrderOtherIncomeItem.getId());
            orderOtherIncomeItemDTO.setShopId(repairOrderDTO.getShopId());

          } else {
            RepairOrderOtherIncomeItem repairOrderOtherIncomeItem = writer.getById(RepairOrderOtherIncomeItem.class, orderOtherIncomeItemDTO.getId());
            repairOrderOtherIncomeItem.setMemo(orderOtherIncomeItemDTO.getMemo());
            repairOrderOtherIncomeItem.setName(orderOtherIncomeItemDTO.getName());
            repairOrderOtherIncomeItem.setPrice(orderOtherIncomeItemDTO.getPrice());
            writer.update(repairOrderOtherIncomeItem);
            orderOtherIncomeItemDTO.setShopId(repairOrderDTO.getShopId());
          }
          addOtherIncomeItemIndex(repairOrderDTO, orderOtherIncomeItemDTO, itemIndexList);
        }
      }

      if (enough && repairOrderDTO.getStatus() != OrderStatus.REPAIR_SETTLED) {
        //增加待交付提醒
        RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
        repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);
        repairRemindEvent.setShopId(repairOrderDTO.getShopId());
        if (repairOrderDTO.getRemindEventDTOs() != null && repairOrderDTO.getRemindEventDTOs().length > 0) {
          RepairRemindEventDTO[] repairRemindEventDTOs = repairOrderDTO.getRemindEventDTOs();
          if (repairRemindEventDTOs != null && repairRemindEventDTOs.length > 0) {
            StringBuffer productName = new StringBuffer();
            for (RepairRemindEventDTO r : repairRemindEventDTOs) {
              productName.append(r.getProductName());
            }
            repairRemindEvent.setProductName(productName.toString());
          }

        }
        repairRemindEvent.setRepairOrderId(repairOrder.getId());
        repairRemindEvent.setVehicleBrand(repairOrderDTO.getBrand());
        repairRemindEvent.setVehicleModel(repairOrderDTO.getModel());
        repairRemindEvent.setVehicleEngine(repairOrderDTO.getEngine());
        repairRemindEvent.setVehicleYear(repairOrderDTO.getYear());
        repairRemindEvent.setAmount(repairOrderDTO.getSettledAmount());
        repairRemindEvent.setCustomer(repairOrderDTO.getCustomerName());
        repairRemindEvent.setMobile(repairOrderDTO.getMobile());
        repairRemindEvent.setVehicle(repairOrderDTO.getVechicle());
        repairRemindEvent.setStartDateStr(repairOrderDTO.getStartDateStr());
        repairRemindEvent.setEndDateStr(repairOrderDTO.getEndDateStr());
        repairRemindEvent.setService(serviceStr);
        repairRemindEvent.setFinishTime(repairOrderDTO.getEndDate());
        //ToDo: set product names and services
        writer.save(repairRemindEvent);

        //add by WLF 保存到提醒总表
        getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
        //add by WLF 提醒数量，更新到缓存
        getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
      }
      repairOrderDTO.setId(repairOrder.getId());
      if ((repairOrder.getStatus() == TxnConstant.REPAIR_ORDER_STATUS_SETTLED) || repairOrder.getStatusEnum() == OrderStatus.REPAIR_SETTLED) {
        postProcessAfterUpdateRepairOrderStatus(writer, repairOrderDTO, true);
        if (repairOrderDTO.isHaveItem()) {
          ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(repairOrderDTO, OrderTypes.REPAIR, repairOrderDTO.getStatus(), writer, null);
        }
      }
      if (StringUtils.isNotBlank(repairOrderDTO.getDraftOrderIdStr())) {
        ServiceManager.getService(DraftOrderService.class).deleteDraftOrder(repairOrderDTO.getShopId(), Long.valueOf(repairOrderDTO.getDraftOrderIdStr()));
      }
      LOG.info("=======保存维修单--create--阶段4-3。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      writer.commit(status);
      LOG.info("=======保存维修单--create--阶段4-4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      searchService.addOrUpdateItemIndexWithList(itemIndexList, null);
      ServiceManager.getService(IInventoryService.class).addOrUpdateInventorySearchIndexWithList(repairOrderDTO.getShopId(), inventorySearchIndexList);
      LOG.info("=======保存维修单--create--阶段4-5。执行时间: {} ms", System.currentTimeMillis() - current);
      return repairOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public RepairOrderDTO RFCreateRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    long current = System.currentTimeMillis();
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    Object status = writer.begin();
    try {
      if (repairOrderDTO == null) {
        return null;
      }
      addInventoryForSpecialShopVersion(repairOrderDTO, repairOrderDTO.getShopId(), repairOrderDTO.getShopVersionId(), writer);  //为小型店补充库存

      //结算的时候才设置成本价  维修单item总金额和单据总金额
      if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
        getRepairOrderCostCaculator().calculate(repairOrderDTO);
      }
      //保存service，service和营业分类的关系
      initRepairOrderService(writer, repairOrderDTO);
      //保存维修单
      RepairOrder repairOrder = new RepairOrder();
      repairOrder.fromDTO(repairOrderDTO);
      writer.save(repairOrder);
      repairOrderDTO.setId(repairOrder.getId());


      // 保存service
      saveOrUpdateRepairOrderServiceItem(repairOrderDTO, writer);

      //保存其他费用
      saveOrUpdateOtherIncomeItem(repairOrderDTO, writer);

      //保存材料栏
      saveRepairOrderItemsNoPicking(repairOrderDTO, writer, inventorySearchIndexList);

      if ((repairOrder.getStatus() == TxnConstant.REPAIR_ORDER_STATUS_SETTLED) || repairOrder.getStatusEnum() == OrderStatus.REPAIR_SETTLED) {
        postProcessAfterUpdateRepairOrderStatus(writer, repairOrderDTO, true);
        if (repairOrderDTO.isHaveItem()) {
          ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(repairOrderDTO, OrderTypes.REPAIR, repairOrderDTO.getStatus(), writer, null);
        }
      }
      writer.commit(status);

      ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(repairOrderDTO.getShopId(), inventorySearchIndexList);

      //保存提醒事件
      saveOrUpdateRepairRemindEventNoRepairPicking(repairOrderDTO);

      //更新库存上下限告警
      getInventoryService().updateMemocacheLimitByInventoryLimitDTO(repairOrderDTO.getShopId(), repairOrderDTO.getInventoryLimitDTO());

      return repairOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  //改单之后重新countMemcachLimitInfo
  @Override
  public RepairOrderDTO updateRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    long current = System.currentTimeMillis();
    TxnWriter writer = txnDaoManager.getWriter();
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    List<ItemIndex> itemIndexList = new ArrayList<ItemIndex>();
    List<ItemIndex> itemIndexToDeleteList = new ArrayList<ItemIndex>();
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    Object status = writer.begin();
    try {
      addInventoryForSpecialShopVersion(repairOrderDTO, repairOrderDTO.getShopId(), repairOrderDTO.getShopVersionId(), writer);  //为小型店补充库存

      RepairOrder order = writer.getById(RepairOrder.class, repairOrderDTO.getId());
      Long oldStorehouseId = order.getStorehouseId();

      repairOrderDTO.setShopId(order.getShopId());
      repairOrderDTO.setVechicle(order.getVechicle());
      repairOrderDTO.setVechicleId(order.getVechicleId());
      order.fromDTO(repairOrderDTO);
      if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
        //设置每个orderItem总金额和order总金额
        getRepairOrderCostCaculator().calculate(repairOrderDTO);
        //设置原维修单总金额
        order.setTotalCostPrice(repairOrderDTO.getTotalCostPrice());
        order.setOtherTotalCostPrice(repairOrderDTO.getOtherTotalCostPrice());
      }

      //保存施工人
      setServiceWorksAndProductSaler(repairOrderDTO);
      order.setServiceWorker(repairOrderDTO.getServiceWorker());
      order.setProductSaler(repairOrderDTO.getProductSaler());

      LOG.info("=======保存维修单--update--阶段4-1。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      //更新原order
      writer.update(order);
      //  更新service
      String services = updateServices(repairOrderDTO, writer, itemIndexList, itemIndexToDeleteList);
      updateOtherIncomeItem(repairOrderDTO, writer, itemIndexList, itemIndexToDeleteList);
      //没有缺料的情况下更新改单之后的服务内容的提醒事件   有缺料的时候要删除待交付
      List<RepairRemindEvent> events = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
      if (events != null && events.size() > 0) {
        for (RepairRemindEvent event : events) {
          event.setService(services);
          writer.update(event);
          List<RemindEventDTO> remindEventDTOList = getTxnService().getRemindEventListByOrderIdAndObjectIdAndEventStatus(RemindEventType.REPAIR, event.getRepairOrderId(), event.getProductId(), RepairRemindEventTypes.PENDING.toString());
          if (CollectionUtil.isNotEmpty(remindEventDTOList)) {
            remindEventDTOList.get(0).setService(services);
            getTxnService().updateRemindEvent(remindEventDTOList.get(0));
          }
        }
      }
      //modifyExistingItems ,createNewItems中跨库提交了一个事务，故在modifyExistingItems中独立为writer开事务。

      LOG.info("=======保存维修单--update--阶段4-2。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      modifyExistingItems(oldStorehouseId, repairOrderDTO, writer, services, itemIndexList, inventorySearchIndexList, itemIndexToDeleteList);
      createNewItems(repairOrderDTO, writer, services, itemIndexList, inventorySearchIndexList);

      LOG.info("=======保存维修单--update--阶段4-3。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      repairOrderDTO.setId(order.getId());
      if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
        //如果是结算
        postProcessAfterUpdateRepairOrderStatus(writer, repairOrderDTO, false);
        //保存出入库详情
        if (repairOrderDTO.isHaveItem()) {
          ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(repairOrderDTO, OrderTypes.REPAIR, repairOrderDTO.getStatus(), writer, null);
        }
      } else if (OrderStatus.REPAIR_DISPATCH.equals(repairOrderDTO.getStatus())) {     //改单的提醒事项 \
        boolean enough = true;
        RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
        if (!ArrayUtils.isEmpty(itemDTOs)) {
          for (RepairOrderItemDTO itemDTO : itemDTOs) {
            if (StringUtils.isBlank(itemDTO.getProductName())) {
              continue;
            }
            if (itemDTO.getReserved() < itemDTO.getAmount() - 0.0001) {
              enough = false;
              break;
            }
          }
        }
        if (enough) {
          RepairRemindEvent repairRemindEvent;
          List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
            repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
          if (CollectionUtils.isNotEmpty(repairRemindEvents)) {
            repairRemindEvent = repairRemindEvents.get(0);
          } else {
            repairRemindEvent = new RepairRemindEvent();
          }
          //增加待交付提醒
          repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);
          repairRemindEvent.setShopId(repairOrderDTO.getShopId());
          if (repairOrderDTO.getRemindEventDTOs() != null && repairOrderDTO.getRemindEventDTOs().length > 0) {
            RepairRemindEventDTO[] repairRemindEventDTOs = repairOrderDTO.getRemindEventDTOs();
            if (repairRemindEventDTOs != null && repairRemindEventDTOs.length > 0) {
              StringBuffer productName = new StringBuffer();
              for (RepairRemindEventDTO r : repairRemindEventDTOs) {
                productName.append(r.getProductName());
              }
              repairRemindEvent.setProductName(productName.toString());
            }
          }
          repairRemindEvent.setRepairOrderId(repairOrderDTO.getId());
          repairRemindEvent.setVehicleBrand(repairOrderDTO.getBrand());
          repairRemindEvent.setVehicleModel(repairOrderDTO.getModel());
          repairRemindEvent.setVehicleEngine(repairOrderDTO.getEngine());
          repairRemindEvent.setVehicleYear(repairOrderDTO.getYear());
          repairRemindEvent.setAmount(repairOrderDTO.getSettledAmount());
          repairRemindEvent.setCustomer(repairOrderDTO.getCustomerName());
          repairRemindEvent.setMobile(repairOrderDTO.getMobile());
          repairRemindEvent.setVehicle(repairOrderDTO.getVechicle());
          repairRemindEvent.setStartDateStr(repairOrderDTO.getStartDateStr());
          repairRemindEvent.setEndDateStr(repairOrderDTO.getEndDateStr());
          repairRemindEvent.setService(services);
          repairRemindEvent.setFinishTime(repairOrderDTO.getEndDate());
          writer.saveOrUpdate(repairRemindEvent);

          //add by WLF 保存到提醒总表
          getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
          //add by WLF 提醒数量，更新到缓存
          getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
        }
      } else if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_DONE) {
        //如果完工，删除来料待修的提醒
        writer.deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
        getTxnService().cancelRemindEventByOrderIdAndStatus(RemindEventType.REPAIR, repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);

        RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
        repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);

        repairRemindEvent.setShopId(repairOrderDTO.getShopId());
        repairRemindEvent.setRepairOrderId(repairOrderDTO.getId());
        repairRemindEvent.setVehicleBrand(repairOrderDTO.getBrand());
        repairRemindEvent.setVehicleModel(repairOrderDTO.getModel());
        repairRemindEvent.setVehicleEngine(repairOrderDTO.getEngine());
        repairRemindEvent.setVehicleYear(repairOrderDTO.getYear());
        repairRemindEvent.setAmount(repairOrderDTO.getSettledAmount());
        repairRemindEvent.setCustomer(repairOrderDTO.getCustomerName());
        repairRemindEvent.setMobile(repairOrderDTO.getMobile());
        repairRemindEvent.setVehicle(repairOrderDTO.getVechicle());
        repairRemindEvent.setStartDateStr(repairOrderDTO.getStartDateStr());
        repairRemindEvent.setEndDateStr(repairOrderDTO.getEndDateStr());
        repairRemindEvent.setFinishTime(repairOrderDTO.getEndDate());
        if (repairOrderDTO.getItemDTOs() != null && repairOrderDTO.getItemDTOs().length > 0) {
          RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
          if (repairOrderItemDTOs != null && repairOrderItemDTOs.length > 0) {
            String productName = "";
            for (RepairOrderItemDTO r : repairOrderItemDTOs) {
              productName = productName + r.getProductName();
            }
            repairRemindEvent.setProductName(productName);
          }

        }
        writer.deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId());
        writer.cancelRemindEventByOrderId(RemindEventType.REPAIR, repairOrderDTO.getId());

        //ToDo: set product names and services
        writer.save(repairRemindEvent);

        //add by WLF 保存到提醒总表
        getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
        //add by WLF 提醒数量，更新到缓存
        getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
      }
      LOG.info("=======保存维修单--update--阶段4-4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();

      if (StringUtils.isNotBlank(repairOrderDTO.getDraftOrderIdStr())) {
        ServiceManager.getService(DraftOrderService.class).deleteDraftOrder(repairOrderDTO.getShopId(), Long.valueOf(repairOrderDTO.getDraftOrderIdStr()));
      }
      writer.commit(status);
      LOG.info("=======保存维修单--update--阶段4-5。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      iInventoryService.saveMemcacheLimitDTOFromDB(repairOrderDTO.getShopId());
      searchService.addOrUpdateItemIndexWithList(itemIndexList, itemIndexToDeleteList);
      ServiceManager.getService(IInventoryService.class).addOrUpdateInventorySearchIndexWithList(order.getShopId(), inventorySearchIndexList);
      LOG.info("=======保存维修单--update--阶段4-6。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      return repairOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public RepairOrderDTO RFUpdateRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    long current = System.currentTimeMillis();
    TxnWriter writer = txnDaoManager.getWriter();
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    Object status = writer.begin();
    try {
      addInventoryForSpecialShopVersion(repairOrderDTO, repairOrderDTO.getShopId(), repairOrderDTO.getShopVersionId(), writer);  //为小型店补充库存

      //保存service，service和营业分类的关系
      initRepairOrderService(writer, repairOrderDTO);

      RepairOrder order = writer.getById(RepairOrder.class, repairOrderDTO.getId());
      Long oldStorehouseId = order.getStorehouseId();
      repairOrderDTO.setShopId(order.getShopId());
      repairOrderDTO.setVechicle(order.getVechicle());
      repairOrderDTO.setVechicleId(order.getVechicleId());
      repairOrderDTO.setAppointOrderId(order.getAppointOrderId());
      repairOrderDTO.setAppUserNo(order.getAppUserNo());
      if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
        //设置每个orderItem总金额和order总金额
        getRepairOrderCostCaculator().calculate(repairOrderDTO);
      }
      //更新原order
      order.fromDTO(repairOrderDTO);
      writer.update(order);
      //  更新service
      deleteRepairOrderServiceItem(repairOrderDTO, writer);
      saveOrUpdateRepairOrderServiceItem(repairOrderDTO, writer);

      //更新其他费用
      saveOrUpdateOtherIncomeItem(repairOrderDTO, writer);

      //更新repairOrderItem
      updateRepairOrderItemsNoPicking(repairOrderDTO, writer, oldStorehouseId, inventorySearchIndexList);

      if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
        //如果是结算
        postProcessAfterUpdateRepairOrderStatus(writer, repairOrderDTO, false);

        //保存出入库详情
        if (repairOrderDTO.isHaveItem()) {
          ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(repairOrderDTO, OrderTypes.REPAIR, repairOrderDTO.getStatus(), writer, null);
        }
      }
      writer.commit(status);
      iInventoryService.updateMemocacheLimitByInventoryLimitDTO(repairOrderDTO.getShopId(), repairOrderDTO.getInventoryLimitDTO());
      ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(repairOrderDTO.getShopId(), inventorySearchIndexList);
      //保存提醒事件
      saveOrUpdateRepairRemindEventNoRepairPicking(repairOrderDTO);
      return repairOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  private void saveOrUpdateRepairOrderServiceItem(RepairOrderDTO repairOrderDTO, TxnWriter writer) {
    if (repairOrderDTO.getId() != null && repairOrderDTO.getShopId() != null && !ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (StringUtils.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        RepairOrderService repairOrderService = new RepairOrderService();
        repairOrderService = repairOrderService.fromDTO(repairOrderServiceDTO, false);
        repairOrderService.setRepairOrderId(repairOrderDTO.getId());
        repairOrderService.setShopId(repairOrderDTO.getShopId());
        writer.save(repairOrderService);
        repairOrderServiceDTO.setId(repairOrderService.getId());
      }
    }
  }

  private void deleteRepairOrderServiceItem(RepairOrderDTO repairOrderDTO, TxnWriter writer) {
    if (repairOrderDTO.getId() != null && repairOrderDTO.getShopId() != null) {
      List<RepairOrderService> lastRepairOrderServices = writer.getRepairOrderServicesByOrderId(repairOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(lastRepairOrderServices)) {
        for (RepairOrderService repairOrderService : lastRepairOrderServices) {
          writer.delete(repairOrderService);
        }
      }
    }
  }

  private void addInventoryForSpecialShopVersion(RepairOrderDTO repairOrderDTO, Long shopId, Long shopVersionId, TxnWriter writer) throws Exception {
    if (shopVersionId == null || shopId == null ||
      !BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId) || repairOrderDTO == null) {
      return;
    }
    RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
    if (itemDTOs == null) {
      return;
    }
    Set<Long> productIds = new HashSet<Long>();
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    for (RepairOrderItemDTO itemDTO : itemDTOs) {
      if (itemDTO == null || StringUtils.isBlank(itemDTO.getProductName())) {
        continue;
      }
      if (itemDTO.getProductId() != null) {
        productIds.add(itemDTO.getProductId());
      }
    }
    Map<Long, Inventory> inventoryMap = getInventoryService().getInventoryMap(shopId, productIds);
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(
      shopId, productIds.toArray(new Long[productIds.size()]));

    for (RepairOrderItemDTO itemDTO : itemDTOs) {
      if (itemDTO == null || StringUtils.isBlank(itemDTO.getProductName())) {
        continue;
      }
      double supplierInventoryAmountChange = 0d;
      String supplierInventoryUnit = null;
      if (itemDTO.getProductId() != null) {
        Inventory inventory = inventoryMap.get(itemDTO.getProductId());
        if (inventory == null) {
          inventory = new Inventory();
          inventory.setId(itemDTO.getProductId());
          inventory.setShopId(shopId);
          inventory.setAmount(itemDTO.getAmount());
          inventory.setNoOrderInventory(itemDTO.getAmount());
          inventory.setUnit(itemDTO.getUnit());
          supplierInventoryAmountChange = NumberUtil.doubleVal(itemDTO.getAmount());
          supplierInventoryUnit = itemDTO.getUnit();
          writer.save(inventory);
          inventoryMap.put(inventory.getId(), inventory);
        } else {
          ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(itemDTO.getProductId());
          double amount = NumberUtil.doubleVal(itemDTO.getAmount());
          double reservedAmount = NumberUtil.doubleVal(itemDTO.getReserved());
          double inventoryAmount = inventory.getAmount();
          if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
            amount = amount * productLocalInfoDTO.getRate();
            reservedAmount = reservedAmount * productLocalInfoDTO.getRate();
          }
          if (amount > inventoryAmount + reservedAmount - 0.0001) {
            inventory.setAmount(amount - reservedAmount);
            inventory.setNoOrderInventory(NumberUtil.doubleVal(inventory.getNoOrderInventory()) + amount - inventoryAmount - reservedAmount);
            supplierInventoryAmountChange = amount - inventoryAmount - reservedAmount;
            supplierInventoryUnit = inventory.getUnit();
            writer.update(inventory);
          }
        }
      }
      if (supplierInventoryAmountChange > 0.001) {
        SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
        supplierInventoryDTO.setShopId(shopId);
        supplierInventoryDTO.setProductId(itemDTO.getProductId());
        supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        supplierInventoryDTO.setChangeAmount(supplierInventoryAmountChange);
        supplierInventoryDTO.setUnit(supplierInventoryUnit);
        supplierInventoryDTOs.add(supplierInventoryDTO);

      }
    }
    if (CollectionUtils.isNotEmpty(supplierInventoryDTOs)) {
      productThroughService.saveOrUpdateSupplierInventory(writer, supplierInventoryDTOs);
    }
  }

  /**
   * 遍历每个service,把施工人保存到repairOrderDTO中，以满足现有的员工业绩统计
   *
   * @param repairOrderDTO
   */
  @Override
  public void setServiceWorksAndProductSaler(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO == null || repairOrderDTO.getServiceDTOs() == null ||
      repairOrderDTO.getServiceDTOs().length <= 0) {
      return;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    //施工人
    Set<String> allWorkers = new LinkedHashSet<String>();  //所有施工人
    //批量获取施工人，销售人信息
    Set<String> allSalesManNames = new HashSet<String>();
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService()) || StringUtil.isEmpty(repairOrderServiceDTO.getWorkers())) {
          continue;
        }
        repairOrderServiceDTO.setWorkers(repairOrderServiceDTO.getWorkers().replace("，", ","));
        for (String itemWorker : repairOrderServiceDTO.getWorkers().split(",")) {
          if (StringUtils.isBlank(itemWorker)) {
            continue;
          }
          allSalesManNames.add(itemWorker.toLowerCase());
        }
      }
    }
    if (StringUtils.isNotBlank(repairOrderDTO.getProductSaler())) {
      repairOrderDTO.setProductSaler(repairOrderDTO.getProductSaler().replace("，", ","));
      for (String saler : repairOrderDTO.getProductSaler().split(",")) {
        if (StringUtils.isBlank(saler)) {
          continue;
        }
        allSalesManNames.add(saler.trim().toLowerCase());
      }
    }
    Map<String, SalesManDTO> salesManDTOMap = userService.getSalesManDTOMap(repairOrderDTO.getShopId(), allSalesManNames);

    for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
      if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
        continue;
      }
      if (StringUtil.isEmpty(repairOrderServiceDTO.getWorkers())) {
        continue;
      }
      Set<String> itemWorkersSet = new LinkedHashSet<String>();
      Set<Long> itemWorkerIdsSet = new LinkedHashSet<Long>();
      //遍历以逗号分隔的工人,如果没有就新增, 有的话setId到workerIds, 顺便去重.

      repairOrderServiceDTO.setWorkers(repairOrderServiceDTO.getWorkers().replace("，", ","));
      for (String itemWorker : repairOrderServiceDTO.getWorkers().split(",")) {
        if (StringUtils.isBlank(itemWorker)) {
          continue;
        }
        itemWorker = itemWorker.trim().toLowerCase();
        SalesManDTO salesManDTO = salesManDTOMap.get(itemWorker);
        if (salesManDTO != null) {
          itemWorkerIdsSet.add(salesManDTO.getId());
        } else {
          SalesManDTO newSalesMan = new SalesManDTO();
          newSalesMan.setName(itemWorker);
          newSalesMan.setShopId(repairOrderDTO.getShopId());
          newSalesMan.setStatus(SalesManStatus.ONTRIAL);
          newSalesMan.setDepartmentName(SalesManDTO.defaultEmptyDepartment);
          userService.saveOrUpdateSalesMan(newSalesMan);
          itemWorkerIdsSet.add(newSalesMan.getId());
          salesManDTOMap.put(itemWorker, newSalesMan);

        }
        itemWorkersSet.add(itemWorker);
        allWorkers.add(itemWorker);
      }
      String commaWorkers = CollectionUtil.collectionToCommaString(itemWorkersSet);
      String commaWorkerIds = CollectionUtil.collectionToCommaString(itemWorkerIdsSet);
      repairOrderServiceDTO.setWorkers(commaWorkers);
      repairOrderServiceDTO.setWorkerIds(commaWorkerIds);

    }
    if (CollectionUtils.isNotEmpty(allWorkers)) {
      repairOrderDTO.setServiceWorker(CollectionUtil.collectionToCommaString(allWorkers));
    } else {
      repairOrderDTO.setServiceWorker(TxnConstant.ASSISTANT_NAME);
    }

    //productSaler遍历, 没有的新增, 顺便去重
    if (StringUtils.isNotBlank(repairOrderDTO.getProductSaler())) {
      repairOrderDTO.setProductSaler(repairOrderDTO.getProductSaler().replace("，", ","));
      Set<String> salersSet = new LinkedHashSet<String>();
      Set<Long> salersIds = new LinkedHashSet<Long>();

      for (String saler : repairOrderDTO.getProductSaler().split(",")) {
        if (StringUtils.isBlank(saler)) {
          continue;
        }
        saler = saler.trim().toLowerCase();
        SalesManDTO salesManDTO = salesManDTOMap.get(saler);
        if (salesManDTO == null) {
          SalesManDTO newSalesMan = new SalesManDTO();
          newSalesMan.setName(saler);
          newSalesMan.setShopId(repairOrderDTO.getShopId());
          newSalesMan.setStatus(SalesManStatus.ONTRIAL);
          newSalesMan.setDepartmentName(SalesManDTO.defaultEmptyDepartment);
          userService.saveOrUpdateSalesMan(newSalesMan);
          salersIds.add(newSalesMan.getId());
        } else {
          salersIds.add(salesManDTO.getId());
        }
        salersSet.add(saler);
      }
      if (CollectionUtils.isNotEmpty(salersSet)) {
        repairOrderDTO.setProductSaler(CollectionUtil.collectionToCommaString(salersSet));
        repairOrderDTO.setProductSalerIds(CollectionUtil.collectionToCommaString(salersIds));
      } else {
        repairOrderDTO.setProductSaler(TxnConstant.ASSISTANT_NAME);
        repairOrderDTO.setProductSalerIds(null);
      }
    } else {
      repairOrderDTO.setProductSaler(TxnConstant.ASSISTANT_NAME);
    }
  }

  private String createRepairOrderService(RepairOrderDTO repairOrderDTO, RepairOrder repairOrder, TxnWriter writer,
                                          List<ItemIndex> itemIndexList) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    RepairOrderServiceDTO repairOrderServiceDTOs[] = repairOrderDTO.getServiceDTOs();
    StringBuffer serviceStr = new StringBuffer();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Set<Long> set = new HashSet<Long>();
    if (repairOrderServiceDTOs != null) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
        //过滤没用的空行
        if (StringUtils.isNotEmpty(repairOrderServiceDTO.getService())) {
          //判断是否加入service表中
          //add new repairOrderService

          //判断是否加入service表中 新加的施工服务类型只能是现金
          Service serviceByName = rfiTxnService.getRFServiceByServiceNameAndShopId(repairOrderDTO.getShopId(), repairOrderServiceDTO.getService());
          if (null == serviceByName) {
            Service newService = createServiceByRepairOrder(writer, repairOrderDTO.getShopId(), repairOrderServiceDTO);
            repairOrderServiceDTO.setServiceId(newService.getId());
            set.add(newService.getId());
          } else {
            if (ServiceStatus.DISABLED == serviceByName.getStatus()) {
              rfiTxnService.changeServiceStatus(repairOrderDTO.getShopId(), serviceByName.getId(), ServiceStatus.ENABLED);
              set.add(serviceByName.getId());
            }
            repairOrderServiceDTO.setServiceId(serviceByName.getId());
            rfiTxnService.saveOrUpdateCategoryItemRelation(repairOrderDTO.getShopId(), repairOrderServiceDTO.getBusinessCategoryId(), repairOrderServiceDTO.getServiceId());
          }

          ServiceHistory serviceHistory = ServiceManager.getService(IServiceHistoryService.class).getOrSaveServiceHistoryByServiceId(repairOrderServiceDTO.getServiceId(), repairOrderDTO.getShopId());
          repairOrderServiceDTO.setServiceHistoryId(serviceHistory == null ? null : serviceHistory.getId());

          RepairOrderService repairOrderService = new RepairOrderService();
          repairOrderServiceDTO.setRepairOrderId(repairOrder.getId());
          repairOrderServiceDTO.setShopId(repairOrder.getShopId());
          repairOrderService = repairOrderService.fromDTO(repairOrderServiceDTO, false);
          writer.save(repairOrderService);
          repairOrderServiceDTO.setId(repairOrderService.getId());
          repairOrderServiceDTO.setRepairOrderId(repairOrder.getId());

          if (repairOrderServiceDTO.getService() != null && !"".equals(repairOrderServiceDTO.getService())) {
            serviceStr.append(repairOrderServiceDTO.getService()).append(";");
          }
          ItemIndex itemIndex = new ItemIndex();
          itemIndex.setCustomerId(repairOrderDTO.getCustomerId());
          itemIndex.setShopId(repairOrderDTO.getShopId());
          itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
          itemIndex.setOrderId(repairOrder.getId());
          itemIndex.setServiceId(repairOrderServiceDTO.getServiceId());
          itemIndex.setServices(repairOrderServiceDTO.getService());
          String startDateStr = repairOrderDTO.getStartDateStr();
          Long startDateLong = null != startDateStr && !"".equals(startDateStr) ?
            new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startDateStr)) : null;
          itemIndex.setOrderTimeCreated(startDateLong);
          itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
          itemIndex.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
          itemIndex.setOrderStatus(repairOrder.getStatus() + "");
          itemIndex.setOrderStatusEnum(repairOrder.getStatusEnum());
          itemIndex.setItemTypeEnum(ItemTypes.SERVICE);
          itemIndex.setItemName(repairOrderServiceDTO.getService() != null ? repairOrderServiceDTO.getService() : "");
          itemIndex.setItemMemo(repairOrderServiceDTO.getMemo());
          itemIndex.setItemId(repairOrderServiceDTO.getId());
          itemIndex.setItemPrice(repairOrderServiceDTO.getTotal());
          itemIndex.setArrears(repairOrderDTO.getDebt());
          itemIndex.setVehicleBrand(repairOrderDTO.getBrand());
          itemIndex.setVehicleModel(repairOrderDTO.getModel());
          itemIndex.setVehicleYear(repairOrderDTO.getYear());
          itemIndex.setVehicleEngine(repairOrderDTO.getEngine());
          itemIndex.setOrderTotalAmount(repairOrderDTO.getTotal());
          itemIndex.setBusinessCategoryId(repairOrderServiceDTO.getBusinessCategoryId());
          itemIndex.setBusinessCategoryName(repairOrderServiceDTO.getBusinessCategoryName());
//          itemIndex.setItemCostPrice(serviceDTO.getCostPrice() == null ? Double.valueOf(0.0) : serviceDTO.getCostPrice());
          String endDateStr = repairOrderDTO.getHuankuanTime() != null ? repairOrderDTO.getHuankuanTime() : "";
          Long endDateLong = null != endDateStr && !"".equals(endDateStr) ?
            new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endDateStr)) : null;
          itemIndex.setPaymentTime(endDateLong);
          itemIndexList.add(itemIndex);
        }
      }

      if (CollectionUtils.isNotEmpty(set)) {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(repairOrder.getShopId(), set);
      }

      if (serviceStr.length() > 0) serviceStr.setLength(serviceStr.length() - 1);
    }
    return serviceStr.toString();
  }

  private Service createServiceByRepairOrder(TxnWriter writer, Long shopId, RepairOrderServiceDTO repairOrderServiceDTO) throws Exception {
    Service service = new Service();
    service.setShopId(shopId);
    service.setName(repairOrderServiceDTO.getService());
    service.setPrice(repairOrderServiceDTO.getTotal());
    service.setStatus(ServiceStatus.ENABLED);
    writer.save(service);

    if (null != repairOrderServiceDTO.getBusinessCategoryId()) {
      CategoryItemRelation categoryItemRelation = new CategoryItemRelation();
      categoryItemRelation.setServiceId(service.getId());
      categoryItemRelation.setCategoryId(repairOrderServiceDTO.getBusinessCategoryId());
      writer.save(categoryItemRelation);
    }

    return service;
  }

  public void addVehicleToProduct(RepairOrderDTO repairOrderDTO, RepairOrderItemDTO itemDTO) throws Exception {
    if (itemDTO.getProductType() != SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE) return;
    ProductDTO productDTO = new ProductDTO();
    populateProductDTO(repairOrderDTO, itemDTO, productDTO);
    getProductService().addVehicleToProduct(repairOrderDTO.getShopId(), productDTO);
  }


  public boolean addNewProduct(RepairOrderDTO repairOrderDTO, RepairOrderItemDTO itemDTO, ProductDTO productDTO) throws Exception {
    populateProductDTO(repairOrderDTO, itemDTO, productDTO);
    productDTO.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
    return getProductService().saveNewProduct(productDTO);
  }

  private ProductDTO populateProductDTO(RepairOrderDTO repairOrderDTO, RepairOrderItemDTO itemDTO, ProductDTO productDTO) {
    productDTO.setShopId(repairOrderDTO.getShopId());
    productDTO.setId(itemDTO.getProductId());
    productDTO.setName(itemDTO.getProductName());
    productDTO.setBrand(itemDTO.getBrand());
    productDTO.setSpec(itemDTO.getSpec());
    productDTO.setModel(itemDTO.getModel());
    productDTO.setVehicleBrand(repairOrderDTO.getBrand());
    productDTO.setVehicleModel(repairOrderDTO.getModel());
    productDTO.setVehicleYear(repairOrderDTO.getYear());
    productDTO.setVehicleEngine(repairOrderDTO.getEngine());
    productDTO.setVehicleBrandId(repairOrderDTO.getBrandId());
    productDTO.setVehicleModelId(repairOrderDTO.getModelId());
    productDTO.setVehicleYearId(repairOrderDTO.getYearId());
    productDTO.setVehicleEngineId(repairOrderDTO.getEngineId());
    //维修单中新增的新产品默认为专车专用 todo 3.0 之后是否还是如此逻辑
    if (itemDTO.getProductType() != null && itemDTO.getProductType().intValue() == SearchConstant.PRODUCT_PRODUCTSTATUS_NULL) {
      itemDTO.setProductType(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
    }

    if (itemDTO.getProductType() == null) {
      itemDTO.setProductType(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
      productDTO.setProductVehicleBrand(itemDTO.getVehicleBrand());
      productDTO.setProductVehicleEngine(itemDTO.getVehicleEngine());
      productDTO.setProductVehicleModel(itemDTO.getVehicleModel());
      productDTO.setProductVehicleYear(itemDTO.getVehicleYear());
    } else if (itemDTO.getProductType() != null && itemDTO.getProductType().intValue() == SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL) {
      productDTO.setProductVehicleBrand(repairOrderDTO.getBrand());
      productDTO.setProductVehicleModel(repairOrderDTO.getModel());
      productDTO.setProductVehicleYear(repairOrderDTO.getYear());
      productDTO.setProductVehicleEngine(repairOrderDTO.getEngine());
    } else {
      LOG.warn("ProductType is not expected  :  " + itemDTO.getProductType());   //这边不需要打error日志
    }
    productDTO.setMemo(itemDTO.getMemo());
    productDTO.setProductVehicleStatus(itemDTO.getProductType());
    productDTO.setPrice(itemDTO.getPrice());
    productDTO.setPurchasePrice(itemDTO.getPurchasePrice());

    //add unit for new product
    productDTO.setSellUnit(itemDTO.getUnit());
    productDTO.setStorageUnit(itemDTO.getUnit());
    productDTO.setCommodityCode(itemDTO.getCommodityCode());
    return productDTO;
  }

  private void createRepairRemindEvent(RepairOrderDTO repairOrderDTO, Long repairOrderId, TxnWriter writer,
                                       RepairOrderItemDTO itemDTO, String serviceStr, RepairRemindEventTypes type, Inventory inventory) {
    RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
    repairRemindEvent.setEventTypeEnum(type);
    repairRemindEvent.setShopId(repairOrderDTO.getShopId());
    repairRemindEvent.setEventContent(itemDTO.getProductId());
    repairRemindEvent.setRepairOrderId(repairOrderId);
    repairRemindEvent.setProductBrand(itemDTO.getBrand());
    repairRemindEvent.setProductName(itemDTO.getProductName());
    repairRemindEvent.setProductModel(itemDTO.getModel());
    repairRemindEvent.setProductSpec(itemDTO.getSpec());
    repairRemindEvent.setVehicleBrand(repairOrderDTO.getBrand());
    repairRemindEvent.setVehicleModel(repairOrderDTO.getModel());
    repairRemindEvent.setVehicleEngine(repairOrderDTO.getEngine());
    repairRemindEvent.setVehicleYear(repairOrderDTO.getYear());
    repairRemindEvent.setCustomer(repairOrderDTO.getCustomerName());
    repairRemindEvent.setMobile(repairOrderDTO.getMobile());
    repairRemindEvent.setVehicle(repairOrderDTO.getVechicle());
    repairRemindEvent.setService(StringUtils.substring(serviceStr, 0, 200));
    repairRemindEvent.setProductId(itemDTO.getProductId());
    repairRemindEvent.setFinishTime(repairOrderDTO.getEndDate());
    repairRemindEvent.setStartDateStr(repairOrderDTO.getStartDateStr());
    repairRemindEvent.setEndDateStr(repairOrderDTO.getEndDateStr());
    repairRemindEvent.setUnit(itemDTO.getUnit());
    repairRemindEvent.setAmount(itemDTO.getAmount());
    writer.save(repairRemindEvent);
    //add by WLF 保存到提醒总表
    getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
    //add by WLF 提醒数量，更新到缓存
    getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
  }

  private void addMaterialItemIndex(
    RepairOrderDTO repairOrderDTO, RepairOrderItemDTO itemDTO, List<ItemIndex> itemIndexList, boolean isNewProduct) throws Exception {
    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setCustomerId(repairOrderDTO.getCustomerId());
    itemIndex.setShopId(repairOrderDTO.getShopId());
    itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
    itemIndex.setOrderId(itemDTO.getRepairOrderId());
    String startDateStr = repairOrderDTO.getStartDateStr();
    Long startDateLong = null != startDateStr && !"".equals(startDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startDateStr)) : null;
    itemIndex.setOrderTimeCreated(startDateLong);
    itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
    itemIndex.setItemTypeEnum(ItemTypes.MATERIAL);
    itemIndex.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    itemIndex.setOrderStatusEnum(repairOrderDTO.getStatus());
    itemIndex.setCommodityCode(itemDTO.getCommodityCode());
    itemIndex.setItemName(itemDTO.getProductName());
    itemIndex.setItemMemo(itemDTO.getMemo());
    itemIndex.setItemBrand(itemDTO.getBrand());
    itemIndex.setItemModel(itemDTO.getModel());
    itemIndex.setItemSpec(itemDTO.getSpec());
    itemIndex.setItemId(itemDTO.getId());
    itemIndex.setItemPrice(itemDTO.getPrice());
    itemIndex.setItemCostPrice(itemDTO.getCostPrice());
    itemIndex.setTotalCostPrice(itemDTO.getTotalCostPrice());
    //itemIndex itemCout改为double类型
    itemIndex.setItemCount(itemDTO.getAmount());
    itemIndex.setUnit(itemDTO.getUnit());                     //add unit
    itemIndex.setProductId(itemDTO.getProductId());
    itemIndex.setOrderTotalAmount(repairOrderDTO.getTotal());
    itemIndex.setArrears(repairOrderDTO.getDebt());

    itemIndex.setVehicleBrand(itemDTO.getVehicleBrand());
    itemIndex.setVehicleModel(itemDTO.getVehicleModel());
    itemIndex.setVehicleYear(itemDTO.getVehicleYear());
    itemIndex.setVehicleEngine(itemDTO.getVehicleEngine());
    String endDateStr = repairOrderDTO.getHuankuanTime();
    Long endDateLong = null != endDateStr && !"".equals(endDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endDateStr)) : null;
    itemIndex.setPaymentTime(endDateLong);
    itemIndex.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
    itemIndex.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
    itemIndexList.add(itemIndex);
  }

  //添加一条其他费用到intemIndexList中
  private void addOtherIncomeItemIndex(RepairOrderDTO repairOrderDTO, RepairOrderOtherIncomeItemDTO itemDTO, List<ItemIndex> itemIndexList) throws Exception {
    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setCustomerId(repairOrderDTO.getCustomerId());
    itemIndex.setShopId(repairOrderDTO.getShopId());
    itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
    itemIndex.setOrderId(repairOrderDTO.getId());
    String startDateStr = repairOrderDTO.getStartDateStr();
    Long startDateLong = null != startDateStr && !"".equals(startDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startDateStr)) : null;
    itemIndex.setOrderTimeCreated(startDateLong);
    itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
    itemIndex.setItemTypeEnum(ItemTypes.OTHER_INCOME);
    itemIndex.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    itemIndex.setOrderStatusEnum(repairOrderDTO.getStatus());
    itemIndex.setItemName(itemDTO.getName());
    itemIndex.setItemMemo(itemDTO.getMemo());
    itemIndex.setItemId(itemDTO.getId());
    itemIndex.setItemPrice(itemDTO.getPrice());
    itemIndex.setItemCostPrice(0D);
    itemIndex.setItemCount(1d);
    itemIndex.setOrderTotalAmount(repairOrderDTO.getTotal());
    itemIndex.setArrears(repairOrderDTO.getDebt());
    itemIndex.setVehicleBrand(repairOrderDTO.getBrand());
    itemIndex.setVehicleModel(repairOrderDTO.getModel());
    itemIndex.setVehicleYear(repairOrderDTO.getYear());
    itemIndex.setVehicleEngine(repairOrderDTO.getEngine());
    String endDateStr = repairOrderDTO.getHuankuanTime();
    Long endDateLong = null != endDateStr && !"".equals(endDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endDateStr)) : null;
    itemIndex.setPaymentTime(endDateLong);

    itemIndexList.add(itemIndex);
  }

  private void postProcessAfterUpdateRepairOrderStatus(TxnWriter writer, RepairOrderDTO repairOrderDTO, boolean newOrder) throws Exception {
    Long shopId = repairOrderDTO.getShopId();

    //增加或修改收款单
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SERVICE start:RepairService:RFUpdateRepairOrder_block7_1");
    ReceivableDTO receivableDTO = createOrUpdateReceivable(writer, repairOrderDTO);
    LOG.debug("AOP_SERVICE end:RepairService:RFUpdateRepairOrder_block7_1 用时：{}ms，memo：{}", System.currentTimeMillis() - begin, "增加或修改收款单");

    //添加欠款信息
    begin = System.currentTimeMillis();
    LOG.debug("AOP_SERVICE start:RepairService:RFUpdateRepairOrder_block7_2");
    createDebtForRepairOrder(writer, repairOrderDTO, shopId, receivableDTO);
    LOG.debug("AOP_SERVICE end:RepairService:RFUpdateRepairOrder_block7_2 用时：{}ms，memo：{}", System.currentTimeMillis() - begin, "添加欠款信息");


    //add by WLF 欠款提醒加入提醒总表
    begin = System.currentTimeMillis();
    LOG.debug("AOP_SERVICE start:RepairService:RFUpdateRepairOrder_block7_3");
    List<DebtDTO> debtList = getTxnService().getDebtByShopIdAndOrderId(shopId, repairOrderDTO.getId());
    LOG.debug("AOP_SERVICE end:RepairService:RFUpdateRepairOrder_block7_3 用时：{}ms，memo：{}", System.currentTimeMillis() - begin, "欠款提醒加入提醒总表");
    if (CollectionUtil.isNotEmpty(debtList)) {
      begin = System.currentTimeMillis();
      LOG.debug("AOP_SERVICE start:RepairService:RFUpdateRepairOrder_block7_4");
      writer.updateDebtRemindDeletedType(repairOrderDTO.getShopId(), repairOrderDTO.getCustomerId(), "customer", DeletedType.FALSE);
      getTxnService().saveRemindEvent(writer, new Debt().fromDTO(debtList.get(0), true), repairOrderDTO.getCustomerName(), repairOrderDTO.getMobile());
      LOG.debug("AOP_SERVICE end:RepairService:RFUpdateRepairOrder_block7_4 用时：{}ms，memo：{}", System.currentTimeMillis() - begin, "saveRemindEvent");
      begin = System.currentTimeMillis();
      LOG.debug("AOP_SERVICE start:RepairService:RFUpdateRepairOrder_block7_5");
//      getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT,shopId);     //放到线程里去了
      LOG.debug("AOP_SERVICE end:RepairService:RFUpdateRepairOrder_block7_5 用时：{}ms，memo：{}", System.currentTimeMillis() - begin, "updateRemindCountInMemcacheByTypeAndShopId");

    }
  }

  /**
   * 根据施工单中的内容 创建receivable、receivable_services_times、reception_record、reception_services_times表
   *
   * @param writer
   * @param repairOrderDTO
   * @return
   * @throws Exception
   */
  private ReceivableDTO createOrUpdateReceivable(TxnWriter writer, RepairOrderDTO repairOrderDTO) throws Exception {

    Long remindTime = null;
    try {
      remindTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getHuankuanTime());
    } catch (ParseException e) {
      LOG.error(e.getMessage());
    }

    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ReceivableDTO receivableDTO = new ReceivableDTO();
    receivableDTO.setId(repairOrderDTO.getReceivableId());
    receivableDTO.setRemindTime(remindTime);
    receivableDTO.setOrderType(OrderTypes.REPAIR);
    receivableDTO.setStatus(ReceivableStatus.FINISH);
    receivableDTO.setOrderId(repairOrderDTO.getId());
    receivableDTO.setShopId(repairOrderDTO.getShopId());
    receivableDTO.setSettledAmount(repairOrderDTO.getSettledAmount());
    receivableDTO.setDebt(repairOrderDTO.getDebt());
    receivableDTO.setTotal(repairOrderDTO.getTotal());
    receivableDTO.setDiscount(repairOrderDTO.getOrderDiscount());
    receivableDTO.setAfterMemberDiscountTotal(repairOrderDTO.getAfterMemberDiscountTotal());
    receivableDTO.setMemberDiscountRatio(repairOrderDTO.getMemberDiscountRatio());
    receivableDTO.setLastPayee(repairOrderDTO.getUserName());
    receivableDTO.setLastPayeeId(repairOrderDTO.getUserId());
    receivableDTO.setCustomerId(repairOrderDTO.getCustomerId());
    receivableDTO.setVestDate(repairOrderDTO.getVestDate());
    receivableDTO.setReceiptNo(repairOrderDTO.getReceiptNo());
    receivableDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
    //更新代金券金额
//    if(repairOrderDTO.getConsumingRecordId()!=null) {
//      CouponConsumeRecordDTO couponConsumeRecordDTO=getConsumingService().getCouponConsumeRecordById(repairOrderDTO.getConsumingRecordId());
//      if(couponConsumeRecordDTO!=null) {
//        receivableDTO.setCouponConsume(couponConsumeRecordDTO.getCoupon().doubleValue());
//      }
//    }
    //如果含有会员结算信息 把member_id保存到receivable表中
    if (memberCheckerService.containMemberAmount(repairOrderDTO)
      || memberCheckerService.containMemberCountConsume(repairOrderDTO) || null != receivableDTO.getMemberDiscountRatio()) {
      String memberNo = repairOrderDTO.getAccountMemberNo();
      Member member = membersService.getMemberByShopIdAndMemberNo(repairOrderDTO.getShopId(), memberNo);
      if (member == null) {
        LOG.error("/TxnService.java");
        LOG.error("method=createOrUpdateReceivable");
        LOG.error("会员查询出错");
      } else {
        receivableDTO.setMemberId(member.getId());
        receivableDTO.setMemberNo(member.getMemberNo());
      }
    }

    //添加会员相关
    receivableDTO.setMemberBalancePay(NumberUtil.doubleVal(repairOrderDTO.getMemberAmount()));
    receivableDTO.setCash(NumberUtil.doubleVal(repairOrderDTO.getCashAmount()));  //现金
    receivableDTO.setBankCard(NumberUtil.doubleVal(repairOrderDTO.getBankAmount()));    //银行卡
    receivableDTO.setCheque(NumberUtil.doubleVal(repairOrderDTO.getBankCheckAmount()));    //支票
    receivableDTO.setBankCheckNo(repairOrderDTO.getBankCheckNo());

    receivableDTO.setCoupon(repairOrderDTO.getCouponAmount()); //代金券

    ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
    ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
    writer.save(receivableHistory);
    receivableHistoryDTO.setId(receivableHistory.getId());

    ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
    receptionRecordDTO.setDayType(DayType.OTHER_DAY);
    receptionRecordDTO.setReceivableHistoryId(receivableHistoryDTO.getId());

    if (repairOrderDTO.getSettledAmount() >= 0) {
      receptionRecordDTO.setAmount(repairOrderDTO.getSettledAmount());
      receptionRecordDTO.setReceivableId(receivableDTO.getId());
      receptionRecordDTO.setMemberBalancePay(NumberUtil.doubleVal(repairOrderDTO.getMemberAmount()));
      receptionRecordDTO.setChequeNo(repairOrderDTO.getBankCheckNo());

      receptionRecordDTO.setCash(NumberUtil.doubleVal(repairOrderDTO.getCashAmount()));
      receptionRecordDTO.setBankCard(NumberUtil.doubleVal(repairOrderDTO.getBankAmount()));
      receptionRecordDTO.setCheque(NumberUtil.doubleVal(repairOrderDTO.getBankCheckAmount()));
      receptionRecordDTO.setMemberId(receivableDTO.getMemberId());
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setOriginDebt(0d);
      receptionRecordDTO.setDiscount(repairOrderDTO.getOrderDiscount());
      receptionRecordDTO.setRemainDebt(repairOrderDTO.getDebt());

      receptionRecordDTO.setCoupon(repairOrderDTO.getCouponAmount());//代金券

      receptionRecordDTO.setShopId(repairOrderDTO.getShopId());
      receptionRecordDTO.setOrderId(repairOrderDTO.getId());
      receptionRecordDTO.setOrderTotal(receivableDTO.getTotal());
      receptionRecordDTO.setOrderTypeEnum(OrderTypes.REPAIR);
      receptionRecordDTO.setOrderStatusEnum(repairOrderDTO.getStatus());
      if (StringUtil.isNotEmpty(repairOrderDTO.getAccountDateStr())) {
        receptionRecordDTO.setReceptionDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, repairOrderDTO.getAccountDateStr()));
      } else {
        receptionRecordDTO.setReceptionDate(System.currentTimeMillis());  //
      }
      receptionRecordDTO.setToPayTime(DateUtil.convertDateStringToDateLong(DateUtil.DEFAULT, repairOrderDTO.getHuankuanTime()));
      receptionRecordDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
      receptionRecordDTO.setMemberDiscountRatio(receivableDTO.getMemberDiscountRatio());
      receptionRecordDTO.setPayee(repairOrderDTO.getUserName());
      receptionRecordDTO.setPayeeId(repairOrderDTO.getUserId());
      ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
      receptionRecordDTOs[0] = receptionRecordDTO;
      receivableDTO.setRecordDTOs(receptionRecordDTOs);
    }

    Receivable receivable = null;
    if (null == receivableDTO.getId()) {
      receivable = new Receivable();
      receivable.fromDTO(receivableDTO);
      writer.save(receivable);
      receivableDTO.setId(receivable.getId());
    } else {
      receivable = writer.getById(Receivable.class, receivableDTO.getId());
      receivable.setSettledAmount(receivableDTO.getSettledAmount());
      receivable.setDebt(receivableDTO.getDebt());

      receivable.setTotal(receivableDTO.getTotal());
      receivable.setMemberBalancePay(receivableDTO.getMemberBalancePay());
      receivable.setCash(receivableDTO.getCash());
      receivable.setBankCard(receivableDTO.getBankCard());
      receivable.setCheque(receivableDTO.getCheque());
      receivable.setMemberId(receivableDTO.getMemberId());
      receivable.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
      receivable.setMemberDiscountRatio(receivable.getMemberDiscountRatio());
      receivable.setLastPayee(receivableDTO.getLastPayee());
      receivable.setLastPayeeId(receivableDTO.getLastPayeeId());
      receivable.setCoupon(receivable.getCoupon());//代金券
      writer.update(receivable);
    }

    ReceptionRecordDTO recordDTOs[] = receivableDTO.getRecordDTOs();
    if (!ArrayUtils.isEmpty(recordDTOs)) {
      for (ReceptionRecordDTO recordDTO : recordDTOs) {
        ReceptionRecord record = new ReceptionRecord();
        record.fromDTO(recordDTO);
        record.setReceivableId(receivable.getId());
        writer.save(record);
        receptionRecordDTO.setId(record.getId());
      }
    }
    receivableDTO.setId(receivable.getId());

    RepairOrderServiceDTO[] repairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
    if (ArrayUtils.isEmpty(repairOrderServiceDTOs)) {
      return receivableDTO;
    }
    for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
      if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
        continue;
      }

      ReceivableServiceTimes receivableServiceTimes = new ReceivableServiceTimes();
      receivableServiceTimes.setShopId(repairOrderDTO.getShopId());
      receivableServiceTimes.setTimes(1);
      receivableServiceTimes.setReceivableId(receivable.getId());
      receivableServiceTimes.setServiceId(repairOrderServiceDTO.getServiceId());
      receivableServiceTimes.setOriginAmount(repairOrderServiceDTO.getPrice());
      writer.save(receivableServiceTimes);

      ReceptionServiceTimes receptionServiceTimes = new ReceptionServiceTimes();
      receptionServiceTimes.setShopId(repairOrderDTO.getShopId());
      receptionServiceTimes.setTimes(1);
      receptionServiceTimes.setReceptionRecordId(receptionRecordDTO.getId());
      receptionServiceTimes.setServiceId(repairOrderServiceDTO.getServiceId());
      receptionServiceTimes.setOriginAmount(repairOrderServiceDTO.getPrice());
      writer.save(receptionServiceTimes);
    }
    return receivableDTO;
  }

  private void createDebtForRepairOrder(TxnWriter writer,
                                        RepairOrderDTO repairOrderDTO, Long shopId,
                                        ReceivableDTO receivableDTO) throws Exception {
    if (repairOrderDTO.getDebt() > 0) {
      StringBuffer materials = new StringBuffer();
      StringBuffer services = new StringBuffer();
      for (int i = 0; i < repairOrderDTO.getItemDTOs().length; i++) {
        if (i != repairOrderDTO.getItemDTOs().length - 1) {
          if (repairOrderDTO.getItemDTOs()[i].getProductName() != null) {
            materials.append(repairOrderDTO.getItemDTOs()[i].getProductName()).append(",");
          }
        } else {
          if (repairOrderDTO.getItemDTOs()[i].getProductName() != null) {
            materials.append(repairOrderDTO.getItemDTOs()[i].getProductName());
          }
        }
      }
      if (materials.length() > 450) {
        materials.setLength(450);
        materials.append("等");
      }
      for (int i = 0; i < repairOrderDTO.getServiceDTOs().length; i++) {
        if (i != repairOrderDTO.getServiceDTOs().length - 1) {
          if (repairOrderDTO.getServiceDTOs()[i].getService() != null) {
            services.append(repairOrderDTO.getServiceDTOs()[i].getService()).append(",");
          }
        } else {
          if (repairOrderDTO.getServiceDTOs()[i].getService() != null) {
            services.append(repairOrderDTO.getServiceDTOs()[i].getService());
          }
        }
      }
      if (services.length() > 180) {
        services.setLength(180);
        services.append("等");
      }
      Debt debt = new Debt();
      if (OrderTypes.REPAIR == repairOrderDTO.getServiceType()) {
        debt.setContent(BcgogoI18N.getMessageByKey("debt.type.mainteance"));
        debt.setOrderTypeEnum(OrderTypes.REPAIR);
      } else if (OrderTypes.SALE == repairOrderDTO.getServiceType()) {
        debt.setContent(BcgogoI18N.getMessageByKey("debt.type.sales"));
        debt.setOrderTypeEnum(OrderTypes.SALE);
      }
      debt.setCustomerId(repairOrderDTO.getCustomerId());
      if (receivableDTO != null) {
        debt.setDebt(receivableDTO.getDebt());
        debt.setRecievableId(receivableDTO.getId());
      }
      debt.setReceiptNo(repairOrderDTO.getReceiptNo());
      debt.setMaterial(materials.toString());
      debt.setService(services.toString());
      debt.setOrderId(repairOrderDTO.getId());
      debt.setOrderTime(repairOrderDTO.getEndDate());
      debt.setSettledAmount(repairOrderDTO.getSettledAmount());
      debt.setShopId(shopId);
      debt.setTotalAmount(repairOrderDTO.getTotal());
      debt.setVehicleNumber(repairOrderDTO.getVechicle());
      Long payTime = System.currentTimeMillis();
      Long remindTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getHuankuanTime());
      debt.setPayTime(payTime);
      debt.setRemindTime(remindTime);
      debt.setStatusEnum(DebtStatus.ARREARS);
      debt.setRemindStatus(UserConstant.Status.ACTIVITY);
      writer.save(debt);
    }
  }

  /**
   * 删除已经不存在的repairorderService，记录对应的itemIndex，保存或者更新，新order上的service ，记录对应的ItemIndex
   *
   * @param repairOrderDTO
   * @param writer
   * @param itemIndexList
   * @param itemIndexToDeleteList
   * @return
   * @throws Exception
   */

  private String updateServices(RepairOrderDTO repairOrderDTO, TxnWriter writer,
                                List<ItemIndex> itemIndexList, List<ItemIndex> itemIndexToDeleteList) throws Exception {
    String serviceStr = "";
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
    if (null != repairOrderDTO.getId()) {
      List<RepairOrderService> repairOrderServices = writer.getRepairOrderServicesByOrderId(repairOrderDTO.getId());
      //delete repairOrdersevice  改单之前的service不存在则删除该service 并将该service对应的itemIndex存入itemIndexToDeleteList
      for (RepairOrderService repairOrderService : repairOrderServices) {
        boolean flag = false;
        if (repairOrderServiceDTOs != null) {
          for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
            if (repairOrderService.getId().equals(repairOrderServiceDTO.getId())) {
              flag = true;
              break;
            }
          }
        }
        if (!flag) {
          writer.delete(RepairOrderService.class, repairOrderService.getId());
          ItemIndex itemIndex = new ItemIndex();
          itemIndex.setOrderId(repairOrderDTO.getId());
          itemIndex.setShopId(repairOrderDTO.getShopId());
          itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
          itemIndex.setItemId(repairOrderService.getId());
          itemIndex.setItemTypeEnum(ItemTypes.SERVICE);
          itemIndexToDeleteList.add(itemIndex);
        }
      }
    }

    if (repairOrderServiceDTOs != null) {
      Set<Long> set = new HashSet<Long>();
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
        //过滤没用的空行
        if (StringUtils.isBlank(repairOrderServiceDTO.getService())) {
          continue;
        }

        if (repairOrderServiceDTO.getServiceId() == null) {
          //判断是否加入service表中   ,用于保存service类型的
          Service serviceByName = rfiTxnService.getRFServiceByServiceNameAndShopId(repairOrderDTO.getShopId(), repairOrderServiceDTO.getService());
          if (null == serviceByName) {
            Service service = createServiceByRepairOrder(writer, repairOrderDTO.getShopId(), repairOrderServiceDTO);
            repairOrderServiceDTO.setServiceId(service.getId());
            set.add(service.getId());
          } else {
            if (ServiceStatus.DISABLED == serviceByName.getStatus()) {
              rfiTxnService.changeServiceStatus(repairOrderDTO.getShopId(), serviceByName.getId(), ServiceStatus.ENABLED);
              set.add(serviceByName.getId());
            }
            repairOrderServiceDTO.setServiceId(serviceByName.getId());
            rfiTxnService.saveOrUpdateCategoryItemRelation(repairOrderDTO.getShopId(), repairOrderServiceDTO.getBusinessCategoryId(), repairOrderServiceDTO.getServiceId());
          }
        }

        if (repairOrderServiceDTO.getServiceHistoryId() == null) {
          ServiceHistory serviceHistory = ServiceManager.getService(IServiceHistoryService.class).getOrSaveServiceHistoryByServiceId(repairOrderServiceDTO.getServiceId(), repairOrderDTO.getShopId());
          repairOrderServiceDTO.setServiceHistoryId(serviceHistory == null ? null : serviceHistory.getId());
        }

        serviceStr = serviceStr + repairOrderServiceDTO.getService();
        //add new repairOrderService
        if (null == repairOrderServiceDTO.getId()) {
          RepairOrderService service = new RepairOrderService();

          repairOrderServiceDTO.setRepairOrderId(repairOrderDTO.getId());
          repairOrderServiceDTO.setShopId(repairOrderDTO.getShopId());
          service = service.fromDTO(repairOrderServiceDTO, false);

          writer.save(service);
          repairOrderServiceDTO.setId(service.getId());
          addServiceItemIndex(repairOrderDTO, repairOrderServiceDTO, itemIndexList);
        } else {
          //update new repairOrderService
          RepairOrderService repairOrderService = writer.getById(RepairOrderService.class, repairOrderServiceDTO.getId());

          if (repairOrderService != null) {
            repairOrderServiceDTO.setRepairOrderId(repairOrderService.getRepairOrderId());
            repairOrderServiceDTO.setShopId(repairOrderDTO.getShopId());
            repairOrderService = repairOrderService.fromDTO(repairOrderServiceDTO, false);
            writer.update(repairOrderService);
          } else {
            LOG.error("TxnService.updateServices repairOrderService is null");
            LOG.error(repairOrderDTO.toString() + "," + repairOrderServiceDTO.toString());
          }
          addServiceItemIndex(repairOrderDTO, repairOrderServiceDTO, itemIndexList);
        }
      }

      if (CollectionUtils.isNotEmpty(set)) {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(repairOrderDTO.getShopId(), set);
      }
    }
    return serviceStr;
  }

  /**
   * 删除已经不存在的repairorderOtherIncomeItem，记录对应的itemIndex，保存或者更新 ，记录对应的ItemIndex
   *
   * @param repairOrderDTO
   * @param writer
   * @param itemIndexList
   * @param itemIndexToDeleteList
   * @return
   * @throws Exception
   */

  private void updateOtherIncomeItem(RepairOrderDTO repairOrderDTO, TxnWriter writer,
                                     List<ItemIndex> itemIndexList, List<ItemIndex> itemIndexToDeleteList) throws Exception {
    if (itemIndexList == null) {
      itemIndexList = new ArrayList<ItemIndex>();
    }
    if (itemIndexToDeleteList == null) {
      itemIndexToDeleteList = new ArrayList<ItemIndex>();
    }

    Long shopId = repairOrderDTO.getShopId();

    List<RepairOrderOtherIncomeItem> otherIncomeItemList = writer.getRepairOtherIncomeItemByOrderId(shopId, repairOrderDTO.getId());

    Map<Long, RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOMap = RepairOrderOtherIncomeItemDTO.listToMap(repairOrderDTO.getOtherIncomeItemDTOList());

    if (CollectionUtils.isNotEmpty(otherIncomeItemList)) {
      for (RepairOrderOtherIncomeItem item : otherIncomeItemList) {
        if (null != otherIncomeItemDTOMap.get(item.getId())) {
          RepairOrderOtherIncomeItem otherIncomeItem = writer.getById(RepairOrderOtherIncomeItem.class, item.getId());
          RepairOrderOtherIncomeItemDTO otherIncomeItemDTO = otherIncomeItemDTOMap.get(item.getId());
          otherIncomeItemDTO.setShopId(shopId);
          otherIncomeItemDTO.setOrderId(repairOrderDTO.getId());
          otherIncomeItem.fromDTO(otherIncomeItemDTOMap.get(item.getId()));
          writer.update(otherIncomeItem);
          addOtherIncomeItemIndex(repairOrderDTO, otherIncomeItemDTO, itemIndexList);
        } else {
          writer.delete(RepairOrderOtherIncomeItem.class, item.getId());
          ItemIndex itemIndex = new ItemIndex();
          itemIndex.setOrderId(repairOrderDTO.getId());
          itemIndex.setShopId(shopId);
          itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
          itemIndex.setItemId(item.getId());
          itemIndex.setItemTypeEnum(ItemTypes.OTHER_INCOME);
          itemIndexToDeleteList.add(itemIndex);
        }
      }
    }

    if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
        if (null == itemDTO.getId()) {
          itemDTO.setShopId(shopId);
          itemDTO.setOrderId(repairOrderDTO.getId());
          RepairOrderOtherIncomeItem item = new RepairOrderOtherIncomeItem(itemDTO);
          writer.save(item);
          addOtherIncomeItemIndex(repairOrderDTO, itemDTO, itemIndexList);
        }
      }
    }
  }

  private void saveOrUpdateOtherIncomeItem(RepairOrderDTO repairOrderDTO, TxnWriter writer) throws Exception {
    Long shopId = repairOrderDTO.getShopId();
    List<RepairOrderOtherIncomeItem> otherIncomeItemList = writer.getRepairOtherIncomeItemByOrderId(shopId, repairOrderDTO.getId());
    Map<Long, RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOMap = RepairOrderOtherIncomeItemDTO.listToMap(repairOrderDTO.getOtherIncomeItemDTOList());
    if (CollectionUtils.isNotEmpty(otherIncomeItemList)) {
      for (RepairOrderOtherIncomeItem item : otherIncomeItemList) {
        if (null != otherIncomeItemDTOMap.get(item.getId())) {
          RepairOrderOtherIncomeItemDTO otherIncomeItemDTO = otherIncomeItemDTOMap.get(item.getId());
          otherIncomeItemDTO.setShopId(shopId);
          otherIncomeItemDTO.setOrderId(repairOrderDTO.getId());
          item.fromDTO(otherIncomeItemDTOMap.get(item.getId()));
          writer.update(item);
        } else {
          writer.delete(RepairOrderOtherIncomeItem.class, item.getId());
        }
      }
    }

    if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
        if (null == itemDTO.getId()) {
          itemDTO.setShopId(shopId);
          itemDTO.setOrderId(repairOrderDTO.getId());
          RepairOrderOtherIncomeItem item = new RepairOrderOtherIncomeItem(itemDTO);
          writer.save(item);
        }
      }
    }
  }


  private void modifyExistingItems(Long oldStorehouseId, RepairOrderDTO repairOrderDTO, TxnWriter writer, String serviceStr,
                                   List<ItemIndex> itemIndexList, List<InventorySearchIndex> inventorySearchIndexList,
                                   List<ItemIndex> itemIndexToDeleteList) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
    List<RepairOrderItem> items = writer.getRepairOrderItemsByRepairOrderId(repairOrderDTO.getId());
    if (CollectionUtils.isNotEmpty(items)) {
      for (RepairOrderItem repairOrderItem : items) {
        RepairOrderItemDTO currentItemDTO = null;
        if (!ArrayUtils.isEmpty(itemDTOs)) {
          for (RepairOrderItemDTO itemDTO : itemDTOs) {
            //维修单中id相同材料项目
            if (repairOrderItem.getId().equals(itemDTO.getId())) {
              //   modifyExistingItem 有其他数据库提交，故独立提交了事务
              modifyExistingItem(oldStorehouseId, repairOrderDTO, repairOrderItem, itemDTO, writer, serviceStr, itemIndexList, inventorySearchIndexList);
              currentItemDTO = itemDTO;
              break;
            }
          }
        }
        if (currentItemDTO == null) {                                    //todo 方法貌似有问题，如果改单之后，以前预留没有加到库存里去
          writer.delete(RepairOrderItem.class, repairOrderItem.getId());
          writer.deleteRepairRemindEventByShopIdAndRepairOrderIdAndTypeAndContent(repairOrderDTO.getShopId(), repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING, repairOrderItem.getProductId());
          //add by WLF
          getTxnService().cancelRemindEventByOrderIdAndObjectId(RemindEventType.REPAIR, RepairRemindEventTypes.INCOMING.toString(), repairOrderDTO.getId(), repairOrderItem.getProductId());
          //add by WLF 提醒数量，更新到缓存
          getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
          ItemIndex itemIndex = new ItemIndex();
          itemIndex.setShopId(repairOrderDTO.getShopId());
          itemIndex.setOrderId(repairOrderDTO.getId());
          itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
          itemIndex.setItemId(repairOrderItem.getId());
          itemIndex.setItemTypeEnum(ItemTypes.MATERIAL);
          itemIndexToDeleteList.add(itemIndex);

          List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
          for (int i = 0; i < repairRemindEvents.size(); i++) {
            RepairRemindEvent event = repairRemindEvents.get(i);
            if (event.getEventContent() != null && event.getEventContent().equals(repairOrderItem.getProductId())) {
              Long eventId = event.getId();
              writer.delete(event);
              //add by WLF
              getTxnService().cancelRemindEventByOldRemindEventId(RemindEventType.REPAIR, eventId, writer);
              //add by WLF 提醒数量，更新到缓存
              getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
              break;
            }
          }
          List<RepairRemindEvent> lackRepairRemindEvents = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
          //删除所有缺料待修提醒后，如果有服务增加待交付提醒
          if (repairOrderDTO.getServiceDTOs() != null && repairOrderDTO.getServiceDTOs().length > 0 && CollectionUtil.isEmpty(lackRepairRemindEvents)) {
            RepairRemindEvent repairRemindEvent;
            List<RepairRemindEvent> repairRemindEvent_PENDING = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
            if (CollectionUtils.isNotEmpty(repairRemindEvent_PENDING)) {
              repairRemindEvent = repairRemindEvent_PENDING.get(0);
            } else {
              repairRemindEvent = new RepairRemindEvent();
            }
            repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.PENDING);
            repairRemindEvent.setShopId(repairOrderDTO.getShopId());
            if (repairOrderDTO.getRemindEventDTOs() != null && repairOrderDTO.getRemindEventDTOs().length > 0) {
              RepairRemindEventDTO[] repairRemindEventDTOs = repairOrderDTO.getRemindEventDTOs();
              if (repairRemindEventDTOs != null && repairRemindEventDTOs.length > 0) {
                StringBuffer productName = new StringBuffer();
                for (RepairRemindEventDTO r : repairRemindEventDTOs) {
                  productName.append(r.getProductName());
                }
                repairRemindEvent.setProductName(productName.toString());
              }
            }
            repairRemindEvent.setRepairOrderId(repairOrderDTO.getId());
            repairRemindEvent.setVehicleBrand(repairOrderDTO.getBrand());
            repairRemindEvent.setVehicleModel(repairOrderDTO.getModel());
            repairRemindEvent.setVehicleEngine(repairOrderDTO.getEngine());
            repairRemindEvent.setVehicleYear(repairOrderDTO.getYear());
            repairRemindEvent.setAmount(repairOrderDTO.getSettledAmount());
            repairRemindEvent.setCustomer(repairOrderDTO.getCustomerName());
            repairRemindEvent.setMobile(repairOrderDTO.getMobile());
            repairRemindEvent.setVehicle(repairOrderDTO.getVechicle());
            repairRemindEvent.setStartDateStr(repairOrderDTO.getStartDateStr());
            repairRemindEvent.setEndDateStr(repairOrderDTO.getEndDateStr());
            repairRemindEvent.setService(serviceStr);
            repairRemindEvent.setFinishTime(repairOrderDTO.getEndDate());
            writer.save(repairRemindEvent);

            //add by WLF 保存到提醒总表
            getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
            //add by WLF 提醒数量，更新到缓存
            getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
          }

          if (NumberUtil.doubleVal(repairOrderItem.getReserved()) > 0) {
            Inventory inventory = writer.getById(Inventory.class, repairOrderItem.getProductId());
            ProductDTO productDTO = getProductService().getProductByProductLocalInfoId(inventory.getId(), inventory.getShopId());

            //check unit
            Double reservedAmount = 0d;
            if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productDTO)) {
              reservedAmount = repairOrderItem.getReserved() * productDTO.getRate();
            } else {
              reservedAmount = repairOrderItem.getReserved();
            }
            inventory.setAmount(inventory.getAmount() + reservedAmount);
            if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
              storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(oldStorehouseId, repairOrderItem.getProductId(), null, reservedAmount));
            }
            writer.save(inventory);
            InventorySearchIndex inventorySearchIndex = getTxnService().createInventorySearchIndex(inventory, productDTO);
            inventorySearchIndexList.add(inventorySearchIndex);
          }
        }
      }
    }
  }

  private void createNewItems(RepairOrderDTO repairOrderDTO, TxnWriter writer, String serviceStr,
                              List<ItemIndex> itemIndexList, List<InventorySearchIndex> inventorySearchIndexList) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
    if (!ArrayUtils.isEmpty(itemDTOs)) {
      for (RepairOrderItemDTO itemDTO : itemDTOs) {
        boolean isNP = itemDTO.getProductId() == null;
        boolean isNewProduct = false;
        if (itemDTO.getId() != null || StringUtils.isBlank(itemDTO.getProductName())) continue;
        Long parentProductId = null;
        if (null == itemDTO.getProductId()) {
          ProductDTO productDTO = new ProductDTO();
          isNewProduct = addNewProduct(repairOrderDTO, itemDTO, productDTO);
          Long newProductId = productDTO.getProductLocalInfoId();
          parentProductId = productDTO.getId();
          if (isNewProduct) {
            getTxnService().saveInventoryAfterSaveNewProduct(productDTO.getShopId(), newProductId, 0, writer, productDTO.getSellUnit());//todo to check the new function
            if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
              storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), newProductId, null, 0d));
            }
          } else {
            getProductService().updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), newProductId, itemDTO.getCommodityCode());
          }
          itemDTO.setProductId(newProductId);
        } else {
          getProductService().updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), itemDTO.getProductId(), itemDTO.getCommodityCode());
          addVehicleToProduct(repairOrderDTO, itemDTO);
          ProductLocalInfoDTO productLocalInfoDTO =
            getProductService().getProductLocalInfoById(itemDTO.getProductId(), repairOrderDTO.getShopId());
          if (productLocalInfoDTO != null) {
            parentProductId = productLocalInfoDTO.getProductId();
            getProductService().updateProductLocalInfoCategory(repairOrderDTO.getShopId(), productLocalInfoDTO.getId(), itemDTO.getBusinessCategoryId());
          }
        }
        modifyNewItemInventoryIndex(repairOrderDTO, itemDTO, serviceStr, inventorySearchIndexList, writer,
          parentProductId, isNewProduct);
        RepairOrderItem item = new RepairOrderItem();
        item.setAmount(itemDTO.getAmount());
        item.setUnit(itemDTO.getUnit());                                    //add unti
        item.setReserved(itemDTO.getReserved());
        item.setMemo(itemDTO.getMemo() != null ? itemDTO.getMemo() : "");
        item.setPrice(itemDTO.getPrice());
        item.setTotal(itemDTO.getTotal());
        item.setProductId(itemDTO.getProductId());
        item.setShopId(repairOrderDTO.getShopId());
        item.setRepairOrderId(repairOrderDTO.getId());
        item.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
        item.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
        if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
          item.setCostPrice(NumberUtil.doubleVal(itemDTO.getCostPrice()));
          item.setTotalCostPrice(NumberUtil.doubleVal(itemDTO.getTotalCostPrice()));
        }
        writer.save(item);
        itemDTO.setId(item.getId());
        itemDTO.setRepairOrderId(repairOrderDTO.getId());
        addMaterialItemIndex(repairOrderDTO, itemDTO, itemIndexList, isNP);
      }
    }
  }

  private void addServiceItemIndexes(RepairOrderDTO repairOrderDTO, RepairOrderServiceDTO serviceValue, List<ItemIndex> itemIndexList) throws Exception {
    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setCustomerId(repairOrderDTO.getCustomerId());
    itemIndex.setShopId(repairOrderDTO.getShopId());
    itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
    itemIndex.setOrderId(repairOrderDTO.getId());
    String startDateStr = repairOrderDTO.getStartDateStr();
    Long startDateLong = null != startDateStr && !"".equals(startDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startDateStr)) : null;
    itemIndex.setOrderTimeCreated(startDateLong);
    itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
    itemIndex.setItemTypeEnum(ItemTypes.SERVICE);
    itemIndex.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    itemIndex.setOrderStatusEnum(repairOrderDTO.getStatus());
    itemIndex.setItemName(serviceValue.getService());
    itemIndex.setItemMemo(serviceValue.getMemo());
    itemIndex.setItemPrice(serviceValue.getTotal());
    itemIndex.setArrears(repairOrderDTO.getDebt());
    itemIndex.setVehicleBrand(repairOrderDTO.getBrand());
    itemIndex.setVehicleModel(repairOrderDTO.getModel());
    itemIndex.setVehicleYear(repairOrderDTO.getYear());
    itemIndex.setVehicleEngine(repairOrderDTO.getEngine());
    String endDateStr = repairOrderDTO.getHuankuanTime();
    Long endDateLong = null != endDateStr && !"".equals(endDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endDateStr)) : null;
    itemIndex.setPaymentTime(endDateLong);
    itemIndexList.add(itemIndex);
  }

  //添加一条维修服务到intemIndexList中
  private void addServiceItemIndex(RepairOrderDTO repairOrderDTO, RepairOrderServiceDTO itemDTO, List<ItemIndex> itemIndexList) throws Exception {
    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setCustomerId(repairOrderDTO.getCustomerId());
    itemIndex.setShopId(repairOrderDTO.getShopId());
    itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
    itemIndex.setOrderId(repairOrderDTO.getId());
    String startDateStr = repairOrderDTO.getStartDateStr();
    Long startDateLong = null != startDateStr && !"".equals(startDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startDateStr)) : null;
    itemIndex.setOrderTimeCreated(startDateLong);
    itemIndex.setOrderTypeEnum(OrderTypes.REPAIR);
    itemIndex.setItemTypeEnum(ItemTypes.SERVICE);
    itemIndex.setServiceId(itemDTO.getServiceId());
    itemIndex.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    itemIndex.setOrderStatusEnum(repairOrderDTO.getStatus());
    itemIndex.setItemName(itemDTO.getService());
    itemIndex.setItemMemo(itemDTO.getMemo());
    itemIndex.setItemId(itemDTO.getId());
    itemIndex.setItemPrice(itemDTO.getTotal());
    itemIndex.setItemCostPrice(itemDTO.getCostPrice() == null ? Double.valueOf(0.0) : itemDTO.getCostPrice());
    //itemIndex itemCout改为double类型
    itemIndex.setItemCount(1d);
    itemIndex.setOrderTotalAmount(repairOrderDTO.getTotal());
    itemIndex.setArrears(repairOrderDTO.getDebt());
    itemIndex.setVehicleBrand(repairOrderDTO.getBrand());
    itemIndex.setVehicleModel(repairOrderDTO.getModel());
    itemIndex.setVehicleYear(repairOrderDTO.getYear());
    itemIndex.setVehicleEngine(repairOrderDTO.getEngine());
    String endDateStr = repairOrderDTO.getHuankuanTime();
    Long endDateLong = null != endDateStr && !"".equals(endDateStr) ?
      new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endDateStr)) : null;
    itemIndex.setPaymentTime(endDateLong);
    itemIndex.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
    itemIndex.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
    itemIndexList.add(itemIndex);
  }

  private void modifyExistingItem(Long oldStorehouseId, RepairOrderDTO repairOrderDTO, RepairOrderItem item, RepairOrderItemDTO itemDTO, TxnWriter writer, String serviceStr,
                                  List<ItemIndex> itemIndexList, List<InventorySearchIndex> inventorySearchIndexList) throws Exception {

    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Long parentProductId = null;
    if (null == itemDTO.getProductId()) {
      ProductDTO productDTO = new ProductDTO();
      boolean isNewProduct = addNewProduct(repairOrderDTO, itemDTO, productDTO);
      Long newProductId = productDTO.getProductLocalInfoId();
      parentProductId = productDTO.getId();
      if (isNewProduct) {
        getTxnService().saveInventoryAfterSaveNewProduct(productDTO.getShopId(), newProductId, 0, writer, productDTO.getSellUnit());   //todo to check the new function
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), newProductId, null, 0d));
        }
      } else {
        getProductService().updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), newProductId, itemDTO.getCommodityCode());
      }
      itemDTO.setProductId(newProductId);
    } else {
      getProductService().updateCommodityCodeByProductLocalInfoId(repairOrderDTO.getShopId(), itemDTO.getProductId(), itemDTO.getCommodityCode());
      addVehicleToProduct(repairOrderDTO, itemDTO);
      ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(itemDTO.getProductId(), repairOrderDTO.getShopId());
      if (productLocalInfoDTO != null) {
        parentProductId = productLocalInfoDTO.getProductId();
        getProductService().updateProductLocalInfoCategory(repairOrderDTO.getShopId(), productLocalInfoDTO.getId(), itemDTO.getBusinessCategoryId());
      }
      if (itemDTO.getProductType() != null && itemDTO.getProductType() != SearchConstant.PRODUCT_PRODUCTSTATUS_ALL) {
        ProductVehicleDTO productVehicleDTO = new ProductVehicleDTO();
        productVehicleDTO.setShopId(repairOrderDTO.getShopId());
        productVehicleDTO.setBrandId(repairOrderDTO.getBrandId());
        productVehicleDTO.setModelId(repairOrderDTO.getModelId());
        productVehicleDTO.setYearId(repairOrderDTO.getYearId());
        productVehicleDTO.setEngineId(repairOrderDTO.getEngineId());
        productVehicleDTO.setProductId(parentProductId);
        getProductService().createProductVehicle(productVehicleDTO);
      }
    }
    modifyExistingItemInventoryIndex(oldStorehouseId, repairOrderDTO, item, itemDTO, serviceStr, inventorySearchIndexList, writer, parentProductId);
    item.setAmount(itemDTO.getAmount());
    item.setUnit(itemDTO.getUnit());
    item.setMemo(itemDTO.getMemo());
    item.setPrice(itemDTO.getPrice());
    item.setTotal(itemDTO.getTotal());
    item.setProductId(itemDTO.getProductId());
    item.setRepairOrderId(repairOrderDTO.getId());
    item.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
    item.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
    if (OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus()) {
      item.setCostPrice(itemDTO.getCostPrice());
      item.setTotalCostPrice(itemDTO.getTotalCostPrice());
    }
    writer.saveOrUpdate(item);
    itemDTO.setId(item.getId());
    addMaterialItemIndex(repairOrderDTO, itemDTO, itemIndexList, false);
  }

  private void modifyNewItemInventoryIndex(RepairOrderDTO repairOrderDTO, RepairOrderItemDTO itemDTO, String serviceStr,
                                           List<InventorySearchIndex> inventorySearchIndexList, TxnWriter writer,
                                           Long parentProductId, boolean isNewProductId) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Inventory inventory = null;
    if (!isNewProductId) inventory = writer.getById(Inventory.class, itemDTO.getProductId());
    boolean newProduct = false;
    if (inventory == null) {
      inventory = new Inventory();
      inventory.setShopId(repairOrderDTO.getShopId());
      inventory.setAmount(0d);
      inventory.setId(itemDTO.getProductId());
      newProduct = true;
    }

    Double inventoryAmount = 0d;
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
      StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), itemDTO.getProductId());
      inventoryAmount = storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount();
    } else {
      inventoryAmount = inventory.getAmount();
    }
    ProductDTO productDTO = getProductService().getProductById(parentProductId, repairOrderDTO.getShopId());
    double itemDTOAmountWhitSellUnit = 0d;
    double inventoryAmountWhitItemUnit = 0d;
    if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
      itemDTOAmountWhitSellUnit = itemDTO.getAmount() * productDTO.getRate();
      inventoryAmountWhitItemUnit = inventoryAmount / productDTO.getRate();
    } else {
      itemDTOAmountWhitSellUnit = itemDTO.getAmount();
      inventoryAmountWhitItemUnit = inventoryAmount;
    }
    if (inventoryAmount + 0.0001 > itemDTOAmountWhitSellUnit) {
      itemDTO.setReserved(itemDTO.getAmount());
      inventory.setAmount(inventory.getAmount() - itemDTOAmountWhitSellUnit);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
        storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), itemDTO.getProductId(), null, itemDTOAmountWhitSellUnit * -1));
      }
      if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_SETTLED && NumberUtil.longValue(inventory.getLastSalesTime()) < repairOrderDTO.getVestDate()) {
        inventory.setLastSalesTime(repairOrderDTO.getVestDate());
      }
      writer.save(inventory);
      InventorySearchIndex inventorySearchIndex = getTxnService().createInventorySearchIndex(inventory, parentProductId);
      inventorySearchIndexList.add(inventorySearchIndex);
      itemDTO.setReserved(itemDTO.getAmount());
    } else {
      //todo 缺料的时候删除待交付提醒事件
      writer.deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
      //add by WLF
      getTxnService().cancelRemindEventByOrderIdAndStatus(RemindEventType.REPAIR, repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
      //add by WLF 提醒数量，更新到缓存
      getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());

      itemDTO.setReserved(0d);
      RepairRemindEvent repairRemindEvent = new RepairRemindEvent();
      repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.LACK);
      repairRemindEvent.setShopId(repairOrderDTO.getShopId());
      repairRemindEvent.setEventContent(itemDTO.getProductId());
      repairRemindEvent.setRepairOrderId(repairOrderDTO.getId());
      repairRemindEvent.setProductBrand(itemDTO.getBrand());
      repairRemindEvent.setProductName(itemDTO.getProductName());
      repairRemindEvent.setProductModel(itemDTO.getModel());
      repairRemindEvent.setProductSpec(itemDTO.getSpec());
      repairRemindEvent.setVehicleBrand(repairOrderDTO.getBrand());
      repairRemindEvent.setVehicleModel(repairOrderDTO.getModel());
      repairRemindEvent.setVehicleEngine(repairOrderDTO.getEngine());
      repairRemindEvent.setVehicleYear(repairOrderDTO.getYear());
      repairRemindEvent.setCustomer(repairOrderDTO.getCustomerName());
      repairRemindEvent.setMobile(repairOrderDTO.getMobile());
      repairRemindEvent.setVehicle(repairOrderDTO.getVechicle());
      repairRemindEvent.setService(serviceStr);
      repairRemindEvent.setAmount((itemDTO.getAmount()) - inventoryAmountWhitItemUnit);
      repairRemindEvent.setUnit(itemDTO.getUnit());
      repairRemindEvent.setProductId(itemDTO.getProductId());
      repairRemindEvent.setFinishTime(repairOrderDTO.getEndDate());
      repairRemindEvent.setStartDateStr(repairOrderDTO.getStartDateStr());
      repairRemindEvent.setEndDateStr(repairOrderDTO.getEndDateStr());
      writer.save(repairRemindEvent);

      //add by WLF 保存到提醒总表
      getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
      //add by WLF 提醒数量，更新到缓存
      getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());

      if (newProduct) {
        writer.delete(Inventory.class, itemDTO.getProductId());
        writer.save(inventory);
        InventorySearchIndex inventorySearchIndex = getTxnService().createInventorySearchIndex(inventory, parentProductId);
        inventorySearchIndexList.add(inventorySearchIndex);
      }
    }
  }

  private void modifyExistingItemInventoryIndex(Long oldStorehouseId, RepairOrderDTO repairOrderDTO, RepairOrderItem item, RepairOrderItemDTO itemDTO, String serviceStr,
                                                List<InventorySearchIndex> inventorySearchIndexList, TxnWriter writer, Long parentProductId) throws Exception {

    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    // for now, assume product id is same.
    //get current inventory
    Inventory inventory = writer.getById(Inventory.class, item.getProductId());

    ProductDTO productDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(item.getProductId(), repairOrderDTO.getShopId());
    //  缺料提醒
    RepairRemindEvent repairRemindEvent = null;
    //来料待修提醒
    RepairRemindEvent repairRemindEventInComing = null;
    //缺料列表
    List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
      repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    //带交付列表
    List<RepairRemindEvent> repairRemindEvent_PENDING = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
      repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    for (int i = 0; i < repairRemindEvents.size(); i++) {
      RepairRemindEvent event = repairRemindEvents.get(i);
      if (event.getEventContent() != null && event.getEventContent().equals(item.getProductId())) {
        repairRemindEvent = event;
        break;
      }
    }
    //来料待修列表
    List<RepairRemindEvent> repairRemindEventsInComings = writer.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
      repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    for (int i = 0; i < repairRemindEventsInComings.size(); i++) {
      RepairRemindEvent event = repairRemindEventsInComings.get(i);
      if (event.getEventContent() != null && event.getEventContent().equals(item.getProductId())) {
        repairRemindEventInComing = event;
        break;
      }
    }
    Double inventoryAmount = 0d, reservedAmount = 0d;
    //to checkUnit
    if (UnitUtil.isStorageUnit(item.getUnit(), productDTO)) {               //item单位为库存大单位
      reservedAmount = item.getReserved() * productDTO.getRate();
    } else {
      reservedAmount = item.getReserved();
    }
    StoreHouseInventoryDTO storeHouseInventoryDTO = null;
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
      storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), item.getProductId());
      if (storeHouseInventoryDTO == null) {
        storeHouseInventoryDTO = new StoreHouseInventoryDTO(oldStorehouseId, item.getProductId(), 0d);
      }
      if (!oldStorehouseId.equals(repairOrderDTO.getStorehouseId())) {
        //归还给老仓库
        storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(oldStorehouseId, item.getProductId(), null, reservedAmount));
        inventoryAmount = storeHouseInventoryDTO.getAmount();
      } else {
        inventoryAmount = storeHouseInventoryDTO.getAmount() + reservedAmount;
        storeHouseInventoryDTO.setAmount(storeHouseInventoryDTO.getAmount() + reservedAmount);
      }
    } else {
      inventoryAmount = inventory.getAmount() + reservedAmount;
    }
    inventory.setAmount(inventory.getAmount() + reservedAmount);

    if (itemDTO != null) {
      double itemDTOAmountWithSellUnit = 0d;
      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {               //item单位为库存大单位
        itemDTOAmountWithSellUnit = itemDTO.getAmount() * productDTO.getRate();
      } else {
        itemDTOAmountWithSellUnit = itemDTO.getAmount();
      }

      if (inventoryAmount > itemDTOAmountWithSellUnit - 0.001) {
        //如果库存大于数量，则删除提醒
        inventory.setAmount(inventory.getAmount() - itemDTOAmountWithSellUnit);
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
          storeHouseInventoryDTO.setAmount(storeHouseInventoryDTO.getAmount() - itemDTOAmountWithSellUnit);
        }
        if (repairRemindEvent != null) {
          writer.delete(repairRemindEvent);
          //add by WLF
          getTxnService().cancelRemindEventByOldRemindEventId(RemindEventType.REPAIR, repairRemindEvent.getId(), writer);
          //add by WLF 提醒数量，更新到缓存
          getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
        }
        item.setReserved(itemDTO.getAmount());
        itemDTO.setReserved(itemDTO.getAmount());
      } else {
        //如果提醒表中存在来料则删除
        if (repairRemindEventInComing != null) {
          if ((repairRemindEventInComing.getEventType() == TxnConstant.REPAIR_REMIND_EVENT_INCOMING) ||
            repairRemindEventInComing.getEventTypeEnum().equals(RepairRemindEventTypes.INCOMING)) {
            writer.deleteRepairRemindEventByShopIdAndAndRepairOrderIdAndProductId(repairOrderDTO.getShopId(), item.getRepairOrderId(), itemDTO.getProductId());
            //add by WLF
            getTxnService().cancelRemindEventByOrderIdAndObjectId(RemindEventType.REPAIR, null, item.getRepairOrderId(), itemDTO.getProductId());
            //add by WLF 提醒数量，更新到缓存
            getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
          }
        }
        // TODO add by WLF 此处为何要设预留为0
        itemDTO.setReserved(0d);
        item.setReserved(0d);
        //有待交付提醒则删除
        if (CollectionUtils.isNotEmpty(repairRemindEvent_PENDING)) {
          writer.deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(repairOrderDTO.getShopId(), repairOrderDTO.getId(),
            RepairRemindEventTypes.PENDING);
          //add by WLF
          getTxnService().cancelRemindEventByOrderIdAndStatus(RemindEventType.REPAIR, repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
          //add by WLF 提醒数量，更新到缓存
          getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
        }
        //缺料需要保存提醒
        if (inventoryAmount - (itemDTOAmountWithSellUnit - itemDTO.getReserved()) < 0.01 && repairRemindEvent == null) {
          repairRemindEvent = new RepairRemindEvent();
          repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.LACK);
          repairRemindEvent.setShopId(repairOrderDTO.getShopId());
          repairRemindEvent.setEventContent(itemDTO.getProductId());
          repairRemindEvent.setRepairOrderId(repairOrderDTO.getId());
          repairRemindEvent.setProductBrand(itemDTO.getBrand());
          repairRemindEvent.setProductName(itemDTO.getProductName());
          repairRemindEvent.setProductModel(itemDTO.getModel());
          repairRemindEvent.setProductSpec(itemDTO.getSpec());
          repairRemindEvent.setVehicleBrand(repairOrderDTO.getBrand());
          repairRemindEvent.setVehicleModel(repairOrderDTO.getModel());
          repairRemindEvent.setVehicleEngine(repairOrderDTO.getEngine());
          repairRemindEvent.setVehicleYear(repairOrderDTO.getYear());
          repairRemindEvent.setCustomer(repairOrderDTO.getCustomerName());
          repairRemindEvent.setMobile(repairOrderDTO.getMobile());
          repairRemindEvent.setVehicle(repairOrderDTO.getVechicle());
          repairRemindEvent.setService(serviceStr);

          repairRemindEvent.setAmount(itemDTO.getAmount());
          repairRemindEvent.setUnit(itemDTO.getUnit());
          repairRemindEvent.setProductId(itemDTO.getProductId());
          repairRemindEvent.setFinishTime(repairOrderDTO.getEndDate());
          repairRemindEvent.setStartDateStr(repairOrderDTO.getStartDateStr());
          repairRemindEvent.setEndDateStr(repairOrderDTO.getEndDateStr());

          writer.save(repairRemindEvent);

          //add by WLF 保存到提醒总表
          getTxnService().saveRemindEvent(repairRemindEvent, repairOrderDTO, writer);
          //add by WLF 提醒数量，更新到缓存
          getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
        }
      }
    } else {
      if (repairRemindEvent != null) {
        writer.delete(repairRemindEvent);
        //add by WLF
        getTxnService().cancelRemindEventByOldRemindEventId(RemindEventType.REPAIR, repairRemindEvent.getId(), writer);
        //add by WLF 提醒数量，更新到缓存
        getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());
      }
    }

    if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_SETTLED && NumberUtil.longValue(inventory.getLastSalesTime()) < repairOrderDTO.getVestDate()) {
      inventory.setLastSalesTime(repairOrderDTO.getVestDate());
    }
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
      storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
    }
    writer.saveOrUpdate(inventory);
    InventorySearchIndex inventorySearchIndex = getTxnService().createInventorySearchIndex(inventory, parentProductId);
    inventorySearchIndexList.add(inventorySearchIndex);
  }


  @Override
  public Result validateRepairPicking(RepairOrderDTO repairOrderDTO, String validateType) throws Exception {
    if (repairOrderDTO == null || ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      return null;
    }
    if (repairOrderDTO.getId() == null) {
      boolean isEmptyItem = true;
      if (!ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs())) {
        for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
          if (StringUtils.isNotBlank(repairOrderItemDTO.getProductName())) {
            isEmptyItem = false;
            break;
          }
        }
        if (!isEmptyItem) {
          Result result = new Result();
          result.setSuccess(false);
          if ("finish".equals(validateType)) {
            result.setMsg("您有商品未出库，无法完工，请先出库！");
          } else if ("account".equals(validateType) || "accountDetail".equals(validateType)) {
            result.setMsg("您有商品未出库，无法结算，请先出库！");
          }
          result.setOperation(Result.Operation.ALERT.toString());
          return result;
        }
      }
    } else if (repairOrderDTO.getId() != null) {
      List<RepairOrderItem> repairOrderItems = getTxnService().getRepairOrderItemByRepairOrderId(repairOrderDTO.getId());

      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
          continue;
        }
        boolean isSetReserved = false;
        if (CollectionUtils.isNotEmpty(repairOrderItems)) {
          for (RepairOrderItem repairOrderItem : repairOrderItems) {
            if (repairOrderItemDTO.getId() != null && repairOrderItemDTO.getId().equals(repairOrderItem.getId())) {
              repairOrderItemDTO.setReserved(repairOrderItem.getReserved());
              isSetReserved = true;
              break;
            }
          }
        }
        if (!isSetReserved) {
          repairOrderItemDTO.setReserved(0d);
        }
        if (!NumberUtil.isEqualOrGreater(repairOrderItemDTO.getReserved(), repairOrderItemDTO.getAmount())) {
          Result result = new Result();
          result.setSuccess(false);
          if ("finish".equals(validateType)) {
            result.setMsg("您有商品未出库，无法完工，请先出库！");
          } else if ("account".equals(validateType) || "accountDetail".equals(validateType)) {
            result.setMsg("您有商品未出库，无法结算，请先出库！");
          }
          result.setOperation(Result.Operation.ALERT.toString());
          return result;
        }
      }
    }
    return null;
  }

  @Override
  public void updateInventoryByRepealRepairOrderWithRepairPicking(RepairOrderDTO repairOrderDTO, TxnWriter writer) throws Exception {
    if (repairOrderDTO == null || repairOrderDTO.getId() == null) {
      return;
    }
    RepairPickingDTO repairPickingDTO = null;
    boolean isNeedToCreateNewRepairPicking = getPickingService().isNeedToCreateNewRepairPicking(repairOrderDTO);
    if (isNeedToCreateNewRepairPicking) {
      repairPickingDTO = new RepairPickingDTO();
      repairPickingDTO.setReceiptNo(getTxnService().getReceiptNo(repairOrderDTO.getShopId(), OrderTypes.REPAIR_PICKING, null));
    }
    RepairPicking repairPicking = writer.getRepairPickingByRepairOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
    if (repairPicking == null && isNeedToCreateNewRepairPicking) {
      repairPickingDTO.fromRepairDTO(repairOrderDTO);
      repairPicking = new RepairPicking();
      repairPicking.fromDTO(repairPickingDTO);
      repairPicking.setStatus(OrderStatus.PENDING);
      writer.save(repairPicking);
    }
    List<RepairOrderItem> oldRepairOrderItems = writer.getRepairOrderItemsByOrderId(repairOrderDTO.getId());
    Map<Long, RepairPickingItem> repairPickingItemMap = new HashMap<Long, RepairPickingItem>();
    if (repairPicking != null && repairPicking.getId() != null) {
      repairPickingItemMap = getPickingService().getPendingRepairPickingItemMap(repairPicking.getId());
    }
    Set<Long> productIds = new HashSet<Long>();
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    if (CollectionUtils.isNotEmpty(oldRepairOrderItems)) {
      for (RepairOrderItem repairOrderItem : oldRepairOrderItems) {
        if (repairOrderItem.getProductId() != null) {
          productIds.add(repairOrderItem.getProductId());
        }
      }
    }
    productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
    if (CollectionUtils.isNotEmpty(oldRepairOrderItems) && repairPicking != null) {
      for (RepairOrderItem repairOrderItem : oldRepairOrderItems) {
        if (repairOrderItem.getProductId() == null) {
          continue;
        }
        ProductDTO productDTO = productDTOMap.get(repairOrderItem.getProductId());
        double reservedWithSellUnit = NumberUtil.doubleVal(repairOrderItem.getReserved());
        if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productDTO)) {
          reservedWithSellUnit = reservedWithSellUnit * productDTO.getRate();
        }
        RepairPickingItem repairPickingItem = repairPickingItemMap.get(repairOrderItem.getProductId());
        if (repairPickingItem == null) {
          String unit = productDTO == null ? null : productDTO.getSellUnit();
          RepairPickingItemDTO repairPickingItemDTO = new RepairPickingItemDTO(
            repairPicking.getId(), repairOrderItem.getProductId(), reservedWithSellUnit, unit, OrderStatus.WAIT_RETURN_STORAGE);
          repairPickingItemDTO.setProductDTO(productDTO);
          repairPickingItem = new RepairPickingItem();
          repairPickingItem.fromDTO(repairPickingItemDTO);
        } else if (OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItem.getStatus())) {
          repairPickingItem.setAmount(reservedWithSellUnit);
          repairPickingItem.setStatus(OrderStatus.WAIT_RETURN_STORAGE);
        } else if (OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItem.getStatus())) {
          repairPickingItem.setAmount(repairPickingItem.getAmount() + reservedWithSellUnit);
        }
        repairPickingItemMap.put(repairOrderItem.getProductId(), repairPickingItem);
      }
    }
    //重新生成维修领料
    boolean isRepairPickingFinish = true;
    if (!repairPickingItemMap.isEmpty()) {
      for (RepairPickingItem repairPickingItem : repairPickingItemMap.values()) {
        if (repairPickingItem.getId() == null && Math.abs(NumberUtil.doubleVal(repairPickingItem.getAmount())) < 0.0001) {
          continue;
        } else if (Math.abs(NumberUtil.doubleVal(repairPickingItem.getAmount())) > 0.0001) {
          isRepairPickingFinish = false;
          writer.saveOrUpdate(repairPickingItem);
        } else {
          writer.delete(repairPickingItem);
        }
      }
    }
    if (repairPicking != null && repairPicking.getId() != null) {
      List<RepairPickingItem> repairPickingItems = writer.getRepairPickingItemsByOrderIds(repairPicking.getId());
      if (CollectionUtils.isEmpty(repairPickingItems)) {
        writer.delete(repairPicking);
      } else {
        //维修领料完结逻辑
        if (repairPicking != null && OrderStatus.REPAIR_REPEAL.equals(repairOrderDTO.getStatus()) && isRepairPickingFinish) {
          repairPicking.setStatus(OrderStatus.REPEAL);
          writer.update(repairPicking);
        }
      }
    }
  }

  @Override
  public void getProductInfo(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO == null) {
      return;
    }
    RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
    if (ArrayUtils.isEmpty(repairOrderItemDTOs)) {
      return;
    }
    Set<Long> productHistoryIds = new HashSet<Long>();
    Set<Long> productIds = new HashSet<Long>();
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOs) {
      if (repairOrderItemDTO != null && repairOrderItemDTO.getProductHistoryId() != null) {
        productHistoryIds.add(repairOrderItemDTO.getProductHistoryId());
      }
      if (repairOrderItemDTO != null && repairOrderItemDTO.getProductId() != null) {
        productIds.add(repairOrderItemDTO.getProductId());
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
    Map<Long, InventoryDTO> inventoryDTOMap = getInventoryService().getInventoryDTOMap(repairOrderDTO.getShopId(), productIds);
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
      storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(repairOrderDTO.getShopId(), repairOrderDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
    }

    //得到材料列表
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOs) {
      ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(repairOrderItemDTO.getProductHistoryId());
      ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
      if (productHistoryDTO != null) {
        repairOrderItemDTO.setProductHistoryDTO(productHistoryDTO);
        if (OrderUtil.repairOrderInProgress.contains(repairOrderDTO.getStatus())) {
          repairOrderItemDTO.setProductUnitRateInfo(productDTO);
        }
      } else {
        repairOrderItemDTO.setProductDTOWithOutUnit(productDTO);
      }

      double inventoryAmount = 0d;
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
        StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(repairOrderItemDTO.getProductId());
        inventoryAmount = storeHouseInventoryDTO == null ? 0d : NumberUtil.doubleVal(storeHouseInventoryDTO.getAmount());
      } else {
        InventoryDTO inventoryDTO = inventoryDTOMap.get(repairOrderItemDTO.getProductId());
        inventoryAmount = inventoryDTO == null ? 0d : NumberUtil.doubleVal(inventoryDTO.getAmount());
      }
      double inventoryAmountWithUnit = 0d;
      double purchasePriceWhitUnit = 0d;
      if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productDTO)) {       //维修单商品单位为库存大单位
        repairOrderItemDTO.setInventoryAmount(inventoryAmount / productDTO.getRate());
        inventoryAmountWithUnit = inventoryAmount / productDTO.getRate();
        purchasePriceWhitUnit = (productDTO.getPurchasePrice() == null ? 0d : productDTO.getPurchasePrice()) * productDTO.getRate();
      } else {
        repairOrderItemDTO.setInventoryAmount(inventoryAmount);
        inventoryAmountWithUnit = inventoryAmount;
        purchasePriceWhitUnit = (null == productDTO || productDTO.getPurchasePrice() == null) ? 0d : productDTO.getPurchasePrice();
      }
      repairOrderItemDTO.setPurchasePrice(purchasePriceWhitUnit);
      if (repairOrderItemDTO.getReserved() != null && repairOrderItemDTO.getReserved() > 0) {
        repairOrderItemDTO.setLack(false);
      } else if (repairOrderItemDTO.getAmount() < inventoryAmountWithUnit + 0.001) {
        repairOrderItemDTO.setLack(false);
      } else {
        repairOrderItemDTO.setLack(true);
      }

      if (!repairOrderItemDTO.getCommodityCodeModifyFlag()) {
        repairOrderItemDTO.setCommodityCode(null == productDTO ? repairOrderItemDTO.getCommodityCode() : productDTO.getCommodityCode());
      }
    }
  }

  @Override
  public void initRepairOrderModel(RepairOrderDTO repairOrderDTO, ModelMap model) throws Exception {
    List<CustomerCardDTO> customerCardDTOs = null;
    if (null != repairOrderDTO && null != repairOrderDTO.getShopId()) {
      double totalDebt = 0;
      double totalConsume = 0.0;
      if (repairOrderDTO.getCustomerId() != null) {
        List<CustomerRecordDTO> customerRecordDTOs = getUserService().getCustomerRecordByCustomerId(repairOrderDTO.getCustomerId());
        if (CollectionUtils.isNotEmpty(customerRecordDTOs)) {
          totalDebt = customerRecordDTOs.get(0).getTotalReceivable();
          totalConsume = customerRecordDTOs.get(0).getTotalAmount();
        }
        customerCardDTOs = getUserService().getCustomerCardByCustomerIdAndCardType(repairOrderDTO.getShopId(), repairOrderDTO.getCustomerId(),
          WashCardConstants.CARD_TYPE_DEFAULT);
        if (CollectionUtils.isNotEmpty(customerCardDTOs)) {
          model.addAttribute("customerCardDTO", customerCardDTOs.get(0));
          //获取当天洗车次数
          model.addAttribute("todayWashTimes", getTxnService().getTodayWashTimes(repairOrderDTO.getCustomerId()));
        }
        List<WashOrderDTO> washOrderDTOs = getTxnService().getCustomerWashOrders(repairOrderDTO.getCustomerId());
        if (CollectionUtils.isNotEmpty(washOrderDTOs)) {
          model.addAttribute("washOrderDTOs", washOrderDTOs);
        }
      }
      model.addAttribute("totalDebt", NumberUtil.round(totalDebt, NumberUtil.MONEY_PRECISION));
      model.addAttribute("totalConsume", NumberUtil.round(totalConsume, NumberUtil.MONEY_PRECISION));
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
        List<StoreHouseDTO> storeHouseDTOList = getStoreHouseService().getAllStoreHousesByShopId(repairOrderDTO.getShopId());
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if (CollectionUtils.isNotEmpty(storeHouseDTOList) && repairOrderDTO.getStorehouseId() == null) {
          if (storeHouseDTOList.size() == 1) {
            repairOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        //更新库存 根据仓库
        getInventoryService().updateItemDTOInventoryAmountByStorehouse(repairOrderDTO.getShopId(), repairOrderDTO.getStorehouseId(), repairOrderDTO);
      }
      repairOrderDTO.calculateTotal();
    }

    model.addAttribute("repairOrderDTO", repairOrderDTO);
  }

  @Override
  public Result validateRepairOrderOnSaveDraft(RepairOrderDTO repairOrderDTO) throws Exception {
    Result result = new Result();
    if (repairOrderDTO.getId() != null && repairOrderDTO.getShopId() != null) {
      RepairOrderDTO dbRepairOrderDTO = getRfiTxnService().getRepairOrderDTOById(repairOrderDTO.getId(), repairOrderDTO.getShopId());
      if (dbRepairOrderDTO != null && OrderStatus.REPAIR_SETTLED.equals(dbRepairOrderDTO.getStatus())) {
        return new Result("当前单据已结算无法保存草稿", false);
      } else if (dbRepairOrderDTO != null && OrderStatus.REPAIR_REPEAL.equals(dbRepairOrderDTO.getStatus())) {
        return new Result("当前单据已被作废无法保存草稿", false);
      }
    }
    return result;
  }

  @Override
  public RepairOrderDTO getSimpleRepairOrderDTO(Long shopId, Long id) {
    if (shopId == null || id == null) {
      return null;
    }
    RepairOrder repairOrder = txnDaoManager.getWriter().getRepairOrderById(id, shopId);
    if (repairOrder != null) {
      return repairOrder.toDTO();
    }
    return null;
  }


  @Override
  public void saveOrUpdateProductForRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = repairOrderDTO.getShopId();
    //新增或者更新产品信息
    List<ProductDTO> toUpdateProductDTOs = new ArrayList<ProductDTO>();
    Set<Long> productIds = new HashSet<Long>();
    List<RepairOrderItemDTO> toAddUnitItemDTOs = new ArrayList<RepairOrderItemDTO>();
    Set<Long> deletedProductIds = new HashSet<Long>();
    //1.没有productId的item去根据6字段匹配取出product表的id  todo by qxy 优化方案可以批量查询
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
        continue;
      }
      if (repairOrderDTO.getShopId() != null && repairOrderItemDTO.getProductId() == null) {
        ProductDTO productSearchCondition = new ProductDTO(repairOrderDTO.getShopId(), repairOrderItemDTO);
        List<ProductDTO> productDTOs = productService.getProductDTOsBy7P(repairOrderDTO.getShopId(), productSearchCondition);
        ProductDTO productDTO = CollectionUtil.getFirst(productDTOs);
        if (productDTO != null) {
          repairOrderItemDTO.setProductOriginId(productDTO.getId());
          productIds.add(productDTO.getId());
        }
      }
    }
    //根据productId取出productLocalInfoId
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMapByProductIds(shopId, productIds);
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
        continue;
      }
      if (repairOrderItemDTO.getProductOriginId() != null) {
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductOriginId());
        if (productLocalInfoDTO != null) {
          repairOrderItemDTO.setProductId(productLocalInfoDTO.getId());
        }
      }
    }
    //新增产品
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
        continue;
      }
      boolean isNewProductFlag = false;
      ProductDTO productDTO = new ProductDTO(repairOrderDTO.getShopId(), repairOrderItemDTO);
      productDTO.setProductLocalInfoId(repairOrderItemDTO.getProductId());
      if (repairOrderItemDTO.getProductId() == null) {
        isNewProductFlag = productService.saveNewProduct(productDTO);
        repairOrderItemDTO.setProductOriginId(productDTO.getId());
        repairOrderItemDTO.setProductId(productDTO.getProductLocalInfoId());
      }
      if (!isNewProductFlag) {
        toUpdateProductDTOs.add(productDTO);
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductOriginId());
        //需要添加单位的item
        if (StringUtils.isNotBlank(repairOrderItemDTO.getUnit()) && productLocalInfoDTO != null && StringUtils.isBlank(productLocalInfoDTO.getSellUnit())) {
          toAddUnitItemDTOs.add(repairOrderItemDTO);
        }
      }
    }
    //更新产品信息
    if (CollectionUtils.isNotEmpty(toUpdateProductDTOs)) {
      productService.updateProductWithRepairOrder(repairOrderDTO.getShopId(), deletedProductIds, toUpdateProductDTOs.toArray(new ProductDTO[toUpdateProductDTOs.size()]));
    }
    //删除商品恢复重新计算上下限
    if (CollectionUtils.isNotEmpty(deletedProductIds)) {
      Map<Long, InventoryDTO> deleteInventoryDTOs = getInventoryService().getInventoryDTOMap(shopId, deletedProductIds);
      if (MapUtils.isNotEmpty(deleteInventoryDTOs)) {
        for (InventoryDTO inventoryDTO : deleteInventoryDTOs.values()) {
          getInventoryService().caculateAfterLimit(inventoryDTO, repairOrderDTO.getInventoryLimitDTO());
        }
      }
    }
    //更新维修单中原先库存存在且不带单位的商品的单位
    getTxnService().updateProductUnit(repairOrderDTO.getShopId(), toAddUnitItemDTOs.toArray(new RepairOrderItemDTO[toAddUnitItemDTOs.size()]));
  }

  @Override
  public List<InventorySearchIndex> updateInventoryAndInventorySearchIndexByRepealedRepairOrderDTO(Long toStorehouseId, RepairOrderDTO repairOrderDTO, TxnWriter writer) throws Exception {
    StopWatchUtil sw = new StopWatchUtil("updateInventoryForRepairRepeal", "getData");
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    List<InventorySearchIndex> newInventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    AllocateRecordDTO allocateRecordDTO = null;
    if (ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      return newInventorySearchIndexList;
    }
    List<Long> productIdList = repairOrderDTO.getProductIdList();
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = null;
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
      storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(repairOrderDTO.getShopId(), repairOrderDTO.getStorehouseId(), productIdList.toArray(new Long[productIdList.size()]));
    }
    sw.stopAndStart("update");
    Set<Long> productIds = new HashSet<Long>();
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      productIds.add(repairOrderItemDTO.getProductId());
    }
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(repairOrderDTO.getShopId(), productIds);
    Map<Long, Inventory> inventoryMap = inventoryService.getInventoryMap(repairOrderDTO.getShopId(), productIds);
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      if (repairOrderItemDTO.getReserved() != null && repairOrderItemDTO.getReserved() > 0) {
        Inventory inventory = inventoryMap.get(repairOrderItemDTO.getProductId());
        inventoryService.caculateBeforeLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
        ProductDTO productDTO = productDTOMap.get(repairOrderItemDTO.getProductId());
        double reservedAmount = repairOrderItemDTO.getReserved();
        double inventoryAmount = inventory.getAmount();
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(repairOrderItemDTO.getProductId());
          inventoryAmount = storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount();
        }
        double inventoryAmountWithItemUnit = inventoryAmount + reservedAmount;
        if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productDTO)) {
          inventoryAmountWithItemUnit = inventoryAmount / productDTO.getRate() + reservedAmount;
          reservedAmount = reservedAmount * productDTO.getRate();
        }
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId())) {
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(repairOrderDTO.getStorehouseId(), repairOrderItemDTO.getProductId(), null, reservedAmount));
        }
        inventory.setAmount(inventory.getAmount() + reservedAmount);
        Sort sort = new Sort(" a.vestDate ", " desc ");
        Long salesVestDate = NumberUtil.longValue(writer.getSalesVestDateByShopId(repairOrderDTO.getShopId(), inventory.getId(), sort));
        Long repairVestDate = NumberUtil.longValue(writer.getRepairVestDateByShopId(repairOrderDTO.getShopId(), inventory.getId(), sort));

        Long lastSalesVestDate = salesVestDate > repairVestDate ? salesVestDate : repairVestDate;
        if (NumberUtil.longValue(lastSalesVestDate) > 0) {
          inventory.setLastSalesTime(lastSalesVestDate);
        } else {
          inventory.setLastSalesTime(null);
        }

        writer.update(inventory);

        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        inventorySearchIndex.createInventorySearchIndex(inventory.toDTO(), productDTO);
        newInventorySearchIndexList.add(inventorySearchIndex);

        repairOrderItemDTO.setInventoryAmount(inventoryAmountWithItemUnit);
        repairOrderItemDTO.setReserved(0d);
        inventoryService.caculateAfterLimit(inventory.toDTO(), repairOrderDTO.getInventoryLimitDTO());
      }
    }
    sw.stopAndStart("productThrough");
    //施工单作废出入库打通
    repairOrderDTO.setSelectSupplier(false);
    ServiceManager.getService(IProductInStorageService.class).productThroughByOrder(repairOrderDTO, OrderTypes.REPAIR, repairOrderDTO.getStatus(), writer);
    sw.stopAndStart("allocate");
    //自动生成调拨单
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId()) && toStorehouseId != null) {
      allocateRecordDTO = createAllocateRecordByRepairOrderDTO(writer, toStorehouseId, repairOrderDTO);
      if (allocateRecordDTO != null) {
        BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
        BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(allocateRecordDTO, OrderTypes.ALLOCATE_RECORD);
        bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
      }
    }
    sw.stopAndPrintLog();
    return newInventorySearchIndexList;
  }

  private AllocateRecordDTO createAllocateRecordByRepairOrderDTO(TxnWriter writer, Long toStorehouseId, RepairOrderDTO repairOrderDTO) throws Exception {
    AllocateRecordDTO allocateRecordDTO = new AllocateRecordDTO();
    allocateRecordDTO.setOutStorehouseId(repairOrderDTO.getStorehouseId());
    allocateRecordDTO.setInStorehouseId(toStorehouseId);
    allocateRecordDTO.setOriginOrderId(repairOrderDTO.getId());
    allocateRecordDTO.setOriginOrderType(OrderTypes.REPAIR);
    allocateRecordDTO.setEditorId(repairOrderDTO.getEditorId());
    allocateRecordDTO.setEditor(repairOrderDTO.getEditor());
    allocateRecordDTO.setEditDate(System.currentTimeMillis());
    allocateRecordDTO.setVestDate(System.currentTimeMillis());
    allocateRecordDTO.setShopId(repairOrderDTO.getShopId());
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    allocateRecordDTO.setReceiptNo(txnService.getReceiptNo(allocateRecordDTO.getShopId(), OrderTypes.ALLOCATE_RECORD, null));

    AllocateRecordItemDTO allocateRecordItemDTO = null;
    Double totalCostPrice = 0d, totalAmount = 0d;
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        allocateRecordItemDTO = new AllocateRecordItemDTO();
        allocateRecordItemDTO.setAmount(repairOrderItemDTO.getAmount());
        allocateRecordItemDTO.setTotalCostPrice(repairOrderItemDTO.getTotalCostPrice());
        allocateRecordItemDTO.setCostPrice(repairOrderItemDTO.getCostPrice());
        allocateRecordItemDTO.setProductHistoryId(repairOrderItemDTO.getProductHistoryId());
        allocateRecordItemDTO.setProductId(repairOrderItemDTO.getProductId());
        allocateRecordItemDTO.setUnit(repairOrderItemDTO.getUnit());
        allocateRecordItemDTO.setStorageBin(repairOrderItemDTO.getStorageBin());
        totalCostPrice += NumberUtil.doubleVal(allocateRecordItemDTO.getTotalCostPrice());
        allocateRecordItemDTO.setOutStorageRelationDTOs(repairOrderItemDTO.getOutStorageRelationDTOs());
        allocateRecordDTO.setSelectSupplier(true);
        totalAmount += NumberUtil.doubleVal(allocateRecordItemDTO.getAmount());
        allocateRecordItemDTOList.add(allocateRecordItemDTO);
      }
      allocateRecordDTO.setItemDTOs(allocateRecordItemDTOList.toArray(new AllocateRecordItemDTO[allocateRecordItemDTOList.size()]));
    }

    allocateRecordDTO.setTotalCostPrice(totalCostPrice);
    allocateRecordDTO.setTotalAmount(totalAmount);
    IAllocateRecordService allocateRecordService = ServiceManager.getService(IAllocateRecordService.class);
    allocateRecordService.saveOrUpdateAllocateRecord(writer, repairOrderDTO.getShopId(), allocateRecordDTO);
    return allocateRecordDTO;
  }

  public CarConstructionInvoiceSearchResultListDTO getRepairOrderByShopId(Long shopId, OrderStatus[] orderStatus, RepairRemindEventTypes repairRemindEventTypes, int pagNo, int pageSize) throws Exception {
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    CarConstructionInvoiceSearchResultListDTO searchResultListDTO = new CarConstructionInvoiceSearchResultListDTO();
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<RepairOrder> list = txnWriter.getRepairOrderByShopId(shopId, orderStatus, repairRemindEventTypes, (pagNo - 1) * pageSize, pageSize);
    if (list != null && CollectionUtils.isNotEmpty(list)) {
      List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>(list.size());
      Set<Long> repairOrderIdSet = new HashSet<Long>();
      for (RepairOrder repairOrder : list) {
        repairOrderIdSet.add(repairOrder.getId());
        repairOrderDTOList.add(repairOrder.toDTO());
      }

      Set<Long> serviceHistoryIdSet = new HashSet<Long>();
      Set<Long> serviceIdSet = new HashSet<Long>();
      Map<Long, List<RepairOrderServiceDTO>> convert = new HashMap<Long, List<RepairOrderServiceDTO>>();

      List<RepairOrderService> repairOrderServiceList = txnWriter.getRepairOrderService(repairOrderIdSet);
      for (RepairOrderService repairOrderService : repairOrderServiceList) {
        if (repairOrderService.getServiceHistoryId() != null) {
          serviceHistoryIdSet.add(repairOrderService.getServiceHistoryId());
        } else if (repairOrderService.getServiceId() != null) {
          serviceIdSet.add(repairOrderService.getServiceId());
        }
      }

      //服务信息和历史服务信息，优先取历史服务中的数据
      Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = null;
      Map<Long, ServiceDTO> serviceDTOMap = null;
      if (CollectionUtils.isNotEmpty(serviceHistoryIdSet)) {
        serviceHistoryDTOMap = serviceHistoryService.getServiceHistoryByServiceHistoryIdSet(shopId, serviceHistoryIdSet);
      }
      if (CollectionUtils.isNotEmpty(serviceIdSet)) {
        serviceDTOMap = getTxnService().getServiceByServiceIdSet(shopId, serviceIdSet);
      }

      //组装车辆施工单服务信息,改信息被组装到车辆施工单服务中
      for (RepairOrderService repairOrderService : repairOrderServiceList) {
        RepairOrderServiceDTO repairOrderServiceDTO = repairOrderService.toDTO();

        //车辆施工单服务信息列表转化成Map，共后续数据组装
        /***************************************************************************************************************/
        List<RepairOrderServiceDTO> temp = convert.get(repairOrderServiceDTO.getRepairOrderId());
        if (temp == null) {
          temp = new ArrayList<RepairOrderServiceDTO>();
          convert.put(repairOrderServiceDTO.getRepairOrderId(), temp);
        }
        temp.add(repairOrderServiceDTO);
        /***************************************************************************************************************/

        if (serviceHistoryDTOMap != null && repairOrderServiceDTO.getServiceHistoryId() != null) {
          ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOMap.get(repairOrderServiceDTO.getServiceHistoryId());
          if (serviceHistoryDTO != null) {
            repairOrderServiceDTO.setService(serviceHistoryDTO.getName());
          }
        }
        if (repairOrderServiceDTO.getService() == null && serviceDTOMap != null && repairOrderServiceDTO.getServiceId() != null) {
          ServiceDTO serviceDTO = serviceDTOMap.get(repairOrderServiceDTO.getServiceId());
          if (serviceDTO != null) {
            repairOrderServiceDTO.setService(serviceDTO.getName());
          }
        }

      }

      //施工材料明细事件数据
      Map<Long, Map<Long, RepairRemindEventDTO>> eventDataMap = getRepairRemindEventsByOrderId(shopId, repairOrderIdSet);
      Map<Long, RepairPickingDTO> repairPickingMap = getRepairPickingByOrderId(repairOrderIdSet);
      //组装车辆施工单中车辆服务信息
      /******************************************************************************************************************/
      for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
        List<RepairOrderServiceDTO> RepairOrderServiceList = convert.get(repairOrderDTO.getId());
        if (RepairOrderServiceList != null && CollectionUtils.isNotEmpty(RepairOrderServiceList)) {
          RepairOrderServiceDTO[] RepairOrderServiceArray = new RepairOrderServiceDTO[RepairOrderServiceList.size()];
          RepairOrderServiceList.toArray(RepairOrderServiceArray);
          repairOrderDTO.setServiceDTOs(RepairOrderServiceArray);
        }
        Map<Long, RepairRemindEventDTO> enentDateTemp = eventDataMap.get(repairOrderDTO.getId());
        //组装施工材料明细
        List<RepairOrderItem> repairOrderItemList = txnWriter.getRepairOrderItemsByOrderId(repairOrderDTO.getId());
        RepairOrderItemDTO[] repairOrderItemDTOArray = new RepairOrderItemDTO[repairOrderItemList.size()];
        int i = 0;
        for (RepairOrderItem repairOrderItem : repairOrderItemList) {
          RepairOrderItemDTO repairOrderItemDTO = repairOrderItem.toDTO();
          repairOrderItemDTOArray[i++] = repairOrderItemDTO;
          if (enentDateTemp != null) {
            repairOrderItemDTO.setRepairRemindEventDTO(enentDateTemp.get(repairOrderItemDTO.getProductId()));
          }
        }
        repairOrderItemSort(repairOrderItemDTOArray);//排序
        repairOrderDTO.setItemDTOs(repairOrderItemDTOArray);
        getProductInfo(repairOrderDTO);

        if (repairPickingMap != null && repairPickingMap.get(repairOrderDTO.getId()) != null) {
          RepairPickingDTO repairPickingDTO = repairPickingMap.get(repairOrderDTO.getId());
          repairOrderDTO.setRepairPickingId(repairPickingDTO.getId());
          repairOrderDTO.setRepairPickingIdStr(repairPickingDTO.getIdStr());
        }
      }
      /******************************************************************************************************************/
      searchResultListDTO.setRepairOrderDTOList(repairOrderDTOList);
      int numFound = txnWriter.getRepairOrderCountByShopId(shopId, orderStatus, repairRemindEventTypes);
      searchResultListDTO.setNumFound(numFound);
    }
    getRepairOrderStatisticsByShopId(searchResultListDTO, shopId);
    return searchResultListDTO;
  }

  @Override
  public Map<Long, List<RepairOrderServiceDTO>> getRepairOrderServiceDTOMap(Set<Long> repairOrderIds) {
    Map<Long, List<RepairOrderServiceDTO>> repairOrderServiceDTOMap = new HashMap<Long, List<RepairOrderServiceDTO>>();
    if (CollectionUtils.isNotEmpty(repairOrderIds)) {
      TxnWriter writer = txnDaoManager.getWriter();
      List<RepairOrderService> repairOrderServices = writer.getRepairOrderService(repairOrderIds);
      if (CollectionUtils.isNotEmpty(repairOrderServices)) {
        Set<Long> serviceHistoryIds = new HashSet<Long>();
        Set<Long> serviceIds = new HashSet<Long>();
        for (RepairOrderService repairOrderService : repairOrderServices) {
          if (repairOrderService != null && repairOrderService.getServiceHistoryId() != null) {
            serviceHistoryIds.add(repairOrderService.getServiceHistoryId());
          }
        }
        Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = getServiceHistoryService().getServiceHistoryByServiceHistoryIdSet(null, serviceHistoryIds);
        List<RepairOrderServiceDTO> repairOrderServiceDTOs = new ArrayList<RepairOrderServiceDTO>();
        for (RepairOrderService repairOrderService : repairOrderServices) {
          if (repairOrderService != null) {
            RepairOrderServiceDTO repairOrderServiceDTO = repairOrderService.toDTO();
            if (repairOrderServiceDTO != null && repairOrderServiceDTO.getServiceHistoryId() != null) {
              ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOMap.get(repairOrderService.getServiceHistoryId());
              if (serviceHistoryDTO != null && StringUtils.isNotBlank(serviceHistoryDTO.getName())) {
                repairOrderServiceDTO.setService(serviceHistoryDTO.getName());
              } else if (repairOrderServiceDTO.getServiceId() != null) {
                serviceIds.add(repairOrderServiceDTO.getServiceId());
              }
            }
            repairOrderServiceDTOs.add(repairOrderServiceDTO);
          }
        }
        Map<Long, ServiceDTO> serviceDTOMap = null;
        if (CollectionUtils.isNotEmpty(serviceIds)) {
          serviceDTOMap = getTxnService().getServiceByServiceIdSet(null, serviceIds);
        }
        if (serviceDTOMap == null) {
          serviceDTOMap = new HashMap<Long, ServiceDTO>();
        }
        if (CollectionUtils.isNotEmpty(serviceIds)) {
          for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
            if (repairOrderServiceDTO != null && StringUtils.isEmpty(repairOrderServiceDTO.getService())
              && repairOrderServiceDTO.getServiceId() != null) {
              ServiceDTO serviceDTO = serviceDTOMap.get(repairOrderServiceDTO.getServiceId());
              if (serviceDTO != null && StringUtils.isNotBlank(serviceDTO.getName())) {
                repairOrderServiceDTO.setService(serviceDTO.getName());
              }
            }
          }
        }
        if (CollectionUtils.isNotEmpty(repairOrderServiceDTOs)) {
          for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
            if (repairOrderServiceDTO != null && repairOrderServiceDTO.getRepairOrderId() != null) {
              List<RepairOrderServiceDTO> repairOrderServiceDTOList = repairOrderServiceDTOMap.get(repairOrderServiceDTO.getRepairOrderId());
              if (repairOrderServiceDTOList == null) {
                repairOrderServiceDTOList = new ArrayList<RepairOrderServiceDTO>();
              }
              repairOrderServiceDTOList.add(repairOrderServiceDTO);
              repairOrderServiceDTOMap.put(repairOrderServiceDTO.getRepairOrderId(), repairOrderServiceDTOList);
            }
          }
        }
      }
    }
    return repairOrderServiceDTOMap;
  }

  public CarConstructionInvoiceSearchResultListDTO getRepairOrderStatisticsByShopId(CarConstructionInvoiceSearchResultListDTO searchResultListDTO, Long shopId) {
    Map<String, Integer> statisticsItem = txnDaoManager.getWriter().getPepairOrderItemStatistics(shopId);
    Map<String, Double> statistics = txnDaoManager.getWriter().getPepairOrderStatistics(shopId, new OrderStatus[]{OrderStatus.REPAIR_DISPATCH, OrderStatus.REPAIR_DONE});

    if (statistics.get("REPAIR_DISPATCH_SUM") != null) {
      searchResultListDTO.setDispatchFee(statistics.get("REPAIR_DISPATCH_SUM"));
    }
    if (statistics.get("REPAIR_DONE_SUM") != null) {
      searchResultListDTO.setPendingFee(statistics.get("REPAIR_DONE_SUM"));
    }

    if (statistics.get("REPAIR_DISPATCH_COUNT") != null) {
      searchResultListDTO.setDispatchTotal(statistics.get("REPAIR_DISPATCH_COUNT").intValue());
    }
    if (statistics.get("REPAIR_DONE_COUNT") != null) {
      searchResultListDTO.setPendingTotal(statistics.get("REPAIR_DONE_COUNT").intValue());
    }

    searchResultListDTO.setTotal(searchResultListDTO.getDispatchTotal() + searchResultListDTO.getPendingTotal());

    if (statisticsItem.get("LACK") != null) {
      searchResultListDTO.setLackTotal(statisticsItem.get("LACK"));
    }
    if (statisticsItem.get("INCOMING") != null) {
      searchResultListDTO.setIncomingTotal(statisticsItem.get("INCOMING"));
    }
    if (statisticsItem.get("WAIT_OUT_STORAGE") != null) {
      searchResultListDTO.setWaitOutStorageTotal(statisticsItem.get("WAIT_OUT_STORAGE"));
    }
    if (statisticsItem.get("OUT_STORAGE") != null) {
      searchResultListDTO.setOutStorageTotal(statisticsItem.get("OUT_STORAGE"));
    }
    if (statisticsItem.get("PENDING") != null) {
      searchResultListDTO.setNormalTotal(statisticsItem.get("PENDING").intValue() - searchResultListDTO.getPendingTotal());
    }
    return searchResultListDTO;
  }

  ;

  private void repairOrderItemSort(RepairOrderItemDTO[] repairOrderItemDTOArray) {
    Arrays.sort(repairOrderItemDTOArray, new Comparator<RepairOrderItemDTO>() {
      private int scoring(RepairRemindEventDTO repairRemindEventDTO) {
        int result = 0;
        if (repairRemindEventDTO != null) {
          result++;
          switch (repairRemindEventDTO.getEventType()) {
            case LACK:
              result = result + 7;
              break;
            case INCOMING:
              result = result + 6;
              break;
            case WAIT_OUT_STORAGE:
              result = result + 5;
              break;
            case OUT_STORAGE:
              result = result + 4;
              break;
            case PENDING:
              result = result + 3;
              break;
            case DEBT:
              result = result + 2;
              break;
            case FINISH:
              result = result + 1;
              break;
          }
        }
        return result;
      }

      public final int compare(RepairOrderItemDTO first, RepairOrderItemDTO second) {
        RepairRemindEventDTO firstEvent = first.getRepairRemindEventDTO();
        RepairRemindEventDTO secondEvent = second.getRepairRemindEventDTO();
        int a = scoring(firstEvent), b = scoring(secondEvent);
        return b - a;
      }
    });
  }

  private Map<Long, Map<Long, RepairRemindEventDTO>> getRepairRemindEventsByOrderId(Long shopId, Set<Long> repairOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long, Map<Long, RepairRemindEventDTO>> result = new HashMap<Long, Map<Long, RepairRemindEventDTO>>();
    Map<Long, List<RepairRemindEventDTO>> tempMap = new HashMap<Long, List<RepairRemindEventDTO>>();

    List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventsByOrderId(shopId, repairOrderId);
    Iterator<RepairRemindEvent> repairRemindEventIterator = repairRemindEvents.iterator();
    while (repairRemindEventIterator.hasNext()) {
      RepairRemindEventDTO dto = repairRemindEventIterator.next().toDTO();
      List<RepairRemindEventDTO> list = tempMap.get(dto.getRepairOrderId());
      if (list == null) {
        list = new ArrayList<RepairRemindEventDTO>();
        tempMap.put(dto.getRepairOrderId(), list);
      }
      list.add(dto);
    }

    Set<Long> tempKey = tempMap.keySet();
    Iterator<Long> it = tempKey.iterator();
    while (it.hasNext()) {
      Long key = it.next();
      List<RepairRemindEventDTO> list = tempMap.get(key);
      if (list != null && CollectionUtils.isNotEmpty(list)) {
        Map<Long, RepairRemindEventDTO> innerMap = new HashMap<Long, RepairRemindEventDTO>();
        Iterator<RepairRemindEventDTO> innerIterator = list.iterator();
        while (innerIterator.hasNext()) {
          RepairRemindEventDTO dto = innerIterator.next();
          innerMap.put(dto.getProductId(), dto);
        }
        result.put(key, innerMap);
      }
    }
    return result;
  }

  public Map<Long, RepairPickingDTO> getRepairPickingByOrderId(Set<Long> repairOrderIdSet) {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long, RepairPickingDTO> result = null;
    List<RepairPicking> repairPickingList = writer.getRepairPickingByOrderId(repairOrderIdSet);
    if (repairPickingList != null && CollectionUtils.isNotEmpty(repairPickingList)) {
      result = new HashMap<Long, RepairPickingDTO>();
      for (RepairPicking repairPicking : repairPickingList) {
        result.put(repairPicking.getRepairOrderId(), repairPicking.toDTO());
      }
    }
    return result;
  }

  public int countRepairOrderByDate(Long shopId, Long startDate, Long endDate) {
    return txnDaoManager.getWriter().countRepairOrderByDate(shopId, startDate, endDate);
  }

  public void getCustomerByTodayServiceVehicle(Long shopId, CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Long fromDate = DateUtil.getStartTimeOfToday(), endDate = DateUtil.getEndTimeOfToday();
    TwoTuple<Integer, Set<Long>> result = writer.getCustomerOfTodayAddVehicle(shopId, fromDate, endDate, searchConditionDTO.getStart(), searchConditionDTO.getRows());
    searchConditionDTO.setToDayAddCustomer(result);
    searchConditionDTO.setIds(result.second.toString().replace("[", "").replace("]", ""));
  }

  public List<String> getTodayServiceVehicleByCustomerId(Long shopId, Set<Long> customerIdSet) throws Exception {
    if (customerIdSet != null && CollectionUtils.isNotEmpty(customerIdSet)) {
      TxnWriter writer = txnDaoManager.getWriter();
      Long fromDate = DateUtil.getStartTimeOfToday(), endDate = DateUtil.getEndTimeOfToday();
      return writer.getTodayServiceVehicleByCustomerId(shopId, customerIdSet, fromDate, endDate);
    } else {
      return null;
    }
  }

  /**
   * 从维修单中获取信息，更新代金券消费记录
   * @param repairOrderDTO
   */
  public void updateConsumingRecordFromRepairOrder(RepairOrderDTO repairOrderDTO) {
    //尝试从repairOrderDTO中获取couponConsumeRecordDTO
    //也可以改成通过repairOrderDTO中的consumingRecordId，从数据库获取couponConsumeRecordDTO(consuming_record表)
    CouponConsumeRecordDTO couponConsumeRecordDTO = repairOrderDTO.getCouponConsumeRecordDTO();
    if (couponConsumeRecordDTO != null) {
      couponConsumeRecordDTO.setOrderId(repairOrderDTO.getId());
      couponConsumeRecordDTO.setOrderStatus(repairOrderDTO.getStatus());
      couponConsumeRecordDTO.setConsumerTime(System.currentTimeMillis());
      couponConsumeRecordDTO.setProduct("施工销售");
      couponConsumeRecordDTO.setSumMoney(repairOrderDTO.getTotal());
      //把用户名和车牌号记录在代金券消费记录的客户信息(customerInfo)中
      String customerName=null;
      String licenceNo=null;
      String customerInfo=null;
      if(null!=repairOrderDTO.getCustomer()&&null!=repairOrderDTO.getCustomer().getName()){
        customerName=repairOrderDTO.getCustomer().getName();
      }else if(null!=repairOrderDTO.getCustomerName()){
        customerName=repairOrderDTO.getCustomerName();
      }
      if(null!=repairOrderDTO.getLicenceNo()){
        licenceNo=repairOrderDTO.getLicenceNo();
      } else if(null!=repairOrderDTO.getVechicle()){
        licenceNo=repairOrderDTO.getVechicle();
      }
      if(null!=customerName&&null!=licenceNo){
        customerInfo=customerName + "/" + licenceNo;
      }else if(null!=customerName){
        customerInfo=customerName;
      }else if(null!=licenceNo){
        customerInfo=licenceNo;
      }
      couponConsumeRecordDTO.setCustomerInfo(customerInfo);
      try {
        //更新receivable
//        ReceivableDTO receivableDTO =getTxnService().getReceivableByShopIdOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
//        if(receivableDTO!=null&&couponConsumeRecordDTO.getCoupon()!=null) {
//          getTxnService().updateReceivableCouponConsume(repairOrderDTO.getShopId(),receivableDTO.getId(),couponConsumeRecordDTO.getCoupon().doubleValue());
//        }
        //更新ConsumingRecord
        ServiceManager.getService(ConsumingService.class).updateConsumingRecordFromOrderInfo(repairOrderDTO.getShopId(), couponConsumeRecordDTO);
      } catch (Exception e) {
        LOG.error("更新receivable出错");
        LOG.error(e.getMessage(), e);
        e.printStackTrace();
      }
    }
  }

  @Override
  public Result accountRepairOrderByCouponConsumingRecord(RepairOrderDTO repairOrderDTO) throws Exception {
    IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
    Long consumingRecordId = repairOrderDTO.getConsumingRecordId();
    CouponConsumeRecordDTO couponConsumeRecordDTO = consumingService.getCouponConsumeRecordById(consumingRecordId);
    if (couponConsumeRecordDTO == null || StringUtil.isEmpty(couponConsumeRecordDTO.getAppUserNo())) {
      return new Result(false);
    }
    repairOrderDTO.setConsumingRecordId(consumingRecordId);
    Long shopId = couponConsumeRecordDTO.getShopId();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
    repairOrderDTO.setTotal(couponConsumeRecordDTO.getCoupon());
    repairOrderDTO.setSettledAmount(0D);
    repairOrderDTO.setCashAmount(0D);  //todo
    repairOrderDTO.setAfterMemberDiscountTotal(0D);
    //添加代金券消费记录信息到repairOrderDTO中
    repairOrderDTO.setCouponConsumeRecordDTO(couponConsumeRecordDTO);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNo(couponConsumeRecordDTO.getAppUserNo()));
    if (appUserCustomerDTO == null) {
      return new Result("客户信息异常", false);
    }
    repairOrderDTO.setCustomerId(appUserCustomerDTO.getCustomerId());
    CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(getUserService().getVehicleByCustomerId(appUserCustomerDTO.getCustomerId()));
    repairOrderDTO.setVechicleId(customerVehicleDTO.getVehicleId());
    repairOrderDTO.setShopId(shopId);
    repairOrderDTO.setShopVersionId(shopDTO.getShopVersionId());
    return doAccountRepairOrder(repairOrderDTO);
  }


  private Result doAccountRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    Long shopId = repairOrderDTO.getShopId();
    LOG.info("施工单自动结算开始!,shopId:{}", shopId);
    //为施工单组装一些静态数据
    repairOrderDTO.setStatus(OrderStatus.REPAIR_SETTLED);
    repairOrderDTO.setVechicle(repairOrderDTO.getLicenceNo());
    repairOrderDTO.setInventoryLimitDTO(new InventoryLimitDTO());
    repairOrderDTO.getInventoryLimitDTO().setShopId(repairOrderDTO.getShopId());
    repairOrderDTO.setStartDate(System.currentTimeMillis());       //进厂时间
    repairOrderDTO.setEndDate(System.currentTimeMillis());       //预计出厂时间
    repairOrderDTO.setEditDate(System.currentTimeMillis());
    repairOrderDTO.setSettleDate(System.currentTimeMillis());
    repairOrderDTO.setEditDate(System.currentTimeMillis());
    repairOrderDTO.setVestDate(System.currentTimeMillis());
    //添加客户信息和车辆信息
    IUserService userService = ServiceManager.getService(IUserService.class);
    repairOrderDTO.setVehicleDTO(userService.getVehicleById(repairOrderDTO.getVechicleId()));
    repairOrderDTO.setCustomerDTO(userService.getCustomerById(repairOrderDTO.getCustomerId()));
    //单据号
    if (StringUtils.isBlank(repairOrderDTO.getReceiptNo())) {
      repairOrderDTO.setReceiptNo(getTxnService().getReceiptNo(repairOrderDTO.getShopId(), OrderTypes.REPAIR, null));
    }
    getCustomerService().updateCustomerRecordForRepairOrder(repairOrderDTO);
    //do account repairOrder
    repairOrderDTO = RFCreateRepairOrder(repairOrderDTO);
    //   afterProcessRepairOrder
    //更新代金券消费记录
    updateConsumingRecordFromRepairOrder(repairOrderDTO);
    //发送微信账单到车主
    ServiceManager.getService(WXTxnService.class).sendConsumeMsg(repairOrderDTO);
    //保存施工单的操作日志
    ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
      new OperationLogDTO(repairOrderDTO.getShopId(), repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.SETTLE));
    //财务统计
    RepairOrderSavedEvent repairOrderSavedEvent = new RepairOrderSavedEvent(repairOrderDTO);
    BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
    bcgogoEventPublisher.publisherRepairOrderSaved(repairOrderSavedEvent);
    repairOrderSavedEvent.setMainFlag(true);
    LOG.info("施工单保存成功,repairOrderId:{}", repairOrderDTO.getId());
    return new Result(true);
  }


  public void overdueConsumingRecordAccount(Long overdueTime) throws Exception {
    //判断参数
    if(null==overdueTime){
      overdueTime=System.currentTimeMillis();
    }
    int start=0;
    int size=50;

    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    List<CouponConsumeRecordDTO> couponConsumeRecordDTOs=getConsumingService().getOverdueConsumingRecord(overdueTime,start,size);
    while(null!=couponConsumeRecordDTOs&&couponConsumeRecordDTOs.size()>1){
      for(CouponConsumeRecordDTO dto:couponConsumeRecordDTOs){
        RepairOrderDTO repairOrderDTO=new RepairOrderDTO();
        Long shopId = dto.getShopId();
        repairOrderDTO.setShopId(shopId);
        AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNo(dto.getAppUserNo()));
        if (appUserCustomerDTO == null) {
//          return new Result("客户信息异常", false);
          LOG.error("过期空白单据自动结算失败:　客户信息异常,consuming_record.id:"+dto.getIdStr());
          getConsumingService().consumingRecordRepeal(shopId,dto.getId());
          continue;
        }
        repairOrderDTO.setConsumingRecordId(dto.getId());
        ShopDTO shopDTO = getConfigService().getShopById(shopId);
        repairOrderDTO.setTotal(dto.getCoupon());
        repairOrderDTO.setSettledAmount(0D);
        repairOrderDTO.setCashAmount(0D);  //todo
        repairOrderDTO.setAfterMemberDiscountTotal(0D);
        //添加代金券消费记录信息到repairOrderDTO中
        repairOrderDTO.setCouponConsumeRecordDTO(dto);
        repairOrderDTO.setCustomerId(appUserCustomerDTO.getCustomerId());
        CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(getUserService().getVehicleByCustomerId(appUserCustomerDTO.getCustomerId()));
        repairOrderDTO.setVechicleId(customerVehicleDTO.getVehicleId());
        repairOrderDTO.setShopVersionId(shopDTO.getShopVersionId());
//        BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(repairOrderDTO.getShopVersionId());//测试一下
        doAccountRepairOrder(repairOrderDTO);
        repairOrderDTO=null;
      }
      couponConsumeRecordDTOs=getConsumingService().getOverdueConsumingRecord(overdueTime,start,size);
    }
  }


}
