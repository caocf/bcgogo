package com.bcgogo.product.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.BrandDTO;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class BrandTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testBrandTest() throws Exception {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date d = fmt.parse("2012-01-16 15:23");
    Date d1 = fmt.parse("2012-01-16 00:00");
    Date d2 = fmt.parse("2012-01-16 23:59");

    long io = d.getTime();
    long is = d1.getTime();
    long it = d2.getTime();


    BrandDTO brandDTO = new BrandDTO();
    brandDTO.setFirstLetter("F");
    brandDTO.setMemo("备注");
    brandDTO.setName("依维柯");
    brandDTO.setNameEn("YWK");
    brandDTO.setState(3l);
    brandDTO.setShopId(3434L);

    IProductService productService = ServiceManager.getService(IProductService.class);
    BrandDTO bDTO = productService.createBrand(brandDTO);

    BrandDTO otherBrandDTO = productService.getBrand(bDTO.getId());

//     assertEquals("1",bDTO.getId());
    assertEquals("F", otherBrandDTO.getFirstLetter());
    assertEquals("备注", otherBrandDTO.getMemo());
    assertEquals("依维柯", otherBrandDTO.getName());
    assertEquals("YWK", otherBrandDTO.getNameEn());
    assertEquals("3", otherBrandDTO.getState() + "");
    assertEquals("3434", otherBrandDTO.getShopId() + "");

  }

}
