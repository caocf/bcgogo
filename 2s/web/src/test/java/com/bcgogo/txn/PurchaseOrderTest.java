package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.Product;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.OrderSavedEvent;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.PurchaseOrderItemDTO;
import com.bcgogo.txn.model.InventoryRemindEvent;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.utils.SearchConstant;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/18/12
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseOrderTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    buyController = new RFGoodBuyController();
    goodsHistoryController = new GoodsHistoryController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    searchDaoManager = ServiceManager.getService(SearchDaoManager.class);

    initTxnControllers(goodsHistoryController);
  }

/**
 *
 */
  @Test
  public void testCreatePurchaseOrder() throws Exception {
    flushAllMemCache();
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.setSupplier("pTest");
    purchaseOrderDTO.setContact("HHH");
    purchaseOrderDTO.setMobile("13012343933");
    purchaseOrderDTO.setVestDateStr("2012-02-24 12:20");
    purchaseOrderDTO.setDeliveryDateStr("2012-02-24");
    purchaseOrderDTO.setTotal(100);
    purchaseOrderDTO.setShopId(shopId);

    PurchaseOrderItemDTO purchaseOrderItemDTO = new PurchaseOrderItemDTO();
    purchaseOrderItemDTO.setAmount(11D);
    purchaseOrderItemDTO.setPrice(1D);
    String productName = "QQQ" + System.currentTimeMillis();

    purchaseOrderItemDTO.setProductName(productName);
    purchaseOrderItemDTO.setSpec("123");
    purchaseOrderItemDTO.setTotal(11D);
    purchaseOrderItemDTO.setVehicleBrand("多款");
    purchaseOrderItemDTO.setPrice(100D);
    purchaseOrderItemDTO.setBrand("牛顿");


    PurchaseOrderItemDTO[] itemDTOs = new PurchaseOrderItemDTO[1];
    itemDTOs[0] = purchaseOrderItemDTO;
    purchaseOrderDTO.setItemDTOs(itemDTOs);
    this.flushAllMemCache();
    buyController.save(request, model, purchaseOrderDTO);
	  buyController.show(model, purchaseOrderDTO.getId().toString(), request);
    this.orderDropDownListTest(purchaseOrderItemDTO.getBrand(), purchaseOrderItemDTO.getProductName(), purchaseOrderItemDTO.getVehicleBrand(), shopId);



    PurchaseOrderDTO newPurchaseOrderDTO = (PurchaseOrderDTO) model.get("purchaseOrderDTO");
    Long orderId = newPurchaseOrderDTO.getId();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    PurchaseOrderDTO purchaseOrderDTO1 = txnService.getPurchaseOrder(orderId,shopId);
    Assert.assertNotNull(purchaseOrderDTO1);

    Supplier supplier = userWriter.getSupplierByMobile(shopId, purchaseOrderDTO.getMobile()).get(0);

    Assert.assertEquals(purchaseOrderDTO.getId().longValue(), supplier.getLastOrderId().longValue());
    Assert.assertEquals(OrderTypes.PURCHASE.getName(), supplier.getLastOrderType());
    Assert.assertEquals(0, supplier.getTotalInventoryAmount(), 0.001);

    ProductLocalInfo productLocalInfo = productWriter.getById(ProductLocalInfo.class, purchaseOrderDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(0, productLocalInfo.getPurchasePrice(), 0.001);
    Object[] objects = productWriter.getProductByProductLocalInfoId(productLocalInfo.getId(), purchaseOrderDTO.getShopId());
    Product product = (Product)objects[0];

    Assert.assertEquals(purchaseOrderItemDTO.getProductName(), product.getName());
    Assert.assertEquals("多款", newPurchaseOrderDTO.getItemDTOs()[0].getVehicleBrand());

    InventoryRemindEvent inventoryRemindEvent = txnWriter.getInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId, 0, 10).get(0);
    Assert.assertEquals(11, inventoryRemindEvent.getAmount(), 0.001);


    purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.setSupplier("pTest");
    purchaseOrderDTO.setContact("HHH");
    purchaseOrderDTO.setMobile("13012343933");
    purchaseOrderDTO.setVestDateStr("2012-02-24 12:20");
    purchaseOrderDTO.setDeliveryDateStr("2012-02-24");
    purchaseOrderDTO.setSupplierId(supplier.getId());
    purchaseOrderDTO.setTotal(100);

    purchaseOrderItemDTO = new PurchaseOrderItemDTO();
    purchaseOrderItemDTO.setPrice(1D);
    purchaseOrderItemDTO.setProductName(productName);
    purchaseOrderItemDTO.setSpec("123");
    purchaseOrderItemDTO.setVehicleBrand("多款");
    purchaseOrderItemDTO.setPrice(100D);

    purchaseOrderItemDTO.setAmount(100D);
    purchaseOrderItemDTO.setTotal(100D);
    purchaseOrderDTO.setId(null);
    itemDTOs = new PurchaseOrderItemDTO[1];
    itemDTOs[0] = purchaseOrderItemDTO;
    purchaseOrderDTO.setItemDTOs(itemDTOs);
    //now enter another order with same product
    model = new ModelMap();
    buyController.save(request, model, purchaseOrderDTO);
	  buyController.show(model, purchaseOrderDTO.getId().toString(), request);
    PurchaseOrderDTO newPurchaseOrderDTO2 = (PurchaseOrderDTO) model.get("purchaseOrderDTO");

    supplier = userWriter.getSupplierByMobile(shopId, purchaseOrderDTO.getMobile()).get(0);
    Assert.assertEquals(purchaseOrderDTO.getId().longValue(), supplier.getLastOrderId().longValue());
    Assert.assertEquals(OrderTypes.PURCHASE.getName(), supplier.getLastOrderType());
    Assert.assertEquals(0, supplier.getTotalInventoryAmount().intValue(), 0.001);

    Long orderId2 = newPurchaseOrderDTO2.getId();
    Assert.assertTrue(orderId.longValue() != orderId2.longValue());
    Assert.assertEquals(100, newPurchaseOrderDTO2.getItemDTOs()[0].getAmount(), 0.001);

    model = new ModelMap();
    buyController.purchaseOrderRepeal(model,orderId2,request);
    buyController.show(model, purchaseOrderDTO.getId().toString(), request);
    PurchaseOrderDTO newPurchaseOrderDTO3 = (PurchaseOrderDTO)model.get("purchaseOrderDTO");
    Assert.assertEquals(OrderStatus.PURCHASE_ORDER_REPEAL, newPurchaseOrderDTO3.getStatus());
  }

  @Test
  public void testCreatePurchaseOrderWithMiltiType() throws Exception {
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.setSupplier("pTest");
    purchaseOrderDTO.setContact("HHH");
    purchaseOrderDTO.setMobile("13012343933");
    purchaseOrderDTO.setVestDateStr("2012-02-24 12:20");
    purchaseOrderDTO.setDeliveryDateStr("2012-02-24");

    PurchaseOrderItemDTO purchaseOrderItemDTO = new PurchaseOrderItemDTO();
    purchaseOrderItemDTO.setAmount(11D);
    purchaseOrderItemDTO.setPrice(1D);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseOrderItemDTO.setProductName(productName);
    purchaseOrderItemDTO.setSpec("123");
    purchaseOrderItemDTO.setTotal(11D);
    purchaseOrderItemDTO.setVehicleBrand("多款");
    purchaseOrderItemDTO.setPrice(100D);


    PurchaseOrderItemDTO[] itemDTOs = new PurchaseOrderItemDTO[1];
    itemDTOs[0] = purchaseOrderItemDTO;
    purchaseOrderDTO.setItemDTOs(itemDTOs);
    buyController.save(request, model, purchaseOrderDTO);
	  buyController.show(model, purchaseOrderDTO.getId().toString(), request);
    PurchaseOrderDTO newPurchaseOrderDTO = (PurchaseOrderDTO) model.get("purchaseOrderDTO");
    Long orderId = newPurchaseOrderDTO.getId();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    PurchaseOrderDTO purchaseOrderDTO1 = txnService.getPurchaseOrder(orderId,shopId);
    Assert.assertNotNull(purchaseOrderDTO1);

    ProductLocalInfo productLocalInfo = productWriter.getById(ProductLocalInfo.class, purchaseOrderDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(0, productLocalInfo.getPurchasePrice(), 0.001);
    Object[] objects = productWriter.getProductByProductLocalInfoId(productLocalInfo.getId(), purchaseOrderDTO.getShopId());
    Product product = (Product)objects[0];

    Assert.assertEquals(purchaseOrderItemDTO.getProductName(), product.getName());
    Assert.assertEquals("多款", newPurchaseOrderDTO.getItemDTOs()[0].getVehicleBrand());

    InventoryRemindEvent inventoryRemindEvent = txnWriter.getInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId, 0, 10).get(0);
    Assert.assertEquals(11, inventoryRemindEvent.getAmount(), 0.001);


    purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.setSupplier("pTest");
    purchaseOrderDTO.setContact("HHH");
    purchaseOrderDTO.setMobile("13012343933");
    purchaseOrderDTO.setVestDateStr("2012-02-24 12:20");
    purchaseOrderDTO.setDeliveryDateStr("2012-02-24");

    purchaseOrderItemDTO = new PurchaseOrderItemDTO();
    purchaseOrderItemDTO.setPrice(1D);
    purchaseOrderItemDTO.setProductName(productName);
    purchaseOrderItemDTO.setSpec("123");
    purchaseOrderItemDTO.setVehicleBrand("AA");
    purchaseOrderItemDTO.setPrice(100D);

    purchaseOrderItemDTO.setAmount(100D);
    purchaseOrderItemDTO.setTotal(100D);
    purchaseOrderDTO.setId(null);
    itemDTOs = new PurchaseOrderItemDTO[1];
    itemDTOs[0] = purchaseOrderItemDTO;
    purchaseOrderDTO.setItemDTOs(itemDTOs);
    //now enter another order with similar product
    model = new ModelMap();
    buyController.save(request, model, purchaseOrderDTO);
    unitTestSleepSecond();
	  buyController.show(model, purchaseOrderDTO.getId().toString(), request);
    PurchaseOrderDTO newPurchaseOrderDTO2 = (PurchaseOrderDTO) model.get("purchaseOrderDTO");
    Long orderId2 = newPurchaseOrderDTO2.getId();
    Assert.assertTrue(orderId.longValue() != orderId2.longValue());
    Assert.assertEquals(100, newPurchaseOrderDTO2.getItemDTOs()[0].getAmount(), 0.001);

    request.setParameter("orderType", "purchase");
    model = new ModelMap();
    InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
    inventorySearchIndexDTO.setProductName(productName);
    searchController.searchProduct(model, request,inventorySearchIndexDTO);

    List<InventorySearchIndexDTO> results = (List<InventorySearchIndexDTO>) model.get("inventorySearchIndexDTOList");
    org.junit.Assert.assertEquals(2, results.size());
  }

  @Test
  public void testItemMemo() throws Exception {
    Long shopId = createShop();
    ModelMap model = new ModelMap();
    String itemMemo = "采购备注";
    String supplier = "苏州贝斯";

    PurchaseOrderItemDTO purchaseOrderItemDTO1 = new PurchaseOrderItemDTO();
    purchaseOrderItemDTO1.setProductDTO(new ProductDTO());
    purchaseOrderItemDTO1.setAmount(20D);
    purchaseOrderItemDTO1.setPrice(200D);
    purchaseOrderItemDTO1.setTotal(4000D);
    purchaseOrderItemDTO1.setMemo(itemMemo);
    purchaseOrderItemDTO1.setProductName("轮胎");
    purchaseOrderItemDTO1.setSpec("215/R45");
    purchaseOrderItemDTO1.setVehicleBrand("奇瑞");
    purchaseOrderItemDTO1.setVehicleModel("东方之子");
    purchaseOrderItemDTO1.setVehicleYear("2010");
    purchaseOrderItemDTO1.setVehicleEngine("2.0L");
    purchaseOrderItemDTO1.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);

    PurchaseOrderItemDTO purchaseOrderItemDTO2 = new PurchaseOrderItemDTO();
    purchaseOrderItemDTO2.setProductDTO(new ProductDTO());
    purchaseOrderItemDTO2.setAmount(20D);
    purchaseOrderItemDTO2.setPrice(200D);
    purchaseOrderItemDTO2.setTotal(4000D);
    purchaseOrderItemDTO2.setMemo(itemMemo);
    purchaseOrderItemDTO2.setProductName("导航仪");
    purchaseOrderItemDTO2.setSpec("215/R45");
    purchaseOrderItemDTO2.setVehicleBrand("多款");
    purchaseOrderItemDTO2.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE);

    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.setShopId(shopId);
    purchaseOrderDTO.setSupplier(supplier);
    purchaseOrderDTO.setTotal(4000);
    purchaseOrderDTO.setEditorId(10000010001000000L);
    purchaseOrderDTO.setEditor("王先生");
    purchaseOrderDTO.setDeliveryDateStr("2012-04-21");
    purchaseOrderDTO.setEditDateStr("2012-04-05");
    purchaseOrderDTO.setVestDateStr("2012-04-21 12:20");
    purchaseOrderDTO.setItemDTOs(new PurchaseOrderItemDTO[]{purchaseOrderItemDTO1, purchaseOrderItemDTO2});
    purchaseOrderDTO.setContact("李先生");
    purchaseOrderDTO.setMobile("13513213222");
    purchaseOrderDTO.setAddress("苏州");
    request.getSession().setAttribute("shopId", shopId);
    buyController.save(request, model, purchaseOrderDTO);
    unitTestSleepSecond();
	  buyController.show(model, purchaseOrderDTO.getId().toString(), request);
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setOrderType(OrderTypes.PURCHASE);
    itemIndexDTO.setCustomerOrSupplierName(supplier);
    itemIndexDTO.setGoodsBuyOrderType(true);
    itemIndexDTO.setGoodsStorageOrderType(false);
    itemIndexDTO.setGoodsStorageOrderType(false);
    itemIndexDTO.setGoodsSaleOrderType(false);
    itemIndexDTO.setReturnOrderType(false);
    itemIndexDTO.setRepairOrderType(false);
    itemIndexDTO.setReturnOrderType(false);
    itemIndexDTO.setPageNo("1");
    OrderSavedEvent orderSavedEvent = (OrderSavedEvent) request.getAttribute("UNIT_TEST");
    while (orderSavedEvent.mockFlag()) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
    }
    goodsHistoryController.searchGoodsHistory(model, request, itemIndexDTO);

    List<ItemIndexDTO> itemIndexList = (List<ItemIndexDTO>) model.get("itemIndexList");
    Assert.assertEquals(2, itemIndexList.size());
    Assert.assertEquals(itemMemo, itemIndexList.get(0).getItemMemo());

    InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
    inventorySearchIndexDTO.setProductName("导航仪");
    inventorySearchIndexDTO.setPageStatus("Home");
    searchController.searchProduct(model, request,inventorySearchIndexDTO);
  }
}
