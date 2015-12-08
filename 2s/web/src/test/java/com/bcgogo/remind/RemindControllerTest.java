//package com.bcgogo.remind;
//
//import com.bcgogo.AbstractTest;
//import com.bcgogo.common.PagingListResult;
//import com.bcgogo.config.service.IConfigService;
//import com.bcgogo.customer.SmsController;
//import com.bcgogo.enums.PlansRemindStatus;
//import com.bcgogo.enums.RemindEventType;
//import com.bcgogo.notification.model.NotificationDaoManager;
//import com.bcgogo.notification.model.NotificationWriter;
//import com.bcgogo.notification.model.SmsJob;
//import com.bcgogo.notification.smsSend.SmsYiMeiSenderMock;
//import com.bcgogo.remind.dto.ShopPlanDTO;
//import com.bcgogo.service.ServiceManager;
//import com.bcgogo.txn.TxnController;
//import com.bcgogo.txn.service.ITxnService;
//import com.bcgogo.txn.service.RemindEventStrategySelector;
//import com.bcgogo.user.dto.AppointServiceDTO;
//import com.bcgogo.user.dto.CustomerDTO;
//import com.bcgogo.user.dto.CustomerServiceJobDTO;
//import com.bcgogo.user.dto.VehicleDTO;
//import com.bcgogo.user.model.*;
//import com.bcgogo.user.service.ICustomerService;
//import com.bcgogo.user.service.IShopPlanService;
//import com.bcgogo.user.service.IUserService;
//import com.bcgogo.utils.*;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.ui.ModelMap;
//
//import java.lang.reflect.Type;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
//* Created by IntelliJ IDEA.
//* User: Lucien
//* Date: 12-4-7
//* Time: 下午12:48
//* To change this template use File | Settings | File Templates.
//*/
//public class RemindControllerTest extends AbstractTest {
//  @Before
//  public void setUp() throws Exception {
//    this.response = new MockHttpServletResponse();
//    this.request = new MockHttpServletRequest();
//    this.remindController = new RemindController();
//    remindEventStrategySelector = ServiceManager.getService(RemindEventStrategySelector.class);
//    remindController.setRemindEventStrategySelector(remindEventStrategySelector);
//
//    this.smsController = new SmsController();
//  }
//
//  @Test
//  public void testCustomerRemind() throws Exception {
//    IUserService userService = ServiceManager.getService(IUserService.class);
//    Long shopId = createShop();
//    CustomerDTO customerDTO = createCustomer();
//    customerDTO.setShopId(shopId);
//    customerDTO = userService.createCustomer(customerDTO);
//    VehicleDTO vehicleDTO = createVehicle();
//    vehicleDTO.setShopId(shopId);
//    vehicleDTO = userService.createVehicle(vehicleDTO);
//    userService.addVehicleToCustomer(vehicleDTO.getId(), customerDTO.getId());
//    AppointServiceDTO appointServiceDTO=new AppointServiceDTO();
//    appointServiceDTO.setShopId(shopId);
//    appointServiceDTO.setCustomerId(String.valueOf(customerDTO.getId()));
//    appointServiceDTO.setVehicleId(String.valueOf(vehicleDTO.getId()));
//    appointServiceDTO.setMaintainTimeStr(new SimpleDateFormat(DateUtil.YEAR_MONTH_DATE).format(new Date()));
//    appointServiceDTO.setInsureTimeStr("");
//    appointServiceDTO.setExamineTimeStr("");
//    //保险，保养，验车
//    userService.addYuyueToCustomerVehicle(appointServiceDTO);
//    //生日
//    CustomerServiceJobDTO customerServiceJobDTO = new CustomerServiceJobDTO();
//    customerServiceJobDTO.setShopId(shopId);
//    customerServiceJobDTO.setCustomerId(customerDTO.getId());
//    Calendar cal = Calendar.getInstance();
//
//    customerServiceJobDTO.setRemindTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,cal.get(Calendar.YEAR)+"-" + DateUtil.convertDateLongToDateString(DateUtil.MONTH_DATE, customerDTO.getBirthday())));
//    customerServiceJobDTO.setRemindType(UserConstant.BIRTH_TIME);
//    customerServiceJobDTO.setStatus(UserConstant.Status.ACTIVITY);
//    List<CustomerServiceJobDTO> customerServiceJobDTOs = new ArrayList<CustomerServiceJobDTO>();
//    customerServiceJobDTOs.add(customerServiceJobDTO);
//    userService.saveCustomerBirthdayRemind(customerServiceJobDTOs);
//    List<CustomerServiceJobDTO> customerServiceJobDTOList = userService.getCustomerServiceJobByCustomerIdAndRemindType(customerDTO.getId(),UserConstant.BIRTH_TIME);
//    ServiceManager.getService(ITxnService.class).saveRemindEvent(new CustomerServiceJob().fromDTO(customerServiceJobDTOList.get(0)),"XXX","13626199000","苏E88552");
//    request.getSession().setAttribute("shopId", shopId);
//    PagingListResult pagingListResult = remindController.customerRemind(request, 1);
//    customerServiceJobDTOList = pagingListResult.getResults();
//    Assert.assertEquals(new SimpleDateFormat(DateUtil.YEAR_MONTH_DATE).format(new Date()), customerServiceJobDTOList.get(0).getRemindTimeStr());
//  }
//
//  @Test
//  public void testCreatePlanAndGetPlans() throws Exception {
//    ModelMap model = new ModelMap();
//    Long shopId = createShop();
//    String remindTime = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()));
//
////    String plans = "tt<tt<邵磊<" + remindTime + "<<all;";
//    String plans = "{\"remindType\":\"tt\",\"content\":\"tt\",\"customerNames\":\"邵磊\",\"customerIds\":\"\",\"remindTimeStr\":\""+remindTime+"\",\"customerType\":\"all\"}";
//    request.getSession().setAttribute("shopId", shopId);
//    remindController.createPlan(request, plans, response);
////    remindController.getPlans(model, request, response, 1,null);
////    String jsonStr = (String) model.get("jsonStr");
////    Type type = new TypeToken<List<ShopPlanDTO>>() {}.getType();
////    List<ShopPlanDTO> shopPlanDTOList = new Gson().fromJson(jsonStr, type);
////    Assert.assertEquals("tt", shopPlanDTOList.get(0).getRemindType());
////    Assert.assertEquals("tt", shopPlanDTOList.get(0).getContent());
////    Assert.assertEquals("邵磊", shopPlanDTOList.get(0).getCustomerNames());
////    Assert.assertEquals(remindTime, shopPlanDTOList.get(0).getRemindTimeStr());
////
////    Assert.assertEquals(UserConstant.CustomerType.ALL_CUSTOMER, shopPlanDTOList.get(0).getCustomerType());
//  }
//
//  private String smsContent = "张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试" +      //70
//      "张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试张峻滔测试"+      //70
//      "该测试是模拟发送到"; //40
//
//  private int size = smsContent.length();
//
//  @Test
//  public void testSmsSendAllShopCustomersSuccess() throws Exception {
//    ModelMap model = new ModelMap();
//    Long shopId = createShop();
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
//    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    //使用 swe
//    SmsYiMeiSenderMock.setCode(0);
//    //保存 客户
//    UserWriter userWriter = userDaoManager.getWriter();
//    for (int i = 0; i < 199; i++) {
//      Object userStatus = userWriter.begin();
//      try {
//        Customer customer = new Customer();
//        customer.setShopId(shopId);
//        if (i != 190) {
//        customer.setMobile("15851654173");
//        }
//        userWriter.save(customer);
//        userWriter.commit(userStatus);
//      } finally {
//        userWriter.rollback(userStatus);
//      }
//    }
//    //判断customer是否存储正确
//    List<String> customerPhones = customerService.getCustomersPhonesByShopId(shopId);
//    Assert.assertEquals(198, customerPhones.size());
//
//    //保存一条计划 顾客为all
//    ShopPlan shopPlan = new ShopPlan();
//    shopPlan.setShopId(shopId);
//    shopPlan.setContact("18915410141");
//    shopPlan.setContent(smsContent);
//    shopPlan.setStatus(PlansRemindStatus.activity);
//    shopPlan.setCustomerNames("abab");
//    shopPlan.setUserInfo("[\"userId\":\"1\",\"name\":\"abab\",\"mobile\":\"18915410141\"]");
//    shopPlan.setRemindTime(System.currentTimeMillis());
//
//    Object statusSP = userWriter.begin();
//    try {
//      userWriter.save(shopPlan);
//      userWriter.commit(statusSP);
//    } finally {
//      userWriter.rollback(statusSP);
//    }// 判断计划是否 保存正确
//    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
//    List<ShopPlanDTO> shopPlanDTOList = shopPlanService.getPlans(shopId, 0,100,DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()),null);
//    Assert.assertEquals(1, shopPlanDTOList.size());
//
//    request.getSession().setAttribute("shopId", shopId);
//    request.setParameter("content", smsContent);
//
//
//    request.setParameter("phoneNumbers", shopPlan.getContact());
//    request.setParameter("smsContent", shopPlan.getContent());
//    request.setParameter("shopPlanId", shopPlanDTOList.get(0).getId().toString());
//
//    smsController.sendSms(request,shopPlan.getContact(),shopPlan.getContent());
//
//    shopPlan = shopPlanService.getPlan(shopId,shopPlanDTOList.get(0).getId());
//
//    Assert.assertEquals(PlansRemindStatus.reminded,shopPlan.getStatus());
//
//
//    //判断 job是否正确 是否是一个job
//    List<SmsJob> smsJobList = notificationWriter.getSmsJobsByShopId(shopId, 0, 1000);
//    Assert.assertEquals(1, smsJobList.size());
//
////    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
////    Assert.assertEquals(100d, shopBalanceDTO.getSmsBalance(), 0);
////    smsSendSchedule.processSmsJobs();
////    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
////    Assert.assertEquals(40.6, shopBalanceDTO.getSmsBalance(), 0.001);
////
////    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
////    Assert.assertEquals((int) Math.ceil(198.0 * 12 / 450.0), outBoxs.size());
//  }
//
//  @Test
//  public void testSmsSendNormalShopCustomersSuccess() throws Exception {
//    ModelMap model = new ModelMap();
//    Long shopId = createShop();
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
//    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    //使用 swe
//    SmsYiMeiSenderMock.setCode(0);
//    //保存 客户
//    UserWriter userWriter = userDaoManager.getWriter();
//    for (int i = 0; i < 198; i++) {
//      Object userStatus = userWriter.begin();
//      try {
//        Customer customer = new Customer();
//        customer.setShopId(shopId);
//        customer.setMobile("15851654173");
//        userWriter.save(customer);
//        userWriter.commit(userStatus);
//      } finally {
//        userWriter.rollback(userStatus);
//      }
//    }
//    //判断customer是否存储正确
//    List<String> customerPhoneList = customerService.getCustomersPhonesByShopId(shopId);
//    Assert.assertEquals(198, customerPhoneList.size());
//
//    //保存一条计划 顾客为all
//    ShopPlan shopPlan = new ShopPlan();
//    shopPlan.setShopId(shopId);
//    shopPlan.setCustomerType(UserConstant.CustomerType.NORM_CUSTOMER);
//    shopPlan.setContent(smsContent);
//    shopPlan.setContact("18915410141");
//    shopPlan.setStatus(PlansRemindStatus.activity);
//    shopPlan.setRemindTime(System.currentTimeMillis());
//    Object statusSP = userWriter.begin();
//    try {
//      userWriter.save(shopPlan);
//      userWriter.commit(statusSP);
//    } finally {
//      userWriter.rollback(statusSP);
//    }// 判断计划是否 保存正确
//    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
//    List<ShopPlanDTO> shopPlanDTOList = shopPlanService.getPlans(shopId, 0,100, DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()), null);
//    Assert.assertEquals(1, shopPlanDTOList.size());
//    String customerPhones = "";
//    for (String customerPhone : customerPhoneList) {
//      customerPhones += customerPhone;
//    }
//
//    request.getSession().setAttribute("shopId", shopId);
//    request.setParameter("customerIds", customerPhones);
//    request.setParameter("content", smsContent);
//    request.setParameter("customerType", "all");
//
//    request.setParameter("idStr", shopPlan.getId().toString());
//
//
//    request.setParameter("phoneNumbers", shopPlan.getContact());
//    request.setParameter("smsContent", shopPlan.getContent());
//    request.setParameter("shopPlanId", shopPlanDTOList.get(0).getId().toString());
//
//    smsController.sendSms(request,shopPlan.getContact(),shopPlan.getContent());
//
//    shopPlan = shopPlanService.getPlan(shopId,shopPlanDTOList.get(0).getId());
//
//    Assert.assertEquals(PlansRemindStatus.reminded,shopPlan.getStatus());
//
//    //判断 job是否正确 是否是一个job
//    List<SmsJob> smsJobList = notificationWriter.getSmsJobsByShopId(shopId, 0, 1000);
//    Assert.assertEquals(1, smsJobList.size());
////
////    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
////    Assert.assertEquals(100d, shopBalanceDTO.getSmsBalance(), 0);
////    smsSendSchedule.processSmsJobs();
////    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
////    Assert.assertEquals(40.6, shopBalanceDTO.getSmsBalance(), 0.001);
////
////    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
////    Assert.assertEquals((int) Math.ceil(198.0 * 12 / 450.0), outBoxs.size());
//  }
//
//  @Test
//  public void testSmsSendAllShopCustomersFail() throws Exception {
//    ModelMap model = new ModelMap();
//    Long shopId = createShop();
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
//    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    SmsYiMeiSenderMock.setCode(-1);
//    //保存 客户
//    UserWriter userWriter = userDaoManager.getWriter();
//    int fail = 50;
//    for (int i = 0; i < 198; i++) {
//      Object userStatus = userWriter.begin();
//      try {
//        Customer customer = new Customer();
//        customer.setShopId(shopId);
//        if (i == fail) {
//          customer.setMobile("false");//这个时候发送失败
//        } else {
//          customer.setMobile("15851654173");
//        }
//        userWriter.save(customer);
//        userWriter.commit(userStatus);
//      } finally {
//        userWriter.rollback(userStatus);
//      }
//    }
//    //判断customer是否存储正确
//    List<String> customerPhoneList = customerService.getCustomersPhonesByShopId(shopId);
//    Assert.assertEquals(198, customerPhoneList.size());
//
//    //保存一条计划 顾客为all
//    ShopPlan shopPlan = new ShopPlan();
//    shopPlan.setShopId(shopId);
//    shopPlan.setCustomerType(UserConstant.CustomerType.NORM_CUSTOMER);
//    shopPlan.setContent(smsContent);
//    shopPlan.setContact("18915410141");
//    shopPlan.setStatus(PlansRemindStatus.activity);
//    shopPlan.setRemindTime(System.currentTimeMillis());
//    Object statusSP = userWriter.begin();
//    try {
//      userWriter.save(shopPlan);
//      userWriter.commit(statusSP);
//    } finally {
//      userWriter.rollback(statusSP);
//    }// 判断计划是否 保存正确
//    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
//    List<ShopPlanDTO> shopPlanDTOList = shopPlanService.getPlans(shopId, 0,100,DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()), null);
//    Assert.assertEquals(1, shopPlanDTOList.size());
//    String customerPhones = "";
//    for (String customerPhone : customerPhoneList) {
//      customerPhones += customerPhone;
//    }
//
//    request.getSession().setAttribute("shopId", shopId);
//    request.setParameter("customerIds", customerPhones);
//    request.setParameter("content", smsContent);
//    request.setParameter("customerType", "all");
//    request.setParameter("idStr", shopPlan.getId().toString());
//
//    request.setParameter("phoneNumbers", shopPlan.getContact());
//    request.setParameter("smsContent", shopPlan.getContent());
//    request.setParameter("shopPlanId", shopPlanDTOList.get(0).getId().toString());
//
//    smsController.sendSms(request,shopPlan.getContact(),shopPlan.getContent());
//
//    shopPlan = shopPlanService.getPlan(shopId,shopPlanDTOList.get(0).getId());
//
//    Assert.assertEquals(PlansRemindStatus.reminded,shopPlan.getStatus());
////    remindController.smsSendPlan(request, response);
////    shopPlan = userWriter.getById(ShopPlan.class, shopPlan.getId());
////    //判断 shopPlan状态改变成reminded
////    Assert.assertEquals(UserConstant.Status.REMINDED, shopPlan.getStatus());
////    //判断 job是否正确 是否是一个job
//    List<SmsJob> smsJobList = notificationWriter.getSmsJobsByShopId(shopId, 0, 1000);
//    Assert.assertEquals(1, smsJobList.size());
////
////    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
////    Assert.assertEquals(100d, shopBalanceDTO.getSmsBalance(), 0);
////    smsSendSchedule.processSmsJobs();
////    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
////    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
////    List<SmsJob> smsJobs = notificationWriter.getSmsJobsByShopId(shopId, 0, 1000);
////    //中间 有三十条短信没有发出去
////    Assert.assertEquals(5, outBoxs.size());
////    Assert.assertEquals(353, smsJobs.get(0).getReceiveMobile().length());
////    Assert.assertEquals(100 - (198 * 0.3 - 30 * 0.3), shopBalanceDTO.getSmsBalance(), 0.001);
//  }
//
//  @Test
//  public void testUpdatePlan() throws Exception
//  {
//    Long shopId = createShop();
//    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
//    ShopPlanDTO shopPlanDTO = new ShopPlanDTO();
//
//    shopPlanDTO.setShopId(shopId);
//    shopPlanDTO.setContent("测试测试");
//    shopPlanDTO.setContact("18915410141");
//    shopPlanDTO.setCustomerNames("陈方雷");
//    shopPlanDTO.setUserInfo("[\"userId\":\"1\",\"name\":\"陈方雷\",\"mobile\":\"18915410141\"]");
//
//    shopPlanService.savePlan(shopPlanDTO);
//
//    ShopPlan shopPlan = shopPlanService.getPlan(shopId,shopPlanDTO.getId());
//
//    Assert.assertEquals(shopPlan.getContent(),shopPlanDTO.getContent());
//    Assert.assertEquals(shopPlan.getContact(),shopPlanDTO.getContact());
//    Assert.assertEquals(shopPlan.getCustomerNames(),shopPlanDTO.getCustomerNames());
//    Assert.assertEquals(shopPlan.getUserInfo(),shopPlanDTO.getUserInfo());
//
//    shopPlanDTO.setContent("abab");
//    shopPlanDTO.setContact("11111111111");
//    shopPlanDTO.setCustomerNames("lalal");
//    shopPlanDTO.setUserInfo("[\"userId\":\"1\",\"name\":\"lalal\",\"mobile\":\"11111111111\"]");
//
//    Map<String,String> map= new HashMap<String, String>();
//
//    map.put("content",shopPlanDTO.getContent());
//    map.put("contact",shopPlanDTO.getContact());
//    map.put("customerNames",shopPlanDTO.getCustomerNames());
//    map.put("userInfo",shopPlanDTO.getUserInfo());
//    map.put("id",shopPlanDTO.getId().toString());
//
//    String jsonStr = JsonUtil.mapToJson(map);
//
//    remindController.updatePlan(request,jsonStr,response);
//
//    shopPlan = shopPlanService.getPlan(shopId,shopPlanDTO.getId());
//
//    Assert.assertEquals(shopPlan.getContent(),shopPlanDTO.getContent());
//    Assert.assertEquals(shopPlan.getContact(),shopPlanDTO.getContact());
//    Assert.assertEquals(shopPlan.getCustomerNames(),shopPlanDTO.getCustomerNames());
//    Assert.assertEquals(shopPlan.getUserInfo(),shopPlanDTO.getUserInfo());
//
//  }
//
//}
