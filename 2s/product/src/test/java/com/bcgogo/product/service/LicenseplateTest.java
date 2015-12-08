package com.bcgogo.product.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.product.dto.LicenseplateDTO;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Rex
 * Date: 12-1-17
 * Time: 下午6:27
 * To change this template use File | Settings | File Templates.
 */
public class LicenseplateTest extends AbstractTest {
@Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testLicenseplateTest() throws Exception {

    LicenseplateDTO licenseplate = new LicenseplateDTO();
//    licenseplate.setId(new Long(123));
    licenseplate.setAreaName("东京市");
    licenseplate.setCarno("东E");
    licenseplate.setAreaFirstname("D");
    licenseplate.setAreaFirstcarno("东");



    IProductService productService = ServiceManager.getService(IProductService.class);
    LicenseplateDTO licenseplateDTO = productService.caeateLicenseplateDTO(licenseplate);

    LicenseplateDTO otherlicenseplateDTO = productService.getLicenseplate(licenseplateDTO.getId());


    //assertEquals(123, otherlicenseplateDTO.getId() + "");
    assertEquals("东京市", otherlicenseplateDTO.getAreaName());
    assertEquals("东E", otherlicenseplateDTO.getCarno());
    assertEquals("D", otherlicenseplateDTO.getAreaFirstname());
    assertEquals("东", otherlicenseplateDTO.getAreaFirstcarno());

  }
}
