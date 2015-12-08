package com.bcgogo.txn;

import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.remind.message.AbstractMessageController;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.service.IServiceVehicleCountService;
import com.bcgogo.txn.html.ActiveRecommendSupplierHtmlBuilder;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.client.IClientApplyService;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.pushMessage.IApplyPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-7-5
 * Time: 下午7:21
 */
public class AbstractTxnController extends AbstractMessageController {
  @Autowired
  protected ITxnService txnService;
  @Autowired
  protected RFITxnService rfiTxnService;
//  @Autowired
//  protected IGoodsBuyService goodsBuyService;
//  @Autowired
//  protected IGoodsIndexService goodsIndexService;
  @Autowired
  protected IGoodsStorageService goodsStorageService;

  @Autowired
  protected ActiveRecommendSupplierHtmlBuilder activeRecommendSupplierHtmlBuilder;
//  @Autowired
//  protected IWashService washService;
//  @Autowired
//  protected IRepairService repairService;
  @Autowired
  protected IGoodSaleService goodSaleService;
//  @Autowired
//  protected IGoodsReturnService goodsReturnService;
//
//  @Autowired
//  protected IGeneralTxnService generalTxnService;
  @Autowired
  protected IConfigService configService;
  @Autowired
  protected IItemIndexService itemIndexService;
  @Autowired
  protected ISearchService searchService;
  @Autowired
  protected IOrderIndexService orderIndexService;
  @Autowired
  protected IUserService userService;
  @Autowired
  protected IProductService productService;
  @Autowired
  protected IInventoryService inventoryService;
  @Autowired
  protected ICustomerService customerService;
  @Autowired
  protected IProductSolrService productSolrService;
  @Autowired
  protected ISmsService smsService;
  @Autowired
  protected ISupplierPayableService supplierPayableService;
  @Autowired
  protected ISupplierService supplierService;
	@Autowired
	protected IProductCurrentUsedService productCurrentUsedService;
  @Autowired
  protected IMembersService membersService;
  @Autowired
  protected IProductHistoryService productHistoryService;
  @Autowired
  protected IServiceVehicleCountService serviceVehicleCountService;
  @Autowired
  protected IRepairService repairService;
  @Autowired
  protected IInsuranceService insuranceService;
  @Autowired
  protected IPickingService pickingService;
  @Autowired
  protected IApplyService applyService;
  @Autowired
  protected IClientApplyService clientApplyService;
  @Autowired
  protected IApplyPushMessageService applyPushMessageService;
  @Autowired
  protected IPushMessageService pushMessageService;

  @Autowired
  protected ICustomerOrSupplierSolrWriteService supplierSolrWriteService;
  @Autowired
  private RemindEventStrategySelector remindEventStrategySelector;
  @Autowired
  protected IProductInStorageService productInStorageService;
  @Autowired
  protected IProductOutStorageService productOutStorageService;
  @Autowired
  protected IProductThroughService productThroughService;

  @Autowired
  protected IOperationLogService operationLogService;

  public void setSupplierService(ISupplierService supplierService) {
    this.supplierService = supplierService;
  }

  public void setSupplierPayableService(ISupplierPayableService supplierPayableService) {
    this.supplierPayableService = supplierPayableService;
  }

//  public void setGeneralTxnService(IGeneralTxnService generalTxnService) {
//    this.generalTxnService = generalTxnService;
//  }
  public void setTxnService(ITxnService txnService) {
    this.txnService = txnService;
  }

  public void setConfigService(IConfigService configService) {
    this.configService = configService;
  }

  public void setItemIndexService(IItemIndexService itemIndexService) {
    this.itemIndexService = itemIndexService;
  }

  public void setSearchService(ISearchService searchService) {
    this.searchService = searchService;
  }

  public void setOrderIndexService(IOrderIndexService orderIndexService) {
    this.orderIndexService = orderIndexService;
  }

  public void setUserService(IUserService userService) {
    this.userService = userService;
  }

  public void setProductService(IProductService productService) {
    this.productService = productService;
  }

  public void setInventoryService(IInventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  public void setCustomerService(ICustomerService customerService) {
    this.customerService = customerService;
  }

  public void setProductSolrService(IProductSolrService productSolrService) {
    this.productSolrService = productSolrService;
  }

  public void setSmsService(ISmsService smsService) {
    this.smsService = smsService;
  }

  public RFITxnService getRfiTxnService() {
    return rfiTxnService;
  }

  public void setRfiTxnService(RFITxnService rfiTxnService) {
    this.rfiTxnService = rfiTxnService;
  }

	public IProductCurrentUsedService getProductCurrentUsedService() {
		if(productCurrentUsedService == null){
			productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
		}
		return productCurrentUsedService;
	}

	public void setProductCurrentUsedService(IProductCurrentUsedService productCurrentUsedService) {
		this.productCurrentUsedService = productCurrentUsedService;
	}

  public void setMembersService(IMembersService membersService) {
    this.membersService = membersService;
  }

  public void setProductHistoryService(IProductHistoryService productHistoryService) {
    this.productHistoryService = productHistoryService;
  }

  //  public void setGoodsBuyService(IGoodsBuyService goodsBuyService) {
//    this.goodsBuyService = goodsBuyService;
//  }
//
//  public void setGoodsIndexService(IGoodsIndexService goodsIndexService) {
//    this.goodsIndexService = goodsIndexService;
//  }

  public void setGoodsStorageService(IGoodsStorageService goodsStorageService) {
    this.goodsStorageService = goodsStorageService;
  }

  public void setServiceVehicleCountService(IServiceVehicleCountService serviceVehicleCountService) {
    this.serviceVehicleCountService = serviceVehicleCountService;
  }

  public void setRepairService(IRepairService repairService) {
    this.repairService = repairService;
  }

  public void setInsuranceService(IInsuranceService insuranceService) {
    this.insuranceService = insuranceService;
  }

  public void setPickingService(IPickingService pickingService) {
    this.pickingService = pickingService;
  }

  public void setSupplierSolrWriteService(ICustomerOrSupplierSolrWriteService supplierSolrWriteService) {
    this.supplierSolrWriteService = supplierSolrWriteService;
  }

  public void setActiveRecommendSupplierHtmlBuilder(ActiveRecommendSupplierHtmlBuilder activeRecommendSupplierHtmlBuilder) {
    this.activeRecommendSupplierHtmlBuilder = activeRecommendSupplierHtmlBuilder;
  }

  public IOperationLogService getOperationLogService() {
    return operationLogService;
  }

  public void setOperationLogService(IOperationLogService operationLogService) {
    this.operationLogService = operationLogService;
  }

  public IProductInStorageService getProductInStorageService() {
    return productInStorageService;
  }

  public void setProductInStorageService(IProductInStorageService productInStorageService) {
    this.productInStorageService = productInStorageService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService;
  }

  public void setProductOutStorageService(IProductOutStorageService productOutStorageService) {
    this.productOutStorageService = productOutStorageService;
  }

  public IProductThroughService getProductThroughService() {
    return productThroughService;
  }

  public void setProductThroughService(IProductThroughService productThroughService) {
    this.productThroughService = productThroughService;
  }

  public IGoodSaleService getGoodSaleService() {
    return goodSaleService;
  }

  public void setGoodSaleService(IGoodSaleService goodSaleService) {
    this.goodSaleService = goodSaleService;
  }

  public ITxnService getTxnService() {
    return txnService;
  }

  public IGoodsStorageService getGoodsStorageService() {
    return goodsStorageService;
  }

  public ActiveRecommendSupplierHtmlBuilder getActiveRecommendSupplierHtmlBuilder() {
    return activeRecommendSupplierHtmlBuilder;
  }

  public IConfigService getConfigService() {
    return configService;
  }

  public IItemIndexService getItemIndexService() {
    return itemIndexService;
  }

  public ISearchService getSearchService() {
    return searchService;
  }

  public IOrderIndexService getOrderIndexService() {
    return orderIndexService;
  }

  public IUserService getUserService() {
    return userService;
  }

  public IProductService getProductService() {
    return productService;
  }

  public IInventoryService getInventoryService() {
    return inventoryService;
  }

  public ICustomerService getCustomerService() {
    return customerService;
  }

  public IProductSolrService getProductSolrService() {
    return productSolrService;
  }

  public ISmsService getSmsService() {
    return smsService;
  }

  public ISupplierPayableService getSupplierPayableService() {
    return supplierPayableService;
  }

  public ISupplierService getSupplierService() {
    return supplierService;
  }

  public IMembersService getMembersService() {
    return membersService;
  }

  public IProductHistoryService getProductHistoryService() {
    return productHistoryService;
  }

  public IServiceVehicleCountService getServiceVehicleCountService() {
    return serviceVehicleCountService;
  }

  public IRepairService getRepairService() {
    return repairService;
  }

  public IInsuranceService getInsuranceService() {
    return insuranceService;
  }

  public IPickingService getPickingService() {
    return pickingService;
  }

  public IApplyService getApplyService() {
    return applyService;
  }

  public ICustomerOrSupplierSolrWriteService getSupplierSolrWriteService() {
    return supplierSolrWriteService;
  }

  /**
   * 将以逗号分隔的productId字符串转为productId数组, 过滤掉非数字的productId.
   * @param productIdsStr
   * @return
   */
  protected String[] convertProductIdsToArray(String productIdsStr) {
    if(StringUtils.isBlank(productIdsStr)){
      return null;
    }
    String[] productIds = productIdsStr.split(",");
    List<String> list = new ArrayList<String>();
    for(String productId : productIds){
      if(NumberUtil.isNumber(productId)){
        list.add(productId);
      }
    }
    list.toArray(productIds);
    return productIds;
  }
}
