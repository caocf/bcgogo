package com.bcgogo.product;

import com.bcgogo.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: Rex
 * Date: 12-1-18
 * Time: 下午12:52
 * To change this template use File | Settings | File Templates.
 */
public class ProductControllerTest extends AbstractTest {
  private ProductController pc;
   private MockHttpServletRequest request;
   private MockHttpServletResponse response;

   @Before
   public void setUp() throws Exception {
     pc = new ProductController();
     request = new MockHttpServletRequest();
     response = new MockHttpServletResponse();
   }

  @Test
  public void testSearchlicenseplate() throws Exception
  {
        pc.searchlicenseplate(request,"苏");
//        String abc=pc.
//       Assert.assertEquals("/user/group", url);
  }
}
