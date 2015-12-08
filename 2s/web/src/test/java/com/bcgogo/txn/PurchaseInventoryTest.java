package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.CommonTestService;
import com.bcgogo.common.Result;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayMethod;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.Product;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.model.OrderIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Inventory;
import com.bcgogo.txn.model.SupplierRecord;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.txn.service.SupplierPayableService;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.utils.NumberUtil;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/18/12
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventoryTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    buyController = new RFGoodBuyController();
    saleController = new GoodSaleController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    searchDaoManager = ServiceManager.getService(SearchDaoManager.class);
    supplierPayableController = new SupplierPayableController();
    initTxnControllers(goodsStorageController, saleController);
  }

  /**
   * @throws Exception 入库单测试1：
   *                   1做个大单位商品入库，
   *                   2，修改这个商品的销售单位
   *                   3，以小单位入库
   *                   4，以大单位入库
   */

  @Test
  public void testCreatePurchaseInventory() throws Exception {
//    MemCacheAdapter.flushAll();
    ModelMap model = new ModelMap();
    Map testModel = new HashMap();
    Map orderTestModel = new HashMap();
    CommonTestService commonTestService = new CommonTestService();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    orderTestModel.put("supplierName", "pTest1");
    orderTestModel.put("mobile", "13012343943");
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");
    purchaseInventoryDTO.setTotal(11D);
    //页面中修改实付，挂账金额
    purchaseInventoryDTO.setStroageActuallyPaid(3d);  //实付3
    purchaseInventoryDTO.setStroageCreditAmount(5d);    //挂账5
    purchaseInventoryDTO.setStroageSupplierDeduction(3d);   //扣款，优惠3
    purchaseInventoryDTO.setCreditAmount(5d); //挂账5
    purchaseInventoryDTO.setDeduction(3d);    //扣款，优惠3
    purchaseInventoryDTO.setActuallyPaid(3d);    //实付3
    purchaseInventoryDTO.setCash(2d);      //现金2
    purchaseInventoryDTO.setBankCardAmount(1d); //银行卡1
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    purchaseInventoryItemDTO.setRecommendedPrice(5D);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setBrand("牛顿");
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(11d);
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setUnit("箱");
    orderTestModel.put("total", 11D);
    commonTestService.setTestProductModel(testModel, 25D, 1D, 5D, "箱", "箱", null, null, null);
    commonTestService.setTestProductModel(testModel, productName, "牛顿", "", "123", "", "", "", "");
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    //step1 入库一件新商品
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    orderTestModel.put("status", OrderStatus.PURCHASE_INVENTORY_DONE);
   // this.orderDropDownListTest(purchaseInventoryItemDTO.getBrand(), purchaseInventoryItemDTO.getProductName(), purchaseInventoryItemDTO.getVehicleBrand(), shopId);

    PurchaseInventoryDTO returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertNotNull(returnedPurchaseInventoryDTO.getId());

    Supplier supplier = userWriter.getSupplierByMobile(shopId, returnedPurchaseInventoryDTO.getMobile()).get(0);
    Assert.assertEquals(returnedPurchaseInventoryDTO.getId().longValue(), supplier.getLastOrderId().longValue());
    Assert.assertEquals(OrderTypes.INVENTORY.getName(), supplier.getLastOrderType());
    Assert.assertEquals(11, supplier.getTotalInventoryAmount().intValue(), 0.001);
    SupplierRecord supplierRecord = txnWriter.getSupplierRecord(shopId, supplier.getId());
    Assert.assertNotNull(supplierRecord);
    PayableDTO payableDTO = supplierPayableService.getInventoryPayable(shopId, purchaseInventoryDTO.getId(), supplier.getId());
    Assert.assertEquals(5d, payableDTO.getCreditAmount(), 0.001);
    Assert.assertEquals(3d, payableDTO.getDeduction(), 0.001);
    Assert.assertEquals(3d, payableDTO.getPaidAmount(), 0.001);   //代码将扣款视为已付
    List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = supplierPayableService.getPayableHistoryRecord(shopId, supplier.getId(), purchaseInventoryDTO.getId(),null);
    Assert.assertTrue(CollectionUtils.isNotEmpty(payableHistoryRecordDTOList));
    Assert.assertEquals(3d, payableHistoryRecordDTOList.get(0).getPaidAmount(), 0.001);
    Assert.assertEquals(3d, payableHistoryRecordDTOList.get(0).getActuallyPaid(), 0.001);
    Assert.assertEquals(5d, payableHistoryRecordDTOList.get(0).getCreditAmount(), 0.001);

    ProductLocalInfo productLocalInfo = productWriter.getById(ProductLocalInfo.class, purchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Long productLocalInfoId = productLocalInfo.getId();
    Long productId = productLocalInfo.getProductId();
    Assert.assertEquals(1, productLocalInfo.getPurchasePrice(), 0.001);
    Assert.assertEquals("箱", productLocalInfo.getStorageUnit());
    Assert.assertEquals("箱", productLocalInfo.getSellUnit());
    Object[] objects = productWriter.getProductByProductLocalInfoId(productLocalInfo.getId(), purchaseInventoryDTO.getShopId());
    Product product = (Product)objects[0];
    Assert.assertEquals(purchaseInventoryItemDTO.getProductName(), product.getName());
    Assert.assertEquals("", returnedPurchaseInventoryDTO.getItemDTOs()[0].getVehicleBrand());

    Inventory inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(25, inventory.getAmount(), 0.001);
    Assert.assertEquals("箱", inventory.getUnit());

    List<InventorySearchIndexDTO> inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, null, "123", null, null, null, null, null,
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(25, inventorySearchIndexes.get(0).getAmount(), 0.001);
    Assert.assertEquals("箱", inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(5D, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.001);

    List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
    itemOrderType.add(OrderTypes.INVENTORY);

    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(shopId);
    dto.setOrderId(returnedPurchaseInventoryDTO.getId());
    dto.setSelectedOrderTypes(itemOrderType);
    List<ItemIndex> indexes = searchService.searchItemIndex(dto,
        null,
        null,
        null,
        null);

    Assert.assertEquals(1, indexes.size());
    Assert.assertEquals(productName, indexes.get(0).getItemName());
    Assert.assertEquals("箱", indexes.get(0).getUnit());
    Assert.assertEquals(returnedPurchaseInventoryDTO.getItemDTOs()[0].getId(), indexes.get(0).getItemId());
    Assert.assertEquals(returnedPurchaseInventoryDTO.getId(), indexes.get(0).getOrderId());
    Assert.assertEquals(11D, indexes.get(0).getOrderTotalAmount(), 0.001);
    Assert.assertEquals(25, indexes.get(0).getItemCount(), 0.001);
    //checkSolr
    ProductDTO productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals("箱", productDTO.getSellUnit());
    Assert.assertEquals("箱", productDTO.getStorageUnit());
    Assert.assertEquals(25d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("牛顿", productDTO.getBrand());
    Assert.assertEquals("123", productDTO.getSpec());
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    Assert.assertEquals(1d, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(5d, productDTO.getRecommendedPrice(), 0.001);
    testModel.put("isTestOrder","true");
    commonTestService.testProductId(testModel, productLocalInfoId, shopId);

    commonTestService.testPurchaseInventoryId(orderTestModel, purchaseInventoryDTO.getId(), shopId);

    //step2 新增一个销售单位
    txnController.setSellUnitAndRate(request, response, productLocalInfoId, "箱", "个", 6L);
//
//    testModel.put("amount",25 * 6.0);
//    testModel.put("sellUnit","个");
//    testModel.put("rate",6L);
    commonTestService.setTestProductModel(testModel, 25 * 6.0, 1D / 6.0, 5D / 6.0, "箱", "个", 6L, null, null);
    commonTestService.testProductId(testModel, productLocalInfoId, shopId);

    productLocalInfo = productWriter.getById(ProductLocalInfo.class, productLocalInfoId);
    Assert.assertEquals("个", productLocalInfo.getSellUnit());
    Assert.assertEquals("箱", productLocalInfo.getStorageUnit());
    Assert.assertEquals(new Long(6l), productLocalInfo.getRate());
    Assert.assertEquals(NumberUtil.round(1.0 / 6, 2), productLocalInfo.getPurchasePrice(), 0.01);
    Assert.assertEquals(0, productLocalInfo.getPrice(), 0.001);

    inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(150d, inventory.getAmount(), 0.001);
    Assert.assertEquals("个", inventory.getUnit());

    inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, "", "123", "", "", "", "", "",
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(150d, inventorySearchIndexes.get(0).getAmount(), 0.001);
    Assert.assertEquals("个", inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(5.0 / 6, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.1);
    Assert.assertEquals(1.0 / 6, inventorySearchIndexes.get(0).getPurchasePrice());

    //checkSolr
    productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals("个", productDTO.getSellUnit());
    Assert.assertEquals("箱", productDTO.getStorageUnit());
    Assert.assertEquals(150d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("牛顿", productDTO.getBrand());
    Assert.assertEquals("123", productDTO.getSpec());
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    Assert.assertEquals(1.0 / 6, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(5.0 / 6, productDTO.getRecommendedPrice(), 0.01);
    Assert.assertEquals(6L, productDTO.getRate(), 0.0001);

    //以小单位入库
    //给供应商充值 30元
    String depositJson = "{\"cash\":\"10\",\"bankCardAmount\":\"10\",\"checkAmount\":\"10\",\"checkNo\":\"1011\",\"actuallyPaid\":\"30\",\"supplierId\":\""+supplier.getId()+"\"}";
    request.setParameter("depositDTO", depositJson);
    request.getSession().setAttribute("shopId", shopId);
    supplierPayableController.setSupplierPayableService((SupplierPayableService) supplierPayableService);
    Result result = supplierPayableController.addDeposit(model, request, response);

    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(11D);

    // 付款详细页面结算
    purchaseInventoryDTO.setPaidtype(PayMethod.PURCHASE_RETURN_SURPAY.getValue());
    purchaseInventoryDTO.setCash(3d);       //现金3
    purchaseInventoryDTO.setBankCardAmount(1d);   //银行1
    purchaseInventoryDTO.setDepositAmount(2d);    //定金2
    purchaseInventoryDTO.setActuallyPaid(6d);     //实付6
    purchaseInventoryDTO.setDeduction(2d);        //扣款2
    purchaseInventoryDTO.setCreditAmount(3d);     //挂账3
    purchaseInventoryDTO.setStroageActuallyPaid(6d);  //实付3
    purchaseInventoryDTO.setStroageCreditAmount(3d);    //挂账5
    purchaseInventoryDTO.setStroageSupplierDeduction(2d);   //扣款，优惠3


    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    purchaseInventoryDTO.setSupplierId(supplier.getId());

    purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    purchaseInventoryItemDTO.setUnit("个");
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setRecommendedPrice(2D);
    purchaseInventoryItemDTO.setBrand("牛顿");
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setTotal(11d);

    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);


    //step3 以小单位入库
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    testModel.put("amount", 175D);
    testModel.put("purchasePrice", 1D);
    testModel.put("recommondPrice", 2D);
    commonTestService.testProductId(testModel, productLocalInfoId, shopId);

    orderTestModel.put("total",11D);
    commonTestService.testPurchaseInventoryId(orderTestModel, purchaseInventoryDTO.getId(), shopId);

    supplier = userWriter.getSupplierByMobile(shopId, returnedPurchaseInventoryDTO.getMobile()).get(0);
    Assert.assertEquals(purchaseInventoryDTO.getId().longValue(), supplier.getLastOrderId().longValue());
    Assert.assertEquals(OrderTypes.INVENTORY.getName(), supplier.getLastOrderType());
    Assert.assertEquals(22, supplier.getTotalInventoryAmount().intValue(), 0.001);

    payableDTO = supplierPayableService.getInventoryPayable(shopId, purchaseInventoryDTO.getId(), supplier.getId());
    Assert.assertEquals(11d, payableDTO.getAmount(), 0.001);
    Assert.assertEquals(3d, payableDTO.getCreditAmount(), 0.001);
    Assert.assertEquals(2d, payableDTO.getDeduction(), 0.001);
    Assert.assertEquals(6d, payableDTO.getPaidAmount(), 0.001);   //代码将扣款视为已付
    payableHistoryRecordDTOList = supplierPayableService.getPayableHistoryRecord(shopId, supplier.getId(), purchaseInventoryDTO.getId(),null);
    Assert.assertEquals(1, payableHistoryRecordDTOList.size());
    Assert.assertEquals(2d, payableHistoryRecordDTOList.get(0).getDeduction(), 0.001);
    Assert.assertEquals(3d, payableHistoryRecordDTOList.get(0).getCreditAmount(), 0.001);
    Assert.assertEquals(3d, payableHistoryRecordDTOList.get(0).getCash(), 0.001);
    Assert.assertEquals(1d, payableHistoryRecordDTOList.get(0).getBankCardAmount(), 0.001);
    Assert.assertEquals(2d, payableHistoryRecordDTOList.get(0).getDepositAmount(), 0.001);
    Assert.assertEquals(6d, payableHistoryRecordDTOList.get(0).getActuallyPaid(), 0.001);
    Assert.assertEquals(6d, payableHistoryRecordDTOList.get(0).getPaidAmount(), 0.001);


    inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(175d, inventory.getAmount(), 0.001);
    Assert.assertEquals("个", inventory.getUnit());

    inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, null, "123", null, null, null, null, null,
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(175d, inventorySearchIndexes.get(0).getAmount(), 0.001);
    Assert.assertEquals("个", inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(2D, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.001);
    Assert.assertEquals(1D, inventorySearchIndexes.get(0).getPurchasePrice(), 0.01);

    //step4获得这张入库单
    goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    PurchaseInventoryItemDTO[] returnedItemDTOs = returnedPurchaseInventoryDTO.getItemDTOs();
    Assert.assertEquals(11d, returnedPurchaseInventoryDTO.getTotal(), 0.001);
    Assert.assertEquals(1, returnedItemDTOs.length);

    Assert.assertEquals(25d, returnedItemDTOs[0].getAmount());
    Assert.assertEquals("个", returnedItemDTOs[0].getUnit());
    Assert.assertEquals("箱", returnedItemDTOs[0].getStorageUnit());
    Assert.assertEquals("个", returnedItemDTOs[0].getSellUnit());
    Assert.assertEquals(new Long(6), returnedItemDTOs[0].getRate());
    Assert.assertEquals(175d, returnedItemDTOs[0].getInventoryAmount());
    Assert.assertEquals(1d, returnedItemDTOs[0].getPurchasePrice(), 0.01);
    Assert.assertEquals(productLocalInfoId, returnedItemDTOs[0].getProductId());

    dto.setShopId(shopId);
    dto.setOrderId(returnedPurchaseInventoryDTO.getId());
    dto.setSelectedOrderTypes(itemOrderType);
    //checkItemIndex
    indexes = searchService.searchItemIndex(dto,
        null,
        null,
        null,
        null);

    Assert.assertEquals(1, indexes.size());
    Assert.assertEquals(productName, indexes.get(0).getItemName());
    Assert.assertEquals("个", indexes.get(0).getUnit());
    Assert.assertEquals(returnedItemDTOs[0].getId(), indexes.get(0).getItemId());
    Assert.assertEquals(returnedItemDTOs[0].getPurchaseInventoryId(), indexes.get(0).getOrderId());
    Assert.assertEquals(11D, indexes.get(0).getOrderTotalAmount(), 0.001);
    Assert.assertEquals(25, indexes.get(0).getItemCount(), 0.001);

    //checkSolr
    productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals("个", productDTO.getSellUnit());
    Assert.assertEquals("箱", productDTO.getStorageUnit());
    Assert.assertEquals(175d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("牛顿", productDTO.getBrand());
    Assert.assertEquals("123", productDTO.getSpec());
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    Assert.assertEquals(1d, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(2d, productDTO.getRecommendedPrice(), 0.001);
    Assert.assertEquals(6L, productDTO.getRate(), 0.0001);

    //step 5 入库一个大单位商品

    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(7D);
    purchaseInventoryDTO.setSupplierId(supplier.getId());
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(1d);
    purchaseInventoryItemDTO.setPurchasePrice(7d);
    purchaseInventoryItemDTO.setUnit("箱");
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setBrand("牛顿");
    purchaseInventoryItemDTO.setRecommendedPrice(10D);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setTotal(7d);

    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    orderTestModel.put("total", 7D);
    commonTestService.testPurchaseInventoryId(orderTestModel, purchaseInventoryDTO.getId(), shopId);

    orderTestModel.put("total",7D);
    commonTestService.testPurchaseInventoryId(orderTestModel, purchaseInventoryDTO.getId(), shopId);

    returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(181d, inventory.getAmount(), 0.001);
    Assert.assertEquals("个", inventory.getUnit());

    inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, null, "123", null, null, null, null, null,
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(181d, inventorySearchIndexes.get(0).getAmount(), 0.001);
    Assert.assertEquals("个", inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(10.0 / 6, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.001);
    Assert.assertEquals(7.0 / 6, inventorySearchIndexes.get(0).getPurchasePrice(), 0.01);

    //获得这张入库单
    goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    returnedItemDTOs = returnedPurchaseInventoryDTO.getItemDTOs();

    Assert.assertEquals(7d, returnedPurchaseInventoryDTO.getTotal(), 0.001);
    Assert.assertEquals(1, returnedItemDTOs.length);
    Assert.assertEquals(1d, returnedItemDTOs[0].getAmount());
    Assert.assertEquals("箱", returnedItemDTOs[0].getUnit());
    Assert.assertEquals("箱", returnedItemDTOs[0].getStorageUnit());
    Assert.assertEquals("个", returnedItemDTOs[0].getSellUnit());
    Assert.assertEquals(new Long(6), returnedItemDTOs[0].getRate());
    Assert.assertEquals(181 / 6d, returnedItemDTOs[0].getInventoryAmount());
    Assert.assertEquals(7d, returnedItemDTOs[0].getPurchasePrice(), 0.01);
    Assert.assertEquals(productLocalInfoId, returnedItemDTOs[0].getProductId());

    dto.setShopId(shopId);
    dto.setOrderId(returnedPurchaseInventoryDTO.getId());
    dto.setSelectedOrderTypes(itemOrderType);
    //checkItemIndex
    indexes = searchService.searchItemIndex(dto,
        null,
        null,
        null,
        null);

    Assert.assertEquals(1, indexes.size());
    Assert.assertEquals(productName, indexes.get(0).getItemName());
    Assert.assertEquals("箱", indexes.get(0).getUnit());
    Assert.assertEquals(returnedItemDTOs[0].getId(), indexes.get(0).getItemId());
    Assert.assertEquals(returnedItemDTOs[0].getPurchaseInventoryId(), indexes.get(0).getOrderId());
    Assert.assertEquals(7D, indexes.get(0).getOrderTotalAmount(), 0.001);
    Assert.assertEquals(1, indexes.get(0).getItemCount(), 0.001);

    //checkSolr
    productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals("个", productDTO.getSellUnit());
    Assert.assertEquals("箱", productDTO.getStorageUnit());
    Assert.assertEquals(181d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("牛顿", productDTO.getBrand());
    Assert.assertEquals("123", productDTO.getSpec());
    Assert.assertEquals(7.0 / 6, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(10.0 / 6, productDTO.getRecommendedPrice(), 0.01);
    Assert.assertEquals(6L, productDTO.getRate(), 0.0001);

  }

  @Test
  public void recommendedPriceTest() throws Exception {
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(11D);
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(11d);
    purchaseInventoryItemDTO.setVehicleBrand("全部");
    purchaseInventoryItemDTO.setBarcode("1234567890123");

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    String productLocalInfoId = purchaseInventoryItemDTO.getProductId().toString();
    String RecommendedPrice = new Double(50.77d).toString();
    Long[] productLocalInfoIdforSearch = new Long[1];
    productLocalInfoIdforSearch[0] = new Long(productLocalInfoId);
    txnController.ajaxUpdateRecommendedPrice(request, response, productLocalInfoId, RecommendedPrice);

    InventorySearchIndex inventorySearchIndex = searchService.searchInventorySearchIndexByProductIds(shopId, productLocalInfoIdforSearch).get(0);
    Assert.assertEquals(50.77d, inventorySearchIndex.getRecommendedPrice(), 0001);

    //生成一张施工单，带出销售价
    String[] productIds = new String[1];
    productIds[0] = productLocalInfoId;
    request.setParameter("productIds", productIds);
    String repairOrderResult = txnController.getProducts(model, request);
    Assert.assertEquals("/txn/invoicing", repairOrderResult);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(50.77d, repairOrderDTO.getItemDTOs()[0].getPrice(), 0.0001);

    //生成一张销售单带出销售价
    request.setParameter("productIds", productIds);
    String salesOrderResult = saleController.getProducts(model, request, response);
    Assert.assertEquals("/txn/goodsSale", salesOrderResult);
    SalesOrderDTO salesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
    Assert.assertEquals(50.77d, salesOrderDTO.getItemDTOs()[0].getPrice(), 0.0001);

  }

  @Test
  public void cancelPurchaseInventoryTest1() throws Exception {
    //做一张入库单，再退货
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
      SupplierPayableService supplierPayableService=ServiceManager.getService(SupplierPayableService.class);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(11D);
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(11d);
    purchaseInventoryItemDTO.setVehicleBrand("全部");


    PurchaseInventoryItemDTO purchaseInventoryItemDTO2 = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO2.setAmount(23d);
    purchaseInventoryItemDTO2.setPurchasePrice(1d);
    String productName2 = "BB" + System.currentTimeMillis();
    purchaseInventoryItemDTO2.setProductName(productName2);
    purchaseInventoryItemDTO2.setSpec("45");
    purchaseInventoryItemDTO2.setTotal(11d);


    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[2];
    itemDTOs[0] = purchaseInventoryItemDTO;
    itemDTOs[1] = purchaseInventoryItemDTO2;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    goodsStorageController.setSupplierPayableService(supplierPayableService);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    goodsStorageController.cancelPurchaseInventory(model, request, purchaseInventoryDTO.getId());
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    PurchaseInventoryDTO purchaseInventoryDTOTest = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertEquals(OrderStatus.PURCHASE_INVENTORY_REPEAL, purchaseInventoryDTOTest.getStatus());

    Double inventorySearchAmount = searchService.searchInventorySearchIndexAmount(shopId, productName, null, "123", null, "全部", null, null, null, null).getAmount();
    Double inventoryAmount = txnService.getInventoryAmount(shopId, purchaseInventoryDTO.getItemDTOs()[0].getProductId()).getAmount();
    Assert.assertEquals(inventorySearchAmount, 0d, 0.001);
    Assert.assertEquals(inventoryAmount, 0d, 0.001);

    Double inventorySearchAmount2 = searchService.searchInventorySearchIndexAmount(shopId, productName2, null, "45", null, null, null, null, null,null).getAmount();
    Double inventoryAmount2 = txnService.getInventoryAmount(shopId, purchaseInventoryDTO.getItemDTOs()[0].getProductId()).getAmount();
    Assert.assertEquals(inventorySearchAmount2, 0d, 0.001);
    Assert.assertEquals(inventoryAmount2, 0d, 0.001);

    List<OrderIndex> list = searchService.getOrderIndexByOrderId(shopId, purchaseInventoryDTO.getId(), OrderTypes.INVENTORY, null, null);
    if(list != null && list.size() > 0){
       Assert.assertEquals(OrderStatus.PURCHASE_INVENTORY_REPEAL, list.get(0).getOrderStatusEnum());
    }


  }

  @Test
  public void cancelPurchaseInventoryTest2() throws Exception {
    SupplierPayableService supplierPayableService=ServiceManager.getService(SupplierPayableService.class);
    //做一张采购单，再入库，再取消入库单
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    request.getSession().setAttribute("userName", "testUser");
    request.getSession().setAttribute("userId", 100000011L);
    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();

    purchaseOrderDTO.setSupplier("supplier007");
    purchaseOrderDTO.setMobile("12234678901");
    purchaseOrderDTO.setContact("supplier008");
    purchaseOrderDTO.setVestDateStr("2012-09-20 12:20");

    PurchaseOrderItemDTO itemDTO1 = new PurchaseOrderItemDTO();
    itemDTO1.setProductName("TestProductName1");
    itemDTO1.setBrand("TestBrand1");
    itemDTO1.setSpec("TestSpec1");
    itemDTO1.setModel("TestModel1");
    itemDTO1.setVehicleBrand("VehicleBrand1");
    itemDTO1.setVehicleModel("VehicleModel1");
    itemDTO1.setVehicleEngine("2.0L");
    itemDTO1.setVehicleYear("2011");
    itemDTO1.setPrice(20.25d);
    itemDTO1.setAmount(1.6d);
    itemDTO1.setTotal(20.25 * 1.6);

    PurchaseOrderItemDTO itemDTO2 = new PurchaseOrderItemDTO();
    itemDTO2.setProductName("TestProductName2");
    itemDTO2.setBrand("TestBrand2");
    itemDTO2.setSpec("TestSpec2");
    itemDTO2.setModel("TestModel2");
    itemDTO2.setVehicleBrand("VehicleBrand2");
    itemDTO2.setVehicleModel("VehicleModel2");
    itemDTO2.setVehicleEngine("3.0L");
    itemDTO2.setVehicleYear("2012");
    itemDTO2.setPrice(33.25d);
    itemDTO2.setAmount(4.6d);
    itemDTO2.setTotal(33.25 * 4.6);

    PurchaseOrderItemDTO[] itemDTOs = new PurchaseOrderItemDTO[2];
    itemDTOs[0] = itemDTO1;
    itemDTOs[1] = itemDTO2;
    purchaseOrderDTO.setItemDTOs(itemDTOs);
    purchaseOrderDTO.setTotal(itemDTO1.getTotal() + itemDTO2.getTotal());

    buyController.save(request, model, purchaseOrderDTO);
	  buyController.show(model, purchaseOrderDTO.getId().toString(), request);
    purchaseOrderDTO = (PurchaseOrderDTO) model.get("purchaseOrderDTO");

    Long purchaseOrderId = purchaseOrderDTO.getId();
    request.setParameter("purchaseOrderId", purchaseOrderId.toString());
      goodsStorageController.setSupplierPayableService(supplierPayableService);
    goodsStorageController.create(model, request);
    PurchaseInventoryDTO purchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    purchaseInventoryDTO.setPurchaseOrderId(purchaseOrderId);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    purchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    goodsStorageController.cancelPurchaseInventory(model, request, purchaseInventoryDTO.getId());
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    PurchaseInventoryDTO purchaseInventoryDTOTest = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertEquals(purchaseInventoryDTOTest.getStatus(), OrderStatus.PURCHASE_INVENTORY_REPEAL);

    PurchaseOrderDTO purchaseOrderDTOTest = txnService.getPurchaseOrder(purchaseInventoryDTOTest.getPurchaseOrderId(), shopId);
    Assert.assertEquals(OrderStatus.PURCHASE_ORDER_WAITING,purchaseOrderDTOTest.getStatus());
    List<InventoryRemindEventDTO> inventoryRemindEventDTOs = txnService.getInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId, 0, 10);
    Assert.assertEquals(2, inventoryRemindEventDTOs.size());
    if (inventoryRemindEventDTOs.get(0).getProductName().equals("TestProductName1")) {
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductName(), "TestProductName1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductBrand(), "TestBrand1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductModel(), "TestModel1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductSpec(), "TestSpec1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getPrice(), 20.25d, 0.001d);
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getAmount(), 1.6d, 0.001d);
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductName(), "TestProductName2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductBrand(), "TestBrand2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductModel(), "TestModel2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductSpec(), "TestSpec2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getPrice(), 33.25d, 0.001d);
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getAmount(), 4.6d, 0.001d);
    } else {
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductName(), "TestProductName1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductBrand(), "TestBrand1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductModel(), "TestModel1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getProductSpec(), "TestSpec1");
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getPrice(), 20.25d, 0.001d);
      Assert.assertEquals(inventoryRemindEventDTOs.get(1).getAmount(), 1.6d, 0.001d);
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductName(), "TestProductName2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductBrand(), "TestBrand2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductModel(), "TestModel2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getProductSpec(), "TestSpec2");
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getPrice(), 33.25d, 0.001d);
      Assert.assertEquals(inventoryRemindEventDTOs.get(0).getAmount(), 4.6d, 0.001d);
    }

    Double inventorySearchAmount = searchService.searchInventorySearchIndexAmount(shopId, "TestProductName1",
        "TestBrand1", "TestSpec1", "TestModel1", "VehicleBrand1", "VehicleModel1", "2011", "2.0L",null).getAmount();
    Double inventoryAmount = txnService.getInventoryAmount(shopId, purchaseInventoryDTO.getItemDTOs()[0].getProductId()).getAmount();
    Assert.assertEquals(inventorySearchAmount, 0d, 0.001);
    Assert.assertEquals(inventoryAmount, 0d, 0.001);

    Double inventorySearchAmount2 = searchService.searchInventorySearchIndexAmount(shopId, "TestProductName2",
        "TestBrand2", "TestSpec2", "TestModel2", "VehicleBrand2", "VehicleModel2", "2012", "3.0L",null).getAmount();
    Double inventoryAmount2 = txnService.getInventoryAmount(shopId, purchaseInventoryDTO.getItemDTOs()[0].getProductId()).getAmount();
    Assert.assertEquals(inventorySearchAmount2, 0d, 0.001);
    Assert.assertEquals(inventoryAmount2, 0d, 0.001);

    List<OrderIndex> list = searchService.getOrderIndexByOrderId(shopId, purchaseInventoryDTO.getId(), OrderTypes.INVENTORY, null, null);
    Assert.assertEquals(1, list.size());
      Assert.assertEquals(OrderStatus.PURCHASE_INVENTORY_REPEAL, list.get(0).getOrderStatusEnum());

    model = new ModelMap();
    goodsStorageController.copyPurchaseInventory(model, request, response, purchaseInventoryDTO.getId());
    PurchaseInventoryDTO returnPurchaseinventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertEquals(purchaseInventoryDTOTest.getTotal(), returnPurchaseinventoryDTO.getTotal(), 0.0001);
    Assert.assertEquals(purchaseInventoryDTOTest.getSupplierId(), returnPurchaseinventoryDTO.getSupplierId());
    Assert.assertEquals(purchaseInventoryDTOTest.getSupplier(), returnPurchaseinventoryDTO.getSupplier());
    Assert.assertNull(returnPurchaseinventoryDTO.getId());
    Assert.assertNull(returnPurchaseinventoryDTO.getStatus());


    Map exceptMap = new HashMap();
    CommonTestService commonTestService = new CommonTestService();
    commonTestService.setTestProductModel(exceptMap,"TestProductName1","TestBrand1","TestModel1","TestSpec1",
        "VehicleBrand1", "VehicleModel1","2011","2.0L");
     Map actualMap = new HashMap();
    PurchaseInventoryItemDTO actualItemDTO1 = returnPurchaseinventoryDTO.getItemDTOs()[0];
    commonTestService.setTestProductModel(actualMap,actualItemDTO1.getProductName(),actualItemDTO1.getBrand(),
        actualItemDTO1.getModel(),actualItemDTO1.getSpec(),actualItemDTO1.getVehicleBrand(),
        actualItemDTO1.getVehicleModel(),actualItemDTO1.getVehicleYear(),actualItemDTO1.getVehicleEngine());
     Assert.assertEquals(exceptMap,actualMap);
    Assert.assertNull(actualItemDTO1.getId());


    exceptMap = new HashMap();
    commonTestService = new CommonTestService();
    commonTestService.setTestProductModel(exceptMap,"TestProductName2","TestBrand2","TestModel2","TestSpec2",
        "VehicleBrand2", "VehicleModel2","2012","3.0L");
      actualMap = new HashMap();
    actualItemDTO1 = returnPurchaseinventoryDTO.getItemDTOs()[1];
    commonTestService.setTestProductModel(actualMap,actualItemDTO1.getProductName(),actualItemDTO1.getBrand(),
        actualItemDTO1.getModel(),actualItemDTO1.getSpec(),actualItemDTO1.getVehicleBrand(),
        actualItemDTO1.getVehicleModel(),actualItemDTO1.getVehicleYear(),actualItemDTO1.getVehicleEngine());
     Assert.assertEquals(exceptMap,actualMap);
    Assert.assertNull(actualItemDTO1.getId());


  }


  /**
   * @throws Exception 入库单测试2：
   *                   1，做个无单位商品入库，
   *                   2，为该商品做一个小单位入库
   *                   3，增加一个大单位
   *                   4，以大单位入库
   */

  @Test
  public void testCreatePurchaseInventory2() throws Exception {
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    Long productLocalInfoId;
    Long productId;
    request.getSession().setAttribute("shopId", shopId);
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(11D);
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    purchaseInventoryItemDTO.setRecommendedPrice(5D);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(11d);
    purchaseInventoryItemDTO.setBrand("");
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setUnit("");

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    //step1 入库一件新商品
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    PurchaseInventoryDTO returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertNotNull(returnedPurchaseInventoryDTO.getId());

    Supplier supplier = userWriter.getSupplierByMobile(shopId, returnedPurchaseInventoryDTO.getMobile()).get(0);
    Assert.assertEquals(returnedPurchaseInventoryDTO.getId().longValue(), supplier.getLastOrderId().longValue());
    Assert.assertEquals(OrderTypes.INVENTORY.getName(), supplier.getLastOrderType());
    Assert.assertEquals(11, supplier.getTotalInventoryAmount().intValue(), 0.001);


    ProductLocalInfo productLocalInfo = productWriter.getById(ProductLocalInfo.class, purchaseInventoryDTO.getItemDTOs()[0].getProductId());
    productLocalInfoId = productLocalInfo.getId();
    productId = productLocalInfo.getProductId();
    Assert.assertEquals(1, productLocalInfo.getPurchasePrice(), 0.01);
    Assert.assertEquals(null, productLocalInfo.getStorageUnit());
    Assert.assertEquals(null, productLocalInfo.getSellUnit());
    Object[] objects = productWriter.getProductByProductLocalInfoId(productLocalInfo.getId(), purchaseInventoryDTO.getShopId());
    Product product = (Product)objects[0];
    Assert.assertEquals(purchaseInventoryItemDTO.getProductName(), product.getName());
    Assert.assertEquals("", returnedPurchaseInventoryDTO.getItemDTOs()[0].getVehicleBrand());

    Inventory inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(25, inventory.getAmount(), 0.001);
    Assert.assertEquals(null, inventory.getUnit());

    List<InventorySearchIndexDTO> inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, null, "123", null, null, null, null, null,
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(25, inventorySearchIndexes.get(0).getAmount(), 0.01);
    Assert.assertEquals(null, inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(5D, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.01);

    List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
    itemOrderType.add(OrderTypes.INVENTORY);

    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(shopId);
    dto.setOrderId(returnedPurchaseInventoryDTO.getId());
    dto.setSelectedOrderTypes(itemOrderType);
    List<ItemIndex> indexes = searchService.searchItemIndex(dto,
        null,
        null,
        null,
        null);

    Assert.assertEquals(1, indexes.size());
    Assert.assertEquals(productName, indexes.get(0).getItemName());
    Assert.assertEquals("", indexes.get(0).getUnit());
    Assert.assertEquals(returnedPurchaseInventoryDTO.getItemDTOs()[0].getId(), indexes.get(0).getItemId());
    Assert.assertEquals(returnedPurchaseInventoryDTO.getId(), indexes.get(0).getOrderId());
    Assert.assertEquals(11D, indexes.get(0).getOrderTotalAmount(), 0.001);
    Assert.assertEquals(25, indexes.get(0).getItemCount(), 0.001);

    //checkSolr
    ProductDTO productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals(null, productDTO.getSellUnit());
    Assert.assertEquals(null, productDTO.getStorageUnit());
    Assert.assertEquals(25d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("123", productDTO.getSpec());
    Assert.assertEquals(1.0, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(5.0, productDTO.getRecommendedPrice(), 0.001);

    //step2 为该商品做一个小单位入库
    //***
    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(25D);
    purchaseInventoryDTO.setSupplierId(supplier.getId());
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    purchaseInventoryItemDTO.setUnit("个");
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setRecommendedPrice(2D);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(25d);
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setProductId(productLocalInfoId);

    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertNotNull(returnedPurchaseInventoryDTO.getId());

    supplier = userWriter.getSupplierByMobile(shopId, returnedPurchaseInventoryDTO.getMobile()).get(0);
    Assert.assertEquals(purchaseInventoryDTO.getId().longValue(), supplier.getLastOrderId().longValue());
    Assert.assertEquals(OrderTypes.INVENTORY.getName(), supplier.getLastOrderType());
    Assert.assertEquals(36, supplier.getTotalInventoryAmount().intValue(), 0.001);


    inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(50d, inventory.getAmount(), 0.001);
    Assert.assertEquals("个", inventory.getUnit());

    inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, null, "123", null, null, null, null, null,
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(50d, inventorySearchIndexes.get(0).getAmount(), 0.001);
    Assert.assertEquals("个", inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(2D, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.001);
    Assert.assertEquals(1D, inventorySearchIndexes.get(0).getPurchasePrice(), 0.01);

    //获得这张入库单
    goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    PurchaseInventoryItemDTO[] returnedItemDTOs = returnedPurchaseInventoryDTO.getItemDTOs();
    Assert.assertEquals(25d, returnedPurchaseInventoryDTO.getTotal(), 0.001);
    Assert.assertEquals(1, returnedItemDTOs.length);

    Assert.assertEquals(25d, returnedItemDTOs[0].getAmount());
    Assert.assertEquals("个", returnedItemDTOs[0].getUnit());
    Assert.assertEquals("个", returnedItemDTOs[0].getStorageUnit());
    Assert.assertEquals("个", returnedItemDTOs[0].getSellUnit());
    Assert.assertEquals(true, returnedItemDTOs[0].getRate() == null);
    Assert.assertEquals(50d, returnedItemDTOs[0].getInventoryAmount());
    Assert.assertEquals(1d, returnedItemDTOs[0].getPurchasePrice(), 0.01);
    Assert.assertEquals(productLocalInfoId, returnedItemDTOs[0].getProductId());

    dto.setShopId(shopId);
    dto.setOrderId(returnedPurchaseInventoryDTO.getId());
    dto.setSelectedOrderTypes(itemOrderType);
    //checkItemIndex
    indexes = searchService.searchItemIndex(dto,
        null,
        null,
        null,
        null);

    Assert.assertEquals(1, indexes.size());
    Assert.assertEquals(productName, indexes.get(0).getItemName());
    Assert.assertEquals("个", indexes.get(0).getUnit());
    Assert.assertEquals(returnedItemDTOs[0].getId(), indexes.get(0).getItemId());
    Assert.assertEquals(returnedItemDTOs[0].getPurchaseInventoryId(), indexes.get(0).getOrderId());
    Assert.assertEquals(25D, indexes.get(0).getOrderTotalAmount(), 0.001);
    Assert.assertEquals(25, indexes.get(0).getItemCount(), 0.001);

    //checkSolr
    productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals("个", productDTO.getSellUnit());
    Assert.assertEquals("个", productDTO.getStorageUnit());
    Assert.assertEquals(50d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("123", productDTO.getSpec());
    Assert.assertEquals(1.0, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(2.0, productDTO.getRecommendedPrice(), 0.001);

    //***

    //3 增加一个大单位
    txnController.setSellUnitAndRate(request, response, productLocalInfoId, "箱", "个", 6L);

    productLocalInfo = productWriter.getById(ProductLocalInfo.class, productLocalInfoId);
    Assert.assertEquals("个", productLocalInfo.getSellUnit());
    Assert.assertEquals("箱", productLocalInfo.getStorageUnit());
    Assert.assertEquals(new Long(6l), productLocalInfo.getRate());
    Assert.assertEquals(1, productLocalInfo.getPurchasePrice(), 0.01);
    Assert.assertEquals(0, productLocalInfo.getPrice(), 0.001);

    inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(50d, inventory.getAmount(), 0.001);
    Assert.assertEquals("个", inventory.getUnit());

    inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, null, "123", null, null, null, null, null,
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(50d, inventorySearchIndexes.get(0).getAmount(), 0.001);
    Assert.assertEquals("个", inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(2, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.001);
    Assert.assertEquals(1, inventorySearchIndexes.get(0).getPurchasePrice(), 0.01);

    //checkSolr
    productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals("个", productDTO.getSellUnit());
    Assert.assertEquals("箱", productDTO.getStorageUnit());
    Assert.assertEquals(6L, productDTO.getRate(), 0.0001);
    Assert.assertEquals(50d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("123", productDTO.getSpec());
    Assert.assertEquals(1.0, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(2.0, productDTO.getRecommendedPrice(), 0.001);

    //4.以大单位入库
    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(10D);
    purchaseInventoryDTO.setSupplierId(supplier.getId());
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(2d);
    purchaseInventoryItemDTO.setPurchasePrice(5d);
    purchaseInventoryItemDTO.setUnit("箱");
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setRecommendedPrice(12D);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setBrand("");
    purchaseInventoryItemDTO.setTotal(10d);
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    supplier = userWriter.getSupplierByMobile(shopId, returnedPurchaseInventoryDTO.getMobile()).get(0);
    Assert.assertEquals(purchaseInventoryDTO.getId().longValue(), supplier.getLastOrderId().longValue());
    Assert.assertEquals(OrderTypes.INVENTORY.getName(), supplier.getLastOrderType());
    Assert.assertEquals(46, supplier.getTotalInventoryAmount(), 0.001);


    inventory = txnWriter.getById(Inventory.class, returnedPurchaseInventoryDTO.getItemDTOs()[0].getProductId());
//    Assert.assertEquals(62d, inventory.getAmount(), 0.001);    todo later
    Assert.assertEquals("个", inventory.getUnit());

    inventorySearchIndexes = searchService.searchInventorySearchIndex(shopId, productName, "", "123", "", "", "", "", "",
        0, 10, true);
    Assert.assertEquals(1, inventorySearchIndexes.size());
    Assert.assertEquals(62d, inventorySearchIndexes.get(0).getAmount(), 0.001);
    Assert.assertEquals("个", inventorySearchIndexes.get(0).getUnit());
    Assert.assertEquals(2D, inventorySearchIndexes.get(0).getRecommendedPrice(), 0.01);
    Assert.assertEquals(5.0 / 6, inventorySearchIndexes.get(0).getPurchasePrice(), 0.01);

    //step4获得这张入库单
    goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    returnedItemDTOs = returnedPurchaseInventoryDTO.getItemDTOs();
    Assert.assertEquals(10d, returnedPurchaseInventoryDTO.getTotal(), 0.001);
    Assert.assertEquals(1, returnedItemDTOs.length);

    Assert.assertEquals(2d, returnedItemDTOs[0].getAmount());
    Assert.assertEquals("箱", returnedItemDTOs[0].getUnit());
    Assert.assertEquals("箱", returnedItemDTOs[0].getStorageUnit());
    Assert.assertEquals("个", returnedItemDTOs[0].getSellUnit());
    Assert.assertEquals(new Long(6), returnedItemDTOs[0].getRate());
    Assert.assertEquals(62.0 / 6, returnedItemDTOs[0].getInventoryAmount());
    Assert.assertEquals(5, returnedItemDTOs[0].getPurchasePrice(), 0.01);
    Assert.assertEquals(productLocalInfoId, returnedItemDTOs[0].getProductId());

    dto.setShopId(shopId);
    dto.setOrderId(returnedPurchaseInventoryDTO.getId());
    dto.setSelectedOrderTypes(itemOrderType);
    //checkItemIndex
    indexes = searchService.searchItemIndex(dto,
        null,
        null,
        null,
        null);

    Assert.assertEquals(1, indexes.size());
    Assert.assertEquals(productName, indexes.get(0).getItemName());
    Assert.assertEquals("箱", indexes.get(0).getUnit());
    Assert.assertEquals(returnedItemDTOs[0].getId(), indexes.get(0).getItemId());
    Assert.assertEquals(returnedItemDTOs[0].getPurchaseInventoryId(), indexes.get(0).getOrderId());
    Assert.assertEquals(10, indexes.get(0).getOrderTotalAmount(), 0.001);
    Assert.assertEquals(2, indexes.get(0).getItemCount(), 0.001);

    //checkSolr
    productDTO = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, productId);
    Assert.assertEquals("个", productDTO.getSellUnit());
    Assert.assertEquals("箱", productDTO.getStorageUnit());
    Assert.assertEquals(6L, productDTO.getRate(), 0.0001);
    Assert.assertEquals(62d, productDTO.getInventoryNum(), 0.001);
    Assert.assertEquals(productName, productDTO.getName());
    Assert.assertEquals("123", productDTO.getSpec());
    Assert.assertEquals(5.0 / 6, productDTO.getPurchasePrice(), 0.001);
    Assert.assertEquals(2.0, productDTO.getRecommendedPrice(), 0.001);

  }

  @Test
  public void testCreatePurchaseInventory3() throws Exception {
    ModelMap model = new ModelMap();
    Map orderModel = new HashMap();
    Map productModel = new HashMap();
    Map itemModel = new HashMap();
    CommonTestService commonTestService = new CommonTestService();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(11D);
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    purchaseInventoryItemDTO.setRecommendedPrice(5D);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(11d);
    purchaseInventoryItemDTO.setBrand("");
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setLowerLimit(5D);
    purchaseInventoryItemDTO.setUpperLimit(10D);
    purchaseInventoryItemDTO.setUnit("");

    orderModel.put("mobile", "13012343943");
    orderModel.put("contact", "HHH");
    orderModel.put("total", 11D);
    orderModel.put("supplierName", "pTest1");
    orderModel.put("status", OrderStatus.PURCHASE_INVENTORY_DONE);
    itemModel.put("amount", 25d);
    itemModel.put("price", 1d);
    itemModel.put("total", 11d);
    itemModel.put("vehicleBrand", "");
    itemModel.put("vehicleModel", "");
    itemModel.put("unit", "");
    productModel.put("isTestOrder", "true");
    commonTestService.setTestProductModel(productModel, productName, "", "", "123", "", "", "", "");
    commonTestService.setTestProductModel(productModel, 25d, 1d, 5d, "", "", null, 5D, 10D);
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    //step1 入库一件新商品
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());


    PurchaseInventoryDTO returnPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");

    Long productLocalInfoId =returnPurchaseInventoryDTO.getItemDTOs()[0].getProductId();
    commonTestService.testProductId(productModel, productLocalInfoId, shopId);
    commonTestService.testPurchaseInventoryId(orderModel, returnPurchaseInventoryDTO.getId(), shopId);
    commonTestService.testPurchaseInventoryItemId(itemModel, returnPurchaseInventoryDTO.getItemDTOs()[0].getId());

    //step2 为该商品做一个小单位入库
    //***
    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(25D);
    purchaseInventoryDTO.setSupplierId(returnPurchaseInventoryDTO.getSupplierId());
    purchaseInventoryDTO.setShopId(shopId);

    purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(1d);
    purchaseInventoryItemDTO.setUnit("个");
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setRecommendedPrice(2D);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(25d);
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setLowerLimit(4D);
    purchaseInventoryItemDTO.setUpperLimit(7D);
    purchaseInventoryItemDTO.setProductId(productLocalInfoId);

    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    orderModel.put("mobile", "13012343943");
    orderModel.put("contact", "HHH");
    orderModel.put("total", 25D);
    orderModel.put("supplierName", "pTest1");
    orderModel.put("status", OrderStatus.PURCHASE_INVENTORY_DONE);
    itemModel.put("amount", 25d);
    itemModel.put("price", 1d);
    itemModel.put("total", 25d);
    itemModel.put("vehicleBrand", "");
    itemModel.put("vehicleModel", "");
    itemModel.put("unit", "个");
    productModel.put("isTestOrder", "true");
    commonTestService.setTestProductModel(productModel, productName, "", "", "123", "", "", "", "");
    commonTestService.setTestProductModel(productModel, 50d, 1d, 2d, "个", "个", null, 4D, 7D);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    PurchaseInventoryDTO returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");

    commonTestService.testProductId(productModel, productLocalInfoId, shopId);
    commonTestService.testPurchaseInventoryId(orderModel, returnedPurchaseInventoryDTO.getId(), shopId);
    commonTestService.testPurchaseInventoryItemId(itemModel, returnedPurchaseInventoryDTO.getItemDTOs()[0].getId());


    //3 增加一个大单位
    txnController.setSellUnitAndRate(request, response, productLocalInfoId, "箱", "个", 6L);
   commonTestService.setTestProductModel(productModel,50d,1d,2d,"箱", "个", 6L,4D,7D);
   commonTestService.testProductId(productModel, productLocalInfoId, shopId);

    //4.以大单位入库
    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setTotal(10D);
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");
    purchaseInventoryDTO.setSupplierId(returnedPurchaseInventoryDTO.getSupplierId());

    purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(2d);
    purchaseInventoryItemDTO.setPurchasePrice(5d);
    purchaseInventoryItemDTO.setUnit("箱");
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setRecommendedPrice(12D);
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setBrand("");
    purchaseInventoryItemDTO.setTotal(10d);
    purchaseInventoryItemDTO.setModel("");
    purchaseInventoryItemDTO.setVehicleBrand("");
    purchaseInventoryItemDTO.setVehicleEngine("");
    purchaseInventoryItemDTO.setVehicleModel("");
    purchaseInventoryItemDTO.setVehicleYear("");
    purchaseInventoryItemDTO.setLowerLimit(4D);
    purchaseInventoryItemDTO.setUpperLimit(7D);
    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);


    orderModel.put("mobile", "13012343943");
    orderModel.put("contact", "HHH");
    orderModel.put("total", 10D);
    orderModel.put("supplierName", "pTest1");
    orderModel.put("status", OrderStatus.PURCHASE_INVENTORY_DONE);
    itemModel.put("amount", 2d);
    itemModel.put("price", 5d);
    itemModel.put("total", 10d);
    itemModel.put("vehicleBrand", "");
    itemModel.put("vehicleModel", "");
    itemModel.put("unit", "箱");
    productModel.put("isTestOrder", "true");
    commonTestService.setTestProductModel(productModel, productName, "", "", "123", "", "", "", "");
    commonTestService.setTestProductModel(productModel, 62d, 5.0 / 6, 2d, "箱", "个", 6L, 4D * 6, 7D * 6);


    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());

    returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    commonTestService.testProductId(productModel, productLocalInfoId, shopId);
    commonTestService.testPurchaseInventoryId(orderModel, returnedPurchaseInventoryDTO.getId(), shopId);
    commonTestService.testPurchaseInventoryItemId(itemModel, returnedPurchaseInventoryDTO.getItemDTOs()[0].getId());


  }
}
