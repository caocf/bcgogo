package com.bcgogo;

import com.bcgogo.admin.AdminController;
import com.bcgogo.admin.StaffManageController;
import com.bcgogo.common.Result;
import com.bcgogo.config.ConfigController;
import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ShopBalance;
import com.bcgogo.config.service.*;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.customer.CustomerController;
import com.bcgogo.customer.MemberController;
import com.bcgogo.customer.SmsController;
import com.bcgogo.customer.UnitLinkController;
import com.bcgogo.enums.*;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.NotificationServiceFactory;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.model.MessageTemplate;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.notification.service.NotificationService;
import com.bcgogo.notification.service.SmsService;
import com.bcgogo.payment.ChinapayController;
import com.bcgogo.payment.PaymentServiceFactory;
import com.bcgogo.payment.model.PaymentDaoManager;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.product.ProductController;
import com.bcgogo.product.ProductServiceFactory;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductWriter;
import com.bcgogo.product.service.BaseProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.product.service.ProductService;
import com.bcgogo.product.service.ProductSolrService;
import com.bcgogo.remind.RemindController;
import com.bcgogo.schedule.bean.SmsSendSchedule;
import com.bcgogo.search.SearchController;
import com.bcgogo.search.SearchServiceFactory;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchMemoryConditionDTO;
import com.bcgogo.search.model.CurrentUsedProduct;
import com.bcgogo.search.model.CurrentUsedVehicle;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.CurrentUsed.IVehicleCurrentUsedService;
import com.bcgogo.search.service.CurrentUsed.ProductCurrentUsedService;
import com.bcgogo.search.service.*;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.StatServiceFactory;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.stat.service.IServiceVehicleCountService;
import com.bcgogo.stat.service.ServiceVehicleCountService;
import com.bcgogo.txn.*;
import com.bcgogo.txn.bcgogoListener.orderEvent.OrderSavedEvent;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.html.ActiveRecommendSupplierHtmlBuilder;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.messageCenter.INoticeService;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.web.GoodsStorageService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.UserController;
import com.bcgogo.user.UserServiceFactory;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.*;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.Module;
import com.bcgogo.user.model.permission.ShopRole;
import com.bcgogo.user.model.permission.UserGroupRole;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.permission.*;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WeChatController;
import com.bcgogo.wx.user.WXAccountDTO;
import com.bcgogo.wx.user.WXUserDTO;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

public class AbstractTest {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractTest.class);
  protected RFGoodBuyController buyController;
  protected GoodStorageController goodsStorageController;
  protected UnitLinkController unitLinkController;
  protected GoodSaleController saleController;
  protected ConfigController configController;
  protected ChinapayController chinapayController;
  protected MockHttpServletRequest request;
  protected MockHttpServletResponse response;
  protected TxnDaoManager txnDaoManager;
  protected TxnWriter txnWriter;
  protected ProductDaoManager productDaoManager;
  protected SearchDaoManager searchDaoManager;
  protected ConfigDaoManager configDaoManager;
  protected ProductWriter productWriter;
  protected UserWriter userWriter;
  protected SearchWriter searchWriter;
  protected SearchController searchController;
  protected SupplierPayableController supplierPayableController;
  protected ISupplierPayableService supplierPayableService;
  //  protected INotificationService notificationService;
//  protected ISmsRechargeService smsRechargeService;
//  protected IConfigService configService;
  protected IChinapayService chinapayService;
  //  protected SmsSendSchedule smsSendSchedule;
//  protected IUserService userService;
  protected CustomerController customerController;
  protected IProductSolrService productSolrService;
  protected IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
  protected IVehicleCurrentUsedService vehicleCurrentUsedService = ServiceManager.getService(IVehicleCurrentUsedService.class);
  protected ProductController productController;
  protected AdminController adminController;

  protected NotificationService notificationService;
  protected SmsRechargeService smsRechargeService;
  protected ShopBalanceService shopBalanceService;
  protected ProductService productService;
  protected CustomerService customerService;
  protected ConfigService configService;
  protected SmsService smsService;
  protected ServiceVehicleCountService iscService;
  protected SearchService searchService;
  protected BaseProductService baseProductService;
  protected IInventoryService inventoryService;
  protected IItemIndexService itemIndexService;
  protected IOrderIndexService orderIndexService;
  protected IMembersService membersService;
  protected IProductHistoryService productHistoryService;

  protected IGoodsStorageService goodsStorageService;
  protected SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
  protected IUserService userService;
  protected IShopVersionService shopService;
  protected TxnController txnController;
  protected RepairController repairController;
  protected ArrearsController arrearsController;
  protected GoodsHistoryController goodsHistoryController;
  protected RemindController remindController;
  protected GoodsReturnController goodsReturnController;
  protected PurchaseReturnService purchaseReturnService;
  protected WeChatController weChatController;

  protected ModelMap modelMap;
  protected ShopDTO shopDTO;
  protected TxnService txnService;
  protected RFTxnService rfiTxnService;
  protected IInventoryService iInventoryService;
  protected UserDaoManager userDaoManager;
  protected PaymentDaoManager paymentDaoManager;
  protected IUserCacheService userCacheService;
  protected IUserGroupService userGroupService;
  protected IRoleService roleService;
  protected IResourceService resourceService;
  protected ISupplierRecordService supplierRecordService;
  protected ISupplierService supplierService;

  protected UserController userController;
  protected StockSearchController stockSearchController;
  protected MemberController memberController;
  protected StaffManageController staffManageController;

  protected CategoryController categoryController;
  protected WashBeautyController washBeautyController;
  protected SmsController smsController;

  protected DraftOrderController draftOrderController;
  protected IDraftOrderService draftOrderService;
  protected IServiceVehicleCountService serviceVehicleCountService;
  protected IRepairService repairService;
  protected IInsuranceService insuranceService;
  protected IPickingService pickingService;
  protected RemindEventStrategySelector remindEventStrategySelector;
  protected ActiveRecommendSupplierHtmlBuilder activeRecommendSupplierHtmlBuilder;
  protected IApplyService applyService;
  protected IOperationLogService operationLogService;
  protected INoticeService noticeService;
  protected IGoodSaleService goodSaleService;
  protected IProductInStorageService productInStorageService;
  protected IProductOutStorageService productOutStorageService;
  protected IProductThroughService productThroughService;

  protected IWXService wxService;
  protected IWXAccountService accountService;
  protected IWXMsgSender sender;
  protected IWXUserService wxUserService;
  protected IWXTxnService wxTxnService;
  protected IWXArticleService articleService;

  @BeforeClass
  public static void init() throws Exception {
    XmlWebApplicationContext ctx;

    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("unit.test", "true");
    System.setProperty("solr.solr.home", "../search/src/test/resources/solr");
    jpaProperties.put("bcgogo.dataSource.url",
      "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
        ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");

    SimpleNamingContextBuilder.emptyActivatedContextBuilder();

    new ConfigServiceFactory(jpaProperties);
    new UserServiceFactory(jpaProperties);
    new NotificationServiceFactory(jpaProperties);
    new SearchServiceFactory(jpaProperties);
    new ProductServiceFactory(jpaProperties);
    new TxnServiceFactory(jpaProperties);
    new StatServiceFactory(jpaProperties);
    new PaymentServiceFactory(jpaProperties);

    SolrClientHelper.unitTestClear();


    String[] paths = {};
    ctx = new XmlWebApplicationContext();
    ctx.setConfigLocations(paths);
    ctx.setServletContext(new MockServletContext(""));
    ctx.refresh();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
    configService.setConfig("SmsMarketingSenderStrategy", SmsConstant.SmsLianYuConstant.name, ShopConstant.BC_SHOP_ID);
    configService.setConfig("SmsIndustrySenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
    configService.setConfig("SelectOptionNumber", "5", ShopConstant.BC_SHOP_ID);

    configService.setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);

  }

  public void createShopBalance(Long shopId) {
    ShopBalance shopBalance = new ShopBalance();
    shopBalance.setSmsBalance(100d);
    shopBalance.setShopId(shopId);
    shopBalance.setRechargeTotal(100d);
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(shopBalance);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  protected void initTxnControllers(AbstractTxnController... controllers) {
    initServices();
    for (AbstractTxnController controller : controllers) {
      controller.setConfigService(configService);
      controller.setCustomerService(customerService);
//      controller.setGoodsBuyService(goodsBuyService);
//      controller.setGoodsIndexService(goodsIndexService);
      controller.setGoodSaleService(goodSaleService);
      controller.setGoodsStorageService(goodsStorageService);
//      controller.setGeneralTxnService(generalTxnService);
      controller.setInventoryService(inventoryService);
      controller.setItemIndexService(itemIndexService);
//      controller.setWashService(washService);
      controller.setOrderIndexService(orderIndexService);
      controller.setProductService(productService);
      controller.setProductSolrService(productSolrService);
      controller.setSearchService(searchService);
      controller.setSmsService(smsService);
      controller.setUserService(userService);
      controller.setTxnService(txnService);
      controller.setInventoryService(inventoryService);
      controller.setRfiTxnService(rfiTxnService);
      controller.setSupplierPayableService(supplierPayableService);
      controller.setSupplierService(supplierService);
      controller.setProductHistoryService(productHistoryService);
      controller.setServiceVehicleCountService(serviceVehicleCountService);
      controller.setRepairService(repairService);
      controller.setInsuranceService(insuranceService);
      controller.setPickingService(pickingService);
      controller.setActiveRecommendSupplierHtmlBuilder(activeRecommendSupplierHtmlBuilder);
      controller.setProductInStorageService(productInStorageService);
      controller.setProductOutStorageService(productOutStorageService);
      controller.setProductThroughService(productThroughService);
    }
  }

  protected void initServices() {
    configService = ServiceManager.getService(ConfigService.class);
    notificationService = ServiceManager.getService(NotificationService.class);
    smsRechargeService = ServiceManager.getService(SmsRechargeService.class);
    shopBalanceService = ServiceManager.getService(ShopBalanceService.class);
    searchService = ServiceManager.getService(SearchService.class);
    customerService = ServiceManager.getService(CustomerService.class);
    smsService = ServiceManager.getService(SmsService.class);
    iscService = ServiceManager.getService(ServiceVehicleCountService.class);
    userService = ServiceManager.getService(UserService.class);
    productService = ServiceManager.getService(ProductService.class);
    baseProductService = ServiceManager.getService(BaseProductService.class);
    orderIndexService = ServiceManager.getService(OrderIndexService.class);
    productSolrService = ServiceManager.getService(ProductSolrService.class);
    txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    activeRecommendSupplierHtmlBuilder = new ActiveRecommendSupplierHtmlBuilder();
    activeRecommendSupplierHtmlBuilder.setActiveRecommendSupplierService(ServiceManager.getService(IActiveRecommendSupplierService.class));

//    generalTxnService = ServiceManager.getService(GeneralTxnService.class);
    inventoryService = ServiceManager.getService(InventoryService.class);
    itemIndexService = ServiceManager.getService(ItemIndexService.class);
//    goodsBuyService = ServiceManager.getService(GoodsBuyService.class);
//    goodsIndexService = ServiceManager.getService(GoodsIndexService.class);
    goodSaleService = ServiceManager.getService(IGoodSaleService.class);
    goodsStorageService = ServiceManager.getService(GoodsStorageService.class);
//    washService = ServiceManager.getService(WashService.class);
    repairService = ServiceManager.getService(RepairService.class);
//    goodsReturnService = ServiceManager.getService(GoodsReturnService.class);
    productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    txnWriter = txnDaoManager.getWriter();

    userGroupService = ServiceManager.getService(UserGroupService.class);
    userCacheService = ServiceManager.getService(UserCacheService.class);
    resourceService = ServiceManager.getService(ResourceService.class);
    roleService = ServiceManager.getService(RoleService.class);

    txnService = ServiceManager.getService(TxnService.class);
    rfiTxnService = ServiceManager.getService(RFTxnService.class);
    supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    supplierRecordService = ServiceManager.getService(ISupplierRecordService.class);
    supplierService = ServiceManager.getService(ISupplierService.class);
    serviceVehicleCountService = ServiceManager.getService(IServiceVehicleCountService.class);
    repairService = ServiceManager.getService(IRepairService.class);
    insuranceService = ServiceManager.getService(IInsuranceService.class);
    pickingService = ServiceManager.getService(IPickingService.class);
    applyService = ServiceManager.getService(IApplyService.class);
    operationLogService = ServiceManager.getService(IOperationLogService.class);
    noticeService = ServiceManager.getService(INoticeService.class);
    productInStorageService = ServiceManager.getService(IProductInStorageService.class);
    productOutStorageService = ServiceManager.getService(IProductOutStorageService.class);
    productThroughService = ServiceManager.getService(IProductThroughService.class);

  }

  public SalesOrderDTO createSalesOrderDTO(ProductDTO productDTO, double amount, double price, double payAmount) {
    SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
    salesOrderDTO.setCustomer("User1");
    salesOrderDTO.setContact("1311111111");
    salesOrderDTO.setMobile("1311111111");
    salesOrderDTO.setEditDateStr("2012-02-15");
    salesOrderDTO.setVestDateStr("2012-02-15 12:20");
    salesOrderDTO.setSettledAmount(payAmount);
    salesOrderDTO.setSettledAmountHid(0D);
    salesOrderDTO.setDebt(amount * price - payAmount);
    salesOrderDTO.setTotal(amount * price);
    salesOrderDTO.setShopId(productDTO.getShopId());
    salesOrderDTO.setBrand("牛顿");

    SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO();
    salesOrderItemDTO.setShopId(productDTO.getShopId());
    salesOrderItemDTO.setProductName(productDTO.getName());
    salesOrderItemDTO.setSpec(productDTO.getSpec());
    salesOrderItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
    salesOrderItemDTO.setPrice(price);
    salesOrderItemDTO.setBrand("牛顿");
    salesOrderItemDTO.setAmount(amount);
    salesOrderItemDTO.setProductId(productDTO.getProductLocalInfoId());
    salesOrderItemDTO.setTotal(price * amount);
    SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[1];
    itemDTOs[0] = salesOrderItemDTO;
    salesOrderDTO.setItemDTOs(itemDTOs);
    return salesOrderDTO;
  }


  @AfterClass
  public static void terminate() {
  }

  private UserDTO createUser(Long shopId) throws BcgogoException {
    UserDTO userDTO = new UserDTO();
    userDTO.setUserName("test");
    userDTO.setPassword("test");
    userDTO.setStatusEnum(Status.active);
    userDTO = userService.createUser(userDTO);
    return userDTO;
  }

  public Long createShop() throws Exception {
    initServices();
    adminController = new AdminController();
    userController = new UserController();
    baseProductService = ServiceManager.getService(BaseProductService.class);
    if (txnController == null) {
      txnController = new TxnController();
    }
    goodsStorageController = new GoodStorageController();
    staffManageController = new StaffManageController();
    initTxnControllers(goodsStorageController);
    txnController.setConfigService(configService);
    txnController.setSmsService(smsService);
    txnController.setSearchService(searchService);
    txnController.setCustomerService(customerService);
    txnController.setSmsService(smsService);
    txnController.setServiceVehicleCountService(iscService);
    txnController.setRfiTxnService(rfiTxnService);
    txnController.setTxnService(txnService);
    txnController.setUserService(userService);
    txnController.setProductService(productService);


    txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    txnWriter = txnDaoManager.getWriter();
    productSolrService = ServiceManager.getService(IProductSolrService.class);
    productDaoManager = ServiceManager.getService(ProductDaoManager.class);
    paymentDaoManager = ServiceManager.getService(PaymentDaoManager.class);
    productWriter = productDaoManager.getWriter();
    userDaoManager = ServiceManager.getService(UserDaoManager.class);
    userWriter = userDaoManager.getWriter();
    SearchDaoManager searchDaoManager = ServiceManager.getService(SearchDaoManager.class);
    searchWriter = searchDaoManager.getWriter();
    searchController = new SearchController();
    chinapayController = new ChinapayController();
//    notificationService = ServiceManager.getService(INotificationService.class);
//    customerService = ServiceManager.getService(ICustomerService.class);
//    smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
//    userService =  ServiceManager.getService(IUserService.class);
    chinapayService = ServiceManager.getService(IChinapayService.class);
    iInventoryService = ServiceManager.getService(IInventoryService.class);
    shopDTO = new ShopDTO();
    shopDTO.setShopStatus(ShopStatus.REGISTERED_PAID);
    shopDTO.setAccount("test");
    shopDTO.setName("test");
    shopDTO.setMobile("13584876666");
    shopDTO.setStoreManagerMobile("13584876667");
    shopDTO.setShopKind(ShopKind.TEST);
    ServiceManager.getService(IShopService.class).createShop(shopDTO);

    //版本
    ShopVersionDTO shopVersionDTO = new ShopVersionDTO();
    shopVersionDTO.setName("INTEGRATED_SHOP");
    shopVersionDTO.setValue("testValue");
    ServiceManager.getService(IShopVersionService.class).saveOrUpdateShopVersion(shopVersionDTO);

    smsRechargeService = ServiceManager.getService(SmsRechargeService.class);
    configService.setConfig("PaymentQueryTag", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("MOCK_PAYMENT", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("MerId", "808080060692183", ShopConstant.BC_SHOP_ID);
    configService.setConfig("Version", "20100401", ShopConstant.BC_SHOP_ID);
    configService.setConfig("CurId", "156", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ReturnMerBgUrl", "http://122.193.109.138:8000/web/chinapay.do?method=receive", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ReturnMerPgUrl", "http://122.193.109.138:8000/web/smsrecharge.do?method=smsrechargecomplete", ShopConstant.BC_SHOP_ID);
    configService.setConfig("GateId", "0001", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ShareType", "0001", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ShareA", "00055916^", ShopConstant.BC_SHOP_ID);
    configService.setConfig("MerPriKeyPath", "C:\\Tomcat\\Tomcat6.0\\lib\\MerPrK_808080060692212_20120606105157.key", ShopConstant.BC_SHOP_ID);
    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("PermissionTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
    configService.setConfig("SmsMarketingSenderStrategy", SmsConstant.SmsLianYuConstant.name, ShopConstant.BC_SHOP_ID);
    configService.setConfig("SmsIndustrySenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_3TONG_TRICOM_URL", "http://3tong.cn:8080/ema_new/http/SendSms", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_3TONG_ACCOUNT", "dh6763", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_3TONG_PASSWORD", "4ac74ac5bd65f4526820746c28dd7b", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_TRICOM_URL", "http://www.smswe.com:50000/sms/services/sms/", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_KEY", "richarapi", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_SECRET", "560e4c1e9d609840497ef45e38ec9f70f35427", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_USERNAME", "richar_ji", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_PASSWORD", "hfps860621_", ShopConstant.BC_SHOP_ID);

    ShopBalance shopBalance = new ShopBalance();
    shopBalance.setSmsBalance(100d);
    shopBalance.setShopId(shopDTO.getId());
    shopBalance.setRechargeTotal(100d);
    shopBalanceService.createSmsBalance(shopBalance.toDTO());
    productController = new ProductController();

    shopService = ServiceManager.getService(IShopVersionService.class);
    userCacheService = ServiceManager.getService(IUserCacheService.class);
    userGroupService = ServiceManager.getService(IUserGroupService.class);
    roleService = ServiceManager.getService(IRoleService.class);
    resourceService = ServiceManager.getService(IResourceService.class);
    request.getSession().setAttribute("shopId", shopDTO.getId());
    UserDTO userDTO = createUser(shopDTO.getId());
    request.getSession().setAttribute("userName", userDTO.getUserName());
    request.getSession().setAttribute("shopVersion", shopVersionDTO);
    request.getSession().setAttribute("userId", userDTO.getId());
    return shopDTO.getId();
  }

  protected void initWeChatInfo() throws Exception {

    //wx services
    wxUserService = ServiceManager.getService(IWXUserService.class);
    wxService = ServiceManager.getService(IWXService.class);
    accountService = ServiceManager.getService(IWXAccountService.class);
    articleService = ServiceManager.getService(IWXArticleService.class);
    configService = ServiceManager.getService(ConfigService.class);

    weChatController.setWXUserService(wxUserService);
    weChatController.setWxService(wxService);
    weChatController.setAccountService(accountService);
    weChatController.setWxArticleService(articleService);
    weChatController.setConfigService(configService);


    String path =  System.getProperty("os.name").contains("Windows")?"C:\\tomcat\\key\\wx\\":"/usr/local/tomcat/key/wx/";
    configService.setConfig("wx_cfg_path", path, ShopConstant.BC_SHOP_ID);

    //save WXAccountDTO
    String publicNo = "gh_e1f30832359c";
    String name = "苏州统购";
    String encoding_key = "XoqBFc9Iz6adq5ItLpZOOq60SR6A0feBGxybCUVc7eX";
    String appId = "wxbb680e8d91db399e";
    String token = "1b51f05aac9a79170110df3f4b3510cc";
    String secret = "4f9b69507594e740d35f676c871acb67";
    WXAccountDTO accountDTO = new WXAccountDTO();
    accountDTO.setPublicNo(publicNo);
    accountDTO.setName(name);
    accountDTO.setAppId(appId);
    accountDTO.setSecret(secret);
    byte[] encryptData = EncryptionUtil.encrypt(accountDTO.getSecret().getBytes(), WXHelper.getSecretKey());
    accountDTO.setAppSecretBlob(Hibernate.createBlob(encryptData));
    accountDTO.setToken(token);
    accountDTO.setEncodingKey(encoding_key);
    accountDTO.setDeleted(DeletedType.FALSE);
    accountService.saveOrUpdateWXAccount(accountDTO);
    //save wxUser
    WXUserDTO userDTO = new WXUserDTO();
    userDTO.setOpenid("oCFjjt069Ms1D-vzGeyojcFcwQK8");
    userDTO.setPublicNo(publicNo);
    userDTO.setNickname("统购财神");
    userDTO.setSex("1");
    userDTO.setHeadimgurl("http://wx.qlogo.cn/mmopen/l1FBznHsrScIRhNBtaibuJd1lc6POVapQO1HYtb7r3wLnAZpVwPqlHT9hwahjOj2ickzOevVc9WViaeubnR2ZHTBQq9KEBMBMNR/0");
    userDTO.setDeleted(DeletedType.FALSE);
    wxUserService.saveOrUpdateWXUser(userDTO);

//    wxUserManager.saveOrUpdateWXUserVehicle()
  }

  public void openMessageSwitch() throws Exception {
    adminController.changeMessageSwitch(request, response, MessageScene.BOSS_DEBT_MSG, MessageSwitchStatus.ON);
    adminController.changeMessageSwitch(request, response, MessageScene.CUSTOMER_DEBT_MSG, MessageSwitchStatus.ON);
    adminController.changeMessageSwitch(request, response, MessageScene.FINISH_MSG, MessageSwitchStatus.ON);
    adminController.changeMessageSwitch(request, response, MessageScene.DISCOUNT_MSG, MessageSwitchStatus.ON);
  }

  public void closeMessageSwitch() throws Exception {
    adminController.changeMessageSwitch(request, response, MessageScene.BOSS_DEBT_MSG, MessageSwitchStatus.OFF);
    adminController.changeMessageSwitch(request, response, MessageScene.CUSTOMER_DEBT_MSG, MessageSwitchStatus.OFF);
    adminController.changeMessageSwitch(request, response, MessageScene.FINISH_MSG, MessageSwitchStatus.OFF);
    adminController.changeMessageSwitch(request, response, MessageScene.DISCOUNT_MSG, MessageSwitchStatus.OFF);
  }

  public void createShopRole(Long roleId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopRole shopRole = new ShopRole();
      shopRole.setRoleId(roleId);
      writer.save(shopRole);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

//  public ResourceDTO createResource(String name, String value, ResourceType type) {
//    ResourceDTO resourceDTO = resourceService.setResource(name, type, value, "");
//    return resourceDTO;
//  }

  public Module createModule(String name) {
    Module module = new Module();
    module.setName(name);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(module);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return module;
  }

  public RoleDTO createRole(String name) throws BcgogoException {
    RoleDTO roleDTO = roleService.setRole(name, "");
    return roleDTO;
  }

  public RoleResourceDTO createRoleResource(Long resourceId, Long roleId) {
    RoleResourceDTO roleResourceDTO = new RoleResourceDTO();
    roleResourceDTO = resourceService.setRoleResource(roleId, resourceId);
    return roleResourceDTO;
  }


  public UserGroupDTO createUserGroup() throws BcgogoException {
    Long temp = System.currentTimeMillis();
    UserGroupDTO userGroupDTO = new UserGroupDTO();
    userGroupDTO.setName("Group" + temp);
    userGroupDTO.setShopId(-1L);
    userGroupDTO = userGroupService.setUserGroup(userGroupDTO);
    return userGroupDTO;
  }

  public UserDTO initUser() throws Exception {
    Long shopId = createShop();
    UserGroupDTO userGroupDTO = createUserGroup();
    return createUser(shopId, userGroupDTO.getId());
  }

  public UserDTO createUser(Long shopId, Long userGroupId) throws BcgogoException {
    Long temp = System.currentTimeMillis();
    UserDTO userDTO = new UserDTO();
    userDTO.setPassword(EncryptionUtil.encryptPassword("123", shopId));
    userDTO.setUserNo(String.valueOf(temp));
    userDTO.setUserName("UName" + temp);
    userDTO.setName("Name" + temp);
    userDTO.setEmail(temp + "@qq.com");
    userDTO.setMobile("15851654173");
    userDTO.setShopId(shopId);
    userDTO.setUserGroupId(userGroupId);
    userDTO = userCacheService.setUser(userDTO);

//    UserGroupUserDTO userGroupUserDTO = new UserGroupUserDTO();
//    userGroupUserDTO.setUserGroupId(userGroupId);
//    userGroupUserDTO.setUserId(userDTO.getId());
//    userGroupService.setUserGroupUser(userGroupUserDTO);
    createUserGroupUser(userDTO.getId(), userGroupId);
    return userDTO;
  }

  public UserGroupUserDTO createUserGroupUser(Long userId, Long userGroupId) throws BcgogoException {
    UserGroupUserDTO userGroupUserDTO = new UserGroupUserDTO();
    userGroupUserDTO.setUserGroupId(userGroupId);
    userGroupUserDTO.setUserId(userId);
    userGroupUserDTO = userGroupService.setUserGroupUser(userGroupUserDTO);
    return userGroupUserDTO;
  }

  public UserGroupRoleDTO createUserGroupRole(Long roleId, Long userGroupId) throws BcgogoException {
    UserGroupRole userGroupRole = new UserGroupRole();
    userGroupRole.setUserGroupId(userGroupId);
    userGroupRole.setRoleId(roleId);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(userGroupRole);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return userGroupRole.toDTO();
  }

  public void addInventory(Long shopId, ProductDTO productDTO, double amount, double price) throws Exception {
    ModelMap model = new ModelMap();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("1301234393");
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setVestDateStr("2012-03-20 12:20");
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(amount);
    purchaseInventoryItemDTO.setPrice(price);
    purchaseInventoryItemDTO.setPurchasePrice(price);
    purchaseInventoryItemDTO.setProductName(productDTO.getName());
    purchaseInventoryItemDTO.setSpec(productDTO.getSpec());
    purchaseInventoryItemDTO.setModel(productDTO.getModel());
    purchaseInventoryItemDTO.setBrand(productDTO.getBrand());
    purchaseInventoryItemDTO.setTotal(amount * price);
    purchaseInventoryItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
    purchaseInventoryItemDTO.setVehicleEngine(productDTO.getProductVehicleEngine());
    purchaseInventoryItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
    purchaseInventoryItemDTO.setVehicleYear(productDTO.getProductVehicleYear());
    purchaseInventoryItemDTO.setProductId(productDTO.getId());
    purchaseInventoryItemDTO.setCommodityCode(productDTO.getCommodityCode());
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request, response);
    unitTestSleepSecond();
    goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    PurchaseInventoryDTO returnDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    productDTO.setProductLocalInfoId(returnDTO.getItemDTOs()[0].getProductId());
    unitTestSleepSecond();
  }

  public Long addInventory(Long shopId, ProductDTO productDTO, double amount, double price, String unit) throws Exception {
    ModelMap model = new ModelMap();
    request.getSession().setAttribute("shopId", shopId);
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setSupplier("pTest");
    purchaseInventoryDTO.setContact("HHH");
    purchaseInventoryDTO.setMobile("1301234393");
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setVestDateStr("2012-03-20 12:20");
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setAmount(amount);
    purchaseInventoryItemDTO.setPrice(price);
    purchaseInventoryItemDTO.setPurchasePrice(price);
    purchaseInventoryItemDTO.setProductName(productDTO.getName());
    purchaseInventoryItemDTO.setSpec(productDTO.getSpec());
    purchaseInventoryItemDTO.setModel(productDTO.getModel());
    purchaseInventoryItemDTO.setBrand(productDTO.getBrand());
    purchaseInventoryItemDTO.setTotal(amount * price);
    purchaseInventoryItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
    purchaseInventoryItemDTO.setVehicleEngine(productDTO.getProductVehicleEngine());
    purchaseInventoryItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
    purchaseInventoryItemDTO.setVehicleYear(productDTO.getProductVehicleYear());
    purchaseInventoryItemDTO.setProductId(productDTO.getId());
    purchaseInventoryItemDTO.setCommodityCode(productDTO.getCommodityCode());
    purchaseInventoryItemDTO.setUnit(unit);
    PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[1];
    itemDTOs[0] = purchaseInventoryItemDTO;
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    goodsStorageController.savePurchaseInventory(model, purchaseInventoryDTO, request, response);
    goodsStorageController.getPurchaseInventory(model, request, purchaseInventoryDTO.getId().toString());
    PurchaseInventoryDTO returnDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    productDTO.setProductLocalInfoId(returnDTO.getItemDTOs()[0].getProductId());
    unitTestSleepSecond();
    return returnDTO.getId();
  }

  public ProductDTO createProductDTO(String name, String brand, String model, String spec,
                                     String pvBrand, String pvModel, String pvYear, String pvEngine) {
    ProductDTO productDTO = new ProductDTO();
    productDTO.setName(name);
    productDTO.setBrand(brand);
    productDTO.setModel(model);
    productDTO.setSpec(spec);

//    if (pvBrand == "全部") {
//      productDTO.setProductVehicleBrand(pvBrand);
//      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
//    } else if (pvBrand == "多款") {
//      productDTO.setProductVehicleBrand(pvBrand);
//      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE);
//    } else {
//      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
//      productDTO.setProductVehicleBrand(pvBrand);
//      productDTO.setProductVehicleModel(pvModel);
//      productDTO.setProductVehicleYear(pvYear);
//      productDTO.setProductVehicleEngine(pvEngine);
//    }

    if (StringUtils.isBlank(pvBrand) || "全部".equals(pvBrand)) {
      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
      if ("全部".equals(pvBrand)) {
        productDTO.setProductVehicleBrand(pvBrand);
      }
    } else if ("多款".equals(pvBrand)) {
      productDTO.setProductVehicleBrand(pvBrand);
      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE);
    } else {
      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
      productDTO.setProductVehicleBrand(pvBrand);
      productDTO.setProductVehicleModel(pvModel);
      productDTO.setProductVehicleYear(pvYear);
      productDTO.setProductVehicleEngine(pvEngine);
    }
    return productDTO;
  }

  public ProductDTO createProductNoVehicle(String str) {
    ProductDTO productDTO = new ProductDTO();
    productDTO.setName("测试商品品名" + str);
    productDTO.setBrand("测试商品品牌" + str);
    productDTO.setModel("测试商品型号" + str);
    productDTO.setSpec("测试商品规格" + str);
    productDTO.setProductVehicleModel("");
    productDTO.setProductVehicleBrand("");
    productDTO.setProductVehicleYear("");
    productDTO.setProductVehicleEngine("");
    return productDTO;
  }

  public ProductDTO createProductHaveVehicle(String str) {
    ProductDTO productDTO = new ProductDTO();
    productDTO.setName("测试商品品名" + str);
    productDTO.setBrand("测试商品品牌" + str);
    productDTO.setModel("测试商品型号" + str);
    productDTO.setSpec("测试商品规格" + str);
    productDTO.setProductVehicleModel("Model" + str);
    productDTO.setProductVehicleBrand("Brand" + str);
    productDTO.setProductVehicleYear("2012");
    productDTO.setProductVehicleEngine("3.0L");
    return productDTO;
  }

  public VehicleDTO generateVehicleDTO(String vehicleNo, String brand, String model, String year, String engine) {
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setLicenceNo(vehicleNo);
    vehicleDTO.setBrand(brand);
    vehicleDTO.setModel(model);
    vehicleDTO.setYear(year);
    vehicleDTO.setEngine(engine);
    return vehicleDTO;
  }

  public CustomerDTO generateCustomerDTO(String name, String mobile) {
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setName(name);
    customerDTO.setMobile(mobile);
    return customerDTO;
  }


  public RepairOrderDTO generateRepairOrderDTO(ProductDTO productDTO, VehicleDTO vehicleDTO, CustomerDTO customerDTO,
                                               MockHttpServletRequest request) {
    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    request.setParameter("vehicleNumber", vehicleDTO.getLicenceNo());
    repairOrderDTO.setBrand(vehicleDTO.getBrand());
    repairOrderDTO.setModel(vehicleDTO.getModel());
    repairOrderDTO.setYear(vehicleDTO.getYear());
    repairOrderDTO.setEngine(vehicleDTO.getEngine());
    repairOrderDTO.setVechicleId(vehicleDTO.getId());

    repairOrderDTO.setCustomerId(customerDTO.getId());
    repairOrderDTO.setCustomerName(customerDTO.getName());
    repairOrderDTO.setMobile(customerDTO.getMobile());
    repairOrderDTO.setLicenceNo(vehicleDTO.getLicenceNo());
    repairOrderDTO.setTotal(240D);
    repairOrderDTO.setSettledAmount(240D);

    RepairOrderServiceDTO serviceDTO = new RepairOrderServiceDTO();
    serviceDTO.setService("work");
    serviceDTO.setTotal(100D);
    serviceDTO.setConsumeType(ConsumeType.MONEY);
    RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[1];
    repairOrderServiceDTOs[0] = serviceDTO;
    repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);

    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
    repairOrderItemDTO.setBrand(productDTO.getBrand());
    repairOrderItemDTO.setProductName(productDTO.getName());
    repairOrderItemDTO.setSpec(productDTO.getSpec());
    repairOrderItemDTO.setModel(productDTO.getModel());
    repairOrderItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
    repairOrderItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
    repairOrderItemDTO.setVehicleEngine(productDTO.getProductVehicleEngine());
    repairOrderItemDTO.setVehicleYear(productDTO.getProductVehicleYear());
    repairOrderItemDTO.setProductId(productDTO.getProductLocalInfoId());
    repairOrderItemDTO.setProductType(productDTO.getProductVehicleStatus());
    repairOrderItemDTO.setPurchasePrice(100D);
    repairOrderItemDTO.setPrice((120D));
    repairOrderItemDTO.setInventoryAmount(0d);
    repairOrderItemDTO.setAmount(2D);
    RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[1];
    repairOrderItemDTOs[0] = repairOrderItemDTO;
    repairOrderDTO.setItemDTOs(repairOrderItemDTOs);

    repairOrderDTO.setServiceType(OrderTypes.REPAIR);
    repairOrderDTO.setStartDate(System.currentTimeMillis());
    repairOrderDTO.setStartDateStr(DateUtil.convertDateLongToDateString(
      DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, System.currentTimeMillis()));

    repairOrderDTO.setEndDate(System.currentTimeMillis());
    repairOrderDTO.setEndDateStr(DateUtil.convertDateLongToDateString(
      DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, System.currentTimeMillis()));
    return repairOrderDTO;
  }


  public SupplierDTO addSupplier() throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.setShopId(77876L);
    supplierDTO.setLastOrderTime(10000010001160016L);
    supplierDTO.setLastOrderType(OrderTypes.PURCHASE);
    supplierDTO.setLastOrderProducts("轮胎，机油等");
    supplierDTO.setTotalInventoryAmount(20000.00);
    ContactDTO contactDTO = new ContactDTO();
    ContactDTO[] contactDTOs = new ContactDTO[3];
    contactDTO.setShopId(77876L);
    contactDTO.setIsMainContact(1);
    contactDTO.setName("abc");
    contactDTOs[0] = contactDTO;
    supplierDTO.setContacts(contactDTOs);
    userService.createSupplier(supplierDTO);

    SupplierRecordDTO supplierRecordDTO = new SupplierRecordDTO();
    supplierRecordDTO.setShopId(supplierDTO.getShopId());
    supplierRecordDTO.setSupplierId(supplierDTO.getId());
    supplierRecordDTO.setCreditAmount(0d);
    return supplierDTO;
  }

  public List<SupplierDTO> addSuppliers() throws BcgogoException {
    List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
    for (int i = 0; i < 6; i++) {
      supplierDTOList.add(addSupplier());
    }
    return supplierDTOList;
  }

  public CustomerDTO addCustomer() throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setShopId(5434L);
    customerDTO.setName("邹建宏11111111");
    customerDTO.setMobile("87657678876");
    ContactDTO[] contactDTOs = new ContactDTO[3];
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setName("邹建宏11111111");
    contactDTO.setMobile("87657678876");
    contactDTO.setEmail("2929@bcgogo.com");
    contactDTO.setIsMainContact(1);
    contactDTOs[0] = contactDTO;
    customerDTO.setContacts(contactDTOs);
    customerDTO.setEmail("2929@bcgogo.com");
    return userService.createCustomer(customerDTO);
  }

  public CustomerRecordDTO addCustomerRecord() throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO.setShopId(5434L);
    customerRecordDTO.setName("邹建宏11111111");
    customerRecordDTO.setMobile("87657678876");
    customerRecordDTO.setCustomerId(12345678L);
    customerRecordDTO.setEmail("2929@bcgogo.com");
    customerRecordDTO.setPhone("0512-87654345");
    customerRecordDTO.setTotalAmount(234454.00);
    customerRecordDTO.setLastAmount(23434.00);
    customerRecordDTO.setLastDate(10000010001167016L);
    return userService.createCustomerRecord(customerRecordDTO);
  }

  public void deleteJobs() {
    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
    NotificationService notificationService = ServiceManager.getService(NotificationService.class);
    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY);
    if (CollectionUtils.isNotEmpty(jobs)) {
      NotificationWriter notificationWriter = notificationDaoManager.getWriter();
      Object notificationStatus = notificationWriter.begin();
      try {
        for (SmsJob smsJob : jobs)
          notificationWriter.delete(SmsJob.class, smsJob.getId());
        notificationWriter.commit(notificationStatus);
      } finally {
        notificationWriter.rollback(notificationStatus);
      }
    }
  }

  public CustomerRecordDTO addCustomerRecord(Long customerId) throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO.setShopId(5434L);
    customerRecordDTO.setName("邹建宏11111111");
    customerRecordDTO.setMobile("87657678876");
    customerRecordDTO.setCustomerId(customerId);
    customerRecordDTO.setEmail("2929@bcgogo.com");
    customerRecordDTO.setPhone("0512-87654345");
    customerRecordDTO.setTotalAmount(234454.00);
    customerRecordDTO.setLastAmount(23434.00);
    customerRecordDTO.setLastDate(10000010001167016L);
    return userService.createCustomerRecord(customerRecordDTO);
  }

  public List<CustomerRecordDTO> addCustomerRecordList() throws BcgogoException {
    List<CustomerRecordDTO> customerRecordDTOList = new ArrayList<CustomerRecordDTO>();
    for (int i = 0; i < 6; i++) {
      customerRecordDTOList.add(addCustomerRecord());
    }
    return customerRecordDTOList;
  }

  public VehicleDTO addVehicle() throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setShopId(5434L);
    vehicleDTO.setLicenceNo("苏A00098");
    vehicleDTO.setBrand("奥迪");
    vehicleDTO.setModel("A6");
    vehicleDTO.setYear("2012");
    return userService.createVehicle(vehicleDTO);
  }

  public List<VehicleDTO> addVehicleList() throws BcgogoException {
    List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();
    for (int i = 0; i < 6; i++) {
      vehicleDTOList.add(addVehicle());
    }
    return vehicleDTOList;
  }


  public void addCustomerVehicleInfo() throws BcgogoException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = null;
    VehicleDTO vehicleDTO = null;
    for (int i = 0; i < 6; i++) {
      customerDTO = addCustomer();
      vehicleDTO = addVehicle();
      addCustomerRecord(customerDTO.getId().longValue());
      userService.addVehicleToCustomer(vehicleDTO.getId().longValue(), customerDTO.getId().longValue());
    }
  }

  public void flushAllMemCache() throws Exception {
    MemCacheAdapter.flushAll();
  }

  public void orderDropDownListTest(String expectProductBrand, String expectProductName, String expectVehicleBrand, Long shopId) throws Exception {
    OrderSavedEvent orderSavedEvent = (OrderSavedEvent) request.getAttribute("UNIT_TEST");
    while (orderSavedEvent.mockFlag()) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setShopId(shopId);
    searchConditionDTO.setSearchField(SearchConstant.PRODUCT_BRAND);
    //memcache中没有值   产品品牌
    ProductCurrentUsedService pcus = new ProductCurrentUsedService();
    List<CurrentUsedProduct> currentUsedProductList = (List<CurrentUsedProduct>) MemCacheAdapter.get(pcus.getMemCacheKey(new SearchMemoryConditionDTO(searchConditionDTO)));
    Assert.assertEquals(expectProductBrand, currentUsedProductList.get(0).getBrand());
    SearchWriter writer = searchDaoManager.getWriter();
    //memcache中没有值   产品品牌
    currentUsedProductList = writer.getCurrentUsedProduct(new SearchMemoryConditionDTO(searchConditionDTO));
    Assert.assertEquals(expectProductBrand, currentUsedProductList.get(0).getBrand());

    searchConditionDTO.setSearchField(SearchConstant.PRODUCT_NAME);
    //memcache中没有值   品名
    currentUsedProductList = (List<CurrentUsedProduct>) MemCacheAdapter.get(pcus.getMemCacheKey(new SearchMemoryConditionDTO(searchConditionDTO)));
    Assert.assertEquals(expectProductName, currentUsedProductList.get(0).getProductName());
    //数据库中有值
    currentUsedProductList = writer.getCurrentUsedProduct(new SearchMemoryConditionDTO(searchConditionDTO));
    Assert.assertEquals(expectProductName, currentUsedProductList.get(0).getProductName());
    if (expectVehicleBrand != null) {
      searchConditionDTO.setSearchField("vehicle_" + SearchConstant.VEHICLE_BRAND);   //todo 临时
      //memcache中没有值   车辆品牌
      List<CurrentUsedVehicle> currentUsedVehicleList = (List<CurrentUsedVehicle>) MemCacheAdapter.get(pcus.getMemCacheKey(new SearchMemoryConditionDTO(searchConditionDTO)));
      Assert.assertEquals(expectVehicleBrand, currentUsedVehicleList.get(0).getBrand());
      //数据库中有值
      currentUsedVehicleList = writer.getCurrentUsedVehicle(searchConditionDTO);
      Assert.assertEquals(expectVehicleBrand, currentUsedVehicleList.get(0).getBrand());
    }
  }

  public CustomerDTO createCustomer() throws Exception {
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setName("miss");
    customerDTO.setMobile("15151771582");
    customerDTO.setContact("tang");
    customerDTO.setBirthday(DateUtil.convertDateStringToDateLong("MM-dd", new SimpleDateFormat("MM-dd").format(new Date())));
    return customerDTO;
  }

  public VehicleDTO createVehicle() throws Exception {
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setLicenceNo("苏A12345");
    return vehicleDTO;
  }

  // 创建一条smsRecharge数据
  public SmsRechargeDTO createRecharge(Long shopId) {
    SmsRechargeDTO smsRechargeDTO = new SmsRechargeDTO();
    smsRechargeDTO.setSmsBalance(0d);
    smsRechargeDTO.setRechargeAmount(100d);
    smsRechargeDTO.setShopId(shopId);
    smsRechargeDTO.setRechargeNumber("0000010001240029");
    smsRechargeDTO.setRechargeTime(System.currentTimeMillis());
    smsRechargeDTO.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_INIT);
    smsRechargeDTO = smsRechargeService.createSmsRecharge(smsRechargeDTO);
    return smsRechargeDTO;
  }

  public void unitTestSleepSecond() throws Exception {
    OrderSavedEvent orderSavedEvent = (OrderSavedEvent) request.getAttribute("UNIT_TEST");
    long begin = System.currentTimeMillis();
    while (orderSavedEvent.mockFlag()) {
      try {
        Thread.sleep(1000);
        LOG.warn("sleep 1000ms");
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      if (System.currentTimeMillis() - begin > 60 * 1000) {
        throw new Exception("unitTestSleepSecond 超时");
      }
    }
  }

  public void createServices() {
    txnWriter = ServiceManager.getService(TxnDaoManager.class).getWriter();
    Object status = txnWriter.begin();
    Service service = new Service();
    service.setName("换机油");
    service.setPrice(50d);
    txnWriter.saveAndFlush(service);
    service = new Service();
    service.setName("换机滤");
    service.setPrice(60d);
    txnWriter.saveAndFlush(service);
    txnWriter.commit(status);
  }

  public List<Service> createServicesForShop(Long shopId) {
    txnWriter = ServiceManager.getService(TxnDaoManager.class).getWriter();
    List<Service> services = new ArrayList<Service>();
    Object status = txnWriter.begin();
    Service service = new Service();
    service.setName("换机油");
    service.setShopId(shopId);
    service.setPrice(50d);
    txnWriter.saveAndFlush(service);
    services.add(service);
    service = new Service();
    service.setName("换机滤");
    service.setShopId(shopId);
    service.setPrice(60d);
    txnWriter.saveAndFlush(service);
    txnWriter.commit(status);
    services.add(service);
    return services;
  }

  public MessageTemplateDTO createMsgTemplate(MessageTemplateDTO messageTemplateDTO) {
    NotificationDaoManager notificationDaoManager = new NotificationDaoManager();
    NotificationWriter writer = notificationDaoManager.getWriter();

    if (messageTemplateDTO == null) {
      return null;
    }
    Object status = writer.begin();
    try {
      MessageTemplate msgTemplate = new MessageTemplate();
      msgTemplate.setType(messageTemplateDTO.getType());
      //shopId暂时不处理，现在不做，默认为-1
      msgTemplate.setShopId(-1l);
      msgTemplate.setContent(messageTemplateDTO.getContent());
      msgTemplate.setName(messageTemplateDTO.getName());
      msgTemplate.setScene(messageTemplateDTO.getScene());
      msgTemplate.setNecessary(messageTemplateDTO.getNecessary());
      writer.save(msgTemplate);
      messageTemplateDTO.setId(msgTemplate.getId());
      writer.commit(status);
      return messageTemplateDTO;
    } finally {
      writer.rollback(status);
    }
  }

  protected String getRepairOrderRedirectUrl(RepairOrderDTO repairOrderDTO) {
    return "redirect:/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + String.valueOf(repairOrderDTO.getId()) +
      "&print=" + repairOrderDTO.getPrint() + "&resultMsg=success";
  }

  public MemberCardDTO createMemberCard(Long shopId, String name) {
    List<Service> services = createServicesForShop(shopId);
    membersService = ServiceManager.getService(IMembersService.class);
    MemberCardDTO memberCardDTO = new MemberCardDTO();
    memberCardDTO.setPrice(2000.0);
    memberCardDTO.setWorth(2500.0);
    memberCardDTO.setPercentageAmount(10.0);
    memberCardDTO.setName(name);
    memberCardDTO.setShopId(shopId);
    List<MemberCardServiceDTO> memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();

    MemberCardServiceDTO memberCardServiceDTO = new MemberCardServiceDTO();
    memberCardServiceDTO.setServiceId(services.get(0).getId());
    memberCardServiceDTO.setServiceName(services.get(0).getName());
    memberCardServiceDTO.setTerm(5);
    memberCardServiceDTO.setTimes(8);
    memberCardServiceDTOs.add(memberCardServiceDTO);
    memberCardServiceDTO = new MemberCardServiceDTO();
    memberCardServiceDTO.setServiceId(services.get(1).getId());
    memberCardServiceDTO.setServiceName(services.get(1).getName());
    memberCardServiceDTO.setTerm(2);
    memberCardServiceDTO.setTimes(-1);
    memberCardServiceDTOs.add(memberCardServiceDTO);

    memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);

    return membersService.saveOrUpdateMemberCard(memberCardDTO);
  }

  public MemberDTO createMember(Long shopId, Long customerId, String memberNo, String password) throws Exception {
    membersService = ServiceManager.getService(IMembersService.class);
    txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    txnWriter = txnDaoManager.getWriter();

    MemberDTO memberDTO = new MemberDTO();
    memberDTO.setShopId(shopId);
    memberDTO.setCustomerId(customerId);
    memberDTO.setMemberNo(memberNo);
    memberDTO.setBalance(2000.0);
    memberDTO.setJoinDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, "2012-07-08"));
    memberDTO.setPassword(EncryptionUtil.encryptPassword(password, shopId));
    memberDTO.setPasswordStatus(PasswordValidateStatus.VALIDATE);

    List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();

    //创建一个施工项目 保存到该店铺下
    String serviceName = "施工项目1";
    Service service = new Service();
    service.setShopId(shopId);
    service.setName(serviceName);
    service.setPrice(0d);
    service.setMemo("");

    Object status = txnWriter.begin();
    try {
      txnWriter.save(service);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }

    MemberServiceDTO memberServiceDTO = new MemberServiceDTO();
    memberServiceDTO.setTimes(20);
    memberServiceDTO.setServiceName(serviceName);
    memberServiceDTO.setServiceId(service.getId());
    memberServiceDTO.setDeadline(DateUtil.getDeadline(System.currentTimeMillis(), 1));
    memberServiceDTO.setStatus(MemberStatus.ENABLED);
    memberServiceDTOs.add(memberServiceDTO);


    serviceName = "施工项目2";
    service = new Service();
    service.setShopId(shopId);
    service.setName(serviceName);
    service.setPrice(0d);
    service.setMemo("");

    status = txnWriter.begin();
    try {
      txnWriter.save(service);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    memberServiceDTO = new MemberServiceDTO();
    memberServiceDTO.setTimes(-1);
    memberServiceDTO.setServiceName(serviceName);
    memberServiceDTO.setServiceId(service.getId());
    memberServiceDTO.setDeadline(DateUtil.getDeadline(System.currentTimeMillis(), 1));
    memberServiceDTO.setStatus(MemberStatus.ENABLED);
    memberServiceDTOs.add(memberServiceDTO);
    memberDTO.setMemberServiceDTOs(memberServiceDTOs);

    return membersService.createMember(memberDTO);
  }

  public void saveServiceAndCategory(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Service service = new Service();
      service.setName("洗车");
      service.setShopId(shopId);
      service.setPercentageAmount(5d);
      service.setPrice(20d);
      writer.save(service);
      Category category = new Category();
      category.setCategoryName("洗车");
      category.setShopId(shopId);
      category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
      writer.save(category);
      CategoryItemRelation categoryItemRelation = new CategoryItemRelation();
      categoryItemRelation.setCategoryId(category.getId());
      categoryItemRelation.setServiceId(service.getId());
      writer.save(categoryItemRelation);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public WashBeautyOrderDTO saveMemberInfoDetail(Long shopId, Long serviceId) throws Exception {
    WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
    IUserService userService = ServiceManager.getService(IUserService.class);
    membersService = ServiceManager.getService(IMembersService.class);
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setShopId(shopId);
    customerDTO.setName("周东明");
    customerDTO.setMobile("15995496255");
    customerDTO.setEmail("2032@bcgogo.com");
    customerDTO = userService.createCustomer(customerDTO);
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setShopId(shopId);
    vehicleDTO.setLicenceNo("苏E00000");
    vehicleDTO.setBrand("奥迪");
    vehicleDTO.setModel("TT");
    vehicleDTO.setYear("2012");
    vehicleDTO = userService.createVehicle(vehicleDTO);
    userService.addVehicleToCustomer(vehicleDTO.getId(), customerDTO.getId());
    MemberDTO memberDTO = new MemberDTO();
    memberDTO.setShopId(shopId);
    memberDTO.setCustomerId(customerDTO.getId());
    memberDTO.setMemberNo("苏E00000");
    memberDTO.setBalance(2000.0);
    memberDTO.setJoinDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, "2012-07-08"));
    memberDTO.setPasswordStatus(PasswordValidateStatus.VALIDATE);
    List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
    MemberServiceDTO memberServiceDTO = new MemberServiceDTO();
    memberServiceDTO.setTimes(20);
    memberServiceDTO.setServiceName("洗车");
    memberServiceDTO.setServiceId(serviceId);
    memberServiceDTO.setDeadline(DateUtil.getDeadline(System.currentTimeMillis(), 1));
    memberServiceDTO.setStatus(MemberStatus.ENABLED);
    memberServiceDTOs.add(memberServiceDTO);
    memberDTO.setMemberServiceDTOs(memberServiceDTOs);
    membersService.createMember(memberDTO);
    washBeautyOrderDTO.setCustomerId(customerDTO.getId());
    washBeautyOrderDTO.setLicenceNo(vehicleDTO.getLicenceNo());
    return washBeautyOrderDTO;
  }

  public Long createSalesMan(Long shopId, String name) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    SalesManDTO dto = new SalesManDTO();
    dto.setName(name);
    dto.setShopId(shopId);
    dto.setStatus(SalesManStatus.INSERVICE);
    dto.setDepartmentName("test");
    userService.saveOrUpdateSalesMan(dto);
    return dto.getId();
  }

  public void initRepairShopRoleResource(Long shopId) {
    ShopVersionDTO shopVersionDTO = new ShopVersionDTO();
    shopVersionDTO.setName("REPAIR_SHOP");
    shopVersionDTO.setValue("初级版");
    ServiceManager.getService(IShopVersionService.class).saveOrUpdateShopVersion(shopVersionDTO);
    request.getSession().setAttribute("shopVersion", shopVersionDTO);

    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setMemo("忽略校验库存");
    resourceDTO.setName("WEB_VERSION_IGNORE_VERIFIER_INVENTORY");
    resourceDTO.setStatus("active");
    resourceDTO.setType(ResourceType.logic);
    resourceDTO.setValue(LogicResource.WEB_VERSION_IGNORE_VERIFIER_INVENTORY);
    resourceDTO.setSystemType(SystemType.SHOP);
    Result result = resourceService.setResource(resourceDTO);
    if (!result.isSuccess()) {
      resourceDTO = resourceService.getResource(resourceDTO.getName(), resourceDTO.getType().toString());
    }

    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setValue("初级版基本权限");
    roleDTO.setName("REPAIR_SHOP_BASE");
    roleDTO.setType(SystemType.SHOP);
    roleDTO.setModuleId(-1l);
    roleDTO.setStatus("active");
    roleService.saveOrUpdateRole(roleDTO);

    Assert.assertNotNull(roleDTO.getId());
    Assert.assertNotNull(resourceDTO.getResourceId());
    resourceService.setRoleResource(roleDTO.getId(), resourceDTO.getResourceId());
    List<ShopRoleDTO> shopRoleDTOList = new ArrayList<ShopRoleDTO>();
    ShopRoleDTO shopRoleDTO = new ShopRoleDTO();
    shopRoleDTO.setRoleId(roleDTO.getId());
    shopRoleDTO.setShopId(shopId);
    shopRoleDTO.setShopVersionId(shopVersionDTO.getId());
    shopRoleDTO.setStatus(Status.active);
    shopRoleDTOList.add(shopRoleDTO);
    ServiceManager.getService(IShopVersionService.class).saveRolesConfigForShopVersion(shopRoleDTOList, shopVersionDTO.getId());
  }

  public ShopDTO createRandomShop() throws Exception {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setAccount("test");
    shopDTO.setName("test" + StringUtil.getFixedLengthChinese(10));
    shopDTO.setMobile("130" + StringUtil.getRandomNumberStr(8));
    shopDTO.setMobile("130" + StringUtil.getRandomNumberStr(8));
    shopDTO.setStoreManagerMobile("130" + StringUtil.getRandomNumberStr(8));
    shopDTO.setAccount("6224021");
    shopDTO.setAddress("江苏省苏州市工业园区" + StringUtil.getRandomLengthChiness(5) + StringUtil.getRandomNumberStr(3));
    shopDTO.setAreaId(1010013012L);
    shopDTO.setProvince(1010L);
    shopDTO.setCity(1010013L);
    shopDTO.setRegion(1010013012L);
    shopDTO.setAgent("张三");
    shopDTO.setAgentId("120202020");
    shopDTO.setBank("建设银行");
    shopDTO.setEmail("www.224422@qq.com");
    shopDTO.setBusinessScope("个体经营");
    shopDTO.setContact("张三");
    shopDTO.setFax("0512-125565656");
    shopDTO.setStoreManager("管" + StringUtil.getFixedLengthChinese(3));
    return shopDTO;
  }
}


