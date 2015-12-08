package com.bcgogo.user;

import com.bcgogo.AbstractTest;
import com.bcgogo.customer.UnitLinkController;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-20
 * Time: 下午1:16
 * To change this template use File | Settings | File Templates.
 */
public class UnitLinkControllerTest  extends AbstractTest {

    @Before
  public void setUp() throws Exception {
    unitLinkController = new UnitLinkController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testSupplier() throws Exception{
      ModelMap model = new ModelMap();
      request.setParameter("supplierId","10000010001120012");
  }

    @Test
    public void testSupplierResponse() throws Exception{
       addSuppliers();
       ModelMap model = new ModelMap();
       this.request.getSession().setAttribute("shopId", 77876L);
       this.unitLinkController.supplierResponse(request,response,model,1,5,"","");
       String jsonStr = (String) model.get("jsonStr");
       Type type = new TypeToken<List<SupplierDTO>>(){}.getType();
       jsonStr = jsonStr.replaceAll(":\"\",", ":null,").replaceAll(":\"\"}", ":null}");          //todo 临时代码
       List<SupplierDTO> supplierDTOList = new Gson().fromJson(jsonStr,type);
       Assert.assertEquals(5, supplierDTOList.size());
       SupplierDTO supplierDTO = supplierDTOList.get(0);
       Assert.assertEquals(77876L, supplierDTO.getShopId().longValue());
       Assert.assertEquals(10000010001160016L, supplierDTO.getLastOrderTime().longValue());
       Assert.assertEquals(OrderTypes.PURCHASE, supplierDTO.getLastOrderType());
       Assert.assertEquals("轮胎，机油等", supplierDTO.getLastOrderProducts());
       Assert.assertEquals(20000.00, supplierDTO.getTotalInventoryAmount());
    }

    @Test
    public void testCustomerResponse() throws Exception{
        addCustomerVehicleInfo();
        ModelMap model = new ModelMap();
        this.request.getSession().setAttribute("shopId", 5434L);
        this.unitLinkController.customerResponse(request,response,model,1,0,"","");
        String jsonStr = (String) model.get("jsonStr");
        jsonStr = jsonStr.replaceAll(":\"\",", ":null,").replaceAll(":\"\"}", ":null}");          //todo 临时代码
        Type type = new TypeToken<List<CustomerRecordDTO>>(){}.getType();
        List<CustomerRecordDTO> customerRecordDTOList = new Gson().fromJson(jsonStr, type);
        Assert.assertEquals(5,customerRecordDTOList.size());
        CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(0);
        Assert.assertEquals(5434L, customerRecordDTO.getShopId().longValue());
        Assert.assertEquals("邹建宏11111111", customerRecordDTO.getName());
        Assert.assertEquals("87657678876", customerRecordDTO.getMobile());
        Assert.assertEquals("2929@bcgogo.com", customerRecordDTO.getEmail());
        Assert.assertEquals(234454.00, customerRecordDTO.getTotalAmount());
        Assert.assertEquals(23434.00, customerRecordDTO.getLastAmount());
        Assert.assertEquals(10000010001167016L, customerRecordDTO.getLastDate().longValue());
        Assert.assertEquals(1, customerRecordDTO.getVehicleCount());
    }

}
