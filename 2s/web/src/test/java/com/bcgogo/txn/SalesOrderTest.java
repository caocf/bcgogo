package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.CommonTestService;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.*;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.model.OrderIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.OrderSavedEvent;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.model.Debt;
import com.bcgogo.txn.model.Inventory;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.user.dto.permission.ShopRoleDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.model.CustomerRecord;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.utils.DateUtil;
import junit.framework.Assert;
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
* Time: 10:11 PM
* To change this template use File | Settings | File Templates.
*/
public class SalesOrderTest extends AbstractTest {
//  @Test
//  public void testGoodsReturn() throws Exception {
//    Long shopId = 10000010001170017l;
//      ModelMap model = new ModelMap();
//      ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
//      request.getSession().setAttribute("shopId",shopId);
////      goodsReturnController.getProductsSearchResult(model,request,response,itemIndexDTO,null,null,"");
////
////      goodsReturnController.getProductsSearchResult(model,request,response,itemIndexDTO,null,null,"");
//
//  }
  @Before
  public void setUp() throws Exception {
    goodsReturnController = new GoodsReturnController();
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    buyController = new RFGoodBuyController();
    saleController = new GoodSaleController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    searchDaoManager = ServiceManager.getService(SearchDaoManager.class);

    initTxnControllers(goodsStorageController, saleController);
  }

  @Test   //品牌下拉框单元测试
  public void testSalesOrderBrand() throws Exception {
    Long shopId = createShop();
    ProductDTO productDTO = new ProductDTO();
    productDTO.setShopId(shopId);
    productDTO.setName("MyProduct");
    productDTO.setSpec("1232");
    productDTO.setBrand("牛顿");
    productDTO.setProductVehicleBrand("多款");
    addInventory(shopId, productDTO, 100, 10);

    InventorySearchIndex inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
    Assert.assertEquals(100, inventorySearchIndex.getAmount(), 0.001);

    SalesOrderDTO salesOrderDTO = createSalesOrderDTO(productDTO, 30, 100, 3000);
    ModelMap model = new ModelMap();

    this.flushAllMemCache();
    saleController.saveSale(model, salesOrderDTO, request, response,null);
    unitTestSleepSecond();
    this.orderDropDownListTest(salesOrderDTO.getBrand(), salesOrderDTO.getItemDTOs()[0].getProductName(), null, shopId);

  }

  @Test
  public void testSalesOrder() throws Exception {
    Long shopId = createShop();

//    initRepairShopRoleResource(shopId);

    ProductDTO productDTO = new ProductDTO();
    productDTO.setName("MyProduct");
    productDTO.setSpec("1232");
    productDTO.setBrand("牛顿");
    productDTO.setProductVehicleBrand("多款");
    addInventory(shopId, productDTO, 100, 10);

    InventorySearchIndex inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
    Assert.assertEquals(100, inventorySearchIndex.getAmount(), 0.001);

    SalesOrderDTO salesOrderDTO = createSalesOrderDTO(productDTO, 30, 100, 3000);
    ModelMap model = new ModelMap();

    saleController.saveSale(model, salesOrderDTO, request,response, null);
    unitTestSleepSecond();
	  saleController.getSalesOrder(model,request,salesOrderDTO.getId().toString());
    SalesOrderDTO returnedSalesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
    Long customerId = returnedSalesOrderDTO .getCustomerId();
    Inventory inventory = txnWriter.getById(Inventory.class, returnedSalesOrderDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(70, inventory.getAmount(), 0.001);
    List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
    itemOrderType.add(OrderTypes.SALE);

    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(shopId);
    dto.setOrderId(returnedSalesOrderDTO.getId());
    dto.setSelectedOrderTypes(itemOrderType);
    List<ItemIndex> indexes = searchService.searchItemIndex(dto,null, null, null, null);

    Assert.assertEquals(1, indexes.size());
    inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
    Assert.assertEquals(70, inventorySearchIndex.getAmount(), 0.001);
    //another sale, with debt
    salesOrderDTO = createSalesOrderDTO(productDTO, 70, 100, 3000);
    model = new ModelMap();
    salesOrderDTO.setDebt(3500d);
    salesOrderDTO.setOrderDiscount(salesOrderDTO.getTotal()-salesOrderDTO.getSettledAmount()-salesOrderDTO.getDebt());
    salesOrderDTO.setCustomerId(customerId);
    salesOrderDTO.setVestDateStr("2012-4-12 12:20");
    saleController.saveSale(model, salesOrderDTO, request, response,"2012-04-12");
    unitTestSleepSecond();
	  saleController.getSalesOrder(model,request,salesOrderDTO.getId().toString());
    List<OrderIndex> orderIndexs = searchService.getOrderIndexByOrderId(shopId, returnedSalesOrderDTO.getId(),
        OrderTypes.SALE, OrderStatus.SALE_REPEAL, customerId);
    Assert.assertEquals(1, orderIndexs.size());

    returnedSalesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
    inventory = txnWriter.getById(Inventory.class, returnedSalesOrderDTO.getItemDTOs()[0].getProductId());
    Assert.assertEquals(0, inventory.getAmount(), 0.001);
    inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
    Assert.assertEquals(0, inventorySearchIndex.getAmount(), 0.001);

    //verify debt related
    List<Debt> debts =txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{returnedSalesOrderDTO.getCustomerId()});
    Assert.assertEquals(3500, debts.get(0).getDebt(), 0.001);
    Long payTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", "2012-04-12");
    Assert.assertEquals(payTime, debts.get(0).getRemindTime(), 0.001);

    debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{returnedSalesOrderDTO.getCustomerId()});
    Assert.assertEquals(3500, debts.get(0).getDebt(), 0.001);
    Assert.assertEquals(7000, debts.get(0).getTotalAmount(), 0.001);
    Assert.assertEquals(3000, debts.get(0).getSettledAmount(), 0.001);
    Assert.assertEquals(DebtStatus.ARREARS, debts.get(0).getStatusEnum());


    List<CustomerRecord> customerRecords = userWriter.getCustomerRecordByCustomerId(returnedSalesOrderDTO.getCustomerId());
    Assert.assertEquals(1,customerRecords.size());
    Assert.assertEquals(3500, customerRecords.get(0).getTotalReceivable(), 0.001);
    Assert.assertEquals(7000, customerRecords.get(0).getLastAmount(), 0.001);

    List<ReceivableDTO> receivableDTOs = txnService.getReceivableDTOList(shopId, 0, 10);
    Assert.assertEquals(2, receivableDTOs.size());
    ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.SALE, returnedSalesOrderDTO.getId());
    Assert.assertEquals(7000, receivableDTO.getTotal(), 0.0001);
    Assert.assertEquals(3500, receivableDTO.getDebt(), 0.0001);
    Assert.assertEquals(500, receivableDTO.getDiscount(), 0.0001);
    Assert.assertEquals(3000, receivableDTO.getSettledAmount()  , 0.0001);
    Assert.assertEquals(ReceivableStatus.FINISH,receivableDTOs.get(0).getStatus());

    //销售单重录
    model = new ModelMap();
    saleController.copyGoodSale(model,request,salesOrderDTO.getId());
    unitTestSleepSecond();
    SalesOrderDTO returnReinputSalesOrderDTO = (SalesOrderDTO)model.get("salesOrderDTO");
    Assert.assertNull(returnReinputSalesOrderDTO.getId());
    Assert.assertNull(returnReinputSalesOrderDTO.getStatus());
    Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getId());
    Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getSalesOrderId());
    Map expectModel = new HashMap();
    CommonTestService commonTestService = new CommonTestService();
    commonTestService.setTestProductModel(expectModel,"MyProduct","牛顿",null,"1232",null,null,null,null);
    Map actualModel = new HashMap();
    commonTestService.setTestProductModel(actualModel,returnReinputSalesOrderDTO.getItemDTOs()[0].getProductName(),
        returnReinputSalesOrderDTO.getItemDTOs()[0].getBrand(),null,returnReinputSalesOrderDTO.getItemDTOs()[0].getSpec()
        ,null,null,null,null);
    Assert.assertEquals(expectModel,actualModel);
    Assert.assertEquals(customerId,returnedSalesOrderDTO.getCustomerId());
    Assert.assertEquals(70d,returnReinputSalesOrderDTO.getItemDTOs()[0].getAmount(),0.0001);
    Assert.assertEquals(100d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPrice(),0.0001);
    Assert.assertEquals(10d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPurchasePrice(),0.0001);
    Assert.assertEquals(70d*100d,returnReinputSalesOrderDTO.getTotal(),0.0001);
    Assert.assertEquals(0d,returnReinputSalesOrderDTO.getDebt(),0.0001);
    Assert.assertEquals(0d,returnReinputSalesOrderDTO.getItemDTOs()[0].getInventoryAmount(),0.0001);



    model = new ModelMap();
    saleController.saleOrderRepeal(model,request,returnedSalesOrderDTO.getId(),null,null);
    unitTestSleepSecond();
    returnedSalesOrderDTO = (SalesOrderDTO)model.get("salesOrderDTO");

    Assert.assertEquals(OrderStatus.SALE_REPEAL,returnedSalesOrderDTO.getStatus());

    inventory = txnWriter.getById(Inventory.class, returnedSalesOrderDTO.getItemDTOs()[0].getProductId());
    inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
    Assert.assertEquals(70, inventory.getAmount(), 0.001);
    Assert.assertEquals(70, inventorySearchIndex.getAmount(), 0.001);


    ProductDTO productSolr = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, inventorySearchIndex.getParentProductId());
    Assert.assertEquals(70,productSolr.getInventoryNum(),0.001);
    Assert.assertEquals(productDTO.getName(),productSolr.getName());
    Assert.assertEquals(productDTO.getBrand(),productSolr.getBrand());
    Assert.assertEquals(productDTO.getModel(),productSolr.getModel());

    debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{returnedSalesOrderDTO.getCustomerId()});
    payTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", "2012-04-12");
    Assert.assertEquals(payTime, debts.get(0).getRemindTime(), 0.001);
    Assert.assertEquals(1,debts .size());
    Assert.assertEquals(3500, debts.get(0).getDebt(), 0.001);
    Assert.assertEquals(7000, debts.get(0).getTotalAmount(), 0.001);
    Assert.assertEquals(3000, debts.get(0).getSettledAmount(), 0.001);
    Assert.assertEquals(DebtStatus.REPEAL, debts.get(0).getStatusEnum());


    customerRecords = userWriter.getCustomerRecordByCustomerId(returnedSalesOrderDTO.getCustomerId());
    Assert.assertEquals(1,customerRecords.size());
    Assert.assertEquals(0, customerRecords.get(0).getTotalReceivable(), 0.001);
    Assert.assertEquals(7000, customerRecords.get(0).getLastAmount(), 0.001);

    receivableDTOs = txnService.getReceivableDTOList(shopId, 0, 10);
    Assert.assertEquals(2, receivableDTOs.size());
    receivableDTO = txnService .getReceivableByShopIdAndOrderTypeAndOrderId(shopId ,
        OrderTypes.SALE,returnedSalesOrderDTO.getId());
    Assert.assertEquals(7000, receivableDTO.getTotal(), 0.0001);
    Assert.assertEquals(3500, receivableDTO.getDebt(), 0.0001);
    Assert.assertEquals(500, receivableDTO.getDiscount(), 0.0001);
    Assert.assertEquals(3000, receivableDTO.getSettledAmount()  , 0.0001);
    Assert.assertEquals(ReceivableStatus.REPEAL, receivableDTO.getStatus());

//    orderIndexs = searchService.getOrderIndexByOrderId(shopId, returnedSalesOrderDTO.getId(),
//        OrderTypes.SALE, OrderStatus.SALE_REPEAL, customerId);
//    Assert.assertEquals(1, orderIndexs.size());
//    Assert.assertEquals(OrderStatus.SALE_REPEAL, orderIndexs.get(0).getOrderStatusEnum());
//    List<ItemIndexDTO> itemIndexeDTOs = searchService.getItemIndexDTOListByOrderId(shopId, returnedSalesOrderDTO.getId());
//    Assert.assertEquals(1, itemIndexeDTOs.size());

    //销售单重录
    model = new ModelMap();
    saleController.copyGoodSale(model,request,salesOrderDTO.getId());
    unitTestSleepSecond();
     returnReinputSalesOrderDTO = (SalesOrderDTO)model.get("salesOrderDTO");
    Assert.assertNull(returnReinputSalesOrderDTO.getId());
    Assert.assertNull(returnReinputSalesOrderDTO.getStatus());
    Assert.assertNull(returnReinputSalesOrderDTO.getReceivableId());
    Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getId());
    Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getSalesOrderId());
     expectModel = new HashMap();
    commonTestService.setTestProductModel(expectModel,"MyProduct","牛顿",null,"1232",null,null,null,null);
     actualModel = new HashMap();
    commonTestService.setTestProductModel(actualModel,returnReinputSalesOrderDTO.getItemDTOs()[0].getProductName(),
        returnReinputSalesOrderDTO.getItemDTOs()[0].getBrand(),null,returnReinputSalesOrderDTO.getItemDTOs()[0].getSpec()
        ,null,null,null,null);
    Assert.assertEquals(expectModel,actualModel);
    Assert.assertEquals(customerId,returnedSalesOrderDTO.getCustomerId());
    Assert.assertEquals(70d,returnReinputSalesOrderDTO.getItemDTOs()[0].getAmount(),0.0001);
    Assert.assertEquals(100d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPrice(),0.0001);
    Assert.assertEquals(10d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPurchasePrice(),0.0001);
    Assert.assertEquals(70d*100d,returnReinputSalesOrderDTO.getTotal(),0.0001);
    Assert.assertEquals(0d,returnReinputSalesOrderDTO.getDebt(),0.0001);
    Assert.assertEquals(70d,returnReinputSalesOrderDTO.getItemDTOs()[0].getInventoryAmount(),0.0001);
  }


  @Test
	public void testSalesOrderForRepairShop() throws Exception {
	  Long shopId = createShop();
    initRepairShopRoleResource(shopId);
	  ProductDTO productDTO = new ProductDTO();
	  productDTO.setName("MyProduct");
	  productDTO.setSpec("1232");
	  productDTO.setBrand("牛顿");
	  productDTO.setProductVehicleBrand("多款");
	  addInventory(shopId, productDTO, 100, 10);

	  InventorySearchIndex inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
	  Assert.assertEquals(100, inventorySearchIndex.getAmount(), 0.001);

	  SalesOrderDTO salesOrderDTO = createSalesOrderDTO(productDTO, 150, 100, 15000);
	  ModelMap model = new ModelMap();


    salesOrderDTO.setOrderDiscount(salesOrderDTO.getTotal()-salesOrderDTO.getSettledAmount()-salesOrderDTO.getDebt());
	  saleController.saveSale(model, salesOrderDTO, request,response, null);
    unitTestSleepSecond();
		saleController.getSalesOrder(model,request,salesOrderDTO.getId().toString());
	  SalesOrderDTO returnedSalesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
	  Long customerId = returnedSalesOrderDTO .getCustomerId();
	  Inventory inventory = txnWriter.getById(Inventory.class, returnedSalesOrderDTO.getItemDTOs()[0].getProductId());
	  Assert.assertEquals(0, inventory.getAmount(), 0.001);
	  Assert.assertEquals(50, inventory.getNoOrderInventory(), 0.001);
	  List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
	  itemOrderType.add(OrderTypes.SALE);

	  ItemIndexDTO dto = new ItemIndexDTO();
	  dto.setShopId(shopId);
	  dto.setOrderId(returnedSalesOrderDTO.getId());
	  dto.setSelectedOrderTypes(itemOrderType);
	  List<ItemIndex> indexes = searchService.searchItemIndex(dto,null, null, null, null);

	  Assert.assertEquals(1, indexes.size());
	  inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
	  Assert.assertEquals(0, inventorySearchIndex.getAmount(), 0.001);
	  //another sale, with debt
	  salesOrderDTO = createSalesOrderDTO(productDTO, 70, 100, 3000);
	  model = new ModelMap();
	  salesOrderDTO.setDebt(3500d);
	  salesOrderDTO.setCustomerId(customerId);
     salesOrderDTO.setVestDateStr("2012-03-20 12:20");
    salesOrderDTO.setOrderDiscount(salesOrderDTO.getTotal()-salesOrderDTO.getSettledAmount()-salesOrderDTO.getDebt());
	  saleController.saveSale(model, salesOrderDTO, request,response, "2012-04-12");
    unitTestSleepSecond();
		saleController.getSalesOrder(model,request,salesOrderDTO.getId().toString());
	  List<OrderIndex> orderIndexs = searchService.getOrderIndexByOrderId(shopId, returnedSalesOrderDTO.getId(),
	      OrderTypes.SALE, OrderStatus.SALE_REPEAL, customerId);
	  Assert.assertEquals(1, orderIndexs.size());

	  returnedSalesOrderDTO = (SalesOrderDTO) model.get("salesOrderDTO");
	  inventory = txnWriter.getById(Inventory.class, returnedSalesOrderDTO.getItemDTOs()[0].getProductId());
	  Assert.assertEquals(0, inventory.getAmount(), 0.001);
	  Assert.assertEquals(120, inventory.getNoOrderInventory(), 0.001);
	  inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
	  Assert.assertEquals(0, inventorySearchIndex.getAmount(), 0.001);

	  //verify debt related
	  List<Debt> debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{returnedSalesOrderDTO.getCustomerId()});
	  Assert.assertEquals(3500, debts.get(0).getDebt(), 0.001);
	  Long payTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", "2012-04-12");
	  Assert.assertEquals(payTime, debts.get(0).getRemindTime(), 0.001);

	  debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{returnedSalesOrderDTO.getCustomerId()});
	  Assert.assertEquals(3500, debts.get(0).getDebt(), 0.001);
	  Assert.assertEquals(7000, debts.get(0).getTotalAmount(), 0.001);
	  Assert.assertEquals(3000, debts.get(0).getSettledAmount(), 0.001);
	  Assert.assertEquals(DebtStatus.ARREARS, debts.get(0).getStatusEnum());


	  List<CustomerRecord> customerRecords = userWriter.getCustomerRecordByCustomerId(returnedSalesOrderDTO.getCustomerId());
	  Assert.assertEquals(1,customerRecords.size());
	  Assert.assertEquals(3500, customerRecords.get(0).getTotalReceivable(), 0.001);
	  Assert.assertEquals(7000, customerRecords.get(0).getLastAmount(), 0.001);

	  List<ReceivableDTO> receivableDTOs = txnService.getReceivableDTOList(shopId, 0, 10);
	  Assert.assertEquals(2, receivableDTOs.size());
	  ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.SALE, returnedSalesOrderDTO.getId());
	  Assert.assertEquals(7000, receivableDTO.getTotal(), 0.0001);
	  Assert.assertEquals(3500, receivableDTO.getDebt(), 0.0001);
	  Assert.assertEquals(500, receivableDTO.getDiscount(), 0.0001);
	  Assert.assertEquals(3000, receivableDTO.getSettledAmount()  , 0.0001);
	  Assert.assertEquals(ReceivableStatus.FINISH,receivableDTOs.get(0).getStatus());

	  //销售单重录
	  model = new ModelMap();
	  saleController.copyGoodSale(model,request,salesOrderDTO.getId());
    unitTestSleepSecond();
	  SalesOrderDTO returnReinputSalesOrderDTO = (SalesOrderDTO)model.get("salesOrderDTO");
	  Assert.assertNull(returnReinputSalesOrderDTO.getId());
	  Assert.assertNull(returnReinputSalesOrderDTO.getStatus());
	  Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getId());
	  Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getSalesOrderId());
	  Map expectModel = new HashMap();
	  CommonTestService commonTestService = new CommonTestService();
	  commonTestService.setTestProductModel(expectModel,"MyProduct","牛顿",null,"1232",null,null,null,null);
	  Map actualModel = new HashMap();
	  commonTestService.setTestProductModel(actualModel,returnReinputSalesOrderDTO.getItemDTOs()[0].getProductName(),
	      returnReinputSalesOrderDTO.getItemDTOs()[0].getBrand(),null,returnReinputSalesOrderDTO.getItemDTOs()[0].getSpec()
	      ,null,null,null,null);
	  Assert.assertEquals(expectModel,actualModel);
	  Assert.assertEquals(customerId,returnedSalesOrderDTO.getCustomerId());
	  Assert.assertEquals(70d,returnReinputSalesOrderDTO.getItemDTOs()[0].getAmount(),0.0001);
	  Assert.assertEquals(100d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPrice(),0.0001);
	  Assert.assertEquals(10d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPurchasePrice(),0.0001);
	  Assert.assertEquals(70d*100d,returnReinputSalesOrderDTO.getTotal(),0.0001);
	  Assert.assertEquals(0d,returnReinputSalesOrderDTO.getDebt(),0.0001);
	  Assert.assertEquals(0d,returnReinputSalesOrderDTO.getItemDTOs()[0].getInventoryAmount(),0.0001);



	  model = new ModelMap();
	  saleController.saleOrderRepeal(model,request,returnedSalesOrderDTO.getId(),null,null);
    unitTestSleepSecond();
	  returnedSalesOrderDTO = (SalesOrderDTO)model.get("salesOrderDTO");

	  Assert.assertEquals(OrderStatus.SALE_REPEAL,returnedSalesOrderDTO.getStatus());

	  inventory = txnWriter.getById(Inventory.class, returnedSalesOrderDTO.getItemDTOs()[0].getProductId());
	  inventorySearchIndex = searchWriter.getById(InventorySearchIndex.class, productDTO.getProductLocalInfoId());
	  Assert.assertEquals(70, inventory.getAmount(), 0.001);
	  Assert.assertEquals(120, inventory.getNoOrderInventory(), 0.001);
	  Assert.assertEquals(70, inventorySearchIndex.getAmount(), 0.001);

	  ProductDTO productSolr = ServiceManager.getService(ISearchProductService.class).getProductDTOFromSolrById(shopId, inventorySearchIndex.getParentProductId());
	  Assert.assertEquals(70,productSolr.getInventoryNum(),0.001);
	  Assert.assertEquals(productDTO.getName(),productSolr.getName());
	  Assert.assertEquals(productDTO.getBrand(),productSolr.getBrand());
	  Assert.assertEquals(productDTO.getModel(),productSolr.getModel());

	  debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{returnedSalesOrderDTO.getCustomerId()});
	  payTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", "2012-04-12");
	  Assert.assertEquals(payTime, debts.get(0).getRemindTime(), 0.001);
	  Assert.assertEquals(1,debts .size());
	  Assert.assertEquals(3500, debts.get(0).getDebt(), 0.001);
	  Assert.assertEquals(7000, debts.get(0).getTotalAmount(), 0.001);
	  Assert.assertEquals(3000, debts.get(0).getSettledAmount(), 0.001);
	  Assert.assertEquals(DebtStatus.REPEAL, debts.get(0).getStatusEnum());


	  customerRecords = userWriter.getCustomerRecordByCustomerId(returnedSalesOrderDTO.getCustomerId());
	  Assert.assertEquals(1,customerRecords.size());
	  Assert.assertEquals(0, customerRecords.get(0).getTotalReceivable(), 0.001);
	  Assert.assertEquals(7000, customerRecords.get(0).getLastAmount(), 0.001);

	  receivableDTOs = txnService.getReceivableDTOList(shopId, 0, 10);
	  Assert.assertEquals(2, receivableDTOs.size());
	  receivableDTO = txnService .getReceivableByShopIdAndOrderTypeAndOrderId(shopId ,
	      OrderTypes.SALE,returnedSalesOrderDTO.getId());
	  Assert.assertEquals(7000, receivableDTO.getTotal(), 0.0001);
	  Assert.assertEquals(3500, receivableDTO.getDebt(), 0.0001);
	  Assert.assertEquals(500, receivableDTO.getDiscount(), 0.0001);
	  Assert.assertEquals(3000, receivableDTO.getSettledAmount()  , 0.0001);
	  Assert.assertEquals(ReceivableStatus.REPEAL, receivableDTO.getStatus());

//	  orderIndexs = searchService.getOrderIndexByOrderId(shopId, returnedSalesOrderDTO.getId(),
//	      OrderTypes.SALE, OrderStatus.SALE_REPEAL, customerId);
//	  Assert.assertEquals(1, orderIndexs.size());
//	  Assert.assertEquals(OrderStatus.SALE_REPEAL, orderIndexs.get(0).getOrderStatusEnum());
//	  List<ItemIndexDTO> itemIndexeDTOs = searchService.getItemIndexDTOListByOrderId(shopId, returnedSalesOrderDTO.getId());
//	  Assert.assertEquals(1, itemIndexeDTOs.size());

	  //销售单重录
	  model = new ModelMap();
	  saleController.copyGoodSale(model,request,salesOrderDTO.getId());
    unitTestSleepSecond();
	   returnReinputSalesOrderDTO = (SalesOrderDTO)model.get("salesOrderDTO");
	  Assert.assertNull(returnReinputSalesOrderDTO.getId());
	  Assert.assertNull(returnReinputSalesOrderDTO.getStatus());
	  Assert.assertNull(returnReinputSalesOrderDTO.getReceivableId());
	  Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getId());
	  Assert.assertNull(returnReinputSalesOrderDTO.getItemDTOs()[0].getSalesOrderId());
	   expectModel = new HashMap();
	  commonTestService.setTestProductModel(expectModel,"MyProduct","牛顿",null,"1232",null,null,null,null);
	   actualModel = new HashMap();
	  commonTestService.setTestProductModel(actualModel,returnReinputSalesOrderDTO.getItemDTOs()[0].getProductName(),
	      returnReinputSalesOrderDTO.getItemDTOs()[0].getBrand(),null,returnReinputSalesOrderDTO.getItemDTOs()[0].getSpec()
	      ,null,null,null,null);
	  Assert.assertEquals(expectModel,actualModel);
	  Assert.assertEquals(customerId,returnedSalesOrderDTO.getCustomerId());
	  Assert.assertEquals(70d,returnReinputSalesOrderDTO.getItemDTOs()[0].getAmount(),0.0001);
	  Assert.assertEquals(100d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPrice(),0.0001);
	  Assert.assertEquals(10d,returnReinputSalesOrderDTO.getItemDTOs()[0].getPurchasePrice(),0.0001);
	  Assert.assertEquals(70d*100d,returnReinputSalesOrderDTO.getTotal(),0.0001);
	  Assert.assertEquals(0d,returnReinputSalesOrderDTO.getDebt(),0.0001);
	  Assert.assertEquals(70d,returnReinputSalesOrderDTO.getItemDTOs()[0].getInventoryAmount(),0.0001);
	}

}

