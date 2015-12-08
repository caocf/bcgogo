package com.bcgogo.product;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.GoodStorageController;
import com.bcgogo.txn.RepairController;
import com.bcgogo.txn.TxnController;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.RepairOrderItemDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/25/12
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveVehicleTest extends AbstractTest {


  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    repairController = new RepairController();
    initTxnControllers(goodsStorageController,repairController);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testPopulateRepairOrderDTO() throws Exception {
    Long shopId = createShop();
    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    repairOrderDTO.setVechicle("苏E00223");
    repairOrderDTO.setLicenceNo("苏E00223");
    repairOrderDTO.setCustomerName("张先生");
    repairOrderDTO.setStartMileage(2000D);
    repairOrderDTO.setFuelNumber("3");
    repairOrderDTO.setStartDate(1330334252929L);
    repairOrderDTO.setStartDateStr("2012-02-27 17:17");
    repairOrderDTO.setEndDateStr("2012-02-27 17:17");
    repairOrderDTO.setTotal(4000D);
    repairOrderDTO.setTotalHid(0.0);
    repairOrderDTO.setServiceType(OrderTypes.REPAIR);
    repairOrderDTO.setBrand("车牌D");
    repairOrderDTO.setModel("车型D");
    repairOrderDTO.setYear("2012");
    repairOrderDTO.setEngine("2.5L");
    repairOrderDTO.setSettledAmount(4000D);
    repairOrderDTO.setSettledAmountHid(0.0);
    repairOrderDTO.setMobile("13512502525");

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setAmount(23D);
    repairOrderItemDTO.setPrice(200D);
    repairOrderItemDTO.setTotal(4600d);
    repairOrderItemDTO.setProductName("测试商品A");
    repairOrderItemDTO.setSpec("JO");
    repairOrderItemDTO.setInventoryAmount(0.0D);
    repairOrderItemDTO.setProductType(3);

    repairOrderDTO.setItemDTOs(new RepairOrderItemDTO[]{repairOrderItemDTO});

    request.getSession().setAttribute("shopId", shopId);
    ModelMap model = new ModelMap();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertEquals("车牌D", repairOrderDTO.getBrand());
    Assert.assertEquals("车型D", repairOrderDTO.getModel());
    Assert.assertEquals("2012", repairOrderDTO.getYear());
    Assert.assertEquals("2.5L", repairOrderDTO.getEngine());

  }

  @Test
  public void testPopulatePurchaseInventoryDTO() throws Exception {
    Long shopId = createShop();
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setSupplier("苏州统销");
    purchaseInventoryDTO.setTotal(4000D);
    purchaseInventoryDTO.setEditDateStr("2012-02-27");
    purchaseInventoryDTO.setVestDateStr("2012-02-27 12:20");
    purchaseInventoryDTO.setContact("wjl");
    purchaseInventoryDTO.setMobile("12352545658");
    purchaseInventoryDTO.setAddress("苏州");

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(23D);
    purchaseInventoryItemDTO.setPrice(200D);
    purchaseInventoryItemDTO.setTotal(4600d);
    purchaseInventoryItemDTO.setProductName("测试商品A");
    purchaseInventoryItemDTO.setSpec("JO");
    purchaseInventoryItemDTO.setVehicleBrand("皇冠");
    purchaseInventoryItemDTO.setVehicleModel("X45");
    purchaseInventoryItemDTO.setVehicleYear("2002");
    purchaseInventoryItemDTO.setVehicleEngine("2.0L");
    purchaseInventoryItemDTO.setProductVehicleStatus(1);

    purchaseInventoryDTO.setItemDTOs(new PurchaseInventoryItemDTO[]{purchaseInventoryItemDTO});

    request.getSession().setAttribute("shopId", shopId);
    ModelMap model = new ModelMap();
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    PurchaseInventoryDTO rpDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");


    Assert.assertEquals("皇冠", rpDTO.getItemDTOs()[0].getVehicleBrand());
    Assert.assertEquals("X45", rpDTO.getItemDTOs()[0].getVehicleModel());
    Assert.assertEquals("2002", rpDTO.getItemDTOs()[0].getVehicleYear());
    Assert.assertEquals("2.0L", rpDTO.getItemDTOs()[0].getVehicleEngine());
  }
}
