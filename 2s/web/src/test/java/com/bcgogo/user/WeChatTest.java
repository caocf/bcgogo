package com.bcgogo.user;

import com.bcgogo.AbstractTest;
import com.bcgogo.common.Result;
import com.bcgogo.customer.CustomerController;
import com.bcgogo.txn.TxnController;
import com.bcgogo.wx.WeChatController;
import com.bcgogo.wx.user.WXUserVehicleDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2014-12-25
 * Time: 14:38
 */
public class WeChatTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    this.request = new MockHttpServletRequest();
    this.weChatController = new WeChatController();
    this.modelMap = new ModelMap();
    super.initWeChatInfo();
  }


  @Test
  public void testBindVehicle() throws Exception {
    WXUserVehicleDTO userVehicleDTO = new WXUserVehicleDTO();
    userVehicleDTO.setOpenId("oCFjjt069Ms1D-vzGeyojcFcwQK8");
    userVehicleDTO.setVehicleNo("苏E552UQ");
    userVehicleDTO.setVin("LSGPC54R8CF064000");
    userVehicleDTO.setEngineNo("120660580");
    userVehicleDTO.setProvince(1016L);
    userVehicleDTO.setCity(1016016L);
    Result result = weChatController.bindVehicle(request, userVehicleDTO);
    Assert.assertNotNull(result);
    Assert.assertTrue("bind vehicle success?",result.isSuccess());

  }

}
