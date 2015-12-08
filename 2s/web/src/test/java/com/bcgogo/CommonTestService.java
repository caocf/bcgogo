package com.bcgogo;

import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.ShopBalanceService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.notification.service.NotificationService;
import com.bcgogo.notification.service.SmsService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.service.BaseProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.product.service.ProductService;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.service.ServiceVehicleCountService;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.RFTxnService;
import com.bcgogo.txn.service.SmsRechargeService;
import com.bcgogo.txn.service.TxnService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.CustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.UnitUtil;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: dell
 * Date: 12-6-29
 * Time: 下午4:39
 * To change this template use File | Settings | File Templates.
 */
public class CommonTestService extends AbstractTest {
  public CommonTestService() {
     initServices();
  }
  private static final Logger LOG = LoggerFactory.getLogger(CommonTestService.class);
  public void initService() {
    configService = ServiceManager.getService(ConfigService.class);
    notificationService = ServiceManager.getService(NotificationService.class);
    smsRechargeService = ServiceManager.getService(SmsRechargeService.class);
    shopBalanceService = ServiceManager.getService(ShopBalanceService.class);
    searchService = ServiceManager.getService(SearchService.class);
    customerService = ServiceManager.getService(CustomerService.class);
    smsService = ServiceManager.getService(SmsService.class);
    iscService = ServiceManager.getService(ServiceVehicleCountService.class);
    rfiTxnService = ServiceManager.getService(RFTxnService.class);
    txnService = ServiceManager.getService(TxnService.class);
    userService = ServiceManager.getService(IUserService.class);
    productService = ServiceManager.getService(ProductService.class);
    baseProductService = ServiceManager.getService(BaseProductService.class);
    txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    txnWriter = txnDaoManager.getWriter();
    productSolrService = ServiceManager.getService(IProductSolrService.class);
    productDaoManager = ServiceManager.getService(ProductDaoManager.class);
    productWriter = productDaoManager.getWriter();
    orderIndexService = ServiceManager.getService(IOrderIndexService.class);
  }

  public void setTestProductModel(Map model, Double amount, Double purchasePrice, Double recommondPrice, String storageUnit,
                                  String sellUnit, Long rate, Double lowerLimit, Double upperLimit) throws Exception {
    if (model == null) {
      model = new HashMap();
    }
    if (amount != null) {
      model.put("amount", amount);
    }
    if (purchasePrice != null) {
      model.put("purchasePrice", purchasePrice);
    }
    if (recommondPrice != null) {
      model.put("recommondPrice", recommondPrice);
    }
    if (StringUtils.isNotBlank(storageUnit)) {
      model.put("storageUnit", storageUnit);
    }
    if (StringUtils.isNotBlank(sellUnit)) {
      model.put("sellUnit", sellUnit);
    }
    if (rate != null) {
      model.put("rate", rate);
    }
    if (lowerLimit != null) {
      model.put("lowerLimit", lowerLimit);
    }
    if (upperLimit != null) {
      model.put("upperLimit", upperLimit);
    }
  }

  public void setTestProductModel(Map model, String productName, String productBrand, String productModel,
                                  String productSpec, String productVehicleBrand, String productVehicleModel,
                                  String productVehicleYear, String productVehicleEngin) throws Exception {
    if (model == null) {
      model = new HashMap();
    }
    if (productName != null) {
      model.put("productName", productName);
    }
    if (productBrand != null) {
      model.put("productBrand", productBrand);
    }
    if (productModel != null) {
      model.put("productModel", productModel);
    }
    if (productSpec != null) {
      model.put("productSpec", productSpec);
    }
    if (productVehicleBrand != null) {
      model.put("productVehicleBrand", productVehicleBrand);
    }
    if (productVehicleModel != null) {
      model.put("productVehicleModel", productVehicleModel);
    }
    if (productVehicleYear != null) {
      model.put("productVehicleYear", productVehicleYear);
    }
    if (productVehicleEngin != null) {
      model.put("productVehicleEngin", productVehicleEngin);
    }

  }


  /**
   * @param model
   * @param productLocalInfoId 将需要用的测试数据放到model中去,用key,value保存.
   */
  public void testProductId(Map model, Long productLocalInfoId, Long shopId) throws Exception {
//    initService();
    Thread.sleep(500L);
    ProductDTO productDTO = productService.getProductByProductLocalInfoId(productLocalInfoId, shopId);
    Assert.assertNotNull(productDTO);
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(productLocalInfoId, shopId);
    Assert.assertNotNull(productLocalInfoDTO);
    InventorySearchIndexDTO inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId, productLocalInfoId);
    Assert.assertNotNull(inventorySearchIndexDTO);
    ProductDTO productSolrDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productDTO.getId());
    Assert.assertNotNull(productSolrDTO);
    InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopId, productLocalInfoId);
    Assert.assertNotNull(inventoryDTO);
    boolean isTestOrder = model.get("isTestOrder") != null && "true".equals(model.get("isTestOrder").toString()) ? true : false;
    List<RepairOrderItem> repairOrderItems = new ArrayList<RepairOrderItem>();
    List<SalesOrderItem> salesOrderItems = new ArrayList<SalesOrderItem>();
    List<PurchaseInventoryItem> purchaseInventoryItems = new ArrayList<PurchaseInventoryItem>();
    List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<PurchaseOrderItem>();
    List<PurchaseReturnItem> purchaseReturnItems = new ArrayList<PurchaseReturnItem>();
    List<RepairRemindEvent> repairRemindEvents = new ArrayList<RepairRemindEvent>();
    List<InventoryRemindEvent> inventoryRemindEvents = new ArrayList<InventoryRemindEvent>();
    if (isTestOrder) {
      repairOrderItems = txnService.getRepairOrderItemByProductId(shopId, productLocalInfoId, null);
      salesOrderItems = txnService.getSalesOrderItemByProductId(shopId, productLocalInfoId, null);
      purchaseInventoryItems = txnService.getPurchaseInventoryItemByProductId(shopId, productLocalInfoId, null);
      purchaseOrderItems = txnService.getPurchaseOrderItemByProductId(shopId, productLocalInfoId, null);
      purchaseReturnItems = txnService.getPurchaseReturnItemByProdctId(shopId, productLocalInfoId, null);
      repairRemindEvents = txnService.getRepairRemindEventByProductId(shopId, productLocalInfoId);
      inventoryRemindEvents = txnService.getInventoryRemindEventByProductId(shopId, productLocalInfoId);
    }

    Double expectAmount = (Double) model.get("amount");
    Double expectPurchasePrice = (Double) model.get("purchasePrice");
    Double expectRecommondPrice = (Double) model.get("recommondPrice");
    String expectStorageUnit = model.get("storageUnit") == null ? null : model.get("storageUnit").toString();
    String expectSellUnit = model.get("sellUnit") == null ? null : model.get("sellUnit").toString();
    Long expectRate = (Long) model.get("rate");
    Double expectLowerLimit = (Double) model.get("lowerLimit");
    Double expectUpperLimit = (Double) model.get("upperLimit");


    String expectProductName = model.get("productName") == null ? null : model.get("productName").toString();
    String expectProductBrand = model.get("productBrand") == null ? null : model.get("productBrand").toString();
    String expectProductModel = model.get("productModel") == null ? null : model.get("productModel").toString();
    String expectProductSpec = model.get("productSpec") == null ? null : model.get("productSpec").toString();
    String expectProductVehicleBrand = model.get("productVehicleBrand") == null ? null : model.get("productVehicleBrand").toString();
    String expectProductVehicleModel = model.get("productVehicleModel") == null ? null : model.get("productVehicleModel").toString();
    String expectProductVehicleYear = model.get("productVehicleYear") == null ? null : model.get("productVehicleYear").toString();
    String expectProductVehicleEngin = model.get("productVehicleEngin") == null ? null : model.get("productVehicleEngin").toString();

    //检查库存重量是否正确
    if (expectAmount != null) {
      Assert.assertEquals(expectAmount, inventorySearchIndexDTO.getAmount(), 0.0001);
      Assert.assertEquals(expectAmount, productSolrDTO.getInventoryNum(), 0.0001);
      Assert.assertEquals(expectAmount, inventoryDTO.getAmount(), 0.0001);
    }
    //检查最新入库价是否正确
    if (expectPurchasePrice != null) {
      Assert.assertEquals(expectPurchasePrice, inventorySearchIndexDTO.getPurchasePrice(), 0.0001);
      Assert.assertEquals(expectPurchasePrice, productSolrDTO.getPurchasePrice(), 0.0001);
    }
    //检查设定销售价是否正确
    if (expectRecommondPrice != null) {
      Assert.assertEquals(expectRecommondPrice, inventorySearchIndexDTO.getRecommendedPrice(), 0.0001);
      Assert.assertEquals(expectRecommondPrice, productSolrDTO.getRecommendedPrice(), 0.01);
    }
    //检查库存大单位是否正确
    if (expectStorageUnit != null) {
      if (StringUtils.isNotEmpty(expectStorageUnit)) {
      Assert.assertEquals(expectStorageUnit, productSolrDTO.getStorageUnit());
      } else {
        Assert.assertNull("".equals(productSolrDTO.getStorageUnit())?null:productSolrDTO.getStorageUnit());
      }

      Assert.assertEquals(expectStorageUnit, productLocalInfoDTO.getStorageUnit());
    }
    //检查销售小单位是否正确
    if (expectSellUnit != null) {
      if (StringUtils.isNotEmpty(expectSellUnit)) {
      Assert.assertEquals(expectSellUnit, productSolrDTO.getSellUnit());
      } else {
        Assert.assertNull("".endsWith(productSolrDTO.getSellUnit())?null:productSolrDTO.getSellUnit());
      }
      Assert.assertEquals(expectSellUnit, productSolrDTO.getSellUnit());
      Assert.assertEquals(expectSellUnit, productLocalInfoDTO.getSellUnit());
      Assert.assertEquals(expectSellUnit, inventorySearchIndexDTO.getSellUnit());
      Assert.assertEquals(expectSellUnit, inventoryDTO.getUnit());
    }
    //检查单位换算比例是否正确
    if (expectRate != null) {
      Assert.assertEquals(expectRate, productLocalInfoDTO.getRate(), 0.0001);
      Assert.assertEquals(expectRate, productSolrDTO.getRate(), 0.0001);
    }
    //检查库存下限是否正确
    if (expectLowerLimit != null) {
      Assert.assertEquals(expectLowerLimit, inventoryDTO.getLowerLimit(), 0.0001);
      Assert.assertEquals(expectLowerLimit, inventorySearchIndexDTO.getLowerLimit(), 0.0001);
    }
    //检查库存上限是否正确
    if (expectUpperLimit != null) {
      Assert.assertEquals(expectUpperLimit, inventoryDTO.getUpperLimit(), 0.0001);
      Assert.assertEquals(expectUpperLimit, inventorySearchIndexDTO.getUpperLimit(), 0.0001);
    }
    //检查库存品名是否正确
    if (expectProductName != null) {
      Assert.assertEquals(expectProductName, productDTO.getName());
      Assert.assertEquals(expectProductName, inventorySearchIndexDTO.getProductName());
      Assert.assertEquals(expectProductName, productSolrDTO.getName());
    }
    //检查库存品牌是否正确
    if (expectProductBrand != null) {
      Assert.assertEquals(expectProductBrand, productDTO.getBrand());
      Assert.assertEquals(expectProductBrand, inventorySearchIndexDTO.getProductBrand());
      if (StringUtils.isNotEmpty(expectProductBrand)) {
      Assert.assertEquals(expectProductBrand, productSolrDTO.getBrand());
      } else {
        Assert.assertNull("".equals(productSolrDTO.getBrand())?null:productSolrDTO.getBrand());
    }
    }
    //检查库存商品型号是否正确
    if (expectProductModel != null) {
      Assert.assertEquals(expectProductModel, productDTO.getModel());
      Assert.assertEquals(expectProductModel, inventorySearchIndexDTO.getProductModel());
      if (StringUtils.isNotBlank(expectProductModel)) {
        Assert.assertEquals(expectProductModel, productSolrDTO.getModel());
      } else {
        Assert.assertNull("".equals(productSolrDTO.getModel())?null:productSolrDTO.getModel());
      }
    }
    //检查库存规格是否正确
    if (expectProductSpec != null) {
      Assert.assertEquals(expectProductSpec, productDTO.getSpec());
      Assert.assertEquals(expectProductSpec, inventorySearchIndexDTO.getProductSpec());
      if (StringUtils.isNotEmpty(expectProductSpec)) {
      Assert.assertEquals(expectProductSpec, productSolrDTO.getSpec());
      } else {
        Assert.assertNull("".equals(productSolrDTO.getSpec())?null:productSolrDTO.getSpec());
    }

    }
    //检查库存商品车辆品牌是否正确
    if (expectProductVehicleBrand != null && !("全部").equals(expectProductVehicleBrand) &&
        !("多款").equals(expectProductVehicleBrand)) {
      Assert.assertEquals(expectProductVehicleBrand, productDTO.getProductVehicleBrand());
      Assert.assertEquals(expectProductVehicleBrand, inventorySearchIndexDTO.getBrand());
      if (StringUtils.isNotBlank(expectProductVehicleBrand)) {
        Assert.assertEquals(expectProductVehicleBrand, productSolrDTO.getProductVehicleBrand());
      } else {
        Assert.assertNull("".endsWith(productSolrDTO.getProductVehicleBrand())?null:productSolrDTO.getProductVehicleBrand());
      }
    }
    //检查库存商品车辆型号是否正确
    if (expectProductVehicleModel != null && !("全部").equals(expectProductVehicleModel) &&
        !("多款").equals(expectProductVehicleModel)) {
      Assert.assertEquals(expectProductVehicleModel, productDTO.getProductVehicleModel());
      Assert.assertEquals(expectProductVehicleModel, inventorySearchIndexDTO.getModel());
      if (StringUtils.isNotBlank(expectProductModel)) {
        Assert.assertEquals(expectProductVehicleModel, productSolrDTO.getProductVehicleModel());
      } else {
        Assert.assertNull("".equals(productSolrDTO.getProductVehicleModel())?null:productSolrDTO.getProductVehicleModel());
      }
    }
    if (isTestOrder) {
      //检查入库单产品信息是否正确
      if (CollectionUtils.isNotEmpty(purchaseInventoryItems)) {
        for (PurchaseInventoryItem purchaseInventoryItem : purchaseInventoryItems) {
          if (expectProductVehicleBrand != null) {
            Assert.assertEquals(expectProductVehicleBrand, purchaseInventoryItem.getVehicleBrand());
          }
          if (expectProductVehicleModel != null) {
            Assert.assertEquals(expectProductVehicleModel, purchaseInventoryItem.getVehicleModel());
          }
          if (expectProductVehicleYear != null) {
            Assert.assertEquals(expectProductVehicleYear, purchaseInventoryItem.getVehicleYear());
          }
          if (expectProductVehicleEngin != null) {
            Assert.assertEquals(expectProductVehicleEngin, purchaseInventoryItem.getVehicleEngine());
          }
        }
      }
      //检查采购单产品信息是否正确
      if (CollectionUtils.isNotEmpty(purchaseOrderItems)) {
        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
          if (expectProductVehicleBrand != null) {
            Assert.assertEquals(expectProductVehicleBrand, purchaseOrderItem.getVehicleBrand());
          }
          if (expectProductVehicleModel != null) {
            Assert.assertEquals(expectProductVehicleModel, purchaseOrderItem.getVehicleModel());
          }
          if (expectProductVehicleYear != null) {
            Assert.assertEquals(expectProductVehicleYear, purchaseOrderItem.getVehicleYear());
          }
          if (expectProductVehicleEngin != null) {
            Assert.assertEquals(expectProductVehicleEngin, purchaseOrderItem.getVehicleEngine());
          }
        }
      }

      //检查维修提醒事件产品信息是否正确
      if (CollectionUtils.isNotEmpty(repairRemindEvents)) {
        for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
          if (expectProductVehicleBrand != null) {
            Assert.assertEquals(expectProductName, repairRemindEvent.getProductName());
          }
          if (expectProductVehicleModel != null) {
            Assert.assertEquals(expectProductBrand, repairRemindEvent.getProductBrand());
          }
          if (expectProductVehicleYear != null) {
            Assert.assertEquals(expectProductModel, repairRemindEvent.getProductModel());
          }
          if (expectProductVehicleEngin != null) {
            Assert.assertEquals(expectProductSpec, repairRemindEvent.getProductSpec());
          }
          if (expectProductVehicleBrand != null) {
            Assert.assertEquals(expectProductVehicleBrand, repairRemindEvent.getVehicleBrand());
          }
          if (expectProductVehicleModel != null) {
            Assert.assertEquals(expectProductVehicleModel, repairRemindEvent.getVehicleModel());
          }
          if (expectProductVehicleYear != null) {
            Assert.assertEquals(expectProductVehicleYear, repairRemindEvent.getVehicleYear());
          }
          if (expectProductVehicleEngin != null) {
            Assert.assertEquals(expectProductVehicleEngin, repairRemindEvent.getVehicleEngine());
          }
        }
      }
      //检查采购提醒产品信息是否正确
      if (CollectionUtils.isNotEmpty(inventoryRemindEvents)) {
        for (InventoryRemindEvent inventoryRemindEvent : inventoryRemindEvents) {
          if (expectProductVehicleBrand != null) {
            Assert.assertEquals(expectProductName, inventoryRemindEvent.getProductName());
          }
          if (expectProductVehicleModel != null) {
            Assert.assertEquals(expectProductBrand, inventoryRemindEvent.getProductBrand());
          }
          if (expectProductVehicleYear != null) {
            Assert.assertEquals(expectProductModel, inventoryRemindEvent.getProductModel());
          }
          if (expectProductVehicleEngin != null) {
            Assert.assertEquals(expectProductSpec, inventoryRemindEvent.getProductSpec());
          }
        }
      }
    }
    OrderTestDTO orderTestDTO = new OrderTestDTO();
    if (isTestOrder) {
      List<PurchaseInventoryItem> activePurchaseInventoryItem = txnService.getPurchaseInventoryItemByProductId(shopId,
          productLocalInfoId, OrderStatus.PURCHASE_INVENTORY_DONE);
      if (CollectionUtils.isNotEmpty(activePurchaseInventoryItem)) {
        for (PurchaseInventoryItem purchaseInventoryItem : activePurchaseInventoryItem) {
          if (UnitUtil.isStorageUnit(purchaseInventoryItem.getUnit(), productDTO)) {
            orderTestDTO.setActivePurchaseInvenoryAmount(orderTestDTO.getActivePurchaseInvenoryAmount()
                + purchaseInventoryItem.getAmount() * productDTO.getRate());
          } else {
            orderTestDTO.setActivePurchaseInvenoryAmount(orderTestDTO.getActivePurchaseInvenoryAmount()
                + purchaseInventoryItem.getAmount());
          }
        }
      }
      List<PurchaseInventoryItem> cancelPurchaseInventoryItem = txnService.getPurchaseInventoryItemByProductId(shopId,
          productLocalInfoId, OrderStatus.PURCHASE_INVENTORY_REPEAL);
      if (CollectionUtils.isNotEmpty(cancelPurchaseInventoryItem)) {
        for (PurchaseInventoryItem purchaseInventoryItem : cancelPurchaseInventoryItem) {
          if (UnitUtil.isStorageUnit(purchaseInventoryItem.getUnit(), productDTO)) {
            orderTestDTO.setCancelPurchaseInventoryAmount(orderTestDTO.getCancelPurchaseInventoryAmount()
                + purchaseInventoryItem.getAmount() * productDTO.getRate());
          } else {
            orderTestDTO.setCancelPurchaseInventoryAmount(orderTestDTO.getCancelPurchaseInventoryAmount()
                + purchaseInventoryItem.getAmount());
          }
        }
      }

      List<SalesOrderItem> activeSalesOrderItem = txnService.getSalesOrderItemByProductId(shopId, productLocalInfoId,
          OrderStatus.SALE_DONE);
      if (CollectionUtils.isNotEmpty(activeSalesOrderItem)) {
        for (SalesOrderItem orderItem : activeSalesOrderItem) {
          if (UnitUtil.isStorageUnit(orderItem.getUnit(), productDTO)) {
            orderTestDTO.setActiveSalesOrderAmount(orderTestDTO.getActiveSalesOrderAmount()
                + orderItem.getAmount() * productDTO.getRate());
          } else {
            orderTestDTO.setActiveSalesOrderAmount(orderTestDTO.getActiveSalesOrderAmount()
                + orderItem.getAmount());
          }
        }
      }

      List<SalesOrderItem> cancelSalesOrderItem = txnService.getSalesOrderItemByProductId(shopId, productLocalInfoId,
          OrderStatus.SALE_DONE);
      if (CollectionUtils.isNotEmpty(cancelSalesOrderItem)) {
        for (SalesOrderItem orderItem : cancelSalesOrderItem) {
          if (UnitUtil.isStorageUnit(orderItem.getUnit(), productDTO)) {
            orderTestDTO.setCancelSalesOrderAmount(orderTestDTO.getCancelSalesOrderAmount()
                + orderItem.getAmount() * productDTO.getRate());
          } else {
            orderTestDTO.setCancelSalesOrderAmount(orderTestDTO.getCancelSalesOrderAmount()
                + orderItem.getAmount());
          }
        }
      }

      List<RepairOrderItem> activeRepaiOrderItem = txnService.getRepairOrderItemByProductId(shopId, productLocalInfoId,
          OrderStatus.REPAIR_DISPATCH);
      if (CollectionUtils.isEmpty(activePurchaseInventoryItem)) {
        activeRepaiOrderItem = new ArrayList<RepairOrderItem>();
      }
      List<RepairOrderItem> activeRepaiOrderDoneItem = txnService.getRepairOrderItemByProductId(shopId, productLocalInfoId,
          OrderStatus.REPAIR_DONE);
      if (CollectionUtils.isNotEmpty(activeRepaiOrderDoneItem)) {
        activeRepaiOrderItem.addAll(activeRepaiOrderDoneItem);
      }
      List<RepairOrderItem> activeRepaiOrderSettledItem = txnService.getRepairOrderItemByProductId(shopId, productLocalInfoId,
          OrderStatus.REPAIR_SETTLED);
      if (CollectionUtils.isNotEmpty(activeRepaiOrderSettledItem)) {
        activeRepaiOrderItem.addAll(activeRepaiOrderSettledItem);
      }
      if (CollectionUtils.isNotEmpty(activeRepaiOrderItem)) {
        for (RepairOrderItem orderItem : activeRepaiOrderItem) {
          if (UnitUtil.isStorageUnit(orderItem.getUnit(), productDTO)) {
            orderTestDTO.setActiveRepairOrderAmount(orderTestDTO.getCancelSalesOrderAmount()
                + orderItem.getReserved() * productDTO.getRate());
          } else {
            orderTestDTO.setActiveRepairOrderAmount(orderTestDTO.getCancelSalesOrderAmount()
                + orderItem.getReserved());
          }
        }
      }

      List<RepairOrderItem> cancelRepaiOrderItem = txnService.getRepairOrderItemByProductId(shopId, productLocalInfoId,
          OrderStatus.REPAIR_REPEAL);
      if (CollectionUtils.isNotEmpty(cancelRepaiOrderItem)) {
        for (RepairOrderItem orderItem : cancelRepaiOrderItem) {
          if (UnitUtil.isStorageUnit(orderItem.getUnit(), productDTO)) {
            orderTestDTO.setCancelRepairOrderAmount(orderTestDTO.getCancelRepairOrderAmount()
                + orderItem.getReserved() * productDTO.getRate());
          } else {
            orderTestDTO.setCancelRepairOrderAmount(orderTestDTO.getCancelRepairOrderAmount()
                + orderItem.getReserved());
          }
        }
      }

      List<PurchaseReturnItem> activePurchaseReturnItem = txnService.getPurchaseReturnItemByProdctId(shopId, productLocalInfoId, 0L);
      if (CollectionUtils.isNotEmpty(activePurchaseReturnItem)) {
        for (PurchaseReturnItem orderItem : activePurchaseReturnItem) {
          if (UnitUtil.isStorageUnit(orderItem.getUnit(), productDTO)) {
            orderTestDTO.setCancelRepairOrderAmount(orderTestDTO.getCancelRepairOrderAmount()
                + orderItem.getAmount() * productDTO.getRate());
          } else {
            orderTestDTO.setCancelRepairOrderAmount(orderTestDTO.getCancelRepairOrderAmount()
                + orderItem.getAmount());
          }
        }
      }


      if (expectAmount != null) {
        Assert.assertEquals(expectAmount, orderTestDTO.getInventoryAmount(), 0.0001);
      }
    }
  }


  public void setTestSupplierModel(Map model, Long shopId, String supplierName, String abbr, Long categotry, String contact,
                                   String mobile, String landline, String fax, String email, String address, String qq, String bank, String account,
                                   Long invoiceCateGoryId, Long settlementTypeId, String accountName, String business, String firstLetters,
                                   Double totalInventoryAmount, Long lastOrderId, String lastOrderProducts, Long lastOrderTime, String lastOrderType) {
    if (model == null) {
      model = new HashMap();
    }
    if (shopId != null) {
      model.put("shopId", shopId);
    }
    if (supplierName != null) {
      model.put("supplierName", supplierName);
    }
    if (abbr != null) {
      model.put("abbr", abbr);//简称
    }
    if (categotry != null) {
      model.put("category", categotry);//客户类型
    }
    if (contact != null) {
      model.put("contact", contact);    //联系人
    }
    if (mobile != null) {
      model.put("mobile", mobile); //手机
    }
    if (landline != null) {
      model.put("landline", landline);   //座机
    }
    if (fax != null) {
      model.put("fax", fax);   //座机
    }
    if (email != null) {
      model.put("email", email); //邮箱
    }
    if (address != null) {
      model.put("address", address); //地址
    }
    if (qq != null) {
      model.put("qq", qq); //qq
    }
    if (bank != null) {
      model.put("bank", bank); //银行
    }
    if (account != null) {
      model.put("account", account); //银行帐号
    }
    if (invoiceCateGoryId != null) {
      model.put("invoiceCateGoryId", invoiceCateGoryId); //发票类型
    }
    if (settlementTypeId != null) {
      model.put("settlementTypeId", settlementTypeId); //结算方式
    }
    if (accountName != null) {
      model.put("accountName", accountName);//开户名
    }
    if (business != null) {
      model.put("business", business);//业务范围
    }
    if (firstLetters != null) {
      model.put("firstLetters", firstLetters);//首字母
    }
    if (totalInventoryAmount != null) {
      model.put("totalInventoryAmount", totalInventoryAmount);//总共入库金额
    }
    if (lastOrderId != null) {
      model.put("lastOrderId", lastOrderId);//最后一次单据ID
    }
    if (lastOrderProducts != null) {
      model.put("lastOrderProducts", lastOrderProducts);//最后单据商品名
    }
    if (lastOrderTime != null) {
      model.put("lastOrderTime", lastOrderTime);//最后单据时间
    }
    if (lastOrderType != null) {
      model.put("lastOrderType", lastOrderType);//最后单据类型
    }
  }

  //model 里可以放supplierName,mobile,total,status  用作测试
  public void testPurchaseInventoryId(Map model, Long purchaseInventoryId, Long shopId) throws Exception {
//    initService();
    Thread.sleep(500L);
    List<PurchaseInventory> purchaseInventories = txnDaoManager.getWriter().getPurchaseInventoryById(purchaseInventoryId, shopId);
    Assert.assertEquals(1, purchaseInventories.size());
    PurchaseInventory purchaseInventory = purchaseInventories.get(0);
    List<PurchaseInventoryItem> purchaseInventoryItems = txnDaoManager.getWriter().getPurchaseInventoryItemsByInventoryId(purchaseInventoryId);
    List<SupplierDTO> suppliers = userService.getSupplierById(shopId, purchaseInventory.getSupplierId());
    Assert.assertEquals(1, suppliers.size());
    SupplierDTO supplierDTO = suppliers.get(0);
    List<OrderIndexDTO> orderIndexDTOs = searchService.getOrderIndexDTOByOrderId(shopId, purchaseInventoryId);
    Assert.assertEquals(1, orderIndexDTOs.size());
    List<ItemIndexDTO> itemIndexesDTOs = searchService.getItemIndexDTOListByOrderId(shopId, purchaseInventoryId);
    Assert.assertEquals(purchaseInventoryItems.size(), itemIndexesDTOs.size());

    for (ItemIndexDTO itemIndexDTO : itemIndexesDTOs) {
      Assert.assertEquals(supplierDTO.getId(), itemIndexDTO.getCustomerId());
      Assert.assertEquals(supplierDTO.getName(), itemIndexDTO.getCustomerOrSupplierName());
    }

    List<OrderIndexDTO> solrOrderIndexDTOs = orderIndexService.getByOrderId(shopId, purchaseInventoryId);
    if (CollectionUtils.isEmpty(solrOrderIndexDTOs)) {
      System.out.print("第一次从solr中读取orderIndex 信息失败,等待2秒重试..................");
      Thread.sleep(2000L);
      solrOrderIndexDTOs = orderIndexService.getByOrderId(shopId, purchaseInventoryId);
    }
    Assert.assertEquals(1, solrOrderIndexDTOs.size());


    String expectSupplierName = model.get("supplierName") == null ? null : model.get("supplierName").toString();
    String expectMobile = model.get("mobile") == null ? null : model.get("mobile").toString();
    Double expectTotal = (Double) model.get("total");
    OrderStatus expectStatus = (OrderStatus) model.get("status");
    if (expectSupplierName != null) {
      Assert.assertEquals(expectSupplierName, purchaseInventory.getSupplier());
      Assert.assertEquals(expectSupplierName, solrOrderIndexDTOs.get(0).getCustomerOrSupplierName());
      Assert.assertEquals(expectSupplierName, orderIndexDTOs.get(0).getCustomerOrSupplierName());
    }
    if (expectMobile != null) {
      Assert.assertEquals(expectMobile, solrOrderIndexDTOs.get(0).getContactNum().substring(1,solrOrderIndexDTOs.get(0).getContactNum().length()  - 1));
      Assert.assertEquals(expectMobile, orderIndexDTOs.get(0).getContactNum());
    }
//    Assert.assertEquals(purchaseInventory.getSupplierId(), orderIndexDTOs.get(0).getCustomerOrSupplierId());
//    Assert.assertEquals(purchaseInventory.getSupplierId(), solrOrderIndexDTOs.get(0).getCustomerOrSupplierId());
    if (expectTotal != null) {
      Assert.assertEquals(expectTotal, purchaseInventory.getTotal(), 0.0001);
      Assert.assertEquals(expectTotal, orderIndexDTOs.get(0).getOrderTotalAmount(), 0.0001);
      Assert.assertEquals(expectTotal, solrOrderIndexDTOs.get(0).getOrderTotalAmount(), 0.0001);
      double actualTotal = 0;
      for (PurchaseInventoryItem item : purchaseInventoryItems) {
        actualTotal += item.getTotal();
        Assert.assertEquals(purchaseInventoryId, item.getPurchaseInventoryId());
      }
      Assert.assertEquals(expectTotal, actualTotal);
    }
    if (expectStatus != null) {
      Assert.assertEquals(expectStatus, purchaseInventory.getStatusEnum());
      Assert.assertEquals(expectStatus, orderIndexDTOs.get(0).getOrderStatus());
      if (OrderStatus.PURCHASE_INVENTORY_DONE.equals(expectStatus)) {
        Assert.assertEquals(OrderStatus.PURCHASE_INVENTORY_DONE, solrOrderIndexDTOs.get(0).getOrderStatus());
      } else if (OrderStatus.PURCHASE_INVENTORY_REPEAL.equals(expectStatus)) {
        Assert.assertEquals(OrderStatus.PURCHASE_INVENTORY_REPEAL, solrOrderIndexDTOs.get(0).getOrderStatus());
      } else {
        Assert.assertNull(solrOrderIndexDTOs.get(0).getOrderStatus());
      }
    }
  }

  // /model 里可以放amount,price,total,vehicleBrand,vehicleModel,vehicleYear,vehicleEngin,unit  用作测试
  public void testPurchaseInventoryItemId(Map model, Long purchaseInventoryItemId) throws Exception {
//    initService();
    Thread.sleep(500L);
    PurchaseInventoryItem purchaseInventoryItem = txnDaoManager.getWriter().getById(PurchaseInventoryItem.class, purchaseInventoryItemId);
    ItemIndex itemIndex = searchService.getItemIndexByOrderIdAndItemIdAndOrderType(purchaseInventoryItem.getPurchaseInventoryId(),
        purchaseInventoryItemId, OrderTypes.INVENTORY);
    Assert.assertNotNull(purchaseInventoryItem);
    Assert.assertNotNull(itemIndex);

    Double expectAmount = (Double) model.get("amount");
    Double expectPrice = (Double) model.get("price");
    Double expectTotal = (Double) model.get("total");
    String expectVehicleBrand = model.get("vehicleBrand") == null ? null : model.get("vehicleBrand").toString();
    String expectVehicleModel = model.get("vehicleModel") == null ? null : model.get("vehicleModel").toString();
    String expectUnit = model.get("unit") == null ? null : model.get("unit").toString();
    Assert.assertEquals(purchaseInventoryItem.getProductId(), itemIndex.getProductId());
    if (expectAmount != null) {
      Assert.assertEquals(expectAmount, purchaseInventoryItem.getAmount(), 0.0001);
      Assert.assertEquals(expectAmount, itemIndex.getItemCount(), 0.0001);
    }
    if (expectPrice != null) {
      Assert.assertEquals(expectPrice, purchaseInventoryItem.getPrice(), 0.0001);
      Assert.assertEquals(expectPrice, itemIndex.getItemPrice(), 0.0001);
    }
    if (expectTotal != null) {
      Assert.assertEquals(expectTotal, purchaseInventoryItem.getTotal(), 0.0001);
      Assert.assertEquals(expectTotal, itemIndex.getOrderTotalAmount(), 0.0001);
    }
    if (expectVehicleBrand != null) {
      Assert.assertEquals(expectVehicleBrand, purchaseInventoryItem.getVehicleBrand());
      Assert.assertEquals(expectVehicleBrand, itemIndex.getVehicleBrand());
    }
    if (expectVehicleModel != null) {
      Assert.assertEquals(expectVehicleModel, purchaseInventoryItem.getVehicleModel());
      Assert.assertEquals(expectVehicleModel, itemIndex.getVehicleModel());
    }
    if (expectUnit != null) {
      Assert.assertEquals(expectUnit, purchaseInventoryItem.getUnit());
      Assert.assertEquals(expectUnit, itemIndex.getUnit());
    }
  }

  //创建supplierDTO 信息，并将这些信息放到测试model中待测
  public SupplierDTO createSupplier(Map model) {
    if (model == null) {
      model = new HashMap();
    }
    SupplierDTO supplierDTO = new SupplierDTO();
    String supplierName = "供应商" + getRandomLengthChiness(5) + getCharacterAndNumber(5);
    supplierDTO.setName(supplierName);
    String abbr = getCharacterAndNumber(3) + getRandomLengthChiness(5);
    supplierDTO.setAbbr(abbr);
    Long categotry = new Random().nextInt(3) + 1L;
    supplierDTO.setCategory(categotry);
    String contact = getRandomLengthChiness(5);
    supplierDTO.setContact(contact);
    String mobile = "1" + getNumberStr(10);
    supplierDTO.setMobile(mobile);
    String landline = "0512-" + getNumberStr(8);
    supplierDTO.setLandLine(landline);
    String fax = getNumberStr(10);
    supplierDTO.setFax(fax);
    String email = getCharacterAndNumber(10) + "@" + getCharacterAndNumber(5) + ".com";
    supplierDTO.setEmail(email);
    String address = getFixedLengthChinese(20);
    supplierDTO.setAddress(address);
    String qq = getNumberStr(10);
    supplierDTO.setQq(qq);
    String bank = getFixedLengthChinese(6) + "银行";
    supplierDTO.setBank(bank);
    String account = getFixedLengthChinese(6);
    supplierDTO.setAccount(account);
    Long invoiceCateGoryId = new Random().nextInt(3) + 1L;
    supplierDTO.setInvoiceCategoryId(invoiceCateGoryId);
    Long settlementTypeId = new Random().nextInt(3) + 1L;
    supplierDTO.setSettlementTypeId(settlementTypeId);
    String accountName = getFixedLengthChinese(4);
    supplierDTO.setAccountName(accountName);
    String business = getFixedLengthChinese(30);
    supplierDTO.setBusinessScope(business);
    String firstLetters = supplierDTO.getFirstLetters();
    setTestSupplierModel(model, null, supplierName, abbr, categotry, contact, mobile, landline, fax, email, address, qq, bank, account,
        invoiceCateGoryId, settlementTypeId, accountName, business, firstLetters, null, null, null, null, null);
    return supplierDTO;
  }

	public CustomerDTO createCustomerDTO(Map model) {
		if (model == null) {
			model = new HashMap();
		}
		CustomerDTO customerDTO = new CustomerDTO();
		String customerName = "客户" + getRandomLengthChiness(5) + getCharacterAndNumber(5);
		customerDTO.setName(customerName);
		model.put("name", customerName);
		String contact = getRandomLengthChiness(5);
		customerDTO.setContact(contact);
		model.put("contact", contact);
		String mobile = "1" + getNumberStr(10);
		customerDTO.setMobile(mobile);
		model.put("mobile", mobile);
		String landline = "0512-" + getNumberStr(8);
		customerDTO.setLandLine(landline);
		model.put("landline", landline);
		String fax = getNumberStr(10);
		customerDTO.setFax(fax);
		model.put("fax", fax);
		String email = getCharacterAndNumber(10) + "@" + getCharacterAndNumber(5) + ".com";
		customerDTO.setEmail(email);
		model.put("email", email);
		String address = getFixedLengthChinese(20);
		customerDTO.setAddress(address);
		model.put("address", address);
		String qq = getNumberStr(10);
		customerDTO.setQq(qq);
		model.put("qq", qq);
		String bank = getFixedLengthChinese(6) + "银行";
		customerDTO.setBank(bank);
		model.put("bank", bank);
		String account = getFixedLengthChinese(6);
		customerDTO.setAccount(account);
		model.put("account", account);
		String firstLetters = customerDTO.getFirstLetters();
	 model.put("firstLetters", firstLetters);
		return customerDTO;
	}

  //创建productDTO 信息，并将这些信息放到测试model中待测
  public ProductDTO createProduct(Map productModel) throws Exception {
    ProductDTO productDTO = new ProductDTO();
    String name = getFixedLengthChinese(3) + getRandomLengthChiness(4) + getCharacterAndNumber(3);
    productDTO.setName(name);
    String brand = getFixedLengthChinese(3) + getRandomLengthChiness(4) + getCharacterAndNumber(3);
    productDTO.setBrand(brand);
    String model = getFixedLengthChinese(3) + getRandomLengthChiness(4) + getCharacterAndNumber(3);
    productDTO.setModel(model);
    String spec = getFixedLengthChinese(3) + getRandomLengthChiness(4) + getCharacterAndNumber(3);
    productDTO.setSpec(spec);
    productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
    String pvBrand = getFixedLengthChinese(3) + getRandomLengthChiness(4);
    productDTO.setProductVehicleBrand(pvBrand);
    String pvModel = getFixedLengthChinese(3) + getRandomLengthChiness(4);
    productDTO.setProductVehicleModel(pvModel);
    setTestProductModel(productModel, name, brand, model, spec, pvBrand, pvModel, null, null);
    return productDTO;
  }

  public PurchaseInventoryItemDTO createPurchaseInventoryItemDTO(Map itemModel, Map productModel, ProductDTO productDTO, double price, double amount,
                                  String unit,Double lowerLimit,Double upperLimit,double recommendedPrice)throws Exception{
	  if(itemModel==null){
		  itemModel = new HashMap();
	  }
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setProductId(productDTO.getProductLocalInfoId());
    purchaseInventoryItemDTO.setProductName(productDTO.getName());
    purchaseInventoryItemDTO.setModel(productDTO.getModel());
    purchaseInventoryItemDTO.setBrand(productDTO.getBrand());
    purchaseInventoryItemDTO.setSpec(productDTO.getSpec());
    purchaseInventoryItemDTO.setProductVehicleStatus(productDTO.getProductVehicleStatus());
    purchaseInventoryItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
    purchaseInventoryItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
    purchaseInventoryItemDTO.setRate(productDTO.getRate());
    purchaseInventoryItemDTO.setStorageUnit(productDTO.getStorageUnit());
    purchaseInventoryItemDTO.setSellUnit(productDTO.getSellUnit());
    purchaseInventoryItemDTO.setUnit(unit);
    purchaseInventoryItemDTO.setPurchasePrice(price);
    purchaseInventoryItemDTO.setAmount(amount);
    purchaseInventoryItemDTO.setTotal(price * amount);
    purchaseInventoryItemDTO.setLowerLimit(lowerLimit);
    purchaseInventoryItemDTO.setUpperLimit(upperLimit);
    purchaseInventoryItemDTO.setRecommendedPrice(recommendedPrice);
    setTestProductModel(productModel, amount, price, recommendedPrice, unit, unit, null, lowerLimit, upperLimit);
    itemModel.put("amount", amount);
    itemModel.put("price", price);
    itemModel.put("total", price * amount);
    itemModel.put("unit", unit);
    itemModel.put("vehicleBrand", purchaseInventoryItemDTO.getVehicleBrand());
    itemModel.put("vehicleModel", purchaseInventoryItemDTO.getVehicleModel());
    return purchaseInventoryItemDTO;
  }

  public PurchaseInventoryDTO createPurchaseInventoryDTO(SupplierDTO supplierDTO, PurchaseInventoryItemDTO purchaseInventoryItemDTO) {
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setVestDateStr("2012-03-20 10:00");
    purchaseInventoryDTO.setSupplierDTO(supplierDTO);
    PurchaseInventoryItemDTO[] purchaseInventoryItemDTOs = new PurchaseInventoryItemDTO[1];
    purchaseInventoryItemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(purchaseInventoryItemDTOs);
    double total = 0;
    if(purchaseInventoryDTO.getItemDTOs() !=null && purchaseInventoryDTO.getItemDTOs().length>0){
      for(PurchaseInventoryItemDTO itemDTO :purchaseInventoryDTO.getItemDTOs()){
        total +=itemDTO.getTotal();
      }
    }
    purchaseInventoryDTO.setTotal(total);
    return  purchaseInventoryDTO;
  }

	public PurchaseInventoryDTO addPurchaseInventoryItemDTO(PurchaseInventoryDTO purchaseInventoryDTO,
	                                                        PurchaseInventoryItemDTO purchaseInventoryItemDTO) {
		if (purchaseInventoryDTO == null) {
			purchaseInventoryDTO = new PurchaseInventoryDTO();
		}
		PurchaseInventoryItemDTO[] purchaseInventoryItemDTOs = purchaseInventoryDTO.getItemDTOs();
		if (purchaseInventoryItemDTOs == null) {
			purchaseInventoryItemDTOs = new PurchaseInventoryItemDTO[1];
			purchaseInventoryItemDTOs[0] = purchaseInventoryItemDTO;
			purchaseInventoryDTO.setItemDTOs(purchaseInventoryItemDTOs);
		} else {
			PurchaseInventoryItemDTO[] purchaseInventoryItemDTOs2 = new PurchaseInventoryItemDTO[purchaseInventoryItemDTOs.length + 1];
			for(int i=0;i<purchaseInventoryDTO.getItemDTOs().length;i++){
				purchaseInventoryItemDTOs2[i]=purchaseInventoryDTO.getItemDTOs()[i];
			}
			purchaseInventoryItemDTOs2[purchaseInventoryItemDTOs2.length-1] = purchaseInventoryItemDTO;
			purchaseInventoryDTO.setItemDTOs(purchaseInventoryItemDTOs2);
		}
		double total = 0;
		if (purchaseInventoryDTO.getItemDTOs() != null && purchaseInventoryDTO.getItemDTOs().length > 0) {
			for (PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()) {
				total += itemDTO.getTotal();
			}
		}
		purchaseInventoryDTO.setTotal(total);
		return purchaseInventoryDTO;
	}
  private static Random random = null;

  private Random getRandomInstance() {

    if (random == null) {
      random = new Random(new Date().getTime());
    }
    return random;
  }

  public String getChinese() {
    String str = null;
    int highPos, lowPos;
    Random random = getRandomInstance();
    highPos = (176 + Math.abs(random.nextInt(39)));
    lowPos = 161 + Math.abs(random.nextInt(93));
    byte[] b = new byte[2];
    b[0] = (new Integer(highPos)).byteValue();
    b[1] = (new Integer(lowPos)).byteValue();
    try {
      str = new String(b, "GB2312");
    } catch (UnsupportedEncodingException e) {
      LOG.error(e.getMessage(), e);
    }
    return str;
  }

  public String getFixedLengthChinese(int length) {
    String str = "";
    for (int i = length; i > 0; i--) {
      str = str + getChinese();
    }
    return str;
  }

  public String getRandomLengthChiness(int length) {
    String str = "";
    length = new Random().nextInt(length + 1);
      for (int i = 0; i < length; i++) {
        str = str + getChinese();
      }
    return str;
  }

  //随机字符数字，字母混合
  public String getCharacterAndNumber(int length)
{
    String val = "";

    Random random = new Random();
    for(int i = 0; i < length; i++)
    {
        String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

        if("char".equalsIgnoreCase(charOrNum)) // 字符串
        {
            int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
            val += (char) (choice + random.nextInt(26));
        }
        else if("num".equalsIgnoreCase(charOrNum)) // 数字
        {
            val += String.valueOf(random.nextInt(10));
        }
    }

    return val;
}

  public String getNumberStr(int length){
    String str = "";
    for(int i=0;i<length;i++){
      str += new Random(i).nextInt(10);
    }
    return str;
  }

  public void testGetReturnItemDTO(Map model, PurchaseReturnItemDTO purchaseReturnItemDTO) {
    Long productId = (Long)model.get("productId");
    if(productId !=null){
      Assert.assertEquals(productId,purchaseReturnItemDTO.getProductId());
    }
    Long supplierId = (Long)model.get("supplierId");
    if(supplierId !=null){
      Assert.assertEquals(supplierId,purchaseReturnItemDTO.getSupplierId());
    }
    String supplierName = (String)model.get("supplierName");
    if(StringUtils.isNotBlank(supplierName)){
      Assert.assertEquals(supplierName,purchaseReturnItemDTO.getSupplierName());
    }
//    Double amount = (Double)model.get("amount");
//    if(amount !=null){
//      Assert.assertEquals(amount,purchaseReturnItemDTO.getAmount(),0.0001);
//    }
    Double price = (Double)model.get("price");
    if(price!=null){
      Assert.assertEquals(price,purchaseReturnItemDTO.getPrice(),0.0001);
    }
//    Double total = (Double)model.get("total");
//    if(total!=null){
//      Assert.assertEquals(total,purchaseReturnItemDTO.getTotal(),0.0001);
//    }
    String unit = (String)model.get("unit");
    if(StringUtils.isNotBlank(unit)){
      Assert.assertEquals(unit,purchaseReturnItemDTO.getUnit());
    }

    String productName = (String) model.get("productName");
    if (StringUtils.isNotBlank(productName)) {
      Assert.assertEquals(productName, purchaseReturnItemDTO.getProductName());
    }
    String productBrand = (String) model.get("productBrand");
    if (StringUtils.isNotBlank(productBrand)) {
      Assert.assertEquals(productBrand, purchaseReturnItemDTO.getBrand());
    }
      String productModel = (String) model.get("productModel");
    if (StringUtils.isNotBlank(productModel)) {
      Assert.assertEquals(productModel, purchaseReturnItemDTO.getModel());
    }
        String productSpec = (String) model.get("productSpec");
    if (StringUtils.isNotBlank(productSpec)) {
      Assert.assertEquals(productSpec, purchaseReturnItemDTO.getSpec());
    }
    String productVehicleBrand = (String) model.get("productVehicleBrand");
   if (StringUtils.isNotBlank(productVehicleBrand)) {
     Assert.assertEquals(productVehicleBrand, purchaseReturnItemDTO.getVehicleBrand());
   }
     String productVehicleModel = (String) model.get("productVehicleModel");
   if (StringUtils.isNotBlank(productVehicleModel)) {
     Assert.assertEquals(productVehicleModel, purchaseReturnItemDTO.getVehicleModel());
   }
   Double returnAbleAmount = (Double)model.get("returnAbleAmount");
    if(returnAbleAmount !=null){
      Assert.assertEquals(returnAbleAmount,purchaseReturnItemDTO.getReturnAbleAmount(),0.0001);
    }
  }

    public void testCreateReturenItemDTO(Map model, PurchaseReturnItemDTO purchaseReturnItemDTO) {
     testGetReturnItemDTO(model, purchaseReturnItemDTO);
      Double itemAmount = (Double) model.get("itemAmount");
      if (itemAmount != null) {
        Assert.assertEquals(itemAmount, purchaseReturnItemDTO.getAmount(), 0.0001);
      }
      Double itemTotal = (Double) model.get("itemTotal");
      if (itemTotal != null) {
        Assert.assertEquals(itemTotal, purchaseReturnItemDTO.getTotal(), 0.0001);
      }
    }

  public void testCreatePurchaseReturnDTO(Map model, PurchaseReturnDTO purchaseReturnDTO){
        Long supplierId = (Long)model.get("supplierId");
    if(supplierId !=null){
      Assert.assertEquals(supplierId,purchaseReturnDTO.getSupplierId());
    }
    String supplierName = (String)model.get("supplierName");
    if(StringUtils.isNotBlank(supplierName)){
      Assert.assertEquals(supplierName,purchaseReturnDTO.getSupplier());
    }
  }

  public void testSupplierId(Map model, Long supplierId, Long shopId) {
    List<SupplierDTO> supplierDTOs = userService.getSupplierById(shopId, supplierId);
    Assert.assertEquals(1, supplierDTOs.size());
    SupplierDTO supplierDTO = supplierDTOs.get(0);
    String supplierName = (String) model.get("supplierName");
    if (StringUtils.isNotBlank(supplierName)) {
      Assert.assertEquals(supplierName, supplierDTO.getName());
    }
    String abbr = (String) model.get("abbr");
    if (StringUtils.isNotBlank(abbr)) {
      Assert.assertEquals(abbr, supplierDTO.getAbbr());
    }
    Long categotry = (Long) model.get("categotry");
    if (categotry != null) {
      Assert.assertEquals(categotry, supplierDTO.getCategory());
    }
    String contact = (String) model.get("contact");
    if (StringUtils.isNotBlank(contact)) {
      Assert.assertEquals(contact, supplierDTO.getContact());
    }
    String mobile = (String) model.get("mobile");
    if (StringUtils.isNotBlank(mobile)) {
      Assert.assertEquals(mobile, supplierDTO.getMobile());
    }
    String landline = (String) model.get("landline");
    if (StringUtils.isNotBlank(landline)) {
      Assert.assertEquals(landline, supplierDTO.getLandLine());
    }
    String fax = (String) model.get("fax");
    if (StringUtils.isNotBlank(fax)) {
      Assert.assertEquals(fax, supplierDTO.getFax());
    }
    String email = (String) model.get("email");
    if (StringUtils.isNotBlank(email)) {
      Assert.assertEquals(email, supplierDTO.getEmail());
    }
    String address = (String) model.get("address");
    if (StringUtils.isNotBlank(address)) {
      Assert.assertEquals(address, supplierDTO.getAddress());
    }
    String qq = (String) model.get("qq");
    if (StringUtils.isNotBlank(qq)) {
      Assert.assertEquals(qq, supplierDTO.getQq());
    }
    String bank = (String) model.get("bank");
    if (StringUtils.isNotBlank(bank)) {
      Assert.assertEquals(bank, supplierDTO.getBank());
    }
    String account = (String) model.get("account");
    if (StringUtils.isNotBlank(account)) {
      Assert.assertEquals(account, supplierDTO.getAccount());
    }
    String accountName = (String) model.get("accountName");
    if (StringUtils.isNotBlank(accountName)) {
      Assert.assertEquals(accountName, supplierDTO.getAccountName());
    }
    Long invoiceCateGoryId = (Long) model.get("invoiceCateGoryId");
    if (invoiceCateGoryId != null) {
      Assert.assertEquals(invoiceCateGoryId, supplierDTO.getInvoiceCategoryId());
    }
    Long settlementTypeId = (Long) model.get("settlementTypeId");
    if (settlementTypeId != null) {
      Assert.assertEquals(settlementTypeId, supplierDTO.getSettlementTypeId());
    }
    String business = (String) model.get("business");
    if (StringUtils.isNotBlank(business)) {
      Assert.assertEquals(business, supplierDTO.getBusinessScope());
    }
    String firstLetters = (String) model.get("firstLetters");
    if (StringUtils.isNotBlank(firstLetters)) {
      Assert.assertEquals(firstLetters, supplierDTO.getFirstLetters());
    }
    Double totalInventoryAmount = (Double) model.get("totalInventoryAmount");
    if (totalInventoryAmount != null) {
      Assert.assertEquals(totalInventoryAmount, supplierDTO.getTotalInventoryAmount());
    }
    Long lastOrderId = (Long) model.get("lastOrderId");
    if (lastOrderId != null) {
      Assert.assertEquals(lastOrderId, supplierDTO.getLastOrderId());
    }
//    String lastOrderProducts = (String) model.get("lastOrderProducts");
//    if (StringUtils.isNotBlank(lastOrderProducts)) {
//      Assert.assertEquals(lastOrderProducts, supplierDTO.getLastOrderProducts());
//    }
    OrderTypes lastOrderType = (OrderTypes) model.get("lastOrderType");
    if (lastOrderType!=null) {
      Assert.assertEquals(lastOrderType, supplierDTO.getLastOrderType());
    }

  }

	public SalesOrderItemDTO createSalesItemDTO(Map salesItem1,Map productModel, ProductDTO productDTO, double amount, String unit,
	                                            double purchasePrice,double salesPrice)throws Exception{
		if(salesItem1 == null){
			salesItem1 = new HashMap();
		}
		SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO();
    salesOrderItemDTO.setProductId(productDTO.getProductLocalInfoId());
    salesOrderItemDTO.setProductName(productDTO.getName());
    salesOrderItemDTO.setModel(productDTO.getModel());
    salesOrderItemDTO.setBrand(productDTO.getBrand());
    salesOrderItemDTO.setSpec(productDTO.getSpec());
    salesOrderItemDTO.setProductVehicleStatus(productDTO.getProductVehicleStatus() == null ? null : productDTO.getProductVehicleStatus().toString());
    salesOrderItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
    salesOrderItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
    salesOrderItemDTO.setRate(productDTO.getRate());
    salesOrderItemDTO.setStorageUnit(productDTO.getStorageUnit());
    salesOrderItemDTO.setSellUnit(productDTO.getSellUnit());
    salesOrderItemDTO.setUnit(unit);
    salesOrderItemDTO.setPurchasePrice(purchasePrice);
		salesOrderItemDTO.setPrice(salesPrice);
    salesOrderItemDTO.setAmount(amount);
    salesOrderItemDTO.setTotal(salesPrice * amount);
    setTestProductModel(productModel, amount, purchasePrice, salesPrice, unit, unit, null, null, null);
    salesItem1.put("amount", amount);
    salesItem1.put("price", salesPrice);
    salesItem1.put("total", salesPrice * amount);
    salesItem1.put("unit", unit);
		return salesOrderItemDTO;
	}


}
