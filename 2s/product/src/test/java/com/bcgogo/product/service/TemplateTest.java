package com.bcgogo.product.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.TemplateDTO;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;


public class TemplateTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testBrandTest() throws Exception {


    TemplateDTO templateDTO = new TemplateDTO();
    templateDTO.setMemo("备注");
    templateDTO.setKindId(11L);
    templateDTO.setName("名称");
    templateDTO.setTemplate("XML");
    templateDTO.setShopId(22L);
    templateDTO.setState(33L);
    templateDTO.setType(44);
    templateDTO.setVer(new BigDecimal("55.50"));

    IProductService productService = ServiceManager.getService(IProductService.class);
    TemplateDTO fDTO = productService.createTemplate(templateDTO);

    TemplateDTO otherBrandDTO = productService.getTemplate(fDTO.getId());

    assertEquals("11", otherBrandDTO.getKindId() + "");
    assertEquals("名称", otherBrandDTO.getName());
    assertEquals("XML", otherBrandDTO.getTemplate());
    assertEquals("22", otherBrandDTO.getShopId() + "");
    assertEquals("33", otherBrandDTO.getState() + "");
    assertEquals("44", otherBrandDTO.getType() + "");
    assertEquals("55.50", otherBrandDTO.getVer() + "");


  }

}
