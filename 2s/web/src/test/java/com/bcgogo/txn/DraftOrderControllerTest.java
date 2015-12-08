package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.ConsumeType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.DraftOrderSearchDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.DraftOrder;
import com.bcgogo.txn.service.IDraftOrderService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-10
 * Time: 上午5:54
 * To change this template use File | Settings | File Templates.
 */
public class DraftOrderControllerTest  extends AbstractTest {

  protected MockHttpServletRequest request;
  protected Long shopId=111115852l;
  @Before
  public void setUp() throws Exception {
    request= new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    draftOrderController=new DraftOrderController();
    draftOrderService = ServiceManager.getService(IDraftOrderService.class);
    draftOrderController.setDraftOrderService(draftOrderService);
    UserDTO userDTO=createUser();
    request.getSession().setAttribute("shopId",shopId);
    request.getSession().setAttribute("userId",userDTO.getId());
  }


  @Test
  public void testSavePurchaseOrderDraft(){
    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.setSupplier("purchase1");
    purchaseOrderDTO.setContact("HHH");
    purchaseOrderDTO.setMobile("1301234393");
    purchaseOrderDTO.setVestDateStr("2012-02-01 12:20");
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
    DraftOrderDTO draftOrderDTO= draftOrderController.savePurchaseOrderDraft(request,purchaseOrderDTO);
    Assert.assertNotNull(draftOrderDTO);
    Assert.assertNotNull(draftOrderDTO.getId());
    Assert.assertEquals("purchase1", draftOrderDTO.getCustomerOrSupplierName());
    Assert.assertEquals("HHH",draftOrderDTO.getContact());
    Assert.assertEquals("1301234393",draftOrderDTO.getMobile());
    Assert.assertEquals("2012-02-01 12:20",draftOrderDTO.getVestDateStr());
    Assert.assertEquals("2012-02-24",draftOrderDTO.getDeliveryDateStr());
    Assert.assertEquals(100D,draftOrderDTO.getTotal(),0.0001);
    Assert.assertEquals(shopId,draftOrderDTO.getShopId());
  }

  /**
   *  主要测试GetOrderByDraftOrderId，也包括对保存入库草稿的测试
   * @throws Exception
   */
  @Test
  public void testGetOrderByDraftOrderId() throws Exception {
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("inventory1");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("13012343943");
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");
    purchaseInventoryDTO.setTotal(100D);
    //页面中修改实付，挂账金额
    purchaseInventoryDTO.setStroageActuallyPaid(300d);  //实付3
    purchaseInventoryDTO.setStroageCreditAmount(50d);    //挂账5
    purchaseInventoryDTO.setStroageSupplierDeduction(3d);   //扣款3

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPurchasePrice(100d);
    purchaseInventoryItemDTO.setRecommendedPrice(500d);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setBrand("牛顿");
    purchaseInventoryItemDTO.setSpec("123");
    purchaseInventoryItemDTO.setTotal(1100d);
    purchaseInventoryItemDTO.setModel("M2");
    purchaseInventoryItemDTO.setVehicleBrand("宝马");
    purchaseInventoryItemDTO.setVehicleModel("vm");
    purchaseInventoryItemDTO.setUnit("箱");
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    DraftOrderDTO draftOrderDTO= draftOrderController.savePurchaseInventoryDraft(request, purchaseInventoryDTO);
    Assert.assertNotNull(draftOrderDTO);
    Assert.assertNotNull(draftOrderDTO.getId());
    Assert.assertEquals(shopId,draftOrderDTO.getShopId());
    Assert.assertEquals("inventory1",draftOrderDTO.getCustomerOrSupplierName());
    Assert.assertEquals("HHH",draftOrderDTO.getContact());
    Assert.assertEquals("13012343943",draftOrderDTO.getMobile());
    Assert.assertEquals("2012-09-20 12:20",draftOrderDTO.getVestDateStr());
    Assert.assertEquals(100D,draftOrderDTO.getTotal(),0.0001);
    Assert.assertEquals(300d,draftOrderDTO.getSettledAmount(),0.0001);
    Assert.assertEquals(50d,draftOrderDTO.getDebt(),0.0001);

    modelMap=new ModelMap();
    String url_storage=draftOrderController.getOrderByDraftOrderId(modelMap,request,draftOrderDTO.getIdStr());
    Assert.assertEquals("/txn/goodsStorage",url_storage);
    PurchaseInventoryDTO inventoryDTO=(PurchaseInventoryDTO)modelMap.get("purchaseInventoryDTO");
    Assert.assertNotNull(inventoryDTO);
    Assert.assertEquals(draftOrderDTO.getIdStr(),inventoryDTO.getDraftOrderIdStr());
    Assert.assertEquals("inventory1",inventoryDTO.getSupplier());
    Assert.assertEquals("HHH",inventoryDTO.getContact());
    Assert.assertEquals("13012343943",inventoryDTO.getMobile());
    Assert.assertEquals("2012-09-20 12:20",inventoryDTO.getVestDateStr());
    Assert.assertEquals(100d,inventoryDTO.getTotal(),0.0001);
    Assert.assertEquals(300d,inventoryDTO.getStroageActuallyPaid(),0.0001);
    Assert.assertEquals(50d,inventoryDTO.getStroageCreditAmount(),0.0001);


    PurchaseInventoryItemDTO itemDTO=inventoryDTO.getItemDTOs()[0];
    Assert.assertEquals(productName,itemDTO.getProductName());
    Assert.assertEquals("牛顿",itemDTO.getBrand());
    Assert.assertEquals("123",itemDTO.getSpec());
    Assert.assertEquals("M2",itemDTO.getModel());
    Assert.assertEquals("宝马",itemDTO.getVehicleBrand());
    Assert.assertEquals("vm",itemDTO.getVehicleModel());
    Assert.assertEquals("箱",itemDTO.getUnit());
    Assert.assertEquals(25d,itemDTO.getAmount(),0.0001);
    Assert.assertEquals(100d,itemDTO.getPurchasePrice(),0.0001);
    Assert.assertEquals(500d,itemDTO.getRecommendedPrice(),0.0001);
    Assert.assertEquals(1100d,itemDTO.getTotal(),0.0001);

    String url= draftOrderController.toDraftOrderBox(modelMap,request);
    Assert.assertEquals("/remind/draftOrderBox",url);
    DraftOrderSearchDTO draftOrderSearchDTO=(DraftOrderSearchDTO)modelMap.get("draftOrderSearchDTO");
    Assert.assertNotNull(draftOrderSearchDTO.getEndTime());

  }


  @Test
  public void testSalesOrderDraft() throws Exception {
    SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
    salesOrderDTO.setCustomer("User1");
    salesOrderDTO.setContact("contact3");
    salesOrderDTO.setMobile("1311111111");
    salesOrderDTO.setEditDateStr("2012-02-15");
    salesOrderDTO.setVestDateStr("2012-02-24 12:20");
    salesOrderDTO.setSettledAmount(500D);
    salesOrderDTO.setSettledAmountHid(0D);
    salesOrderDTO.setDebt(50D);
    salesOrderDTO.setTotal(500D);
    salesOrderDTO.setShopId(shopId);

    SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO();
    salesOrderItemDTO.setProductName("轮胎1");
    salesOrderItemDTO.setSpec("M3");
    salesOrderItemDTO.setVehicleBrand("大众");
    salesOrderItemDTO.setPrice(500D);
    salesOrderItemDTO.setBrand("牛顿");
    salesOrderItemDTO.setAmount(1d);
    salesOrderItemDTO.setProductId(11101111l);
    salesOrderItemDTO.setTotal(500D);
    SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[1];
    itemDTOs[0] = salesOrderItemDTO;
    salesOrderDTO.setItemDTOs(itemDTOs);
    DraftOrderDTO draftOrderDTO= draftOrderController.saveSalesOrderDraft(request,salesOrderDTO);
    Assert.assertNotNull(draftOrderDTO);
    Assert.assertNotNull(draftOrderDTO.getId());
    Assert.assertEquals(shopId,draftOrderDTO.getShopId());
    Assert.assertEquals("User1",draftOrderDTO.getCustomerOrSupplierName());
    Assert.assertEquals("contact3",draftOrderDTO.getContact());
    Assert.assertEquals("1311111111",draftOrderDTO.getMobile());
    Assert.assertEquals("2012-02-24 12:20",draftOrderDTO.getVestDateStr());
    Assert.assertEquals(500D,draftOrderDTO.getSettledAmount(),0.0001);
    Assert.assertEquals(50D,draftOrderDTO.getDebt(),0.0001);
    Assert.assertEquals(500D, draftOrderDTO.getTotal(), 0.0001);
  }

  @Test
  public void testSaveReturnOrderDraft(){
    PurchaseReturnDTO returnDTO = new  PurchaseReturnDTO();
    returnDTO.setShopId(shopId);
    returnDTO.setSupplier("return_supplier");
    returnDTO.setContact("s_contact");
    returnDTO.setMobile("13816852541");
    returnDTO.setAddress("abc");
    returnDTO.setReturnPayableType("cash");
    returnDTO.setVestDateStr("2012-02-01 12:20");
    returnDTO.setTotal(500d);
    PurchaseReturnItemDTO  purchaseReturnItemDTO = new PurchaseReturnItemDTO();
    purchaseReturnItemDTO.setModel("M6");
    purchaseReturnItemDTO.setSpec("spc");
    purchaseReturnItemDTO.setProductName("挡风板");
    purchaseReturnItemDTO.setBrand("中国");
    purchaseReturnItemDTO.setPrice(36d);
    purchaseReturnItemDTO.setAmount(2d);
    purchaseReturnItemDTO.setTotal(72d);

    PurchaseReturnItemDTO[] a = {purchaseReturnItemDTO};
    returnDTO.setItemDTOs(a);

    DraftOrderDTO draftOrderDTO= draftOrderController.saveReturnStorageOrderDraft(request, returnDTO);
    Assert.assertNotNull(draftOrderDTO);
    Assert.assertNotNull(draftOrderDTO.getId());
    Assert.assertEquals(shopId,draftOrderDTO.getShopId());
    Assert.assertEquals("return_supplier", draftOrderDTO.getCustomerOrSupplierName());
    Assert.assertEquals("s_contact",draftOrderDTO.getContact());
    Assert.assertEquals("13816852541",draftOrderDTO.getMobile());
    Assert.assertEquals("abc",draftOrderDTO.getAddress());
    Assert.assertEquals("2012-02-01 12:20",draftOrderDTO.getVestDateStr());
    Assert.assertEquals(500d,draftOrderDTO.getTotal(),0.0001);
  }

  @Test
  public void testSaveRepairOrderDraft(){
    RepairOrderDTO repairOrderDTO=new RepairOrderDTO();
    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");
    repairOrderDTO.setShopId(shopId);
    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setMobile("12311111111");
    repairOrderDTO.setTotal(240D);

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setTotal(100d);
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setVehicleBrand("雪佛兰");
    repairOrderItemDTO.setShopId(shopId);
    repairOrderItemDTO.setProductName("product");
    repairOrderItemDTO.setBrand("brand");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);
    Object id= draftOrderController.saveRepairOrderDraft(request,repairOrderDTO);
    Assert.assertNotNull(id);

  }

  /**
   * 测试getDraftOrders和deleteDraftOrder两个方法
   * @throws Exception
   */
  @Test
  public void testGetAndDeleteDraftOrder() throws Exception {
    SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
    salesOrderDTO.setCustomer("User1");
    salesOrderDTO.setContact("contact3");
    salesOrderDTO.setMobile("1311111111");
    salesOrderDTO.setEditDateStr("2012-02-15");
    salesOrderDTO.setVestDateStr("2012-02-24 12:20");
    salesOrderDTO.setSettledAmount(500D);
    salesOrderDTO.setSettledAmountHid(0D);
    salesOrderDTO.setDebt(50D);
    salesOrderDTO.setTotal(500D);
    salesOrderDTO.setShopId(shopId);

    SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO();
    salesOrderItemDTO.setProductName("轮胎1");
    salesOrderItemDTO.setSpec("M3");
    salesOrderItemDTO.setVehicleBrand("大众");
    salesOrderItemDTO.setPrice(500D);
    salesOrderItemDTO.setBrand("牛顿");
    salesOrderItemDTO.setAmount(1d);
    salesOrderItemDTO.setProductId(11101111l);
    salesOrderItemDTO.setTotal(500D);
    SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[1];
    itemDTOs[0] = salesOrderItemDTO;
    salesOrderDTO.setItemDTOs(itemDTOs);
    draftOrderController.saveSalesOrderDraft(request,salesOrderDTO);

    ModelMap model = new ModelMap();
    String[]orderTypes=new String[]{"ALL"};
    DraftOrderSearchDTO draftOrderSearchDTO=new DraftOrderSearchDTO();
    draftOrderSearchDTO.setOrderTypes(orderTypes);
    draftOrderSearchDTO.setStartPageNo("1");
    Object result=draftOrderController.getDraftOrders(request, draftOrderSearchDTO);
    List<Object> result_list=(List<Object>)result;
    Map<String,Object> data= ( Map<String,Object>)result_list.get(0);
    Pager pager=(Pager)result_list.get(1);
    Assert.assertNotNull(data);
    List<DraftOrderDTO> draftOrderDTOs=(List<DraftOrderDTO>)data.get("draftOrderData");
    List countNums=(List)data.get("countOrderTypeList");
    Assert.assertNotNull(result);
    Assert.assertNotNull(pager);
    Assert.assertNotNull(countNums);
    Assert.assertNotSame(0, pager.getTotalRows());
    Assert.assertNotSame(0, draftOrderDTOs.size());

    DraftOrderDTO draftOrderDTO=draftOrderDTOs.get(0);
    draftOrderSearchDTO.setDraftOrderIdStr(draftOrderDTO.getIdStr());
    draftOrderController.deleteDraftOrder(request, draftOrderSearchDTO);
    DraftOrder draftOrder= draftOrderService.lazyLoadDraftOrderId(shopId, draftOrderDTO.getId()) ;
    Assert.assertEquals("DRAFT_REPEAL",draftOrder.getStatus().toString());

  }



  public UserDTO createUser() throws BcgogoException {
    IUserService userService=ServiceManager.getService(IUserService.class);
    Long temp = System.currentTimeMillis();
    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("123");
    userDTO.setUserName("UName" + temp);
    userDTO.setName("Name" + temp);
    userDTO.setEmail(temp + "@qq.com");
    userDTO.setMobile("1" + temp.toString().substring(2, 12));
    userDTO.setShopId(shopId);
    userService.createUser(userDTO);
    return userDTO;
    }
}
