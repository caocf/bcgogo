package com.bcgogo.product.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.ProductVehicleDTO;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ProductVehicleTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testBrandTest() throws Exception {


    ProductVehicleDTO productVehicleDTO = new ProductVehicleDTO();
    productVehicleDTO.setBrandId(11L);
    productVehicleDTO.setMfrId(22L);
    productVehicleDTO.setModelId(33L);
    productVehicleDTO.setProductId(44L);
    productVehicleDTO.setShopId(55L);
    productVehicleDTO.setTrimId(66L);
    productVehicleDTO.setYearId(77L);

    IProductService productService = ServiceManager.getService(IProductService.class);
    ProductVehicleDTO fDTO = productService.createProductVehicle(productVehicleDTO);

    ProductVehicleDTO otherBrandDTO = productService.getProductVehicle(fDTO.getId());

    assertEquals("11", otherBrandDTO.getBrandId() + "");
    assertEquals("22", otherBrandDTO.getMfrId() + "");
    assertEquals("33", otherBrandDTO.getModelId() + "");
    assertEquals("44", otherBrandDTO.getProductId() + "");
    assertEquals("55", otherBrandDTO.getShopId() + "");
    assertEquals("66", otherBrandDTO.getTrimId() + "");
    assertEquals("77", otherBrandDTO.getYearId() + "");


  }

}
