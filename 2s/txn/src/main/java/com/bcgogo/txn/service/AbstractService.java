package com.bcgogo.txn.service;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.product.service.IBaseProductService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午10:59
 */
public class AbstractService  {
  protected IUserService userService;
  protected ITxnService txnService;
  protected ISearchService searchService;
  protected RFITxnService rfiTxnService;
  protected IProductService productService;
  protected IConfigService configService;
  protected IInventoryService inventoryService;
  protected IServiceHistoryService serviceHistoryService;
  protected IPickingService pickingService;
  protected RepairOrderCostCaculator repairOrderCostCaculator;
  protected IStoreHouseService storeHouseService;
  protected IProductHistoryService productHistoryService;
  protected IRepairService repairService;
  protected IBaseProductService baseProductService;
  protected ICustomerService customerService;
  protected IMembersService membersService;


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
      return repairOrderCostCaculator == null ? ServiceManager.getService(RepairOrderCostCaculator.class) :repairOrderCostCaculator;
    }

    public IStoreHouseService getStoreHouseService() {
      return storeHouseService == null ? ServiceManager.getService(IStoreHouseService.class) : storeHouseService;
    }

    public IProductHistoryService getProductHistoryService() {
      return productHistoryService == null ?ServiceManager.getService(IProductHistoryService.class):productHistoryService;
    }

  public IRepairService getRepairService() {
    return repairService == null ? ServiceManager.getService(IRepairService.class) : repairService;
  }

  public IBaseProductService getBaseProductService() {
    return baseProductService == null ? ServiceManager.getService(IBaseProductService.class) : baseProductService;
  }

  public ICustomerService getCustomerService() {
    return customerService == null ? ServiceManager.getService(ICustomerService.class) : customerService;
  }

  public IMembersService getMembersService() {
    return membersService == null ?ServiceManager.getService(IMembersService.class) : membersService;
  }
}
