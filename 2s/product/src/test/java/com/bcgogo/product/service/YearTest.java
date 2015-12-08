package com.bcgogo.product.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.YearDTO;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class YearTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testBrandTest() throws Exception {


    YearDTO yearDTO = new YearDTO();
    yearDTO.setBrandId(11L);
    yearDTO.setMemo("备注");
    yearDTO.setMfrId(22L);
    yearDTO.setModelId(33L);
    yearDTO.setShopId(55L);
    yearDTO.setState(66L);
    yearDTO.setYear(77);

    IProductService productService = ServiceManager.getService(IProductService.class);
    YearDTO fDTO = productService.createYear(yearDTO);

    YearDTO otherBrandDTO = productService.getYear(fDTO.getId());

    assertEquals("11", otherBrandDTO.getBrandId() + "");
    assertEquals("备注", otherBrandDTO.getMemo());
    assertEquals("22", otherBrandDTO.getMfrId() + "");
    assertEquals("33", otherBrandDTO.getModelId() + "");

    assertEquals("55", otherBrandDTO.getShopId() + "");
    assertEquals("66", otherBrandDTO.getState() + "");
    assertEquals("77", otherBrandDTO.getYear() + "");


  }

}
