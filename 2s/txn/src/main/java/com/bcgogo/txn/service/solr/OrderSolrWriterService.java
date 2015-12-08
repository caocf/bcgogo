package com.bcgogo.txn.service.solr;

import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.ISolrReindexJobService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayMethod;
import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.StatementAccount.StatementAccountConstant;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 12-8-25
 * Time: 下午6:30
 * 该service 负责查找 txn 库中 各种单据
 */
@Component
public class OrderSolrWriterService implements IOrderSolrWriterService {
  private static final Logger LOG = LoggerFactory.getLogger(OrderSolrWriterService.class);
  private static final int SOLR_ORDER_MAX_SIZE = 1000;

  @Autowired
  private TxnDaoManager txnDaoManager;
  private IItemIndexService itemIndexService;
  private ITxnService txnService;
  private IOrderIndexService orderIndexService;
  private IVehicleService vehicleService;
  private IPurchaseReturnService purchaseReturnService;
  private ISupplierService supplierService;
  private IProductService productService;
  private IMembersService membersService;
  private ICustomerService customerService;
  private IUserService userService;
  private IServiceHistoryService serviceHistoryService;
  private IProductHistoryService productHistoryService;
  private IPickingService pickingService;
  private IProductThroughService productThroughService;

  public IProductThroughService getProductThroughService() {
    if (productThroughService == null) {
      productThroughService = ServiceManager.getService(IProductThroughService.class);
    }
    return productThroughService;
  }

  public IPickingService getPickingService() {
    if (pickingService == null) {
      pickingService = ServiceManager.getService(IPickingService.class);
    }
    return pickingService;
  }

  public IUserService getUserService() {
    if (userService == null) {
      userService = ServiceManager.getService(IUserService.class);
    }
    return userService;
  }
  public IMembersService getMembersService() {
    if (membersService == null) {
      membersService = ServiceManager.getService(IMembersService.class);
    }
    return membersService;
  }

  public IItemIndexService getItemIndexService() {
    if (itemIndexService == null) {
      itemIndexService = ServiceManager.getService(IItemIndexService.class);
    }
    return itemIndexService;
  }

  public ITxnService getTxnService() {
    if (txnService == null) {
      txnService = ServiceManager.getService(ITxnService.class);
    }
    return txnService;
  }

  public IOrderIndexService getOrderIndexService() {
    if (orderIndexService == null) {
      orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    }
    return orderIndexService;
  }

  public IProductService getProductService() {
    if (productService == null) {
      productService = ServiceManager.getService(IProductService.class);
    }
    return productService;
  }

  public ICustomerService getCustomerService() {
    if (customerService == null) {
      customerService = ServiceManager.getService(ICustomerService.class);
    }
    return customerService;
  }

  public IVehicleService getVehicleService() {
    if (vehicleService == null) {
      vehicleService = ServiceManager.getService(IVehicleService.class);
    }
    return vehicleService;
  }

  public IPurchaseReturnService getPurchaseReturnService() {
    if (purchaseReturnService == null) {
      purchaseReturnService = ServiceManager.getService(IPurchaseReturnService.class);
    }
    return purchaseReturnService;
  }

  public ISupplierService getSupplierService() {
    if (supplierService == null) {
      supplierService = ServiceManager.getService(ISupplierService.class);
    }
    return supplierService;
  }

  public IServiceHistoryService getServiceHistoryService() {
    if(serviceHistoryService == null){
      serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    }
    return serviceHistoryService;
  }

  public IProductHistoryService getProductHistoryService() {
    if(productHistoryService == null){
      productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    }
    return productHistoryService;
  }

  @Override
  public void optimizeSolrOrderCore() throws Exception {
    SolrClientHelper.getOrderItemSolrClient().solrOptimize();
    SolrClientHelper.getOrderSolrClient().solrOptimize();
  }

  /**
   * 根据原单据reindex
   *
   * @param shopDTO ShopDTO
   * @throws Exception
   */
  @Override
	public void reCreateOrderSolrIndexAll(ShopDTO shopDTO,OrderTypes orderType, int pageSize) throws Exception {
		try {
      fillingShopDTO(shopDTO);

			MemCacheAdapter.set(MemcachePrefix.entityOnLoadFlag.getValue(), "off");
      LOG.debug("reCreateOrderSolrIndexAll shopId:{},orderType is {}!", shopDTO.toString(),orderType);
      if(orderType!=null){
        switch (orderType) {
          case REPAIR:
            reindexRepairOrder(shopDTO, pageSize);
            break;
          case INVENTORY:
            reindexPurchaseInventoryOrder(shopDTO, pageSize);
            break;
          case PURCHASE:
            reindexPurchaseOrder(shopDTO, pageSize);
            break;
          case RETURN:
            reindexPurchaseReturnOrder(shopDTO, pageSize);
            break;
          case SALE:
            reindexSalesOrder(shopDTO, pageSize);
            break;
          case WASH_BEAUTY:
            reindexWashBeautyOrder(shopDTO, pageSize);
            break;
          case MEMBER_RETURN_CARD:
            reindexMemberReturnCardOrder(shopDTO, pageSize);
            break;
          case MEMBER_BUY_CARD:
            reindexMemberCardOrder(shopDTO, pageSize);
            break;
          case SALE_RETURN:
            reindexSalesReturnOrder(shopDTO, pageSize);
            break;
          case CUSTOMER_STATEMENT_ACCOUNT:
            reindexStatementAccountOrder(shopDTO, pageSize);
            break;
          case SUPPLIER_STATEMENT_ACCOUNT:
            reindexStatementAccountOrder(shopDTO, pageSize);
            break;
          case INVENTORY_CHECK:
            reindexInventoryCheckOrder(shopDTO, pageSize);
            break;
          case ALLOCATE_RECORD:
            reindexAllocateRecordOrder(shopDTO, pageSize);
            break;
          case INNER_PICKING:
            reindexInnerPickingOrder(shopDTO, pageSize);
            break;
          case INNER_RETURN:
            reindexInnerReturnOrder(shopDTO, pageSize);
            break;
          case BORROW_ORDER:
            reindexBorrowOrder(shopDTO, pageSize);
            break;
          case RETURN_ORDER:
            reindexReturnBorrowOrder(shopDTO, pageSize);
            break;
          case PRE_BUY_ORDER:
            reindexPreBuyOrder(shopDTO, pageSize);
            break;
          case QUOTED_PRE_BUY_ORDER:
            reindexQuotedPreBuyOrder(shopDTO, pageSize);
            break;
          default:
            LOG.error("shopId:{},orderType is not found!", shopDTO.toString());
        }
      }else{
        reindexRepairOrder(shopDTO, pageSize);
        reindexPurchaseInventoryOrder(shopDTO, pageSize);
        reindexPurchaseReturnOrder(shopDTO, pageSize);
        reindexSalesReturnOrder(shopDTO, pageSize);
        reindexPurchaseOrder(shopDTO, pageSize);
        reindexSalesOrder(shopDTO, pageSize);
        reindexMemberCardOrder(shopDTO, pageSize);
        reindexWashBeautyOrder(shopDTO, pageSize);
        reindexMemberReturnCardOrder(shopDTO, pageSize);
        reindexStatementAccountOrder(shopDTO, pageSize);

        //没有 order  出入库记录 start
        reindexInventoryCheckOrder(shopDTO, pageSize);
        reindexAllocateRecordOrder(shopDTO,pageSize);
        //维修领料  重做索引放在施工单一起了
        reindexInnerPickingOrder(shopDTO, pageSize);
        reindexInnerReturnOrder(shopDTO, pageSize);
        reindexBorrowOrder(shopDTO, pageSize);
        reindexReturnBorrowOrder(shopDTO, pageSize);
        //没有 order  出入库记录 end

        //预购
        reindexPreBuyOrder(shopDTO,pageSize);
        reindexQuotedPreBuyOrder(shopDTO,pageSize);
      }
    }catch (Exception e){
      throw new Exception(e);
		} finally {
			MemCacheAdapter.set(MemcachePrefix.entityOnLoadFlag.getValue(), "on");
		}

  }

  private void fillingShopDTO(ShopDTO shopDTO) {
    StringBuilder areaInfo = new StringBuilder();
    if(shopDTO.getProvince()!=null){
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(shopDTO.getProvince());
      if(areaDTO!=null){
        areaInfo.append(areaDTO.getName());
      }
    }
    if(shopDTO.getCity()!=null){
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(shopDTO.getCity());
      if(areaDTO!=null){
        areaInfo.append(areaDTO.getName());
      }
    }
    if(shopDTO.getRegion()!=null){
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(shopDTO.getRegion());
      if(areaDTO!=null){
        areaInfo.append(areaDTO.getName());
      }
    }
    shopDTO.setAreaName(areaInfo.toString());
  }

  /**
   * reindex order by orderId
   *
   * @param shopDTO    ShopDTO
   * @param orderType OrderTypes
   * @param orderId   Long
   */
  @Override
  public void reCreateOrderSolrIndex(ShopDTO shopDTO, OrderTypes orderType, Long... orderId) {
    try {
      if (orderId == null) throw new Exception("orderId is null");
      if (shopDTO == null) throw new Exception("shopDTO is null");
      if (orderType == null) throw new Exception("orderType is null");

      fillingShopDTO(shopDTO);

      if(ArrayUtils.isEmpty(orderId)){
        LOG.warn("OrderSolrWriterService.reCreateOrderSolrIndex, orderId为空");
        return;
      }
      if(orderId.length > SOLR_ORDER_MAX_SIZE){
        for(int i = 0; i < orderId.length; i+=SOLR_ORDER_MAX_SIZE){
          int end = i + SOLR_ORDER_MAX_SIZE;
          Long[] batchOrderIds = (Long[])ArrayUtils.subarray(orderId, i, end);
          batchCreateOrderSolrIndex(shopDTO, orderType, batchOrderIds);
        }
      }else{
        batchCreateOrderSolrIndex(shopDTO, orderType, orderId);
      }
    } catch (Exception e) {
      LOG.error("method=reCreateOrderSolrIndex , order reindex 失败！", e);
    }
  }

  private void batchCreateOrderSolrIndex(ShopDTO shopDTO, OrderTypes orderType, Long[] orderId) throws Exception {
    switch (orderType) {
      case REPAIR:
        reindexRepairOrderImplementor(shopDTO, orderId);
        break;
      case INVENTORY:
        reindexPurchaseInventoryOrderImplementor(shopDTO, orderId);
        break;
      case PURCHASE:
        reindexPurchaseOrderImplementor(shopDTO, orderId);
        break;
      case RETURN:
        reindexPurchaseReturnOrderImplementor(shopDTO, orderId);
        break;
      case SALE:
        reindexSalesOrderImplementor(shopDTO, orderId);
        break;
      case WASH_BEAUTY:
        reindexWashBeautyOrderImplementor(shopDTO, orderId);
        break;
      case MEMBER_RETURN_CARD:
        reindexMemberReturnCardOrderImplementor(shopDTO, orderId);
        break;
      case MEMBER_BUY_CARD:
        reindexMemberCardOrderImplementor(shopDTO, orderId);
        break;
      case SALE_RETURN:
        reindexSalesReturnOrderImplementor(shopDTO, orderId);
        break;
      case CUSTOMER_STATEMENT_ACCOUNT:
        reindexStatementAccountImplementor(shopDTO, orderId);
        break;
      case SUPPLIER_STATEMENT_ACCOUNT:
        reindexStatementAccountImplementor(shopDTO, orderId);
        break;
      case INVENTORY_CHECK:
        reindexInventoryCheckImplementor(shopDTO, orderId);
        break;
      case ALLOCATE_RECORD:
        reindexAllocateRecordImplementor(shopDTO, orderId);
        break;
      case REPAIR_PICKING:
        reindexRepairPickingImplementor(shopDTO, orderId);
        break;
      case INNER_PICKING:
        reindexInnerPickingImplementor(shopDTO, orderId);
        break;
      case INNER_RETURN:
        reindexInnerReturnImplementor(shopDTO, orderId);
        break;
      case BORROW_ORDER:
        reindexBorrowOrderImplementor(shopDTO, orderId);
        break;
      case RETURN_ORDER:
        reindexReturnBorrowOrderImplementor(shopDTO, orderId);
        break;
      case PRE_BUY_ORDER:
        reindexPreBuyOrderImplementor(shopDTO, orderId);
        break;
      case QUOTED_PRE_BUY_ORDER:
        reindexQuotedPreBuyOrderImplementor(shopDTO, orderId);
        break;
      default:
        LOG.error("shopId:{},orderId:{},orderType is not found!", shopDTO.toString(), orderId);
    }
  }

  //入库单 reindex
  private void reindexPurchaseInventoryOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getPurchaseInventoryIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) {
        break;
      }
      start += pageSize;
      reindexPurchaseInventoryOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexPurchaseInventoryOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    List<PurchaseInventoryDTO> purchaseInventoryDTOs = getTxnService().getPurchaseInventoryByShopIdAndOrderIds(shopId, ids);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();

    Set<Long> supplierIds = new HashSet<Long>(purchaseInventoryDTOs.size());
    for (int i = 0, len = purchaseInventoryDTOs.size(); i < len; i++) {
      if (purchaseInventoryDTOs.get(i) == null) {
        continue;
      }
      if (purchaseInventoryDTOs.get(i).getSupplierId() != null) {
        supplierIds.add(purchaseInventoryDTOs.get(i).getSupplierId());
      }
    }
    List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOs = getTxnService().getPurchaseInventoryItemByOrderIds(ids);
    Set<Long> productHistoryIds = new HashSet<Long>(purchaseInventoryItemDTOs.size());
    Map<Long, List<PurchaseInventoryItemDTO>> purchaseInventoryItemMap = new HashMap<Long, List<PurchaseInventoryItemDTO>>(purchaseInventoryDTOs.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(purchaseInventoryItemDTOs)) {
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryItemDTOs) {
        if (purchaseInventoryItemDTO != null && purchaseInventoryItemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(purchaseInventoryItemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(purchaseInventoryItemDTOs)) {
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryItemDTOs) {
        if (purchaseInventoryItemDTO == null) {
          continue;
        }
        if (purchaseInventoryItemDTO.getProductHistoryId() != null) {
          purchaseInventoryItemDTO.setProductHistoryDTO(productHistoryDTOMap.get(purchaseInventoryItemDTO.getProductHistoryId()));
        }
        if (purchaseInventoryItemDTO.getPurchaseInventoryId() != null) {
          List<PurchaseInventoryItemDTO> itemDTOList = purchaseInventoryItemMap.get(purchaseInventoryItemDTO.getPurchaseInventoryId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(purchaseInventoryItemDTO);
            purchaseInventoryItemMap.put(purchaseInventoryItemDTO.getPurchaseInventoryId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<PurchaseInventoryItemDTO>();
            itemDTOList.add(purchaseInventoryItemDTO);
            purchaseInventoryItemMap.put(purchaseInventoryItemDTO.getPurchaseInventoryId(), itemDTOList);
          }
        }
      }
    }

    Map<Long, PayableDTO> payableDTOMap = ServiceManager.getService(IInventoryService.class).getPayableDTOByPurchaseInventoryId(shopId, ids);
    PayableDTO payableDTO = null;
    for (PurchaseInventoryDTO purchaseInventoryDTO : purchaseInventoryDTOs) {
      if (purchaseInventoryDTO == null) {
        continue;
      }
      List<PurchaseInventoryItemDTO> itemDTOList = purchaseInventoryItemMap.get(purchaseInventoryDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        purchaseInventoryDTO.setItemDTOs(itemDTOList.toArray(new PurchaseInventoryItemDTO[itemDTOList.size()]));
      }
      payableDTO = payableDTOMap.get(purchaseInventoryDTO.getId());
      if (payableDTO != null) {
        purchaseInventoryDTO.setCash(payableDTO.getCash());//现金
        purchaseInventoryDTO.setBankCardAmount(payableDTO.getBankCard());//银行卡
        purchaseInventoryDTO.setCheckAmount(payableDTO.getCheque());//支票
        purchaseInventoryDTO.setDepositAmount(payableDTO.getDeposit()); //定金
        purchaseInventoryDTO.setCreditAmount(payableDTO.getCreditAmount());//欠款挂账
        purchaseInventoryDTO.setTotal(payableDTO.getAmount());
        purchaseInventoryDTO.setCreditAmount(payableDTO.getCreditAmount());
        purchaseInventoryDTO.setActuallyPaid(payableDTO.getPaidAmount());
        purchaseInventoryDTO.setDeduction(payableDTO.getDeduction());
      }
      OrderIndexDTO orderIndexDTO = purchaseInventoryDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_PAYABLE_DEBT_TYPE);
      SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseInventoryDTO.getSupplierId());
      List<Long> customerOrSupplierSreaIdList = new ArrayList<Long>();
      if(supplierDTO != null) {
        if(supplierDTO.getProvince() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getProvince());
        }
        if(supplierDTO.getCity() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getCity());
        }
        if(supplierDTO.getRegion() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getRegion());
        }

      }
      orderIndexDTO.setCustomerOrSupplierAreaIdList(customerOrSupplierSreaIdList);
      ServiceManager.getService(IOperationLogService.class).setOrderOperators(purchaseInventoryDTO.getId(),"inventory",orderIndexDTO);
      orderIndexDTOList.add(orderIndexDTO);

      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("PurchaseInventory Order[orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList()) && OrderStatus.PURCHASE_INVENTORY_DONE.equals(orderIndexDTO.getOrderStatus()) ){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);

    this.createOrderItemSolrIndex(shopDTO,itemIndexDTOList,inOutRecordDTOList,ids);
  }

  //退货单 reindex
  private void reindexPurchaseReturnOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getPurchaseReturnIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) {
        break;
      }
      start += pageSize;
      reindexPurchaseReturnOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexPurchaseReturnOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    List<PurchaseReturnDTO> purchaseReturnDTOs = getTxnService().getPurchaseReturnByShopIdAndOrderIds(shopId, ids);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    Set<Long> supplierIds = new HashSet<Long>(purchaseReturnDTOs.size());
    for (int i = 0, len = purchaseReturnDTOs.size(); i < len; i++) {
      if (purchaseReturnDTOs.get(i) == null) {
        continue;
      }
      if (purchaseReturnDTOs.get(i).getSupplierId() != null) {
        supplierIds.add(purchaseReturnDTOs.get(i).getSupplierId());
      }
    }
    List<PurchaseReturnItemDTO> purchaseReturnItemDTOs = getTxnService().getPurchaseReturnItemDTOs(ids);
    Set<Long> productHistoryIds = new HashSet<Long>(purchaseReturnItemDTOs.size());
    Map<Long, List<PurchaseReturnItemDTO>> purchaserReturnItemMap = new HashMap<Long, List<PurchaseReturnItemDTO>>(purchaseReturnItemDTOs.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(purchaseReturnItemDTOs)) {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnItemDTOs) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(purchaseReturnItemDTOs)) {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnItemDTOs) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getProductHistoryId() != null) {
          itemDTO.setProductHistoryDTO(productDTOMap.get(itemDTO.getProductHistoryId()));
        }
        if (itemDTO.getPurchaseReturnId() != null) {
          List<PurchaseReturnItemDTO> itemDTOList = purchaserReturnItemMap.get(itemDTO.getPurchaseReturnId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            purchaserReturnItemMap.put(itemDTO.getPurchaseReturnId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<PurchaseReturnItemDTO>();
            itemDTOList.add(itemDTO);
            purchaserReturnItemMap.put(itemDTO.getPurchaseReturnId(), itemDTOList);
          }
        }
      }
    }

    Map<Long, PayableDTO> payableDTOMap = ServiceManager.getService(IInventoryService.class).getPayableDTOByPurchaseInventoryId(shopId, ids);
//    Map<Long, SupplierReturnPayableDTO> supplierReturnPayableDTOMap = ServiceManager.getService(ISupplierPayableService.class).getSupplierReturnPayableByPurchaseReturnId(shopId, ids);

    for (PurchaseReturnDTO purchaseReturnDTO : purchaseReturnDTOs) {
      if (purchaseReturnDTO == null) {
        continue;
      }
      List<PurchaseReturnItemDTO> itemDTOList = purchaserReturnItemMap.get(purchaseReturnDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        purchaseReturnDTO.setItemDTOs(itemDTOList.toArray(new PurchaseReturnItemDTO[itemDTOList.size()]));
      }

      PayableDTO payableDTO = payableDTOMap.get(purchaseReturnDTO.getId());
//      SupplierReturnPayableDTO supplierReturnPayableDTO = supplierReturnPayableDTOMap.get(purchaseReturnDTO.getId());
      if (payableDTO != null) {
        purchaseReturnDTO.setDepositAmount(-NumberUtil.numberValue(payableDTO.getDeposit(),0D));
        purchaseReturnDTO.setCash(-NumberUtil.numberValue(payableDTO.getCash(),0D));
        purchaseReturnDTO.setDepositAmount(-NumberUtil.numberValue(payableDTO.getDeposit(),0D));
        purchaseReturnDTO.setStrikeAmount(-NumberUtil.numberValue(payableDTO.getStrikeAmount(),0D));
        purchaseReturnDTO.setAccountDiscount(-NumberUtil.numberValue(payableDTO.getDeduction(),0D));
        purchaseReturnDTO.setSettledAmount(-NumberUtil.numberValue(payableDTO.getPaidAmount(),0D));
        purchaseReturnDTO.setAccountDebtAmount(-NumberUtil.numberValue(payableDTO.getCreditAmount(),0D));
        purchaseReturnDTO.setBankAmount(-NumberUtil.numberValue(payableDTO.getBankCard(),0D));
        purchaseReturnDTO.setBankCheckAmount(-NumberUtil.numberValue(payableDTO.getCheque(),0D));
      } else {
        LOG.warn("purchase return order [orderId:{}] has should have supplierReturnPayable ,but supplierReturnPayable is null in order.", purchaseReturnDTO.getId());
      }
      OrderIndexDTO orderIndexDTO = purchaseReturnDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_RECEIVABLE_DEBT_TYPE);
      SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseReturnDTO.getSupplierId());
      List<Long> customerOrSupplierSreaIdList = new ArrayList<Long>();
      if(supplierDTO != null) {
        if(supplierDTO.getProvince() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getProvince());
        }
        if(supplierDTO.getCity() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getCity());
        }
        if(supplierDTO.getRegion() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getRegion());
        }

      }
      orderIndexDTO.setCustomerOrSupplierAreaIdList(customerOrSupplierSreaIdList);
      ServiceManager.getService(IOperationLogService.class).setOrderOperators(purchaseReturnDTO.getId(),"purchase_return",orderIndexDTO);
      orderIndexDTOList.add(orderIndexDTO);
      if(NumberUtil.doubleVal(purchaseReturnDTO.getAccountDebtAmount()) > 0){
        orderIndexDTO.setArrears(purchaseReturnDTO.getAccountDebtAmount());
      }

      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("purchase Order[orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList()) &&(OrderStatus.SETTLED.equals(orderIndexDTO.getOrderStatus()))){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
    createOrderItemSolrIndex(shopDTO,itemIndexDTOList,inOutRecordDTOList,ids);
  }
  //退货单 reindex
  private void reindexSalesReturnOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getSalesReturnIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) {
        break;
      }
      start += pageSize;
      reindexSalesReturnOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexSalesReturnOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<SalesReturnDTO> salesReturnDTOList = getTxnService().getSalesReturnByShopIdAndOrderIds(shopId, ids);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    Set<Long> customerIds = new HashSet<Long>(salesReturnDTOList.size());
    for (int i = 0, len = salesReturnDTOList.size(); i < len; i++) {
      if (salesReturnDTOList.get(i) == null) {
        continue;
      }
      if (salesReturnDTOList.get(i).getCustomerId() != null) {
        customerIds.add(salesReturnDTOList.get(i).getCustomerId());
      }
    }
    List<SalesReturnItemDTO> salesReturnItemDTOList = getTxnService().getSalesReturnItemDTOs(shopId,ids);
    Set<Long> productHistoryIds = new HashSet<Long>(salesReturnItemDTOList.size());
    Map<Long, List<SalesReturnItemDTO>> salesReturnItemMap = new HashMap<Long, List<SalesReturnItemDTO>>(salesReturnItemDTOList.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(salesReturnItemDTOList)) {
      for (SalesReturnItemDTO itemDTO : salesReturnItemDTOList) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(salesReturnItemDTOList)) {
      Map<Long,List<InStorageRecordDTO>> inStorageRecordDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<InStorageRecordDTO>>():getProductThroughService().getInStorageRecordDTOMapByOrderIds(shopId, ids);
      List<InStorageRecordDTO> inStorageRecordDTOList = null;
      for (SalesReturnItemDTO itemDTO : salesReturnItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        inStorageRecordDTOList = inStorageRecordDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(inStorageRecordDTOList)){
          itemDTO.setInStorageRecordDTOs(inStorageRecordDTOList.toArray(new InStorageRecordDTO[inStorageRecordDTOList.size()]));
        }
        if (itemDTO.getProductHistoryId() != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTOMap.get(itemDTO.getProductHistoryId()));
        }
        if (itemDTO.getSalesReturnId() != null) {
          List<SalesReturnItemDTO> itemDTOList = salesReturnItemMap.get(itemDTO.getSalesReturnId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            salesReturnItemMap.put(itemDTO.getSalesReturnId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<SalesReturnItemDTO>();
            itemDTOList.add(itemDTO);
            salesReturnItemMap.put(itemDTO.getSalesReturnId(), itemDTOList);
          }
        }
      }
    }

    Map<Long, ReceivableDTO> receivableDTOMap = getTxnService().getReceivableDTOByShopIdAndArrayOrderId(shopId, ids);

    for (SalesReturnDTO salesReturnDTO : salesReturnDTOList) {
      if (salesReturnDTO == null) {
        continue;
      }
      salesReturnDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      List<SalesReturnItemDTO> itemDTOList = salesReturnItemMap.get(salesReturnDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        salesReturnDTO.setItemDTOs(itemDTOList.toArray(new SalesReturnItemDTO[itemDTOList.size()]));
      }
      ReceivableDTO receivableDTO = receivableDTOMap.get(salesReturnDTO.getId());
      if (receivableDTO != null) {
        salesReturnDTO.setSettledAmount(-NumberUtil.round(receivableDTO.getSettledAmount(), NumberUtil.MONEY_PRECISION));
        salesReturnDTO.setCashAmount(-NumberUtil.round(receivableDTO.getCash(),NumberUtil.MONEY_PRECISION));
        salesReturnDTO.setBankAmount(-NumberUtil.round(receivableDTO.getBankCard(),NumberUtil.MONEY_PRECISION));
        salesReturnDTO.setBankCheckAmount(-NumberUtil.round(receivableDTO.getCheque(),NumberUtil.MONEY_PRECISION));
        salesReturnDTO.setCustomerDeposit(-NumberUtil.round(receivableDTO.getDeposit(),NumberUtil.MONEY_PRECISION)); // add by zhuj
        salesReturnDTO.setDiscountAmount(-NumberUtil.round(receivableDTO.getDiscount(),NumberUtil.MONEY_PRECISION));
        salesReturnDTO.setStrikeAmount(-NumberUtil.round(receivableDTO.getStrike(), NumberUtil.MONEY_PRECISION));
        salesReturnDTO.setAccountDebtAmount(-NumberUtil.round(receivableDTO.getDebt(),NumberUtil.MONEY_PRECISION));
      }
      OrderIndexDTO orderIndexDTO = salesReturnDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_PAYABLE_DEBT_TYPE);

      if(salesReturnDTO.getAccountDebtAmount()!=null && salesReturnDTO.getAccountDebtAmount() >0){
        orderIndexDTO.setArrears(salesReturnDTO.getAccountDebtAmount());
      }
      CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(salesReturnDTO.getCustomerId());
      List<Long> customerOrSupplierSreaIdList = new ArrayList<Long>();
      if(customerDTO != null) {
        if(customerDTO.getProvince() != null) {
          customerOrSupplierSreaIdList.add(customerDTO.getProvince());
        }
        if(customerDTO.getCity() != null) {
          customerOrSupplierSreaIdList.add(customerDTO.getCity());
        }
        if(customerDTO.getRegion() != null) {
          customerOrSupplierSreaIdList.add(customerDTO.getRegion());
        }

      }
      orderIndexDTO.setCustomerOrSupplierAreaIdList(customerOrSupplierSreaIdList);
      ServiceManager.getService(IOperationLogService.class).setOrderOperators(salesReturnDTO.getId(), "sale_return",orderIndexDTO);
      orderIndexDTOList.add(orderIndexDTO);

      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("SalesReturn Order[orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList()) && OrderStatus.SETTLED.equals(orderIndexDTO.getOrderStatus())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
    createOrderItemSolrIndex(shopDTO,itemIndexDTOList,inOutRecordDTOList,ids);
  }
  //采购单 reindex
  private void reindexPurchaseOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getPurchaseOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) {
        break;
      }
      start += pageSize;
      reindexPurchaseOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexPurchaseOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    List<PurchaseOrderDTO> purchaseOrderDTOs = getTxnService().getPurchaseOrdersByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    Set<Long> supplierIds = new HashSet<Long>(purchaseOrderDTOs.size());
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    for (int i = 0, len = purchaseOrderDTOs.size(); i < len; i++) {
      if (purchaseOrderDTOs.get(i) == null) {
        continue;
      }
      if (purchaseOrderDTOs.get(i).getSupplierId() != null) {
        supplierIds.add(purchaseOrderDTOs.get(i).getSupplierId());
      }
    }
    List<PurchaseOrderItemDTO> purchaseOrderItemDTOs = getTxnService().getPurchaseOrderItemDTOs(ids);
    Set<Long> productHistoryIds = new HashSet<Long>(purchaseOrderItemDTOs.size() );
    Set<Long> supplierProductIds = new HashSet<Long>(purchaseOrderItemDTOs.size() );

    Map<Long, List<PurchaseOrderItemDTO>> purchaseOrderItemMap = new HashMap<Long, List<PurchaseOrderItemDTO>>(purchaseOrderItemDTOs.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(purchaseOrderItemDTOs)) {
      for (PurchaseOrderItemDTO itemDTO : purchaseOrderItemDTOs) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
        if (itemDTO != null && itemDTO.getSupplierProductId() != null) {
          supplierProductIds.add(itemDTO.getSupplierProductId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
//    Map<Long, ProductHistoryDTO> supplierProductHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(purchaseOrderItemDTOs)) {
      for (PurchaseOrderItemDTO itemDTO : purchaseOrderItemDTOs) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getProductHistoryId() != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTOMap.get(itemDTO.getProductHistoryId()));
        }
        if (itemDTO.getPurchaseOrderId() != null) {
          List<PurchaseOrderItemDTO> itemDTOList = purchaseOrderItemMap.get(itemDTO.getPurchaseOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            purchaseOrderItemMap.put(itemDTO.getPurchaseOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<PurchaseOrderItemDTO>();
            itemDTOList.add(itemDTO);
            purchaseOrderItemMap.put(itemDTO.getPurchaseOrderId(), itemDTOList);
          }
        }
      }
    }

    for (PurchaseOrderDTO purchaseOrderDTO : purchaseOrderDTOs) {
      if (purchaseOrderDTO == null) {
        continue;
      }
      List<PurchaseOrderItemDTO> itemDTOList = purchaseOrderItemMap.get(purchaseOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        purchaseOrderDTO.setItemDTOs(itemDTOList.toArray(new PurchaseOrderItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = purchaseOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);

      SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseOrderDTO.getSupplierId());
      List<Long> customerOrSupplierSreaIdList = new ArrayList<Long>();
      if(supplierDTO != null) {
        if(supplierDTO.getProvince() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getProvince());
        }
        if(supplierDTO.getCity() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getCity());
        }
        if(supplierDTO.getRegion() != null) {
          customerOrSupplierSreaIdList.add(supplierDTO.getRegion());
        }

      }
      orderIndexDTO.setCustomerOrSupplierAreaIdList(customerOrSupplierSreaIdList);
      ServiceManager.getService(IOperationLogService.class).setOrderOperators(purchaseOrderDTO.getId(),"purchase",orderIndexDTO);
      orderIndexDTOList.add(orderIndexDTO);

      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("purchase Order[orderId:{}] item is null",orderIndexDTO.getOrderId());
    }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
    createOrderItemSolrIndex(shopDTO,itemIndexDTOList,null,ids);
  }

  //销售单 sale order reindex
  private void reindexSalesOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getSalesOrderDTOs(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) {
        break;
      }
      start += pageSize;
      reindexSalesOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexSalesOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<SalesOrderDTO> salesOrderDTOs = getTxnService().getSalesOrdersByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();

    Set<Long> customerIds = new HashSet<Long>(salesOrderDTOs.size());
    for (int i = 0, len = salesOrderDTOs.size(); i < len; i++) {
      if (salesOrderDTOs.get(i) == null) {
        continue;
      }
      if (salesOrderDTOs.get(i).getCustomerId() != null) {
        customerIds.add(salesOrderDTOs.get(i).getCustomerId());
      }
    }
    List<SalesOrderItemDTO> salesOrderItemDTOs = getTxnService().getSalesOrderItemDTOs(shopId,ids);
    List<SalesOrderOtherIncomeItemDTO> incomeItemDTOList = getTxnService().getSalesOrderOtherIncomeItemDTOs(ids);
    Set<Long> productHistoryIds = new HashSet<Long>((int) (salesOrderItemDTOs.size() / 0.75f) + 1, 0.75f);
    Map<Long, List<SalesOrderItemDTO>> salesOrderItemMap = new HashMap<Long, List<SalesOrderItemDTO>>((int) (salesOrderItemDTOs.size() / 0.75f) + 1, 0.75f);
    Map<Long, List<SalesOrderOtherIncomeItemDTO>> otherLongListMap = new HashMap<Long, List<SalesOrderOtherIncomeItemDTO>>((int) (incomeItemDTOList.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(salesOrderItemDTOs)) {
      for (SalesOrderItemDTO itemDTO : salesOrderItemDTOs) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(salesOrderItemDTOs)) {
      Map<Long,List<OutStorageRelationDTO>> outStorageRelationDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<OutStorageRelationDTO>>():getProductThroughService().getOutStorageRelationMap(shopId,ids);
      List<OutStorageRelationDTO> outStorageRelationDTOList = null;
      ProductHistoryDTO productHistoryDTO = null;
      for (SalesOrderItemDTO itemDTO : salesOrderItemDTOs) {
        if (itemDTO == null) {
          continue;
        }
        outStorageRelationDTOList = outStorageRelationDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(outStorageRelationDTOList)){
          itemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
        }
        productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductHistoryId());
        if (productHistoryDTO != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTO);
        }
        if (itemDTO.getSalesOrderId() != null) {
          List<SalesOrderItemDTO> itemDTOList = salesOrderItemMap.get(itemDTO.getSalesOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            salesOrderItemMap.put(itemDTO.getSalesOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<SalesOrderItemDTO>();
            itemDTOList.add(itemDTO);
            salesOrderItemMap.put(itemDTO.getSalesOrderId(), itemDTOList);
          }
        }
      }
    }

    if (CollectionUtils.isNotEmpty(incomeItemDTOList)) {
      for (SalesOrderOtherIncomeItemDTO itemDTO : incomeItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getOrderId() != null) {
          List<SalesOrderOtherIncomeItemDTO> itemDTOList = otherLongListMap.get(itemDTO.getOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            otherLongListMap.put(itemDTO.getOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<SalesOrderOtherIncomeItemDTO>();
            itemDTOList.add(itemDTO);
            otherLongListMap.put(itemDTO.getOrderId(), itemDTOList);
          }
        }
      }
    }
    Map<Long, ReceivableDTO> receivableDTOMap = getTxnService().getReceivableDTOByShopIdAndArrayOrderId(shopId, ids);

    for (SalesOrderDTO salesOrderDTO : salesOrderDTOs) {
      if (salesOrderDTO == null) {
        continue;
      }
      if (salesOrderDTO.getLicenceNo() != null) {
        VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByLicenceNo(shopId, salesOrderDTO.getLicenceNo());
        if (vehicleDTO != null) {
          salesOrderDTO.setVehicleBrand(vehicleDTO.getBrand());
          salesOrderDTO.setVehicleColor(vehicleDTO.getColor());
          salesOrderDTO.setVehicleModel(vehicleDTO.getModel());
        }
      }

      salesOrderDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      List<SalesOrderItemDTO> itemDTOList = salesOrderItemMap.get(salesOrderDTO.getId());
      salesOrderDTO.setOtherIncomeItemDTOList(otherLongListMap.get(salesOrderDTO.getId()));
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        salesOrderDTO.setItemDTOs(itemDTOList.toArray(new SalesOrderItemDTO[itemDTOList.size()]));
      }
      ReceivableDTO receivableDTO = receivableDTOMap.get(salesOrderDTO.getId());
      if (receivableDTO != null) {
        salesOrderDTO.setDebt(receivableDTO.getDebt());
        salesOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
        salesOrderDTO.setCashAmount(receivableDTO.getCash());
        salesOrderDTO.setBankAmount(receivableDTO.getBankCard());
        salesOrderDTO.setBankCheckAmount(receivableDTO.getCheque());
        //add by zhuj
        salesOrderDTO.setCustomerDeposit(receivableDTO.getDeposit());
        salesOrderDTO.setMemberAmount(receivableDTO.getMemberBalancePay());
        salesOrderDTO.setOrderDiscount(receivableDTO.getDiscount());
        salesOrderDTO.setTotal(receivableDTO.getTotal());
        salesOrderDTO.setMemberDiscountRatio(receivableDTO.getMemberDiscountRatio());
        Double afterMemberDiscountTotal = receivableDTO.getAfterMemberDiscountTotal();
        salesOrderDTO.setAfterMemberDiscountTotal(null == afterMemberDiscountTotal?salesOrderDTO.getTotal():NumberUtil.round(afterMemberDiscountTotal,NumberUtil.MONEY_PRECISION));
        //会员消费统计
        salesOrderDTO.setAccountMemberId(receivableDTO.getMemberId());
        salesOrderDTO.setAccountMemberNo(receivableDTO.getMemberNo());
      }
      OrderIndexDTO orderIndexDTO = salesOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_RECEIVABLE_DEBT_TYPE);
      orderIndexDTO.setPaymentTime(receivableDTO == null ? null : receivableDTO.getRemindTime());
      CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(salesOrderDTO.getCustomerId());
      List<Long> customerOrSupplierSreaIdList = new ArrayList<Long>();
      if(customerDTO != null) {
        if(customerDTO.getProvince() != null) {
          customerOrSupplierSreaIdList.add(customerDTO.getProvince());
        }
        if(customerDTO.getCity() != null) {
          customerOrSupplierSreaIdList.add(customerDTO.getCity());
        }
        if(customerDTO.getRegion() != null) {
          customerOrSupplierSreaIdList.add(customerDTO.getRegion());
        }
      }
      orderIndexDTO.setCustomerOrSupplierAreaIdList(customerOrSupplierSreaIdList);
      ServiceManager.getService(IOperationLogService.class).setOrderOperators(salesOrderDTO.getId(),"sale",orderIndexDTO);
      orderIndexDTOList.add(orderIndexDTO);

      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("Sales Order[orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList()) && (OrderStatus.SALE_DONE.equals(orderIndexDTO.getOrderStatus()))){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);

    this.createOrderItemSolrIndex(shopDTO,itemIndexDTOList,inOutRecordDTOList,ids);
  }


  //洗车单 WashBeauty reindex
  private void reindexWashBeautyOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      TxnWriter writer = txnDaoManager.getWriter();
      List<Long> orderIds = writer.getWashBeautyOrderIds(shopDTO.getId(), start, pageSize);   //得到将要reindex 的orderId
      if (CollectionUtils.isEmpty(orderIds)) break;
      reindexWashBeautyOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
      start += orderIds.size();
    }
  }

  private void reindexWashBeautyOrderImplementor(ShopDTO shopDTO, Long... orderIds) throws Exception {
    Long shopId = shopDTO.getId();
    List<WashBeautyOrderDTO> washBeautyOrderDTOList = getTxnService().getWashBeautyOrdersDetailByShopIdAndOrderIds(shopId, orderIds);  //得到将要reindex 的orderId
    Set<Long> customerIds = new HashSet<Long>();
    Set<Long> vehicleIds = new HashSet<Long>();
    Set<Long> serviceHistoryIds = new HashSet<Long>();
    for (WashBeautyOrderDTO beautyOrderDTO : washBeautyOrderDTOList) {
      //customerIds
      if (beautyOrderDTO.getCustomerId() != null) {
        customerIds.add(beautyOrderDTO.getCustomerId());
      }
      //vechicleIds
      if (beautyOrderDTO.getVechicleId() != null) {
        vehicleIds.add(beautyOrderDTO.getVechicleId());
      }
      //serviceIds
      if (CollectionUtils.isNotEmpty(beautyOrderDTO.getWashBeautyOrderItemDTOList())) {
        for (WashBeautyOrderItemDTO itemDTO : beautyOrderDTO.getWashBeautyOrderItemDTOList()) {
          serviceHistoryIds.add(itemDTO.getServiceHistoryId());
        }
        Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = getServiceHistoryService().getServiceHistoryByServiceHistoryIdSet(shopId, serviceHistoryIds);
        ServiceHistoryDTO serviceHistoryDTO = null;
        for (WashBeautyOrderItemDTO itemDTO : beautyOrderDTO.getWashBeautyOrderItemDTOList()) {
          serviceHistoryDTO = serviceHistoryDTOMap.get(itemDTO.getServiceHistoryId());
          if(serviceHistoryDTO!=null){
            itemDTO.setServiceName(serviceHistoryDTO.getName());
          }
        }
      }
    }
    Map<Long, ReceivableDTO> receivableDTOMap = getTxnService().getReceivableDTOByShopIdAndArrayOrderId(shopId, orderIds);

    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();

    OrderIndexDTO orderIndexDTO = null;
    for (WashBeautyOrderDTO washBeautyOrderDTO : washBeautyOrderDTOList) {
      ReceivableDTO receivableDTO = receivableDTOMap.get(washBeautyOrderDTO.getId());
      //支付方式 存入solr   //欠款
      if (receivableDTO != null) {
        washBeautyOrderDTO.setDebt(receivableDTO.getDebt());
        washBeautyOrderDTO.setCashAmount(receivableDTO.getCash());
        washBeautyOrderDTO.setBankAmount(receivableDTO.getBankCard());
        washBeautyOrderDTO.setBankCheckAmount(receivableDTO.getCheque());
        washBeautyOrderDTO.setMemberAmount(receivableDTO.getMemberBalancePay());
        washBeautyOrderDTO.setOrderDiscount(receivableDTO.getDiscount());
        washBeautyOrderDTO.setTotal(receivableDTO.getTotal());
        washBeautyOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
        //会员消费统计
        washBeautyOrderDTO.setAccountMemberId(receivableDTO.getMemberId());
        washBeautyOrderDTO.setAccountMemberNo(receivableDTO.getMemberNo());
        washBeautyOrderDTO.setMemberDiscountRatio(receivableDTO.getMemberDiscountRatio());
        Double afterMemberDiscountTotal = receivableDTO.getAfterMemberDiscountTotal();
        washBeautyOrderDTO.setAfterMemberDiscountTotal(null == afterMemberDiscountTotal?washBeautyOrderDTO.getTotal():NumberUtil.round(afterMemberDiscountTotal,NumberUtil.MONEY_PRECISION));
      }
      orderIndexDTO = washBeautyOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_RECEIVABLE_DEBT_TYPE);
      ServiceManager.getService(IOperationLogService.class).setOrderOperators(washBeautyOrderDTO.getId(),"wash",orderIndexDTO);
      orderIndexDTOList.add(orderIndexDTO);

      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("WashBeauty Order[orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);

    this.createOrderItemSolrIndex(shopDTO,itemIndexDTOList,null,orderIds);
  }

  //盘点单reindex
  private void reindexInventoryCheckOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getInventoryCheckOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) break;
      start += orderIds.size();
      reindexInventoryCheckImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

    //施工单 RepairOrder reindex
  private void reindexRepairOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getRepairOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) break;
      start += orderIds.size();
      reindexRepairOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexRepairOrderImplementor(ShopDTO shopDTO, Long... orderIds) throws Exception {
    Long shopId = shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());

    List<RepairOrderDTO> repairOrderDTOList = getTxnService().getRepairOrdersByShopIdAndOrderIds(shopId, orderIds);
    Set<Long> customerIds = new HashSet<Long>();
    Set<Long> vehicleIds = new HashSet<Long>();
    for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
      customerIds.add(repairOrderDTO.getCustomerId());
      vehicleIds.add(repairOrderDTO.getVechicleId());
    }
    List<RepairOrderItemDTO> repairOrderItemDTOList = getTxnService().getRepairOrderItemDTOsByShopIdAndArrayOrderId(shopId, orderIds);
    Map<Long,List<RepairOrderItemDTO>> repairOrderItemDTOListMap= new HashMap<Long,List<RepairOrderItemDTO>>();
    if (CollectionUtils.isNotEmpty(repairOrderItemDTOList)) {
      Set<Long> productHistoryIds = new HashSet<Long>();
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOList) {
        productHistoryIds.add(repairOrderItemDTO.getProductHistoryId());
      }
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
      Map<Long,List<OutStorageRelationDTO>> outStorageRelationDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<OutStorageRelationDTO>>(): getProductThroughService().getOutStorageRelationMap(shopId,orderIds);
      ProductHistoryDTO productHistoryDTO = null;
      List<RepairOrderItemDTO> repairOrderItemDTOs = null;
      List<OutStorageRelationDTO> outStorageRelationDTOList = null;
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOList) {
        productHistoryDTO = productHistoryDTOMap.get(repairOrderItemDTO.getProductHistoryId());
        repairOrderItemDTO.setProductHistoryDTO(productHistoryDTO);
        outStorageRelationDTOList = outStorageRelationDTOMap.get(repairOrderItemDTO.getId());
        if(CollectionUtils.isNotEmpty(outStorageRelationDTOList)){
          repairOrderItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
        }

        if (repairOrderItemDTOListMap.get(repairOrderItemDTO.getRepairOrderId()) == null) {
          repairOrderItemDTOs = new ArrayList<RepairOrderItemDTO>();
          repairOrderItemDTOs.add(repairOrderItemDTO);
          repairOrderItemDTOListMap.put(repairOrderItemDTO.getRepairOrderId(), repairOrderItemDTOs);
        } else {
          repairOrderItemDTOListMap.get(repairOrderItemDTO.getRepairOrderId()).add(repairOrderItemDTO);
        }
      }
    }
    Map<Long, List<RepairOrderServiceDTO>> repairOrderServiceDTOListMap = getTxnService().getRepairOrderServiceDTOByShopIdAndArrayOrderId(shopId, orderIds);
    Map<Long, List<RepairOrderOtherIncomeItemDTO>> repairOrderOtherIncomeItemDTOListMap = getTxnService().getRepairOrderOtherIncomeItemDTOByShopIdAndArrayOrderId(shopId, orderIds);
    Map<Long, ReceivableDTO> receivableDTOMap = getTxnService().getReceivableDTOByShopIdAndArrayOrderId(shopId, orderIds);

    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    OrderIndexDTO orderIndexDTO = null;
    List<RepairOrderServiceDTO> repairOrderServiceDTOList = null;
    List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList = null;

    Map<Long,RepairPickingDTO> repairPickingDTOMap = getPickingService().getSimpleRepairPickingDTOsByRepairOrderIds(shopId, orderIds);

    for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
      repairOrderDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      repairOrderItemDTOList = repairOrderItemDTOListMap.get(repairOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(repairOrderItemDTOList)) {
        repairOrderDTO.setItemDTOs(repairOrderItemDTOList.toArray(new RepairOrderItemDTO[repairOrderItemDTOList.size()]));
      }
      repairOrderServiceDTOList = repairOrderServiceDTOListMap.get(repairOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(repairOrderServiceDTOList)) {
        repairOrderDTO.setServiceDTOs(repairOrderServiceDTOList.toArray(new RepairOrderServiceDTO[repairOrderServiceDTOList.size()]));
      }

      otherIncomeItemDTOList = repairOrderOtherIncomeItemDTOListMap.get(repairOrderDTO.getId());

      if(CollectionUtils.isNotEmpty(otherIncomeItemDTOList)) {
        repairOrderDTO.setOtherIncomeItemDTOList(otherIncomeItemDTOList);
      }

      ReceivableDTO receivableDTO = receivableDTOMap.get(repairOrderDTO.getId());
      if(receivableDTO!=null){
        repairOrderDTO.setDebt(receivableDTO.getDebt());
        repairOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
        repairOrderDTO.setCashAmount(receivableDTO.getCash());
        repairOrderDTO.setBankAmount(receivableDTO.getBankCard());
        repairOrderDTO.setBankCheckAmount(receivableDTO.getCheque());
        repairOrderDTO.setMemberAmount(receivableDTO.getMemberBalancePay());
        repairOrderDTO.setOrderDiscount(receivableDTO.getDiscount());
        repairOrderDTO.setTotal(receivableDTO.getTotal());
        repairOrderDTO.setMemberDiscountRatio(receivableDTO.getMemberDiscountRatio());
        Double afterMemberDiscountTotal = receivableDTO.getAfterMemberDiscountTotal();
        repairOrderDTO.setAfterMemberDiscountTotal(null == afterMemberDiscountTotal?repairOrderDTO.getTotal():NumberUtil.round(afterMemberDiscountTotal,NumberUtil.MONEY_PRECISION));
        //含有会员消费 包括储值消费 或者计次划卡
        repairOrderDTO.setAccountMemberId(receivableDTO.getMemberId());
        repairOrderDTO.setAccountMemberNo(receivableDTO.getMemberNo());
      }

			orderIndexDTO = repairOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);

      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_RECEIVABLE_DEBT_TYPE);
      orderIndexDTO.setPaymentTime(receivableDTO == null ? null : receivableDTO.getRemindTime());
      orderIndexDTO.setVestDate(repairOrderDTO.getVestDate());
      orderIndexDTO.setEndDate(repairOrderDTO.getEndDate());
      orderIndexDTO.setStartDateStr(repairOrderDTO.getStartDateStr());
      orderIndexDTO.setCreationDate(repairOrderDTO.getStartDate());    //使用进厂日期作为单据开始日期
      ServiceManager.getService(IOperationLogService.class).setOrderOperators(repairOrderDTO.getId(),"repair",orderIndexDTO);
      orderIndexDTOList.add(orderIndexDTO);

      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("Repair Order[orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList()) && OrderStatus.REPAIR_SETTLED.equals(orderIndexDTO.getOrderStatus())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);

    this.createOrderItemSolrIndex(shopDTO,itemIndexDTOList,inOutRecordDTOList,orderIds);

    //维修领料 出入库记录
    List<Long> repiarPickingOrderIdList = new ArrayList<Long>();
    RepairPickingDTO repairPickingDTO = null;
    for(Long orderId:orderIds){
      repairPickingDTO = repairPickingDTOMap.get(orderId);
      if(repairPickingDTO!=null){
        repiarPickingOrderIdList.add(repairPickingDTO.getId());
      }
    }
    if(CollectionUtils.isNotEmpty(repiarPickingOrderIdList)){
      reindexRepairPickingImplementor(shopDTO,repiarPickingOrderIdList.toArray(new Long[repiarPickingOrderIdList.size()]));
    }
  }

  //会员 购卡续卡 MemberCardOrder reindex
  private void reindexMemberCardOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      TxnWriter writer = txnDaoManager.getWriter();
      List<Long> orderIdList = writer.getMemberCardOrderIds(shopDTO.getId(), start, pageSize);   //得到将要reindex 的orderId
      if (CollectionUtils.isEmpty(orderIdList)) break;
      Long[] orderIds = orderIdList.toArray(new Long[orderIdList.size()]);
      reindexMemberCardOrderImplementor(shopDTO, orderIds);
      start += orderIdList.size();
    }
  }

  private void reindexMemberCardOrderImplementor(ShopDTO shopDTO, Long... orderIds) throws Exception {
    Long shopId =shopDTO.getId();
    OrderIndexDTO orderIndexDTO = null;
    List<MemberCardOrderDTO> memberCardOrderDTOList = getTxnService().getMemberCardOrdersDetailByShopIdAndOrderIds(shopId, orderIds);
    Set<Long> customerIds = new HashSet<Long>();
    Set<Long> executorIds = new HashSet<Long>();
    Set<Long> salesManIds = new HashSet<Long>();
    for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
      //customerIds
      if (memberCardOrderDTO.getCustomerId() != null) {
        customerIds.add(memberCardOrderDTO.getCustomerId());
      } //customerIds
      if (memberCardOrderDTO.getExecutorId() != null) {
        executorIds.add(memberCardOrderDTO.getExecutorId());
      }
    }
    Set<Long> serviceHistoryIds = new HashSet<Long>();
    Set<Long> carIds = new HashSet<Long>();
    for (MemberCardOrderDTO orderDTO : memberCardOrderDTOList) {
      if (CollectionUtils.isNotEmpty(orderDTO.getNewMemberCardOrderServiceDTOs()))
        for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : orderDTO.getNewMemberCardOrderServiceDTOs()) {
          serviceHistoryIds.add(memberCardOrderServiceDTO.getServiceHistoryId());
        }
      if (CollectionUtils.isNotEmpty(orderDTO.getMemberCardOrderItemDTOs()))
        for (MemberCardOrderItemDTO itemDTO : orderDTO.getMemberCardOrderItemDTOs()) {
          carIds.add(itemDTO.getCardId());
          if (itemDTO.getSalesId() != null) salesManIds.add(itemDTO.getSalesId());
        }
    }
    //销售人
    Map<Long, SalesManDTO> saleManMap = new HashMap<Long, SalesManDTO>();
    if (CollectionUtils.isNotEmpty(salesManIds)) {
      saleManMap = getUserService().getSalesManByIdSet(shopId, salesManIds);
    }
    Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = null;
    Map<Long, MemberCardDTO> memberCardDTOMap = null;
    if (CollectionUtils.isNotEmpty(serviceHistoryIds))
      serviceHistoryDTOMap = getServiceHistoryService().getServiceHistoryByServiceHistoryIdSet(shopId, serviceHistoryIds);
    if (CollectionUtils.isNotEmpty(carIds)) {
      memberCardDTOMap = getCustomerService().getMemberCardByIds(shopId, carIds);
    }

    Map<Long,MemberDTO> memberDTOMap = getUserService().getMemberByIds(shopId,customerIds);

    Map<Long, ReceivableDTO> receivableDTOMap = getTxnService().getReceivableDTOByShopIdAndArrayOrderId(shopId, orderIds);
    Map<Long, UserDTO> executorMap = getCustomerService().getExecutorByIdSet(shopId, executorIds);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
      if (CollectionUtils.isNotEmpty(memberCardOrderDTO.getNewMemberCardOrderServiceDTOs())) {
        for (MemberCardOrderServiceDTO orderServiceDTO : memberCardOrderDTO.getNewMemberCardOrderServiceDTOs()) {
          if (orderServiceDTO == null || orderServiceDTO.getServiceId() == null) continue;
          if (serviceHistoryDTOMap != null && MapUtils.isNotEmpty(serviceHistoryDTOMap)) {
            ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOMap.get(orderServiceDTO.getServiceHistoryId());
            if (serviceHistoryDTO != null) {
              orderServiceDTO.setServiceName(serviceHistoryDTO.getName());
            }
          }
        }
      }
      ReceivableDTO receivableDTO = receivableDTOMap.get(memberCardOrderDTO.getId());
      memberCardOrderDTO.setReceivableDTO(receivableDTO);

      MemberDTO memberDTO = memberDTOMap.get(memberCardOrderDTO.getCustomerId());
      memberCardOrderDTO.setMemberDTO(memberDTO);

      orderIndexDTO = memberCardOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_RECEIVABLE_DEBT_TYPE);

      UserDTO executorDTO = executorMap.get(memberCardOrderDTO.getExecutorId());
      if (executorDTO != null) {
        Set<String> operators = new HashSet<String>();
        Set<Long> operatorIds = new HashSet<Long>();
        operators.add(executorDTO.getName());
        operatorIds.add(executorDTO.getId());
        orderIndexDTO.setOperators(operators);
        orderIndexDTO.setOperatorIds(operatorIds);
      }
      //销售人
      if (CollectionUtils.isNotEmpty(memberCardOrderDTO.getMemberCardOrderItemDTOs())) {
        StringBuffer saleMans = new StringBuffer();
        for (MemberCardOrderItemDTO memberCardOrderItemDTO : memberCardOrderDTO.getMemberCardOrderItemDTOs()) {
          SalesManDTO salesManDTO = saleManMap.get(memberCardOrderItemDTO.getSalesId());
          if (salesManDTO != null && StringUtils.isNotBlank(salesManDTO.getName())) {
            saleMans.append(salesManDTO.getName()).append(",");
          }
          //卡名字
          if (memberCardDTOMap != null && MapUtils.isNotEmpty(memberCardDTOMap)) {
            if (memberCardOrderItemDTO.getCardId() == null) continue;
            MemberCardDTO cardDTO = memberCardDTOMap.get(memberCardOrderItemDTO.getCardId());
            if (cardDTO != null) {
              memberCardOrderDTO.setMemberCardName(cardDTO.getName());
            }
          }
        }
        orderIndexDTO.setSalesMans(saleMans.length()==0 ? TxnConstant.ASSISTANT_NAME : saleMans.toString());
      }
      orderIndexDTOList.add(orderIndexDTO);
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
  }

  //会员卡退卡
  private void reindexMemberReturnCardOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      TxnWriter writer = txnDaoManager.getWriter();
      List<Long> orderIdList = writer.getMemberReturnCardOrderIds(shopDTO.getId(), start, pageSize);   //得到将要reindex 的orderId
      if (CollectionUtils.isEmpty(orderIdList)) break;
      Long[] orderIds = orderIdList.toArray(new Long[orderIdList.size()]);
      reindexMemberReturnCardOrderImplementor(shopDTO, orderIds);
      start += orderIdList.size();
    }
  }


  //对账单
  private void reindexStatementAccountOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      TxnWriter writer = txnDaoManager.getWriter();
      List<Long> orderIdList = writer.getStatementAccountOrderIds(shopDTO.getId(), start, pageSize);   //得到将要reindex 的orderId
      if (CollectionUtils.isEmpty(orderIdList)) break;
      Long[] orderIds = orderIdList.toArray(new Long[orderIdList.size()]);
      reindexStatementAccountImplementor(shopDTO, orderIds);
      start += orderIdList.size();
    }
  }

  private void reindexMemberReturnCardOrderImplementor(ShopDTO shopDTO, Long... orderIds) throws Exception {
    Long shopId = shopDTO.getId();
    OrderIndexDTO orderIndexDTO;
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    //组合 item ReceptionRecord
    List<MemberCardReturnDTO> memberCardReturnDTOList = getTxnService().getMemberReturnCardOrdersDetailByShopIdAndOrderIds(shopId, orderIds);
    for (MemberCardReturnDTO memberCardReturnDTO : memberCardReturnDTOList) {
      orderIndexDTO = memberCardReturnDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_PAYABLE_DEBT_TYPE);
      //支付方式 存入solr
      if (memberCardReturnDTO.getReceptionRecordDTO() != null) {
        List<PayMethod> payMethods = new ArrayList<PayMethod>();
        if (null != memberCardReturnDTO.getReceptionRecordDTO().getCash() && memberCardReturnDTO.getReceptionRecordDTO().getCash() > 0) { //现金
          payMethods.add(PayMethod.CASH);
        }
        if (null != memberCardReturnDTO.getReceptionRecordDTO().getBankCard() && memberCardReturnDTO.getReceptionRecordDTO().getBankCard() > 0) { //银行卡
          payMethods.add(PayMethod.BANK_CARD);
        }
        if (null != memberCardReturnDTO.getReceptionRecordDTO().getCheque() && memberCardReturnDTO.getReceptionRecordDTO().getCheque() > 0) {// 支票
          payMethods.add(PayMethod.CHEQUE);
        }
        if (memberCardReturnDTO.getStatementAccountOrderId() != null) {//对账支付
          payMethods.add(PayMethod.STATEMENT_ACCOUNT);
        }
        orderIndexDTO.setPayMethods(payMethods);
      } else {
        LOG.warn("memberCardReturnDTO ReceptionRecordDTO is null,orderIndexDto payMethods save order failed.");
      }
      if (CollectionUtils.isNotEmpty(memberCardReturnDTO.getMemberCardReturnItemDTOs())) {
        String salesMan = memberCardReturnDTO.getMemberCardReturnItemDTOs().get(0).getSalesMan();
        orderIndexDTO.setSalesMans(StringUtils.isNotBlank(salesMan) ? salesMan : TxnConstant.ASSISTANT_NAME);
      }
      orderIndexDTOList.add(orderIndexDTO);
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
  }

  @Override
  public void reCreateRepairServiceSolrIndex(Long shopId, int rows) throws Exception {
    if (shopId == null) return;
    SolrClientHelper.getSuggestionClient().deleteByQuery("shop_id:" + shopId + " AND doc_type:" + SolrClientHelper.BcgogoSolrDocumentType.SERVICE_DOC_TYPE.getValue());

    Set<Long> serviceIdSet = null;
    int start = 0;
    while (true) {
      List<Long> serviceIdList = ServiceManager.getService(ITxnService.class).getAllServiceIdsByShopId(shopId, start, rows);
      if (CollectionUtils.isEmpty(serviceIdList)) break;
      start += serviceIdList.size();
      serviceIdSet = new HashSet<Long>();
      serviceIdSet.addAll(serviceIdList);
      createRepairServiceSolrIndex(shopId, serviceIdSet);
    }
  }

  public void createRepairServiceSolrIndex(Long shopId, Set<Long> serviceIdSet) throws Exception {
    LOG.debug("开始对serviceId 做索引");
    if (CollectionUtils.isEmpty(serviceIdSet)) return;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Map<Long, ServiceDTO> serviceDTOMap = txnService.getServiceByServiceIdSet(shopId, serviceIdSet);
    if (serviceDTOMap.isEmpty()) return;
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    for (ServiceDTO serviceDTO : serviceDTOMap.values()) {
      SolrInputDocument doc = new SolrInputDocument();

      doc.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.SERVICE_DOC_TYPE.getValue());
      doc.addField("id", serviceDTO.getId());
      doc.addField("shop_id", serviceDTO.getShopId());
      doc.addField("business_category_id", serviceDTO.getCategoryId());
      doc.addField("name", serviceDTO.getName());
      if (StringUtils.isNotBlank(serviceDTO.getName())) {
        PingyinInfo pingyinInfo = PinyinUtil.getPingyinInfo(serviceDTO.getName());
        doc.addField("name_fl", pingyinInfo.firstLetters);
        doc.addField("name_py", pingyinInfo.pingyin);
      }
      doc.addField("price", serviceDTO.getPrice()==null?0d:serviceDTO.getPrice());
      if (serviceDTO.getStatus() == null) {
        doc.addField("status", ServiceStatus.ENABLED);
      } else {
        doc.addField("status", serviceDTO.getStatus());
      }
      doc.addField("use_times", NumberUtil.longValue(serviceDTO.getUseTimes()));

      docs.add(doc);
    }
    SolrClientHelper.getSuggestionClient().addDocs(docs);
  }

  private void createOrderItemSolrIndex(ShopDTO shopDTO,Collection<ItemIndexDTO> itemColl,Collection<ItemIndexDTO> recordColl,Long[] orderIds) throws Exception {
    if (!ArrayUtils.isEmpty(orderIds)){
      StringBuffer sb = new StringBuffer();
      sb.append("order_id:(");
      for (int i = 0; i < orderIds.length; i++) {
        sb.append(orderIds[i]);
        if (i < orderIds.length - 1) {
          sb.append(" ");
        }
      }
      sb.append(") ");
      SolrClientHelper.getOrderItemSolrClient().deleteByQuery(sb.toString());
    }

    if (CollectionUtils.isNotEmpty(itemColl)) {
      Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
      for (ItemIndexDTO itemIndexDTO : itemColl) {
        itemIndexDTO.setShopInfo(shopDTO);
        SolrInputDocument doc = generateSolrInputDocument(itemIndexDTO);
        doc.addField("id", itemIndexDTO.getItemId());
        doc.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.ORDER_ITEM_DOC_TYPE.getValue());
        doc.addField("end_time",itemIndexDTO.getEndTime());
        doc.addField("item_memo",itemIndexDTO.getItemMemo());
        doc.addField("quoted_result",itemIndexDTO.getQuotedResult());
        doc.addField("item_total", NumberUtil.doubleVal(itemIndexDTO.getItemTotalAmount()));
        doc.addField("item_total_cost_price", NumberUtil.doubleVal(itemIndexDTO.getTotalCostPrice()));
        doc.addField("item_price", NumberUtil.doubleVal(itemIndexDTO.getItemPrice()));
        doc.addField("business_chance_type", itemIndexDTO.getBusinessChanceType());
        doc.addField("customer_or_supplier_name", itemIndexDTO.getCustomerOrSupplierName());
        doc.addField("customer_or_supplier_status", itemIndexDTO.getCustomerOrSupplierStatus());
        doc.addField("customer_or_supplier_id", itemIndexDTO.getCustomerId());
        doc.addField("vehicle_licence_no", itemIndexDTO.getVehicle());
        doc.addField("business_category_id",itemIndexDTO.getBusinessCategoryId());
        doc.addField("business_category_name",itemIndexDTO.getBusinessCategoryName());
        doc.addField("service_id", itemIndexDTO.getServiceId());
        doc.addField("services", itemIndexDTO.getServices());//服务
        doc.addField("consume_type", itemIndexDTO.getConsumeType());
        doc.addField("supplier_product_id", itemIndexDTO.getSupplierProductId());
        doc.addField("coupon_type", itemIndexDTO.getCouponType());
        doc.addField("vest_date", itemIndexDTO.getVestDate());
        docs.add(doc);
      }
      LOG.debug("start system time : " + System.currentTimeMillis());
      LOG.debug("docs.size : " + docs.size());
      SolrClientHelper.getOrderItemSolrClient().addDocs(docs);
      LOG.debug("updateOrderItem 结束.");
      LOG.debug("end system time : " + System.currentTimeMillis());
    }
    if(CollectionUtils.isNotEmpty(recordColl)){
      Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
      for (ItemIndexDTO itemIndexDTO : recordColl) {
        itemIndexDTO.setShopInfo(shopDTO);
        SolrInputDocument doc = generateSolrInputDocument(itemIndexDTO);
        doc.addField("id", itemIndexDTO.getItemType()+"_"+itemIndexDTO.getInOutRecordId());
        doc.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.INOUT_RECORD_DOC_TYPE.getValue());

        doc.addField("related_supplier_id",itemIndexDTO.getRelatedSupplierId());
        doc.addField("related_supplier_name",itemIndexDTO.getRelatedSupplierName());
        doc.addField("related_customer_id",itemIndexDTO.getRelatedCustomerId());
        doc.addField("related_customer_name",itemIndexDTO.getRelatedCustomerName());

        doc.addField("item_total", NumberUtil.doubleVal(itemIndexDTO.getInoutRecordItemTotalAmount()));
        doc.addField("item_total_cost_price", NumberUtil.doubleVal(itemIndexDTO.getInoutRecordTotalCostPrice()));
        doc.addField("item_price", NumberUtil.doubleVal(itemIndexDTO.getItemPrice()));

        docs.add(doc);
      }
      LOG.debug("start system time : " + System.currentTimeMillis());
      LOG.debug("docs.size : " + docs.size());
      SolrClientHelper.getOrderItemSolrClient().addDocs(docs);
      LOG.debug("update in out record 结束.");
      LOG.debug("end system time : " + System.currentTimeMillis());

    }
  }

  private SolrInputDocument generateSolrInputDocument(ItemIndexDTO itemIndexDTO) throws Exception {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("shop_id", itemIndexDTO.getShopId());
    doc.addField("shop_kind", itemIndexDTO.getShopKind());
    doc.addField("shop_name", itemIndexDTO.getShopName());
    doc.addField("shop_area_info", itemIndexDTO.getShopAreaInfo());
    if (CollectionUtils.isNotEmpty(itemIndexDTO.getShopAreaIdList())) {
      for (Long areaNo : itemIndexDTO.getShopAreaIdList()) {
        if(areaNo!=null){
          doc.addField("shop_area_ids", areaNo);
        }
      }
    }

    doc.addField("order_id",itemIndexDTO.getOrderId());
    doc.addField("order_type", itemIndexDTO.getOrderType());
    doc.addField("order_status", itemIndexDTO.getOrderStatus());
    doc.addField("order_created_time", itemIndexDTO.getOrderTimeCreated());
    doc.addField("order_receipt_no", itemIndexDTO.getOrderReceiptNo());

    doc.addField("storehouse_id", itemIndexDTO.getStorehouseId());
    doc.addField("storehouse_name", itemIndexDTO.getStorehouseName());

    doc.addField("editor", itemIndexDTO.getEditor());

    doc.addField("item_type", itemIndexDTO.getItemType());
    doc.addField("item_count", NumberUtil.doubleVal(itemIndexDTO.getItemCount()));

    doc.addField("product_id", itemIndexDTO.getProductId());

    doc.addField("product_name", itemIndexDTO.getItemName());//产品名称
    doc.addField("product_brand", itemIndexDTO.getItemBrand());
    doc.addField("product_model", itemIndexDTO.getItemModel());
    doc.addField("product_spec", itemIndexDTO.getItemSpec());
    doc.addField("product_vehicle_brand", itemIndexDTO.getVehicleBrand());
    doc.addField("product_vehicle_model", itemIndexDTO.getVehicleModel());
    doc.addField("commodity_code", itemIndexDTO.getCommodityCode());
    doc.addField("unit", itemIndexDTO.getUnit());
    doc.addField("custom_match_p_content", itemIndexDTO.getCustomMatchPContent());
    doc.addField("custom_match_pv_content", itemIndexDTO.getCustomMatchPVContent());
    return doc;
  }


  private void reindexStatementAccountImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    List<StatementAccountOrderDTO> statementAccountOrderDTOList = getTxnService().getStatementAccountOrderByShopIdAndOrderIds(shopId, ids);
    if (CollectionUtils.isEmpty(statementAccountOrderDTOList)) {
      return;
    }
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    Set<Long> customerIds = new HashSet<Long>(statementAccountOrderDTOList.size());
    for (int i = 0, len = statementAccountOrderDTOList.size(); i < len; i++) {
      if (statementAccountOrderDTOList.get(i) == null) {
        continue;
      }
      if (statementAccountOrderDTOList.get(i).getCustomerOrSupplierId() != null) {
        customerIds.add(statementAccountOrderDTOList.get(i).getCustomerOrSupplierId());
      }
    }

    Map<Long, ReceivableDTO> receivableDTOMap = getTxnService().getReceivableDTOByShopIdAndArrayOrderId(shopId, ids);
    Map<Long, PayableDTO> payableDTOMap = ServiceManager.getService(IInventoryService.class).getPayableDTOByPurchaseInventoryId(shopId, ids);
    CustomerDTO customerDTO = null;
    SupplierDTO supplierDTO = null;
    for (StatementAccountOrderDTO statementAccountOrderDTO : statementAccountOrderDTOList) {
      if (statementAccountOrderDTO == null) {
        continue;
      }
      if (statementAccountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
        ReceivableDTO receivableDTO = receivableDTOMap.get(statementAccountOrderDTO.getId());
        if (receivableDTO != null) {
          statementAccountOrderDTO = receivableDTO.toStatementAccountOrderDTO(statementAccountOrderDTO);
          statementAccountOrderDTO.setAccountMemberId(receivableDTO.getMemberId());
          statementAccountOrderDTO.setAccountMemberNo(receivableDTO.getMemberNo());
        }
        customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(statementAccountOrderDTO.getCustomerOrSupplierId());
      } else if (statementAccountOrderDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
        PayableDTO payableDTO = payableDTOMap.get(statementAccountOrderDTO.getId());
        if(payableDTO != null){
          statementAccountOrderDTO = payableDTO.toStatementAccountOrderDTO(statementAccountOrderDTO);
        }
        supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(statementAccountOrderDTO.getCustomerOrSupplierId());
      }
      OrderIndexDTO orderIndexDTO = statementAccountOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(statementAccountOrderDTO.getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_PAYABLE || statementAccountOrderDTO.getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_PAYABLE){
        orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_PAYABLE_DEBT_TYPE);
      }else{
        orderIndexDTO.setDebtType(StatementAccountConstant.SOLR_RECEIVABLE_DEBT_TYPE);
      }


      if (statementAccountOrderDTO.getDebt() != 0) {
        orderIndexDTO.setArrears(statementAccountOrderDTO.getDebt());
      }

      List<Long> customerOrSupplierSreaIdList = new ArrayList<Long>();
      if (statementAccountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
        if(customerDTO != null) {
          if(customerDTO.getProvince() != null) {
            customerOrSupplierSreaIdList.add(customerDTO.getProvince());
          }
          if(customerDTO.getCity() != null) {
            customerOrSupplierSreaIdList.add(customerDTO.getCity());
          }
          if(customerDTO.getRegion() != null) {
            customerOrSupplierSreaIdList.add(customerDTO.getRegion());
          }

        }
      } else if (statementAccountOrderDTO.getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
        if(supplierDTO != null) {
          if(supplierDTO.getProvince() != null) {
            customerOrSupplierSreaIdList.add(supplierDTO.getProvince());
          }
          if(supplierDTO.getCity() != null) {
            customerOrSupplierSreaIdList.add(supplierDTO.getCity());
          }
          if(supplierDTO.getRegion() != null) {
            customerOrSupplierSreaIdList.add(supplierDTO.getRegion());
          }

        }
      }
      orderIndexDTO.setCustomerOrSupplierAreaIdList(customerOrSupplierSreaIdList);
      orderIndexDTOList.add(orderIndexDTO);
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
  }

  private void reindexInventoryCheckImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<InventoryCheckDTO> inventoryCheckDTOList = getTxnService().getInventoryCheckDTOsByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();


    List<InventoryCheckItemDTO> inventoryCheckItemDTOList = getTxnService().getInventoryCheckItemDTOs(ids);
    Set<Long> productHistoryIds = new HashSet<Long>((int) (inventoryCheckItemDTOList.size() / 0.75f) + 1, 0.75f);
    Map<Long, List<InventoryCheckItemDTO>> inventoryCheckItemDTOMap = new HashMap<Long, List<InventoryCheckItemDTO>>((int) (inventoryCheckItemDTOList.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(inventoryCheckItemDTOList)) {
      for (InventoryCheckItemDTO itemDTO : inventoryCheckItemDTOList) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(inventoryCheckItemDTOList)) {
      Map<Long,List<OutStorageRelationDTO>> outStorageRelationDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<OutStorageRelationDTO>>(): getProductThroughService().getOutStorageRelationMap(shopId,ids);
      Map<Long,List<InStorageRecordDTO>> inStorageRecordDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<InStorageRecordDTO>>():getProductThroughService().getInStorageRecordDTOMapByOrderIds(shopId, ids);
      List<OutStorageRelationDTO> outStorageRelationDTOList = null;
      List<InStorageRecordDTO> inStorageRecordDTOList = null;
      ProductHistoryDTO productHistoryDTO = null;
      for (InventoryCheckItemDTO itemDTO : inventoryCheckItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        outStorageRelationDTOList = outStorageRelationDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(outStorageRelationDTOList)){
          itemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
        }
        inStorageRecordDTOList = inStorageRecordDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(inStorageRecordDTOList)){
          itemDTO.setInStorageRecordDTOs(inStorageRecordDTOList.toArray(new InStorageRecordDTO[inStorageRecordDTOList.size()]));
        }

        productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductHistoryId());
        if (productHistoryDTO != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTO);
        }
        if (itemDTO.getInventoryCheckId() != null) {
          List<InventoryCheckItemDTO> itemDTOList = inventoryCheckItemDTOMap.get(itemDTO.getInventoryCheckId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
          } else {
            itemDTOList = new ArrayList<InventoryCheckItemDTO>();
            itemDTOList.add(itemDTO);
          }
          inventoryCheckItemDTOMap.put(itemDTO.getInventoryCheckId(), itemDTOList);
        }
      }
    }

    for (InventoryCheckDTO inventoryCheckDTO : inventoryCheckDTOList) {
      if (inventoryCheckDTO == null) {
        continue;
      }
      inventoryCheckDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);

      List<InventoryCheckItemDTO> checkItemDTOList = inventoryCheckItemDTOMap.get(inventoryCheckDTO.getId());
      if(CollectionUtils.isNotEmpty(checkItemDTOList)){
        inventoryCheckDTO.setItemDTOs(checkItemDTOList.toArray(new InventoryCheckItemDTO[checkItemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = inventoryCheckDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    this.createOrderItemSolrIndex(shopDTO,null,inOutRecordDTOList,ids);
  }

  private void reindexAllocateRecordImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<AllocateRecordDTO> allocateRecordDTOList = getTxnService().getAllocateRecordDTOsByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();


    List<AllocateRecordItemDTO> allocateRecordItemDTOList = getTxnService().getAllocateRecordItemDTOs(ids);
    Set<Long> productHistoryIds = new HashSet<Long>((int) (allocateRecordItemDTOList.size() / 0.75f) + 1, 0.75f);
    Map<Long, List<AllocateRecordItemDTO>> allocateRecordItemDTOMap = new HashMap<Long, List<AllocateRecordItemDTO>>((int) (allocateRecordItemDTOList.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(allocateRecordItemDTOList)) {
      for (AllocateRecordItemDTO itemDTO : allocateRecordItemDTOList) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(allocateRecordItemDTOList)) {
      Map<Long,List<OutStorageRelationDTO>> outStorageRelationDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<OutStorageRelationDTO>>(): getProductThroughService().getOutStorageRelationMap(shopId, ids);
      Map<Long,List<InStorageRecordDTO>> inStorageRecordDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<InStorageRecordDTO>>(): getProductThroughService().getInStorageRecordDTOMapByOrderIds(shopId, ids);
      List<OutStorageRelationDTO> outStorageRelationDTOList = null;
      List<InStorageRecordDTO> inStorageRecordDTOList = null;
      ProductHistoryDTO productHistoryDTO = null;
      for (AllocateRecordItemDTO itemDTO : allocateRecordItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        outStorageRelationDTOList = outStorageRelationDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(outStorageRelationDTOList)){
          itemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
        }
        inStorageRecordDTOList = inStorageRecordDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(inStorageRecordDTOList)){
          itemDTO.setInStorageRecordDTOs(inStorageRecordDTOList.toArray(new InStorageRecordDTO[inStorageRecordDTOList.size()]));
        }
        productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductHistoryId());
        if (productHistoryDTO != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTO);
        }
        if (itemDTO.getAllocateRecordId() != null) {
          List<AllocateRecordItemDTO> itemDTOList = allocateRecordItemDTOMap.get(itemDTO.getAllocateRecordId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
          } else {
            itemDTOList = new ArrayList<AllocateRecordItemDTO>();
            itemDTOList.add(itemDTO);
          }
          allocateRecordItemDTOMap.put(itemDTO.getAllocateRecordId(), itemDTOList);
        }
      }
    }

    List<AllocateRecordItemDTO> itemDTOList = null;
    for (AllocateRecordDTO allocateRecordDTO : allocateRecordDTOList) {
      if (allocateRecordDTO == null) {
        continue;
      }
      allocateRecordDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      itemDTOList = allocateRecordItemDTOMap.get(allocateRecordDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        allocateRecordDTO.setItemDTOs(itemDTOList.toArray(new AllocateRecordItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = allocateRecordDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    this.createOrderItemSolrIndex(shopDTO,null,inOutRecordDTOList,ids);
  }

  //调拨单reindex
  private void reindexAllocateRecordOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getAllocateRecordOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) break;
      start += orderIds.size();
      reindexAllocateRecordImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexRepairPickingImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    List<RepairPickingDTO> repairPickingDTOList = getPickingService().getRepairPickingDTODetails(shopId, ids);  //得到将要reindex 的orderId
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    for (RepairPickingDTO repairPickingDTO : repairPickingDTOList) {
      if (repairPickingDTO == null || CollectionUtils.isEmpty(repairPickingDTO.getHandledItemDTOs())) {
        continue;
      }
      OrderIndexDTO orderIndexDTO = repairPickingDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    this.createOrderItemSolrIndex(shopDTO,null,inOutRecordDTOList,ids);
  }


  private void reindexInnerPickingOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getInnerPickingOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) break;
      start += orderIds.size();
      reindexInnerPickingImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexInnerPickingImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<InnerPickingDTO> innerPickingDTOList = getTxnService().getInnerPickingDTOsByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();

    Map<Long,List<InnerPickingItemDTO>> innerPickingItemDTOMap = getPickingService().getInnerPickingItemDTOs(ids);
    Map<Long,List<OutStorageRelationDTO>> outStorageRelationDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<OutStorageRelationDTO>>():getProductThroughService().getOutStorageRelationMap(shopId,ids);
    List<OutStorageRelationDTO> outStorageRelationDTOList = null;
    for(List<InnerPickingItemDTO> innerPickingItemDTOList :innerPickingItemDTOMap.values()){
      if(CollectionUtils.isNotEmpty(innerPickingItemDTOList)){
        for(InnerPickingItemDTO innerPickingItemDTO : innerPickingItemDTOList){
          outStorageRelationDTOList = outStorageRelationDTOMap.get(innerPickingItemDTO.getId());
          if(CollectionUtils.isNotEmpty(outStorageRelationDTOList)){
            innerPickingItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
          }
        }
      }
    }
    List<InnerPickingItemDTO> itemDTOList = null;
    for (InnerPickingDTO innerPickingDTO : innerPickingDTOList) {
      if (innerPickingDTO == null) {
        continue;
      }
      innerPickingDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      itemDTOList = innerPickingItemDTOMap.get(innerPickingDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        innerPickingDTO.setItemDTOs(itemDTOList.toArray(new InnerPickingItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = innerPickingDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    this.createOrderItemSolrIndex(shopDTO,null,inOutRecordDTOList,ids);
  }

  private void reindexInnerReturnOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getInnerReturnOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) break;
      start += orderIds.size();
      reindexInnerReturnImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexInnerReturnImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId =shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<InnerReturnDTO> innerReturnDTOList = getTxnService().getInnerReturnDTOsByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();

    Map<Long,List<InnerReturnItemDTO>> innerReturnItemDTOMap = getPickingService().getInnerReturnItemDTOs(ids);
    Map<Long,List<InStorageRecordDTO>> inStorageRecordDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<InStorageRecordDTO>>():getProductThroughService().getInStorageRecordDTOMapByOrderIds(shopId, ids);
    List<InStorageRecordDTO> inStorageRecordDTOList = null;
    for(List<InnerReturnItemDTO> innerReturnItemDTOList :innerReturnItemDTOMap.values()){
      if(CollectionUtils.isNotEmpty(innerReturnItemDTOList)){
        for(InnerReturnItemDTO innerReturnItemDTO : innerReturnItemDTOList){
          inStorageRecordDTOList = inStorageRecordDTOMap.get(innerReturnItemDTO.getId());
          if(CollectionUtils.isNotEmpty(inStorageRecordDTOList)){
            innerReturnItemDTO.setInStorageRecordDTOs(inStorageRecordDTOList.toArray(new InStorageRecordDTO[inStorageRecordDTOList.size()]));
          }
        }
      }
    }
    List<InnerReturnItemDTO> itemDTOList = null;
    for (InnerReturnDTO innerReturnDTO : innerReturnDTOList) {
      if (innerReturnDTO == null) {
        continue;
      }
      innerReturnDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      itemDTOList = innerReturnItemDTOMap.get(innerReturnDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        innerReturnDTO.setItemDTOs(itemDTOList.toArray(new InnerReturnItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = innerReturnDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    this.createOrderItemSolrIndex(shopDTO,null,inOutRecordDTOList,ids);
  }

  private void reindexBorrowOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getBorrowOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) break;
      start += orderIds.size();
      reindexBorrowOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexBorrowOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId =shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<BorrowOrderDTO> borrowOrderDTOList = getTxnService().getBorrowOrderDTOsByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    Map<String,BorrowOrderDTO> borrowOrderDTOMap = new HashMap<String, BorrowOrderDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    IBorrowService borrowService = ServiceManager.getService(IBorrowService.class);
    List<BorrowOrderItemDTO> borrowOrderItemDTOList = borrowService.getBorrowOrderItemDTOByOrderId(shopId, ids);
    Set<Long> productHistoryIds = new HashSet<Long>((int) (borrowOrderItemDTOList.size() / 0.75f) + 1, 0.75f);
    Map<Long, List<BorrowOrderItemDTO>> borrowOrderItemDTOMap = new HashMap<Long, List<BorrowOrderItemDTO>>((int) (borrowOrderItemDTOList.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(borrowOrderItemDTOList)) {
      for (BorrowOrderItemDTO itemDTO : borrowOrderItemDTOList) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(borrowOrderItemDTOList)) {
      Map<Long,List<OutStorageRelationDTO>> outStorageRelationDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<OutStorageRelationDTO>>():getProductThroughService().getOutStorageRelationMap(shopId,ids);
      List<OutStorageRelationDTO> outStorageRelationDTOList = null;
      ProductHistoryDTO productHistoryDTO = null;
      for (BorrowOrderItemDTO itemDTO : borrowOrderItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        outStorageRelationDTOList = outStorageRelationDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(outStorageRelationDTOList)){
          itemDTO.setOutStorageRelationDTOs(outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]));
        }

        productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductHistoryId());
        if (productHistoryDTO != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTO);
        }
        if (itemDTO.getOrderId() != null) {
          List<BorrowOrderItemDTO> itemDTOList = borrowOrderItemDTOMap.get(itemDTO.getOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
          } else {
            itemDTOList = new ArrayList<BorrowOrderItemDTO>();
            itemDTOList.add(itemDTO);
          }
          borrowOrderItemDTOMap.put(itemDTO.getOrderId(), itemDTOList);
        }
      }
    }
    List<BorrowOrderItemDTO> itemDTOList = null;
    for (BorrowOrderDTO borrowOrderDTO : borrowOrderDTOList) {
      if (borrowOrderDTO == null) {
        continue;
      }
      borrowOrderDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      borrowOrderDTOMap.put(borrowOrderDTO.getId().toString(),borrowOrderDTO);

      itemDTOList = borrowOrderItemDTOMap.get(borrowOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        borrowOrderDTO.setItemDTOs(itemDTOList.toArray(new BorrowOrderItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = borrowOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    this.createOrderItemSolrIndex(shopDTO,null,inOutRecordDTOList,ids);
  }

  private void reindexReturnBorrowOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getReturnBorrowOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) break;
      start += orderIds.size();
      reindexReturnBorrowOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexReturnBorrowOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId =shopDTO.getId();
    boolean mergeInOutRecordFlag = !BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopDTO.getShopVersionId());
    List<ReturnOrderDTO> returnOrderDTOList = getTxnService().getReturnBorrowOrderDTOsByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    Set<Long> borrowOrderIdSet = new HashSet<Long>();
    for(ReturnOrderDTO returnOrderDTO :returnOrderDTOList){
      if(NumberUtil.isLongNumber(returnOrderDTO.getBorrowOrderId())){
        borrowOrderIdSet.add(NumberUtil.longValue(returnOrderDTO.getBorrowOrderId()));
      }
    }
    List<BorrowOrderDTO> borrowOrderDTOList = getTxnService().getBorrowOrderDTOsByShopIdAndOrderIds(shopId, borrowOrderIdSet.toArray(new Long[borrowOrderIdSet.size()]));
    Map<String,BorrowOrderDTO> borrowOrderDTOMap = new HashMap<String, BorrowOrderDTO>();
    for (BorrowOrderDTO borrowOrderDTO : borrowOrderDTOList) {
      if (borrowOrderDTO == null) {
        continue;
      }
      borrowOrderDTOMap.put(borrowOrderDTO.getId().toString(),borrowOrderDTO);
    }

    IBorrowService borrowService = ServiceManager.getService(IBorrowService.class);
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    List<ReturnOrderItemDTO> returnOrderItemDTOList = borrowService.getReturnOrderItemDTOsByBorrowOrderIds(shopId, ids);
    Set<Long> productHistoryIds = new HashSet<Long>((int) (returnOrderItemDTOList.size() / 0.75f) + 1, 0.75f);
    Map<Long, List<ReturnOrderItemDTO>> returnOrderItemDTOMap = new HashMap<Long, List<ReturnOrderItemDTO>>((int) (returnOrderItemDTOList.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(returnOrderItemDTOList)) {
      for (ReturnOrderItemDTO itemDTO : returnOrderItemDTOList) {
        if (itemDTO != null && itemDTO.getProductHistoryId() != null) {
          productHistoryIds.add(itemDTO.getProductHistoryId());
        }
      }
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    if (CollectionUtils.isNotEmpty(returnOrderItemDTOList)) {
      Map<Long,List<InStorageRecordDTO>> inStorageRecordDTOMap = mergeInOutRecordFlag? new HashMap<Long, List<InStorageRecordDTO>>():getProductThroughService().getInStorageRecordDTOMapByOrderIds(shopId, ids);
      List<InStorageRecordDTO> inStorageRecordDTOList = null;
      ProductHistoryDTO productHistoryDTO = null;
      for (ReturnOrderItemDTO itemDTO : returnOrderItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        inStorageRecordDTOList = inStorageRecordDTOMap.get(itemDTO.getId());
        if(CollectionUtils.isNotEmpty(inStorageRecordDTOList)){
          itemDTO.setInStorageRecordDTOs(inStorageRecordDTOList.toArray(new InStorageRecordDTO[inStorageRecordDTOList.size()]));
        }

        productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductHistoryId());
        if (productHistoryDTO != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTO);
        }
        if (itemDTO.getOrderId() != null) {
          List<ReturnOrderItemDTO> returnOrderItemDTOs = returnOrderItemDTOMap.get(itemDTO.getOrderId());
          if (CollectionUtils.isNotEmpty(returnOrderItemDTOs)) {
            returnOrderItemDTOs.add(itemDTO);
          } else {
            returnOrderItemDTOs = new ArrayList<ReturnOrderItemDTO>();
            returnOrderItemDTOs.add(itemDTO);
          }
          returnOrderItemDTOMap.put(itemDTO.getOrderId(), returnOrderItemDTOs);
        }
      }
    }
    List<ReturnOrderItemDTO> returnOrderItemDTOs = null;
    BorrowOrderDTO borrowOrderDTO = null;
    for (ReturnOrderDTO returnOrderDTO : returnOrderDTOList) {
      if (returnOrderDTO == null) {
        continue;
      }
      returnOrderDTO.setMergeInOutRecordFlag(mergeInOutRecordFlag);
      borrowOrderDTO = borrowOrderDTOMap.get(returnOrderDTO.getBorrowOrderId());
      if(borrowOrderDTO!=null){
        returnOrderDTO.setBorrowOrderReceiptNo(borrowOrderDTO.getReceiptNo());
        returnOrderDTO.setReturner(borrowOrderDTO.getBorrower());
        returnOrderDTO.setReturnId(borrowOrderDTO.getBorrowerId());
        returnOrderDTO.setReturnerType(borrowOrderDTO.getBorrowerType());
      }
      returnOrderItemDTOs = returnOrderItemDTOMap.get(returnOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(returnOrderItemDTOs)) {
        returnOrderDTO.setItemDTOs(returnOrderItemDTOs.toArray(new ReturnOrderItemDTO[returnOrderItemDTOs.size()]));
      }
      OrderIndexDTO orderIndexDTO = returnOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getInOutRecordDTOList())){
        inOutRecordDTOList.addAll(orderIndexDTO.getInOutRecordDTOList());
      }
    }
    this.createOrderItemSolrIndex(shopDTO,null,inOutRecordDTOList,ids);
  }


  private void reindexPreBuyOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getPreBuyOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) {
        break;
      }
      start += pageSize;
      reindexPreBuyOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexPreBuyOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    List<PreBuyOrderDTO> preBuyOrderDTOList = getTxnService().getPreBuyOrdersByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();

    List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = getTxnService().getPreBuyOrderItemDTOs(shopId,ids);
    Map<Long, List<PreBuyOrderItemDTO>> preBuyOrderItemMap = new HashMap<Long, List<PreBuyOrderItemDTO>>((int) (preBuyOrderItemDTOList.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(preBuyOrderItemDTOList)) {
      for (PreBuyOrderItemDTO itemDTO : preBuyOrderItemDTOList) {
        if (itemDTO == null||itemDTO.getPreBuyOrderId() == null) {
          continue;
        }
          List<PreBuyOrderItemDTO> itemDTOList = preBuyOrderItemMap.get(itemDTO.getPreBuyOrderId());
        if (CollectionUtils.isNotEmpty(itemDTOList)){
            itemDTOList.add(itemDTO);
            preBuyOrderItemMap.put(itemDTO.getPreBuyOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<PreBuyOrderItemDTO>();
            itemDTOList.add(itemDTO);
            preBuyOrderItemMap.put(itemDTO.getPreBuyOrderId(), itemDTOList);
          }
        }
      }
    for (PreBuyOrderDTO preBuyOrderDTO : preBuyOrderDTOList) {
      if (preBuyOrderDTO == null) {
        continue;
      }
      List<PreBuyOrderItemDTO> itemDTOList = preBuyOrderItemMap.get(preBuyOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        preBuyOrderDTO.setItemDTOs(itemDTOList.toArray(new PreBuyOrderItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = preBuyOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTOList.add(orderIndexDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("PreBuyOrder[orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
    this.createOrderItemSolrIndex(shopDTO,itemIndexDTOList,null,ids);
  }


  private void reindexQuotedPreBuyOrder(ShopDTO shopDTO, int pageSize) throws Exception {
    int start = 0;
    while (true) {
      List<Long> orderIds = getTxnService().getQuotedPreBuyOrderIds(shopDTO.getId(), start, pageSize);
      if (CollectionUtils.isEmpty(orderIds)) {
        break;
      }
      start += pageSize;
      reindexQuotedPreBuyOrderImplementor(shopDTO, orderIds.toArray(new Long[orderIds.size()]));
    }
  }

  private void reindexQuotedPreBuyOrderImplementor(ShopDTO shopDTO, Long... ids) throws Exception {
    Long shopId = shopDTO.getId();
    List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOList = getTxnService().getQuotedPreBuyOrdersByShopIdAndOrderIds(shopId, ids);  //得到将要reindex 的orderId
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();

    List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = getTxnService().getQuotedPreBuyOrderItemDTOs(shopId,ids);
    Map<Long, List<QuotedPreBuyOrderItemDTO>> quotedPreBuyOrderItemMap = new HashMap<Long, List<QuotedPreBuyOrderItemDTO>>((int) (quotedPreBuyOrderItemDTOList.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemDTOList)) {
      Set<Long> productIdSet = new HashSet<Long>((int) (quotedPreBuyOrderItemDTOList.size() / 0.75f) + 1, 0.75f);
      for (QuotedPreBuyOrderItemDTO itemDTO : quotedPreBuyOrderItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        productIdSet.add(itemDTO.getProductId());
      }
      Map<Long,ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopDTO.getId(),productIdSet);
      for (QuotedPreBuyOrderItemDTO itemDTO : quotedPreBuyOrderItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));

        if (itemDTO.getQuotedPreBuyOrderId() != null) {
          List<QuotedPreBuyOrderItemDTO> itemDTOList = quotedPreBuyOrderItemMap.get(itemDTO.getQuotedPreBuyOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            quotedPreBuyOrderItemMap.put(itemDTO.getQuotedPreBuyOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<QuotedPreBuyOrderItemDTO>();
            itemDTOList.add(itemDTO);
            quotedPreBuyOrderItemMap.put(itemDTO.getQuotedPreBuyOrderId(), itemDTOList);
          }
        }
      }
    }
    for (QuotedPreBuyOrderDTO quotedPreBuyOrderDTO : quotedPreBuyOrderDTOList) {
      if (quotedPreBuyOrderDTO == null) {
        continue;
      }
      List<QuotedPreBuyOrderItemDTO> itemDTOList = quotedPreBuyOrderItemMap.get(quotedPreBuyOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        quotedPreBuyOrderDTO.setItemDTOs(itemDTOList.toArray(new QuotedPreBuyOrderItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = quotedPreBuyOrderDTO.toOrderIndexDTO();
      orderIndexDTO.setShopInfo(shopDTO);
      orderIndexDTOList.add(orderIndexDTO);
      if(CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())){
        itemIndexDTOList.addAll(orderIndexDTO.getItemIndexDTOList());
      }else{
        LOG.error("QuotedPreBuyOrder [orderId:{}] item is null",orderIndexDTO.getOrderId());
      }
    }
    getOrderIndexService().addOrderIndexToSolr(orderIndexDTOList);
    this.createOrderItemSolrIndex(shopDTO,itemIndexDTOList,null,ids);
  }
}