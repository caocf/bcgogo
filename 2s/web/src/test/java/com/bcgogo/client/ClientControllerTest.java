package com.bcgogo.client;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.security.BCClientLoginHandler;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * User: ZhangJuntao
 * Date: 13-6-26
 * Time: 下午4:24
 */
public class ClientControllerTest extends AbstractTest {
  private static final Logger LOG = LoggerFactory.getLogger(ClientControllerTest.class);
  private ClientController clientController;

  @Before
  public void setUp() throws Exception {
    clientController = new ClientController();
    clientController.setLoginHandler(new BCClientLoginHandler());
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @After
  public void tearDown() throws Exception {
    request = null;
    response = null;
  }

  @Test
  public void checkUpdateTest() throws Exception {
    ServiceManager.getService(ConfigService.class).setConfig("ClientCurrentVersion", "2.0", ShopConstant.BC_SHOP_ID);
    ClientVersionCheckResult result = clientController.checkUpdate(request, null, 1l, "test", "2.0", null);
    Assert.assertFalse(result.getNeedUpdate());
    result = clientController.checkUpdate(request, null, 1l, "test", "1.0", null);
    Assert.assertTrue(result.getNeedUpdate());
  }

  @Test
  public void checkFirefoxUpdateTest() throws Exception {
    ServiceManager.getService(ConfigService.class).setConfig("FirefoxCurrentVersion", "21.0", ShopConstant.BC_SHOP_ID);
    FirefoxVersionCheckResult result = clientController.checkFirefoxUpdate(request, null, 1l, "test", "21.0", null);
    Assert.assertFalse(result.getNeedUpdate());
    result = clientController.checkFirefoxUpdate(request, null, 1l, "test", "20.0", null);
    Assert.assertTrue(result.getNeedUpdate());
  }

  @Test
  public void loginTest() throws Exception {
//    String MAC = "DC-0E-A1-82-AB-17";
//    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
//    UserWriter writer = userDaoManager.getWriter();
//    UserDTO userDTO = this.initUser();
//    Long shopId = userDTO.getShopId();
//    Assert.assertFalse(writer.isClientBinding(shopId, userDTO.getUserNo(), MAC));
//    ClientLoginResult result = clientController.login(request, "1", userDTO.getUserNo(), null, MAC);
//    Assert.assertFalse(result.isSuccess());
//    result = clientController.login(request, "123", userDTO.getUserNo(), null, MAC);
//    Assert.assertTrue(result.isSuccess());
//    Assert.assertTrue(writer.isClientBinding(shopId, userDTO.getUserNo(), MAC));

  }


}
