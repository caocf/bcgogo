package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.ShopRelation.IShopRelationService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-11-14
 * Time: 上午10:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GoodBuyService implements IGoodBuyService {

	private static final Logger LOG = LoggerFactory.getLogger(GoodBuyService.class);

	@Autowired
	private TxnDaoManager txnDaoManager;

	private IProductService productService;
	private IInventoryService inventoryService;
	private IUserService userService;
	private ISupplierRecordService supplierRecordService;
	private ISupplierService supplierService;
	private ITxnService txnService;
	private RFITxnService rfiTxnService;
	private IConfigService configService;
	private ISearchService searchService;
	private IProductCurrentUsedService productCurrentUsedService;
	private IOrderStatusChangeLogService orderStatusChangeLogService;
  private IGoodSaleService goodSaleService;

	public IProductService getProductService() {
		if(productService == null){
			productService = ServiceManager.getService(IProductService.class);
		}
		return productService;
	}

	public IInventoryService getInventoryService() {
		if(inventoryService == null){
			inventoryService = ServiceManager.getService(IInventoryService.class);
		}
		return inventoryService;
	}

	public IUserService getUserService() {
		if(userService == null){
			userService = ServiceManager.getService(IUserService.class);
		}
		return userService;
	}

	public ISupplierRecordService getSupplierRecordService() {
		if(supplierRecordService == null){
			supplierRecordService = ServiceManager.getService(ISupplierRecordService.class);
		}
		return supplierRecordService;
	}

	public ISupplierService getSupplierService() {
		if(supplierService == null){
			supplierService = ServiceManager.getService(ISupplierService.class);
		}
		return supplierService;
	}

	public ITxnService getTxnService() {
		if(txnService == null){
			txnService = ServiceManager.getService(ITxnService.class);
		}
		return txnService;
	}


	public RFITxnService getRfiTxnService() {
		if(rfiTxnService == null){
			rfiTxnService = ServiceManager.getService(RFITxnService.class);
		}
		return rfiTxnService;
	}

	public IConfigService getConfigService() {
		if(configService == null){
			configService = ServiceManager.getService(IConfigService.class);
		}
		return configService;
	}

	public ISearchService getSearchService() {
		if(searchService == null){
			searchService = ServiceManager.getService(ISearchService.class);
		}
		return searchService;
	}

	public IProductCurrentUsedService getProductCurrentUsedService() {
		if(productCurrentUsedService == null){
			productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
		}
		return productCurrentUsedService;
	}

	public IOrderStatusChangeLogService getOrderStatusChangeLogService() {
		if(orderStatusChangeLogService == null){
			orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
		}
		return orderStatusChangeLogService;
	}

  public IGoodSaleService getGoodSaleService() {
    return goodSaleService == null ? ServiceManager.getService(IGoodSaleService.class) : goodSaleService;
  }

  @Override
	public void setLocalInfoWithProductMapping(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
		if (purchaseOrderDTO == null || !purchaseOrderDTO.isWholesalerPurchase() || ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())) {
			return;
		}
		if (purchaseOrderDTO.getSupplierShopId() == null || purchaseOrderDTO.getShopId() == null) {
			LOG.error("批发商采购单，批发商shopId:{} 或者本店shopId:{}为空", purchaseOrderDTO.getSupplierShopId(), purchaseOrderDTO.getShopId());
		}
		Long productShopId = purchaseOrderDTO.getSupplierShopId();
		Long shopId = purchaseOrderDTO.getShopId();
		Set<Long> productIdSet = new HashSet<Long>();
		for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
			if (purchaseOrderItemDTO.getSupplierProductId() != null) {
				productIdSet.add(purchaseOrderItemDTO.getSupplierProductId());
			}
		}
		Map<Long, ProductMappingDTO> productMappingDTOMap = getProductService().getSupplierProductMappingDTODetailMap(productShopId, shopId, productIdSet);

		Set<Long> customerProductIds = new HashSet<Long>();
		if (productMappingDTOMap != null && !productMappingDTOMap.isEmpty()) {
			List<ProductMappingDTO> productMappingDTOs = new ArrayList<ProductMappingDTO>(productMappingDTOMap.values());
			for (ProductMappingDTO productMappingDTO : productMappingDTOs) {
				customerProductIds.add(productMappingDTO.getCustomerProductId());
			}
		}
		Map<Long, InventoryDTO> customerInventoryDTOMap = getInventoryService().getInventoryDTOMap(shopId, customerProductIds);
		for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
			if (purchaseOrderItemDTO.getSupplierProductId() != null) {
				ProductMappingDTO productMappingDTO = productMappingDTOMap.get(purchaseOrderItemDTO.getSupplierProductId());
				if (productMappingDTO != null && productMappingDTO.isProductMappingEnabled()) {
					InventoryDTO inventoryDTO = customerInventoryDTOMap.get(productMappingDTO.getCustomerProductId());
					if (inventoryDTO != null) {
						purchaseOrderItemDTO.setLowerLimit(inventoryDTO.getLowerLimit());
						purchaseOrderItemDTO.setUpperLimit(inventoryDTO.getUpperLimit());
					}
					ProductDTO productDTO = productMappingDTO.getCustomerProductDTO();
					if (productDTO != null) {
						purchaseOrderItemDTO.setTradePrice(productDTO.getTradePrice());
						purchaseOrderItemDTO.setStorageBin(productDTO.getStorageBin());
            purchaseOrderItemDTO.setProductKind(productDTO.getKindName());
					}
				}
			}
		}
	}

	@Override
  public SupplierDTO handleSupplierForPurchase(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    SupplierDTO supplierDTO = new SupplierDTO();
    if (purchaseOrderDTO.getSupplierId() != null) {
      supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseOrderDTO.getSupplierId());
    }
    if ((purchaseOrderDTO.getSupplierId() == null && StringUtils.isNotBlank(purchaseOrderDTO.getSupplier())) ||
        (supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus()))) { // 页面直接新填入供应商名称
      supplierDTO = getSupplierService().getSupplierDTOByPreciseName(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getSupplier());
    }
    if (supplierDTO == null){
       supplierDTO = new SupplierDTO();
    }
    supplierDTO.set(purchaseOrderDTO);
    /*if (supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) { commented by zhuj
      supplierDTO.setStatus(CustomerStatus.DISABLED);
    }*/
      //by qxy 2013-08-05下面这段代码在上面supplierDTO构造里面已经有了
    // add by zhuj 单独处理联系人 contactId存在 更新联系人信息 不存在 新增 设置到supplierDTO 做单的时候 不会出现既是客户又是供应商的情况    、
//    ContactDTO contactDTO = new ContactDTO();
//    contactDTO.setDisabled(1);
//    contactDTO.setMainContact(1);
//    contactDTO.setLevel(0);
//    contactDTO.setMobile(purchaseOrderDTO.getMobile());
//    contactDTO.setName(purchaseOrderDTO.getContact());
//    contactDTO.setEmail(purchaseOrderDTO.getEmail()); //email和QQ 事实上是不存在的。。。
//    contactDTO.setQq(purchaseOrderDTO.getQq());
//    contactDTO.setShopId(purchaseOrderDTO.getShopId());
//    if (purchaseOrderDTO.getContactId() == null || purchaseOrderDTO.getContactId() == 0L) {
//      ContactDTO[] contactDTOs = new ContactDTO[1];
//      contactDTOs[0] = contactDTO;
//      supplierDTO.setContacts(contactDTOs);
//    } else {
//      contactDTO.setId(purchaseOrderDTO.getContactId());
//      ServiceManager.getService(IContactService.class).updateContact(contactDTO);
//    }

    if (supplierDTO.getId() == null || supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) {
      getUserService().createSupplier(supplierDTO);
      // 新增的时候 设置contactId
      supplierDTO = getUserService().getSupplierById(supplierDTO.getId());
      purchaseOrderDTO.setContactId(supplierDTO.getContactId());
      getSupplierRecordService().createSupplierRecordUsingSupplierDTO(supplierDTO);
    }else{
      if(supplierDTO.getCustomerId()==null && supplierDTO.isAddContacts()){
       getUserService().updateSupplier(supplierDTO);
        if (supplierDTO.getCustomerId() == null && supplierDTO.isAddContacts()) {
          getUserService().updateSupplier(supplierDTO);
          if (!ArrayUtils.isEmpty(supplierDTO.getContacts())
              && supplierDTO.getContacts()[0] != null
              && supplierDTO.getContacts()[0].getId() != null) {
            purchaseOrderDTO.setContactId(supplierDTO.getContacts()[0].getId());
          }
        }
      }
    }
    purchaseOrderDTO.setSupplierId(supplierDTO.getId());
    if(supplierDTO.getCustomerId() != null) {
        //同时更新客户的信息
      CustomerDTO customerDTO =  ServiceManager.getService(IUserService.class).getCustomerById(supplierDTO.getCustomerId());
      customerDTO.fromSupplierDTO(supplierDTO);
      ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
      if(supplierDTO.isAddContacts()) {
      ContactDTO[] contactDTOs =  ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(
          supplierDTO.getCustomerId(), supplierDTO.getId(), purchaseOrderDTO.getShopId(), supplierDTO.getContacts()); // add by zhuj 既是客户又是供应商的联系人新增
        if (!ArrayUtils.isEmpty(contactDTOs)
            && supplierDTO.getContacts()[0] != null
            && supplierDTO.getContacts()[0].getId() != null) {
          purchaseOrderDTO.setContactId(supplierDTO.getContacts()[0].getId());
        }
        supplierDTO.setContacts(contactDTOs);
      }
      CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId()).get(0);
      customerRecordDTO.fromCustomerDTO(customerDTO);
      ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
      //出于性能考虑索引都要在线程里做 by qxy
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());

    }
    return supplierDTO;
  }

	@Override
	public void updateSupplierAfterPurchaseOrder(SupplierDTO supplierDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
		String products = purchaseOrderDTO.getItemDTOs()[0].getProductName();
		//如果 最后入库时间>结算时间 默认不变
		if (supplierDTO.getLastOrderTime() == null || supplierDTO.getLastOrderTime() - purchaseOrderDTO.getVestDate() < 0) {
			supplierDTO.setLastOrderTime(purchaseOrderDTO.getVestDate());
		}
    //supplierDTO 没有调用handleSupplierForPurchase 都是db数据  这边 在线采购只更新3个字段
    supplierDTO.setContact(purchaseOrderDTO.getContact());
    supplierDTO.setMobile(purchaseOrderDTO.getMobile());
    supplierDTO.setProvince(purchaseOrderDTO.getProvince());
    supplierDTO.setCity(purchaseOrderDTO.getCity());
    supplierDTO.setRegion(purchaseOrderDTO.getRegion());
    supplierDTO.setAddress(purchaseOrderDTO.getAddress());
		getUserService().updateSupplier(supplierDTO, purchaseOrderDTO.getId(), OrderTypes.PURCHASE, products, 0d); //purchaseOrderDTO.getTotal()采购金额不计入供应商总额
	}

	@Override
	public Result verifyPurchaseModify(PurchaseOrderDTO purchaseOrderDTO ) throws Exception {
		Result result = new Result();
		PurchaseOrderDTO lastPurchaseOrderDTO = getTxnService().getPurchaseOrder(purchaseOrderDTO.getId(),purchaseOrderDTO.getShopId());
		if (lastPurchaseOrderDTO != null) {
			if (!OrderStatus.SELLER_PENDING.equals(lastPurchaseOrderDTO.getStatus())) {
				result.setMsg("当前采购单状态为：" + lastPurchaseOrderDTO.getStatus().getName() + "\"无法改单！");
				result.setOperation(Result.Operation.REDIRECT_SHOW.getValue());
				result.setSuccess(false);
			}
		}
//		if (salesOrderDTO != null) {
//			if (!OrderStatus.PENDING.equals(salesOrderDTO.getStatus())) {
//				result.setMsg("当前单据对应的销售单状态为：" + salesOrderDTO.getStatus().getName() + "\"无法改单！");
//				result.setOperation(Result.Operation.REDIRECT_SHOW.getValue());
//				result.setSuccess(false);
//			}
//		}
		if(!result.isSuccess()){
			 return result;
		}
		return new Result("success",true);
	}

	@Override
	public void handleProductForPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    //在线采购单 不生成 新商品  通过把 对方的商品 复制到历史商品记录中来显示 单据
		//激活有删除的商品
		getRfiTxnService().updateDeleteProductsByOrderDTO(purchaseOrderDTO);
		//保存或者更新商品信息（不包括txn，search）
		getProductService().saveOrUpdateProductForPurchaseOrder(purchaseOrderDTO);
		//更新商品单位顺序
		getConfigService().updateOrderUnitSort(purchaseOrderDTO.getShopId(), purchaseOrderDTO);
	}

	@Override
  public void repealPurchaseSaleOrderDTO(SalesOrderDTO salesOrderDTO) throws Exception {
    if (salesOrderDTO == null || !OrderStatus.PENDING.equals(salesOrderDTO.getStatus())) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
      OrderStatus lastOrderStatus = salesOrderDTO.getStatus();
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        salesOrders.get(0).setStatusEnum(OrderStatus.STOP);
        writer.update(salesOrders.get(0));
        salesOrderDTO.setStatus(salesOrders.get(0).getStatusEnum());
        getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(),
            salesOrderDTO.getUserId(),
            salesOrderDTO.getStatus(),
            lastOrderStatus,
            salesOrderDTO.getId(),
            OrderTypes.SALE));
      }
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

	@Override
	public PurchaseOrderDTO getSimplePurchaseOrderDTO(Long shopId, Long purchaseOrderId) {
    if(shopId == null || purchaseOrderId == null){
      return null;
    }
		TxnWriter writer = txnDaoManager.getWriter();
		List<PurchaseOrder> purchaseOrders =	writer.getPurchaseOrderById(purchaseOrderId,shopId);
		if(CollectionUtils.isNotEmpty(purchaseOrders)){
			return  purchaseOrders.get(0).toDTO();
		}
		return null;
	}

	@Override
	public void synSupplierInSalesAmount(PurchaseOrderDTO purchaseOrderDTO) {
		if(purchaseOrderDTO == null || ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())
				   || purchaseOrderDTO.getSupplierShopId() ==null){
			return;
		}
		Set<Long> supplierIdProductIds = new HashSet<Long>();
		for(PurchaseOrderItemDTO purchaseOrderItemDTO :purchaseOrderDTO.getItemDTOs()){
			if(purchaseOrderItemDTO.getSupplierProductId()!=null){
				supplierIdProductIds.add(purchaseOrderItemDTO.getSupplierProductId());
			}
		}
    Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(purchaseOrderDTO.getSupplierShopId(),supplierIdProductIds.toArray(new Long[supplierIdProductIds.size()]));
			for(PurchaseOrderItemDTO purchaseOrderItemDTO :purchaseOrderDTO.getItemDTOs()){
			if(purchaseOrderItemDTO.getSupplierProductId()!=null){
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(purchaseOrderItemDTO.getSupplierProductId());
				purchaseOrderItemDTO.setInventoryAmount(productLocalInfoDTO.getInSalesAmount());
			}
		}
  }

  @Override
  public Result validateCopy(Long id, Long shopId) {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseOrderDTO purchaseOrderDTO = getSimplePurchaseOrderDTO(shopId, id);

    if (purchaseOrderDTO == null) {
      return new Result("无法复制", "单据不存在，无法复制！", false, Result.Operation.ALERT);
    }
    SupplierDTO supplierDTO = purchaseOrderDTO.generateSupplierDTO();
    boolean supplierSame = supplierService.compareSupplierSameWithHistory(supplierDTO, shopId);
    if (!supplierSame) {
      purchaseOrderDTO.clearSupplierInfo();
    }
//    RelationChangeEnum changeEnum = supplierService.compareSupplierRelationChange(supplierDTO, shopId);
//    if (RelationChangeEnum.UNRELATED_TO_RELATED.equals(changeEnum)) {
//      return new Result("无法复制", "友情提示：当前单据不是在线采购单，无法复制成在线采购单！", false, Result.Operation.ALERT);
//    } else if (RelationChangeEnum.RELATED_CHANGED.equals(changeEnum)) {
//      return new Result("无法复制", "友情提示：当前在线采购的供应商关联关系发生变更，无法复制！", false, Result.Operation.ALERT);
//    }
    List<PurchaseOrderItem> items = writer.getPurchaseOrderItemsByOrderId(id);
    boolean productSame = false;
    Map<Long, Long> localInfoIdAndHistoryIdMap = new HashMap<Long, Long>();
    if (CollectionUtils.isNotEmpty(items)) {
      for (PurchaseOrderItem item : items) {
        localInfoIdAndHistoryIdMap.put(item.getProductId(), item.getProductHistoryId());
      }
    }
    productSame = productHistoryService.compareProductSameWithHistory(localInfoIdAndHistoryIdMap, shopId);
    StringBuffer sb = new StringBuffer("友情提示：");
    if (supplierSame && productSame) {
      return new Result("通过校验", true);
//      if(RelationChangeEnum.UNCHANGED.equals(changeEnum)){
//        return new Result("通过校验", true);
//      }else {
//        return new Result("提示", sb.toString(), false, Result.Operation.CONFIRM);
//      }
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

  @Override
  public PurchaseOrderItemDTO getPurchaseOrderItemById(Long id) {
    if (id == null || id == 0L) {
      throw new RuntimeException("getPurchaseOrderItemByIdAndShopId,id is null or 0L.");
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    PurchaseOrderItem purchaseOrderItem = txnWriter.getById(PurchaseOrderItem.class, id);
    if (purchaseOrderItem == null) {
      return null;
    }
    return purchaseOrderItem.toDTO();
  }

  @Override
  public void createOnlineSupplier(PurchaseOrderDTO purchaseOrderDTO) throws Exception{
    if(purchaseOrderDTO == null || purchaseOrderDTO.getShopId() == null
        || purchaseOrderDTO.getSupplierId() != null || purchaseOrderDTO.getSupplierShopId() == null) {
      return;
    }
    SupplierDTO supplierDTO = getUserService().getSupplierDTOBySupplierShopIdAndShopId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getSupplierShopId());
    if (supplierDTO == null) {
      IShopRelationService shopRelationService = ServiceManager.getService(IShopRelationService.class);
      ShopDTO customerShopDTO = getConfigService().getShopById(purchaseOrderDTO.getShopId());
      ShopDTO supplierShopDTO = getConfigService().getShopById(purchaseOrderDTO.getSupplierShopId());
      supplierDTO = shopRelationService.collectSupplierShop(customerShopDTO, supplierShopDTO);
    }
    if (supplierDTO != null) {
      purchaseOrderDTO.setSupplierId(supplierDTO.getId());
      if (!ArrayUtils.isEmpty(supplierDTO.getContacts())) {
        boolean isMatchContact = false;
        if (StringUtils.isNotEmpty(purchaseOrderDTO.getContact()) || StringUtils.isNotEmpty(purchaseOrderDTO.getMobile())) {
          for (ContactDTO contactDTO : supplierDTO.getContacts()) {
            if (contactDTO != null) {
              if (StringUtil.compareSame(purchaseOrderDTO.getContact(), contactDTO.getName())
                  && StringUtil.compareSame(purchaseOrderDTO.getMobile(), contactDTO.getMobile())) {
                isMatchContact = true;
                purchaseOrderDTO.setContactId(contactDTO.getId());
              }
            }
          }
        }
        if(!isMatchContact){
          for (ContactDTO contactDTO : supplierDTO.getContacts()) {
            if (contactDTO != null) {
              purchaseOrderDTO.setContactId(contactDTO.getId());
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public List<PurchaseOrderDTO> getSimpleProcessingRelatedPurchaseOrders(Long customerShopId, Long supplierShopId) {
    List<PurchaseOrderDTO> purchaseOrderDTOs = new ArrayList<PurchaseOrderDTO>();
    if(customerShopId == null || supplierShopId == null){
      return purchaseOrderDTOs;
    }
    List<PurchaseOrder> purchaseOrders = txnDaoManager.getWriter().getProcessingRelatedPurchaseOrders(customerShopId,supplierShopId);
    if(CollectionUtils.isNotEmpty(purchaseOrders)){
      for(PurchaseOrder purchaseOrder : purchaseOrders){
        purchaseOrderDTOs.add(purchaseOrder.toDTO());
      }
    }
    return purchaseOrderDTOs;
  }
}
