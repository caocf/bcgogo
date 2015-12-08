package com.bcgogo;

import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ShopBalance;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.service.ShopBalanceService;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.NotificationServiceFactory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.UserServiceFactory;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;
import com.bcgogo.user.dto.permission.UserGroupUserDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.*;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IResourceService;
import com.bcgogo.user.service.permission.IRoleService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import java.util.HashMap;
import java.util.Map;

public class AbstractTest {
  protected IUserService userService;
  protected UserDaoManager userDaoManager;
  protected IConfigService configService;
  protected IUserCacheService userCacheService;
  protected IUserGroupService userGroupService;
  protected IRoleService roleService;
  protected IResourceService resourceService;

  @BeforeClass
  public static void init() throws Exception {
    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("unit.test", "true");
    jpaProperties.put("bcgogo.dataSource.url",
        "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
            ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");

    SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    new ConfigServiceFactory(jpaProperties);
    new UserServiceFactory(jpaProperties);
    new NotificationServiceFactory(jpaProperties);
    ServiceManager.getService(ConfigService.class).setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);
    ServiceManager.getService(ConfigService.class).setConfig("CustomerRegister", "http://regc.bcgogo.com", ShopConstant.BC_SHOP_ID);
    ServiceManager.getService(ConfigService.class).setConfig("SupplierRegister", "http://regs.bcgogo.com", ShopConstant.BC_SHOP_ID);
    ServiceManager.getService(ConfigService.class).setConfig("SystemSupplierRegister", "http://rs.bcgogo.com", ShopConstant.BC_SHOP_ID);
    ServiceManager.getService(ConfigService.class).setConfig("SystemCustomerRegister", "http://rc.bcgogo.com", ShopConstant.BC_SHOP_ID);
  }

  public Long createShop() throws Exception {
    userService = ServiceManager.getService(IUserService.class);
    configService = ServiceManager.getService(IConfigService.class);
    userDaoManager = ServiceManager.getService(UserDaoManager.class);
    userCacheService = ServiceManager.getService(IUserCacheService.class);
    userGroupService = ServiceManager.getService(IUserGroupService.class);
    roleService = ServiceManager.getService(IRoleService.class);
    resourceService = ServiceManager.getService(IResourceService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setAccount("test");
    shopDTO.setName("test");
    shopService.createShop(shopDTO);
    ShopBalance shopBalance = new ShopBalance();
    shopBalance.setSmsBalance(100d);
    shopBalance.setShopId(shopDTO.getId());
    shopBalance.setRechargeTotal(100d);
    ShopBalanceService shopBalanceService = ServiceManager.getService(ShopBalanceService.class) ;
    shopBalanceService.createSmsBalance(shopBalance.toDTO());
    return shopDTO.getId();
  }

  public Resource createResource(String name, String value) {
    Resource resource = new Resource();
    resource.setName(name);
    resource.setValue(value);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(resource);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return resource;
  }

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

  public Role createRole(String name) {
    Role role = new Role();
    role.setName(name);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(role);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return role;
  }

  public RoleResource createRoleResource( Long resourceId,Long roleId) {
    RoleResource roleResource = new RoleResource();
    roleResource.setRoleId(roleId);
    roleResource.setResourceId(resourceId);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(roleResource);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return roleResource;
  }


  public UserGroupDTO createUserGroup() throws BcgogoException {
    Long temp = System.currentTimeMillis();
    UserGroupDTO userGroupDTO = new UserGroupDTO();
    userGroupDTO.setName("Group" + temp);
    userGroupDTO.setShopId(-1L);
    userGroupDTO = userGroupService.setUserGroup(userGroupDTO);
    return userGroupDTO;
  }

  public UserDTO createUser(Long shopId, Long userGroupId) throws BcgogoException {
    Long temp = System.currentTimeMillis();
    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("123");
    userDTO.setUserName("UName" + temp);
    userDTO.setName("Name" + temp);
    userDTO.setEmail(temp + "@qq.com");
    userDTO.setMobile("1" + temp.toString().substring(2, 12));
    userDTO.setShopId(shopId);
    userDTO.setUserGroupId(userGroupId);
    userDTO = userCacheService.setUser(userDTO);
    createUserGroupUser(userDTO.getId(), userGroupId);
    return userDTO;
  }

  public UserGroupUserDTO createUserGroupUser(Long userId, Long userGroupId) throws BcgogoException {
    UserGroupUser userGroupUser = new UserGroupUser();
    userGroupUser.setUserGroupId(userGroupId);
    userGroupUser.setUserId(userId);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(userGroupUser);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return userGroupUser.toDTO();
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


  public CustomerRecordDTO createCustomerRecordDTO(Long shopId) {
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO.setShopId(shopId);
    customerRecordDTO.setCustomerId(123456L);
    customerRecordDTO.setName("苏州统购");
    customerRecordDTO.setMobile("15151771582");
    customerRecordDTO.setContact("邵磊");
    return customerRecordDTO;
  }


  public CustomerDTO createCustomerDTO(Long shopId) throws Exception {
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setShopId(shopId);
    customerDTO.setId(123456L);
    customerDTO.setName("苏州统购");
    customerDTO.setMobile("15151771582");
    customerDTO.setContact("邵磊");
    customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, "2012-01-01"));
    return customerDTO;
  }

  public CustomerDTO createCustomer(Long shopId, String name, RelationTypes relationTypes) throws Exception {
    CustomerDTO customerDTO = new CustomerDTO();
    String mobile  ="151"+ RandomStringUtils.randomNumeric(8);
    customerDTO.setShopId(shopId);
    customerDTO.setName(name);
    customerDTO.setMobile(mobile);
    customerDTO.setRelationType(relationTypes);
    customerDTO.setContact(name);
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setMobile(mobile);
    contactDTO.setName(name);
    contactDTO.setDisabled(1);
    contactDTO.setIsMainContact(1);
    ContactDTO[] contacts = new ContactDTO[]{contactDTO};
    customerDTO.setContacts(contacts);

    ServiceManager.getService(IUserService.class).createCustomer(customerDTO);
    return customerDTO;
  }

  public VehicleDTO createVehicleDTO(Long shopId) throws Exception {
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setShopId(shopId);
    vehicleDTO.setLicenceNo("苏E11000");
    vehicleDTO.setBrand("奔驰");
    vehicleDTO.setModel("S600");
    vehicleDTO.setYear("2012");
    vehicleDTO.setEngine("6.0L");
    vehicleDTO.setLicenceNoRevert("00011E苏");
    return vehicleDTO;
  }


  public CustomerCardDTO createCustomerCardDTO(Long shopId) {
    CustomerCardDTO customerCardDTO = new CustomerCardDTO();
    customerCardDTO.setShopId(shopId);
    customerCardDTO.setCardType(0L);
    customerCardDTO.setCustomerId(123456L);
    customerCardDTO.setWashRemain(1L);
    return customerCardDTO;
  }

  @AfterClass
  public static void terminate() {
    System.out.println("单元测试结束");
  }


}
