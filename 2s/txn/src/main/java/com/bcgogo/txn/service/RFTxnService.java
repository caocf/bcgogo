package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.WholesalerShopRelationDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.enums.txn.preBuyOrder.QuotedResult;
import com.bcgogo.enums.user.RelationChangeEnum;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.notification.dto.CustomerRemindSms;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.model.*;
import com.bcgogo.product.service.IBaseProductService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.*;

@Component
public class RFTxnService implements RFITxnService {
  private static final Logger LOGGER = LoggerFactory.getLogger(RFTxnService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  //  @Autowired
  private ProductDaoManager productDaoManager;

  private IProductService productService;

  private ITxnService txnService;

  private ISearchService searchService;

  private IInventoryService inventoryService;

  private IGoodBuyService goodBuyService;

  private ICustomerService customerService;

  private IUserService userService;

  private IOperationLogService operationLogService;

  private IOrderStatusChangeLogService orderStatusChangeLogService;

  private IConfigService configService;

  private ISupplierService supplierService;

  private ICustomerOrSupplierSolrWriteService supplierSolrWriteService;

  private IProductOutStorageService productOutStorageService;
  private IProductThroughService productThroughService;

  public IProductService getProductService() {
    if (productService == null) {
      productService = ServiceManager.getService(IProductService.class);
    }
    return productService;
  }

  public void setProductService(IProductService productService) {
    this.productService = productService;
  }

  public ITxnService getTxnService() {
    if(txnService == null){
      txnService = ServiceManager.getService(ITxnService.class);
    }
    return txnService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }
  public IProductThroughService getProductThroughService() {
    return productThroughService == null ? ServiceManager.getService(IProductThroughService.class) : productThroughService;
  }

  public void setTxnService(ITxnService txnService) {
    this.txnService = txnService;
  }

  public ISearchService getSearchService() {
    if(searchService == null){
      searchService = ServiceManager.getService(ISearchService.class);
    }
    return searchService;
  }

  public void setSearchService(ISearchService searchService) {
    this.searchService = searchService;
  }

  public IInventoryService getInventoryService() {
    if(inventoryService == null){
      inventoryService = ServiceManager.getService(IInventoryService.class);
    }
    return inventoryService;
  }

  public void setInventoryService(IInventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  public IGoodBuyService getGoodBuyService() {
    if(goodBuyService == null ){
      goodBuyService = ServiceManager.getService(IGoodBuyService.class);
    }
    return goodBuyService;
  }

  public ICustomerService getCustomerService() {
    if(customerService == null){
      customerService = ServiceManager.getService(ICustomerService.class);
    }
    return customerService;
  }

  public IUserService getUserService() {
    if(userService == null){
      userService = ServiceManager.getService(IUserService.class);
    }
    return userService;
  }

  public IOperationLogService getOperationLogService() {
    if(operationLogService == null){
      operationLogService = ServiceManager.getService(IOperationLogService.class);
    }
    return operationLogService;
  }

  public IOrderStatusChangeLogService getOrderStatusChangeLogService() {
    if(orderStatusChangeLogService == null){
      orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
    }
    return orderStatusChangeLogService;
  }

  public IConfigService getConfigService() {
    return configService == null ? ServiceManager.getService(IConfigService.class) : configService;
  }

  public ISupplierService getSupplierService() {
    return supplierService == null ? ServiceManager.getService(ISupplierService.class):supplierService;
  }

  public ICustomerOrSupplierSolrWriteService getSupplierSolrWriteService() {
    return supplierSolrWriteService == null ? ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class):supplierSolrWriteService;
  }

  @Override
  public PurchaseOrderDTO createPurchaseOrder(Long shopId, Long userId, String userName, String supplierId, String productIdStr) throws Exception {
    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.set(shopId, userId, userName);
    //判断是批发商采购还是普通采购
    if (StringUtils.isNotBlank(supplierId)) {
      SupplierDTO supplierDTO = getUserService().getSupplierById(Long.parseLong(supplierId));
      purchaseOrderDTO.setSupplierDTO(supplierDTO);
    }
    if (StringUtils.isNotBlank(productIdStr)) {
      String[] productIds = productIdStr.split(",");
      Set<Long> productIdSet = new HashSet<Long>();
      if (!ArrayUtils.isEmpty(productIds)) {
        for (String productId : productIds) {
          if (NumberUtil.isNumber(productId)) {
            productIdSet.add(Long.parseLong(productId));
          }
        }
      }
      if(CollectionUtils.isNotEmpty(productIdSet)){
        List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = new ArrayList<PurchaseOrderItemDTO>();
        double orderTotal = 0d;
        Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(purchaseOrderDTO.getShopId(), productIdSet);
        Map<Long,InventoryDTO> inventoryDTOMap = getInventoryService().getInventoryDTOMap(purchaseOrderDTO.getShopId(),productIdSet);
        PurchaseOrderItemDTO purchaseOrderItemDTO= null;
        for (Long productId : productIdSet) {
          purchaseOrderItemDTO = new PurchaseOrderItemDTO();
          ProductDTO productDTO = productDTOMap.get(productId);
          if (productDTO != null) {
            purchaseOrderItemDTO.setProductDTOWithOutUnit(productDTO);
            purchaseOrderItemDTO.setUnit(productDTO.getSellUnit());
            purchaseOrderItemDTO.setQuotedPrice(productDTO.getPurchasePrice());
            purchaseOrderItemDTO.setPrice(productDTO.getPurchasePrice());
            purchaseOrderItemDTO.setProductId(productId);
          }
          InventoryDTO inventoryDTO = inventoryDTOMap.get(productId);
          if (inventoryDTO != null) {
            purchaseOrderItemDTO.setInventoryAmount(inventoryDTO.getAmount());
            purchaseOrderItemDTO.setLowerLimit(inventoryDTO.getLowerLimit());
            purchaseOrderItemDTO.setUpperLimit(inventoryDTO.getUpperLimit());
          }
          String kindName = null;

          if(null != productDTO && null != productDTO.getKindId()) {
            KindDTO kindDTO = getProductService().getEnabledKindDTOById(purchaseOrderDTO.getShopId(), productDTO.getKindId());
            if (null != kindDTO) {
              kindName = kindDTO.getName();
            }
          }
          purchaseOrderItemDTO.setProductKind(kindName);
          purchaseOrderItemDTO.setAmount(TxnConstant.ORDER_DEFAULT_AMOUNT);
          double itemAmount = NumberUtil.doubleVal(purchaseOrderItemDTO.getAmount());
          double price = NumberUtil.doubleVal(purchaseOrderItemDTO.getPrice());
          orderTotal += itemAmount * price;
          purchaseOrderItemDTO.setTotal(NumberUtil.round(itemAmount * price, NumberUtil.MONEY_PRECISION));
          purchaseOrderItemDTOList.add(purchaseOrderItemDTO);
        }
        purchaseOrderDTO.setItemDTOs(purchaseOrderItemDTOList.toArray(new PurchaseOrderItemDTO[purchaseOrderItemDTOList.size()]));
        purchaseOrderDTO.setTotal(NumberUtil.round(orderTotal,NumberUtil.MONEY_PRECISION));
      }
    }
    return purchaseOrderDTO;
  }
  @Override
  public PurchaseOrderDTO createPurchaseOrderOnline(Long shopId, Long userId, String userName, SupplierDTO supplierDTO,Map<Long,Pair<Double,Long>> productPairMap) throws Exception {
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.set(shopId, userId, userName);
    purchaseOrderDTO.setSupplierDTO(supplierDTO);
    purchaseOrderDTO.setAreaInfo(AreaCacheManager.getAreaInfo(purchaseOrderDTO.getProvince(), purchaseOrderDTO.getCity(), purchaseOrderDTO.getRegion()));
    List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = new ArrayList<PurchaseOrderItemDTO>();
    double orderTotal = 0d;
    Set<Long> productIdSet=productPairMap.keySet();
    Set<String> productIdStrSet=new HashSet<String>();
    for(Long productId:productIdSet){
      productIdStrSet.add(StringUtil.valueOf(productId));
    }
    Map<Long, ProductDTO> pMap = getProductService().getProductDTOMapByProductLocalInfoIds(purchaseOrderDTO.getSupplierShopId(), productIdSet);
    Map<Long, List<PromotionsDTO>> promotionsMap = promotionsService.getPromotionsDTOMapByProductLocalInfoId(purchaseOrderDTO.getSupplierShopId(), true, productIdSet.toArray(new Long[0]));
    Set<Long> pMapSet = pMap.keySet();
    for (Long productId : pMapSet) {
      ProductDTO productDTO = pMap.get(productId);
      productDTO.setPromotionsDTOs(promotionsMap.get(productId));
    }

    PurchaseOrderItemDTO purchaseOrderItemDTO= null;
//    List<PromotionsDTO> promotionsDTOs = null;
    for (Map.Entry<Long,Pair<Double,Long>> entry : productPairMap.entrySet()){
      ProductDTO productDTO = pMap.get(entry.getKey());
      if (productDTO != null) {
        purchaseOrderItemDTO = new PurchaseOrderItemDTO();
        purchaseOrderItemDTO.setAmount(NumberUtil.doubleVal(entry.getValue().getKey()));
        purchaseOrderItemDTO.setInSalesAmount(productDTO.getInSalesAmount());
        purchaseOrderItemDTO.setShoppingCartItemId(entry.getValue().getValue());
        purchaseOrderItemDTO.setWholesalerProductDTO(productDTO);
        purchaseOrderItemDTO.setUnit(productDTO.getInSalesUnit());
        purchaseOrderItemDTO.setInventoryAmount(productDTO.getInSalesAmount());//批发商的上架量
        purchaseOrderItemDTO.setPrice(NumberUtil.round(productDTO.getInSalesPrice()));
        purchaseOrderItemDTO.setQuotedPrice(NumberUtil.round(productDTO.getInSalesPrice()));
        if(CollectionUtils.isNotEmpty(productDTO.getPromotionsDTOs())){
          String[] ids = new String[productDTO.getPromotionsDTOs().size()];
          int i= 0;
          for(PromotionsDTO promotionsDTO:productDTO.getPromotionsDTOs()){
            ids[i++] = promotionsDTO.getIdStr();
          }
          purchaseOrderItemDTO.setPromotionsId(StringUtils.join(ids, ","));
        }
//        purchaseOrderItemDTO.setPromotionsDTOs(promotionsDTOs);
//        purchaseOrderItemDTO.setPromotionsInfoJson(JsonUtil.listToJson(promotionsDTOs));
        purchaseOrderItemDTO.setTotal(NumberUtil.round(purchaseOrderItemDTO.getAmount()*purchaseOrderItemDTO.getPrice(), NumberUtil.MONEY_PRECISION));
        orderTotal+=purchaseOrderItemDTO.getTotal();
        purchaseOrderItemDTOList.add(purchaseOrderItemDTO);
      }
    }
    purchaseOrderDTO.setItemDTOs(purchaseOrderItemDTOList.toArray(new PurchaseOrderItemDTO[purchaseOrderItemDTOList.size()]));
    purchaseOrderDTO.setTotal(NumberUtil.round(orderTotal,NumberUtil.MONEY_PRECISION));

    getGoodBuyService().setLocalInfoWithProductMapping(purchaseOrderDTO);
    return purchaseOrderDTO;
  }

  private void parsePurchaseOrderDTOAfterGet(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    purchaseOrderDTO.setEditDateStr(DateUtil.convertDateLongToDateString(
      DateUtil.DATE_STRING_FORMAT_DAY, purchaseOrderDTO.getEditDate()));
    purchaseOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(
      DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, purchaseOrderDTO.getVestDate()));
    purchaseOrderDTO.setDeliveryDateStr(DateUtil.convertDateLongToDateString(
      DateUtil.DATE_STRING_FORMAT_DAY, purchaseOrderDTO.getDeliveryDate()));
  }

  private void saveSupplier(SupplierDTO supplierDTO) throws Exception {
    if (supplierDTO.getId() == null) {
      ServiceManager.getService(IUserService.class).createSupplier(supplierDTO);
      ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
    }
  }

  private SupplierDTO saveSupplier(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    SupplierDTO supplierDTO = new SupplierDTO();
    IUserService userService = ServiceManager.getService(IUserService.class);
    if (purchaseOrderDTO.getSupplierId() == null && StringUtils.isNotBlank(purchaseOrderDTO.getSupplier())) {
      List<SupplierDTO> checkSuppliers = userService.getSupplierByNameAndShopId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getSupplier());
      if (CollectionUtils.isNotEmpty(checkSuppliers)) {
        purchaseOrderDTO.setSupplierId(checkSuppliers.get(0).getId());
      }
    }
    supplierDTO.set(purchaseOrderDTO);
    saveSupplier(supplierDTO);
    purchaseOrderDTO.setSupplierId(supplierDTO.getId());
    return supplierDTO;
  }

  private void updateSupplier(SupplierDTO supplierDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    String products = purchaseOrderDTO.getItemDTOs()[0].getProductName();
    //如果 最后入库时间>结算时间 默认不变
    if (supplierDTO.getLastOrderTime() == null || supplierDTO.getLastOrderTime() - purchaseOrderDTO.getVestDate() < 0) {
      supplierDTO.setLastOrderTime(purchaseOrderDTO.getVestDate());
    }
    userService.updateSupplier(supplierDTO, purchaseOrderDTO.getId(), OrderTypes.PURCHASE,
      products, 0d); //purchaseOrderDTO.getTotal()采购金额不计入供应商总额
  }

  private void savePurchasePrice(TxnWriter txnWriter, Long shopId, Long productLocalInfoId, Double price, String memo, String unit) {
    PurchasePrice purchasePrice = new PurchasePrice();
    purchasePrice.setShopId(shopId);
    purchasePrice.setProductId(productLocalInfoId);
    purchasePrice.setPrice(price);
    purchasePrice.setMemo(memo);
    purchasePrice.setDate(System.currentTimeMillis());
    purchasePrice.setUnit(unit);
    txnWriter.save(purchasePrice);
  }

  private void saveOrUpdateInventoryForPurchaseOrder(TxnWriter txnWriter, Long shopId, PurchaseOrderItemDTO purchaseOrderItemDTO,
                                                     InventoryLimitDTO inventoryLimitDTO) throws Exception {
    if(purchaseOrderItemDTO.getProductId() == null || shopId == null){
      LOGGER.error("saveOrUpdateInventoryForPurchaseOrder shopId :{},productId:{}",shopId, purchaseOrderItemDTO.getProductId());
      return;
    }

    Inventory inventory = txnWriter.getInventoryByIdAndshopId(purchaseOrderItemDTO.getProductId(), shopId);
    if (inventory == null) {
      inventory = new Inventory();
      inventory.setId(purchaseOrderItemDTO.getProductId());
      inventory.setShopId(shopId);
      inventory.setAmount(0D);
      inventory.setUnit(purchaseOrderItemDTO.getUnit());
      inventory.setLowerLimit(purchaseOrderItemDTO.getLowerLimit());
      inventory.setUpperLimit(purchaseOrderItemDTO.getUpperLimit());
      txnWriter.save(inventory);
      getInventoryService().caculateAfterLimit(inventory.toDTO(),inventoryLimitDTO);
    }else {
      ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(purchaseOrderItemDTO.getProductId(),shopId);
      getInventoryService().caculateBeforeLimit(inventory.toDTO(),inventoryLimitDTO);
      if(StringUtils.isEmpty(inventory.getUnit())){
        inventory.setUnit(purchaseOrderItemDTO.getUnit());
      }
      double lowerLimit = NumberUtil.doubleVal(purchaseOrderItemDTO.getLowerLimit());
      double upperLimit = NumberUtil.doubleVal(purchaseOrderItemDTO.getUpperLimit());
      if(UnitUtil.isStorageUnit(purchaseOrderItemDTO.getUnit(),productLocalInfoDTO)){
        lowerLimit = lowerLimit * productLocalInfoDTO.getRate();
        upperLimit = upperLimit * productLocalInfoDTO.getRate();
      }
      inventory.setUpperLimit(upperLimit);
      inventory.setLowerLimit(lowerLimit);
      txnWriter.update(inventory);
      getInventoryService().caculateAfterLimit(inventory.toDTO(),inventoryLimitDTO);
    }
  }

  private void addItemIndex(List<ItemIndex> itemIndexList, PurchaseOrderDTO purchaseOrderDTO, OrderTypes orderType,
                            PurchaseOrderItemDTO purchaseOrderItemDTO, ItemTypes itemType) throws Exception {
    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setShopId(purchaseOrderDTO.getShopId());
    itemIndex.setOrderId(purchaseOrderDTO.getId());
    itemIndex.setOrderTypeEnum(orderType);
    itemIndex.setItemId(purchaseOrderItemDTO.getId());
    itemIndex.setItemTypeEnum(itemType);
    itemIndex.setItemName(purchaseOrderItemDTO.getProductName());
    itemIndex.setItemBrand(purchaseOrderItemDTO.getBrand());
    itemIndex.setItemSpec(purchaseOrderItemDTO.getSpec());
    itemIndex.setItemModel(purchaseOrderItemDTO.getModel());
    itemIndex.setItemMemo(purchaseOrderItemDTO.getMemo());
    itemIndex.setVehicleBrand(purchaseOrderItemDTO.getVehicleBrand());
    itemIndex.setVehicleModel(purchaseOrderItemDTO.getVehicleModel());
    itemIndex.setVehicleYear(purchaseOrderItemDTO.getVehicleYear());
    itemIndex.setVehicleEngine(purchaseOrderItemDTO.getVehicleEngine());
    itemIndex.setItemPrice(purchaseOrderItemDTO.getPrice());
    itemIndex.setItemCount(purchaseOrderItemDTO.getAmount());
    itemIndex.setCustomerId(purchaseOrderDTO.getSupplierId());
    itemIndex.setCustomerOrSupplierName(purchaseOrderDTO.getSupplier());
    itemIndex.setOrderStatusEnum(purchaseOrderDTO.getStatus());
    itemIndex.setPaymentTime(purchaseOrderDTO.getDeliveryDate());
    itemIndex.setOrderTimeCreated(System.currentTimeMillis());
    itemIndex.setUnit(purchaseOrderItemDTO.getUnit());
    itemIndex.setProductId(purchaseOrderItemDTO.getProductId());
    itemIndex.setCommodityCode(StringUtils.isNotBlank(purchaseOrderItemDTO.getCommodityCode()) ?
      purchaseOrderItemDTO.getCommodityCode() : null);
    itemIndex.setProductKind(purchaseOrderItemDTO.getProductKind());
    itemIndexList.add(itemIndex);
  }

  private void addInventorySearchIndex(List<InventorySearchIndex> inventorySearchIndexList, PurchaseOrderDTO purchaseOrderDTO,
                                       PurchaseOrderItemDTO purchaseOrderItemDTO) throws Exception {
    InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
    inventorySearchIndex.setShopId(purchaseOrderDTO.getShopId());
    inventorySearchIndex.setProductId(purchaseOrderItemDTO.getProductId());
    inventorySearchIndex.setProductName(purchaseOrderItemDTO.getProductName());
    inventorySearchIndex.setProductBrand(purchaseOrderItemDTO.getBrand());
    inventorySearchIndex.setProductSpec(purchaseOrderItemDTO.getSpec());
    inventorySearchIndex.setProductModel(purchaseOrderItemDTO.getModel());
    inventorySearchIndex.setBrand(purchaseOrderItemDTO.getVehicleBrand());
    inventorySearchIndex.setModel(purchaseOrderItemDTO.getVehicleModel());
    inventorySearchIndex.setYear(purchaseOrderItemDTO.getVehicleYear());
    inventorySearchIndex.setEngine(purchaseOrderItemDTO.getVehicleEngine());
    inventorySearchIndex.setCommodityCode(purchaseOrderItemDTO.getCommodityCode());
    inventorySearchIndex.setProductVehicleStatus(purchaseOrderItemDTO.getProductVehicleStatus());
    InventoryDTO inventoryDTO = getInventoryByShopIdAndProductId(purchaseOrderDTO.getShopId(), purchaseOrderItemDTO.getProductId());
    ProductLocalInfoDTO productLocalInfoDTO = ServiceManager.getService(IProductService.class)
      .getProductLocalInfoById(purchaseOrderItemDTO.getProductId(), purchaseOrderDTO.getShopId());
    if (inventoryDTO != null) {
      inventorySearchIndex.setAmount(inventoryDTO.getAmount());
      inventorySearchIndex.setUnit(inventoryDTO.getUnit());
      inventorySearchIndex.setLowerLimit(inventoryDTO.getLowerLimit());
      inventorySearchIndex.setUpperLimit(inventoryDTO.getUpperLimit());
      inventorySearchIndex.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
    } else {
      inventorySearchIndex.setAmount(0d);
      //根据单位不同，更新不同价格
      if (UnitUtil.isStorageUnit(purchaseOrderItemDTO.getUnit(), productLocalInfoDTO)) {     //存在两个单位，且采购单位为大单位
        inventorySearchIndex.setUnit(purchaseOrderItemDTO.getSellUnit());
        inventorySearchIndex.setLowerLimit(purchaseOrderItemDTO.getLowerLimit() == null ? null :
          purchaseOrderItemDTO.getLowerLimit() * productLocalInfoDTO.getRate());
        inventorySearchIndex.setUpperLimit(purchaseOrderItemDTO.getUpperLimit() == null ? null :
          purchaseOrderItemDTO.getUpperLimit() * productLocalInfoDTO.getRate());
      } else {
        inventorySearchIndex.setUnit(purchaseOrderItemDTO.getSellUnit());
        inventorySearchIndex.setLowerLimit(purchaseOrderItemDTO.getLowerLimit());
        inventorySearchIndex.setUpperLimit(purchaseOrderItemDTO.getUpperLimit());
      }
    }
    if (productLocalInfoDTO != null) {
      inventorySearchIndex.setParentProductId(productLocalInfoDTO.getProductId());
    }
    inventorySearchIndex.setEditDate(purchaseOrderDTO.getEditDate());
    inventorySearchIndex.setKindName(purchaseOrderItemDTO.getProductKind());
    inventorySearchIndexList.add(inventorySearchIndex);
  }

  @Override
  public void addInventorySearchIndex(List<InventorySearchIndex> inventorySearchIndexList, SalesOrderDTO salesOrderDTO,
                                      SalesOrderItemDTO salesOrderItemDTO) throws Exception {
    InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
    inventorySearchIndex.setShopId(salesOrderDTO.getShopId());
    inventorySearchIndex.setProductId(salesOrderItemDTO.getProductId());
    inventorySearchIndex.setProductName(salesOrderItemDTO.getProductName());
    inventorySearchIndex.setProductBrand(salesOrderItemDTO.getBrand());
    inventorySearchIndex.setProductSpec(salesOrderItemDTO.getSpec());
    inventorySearchIndex.setProductModel(salesOrderItemDTO.getModel());
    inventorySearchIndex.setBrand(salesOrderItemDTO.getVehicleBrand());
    inventorySearchIndex.setModel(salesOrderItemDTO.getVehicleModel());
    inventorySearchIndex.setYear(salesOrderItemDTO.getVehicleYear());
    inventorySearchIndex.setEngine(salesOrderItemDTO.getVehicleEngine());
    inventorySearchIndex.setCommodityCode(salesOrderItemDTO.getCommodityCode());
    inventorySearchIndex.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
    InventoryDTO inventoryDTO = getInventoryByShopIdAndProductId(salesOrderDTO.getShopId(), salesOrderItemDTO.getProductId());
    inventorySearchIndex.setAmount(inventoryDTO == null ? 0 : inventoryDTO.getAmount());
    ProductLocalInfoDTO productLocalInfoDTO = ServiceManager.getService(IProductService.class)
      .getProductLocalInfoById(salesOrderItemDTO.getProductId(), salesOrderDTO.getShopId());
    inventorySearchIndex.setUnit(salesOrderItemDTO.getSellUnit());

    if (productLocalInfoDTO != null) {
      inventorySearchIndex.setParentProductId(productLocalInfoDTO.getProductId());
    }
    inventorySearchIndex.setEditDate(salesOrderDTO.getEditDate());
    if (CollectionUtils.isNotEmpty(inventorySearchIndexList)) {
      Iterator<InventorySearchIndex> iterator = inventorySearchIndexList.iterator();
      while (iterator.hasNext()) {
        InventorySearchIndex temp = iterator.next();
        if (temp.getId() != null && temp.getId().equals(inventorySearchIndex.getId())) {
          iterator.remove();
        }
      }
    }
    inventorySearchIndexList.add(inventorySearchIndex);
  }

  public void saveInventoryRemindEvent(TxnWriter txnWriter, PurchaseOrderDTO purchaseOrderDTO, PurchaseOrderItemDTO purchaseOrderItemDTO) {
    InventoryRemindEvent inventoryRemindEvent = new InventoryRemindEvent();
    inventoryRemindEvent.setShopId(purchaseOrderDTO.getShopId());
    inventoryRemindEvent.setPurchaseOrderId(purchaseOrderDTO.getId());
    inventoryRemindEvent.setProductBrand(purchaseOrderItemDTO.getBrand());
    inventoryRemindEvent.setProductModel(purchaseOrderItemDTO.getModel());
    inventoryRemindEvent.setProductName(purchaseOrderItemDTO.getProductName());
    inventoryRemindEvent.setProductSpec(purchaseOrderItemDTO.getSpec());
    inventoryRemindEvent.setSupplier(purchaseOrderDTO.getSupplier());
    inventoryRemindEvent.setAmount(purchaseOrderItemDTO.getAmount());
    inventoryRemindEvent.setUnit(purchaseOrderItemDTO.getUnit());//todo to check unit
    inventoryRemindEvent.setPrice(purchaseOrderItemDTO.getPrice());
    inventoryRemindEvent.setContent(StringUtil.longToString(purchaseOrderItemDTO.getProductId(), null));
    inventoryRemindEvent.setDeliverTime(purchaseOrderDTO.getDeliveryDate());
    txnWriter.save(inventoryRemindEvent);
    ServiceManager.getService(ITxnService.class).saveRemindEvent(inventoryRemindEvent);
    //更新缓存
    getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.TXN, inventoryRemindEvent.getShopId());
  }

  private void saveOrUpdatePurchaseOrder(TxnWriter txnWriter, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    OrderStatus lastOrderStatus = null;
    PurchaseOrder purchaseOrder = new PurchaseOrder();
    OperationTypes operationTypes = OperationTypes.CREATE;
    if (purchaseOrderDTO.getId() != null) {
      operationTypes = OperationTypes.UPDATE;
      List<PurchaseOrder> purchaseOrders = txnWriter.getPurchaseOrderById(purchaseOrderDTO.getId(),purchaseOrderDTO.getShopId());
      if(CollectionUtils.isNotEmpty(purchaseOrders)){
        purchaseOrder = purchaseOrders.get(0);
        lastOrderStatus = purchaseOrder.getStatusEnum();
      }
    }
    purchaseOrder.fromDTO(purchaseOrderDTO);
    txnWriter.saveOrUpdate(purchaseOrder);
    purchaseOrderDTO.setId(purchaseOrder.getId());

    //保存操作记录
    getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getUserId(), purchaseOrderDTO.getId(), ObjectTypes.PURCHASE_ORDER,operationTypes));
    //保存单据状态变更记录
    getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getUserId(), purchaseOrderDTO.getStatus(), lastOrderStatus, purchaseOrderDTO.getId(), OrderTypes.PURCHASE));
    //add by WLF 更新缓存中待办采购单的数量
    List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);
  }

  private void savePurchaseOrderItem(TxnWriter txnWriter, Long purchaseOrderId, PurchaseOrderItemDTO purchaseOrderItemDTO) throws Exception {
    PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
    purchaseOrderItem.fromDTO(purchaseOrderItemDTO);
    purchaseOrderItem.setPurchaseOrderId(purchaseOrderId);
    txnWriter.save(purchaseOrderItem);

    purchaseOrderItemDTO.setId(purchaseOrderItem.getId());
    //更新 报价单
    if(purchaseOrderItem.getQuotedPreBuyOrderItemId()!=null){
      QuotedPreBuyOrderItem quotedPreBuyOrderItem = txnWriter.getById(QuotedPreBuyOrderItem.class,purchaseOrderItem.getQuotedPreBuyOrderItemId());
      quotedPreBuyOrderItem.setQuotedResult(QuotedResult.Orders);
      txnWriter.saveOrUpdate(quotedPreBuyOrderItem);
    }
  }

  @Override
  public void saveOrUpdatePurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ProductSupplierDTO> productSupplierDTOs = new ArrayList<ProductSupplierDTO>();
    Object status = txnWriter.begin();
    try {
      List<InventorySearchIndex> purchaseInventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      //普通采购单不填SupplierShopId
      purchaseOrderDTO.setSupplierShopId(null);
      saveOrUpdatePurchaseOrder(txnWriter, purchaseOrderDTO);

      for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(purchaseOrderItemDTO.getProductName())) {
          continue;
        }
        purchaseOrderItemDTO.setPrice(NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION));
        purchaseOrderItemDTO.setAmount(NumberUtil.round(purchaseOrderItemDTO.getAmount(), NumberUtil.AMOUNT_PRECISION));
        savePurchaseOrderItem(txnWriter, purchaseOrderDTO.getId(), purchaseOrderItemDTO);

        saveOrUpdateInventoryForPurchaseOrder(txnWriter, purchaseOrderDTO.getShopId(), purchaseOrderItemDTO, purchaseOrderDTO.getInventoryLimitDTO());
        savePurchasePrice(txnWriter, purchaseOrderDTO.getShopId(), purchaseOrderItemDTO.getProductId(),purchaseOrderItemDTO.getPrice(), purchaseOrderItemDTO.getMemo(), purchaseOrderItemDTO.getUnit());
        saveInventoryRemindEvent(txnWriter, purchaseOrderDTO, purchaseOrderItemDTO);
        addInventorySearchIndex(purchaseInventorySearchIndexList, purchaseOrderDTO, purchaseOrderItemDTO);
      }

      txnWriter.commit(status);
      //更新memcach 库存上下限 超出，缺料
      iInventoryService.updateMemocacheLimitByInventoryLimitDTO(purchaseOrderDTO.getShopId(),purchaseOrderDTO.getInventoryLimitDTO());
      getInventoryService().addOrUpdateInventorySearchIndexWithList(purchaseOrderDTO.getShopId(), purchaseInventorySearchIndexList);
    } finally {
      txnWriter.rollback(status);
    }
    //add by WLF 更新缓存中待办采购单的数量
    List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);
  }

  @Override
  public void saveOrUpdatePurchaseOrderOnline(PurchaseOrderDTO purchaseOrderDTO,SalesOrderDTO salesOrderDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      List<InventorySearchIndex> purchaseInventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      Set<Long> supplierProductIdSet = new HashSet<Long>();
      for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
        if (purchaseOrderItemDTO.getSupplierProductId() != null) {
          supplierProductIdSet.add(purchaseOrderItemDTO.getSupplierProductId());
        }
      }
      //计算最新促销后的 价格
      Map<Long, ProductDTO> supplierProductDTOMap = productService.getProductDTOMapByProductLocalInfoIds(purchaseOrderDTO.getSupplierShopId(), supplierProductIdSet);
      calculatePurchasePrice(purchaseOrderDTO, supplierProductDTOMap, supplierProductIdSet);

      //改单逻辑
      if (purchaseOrderDTO.getId() != null) {
        List<PurchaseOrderItem> purchaseOrderItems = txnWriter.getPurchaseOrderItemsByOrderId(purchaseOrderDTO.getId());
        if (CollectionUtils.isNotEmpty(purchaseOrderItems)) {
          for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            txnWriter.delete(purchaseOrderItem);
          }
        }
      }

      saveOrUpdatePurchaseOrder(txnWriter, purchaseOrderDTO);

      for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(purchaseOrderItemDTO.getProductName())) {
          continue;
        }
        purchaseOrderItemDTO.setPrice(NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION));
        purchaseOrderItemDTO.setAmount(NumberUtil.round(purchaseOrderItemDTO.getAmount()));
        savePurchaseOrderItem(txnWriter, purchaseOrderDTO.getId(), purchaseOrderItemDTO);

        if(purchaseOrderItemDTO.getShoppingCartItemId()!=null){
          txnWriter.delete(ShoppingCartItem.class, purchaseOrderItemDTO.getShoppingCartItemId());
        }
      }

      createWholesalerSaleOrder(purchaseOrderDTO, salesOrderDTO, supplierProductDTOMap);
      txnWriter.commit(status);
      getInventoryService().addOrUpdateInventorySearchIndexWithList(purchaseOrderDTO.getShopId(), purchaseInventorySearchIndexList);
    } finally {
      txnWriter.rollback(status);
    }
    savePromotionRecord(purchaseOrderDTO);
    //add by WLF 更新缓存中待办采购单的数量
    List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);
    //add by WLF 更新缓存中待办销售单的数量
    if (salesOrderDTO.getShopId() != null) {
      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrderDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrderDTO.getShopId(), customerIdList);
    }
  }

  private void savePromotionRecord(PurchaseOrderDTO purchaseOrderDTO){
    ServiceManager.getService(IPromotionsService.class).savePromotionOrderRecord(purchaseOrderDTO);
    //item的和促销record的关联
    Map<Long,PromotionOrderRecordDTO> recordMap=ServiceManager.getService(IPromotionsService.class).getPromotionOrderRecordDTOMap(purchaseOrderDTO.getId());
    if(recordMap!=null&&!recordMap.keySet().isEmpty()){
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        for (PurchaseOrderItemDTO itemDTO : purchaseOrderDTO.getItemDTOs()) {
          PromotionOrderRecordDTO recordDTO=recordMap.get(itemDTO.getSupplierProductId());
          if(recordDTO==null){
            continue;
          }
          OrderItemPromotion itemPromotion=new OrderItemPromotion();
          itemPromotion.setOrderItemId(itemDTO.getId());
          itemPromotion.setOrderTypes(OrderTypes.PURCHASE);
          itemPromotion.setPromotionOrderRecordId(recordDTO.getId());
          writer.save(itemPromotion);
        }
        writer.commit(status);
      }catch (Exception e){
        LOGGER.error(e.getMessage(),e);
      }finally {
        writer.rollback(status);
      }
    }
  }

  public void saveOrUpdatePurchaseOrderOnlineNotCreateSalesOrderDTO(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      Set<Long> supplierProductIdSet = new HashSet<Long>();
      for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
        if (purchaseOrderItemDTO.getSupplierProductId() != null) {
          supplierProductIdSet.add(purchaseOrderItemDTO.getSupplierProductId());
        }
      }
      //计算最新促销后的 价格
      Map<Long, ProductDTO> supplierProductDTOMap = productService.getProductDTOMapByProductLocalInfoIds(purchaseOrderDTO.getSupplierShopId(), supplierProductIdSet);
      calculatePurchasePrice(purchaseOrderDTO, supplierProductDTOMap, supplierProductIdSet);

      //改单逻辑
      if (purchaseOrderDTO.getId() != null) {
        List<PurchaseOrderItem> purchaseOrderItems = txnWriter.getPurchaseOrderItemsByOrderId(purchaseOrderDTO.getId());
        if (CollectionUtils.isNotEmpty(purchaseOrderItems)) {
          for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            txnWriter.delete(purchaseOrderItem);
          }
        }
      }

      saveOrUpdatePurchaseOrder(txnWriter, purchaseOrderDTO);

      for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(purchaseOrderItemDTO.getProductName())) {
          continue;
        }
        purchaseOrderItemDTO.setPrice(NumberUtil.round(purchaseOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION));
        purchaseOrderItemDTO.setAmount(NumberUtil.round(purchaseOrderItemDTO.getAmount()));
        savePurchaseOrderItem(txnWriter, purchaseOrderDTO.getId(), purchaseOrderItemDTO);

        if(purchaseOrderItemDTO.getShoppingCartItemId()!=null){
          txnWriter.delete(ShoppingCartItem.class, purchaseOrderItemDTO.getShoppingCartItemId());
        }
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
     savePromotionRecord(purchaseOrderDTO);
      //add by WLF 更新缓存中待办采购单的数量
    List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);
    //add by WLF 更新缓存中待办销售单的数量    todo 需要更新方法byqxy
//    if (salesOrderDTO.getShopId() != null) {
//      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrderDTO.getShopId());
//      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrderDTO.getShopId(), customerIdList);
//    }
  }

  private void calculatePurchasePrice(PurchaseOrderDTO purchaseOrderDTO, Map<Long, ProductDTO> supplierProductDTOMap, Set<Long> supplierProductIdSet) throws Exception {
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    Map<Long, List<PromotionsDTO>> promotionsMap = promotionsService.getPromotionsDTOMapByProductLocalInfoId(purchaseOrderDTO.getSupplierShopId(), true, supplierProductIdSet.toArray(new Long[0]));
    Set<Long> pMapSet = supplierProductDTOMap.keySet();
    //过滤出使用中的促销
    if(purchaseOrderDTO.getId()==null){
      for(Long productId:pMapSet){
        ProductDTO productDTO = supplierProductDTOMap.get(productId);
        List<PromotionsDTO> promotionsDTOs=promotionsMap.get(productId);
        List<PromotionsDTO> usingPromotions=null;
        if(CollectionUtil.isNotEmpty(promotionsDTOs)){
          for (PromotionsDTO promotionsDTO:promotionsDTOs){
            if(promotionsDTO==null||!PromotionsEnum.PromotionStatus.USING.equals(promotionsDTO.getStatus())){
              continue;
            }
            if(usingPromotions==null){
              usingPromotions=new ArrayList<PromotionsDTO>();
            }
            usingPromotions.add(promotionsDTO);
          }
          productDTO.setPromotionsDTOs(usingPromotions);
        }
      }
    }else{
      List<PromotionOrderRecordDTO> orderRecordDTOs = promotionsService.getPromotionOrderRecordDTO(purchaseOrderDTO.getId());
      Map<Long, List<PromotionOrderRecordDTO>> recordMap = new HashMap<Long, List<PromotionOrderRecordDTO>>();
      if (CollectionUtil.isNotEmpty(orderRecordDTOs)) {
        for (PromotionOrderRecordDTO recordDTO : orderRecordDTOs) {
          List<PromotionOrderRecordDTO> recordDTOList = recordMap.get(recordDTO.getProductId());
          if (recordDTOList == null) {
            recordDTOList = new ArrayList<PromotionOrderRecordDTO>();
            recordMap.put(recordDTO.getProductId(), recordDTOList);
          }
          recordDTOList.add(recordDTO);
        }
      }
      for(Long productId:pMapSet){
        ProductDTO productDTO = supplierProductDTOMap.get(productId);
        if(productDTO==null){
          continue;
        }
        productDTO.setPromotionsDTOs(PromotionsUtils.generatePromotionsFromRecord(recordMap.get(productId)));
      }
    }
    Map<String,String> promotionsInfoMap=promotionsService.calculateOrderTotal(purchaseOrderDTO,supplierProductDTOMap);
    if(promotionsInfoMap.keySet().size()>0){
      purchaseOrderDTO.setPromotionsInfoJson(JsonUtil.mapToJson(promotionsInfoMap));
    }

  }

  /**
   * 采购下单的时候同时创建在线销售单逻辑
   * @param purchaseOrderDTO
   * @param salesOrderDTO
   * @param supplierProductDTOMap
   * @return
   * @throws Exception
   */
  private SalesOrderDTO createWholesalerSaleOrder(PurchaseOrderDTO purchaseOrderDTO, SalesOrderDTO salesOrderDTO,Map<Long,ProductDTO> supplierProductDTOMap) throws Exception {
    if (ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())) {
      LOGGER.error("采购单的Item为空，无法生成销售单");
      return null;
    }
    Long supplierShopId = purchaseOrderDTO.getSupplierShopId();
    Long customerShopId = purchaseOrderDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    if (salesOrderDTO == null) {
      salesOrderDTO = new SalesOrderDTO();
    }
    //保存新的销售单
    salesOrderDTO.setPurchaseOrderDTO(purchaseOrderDTO);
    if(StringUtils.isBlank(salesOrderDTO.getReceiptNo())){
      salesOrderDTO.setReceiptNo(getTxnService().getReceiptNo(salesOrderDTO.getShopId(), OrderTypes.SALE, null));
    }
    CustomerDTO customerDTO = getUserService().getCustomerByCustomerShopIdAndShopId(supplierShopId, customerShopId);
    if (customerDTO == null) {
      LOGGER.error("批发商下无此客户，无法生成销售单");
      return null;
    }
    salesOrderDTO.setCustomerDTO(customerDTO);

    //采购改单逻辑,删除旧的采购单
    if (salesOrderDTO.getId() != null) {
      List<SalesOrderItem> salesOrderItems = writer.getSalesOrderItemsByOrderId(salesOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(salesOrderItems)) {
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          writer.delete(salesOrderItem);
        }
      }
    }
    salesOrderDTO.setStatus(OrderStatus.PENDING);

    SalesOrder salesOrder = new SalesOrder();
    OrderStatus lastOrderStatus = null;
    if (salesOrderDTO.getId() != null) {
      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        salesOrder = salesOrders.get(0);
        lastOrderStatus = salesOrders.get(0).getStatusEnum();
      }
    }
    salesOrder.fromDTO(salesOrderDTO);
    writer.saveOrUpdate(salesOrder);

    if(salesOrderDTO.getId()==null){
      //ad by WLF 保存销售单的创建日志
      getTxnService().saveOperationLogTxnService(
        new OperationLogDTO(salesOrder.getShopId(), purchaseOrderDTO.getUserId(), salesOrder.getId(), ObjectTypes.SALE_ORDER, OperationTypes.CREATE));

    }else{
      //ad by WLF 保存销售单的更新日志
      getTxnService().saveOperationLogTxnService(
        new OperationLogDTO(salesOrder.getShopId(), purchaseOrderDTO.getUserId(), salesOrder.getId(), ObjectTypes.SALE_ORDER, OperationTypes.UPDATE));
    }
    //add by WLF 更新缓存中待办销售单和采购的数量
    List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrder.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrder.getShopId(), customerIdList);
    List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);

    salesOrderDTO.setId(salesOrder.getId());
    //保存单据状态变更记录
    getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getStatus(), lastOrderStatus, salesOrderDTO.getId(), OrderTypes.PURCHASE));


    if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      Set<Long> productIds = new HashSet<Long>();
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        productIds.add(salesOrderItemDTO.getProductId());
      }
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class).getOrSaveProductHistoryByLocalInfoId(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        salesOrderItemDTO.setSalesOrderId(salesOrderDTO.getId());
        ProductDTO productDTO = supplierProductDTOMap.get(salesOrderItemDTO.getProductId());
        if (productDTO != null) {
          salesOrderItemDTO.setProductDTOWithOutUnit(productDTO);
          salesOrderItemDTO.setUnit(productDTO.getSellUnit());
        }
        salesOrderItemDTO.setReserved(0d);
        ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(salesOrderItemDTO.getProductId());
        salesOrderItemDTO.setProductHistoryId(productHistoryDTO == null ? null : productHistoryDTO.getId());
        SalesOrderItem salesOrderItem = new SalesOrderItem();
        salesOrderItem.fromDTO(salesOrderItemDTO);
        writer.save(salesOrderItem);
        salesOrderItemDTO.setId(salesOrderItem.getId());
      }
    }
    return salesOrderDTO;
  }

  @Override
  public PurchaseOrderDTO getPurchaseOrderDTOById(Long id, Long shopId) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    PurchaseOrder purchaseOrder = null;
    PurchaseOrderDTO purchaseOrderDTO = null;
    List<PurchaseOrder> purchaseOrders = txnWriter.getPurchaseOrderById(id, shopId);
    if (purchaseOrders != null && purchaseOrders.size() > 0) {
      purchaseOrder = purchaseOrders.get(0);
    }
    if (null != purchaseOrder) {
      purchaseOrderDTO = generatePurchaseOrderDTO(purchaseOrder);
    }
    return purchaseOrderDTO;
  }

  public PurchaseOrderDTO getPurchaseOrderDTOByIdAndSupplierShopId(Long id, Long supplierShopId) throws Exception {
    if(id == null || supplierShopId == null){
      return null;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    PurchaseOrder purchaseOrder = null;
    PurchaseOrderDTO purchaseOrderDTO = null;
    List<PurchaseOrder> purchaseOrders = txnWriter.getPurchaseOrderBySupplierShopIdAndIds(supplierShopId, id);
    if (purchaseOrders != null && purchaseOrders.size() > 0) {
      purchaseOrder = purchaseOrders.get(0);
    }
    if (null != purchaseOrder) {
      purchaseOrderDTO = generatePurchaseOrderDTO(purchaseOrder);
    }
    return purchaseOrderDTO;
  }

  //todo 下面代码查询逻辑可以优化
  private PurchaseOrderDTO generatePurchaseOrderDTO (PurchaseOrder purchaseOrder)throws Exception{
    if(purchaseOrder == null){
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseOrderDTO purchaseOrderDTO  = purchaseOrder.toDTO();
    purchaseOrderDTO.setId(purchaseOrder.getId());
    parsePurchaseOrderDTOAfterGet(purchaseOrderDTO);
    List<PurchaseOrderItem> purchaseOrderItemList = writer.getPurchaseOrderItemsByOrderId(purchaseOrder.getId());
    PurchaseOrderItemDTO[] purchaseOrderItemDTOs = new PurchaseOrderItemDTO[purchaseOrderItemList.size()];
    purchaseOrderDTO.setItemDTOs(purchaseOrderItemDTOs);
    for (int i = 0; i < purchaseOrderItemList.size(); i++) {
      purchaseOrderItemDTOs[i] = purchaseOrderItemList.get(i).toDTO();
      if (purchaseOrderDTO.getSupplierShopId() != null) {//在线采购单
        ProductHistoryDTO supplierProductHistoryDTO = ServiceManager.getService(IProductHistoryService.class)
            .getProductHistoryById(purchaseOrderItemDTOs[i].getProductHistoryId(), purchaseOrderDTO.getSupplierShopId());
        ProductDTO supplierProductDTO = ServiceManager.getService(IProductService.class)
            .getProductByProductLocalInfoId(purchaseOrderItemDTOs[i].getSupplierProductId(), purchaseOrderDTO.getSupplierShopId());
        if (supplierProductHistoryDTO != null) {
          purchaseOrderItemDTOs[i].setWholesalerProductHistoryDTO(supplierProductHistoryDTO);
        } else {
          purchaseOrderItemDTOs[i].setWholesalerProductDTO(supplierProductDTO);
        }
      } else {
        ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class)
            .getProductHistoryById(purchaseOrderItemDTOs[i].getProductHistoryId(), purchaseOrder.getShopId());
        ProductDTO productDTO = ServiceManager.getService(IProductService.class)
            .getProductByProductLocalInfoId(purchaseOrderItemDTOs[i].getProductId(), purchaseOrderDTO.getShopId());
        if (productHistoryDTO != null) {
          purchaseOrderItemDTOs[i].setProductHistoryDTO(productHistoryDTO);
          if (OrderUtil.purchaseOrderInProgress.contains(purchaseOrderDTO.getStatus())) {
            purchaseOrderItemDTOs[i].setProductUnitRateInfo(productDTO);
          }
        } else {
          purchaseOrderItemDTOs[i].setProductDTOWithOutUnit(productDTO);
        }
        Inventory inventory = writer.getById(Inventory.class, purchaseOrderItemDTOs[i].getProductId());
        if (UnitUtil.isStorageUnit(purchaseOrderItemDTOs[i].getUnit(), productDTO)) {
          purchaseOrderItemDTOs[i].setInventoryAmount(inventory.getAmount() / productDTO.getRate());
          purchaseOrderItemDTOs[i].setLowerLimit(inventory.getLowerLimit() == null ? null : inventory.getLowerLimit() / productDTO.getRate());
          purchaseOrderItemDTOs[i].setUpperLimit(inventory.getUpperLimit() == null ? null : inventory.getUpperLimit() / productDTO.getRate());
          purchaseOrderItemDTOs[i].setTradePrice(productDTO.getTradePrice() == null ? null : productDTO.getTradePrice() * productDTO.getRate());
        } else {
          purchaseOrderItemDTOs[i].setInventoryAmount(inventory.getAmount());
          purchaseOrderItemDTOs[i].setLowerLimit(inventory.getLowerLimit());
          purchaseOrderItemDTOs[i].setUpperLimit(inventory.getUpperLimit());
        }
      }
    }
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(new Long(purchaseOrderDTO.getShopId()));
    purchaseOrderDTO.set(shopDTO);
    if (purchaseOrderDTO.getExpressId() != null) {
      purchaseOrderDTO.setExpressDTO(getExpressDTOById(purchaseOrderDTO.getExpressId()));
    }
    return purchaseOrderDTO;
  }

  @Override
  public RepairOrderDTO getRepairOrderDTOById(Long id, Long shopId) throws Exception {
    if(id == null || shopId == null){
      return  null;
    }
    RepairOrder repairOrder = txnDaoManager.getWriter().getRepairOrderById(id,shopId);
    return repairOrder==null?null:repairOrder.toDTO();
  }

  @Override
  public RepairOrderDTO getRepairOrderDTODetailById(Long id, Long shopId) throws Exception {
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    RepairOrderDTO repairOrderDTO = getRepairOrderDTOById(id, shopId);
    if (repairOrderDTO != null) {
      List<RepairOrderItem> items = writer.getRepairOrderItemsByOrderId(id);
      RepairOrderItemDTO[] itemDTOs = new RepairOrderItemDTO[items.size()];
      repairOrderDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        RepairOrderItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      List<RepairOrderService> repairOrderServiceList = writer.getRepairOrderServicesByOrderId(id);
      RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[repairOrderServiceList.size()];
      repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);
      Set<Long> serviceHistoryIds = new HashSet<Long>();
      Set<Long> noHistoryServiceIds = new HashSet<Long>();

      if(CollectionUtils.isNotEmpty(repairOrderServiceList)){
        for(RepairOrderService repairOrderService :repairOrderServiceList){
          if(repairOrderService.getServiceHistoryId() != null) {
            serviceHistoryIds.add(repairOrderService.getId());
          }else{
            if(repairOrderService.getServiceId() != null) {
              noHistoryServiceIds.add(repairOrderService.getServiceId());
            }
          }
        }
      }
      Map<Long,ServiceHistoryDTO> serviceHistoryDTOMap = serviceHistoryService.getServiceHistoryByServiceHistoryIdSet(shopId,serviceHistoryIds);
      for (int i = 0; i < repairOrderServiceList.size(); i++) {
        RepairOrderService repairOrderService = repairOrderServiceList.get(i);
        repairOrderServiceDTOs[i] = repairOrderService.toDTO();
        ServiceHistoryDTO serviceHistoryDTO = null;
        if (repairOrderService.getServiceHistoryId() != null) {
          serviceHistoryDTO = serviceHistoryDTOMap.get(repairOrderService.getServiceHistoryId());
        }
        if (serviceHistoryDTO != null) {
          repairOrderServiceDTOs[i].setService(serviceHistoryDTO.getName());
        } else {
          if (repairOrderService.getServiceId() != null) {
            noHistoryServiceIds.add(repairOrderService.getServiceId());
          }
        }
      }
      if (CollectionUtil.isNotEmpty(noHistoryServiceIds)) {
        Map<Long, ServiceDTO> serviceDTOMap = getTxnService().getServiceByServiceIdSet(shopId, noHistoryServiceIds);
        for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
          if (repairOrderServiceDTO.getServiceId() != null && noHistoryServiceIds.contains(repairOrderServiceDTO.getServiceId())) {
            ServiceDTO serviceDTO = serviceDTOMap.get(repairOrderServiceDTO.getServiceId());
            if (serviceDTO != null) {
              repairOrderServiceDTO.setService(serviceDTO.getName());
            }
          }
        }
      }
      List<RepairOrderOtherIncomeItem> otherIncomeItemList = writer.getRepairOtherIncomeItemByOrderId(shopId, repairOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(otherIncomeItemList)) {
        List<RepairOrderOtherIncomeItemDTO> incomeItemDTOList = new ArrayList<RepairOrderOtherIncomeItemDTO>();
        for (RepairOrderOtherIncomeItem incomeItem : otherIncomeItemList) {
          incomeItemDTOList.add(incomeItem.toDTO());
        }
        repairOrderDTO.setOtherIncomeItemDTOList(incomeItemDTOList);
      }
      return repairOrderDTO;
    }
    return null;
  }

  @Override
  public InventoryDTO getInventoryByShopIdAndProductId(Long shopId, Long productId) throws Exception {
    Inventory inventory = txnDaoManager.getWriter().getInventoryByIdAndshopId(productId,shopId);
    if (inventory == null) {
      return null;
    }
    InventoryDTO inventoryDTO = new InventoryDTO();
    inventoryDTO.setId(inventory.getId());
    inventoryDTO = inventory.toDTO();
    return inventoryDTO;
  }

  @Override
  public PurchasePriceDTO getLatestPurchasePriceByShopIdAndProductId(Long shopId, Long productId) throws Exception {
    PurchasePrice purchasePrice = txnDaoManager.getWriter().getLatestPurchasePriceByShopIdAndProductId(shopId, productId);
    if (purchasePrice == null) return null;
    PurchasePriceDTO purchasePriceDTO = purchasePrice.toDTO();
    return purchasePriceDTO;
  }

  @Override
  public void saveOrUpdatePurchaseInventory(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {

  }

  @Override
  public PurchaseInventoryDTO getPurchaseInventoryById(Long id) throws Exception {
    return null;
  }

  /**
   * 维修单 所填车辆若为新车型，则新增此车辆，并将车辆品牌，品名，规格，型号ID保存到此维修单
   * 不保存solr，如果需要存solr 在service外部调用
   * @return
   * @throws Exception
   * @author wjl
   */
  @Override
  public VehicleDTO populateVehicleAppointOrderDTO(AppointOrderDTO appointOrderDTO) throws Exception {
    VehicleDTO vehicleDTO = null;
    if (appointOrderDTO == null) {
      return vehicleDTO;
    }
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    if (StringUtils.isNotBlank(appointOrderDTO.getVehicleBrand())) {
      vehicleDTO = baseProductService.addVehicleToDB(appointOrderDTO.getVehicleBrand(), appointOrderDTO.getVehicleModel(), null, null);
      appointOrderDTO.setVehicleBrandId(vehicleDTO.getVirtualBrandId());
      appointOrderDTO.setVehicleModelId(vehicleDTO.getVirtualModelId());
      appointOrderDTO.setAddVehicleToSolr(true);
    }
    return vehicleDTO;
  }

  /**
   * 维修单 所填车辆若为新车型，则新增此车辆，并将车辆品牌，品名，规格，型号ID保存到此维修单
   * 不保存solr，如果需要存solr 在service外部调用
   * @param repairOrderDTO
   * @return
   * @throws Exception
   * @author wjl
   */
  @Override
  public List<VehicleDTO> populateRepairOrderDTO(RepairOrderDTO repairOrderDTO) throws Exception {
    List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
    if (repairOrderDTO == null) {
      return vehicleDTOs;
    }
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    if ((repairOrderDTO.getBrandId() == null || repairOrderDTO.getModelId() == null
      || repairOrderDTO.getYearId() == null || repairOrderDTO.getEngineId() == null)
      && (StringUtils.isNotBlank(repairOrderDTO.getBrand()))) {

      VehicleDTO vehicleDTO = baseProductService.addVehicleToDB(repairOrderDTO.getBrand(), repairOrderDTO.getModel(),
        repairOrderDTO.getYear(), repairOrderDTO.getEngine());
      vehicleDTOs.add(vehicleDTO);
      repairOrderDTO.setBrandId(vehicleDTO.getVirtualBrandId());
      repairOrderDTO.setModelId(vehicleDTO.getVirtualModelId());
      repairOrderDTO.setYearId(vehicleDTO.getVirtualYearId());
      repairOrderDTO.setEngineId(vehicleDTO.getVirtualEngineId());
      repairOrderDTO.setAddVehicleInfoToSolr(true);
    }
    return vehicleDTOs;
  }

  /**
   * 入库单 所填车辆若为新车型，则新增此车辆，并将ID保存到此入库单
   *
   * @param purchaseInventoryDTO
   * @return
   * @throws Exception
   * @author wjl
   */
  @Override
  public PurchaseInventoryDTO populatePurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    PurchaseInventoryItemDTO[] purchaseInventoryItemDTOs = purchaseInventoryDTO.getItemDTOs();
    if (purchaseInventoryItemDTOs != null) {
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryItemDTOs) {

        Long[] vehicleIds = ServiceManager.getService(IBaseProductService.class).
          saveVehicle(purchaseInventoryItemDTO.getVehicleBrandId(), purchaseInventoryItemDTO.getVehicleModelId(),
            purchaseInventoryItemDTO.getVehicleYearId(), purchaseInventoryItemDTO.getVehicleEngineId(),
            purchaseInventoryItemDTO.getVehicleBrand(), purchaseInventoryItemDTO.getVehicleModel(),
            purchaseInventoryItemDTO.getVehicleYear(), purchaseInventoryItemDTO.getVehicleEngine());

        purchaseInventoryItemDTO.setVehicleBrandId(vehicleIds[0]);
        purchaseInventoryItemDTO.setVehicleModelId(vehicleIds[1]);
        purchaseInventoryItemDTO.setVehicleYearId(vehicleIds[2]);
        purchaseInventoryItemDTO.setVehicleEngineId(vehicleIds[3]);
      }
    }
    return purchaseInventoryDTO;
  }


  @Override
  public List<ItemIndexDTO> getItemIndexDTOs(ItemIndexDTO itemIndexDTO) throws Exception {
    String pageNoStr = itemIndexDTO.getPageNo();
    Integer startNo = pageNoStr != null && !"".equals(pageNoStr) ? Integer.parseInt(pageNoStr) : 1;
    List<ItemIndexDTO> itemIndexDTOs = this.doItemIndexDTOs(itemIndexDTO, (startNo - 1) * 5);
    if ((itemIndexDTOs == null || itemIndexDTOs.size() <= 0) && startNo != 1) {
      itemIndexDTOs = this.doItemIndexDTOs(itemIndexDTO, (startNo - 2) * 5);
      itemIndexDTO.setPageNo((startNo - 1) + "");
      return itemIndexDTOs;
    }
    if (itemIndexDTOs != null && itemIndexDTOs.size() > 0) {
      for (int i = 0; i < itemIndexDTOs.size(); i++) {
        itemIndexDTOs.get(i).setIndexNo(i);
        List<ItemIndexDTO> itemIndexDTOList = this.doChildItemIndexDTO(itemIndexDTOs.get(i), OrderTypes.INVENTORY);
        if (itemIndexDTOList != null && itemIndexDTOList.size() > 0) {
          InventorySearchIndex inventorySearchIndex = this.doInventoryAmount(itemIndexDTOs.get(i));
          Double amount = 0d;
          if (inventorySearchIndex != null) {
            amount = inventorySearchIndex.getAmount();
            itemIndexDTOs.get(i).setProductId(inventorySearchIndex.getProductId());
          }
          itemIndexDTOs.get(i).setItemPrice((itemIndexDTOList.get(0).getItemPrice() == null ? 0 : itemIndexDTOList.get(0).getItemPrice()));
          itemIndexDTOs.get(i).setItemCount(0d);
          itemIndexDTOs.get(i).setOrderTimeCreated(itemIndexDTOList.get(0).getOrderTimeCreated());
          String createDate = DateUtil.convertDateLongToDateString("yyyy-MM-dd", itemIndexDTOList.get(0).getOrderTimeCreated());
          itemIndexDTOs.get(i).setOrderTimeCreatedStr(createDate);
          Double count = 0d;
          for (ItemIndexDTO indexDTO : itemIndexDTOList) {
            String date = DateUtil.convertDateLongToDateString("yyyy-MM-dd", indexDTO.getOrderTimeCreated());
            indexDTO.setOrderTimeCreatedStr(date);
            count += indexDTO.getItemCount();
          }
          List<ItemIndexDTO> returnIndexDTOs = this.doChildItemIndexDTO(itemIndexDTOs.get(i), OrderTypes.RETURN);
          if (returnIndexDTOs != null && returnIndexDTOs.size() > 0) {
            for (ItemIndexDTO returnIndexDTO : returnIndexDTOs) {
              count -= returnIndexDTO.getItemCount();
            }
          }
          if (amount >= count) {
            itemIndexDTOs.get(i).setReturnAbleCount(count);
          } else {
            itemIndexDTOs.get(i).setReturnAbleCount(amount);
          }
        } else {
          itemIndexDTOs.get(i).setReturnAbleCount(0d);
          itemIndexDTOs.get(i).setItemCount(0d);
        }
        itemIndexDTOs.get(i).setIndexNo(i);
      }
    }
    return itemIndexDTOs;
  }

  public List<ItemIndexDTO> doItemIndexDTOs(ItemIndexDTO itemIndexDTO, Integer startNo) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<ItemIndex> list = searchService.searchReturnTotal(itemIndexDTO, startNo);
    List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
    for (ItemIndex itemIndex : list) {
      ItemIndexDTO indexDTO = itemIndex.toDTO();
      itemIndexDTOs.add(indexDTO);
    }
    return itemIndexDTOs;
  }

  public List<ItemIndexDTO> doChildItemIndexDTO(ItemIndexDTO itemIndexDTO, OrderTypes type) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    InventorySearchIndex inventorySearchIndex = searchService.searchInventorySearchIndexAmount(itemIndexDTO.getShopId(), itemIndexDTO.getItemName(),
      itemIndexDTO.getItemBrand(), itemIndexDTO.getItemSpec(), itemIndexDTO.getItemModel(), itemIndexDTO.getVehicleBrand(),
      itemIndexDTO.getVehicleModel(), itemIndexDTO.getVehicleYear(), itemIndexDTO.getVehicleEngine(), itemIndexDTO.getCommodityCode());
    if (inventorySearchIndex == null || inventorySearchIndex.getParentProductId() == null || inventorySearchIndex.getParentProductId() == 0) {
      return null;
    }
    ProductDTO productDTO = productService.getProductByProductLocalInfoId(inventorySearchIndex.getProductId(), itemIndexDTO.getShopId());
    List<ItemIndex> itemIndexes = searchService.searchReturnAbleProducts(itemIndexDTO, type);
    List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
    if (itemIndexes != null && itemIndexes.size() > 0) {
      for (ItemIndex itemIndex : itemIndexes) {
        ItemIndexDTO indexDTO = itemIndex.toDTO();
        if (UnitUtil.isStorageUnit(itemIndex.getUnit(), productDTO)) {
          indexDTO.setItemCount(indexDTO.getItemCount() * productDTO.getRate());
        }
        itemIndexDTOs.add(indexDTO);
      }
    }
    return itemIndexDTOs;
  }

  public InventorySearchIndex doInventoryAmount(ItemIndexDTO itemIndexDTO) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    return searchService.searchInventorySearchIndexAmount(itemIndexDTO.getShopId(), itemIndexDTO.getItemName(),
      itemIndexDTO.getItemBrand(), itemIndexDTO.getItemSpec(), itemIndexDTO.getItemModel(), itemIndexDTO.getVehicleBrand(),
      itemIndexDTO.getVehicleModel(), itemIndexDTO.getVehicleYear(), itemIndexDTO.getVehicleEngine(), itemIndexDTO.getCommodityCode());
  }

  @Override
  public void populateVehicleAppointment(RepairOrderDTO repairOrderDTO) throws BcgogoException {
    CustomerVehicle customerVehicle = ServiceManager.getService(ICustomerService.class)
      .getVehicleAppointment(repairOrderDTO.getCustomerId(), repairOrderDTO.getVechicleId());

    if (customerVehicle != null) {
      String maintainTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", customerVehicle.getMaintainTime());
      String insureTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", customerVehicle.getInsureTime());
      String examineTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", customerVehicle.getExamineTime());
      repairOrderDTO.setMaintainTimeStr(maintainTimeStr);
      repairOrderDTO.setInsureTimeStr(insureTimeStr);
      repairOrderDTO.setExamineTimeStr(examineTimeStr);
      repairOrderDTO.setMaintainMileage(customerVehicle.getMaintainMileage());
    }
  }

  @Override
  public PurchaseReturnDTO createPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO, List<ItemIndexDTO> indexDTOs) throws Exception {
    productDaoManager = ServiceManager.getService(ProductDaoManager.class);
    ProductWriter writer = productDaoManager.getWriter();
    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString("yyyy-MM-dd", curTime);
    purchaseReturnDTO.setEditDate(curTime);
    purchaseReturnDTO.setEditDateStr(time);
    if (indexDTOs != null && indexDTOs.size() > 0) {
      purchaseReturnDTO = this.doSupplierFill(purchaseReturnDTO, indexDTOs.get(0).getCustomerOrSupplierName());
      PurchaseReturnItemDTO[] purchaseReturnItemDTOs = new PurchaseReturnItemDTO[indexDTOs.size()];
      for (int i = 0; i < indexDTOs.size(); i++) {
        purchaseReturnItemDTOs[i] = new PurchaseReturnItemDTO();
        purchaseReturnItemDTOs[i].setAmount(indexDTOs.get(i).getItemCount());
        purchaseReturnItemDTOs[i].setMemo(indexDTOs.get(i).getItemMemo());
        purchaseReturnItemDTOs[i].setPrice(indexDTOs.get(i).getItemPrice() == null ? 0 : indexDTOs.get(i).getItemPrice());
        purchaseReturnItemDTOs[i].setProductId(indexDTOs.get(i).getProductId());
        purchaseReturnItemDTOs[i].setTotal(indexDTOs.get(i).getItemCount() * (indexDTOs.get(i).getItemPrice() == null ? 0 : indexDTOs.get(i).getItemPrice()));
        purchaseReturnItemDTOs[i].setProductName(indexDTOs.get(i).getItemName());
        purchaseReturnItemDTOs[i].setBrand(indexDTOs.get(i).getItemBrand());
        purchaseReturnItemDTOs[i].setSpec(indexDTOs.get(i).getItemSpec());
        purchaseReturnItemDTOs[i].setModel(indexDTOs.get(i).getItemModel());
        purchaseReturnItemDTOs[i].setVehicleBrand(indexDTOs.get(i).getVehicleBrand());
        purchaseReturnItemDTOs[i].setVehicleEngine(indexDTOs.get(i).getVehicleEngine());
        purchaseReturnItemDTOs[i].setVehicleModel(indexDTOs.get(i).getVehicleModel());
        purchaseReturnItemDTOs[i].setVehicleYear(indexDTOs.get(i).getVehicleYear());
        purchaseReturnItemDTOs[i].setReturnAbleAmount(indexDTOs.get(i).getReturnAbleCount());
        ProductLocalInfo productLocalInfo = writer.getById(ProductLocalInfo.class, indexDTOs.get(i).getProductId());
        Product product = writer.getById(Product.class, productLocalInfo.getProductId());
        purchaseReturnItemDTOs[i].setProductVehicleStatus(product.getProductVehicleStatus());
        purchaseReturnItemDTOs[i].setUnit(productLocalInfo.getSellUnit());
        purchaseReturnItemDTOs[i].setStorageUnit(productLocalInfo.getStorageUnit());
        purchaseReturnItemDTOs[i].setSellUnit(productLocalInfo.getSellUnit());
        purchaseReturnItemDTOs[i].setRate(productLocalInfo.getRate());
      }
      purchaseReturnDTO.setItemDTOs(purchaseReturnItemDTOs);
    }
    return purchaseReturnDTO;
  }

  public PurchaseReturnDTO doSupplierFill(PurchaseReturnDTO purchaseReturnDTO, String supplierName) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SupplierDTO> supplierDTOList = userService.getSupplierByNameAndShopId(purchaseReturnDTO.getShopId(), supplierName);
    if (supplierDTOList != null && !supplierDTOList.isEmpty()) {
      SupplierDTO supplierDTO = supplierDTOList.get(0);
      purchaseReturnDTO.setSupplierId(supplierDTO.getId());
      purchaseReturnDTO.setSupplier(supplierDTO.getName());
      purchaseReturnDTO.setContact(supplierDTO.getContact());
      purchaseReturnDTO.setMobile(supplierDTO.getMobile());
      purchaseReturnDTO.setAddress(supplierDTO.getAddress());
      purchaseReturnDTO.setAccount(supplierDTO.getAccount());
      purchaseReturnDTO.setBank(supplierDTO.getBank());
      purchaseReturnDTO.setAccountName(supplierDTO.getAccountName());
      purchaseReturnDTO.setCategory(supplierDTO.getCategory());
      purchaseReturnDTO.setAbbr(supplierDTO.getAbbr());
      purchaseReturnDTO.setSettlementType(supplierDTO.getSettlementTypeId());
      purchaseReturnDTO.setLandline(supplierDTO.getLandLine());
      purchaseReturnDTO.setFax(supplierDTO.getFax());
      purchaseReturnDTO.setQq(supplierDTO.getQq());
      purchaseReturnDTO.setEmail(supplierDTO.getEmail());
      purchaseReturnDTO.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
      purchaseReturnDTO.setBusinessScope(supplierDTO.getBusinessScope());
    }
    return purchaseReturnDTO;
  }

  @Override
  public PurchaseReturnDTO savePurchaseReturn(Long shopId,Long shopVersionId,PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    //退货供应商
    SupplierDTO supplierDTO = saveOrUpdateSupplierByPurchaseReturn(shopId, purchaseReturnDTO);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,purchaseReturnDTO.getStorehouseId());
        purchaseReturnDTO.setStorehouseName(storeHouseDTO==null?null:storeHouseDTO.getName());
      }
      PurchaseReturn purchaseReturn = new PurchaseReturn();
      purchaseReturnDTO.setSupplierShopId(null);
      purchaseReturn.fromDTO(purchaseReturnDTO);
      purchaseReturn.setStatus(OrderStatus.SETTLED);
//      if(purchaseReturnDTO.getSupplierShopId()!=null){
//        purchaseReturn.setStatus(OrderStatus.SELLER_PENDING);
//      }else{
//        purchaseReturn.setStatus(OrderStatus.SETTLED);
//      }
      //保存退货单
      writer.save(purchaseReturn);
      purchaseReturnDTO.setId(purchaseReturn.getId());
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        if (purchaseReturnItemDTO.getProductId() == null) {
          continue;
        }
        //更新商品编码
        getProductService().updateCommodityCodeByProductLocalInfoId(shopId,purchaseReturnItemDTO.getProductId(),purchaseReturnItemDTO.getCommodityCode());
        //初始化退货明细
        //处理价格double长度 四舍五入
        purchaseReturnItemDTO.setTotal(NumberUtil.round(purchaseReturnItemDTO.getTotal(), NumberUtil.MONEY_PRECISION));
        purchaseReturnItemDTO.setReturnAbleAmount(NumberUtil.round(purchaseReturnItemDTO.getReturnAbleAmount(), NumberUtil.MONEY_PRECISION));

        PurchaseReturnItem purchaseReturnItem = this.generatePurchaseReturnItem(purchaseReturnItemDTO, purchaseReturn.getId());
        writer.save(purchaseReturnItem);
        purchaseReturnItemDTO.setId(purchaseReturnItem.getId());
        //处理库存信息
        processProductInventoryInfo(purchaseReturnDTO,shopVersionId, writer, purchaseReturnItemDTO,inventorySearchIndexList);
      }

      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseReturn.getShopId(),purchaseReturnDTO.getUserId(),purchaseReturn.getId(),ObjectTypes.PURCHASE_RETURN_ORDER,OperationTypes.CREATE));
      orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseReturn.getShopId(),purchaseReturnDTO.getUserId(),purchaseReturn.getStatus(),null,purchaseReturn.getId(),OrderTypes.RETURN));

      //add by WLF 更新缓存中待办入库退货单的数量
      List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseReturnDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_RETURN_ORDER, purchaseReturnDTO.getShopId(), supplierIdList);

//      if (purchaseReturnDTO.getSupplierShopId() != null){
//        createSalesReturnDTOByPurchaseReturnOrderDTO(writer,purchaseReturnDTO);
//      }

      ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(purchaseReturnDTO,OrderTypes.RETURN,purchaseReturnDTO.getStatus(),writer,null);


      writer.commit(status);
      purchaseReturnDTO.setStatus(purchaseReturn.getStatus());
      //草稿单作废
      if (StringUtils.isNotBlank(purchaseReturnDTO.getDraftOrderIdStr())) {
        ServiceManager.getService(IDraftOrderService.class).deleteDraftOrder(purchaseReturnDTO.getShopId(), NumberUtil.longValue(purchaseReturnDTO.getDraftOrderIdStr()));
      }
      getInventoryService().addOrUpdateInventorySearchIndexWithList(purchaseReturnDTO.getShopId(), inventorySearchIndexList);
      supplierDTO.setLastOrderType(OrderTypes.RETURN);
      supplierDTO.setLastOrderTime(System.currentTimeMillis());
      supplierDTO.setLastOrderId(purchaseReturnDTO.getId());
      ServiceManager.getService(IUserService.class).updateSupplier(supplierDTO);
      return purchaseReturnDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PurchaseReturnDTO saveOnlinePurchaseReturn(Long shopId, Long shopVersionId, PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    IProductService productService=ServiceManager.getService(IProductService.class);
    //退货供应商
    SupplierDTO supplierDTO = updateSupplierByOnlinePurchaseReturn(shopId, purchaseReturnDTO);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, purchaseReturnDTO.getStorehouseId());
        purchaseReturnDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
      }
      PurchaseReturn purchaseReturn = new PurchaseReturn();
      purchaseReturn.fromDTO(purchaseReturnDTO);
      purchaseReturn.setStatus(OrderStatus.SELLER_PENDING);
      //保存退货单
      writer.save(purchaseReturn);
      purchaseReturnDTO.setId(purchaseReturn.getId());
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      List<Long> productIdList=new ArrayList<Long>();
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        if (purchaseReturnItemDTO.getProductId() == null) {
          continue;
        }
        //初始化退货明细
        //处理价格double长度 四舍五入
        purchaseReturnItemDTO.setTotal(NumberUtil.round(purchaseReturnItemDTO.getTotal(), NumberUtil.MONEY_PRECISION));
        purchaseReturnItemDTO.setReturnAbleAmount(NumberUtil.round(purchaseReturnItemDTO.getReturnAbleAmount(), NumberUtil.MONEY_PRECISION));

        PurchaseReturnItem purchaseReturnItem = this.generatePurchaseReturnItem(purchaseReturnItemDTO, purchaseReturn.getId());
        writer.save(purchaseReturnItem);
        purchaseReturnItemDTO.setId(purchaseReturnItem.getId());
        //处理库存信息
        processProductInventoryInfo(purchaseReturnDTO, shopVersionId, writer, purchaseReturnItemDTO, inventorySearchIndexList);
        productIdList.add(purchaseReturnItemDTO.getProductId());
      }
      //item的和促销record的关联
      Map<Long,PromotionOrderRecordDTO> recordMap=promotionsService.getPromotionOrderRecordDTOMap(purchaseReturn.getPurchaseOrderId());
      if(recordMap!=null&&!recordMap.keySet().isEmpty()){
       Map<Long,ProductMappingDTO> productMappingDTOMap=productService.getCustomerProductMappings(shopId,ArrayUtil.toLongArr(productIdList));
        for (PurchaseReturnItemDTO itemDTO : purchaseReturnDTO.getItemDTOs()) {
        ProductMappingDTO mappingDTO= productMappingDTOMap.get(itemDTO.getProductId());
          if(mappingDTO==null){
            continue;
          }
          PromotionOrderRecordDTO recordDTO=recordMap.get(mappingDTO.getSupplierProductId());
          if(recordDTO==null){
            continue;
          }
          OrderItemPromotion itemPromotion=new OrderItemPromotion();
          itemPromotion.setOrderItemId(itemDTO.getId());
          itemPromotion.setOrderTypes(OrderTypes.RETURN);
          itemPromotion.setPromotionOrderRecordId(recordDTO.getId());
          writer.save(itemPromotion);
        }
      }

      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseReturn.getShopId(), purchaseReturnDTO.getUserId(), purchaseReturn.getId(), ObjectTypes.PURCHASE_RETURN_ORDER, OperationTypes.CREATE));
      orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseReturn.getShopId(), purchaseReturnDTO.getUserId(), purchaseReturn.getStatus(), null, purchaseReturn.getId(), OrderTypes.RETURN));

      //add by WLF 更新缓存中待办入库退货单的数量
      List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseReturnDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_RETURN_ORDER, purchaseReturnDTO.getShopId(), supplierIdList);
      //创建对方的销售退货单
      createSalesReturnDTOByPurchaseReturnOrderDTO(writer, purchaseReturnDTO);
      if(BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopVersionId)){
        purchaseReturnDTO.setSelectSupplier(true);
      }
      getProductOutStorageService().productThroughByOrder(purchaseReturnDTO,OrderTypes.RETURN,purchaseReturn.getStatus(),writer,null);
      writer.commit(status);
      purchaseReturnDTO.setStatus(purchaseReturn.getStatus());

      getInventoryService().addOrUpdateInventorySearchIndexWithList(purchaseReturnDTO.getShopId(), inventorySearchIndexList);
      supplierDTO.setLastOrderType(OrderTypes.RETURN);
      supplierDTO.setLastOrderTime(System.currentTimeMillis());
      supplierDTO.setLastOrderId(purchaseReturnDTO.getId());
      ServiceManager.getService(IUserService.class).updateSupplier(supplierDTO);
      return purchaseReturnDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PurchaseReturnDTO updatePurchaseReturn(Long shopId,Long shopVersionId, PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    updateSupplierByOnlinePurchaseReturn(shopId, purchaseReturnDTO);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,purchaseReturnDTO.getStorehouseId());
        purchaseReturnDTO.setStorehouseName(storeHouseDTO==null?null:storeHouseDTO.getName());
      }
      PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class,purchaseReturnDTO.getId());
      Long oldStorehouseId = purchaseReturn.getStorehouseId();
      purchaseReturn.fromDTO(purchaseReturnDTO);
      if(purchaseReturnDTO.getSupplierShopId()!=null){
        purchaseReturn.setStatus(OrderStatus.SELLER_PENDING);
      }else{
        purchaseReturn.setStatus(OrderStatus.SETTLED);
      }

      //保存退货单
      writer.saveOrUpdate(purchaseReturn);
      purchaseReturnDTO.setId(purchaseReturn.getId());
      //先删item  和  归还库存
      List<PurchaseReturnItem> items = writer.getPurchaseReturnItemsByReturnId(purchaseReturn.getId());
      PurchaseReturnDTO purchaseReturnDTOClone=purchaseReturnDTO.clone();
      PurchaseReturnDTO originPurchaseReturnDTO=purchaseReturnDTO.clone();
      List<PurchaseReturnItemDTO> originItemDTOs=new ArrayList<PurchaseReturnItemDTO>();
      ISearchService searchService = ServiceManager.getService(ISearchService.class);
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      for (PurchaseReturnItem item : items) {
        if(item==null) continue;
        originItemDTOs.add(item.toDTO());
        //本地商品库
        ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(item.getProductId(), purchaseReturnDTO.getShopId());
        ProductDTO productDTO = getProductService().getProductById(productLocalInfoDTO.getProductId(), purchaseReturnDTO.getShopId());
        Inventory inventory = writer.getById(Inventory.class, item.getProductId());      // 参数 purchaseReturnItemDTO.getProductId()是本地库productId
        getInventoryService().caculateBeforeLimit(inventory.toDTO(),purchaseReturnDTO.getInventoryLimitDTO());
        double purchaseItemAmount = item.getAmount();
        if (UnitUtil.isStorageUnit(item.getUnit(), productDTO)) {
          purchaseItemAmount = purchaseItemAmount * productDTO.getRate();
        }
        //现库存量=原库存量+退货量
        if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
          //现库存量=原库存量+退货量
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,new StoreHouseInventoryDTO(oldStorehouseId,item.getProductId(),null,purchaseItemAmount));
        }
        inventory.setAmount(inventory.getAmount() + purchaseItemAmount);
        //更新库存
        writer.update(inventory);
        getInventoryService().caculateAfterLimit(inventory.toDTO(),purchaseReturnDTO.getInventoryLimitDTO());

        writer.delete(item);
        //根据productId找到库存 SearchIndex
        InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(item.getProductId());
        if (inventorySearchIndex != null) {
          inventorySearchIndex.setEditDate(purchaseReturnDTO.getEditDate());
          //设置库存量
          inventorySearchIndex.setAmount(inventory.getAmount());
          inventorySearchIndexList.add(inventorySearchIndex);
        }
      }
      if(CollectionUtil.isNotEmpty(originItemDTOs)){
        originPurchaseReturnDTO.setItemDTOs(originItemDTOs.toArray(new PurchaseReturnItemDTO[originItemDTOs.size()]));
      }
      Set<Long> productIds = new HashSet<Long>();
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        if (purchaseReturnItemDTO.getProductId() == null) {
          continue;
        }
        productIds.add(purchaseReturnItemDTO.getProductId());
      }
      Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId,productIds);
      //保存最新的
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        if (purchaseReturnItemDTO.getProductId() == null) {
          continue;
        }
        //添加其他地方修改单位后单位
        ProductDTO productDTO = productDTOMap.get(purchaseReturnItemDTO.getProductId());
        if(StringUtil.isEmpty(purchaseReturnItemDTO.getUnit())
          && productDTO!= null && StringUtil.isNotEmpty(productDTO.getSellUnit())){
          purchaseReturnItemDTO.setUnit(productDTO.getSellUnit());
        }
        //更新商品编码
        getProductService().updateCommodityCodeByProductLocalInfoId(shopId,purchaseReturnItemDTO.getProductId(),purchaseReturnItemDTO.getCommodityCode());
        //初始化退货明细
        //处理价格double长度 四舍五入
        purchaseReturnItemDTO.setTotal(NumberUtil.round(purchaseReturnItemDTO.getTotal(), NumberUtil.MONEY_PRECISION));
        purchaseReturnItemDTO.setReturnAbleAmount(NumberUtil.round(purchaseReturnItemDTO.getReturnAbleAmount(), NumberUtil.MONEY_PRECISION));

        PurchaseReturnItem purchaseReturnItem = this.generatePurchaseReturnItem(purchaseReturnItemDTO, purchaseReturn.getId());
        writer.save(purchaseReturnItem);
        purchaseReturnItemDTO.setId(purchaseReturnItem.getId());
        //处理库存信息
        processProductInventoryInfo(purchaseReturnDTO,shopVersionId, writer, purchaseReturnItemDTO,inventorySearchIndexList);
      }
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getUserId(), purchaseReturnDTO.getId(), ObjectTypes.PURCHASE_RETURN_ORDER, OperationTypes.UPDATE));

      //add by WLF 更新缓存中待办入库退货单的数量
      List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseReturnDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_RETURN_ORDER, purchaseReturnDTO.getShopId(), supplierIdList);

      if (purchaseReturnDTO.getSupplierShopId() != null){
        updateSalesReturnDTOByPurchaseReturnOrderDTO(writer, purchaseReturnDTO);
      }
      getProductThroughService().productThroughByOrderForUpdateOnlineReturn(purchaseReturnDTOClone,originPurchaseReturnDTO,writer);
      writer.commit(status);
      purchaseReturnDTO.setStatus(purchaseReturn.getStatus());
      //草稿单作废
      ServiceManager.getService(IDraftOrderService.class).deleteDraftOrderByTxnOrderId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());
      getInventoryService().addOrUpdateInventorySearchIndexWithList(purchaseReturnDTO.getShopId(),inventorySearchIndexList);
      return purchaseReturnDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ExpressDTO getExpressDTOById(Long expressId) {
    if (expressId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Express express = writer.getById(Express.class, expressId);
    return express == null ? null : express.toDTO();
  }

  @Override
  public void updateService(Service service) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      writer.update(service);
      writer.commit(status);
      //做solr
      Set<Long> serviceIdSet = new HashSet<Long>();
      serviceIdSet.add(service.getId());
      ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(service.getShopId(),serviceIdSet);
    }finally{
      writer.rollback(status);
    }
  }

  private SupplierDTO saveOrUpdateSupplierByPurchaseReturn(Long shopId, PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    SupplierDTO supplierDTO = new SupplierDTO();
    if (purchaseReturnDTO.getSupplierId() != null) {
      supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
    }
    if ((purchaseReturnDTO.getSupplierId() == null && StringUtils.isNotBlank(purchaseReturnDTO.getSupplier())) ||
      (supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus()))) {
      supplierDTO = getSupplierService().getSupplierDTOByPreciseName(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplier());
    }
    if (supplierDTO == null) {
      supplierDTO = new SupplierDTO();
    }
    supplierDTO.set(purchaseReturnDTO);
    if (supplierDTO.getId() == null || supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) {
      getUserService().createSupplier(supplierDTO);
      supplierDTO = getUserService().getSupplierById(supplierDTO.getId());
      purchaseReturnDTO.setContactId(supplierDTO.getContactId());
      ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
    }else {
      if (supplierDTO.getCustomerId() == null && supplierDTO.isAddContacts()) {
        getUserService().updateSupplier(supplierDTO);
        if (supplierDTO.getCustomerId() == null && supplierDTO.isAddContacts()) {
          getUserService().updateSupplier(supplierDTO);
          if (!ArrayUtils.isEmpty(supplierDTO.getContacts())
              && supplierDTO.getContacts()[0] != null
              && supplierDTO.getContacts()[0].getId() != null) {
            purchaseReturnDTO.setContactId(supplierDTO.getContacts()[0].getId());
          }
        }
      }
    }
    purchaseReturnDTO.setSupplierId(supplierDTO.getId());
    if (supplierDTO.getCustomerId() != null) {
      //同时更新客户的信息
      CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(supplierDTO.getCustomerId());
      customerDTO.fromSupplierDTO(supplierDTO);
      ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
      if(supplierDTO.isAddContacts()) {
        // 既是客户又是供应商的联系人新增
        ContactDTO[] contactDTOs = ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(),
            supplierDTO.getId(), purchaseReturnDTO.getShopId(), supplierDTO.getContacts());
        supplierDTO.setContacts(contactDTOs);
        if (!ArrayUtils.isEmpty(supplierDTO.getContacts())
            && supplierDTO.getContacts()[0] != null
            && supplierDTO.getContacts()[0].getId() != null) {
          purchaseReturnDTO.setContactId(supplierDTO.getContacts()[0].getId());
        }
      }
      CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId()).get(0);
      customerRecordDTO.fromCustomerDTO(customerDTO);
      ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
    }
    return supplierDTO;
  }

  private SupplierDTO updateSupplierByOnlinePurchaseReturn(Long shopId, PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    SupplierDTO supplierDTO = new SupplierDTO();
    if (purchaseReturnDTO.getSupplierId() != null) {
      supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
    }
    if ((purchaseReturnDTO.getSupplierId() == null && StringUtils.isNotBlank(purchaseReturnDTO.getSupplier())) ||
      (supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus()))) {
      supplierDTO = getSupplierService().getSupplierDTOByPreciseName(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplier());
    }
    if (supplierDTO == null) {
      supplierDTO = new SupplierDTO();
    }
    supplierDTO.setByOnlinePurchaseReturn(purchaseReturnDTO);
    if (supplierDTO.getId() == null || supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) {
      getUserService().createSupplier(supplierDTO);
      ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
    }else if(supplierDTO.getId() != null){
      getUserService().updateSupplier(supplierDTO);
    }
    purchaseReturnDTO.setSupplierId(supplierDTO.getId());
    return supplierDTO;
  }


  private void createSalesReturnDTOByPurchaseReturnOrderDTO(TxnWriter writer,PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    if (purchaseReturnDTO.getShopId() == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    if (purchaseReturnDTO.getSupplierShopId() == null)
      throw new BcgogoException(BcgogoExceptionType.WholeSalerShopIdNotFound);
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = userService.getCustomerByCustomerShopIdAndShopId(purchaseReturnDTO.getSupplierShopId(), purchaseReturnDTO.getShopId());
    if (customerDTO == null) throw new BcgogoException(BcgogoExceptionType.CustomerNotFound);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    SalesReturnDTO salesReturnDTO = new SalesReturnDTO();
    salesReturnDTO.setShopId(purchaseReturnDTO.getSupplierShopId());
    salesReturnDTO.setCustomerDTO(customerDTO);
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
      Map<Long, ProductMappingDTO> productMappingDTOMap = productService.getCustomerProductMappingDTODetailMap(purchaseReturnDTO.getShopId(), salesReturnDTO.getShopId(), customerProductIdSet.toArray(new Long[customerProductIdSet.size()]));
      ProductMappingDTO productMappingDTO = null;
      Set<Long> supplierProductIdSet = new HashSet<Long>();
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        productMappingDTO = productMappingDTOMap.get(purchaseReturnItemDTO.getProductId());
        if(productMappingDTO!=null && productMappingDTO.getSupplierProductDTO()!=null){
          supplierProductIdSet.add(productMappingDTO.getSupplierProductDTO().getProductLocalInfoId());
        }
      }
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class).getOrSaveProductHistoryByLocalInfoId(salesReturnDTO.getShopId(),supplierProductIdSet.toArray(new Long[supplierProductIdSet.size()]));
      SalesReturnItemDTO salesReturnItemDTO = null;
      List<SalesReturnItemDTO> salesReturnItemDTOs=new ArrayList<SalesReturnItemDTO>();
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        productMappingDTO = productMappingDTOMap.get(purchaseReturnItemDTO.getProductId());
        if (productMappingDTO != null && productMappingDTO.getSupplierProductDTO()!=null) {
          salesReturnItemDTO = new SalesReturnItemDTO();
          salesReturnItemDTO.setProductDTOWithOutUnit(productMappingDTO.getSupplierProductDTO());
          salesReturnItemDTO.setCustomerOrderItemId(purchaseReturnItemDTO.getId());
          salesReturnItemDTO.setAmount(purchaseReturnItemDTO.getAmount());
          salesReturnItemDTO.setMemo(purchaseReturnItemDTO.getMemo());
          salesReturnItemDTO.setPrice(purchaseReturnItemDTO.getPrice());
          salesReturnItemDTO.setSalesReturnId(salesReturn.getId());
          salesReturnItemDTO.setTotal(purchaseReturnItemDTO.getTotal());
          salesReturnItemDTO.setUnit(purchaseReturnItemDTO.getUnit());
          ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(salesReturnItemDTO.getProductId());
          salesReturnItemDTO.setProductHistoryId(productHistoryDTO==null?null:productHistoryDTO.getId());
          SalesReturnItem salesReturnItem=new SalesReturnItem();
          salesReturnItem.fromDTO(salesReturnItemDTO);
          writer.save(salesReturnItem);
          salesReturnItemDTO.setId(salesReturnItem.getId());
          salesReturnItemDTOs.add(salesReturnItemDTO);
        }else{
          throw new Exception("退货单中有不属于对应批发商的产品！");
        }
      }
      //item的和促销record的关联
      Map<Long,PromotionOrderRecordDTO> recordMap=ServiceManager.getService(IPromotionsService.class).getPromotionOrderRecordDTOMap(purchaseReturnDTO.getPurchaseOrderId());
      if(recordMap!=null&&!recordMap.keySet().isEmpty()){
        for (SalesReturnItemDTO itemDTO : salesReturnItemDTOs) {
          PromotionOrderRecordDTO recordDTO=recordMap.get(itemDTO.getProductId());
          if(recordDTO==null){
            continue;
          }
          OrderItemPromotion itemPromotion=new OrderItemPromotion();
          itemPromotion.setOrderItemId(itemDTO.getId());
          itemPromotion.setOrderTypes(OrderTypes.SALE_RETURN);
          itemPromotion.setPromotionOrderRecordId(recordDTO.getId());
          writer.save(itemPromotion);
        }
      }
    }


    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
    getTxnService().saveOperationLogTxnService(new OperationLogDTO(salesReturn.getShopId(),purchaseReturnDTO.getUserId(),salesReturn.getId(),ObjectTypes.SALE_RETURN_ORDER,OperationTypes.CREATE));

    //add by WLF 更新缓存中待办销售退货单和入库退货单的数量
    List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesReturnDTO.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_RETURN_ORDER, salesReturnDTO.getShopId(), customerIdList);
    List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseReturnDTO.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_RETURN_ORDER, purchaseReturnDTO.getShopId(), supplierIdList);

    orderStatusChangeLogService.saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesReturn.getShopId(),purchaseReturnDTO.getUserId(),salesReturn.getStatus(),null,salesReturn.getId(),OrderTypes.SALE_RETURN));
  }

  private void updateSalesReturnDTOByPurchaseReturnOrderDTO(TxnWriter writer,PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    if (purchaseReturnDTO.getShopId() == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    if (purchaseReturnDTO.getSupplierShopId() == null)
      throw new BcgogoException(BcgogoExceptionType.WholeSalerShopIdNotFound);
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = userService.getCustomerByCustomerShopIdAndShopId(purchaseReturnDTO.getSupplierShopId(), purchaseReturnDTO.getShopId());
    if (customerDTO == null) throw new BcgogoException(BcgogoExceptionType.CustomerNotFound);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
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
    List<SalesReturn> salesReturnList = writer.getSalesReturnDTOByPurchaseReturnOrderId(purchaseReturnDTO.getId());
    SalesReturn salesReturn =null;
    if(CollectionUtils.isNotEmpty(salesReturnList)){
      salesReturn = salesReturnList.get(0);
      salesReturnDTO.setReceiptNo(salesReturn.getReceiptNo());
      salesReturn.fromDTO(salesReturnDTO);
    }else{
      salesReturn = new SalesReturn();
      salesReturn.fromDTO(salesReturnDTO);
      salesReturn.setReceiptNo(txnService.getReceiptNo(salesReturnDTO.getShopId(), OrderTypes.SALE_RETURN, null));
    }
    writer.saveOrUpdate(salesReturn);
    salesReturnDTO.setId(salesReturn.getId());

    //先删item  和  处理库存
    List<SalesReturnItem> items = writer.getSalesReturnItemsBySalesReturnId(salesReturn.getId());
    for (SalesReturnItem item : items) {
      writer.delete(item);
    }
    if (!ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())) {
      Set<Long> customerProductIdSet = new HashSet<Long>();
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        customerProductIdSet.add(purchaseReturnItemDTO.getProductId());
      }
      Map<Long, ProductMappingDTO> productMappingDTOMap = productService.getCustomerProductMappingDTODetailMap(purchaseReturnDTO.getShopId(), salesReturnDTO.getShopId(), customerProductIdSet.toArray(new Long[customerProductIdSet.size()]));
      ProductMappingDTO productMappingDTO = null;
      SalesReturnItemDTO salesReturnItemDTO = null;
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        productMappingDTO = productMappingDTOMap.get(purchaseReturnItemDTO.getProductId());
        if (productMappingDTO != null) {
          salesReturnItemDTO = new SalesReturnItemDTO();
          salesReturnItemDTO.setProductDTOWithOutUnit(productMappingDTO.getSupplierProductDTO());
          salesReturnItemDTO.setCustomerOrderItemId(purchaseReturnItemDTO.getId());
          salesReturnItemDTO.setAmount(purchaseReturnItemDTO.getAmount());
          salesReturnItemDTO.setMemo(purchaseReturnItemDTO.getMemo());
          salesReturnItemDTO.setPrice(purchaseReturnItemDTO.getPrice());
          salesReturnItemDTO.setSalesReturnId(salesReturn.getId());
          salesReturnItemDTO.setTotal(purchaseReturnItemDTO.getTotal());
          if (StringUtils.isNotBlank(purchaseReturnItemDTO.getUnit())
            && (purchaseReturnItemDTO.getUnit().equals(salesReturnItemDTO.getStorageUnit())
            || purchaseReturnItemDTO.getUnit().equals(salesReturnItemDTO.getSellUnit()))) {
            salesReturnItemDTO.setUnit(purchaseReturnItemDTO.getUnit());
          } else {
            salesReturnItemDTO.setUnit(salesReturnItemDTO.getSellUnit());
          }
          writer.save(new SalesReturnItem().fromDTO(salesReturnItemDTO));
        }else{
          throw new Exception("退货单中有不属于对应批发商的产品！");
        }
      }
    }

    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    getTxnService().saveOperationLogTxnService(new OperationLogDTO(salesReturn.getShopId(),purchaseReturnDTO.getUserId(),salesReturn.getId(),ObjectTypes.SALE_RETURN_ORDER,OperationTypes.UPDATE));

    //add by WLF 更新缓存中待办销售退货单的数量
    List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesReturn.getShopId());
    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_RETURN_ORDER, salesReturn.getShopId(), customerIdList);
  }

  private void processProductInventoryInfo(PurchaseReturnDTO purchaseReturnDTO,Long shopVersionId, TxnWriter writer, PurchaseReturnItemDTO purchaseReturnItemDTO,List<InventorySearchIndex> inventorySearchIndexList) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    //本地商品库
    ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(purchaseReturnItemDTO.getProductId(), purchaseReturnDTO.getShopId());
    //标准商品库
    ProductDTO productDTO = getProductService().getProductById(productLocalInfoDTO.getProductId(), purchaseReturnDTO.getShopId());
    //库存
    Inventory inventory = writer.getById(Inventory.class, purchaseReturnItemDTO.getProductId());      // 参数 purchaseReturnItemDTO.getProductId()是本地库productId
    getInventoryService().caculateBeforeLimit(inventory.toDTO(),purchaseReturnDTO.getInventoryLimitDTO());
    double purchaseItemAmount = purchaseReturnItemDTO.getAmount();
    if (UnitUtil.isStorageUnit(purchaseReturnItemDTO.getUnit(), productDTO)) {
      purchaseItemAmount = purchaseItemAmount * productDTO.getRate();
    }
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
      //现库存量=原库存量-退货量
      StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(purchaseReturnDTO.getStorehouseId(),purchaseReturnItemDTO.getProductId());
      if ((storeHouseInventoryDTO==null?0d:storeHouseInventoryDTO.getAmount()) - purchaseItemAmount < 0) {
        purchaseReturnDTO.setId(null);
        purchaseReturnDTO.setStatus(null);
        if (UnitUtil.isStorageUnit(purchaseReturnItemDTO.getUnit(), productDTO)) {
          purchaseReturnItemDTO.setInventoryAmount(NumberUtil.round((storeHouseInventoryDTO==null?0d:storeHouseInventoryDTO.getAmount()) / productDTO.getRate(), 2));
        } else {
          purchaseReturnItemDTO.setInventoryAmount(storeHouseInventoryDTO==null?0d:storeHouseInventoryDTO.getAmount());
        }
        throw new Exception("产品\"" + productDTO.getName() + "\"库存量小于退货量，不能退货！");
      }
      storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,new StoreHouseInventoryDTO(purchaseReturnDTO.getStorehouseId(),purchaseReturnItemDTO.getProductId(),null,purchaseItemAmount*-1));
      inventory.setAmount(inventory.getAmount()-purchaseItemAmount);
    }else{
      //现库存量=原库存量-退货量
      if (inventory.getAmount() - purchaseItemAmount < 0) {
        purchaseReturnDTO.setId(null);
        purchaseReturnDTO.setStatus(null);
        if (UnitUtil.isStorageUnit(purchaseReturnItemDTO.getUnit(), productDTO)) {
          purchaseReturnItemDTO.setInventoryAmount(NumberUtil.round(inventory.getAmount() / productDTO.getRate(), 2));
        } else {
          purchaseReturnItemDTO.setInventoryAmount(inventory.getAmount());
        }
        throw new Exception("产品\"" + productDTO.getName() + "\"库存量小于退货量，不能退货！");
      }
      inventory.setAmount(inventory.getAmount() - purchaseItemAmount);
    }
    //更新库存
    writer.update(inventory);
    getInventoryService().caculateAfterLimit(inventory.toDTO(), purchaseReturnDTO.getInventoryLimitDTO());
    Double amount = purchaseReturnItemDTO.getAmount();
    purchaseReturnItemDTO.setReturnAbleAmount(purchaseReturnItemDTO.getReturnAbleAmount() - amount);
    purchaseReturnItemDTO.setInventoryAmount(purchaseReturnItemDTO.getInventoryAmount()  - amount);

    //根据productId找到库存 SearchIndex
    InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(purchaseReturnItemDTO.getProductId());
    if (inventorySearchIndex != null) {
      inventorySearchIndex.setEditDate(purchaseReturnDTO.getEditDate());
      //设置库存量
      inventorySearchIndex.setCommodityCode(purchaseReturnItemDTO.getCommodityCode());
      inventorySearchIndex.setAmount(inventory.getAmount());
      inventorySearchIndexList.add(inventorySearchIndex);
    }
  }

  @Override
  public PurchaseReturnDTO settlePurchaseReturn(Long shopId, Long userId, PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    IOrderStatusChangeLogService orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class,purchaseReturnDTO.getId());
      OrderStatus preStatus = purchaseReturn.getStatus();
      purchaseReturnDTO.setStatus(OrderStatus.SETTLED);
      purchaseReturn.setStatus(OrderStatus.SETTLED);
      purchaseReturn.setVestDate(purchaseReturnDTO.getVestDate());
      writer.update(purchaseReturn);

      OrderStatusChangeLogDTO orderStatusChangeLogDTO = new OrderStatusChangeLogDTO(shopId, userId, purchaseReturn.getStatus(), preStatus, purchaseReturn.getId(), OrderTypes.RETURN);
      orderStatusChangeLogService.saveOrderStatusChangeLog(orderStatusChangeLogDTO);
      writer.commit(status);

      OperationLogDTO operationLogDTO = new OperationLogDTO(shopId, userId, purchaseReturn.getId(), ObjectTypes.PURCHASE_RETURN_ORDER, OperationTypes.SETTLE);
      getTxnService().saveOperationLogTxnService(operationLogDTO);

      //add by WLF 更新缓存中待办入库退货单的数量
      List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseReturnDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_RETURN_ORDER, purchaseReturnDTO.getShopId(), supplierIdList);

      return purchaseReturnDTO;
    } finally {
      writer.rollback(status);
    }
  }

  public void doOrderIndexSave(PurchaseReturnDTO purchaseReturnDTO) {
    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Collection<OrderIndexDTO> orderIndexDTOs = new ArrayList<OrderIndexDTO>();
    try {
      if (purchaseReturnDTO != null) {
        List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
        ItemIndexDTO itemIndexDTO = null;
        OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
        orderIndexDTO.setShopId(purchaseReturnDTO.getShopId());
        orderIndexDTO.setOrderId(purchaseReturnDTO.getId());
        orderIndexDTO.setOrderType(OrderTypes.RETURN);
        orderIndexDTO.setOrderTotalAmount(purchaseReturnDTO.getTotal());
        orderIndexDTO.setCustomerOrSupplierId(purchaseReturnDTO.getSupplierId());
        orderIndexDTO.setCustomerOrSupplierName(purchaseReturnDTO.getSupplier());
        orderIndexDTO.setContactNum(purchaseReturnDTO.getMobile());
        orderIndexDTO.setReceiptNo(purchaseReturnDTO.getReceiptNo());
        List<PayMethod> payMethods = new ArrayList<PayMethod>();
        payMethods.add(PayMethod.CASH);
        orderIndexDTO.setPayMethods(payMethods);
        StringBuffer str = new StringBuffer();
        str.append("退货内容:");
        for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
          if (purchaseReturnItemDTO == null) continue;
          //添加每个单据的产品信息
          itemIndexDTO = new ItemIndexDTO();
          itemIndexDTO.setItemName(purchaseReturnItemDTO.getProductName());
          itemIndexDTO.setItemBrand(purchaseReturnItemDTO.getBrand());
          itemIndexDTO.setItemSpec(purchaseReturnItemDTO.getSpec());
          itemIndexDTO.setItemModel(purchaseReturnItemDTO.getModel());
          itemIndexDTO.setVehicleBrand(purchaseReturnItemDTO.getVehicleBrand());
          itemIndexDTO.setVehicleModel(purchaseReturnItemDTO.getVehicleModel());
          itemIndexDTO.setProductId(purchaseReturnItemDTO.getProductId());
          itemIndexDTO.setItemType(ItemTypes.MATERIAL);
          itemIndexDTO.setItemCount(purchaseReturnItemDTO.getAmount());
          itemIndexDTO.setItemPrice(purchaseReturnItemDTO.getPrice());

          itemIndexDTOList.add(itemIndexDTO);
          str.append("(品名:").append(purchaseReturnItemDTO.getProductName());
          if (!StringUtils.isEmpty(purchaseReturnItemDTO.getBrand())) {
            str.append(",品牌:").append(purchaseReturnItemDTO.getBrand()).append(",单价:").append(purchaseReturnItemDTO.getPrice())
              .append("数量:").append(purchaseReturnItemDTO.getAmount()).append(");");
          } else {
            str.append("(单价:").append(purchaseReturnItemDTO.getPrice()).append("数量:").append(purchaseReturnItemDTO.getAmount())
              .append(");");
          }
        }
        orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
        String orderContent = str.substring(0, str.length() - 1);
        if (orderContent.length() > 450) {
          orderContent = orderContent.substring(0, 450);
          orderContent = orderContent + "等";
        }
        orderIndexDTO.setOrderContent(orderContent);
        searchService.saveOrUpdateOrderIndex(orderIndexDTO);
//              orderIndexDTOs.add(orderIndexDTO);
//              orderIndexService.addOrderIndexToSolr(orderIndexDTOs);
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private PurchaseReturnItem generatePurchaseReturnItem(PurchaseReturnItemDTO purchaseReturnItemDTO, Long purchaseReturnId) throws Exception {
    PurchaseReturnItem purchaseReturnItem = new PurchaseReturnItem();
    purchaseReturnItem.setAmount(purchaseReturnItemDTO.getAmount());
    purchaseReturnItem.setMemo(purchaseReturnItemDTO.getMemo());
    purchaseReturnItem.setPrice(purchaseReturnItemDTO.getPrice());
    purchaseReturnItem.setProductId(purchaseReturnItemDTO.getProductId());
    purchaseReturnItem.setPurchaseReturnId(purchaseReturnId);
    purchaseReturnItem.setTotal(purchaseReturnItemDTO.getTotal());
    purchaseReturnItem.setUnit(purchaseReturnItemDTO.getUnit());
    purchaseReturnItem.setReserved(purchaseReturnItemDTO.getAmount());
    return purchaseReturnItem;
  }

  public InventorySearchIndex saveSearchIndexByReturn(PurchaseReturnItemDTO purchaseReturnItemDTO) throws Exception {
    InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
    inventorySearchIndex.setProductId(purchaseReturnItemDTO.getProductId());
    inventorySearchIndex.setProductName(purchaseReturnItemDTO.getProductName());
    inventorySearchIndex.setProductBrand(purchaseReturnItemDTO.getBrand());
    inventorySearchIndex.setProductSpec(purchaseReturnItemDTO.getSpec());
    inventorySearchIndex.setProductModel(purchaseReturnItemDTO.getModel());
    inventorySearchIndex.setBrand(purchaseReturnItemDTO.getVehicleBrand());
    inventorySearchIndex.setModel(purchaseReturnItemDTO.getVehicleModel());
    inventorySearchIndex.setYear(purchaseReturnItemDTO.getVehicleYear());
    inventorySearchIndex.setEngine(purchaseReturnItemDTO.getVehicleEngine());
    inventorySearchIndex.setProductVehicleStatus(purchaseReturnItemDTO.getProductVehicleStatus());
    inventorySearchIndex.setPurchasePrice(purchaseReturnItemDTO.getPrice());
    return inventorySearchIndex;
  }

  private SupplierDTO generateSupplierDTO(PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.setId(purchaseReturnDTO.getSupplierId());
    supplierDTO.setShopId(purchaseReturnDTO.getShopId());
    supplierDTO.setSupplierShopId(purchaseReturnDTO.getSupplierShopId());
    supplierDTO.setName(purchaseReturnDTO.getSupplier());
    supplierDTO.setContact(purchaseReturnDTO.getContact());
    if (mobileValidation(purchaseReturnDTO.getMobile())) {
      supplierDTO.setMobile(purchaseReturnDTO.getMobile());
      supplierDTO.setLandLine(purchaseReturnDTO.getLandline());
    } else {
      supplierDTO.setLandLine(purchaseReturnDTO.getMobile());
      supplierDTO.setMobile("");
    }
    supplierDTO.setAddress(purchaseReturnDTO.getAddress());
    supplierDTO.setBusinessScope(purchaseReturnDTO.getBusinessScope());
    supplierDTO.setBank(purchaseReturnDTO.getBank());
    supplierDTO.setAccount(purchaseReturnDTO.getAccount());
    supplierDTO.setAccountName(purchaseReturnDTO.getAccountName());
    supplierDTO.setCategory(purchaseReturnDTO.getCategory());
    supplierDTO.setAbbr(purchaseReturnDTO.getAbbr());
    supplierDTO.setSettlementTypeId(purchaseReturnDTO.getSettlementType());
    supplierDTO.setFax(purchaseReturnDTO.getFax());
    supplierDTO.setQq(purchaseReturnDTO.getQq());
    supplierDTO.setInvoiceCategoryId(purchaseReturnDTO.getInvoiceCategory());
    supplierDTO.setEmail(purchaseReturnDTO.getEmail());

    return supplierDTO;
  }

  public static boolean mobileValidation(String mobile) {
    if (!mobile.isEmpty() && mobile.substring(0, 1).equals("1") && mobile.indexOf("-") == -1 && mobile.length() == 11) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public Map doItemChecked(Map map, Integer[] indexNo, Double[] itemCount, ItemIndexDTO itemIndexDTO, String prePageNum) throws Exception {
    List<String> list = (List<String>) map.get("checkedId");
    List<Double> numbers = (List<Double>) map.get("itemNumber");
    String pageFlag = (String) map.get("pageFlag");
    List<ItemIndexDTO> indexDTOs = (List<ItemIndexDTO>) map.get("indexDTOs");
    List<ItemIndexDTO> itemIndexDTOList = (List<ItemIndexDTO>) map.get("itemIndexDTOs");
    int pageNo = Integer.parseInt(itemIndexDTO.getPageNo());
    if (prePageNum != null) {
      pageNo = Integer.parseInt(prePageNum);
    }
    if (indexNo != null && indexNo.length > 0) {
      if (itemIndexDTOList != null && itemIndexDTOList.size() > 0) {
        if (map.get("supplierName") == null) {
          map.put("supplierName", itemIndexDTOList.get(indexNo[0]).getCustomerOrSupplierName());
        }
        for (int j = 0; j < list.size(); j++) {
          if (list.get(j).substring(list.get(j).length() - (pageNo + "").length(), list.get(j).length()).equals(pageNo + "")) {
            boolean flag = false;
            for (int m = 0; m < indexNo.length; m++) {
              if (list.get(j).equals(indexNo[m] + "" + pageNo)) {
                flag = true;
                break;
              }
            }
            if (!flag) {
              list.remove(j);
              numbers.remove(j);
              indexDTOs.remove(j);
              j -= 1;
            }
          }
        }
        for (int i = 0; i < indexNo.length; i++) {
          boolean flag = true;
          for (String s : list) {
            if ((indexNo[i] + "" + pageNo).equals(s)) {
              flag = false;
              break;
            }
          }
          if (flag) {
            list.add(indexNo[i] + "" + pageNo);
            numbers.add(itemCount[indexNo[i]]);
            itemIndexDTOList.get(indexNo[i]).setItemCount(itemCount[indexNo[i]].doubleValue());
            indexDTOs.add(itemIndexDTOList.get(indexNo[i]));
          }
        }
        map.put("checkedId", list);
        map.put("indexDTOs", indexDTOs);
        map.put("itemNumber", numbers);
      }
    } else {
      if (list == null || list.size() > 0) {
        map.remove("supplierName");
      }
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).substring(list.get(i).length() - (pageNo + "").length(), list.get(i).length()).equals(pageNo + "")) {
          list.remove(i);
          numbers.remove(i);
          indexDTOs.remove(i);
          i -= 1;
        }
      }
      map.put("checkedId", list);
      map.put("indexDTOs", indexDTOs);
      map.put("itemNumber", numbers);
    }
    return map;
  }

  @Override
  public PurchaseReturnDTO getPurchaseReturnDTOById(Long purchaseReturnId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class, purchaseReturnId);
    if (purchaseReturn != null) {
      PurchaseReturnDTO purchaseReturnDTO = purchaseReturn.toDTO();
      purchaseReturnDTO.setVestDateStr(DateUtil.dateLongToStr(purchaseReturnDTO.getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
      List<PurchaseReturnItem> items = writer.getPurchaseReturnItemsByReturnId(purchaseReturnId);
      PurchaseReturnItemDTO[] itemDTOs = new PurchaseReturnItemDTO[items.size()];
      purchaseReturnDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        PurchaseReturnItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      return purchaseReturnDTO;
    }
    return null;
  }

  @Override
  public void updateSaleOrderStatus(Long shopId, Long saleOrderId, OrderStatus saleOrderStatus) throws Exception {
    if (shopId == null || saleOrderId == null) {
      return;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      List<SalesOrder> salesOrders = txnWriter.getSalesOrderById(saleOrderId, shopId);
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        salesOrders.get(0).setStatusEnum(saleOrderStatus);
        txnWriter.update(salesOrders.get(0));
        txnWriter.commit(status);
      }

    } finally {
      txnWriter.rollback(status);
    }
  }

  @Override
  public void updateSaleOrderStatus(Long shopId, Long saleOrderId, OrderStatus saleOrderStatus, TxnWriter writer) throws Exception {
    if (shopId == null || saleOrderId == null) {
      return;
    }
    List<SalesOrder> salesOrders = writer.getSalesOrderById(saleOrderId, shopId);
    if (CollectionUtils.isNotEmpty(salesOrders)) {
      salesOrders.get(0).setStatusEnum(saleOrderStatus);
      writer.update(salesOrders.get(0));
    }
  }

  @Override
  public CustomerRecordDTO updateCustomerRecordByShopIdAndOrderId(Long shopId, Long orderId, Long customerId, OrderTypes orderType) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    IUserService userService = ServiceManager.getService(IUserService.class);
    Receivable receivable = writer.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, orderType, orderId);
    CustomerRecord customerRecord = userService.getRecordByCustomerId(customerId);
    if (customerRecord != null && receivable != null) {
      if (receivable.getDiscount() == null) {
        receivable.setDiscount(0d);
      }
      customerRecord.setTotalAmount(customerRecord.getTotalAmount() - receivable.getSettledAmount() - receivable.getDebt());
      customerRecord.setTotalReceivable(customerRecord.getTotalReceivable() - receivable.getDebt());

      customerRecord.setConsumeTimes(NumberUtil.longValue(customerRecord.getConsumeTimes()) - 1);
      customerRecord.setMemberConsumeTotal(NumberUtil.doubleVal(customerRecord.getMemberConsumeTotal()) - NumberUtil.doubleVal(receivable.getMemberBalancePay()));

      if (NumberUtil.doubleVal(receivable.getMemberBalancePay()) > 0 || receivable.getMemberId() != null) {
        customerRecord.setMemberConsumeTimes(NumberUtil.longValue(customerRecord.getMemberConsumeTimes()) - 1);
      }


      userService.updateCustomerRecord(customerRecord);
    }
    return customerRecord==null?null:customerRecord.toDTO();
  }

  @Override
  public ReceivableDTO updateReceivable(Long shopId, Long orderId, OrderTypes orderType, ReceivableStatus status) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object stat = writer.begin();
    try {
      ReceivableDTO receivableDTO = updateReceivable(shopId, orderId, orderType, status, writer);
      writer.commit(stat);
      return receivableDTO;
    } finally {
      writer.rollback(stat);
    }
  }

  @Override
  public ReceivableDTO updateReceivable(Long shopId, Long orderId, OrderTypes orderType, ReceivableStatus status, TxnWriter writer) throws Exception {
    Receivable receivable = writer.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, orderType, orderId);
    if (receivable != null) {
      receivable.setStatusEnum(status);
      writer.update(receivable);
      return receivable.toDTO();
    }
    return null;
  }

  @Override
  public void updateDebtByRepealOrder(Long shopId, Long orderId, Long customerId, DebtStatus debtStatus) throws Exception {
    if (shopId == null || orderId == null || customerId == null) {
      LOGGER.error("updateDebtByRepealOrder[shopId:{},orderId:{},customerId:{}] failed.", new Long[]{shopId, orderId, customerId});
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      updateDebtByRepealOrder(shopId, orderId, customerId, debtStatus, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateDebtByRepealOrder(Long shopId, Long orderId, Long customerId, DebtStatus debtStatus, TxnWriter writer) throws Exception {
    if (shopId == null || orderId == null || customerId == null) {
      LOGGER.error("updateDebtByRepealOrder[shopId:{},orderId:{},customerId:{}] failed.", new Long[]{shopId, orderId, customerId});
      return;
    }
    Debt debt = writer.getDebtByShopIdAndCustomerIdAndOrderId(shopId, customerId, orderId);
    if (debt != null) {
      debt.setStatusEnum(debtStatus);
      writer.update(debt);
      if(!debtStatus.equals(DebtStatus.ARREARS)){
        getTxnService().cancelRemindEventByOldRemindEventId(RemindEventType.DEBT, debt.getId(), writer);
        //更新缓存
        getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, shopId);
      }
    }
  }

  @Override
  public void saveRepealOrderByOrderIdAndOrderType(Long shopId, Long orderId, OrderTypes orderType) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    RepealOrder repealOrder = null;
    try {
      if (OrderTypes.PURCHASE == orderType) {
        repealOrder = this.doRepealOrderByPurchaseOrder(writer.getPurchaseOrderById(orderId, shopId).get(0));
      } else if (OrderTypes.INVENTORY == orderType) {
        repealOrder = this.doRepealOrderByPurchaseInventory(writer.getPurchaseInventoryById(orderId, shopId).get(0));
      } else if (OrderTypes.SALE == orderType) {
        repealOrder = this.doRepealOrderBySalesOrder(writer.getSalesOrderById(orderId, shopId).get(0));
      } else if (OrderTypes.REPAIR == orderType) {
        repealOrder = this.doRepealOrderByRepairOrder(writer.getById(RepairOrder.class, orderId));
      } else if (OrderTypes.WASH_BEAUTY.equals(orderType)) {
        WashBeautyOrder washBeautyOrder = writer.getWashBeautyOrderDTOById(shopId, orderId);
        if (washBeautyOrder != null) {
          repealOrder = this.doRepealOrderByWashBeauty(washBeautyOrder);
        }

      } else if (OrderTypes.RETURN == orderType) {
        repealOrder = this.doRepealOrderByPurchaseReturn(writer.getById(PurchaseReturn.class, orderId));
      }
      if (repealOrder != null) {
        writer.save(repealOrder);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public PurchaseOrderDTO copyPurchaseOrder(Long shopId, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    txnService = ServiceManager.getService(ITxnService.class);
    productService = ServiceManager.getService(IProductService.class);
    if (purchaseOrderDTO == null) {
      return null;
    }
    purchaseOrderDTO.setId(null);
    purchaseOrderDTO.setStatus(null);
    purchaseOrderDTO.setLastModified(null);
    purchaseOrderDTO.setNo(null);
    SupplierDTO historySupplierDTO = purchaseOrderDTO.generateSupplierDTO();
    boolean supplierSame = ServiceManager.getService(ISupplierService.class).compareSupplierSameWithHistory(historySupplierDTO, purchaseOrderDTO.getShopId());
    if(!supplierSame){
      purchaseOrderDTO.clearSupplierInfo();
    }

    PurchaseOrderItemDTO[] purchaseOrderItemDTOs = purchaseOrderDTO.getItemDTOs();
    List<PurchaseOrderItemDTO> newPurchaseOrderItemDTOLists = new ArrayList<PurchaseOrderItemDTO>();
    if (purchaseOrderItemDTOs != null && purchaseOrderItemDTOs.length > 0) {
      for (int i = 0; i < purchaseOrderItemDTOs.length; i++) {
        if (purchaseOrderItemDTOs[i] == null || purchaseOrderItemDTOs[i].getProductId() == null) {
          continue;
        }

        if(!productHistoryService.compareProductSameWithHistory(purchaseOrderItemDTOs[i].getProductId(), purchaseOrderItemDTOs[i].getProductHistoryId(), purchaseOrderDTO.getShopId())){
          continue;
        }
        purchaseOrderItemDTOs[i].setId(null);
        purchaseOrderItemDTOs[i].setPurchaseOrderId(null);
        //清除特殊信息  因为如果原来是特殊采购单 但是现在关系没了 或者 批发商有改动就用普通采购复制逻辑
        purchaseOrderItemDTOs[i].setPromotionsInfoJson(null);
        purchaseOrderItemDTOs[i].setPromotionsDTOs(null);
        purchaseOrderItemDTOs[i].setPromotionsIds(null);
        purchaseOrderItemDTOs[i].setSupplierProductId(null);

        ProductDTO productDTO = productService.getProductByProductLocalInfoId(purchaseOrderItemDTOs[i].getProductId(),purchaseOrderDTO.getShopId());

        String kindName = null;

        if(null != productDTO && null != productDTO.getKindId()) {
          KindDTO kindDTO = productService.getEnabledKindDTOById(shopId, productDTO.getKindId());
          if (null != kindDTO) {
            kindName = kindDTO.getName();
          }
        }

        purchaseOrderItemDTOs[i].setProductKind(kindName);
        if(productDTO!=null){
          purchaseOrderItemDTOs[i].setSellUnit(productDTO.getSellUnit());
          purchaseOrderItemDTOs[i].setStorageUnit(productDTO.getStorageUnit());
          purchaseOrderItemDTOs[i].setRate(productDTO.getRate());
        }

        newPurchaseOrderItemDTOLists.add(purchaseOrderItemDTOs[i]);
      }
    }
    if (CollectionUtils.isNotEmpty(newPurchaseOrderItemDTOLists)) {
      double total = 0;
      PurchaseOrderItemDTO[] newPurchaseOrderItemDTOs = new PurchaseOrderItemDTO[newPurchaseOrderItemDTOLists.size()];
      for (int i = 0; i < newPurchaseOrderItemDTOLists.size(); i++) {
        newPurchaseOrderItemDTOs[i] = newPurchaseOrderItemDTOLists.get(i);
        Long productId = newPurchaseOrderItemDTOs[i].getProductId();
        if(productId != null){
          InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopId, productId);
          if(inventoryDTO != null){
            newPurchaseOrderItemDTOs[i].setInventoryAmount(inventoryDTO.getAmount());
            newPurchaseOrderItemDTOs[i].setUpperLimit(inventoryDTO.getUpperLimit());
            newPurchaseOrderItemDTOs[i].setLowerLimit(inventoryDTO.getLowerLimit());
          }

          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(productId, shopId);
          if(productLocalInfoDTO != null){
            newPurchaseOrderItemDTOs[i].setTradePrice(productLocalInfoDTO.getTradePrice());
            newPurchaseOrderItemDTOs[i].setStorageBin(productLocalInfoDTO.getStorageBin());
          }
        }
        total += newPurchaseOrderItemDTOLists.get(i).getTotal() == null ? 0D : newPurchaseOrderItemDTOLists.get(i).getTotal();
      }
      purchaseOrderDTO.setItemDTOs(newPurchaseOrderItemDTOs);
      purchaseOrderDTO.setTotal(NumberUtil.round(total, NumberUtil.MONEY_PRECISION));
    }else{
      purchaseOrderDTO.setItemDTOs(new PurchaseOrderItemDTO[0]);
      purchaseOrderDTO.setTotal(0);
    }
    return purchaseOrderDTO;
  }

  @Override
  public PurchaseInventoryDTO copyPurchaseInventory(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {

    productService = ServiceManager.getService(IProductService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    if (purchaseInventoryDTO == null) {
      return null;
    }
    PurchaseInventoryDTO newPurchaseInventoryDTO = purchaseInventoryDTO.clone();
    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
    newPurchaseInventoryDTO.setEditDateStr(time);
    newPurchaseInventoryDTO.setVestDateStr(time);
    newPurchaseInventoryDTO.setEditDate(curTime);
    newPurchaseInventoryDTO.setVestDate(curTime);
    newPurchaseInventoryDTO.setId(null);
    newPurchaseInventoryDTO.setStatus(null);
    newPurchaseInventoryDTO.setLastModified(null);
    newPurchaseInventoryDTO.setPurchaseOrderId(null);
    newPurchaseInventoryDTO.setRepairOrderId(null);
    newPurchaseInventoryDTO.setPurchaseOrderNo(null);

    SupplierDTO historySupplierDTO = newPurchaseInventoryDTO.generateSupplierDTO();
    historySupplierDTO.setId(newPurchaseInventoryDTO.getSupplierId());
    boolean supplierSame = supplierService.compareSupplierSameWithHistory(historySupplierDTO, shopId);
    if(!supplierSame){
      newPurchaseInventoryDTO.clearSupplierInfo();
    }
    PurchaseInventoryItemDTO[] purchaseInventoryItemDTOs = purchaseInventoryDTO.getItemDTOs();
    List<PurchaseInventoryItemDTO> newPurchaseInventoryItemDTOLists = new ArrayList<PurchaseInventoryItemDTO>();
    if (purchaseInventoryItemDTOs != null && purchaseInventoryItemDTOs.length > 0) {
      for (int i = 0; i < purchaseInventoryItemDTOs.length; i++) {

        Long productId = purchaseInventoryItemDTOs[i].getProductId();
        if (purchaseInventoryItemDTOs[i] == null || productId == null || purchaseInventoryItemDTOs[i].getProductHistoryId() == null
          || !productHistoryService.compareProductSameWithHistory(productId, purchaseInventoryItemDTOs[i].getProductHistoryId(), shopId)) {
          continue;
        }
        ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, purchaseInventoryDTO.getShopId());

        String kindName = null;

        if (null != productDTO && null != productDTO.getKindId()) {
          KindDTO kindDTO = productService.getEnabledKindDTOById(shopId, productDTO.getKindId());
          kindName = kindDTO==null?null:kindDTO.getName();
        }
        purchaseInventoryItemDTOs[i].setProductKind(kindName);
        if(productDTO!=null){
          purchaseInventoryItemDTOs[i].setSellUnit(productDTO.getSellUnit());
          purchaseInventoryItemDTOs[i].setStorageUnit(productDTO.getStorageUnit());
          purchaseInventoryItemDTOs[i].setRate(productDTO.getRate());
        }

        purchaseInventoryItemDTOs[i].setId(null);
        purchaseInventoryItemDTOs[i].setPurchaseItemId(null);
        purchaseInventoryItemDTOs[i].setPurchaseInventoryId(null);
        newPurchaseInventoryItemDTOLists.add(purchaseInventoryItemDTOs[i]);
      }
    }
    if (CollectionUtils.isNotEmpty(newPurchaseInventoryItemDTOLists)) {
      double total = 0;
      PurchaseInventoryItemDTO[] newPurchaseInventoryItemDTOs = new PurchaseInventoryItemDTO[newPurchaseInventoryItemDTOLists.size()];
      for (int i = 0; i < newPurchaseInventoryItemDTOLists.size(); i++) {
        newPurchaseInventoryItemDTOs[i] = newPurchaseInventoryItemDTOLists.get(i);
        total += newPurchaseInventoryItemDTOLists.get(i).getTotal() == null ? 0D : newPurchaseInventoryItemDTOLists.get(i).getTotal();
      }
      newPurchaseInventoryDTO.setItemList(newPurchaseInventoryItemDTOLists);
      newPurchaseInventoryDTO.setItemDTOs(newPurchaseInventoryItemDTOs);
      newPurchaseInventoryDTO.setTotal(NumberUtil.round(total, NumberUtil.MONEY_PRECISION));
      newPurchaseInventoryDTO.setStroageCreditAmount(NumberUtil.round(total, NumberUtil.MONEY_PRECISION));         //重新录入时，默认全部挂账
    }else{
      newPurchaseInventoryDTO.setItemDTOs(new PurchaseInventoryItemDTO[0]);
      newPurchaseInventoryDTO.setTotal(0d);
    }
    return newPurchaseInventoryDTO;
  }

  @Override
  public SalesOrderDTO copyGoodSale(Long shopId, SalesOrderDTO salesOrderDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    if (salesOrderDTO == null) {
      salesOrderDTO = new SalesOrderDTO();
    }
    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
    salesOrderDTO.setEditDateStr(time);
    salesOrderDTO.setVestDateStr(time);
    salesOrderDTO.setEditDate(curTime);
    salesOrderDTO.setVestDate(curTime);
    salesOrderDTO.setShopId(shopId);
    salesOrderDTO.setId(null);
    salesOrderDTO.setStatus(null);
    salesOrderDTO.setSettledAmount(salesOrderDTO.getTotal());
    salesOrderDTO.setTotalHid(0d);
    salesOrderDTO.setSettledAmountHid(0d);
    salesOrderDTO.setDebt(0d);
    salesOrderDTO.setReceivableId(null);

    if (salesOrderDTO.getItemDTOs() != null) {
      SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
      SalesOrderItemDTO[] resultItems = new SalesOrderItemDTO[0];
      for (int i = 0; i < salesOrderItemDTOs.length; i++) {
        Long productId = salesOrderItemDTOs[i].getProductId();
        if (salesOrderItemDTOs[i] == null || productId == null
          || !productHistoryService.compareProductSameWithHistory(productId, salesOrderItemDTOs[i].getProductHistoryId(), shopId)) {
          continue;
        }

        salesOrderItemDTOs[i].setId(null);
        salesOrderItemDTOs[i].setSalesOrderId(null);
        salesOrderItemDTOs[i].setReserved(null);
        salesOrderItemDTOs[i].setShortage(null);
        salesOrderItemDTOs[i].setShopId(shopId);
        salesOrderItemDTOs[i].setAmountHid(0d);

        ProductDTO productDTO = productService.getProductByProductLocalInfoId(salesOrderItemDTOs[i].getProductId(), salesOrderDTO.getShopId());
        if(productDTO!=null){
          salesOrderItemDTOs[i].setSellUnit(productDTO.getSellUnit());
          salesOrderItemDTOs[i].setStorageUnit(productDTO.getStorageUnit());
          salesOrderItemDTOs[i].setRate(productDTO.getRate());
        }

        InventoryDTO inventoryDTO = txnService.getInventoryByShopIdAndProductId(salesOrderDTO.getShopId(), salesOrderItemDTOs[i].getProductId());
        //如果采购单位是库存大单位
        if (UnitUtil.isStorageUnit(salesOrderItemDTOs[i].getUnit(),productDTO)) {
          salesOrderItemDTOs[i].setInventoryAmount(inventoryDTO.getAmount() / salesOrderItemDTOs[i].getRate());
          salesOrderItemDTOs[i].setPurchasePrice(inventoryDTO.getLatestInventoryPrice() == null ?
            0d : inventoryDTO.getLatestInventoryPrice() * productDTO.getRate());
        } else {
          salesOrderItemDTOs[i].setInventoryAmount(inventoryDTO.getAmount());
          salesOrderItemDTOs[i].setPurchasePrice(inventoryDTO.getLatestInventoryPrice() == null ? 0 : inventoryDTO.getLatestInventoryPrice());
        }
        if(null != salesOrderItemDTOs[i].getProductId()) {
          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesOrderItemDTOs[i].getProductId(), shopId);
          if (null != productLocalInfoDTO && null != productLocalInfoDTO.getBusinessCategoryId()) {
            Category category = getEnabledCategoryById(shopId, productLocalInfoDTO.getBusinessCategoryId());
            if (null == category) {
              salesOrderItemDTOs[i].setBusinessCategoryId(null);
              salesOrderItemDTOs[i].setBusinessCategoryName(null);
            } else {
              salesOrderItemDTOs[i].setBusinessCategoryId(category.getId());
              salesOrderItemDTOs[i].setBusinessCategoryName(category.getCategoryName());
            }
          } else {
            salesOrderItemDTOs[i].setBusinessCategoryId(null);
            salesOrderItemDTOs[i].setBusinessCategoryName(null);
          }
        }
        else {
          salesOrderItemDTOs[i].setBusinessCategoryId(null);
          salesOrderItemDTOs[i].setBusinessCategoryName(null);
        }
        resultItems = (SalesOrderItemDTO[])ArrayUtils.add(resultItems, salesOrderItemDTOs[i]);
      }
      salesOrderDTO.setItemDTOs(resultItems);
    }

    if(CollectionUtils.isNotEmpty(salesOrderDTO.getOtherIncomeItemDTOList())) {
      for (SalesOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : salesOrderDTO.getOtherIncomeItemDTOList()) {
        orderOtherIncomeItemDTO.setId(null);
        orderOtherIncomeItemDTO.setOrderId(null);
      }
    }
    return salesOrderDTO;
  }

  @Override
  public void initIntemIndexProductId(ModelMap model) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    List<ItemIndex> itemIndexes = new ArrayList<ItemIndex>();
    Long totalCount = searchService.countNullProductIDItemIndex();
    Pager pager = new Pager(totalCount == null ? 0 : totalCount.intValue(), 1, 500);
    Long noNeedUpdateCount =  0L;
    List<Long> itemDTONotFound = new ArrayList<Long>();
    List<Long> itemDTOProductIDNotFound = new ArrayList<Long>();
    List<Long> itemDTOItemIndexFailUpdate = new ArrayList<Long>();
    List<Long> itemOrderTypeNotFount = new ArrayList<Long>();
    model.addAttribute("totalItemIndexCount", totalCount);
    LOGGER.info("总共有{}条数据等待更新", pager.getTotalRows());
    if (totalCount == null || (new Long(0L).equals(totalCount))) {
      return;
    }
    do {
      LOGGER.info("开始执行第{}组数据,每组{}条", pager.getCurrentPage(), pager.getPageSize());
      Long startTime = System.currentTimeMillis();
      itemIndexes = searchService.getNullProductIDItemIndexs(pager);
      pager.setCurrentPage(pager.getCurrentPage() + 1);

      if (CollectionUtils.isNotEmpty(itemIndexes)) {
        for (ItemIndex itemIndex : itemIndexes) {
          if (OrderTypes.PURCHASE.equals(itemIndex.getOrderTypeEnum())) {      //更新采购单productId
            if (itemIndex.getProductId() == null) {
              PurchaseOrderItem purchaseOrderItem = writer.getById(PurchaseOrderItem.class, itemIndex.getItemId());
              if (purchaseOrderItem == null) {
                itemDTONotFound.add(itemIndex.getId());
                continue;
              }
              if (purchaseOrderItem.getProductId() == null) {
                itemDTOProductIDNotFound.add(itemIndex.getId());
                continue;
              }
              itemIndex.setProductId(purchaseOrderItem.getProductId());
              searchService.updateItemIndex(itemIndex, model, itemDTOItemIndexFailUpdate);
              continue;
            } else {
              noNeedUpdateCount++;
              continue;
            }
          } else if (OrderTypes.INVENTORY.equals(itemIndex.getOrderTypeEnum())) {  //更新入库单productId
            if (itemIndex.getProductId() == null) {
              PurchaseInventoryItem purchaseInventoryItem = writer.getById(PurchaseInventoryItem.class, itemIndex.getItemId());
              if (purchaseInventoryItem == null) {
                itemDTONotFound.add(itemIndex.getId());
                continue;
              }
              if (purchaseInventoryItem.getProductId() == null) {
                itemDTOProductIDNotFound.add(itemIndex.getId());
                continue;
              }
              itemIndex.setProductId(purchaseInventoryItem.getProductId());
              searchService.updateItemIndex(itemIndex, model, itemDTOItemIndexFailUpdate);
              continue;
            } else {
              noNeedUpdateCount++;
              continue;
            }
          } else if (OrderTypes.REPAIR.equals(itemIndex.getOrderTypeEnum())) {     //更新维修单productId
            if (itemIndex.getProductId() == null) {
              RepairOrderItem repairOrderItem = writer.getById(RepairOrderItem.class, itemIndex.getItemId());
              if (repairOrderItem == null) {
                itemDTONotFound.add(itemIndex.getId());
                continue;
              }
              if (repairOrderItem.getProductId() == null) {
                itemDTOProductIDNotFound.add(itemIndex.getId());
                continue;
              }
              itemIndex.setProductId(repairOrderItem.getProductId());
              searchService.updateItemIndex(itemIndex, model, itemDTOItemIndexFailUpdate);
              continue;
            } else {
              noNeedUpdateCount++;
              continue;
            }
          } else if (OrderTypes.RETURN.equals(itemIndex.getOrderTypeEnum())) {
            if (itemIndex.getProductId() == null) {
              PurchaseReturnItem purchaseReturnItem = writer.getById(PurchaseReturnItem.class, itemIndex.getItemId());
              if (purchaseReturnItem == null) {
                itemDTONotFound.add(itemIndex.getId());
                continue;
              }
              if (purchaseReturnItem.getProductId() == null) {
                itemDTOProductIDNotFound.add(itemIndex.getId());
                continue;
              }
              itemIndex.setProductId(purchaseReturnItem.getProductId());
              searchService.updateItemIndex(itemIndex, model, itemDTOItemIndexFailUpdate);
              continue;
            } else {
              noNeedUpdateCount++;
              continue;
            }
          } else if (OrderTypes.SALE.equals(itemIndex.getOrderTypeEnum())) {
            if (itemIndex.getProductId() == null) {
              SalesOrderItem salesOrderItem = writer.getById(SalesOrderItem.class, itemIndex.getItemId());
              if (salesOrderItem == null) {
                itemDTONotFound.add(itemIndex.getId());
                continue;
              }
              if (salesOrderItem.getProductId() == null) {
                itemDTOProductIDNotFound.add(itemIndex.getId());
                continue;
              }
              itemIndex.setProductId(salesOrderItem.getProductId());
              searchService.updateItemIndex(itemIndex, model, itemDTOItemIndexFailUpdate);
              continue;
            } else {
              noNeedUpdateCount++;
              continue;
            }
          } else if (OrderTypes.WASH.equals(itemIndex.getOrderTypeEnum())) {
            noNeedUpdateCount++;
            continue;
          } else {
            itemOrderTypeNotFount.add(itemIndex.getId());
            continue;
          }
        }
      }
      Long endTime = System.currentTimeMillis();
      LOGGER.info("执行第{}组数据更新,共耗时{}毫秒", pager.getCurrentPage() - 1, (endTime - startTime));
      LOGGER.info("已经完成{}%,剩余数据预计更新需要{}秒,请稍等", ((pager.getCurrentPage() - 1) * 1.0 / pager.getTotalPage()) * 100, (pager.getTotalPage() - pager.getCurrentPage() + 1) * ((endTime - startTime) / 1000.0));
    } while ((pager.getCurrentPage() - 1) < pager.getTotalPage());
    model.addAttribute("totalItemIndexCount", totalCount);
    model.addAttribute("itemDTONotFound", itemDTONotFound);
    model.addAttribute("noNeedUpdateCount", noNeedUpdateCount);
    model.addAttribute("itemDTOProductIDNotFound", itemDTOProductIDNotFound);
    model.addAttribute("itemDTOItemIndexFailUpdate", itemDTOItemIndexFailUpdate);
    model.addAttribute("itemOrderTypeNotFount", itemOrderTypeNotFount);
  }

  @Override
  public List<PurchaseReturnItemDTO> getPurchaseReturnItemDTOs(Long shopId, ItemIndexDTO itemIndexDTO, Pager pager) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<ItemIndexDTO> itemIndexDTOs = searchService.getItemIndexDTO(shopId, itemIndexDTO, pager);
    List<PurchaseReturnItemDTO> purchaseReturnItemDTOs = new ArrayList<PurchaseReturnItemDTO>();
    List<OrderIndexDTO> orderIndexDTOs = new ArrayList<OrderIndexDTO>();
    if (CollectionUtils.isNotEmpty(itemIndexDTOs)) {
      for (ItemIndexDTO tempItemindexDTO : itemIndexDTOs) {
        if (tempItemindexDTO.getCustomerId() == null || tempItemindexDTO.getProductId() == null) {
          continue;
        }
        PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
        purchaseReturnItemDTO.setSupplier(tempItemindexDTO);
        ProductDTO productDTO = productService.getProductByProductLocalInfoId(tempItemindexDTO.getProductId(), shopId);
        purchaseReturnItemDTO.setProduct(productDTO);
        InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopId, tempItemindexDTO.getProductId());
        List<ItemIndexDTO> itemIndexDTODetailList = searchService.getPurchaseReturnItemIndexDTOs(shopId, tempItemindexDTO.getCustomerId(),
          tempItemindexDTO.getProductId(), orderIndexDTOs);
        caculateReturnAbleAmount(purchaseReturnItemDTO, itemIndexDTODetailList, productDTO, inventoryDTO);
        ItemIndexDTO[] indexDTOs = new ItemIndexDTO[itemIndexDTODetailList.size()];
        purchaseReturnItemDTO.setItemIndexDTOs(itemIndexDTODetailList.toArray(indexDTOs));
        purchaseReturnItemDTOs.add(purchaseReturnItemDTO);
      }
    }
    return purchaseReturnItemDTOs;
  }

  //计算可退货数量
  private void caculateReturnAbleAmount(PurchaseReturnItemDTO purchaseReturnItemDTO, List<ItemIndexDTO> itemIndexDTOs,
                                        ProductDTO productDTO, InventoryDTO inventoryDTO) {
    if (CollectionUtils.isEmpty(itemIndexDTOs) || productDTO == null || inventoryDTO == null) {
      LOGGER.info("ItemIndexDTOs{} or InventorySearchIndexDTO{} is null", itemIndexDTOs, productDTO);
      LOGGER.info("inventoryDTO{} ", inventoryDTO);
      return;
    }
    if (purchaseReturnItemDTO == null) {
      purchaseReturnItemDTO = new PurchaseReturnItemDTO();
    }
    double returnAbleAmount = 0;
    purchaseReturnItemDTO.setUnit(productDTO.getSellUnit());
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOs) {
      if (OrderTypes.INVENTORY == itemIndexDTO.getOrderType() && OrderStatus.PURCHASE_INVENTORY_DONE == itemIndexDTO.getOrderStatus()) {
        if (UnitUtil.isStorageUnit(itemIndexDTO.getUnit(), productDTO)) {
          returnAbleAmount += (itemIndexDTO.getItemCount() == null ? 0 : itemIndexDTO.getItemCount()) * productDTO.getRate();
        } else {
          returnAbleAmount += (itemIndexDTO.getItemCount() == null ? 0 : itemIndexDTO.getItemCount());
        }
      } else if (OrderTypes.RETURN == itemIndexDTO.getOrderType()) {
        if (UnitUtil.isStorageUnit(itemIndexDTO.getUnit(), productDTO)) {
          returnAbleAmount -= (itemIndexDTO.getItemCount() == null ? 0 : itemIndexDTO.getItemCount()) * productDTO.getRate();
        } else {
          returnAbleAmount -= (itemIndexDTO.getItemCount() == null ? 0 : itemIndexDTO.getItemCount());
        }
      }
    }
    if (returnAbleAmount > inventoryDTO.getAmount()) {
      returnAbleAmount = inventoryDTO.getAmount();
    }
    purchaseReturnItemDTO.setReturnAbleAmount(returnAbleAmount);
  }

  public RepealOrder doRepealOrderByPurchaseOrder(PurchaseOrder purchaseOrder) {
    RepealOrder repealOrder = null;
    if (purchaseOrder != null) {
      repealOrder = new RepealOrder();
      repealOrder.setDate(purchaseOrder.getDate());
      repealOrder.setTotal(0 - purchaseOrder.getTotal());
      repealOrder.setDept(purchaseOrder.getDept());
      repealOrder.setDeptId(purchaseOrder.getDeptId() == null ? null : purchaseOrder.getDeptId().longValue());
      repealOrder.setEditDate(purchaseOrder.getEditDate());
      repealOrder.setEditor(purchaseOrder.getEditor());
      repealOrder.setEditorId(purchaseOrder.getEditorId());
      repealOrder.setExecutor(purchaseOrder.getExecutor());
      repealOrder.setExecutorId(purchaseOrder.getExecutorId());
      repealOrder.setMemo(purchaseOrder.getMemo());
      repealOrder.setNo(purchaseOrder.getNo());
      repealOrder.setOrderId(purchaseOrder.getId());
      repealOrder.setOrderTypeEnum(OrderTypes.PURCHASE);
      repealOrder.setRepealDate(System.currentTimeMillis());
      repealOrder.setShopId(purchaseOrder.getShopId());
      repealOrder.setStatus(purchaseOrder.getStatus());
      repealOrder.setStatusEnum(purchaseOrder.getStatusEnum());
      repealOrder.setVestDate(purchaseOrder.getVestDate());
    }
    return repealOrder;
  }

  public RepealOrder doRepealOrderByPurchaseInventory(PurchaseInventory purchaseInventory) {
    RepealOrder repealOrder = null;
    if (purchaseInventory != null) {
      repealOrder = new RepealOrder();
      repealOrder.setDate(purchaseInventory.getDate());
      repealOrder.setTotal(0 - purchaseInventory.getTotal());
      repealOrder.setDept(purchaseInventory.getDept());
      repealOrder.setDeptId(purchaseInventory.getDeptId());
      repealOrder.setEditDate(purchaseInventory.getEditDate());
      repealOrder.setEditor(purchaseInventory.getEditor());
      repealOrder.setEditorId(purchaseInventory.getEditorId());
      repealOrder.setExecutor(purchaseInventory.getExecutor());
      repealOrder.setExecutorId(purchaseInventory.getExecutorId());
      repealOrder.setMemo(purchaseInventory.getMemo());
      repealOrder.setNo(purchaseInventory.getNo());
      repealOrder.setOrderId(purchaseInventory.getId());
      repealOrder.setOrderTypeEnum(OrderTypes.INVENTORY);
      repealOrder.setRepealDate(System.currentTimeMillis());
      repealOrder.setShopId(purchaseInventory.getShopId());
      repealOrder.setStatus(purchaseInventory.getStatus());
      repealOrder.setStatusEnum(purchaseInventory.getStatusEnum());
      repealOrder.setVestDate(purchaseInventory.getVestDate());
    }
    return repealOrder;
  }

  public RepealOrder doRepealOrderBySalesOrder(SalesOrder salesOrder) {
    TxnWriter writer = txnDaoManager.getWriter();
    RepealOrder repealOrder = null;
    if (salesOrder != null) {
      repealOrder = new RepealOrder();
      repealOrder.setDate(salesOrder.getDate());
      repealOrder.setTotal(0 - salesOrder.getTotal());
      repealOrder.setDept(salesOrder.getDept());
      repealOrder.setDeptId(salesOrder.getDeptId() == null ? null : salesOrder.getDeptId().longValue());
      repealOrder.setEditDate(salesOrder.getEditDate());
      repealOrder.setEditor(salesOrder.getEditor());
      repealOrder.setEditorId(salesOrder.getEditorId());
      repealOrder.setExecutor(salesOrder.getExecutor());
      repealOrder.setExecutorId(salesOrder.getExecutorId());
      repealOrder.setMemo(salesOrder.getMemo());
      repealOrder.setNo(salesOrder.getNo());
      repealOrder.setOrderId(salesOrder.getId());
      repealOrder.setOrderTypeEnum(OrderTypes.SALE);
      repealOrder.setRepealDate(System.currentTimeMillis());
      repealOrder.setShopId(salesOrder.getShopId());
      repealOrder.setStatus(salesOrder.getStatus());
      repealOrder.setStatusEnum(salesOrder.getStatusEnum());
      repealOrder.setVestDate(salesOrder.getVestDate());

      Receivable receivableDTO = writer.getReceivableByShopIdAndOrderTypeAndOrderId(salesOrder.getShopId(), OrderTypes.SALE, salesOrder.getId());
      if (receivableDTO != null) {
        repealOrder.setDebt(0 - receivableDTO.getDebt());
        repealOrder.setSettledAmount(0 - receivableDTO.getSettledAmount());
      }
    }
    return repealOrder;
  }

  public RepealOrder doRepealOrderByRepairOrder(RepairOrder repairOrder) {
    TxnWriter writer = txnDaoManager.getWriter();
    RepealOrder repealOrder = null;
    if (repairOrder != null) {
      repealOrder = new RepealOrder();
      repealOrder.setDate(repairOrder.getDate());
      repealOrder.setTotal(0 - repairOrder.getTotal());
      repealOrder.setDept(repairOrder.getDept());
      repealOrder.setDeptId(repairOrder.getDeptId());
      repealOrder.setEditDate(repairOrder.getEditDate());
      repealOrder.setEditor(repairOrder.getEditor());
      repealOrder.setEditorId(repairOrder.getEditorId());
      repealOrder.setExecutor(repairOrder.getExecutor());
      repealOrder.setExecutorId(repairOrder.getExecutorId());
      repealOrder.setMemo(repairOrder.getMemo());
      repealOrder.setNo(repairOrder.getNo());
      repealOrder.setOrderId(repairOrder.getId());
      repealOrder.setOrderTypeEnum(OrderTypes.REPAIR);
      repealOrder.setRepealDate(System.currentTimeMillis());
      repealOrder.setShopId(repairOrder.getShopId());
      repealOrder.setStatus(repairOrder.getStatus());
      repealOrder.setStatusEnum(repairOrder.getStatusEnum());
      repealOrder.setVestDate(repairOrder.getVestDate());

      Receivable receivableDTO = writer.getReceivableByShopIdAndOrderTypeAndOrderId(repairOrder.getShopId(), OrderTypes.REPAIR, repairOrder.getId());
      if (receivableDTO != null) {
        repealOrder.setDebt(0 - receivableDTO.getDebt());
        repealOrder.setSettledAmount(0 - receivableDTO.getSettledAmount());
      }
    }
    return repealOrder;
  }

  public RepealOrder doRepealOrderByWashBeauty(WashBeautyOrder washBeautyOrder) {
    TxnWriter writer = txnDaoManager.getWriter();
    RepealOrder repealOrder = null;
    if (washBeautyOrder != null) {
      repealOrder = new RepealOrder();
      repealOrder.setDate(washBeautyOrder.getDate());
      repealOrder.setTotal(0 - washBeautyOrder.getTotal());
      repealOrder.setDept(washBeautyOrder.getDept());
      repealOrder.setDeptId(washBeautyOrder.getDeptId());
      repealOrder.setEditDate(washBeautyOrder.getEditDate());
      repealOrder.setEditor(washBeautyOrder.getEditor());
      repealOrder.setEditorId(washBeautyOrder.getEditorId());
      repealOrder.setExecutor(washBeautyOrder.getExecutor());
      repealOrder.setExecutorId(washBeautyOrder.getExecutorId());
      repealOrder.setMemo(washBeautyOrder.getMemo());
      repealOrder.setNo(washBeautyOrder.getNo());
      repealOrder.setOrderId(washBeautyOrder.getId());
      repealOrder.setOrderTypeEnum(OrderTypes.WASH_BEAUTY);
      repealOrder.setRepealDate(System.currentTimeMillis());
      repealOrder.setShopId(washBeautyOrder.getShopId());
      repealOrder.setStatusEnum(washBeautyOrder.getStatus());
      repealOrder.setVestDate(washBeautyOrder.getVestDate());

      Receivable receivableDTO = writer.getReceivableByShopIdAndOrderTypeAndOrderId(washBeautyOrder.getShopId(), OrderTypes.WASH_BEAUTY, washBeautyOrder.getId());
      if (receivableDTO != null) {
        repealOrder.setDebt(0 - receivableDTO.getDebt());
        repealOrder.setSettledAmount(0 - receivableDTO.getSettledAmount());
      }
    }
    return repealOrder;
  }



  public RepealOrder doRepealOrderByPurchaseReturn(PurchaseReturn purchaseReturn) {
    RepealOrder repealOrder = null;
    if (purchaseReturn != null) {
      repealOrder = new RepealOrder();
      repealOrder.setDate(purchaseReturn.getDate());
      repealOrder.setTotal(0 - purchaseReturn.getTotal());
      repealOrder.setDept(purchaseReturn.getDept());
      repealOrder.setEditDate(purchaseReturn.getEditDate());
      repealOrder.setEditor(purchaseReturn.getEditor());
      repealOrder.setEditorId(purchaseReturn.getEditorId());
      repealOrder.setExecutor(purchaseReturn.getExecutor());
      repealOrder.setExecutorId(purchaseReturn.getExecutorId());
      repealOrder.setMemo(purchaseReturn.getMemo());
      repealOrder.setNo(purchaseReturn.getNo());
      repealOrder.setOrderId(purchaseReturn.getId());
      repealOrder.setOrderTypeEnum(OrderTypes.RETURN);
      repealOrder.setRepealDate(System.currentTimeMillis());
      repealOrder.setShopId(purchaseReturn.getShopId());
      repealOrder.setStatusEnum(OrderStatus.REPEAL);
      repealOrder.setVestDate(purchaseReturn.getVestDate());
    }
    return repealOrder;
  }
  @Override
  public MemberCardOrderItem getLastMemberCardOrderItemByCustomerId(Long shopId, Long customerId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    MemberCardOrder memberCardOrder = writer.getLastMemberCardOrderByCustomerId(shopId, customerId);
    if (memberCardOrder != null) {
      MemberCardOrderItem memberCardOrderItem = writer.getLastMemberCardOrderItemByOrderId(shopId, memberCardOrder.getId());
      return memberCardOrderItem;
    }
    return null;
  }

  @Override
  public Service getServiceById(Long serviceId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if (serviceId != null) {
      Service service = writer.getById(Service.class, serviceId);
      if (service != null) {
        return service;
      }
    }
    return null;
  }

  @Override
  public Map<Long, Service> getServiceMapByIds(Long shopId, Set<Long> serviceIds) throws Exception {
    Map<Long, Service> serviceMap = new HashMap<Long, Service>();
    if(shopId == null || CollectionUtils.isEmpty(serviceIds)){
      return serviceMap;
    }
    List<Service> services = txnDaoManager.getWriter().getServiceByIds(new ArrayList<Long>(serviceIds));
    if(CollectionUtils.isNotEmpty(services)){
      for(Service service : services){
        serviceMap.put(service.getId(),service);
      }
    }
    return serviceMap;
  }

  @Override
  public Map<Long, ServiceDTO> getServiceDTOMapByIds(Long shopId, Set<Long> serviceIds) throws Exception {
    Map<Long, ServiceDTO> serviceMap = new HashMap<Long, ServiceDTO>();
    if(shopId == null || CollectionUtils.isEmpty(serviceIds)){
      return serviceMap;
    }
    List<Service> services = txnDaoManager.getWriter().getServiceByIds(new ArrayList<Long>(serviceIds));
    if(CollectionUtils.isNotEmpty(services)){
      for(Service service : services){
        serviceMap.put(service.getId(),service.toDTO());
      }
    }
    return serviceMap;
  }

  @Override
  public List<ServiceDTO> getAllServiceByShopId(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if (shopId != null) {
      List<Service> services = writer.getAllServiceByShopId(shopId);
      if (services.size() > 0) {
        List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();
        for (Service service : services) {
          ServiceDTO serviceDTO = service.toDTO();
          serviceDTOs.add(serviceDTO);
        }
        return serviceDTOs;
      }
    }
    return null;
  }

  @Override
  public ServiceDTO[] getServicesByCategory(Long shopId, String serviceName, String categoryName, CategoryType categoryType, Long pageNo, Long pageSize) throws Exception {
    if (categoryType == null) {
      return null;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<Service> services = null;
    if (StringUtils.isBlank(categoryName)) {
      services = txnWriter.getServiceByServiceName(shopId, serviceName, pageNo, pageSize);
    } else {
      services = txnWriter.getServiceByCategoryName(shopId, categoryName, categoryType, serviceName, pageNo, pageSize);
    }
    if (services != null && services.size() > 0) {
      ServiceDTO[] serviceDTOArray = new ServiceDTO[services.size()];
      for (int i = 0; i < services.size(); i++) {
        serviceDTOArray[i] = services.get(i).toDTO();
      }
      for (ServiceDTO serviceDTO : serviceDTOArray) {
        List<CategoryItemRelation> categoryItemRelations = txnWriter.getCategoryIdByServiceId(serviceDTO.getId());
        if (categoryItemRelations.size() > 0) {
          for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
            Category category = txnWriter.getById(Category.class, categoryItemRelation.getCategoryId());
            if (category!=null && categoryType.equals(category.getCategoryType())) {
              serviceDTO.setCategoryId(category.getId());
              serviceDTO.setCategoryName(category.getCategoryName());
              serviceDTO.setCategoryType(category.getCategoryType());
            }
          }
        }
      }
      return serviceDTOArray;
    }
    return null;
  }

  @Override
  public CategoryDTO[] getCategoryByShopId(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Category> categoryList = writer.getCategoryByShopId(shopId);
    if (categoryList.size() > 0) {
      CategoryDTO[] categoryDTOs = new CategoryDTO[categoryList.size()];
      for (int i = 0; i < categoryList.size(); i++) {
        categoryDTOs[i] = categoryList.get(i).toDTO();
      }
      return categoryDTOs;
    }
    return null;
  }

  @Override
  public ServiceDTO[] getServiceNoCategory(Long shopId,String serviceName, Long pageNo, Long pageSize) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<Service> services = txnWriter.getServiceNoCategory(shopId,serviceName, pageNo, pageSize);
    if (services.size() > 0) {
      ServiceDTO[] serviceDTOs = new ServiceDTO[services.size()];
      for (int i = 0; i < services.size(); i++) {
        serviceDTOs[i] = services.get(i).toDTO();
      }
      return serviceDTOs;
    }
    return null;
  }

  @Override
  public ServiceDTO[] getServiceNoPercentage(Long shopId, Long pageNo, Long pageSize) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.getServiceNoPercentage(shopId, pageNo, pageSize);
    if (services.size() > 0) {
      ServiceDTO[] serviceDTOs = new ServiceDTO[services.size()];
      for (int i = 0; i < services.size(); i++) {
        ServiceDTO serviceDTO = services.get(i).toDTO();
        List<CategoryItemRelation> categoryItemRelations = writer.getCategoryIdByServiceId(services.get(i).getId());
        if (categoryItemRelations.size() > 0) {
          for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
            Category category = writer.getById(Category.class, categoryItemRelation.getCategoryId());
            if (category != null && CategoryType.BUSINESS_CLASSIFICATION.equals(category.getCategoryType())) {
              serviceDTO.setCategoryId(category.getId());
              serviceDTO.setCategoryName(category.getCategoryName());
              serviceDTO.setCategoryType(category.getCategoryType());
            }
          }
        }
        serviceDTOs[i] = serviceDTO;
      }
      return serviceDTOs;
    }
    return null;
  }

  @Override
  public void updateServiceSingle(Long shopId,ServiceDTO serviceDTO) throws Exception {
    if(serviceDTO == null){
      return ;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Service service = new Service();
      if (serviceDTO.getId() != null) {
        service = writer.getById(Service.class, serviceDTO.getId());
      } else {
        service.setShopId(shopId);
      }
      service.updateByDTO(serviceDTO);
      service.setStatus(ServiceStatus.ENABLED);
      writer.saveOrUpdate(service);
      serviceDTO.setId(service.getId());
      //做solr
      Set<Long> serviceIdSet = new HashSet<Long>();
      serviceIdSet.add(service.getId());
      try {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(shopId,serviceIdSet);
      } catch (Exception e) {
        LOGGER.error("shopId:{}", shopId);
        LOGGER.error("serviceId:{}", StringUtil.arrayToStr(",", serviceIdSet.toArray(new Long[serviceIdSet.size()])));
        LOGGER.error("createRepairServiceSolrIndex 失败！", e);
      }

      Category category = null;
      if (StringUtils.isNotBlank(serviceDTO.getCategoryName())) {
        category = writer.getCategoryByName(shopId, serviceDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION);
      }
      List<CategoryItemRelation> categoryItemRelations = writer.getCategoryIdByServiceId(service.getId());
      boolean flag = false;
      CategoryItemRelation c = null;
      Category categoryComp = null;
      if (CollectionUtils.isNotEmpty(categoryItemRelations)) {
        for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
          categoryComp = writer.getById(Category.class, categoryItemRelation.getCategoryId());
          if (CategoryType.BUSINESS_CLASSIFICATION.equals(categoryComp.getCategoryType())) {
            flag = true;
            c = categoryItemRelation;
            break;
          }
        }
      }
      if (category == null && StringUtils.isNotBlank(serviceDTO.getCategoryName())) {
        category = new Category();
        category.setCategoryName(serviceDTO.getCategoryName());
        category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
        category.setShopId(shopId);
        writer.save(category);
      }
      if (flag && category != null) {
        if (category.getId() != categoryComp.getId()) {
          c.setCategoryId(category.getId());
          writer.update(c);
        }
      } else if (flag && category == null) {
        writer.delete(c);
      } else if (!flag && category != null) {
        c = new CategoryItemRelation();
        c.setServiceId(service.getId());
        c.setCategoryId(category.getId());
        writer.save(c);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ServiceDTO[] updateServiceCategory(Long shopId, Long categoryId, String categoryName, ServiceDTO[] serviceDTOs) throws Exception {
    if (serviceDTOs != null && serviceDTOs.length > 0) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        if (categoryName == null) {
          for (ServiceDTO serviceDTO : serviceDTOs) {
            if (serviceDTO.getCategoryId() != null && !"洗车".equals(serviceDTO.getName())) {
              CategoryItemRelation c = writer.getCategoryItemRelationByCAndSId(serviceDTO.getCategoryId(), serviceDTO.getId());
              writer.delete(c);
              serviceDTO.setCategoryType(null);
              serviceDTO.setCategoryName(null);
              serviceDTO.setCategoryId(null);
            }
          }
        } else {
          Category category = null;
          if (categoryId != null) {
            category = writer.getById(Category.class, categoryId);
            if(null != category && CategoryStatus.DISABLED.equals(category.getStatus()))
            {
              category.setStatus(CategoryStatus.ENABLED);
              writer.update(category);
            }
          } else {
            category = writer.getCategoryByName(shopId,categoryName,CategoryType.BUSINESS_CLASSIFICATION);

            if(null == category)
            {
              category = new Category();
              category.setCategoryName(categoryName);
              category.setShopId(shopId);
              category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
              writer.save(category);
            }
            else
            {
              if(CategoryStatus.DISABLED.equals(category.getStatus()))
              {
                category.setStatus(CategoryStatus.ENABLED);
                writer.update(category);
              }
            }
          }
          for (ServiceDTO serviceDTO : serviceDTOs) {
            if (!"洗车".equals(serviceDTO.getName())) {
              CategoryItemRelation c = null;
              if (serviceDTO.getCategoryId() != null) {
                c = writer.getCategoryItemRelationByCAndSId(serviceDTO.getCategoryId(), serviceDTO.getId());
              } else {
                c = new CategoryItemRelation();
                c.setServiceId(serviceDTO.getId());
              }
              c.setCategoryId(category.getId());
              writer.saveOrUpdate(c);
              serviceDTO.setCategoryType(category.getCategoryType());
              serviceDTO.setCategoryId(category.getId());
              serviceDTO.setCategoryName(category.getCategoryName());
            } else {
              serviceDTO.setCategoryType(serviceDTO.getCategoryType());
              serviceDTO.setCategoryId(serviceDTO.getId());
              serviceDTO.setCategoryName(serviceDTO.getCategoryName());
            }
          }
        }
        writer.commit(status);
        return serviceDTOs;
      } finally {
        writer.rollback(status);
      }
    }
    return null;
  }

  @Override
  public int countServiceByCategory(Long shopId, String serviceName, String categoryName, CategoryType categoryType) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if (categoryName == null || "".equals(categoryName)) {
      return writer.countServiceByServiceName(shopId, serviceName);
    } else {
      return writer.countServiceByCategory(shopId, categoryName, categoryType, serviceName);
    }
  }

  @Override
  public int countServiceNoCategory(Long shopId,String serviceNmae) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countServiceNoCategory(shopId, serviceNmae);
  }

  @Override
  public int countServiceNoPercentage(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countServiceNoPercentage(shopId);
  }

  @Override
  public ServiceDTO[] updateServicePercentage(ServiceDTO[] serviceDTOs, Double percentageAmount) throws Exception {
    if (serviceDTOs != null && serviceDTOs.length > 0) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        for (ServiceDTO serviceDTO : serviceDTOs) {
          Service service = writer.getById(Service.class, serviceDTO.getId());
          service.setPercentageAmount(percentageAmount);
          serviceDTO.setPercentageAmount(percentageAmount);
          serviceDTO.setStatus(ServiceStatus.ENABLED);
          writer.update(service);
        }
        writer.commit(status);
        return serviceDTOs;
      } finally {
        writer.rollback(status);
      }
    }
    return null;
  }

  @Override
  public ServiceDTO[] getServiceByWashBeauty(Long shopId, MemberDTO memberDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.getServiceByWashBeauty(shopId, CategoryType.BUSINESS_CLASSIFICATION);
    if (services.size() > 0) {
      ServiceDTO[] serviceDTOs = new ServiceDTO[services.size()];
      for (int i = 0; i < services.size(); i++) {
        serviceDTOs[i] = services.get(i).toDTO();
        if (memberDTO != null && memberDTO.getMemberServiceDTOs() != null) {
          for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
            if (serviceDTOs[i].getId().equals(memberServiceDTO.getServiceId())) {
              serviceDTOs[i].setSurplusTimes(memberServiceDTO.getTimesStr());
            }
          }
        }
      }
      return serviceDTOs;
    }
    return null;
  }

  @Override
  public List<MemberDTO> doServiceAndCategoryInit(List<MemberDTO> memberDTOs) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    IUserService userService = ServiceManager.getService(IUserService.class);
    Object status = writer.begin();
    try {
      Category category = writer.getCategoryByName(-1l, "美容", CategoryType.BUSINESS_CLASSIFICATION);
      if (category == null) {
        category = new Category();
        category.setShopId(-1l);
        category.setCategoryName("美容");
        category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
        writer.save(category);
      }
      category = writer.getCategoryByName(-1l, "洗车", CategoryType.BUSINESS_CLASSIFICATION);
      if (category == null) {
        category = new Category();
        category.setShopId(-1l);
        category.setCategoryName("洗车");
        category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
        writer.save(category);
      }
      List<User> users = userService.getAllShopUser();
      if (users != null) {
        for (User user : users) {
          Service service = writer.getWashService(user.getShopId());
          if (service == null) {
            service = new Service();
            service.setName("洗车");
            service.setStatus(ServiceStatus.ENABLED);
            service.setShopId(user.getShopId());
            writer.save(service);
          }
          CategoryItemRelation c = new CategoryItemRelation();
          c.setCategoryId(category.getId());
          c.setServiceId(service.getId());
          writer.save(c);
          if (memberDTOs != null) {
            for (MemberDTO memberDTO : memberDTOs) {
              if (memberDTO.getShopId().equals(service.getShopId())) {
                memberDTO.setWashServiceId(service.getId());
              }
            }
          }
        }
      }
      writer.commit(status);
      return memberDTOs;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PurchaseReturnDTO getPurchaseReturnDTOById(Long shopId, Long id) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseReturnById(shopId,id);
  }

  @Override
  public CustomerDTO doCustomerAndVehicle(Long shopId, Long userId, Long customerId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);

    CustomerDTO customerDTO = null;
    CustomerRecordDTO customerRecordDTO = null;
    if (customerId == null) {
      customerDTO = new CustomerDTO();
      customerDTO.fromWashBeauty(washBeautyOrderDTO);
      customerDTO = userService.createCustomer(customerDTO);
      washBeautyOrderDTO.setCustomerId(customerDTO.getId());
      customerRecordDTO = new CustomerRecordDTO();
      customerRecordDTO.fromWashBeauty(washBeautyOrderDTO);

      this.calculateCustomerConsume(customerRecordDTO,washBeautyOrderDTO,OrderTypes.WASH_BEAUTY,null,false);

      userService.updateCustomerRecord(customerRecordDTO);
    } else {
      customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
      customerDTO.fromWashBeauty(washBeautyOrderDTO);
      customerDTO = userService.updateCustomer(customerDTO);
      customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerId);
      if (customerRecordDTO != null) {
        customerRecordDTO.fromWashBeauty(washBeautyOrderDTO);
        this.calculateCustomerConsume(customerRecordDTO,washBeautyOrderDTO,OrderTypes.WASH_BEAUTY,null,false);
        userService.updateCustomerRecord(customerRecordDTO);
      }
    }
    washBeautyOrderDTO.setCustomerDTO(customerDTO);
    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, washBeautyOrderDTO.getLicenceNo());
    VehicleDTO vehicleDTO = null;
    if (CollectionUtils.isEmpty(vehicleDTOs)) {
      vehicleDTO = new VehicleDTO();
      vehicleDTO.fromWashBeauty(washBeautyOrderDTO);
      vehicleDTO = userService.createVehicle(vehicleDTO);
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicleDTO.getId());
      userService.addVehicleToCustomer(vehicleDTO.getId(), customerDTO.getId());
    } else {
      vehicleDTO = vehicleDTOs.get(0);
      VehicleModifyLogDTO oldLog = new VehicleModifyLogDTO();
      oldLog.setBrand(vehicleDTO.getBrand());
      oldLog.setModel(vehicleDTO.getModel());
      vehicleDTO.fromWashBeauty(washBeautyOrderDTO);
      VehicleModifyLogDTO newLog = new VehicleModifyLogDTO();
      newLog.setBrand(vehicleDTO.getBrand());
      newLog.setModel(vehicleDTO.getModel());
      List<VehicleModifyLogDTO> logDTOs = VehicleModifyLogDTO.compare(oldLog, newLog);
      for (VehicleModifyLogDTO dto : logDTOs) {
        dto.setShopId(shopId);
        dto.setUserId(userId);
        dto.setVehicleId(vehicleDTO.getId());
        dto.setOperationType(VehicleModifyOperations.REPAIR_WASH);
      }
      userService.updateVehicle(vehicleDTO);

      UserWriter userWriter = userDaoManager.getWriter();
      Object status = userWriter.begin();
      try {
        ServiceManager.getService(ICustomerService.class).batchCreateVehicleModifyLog(logDTOs);
        userWriter.commit(status);
      } finally {
        userWriter.rollback(status);
      }
    }
    customerDTO.setVehicleId(vehicleDTO.getId());
    washBeautyOrderDTO.setVechicleId(vehicleDTO.getId());
    return customerDTO;
  }



  public Service getRFServiceByServiceNameAndShopId(long shopId, String serviceName)
  {
    if(StringUtils.isBlank(serviceName))
    {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getRFServiceByServiceNameAndShopId(shopId,serviceName);
  }

  @Override
  public Service changeServiceStatus(Long shopId,Long serviceId,ServiceStatus status) throws Exception
  {
    if(null == serviceId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object objStatus = writer.begin();

    Service service = writer.getServiceById(shopId,serviceId);

    if(null == service)
    {
      return null;
    }

    try{

      service.setStatus(status);

      writer.update(service);

      writer.commit(objStatus);
      return service;

    }finally {
      writer.rollback(objStatus);
    }
  }

  /**
   * 根据退货单号和店面ID查找退货单
   * @param shopId
   * @param purchaseReturnNo 退货单号
   * @return
   * @throws Exception
   */
  public PurchaseReturnDTO getPurchaseReturnDTOByPurchaseReturnNo(Long shopId,String purchaseReturnNo) throws Exception
  {
    PurchaseReturnDTO purchaseReturnDTO = null;
    if(shopId != null && StringUtils.isNotBlank(purchaseReturnNo))
    {
      TxnWriter writer = txnDaoManager.getWriter();
      PurchaseReturn purchaseReturn =  writer.getPurchaseReturnByNo(shopId, purchaseReturnNo) ;
      if(purchaseReturn != null)
      {
        purchaseReturnDTO =  purchaseReturn.toDTO();
      }
    }
    return purchaseReturnDTO;

  }

  /**
   * 洗车单 所填车辆若为新车型，则新增此车辆，并将ID保存到此洗车单
   *
   * @param washBeautyOrderDTO
   * @return
   * @throws Exception
   * @author cfl
   */
  @Override
  public WashBeautyOrderDTO populateWashBeautyOrderDTO(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    if (washBeautyOrderDTO == null) {
      return washBeautyOrderDTO;
    }
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    if ((washBeautyOrderDTO.getBrandId() == null || washBeautyOrderDTO.getModelId() == null)
      && (StringUtils.isNotBlank(washBeautyOrderDTO.getBrand()))) {
      VehicleDTO vehicleDTO = baseProductService.addVehicleToDB(washBeautyOrderDTO.getBrand(), washBeautyOrderDTO.getModel(),
        null, null);
      washBeautyOrderDTO.setBrandId(vehicleDTO.getVirtualBrandId());
      washBeautyOrderDTO.setModelId(vehicleDTO.getVirtualModelId());
      washBeautyOrderDTO.setAddVehicleInfoToSolr(true);
    }
    return washBeautyOrderDTO;
  }

  /**
   * 施工单或销售单作废 更新收入记录
   * @param bcgogoOrderDto
   * @param orderTypes
   * @return ：单据的欠款回笼金额 和这个单据产生的新增欠款
   * @throws Exception
   */
  public RunningStatDTO updateOrderRepealReception(BcgogoOrderDto bcgogoOrderDto,OrderTypes orderTypes) throws Exception {

    RunningStatDTO runningStatDTO = new RunningStatDTO();

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);

    double debtWithdrawalIncome = 0;//单据欠款回笼金额
    double debtNewIncome = 0;//这个单据产生的新增欠款
    //如果单据类型或者单据内容为空 返回
    if (bcgogoOrderDto == null || orderTypes == null) {
      return runningStatDTO;
    }
    OrderStatus orderStatus = null;

    //施工单
    if (OrderTypes.REPAIR == orderTypes) {
      RepairOrderDTO repairOrderDTO = (RepairOrderDTO) bcgogoOrderDto;
      if (NumberUtil.longValue(repairOrderDTO.getVestDate()) <= 0) {
        return runningStatDTO;
      }
      orderStatus = repairOrderDTO.getStatus();
    }else if (OrderTypes.SALE == orderTypes) { //销售单
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) bcgogoOrderDto;
      if (NumberUtil.longValue(salesOrderDTO.getVestDate()) <= 0) {
        return runningStatDTO;
      }
      orderStatus = salesOrderDTO.getStatus();
    }else if(OrderTypes.WASH_BEAUTY == orderTypes) {
      orderStatus = OrderStatus.WASH_REPEAL;
    }else if(OrderTypes.SALE_RETURN == orderTypes){
      orderStatus = OrderStatus.REPEAL;
    }

    //判断是否是当天的流水数据
    boolean isCurrentDay = true;

    //获得该单据的收入记录
    List<ReceptionRecordDTO> receptionRecordDTOList = txnService.getReceptionRecordByOrderId(bcgogoOrderDto.getShopId(), bcgogoOrderDto.getId(),null);
    if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
      return runningStatDTO;
    }

    int recordNum = receptionRecordDTOList.get(0).getRecordNum();
    recordNum++;

    double amount = 0; //实收总和
    double memberPayTotal = 0; //会员支付总和
    double cashPayTotal = 0;  //现金支付总和
    double chequePayTotal = 0; //支票支付总和
    double bankCardPayTotal = 0;//银行卡支付总和
    double customerDepositTotal = 0 ; // 预收款支付总额 add by zhuj
    double discountTotal = 0; //折扣总和
    double remainDebt = 0.0;   //剩余欠款
    double customerDebtDiscount = 0;  //客户欠款结算所产生的欠款折扣
    double couponTotal = 0;  //代金券支付总和 add by litao

    ReceptionRecordDTO newReceptionRecord = new ReceptionRecordDTO();

    for (int index = 0; index < receptionRecordDTOList.size(); index++) {

      ReceptionRecordDTO receptionRecordDTO = receptionRecordDTOList.get(index);

      if (receptionRecordDTO.getRecordNum() != null && receptionRecordDTO.getRecordNum().intValue() == 0) {
        debtNewIncome = NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt());
      }

      if (!DateUtil.isSameDay(System.currentTimeMillis(), receptionRecordDTO.getReceptionDate())) {
        isCurrentDay = false;
      }
      if (index == 0) {
        newReceptionRecord.setReceivableId(receptionRecordDTO.getReceivableId());
        newReceptionRecord.setReceptionDate(System.currentTimeMillis());
        newReceptionRecord.setShopId(receptionRecordDTO.getShopId());
        newReceptionRecord.setOrderId(receptionRecordDTO.getOrderId());
        newReceptionRecord.setOrderTypeEnum(orderTypes);
        newReceptionRecord.setOrderStatusEnum(orderStatus);
        newReceptionRecord.setOrderTotal(0 - receptionRecordDTO.getOrderTotal());
        newReceptionRecord.setAfterMemberDiscountTotal(0 - NumberUtil.doubleVal(receptionRecordDTO.getAfterMemberDiscountTotal()));
        newReceptionRecord.setRemainDebt(0 - NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt()));
        newReceptionRecord.setPayee(receptionRecordDTO.getPayee());
        newReceptionRecord.setPayeeId(receptionRecordDTO.getPayeeId());
      }

      if (OrderTypes.DEBT == receptionRecordDTO.getOrderTypeEnum()) {
        debtWithdrawalIncome += receptionRecordDTO.getAmount();
        customerDebtDiscount += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());
      }

      amount += NumberUtil.doubleVal(receptionRecordDTO.getAmount());
      memberPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay());
      cashPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getCash());
      chequePayTotal += NumberUtil.doubleVal(receptionRecordDTO.getCheque());
      bankCardPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getBankCard());
      customerDepositTotal += NumberUtil.doubleVal(receptionRecordDTO.getDeposit()); // add by zhuj
      discountTotal += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());
      remainDebt += NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt());
      couponTotal += NumberUtil.doubleVal(receptionRecordDTO.getCoupon());  //add by litao
    }

    newReceptionRecord.setAmount(0 - amount);
    newReceptionRecord.setMemberBalancePay(0 - memberPayTotal);
    newReceptionRecord.setCash(0 - cashPayTotal);
    newReceptionRecord.setBankCard(0 - bankCardPayTotal);
    newReceptionRecord.setCheque(0 - chequePayTotal);
    newReceptionRecord.setDiscount(0 - discountTotal);
    newReceptionRecord.setRecordNum(recordNum);
    newReceptionRecord.setDeposit(0 - customerDepositTotal);
    newReceptionRecord.setCoupon(0 - couponTotal);

    receptionRecordDTOList.add(newReceptionRecord);

    for(ReceptionRecordDTO dto : receptionRecordDTOList){
      if(isCurrentDay){
        dto.setDayType(DayType.TODAY);
      }else{
        dto.setDayType(DayType.OTHER_DAY);
      }
    }

    runningStatService.saveOrUpdateReceptionRecordList(receptionRecordDTOList);

    runningStatDTO.setDebtNewIncome(debtNewIncome);
    runningStatDTO.setDebtWithdrawalIncome(debtWithdrawalIncome);
    runningStatDTO.setCustomerDebtDiscount(customerDebtDiscount);
    return runningStatDTO;
  }

  @Override
  public CategoryDTO getCategoryDTOByName(Long shopId, String name, CategoryType type) {
    TxnWriter writer = txnDaoManager.getWriter();
    CategoryDTO categoryDTO = null;
    Category category = writer.getCategoryByName( shopId,  name,  type);
    if(category != null)
    {
      categoryDTO = category.toDTO();
    }

    return categoryDTO;
  }

  @Override
  public void saveCategoryDTO(CategoryDTO categoryDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      Category category = new Category();
      category.fromDTO(categoryDTO);
      writer.save(category);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<String, CategoryDTO> getAndSaveCategoryDTOByNames(Long shopId,CategoryType type,String... categoryNames) throws Exception {
    Map<String, CategoryDTO> categoryDTOMap = new HashMap<String, CategoryDTO>();
    if (shopId == null || type == null || ArrayUtil.isEmpty(categoryNames)) {
      return categoryDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Category> categories = writer.getCategoryByNames(shopId, type, categoryNames);
      if (CollectionUtils.isNotEmpty(categories)) {
        for (Category category : categories) {
          if (CategoryStatus.DISABLED.equals(category.getStatus())) {
            category.setStatus(CategoryStatus.ENABLED);
            writer.update(category);
          }
          categoryDTOMap.put(category.getCategoryName(), category.toDTO());
        }
      }
      if (!ArrayUtil.isEmpty(categoryNames)) {
        for (String name : categoryNames) {
          CategoryDTO categoryDTO = categoryDTOMap.get(name);
          if (categoryDTO == null) {
            Category category = new Category(shopId,name,type,null);
            writer.save(category);
            categoryDTOMap.put(category.getCategoryName(), category.toDTO());
          }
        }
      }
      writer.commit(status);
      return categoryDTOMap;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ServiceDTO[] getServiceCategory(Long shopId, Long pageNo, Long pageSize) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<Service> services = txnWriter.getServiceCategory(shopId, CategoryType.BUSINESS_CLASSIFICATION, pageNo, pageSize);
    if (CollectionUtils.isNotEmpty(services)) {
      ServiceDTO[] serviceDTOs = new ServiceDTO[services.size()];
      for (int i = 0; i < services.size(); i++) {
        Service service = services.get(i);
        ServiceDTO serviceDTO = service.toDTO();

        List<CategoryItemRelation> categoryItemRelations = txnWriter.getCategoryIdByServiceId(serviceDTO.getId());
        if (categoryItemRelations.size() > 0) {
          for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
            Category category = txnWriter.getById(Category.class, categoryItemRelation.getCategoryId());
            if (category != null && CategoryType.BUSINESS_CLASSIFICATION.equals(category.getCategoryType())) {
              serviceDTO.setCategoryId(category.getId());
              serviceDTO.setCategoryName(category.getCategoryName());
              serviceDTO.setCategoryType(category.getCategoryType());
            }
          }
        }
        serviceDTOs[i] = serviceDTO;
      }
      return serviceDTOs;
    }
    return null;
  }

  @Override
  public int countServiceCategory(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countServiceCategory(shopId, CategoryType.BUSINESS_CLASSIFICATION);
  }

  @Override
  public ServiceDTO[] getServiceHasCategory(Long shopId, String serviceName,String categoryName, Long pageNo, Long pageSize) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<Service> services = txnWriter.getServiceHasCategory(shopId, serviceName,categoryName, pageNo, pageSize);
    if (CollectionUtils.isNotEmpty(services)) {
      ServiceDTO[] serviceDTOs = new ServiceDTO[services.size()];
      for (int i = 0; i < services.size(); i++) {
        Service service = services.get(i);
        ServiceDTO serviceDTO = service.toDTO();

        List<CategoryItemRelation> categoryItemRelations = txnWriter.getCategoryIdByServiceId(serviceDTO.getId());
        if (categoryItemRelations.size() > 0) {
          for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
            Category category = txnWriter.getById(Category.class, categoryItemRelation.getCategoryId());
            if (category != null && CategoryType.BUSINESS_CLASSIFICATION.equals(category.getCategoryType())) {
              serviceDTO.setCategoryId(category.getId());
              serviceDTO.setCategoryName(category.getCategoryName());
              serviceDTO.setCategoryType(category.getCategoryType());
            }
          }
        }
        serviceDTOs[i] = serviceDTO;
      }
      return serviceDTOs;
    }
    return null;
  }

  @Override
  public int countServiceHasCategory(Long shopId, String serviceName,String categoryName) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countServiceHasCategory( shopId,  serviceName, categoryName);
  }


  @Override
  public List<OrderIndexDTO> getUnsettledOrdersByProductId(Long shopId, Long productId) {
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    ShopDTO shopDTO = getConfigService().getShopById(shopId);
    OrderSearchConditionDTO condition = new OrderSearchConditionDTO();
    condition.setShopId(shopId);
    condition.setProductIds(new String[]{productId.toString()});
    String[] orderStatuses = new String[0];
    String[] orderTypes = new String[0];
    for(Map.Entry<OrderTypes, List<OrderStatus>> entry: OrderUtil.inProgressStatusMap.entrySet()){
      for(OrderStatus orderStatus:entry.getValue()){
        orderStatuses = (String[])ArrayUtils.add(orderStatuses, orderStatus.toString());
      }
      orderTypes = (String[])ArrayUtils.add(orderTypes, entry.getKey().toString());
    }
    condition.setOrderStatus(orderStatuses);
    condition.setOrderType(orderTypes);
    OrderSearchResultListDTO resultListDTO = null;
    try{
      resultListDTO = searchOrderService.queryOrderItems(condition);
    }catch(Exception e){
      LOGGER.error("RFTxnService.getUnsettledOrdersByProductId出错.", e);
    }
    Map<Long, OrderItemSearchResultDTO> orderMap = new HashMap<Long, OrderItemSearchResultDTO>();
    if(resultListDTO!=null && CollectionUtils.isNotEmpty(resultListDTO.getOrderItems())){
      for(OrderItemSearchResultDTO orderItemSearchResultDTO:resultListDTO.getOrderItems()){
        orderMap.put(orderItemSearchResultDTO.getOrderId(), orderItemSearchResultDTO);
      }
    }

    condition = new OrderSearchConditionDTO();
    condition.setSupplierProductIds(new String[]{productId.toString()});
    condition.setOrderStatus(new String[]{OrderStatus.SELLER_PENDING.toString()});
    condition.setOrderType(new String[]{OrderTypes.PURCHASE.toString()});
    condition.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
    condition.setShopKind(shopDTO.getShopKind());
    resultListDTO = null;
    try{
      resultListDTO = searchOrderService.queryOrderItems(condition);
    }catch(Exception e){
      LOGGER.error("RFTxnService.getUnsettledOrdersByProductId 2出错.", e);
    }
    if(resultListDTO!=null && CollectionUtils.isNotEmpty(resultListDTO.getOrderItems())){
      for(OrderItemSearchResultDTO orderItemSearchResultDTO:resultListDTO.getOrderItems()){
        orderMap.put(orderItemSearchResultDTO.getOrderId(), orderItemSearchResultDTO);
      }
    }

    List<OrderIndexDTO> orderIndexes = new ArrayList<OrderIndexDTO>();
    for(Map.Entry<Long, OrderItemSearchResultDTO> entry: orderMap.entrySet()){
      OrderItemSearchResultDTO dto = entry.getValue();
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
      orderIndexDTO.setOrderId(dto.getOrderId());
      orderIndexDTO.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
      orderIndexDTO.setReceiptNo(dto.getOrderReceiptNo());
      orderIndexDTO.setOrderType(OrderTypes.valueOf(dto.getOrderType()));
      orderIndexes.add(orderIndexDTO);
    }
    return orderIndexes;
  }

  @Override
  public List<OrderIndexDTO> getUnsettledOrdersByServiceId(Long shopId, Long serviceId) {
    List<OrderIndexDTO> orderIndexDTOs = new ArrayList<OrderIndexDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderDTO> repairOrderDTOs = writer.getUnsettledRepairOrderByServiceId(shopId, serviceId);
    if(CollectionUtils.isNotEmpty(repairOrderDTOs)){
      for(RepairOrderDTO repairOrderDTO : repairOrderDTOs){
        orderIndexDTOs.add(repairOrderDTO.toOrderIndexDTO());
      }
    }
    return orderIndexDTOs;
  }

  @Override
  public  Set getProductIdsFromOrder(BcgogoOrderDto bcgogoOrderDto){
    Set<Long> productIds = new HashSet<Long>();
    BcgogoOrderItemDto[] itemDTOs = bcgogoOrderDto.getItemDTOs();
    if(itemDTOs!=null){
      for(int i=0,len = itemDTOs.length;i<len;i++){
        if(itemDTOs[i].getProductId() !=null){
          productIds.add(itemDTOs[i].getProductId());
        }
      }
    }
    return productIds;
  }

  public BcgogoOrderDto getOrderDTOByOrderIdAndType(Long orderId,Long shopId,String orderType)throws Exception{
    if(orderId == null || shopId == null || StringUtils.isBlank(orderType)){
      return null;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfitxnService = ServiceManager.getService(RFITxnService.class);
    IGoodsStorageService goodsStorageService = ServiceManager.getService(IGoodsStorageService.class);
    if(orderType.equals(OrderTypes.SALE.toString())){
      return txnService.getSalesOrder(orderId,shopId);
    }else if(orderType.equals(OrderTypes.INVENTORY.toString())){
      return goodsStorageService.getSimplePurchaseInventory(orderId, shopId);
    }else if(orderType.equals(OrderTypes.REPAIR.toString())){
      return rfitxnService.getRepairOrderDTODetailById(orderId, shopId);
    }else if(orderType.equals(OrderTypes.PURCHASE.toString())){
      return txnService.getSimplePurchaseOrder(orderId, shopId);
    }else if(orderType.equals(OrderTypes.RETURN.toString())){
      return this.getPurchaseReturnDTOById(orderId);
    }else {
      return null;
    }
  }

  @Override
  public List<ProductDTO> getDeletedProductsByOrderDTOs(BcgogoOrderDto bcgogoOrderDto) {
    Set<Long> productIds = new HashSet<Long>();
    BcgogoOrderItemDto[] itemDTOs = bcgogoOrderDto.getItemDTOs();
    if (itemDTOs != null) {
      for (int i = 0, len = itemDTOs.length; i < len; i++) {
        if (itemDTOs[i] != null && itemDTOs[i].getProductId() != null) {
          productIds.add(itemDTOs[i].getProductId());
        }
      }
    }
    List<ProductDTO> returnProductDTOs = new ArrayList<ProductDTO>();
    if (!productIds.isEmpty()) {
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productIds);
      List<ProductDTO> productDTOList = new ArrayList<ProductDTO>(productDTOMap.values());
      if (CollectionUtils.isNotEmpty(productDTOList)) {
        Iterator<ProductDTO> iterator = productDTOList.iterator();
        while (iterator.hasNext()) {
          ProductDTO productDTO = iterator.next();
          if (ProductStatus.DISABLED.equals(productDTO.getStatus())) {
            returnProductDTOs.add(productDTO);
          }
        }
      }
    }
    return returnProductDTOs;
  }

  @Override
  public String getDeletedProductMsg(List<ProductDTO> productDTOs) {
    StringBuffer sb = new StringBuffer();
    if (CollectionUtils.isNotEmpty(productDTOs)) {
      for (ProductDTO productDTO : productDTOs) {
        if (productDTO != null && StringUtils.isNotBlank(productDTO.getName())) {
          sb.append("【" + productDTO.getName() + "】");
        }
      }
    }
    return sb.toString();
  }

  @Override
  public void updateDeleteProductsByOrderDTO(BcgogoOrderDto bcgogoOrderDto)throws Exception{
    if(bcgogoOrderDto == null){
      return;
    }
    List<ProductDTO> productDTOs = getDeletedProductsByOrderDTOs(bcgogoOrderDto);
    if (CollectionUtils.isNotEmpty(productDTOs)) {
      Set<Long> productSet = new HashSet<Long>();
      for(ProductDTO productDTO:productDTOs){
        if(productDTO!=null && productDTO.getProductLocalInfoId()!=null){
          productSet.add(productDTO.getProductLocalInfoId());
        }
      }
      Long[] productIds = productSet.toArray(new Long[productSet.size()]);
      getProductService().setProductStatus(bcgogoOrderDto.getShopId(), null,null, productIds);

      //构建库存上下限的缓存
      InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
      List<InventorySearchIndexDTO> inventorySearchIndexDTOs = getSearchService().getInventorySearchIndexDTOsByProductIds(bcgogoOrderDto.getShopId(), productIds);
      if(CollectionUtils.isNotEmpty(inventorySearchIndexDTOs)){
        for(InventorySearchIndexDTO inventorySearchIndexDTO:inventorySearchIndexDTOs){
          getInventoryService().caculateAfterLimit(inventorySearchIndexDTO,inventoryLimitDTO);
        }
        getInventoryService().updateMemocacheLimitByInventoryLimitDTO(bcgogoOrderDto.getShopId(), inventoryLimitDTO);
      }
    }
  }

  @Override
  public void updateProductsForInventoryCheckDTO(InventoryCheckDTO inventoryCheckDTO) throws Exception {
    if(inventoryCheckDTO == null){
      return;
    }
    InventoryCheckItemDTO[] itemDTOs = inventoryCheckDTO.getItemDTOs();
    List<Long> returnProductIds = new ArrayList<Long>();
    if (!ArrayUtils.isEmpty(itemDTOs)) {
      for (InventoryCheckItemDTO itemDTO : itemDTOs) {
        if (itemDTO == null || itemDTO.getProductId() == null) {
          continue;
        }
        ProductDTO productDTO = getProductService().getProductByProductLocalInfoId(itemDTO.getProductId(), inventoryCheckDTO.getShopId());
        if (ProductStatus.DISABLED == productDTO.getStatus()) {
          returnProductIds.add(productDTO.getProductLocalInfoId());
          ProductDTO commodityProductDTO = getProductService().getProductDTOByCommodityCode(inventoryCheckDTO.getShopId(), productDTO.getCommodityCode());
          if(commodityProductDTO != null){    //已删除的商品的商品编码已经被其他商品占用
            itemDTO.setCommodityCode(null);
          }
        }
        getProductService().updateCommodityCodeByProductLocalInfoId(inventoryCheckDTO.getShopId(), itemDTO.getProductId(), itemDTO.getCommodityCode());
      }
    }
    if(CollectionUtils.isNotEmpty(returnProductIds)){
      Long[] resultProductIds = returnProductIds.toArray(new Long[returnProductIds.size()]);
      getProductService().setProductStatus(inventoryCheckDTO.getShopId(), null, null, resultProductIds);
    }
  }

  @Override
  public Result getDeletedProductValidatorResult(BcgogoOrderDto bcgogoOrderDto) throws Exception {
    if (bcgogoOrderDto == null) {
      return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
    }
    List<ProductDTO> deletedProductDTOs = getDeletedProductsByOrderDTOs(bcgogoOrderDto);
    if (CollectionUtils.isNotEmpty(deletedProductDTOs)) {
      String resultMsg = getDeletedProductMsg(deletedProductDTOs);
      resultMsg += ValidatorConstant.DELETED_PRODUCT_SAVE_MSG;
      return new Result(resultMsg, false, Result.Operation.CONFIRM_DELETED_PRODUCT.getValue(), null);
    } else {
      return new Result(ValidatorConstant.NO_DELETED_PRODUCT_MSG, true);
    }
  }

  @Override
  public void repealWashOrderById(Long shopId, Long washBeautyOrderId) throws Exception {
    if (shopId == null || washBeautyOrderId == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      WashBeautyOrder washBeautyOrder = writer.getWashBeautyOrderDTOById(shopId, washBeautyOrderId);
      if (washBeautyOrder != null) {
        washBeautyOrder.setStatus(OrderStatus.WASH_REPEAL);
      }
      writer.update(washBeautyOrder);
      List<Debt>  debts = writer.getDebtByShopIdAndOrderId(shopId,washBeautyOrderId);
      if(CollectionUtils.isNotEmpty(debts)){
        debts.get(0).setStatusEnum(DebtStatus.REPEAL);
        writer.update(debts.get(0));
        getTxnService().cancelRemindEventByOldRemindEventId(RemindEventType.DEBT, debts.get(0).getId(), writer);
        //更新缓存
        txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, shopId);
      }
      Receivable receivable = writer.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.WASH_BEAUTY, washBeautyOrderId);
      if (receivable != null) {
        receivable.setStatusEnum(ReceivableStatus.REPEAL);
        writer.update(receivable);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public String removeDisabledAndChangedServiceInfo(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception{
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    if (washBeautyOrderDTO == null) {
      return null;
    }
    StringBuffer disabledServiceInfo = new StringBuffer();
    Map<Long, ServiceDTO> disabledServiceDTOMap = new HashMap<Long, ServiceDTO>();
    //remove disabled serviced
    if (washBeautyOrderDTO.getServiceDTOs() != null && washBeautyOrderDTO.getServiceDTOs().length > 0) {
      List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>(Arrays.asList(washBeautyOrderDTO.getServiceDTOs()));
      Iterator<ServiceDTO> iterator = serviceDTOs.iterator();
      while (iterator.hasNext()) {
        ServiceDTO serviceDTO = iterator.next();
        if (ServiceStatus.DISABLED.equals(serviceDTO.getStatus())) {
          disabledServiceDTOMap.put(serviceDTO.getId(), serviceDTO);
          disabledServiceInfo.append("【").append(serviceDTO.getName()).append("】").append(",");
          iterator.remove();
        }
      }
      if (CollectionUtils.isNotEmpty(serviceDTOs)) {
        washBeautyOrderDTO.setServiceDTOs(serviceDTOs.toArray(new ServiceDTO[serviceDTOs.size()]));
      } else {
        ServiceDTO[] newServiceDTOs = new ServiceDTO[1];
        newServiceDTOs[0] = new ServiceDTO();
        newServiceDTOs[0].setName("无服务");
        washBeautyOrderDTO.setServiceDTOs(newServiceDTOs);
      }
      if (disabledServiceInfo != null && disabledServiceInfo.length() > 1) {
        disabledServiceInfo.substring(0, disabledServiceInfo.length() - 1);
        disabledServiceInfo.append(ValidatorConstant.DISABLED_SERVICE_COPY_MSG);
      }
    }
    //remove disabled itemDto
    if (washBeautyOrderDTO.getWashBeautyOrderItemDTOs() != null && washBeautyOrderDTO.getWashBeautyOrderItemDTOs().length > 0) {
      List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = new ArrayList<WashBeautyOrderItemDTO>(Arrays.asList(washBeautyOrderDTO.getWashBeautyOrderItemDTOs()));
      Iterator<WashBeautyOrderItemDTO> iterator = washBeautyOrderItemDTOs.iterator();
      while (iterator.hasNext()) {
        WashBeautyOrderItemDTO washBeautyOrderItemDTO = iterator.next();
        if (washBeautyOrderItemDTO != null && washBeautyOrderItemDTO.getServiceId() != null){
          if(disabledServiceDTOMap.get(washBeautyOrderItemDTO.getServiceId()) != null || !serviceHistoryService.compareServiceSameWithHistory(washBeautyOrderItemDTO.getServiceId(), washBeautyOrderItemDTO.getServiceHistoryId(), washBeautyOrderDTO.getShopId())) {
            iterator.remove();
          }
        }
      }
      if (CollectionUtils.isNotEmpty(washBeautyOrderItemDTOs)) {
        washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs.toArray(new WashBeautyOrderItemDTO[washBeautyOrderItemDTOs.size()]));
      } else {
        washBeautyOrderDTO.setWashBeautyOrderItemDTOs(null);
      }
    }
    if (disabledServiceInfo != null) {
      return disabledServiceInfo.toString();
    } else {
      return null;
    }
  }

  @Override
  public void doCopyWashBeautyOrderDTO(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception{
    if (washBeautyOrderDTO == null) {
      return;
    }

    washBeautyOrderDTO.setId(null);
    washBeautyOrderDTO.setIdStr("");
    washBeautyOrderDTO.setEditDate(System.currentTimeMillis());
    washBeautyOrderDTO.setCreationDate(System.currentTimeMillis());
    washBeautyOrderDTO.setStatus(null);
    washBeautyOrderDTO.setOrderDiscount(0d);
    washBeautyOrderDTO.setCashAmount(null);
    washBeautyOrderDTO.setBankAmount(null);
    washBeautyOrderDTO.setBankCheckAmount(null);
    washBeautyOrderDTO.setBankCheckNo(null);
    washBeautyOrderDTO.setAccountMemberNo(null);
    washBeautyOrderDTO.setAccountMemberPassword(null);
    washBeautyOrderDTO.setAccountMemberPassword(null);
    washBeautyOrderDTO.setHuankuanTime(null);
    washBeautyOrderDTO.setSettledAmount(0d);
    washBeautyOrderDTO.setReceiptNo(null);
    washBeautyOrderDTO.setDebt(0d);
    washBeautyOrderDTO.setMemberAmount(null);
    washBeautyOrderDTO.setPayMethods(null);
    double total = 0d;
    if (washBeautyOrderDTO.getWashBeautyOrderItemDTOs() != null) {
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        washBeautyOrderItemDTO.setId(null);
        washBeautyOrderItemDTO.setWashBeautyOrderId(null);
        if(ConsumeType.MONEY.equals(washBeautyOrderItemDTO.getPayType())){
          total += washBeautyOrderItemDTO.getPrice();
        }

        if(null != washBeautyOrderItemDTO.getServiceId())
        {
          CategoryDTO categoryDTO = getCateGoryByServiceId(washBeautyOrderDTO.getShopId(),washBeautyOrderItemDTO.getBusinessCategoryId());
          if(null== categoryDTO)
          {
            washBeautyOrderItemDTO.setBusinessCategoryId(null);
            washBeautyOrderItemDTO.setBusinessCategoryName(null);
          }
          else
          {
            washBeautyOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
            washBeautyOrderItemDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
          }
        }
      }
    }
    washBeautyOrderDTO.setTotal(total);
    washBeautyOrderDTO.setReceiptNo(null);
  }

  @Override
  public Long[] getDebtOrReceivableErrorRepairOrderIds() throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Long> repairOrders = writer.getDebtOrReceivableErrorRepairOrder();
    if(CollectionUtils.isNotEmpty(repairOrders)){
      return repairOrders.toArray(new Long[repairOrders.size()]);
    }else{
      return new Long[0];
    }
  }

  @Override
  public void saveReceivableAndUpdateDebt(RepairOrderDTO repairOrderDTO, DebtDTO debtDTO,List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    Receivable receivable = new Receivable();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      receivable.setOrderTypeEnum(OrderTypes.REPAIR);
      if (OrderStatus.REPAIR_REPEAL.equals(repairOrderDTO.getStatus())) {
        receivable.setStatusEnum(ReceivableStatus.REPEAL);
      } else if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
        receivable.setStatusEnum(ReceivableStatus.FINISH);
      }
      receivable.setOrderId(repairOrderDTO.getId());
      receivable.setShopId(repairOrderDTO.getShopId());

      receivable.setSettledAmount(debtDTO.getSettledAmount());
      receivable.setDebt(debtDTO.getDebt());
      receivable.setTotal(repairOrderDTO.getTotal());
      receivable.setDiscount(repairOrderDTO.getTotal() - debtDTO.getSettledAmount() - debtDTO.getDebt());
      receivable.setCash(debtDTO.getSettledAmount());
      writer.save(receivable);
      Debt debt = writer.getById(Debt.class, Long.parseLong(debtDTO.getId()));
      debt.setRecievableId(receivable.getId());
      writer.update(debt);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    for(ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList){
      receptionRecordDTO.setReceivableId(receivable.getId());
    }
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    runningStatService.saveOrUpdateReceptionRecordList(receptionRecordDTOList);

  }

  @Override
  public void saveMissingDebt(RepairOrderDTO repairOrderDTO, ReceivableDTO receivableDTO,List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    if (receivableDTO.getDebt() > 0) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        Debt debt = new Debt();
        debt.setOrderTypeEnum(OrderTypes.REPAIR);
        if (OrderStatus.REPAIR_REPEAL.equals(repairOrderDTO.getStatus())) {
          debt.setStatusEnum(DebtStatus.REPEAL);
        } else if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
          if (receivableDTO.getDebt() > 0.00001) {
            debt.setStatusEnum(DebtStatus.ARREARS);
          } else {
            debt.setStatusEnum(DebtStatus.SETTLED);
          }
        }
        debt.setOrderId(repairOrderDTO.getId());
        debt.setShopId(repairOrderDTO.getShopId());

        debt.setSettledAmount(receivableDTO.getSettledAmount());
        debt.setDebt(receivableDTO.getDebt());
        debt.setTotalAmount(repairOrderDTO.getTotal());
        debt.setRecievableId(receivableDTO.getId());
        debt.setCustomerId(repairOrderDTO.getCustomerId());
        debt.setReceiptNo(repairOrderDTO.getReceiptNo());
        debt.setOrderTime(repairOrderDTO.getCreationDate());
        debt.setVehicleNumber(repairOrderDTO.getVechicle());

        StringBuffer materials = new StringBuffer();
        StringBuffer services = new StringBuffer();
        if (repairOrderDTO.getItemDTOs() != null && repairOrderDTO.getItemDTOs().length > 0) {
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
        }
        if (repairOrderDTO.getServiceDTOs() != null && repairOrderDTO.getServiceDTOs().length > 0) {
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
        }


        debt.setContent(BcgogoI18N.getMessageByKey("debt.type.mainteance"));
        debt.setService(services.toString());
        debt.setMaterial(materials.toString());
        debt.setPayTime(repairOrderDTO.getVestDate());
        debt.setRemindTime(repairOrderDTO.getVestDate() + 72 * 3600000);
        debt.setRemindStatus(UserConstant.Status.ACTIVITY);
        writer.save(debt);
        //add by WLF 保存提醒总表
        getTxnService().saveRemindEvent(writer, debt, repairOrderDTO.getCustomerName(), repairOrderDTO.getMobile());
        //add by WLF 更新缓存
        getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, debt.getShopId());
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }


    for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
      receptionRecordDTO.setReceivableId(receivableDTO.getId());
    }
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    runningStatService.saveOrUpdateReceptionRecordList(receptionRecordDTOList);
  }


  /**
   *
   * 根据施工单进行数据纠正
   * @param repairOrderDTO
   */
  public void saveReceivableAndDebtFromRepairOrder(RepairOrderDTO repairOrderDTO,List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    boolean isRepeal = (repairOrderDTO.getStatus() == OrderStatus.REPAIR_REPEAL ? true : false);

    double settledAmount = 0.0;
    double debt = 0.0;
    double discount = 0.0;

    double memberBalancePay = 0.0;
    double cash = 0.0;
    double bankCard = 0.0;
    double cheque = 0.0;
    Long memberId = null;

    ReceivableDTO newReceivableDTO = new ReceivableDTO();
    newReceivableDTO.setShopId(repairOrderDTO.getShopId());
    newReceivableDTO.setOrderType(OrderTypes.REPAIR);
    newReceivableDTO.setOrderId(repairOrderDTO.getId());
    if (isRepeal) {
      newReceivableDTO.setStatus(ReceivableStatus.REPEAL);
    }
    newReceivableDTO.setTotal(repairOrderDTO.getTotal());

    for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
      if (receptionRecordDTO.getOrderStatusEnum() != OrderStatus.REPAIR_REPEAL) {
        cash += NumberUtil.doubleVal(receptionRecordDTO.getCash());
        memberBalancePay += NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay());
        bankCard += NumberUtil.doubleVal(receptionRecordDTO.getBankCard());
        cheque += NumberUtil.doubleVal(receptionRecordDTO.getCheque());
        discount += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());
        if (NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay()) > 0) {
          memberId = receptionRecordDTO.getMemberId();
        }
      }
    }

    settledAmount = NumberUtil.toReserve(cash + memberBalancePay + bankCard + cheque, NumberUtil.MONEY_PRECISION);
    debt = NumberUtil.toReserve(newReceivableDTO.getTotal() - settledAmount - discount, NumberUtil.MONEY_PRECISION);

    newReceivableDTO.setSettledAmount(settledAmount);
    newReceivableDTO.setDebt(debt);
    newReceivableDTO.setDiscount(discount);
    newReceivableDTO.setMemberBalancePay(memberBalancePay);
    newReceivableDTO.setCash(cash);
    newReceivableDTO.setBankCard(bankCard);
    newReceivableDTO.setCheque(cheque);
    newReceivableDTO.setMemberId(memberId);

    newReceivableDTO = txnService.saveReceivableDTO(newReceivableDTO);

    if (debt > 0) {
      this.saveMissingDebt(repairOrderDTO, newReceivableDTO,receptionRecordDTOList);
    }else {
      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
        receptionRecordDTO.setReceivableId(newReceivableDTO.getId());
      }
      runningStatService.saveOrUpdateReceptionRecordList(receptionRecordDTOList);
    }

  }

  @Override
  public void updateMultipleInventoryRecommendedPrice(ProductDTO[] productDTOs, Long shopId) throws Exception{
    if (productDTOs == null && productDTOs.length == 0) {
      return;
    }
    Long[] productIds = new Long[productDTOs.length];
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>(productDTOs.length * 2, 0.75f);
    for (int i = 0, len = productDTOs.length; i < len; i++) {
      productIds[i] = productDTOs[i].getProductLocalInfoId();
      productDTOMap.put(productDTOs[i].getProductLocalInfoId(), productDTOs[i]);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Inventory> inventories = writer.getInventoryByIds(shopId, productIds);
      if (CollectionUtils.isNotEmpty(inventories)) {
        for (Inventory inventory : inventories) {
          if (productDTOMap != null && inventory != null && productDTOMap.get(inventory.getId()) != null) {
            inventory.setSalesPrice(productDTOMap.get(inventory.getId()).getRecommendedPrice());
            writer.update(inventory);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CategoryDTO saveCategory(Long shopId, String categoryName) {
    if (null == shopId || StringUtils.isBlank(categoryName)) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      CategoryDTO categoryDTO = getCategoryDTOByName(shopId, categoryName, CategoryType.BUSINESS_CLASSIFICATION);
      if (null != categoryDTO && CategoryStatus.DISABLED != categoryDTO.getStatus()) {
        return categoryDTO;
      } else if (null != categoryDTO && CategoryStatus.DISABLED == categoryDTO.getStatus()) {
        Category category = writer.getCategoryById(shopId, categoryDTO.getId());
        category.setStatus(CategoryStatus.ENABLED);
        writer.update(category);
        categoryDTO.setStatus(category.getStatus());
      } else {
        Category category = new Category();
        category.setShopId(shopId);
        category.setCategoryName(categoryName);
        category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
        writer.save(category);
        categoryDTO = category.toDTO();
      }
      writer.commit(status);
      return categoryDTO;
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public Map<String, CategoryDTO> batchSaveAndGetCateGory(Long shopId, Set<String> categoryNames) {

    if (null == shopId || CollectionUtils.isEmpty(categoryNames)) {
      return new HashMap<String, CategoryDTO>();
    }
    Map<String, CategoryDTO> categoryDTOMap = new HashMap<String, CategoryDTO>((int) (categoryNames.size() / 0.75f + 1));
    Map<String, Category> categoryMap = new HashMap<String, Category>((int) (categoryNames.size() / 0.75f + 1));
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Category> categories = writer.getCategoryByNames(shopId, CategoryType.BUSINESS_CLASSIFICATION,categoryNames.toArray(new String[categoryNames.size()]));
      if(CollectionUtils.isNotEmpty(categories)){
        for(Category category : categories){
          categoryDTOMap.put(category.getCategoryName(),category.toDTO());
          categoryMap.put(category.getCategoryName(),category);
        }
      }
      for(String categoryName :categoryNames){
        Category category = categoryMap.get(categoryName);
        if(null != category && CategoryStatus.DISABLED == category.getStatus()){
          category.setStatus(CategoryStatus.ENABLED);
          writer.update(category);
          categoryDTOMap.put(categoryName,category.toDTO());
        } else if (category == null) {
          category = new Category();
          category.setShopId(shopId);
          category.setCategoryName(categoryName);
          category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
          writer.save(category);
          categoryDTOMap.put(categoryName,category.toDTO());
          categoryMap.put(categoryName,category);
        }
      }
      writer.commit(status);
      return categoryDTOMap;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CategoryDTO updateCategory(Long shopId,Long categoryId,String name)
  {
    if(null == shopId || null==categoryId || StringUtils.isBlank(name))
    {
      return null;
    }

    Category category= getCategoryById(shopId,categoryId);

    if(null == category)
    {
      return null;
    }

    if(category.getCategoryName().equals(name))
    {
      return category.toDTO();
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      category.setCategoryName(name);
      writer.update(category);
      writer.commit(status);

      return category.toDTO();
    }finally {
      writer.rollback(status);
    }

  }

  @Override
  public Category getCategoryById(Long shopId,Long categoryId)
  {
    if(null == shopId || null== categoryId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Category category = writer.getCategoryById(shopId,categoryId);

    return category;
  }

  @Override
  public Map<Long, CategoryDTO> getCategoryDTOMapById(Long shopId, Set<Long> categoryIds) {
    Map<Long, CategoryDTO> categoryDTOMap = new HashMap<Long, CategoryDTO>();
    if (null == shopId || CollectionUtils.isEmpty(categoryIds)) {
      return categoryDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    List<Category> categories = writer.getCategoriesByIds(shopId, categoryIds);
    if(CollectionUtils.isNotEmpty(categories)){
      for(Category category : categories){
        categoryDTOMap.put(category.getId(),category.toDTO());
      }
    }
    return categoryDTOMap;
  }


  @Override
  public List<CategoryDTO> vagueGetCategoryByShopIdAndName(Long shopId,String keyWord)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    List<Category> categoryList = writer.vagueGetCategoryByShopIdAndName(shopId,keyWord);

    if(CollectionUtils.isEmpty(categoryList))
    {
      return null;
    }

    List<CategoryDTO> categoryDTOList = new ArrayList<CategoryDTO>();

    for(Category category : categoryList)
    {
      categoryDTOList.add(category.toDTO());
    }

    return categoryDTOList;
  }

  public List<CategoryDTO> getCategoryByShopIdAndName(Long shopId,String name)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    List<Category> categoryList = writer.getCategoryByShopIdAndName(shopId,name);

    if(CollectionUtils.isEmpty(categoryList))
    {
      return null;
    }

    List<CategoryDTO> categoryDTOList = new ArrayList<CategoryDTO>();

    for(Category category : categoryList)
    {
      categoryDTOList.add(category.toDTO());
    }

    return categoryDTOList;
  }

  public CategoryDTO getCateGoryByServiceId(Long shopId, Long serviceId) {
    if (null == shopId || null == serviceId) {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    CategoryItemRelation categoryItemRelation = writer.getCategoryItemRelationByServiceId(serviceId);

    if (null == categoryItemRelation) {
      return null;
    }

    Category category = getEnabledCategoryById(shopId, categoryItemRelation.getCategoryId());

    if (null == category) {
      return null;
    }

    return category.toDTO();
  }

  //key 是serviceId,value 是CategoryDTO
  @Override
  public Map<Long,CategoryDTO> getCategoryDTOMapByServiceIds(Long shopId,Set<Long> serviceIds){
    Map<Long,CategoryDTO> categoryDTOMap = new HashMap<Long, CategoryDTO>();
    if(shopId == null || CollectionUtils.isEmpty(serviceIds)){
      return categoryDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<CategoryItemRelation> categoryItemRelations = writer.getCategoryItemRelationByServiceIds(serviceIds.toArray(new Long[serviceIds.size()]));
    Set<Long> categoryIds = new HashSet<Long>();
    if(CollectionUtils.isNotEmpty(categoryItemRelations)){
      for(CategoryItemRelation categoryItemRelation : categoryItemRelations){
        if(categoryItemRelation.getCategoryId()!=null){
          categoryIds.add(categoryItemRelation.getCategoryId());
        }
      }
    }
    Map<Long, CategoryDTO> idCategoryDTOMap = getCategoryDTOMapById(shopId,categoryIds);
    if (CollectionUtils.isNotEmpty(categoryItemRelations)) {
      for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
        if (categoryItemRelation.getServiceId() != null && categoryItemRelation.getCategoryId() != null
          && serviceIds.contains(categoryItemRelation.getServiceId())) {
          CategoryDTO categoryDTO = idCategoryDTOMap.get(categoryItemRelation.getCategoryId());
          if (categoryDTO != null && !CategoryStatus.DISABLED.equals(categoryDTO.getStatus())) {
            categoryDTOMap.put(categoryItemRelation.getServiceId(), categoryDTO);
          }
        }
      }
    }
    return categoryDTOMap;
  }

  public CategoryItemRelation saveOrUpdateCategoryItemRelation(Long shopId, Long categoryId, Long serviceId) {
    if (null == serviceId) {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {
      CategoryItemRelation categoryItemRelation = writer.getCategoryItemRelationByServiceId(serviceId);

      if (null == categoryItemRelation) {
        if (null != categoryId) {
          categoryItemRelation = new CategoryItemRelation();
          categoryItemRelation.setCategoryId(categoryId);
          categoryItemRelation.setServiceId(serviceId);
          writer.save(categoryItemRelation);
        }
      } else {
        if (!categoryItemRelation.getCategoryId().equals(categoryId)) {
          if (null == categoryId) {
            writer.delete(categoryItemRelation);
          } else {
            categoryItemRelation.setCategoryId(categoryId);
            writer.update(categoryItemRelation);
          }
        }
      }

      writer.commit(status);
      return categoryItemRelation;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<Long, List<CategoryItemRelation>> getCategoryItemRelationMapByServiceIds(Long... serviceIds) {
    Map<Long, List<CategoryItemRelation>> categoryItemRelationMap = new HashMap<Long, List<CategoryItemRelation>>();
    if (ArrayUtils.isEmpty(serviceIds)) {
      return categoryItemRelationMap;
    }
    List<CategoryItemRelation> categoryItemRelations = txnDaoManager.getWriter().getCategoryItemRelationByServiceIds(serviceIds);
    if (CollectionUtils.isNotEmpty(categoryItemRelations)) {
      for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
        List<CategoryItemRelation> tempList = categoryItemRelationMap.get(categoryItemRelation.getServiceId());
        if (CollectionUtils.isNotEmpty(tempList)) {
          tempList.add(categoryItemRelation);
          categoryItemRelationMap.put(categoryItemRelation.getServiceId(), tempList);
        } else {
          tempList = new ArrayList<CategoryItemRelation>();
          tempList.add(categoryItemRelation);
          categoryItemRelationMap.put(categoryItemRelation.getServiceId(), tempList);
        }
      }
    }
    return categoryItemRelationMap;
  }

  @Override
  public Result validateWashBeautyCopy(Long washBeautyOrderId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    WashBeautyOrder washBeautyOrder = writer.getWashBeautyOrderDTOById(shopId, washBeautyOrderId);
    if(washBeautyOrder == null){
      return new Result("无法复制", "单据不存在，无法复制！", false, Result.Operation.ALERT);
    }
    WashBeautyOrderDTO washBeautyOrderDTO = washBeautyOrder.toDTO();

    CustomerDTO customerDTO = washBeautyOrderDTO.generateCustomerDTO();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    boolean customerSame = customerService.compareCustomerSameWithHistory(customerDTO, shopId);

    VehicleDTO vehicleDTO = washBeautyOrderDTO.generateVehicleDTO();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    boolean vehicleSame = vehicleService.compareVehicleSameWithHistory(vehicleDTO, shopId);

    List<WashBeautyOrderItem> items = writer.getWashBeautyOrderItemDTOByOrderId(shopId, washBeautyOrderId);
    Map<Long, Long> serviceIdAndHistoryIdMap = new HashMap<Long, Long>();
    if(CollectionUtils.isNotEmpty(items)){
      for(WashBeautyOrderItem item: items){
        serviceIdAndHistoryIdMap.put(item.getServiceId(), item.getServiceHistoryId());
      }
    }
    boolean serviceSame = serviceHistoryService.compareServiceSameWithHistory(serviceIdAndHistoryIdMap, shopId);
    if(customerSame && serviceSame && vehicleSame){
      return new Result("通过校验", true);
    }
    StringBuffer sb = new StringBuffer("<ul>");
    if(!customerSame || !vehicleSame){
      sb.append("<li>").append("客户/车辆信息").append("</li>");
    }
    if(!serviceSame){
      sb.append("<li>").append("施工名称").append("</li>");
    }
    sb.append("</ul>");
    return new Result("提示", "此单据中的以下信息已被修改，请确认是否继续复制。<br/>"+sb.toString()+"<br/>如果继续，已被修改过的信息将不会被复制。", false, Result.Operation.CONFIRM);
  }

  @Override
  public Result validatePurchaseReturnCopy(Long purchaseReturnId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    PurchaseReturnDTO purchaseReturnDTO = writer.getPurchaseReturnById(shopId, purchaseReturnId);
    if(purchaseReturnDTO == null){
      return new Result("无法复制", "单据不存在，无法复制！", false, Result.Operation.ALERT);
    }
    SupplierDTO historySupplierDTO = purchaseReturnDTO.generateSupplierDTO();
    boolean supplierSame = supplierService.compareSupplierSameWithHistory(historySupplierDTO, shopId);
    RelationChangeEnum changeEnum = RelationChangeEnum.UNCHANGED;
    if (purchaseReturnDTO.getSupplierShopId() != null) {
      changeEnum = supplierService.compareSupplierRelationChange(historySupplierDTO, shopId);
      if (RelationChangeEnum.UNRELATED_TO_RELATED.equals(changeEnum)) {
        return new Result("无法复制", "友情提示：当前单据不是在线退货，无法复制成在线退货！", false, Result.Operation.ALERT);
      } else if (RelationChangeEnum.RELATED_CHANGED.equals(changeEnum)) {
        return new Result("无法复制", "友情提示：当前在线退货的供应商关联关系发生变更，无法复制！", false, Result.Operation.ALERT);
      }
    }

    List<PurchaseReturnItem> items = writer.getPurchaseReturnItemsByReturnId(purchaseReturnId);
    Map<Long, Long> productLocalInfoIdAndHistoryIdMap = new HashMap<Long, Long>();
    if(CollectionUtils.isNotEmpty(items)){
      for(PurchaseReturnItem item: items){
        productLocalInfoIdAndHistoryIdMap.put(item.getProductId(), item.getProductHistoryId());
      }
    }
    boolean productSame = productHistoryService.compareProductSameWithHistory(productLocalInfoIdAndHistoryIdMap, shopId);
    StringBuffer  sb = new StringBuffer();
    if (RelationChangeEnum.RELATED_TO_UNRELATED.equals(changeEnum)) {
      sb.append("友情提示：该供应商关联已取消，如继续复制操作，只能复制单据数据，不能生成在线退货！<br/><br/>");
    }else {
      sb.append("友情提示：");
    }
    if (supplierSame && productSame) {
      if(RelationChangeEnum.UNCHANGED.equals(changeEnum)){
        return new Result("通过校验", true);
      }else {
        return new Result("提示", sb.toString(), false, Result.Operation.CONFIRM);
      }
    } else if (!productSame) {
      sb.append("此单据中的商品信息已被修改，请确认是否继续复制。<br/><br/>如果继续，已被修改过的商品将不会被复制。");
    } else if (!supplierSame) {
      sb.append("此单据中的供应商信息已被修改，请确认是否继续复制。<br/><br/>如果继续，供应商信息将不会被复制。");
    } else {
      sb.append("此单据中的供应商信息与商品信息已被修改，请确认是否继续复制。<br/><br/>");
      sb.append("如果继续，供应商信息与已被修改过的商品将不会被复制。");
    }
    return new Result("提示", sb.toString(), false, Result.Operation.CONFIRM);

  }

  public List<CategoryItemRelation> getCategoryItemRelation()
  {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getCategoryItemRelation();
  }

  public Category getCategory(Long categoryId)
  {
    if(null == categoryId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getById(Category.class,categoryId);
  }

  public List<WashBeautyOrderItem> getWashBeautyOrderItem()
  {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getWashBeautyOrderItem();
  }

  public List<WashBeautyOrderItem> initWashBeautyOrderCategory(List<WashBeautyOrderItem> washBeautyOrderItemList,Map<Long,Category> map)
  {
    if(CollectionUtils.isEmpty(washBeautyOrderItemList))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      for(WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItemList)
      {
        if(null == map.get(washBeautyOrderItem.getServiceId()))
        {
          continue;
        }

        Category category = map.get(washBeautyOrderItem.getServiceId());

        washBeautyOrderItem.setBusinessCategoryId(category.getId());
        washBeautyOrderItem.setBusinessCategoryName(category.getCategoryName());

        writer.update(washBeautyOrderItem);
      }

      writer.commit(status);

      return washBeautyOrderItemList;
    }finally {
      writer.rollback(status);
    }
  }

  public List<Category> initCategoryList(List<Category> categoryList)
  {
    if(CollectionUtils.isEmpty(categoryList))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    List<Category> newCategoryList = new ArrayList<Category>();

    try{
      for(Category category : categoryList)
      {
        Category category2 = getCategoryByShopIdAndNameForInit(-1L, category.getCategoryName());

        if(null !=  category2)
        {
          newCategoryList.add(category2);
          continue;
        }

        writer.save(category);
        newCategoryList.add(category);
      }
      writer.commit(status);
      return newCategoryList;
    }finally {
      writer.rollback(status);
    }
  }

  public Category getCategoryByShopIdAndNameForInit(Long shopId,String name)
  {
    if(null == shopId || StringUtils.isBlank(name))
    {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getCategoryByShopIdAndNameForInit(shopId,name);
  }

  public List<Category> getCategoryByNameNotDefault(String name)
  {
    if(StringUtils.isBlank(name))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getCategoryByNameNotDefault(name);
  }

  public void initCategoryRelationItem(Category category,List<Category> categoryList)
  {
    if(null == category || null ==category.getId() ||  CollectionUtils.isEmpty(categoryList))
    {
      return;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      for(Category category2 : categoryList)
      {
        List<CategoryItemRelation> categoryItemRelationList = getCategoryItemRelationByCategoryId(category2.getId());
        if(CollectionUtils.isEmpty(categoryItemRelationList))
        {
          category2.setCategoryName(category2.getShopId().toString()+"_"+category2.getCategoryName());
          category2.setShopId(-2L);
          writer.update(category2);
          continue;
        }

        for(CategoryItemRelation categoryItemRelation : categoryItemRelationList)
        {
          categoryItemRelation.setCategoryId(category.getId());
          writer.update(categoryItemRelation);
        }

        category2.setCategoryName(category2.getShopId().toString()+"_"+category2.getCategoryName());
        category2.setShopId(-2L);

        writer.update(category2);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }

  }

  public List<CategoryItemRelation> getCategoryItemRelationByCategoryId(Long categoryId)
  {
    if(null == categoryId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getCategoryItemRelationByCategoryId(categoryId);
  }

  public void changeCategoryStatus(Long shopId,Long categoryId,CategoryStatus status)
  {
    if(null == shopId || null == categoryId)
    {
      return;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object objStatus = writer.begin();

    try{
      Category category = writer.getCategoryById(shopId,categoryId);

      if(null != category)
      {
        category.setStatus(status);
        writer.update(category);
      }

      writer.commit(objStatus);

    }finally {
      writer.rollback(objStatus);
    }

  }

  public void deleteCategoryRelationItemByCategoryId(Long categoryId)
  {
    if(null == categoryId)
    {
      return;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{

      List<CategoryItemRelation> categoryItemRelationList = writer.getCategoryItemRelationByCategoryId(categoryId);

      if(CollectionUtils.isNotEmpty(categoryItemRelationList))
      {
        for(CategoryItemRelation categoryItemRelation : categoryItemRelationList)
        {
          writer.delete(categoryItemRelation);
        }
      }

      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Category getEnabledCategoryById(Long shopId,Long categoryId)
  {
    if(null == categoryId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getEnabledCategoryById(shopId,categoryId);
  }


  public CategoryItemRelation saveOrUpdateCategoryItemRelation(TxnWriter writer,Long shopId,Long categoryId,Long serviceId) {
    if (null == serviceId) {
      return null;
    }
    CategoryItemRelation categoryItemRelation = writer.getCategoryItemRelationByServiceId(serviceId);

    if (null == categoryItemRelation) {
      if (null != categoryId) {
        categoryItemRelation = new CategoryItemRelation();
        categoryItemRelation.setCategoryId(categoryId);
        categoryItemRelation.setServiceId(serviceId);
        writer.save(categoryItemRelation);
      }
    } else {
      if (!categoryItemRelation.getCategoryId().equals(categoryId)) {
        if (null == categoryId) {
          writer.delete(categoryItemRelation);
        } else {
          categoryItemRelation.setCategoryId(categoryId);
          writer.update(categoryItemRelation);
        }
      }
    }

    return categoryItemRelation;
  }


  @Override
  public CategoryDTO saveCategory(TxnWriter writer,Long shopId,String categoryName) {
    if (null == shopId || StringUtils.isBlank(categoryName)) {
      return null;
    }

    CategoryDTO categoryDTO = getCategoryDTOByName(shopId, categoryName, CategoryType.BUSINESS_CLASSIFICATION);

    if (null != categoryDTO && CategoryStatus.DISABLED != categoryDTO.getStatus()) {
      return categoryDTO;
    } else if (null != categoryDTO && CategoryStatus.DISABLED == categoryDTO.getStatus()) {
      Category category = writer.getCategoryById(shopId, categoryDTO.getId());
      category.setStatus(CategoryStatus.ENABLED);
      writer.update(category);
      categoryDTO.setStatus(category.getStatus());
    } else {
      Category category = new Category();
      category.setShopId(shopId);
      category.setCategoryName(categoryName);
      category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);

      writer.save(category);
      categoryDTO = category.toDTO();
    }
    return categoryDTO;

  }

  private Long[] getOrderItemProductIds(BcgogoOrderDto bcgogoOrderDto){
    if(bcgogoOrderDto == null || ArrayUtils.isEmpty(bcgogoOrderDto.getItemDTOs())){
      return new Long[0];
    }
    Set<Long> productIds = new HashSet<Long>();
    for (BcgogoOrderItemDto itemDto :bcgogoOrderDto.getItemDTOs()){
      if(itemDto.getProductId()!=null){
        productIds.add(itemDto.getProductId());
      }
    }
    if(CollectionUtils.isNotEmpty(productIds)){
      return productIds.toArray(new Long[productIds.size()]);
    }else {
      return new Long[0];
    }
  }

  @Override
  public SupplierDTO createRelationSupplier(ShopDTO customerShopDTO, ShopDTO supplierShopDTO, RelationTypes relationType) throws Exception {
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.fromSupplierShopDTO(supplierShopDTO);
    supplierDTO.setShopId(customerShopDTO.getId());
    supplierDTO.setRelationType(relationType);
    getUserService().createSupplier(supplierDTO);
    ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
    supplierDTO.getContactMobiles();
    return supplierDTO;
  }

  @Override
  public Result validateCustomerCancelSupplierShopRelation(Long shopId, SupplierDTO supplierDTO) {
    if (shopId == null || supplierDTO == null) {
      return new Result("您要取消的供应商不存在，请核实和再处理！", false);
    }
    ShopDTO supplierShopDTO = null;
    WholesalerShopRelationDTO wholesalerShopRelationDTO = null;
    if (supplierDTO.getSupplierShopId() != null) {
      supplierShopDTO = getConfigService().getShopById(supplierDTO.getSupplierShopId());
      wholesalerShopRelationDTO = getConfigService().getWholesalerShopRelationDTOByShopId(
        shopId, supplierDTO.getSupplierShopId(),RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST);
    }
    if (supplierShopDTO == null || wholesalerShopRelationDTO == null) {
      return new Result("该供应商不是您的关联供应商，请核实后再处理", false);
    }
    //校验未结算单据
    TxnWriter writer = txnDaoManager.getWriter();
    if(writer.countProcessingRelatedPurchaseOrders(shopId,supplierShopDTO.getId())>0){
      return new Result("对不起，您与该供应商还有未结算的单据，无法取消关联，请结算完成后再进行操作！", false);
    }else if(writer.countProcessingRelatedPurchaseReturnOrders(shopId,supplierShopDTO.getId())>0){
      return new Result("对不起，您与该供应商还有未结算的单据，无法取消关联，请结算完成后再进行操作！", false);
    }
    return new Result();
  }

  /**
   * 删除供应商的时候校验在线供应商相关信息
   * @param shopId
   * @param supplierDTO
   * @return
   */
  @Override
  public Result validateDeleteOnlineSupplier(Long shopId, SupplierDTO supplierDTO) {
    if (shopId == null || supplierDTO == null) {
      return new Result("您要删除的在线供应商不存在，请核实和再处理！", false);
    }
    ShopDTO supplierShopDTO = null;
    WholesalerShopRelationDTO wholesalerShopRelationDTO = null;
    if (supplierDTO.getSupplierShopId() != null) {
      supplierShopDTO = getConfigService().getShopById(supplierDTO.getSupplierShopId());
      wholesalerShopRelationDTO = getConfigService().getWholesalerShopRelationDTOByShopId(
        shopId, supplierDTO.getSupplierShopId(),RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST);
    }
       //校验未结算单据
    if (supplierShopDTO != null && supplierShopDTO.getId() != null) {
      boolean isHaveUnsettledOrder = false;
        Map<String, Object> orders = new HashMap<String, Object>();
        List<PurchaseOrderDTO> purchaseOrderDTOs = getGoodBuyService().getSimpleProcessingRelatedPurchaseOrders(shopId, supplierShopDTO.getId());
        if (CollectionUtils.isNotEmpty(purchaseOrderDTOs)) {
          isHaveUnsettledOrder = true;
          orders.put("purchaseOrder", purchaseOrderDTOs);
        }
        IPurchaseReturnService purchaseReturnService = ServiceManager.getService(IPurchaseReturnService.class);
        List<PurchaseReturnDTO> purchaseReturnDTOs = purchaseReturnService.getSimpleProcessingRelatedPurchaseReturnDTOs(shopId, supplierShopDTO.getId());
        if (CollectionUtils.isNotEmpty(purchaseReturnDTOs)) {
          isHaveUnsettledOrder = true;
          orders.put("purchaseReturn", purchaseReturnDTOs);
        }
        if(isHaveUnsettledOrder){
          return new Result("对不起，您与该供应商还有未结算的单据，无法删除，请结算完成后再进行操作！",false,orders);
        }
    }
    return new Result();
  }

  @Override
  public Result validateSupplierCancelCustomerShopRelation(Long shopId, CustomerDTO customerDTO) {
    if (shopId == null || customerDTO == null) {
      return new Result("您要取消的客户不存在，请核实和再处理！", false);
    }
    ShopDTO customerShopDTO = null;
    WholesalerShopRelationDTO wholesalerShopRelationDTO = null;
    if (customerDTO.getCustomerShopId() != null) {
      customerShopDTO = getConfigService().getShopById(customerDTO.getCustomerShopId());
      wholesalerShopRelationDTO = getConfigService().getWholesalerShopRelationDTOByShopId(
        customerDTO.getCustomerShopId(),shopId,RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_LIST);
    }
    if (customerShopDTO == null || wholesalerShopRelationDTO == null) {
      return new Result("该不是您的关联客户，请核实后再处理", false);
    }
    //校验未结算单据
    TxnWriter writer = txnDaoManager.getWriter();
    if (writer.countProcessingRelatedSalesOrders(customerDTO.getCustomerShopId(), shopId) > 0) {
      return new Result("对不起，您与该客户还有未结算的单据，无法取消关联，请结算完成后再进行操作！", false);
    } else if (writer.countProcessingRelatedSalesReturnOrders(customerDTO.getCustomerShopId(), shopId) > 0) {
      return new Result("对不起，您与该客户还有未结算的单据，无法取消关联，请结算完成后再进行操作！", false);
    }
    return new Result();
  }

  /**
   * 删除客户时校验未结算单据
   * @param shopId
   * @param customerDTO
   * @return
   */
  @Override
  public Result validateDeleteCustomerHasUnSettledOrder(Long shopId, CustomerDTO customerDTO){
    IGoodSaleService goodSaleService = ServiceManager.getService(IGoodSaleService.class);
    ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
    if (shopId == null || customerDTO == null || customerDTO.getId() == null) {
      return new Result("您要取消的客户不存在，请核实和再处理！", false);
    }
    //校验未结算单据
    boolean haveUnSettledOrder = false;

    Map<String,Object> orderMap = new HashMap<String, Object>();
   List< RepairOrderDTO > repairOrderDTOs = getTxnService().getRepairOrderReceiptNoOfNotSettled(shopId,customerDTO.getId());
    if (CollectionUtils.isNotEmpty(repairOrderDTOs)) {
      haveUnSettledOrder = true;
      orderMap.put("repair",repairOrderDTOs);
    }
    List<SalesOrderDTO> salesOrderDTOs = goodSaleService.getUnSettledSalesOrdersByCustomerId(shopId,customerDTO.getId());
    if (CollectionUtils.isNotEmpty(salesOrderDTOs)) {
      haveUnSettledOrder = true;
      orderMap.put("sale", salesOrderDTOs);
    }
    List<SalesReturnDTO> salesReturnDTOs = saleReturnOrderService.getUnsettledSalesReturnDTOsByCustomerId(shopId,customerDTO.getId());
    if (CollectionUtils.isNotEmpty(salesReturnDTOs)) {
      haveUnSettledOrder = true;
      orderMap.put("saleReturn", salesOrderDTOs);
    }
    if(haveUnSettledOrder){
      return new Result("对不起，您与该客户还有未结算的单据，无法取消关联，请结算完成后再进行操作！",false,orderMap);
    }
    return new Result();
  }


  //客户店铺取消供应商店铺关联的时候更新逻辑
  @Override
  public void cancelSupplierRelationAndReindex(Long supplierShopId, Long customerShopId)throws Exception{
    Set<Long> updatedSupplierIds = getSupplierService().cancelSupplierRelation(supplierShopId,customerShopId);
    if (CollectionUtil.isNotEmpty(updatedSupplierIds)) {
      for (Long supplierId : updatedSupplierIds) {
        getSupplierSolrWriteService().reindexSupplierBySupplierId(supplierId);
      }
    }
  }


  @Override
  public String getRandomNProductIdStr(Long shopId, int n) {
    return txnDaoManager.getWriter().getRandomNProductIdStr(shopId,n);
  }

  /**
   * 增加appUserNo到repair_order wash_beauty_order中
   *
   * //todo 写成 query.executeUpdate
   * @param appUserCustomerList
   */
  @Override
  public void addAppUserNoToRepairWashBeautyAppoint(List<AppUserCustomer> appUserCustomerList) {
    if (CollectionUtils.isEmpty(appUserCustomerList)) {
      return;
    }

    Map<Long, String> customerIdMap = new HashMap<Long, String>();
    for (AppUserCustomer appUserCustomer : appUserCustomerList) {
      customerIdMap.put(appUserCustomer.getCustomerId(), appUserCustomer.getAppUserNo());
    }

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (AppUserCustomer appUserCustomer : appUserCustomerList) {
        List<WashBeautyOrder> washBeautyOrders = writer.getWashBeautyOrderByCustomerId(appUserCustomer.getCustomerId());
        if (CollectionUtils.isNotEmpty(washBeautyOrders)) {
          for (WashBeautyOrder washBeautyOrder : washBeautyOrders) {
            if (StringUtil.isNotEmpty(washBeautyOrder.getAppUserNo())) {
              continue;
            }
            washBeautyOrder.setAppUserNo(appUserCustomer.getAppUserNo());
            writer.update(washBeautyOrder);
          }
        }

        List<RepairOrder> repairOrders = writer.getRepairOrderByCustomerId(appUserCustomer.getCustomerId());
        if (CollectionUtils.isNotEmpty(repairOrders)) {
          for (RepairOrder repairOrder : repairOrders) {
            if (StringUtil.isNotEmpty(repairOrder.getAppUserNo())) {
              continue;
            }
            repairOrder.setAppUserNo(appUserCustomer.getAppUserNo());
            writer.update(repairOrder);
          }
        }

        List<AppointOrder> appointOrders = writer.getAppointmentOrderByCustomerId(appUserCustomer.getCustomerId());
        if (CollectionUtils.isNotEmpty(appointOrders)) {
          for (AppointOrder appointOrder : appointOrders) {
            if (StringUtil.isNotEmpty(appointOrder.getAppUserNo())) {
              continue;
            }
            appointOrder.setAppUserNo(appUserCustomer.getAppUserNo());
            writer.update(appointOrder);
          }
        }
        writer.flush();
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 计算客户消费信息
   * @param bcgogoOrderDto
   * @param orderType
   * @param receivableDTO
   * @param isRepeal
   */
  public void calculateCustomerConsume(CustomerRecordDTO customerRecordDTO,BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, ReceivableDTO receivableDTO, boolean isRepeal) {
    if (customerRecordDTO == null) {
      return;
    }

    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);

    Long consumeTimes = isRepeal ? -1L : 1L;
    Long memberConsumeTime = 0L;
    Double memberConsumeTotal = 0D;

    if (receivableDTO != null && isRepeal) {
      memberConsumeTotal = -NumberUtil.doubleVal(receivableDTO.getMemberBalancePay());
      if (receivableDTO.getMemberId() != null && NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()) > 0) {
        memberConsumeTime = -1L;
      }
    } else if (OrderTypes.WASH_BEAUTY == orderType) {
      WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO) bcgogoOrderDto;

      if (memberCheckerService.containMemberAmountByWash(washBeautyOrderDTO)
          || memberCheckerService.containMemberCountConsumeByWash(washBeautyOrderDTO)
          || null != washBeautyOrderDTO.getMemberDiscountRatio()) {
        memberConsumeTime = 1L;
        memberConsumeTotal = NumberUtil.doubleVal(washBeautyOrderDTO.getMemberAmount());
      }
    } else if (OrderTypes.REPAIR == orderType) {
      RepairOrderDTO repairOrderDTO = (RepairOrderDTO) bcgogoOrderDto;

      if (memberCheckerService.containMemberAmount(repairOrderDTO)
          || memberCheckerService.containMemberCountConsume(repairOrderDTO) || null != receivableDTO.getMemberDiscountRatio()) {
        memberConsumeTime = 1L;
        memberConsumeTotal = NumberUtil.doubleVal(repairOrderDTO.getMemberAmount());
      }
    } else if (OrderTypes.SALE == orderType) {
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) bcgogoOrderDto;

      if (NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()) > 0 || null != receivableDTO.getMemberDiscountRatio()) {
        memberConsumeTime = 1L;
        memberConsumeTotal = NumberUtil.doubleVal(salesOrderDTO.getMemberAmount());
      }
    } else if (OrderTypes.SALE_RETURN == orderType) {
      SalesReturnDTO salesReturnDTO = (SalesReturnDTO) bcgogoOrderDto;
    } else if (OrderTypes.MEMBER_BUY_CARD == orderType) {
      MemberCardOrderDTO memberCardOrderDTO = (MemberCardOrderDTO) bcgogoOrderDto;
    } else if (OrderTypes.MEMBER_RETURN_CARD == orderType) {
      MemberCardReturnDTO memberCardReturnDTO = (MemberCardReturnDTO) bcgogoOrderDto;
    }

    customerRecordDTO.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()) + memberConsumeTime);
    customerRecordDTO.setMemberConsumeTotal(NumberUtil.doubleVal(customerRecordDTO.getMemberConsumeTotal()) + memberConsumeTotal);
    customerRecordDTO.setConsumeTimes(NumberUtil.longValue(customerRecordDTO.getConsumeTimes()) + consumeTimes);


  }

  public Map<Long, List<NormalProductInventoryStatDTO>> getProductTopPriceByProductIdTime(Long[] productIds, Long startTime, Long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getProductTopPriceByProductIdTime(productIds, startTime, endTime);
  }

  public Result bcgogoAppSendMsg(Long shopId,Long userId,Long remindEventId,CustomerRemindSms customerRemindSms) throws Exception{
    if (customerRemindSms != null) {
      ContactDTO contactDTO = null;
      if(StringUtils.isNotBlank(customerRemindSms.getMobile()) && StringUtils.isNotBlank(customerRemindSms.getLicenceNo())){
        VehicleDTO vehicleDTO = ServiceManager.getService(IVehicleService.class).updateVehicleMobile(shopId, customerRemindSms.getLicenceNo(), customerRemindSms.getMobile());
        if (vehicleDTO != null) {
          List<CustomerVehicleDTO> customerVehicleDTOList = ServiceManager.getService(IUserService.class).getCustomerVehicleByVehicleId(vehicleDTO.getId());
          if(CollectionUtils.isNotEmpty(customerVehicleDTOList)){
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(CollectionUtil.getFirst(customerVehicleDTOList).getCustomerId());
          }
          ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId,vehicleDTO.getId());
          contactDTO = new ContactDTO(vehicleDTO.getId());
        }
      }else if(StringUtils.isNotBlank(customerRemindSms.getLicenceNo())){
        VehicleDTO vehicleDTO = ServiceManager.getService(IVehicleService.class).getVehicleDTOByLicenceNo(shopId, customerRemindSms.getLicenceNo());
        if(StringUtils.isNotBlank(vehicleDTO.getMobile())){
          contactDTO = new ContactDTO(vehicleDTO.getId());
        }else{
          List<CustomerVehicleDTO> customerVehicleDTOList = ServiceManager.getService(IUserService.class).getCustomerVehicleByVehicleId(vehicleDTO.getId());
          if(CollectionUtils.isNotEmpty(customerVehicleDTOList)){
            Long customerId = CollectionUtil.getFirst(customerVehicleDTOList).getCustomerId();
            Map<Long,ContactDTO> mainContactDTOMap = ServiceManager.getService(IContactService.class).getMainContactDTOMapByCusIds(customerId);
            contactDTO = mainContactDTOMap.get(customerId);
          }
        }
      }else if(StringUtils.isNotBlank(customerRemindSms.getMobile())){
        contactDTO  = new ContactDTO();
        contactDTO.setMobile(customerRemindSms.getMobile());
      }


        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        if (remindEventId != null) {
          RemindEventDTO remindEventDTO = txnService.getRemindEventById(remindEventId);
          if (remindEventDTO != null) {
            if (RemindEventType.CUSTOMER_SERVICE.equals(RemindEventType.valueOf(remindEventDTO.getEventType())) || RemindEventType.MEMBER_SERVICE.equals(RemindEventType.valueOf(remindEventDTO.getEventType()))) {
              remindEventDTO.setRemindStatus(UserConstant.Status.REMINDED);
              txnService.updateRemindEvent(remindEventDTO);
            }
          }
        }

      if(contactDTO!=null){
        return ServiceManager.getService(ISendSmsService.class).sendSms(shopId,userId,customerRemindSms.getContent(),customerRemindSms.isAppFlag(),customerRemindSms.isSmsFlag(),customerRemindSms.isTemplateFlag(),contactDTO);
      }
    }
    return new Result(false);
  }

  public int countRemindMileageCustomerRemind(Long shopId){
    TxnReader txnReader = txnDaoManager.getReader();
    return txnReader.countRemindMileageCustomerRemind(shopId);
  }

  public List<RemindEventDTO> getRemindMileageCustomerRemind(Long shopId,Pager pager){
    TxnReader txnReader = txnDaoManager.getReader();
    List<RemindEvent> remindEventList = txnReader.getRemindMileageCustomerRemind(shopId,pager);
    List<RemindEventDTO> remindEventDTOs = new ArrayList<RemindEventDTO>();
    if(CollectionUtil.isEmpty(remindEventList)){
      return remindEventDTOs;
    }
    for(RemindEvent remindEvent:remindEventList){
      remindEventDTOs.add(remindEvent.toDTO());
    }
    return remindEventDTOs;

  }

}
