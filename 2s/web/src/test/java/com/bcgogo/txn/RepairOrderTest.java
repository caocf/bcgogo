package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.admin.AdminController;
import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.enums.*;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.model.OrderIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.OrderSavedEvent;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Debt;
import com.bcgogo.txn.model.Inventory;
import com.bcgogo.txn.model.Receivable;
import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.service.IMemberCheckerService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.CustomerRecord;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/1/12
 * Time: 7:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderTest extends AbstractTest {
  static boolean flag = true;
  public static final Logger LOG = LoggerFactory.getLogger(RepairOrderTest.class);
  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    repairController = new RepairController();
    adminController = new AdminController();
    goodsStorageController = new GoodStorageController();
    goodsHistoryController = new GoodsHistoryController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    populateVehiclesAndProduct();
    if (flag) {
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      notificationService.setMessageTemplate("sendFinishMsg2", "尊敬的{licenceNo}车主{customer}您好！感谢您对本店的照顾，您的爱车已施工完毕，预计金额{receivable}元。麻烦您尽快前来提车！{shopName}店敬启。", ShopConstant.BC_SHOP_ID);
      notificationService.setMessageTemplate("sendDebtMsg1", "欠款备忘：{memoTime}，车辆{licenceNo}消费项目：{services};材料：{productNameAndCounts},应收款{receivable}元，实收{actualCollection}元，欠款{debt}元，预计还款日：{repaymentDate}。", ShopConstant.BC_SHOP_ID);
      notificationService.setMessageTemplate("sendDiscountMsg2", "欠款备忘：{memoTime}，车辆{licenceNo}消费项目：{services};材料：{productNameAndCounts},应收款{receivable}元，实收{actualCollection}元，折扣{discount}元。", ShopConstant.BC_SHOP_ID);
      flag = false;
    }
    searchDaoManager = ServiceManager.getService(SearchDaoManager.class);
    createServices();
    initTxnControllers(txnController,repairController);
  }

  @Test
  public void testCurrentUsedProductRepairOrder() throws Exception {
    ModelMap model = new ModelMap();
    String vehicleNumber = new String("苏E1232322");
    request.setParameter("vehicleNumber", vehicleNumber);
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");
    repairOrderDTO.setShopId(shopId);
    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setMobile("13584876666");
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
    this.flushAllMemCache();
    openMessageSwitch();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    url = repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    this.orderDropDownListTest(repairOrderItemDTO.getBrand(), repairOrderItemDTO.getProductName(), repairOrderItemDTO.getVehicleBrand(), shopId);
  }

  @Test
  public void testRepairOrder() throws Exception {
    ModelMap model = new ModelMap();
    String vehicleNumber = new String("苏E1232322");
    request.setParameter("vehicleNumber", vehicleNumber);
    Long shopId = createShop();
    Long worker1 = createSalesMan(shopId, "工人1");
    Long worker2 = createSalesMan(shopId, "工人2");
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");
    repairOrderDTO.setShopId(shopId);
    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setStartDateStr("2012-03-20 12:00");
    repairOrderDTO.setEndDateStr("2012-03-20 12:00");
    repairOrderDTO.setMobile("13584876667");
    repairOrderDTO.setProductSaler("工人2,工人4,工人4");
    repairOrderDTO.setTotal(340D);

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("换机油");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    serviceDTO.setWorkers("工人1,工人3,工人3");       //测去重
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand("brand");
    repairOrderItemDTO.setShopId(shopId);
    repairOrderItemDTO.setProductName("product");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setTotal(240D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);
    openMessageSwitch();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));
    Long repairOrderId = repairOrderDTO.getId();

    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());

    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");

    Assert.assertEquals(340, repairOrderDTO.getTotal(), 0.001);
    Assert.assertEquals(1, repairOrderDTO.getServiceDTOs().length);
    Assert.assertEquals("换机油", repairOrderDTO.getServiceDTOs()[0].getService());
    Assert.assertEquals(1, repairOrderDTO.getItemDTOs().length);
    Assert.assertEquals("product", repairOrderDTO.getItemDTOs()[0].getProductName());

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);

    Assert.assertEquals(1, repairRemindEventDTOs.size());
    Assert.assertEquals(2, repairRemindEventDTOs.get(0).getAmount(), 0.001);

    //测试多个施工人,自动保存
    RepairOrderDTO dbRepairOrderDTO = txnService.getRepairOrder(repairOrderId);
    Assert.assertEquals("工人2,工人4", dbRepairOrderDTO.getProductSaler());
    RepairOrderServiceDTO[] dbServiceDTOs = dbRepairOrderDTO.getServiceDTOs();
    Assert.assertEquals(1, dbServiceDTOs.length);
    Assert.assertTrue(dbServiceDTOs[0].getWorkerIds().contains(String.valueOf(worker1)));
    Assert.assertEquals("工人1,工人3", dbServiceDTOs[0].getWorkers());

    List<OrderIndex> orderIndex = searchWriter.getOrderIndexDTOByOrderId(shopId, repairOrderId);
    Assert.assertEquals(1, orderIndex.size());
    Assert.assertTrue(orderIndex.get(0).getServiceWorker().contains("工人1"));
    Assert.assertTrue(orderIndex.get(0).getServiceWorker().contains("工人2"));
    Assert.assertTrue(orderIndex.get(0).getServiceWorker().contains("工人3"));
    Assert.assertTrue(orderIndex.get(0).getServiceWorker().contains("工人4"));

    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), shopId);
    Assert.assertEquals("product", productDTO.getName());

    Inventory inventory = txnWriter.getById(Inventory.class, productLocalInfoDTO.getId());
    Assert.assertEquals(0, inventory.getAmount(), 0.001);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584876668");
    purchaseInventoryDTO.setVestDateStr("2012-03-12 12:20");

    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());

    PurchaseInventoryItemDTO itemDTO = new PurchaseInventoryItemDTO();
    itemDTO.setAmount(100d);
    itemDTO.setPrice(11d);
    itemDTO.setAmount(1100D);
    itemDTO.setProductName("new item");
    itemDTO.setProductId(repairRemindEventDTOs.get(0).getProductId());

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);

    Assert.assertEquals(1, repairRemindEventDTOs.size());

    request = new MockHttpServletRequest();
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch(); //短信开关
    txnController.getRepairOrderByVehicleNumber(model, request, response);

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    model = new ModelMap();

    //更换服务项目名
    RepairOrderServiceDTO repairOrderServiceDTO = repairOrderDTO.getServiceDTOs()[0];
    repairOrderServiceDTO.setService("换机滤");
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrderDTO.getStatus());
    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrder.getStatusEnum());
    Assert.assertEquals("换机滤", repairOrderDTO.getServiceDTOs()[0].getService());

    model = new ModelMap();
    repairOrderDTO.setSettledAmount(340);
    repairOrderDTO.setDebt(0);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal()-repairOrderDTO.getSettledAmount()-repairOrderDTO.getDebt());
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.REPAIR, repairOrder.getId());
    Assert.assertEquals(340, receivable.getTotal(), 0.001);
    Assert.assertEquals(0, receivable.getDebt(), 0.001);
    Assert.assertEquals(0, receivable.getDiscount(), 0.001);
    smsSendSchedule.processSmsJobs();
  }

  @Test
  public void testRepairOrderWithDebt() throws Exception {
    ModelMap model = new ModelMap();
    String vehicleNumber = new String("苏E1255555");
    request.setParameter("vehicleNumber", vehicleNumber);
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");

     repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");

    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setMobile("13584876669");
    repairOrderDTO.setTotal(340D);

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setTotal(100d);
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand("brand");
    repairOrderItemDTO.setProductName("product");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setTotal(240D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);
    openMessageSwitch();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());

    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");

    Assert.assertEquals(340, repairOrderDTO.getTotal(), 0.001);

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);

    Assert.assertEquals(1, repairRemindEventDTOs.size());
    Assert.assertEquals(2, repairRemindEventDTOs.get(0).getAmount(), 0.001);


    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), shopId);
    Assert.assertEquals("product", productDTO.getName());

    Inventory inventory = txnWriter.getById(Inventory.class, productLocalInfoDTO.getId());
    Assert.assertEquals(0, inventory.getAmount(), 0.001);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584876670");
    purchaseInventoryDTO.setVestDateStr("2012-03-20 12:20");

    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());

    PurchaseInventoryItemDTO itemDTO = new PurchaseInventoryItemDTO();
    itemDTO.setAmount(100d);
    itemDTO.setPrice(11d);
    itemDTO.setTotal(1100D);
    itemDTO.setProductName("new item");
    itemDTO.setProductId(repairRemindEventDTOs.get(0).getProductId());

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);

    Assert.assertEquals(1, repairRemindEventDTOs.size());

    request = new MockHttpServletRequest();
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    txnController.getRepairOrderByVehicleNumber(model, request, response);

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    model = new ModelMap();

//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrderDTO.getStatus());
    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrder.getStatusEnum());
    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
    ShopBalanceDTO shopBalanceDTO = null;
    Assert.assertEquals(1, jobs.size());
    Assert.assertEquals(repairOrderDTO.getMobile(), jobs.get(0).getReceiveMobile());
    smsSendSchedule.processSmsJobs();
    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
    Assert.assertEquals(0, jobs.size());
    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(99.80, shopBalanceDTO.getSmsBalance(), 0.001);   //短信长度大于70
    model = new ModelMap();
    repairOrderDTO.setSettledAmount(140);
    repairOrderDTO.setDebt(0);
    repairOrderDTO.setDebt(200);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal() - repairOrderDTO.getSettledAmount() - repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime("2012-03-24");
    openMessageSwitch();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.REPAIR, repairOrder.getId());
    Assert.assertEquals(340, receivable.getTotal(), 0.001);
    Assert.assertEquals(200, receivable.getDebt(), 0.001);
    Assert.assertEquals(0, receivable.getDiscount(), 0.001);
    Assert.assertEquals(140, receivable.getSettledAmount(), 0.001);

    List<Debt> debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{repairOrderDTO.getCustomerId()});

    Assert.assertEquals(200, debts.get(0).getDebt(), 0.001);

    inventory = txnWriter.getById(Inventory.class, repairOrderItemDTO.getProductId());
    Assert.assertEquals(98, inventory.getAmount(), 0.001);

    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());

    Assert.assertEquals(1, jobs.size());
    Assert.assertEquals(shopDTO.getMobile(), jobs.get(0).getReceiveMobile());

    smsSendSchedule.processSmsJobs();
    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
    Assert.assertEquals(0, jobs.size());
    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);  //短信长度大于70
    Assert.assertEquals(99.60, shopBalanceDTO.getSmsBalance(), 0.001);
  }

  @Test
  public void testRepairOrderReserve() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ModelMap model = new ModelMap();
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH1");
    purchaseInventoryDTO.setMobile("1301234399");
    purchaseInventoryDTO.setSupplier("test supplier1");
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPrice(100d);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("124");
    purchaseInventoryItemDTO.setTotal(2500d);
    purchaseInventoryItemDTO.setVehicleBrand("多款");

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    PurchaseInventoryDTO returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertNotNull(returnedPurchaseInventoryDTO.getId());
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(purchaseInventoryItemDTO.getProductId(),
        purchaseInventoryDTO.getShopId());
    ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), shopId);
    Assert.assertEquals(productName, productDTO.getName());
    Inventory inventory = txnWriter.getById(Inventory.class, productLocalInfoDTO.getId());
    Assert.assertEquals(25, inventory.getAmount(), 0.001);
    Assert.assertEquals("", purchaseInventoryDTO.getReturnType());

    String vehicleNumber = new String("苏E1255557");
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");

     repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");

    repairOrderDTO.setCustomerName("Test2");
    repairOrderDTO.setMobile("13584876671");
    repairOrderDTO.setTotal(600D);

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand(productDTO.getBrand());
    repairOrderItemDTO.setProductName(productDTO.getName());
    repairOrderItemDTO.setSpec(productDTO.getSpec());
    repairOrderItemDTO.setModel(productDTO.getModel());
    repairOrderItemDTO.setProductId(inventory.getId());
    ProductLocalInfo productLocalInfo = productWriter.getById(ProductLocalInfo.class, purchaseInventoryItemDTO.getProductId());
    repairOrderItemDTO.setPurchasePrice(productLocalInfo.getPurchasePrice() == null ? 0 : productLocalInfo.getPurchasePrice());
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(5D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    model = new ModelMap();
    repairOrderDTO.setSettledAmount(600);
    openMessageSwitch();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(600, repairOrderDTO.getTotal(), 0.001);
    Assert.assertEquals(5, repairOrderDTO.getItemDTOs()[0].getReserved(), 0.001);
    repairOrderDTO.getItemDTOs()[0].setAmount(30D);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    model = new ModelMap();
    repairOrderDTO.setSettledAmount(600);
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertEquals(0, repairOrderDTO.getItemDTOs()[0].getReserved(), 0.001);

    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(1, repairRemindEventDTOs.size());
    Assert.assertEquals(30, repairRemindEventDTOs.get(0).getAmount(), 0.001);
    smsSendSchedule.processSmsJobs();
  }

  @Test
  public void testRepairOrderWithDebitWithoutSaveAndFinishPhase() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ModelMap model = new ModelMap();
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH1");
    purchaseInventoryDTO.setMobile("1301234398");
    purchaseInventoryDTO.setSupplier("test supplier1");
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPrice(100d);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("124");
    purchaseInventoryItemDTO.setTotal(2500d);
    purchaseInventoryItemDTO.setVehicleBrand("多款");

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    openMessageSwitch();
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    PurchaseInventoryDTO returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertNotNull(returnedPurchaseInventoryDTO.getId());
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(purchaseInventoryItemDTO.getProductId(),
        purchaseInventoryDTO.getShopId());
    ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), shopId);
    Assert.assertEquals(productName, productDTO.getName());
    Inventory inventory = txnWriter.getById(Inventory.class, productLocalInfoDTO.getId());
    Assert.assertEquals(25, inventory.getAmount(), 0.001);
    Assert.assertEquals("", purchaseInventoryDTO.getReturnType());


    String vehicleNumber = new String("苏E1255557");
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");

    repairOrderDTO.setCustomerName("Test2");
    repairOrderDTO.setMobile("13584876672");
    repairOrderDTO.setTotal(240D);

    repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setTotal(100d);
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand(productDTO.getBrand());
    repairOrderItemDTO.setProductName(productDTO.getName());
    repairOrderItemDTO.setSpec(productDTO.getSpec());
    repairOrderItemDTO.setModel(productDTO.getModel());
    repairOrderItemDTO.setProductId(inventory.getId());
    ProductLocalInfo productLocalInfo = productWriter.getById(ProductLocalInfo.class, purchaseInventoryItemDTO.getProductId());
    repairOrderItemDTO.setPurchasePrice(productLocalInfo.getPurchasePrice() == null ? 0d : productLocalInfo.getPurchasePrice());
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);


    model = new ModelMap();
    repairOrderDTO.setSettledAmount(40);
    repairOrderDTO.setDebt(100);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal()-repairOrderDTO.getSettledAmount()-repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime("2012-03-24");
    openMessageSwitch();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);

    unitTestSleepSecond();
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());

    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(240, repairOrderDTO.getTotal(), 0.001);


    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(0, repairRemindEventDTOs.size());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_SETTLED, repairOrder.getStatusEnum());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
        OrderTypes.REPAIR, repairOrder.getId());
    Assert.assertEquals(240, receivable.getTotal(), 0.001);
    Assert.assertEquals(100, receivable.getDebt(), 0.001);
    Assert.assertEquals(100, receivable.getDiscount(), 0.001);
    Assert.assertEquals(40, receivable.getSettledAmount(), 0.001);

    List<Debt> debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{repairOrderDTO.getCustomerId()});
    Assert.assertEquals(100, debts.get(0).getDebt(), 0.001);

    List<CustomerRecord> customerRecords = userWriter.getCustomerRecordByCustomerId(repairOrderDTO.getCustomerId());
    Assert.assertEquals(1, customerRecords.size());
    Assert.assertEquals(100, customerRecords.get(0).getTotalReceivable(), 0.001);
    Assert.assertEquals(140, customerRecords.get(0).getTotalAmount(), 0.001);

    inventory = txnWriter.getById(Inventory.class, repairOrderItemDTO.getProductId());
    Assert.assertEquals(23, inventory.getAmount(), 0.001);

//    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
    List<SmsJobDTO> jobs = notificationService.getSmsJobsByShopId(shopId,0,100);
    Assert.assertEquals(2, jobs.size());
    Assert.assertEquals(shopDTO.getMobile(), jobs.get(0).getReceiveMobile());
    Assert.assertEquals(shopDTO.getMobile(), jobs.get(1).getReceiveMobile());
    smsSendSchedule.processSmsJobs();
//    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
    jobs = notificationService.getSmsJobsByShopId(shopId,0,100);
    Assert.assertEquals(0, jobs.size());
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(99.60, shopBalanceDTO.getSmsBalance(), 0.001);
  }


  @Test
  public void testRepairOrderWithDebitWithoutSavePhase() throws Exception {
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ModelMap model = new ModelMap();
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest1");
    purchaseInventoryDTO.setContact("HHH1");
    purchaseInventoryDTO.setMobile("1301234397");
    purchaseInventoryDTO.setSupplier("test supplier1");
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());

    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(25d);
    purchaseInventoryItemDTO.setPrice(100d);
    String productName = "QQQ" + System.currentTimeMillis();
    purchaseInventoryItemDTO.setProductName(productName);
    purchaseInventoryItemDTO.setSpec("124");
    purchaseInventoryItemDTO.setTotal(2500d);
    purchaseInventoryItemDTO.setVehicleBrand("多款");

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    openMessageSwitch();
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
	  goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    PurchaseInventoryDTO returnedPurchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    Assert.assertNotNull(returnedPurchaseInventoryDTO.getId());
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(purchaseInventoryItemDTO.getProductId(),
        purchaseInventoryDTO.getShopId());
    ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), shopId);
    Assert.assertEquals(productName, productDTO.getName());
    Inventory inventory = txnWriter.getById(Inventory.class, productLocalInfoDTO.getId());
    Assert.assertEquals(25, inventory.getAmount(), 0.001);
    Assert.assertEquals("", purchaseInventoryDTO.getReturnType());


    String vehicleNumber = new String("苏E1255557");
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");

    repairOrderDTO.setCustomerName("Test2");
    repairOrderDTO.setMobile("13584879666");
    repairOrderDTO.setTotal(240D);

    repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand(productDTO.getBrand());
    repairOrderItemDTO.setProductName(productDTO.getName());
    repairOrderItemDTO.setSpec(productDTO.getSpec());
    repairOrderItemDTO.setModel(productDTO.getModel());
    repairOrderItemDTO.setProductId(inventory.getId());
    ProductLocalInfo productLocalInfo = productWriter.getById(ProductLocalInfo.class, purchaseInventoryItemDTO.getProductId());
    repairOrderItemDTO.setPurchasePrice(productLocalInfo.getPurchasePrice() == null ? 0d : productLocalInfo.getPurchasePrice());
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    model = new ModelMap();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(0, repairRemindEventDTOs.size());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(1, repairRemindEventDTOs.size());

    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrder.getStatusEnum());

    model = new ModelMap();
    repairOrderDTO.setSettledAmount(40);
    repairOrderDTO.setDebt(100);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal() - repairOrderDTO.getSettledAmount() - repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime("2012-03-24");
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());
    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(240, repairOrderDTO.getTotal(), 0.001);

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(0, repairRemindEventDTOs.size());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_SETTLED, repairOrder.getStatusEnum());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
        OrderTypes.REPAIR, repairOrder.getId());
    Assert.assertEquals(240, receivable.getTotal(), 0.001);
    Assert.assertEquals(100, receivable.getDebt(), 0.001);
    Assert.assertEquals(100, receivable.getDiscount(), 0.001);
    Assert.assertEquals(40, receivable.getSettledAmount(), 0.001);

    List<Debt> debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{repairOrderDTO.getCustomerId()});
    Assert.assertEquals(100, debts.get(0).getDebt(), 0.001);

    List<CustomerRecord> customerRecords = userWriter.getCustomerRecordByCustomerId(repairOrderDTO.getCustomerId());
    Assert.assertEquals(1, customerRecords.size());
    Assert.assertEquals(100, customerRecords.get(0).getTotalReceivable(), 0.001);
    Assert.assertEquals(140, customerRecords.get(0).getTotalAmount(), 0.001);

    inventory = txnWriter.getById(Inventory.class, repairOrderItemDTO.getProductId());
    Assert.assertEquals(23, inventory.getAmount(), 0.001);
  }


  @Test
  public void testRepairOrderWithDebitWithoutFinishPhase() throws Exception {
    ModelMap model = new ModelMap();
    String vehicleNumber = new String("苏E1255556");
    request.setParameter("vehicleNumber", vehicleNumber);
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");

    repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");

    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setMobile("13584876676");
    repairOrderDTO.setTotal(240D);

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand("brand");
    repairOrderItemDTO.setProductName("product");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());

    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");

    Assert.assertEquals(240, repairOrderDTO.getTotal(), 0.001);

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);

    Assert.assertEquals(1, repairRemindEventDTOs.size());
    Assert.assertEquals(2, repairRemindEventDTOs.get(0).getAmount(), 0.001);


    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), shopId);
    Assert.assertEquals("product", productDTO.getName());

    Inventory inventory = txnWriter.getById(Inventory.class, productLocalInfoDTO.getId());
    Assert.assertEquals(0, inventory.getAmount(), 0.001);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584876686");
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());

    PurchaseInventoryItemDTO itemDTO = new PurchaseInventoryItemDTO();
    itemDTO.setTotal(1100D);
    itemDTO.setAmount(100d);
    itemDTO.setPrice(11d);
    itemDTO.setProductName("new item");
    itemDTO.setProductId(repairRemindEventDTOs.get(0).getProductId());

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    openMessageSwitch();
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);

    Assert.assertEquals(1, repairRemindEventDTOs.size());


    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, repairOrder.getStatusEnum());

    model = new ModelMap();
    repairOrderDTO.setSettledAmount(40);
    repairOrderDTO.setDebt(100);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal() - repairOrderDTO.getSettledAmount() - repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime("2012-03-24");

//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
        OrderTypes.REPAIR, repairOrder.getId());
    Assert.assertEquals(240, receivable.getTotal(), 0.001);
    Assert.assertEquals(100, receivable.getDebt(), 0.001);
    Assert.assertEquals(100, receivable.getDiscount(), 0.001);
    Assert.assertEquals(40, receivable.getSettledAmount(), 0.001);

    List<Debt> debts = txnWriter.getAllDebtsByCustomerIds(shopId, new Long[]{repairOrderDTO.getCustomerId()});
    Assert.assertEquals(100, debts.get(0).getDebt(), 0.001);

    List<CustomerRecord> customerRecords = userWriter.getCustomerRecordByCustomerId(repairOrderDTO.getCustomerId());
    Assert.assertEquals(1, customerRecords.size());
    Assert.assertEquals(100, customerRecords.get(0).getTotalReceivable(), 0.001);
    Assert.assertEquals(140, customerRecords.get(0).getTotalAmount(), 0.001);

    inventory = txnWriter.getById(Inventory.class, repairOrderItemDTO.getProductId());
    Assert.assertEquals(98, inventory.getAmount(), 0.001);

  }

  @Test
  public void testRepairOrderWithTwoItemsThenDeleteOne() throws Exception {
    ModelMap model = new ModelMap();
    String vehicleNumber = new String("苏E1232325");
    request.setParameter("vehicleNumber", vehicleNumber);
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");
    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setMobile("13584876696");
    repairOrderDTO.setTotal(600D);

    repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work1");
    serviceDTO.setTotal(100d);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[2];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work2");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    repairOrderServiceDTOs[1] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand("brand1");
    repairOrderItemDTO.setProductName("product1");
    repairOrderItemDTO.setModel("");
    repairOrderItemDTO.setSpec("");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setTotal(240D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[2];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    RepairOrderItemDTO repairOrderItemDTO2 = new RepairOrderItemDTO();
    repairOrderItemDTO2.setBrand("brand2");
    repairOrderItemDTO2.setProductName("product2");
    repairOrderItemDTO2.setModel("");
    repairOrderItemDTO2.setSpec("");
    repairOrderItemDTO2.setPurchasePrice(100d);
    repairOrderItemDTO2.setPrice((160d));
    repairOrderItemDTO2.setInventoryAmount(0d);
    repairOrderItemDTO2.setAmount(1D);
    repairOrderItemDTO2.setTotal(160D);
    repairOrderItemDTO2.setProductType(1);
    repairOrderItemDTOs[1] = repairOrderItemDTO2;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));
    List<OrderTypes> orderTypes = new ArrayList<OrderTypes>();
    orderTypes.add(OrderTypes.REPAIR);
    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    List<ItemIndex>
        itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(4, itemIndexes.size());


    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());

    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(600, repairOrderDTO.getTotal(), 0.001);
    Assert.assertEquals(2, repairOrderDTO.getItemDTOs().length);


    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(2, repairRemindEventDTOs.size());


    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductLocalInfoDTO productLocalInfoDTO1 = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO1 = productService.getProductById(productLocalInfoDTO1.getProductId(), shopId);
    Assert.assertEquals("product1", productDTO1.getName());
    Inventory inventory1 = txnWriter.getById(Inventory.class, productLocalInfoDTO1.getId());
    Assert.assertEquals(0, inventory1.getAmount(), 0.001);

    ProductLocalInfoDTO productLocalInfoDTO2 = productService.getProductLocalInfoById(repairOrderItemDTO2.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO2 = productService.getProductById(productLocalInfoDTO2.getProductId(), shopId);
    Assert.assertEquals("product2", productDTO2.getName());
    Inventory inventory2 = txnWriter.getById(Inventory.class, productLocalInfoDTO2.getId());
    Assert.assertEquals(0, inventory2.getAmount(), 0.001);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584876766");
    purchaseInventoryDTO.setVestDateStr("2012-03-12 12:20");
    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());
    PurchaseInventoryItemDTO itemDTO1 = new PurchaseInventoryItemDTO();
    itemDTO1.setAmount(100d);
    itemDTO1.setPrice(11d);
    itemDTO1.setTotal(1100D);
    itemDTO1.setProductName("product1");
    itemDTO1.setSpec(productDTO1.getSpec());
    itemDTO1.setBrand("brand1");
    itemDTO1.setModel("");

    itemDTO1.setProductId(productDTO1.getProductLocalInfoId());

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO1;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(1, repairRemindEventDTOs.size());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(1, repairRemindEventDTOs.size());

    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584876866");
    purchaseInventoryDTO.setVestDateStr("2012-09-20 12:20");

    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());
    PurchaseInventoryItemDTO itemDTO2 = new PurchaseInventoryItemDTO();
    itemDTO2.setAmount(100d);
    itemDTO2.setPrice(11d);
    itemDTO2.setTotal(1100D);
    itemDTO2.setProductName("product2");
    itemDTO2.setSpec(productDTO2.getSpec());
    itemDTO1.setBrand("brand1");
    itemDTO1.setModel("");
    itemDTO2.setProductId(productDTO2.getProductLocalInfoId());

    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO2;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(2, repairRemindEventDTOs.size());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    request = new MockHttpServletRequest();
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    txnController.getRepairOrderByVehicleNumber(model, request, response);

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    model = new ModelMap();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrderDTO.getStatus());
    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrder.getStatusEnum());

    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(4, itemIndexes.size());

    InventorySearchIndex inventorySearchIndex1 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO1.getId());
    Assert.assertEquals(98, inventorySearchIndex1.getAmount(), 0.001);
    InventorySearchIndex inventorySearchIndex2 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventorySearchIndex2.getAmount(), 0.001);

    model = new ModelMap();
    repairOrderDTO.setSettledAmount(200);
    repairOrderDTO.setDebt(300);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal() - repairOrderDTO.getSettledAmount() - repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime("2012-04-09");
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
        OrderTypes.REPAIR, repairOrder.getId());
    Assert.assertEquals(600, receivable.getTotal(), 0.001);
    Assert.assertEquals(300, receivable.getDebt(), 0.001);
    Assert.assertEquals(100, receivable.getDiscount(), 0.001);

    //Now create another repair order


    model = new ModelMap();
    vehicleNumber = new String("苏E1232326");
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");
    repairOrderDTO.setCustomerName("Test" + System.currentTimeMillis());
    repairOrderDTO.setMobile(String.valueOf(System.currentTimeMillis()));
    repairOrderDTO.setTotal(600D);
    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work1");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    repairOrderServiceDTOs = new RepairOrderServiceDTO[2];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work2");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    repairOrderServiceDTOs[1] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand("brand1");
    repairOrderItemDTO.setProductName("product1");
    repairOrderItemDTO.setModel("");
    repairOrderItemDTO.setSpec("");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setProductType(1);
    repairOrderItemDTOs = new RepairOrderItemDTO[2];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    repairOrderItemDTO2 = new RepairOrderItemDTO();
    repairOrderItemDTO2.setBrand("brand2");
    repairOrderItemDTO2.setProductName("product2");
    repairOrderItemDTO2.setModel("");
    repairOrderItemDTO2.setSpec("");
    repairOrderItemDTO2.setPurchasePrice(100d);
    repairOrderItemDTO2.setPrice((160d));
    repairOrderItemDTO2.setInventoryAmount(0d);
    repairOrderItemDTO2.setAmount(1D);
    repairOrderItemDTO2.setProductType(1);
    repairOrderItemDTOs[1] = repairOrderItemDTO2;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(4, itemIndexes.size());

    inventorySearchIndex1 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO1.getId());
    Assert.assertEquals(96, inventorySearchIndex1.getAmount(), 0.001);
    inventorySearchIndex2 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO2.getId());
    Assert.assertEquals(98, inventorySearchIndex2.getAmount(), 0.001);

    //now modify the repair order
    //remove second service , modify first one
    //remoce second item   , modify first one

    repairOrderItemDTO.setBrand("brand1");
    repairOrderItemDTO.setProductName("product1");
    repairOrderItemDTO.setModel("");
    repairOrderItemDTO.setSpec("");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(5D);
    repairOrderItemDTO.setProductType(1);
    repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    serviceDTO.setService("work3");
    serviceDTO.setServiceId(null);
    serviceDTO.setServiceHistoryId(null);
    serviceDTO.setTotal(100d);
    repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(2, itemIndexes.size());
    Assert.assertEquals(1, repairOrderDTO.getServiceDTOs().length);
    Assert.assertEquals(1, repairOrderDTO.getItemDTOs().length);
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals(600, repairOrderDTO.getTotal(), 0.001);
    RepairOrderDTO repairOrderDTODb = rfiTxnService.getRepairOrderDTODetailById(repairOrderDTO.getId(), shopId);
    Assert.assertEquals(600, repairOrderDTODb.getTotal(), 0.001);
    RepairOrderItemDTO[] repairOrderItems = repairOrderDTODb.getItemDTOs();
    Assert.assertEquals(1, repairOrderItems.length);
    Assert.assertEquals(5, repairOrderItems[0].getAmount(), 0.001);
    RepairOrderServiceDTO[] repairOrderServices = repairOrderDTODb.getServiceDTOs();
    Assert.assertEquals(1, repairOrderServices.length);
    Assert.assertEquals("work3", repairOrderServices[0].getService());

    inventorySearchIndex1 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO1.getId());
    inventory1 = txnWriter.getById(Inventory.class, productLocalInfoDTO1.getId());
    Assert.assertEquals(93, inventorySearchIndex1.getAmount(), 0.001);
    Assert.assertEquals(93, inventory1.getAmount(), 0.001);

    inventorySearchIndex2 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventorySearchIndex2.getAmount(), 0.001);
    inventory2 = txnWriter.getById(Inventory.class, productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventory2.getAmount(), 0.001);

    model = new ModelMap();
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    url = repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(2, itemIndexes.size());
    Assert.assertEquals(1, repairOrderDTO.getServiceDTOs().length);
    Assert.assertEquals(1, repairOrderDTO.getItemDTOs().length);
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals(600, repairOrderDTO.getTotal(), 0.001);

    repairOrderDTODb = rfiTxnService.getRepairOrderDTODetailById(repairOrderDTO.getId(), shopId);
    Assert.assertEquals(600, repairOrderDTODb.getTotal(), 0.001);
    repairOrderItems = repairOrderDTODb.getItemDTOs();
    Assert.assertEquals(1, repairOrderItems.length);
    Assert.assertEquals(5, repairOrderItems[0].getAmount(), 0.001);
    repairOrderServices = repairOrderDTODb.getServiceDTOs();
    Assert.assertEquals(1, repairOrderServices.length);
    Assert.assertEquals("work3", repairOrderServices[0].getService());

    model = new ModelMap();
    repairOrderDTO.setSettledAmount(200);
    repairOrderDTO.setDebt(200);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal()-repairOrderDTO.getSettledAmount()-repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime("2012-04-09");

//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    url = repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
        OrderTypes.REPAIR, repairOrderDTODb.getId());
    Assert.assertEquals(600, receivable.getTotal(), 0.001);
    Assert.assertEquals(200, receivable.getDebt(), 0.001);
    Assert.assertEquals(200, receivable.getSettledAmount(), 0.001);
    Assert.assertEquals(200, receivable.getDiscount(), 0.001);

    inventorySearchIndex1 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO1.getId());
    inventory1 = txnWriter.getById(Inventory.class, productLocalInfoDTO1.getId());
    Assert.assertEquals(93, inventorySearchIndex1.getAmount(), 0.001);
    Assert.assertEquals(93, inventory1.getAmount(), 0.001);

    inventorySearchIndex2 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventorySearchIndex2.getAmount(), 0.001);
    inventory2 = txnWriter.getById(Inventory.class, productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventory2.getAmount(), 0.001);

  }

  @Test
  public void testRepairOrderWithTwoItemsThenAddOneMore() throws Exception {
    ModelMap model = new ModelMap();
    String vehicleNumber = new String("苏E1232325");
    request.setParameter("vehicleNumber", vehicleNumber);
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    openMessageSwitch();
    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");
    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setMobile("13584878666");
    repairOrderDTO.setTotal(600D);

    repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work1");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[2];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work2");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    repairOrderServiceDTOs[1] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    String productBrand1 = "brand1" + System.currentTimeMillis();
    repairOrderItemDTO.setBrand(productBrand1);
    String productName1 = "product1" + System.currentTimeMillis();
    repairOrderItemDTO.setProductName(productName1);
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[2];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    RepairOrderItemDTO repairOrderItemDTO2 = new RepairOrderItemDTO();
    String productBrand2 = "brand2" + System.currentTimeMillis();
    String productName2 = "product2" + System.currentTimeMillis();
    repairOrderItemDTO2.setBrand(productBrand2);
    repairOrderItemDTO2.setProductName(productName2);
    repairOrderItemDTO2.setPurchasePrice(100d);
    repairOrderItemDTO2.setPrice((160d));
    repairOrderItemDTO2.setInventoryAmount(0d);
    repairOrderItemDTO2.setAmount(1D);
    repairOrderItemDTO2.setProductType(1);
    repairOrderItemDTOs[1] = repairOrderItemDTO2;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));
    List<OrderTypes> orderTypes = new ArrayList<OrderTypes>();
    orderTypes.add(OrderTypes.REPAIR);

    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    List<ItemIndex>
        itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(4, itemIndexes.size());

    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());

    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(600, repairOrderDTO.getTotal(), 0.001);
    Assert.assertEquals(2, repairOrderDTO.getItemDTOs().length);


    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(2, repairRemindEventDTOs.size());


    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductLocalInfoDTO productLocalInfoDTO1 = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO1 = productService.getProductById(productLocalInfoDTO1.getProductId(), shopId);
    Assert.assertEquals(productName1, productDTO1.getName());
    Inventory inventory1 = txnWriter.getById(Inventory.class, productLocalInfoDTO1.getId());
    Assert.assertEquals(0, inventory1.getAmount(), 0.001);

    ProductLocalInfoDTO productLocalInfoDTO2 = productService.getProductLocalInfoById(repairOrderItemDTO2.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO2 = productService.getProductById(productLocalInfoDTO2.getProductId(), shopId);
    Assert.assertEquals(productName2, productDTO2.getName());
    Inventory inventory2 = txnWriter.getById(Inventory.class, productLocalInfoDTO2.getId());
    Assert.assertEquals(0, inventory2.getAmount(), 0.001);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584875666");
    purchaseInventoryDTO.setVestDateStr("2012-03-12 12:20");
    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());
    PurchaseInventoryItemDTO itemDTO1 = new PurchaseInventoryItemDTO();
    itemDTO1.setAmount(100d);
    itemDTO1.setPrice(11d);
    itemDTO1.setTotal(1100D);
    itemDTO1.setProductName(productDTO1.getName());
    itemDTO1.setSpec(productDTO1.getSpec());
    itemDTO1.setProductId(productDTO1.getProductLocalInfoId());

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO1;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(1, repairRemindEventDTOs.size());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(1, repairRemindEventDTOs.size());

    purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584874666");
    purchaseInventoryDTO.setVestDateStr("2012-03-20 12:20");

    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());
    PurchaseInventoryItemDTO itemDTO2 = new PurchaseInventoryItemDTO();
    itemDTO2.setAmount(100d);
    itemDTO2.setPrice(11d);
    itemDTO2.setTotal(1100D);
    itemDTO2.setProductName(productDTO2.getName());
    itemDTO2.setSpec(productDTO2.getSpec());
    itemDTO2.setProductId(productDTO2.getProductLocalInfoId());

    itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO2;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(2, repairRemindEventDTOs.size());

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.LACK);
    Assert.assertEquals(0, repairRemindEventDTOs.size());


    ProductDTO productDTO = new ProductDTO();
    productDTO.setName("product3" + System.currentTimeMillis());
    productDTO.setSpec("1232");
    productDTO.setProductVehicleBrand("多款");
    addInventory(shopId, productDTO, 100, 10);

    request = new MockHttpServletRequest();
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    txnController.getRepairOrderByVehicleNumber(model, request, response);

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    model = new ModelMap();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrderDTO.getStatus());
    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrder.getStatusEnum());

    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(4, itemIndexes.size());

    InventorySearchIndex inventorySearchIndex1 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO1.getId());
    Assert.assertEquals(98, inventorySearchIndex1.getAmount(), 0.001);
    InventorySearchIndex inventorySearchIndex2 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventorySearchIndex2.getAmount(), 0.001);

    // Add another service and item
    model = new ModelMap();

    repairOrderServiceDTOs = new RepairOrderServiceDTO[3];
    repairOrderServiceDTOs[0] = repairOrderDTO.getServiceDTOs()[0];
    repairOrderServiceDTOs[1] = repairOrderDTO.getServiceDTOs()[1];
    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work3");
    serviceDTO.setTotal(100d);
    repairOrderServiceDTOs[2] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    repairOrderItemDTOs = new RepairOrderItemDTO[3];
    repairOrderItemDTOs[0] = repairOrderDTO.getItemDTOs()[0];
    repairOrderItemDTOs[1] = repairOrderDTO.getItemDTOs()[1];
    RepairOrderItemDTO repairOrderItemDTO3 = new RepairOrderItemDTO();
    repairOrderItemDTO3.setBrand(productDTO.getBrand());
    repairOrderItemDTO3.setProductName(productDTO.getName());
    repairOrderItemDTO3.setPurchasePrice(100d);
    repairOrderItemDTO3.setPrice((100d));
    repairOrderItemDTO3.setInventoryAmount(0d);
    repairOrderItemDTO3.setAmount(3D);
    repairOrderItemDTO3.setProductType(1);
    repairOrderItemDTOs[2] = repairOrderItemDTO3;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);


    model = new ModelMap();
    repairOrderDTO.setTotal(1000D);
    repairOrderDTO.setSettledAmount(600);
    repairOrderDTO.setDebt(400);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal() - repairOrderDTO.getSettledAmount() - repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime("2012-04-09");
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.PENDING);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
        OrderTypes.REPAIR, repairOrder.getId());
    Assert.assertEquals(1000, receivable.getTotal(), 0.001);
    Assert.assertEquals(400, receivable.getDebt(), 0.001);
    Assert.assertEquals(0, receivable.getDiscount(), 0.001);
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    dto.setShopId(shopId);
    dto.setOrderId(repairOrderDTO.getId());
    dto.setSelectedOrderTypes(orderTypes);
    itemIndexes = searchWriter.searchItemIndex(dto,
        null,
        null,
        null,
        null);
    Assert.assertEquals(6, itemIndexes.size());
    Assert.assertEquals(3, repairOrderDTO.getServiceDTOs().length);
    Assert.assertEquals(3, repairOrderDTO.getItemDTOs().length);
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());

    RepairOrderDTO repairOrderDTODb = rfiTxnService.getRepairOrderDTODetailById(repairOrderDTO.getId(), shopId);
    Assert.assertEquals(1000, repairOrderDTODb.getTotal(), 0.001);
    RepairOrderItemDTO[] repairOrderItems = repairOrderDTODb.getItemDTOs();
    Assert.assertEquals(3, repairOrderItems.length);
    Assert.assertEquals(3, repairOrderItems[2].getAmount(), 0.001);
    RepairOrderServiceDTO[] repairOrderServices = repairOrderDTODb.getServiceDTOs();
    Assert.assertEquals(3, repairOrderServices.length);
    Assert.assertEquals("work3", repairOrderServices[2].getService());

    inventorySearchIndex1 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO1.getId());
    inventory1 = txnWriter.getById(Inventory.class, productLocalInfoDTO1.getId());
    Assert.assertEquals(98, inventorySearchIndex1.getAmount(), 0.001);
    Assert.assertEquals(98, inventory1.getAmount(), 0.001);

    inventorySearchIndex2 = searchWriter.getInventorySearchIndexByProductLocalInfoId(shopId,
        productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventorySearchIndex2.getAmount(), 0.001);
    inventory2 = txnWriter.getById(Inventory.class, productLocalInfoDTO2.getId());
    Assert.assertEquals(99, inventory2.getAmount(), 0.001);

  }


  public void populateVehiclesAndProduct() throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource resource = resourceLoader.getResource("车型数据简化.csv");
    InputStream inputStream = resource.getInputStream();
  }


  private void enterRepairOrder(RepairOrderDTO repairOrderDTO) {
    repairOrderDTO.setBrand("test");
  }

  @Test
  public void testGoodsHistory() throws Exception {
    Random random = new Random(47);
    String licenceNo = "苏E11559";
//    String btnType = "save";
    Long shopId = createShop();
    DecimalFormat df = new DecimalFormat("#.00");
    ModelMap model = new ModelMap();
    Double totalAmount = 0.0D;
    String timeStr = "2012-04-05 10:13";
    String hkTime = "2012-04-26";
    StringBuffer sb = new StringBuffer();

    char c = 'A';
    int itemNum = 2;
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[itemNum];
    for (int i = 0; i < itemNum; i++) {
      double amount = new Double(df.format(random.nextDouble() * 100));
      double price = new Double(df.format(random.nextDouble() * 100));
      repairOrderItemDTOs[i] = new RepairOrderItemDTO();
      repairOrderItemDTOs[i].setAmount(amount);
      repairOrderItemDTOs[i].setPrice(price);
      repairOrderItemDTOs[i].setTotal(amount * price);
      repairOrderItemDTOs[i].setProductName("产品" + ((char) (c + i)));
      repairOrderItemDTOs[i].setSpec("规格" + ((char) (c + i)));
      repairOrderItemDTOs[i].setInventoryAmount(0.0D);
      repairOrderItemDTOs[i].setProductType(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
      totalAmount = totalAmount + repairOrderItemDTOs[i].getTotal();
    }
    for (int i = 0; i < itemNum; i++) {
      sb.append(repairOrderItemDTOs[itemNum - 1 - i].getProductName()).append(",");
    }

    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    repairOrderDTO.setVechicle(licenceNo);
    repairOrderDTO.setLicenceNo(licenceNo);
    repairOrderDTO.setCustomerName("蔡先生");
    repairOrderDTO.setStartMileage(200D);
    repairOrderDTO.setFuelNumber("3");
    repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");
    repairOrderDTO.setStartDate(DateUtil.convertDateStringToDateLong(
        DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, timeStr));
    repairOrderDTO.setEndDateStr(timeStr);
    repairOrderDTO.setTotal(totalAmount);
    repairOrderDTO.setServiceType(OrderTypes.REPAIR);
    repairOrderDTO.setBrand("奇瑞");
    repairOrderDTO.setModel("旗云3");
    repairOrderDTO.setYear("2010");
    repairOrderDTO.setEngine("2.0L");
    repairOrderDTO.setSettledAmount(totalAmount / 2);
    repairOrderDTO.setMaintainTimeStr("2012-04-10");
    repairOrderDTO.setInsureTimeStr("2012-04-30");
    repairOrderDTO.setExamineTimeStr("2012-04-27");
    repairOrderDTO.setMobile("13512012567");
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);
    Double pm = totalAmount - repairOrderDTO.getSettledAmount();
    repairOrderDTO.setDebt(pm);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal()-repairOrderDTO.getSettledAmount()-repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime(hkTime);

    request.setParameter("vehicleNumber", licenceNo);
    request.getSession().setAttribute("shopId", shopId);
//    txnController.saveRepairOrder(model, repairOrderDTO, request, btnType);
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    RepairOrderDTO ro = (RepairOrderDTO) model.get("repairOrderDTO");
//    Assert.assertEquals(1, ro.getItemDTOs().length);

    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setVehicle(licenceNo);
    itemIndexDTO.setOrderType(OrderTypes.REPAIR);
    itemIndexDTO.setPageNo("1");
    goodsHistoryController.searchCarHistory(model, request, itemIndexDTO);

    List<ItemIndexDTO> itemIndexList = (List<ItemIndexDTO>) model.get("itemIndexList");

    Assert.assertEquals(1, itemIndexList.size());
    Assert.assertEquals(totalAmount, itemIndexList.get(0).getOrderTotalAmount());
    Assert.assertEquals(sb.substring(0, sb.length() - 1), itemIndexList.get(0).getItemName());

    Assert.assertEquals(licenceNo, itemIndexList.get(0).getVehicle());
    Assert.assertEquals(pm, itemIndexList.get(0).getArrears());
    Assert.assertEquals(hkTime, itemIndexList.get(0).getPaymentTimeStr());
  }

  /*
   * 1,施工单派单缺料,作废
   * 2,施工单派单未缺料,作废
   * 3,施工单完工,待交付作废
   * 3,施工交车结算,有欠款作废
   * test :
   * 1.客户记录是否正确
   * 2,orderIndex 记录是否正确
   * 3,itemIndex记录是否正确
   * 4,inventory,inventorySearchindex是否正确
   * 5,repairRemindEvents是否正确
   * 6,欠款总额是否正确
   * 7,营业统计是否正确
   */
  @Test
  public void testRepairOrderRepeal1() throws Exception {
    Long shopId = createShop();
    ProductDTO productDTO1 = createProductNoVehicle("QQ");
    VehicleDTO vehicleDTO = createVehicle();
    CustomerDTO customerDTO = createCustomer();
    ModelMap model = new ModelMap();
    request.getSession().setAttribute("shopId", shopId);
    RepairOrderDTO repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
    openMessageSwitch();
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    productDTO1.setProductLocalInfoId(repairOrderDTO.getItemDTOs()[0].getProductId());
    customerDTO.setId(repairOrderDTO.getCustomerId());
    CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
    Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, repairOrderDTO.getStatus());
    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
        shopId, repairOrderDTO.getId(), null);
    Assert.assertEquals(1, repairRemindEventDTOs.size());
    Assert.assertEquals(RepairRemindEventTypes.LACK, repairRemindEventDTOs.get(0).getEventType());
    Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
    Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
    Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
    Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
    Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
    Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
    Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
    Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());

    Assert.assertEquals(1, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
    OrderIndexDTO orderIndexDTO = searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId,
        customerDTO.getId()).get(0).toDTO();
    Assert.assertEquals(OrderTypes.REPAIR, orderIndexDTO.getOrderType());
    Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, orderIndexDTO.getOrderStatus());
    Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);

    List<ItemIndexDTO> itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
    Assert.assertEquals(2, itemIndexDTOs.size());

    InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
    Assert.assertEquals(0, inventoryDTO.getAmount(), 0.0001);
    InventorySearchIndexDTO inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
        productDTO1.getProductLocalInfoId());
    Assert.assertEquals(0, inventorySearchIndexDTO.getAmount(), 0.0001);
    Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
    Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
    Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
    Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
    Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
    Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
    Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
    Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());


    model = new ModelMap();
    txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(),null);
    unitTestSleepSecond();
    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, repairOrderDTO.getStatus());

    customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
        shopId, repairOrderDTO.getId(), null);
    Assert.assertEquals(0, repairRemindEventDTOs.size());
    Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
    Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
    Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
    Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
    Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
    Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
    Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
    Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());

    Assert.assertEquals(1, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
    orderIndexDTO = searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId,
        customerDTO.getId()).get(0).toDTO();
    Assert.assertEquals(OrderTypes.REPAIR, orderIndexDTO.getOrderType());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, orderIndexDTO.getOrderStatus());
    Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);

    itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
    Assert.assertEquals(2, itemIndexDTOs.size());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());


    inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
    Assert.assertEquals(0, inventoryDTO.getAmount(), 0.0001);
    inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
        productDTO1.getProductLocalInfoId());
    Assert.assertEquals(0, inventorySearchIndexDTO.getAmount(), 0.001);
    Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
    Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
    Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
    Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
    Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
    Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
    Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
    Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());

    // 施工单派单未缺料,作废

    addInventory(shopId, productDTO1, 100d, 10d);
    repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    productDTO1.setProductLocalInfoId(repairOrderDTO.getItemDTOs()[0].getProductId());
    customerDTO.setId(repairOrderDTO.getCustomerId());
    customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
    Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, repairOrderDTO.getStatus());
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
        shopId, repairOrderDTO.getId(), null);
    Assert.assertEquals(1, repairRemindEventDTOs.size());
    Assert.assertEquals(RepairRemindEventTypes.PENDING, repairRemindEventDTOs.get(0).getEventType());
    Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
    Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
    Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
    Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
    Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
    Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
    Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
    Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());

    Assert.assertEquals(2, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
    orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
        OrderStatus.REPAIR_DISPATCH, customerDTO.getId()).get(0).toDTO();
    Assert.assertEquals(OrderTypes.REPAIR, orderIndexDTO.getOrderType());
    Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, orderIndexDTO.getOrderStatus());
    Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);

    itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
    Assert.assertEquals(2, itemIndexDTOs.size());


    model = new ModelMap();
    txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(),null);
    unitTestSleepSecond();
    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, repairOrderDTO.getStatus());
    //1,客户记录
    customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
    Assert.assertEquals(0, txnService.getRepairRemindEventByShopIdAndOrderIdAndType(shopId, repairOrderDTO.getId(), null).size());
    Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
    Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
    Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
    Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
    Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
    Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
    Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
    Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());
//   * 2,orderIndex 记录是否正确
    Assert.assertEquals(2, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
    orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
        OrderStatus.REPAIR_REPEAL, customerDTO.getId()).get(0).toDTO();
    Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);
//   3itemIndex记录是否正确
    itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
    Assert.assertEquals(2, itemIndexDTOs.size());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());
//    * 4,inventory,inventorySearchindex是否正确
    inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
    Assert.assertEquals(100, inventoryDTO.getAmount(), 0.0001);
    inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
        productDTO1.getProductLocalInfoId());
    Assert.assertEquals(100, inventorySearchIndexDTO.getAmount(), 0.0001);
    Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
    Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
    Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
    Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
    Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
    Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
    Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
    Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());
//     * 5,repairRemindEvents是否正确
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
        shopId, repairOrderDTO.getId(), null);
    Assert.assertEquals(0, repairRemindEventDTOs.size());


    //派单 ,完工,作废
    model = new ModelMap();
    repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
    txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(), null);
    unitTestSleepSecond();
    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, repairOrderDTO.getStatus());
    //1,客户记录
    customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
    Assert.assertEquals(0, txnService.getRepairRemindEventByShopIdAndOrderIdAndType(shopId, repairOrderDTO.getId(), null).size());
    Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
    Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
    Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
    Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
    Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
    Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
    Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
    Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());
//   * 2,orderIndex 记录是否正确
    Assert.assertEquals(3, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
    orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
        OrderStatus.REPAIR_REPEAL, customerDTO.getId()).get(0).toDTO();
    Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);
//   3itemIndex记录是否正确
    itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
    Assert.assertEquals(2, itemIndexDTOs.size());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());
//    * 4,inventory,inventorySearchindex是否正确
    inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
    Assert.assertEquals(100, inventoryDTO.getAmount(), 0.0001);
    inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
        productDTO1.getProductLocalInfoId());
    Assert.assertEquals(100, inventorySearchIndexDTO.getAmount(), 0.0001);
    Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
    Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
    Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
    Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
    Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
    Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
    Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
    Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());
//     * 5,repairRemindEvents是否正确
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
        shopId, repairOrderDTO.getId(), null);
    Assert.assertEquals(0, repairRemindEventDTOs.size());

    //派单,完工,欠款,结算,作废
    model = new ModelMap();
    repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    repairOrderDTO.setSettledAmount(0d);
    repairOrderDTO.setDebt(200d);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal()-repairOrderDTO.getSettledAmount()-repairOrderDTO.getDebt());
    repairOrderDTO.setHuankuanTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(),null);
    unitTestSleepSecond();
    //1,客户记录
    customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
    Assert.assertEquals(0, txnService.getRepairRemindEventByShopIdAndOrderIdAndType(shopId, repairOrderDTO.getId(), null).size());
    Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
    Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
    Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
    Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
    Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
    Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
    Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
    Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());
//   * 2,orderIndex 记录是否正确
    Assert.assertEquals(4, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
    orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
        OrderStatus.REPAIR_REPEAL, customerDTO.getId()).get(0).toDTO();
    Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);
//   3itemIndex记录是否正确
    itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
    Assert.assertEquals(2, itemIndexDTOs.size());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
    Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());
//    * 4,inventory,inventorySearchindex是否正确
    inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
    Assert.assertEquals(100, inventoryDTO.getAmount(), 0.0001);
    inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
        productDTO1.getProductLocalInfoId());
    Assert.assertEquals(100, inventorySearchIndexDTO.getAmount(), 0.0001);
    Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
    Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
    Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
    Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
    Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
    Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
    Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
    Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());
//     * 5,repairRemindEvents是否正确
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
        shopId, repairOrderDTO.getId(), null);
    Assert.assertEquals(0, repairRemindEventDTOs.size());
//      * 6,欠款总额是否正确
    Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
        OrderTypes.REPAIR, repairOrderDTO.getId());
    Assert.assertEquals(240, receivable.getTotal(), 0.001);
    Assert.assertEquals(200, receivable.getDebt(), 0.001);
    Assert.assertEquals(40, receivable.getDiscount(), 0.001);
    Assert.assertEquals(ReceivableStatus.REPEAL, receivable.getStatusEnum());

    List<DebtDTO> debtDTOs = txnService.getDebtByShopIdAndOrderId(shopId, repairOrderDTO.getId());
    Assert.assertEquals(1, debtDTOs.size());
    Assert.assertEquals(240d, debtDTOs.get(0).getTotalAmount(), 0.001);
    Assert.assertEquals(0d, debtDTOs.get(0).getSettledAmount(), 0.001);
    Assert.assertEquals(200d, debtDTOs.get(0).getDebt(), 0.001);
    Assert.assertEquals(DebtStatus.REPEAL, debtDTOs.get(0).getStatus());

  }

  /**
   * 施工单使用会员信息进行结算
   *
   * 流程:创建车牌号->创建缺料施工单 ->验证 客户 车牌号是否保存
   * ->验证是否缺料带修->入库->验证是否变成来料带修->完工->会员结算验证->会员结算->会员信息验证
   * @throws Exception
   */
  @Test
  public void testRepairOrderWithMemberInfo() throws Exception {

    ModelMap model = new ModelMap();
    String vehicleNumber = new String("苏E99991");
    request.setParameter("vehicleNumber", vehicleNumber);
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);

    //创建一张会员卡
    MemberCardDTO memberCardDTO = createMemberCard(shopId, "测试卡");
    Assert.assertNotNull(memberCardDTO);
    long customerId = 10000001;
    String memberNo = "0001";
    String password = "1234";
    MemberDTO memberDTO = createMember(shopId, customerId, memberNo, password);
    Assert.assertNotNull(memberDTO);

    String url = txnController.getRepairOrderByVehicleNumber(model, request, response);
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(vehicleNumber, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(0, repairOrderDTO.getDebt(), 0.001);
    Assert.assertEquals("/txn/invoicing", url);

    //创建一张施工单
    repairOrderDTO.setBrand("雪佛兰");
    repairOrderDTO.setModel("赛欧");
    repairOrderDTO.setYear("2011");
    repairOrderDTO.setEngine("1.4L");
    repairOrderDTO.setShopId(shopId);
    repairOrderDTO.setCustomerName("Test");
    repairOrderDTO.setMobile("13584873666");
    repairOrderDTO.setTotal(240D);

    //创建两条施工内容:1.使用现金 2.使用会员计次服务
    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[2];
    repairOrderServiceDTOs[0] = serviceDTO;
    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("测试施工项目");
    serviceDTO.setConsumeType(ConsumeType.TIMES);
    repairOrderServiceDTOs[1] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    //创建一个材料单
    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand("brand");
    repairOrderItemDTO.setShopId(shopId);
    repairOrderItemDTO.setProductName("product");
    repairOrderItemDTO.setPurchasePrice(100d);
    repairOrderItemDTO.setPrice((120d));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    repairOrderItemDTO.setProductType(1);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    //施工单保存并校验
//    url = txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    url = repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    Assert.assertTrue(url.startsWith(getRepairOrderRedirectUrl(repairOrderDTO)));

    //施工单保存后判断客户是否保存
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<CustomerDTO> customerDTOs = userService.getCustomerByMobile(shopId, repairOrderDTO.getMobile());
    Assert.assertEquals(1, customerDTOs.size());

    userService.getVehicleByCustomerId(customerDTOs.get(0).getId());

    //施工单保存后判断车辆信息是否保存
    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
    Assert.assertEquals(1, vehicleDTOs.size());
    Assert.assertEquals(repairOrderDTO.getBrand(), vehicleDTOs.get(0).getBrand());

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    //施工单保存后判断单据总额是否对应
    Assert.assertEquals(240, repairOrderDTO.getTotal(), 0.001);

    //判断施工单保存后  是否生成两条施工内容 一个是现金 一个是计次划卡
    RepairOrderServiceDTO[] returnRepairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
    Assert.assertEquals(2, returnRepairOrderServiceDTOs.length);
    Assert.assertEquals(ConsumeType.MONEY.getType(), returnRepairOrderServiceDTOs[0].getConsumeType().getType());
    Assert.assertEquals(ConsumeType.TIMES.getType(), returnRepairOrderServiceDTOs[1].getConsumeType().getType());

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    //判断施工单派单后是否是缺料待修状态
    List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(),RepairRemindEventTypes.LACK);

    Assert.assertEquals(1, repairRemindEventDTOs.size());
    Assert.assertEquals(2, repairRemindEventDTOs.get(0).getAmount(), 0.001);

    //施工单保存后判断产品是否保存
    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(), repairOrderDTO.getShopId());
    ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), shopId);
    Assert.assertEquals("product", productDTO.getName());

    //进行入库操作
    Inventory inventory = txnWriter.getById(Inventory.class, productLocalInfoDTO.getId());
    Assert.assertEquals(0, inventory.getAmount(), 0.001);

    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setDeliveryDate(System.currentTimeMillis());
    purchaseInventoryDTO.setSupplier("test supplier");
    purchaseInventoryDTO.setMobile("13584872666");
    purchaseInventoryDTO.setVestDateStr("2012-03-20 12:20");

    request.setParameter("repairOrderId", repairOrderDTO.getId().toString());

    PurchaseInventoryItemDTO itemDTO = new PurchaseInventoryItemDTO();
    itemDTO.setAmount(100d);
    itemDTO.setPrice(11d);
    itemDTO.setTotal(1100D);

    itemDTO.setProductName("new item");
    itemDTO.setProductId(repairRemindEventDTOs.get(0).getProductId());

    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = itemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);

    //保存入库单
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request,response);
    unitTestSleepSecond();
    Assert.assertEquals("1", purchaseInventoryDTO.getReturnType());

    //入库之后提醒项目是否变成来料待修
    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(), RepairRemindEventTypes.INCOMING);

    Assert.assertEquals(1, repairRemindEventDTOs.size());

    request = new MockHttpServletRequest();
    request.setParameter("vehicleNumber", vehicleNumber);
    request.getSession().setAttribute("shopId", shopId);
    txnController.getRepairOrderByVehicleNumber(model, request, response);

    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    model = new ModelMap();

    //施工单完工提醒
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(repairOrderDTO.getShopId(),
        repairOrderDTO.getId(),RepairRemindEventTypes.PENDING);
    Assert.assertEquals(1, repairRemindEventDTOs.size());

    //判断施工单状态是否是 已完工
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrderDTO.getStatus());
    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderDTO.getId());
    Assert.assertEquals(OrderStatus.REPAIR_DONE, repairOrder.getStatusEnum());

    returnRepairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
    Assert.assertEquals(2, returnRepairOrderServiceDTOs.length);
    Assert.assertEquals(ConsumeType.MONEY.getType(), returnRepairOrderServiceDTOs[0].getConsumeType().getType());
    Assert.assertEquals(ConsumeType.TIMES.getType(), returnRepairOrderServiceDTOs[1].getConsumeType().getType());

    //判断实收和欠款是否正确
    model = new ModelMap();
    repairOrderDTO.setSettledAmount(240);
    repairOrderDTO.setDebt(0);
	  repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal()-repairOrderDTO.getSettledAmount()-repairOrderDTO.getDebt());
    //施工单不添加会员号码  校验
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    String resultStr = memberCheckerService.checkRepairOrderDTO(shopId, repairOrderDTO);
    Assert.assertNotNull(resultStr);
    Assert.assertEquals(MemberConstant.MEMBER_NO_NEED, resultStr);

    //施工单添加不存在的会员号码 校验
    repairOrderDTO.setAccountMemberNo("45678");
    resultStr = memberCheckerService.checkRepairOrderDTO(shopId, repairOrderDTO);
    Assert.assertNotNull(resultStr);
    Assert.assertEquals(MemberConstant.MEMBER_NOT_EXIST, resultStr);

    //密码不正确 校验
    repairOrderDTO.setAccountMemberNo(memberNo);
    resultStr = memberCheckerService.checkRepairOrderDTO(shopId, repairOrderDTO);
    Assert.assertNotNull(resultStr);
    Assert.assertEquals(MemberConstant.PASSWORD_NO_CORRECT, resultStr);

    //该会员无此项服务
    repairOrderDTO.setAccountMemberPassword(password);
    resultStr = memberCheckerService.checkRepairOrderDTO(shopId, repairOrderDTO);
    Assert.assertNotNull(resultStr);
    Assert.assertEquals(repairOrderServiceDTOs[1].getService() + "," + MemberConstant.MEMBER_NO_CONTAIN_SERVICE, resultStr);

    //更改施工单内容
    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    serviceDTO.setTotal(100d);
    repairOrderServiceDTOs = new RepairOrderServiceDTO[2];
    repairOrderServiceDTOs[0] = serviceDTO;
    serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("施工项目1");
    serviceDTO.setConsumeType(ConsumeType.TIMES);
    repairOrderServiceDTOs[1] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    //校验成功
    resultStr = memberCheckerService.checkRepairOrderDTO(shopId, repairOrderDTO);
    Assert.assertNotNull(resultStr);
    Assert.assertEquals(MemberConstant.MEMBER_VALIDATE_SUCCESS, resultStr);

    //施工单校验
    txnController.validateRepairOrder(request,repairOrderDTO);

    //施工单结算
//    txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
    repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

    //判断会员号是否存在
    Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
    Assert.assertNotNull(member);
    Assert.assertEquals("0001", member.getMemberNo());

    //判断该会员 所拥有的服务是否正确
    List<MemberServiceDTO> memberServiceDTOList = membersService.getMemberServiceEnabledByMemberId(shopId, member.getId());
    Assert.assertNotNull(memberServiceDTOList);
    Assert.assertEquals(2, memberServiceDTOList.size());

    //判断 后台保存的 service_id是否正确
    List<MemberServiceDTO> memberServiceDTOs = memberDTO.getMemberServiceDTOs();
    Long serviceId = 0L;
    for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
      if (memberServiceDTO.getServiceName().equals("施工项目1")) {
        serviceId = memberServiceDTO.getServiceId();
      }
    }
    //判断 后台保存的 service_id是否拿到
    Assert.assertNotSame(0, serviceId);

     //判断 后台会员的计次划卡次数是否被正确扣除
    for (MemberServiceDTO memberServiceDTO : memberServiceDTOList) {
      if (NumberUtil.isEqual(memberServiceDTO.getServiceId(),serviceId)) {
        Assert.assertEquals(19, memberServiceDTO.getTimes().intValue());
      }
    }
  }


	@Test
	public void testRepairOrderForRepairShop() throws Exception {
	  Long shopId = createShop();
    initRepairShopRoleResource(shopId);
	  ProductDTO productDTO1 = createProductNoVehicle("QQ");
	  VehicleDTO vehicleDTO = createVehicle();
	  CustomerDTO customerDTO = createCustomer();
	  ModelMap model = new ModelMap();
	  request.getSession().setAttribute("shopId", shopId);
	  RepairOrderDTO repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
	  openMessageSwitch();
//	  txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
	  repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
	  productDTO1.setProductLocalInfoId(repairOrderDTO.getItemDTOs()[0].getProductId());
	  customerDTO.setId(repairOrderDTO.getCustomerId());
	  CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
	  Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, repairOrderDTO.getStatus());
	  List<RepairRemindEventDTO> repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
	      shopId, repairOrderDTO.getId(), null);
	  Assert.assertEquals(1, repairRemindEventDTOs.size());
	  Assert.assertEquals(RepairRemindEventTypes.PENDING, repairRemindEventDTOs.get(0).getEventType());
	  Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
	  Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
	  Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
	  Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
	  Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
	  Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
	  Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
	  Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());

	  Assert.assertEquals(1, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
	  OrderIndexDTO orderIndexDTO = searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId,
	      customerDTO.getId()).get(0).toDTO();
	  Assert.assertEquals(OrderTypes.REPAIR, orderIndexDTO.getOrderType());
	  Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, orderIndexDTO.getOrderStatus());
	  Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);

	  List<ItemIndexDTO> itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
	  Assert.assertEquals(2, itemIndexDTOs.size());

	  InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(0, inventoryDTO.getAmount(), 0.0001);
	  Assert.assertEquals(2D, inventoryDTO.getNoOrderInventory(), 0.0001);
	  InventorySearchIndexDTO inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
	      productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(0, inventorySearchIndexDTO.getAmount(), 0.0001);
	  Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
	  Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
	  Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
	  Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
	  Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
	  Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
	  Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
	  Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());


	  model = new ModelMap();
	  txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(),null);
    unitTestSleepSecond();
	  repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, repairOrderDTO.getStatus());

	  customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
	  repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
	      shopId, repairOrderDTO.getId(), null);
	  Assert.assertEquals(0, repairRemindEventDTOs.size());
	  Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
	  Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
	  Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
	  Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
	  Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
	  Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
	  Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
	  Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());

	  Assert.assertEquals(1, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
	  orderIndexDTO = searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId,
	      customerDTO.getId()).get(0).toDTO();
	  Assert.assertEquals(OrderTypes.REPAIR, orderIndexDTO.getOrderType());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, orderIndexDTO.getOrderStatus());
	  Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);

	  itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
	  Assert.assertEquals(2, itemIndexDTOs.size());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());


	  inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(2, inventoryDTO.getAmount(), 0.0001);
	  Assert.assertEquals(2, inventoryDTO.getNoOrderInventory(), 0.0001);
	  inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
	      productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(2, inventorySearchIndexDTO.getAmount(), 0.001);
	  Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
	  Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
	  Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
	  Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
	  Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
	  Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
	  Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
	  Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());

	  // 施工单派单未缺料,作废    入库100个，10块钱一个

	  addInventory(shopId, productDTO1, 100d, 10d);
	  repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
		repairOrderDTO.getItemDTOs()[0].setAmount(150D);
		repairOrderDTO.getItemDTOs()[0].setPrice(15d);
		repairOrderDTO.getItemDTOs()[0].setTotal(15 * 150D);
    repairOrderDTO.setTotal(100 + 15 * 150D);
    repairOrderDTO.setStartDateStr("2012-3-20 12:00");
    repairOrderDTO.setEndDateStr("2012-3-20 12:00");
//	  txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();
//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
	  productDTO1.setProductLocalInfoId(repairOrderDTO.getItemDTOs()[0].getProductId());
	  customerDTO.setId(repairOrderDTO.getCustomerId());
	  customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
	  Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, repairOrderDTO.getStatus());
	  repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
	      shopId, repairOrderDTO.getId(), null);
	  Assert.assertEquals(1, repairRemindEventDTOs.size());
	  Assert.assertEquals(RepairRemindEventTypes.PENDING, repairRemindEventDTOs.get(0).getEventType());
	  Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
	  Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
	  Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
	  Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
	  Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
	  Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
	  Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
	  Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());

	  Assert.assertEquals(2, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
	  orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
	      OrderStatus.REPAIR_DISPATCH, customerDTO.getId()).get(0).toDTO();
	  Assert.assertEquals(OrderTypes.REPAIR, orderIndexDTO.getOrderType());
	  Assert.assertEquals(OrderStatus.REPAIR_DISPATCH, orderIndexDTO.getOrderStatus());
	  Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);

	  itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
	  Assert.assertEquals(2, itemIndexDTOs.size());
		InventoryDTO inventoryDTO2 = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(0, inventoryDTO2.getAmount(), 0.0001);
	  Assert.assertEquals(50d, inventoryDTO2.getNoOrderInventory(), 0.0001);
	  InventorySearchIndexDTO inventorySearchIndexDTO2 = searchService.getInventorySearchIndexById(shopId,
	      productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(0, inventorySearchIndexDTO2.getAmount(), 0.0001);
	  Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
	  Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
	  Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
	  Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
	  Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
	  Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
	  Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
	  Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());

	  model = new ModelMap();
	  txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(),null);
    unitTestSleepSecond();
	  repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, repairOrderDTO.getStatus());
	  //1,客户记录
	  customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
	  Assert.assertEquals(0, txnService.getRepairRemindEventByShopIdAndOrderIdAndType(shopId, repairOrderDTO.getId(), null).size());
	  Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
	  Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
	  Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
	  Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
	  Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
	  Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
	  Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
	  Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());
//   * 2,orderIndex 记录是否正确
	  Assert.assertEquals(2, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
	  orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
	      OrderStatus.REPAIR_REPEAL, customerDTO.getId()).get(0).toDTO();
	  Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);
//   3itemIndex记录是否正确
	  itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
	  Assert.assertEquals(2, itemIndexDTOs.size());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());
//    * 4,inventory,inventorySearchindex是否正确
	  inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(150, inventoryDTO.getAmount(), 0.0001);
	  inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
	      productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(150, inventorySearchIndexDTO.getAmount(), 0.0001);
	  Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
	  Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
	  Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
	  Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
	  Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
	  Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
	  Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
	  Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());
//     * 5,repairRemindEvents是否正确
	  repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
	      shopId, repairOrderDTO.getId(), null);
	  Assert.assertEquals(0, repairRemindEventDTOs.size());


	  //派单 ,完工,作废
	  model = new ModelMap();
	  repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
//	  txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
//	  txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();


	  txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(),null);
    unitTestSleepSecond();
	  repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, repairOrderDTO.getStatus());
	  //1,客户记录
	  customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
	  Assert.assertEquals(0, txnService.getRepairRemindEventByShopIdAndOrderIdAndType(shopId, repairOrderDTO.getId(), null).size());
	  Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
	  Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
	  Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
	  Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
	  Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
	  Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
	  Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
	  Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());
//   * 2,orderIndex 记录是否正确
	  Assert.assertEquals(3, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
	  orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
	      OrderStatus.REPAIR_REPEAL, customerDTO.getId()).get(0).toDTO();
	  Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);
//   3itemIndex记录是否正确
	  itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
	  Assert.assertEquals(2, itemIndexDTOs.size());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());
//    * 4,inventory,inventorySearchindex是否正确
	  inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(150, inventoryDTO.getAmount(), 0.0001);
	  inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
	      productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(150, inventorySearchIndexDTO.getAmount(), 0.0001);
	  Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
	  Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
	  Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
	  Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
	  Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
	  Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
	  Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
	  Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());
//     * 5,repairRemindEvents是否正确
	  repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
        shopId, repairOrderDTO.getId(), null);
	  Assert.assertEquals(0, repairRemindEventDTOs.size());

	  //派单,完工,欠款,结算,作废
	  model = new ModelMap();
	  repairOrderDTO = generateRepairOrderDTO(productDTO1, vehicleDTO, customerDTO, request);
//	  txnController.saveRepairOrder(model, repairOrderDTO, request, "save");
    repairController.dispatchRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

//    repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
//	  txnController.saveRepairOrder(model, repairOrderDTO, request, "finish");
    repairController.finishRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

	  repairOrderDTO.setSettledAmount(0d);
	  repairOrderDTO.setDebt(200d);
		repairOrderDTO.setOrderDiscount(repairOrderDTO.getTotal()-repairOrderDTO.getSettledAmount()-repairOrderDTO.getDebt());
	  repairOrderDTO.setHuankuanTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
//	  txnController.saveRepairOrder(model, repairOrderDTO, request, "account");
	  repairController.accountRepairOrder(model, repairOrderDTO, request);
    unitTestSleepSecond();

	  txnController.repairOrderRepeal(model, request, response, repairOrderDTO.getIdStr(),null);
    unitTestSleepSecond();
	  //1,客户记录
	  customerRecordDTO = userService.getCustomerRecordByCustomerId(customerDTO.getId()).get(0);
	  Assert.assertEquals(0, txnService.getRepairRemindEventByShopIdAndOrderIdAndType(shopId, repairOrderDTO.getId(), null).size());
	  Assert.assertEquals(0, customerRecordDTO.getTotalAmount(), 0.0001);
	  Assert.assertEquals(0, customerRecordDTO.getTotalReceivable(), 0.0001);
	  Assert.assertEquals(repairOrderDTO.getTotal(), customerRecordDTO.getLastAmount(), 0.0001);
	  Assert.assertEquals(vehicleDTO.getBrand(), customerRecordDTO.getBrand());
	  Assert.assertEquals(vehicleDTO.getModel(), customerRecordDTO.getModel());
	  Assert.assertEquals(customerDTO.getMobile(), customerRecordDTO.getMobile());
	  Assert.assertEquals(customerDTO.getName(), customerRecordDTO.getName());
	  Assert.assertEquals(repairOrderDTO.getVechicle(), customerRecordDTO.getLicenceNo());
//   * 2,orderIndex 记录是否正确
	  Assert.assertEquals(4, searchService.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, customerDTO.getId()).size());
	  orderIndexDTO = searchService.getOrderIndexByOrderId(shopId, repairOrderDTO.getId(), OrderTypes.REPAIR,
	      OrderStatus.REPAIR_REPEAL, customerDTO.getId()).get(0).toDTO();
	  Assert.assertEquals(repairOrderDTO.getTotal(), orderIndexDTO.getOrderTotalAmount(), 0.0001);
//   3itemIndex记录是否正确
	  itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId, repairOrderDTO.getId());
	  Assert.assertEquals(2, itemIndexDTOs.size());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(0).getOrderStatus());
	  Assert.assertEquals(OrderStatus.REPAIR_REPEAL, itemIndexDTOs.get(1).getOrderStatus());
//    * 4,inventory,inventorySearchindex是否正确
	  inventoryDTO = txnService.getInventoryAmount(shopId, productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(150, inventoryDTO.getAmount(), 0.0001);
	  inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId,
	      productDTO1.getProductLocalInfoId());
	  Assert.assertEquals(150, inventorySearchIndexDTO.getAmount(), 0.0001);
	  Assert.assertEquals(productDTO1.getName(), inventorySearchIndexDTO.getProductName());
	  Assert.assertEquals(productDTO1.getBrand(), inventorySearchIndexDTO.getProductBrand());
	  Assert.assertEquals(productDTO1.getSpec(), inventorySearchIndexDTO.getProductSpec());
	  Assert.assertEquals(productDTO1.getModel(), inventorySearchIndexDTO.getProductModel());
	  Assert.assertEquals(productDTO1.getProductVehicleBrand(), inventorySearchIndexDTO.getBrand());
	  Assert.assertEquals(productDTO1.getProductVehicleModel(), inventorySearchIndexDTO.getModel());
	  Assert.assertEquals(productDTO1.getProductVehicleEngine(), inventorySearchIndexDTO.getEngine());
	  Assert.assertEquals(productDTO1.getProductVehicleYear(), inventorySearchIndexDTO.getYear());
//     * 5,repairRemindEvents是否正确
	  repairRemindEventDTOs = txnService.getRepairRemindEventByShopIdAndOrderIdAndType(
	      shopId, repairOrderDTO.getId(), null);
	  Assert.assertEquals(0, repairRemindEventDTOs.size());
//      * 6,欠款总额是否正确
	  Receivable receivable = txnWriter.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,
	      OrderTypes.REPAIR, repairOrderDTO.getId());
	  Assert.assertEquals(240, receivable.getTotal(), 0.001);
	  Assert.assertEquals(200, receivable.getDebt(), 0.001);
	  Assert.assertEquals(40, receivable.getDiscount(), 0.001);
	  Assert.assertEquals(ReceivableStatus.REPEAL, receivable.getStatusEnum());

	  List<DebtDTO> debtDTOs = txnService.getDebtByShopIdAndOrderId(shopId, repairOrderDTO.getId());
	  Assert.assertEquals(1, debtDTOs.size());
	  Assert.assertEquals(240d, debtDTOs.get(0).getTotalAmount(), 0.001);
	  Assert.assertEquals(0d, debtDTOs.get(0).getSettledAmount(), 0.001);
	  Assert.assertEquals(200d, debtDTOs.get(0).getDebt(), 0.001);
	  Assert.assertEquals(DebtStatus.REPEAL, debtDTOs.get(0).getStatus());

	}


}
