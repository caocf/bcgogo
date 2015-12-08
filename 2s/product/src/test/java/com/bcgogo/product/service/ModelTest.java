package com.bcgogo.product.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.ModelDTO;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ModelTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testBrandTest() throws Exception {


    ModelDTO modelDTO = new ModelDTO();
    modelDTO.setBrandId(33L);
    modelDTO.setFirstLetter("T");
    modelDTO.setMemo("备注");
    modelDTO.setMfrId(44L);
    modelDTO.setName("名称");
    modelDTO.setNameEn("name");
    modelDTO.setShopId(66L);
    modelDTO.setState(77L);

    IProductService productService = ServiceManager.getService(IProductService.class);
    ModelDTO fDTO = productService.createModel(modelDTO);

    ModelDTO otherBrandDTO = productService.getModel(fDTO.getId());

    assertEquals("33", otherBrandDTO.getBrandId() + "");
    assertEquals("T", otherBrandDTO.getFirstLetter());
    assertEquals("备注", otherBrandDTO.getMemo());
    assertEquals("44", otherBrandDTO.getMfrId() + "");
    assertEquals("名称", otherBrandDTO.getName());
    assertEquals("name", otherBrandDTO.getNameEn());
    assertEquals("66", otherBrandDTO.getShopId() + "");
    assertEquals("77", otherBrandDTO.getState() + "");

  }

}
