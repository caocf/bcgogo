//package com.bcgogo.txn.service;
//
//import com.bcgogo.AbstractTest;
//import com.bcgogo.api.AppUserDTO;
//import com.bcgogo.service.ServiceManager;
//import com.bcgogo.txn.service.pushMessage.ICustomPushMessageService;
//import com.bcgogo.user.dto.CustomerDTO;
//import com.bcgogo.user.model.Customer;
//import com.bcgogo.user.service.ICustomerService;
//import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.MockitoAnnotations;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.*;
//
//import java.util.*;
//
///**
// * 单元测试
// * Created by Hans on 14-1-15.
// */
//public class CustomPushMessageServiceTest  extends AbstractTest {
//  @Mock
//  private ICustomerService customerService;
//  @Mock
//  private ICustomPushMessageService customPushMessageService;
//  @Mock
//  private IAppUserCustomerMatchService appUserCustomerMatchService;
//
//  @Before
//  public void setUp() {
//    MockitoAnnotations.initMocks(this);
//  }
//
//  @Test
//  public void testCreateCustomPushMessage2App() throws Exception {
//    List<Long> customerIds = new ArrayList<Long>();
//    List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
//    CustomerDTO customer1 = new CustomerDTO();
//    customer1.setShopId(100000l);
//    CustomerDTO customer2 = new CustomerDTO();
//    customer2.setShopId(100000l);
//    customerIds.add(1l);
//    customerIds.add(2l);
//    customerDTOList.add(customer1);
//    customerDTOList.add(customer2);
//    Map<Long, AppUserDTO> appUserDTOMap = new HashMap<Long, AppUserDTO>();
//    AppUserDTO appUserDTO1= new AppUserDTO();
//    appUserDTO1.setName("app1");
//    AppUserDTO appUserDTO2= new AppUserDTO();
//    appUserDTO2.setName("app2");
//    appUserDTOMap.put(1l,appUserDTO1);
//    appUserDTOMap.put(2l,appUserDTO2);
//    when(customerService.getCustomerByIds(customerIds)).thenReturn(customerDTOList);
//    when(appUserCustomerMatchService.getAppUserMapByCustomerIds(new HashSet<Long>(customerIds))).thenReturn(appUserDTOMap);
//    ServiceManager.getService(ICustomPushMessageService.class).createCustomPushMessage2App(new HashSet<Long>(customerIds), "test");
//  }
//}
