package com.bcgogo.product.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseProductServiceTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testSaveVehicle() throws Exception {
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    Long[] ids = baseProductService.saveVehicle(null, null, null, null, "新车品", "新车模", "2022", "新排量");
    assertEquals(4, ids.length);
  }

}
